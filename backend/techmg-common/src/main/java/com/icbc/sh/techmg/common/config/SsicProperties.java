package com.icbc.sh.techmg.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SSIC 统一认证配置属性。
 *
 * 对接行内 SSIC (统一认证平台) 时，在 application.yml 中配置 ssic.* 属性即可。
 * 对应行内配置项：
 *   ssicIp              → ssic.ip
 *   ssicserverkeyName   → ssic.server-key-name
 *   ssicversion         → ssic.version
 *   ssic.client.key.name → ssic.client-key-name
 *   client.site.url     → ssic.client-site-url
 *   enablessIc          → ssic.enabled
 *   ssic.smPublickey    → ssic.sm-public-key
 */
@Data
@Component
@ConfigurationProperties(prefix = "ssic")
public class SsicProperties {

    /** 是否开启统一认证 (true: 开启, false: 关闭) */
    private boolean enabled = false;

    /** 统一认证服务器地址，如 aam.icbc */
    private String ip = "aam.icbc";

    /** 统一认证公钥名称，应用会获得 SsIC:pub 这个公钥 */
    private String serverKeyName = "SsIC";

    /** 统一认证版本，如 SM2 */
    private String version = "SM2";

    /** 应用的密钥名称 */
    private String clientKeyName = "techmanageplatform";

    /** SM2 非对称密钥公钥（3.0非对称密钥，部署时填写） */
    private String smPublicKey = "";

    /**
     * 应用登录回调 URL。
     * 格式: http://ip:port/上下文根 或 http://域名:端口/上下文根
     * 登录完成后统一认证会回跳到此 URL 并带上 SSIAuth 参数。
     */
    private String clientSiteUrl = "http://localhost:5173/#/";

    /** AAM 查询用户信息接口 URL */
    private String aamQryTellerInfoUrl = "";

    /** AAM DSF IP 地址 */
    private String aamDsfIp = "";

    /**
     * 统一认证登录页基础 URL（含协议）。
     * 默认使用 ip 拼接 https://{ip}
     */
    public String getLoginPageUrl() {
        return "https://" + ip;
    }

    /**
     * 统一认证服务 URL（即后端 login 方法的访问地址，SSO 回调用）。
     */
    public String getServiceUrl() {
        return clientSiteUrl.replace("/#/", "") + "/api/auth/login";
    }
}
