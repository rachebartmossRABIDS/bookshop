package cn.edu.hbpu.service;

public interface MailService {
    void sendResetCode(String email, String code);
}
