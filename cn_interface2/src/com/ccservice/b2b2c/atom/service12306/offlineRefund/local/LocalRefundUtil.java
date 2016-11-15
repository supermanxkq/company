package com.ccservice.b2b2c.atom.service12306.offlineRefund.local;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.sql.Timestamp;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.train.FuzzyRefund;
import com.ccservice.b2b2c.base.uniontrade.Uniontrade;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

/**
 * 退票工具类
 * @author WH
 * @time 2015年7月9日 上午11:16:49
 * @version 1.0
 */

public class LocalRefundUtil extends TongchengSupplyMethod {

    /**
     * 订单或改签流水正确
     */
    public boolean payTradeNoIsRight(String[] payTradeNoArray, String orderTradeNum) {
        boolean right = false;
        for (String tradeNum : payTradeNoArray) {
            if (tradeNum.equals(orderTradeNum)) {
                right = true;
                break;
            }
        }
        return right;
    }

    /**
     * 订单支付流水，用于淘宝推退款判断多次支付
     */
    public String payTradeNos(List<Uniontrade> aliRefundPrice) {
        if (aliRefundPrice.size() <= 0) {
            return "";
        }
        for (Uniontrade trade : aliRefundPrice) {
            String payTradeNos = trade.getPayTradeNos();
            //非空
            if (!ElongHotelInterfaceUtil.StringIsNull(payTradeNos)) {
                trade.setPayCount(payTradeNos.split("@").length);
            }
        }
        //取支付次数最多的
        Collections.sort(aliRefundPrice, new Comparator<Uniontrade>() {
            public int compare(Uniontrade a, Uniontrade b) {
                return b.getPayCount() - a.getPayCount();//大的放前面
            }
        });
        String payTradeNos = aliRefundPrice.get(0).getPayTradeNos();
        //返回结果
        return ElongHotelInterfaceUtil.StringIsNull(payTradeNos) ? "" : payTradeNos;
    }

    /**
     * 支付流水、是否都退款
     * 重复支付的>>1、取金额最低的；2、金额一样时，取退款次数最多的
     */
    public JSONObject refundData(String[] payTradeNoArray) {
        //支付流水
        String payTradeNo = "";
        //都有退款
        boolean allRefunded = false;
        //正常支付
        if (payTradeNoArray.length == 1) {
            allRefunded = true;
            payTradeNo = payTradeNoArray[0];
        }
        //重复支付
        else {
            //退款
            Map<String, List<Uniontrade>> map = new HashMap<String, List<Uniontrade>>();
            //循环
            for (String tradeNum : payTradeNoArray) {
                //查退款
                List<Uniontrade> list = aliRefundPrice(tradeNum);
                //有退款
                if (list.size() > 0) {
                    map.put(tradeNum, list);
                }
            }
            //转LIST
            List<Entry<String, List<Uniontrade>>> list = new ArrayList<Entry<String, List<Uniontrade>>>(map.entrySet());
            //排序LIST
            Collections.sort(list, new Comparator<Entry<String, List<Uniontrade>>>() {
                public int compare(Entry<String, List<Uniontrade>> EntryA, Entry<String, List<Uniontrade>> EntryB) {
                    //退款
                    List<Uniontrade> RefundA = EntryA.getValue();
                    List<Uniontrade> RefundB = EntryB.getValue();
                    //金额
                    float PriceA = aliRefundTotal(RefundA);
                    float PriceB = aliRefundTotal(RefundB);
                    //金额一样，取退的次数多的
                    if (PriceA == PriceB) {
                        return RefundB.size() - RefundA.size();
                    }
                    //金额小的放前
                    else {
                        return PriceA < PriceB ? -1 : 1;
                    }
                }
            });
            //第一个
            payTradeNo = list.get(0).getKey();
            //流水全退
            allRefunded = map.size() == payTradeNoArray.length;
        }
        //设置结果
        JSONObject result = new JSONObject();
        result.put("payTradeNo", payTradeNo);
        result.put("allRefunded", allRefunded);
        //返回结果
        return result;
    }

