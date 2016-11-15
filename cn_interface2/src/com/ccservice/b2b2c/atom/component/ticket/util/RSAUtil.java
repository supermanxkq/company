package com.ccservice.b2b2c.atom.component.ticket.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RSAUtil {

    public static void main(String[] args) throws Exception {
        RSAUtil rsa = new RSAUtil();
        String modeHex = "BB83CB428FF41B61762FB8A34041EF8B897301DC4DB84CBD60FEBFC244260B7CC3D57925591970028BB466E13C5A51650213DB7566A78453EA55D725A9B78884A99FD8B1530499D08F3D8CF078CDA4346395CCCA8379E814559E3F6A7DB851C9FCA1FC5A0D3983C637E33B02DA65DF139428C19D9EE2AD8F9A15663E984B166B";
        String messageg = "06111111FFFFFFFFA1D93F0C1C682F1836B7E97CC0A8D37363237EA630383632323235383030303030303030303030303120202020202020202036323232353830303030303030303030303032202020202020202020303531323234363536343836352020343231303333202020202020202020202020202020202020202020";
        String exponentHex = "010001";
        KeyFactory factory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        BigInteger n = new BigInteger(modeHex, 16);
        BigInteger e = new BigInteger(exponentHex, 16);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);
        RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
        //        byte[] mi = rsa.encryptByPublicKey(messageg, pub);//(messageg, pub);
        //        String result1 = new String(Hex.encodeHex(mi));
        String ssss = rsa.encryptByPublicKey(messageg, pub).toLowerCase();//(messageg, pub);;
        System.out.println("加密后==" + ssss);
    }

    public static void main1(String[] args) throws Exception {
        // TODO Auto-generated method stub
        HashMap<String, Object> map = RSAUtil.getKeys();
        //生成公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");

        //模
        //        String modulus = publicKey.getModulus().toString();
        String modulus = "0098471b9a05c816ee949b4fe93520a8681a14e65d7a0501221136951a52a3b76cf9e2375e45aca1ad6fc9f00b401ece966a1f8fb521dd9de4215c90b7e9cd77b1c7d2f6e9b7aba6f94322d7375cbb321be653826d921030b6ef9fd453a7ece0ae4785a6166dd5d1560f3992cbad493201bb18616251610890bd0ea6736c346e15";
        modulus = getmodulus(modulus);
        System.out.println("=modules=============");
        System.out.println(modulus);
        //公钥指数
        String public_exponent = publicKey.getPublicExponent().toString();
        //        String public_exponent = "010001";
        //私钥指数
        //        String private_exponent = privateKey.getPrivateExponent().toString();
        //明文
        String ming = "13522543333";
        //使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSAUtil.getPublicKey(modulus, public_exponent);
        //        RSAPrivateKey priKey = RSAUtil.getPrivateKey(modulus, private_exponent);
        //加密后的密文
        String mi = RSAUtil.encryptByPublicKey(ming, pubKey);
        System.err.println("=mi=============");
        System.err.println(mi);
        //解密后的明文
        //        ming = RSAUtil.decryptByPrivateKey(mi, priKey);
        //        System.err.println("=ming=============");
        //        System.err.println(ming);
    }

    private static String getmodulus(String modulus) {
        String result = "";
        for (int i = 0; i < modulus.length(); i++) {
            String s_temp = modulus.charAt(i) + "";
            try {
                Integer i_temp = Integer.parseInt(s_temp);
                if (i_temp >= 0 && i_temp < 10) {
                    s_temp = i_temp + "";
                }
            }
            catch (Exception e) {
                s_temp = (int) modulus.charAt(i) + "";
            }
            result += "" + s_temp;
        }
        return result;
    }

    /**
    * 生成公钥和私钥
    * @throws NoSuchAlgorithmException
    *
    */
    public static HashMap<String, Object> getKeys() throws NoSuchAlgorithmException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        map.put("public", publicKey);
        map.put("private", privateKey);
        return map;
    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus
     *            模
     * @param exponent
     *            指数
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus
     *            模
     * @param exponent
     *            指数
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 模长
        int key_len = publicKey.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //模长
        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        System.err.println(bcd.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }

    /**
     * ASCII码转BCD码
     *
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    /**
     * BCD转字符串
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            }
            else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     *拆分数组
     */
    public static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            }
            else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }
}
