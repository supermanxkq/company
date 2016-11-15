package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.bcel.generic.NEW;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;

/**
 * 途牛改签回调接口
 * @time 2015年11月19日 下午5:43:30
 * 朱李旭
 **/
public class TuNiuTrainCallBackChange {

    public String partnerid;

    public String key;

    /**
     * 异步改签回调
     * @param json 请求json
     * @param type 1:请求改签，2:确认改签
     * @return 回调结果
     * @author 朱李旭
     */
    public String tuniuTrainChange(JSONObject json, String orderid, String agentId, int type) {
        JSONObject object = new JSONObject();
        JSONObject newTicketobject = new JSONObject();
        JSONArray newTicketArray = new JSONArray();
        JSONObject dataJson = new JSONObject();
        //结果
        String result = "";
        //日志名称
        String logName = "t途牛火车票接口_异步改签回调";
        if (type == 1) {
            logName = "t途牛火车票接口_4.12.改签占座回调";
        }
        else if (type == 2) {
            logName = "t同途牛火车票接口_4.14.改签确认回调";
        }
        try {
            //回调地址
            String callBackUrl = json.getString("callBackUrl");
            //移除属性
            json.remove("agentId");
            json.remove("callBackUrl");
            //获取KEY
            String key = this.key;
            String partnerid = this.partnerid;
            String reqJsonString = json.toJSONString();
            try {
                reqJsonString = URLDecoder.decode(reqJsonString, "UTF-8");
                json = JSONObject.parseObject(reqJsonString);
            }
            catch (Exception e) {
            }
            //拼参数
            object.put("account", partnerid);
            //请求时间
            String reqtime = gettimeString(2);
            object.put("timestamp", reqtime);
            WriteLog.write(logName, orderid + ":callBackUrl:" + callBackUrl + ":partnerid=" + partnerid + ":reqtime="
                    + reqtime + ":key=" + key);
            //数字签名
            object.put("sign", ElongHotelInterfaceUtil.MD5(partnerid + reqtime + ElongHotelInterfaceUtil.MD5(key)));
            String msg = json.containsKey("msg") ? json.getString("msg") : "";
            int returnCode = json.containsKey("success") ? json.getIntValue("code") : 0;
            boolean changeSuccess = json.getBooleanValue("success");
            if (type == 1) {
                returnCode = TuNiuChangeCodeMethod.changeCallBackCodeV1(returnCode, msg, changeSuccess);
                if (returnCode == 1600108) {
                    msg = msg.split("车票已改签")[0] + "车票已改签";
                }
            }
            else if (type == 2) {
                returnCode = TuNiuChangeCodeMethod.changeConfirmCodeV1(returnCode, changeSuccess);
            }
            object.put("returnCode", returnCode);
            object.put("errorMsg", msg);
            String vendorOrderId = json.containsKey("transactionid") ? json.getString("transactionid") : "";
            String orderId = json.containsKey("orderid") ? json.getString("orderid") : "";
            String changeId = json.containsKey("reqtoken") ? json.getString("reqtoken") : "";
            dataJson.put("vendorOrderId", vendorOrderId);
            dataJson.put("orderId", orderId);
            dataJson.put("changeId", changeId);
            if (type == 1) {
                //查询订单
                Trainform trainform = new Trainform();
                trainform.setQunarordernumber(orderId);
                trainform.setOrdernumber(vendorOrderId);
                List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
                Trainorder trainorder = new Trainorder();
                if (orders.size() > 0) {
                    trainorder = orders.get(0);
                }
                String priceChangeTypeInfo = json.containsKey("priceinfo") ? json.getString("priceinfo") : "";
                String ordernumber = trainorder.getExtnumber();//订单12306票号
                String cheCi = json.containsKey("checi") ? json.getString("checi") : "";
                String fromStationCode = json.containsKey("from_station_code") ? json.getString("from_station_code")
                        : "";
                String fromStationName = json.containsKey("from_station_name") ? json.getString("from_station_name")
                        : "";
                String toStationCode = json.containsKey("to_station_code") ? json.getString("to_station_code") : "";
                String toStationName = json.containsKey("to_station_name") ? json.getString("to_station_name") : "";
                String trainDate = json.containsKey("train_date") ? json.getString("train_date") : "";
                String startTime = json.containsKey("start_time") ? json.getString("start_time") : "";
                String arriveTime = json.containsKey("arrive_time") ? json.getString("arrive_time") : "";
                int priceChangeType = json.containsKey("priceinfotype") ? json.getIntValue("priceinfotype") : 0;
                double priceDifference = json.containsKey("pricedifference") ? json.getDoubleValue("pricedifference")
                        : 0.00;
                double diffRate = json.containsKey("diffrate") ? json.getDoubleValue("diffrate") : 0.00;
                double totalPriceDiff = json.containsKey("totalpricediff") ? json.getDoubleValue("totalpricediff")
                        : 0.00;
                int fee = json.containsKey("fee") ? json.getIntValue("fee") : 0;
                dataJson.put("ordernumber", ordernumber);
                dataJson.put("changeSuccess", changeSuccess);
                dataJson.put("cheCi", cheCi);
                dataJson.put("fromStationCode", fromStationCode);
                dataJson.put("fromStationName", fromStationName);
                dataJson.put("toStationCode", toStationCode);
                dataJson.put("toStationName", toStationName);
                dataJson.put("trainDate", trainDate);
                dataJson.put("startTime", startTime);
                dataJson.put("arriveTime", arriveTime);
                dataJson.put("priceChangeType", priceChangeType);
                dataJson.put("priceDifference", priceDifference);
                dataJson.put("diffRate", diffRate);
                if (priceChangeTypeInfo.contains("收取新票款")) {
                    dataJson.put("totalPriceDiff", 0);
                }
                else {
                    dataJson.put("totalPriceDiff", totalPriceDiff);
                }
                dataJson.put("fee", fee);
                dataJson.put("priceChangeTypeInfo", priceChangeTypeInfo);
                JSONArray array = json.containsKey("newtickets") ? json.getJSONArray("newtickets") : null;
                List<Trainpassenger> trainpassengers = trainorder.getPassengers();
                if (array.size() > 0) {
                    for (int i = 0; i < array.size(); i++) {

                        JSONObject jsonObject = JSONObject.parseObject(array.get(i).toString());
                        String zwName = jsonObject.containsKey("zwname") ? jsonObject.getString("zwname") : "";
                        double price = jsonObject.containsKey("price") ? jsonObject.getDoubleValue("price") : 0.00;
                        String oldTicketNo = jsonObject.containsKey("old_ticket_no") ? jsonObject
                                .getString("old_ticket_no") : "";
                        String newTicketNo = jsonObject.containsKey("new_ticket_no") ? jsonObject
                                .getString("new_ticket_no") : "";
                        String zwCode = jsonObject.containsKey("zwcode") ? jsonObject.getString("zwcode") : "";
                        String cxin = jsonObject.containsKey("cxin") ? jsonObject.getString("cxin") : "";
                        String passprotNo = jsonObject.containsKey("passportseno") ? jsonObject
                                .getString("passportseno") : "";
                        int piaoType = jsonObject.containsKey("piaotype") ? jsonObject.getIntValue("piaotype") : 1;
                        newTicketobject.put("zwName", zwName);
                        newTicketobject.put("price", price);
                        newTicketobject.put("oldTicketNo", oldTicketNo);
                        newTicketobject.put("newTicketNo", newTicketNo);
                        newTicketobject.put("zwCode", zwCode);
                        newTicketobject.put("cxin", cxin);
                        newTicketobject.put("passprotNo", passprotNo);
                        newTicketobject.put("piaoType", piaoType);
                        for (int j = 0; j < trainpassengers.size(); j++) {
                            Trainpassenger trainpassenger = trainpassengers.get(j);
                            String idNumber = trainpassenger.getIdnumber();
                            if (idNumber.equals(passprotNo)) {
                                long passengerId = jsonObject.containsKey("passportseno") ? jsonObject
                                        .getLongValue("passportseno") : 0;
                                int reason = trainpassenger.getAduitstatus();//核验状态
                                String passprotTypeName = trainpassenger.getIdtypestr();//证件名称
                                String passengerName = trainpassenger.getName();//姓名
                                int passportTypeId = trainpassenger.getIdtype();//证件类型ID
                                String piaoTypeName = getTickettypestr(piaoType);//票类型名称
                                newTicketobject.put("passengerId", passengerId);
                                newTicketobject.put("reason", reason);
                                newTicketobject.put("passprotTypeName", passprotTypeName);
                                newTicketobject.put("passengerName", passengerName);
                                newTicketobject.put("passportTypeId", passportTypeId);
                                newTicketobject.put("piaoTypeName", piaoTypeName);
                                break;
                            }
                        }
                        newTicketArray.add(newTicketobject);
                    }
                }
                dataJson.put("newTicketInfos", newTicketArray);
                object.put("data", TuNiuDesUtil.encrypt(dataJson.toString()));//加密
            }
            else {
                object.put("data", dataJson);
            }
            //记录日志
            WriteLog.write(logName, orderid + ":callBackUrl:" + callBackUrl + ":param:backjson=" + object);
            String reqjsonString = object.toJSONString();
            WriteLog.write(logName, orderid + ":reqjsonString:" + reqjsonString);
            //连续通知5次，直到成功
            String backResult = "";
            for (int i = 1; i <= 5; i++) {
                if (i > 1) {
                    Thread.sleep(15000l);
                }
                //请求代理商
                //                backResult = RequestUtil.post(callBackUrl, "backjson=" + reqjson, "UTF-8",
                //                        new HashMap<String, String>(), 0);//这个方法有乱码
                //中兴商旅>>Content-Type>>application/x-www-form-urlencoded
                //                if (partnerid.contains("shanglvelutong")) {
                //                    backResult = SendPostandGet.submitPostMeiTuan(callBackUrl, "backjson=" + reqjsonString, "UTF-8")
                //                            .toString();
                //                }
                //                else {
                backResult = SendPostandGet.submitPost(callBackUrl, "backjson=" + reqjsonString, "UTF-8").toString();
                //                }
                //记录日志
                WriteLog.write(logName, orderid + ":第" + i + "次回调返回:" + backResult);
                //成功
                if ("success".equalsIgnoreCase(backResult)) {
                    break;
                }
            }
            //成功
            if ("success".equalsIgnoreCase(backResult)) {
                result = "success";
            }
            else {
                result = "连续通知异常5次,停止通知,需人工介入处理"
                        + (ElongHotelInterfaceUtil.StringIsNull(backResult) ? "" : "---" + backResult);
            }
        }
        catch (Exception e) {
            WriteLog.write(logName, orderid + ":Exception:" + ElongHotelInterfaceUtil.errormsg(e));
        }
        return result;
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
        if (Server.getInstance().getKeyMap().get(agentid) == null) {
            Map keymapbydb = null;
            try {
                Map keyMap = Server.getInstance().getKeyMap();
                keymapbydb = getkeybyagentidDB(agentid);
                keyMap.put(agentid, keymapbydb);
                //                Server.getInstance().setKeyMap(keyMap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return keymapbydb;
        }
        else {
            return Server.getInstance().getKeyMap().get(agentid);
        }

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
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE,C_REFUNDCALLBACKURL "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=(" + agentid + ")";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    private String gettimeString(int type) {
        if (type == 1) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
        else if (type == 2) {
            return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        }
        else {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
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

    /**
     * 1:成人票，2:儿童票，3:学生票，4:残军票
     * 
     * @return
     * @time 2014年12月24日 下午12:01:54
     * @author zlx
     * @return 票类型名称
     */
    public String getTickettypestr(int tickettype) {
        switch (tickettype) {
        case 1:
            return "成人";
        case 2:
            return "儿童";
        case 3:
            return "学生";
        case 4:
            return "残军";
        }
        return "";
    }

}
