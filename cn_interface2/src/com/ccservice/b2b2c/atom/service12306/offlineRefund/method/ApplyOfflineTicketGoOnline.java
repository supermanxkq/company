package com.ccservice.b2b2c.atom.service12306.offlineRefund.method;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.util.RefundTicketUtil;

/**
 * 申请线下退票，未发车且未取票的走线上
 * @author WH
 * @time 2016年4月25日 上午10:29:01
 * @version 1.0
 */

public class ApplyOfflineTicketGoOnline extends TongchengSupplyMethod {

    //订单ID
    private long trainOrderId;

    //REP超时时间，单位：毫秒
    private static final int timeout = 60 * 1000;

    //登录失败，重试次数
    private static final int loginErrorCount = 3;

    //多次请求12306
    private static final int totalErrorCount = 3;

    //退票标识
    private static final String datatypeflag = "100";

    //可退时间，单位：分钟
    public static final int RefundTimeLimitIntValue = 30;

    //日志名称
    private static final String logName = "12306_GT_火车票申请线下退票走线上";

    //构造
    public ApplyOfflineTicketGoOnline(long trainOrderId) {
        this.trainOrderId = trainOrderId;
    }

    @SuppressWarnings("rawtypes")
    public void operate() {
        //可退
        Map<Long, String> canRefundMap = new HashMap<Long, String>();
        //捕捉
        try {
            //SQL
            String sql = "select ID, C_STATUS, C_ISAPPLYTICKET, C_APPLYTICKETFLAG, C_CHANGETYPE, "
                    + "C_STATE12306, C_DEPARTTIME, C_TTCDEPARTTIME, C_TICKETNO, C_TCTICKETNO "
                    + "from T_TRAINTICKET t with(nolock) where t.C_ORDERID = " + trainOrderId;
            //查询订单
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            //循环判断
            for (int i = 0; i < list.size(); i++) {
                //车票ID
                long ticketId = 0;
                //异常捕捉
                try {
                    Map map = (Map) list.get(i);
                    //ID
                    ticketId = RefundTicketUtil.getLongMapValue(map, "ID");
                    //状态
                    int status = RefundTicketUtil.getIntMapValue(map, "C_STATUS");
                    int status12306 = RefundTicketUtil.getIntMapValue(map, "C_STATE12306");
                    int isApplyTicket = RefundTicketUtil.getIntMapValue(map, "C_ISAPPLYTICKET");
                    int applyTicketFlag = RefundTicketUtil.getIntMapValue(map, "C_APPLYTICKETFLAG");
                    //改签
                    int changeType = RefundTicketUtil.getIntMapValue(map, "C_CHANGETYPE");
                    //票号
                    String ticketNo = RefundTicketUtil.getStringMapValue(map, "C_TICKETNO");
                    String tcTicketNo = RefundTicketUtil.getStringMapValue(map, "C_TCTICKETNO");
                    //发车时间
                    String departTime = RefundTicketUtil.getStringMapValue(map, "C_DEPARTTIME");
                    String tcDepartTime = RefundTicketUtil.getStringMapValue(map, "C_TTCDEPARTTIME");
                    //已取票、退票完成、账号被封、手机待核验、旅游旺季
                    if (RefundTicketUtil.dontRefundStatus(status12306)) {
                        continue;
                    }
                    //线下退款申请、未改签或线上改签
                    if (ticketId > 0 && status == 5 && isApplyTicket == 2 && applyTicketFlag == 1
                            && (changeType == 0 || changeType == 1)) {
                        //发车时间
                        departTime = changeType == 0 ? departTime : tcDepartTime;
                        //判断时间
                        long trainStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(departTime).getTime();
                        //开车时间-当前时间在30分钟以内
                        if (trainStartTime - System.currentTimeMillis() <= RefundTimeLimitIntValue * 60 * 1000) {
                            continue;
                        }
                        //可退车票，Map<车票ID, 票号>
                        canRefundMap.put(ticketId, changeType == 0 ? ticketNo : tcTicketNo);
                    }
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException(logName + "_异常", e, trainOrderId + "_" + ticketId);
                }
            }
            //存在可退
            if (canRefundMap.size() > 0) {
                onlineRefund(canRefundMap);
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "_异常", e, trainOrderId + "_" + canRefundMap.size());
        }
    }

