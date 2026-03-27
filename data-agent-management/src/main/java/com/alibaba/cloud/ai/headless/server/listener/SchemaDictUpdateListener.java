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
package com.alibaba.cloud.ai.headless.server.listener;

import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import com.alibaba.cloud.ai.headless.common.pojo.DataEvent;
import com.alibaba.cloud.ai.headless.common.pojo.enums.DictWordType;
import com.alibaba.cloud.ai.headless.common.pojo.enums.EventType;
import com.alibaba.cloud.ai.headless.chat.knowledge.DictWord;
import com.alibaba.cloud.ai.headless.chat.knowledge.helper.HanlpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
public class SchemaDictUpdateListener {

    @Async("eventExecutor")
    @EventListener
    public void onApplicationEvent(DataEvent dataEvent) {
        if (CollectionUtils.isEmpty(dataEvent.getDataItems())) {
            return;
        }
        dataEvent.getDataItems().forEach(dataItem -> {
            DictWord dictWord = new DictWord();
            dictWord.setWord(dataItem.getName());
            String sign = DictWordType.NATURE_SPILT;
            String suffixNature = DictWordType.getSuffixNature(dataItem.getType());
            String nature = sign + dataItem.getModelId() + sign + dataItem.getId() + suffixNature;
            String natureWithFrequency = nature + " " + Constants.DEFAULT_FREQUENCY;
            dictWord.setNature(nature);
            dictWord.setNatureWithFrequency(natureWithFrequency);
            if (EventType.ADD.equals(dataEvent.getEventType())) {
                HanlpHelper.addToCustomDictionary(dictWord);
            } else if (EventType.DELETE.equals(dataEvent.getEventType())) {
                HanlpHelper.removeFromCustomDictionary(dictWord);
            } else if (EventType.UPDATE.equals(dataEvent.getEventType())) {
                HanlpHelper.removeFromCustomDictionary(dictWord);
                dictWord.setWord(dataItem.getNewName());
                HanlpHelper.addToCustomDictionary(dictWord);
            }
        });
    }
}
