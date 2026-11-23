package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Book;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
/**
* @author joker
* @description 针对表【book】的数据库操作Mapper
* @createDate 2024-12-17 01:33:08
* @Entity generator.pojo.Book
*/
public interface BookMapper {
    @Select("select bid,`name`,image,author,price,sid from book where kid=#{kid} order by bid desc ")
    List<Book> selectBykid(Integer kid);

    @Select("select bid,`name`,image,author,price,sid from book where sid=#{sid} order by bid desc ")
    List<Book> selectBySid(Integer sid);

    @Select("select bid,`name`,image,author,price from book where bid=#{bid}")
    Book getByBidPrimary(Integer bid);

    /**
     * 购物车用图书查询：额外携带库存/销量/上下架状态
     */
    @Select("select bid,`name`,image,author,price,inventory,volume,state,sid,kid from book where bid=#{bid}")
    Book getWithStock(Integer bid);

    /**
     * 按书名模糊搜索；sort 白名单排序（防注入）：
     * price_asc / price_desc / volume_desc，其余按 bid 倒序
     */
    @Select("<script>select bid,`name`,image,author,price,sid,kid,inventory,volume,state from book "
            + "where name like concat('%',#{name},'%') order by "
            + "<choose>"
            + "<when test=\"sort == 'price_asc'\">price asc, bid desc</when>"
            + "<when test=\"sort == 'price_desc'\">price desc, bid desc</when>"
            + "<when test=\"sort == 'volume_desc'\">volume desc, bid desc</when>"
            + "<otherwise>bid desc</otherwise>"
            + "</choose></script>")
    List<Book> selectByName(@Param("name") String name, @Param("sort") String sort);

    /**
     * 原子扣减库存：库存不足时不更新，返回 0（inventory 为 INT 字段）
     */
    @Update("update book set inventory = inventory - #{num} where bid = #{bid} and inventory >= #{num}")
    int deductInventory(@Param("bid") Integer bid, @Param("num") Integer num);

    /**
     * 取消订单时回补库存
     */
    @Update("update book set inventory = inventory + #{num} where bid = #{bid}")
    int restoreInventory(@Param("bid") Integer bid, @Param("num") Integer num);

    /**
     * 付款成功后增加销量
     */
    @Update("update book set volume = volume + #{num} where bid = #{bid}")
    int addVolume(@Param("bid") Integer bid, @Param("num") Integer num);

    /**
     * 已付款订单取消时回滚销量（不低于 0）
     */
    @Update("update book set volume = GREATEST(volume - #{num}, 0) where bid = #{bid}")
    int subtractVolume(@Param("bid") Integer bid, @Param("num") Integer num);

    int deleteByPrimaryKey(Long id);

    int insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);

}
