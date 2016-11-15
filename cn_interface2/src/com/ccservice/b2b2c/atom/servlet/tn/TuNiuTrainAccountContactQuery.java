package com.ccservice.b2b2c.atom.servlet.tn;

import java.util.Collection;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainAccountPassengerUtil;

public class TuNiuTrainAccountContactQuery {
    /**
     * 途牛查询常用联系人接口
     * 2015年11月10日 11:27:06
     * */

    static int id;

    public String cookie;

    String param = null;

    String type = null;

    static String name;

    static int isUserSelf;

    static int sex;

    static String birthday;

    static String country;

    static String identyType;

    static String identy;

    static int personType;

    static String phone;

    static String tel;

    static String email;

    static String address;

    static int checkStatus;

    JSONObject jsonObjects;

    static int sexs;

    public JSONObject find(JSONObject Getjson) {
        TuNiuTrainAccountValidate tu = new TuNiuTrainAccountValidate();
        JSONObject cookieJson = tu.validateLoginNameAndPassword(Getjson);
        JSONObject sbt = new JSONObject();
        JSONObject jsonObject = null;
        JSONObject josn = null;
        cookie = cookieJson.getString("cookie");
        if (cookie == null || "".equals(cookie)) {

            String msg = "没登陆";
            JSONObject Getjso = new JSONObject();
            Getjso.put("msg", msg);
            return Getjso;
        }
        sbt.put("cookie", cookie);
        sbt.put("type", "4");
        String re = TrainAccountPassengerUtil.editPassengers(sbt);
        josn = JSONObject.parseObject(re);
        Boolean typel = false;
        if (josn.getString("success").equals(typel) || josn.getString("success").equals("false")) {
            JSONObject jsonget = new JSONObject();
            String success = josn.getString("success");
            String msg = josn.getString("msg");
            jsonget.put("success", success);
            jsonget.put("msg", msg);

            return jsonget;
        }
        else {
            JSONArray jsona = JSONObject.parseArray(josn.getString("passengers"));
            for (int i = 0; i < jsona.size(); i++) {
                JSONObject json = (JSONObject) jsona.get(0);
                String ids = json.getString("code");
                if (!ids.endsWith("") || ids != null) {

                    id = Integer.parseInt(ids);
                }
                name = json.getString("passenger_name");
                if (json.getString("isUserSelf").equals("Y") || json.getString("isUserSelf") == "Y") {
                    isUserSelf = 0;
                }
                else {
                    isUserSelf = 1;
                }

                if ("男".equals(json.getString("sex_name"))) {
                    sex = 0;
                }
                else {
                    sex = 1;
                }
                birthday = json.getString("born_date");
                country = json.getString("country_code");
                identyType = json.getString("passenger_id_type_name");
                /*      if (!identyTypee.equals("") || identyTypee != null) {
                          identyType = Integer.parseInt(identyTypee);
                      }*/
                String identyy = json.getString("passenger_id_no");
                if (!identyy.equals("") || identyy != null) {
                    identy = identyy;
                }
                String personTypee = json.getString("passenger_type");
                if (!personTypee.equals("") || personTypee != null) {
                    personType = Integer.parseInt(personTypee);
                }
                phone = json.getString("mobile_no");
                tel = json.getString("phone_no");
                email = json.getString("email");
                address = json.getString("address");
                String checkStatuss = json.getString("passenger_id_type_code");
                if (!checkStatuss.equals("") || checkStatuss != null) {
                    checkStatus = Integer.parseInt(checkStatuss);
                }
                String success = json.getString("success");
                jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("name", name);
                jsonObject.put("isUserSelf", isUserSelf);
                jsonObject.put("sex", sex);
                jsonObject.put("birthday", birthday);
                jsonObject.put("country", country);
                jsonObject.put("identyType", identyType);
                jsonObject.put("identy", identy);
                jsonObject.put("personType", personType);
                jsonObject.put("phone", phone);
                jsonObject.put("tel", tel);
                jsonObject.put("email", email);
                jsonObject.put("address", address);
                jsonObject.put("checkStatus", checkStatus);
                jsonObject.put("success", success);

            }
        }
        return jsonObject;
    }

    public static void main(String[] args) {
        /*    TuNiuTrainAccountContactQuery tu = new TuNiuTrainAccountContactQuery();
            JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("cookie",
                            "JSESSIONID=0A02F00CFC2D56764EEAEE31DF9B2C7F3D5D84F673;BIGipServerotn=217055754.64545.0000;current_captcha_type=Z");
            */
        // jsonObject.put("trainAccount", "danweifeng1");
        /*  //jsonObject.put("pass", "swf19901018");
          JSONArray dama = tu.find(jsonObject);
          JSONObject json = new JSONObject();
          JSONArray js = dama;
          for (int i = 0; i < js.size(); i++) {
              Object jt = dama.get(i);
              System.out.println(jt);
          }
          // for (int i = 0; i < js.size(); i++) {
          System.out.println(js);*/
        // }

        /*    JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("cookie",
                            "JSESSIONID=0A02F02AFC710D8902CC8945DC2251FF016F7ED5C3;BIGipServerotn=720372234.64545.0000;current_captcha_type=Z");
            jsonObject.put("type", "4");
            TuNiuTrainAccountContactQuery tu = new TuNiuTrainAccountContactQuery();
            System.out.println(tu.find("lubing_12306", "lubing12306"));
            */
    }
}
