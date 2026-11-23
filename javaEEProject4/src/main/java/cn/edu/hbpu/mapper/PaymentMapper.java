package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface PaymentMapper {

    @Insert("insert into payment(oid,channel,amount,status,trade_no,time) "
            + "values(#{oid},#{channel},#{amount},#{status},#{tradeNo},#{time})")
    @Options(useGeneratedKeys = true, keyProperty = "pid")
    int insert(Payment payment);

    @Select("select pid,oid,channel,amount,status,trade_no tradeNo,time from payment where oid=#{oid}")
    Payment selectByOid(Integer oid);

    @Update("update payment set status='REFUNDED' where oid=#{oid} and status='PAID'")
    int markRefunded(Integer oid);
}
