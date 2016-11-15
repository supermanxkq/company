package com.ccservice.b2b2c.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.callback.PropertyUtil;

/**
 * 加密
 * @author zyx
 * 2016年7月8日
 */
public class TrusteeshipUtil {

    private static final String DES = "DES";

    private static final String key = PropertyUtil.getValue("TrusteeshipdesKey", "Train.properties");

    /**
     * 解密
     */
    public static String decrypt(String data) {
        try {

            return new String(decrypt(Base64.decodeBase64(data.getBytes()), key.getBytes()), "UTF-8");
        }
        catch (Exception e) {
            return data;
        }
    }

    /**
     * 加密
     */
    public static String encrypt(String data) throws Exception {
        return new String(Base64.encodeBase64(encrypt(data.getBytes("UTF-8"), key.getBytes())));
    }

    /**
     * 加密
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        //生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        //从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        //创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        //Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        //用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        //返回
        return cipher.doFinal(data);
    }

    /**
     * 解密
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        //生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        //从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        //创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        //Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        //用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        //返回
        return cipher.doFinal(data);
    }

    public static void main(String[] args) {
        TrusteeshipUtil tru = new TrusteeshipUtil();
        try {
            System.out.println(tru.encrypt("asaa1223afffgvv"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(tru.decrypt("1Z5xOtf/J5Kb2TXQzpJM8A=="));
    }
}
