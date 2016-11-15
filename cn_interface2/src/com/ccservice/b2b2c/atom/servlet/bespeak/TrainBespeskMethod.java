package com.ccservice.b2b2c.atom.servlet.bespeak;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.tuniu.TuNiuTraintrainAccountGrab;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

public class TrainBespeskMethod {
    public JSONObject transferData(JSONObject json, int Interfacetype) {
        JSONObject jsonObject = new JSONObject();
        try {
            WriteLog.write("3.13_抢票接口", "json:" + json);
            Trainorder trainorder = new Trainorder();
            TuNiuTraintrainAccountGrab tuNiuTraintrainAccountGrab = new TuNiuTraintrainAccountGrab();
            String URL = PropertyUtil.getValue("TrainorderBespeakServelt_URL", "Train.properties");
            String resultString = "";
            if (Interfacetype == 8) {
                String qorderid = getParamByJsonStr("qorderid", json);//接口订单号
                int AgentId = getParamByJsonInt("AgentId", json);
                String from_station_name = getParamByJsonStr("from_station_name", json);//出发站名称
                String to_station_name = getParamByJsonStr("to_station_name", json);//到达站名称
                String train_codes = getParamByJsonStr("train_codes", json);//约票抢票的车次，以”,”隔开
                String start_date = getDate(getParamByJsonStr("start_date", json));//出发日期集合
                JSONArray jsonArray = json.getJSONArray("passengers");
                /** 托管账号 **/
                String LoginName12306 = getParamByJsonStr("LoginName12306", json);//12306用户名可以为null
                String LoginPassword12306 = getParamByJsonStr("LoginPassword12306", json);//12306用户密码可以为null
                String Cookies = getParamByJsonStr("Cookies", json);// 12306用户密码 对应的cookie
                String accountId = getParamByJsonStr("accountId", json);
                int Ordertype = getOrdertype(LoginName12306, LoginPassword12306, Cookies, accountId);
                trainorder.setAgentid(AgentId);// 代理ID
                trainorder.setCreateuid(AgentId);//
                List<Trainpassenger> passengerlist = tuNiuTraintrainAccountGrab.gettrainpassenger(jsonArray, null,
                        train_codes, to_station_name, start_date, from_station_name);
                trainorder.setPassengers(passengerlist);
                trainorder.setOrderstatus(Trainorder.WAITPAY);
                trainorder.setQunarOrdernumber(qorderid);
                trainorder.setOrderprice(0f);
                trainorder.setAgentprofit(0f);// 采购利润
                trainorder.setInterfacetype(8);
                trainorder.setCommission(0f);
                trainorder.setSupplyprice(0f);
                trainorder.setCreateuser("接口");
                trainorder.setPaymethod(4);
                trainorder.setState12306(Trainorder.WAITORDER);// 12306状态--等待下单
                trainorder.setOrdertype(Ordertype);
                trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
                json.put("trainOldorderid", trainorder.getId());
            }
            for (int i = 1; i <= 5; i++) {
                String prm = json.toString();
                try {
                    List list = Server
                            .getInstance()
                            .getSystemService()
                            .findMapResultBySql(
                                    "select C_AGENTID,C_INTERFACETYPE from T_INTERFACEACCOUNT with (nolock) where C_USERNAME='"
                                            + json.getString("partnerid") + "'", null);
                    if (list == null || list.size() == 0) {
                        jsonObject.put("success", true);
                        jsonObject.put("code", "104");
                        jsonObject.put("orderid", json.getString("qorderid"));
                        jsonObject.put("msg", "账户无效");
                        return jsonObject;
                    }
                    Map map = (Map) list.get(0);
                    if (!map.containsKey("C_AGENTID") || "".equals(map.get("C_AGENTID").toString())
                            || "0".equals(map.get("C_AGENTID").toString())) {
                        jsonObject.put("success", true);
                        jsonObject.put("code", "104");
                        jsonObject.put("orderid", json.getString("qorderid"));
                        jsonObject.put("msg", "账户无效");
                        return jsonObject;
                    }
                    json.put("AgentId", map.get("C_AGENTID").toString());
                    json.put("interfacetype", map.get("C_INTERFACETYPE").toString());
                }
                catch (Exception e1) {
                    ExceptionUtil.writelogByException("ERROR_Q_向trainorder_bespeak项目传递数据", e1);
                    jsonObject.put("success", true);
                    jsonObject.put("code", "104");
                    jsonObject.put("orderid", json.getString("qorderid"));
                    jsonObject.put("msg", "账户无效");
                    return jsonObject;
                }
                String paramContent = "jsonStr=" + URLEncoder.encode(json.toString(), "UTF-8");
                WriteLog.write("Q_向trainorder_bespeak项目传递数据", "qorderid:" + json.getString("qorderid") + ";jsonStr="
                        + json.toString());
                resultString = SendPostandGet.submitPost(URL, paramContent, "UTF-8").toString();
                if (resultString.equalsIgnoreCase("success")) {
                    WriteLog.write("Q_向trainorder_bespeak项目传递数据", "qorderid:" + json.getString("qorderid")
                            + ";向新接口传递数据  第" + i + "传送:" + resultString + " 成功！");
                    break;
                }
                WriteLog.write("Q_向trainorder_bespeak项目传递数据", "qorderid:" + json.getString("qorderid") + ";向新接口传递数据  第"
                        + i + "传送:" + resultString + " 失败！");
                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {
                }
            }
            WriteLog.write("Q_向trainorder_bespeak项目传递数据", "qorderid:" + json.getString("qorderid") + ";result:"
                    + resultString);
            if (resultString.equalsIgnoreCase("success")) {
                try {
                    jsonObject.put("success", true);
                    jsonObject.put("code", "100");
                    jsonObject.put("orderid", json.getString("qorderid"));
                    jsonObject.put("msg", "业务参数已获取");
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException("ERROR_Q_向trainorder_bespeak项目传递数据", e);
                    jsonObject.put("success", true);
                    jsonObject.put("code", "113");
                    jsonObject.put("orderid", json.getString("qorderid"));
                    jsonObject.put("msg", "当前时间不提供服务");
                }
            }
            else {
                jsonObject.put("success", true);
                jsonObject.put("code", "113");
                jsonObject.put("orderid", json.getString("qorderid"));
                jsonObject.put("msg", "当前时间不提供服务");
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_Q_向trainorder_bespeak项目传递数据", e);
            jsonObject.put("success", true);
            jsonObject.put("code", "113");
            jsonObject.put("orderid", json.getString("qorderid"));
            jsonObject.put("msg", "当前时间不提供服务");
        }
        return jsonObject;
    }

    /**
     * 通过json获取String型的数据
     * 
     * @param key
     * @param jsonObject
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public String getParamByJsonStr(String key, JSONObject jsonObject) {
        if (jsonObject == null || key == null || "".equals(key)) {
            return "";
        }
        if (!jsonObject.containsKey(key)) {
            return "";
        }
        return jsonObject.getString(key);
    }

    /**
     * 通过json获取float型的数据
     * 
     * @param key
     * @param jsonObject
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public float getParamByJsonFloat(String key, JSONObject jsonObject) {
        if (jsonObject == null || key == null || "".equals(key)) {
            return 10000;
        }
        if (!jsonObject.containsKey(key)) {
            return 10000;
        }
        return jsonObject.getFloatValue(key);
    }

    /**
     * 通过json获取int型的数据
     * 
     * @param key
     * @param jsonObject
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public int getParamByJsonInt(String key, JSONObject jsonObject) {
        return getParamByJsonIntDefout(key, jsonObject, 0);
    }

    /**
     * 通过json获取int型的数据
     * 
     * @param key
     * @param jsonObject
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public int getParamByJsonIntDefout(String key, JSONObject jsonObject, int defoutInt) {
        if (jsonObject == null || key == null || "".equals(key)) {
            return defoutInt;
        }
        if (!jsonObject.containsKey(key)) {
            return defoutInt;
        }
        return jsonObject.getIntValue(key);
    }

    /**
     * 通过json获取Long型的数据
     * 
     * @param key
     * @param jsonObject
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public long getParamByJsonLong(String key, JSONObject jsonObject) {
        if (jsonObject == null || key == null || "".equals(key)) {
            return 0;
        }
        if (!jsonObject.containsKey(key)) {
            return 0;
        }
        return jsonObject.getLongValue(key);
    }

    /**
     * 通过json获取Boolean型的数据
     * 
     * @param key
     * @param jsonObject
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public boolean getParamByJsonBoolean(String key, JSONObject jsonObject) {
        if (jsonObject == null || key == null || "".equals(key)) {
            return false;
        }
        if (!jsonObject.containsKey(key)) {
            return false;
        }
        return jsonObject.getBoolean(key);
    }

    private static String getDate(String a) {
        String[] arr = a.split(",");
        a = "";
        for (int i = 0; i < arr.length; i++) {
            a += formatDate(arr[i].trim()) + ",";
        }
        a = a.substring(0, a.length() - 1);
        return a;
    }

    public static String formatDate(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sfstr;
    }

    /**
     * 1、线上 订单
     * 2、线上 订单使用客户 账号下单
     * 3、线上 订单使用客户 账号下单 cookie方式下单
     * 6、线上 订单使用客户 账号下单 帐号密码需解密，解密后下单
     * @param username
     * @param userpassword
     * @param cookie
     * @return
     * @time 2015年12月3日 下午3:47:22
     * @author QingXin
     */
    public static int getOrdertype(String username, String userpassword, String cookie, String accountId) {
        int ordertype = 1;
        try {
            if (!StringIsNull(username) && !StringIsNull(userpassword) && StringIsNull(accountId)) {
                ordertype = 3;
            }
            else if (!StringIsNull(accountId) && !StringIsNull(username) && !StringIsNull(userpassword)) {
                ordertype = 6;
            }
            else if (!StringIsNull(cookie)) {
                ordertype = 4;
            }
        }
        catch (Exception e) {
        }
        return ordertype;
    }

    public static boolean StringIsNull(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }
}
