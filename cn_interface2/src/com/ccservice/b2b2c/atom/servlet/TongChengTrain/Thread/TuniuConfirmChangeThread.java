package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

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
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.interfacetype.TrainInterfaceType;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.servlet.MQ.AliPayUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Payresult;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.GuidUtil;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.elong.inter.PropertyUtil;
import com.weixin.util.RequestUtil;

/**
 * 途牛确认改签Thread
 * 
 * @time 2015年8月25日 下午5:30:46
 * @author 
 */
public class TuniuConfirmChangeThread extends TongchengSupplyMethod implements Runnable {

    String partnerid;

    int random;

    int checkCount;

    long checkWait;

    String orderid;

    String transactionid;

    String isasync;

    String callbackurl;

    String reqtoken;

    String changeid;

    String code113;

    String code999;

    String datatypeflag;

    String dataTypeFlag;

    SimpleDateFormat shiFenFormat;

    SimpleDateFormat totalFormat;

    boolean isneedtradeno;

    boolean McCached = false;

    String guid = "";

    public TuniuConfirmChangeThread(String partnerid, int random, int checkCount, long checkWait, String orderid,
            String transactionid, String isasync, String callbackurl, String reqtoken, String changeid, String code113,
            String code999, String datatypeflag, String dataTypeFlag, SimpleDateFormat shiFenFormat,
            SimpleDateFormat totalFormat) {
        this.partnerid = partnerid;
        this.random = random;
        this.checkCount = checkCount;
        this.checkWait = checkWait;
        this.orderid = orderid;
        this.transactionid = transactionid;
        this.isasync = isasync;
        this.callbackurl = callbackurl;
        this.reqtoken = reqtoken;
        this.changeid = changeid;
        this.code113 = code113;
        this.code999 = code999;
        this.datatypeflag = datatypeflag;
        this.dataTypeFlag = dataTypeFlag;
        this.shiFenFormat = shiFenFormat;
        this.totalFormat = totalFormat;

    }

    @Override
    public void run() {
        WriteLog.write("途牛确认改签Thread", partnerid + random + checkCount + checkWait + orderid + transactionid);
        String guid = GuidUtil.getUuid();
        McCached = setMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        //TODO  验证
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        //订单不存在
        if (orders == null || orders.size() != 1) {
            tuniuComfrimChangeCallBack(partnerid, 0, false);
            WriteLog.write("途牛确认改签Thread", "订单不存在orderid" + orderid);
            return;
        }
        Trainorder order = orders.get(0);
        long orderId = order.getId();
        int status = order.getOrderstatus();
        String extnumber = order.getExtnumber();
        //占座成功，12306订单号不为空
        if (status != Trainorder.ISSUED || ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
            WriteLog.write("途牛确认改签Thread", "该状态下不能改签,status:" + status + "---extnumber:" + extnumber + "--orderid:"
                    + orderid);
            tuniuComfrimChangeCallBack(partnerid, 0, false);

            return;
        }
        change(orderId, new Trainorder(), 0, random, isasync, callbackurl, reqtoken, orderid, changeid);
    }

