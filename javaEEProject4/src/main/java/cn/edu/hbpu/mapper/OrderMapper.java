package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Order;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

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

    @Select("select * from `order` where uid=#{uid}")
    @Results({
            @Result(column = "bid",property = "book",
                    one = @One(select = "cn.edu.hbpu.mapper.BookMapper.getByBidPrimary"))
    })
    List<Order> selectByUid(Integer uid);

    @Select("select * from `order` where bid=#{bid} order by oid desc ")
    @Results({
            @Result(column = "uid",property = "user",
                    one = @One(select = "cn.edu.hbpu.mapper.UserMapper.selectByPrimaryKey"))
    })
    List<Order> selectByBid(Integer bid);

    List<Order> selectByContent(String content);
}
