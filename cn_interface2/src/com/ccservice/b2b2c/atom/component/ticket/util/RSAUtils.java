package com.ccservice.b2b2c.atom.component.ticket.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.apache.commons.lang.time.DateFormatUtils;

/**
 * RSA算法加密/解密工具类。
 * 
 * @author fuchun
 * @version 1.0.0, 2010-05-05
 */
public abstract class RSAUtils {
    public static void main(String[] args) {
        //        String plaintext = "nicaicai123";
        String plaintext = "13522543333";

        String hexModulus = "0098471b9a05c816ee949b4fe93520a8681a14e65d7a0501221136951a52a3b76cf9e2375e45aca1ad6fc9f00b401ece966a1f8fb521dd9de4215c90b7e9cd77b1c7d2f6e9b7aba6f94322d7375cbb321be653826d921030b6ef9fd453a7ece0ae4785a6166dd5d1560f3992cbad493201bb18616251610890bd0ea6736c346e15";
        String hexPublicExponent = "010001";
        RSAPublicKey rsapublickey = getRSAPublidKey(hexModulus, hexPublicExponent);
        //        RSAPublicKey rsapublickey = getPublicKey(hexModulus, hexPublicExponent);
        //模
        String modulus = rsapublickey.getModulus().toString();
        //公钥指数
        String public_exponent = rsapublickey.getPublicExponent().toString();

        String plaintext_public = encryptString(rsapublickey, plaintext);
        //                String plaintext_public = decryptStringByJs(plaintext);
        System.out.println("==plaintext_public=======");
        System.out.println(plaintext_public);
        plaintext_public = StringUtils.reverse(plaintext_public);
        //        System.out.println("==plaintext_public2=======");
        //        System.out.println(plaintext_public);
    }

