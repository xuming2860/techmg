package com.icbc.sh.techmg.framework.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSIC 统一认证用户信息。
 * 对应行内 SDK 中的 SSIcUser 对象。
 *
 * <pre>
 * 对应行内代码:
 *   Credentials cred = (Credentials) req.getAttribute(SSI_CREDENTIALS);
 *   ssicUser = cred.getSsIcUser();
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsicUser {

    /** 统一认证号（9位 userId） */
    private String userId;

    /** 用户中文名 */
    private String tellerName;

    /** 用户 AD 账号 */
    private String ad;

    /** 用户所在机构号 */
    private String branchId;

    /** 用户所在机构全名 */
    private String branchName;

    /** 用户 Notes ID */
    private String notesId;

    /** 用户多机构列表 */
    private String branchIdList;
}
