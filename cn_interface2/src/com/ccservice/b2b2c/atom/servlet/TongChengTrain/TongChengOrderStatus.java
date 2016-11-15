package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;

/**
 * 同程订单状态
 * @author WH
 */

public class TongChengOrderStatus {
    /**
     * @param order 订单
     * @param tickets  车票
     * @return 同程订单状态
     *             |--> 占座失败
     *             |--> 占座成功
     *             |--> 正在出票
     *             |--> 出票成功
     *             |--> 出票失败
     *             |--> 订单已取消
     *             |--> 补单成功
     *             |--> 正在退票
     *             |--> 退票成功
     *             |--> 退票失败
     *             |--> 正在改签
     *             |--> 改签成功
     *             |--> 改签失败
     */
    public static String StatusStr(Trainorder order) {
        //返回
        String ret = "";
        //订单状态
        int orderStatus = order.getOrderstatus();
        //车票状态，一个订单可能有多张车票
        //        List<Integer> ticketStatus = new ArrayList<Integer>();
        //        for (Trainticket ticket : tickets) {
        //            int tempStatus = ticket.getStatus();
        //            if (!ticketStatus.contains(tempStatus)) {
        //                ticketStatus.add(tempStatus);
        //            }
        //        }
        boolean orderIssued = orderStatus == Trainorder.ISSUED ? true : false;
        //逻辑判断
        if (orderStatus == Trainorder.WAITPAY) {
            ret = "占座成功";
        }
        else if (orderStatus == Trainorder.WAITISSUE) {
            ret = "正在出票";
        }
        else if (orderStatus == Trainorder.ISSUED) {
            ret = "出票成功";
        }
        else if (orderStatus == Trainorder.REFUSED) {
            ret = "出票失败";
        }
        else if (orderStatus == Trainorder.CANCLED) {
            ret = "订单已取消";
        }
        else if (orderStatus == Trainorder.ISSUED) {
            ret = "正在改签";
        }
        else if (orderStatus == Trainorder.ISSUED) {
            ret = "改签成功";
        }
        else if (orderStatus == Trainorder.ISSUED) {
            ret = "改签失败";
        }
        else if (orderStatus == Trainorder.ISSUED) {
            ret = "正在退票";
        }
        else if (orderStatus == Trainorder.ISSUED) {
            ret = "退票成功";
        }
        else if (orderStatus == Trainorder.ISSUED) {
            ret = "退票失败";
        }
        else {
            ret = "待核实";
        }
        return ret;
    }

    /**
     * @param order 订单
     * @param tickets  车票
     * @return 同程订单状态
     *  public static final int WAITORDER = 1;//等待下单

    public static final int ORDERING = 2;//正在下单

    public static final int ORDERFALSE = 3;//下单失败

    public static final int ORDEREDWAITPAY = 4;//下单成功等待支付

    public static final int ORDEREDPAYING = 5;//下单成功支付中

    public static final int ORDEREDPAYED = 6;//支付成功

    public static final int ORDEREDPAYFALSE = 7;//支付失败

    public static final int ORDERPAYSHENHE = 8;//支付审核中
     */
    public static String StatusStr12306(Trainorder order) {
        //返回
        String ret = "";
        //订单状态
        int status12306 = order.getState12306();
        //逻辑判断
        if (status12306 == Trainorder.WAITORDER) {
            ret = "等待下单";
        }
        else if (status12306 == Trainorder.ORDERING) {
            ret = "正在下单";
        }
        else if (status12306 == Trainorder.ORDERFALSE) {
            ret = "下单失败";
        }
        else if (status12306 == Trainorder.ORDEREDWAITPAY) {
            ret = "下单成功等待支付";
        }
        else if (status12306 == Trainorder.ORDEREDPAYING) {
            ret = "下单成功支付中";
        }
        else if (status12306 == Trainorder.ORDEREDPAYED) {
            ret = "支付成功";
        }
        else if (status12306 == Trainorder.ORDEREDPAYFALSE) {
            ret = "支付失败";
        }
        else if (status12306 == Trainorder.ORDERPAYSHENHE) {
            ret = "支付审核中";
        }
        return ret;
    }

