package com.icbc.sh.techmg.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.system.entity.SysOperationLog;
import com.icbc.sh.techmg.system.service.SysOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/api/system/operation-log")
@RequiredArgsConstructor
public class SysOperationLogController {

    private final SysOperationLogService sysOperationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/list")
    public R<IPage<SysOperationLog>> list(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String module,
                                           @RequestParam(required = false) LocalDateTime startTime,
                                           @RequestParam(required = false) LocalDateTime endTime,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return R.ok(sysOperationLogService.queryPage(keyword, module, startTime, endTime, page, size));
    }

    @Operation(summary = "根据ID查询操作日志详情")
    @GetMapping("/{id}")
    public R<SysOperationLog> getById(@PathVariable("id") Long id) {
        SysOperationLog log = sysOperationLogService.getById(id);
        if (log == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "操作日志不存在");
        }
        return R.ok(log);
    }
}
