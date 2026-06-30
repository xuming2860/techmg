package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysOperationLog;

import java.time.LocalDateTime;

public interface SysOperationLogService extends IService<SysOperationLog> {

    IPage<SysOperationLog> queryPage(String keyword, String module,
                                     LocalDateTime startTime, LocalDateTime endTime,
                                     int page, int size);
}
