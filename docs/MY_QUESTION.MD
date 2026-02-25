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

## 3.流程分析
### 3.1 意图识别
IntentRecognitionNode，实现NodeAction，根据输入，+提示词：intent-recognition，调用大模型，推理用户问题是否问数还是闲聊
返回classification（大模型返回值），把结果写入INTENT_RECOGNITION_NODE_OUTPUT边中
classification：值为"《闲聊或无关指令》"或"《可能的数据分析请求》"
```java
public class IntentRecognitionNode implements NodeAction {
	private final LlmService llmService;
	private final JsonParseUtil jsonParseUtil;
	@Override
	public Map<String, Object> apply(OverAllState state) throws Exception {

		// 获取用户输入
		String userInput = StateUtil.getStringValue(state, INPUT_KEY);
		log.info("User input for intent recognition: {}", userInput);

		String multiTurn = StateUtil.getStringValue(state, MULTI_TURN_CONTEXT, "(无)");

		// 构建意图识别提示
		String prompt = PromptHelper.buildIntentRecognitionPrompt(multiTurn, userInput);
		log.debug("Built intent recognition prompt as follows \n {} \n", prompt);

		// 调用LLM进行意图识别
		Flux<ChatResponse> responseFlux = llmService.callUser(prompt);

		Flux<GraphResponse<StreamingOutput>> generator = FluxUtil.createStreamingGenerator(this.getClass(), state,
				responseFlux,
				Flux.just(ChatResponseUtil.createResponse("正在进行意图识别..."),
						ChatResponseUtil.createPureResponse(TextType.JSON.getStartSign())),
				Flux.just(ChatResponseUtil.createPureResponse(TextType.JSON.getEndSign()),
						ChatResponseUtil.createResponse("\n意图识别完成！")),
				result -> {
					// 使用JsonParseUtil解析JSON并转换为IntentRecognitionOutputDTO对象
					IntentRecognitionOutputDTO intentRecognitionOutput = jsonParseUtil.tryConvertToObject(result,
							IntentRecognitionOutputDTO.class);
					return Map.of(INTENT_RECOGNITION_NODE_OUTPUT, intentRecognitionOutput);
				});
		return Map.of(INTENT_RECOGNITION_NODE_OUTPUT, generator);
	}
}
```
IntentRecognitionDispatcher，implements EdgeAction，意图识别结果分发器（边）
```java
public class IntentRecognitionDispatcher implements EdgeAction {
	@Override
	public String apply(OverAllState state) throws Exception {
		// 获取意图识别结果
		IntentRecognitionOutputDTO intentResult = StateUtil.getObjectValue(state, INTENT_RECOGNITION_NODE_OUTPUT,
				IntentRecognitionOutputDTO.class);

		if (intentResult == null || intentResult.getClassification() == null
				|| intentResult.getClassification().trim().isEmpty()) {
			log.warn("Intent recognition result is null or empty, defaulting to END");
			return END;
		}

		String classification = intentResult.getClassification();

		// 根据分类结果决定下一个节点
		if ("《闲聊或无关指令》".equals(classification)) {
			log.warn("Intent classified as chat or irrelevant, ending conversation");
			return END;
		}
		else {
			log.info("Intent classified as potential data analysis request, proceeding to evidence recall");
			return EVIDENCE_RECALL_NODE;
		}
	}
}
```
### 3.2 问题重写 EvidenceRecallNode
EvidenceRecallNode，查询重写。
1.从多轮对话和用户最新输入，调用大模型，重写用户最新输入的问题，提示词evidence-query-rewrite
2.根据大模型返回的重写问题，查询知识库信息（智能体知识召回），获取业务知识文档，获取智能体知识文档，然后返回信息进行合并
3.根据业务知识/智能体知识构造参考信息(加到返回中)
4.无条件直连QUERY_ENHANCE_NODE
### 3.3 查询扩展/增强 QueryEnhanceNode
问题+业务知识/智能体知识
1.从问题重写的输出中获取EVIDENCE（参考信息）
2.根据用户原始问题+参考信息+提示词query-enhancement，调用大模型（重写问题只为了知识召回？如果替换为用户问题或把重写问题的结果同时给大模型是否更好？）
3.llm返回规范化查询和扩展查询
4.根据规范化查询和扩展查询（llm结果）进行路由QueryEnhanceDispatcher，确认执行节点
4.1没有规范化查询或扩展查询结果时，END节点
4.2有规范化查询或扩展查询时，SCHEMA_RECALL_NODE节点
### 3.4 数据库相关信息（表，字段）SchemaRecallNode(根据关键词和意图检索)
1.根据规范化查询，获取agent相关表（RAG）
2.根据相关表，获取相关列(RAG)
3.把表名，列名信息返回
4.SCHEMA_RECALL_NODE_OUTPUT，SchemaRecallDispatcher，确定流程节点
没有相关表名时，END
有相关表名时TABLE_RELATION_NODE节点
### 3.5 TABLE_RELATION_NODE TableRelationNode
动完成复杂结构（如*JOIN和外键*）的表关系推断节点。
推断表和字段之间的关系
1.获取规范化查询（QueryEnhanceNode节点输出）
2.获取EVIDENCE（EvidenceRecallNode节点输出）
3.获取表和列信息（SchemaRecallNode节点输出）
4.获取agent配置的数据源（DB层面）
5.获取agent配置的逻辑外键（DB层面配置）并过滤（只保留当前表相关的（LLM返回和db配置方面））
6.构造数据库schema包括表名、表描述、表之间的外键依赖，每张表中包含多个列的列名、列描述和主键信息
7.调用模型semantic-model，生成用户问题相关表
8.构建语义模型提示并存储到OverAllState中
9.TABLE_RELATION_OUTPUT，TableRelationDispatcher确定流程节点（TABLE_RELATION_RETRY_COUNT，TABLE_RELATION_EXCEPTION_OUTPUT值什么时候变化？）
有错并且重试次数<3时，重写执行TableRelationNode节点
从流程中能获取到TABLE_RELATION_OUTPUT值时，进入FEASIBILITY_ASSESSMENT_NODE，没有值END
### 3.6 FEASIBILITY_ASSESSMENT_NODE FeasibilityAssessmentNode
可行性评估节点，看需求是 数据分析/需要澄清 或者最终确认为自由闲聊
1.获取规范化查询（QueryEnhanceNode节点输出）
2.获取数据库表相关信息（表，外键，描述等）（TableRelationNode节点输出）
3.获取EVIDENCE（EvidenceRecallNode节点输出）
4.把表，字段，外键等信息，拼接进提示词+feasibility-assessment(提示词)
	public static String buildFeasibilityAssessmentPrompt(String canonicalQuery, SchemaDTO recalledSchema,
		String evidence, String multiTurn) {
		Map<String, Object> params = new HashMap<>();
		String schemaInfo = buildMixMacSqlDbPrompt(recalledSchema, true);
		params.put("canonical_query", canonicalQuery != null ? canonicalQuery : "");
		params.put("recalled_schema", schemaInfo);
		params.put("evidence", evidence != null ? evidence : "");
		params.put("multi_turn", multiTurn != null ? multiTurn : "(无)");
		return PromptConstant.getFeasibilityAssessmentPromptTemplate().render(params);
	}
