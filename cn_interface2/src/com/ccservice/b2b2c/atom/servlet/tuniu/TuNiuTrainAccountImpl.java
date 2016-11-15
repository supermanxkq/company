package com.ccservice.b2b2c.atom.servlet.tuniu;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

/**
 * 此类弃用，请参考TrainAccountOperateMethod类
 * @author WH
 * @time 2015年12月25日 上午9:53:59
 * @version 1.0
 */

public class TuNiuTrainAccountImpl extends TongchengSupplyMethod {
    static int r1 = new Random().nextInt(10000000);

    public static JSONObject editPassengers(JSONObject jsonObject_jsonObject) {
        String cookie = jsonObject_jsonObject.containsKey("cookie") ? jsonObject_jsonObject.getString("cookie") : "";
        int id = Integer.valueOf(jsonObject_jsonObject.containsKey("id") ? jsonObject_jsonObject.getString("id") : "0")
                .intValue();
        int type = Integer.valueOf(
                jsonObject_jsonObject.containsKey("type") ? jsonObject_jsonObject.getString("type") : "").intValue();
        String result = "";
        String url = "http://localhost:9004/Reptile/traininit";
        JSONObject jsonresult = new JSONObject();
        try {
            if (type == 1) {//添加
                result = add(url, cookie, jsonObject_jsonObject);
            }
            else if (type == 2) {//删除
                result = delete(url, cookie, id);
            }
            else if (type == 3) {//修改
                result = update(url, cookie, id, jsonObject_jsonObject);
            }
            else if (type == 4) {//查询
                result = select(url, cookie);
            }

            if (!"-1".equals(result)) {
                jsonresult = JSONObject.parseObject(result);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsonresult;
    }

    /**
     * 增加
     * @time 2015年11月20日16:26:07
     * @author QingXin
     * @throws UnsupportedEncodingException 
     **/
    public static String add(String url, String cookie, JSONObject jsonObject_jsonObject) throws Exception {
        String result = "-1";
        try {
            jsonObject_jsonObject.put("busType", "1");
            jsonObject_jsonObject.put("cookie", cookie);
            String param = "datatypeflag=110&jsonStr=" + jsonObject_jsonObject.toJSONString() + "";
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 删除
     * @time 2015年11月20日16:26:07
     * @author QingXin
     * @throws UnsupportedEncodingException 
     **/
    public static String delete(String url, String cookie, int id) throws UnsupportedEncodingException {
        JSONObject jsonStr = new JSONObject();
        String result = "-1";
        try {
            jsonStr.put("id", id);//要删除的乘客id
            jsonStr.put("cookie", cookie);
            jsonStr.put("busType", "3");
            String param = "datatypeflag=110&jsonStr=" + URLEncoder.encode(jsonStr.toJSONString(), "UTF-8");
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 修改
     * @time 2015年11月20日16:26:07
     * @author QingXin
     * @throws Exception 
     **/
    public static String update(String url, String cookie, int id, JSONObject jsonObject_jsonObject) throws Exception {
        String result = "-1";
        try {
            jsonObject_jsonObject.put("cookie", cookie);
            jsonObject_jsonObject.put("busType", "2");
            jsonObject_jsonObject.put("id", id);//需要修改的乘客id
            String param = "datatypeflag=110&jsonStr="
                    + URLEncoder.encode(jsonObject_jsonObject.toJSONString(), "UTF-8");
            WriteLog.write("途牛增改的传值cookie", r1 + ":TuNiuTrainAccountImpl+param:" + param);
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查询
     * @time 2015年11月20日16:26:07
     * @author QingXin
     * @throws UnsupportedEncodingException 
     **/
    public static String select(String url, String cookie) throws UnsupportedEncodingException {
        JSONObject jObject = new JSONObject();
        String result = "-1";
        try {
            jObject.put("cookie", cookie);
            jObject.put("busType", "4");
            String param = "datatypeflag=110&jsonStr=" + URLEncoder.encode(jObject.toJSONString(), "UTF-8");
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 格式转换
     * @time 2015年11月20日16:26:07
     * @author QingXin
     **/
    public static String isCanGPs(String total_times) {
        String[] two_isOpenClick = { "93", "95", "97", "99" };
        int a = two_isOpenClick.length;
        for (int d = 0; d < a; ++d) {
            if (two_isOpenClick[d].equals(total_times)) {
                return "已通过";
            }
        }
        if (("92".equals(total_times)) || ("98".equals(total_times))) {
            return "待核验";
        }
        if ("91".equals(total_times)) {
            return "请报验";
        }
        if ("94".equals(total_times)) {
            return "未通过";
        }
        return "未通过";
    }

}