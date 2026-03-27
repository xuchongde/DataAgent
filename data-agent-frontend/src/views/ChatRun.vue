<!--
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
-->

<template>
<BaseLayout>
    <el-container style="height: calc(100vh - 60px); gap: 0">
      <!-- 左侧历史消息栏 -->
      <ChatSessionSidebar 
        :agent="agent" 
        :handleSetCurrentSession="
          async (session: ChatSession | null) => {
            currentSession = session; 
            await selectSession(session); 
          }
        " 
        :handleGetCurrentSession="
          () => { 
            return currentSession; 
            }
          " 
        :handleSelectSession="selectSession" 
        :handleDeleteSessionState="deleteSessionState" 
      />
      
      <!-- 右侧对话栏 -->
      <el-main style="background-color: white; display: flex; flex-direction: column">
        <!-- 消息显示区域 -->
        <div class="chat-container" ref="chatContainer">
          <div v-if="!currentSession" class="empty-state">
            <el-empty description="请选择一个会话或创建新会话开始对话" />
            <PresetQuestions 
              v-if="agent.id" 
              :agentId="agent.id" 
              :onQuestionClick="handlePresetQuestionClick"
              class="empty-state-preset"
              />
          </div>
          <div v-else class="messages-area">
            <div
              v-for="message in currentMessages"
              :key="message.id"
              :class="message.messageType === 'text' ? ['message-container', message.role] : ''"
              >
              <!-- HTML类型消息直接渲染 -->
              <div v-if="message.messageType === 'html'" v-html="message.content"></div>
              
              <!-- 文本类型消息使用原有布局 -->
              <div v-else :class="['message', message.role]">
                <div class="message-avatar">
                  <el-avatar :size="32">
                    {{ message.role === 'user' ? '我' : 'AI' }}
                  </el-avatar>
                </div>
                <div class="message-content">
                  <div class="message-text" v-html="formatMessageContent(message)"></div>
                </div>
              </div>
            </div>
            
            <!-- 流式响应显示区域 -->
            <div v-if="isStreaming" class="streaming-response">
              <div class="streaming-header">
                <el-icon class="loading-icon"><Loading /></el-icon>
                <span>智能体正在处理中...</span>
              </div>
              <div class="agent-response-container">
                <template v-for="(nodeBlock, index) in nodeBlocks" :key="index">
                  <!-- 其他节点使用原来的 HTML 渲染方式 -->
                  <div v-html="generateNodeHtml(nodeBlock)"></div>
                </template>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 输入区域 -->
        <div class="input-area" v-if="currentSession">
          <div class="input-container">
            <el-input v-model="userInput" type="textarea" :rows="3" placeholder="请输入您的问题..." :disabled="isStreaming" @keydown.enter.exact.prevent="sendMessage" />
            <el-button v-if="!isStreaming" type="primary" @click="sendMessage" circle class="send-button">
              <el-icon><Promotion /></el-icon>
            </el-button>
            <el-button v-else type="danger" @click="stopStreaming" circle class="send-button stop-button-inline">
              <el-icon><CircleClose /></el-icon>
            </el-button>
          </div>
        </div>
      </el-main>
    </el-container>
  </BaseLayout>
</template>

<script lang="ts">
import { ref, defineComponent, onMounted, nextTick, computed } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Loading, Promotion, CircleClose, } from '@element-plus/icons-vue';
import hljs from 'highlight.js';
import { marked } from 'marked';
import DOMPurify from 'dompurify';
import 'highlight.js/styles/github.css';
import BaseLayout from '@/layouts/BaseLayout.vue';
import AgentService from '@/services/agent';
import ChatService, { type ChatSession, type ChatMessage } from '@/services/chat';
import GraphService, { type GraphRequest, type GraphNodeResponse, TextType, } from '@/services/graph';
import { type Agent } from '@/services/agent';
import HumanFeedback from '@/components/run/HumanFeedback.vue';
import ChatSessionSidebar from '@/components/run/ChatSessionSidebar.vue';
import PresetQuestions from '@/components/run/PresetQuestions.vue';
import MarkdownAgentContainer from '@/components/run/markdown';

// 扩展Window接口以包含自定义方法
declare global {
  interface Window {
    copyTextToClipboard: (btn: HTMLElement) => void;
    handleResultSetPagination: (btn: HTMLElement, direction: 'prev' | 'next') => void;
  }
}

