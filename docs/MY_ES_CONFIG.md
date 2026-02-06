#data agent 配置es作为向量数据库

# es安装
1、下载https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.19.6-windows-x86_64.zip
2、解压到自己当前账号下，如C:\Users\uf202488\elasticsearch-8.19.6
3、创建数据目录和日志目录
4、修改config/elasticsearch.yml的配置文件（配置端口、数据/日志目录、禁用安全等（DEV环境））
参考：
```
``# ======================== Elasticsearch Configuration =========================
#
# NOTE: Elasticsearch comes with reasonable defaults for most settings.
#       Before you set out to tweak and tune the configuration, make sure you
#       understand what are you trying to accomplish and the consequences.
#
# The primary way of configuring a node is via this file. This template lists
# the most important settings you may want to configure for a production cluster.
#
# Please consult the documentation for further information on configuration options:
# https://www.elastic.co/guide/en/elasticsearch/reference/index.html
#
# ---------------------------------- Cluster -----------------------------------
#
# Use a descriptive name for your cluster:
#
#cluster.name: my-application
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#
#node.name: node-1
#
# Add custom attributes to the node:
#
#node.attr.rack: r1
#
# ----------------------------------- Paths ------------------------------------
#
# Path to directory where to store the data (separate multiple locations by comma):
#
path.data: C:/Users/uf202488/elasticsearch-8.19.6/data_user
#
# Path to log files:
#
path.logs: C:/Users/uf202488/elasticsearch-8.19.6/logs_user
#
# ----------------------------------- Memory -----------------------------------
#
# Lock the memory on startup:
#
bootstrap.memory_lock: false
#
# Make sure that the heap size is set to about half the memory available
# on the system and that the owner of the process is allowed to use this
# limit.
#
# Elasticsearch performs poorly when the system is swapping the memory.
#
# ---------------------------------- Network -----------------------------------
#
# By default Elasticsearch is only accessible on localhost. Set a different
# address here to expose this node on the network:
#
network.host: 127.0.0.1
#
# By default Elasticsearch listens for HTTP traffic on the first free port it
# finds starting at 9200. Set a specific HTTP port here:
#
http.port: 9200
#
# For more information, consult the network module documentation.
#
# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when this node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
#
#discovery.seed_hosts: ["host1", "host2"]
#
# Bootstrap the cluster using an initial set of master-eligible nodes:
#
#cluster.initial_master_nodes: ["node-1", "node-2"]
#
# For more information, consult the discovery and cluster formation module documentation.
# ---------------------------------- Security ----------------------------------
#
# Enable security features
#
xpack.security.enabled: false
xpack.security.enrollment.enabled: false
#
# Enable encryption for HTTP API client connections
#
xpack.security.http.ssl.enabled: false
#
# Enable encryption and mutual authentication between cluster nodes
#
xpack.security.transport.ssl.enabled: false
#
# ---------------------------------- Various -----------------------------------
#
# Allow wildcard deletion of indices:
#
# action.destructive_requires_name: false
```
5、修改jvm参数，config\jvm.options，-Xms，-Xmn
6、启动bin/elasticsearch.bat
7、初始化数据库，在bin目录下创建data_agent_mapping.json，内容如下
```json
{
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "embedding": {
        "type": "dense_vector",
        "dims": 1024,
        "index": true,
        "similarity": "cosine",
        "index_options": {
          "type": "int8_hnsw",
          "m": 16,
          "ef_construction": 100
        }
      },
      "id": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "metadata": {
        "properties": {
          "agentId": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "agentKnowledgeId": {
            "type": "long"
          },
          "businessTermId": {
            "type": "long"
          },
          "concreteAgentKnowledgeType": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "vectorType": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          }
        }
      }
    }
  }
}
```json
8、执行命令，初始化
curl -X PUT "http://localhost:9200/data_agent_index" -H "Content-Type: application/json" -d @data_agent_mapping.json
9、验证是否成功curl 
http://localhost:9201/data_agent_index/_mapping?pretty
10、application.yml配置
```yml
spring:
  ai:
    vectorstore:
      type:elasticsearch
      elasticsearch:
        uris: http://127.0.0.1:9200
        index-name: dataagent  #必须与 curl 创建的索引名严格一致
        embedding-dimensions: 1024
        validate-index: false
        similarity: cosine
        initialize-schema: false # 防止SpringAI覆盖int8_hnsw配置
        connection-timeout: 5000  # 新增连接超时(毫秒)
        socket-timeout: 10000     # 新增Socket超时(毫秒)
```
11、启动DataAgent服务
注意elastic.clients和es server版本必须一致
12、配置Agent的知识
13、查询data_agent_index
