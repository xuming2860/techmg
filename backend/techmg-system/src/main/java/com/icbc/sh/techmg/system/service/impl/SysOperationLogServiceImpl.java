package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysOperationLog;
import com.icbc.sh.techmg.system.mapper.SysOperationLogMapper;
import com.icbc.sh.techmg.system.service.SysOperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog>
        implements SysOperationLogService {

    @Override
    public IPage<SysOperationLog> queryPage(String keyword, String module,
                                            LocalDateTime startTime, LocalDateTime endTime,
                                            int page, int size) {
        Page<SysOperationLog> p = new Page<>(page, size);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(SysOperationLog::getModule, keyword)
                    .or()
                    .like(SysOperationLog::getOperation, keyword)
                    .or()
                    .like(SysOperationLog::getOperator, keyword));
        }
        if (StringUtils.hasText(module)) {
            wrapper.eq(SysOperationLog::getModule, module);
        }
        if (startTime != null) {
            wrapper.ge(SysOperationLog::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysOperationLog::getCreateTime, endTime);
        }

        wrapper.orderByDesc(SysOperationLog::getCreateTime);
        return this.page(p, wrapper);
    }
}
