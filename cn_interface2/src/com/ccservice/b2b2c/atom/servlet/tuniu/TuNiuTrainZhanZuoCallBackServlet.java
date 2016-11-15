package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengCallBackServletUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengTrainOrder;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.job.Train12306StationInfo;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.TrainorderTimeUtil;

/**
 * Servlet implementation class TuNiuTrainZhanZuoCallBackServlet
 */
public class TuNiuTrainZhanZuoCallBackServlet {

    @SuppressWarnings("unused")
    
    private static final long serialVersionUID = -4207906210037472014L;

    public String resulta = "";

    public String zhanzuojieguoBackUrl;//占座结果回调url

    public TuNiuServletUtil tuNiuServletUtil;

    private String logName = "tuniu_3_2_4_2_占座回调接口";
    
    private TrainStudentInfo trainStudentInfo;

    public String ZhanZuoCallBack(JSONObject jsons) {
        String result = "";
        boolean is_refund_online = false;//是否是拉萨有关车次
        int r1 = new Random().nextInt(10000);

        String orderid = "";
        String transactionid = "";
        try {
            String param = jsons.toString();
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject obj = new JSONObject();
                obj.put("returnCode", "101");
                obj.put("errorMsg", "请求参数为空");
                result = obj.toString();
            }
            else {
                JSONObject json = JSONObject.parseObject(param);
                orderid = json.getString("orderid");
                transactionid = json.getString("transactionid");
                Long trainorderid = json.getLong("trainorderid");
                String unmatchedpasslist = json.getString("unmatchedpasslist");
                if (trainorderid > 0) { //如果大于0说明有订单号无论是异步的还是同步转异步的目前都是进入这里
                    String returnmsg = json.getString("returnmsg");
                    String interfacetype = json.getString("interfacetype");
                    if (json.containsKey("refund_online") && "1".equals(json.getString("refund_online"))) {
                        is_refund_online = true;
                    }
                    result = train_order_callback(trainorderid, returnmsg, r1, is_refund_online, interfacetype,
                            unmatchedpasslist, json.getIntValue("returncode"));
                }else{
                    WriteLog.write(logName, r1 + "参数异常--->transactionid: " + transactionid + ":orderid: " + orderid); 
                }
            }
        }
        catch (Exception e) {
            JSONObject obj = new JSONObject();
            obj.put("returnCode", "231099");
            obj.put("errorMsg", "未知异常");
            result = obj.toString();
            ExceptionUtil.writelogByException(logName, e, r1 + "");
        }
        return result;
    }

    private String train_order_callback(long trainorderid, String returnmsg, int r1, boolean is_refund_online,
            String interfacetype, String unmatchedpasslistStr, int returncode) {

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonstr = new JSONObject();
        String passportNo = "";
        Map traininfodataMap = getTrainorderstatus(1, trainorderid, "", "");//得到表中的一些信息
        Map dataMap = getcallbackurl(trainorderid, 1, "", "");
        WriteLog.write(logName, r1 + "--->" + trainorderid + ":traininfodataMap:" + traininfodataMap);
        int orderstatus = 0;
        try {
            orderstatus = Integer.parseInt(gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERSTATUS"));
            interfacetype = gettrainorderinfodatabyMapkey(traininfodataMap, "C_INTERFACETYPE");
        }
        catch (Exception e) {
        }
        boolean success = false;//        true:成功，false:失败
        boolean ordersuccess = false;
        int code = 100;//    int 4   状态编码
        String msg = "";//   1~256   提示信息
        String qunarordernumber = gettrainorderinfodatabyMapkey(traininfodataMap, "C_QUNARORDERNUMBER");
        jsonstr.put("orderId", qunarordernumber);
        String Ordernumber = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERNUMBER");
        jsonstr.put("vendorOrderId", Ordernumber);
        String Orderprice = gettrainorderinfodatabyMapkey(traininfodataMap, "C_TOTALPRICE");
        jsonstr.put("orderAmount", Orderprice);
        String transactionid = "";
        Trainorder trainorder = null;
        if (orderstatus == 1) {//等待支付说明占座成功
            trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);//根据ID查询交易单号
            transactionid = trainorder.getOrdernumber();//交易单号
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
                passportNo = trainpassenger.getIdnumber();
                try {
                    arrive_time = TrainorderTimeUtil.getArrivalTime(start_time, runtime);//输入类车发车时间和运行时间，得到到达时间
                }
                catch (Exception e1) {
                }
                checi = trainticket.getTrainno();
                JSONObject passengerjson = new JSONObject();
                String passengerid = trainpassenger.getPassengerid() == null ? "0" : trainpassenger.getPassengerid();
                passengerjson.put("passengerId", passengerid);
                passengerjson.put("ticketNo", trainticket.getTicketno());
                String name = TongChengCallBackServletUtil.geturlencode(trainpassenger.getName());
                passengerjson.put("passengerName", name);
                passengerjson.put("passportNo", trainpassenger.getIdnumber());
                passengerjson.put("passportTypeId", TongchengTrainOrder.getIdtype12306(trainpassenger.getIdtype()));
                String passporttypeseidname = TongChengCallBackServletUtil.geturlencode(trainpassenger.getIdtypestr());
                passengerjson.put("passportTypeName", passporttypeseidname);
                passengerjson.put("piaoType", trainticket.getTickettype() + "");
                String piaotypename = TongChengCallBackServletUtil.geturlencode(trainticket.getTickettypestr() + "票");
                passengerjson.put("piaoTypeName", piaotypename);
                passengerjson.put("zwCode", TongchengTrainOrder.getzwname(trainticket.getSeattype()));
                String zwname = TongChengCallBackServletUtil.geturlencode(trainticket.getSeattype());
                passengerjson.put("zwName", zwname);
                try {
                    String cxin = trainticket.getCoach() + "车厢," + trainticket.getSeatno().replace('号', '座');
                    cxin = TongChengCallBackServletUtil.geturlencode(cxin);
                    passengerjson.put("cxin", cxin);
                }
                catch (Exception e) {
                }
                passengerjson.put("price", trainticket.getPrice() + "");
                passengerjson.put("reason", trainpassenger.getAduitstatus());
                if(trainticket.getTickettype()==3){//学生票
                    trainStudentInfo=trainpassenger.getTrainstudentinfos().get(0);
                    passengerjson.put("provinceCode", trainStudentInfo.getSchoolprovincecode());
                    passengerjson.put("provinceName", trainStudentInfo.getSchoolprovince());
                    passengerjson.put("schoolCode", trainStudentInfo.getSchoolnamecode());
                    passengerjson.put("schoolName", trainStudentInfo.getSchoolname());
                    passengerjson.put("studentNo", trainStudentInfo.getStudentno());
                    passengerjson.put("schoolSystem", trainStudentInfo.getEductionalsystem());
                    passengerjson.put("enterYear", trainStudentInfo.getEntranceyear());
                    passengerjson.put("preferenceFromStationName", trainStudentInfo.getFromcity());
                    passengerjson.put("preferenceFromStationCode", trainStudentInfo.getFromcitycode());
                    passengerjson.put("preferenceToStationName", trainStudentInfo.getTocity());
                    passengerjson.put("preferenceToStationCode", trainStudentInfo.getTocitycode());
                }
                passengers.add(passengerjson);
            }
            try {
                from_station_code = Train12306StationInfo.GetValue(from_station_name);//获取车站名称对应三字码
                to_station_code = Train12306StationInfo.GetValue(to_station_name);//获取车站名称对应三字码
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(logName, e, r1 + "");
            }
            from_station_name = TongChengCallBackServletUtil.geturlencode(from_station_name);
            to_station_name = TongChengCallBackServletUtil.geturlencode(to_station_name);
            jsonstr.put("fromStationName", from_station_name);
            jsonstr.put("fromStationCode", from_station_code);
            jsonstr.put("toStationName", to_station_name);
            jsonstr.put("toStationCode", to_station_code);
            jsonstr.put("trainDate", train_date);
            jsonstr.put("startTime", start_time);
            jsonstr.put("arriveTime", arrive_time);
            jsonstr.put("orderNumber", ordernumber);
            jsonstr.put("cheCi", checi);
            jsonstr.put("passengers", passengers);
            success = true;
            code=23100;
            msg = "处理或操作成功";
        }
        else if (orderstatus == 8) {//取消订单交易关闭   说明没有占座成功
            transactionid = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERNUMBER");
            success = false;
            code = 999;
            String res = returnmsg;
            res = res == null ? "" : geturldecode(res);
            if (res.indexOf("style='color:red'") > 0) {
                res = chuliretrunmsg1(res, r1);
            }
            WriteLog.write(logName, trainorderid + ":res:" + res);
            Map infoMap = TongChengCallBackServletUtil.getMapInfo(res, returncode, dataMap);
            code = Integer.parseInt(infoMap.get("code") == null ? "0" : infoMap.get("code").toString());
            msg = infoMap.get("msg") == null ? "" : infoMap.get("msg").toString();
        }
        jsonstr.put("orderSuccess", success);
        String data;
        try {
            data = TuNiuDesUtil.encrypt(jsonstr.toString());
            jsonObject.put("data", data);
            WriteLog.write(logName, data);
        }
        catch (Exception e1) {
            ExceptionUtil.writelogByException(logName, e1, r1 + "");
        }
        jsonObject.put("returnCode", code);
        jsonObject.put("errorMsg", msg);
        String zhanzuojieguoBackUrl_temp = zhanzuojieguoBackUrl;
        String zhanzuojieguoBackUrl_temp_other = TongChengCallBackServletUtil
                .getValueByMap(dataMap, "C_ZHANZUOHUIDIAO");
        String reqtime = "";
        String ret = "-1";
        String sign = "";
        String key = "";
        Map map_data = getcallbackurl(0L, 2, qunarordernumber, transactionid);
        try {
            if (zhanzuojieguoBackUrl_temp_other != null && !"-1".equals(zhanzuojieguoBackUrl_temp_other)) {
                reqtime = tuNiuServletUtil.getCurrTime();
                jsonObject.put("timestamp", reqtime);
                String partnerid = TongChengCallBackServletUtil.getValueByMap(map_data, "C_USERNAME");
                Map map = tuNiuServletUtil.getInterfaceAccount(partnerid);
                jsonObject.put("account", partnerid);
                sign = SignUtil.generateSign(jsonObject.toString(), key);
                jsonObject.put("sign", sign);
                zhanzuojieguoBackUrl_temp = zhanzuojieguoBackUrl_temp_other;
            }
            WriteLog.write(logName, r1 + "--->" + zhanzuojieguoBackUrl_temp + "?" + jsonObject.toString());
            try {
                ret = SendPostandGet.submitPostMeiTuan(zhanzuojieguoBackUrl_temp, jsonObject.toString(), "utf-8")
                        .toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //成功
            WriteLog.write(logName, r1 + "--->" + trainorderid + "返回:" + ret);
            if ("success".equalsIgnoreCase(ret)) {
                return "success";
            }
            else {
                WriteLog.write(logName, r1 + "--->" + trainorderid + ":回调返回:" + ret);
                return "false";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Map getcallbackurl(Long orderid, int type, String orderid_no, String transactionid) {
        String key = "";
        Map map = new HashMap();
        String sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE ID=" + orderid;
        if (type == 2) {
            if (orderid == 0) {
                sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE C_QUNARORDERNUMBER='"
                        + orderid_no + "' and C_ORDERNUMBER='" + transactionid + "'";
            }
        }
        else if (type == 3) {
            sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE C_QUNARORDERNUMBER='"
                    + orderid_no + "' order by id desc";
        }
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=(" + sql_agentid + ")";
        WriteLog.write(logName, orderid + ":sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        else {
            sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE "
                    + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID="
                    + "(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE ID=" + orderid_no + ")";
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                map = (Map) list.get(0);
            }
        }
        WriteLog.write(logName, orderid + ":map:" + map);
        return map;
    }

    /**
     * 根据订单id获取 一些信息
     * 
     * @param type 1老的同程的 2新的美团的需要获取到信息判断接口类型然后回调
     * @param trainorderid
     * @return
     * @time 2015年1月22日 下午1:05:36
     * @author chendong
     * @param transactionid 
     * @param qunarOrderid 
     */
    private Map getTrainorderstatus(int type, Long trainorderid, String qunarOrderid, String transactionid) {
        Map map = new HashMap();
        String sql = "SELECT C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                + " from T_TRAINORDER where ID=" + trainorderid;
        if (type == 2) {
            if (transactionid != null) {
                sql = "SELECT ID,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                        + " from T_TRAINORDER where C_QUNARORDERNUMBER='"
                        + qunarOrderid
                        + "' and C_ORDERNUMBER='"
                        + transactionid + "'";
            }
            else {
                sql = "SELECT ID,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                        + " from T_TRAINORDER where C_QUNARORDERNUMBER='" + qunarOrderid + "' order by id desc";
            }
        }
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    private String geturlencode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    private String gettimeString(int type) {
        if (type == 1) {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
        else if (type == 2) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
        else {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
    }

    private String geturldecode(String oldstring) {
        try {
            oldstring = URLDecoder.decode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    /**
     * 处理一些字符信息
     * 
     * @param r1
     * @return
     * @time 2015年1月18日 上午11:13:28
     * @author Administrator
     */
    private String chuliretrunmsg1(String returnmsg, int r1) {
        WriteLog.write(logName, r1 + ":returnmsg:0:" + returnmsg);
        returnmsg = returnmsg.replace("订单填写页，", "").replace("订单填写页,", "").replace("校验订单信息失败：", "")
                .replace("校验订单信息失败:", "").replace("<span ", "").replace("style='color:red'>", "").replace("<i>", "")
                .replace("<b>", "").replace("</b>", "").replace("</i>", "").replace("</span>", "");
        WriteLog.write(logName, r1 + ":returnmsg:1:" + returnmsg);
        return returnmsg;
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
}