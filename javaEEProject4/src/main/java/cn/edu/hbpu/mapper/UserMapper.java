package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.User;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
/**
* @author joker
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-12-17 01:33:08
* @Entity generator.pojo.User
*/
public interface UserMapper {
    @Select("select uid,username,password,image,type,state,token_version tokenVersion "
            + "from user where username=#{username}")
    User selectUser(User user);

    /**
     * 对外返回的用户信息（不含密码哈希），用于个人信息接口及评论/订单的嵌套用户查询
     */
    @Select("select uid,username,tel,email,type,image,reputation,state,token_version tokenVersion "
            + "from user where uid=#{uid}")
    User selectPublicByPrimaryKey(Long uid);

    /**
     * 管理端全量用户列表（不含密码哈希）
     */
    @Select("select uid,username,tel,email,type,image,reputation,state from user order by uid")
    List<User> selectAll();

    @Select("select token_version from user where uid=#{uid}")
    Integer selectTokenVersion(Integer uid);

    @Select("select state from user where uid=#{uid}")
    Integer selectState(Integer uid);

    @Update("update user set token_version=token_version+1 where uid=#{uid}")
    int incrementTokenVersion(Integer uid);

    @Select("select uid,username,password,tel,email,type,image,reputation,state,"
            + "token_version tokenVersion from user where email=#{email} limit 1")
    User selectByEmail(String email);

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

}
