package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Comment;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
* @author joker
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2024-12-17 01:33:08
* @Entity generator.pojo.Comment
*/
public interface CommentMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);

    @Select("select * from comment where uid=#{uid}")
    @Results({
            @Result(column = "bid",property = "book",
                    one = @One(select = "cn.edu.hbpu.mapper.BookMapper.getByBidPrimary"))
    })
    List<Comment> selectByUid(Integer uid);

    @Select("select * from comment where bid=#{bid} order by cid desc ")
    @Results({
            @Result(column = "uid",property = "user",
                    one = @One(select = "cn.edu.hbpu.mapper.UserMapper.selectByPrimaryKey"))
    })
    List<Comment> selectByBid(Integer bid);

    List<Comment> selectByContent(String content);
}
