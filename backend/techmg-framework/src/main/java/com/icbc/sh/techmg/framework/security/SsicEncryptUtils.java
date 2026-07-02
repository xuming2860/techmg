package com.icbc.sh.techmg.framework.security;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * SSIC 加解密工具适配器。
 *
 * 对应行内 SDK 中的 EncryptUtils，提供 SM2/SM4 加解密能力。
 * 部署到内网后，引入行内加密 SDK 并替换 TODO 部分。
 *
 * <h3>使用示例（内网）</h3>
 * <pre>
 *   HashMap&lt;String, String&gt; paramMap = new HashMap&lt;&gt;();
 *   paramMap.put("serviceName", ssicClientKeyName);
 *   paramMap.put("clientId", clientId);
 *   paramMap.put("QUERYINFO", username);
 *   // ...
 *   List&lt;String&gt; resultList = EncryptUtils.genEncryptParamJson(paramMap, partnerPubKey);
 *   // resultList.get(0) = 解密密钥
 *   // resultList.get(1) = 加密后的请求体 JSON
 * </pre>
 */
@Slf4j
public final class SsicEncryptUtils {

    private SsicEncryptUtils() {}

    /**
     * 生成加密请求参数。
     * 对应行内: {@code EncryptUtils.genEncryptParamJson(paramMap, partnerPubKey)}
     *
     * @param paramMap     请求参数 map
     * @param partnerPubKey 合作方公钥 (SSIC SM2 公钥)
     * @return [0]=decryptKey(解密用), [1]=encryptedJson(加密后的请求体)
     */
    public static List<String> genEncryptParamJson(Map<String, String> paramMap, String partnerPubKey) {
        // ================================================================
        // TODO: [内网适配] 替换为行内 SM2 加密:
        //   return EncryptUtils.genEncryptParamJson(paramMap, partnerPubKey);
        // ================================================================
        log.warn("[SSIC] genEncryptParamJson using MOCK — params: {}", paramMap.keySet());
        // Mock: 返回明文 JSON（仅供本地开发测试）
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, String> e : paramMap.entrySet()) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
            i++;
        }
        sb.append("}");
        String mockJson = sb.toString();
        return Arrays.asList("mock-decrypt-key", mockJson);
    }

    /**
     * 解密响应数据。
     * 对应行内: {@code EncryptUtils.decrypt(data, decryptKey)}
     *
     * @param encryptedData 加密的响应数据
     * @param decryptKey    解密密钥 (来自 genEncryptParamJson 返回的 [0])
     * @return 解密后的 JSON 字符串
     */
    public static String decrypt(String encryptedData, String decryptKey) {
        // ================================================================
        // TODO: [内网适配] 替换为行内 SM4 解密:
        //   return EncryptUtils.decrypt(encryptedData, decryptKey);
        // ================================================================
        log.warn("[SSIC] decrypt using MOCK — returning plain data");
        return encryptedData;
    }
}
