package com.ccservice.b2b2c.atom.servlet.account;

import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @time 2015年10月21日 下午8:15:48
 */
public class DesUtil {
    private final static String DES = "DES";

    public static void main(String[] args) throws Exception {
        String data="LU3QL-qzsKblqREK1Yab_q8B0fVZ97Nlbr8JkvkRramUVDJYJpaXsohKUnFZeYoylnH_iIFpIqrMxkDOqDKKv3KtywAxSVV4lnH_iIFpIqpzsAtNk7CbhOFPhjcl39LxITujWbv7GgHA6svYwvrBQ_BkMdO7GjyuM36g61bzRekK5kPQh_p_mZGqw91vsog6JAVlq1BMblFTtKakam2kmoumTWyI5HsW11V8HmzwonqmBUmxHVT5SP2hKVBK4gRiwpAirlIslBibVQG93Syi-wxHXWxpinFFM9OnvrQWw4kQTA7Cx280cXkcczp919mhIvaUA45d_TztJL0qsPTtQiXnSZTDLj2HqDO96_INuBhyxU-9UM-gD7TtedG6elmi97IQfviO2ZXp6zJmAV3aGulEg6R7gc7Krl5XD2DmqCef2IyXvCKeRrwHt7YhYu-PB0yYExRJo7yKUy1ilaz4ct_q4-5JA16JgcRXQSAbTASFXfQjxKhzc7_a3RNc8mGMwqG7lV87-nK_nASne-0yGVALl-KHBCxGCZQw_KYCAFwtjK2c1GFXOEOQTNWCl9fTXWCtMc6cCFmoPzGCpX-YQxBd2ivybzRkwg_hxjGEoDYJvShKqmn5QIXaU9q1j-fkXWCtMc6cCFl5sfeR03v6AOQihcq6I8UKofiO16E3LV-GIoVBWtlKxGaj_sR8QegL3_1WIxdYK36vjZs9vEWLCRvh_ap1VZUHi25pNE4qbU27xp6TqoCn-gVujRT7rK2Zj29NpTa7WR3mkhNe6LqnPgfZdPiI2q8h7f7Mv2n6LgYBt3DEhXvKXGOaGJVwwy4dCcNhs7XuLUqG8a1I_qZO-7zb9-3KxhVVIOla-zvunOqqk1iYrcKeXk6m1coGGbiur42bPbxFiwnMMNrpycBJiDO7aN0eSZtkWfwEJECJc6vRbv0IYnqzVbEsJBZIyulMMDtzKQbf7Dd-SQNy41GhWmOaGJVwwy4diyq6d5gujBqd8fQLBF8-bFn8BCRAiXOr0W79CGJ6s1WxLCQWSMrpTJbGwKQk-JbtwOrL2ML6wUOtH5kn73XhlvB6TXPo3W_nrl5XD2DmqCfyxHwISvwLvA97puepZRMqsNsbGmXxogloX6jaYmdJWIHEV0EgG0wEpnKCaF21PmthaYfRcGpQgBgvjdw2DxPsOxBs5JlGJQQ";
        String base64 = new String(new BASE64Decoder().decodeBuffer(data), "utf-8");
        String desKey="v66r9ogtcvtxv3v4xq3gog8fqdbhwmt0";
        String jiemi = DesUtil.decrypt(base64, desKey);
        System.out.println(jiemi);
        
        
        String jiami = DesUtil.encrypt("administrator", "A1B2C3D4E5F60708");
        System.out.println(jiami);
        System.out.println(DesUtil.decrypt("jlQCvSld4d078ZJNVqw91w==", "A1B2C3D4E5F60708"));
    }

    /**
     * Description 根据键值进行加密
     * @param data 
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws IOException, Exception {
        try {
            if (data == null)
                return null;
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] buf = decoder.decodeBuffer(data);
            byte[] bt = decrypt(buf, key.getBytes());
            return new String(bt);
        }
        catch (Exception e) {
            return data;
        }

    }

    /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }
}