    /**
     * 模糊退最多可退金额
     * @param oldCanRefund 原可退
     * @param fuzzyRefundList 模糊退
     * @param orderTicketList 订单车票
     */
    public float maxFuzzyCanRefund(float oldCanRefund, List<FuzzyRefund> fuzzyRefundList,
            List<Trainticket> orderTicketList) {
        //计算
        float tempCanRefund = countCanRefundPriceByTicket(fuzzyRefundList, orderTicketList);
        //返回
        return oldCanRefund < tempCanRefund ? oldCanRefund : tempCanRefund;
    }

    /**
     * 支付宝累计退款
     */
    public float aliRefundTotal(List<Uniontrade> aliRefundPrice) {
        float price = 0;
        for (Uniontrade trade : aliRefundPrice) {
            //退款金额
            float amount = trade.getAmount() == null ? 0 : trade.getAmount().floatValue();
            //退款错误
            if (amount <= 0) {
                return -1;
            }
            price = ElongHotelInterfaceUtil.floatAdd(price, amount);
        }
        return price;
    }

    /**
     * 车票累计退款
     */
    public float ticketRefundTotal(List<FuzzyRefund> fuzzyRefundList, List<Trainticket> orderTicketList,
            boolean highChange) {
        float price = 0;
        //车票退款
        for (Trainticket ticket : orderTicketList) {
            //退款金额
            float amount = countTicketRealRefundPrice(ticket, highChange);
            //退款错误
            if (amount < 0) {
                return -1;
            }
            price = ElongHotelInterfaceUtil.floatAdd(price, amount);
        }
        //模糊退款
        for (FuzzyRefund fuzzy : fuzzyRefundList) {
            price = ElongHotelInterfaceUtil.floatAdd(price, fuzzy.getMoney());
        }
        return price;
    }

    /**
     * 取车票List最早或最晚发车日期
     * @param type 类型，1：最早；2：最晚
     * @return yyyy-MM-dd日期
     */
    public String minOrMaxDpartTime(List<Trainticket> orderTicketList, int type) throws Exception {
        String time = "";
        //循环车票
        for (Trainticket ticket : orderTicketList) {
            //发车时间
            String departtime = ticket.getDeparttime().split(" ")[0];
            //车票状态
            int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
            //已线上改签、改签退已审核
            if (changeType == 1 || changeType == 2) {
                departtime = ticket.getTtcdeparttime().split(" ")[0];
            }
            //默认
            if (ElongHotelInterfaceUtil.StringIsNull(time)) {
                time = departtime;
            }
            //最早
            else if (type == 1 && ElongHotelInterfaceUtil.getSubDays(time, departtime) < 0) {
                time = departtime;
            }
            //最晚
            else if (type == 2 && ElongHotelInterfaceUtil.getSubDays(time, departtime) > 0) {
                time = departtime;
            }
        }
        return time;
    }

    /**
     * 取订单未发车、已发车车票
     * @return key>>G：未发车；H：已发车
     */
    public Map<String, List<Trainticket>> departTicket(List<Trainticket> orderTicketList) {
        List<Trainticket> G = new ArrayList<Trainticket>();//未发车
        List<Trainticket> H = new ArrayList<Trainticket>();//已发车
        try {
            //当前时间
            String current = ElongHotelInterfaceUtil.getCurrentDate();
            //循环车票
            for (Trainticket ticket : orderTicketList) {
                //发车时间
                String departtime = ticket.getDeparttime();
                //车票状态
                int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                //已线上改签、改签退已审核
                if (changeType == 1 || changeType == 2) {
                    departtime = ticket.getTtcdeparttime();
                }
                //与当前时间判断，发车日期-当前日期
                if (ElongHotelInterfaceUtil.getSubDays(current, departtime.split(" ")[0]) >= 0) {
                    G.add(ticket);
                }
                else {
                    H.add(ticket);
                }
            }
        }
        catch (Exception e) {
            G = new ArrayList<Trainticket>();//未发车
            H = new ArrayList<Trainticket>();//已发车
        }
        //结果
        Map<String, List<Trainticket>> map = new HashMap<String, List<Trainticket>>();
        //PUT
        if (G.size() > 0) {
            map.put("G", G);
        }
        if (H.size() > 0) {
            map.put("H", H);
        }
        //返回
        return map;
    }

