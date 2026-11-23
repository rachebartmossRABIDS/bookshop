package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OperationLogMapper {

    @Insert("insert into operation_log(uid,action,detail,time) "
            + "values(#{uid},#{action},#{detail},#{time})")
    int insert(OperationLog log);

    @Select("select id,uid,action,detail,time from operation_log order by id desc")
    List<OperationLog> selectAll();
}
