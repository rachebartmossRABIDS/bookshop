package cn.edu.hbpu.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidateUtilTest {
    @Test
    void validatesPhoneAndEmail() {
        assertTrue(ValidateUtil.isTel("13800000001"));
        assertFalse(ValidateUtil.isTel("12800000001"));
        assertFalse(ValidateUtil.isTel("1380000000"));
        assertTrue(ValidateUtil.isEmail("user@example.com"));
        assertFalse(ValidateUtil.isEmail("not-an-email"));
    }

    @Test
    void detectsBlankStrings() {
        assertTrue(ValidateUtil.isBlank(null));
        assertTrue(ValidateUtil.isBlank("   "));
        assertFalse(ValidateUtil.isBlank("ok"));
    }
}
