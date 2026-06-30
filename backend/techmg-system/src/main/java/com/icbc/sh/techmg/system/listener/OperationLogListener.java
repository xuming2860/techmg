package com.icbc.sh.techmg.system.listener;

import com.icbc.sh.techmg.common.event.OperationLogEvent;
import com.icbc.sh.techmg.system.entity.SysOperationLog;
import com.icbc.sh.techmg.system.service.SysOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogListener {

    private final SysOperationLogService sysOperationLogService;

    @EventListener
    public void handleOperationLogEvent(OperationLogEvent event) {
        try {
            SysOperationLog log = new SysOperationLog();
            log.setModule(event.getModule());
            log.setOperation(event.getOperation());
            log.setOperator(event.getOperator());
            log.setRequestParam(event.getRequestParam());
            log.setResponseResult(event.getResponseResult());
            log.setIp(event.getIp());
            log.setCostTime(event.getCostTime());
            log.setStatus(event.getStatus());
            sysOperationLogService.save(log);
        } catch (Exception e) {
            log.error("Failed to save operation log: {}", e.getMessage(), e);
        }
    }
}
