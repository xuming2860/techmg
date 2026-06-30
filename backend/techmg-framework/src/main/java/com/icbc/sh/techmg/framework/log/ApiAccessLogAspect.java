package com.icbc.sh.techmg.framework.log;

import com.google.gson.Gson;
import com.icbc.sh.techmg.common.event.OperationLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Aspect
@Component
public class ApiAccessLogAspect {

    private final Gson gson = new Gson();
    private final ApplicationEventPublisher eventPublisher;

    @Value("${api.access-log.max-length:4000}")
    private int maxLogLength;

    public ApiAccessLogAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Around("@annotation(com.icbc.sh.techmg.framework.log.ApiAccessLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String method = request != null ? request.getMethod() : "";
        String ip = request != null ? request.getRemoteAddr() : "";
        String userAgent = request != null ? request.getHeader("User-Agent") : "";
        String referer = request != null ? request.getHeader("Referer") : "";
        if (userAgent == null) userAgent = "";
        if (referer == null) referer = "";

        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // get operator from SecurityContext
        String operator = "anonymous";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            operator = authentication.getName();
        }

        // capture request params (truncated to maxLogLength)
        String requestParam = "";
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            try {
                requestParam = gson.toJson(args);
                requestParam = truncate(requestParam, maxLogLength);
            } catch (Exception ignored) {
                requestParam = "[serialization error]";
            }
        }

        long start = System.currentTimeMillis();
        try {
            Object result = point.proceed();
            long cost = System.currentTimeMillis() - start;

            String responseResult = result != null ? gson.toJson(result) : "null";
            responseResult = truncate(responseResult, maxLogLength);
            int resultJsonLen = responseResult.length();

            log.info("[API] {} {} | IP:{} | UA:{} | Referer:{} | Operator:{} | Cost:{}ms\n" +
                     "     Request: {}\n" +
                     "     Response(code=200, size={}B): {}",
                    method, getFullUrl(request), ip, userAgent, referer, operator, cost,
                    requestParam, resultJsonLen, responseResult);

            // publish event for async DB logging
            publishEvent(className, methodName, operator, requestParam, responseResult, ip, cost, 1);

            return result;
        } catch (Throwable e) {
            long cost = System.currentTimeMillis() - start;

            String responseResult = "error: " + e.getMessage();
            responseResult = truncate(responseResult, maxLogLength);

            // build stack trace summary (first 3 frames)
            String stackSummary = "";
            StackTraceElement[] stack = e.getStackTrace();
            if (stack != null && stack.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Math.min(3, stack.length); i++) {
                    sb.append("     at ").append(stack[i].toString()).append("\n");
                }
                sb.append("     ...[stack truncated]");
                stackSummary = sb.toString();
            }

            log.error("[API] {} {} | IP:{} | UA:{} | Operator:{} | Cost:{}ms | ERROR: {}\n" +
                      "     Request: {}\n" +
                      "     Response(error): {}\n" +
                      "{}",
                    method, getFullUrl(request), ip, userAgent, operator, cost, e.getClass().getSimpleName(),
                    requestParam, responseResult, stackSummary);

            // publish event for async DB logging (failure)
            publishEvent(className, methodName, operator, requestParam, responseResult, ip, cost, 0);

            throw e;
        }
    }

    private String getFullUrl(HttpServletRequest request) {
        if (request == null) return "";
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        return query != null ? uri + "?" + query : uri;
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "null";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + " ...[truncated, total: " + text.length() + " chars]";
    }

    private void publishEvent(String module, String operation, String operator,
                              String requestParam, String responseResult,
                              String ip, Long costTime, Integer status) {
        try {
            eventPublisher.publishEvent(new OperationLogEvent(
                    this, module, operation, operator,
                    requestParam, responseResult, ip, costTime, status));
        } catch (Exception e) {
            log.warn("Failed to publish OperationLogEvent: {}", e.getMessage());
        }
    }
}
