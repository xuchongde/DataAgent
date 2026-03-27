/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.dataagent.splitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 向量化MinerU返回的md文件内容，rag多模态解析
 * 上传的可能是zip文件（包括md，images目录）或md文件，当前只对md文件做解析，后续需要扩展，如images需要拷贝到相关目录下，后续召回时可以查看
 */
@Slf4j
public class MinerUMarkdownSplitter extends TextSplitter{

    // 配置参数
    private static int maxTableRowsPerChunk = 400;
    private static int maxTextTokens = 500;
    private static int overlapTokens = 50;

    // 正则模式
    private static final Pattern PAGE_PATTERN = Pattern.compile("\\[Page\\s+(\\d+)\\]");
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");

    // Token 估算系数
    private static final int CHARS_PER_TOKEN_ESTIMATE = 3;

    /**
     * 核心入口：重写 split 方法以完全控制 Document 的处理流程
     * 这样可以保留 Metadata (页码、表格ID等)，这是 splitText(String) 做不到的
     */
    @Override
    public List<Document> split(List<Document> documents) {
        List<Document> splitDocuments = new ArrayList<>();

        for (Document document : documents) {
            String content = document.getText();
            Map<String, Object> baseMetadata = new HashMap<>(document.getMetadata());

            log.debug("Processing document: {}", baseMetadata.get("source_file"));

            // 1. 解析 Markdown 为逻辑块 (Segment)
            // 这一步在 split 方法中做，因为我们需要 baseMetadata 来初始化页码
            List<BlockSegment> segments = parseMarkdownSegments(content, baseMetadata);

            // 2. 对每个 Segment 进行处理和切分
            for (BlockSegment segment : segments) {
                // 手动构建切分后的文本列表
                List<String> textChunks = processSegmentToText(segment);

                // 3. 将文本 chunks 转换回 Document，并合并元数据
                for (int i = 0; i < textChunks.size(); i++) {
                    String chunkText = textChunks.get(i);
                    if (!StringUtils.hasText(chunkText)) continue;

                    Map<String, Object> chunkMeta = new HashMap<>(baseMetadata);
                    chunkMeta.put("page_number", segment.pageNum);
                    chunkMeta.put("block_type", segment.type);
                    if (segment.tableId != null) {
                        chunkMeta.put("table_id", segment.tableId);
                    }

                    // 添加分片索引
                    if (textChunks.size() > 1) {
                        chunkMeta.put("chunk_index", i);
                        chunkMeta.put("total_chunks", textChunks.size());
                    }

                    // 特殊处理：如果是图片，提取路径存入 meta
                    if ("IMAGE".equals(segment.type)) {
                        Matcher m = IMAGE_PATTERN.matcher(segment.content);
                        if (m.find()) {
                            chunkMeta.put("image_path", m.group(2));
                            chunkMeta.put("has_seal", m.group(1).contains("章") || m.group(1).contains("签名"));
                        }
                    }

                    splitDocuments.add(new Document(chunkText, chunkMeta));
                }
            }
        }

        log.info("Splitting complete. Generated {} chunks.", splitDocuments.size());
        return splitDocuments;
    }

    /**
     * 实现抽象方法 splitText
     * 注意：由于我们的核心逻辑在 split(List<Document>) 中以保留元数据，
     * 此方法主要用于兼容父类接口，或在纯文本场景下被调用。
     * 在这里，我们可以简单地委托给内部逻辑，或者直接抛出异常提示使用 split(List)
     * 但为了健壮性，我们实现一个简单的基于文本的逻辑（不带丰富元数据）
     */
    @Override
    protected List<String> splitText(String text) {
        // 创建一个临时的空 Metadata 用于解析
        Map<String, Object> emptyMeta = new HashMap<>();
        List<BlockSegment> segments = parseMarkdownSegments(text, emptyMeta);

        List<String> result = new ArrayList<>();
        for (BlockSegment segment : segments) {
            result.addAll(processSegmentToText(segment));
        }
        return result;
    }