export default defineComponent({
  name: 'AgentRun',
  components: {
    BaseLayout,
    Loading,
    Promotion,
    CircleClose,
    HumanFeedback,
    ChatSessionSidebar,
    PresetQuestions,
    MarkdownAgentContainer,
  },
  created() {
    window.copyTextToClipboard = btn => {
      const text = btn.previousElementSibling.textContent;
      const originalText = btn.textContent;
      navigator.clipboard
        .writeText(text)
        .then(() => {
          btn.textContent = '已复制!';
          setTimeout(() => {
            btn.textContent = originalText;
          }, 3000);
        })
        .catch(() => {
          btn.textContent = '复制失败';
          setTimeout(() => {
            btn.textContent = originalText;
          }, 3000);
        });
    };
  },
  setup() {
    const route = useRoute();
    const agent = ref<Agent>({} as Agent);
    const currentSession = ref<ChatSession | null>(null);
    const currentMessages = ref<ChatMessage[]>([]);
    const userInput = ref('');
    const { getSessionState, syncStateToView, saveViewToState, deleteSessionState } = useSessionStateManager();
    const isStreaming = ref(false);
    const nodeBlocks = ref<GraphNodeResponse[][]>([]);
    
    const autoScroll = ref(true);
    const chatContainer = ref<HTMLElement | null>(null);

    const agentId = computed(() => route.params.id as string);

    const loadAgent = async () => {
      try {
        const agentData = await AgentService.get(parseInt(agentId.value));
        if (agentData) {
          agent.value = agentData;
        } else {
          throw new Error('Agent 不存在');
        }
      } catch (error) {
        ElMessage.error('加载Agent失败');
        console.error('加载Agent失败:', error);
      }
    };

    const selectSession = async (session: ChatSession | null) => {
      // 将源会话状态保存，然后切换到目标会话
      if (currentSession.value) {
        saveViewToState(currentSession.value.id, { isStreaming, nodeBlocks });
      }
      currentSession.value = session;
      try {
        if (session === null) {
          currentMessages.value = [];
          nodeBlocks.value = [];
          isStreaming.value = false;
          return;
        }
        syncStateToView(session.id, { isStreaming, nodeBlocks });
        currentMessages.value = await ChatService.getSessionMessages(session.id);
        scrollToBottom();
      } catch (error) {
        ElMessage.error('加载消息失败');
        console.error('加载消息失败:', error);
      }
    };

    const sendMessage = async () => {
      console.log(userInput.value);
      if (!userInput.value.trim()) {
        ElMessage.warning('请输入请求消息！');
        return;
      }
      if (!currentSession.value || isStreaming.value) {
        ElMessage.warning('智能体正在处理中，请稍后...');
        return;
      }
      const needsTitle = !currentSession.value?.title || currentSession.value.title === '新会话';
      const userMessage: ChatMessage = {
        sessionId: currentSession.value.id,
        role: 'user',
        content: userInput.value,
        messageType: 'text',
        titleNeeded: needsTitle,
      };
      
      try {
        // 保存用户消息
        const savedMessage = await ChatService.saveMessage(currentSession.value.id, userMessage);
        currentMessages.value.push(savedMessage);
        
        const sessionState = getSessionState(currentSession.value.id);
        const request: GraphRequest = {
          agentId: agentId.value,
          query: userInput.value,
          humanFeedback: false,
          nl2sqlOnly: false,
          rejectedPlan: false,
          humanFeedbackContent: null,
          threadId: sessionState.lastRequest?.threadId || null,
          sessionId: currentSession.value.id,
        };
        
        userInput.value = '';
        await sendGraphRequest(request, true);
      } catch (error) {
        ElMessage.error('未知错误');
        console.error(error);
      }
    };

    const sendGraphRequest = async (request: GraphRequest, rejectedPlan: boolean) => {
      const sessionId = currentSession.value!.id;
      const sessionTitle = currentSession.value!.title;
      const sessionState = getSessionState(sessionId);
      
      try {
        lastRequest.value = request;
        isStreaming.value = true;
        nodeBlocks.value = [];
        
        let currentNodeName: string | null = null;
        let currentBlockIndex: number = -1;
        const pendingSavePromises: Promise<void>[] = [];
        
        const saveNodeMessage = (node: GraphNodeResponse[]): Promise<void> => {
          if (!node || !node.length) return Promise.resolve();
          
          const nodeHtml = generateNodeHtml(node);
          const aiMessage: ChatMessage = {
            sessionId,
            role: 'assistant',
            content: nodeHtml,
            messageType: 'html',
          };
          
          return ChatService.saveMessage(sessionId, aiMessage).catch(error => {
            console.error('保存AI消息失败:', error);
          });
        };
        
        // 发送流式请求
        const closeStream = await GraphService.streamSearch(
          request,
          (response: GraphNodeResponse) => {
            if (response.error) {
              ElMessage.error(`处理错误: ${response.text}`);
              return;
            }
            
            if (sessionState.lastRequest) {
              sessionState.lastRequest.threadId = response.threadId;
            }
            
            // 处理其他节点
            const isNewNode: boolean = currentNodeName === null || response.nodeName !== currentNodeName;
            if (isNewNode) {
              // 保存上一个节点的消息（如果有）
              if (currentBlockIndex >= 0 && sessionState.nodeBlocks[currentBlockIndex]) {
                const savePromise = saveNodeMessage(sessionState.nodeBlocks[currentBlockIndex]);
                pendingSavePromises.push(savePromise);
              }
              
              // 创建新的节点块
              const newBlock: GraphNodeResponse = {
                ...response,
                text: response.text,
              };
              sessionState.nodeBlocks.push([newBlock]);
              currentBlockIndex = sessionState.nodeBlocks.length - 1;
              currentNodeName = response.nodeName;
            } else {
              // 继续当前节点的内容
              if (currentBlockIndex >= 0 && sessionState.nodeBlocks[currentBlockIndex]) {
                const newBlock: GraphNodeResponse = {
                  ...response,
                  text: response.text,
                };
                sessionState.nodeBlocks[currentBlockIndex].push(newBlock);
              } else {
                // 创建新的节点块
                const newBlock: GraphNodeResponse = {
                  ...response,
                  text: response.text,
                };
                sessionState.nodeBlocks.push([newBlock]);
                currentBlockIndex = sessionState.nodeBlocks.length - 1;
                currentNodeName = response.nodeName;
              }
            }
            
            // 如果是当前显示的会话，同步到视图并滚动
            if (currentSession.value?.id === sessionId) {
              nodeBlocks.value = sessionState.nodeBlocks;
              if (autoScroll.value) {
                scrollToBottom();
              }
            }
          },
          async (error: Error) => {
            ElMessage.error(`流式请求失败: ${error.message}`);
            console.error('error: ' + error);
            
            // 等待所有待处理的保存操作完成
            if (pendingSavePromises.length > 0) {
              await Promise.all(pendingSavePromises);
            }
            
            sessionState.isStreaming = false;
            sessionState.closeStream = null;
            currentNodeName = null;
            
            // 出错时只有当前会话才重新加载
            if (currentSession.value?.id === sessionId) {
              isStreaming.value = false;
              await selectSession(currentSession.value);
            }
          },
          async () => {
            // 等待所有待处理的保存操作完成
            if (pendingSavePromises.length > 0) {
              await Promise.all(pendingSavePromises);
            }
            
            // 其他节点，可能是错误或人类反馈模式
            // 保存最后一个节点的消息（如果有）
            if (currentBlockIndex >= 0 && sessionState.nodeBlocks[currentBlockIndex]) {
              await saveNodeMessage(sessionState.nodeBlocks[currentBlockIndex]);
            }
            
            // 所有节点处理完成
            sessionState.isStreaming = false;
            
            // 如果是当前显示的会话，同步到视图
            if (currentSession.value?.id === sessionId) {
              isStreaming.value = false;
            }
            
            ElMessage.success(`会话[${sessionTitle}]处理完成`);
            currentNodeName = null;
            closeStream();
            
            // 只有当前会话才重新加载消息
            if (currentSession.value?.id === sessionId) {
              await selectSession(currentSession.value);
            }
          },
        );
        
        // 保存closeStream函数到会话状态
        sessionState.closeStream = closeStream;
      } catch (error) {
        ElMessage.error('发送消息失败');
        console.error('发送消息失败:', error);
        sessionState.isStreaming = false;
        sessionState.closeStream = null;
        if (currentSession.value?.id === sessionId) {
          isStreaming.value = false;
        }
      }
    };

    const formatMessageContent = (message: ChatMessage) => {
      if (message.messageType === 'text') {
        return message.content.replace(/\n/g, '<br>');
      }
      return message.content;
    };

    const generateNodeHtml = (node: GraphNodeResponse[]) => {
      const content = formatNodeContent(node);
      return `<div class="agent-response-block" style="display: block !important; width: 100% !important;">
        <div class="agent-response-title">${node.length > 0 ? node[0].nodeName : '空节点'}</div>
        <div class="agent-response-content">${content}</div>
      </div>`;
    };

    const formatNodeContent = (node: GraphNodeResponse[]) => {
      let content = '';
      for (let idx = 0; idx < node.length; idx++) {
        if (node[idx].textType === TextType.HTML) {
          content += node[idx].text;
        } else if (node[idx].textType === TextType.TEXT) {
          content += node[idx].text.replace(/\n/g, '<br>');
        } else if (
          node[idx].textType === TextType.JSON ||
          node[idx].textType === TextType.PYTHON ||
          node[idx].textType === TextType.SQL
        ) {
          let pre = '';
          let p = idx;
          for (; p < node.length; p++) {
            if (node[p].textType !== node[idx].textType) {
              break;
            }
            pre += node[p].text;
          }
          
          try {
            // 使用 highlight.js 进行代码高亮
            const language = node[idx].textType.toLowerCase();
            const highlighted = hljs.highlight(pre, { language });
            content += `<pre><div style="display: flex; justify-content: space-between; align-items: center; background: #f8f9fa; padding: 8px 12px; border-bottom: none; font-family: system-ui, sans-serif; font-size: 14px;"><span style="color: #666;">${language}</span><span hidden>${pre}</span><button onclick='copyTextToClipboard(this)' style="background: #f8f9fa; border: none; padding: 4px 12px; border-radius: 12px; font-size: 13px; cursor: pointer; transition: background 0.2s;">复制</button></div><code class="hljs ${language}">${highlighted.value}</code></pre>`;
          } catch (error) {
            // 如果高亮失败，返回原始代码
            content += `<pre><code>${pre}</code></pre>`;
          }
          
          if (p < node.length) {
            idx = p - 1;
          } else {
            break;
          }
        } else {
          console.warn(`不支持的 textType: ${node[idx].textType}`);
          content += node[idx].text;
        }
      }
      return content;
    };

    const scrollToBottom = () => {
      nextTick(() => {
        if (chatContainer.value) {
          chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
        }
      });
    };

    const handlePresetQuestionClick = async (question: string) => {
      if (isStreaming.value) {
        ElMessage.warning('智能体正在处理中，请稍后...');
        return;
      }
      
      // 如果没有会话，先创建新会话
      if (!currentSession.value) {
        try {
          const newSession = await ChatService.createSession(parseInt(agentId.value), '新会话');
          currentSession.value = newSession;
          ElMessage.success('新会话创建成功');
        } catch (error) {
          ElMessage.error('创建会话失败');
          return;
        }
      }
      
      userInput.value = question;
      // 自动发送消息
      nextTick(() => {
        sendMessage();
      });
    };

    const stopStreaming = async () => {
      if (!currentSession.value) {
        ElMessage.warning('当前没有活动的会话');
        return;
      }
      
      const sessionId = currentSession.value.id;
      const sessionState = getSessionState(sessionId);
      
      try {
        // 检查是否有活动的流式连接
        if (!sessionState.closeStream) {
          ElMessage.warning('没有正在进行的对话');
          return;
        }
        
        // 关闭 EventSource 连接
        sessionState.closeStream();
        sessionState.closeStream = null;
        
        // 保存已接收的节点消息
        if (sessionState.nodeBlocks && sessionState.nodeBlocks.length > 0) {
          const saveNodeMessage = (node: GraphNodeResponse[]): Promise<void> => {
            if (!node || !node.length) return Promise.resolve();
            
            const nodeHtml = generateNodeHtml(node);
            const aiMessage: ChatMessage = {
              sessionId,
              role: 'assistant',
              content: nodeHtml,
              messageType: 'html',
            };
            
            return ChatService.saveMessage(sessionId, aiMessage).catch(error => {
              console.error('保存AI消息失败:', error);
            });
          };
          
          // 保存所有未保存的节点块
          const savePromises = sessionState.nodeBlocks.map(block => saveNodeMessage(block));
          await Promise.all(savePromises).catch(error => {
            console.error('保存节点消息时出错:', error);
          });
        }
        
        // 清理流式状态
        sessionState.isStreaming = false;
        sessionState.nodeBlocks = [];
        
        // 如果是当前显示的会话，同步更新视图
        if (currentSession.value?.id === sessionId) {
          isStreaming.value = false;
          nodeBlocks.value = [];
        }
        
        // 重新加载会话消息以刷新显示
        await selectSession(currentSession.value);
        
        ElMessage.success('已停止对话');
      } catch (error) {
        console.error('停止对话时出错:', error);
        ElMessage.error('停止对话失败');
        
        // 确保状态清理总是执行
        sessionState.isStreaming = false;
        sessionState.closeStream = null;
        
        if (currentSession.value?.id === sessionId) {
          isStreaming.value = false;
          nodeBlocks.value = [];
        }
      }
    };

    onMounted(async () => {
      await loadAgent();
    });

    return {
      agent,
      currentSession,
      currentMessages,
      userInput,
      isStreaming,
      nodeBlocks,
      agentId,
      formatMessageContent,
      generateNodeHtml,
      formatNodeContent,
      selectSession,
      sendMessage,
      handlePresetQuestionClick,
      stopStreaming,
      deleteSessionState,
      chatContainer,
      autoScroll,
    };
  },
});
</script>

