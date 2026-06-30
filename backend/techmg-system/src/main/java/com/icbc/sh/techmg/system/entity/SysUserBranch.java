package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_branch")
public class SysUserBranch {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String branchId;
    private String branchName;
}