    private List<BlockSegment> parseMarkdownSegments(String content, Map<String, Object> globalMeta) {
        List<BlockSegment> segments = new ArrayList<>();
        String[] lines = content.split("\n");
        StringBuilder currentTextBuffer = new StringBuilder();
        StringBuilder currentTableBuffer = new StringBuilder();

        int currentPage = globalMeta.containsKey("page_number")
                ? (Integer) globalMeta.get("page_number")
                : 1;

        boolean inTable = false;
        String currentTableId = UUID.randomUUID().toString();

        for (String line : lines) {
            Matcher pageMatcher = PAGE_PATTERN.matcher(line);
            if (pageMatcher.find()) {
                int newPage = Integer.parseInt(pageMatcher.group(1));
                if (newPage != currentPage && currentTextBuffer.length() > 0) {
                    segments.add(new BlockSegment("TEXT", currentTextBuffer.toString(), currentPage, null));
                    currentTextBuffer = new StringBuilder();
                }
                currentPage = newPage;
                continue;
            }

            if (line.trim().startsWith("![")) {
                if (currentTextBuffer.length() > 0) {
                    segments.add(new BlockSegment("TEXT", currentTextBuffer.toString(), currentPage, null));
                    currentTextBuffer = new StringBuilder();
                }
                segments.add(new BlockSegment("IMAGE", line, currentPage, null));
                continue;
            }

            if (line.trim().startsWith("|") && line.contains("|")) {
                if (!inTable) {
                    inTable = true;
                    currentTableBuffer = new StringBuilder();
                    currentTableId = UUID.randomUUID().toString();
                }
                currentTableBuffer.append(line).append("\n");
            } else {
                if (inTable) {
                    segments.add(new BlockSegment("TABLE", currentTableBuffer.toString(), currentPage, currentTableId));
                    inTable = false;
                    currentTableBuffer = new StringBuilder();
                    currentTableId = null;
                }
                if (StringUtils.hasText(line.trim())) {
                    currentTextBuffer.append(line).append("\n");
                } else if (currentTextBuffer.length() > 0) {
                    currentTextBuffer.append("\n");
                }
            }
        }

        if (inTable && currentTableBuffer.length() > 0) {
            segments.add(new BlockSegment("TABLE", currentTableBuffer.toString(), currentPage, currentTableId));
        }
        if (currentTextBuffer.length() > 0) {
            segments.add(new BlockSegment("TEXT", currentTextBuffer.toString(), currentPage, null));
        }

        return segments;
    }

    /**
     * 将 Segment 转换为字符串列表 (切分逻辑)
     * 不再创建 Document，只返回纯文本
     */
    private List<String> processSegmentToText(BlockSegment segment) {
        List<String> chunks = new ArrayList<>();

        if ("IMAGE".equals(segment.type)) {
            String description = "";
            Matcher m = IMAGE_PATTERN.matcher(segment.content);
            if (m.find()) description = m.group(1);

            String enhancedText = String.format("[类型:图片] [页码:%d] 内容描述:%s", segment.pageNum, description);
            chunks.add(enhancedText);
        }
        else if ("TABLE".equals(segment.type)) {
            String[] rows = segment.content.trim().split("\n");
            List<String> validRows = new ArrayList<>();
            for(String r : rows) if(StringUtils.hasText(r.trim())) validRows.add(r);

            if (validRows.isEmpty()) return chunks;

            if (validRows.size() <= maxTableRowsPerChunk) {
                String summary = "[语义摘要:合同数据表格]";
                String enhancedText = String.format("[类型:表格] [页码:%d] %s\n%s", segment.pageNum, summary, segment.content);
                chunks.add(enhancedText);
            } else {
                String headerRow = validRows.get(0);
                String separatorRow = validRows.size() > 1 ? validRows.get(1) : "";
                int dataRowsPerChunk = Math.max(1, maxTableRowsPerChunk - 2);

                for (int i = 2; i < validRows.size(); i += dataRowsPerChunk) {
                    int end = Math.min(i + dataRowsPerChunk, validRows.size());
                    StringBuilder chunkBuilder = new StringBuilder();
                    chunkBuilder.append("[类型:表格(分片)] [页码:").append(segment.pageNum).append("] ");
                    chunkBuilder.append("[语义摘要:合同数据表格] ");
                    chunkBuilder.append(headerRow).append("\n");
                    if (StringUtils.hasText(separatorRow)) chunkBuilder.append(separatorRow).append("\n");

                    for (int j = i; j < end; j++) chunkBuilder.append(validRows.get(j)).append("\n");
                    chunks.add(chunkBuilder.toString());
                }
            }
        }
        else {
            // 普通文本切分 (带重叠)
            String text = segment.content.trim();
            if (!StringUtils.hasText(text)) return chunks;

            int totalChars = text.length();
            int maxChars = maxTextTokens * CHARS_PER_TOKEN_ESTIMATE;
            int overlapChars = overlapTokens * CHARS_PER_TOKEN_ESTIMATE;

            if (totalChars <= maxChars) {
                chunks.add(text);
            } else {
                int start = 0;
                while (start < totalChars) {
                    int end = Math.min(start + maxChars, totalChars);
                    if (end < totalChars) {
                        int lastParagraphBreak = text.lastIndexOf("\n\n", end);
                        int lastLineBreak = text.lastIndexOf("\n", end);
                        int cutPoint = -1;
                        if (lastParagraphBreak > start + maxChars / 2) cutPoint = lastParagraphBreak;
                        else if (lastLineBreak > start + maxChars / 2) cutPoint = lastLineBreak;
                        if (cutPoint != -1) end = cutPoint;
                    }

                    String subText = text.substring(start, end).trim();
                    if (StringUtils.hasText(subText)) chunks.add(subText);

                    start = end - overlapChars;
                    if (start < 0) start = 0;
                    if (end == totalChars) break;
                    if (start >= totalChars) break;
                }
            }
        }
        return chunks;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class BlockSegment {
        private String type;
        private String content;
        private int pageNum;
        private String tableId;
    }
}
