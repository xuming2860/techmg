package com.icbc.sh.techmg.framework.security;

import com.icbc.sh.techmg.common.config.SsicProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SSIC 统一认证提供者实现。
 * 仅在 ssic.enabled=true 时激活。
 *
 * <h3>内网部署指引</h3>
 * 部署到内网后，替换 TODO 标记的部分为实际的 SSIC SDK 调用:
 * <ol>
 *   <li>引入 SSIC SDK 依赖（ServerSideAuth / EncryptUtils 等）</li>
 *   <li>authenticate(): 替换为 serverSideAuth.execute() + Credentials 提取</li>
 *   <li>queryUserInfo(): 替换为真实的 queryUserInfoAAM() 调用</li>
 * </ol>
 *
 * @see SsoAuthProvider 接口定义
 * @see SsicProperties 配置属性
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "ssic.enabled", havingValue = "true")
@RequiredArgsConstructor
public class SsoAuthProviderImpl implements SsoAuthProvider {

    private final SsicProperties ssicProperties;

    // ==================== authenticate ====================

    @Override
    public SsicUser authenticate(HttpServletRequest req, HttpServletResponse resp,
                                  String ssiAuth, String ssiSign) {
        // ================================================================
        // TODO: [内网适配] 替换为行内 SSIC SDK 调用:
        //
        //   String url = ssicProperties.getServiceUrl();
        //   serverSideAuth.setServiceURL(url);
        //   serverSideAuth.setOperation(SIGN_IN);
        //   if (serverSideAuth.execute(req, resp, ssiAuth, ssiSign)) {
        //       Credentials cred = (Credentials) req.getAttribute(SSI_CREDENTIALS);
        //       SSIcUser ssicUser = cred.getSsIcUser();
        //       return SsicUser.builder()
        //           .userId(ssicUser.getUserId())
        //           .tellerName(ssicUser.getTellerName())
        //           .ad(ssicUser.getAd())
        //           .branchId(ssicUser.getBranchId())
        //           .branchName(ssicUser.getBranchName())
        //           .notesId(ssicUser.getNotesId())
        //           .build();
        //   }
        //   return null;
        // ================================================================

        log.warn("[SSIC] authenticate using MOCK — ssiAuth: {}, ssiSign: {}", ssiAuth, ssiSign);

        // Mock: treat ssiAuth as userId for local dev/test
        if (ssiAuth != null && !ssiAuth.isBlank()) {
            return SsicUser.builder()
                    .userId(ssiAuth)
                    .username("Mock用户")
                    .branchId("12092342")
                    .branchName("上海技术部")
                    .notesId(ssiAuth + "@sdc.com")
                    .build();
        }
        return null;
    }

    @Override
    public boolean enabled() {
        return ssicProperties.isEnabled();
    }

    // ==================== SSO 登录页 URL ====================

    @Override
    public String getLoginRedirectUrl(String serviceUrl) {
        // ================================================================
        // TODO: [内网适配] 拼接行内 SSO 登录页 URL:
        //   https://{ssicIp}/userService/login/aam2/tmvp?service={serviceUrl}
        // 其中 {ssicIp} 是 ssicProperties.getIp()，service 是回调地址
        // ================================================================

        String ssoUrl = String.format("%s/userService/login/aam2/tmvp?service=%s",
                ssicProperties.getLoginPageUrl(), serviceUrl);
        log.info("[SSIC] SSO login redirect URL: {}", ssoUrl);
        return ssoUrl;
    }

    @Override
    public String getLogoutUrl() {
        // TODO: [内网适配] 返回 SSO 登出地址
        return ssicProperties.getLoginPageUrl() + "/logout";
    }

    // ==================== queryUserInfo ====================

    @Override
    public Map<String, Object> queryUserInfo(String userId) {
        // ================================================================
        // TODO: [内网适配] 替换为行内 AAM 查询用户信息调用:
        //
        //   HashMap<String, String> paramMap = new HashMap<>();
        //   paramMap.put("serviceName", ssicProperties.getClientKeyName());
        //   paramMap.put("clientId", clientId);
        //   paramMap.put("QUERYINFO", userId);
        //   paramMap.put("QUERYFLAG", "1");
        //   paramMap.put("IGNORESTATUS", "1");
        //
        //   List<String> resultList = EncryptUtils.genEncryptParamJson(paramMap, partnerPubKey);
        //   String url = ResourceUtil.getConfigHoveVariable("aam.qryTellerInfo.url", "ip", aamDsfIp);
        //
        //   HttpHeaders headers = new HttpHeaders();
        //   headers.add("x-request-app", "F-BDSP-DWP");
        //   JsonArray jsonArray = new JsonArray();
        //   jsonArray.add(JsonParser.parseString(resultList.get(1)).getAsJsonObject());
        //
        //   String resultStr = HttpClientUtil.sendPostRequestByParams4String(url, headers, jsonArray);
        //   JsonObject resJsonObj = JsonParser.parseString(resultStr).getAsJsonObject();
        //
        //   if (resJsonObj.has("data") && resJsonObj.get("return_code").getAsString().equals("0")) {
        //       String decryptData = EncryptUtils.decrypt(
        //           resJsonObj.get("data").getAsString(), resultList.get(0));
        //       JsonObject tellerObj = JsonParser.parseString(decryptData).getAsJsonObject();
        //
        //       Map<String, Object> userInfo = new LinkedHashMap<>();
        //       userInfo.put("authNo", userId);
        //       userInfo.put("tellername", tellerObj.get("TELLERNAME").getAsString());
        //       userInfo.put("ad", tellerObj.get("AD").getAsString());
        //       userInfo.put("branchId", tellerObj.get("BRANCHID").getAsString());
        //       userInfo.put("branchName", tellerObj.get("BRANCHNAME").getAsString());
        //       userInfo.put("notesId", tellerObj.get("NOTESID").getAsString());
        //       return userInfo;
        //   }
        //   return null;
        // ================================================================

        log.info("[SSIC] queryUserInfo using MOCK data for userId: {}", userId);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("username", "徐敏");
        userInfo.put("branchId", "12092342");
        userInfo.put("branchName", "上海技术部");
        userInfo.put("notesId", userId + "@sdc.com");
                Map.of("branchId", "12092342", "branchName", "上海技术部")
        ));

        return userInfo;
    }
}
