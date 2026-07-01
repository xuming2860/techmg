package com.icbc.sh.techmg.framework.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 幂等拦截器 — 当前为 pass-through（Redis 已移除，待行内 NOS 封装后再启用）
 */
@Slf4j
@Component
public class IdempotentInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // TODO: 待行内 NOS（基于 Redis 改造）封装后实现幂等逻辑
        return true;
    }
}
