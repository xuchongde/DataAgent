# 问题
## 1.导入数据时报外键约束问题
手动去除外键，导入成功后，再加上

## 2.配置千问
### 2.1 chat
模型名称：qwen-turbo
Base URL：https://dashscope.aliyuncs.com/compatible-mode
### 2.2 text-embedding
模型名称：text-embedding-v4
Base Url: https://dashscope.aliyuncs.com/compatible-mode
Base URL：
- https://dashscope.aliyuncs.com/compatible-mode
- https://dashscope.aliyuncs.com/api/v1/
- https://dashscope.aliyuncs.com/compatible-mode/v1（官网提供地址）
都报404
git bash命令行调用（不是window shell）
  curl --location 'https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings' \
  --header "Authorization: Bearer sk-a2341aef5b974a55a93d1fa4c5b65161" \
  --header 'Content-Type: application/json' \
  --data '{"model": "text-embedding-v4", "input": "衣服的质量杠杠的，很漂亮，不枉我等了这么久啊，喜欢，以后还来这里买", "encoding_format": "float"}'
执行上面的命令，能返回，说明路径正确

## 3.智能体配置
### 3.1基本信息
agent表：agent
### 3.2数据源配置
1.添加数据源，主要就是访问数据库的相关信息，地址，端口，用户名，密码，数据库名。
注意连接地址不会自动根据上面的信息自动拼接，修改时要注意，特别时数据库名
agent绑定的数据源：agent_datasource
？能否有多个数据源，如果可以，后续textToSql怎么实现？？？
2.配置数据表
在数据源配置完成后-》数据表管理，刷新表列表，选择表，然后点击《跟新数据表》，才会把agent和要分析的表绑定起来（绑定或解绑）
agent绑定的数据表：agent_datasource_tables
3.初始化数据源
agent和数据源中的表绑定后
4.初始化数据源
***
/api/agent/{agentId}/datasources/init
把agent相关的表/字段信息进行向量化（纯数据库层面，ddl）
如果已经初始化过，根据agentId，删除已经存在的向量信息，再重新初始化

