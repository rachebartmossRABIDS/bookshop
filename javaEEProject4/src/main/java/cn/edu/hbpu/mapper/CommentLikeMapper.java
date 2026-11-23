package cn.edu.hbpu.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CommentLikeMapper {

    @Insert("insert ignore into comment_like(uid,cid,time) values(#{uid},#{cid},now())")
    int insertIgnore(@Param("uid") Integer uid, @Param("cid") Integer cid);

    @Delete("delete from comment_like where uid=#{uid} and cid=#{cid}")
    int delete(@Param("uid") Integer uid, @Param("cid") Integer cid);

    @Select("select count(*) from comment_like where cid=#{cid}")
    int countByCid(Integer cid);

    @Select("select count(*) from comment_like where uid=#{uid} and cid=#{cid}")
    int exists(@Param("uid") Integer uid, @Param("cid") Integer cid);

    @Delete("delete from comment_like where cid=#{cid}")
    int deleteByCid(Integer cid);
}
