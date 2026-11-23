package cn.edu.hbpu.service;

public interface PasswordResetService {
    void sendCode(String email);
    void reset(String email, String code, String newPassword);
}
