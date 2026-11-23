package cn.edu.hbpu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

public interface OrderRequestMapper {

    @Insert("insert ignore into order_request(request_id,uid,result_json,time) "
            + "values(#{requestId},#{uid},null,now())")
    int tryInsert(@Param("requestId") String requestId, @Param("uid") Integer uid);

    @Select("select result_json from order_request where request_id=#{requestId} and uid=#{uid}")
    String selectResult(@Param("requestId") String requestId, @Param("uid") Integer uid);

    @Select("select result_json from order_request where request_id=#{requestId} and uid=#{uid} for update")
    String selectResultForUpdate(@Param("requestId") String requestId, @Param("uid") Integer uid);

    @Update("update order_request set result_json=#{resultJson} "
            + "where request_id=#{requestId} and uid=#{uid}")
    int saveResult(@Param("requestId") String requestId, @Param("uid") Integer uid,
                   @Param("resultJson") String resultJson);
}
