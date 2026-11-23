package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Notification;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface NotificationMapper {

    @Insert("insert into notification(uid,title,content,is_read,time) "
            + "values(#{uid},#{title},#{content},0,#{time})")
    @Options(useGeneratedKeys = true, keyProperty = "nid")
    int insert(Notification notification);

    @Select("select nid,uid,title,content,is_read isRead,time from notification "
            + "where uid=#{uid} order by nid desc")
    List<Notification> selectByUid(Integer uid);

    @Select("select count(*) from notification where uid=#{uid} and is_read=0")
    int countUnread(Integer uid);

    @Update("update notification set is_read=1 where nid=#{nid} and uid=#{uid}")
    int markRead(@Param("nid") Integer nid, @Param("uid") Integer uid);
}
