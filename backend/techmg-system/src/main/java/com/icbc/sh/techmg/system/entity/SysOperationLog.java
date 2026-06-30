package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String module;

    private String operation;

    private String operator;

    private String requestParam;

    private String responseResult;

    private String ip;

    private Long costTime;

    private Integer status;

    private LocalDateTime createTime;
}
