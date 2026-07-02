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

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserService sysUserService;
    private final LoginMockProperties loginMockProperties;
    private final SsoAuthProvider ssoAuthProvider;
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

    // ==================== 登录入口 ====================

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
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(400);
        resp.getWriter().write("{\"code\":400,\"message\":\"SSO 未开启，请使用 POST 登录\"}");
    }

    @ApiAccessLog
    @Idempotent(value = "login", expire = 1, timeUnit = TimeUnit.MINUTES)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public R<Map<String, Object>> loginPost(HttpServletRequest req, HttpServletResponse resp,
                                             @RequestBody(required = false) Map<String, String> body) {
        boolean ssicEnabled = ssicProperties.isEnabled() && ssoAuthProvider != null;
        if (ssicEnabled) {
            return ssiLogin(req, resp, body);
        }
        String userId = (body != null) ? body.getOrDefault("userId", "") : "";
        return mockLogin(userId);
    }

    // ==================== SSIC 登录 ====================

    private R<Map<String, Object>> ssiLogin(HttpServletRequest req, HttpServletResponse resp,
                                             Map<String, String> body) {
        String ssiAuth = null;
        String ssiSign = null;
        if (body != null && !body.isEmpty()) {
            ssiAuth = body.get("SSIAuth");
            ssiSign = body.get("SSI_SIGN");
            if ((ssiAuth == null || ssiAuth.isEmpty()) && body.containsKey("config")) {
                Map<String, String> params = parseQueryString(body.get("config"));
                ssiAuth = params.get("SSIAuth");
                ssiSign = params.get("SSI_SIGN");
            }
        }
        if (ssiAuth == null) ssiAuth = req.getParameter("SSIAuth");
        if (ssiSign == null) ssiSign = req.getParameter("SSI_SIGN");

        log.info("[SSIC] POST login — ssiAuth: {}, ssiSign: {}", ssiAuth, ssiSign);

        if (ssiAuth == null || ssiAuth.isBlank()) {
            String redirectUrl = ssoAuthProvider.getLoginRedirectUrl(ssicProperties.getServiceUrl());
            Map<String, Object> result = new HashMap<>();
            result.put("needRedirect", true);
            result.put("redirectUrl", redirectUrl);
            return R.fail(302, "请先通过统一认证登录", result);
        }

        SsicUser ssicUser;
        try {
            ssicUser = ssoAuthProvider.authenticate(req, resp, ssiAuth, ssiSign);
        } catch (Exception e) {
            log.error("[SSIC] authenticate failed", e);
            throw new RuntimeException("统一认证验签失败: " + e.getMessage(), e);
        }
        if (ssicUser == null) {
            return R.fail(401, "统一认证验证失败");
        }

        String userId = ssicUser.getUserId();
        log.info("[SSIC] authenticate success — userId: {}", userId);

        Map<String, Object> extInfo = ssoAuthProvider.queryUserInfo(userId);
        if (extInfo == null) {
            extInfo = buildUserInfoFromSsicUser(ssicUser);
        }

        SysUser finalUser = sysUserService.syncUserInfo(extInfo);
        List<String> roles = loadRolesForUser(finalUser);
        String token = jwtTokenProvider.generateToken(userId,
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("username", extInfo.getOrDefault("username", ""));
        userInfo.put("branchId", extInfo.getOrDefault("branchId", ""));
        userInfo.put("branchName", extInfo.getOrDefault("branchName", ""));
        userInfo.put("notesId", extInfo.getOrDefault("notesId", ""));
        userInfo.put("branchIdList", extInfo.getOrDefault("branchIdList", List.of()));
        userInfo.put("roles", roles);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return R.ok(result);
    }

    // ==================== Mock 登录 ====================

    private R<Map<String, Object>> mockLogin(String userId) {
        String mockUserId = loginMockProperties.getUserId();
        log.info("Mock login: input={}, using mock user={}", userId, mockUserId);

        Map<String, Object> extInfo = new LinkedHashMap<>();
        extInfo.put("userId", mockUserId);
        extInfo.put("username", loginMockProperties.getUsername());
        extInfo.put("branchId", loginMockProperties.getBranchId());
        extInfo.put("branchName", loginMockProperties.getBranchName());
        extInfo.put("notesId", loginMockProperties.getNotesId());
        extInfo.put("branchIdList", List.of(
            Map.of("branchId", loginMockProperties.getBranchId(),
                   "branchName", loginMockProperties.getBranchName())
        ));

        SysUser finalUser = sysUserService.syncUserInfo(extInfo);
        List<String> roles = loginMockProperties.getRoles();
        String token = jwtTokenProvider.generateToken(mockUserId,
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userId", mockUserId);
        userInfo.put("username", loginMockProperties.getUsername());
        userInfo.put("branchId", loginMockProperties.getBranchId());
        userInfo.put("branchName", loginMockProperties.getBranchName());
        userInfo.put("notesId", loginMockProperties.getNotesId());
        userInfo.put("branchIdList", extInfo.get("branchIdList"));
        userInfo.put("roles", roles);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return R.ok(result);
    }

    // ==================== 辅助 ====================

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new LinkedHashMap<>();
        if (queryString == null || queryString.isBlank()) return params;
        for (String pair : queryString.split("&")) {
            int idx = pair.indexOf("=");
            if (idx > 0) params.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return params;
    }

    private Map<String, Object> buildUserInfoFromSsicUser(SsicUser ssicUser) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("userId", ssicUser.getUserId());
        info.put("username", ssicUser.getUsername() != null ? ssicUser.getUsername() : "");
        info.put("branchId", ssicUser.getBranchId() != null ? ssicUser.getBranchId() : "");
        info.put("branchName", ssicUser.getBranchName() != null ? ssicUser.getBranchName() : "");
        info.put("notesId", ssicUser.getNotesId() != null ? ssicUser.getNotesId() : "");
        info.put("branchIdList", ssicUser.getBranchIdList() != null ? ssicUser.getBranchIdList() : "");
        return info;
    }

    private List<String> loadRolesForUser(SysUser sysUser) {
        if (sysUser == null) return List.of();
        return sysUserService.getRoles(sysUser.getId()).stream()
            .map(role -> "ROLE_" + role.getRoleCode())
            .collect(Collectors.toList());
    }

    // ==================== 兼容端点 ====================

    @ApiAccessLog
    @PostMapping("/sso/login")
    public R<Map<String, Object>> ssoLogin(@RequestBody Map<String, String> request) {
        if (ssoAuthProvider == null || !ssoAuthProvider.enabled()) {
            return R.fail(400, "SSO is not enabled");
        }
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

    // ==================== 配置 + 用户信息 + 登出 ====================

    @GetMapping("/config")
    public R<Map<String, Object>> authConfig() {
        boolean ssoEnabled = ssoAuthProvider != null && ssoAuthProvider.enabled();
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("ssoEnabled", ssoEnabled);
        if (ssoEnabled) {
            config.put("loginUrl", ssoAuthProvider.getLoginRedirectUrl(ssicProperties.getServiceUrl()));
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
        String userId;
        List<String> roles;
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            userId = userDetails.getUsername();
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        } else {
            userId = principal.toString();
            roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        }

        SysUser sysUser = sysUserService.getByUserId(userId);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("username", sysUser != null ? sysUser.getUsername() : "");
        userInfo.put("branchId", sysUser != null ? sysUser.getBranchId() : "");
        userInfo.put("branchName", sysUser != null ? sysUser.getBranchName() : "");
        userInfo.put("notesId", sysUser != null ? sysUser.getNotesId() : "");
        userInfo.put("roles", roles);

        return R.ok(userInfo);
    }
}