<style scoped>
/* 聊天容器样式 */
.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding: 40px 20px;
}

.empty-state-preset {
  width: 100%;
  max-width: 800px;
}

.messages-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 消息容器样式 */
.message-container {
  display: flex;
  max-width: 100%;
}

.message-container.user {
  justify-content: flex-end;
}

.message-container.assistant {
  justify-content: flex-start;
}

/* 消息样式 */
.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message.assistant {
  align-self: flex-start;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  word-wrap: break-word;
}

.message.user .message-text {
  background: #409eff;
  color: white;
}

.message.assistant .message-text {
  background: white;
  color: #303133;
  border: 1px solid #e8e8e8;
}

/* 流式响应样式 */
.streaming-response {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
}

.streaming-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.loading-icon {
  animation: spin 1s linear infinite;
  color: #409eff;
}

.streaming-header span {
  font-weight: 500;
  color: #409eff;
}

.stop-button-inline {
  width: 48px;
  height: 48px;
}

/* 节点容器样式 */
.agent-response-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.agent-response-block {
  background: #f8f9fa;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.agent-response-block:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.agent-response-title {
  background: #ecf5ff;
  padding: 12px 16px;
  font-weight: 600;
  color: #409eff;
  border-bottom: 1px solid #e8e8e8;
  font-size: 14px;
}

.agent-response-content {
  padding: 16px;
  line-height: 1.6;
  min-height: 40px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 当 agent-response-content 包含 Markdown 组件时，重置样式 */
.agent-response-content .markdown-container {
  line-height: 1.4;
  white-space: normal;
  font-family: inherit;
}

.agent-response-content pre {
  margin: 0;
  background: transparent;
  border: none;
  padding: 0;
}

.agent-response-content code {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  background: transparent;
  padding: 0;
}

/* 代码高亮样式 */
.agent-response-content pre.hljs {
  background: #f6f8fa !important;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  padding: 16px;
  margin: 8px 0;
  overflow-x: auto;
}

.agent-response-content code.hljs {
  background: transparent !important;
  padding: 0;
  font-size: 13px;
  line-height: 1.45;
}

.agent-response-content .hljs {
  display: block;
  overflow-x: auto;
  color: #24292e;
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #e1e4e8;
}

/* 输入区域样式 */
.input-area {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e8e8e8;
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.send-button {
  width: 48px;
  height: 48px;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-aside {
    width: 250px !important;
  }
  .message {
    max-width: 90%;
  }
  .input-container {
    flex-direction: column;
  }
}
</style>