    /**
     * 线上退票
     */
    private void onlineRefund(Map<Long, String> canRefundMap) {
        //车票ID
        Set<Long> ids = canRefundMap.keySet();
        //随机数据
        int random = new Random().nextInt(900000) + 100000;
        //记录日志
        WriteLog.write(logName, random + ">>>>>车票[ " + trainOrderId + " / " + ids + " ]开始退票");
        //查订票
        Trainorder trainOrder = Server.getInstance().getTrainService().findTrainorder(trainOrderId);
        //车票明细
        Map<Long, Trainticket> ticketMap = new HashMap<Long, Trainticket>();
        //循环乘客
        for (Trainpassenger passenger : trainOrder.getPassengers()) {
            //循环车票
            for (Trainticket ticket : passenger.getTraintickets()) {
                ticket.setTrainpassenger(passenger);
                ticketMap.put(ticket.getId(), ticket);
            }
        }
        //1、下单账户
        Customeruser user = new Customeruser();
        //循环取账号
        for (int i = 0; i < loginErrorCount; i++) {
            //取账号
            user = orderUser(trainOrder, user, random);
            //不用重试、成功获取cookie
            if (user != null
                    && (user.isDontRetryLogin() || !ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber()))) {
                break;
            }
        }
        //未获取到Cookie
        if (user == null || user.isDontRetryLogin() || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //登录信息
            String msg = user.getNationality() == null ? "" : user.getNationality();
            //账号被封
            if (msg.contains("用户信息被他人冒用，请重新在网上注册新的账户，为了确保您的购票安全，您还需尽快到就近的办理客运售票业务的铁路车站完成身份核验")) {
                //第一张票
                int countTicket = 0;
                //更新车票
                for (long ticketId : ids) {
                    countTicket++;
                    update12306Status(ticketId, 18, countTicket == 1 ? msg : "");
                }
                //释放账号
                FreeNoCare(user);
                //记录日志
                WriteLog.write(logName, random + ">>>>>车票[ " + trainOrderId + " / " + ids + " ]退票失败，" + msg);
            }
            else {
                //释放账号
                FreeNoCare(user);
                //文本日志
                WriteLog.write(logName, random + ">>>>>车票[ " + trainOrderId + " / " + ids + " ]退票失败，未获取到下单账号Cookie");
            }
            //中断返回
            return;
        }
        //账号不可用
        int accountEnable = 0;
        //12306单号
        String sequence_no = trainOrder.getExtnumber();
        //已处理退票
        List<Long> refundedTicket = new ArrayList<Long>();
        //时间格式化
        SimpleDateFormat totalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //订单创建时间，用于12306通过时间查询订单
        String order_date = totalFormat.format(trainOrder.getCreatetime()).split(" ")[0];
        //循环退票
        for (long ticketId : ids) {
            //异常捕捉
            try {
                //票号
                String ticket_no = canRefundMap.get(ticketId);
                //请求REP参数
                JSONObject req = new JSONObject();
                req.put("refundFlag", true);
                req.put("ticket_no", ticket_no);
                req.put("order_date", order_date);
                req.put("sequence_no", sequence_no);
                req.put("cookie", user.getCardnunber());
                req.put("RefundTimeLimit", RefundTimeLimitIntValue);
                req.put("refundType", "1");//符合条件的是否可以直接退票，1：是
                req.put("refundTime", RefundTimeLimitIntValue + 60);//退票时间+1小时内，直接退
                req.put("passenger_name", ticketMap.get(ticketId).getTrainpassenger().getName());//乘客姓名，用于查订单
                //REP退票
                JSONObject obj12306 = refund12306(ticketId, trainOrder, req, user, random, 0, 0);
                //已处理的
                refundedTicket.add(ticketId);
                //Cookie
                String cookie = obj12306.getString("cookie");
                //更新Cookie
                if (!ElongHotelInterfaceUtil.StringIsNull(cookie)) {
                    user.setCardnunber(cookie);
                }
                //退票成功
                if (obj12306.getBooleanValue("success")) {
                    //车票票款
                    float ticket_price = obj12306.getFloatValue("ticket_price");
                    //应退票款
                    float return_price = obj12306.getFloatValue("return_price");
                    //退票费
                    float return_cost = obj12306.getFloatValue("return_cost");
                    //记录日志
                    createtrainorderrc(1, "车票[" + ticketId + "]退票成功，等待退款，车票票款：" + ticket_price + "，应退票款："
                            + return_price + "，退票费：" + return_cost, trainOrderId, ticketId, Trainticket.WAITREFUND,
                            "系统");
                }
                //退票失败
                else {
                    //失败原因
                    String msg = obj12306.getString("msg");
                    //车站已取票
                    if (("车票[" + ticket_no + "]已出票").equals(msg)) {
                        update12306Status(ticketId, 8, msg);
                    }
                    //车站已改签
                    else if (("车票[" + ticket_no + "]已改签").equals(msg)) {
                        update12306Status(ticketId, 8, msg);
                    }
                    //车票已退票
                    else if (("车票[" + ticket_no + "]已退票").equals(msg)) {
                        update12306Status(ticketId, 17, "");
                    }
                    //12306提示错误
                    else if (!ElongHotelInterfaceUtil.StringIsNull(msg) && obj12306.getBooleanValue("isLimitTran")) {
                        //手机核验
                        if (msg.contains("“我的12306”的“账号安全”中选择“手机核验”")) {
                            accountEnable = 19;
                            update12306Status(ticketId, accountEnable, msg);
                            break;
                        }
                        //账号被封
                        else if ((msg.contains("根据本网站的服务条款，您需要提供真实、准确的本人资料") || msg
                                .contains("根据本网站服务条款，您需要提供真实、准确的本人资料"))
                                && (msg.contains("请您尽快到就近的办理客运售票业务的铁路车站完成身份核验") || msg
                                        .contains("请您到就近办理客运售票业务的铁路车站完成身份核验"))) {
                            accountEnable = 18;
                            update12306Status(ticketId, accountEnable, msg);
                            break;
                        }
                        //旅游旺季
                        else if (msg
                                .contains("凡通过互联网或手机购买的本次列车车票，如需办理退票、改签和变更到站等变更业务，请持乘车人身份证件原件到就近车站办理，代办时还需持代办人的身份证件原件")) {
                            accountEnable = 20;
                            update12306Status(ticketId, accountEnable, msg);
                            break;
                        }
                        //信息有误
                        else if (msg.contains("您填写的身份信息有误，未能通过国家身份信息管理权威部门核验，请检查您的姓名和身份证件号码填写是否正确。如有疑问，可致电12306客服咨询")
                                || msg.contains("您的用户信息被他人冒用，请重新在网上注册新的账户，为了确保您的购票安全，您还需尽快到就近的办理客运售票业务的铁路车站完成身份核验，谢谢您对12306网站的支持")) {
                            accountEnable = 18;
                            update12306Status(ticketId, accountEnable, msg);
                            break;
                        }
                    }
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(logName + "_异常", e, trainOrderId + "_" + ticketId);
            }
        }
        //账号被封
        if (accountEnable > 0) {
            for (long ticketId : ids) {
                //未处理
                if (!refundedTicket.contains(ticketId)) {
                    update12306Status(ticketId, accountEnable, "");
                }
            }
        }
        //释放账号
        FreeNoCare(user);
    }

