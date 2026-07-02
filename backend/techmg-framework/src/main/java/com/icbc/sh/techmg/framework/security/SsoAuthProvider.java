package com.icbc.sh.techmg.framework.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * SSIC 统一认证扩展接口 — 对接行内 SSIC 平台。
 *
 * <h3>登录流程</h3>
 * <ol>
 *   <li>前端无 token → GET /api/auth/login → 本接口 {@link #getLoginRedirectUrl(String)} 返回 SSO 登录页 → 302 重定向</li>
 *   <li>用户在 SSO 页登录成功 → 回跳 client.site.url?SSIAuth=xxx&amp;SSI_SIGN=xxx</li>
 *   <li>前端截取 URL 中 ? 后的参数 → POST /api/auth/login → 后端调用 {@link #authenticate(HttpServletRequest, HttpServletResponse, String, String)}</li>
 *   <li>验证通过 → 获取 SSIcUser → 调用 {@link #queryUserInfo(String)} 查询扩展信息 → 生成 JWT</li>
 * </ol>
 *
 * <h3>行内 SDK 对应关系</h3>
 * <pre>
 *   serverSideAuth.setServiceURL(url);       → 由调用方设置
 *   serverSideAuth.setOperation(SIGN_IN);     → 由调用方设置
 *   serverSideAuth.execute(req, resp, ssiAuth, ssiSign);  → {@link #authenticate}
 *   Credentials cred = req.getAttribute(SSI_CREDENTIALS);  → authenticate 内部处理
 *   ssicUser = cred.getSsIcUser();            → authenticate 返回值
 *   Ls.loginTmvpEm(req, resp, ssicUser, url); → 由 AuthController 调用
 * </pre>
 */
public interface SsoAuthProvider {

    /**
     * 执行统一认证验证（对应 serverSideAuth.execute）。
     * 验证通过后从 Request Attribute 中获取 Credentials → SSIcUser。
     *
     * @param req     HTTP 请求
     * @param resp    HTTP 响应
     * @param ssiAuth SSIAuth 参数值
     * @param ssiSign SSI_SIGN 参数值
     * @return SsicUser 统一认证用户信息，验证失败返回 null
     */
    SsicUser authenticate(HttpServletRequest req, HttpServletResponse resp,
                          String ssiAuth, String ssiSign);

    /** 是否启用 SSIC 统一认证 */
    default boolean enabled() { return false; }

    /**
     * 获取统一认证登录页跳转 URL。
     * 对应行内代码: {@code serverSideAuth.setServiceURL(url)} 后重定向到 SSO 登录页。
     *
     * @param serviceUrl 登录成功后的回调地址（即后端 /api/auth/login 的完整 URL）
     * @return SSO 登录页完整 URL
     */
    default String getLoginRedirectUrl(String serviceUrl) { return null; }

    /** 获取 SSO 登出 URL */
    default String getLogoutUrl() { return null; }

    /**
     * 通过统一认证号查询用户扩展信息。
     * 对应行内代码 queryUserInfoAAM(userId)。
     *
     * @param userId 统一认证号（9位）
     * @return user info map，keys: authNo, tellername, ad, branchId, branchName, notesId, branchIdList, SAERole
     */
    default Map<String, Object> queryUserInfo(String userId) { return null; }
}
