package com.ccservice.b2b2c.atom.service12306.offlineRefund;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.train.FuzzyRefund;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.uniontrade.Uniontrade;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.rep.RepRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.LocalRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.MatchTicketUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.AliPayRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.RefundCallBackUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.util.RefundTicketDataUtil;

/**
 * 火车票线下退款
 * @author WH
 * @time 2015年7月6日 下午5:53:32
 * @version 1.0
 * @备注
 * 1、同程、淘宝、途牛、去哪儿情况都不一样
 *      |- a、同程接收退票、改签
 *      |- b、淘宝只接收线下退票
 *      |- c、途牛接收退票、改签，退票人不明确时，可随便退一人
 *      |- d、去哪儿只接收线下退票
 *      |- e、优先处理前三个
 * 2、12306订单情况
 *      |- a、明确了退票或改签类型，并明确退款金额
 *      |- b、明确了退票或改签类型，但没有退款金额
 *      |- c、仅显示支付或出票信息，无退票、退款等
 *      |- d、不显示订单信息
 *      |- e、有的车票显示退票、退款等信息、有的不显示
 * 3、系统情况
 *      |- a、客服已经处理过某个退款
 *      |- b、程序已经线上退、或之前已经线下退
 *      |- c、车票已经进行过线上或线下改签退款
 *      |- d、某订单正在处理退款，下一个退款又进来了，等待处理完再操作
 * 4、模糊退之后统一走模糊退
 * 5、非多次支付，直接走退款逻辑；多次支付，如果12306明确退款了，直接走退款逻辑、如果没有明确退款，所有支付流水都有退款才走退款逻辑
 */

public class TrainOfflineRefundMethod extends TongchengSupplyMethod {

    private RepRefundUtil repUtil;

    private LocalRefundUtil localUtil;

    private MatchTicketUtil matchUtil;

    private String keyName = "OrderRefunding=";

    private RefundCallBackUtil refundCallBackUtil;

    /**
     * 订单正在退款
     */
    private boolean refunding(String[] payTradeNoArray, int specialFlag) {
        //正在
        int refunding = 0;
        //淘宝
        String special = specialFlag == 1 ? " ---> Apply" : "";
        //循环
        for (String tradeNum : payTradeNoArray) {
            //KEY
            String key = keyName + tradeNum;
            //内存存在
            if (RefundTicketDataUtil.refundDataContains(key)) {
                refunding++;
            }
            else {
                RefundTicketDataUtil.refundDataAdd(key, ElongHotelInterfaceUtil.getCurrentTime() + special);
            }
        }
        return refunding > 0;
    }

    /**
     * 内存中移除
     */
    private void remove(String[] payTradeNoArray) {
        //循环
        for (String tradeNum : payTradeNoArray) {
            //KEY
            String key = keyName + tradeNum;
            //移除
            RefundTicketDataUtil.refundDataRemove(key);
        }
    }

