package com.ccservice.b2b2c.atom.service12306.onlineRefund.thread;

import java.util.HashMap;
import java.net.URLEncoder;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.util.OnlineRefundUtil;

/**
 * 退票审核
 * @author WH
 * @time 2016年11月4日 上午9:22:15
 * @version 1.0
 */

public class CheckRefundThread extends Thread {

    private int random;

    private long orderId;

    private long ticketId;

    private Customeruser user;

    private boolean retryRefund;//内部重试退票

    private TongchengSupplyMethod supplyMethod;

    private static final String datatypeflag = "105";//REP标识

    private static final int CheckRefundCount = 30;//审核多次，失败变问题订单

    private static final long CheckFailWaitTime = 1500;//审核失败，等待的时间，单位：毫秒

    private static final long CheckTotalTime = CheckRefundCount * CheckFailWaitTime;//审核总时间，单位：毫秒，失败变问题订单

    public CheckRefundThread(long orderId, long ticketId, Customeruser user, boolean retryRefund, int random) {
        this.user = user;
        this.random = random;
        this.orderId = orderId;
        this.ticketId = ticketId;
        this.retryRefund = retryRefund;
        this.supplyMethod = new TongchengSupplyMethod();
    }

    public void run() {
        //乘客
        String passenger_name = "";
        //车票
        Trainticket ticket = new Trainticket();
        //释放账号次数、审核标识、REP超时（单位：毫秒）
        int free = 0, checkFlag = 0, timeout = 30 * 1000;
        //获取账号次数
        int get = user != null && user.getId() > 0 ? 1 : 0;
        //订单
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderId);
        //失败
        if (order == null || order.getId() != orderId) {
            //审核问题
            QuestionTicket(ticketId);
            //释放次数
            int freeCount = get - free;
            //还要释放
            if (freeCount > 0) {
                FreeNoCare(user, supplyMethod, freeCount);
            }
            //直接中断
            return;
        }
        //乘客
        out: for (Trainpassenger trainPassenger : order.getPassengers()) {
            for (Trainticket trainTicket : trainPassenger.getTraintickets()) {
                if (trainTicket.getId() == ticketId) {
                    ticket = trainTicket;
                    passenger_name = trainPassenger.getName();
                    break out;
                }
            }
        }
        //车票号
        JSONArray ticket_no = new JSONArray();
        //原有车票
        String ticketNo = ticket.getTicketno();
        //发车时间
        String departTime = ticket.getDeparttime();
        //改签车票
        if (ticket.getChangeid() > 0 && ticket.getChangeType() != null
                && (ticket.getChangeType() == 1 || ticket.getChangeType() == 2)) {
            ticketNo = ticket.getTcticketno();
            //线上改签
            if (ticket.getChangeType() == 1) {
                departTime = ticket.getTtcdeparttime();
            }
        }
        //ADD
        ticket_no.add(ticketNo);
        //请求JSON
        JSONObject json = new JSONObject();
        //开始时间
        long startTime = System.currentTimeMillis();
        //JSON赋值
        json.put("ticket_no", ticket_no);
        json.put("passenger_name", passenger_name);
        json.put("sequence_no", order.getExtnumber());
        json.put("order_date", order.getCreatetime().toString().split(" ")[0]);
        //开始审核
        out: for (int idx = 0; idx < CheckRefundCount; idx++) {
            try {
                //重置
                checkFlag = 0;
                //为空
                if (user == null) {
                    user = new Customeruser();
                }
                //Cookie
                String cookie = user.getCardnunber();
                json.put("cookie", cookie);//设置请求Cookie
                //最后一次
                boolean isLast = idx == CheckRefundCount - 1;
                //请求参数URLEncoder
                String jsonStr = URLEncoder.encode(json.toString(), "UTF-8");
                //Cookie为空
                boolean cookieIsNull = ElongHotelInterfaceUtil.StringIsNull(cookie);
                //获取REP
                RepServerBean rep = cookieIsNull ? new RepServerBean() : RepServerUtil.getRepServer(user, false);
                //请求参数
                String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr
                        + supplyMethod.JoinCommonAccountInfo(user, rep);
                //请求REP
                String retdata = cookieIsNull ? "用户未登录" : RequestUtil.post(rep.getUrl(), param, "UTF-8",
                        new HashMap<String, String>(), timeout);
                //用户未登录
                if (!cookieIsNull && retdata.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
                    //切换REP
                    rep = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
                    //类型正确
                    if (rep.getType() == 1) {
                        //重拼参数
                        param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr
                                + supplyMethod.JoinCommonAccountInfo(user, rep);
                        //重新请求
                        retdata = cookieIsNull ? "用户未登录" : RequestUtil.post(rep.getUrl(), param, "UTF-8",
                                new HashMap<String, String>(), timeout);
                    }
                }
                //用户未登录
                if (Account12306Util.accountNoLogin(retdata, user)) {
                    //非第一次、账号非空（第一次且账号为空则不释放）
                    if (idx > 0 || user.getId() > 0) {
                        //释放次数
                        free++;
                        //释放未登录
                        FreeNoLogin(user, supplyMethod);
                    }
                    //非最后一次、非不登录重试，重拿账号
                    if (!isLast && !user.isDontRetryLogin()) {
                        //获取次数
                        get++;
                        //获取账号
                        user = supplyMethod.getCustomeruserBy12306Account(order, random, true);
                    }
                    //重走
                    continue;
                }
                //解析数据
                JSONObject retobj = JSONObject.parseObject(retdata);
                //获取数据失败
                if (!retobj.getBooleanValue("success")) {
                    continue;
                }
                JSONArray refunds = retobj.getJSONArray("refunds");
                if (refunds == null || refunds.size() == 0) {
                    continue;
                }
                for (int i = 0; i < refunds.size(); i++) {
                    //REP结果
                    JSONObject refund = refunds.getJSONObject(i);
                    //存在结果
                    if (ticketNo.equals(refund.getString("ticket_no"))) {
                        //车票状态
                        String ticket_status_name = refund.getString("ticket_status_name");
                        //打印消息
                        System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + "---" + orderId + "---"
                                + ticketId + "---" + ticket_status_name);
                        //已支付、改签票、变更到站票
                        if ("已支付".equals(ticket_status_name) || "改签票".equals(ticket_status_name)
                                || "变更到站票".equals(ticket_status_name)) {
                            checkFlag = 2;
                        }
                        //退票成功
                        else if (ticket_status_name.contains("已退票") && ticket_status_name.contains("业务流水号")) {
                            //成功
                            checkFlag = 1;
                            //中断
                            break out;
                        }
                    }
                }
                //非最后一次
                if (!isLast) {
                    Thread.sleep(CheckFailWaitTime);//减少12306状态未变化可能
                }
            }
            catch (Exception e) {

            }
            finally {
                //非审核成功、时间到
                if (checkFlag != 1 && System.currentTimeMillis() - startTime >= CheckTotalTime) {
                    break out;
                }
            }
        }
        //审核成功
        if (checkFlag == 1) {
            //回调地址
            String trainRefundPriceUrl = PropertyUtil.getValue("trainRefundPriceUrl");
            //手续费
            float procedure = ticket.getProcedure() == null ? -1 : ticket.getProcedure();
            //数据校验
            if (procedure < 0 || ElongHotelInterfaceUtil.StringIsNull(trainRefundPriceUrl)) {
                //问题退票
                QuestionTicket(ticketId);
            }
            else {
                //更新状态
                SuccessTicket(ticketId);
                //接口类型
                int interfacetype = order.getInterfacetype() != null && order.getInterfacetype() > 0 ? order
                        .getInterfacetype().intValue() : supplyMethod.getOrderAttribution(order);
                //退款参数
                String url = trainRefundPriceUrl + "?trainorderid=" + orderId + "&ticketid=" + ticketId
                        + "&interfacetype=" + interfacetype + "&procedure=" + procedure + "&responseurl="
                        + trainRefundPriceUrl;
                //回调退款
                SendPostandGet.submitGet(url, "UTF-8");
            }
        }
        //退票失败
        else if (checkFlag == 2) {
            RefundFail(orderId, ticketId, ticketNo, departTime, ticket.getRefundRequestTime(), retryRefund);
        }
        //其他问题
        else {
            QuestionTicket(ticketId);
        }
        //释放次数
        int freeCount = get - free;
        //还要释放
        if (freeCount > 0) {
            FreeNoCare(user, supplyMethod, freeCount);
        }
    }

    //退票成功
    private void SuccessTicket(long ticketId) {
        //SQL
        String sql = "update T_TRAINTICKET set C_REFUNDTYPE = 3, C_REFUNDSUCCESSTIME = '"
                + ElongHotelInterfaceUtil.getCurrentTime() + "' where ID = " + ticketId;
        //更新
        Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
    }

    //退票失败
    private void RefundFail(long orderId, long ticketId, String ticketNo, String departTime, String requestTime,
            boolean retryRefund) {
        //SQL
        String sql = "update T_TRAINTICKET set C_REFUNDTYPE = 0, C_ISQUESTIONTICKET = " + Trainticket.REFUNDQUESTION
                + ", C_STATUS = " + Trainticket.REFUNDROCESSING + " where ID = " + ticketId + " and C_STATUS = "
                + Trainticket.WAITREFUND;
        //退票问题
        boolean updateFlag = Server.getInstance().getSystemService().excuteAdvertisementBySql(sql) > 0;
        //重新丢MQ退票
        if (updateFlag && !retryRefund) {
            new OnlineRefundUtil().retryRefund(orderId, ticketId, ticketNo, departTime, requestTime, true);
        }
    }

    //退票问题
    private void QuestionTicket(long ticketId) {
        //SQL
        String sql = "update T_TRAINTICKET set C_REFUNDTYPE = 0, C_ISQUESTIONTICKET = " + Trainticket.CHECKQUESTION
                + " where ID = " + ticketId + " and C_STATUS = " + Trainticket.WAITREFUND;
        //更新
        Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
    }

    //账号系统释放，未登录
    private void FreeNoLogin(Customeruser user, TongchengSupplyMethod supplyMethod) {
        if (user != null && user.isFromAccountSystem()) {
            supplyMethod.freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree,
                    AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
        }
    }

    //账号系统释放，不关心
    private void FreeNoCare(Customeruser user, TongchengSupplyMethod supplyMethod, int freeCount) {
        if (user != null && user.isFromAccountSystem()) {
            supplyMethod.freeCustomeruser(user, AccountSystem.FreeNoCare, freeCount, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
        }
    }

}