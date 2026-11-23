package cn.edu.hbpu.utils;

import java.util.regex.Pattern;

/**
 * 常用输入格式校验（服务端统一使用，不信任前端校验）。
 */
public class ValidateUtil {

    /** 中国大陆 11 位手机号 */
    private static final Pattern TEL = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    public static boolean isTel(String tel) {
        return tel != null && TEL.matcher(tel).matches();
    }

    public static boolean isEmail(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}