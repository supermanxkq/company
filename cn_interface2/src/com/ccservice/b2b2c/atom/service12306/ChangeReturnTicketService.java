package com.ccservice.b2b2c.atom.service12306;

import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.util.OnlineRefundUtil;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.method.RefundTicketMethod;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.changeRefund.method.ChangeRefundMethod;

/**
 * 改签退
 * @author WH
 */

public class ChangeReturnTicketService extends TongchengSupplyMethod {

    //REP超时时间，单位：毫秒
    private static final int timeout = 60 * 1000;

    //登录失败，重试次数
    private static final int loginErrorCount = 3;

    //多次请求12306
    private static final int totalErrorCount = 3;

    //订单详情
    //private static final String detailflag = "104";

    //REP退票
    private static final String datatypeflag = "100";

    //REP取消改签超时时间，单位：毫秒
    private static final int cancelTimeOut = 30 * 1000;

    private static final String logName = "12306_GT_火车票改签退";

    /**
     * 退票问题
     * @param ticketId 车票ID
     * @param isQuestionTicket 问题类型
     */
    private void refundQuestion(long ticketId, int isQuestionTicket) {
        if (ticketId > 0) {
            //SQL
            String sql = "update T_TRAINTICKET set C_REFUNDTYPE = 0, C_ISQUESTIONTICKET = " + isQuestionTicket
                    + " where ID  = " + ticketId + " and C_STATUS = " + Trainticket.REFUNDROCESSING;
            //UPDATE
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
    }

    /**
     * 更新车票12306状态
     */
    private void update12306Status(long ticketId, int status) {
        if (ticketId > 0) {
            //SQL
            String sql = "update T_TRAINTICKET set C_STATE12306 = " + status + " where ID  = " + ticketId;
            //UPDATE
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
    }

    /**
     * 账号系统释放，不关心
     */
    private void FreeNoCare(Customeruser user) {
        if (user != null && user.isFromAccountSystem()) {
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
        }
    }

    /**
     * 账号系统释放，未登录
     */
    private void FreeNoLogin(Customeruser user) {
        if (user != null && user.isFromAccountSystem()) {
            freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
        }
    }

    //退票结束
    private static final int RefundOver = 1;

    //退票审核
    private static final int RefundCheck = 2;

    //走改签退
    private static final int ChangeRefund = 3;

    /**
     * 改签退
     * @param trainOrder 火车票订单
     * @param trainTicket 订单要退的车票
     * @param changeRefund true时表示改签退过来的，直接退
     */
    public void operate(long trainOrderId, long trainTicketId, boolean changeRefund) {
        mq(trainOrderId, trainTicketId, changeRefund, false);
    }

    /**
     * MQ逻辑
     * @param retryRefund true:内部重试退票
     */
    public void mq(long trainOrderId, long trainTicketId, boolean changeRefund, boolean retryRefund) {
        //退票处理
        int flag = deal(trainOrderId, trainTicketId, changeRefund, retryRefund);
        //同程>>发送消息
        if (flag == RefundOver && OnlineRefundUtil.istc()) {
            new RefundTicketMethod().refundOver(trainOrderId, trainTicketId);
        }
    }

    /**
     * 改签退
     * @author WH
     * @time 2016年11月3日 下午3:34:50
     * @version 1.0
     * @param trainOrder 火车票订单
     * @param trainTicket 订单要退的车票
     * @param retryRefund 内部重试退票
     * @param changeRefund true时表示改签退过来的，直接退
     * @return 标识，判断直接走审核
     */
    private int deal(long trainOrderId, long trainTicketId, boolean changeRefund, boolean retryRefund) {
        //订单
        Trainorder trainOrder = Server.getInstance().getTrainService().findTrainorder(trainOrderId);
        //车票
        Trainticket trainTicket = new Trainticket();
        //乘客
        Trainpassenger trainPassenger = new Trainpassenger();
        //乘客
        List<Trainpassenger> passengers = trainOrder.getPassengers();
        for (Trainpassenger passenger : passengers) {
            List<Trainticket> tickets = passenger.getTraintickets();
            for (Trainticket ticket : tickets) {
                if (ticket.getId() == trainTicketId) {
                    trainTicket = ticket;
                    trainPassenger = passenger;
                    trainTicket.setTrainpassenger(trainPassenger);
                    break;
                }
            }
        }
        if (trainTicket.getId() == 0 || trainTicket.getRefundType() == null
                || trainTicket.getRefundType().intValue() != -1
                || trainTicket.getStatus() != Trainticket.REFUNDROCESSING) {
            return RefundOver;
        }
        //随机数据
        int random = new Random().nextInt(900000) + 100000;
        //日志
        WriteLog.write(logName, random + ">>>>>车票[ " + trainOrderId + " / " + trainTicketId + " ]开始改签退");
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
        //为空，重置
        user = user == null ? new Customeruser() : user;
        //未获取到Cookie
        if (user == null || user.isDontRetryLogin() || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //退票问题
            refundQuestion(trainTicketId, Trainticket.REFUNDQUESTION);
            //第三方账号
            if (trainOrder.getOrdertype() == 3 || trainOrder.getOrdertype() == 4) {
                //日志
                String logContent = user.isDontRetryLogin() ? user.getNationality() : "客人账号登录失败";
                //直接拒
                refuseRefund(trainOrder, trainTicket, 51, user.isDontRetryLogin() ? user.getNationality() : "",
                        logContent, 0);
                //文本日志
                WriteLog.write(logName, random + ">>>>>车票[ " + trainOrderId + " / " + trainTicketId + " ]改签退失败，"
                        + (user.isDontRetryLogin() ? user.getNationality() : "未获取到用户12306账号Cookie"));
            }
            //账号被封
            else if (!ElongHotelInterfaceUtil.StringIsNull(user.getNationality())
                    && user.getNationality().contains(
                            "您的用户信息被他人冒用，请重新在网上注册新的账户，为了确保您的购票安全，您还需尽快到就近的办理客运售票业务的铁路车站完成身份核验，谢谢您对12306网站的支持")) {
                //日志内容
                String msg = "您的用户信息被他人冒用，请重新在网上注册新的账户，为了确保您的购票安全，您还需尽快到就近的办理客运售票业务的铁路车站完成身份核验，谢谢您对12306网站的支持。";
                //直接拒退
                disableAccount(trainOrder, trainTicket, msg, msg, "系统接口");
                //文本日志
                WriteLog.write(logName, random + ">>>>>车票[ " + trainOrderId + " / " + trainTicketId + " ]改签退失败，" + msg);
            }
            else {
                //文本日志
                WriteLog.write(logName, random + ">>>>>车票[ " + trainOrderId + " / " + trainTicketId
                        + " ]改签退失败，未获取到下单账号Cookie");
            }
            //释放账号
            FreeNoCare(user);
            //中断返回
            return RefundOver;
        }
        //cookie
        String cookie = user.getCardnunber();
        //12306单号
        String sequence_no = trainOrder.getExtnumber();
        //时间格式化
        SimpleDateFormat totalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //订单创建时间，用于12306通过时间查询订单
        String order_date = totalFormat.format(trainOrder.getCreatetime()).split(" ")[0];
        //去哪儿代付，暂不直接退
        //        boolean isQunarPay = trainOrder.getSupplypayway() != null
        //                && trainOrder.getSupplypayway().intValue() == Paymentmethod.Qunarpay ? true : false;
        //2、退票操作
        //     |--> A、直接退
        //     |--> B、改签退
        //     |--> C、退票失败或不可退
        //退票
        String refundTicket = refundTicket(cookie, sequence_no, order_date, trainOrder, trainTicket, trainPassenger,
                user, 0, changeRefund, random);
        //退票成功或不能退，中断，不释放账号，统一在退款释放
        if ("true".equals(refundTicket) || "false".equals(refundTicket) || "error".equals(refundTicket)) {
            //REP信息
            String repInfo = user.getMemberemail();
            //改签退>>原先筛选车次后走改签退时没有释放
            if (changeRefund) {
                //申请改签成功已释放REP
                //REP信息置空，不同时释放
                user.setMemberemail("");
                //释放账号
                FreeNoCare(user);
                //重新赋值
                user.setMemberemail(repInfo);
            }
            //释放账号
            if (!"true".equals(refundTicket)) {
                FreeNoCare(user);
            }
            //释放REP
            else if (!OnlineRefundUtil.istc()) {
                RepServerUtil.freeRepServerByAccount(user);
            }
            //退票审核
            else {
                new RefundTicketMethod().refundCheck(trainOrderId, trainTicketId, user, retryRefund, random);
            }
            //中断返回
            return "true".equals(refundTicket) ? RefundCheck : RefundOver;
        }
        //改签前退票手续费
        float oldFee = JSONObject.parseObject(refundTicket).getFloatValue("return_cost");
        //手续费大于0才改签退
        if (oldFee > 0) {
            //车票车站
            String to_station_name = trainTicket.getArrival();
            String from_station_name = trainTicket.getDeparture();
            //选改签车次、座席
            Train train = selChangeTrain(from_station_name, to_station_name, trainTicket.getDeparttime(),
                    trainTicket.getPrice(), oldFee);
            //获取到最佳车次，异步改签请求
            if (train != null
                    && train.getDistance() > 0
                    && new ChangeRefundMethod().requestChange(trainOrder, trainTicket, trainPassenger, train,
                            refundTicket)) {
                //释放REP
                RepServerUtil.freeRepServerByAccount(user);
                //文本日志
                WriteLog.write(
                        logName + "_改签车次",
                        random + ">>>>>车票[ " + trainOrderId + " / " + trainTicketId + " ]走改签退，新车次信息>>"
                                + train.getTraincode() + " / " + train.getStartdate() + " " + train.getStarttime()
                                + " / " + train.getDistance() + " / " + train.getSeattypeval());
                //申请改签成功，后续不走
                return ChangeRefund;
            }
        }
        //未选到车次或申请改签失败，退票处理
        String refundAgain = refundTicket(cookie, sequence_no, order_date, trainOrder, trainTicket, trainPassenger,
                user, 0, true, random);
        //释放账号
        if (!"true".equals(refundAgain)) {
            FreeNoCare(user);
        }
        //释放REP
        else if (!OnlineRefundUtil.istc()) {
            RepServerUtil.freeRepServerByAccount(user);
        }
        //退票审核
        else {
            new RefundTicketMethod().refundCheck(trainOrderId, trainTicketId, user, retryRefund, random);
        }
        //返回结果
        return "true".equals(refundAgain) ? RefundCheck : RefundOver;
    }

    /**
     * 选改签车次、座席
     */
    private Train selChangeTrain(String from_station_name, String to_station_name, String oldDepartTime,
            float oldPrice, float oldFee) {
        try {
            SelectBestTrainForReturnTicket sel = new SelectBestTrainForReturnTicket();
            return sel.getBest(from_station_name, to_station_name, oldDepartTime, oldPrice, oldFee);
        }
        catch (Exception e) {
            return new Train();
        }
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
            //重新登录用户
            if (user != null && !ElongHotelInterfaceUtil.StringIsNull(user.getLoginname())) {
                //账号系统，先释放
                FreeNoLogin(user);
                //账号系统，重新获取
                return getCustomeruserBy12306Account(trainOrder, random, true);
            }
            else {
                return getCustomeruserBy12306Account(trainOrder, random, true);
            }
        }
        catch (Exception e) {
            return new Customeruser();
        }
    }

    /**
     * 不走改签退代理
     */
    private boolean dontChangeRefund(long agentId) {
        //结果
        boolean result = false;
        //ID
        String ids = PropertyUtil.getValue("dontChangeRefundAgentIds");
        //非空
        if (ElongHotelInterfaceUtil.StringIsNull(ids)) {
            return result;
        }
        for (String id : ids.split("@")) {
            if (String.valueOf(agentId).equals(id)) {
                result = true;
                break;
            }
        }
        //返回
        return result;
    }

    /**
     * 退票操作，以票为单位
     * @param refundFlag true时，直接退
     * @param refundType 退票类型，0：直接退；1：改签退；2：改签后退票
     */
    private String refundTicket(String cookie, String sequence_no, String order_date, Trainorder trainOrder,
            Trainticket ticket, Trainpassenger passenger, Customeruser oldUser, int refundType, boolean refundFlag,
            int random) {
        //12306退票走改签退，1：是
        String refundFlagValue = "0";
        //KEY
        String refundFlagKey = "changeRefundFlag";
        //内存无数据
        if (!Server.getInstance().getDateHashMap().containsKey(refundFlagKey)) {
            //从配置文件取数据
            refundFlagValue = PropertyUtil.getValue(refundFlagKey);
            //配置文件无数据，设为0
            refundFlagValue = refundFlagValue == null ? "0" : refundFlagValue;
            //配置文件放到内存
            Server.getInstance().getDateHashMap().put(refundFlagKey, refundFlagValue);
        }
        else {
            refundFlagValue = Server.getInstance().getDateHashMap().get(refundFlagKey);
        }
        //直接退票标示
        refundFlag = "1".equals(refundFlagValue) ? refundFlag : true;
        //订单号
        long orderId = trainOrder.getId();
        //获取车票号
        String ticket_no = ticket.getTicketno();
        //判断是否是改签票
        long changeId = ticket.getChangeid();
        Trainorderchange change = new Trainorderchange();
        //车票改签过，进行查询
        if (changeId > 0) {
            //查询改签
            change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
            //查询失败
            if (change == null || change.getId() != changeId) {
                //改签类型 1:线上改签、2:改签退
                if (ticket.getChangeType() == 1 || ticket.getChangeType() == 2) {
                    //直接退票
                    refundFlag = true;
                    //改签票号
                    ticket_no = ticket.getTcticketno();
                    //退票类型
                    refundType = ticket.getChangeType() == 1 ? 2 : 1;
                }
            }
            //改签完成
            else if (change.getTcstatus() == Trainorderchange.FINISHCHANGE) {
                //直接退票
                refundFlag = true;
                //改签票号
                ticket_no = ticket.getTcticketno();
                //1:改退签  0:改签
                int tcischangerefund = change.getTcischangerefund() == null ? 0 : change.getTcischangerefund()
                        .intValue();
                //改签退
                if (refundType == 1 || tcischangerefund == 1) {
                    refundType = 1;
                }
                //改签后退票
                else {
                    refundType = 2;
                }
            }
        }
        //非成人票，直接退
        if (!refundFlag && ticket.getTickettype() != 1) {
            refundFlag = true;
        }
        //不走改签退代理
        if (!refundFlag && dontChangeRefund(trainOrder.getAgentid())) {
            refundFlag = true;
        }
        //客人账号订单，直接退
        if (!refundFlag && ((trainOrder.getOrdertype() == 3 || trainOrder.getOrdertype() == 4))) {
            refundFlag = true;
        }
        //原发车时间不符合改签退，直接退
        if (!refundFlag && refundType == 0
                && !new SelectBestTrainForReturnTicket().GoChangeRefund(ticket.getDeparttime())) {
            refundFlag = true;
        }
        //请求REP参数
        JSONObject req = new JSONObject();
        req.put("cookie", cookie);
        req.put("ticket_no", ticket_no);
        req.put("order_date", order_date);
        req.put("sequence_no", sequence_no);
        //用于查订单
        req.put("passenger_name", passenger.getName());
        //退票类型，true：直接退票，false：只获取手续费
        req.put("refundFlag", refundFlag);//直接退票
        //退票时间限制,单位:分钟
        String RefundTimeLimit = getSysconfigString("RefundTimeLimit");
        int RefundTimeLimitIntValue = Integer.parseInt(RefundTimeLimit);
        req.put("RefundTimeLimit", RefundTimeLimitIntValue);
        req.put("refundType", PropertyUtil.getValue("refundType"));
        req.put("refundTime", RefundTimeLimitIntValue + 60);//退票时间+1小时内，直接退

        /*****获取已退票的退款金额_开始*****/
        //业务流水号长度，当前26
        req.put("tradeNoLen", Integer.parseInt(PropertyUtil.getValue("tradeNoLen")));
        //如果是已退，获取退款详情
        req.put("needRefundDetail", "1".equals(PropertyUtil.getValue("needRefundDetail")));
        /*****获取已退票的退款金额_结束*****/

        //REP退票
        JSONObject obj12306 = refund12306(ticket, trainOrder, req, oldUser, random, 0, 0);
        //退票成功
        boolean refundSuccess = obj12306.getBooleanValue("success");
        //车票改签中
        if (!refundSuccess && ("车票[" + ticket_no + "]改签中").equals(obj12306.getString("msg"))) {
            //取消成功
            boolean cancelSuccess = false;
            //多次尝试
            for (int i = 0; i < totalErrorCount; i++) {
                //取消改签
                int cancelResult = cancelChange(trainOrder, ticket, change, oldUser, sequence_no,
                        i == totalErrorCount - 1, random);
                //取消成功
                if (cancelResult == 1) {
                    //标识
                    cancelSuccess = true;
                    //中断
                    break;
                }
                //非取消重试
                else if (cancelResult != 2) {
                    break;
                }
            }
            //取消成功
            if (cancelSuccess) {
                //重试退票
                obj12306 = refund12306(ticket, trainOrder, req, oldUser, random, 0, 0);
                //重试结果
                refundSuccess = obj12306.getBooleanValue("success");
            }
        }
        //有退款信息
        if (!refundSuccess && obj12306.containsKey("refundInfo")
                && ("车票[" + ticket_no + "]已退票").equals(obj12306.getString("msg"))) {
            try {
                //退款信息
                JSONObject refundInfo = obj12306.getJSONObject("refundInfo");
                //退款成功
                if (refundInfo.getBooleanValue("success")) {
                    //退款金额
                    float return_price = refundInfo.getFloatValue("refundPrice");
                    //车票票价
                    float ticket_price = refundType == 0 ? ticket.getPrice() : ticket.getTcPrice();
                    //手续费
                    float return_cost = ElongHotelInterfaceUtil.floatSubtract(ticket_price, return_price);
                    //价格正确
                    if (priceIsRight(return_price) && priceIsRight(ticket_price) && priceIsRight(return_cost)
                            && dataIsTrue(trainOrder, oldUser, passenger.getName(), ticket_no, ticket_price)) {
                        //数据设置
                        obj12306.put("return_cost", return_cost);//退票手续费
                        obj12306.put("ticket_price", ticket_price);//车票票款
                        obj12306.put("return_price", return_price);//应退票款
                        //认为成功
                        refundSuccess = true;
                        //记录日志
                        WriteLog.write(logName + "_特殊处理", random + ">>>>>车票[ " + orderId + " / " + ticket.getId()
                                + " ]退款信息>>" + obj12306);
                    }
                }
            }
            catch (Exception e) {

            }
        }
        //直接退票成功
        if (refundSuccess) {
            //处理
            refundSuccess(orderId, obj12306, ticket, passenger, ticket_no, refundType, random);
            //RETURN
            return "true";
        }
        //JSON字符串
        String retdata = obj12306.toJSONString();
        //手续费
        if (retdata.contains("不直接退票，获取退票手续费成功")) {
            return retdata;
        }
        //不可退票或出错
        else {
            refundQuestion(ticket.getId(), Trainticket.REFUNDQUESTION);
            //第三方账号
            if (trainOrder.getOrdertype() == 3 || trainOrder.getOrdertype() == 4) {
                String msg = obj12306.getString("msg");
                msg = msg == null ? "" : msg;//防止空指针
                //用户未登录
                if ("用户未登录".equals(msg) || msg.contains("该用户已在其他地点登录，本次登录已失效")) {
                    refuseRefund(trainOrder, ticket, 51, "", msg, refundType);
                }
                //发车时间太近
                if (msg.startsWith("车票[" + ticket_no + "]不可退，距发车时间") && msg.endsWith("太近")) {
                    refuseRefund(trainOrder, ticket, 51, "", msg, refundType);
                }
            }
            //Code Is Null
            if (ElongHotelInterfaceUtil.StringIsNull(obj12306.getString("code"))) {
                return "error";
            }
            else {
                //乘客信息
                String passportseno = passenger.getIdnumber();//证件号
                String passengername = passenger.getName();//乘客姓名
                //票类型
                String ticketType = getTicketType(ticket);//票类型 1:成人票，2:儿童票，3:学生票，4:残军票
                //创建者
                String createUser = refundType == 1 ? "自动改签退" : "系统接口";
                //日志内容
                String msg = obj12306.getString("msg");
                String logContent = (refundType > 0 ? "[退票 - " + changeId + "]" : "") + "乘客[" + passengername + "]["
                        + passportseno + "][" + ticketType + "]执行退票，票号：" + ticket_no + "，失败"
                        + (ElongHotelInterfaceUtil.StringIsNull(msg) ? "" : "：" + msg);
                //车站已取票
                if (("车票[" + ticket_no + "]已出票").equals(msg)) {
                    //保存日志
                    createtrainorderrc(1, logContent, orderId, ticket.getId(), Trainticket.NONREFUNDABLE, createUser);
                    //已取票，直接拒
                    hasTicket(trainOrder, ticket, false);
                    //取详情
                    catchDetail(ticket, obj12306, cookie, sequence_no);
                }
                //车票已退票
                else if (("车票[" + ticket_no + "]已退票").equals(msg)) {
                    update12306Status(ticket.getId(), Trainticket.REFUNDED12306);
                }
                //车站已改签
                else if (refundType == 0 && ("车票[" + ticket_no + "]已改签").equals(msg)) {
                    //保存日志
                    createtrainorderrc(1, logContent, orderId, ticket.getId(), Trainticket.NONREFUNDABLE, createUser);
                    //已改签，直接拒
                    hasTicket(trainOrder, ticket, true);
                }
                //12306提示错误
                else if (!ElongHotelInterfaceUtil.StringIsNull(msg) && obj12306.getBooleanValue("isLimitTran")) {
                    //旅游旺季
                    if (msg.contains("凡通过互联网或手机购买的本次列车车票，如需办理退票、改签和变更到站等变更业务，请持乘车人身份证件原件到就近车站办理，代办时还需持代办人的身份证件原件")) {
                        refuseRefund(trainOrder, ticket, 41, "", msg, refundType);
                    }
                    //账号被封
                    else {
                        disableAccount(trainOrder, ticket, msg, logContent, createUser);
                    }
                }
                //RETURN
                return "false";
            }
        }
    }

    /**
     * 匹配车票
     */
    private void catchDetail(Trainticket ticket, JSONObject obj12306, String cookie, String sequence_no) {
        /*
        try {
            //取详情
            boolean getdetail = false;
            //REP
            String repurl = obj12306.getString("repurl");
            //改签标识
            int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
            //改签
            if (changeType == 1) {
                getdetail = true;
                //getdetail = ElongHotelInterfaceUtil.StringIsNull(ticket.getNewOutTicketDetail());
            }
            else {
                getdetail = true;
                //getdetail = ElongHotelInterfaceUtil.StringIsNull(ticket.getOldOutTicketDetail());
            }
            if (!getdetail || ElongHotelInterfaceUtil.StringIsNull(repurl)) {
                return;
            }
            //订单详情
            JSONObject detailRequest = new JSONObject();
            detailRequest.put("cookie", cookie);
            detailRequest.put("sequence_no", sequence_no);
            //请求参数
            String param = "datatypeflag=" + detailflag + "&jsonStr="
                    + URLEncoder.encode(detailRequest.toString(), AccountSystem.UTF8);
            //返回结果，30秒超时
            String detailInfo = RequestUtil.post(repurl, param, AccountSystem.UTF8, AccountSystem.NullMap, 20 * 1000);
            //解析
            JSONObject repData = ElongHotelInterfaceUtil.StringIsNull(detailInfo) ? new JSONObject() : JSONObject
                    .parseObject(detailInfo);
            //失败
            if (!repData.getBooleanValue("success")) {
                return;
            }
            //匹配个数
            int matchcount = 0;
            JSONObject matchobj = new JSONObject();
            //12306订单详情
            JSONArray OrderDetail = repData.getJSONArray("OrderDetail");
            //循环OrderDetail
            for (int i = 0; i < OrderDetail.size(); i++) {
                JSONObject detail = OrderDetail.getJSONObject(i);
                //车票信息
                String status = detail.getString("status");
                String coach_no = detail.getString("coach_no");
                String seat_name = detail.getString("seat_name");
                String train_date = detail.getString("train_date");
                String ticket_type = detail.getString("ticket_type");
                String passenger_name = detail.getString("passenger_name");
                String board_train_code = detail.getString("board_train_code");
                //非已出票
                if (!"已出票".equals(status)) {
                    continue;
                }
                //车票类型
                if (!isEqual(ticket_type, String.valueOf(ticket.getTickettype()))) {
                    continue;
                }
                //乘客姓名
                if (!passenger_name.equals(ticket.getTrainpassenger().getName())) {
                    continue;
                }
                //同天、同车次、同车箱、同座位
                if (isEqual(train_date, ticket.getDeparttime().split(" ")[0].replace("-", ""))
                        && isEqual(board_train_code, ticket.getTrainno()) && isEqual(coach_no, ticket.getCoach())
                        && isEqual(seat_name, ticket.getSeatno())) {
                    matchcount++;
                    matchobj = detail;
                }
                //已线上改签>>同天、同车次、同车箱、同座位
                if (changeType == 1 && isEqual(train_date, ticket.getTtcdeparttime().split(" ")[0].replace("-", ""))
                        && isEqual(board_train_code, ticket.getTctrainno()) && isEqual(coach_no, ticket.getTccoach())
                        && isEqual(seat_name, ticket.getTcseatno())) {
                    matchcount++;
                    matchobj = detail;
                }
            }
            //唯一
            if (matchcount == 1) {
                String operate_time = matchobj.getString("operate_time");//时间
                String office_name = matchobj.getString("office_name");//旬阳北自取
                //2015-08-04 09:18 制票成功(旬阳北自取)
                String outticketdetail = operate_time + " 制票成功(" + office_name + ")";
                //更新数据
                String updateSql = "";
                //线上改签
                if (changeType == 1) {
                    updateSql = "update T_TRAINTICKET set C_NEWOUTTICKETDETAIL = '" + outticketdetail + "' where ID = "
                            + ticket.getId();
                }
                else {
                    updateSql = "update T_TRAINTICKET set C_OLDOUTTICKETDETAIL = '" + outticketdetail + "' where ID = "
                            + ticket.getId();
                }
                //更新车票
                Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
            }
        }
        catch (Exception e) {
        }
        */
    }

    /**
     * 判断字符串相同
     */
    @SuppressWarnings("unused")
    private boolean isEqual(String value12306, String valuelocal) {
        return value12306.equals(valuelocal);
    }

    /**
     * 取消改签占座
     * @return 1:取消成功；2:取消重试
     */
    private int cancelChange(Trainorder order, Trainticket ticket, Trainorderchange change, Customeruser user,
            String sequence_no, boolean isLast, int random) {
        //取消结果
        int cancelResult = 0;
        //取消改签
        try {
            //改签占座失败
            if (change != null && change.getId() == ticket.getChangeid()
                    && (change.getTcstatus() == 3 || change.getTcstatus() == 10)) {
                //REP地址
                RepServerBean rep = RepServerUtil.getRepServer(user, false);
                //请求参数
                String param = "datatypeflag=10&cookie=" + user.getCardnunber() + "&extnumber=" + sequence_no
                        + "&trainorderid=" + order.getId() + JoinCommonAccountInfo(user, rep);
                //请求REP
                String result = RequestUtil.post(rep.getUrl(), param, "UTF-8", new HashMap<String, String>(),
                        cancelTimeOut);
                //用户未登录
                if (result.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
                    //切换REP
                    rep = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
                    //类型正确
                    if (rep.getType() == 1) {
                        //重拼参数
                        param = "datatypeflag=10&cookie=" + user.getCardnunber() + "&extnumber=" + sequence_no
                                + "&trainorderid=" + order.getId() + JoinCommonAccountInfo(user, rep);
                        //重新请求
                        result = RequestUtil.post(rep.getUrl(), param, "UTF-8", new HashMap<String, String>(),
                                cancelTimeOut);
                    }
                }
                //取消成功
                if (result.contains("取消订单成功")) {
                    cancelResult = 1;//成功
                }
                //取消失败
                else if ("取消失败，请去12306官网进行取消".equals(result)) {
                    cancelResult = 2;//重试
                }
                //空、没查到订单
                else if (ElongHotelInterfaceUtil.StringIsNull(result) || "无未支付订单".equals(result)) {
                    cancelResult = isLast ? 1 : 2;//最后一次认为成功，其他重试
                }
                //记录操作日志
                WriteLog.write(logName, random + ">>>>>车票[ " + order.getId() + " / " + ticket.getId()
                        + " ]取消改签，REP服务器地址>>>>>" + rep.getUrl() + ">>>>>REP返回>>>>>" + result);
            }
        }
        //异常，重试
        catch (Exception e) {
            if (cancelResult == 0) {
                cancelResult = 2;
            }
        }
        return cancelResult;
    }

    /**
     * 已取票，直接拒
     * @param isChange 已改签
     */
    private void hasTicket(Trainorder order, Trainticket ticket, boolean isChange) {
        ticket.setRefundType(0);
        ticket.setIsQuestionTicket(Trainticket.REFUNDQUESTION);
        ticket.setState12306(isChange ? Trainticket.CHANGEDPAYED : Trainticket.HASTICKET);
        //更新成功
        if (Server.getInstance().getTrainService().updateTrainticket(ticket)) {
            try {
                //接口类型
                int interfacetype = order.getInterfacetype() != null && order.getInterfacetype().intValue() > 0 ? order
                        .getInterfacetype().intValue() : getOrderAttribution(order);
                //订单归属类型错误
                if (interfacetype <= 0) {
                    return;
                }
                String trainorderNonRefundable = getSysconfigString("trainorderNonRefundable");
                //地址为空
                if (ElongHotelInterfaceUtil.StringIsNull(trainorderNonRefundable)) {
                    return;
                }
                int reason = isChange ? 31 : 33;//31：已改签；33：已出票,只能在窗口办理退票
                //回调参数
                String url = trainorderNonRefundable + "?trainorderid=" + order.getId() + "&ticketid=" + ticket.getId()
                        + "&interfacetype=" + interfacetype + "&reason=" + reason + "&responseurl="
                        + trainorderNonRefundable;
                //请求接口
                new RefundTicketMethod().refusedRefund(url);
            }
            catch (Exception e) {
                System.out.println(ElongHotelInterfaceUtil.errormsg(e));
            }
        }
    }

    /**
     * 账号被封，直接拒
     */
    private void disableAccount(Trainorder order, Trainticket ticket, String msg, String logContent, String createUser) {
        //手机核验
        boolean mobileCheck = "为了保障您的个人信息安全，请您购票前在“我的12306”的“账号安全”中选择“手机核验”，核验后再请办理购票业务，谢谢您的支持。".equals(msg);
        //再次核验
        if (!mobileCheck) {
            mobileCheck = msg.contains("使用本人真实手机号码核验通过后即可恢复网上正常购票、退票和改签")
                    && msg.contains("您在12306网站注册时填写信息有误，或已被他人冒用，为了保障您的个人信息安全，网站对您的账号采取了保护性措施，该账号目前在互联网上不能办理购票、退票和改签业务，您可在“我的12306”的“账号安全”中选择“手机核验”");
        }
        //直接拒退
        mobileCheck = mobileCheck && "1".equals(PropertyUtil.getValue("refuseMobileCheckRefund"));

        //真实资料
        boolean realInfo = msg.contains("根据本网站的服务条款，您需要提供真实、准确的本人资料") || msg.contains("根据本网站服务条款，您需要提供真实、准确的本人资料");
        //身份核验
        boolean identityCheck = msg.contains("请您尽快到就近的办理客运售票业务的铁路车站完成身份核验") || msg.contains("请您到就近办理客运售票业务的铁路车站完成身份核验");
        //账号被封
        boolean disableAccount = realInfo && identityCheck;

        //信息有误
        boolean errorInfo = msg.contains("您填写的身份信息有误，未能通过国家身份信息管理权威部门核验，请检查您的姓名和身份证件号码填写是否正确。如有疑问，可致电12306客服咨询")
                || msg.contains("您的用户信息被他人冒用，请重新在网上注册新的账户，为了确保您的购票安全，您还需尽快到就近的办理客运售票业务的铁路车站完成身份核验，谢谢您对12306网站的支持");

        //理由不对
        if (!mobileCheck && !disableAccount && !errorInfo) {
            return;
        }
        try {
            //账号被封
            update12306Status(ticket.getId(), mobileCheck ? 19 : 18);
            //保存日志
            createtrainorderrc(1, logContent, order.getId(), ticket.getId(), Trainticket.NONREFUNDABLE, createUser);
            //接口类型
            int interfacetype = order.getInterfacetype() != null && order.getInterfacetype().intValue() > 0 ? order
                    .getInterfacetype().intValue() : getOrderAttribution(order);
            //暂只针对同程、淘宝、美团
            if (interfacetype != 3 && interfacetype != 6 && interfacetype != 7) {
                return;
            }
            String trainorderNonRefundable = getSysconfigString("trainorderNonRefundable");
            //地址为空
            if (ElongHotelInterfaceUtil.StringIsNull(trainorderNonRefundable)) {
                return;
            }
            int reason = 40;//订单所在账号被封
            //回调参数
            String url = trainorderNonRefundable + "?trainorderid=" + order.getId() + "&ticketid=" + ticket.getId()
                    + "&interfacetype=" + interfacetype + "&reason=" + reason + "&responseurl="
                    + trainorderNonRefundable;
            //请求接口
            new RefundTicketMethod().refusedRefund(url);
            //记录日志
            WriteLog.write("disableAccountOrder", order.getId() + ":" + ticket.getId() + ":" + msg);
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.errormsg(e));
        }
    }