    /**  
     * 使用模和指数生成RSA公钥  
     *   
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
            KeyFactory keyFactory = KeyFactory.getInstance("RSA",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void jsMethod(String plaintext, String hexModulus, String hexPublicExponent) {
        char[] a = plaintext.toCharArray();
        int sl = a.length;
        int al = a.length;
        String result = "";
        BigInt block;
        int j;
        int k;
        BigInt m = biFromHex(hexModulus);
        BigInt e = biFromHex(hexPublicExponent);
        int chunkSize = 2 * biHighIndex(m);
        for (int i = 0; i < al; i += chunkSize) {
            block = new BigInt();
            j = 0;
            for (k = i; k < i + chunkSize; ++j) {
                block.digits[j] = (int) a[k++];
                block.digits[j] += a[k++] << 8;
            }
            //            var crypt = powMod(block, e);
            //            var text = key.radix == 16 ? RSAUtils.biToHex(crypt) : RSAUtils.biToString(crypt, key.radix);
            //            result += text + " ";
        }
        //        return result.substring(0, result.length - 1); // Remove last space.
    }

    //    public static BigInt powMod(BigInt block, BigInt e) {
    //        BigInt result = new BigInt();
    //        result.digits[0] = 1;
    //        BigInt a = x;
    //        BigInt k = y;
    //        while (true) {
    //            if ((k.digits[0] & 1) != 0)
    //                result = multiplyMod(result, a);
    //            k = biShiftRight(k, 1);
    //            if (k.digits[0] == 0 && biHighIndex(k) == 0)
    //                break;
    //            a = this.multiplyMod(a, a);
    //        }
    //        return result;
    //    }

    //    public static BigInt multiplyMod(BigInt x, BigInt y) {
    //        BigInt xy = biMultiply(x, y);
    //        return modulo(xy);
    //    }

    //    public static BigInt biModulo(BigInt x, BigInt y) {
    //        return biDivideModulo(x, y)[1];
    //    }

    //    public static BigInt biDivideModulo(BigInt x, BigInt y) {
    //        var nb = RSAUtils.biNumBits(x);
    //        var tb = RSAUtils.biNumBits(y);
    //        var origYIsNeg = y.isNeg;
    //        var q, r;
    //        if (nb < tb) {
    //            // |x| < |y|
    //            if (x.isNeg) {
    //                q = RSAUtils.biCopy(bigOne);
    //                q.isNeg = !y.isNeg;
    //                x.isNeg = false;
    //                y.isNeg = false;
    //                r = biSubtract(y, x);
    //                // Restore signs, 'cause they're references.
    //                x.isNeg = true;
    //                y.isNeg = origYIsNeg;
    //            } else {
    //                q = new BigInt();
    //                r = RSAUtils.biCopy(x);
    //            }
    //            return [q, r];
    //        }
    //
    //        q = new BigInt();
    //        r = x;
    //
    //        // Normalize Y.
    //        var t = Math.ceil(tb / bitsPerDigit) - 1;
    //        var lambda = 0;
    //        while (y.digits[t] < biHalfRadix) {
    //            y = RSAUtils.biShiftLeft(y, 1);
    //            ++lambda;
    //            ++tb;
    //            t = Math.ceil(tb / bitsPerDigit) - 1;
    //        }
    //        // Shift r over to keep the quotient constant. We'll shift the
    //        // remainder back at the end.
    //        r = RSAUtils.biShiftLeft(r, lambda);
    //        nb += lambda; // Update the bit count for x.
    //        var n = Math.ceil(nb / bitsPerDigit) - 1;
    //
    //        var b = RSAUtils.biMultiplyByRadixPower(y, n - t);
    //        while (RSAUtils.biCompare(r, b) != -1) {
    //            ++q.digits[n - t];
    //            r = RSAUtils.biSubtract(r, b);
    //        }
    //        for (var i = n; i > t; --i) {
    //        var ri = (i >= r.digits.length) ? 0 : r.digits[i];
    //        var ri1 = (i - 1 >= r.digits.length) ? 0 : r.digits[i - 1];
    //        var ri2 = (i - 2 >= r.digits.length) ? 0 : r.digits[i - 2];
    //        var yt = (t >= y.digits.length) ? 0 : y.digits[t];
    //        var yt1 = (t - 1 >= y.digits.length) ? 0 : y.digits[t - 1];
    //            if (ri == yt) {
    //                q.digits[i - t - 1] = maxDigitVal;
    //            } else {
    //                q.digits[i - t - 1] = Math.floor((ri * biRadix + ri1) / yt);
    //            }
    //
    //            var c1 = q.digits[i - t - 1] * ((yt * biRadix) + yt1);
    //            var c2 = (ri * biRadixSquared) + ((ri1 * biRadix) + ri2);
    //            while (c1 > c2) {
    //                --q.digits[i - t - 1];
    //                c1 = q.digits[i - t - 1] * ((yt * biRadix) | yt1);
    //                c2 = (ri * biRadix * biRadix) + ((ri1 * biRadix) + ri2);
    //            }
    //
    //            b = RSAUtils.biMultiplyByRadixPower(y, i - t - 1);
    //            r = RSAUtils.biSubtract(r, RSAUtils.biMultiplyDigit(b, q.digits[i - t - 1]));
    //            if (r.isNeg) {
    //                r = RSAUtils.biAdd(r, b);
    //                --q.digits[i - t - 1];
    //            }
    //        }
    //        r = RSAUtils.biShiftRight(r, lambda);
    //        // Fiddle with the signs and stuff to make sure that 0 <= r < y.
    //        q.isNeg = x.isNeg != origYIsNeg;
    //        if (x.isNeg) {
    //            if (origYIsNeg) {
    //                q = RSAUtils.biAdd(q, bigOne);
    //            } else {
    //                q = RSAUtils.biSubtract(q, bigOne);
    //            }
    //            y = RSAUtils.biShiftRight(y, lambda);
    //            r = RSAUtils.biSubtract(y, r);
    //        }
    //        // Check for the unbelievably stupid degenerate case of r == -0.
    //        if (r.digits[0] == 0 && RSAUtils.biHighIndex(r) == 0) r.isNeg = false;
    //
    //        return [q, r];
    //    }

    public static BigInt biMultiply(BigInt x, BigInt y) {
        BigInt result = new BigInt();
        int c;
        int n = biHighIndex(x);
        int t = biHighIndex(y);
        BigInt u;
        int uv;
        int k;
        int biRadix = 1 << 16; // = 2^16 = 65536
        int maxDigitVal = biRadix - 1;
        int biRadixBase = 2;
        int biRadixBits = 16;
        for (int i = 0; i <= t; ++i) {
            c = 0;
            k = i;
            for (int j = 0; j <= n; ++j, ++k) {
                uv = result.digits[k] + x.digits[j] * y.digits[i] + c;
                result.digits[k] = uv & maxDigitVal;
                c = uv >>> biRadixBits;
                //c = Math.floor(uv / biRadix);
            }
            result.digits[i + n + 1] = c;
        }
        result.isNeg = x.isNeg != y.isNeg;
        return result;
    };

    public static int charToHex(int c) {
        int ZERO = 48;
        int NINE = ZERO + 9;
        int littleA = 97;
        int littleZ = littleA + 25;
        int bigA = 65;
        int bigZ = 65 + 25;
        int result;

        if (c >= ZERO && c <= NINE) {
            result = c - ZERO;
        }
        else if (c >= bigA && c <= bigZ) {
            result = 10 + c - bigA;
        }
        else if (c >= littleA && c <= littleZ) {
            result = 10 + c - littleA;
        }
        else {
            result = 0;
        }
        return result;
    }

    public static int hexToDigit(String s) {
        int result = 0;
        int sl = Math.min(s.length(), 4);
        for (int i = 0; i < sl; ++i) {
            result <<= 4;
            result |= charToHex(s.charAt(i));
        }
        return result;
    }

    public static BigInt biFromHex(String s) {
        BigInt result = new BigInt();
        int sl = s.length();
        for (int i = sl, j = 0; i > 0; i -= 4, ++j) {
            result.digits[j] = hexToDigit(s.substring(Math.max(i - 4, 0), Math.min(i, 4)));
        }
        return result;
    }

    public static int biHighIndex(BigInt x) {
        int result = x.digits.length - 1;
        while (result > 0 && x.digits[result] == 0)
            --result;
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RSAUtils.class);

    /** 算法名称 */
    private static final String ALGORITHOM = "RSA";

