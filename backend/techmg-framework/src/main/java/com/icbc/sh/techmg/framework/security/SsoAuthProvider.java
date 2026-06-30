package com.icbc.sh.techmg.framework.security;

import java.util.Map;

/**
 * SSO 单点登录扩展接口。
 * 后期对接行内统一认证平台（如 OAuth2 / LDAP / SAML）时实现此接口。
 */
public interface SsoAuthProvider {

    /** 根据 SSO token 验证用户身份，返回 authNo */
    String authenticate(String ssoToken);

    /** 是否启用 SSO 模式 */
    default boolean enabled() { return false; }

    /** 获取 SSO 登录页面 URL（重定向用） */
    default String getLoginUrl() { return null; }

    /** 获取 SSO 登出 URL */
    default String getLogoutUrl() { return null; }

    /**
     * 查询用户扩展信息（SSO 认证后从外部系统获取）。
     * @param userId the authNo from SSO
     * @return user info map with keys: authNo, tellername, ad, branchId, branchName, notesId, branchIdList
     */
    default Map<String, Object> getUserInfo(String userId) { return null; }
}
