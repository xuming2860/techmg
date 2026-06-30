package com.icbc.sh.techmg.framework.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SSO 认证提供者 Mock 实现。
 * 仅在 sso.enabled=true 时激活，用于开发/测试环境。
 * TODO: 对接行内统一认证平台后替换为真实实现。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sso.enabled", havingValue = "true")
public class SsoAuthProviderImpl implements SsoAuthProvider {

    @Override
    public String authenticate(String ssoToken) {
        // TODO: call ICBC SSO service to validate ticket and get userId
        // For now, treat ssoToken as userId for test/dev
        log.warn("SSO authenticate using mock -- token: {}", ssoToken);
        return ssoToken;
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public String getLoginUrl() {
        // TODO: return actual SSO login URL
        return null;
    }

    @Override
    public String getLogoutUrl() {
        // TODO: return actual SSO logout URL
        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String userId) {
        // TODO: call external API to query user extended info
        // POST /api/external/user/query {userId}
        // Once the external API is available, replace this hardcoded data

        log.info("SSO getUserInfo using MOCK data for userId: {}", userId);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("authNo", "0011491111");
        userInfo.put("tellername", "徐敏");
        userInfo.put("ad", "");
        userInfo.put("branchId", "12092342");
        userInfo.put("branchName", "上海技术部");
        userInfo.put("notesId", "0011491111@sdc.com");
        // branchIdList: user may belong to multiple branches
        userInfo.put("branchIdList", Collections.singletonList(
                Map.of("branchId", "12092342", "branchName", "上海技术部")
        ));

        return userInfo;
    }
}