    /**
     * 校验订单信息
     */
    public String checkOrder(Trainorder order) {
        String ret = "";
        //无订单
        if (order == null || order.getId() <= 0) {
            return "查询订单失败";
        }
        //订单状态
        if (order.getOrderstatus() != Trainorder.ISSUED) {
            return "订单状态非已出票";
        }
        //订单号为空
        if (ElongHotelInterfaceUtil.StringIsNull(order.getExtnumber())) {
            return "订单号为空";
        }
        //订单归属
        int interfacetype = order.getInterfacetype() != null && order.getInterfacetype() > 0 ? order.getInterfacetype()
                .intValue() : getOrderAttribution(order);
        if (interfacetype < 1) {
            return "订单归属类型错误";
        }
        else {
            order.setInterfacetype(interfacetype);
        }
        return ret;
    }

    /**
     * 校验改签信息
     */
    public String checkChange(Trainorder order, Trainorderchange change) {
        //校验订单
        String ret = checkOrder(order);
        //校验改签
        if (ElongHotelInterfaceUtil.StringIsNull(ret)) {
            //无改签
            if (change == null || change.getId() <= 0) {
                ret = "查询改签失败";
            }
            //改签状态
            else if (change.getTcstatus() != Trainorderchange.FINISHCHANGE) {
                ret = "改签状态非已完成";
            }
            //改签金额
            else if (change.getTcoriginalprice() >= change.getTcprice()) {
                ret = "改签非高改[" + change.getTcoriginalprice() + "][" + change.getTcprice() + "]";
            }
        }
        return ret;
    }

    /**
     * 校验车票信息
     */
    public String checkTicket(List<FuzzyRefund> fuzzyRefundList, List<Trainticket> orderTicketList) {
        String ret = "";
        //车票
        for (Trainticket ticket : orderTicketList) {
            //改签处理中
            if (changeing(ticket)) {
                return "改签处理中[" + ticket.getId() + "][" + ticket.getStatusstr() + "]";
            }
            //退票处理中
            if (refunding(ticket)) {
                return "退票处理中[" + ticket.getId() + "][" + ticket.getStatusstr() + "]";
            }
            //车票状态
            int status = ticket.getStatus();
            //状态错误
            if (status < Trainticket.ISSUED || status == Trainticket.NONISSUEDABLE) {
                return "车票[" + ticket.getId() + "]状态非已出票";
            }
        }
        //模糊退
        for (FuzzyRefund fuzzy : fuzzyRefundList) {
            //模糊处理中
            if (fuzzying(fuzzy)) {
                return "模糊处理中[" + fuzzy.getId() + "][" + fuzzy.getStatusStr() + "]";
            }
            //金额错误，可能性小
            if (fuzzy.getMoney() <= 0) {
                return "模糊退[" + fuzzy.getId() + "][金额错误:" + fuzzy.getMoney() + "]";
            }
            //ID错误，可能性小
            if (fuzzy.getChangeId() < 0) {
                return "模糊退[" + fuzzy.getId() + "]改签ID[" + fuzzy.getChangeId() + "]错误";
            }
        }
        return ret;
    }

    /**
     * 订单模糊退
     */
    public List<FuzzyRefund> orderFuzzy(Trainorder order) {
        //SQL
        String sql = "where OrderId = " + order.getId();
        //QUERY
        return Server.getInstance().getTrainService().findAllFuzzyRefund(sql, "", -1, 0);
    }

    /**
     * 改签模糊退
     * @param change 改签订单，改签ID为0时，返回非改签模糊退
     */
    public List<FuzzyRefund> changeFuzzy(Trainorderchange change, List<FuzzyRefund> orderFuzzyRefundList) {
        //ID
        long changeId = change.getId();
        //结果
        List<FuzzyRefund> changeFuzzyRefundList = new ArrayList<FuzzyRefund>();
        //循环
        for (FuzzyRefund fuzzy : orderFuzzyRefundList) {
            if (fuzzy.getChangeId() == changeId) {
                changeFuzzyRefundList.add(fuzzy);
            }
        }
        //返回
        return changeFuzzyRefundList;
    }

