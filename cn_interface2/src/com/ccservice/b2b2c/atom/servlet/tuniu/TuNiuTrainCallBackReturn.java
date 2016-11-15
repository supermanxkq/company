package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;

/**
 * 途牛退票回调（新版）
 */
public class TuNiuTrainCallBackReturn {
    private final String logname = "tuniu_3_5_5_2_退票回调接口";

    private static final long serialVersionUID = 1L;

    public String partnerid;

    public String key;

    public String refuseMsg = "";

    /**
     * 途牛退票回调
     * @param json 请求json
     * @return 回调结果
     * @author 朱李旭
     */
    public String tuniuTrainReturn(JSONObject json, String agentid, String orderId) {
        int random = (int) (Math.random() * 1000000);
        TuNiuDesUtil TuNiuDesUtil = new TuNiuDesUtil();

        //------------------------------------------退票回调内容----------------------------------------------------------------

        String passengerid = "";
        WriteLog.write(logname, random + "--->json:" + json.toString());
        String result = "false";
        String vendorOrderId = json.containsKey("transactionid") ? json.getString("transactionid") : "";//合作伙伴方单号
        orderId = json.containsKey("orderId") ? json.getString("orderId") : orderId;//途牛订单号
        String orderNumber = json.containsKey("trainorderid") ? json.getString("trainorderid") : "";//火车票取票单号
        String reqtoken = json.containsKey("reqtoken") ? json.getString("reqtoken") : "";//退票特征  线上 线下
        String returnmoney = json.containsKey("returnmoney") ? json.getString("returnmoney") : "";//退款金额   线上为 总款
        String returnmsg = json.containsKey("returnmsg") ? json.getString("returnmsg") : "";//退票后消息描述（当returnstate=false时，需显示退票失败原因等）
        this.refuseMsg = returnmsg;
        String returntype = json.containsKey("returntype") ? json.getString("returntype") : "";//退票方式
        String ticket_no = json.getString("ticket_no");//票号
        String passengername = json.getString("passengername");//乘客姓名
        String passporttypeseid = json.getString("passporttypeseid");//证件类型
        String passportseno = json.getString("passengerid");//证件编号
        boolean returnsuccess = json.getBooleanValue("returnsuccess");//退票是否成功
        String returntime = json.getString("returnTime");//退票成功时间  成功才有值
        String returnfailid = json.getString("returnfailid");//失败愿意编号 失败才有值
        String returnfailmsg = json.getString("returnfailmsg");//失败原因内容 失败才有值
        boolean returnstate = json.containsKey("returnstate") ? json.getBoolean("returnstate") : true;//退票状态
        //        agentid = json.getString("agentid");//代理点id
        String refundTimeStamp = json.containsKey("refundTimeStamp") ? json.getString("refundTimeStamp") : "";//是否已退票
        String changeTimeStamp = json.containsKey("changeTimeStamp") ? json.getString("changeTimeStamp") : "";//改签
        boolean remarkTimeStamp = json.containsKey("remarkTimeStamp") ? json.getBoolean("remarkTimeStamp") : false;
        //模糊退
        boolean mohutui = json.containsKey("mohutui") ? json.getBooleanValue("mohutui") : false;
        //改签请求特征值
        String changereqtoken = json.containsKey("changereqtoken") ? json.getString("changereqtoken") : "";
        String transactionid = json.getString("transactionid") == null ? "" : json.getString("transactionid");
        passengerid = json.getString("passengerid") == null ? "" : json.getString("passengerid");
        WriteLog.write(logname, random + "--->" + returnmsg + ":map:" + orderId + "|passporttypeseid"
                + passporttypeseid + "|ticket_no" + ticket_no + "|passportseno" + passportseno + "|apiorderid"
                + orderId + "|returntype" + returntype + "remarkTimeStamp" + remarkTimeStamp);
        //当前
        long currentTime = System.currentTimeMillis();
        //时间戳
        String timestamp = String.valueOf(currentTime / 1000);
        //用原时间戳
        if (remarkTimeStamp) {
            //已操作过退票
            if (!ElongHotelInterfaceUtil.StringIsNull(refundTimeStamp)
                    && ("0".equals(returntype) || "1".equals(returntype))) {
                currentTime = Long.parseLong(refundTimeStamp);
                timestamp = String.valueOf(currentTime / 1000);
            }
            //已操作过改签
            else if (!ElongHotelInterfaceUtil.StringIsNull(changeTimeStamp)
                    && ("2".equals(returntype) || "3".equals(returntype))) {
                currentTime = Long.parseLong(changeTimeStamp);
                timestamp = String.valueOf(currentTime / 1000);
            }
        }
        Map map = getkeybyagentid(agentid);
        WriteLog.write(logname, random + "--->" + orderId + ":map:" + map);
        //退票回调地址
        String callbackurl = gettrainorderinfodatabyMapkey(map, "C_REFUNDCALLBACKURL");
        //请求途牛
        String interfacetype = gettrainorderinfodatabyMapkey(map, "C_INTERFACETYPE");
        String account = gettrainorderinfodatabyMapkey(map, "C_USERNAME");
        //车票退票信息，包括乘客和退票相关信息
        JSONArray returntickets = new JSONArray();
        //非模糊退
        WriteLog.write(logname, random + "--->callbackurl--->" + callbackurl + "--->interfacetype--->" + interfacetype
                + "--->account--->" + account + "--->agentid--->" + agentid + "--->passengerid--->" + passengerid);
        if (!mohutui) {
            JSONObject o1 = new JSONObject();
            o1.put("ticketNo", ticket_no);
            o1.put("passengerName", passengername);
            o1.put("passportTypeId", passporttypeseid);
            o1.put("passportNo", passportseno);
            o1.put("returnSuccess", returnsuccess);
            o1.put("returnMoney", returnmoney);
            o1.put("returnTime", returntime);
            o1.put("returnFailId", transformationCode(returnfailid));
            o1.put("returnFailMsg", returnfailmsg);
            if (passengerid == null || "".equals(passengerid)) {
                passengerid = json.getString("passengerid");
            }
            returntickets.add(o1);
            WriteLog.write(logname, random + "--->apiorderid:" + orderId + ":passengerid:" + passengerid
                    + "|passportseno" + passportseno);
        }
        //请求途牛
        WriteLog.write(logname, random + "--->" + orderId + ":map:" + map);
        String key = this.key;
        String partnerid = this.partnerid;
        String C_LOGINNAME = gettrainorderinfodatabyMapkey(map, "C_USERNAME");
        partnerid = C_LOGINNAME;
        key = gettrainorderinfodatabyMapkey(map, "C_KEY");
        try {
            JSONObject obj = new JSONObject();
            //0：表示线下退票退款  2：线下改签退款
            if ("0".equals(returntype) || "2".equals(returntype)) {
                callbackurl = gettrainorderinfodatabyMapkey(map, "RefundCallbackOfflineUrl");
                obj.put("vendorOrderId", vendorOrderId);
                obj.put("orderNumber", orderNumber);
                obj.put("orderId", orderId);
                obj.put("returnState", returnstate);
                obj.put("tickets", returntickets);
                obj.put("returnMoney", returnmoney);
                obj.put("returnMsg", returnmsg);
            }
            //1：表示线上退票退款  3：线上改签退款
            else/* if ("1".equals(returntype) || "3".equals(returntype))*/{
                obj.put("vendorOrderId", vendorOrderId);
                obj.put("orderNumber", orderNumber);
                obj.put("returnTickets", returntickets);
                obj.put("returnState", returnstate);
                obj.put("returnMoney", returnmoney);
                obj.put("returnMsg", returnmsg);
                obj.put("refundId", reqtoken);
            }
            WriteLog.write(logname, random + "--->obj:" + obj.toString());

            //            obj.put("returntype", returntype);
            //            obj.put("orderId", orderId);
            //            obj.put("vendorOrderId", vendorOrderId);
            //            obj.put("orderNumber", orderNumber);
            //            obj.put("returntickets", returntickets);
            //            obj.put("returnstate", returnstate);
            //            obj.put("returnmoney", returnmoney);
            //            obj.put("timestamp", timestamp);
            //            obj.put("returnmsg", returnmsg);
            String data = TuNiuDesUtil.encrypt(obj.toString());
            WriteLog.write(logname, random + "--->data:" + data);

            JSONObject json1 = new JSONObject();
            json1.put("account", account);
            json1.put("timestamp", timestamp);
            json1.put("data", data);
            json1.put("sign", SignUtil.generateSign(json1.toString(), key));
            //请求途牛
            WriteLog.write(logname, random + "--->" + orderId + ":partnerid:" + partnerid + ":key:" + key + ":"
                    + ":backurl:" + callbackurl + ":parm:" + json1.toString() + ":timeStamp:" + currentTime);
            String ret = SendPostandGet.submitPost(callbackurl, json1.toString(), "utf-8").toString();
            WriteLog.write(logname, random + "--->" + orderId + ":partnerid:" + partnerid + ":" + ":途牛返回:" + ret
                    + "remarkTimeStamp" + remarkTimeStamp);
            JSONObject resultJsonObject = new JSONObject();
            try {
                resultJsonObject = JSONObject.parseObject(ret);
            }
            catch (Exception e) {
            }
            //成功
            if ("success".equalsIgnoreCase(ret)
                    || (resultJsonObject.containsKey("success") && resultJsonObject.getBooleanValue("success"))) {
                if (remarkTimeStamp) {
                    result = "success@" + currentTime;//时间戳用于多次请求用同一个时间戳，勿修改
                }
                else {
                    result = "success";
                }

            }
            else {
                if (remarkTimeStamp) {
                    result = "false@" + currentTime;//时间戳用于多次请求用同一个时间戳，勿修改
                }
                else {
                    result = "false";
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WriteLog.write(logname, random + "--->" + orderId + ":result:" + result);
        return result;

    }

    public String errMsgCode() {
        String errMsgCode = "";
        if (this.refuseMsg.indexOf("距离开车时间太近无法退票") > -1) {
            errMsgCode = "140000," + this.refuseMsg;
        }
        else if (this.refuseMsg.indexOf("退票成功") > -1) {
            errMsgCode = "140001," + this.refuseMsg;
        }
        else if (this.refuseMsg.indexOf("退票失败") > -1) {
            errMsgCode = "140002," + this.refuseMsg;
        }
        else if (this.refuseMsg.indexOf("已取纸制票，不能退票") > -1) {
            errMsgCode = "140003," + this.refuseMsg;
        }
        else {
            errMsgCode = "231099," + this.refuseMsg;
        }
        return errMsgCode;
    }

    /**
    * 根据12306订单号找到这个接口用户的key和loginname
    * 
    * @param trainorderid
    * @return
    * @time 2015年3月30日 下午7:55:59
    * @author chendong
    */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getkeybyagentid(String agentid) {
        Map keymapbydb = null;
        try {
            keymapbydb = getkeybyagentidDB(agentid);
            //                Server.getInstance().setKeyMap(keyMap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return keymapbydb;

    }

    /**
     * 修改这个方法不用customeruser里的workphone了
     * 
     * @param agentid
     * @return
     * @time 2015年7月31日 下午1:02:02
     * @author chendong
     */
    private Map getkeybyagentidDB(String agentid) {
        Map map = new HashMap();
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE,C_REFUNDCALLBACKURL,RefundCallbackOfflineUrl "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=(" + agentid + ")";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 根据查到的map信息获取value
     * 
     * @param key
     * @time 2015年1月22日 下午1:08:54
     * @author chendong
     */
    private String gettrainorderinfodatabyMapkey(Map map, String key) {
        String value = "";
        if (map.get(key) != null) {
            try {
                value = map.get(key).toString();
            }
            catch (Exception e) {
            }
        }
        return value;
    }

    public String transformationCode(String returnfailid) {
        String Code = "";
        if (returnfailid.equals("")) {
            Code = "140001";
        }
        if (returnfailid.equals("1")) {
            Code = "99000100";
        }
        if (returnfailid.equals("2")) {
            Code = "99000101";
        }
        if (returnfailid.equals("3")) {
            Code = "99000102";
        }
        if (returnfailid.equals("4")) {
            Code = "99000103";
        }
        if (returnfailid.equals("5")) {
            Code = "140003";
        }
        if (returnfailid.equals("6")) {
            Code = "99000104";
        }
        return Code;
    }

}
