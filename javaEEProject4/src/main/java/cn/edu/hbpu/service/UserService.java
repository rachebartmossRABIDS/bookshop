package cn.edu.hbpu.service;


import cn.edu.hbpu.pojo.User;

public interface UserService {
    User selectUser(User user);

    User getInfo(Integer uid);

    int updatePassword(Integer uid, String oldPassword, String newPassword);
    
}