    /**
     * 接口申请线下退票>>特殊状态
     */
    public int interfaceApplyTicketStatus(Trainticket ticket) {
        return ticket.getChangeType() == 0 ? Trainticket.ISSUED : Trainticket.FINISHCHANGE;
    }

    /**
     * 接口申请线下退票，与RefundCallBackUtil类要保持一致
     */
    public boolean interfaceApplyTicket(Trainticket ticket) {
        return ticket.getApplyTicketFlag() == 1 && ticket.getStatus() == Trainticket.APPLYTREFUND;
    }

    /**
     * 接口申请线下退票>>更新为退票问题
     */
    public void interfaceApplyTicketQuestion(Trainticket ticket) {
        //SQL
        String updateSql = "update T_TRAINTICKET set C_APPLYTICKETFLAG = -1 where ID = " + ticket.getId()
                + " and C_STATUS = " + Trainticket.APPLYTREFUND + " and C_APPLYTICKETFLAG = 1";
        //更新
        Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
    }

    /**
     * 订单所有车票
     */
    public List<Trainticket> orderTicket(Trainorder order) {
        List<Trainticket> result = new ArrayList<Trainticket>();
        //订单乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //存在乘客
        if (passengers != null && passengers.size() > 0) {
            //循环乘客
            for (Trainpassenger passenger : passengers) {
                //乘客订单
                passenger.setTrainorder(order);
                //乘客车票
                List<Trainticket> tickets = passenger.getTraintickets();
                //循环车票
                for (Trainticket ticket : tickets) {
                    ticket.setTrainpassenger(passenger);
                    //接口申请线下退票，正常或改签退车票，状态设为已出票
                    if (interfaceApplyTicket(ticket)) {
                        ticket.setStatus(interfaceApplyTicketStatus(ticket));
                    }
                    result.add(ticket);
                }
            }
        }
        return result;
    }