    /**
     * 当前时间00:00至06:00
     */
    private boolean isNight() {
        boolean isNight = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            //当前
            Date current = sdf.parse(sdf.format(new Date()));
            //00:00 - 06:00
            Date start = sdf.parse("23:55");
            Date end = sdf.parse("06:05");
            //判断
            if (current.after(start) || current.before(end)) {
                isNight = true;
            }
        }
        catch (Exception e) {

        }
        return isNight;
    }

    /**
     * @param tradeNum 商户交易号
     * @param aliTotalRefund 抓取支付宝页面的退款
     * @param passengers 乘客姓名，用于同程导表格数据
     * @param specialFlag 特殊标识>>如果是淘宝申请线下退款，状态要作特殊处理
     */
    @SuppressWarnings("rawtypes")
    public JSONObject operate(JSONObject reqdata) {
        //1:订单；2:改签
        String busType = reqdata.getString("busType");
        String payTradeNos = reqdata.getString("payTradeNos");
        long orderOrChangeId = reqdata.getLongValue("orderId");
        boolean newModel = reqdata.getBooleanValue("newModel");
        JSONArray passengers = reqdata.getJSONArray("passengers");
        //特殊标识
        int specialFlag = reqdata.getIntValue("specialFlag");
        //明确退款
        float aliTotalRefund = reqdata.getFloatValue("aliTotalRefund");
        //返回数据
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //交易号为空
        if (ElongHotelInterfaceUtil.StringIsNull(payTradeNos)) {
            retobj.put("result", "商户交易号为空");
            return retobj;
        }
        //自动开启标识
        if (!"1".equals(getSysconfigString("AutoOfflineRefundOpen"))) {
            retobj.put("result", "线下自动退未开启");
            return retobj;
        }
        //夜间不走
        if (isNight()) {
            retobj.put("result", "00:00-06:00之间，后续不走");
            return retobj;
        }
        //支付流水
        String[] payTradeNoArray = payTradeNos.split("@");
        //正在退款
        if (refunding(payTradeNoArray, specialFlag)) {
            retobj.put("result", "订单正在进行退款");
            return retobj;
        }
        //捕捉异常
        try {
            //初始化
            repUtil = new RepRefundUtil();
            localUtil = new LocalRefundUtil();
            matchUtil = new MatchTicketUtil();
            refundCallBackUtil = new RefundCallBackUtil();
            passengers = passengers == null ? new JSONArray() : passengers;
            //随机数
            int random = new Random().nextInt(900000) + 100000;
            //查询订单
            List<Trainorder> orders = new ArrayList<Trainorder>();
            //订单模式
            if ("1".equals(busType)) {
                //虚拟
                Trainorder order = new Trainorder();
                order.setId(orderOrChangeId);
                //ADD
                orders.add(order);
            }
            //非新模式
            else if (!newModel) {
                Trainform trainform = new Trainform();
                trainform.setSupplytradeno(payTradeNos);
                orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
            }
            //订单长度
            int size = orders == null ? 0 : orders.size();
            //订单
            if (size == 1) {
                //订单ID
                long orderId = orders.get(0).getId();
                //退款信息
                JSONObject refundData = localUtil.refundData(payTradeNoArray);
                //订单处理
                retobj = order(orderId, aliTotalRefund, refundData, payTradeNoArray, passengers, random, specialFlag);
                //结果为空
                if (retobj == null) {
                    retobj = new JSONObject();
                    retobj.put("success", false);
                }
                //设置ID
                retobj.put("orderId", orderId);
            }
            else {
                //改签ID
                long changeId = 0;
                //改签模式
                if ("2".equals(busType)) {
                    changeId = orderOrChangeId;
                }
                //非新模式
                else if (!newModel && size == 0) {
                    //SQL
                    String sql = "select ID from T_TRAINORDERCHANGE with(nolock) where C_SUPPLYTRADENO = '"
                            + payTradeNos + "'";
                    //查改签
                    List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    //查询成功
                    if (list != null && list.size() == 1) {
                        Map map = (Map) list.get(0);
                        changeId = Long.parseLong(map.get("ID").toString());
                    }
                }
                //改签退款
                if (changeId > 0) {
                    //退款信息
                    JSONObject refundData = localUtil.refundData(payTradeNoArray);
                    //改签处理
                    retobj = change(changeId, aliTotalRefund, refundData, payTradeNoArray, passengers, random,
                            specialFlag);
                    //结果为空
                    if (retobj == null) {
                        retobj = new JSONObject();
                        retobj.put("success", false);
                    }
                    //设置ID
                    retobj.put("changeId", changeId);
                }
                //非改签订单
                else {
                    localUtil.updateAliByTrandNum(0, payTradeNoArray, "-3");
                    //设置结果
                    retobj.put("result", "未查询到订单");
                }
            }
        }
        catch (Exception e) {
            //结果为空
            if (retobj == null) {
                retobj = new JSONObject();
                retobj.put("success", false);
            }
            //异常信息
            retobj.put("Exception", ElongHotelInterfaceUtil.errormsg(e));
            //异常日志
            ExceptionUtil.writelogByException("TrainOfflineRefund_Exception", e, payTradeNos);
        }
        finally {
            remove(payTradeNoArray);//内存移除
        }
        //返回数据
        return retobj;
    }

    /**
     * 普通订单处理
     */
    private JSONObject order(long orderId, float aliTotalRefund, JSONObject refundData, String[] payTradeNoArray,
            JSONArray passengers, int random, int specialFlag) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //加载订单
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderId);
        //查询失败
        if (order == null || !localUtil.payTradeNoIsRight(payTradeNoArray, order.getSupplytradeno())) {
            localUtil.updateAliByTrandNum(0, payTradeNoArray, "-3");
            //返回
            retobj.put("result", "未查询到订单");
            return retobj;
        }
        //所有车票
        List<Trainticket> orderTicketList = localUtil.orderTicket(order);
        //非高改票
        List<Trainticket> notHighChangeTicketList = localUtil.notHighChangeTicket(order);
        //已退车票
        List<Trainticket> orderRefundList = localUtil.orderRefund(notHighChangeTicketList);
        /**
         * 全部退完
         */
        if (notHighChangeTicketList.size() == orderRefundList.size()) {
            localUtil.updateAliByTrandNum(orderId, payTradeNoArray, "-1");
            //返回
            retobj.put("result", "已退完: " + orderRefundList.size());
            return retobj;
        }
        /**
         * 校验订单
         */
        String checkOrder = localUtil.checkOrder(order);
        //存在错误
        if (!ElongHotelInterfaceUtil.StringIsNull(checkOrder)) {
            retobj.put("result", checkOrder);
            return retobj;
        }
        //模糊退数据
        List<FuzzyRefund> fuzzyRefundList = localUtil.orderFuzzy(order);
        List<FuzzyRefund> notHighChangeFuzzyRefundList = localUtil.changeFuzzy(new Trainorderchange(), fuzzyRefundList);
        /**
         * 校验车票
         */
        String checkTicket = localUtil.checkTicket(fuzzyRefundList, orderTicketList);
        //存在错误
        if (!ElongHotelInterfaceUtil.StringIsNull(checkTicket)) {
            retobj.put("result", checkTicket);
            return retobj;
        }
        /**
         * 以车票款项计算还能退多少钱
         */
        float canRefundPriceByTicket = localUtil.countCanRefundPriceByTicket(fuzzyRefundList, orderTicketList);
        //不能再退了
        if (canRefundPriceByTicket < 0) {
            retobj.put("result", "以车票计算还能退的金额错误，车票状态或金额可能不符合条件");
            return retobj;
        }
        if (canRefundPriceByTicket == 0) {
            localUtil.updateAliByTrandNum(orderId, payTradeNoArray, "-2");
            //返回
            retobj.put("result", "以车票计算还能退的金额，已处理完");
            return retobj;
        }
        /**
         * 交易记录，计算订单还能退多少钱
         */
        float canRefundPriceByRebaterecord = localUtil.countCanRefundPriceByRebaterecord(order);
        //不能再退了
        if (canRefundPriceByRebaterecord < 0) {
            retobj.put("result", "以交易记录计算还能退的金额错误: " + canRefundPriceByRebaterecord);
            return retobj;
        }
        if (canRefundPriceByRebaterecord == 0) {
            localUtil.updateAliByTrandNum(orderId, payTradeNoArray, "-2");
            //返回
            retobj.put("result", "以交易记录计算还能退的金额，已处理完");
            return retobj;
        }
        //能退金额不一致
        if (canRefundPriceByTicket != canRefundPriceByRebaterecord) {
            retobj.put("result", "能退金额不一致: " + canRefundPriceByTicket + "#" + canRefundPriceByRebaterecord);
            return retobj;
        }
        /**
         * 支付宝退款
         */
        List<Uniontrade> aliRefundPrice = localUtil.aliRefundPrice(refundData.getString("payTradeNo"));
        //临时处理
        float tempRefundTotal = localUtil.aliRefundTotal(aliRefundPrice);
        //走虚拟
        if (aliRefundPrice.size() == 0 && aliTotalRefund > 0) {
            aliRefundPrice.add(AliPayRefundUtil.getRefundTrade(aliTotalRefund, AliPayRefundUtil.NullDepartTime));
        }
        //临时处理
        else if (specialFlag == 1 && tempRefundTotal > 0 && tempRefundTotal < aliTotalRefund) {
            //添加>>淘宝实际退款-数据库中退款
            aliRefundPrice.add(AliPayRefundUtil.getRefundTrade(
                    ElongHotelInterfaceUtil.floatSubtract(aliTotalRefund, tempRefundTotal),
                    AliPayRefundUtil.NullDepartTime));
        }
        //支付宝累计退款
        float aliRefundTotal = localUtil.aliRefundTotal(aliRefundPrice);
        //累计退款错误
        if (aliRefundTotal < 0) {
            retobj.put("result", "支付宝累计退款错误");
            return retobj;
        }
        //支付宝无退款
        if (aliRefundTotal == 0) {
            retobj.put("result", "支付宝无退款");
            return retobj;
        }
        /**
         * 车票累计退款
         */
        float ticketRefundTotal = localUtil.ticketRefundTotal(notHighChangeFuzzyRefundList, orderTicketList, false);
        //累计退款错误
        if (ticketRefundTotal < 0) {
            retobj.put("result", "以车票计算车票累计退款错误，车票状态或金额可能不符合条件");
            return retobj;
        }
        //当前支付宝退款金额已退完
        if (aliRefundTotal <= ticketRefundTotal) {
            localUtil.updateAliByTrandNum(orderId, payTradeNoArray, "-2");
            //返回
            retobj.put("result", "已处理完: " + aliRefundTotal + "#" + ticketRefundTotal);
            return retobj;
        }
        //还能退的金额
        aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, ticketRefundTotal);
        //临时处理
        if (aliRefundPrice.size() == 1 && specialFlag == 1 && ticketRefundTotal > 0) {
            /*
            //原来
            Uniontrade old = aliRefundPrice.get(0);
            //临时
            Uniontrade tempOne = new Uniontrade();
            Uniontrade tempTwo = new Uniontrade();
            //重置ID
            tempOne.setId(-1L);
            tempTwo.setId(-2L);
            //重置金额
            tempTwo.setAmount(aliRefundTotal);
            tempOne.setAmount(ticketRefundTotal);
            //重置时间
            tempOne.setOrdertime(old.getOrdertime());
            tempTwo.setOrdertime(old.getOrdertime());
            //重置退款
            aliRefundPrice = new ArrayList<Uniontrade>();
            //重新设置
            aliRefundPrice.add(tempOne);
            aliRefundPrice.add(tempTwo);
            */
        }
        //返回结果
        return rep(order, new Trainorderchange(), notHighChangeTicketList, canRefundPriceByRebaterecord,
                aliRefundTotal, ticketRefundTotal, aliRefundPrice, passengers, fuzzyRefundList,
                notHighChangeFuzzyRefundList, payTradeNoArray, refundData, random, specialFlag);
    }

    /**
     * 改签订单处理
     */
    private JSONObject change(long changeId, float aliTotalRefund, JSONObject refundData, String[] payTradeNoArray,
            JSONArray passengers, int random, int specialFlag) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //加载改签
        Trainorderchange change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
        //查询失败
        if (change == null || !localUtil.payTradeNoIsRight(payTradeNoArray, change.getSupplytradeno())) {
            localUtil.updateAliByTrandNum(0, payTradeNoArray, "-4");
            //返回
            retobj.put("result", "未查询到改签");
            return retobj;
        }
        //订单ID
        long orderId = change.getOrderid();
        //加载订单
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderId);
        //查询失败
        if (order == null || order.getId() != orderId) {
            localUtil.updateAliByTrandNum(0, payTradeNoArray, "-3");
            //返回
            retobj.put("result", "未查询到订单");
            return retobj;
        }
        //所有车票
        List<Trainticket> orderTicketList = localUtil.orderTicket(order);
        //改签车票
        List<Trainticket> changeTicketList = localUtil.changeTicket(order, change);
        //已退车票
        List<Trainticket> orderRefundList = localUtil.orderRefund(changeTicketList);
        /**
         * 全部退完
         */
        if (changeTicketList.size() == orderRefundList.size()) {
            localUtil.updateAliByTrandNum(orderId, payTradeNoArray, "-1");
            //返回
            retobj.put("result", "已退完: " + orderRefundList.size());
            return retobj;
        }
        /**
         * 校验改签
         */
        String checkChange = localUtil.checkChange(order, change);
        //存在错误
        if (!ElongHotelInterfaceUtil.StringIsNull(checkChange)) {
            retobj.put("result", checkChange);
            return retobj;
        }
        //模糊退数据
        List<FuzzyRefund> fuzzyRefundList = localUtil.orderFuzzy(order);
        List<FuzzyRefund> changeFuzzyRefundList = localUtil.changeFuzzy(change, fuzzyRefundList);
        /**
         * 校验车票
         */
        String checkTicket = localUtil.checkTicket(fuzzyRefundList, orderTicketList);
        //存在错误
        if (!ElongHotelInterfaceUtil.StringIsNull(checkTicket)) {
            retobj.put("result", checkTicket);
            return retobj;
        }
        /**
         * 以车票款项计算还能退多少钱
         */
        float canRefundPriceByTicket = localUtil.countCanRefundPriceByTicket(fuzzyRefundList, orderTicketList);
        //不能再退了
        if (canRefundPriceByTicket < 0) {
            retobj.put("result", "以车票计算还能退的金额错误，车票状态或金额可能不符合条件");
            return retobj;
        }
        if (canRefundPriceByTicket == 0) {
            localUtil.updateAliByTrandNum(orderId, payTradeNoArray, "-2");
            //返回
            retobj.put("result", "以车票计算还能退的金额，已处理完");
            return retobj;
        }
        /**
         * 交易记录，计算订单还能退多少钱
         */
        float canRefundPriceByRebaterecord = localUtil.countCanRefundPriceByRebaterecord(order);
        //不能再退了
        if (canRefundPriceByRebaterecord < 0) {
            retobj.put("result", "以交易记录计算还能退的金额错误: " + canRefundPriceByRebaterecord);
            return retobj;
        }
        if (canRefundPriceByRebaterecord == 0) {
            localUtil.updateAliByTrandNum(orderId, payTradeNoArray, "-2");
            //返回
            retobj.put("result", "以交易记录计算还能退的金额，已处理完");
            return retobj;
        }
        //能退金额不一致
        if (canRefundPriceByTicket != canRefundPriceByRebaterecord) {
            retobj.put("result", "能退金额不一致: " + canRefundPriceByTicket + "#" + canRefundPriceByRebaterecord);
            return retobj;
        }
        /**
         * 支付宝退款
         */
        List<Uniontrade> aliRefundPrice = localUtil.aliRefundPrice(refundData.getString("payTradeNo"));
        //临时处理
        float tempRefundTotal = localUtil.aliRefundTotal(aliRefundPrice);
        //走虚拟
        if (aliRefundPrice.size() == 0 && aliTotalRefund > 0) {
            aliRefundPrice.add(AliPayRefundUtil.getRefundTrade(aliTotalRefund, AliPayRefundUtil.NullDepartTime));
        }
        //临时处理
        else if (specialFlag == 1 && tempRefundTotal > 0 && tempRefundTotal < aliTotalRefund) {
            //添加>>淘宝实际退款-数据库中退款
            aliRefundPrice.add(AliPayRefundUtil.getRefundTrade(
                    ElongHotelInterfaceUtil.floatSubtract(aliTotalRefund, tempRefundTotal),
                    AliPayRefundUtil.NullDepartTime));
        }
        //支付宝累计退款
        float aliRefundTotal = localUtil.aliRefundTotal(aliRefundPrice);
        //累计退款错误
        if (aliRefundTotal < 0) {
            retobj.put("result", "支付宝累计退款错误");
            return retobj;
        }
        //支付宝无退款
        if (aliRefundTotal == 0) {
            retobj.put("result", "支付宝无退款");
            return retobj;
        }
        /**
         * 车票累计退款
         */
        float ticketRefundTotal = localUtil.ticketRefundTotal(changeFuzzyRefundList, changeTicketList, true);
        //累计退款错误
        if (ticketRefundTotal < 0) {
            retobj.put("result", "以车票计算车票累计退款错误，车票状态或金额可能不符合条件");
            return retobj;
        }
        //当前支付宝退款金额已退完
        if (aliRefundTotal <= ticketRefundTotal) {
            localUtil.updateAliByTrandNum(orderId, payTradeNoArray, "-2");
            //返回
            retobj.put("result", "已处理完: " + aliRefundTotal + "#" + ticketRefundTotal);
            return retobj;
        }
        //还能退的金额
        aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, ticketRefundTotal);
        //临时处理
        if (aliRefundPrice.size() == 1 && specialFlag == 1 && ticketRefundTotal > 0) {
            /*
            //原来
            Uniontrade old = aliRefundPrice.get(0);
            //临时
            Uniontrade tempOne = new Uniontrade();
            Uniontrade tempTwo = new Uniontrade();
            //重置ID
            tempOne.setId(-1L);
            tempTwo.setId(-2L);
            //重置金额
            tempTwo.setAmount(aliRefundTotal);
            tempOne.setAmount(ticketRefundTotal);
            //重置时间
            tempOne.setOrdertime(old.getOrdertime());
            tempTwo.setOrdertime(old.getOrdertime());
            //重置退款
            aliRefundPrice = new ArrayList<Uniontrade>();
            //重新设置
            aliRefundPrice.add(tempOne);
            aliRefundPrice.add(tempTwo);
            */
        }
        //返回结果
        return rep(order, change, changeTicketList, canRefundPriceByRebaterecord, aliRefundTotal, ticketRefundTotal,
                aliRefundPrice, passengers, fuzzyRefundList, changeFuzzyRefundList, payTradeNoArray, refundData,
                random, specialFlag);
    }

    /**
     * REP处理
     */
    private JSONObject rep(Trainorder order, Trainorderchange change, List<Trainticket> orderTicketList,
            float oldCanRefund, float aliRefundTotal, float ticketRefundTotal, List<Uniontrade> aliRefundPrice,
            JSONArray passengers, List<FuzzyRefund> fuzzyRefundList, List<FuzzyRefund> changeFuzzyRefundList,
            String[] payTradeNoArray, JSONObject refundData, int random, int specialFlag) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //下单用户
        Customeruser user = repUtil.getUserByOrder(order, new Customeruser(), random);
        //未获取到用户
        user = user == null ? new Customeruser() : user;
        //12306订单信息
        Map<String, String> orderInfoMap = repUtil.get12306OrderInfo(order, user, random);
        //错误
        if (orderInfoMap.size() <= 0) {
            retobj.put("result", "获取订单信息异常");
            return retobj;
        }
        //详情
        String DetailInfo = orderInfoMap.get("DetailInfo");
        //已发车
        String YiFaCheInfo = orderInfoMap.get("YiFaCheInfo");
        //未发车
        String WeiFaCheInfo = orderInfoMap.get("WeiFaCheInfo");
        //列表
        List<JSONObject> listInfoList = new ArrayList<JSONObject>();
        Map<String, JSONObject> listInfoMap = new HashMap<String, JSONObject>();
        //PUT
        listInfoMap.putAll(repUtil.ticketStatusIn12306(YiFaCheInfo));
        listInfoMap.putAll(repUtil.ticketStatusIn12306(WeiFaCheInfo));
        //未出票统计
        List<Trainticket> weiChuPiaoList = new ArrayList<Trainticket>();
        //可按退票处理
        List<Trainticket> canGoRefundList = new ArrayList<Trainticket>();
        //ID对应车票
        Map<Long, Trainticket> idTicketMap = new HashMap<Long, Trainticket>();
        //车票在12306状态等
        Map<Long, JSONObject> listTicketMap = new HashMap<Long, JSONObject>();
        //循环车票
        for (Trainticket ticket : orderTicketList) {
            idTicketMap.put(ticket.getId(), ticket);
            //票号
            String ticketNo = ticket.getTicketno();
            //车票状态
            int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
            //已线上改签、改签退已审核
            if (changeType == 1 || changeType == 2) {
                ticketNo = ticket.getTcticketno();
            }
            //列表
            JSONObject temp = listInfoMap.get(ticketNo);
            temp = temp == null ? new JSONObject() : temp;
            //可退
            if (repUtil.canGoRefund(temp, ticket)) {
                canGoRefundList.add(ticket);
            }
            //跳过
            if (!listInfoMap.containsKey(ticketNo)) {
                continue;
            }
            //未出票
            if (repUtil.weiChuPiao(temp)) {
                weiChuPiaoList.add(ticket);
            }
            listInfoList.add(temp);
            listTicketMap.put(ticket.getId(), temp);
        }
        if (weiChuPiaoList.size() == orderTicketList.size()) {
            retobj.put("result", "订单车票均未制票成功");
            return retobj;
        }
        //详情
        JSONObject matchdata = repUtil.matchTicketBy12306Data(order, change, orderTicketList, DetailInfo,
                listTicketMap, listInfoList, idTicketMap);
        //错误
        if (!matchdata.getBooleanValue("success")) {
            matchdata = repUtil.resetMatchData(orderTicketList);//全部设为unknown
        }
        return match(matchdata, order, change, idTicketMap, weiChuPiaoList, oldCanRefund, aliRefundTotal,
                ticketRefundTotal, aliRefundPrice, orderTicketList, passengers, fuzzyRefundList, changeFuzzyRefundList,
                payTradeNoArray, refundData, specialFlag, canGoRefundList, random);
    }

    /**
     * 匹配后处理
     * @param orderTicketList 所有车票
     * @param weiChuPiaoList 12306显示的未出票
     * @param oldCanRefund 原订单可退金额
     * @param aliRefundTotal 支付宝还能退的金额
     * @param ticketRefundTotal 车票累计退款
     * @param fuzzyRefundList 所有模糊退记录
     * @param changeFuzzyRefundList 高改时表示改签模糊退，非高改表示非高改模糊退
     */
    private JSONObject match(JSONObject matchdata, Trainorder order, Trainorderchange change,
            Map<Long, Trainticket> idTicketMap, List<Trainticket> weiChuPiaoList, float oldCanRefund,
            float aliRefundTotal, float ticketRefundTotal, List<Uniontrade> aliRefundPrice,
            List<Trainticket> orderTicketList, JSONArray passengers, List<FuzzyRefund> fuzzyRefundList,
            List<FuzzyRefund> changeFuzzyRefundList, String[] payTradeNoArray, JSONObject refundData, int specialFlag,
            List<Trainticket> canGoRefundList, int random) {
        //线上高改
        boolean highChange = change.getId() > 0;
        //退票、改签不明确
        JSONArray unknown = matchdata.getJSONArray("unknown");
        //仅明确改签
        JSONArray gaiqian = matchdata.getJSONArray("gaiqian");
        //仅明确退票
        JSONArray tuipiao = matchdata.getJSONArray("tuipiao");
        //明确改签、退款
        JSONArray gaiqianAndtuikuan = matchdata.getJSONArray("gaiqianAndtuikuan");
        //明确退票、退款
        JSONArray tuipiaoAndtuikuan = matchdata.getJSONArray("tuipiaoAndtuikuan");
        //1、非多次支付，直接走退款逻辑
        //2、多次支付，如果12306明确已退票了，直接走退款逻辑
        //3、多次支付，如果没有明确退票，所有支付流水都有退款才走退款逻辑
        //判断逻辑>>重复支付、12306未明确改签或退票、支付流水存在没有退款的
        if (payTradeNoArray.length > 1 && orderTicketList.size() == unknown.size()
                && !refundData.getBooleanValue("allRefunded")) {
            JSONObject retobj = new JSONObject();
            retobj.put("success", false);
            retobj.put("result", "重复支付，支付流水存在没有退款的");
            return retobj;
        }
        //线上高改数据错误
        if (highChange && (gaiqian.size() > 0 || gaiqianAndtuikuan.size() > 0)) {
            JSONObject retobj = new JSONObject();
            retobj.put("success", false);
            retobj.put("result", "线上高改数据存在线下改签，冲突");
            return retobj;
        }
        //模糊退之后统一走模糊退
        if (fuzzyRefundList != null && fuzzyRefundList.size() > 0) {
            return fuzzy(order, change, oldCanRefund, aliRefundTotal, ticketRefundTotal, aliRefundPrice,
                    orderTicketList, changeFuzzyRefundList, highChange, payTradeNoArray, specialFlag, weiChuPiaoList,
                    canGoRefundList, random);
        }
        //统计数据
        int totalCount = 0;
        int refundCount = 0;
        //每次退款处理结果
        JSONObject refundResult = new JSONObject();
        refundResult.put("success", true);
        //原订单可退金额
        refundResult.put("oldCanRefund", oldCanRefund);
        //支付宝还能退的金额
        refundResult.put("aliRefundTotal", aliRefundTotal);
        //车票累计退款
        refundResult.put("ticketRefundTotal", ticketRefundTotal);
        //明确退票、退款处理逻辑
        refundResult = tuipiaoAndtuikuan(refundResult, tuipiaoAndtuikuan, order, idTicketMap, highChange);
        totalCount += refundResult.getIntValue("totalCount");
        refundCount += refundResult.getIntValue("refundCount");
        //明确改签、退款处理逻辑
        refundResult = gaiqianAndtuikuan(refundResult, gaiqianAndtuikuan, order, idTicketMap, specialFlag, random);
        totalCount += refundResult.getIntValue("totalCount");
        refundCount += refundResult.getIntValue("refundCount");
        //仅明确退票处理逻辑
        refundResult = tuipiao(refundResult, tuipiao, order, idTicketMap, aliRefundPrice, highChange);
        totalCount += refundResult.getIntValue("totalCount");
        refundCount += refundResult.getIntValue("refundCount");
        //仅明确改签处理逻辑
        refundResult = gaiqian(refundResult, gaiqian, order, idTicketMap, specialFlag, random);
        //单个车票已改签，按未知处理，走退票
        if (orderTicketList.size() == 1 && refundResult.getBooleanValue("oneRefund")
                && refundResult.getFloatValue("aliRefundTotal") > 0 && unknown.size() == 0) {
            //RESET
            refundResult.put("refundCount", 0);
            //ID
            long ticketId = orderTicketList.get(0).getId();
            //NEW
            JSONArray matchArray = new JSONArray();
            JSONObject matchObject = new JSONObject();
            //ADD
            matchArray.add(ticketId);
            matchObject.put("ticketIds", matchArray);
            //ADD
            unknown.add(matchObject);
        }
        else {
            totalCount += refundResult.getIntValue("totalCount");
            refundCount += refundResult.getIntValue("refundCount");
        }
        //退票、改签不明确处理逻辑
        refundResult = unknown(refundResult, unknown, order, change, idTicketMap, aliRefundPrice, weiChuPiaoList,
                passengers, orderTicketList, changeFuzzyRefundList, highChange, specialFlag, canGoRefundList, random);
        totalCount += refundResult.getIntValue("totalCount");
        refundCount += refundResult.getIntValue("refundCount");
        //重新赋值
        refundResult.put("totalCount", totalCount);
        refundResult.put("refundCount", refundCount);
        //处理完成
        if (totalCount == refundCount || refundResult.getFloat("aliRefundTotal") <= 0) {
            refundResult.put("success", true);
            refundResult.put("result", "处理成功");
            localUtil.updateAliByTrandNum(order.getId(), payTradeNoArray, "-2");
        }
        //返回
        return refundResult;
    }

    /**
     * 模糊退之后统一走模糊退
     */
    private JSONObject fuzzy(Trainorder order, Trainorderchange change, float oldCanRefund, float aliRefundTotal,
            float ticketRefundTotal, List<Uniontrade> aliRefundPrice, List<Trainticket> orderTicketList,
            List<FuzzyRefund> changeFuzzyRefundList, boolean highChange, String[] payTradeNoArray, int specialFlag,
            List<Trainticket> weiChuPiaoList, List<Trainticket> canGoRefundList, int random) {
        //结果
        JSONObject result = new JSONObject();
        //总的
        String title = "模糊退之后统一走模糊退失败：";
        //初始化
        result.put("success", false);
        result.put("result", title + "代理不可模糊退");
        //可模糊退
        if (specialFlag == 1 || refundCallBackUtil.canFuzzyRefund(order.getAgentid())) {
            //未处理退款
            List<Uniontrade> noRefundedTrade = matchUtil.matchTradeByRefundTotal(aliRefundPrice, ticketRefundTotal,
                    false);
            //总退款金额
            float totalNoRefundedPrice = 0;
            //循环未退款
            for (Uniontrade uniontrade : noRefundedTrade) {
                totalNoRefundedPrice = ElongHotelInterfaceUtil.floatAdd(totalNoRefundedPrice, uniontrade.getAmount());
            }
            //可退总金额
            float totalMoney = localUtil.maxFuzzyCanRefund(oldCanRefund, changeFuzzyRefundList, orderTicketList);
            //模糊退处理
            if (totalNoRefundedPrice > 0 && totalNoRefundedPrice <= totalMoney
                    && aliRefundTotal == totalNoRefundedPrice) {
                //标识
                result.put("mohutui", true);
                //未出票
                Map<Long, Trainticket> weiChuPiaoMap = new HashMap<Long, Trainticket>();
                for (Trainticket ticket : weiChuPiaoList) {
                    weiChuPiaoMap.put(ticket.getId(), ticket);
                }
                //回调
                aliRefundTotal = mohutui(order, change, oldCanRefund, aliRefundTotal, ticketRefundTotal,
                        totalNoRefundedPrice, totalMoney, specialFlag, orderTicketList, weiChuPiaoMap, canGoRefundList,
                        random);
                //处理完成
                if (aliRefundTotal <= 0) {
                    result.put("success", true);
                    result.put("result", "处理成功");
                    //更新退款
                    localUtil.updateAliByTrandNum(order.getId(), payTradeNoArray, "-2");
                }
                else {
                    result.put("result", title + "模糊退处理失败");
                }
            }
            else {
                result.put("result", title + "未处理退款与可退金额匹配失败");
            }
        }
        return result;
    }

    /**
     * 退票、改签不明确，模糊退处理
     */
    private JSONObject unknown(JSONObject refundResult, JSONArray unknown, Trainorder order, Trainorderchange change,
            Map<Long, Trainticket> idTicketMap, List<Uniontrade> aliRefundPrice, List<Trainticket> weiChuPiaoList,
            JSONArray passengers, List<Trainticket> orderTicketList, List<FuzzyRefund> changeFuzzyRefundList,
            boolean highChange, int specialFlag, List<Trainticket> canGoRefundList, int random) {
        int totalCount = 0;
        int refundCount = 0;
        //上级参数
        float oldCanRefund = refundResult.getFloatValue("oldCanRefund");
        float aliRefundTotal = refundResult.getFloatValue("aliRefundTotal");
        float ticketRefundTotal = refundResult.getFloatValue("ticketRefundTotal");
        //处理成功
        if (refundResult.getBooleanValue("success") && aliRefundTotal > 0) {
            //时间一样车票
            int departSameTimeCount = 0;
            //车票可退总价
            float ticketCanRefundPrice = 0;
            //出发时间一样
            List<String> departTimeList = new ArrayList<String>();
            //未退车票集合
            List<Trainticket> noRefundedTicket = new ArrayList<Trainticket>();
            //未出票
            Map<Long, Trainticket> weiChuPiaoMap = new HashMap<Long, Trainticket>();
            for (Trainticket ticket : weiChuPiaoList) {
                weiChuPiaoMap.put(ticket.getId(), ticket);
            }
            //循环
            for (int i = 0; i < unknown.size(); i++) {
                JSONObject temp = unknown.getJSONObject(i);
                //车票ID集合
                JSONArray ticketIds = temp.getJSONArray("ticketIds");
                //循环车票ID
                for (int j = 0; j < ticketIds.size(); j++) {
                    totalCount++;
                    //ID
                    long ticketId = ticketIds.getLongValue(j);
                    //车票
                    Trainticket ticket = idTicketMap.get(ticketId);
                    //车票状态
                    int status = ticket.getStatus();
                    int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                    //新票价
                    float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                    //可退金额
                    float tempCanRefund = localUtil.countTicketCanRefundPrice(ticket);
                    //已退、高改
                    if (status == Trainticket.REFUNDED || tempCanRefund == 0
                            || (!highChange && newPrice > ticket.getPrice())) {
                        refundCount++;
                    }
                    //非未出票、包含乘客
                    else if (!weiChuPiaoMap.containsKey(ticketId)) {
                        //乘客姓名
                        String passengerName = ticket.getTrainpassenger().getName();
                        //同程表格乘客
                        if (passengers.size() > 0 && !passengers.contains(passengerName)) {
                            continue;
                        }
                        noRefundedTicket.add(ticket);
                        ticketCanRefundPrice = ElongHotelInterfaceUtil.floatAdd(ticketCanRefundPrice, tempCanRefund);
                        //正常订单、线上改签
                        if (changeType == 0 || changeType == 1 || changeType == 2) {
                            departSameTimeCount++;
                            //发车时间
                            String departTime = changeType == 0 ? ticket.getDeparttime() : ticket.getTtcdeparttime();
                            //不包括时间，添加
                            if (!departTimeList.contains(departTime)) {
                                departTimeList.add(departTime);
                            }
                        }
                    }
                }
            }
            //存在未处理
            if (noRefundedTicket.size() > 0) {
                //未处理退款
                List<Uniontrade> noRefundedTrade = matchUtil.matchTradeByRefundTotal(aliRefundPrice, ticketRefundTotal,
                        false);
                //总退款金额
                float totalNoRefundedPrice = 0;
                //循环未退款
                for (Uniontrade uniontrade : noRefundedTrade) {
                    totalNoRefundedPrice = ElongHotelInterfaceUtil.floatAdd(totalNoRefundedPrice,
                            uniontrade.getAmount());
                }
                //退款个数
                int noRefundedTradeSize = noRefundedTrade.size();
                //未退票数
                int noRefundedTicketSize = noRefundedTicket.size();
                //获取到退款
                if (noRefundedTradeSize > 0 && ticketCanRefundPrice >= totalNoRefundedPrice && totalNoRefundedPrice > 0) {
                    //模糊退
                    boolean mohutui = false;
                    //单个车票
                    if (noRefundedTicketSize == 1) {
                        Trainticket ticket = noRefundedTicket.get(0);
                        //新票价
                        float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                        //车票价
                        float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
                        //可继续退，单笔退款或未改签两笔退款
                        if (ticketPrice >= totalNoRefundedPrice && aliRefundTotal == totalNoRefundedPrice
                                && (noRefundedTradeSize == 1 || (newPrice == 0 && noRefundedTradeSize == 2))) {
                            //判断是退票
                            boolean isRefund = false;
                            //车票已改签、两笔退款
                            if (newPrice > 0 || (newPrice == 0 && noRefundedTradeSize == 2)) {
                                isRefund = true;
                            }
                            //非退票、计算手续费
                            if (!isRefund) {
                                Uniontrade uniontrade = noRefundedTrade.get(0);
                                //取支付宝退款时间
                                String transDate = uniontrade.getOrdertime().toString();
                                String operateTime = uniontrade.getOrdertime().toString();
                                //以支付宝退款时间计算费率
                                float refundRate = matchUtil.matchRefundRate(operateTime, transDate,
                                        ticket.getDeparttime());
                                //退票费
                                float refundFee = matchUtil.countRefundFee(ticketPrice, refundRate);
                                //计算的退款与支付宝退款相等
                                if (ElongHotelInterfaceUtil.floatSubtract(ticketPrice, refundFee) == totalNoRefundedPrice) {
                                    isRefund = true;
                                }
                            }
                            //即时取可退金额
                            float newCanRefund = localUtil.countCanRefundPriceByRebaterecord(order);
                            //交易记录可退>=当前要退
                            if (newCanRefund >= totalNoRefundedPrice && newCanRefund == oldCanRefund
                                    && (isRefund || !highChange)) {
                                //减掉退的
                                oldCanRefund = ElongHotelInterfaceUtil
                                        .floatSubtract(oldCanRefund, totalNoRefundedPrice);
                                //减掉退的
                                aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal,
                                        totalNoRefundedPrice);
                                //加上退的
                                ticketRefundTotal = ElongHotelInterfaceUtil.floatAdd(ticketRefundTotal,
                                        totalNoRefundedPrice);
                                //以退票回调
                                if (isRefund) {
                                    //退成功+1
                                    refundCount++;
                                    //申请退票
                                    refundCallBackUtil.requestRefund(ticket);
                                    //手续费
                                    float refundFee = ElongHotelInterfaceUtil.floatSubtract(ticketPrice,
                                            totalNoRefundedPrice);
                                    //退票回调
                                    refundCallBackUtil.refundCallBack(order, ticket, refundFee);
                                }
                                //以改签回调
                                else if (!highChange) {
                                    //退成功+1
                                    refundCount++;
                                    //改签回调
                                    refundCallBackUtil.changeCallBack(order, ticket, totalNoRefundedPrice, specialFlag,
                                            random);
                                }
                            }
                        }
                    }
                    //多个车票，出发时间且以支付宝退款时间的费率一致时处理
                    else if (departSameTimeCount == noRefundedTicketSize && departTimeList.size() == 1) {
                        String departTime = departTimeList.get(0);
                        List<Float> refundRateList = new ArrayList<Float>();
                        //取费率
                        for (Uniontrade uniontrade : noRefundedTrade) {
                            //取支付宝退款时间
                            String transDate = uniontrade.getOrdertime().toString();
                            String operateTime = uniontrade.getOrdertime().toString();
                            //以支付宝退款时间计算费率
                            float refundRate = matchUtil.matchRefundRate(operateTime, transDate, departTime);
                            //不包含
                            if (!refundRateList.contains(refundRate)) {
                                refundRateList.add(refundRate);
                            }
                        }
                        if (refundRateList.size() == 1) {
                            boolean existsError = false;
                            float totalNoMatchRefund = 0;
                            float totalNoMatchTicketPrice = 0;
                            float refundRate = refundRateList.get(0);
                            List<Trainticket> noMatchedList = new ArrayList<Trainticket>();
                            //循环车票
                            for (Trainticket ticket : noRefundedTicket) {
                                //新票价
                                float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                                //车票价
                                float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
                                //退票费
                                float refundFee = matchUtil.countRefundFee(ticketPrice, refundRate);
                                //退票金额
                                float currentRefundPrice = ElongHotelInterfaceUtil
                                        .floatSubtract(ticketPrice, refundFee);
                                //支付宝可退<当前要退
                                if (aliRefundTotal < currentRefundPrice) {
                                    existsError = true;
                                    break;
                                }
                                //票价>=退票费
                                if (refundFee >= 0 && ticketPrice >= refundFee) {
                                    //退票费
                                    ticket.setProcedure(refundFee);
                                    //ADD
                                    noMatchedList.add(ticket);
                                    //退款和
                                    totalNoMatchRefund = ElongHotelInterfaceUtil.floatAdd(totalNoMatchRefund,
                                            currentRefundPrice);
                                    //票价和
                                    totalNoMatchTicketPrice = ElongHotelInterfaceUtil.floatAdd(totalNoMatchTicketPrice,
                                            ticketPrice);
                                }
                                else {
                                    existsError = true;
                                    break;
                                }
                            }
                            //无错，从剩余支付宝退款取
                            if (!existsError && noMatchedList.size() > 0) {
                                //全退
                                boolean allRefund = totalNoRefundedPrice == totalNoMatchTicketPrice;
                                //未处理的退款和
                                List<Uniontrade> NoMatchList = matchUtil.matchTradeByRefundTotal(noRefundedTrade,
                                        totalNoMatchRefund, true);
                                //匹配上退款和，继续退票处理
                                if (NoMatchList.size() > 0 || allRefund) {
                                    //循环
                                    for (Trainticket ticket : noMatchedList) {
                                        //申请退票
                                        refundCallBackUtil.requestRefund(ticket);
                                        //退票费
                                        float refundFee = allRefund ? 0 : ticket.getProcedure();
                                        //新票价
                                        float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                                        //车票价
                                        float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
                                        //退票金额
                                        float currentRefundPrice = ElongHotelInterfaceUtil.floatSubtract(ticketPrice,
                                                refundFee);
                                        //支付宝可退<当前要退
                                        if (aliRefundTotal < currentRefundPrice) {
                                            break;
                                        }
                                        //即时取可退金额
                                        float newCanRefund = localUtil.countCanRefundPriceByRebaterecord(order);
                                        //交易记录可退>=当前要退
                                        if (newCanRefund >= currentRefundPrice && newCanRefund == oldCanRefund) {
                                            //减掉退的
                                            oldCanRefund = ElongHotelInterfaceUtil.floatSubtract(oldCanRefund,
                                                    currentRefundPrice);
                                            aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal,
                                                    currentRefundPrice);
                                            ticketRefundTotal = ElongHotelInterfaceUtil.floatAdd(ticketRefundTotal,
                                                    currentRefundPrice);
                                            //更新、回调
                                            refundCallBackUtil.refundCallBack(order, ticket, refundFee);
                                            //退成功+1
                                            refundCount++;
                                        }
                                        else {
                                            break;
                                        }
                                    }
                                }
                                else {
                                    mohutui = true;
                                }
                            }
                            else {
                                mohutui = true;
                            }
                        }
                        else {
                            mohutui = true;
                        }
                    }
                    else {
                        mohutui = true;
                    }
                    //订单总价
                    float totalMoney = localUtil
                            .maxFuzzyCanRefund(oldCanRefund, changeFuzzyRefundList, orderTicketList);
                    //模糊退处理
                    if (mohutui && totalNoRefundedPrice > 0 && totalNoRefundedPrice <= totalMoney
                            && aliRefundTotal == totalNoRefundedPrice
                            && (specialFlag == 1 || refundCallBackUtil.canFuzzyRefund(order.getAgentid()))) {
                        refundResult.put("mohutui", true);
                        aliRefundTotal = mohutui(order, change, oldCanRefund, aliRefundTotal, ticketRefundTotal,
                                totalNoRefundedPrice, totalMoney, specialFlag, orderTicketList, weiChuPiaoMap,
                                canGoRefundList, random);
                    }
                }
            }
        }
        refundResult.put("totalCount", totalCount);
        refundResult.put("refundCount", refundCount);
        refundResult.put("oldCanRefund", oldCanRefund);
        refundResult.put("aliRefundTotal", aliRefundTotal);
        refundResult.put("success", refundCount == totalCount);
        refundResult.put("ticketRefundTotal", ticketRefundTotal);
        return refundResult;
    }

    /**
     * 模糊退处理
     * @param totalMoney 订单总价
     * @param oldCanRefund 原可退金额
     * @param aliRefundTotal 支付宝退款金额
     * @param ticketRefundTotal 车票已退金额
     * @param totalNoRefundedPrice 未退金额
     * @param canGoRefundList 模糊退可按退票处理的车票
     */
    private float mohutui(Trainorder order, Trainorderchange change, float oldCanRefund, float aliRefundTotal,
            float ticketRefundTotal, float totalNoRefundedPrice, float totalMoney, int specialFlag,
            List<Trainticket> orderTicketList, Map<Long, Trainticket> weiChuPiaoMap, List<Trainticket> canGoRefundList,
            int random) {
        //改签ID
        long changeId = change.getId();
        //即时取可退金额
        float defaultCanRefund = localUtil.countCanRefundPriceByRebaterecord(order);
        //交易记录可退>=当前要退
        if (defaultCanRefund >= totalNoRefundedPrice && defaultCanRefund == oldCanRefund) {
            //可模糊退
            if (refundCallBackUtil.canFuzzyRefund(order.getAgentid())) {
                //创建记录
                FuzzyRefund fuzzy = new FuzzyRefund();
                //记录赋值
                fuzzy.setRemark("系统");
                fuzzy.setTimeStamp("");
                fuzzy.setChangeId(changeId);
                fuzzy.setOrderId(order.getId());
                fuzzy.setTotalMoney(totalMoney);
                fuzzy.setMoney(totalNoRefundedPrice);
                fuzzy.setStatus(FuzzyRefund.WAITREFUND);
                fuzzy.setOrderNumber(order.getOrdernumber());
                fuzzy.setOrderPayMethod(order.getPaymethod());
                fuzzy.setOrderTradeNum(order.getSupplytradeno());
                fuzzy.setOrderInterfaceType(order.getInterfacetype());
                fuzzy.setRefundPriceQuestion(FuzzyRefund.REFUNDNORMAL);
                //改签交易号
                fuzzy.setChangeTradeNum(ElongHotelInterfaceUtil.StringIsNull(change.getSupplytradeno()) ? "" : change
                        .getSupplytradeno());
                //改签特征值
                fuzzy.setChangeRequestReqtoken(!ElongHotelInterfaceUtil.StringIsNull(change.getRequestReqtoken()) ? change
                        .getRequestReqtoken() : "");
                //保存记录
                fuzzy = Server.getInstance().getTrainService().createFuzzyRefund(fuzzy);
                //保存成功
                if (fuzzy != null && fuzzy.getId() > 0) {
                    //减掉退的
                    oldCanRefund = ElongHotelInterfaceUtil.floatSubtract(oldCanRefund, totalNoRefundedPrice);
                    //减掉退的
                    aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, totalNoRefundedPrice);
                    //加上退的
                    ticketRefundTotal = ElongHotelInterfaceUtil.floatAdd(ticketRefundTotal, totalNoRefundedPrice);
                    //更新、回调
                    refundCallBackUtil.fuzzyCallBack(order, change, fuzzy, totalNoRefundedPrice);
                }
            }
            //取第一个接口申请线下退票，变问题退票
            else if (specialFlag == 1) {
                //循环
                for (Trainticket ticket : orderTicketList) {
                    //接口申请、非未出票、状态正确
                    if (ticket.getApplyTicketFlag() == 1 && !weiChuPiaoMap.containsKey(ticket.getId())
                            && ticket.getStatus() == localUtil.interfaceApplyTicketStatus(ticket)) {
                        //退票问题
                        localUtil.interfaceApplyTicketQuestion(ticket);
                        //中断循环
                        break;
                    }
                }
            }
            //不支持模糊退>>非淘宝，按退票处理
            else if (order.getInterfacetype() != null && order.getInterfacetype() > 0 && order.getInterfacetype() != 6) {
                //循环>>按全退处理
                for (int i = 0; i < canGoRefundList.size(); i++) {
                    //车票
                    Trainticket ticket = canGoRefundList.get(i);
                    //新票价
                    float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                    //车票价
                    float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
                    //改签类型
                    int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                    //退票金额
                    float currentRefundPrice = ticketPrice < totalNoRefundedPrice ? ticketPrice : totalNoRefundedPrice;
                    //全部退完
                    if (currentRefundPrice <= 0) {
                        break;
                    }
                    //即时取可退金额
                    float newCanRefund = i == 0 ? defaultCanRefund : localUtil.countCanRefundPriceByRebaterecord(order);
                    //交易记录可退>=当前要退
                    if (newCanRefund >= currentRefundPrice && newCanRefund == oldCanRefund) {
                        //记日志
                        boolean writeLog = true;
                        //减掉退的
                        totalNoRefundedPrice = ElongHotelInterfaceUtil.floatSubtract(totalNoRefundedPrice,
                                currentRefundPrice);
                        oldCanRefund = ElongHotelInterfaceUtil.floatSubtract(oldCanRefund, currentRefundPrice);
                        aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, currentRefundPrice);
                        ticketRefundTotal = ElongHotelInterfaceUtil.floatAdd(ticketRefundTotal, currentRefundPrice);
                        //改签处理
                        if (changeType == 0) {
                            //更新、回调
                            if (refundCallBackUtil.changeCallBack(order, ticket, currentRefundPrice, specialFlag,
                                    random) == 1) {
                                writeLog = false;
                            }
                        }
                        //退票处理
                        else {
                            //申请退票
                            refundCallBackUtil.requestRefund(ticket);
                            //更新、回调
                            refundCallBackUtil.refundCallBack(order, ticket,
                                    ElongHotelInterfaceUtil.floatSubtract(ticketPrice, currentRefundPrice));
                        }
                        //记录日志
                        if (writeLog) {
                            WriteLog.write("h火车票支付宝退款_特殊", random + ":interfaceType:" + order.getInterfacetype()
                                    + ":orderId:" + order.getId() + ":ticketId:" + ticket.getId() + ":refundPrice:"
                                    + currentRefundPrice);
                        }
                    }
                    else {
                        break;
                    }
                }
            }
        }
        return aliRefundTotal;
    }

    /**
     * 仅明确改签
     */
    private JSONObject gaiqian(JSONObject refundResult, JSONArray gaiqian, Trainorder order,
            Map<Long, Trainticket> idTicketMap, int specialFlag, int random) {
        int totalCount = 0;
        int refundCount = 0;
        //存在错误
        boolean existsErrorStatus = false;
        //未退车票总价
        float noRefundedTicketTotalPrice = 0;
        //未退车票集合
        List<Trainticket> noRefundedTicket = new ArrayList<Trainticket>();
        //上级参数
        float aliRefundTotal = refundResult.getFloatValue("aliRefundTotal");
        //退票退款处理失败，更新状态为申请改签
        if (!refundResult.getBooleanValue("success") || aliRefundTotal <= 0) {
            existsErrorStatus = true;
        }
        else {
            //循环改签
            out: for (int i = 0; i < gaiqian.size(); i++) {
                JSONObject temp = gaiqian.getJSONObject(i);
                //车票ID集合
                JSONArray ticketIds = temp.getJSONArray("ticketIds");
                //循环车票ID
                for (int j = 0; j < ticketIds.size(); j++) {
                    totalCount++;
                    //ID
                    long ticketId = ticketIds.getLongValue(j);
                    //车票
                    Trainticket ticket = idTicketMap.get(ticketId);
                    //车票状态
                    int status = ticket.getStatus();
                    int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                    //新票价
                    float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                    //已线下改签
                    if (changeType == Trainticket.REFUNDED) {
                        refundCount++;
                    }
                    //已出票、无法退票
                    else if ((status == Trainticket.ISSUED || status == Trainticket.NONREFUNDABLE) && changeType == 0
                            && newPrice == 0) {
                        noRefundedTicket.add(ticket);
                        noRefundedTicketTotalPrice = ElongHotelInterfaceUtil.floatAdd(noRefundedTicketTotalPrice,
                                ticket.getPrice());
                    }
                    //非已线下改签
                    else {
                        existsErrorStatus = true;
                        break out;
                    }
                }
            }
        }
        //支付宝退款>合计票价
        if (aliRefundTotal > noRefundedTicketTotalPrice || noRefundedTicket.size() == 0) {
            existsErrorStatus = true;
        }
        //存在错误
        if (existsErrorStatus) {
            //存在退款
            if (aliRefundTotal > 0) {
                //设为申请
                out: for (int i = 0; i < gaiqian.size(); i++) {
                    JSONObject temp = gaiqian.getJSONObject(i);
                    //车票ID集合
                    JSONArray ticketIds = temp.getJSONArray("ticketIds");
                    //循环车票ID
                    for (int j = 0; j < ticketIds.size(); j++) {
                        //设为改签成功
                        if (refundCallBackUtil.requestChange(idTicketMap.get(ticketIds.getLongValue(j)))) {
                            break out;
                        }
                    }
                }
                if (totalCount > 0) {
                    //RESET
                    boolean success = refundCount == totalCount;
                    //RESET
                    refundResult.put("success", success);//重新设置success值
                    //一个车票已处理过改签
                    refundResult.put("oneRefund", success && refundCount == 1 ? true : false);
                }
            }
            //重新设置success值
            else {
                refundResult.put("success", false);
            }
            refundResult.put("totalCount", totalCount);
            refundResult.put("refundCount", refundCount);
            return refundResult;
        }
        //设置虚拟的明确改签、退款
        else {
            //重置
            idTicketMap = new HashMap<Long, Trainticket>();
            //虚拟
            JSONArray matchArray = new JSONArray();
            JSONArray gaiqianAndtuikuan = new JSONArray();
            //循环
            for (Trainticket ticket : noRefundedTicket) {
                //NEW
                matchArray.add(ticket.getId());
                //PUT
                idTicketMap.put(ticket.getId(), ticket);
            }
            JSONObject matchObject = new JSONObject();
            matchObject.put("ticketIds", matchArray);
            matchObject.put("refundPrice", aliRefundTotal);
            gaiqianAndtuikuan.add(matchObject);
            //处理
            return gaiqianAndtuikuan(refundResult, gaiqianAndtuikuan, order, idTicketMap, specialFlag, random);
        }
    }

    /**
     * 仅明确退票
     */
    private JSONObject tuipiao(JSONObject refundResult, JSONArray tuipiao, Trainorder order,
            Map<Long, Trainticket> idTicketMap, List<Uniontrade> aliRefundPrice, boolean highChange) {
        int totalCount = 0;
        int refundCount = 0;
        //上级参数
        float oldCanRefund = refundResult.getFloatValue("oldCanRefund");
        float aliRefundTotal = refundResult.getFloatValue("aliRefundTotal");
        float ticketRefundTotal = refundResult.getFloatValue("ticketRefundTotal");
        //未处理的退款
        List<Uniontrade> noRefundedTrade = new ArrayList<Uniontrade>();
        //获取未处理的退款
        if (refundResult.getBooleanValue("success") && tuipiao.size() > 0 && aliRefundTotal > 0) {
            noRefundedTrade = matchUtil.matchTradeByRefundTotal(aliRefundPrice, ticketRefundTotal, false);
        }
        if (noRefundedTrade.size() <= 0) {
            //设为申请
            for (int i = 0; i < tuipiao.size(); i++) {
                JSONObject temp = tuipiao.getJSONObject(i);
                //车票ID集合
                JSONArray ticketIds = temp.getJSONArray("ticketIds");
                //循环车票ID
                for (int j = 0; j < ticketIds.size(); j++) {
                    totalCount++;
                    //ID
                    long ticketId = ticketIds.getLongValue(j);
                    //车票
                    Trainticket ticket = idTicketMap.get(ticketId);
                    //已退
                    if (ticket.getStatus() == Trainticket.REFUNDED) {
                        refundCount++;
                    }
                    else if (aliRefundTotal > 0) {
                        refundCallBackUtil.requestRefund(ticket);
                    }
                }
            }
            if (tuipiao.size() > 0) {
                refundResult.put("success", false);
            }
            refundResult.put("totalCount", totalCount);
            refundResult.put("refundCount", refundCount);
            return refundResult;
        }
        //错误
        boolean existsError = false;
        //未匹配上退票退款
        float totalNoMatchRefund = 0;
        float totalNoMatchTicketPrice = 0;
        List<Trainticket> noMatchedList = new ArrayList<Trainticket>();
        //循环
        for (int i = 0; i < tuipiao.size(); i++) {
            JSONObject temp = tuipiao.getJSONObject(i);
            String operateTime = temp.getString("operateTime");//订单明细中时间
            JSONArray ticketIds = temp.getJSONArray("ticketIds");//车票ID集合
            //车票ID
            for (int j = 0; j < ticketIds.size(); j++) {
                totalCount++;
                //ID
                long ticketId = ticketIds.getLongValue(j);
                //车票
                Trainticket ticket = idTicketMap.get(ticketId);
                //已退
                if (ticket.getStatus() == Trainticket.REFUNDED) {
                    refundCount++;
                    continue;
                }
                //申请退票
                refundCallBackUtil.requestRefund(ticket);
                //存在错误
                if (existsError) {
                    continue;
                }
                //改签
                int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                //新票价
                float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                //车票价
                float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
                //发车时间
                String departTime = newPrice > 0 ? ticket.getTtcdeparttime() : ticket.getDeparttime();
                //无时间、线下改签
                if (ElongHotelInterfaceUtil.StringIsNull(operateTime) || changeType == Trainticket.REFUNDED) {
                    continue;
                }
                //订单退款查出来的高改，认为退成功，后续再处理
                if (!highChange && newPrice > ticket.getPrice()) {
                    //退成功+1
                    refundCount++;
                    continue;
                }
                //费率
                float refundRate = matchUtil.matchRefundRate(operateTime, "", departTime);
                //退票费
                float refundFee = matchUtil.countRefundFee(ticketPrice, refundRate);
                //退票金额
                float currentRefundPrice = ElongHotelInterfaceUtil.floatSubtract(ticketPrice, refundFee);
                //支付宝可退<当前要退
                if (aliRefundTotal < currentRefundPrice) {
                    existsError = true;
                    continue;
                }
                //票价>=退票费
                if (refundFee >= 0 && ticketPrice >= refundFee) {
                    Uniontrade trade = matchUtil.matchUniontradeByRefundPrice(noRefundedTrade, currentRefundPrice);
                    //匹配上支付宝退款
                    if (trade != null && trade.getAmount() != null && trade.getAmount() == currentRefundPrice) {
                        noRefundedTrade.remove(trade);
                        //即时取可退金额
                        float newCanRefund = localUtil.countCanRefundPriceByRebaterecord(order);
                        //交易记录可退>=当前要退
                        if (newCanRefund >= currentRefundPrice && newCanRefund == oldCanRefund) {
                            //减掉退的
                            oldCanRefund = ElongHotelInterfaceUtil.floatSubtract(oldCanRefund, currentRefundPrice);
                            aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, currentRefundPrice);
                            ticketRefundTotal = ElongHotelInterfaceUtil.floatAdd(ticketRefundTotal, currentRefundPrice);
                            //更新、回调
                            refundCallBackUtil.refundCallBack(order, ticket, refundFee);
                            //退成功+1
                            refundCount++;
                        }
                        else {
                            existsError = true;
                        }
                    }
                    //未匹配上支付宝退款
                    else {
                        //退票费
                        ticket.setProcedure(refundFee);
                        //ADD
                        noMatchedList.add(ticket);
                        //退款和
                        totalNoMatchRefund = ElongHotelInterfaceUtil.floatAdd(totalNoMatchRefund, currentRefundPrice);
                        //票价和
                        totalNoMatchTicketPrice = ElongHotelInterfaceUtil
                                .floatAdd(totalNoMatchTicketPrice, ticketPrice);
                    }
                }
            }
        }
        //无错，从剩余支付宝退款取
        if (!existsError && noMatchedList.size() > 0) {
            //总退款金额
            float totalNoRefundedPrice = 0;
            //循环未退款
            for (Uniontrade uniontrade : noRefundedTrade) {
                totalNoRefundedPrice = ElongHotelInterfaceUtil.floatAdd(totalNoRefundedPrice, uniontrade.getAmount());
            }
            //全退
            boolean allRefund = totalNoRefundedPrice == totalNoMatchTicketPrice;
            //未处理的退款和
            List<Uniontrade> NoMatchList = matchUtil.matchTradeByRefundTotal(noRefundedTrade, totalNoMatchRefund, true);
            //匹配上退款和，继续退票处理
            if (NoMatchList.size() > 0 || allRefund) {
                //循环
                for (Trainticket ticket : noMatchedList) {
                    //退票费
                    float refundFee = allRefund ? 0 : ticket.getProcedure();
                    //新票价
                    float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                    //车票价
                    float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
                    //退票金额
                    float currentRefundPrice = ElongHotelInterfaceUtil.floatSubtract(ticketPrice, refundFee);
                    //支付宝可退<当前要退
                    if (aliRefundTotal < currentRefundPrice) {
                        break;
                    }
                    //即时取可退金额
                    float newCanRefund = localUtil.countCanRefundPriceByRebaterecord(order);
                    //交易记录可退>=当前要退
                    if (newCanRefund >= currentRefundPrice && newCanRefund == oldCanRefund) {
                        //减掉退的
                        oldCanRefund = ElongHotelInterfaceUtil.floatSubtract(oldCanRefund, currentRefundPrice);
                        aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, currentRefundPrice);
                        ticketRefundTotal = ElongHotelInterfaceUtil.floatAdd(ticketRefundTotal, currentRefundPrice);
                        //更新、回调
                        refundCallBackUtil.refundCallBack(order, ticket, refundFee);
                        //退成功+1
                        refundCount++;
                    }
                    else {
                        break;
                    }
                }
            }
        }
        refundResult.put("totalCount", totalCount);
        refundResult.put("refundCount", refundCount);
        refundResult.put("oldCanRefund", oldCanRefund);
        refundResult.put("aliRefundTotal", aliRefundTotal);
        refundResult.put("success", refundCount == totalCount);
        refundResult.put("ticketRefundTotal", ticketRefundTotal);
        //RETURN
        return refundResult;
    }

    /**
     * 明确改签、退款
     */
    private JSONObject gaiqianAndtuikuan(JSONObject refundResult, JSONArray gaiqianAndtuikuan, Trainorder order,
            Map<Long, Trainticket> idTicketMap, int specialFlag, int random) {
        int totalCount = 0;
        int refundCount = 0;
        //金额
        float oldCanRefund = refundResult.getFloatValue("oldCanRefund");
        float aliRefundTotal = refundResult.getFloatValue("aliRefundTotal");
        float ticketRefundTotal = refundResult.getFloatValue("ticketRefundTotal");
        //退票退款处理失败 或 已退完
        if (!refundResult.getBooleanValue("success") || aliRefundTotal <= 0) {
            for (int i = 0; i < gaiqianAndtuikuan.size(); i++) {
                JSONObject temp = gaiqianAndtuikuan.getJSONObject(i);
                //车票ID集合
                JSONArray ticketIds = temp.getJSONArray("ticketIds");
                //循环车票ID
                for (int j = 0; j < ticketIds.size(); j++) {
                    totalCount++;
                    //ID
                    long ticketId = ticketIds.getLongValue(j);
                    //车票
                    Trainticket ticket = idTicketMap.get(ticketId);
                    //车票状态
                    int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                    //已线下改签
                    if (changeType == Trainticket.REFUNDED) {
                        refundCount++;
                    }
                    //有可退金额
                    else if (aliRefundTotal > 0) {
                        refundCallBackUtil.requestChange(idTicketMap.get(ticketIds.getLongValue(j)));
                    }
                }
            }
            refundResult.put("totalCount", totalCount);
            refundResult.put("refundCount", refundCount);
            return refundResult;
        }
        //循环
        out: for (int i = 0; i < gaiqianAndtuikuan.size(); i++) {
            JSONObject temp = gaiqianAndtuikuan.getJSONObject(i);
            float refundPrice = temp.getFloatValue("refundPrice");//退款金额
            JSONArray ticketIds = temp.getJSONArray("ticketIds");//车票ID集合
            //已退金额
            float refundedPrice = 0;
            //存在退票、线上改签
            int existsErrorStatus = 0;
            //未退车票
            List<Trainticket> noRefundedTicket = new ArrayList<Trainticket>();
            //循环车票ID
            for (int j = 0; j < ticketIds.size(); j++) {
                //ID
                long ticketId = ticketIds.getLongValue(j);
                //车票
                Trainticket ticket = idTicketMap.get(ticketId);
                //车票状态
                int status = ticket.getStatus();
                int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                //新票价
                float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                //线下改签退款
                float changeReturn = ticket.getTcProcedure() == null ? 0 : ticket.getTcProcedure();
                //已线下改签
                if (changeType == Trainticket.REFUNDED) {
                    refundedPrice = ElongHotelInterfaceUtil.floatAdd(refundedPrice, changeReturn);
                }
                //已出票、无法退票
                else if ((status == Trainticket.ISSUED || status == Trainticket.NONREFUNDABLE) && changeType == 0
                        && newPrice == 0) {
                    totalCount++;
                    noRefundedTicket.add(ticket);
                }
                else {
                    totalCount++;
                    existsErrorStatus++;
                }
            }
            //未退
            int noRefundedTicketSize = noRefundedTicket.size();
            //全退
            if (noRefundedTicketSize == 0) {
                continue;
            }
            //票价一致
            float ticketPrice = 0;
            float totalTicketPrice = 0;
            Map<Float, Boolean> samePrice = new HashMap<Float, Boolean>();
            //循环PUT
            for (Trainticket ticket : noRefundedTicket) {
                ticketPrice = ticket.getPrice();
                samePrice.put(ticketPrice, true);
                totalTicketPrice = ElongHotelInterfaceUtil.floatAdd(totalTicketPrice, ticketPrice);
            }
            //还可退款
            refundPrice = ElongHotelInterfaceUtil.floatSubtract(refundPrice, refundedPrice);
            //全退
            boolean allRefund = totalTicketPrice == refundPrice && existsErrorStatus == 0;
            //平均退价
            float currentRefundPrice = floatDivide(refundPrice, noRefundedTicketSize, 2);
            //临时字符
            String tempPrice = String.valueOf(currentRefundPrice);
            //价格正确
            boolean priceIsRight = !tempPrice.contains(".");//无小数
            //判断小数
            if (!priceIsRight) {
                String xiaoshu = "0" + tempPrice.substring(tempPrice.indexOf("."));
                priceIsRight = Float.valueOf(xiaoshu) == 0 || Float.valueOf(xiaoshu) == 0.5;
            }
            //平均退价之和
            float totalRefundPrice = ElongHotelInterfaceUtil.floatMultiply(currentRefundPrice, noRefundedTicketSize);
            //平均状态正确、全退
            if (allRefund
                    || (refundPrice > 0 && samePrice.size() == 1 && totalRefundPrice == refundPrice
                            && ticketPrice >= currentRefundPrice && existsErrorStatus == 0 && priceIsRight)) {
                for (Trainticket ticket : noRefundedTicket) {
                    //全退退款金额取票价
                    currentRefundPrice = allRefund ? ticket.getPrice() : currentRefundPrice;
                    //支付宝可退<当前要退
                    if (aliRefundTotal < currentRefundPrice) {
                        break out;
                    }
                    //即时取可退金额
                    float newCanRefund = localUtil.countCanRefundPriceByRebaterecord(order);
                    //交易记录可退>=当前要退
                    if (newCanRefund >= currentRefundPrice && newCanRefund == oldCanRefund) {
                        //减掉退的
                        oldCanRefund = ElongHotelInterfaceUtil.floatSubtract(oldCanRefund, currentRefundPrice);
                        aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, currentRefundPrice);
                        ticketRefundTotal = ElongHotelInterfaceUtil.floatAdd(ticketRefundTotal, currentRefundPrice);
                        //更新、回调
                        refundCallBackUtil.changeCallBack(order, ticket, currentRefundPrice, specialFlag, random);
                        //退成功+1
                        refundCount++;
                    }
                    else {
                        break out;
                    }
                }
            }
            else {
                for (Trainticket ticket : noRefundedTicket) {
                    refundCallBackUtil.requestChange(ticket);
                }
            }
        }
        refundResult.put("totalCount", totalCount);
        refundResult.put("refundCount", refundCount);
        refundResult.put("oldCanRefund", oldCanRefund);
        refundResult.put("aliRefundTotal", aliRefundTotal);
        refundResult.put("success", refundCount == totalCount);
        refundResult.put("ticketRefundTotal", ticketRefundTotal);
        //RETURN
        return refundResult;
    }

    /**
     * 明确退票、退款
     * @param oldCanRefund 原可退金额
     */
    private JSONObject tuipiaoAndtuikuan(JSONObject refundResult, JSONArray tuipiaoAndtuikuan, Trainorder order,
            Map<Long, Trainticket> idTicketMap, boolean highChange) {
        int totalCount = 0;
        int refundCount = 0;
        float oldCanRefund = refundResult.getFloatValue("oldCanRefund");
        float aliRefundTotal = refundResult.getFloatValue("aliRefundTotal");
        float ticketRefundTotal = refundResult.getFloatValue("ticketRefundTotal");
        //循环
        out: for (int i = 0; i < tuipiaoAndtuikuan.size(); i++) {
            JSONObject temp = tuipiaoAndtuikuan.getJSONObject(i);
            String transDate = temp.getString("transDate");//退款明细中时间
            float refundPrice = temp.getFloatValue("refundPrice");//退款金额
            String operateTime = temp.getString("operateTime");//订单明细中时间
            JSONArray ticketIds = temp.getJSONArray("ticketIds");//车票ID集合
            //已退金额
            float refundedPrice = 0;
            //线下改签
            int existsOfflineChange = 0;
            //未退车票
            List<Trainticket> noRefundedTicket = new ArrayList<Trainticket>();
            //车票ID
            for (int j = 0; j < ticketIds.size(); j++) {
                //ID
                long ticketId = ticketIds.getLongValue(j);
                //车票
                Trainticket ticket = idTicketMap.get(ticketId);
                //新价
                Float newPrice = ticket.getTcnewprice();
                //改签
                int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                //票价
                float ticketPrice = newPrice != null && newPrice > 0 ? newPrice : ticket.getPrice();
                //已退
                if (ticket.getStatus() == Trainticket.REFUNDED) {
                    refundedPrice = ElongHotelInterfaceUtil.floatAdd(refundedPrice,
                            ElongHotelInterfaceUtil.floatSubtract(ticketPrice, ticket.getProcedure().floatValue()));
                }
                //未退
                else {
                    totalCount++;
                    //未退车票
                    noRefundedTicket.add(ticket);
                    //存在线下改签
                    if (changeType == Trainticket.REFUNDED) {
                        existsOfflineChange++;
                    }
                }
            }
            //全退
            if (noRefundedTicket.size() == 0) {
                continue;
            }
            //申请退票
            for (Trainticket ticket : noRefundedTicket) {
                refundCallBackUtil.requestRefund(ticket);
            }
            //还可退款
            refundPrice = ElongHotelInterfaceUtil.floatSubtract(refundPrice, refundedPrice);
            //计算退票费
            if (refundPrice > 0 && existsOfflineChange == 0) {
                boolean allMatched = true;//全匹配
                float totalTicketPrice = 0;//所有要退票票价金额
                float totalCountRefundPrice = 0;//计算出来的退票金额
                Map<Long, Float> refundFeeMap = new HashMap<Long, Float>();
                Map<Long, Float> refundPriceMap = new HashMap<Long, Float>();
                //循环
                for (Trainticket ticket : noRefundedTicket) {
                    //新票价
                    float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                    //车票价
                    float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
                    //所有票价
                    totalTicketPrice = ElongHotelInterfaceUtil.floatAdd(totalTicketPrice, ticketPrice);
                    //发车时间
                    String departTime = newPrice > 0 ? ticket.getTtcdeparttime() : ticket.getDeparttime();
                    //费率
                    float refundRate = matchUtil.matchRefundRate(operateTime, transDate, departTime);
                    //退票费
                    float refundFee = matchUtil.countRefundFee(ticketPrice, refundRate);
                    //票价>=退票费
                    if (refundFee >= 0 && ticketPrice >= refundFee) {
                        //设置值
                        refundFeeMap.put(ticket.getId(), refundFee);
                        //退票金额
                        refundFee = ElongHotelInterfaceUtil.floatSubtract(ticketPrice, refundFee);
                        //设置值
                        refundPriceMap.put(ticket.getId(), refundFee);
                        //累计金额
                        totalCountRefundPrice = ElongHotelInterfaceUtil.floatAdd(totalCountRefundPrice, refundFee);
                    }
                    else {
                        allMatched = false;
                    }
                }
                //已匹配
                if (allMatched) {
                    allMatched = totalCountRefundPrice == refundPrice;
                }
                //未匹配
                if (!allMatched && (totalTicketPrice == refundPrice || noRefundedTicket.size() == 1)) {
                    //重置
                    refundFeeMap = new HashMap<Long, Float>();
                    refundPriceMap = new HashMap<Long, Float>();
                    //全退
                    boolean allRefund = totalTicketPrice == refundPrice;
                    //循环
                    for (Trainticket ticket : noRefundedTicket) {
                        //新票价
                        float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                        //车票价
                        float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
                        //退票费，全退为0，单张为票价-退款
                        float refundFee = allRefund ? 0 : ElongHotelInterfaceUtil.floatSubtract(ticketPrice,
                                refundPrice);
                        //可继续
                        if (refundFee >= 0) {
                            allMatched = true;
                            refundFeeMap.put(ticket.getId(), refundFee);
                            refundPriceMap.put(ticket.getId(), allRefund ? ticketPrice : refundPrice);
                        }
                    }
                }
                if (allMatched) {
                    for (Trainticket ticket : noRefundedTicket) {
                        //新票价
                        float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                        //订单查出来的高改，认为退成功，后续再处理
                        if (!highChange && newPrice > ticket.getPrice()) {
                            //退成功+1
                            refundCount++;
                            continue;
                        }
                        //当前退款
                        float currentRefundPrice = refundPriceMap.get(ticket.getId());
                        //支付宝可退<当前要退
                        if (aliRefundTotal < currentRefundPrice) {
                            break out;
                        }
                        //即时取可退金额
                        float newCanRefund = localUtil.countCanRefundPriceByRebaterecord(order);
                        //交易记录可退>=当前要退
                        if (newCanRefund >= currentRefundPrice && newCanRefund == oldCanRefund) {
                            //减掉退的
                            oldCanRefund = ElongHotelInterfaceUtil.floatSubtract(oldCanRefund, currentRefundPrice);
                            aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, currentRefundPrice);
                            ticketRefundTotal = ElongHotelInterfaceUtil.floatAdd(ticketRefundTotal, currentRefundPrice);
                            //更新、回调
                            refundCallBackUtil.refundCallBack(order, ticket, refundFeeMap.get(ticket.getId()));
                            //退成功+1
                            refundCount++;
                        }
                        else {
                            break out;
                        }
                    }
                }
            }
        }
        refundResult.put("totalCount", totalCount);
        refundResult.put("refundCount", refundCount);
        refundResult.put("oldCanRefund", oldCanRefund);
        refundResult.put("aliRefundTotal", aliRefundTotal);
        refundResult.put("success", refundCount == totalCount);
        refundResult.put("ticketRefundTotal", ticketRefundTotal);
        //RETURN
        return refundResult;
    }

    /**
     * float除法
     * @len 小数位数
     */
    private float floatDivide(float a, float b, int len) {
        BigDecimal x = new BigDecimal(a + "");
        BigDecimal y = new BigDecimal(b + "");
        return x.divide(y, len, BigDecimal.ROUND_FLOOR).floatValue();
    }
}