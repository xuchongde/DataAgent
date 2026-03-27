/*
 * Copyright 2024-2026 the original author or authors.
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
package com.alibaba.cloud.ai.dataagent.service.llm;

import com.alibaba.cloud.ai.dataagent.util.ChatResponseUtil;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.time.Duration;

public interface LlmService {

	Flux<ChatResponse> call(String system, String user);

	Flux<ChatResponse> callSystem(String system);

	Flux<ChatResponse> callUser(String user);

	@Deprecated
	default String blockToString(Flux<ChatResponse> responseFlux) {
		return toStringFlux(responseFlux).collect(StringBuilder::new, StringBuilder::append)
			.map(StringBuilder::toString)
			.block();
	}

	default Flux<String> toStringFlux(Flux<ChatResponse> responseFlux) {
		return responseFlux.map(ChatResponseUtil::getText);
	}

	/**
	 * 接收流式数据并转换成string
	 * @param chatResponseFlux
	 * @return
	 */
	default String collectFluxToStringSafe(Flux<ChatResponse> chatResponseFlux) {
		try {
			return chatResponseFlux
					.map(response -> {
						if (response.getResult() != null && response.getResult().getOutput() != null) {
							return response.getResult().getOutput().getText();
						}
						return "";
					})
					.collectList()
					.timeout(Duration.ofSeconds(60)) // 设置 60 秒超时，防止 LLM 卡死
					.blockOptional() // 使用 blockOptional 避免直接抛异常，返回 Optional
					.map(list -> list.stream().collect(java.util.stream.Collectors.joining()))
					.orElse(""); // 如果超时或为空，返回空字符串

		} catch (Exception e) {
			// 记录日志
			throw new RuntimeException("LLM stream processing failed", e);
		}
	}

}
