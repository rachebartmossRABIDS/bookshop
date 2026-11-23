package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.ResetCodeMapper;
import cn.edu.hbpu.mapper.UserMapper;
import cn.edu.hbpu.pojo.User;
import cn.edu.hbpu.pojo.ResetCode;
import cn.edu.hbpu.service.MailService;
import cn.edu.hbpu.service.PasswordResetService;
import cn.edu.hbpu.utils.PasswordUtil;
import cn.edu.hbpu.utils.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final ResetCodeMapper resetCodeMapper;
    private final UserMapper userMapper;
    private final MailService mailService;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public void sendCode(String email) {
        String normalized = normalizeEmail(email);
        if (userMapper.selectByEmail(normalized) == null) {
            throw new BusinessException("该邮箱尚未注册");
        }
        ResetCode latest = resetCodeMapper.selectLatest(normalized);
        if (latest != null && latest.getTime() != null) {
            Date sentAt = latest.getTime();
            if (System.currentTimeMillis() - sentAt.getTime() < 60_000L) {
                throw new BusinessException("验证码发送过于频繁，请 60 秒后重试");
            }
        }
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        resetCodeMapper.deleteByEmail(normalized);
        resetCodeMapper.insert(normalized, code);
        mailService.sendResetCode(normalized, code);
    }

    @Override
    @Transactional
    public void reset(String email, String code, String newPassword) {
        String normalized = normalizeEmail(email);
        User user = userMapper.selectByEmail(normalized);
        if (user == null) {
            throw new BusinessException("该邮箱尚未注册");
        }
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 64) {
            throw new BusinessException("新密码长度需为 6-64 位");
        }
        ResetCode latest = resetCodeMapper.selectLatest(normalized);
        if (latest == null || latest.getCode() == null || !latest.getCode().equals(code)) {
            throw new BusinessException("验证码错误");
        }
        Date expire = latest.getExpireTime();
        if (expire == null || !expire.after(new Date())) {
            resetCodeMapper.deleteByEmail(normalized);
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (resetCodeMapper.consume(normalized, code) != 1) {
            throw new BusinessException("验证码已失效，请重新获取");
        }
        User update = new User();
        update.setUid(user.getUid());
        update.setPassword(PasswordUtil.encode(newPassword));
        if (userMapper.updateByPrimaryKeySelective(update) != 1) {
            throw new BusinessException("重置密码失败");
        }
        userMapper.incrementTokenVersion(user.getUid());
    }

    private String normalizeEmail(String email) {
        String normalized = email == null ? "" : email.trim();
        if (!ValidateUtil.isEmail(normalized)) {
            throw new BusinessException("邮箱格式不正确");
        }
        return normalized;
    }
}