    /**保存生成的密钥对的文件名称。 */
    private static final String RSA_PAIR_FILENAME = "/__RSA_PAIR.txt";

    /** 密钥大小 */
    private static final int KEY_SIZE = 1024;

    /** 默认的安全服务提供者 */
    private static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();

    //    private static KeyPairGenerator keyPairGen = null;

    private static KeyFactory keyFactory = null;

    /** 缓存的密钥对。 */
    private static KeyPair oneKeyPair = null;

    private static File rsaPairFile = null;

    static {
        try {
            //            keyPairGen = KeyPairGenerator.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        }
        catch (Exception ex) {
            //            ex.printStackTrace();
        }
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHOM, new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
        catch (NoSuchAlgorithmException e) {
            //            e.printStackTrace();
        }
        rsaPairFile = new File(getRSAPairFilePath());
    }

    private RSAUtils() {
    }

    /**
     * 生成并返回RSA密钥对。
     */
    private static synchronized KeyPair generateKeyPair() {
        try {
            //  key = RSAUtils.getKeyPair("010001","","0098471b9a05c816ee949b4fe93520a8681a14e65d7a0501221136951a52a3b76cf9e2375e45aca1ad6fc9f00b401ece966a1f8fb521dd9de4215c90b7e9cd77b1c7d2f6e9b7aba6f94322d7375cbb321be653826d921030b6ef9fd453a7ece0ae4785a6166dd5d1560f3992cbad493201bb18616251610890bd0ea6736c346e15"); 
            System.out.println(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd"));
            SecureRandom securerandom = new SecureRandom(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd")
                    .getBytes());
            //            keyPairGen.initialize(KEY_SIZE, securerandom);

            //            oneKeyPair = keyPairGen.generateKeyPair();
            //            saveKeyPair(oneKeyPair);

            return oneKeyPair;
        }
        catch (InvalidParameterException ex) {
            LOGGER.error("KeyPairGenerator does not support a key length of " + KEY_SIZE + ".", ex);
        }
        catch (NullPointerException ex) {
            ex.printStackTrace();
            LOGGER.error("RSAUtils#KEY_PAIR_GEN is null, can not generate KeyPairGenerator instance.", ex);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("", ex);
        }
        return null;
    }

    /**
     * 返回生成/读取的密钥对文件的路径。
     */
    private static String getRSAPairFilePath() {
        String urlPath = RSAUtils.class.getResource("/").getPath();
        return (new File(urlPath).getParent() + RSA_PAIR_FILENAME);
    }

    /**
     * 若需要创建新的密钥对文件，则返回 {@code true}，否则 {@code false}。
     */
    private static boolean isCreateKeyPairFile() {
        // 是否创建新的密钥对文件
        boolean createNewKeyPair = false;
        if (!rsaPairFile.exists() || rsaPairFile.isDirectory()) {
            createNewKeyPair = true;
        }
        return createNewKeyPair;
    }

    /**
     * 将指定的RSA密钥对以文件形式保存。
     * 
     * @param keyPair 要保存的密钥对。
     */
    private static void saveKeyPair(KeyPair keyPair) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = FileUtils.openOutputStream(rsaPairFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(keyPair);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * 返回RSA密钥对。
     */
    public static KeyPair getKeyPair() {
        // 首先判断是否需要重新生成新的密钥对文件
        if (isCreateKeyPairFile()) {
            // 直接强制生成密钥对文件，并存入缓存。
            return generateKeyPair();
        }
        if (oneKeyPair != null) {
            return oneKeyPair;
        }
        return readKeyPair();
    }

    // 同步读出保存的密钥对
    private static KeyPair readKeyPair() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = FileUtils.openInputStream(rsaPairFile);
            ois = new ObjectInputStream(fis);
            oneKeyPair = (KeyPair) ois.readObject();
            return oneKeyPair;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(fis);
        }
        return null;
    }

    /**
     * 根据给定的系数和专用指数构造一个RSA专用的公钥对象。
     * 
     * @param modulus 系数。
     * @param publicExponent 专用指数。
     * @return RSA专用公钥对象。
     */
    public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) {
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
        try {
            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        }
        catch (InvalidKeySpecException ex) {
            LOGGER.error("RSAPublicKeySpec is unavailable.", ex);
        }
        catch (NullPointerException ex) {
            LOGGER.error("RSAUtils#KEY_FACTORY is null, can not generate KeyFactory instance.", ex);
        }
        return null;
    }

