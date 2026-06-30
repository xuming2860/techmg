package com.icbc.sh.techmg.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OperationLogEvent extends ApplicationEvent {

    private final String module;
    private final String operation;
    private final String operator;
    private final String requestParam;
    private final String responseResult;
    private final String ip;
    private final Long costTime;
    private final Integer status;

    public OperationLogEvent(Object source, String module, String operation, String operator,
                             String requestParam, String responseResult, String ip,
                             Long costTime, Integer status) {
        super(source);
        this.module = module;
        this.operation = operation;
        this.operator = operator;
        this.requestParam = requestParam;
        this.responseResult = responseResult;
        this.ip = ip;
        this.costTime = costTime;
        this.status = status;
    }
}
