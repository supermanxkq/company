package com.ccservice.b2b2c.atom.service12306.offlineRefund.local;

import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.uniontrade.Uniontrade;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 12306车票信息匹配本地车票
 * @author WH
 * @time 2015年7月15日 下午3:40:39
 * @version 1.0
 */

public class MatchTicketUtil {

    private SimpleDateFormat shiFenFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 以金额匹配支付宝退款
     */
    public Uniontrade matchUniontradeByRefundPrice(List<Uniontrade> noRefundedTrade, float ticketRefund) {
        for (Uniontrade trade : noRefundedTrade) {
            if (trade.getAmount() == ticketRefund) {
                return trade;
            }
        }
        return new Uniontrade();
    }

    /**
     * 取未处理的支付宝退款 
     * @param match 匹配上的
     * @param aliRefundPrice 所有支付宝退款
     * @param ticketRefundTotal 车票已退金额，如果金额>0，但没拿到匹配的退款，!match时也返回空
     */
    public List<Uniontrade> matchTradeByRefundTotal(List<Uniontrade> aliRefundPrice, float ticketRefundTotal,
            boolean match) {
        //退款长度
        int size = aliRefundPrice.size();
        //匹配上的
        List<Uniontrade> matchList = new ArrayList<Uniontrade>();
        //未匹配上的
        List<Uniontrade> noMatchList = new ArrayList<Uniontrade>();
        //存在退款
        if (size > 0 && ticketRefundTotal > 0) {
            //退款统计
            float countRefundTotal = new LocalRefundUtil().aliRefundTotal(aliRefundPrice);
            //退款=已退
            if (countRefundTotal == ticketRefundTotal) {
                matchList.addAll(aliRefundPrice);
            }
            else {
                int max = 1 << size;//相当于1乘以2^size
                for (int i = 1; i < max - 1; i++) {
                    int t = i;
                    int k = size - 1;
                    float totalPrice = 0;
                    while (t > 0) {
                        if ((t & 1) > 0) {
                            totalPrice = ElongHotelInterfaceUtil
                                    .floatAdd(totalPrice, aliRefundPrice.get(k).getAmount());
                        }
                        k--;
                        t = t >> 1;//相当于t除以2
                    }
                    if (totalPrice == ticketRefundTotal) {
                        matchList = getMatchTrade(aliRefundPrice, i);
                        break;
                    }
                }
            }
        }
        //取未匹配的
        if (!match && size > 0) {
            for (Uniontrade trade : aliRefundPrice) {
                if (!matchList.contains(trade)) {
                    noMatchList.add(trade);
                }
            }
            //金额>0，但没拿到匹配的退款
            if (ticketRefundTotal > 0 && matchList.size() == 0) {
                noMatchList = new ArrayList<Uniontrade>();
            }
        }
        return match ? matchList : noMatchList;
    }

    /**
     * 已退结果
     */
    private List<Uniontrade> getMatchTrade(List<Uniontrade> aliRefundPrice, int i) {
        List<Uniontrade> refundedList = new ArrayList<Uniontrade>();
        int t = i;
        int k = aliRefundPrice.size() - 1;
        while (t > 0) {
            if ((t & 1) > 0) {
                refundedList.add(aliRefundPrice.get(k));
            }
            k--;
            t = t >> 1;
        }
        return refundedList;
    }

    /**
     * 匹配费率 0、5%、10%、20%
     * @param departTime 发车时间
     * @param transDate 退款明细中时间
     * @param operateTime 订单明细中时间
     * 
     */
    public float matchRefundRate(String operateTime, String transDate, String departTime) {
        float result = -1;
        //时间
        operateTime = ElongHotelInterfaceUtil.StringIsNull(operateTime) ? transDate : operateTime;
        try {
            long end = shiFenFormat.parse(departTime).getTime();
            long start = shiFenFormat.parse(operateTime).getTime();
            //时间差
            long timesub = end - start;
            //24小时
            if (timesub <= 24 * 60 * 60 * 1000) {
                result = 0.2f;
            }
            //48小时
            else if (timesub <= 48 * 60 * 60 * 1000) {
                result = 0.1f;
            }
            //15天
            else if (timesub < 15 * 24 * 60 * 60 * 1000) {
                result = 0.05f;
            }
            else {
                result = 0;
            }
        }
        catch (Exception e) {

        }
        return result;
    }

    /**
     * 退票费
     * @param refundRate 费率
     * @param ticketPrice 票价
     */
    public float countRefundFee(float ticketPrice, float refundRate) {
        float result = refundRate == 0 ? 0 : -1;
        //收手续费
        if (refundRate > 0) {
            float tempFee = ElongHotelInterfaceUtil.floatMultiply(ticketPrice, refundRate);
            //最低退票费
            float minReturnFee = Float.parseFloat(PropertyUtil.getValue("minReturnFee"));
            //手续费小数舍弃规则
            String[] trainRefundFeeDecimals = PropertyUtil.getValue("TrainRefundFeeDecimal").split("@");
            //比较最低手续
            if (tempFee <= minReturnFee) {
                //票价小于最低手续费
                if (ticketPrice < minReturnFee) {
                    return ticketPrice;
                }
                else {
                    return minReturnFee;
                }
            }
            //取小数
            String strFee = String.valueOf(tempFee);
            //小数点
            int idx = strFee.indexOf(".");
            //存在小数
            if (idx > 0) {
                //手续费小数
                float xiaoShuFloat = Float.parseFloat("0" + strFee.substring(idx));
                //小数>0
                if (xiaoShuFloat > 0) {
                    //手续费取整
                    tempFee = ElongHotelInterfaceUtil.floatSubtract(tempFee, xiaoShuFloat);
                    //0-0.25-0@0.25-0.75-0.5@0.75-1-1
                    for (String decimal : trainRefundFeeDecimals) {
                        String[] decimals = decimal.split("-");
                        float start = Float.parseFloat(decimals[0]);
                        float end = Float.parseFloat(decimals[1]);
                        if (xiaoShuFloat >= start && xiaoShuFloat < end) {
                            xiaoShuFloat = Float.parseFloat(decimals[2]);
                            tempFee = ElongHotelInterfaceUtil.floatAdd(tempFee, xiaoShuFloat);
                            break;
                        }
                    }
                }
            }
            result = tempFee;
        }
        return result;
    }

