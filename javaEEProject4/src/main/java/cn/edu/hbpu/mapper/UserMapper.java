package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.User;
import org.apache.ibatis.annotations.Select;
/**
* @author joker
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-12-17 01:33:08
* @Entity generator.pojo.User
*/
public interface UserMapper {
    @Select("select uid,username,password,image from user where username=#{username}")
    User selectUser(User user);

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

}
