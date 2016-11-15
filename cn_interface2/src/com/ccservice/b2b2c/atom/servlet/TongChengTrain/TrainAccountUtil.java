package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;

/**
 * 接口使用的12306帐号操作
 * @author Administrator
 *
 */
public class TrainAccountUtil extends TongchengSupplyMethod {

    public static void main(String[] args) {
        String cookieString = "JSESSIONID=0A01D94F980F235E8B82D81918B2AA3906B0DD6D79;BIGipServerotn=1339621642.38945.0000;  BIGipServeropn=1993605386.64545.0000; current_captcha_type=Z";
        JSONObject jsonObject_jsonStr = new JSONObject();
        jsonObject_jsonStr.put("type", 4);
        jsonObject_jsonStr.put("cookie", cookieString);
        jsonObject_jsonStr.put("passenger_name", "lubignb");
        jsonObject_jsonStr.put("passenger_id_no", "34128219920926461X");
        jsonObject_jsonStr.put("passenger_id_type_code", 1);
        System.out.println(new TrainAccountUtil().editPassenger(jsonObject_jsonStr));
        JSONObject json = new JSONObject();

    }

    public String send12306sms(JSONObject json) {
        String url = "http://localhost:9016/Reptile/traininit";
        //        RepServerBean repServerBean = GetCommonRep(false);
        //        String url = repServerBean.getUrl();
        String result = "";
        String param = "";
        String password = json.getString("json");
        String cookie = json.getString("cookie");
        JSONObject jObject = new JSONObject();
        jObject.put("password", password);
        jObject.put("cookie", cookie);
        String jsonStr = jObject.toJSONString();
        param = "datatypeflag=16_1&cookie=" + cookie + "&jsonStr=" + jsonStr;
        result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        return result;
    }

    public String push12306sms(JSONObject json) {
        String url = "http://localhost:9016/Reptile/traininit";
        //        RepServerBean repServerBean = GetCommonRep(false);
        //        String url = repServerBean.getUrl();
        String result = "";
        String param = "";
        String cookie = json.getString("cookie");
        String mobilecode = json.getString("mobilecode");
        JSONObject jObject = new JSONObject();
        jObject.put("cookie", cookie);
        jObject.put("mobilecode", mobilecode);
        String jsonStr = jObject.toJSONString();
        param = "datatypeflag=16_1&cookie=" + cookie + "&jsonStr=" + jsonStr;
        result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        return result;
    }

