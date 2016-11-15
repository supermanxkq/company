package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.MQ.TrainpayMqMSGUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * Servlet implementation class TaoBaoChangeCallbackServlet
 */
public class TaoBaoChangeCallbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @SuppressWarnings({ "rawtypes", "unused" })
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String result = "false";
        String callbackUrl = PropertyUtil.getValue("TaoBao_Change_CallBack_Url", "Train.properties");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        int r1 = new Random().nextInt(10000);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer sb = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String param = sb.toString();
            if (!ElongHotelInterfaceUtil.StringIsNull(param)) {
                WriteLog.write("淘宝火车异步_回调", r1 + ":" + param);
                JSONObject jsonObject = JSONObject.parseObject(param);
                //请求特征值[异步改签时的同步输出和异步回调有值]
                String reqtoken = jsonObject.containsKey("reqtoken") ? jsonObject.getString("reqtoken") : "";
                //是否改签占座成功
                boolean success = jsonObject.containsKey("success") ? jsonObject.getBoolean("success") : false;
                //消息代码成功：100
                String code = jsonObject.containsKey("code") ? jsonObject.getString("code") : "";
                //消息描述
                String msg = jsonObject.containsKey("msg") ? jsonObject.getString("msg") : "";
                //客户端账号（非空，开放平台登录账号）
                String partnerid = jsonObject.containsKey("partnerid") ? jsonObject.getString("partnerid") : "";
                //请求时间，格式：yyyyMMddHHmmss（非空）例：20140101093518
                String reqtime = jsonObject.containsKey("reqtime") ? jsonObject.getString("reqtime") : "";
                //数字签名=md5(partnerid+reqtime+md5(key)),其中 key 即是由 开放平台分配给同程的。md5 算 法得到的字符串全部为小写
                String sign = jsonObject.containsKey("sign") ? jsonObject.getString("sign") : "";
                //操作功能名（非空）
                String method = jsonObject.containsKey("method") ? jsonObject.getString("method") : "";
                //交易单号
                String transactionid = jsonObject.containsKey("transactionid") ? jsonObject.getString("transactionid")
                        : "";
                //同程订单号
                String orderid = jsonObject.containsKey("orderid") ? jsonObject.getString("orderid") : "";
                //改签后的新车票信息
                JSONArray newtickets = jsonObject.containsKey("newtickets") ? jsonObject.getJSONArray("newtickets")
                        : new JSONArray();
                //改签票款异动类型编号 ,改签失败时为0
                int priceinfotype = jsonObject.containsKey("priceinfotype") ? jsonObject.getIntValue("priceinfotype")
                        : 0;
                //改签票款异动信息。改签失败时为空字符串
                String priceinfo = jsonObject.containsKey("priceinfo") ? jsonObject.getString("priceinfo") : "";
                //差价，差价=新票总价-原票总价（成功才有值）
                float pricedifference = jsonObject.containsKey("pricedifference") ? jsonObject
                        .getFloatValue("pricedifference") : 0f;
                //差额退款费率【默认0】，如：5%
                float diffrate = jsonObject.containsKey("diffrate") ? jsonObject.getFloatValue("diffrate") : 0f;
                //实际退还差额合计，如7
                float totalpricediff = jsonObject.containsKey("totalpricediff") ? jsonObject
                        .getFloatValue("totalpricediff") : 0f;
                //改签订单ID
                long changeorderid = jsonObject.containsKey("changeorderid") ? jsonObject.getLongValue("changeorderid")
                        : 0l;
                //改签手续费【默认0】，如：2        
                float fee = jsonObject.containsKey("fee") ? jsonObject.getFloatValue("fee") : 0f;
                //在预订失败的情况下，给出帮助提示信息，可以直接展示给客户看
                String help_info = jsonObject.containsKey("help_info") ? jsonObject.getString("help_info") : "";
                String apply_id = jsonObject.containsKey("apply_id") ? jsonObject.getString("apply_id") : "";
                int return_online = jsonObject.containsKey("change_order_id") ? jsonObject.getIntValue("return_online")
                        : 0;

                long trainorderid = getOrderIdByJiekouNum(orderid);
                WriteLog.write("淘宝火车异步_回调", r1 + ":" + trainorderid);
                Trainorder order = Server.getInstance().getTrainService().findTrainorder(trainorderid);
                if (trainorderid > 0) {
                    //请求改签
                    if ("train_request_change".equals(method)) {
                        if (success) {
                            JSONObject j = new JSONObject();
                            j.put("orderidme", order.getId());
                            j.put("orderid", order.getQunarOrdernumber());
                            j.put("transactionid", order.getOrdernumber());
                            //拼装回调参数
                            j.put("mainbizorderid", order.getQunarOrdernumber());
                            j.put("apply_id", apply_id);
                            j.put("sellerid", TaobaoHotelInterfaceUtil.agentid);
                            j.put("errorcode", "0");
                            j.put("reqtoken", reqtoken);
                            j.put("isasync", "Y");
                            j.put("changeid", changeorderid);
                            j.put("callbackurl", callbackUrl);
                            try {
                                j.put("refund_online", return_online);
                            }
                            catch (Exception e) {
                                j.put("refund_online", 0);
                            }
                            JSONArray cgja = new JSONArray();
                            for (Trainpassenger trainpassenger : order.getPassengers()) {
                                Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                                for (int x = 0; x < newtickets.size(); x++) {
                                    JSONObject ticketJsonObject = newtickets.getJSONObject(x);
                                    if (trainticket.getTicketno().equals(ticketJsonObject.getString("old_ticket_no"))) {
                                        WriteLog.write("淘宝火车异步_回调", r1 + ":" + trainticket.getTicketno());
                                        JSONObject cgj = new JSONObject();
                                        float changeprocedure = trainticket.getChangeProcedure() == null ? 0f
                                                : trainticket.getChangeProcedure();
                                        long changfee = (long) ((trainticket.getTcPrice() - trainticket.getPrice() + changeprocedure) * 100);
                                        cgj.put("changfee", changfee);
                                        cgj.put("handing_fee", (long) (changeprocedure * 100));
                                        cgj.put("chooseseat",
                                                trainticket.getTtcseattype() + "_" + trainticket.getTccoach() + "_"
                                                        + trainticket.getTcseatno());
                                        cgj.put("realseat",
                                                Long.parseLong(TaobaoHotelInterfaceUtil.CackBackSuccessseao(
                                                        trainticket.getTtcseattype(), trainticket.getTcseatno())));
                                        cgj.put("subbizorderid", Long.parseLong(trainticket.getInterfaceticketno()));
                                        cgja.add(cgj);
                                    }
                                }
                            }

                            j.put("cgja", cgja);
                            String e = j.toJSONString();
                            if (e == null) {//改签回调失败失败
                                WriteLog.write("101_TAOBAO_CHANGE", order.getQunarOrdernumber() + ":下单失败 ：" + e);
                            }
                            else if (e.length() > 10) {//改签下单成功推mq出票
                                WriteLog.write("101_TAOBAO_CHANGE", order.getQunarOrdernumber() + ":推送mq:" + e);
                                TrainpayMqMSGUtil t = new TrainpayMqMSGUtil("TB_Change_Order");
                                try {
                                    t.sendTBChangeOrderMQmsg(JSONObject.parseObject(e));//推mq
                                    WriteLog.write("101_TAOBAO_CHANGE", order.getQunarOrdernumber() + ":推送mq成功");
                                    result = "SUCCESS";
                                }
                                catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        else {
                            JSONObject j = new JSONObject();
                            j.put("mainbizorderid", order.getQunarOrdernumber());
                            j.put("applyid", apply_id);
                            j.put("orderidme", order.getId());
                            //REP返回MSG
                            String msgs = ElongHotelInterfaceUtil.getJsonString(jsonObject, "msg");
                            WriteLog.write("同程火车异步_查询", r1 + ":" + msgs);
                            j.put("errorcode", TaobaoHotelInterfaceUtil.msg2TaoBaoError(msgs));
                            result = new TaobaoHotelInterfaceUtil().CommitChangOrderageOver(j);
                            result = isSuccess(result);
                            //excetn ok 回调失败成功  null 回调失败失败         
                        }

                    }
                    //确认改签
                    else if ("train_confirm_change".equals(method)) {
//                        String sql = "SELECT ISNULL(C_ISCANREFUNDONLINE,0) AS Iscanrefundonline,C_SUPPLYTRADENO AS Supplytradeno,ISNULL(C_TAOBAOAPPLYID,'') AS Taobaoapplyid FROM T_TRAINORDERCHANGE WITH (NOLOCK) WHERE ID ="
//                                + changeorderid;
                        String sql = "SELECT ISNULL(C_ISCANREFUNDONLINE,0) AS Iscanrefundonline,C_SUPPLYTRADENO AS Supplytradeno,ISNULL(C_TAOBAOAPPLYID,'') AS Taobaoapplyid,ISNULL(C_PAYACCOUNT,'') AS PayAccount FROM T_TRAINORDERCHANGE WITH (NOLOCK) WHERE ID ="
                                + changeorderid;
                        Map map = new HashMap();
                        try {
                            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                            map = list.size() > 0 ? (Map) list.get(0) : map;
                        }
                        catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        String extent = "";
                        JSONArray cgja = new JSONArray();
                        if (success) {//回调成功
                            if (changeorderid > 0) {
                                JSONObject trainConfirm = new JSONObject();
                                trainConfirm.put("isneedtradeno", true);
                                trainConfirm.put("refund_online",
                                        map.containsKey("Iscanrefundonline") ? map.get("Iscanrefundonline") : 0);
                                trainConfirm.put("changealipaytradeno",
                                        map.containsKey("Supplytradeno") ? map.get("Supplytradeno") : null);
                                //TODO 这里放改签支付宝账号   进入方法    放入新的或者老的
                                trainConfirm.put("changealipayaccount",getAccountPay(map,order));
                                WriteLog.write("1.1.1_changealipaytradeno",
                                        111 + trainConfirm.getString("changealipaytradeno") + 111);
                                trainConfirm.put("transactionid", order.getOrdernumber());
                                trainConfirm.put("orderidme", order.getId());
                                trainConfirm.put("orderid", order.getQunarOrdernumber());
                                trainConfirm.put("applyid", map.containsKey("Taobaoapplyid") ? map.get("Taobaoapplyid")
                                        : "");
                                trainConfirm.put("errorcode", "0");
                                trainConfirm.put("sellerid", TaobaoHotelInterfaceUtil.agentid);
                                trainConfirm.put("mainbizorderid", order.getQunarOrdernumber());
                                JSONArray jsonArry = new JSONArray();
                                for (Trainpassenger trainpassenger : order.getPassengers()) {
                                    for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                                        if (trainticket.getChangeid() == changeorderid) { //Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                                            float changeprocedure = trainticket.getChangeProcedure() == null ? 0f
                                                    : trainticket.getChangeProcedure();
                                            long changfee = (long) ((trainticket.getTcPrice() - trainticket.getPrice() + changeprocedure) * 100);
                                            JSONObject json = new JSONObject();
                                            json.put("changfee", changfee);
                                            json.put("handing_fee", (long) (changeprocedure * 100));
                                            if ("无座".equals(trainticket.getTcseatno())) {
                                                json.put("chooseseat", "无座_" + trainticket.getTccoach() + "车厢");
                                            }
                                            else {
                                                json.put("chooseseat",
                                                        trainticket.getTtcseattype() + "_" + trainticket.getTccoach()
                                                                + "_" + trainticket.getTcseatno());
                                            }
                                            json.put("realseat", Long.parseLong(TaobaoHotelInterfaceUtil
                                                    .CackBackSuccessseao(trainticket.getTtcseattype(),
                                                            trainticket.getTcseatno())));
                                            json.put("subbizorderid",
                                                    Long.parseLong(trainticket.getInterfaceticketno()));
                                            cgja.add(json);
                                        }
                                    }
                                }
                                WriteLog.write("taobaochangecallbacksevlet_cgja", cgja.toString());
                                trainConfirm.put("cgja", cgja);
                                JSONArray jsonarry = new JSONArray();
                                try {//投保
                                    JSONObject js = TaobaoTrainInsure.getTaobaoTrainInsure().GaiQianTui(
                                            trainConfirm.getIntValue("orderidme"), "改签退保", cgja);
                                    if (js.getBooleanValue("success")) {

                                        jsonarry = js.getJSONArray("jArray");

                                        WriteLog.write(
                                                "101_TAOBAO_CHANGE_LISTENER",
                                                r1 + ":改签退退保投保成功--->" + cgja.toString() + "orderid"
                                                        + trainConfirm.getIntValue("orderidme"));
                                    }
                                    else {
                                        WriteLog.write(
                                                "101_TAOBAO_CHANGE_LISTENER",
                                                r1 + ":改签退退保投保失败--->" + cgja.toString() + "orderid"
                                                        + trainConfirm.getIntValue("orderidme"));
                                    }
                                }
                                catch (Exception e) {
                                    WriteLog.write(
                                            "101_TAOBAO_CHANGE_LISTENER_ERROR",
                                            r1 + ":改签退退保投保异常--->" + cgja.toString() + "orderid"
                                                    + trainConfirm.getIntValue("orderidme"));
                                    ExceptionUtil.writelogByException("101_TAOBAO_CHANGE_LISTENER_ERROR", e);
                                }
                                trainConfirm.put("jsonarry", jsonArry);
                                WriteLog.write("101_TAOBAO_CHANGE_LISTENER", r1 + ":进入改签出票队列回调淘宝成功状态--->"
                                        + trainConfirm.toString());
                                String jsonString = trainConfirm.toString();
                                //                String Taobao_TrainCallBack = getSysconfigString("Taobao_MealCallBack");
                                String Taobao_TrainCallBack = PropertyUtil.getValue("Taobao_MealCallBack",
                                        "Train.properties");
                                WriteLog.write("1.1.1_changealipaytradeno",
                                        222 + trainConfirm.getString("changealipaytradeno") + 222);
                                if (Taobao_TrainCallBack != null && !"".equals(Taobao_TrainCallBack)) {
                                    try {
                                        jsonString = URLEncoder.encode(jsonString, "UTF-8");
                                    }
                                    catch (UnsupportedEncodingException e) {
                                        WriteLog.write("101_TAOBAO_CHANGE_LISTENER_ERROR", r1 + ":异常");
                                        ExceptionUtil.writelogByException("101_TAOBAO_CHANGE_LISTENER_ERROR", e);
                                    }
                                    extent = SendPostandGet2.doGet(Taobao_TrainCallBack + "?json=" + jsonString
                                            + "&statue=1", "UTF-8");
                                    WriteLog.write("淘宝火车异步_回调", r1 + ":" + extent);
                                    result = isSuccess(extent);
                                }
                                else {
                                    TaobaoHotelInterfaceUtil thi = new TaobaoHotelInterfaceUtil();
                                    extent = thi.CommitChangOrderage(trainConfirm, jsonarry);
                                    WriteLog.write("淘宝火车异步_回调", r1 + ":" + extent);
                                    result = isSuccess(extent);
                                }
                            }
                        }
                        else {//确认改签失败 
                            WriteLog.write("101_TAOBAO_CHANGE_LISTENER", r1 + ":进入改签出票队列回调淘宝失败状态_改签接口返回--->"
                                    + jsonObject.toString());
                            JSONObject falseJsonObject = new JSONObject();
                            falseJsonObject.put("orderidme", order.getId());
                            falseJsonObject.put("mainbizorderid", order.getQunarOrdernumber());
                            falseJsonObject.put("applyid", map.containsKey("Taobaoapplyid") ? map.get("Taobaoapplyid")
                                    : "");
                            falseJsonObject.put("errorcode", TaobaoHotelInterfaceUtil
                                    .msg2TaoBaoError(ElongHotelInterfaceUtil.getJsonString(jsonObject, "msg")));
                            WriteLog.write("101_TAOBAO_CHANGE_LISTENER",
                                    r1 + ":进入改签出票队列回调淘宝失败状态--->" + falseJsonObject.toString());
                            String Taobao_TrainCallBack = PropertyUtil.getValue("Taobao_MealCallBack",
                                    "Train.properties");
                            WriteLog.write("淘宝火车异步_回调", r1 + ":" + Taobao_TrainCallBack);
                            if (Taobao_TrainCallBack != null && !"".equals(Taobao_TrainCallBack)) {
                                String jsonString = falseJsonObject.toString();
                                try {
                                    jsonString = URLEncoder.encode(jsonString, "UTF-8");
                                }
                                catch (UnsupportedEncodingException e) {
                                    WriteLog.write("101_TAOBAO_CHANGE_LISTENER_ERROR", r1 + ":异常");
                                    ExceptionUtil.writelogByException("101_TAOBAO_CHANGE_LISTENER_ERROR", e);
                                }
                                extent = SendPostandGet2.doGet(Taobao_TrainCallBack + "?json=" + jsonString
                                        + "&statue=2", "UTF-8");
                                WriteLog.write("淘宝火车异步_回调", r1 + ":" + extent);
                                result = isSuccess(extent);
                            }
                            else {
                                TaobaoHotelInterfaceUtil thi = new TaobaoHotelInterfaceUtil();
                                extent = thi.CommitChangOrderageOver(falseJsonObject);
                                WriteLog.write("淘宝火车异步_回调", r1 + ":" + extent);
                                result = isSuccess(extent);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            out.print(result);
            out.flush();
            out.close();
        }

    }

    /**
     * 通过接口订单号查询订单ID  如果查询不到就返回0
     * @param jiekounumber
     * @return
     * @author wangchengliang
     */
    @SuppressWarnings("rawtypes")
    private long getOrderIdByJiekouNum(String jiekounumber) {
        long trainorderid = 0;
        try {
            String sql = "SELECT top 1 ID FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='" + jiekounumber
                    + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() == 1) {
                Map map = (Map) list.get(0);
                trainorderid = Long.valueOf(map.get("ID").toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return trainorderid;
    }

    private String isSuccess(String result) {
        try {
            if (result != null && !"".equals(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.containsKey("train_agent_change_agree_response")) {
                    JSONObject reJsonObject = jsonObject.getJSONObject("train_agent_change_agree_response");
                    if (reJsonObject.containsKey("is_success") && reJsonObject.getBooleanValue("is_success")) {
                        return "SUCCESS";
                    }
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("TaoBaoChangeCallbackServlet_isSuccess", e);
        }
        return result;
    }
    /**
     * 
     * @author RRRRRR
     * @time 2016年11月7日 下午12:03:47
     * @Description 高改会有新的支付账号    平改或者低改 原先为空  现在传老的支付帐号
     * @param map
     * @param trainorder
     * @return
     */
    private static String getAccountPay(Map map,Trainorder trainorder){
        String result="";
        result=map.containsKey("PayAccount")?map.get("PayAccount").toString():"";
        if(ElongHotelInterfaceUtil.StringIsNull(result)){
            if(ElongHotelInterfaceUtil.StringIsNull(trainorder.getAutounionpayurlsecond())){
                WriteLog.write("淘宝火车异步改签传支付帐号_回调","出鬼了啊啊啊啊啊：订单号 "+ trainorder.getId() + ": new支付 :" + map.get("PayAccount").toString()+" old支付: "+trainorder.getAutounionpayurlsecond());
                return result;
            }else{
                result=trainorder.getAutounionpayurlsecond(); 
            }
        }else{
            return result;
        }
        return result;
    }
}
