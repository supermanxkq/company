package com.insurance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SunshineInsuranceTrainMD5Util {

    public static byte[] getMD5Mac(byte[] bySourceByte) {
        byte[] byDisByte;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(bySourceByte);
            byDisByte = md.digest();
        }
        catch (NoSuchAlgorithmException n) {

            return (null);
        }
        return (byDisByte);
    }

    public static String bintoascii(byte[] bySourceByte) {
        int len, i;
        byte tb;
        char high, tmp, low;
        String result = new String();
        len = bySourceByte.length;
        for (i = 0; i < len; i++) {
            tb = bySourceByte[i];
            tmp = (char) ((tb >>> 4) & 0x000f);
            if (tmp >= 10)
                high = (char) ('a' + tmp - 10);
            else
                high = (char) ('0' + tmp);
            result += high;
            tmp = (char) (tb & 0x000f);
            if (tmp >= 10)
                low = (char) ('a' + tmp - 10);
            else
                low = (char) ('0' + tmp);
            result += low;
        }
        return result;
    }

    public static String getMD5ofStr(String inbuf) {
        if (inbuf == null || "".equals(inbuf))
            return "";
        try {
            return bintoascii(getMD5Mac(inbuf.getBytes("GBK")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMD5ofStr(byte[] bySourceByte) {
        if (bySourceByte == null || "".equals(bySourceByte))
            return "";
        try {
            return bintoascii(getMD5Mac(bySourceByte));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String createSignUsingMD5(String inbuf, String codetype) {
        return getMD5ofStr(inbuf).toLowerCase();
    }

    public static void main(String[] args) {
        String infodata = "f15r8gth2yjny6jy56<?xml version=\"1.0\" encoding=\"GBK\"?><INSURENCEINFO><USERNAME>G5RH4T1HJY6UKUK41UJ3TG236ED</USERNAME><PASSWORD>1THTH1Y3KM5L6IOU4KI6L412F1</PASSWORD><ORDER><ORDERID>14323000045470438</ORDERID><POLICYINFO><PRODUCTCODE>QP010901</PRODUCTCODE><PLANCODE></PLANCODE><INSURDATE>2015-05-22 21:06:44</INSURDATE><INSURSTARTDATE>2015-06-01 20:06:44</INSURSTARTDATE><INSURENDDATE>2015-06-04 20:06:44</INSURENDDATE><APPNTMOBILE>18888832156</APPNTMOBILE><APPNTNAME>陈栋</APPNTNAME><APPNTIDTYPE>10</APPNTIDTYPE><APPNTIDNO>210381198401124515</APPNTIDNO><APPNTBIRTHDAY>19840112</APPNTBIRTHDAY><INSURPERIOD>3</INSURPERIOD><PERIODFLAG>D</PERIODFLAG><MULT>1</MULT><AGREEMENTNO>860114020001</AGREEMENTNO><PREMIUM>20.0</PREMIUM><AMOUNT>805000</AMOUNT><BENEFMODE>0</BENEFMODE><AIRLINEDATE>2015-06-01 21:06:44</AIRLINEDATE><AIRLINENO>2549</AIRLINENO><INSUREDLIST><INSURED><INSUREDNAME>陈栋</INSUREDNAME><RELATIONSHIP>10</RELATIONSHIP><INSUREDIDNO>210381198401124515</INSUREDIDNO><INSUREDIDTYPE>10</INSUREDIDTYPE><INSUREDBIRTHDAY>19840112</INSUREDBIRTHDAY><INSUREDMOBILE>18888832156</INSUREDMOBILE><INSUREDEMAIL>zengqingquan@clbao.com</INSUREDEMAIL></INSURED></INSUREDLIST></POLICYINFO></ORDER></INSURENCEINFO>";
        System.out.println(infodata);
        System.out.println(getMD5ofStr(infodata));

    }
}
