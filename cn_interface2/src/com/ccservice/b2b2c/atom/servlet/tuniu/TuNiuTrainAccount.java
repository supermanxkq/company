package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

/**
 * 此类弃用，请参考TrainAccountOperateMethod类
 * @author WH
 * @time 2015年12月25日 上午9:53:59
 * @version 1.0
 */

public class TuNiuTrainAccount extends TongchengSupplyMethod {

    int r1 = new Random().nextInt(10000000);

    private final String[] two_isOpenClick = { "93", "95", "97", "99" };

    private final String[] other_isOpenClick = { "93", "98", "99", "91", "95", "97" };

    public static void main(String[] args) {
        JSONObject Getjson = new JSONObject();
        //        JSONObject json = new JSONObject();
        //        json.put("id", 0);
        //        json.put("name", "小于娟");
        JSONObject json = new JSONObject();
        JSONObject jsons = new JSONObject();
        json.put("id", "1");
        json.put("name", "于娟娟");
        json.put("sex", "1");
        json.put("birthday", "");
        json.put("country", "CN");
        json.put("identyType", "1");
        json.put("identy", "360222199405058801");
        json.put("personType", "0");
        json.put("phone", "13041280278");
        json.put("tel", "13041280278");
        json.put("email", "916620419@qq.com");
        json.put("address", "北京市丰台区麦当劳厕所");
        json.put("phone", "63729259");
        Getjson.put("contacts", json);
        Getjson.put("trainAccount", "18232023860 ");
        Getjson.put("pass", "weidou318");
        Getjson.put("identyType", "1");
        Getjson.put("personType", "1");
        Getjson.put("id", "1");//修改或删除的id
        TuNiuTrainAccount account = new TuNiuTrainAccount();
        JSONObject jsonObject = account.AccountUpdateAndAdd(jsons);
        //        JSONObject jsonObject = account.Accountdelete(Getjson);
        //        JSONObject jsonObject = account.AccountUpdateAndAdd(Getjson);
        System.out.println(jsonObject.toJSONString());
    }

