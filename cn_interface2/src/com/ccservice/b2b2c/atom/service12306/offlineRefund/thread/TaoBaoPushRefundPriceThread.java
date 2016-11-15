package com.ccservice.b2b2c.atom.service12306.offlineRefund.thread;

import java.util.Map;
import java.util.List;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.FuzzyRefund;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.uniontrade.Uniontrade;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.atom.train.TrainVmoneyRecord;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.LocalRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.MatchTicketUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.AliPayRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.util.RefundTicketDataUtil;

/**
 * 淘宝推送退款处理
 * @author WH
 * @time 2015年9月1日 上午11:33:17
 * @version 1.0
 */

public class TaoBaoPushRefundPriceThread extends Thread {

    private int CanRefund = 0;//可退

    private int NoPrice = 1;//拒绝原因：无退款

    private int Refunded = 2;//拒绝原因：已退款

    private int Refunding = 3;//拒绝原因：退款处理中

    private int ErrorPrice = 4;//拒绝原因：退款金额不一致

    //    private int TimeOut = 5;//超时，暂不用

    private String keyName = "OrderRefunding=";

    //上一层传递的数据
    private int random;

    //单位：秒
    private int waitTime;

    private int waitCount;

    private JSONObject req;//请求

    private String callBackUrl;//回调地址

    private Map<String, String> dataMap;

    private String CatchRefundPriceWhenNoOpen;

    public TaoBaoPushRefundPriceThread(JSONObject req, String callBackUrl, Map<String, String> dataMap,
            String CatchRefundPriceWhenNoOpen, int waitTime, int waitCount, int random) {
        this.req = req;
        this.random = random;
        this.dataMap = dataMap;
        this.waitTime = waitTime;
        this.waitCount = waitCount;
        this.callBackUrl = callBackUrl;
        this.CatchRefundPriceWhenNoOpen = CatchRefundPriceWhenNoOpen;
    }

    public void run() {
        //为空
        if (req == null) {
            req = new JSONObject();
        }
        //正在退款
        boolean refunding = false;
        //结果
        JSONObject result = new JSONObject();
        //加载订单
        Trainorder order = new Trainorder();
        //退款ID
        long PKId = req.getLongValue("PKId");
        //订单ID
        long orderId = req.getLongValue("orderId");
        //申请标识
        String applyId = req.getString("taobao_refundnumber");
        //淘宝退款流水号
        String tradeNum = req.getString("taobao_alipaytraindeno");
        //淘宝退款金额
        float taobao_refundprice = req.getFloatValue("taobao_refundprice") / 100;
        //请求数据错误
        if (dataMap == null || orderId <= 0 || taobao_refundprice <= 0
                || ElongHotelInterfaceUtil.StringIsNull(tradeNum)) {
            //以无退款回调
            result = result(req, false, NoPrice);
            callBack(result, order, orderId, tradeNum, taobao_refundprice, refunding, PKId, applyId);
            return;
        }
        //正在退款判断
        refunding = refunding(tradeNum);
        //正在退款，尝试等待
        if (refunding) {
            try {
                for (int i = 0; i < waitCount; i++) {
                    //等待
                    Thread.sleep(waitTime * 1000);
                    //尝试
                    refunding = refunding(tradeNum);
                    //非正在退款
                    if (!refunding) {
                        break;//中断
                    }
                }
            }
            catch (Exception e) {
                System.out.println("ThreadSleepException：" + ElongHotelInterfaceUtil.errormsg(e));
            }
        }
        //正在退款，直接拒绝
        if (refunding) {
            result = result(req, false, Refunding);
        }
        else {
            try {
                //加载订单
                order = Server.getInstance().getTrainService().findTrainorder(orderId);
                //查询失败
                if (order == null || order.getId() != orderId) {
                    throw new Exception("查询订单失败！");
                }
                //处理结果
                result = operate(order, req, taobao_refundprice);
            }
            catch (Exception e) {
                //无退款
                result = result(req, false, NoPrice);
                //异常日志
                ExceptionUtil.writelogByException("t淘宝推送退款处理_异常", e);
            }
        }
        //回调处理
        callBack(result, order, orderId, tradeNum, taobao_refundprice, refunding, PKId, applyId);
    }

