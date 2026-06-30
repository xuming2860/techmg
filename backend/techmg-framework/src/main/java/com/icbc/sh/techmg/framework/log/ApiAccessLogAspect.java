package com.icbc.sh.techmg.framework.log;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class ApiAccessLogAspect {

    private final Gson gson = new Gson();

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

        long start = System.currentTimeMillis();
        try {
            Object result = point.proceed();
            long cost = System.currentTimeMillis() - start;
            log.info("[API] {} {} | {}.{} | IP:{} | cost:{}ms | result:{}",
                    method, url, className, methodName, ip, cost,
                    result != null ? gson.toJson(result) : "null");
            return result;
        } catch (Throwable e) {
            long cost = System.currentTimeMillis() - start;
            log.error("[API] {} {} | {}.{} | IP:{} | cost:{}ms | error:{}",
                    method, url, className, methodName, ip, cost, e.getMessage(), e);
            throw e;
        }
    }
}
