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

@Slf4j
@Aspect
@Component
public class ApiAccessLogAspect {

    private final Gson gson = new Gson();
    private final ApplicationEventPublisher eventPublisher;

    public ApiAccessLogAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Around("@annotation(com.icbc.sh.techmg.framework.log.ApiAccessLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String url = request != null ? request.getRequestURI() : "";
        String method = request != null ? request.getMethod() : "";
        String ip = request != null ? request.getRemoteAddr() : "";

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

        // capture request params (truncated to 2000 chars)
        String requestParam = "";
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            try {
                requestParam = gson.toJson(args);
                if (requestParam.length() > 2000) {
                    requestParam = requestParam.substring(0, 2000);
                }
            } catch (Exception ignored) {
                requestParam = "[serialization error]";
            }
        }

        long start = System.currentTimeMillis();
        try {
            Object result = point.proceed();
            long cost = System.currentTimeMillis() - start;

            String responseResult = result != null ? gson.toJson(result) : "null";
            if (responseResult.length() > 2000) {
                responseResult = responseResult.substring(0, 2000);
            }

            log.info("[API] {} {} | {}.{} | IP:{} | cost:{}ms | result:{}",
                    method, url, className, methodName, ip, cost, responseResult);

            // publish event for async DB logging
            publishEvent(className, methodName, operator, requestParam, responseResult, ip, cost, 1);

            return result;
        } catch (Throwable e) {
            long cost = System.currentTimeMillis() - start;

            String responseResult = "error: " + e.getMessage();
            if (responseResult.length() > 2000) {
                responseResult = responseResult.substring(0, 2000);
            }

            log.error("[API] {} {} | {}.{} | IP:{} | cost:{}ms | error:{}",
                    method, url, className, methodName, ip, cost, e.getMessage(), e);

            // publish event for async DB logging (failure)
            publishEvent(className, methodName, operator, requestParam, responseResult, ip, cost, 0);

            throw e;
        }
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
