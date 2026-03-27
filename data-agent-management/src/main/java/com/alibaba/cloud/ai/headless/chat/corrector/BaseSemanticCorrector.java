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
package com.alibaba.cloud.ai.headless.chat.corrector;

import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlAddHelper;
import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlRemoveHelper;
import com.alibaba.cloud.ai.headless.common.pojo.enums.AggregateTypeEnum;
import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElement;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticSchema;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * basic semantic correction functionality, offering common methods and an abstract method called
 * doCorrect
 */
@Slf4j
public abstract class BaseSemanticCorrector implements SemanticCorrector {

    public void correct(ChatQueryContext chatQueryContext, SemanticParseInfo semanticParseInfo) {
        try {
            String s2SQL = semanticParseInfo.getSqlInfo().getCorrectedS2SQL();
            if (Objects.isNull(s2SQL)) {
                semanticParseInfo.getSqlInfo()
                        .setCorrectedS2SQL(semanticParseInfo.getSqlInfo().getParsedS2SQL());
            }
            doCorrect(chatQueryContext, semanticParseInfo);
            log.debug("sqlCorrection:{} sql:{}", this.getClass().getSimpleName(),
                    semanticParseInfo.getSqlInfo());
        } catch (Exception e) {
            log.error(String.format("correct error,sqlInfo:%s", semanticParseInfo.getSqlInfo()), e);
        }
    }

    public abstract void doCorrect(ChatQueryContext chatQueryContext,
            SemanticParseInfo semanticParseInfo);

    protected Map<String, String> getFieldNameMap(ChatQueryContext chatQueryContext,
            Long dataSetId) {

        return getFieldNameMapFromDB(chatQueryContext, dataSetId);
    }

    private static Map<String, String> getFieldNameMapFromDB(ChatQueryContext chatQueryContext,
            Long dataSetId) {
        SemanticSchema semanticSchema = chatQueryContext.getSemanticSchema();

        List<SchemaElement> dbAllFields = new ArrayList<>();
        dbAllFields.addAll(semanticSchema.getMetrics());
        dbAllFields.addAll(semanticSchema.getDimensions());

        // support fieldName and field alias
        return dbAllFields.stream().filter(entry -> dataSetId.equals(entry.getDataSetId()))
                .flatMap(schemaElement -> {
                    Set<String> elements = new HashSet<>();
                    elements.add(schemaElement.getName());
                    if (!CollectionUtils.isEmpty(schemaElement.getAlias())) {
                        elements.addAll(schemaElement.getAlias());
                    }
                    return elements.stream();
                }).collect(Collectors.toMap(a -> a, a -> a, (k1, k2) -> k1));
    }

    protected void addAggregateToMetric(ChatQueryContext chatQueryContext,
            SemanticParseInfo semanticParseInfo) {
        // add aggregate to all metric
        String correctS2SQL = semanticParseInfo.getSqlInfo().getCorrectedS2SQL();
        Long dataSetId = semanticParseInfo.getDataSet().getDataSetId();
        List<SchemaElement> metrics = getMetricElements(chatQueryContext, dataSetId);

        Map<String, String> metricToAggregate = metrics.stream().map(schemaElement -> {
            if (Objects.isNull(schemaElement.getDefaultAgg())) {
                schemaElement.setDefaultAgg(AggregateTypeEnum.SUM.name());
            }
            return schemaElement;
        }).flatMap(schemaElement -> {
            Set<String> elements = new HashSet<>();
            elements.add(schemaElement.getName());
            if (!CollectionUtils.isEmpty(schemaElement.getAlias())) {
                elements.addAll(schemaElement.getAlias());
            }
            return elements.stream()
                    .map(element -> Pair.of(element, schemaElement.getDefaultAgg()));
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (k1, k2) -> k1));

        if (CollectionUtils.isEmpty(metricToAggregate)) {
            return;
        }
        String aggregateSql = SqlAddHelper.addAggregateToField(correctS2SQL, metricToAggregate);
        semanticParseInfo.getSqlInfo().setCorrectedS2SQL(aggregateSql);
    }

    protected List<SchemaElement> getMetricElements(ChatQueryContext chatQueryContext,
            Long dataSetId) {
        SemanticSchema semanticSchema = chatQueryContext.getSemanticSchema();
        return semanticSchema.getMetrics(dataSetId);
    }

    protected Set<String> getDimensions(Long dataSetId, SemanticSchema semanticSchema) {
        Set<String> dimensions =
                semanticSchema.getDimensions(dataSetId).stream().flatMap(schemaElement -> {
                    Set<String> elements = new HashSet<>();
                    elements.add(schemaElement.getName());
                    if (!CollectionUtils.isEmpty(schemaElement.getAlias())) {
                        elements.addAll(schemaElement.getAlias());
                    }
                    return elements.stream();
                }).collect(Collectors.toSet());
        return dimensions;
    }

    protected boolean containsPartitionDimensions(ChatQueryContext chatQueryContext,
            SemanticParseInfo semanticParseInfo) {
        Long dataSetId = semanticParseInfo.getDataSetId();
        SemanticSchema semanticSchema = chatQueryContext.getSemanticSchema();
        DataSetSchema dataSetSchema = semanticSchema.getDataSetSchemaMap().get(dataSetId);
        return dataSetSchema.containsPartitionDimensions();
    }

    protected void removeDateIfExist(ChatQueryContext chatQueryContext,
            SemanticParseInfo semanticParseInfo) {
        String correctS2SQL = semanticParseInfo.getSqlInfo().getCorrectedS2SQL();
        Set<String> removeFieldNames = new HashSet<>();
        Map<String, String> fieldNameMap =
                getFieldNameMapFromDB(chatQueryContext, semanticParseInfo.getDataSetId());
        removeFieldNames.removeIf(fieldName -> fieldNameMap.containsKey(fieldName));
        if (!CollectionUtils.isEmpty(removeFieldNames)) {
            correctS2SQL = SqlRemoveHelper.removeWhereCondition(correctS2SQL, removeFieldNames);
            correctS2SQL = SqlRemoveHelper.removeSelect(correctS2SQL, removeFieldNames);
            correctS2SQL = SqlRemoveHelper.removeGroupBy(correctS2SQL, removeFieldNames);
        }
        semanticParseInfo.getSqlInfo().setCorrectedS2SQL(correctS2SQL);
    }
}
