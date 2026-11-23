package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.ResetCodeMapper;
import cn.edu.hbpu.mapper.UserMapper;
import cn.edu.hbpu.pojo.User;
import cn.edu.hbpu.pojo.ResetCode;
import cn.edu.hbpu.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {
    @Mock private ResetCodeMapper resetCodeMapper;
    @Mock private UserMapper userMapper;
    @Mock private MailService mailService;
    private PasswordResetServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PasswordResetServiceImpl(resetCodeMapper, userMapper, mailService);
    }

    @Test
    void rejectsExpiredCodeAndDeletesIt() {
        User user = new User();
        user.setUid(8);
        when(userMapper.selectByEmail("user@example.com")).thenReturn(user);
        ResetCode latest = new ResetCode();
        latest.setCode("123456");
        latest.setExpireTime(new Date(System.currentTimeMillis() - 1000));
        when(resetCodeMapper.selectLatest("user@example.com")).thenReturn(latest);

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.reset("user@example.com", "123456", "new-password"));
        assertTrue(error.getMessage().contains("过期"));
        verify(resetCodeMapper).deleteByEmail("user@example.com");
        verify(userMapper, never()).updateByPrimaryKeySelective(any(User.class));
    }

    @Test
    void rejectsUnknownEmailBeforeSending() {
        when(userMapper.selectByEmail("missing@example.com")).thenReturn(null);
        assertThrows(BusinessException.class, () -> service.sendCode("missing@example.com"));
        verifyNoInteractions(mailService);
    }
}