    /**
     * 改签所有车票
     */
    public List<Trainticket> changeTicket(Trainorder order, Trainorderchange change) {
        //改签ID
        long changeId = change.getId();
        //返回结果
        List<Trainticket> result = new ArrayList<Trainticket>();
        //订单乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //存在乘客
        if (passengers != null && passengers.size() > 0) {
            //循环乘客
            for (Trainpassenger passenger : passengers) {
                //乘客订单
                passenger.setTrainorder(order);
                //乘客车票
                List<Trainticket> tickets = passenger.getTraintickets();
                //循环车票
                for (Trainticket ticket : tickets) {
                    if (ticket.getChangeid() == changeId) {
                        ticket.setTrainpassenger(passenger);
                        //接口申请线下退票，正常或改签退车票，状态设为已出票
                        if (interfaceApplyTicket(ticket)) {
                            ticket.setStatus(interfaceApplyTicketStatus(ticket));
                        }
                        result.add(ticket);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 非高改车票
     */
    public List<Trainticket> notHighChangeTicket(Trainorder order) {
        List<Trainticket> result = new ArrayList<Trainticket>();
        //订单乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //存在乘客
        if (passengers != null && passengers.size() > 0) {
            //循环乘客
            for (Trainpassenger passenger : passengers) {
                //乘客订单
                passenger.setTrainorder(order);
                //乘客车票
                List<Trainticket> tickets = passenger.getTraintickets();
                //循环车票
                for (Trainticket ticket : tickets) {
                    //新票价为空、新票价<=原票价
                    if (ticket.getTcnewprice() == null || ticket.getTcnewprice() <= ticket.getPrice()) {
                        ticket.setTrainpassenger(passenger);
                        //接口申请线下退票，正常或改签退车票，状态设为已出票
                        if (interfaceApplyTicket(ticket)) {
                            ticket.setStatus(interfaceApplyTicketStatus(ticket));
                        }
                        result.add(ticket);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 订单已退车票
     */
    public List<Trainticket> orderRefund(List<Trainticket> orderTicketList) {
        //判断车票状态
        List<Trainticket> result = new ArrayList<Trainticket>();
        //循环车票
        for (Trainticket ticket : orderTicketList) {
            //已退票退款
            if (ticket.getStatus() == Trainticket.REFUNDED) {
                result.add(ticket);
            }
        }
        return result;
    }

    /**
     * 订单已线下改签车票
     */
    public List<Trainticket> orderOfflineChange(List<Trainticket> orderTicketList) {
        //判断车票状态
        List<Trainticket> result = new ArrayList<Trainticket>();
        //循环车票
        for (Trainticket ticket : orderTicketList) {
            //已退票退款
            if (ticket.getChangeType() != null && ticket.getChangeType() == Trainticket.REFUNDED) {
                result.add(ticket);
            }
        }
        return result;
    }

    /**
     * 订单车票已线上改签，包含改签退
     */
    public List<Trainticket> orderOnlineChanged(List<Trainticket> orderTicketList) {
        //判断车票状态
        List<Trainticket> result = new ArrayList<Trainticket>();
        //循环车票
        for (Trainticket ticket : orderTicketList) {
            //车票状态
            int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
            //已线上改签、改签退已审核
            if (changeType == 1 || changeType == 2) {
                result.add(ticket);
            }
        }
        return result;
    }

    /**
     * 模糊处理中
     */
    public boolean fuzzying(FuzzyRefund fuzzy) {
        //模糊状态
        int status = fuzzy.getStatus();
        //状态判断
        if (status == FuzzyRefund.APPLYTREFUND || status == FuzzyRefund.REFUNDROCESSING
                || status == FuzzyRefund.WAITREFUND || status == FuzzyRefund.REFUNDIING
                || status == FuzzyRefund.REFUNDFAIL) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 退票处理中
     */
    public boolean refunding(Trainticket ticket) {
        //车票状态
        int status = ticket.getStatus();
        //状态判断
        if (status == Trainticket.APPLYTREFUND || status == Trainticket.REFUNDROCESSING
                || status == Trainticket.WAITREFUND || status == Trainticket.REFUNDIING
                || status == Trainticket.REFUNDFAIL) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 线下改签处理中
     */
    public boolean changeing(Trainticket ticket) {
        //车票状态
        int status = ticket.getStatus();
        int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
        //状态判断
        if (changeType == Trainticket.APPLYTREFUND || changeType == Trainticket.REFUNDROCESSING
                || changeType == Trainticket.WAITREFUND || changeType == Trainticket.REFUNDIING
                || changeType == Trainticket.REFUNDFAIL || status == Trainticket.APPLYCHANGE
                || status == Trainticket.APPLYROCESSING || status == Trainticket.THOUGHCHANGE) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 计算车票实际票款，存在的问题：客服改签退
     * @param highChange true:高改
     * @return 小于0时，表示错误，中断退票操作，抛给客服处理
     * @remark changeType 1:线上改签、2:改签退、5:线下改签申请、6:线下改签处理中、7:线下无法改签(暂无)
     *                    8:线下改签等待退款、9:线下改签退款中、10:线下改签已退款、11:线下改签退款失败
     */
    public float countTicketRealRefundPrice(Trainticket ticket, boolean highChange) {
        float refundPrice = -1;
        //车票状态
        int status = ticket.getStatus();
        int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
        //车票价格
        float oldPrice = ticket.getPrice().floatValue();
        float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
        //退票手续费
        float refundFee = ticket.getProcedure() == null ? 0 : ticket.getProcedure();
        //线下改签退款、改签退手续费
        float changeReturn = ticket.getTcProcedure() == null ? 0 : ticket.getTcProcedure();
        //改签手续费
        float changeFee = ticket.getChangeProcedure() == null ? 0 : ticket.getChangeProcedure();
        //线上高改
        if (highChange) {
            if (changeType == 1 && newPrice > oldPrice) {
                //线上改签后退票
                if (status == Trainticket.REFUNDED) {
                    refundPrice = ElongHotelInterfaceUtil.floatSubtract(newPrice, refundFee);
                }
                //改签完成、无法退票
                else if (status == Trainticket.FINISHCHANGE || status == Trainticket.NONREFUNDABLE) {
                    refundPrice = 0;
                }
            }
            return refundPrice;
        }
        //仅已退票
        if (status == Trainticket.REFUNDED && (changeType == 0 || changeType == 2)) {
            //改签退已审核
            if (changeType == 2) {
                refundPrice = ElongHotelInterfaceUtil.floatSubtract(oldPrice, changeReturn);
            }
            else {
                refundPrice = ElongHotelInterfaceUtil.floatSubtract(oldPrice, refundFee);
            }
        }
        //线上改签
        else if ((status == Trainticket.FINISHCHANGE || status == Trainticket.NONREFUNDABLE) && changeType == 1
                && newPrice > 0) {
            //高改
            if (newPrice > oldPrice) {
                refundPrice = oldPrice;
            }
            //平改、低改，原票价-新票价-手续费
            else {
                refundPrice = ElongHotelInterfaceUtil.floatSubtract(
                        ElongHotelInterfaceUtil.floatSubtract(oldPrice, newPrice), changeFee);
            }
        }
        //线上改签后退票
        else if (status == Trainticket.REFUNDED && changeType == 1 && newPrice > 0) {
            //高改
            if (newPrice > oldPrice) {
                refundPrice = oldPrice;
            }
            //平改、低改，原票价-改签手续费-退票手续费
            else {
                refundPrice = ElongHotelInterfaceUtil.floatSubtract(oldPrice,
                        ElongHotelInterfaceUtil.floatAdd(changeFee, refundFee));
            }
        }
        //线下改签
        else if ((status == Trainticket.FINISHCHANGE || status == Trainticket.NONREFUNDABLE)
                && changeType == Trainticket.REFUNDED && changeReturn > 0 && newPrice > 0) {
            refundPrice = changeReturn;
        }
        //线下改签后退票
        else if (status == Trainticket.REFUNDED && changeType == Trainticket.REFUNDED && changeReturn > 0
                && newPrice > 0) {
            refundPrice = ElongHotelInterfaceUtil.floatSubtract(oldPrice, refundFee);
        }
        //已出票、无法退票
        else if ((status == Trainticket.ISSUED || status == Trainticket.NONREFUNDABLE) && changeType == 0) {
            refundPrice = 0;
        }
        return refundPrice;
    }

    /**
     * 统计车票可退金额
     */
    public float countTicketCanRefundPrice(Trainticket ticket) {
        float canRefundPrice = -1;
        //车票状态
        int status = ticket.getStatus();
        int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
        //车票价格
        float oldPrice = ticket.getPrice().floatValue();
        float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
        //退票手续费
        float refundFee = ticket.getProcedure() == null ? 0 : ticket.getProcedure();
        //线下改签退款、改签退手续费
        float changeReturn = ticket.getTcProcedure() == null ? 0 : ticket.getTcProcedure();
        //改签手续费
        float changeFee = ticket.getChangeProcedure() == null ? 0 : ticket.getChangeProcedure();
        //仅已退票
        if (status == Trainticket.REFUNDED && (changeType == 0 || changeType == 2)) {
            canRefundPrice = refundFee;
        }
        //线上改签
        else if ((status == Trainticket.FINISHCHANGE || status == Trainticket.NONREFUNDABLE) && changeType == 1
                && newPrice > 0) {
            canRefundPrice = newPrice + changeFee;
        }
        //线上改签后退票
        else if (status == Trainticket.REFUNDED && changeType == 1 && newPrice > 0) {
            canRefundPrice = refundFee + changeFee;
        }
        //线下改签
        else if ((status == Trainticket.FINISHCHANGE || status == Trainticket.NONREFUNDABLE)
                && changeType == Trainticket.REFUNDED && changeReturn > 0 && newPrice > 0) {
            canRefundPrice = ElongHotelInterfaceUtil.floatSubtract(oldPrice, changeReturn);
        }
        //线下改签后退票
        else if (status == Trainticket.REFUNDED && changeType == Trainticket.REFUNDED && changeReturn > 0
                && newPrice > 0) {
            canRefundPrice = refundFee;
        }
        //已出票、无法退票
        else if ((status == Trainticket.ISSUED || status == Trainticket.NONREFUNDABLE) && changeType == 0) {
            canRefundPrice = oldPrice;
        }
        return canRefundPrice;
    }

    /**
     * 通过车票，计算订单还能退多少钱
     */
    public float countCanRefundPriceByTicket(List<FuzzyRefund> fuzzyRefundList, List<Trainticket> orderTicketList) {
        float canRefundPrice = 0;
        //车票能退
        for (Trainticket ticket : orderTicketList) {
            //退款金额
            float amount = countTicketCanRefundPrice(ticket);
            //退款错误
            if (amount < 0) {
                return -1;
            }
            canRefundPrice = ElongHotelInterfaceUtil.floatAdd(canRefundPrice, amount);
        }
        //减掉模糊退
        for (FuzzyRefund fuzzy : fuzzyRefundList) {
            canRefundPrice = ElongHotelInterfaceUtil.floatSubtract(canRefundPrice, fuzzy.getMoney());
        }
        return canRefundPrice;
    }

    /**
     * 判断是否可以继续退款
     * @param refundPrice 要退的金额
     */
    public boolean canContinueRefundPrice(Trainorder order, float refundPrice) {
        return countCanRefundPriceByRebaterecord(order) >= refundPrice && refundPrice >= 0;
    }

    /**
     * 通过交易记录，计算订单还能退多少钱
     */
    @SuppressWarnings("rawtypes")
    public float countCanRefundPriceByRebaterecord(Trainorder order) {
        float canRefundPrice = -1;
        //手续费
        Float commission = order.getCommission();
        commission = (commission == null || commission < 0) ? 0 : commission;
        //订单号
        String orderNumber = order.getOrdernumber();
        //查询SQL
        String sql = "SELECT ISNULL(SUM(C_REBATEMONEY), 0) C_REBATEMONEY "
                + "FROM T_REBATERECORD WITH(NOLOCK) WHERE C_ORDERNUMBER = '" + orderNumber + "'";
        //查询交易记录
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        //统计交易记录
        if (list != null && list.size() == 1) {
            Map map = (Map) list.get(0);
            //负的交易和，支付金额+退款金额，理论上要<=0
            canRefundPrice = -Float.valueOf(map.get("C_REBATEMONEY").toString());
        }
        return ElongHotelInterfaceUtil.floatSubtract(canRefundPrice, commission.floatValue());
    }

    /**
     * 支付宝退款
     * @param tradeNum 流水号
     */
    @SuppressWarnings("unchecked")
    public List<Uniontrade> aliRefundPrice(String tradeNum) {
        String where = "where C_TRANDNUM = '" + tradeNum + "' and C_BUSSTYPE = 2";
        List<Uniontrade> tradeList = Server.getInstance().getSystemService().findAllUniontrade(where, "", -1, 0);
        //排序
        Collections.sort(tradeList, new Comparator<Uniontrade>() {
            public int compare(Uniontrade a, Uniontrade b) {
                Timestamp at = a.getOrdertime();
                Timestamp bt = b.getOrdertime();
                //时间差
                long timesub = at.getTime() - bt.getTime();
                //时间相等
                if (timesub == 0) {
                    float af = a.getBalance().floatValue();
                    float bf = b.getBalance().floatValue();
                    //小的放后面
                    return af < bf ? 1 : -1;
                }
                else {
                    //小的放前面
                    return timesub < 0 ? -1 : 1;
                }
            }
        });
        return tradeList;
    }

    /**
     * 支付宝退款设置特殊状态
     * @param bankTransNo 0:无此订单；-1:全部退完；-2:已退；-3：未找到订单
     */
    public void updateAliByTrandNum(long orderId, String[] payTradeNoArray, String bankTransNo) {
        if ("-1".equals(bankTransNo)) {
            bankTransNo = "已退完";
        }
        else if ("-2".equals(bankTransNo)) {
            bankTransNo = "已处理";
        }
        else if ("-3".equals(bankTransNo)) {
            bankTransNo = "未查询到订单";
        }
        else if ("-4".equals(bankTransNo)) {
            bankTransNo = "未查询到改签";
        }
        //循环
        for (String tradeNum : payTradeNoArray) {
            //SQL
            String sql = "update T_UNIONTRADE set C_ORDERID = " + orderId + ", C_BANKTRANSNO = '" + bankTransNo
                    + "' where C_TRANDNUM = '" + tradeNum + "' and C_BUSSTYPE = 2";
            //更新
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
    }

}