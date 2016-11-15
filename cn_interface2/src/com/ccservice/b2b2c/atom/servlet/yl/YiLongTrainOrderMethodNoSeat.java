package com.ccservice.b2b2c.atom.servlet.yl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MD5Util;

public class YiLongTrainOrderMethodNoSeat extends Thread {

    private Trainticket trainticket;

    private Trainorder trainorder;

    public YiLongTrainOrderMethodNoSeat(Trainticket trainticket, Trainorder trainorder) {
        this.trainticket = trainticket;
        this.trainorder = trainorder;
    }

    private String logname = "无座转换";

    public void run() {
        if (trainticket != null) {
            WriteLog.write(
                    logname + "_start",
                    trainorder.getId() + ":" + ";QunarOrdernumber()=" + trainorder.getQunarOrdernumber()
                            + ";Departure()-->" + trainticket.getDeparture() + ";Arrival()-->"
                            + trainticket.getArrival() + ";Trainno()-->" + trainticket.getTrainno()
                            + ";Departtime()-->" + trainticket.getDeparttime() + ";Price()--->"
                            + trainticket.getPrice() + ";Id()--->" + trainorder.getId()
                            + ";trainticket.getSeattype()-->" + trainticket.getSeattype());
            String fromTime = "";
            String fromStation = "";
            String toStation = "";
            String trainNum = "";
            float isNeedStandticketPrice = 0f;
            boolean flag = false;//是否从12306返回最低坐席
            String zwtype = "";//座位类别

            JSONObject allSurplusTrainTicketJson = null;
            try {
                fromTime = trainticket.getDeparttime() == null ? null
                        : trainticket.getDeparttime().contains(" ") ? trainticket.getDeparttime().split(" ")[0]
                                : trainticket.getDeparttime().length() == 10 ? trainticket.getDeparttime() : "";
                fromStation = trainticket.getDeparture();// 出发站
                toStation = trainticket.getArrival(); // 到达站
                trainNum = trainticket.getTrainno(); // 车次
                isNeedStandticketPrice = trainticket.getPrice(); // 价格
                for (int i = 0; i < 3; i++) {
                    WriteLog.write(logname + "_start",
                            "得值fromTime_exception-->trainorder.getId()=" + trainorder.getId() + ":"
                                    + ";trainorder.getQunarOrdernumber()=" + trainorder.getQunarOrdernumber()
                                    + ";trainorder.getOrdernumber()=" + trainorder.getOrdernumber()
                                    + ";querySurplusTrainticket(" + fromTime + "," + fromStation + "," + toStation
                                    + "," + trainNum + "," + isNeedStandticketPrice + ")");
                    try {
                        allSurplusTrainTicketJson = querySurplusTrainticket(trainorder.getOrdernumber(), fromTime,
                                fromStation, toStation, trainNum, isNeedStandticketPrice);
                    }
                    catch (Exception e) {
                        WriteLog.write("无座转换_excetion", "查询最低坐席=(" + fromTime + "," + fromStation + "," + toStation
                                + "," + trainNum + ")--->Ordernumber=" + this.trainorder.getOrdernumber() + ";zetype="
                                + zwtype + ";flag=" + flag);
                        ExceptionUtil.writelogByException("无座转换_ERROR", e);
                    }
                    flag = allSurplusTrainTicketJson.containsKey("success") ? allSurplusTrainTicketJson
                            .getBoolean("success") : false;
                    if (flag) {
                        zwtype = allSurplusTrainTicketJson.getString("zwtype");
                        break;
                    }
                }
            }
            catch (Exception e) {
                WriteLog.write(logname + "_start", "得值fromTime_exception-->trainorder.getId()=" + trainorder.getId()
                        + ":" + ";QunarOrdernumber()=" + trainorder.getQunarOrdernumber() + ";Ordernumber()="
                        + trainorder.getOrdernumber());
            }
            //只要票不是null就需要给一个备用坐席
            if (!flag || zwtype == null || "".equals(zwtype)) {
                zwtype = getSeat(trainNum);
            }
            trainticket.setSeattype(zwtype);
            WriteLog.write(logname, this.trainorder.getId() + ":zwtype=" + zwtype + ";isNeedStandticketPrice"
                    + isNeedStandticketPrice);
            //无座和备用坐席价格一致
            setTrainOrderExtSeatPrice(this.trainorder.getId(), isNeedStandticketPrice, isNeedStandticketPrice);
            for (Trainpassenger trainpassenger : this.trainorder.getPassengers()) {
                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                    String sql = " [dbo].[sp_T_TRAINTICKET_UPDATE] @ID=" + trainticket.getId() + ", @seatType='"
                            + zwtype + "'";
                    try {
                        Server.getInstance().getSystemService().findMapResultByProcedure(sql);
                    }
                    catch (Exception e) {
                        WriteLog.write("无座转换_ERROR", this.trainorder.getId() + "插入无座和备用坐席价格--->sql--->" + sql);
                        ExceptionUtil.writelogByException("无座转换_ERROR", e);
                    }
                }
            }
            activeMQroordering(trainorder.getId());
        }
        else {
            WriteLog.write(logname + "_ERROR_票为null", this.trainorder.getId() + "");
        }
    }

    /**
     * 把异步的订单扔进队列里 等待下单
     * 
     * @param id
     * @time 2015年2月10日 上午10:12:52
     * @author chendong
     */
    public void activeMQroordering(long id) {
        WriteLog.write("无座转换", "id--->" + id);
        String url = PropertyUtil.getValue("activeMQ_url", "Train.properties");
        String QUEUE_NAME = PropertyUtil.getValue("QueueMQ_trainorder_waitorder_orderid", "Train.properties");
        try {
            ActiveMQUtil.sendMessage(url, QUEUE_NAME, id + "");
        }
        catch (Exception e) {
            WriteLog.write("无座转换_发送下单MQ_ERROR", this.trainorder.getId() + "");
            ExceptionUtil.writelogByException("无座转换_发送下单MQ_ERROR", e);
            try {
                ActiveMQUtil.sendMessage(url, QUEUE_NAME, id + "");
            }
            catch (Exception e1) {
                WriteLog.write("无座转换_发送下单MQ_2_ERROR", this.trainorder.getId() + "");
                ExceptionUtil.writelogByException("无座转换_发送下单MQ_2_ERROR", e1);
            }
        }

    }

    /**
     * 将无座和最低坐席的价格存入备用坐席表 传值
     * 
     * @param orderid
     * @param extSeatPrice
     * @param isNeedStandticketPrice
     * @return
     * 
     *         时间:2016年4月14日 下午3:39:42
     * 
     *         fengfh
     */
    private void setTrainOrderExtSeatPrice(long orderid, float extSeatPrice, float isNeedStandticketPrice) {
        WriteLog.write(logname, "orderid-->" + orderid + ",extSeatPrice-->" + extSeatPrice
                + ",isNeedStandticketPrice-->" + isNeedStandticketPrice);
        createTrainOrderExtSeat(orderid, "[{\"0\":" + isNeedStandticketPrice + "}]");
    }

    /**
     * 创建火车备选坐席
     * 
     * @param orderid
     * @param extseats
     */
    private void createTrainOrderExtSeat(long orderid, String extseats) {
        WriteLog.write(logname, "orderid-->" + orderid + ",extseats-->" + extseats);
        String sql = "INSERT INTO TrainOrderExtSeat (OrderId ,ExtSeat ,ReMark) VALUES ( " + orderid + ",'" + extseats
                + "' ,'')";
        try {
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            WriteLog.write("无座转换_备选坐席_ERROR", orderid + "");
            ExceptionUtil.writelogByException("无座转换_备选坐席_ERROR", e);
        }
    }

    /**
     * 查询余票最低坐席
     * 
     * @param fromDate  出发日期 
     * @param fromStation   到达站
     * @param toStation 到达站
     * @param trainNum  车次
     * @param noSeatTicketPrice 无座价格
     * @return
     * 
     *         时间:2016年4月14日 下午3:38:24
     * 
     *         fengfh
     */
    public JSONObject querySurplusTrainticket(String orderNumber, String fromDate, String fromStation,
            String toStation, String trainNum, float noSeatTicketPrice) throws Exception {
        WriteLog.write(logname, orderNumber + ";querySurplusTrainticket()开始--->" + fromDate + "," + fromStation + ","
                + toStation + "," + trainNum + "," + noSeatTicketPrice);
        JSONObject jsonObject = new JSONObject();
        String fromStationThreeCode = Train12306StationInfoUtil.getThreeByName(fromStation);
        String toStationThreeCode = Train12306StationInfoUtil.getThreeByName(toStation);
        WriteLog.write(logname, orderNumber + ";获得三字吗-->fromStationThreeCode=" + fromStationThreeCode
                + ";toStationThreeCode=" + toStationThreeCode + "");
        jsonObject = queryLowestSeattype(orderNumber, fromDate, fromStationThreeCode, toStationThreeCode, trainNum,
                noSeatTicketPrice);
        WriteLog.write(logname, orderNumber + ";获得jsonobject-->jsonObject=" + jsonObject + "");
        if (!jsonObject.containsKey("success")) {
            jsonObject.put("success", false);
        }
        return jsonObject;
    }

    /**
     * 查询最低座票
     * 
     * @param fromDate
     * @param fromStation   三字码
     * @param toStation     三字码
     * @param trainNum
     * @param noSeatTicketPrice
     * @return
     * 
     *         时间:2016年4月14日 下午3:37:40
     * 
     *         fengfh
     */
    private JSONObject queryLowestSeattype(String orderNumber, String fromDate, String fromStation, String toStation,
            String trainNum, float noSeatTicketPrice) throws Exception {
        WriteLog.write(logname, "queryLowestSeattype()开始--->orderNumber:" + orderNumber + "==>" + fromDate + ","
                + fromStation + "," + toStation + "," + trainNum + "," + noSeatTicketPrice);
        JSONObject lowestTrainTicketSeattypeJson = new JSONObject();
        String allSurplusTrainTicket = queryTrainTicket(orderNumber, fromDate, fromStation, toStation, trainNum,
                noSeatTicketPrice);
        JSONObject trainoJson = getTrainTicketJson(orderNumber, allSurplusTrainTicket, trainNum);
        //flag表示是否有车  true 表示有车次返回 , false 表示没有车次
        boolean flag = trainoJson.isEmpty() ? false : true;
        lowestTrainTicketSeattypeJson.put("success", flag);
        if (flag) {
            String zwtype = getSurplusTrainTicket(trainoJson);
            lowestTrainTicketSeattypeJson.put("zwtype", zwtype);
            WriteLog.write(logname, orderNumber
                    + ":解析查询到的所有余票的String(json),得到确定车次的json成功-->lowestTrainTicketSeattypeJson"
                    + lowestTrainTicketSeattypeJson);
        }
        else {
            WriteLog.write(logname + "_error", orderNumber
                    + ":解析查询到的所有余票的String(json),得到确定车次的json失败-->lowestTrainTicketSeattypeJson"
                    + lowestTrainTicketSeattypeJson);
        }
        return lowestTrainTicketSeattypeJson;
    }

    /**
     * 查询余票
     * 
     * @param fromDate
     * @param fromStation
     * @param toStation
     * @param trainNum
     * @param noSeatTicketPrice
     * @return
     * 
     *         时间:2016年4月14日 下午3:37:28
     * 
     *         fengfh
     */
    private String queryTrainTicket(String orderNumber, String fromDate, String fromStation, String toStation,
            String trainNum, float noSeatTicketPrice) throws Exception {
        WriteLog.write(logname, orderNumber + ":queryTrainTicket()开始--->" + fromDate + "," + fromStation + ","
                + toStation + "," + trainNum + "," + noSeatTicketPrice);
        String method = "train_query";
        String partnerid = "hthy_test";
        String key = "2pUjUHRFSvWLWoUrfiWiZ813Be8f0IQI";
        String url = "http://searchtrain.hangtian123.net/trainSearch";// 正式查询地址
        String allTrainTicket = null;
        allTrainTicket = visitQuerySurplusTrainTicketInterface(orderNumber, fromDate, fromStation, toStation,
                partnerid, key, url, method);
        WriteLog.write(logname, orderNumber + ":查询余票：orderNumber" + orderNumber + ";allTrainTicket" + allTrainTicket);
        return allTrainTicket;
    }

    /**
     * 解析查询到的所有余票的String(json),得到确定车次的json
     * 
     * @param allTrainTicketJson
     * @param trainNum
     * @return
     * 
     *         时间:2016年4月14日 下午3:36:47
     * 
     *         fengfh
     */
    private JSONObject getTrainTicketJson(String orderNumber, String allTrainTicketJson, String trainNum)
            throws Exception {
        WriteLog.write(logname, orderNumber + "  ;getTrainTicketJson()==》" + allTrainTicketJson + "," + trainNum);
        JSONObject trainTicketJson = new JSONObject();
        if (allTrainTicketJson != null && !"".equals(allTrainTicketJson)) {
            JSONObject TrainTicketJson = new JSONObject();
            TrainTicketJson = JSONObject.parseObject(allTrainTicketJson);
            if (TrainTicketJson != null && TrainTicketJson.containsKey("data")) {
                JSONArray allTrainTicketJsonArray = TrainTicketJson.getJSONArray("data");
                if (allTrainTicketJsonArray != null) {
                    for (int i = 0; i < allTrainTicketJsonArray.size(); i++) {
                        trainTicketJson = allTrainTicketJsonArray.getJSONObject(i);
                        String trainCode = trainTicketJson.containsKey("train_code") ? trainTicketJson
                                .getString("train_code") : "-1"; // 车次
                        String[] trainnums = trainNum.split("/");
                        for (String trainnumeach : trainnums) {
                            if (trainCode.equals(trainnumeach)) {
                                trainTicketJson.put("success", true);
                                WriteLog.write(logname, orderNumber + ";  确定车次ok");
                                return trainTicketJson;
                            }
                        }
                    }
                }
            }
        }
        else {
            WriteLog.write(logname, this.trainorder.getId() + "==>所有余票json为空:allTrainTicketJson=" + allTrainTicketJson);
            trainTicketJson.put("success", false);
        }
        return trainTicketJson;
    }

    /**
     * 解析具体余票,得到最低坐席
     * 
     * @param trainJson
     * @return 时间:2016年4月14日 下午3:42:52 fengfh
     */
    private String getSurplusTrainTicket(JSONObject trainJson) {
        WriteLog.write("无座转换_getSurplusTrainTicket", this.trainorder.getId() + "trainJson" + trainJson);
        String zwtype = null;
        String train_type = null;

        if (trainJson != null && !trainJson.isEmpty()) {
            train_type = trainJson.getString("train_type");
            String gjrw_num = trainJson.getString("gjrw_num");
            String tdz_num = trainJson.getString("tdz_num");
            String ydz_num = trainJson.getString("ydz_num");
            String rw_num = trainJson.getString("rw_num");
            String yw_num = trainJson.getString("yw_num");
            String qtxb_num = trainJson.getString("qtxb_num");
            String edz_num = trainJson.getString("edz_num");
            String wz_num = trainJson.getString("wz_num");
            String rz_num = trainJson.getString("rz_num");
            String yz_num = trainJson.getString("yz_num");
            String swz_num = trainJson.getString("swz_num");

            if (!"--".equals(qtxb_num)) {
                zwtype = "其他席别";
            }
            else if (!"--".equals(yz_num)) {
                zwtype = "硬座";
            }
            else if (!"--".equals(rz_num)) {
                zwtype = "软座";
            }
            else if (!"--".equals(yw_num)) {
                zwtype = "硬卧";
            }
            else if (!"--".equals(rw_num)) {
                zwtype = "软卧";
            }
            else if (!"--".equals(gjrw_num)) {
                zwtype = "高级软卧";
            }
            else if (!"--".equals(edz_num)) {
                zwtype = "二等座";
            }
            else if (!"--".equals(ydz_num)) {
                zwtype = "一等座";
            }
            else if (!"--".equals(tdz_num)) {
                zwtype = "特等座";
            }
            else if (!"--".equals(swz_num)) {
                zwtype = "商务座";
            }
            else {
                zwtype = getSeat(train_type);
            }

        }

        return zwtype;
    }

    /**
     * 如果没有余票，按旧的无座转换
     * 
     * @param traino
     * @return
     * 
     *         时间:2016年4月14日 下午3:41:50
     * 
     *         fengfh
     */
    private String getSeat(String traino) {
        String i_st = "";
        if (traino.startsWith("D")) {
            i_st = "二等座";
        }
        else if (traino.startsWith("C")) {
            i_st = "软座";
        }
        else {
            i_st = "硬座";
        }
        return i_st;
    }

    /**
     * 访问接口(查询余票)
     * 
     * @time 2015年4月30日 下午2:30:51
     * @author chendong
     * @throws Exception
     */
    private String visitQuerySurplusTrainTicketInterface(String orderNumber, String train_date, String from_station,
            String to_station, String partnerid, String key, String url, String method) throws Exception {
        WriteLog.write(logname, orderNumber + "==》train_date:" + train_date + ";from_station:" + from_station
                + ";to_station:" + to_station + ";partnerid:" + partnerid + ";key:" + key + ";url:" + url + ";method:"
                + method);
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        JSONObject json1 = new JSONObject();
        json1.put("partnerid", partnerid);
        json1.put("method", method);
        json1.put("reqtime", reqtime);
        json1.put("sign", sign);
        json1.put("train_date", train_date);
        json1.put("from_station", Train12306StationInfoUtil.getThreeByName(from_station));
        json1.put("to_station", Train12306StationInfoUtil.getThreeByName(to_station));
        json1.put("purpose_codes", "ADULT");
        // json1.put("ischeck", "no");
        String paramContent = "jsonStr=" + json1.toJSONString();
        String resultString = null;
        try {
            resultString = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
        }
        catch (Exception e) {
            WriteLog.write("无座转换_ERROR_调用失败", this.trainorder.getId() + "--->url--->" + url + "?paramContent:"
                    + paramContent);
            ExceptionUtil.writelogByException("无座转换_ERROR_调用失败", e);
        }
        return resultString;
    }

    public String getreqtime() {
        SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    /**
     * md5(partnerid+method+reqtime+md5(key))，
     * 
     * @time 2014年12月12日 下午2:44:31
     * @author chendong
     */
    public String getsign(String partnerid, String method, String reqtime, String key) {
        // String keyString = partnerid + method + reqtime +
        // MD5Util.MD5Encode(key, "UTF-8");
        // keyString = MD5Util.MD5Encode(keyString, "UTF-8");
        WriteLog.write(logname, this.trainorder.getId() + "getsign==>" + partnerid + "," + method + "," + reqtime + ","
                + key);
        key = MD5Util.MD5Encode(key, "UTF-8");
        String jiamiqian = partnerid + method + reqtime + key;
        String sign = MD5Util.MD5Encode(jiamiqian, "UTF-8");

        return sign;
    }

    //    public static void main(String[] args) {
    //        String str = "";
    //        try {
    //            str = queryTrainTicket2("hehe", "2016-06-03", "北京西", "西安北", "T41", 155.5f);
    //        }
    //        catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //        System.out.println(str);
    //    }

}
