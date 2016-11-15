package com.tenpay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pay.config.TenpayConfig;

public class TenpayUtil {

    /**
     * �Ѷ���ת�����ַ�
     * @param obj
     * @return String ת�����ַ�,������Ϊnull,�򷵻ؿ��ַ�.
     */
    public static String toString(Object obj) {
        if (obj == null)
            return "";

        return obj.toString();
    }

    /**
     * �Ѷ���ת��Ϊint��ֵ.
     * 
     * @param obj
     *            �����ֵĶ���.
     * @return int ת�������ֵ,�Բ���ת���Ķ��󷵻�0��
     */
    public static int toInt(Object obj) {
        int a = 0;
        try {
            if (obj != null)
                a = Integer.parseInt(obj.toString());
        }
        catch (Exception e) {

        }
        return a;
    }

    /**
     * ��ȡ��ǰʱ�� yyyyMMddHHmmss
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }

    /**
     * ��ȡ��ǰ���� yyyyMMdd
     * @param date
     * @return String
     */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String strDate = formatter.format(date);
        return strDate;
    }

    /**
     * ȡ��һ��ָ�����ȴ�С�����������.
     * 
     * @param length
     *            int �趨��ȡ�������ĳ��ȡ�lengthС��11
     * @return int ������ɵ������
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

    /**
     * ��ȡ�����ַ�
     * @param request
     * @param response
     * @return String
     */
    public static String getCharacterEncoding(HttpServletRequest request, HttpServletResponse response) {

        if (null == request || null == response) {
            return "gbk";
        }

        String enc = request.getCharacterEncoding();
        if (null == enc || "".equals(enc)) {
            enc = response.getCharacterEncoding();
        }

        if (null == enc || "".equals(enc)) {
            enc = "gbk";
        }

        return enc;
    }

    /**
     * ��ȡunixʱ�䣬��1970-01-01 00:00:00��ʼ������
     * @param date
     * @return long
     */
    public static long getUnixTime(Date date) {
        if (null == date) {
            return 0;
        }

        return date.getTime() / 1000;
    }

    /**
     * ʱ��ת�����ַ�
     * @param date ʱ��
     * @param formatType ��ʽ������
     * @return String
     */
    public static String date2String(Date date, String formatType) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        return sdf.format(date);
    }

    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    public static String createSign(SortedMap parameters, String key) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + key);
        String enc = TenpayConfig.getCharSet();
        String sign = MD5Util.MD5Encode(sb.toString(), enc).toUpperCase();
        parameters.put("sign", sign);
        return sign;
    }

    /**
     * 获取带参数的请求URL
     * @return String
     * @throws UnsupportedEncodingException 
     */
    public static String getRequestURL(SortedMap parameters, String key) throws UnsupportedEncodingException {
        String gateurl = (String) parameters.remove("paygateway");
        createSign(parameters, key);
        StringBuffer sb = new StringBuffer();
        String enc = TenpayConfig.getCharSet();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (!"spbill_create_ip".equals(k)) {
                sb.append(k + "=" + URLEncoder.encode(v, enc) + "&");
            }
            else {
                sb.append(k + "=" + v.replace("\\.", "%2E") + "&");
            }
        }
        //去掉最后一个&
        String reqPars = sb.substring(0, sb.lastIndexOf("&"));
        return gateurl + "?" + reqPars;

    }

    /**
     * 解析内容,子类一般要重写此方法。
     */
    public static Map doParse(String content) throws Exception {
        String xmlContent = content;
        //解析xml,得到map
        Pattern pattern = Pattern.compile("window.location.href.*=.*(http://.*|https://.*)['|\"]");
        Matcher matcher = pattern.matcher(xmlContent);
        Map m = new HashMap();
        if (matcher.find()) {
            //获取url
            String url = matcher.group(1);
            String queryString = HttpClientUtil.getQueryString(url);
            m = HttpClientUtil.queryString2Map(queryString);
            if (null != m && !m.isEmpty()) {
                String charset = TenpayConfig.getCharSet();
                Iterator it = m.keySet().iterator();
                while (it.hasNext()) {
                    String k = (String) it.next();
                    String v = (String) m.get(k);
                    m.put(k, URLDecoder.decode(v, charset));
                }
            }
        }
        //设置参数
        return m;
    }
}
