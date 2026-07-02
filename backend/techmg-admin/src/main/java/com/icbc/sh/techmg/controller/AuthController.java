package com.icbc.sh.techmg.controller;

import com.icbc.sh.techmg.common.annotation.Idempotent;
import com.icbc.sh.techmg.common.config.SsicProperties;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.config.LoginMockProperties;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.framework.security.JwtTokenProvider;
import com.icbc.sh.techmg.framework.security.SsoAuthProvider;
import com.icbc.sh.techmg.framework.security.SsicUser;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证控制器 — 支持 Mock 和 SSIC 统一认证两种模式。
 *
 * <h3>SSIC 统一认证流程</h3>
 * <ol>
 *   <li>前端无 token → GET /api/auth/login → 后端 302 重定向到 SSO 登录页</li>
 *   <li>用户在 SSO 登录 → 回跳 client.site.url?SSIAuth=xxx&amp;SSI_SIGN=xxx</li>
 *   <li>前端截取 URL ? 后参数 → POST /api/auth/login (config=原始参数字符串)</li>
 *   <li>后端验证 SSI 参数 → 获取 SSIcUser → 查询 AAM 用户信息 → 生成 JWT</li>
 * </ol>
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserService sysUserService;
    private final LoginMockProperties loginMockProperties;
    private final SsoAuthProvider ssoAuthProvider; // null if SSO not configured
    private final SsicProperties ssicProperties;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          SysUserService sysUserService,
                          LoginMockProperties loginMockProperties,
                          @Autowired(required = false) SsoAuthProvider ssoAuthProvider,
                          SsicProperties ssicProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sysUserService = sysUserService;
        this.loginMockProperties = loginMockProperties;
        this.ssoAuthProvider = ssoAuthProvider;
        this.ssicProperties = ssicProperties;
    }

    // ==================== 登录入口（GET + POST） ====================

    /**
     * GET 登录 — SSIC 开启时重定向到 SSO 登录页。
     *
     * 对应行内流程：前端无 token → GET /api/auth/login → 302 重定向到统一认证登录页。
     */
    @ApiAccessLog
    @GetMapping("/login")
    public void loginGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean ssicEnabled = ssicProperties.isEnabled() && ssoAuthProvider != null;
        if (ssicEnabled) {
            String serviceUrl = ssicProperties.getServiceUrl();
            String redirectUrl = ssoAuthProvider.getLoginRedirectUrl(serviceUrl);
            if (redirectUrl != null) {
                log.info("[SSIC] GET login → redirect to: {}", redirectUrl);
                resp.sendRedirect(redirectUrl);
                return;
            }
            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(500);
            resp.getWriter().write("{\"code\":500,\"message\":\"SSO 登录地址未配置\"}");
            return;
        }
        // SSIC 未开启 → GET 不支持
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(400);
        resp.getWriter().write("{\"code\":400,\"message\":\"SSO 未开启，请使用 POST 登录\"}");
    }

    /**
     * POST 登录 — SSIC 模式验证 SSIAuth/SSI_SIGN，Mock 模式自动登录。
     *
     * 请求体:
     * <ul>
     *   <li>Mock: {"authNo":"", "password":""}</li>
     *   <li>SSIC JSON: {"SSIAuth":"xxx", "SSI_SIGN":"xxx"} 或 {"config":"SSIAuth=xxx&SSI_SIGN=xxx"}</li>
     * </ul>
     */
    @ApiAccessLog
    @Idempotent(value = "login", expire = 1, timeUnit = TimeUnit.MINUTES)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public R<Map<String, Object>> loginPost(HttpServletRequest req, HttpServletResponse resp,
                                             @RequestBody(required = false) Map<String, String> body) {
        boolean ssicEnabled = ssicProperties.isEnabled() && ssoAuthProvider != null;

        if (ssicEnabled) {
            return ssiLogin(req, resp, body);
        }

        // Mock 模式
        String authNo = (body != null) ? body.getOrDefault("authNo", "") : "";
        return mockLogin(authNo);
    }

    // ==================== SSIC 登录 ====================

    /**
     * SSIC 统一认证登录。
     *
     * 对应行内流程:
     * <pre>
     *   serverSideAuth.setServiceURL(url);
     *   serverSideAuth.setOperation(SIGN_IN);
     *   if (serverSideAuth.execute(req, resp, ssiAuth, ssiSign)) {
     *       Credentials cred = req.getAttribute(SSI_CREDENTIALS);
     *       ssicUser = cred.getSsIcUser();
     *       forwardPage = Ls.loginTmvpEm(req, resp, ssicUser, url);
     *   }
     * </pre>
     */
    private R<Map<String, Object>> ssiLogin(HttpServletRequest req, HttpServletResponse resp,
                                             Map<String, String> body) {
        // 1. 提取 SSIAuth / SSI_SIGN 参数
        String ssiAuth = null;
        String ssiSign = null;

        if (body != null && !body.isEmpty()) {
            ssiAuth = body.get("SSIAuth");
            ssiSign = body.get("SSI_SIGN");
            // 也支持 config 字段（前端传原始 query string）
            if ((ssiAuth == null || ssiAuth.isEmpty()) && body.containsKey("config")) {
                Map<String, String> params = parseQueryString(body.get("config"));
                ssiAuth = params.get("SSIAuth");
                ssiSign = params.get("SSI_SIGN");
            }
        }

        // 也尝试从 URL query string 获取
        if (ssiAuth == null) ssiAuth = req.getParameter("SSIAuth");
        if (ssiSign == null) ssiSign = req.getParameter("SSI_SIGN");

        log.info("[SSIC] POST login — ssiAuth: {}, ssiSign: {}", ssiAuth, ssiSign);

        // 2. 无 SSI 参数 → 无法登录，返回重定向提示
        if (ssiAuth == null || ssiAuth.isBlank()) {
            String redirectUrl = ssoAuthProvider.getLoginRedirectUrl(
                    ssicProperties.getServiceUrl());
            Map<String, Object> result = new HashMap<>();
            result.put("needRedirect", true);
            result.put("redirectUrl", redirectUrl);
            return R.fail(302, "请先通过统一认证登录", result);
        }

        // 3. 执行 SSIC 验证
        SsicUser ssicUser;
        try {
            ssicUser = ssoAuthProvider.authenticate(req, resp, ssiAuth, ssiSign);
        } catch (Exception e) {
            log.error("[SSIC] authenticate failed — ssiAuth: {}, ssiSign: {}", ssiAuth, ssiSign, e);
            throw new RuntimeException("统一认证验签失败: " + e.getMessage(), e);
        }

        if (ssicUser == null) {
            return R.fail(401, "统一认证验证失败");
        }

        String authNo = ssicUser.getUserId();
        log.info("[SSIC] authenticate success — userId: {}", authNo);

        // 4. 查询用户扩展信息（AAM）
        Map<String, Object> extInfo = ssoAuthProvider.queryUserInfo(authNo);
        if (extInfo == null) {
            // fallback: 使用 SSIcUser 中的基本信息
            extInfo = buildUserInfoFromSsicUser(ssicUser);
        }

        // 5. 同步到 sys_user 表
        SysUser finalUser = sysUserService.syncUserInfo(extInfo);

        // 6. 加载角色 + 生成 JWT
        List<String> roles = loadRolesForUser(finalUser);
        String token = jwtTokenProvider.generateToken(authNo,
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        // 7. 构建响应
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("authNo", authNo);
        userInfo.put("realName", finalUser != null ? finalUser.getRealName() : "");
        userInfo.put("tellername", extInfo.getOrDefault("tellername", ""));
        userInfo.put("ad", extInfo.getOrDefault("ad", ""));
        userInfo.put("branchId", extInfo.getOrDefault("branchId", ""));
        userInfo.put("branchName", extInfo.getOrDefault("branchName", ""));
        userInfo.put("notesId", extInfo.getOrDefault("notesId", ""));
        userInfo.put("branchIdList", extInfo.getOrDefault("branchIdList", List.of()));
        userInfo.put("urlPermission", extInfo.getOrDefault("urlPermission", "all"));
        userInfo.put("roles", roles);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return R.ok(result);
    }

    // ==================== Mock 登录 ====================

    /**
     * Mock 登录：使用 login.mock 配置的用户信息 + 动态角色 → JWT。
     * 自动同步用户到 sys_user 表，记录登录时间。
     */
    private R<Map<String, Object>> mockLogin(String authNo) {
        String mockAuthNo = loginMockProperties.getAuthNo();
        log.info("Mock login: input={}, using mock user={}", authNo, mockAuthNo);

        Map<String, Object> extInfo = new LinkedHashMap<>();
        extInfo.put("authNo", mockAuthNo);
        extInfo.put("tellername", loginMockProperties.getRealName());
        extInfo.put("ad", loginMockProperties.getAdAccount());
        extInfo.put("branchId", loginMockProperties.getBranchId());
        extInfo.put("branchName", loginMockProperties.getBranchName());
        extInfo.put("notesId", loginMockProperties.getNotesId());
        extInfo.put("branchIdList", List.of(
            Map.of("branchId", loginMockProperties.getBranchId(),
                   "branchName", loginMockProperties.getBranchName())
        ));
        extInfo.put("urlPermission", "all");

        SysUser finalUser = sysUserService.syncUserInfo(extInfo);

        List<String> roles = loginMockProperties.getRoles();
        String token = jwtTokenProvider.generateToken(mockAuthNo,
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("authNo", mockAuthNo);
        userInfo.put("realName", finalUser != null ? finalUser.getRealName() : "");
        userInfo.put("tellername", loginMockProperties.getRealName());
        userInfo.put("ad", loginMockProperties.getAdAccount());
        userInfo.put("branchId", loginMockProperties.getBranchId());
        userInfo.put("branchName", loginMockProperties.getBranchName());
        userInfo.put("notesId", loginMockProperties.getNotesId());
        userInfo.put("branchIdList", extInfo.get("branchIdList"));
        userInfo.put("urlPermission", "all");
        userInfo.put("roles", roles);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return R.ok(result);
    }

    // ==================== 辅助方法 ====================

    /**
     * 解析 URL query string (key=value&key=value) 为 Map。
     */
    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new LinkedHashMap<>();
        if (queryString == null || queryString.isBlank()) return params;
        for (String pair : queryString.split("&")) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                params.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
        }
        return params;
    }

    private Map<String, Object> buildUserInfoFromSsicUser(SsicUser ssicUser) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("authNo", ssicUser.getUserId());
        info.put("tellername", ssicUser.getTellerName() != null ? ssicUser.getTellerName() : "");
        info.put("ad", ssicUser.getAd() != null ? ssicUser.getAd() : "");
        info.put("branchId", ssicUser.getBranchId() != null ? ssicUser.getBranchId() : "");
        info.put("branchName", ssicUser.getBranchName() != null ? ssicUser.getBranchName() : "");
        info.put("notesId", ssicUser.getNotesId() != null ? ssicUser.getNotesId() : "");
        info.put("branchIdList", ssicUser.getBranchIdList() != null
                ? ssicUser.getBranchIdList() : "");
        info.put("urlPermission", "all");
        return info;
    }

    private List<String> loadRolesForUser(SysUser sysUser) {
        if (sysUser == null) return List.of();
        return sysUserService.getRoles(sysUser.getId()).stream()
            .map(role -> "ROLE_" + role.getRoleCode())
            .collect(Collectors.toList());
    }

    // ==================== 旧 SSO 端点（保留兼容） ====================

    @ApiAccessLog
    @PostMapping("/sso/login")
    public R<Map<String, Object>> ssoLogin(@RequestBody Map<String, String> request) {
        if (ssoAuthProvider == null || !ssoAuthProvider.enabled()) {
            return R.fail(400, "SSO is not enabled");
        }
        // 委托给统一的 ssiLogin（通过 body 中的 SSIAuth/SSI_SIGN）
        // 这里需要构造 body 字符串；简单处理：传 null 让 ssiLogin 从 request param 读取
        return R.fail(400, "请使用 POST /api/auth/login 并传入 SSIAuth/SSI_SIGN 参数");
    }

    @ApiAccessLog
    @GetMapping("/sso/login-url")
    public R<Map<String, String>> ssoLoginUrl() {
        if (ssoAuthProvider == null || !ssoAuthProvider.enabled()) {
            return R.fail(400, "SSO is not enabled");
        }
        String url = ssoAuthProvider.getLoginRedirectUrl(ssicProperties.getServiceUrl());
        Map<String, String> data = new HashMap<>();
        data.put("loginUrl", url != null ? url : "");
        return R.ok(data);
    }

    // ==================== 认证配置 + 用户信息 + 登出 ====================

    @GetMapping("/config")
    public R<Map<String, Object>> authConfig() {
        boolean ssoEnabled = ssoAuthProvider != null && ssoAuthProvider.enabled();
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("ssoEnabled", ssoEnabled);
        if (ssoEnabled) {
            config.put("loginUrl", ssoAuthProvider.getLoginRedirectUrl(
                    ssicProperties.getServiceUrl()));
            config.put("ssicEnabled", true);
            config.put("clientSiteUrl", ssicProperties.getClientSiteUrl());
        } else {
            config.put("loginUrl", "");
            config.put("ssicEnabled", false);
        }
        return R.ok(config);
    }

    @ApiAccessLog
    @PostMapping("/logout")
    public R<Void> logout() {
        SecurityContextHolder.clearContext();
        return R.ok();
    }

    @ApiAccessLog
    @GetMapping("/userinfo")
    public R<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return R.fail(401, "未登录");
        }

        Object principal = authentication.getPrincipal();
        String authNo;
        List<String> roles;
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            authNo = userDetails.getUsername();
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        } else {
            authNo = principal.toString();
            roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }

        SysUser sysUser = sysUserService.getByAuthNo(authNo);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("authNo", authNo);
        userInfo.put("realName", sysUser != null ? sysUser.getRealName() : "");
        userInfo.put("tellername", sysUser != null ? sysUser.getRealName() : "");
        userInfo.put("ad", sysUser != null ? sysUser.getAdAccount() : "");
        userInfo.put("branchId", sysUser != null ? sysUser.getBranchId() : "");
        userInfo.put("branchName", sysUser != null ? sysUser.getBranchName() : "");
        userInfo.put("notesId", sysUser != null ? sysUser.getNotesId() : "");
        userInfo.put("urlPermission", sysUser != null ? sysUser.getUrlPermission() : "all");
        userInfo.put("roles", roles);

        return R.ok(userInfo);
    }
}