    /**
     * 验证
     * @time 2015年11月20日16:18:34
     * @author QingXin
     **/
    public JSONObject AccountValidate(JSONObject Getjson) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String cookie = "";
        String errorMsg = "";
        String returnCode = "";
        boolean success = false;
        cookie = AccountLogin(Getjson);
        try {
            if (cookie.contains("失败")) {
                jsonObject1.put("isPass", "1");
                errorMsg = cookie.split("[|]")[1];
                returnCode = "901";
            }
            jsonObject1.put("isPass", "0");
            returnCode = "231000";
            success = true;
        }
        catch (Exception e) {
            returnCode = "901";
            errorMsg = (errorMsg.equals("")) ? "系统异常" : errorMsg;
            success = false;
            e.printStackTrace();
        }
        jsonObject.put("success", success);
        jsonObject.put("returnCode", returnCode);
        jsonObject.put("errorMsg", errorMsg);
        jsonArray.add(jsonObject1);
        jsonObject.put("data", jsonArray.toArray());
        return jsonObject;
    }

    /**
     * 查询
     * @time 2015年11月20日16:18:34
     * @author QingXin
     **/
    public JSONObject AccountQuery(JSONObject Getjson) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String cookie = AccountLogin(Getjson);
        //        String cookie = "JSESSIONID=0A02F0089813B48FAA3F77130E00BAE35480270068; __NRF=A35F04881E6CCACA8EB7CA3F6D2F23C4; _jc_save_fromStation=%u5317%u4EAC%2CBJP; _jc_save_toStation=%u4E0A%u6D77%2CSHH; _jc_save_fromDate=2015-12-14; _jc_save_toDate=2015-12-14; _jc_save_wfdc_flag=dc; BIGipServerotn=149946890.38945.0000; current_captcha_type=Z; BIGipServerportal=3168010506.16671.0000";
        String errorMsg = "";
        String returnCode = "";
        boolean success = false;
        try {
            if (cookie.contains("失败")) {
                errorMsg = cookie.split("[|]")[1];
                returnCode = "1001";
            }
            JSONObject sbt = new JSONObject();
            sbt.put("cookie", cookie);
            sbt.put("type", "4");
            JSONObject re = TuNiuTrainAccountImpl.editPassengers(sbt);
            System.out.println(re);
            if (!re.getBooleanValue("success")) {
                errorMsg = re.getString("msg");
                returnCode = "1001";
            }
            else {
                JSONArray jsona = JSON.parseArray(re.getString("passengers"));
                success = true;
                returnCode = "231000";
                for (int i = 0; i < jsona.size(); ++i) {
                    JSONObject json = JSON.parseObject(jsona.getString(i));
                    String id = json.getString("code");
                    String name = json.getString("passenger_name");
                    int isUserSelf = 0;
                    int sex = 0;
                    if ((json.getString("isUserSelf").equals("Y")) || (json.getString("isUserSelf") == "Y"))
                        isUserSelf = 0;
                    else {
                        isUserSelf = 1;
                    }
                    if ("男".equals(json.getString("sex_name"))) {
                        sex = 0;
                    }
                    else {
                        sex = 1;
                    }
                    String birthday = json.getString("born_date");
                    String country = json.getString("country_code");
                    String identyType = passportTypeId(json.getString("passenger_id_type_code"));
                    String identy = json.getString("passenger_id_no");
                    int personType = Integer.parseInt(json.getString("passenger_type"));
                    String phone = json.getString("mobile_no");
                    String tel = json.getString("phone_no");
                    String email = json.getString("email");
                    String address = json.getString("address");
                    String total_times = json.getString("total_times");
                    String checkStatus = isCanGP(identyType, total_times);
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("id", Integer.valueOf(id));
                    jsonObject1.put("name", name);
                    jsonObject1.put("isUserSelf", Integer.valueOf(isUserSelf));
                    jsonObject1.put("sex", Integer.valueOf(sex));
                    jsonObject1.put("birthday", birthday);
                    jsonObject1.put("country", country);
                    jsonObject1.put("identyType", identyType);
                    jsonObject1.put("identy", identy);
                    jsonObject1.put("personType", Integer.valueOf(personType));
                    jsonObject1.put("phone", phone);
                    jsonObject1.put("tel", tel);
                    jsonObject1.put("email", email);
                    jsonObject1.put("address", address);
                    jsonObject1.put("checkStatus", checkStatus);
                    jsonArray.add(jsonObject1);
                }
            }
        }
        catch (Exception e) {
            returnCode = "1001";
            errorMsg = StringIsNull(errorMsg) ? "" : errorMsg;
            errorMsg = (errorMsg.equals("")) ? "系统异常" : errorMsg;
            success = false;
            e.printStackTrace();
        }
        jsonObject.put("success", success);
        jsonObject.put("returnCode", returnCode);
        jsonObject.put("errorMsg", errorMsg);
        jsonObject.put("data", jsonArray.toArray());
        return jsonObject;
    }

    /**
     * 增加和修改
     * @time 2015年11月20日16:18:34
     * @author QingXin
     **/
    public JSONObject AccountUpdateAndAdd(JSONObject Getjson) {
        WriteLog.write("途牛增改的传值", r1 + ":AccountUpdateAndAdd+Getjson1:" + Getjson);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String cookie = "JSESSIONID=0A01D96F981CA12799D5A8378CF5190D2F55BBA0A3; __NRF=E2D35CE8C2A305ABDCB892366D6C506A; BIGipServerotn=1876492554.38945.0000; current_captcha_type=Z";
        String errorMsg = "";
        String returnCode = "";
        boolean success = false;
        JSONObject Getjsons = JSON.parseObject(Getjson.getString("contacts"));
        String at = Getjsons.getString("id");
        try {
            if (cookie.contains("失败")) {
                errorMsg = cookie.split("[|]")[1];
                if ("0".equals(at)) {
                    returnCode = "1101";//添加失败返回的状态码
                }
                else {
                    returnCode = "1102";//修改失败返回的状态码
                }
            }
            JSONObject sbt = new JSONObject();
            sbt.put("cookie", cookie);
            WriteLog.write("途牛增改的传值cookie", r1 + ":AccountUpdateAndAdd+JSONObject+2:" + cookie);

            if ("0".equals(at)) {//0为添加乘客,大于0修改
                sbt.put("type", "1");
                sbt.put("name", Getjsons.getString("name"));
                sbt.put("identy", Getjsons.getString("identy"));
                sbt.put("identyType", Getjsons.getString("identyType"));
                sbt.put("sex", Getjsons.getString("sex"));
                sbt.put("birthday", Getjsons.getString("birthday"));
                sbt.put("country", Getjsons.getString("country"));
                sbt.put("personType", Getjsons.getString("personType"));
                sbt.put("phone", Getjsons.getString("phone"));
                sbt.put("tel", Getjsons.getString("tel"));
                sbt.put("email", Getjsons.getString("email"));
                sbt.put("address", Getjsons.getString("address"));
            }
            else {
                sbt.put("type", "3");
                sbt.put("id", Getjsons.getString("id"));
                sbt.put("name", Getjsons.getString("name"));
                sbt.put("identy", Getjsons.getString("identy"));
                sbt.put("identyType", Getjsons.getString("identyType"));
                sbt.put("sex", Getjsons.getString("sex"));
                sbt.put("birthday", Getjsons.getString("birthday"));
                sbt.put("country", Getjsons.getString("country"));
                sbt.put("personType", Getjsons.getString("personType"));
                sbt.put("phone", Getjsons.getString("phone"));
                sbt.put("tel", Getjsons.getString("tel"));
                sbt.put("email", Getjsons.getString("email"));
                sbt.put("address", Getjsons.getString("address"));
            }
            JSONObject re = TuNiuTrainAccountImpl.editPassengers(sbt);
            WriteLog.write("途牛增改的传值cookie", r1 + ":editPassengers+3+sbt:" + sbt);

            if (!re.getBooleanValue("success")) {
                success = false;
                if ("0".equals(at)) {
                    returnCode = "1101";
                }
                else {
                    returnCode = "1102";
                }

                errorMsg = re.getString("msg");//失败信息
            }
            else {
                returnCode = "231000";
                success = true;
            }
            System.out.println(re);
        }
        catch (Exception e) {
            WriteLog.write("途牛增改的传值cookie", r1 + ":Exception:" + e);
            success = false;

            if ("0".equals(at)) {
                returnCode = "1101";
            }
            else {
                returnCode = "1102";
            }
            errorMsg = (errorMsg.equals("")) ? "系统异常" : errorMsg;
            e.printStackTrace();
        }
        jsonObject.put("success", success);
        jsonObject.put("returnCode", returnCode);
        jsonObject.put("errorMsg", errorMsg);
        jsonObject.put("data", jsonArray.toArray());
        return jsonObject;
    }

    /**
     * 删除
     * @time 2015年11月20日16:18:34
     * @author QingXin
     **/
    public JSONObject Accountdelete(JSONObject Getjson) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String cookie = "JSESSIONID=0A02F047C4EBBE62075504062230245F2B849247AF; __NRF=5AD87AC8C815C381425C834F06B4A83D; BIGipServerotn=1206911498.50210.0000; current_captcha_type=Z";
        String errorMsg = "";
        String returnCode = "";
        boolean success = false;
        try {
            if (cookie.contains("失败")) {
                errorMsg = cookie.split("[|]")[1];
                returnCode = "1201";
            }
            else {
                String id = Getjson.getString("id");
                JSONObject sbt = new JSONObject();
                sbt.put("cookie", cookie);
                sbt.put("type", "2");
                sbt.put("id", id);
                JSONObject re = TuNiuTrainAccountImpl.editPassengers(sbt);
                if (re.getBooleanValue("success")) {
                    System.out.println("re=" + re);
                    returnCode = "231000";
                    success = true;
                }
                else {
                    success = false;
                    returnCode = "1201";
                    errorMsg = re.getString("msg");
                }
            }
        }
        catch (Exception e) {
            success = false;
            returnCode = "1201";
            errorMsg = (errorMsg.equals("")) ? "系统异常" : errorMsg;
            e.printStackTrace();
        }
        jsonObject.put("success", success);
        jsonObject.put("returnCode", returnCode);
        jsonObject.put("errorMsg", errorMsg);
        jsonObject.put("data", jsonArray.toArray());
        return jsonObject;
    }

    /**
     * 登录12306
     * @time 2015年11月20日16:18:34
     * @author QingXin
     **/
    public String AccountLogin(JSONObject Getjson) {
        String cookie = "";
        try {
            String logname = Getjson.getString("trainAccount");
            String logpassword = Getjson.getString("pass");
            // String url = "http://localhost:9004/Reptile/traininit";
            // String parm = "datatypeflag=12&logname=" + logname + "&logpassword=" + logpassword + "&mobile=";
            cookie = "  JSESSIONID=0A01D72AFCCADDFF417F6BD7D55C6611183DAC6F13; __NRF=47FBEDB9BE5B556676B9784A09A32C30; BIGipServerotn=718733578.64545.0000; current_captcha_type=Z";
            System.out.println(cookie);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return cookie;
    }

    /**
     * 证件转码
     * @time 2015年11月20日16:18:34
     * @author QingXin
     **/
    public static String passportTypeId(String passportType) {
        if (StringIsNull(passportType)) {//如果为空结果为 ""
            return "";
        }
        if (passportType.equals("二代身份证")) {
            passportType = "1";
        }
        else if (passportType.equals("一代身份证")) {
            passportType = "2";
        }
        else if (passportType.equals("港澳通行证")) {
            passportType = "C";
        }
        else if (passportType.equals("台湾通行证")) {
            passportType = "G";
        }
        else if (passportType.equals("护照")) {
            passportType = "B";
        }
        else if (passportType.equals("外国人居留证")) {
            passportType = "H";
        }
        return passportType;
    }

    /**
     * 证件转码
     * @time 2015年11月20日16:18:34
     * @author QingXin
     **/
    public static String passportTypeString(String passportType) {
        if (StringIsNull(passportType)) {//如果为空结果为 ""

            return "";
        }

        if (passportType.equals("1")) {
            passportType = "二代身份证";
        }
        else if (passportType.equals("2")) {
            passportType = "一代身份证";
        }
        else if (passportType.equals("C")) {
            passportType = "港澳通行证";
        }
        else if (passportType.equals("G")) {
            passportType = "台湾通行证";
        }
        else if (passportType.equals("B")) {
            passportType = "护照";
        }
        else if (passportType.equals("H")) {
            passportType = "外国人居留证";
        }
        return passportType;
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
    public String isCanGP(String id_type, String total_times) {
        String checkStatus = "1";
        if ("1".equals(id_type)) {
            int a = two_isOpenClick.length;
            for (int d = 0; d < a; d++) {
                if (two_isOpenClick[d].equals(total_times)) {
                    checkStatus = "0";
                    break;
                }
            }

        }
        else {
            int a = other_isOpenClick.length;
            for (int d = 0; d < a; d++) {
                if (other_isOpenClick[d].equals(total_times)) {
                    checkStatus = "0";
                    break;
                }
            }
        }
        return checkStatus;
    }

    /**字符串是否为空*/
    public static boolean StringIsNull(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }
}