    /**
     * 确认改签
     * @param changeType 改签类型，0：改签；1：改签退
     * @param changeid taobao改签单会传changeid
     */
    @SuppressWarnings("rawtypes")
    public void change(long orderId, Trainorder order, int changeType, int random, String isasync, String callbackurl,
            String reqtoken, String qunarordernumber, String changeid) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //订单改签信息
        String changeSql = "select top 1 ID, C_TCSTATUS, C_TCCREATETIME, C_TCDEPARTTIME, C_CONFIRMREQTOKEN, "
                + "ISNULL(C_ISQUESTIONCHANGE, 0) C_ISQUESTIONCHANGE from T_TRAINORDERCHANGE with(nolock) "
                + "where C_ORDERID = " + orderId + " order by ID desc";
        //如果传了changeid  可以直接用ID查找
        if (!ElongHotelInterfaceUtil.StringIsNull(changeid)) {
            changeSql = "select ID, C_TCSTATUS, C_TCCREATETIME, C_TCDEPARTTIME, C_CONFIRMREQTOKEN, "
                    + "ISNULL(C_ISQUESTIONCHANGE, 0) C_ISQUESTIONCHANGE from T_TRAINORDERCHANGE with(nolock) "
                    + "where ID = " + changeid;
        }
        WriteLog.write("途牛确认改签Thread", "changeid:" + changeid + "--orderid:" + orderid);
        List changeList = Server.getInstance().getSystemService().findMapResultBySql(changeSql, null);
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        //TODO 验证
        if (changeList == null || changeList.size() == 0) {
            WriteLog.write("途牛确认改签Thread", "未找到可以确认的改签车票" + "orderid:" + orderid);
            tuniuComfrimChangeCallBack(partnerid, 0, false);
            return;
        }
        //最后一个
        Map changeMap = (Map) changeList.get(0);
        //改签ID
        long changeId = Long.parseLong(changeMap.get("ID").toString());
        //改签状态
        int tcStatus = Integer.parseInt(changeMap.get("C_TCSTATUS").toString());
        //问题订单
        //int isQuestionChange = Integer.parseInt(changeMap.get("C_ISQUESTIONCHANGE").toString());
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
            WriteLog.write("途牛确认改签Thread", "确认请求已接受" + "orderid" + orderid);
            tuniuComfrimChangeCallBack(partnerid, changeId, true);
            return;
        }
        //状态判断
        if (tcStatus == Trainorderchange.FINISHCHANGE) {
            WriteLog.write("途牛确认改签Thread", "改签已确认，不能再次确认,tcStatus:" + tcStatus + "--orderid:" + orderid);
            tuniuComfrimChangeCallBack(partnerid, changeId, true);
            return;
        }
        else if (tcStatus == Trainorderchange.FAILCHANGE) {
            WriteLog.write("途牛确认改签Thread", "改签占座失败，不能确认,tcStatus:" + tcStatus + "--orderid:" + orderid);
            tuniuComfrimChangeCallBack(partnerid, changeId, true);
            return;
        }
        else if (tcStatus == Trainorderchange.CANTCHANGE) {
            WriteLog.write("途牛确认改签Thread", "改签已取消，不能继续确认,tcStatus:" + tcStatus + "--orderid:" + orderid);
            tuniuComfrimChangeCallBack(partnerid, changeId, true);
            return;
        }
        else if (tcStatus == Trainorderchange.CHANGEWAITPAY || tcStatus == Trainorderchange.CHANGEPAYING) {
            WriteLog.write("途牛确认改签Thread", "正在确认改签，不能再次确认,tcStatus:" + tcStatus);
            tuniuComfrimChangeCallBack(partnerid, changeId, true);
            return;
        }
        else if (tcStatus == Trainorderchange.THOUGHCHANGE) {
            //判断超时
            boolean timeout = false;
            WriteLog.write("途牛确认改签Thread", "判断超时,tcStatus:" + tcStatus);
            try {
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
                WriteLog.write("途牛确认改签Thread", "发车时间:" + tcDepartTime + "&时间差:" + subTime + "&createToDepart:"
                        + createToDepart);
                //12306在晚上11点半后不可支付了
                //开车前2小时内所购车票请于10分钟内完成支付
                //判断是否超时，22:44:59前成功申请的单子，供应商应该保留  30分钟的付款时间；22:45:00-23:00:00下的单子,供应商应该保留支付时限到23:30
                if (createToDepart < 120 * 60 * 1000 && subTime >= 8 * 60) {
                    timeout = true;
                }
                else if (subTime > 25 * 60) {//25分钟付款时间
                    timeout = true;
                }
            }
            catch (Exception e) {
            }
            if (timeout) {
                WriteLog.write("途牛确认改签Thread", "确认改签的请求时间已超过规定的时间--timeout" + timeout + "--orderid:" + orderid);
                tuniuComfrimChangeCallBack(partnerid, changeId, true);
                return;
            }
        }
        else {
            WriteLog.write("途牛确认改签Thread", "该状态下，不能确认改签----tcStatus:" + tcStatus + "--orderid:" + orderid);
            tuniuComfrimChangeCallBack(partnerid, changeId, true);
            return;
        }
        //异步改签
        if ("Y".equals(isasync)) {
            //配置地址
            String systemSetUrl = getTrainCallBackUrl(order.getAgentid(), 2);
            WriteLog.write("途牛确认改签Thread", "异步改签---isasync" + tcStatus + "--orderid:" + orderid);
            //非空，取配置
            if (!ElongHotelInterfaceUtil.StringIsNull(systemSetUrl)) {
                callbackurl = systemSetUrl;
            }
            AsyncChange(changeId, orderId, callbackurl, reqtoken, qunarordernumber, resetCaiGouQuestion);
            return;
        }
        //同步改签
        else {
            //改签信息
            Trainorderchange trainOrderChange = Server.getInstance().getTrainService()
                    .findTrainOrderChangeById(changeId);
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
            //查询订单
            if (order == null || order.getId() <= 0) {
                order = Server.getInstance().getTrainService().findTrainorder(orderId);
                McCached = getMemCachedTrainorder(orderid, guid);
                if (!McCached) {
                    return;
                }
            }
            //TODO 验证
            //确认操作
            changeOperate(trainOrderChange, order, changeType, false, random);
        }
    }

    /**
     * 异步确认
     */
    public void AsyncChange(long changeId, long orderId, String callbackurl, String reqtoken, String qunarordernumber,
            boolean resetCaiGouQuestion) {
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
            retobj.put("msg", "确认请求已接受");
            retobj.put("orderid", qunarordernumber);
            //操作日志
            createtrainorderrc(1, "[确认 - " + changeId + "]接口确认改签，等待支付12306", orderId, 0l, Trainticket.APPLYROCESSING,
                    "系统接口");
            //队列确认
            activeMQChangeOrder(changeId, 2);
        }
    }

    /**
     * 异步确认操作
     */
    public JSONObject AsyncChangeMQ(Trainorderchange trainOrderChange) {
        //订单ID
        long orderId = trainOrderChange.getOrderid();
        //查询订单
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderId);
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return null;
        }
        //确认操作
        JSONObject asyncResult = changeOperate(trainOrderChange, order, 0, true, new Random().nextInt(10000000));
        //确认成功
        if (asyncResult.getBooleanValue("success")) {
            //参数
            asyncResult.put("method", "train_confirm_change");
            asyncResult.put("reqtoken", trainOrderChange.getConfirmReqtoken());
            asyncResult.put("callBackUrl", trainOrderChange.getConfirmCallBackUrl());
            int interfacetype = order.getInterfacetype() != null ? order.getInterfacetype() : new TrainInterfaceType()
                    .getTrainInterfaceType(order.getId());
            if (interfacetype == TrainInterfaceMethod.TONGCHENG || interfacetype == TrainInterfaceMethod.MEITUAN) {
                //回调同程
                callBackTongCheng(asyncResult, order, trainOrderChange);
            }
            else if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                //回调淘宝
                callBackTaoBao(asyncResult, order, trainOrderChange);
            }
        }
        return asyncResult;
    }

    /**
     * 确认操作
     */
    public JSONObject changeOperate(Trainorderchange trainOrderChange, Trainorder order, int changeType,
            boolean isasync, int random) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //改签ID
        long changeId = trainOrderChange.getId();
        //订单ID
        long orderId = trainOrderChange.getOrderid();
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
        String tcTicketNo = "";
        String passenger_name = "";
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
                passenger_name = passenger.getName();
                tcTicketNo = changeTicket.getTcticketno();
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
                updateChange(false, changeId, Trainorderchange.PAYINGQUESTION, false);
            }
            //返回

            retobj.put("code", code113);
            retobj.put("msg", "确认改签失败");
            return retobj;
        }
        //下单账户
        String createAccount = order.getSupplyaccount();
        if (ElongHotelInterfaceUtil.StringIsNull(createAccount)) {
            //异步
            if (isasync) {
                updateChange(false, changeId, Trainorderchange.PAYINGQUESTION, false);
            }
            //返回
            retobj.put("code", code999);
            retobj.put("msg", "确认改签失败");
            return retobj;
        }
        //更新改签订单成功
        boolean updateSuccess = false;
        //同步，更新改签订单为支付中
        if (changeType == 0) {
            //异步
            if (isasync) {
                createtrainorderrc(1, "[确认 - " + changeId + "]开始支付异步改签", orderId, 0l, Trainticket.APPLYROCESSING,
                        "系统接口");
            }
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
        }
        //获取账号
        Customeruser user = getCustomeruserBy12306Account(order, random, true);
        if (user == null || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //还原
            reductionChange(changeId, updateSuccess);
            //异步
            if (isasync) {
                updateChange(false, changeId, Trainorderchange.PAYINGQUESTION, false);
            }
            //账号系统，以未登录释放账号
            if (user != null && user.isFromAccountSystem()) {
                freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            retobj.put("code", code999);
            retobj.put("msg", "确认改签失败");
            return retobj;
        }
        String url = "";
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
            url = RepServerUtil.getRepServer(user, false).getUrl();
            //请求参数URLEncoder
            String jsonStr = URLEncoder.encode(params.toString(), "UTF-8");
            //请求参数
            String param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr;
            //请求REP
            retdata = SendPostandGet.submitPost(url, param, "UTF-8").toString();
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
        boolean highChange = false;
        boolean FromAccountSystem = user.isFromAccountSystem();
        //失败
        if (!obj12306.getBooleanValue("success")) {
            //改签退
            if (changeType == 1) {
                //账号系统，释放账号
                if (FromAccountSystem) {
                    freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                            AccountSystem.NullDepartTime);
                }
                retobj.put("code", code999);
                retobj.put("msg", "确认改签失败");
                return retobj;
            }
            //新票款大于原票款
            if (changePrice > oldPrice && "新票款大于原票款，验证价格成功".equals(obj12306.getString("msg"))) {
                highChange = true;
            }
            else if (!obj12306.getBooleanValue("payConfirm")) {
                //还原
                reductionChange(changeId, updateSuccess);
                //异步
                if (isasync) {
                    updateChange(false, changeId, Trainorderchange.PAYINGQUESTION, false);
                }
                //账号系统，释放账号
                if (FromAccountSystem) {
                    freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                            AccountSystem.NullDepartTime);
                }
                //返回
                retobj.put("code", code999);
                retobj.put("msg", "确认改签失败");
                return retobj;
            }
        }
        //改签支付
        int question = 0;
        Payresult payResult = new Payresult();
        Payresult returnResult = new Payresult();
        boolean pay12306true = changeType == 0 ? false : true;
        //变更到站
        boolean isTs = trainOrderChange.getChangeArrivalFlag() == 1;
        //日志内容
        String changeFlag = isTs ? "变更到站" : "改签";
        //非改签退，虚拟账户扣新价格，退原价格
        if (changeType == 0) {
            //是否支付成功
            boolean paySuccess = highChange ? false : true;
            //高改，支付12306
            if (highChange) {
                AliPayUtil aliPayUtil = new AliPayUtil();
                //获取支付链接成功
                if (aliPayUtil.orderpayment(user, order, trainOrderChange, random)) {
                    //支付12306标识， 0:支付失败; 1:支付成功; 2:不确定是否支付成功
                    int payHighFlag = aliPayUtil.autoalipayPay(order, random, 0, trainOrderChange);
                    if (payHighFlag == 1 || payHighFlag == 2) {
                        paySuccess = true;
                    }
                }
            }
            //支付失败
            if (!paySuccess) {
                //还原
                reductionChange(changeId, updateSuccess);
                //异步
                if (isasync) {
                    updateChange(false, changeId, Trainorderchange.PAYINGQUESTION, false);
                }
                //账号系统，释放账号
                if (FromAccountSystem) {
                    freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                            AccountSystem.NullDepartTime);
                }
                //返回
                retobj.put("code", code999);
                retobj.put("msg", "确认改签失败");
                return retobj;
            }
            //支付审核
            pay12306true = check12306pay(user, order, tcTicketNo, passenger_name, random, 0, isTs);
            //支付成功
            if (pay12306true) {
                //记录日志
                createtrainorderrc(1, "[确认 - " + changeId + "]支付" + changeFlag + "订单成功,已审核", order.getId(), 0l, 0,
                        changeFlag + "支付审核");
                //扣除新票款
                payResult = changePayPrice(order.getId(), order.getOrdernumber(), changePrice,
                        order.getQunarOrdernumber(), order.getAgentid(), isasync);
                //扣款问题
                if (payResult == null || !payResult.isPaysuccess()) {
                    question = Trainorderchange.CUTPRICEQUESTION;
                }
                //支付成功，返还原票价
                else {
                    returnResult = changeReturnPrice(order.getId(), order.getOrdernumber(), oldPrice, changeProcedure,
                            order.getQunarOrdernumber(), order.getAgentid(), isasync);
                    //退款问题
                    if (returnResult == null || !returnResult.isPaysuccess()) {
                        question = Trainorderchange.RETURNPRICEQUESTION;
                    }
                }
            }
            //支付问题
            else {
                question = Trainorderchange.PAYINGQUESTION;
            }
        }
        //日志
        if (pay12306true) {
            //释放账号
            if (FromAccountSystem) {
                freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.TwoFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
                deleteMemCached(order);
            }
            try {
                //创建者
                String createuser = changeType == 1 ? "自动改签退" : "系统接口";
                //日志内容
                String content = "[确认 - " + changeId + "]接口请求确认" + changeFlag + "，12306<span style='color:red;'>确认"
                        + changeFlag + "成功</span>";
                //保存日志
                createtrainorderrc(1, content, orderId, 0l, Trainticket.FINISHCHANGE, createuser);
            }
            catch (Exception e) {
            }
        }
        //更新改签
        updateChange(pay12306true, changeId, question, false);
        //更新车票、同程需要信息
        JSONArray ticketmsg = new JSONArray();
        try {
            String ids = "";
            for (Trainticket changeTicket : changeTicketList) {
                //同程返回
                JSONObject ticket = new JSONObject();
                ticket.put("old_ticket_no", changeTicket.getTicketno());
                ticket.put("new_ticket_no", changeTicket.getTcticketno());
                ticket.put("cxin", changeTicket.getTccoach() + "车厢," + changeTicket.getTcseatno());
                ticketmsg.add(ticket);
                //更新本地
                if (pay12306true) {
                    ids += changeTicket.getId() + ",";
                }
            }
            if (ids.endsWith(",")) {
                //ID
                ids = ids.substring(0, ids.length() - 1);
                //SQL
                String updateSql = "";
                //状态
                int status = Trainticket.FINISHCHANGE;
                int status12306 = Trainticket.CHANGEDPAYED;
                //C_CHANGETYPE>>改签类型 1:线上改签、2:改签退
                if (changeType == 0) {
                    updateSql = "update T_TRAINTICKET set C_CHANGETYPE = 1, C_TCNEWPRICE = C_TCPRICE, C_STATUS = "
                            + status + ", C_STATE12306 = " + status12306 + " where ID in (" + ids + ")";
                }
                else {
                    updateSql = "update T_TRAINTICKET set C_CHANGETYPE = 2, C_STATE12306 = " + status12306
                            + " where ID in (" + ids + ")";
                }
                Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
                McCached = getMemCachedTrainorder(orderid, guid);
                if (!McCached) {
                    return null;
                }
            }
        }
        catch (Exception e) {
            System.out.println("确认改签，更新车票异常：" + e.getMessage());
        }
        if (!pay12306true) {
            retobj.put("code", code113);
            retobj.put("msg", "改签进行中，请联系供应商确认进度");
            return retobj;
        }
        JSONObject obj = new JSONObject();
        obj.put("code", "100");
        obj.put("success", true);
        obj.put("msg", "确认改签成功");
        obj.put("newticketcxins", ticketmsg);
        obj.put("orderid", order.getQunarOrdernumber());
        if (isasync) {
            obj.put("agentId", order.getAgentid());
        }
        if (isneedtradeno) {
            obj.put("changealipaytradeno", trainOrderChange.getSupplytradeno());
        }
        //非改签退
        if (changeType == 0) {
            if (payResult == null) {
                payResult = new Payresult();
            }
            if (returnResult == null) {
                returnResult = new Payresult();
            }
            //退还原票票款记录的同程资金变动流水号    1~32    string
            obj.put("oldticketchangeserial", returnResult.isPaysuccess() ? returnResult.getResultmessage() : "");
            //收取新票票款记录的同程资金变动流水号    1~32    string
            obj.put("newticketchangeserial", payResult.isPaysuccess() ? payResult.getResultmessage() : "");
        }
        return obj;
    }

    /**
     * 更新改签
     */
    private void updateChange(boolean pay12306true, long changeId, int question, boolean isAgain) {
        try {
            //STATUS
            int tcstatus = pay12306true ? Trainorderchange.FINISHCHANGE : Trainorderchange.CHANGEPAYING;
            int status12306 = pay12306true ? Trainorderchange.ORDEREDPAYED : Trainorderchange.ORDEREDPAYING;
            //SQL
            String updateSql = "";
            if (question > 0) {
                updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + tcstatus + ", C_STATUS12306 = "
                        + status12306 + ", C_ISQUESTIONCHANGE = " + question + " where ID = " + changeId;
            }
            else {
                updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + tcstatus + ", C_STATUS12306 = "
                        + status12306 + " where ID = " + changeId;
            }
            //UPDATE
            int updateSuccessSize = Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
            //更新失败
            if (updateSuccessSize != 1 && !isAgain) {
                updateChange(pay12306true, changeId, question, true);
            }
        }
        catch (Exception e) {
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
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
        }
    }

    /**
     * 非改签退，进行审核
     */
    private boolean check12306pay(Customeruser user, Trainorder order, String tcTicketNo, String passenger_name,
            int random, int errorCount, boolean isTs) {
        boolean isTrue = false;
        try {
            JSONObject params = new JSONObject();
            params.put("cookie", user.getCardnunber());
            params.put("passenger_name", passenger_name);
            params.put("sequence_no", order.getExtnumber());
            params.put("order_date", order.getCreatetime().toString().split(" ")[0]);
            //票号，验证一个即可
            JSONArray ticket_no = new JSONArray();
            ticket_no.add(tcTicketNo);
            params.put("ticket_no", ticket_no);
            /*****请求REP*****/
            String url = RepServerUtil.getRepServer(user, false).getUrl();
            //请求参数URLEncoder
            String jsonStr = URLEncoder.encode(params.toString(), "UTF-8");
            //请求参数
            String param = "datatypeflag=" + dataTypeFlag + "&jsonStr=" + jsonStr;
            //请求REP
            String retdata = SendPostandGet.submitPost(url, param, "UTF-8").toString();
            //返回数据
            JSONArray refunds = JSONObject.parseObject(retdata).getJSONArray("refunds");
            //查询到车票
            if (refunds != null && refunds.size() == 1) {
                JSONObject refund = refunds.getJSONObject(0);
                if (tcTicketNo.equals(refund.getString("ticket_no"))) {
                    String ticket_status_name = refund.getString("ticket_status_name");
                    //变更到站
                    if (isTs) {
                        if ("变更到站票".equals(ticket_status_name)) {
                            isTrue = true;
                        }
                    }
                    else {
                        if ("改签票".equals(ticket_status_name)) {
                            isTrue = true;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
        }
        if (!isTrue) {
            errorCount++;
            if (errorCount >= checkCount) {
                return isTrue;
            }
            //继续审核
            try {
                Thread.sleep(checkWait);
            }
            catch (Exception e) {
                System.out.println("改签审核，等待异常：" + ElongHotelInterfaceUtil.errormsg(e));
            }
            return check12306pay(user, order, tcTicketNo, passenger_name, random, errorCount, isTs);
        }
        return isTrue;
    }

    /**
     * 改签扣款和退差价
     * @param orderid 订单ID
     * @param ordernumber 订单号
     * @param orderprice 订单支付金额
     * @param refordernum  同城关联订单号
     * @param agentid 代理ID
     * @param virtual 虚拟，直接返回成功
     * @remark 业务类型 3.火车票（已无支付操作）31 火车票退票    32 改签退款  33改签扣款 34 线下改签
     */
    private Payresult changePayPrice(long orderid, String ordernumber, float orderprice, String refordernum,
            long agentid, boolean virtual) {
        if (virtual) {
            Payresult payResult = new Payresult();
            payResult.setPaysuccess(true);
            payResult.setResultmessage("");
            return payResult;
        }
        else {
            return vmonyPay(orderid, ordernumber, orderprice, 33, refordernum, agentid);
        }
    }

    private Payresult changeReturnPrice(long orderid, String ordernumber, float orderprice, float changeProcedure,
            String refordernum, long agentid, boolean virtual) {
        if (virtual) {
            Payresult returnResult = new Payresult();
            returnResult.setPaysuccess(true);
            returnResult.setResultmessage("");
            return returnResult;
        }
        else {
            orderprice = ElongHotelInterfaceUtil.floatSubtract(orderprice, changeProcedure);
            return vmonyPayReturn(orderid, ordernumber, -orderprice, 32, refordernum, agentid);
        }
    }

    /**
     * 回调、扣款、退款
     */
    private void callBackTongCheng(JSONObject retobj, Trainorder trainOrder, Trainorderchange trainOrderChange) {
        long orderId = trainOrder.getId();
        long changeId = trainOrderChange.getId();
        //地址
        String tcTrainCallBack = getSysconfigString("tcTrainCallBack");
        //回调
        String result = RequestUtil.post(tcTrainCallBack, retobj.toString(), "UTF-8", new HashMap<String, String>(), 0);
        //回调成功
        boolean callbacktrue = "success".equalsIgnoreCase(result) ? true : false;
        //采购问题
        if (!callbacktrue) {
            String updateSql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = " + Trainorderchange.CAIGOUQUESTION
                    + " where ID = " + changeId;
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
        }
        //改签结果
        String changeResult = retobj.getBoolean("success") ? "成功" : "失败";
        //回调结果
        String callbackResult = callbacktrue ? "成功" : "失败---" + result;
        //记录日志
        createtrainorderrc(1, "[确认 - " + changeId + "]---改签确认" + changeResult + "---回调" + callbackResult, orderId, 0l,
                Trainticket.APPLYROCESSING, "系统");
        //扣款、退款
        if (callbacktrue) {
            //扣除新票款
            Payresult payResult = changePayPrice(orderId, trainOrder.getOrdernumber(), trainOrderChange.getTcprice(),
                    trainOrder.getQunarOrdernumber(), trainOrder.getAgentid(), false);
            //扣款问题
            if (payResult == null || !payResult.isPaysuccess()) {
                String updateSql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = "
                        + Trainorderchange.CUTPRICEQUESTION + " where ID = " + changeId;
                Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
                McCached = getMemCachedTrainorder(orderid, guid);
                if (!McCached) {
                    return;
                }
            }
            //支付成功，返还原票价
            else {
                //记录日志
                createtrainorderrc(1, "[确认 - " + changeId + "]改签扣款成功：" + trainOrderChange.getTcprice(), orderId,
                        changeId, Trainticket.FINISHCHANGE, "系统");
                //退款操作
                Payresult returnResult = changeReturnPrice(orderId, trainOrder.getOrdernumber(),
                        trainOrderChange.getTcoriginalprice(), trainOrderChange.getChangeProcedure(),
                        trainOrder.getQunarOrdernumber(), trainOrder.getAgentid(), false);
                //退款问题
                if (returnResult == null || !returnResult.isPaysuccess()) {
                    String updateSql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = "
                            + Trainorderchange.RETURNPRICEQUESTION + " where ID = " + changeId;
                    Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
                    McCached = getMemCachedTrainorder(orderid, guid);
                    if (!McCached) {
                        return;
                    }
                }
                //记录日志
                else {
                    //退款
                    float price = ElongHotelInterfaceUtil.floatSubtract(trainOrderChange.getTcoriginalprice(),
                            trainOrderChange.getChangeProcedure());
                    //保存
                    createtrainorderrc(1, "[确认 - " + changeId + "]改签退款成功：" + price, orderId, changeId,
                            Trainticket.FINISHCHANGE, "系统");
                }
            }
        }
    }

    /**
     * 回调TAOBAO
     */
    private void callBackTaoBao(JSONObject retobj, Trainorder trainOrder, Trainorderchange trainOrderChange) {
        long orderId = trainOrder.getId();
        long changeId = trainOrderChange.getId();
        //确认改签
        retobj.put("method", "train_confirm_change");
        retobj.put("changeorderid", trainOrderChange.getId());
        //地址
        String TaoBaoChangeCallBackUrl = PropertyUtil.getValue("TaoBao_Change_CallBack_Url", "Train.properties");
        //回调
        String result = RequestUtil.post(TaoBaoChangeCallBackUrl, retobj.toString(), "UTF-8",
                new HashMap<String, String>(), 0);
        //回调成功
        boolean callbacktrue = "success".equalsIgnoreCase(result) ? true : false;
        //采购问题
        if (!callbacktrue) {
            String updateSql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = " + Trainorderchange.CAIGOUQUESTION
                    + " where ID = " + changeId;
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
        }
        //改签结果
        String changeResult = retobj.getBoolean("success") ? "成功" : "失败";
        //回调结果
        String callbackResult = callbacktrue ? "成功" : "失败---" + result;
        //记录日志
        createtrainorderrc(1, "[确认 - " + changeId + "]---改签确认" + changeResult + "---回调" + callbackResult, orderId, 0l,
                Trainticket.APPLYROCESSING, "系统");
        //淘宝虚拟帐户只供参考，成功与否忽略
        if (callbacktrue) {
            //扣除新票款
            Payresult payResult = changePayPrice(orderId, trainOrder.getOrdernumber(), trainOrderChange.getTcprice(),
                    trainOrder.getQunarOrdernumber(), trainOrder.getAgentid(), false);
            //扣款成功，返还原票款
            if (payResult != null && payResult.isPaysuccess()) {
                //返还原票款
                changeReturnPrice(orderId, trainOrder.getOrdernumber(), trainOrderChange.getTcoriginalprice(),
                        trainOrderChange.getChangeProcedure(), trainOrder.getQunarOrdernumber(),
                        trainOrder.getAgentid(), false);
            }
        }
    }

    /**
     * 改签结束删除MemCached中的账号
     */
    public void deleteMemCached(Trainorder order) {
        try {
            //账号
            String supplyaccount = order.getSupplyaccount();
            //删除
            if (!ElongHotelInterfaceUtil.StringIsNull(supplyaccount) && !"".equals(supplyaccount.split("/")[0])) {
                MemCached.getInstance().delete("TrainChange=" + supplyaccount.split("/")[0]);
            }
        }
        catch (Exception e) {

        }
    }

    /**
     * 判断McCached有没有值,有就返回false,没有,添加值并返回true
     * 
     * @param orderid
     * @return
     * @time 2015年8月22日 下午1:47:54
     * @author wcl
     */
    public boolean setMemCachedTrainorder(String orderid, String guid) {
        String mccguid = OcsMethod.getInstance().get("confirm=" + orderid);
        WriteLog.write("途牛确认改签回调Thread", "orderid:" + orderid + "--mccguid:" + mccguid);
        if (mccguid == null || "".equals(mccguid)) {
            OcsMethod.getInstance().add("confirm=" + orderid, guid, 120);
            WriteLog.write("途牛确认改签回调Thread", "orderid:" + orderid + "--guid:" + guid + "--mccguid" + mccguid);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 判断MemCached中有没有数据且数据是否一致, 有:true,没有false
     * 
     * @param orderid
     * @return
     * @time 2015年8月18日 下午6:52:17
     * @author wcl
     */
    public boolean getMemCachedTrainorder(String orderid, String guid) {
        WriteLog.write("途牛请求改签Thread", "orderid" + orderid + "-" + "guid:" + guid);
        String mccguid = OcsMethod.getInstance().get("confirm=" + orderid);
        if (mccguid != null || "".equals(mccguid) && OcsMethod.getInstance().get("confirm=" + orderid).equals(guid)) {
            WriteLog.write("途牛请求改签Thread", "orderid" + orderid + "-" + "guid:" + guid);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 回调
     * 
     * @param retobj
     * @time 2015年8月22日 下午1:51:22
     * @author wcl
     */
    @SuppressWarnings("rawtypes")
    public void tuniuComfrimChangeCallBack(String partnerid, long changeId, boolean existBool) {
        boolean bool = false;
        String url = PropertyUtil.getValue("TuNiu_CallBack_Url", "Train.properties");
        String sql = "SELECT C_CONFIRMCHANGECALLBACKURL,C_REQUESTCHANGECALLBACKURL FROM T_INTERFACEACCOUNT with(nolock) WHERE C_USERNAME='"
                + partnerid + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        Map map = (Map) list.get(0);
        String confrimChangeCallBackUrl = map.get("C_CONFIRMCHANGECALLBACKURL").toString();
        JSONObject jso = new JSONObject();
        jso.put("method", "train_confirm_change");
        jso.put("orderid", orderid);
        jso.put("transactionid", transactionid);
        jso.put("agentId", partnerid);
        jso.put("callBackUrl", confrimChangeCallBackUrl);
        for (int i = 0; i < 6; i++) {
            String resultUrlString = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            if (resultUrlString.equals("SUCCESS")) {
                OcsMethod.getInstance().remove("confirm=" + orderid);
                WriteLog.write("途牛确认改签Thread", "第" + i + "回调:" + resultUrlString);
                bool = true;
                break;
            }
            WriteLog.write("途牛确认改签Thread", "第" + i + "回调" + resultUrlString);
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //回调失败且订单存在
        if (!bool && existBool) {
            try {
                String update = "UPDATE T_TRAINORDERCHANGE SET C_ISQUESTIONCHANGE=3 WHERE ID=" + changeId;
                Server.getInstance().getSystemService().excuteAdvertisementBySql(update);
            }
            catch (Exception e) {
                WriteLog.write("err_TuniuConfirmChangeThread", "" + changeId);
                ExceptionUtil.writelogByException("err_TuniuConfirmChangeThread", e);
            }
        }
    }

}