    /**
     * @param order 订单
     * @param tickets  车票
     * @return 同程订单状态
     *             |--> 占座失败
     *             |--> 占座成功
     *             |--> 正在出票
     *             |--> 出票成功
     *             |--> 出票失败
     *             |--> 订单已取消
     *             |--> 补单成功
     *             |--> 正在退票
     *             |--> 退票成功
     *             |--> 退票失败
     *             |--> 正在改签
     *             |--> 改签成功
     *             |--> 改签失败
     */
    public static String StatusStr111(Trainorder order, List<Trainticket> tickets) {
        //返回
        String ret = "";
        //订单状态
        int orderStatus = order.getOrderstatus();
        //车票状态，一个订单可能有多张车票
        List<Integer> ticketStatus = new ArrayList<Integer>();
        for (Trainticket ticket : tickets) {
            int tempStatus = ticket.getStatus();
            if (!ticketStatus.contains(tempStatus)) {
                ticketStatus.add(tempStatus);
            }
        }
        boolean orderIssued = orderStatus == Trainorder.ISSUED ? true : false;
        //逻辑判断
        if (orderStatus == Trainorder.WAITPAY) {
            ret = "占座成功";
        }
        else if (orderStatus == Trainorder.WAITISSUE) {
            ret = "正在出票";
        }
        else if (orderIssued && (ticketStatus.size() == 1 && ticketStatus.contains(Trainticket.ISSUED))) {
            ret = "出票成功";
        }
        else if (orderStatus == Trainorder.REFUSED) {
            ret = "出票失败";
        }
        else if (orderStatus == Trainorder.CANCLED) {
            ret = "订单已取消";
        }
        else if (orderIssued
                && (ticketStatus.contains(Trainticket.APPLYCHANGE) || ticketStatus.contains(Trainticket.APPLYROCESSING))) {
            ret = "正在改签";
        }
        else if (orderIssued && ticketStatus.contains(Trainticket.THOUGHCHANGE)) {
            ret = "改签成功";
        }
        else if (orderIssued && ticketStatus.contains(Trainticket.CANTCHANGE)) {
            ret = "改签失败";
        }
        else if (orderIssued
                && (ticketStatus.contains(Trainticket.APPLYTREFUND) || ticketStatus
                        .contains(Trainticket.REFUNDROCESSING))) {
            ret = "正在退票";
        }
        else if (orderIssued && ticketStatus.contains(Trainticket.REFUNDED)) {
            ret = "退票成功";
        }
        else if (orderIssued && ticketStatus.contains(Trainticket.NONREFUNDABLE)) {
            ret = "退票失败";
        }
        else {
            ret = "待核实";
        }
        return ret;
    }

    /**
     * 1. 已取票
     * 2. 已在线改签，
     * 3. 已线下改签
     * 4. 已在线退票
     * 5. 已线下退票
     * 6. 已出票
     * 7. 待出票，表示尚未支付票款
     * 8. 待核实，表示出现系统故障获取状态出错
     * @param state
     * @return
     */
    public static String getticketstate(int state) {
        //        public static final int WAITPAY = 1;//等待支付
        //
        //        public static final int WAITISSUE = 2;//等待出票
        //
        //        public static final int ISSUED = 3;//已出票
        //
        //        public static final int NONISSUEDABLE = 4;//拒单无法出票
        //
        //        public static final int APPLYTREFUND = 5;//申请退票
        //
        //        public static final int REFUNDROCESSING = 6;//退票处理中
        //
        //        public static final int NONREFUNDABLE = 7;//无法退票
        //
        //        public static final int WAITREFUND = 8;//已退票-等待退款
        //
        //        public static final int REFUNDIING = 9;//退款中
        //
        //        public static final int REFUNDED = 10;//已退票退款
        //
        //        public static final int REFUNDFAIL = 11;//退款失败
        //
        //        public static final int APPLYCHANGE = 12;//申请改签 
        //
        //        public static final int APPLYROCESSING = 13;//改签处理中 
        //
        //        public static final int CANTCHANGE = 14;//无法改签  
        //
        //        public static final int THOUGHCHANGE = 15;//改签通过    
        //
        //        public static final int FILLMONEY = 16;//补款成功
        //
        //        public static final int FINISHCHANGE = 17;//改签完成
        String stateinfo = "待核实";
        if (state != 0) {
            if (state == Trainticket.WAITISSUE || state == Trainticket.WAITPAY) {
                stateinfo = "待出票";
            }
            else if (state == Trainticket.ISSUED) {
                stateinfo = "已出票";
            }
            else if (state == Trainticket.WAITREFUND) {
                stateinfo = "已线下退票";
            }
            else if (state == Trainticket.REFUNDROCESSING || state == Trainticket.REFUNDIING
                    || state == Trainticket.APPLYTREFUND) {
                stateinfo = "已在线退票";
            }
            else if (state == Trainticket.APPLYROCESSING) {
                stateinfo = "已线下改签";
            }
            else if (state == Trainticket.THOUGHCHANGE || state == Trainticket.FINISHCHANGE) {
                stateinfo = "已在线改签";
            }
            //            1.  已取票，表示客户已在车站窗口或取票机 取得了车票，因此如果有退款，可能会有
            //            以下三种可能：取了之后退票；取了之后
            //            改签；还有可能取了票之后再改签，然后 再退票
            //            2.  已在线改签，表示通过我们双方的系统改 签的
            //            3.  已线下改签，表示客户直接在窗口改签
            //            4.  已在线退票，表示通过我们双方的系统退 票的
            //            5.  已线下退票，表示客户直接在窗口退票
            //            6.  已出票，表示客户已经支付购票款项
            //            7.  待出票，表示尚未支付票款
            //            8.  待核实，表示出现系统故障获取状态出错

        }

        return stateinfo;
    }

