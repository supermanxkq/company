package com.ccservice.b2b2c.atom.train;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import TrainInterfaceMethod.TrainInterfaceMethod;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.tenpay.util.MD5Util;

/**
 * 系统无法退票统一接口
 * 
 * @time 2015年2月9日 下午3:45:11
 * @author fiend
 */

public class TrainorderNonRefundable extends TrainorderRefundMethod {

    private Trainticket ticket;

    private Trainorder trainorder;

    private Trainorderchange change;

    private long trainorderid;

    private int interfacetype;

    private int isapplyticket;

    private int reason;

    private String serverinfo;

    public void nonRefundableJsp(long ticketid, long trainorderid, int interfacetype, int reason, String responseurl,
            String errMsg) {
        //去哪儿无法退票理由转换
        if ((interfacetype == TrainInterfaceMethod.QUNAR || interfacetype == TrainInterfaceMethod.TRAINORDERBESPEAK_INTERFACETYPE_QUNAR)) {
            reason = qunarFailReasonConversion(reason);
        }
        this.reason = reason;
        this.trainorderid = trainorderid;
        this.ticket = Server.getInstance().getTrainService().findTrainticket(ticketid);
        this.interfacetype = interfacetype;
        this.isapplyticket = ticket.getIsapplyticket();
        //特殊
        if (isapplyticket == 3) {
            //日志
            WriteLog.write("TrainorderNonRefundable_IsApplyTicket", "isapplyticket:" + isapplyticket + ":trainorderid:"
                    + trainorderid + ":ticketid:" + ticketid);
            //中断
            return;
        }
        this.trainorder = Server.getInstance().getTrainService().findTrainorder(this.trainorderid);
        try {
            if (responseurl != null && responseurl.contains("http://")) {
                this.serverinfo = responseurl.replace("http://", "").split("/")[0];
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WriteLog.write("TrainorderNonRefundable",
                "ticket:" + this.ticket.getId() + ":trainorder:" + this.trainorder.getId() + ":interfacetype:"
                        + this.interfacetype + ":serverinfo:" + this.serverinfo + ":isapplyticket:"
                        + this.isapplyticket + ":reason:" + this.reason + ":errMsg:" + errMsg);
        nonrefund(errMsg);
    }

    /**
     * 无法退票统一处理
     * 
     * @time 2015年2月9日 下午3:19:05
     * @author fiend
     */
    public void nonrefund(String errMsg) {
        if ((interfacetype == TrainInterfaceMethod.QUNAR || interfacetype == TrainInterfaceMethod.TRAINORDERBESPEAK_INTERFACETYPE_QUNAR)
                && isapplyticket == 2) {// qunar客服申请退票
            nonrefundQunarOffline();
        }
        else if ((interfacetype == TrainInterfaceMethod.QUNAR || interfacetype == TrainInterfaceMethod.TRAINORDERBESPEAK_INTERFACETYPE_QUNAR)
                && isapplyticket == 1) {// qunar线上退票
            nonrefundQunarOnline();
        }
        else if ((interfacetype == TrainInterfaceMethod.TONGCHENG || interfacetype == TrainInterfaceMethod.MEITUAN
                || interfacetype == TrainInterfaceMethod.WITHHOLDING_BEFORE || interfacetype == TrainInterfaceMethod.WITHHOLDING_AFTER)
                && isapplyticket == 2) {// 同程客服申请退票
            nonrefundTongchengOffline();
        }
        else if ((interfacetype == TrainInterfaceMethod.TONGCHENG || interfacetype == TrainInterfaceMethod.MEITUAN
                || interfacetype == TrainInterfaceMethod.WITHHOLDING_BEFORE
                || interfacetype == TrainInterfaceMethod.WITHHOLDING_AFTER
                || interfacetype == TrainInterfaceMethod.TRAIN_BESPEAKTICKET
                || interfacetype == TrainInterfaceMethod.TRAIN_BESPEAKTICKET_MEITUAN
                || interfacetype == TrainInterfaceMethod.YILONG1 || interfacetype == TrainInterfaceMethod.YILONG2)
                && isapplyticket == 1) {// 同程线上退票
            nonrefundTongchengOnline(errMsg);
        }
        else if (interfacetype == TrainInterfaceMethod.HTHY) {// 易定行退票
            nonrefundHTHY();
        }
        else if (interfacetype == TrainInterfaceMethod.TAOBAO) {// 淘宝退票
            nonrefundTaobao();
        }
    }

    // TODO================================================接口============================================================================
    /**
     * 淘宝退票
     * 
     * @time 2015年4月14日 下午4:59:27
     * @author fiend
     */

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void nonrefundTaobao() {

        WriteLog.write("淘宝退票", "淘宝退票回调开始 ordernumber=" + this.trainorder.getOrdernumber());
        Map mp = new HashMap();
        String refundfee = "0";
        //        if (ticket.getTcnewprice() != null && ticket.getTcnewprice().floatValue() > 0) {
        //            int n = (int) (ticket.getTcnewprice() - ticket.getProcedure()) * 100;
        //            refundfee = n + "";
        //        }
        //        else {
        //            int n = (int) (ticket.getPrice() - ticket.getProcedure()) * 100;
        //            refundfee = n + "";
        //        }
        mp.put("refund_fee", refundfee);
        mp.put("agree_return", false);
        mp.put("refuse_return_reason", returnTaoBaofailmsgById(this.reason));
        mp.put("main_order_id", trainorder.getQunarOrdernumber());//
        mp.put("sub_biz_order_id", ticket.getInterfaceticketno());
        mp.put("buyerid", trainorder.getTaobaosendid());
        JSONObject jsonObj = JSONObject.fromObject(mp);
        String Taobao_TrainCallBack = getSysconfigString("Taobao_RefundCallBack");
        String taobao_callbackstr = SendPostandGet.submitGet(Taobao_TrainCallBack + "?json=" + jsonObj, "UTF-8");
        // WriteLog.write("TrainCreateOrder_issueTAOBAO", this.trainorderid + ":" + taobao_callbackstr);
        if ("SUCCESS".equals(taobao_callbackstr)) {

            createTrainorderrc(1, trainorder.getId(), "回调淘宝成功", "淘宝退票退款接口", Trainticket.REFUNDFALSE, ticket.getId());
            this.ticket.setStatus(Trainticket.NONREFUNDABLE);
            this.ticket.setRefundfailreason(reason);
            Server.getInstance().getTrainService().updateTrainticket(ticket);

        }
        else {
            createTrainorderrc(1, trainorder.getId(), "回调淘宝失败", "淘宝退票退款接口", Trainticket.REFUNDFALSE, ticket.getId());
        }

    }

    public String getSysconfigString(String name) {
        String result = "-1";
        try {
            if (Server.getInstance().getDateHashMap().get(name) == null) {
                List<Sysconfig> sysoconfigs = Server.getInstance().getSystemService()
                        .findAllSysconfig("WHERE C_NAME='" + name + "'", "", -1, 0);
                if (sysoconfigs.size() > 0) {
                    result = sysoconfigs.get(0).getValue();
                    Server.getInstance().getDateHashMap().put(name, result);
                }
            }
            else {
                result = Server.getInstance().getDateHashMap().get(name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 易定行退票
     * 
     * @time 2015年2月9日 下午2:47:53
     * @author fiend
     */
    private void nonrefundHTHY() {
        this.ticket.setStatus(Trainticket.NONREFUNDABLE);
        this.ticket.setRefundfailreason(reason);
        Server.getInstance().getTrainService().updateTrainticket(ticket);
        WriteLog.write("无法退票统一接口_yeebooking无法退票回调", this.trainorder.getOrdernumber());
        if (this.ticket.getTcnewprice() > 0) {
            getTrainorderChange();
            trainorderrcNonRefundableChange(returnfailmsgByIdQunar(this.reason));
        }
        else {
            trainorderrcNonRefundable(returnfailmsgByIdQunar(this.reason));
        }
    }

    /**
     * 同程线下退票
     * 
     * @time 2015年2月9日 下午2:47:53
     * @author fiend
     */
    private void nonrefundTongchengOffline() {
        // this.ticket.setStatus(Trainticket.NONREFUNDABLE);
        // this.ticket.setRefundfailreason(reason);
        // Server.getInstance().getTrainService().updateTrainticket(ticket);
        // WriteLog.write("无法退票统一接口_tongcheng无法退票回调",
        // this.trainorder.getOrdernumber());
        // trainorderrcNonRefundable(returnfailmsgById(this.reason));
        String refundbackstr = "";
        out: for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                if (trainticket.getId() == this.ticket.getId()) {
                    refundbackstr = callBackTongChengRefund(this.trainorder, trainpassenger, trainticket, false, 9, "");
                    if ("success".equalsIgnoreCase(refundbackstr)) {
                        WriteLog.write("无法退票接口_修改数据库", "当前票ID:" + this.ticket.getId());
                        trainticket.setStatus(Trainticket.NONREFUNDABLE);
                        Server.getInstance().getTrainService().updateTrainticket(trainticket);
                        trainorderrcNonRefundable(returnfailmsgById(9, ""));
                    }
                    else {
                        trainorderrcNonRefundableFalse(returnfailmsgById(9, ""));
                    }
                    break out;
                }
            }
        }
    }

    /**
     * qunar线下退票
     * 
     * @time 2015年2月9日 下午2:47:53
     * @author fiend
     */
    private void nonrefundQunarOffline() {
        if (isCanCallbackQuanr(this.trainorderid, this.ticket.getId(), false)) {
            try {
                String sql = "UPDATE T_TRAINTICKET SET C_STATUS=" + Trainticket.NONREFUNDABLE + " WHERE ID ="
                        + ticket.getId();
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                WriteLog.write("无法退票统一接口_qunar无法退票回调", this.trainorder.getQunarOrdernumber() + ":线下");
                trainorderrcNonRefundable(returnfailmsgByIdQunar(this.reason));
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * qunar线上退票 1:已取票;2:已过时间;3:来电取消
     * 
     * @time 2015年2月9日 下午2:47:53
     * @author fiend
     */
    private void nonrefundQunarOnline() {
        WriteLog.write("无法退票统一接口_qunar无法退票回调", this.trainorder.getQunarOrdernumber() + ":" + this.reason);
        boolean result = false;
        if (isCanCallbackQuanr(this.trainorderid, this.ticket.getId(), false)) {
            try {
                result = Server
                        .getInstance()
                        .getIQTrainService()
                        .trainRefundresult(this.trainorder.getQunarOrdernumber(), Trainticket.NONREFUNDABLE,
                                this.reason);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            WriteLog.write("无法退票统一接口_qunar无法退票回调", this.trainorder.getQunarOrdernumber() + ":" + result);
            if (!result) {
                trainorderrcNonRefundableFalse(returnfailmsgByIdQunar(this.reason));
            }
            else {
                updateTicket();
                trainorderrcNonRefundable(returnfailmsgByIdQunar(this.reason));
            }
        }
    }

    /**
     * 同程线上退票
     * 
     * @time 2015年2月9日 下午2:47:53
     * @author fiend
     */
    private void nonrefundTongchengOnline(String errMsg) {
        String refundbackstr = "";
        out: for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                if (trainticket.getId() == this.ticket.getId()) {
                    refundbackstr = callBackTongChengRefund(this.trainorder, trainpassenger, trainticket, false,
                            this.reason, errMsg);
                    if ("success".equalsIgnoreCase(refundbackstr)) {
                        WriteLog.write("无法退票接口_修改数据库", "当前票ID:" + this.ticket.getId());
                        trainticket.setStatus(Trainticket.NONREFUNDABLE);
                        Server.getInstance().getTrainService().updateTrainticket(trainticket);
                        trainorderrcNonRefundable(returnfailmsgById(this.reason, errMsg));
                    }
                    else {
                        trainorderrcNonRefundableFalse(returnfailmsgById(this.reason, errMsg));
                    }
                    break out;
                }
            }
        }
    }

    /**
     * 同程回调退票
     * 
     * @param i
     * @param trainorder
     * @param trainpassenger
     * @param trainticket
     * @param returnstate
     * @param returnfailid
     * @return
     * @time 2014年12月12日 下午2:20:30
     * @author fiend
     */
    public String callBackTongChengRefund(Trainorder trainorder, Trainpassenger trainpassenger,
            Trainticket trainticket, boolean returnstate, int returnfailid, String errMsg) {
        String result = "false";
        // Trainorder trainorder =
        // Server.getInstance().getTrainService().findTrainorder(orderid);
        String url = "http://" + serverinfo + "/cn_interface/tcTrainCallBack";
        JSONObject jso = new JSONObject();
        jso.put("method", "train_refund_callback");
        // String apiorderid = "TC_20141230151133916251";
        jso.put("apiorderid", trainorder.getQunarOrdernumber());
        // String returntype = "1";
        jso.put("returntype", isapplyticket == 2 ? "0" : "1");
        // String reqtoken = "7824374";
        jso.put("reqtoken", trainticket.getInsureno());
        if (trainorder.getAgentid() == 86) {
            String refundtoken = MD5Util.MD5Encode(trainticket.getId() + "@" + 1, "utf-8");
            jso.put("refundtoken", refundtoken);  
        }
        
        // String trainorderid = "EC11991540";
        jso.put("trainorderid", trainorder.getExtnumber());
        // boolean returnstate = true;
        jso.put("returnstate", returnstate);
        jso.put("agentid", trainorder.getAgentid());
        if (returnstate) {
            // String returnmoney = "1.00";
            if (trainticket.getTcnewprice() != null && trainticket.getTcnewprice().floatValue() > 0) {
                jso.put("returnmoney", (trainticket.getTcnewprice() - trainticket.getProcedure()));
            }
            else {
                jso.put("returnmoney", (trainticket.getPrice() - trainticket.getProcedure()));
            }
        }
        else {
            jso.put("returnmoney", "");
        }
        String returnmsg = returnfailmsgById(returnfailid, errMsg);
        try {
            returnmsg = URLEncoder.encode(returnmsg, "utf-8");
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        jso.put("returnmsg", returnmsg);
        // String ticketid = "";
        jso.put("ticketid", "");
        // String ticket_no = "EC119915401050031";
        if (trainticket.getChangeType() != null && trainticket.getChangeType().intValue() == 1
                && trainticket.getTcnewprice() != null && trainticket.getTcnewprice().floatValue() > 0
                && !ElongHotelInterfaceUtil.StringIsNull(trainticket.getTcticketno())) {
            jso.put("ticket_no", trainticket.getTcticketno());
        }
        else {
            jso.put("ticket_no", trainticket.getTicketno());
        }
        // String passengername = "崔波";
        try {
            jso.put("passengername", URLEncoder.encode(trainpassenger.getName(), "utf-8"));
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        // int passporttypeseid = 1;
        jso.put("passporttypeseid", getIdtype12306(trainpassenger.getIdtype()));
        // String passportseno = "342224198709121116";
        jso.put("passportseno", trainpassenger.getIdnumber());
        jso.put("passengerid", trainpassenger.getPassengerid());
        jso.put("transactionid", trainorder.getOrdernumber());
        // boolean returnsuccess = true;
        jso.put("returnsuccess", returnstate);
        // String returntime = "2014-12-30 15:22:12";
        if (returnstate) {
            jso.put("returntime", TimeUtil.gettodaydate(4));
        }
        else {
            jso.put("returntime", "");
        }
        if (returnstate) {
            // String returnfailid = "";
            jso.put("returnfailid", "");
            // String returnfailmsg = "";
            jso.put("returnfailmsg", "");
        }
        else {
            jso.put("returnfailid", returnfailid);
            String returnfailmsg = returnfailmsgById(returnfailid, errMsg);
            try {
                returnfailmsg = URLEncoder.encode(returnfailmsg, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            jso.put("returnfailmsg", returnfailmsg);
        }
        try {
            WriteLog.write("无法退票统一接口_TongCheng无法退票回调", "订单号:" + trainorder.getOrdernumber() + "地址:" + url + "-请求参数:"
                    + jso.toString());
            result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            WriteLog.write("无法退票统一接口_TongCheng无法退票回调", "订单号:" + trainorder.getOrdernumber() + "-回调结果:" + result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // TODO===================================================操作记录=======================================================================
    public void trainorderrcNonRefundable(String content) {
        createTrainorderrc(1, this.trainorderid, "无法退票 ==><span style='color:red;'>" + content + "</span>", "系统接口",
                Trainticket.NONREFUNDABLE, this.ticket.getId());
    }

    public void trainorderrcNonRefundableFalse(String content) {
        createTrainorderrc(1, this.trainorderid, "<span style='color:red;'>无法退票失败</span>==>" + content, "系统接口",
                Trainticket.REFUNDROCESSING, this.ticket.getId());
    }

    public void trainorderrcNonRefundableChange(String content) {
        createTrainorderrc(2, this.change.getId(), "无法退票==><span style='color:red;'>" + content + "</span>", "系统接口",
                Trainticket.NONREFUNDABLE, this.ticket.getId());
    }

    public void trainorderrcNonRefundableChangeFalse(String content) {
        createTrainorderrc(2, this.change.getId(), "<span style='color:red;'>无法退票失败</span>==>" + content, "系统接口",
                Trainticket.REFUNDROCESSING, this.ticket.getId());
    }

    //    /**
    //     * 书写操作记录
    //     * 
    //     * @param trainorderid
    //     * @param content
    //     * @param createurser
    //     * @time 2015年1月21日 下午7:05:04
    //     * @author fiend
    //     */
    //    public void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser, int status,
    //            long ticketid) {
    //        Trainorderrc rc = new Trainorderrc();
    //        rc.setOrderid(trainorderid);
    //        rc.setContent(content);
    //        rc.setStatus(status);
    //        rc.setCreateuser(createurser);
    //        rc.setTicketid(ticketid);
    //        rc.setYwtype(yewutype);
    //        Server.getInstance().getTrainService().createTrainorderrc(rc);
    //    }

    // TODO===================================================工具=========================================================================

    /**
     * 因需要获取订单
     * 
     * @time 2015年2月9日 下午2:54:31
     * @author fiend
     */
    public void getTrainorderChange() {
        this.change = Server.getInstance().getTrainService()
                .findTrainorcerchange(ticket.getTrainpassenger().getChangeid());
    }

    /**
     * 无法退票
     * 
     * @time 2015年2月9日 下午3:02:38
     * @author fiend
     */
    public void updateTicket() {
        try {
            for (Trainpassenger trainpassenger : this.trainorder.getPassengers()) {
                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                    if (trainticket.getStatus() == Trainticket.REFUNDROCESSING) {
                        trainticket.setStatus(Trainticket.NONREFUNDABLE);
                        trainticket.setRefundfailreason(reason);
                        Server.getInstance().getTrainService().updateTrainticket(trainticket);
                    }
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("无法退票死锁", this.trainorder.getOrdernumber());
        }
    }

    /**
     * 证件类型本地转换12306代码
     * 
     * @param idtype
     * @return
     * @time 2014年12月24日 上午11:21:59
     * @author wzc
     */
    public String getIdtype12306(int idtype) {
        switch (idtype) {
        case 1:
            return "1";
        case 3:
            return "B";
        case 4:
            return "C";
        case 5:
            return "G";
        }
        return "";
    }

    /**
     * 通过失败码号得到提示信息
     * 
     * @param returnfailid
     * @return
     * @time 2015年1月9日 上午11:56:59
     * @author fiend
     */
    public String returnfailmsgById(int returnfailid, String errMsg) {
        switch (returnfailid) {
        case 31:
            return "已改签";
        case 32:
            return "已退票";
        case 33:
            return "已出票,只能在窗口办理退票";
        case 39:
            return "不可退票";
        case 9:
            return "退票操作异常,请与客服联系";
        case 40:
            return "订单所在账号被封";
        case 41:
            return errMsg;
        case 51:
            return errMsg;
        }
        return "";
    }

    /**
     * 通过失败获取代码淘宝专用
     * @param returnfailid
     * @return
     */
    public String returnTaoBaofailmsgById(int returnfailid) {

        /*同城
         *      "<option value='31' id='1'>已改签</option>" +
                "<option value='32' id='2'>已退票</option>" +
                "<option value='33' id='3'>已出票，只能在窗口办理退票</option>" +
                "<option value='39' id='4'>不可退票</option>" +
                "<option value='40' id='5'>订单所在账号被封</option>" +
                "<option value='41' id='6'>正值暑期旅游旺季，无法在线退票</option>" +
         */
        /*淘宝
         *  "1", "买家已取票，请发车前到车站自行办理"
            "2", "该车票已过发车时间"
            "3", "系统异常"
            "0", "未知或非法值" 
                                其他  0
                                已取票 1
                                已过发车时间  2
           12306系统异常   3
           12306账号问题（被封等情况）    4
                                没有查到回款  5
                                发车前30分钟以内，无法在线办理退票  6
                                用户12306账号登录失败 7

        */
        switch (returnfailid) {
        case 31:
            return "1";
        case 32:
            return "1";
        case 33:
            return "1";
        case 39:
            return "2";
        case 40:
            return "4";
        case 41:
            return "1";
        case 42:
            return "5";
        case 43:
            return "6";
        case 51:
            return "7";
        }
        return "3";
    }

    /**
     * 通过失败码号得到提示信息 qunar
     * 
     * @param returnfailid
     * @return
     * @time 2015年1月9日 上午11:56:59
     * @author fiend
     */
    public String returnfailmsgByIdQunar(int returnfailid) {
        switch (returnfailid) {
        case 1:
            return "已取票";
        case 2:
            return "已过时间";
        case 3:
            return "来电取消";
        case 4:
            return "已改签";
        case 5:
            return "其他";
        case 6:
            return "退款金额有损失";
        case 7:
            return "该线路不能网站退改";
        }
        return "其他";
    }

    /**
     * 转换为去哪儿无法退票理由
     * @return 去哪儿无法退票理由
     */
    public int qunarFailReasonConversion(int returnfailid) {
        switch (returnfailid) {
        case 31:
            return 4;//已改签>>已改签
        case 32:
            return 5;//已退票>>其他
        case 33:
            return 1;//已出票，只能在窗口办理退票>>已取票
        case 39:
            return 5;//不可退票>>其他
        case 40:
            return 7;//订单所在账号被封>>该线路不能网站退改
        case 41:
            return 7;//正值暑期旅游旺季，无法在线退票>>该线路不能网站退改
        case 42:
            return 6;//无退款>>退款金额有损失
        case 43:
            return 2;//发车前30分钟以内，无法在线退票>>已过时间
        case 51:
            return 5;//用户12306账号登录失败>>其他
        }
        return returnfailid == 1 ? 1 : 5;
    }
}