    /**
     * 车票匹配
     * @param order 本地订单
     * @param change 改签订单
     * @param detail 12306订单明细
     * @param listInfoList 12306列表明细
     */
    public Trainticket matchTicket(Trainorder order, Trainorderchange change, JSONObject detail,
            List<JSONObject> listInfo) {
        Trainticket result = new Trainticket();
        //车票信息
        String seat_no = detail.getString("seat_no");
        String coach_no = detail.getString("coach_no");
        String seat_name = detail.getString("seat_name");
        String train_date = detail.getString("train_date");
        String ticket_type = detail.getString("ticket_type");
        String passenger_name = detail.getString("passenger_name");
        String board_train_code = detail.getString("board_train_code");
        //匹配列表，拿票号
        String ticketNo = "";
        for (JSONObject list : listInfo) {
            if (isEqual(seat_no, list.getString("seat_no")) && isEqual(ticket_type, list.getString("ticket_type_code"))
                    && isEqual(train_date, list.getString("train_date").split(" ")[0].replace("-", ""))
                    && isEqual(seat_name, list.getString("seat_name")) && isEqual(coach_no, list.getString("coach_no"))) {
                JSONObject passengerDTO = list.getJSONObject("passengerDTO");
                JSONObject stationTrainDTO = list.getJSONObject("stationTrainDTO");
                if (isEqual(passenger_name, passengerDTO.getString("passenger_name"))
                        && isEqual(board_train_code, stationTrainDTO.getString("station_train_code"))) {
                    ticketNo = list.getString("ticket_no");
                    break;
                }
            }
        }
        //改签ID
        long changeId = change == null ? 0 : change.getId();
        //订单乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //筛选结果
        List<Trainticket> matchList = new ArrayList<Trainticket>();
        List<Trainticket> changeList = new ArrayList<Trainticket>();
        //座席等未匹配上，但乘客+票类型唯一
        int onlyCount = 0;
        Trainticket onlyTicket = new Trainticket();
        //循环乘客
        for (Trainpassenger passenger : passengers) {
            //12306乘客姓名与本地匹配上
            if (passenger_name.equals(passenger.getName())) {
                //乘客车票
                List<Trainticket> tickets = passenger.getTraintickets();
                //循环车票
                for (Trainticket ticket : tickets) {
                    //线上高改
                    if (changeId > 0) {
                        //改签ID
                        if (ticket.getChangeid() != changeId) {
                            continue;
                        }
                    }
                    else {
                        //车票已高改
                        if (ticket.getTcnewprice() != null && ticket.getTcnewprice() > ticket.getPrice()) {
                            continue;
                        }
                    }
                    //车票类型
                    if (!isEqual(ticket_type, String.valueOf(ticket.getTickettype()))) {
                        continue;
                    }
                    onlyCount++;
                    onlyTicket = ticket;//同人名、同票类型
                    //车票状态
                    int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                    //车票为空
                    if (ElongHotelInterfaceUtil.StringIsNull(ticketNo)) {
                        //同天、同车次、同车箱、同座位
                        if (isEqual(train_date, ticket.getDeparttime().split(" ")[0].replace("-", ""))
                                && isEqual(board_train_code, ticket.getTrainno())
                                && isEqual(coach_no, ticket.getCoach()) && isEqual(seat_name, ticket.getSeatno())) {
                            matchList.add(ticket);
                        }
                        //已线上改签、改签退已审核>>同天、同车次、同车箱、同座位
                        if ((changeType == 1 || changeType == 2)
                                && isEqual(train_date, ticket.getTtcdeparttime().split(" ")[0].replace("-", ""))
                                && isEqual(board_train_code, ticket.getTctrainno())
                                && isEqual(coach_no, ticket.getTccoach()) && isEqual(seat_name, ticket.getTcseatno())) {
                            changeList.add(ticket);
                        }
                    }
                    else {
                        if (isEqual(ticketNo, ticket.getTicketno())) {
                            matchList.add(ticket);
                        }
                        if ((changeType == 1 || changeType == 2) && isEqual(ticketNo, ticket.getTcticketno())) {
                            changeList.add(ticket);
                        }
                    }
                }
            }
        }
        //未匹配上、改签跟非改签都存在
        if (matchList.size() > 0 && changeList.size() > 0) {
            return result;
        }
        //不存在，取改签
        matchList = matchList.size() == 0 ? changeList : matchList;
        //都没匹配上
        if (matchList.size() == 0 && onlyCount == 1) {
            matchList.add(onlyTicket);
        }
        //匹配到唯一的车票
        return matchList.size() == 1 ? matchList.get(0) : result;
    }

    /**
     * 判断字符串相同
     */
    public boolean isEqual(String value12306, String valuelocal) {
        return value12306.equals(valuelocal);
    }
}