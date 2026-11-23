package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.OperationLogMapper;
import cn.edu.hbpu.pojo.OperationLog;
import cn.edu.hbpu.service.OperationLogService;
import cn.edu.hbpu.utils.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {
    private final OperationLogMapper operationLogMapper;

    @Override
    public void log(String action, String detail) {
        OperationLog log = new OperationLog();
        log.setUid(UserContext.getUid());
        log.setAction(action);
        log.setDetail(detail != null && detail.length() > 1000 ? detail.substring(0, 1000) : detail);
        log.setTime(new Date());
        operationLogMapper.insert(log);
    }

    @Override
    public PageInfo<OperationLog> list(Integer pageNo, Integer pageSize) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("需要管理员权限");
        }
        PageHelper.startPage(pageNo, pageSize);
        List<OperationLog> logs = operationLogMapper.selectAll();
        return new PageInfo<>(logs);
    }
}
