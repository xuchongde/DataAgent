package com.alibaba.cloud.ai.headless.common.embedding;

import com.alibaba.cloud.ai.dataagent.constant.Constant;
import com.alibaba.cloud.ai.dataagent.constant.DocumentMetadataConstant;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import com.alibaba.cloud.ai.headless.common.pojo.DataItem;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class TextSegmentConvert {
    private static final Logger logger = LoggerFactory.getLogger(TextSegmentConvert.class);
    public static final String QUERY_ID = "queryId";

    public static List<Document> convertToEmbedding(List<DataItem> dataItems) {
        return dataItems.stream().map(item -> {
            // suffix with underscore to avoid embedding issue
            DataItem newItem = DataItem.builder().domainId(item.getDomainId())
                    .bizName(item.getBizName()).type(item.getType()).newName(item.getNewName())
                    .defaultAgg(item.getDefaultAgg()).name(item.getName())
                    .id(item.getId() + Constants.UNDERLINE)
                    .modelId(item.getModelId() + Constants.UNDERLINE)
                    .domainId(item.getDomainId() + Constants.UNDERLINE).build();

            String jsonString = JSONObject.toJSONString(newItem);
            Map meta = JSONObject.parseObject(jsonString, Map.class);
            Map<String, Object> metadata = new HashMap<>();
            // answer和isRecall经常变更的放到关系数据库
            metadata.put(Constant.AGENT_ID, newItem.getDomainId().toString());
            logger.info("type name :{}",newItem.getType().name());
            metadata.put(DocumentMetadataConstant.VECTOR_TYPE, newItem.getType().name());
            metadata.put(DocumentMetadataConstant.REF_DB_ID, newItem.getId());
            metadata.putAll(meta);
            Document document = new Document(item.getName(),metadata);
            return document;
        }).collect(Collectors.toList());
    }
}
