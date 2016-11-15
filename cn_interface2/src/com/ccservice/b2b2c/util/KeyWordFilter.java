package com.ccservice.b2b2c.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 敏感词过滤
 */
public class KeyWordFilter {

    private static Pattern pattern = null;

    public static void initPattern() {
        InputStream in = null;
        StringBuffer patternBuf = new StringBuffer("");
        try {
            in = KeyWordFilter.class.getClassLoader().getResourceAsStream("keywords.txt");
            Properties pro = new Properties();
            pro.load(in);
            Enumeration enu = pro.propertyNames();
            patternBuf.append("(");
            while (enu.hasMoreElements()) {
                patternBuf.append(enu.nextElement().toString().trim() + "|");
            }
            patternBuf.deleteCharAt(patternBuf.length() - 1);
            patternBuf.append(")");
            pattern = Pattern.compile(new String(patternBuf.toString().getBytes("ISO-8859-1"), "UTF-8"),
                    Pattern.CASE_INSENSITIVE);
        }
        catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String doFilter(String str) {
        KeyWordFilter.initPattern();
        Matcher m = pattern.matcher(str);
        str = m.replaceAll("");
        return str;
    }

    public static void main(String[] args) {
        System.out.println(KeyWordFilter
                .doFilter("您好，订单号:A216912张东明10月14号国航CA1273北京T3至兰州--06:30起飞09:00到达,预订成功,请及时付款。客服电话:400-105-8166"));
    }
}