    /**
     * 
     * 
     * @param cookie
     * @param type 1增2删3改4查
     * @return
     * @time 2015年10月22日 下午4:16:19
     * @author lubing
     */
    //    public JSONObject editPassenger(String cookie, int type, List<Trainpassenger> trainPassengers, String jsonStr) {
    public String editPassenger(JSONObject jsonObject_jsonObject) {
        String cookie = jsonObject_jsonObject.getString("cookie");
        int type = Integer.valueOf(jsonObject_jsonObject.getString("type"));
        JSONObject jsonObject = new JSONObject();
        String result = "";
        String url = "http://localhost:9016/Reptile/traininit";
        String param = "";

        //查
        if (type == 4) {
            param = "datatypeflag=109&cookie=" + cookie;
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
            jsonObject = JSONObject.parseObject(result);
            String total_times = jsonObject.getString("total_times");
            ValueFilter vfilter = new ValueFilter() {
                @Override
                public Object process(Object object, String propertyName, Object propertyValue) {
                    if ("total_times".equals(propertyName)) {
                        propertyValue = isCanGP(propertyValue.toString());
                    }
                    return propertyValue;
                }
            };
            jsonObject = JSONObject.parseObject(JSONObject.toJSONString(jsonObject, vfilter));

        }
        //改
        else if (type == 3) {
            String name = jsonObject_jsonObject.getString("passenger_name");
            String old_name = jsonObject_jsonObject.getString("old_passenger_name");
            String id_no = jsonObject_jsonObject.getString("passenger_id_no");
            String old_id_no = jsonObject_jsonObject.getString("old_passenger_id_no");
            String id_type = jsonObject_jsonObject.getString("passenger_id_type_code");
            String old_id_type = jsonObject_jsonObject.getString("old_passenger_id_type_code");
            JSONObject jObject = new JSONObject();
            jObject.put("name", name);
            jObject.put("old_name", old_name);
            jObject.put("id_no", id_no);
            jObject.put("old_id_no", old_id_no);
            jObject.put("id_type", id_type);
            jObject.put("old_id_type", old_id_type);
            String jsonStr = jObject.toJSONString();
            param = "datatypeflag=16_1&cookie=" + cookie + "&jsonStr=" + jsonStr;
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        }
        //删除所有
        else if (type == 2) {
            param = "datatypeflag=106&cookie=" + cookie;
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        }
        //增
        else if (type == 1) {
            String name = jsonObject_jsonObject.getString("passenger_name");
            System.out.println(name);
            String id_no = jsonObject_jsonObject.getString("passenger_id_no");
            System.out.println(id_no);
            String id_type = jsonObject_jsonObject.getString("passenger_id_type_code");
            System.out.println(id_type);
            param = "datatypeflag=16_3&cookie=" + cookie + "&name=" + name + "&id_type=" + id_type + "&id_no=" + id_no;
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        } //删除单个
        else if (type == 0) {
            JSONObject jObject = new JSONObject();
            String name = jsonObject_jsonObject.getString("passenger_name");
            jObject.put("name", name);
            String id_no = jsonObject_jsonObject.getString("passenger_id_no");
            jObject.put("id_no", id_no);
            String id_type = jsonObject_jsonObject.getString("passenger_id_type_code");
            jObject.put("id_type", id_type);
            String jsonStr = jObject.toJSONString();
            param = "datatypeflag=16_4&cookie=" + cookie + "&jsonStr=" + jsonStr;
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        }
        return result;
    }

    //查询该账号下所有订单
    public String querymy12306trainorder(JSONObject json) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String cookie = json.getString("cookie");
        String queryType = "1";
        if (json.getString("queryType") != null && !"".equals(json.getString("queryType"))) {
            queryType = json.getString("queryType");
        }
        String queryStartDate = sdf.format(new Date());
        if (json.getString("queryStartDate") != null && !"".equals(json.getString("queryStartDate"))) {
            queryStartDate = json.getString("queryStartDate");
        }
        String queryEndDate = sdf.format(new Date());
        if (json.getString("queryEndDate") != null && !"".equals(json.getString("queryEndDate"))) {
            queryEndDate = json.getString("queryEndDate");
        }

        String sequeue_train_name = "";
        if (json.getString("sequeue_train_name") != null && !"".equals(json.getString("sequeue_train_name"))) {
            sequeue_train_name = json.getString("sequeue_train_name");
        }
        String query_where = json.getString("query_where");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", queryType);
        jsonObject.put("queryStartDate", queryStartDate);
        jsonObject.put("queryEndDate", queryEndDate);
        jsonObject.put("sequeue_train_name", sequeue_train_name);
        jsonObject.put("query_where", query_where);
        String jsonStr = JSONObject.toJSONString(jsonObject);
        String url = "http://localhost:9016/Reptile/traininit";
        String param = "";
        param = "datatypeflag=16_2&cookie=" + cookie + "&jsonStr=" + jsonStr;
        String result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        return result;
    }

    /**
     * 说明：身份验证标准：
     *   身份证:     93,95,97,99可以买票   92,98待核验    96,94未通过      91请报验
     *   非身份证:   "93", "98", "99", "91", "95", "97"可以买票
     * @param id_type
     * @param total_times
     * @return
     * @time 2014年8月30日 下午2:31:15
     * @author yinshubin
     */
    public String isCanGP(String total_times) {
        String[] two_isOpenClick = { "93", "95", "97", "99" };
        int a = two_isOpenClick.length;
        for (int d = 0; d < a; d++) {
            if (two_isOpenClick[d].equals(total_times)) {
                return "已通过";
            }
        }
        if ("92".equals(total_times) || "98".equals(total_times)) {
            return "待核验";
        }
        else if ("91".equals(total_times)) {
            return "请报验";
        }
        else if ("94".equals(total_times)) {
            return "未通过";
        }
        return "未通过";
    }

}
