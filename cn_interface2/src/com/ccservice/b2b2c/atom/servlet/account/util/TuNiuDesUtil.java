package com.ccservice.b2b2c.atom.servlet.account.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;
import org.apache.commons.codec.binary.Base64;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 途牛托管验证账号和常旅专用DES工具类
 * @author WH
 * @time 2016年4月11日 下午3:38:54
 * @version 1.0
 */

public class TuNiuDesUtil {

    private static final String DES = "DES";

    private static final String key = PropertyUtil.getValue("tuniu.desKey", "Train.GuestAccount.properties");

    /**
     * 途牛托管验证账号和常旅专用>>解密
     */
    public static String decrypt(String data) {
        try {
            //途牛专用Base64方法，双方保持一致
            return new String(decrypt(Base64.decodeBase64(data), key.getBytes()), "UTF-8");
        }
        catch (Exception e) {
            return data;//加密未上线时>>密文存在问题，解密失败，直接返回data
        }
    }

    /**
     * 途牛托管验证账号和常旅专用>>加密
     */
    public static String encrypt(String data) throws Exception {
        //途牛专用Base64方法，双方保持一致
        return Base64.encodeBase64URLSafeString(encrypt(data.getBytes("UTF-8"), key.getBytes()));
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

}