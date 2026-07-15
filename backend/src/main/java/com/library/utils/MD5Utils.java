package com.library.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * MD5工具类
 */
public class MD5Utils {
    
    /**
     * MD5加密
     */
    public static String encrypt(String password) {
        return DigestUtil.md5Hex(password);
    }
    
    /**
     * 验证密码
     */
    public static boolean verify(String password, String encryptedPassword) {
        return encrypt(password).equals(encryptedPassword);
    }

    public static void main(String[] args) {
        String password = "student002";
        String encryptedPassword = encrypt(password);
        System.out.println(encryptedPassword);
    }
}