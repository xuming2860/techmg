package com.icbc.sh.techmg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mock 登录配置 — ssic.enabled=false 时生效。
 * 直接返回配置的固定用户信息 + JWT，跳过密码验证和外部 API 调用。
 */
@Data
@Component
@ConfigurationProperties(prefix = "login.mock")
public class LoginMockProperties {

    /** 统一认证号 */
    private String authNo = "admin";

    /** 用户中文名（对应 SSIC TELLERNAME） */
    private String tellerName = "平台管理员";

    /** 机构号 */
    private String branchId = "";

    /** 机构全名 */
    private String branchName = "";

    /** Notes ID */
    private String notesId = "";

    /** AD 账号 */
    private String adAccount = "";

    /** 角色列表（如 ROLE_PLATFORM_ADMIN） */
    private List<String> roles = List.of("ROLE_PLATFORM_ADMIN");
}
