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
package com.alibaba.cloud.ai.headless.server.modeller;

import com.alibaba.cloud.ai.dataagent.service.llm.LlmService;
import com.alibaba.cloud.ai.dataagent.util.JsonParseUtil;
import com.alibaba.cloud.ai.headless.api.pojo.DbSchema;
import com.alibaba.cloud.ai.headless.api.pojo.ModelSchema;
import com.alibaba.cloud.ai.headless.api.pojo.request.ModelBuildReq;
import com.alibaba.cloud.ai.headless.common.pojo.ChatApp;
import com.alibaba.cloud.ai.headless.common.pojo.enums.AppModule;
import com.alibaba.cloud.ai.headless.common.util.ChatAppManager;
import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.common.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class LLMSemanticModeller implements SemanticModeller {

    public static final String APP_KEY = "BUILD_DATA_MODEL";

    private static final String SYS_EXEMPLAR_FILE = "s2-buildModel-exemplar.json";
    private final LlmService llmService;
    private final JsonParseUtil jsonParseUtil;
    public static final String INSTRUCTION = ""
            + "Role: As an experienced data analyst with extensive modeling experience, "
            + "      you are expected to have a deep understanding of data analysis and data modeling concepts."
            + "\nJob: You will be given a database table structure, which includes the database table name, field name,"
            + "       field type, and field comments. Your task is to utilize this information for data modeling."
            + "\nTask:"
            + "\n1. Generate a name and description for the model. Please note, 'bizName' refers to the English name, while 'name' is the Chinese name."
            + "\n2. Create a Chinese name for the field and categorize the field into one of the following five types:"
            + "\n   primary_key: This is a unique identifier for a record row in a database."
            + "\n   foreign_key: This is a key in a database whose value is derived from the primary key of another table."
            + "\n   partition_time: This represents the time when data is generated in the data warehouse."
            + "\n   dimension: Usually a string type, used for grouping and filtering data. No need to generate aggregate functions"
            + "\n   measure: Usually a numeric type, used to quantify data from a certain evaluative perspective. "
            + "              Also, you need to generate aggregate functions(Eg: MAX, MIN, AVG, SUM, COUNT) for the measure type. "
            + "\nTip: I will also give you other related dbSchemas. If you determine that different dbSchemas have the same fields, "
            + "       they can be primary and foreign key relationships."
            + "\nDBSchema: {{DBSchema}}" + "\nOtherRelatedDBSchema: {{otherRelatedDBSchema}}"
            + "\nExemplar: {{exemplar}}";

    private final ObjectMapper objectMapper = JsonUtil.INSTANCE.getObjectMapper();

    public LLMSemanticModeller(LlmService llmService,JsonParseUtil jsonParseUtil) {
        this.llmService = llmService;
        this.jsonParseUtil=jsonParseUtil;
        ChatAppManager.register(APP_KEY, ChatApp.builder().prompt(INSTRUCTION).name("构造数据语义模型")
                .appModule(AppModule.HEADLESS).description("通过大模型来构造数据语义模型").enable(true).build());
    }

    interface ModelSchemaExtractor {
        ModelSchema generateModelSchema(String text);
    }

    @Override
    public void build(DbSchema dbSchema, List<DbSchema> dbSchemas, ModelSchema modelSchema,
            ModelBuildReq modelBuildReq) {
        if (!modelBuildReq.isBuildByLLM()) {
            return;
        }
        Optional<ChatApp> chatApp = ChatAppManager.getApp(APP_KEY);
        if (!chatApp.isPresent() || !chatApp.get().isEnable()) {
            return;
        }

        List<DbSchema> otherDbSchema = getOtherDbSchema(dbSchema, dbSchemas);
        String prompt = generatePrompt(dbSchema, otherDbSchema, chatApp.get());
        Flux<ChatResponse> chatResponseFlux = llmService.callUser(prompt);
        String response = llmService.collectFluxToStringSafe(chatResponseFlux);
        //json to Obj
        if(StringUtils.isNotBlank(response)){
            modelSchema = jsonParseUtil.tryConvertToObject(response, ModelSchema.class);
        }
        log.info("dbSchema:  {}\n otherRelatedDBSchema:{}\n modelSchema: {}",
                JsonUtil.toString(dbSchema), JsonUtil.toString(otherDbSchema),
                JsonUtil.toString(modelSchema));
    }

    private List<DbSchema> getOtherDbSchema(DbSchema curSchema, List<DbSchema> dbSchemas) {
        return dbSchemas.stream()
                .filter(dbSchema -> !dbSchema.getTable().equals(curSchema.getTable()))
                .collect(Collectors.toList());
    }

    private String generatePrompt(DbSchema dbSchema, List<DbSchema> otherDbSchema,
            ChatApp chatApp) {
        Map<String, Object> variable = new HashMap<>();
        variable.put("exemplar", loadExemplars());
        variable.put("DBSchema", JsonUtil.toString(dbSchema));
        variable.put("otherRelatedDBSchema", JsonUtil.toString(otherDbSchema));
        PromptTemplate promptTemplate = new PromptTemplate(INSTRUCTION);
        return promptTemplate.render(variable);
    }

    private String loadExemplars() {
        Environment environment = ContextUtils.getBean(Environment.class);
        String enableExemplarLoading =
                environment.getProperty("s2.model.building.exemplars.enabled");
        if (Boolean.FALSE.equals(Boolean.parseBoolean(enableExemplarLoading))) {
            log.info("Not enable load model-building exemplars");
            return "";
        }
        try {
            ClassPathResource resource = new ClassPathResource(SYS_EXEMPLAR_FILE);
            if (resource.exists()) {
                InputStream inputStream = resource.getInputStream();
                return objectMapper
                        .writeValueAsString(objectMapper.readValue(inputStream, Object.class));
            }
        } catch (Exception e) {
            log.error("Failed to load model-building system exemplars", e);
        }
        return "";
    }

}
