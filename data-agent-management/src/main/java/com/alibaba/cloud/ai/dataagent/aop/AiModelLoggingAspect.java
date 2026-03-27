
package com.alibaba.cloud.ai.dataagent.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import org.springframework.ai.chat.model.ChatResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AiModelLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(AiModelLoggingAspect.class);

    @Around("execution(* org.springframework.ai.chat.client.DefaultChatClient.*(..))")
    public Object logAiModelCall(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();

        // 安全记录请求参数
        String safeArgs = getSafeRequestParams(methodName, args);
        logger.info("AI Model Request: Method={}, Args={}", methodName, safeArgs);

        try {
            Class<?> returnType = method.getReturnType();

            // 处理流式响应 (Flux)
            if (Flux.class.isAssignableFrom(returnType)) {
                return handleStreamResponse(joinPoint, method, methodName);
            }
            // 处理非流式响应 (ChatResponse)
            else if (ChatResponse.class.isAssignableFrom(returnType)) {
                return handleNonStreamResponse(joinPoint, method, methodName);
            }
            // 处理其他情况（如构建器方法）
            else {
                return joinPoint.proceed();
            }
        } catch (Exception e) {
            // 捕获并记录所有异常
            logger.error("AI Model Request Failed: Method={}, Args={}, Error={}",
                    methodName, getSafeRequestParams(methodName, args), e.getMessage(), e);
            throw e;
        }
    }

    private String getSafeRequestParams(String methodName, Object[] args) {
        // 仅对关键方法记录安全参数
        if ("prompt".equals(methodName) || "system".equals(methodName) || "user".equals(methodName)) {
            if (args.length > 0 && args[0] instanceof String) {
                String content = (String) args[0];
                return content.length() > 500 ? content.substring(0, 500) + "..." : content;
            }
        }
        return "N/A";
    }

    private Object handleStreamResponse(ProceedingJoinPoint joinPoint, Method method, String methodName) throws Throwable {
        // 执行原始方法获取Flux
        Flux<ChatResponse> flux = (Flux<ChatResponse>) joinPoint.proceed();

        // 用于收集完整响应内容
        List<String> fullResponseParts = new ArrayList<>();
        AtomicBoolean isComplete = new AtomicBoolean(false);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        // 处理流式响应：收集所有片段
        Flux<ChatResponse> processedFlux = flux
                .doOnNext(chatResponse -> {
                    String content = chatResponse.getResult().getOutput().getText();
                    fullResponseParts.add(content);
                })
                .doOnComplete(() -> {
                    isComplete.set(true);
                    String fullResponse = String.join("", fullResponseParts);
                    // 仅记录最终完整内容
                    logger.info("AI Model Response (Final): {}",fullResponse.length() > 1000 ? fullResponse.substring(0, 1000) + "..." : fullResponse);
                })
                .doOnError(e -> {
                    errorRef.set(e);
                    logger.error("AI Model Stream Error: {}", e.getMessage(), e);
                });

        // 确保在流被取消时也能记录错误
        processedFlux = processedFlux.doOnCancel(() -> {
            if (errorRef.get() != null) {
                logger.error("AI Model Stream Canceled with Error: {}", errorRef.get().getMessage(), errorRef.get());
            } else if (!isComplete.get()) {
                logger.warn("AI Model Stream Canceled without error");
            }
        });

        return processedFlux;
    }

    private Object handleNonStreamResponse(ProceedingJoinPoint joinPoint, Method method, String methodName) throws Throwable {
        // 执行原始方法获取响应
        ChatResponse response = (ChatResponse) joinPoint.proceed();

        // 仅记录最终完整内容
        String fullResponse = response.getResult().getOutput().getText();
        logger.info("AI Model Response (Final): {}",fullResponse.length() > 1000 ? fullResponse.substring(0, 1000) + "..." : fullResponse);

        return response;
    }
}
