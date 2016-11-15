package com.ccservice.b2b2c.atom.servlet.tn;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainAccountPassengerUtil;

public class TuNiuTrainAccountContactSaveOrUpdate {
    static int id;

    public String cookie;

    static String name;

    static int isUserSelf;

    static int sex;

    static String birthday;

    static String country;

    static int identyType;

    static int identy;

    static int personType;

    static String phone;

    static String tel;

    static String email;

    static String address;

    static int checkStatus;

    static int sexs;

    /**
     * 2015年11月10日 20:23:55
     * 增加常用联系人
     * 
     */

    public JSONObject AddtrainAccount(JSONObject jsonObject) {
        JSONObject Getjso = null;
        JSONObject jso = null;
        TuNiuTrainAccountValidate tu = new TuNiuTrainAccountValidate();
        JSONObject cookieJson = tu.validateLoginNameAndPassword(jsonObject);
        cookie = cookieJson.getString("cookie");
        if (cookie == null || "".equals(cookie)) {

            String msg = "没登陆";
            Getjso = new JSONObject();
            Getjso.put("msg", msg);
            return Getjso;
        }

        try {
            jsonObject.put("cookie", cookie);

            jsonObject.put("type", "1");
            String re = TrainAccountPassengerUtil.editPassengers(jsonObject);
            jso = JSONObject.parseObject(re);
            //  String success = jso.getString("success");
            //  String errorCode = jso.getString("errorCode");
            String msg = jso.getString("messages");
            //  String data = jso.getString("data");
            Getjso = new JSONObject();
            //  Getjso.put("success", success);
            // Getjso.put("errorCode", errorCode);
            Getjso.put("msg", msg);
            //Getjso.put("data", data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Getjso;
    }

    /**
     * 2015年11月10日 20:23:45
     * 修改常用联系人
     * */
    public JSONObject UpdatetrainAccount(JSONObject jsonObject) {
        String re = null;
        JSONObject Getjso = null;
        TuNiuTrainAccountValidate tu = new TuNiuTrainAccountValidate();
        JSONObject cookieJson = tu.validateLoginNameAndPassword(jsonObject);
        cookie = cookieJson.getString("cookie");
        try {
            // = new JSONObject();

            jsonObject.put("cookie", cookie);
            jsonObject.put("type", "3");
            re = TrainAccountPassengerUtil.editPassengers(jsonObject);
            JSONObject jso = new JSONObject();
            //  String success = jso.getString("success");
            //  String errorCode = jso.getString("errorCode");
            String msg = jso.getString("messages");
            // String data = jso.getString("data");
            Getjso = new JSONObject();
            // Getjso.put("success", success);
            //  Getjso.put("errorCode", errorCode);
            Getjso.put("msg", msg);
            //  Getjso.put("data", data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Getjso;
    }
}
