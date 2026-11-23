package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Cart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 针对表【cart】的数据库操作 Mapper（纯注解）
 */
public interface CartMapper {

    @Insert("insert into cart(uid,bid,num,time) values(#{uid},#{bid},#{num},#{time})")
    int insert(Cart cart);

    @Update("update cart set num=#{num} where cid=#{cid}")
    int updateNum(@Param("cid") Integer cid, @Param("num") Integer num);

    @Delete("delete from cart where cid=#{cid}")
    int deleteByCid(Integer cid);

    @Delete("delete from cart where uid=#{uid}")
    int deleteByUid(Integer uid);

    @Select("select * from cart where uid=#{uid} order by cid desc")
    @Results({
            @Result(column = "bid", property = "bid"),
            @Result(column = "bid", property = "book",
                    one = @One(select = "cn.edu.hbpu.mapper.BookMapper.getWithStock"))
    })
    List<Cart> selectByUid(Integer uid);

    @Select("select * from cart where uid=#{uid} and bid=#{bid}")
    Cart selectByUidBid(@Param("uid") Integer uid, @Param("bid") Integer bid);

    @Select("select * from cart where cid=#{cid}")
    Cart selectByCid(Integer cid);
}