    /**
     * 拒绝退票
     * @param errMsg 错误信息
     * @param reason 原因
     *          |-->41：旅游旺季
     *          |-->51：第三方账号问题
     */
    private void refuseRefund(Trainorder order, Trainticket ticket, int reason, String errMsg, String logContent,
            int refundType) {
        try {
            //旅游旺季
            if (reason == 41) {
                update12306Status(ticket.getId(), 20);
            }
            //客人账号
            else if (reason == 51) {
                update12306Status(ticket.getId(), 21);
            }
            //保存日志
            if (!ElongHotelInterfaceUtil.StringIsNull(logContent)) {
                //乘客
                Trainpassenger passenger = ticket.getTrainpassenger();
                //创建者
                String createUser = refundType == 1 ? "自动改签退" : "系统接口";
                //线上改签
                boolean onlineChange = ticket.getChangeType() == 1 || ticket.getChangeType() == 2;
                //车票票号
                String ticket_no = onlineChange ? ticket.getTcticketno() : ticket.getTicketno();
                //重拼日志
                logContent = (onlineChange ? "[退票 - " + ticket.getChangeid() + "]" : "") + "乘客[" + passenger.getName()
                        + "][" + passenger.getIdnumber() + "][" + getTicketType(ticket) + "]执行退票，票号：" + ticket_no
                        + "，失败：" + logContent;
                createtrainorderrc(1, logContent, order.getId(), ticket.getId(), Trainticket.NONREFUNDABLE, createUser);
            }
            //接口类型
            int interfacetype = order.getInterfacetype() != null && order.getInterfacetype().intValue() > 0 ? order
                    .getInterfacetype().intValue() : getOrderAttribution(order);
            //订单归属类型错误
            if (interfacetype <= 0) {
                return;
            }
            String trainorderNonRefundable = getSysconfigString("trainorderNonRefundable");
            //地址为空
            if (ElongHotelInterfaceUtil.StringIsNull(trainorderNonRefundable)) {
                return;
            }
            //回调参数
            String url = trainorderNonRefundable + "?trainorderid=" + order.getId() + "&ticketid=" + ticket.getId()
                    + "&interfacetype=" + interfacetype + "&reason=" + reason + "&responseurl="
                    + trainorderNonRefundable;
            //错误非空
            if (!ElongHotelInterfaceUtil.StringIsNull(errMsg)) {
                url += "&errMsg=" + URLEncoder.encode(errMsg, "UTF-8");
            }
            //请求接口
            new RefundTicketMethod().refusedRefund(url);
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.errormsg(e));
        }
    }

    /**
     * 12306退票
     */
    private JSONObject refund12306(Trainticket ticket, Trainorder trainOrder, JSONObject req, Customeruser oldUser,
            int random, int loginError, int elseError) {
        long orderId = trainOrder.getId();
        //REP
        String url = "";
        String retdata = "";
        JSONObject obj12306 = new JSONObject();
        try {
            //REP
            RepServerBean rep = RepServerUtil.getRepServer(oldUser, false);
            //地址
            url = rep.getUrl();
            //REP地址为空
            if (ElongHotelInterfaceUtil.StringIsNull(url)) {
                throw new Exception("REP地址为空");
            }
            //请求参数URLEncoder
            String jsonStr = URLEncoder.encode(req.toJSONString(), "UTF-8");
            //请求参数
            String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr + JoinCommonAccountInfo(oldUser, rep);
            //请求REP
            retdata = RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), timeout);
            //用户未登录
            if (retdata.contains("用户未登录") && RepServerUtil.changeRepServer(oldUser)) {
                //切换REP
                rep = RepServerUtil.getTaoBaoTuoGuanRepServer(oldUser, false);
                //类型正确
                if (rep.getType() == 1) {
                    //地址
                    url = rep.getUrl();
                    //重拼参数
                    param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr
                            + JoinCommonAccountInfo(oldUser, rep);
                    //重新请求
                    retdata = RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), timeout);
                }
            }
            //返回数据
            obj12306 = JSONObject.parseObject(retdata);
        }
        catch (Exception e) {
        }
        finally {
            if (retdata == null) {
                retdata = "";
            }
            if (obj12306 == null) {
                obj12306 = new JSONObject();
            }
            WriteLog.write(logName, random + ">>>>>>车票[ " + loginError + " / " + elseError + " / " + orderId + " / "
                    + ticket.getId() + " ]改签退>>>>>退票，REP服务器地址>>>>>" + url + ">>>>>REP返回>>>>>" + retdata);
        }
        //查询第[1]页已完成订单失败
        boolean queryOrderError = false;
        if (ElongHotelInterfaceUtil.StringIsNull(retdata)) {
            queryOrderError = true;
        }
        else if (retdata.contains("未查询到订单[" + trainOrder.getExtnumber() + "]")) {
            queryOrderError = true;
        }
        else if (retdata.contains("查询第") && retdata.contains("页已完成订单失败")) {
            queryOrderError = true;
        }
        boolean noLogin = Account12306Util.accountNoLogin(retdata, oldUser);
        boolean elseFail = queryOrderError || retdata.contains("网络繁忙") || retdata.contains("退票异常，请确认")
                || retdata.contains("退票失败") || retdata.contains("系统繁忙，请稍后重试");
        //用户未登录
        if (noLogin) {
            //重置
            elseError = 0;
            //失败+1
            loginError = loginError + 1;
            //退票重试
            if (loginError < loginErrorCount) {
                //重新登陆
                oldUser = orderUser(trainOrder, oldUser, random);
                //更新Cookie
                req.put("cookie", oldUser.getCardnunber());
                //退票重试
                return refund12306(ticket, trainOrder, req, oldUser, random, loginError, elseError);
            }
        }
        //其他退票失败
        else if (elseFail) {
            //失败+1
            elseError = elseError + 1;
            //退票重试
            if (elseError < totalErrorCount) {
                return refund12306(ticket, trainOrder, req, oldUser, random, loginError, elseError);
            }
        }
        //URL
        obj12306.put("repurl", url);
        //返回
        return obj12306;
    }

    /**
     * 退票成功
     * @param refundType 退票类型，0：直接退；1：改签退；2：改签后退票
     */
    private void refundSuccess(long orderId, JSONObject obj12306, Trainticket ticket, Trainpassenger passenger,
            String ticket_no, int refundType, int random) {
        try {
            //车票票款
            float ticket_price = obj12306.getFloatValue("ticket_price");
            //应退票款
            float return_price = obj12306.getFloatValue("return_price");
            //退票费
            float return_cost = obj12306.getFloatValue("return_cost");
            //改签退票费
            if (refundType == 1) {
                ticket.setTcProcedure(return_cost);
            }
            else {
                ticket.setProcedure(return_cost);
                //改签后退票
                if (refundType == 2) {
                    ticket.setTcnewprice(ticket.getTcPrice());
                }
            }
            ticket.setStatus(Trainticket.WAITREFUND);
            ticket.setState12306(Trainticket.REFUNDED12306);
            ticket.setRefundType(OnlineRefundUtil.istc() ? 2 : 1);//同程直接审核中
            //ticket.setRefundsuccesstime(ElongHotelInterfaceUtil.getCurrentTime());
            Server.getInstance().getTrainService().updateTrainticket(ticket);
            //乘客信息
            String passportseno = passenger.getIdnumber();//证件号
            String passengername = passenger.getName();//乘客姓名
            //票类型
            String ticketType = getTicketType(ticket);//票类型 1:成人票，2:儿童票，3:学生票，4:残军票
            //创建者
            String createUser = refundType == 1 ? "自动改签退" : "系统接口";
            //日志内容
            String logContent = "";
            if (refundType == 1) {
                logContent = "[退票 - " + ticket.getChangeid() + "]乘客[" + passengername + "][" + passportseno + "]["
                        + ticketType + "]执行退票，票号：" + ticket_no
                        + "，12306<span style='color:red;'>退票成功</span>，等待退款，新退票手续费：" + return_cost;
            }
            else {
                logContent = (refundType == 2 ? "[退票 - " + ticket.getChangeid() + "]" : "") + "乘客[" + passengername
                        + "][" + passportseno + "][" + ticketType + "]执行退票，票号：" + ticket_no
                        + "，12306<span style='color:red;'>退票成功</span>，等待退款，车票票款：" + ticket_price + "，应退票款："
                        + return_price + "，退票费：" + return_cost;
            }
            //保存日志
            createtrainorderrc(1, logContent, orderId, ticket.getId(), Trainticket.WAITREFUND, createUser);
        }
        catch (Exception e) {
            WriteLog.write(logName, random + ">>>>>>车票[ " + orderId + " / " + ticket.getId()
                    + " ]改签退>>>>>更新车票状态为已退票-等待退款异常: " + e.getMessage());
        }
    }

    /**
     * 票类型
     */
    private String getTicketType(Trainticket ticket) {
        //票类型
        int tickettype = ticket.getTickettype();
        //判断类型
        if (tickettype == 1) {
            return "成人票";
        }
        if (tickettype == 2) {
            return "儿童票";
        }
        if (tickettype == 3) {
            return "学生票";
        }
        if (tickettype == 4) {
            return "残军票";
        }
        return String.valueOf(tickettype);
    }

    /**
     * 12306退款等价格正确
     */
    private boolean priceIsRight(float price) {
        return price > 0 && (String.valueOf(price).endsWith(".0") || String.valueOf(price).endsWith(".5"));
    }

    /**
     * 未发车车票，校验数据
     * @param passanger_name 乘客姓名，用于关键字查询
     */
    private boolean dataIsTrue(Trainorder order, Customeruser user, String passanger_name, String ticket_no,
            float ticket_price) throws Exception {
        //结果
        boolean isTrue = false;
        //订单列表
        JSONObject listRequest = new JSONObject();
        //请求参数
        listRequest.put("cookie", user.getCardnunber());
        listRequest.put("sequence_no", order.getExtnumber());
        listRequest.put("query_where", "G");//G：未发车；H：已发车
        listRequest.put("queryType", "1");//1：按订票日期查询；2：按乘车日期查询
        listRequest.put("passanger_name", passanger_name);//乘客姓名，用于关键字查询
        listRequest.put("queryEndDate", ElongHotelInterfaceUtil.getCurrentDate());//结束时间，取当天
        listRequest.put("queryStartDate", order.getCreatetime().toString().split(" ")[0]);//开始时间
        listRequest.put("come_from_flag", "my_order");//查询类型>>my_order：全部；my_resign：可改签； my_refund：可退票
        //REP
        RepServerBean rep = RepServerUtil.getRepServer(user, false);
        //请求参数
        String param = "datatypeflag=108&jsonStr=" + URLEncoder.encode(listRequest.toString(), "UTF-8");
        //请求REP
        String html = RequestUtil.post(rep.getUrl(), param, "UTF-8", new HashMap<String, String>(), cancelTimeOut);
        //解析数据
        JSONObject obj = JSONObject.parseObject(html);
        //获取到订单
        if (obj != null && obj.getBooleanValue("success")) {
            //INFO
            String info = obj.getString("order");
            //JSON
            JSONObject OrderDTOData = JSONObject.parseObject(info);
            //车票数组
            JSONArray tickets = OrderDTOData.getJSONArray("tickets");
            //遍历车票
            for (int i = 0; i < tickets.size(); i++) {
                //车票信息
                JSONObject ticket = tickets.getJSONObject(i);
                //票号一致
                if (ticket_no.equals(ticket.getString("ticket_no"))) {
                    //状态
                    String ticket_status_name = ticket.getString("ticket_status_name");
                    //校验
                    isTrue = ticket_status_name.contains("已退票") && ticket_status_name.contains("业务流水号")
                            && ticket_price == Float.parseFloat(ticket.getString("str_ticket_price_page"));
                    //中断
                    break;
                }
            }
        }
        //直接返回
        return isTrue;
    }

}