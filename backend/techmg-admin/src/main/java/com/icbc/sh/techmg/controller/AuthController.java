package com.icbc.sh.techmg.controller;

import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.framework.log.ApiAccessLog;
import com.icbc.sh.techmg.framework.security.JwtTokenProvider;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserService sysUserService;

    @ApiAccessLog
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String authNo = loginRequest.get("authNo");
        String password = loginRequest.get("password");

        // Spring Security 通过 BCryptPasswordEncoder 自动验证密码
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authNo, password));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authNo, userDetails.getAuthorities());

        SysUser sysUser = sysUserService.getByAuthNo(authNo);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authNo", authNo);
        userInfo.put("realName", sysUser != null ? sysUser.getRealName() : "");
        userInfo.put("roles", roles);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);

        return R.ok(result);
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

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authNo", authNo);
        userInfo.put("realName", sysUser != null ? sysUser.getRealName() : "");
        userInfo.put("roles", roles);

        return R.ok(userInfo);
    }
}
