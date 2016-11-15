package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class SignUtil {

    public static String generateSign(String jsonStr, String pwd) {
        String sign = null;
        if (jsonStr == null || "".equals(jsonStr)) {
            throw new RuntimeException("参数为空");
        }
        else {
            // 按照key值首字母排序，并去掉value为空的
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            List<String> keyList = new ArrayList<String>();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = (String) entry.getKey();
                if (jsonObject.getString(key) != null && !"".equals(jsonObject.getString(key))
                        && !"null".equals(jsonObject.getString(key))) {
                    keyList.add(key);
                }
            }
            // 排序
            // Collections.sort(keyList);

            String temp = null;
            for (int i = 0; i < keyList.size(); i++) {
                for (int j = i + 1; j < keyList.size(); j++) {
                    if (keyList.get(i).charAt(0) > keyList.get(j).charAt(0)) {
                        temp = keyList.get(i);
                        keyList.set(i, keyList.get(j));
                        keyList.set(j, temp);
                    }
                }
            }

            // 2. 将参数名、参数值依次拼接在一起
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < keyList.size(); i++) {
                sb.append(keyList.get(i));
                sb.append(jsonObject.get(keyList.get(i)));
            }
            // 3. 在开头和结尾分别加上密钥
            sign = pwd + sb.toString() + pwd;
            try {
                sign = ElongHotelInterfaceUtil.MD5(sign).toUpperCase();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sign;
    }

    public static void main(String[] args) {
        String data = "{\"account\":\"Ape2hqlBF0sFUUcjbj\",\"timestamp\":\"2015-08-15 18:16:32\",\"agencyProductId\":12345,\"info\":\"\",\"planDateStr\":\"test\"}";
        String pwd = "wUDSCOdFibEL6pIQGYgF";
        System.out.println(generateSign(data, pwd));
    }
}
