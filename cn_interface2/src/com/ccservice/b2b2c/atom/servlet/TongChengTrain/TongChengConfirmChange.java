package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.MQ.MQMethod;
import com.ccservice.b2b2c.atom.servlet.MQ.TrainpayMqMSGUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.TuNiuChangeCodeMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.weixin.util.RequestUtil;

/**
 * 确认改签
 * @author WH
 */

public class TongChengConfirmChange extends TrainSelectLoginWay {

    //登录失败，重试次数
    private static final int loginErrorCount = 3;

    //订单状态不正确
    private static final String code112 = "112";

    private static final String code999 = "999";

    private static final String datatypeflag = "102";//确认改签

    public String operate(JSONObject json, int random) {
        return operate(json, random, "");
    }

    public String operate(JSONObject json, int random, String partnerid) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //同程订单号
        String changeid = json.containsKey("changeid") ? json.getString("changeid") : "";
        String orderid = json.containsKey("orderid") ? json.getString("orderid") : "";
        String transactionid = json.containsKey("transactionid") ? json.getString("transactionid") : "";
        String accountId = json.containsKey("accountId") ? json.getString("accountId") : "";
        if (ElongHotelInterfaceUtil.StringIsNull(orderid) || ElongHotelInterfaceUtil.StringIsNull(transactionid)) {
            retobj.put("code", "107");
            retobj.put("msg", "业务参数缺失");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        //是否为异步改签>>Y：异步改签；N：同步改签
        String isasync = json.containsKey("isasync") ? json.getString("isasync") : "N";
        //请求特征值
        String reqtoken = json.containsKey("reqtoken") ? json.getString("reqtoken") : "";
        //改签占座异步回调地址
        String callbackurl = json.containsKey("callbackurl") ? json.getString("callbackurl") : "";

        //改签退标识
        int changeType = json.containsKey("changeType") ? json.getIntValue("changeType") : 0;
        //异步改签
        if ("Y".equals(isasync)) {
            if (ElongHotelInterfaceUtil.StringIsNull(callbackurl) || ElongHotelInterfaceUtil.StringIsNull(reqtoken)) {
                retobj.put("code", "107");
                retobj.put("msg", "业务参数缺失");
                retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
                return retobj.toString();
            }
        }
        //不再支持同步
        else {
            retobj.put("code", "108");
            retobj.put("msg", "错误的业务参数，请走异步");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toString();
        }
        //查询订单
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        //订单不存在
        if (orders == null || orders.size() != 1) {
            retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        Trainorder order = orders.get(0);
        long orderId = order.getId();
        int status = order.getOrderstatus();
        String extnumber = order.getExtnumber();
        //保存
        saveThirdAccountInfo(order.getId(), json);
        //占座成功，12306订单号不为空
        if (status != Trainorder.ISSUED || ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
            retobj.put("code", "112");
            retobj.put("msg", "该订单状态下，不能确认改签");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toString();
        }
        return change(orderId, new Trainorder(), changeType, random, isasync, callbackurl, reqtoken, orderid, changeid,
                transactionid, order.getAgentid(), partnerid, accountId);
    }

    /**
     * 确认改签
     * @param changeType 改签类型，0：改签；1：改签退
     * @param changeid taobao改签单会传changeid
     */
    @SuppressWarnings("rawtypes")
    public String change(long orderId, Trainorder order, int changeType, int random, String isasync,
            String callbackurl, String reqtoken, String qunarordernumber, String changeid, String transactionid,
            long agentId, String partnerid, String accountId) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //订单改签信息
        String changeSql = "select top 1 ID, C_TCSTATUS, C_TCCREATETIME, C_TCDEPARTTIME, C_CONFIRMREQTOKEN, "
                + "ISNULL(C_ISQUESTIONCHANGE, 0) C_ISQUESTIONCHANGE, ISNULL(C_TCISCHANGEREFUND, 0) C_TCISCHANGEREFUND, C_REQUESTREQTOKEN "
                + "from T_TRAINORDERCHANGE with(nolock) where C_ORDERID = " + orderId + " order by ID desc";
        //如果传了changeid  可以直接用ID查找
        if (!ElongHotelInterfaceUtil.StringIsNull(changeid)) {
            changeSql = "select ID, C_TCSTATUS, C_TCCREATETIME, C_TCDEPARTTIME, C_CONFIRMREQTOKEN, "
                    + "ISNULL(C_ISQUESTIONCHANGE, 0) C_ISQUESTIONCHANGE, ISNULL(C_TCISCHANGEREFUND, 0) C_TCISCHANGEREFUND, C_REQUESTREQTOKEN "
                    + "from T_TRAINORDERCHANGE with(nolock) where ID = " + changeid;
        }
        List changeList = Server.getInstance().getSystemService().findMapResultBySql(changeSql, null);
        if (changeList == null || changeList.size() == 0) {
            retobj.put("code", code112);
            retobj.put("msg", "未找到可以确认的改签车票");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toString();
        }
        //最后一个
        Map changeMap = (Map) changeList.get(0);
        String requestReqtoken = changeMap.get("C_REQUESTREQTOKEN").toString();
        if (partnerid.contains("tuniu") && !reqtoken.equals(requestReqtoken)) {
        	retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toJSONString();
		}
        //改签ID
        long changeId = Long.parseLong(changeMap.get("ID").toString());
        //改签状态
        int tcStatus = Integer.parseInt(changeMap.get("C_TCSTATUS").toString());
        //改签退
        int changeRefund = Integer.parseInt(changeMap.get("C_TCISCHANGEREFUND").toString());
        //重置采购问题
        boolean resetCaiGouQuestion = false;
        //防止占座采购问题
        if (tcStatus == Trainorderchange.THOUGHCHANGE) {
            resetCaiGouQuestion = true;//认为回调成功
        }
        //请求特征，相同时表示同一次请求
        String confirmReqtoken = changeMap.get("C_CONFIRMREQTOKEN") == null ? "" : changeMap.get("C_CONFIRMREQTOKEN")
                .toString();
        //同一次请求
        if ("Y".equals(isasync) && reqtoken.equals(confirmReqtoken)) {
            retobj.put("code", "100");
            retobj.put("success", true);
            retobj.put("msg", "确认请求已接受");//固定值，勿调整
            retobj.put("reqtoken", reqtoken);
            retobj.put("orderid", qunarordernumber);
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toString();
        }
        //改签退
        if (changeType == 1 && changeRefund != 1) {
            retobj.put("code", code999);
            retobj.put("msg", "非法的业务参数");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toString();
        }
        //状态判断
        if (tcStatus == Trainorderchange.FINISHCHANGE) {
            retobj.put("code", code112);
            retobj.put("msg", "改签已确认，不能再次确认");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toString();
        }
        else if (tcStatus == Trainorderchange.FAILCHANGE) {
            retobj.put("code", code112);
            retobj.put("msg", "改签占座失败，不能确认");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toString();
        }
        else if (tcStatus == Trainorderchange.CANTCHANGE) {
            retobj.put("code", code112);
            retobj.put("msg", "改签已取消，不能继续确认");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toString();
        }
        else if (tcStatus == Trainorderchange.CHANGEWAITPAY || tcStatus == Trainorderchange.CHANGEPAYING) {
            retobj.put("code", code112);
            retobj.put("msg", "正在确认改签，不能再次确认");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        else if (tcStatus == Trainorderchange.THOUGHCHANGE) {
            //判断超时
            boolean timeout = false;
            try {
                SimpleDateFormat shiFenFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat totalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //创建时间
                String tcTime = changeMap.get("C_TCCREATETIME").toString();
                long createTime = totalFormat.parse(tcTime).getTime();
                //发车时间
                String tcDepartTime = changeMap.get("C_TCDEPARTTIME").toString();
                long departTime = shiFenFormat.parse(tcDepartTime).getTime();
                //当前时间
                long currentTime = System.currentTimeMillis();
                //时间差
                long subTime = (currentTime - createTime) / 1000;//秒
                long createToDepart = departTime - createTime;//毫秒
                //12306在晚上11点半后不可支付了
                //开车前2小时内所购车票请于10分钟内完成支付
                //判断是否超时，22:44:59前成功申请的单子，供应商应该保留  30分钟的付款时间；22:45:00-23:00:00下的单子,供应商应该保留支付时限到23:30
                if (createToDepart < 120 * 60 * 1000 && subTime > 9 * 60) {
                    timeout = true;
                }
                else if (subTime > 29 * 60) {//25分钟付款时间
                    timeout = true;
                }
            }
            catch (Exception e) {
            }
            if (timeout) {
                retobj.put("code", "1003");
                retobj.put("msg", "确认改签的请求时间已超过规定的时间");
                retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
                return retobj.toString();
            }
        }
        else {
            retobj.put("code", code112);
            retobj.put("msg", "该状态下，不能确认改签");
            retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        //异步改签
        if ("Y".equals(isasync)) {
            //配置地址
            String systemSetUrl = changeType == 1 ? "" : getTrainCallBackUrl(order.getAgentid(), 2);
            //非空，取配置
            if (!ElongHotelInterfaceUtil.StringIsNull(systemSetUrl)) {
                callbackurl = systemSetUrl;
            }
            return AsyncChange(changeId, orderId, callbackurl, reqtoken, qunarordernumber, resetCaiGouQuestion,
                    changeType, transactionid, agentId, partnerid);
        }
        //同步改签
        else {
            //改签信息
            Trainorderchange trainOrderChange = Server.getInstance().getTrainService()
                    .findTrainOrderChangeById(changeId);
            //查询订单
            if (order == null || order.getId() <= 0) {
                order = Server.getInstance().getTrainService().findTrainorder(orderId);
            }
            //确认操作
            return changeOperate(trainOrderChange, order, false, random, accountId).toJSONString();
        }
    }

    /**
     * 异步确认
     */
    @SuppressWarnings("rawtypes")
    public String AsyncChange(long changeId, long orderId, String callbackurl, String reqtoken,
            String qunarordernumber, boolean resetCaiGouQuestion, int changType, String transactionid, long agentId,
            String partnerid) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        retobj.put("reqtoken", reqtoken);
        //更新改签
        int C_TCSTATUS = Trainorderchange.CHANGEWAITPAY;
        int C_STATUS12306 = Trainorderchange.ORDEREDWAITPAY;
        //更新SQL
        String updateSql = "";
        //重置问题
        if (resetCaiGouQuestion) {
            updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + C_TCSTATUS + ", C_STATUS12306 = "
                    + C_STATUS12306 + ", C_CONFIRMISASYNC = 1, C_CONFIRMCALLBACKURL = '" + callbackurl
                    + "', C_CONFIRMREQTOKEN = '" + reqtoken + "', C_ISQUESTIONCHANGE = 0 where ID = " + changeId
                    + " and C_TCSTATUS = " + Trainorderchange.THOUGHCHANGE;
        }
        else {
            updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + C_TCSTATUS + ", C_STATUS12306 = "
                    + C_STATUS12306 + ", C_CONFIRMISASYNC = 1, C_CONFIRMCALLBACKURL = '" + callbackurl
                    + "', C_CONFIRMREQTOKEN = '" + reqtoken + "' where ID = " + changeId + " and C_TCSTATUS = "
                    + Trainorderchange.THOUGHCHANGE;
        }
        //更新失败
        if (Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) != 1) {
            retobj.put("code", code999);
            retobj.put("msg", "确认改签失败");
        }
        else {
            retobj.put("success", true);
            retobj.put("code", "100");
            retobj.put("msg", "确认请求已接受");//固定值，勿调整
            retobj.put("orderid", qunarordernumber);
            retobj.put("transactionid", transactionid);
            //乘客ID
            try {
                //高铁
                //正式:86；测试:1209
                if (agentId == 86) {
                    //SQL
                    String querySql = "select ISNULL(C_TCPASSENGERID, '') C_TCPASSENGERID "
                            + "from T_TRAINTICKET with(nolock) where C_CHANGEID = " + changeId;
                    //查询
                    List list = Server.getInstance().getSystemService().findMapResultBySql(querySql, null);
                    //非空
                    if (list != null && list.size() > 0) {
                        //结果
                        JSONArray changeTickets = new JSONArray();
                        //循环
                        for (int i = 0; i < list.size(); i++) {
                            Map map = (Map) list.get(i);
                            //乘客ID
                            String tcPassengerId = map.get("C_TCPASSENGERID").toString();
                            //乘客ID非空
                            if (!ElongHotelInterfaceUtil.StringIsNull(tcPassengerId)) {
                                JSONObject changeTicket = new JSONObject();
                                changeTicket.put("passengerid", tcPassengerId);
                                changeTickets.add(changeTicket);
                            }
                        }
                        //非空
                        if (changeTickets.size() > 0) {
                            retobj.put("changeTickets", changeTickets);
                        }
                    }
                }
            }
            catch (Exception e) {
            }
            //操作日志
            createtrainorderrc(1, "[确认 - " + changeId + "]接口确认改签，等待支付12306", orderId, 0l, Trainticket.APPLYROCESSING,
                    changType == 1 ? "自动改签退" : "系统接口");
            //队列确认
            activeMQChangeOrder(changeId, 2);
        }
        retobj = TuNiuChangeCodeMethod.changeConfirmCode(retobj, partnerid);
        return retobj.toJSONString();
    }

