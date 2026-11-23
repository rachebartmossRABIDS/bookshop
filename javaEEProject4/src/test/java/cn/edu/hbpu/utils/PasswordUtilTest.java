package cn.edu.hbpu.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test
    void bcryptRoundTrip() {
        String hash = PasswordUtil.encode("123456");
        assertTrue(PasswordUtil.isBcrypt(hash));
        assertTrue(PasswordUtil.matches("123456", hash));
        assertFalse(PasswordUtil.matches("wrong", hash));
    }

    @Test
    void acceptsLegacyMd5() {
        String md5 = "e10adc3949ba59abbe56e057f20f883e";
        assertTrue(PasswordUtil.matches("123456", md5));
        assertFalse(PasswordUtil.isBcrypt(md5));
    }
}
