package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Book;
import org.apache.ibatis.annotations.Select;
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

    @Select("select bid,`name`,image,author,price from book where name like concat('%',#{name},'%') order by bid desc")
    List<Book> selectByName(String name);
    int deleteByPrimaryKey(Long id);

    int insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);

}
