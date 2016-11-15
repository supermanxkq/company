package com.ccservice.b2b2c.atom.mobileApp;

import java.net.URLEncoder;

import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 调用身份验证的接口
 * @time 2015年5月28日 下午7:35:06
 * @author baiyushan
 */
public class ShenfenyanzhengMethod {

    public String yanzheng(String username, String useridentitytype, String useridentity, int num) {
        String result = "";

        try {
            String merchantCode = "tongcheng_train";
            String key = "x3z5nj8mnvl14nirtwlvhvuialo0akyt";
            long timestamp = System.currentTimeMillis();
            String serviceID = "V0101";
            String version = "1.0.0";
            //加密的数据
            JSONObject jso1 = new JSONObject();
            JSONArray jsa1 = new JSONArray();
            JSONObject jsob1 = new JSONObject();
            jsob1.put("passenger_id_no", useridentity);
            jsob1.put("passenger_name", username);
            jsob1.put("passenger_id_type_name", useridentitytype);
            jsob1.put("passenger_id_type_code", num);
            jsa1.add(jsob1);
            jso1.put("passengers", jsa1);
            String data1 = jso1.toString();
            String str_sign = ElongHotelInterfaceUtil.MD5(
                    merchantCode + serviceID + timestamp + data1 + ElongHotelInterfaceUtil.MD5(key).toUpperCase())
                    .toUpperCase();
            JSONObject jso = new JSONObject();
            JSONArray jsa = new JSONArray();
            JSONObject jsob = new JSONObject();
            jsob.put("passenger_id_no", useridentity);
            jsob.put("passenger_name", URLEncoder.encode(username, "UTF-8"));
            jsob.put("passenger_id_type_name", URLEncoder.encode(useridentitytype, "UTF-8"));
            jsob.put("passenger_id_type_code", num);
            jsa.add(jsob);
            jso.put("passengers", jsa);
            //发送的数据
            String data = jso.toString();
            System.out.println("在请求中str_sign" + str_sign);
            //trainorder.test.hangtian123.net
            String url = "http://trainorder.hangtian123.com/cn_home/trainidverification";
            url += "?merchantCode=" + merchantCode + "&serviceId=" + serviceID + "&version=" + version + "&timestamp="
                    + timestamp + "&sign=" + str_sign + "&";
            String paramContent = "data=" + data;
            result = SendPostandGet.submitPost(url, paramContent, "UTF-8").toString();
            //System.out.println(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