    /**
     * 12306退票
     */
    private JSONObject refund12306(long ticketId, Trainorder trainOrder, JSONObject req, Customeruser user, int random,
            int loginError, int elseError) {
        //REP
        String url = "";
        String retdata = "";
        JSONObject obj12306 = new JSONObject();
        try {
            //REP
            RepServerBean rep = RepServerUtil.getRepServer(user, false);
            //地址
            url = rep.getUrl();
            //REP地址为空
            if (ElongHotelInterfaceUtil.StringIsNull(url)) {
                throw new Exception("REP地址为空");
            }
            //请求参数URLEncoder
            String jsonStr = URLEncoder.encode(req.toJSONString(), "UTF-8");
            //请求参数
            String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr + JoinCommonAccountInfo(user, rep);
            //请求REP
            retdata = RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), timeout);
            //用户未登录
            if (retdata.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
                //切换REP
                rep = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
                //类型正确
                if (rep.getType() == 1) {
                    //地址
                    url = rep.getUrl();
                    //重拼参数
                    param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr + JoinCommonAccountInfo(user, rep);
                    //重新请求
                    retdata = RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), timeout);
                }
            }
            //返回数据
            obj12306 = JSONObject.parseObject(retdata);
        }
        catch (Exception e) {
            if (!"REP地址为空".equals(e.getMessage())) {
                ExceptionUtil.writelogByException(logName + "_异常", e, trainOrderId + "_" + ticketId);
            }
        }
        finally {
            if (retdata == null) {
                retdata = "";
            }
            if (obj12306 == null) {
                obj12306 = new JSONObject();
            }
            WriteLog.write(logName, random + ">>>>>>车票[ " + loginError + " / " + elseError + " / " + trainOrderId
                    + " / " + ticketId + " ]退票，REP服务器地址>>>>>" + url + ">>>>>REP返回>>>>>" + retdata);
        }
        //查询第[1]页已完成订单失败
        boolean queryOrderError = false;
        if (ElongHotelInterfaceUtil.StringIsNull(retdata)) {
            queryOrderError = true;
        }
        else if (retdata.contains("查询第") && retdata.contains("页已完成订单失败")) {
            queryOrderError = true;
        }
        else if (retdata.contains("未查询到订单[" + trainOrder.getExtnumber() + "]")) {
            queryOrderError = true;
        }
        boolean noLogin = Account12306Util.accountNoLogin(retdata, user);
        boolean elseFail = queryOrderError || retdata.contains("网络繁忙") || retdata.contains("退票异常，请确认");
        //用户未登录
        if (noLogin) {
            //重置
            elseError = 0;
            //失败+1
            loginError = loginError + 1;
            //退票重试
            if (loginError < loginErrorCount) {
                //重新登陆
                user = orderUser(trainOrder, user, random);
                //更新Cookie
                req.put("cookie", user.getCardnunber());
                //退票重试
                return refund12306(ticketId, trainOrder, req, user, random, loginError, elseError);
            }
        }
        //其他退票失败
        else if (elseFail) {
            //失败+1
            elseError = elseError + 1;
            //退票重试
            if (elseError < totalErrorCount) {
                return refund12306(ticketId, trainOrder, req, user, random, loginError, elseError);
            }
        }
        //Cookie
        obj12306.put("cookie", user.getCardnunber());
        //返回结果
        return obj12306;
    }

    /**
     * 更新车票12306状态
     * @param errorMsg 错误信息
     */
    private void update12306Status(long ticketId, int status, String errorMsg) {
        if (ticketId > 0) {
            //SQL
            String sql = "update T_TRAINTICKET set C_STATE12306 = " + status + " where ID  = " + ticketId;
            //UPDATE
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            //信息非空
            if (!ElongHotelInterfaceUtil.StringIsNull(errorMsg)) {
                createtrainorderrc(1, "车票[" + ticketId + "]退票失败：" + errorMsg, trainOrderId, ticketId,
                        Trainticket.NONREFUNDABLE, "系统");
            }
        }
    }

    /**
     * 账号系统释放，不关心
     */
    private void FreeNoCare(Customeruser user) {
        freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                AccountSystem.NullDepartTime);
    }

    /**
     * 账号系统释放，未登录
     */
    private void FreeNoLogin(Customeruser user) {
        freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                AccountSystem.NullDepartTime);
    }

    /**
     * 下单账户
     */
    private Customeruser orderUser(Trainorder trainOrder, Customeruser user, int random) {
        try {
            //不重试
            if (user != null && user.isDontRetryLogin()) {
                return user;
            }
            //下单用户名
            String createAccount = trainOrder.getSupplyaccount();
            //下单用户为空
            if (ElongHotelInterfaceUtil.StringIsNull(createAccount)) {
                return new Customeruser();
            }
            //已获取过，释放账号
            if (user != null && !ElongHotelInterfaceUtil.StringIsNull(user.getLoginname())) {
                FreeNoLogin(user);
            }
            //账号系统，重新获取
            return getCustomeruserBy12306Account(trainOrder, random, true);
        }
        catch (Exception e) {
            return new Customeruser();
        }
    }

}