5.调用模型，确定需求类型（《数据分析》、《需要澄清》、《自由闲聊》）
【需求类型】：《数据分析》、《需要澄清》、《自由闲聊》
【语种类型】：中文
【需求内容】：...
6.FEASIBILITY_ASSESSMENT_NODE_OUTPUT，FeasibilityAssessmentDispatcher确定流程节点
值中有【需求类型】：《数据分析》的，进入PLANNER_NODE，其它的END
### 3.7 进入PLANNER_NODE PlannerNode
生成sql计划节点
1.是否onlyNl2sql，是直接根据问题生成sql（返回sql）；否，生成查询计划
生成查询计划的逻辑
获取处理后的查询（canonicalQuery，规范化查询）
检查是否有验证错误（validationError），如果有则重新生成计划
获取数据库模式信息（schemaStr）和语义模型（semanticModel）（RAG）
构建用户提示（userPrompt）
使用PromptConstant.getPlannerPromptTemplate()生成提示词模板
调用LLM服务生成计划
PLANNER_NODE_OUTPUT，进入PlanExecutorNode
### 3.8 PlanExecutorNode
计划执行和验证节点，根据计划决定下一个执行节点，并在执行前进行验证
SQL_GENERATE_NODE/PYTHON_GENERATE_NODE/REPORT_GENERATOR_NODE/HUMAN_FEEDBACK_NODE
PlanExecutorDispatcher
### SqlGenerateNode sql脚本生成
sql-error-fixer，sql修复提示词
new-sql-generate，生成sql提示词
修复sql错误次数大于约定次数时，END
SqlGenerateDispatcher,判断进入sql执行节点SqlExecuteNode还是SQL查询语义一致性的语义一致性验证节点SemanticConsistencyNode节点
### SemanticConsistencyNode sql语义验证
semantic-consistency
SemanticConsistenceDispatcher确定进入的节点（sql正确，进入SqlExecuteNode，错误SqlGenerateNode）
### SqlExecuteNode sql执行
SQLExecutorDispatcher

