package com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.util;

import java.util.Hashtable;
import com.alibaba.fastjson.JSONObject;

/**
 * 退票数据管理
 * @author WH
 * @time 2016年11月2日 下午3:34:48
 * @version 1.0
 */

public class ReturnTicketDataManage {

    /**
     * 退票状态：等待退票
     */
    public static final int WAIT = 1;

    /**
     * 退票状态：正在退票
     */
    public static final int PROCESSING = 2;

    /**
     * 退票锁
     */
    private static Object locker = new Object();

    /**
     * 退票数据
     * @remark 数据格式：<订单ID, <车票ID, 退票状态+退票时间>>
     */
    private static Hashtable<Long, Hashtable<Long, JSONObject>> refundMap = new Hashtable<Long, Hashtable<Long, JSONObject>>();

    /**
     * 存在退票中
     */
    private static boolean refunding(Hashtable<Long, JSONObject> tickets) {
        //有在退的
        boolean refunding = false;
        //遍历车票
        for (JSONObject object : tickets.values()) {
            //有在退的
            if (object.getIntValue("status") == PROCESSING) {
                //在退标识
                refunding = true;
                //中断循环
                break;
            }
        }
        //返回结果
        return refunding;
    }

    /**
     * 车票结束退票，移除当前，取下一个
     * @author WH
     * @time 2016年11月2日 下午4:55:46
     * @version 1.0
     * @param orderId 订单ID
     * @param ticketId 车票ID
     * @return 下一个退票的车票ID、发车时间
     */
    public static JSONObject ticketEndRefund(long orderId, long ticketId) {
        //结果
        JSONObject nextTicket = new JSONObject();
        //加锁
        synchronized (locker) {
            //车票集合
            Hashtable<Long, JSONObject> tickets = refundMap.get(orderId);
            //存在数据
            if (tickets != null && tickets.size() > 0) {
                //移除当前
                tickets.remove(ticketId);
                //还有数据、没有在退的
                if (tickets != null && tickets.size() > 0 && !refunding(tickets)) {
                    //取下一个
                    nextTicket = ReturnTicketDataUtil.nextTicket(tickets);
                    //处理状态
                    nextTicket.put("status", PROCESSING);
                    //处理时间
                    nextTicket.put("operateTime", System.currentTimeMillis());
                    //更新车票
                    tickets.put(nextTicket.getLongValue("ticketId"), nextTicket);
                    //更新订单
                    refundMap.put(orderId, tickets);
                }
            }
            //无数据了
            if (tickets == null || tickets.size() <= 0) {
                refundMap.remove(orderId);
            }
        }
        //返回
        return nextTicket;
    }

    /**
     * 车票开始退票
     * @author WH
     * @time 2016年11月2日 下午4:07:45
     * @version 1.0
     * @param orderId 订单ID
     * @param ticketId 车票ID
     * @param data MQ发送数据
     * @return true:开始退票；false:等待退票
     */
    public static boolean ticketStartRefund(long orderId, long ticketId, JSONObject data) {
        //结果
        boolean start = orderId > 0 && ticketId > 0;
        //正确
        if (start) {
            //加锁
            synchronized (locker) {
                //车票集合
                Hashtable<Long, JSONObject> tickets = refundMap.get(orderId);
                //存在数据
                if (tickets != null && tickets.size() > 0) {
                    //有在退的
                    if (refunding(tickets)) {
                        start = false;
                    }
                }
                //初始数据
                else {
                    tickets = new Hashtable<Long, JSONObject>();
                }
                //可开始退票、无此车票
                if (start || !tickets.containsKey(ticketId)) {
                    //信息
                    JSONObject refundInfo = new JSONObject();
                    //车票ID
                    refundInfo.put("ticketId", ticketId);
                    //内部重试
                    if (data.getBooleanValue("retryRefund")) {
                        refundInfo.put("retryRefund", true);
                    }
                    //处理状态
                    refundInfo.put("status", start ? PROCESSING : WAIT);
                    //车票票号
                    refundInfo.put("ticket_no", data.getString("ticket_no"));
                    //处理时间
                    refundInfo.put("operateTime", System.currentTimeMillis());
                    //发车时间
                    refundInfo.put("departTime", data.getLongValue("departTime"));
                    //申请时间
                    refundInfo.put("requestTime", data.getLongValue("requestTime"));
                    //更新车票
                    tickets.put(ticketId, refundInfo);
                    //更新订单
                    refundMap.put(orderId, tickets);
                }
            }
        }
        //返回
        return start;
    }

    /**
     * 获取只读
     * @author WH
     * @time 2016年11月2日 下午6:16:57
     * @version 1.0
     */
    public static Hashtable<Long, Hashtable<Long, JSONObject>> getReadOnly() {
        //只读
        Hashtable<Long, Hashtable<Long, JSONObject>> only = new Hashtable<Long, Hashtable<Long, JSONObject>>();
        //添加
        synchronized (locker) {
            only.putAll(refundMap);
        }
        //返回
        return only;
    }
}