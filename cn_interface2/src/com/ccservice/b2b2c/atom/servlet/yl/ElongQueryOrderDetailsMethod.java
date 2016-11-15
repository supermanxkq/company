package com.ccservice.b2b2c.atom.servlet.yl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainQueryInfo;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 艺龙先占座后支付模式订单查询
 * 
 * @time 2015年12月29日 下午7:48:17
 * @author W.C.L
 */
public class ElongQueryOrderDetailsMethod extends TrainSelectLoginWay {

    public JSONObject queryOrderDetails(String merchantId, String timeStamp, String orderId, String sign, int r1) {
        JSONObject resultJson = new JSONObject();
        JSONObject json = new JSONObject();
        InterfaceAccount interfaceAccount = getInterfaceAccount(merchantId);
        String key = interfaceAccount.getKeystr();

        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId;
        localSign = getSignMethod(localSign) + key;
        WriteLog.write("Elong_艺龙订单查询_ElongQueryOrderServlet", r1 + ":localSign:" + localSign);
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
            if (sign.equals(localSign)) {
                String sql = "SELECT ID,C_EXTORDERCREATETIME,C_ORDERNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='"
                        + orderId + "'";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    long trainorderId = Long.parseLong(map.get("ID").toString());
                    String transactionid = map.get("C_ORDERNUMBER").toString();
                    String holdingSeatSuccessTime = map.containsValue("C_EXTORDERCREATETIME") ? map.get(
                            "C_EXTORDERCREATETIME").toString() : "";

                    json.put("orderid", orderId);
                    json.put("transactionid", transactionid);
                    WriteLog.write("Elong_艺龙订单查询_ElongQueryOrderServlet", r1 + ":json:" + json.toString());
                    String result = new TongChengTrainQueryInfo().trainqueryinfo(json);
                    if (result.indexOf("查询订单成功") > -1) {
                        WriteLog.write("Elong_艺龙订单查询_ElongQueryOrderServlet", "tongchengQueryDataResult:" + result);
                        JSONObject orderDetail = tongchengConversionElongDataMethod(trainorderId,
                                holdingSeatSuccessTime, result);
                        resultJson.put("retcode", "200");
                        resultJson.put("retdesc", "成功");
                        resultJson.put("orderDetail", orderDetail);

                    }
                    else {
                        resultJson = JSONObject.parseObject(result);
                    }
                }
                else {
                    resultJson.put("retcode", "452");
                    resultJson.put("retdesc", "此订单不存在");
                }
            }
            else {
                resultJson.put("retcode", "403");
                resultJson.put("retdesc", "签名校验失败");
            }

        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ElongQueryOrderDetailsMethod_err", e, "艺龙查询接口异常");
        }

        return resultJson;
    }

    /**
     * 将同程查询订单数据转换成艺龙的
     * 
     * @param result
     * @return
     * @time 2015年12月31日 下午12:00:11
     * @author Administrator
     */
    private JSONObject tongchengConversionElongDataMethod(long trainorderId, String holdingSeatSuccessTime,
            String result) {
        JSONObject json = new JSONObject();
        JSONArray passengers = new JSONArray();
        JSONObject tongchengResult = JSONObject.parseObject(result);
        JSONArray ticketstatus = tongchengResult.getJSONArray("ticketstatus");

        for (int i = 0; i < ticketstatus.size(); i++) {
            JSONObject passJson = new JSONObject();
            JSONObject details = ticketstatus.getJSONObject(i);
            passJson.put("certNo", details.getString("idnumber")); // 证件号
            passJson.put("certType", details.getString("idtype")); // 证件类型
            passJson.put("name", details.getString("passengersename")); //姓名
            passJson.put("orderItemId", details.getString("orderItemId")); //票item号
            passJson.put("ticketType", details.getString("piaotypename")); //票类型
            passJson.put("seatNo", details.getString("cxin")); //坐席号
            passJson.put("price", details.getString("price")); //单张票的价格
            json.put("seatType", getzwnameByYlseatTypeCode(details.getString("zwname"))); // 坐席类型
            passengers.add(passJson);
        }
        json.put("passengers", passengers); // 乘客信息列表
        json.put("arrStation", tongchengResult.getString("fromstation")); // 始发站名
        json.put("contactName", tongchengResult.getString("contactName")); // 联系人姓名
        json.put("dptStation", tongchengResult.getString("tostation")); // 到达站名
        json.put("orderDate", tongchengResult.getString("orderDate")); // 下单时间
        json.put("orderId", tongchengResult.getString("orderid")); // 订单号
        json.put("ticketNo", tongchengResult.getString("ordernumber")); // 12306订单号

        json.put("ticketPrice", tongchengResult.getString("ticketPrice")); // 票价总额
        json.put("trainEndTime", tongchengResult.getString("arrivetime")); // 到站时间
        json.put("trainNo", tongchengResult.getString("checi")); // 车次
        json.put("trainStartTime", tongchengResult.getString("traintime")); // 发车时间
        json.put("payTimeDeadLine", addDateMinut(holdingSeatSuccessTime, PAYTIME_DEAD)); // 用户支付截止时间
        json.put("holdingSeatSuccessTime", holdingSeatSuccessTime); // 占座成功时间
        Map<String, Object> orderStatus = orderStatusDescConversion(tongchengResult.getString("orderstatusname"));
        json.put("orderStatus", orderStatus.get("orderStatus")); // 订单状态码（见4.5）
        json.put("orderStatusDesc", orderStatus.get("orderStatusDesc")); // 订单状态描述（见4.5）
        Map<String, Object> failureReasonMap = failureReasonConversion(getFailureReasonDesc(trainorderId));
        json.put("failureReason", failureReasonMap.get("failureReason")); // 订单失败码
        json.put("failureReasonDesc", failureReasonMap.get("failureReasonDesc")); // 订单失败原因

        return json;
    }

    /**
     * 获取下单失败原因
     * 
     * @param orderId
     * @return
     * @time 2015年12月31日 下午4:33:02
     * @author w.c.l
     */
    @SuppressWarnings("rawtypes")
    public String getFailureReasonDesc(long trainorderId) {
        String failureReasonDesc = "";
        String sql = "SELECT C_CONTENT FROM T_TRAINORDERRC WITH(NOLOCK) WHERE C_STATUS=2 and C_CREATEUSER='12306' AND C_ORDERID="
                + trainorderId;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            failureReasonDesc = map.containsValue("C_CONTENT") ? map.get("C_CONTENT").toString() : "";
        }
        return failureReasonDesc;
    }

    /**
     * 订单状态转换
     * 
     * @return
     * @time 2015年12月31日 下午2:17:51
     * @author w.c.l
     */
    public Map<String, Object> orderStatusDescConversion(String orderstatusname) {
        Map<String, Object> orderStatus = new HashMap<String, Object>();
        int statusCode = 0;

        if ("等待下单".equals(orderstatusname) || "正在下单".equals(orderstatusname)) {
            orderstatusname = "占座中";
            statusCode = 4;
        }
        else if ("下单失败".equals(orderstatusname)) {
            orderstatusname = "占座失败";
            statusCode = 8;
        }
        else if ("下单成功等待支付".equals(orderstatusname)) {
            orderstatusname = "占座成功";
            statusCode = 7;
        }
        else if ("下单成功支付中".equals(orderstatusname) || "支付审核中".equals(orderstatusname)) {
            orderstatusname = "出票中";
            statusCode = 12;
        }
        else if ("支付成功".equals(orderstatusname)) {
            orderstatusname = "出票成功";
            statusCode = 9;
        }
        else if ("支付失败".equals(orderstatusname)) {
            orderstatusname = "出票失败";
            statusCode = 11;
        }
        orderStatus.put("orderStatus", statusCode);
        orderStatus.put("orderStatusDesc", orderstatusname);
        return orderStatus;
    }

    /**
     * 失败理由转换
     * 
     * @param failureReasonDesc
     * @return
     * @time 2016年1月4日 下午1:21:13
     * @author w.c.l
     */
    public Map<String, Object> failureReasonConversion(String failureReasonDesc) {
        Map<String, Object> failureReasonMap = new HashMap<String, Object>();
        int failureReason = -1;
        if (failureReasonDesc.indexOf("没有余票") > -1 || failureReasonDesc.indexOf("此车次无票") > -1
                || failureReasonDesc.indexOf("已无余票") > -1 || failureReasonDesc.indexOf("没有足够的票") > -1
                || failureReasonDesc.indexOf("余票不足") > -1 || failureReasonDesc.indexOf("非法的席别") > -1) {

            failureReason = 1;
            failureReasonDesc = "所购买的车次坐席已无票";
        }
        else if (failureReasonDesc.indexOf("已订") > -1) {
            failureReason = 2;
            failureReasonDesc = "所购买的车次坐席已无票";
        }
        else if (failureReasonDesc.indexOf("身份验证失败") > -1) {
            failureReason = 6;
            failureReasonDesc = "12306乘客身份信息核验失败";
        }
        else if (failureReasonDesc.indexOf("多次打码失败") > -1 || failureReasonDesc.indexOf("获取12306账号失败") > -1) {
            failureReason = 8;
            failureReasonDesc = "12306服务错误";
        }
        else {
            failureReason = 0;
        }

        failureReasonMap.put("failureReason", failureReason);
        failureReasonMap.put("failureReasonDesc", failureReasonDesc);
        return failureReasonMap;
    }
    public static String getThreeCode(String stationName) throws Exception{
        String threeCode = getThreeCodeByName(stationName);
        return threeCode;
    }
    
    @SuppressWarnings("rawtypes")
    private static String getThreeCodeByName(String stationName) {
        stationName=stationName.trim();
        if(stationName==null||stationName.isEmpty()){
            return stationName;
        }
        String sql = "select Scode from StationName where SName='"+stationName+"';";
        List list = new ArrayList<String>();
        try{
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }catch(Exception e){
            WriteLog.write("获得三字码", stationName+":list="+list);
        }
        String sCode ="";
        if(!list.isEmpty()){
            Map map =(Map) list.get(0);
            sCode = String.valueOf(map.get("Scode"));
            return sCode;
        }
        WriteLog.write("获得三字码", stationName+";sCode="+sCode);
        return stationName;
    }
    
//    public static void main(String[] args) {
//        String name = "西安北";
////        String name = "西安北交";
//        try {
//            System.out.println(getThreeCode(name));
//        }
//        catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
}
