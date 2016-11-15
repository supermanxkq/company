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
import com.ccservice.b2b2c.atom.servlet.TaobaoTrainInsure;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.train.FuzzyRefund;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.tenpay.util.MD5Util;

//火车票退票退款统一接口
public class TrainorderRefundprice extends TrainorderRefundMethod {

    private Trainticket ticket;

    private Trainorder trainorder;

    private int interfacetype;

    private String serverinfo;

    private float procedure;

    private Customeruser customeruser;

    private String returnType;// 2：线下改签退款；3：线上改签退款；mohutui：同程模糊退

    private FuzzyRefund fuzzyRefund;

    private static final String refundLimit = "虚拟账户：退款失败，金额限制，暂不可退";

    public void refundpriceJsp(long ticketid, long trainorderid, int interfacetype, String responseurl,
            float procedure, String returnType) {
        //模糊退
        boolean mohutui = "mohutui".equals(returnType);
        //订单
        this.trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);
        //模糊退
        this.fuzzyRefund = mohutui ? Server.getInstance().getTrainService().findFuzzyRefundById(ticketid)
                : new FuzzyRefund();
        //车票
        this.ticket = mohutui ? new Trainticket() : Server.getInstance().getTrainService().findTrainticket(ticketid);
        //其他参数赋值
        this.procedure = procedure;
        this.returnType = returnType;
        this.interfacetype = interfacetype;
        this.customeruser = new Customeruser();
        this.customeruser.setId(62);//超级管理员
        this.customeruser.setMembername("系统接口");
        this.serverinfo = responseurl.replace("http://", "").split("/")[0];
        //记录日志
        WriteLog.write("TrainorderRefundprice", (mohutui ? "fuzzy:" : "ticket:") + ticketid + ":trainorder:"
                + trainorderid + ":interfacetype:" + interfacetype + ":serverinfo:" + serverinfo + ":procedure:"
                + procedure + ":returnType:" + returnType);
        //模糊退
        if (mohutui) {
            mohutui();
        }
        //退票、线下改签
        else {
            refundprice();
        }
    }

    private void refundprice() {
        // 2:线下改签
        boolean IsOfflineChange = "2".equals(returnType);
        // 车票状态
        int status = IsOfflineChange ? ticket.getChangeType() : ticket.getStatus();
        // 等待退款、退款失败
        if (status == Trainticket.WAITREFUND || status == Trainticket.REFUNDFAIL) {
            // 退票类型
            int isapplyticket = ticket.getIsapplyticket() == null ? 0 : ticket.getIsapplyticket().intValue();
            // 线下改签
            if (IsOfflineChange) {
                isapplyticket = 0;
                if (ticket.getStatus() != Trainticket.FINISHCHANGE) {
                    WriteLog.write("改签退款ERROR", trainorder.getId() + ":status[" + ticket.getStatusstr() + "]错误");
                    return;
                }
            }
            else {
                if (isapplyticket != 1 && isapplyticket != 2) {
                    WriteLog.write("退票退款ERROR", trainorder.getId() + ":isapplyticket[" + isapplyticket + "]错误");
                    return;
                }
            }
            // 票价
            float ticketPrice = ticket.getPrice().floatValue();
            // 接口订单号
            String qunarOrdernumber = trainorder.getQunarOrdernumber();
            // 线下改签
            if (IsOfflineChange) {
                ticket.setChangeType(Trainticket.REFUNDIING);
                ticket.setTcProcedure(procedure);
                // 相等时表示高改，新票价未知，取原票价
                ticket.setTcnewprice(ticketPrice == procedure ? ticketPrice : ElongHotelInterfaceUtil.floatSubtract(
                        ticketPrice, procedure));
                // 更新标识
                boolean updateFlag = ticket.getTcnewprice() > 0 ? Server.getInstance().getTrainService()
                        .updateTrainticket(ticket) : false;
                // 日志
                WriteLog.write("改签退款接口", trainorder.getId() + ":退款中:" + updateFlag);
                // 更新失败
                if (!updateFlag) {
                    return;
                }
                // 操作记录
                createTrainorderrc(1, trainorder.getId(), "执行退款,线下改签退款中。改签退款:" + procedure, "改签退款接口",
                        Trainticket.REFUNDIING, ticket.getId());
            }
            else {
                ticket.setStatus(Trainticket.REFUNDIING);
                ticket.setProcedure(procedure);
                // 更新标识
                boolean updateFlag = Server.getInstance().getTrainService().updateTrainticket(ticket);
                // 日志
                WriteLog.write("退票退款接口", trainorder.getId() + ":退款中:" + updateFlag);
                // 更新失败
                if (!updateFlag) {
                    return;
                }
                // 操作记录
                createTrainorderrc(1, trainorder.getId(), "执行退款,客票退款中。退票手续费:" + procedure, "退票退款接口",
                        Trainticket.REFUNDIING, ticket.getId());
            }
            // qunar线上
            if ((interfacetype == TrainInterfaceMethod.QUNAR || interfacetype == TrainInterfaceMethod.TRAINORDERBESPEAK_INTERFACETYPE_QUNAR)
                    && isapplyticket == 1) {
                if (isCanCallbackQuanr(this.trainorder.getId(), this.ticket.getId(), true)) {
                    boolean msg = false;
                    try {
                        WriteLog.write("退票退款接口", "debug:1");
                        msg = Server.getInstance().getIQTrainService()
                                .trainRefundresult(qunarOrdernumber, ticket.getStatus(), 0);
                    }
                    catch (Exception e) {
                        WriteLog.write("退票退款接口", "err" + e.getMessage());
                        e.printStackTrace();
                    }
                    WriteLog.write("退票退款接口", "msg:" + msg);

                    /***要判断同一批退票的车票***/

                    //失败
                    if (!msg) {
                        ticket.setStatus(Trainticket.REFUNDFAIL);
                        //问题
                        ticket.setRefundPriceQuestion(Trainticket.REFUNDCALLBACKFAIL);
                        //更新
                        Server.getInstance().getTrainService().updateTrainticket(ticket);
                        WriteLog.write("退票退款接口", trainorder.getId() + ":退款失败");
                        createTrainorderrc(1, trainorder.getId(), "退票退款失败,请客服查看qunar后台订单状态", "退票退款接口",
                                Trainticket.REFUNDFAIL, ticket.getId());
                    }
                    else {
                        for (Trainpassenger p : trainorder.getPassengers()) {
                            for (Trainticket tk : p.getTraintickets()) {
                                tk.setStatus(Trainticket.REFUNDED);
                                //问题
                                tk.setRefundPriceQuestion(Trainticket.REFUNDNORMAL);
                                //更新
                                Server.getInstance().getTrainService().updateTrainticket(tk);
                                new TrainVmoneyRecord().refund(trainorder.getAgentid(),
                                        tk.getPrice() - tk.getProcedure(), tk.getId(), trainorder.getOrdernumber(),
                                        trainorder.getQunarOrdernumber(), false, false);
                            }
                        }
                        WriteLog.write("退票退款接口", trainorder.getId() + ":退款成功");
                        createTrainorderrc(1, trainorder.getId(), "退票退款成功", "退票退款接口", Trainticket.REFUNDED,
                                ticket.getId());
                    }
                }

            }
            // qunar线下
            else if ((interfacetype == TrainInterfaceMethod.QUNAR || interfacetype == TrainInterfaceMethod.TRAINORDERBESPEAK_INTERFACETYPE_QUNAR)
                    && isapplyticket == 2) {
                if (isCanCallbackQuanr(this.trainorder.getId(), this.ticket.getId(), true)) {
                    //                    float orderprice = 0l;
                    //                    for (Trainpassenger p : trainorder.getPassengers()) {
                    //                        for (Trainticket tk : p.getTraintickets()) {
                    //                            orderprice += tk.getPrice();
                    //                        }
                    //                    }
                    float refundCash = this.ticket.getPrice() - procedure;
                    boolean result = false;
                    try {
                        result = Server.getInstance().getIQTrainService()
                                .trainRefundPrice(qunarOrdernumber, 3, refundCash);
                    }
                    catch (Exception e) {
                    }
                    if (result) {
                        for (Trainpassenger p : trainorder.getPassengers()) {
                            for (Trainticket tk : p.getTraintickets()) {
                                if (tk.getId() == this.ticket.getId()) {
                                    tk.setStatus(Trainticket.REFUNDED);
                                    //问题
                                    tk.setRefundPriceQuestion(Trainticket.REFUNDNORMAL);
                                    //更新
                                    Server.getInstance().getTrainService().updateTrainticket(tk);
                                    new TrainVmoneyRecord()
                                            .refund(trainorder.getAgentid(), refundCash, tk.getId(),
                                                    trainorder.getOrdernumber(), trainorder.getQunarOrdernumber(),
                                                    false, false);
                                }
                            }
                        }
                        WriteLog.write("退票退款接口", trainorder.getId() + ":退款成功");
                        createTrainorderrc(1, trainorder.getId(), "退票退款成功", "退票退款接口", Trainticket.REFUNDED,
                                ticket.getId());
                    }
                    else {
                        ticket.setStatus(Trainticket.REFUNDFAIL);
                        //问题
                        ticket.setRefundPriceQuestion(Trainticket.REFUNDCALLBACKFAIL);
                        //更新
                        Server.getInstance().getTrainService().updateTrainticket(ticket);
                        WriteLog.write("退票退款接口", trainorder.getId() + ":退款失败");
                        createTrainorderrc(1, trainorder.getId(), "退票退款失败,请客服查看qunar后台订单状态", "退票退款接口",
                                Trainticket.REFUNDFAIL, ticket.getId());
                    }
                }

            }
            // 同程
            else if (interfacetype == TrainInterfaceMethod.TONGCHENG
                    || interfacetype == TrainInterfaceMethod.WITHHOLDING_AFTER
                    || interfacetype == TrainInterfaceMethod.WITHHOLDING_BEFORE
                    || interfacetype == TrainInterfaceMethod.YILONG1 || interfacetype == TrainInterfaceMethod.YILONG2
                    || interfacetype == TrainInterfaceMethod.MEITUAN
                    || interfacetype == TrainInterfaceMethod.TRAIN_BESPEAKTICKET
                    || interfacetype == TrainInterfaceMethod.TRAIN_BESPEAKTICKET_MEITUAN) {
                Trainpassenger trainpassengerreal = null;
                out: for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                    for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                        if (ticket.getId() == trainticket.getId()) {
                            trainpassengerreal = trainpassenger;
                            break out;
                        }
                    }
                }
                boolean isnoline = true;
                // 线下退票、线下改签
                if (isapplyticket == 2 || "2".equals(returnType)) {
                    isnoline = false;
                }
                refundSuccess(trainorder, trainpassengerreal, ticket, true, isnoline, serverinfo, customeruser);
            }
            // 易定行
            else if (interfacetype == TrainInterfaceMethod.HTHY) {
                Server.getInstance().getTrainService()
                        .ticketRefund(trainorder.getId(), ticket.getId(), customeruser, serverinfo);
            }
            // 淘宝
            else if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                refundSuccessTaobao(trainorder, ticket);
            }
        }
    }

    /**
     * 模糊退款，以订单为单位
     */
    private void mohutui() {
        //无订单
        if (trainorder == null) {
            trainorder = new Trainorder();
        }
        if (fuzzyRefund == null) {
            fuzzyRefund = new FuzzyRefund();
        }
        //模糊ID
        long Id = fuzzyRefund.getId();
        //订单ID
        long orderId = trainorder.getId();
        //原状态
        int oldStats = fuzzyRefund.getStatus();
        //非同程接口
        if (interfacetype != TrainInterfaceMethod.TONGCHENG) {
            //return;
        }
        //非等待退款、退款失败
        if (oldStats != Trainticket.WAITREFUND && oldStats != Trainticket.REFUNDFAIL) {
            return;
        }
        //数据错误
        if (Id <= 0 || orderId <= 0 || procedure <= 0 || fuzzyRefund.getOrderId() != orderId) {
            return;
        }

        //退款中
        int newStatus = FuzzyRefund.REFUNDIING;
        //等待退款
        String updateSql = "update FuzzyRefund set Status = " + newStatus + ", Money = " + procedure + " where Id = "
                + Id + " and Status = " + oldStats;
        //更新标识
        boolean updateFlag = Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) == 1;
        //日志
        WriteLog.write("模糊退接口", orderId + ":" + Id + ":退款中:" + updateFlag);
        //更新失败
        if (!updateFlag) {
            return;
        }
        //设置时间戳
        ticket.setTcProcedure(procedure);
        ticket.setRefundTimeStamp(fuzzyRefund.getTimeStamp());
        //操作记录
        createTrainorderrc(1, orderId, "执行退款，模糊退款中。退款金额:" + procedure, "模糊退接口", newStatus, Id);
        //默认标识
        long reqtoken = System.currentTimeMillis();
        //退款处理
        String result = callBackTongChengRefundOffLine(trainorder, new Trainpassenger(), ticket, true, serverinfo,
                fuzzyRefund, reqtoken);
        //退款限制
        if (refundLimit.equals(result)) {
            return;
        }
        //结果为空
        else if (ElongHotelInterfaceUtil.StringIsNull(result)) {
            result = "";
        }
        //拆分结果
        String[] results = result.split("@");
        //回调结果
        result = results[0];
        //时间辍
        String TimeStamp = results.length > 1 ? results[1] : "";
        //重置数据
        if (ElongHotelInterfaceUtil.StringIsNull(TimeStamp)
                && ElongHotelInterfaceUtil.StringIsNull(fuzzyRefund.getTimeStamp())) {
            TimeStamp = String.valueOf(reqtoken);
        }
        //回调成功
        boolean callBackTrue = "success".equalsIgnoreCase(result);
        //更新状态
        newStatus = callBackTrue ? FuzzyRefund.REFUNDED : FuzzyRefund.REFUNDFAIL;
        //问题类型
        int refundPriceQuestion = callBackTrue ? FuzzyRefund.REFUNDNORMAL : FuzzyRefund.REFUNDCALLBACKFAIL;
        //操作记录
        String content = "模糊退款" + procedure + "元---回调";
        //回调结果
        if (callBackTrue) {
            content += "成功";
        }
        else {
            content += "失败---" + result;
        }
        //保存记录
        createTrainorderrc(1, orderId, content, "模糊退接口", newStatus, Id);
        //回调成功、状态在TrainService.ticketRefund方法中更新
        if (callBackTrue) {
            if (ElongHotelInterfaceUtil.StringIsNull(TimeStamp)) {
                updateSql = "update FuzzyRefund set RefundPriceQuestion = " + refundPriceQuestion + " where Id = " + Id;
            }
            else {
                updateSql = "update FuzzyRefund set TimeStamp = '" + TimeStamp + "', RefundPriceQuestion = "
                        + refundPriceQuestion + " where Id = " + Id;
            }
            //更新数据
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
            //模糊退款标识
            customeruser.setDescription("模糊退退款标识");
            //调用退款接口
            Server.getInstance().getTrainService().ticketRefund(orderId, Id, customeruser, serverinfo);
        }
        else {
            if (ElongHotelInterfaceUtil.StringIsNull(TimeStamp)) {
                updateSql = "update FuzzyRefund set Status = " + newStatus + ", RefundPriceQuestion = "
                        + refundPriceQuestion + " where Id = " + Id;
            }
            else {
                updateSql = "update FuzzyRefund set Status = " + newStatus + ", TimeStamp = '" + TimeStamp
                        + "', RefundPriceQuestion = " + refundPriceQuestion + " where Id = " + Id;
            }
            //更新数据
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
        }
    }

    /**
     * 淘宝退票待完善
     * 
     * @time 2015年4月14日 下午5:02:53
     * @author fiend
     */
    private void refundSuccessTaobao(Trainorder trainorder, Trainticket trainticket) {
        //车票
        out: for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket tticket : trainpassenger.getTraintickets()) {
                if (tticket.getId() == trainticket.getId()) {
                    tticket.setProcedure(trainticket.getProcedure());
                    trainticket = tticket;
                    break out;
                }
            }
        }
        //订单ID
        long orderId = trainorder.getId();
        //票ID
        long ticketId = trainticket.getId();
        //退保状态
        boolean b = false;
        //退保操作
        if (!ElongHotelInterfaceUtil.StringIsNull(trainticket.getRealinsureno())) {
            try {
                b = TaobaoTrainInsure.getTaobaoTrainInsure().tuibao(Integer.parseInt(orderId + ""), "客户退票",
                        trainticket.getRealinsureno());
                //退保成功
                if (b) {
                    createTrainorderrc(1, orderId, "退保成功", "退保接口", trainticket.getStatus(), ticketId);
                    WriteLog.write("淘宝退票",
                            "退票退保成功" + Integer.parseInt(orderId + "") + "   Insureno  " + trainticket.getRealinsureno());
                }
                else {
                    createTrainorderrc(1, orderId, "退保失败", "退保接口", trainticket.getStatus(), ticketId);
                    WriteLog.write("淘宝退票",
                            "退票退保失败" + Integer.parseInt(orderId + "") + "   Insureno  " + trainticket.getRealinsureno());
                }
            }
            catch (Exception e) {
                WriteLog.write("淘宝退票",
                        "退票退保失败" + Integer.parseInt(orderId + "") + "   Insureno  " + trainticket.getRealinsureno());
            }
        }
        //新票价
        float newPrice = trainticket.getTcnewprice() == null ? 0 : trainticket.getTcnewprice();
        //车票价
        float ticketPrice = newPrice > 0 ? newPrice : trainticket.getPrice().floatValue();
        //车票退款
        float ticketRefund = ticketPrice - trainticket.getProcedure();
        //车票保费
        float insurorigprice = b ? trainticket.getInsurorigprice().floatValue() : 0;
        //转换为分
        int n = (int) (ticketRefund * 100);
        //退保成功
        if (b) {
            n = n + (int) (insurorigprice * 100);
        }
        //总退款，单位：分
        String refundfee = Integer.toString(n);
        //退款参数
        Map<String, Object> mp = new HashMap<String, Object>();
        mp.put("agree_return", true);
        mp.put("refund_fee", refundfee);
        mp.put("refuse_return_reason", "no");
        mp.put("buyerid", trainorder.getTaobaosendid());
        mp.put("main_order_id", trainorder.getQunarOrdernumber());
        mp.put("sub_biz_order_id", trainticket.getInterfaceticketno());
        //JSON
        JSONObject jsonObj = JSONObject.fromObject(mp);
        //退款地址
        String Taobao_TrainCallBack = getSysconfigString("Taobao_RefundCallBack");
        //请求淘宝
        String taobao_callbackstr = SendPostandGet.submitGet(Taobao_TrainCallBack + "?json=" + jsonObj, "UTF-8");
        //回调成功
        if ("SUCCESS".equals(taobao_callbackstr)) {
            //内容
            String content = "回调淘宝成功，退票退款金额：" + ticketRefund;
            //退保
            if (b) {
                content += "，退保退款金额：" + insurorigprice;
            }
            //日志
            createTrainorderrc(1, orderId, content, "淘宝退票退款接口", Trainticket.REFUNDED, ticketId);
            //更新SQL
            String sql = "update T_TRAINTICKET set C_STATUS = " + Trainticket.REFUNDED + ", C_REFUNDPRICEQUESTION = "
                    + Trainticket.REFUNDNORMAL + " where ID = " + ticketId;
            //更新票状态
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            //新增虚拟帐户记录
            new TrainVmoneyRecord().refund(trainorder.getAgentid(), ticketRefund, ticketId,
                    trainorder.getOrdernumber(), trainorder.getQunarOrdernumber(), false, false);
        }
        else {
            //日志
            createTrainorderrc(1, orderId, "回调淘宝失败-->" + taobao_callbackstr, "淘宝退票退款接口", Trainticket.REFUNDFAIL,
                    ticketId);
            //更新SQL
            String sql = "update T_TRAINTICKET set C_STATUS = " + Trainticket.REFUNDFAIL + ", C_REFUNDPRICEQUESTION = "
                    + Trainticket.REFUNDCALLBACKFAIL + " where ID = " + ticketId;
            //更新票状态
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }

    }

    @SuppressWarnings("unchecked")
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
     * 退票退款回调同程
     * 
     * @param trainorder
     * @param trainpassenger
     * @param trainticket
     * @param returnstate
     * @param isnoline
     * @time 2014年12月30日 下午2:59:39
     * @author fiend
     */
    public void refundSuccess(Trainorder trainorder, Trainpassenger trainpassenger, Trainticket trainticket,
            boolean returnstate, boolean isnoline, String serverinfo, Customeruser customeruser) {
        String refundbackstr = "fail";
        // 默认标识
        long reqtoken = System.currentTimeMillis();
        // 线上退款
        if (isnoline) {
            refundbackstr = callBackTongChengRefund(trainorder, trainpassenger, trainticket, returnstate, 0, serverinfo);
        }
        else {
            refundbackstr = callBackTongChengRefundOffLine(trainorder, trainpassenger, trainticket, returnstate,
                    serverinfo, new FuzzyRefund(), reqtoken);
        }
        // 退款限制
        if (refundLimit.equals(refundbackstr)) {
            return;
        }
        // 结果为空
        else if (ElongHotelInterfaceUtil.StringIsNull(refundbackstr)) {
            refundbackstr = "";
        }
        // 拆分结果
        String[] results = refundbackstr.split("@");
        // 回调结果
        refundbackstr = results[0];
        // 线下改签
        boolean IsOfflineChange = "2".equals(returnType);
        // 时间辍
        String TimeStamp = results.length > 1 ? results[1] : "";
        // 回调成功
        boolean callBackTrue = "success".equalsIgnoreCase(refundbackstr);
        // 记录日志
        refundOrderRc(trainorder, refundbackstr, trainpassenger, trainticket, returnstate);
        // 车票改签
        if ("2".equals(returnType) || "3".equals(returnType)) {
            if (ElongHotelInterfaceUtil.StringIsNull(TimeStamp)
                    && ElongHotelInterfaceUtil.StringIsNull(trainticket.getChangeTimeStamp())) {
                TimeStamp = String.valueOf(reqtoken);
            }
            trainticket.setChangeTimeStamp(TimeStamp);
            trainticket.setState12306(Trainticket.CHANGEDPAYED);
        }
        else {
            if (ElongHotelInterfaceUtil.StringIsNull(TimeStamp)
                    && ElongHotelInterfaceUtil.StringIsNull(trainticket.getRefundTimeStamp())) {
                TimeStamp = String.valueOf(reqtoken);
            }
            trainticket.setRefundTimeStamp(TimeStamp);
            trainticket.setState12306(Trainticket.REFUNDED12306);
        }
        // 回调成功、状态在TrainService.ticketRefund方法中更新
        if (callBackTrue) {
            // 回调成功
            trainticket.setRefundPriceQuestion(Trainticket.REFUNDNORMAL);
            // 更新车票
            Server.getInstance().getTrainService().updateTrainticket(trainticket);
            // 用于标识为线下改签，文字不能作修改
            if (IsOfflineChange) {
                customeruser.setDescription("线下改签退款标识");
            }
            // 调用退款接口
            Server.getInstance().getTrainService()
                    .ticketRefund(trainorder.getId(), trainticket.getId(), customeruser, serverinfo);
        }
        else {
            // 失败状态
            if (IsOfflineChange) {
                trainticket.setChangeType(Trainticket.REFUNDFAIL);
            }
            else {
                trainticket.setStatus(Trainticket.REFUNDFAIL);
            }
            // 回调失败
            trainticket.setRefundPriceQuestion(Trainticket.REFUNDCALLBACKFAIL);
            // 更新车票
            Server.getInstance().getTrainService().updateTrainticket(trainticket);
        }
    }

    /**
     * 判断是否可退
     * @param refundMoney 退款金额
     * @param isfuzzy 模糊退
     * @param IsOfflineChange 线下改签
     */
    @SuppressWarnings("rawtypes")
    public boolean canRefund(Trainorder trainorder, Trainticket ticket, FuzzyRefund fuzzy, float refundMoney,
            boolean isfuzzy, boolean IsOfflineChange) {
        //可退
        boolean canrefund = true;
        //判断可退金额、虚拟账户支付
        if (trainorder.getPaymethod() == Paymentmethod.VMONEYPAY) {
            //不可退
            canrefund = false;
            //订单号
            String orderNumber = trainorder.getOrdernumber();
            //查询资金
            String moneySql = "SELECT ISNULL(SUM(C_REBATEMONEY), 0) C_REBATEMONEY FROM "
                    + "T_REBATERECORD WITH(NOLOCK) WHERE C_ORDERNUMBER = '" + orderNumber + "'";
            List moneyList = Server.getInstance().getSystemService().findMapResultBySql(moneySql, null);
            //统计交易记录
            if (!ElongHotelInterfaceUtil.StringIsNull(orderNumber) && moneyList != null && moneyList.size() == 1) {
                Map map = (Map) moneyList.get(0);
                //可退金额
                float canRefundPrice = -Float.valueOf(map.get("C_REBATEMONEY").toString());
                //金额判断
                if (canRefundPrice >= refundMoney) {
                    canrefund = true;
                }
            }
        }
        //不可退，置为回调失败
        if (!canrefund) {
            long id = isfuzzy ? fuzzy.getId() : ticket.getId();
            int status = isfuzzy ? FuzzyRefund.REFUNDFAIL : Trainticket.REFUNDFAIL;
            int question = isfuzzy ? FuzzyRefund.REFUNDCALLBACKFAIL : Trainticket.REFUNDCALLBACKFAIL;
            //SQL
            String updateSql = "";
            //模糊退
            if (isfuzzy) {
                updateSql = "update FuzzyRefund set Status = " + status + ", RefundPriceQuestion = " + question
                        + " where ID = " + id;
            }
            //线下改签
            else if (IsOfflineChange) {
                updateSql = "update T_TRAINTICKET set C_CHANGETYPE = " + status + ", C_REFUNDPRICEQUESTION = "
                        + question + " where ID = " + id;
            }
            else {
                updateSql = "update T_TRAINTICKET set C_STATUS = " + status + ", C_REFUNDPRICEQUESTION = " + question
                        + " where ID = " + id;
            }
            //设为回调问题
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
        }
        return canrefund;
    }

    //    public static void main(String[] args) {
    //        String refundtoken = "";
    //        refundtoken = Md5Encrypt.md5(trainticket.getId() + "@" + 2);
    //        System.out.println(refundtoken);
    //
    //    }

    // =========================================================================接口=====================================================================================
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
            Trainticket trainticket, boolean returnstate, int returnfailid, String serverinfo) {
        String result = "false";
        String refundtoken = "";
        // Trainorder trainorder =
        // Server.getInstance().getTrainService().findTrainorder(orderid);
        // String url = getSysconfigString("tcTrainCallBack");
        String url = "http://" + serverinfo + "/cn_interface/tcTrainCallBack";
        JSONObject jso = new JSONObject();
        jso.put("method", "train_refund_callback");
        // String apiorderid = "TC_20141230151133916251";
        jso.put("apiorderid", trainorder.getQunarOrdernumber());
        jso.put("transactionid", trainorder.getOrdernumber());//订单号(交易号)
        // String returntype = "1";
        jso.put("returntype", "3".equals(returnType) ? "3" : "1");
        // String reqtoken = "7824374";
        jso.put("reqtoken", trainticket.getInsureno());
        // String trainorderid = "EC11991540";
        jso.put("trainorderid", trainorder.getExtnumber());
        // boolean returnstate = true;
        jso.put("returnstate", returnstate);
        jso.put("agentid", trainorder.getAgentid());
        if (returnstate) {
            float refundMoney = 0;
            // String returnmoney = "1.00";1改签金额2未改签金额
            if (trainticket.getTcnewprice() != null && trainticket.getTcnewprice().floatValue() > 0) {
                refundMoney = trainticket.getTcnewprice() - trainticket.getProcedure();
            }
            else {
                refundMoney = trainticket.getPrice() - trainticket.getProcedure();
            }
            //判断退款
            if (!canRefund(trainorder, trainticket, new FuzzyRefund(), refundMoney, false, false)) {
                return refundLimit;
            }
            else {
                jso.put("returnmoney", refundMoney);
            }
        }
        else {
            jso.put("returnmoney", "");
        }
        String returnmsg = returnfailmsgById(returnfailid);
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
            //改签ID
            long changeId = trainticket.getChangeid();
            //查询改签
            Trainorderchange change = changeId > 0 ? Server.getInstance().getTrainService()
                    .findTrainOrderChangeById(changeId) : new Trainorderchange();
            //请求特征
            String reqtoekn = change != null ? change.getRequestReqtoken() : "";
            if (trainorder.getAgentid() == 86) {
                refundtoken = MD5Util.MD5Encode(trainticket.getId() + "@" + 2, "utf-8");
                jso.put("refundtoken", refundtoken);
            }
            //改签请求特征值
            jso.put("changereqtoken", ElongHotelInterfaceUtil.StringIsNull(reqtoekn) ? "" : reqtoekn);
        }
        else {
            if (trainorder.getAgentid() == 86) {
                refundtoken = MD5Util.MD5Encode(trainticket.getId() + "@" + 1, "utf-8");
                jso.put("refundtoken", refundtoken);
            }
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
        jso.put("passengerid", trainpassenger.getPassengerid());//乘客的passengerid
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
            String returnfailmsg = returnfailmsgById(returnfailid);
            try {
                returnfailmsg = URLEncoder.encode(returnfailmsg, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            jso.put("returnfailmsg", returnfailmsg);
        }
        // 时间戳标识
        jso.put("remarkTimeStamp", true);
        // 退票时间戳
        jso.put("refundTimeStamp", ElongHotelInterfaceUtil.StringIsNull(trainticket.getRefundTimeStamp()) ? ""
                : trainticket.getRefundTimeStamp());
        // 改签时间戳
        jso.put("changeTimeStamp", ElongHotelInterfaceUtil.StringIsNull(trainticket.getChangeTimeStamp()) ? ""
                : trainticket.getChangeTimeStamp());
        // 退票完成时间
        jso.put("refundSuccessTime", ElongHotelInterfaceUtil.StringIsNull(trainticket.getRefundsuccesstime()) ? ""
                : trainticket.getRefundsuccesstime());
        try {
            WriteLog.write("TongCheng退票回调",
                    "订单号:" + trainorder.getOrdernumber() + "地址:" + url + "-请求参数:" + jso.toString());
            result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            WriteLog.write("TongCheng退票回调", "订单号:" + trainorder.getOrdernumber() + "-回调结果:" + result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 线下退票回调退票成功
     * 
     * @param i
     * @param orderid
     * @return
     * @time 2014年12月12日 下午2:20:30
     * @author fiend
     */
    public String callBackTongChengRefundOffLine(Trainorder trainorder, Trainpassenger trainpassenger,
            Trainticket trainticket, boolean returnstate, String serverinfo, FuzzyRefund fuzzy, long reqtoken) {
        String result = "false";
        String refundtoken="";
        //同程模糊退
        boolean mohutui = "mohutui".equals(returnType);
        // 2:线下改签
        boolean IsOfflineChange = "2".equals(returnType);
        // Trainorder trainorder =
        // Server.getInstance().getTrainService().findTrainorder(orderid);
        // String url = getSysconfigString("tcTrainCallBack");
        String url = "http://" + serverinfo + "/cn_interface/tcTrainCallBack";
        JSONObject jso = new JSONObject();
        jso.put("method", "train_refund_callback");
        // String apiorderid = "TC_20141230151133916251";
        jso.put("apiorderid", trainorder.getQunarOrdernumber());
        jso.put("transactionid", trainorder.getOrdernumber());//订单号(交易号)
        // String returntype = "1";
        jso.put("returntype", IsOfflineChange ? "2" : "0");
        // String reqtoken = "7824374";
        jso.put("reqtoken", reqtoken);
        // String trainorderid = "EC11991540";
        jso.put("trainorderid", trainorder.getExtnumber());
        // boolean returnstate = true;
        jso.put("returnstate", returnstate);
        // String returnmoney = "1.00";
        jso.put("agentid", trainorder.getAgentid());
        //退款金额
        float refundMoney = 0;
        //模糊退、线下改签
        if (mohutui || IsOfflineChange) {
            refundMoney = trainticket.getTcProcedure();
        }
        else if (trainticket.getTcnewprice() != null && trainticket.getTcnewprice().floatValue() > 0) {
            refundMoney = trainticket.getTcnewprice() - trainticket.getProcedure();
        }
        else {
            refundMoney = trainticket.getPrice() - trainticket.getProcedure();
        }
        //判断退款
        if (!canRefund(trainorder, trainticket, fuzzy, refundMoney, mohutui, IsOfflineChange)) {
            return refundLimit;
        }
        else {
            jso.put("returnmoney", refundMoney);
        }
        // String returnmsg = "";
        jso.put("returnmsg", "");
        // String ticketid = "";
        jso.put("ticketid", "");
        // String ticket_no = "EC119915401050031";
        if (mohutui) {
            jso.put("ticket_no", "");
            if (trainorder.getAgentid() == 86) {//如果是高铁 增加一个退票的流水号
                refundtoken = MD5Util.MD5Encode(fuzzy.getId() + "@" + 3, "utf-8");
            
            }
            //改签
            if (fuzzy.getChangeId() > 0) {
                if (trainorder.getAgentid() == 86) {
                    refundtoken = MD5Util.MD5Encode(trainticket.getId() + "@" + 2, "utf-8");

                }
                //请求特征
                String reqtoekn = fuzzy.getChangeRequestReqtoken();
                //改签请求特征值
                jso.put("changereqtoken", ElongHotelInterfaceUtil.StringIsNull(reqtoekn) ? "" : reqtoekn);
            }
        }
        else if (trainticket.getChangeType() != null && trainticket.getChangeType().intValue() == 1
                && trainticket.getTcnewprice() != null && trainticket.getTcnewprice().floatValue() > 0
                && !ElongHotelInterfaceUtil.StringIsNull(trainticket.getTcticketno())) {
            jso.put("ticket_no", trainticket.getTcticketno());
            //改签ID
            long changeId = trainticket.getChangeid();
            //查询改签
            Trainorderchange change = changeId > 0 ? Server.getInstance().getTrainService()
                    .findTrainOrderChangeById(changeId) : new Trainorderchange();
            //请求特征
            String reqtoekn = change != null ? change.getRequestReqtoken() : "";
            //改签请求特征值
            if (trainorder.getAgentid() == 86) {
                refundtoken = MD5Util.MD5Encode(trainticket.getId() + "@" + 2, "utf-8");
            }
            jso.put("changereqtoken", ElongHotelInterfaceUtil.StringIsNull(reqtoekn) ? "" : reqtoekn);
        }
        else {
            jso.put("ticket_no", trainticket.getTicketno());
        }
        // String passengername = "崔波";
        try {
            jso.put("passengername", mohutui ? "" : URLEncoder.encode(trainpassenger.getName(), "utf-8"));
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        // int passporttypeseid = 1;
        jso.put("passporttypeseid", mohutui ? "" : getIdtype12306(trainpassenger.getIdtype()));
        // String passportseno = "342224198709121116";
        jso.put("passportseno", mohutui ? "" : trainpassenger.getIdnumber());
        jso.put("passengerid", trainpassenger.getPassengerid());//乘客的passengerid
        // boolean returnsuccess = true;
        jso.put("returnsuccess", returnstate);
        // String returntime = "2014-12-30 15:22:12";
        if (returnstate) {
            jso.put("returntime", TimeUtil.gettodaydate(4));
        }
        // String returnfailid = "";
        jso.put("returnfailid", "");
        // String returnfailmsg = "";
        jso.put("returnfailmsg", "");
        // 模糊退标识
        jso.put("mohutui", mohutui);
        // 时间戳标识
        jso.put("remarkTimeStamp", true);
        // 退票时间戳
        jso.put("refundTimeStamp", ElongHotelInterfaceUtil.StringIsNull(trainticket.getRefundTimeStamp()) ? ""
                : trainticket.getRefundTimeStamp());
        // 改签时间戳
        jso.put("changeTimeStamp", ElongHotelInterfaceUtil.StringIsNull(trainticket.getChangeTimeStamp()) ? ""
                : trainticket.getChangeTimeStamp());
        if(ElongHotelInterfaceUtil.StringIsNull(refundtoken)){
            refundtoken = MD5Util.MD5Encode(trainticket.getId() + "@" + 1, "utf-8");
            jso.put("refundtoken", refundtoken);
        }
        try {
            WriteLog.write("TongCheng退票线下回调",
                    "订单号:" + trainorder.getOrdernumber() + "地址:" + url + "-请求参数:" + jso.toString());
            result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            WriteLog.write("TongCheng退票线下回调", "订单号:" + trainorder.getOrdernumber() + "-回调结果:" + result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 调同程退票回调接口后日志记录
     * 
     * @param trainorder
     * @param callbackstr
     *            成功传null
     * @time 2014年12月30日 下午3:01:50
     * @author fiend
     */
    public void refundOrderRc(Trainorder trainorder, String callbackstr, Trainpassenger trainpassenger,
            Trainticket trainticket, boolean returnstate) {
        // 类型
        String type = "2".equals(returnType) ? "线下改签" : "退票";
        // 车票票号
        String ticketNo = "";
        if (trainticket.getChangeType() != null && trainticket.getChangeType().intValue() == 1
                && trainticket.getTcnewprice() != null && trainticket.getTcnewprice().floatValue() > 0
                && !ElongHotelInterfaceUtil.StringIsNull(trainticket.getTcticketno())) {
            ticketNo = trainticket.getTcticketno();
        }
        else {
            ticketNo = trainticket.getTicketno();
        }
        String str = "乘客[" + trainpassenger.getName() + "][" + trainpassenger.getIdnumber() + "]["
                + trainticket.getTickettypestr() + "]" + (returnstate ? "" : "拒绝") + type + ",票号:" + ticketNo;
        // 保存日志
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(trainorder.getId());
        // 回调成功
        if ("success".equalsIgnoreCase(callbackstr)) {
            rc.setContent(str + "---成功");
        }
        else {
            rc.setContent(str + "---失败:" + callbackstr);
        }
        rc.setStatus(Trainticket.REFUNDED);
        rc.setCreateuser("接口");
        rc.setYwtype(1);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
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
    //        rc.setStatus(status);// Trainticket.ISSUED
    //        rc.setCreateuser(createurser);// "12306"
    //        rc.setTicketid(ticketid);
    //        rc.setYwtype(yewutype);
    //        Server.getInstance().getTrainService().createTrainorderrc(rc);
    //    }

    // TODO=========================================================================工具=====================================================================================
    /**
     * 通过失败码号得到提示信息
     * 
     * @param returnfailid
     * @return
     * @time 2015年1月9日 上午11:56:59
     * @author fiend
     */
    public String returnfailmsgById(int returnfailid) {
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
        }
        return "";
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

}