package cn.edu.hbpu.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希工具：新密码一律 BCrypt；
 * 兼容历史 MD5 哈希（登录校验通过后由业务层自动升级为 BCrypt）。
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 校验密码：存储值以 $2 开头按 BCrypt 校验，否则按旧版 MD5 校验
     */
    public static boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        if (isBcrypt(storedHash)) {
            return ENCODER.matches(rawPassword, storedHash);
        }
        return Md5Util.checkPassword(rawPassword, storedHash);
    }

    public static boolean isBcrypt(String storedHash) {
        return storedHash != null && storedHash.startsWith("$2");
    }
}