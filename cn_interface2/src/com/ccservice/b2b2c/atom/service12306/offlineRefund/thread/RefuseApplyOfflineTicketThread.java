package com.ccservice.b2b2c.atom.service12306.offlineRefund.thread;

import com.weixin.util.RequestUtil;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service.interfacetype.TrainInterfaceType;

/**
 * 拒绝接口申请的线下退票
 * @author WH
 * @time 2016年4月5日 下午4:48:55
 * @version 1.0
 */

public class RefuseApplyOfflineTicketThread extends Thread {

    //订单ID
    private long orderId;

    //车票ID
    private long ticketId;

    //接口类型
    private int interfaceType;

    //拒绝理由
    private int reason = 42;//无退款

    //拒绝退票地址
    private String trainorderNonRefundable;

    //相关参数赋值
    public RefuseApplyOfflineTicketThread(long orderId, long ticketId, int interfaceType, String trainorderNonRefundable) {
        this.orderId = orderId;
        this.ticketId = ticketId;
        this.interfaceType = interfaceType;
        this.trainorderNonRefundable = trainorderNonRefundable;
    }

    public void run() {
        //查询类型
        if (interfaceType == 0) {
            interfaceType = new TrainInterfaceType().getTrainInterfaceType(orderId);
        }
        //类型正确
        if (interfaceType > 1) {
            //回调参数
            String url = trainorderNonRefundable + "?trainorderid=" + orderId + "&ticketid=" + ticketId
                    + "&interfacetype=" + interfaceType + "&reason=" + reason + "&responseurl="
                    + trainorderNonRefundable;
            //请求接口
            RequestUtil.get(url, AccountSystem.UTF8, AccountSystem.NullMap, 0);
        }
    }

}