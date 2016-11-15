package com.ccservice.b2b2c.atom.service12306.offlineRefund.local;

import com.weixin.util.RequestUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.train.FuzzyRefund;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

public class RefundCallBackUtil extends TongchengSupplyMethod {

    /**
     * 接口申请线下退票，与LocalRefundUtil类要保持一致
     */
    private boolean interfaceApplyTicket(Trainticket ticket) {
        return ticket.getApplyTicketFlag() == 1 && ticket.getStatus() == Trainticket.APPLYTREFUND;
    }

    //可模糊退
    public boolean canFuzzyRefund(long agentId) {
        if (agentId <= 0) {
            return false;
        }
        //取配置文件数据
        String CanFuzzyRefundAgent = PropertyUtil.getValue("CanFuzzyRefundAgent");
        //循环匹配数据
        for (String temp : CanFuzzyRefundAgent.split("@")) {
            if (String.valueOf(agentId).equals(temp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 可线下改签
     */
    public boolean canOfflineChange(long agentId) {
        if (agentId <= 0) {
            return false;
        }
        //取配置文件数据
        String CanOfflineChangeAgent = PropertyUtil.getValue("CanOfflineChangeAgent");
        //循环匹配数据
        for (String temp : CanOfflineChangeAgent.split("@")) {
            if (String.valueOf(agentId).equals(temp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 申请改签
     */
    public boolean requestChange(Trainticket ticket) {
        //申请退票
        if (interfaceApplyTicket(ticket)) {
            return false;
        }
        //车票订单
        Trainorder order = ticket.getTrainpassenger().getTrainorder();
        //不可改签
        if (!canOfflineChange(order.getAgentid())) {
            return false;
        }
        //车票状态
        int status = ticket.getStatus();
        int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
        //已出票、无法退票
        if ((status == Trainticket.ISSUED || status == Trainticket.NONREFUNDABLE) && changeType == 0) {
            //置为申请
            int newType = Trainticket.APPLYTREFUND;
            int newStatus = Trainticket.FINISHCHANGE;
            String updateSql = "update T_TRAINTICKET set C_CHANGETYPE = " + newType + ", C_STATUS = " + newStatus
                    + ", C_CHANGEREQUESTTIME = '" + ElongHotelInterfaceUtil.getCurrentTime() + "' where ID = "
                    + ticket.getId() + " and C_STATUS = " + status + " and C_CHANGETYPE = 0";
            //更新车票
            return Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) == 1;
        }
        else {
            return false;
        }
    }

    /**
     * 申请退票
     */
    public void requestRefund(Trainticket ticket) {
        //接口
        if (interfaceApplyTicket(ticket)) {
            return;
        }
        //已退
        if (ticket.getStatus() == Trainticket.REFUNDED) {
            return;
        }
        //状态
        int newStatus = Trainticket.APPLYTREFUND;
        //SQL
        String updateSql = "update T_TRAINTICKET set C_ISAPPLYTICKET = 2, C_STATUS = " + newStatus
                + ", C_REFUNDREQUESTTIME = '" + ElongHotelInterfaceUtil.getCurrentTime() + "' where ID = "
                + ticket.getId() + " and C_STATUS = " + ticket.getStatus();
        //更新多个车票
        Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
    }

    /**
     * 改签回调
     * @param refundPrice 改签退款
     * @param specialFlag 特殊标识，1：低改按退票处理，用于淘宝等采购商
     * @return 1：记录了日志
     */
    public int changeCallBack(Trainorder order, Trainticket ticket, float refundPrice, int specialFlag, int random) {
        //结果
        int result = 0;
        //接口
        if (interfaceApplyTicket(ticket)) {
            specialFlag = 1;
        }
        //淘宝
        int taobaoType = 6;
        //订单归属>>错误时，认为是淘宝，不支持改签则不处理
        int tempType = order.getInterfacetype() != null && order.getInterfacetype() > 0 ? order.getInterfacetype()
                .intValue() : taobaoType;
        //高改
        if (refundPrice == ticket.getPrice()) {
            requestRefund(ticket);//按退票处理
            refundCallBack(order, ticket, 0);//以退票回调
        }
        //改签按退票处理>>特殊、非淘宝且不支持线下改签
        else if (refundPrice > 0 && refundPrice < ticket.getPrice()
                && (specialFlag == 1 || (tempType != taobaoType && !canOfflineChange(order.getAgentid())))) {
            //按退票处理
            requestRefund(ticket);
            //以退票回调
            refundCallBack(order, ticket, ElongHotelInterfaceUtil.floatSubtract(ticket.getPrice(), refundPrice));
            //记录日志
            if (specialFlag != 1) {
                result = 1;//表示已经记录日志
                WriteLog.write("h火车票支付宝退款_特殊", random + ":interfaceType:" + order.getInterfacetype() + ":orderId:"
                        + order.getId() + ":ticketId:" + ticket.getId() + ":refundPrice:" + refundPrice);
            }
        }
        //低改
        else if (refundPrice > 0 && refundPrice < ticket.getPrice() && requestChange(ticket)) {
            //状态
            int newStatus = Trainticket.WAITREFUND;
            int oldStats = Trainticket.APPLYTREFUND;
            //ID
            long orderId = order.getId();
            long ticketId = ticket.getId();
            //等待退款
            String updateSql = "update T_TRAINTICKET set C_CHANGETYPE = " + newStatus + ", C_TCPROCEDURE = "
                    + refundPrice + " where ID = " + ticketId + " and C_CHANGETYPE = " + oldStats + " and C_PRICE > "
                    + refundPrice;
            //更新成功，记录日志
            if (Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) == 1) {
                //记录日志
                try {
                    String passengerName = ticket.getTrainpassenger().getName();
                    String content = passengerName + "[" + ticket.getTickettypestr() + "]线下改签，退款金额：" + refundPrice;
                    createtrainorderrc(1, content, orderId, ticketId, newStatus, "系统接口");
                }
                catch (Exception e) {
                }
                try {
                    int interfacetype = order.getInterfacetype().intValue();
                    String trainRefundPriceUrl = getSysconfigString("trainRefundPrice");
                    //回调地址非空
                    if (!ElongHotelInterfaceUtil.StringIsNull(trainRefundPriceUrl)) {
                        String url = trainRefundPriceUrl + "?returnType=2&trainorderid=" + orderId + "&ticketid="
                                + ticketId + "&interfacetype=" + interfacetype + "&procedure=" + refundPrice
                                + "&responseurl=" + trainRefundPriceUrl;
                        //GET请求
                        RequestUtil.get(url, AccountSystem.UTF8, AccountSystem.NullMap, 0);
                    }
                }
                catch (Exception e) {
                }
            }
        }
        //返回
        return result;
    }

    /**
     * 退票回调
     * @param refundFee 退票手续费
     */
    public void refundCallBack(Trainorder order, Trainticket ticket, float refundFee) {
        //状态
        int newStatus = Trainticket.WAITREFUND;
        int oldStats = Trainticket.APPLYTREFUND;
        //ID
        long orderId = order.getId();
        long ticketId = ticket.getId();
        //新票价
        float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
        //车票价
        float ticketPrice = newPrice > 0 ? newPrice : ticket.getPrice().floatValue();
        //退款
        float refundPrice = ElongHotelInterfaceUtil.floatSubtract(ticketPrice, refundFee);
        //SQL
        String updateSql = "update T_TRAINTICKET set C_STATUS = " + newStatus + ", C_PROCEDURE = " + refundFee
                + " where ID = " + ticketId + " and C_STATUS = " + oldStats;
        //改签
        if (newPrice > 0) {
            updateSql += " and C_TCNEWPRICE >= " + refundPrice;
        }
        else {
            updateSql += " and C_PRICE >= " + refundPrice;
        }
        if (refundPrice > 0 && Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) == 1) {
            //记录日志
            try {
                String passengerName = ticket.getTrainpassenger().getName();
                String content = passengerName + "[" + ticket.getTickettypestr() + "]线下退票，退款金额：" + refundPrice;
                createtrainorderrc(1, content, orderId, ticketId, newStatus, "系统接口");
            }
            catch (Exception e) {
            }
            //退款回调
            try {
                int interfacetype = order.getInterfacetype().intValue();
                String trainRefundPriceUrl = getSysconfigString("trainRefundPrice");
                //回调地址非空
                if (!ElongHotelInterfaceUtil.StringIsNull(trainRefundPriceUrl)) {
                    //地址
                    String url = trainRefundPriceUrl + "?trainorderid=" + orderId + "&ticketid=" + ticketId
                            + "&interfacetype=" + interfacetype + "&procedure=" + refundFee + "&responseurl="
                            + trainRefundPriceUrl;
                    //GET请求
                    RequestUtil.get(url, AccountSystem.UTF8, AccountSystem.NullMap, 0);
                }
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 模糊退回调
     * @param refundPrice 退款金额
     */
    public void fuzzyCallBack(Trainorder order, Trainorderchange change, FuzzyRefund fuzzy, float refundPrice) {
        //ID
        long orderId = order.getId();
        long fuzzyId = fuzzy.getId();
        //记录日志
        try {
            //内容
            String content = "订单模糊退，退款金额：" + refundPrice;
            //改签
            if (change.getId() > 0) {
                content = "改签订单模糊退，退款金额：" + refundPrice;
            }
            //保存
            createtrainorderrc(1, content, orderId, fuzzyId, FuzzyRefund.WAITREFUND, "系统接口");
        }
        catch (Exception e) {
        }
        //退款回调
        try {
            int interfacetype = order.getInterfacetype().intValue();
            String trainRefundPriceUrl = getSysconfigString("trainRefundPrice");
            //回调地址非空
            if (!ElongHotelInterfaceUtil.StringIsNull(trainRefundPriceUrl)) {
                String url = trainRefundPriceUrl + "?returnType=mohutui&trainorderid=" + orderId + "&ticketid="
                        + fuzzyId + "&interfacetype=" + interfacetype + "&procedure=" + refundPrice + "&responseurl="
                        + trainRefundPriceUrl;
                //GET请求
                RequestUtil.get(url, AccountSystem.UTF8, AccountSystem.NullMap, 0);
            }
        }
        catch (Exception e) {
        }
    }

}