    /**
     * 根据给定的系数和专用指数构造一个RSA专用的私钥对象。
     * 
     * @param modulus 系数。
     * @param privateExponent 专用指数。
     * @return RSA专用私钥对象。
     */
    public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent) {
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(
                privateExponent));
        try {
            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        }
        catch (InvalidKeySpecException ex) {
            LOGGER.error("RSAPrivateKeySpec is unavailable.", ex);
        }
        catch (NullPointerException ex) {
            LOGGER.error("RSAUtils#KEY_FACTORY is null, can not generate KeyFactory instance.", ex);
        }
        return null;
    }

    /**
     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的私钥对象。
     * 
     * @param modulus 系数。
     * @param privateExponent 专用指数。
     * @return RSA专用私钥对象。
     */
    public static RSAPrivateKey getRSAPrivateKey(String hexModulus, String hexPrivateExponent) {
        if (StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPrivateExponent)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("hexModulus and hexPrivateExponent cannot be empty. RSAPrivateKey value is null to return.");
            }
            return null;
        }
        byte[] modulus = null;
        byte[] privateExponent = null;
        try {
            modulus = Hex.decodeHex(hexModulus.toCharArray());
            privateExponent = Hex.decodeHex(hexPrivateExponent.toCharArray());
        }
        catch (DecoderException ex) {
            LOGGER.error("hexModulus or hexPrivateExponent value is invalid. return null(RSAPrivateKey).");
        }
        if (modulus != null && privateExponent != null) {
            return generateRSAPrivateKey(modulus, privateExponent);
        }
        return null;
    }

    /**
     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象。
     * 
     * @param modulus 系数。
     * @param publicExponent 专用指数。
     * @return RSA专用公钥对象。
     */
    public static RSAPublicKey getRSAPublidKey(String hexModulus, String hexPublicExponent) {
        if (StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPublicExponent)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("hexModulus and hexPublicExponent cannot be empty. return null(RSAPublicKey).");
            }
            return null;
        }
        byte[] modulus = null;
        byte[] publicExponent = null;
        try {
            modulus = Hex.decodeHex(hexModulus.toCharArray());
            publicExponent = Hex.decodeHex(hexPublicExponent.toCharArray());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("hexModulus or hexPublicExponent value is invalid. return null(RSAPublicKey).");
        }
        if (modulus != null && publicExponent != null) {
            return generateRSAPublicKey(modulus, publicExponent);
        }
        return null;
    }

    /**
     * 使用指定的公钥加密数据。
     * 
     * @param publicKey 给定的公钥。
     * @param data 要加密的数据。
     * @return 加密后的数据。
     */
    public static byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
        //        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        Cipher ci = Cipher.getInstance(ALGORITHOM, new BouncyCastleProvider());
        ci.init(Cipher.ENCRYPT_MODE, publicKey);
        return ci.doFinal(data);
    }

    /**
     * 使用指定的私钥解密数据。
     * 
     * @param privateKey 给定的私钥。
     * @param data 要解密的数据。
     * @return 原数据。
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.DECRYPT_MODE, privateKey);
        return ci.doFinal(data);
    }

    /**
     * 使用给定的公钥加密给定的字符串。
     * <p />
     * 若 {@code publicKey} 为 {@code null}，或者 {@code plaintext} 为 {@code null} 则返回 {@code
     * null}。
     * 
     * @param publicKey 给定的公钥。
     * @param plaintext 字符串。
     * @return 给定字符串的密文。
     */
    public static String encryptString(PublicKey publicKey, String plaintext) {
        if (publicKey == null || plaintext == null) {
            return null;
        }
        byte[] data = plaintext.getBytes();
        try {
            byte[] en_data = encrypt(publicKey, data);
            return new String(Hex.encodeHex(en_data));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getCause().getMessage());
        }
        return null;
    }

    /**
     * 使用默认的公钥加密给定的字符串。
     * <p />
     * 若{@code plaintext} 为 {@code null} 则返回 {@code null}。
     * 
     * @param plaintext 字符串。
     * @return 给定字符串的密文。
     */
    public static String encryptString(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        byte[] data = plaintext.getBytes();
        KeyPair keyPair = getKeyPair();
        try {
            byte[] en_data = encrypt((RSAPublicKey) keyPair.getPublic(), data);
            return new String(Hex.encodeHex(en_data));
        }
        catch (NullPointerException ex) {
            LOGGER.error("keyPair cannot be null.");
        }
        catch (Exception ex) {
            LOGGER.error(ex.getCause().getMessage());
        }
        return null;
    }

    /**
     * 使用给定的私钥解密给定的字符串。
     * <p />
     * 若私钥为 {@code null}，或者 {@code encrypttext} 为 {@code null}或空字符串则返回 {@code null}。
     * 私钥不匹配时，返回 {@code null}。
     * 
     * @param privateKey 给定的私钥。
     * @param encrypttext 密文。
     * @return 原文字符串。
     */
    public static String decryptString(PrivateKey privateKey, String encrypttext) {
        if (privateKey == null || StringUtils.isBlank(encrypttext)) {
            return null;
        }
        try {
            byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
            byte[] data = decrypt(privateKey, en_data);
            return new String(data);
        }
        catch (Exception ex) {
            LOGGER.error(String.format("\"%s\" Decryption failed. Cause: %s", encrypttext, ex.getCause().getMessage()));
        }
        return null;
    }

    /**
     * 使用默认的私钥解密给定的字符串。
     * <p />
     * 若{@code encrypttext} 为 {@code null}或空字符串则返回 {@code null}。
     * 私钥不匹配时，返回 {@code null}。
     * 
     * @param encrypttext 密文。
     * @return 原文字符串。
     */
    public static String decryptString(String encrypttext) {
        if (StringUtils.isBlank(encrypttext)) {
            return null;
        }
        KeyPair keyPair = getKeyPair();
        try {
            byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
            byte[] data = decrypt((RSAPrivateKey) keyPair.getPrivate(), en_data);
            return new String(data);
        }
        catch (NullPointerException ex) {
            LOGGER.error("keyPair cannot be null.");
        }
        catch (Exception ex) {
            LOGGER.error(String.format("\"%s\" Decryption failed. Cause: %s", encrypttext, ex.getMessage()));
        }
        return null;
    }

    /**
     * 使用默认的私钥解密由JS加密（使用此类提供的公钥加密）的字符串。
     * 
     * @param encrypttext 密文。
     * @return {@code encrypttext} 的原文字符串。
     */
    public static String decryptStringByJs(String encrypttext) {
        String text = decryptString(encrypttext);
        if (text == null) {
            return null;
        }
        return StringUtils.reverse(text);
    }

    /** 返回已初始化的默认的公钥。*/
    public static RSAPublicKey getDefaultPublicKey() {
        KeyPair keyPair = getKeyPair();
        if (keyPair != null) {
            return (RSAPublicKey) keyPair.getPublic();
        }
        return null;
    }

    /** 返回已初始化的默认的私钥。*/
    public static RSAPrivateKey getDefaultPrivateKey() {
        KeyPair keyPair = getKeyPair();
        if (keyPair != null) {
            return (RSAPrivateKey) keyPair.getPrivate();
        }
        return null;
    }
}