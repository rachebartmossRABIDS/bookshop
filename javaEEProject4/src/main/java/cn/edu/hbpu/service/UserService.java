package cn.edu.hbpu.service;


import cn.edu.hbpu.pojo.User;
import com.github.pagehelper.PageInfo;

public interface UserService {
    User selectUser(User user);

    User getInfo(Integer uid);

    int updatePassword(Integer uid, String oldPassword, String newPassword);

    /**
     * 注册新用户，用户名已存在返回 -1，成功返回影响行数
     */
    int register(User user);

    /** 修改本人联系方式（tel/email），返回更新后的公开信息 */
    User updateInfo(Integer uid, String tel, String email);

    /** 更新本人头像文件名，返回更新后的公开信息 */
    User updateAvatar(Integer uid, String image);

    /** 管理员分页查询全部用户（不含密码哈希） */
    PageInfo<User> listAll(Integer pageNo, Integer pageSize);

    /** 管理员封禁/解封用户：state 0 正常 / 1 封禁 */
    int setState(Integer uid, Integer state);

    /** 登录成功后将旧 MD5 哈希升级为 BCrypt */
    void rehashPassword(Integer uid, String rawPassword);
}