package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.exception.BusinessException;
import com.icbc.sh.techmg.system.entity.SysOperationLog;
import com.icbc.sh.techmg.system.mapper.SysOperationLogMapper;
import com.icbc.sh.techmg.system.service.SysOperationLogService;
import com.icbc.sh.techmg.system.vo.SysOperationLogVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog>
        implements SysOperationLogService {

    @Override
    public IPage<SysOperationLogVO> queryPage(String keyword, String module,
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
        IPage<SysOperationLog> entityPage = this.page(p, wrapper);
        return entityPage.convert(this::toVO);
    }

    @Override
    public SysOperationLogVO getLogVO(Long id) {
        SysOperationLog entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "操作日志不存在");
        }
        return toVO(entity);
    }

    private SysOperationLogVO toVO(SysOperationLog entity) {
        SysOperationLogVO vo = new SysOperationLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