    /**
     * 退款回调
     */
    private void callBack(JSONObject result, Trainorder order, long orderId, String tradeNum, float taobao_refundprice,
            boolean refunding, long PKId, String applyId) {
        try {
            long fuzzyId = 0;
            //保存成功
            if (result.containsKey("fuzzyId")) {
                //GET
                fuzzyId = result.getLongValue("fuzzyId");
                //REMOVE
                result.remove("fuzzyId");
            }
            //回调结果
            String callBackResult = "";
            //尝试2次
            for (int i = 0; i < 2; i++) {
                //POST
                callBackResult = RequestUtil.post(callBackUrl, result.toString(), AccountSystem.UTF8,
                        AccountSystem.NullMap, 0);
                //为空，查库
                if (ElongHotelInterfaceUtil.StringIsNull(callBackResult)) {
                    callBackResult = dbCallBackResult(PKId, applyId);
                }
                //非空，中断
                if (!ElongHotelInterfaceUtil.StringIsNull(callBackResult)) {
                    break;
                }
            }
            //日志
            WriteLog.write("t淘宝推送退款处理_回调", random + "-->OrderId:" + orderId + ":FuzzyId:" + fuzzyId + ":回调结果:"
                    + callBackResult + ":回调数据:" + result);
            //回调成功
            if ("Success".equalsIgnoreCase(callBackResult)) {
                if (fuzzyId > 0) {
                    //新增虚拟帐户
                    new TrainVmoneyRecord().refund(order.getAgentid(), taobao_refundprice, orderId,
                            order.getOrdernumber(), order.getQunarOrdernumber(), true, false);
                    //新增操作记录
                    TongchengSupplyMethod.createtrainorderrc(1, "订单退款：" + taobao_refundprice + "元", orderId, fuzzyId,
                            FuzzyRefund.REFUNDED, "系统");
                }
            }
            //回调失败
            else {
                if (fuzzyId > 0) {
                    //SQL
                    String updateSql = "update FuzzyRefund set RefundPriceQuestion = " + FuzzyRefund.REFUNDCALLBACKFAIL
                            + ", FailReason = '" + callBackResult + "' where Id = " + fuzzyId;
                    //更新
                    Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("t淘宝推送退款处理_异常", e);
        }
        finally {
            if (!refunding) {
                remove(tradeNum);
            }
        }
    }

    /**
     * 回调聚石塔无返回数据，查询数据库结果
     */
    @SuppressWarnings("rawtypes")
    private String dbCallBackResult(long PKId, String applyId) {
        String result = "";
        //ID正确
        if (PKId > 0 || !ElongHotelInterfaceUtil.StringIsNull(applyId)) {
            try {
                //SQL
                String sql = "select CallBack from TrainOrderRefund with(nolock)";
                //拼接
                if (PKId > 0) {
                    sql += " where PKId = " + PKId;
                }
                else {
                    sql += " where RefundOrderNo = '" + applyId + "'";
                }
                //查询
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                //正确
                if (list != null && list.size() > 0) {
                    Map map = (Map) list.get(0);
                    result = map.get("CallBack").toString();
                }
            }
            catch (Exception e) {

            }
        }
        return "1".equals(result) ? "Success" : "";
    }

    /**
     * 订单正在退款
     */
    private boolean refunding(String tradeNum) {
        boolean result = true;
        try {
            //KEY
            String key = keyName + tradeNum;
            //内存不存在
            if (!RefundTicketDataUtil.refundDataContains(key)) {
                //设置
                RefundTicketDataUtil.refundDataAdd(key, ElongHotelInterfaceUtil.getCurrentTime() + " ---> TaoBao");
                //结果
                result = false;
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("t淘宝推送退款处理_异常", e);
        }
        return result;
    }

    /**
     * 内存中移除
     */
    private void remove(String tradeNum) {
        RefundTicketDataUtil.refundDataRemove(keyName + tradeNum);
    }

    /**
     * 取支付宝软件地址
     */
    @SuppressWarnings("unchecked")
    private String getAliPayUrl(String aliPayPath) {
        String result = "";
        //路径正确
        if (aliPayPath != null && aliPayPath.contains("alipayurl")) {
            try {
                //内存不存在
                if (dataMap != null && !dataMap.containsKey(aliPayPath)) {
                    List<Sysconfig> configs = Server.getInstance().getSystemService()
                            .findAllSysconfig("where C_NAME = '" + aliPayPath + "'", "", -1, 0);
                    //查询成功
                    if (configs != null && configs.size() > 0) {
                        //取第一个
                        result = configs.get(0).getValue();
                        //存到内存
                        dataMap.put(aliPayPath, result);
                    }
                }
                else {
                    result = dataMap.get(aliPayPath);
                }
            }
            catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 退款处理逻辑
     * @param req 请求JSON
     * @remark 车票高改之后，流水号为改签流水
     */
    private JSONObject operate(Trainorder order, JSONObject req, float taobao_refundprice) {
        //支付宝
        String aliPayPath = "";
        //支付账号
        String aliPayAcount = "";
        //本地流水
        String localTradeNum = "";
        //订单ID
        long orderId = order.getId();
        //加载改签
        Trainorderchange change = new Trainorderchange();
        //工具类
        LocalRefundUtil localUtil = new LocalRefundUtil();
        //所有车票
        List<Trainticket> orderTicketList = localUtil.orderTicket(order);
        //默认：非高改车票、高改：对应改签车票
        List<Trainticket> changeTicketList = localUtil.notHighChangeTicket(order);
        //淘宝退款流水号
        String taobao_alipaytraindeno = req.getString("taobao_alipaytraindeno");
        //是原订单
        if (taobao_alipaytraindeno.equals(order.getSupplytradeno())) {
            aliPayPath = order.getSupplyaccount();
            localTradeNum = order.getSupplytradeno();
            aliPayAcount = order.getAutounionpayurlsecond();
            //拆分
            if (aliPayPath != null && aliPayPath.contains("/alipayurl")) {
                aliPayPath = aliPayPath.substring(aliPayPath.indexOf("alipayurl"));
            }
        }
        //存在高改
        else if (orderTicketList.size() > changeTicketList.size()) {
            //加载改签
            change = orderChange(orderId, taobao_alipaytraindeno);
            //线上高改
            if (!ElongHotelInterfaceUtil.StringIsNull(change.getSupplytradeno())
                    && change.getTcprice() > change.getTcoriginalprice()) {
                //支付软件
                aliPayPath = change.getPayflag();
                //支付账号
                aliPayAcount = change.getPayaccount();
                //改签流水
                localTradeNum = change.getSupplytradeno();
                //改签车票
                changeTicketList = localUtil.changeTicket(order, change);
            }
        }
        //流水为空
        if (ElongHotelInterfaceUtil.StringIsNull(localTradeNum)) {
            return result(req, false, NoPrice);
        }
        //高改标识
        boolean highChange = change.getId() > 0;
        //已退车票
        List<Trainticket> orderRefundList = localUtil.orderRefund(changeTicketList);
        //全部退完
        if (changeTicketList.size() == orderRefundList.size()) {
            return result(req, false, Refunded);
        }
        //校验订单
        String checkOrder = highChange ? localUtil.checkChange(order, change) : localUtil.checkOrder(order);
        //存在错误
        if (!ElongHotelInterfaceUtil.StringIsNull(checkOrder)) {
            return result(req, false, NoPrice);
        }
        //模糊退数据
        List<FuzzyRefund> fuzzyRefundList = localUtil.orderFuzzy(order);
        List<FuzzyRefund> changeFuzzyRefundList = localUtil.changeFuzzy(change, fuzzyRefundList);
        //校验车票
        String checkTicket = localUtil.checkTicket(fuzzyRefundList, orderTicketList);
        //存在错误
        if (!ElongHotelInterfaceUtil.StringIsNull(checkTicket)) {
            if (checkTicket.startsWith("退票处理中")) {
                return result(req, false, Refunding);
            }
            else if (checkTicket.startsWith("模糊处理中")) {
                return result(req, false, Refunding);
            }
            else {
                return result(req, false, NoPrice);
            }
        }
        //车票累计退款
        float ticketRefundTotal = localUtil.ticketRefundTotal(changeFuzzyRefundList, highChange ? changeTicketList
                : orderTicketList, highChange);
        //累计退款错误
        if (ticketRefundTotal < 0) {
            return result(req, false, NoPrice);
        }
        //以车票款项计算还能退多少钱
        float canRefundPriceByTicket = localUtil.countCanRefundPriceByTicket(fuzzyRefundList, orderTicketList);
        //不能再退了
        if (canRefundPriceByTicket <= 0) {
            return result(req, false, NoPrice);
        }
        //交易记录，计算订单还能退多少钱
        float canRefundPriceByRebaterecord = localUtil.countCanRefundPriceByRebaterecord(order);
        //不能再退了
        if (canRefundPriceByRebaterecord <= 0) {
            return result(req, false, NoPrice);
        }
        //能退金额不一致
        if (canRefundPriceByTicket != canRefundPriceByRebaterecord) {
            return result(req, false, NoPrice);
        }
        //支付宝退款
        List<Uniontrade> aliRefundPrice = localUtil.aliRefundPrice(localTradeNum);
        //本地无退款时即时抓支付宝数据
        if (aliRefundPrice.size() == 0 && "1".equals(CatchRefundPriceWhenNoOpen)) {
            //支付软件地址
            String aliPayUrl = getAliPayUrl(aliPayPath);
            //非空，抓取退款金额
            if (!ElongHotelInterfaceUtil.StringIsNull(aliPayUrl)) {
                //退款
                float refundPrice = AliPayRefundUtil.catchPrice(aliPayUrl, localTradeNum, aliPayAcount);
                //退款大于0
                if (refundPrice > 0) {
                    aliRefundPrice.add(AliPayRefundUtil.getRefundTrade(refundPrice, AliPayRefundUtil.NullDepartTime));
                }
            }
        }
        //支付宝累计退款
        float aliRefundTotal = localUtil.aliRefundTotal(aliRefundPrice);
        //累计退款错误
        if (aliRefundTotal <= 0) {
            return result(req, false, NoPrice);
        }
        //当前支付宝退款金额已退完
        if (aliRefundTotal <= ticketRefundTotal) {
            return result(req, false, Refunded);
        }
        //还能退的金额
        aliRefundTotal = ElongHotelInterfaceUtil.floatSubtract(aliRefundTotal, ticketRefundTotal);
        //淘宝退款金额 > 还能退的金额
        if (taobao_refundprice > aliRefundTotal) {
            return result(req, false, ErrorPrice);
        }
        //重复支付
        if (localUtil.payTradeNos(aliRefundPrice).split("@").length > 1) {
            return result(req, false, NoPrice);
        }
        //未处理退款
        List<Uniontrade> noRefundedTrade = new MatchTicketUtil().matchTradeByRefundTotal(aliRefundPrice,
                ticketRefundTotal, false);
        //无未处理的
        if (noRefundedTrade.size() == 0) {
            return result(req, false, NoPrice);
        }
        //总未退款金额
        float totalNoRefundedPrice = 0;
        //循环未处理退款
        for (Uniontrade uniontrade : noRefundedTrade) {
            totalNoRefundedPrice = ElongHotelInterfaceUtil.floatAdd(totalNoRefundedPrice, uniontrade.getAmount());
        }
        //可退总金额
        float totalMoney = localUtil.maxFuzzyCanRefund(canRefundPriceByRebaterecord, changeFuzzyRefundList,
                changeTicketList);
        //可退金额<=0
        if (totalMoney <= 0) {
            return result(req, false, NoPrice);
        }
        //总未退款金额<=可退总金额、支付宝收到还能退的金额==总未退款金额
        if (totalNoRefundedPrice > 0 && totalNoRefundedPrice <= totalMoney && aliRefundTotal == totalNoRefundedPrice) {
            //模糊退
            FuzzyRefund fuzzy = saveFuzzy(order, change, totalMoney, taobao_refundprice, highChange);
            //新增成功
            if (fuzzy != null && fuzzy.getId() > 0) {
                JSONObject result = result(req, true, CanRefund);
                //ID
                result.put("fuzzyId", fuzzy.getId());
                //返回
                return result;
            }
            else {
                return result(req, false, NoPrice);
            }
        }
        else {
            return result(req, false, ErrorPrice);
        }
    }

    /**
     * 新增模糊退记录
     */
    private FuzzyRefund saveFuzzy(Trainorder order, Trainorderchange change, float totalMoney,
            float taobao_refundprice, boolean highChange) {
        //创建记录
        FuzzyRefund fuzzy = new FuzzyRefund();
        //记录赋值
        fuzzy.setRemark("淘宝");
        fuzzy.setTimeStamp("");
        fuzzy.setOrderId(order.getId());
        fuzzy.setChangeId(change.getId());
        fuzzy.setTotalMoney(totalMoney);
        fuzzy.setMoney(taobao_refundprice);
        fuzzy.setStatus(FuzzyRefund.REFUNDED);
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
        return Server.getInstance().getTrainService().createFuzzyRefund(fuzzy);
    }

    /**
     * 高改订单
     * @param orderId 订单ID
     * @param taobao_alipaytraindeno 淘宝传的支付宝交易号
     */
    @SuppressWarnings("rawtypes")
    private Trainorderchange orderChange(long orderId, String taobao_alipaytraindeno) {
        long changeId = 0;
        //错误
        if (ElongHotelInterfaceUtil.StringIsNull(taobao_alipaytraindeno)) {
            return new Trainorderchange();
        }
        //SQL
        String sql = "select ID from T_TRAINORDERCHANGE with(nolock) where C_ORDERID = " + orderId
                + " and C_SUPPLYTRADENO = '" + taobao_alipaytraindeno + "'";
        //有高改
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        //改签退款
        if (list != null && list.size() == 1) {
            Map map = (Map) list.get(0);
            //改签ID
            changeId = Long.parseLong(map.get("ID").toString());
        }
        //结果
        Trainorderchange change = new Trainorderchange();
        //查询
        if (changeId > 0) {
            change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
        }
        //返回
        return change != null ? change : new Trainorderchange();
    }

    /**
     * 退款处理总结果
     * @param req 请求参数
     * @param agree_refund true或false
     * @param refuse_reason 拒绝原因，0：可退，1：无退款，2：已退款，3：退款处理中，4：退款金额不一致
     */
    private JSONObject result(JSONObject req, boolean agree_refund, int refuse_reason) {
        JSONObject result = new JSONObject();
        //是否同意退款
        result.put("agree_refund", agree_refund);
        //拒绝退款原因
        result.put("refuse_reason", refuse_reason);
        result.put("ttp_id", req.getString("taobao_ttp_id"));
        result.put("user_id", req.getString("taobao_user_id"));
        result.put("refuse_reason_msg", reasonMsg(refuse_reason));
        result.put("seller_id", req.getString("taobao_SellerId"));
        result.put("apply_id", req.getString("taobao_refundnumber"));
        result.put("trade_no", req.getString("taobao_alipaytraindeno"));
        result.put("refund_fee", req.getIntValue("taobao_refundprice"));
        result.put("main_biz_order_id", req.getString("taobao_ordernumber"));
        //车票明细
        JSONArray tickets = new JSONArray();
        //设置车票
        result.put("tickets", tickets);
        //返回结果
        return result;
    }

    /**
     * 不可退原因
     */
    private String reasonMsg(int refuse_reason) {
        String ret = "";
        //原因
        if (refuse_reason == NoPrice) {
            ret = "无退款";
        }
        else if (refuse_reason == Refunded) {
            ret = "已退款";
        }
        else if (refuse_reason == Refunding) {
            ret = "退款处理中";
        }
        else if (refuse_reason == ErrorPrice) {
            ret = "退款金额不一致";
        }
        return ret;
    }

}