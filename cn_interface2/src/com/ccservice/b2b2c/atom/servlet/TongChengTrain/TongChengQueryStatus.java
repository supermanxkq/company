package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.TrainorderTimeUtil;

/**
 * 从DB查询订单状态
 */
public class TongChengQueryStatus {

    private final String logname = "高铁_查询订单状态";

    private final String errorlogname = "高铁_查询订单状态_error";

    public String queryTrainStatus(JSONObject json) {
        int random = new Random().nextInt();
        WriteLog.write(logname, random + "-->请求参数:" + json.toString());
        JSONObject resultJson = new JSONObject();
        String orderid = json.containsKey("orderid") ? json.getString("orderid") : "";
        String transactionid = json.containsKey("transactionid") ? json.getString("transactionid") : "";
        if (orderid == null || "".equals(orderid) || transactionid == null || "".equals(transactionid)) {
            resultJson.put("reqtoken", "");
            resultJson.put("orderamount", "0.00");
            resultJson.put("success", false);
            resultJson.put("code", 107);
            resultJson.put("msg", "业务参数缺失");
        }
        else {
            if (orderid.equals(transactionid)) {
                if (nightOrder(random, orderid)) {
                    resultJson.put("success", true);
                    resultJson.put("code", 100);
                    resultJson.put("msg", "处理或操作成功");
                    resultJson.put("orderid", orderid);
                    resultJson.put("status", 1);
                    WriteLog.write(logname, random + "-->" + resultJson.toString());
                    return resultJson.toString();
                }
                else {
                    resultJson.put("reqtoken", "");
                    resultJson.put("orderamount", "0.00");
                    resultJson.put("success", false);
                    resultJson.put("code", 108);
                    resultJson.put("msg", "错误的业务参数");
                    WriteLog.write(logname, random + "-->" + resultJson.toString());
                    return resultJson.toString();
                }
            }
            String status = "";
            //表示在trainorder表里已有此订单
            boolean isTrainOrder = false;
            String sql = "select C_STATE from T_TRAINORDERMSG  with(nolock) where C_KEY='" + orderid + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list != null && list.size() > 0) {
                Map map = (Map) list.get(0);
                long state = Long.valueOf(map.get("C_STATE").toString());
                String updatesql = "";
                if (state == 1) {
                    resultJson.put("success", true);
                    resultJson.put("code", 100);
                    resultJson.put("msg", "处理或操作成功");
                    resultJson.put("orderid", orderid);
                    resultJson.put("status", 1);
                    return resultJson.toJSONString();
                }
                else {
                    isTrainOrder = true;
                }
            }
            else {//夜间无此单，可能在trainorder表
                isTrainOrder = true;
            }
            if (isTrainOrder) {
                String trainorderID = getTrainOrderId(random, orderid, transactionid);
                WriteLog.write(logname, random + "-->trainorderID:" + trainorderID);
                if (trainorderID == null || "".equals(trainorderID)) {
                    resultJson.put("reqtoken", "");
                    resultJson.put("orderamount", "0.00");
                    resultJson.put("success", false);
                    resultJson.put("code", 108);
                    resultJson.put("msg", "错误的业务参数");
                    WriteLog.write(logname, random + "-->" + resultJson.toString());
                    return resultJson.toString();
                }
                Map traininfodataMap = getTrainorderstatus(random, trainorderID);
                WriteLog.write(logname, random + "-->traininfodataMap:" + traininfodataMap.toString());
                if (traininfodataMap.size() != 0) {
                    status = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERSTATUS");
                    String IsQuestionOrder = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ISQUESTIONORDER");
                    WriteLog.write("高铁问题订单记录", IsQuestionOrder);
                    if ("1".equals(status) || "2".equals(status) || "501".equals(status)) {
                        status = "1"; // 购票中 高铁要求购票中只返回状态
                        resultJson.put("success", true);
                        resultJson.put("code", 100);
                        resultJson.put("msg", "处理或操作成功");
                        resultJson.put("orderid", orderid);
                        resultJson.put("status", status);
                        WriteLog.write(logname, random + "-->" + resultJson.toString());
                        return resultJson.toString();
                    }
                    else if ("3".equals(status)) {
                        status = "2"; // 已出票
                    }
                    else {
                        int code;
                        String Msg = "";
                        String msg = getMsgByOrderId(random, trainorderID);
                        if (msg.indexOf("没有余票") > -1) {
                            code = 301;
                            Msg = "没有余票";
                        }
                        else if (msg.indexOf("已经预订") > -1) {
                            code = 305;
                            Msg = "乘客已经预订过该车次";
                        }
                        else if (msg.indexOf("身份") > -1) {
                            code = 308;
                            Msg = "乘客身份信息未通过验证订票失败";
                        }
                        else if (msg.indexOf("足够") > -1) {
                            code = 309;
                            Msg = "没有足够的票";
                        }
                        else if (msg.indexOf("其他订单行") > -1 || msg.indexOf("行程冲突") > -1) {
                            code = 310;
                            Msg = "本次购票与其他订单行程冲突";
                        }
                        else if (msg.indexOf("距离开车时间太近") > -1) {
                            code = 700;
                            Msg = "距离开车时间太近";
                        }
                        else if (msg.indexOf("价格不符") > -1) {
                            code = 801;
                            Msg = "价格不符";
                        }
                        else if (msg.indexOf("发车时间不符") > -1) {
                            code = 802;
                            Msg = "发车时间不符";
                        }
                        else if (msg.indexOf("该订单包含无座") > -1 || msg.indexOf("不接受无座") > -1) {
                            code = 803;
                            Msg = "该订单包含无座而客户不接受无座";
                        }
                        else {
                            code = 999;
                            Msg = "出票失败";
                        }
                        status = "3"; // 出票失败
                        resultJson.put("reqtoken", "");
                        resultJson.put("transactionid", transactionid);
                        resultJson.put("success", true);
                        resultJson.put("code", code);
                        resultJson.put("msg", Msg);
                        resultJson.put("orderid", orderid);
                        resultJson.put("status", status);
                        return resultJson.toString();
                    }
                    resultJson.put("status", status);
                    String reqtoken = gettrainorderinfodatabyMapkey(traininfodataMap, "C_CONTACTUSER");
                    resultJson.put("reqtoken", reqtoken);
                    String orderamount = gettrainorderinfodatabyMapkey(traininfodataMap, "C_TOTALPRICE");
                    resultJson.put("orderamount", orderamount);
                    String enrefundable = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ENREFUNDABLE");// 1：不可以退票
                                                                                                            // 0：可以退票
                    resultJson.put("refund_online", enrefundable);
                    Long trainorderid = Long.parseLong(trainorderID);
                    Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);
                    String checi = "";
                    String from_station_code = "";
                    String from_station_name = "";
                    String to_station_code = "";
                    String to_station_name = "";
                    String train_date = "";
                    String start_time = "";
                    String arrive_time = "";
                    String ordernumber = trainorder.getExtnumber();
                    String runtime = "";
                    JSONArray passengers = new JSONArray();
                    for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                        Trainpassenger trainpassenger = trainorder.getPassengers().get(i);
                        Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                        from_station_name = trainticket.getDeparture();
                        to_station_name = trainticket.getArrival();
                        train_date = trainticket.getDeparttime().split(" ")[0];
                        start_time = trainticket.getDeparttime() + ":00";
                        runtime = trainticket.getCosttime();
                        // int r1 = (int) (Math.random() * 10000);
                        try {
                            arrive_time = TrainorderTimeUtil.getArrivalTime(start_time, runtime);// 输入类车发车时间和运行时间，得到到达时间
                        }
                        catch (Exception e1) {
                        }
                        checi = trainticket.getTrainno();
                        JSONObject passengerjson = new JSONObject();
                        // passengerid int 乘客的顺序号
                        String passengerid = trainpassenger.getPassengerid() == null ? "0" : trainpassenger
                                .getPassengerid();
                        passengerjson.put("passengerid", passengerid);
                        // ticket_no string 票号（此票在本订单中的唯一标识，订票成功后才有值）
                        passengerjson.put("ticket_no", trainticket.getTicketno());
                        // passengersename string 乘客姓名
                        String name = trainpassenger.getName();
                        passengerjson.put("passengersename", name);
                        // passportseno string 乘客证件号码
                        passengerjson.put("passportseno", trainpassenger.getIdnumber());
                        // passporttypeseid string 证件类型ID
                        // 与名称对应关系:
                        // 1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通行证，B:护照
                        passengerjson.put("passporttypeseid",
                                TongchengTrainOrder.getIdtype12306(trainpassenger.getIdtype()));
                        // passporttypeseidname string 证件类型名称
                        String passporttypeseidname = trainpassenger.getIdtypestr();
                        passengerjson.put("passporttypeseidname", passporttypeseidname);
                        // piaotype string 票种ID。
                        // 与票种名称对应关系：
                        // 1:成人票，2:儿童票，3:学生票，4:残军票
                        passengerjson.put("piaotype", trainticket.getTickettype() + "");
                        // piaotypename string 票种名称
                        String piaotypename = trainticket.getTickettypestr() + "票";
                        passengerjson.put("piaotypename", piaotypename);
                        // zwcode string 座位编码。
                        // 与座位名称对应关系：
                        // 9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，
                        // 4:软卧，3:硬卧，2:软座，1:硬座
                        // 注意：当最低的一种座位，无票时，购买选择该座位种类，买下的就是无座(也就说买无座的席别编码就是该车次的最低席别的编码)，另外，当最低席别的票卖完了的时候才可以卖无座的票。
                        passengerjson.put("zwcode", TongchengTrainOrder.getzwname(trainticket.getSeattype()));
                        // zwname string 座位名称
                        String zwname = trainticket.getSeattype();
                        passengerjson.put("zwname", zwname);
                        try {
                            // cxin string 几车厢几座（在订票成功后才会有值）
                            String cxin = trainticket.getCoach() + "车厢," + trainticket.getSeatno().replace('号', '座');
                            passengerjson.put("cxin", cxin);
                        }
                        catch (Exception e) {
                        }
                        // price string 票价
                        passengerjson.put("price", trainticket.getPrice() + "");
                        // reason int 身份核验状态 0：正常 1：待审核 2：未通过
                        passengerjson.put("reason", trainpassenger.getAduitstatus());
                        passengers.add(passengerjson);
                    }
                    try {
                        from_station_code = Train12306StationInfoUtil.getThreeByName(from_station_name);// 获取车站名称对应三字码
                        to_station_code = Train12306StationInfoUtil.getThreeByName(to_station_name);// 获取车站名称对应三字码
                    }
                    catch (Exception e) {
                        System.out.println("错误的三字码:" + from_station_name + ":" + to_station_name);
                        System.out.println(e.fillInStackTrace());
                        WriteLog.write(errorlogname, random + "-->错误的三字码:" + from_station_name + ":" + to_station_name);
                    }

                    resultJson.put("from_station_name", from_station_name);
                    resultJson.put("from_station_code", from_station_code);
                    resultJson.put("to_station_name", to_station_name);
                    resultJson.put("to_station_code", to_station_code);
                    resultJson.put("train_date", train_date);
                    resultJson.put("start_time", start_time);
                    resultJson.put("arrive_time", arrive_time);
                    resultJson.put("ordernumber", ordernumber);
                    resultJson.put("runtime", runtime);
                    resultJson.put("checi", checi);
                    resultJson.put("passengers", passengers);
                    resultJson.put("success", true);
                    resultJson.put("code", 100);
                    resultJson.put("msg", "处理或操作成功");
                    resultJson.put("orderid", orderid);
                    resultJson.put("transactionid", transactionid);
                    resultJson.put("status", status);

                    WriteLog.write(logname, random + "-->" + resultJson.toString());
                    return resultJson.toString();
                }
                else {
                    resultJson.put("reqtoken", "");
                    resultJson.put("orderamount", "0.00");
                    resultJson.put("success", false);
                    resultJson.put("code", 108);
                    resultJson.put("msg", "错误的业务参数");
                    WriteLog.write(logname, random + "-->" + resultJson.toString());
                    return resultJson.toString();
                }

            }
            resultJson.put("reqtoken", "");
            resultJson.put("orderamount", "0.00");
            resultJson.put("success", false);
            resultJson.put("code", 108);
            resultJson.put("msg", "错误的业务参数");
            WriteLog.write(logname, random + "-->" + resultJson.toString());
            return resultJson.toString();

        }
        return resultJson.toString();

    }

    private String gettrainorderinfodatabyMapkey(Map map, String key) {
        String value = "";
        try {
            value = map.get(key).toString();
            if (value != null)
                return value;
        }
        catch (Exception e) {
        }
        return "";
    }

    private Map getTrainorderstatus(int random, String trainorderid) {
        Map map = new HashMap();
        String sql = "SELECT C_ISQUESTIONORDER,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                + " from T_TRAINORDER WITH (NOLOCK)  where ID=" + trainorderid;
        WriteLog.write(logname, random + "-->getTrainorderstatus_sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    private String getTrainOrderId(int random, String orderId, String transactionid) {
        Map map = new HashMap();
        String trainorderId = "";
        String sql = "SELECT ID FROM T_TRAINORDER WITH (NOLOCK)  WHERE C_QUNARORDERNUMBER = '" + orderId
                + "' AND C_ORDERNUMBER = '" + transactionid + "'";
        WriteLog.write(logname, random + "-->sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
            trainorderId = gettrainorderinfodatabyMapkey(map, "ID");
        }
        return trainorderId;
    }

    // 返回出票失败原因
    public String getMsgByOrderId(int random, String orderid) {
        String msg = "";
        Map map = new HashMap();
        String selectSql = "exec TrainOrderRefuseMsg_SelectByOrderId @OrderId=" + orderid;
        WriteLog.write(logname, random + "-->selectSql:" + selectSql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(selectSql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
            msg = gettrainorderinfodatabyMapkey(map, "Msg");
        }
        return msg;
    }

    public boolean nightOrder(int random, String orderid) {
        boolean istrue = false;
        String sql = "SELECT ID FROM T_TRAINORDERMSG WITH (NOLOCK) WHERE C_KEY='" + orderid + "'";
        WriteLog.write(logname, random + "-->sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            istrue = true;
        }
        return istrue;
    }
}
