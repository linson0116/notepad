package com.example.linson.notepad;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by linson on 2017/3/8.
 */

public class EncryptUtils {
    //算法/模式/填充
    public static final String TRANSFORMATION_AES = "AES/CBC/PKCS5Padding";
    public static final String TRANSFORMATION_DES = "DES/CBC/PKCS5Padding";
    //算法
    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_DES = "DES";

    //    private static String key16 = "1234567890123456";
    private static String key16 = "linson0116@163.c";

    public static String encryptAESString(String input) {
        return encrypt(input, key16, TRANSFORMATION_AES, ALGORITHM_AES);
    }

    public static String decryptAESString(String input) {
        return decrypt(input, key16, TRANSFORMATION_AES, ALGORITHM_AES);
    }

    public static String encrypt(String input, String key, String transformation, String algorithm) {
        byte[] enResult = null;
        try {
            Cipher cipher = Cipher.getInstance(transformation);

            SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);

            IvParameterSpec iv = new IvParameterSpec(key.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            enResult = cipher.doFinal(input.getBytes());
        } catch (Exception e) {

        }
        byte[] encode_result = Base64.encode(enResult, Base64.DEFAULT);
        return new String(encode_result);
    }

    public static String decrypt(String input, String key, String transformation, String algorithm) {
        byte[] deResult = null;
        try {
            Cipher cipher = Cipher.getInstance(transformation);

            SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);

            IvParameterSpec iv = new IvParameterSpec(key.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            deResult = cipher.doFinal(Base64.decode(input, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(deResult);
    }


}
