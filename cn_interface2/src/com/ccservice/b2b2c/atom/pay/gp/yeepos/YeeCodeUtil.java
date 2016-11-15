package com.ccservice.b2b2c.atom.pay.gp.yeepos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tenpay.util.MD5Util;

public class YeeCodeUtil {
    public static void main(String[] args) {
        String msg = MD5Util.MD5Encode("好", "UTF-8");
        System.out.println(msg);
    }

    /**
    * @param srcXml 原 xml
    * @param secKey 加密 key
    * @return 生成新的带 hamc 节点的 xml
    */
    public String putMD5(String srcXml, String secKey) {
        // xml 文档开始与结束部分， 此部分不进行加密处理， 在拼接时使用
        String xmlNodeStartString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><COD-MS>";
        String xmlNodeEndString = "</COD-MS>";
        // 需要加密的字符串
        String md5String = "";
        // 本次传送 xml 的 md5
        String md5token = "";
        md5String = srcXml.substring(srcXml.indexOf("<COD-MS>") + 8, srcXml.indexOf("</COD-MS>"));
        // 把 hmac 节点过滤点
        String hmac = srcXml.substring(srcXml.indexOf("<HMAC>"), srcXml.indexOf("</HMAC>") + 7);
        md5String = md5String.replace(hmac, "");
        md5String = filter(md5String);
        md5token = MD5Util.MD5Encode(md5String + secKey, "UTF-8");
        // hmac 节点前部分
        String startStr = md5String.split("</SessionHead>")[0];
        // hmac 节点后部分
        String endStr = "";
        if (md5String.split("</SessionHead>").length > 1) {
            // hmac 节点后部分
            endStr = md5String.split("</SessionHead>")[1];
        }
        return xmlNodeStartString + startStr + "<HMAC>" + md5token + "</HMAC>" + "</SessionHead>" + endStr
                + xmlNodeEndString;
    }

    /**
     * @param srcXml 原 xml
     * @param secKey 加密 key
     * @return 生成新的带 hamc 节点的 xml
     */
    public String putMD5Sign(String srcXml, String secKey) {
        // 需要加密的字符串
        String md5String = "";
        // 本次传送 xml 的 md5
        String md5token = "";
        md5String = srcXml.substring(srcXml.indexOf("<COD-MS>") + 8, srcXml.indexOf("</COD-MS>"));
        String hmac = srcXml.substring(srcXml.indexOf("<HMAC>"), srcXml.indexOf("</HMAC>") + 7);
        md5String = md5String.replace(hmac, "");
        md5String = filter(md5String);
        md5token = MD5Util.MD5Encode(md5String + secKey, "UTF-8");
        return md5token;
    }

    /***过滤制表符**
    
    @param content
    * @return String
    */
    public String filter(String content) {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(content);
        return m.replaceAll("");
    }
}
