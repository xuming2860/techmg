package com.icbc.sh.techmg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mock 登录配置 — ssic.enabled=false 时生效。
 */
@Data
@Component
@ConfigurationProperties(prefix = "login.mock")
public class LoginMockProperties {

    /** 统一认证号（12位数字） */
    private String userId = "000000000001";

    /** 用户中文名 */
    private String username = "平台管理员";

    /** 机构号 */
    private String branchId = "12092342";

    /** 机构全名 */
    private String branchName = "上海技术部";

    /** 邮箱（Notes ID） */
    private String notesId = "admin@sdc.com";

    /** 角色列表 */
    private List<String> roles = List.of("ROLE_PLATFORM_ADMIN");
}