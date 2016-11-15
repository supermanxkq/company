package com.yeepay.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

// ����hmacУ��ǩ����
public class DigestUtil {
    private static String encodingCharset = "UTF-8";

    private static String beforeChangeCode = "iso-8859-1";

    private static String afterChangeCode = "gbk";

    private static String hmac_CONNECT_Flag = "";

    public static void main(String[] args) {

        String value = "";
        String key = "";
        System.out.print(DigestUtil.hmacSign(value, key));
    }

    // �������hmacʱ��Ҫ��sbold
    public static String getHmacSBOld(String[] HmacOrder, Map map) {
        return getHmacSBOld(HmacOrder, map, hmac_CONNECT_Flag);
    }

    // �������hmacʱ��Ҫ��sbOld
    public static String getHmacSBOld(String[] HmacOrder, Map map, String connectFlag) {
        int index = HmacOrder.length;
        String[] args = new String[index];
        String key = "";
        String value = "";
        for (int i = 0; i < index; i++) {
            key = HmacOrder[i];
            value = (String) map.get(key);
            if (value == null) {
                value = "";
            }
            args[i] = value;
        }
        return getHmacSBOld(args, connectFlag);
    }

    // �������hmacʱ��Ҫ��sbOld
    public static String getHmacSBOld(String[] args, String connectFlag) {
        StringBuffer returnString = new StringBuffer();
        int index = args.length;
        for (int i = 0; i < index; i++) {
            returnString.append(args[i]).append(connectFlag);
        }
        return returnString.substring(0, returnString.length() - hmac_CONNECT_Flag.length());
    }

    public static String formatString(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }

    public static String hmacSign(String aValue, String aKey) {
        byte k_ipad[] = new byte[64];
        byte k_opad[] = new byte[64];
        byte keyb[];
        byte value[];
        try {
            String str = aValue;
            keyb = aKey.getBytes(encodingCharset);
            value = aValue.getBytes(encodingCharset);
        }
        catch (UnsupportedEncodingException e) {
            keyb = aKey.getBytes();
            value = aValue.getBytes();
            e.printStackTrace();
        }
        Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
        Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
        for (int i = 0; i < keyb.length; i++) {
            k_ipad[i] = (byte) (keyb[i] ^ 0x36);
            k_opad[i] = (byte) (keyb[i] ^ 0x5c);
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        md.update(k_ipad);
        md.update(value);
        byte dg[] = md.digest();
        md.reset();
        md.update(k_opad);
        md.update(dg, 0, 16);
        dg = md.digest();
        System.out.println("3333333333333333333333");
        return toHex(dg);
    }

    public static String hmacSign(String aValue) {
        try {
            byte[] input = aValue.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            return toHex(md.digest(input));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toHex(byte input[]) {
        if (input == null)
            return null;
        StringBuffer output = new StringBuffer(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            int current = input[i] & 0xff;
            if (current < 16)
                output.append("0");
            output.append(Integer.toString(current, 16));
        }
        return output.toString();
    }

    public static String getHmac(String[] args, String key) {
        if (args == null || args.length == 0) {
            return (null);
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            str.append(args[i]);
        }
        return (hmacSign(str.toString(), key));
    }

    public static String digest(String aValue) {
        aValue = aValue.trim();
        byte value[];
        try {
            value = aValue.getBytes(encodingCharset);
        }
        catch (UnsupportedEncodingException e) {
            value = aValue.getBytes();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return toHex(md.digest(value));
    }

    /**
     * ����hmac����
     * ҵ������
     * @param p0_Cmd
     * �̻����
     * @param p1_MerId
     * �̻�������
     * @param p2_Order
     * ֧�����
     * @param p3_Amt
     * ���ױ���
     * @param p4_Cur
     * ��Ʒ����
     * @param p5_Pid
     * ��Ʒ����
     * @param p6_Pcat
     * ��Ʒ����
     * @param p7_Pdesc
     * �̻�����֧���ɹ����ݵĵ�ַ
     * @param p8_Url
     * �ͻ���ַ
     * @param p9_SAF
     * �̻���չ��Ϣ
     * @param pa_MP
     * ���б���
     * @param pd_FrpId
     * Ӧ�����
     * @param pr_NeedResponse
     * �̻���Կ
     * @param keyValue
     * @return
     */
    public static String getReqMd5HmacForOnlinePayment(String p0_Cmd, String p1_MerId, String p2_Order, String p3_Amt,
            String p4_Cur, String p5_Pid, String p6_Pcat, String p7_Pdesc, String p8_Url, String p9_SAF, String pa_MP,
            String pd_FrpId, String pr_NeedResponse, String keyValue) {
        StringBuffer sValue = new StringBuffer();
        // ҵ������
        sValue.append(p0_Cmd);
        // �̻����
        sValue.append(p1_MerId);
        // �̻�������
        sValue.append(p2_Order);
        // ֧�����
        sValue.append(p3_Amt);
        // ���ױ���
        sValue.append(p4_Cur);
        // ��Ʒ����
        sValue.append(p5_Pid);
        // ��Ʒ����
        sValue.append(p6_Pcat);
        // ��Ʒ����
        sValue.append(p7_Pdesc);
        // �̻�����֧���ɹ����ݵĵ�ַ
        sValue.append(p8_Url);
        // �ͻ���ַ
        sValue.append(p9_SAF);
        // �̻���չ��Ϣ
        sValue.append(pa_MP);
        // ���б���
        sValue.append(pd_FrpId);
        // Ӧ�����
        sValue.append(pr_NeedResponse);

        String sNewString = null;
        sNewString = DigestUtil.hmacSign(sValue.toString(), keyValue);
        return (sNewString);
    }
}
