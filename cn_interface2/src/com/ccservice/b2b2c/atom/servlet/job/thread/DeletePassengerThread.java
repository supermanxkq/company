package com.ccservice.b2b2c.atom.servlet.job.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.train.idmongo.IDModel;
import com.ccservice.b2b2c.atom.train.idmongo.MongoLogic;
import com.weixin.util.RequestUtil;

public class DeletePassengerThread extends Thread {

    private String id;

    private String repUrl;

    private String C_LOGINNAME;

    private String C_LOGPASSWORD;

    private String enDeleteAblePassengerMin;

    private final String[] two_isOpenClick = { "93", "95", "97", "99" };

    private final String[] other_isOpenClick = { "93", "98", "99", "91", "95", "97" };

    private MongoLogic mongologic = new MongoLogic();

    public DeletePassengerThread(String id, String repUrl, String C_LOGINNAME, String C_LOGPASSWORD,
            String enDeleteAblePassengerMin) {
        this.id = id;
        this.repUrl = repUrl;
        this.C_LOGINNAME = C_LOGINNAME;
        this.C_LOGPASSWORD = C_LOGPASSWORD;
        this.enDeleteAblePassengerMin = enDeleteAblePassengerMin;
    }

    public void run() {
        String cookie = rep12Method(C_LOGINNAME, C_LOGPASSWORD, repUrl);
        System.out.println(C_LOGINNAME + "--->" + cookie);
        if (cookie != null && cookie.contains("JSESSIONID")) {
            if (cookie.startsWith("http")) {
                cookie = "JSESSIONID" + cookie.split("JSESSIONID")[1].split("Encryption")[0];
                WriteLog.write("DeletePassengerThread", C_LOGINNAME + "--->" + cookie);
            }
            String data = "datatypeflag=106&cookie=" + cookie + "&enDeleteAblePassengerMin=" + enDeleteAblePassengerMin;
            String represult = RequestUtil.post(repUrl, data, "UTF-8", new HashMap<String, String>(), 0);
            WriteLog.write("DeletePassengerThread", C_LOGINNAME + "--->" + represult);
            refreshMongo(represult);
            int psum = passengerSum(represult);
            if (psum > 0) {
                Server.getInstance()
                        .getSystemService()
                        .findMapResultBySql(
                                "update T_CUSTOMERUSER set C_LOGINNUM = " + psum + ", C_MODIFYTIME = '"
                                        + ElongHotelInterfaceUtil.getCurrentTime() + "' where ID = " + id, null);
            }
        }
        else if (cookie != null && (cookie.contains("登录名不存在") || cookie.contains("该用户已被暂停使用"))) {
            int C_ISENABLE = 0;
            if (cookie.contains("登录名不存在")) {
                C_ISENABLE = 16;
                try {
                    mongologic.DelAccount(C_LOGINNAME);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (cookie.contains("该用户已被暂停使用")) {
                C_ISENABLE = 2;
                try {
                    mongologic.DelAccount(C_LOGINNAME);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (C_ISENABLE > 0) {
                Server.getInstance()
                        .getSystemService()
                        .excuteAdvertisementBySql(
                                "update T_CUSTOMERUSER set C_ISENABLE = " + C_ISENABLE + ", C_MODIFYTIME = '"
                                        + ElongHotelInterfaceUtil.getCurrentTime() + "' where ID = " + id);
            }
        }

    }

    /**
     * 获取12306返回的乘客数量
     * @param represult
     * @return
     */
    private int passengerSum(String represult) {
        JSONArray passengers = new JSONArray();
        int i_passengersum = 0;
        if (represult != null) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(represult);
                if (jsonObject.get("isSuccess") == null ? false : jsonObject.getBoolean("isSuccess")) {
                    passengers = jsonObject.getJSONArray("passengerdata");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            for (int i = 0; i < passengers.size(); i++) {
                //乘客
                JSONObject passenger = passengers.getJSONObject(i);
                //证件类型
                String passenger_type = passenger.getString("passenger_type");
                //12306证件核验code
                String passenger_id_type_code = passenger.getString("passenger_id_type_code");
                if (isCanGP(passenger_type, passenger_id_type_code)) {
                    i_passengersum++;
                }
            }
        }
        return i_passengersum;
    }

    /**
     * 全自动登录,返回cookie
     * 
     * @param logname 12306账号  
     * @param logpassword 12306密码
     * @return
     * @time 2014年12月19日 下午7:41:51
     * @author chendong
     */
    public String rep12Method(String logname, String logpassword, String repUrl) {
        String damarule = "4,4,4,4,4";//打码规则,逗号分隔顺序,数字详见DaMaCommon类
        String paramContent = "";
        paramContent = "logname=" + logname + "&logpassword=" + logpassword + "&damarule=" + damarule
                + "&datatypeflag=12";
        String resultString = "";
        resultString = SendPostandGet.submitPost(repUrl, paramContent, "utf-8").toString();
        return resultString;
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
    private boolean isCanGP(String id_type, String total_times) {
        if ("1".equals(id_type)) {
            int a = two_isOpenClick.length;
            for (int d = 0; d < a; d++) {
                if (two_isOpenClick[d].equals(total_times)) {
                    return true;
                }
            }
            return false;
        }
        else {
            int a = other_isOpenClick.length;
            for (int d = 0; d < a; d++) {
                if (other_isOpenClick[d].equals(total_times)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 将账号中乘客刷新到mongo中
     * @param loginname
     * @param represult
     * @author fiend
     */
    private void refreshMongo(String represult) {
        JSONArray passengers = new JSONArray();
        int i_passengersum = 0;
        if (represult != null) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(represult);
                if (jsonObject.get("isSuccess") != null && jsonObject.getBoolean("isSuccess")) {
                    passengers = jsonObject.getJSONArray("passengerdata");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<IDModel> idModelList = new ArrayList<IDModel>();
        for (int i = 0; i < passengers.size(); i++) {
            JSONObject passenger = passengers.getJSONObject(i);
            //证件类型
            String passenger_type = passenger.getString("passenger_type");
            //12306证件核验code
            String passenger_id_type_code = passenger.getString("passenger_id_type_code");
            if ("1".equals(passenger.getString("passenger_type")) && isCanGP(passenger_type, passenger_id_type_code)) {
                IDModel idModel = new IDModel(mongologic.GetLongFromString(passenger.getString("passenger_id_no")),
                        passenger.getString("passenger_name"), passenger.getIntValue("passenger_type"), C_LOGINNAME);
                idModelList.add(idModel);
            }
        }
        try {
            if (idModelList.size() > 0) {
                mongologic.RefreshMongoByCustomerUser(C_LOGINNAME, idModelList);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
