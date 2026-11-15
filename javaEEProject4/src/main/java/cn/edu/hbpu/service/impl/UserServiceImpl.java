package cn.edu.hbpu.service.impl;


import cn.edu.hbpu.mapper.UserMapper;
import cn.edu.hbpu.pojo.User;
import cn.edu.hbpu.service.UserService;
import cn.edu.hbpu.utils.Md5Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Override
    public User selectUser(User user) {
        return userMapper.selectUser(user);
    }
    @Override
    public User getInfo(Integer uid) {
        return userMapper.selectByPrimaryKey(Long.parseLong(uid+""));
    }

    @Override
    public int updatePassword(Integer uid, String oldPassword, String newPassword) {
        User user = userMapper.selectByPrimaryKey(uid.longValue());
        if (user == null) {
            return -1;
        }
        if (user.getPassword() == null || !Md5Util.checkPassword(oldPassword, user.getPassword())) {
            return 0;
        }
        User update = new User();
        update.setUid(uid);
        update.setPassword(Md5Util.getMD5String(newPassword));
        return userMapper.updateByPrimaryKeySelective(update);
    }
}