### 3.9 PythonGenerateNode python代码生成
1.从graph中获取数据库表相关信息（表，外键，描述等）（TableRelationNode节点输出）
2.从graph中获取sql执行结果List（可能还没有）
3.从graph中获取python执行结果
3.1当前执行python执行结果为false（失败）
从graph中获取最后的python代码和python执行失败信息，构造用户问题
根据提示词python-generator+用户问题+数据库表结构+sql样例，生成无状态python脚本（用代码模型是否更好）
3.2没有当前执行python执行结果（第一次）
获取处理后的查询（canonicalQuery，规范化查询）+提示词python-generator+数据库表结构，调用模型

跳转PYTHON_GENERATE_NODE_OUTPUT PythonExecuteNode
### PythonExecuteNode python执行节点
获取python代码（PythonGenerateNode输出），检查重试次数等信息
执行python代码（错误，只要没有超过最大次数限制，重试）
PythonExecutorDispatcher，决定后续节点（错误重试重新进入python代码生成节点，降解或正常进入分析节点，END等）
### HumanFeedbackNode
用户反馈节点
HumanFeedbackDispatcher
### PythonAnalyzeNode python分析节点
python-analyze
### ReportGeneratorNode
报告生成
report-generator-plain
END

# 增强
## 向量存储使用es,参考MY_ES_CONFIG.md （完成）
## 生成的sql或python代码有时不准确，尝试用编码模型,采用qwen-max后，相对好点，但是复杂的查询，代码还是不准确
如：查询2025年销售量总额最高和最低的产品，需要展示其销售总额，销售数量，产品名称和产品单价
## 车辆profile识别
如何根据用户的输入信息，判断是否需要优先识别车辆profile查询，如用户输入：查询profile F55 XU91 7HQ当前价格？
需要模型识别：
1.这是车型或车辆查询
2.模型需要知道F55 XU91 7HQ对应的是什么意思，是车型表中的哪些字段
3.模型需要返回如eseries_code:F55,model_code:XU91,package_code:7HQ类似信息（即字段，值对应关系）
4.对应关系作为sql生成节点的依据，SqlGeneratNode节点sql的生成，有些需要依赖3的输入
解决方式：
1.新增tool，tool的返回作为附加到SqlGeneratNode的输入。SqlGeneratNode执行时，判断是车型相关查询并且有车型相关核心数据的输入时，调用tool
结果不好
2.基于rag（有业务知识模块，上传相关的知识库）+代码调整(代码改动量大，不通用，效果也一般)
调整内容：
2.1 意图识别，新增业务域
2.2 创建业务规则文档（RAG 基础）business_domain_rules_vehicle.md
```md
# 车辆业务域规则
## 业务描述
车辆销售查询业务，包括产品系列、型号、包装规格和目标日期的查询
## 字段映射规则
| 源值模式 | 目标列 | 业务含义 | 示例 |
|----------|--------|----------|------|
| `[A-Z]\d{2}` | eseries_code | 产品系列代码 | F56, A12 |
| `[A-Z]{2}\d{2}` | model_code | 产品型号代码 | XU91, AB12 |
| `[A-Z]{2}\d{2}` | package_code | 包装规格代码 | ZA18, PK01 |
| `\d{4}` | target_date | 目标日期(MMDD) | 0616, 1225 |

## 必填字段
- eseries_code (必须)
- model_code (必须)
```
通过 DataAgent 管理后台上传此文档（/management/knowledge）(业务知识模块)。
2.3 修改 SQL 生成节点（使用 RAG 检索）SqlGenerateNode
3.新增tool，在EvidenceRecallNode节点中，新增tool，（可以在agent的配置中，新增tool工具的配置）
3.1 EvidenceRecallNode，调用tool，该tool会先判断是否车辆查询（关键字和正则表达），如果是，工具会调用模型识别对应的字段
business_domain_rules_vehicle.md，返回eseries_code=xx，model_code=xx,并把结果暂时存储到graph的运行数据中TOOL_FIELD_VALUE_MAPPING
3.2 sql生成节点，取TOOL_FIELD_VALUE_MAPPING值放入提示词中，new-sql-generate.txt修改提示词，新增## 5. 已识别的结构化参数（参考），告诉模型如果有值并且可以作为where条件时，使用该条件
3.3 在可行性评估节点FeasibilityAssessmentNode中，加入TOOL_FIELD_VALUE_MAPPING值，优先级高于Evidence，让其进入可行性评估
提示词+代码需要调整（todo）









