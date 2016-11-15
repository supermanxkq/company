package com.alipay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.pay.config.AlipayConfig;

// Referenced classes of package com.alipay.util:
//            Md5Encrypt

public class Alipay_fuction {

    public Alipay_fuction() {
    }

    public static String sign(Map params, String privateKey) {
        Properties properties = new Properties();
        for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            Object value = params.get(name);
            if (name != null && !name.equalsIgnoreCase("sign") && !name.equalsIgnoreCase("sign_type")) {
                properties.setProperty(name, value.toString());
            }
        }

        String content = getSignatureContent(properties);
        if (privateKey == null) {
            return null;
        }
        String signBefore = (new StringBuilder(String.valueOf(content))).append(privateKey).toString();
        return Md5Encrypt.md5(signBefore);
    }

    public static String getSignatureContent(Properties properties) {
        StringBuffer content = new StringBuffer();
        List keys = new ArrayList((Collection) properties.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = properties.getProperty(key);
            content.append((new StringBuilder(String.valueOf(i != 0 ? "&" : ""))).append(key).append("=").append(value)
                    .toString());
        }

        return content.toString();
    }

    public static String getContent_public(Map params, String privateKey) {
        List keys = new ArrayList((Collection) params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = (String) params.get(key);
            if (value == null || value.length() == 0) {
                continue;
            }
            if (i == keys.size() - 1) {
                prestr = (new StringBuilder(String.valueOf(prestr))).append(key).append("=").append(value).toString();
            }
            else {
                prestr = (new StringBuilder(String.valueOf(prestr))).append(key).append("=").append(value).append("&")
                        .toString();
            }
        }
        return (new StringBuilder(String.valueOf(prestr))).append(privateKey).toString();
    }

    public static String checkurl(String urlvalue) {
        String inputLine = "";
        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            inputLine = in.readLine().toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return inputLine;
    }

    public String creatteInvokeUrl() {
        Map params = new HashMap();
        params.put("service", "query_timestamp");
        params.put("partner", AlipayConfig.getInstance().getPartnerID());
        params.put("_input_charset", AlipayConfig.getInstance().getCharSet());
        String prestr = "";
        prestr = (new StringBuilder(String.valueOf(prestr))).append(AlipayConfig.getInstance().getKey()).toString();
        String sign = Md5Encrypt.md5(getContent_public(params, AlipayConfig.getInstance().getKey()));
        String parameter = "";
        parameter = (new StringBuilder(String.valueOf(parameter))).append("https://mapi.alipay.com/gateway.do?")
                .toString();
        List keys = new ArrayList((Collection) params.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String value = (String) params.get(keys.get(i));
            if (value != null && value.trim().length() != 0) {
                try {
                    parameter = (new StringBuilder(String.valueOf(parameter))).append(keys.get(i)).append("=")
                            .append(URLEncoder.encode(value, AlipayConfig.getInstance().getCharSet())).append("&")
                            .toString();
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        parameter = (new StringBuilder(String.valueOf(parameter))).append("sign=").append(sign).append("&sign_type=")
                .append("MD5").toString();
        return parameter;
    }

    public String parseAlipayTimestampResultXml(String filepath) throws MalformedURLException, DocumentException,
            IOException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read((new URL(filepath)).openStream());
        List nodeList = doc.selectNodes("//alipay/*");
        StringBuffer buf1 = new StringBuffer();
        for (Iterator iterator = nodeList.iterator(); iterator.hasNext();) {
            Node node = (Node) iterator.next();
            if (node.getName().equals("is_success") && node.getText().equals("T")) {
                List nodeList1 = doc.selectNodes("//response/timestamp/*");
                Node node1;
                for (Iterator iterator1 = nodeList1.iterator(); iterator1.hasNext(); buf1.append(node1.getText())) {
                    node1 = (Node) iterator1.next();
                }

            }
        }

        return buf1.toString();
    }
}
