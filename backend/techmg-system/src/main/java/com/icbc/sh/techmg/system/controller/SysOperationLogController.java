package com.icbc.sh.techmg.system.controller;

import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.common.util.PageResult;
import com.icbc.sh.techmg.system.service.SysOperationLogService;
import com.icbc.sh.techmg.system.vo.SysOperationLogVO;
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
    public R<PageResult<SysOperationLogVO>> list(@RequestParam(name = "keyword", required = false) String keyword,
                                             @RequestParam(name = "module", required = false) String module,
                                             @RequestParam(name = "startTime", required = false) LocalDateTime startTime,
                                             @RequestParam(name = "endTime", required = false) LocalDateTime endTime,
                                             @RequestParam(name = "page", defaultValue = "1") int page,
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        return R.ok(PageResult.of(sysOperationLogService.queryPage(keyword, module, startTime, endTime, page, size)));
    }

    @Operation(summary = "根据ID查询操作日志详情")
    @GetMapping("/{id}")
    public R<SysOperationLogVO> getById(@PathVariable("id") Long id) {
        return R.ok(sysOperationLogService.getLogVO(id));
    }
}
