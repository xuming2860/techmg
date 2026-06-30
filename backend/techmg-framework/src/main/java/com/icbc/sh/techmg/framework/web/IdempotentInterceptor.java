package com.icbc.sh.techmg.framework.web;

import com.icbc.sh.techmg.common.annotation.Idempotent;
import com.icbc.sh.techmg.framework.redis.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentInterceptor implements HandlerInterceptor {

    private final RedisUtil redisUtil;
    private static final String HEADER = "Idempotency-Key";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod hm)) return true;

        Idempotent idempotent = hm.getMethodAnnotation(Idempotent.class);
        if (idempotent == null) return true;

        String key = request.getHeader(HEADER);
        if (key == null || key.isEmpty()) {
            throw new RuntimeException("幂等请求缺少 Idempotency-Key 请求头");
        }

        String redisKey = "idempotent:" + (idempotent.value().isEmpty() ? "" : idempotent.value() + ":") + key;
        if (redisUtil.hasKey(redisKey)) {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":200,\"message\":\"操作已处理(幂等)\",\"data\":null}");
            } catch (Exception ignored) {}
            return false;
        }

        redisUtil.setWithExpire(redisKey, "1", idempotent.expire(), idempotent.timeUnit());
        return true;
    }
}
