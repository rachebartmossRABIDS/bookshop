package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Favorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 针对表【favorite】的数据库操作 Mapper（纯注解）
 */
public interface FavoriteMapper {

    /**
     * 插入收藏；唯一键 uk_uid_bid 冲突时忽略（返回 0），避免重复收藏报错
     */
    @Insert("insert ignore into favorite(uid,bid,time) values(#{uid},#{bid},#{time})")
    int insertIgnore(Favorite favorite);

    @Delete("delete from favorite where uid=#{uid} and bid=#{bid}")
    int deleteByUidBid(@Param("uid") Integer uid, @Param("bid") Integer bid);

    @Select("select * from favorite where uid=#{uid} order by fid desc")
    @Results({
            @Result(column = "bid", property = "bid"),
            @Result(column = "bid", property = "book",
                    one = @One(select = "cn.edu.hbpu.mapper.BookMapper.getByBidPrimary"))
    })
    List<Favorite> selectByUid(Integer uid);

    @Select("select * from favorite where uid=#{uid} and bid=#{bid}")
    Favorite selectByUidBid(@Param("uid") Integer uid, @Param("bid") Integer bid);
}