    /**
     * 车票状态
     * @param isname true:状态名称；false：状态ID
     * @param oldTicket true:原车票；false：新车票
     * @remark statusid
     * 1.  已取票，表示客户已在车站窗口或取票机取得了车票，因此如果有退款，可能会有以下三种可能：取了之后退票；取了之后改签；还有可能取了票之后再改签，然后 再退票
     * 2.  已在线改签，表示通过我们双方的系统改签的
     * 3.  已线下改签，表示客户直接在窗口改签
     * 4.  已在线退票，表示通过我们双方的系统退票的
     * 5.  已线下退票，表示客户直接在窗口退票
     * 6.  已出票，表示客户已经支付购票款项
     * 7.  待出票，表示尚未支付票款
     */
    public static String getTicketStatus(Trainticket ticket, boolean isname, boolean oldTicket) {
        String result = isname ? "待核实" : "0";
        //错误
        if (ticket == null || ticket.getId() <= 0) {
            return result;
        }
        //状态
        int status = ticket.getStatus();
        int state12306 = ticket.getState12306() == null ? 0 : ticket.getState12306().intValue();
        int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType().intValue();
        int isapplyticket = ticket.getIsapplyticket() == null ? 0 : ticket.getIsapplyticket().intValue();
        //原车票
        if (oldTicket) {
            //待出票
            if (status == Trainticket.WAITPAY || status == Trainticket.WAITISSUE) {
                result = isname ? "待出票" : "7";
            }
            //已在线改签
            else if (changeType == 1) {
                result = isname ? "已在线改签" : "2";
            }
            //已在线退票
            else if ((status == Trainticket.WAITREFUND || status == Trainticket.REFUNDIING
                    || status == Trainticket.REFUNDED || status == Trainticket.REFUNDFAIL)
                    && isapplyticket == 1) {
                result = isname ? "已在线退票" : "4";
            }
            //已线下退票
            else if ((status == Trainticket.WAITREFUND || status == Trainticket.REFUNDIING
                    || status == Trainticket.REFUNDED || status == Trainticket.REFUNDFAIL)
                    && isapplyticket == 2) {
                result = isname ? "已线下退票" : "5";
            }
            //已线下改签
            else if (changeType > Trainticket.NONISSUEDABLE && changeType < Trainticket.APPLYCHANGE) {
                result = isname ? "已线下改签" : "3";
            }
            //已取票
            else if (state12306 == Trainticket.HASTICKET) {
                result = isname ? "已取票" : "1";
            }
            //已出票
            else if (status == Trainticket.ISSUED || status == Trainticket.APPLYTREFUND
                    || status == Trainticket.REFUNDROCESSING || status == Trainticket.NONREFUNDABLE
                    || status == Trainticket.APPLYCHANGE || status == Trainticket.THOUGHCHANGE) {
                result = isname ? "已出票" : "6";
            }
        }
        //新车票
        else {
            //待出票
            if (status == Trainticket.APPLYCHANGE || status == Trainticket.THOUGHCHANGE) {
                result = isname ? "待出票" : "7";
            }
            //已在线退票
            else if ((status == Trainticket.WAITREFUND || status == Trainticket.REFUNDIING
                    || status == Trainticket.REFUNDED || status == Trainticket.REFUNDFAIL)
                    && isapplyticket == 1) {
                result = isname ? "已在线退票" : "4";
            }
            //已线下退票
            else if ((status == Trainticket.WAITREFUND || status == Trainticket.REFUNDIING
                    || status == Trainticket.REFUNDED || status == Trainticket.REFUNDFAIL)
                    && isapplyticket == 2) {
                result = isname ? "已线下退票" : "5";
            }
            //已取票
            else if (state12306 == Trainticket.HASTICKET) {
                result = isname ? "已取票" : "1";
            }
            //已出票
            else if (status == Trainticket.FINISHCHANGE || status == Trainticket.APPLYTREFUND
                    || status == Trainticket.REFUNDROCESSING || status == Trainticket.NONREFUNDABLE) {
                result = isname ? "已出票" : "6";
            }
        }
        return result;
    }
}