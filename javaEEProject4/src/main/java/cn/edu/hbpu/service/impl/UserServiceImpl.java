package cn.edu.hbpu.service.impl;


import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.UserMapper;
import cn.edu.hbpu.pojo.User;
import cn.edu.hbpu.service.UserService;
import cn.edu.hbpu.service.OperationLogService;
import cn.edu.hbpu.utils.PasswordUtil;
import cn.edu.hbpu.utils.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final OperationLogService operationLogService;
    @Override
    public User selectUser(User user) {
        return userMapper.selectUser(user);
    }
    @Override
    public User getInfo(Integer uid) {
        // 对外接口不返回密码哈希
        return userMapper.selectPublicByPrimaryKey(uid.longValue());
    }

    @Override
    public int updatePassword(Integer uid, String oldPassword, String newPassword) {
        User user = userMapper.selectByPrimaryKey(uid.longValue());
        if (user == null) {
            return -1;
        }
        if (user.getPassword() == null || !PasswordUtil.matches(oldPassword, user.getPassword())) {
            return 0;
        }
        User update = new User();
        update.setUid(uid);
        update.setPassword(PasswordUtil.encode(newPassword));
        int result = userMapper.updateByPrimaryKeySelective(update);
        if (result > 0) {
            userMapper.incrementTokenVersion(uid);
        }
        return result;
    }

    @Override
    public int register(User user) {
        User query = new User();
        query.setUsername(user.getUsername());
        if (userMapper.selectUser(query) != null) {
            return -1;
        }
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        user.setType(1);
        user.setState(0);
        user.setReputation("5");
        user.setImage("");
        return userMapper.insertSelective(user);
    }

    @Override
    public User updateInfo(Integer uid, String tel, String email) {
        requireUser(uid);
        User update = new User();
        update.setUid(uid);
        // 空字符串表示清空该字段（selective 更新只跳过 null）
        update.setTel(tel == null ? "" : tel.trim());
        update.setEmail(email == null ? "" : email.trim());
        userMapper.updateByPrimaryKeySelective(update);
        return userMapper.selectPublicByPrimaryKey(uid.longValue());
    }

    @Override
    public User updateAvatar(Integer uid, String image) {
        requireUser(uid);
        User update = new User();
        update.setUid(uid);
        update.setImage(image);
        userMapper.updateByPrimaryKeySelective(update);
        return userMapper.selectPublicByPrimaryKey(uid.longValue());
    }

    @Override
    public PageInfo<User> listAll(Integer pageNo, Integer pageSize) {
        requireAdmin();
        PageHelper.startPage(pageNo, pageSize);
        List<User> users = userMapper.selectAll();
        return new PageInfo<>(users);
    }

    @Override
    public int setState(Integer uid, Integer state) {
        requireAdmin();
        if (uid == null || state == null || (state != 0 && state != 1)) {
            throw new BusinessException("参数错误：state 只能为 0（正常）或 1（封禁）");
        }
        if (uid.equals(UserContext.getUid())) {
            throw new BusinessException("不能修改自己的账号状态");
        }
        requireUser(uid);
        User update = new User();
        update.setUid(uid);
        update.setState(state);
        int result = userMapper.updateByPrimaryKeySelective(update);
        if (result > 0) {
            userMapper.incrementTokenVersion(uid);
            operationLogService.log("用户状态变更", "用户 #" + uid + " -> " + (state == 1 ? "封禁" : "正常"));
        }
        return result;
    }

    @Override
    public void rehashPassword(Integer uid, String rawPassword) {
        User update = new User();
        update.setUid(uid);
        update.setPassword(PasswordUtil.encode(rawPassword));
        userMapper.updateByPrimaryKeySelective(update);
    }

    private void requireUser(Integer uid) {
        if (uid == null || userMapper.selectPublicByPrimaryKey(uid.longValue()) == null) {
            throw new BusinessException("用户不存在");
        }
    }

    private void requireAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("需要管理员权限");
        }
    }
}
