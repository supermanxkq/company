package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.base.customeruser.Customeruser;

public class TrainCheckLoginAccountStatus extends TongchengSupplyMethod {
    /**
     * 
     * @param json
     * @time 2015年10月26日 下午6:28:12
     * @author chendong
     */
    public String checkLoginAccountByJson(JSONObject json) {
        String cookie = json.getString("cookie");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from_station", "RZK");
        jsonObject.put("to_station", "ZZF");
        jsonObject.put("from_station_name", "日照");
        jsonObject.put("to_station_name", "郑州");
        jsonObject.put("train_date", TimeUtil.gettodaydatebyfrontandback(1, 20));
        WriteLog.write("TrainCheckLoginAccountStatus_checkLoginAccountByJson", cookie + ":" + jsonObject);
        JSONObject checkResultJSONObject = checkLoginAccount(cookie, jsonObject);
        WriteLog.write("TrainCheckLoginAccountStatus_checkLoginAccountByJson",
                cookie + ":" + checkResultJSONObject.toJSONString());
        return checkResultJSONObject.toJSONString();
    }

    /**
     * 使用cookie做登录判断
     * 
     * @param cookie
     * @time 2015年10月22日 下午3:11:03
     * @author wangchengliang
     */
    public JSONObject checkLoginAccount(String cookie, JSONObject jsonObject) {
        JSONObject json = new JSONObject();
        String code = "100";
        boolean success = true;
        if (!ElongHotelInterfaceUtil.StringIsNull(cookie)) {//判断cookie为不为空
            if (cookie.contains("JSESSIONID") && cookie.contains("BIGipServerotn")) {//判断cookie格式
                RepServerBean repServerBean = RepServerUtil.getRepServer(new Customeruser(), false);
                String url = repServerBean.getUrl();
                String param = "datatypeflag=109&cookie=" + cookie;
                WriteLog.write("TrainCheckLoginAccountStatus_checkLoginAccountByJson", cookie + ":" + param + ":" + url);
                String resultLogin = SendPostandGet.submitPost(url, param, "UTF-8").toString();
                WriteLog.write("TrainCheckLoginAccountStatus_checkLoginAccountByJson", cookie + ":" + param + ":" + url);
                //判断有没有登录
                if (!ElongHotelInterfaceUtil.StringIsNull(resultLogin) && resultLogin.contains("passengers")) {
                    param = "datatypeflag=302&cookie=" + cookie + "&jsonStr=" + jsonObject.toString();
                    String resultPlaceOrder = SendPostandGet.submitPost(url, param, "UTF-8").toString();
                    WriteLog.write("TrainCheckLoginAccountStatus", "验证能否下单返回:" + resultPlaceOrder);
                    JSONObject jsonResult = resultPlaceOrder != null ? jsonObject.parseObject(resultPlaceOrder) : null;
                    //判断能否下单
                    if (jsonResult.containsKey("success") && jsonResult.getBoolean("success")) {

                        json.put("loginStatus", "1");
                        json.put("createOrderStatus", "1");
                    }
                    else {
                        json.put("loginStatus", "1");
                        json.put("createOrderStatus", "0");
                    }

                }
                else {
                    json.put("loginStatus", "0");
                    json.put("createOrderStatus", "0");
                }
            }
            else {
                code = "108";
                success = false;
            }
        }
        else {
            code = "107";
            success = false;
        }
        json.put("success", success);
        json.put("code", code);
        json.put("msg", "");
        WriteLog.write("TrainCheckLoginAccountStatus", "json:" + json.toString());
        return json;
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from_station", "RZK");
        jsonObject.put("to_station", "ZZF");
        jsonObject.put("from_station_name", "日照");
        jsonObject.put("to_station_name", "郑州");
        jsonObject.put("train_date", "2015-11-25");
        String cookie = "JSESSIONID=0A01D9569896C56745CEE0CAE2CDF6D3C7EE5B6CC4;BIGipServerotn=1457062154.38945.0000; current_captcha_type=Z";
        //        JSONObject result = checkLoginAccount(cookie, jsonObject);
        //        System.out.println(result.toString());    String url = "http://192.168.0.56:9016/Reptile/traininit";
    }

}
