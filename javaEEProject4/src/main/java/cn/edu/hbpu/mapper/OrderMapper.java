package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Order;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author joker
* @description 针对表【order】的数据库操作Mapper
* @createDate 2024-12-17 01:33:08
* @Entity generator.pojo.Order
*/
public interface OrderMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    @Select("select * from `order` where uid=#{uid} order by oid desc")
    @Results({
            @Result(column = "uid", property = "uid"),
            @Result(column = "bid", property = "bid"),
            @Result(column = "bid", property = "book",
                    one = @One(select = "cn.edu.hbpu.mapper.BookMapper.getByBidPrimary"))
    })
    List<Order> selectByUid(Integer uid);

    /**
     * 管理端全量订单（嵌套图书与买家公开信息）
     */
    @Select("select * from `order` order by oid desc")
    @Results({
            @Result(column = "uid", property = "uid"),
            @Result(column = "bid", property = "bid"),
            @Result(column = "bid", property = "book",
                    one = @One(select = "cn.edu.hbpu.mapper.BookMapper.getByBidPrimary")),
            @Result(column = "uid", property = "user",
                    one = @One(select = "cn.edu.hbpu.mapper.UserMapper.selectPublicByPrimaryKey"))
    })
    List<Order> selectAll();

    /**
     * 原子状态流转：仅当订单处于 from 状态时更新为 to，返回受影响行数。
     * 返回 0 表示状态已被其它请求改变（并发保护）。
     */
    @Update("update `order` set state = #{to} where oid = #{oid} and state = #{from}")
    int updateState(@Param("oid") Integer oid, @Param("from") String from, @Param("to") String to);

    @Update("update `order` set state='2',ship_time=now(),"
            + "logistics_company=#{company},logistics_no=#{no} "
            + "where oid=#{oid} and state='1'")
    int ship(@Param("oid") Integer oid, @Param("company") String company, @Param("no") String no);

    List<Order> selectByContent(String content);
}
