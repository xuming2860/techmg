package com.icbc.sh.techmg.controller;

import com.icbc.sh.techmg.common.annotation.Idempotent;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.config.LoginMockProperties;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.framework.security.JwtTokenProvider;
import com.icbc.sh.techmg.framework.security.SsoAuthProvider;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserService sysUserService;
    private final LoginMockProperties loginMockProperties;

    @Autowired(required = false)
    private SsoAuthProvider ssoAuthProvider;

    /**
     * 登录 — 支持 SSO 模式和 Mock 模式。
     *
     * Mock 模式（sso.enabled=false）：跳过密码验证，返回配置文件中 login.mock 的用户信息。
     * SSO  模式（sso.enabled=true） ：通过统一认证 ticket 登录。
     */
    @ApiAccessLog
    @Idempotent(value = "login", expire = 1, timeUnit = TimeUnit.MINUTES)
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String authNo = loginRequest.get("authNo");

        // SSO 模式 → 委托 ssoLogin
        if (ssoAuthProvider != null && ssoAuthProvider.enabled()) {
            return ssoLogin(loginRequest);
        }

        // Mock 模式 — 跳过密码验证，返回配置的固定用户
        return mockLogin(authNo);
    }

    /**
     * Mock 登录：使用 login.mock 配置的用户信息 + 动态角色 → JWT。
     * 自动同步用户到 sys_user 表，记录登录时间。
     */
    private R<Map<String, Object>> mockLogin(String authNo) {
        String mockAuthNo = loginMockProperties.getAuthNo();
        log.info("Mock login: input={}, using mock user={}", authNo, mockAuthNo);

        // 构建用户扩展信息（从配置读取）
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

        // 同步到 sys_user（upsert + 更新 last_login_time）
        SysUser finalUser = sysUserService.syncUserInfo(extInfo);

        // 角色从配置读取
        List<String> roles = loginMockProperties.getRoles();

        // 生成 JWT
        String token = jwtTokenProvider.generateToken(mockAuthNo,
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        // 构建响应
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("authNo", mockAuthNo);
        userInfo.put("realName", finalUser != null ? finalUser.getRealName() : "");
        userInfo.put("tellername", loginMockProperties.getRealName());
        userInfo.put("ad", loginMockProperties.getAdAccount());
        userInfo.put("branchId", loginMockProperties.getBranchId());
        userInfo.put("branchName", loginMockProperties.getBranchName());
        userInfo.put("notesId", loginMockProperties.getNotesId());
        userInfo.put("roles", roles);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return R.ok(result);
    }

    @ApiAccessLog
    @PostMapping("/sso/login")
    public R<Map<String, Object>> ssoLogin(@RequestBody Map<String, String> request) {
        if (ssoAuthProvider == null || !ssoAuthProvider.enabled()) {
            return R.fail(400, "SSO is not enabled");
        }

        String ticket = request.get("ticket");
        if (ticket == null || ticket.isBlank()) {
            return R.fail(400, "Missing SSO ticket");
        }

        // Step 1: Validate SSO ticket → get userId (authNo)
        String authNo = ssoAuthProvider.authenticate(ticket);
        if (authNo == null || authNo.isBlank()) {
            return R.fail(401, "SSO authentication failed");
        }

        // Step 2: Query user extended info from external API (TODO → mock for now)
        Map<String, Object> extInfo = ssoAuthProvider.getUserInfo(authNo);
        if (extInfo == null) {
            extInfo = new HashMap<>();
            extInfo.put("authNo", authNo);
        }

        // Step 3: Sync to sys_user (upsert + update last_login_time)
        SysUser finalUser = sysUserService.syncUserInfo(extInfo);

        // Step 4: Load roles and generate JWT
        List<String> roles = loadRolesForUser(finalUser);

        String token = jwtTokenProvider.generateToken(authNo,
            roles.stream().map(SimpleGrantedAuthority::new)
                 .collect(Collectors.toList()));

        // Step 5: Build response with extended user info
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("authNo", authNo);
        userInfo.put("realName", finalUser != null ? finalUser.getRealName() : "");
        userInfo.put("tellername", extInfo.getOrDefault("tellername", ""));
        userInfo.put("ad", extInfo.getOrDefault("ad", ""));
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

    @ApiAccessLog
    @GetMapping("/sso/login-url")
    public R<Map<String, String>> ssoLoginUrl() {
        if (ssoAuthProvider == null || !ssoAuthProvider.enabled()) {
            return R.fail(400, "SSO is not enabled");
        }
        String url = ssoAuthProvider.getLoginUrl();
        Map<String, String> data = new HashMap<>();
        data.put("loginUrl", url != null ? url : "");
        return R.ok(data);
    }

    private List<String> loadRolesForUser(SysUser sysUser) {
        if (sysUser == null) return List.of();
        return sysUserService.getRoles(sysUser.getId()).stream()
            .map(role -> "ROLE_" + role.getRoleCode())
            .collect(Collectors.toList());
    }

    /**
     * 查询认证模式配置 — 前端启动时调用，决定走 SSO 还是 Mock 登录。
     */
    @GetMapping("/config")
    public R<Map<String, Object>> authConfig() {
        boolean ssoEnabled = ssoAuthProvider != null && ssoAuthProvider.enabled();
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("ssoEnabled", ssoEnabled);
        config.put("loginUrl", ssoEnabled && ssoAuthProvider.getLoginUrl() != null
                ? ssoAuthProvider.getLoginUrl() : "");
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

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String authNo = userDetails.getUsername();
        SysUser sysUser = sysUserService.getByAuthNo(authNo);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("authNo", authNo);
        userInfo.put("realName", sysUser != null ? sysUser.getRealName() : "");
        userInfo.put("tellername", sysUser != null ? sysUser.getRealName() : "");
        userInfo.put("ad", sysUser != null ? sysUser.getAdAccount() : "");
        userInfo.put("branchId", sysUser != null ? sysUser.getBranchId() : "");
        userInfo.put("branchName", sysUser != null ? sysUser.getBranchName() : "");
        userInfo.put("notesId", sysUser != null ? sysUser.getNotesId() : "");
        userInfo.put("roles", roles);

        return R.ok(userInfo);
    }
}
