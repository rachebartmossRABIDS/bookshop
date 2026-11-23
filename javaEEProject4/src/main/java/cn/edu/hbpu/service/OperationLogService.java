package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.OperationLog;
import com.github.pagehelper.PageInfo;

public interface OperationLogService {
    void log(String action, String detail);
    PageInfo<OperationLog> list(Integer pageNo, Integer pageSize);
}
