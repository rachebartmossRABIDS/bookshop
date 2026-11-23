package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {
    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    @Value("${mail.enabled:false}")
    private boolean enabled;
    @Value("${mail.host:smtp.example.com}")
    private String host;
    @Value("${mail.port:587}")
    private int port;
    @Value("${mail.username:}")
    private String username;
    @Value("${mail.password:}")
    private String password;
    @Value("${mail.from:no-reply@bookshop.local}")
    private String from;
    @Value("${mail.starttls:true}")
    private boolean starttls;

    @Override
    public void sendResetCode(String email, String code) {
        if (!enabled) {
            log.info("开发模式找回密码验证码：email={}, code={}", email, code);
            return;
        }
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setDefaultEncoding("UTF-8");
        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", String.valueOf(username != null && !username.isEmpty()));
        props.put("mail.smtp.starttls.enable", String.valueOf(starttls));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Bookshop 找回密码验证码");
        message.setText("您的验证码为 " + code + "，10 分钟内有效。如非本人操作请忽略。");
        try {
            sender.send(message);
        } catch (Exception e) {
            log.error("找回密码邮件发送失败", e);
            throw new BusinessException("验证码邮件发送失败，请稍后重试");
        }
    }
}