    /**
     * 异步确认操作
     */
    public JSONObject AsyncChangeMQ(Trainorderchange trainOrderChange, String accountId) {
        //订单ID
        long orderId = trainOrderChange.getOrderid();
        //查询订单
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderId);
        //确认操作
        return changeOperate(trainOrderChange, order, true, new Random().nextInt(10000000), accountId);
    }

    /**
     * 确认操作
     */
    public JSONObject changeOperate(Trainorderchange trainOrderChange, Trainorder order, boolean isasync, int random,
            String accountId) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //改签ID
        long changeId = trainOrderChange.getId();
        //订单ID
        long orderId = trainOrderChange.getOrderid();
        //改签退
        long changeType = trainOrderChange.getTcischangerefund();
        //异步确认
        if (isasync) {
            retobj.put("agentId", order.getAgentid());
        }
        retobj.put("orderid", order.getQunarOrdernumber());
        //获取订单对应乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //原价格
        float oldPrice = 0f;
        //改签价格
        float changePrice = 0f;
        //改签手续费
        float changeProcedure = 0f;
        //车票号
        JSONArray changeTicketNo = new JSONArray();
        List<Trainticket> changeTicketList = new ArrayList<Trainticket>();
        //改签乘客
        for (Trainpassenger passenger : passengers) {
            //车票
            List<Trainticket> traintickets = passenger.getTraintickets();
            //改签车票
            Trainticket changeTicket = null;
            for (Trainticket trainTicket : traintickets) {
                if (trainTicket.getChangeid() == changeId) {
                    changeTicket = trainTicket;
                    break;
                }
            }
            if (changeTicket != null) {
                changeTicketList.add(changeTicket);
                changeTicketNo.add(changeTicket.getTcticketno());
                changePrice = ElongHotelInterfaceUtil.floatAdd(changePrice, changeTicket.getTcPrice());
                oldPrice = ElongHotelInterfaceUtil.floatAdd(oldPrice, changeTicket.getPrice().floatValue());
                changeProcedure = ElongHotelInterfaceUtil.floatAdd(changeProcedure, changeTicket.getChangeProcedure()
                        .floatValue());
            }
        }
        if (changeTicketNo.size() == 0 || changePrice != trainOrderChange.getTcprice()
                || oldPrice != trainOrderChange.getTcoriginalprice()
                || changeProcedure != trainOrderChange.getChangeProcedure()) {
            //异步
            if (isasync) {
                changeQuestionOrder(changeId, false);
            }
            //返回
            retobj.put("code", code112);
            retobj.put("msg", "确认改签失败");
            return retobj;
        }
        //下单账户
        String createAccount = order.getSupplyaccount();
        if (ElongHotelInterfaceUtil.StringIsNull(createAccount) && order.getOrdertype() != 3
                && order.getOrdertype() != 4 && order.getOrdertype() != 6) {
            //异步
            if (isasync) {
                changeQuestionOrder(changeId, false);
            }
            //返回
            retobj.put("code", code999);
            retobj.put("msg", "确认改签失败");
            return retobj;
        }
        //更新改签订单成功
        boolean updateSuccess = false;
        //异步，记录日志
        if (isasync) {
            //订单标识
            String changeFlag = trainOrderChange.getChangeArrivalFlag() == 1 ? "变更到站" : "改签";
            //记录日志
            createtrainorderrc(1, "[确认 - " + changeId + "]开始支付异步" + changeFlag, orderId, 0l,
                    Trainticket.APPLYROCESSING, changeType == 1 ? "自动改签退" : "系统接口");
        }
        //同步，更新改签订单为支付中
        else {
            String updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + Trainorderchange.CHANGEPAYING
                    + " where ID = " + changeId + " and C_TCSTATUS = " + Trainorderchange.THOUGHCHANGE;
            if (Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) != 1) {
                retobj.put("code", code999);
                retobj.put("msg", "确认改签失败");
                return retobj;
            }
            else {
                updateSuccess = true;
            }
        }
        //1、下单账户
        Customeruser user = new Customeruser();
        //循环取账号
        for (int i = 0; i < loginErrorCount; i++) {
            //账号系统，以未登录释放账号
            if (user != null && user.isFromAccountSystem()) {
                freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            //获取账号
            if (order.getOrdertype() == 6 && !ElongHotelInterfaceUtil.StringIsNull(accountId)) {
                user = getCustomeruserByusernameEncryption(accountId);
            }
            user = getCustomeruserBy12306Account(order, random, i > 0);
            //不用重试、成功获取cookie
            if (user != null
                    && (user.isDontRetryLogin() || !ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber()))) {
                break;
            }
        }
        //获取账号
        if (user == null || user.isDontRetryLogin() || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //还原
            reductionChange(changeId, updateSuccess);
            //异步
            if (isasync) {
                changeQuestionOrder(changeId, false);
            }
            //账号系统，以未登录释放账号
            if (user != null && user.isFromAccountSystem()) {
                freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            retobj.put("code", code999);
            retobj.put("msg", user != null && user.isDontRetryLogin() ? user.getNationality() : "确认改签失败");
            return retobj;
        }
        //请求12306
        JSONObject obj12306 = pay12306(user, order, changeTicketNo, oldPrice, changePrice, changeProcedure, random);
        //12306结果
        String returnMsg = obj12306.getString("msg") == null ? "" : obj12306.getString("msg");
        //判断账号未登录
        if (!obj12306.getBooleanValue("success") && Account12306Util.accountNoLogin(returnMsg, user)) {
            //释放未登录
            freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
            //重新获取账号
            if(order.getOrdertype()==6&&!ElongHotelInterfaceUtil.StringIsNull(accountId)){
                user = getCustomeruserByusernameEncryption(accountId);
            }
            user = getCustomeruserBy12306Account(order, random, true);
            //获取cookie成功，重新请求12306
            if (user != null && !user.isDontRetryLogin() && !ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
                obj12306 = pay12306(user, order, changeTicketNo, oldPrice, changePrice, changeProcedure, random);
            }
        }
        //高改标识
        boolean highChange = false;
        user = user == null ? new Customeruser() : user;
        boolean FromAccountSystem = user.isFromAccountSystem();
        //失败
        if (!obj12306.getBooleanValue("success")) {
            //新票款大于原票款
            if (changePrice > oldPrice && "新票款大于原票款，验证价格成功".equals(obj12306.getString("msg"))) {
                highChange = true;
            }
            else if (!obj12306.getBooleanValue("payConfirm")) {
                //还原
                reductionChange(changeId, updateSuccess);
                //异步
                if (isasync) {
                    changeQuestionOrder(changeId, false);
                }
                //账号系统，释放账号
                if (FromAccountSystem) {
                    freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                            AccountSystem.NullDepartTime);
                }
                //返回
                retobj.put("code", code999);
                retobj.put("msg", user.isDontRetryLogin() ? user.getNationality() : "确认改签失败");
                return retobj;
            }
        }
        //日志名称
        String logName = "12306_TongChengConfirmChange_MQ";
        //高改，丢MQ支付
        if (highChange) {
            try {
                //接口类型
                int interfacetype = order.getInterfacetype() != null && order.getInterfacetype() > 0 ? order
                        .getInterfacetype() : getOrderAttribution(order);
                //重新赋值
                order.setInterfacetype(interfacetype);
                //超时时间
                String timeout = changeTimeout(order, trainOrderChange);
                //改签支付
                new TrainpayMqMSGUtil(MQMethod.ORDERPAY_NAME).sendPayMQmsgGQ(order, trainOrderChange, user, timeout);
                //记录日志
                WriteLog.write(logName, order.getId() + ":改签支付:" + trainOrderChange.getId());
            }
            catch (Exception e) {
                ExceptionUtil
                        .writelogByException(logName, e, order.getId() + ":改签支付发送MQ异常:" + trainOrderChange.getId());
            }
        }
        //非高改，丢MQ审核
        else {
            //丢MQ
            activeMQChangeOrder(changeId, 3);
            //释放账号
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
            //记录日志
            WriteLog.write(logName, order.getId() + ":改签审核:" + trainOrderChange.getId());
        }
        retobj.put("code", "100");
        retobj.put("success", true);
        retobj.put("msg", highChange ? "异步支付" : "异步审核");
        return retobj;
    }

    /**
     * 超时时间>>yyyy-MM-dd HH:mm:ss
     */
    private String changeTimeout(Trainorder trainorder, Trainorderchange change) {
        //时间
        String timeout = "";
        //格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //淘宝
        if (change.getChangetimeout() != null && TrainInterfaceMethod.TAOBAO == trainorder.getInterfacetype()) {
            timeout = sdf.format(change.getChangetimeout());
        }
        //返回
        return timeout;
    }

    /**
     * 12306>>高改初步审核、其他确认支付
     */
    private JSONObject pay12306(Customeruser user, Trainorder order, JSONArray changeTicketNo, float oldPrice,
            float changePrice, float changeProcedure, int random) {
        String url = "";
        if (user == null) {
            user = new Customeruser();
        }
        //参数，用于在12306验证数据
        JSONObject params = new JSONObject();
        params.put("cookie", user.getCardnunber());
        params.put("extnumber", order.getExtnumber());
        params.put("oldprice", oldPrice);
        params.put("newprice", changePrice);
        params.put("changeTicketNo", changeTicketNo);
        params.put("changeProcedure", changeProcedure);
        //调用接口，向12306确认
        String retdata = "";
        JSONObject obj12306 = new JSONObject();
        try {
            RepServerBean rep = RepServerUtil.getRepServer(user, false);
            url = rep.getUrl();
            //请求参数URLEncoder
            String jsonStr = URLEncoder.encode(params.toString(), "UTF-8");
            //请求参数
            String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr + JoinCommonAccountInfo(user, rep);
            //请求REP
            retdata = RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), 2 * 60 * 1000);
            //用户未登录
            if (retdata.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
                //切换REP
                rep = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
                //类型正确
                if (rep.getType() == 1) {
                    //REP地址
                    url = rep.getUrl();
                    //重拼参数
                    param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr + JoinCommonAccountInfo(user, rep);
                    //重新请求
                    retdata = RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), 2 * 60 * 1000);
                }
            }
            //返回数据
            obj12306 = JSONObject.parseObject(retdata);
        }
        catch (Exception e) {
        }
        finally {
            if (obj12306 == null) {
                obj12306 = new JSONObject();
            }
            WriteLog.write("t同程火车票接口_4.14确认改签", random + ">>>>>>" + order.getId() + ">>>>>REP服务器地址>>>>>" + url
                    + ">>>>>REP返回>>>>>" + retdata);
        }
        return obj12306;
    }

    /**
     * 问题改签
     */
    private void changeQuestionOrder(long changeId, boolean isAgain) {
        try {
            int question = Trainorderchange.PAYINGQUESTION;
            String sql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = " + question + " where ID = " + changeId;
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("12306_ChangeQuestionOrderException", e, changeId + "-->变支付问题异常");
        }
    }

    /**
     * 还原为改签完成
     */
    private void reductionChange(long changeId, boolean updateSuccess) {
        if (updateSuccess) {
            String updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + Trainorderchange.THOUGHCHANGE
                    + " where ID = " + changeId + " and C_TCSTATUS = " + Trainorderchange.CHANGEPAYING;
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
        }
    }

}