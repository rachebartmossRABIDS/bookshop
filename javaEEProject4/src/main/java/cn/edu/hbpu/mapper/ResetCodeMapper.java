package cn.edu.hbpu.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.edu.hbpu.pojo.ResetCode;

public interface ResetCodeMapper {

    @Delete("delete from reset_code where email=#{email}")
    int deleteByEmail(String email);

    @Insert("insert into reset_code(email,code,expire_time,time) "
            + "values(#{email},#{code},date_add(now(), interval 10 minute),now())")
    int insert(@Param("email") String email, @Param("code") String code);

    @Select("select email,code,expire_time as expireTime,time from reset_code "
            + "where email=#{email} order by rid desc limit 1")
    ResetCode selectLatest(String email);

    @Delete("delete from reset_code where email=#{email} and code=#{code}")
    int consume(@Param("email") String email, @Param("code") String code);
}
