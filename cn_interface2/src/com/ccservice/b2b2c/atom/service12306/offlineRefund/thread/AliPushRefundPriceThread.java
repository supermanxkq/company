package com.ccservice.b2b2c.atom.service12306.offlineRefund.thread;

import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.LocalRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.method.ApplyOfflineTicketGoOnline;

/**
 * 支付推送退款处理
 * @author WH
 * @time 2016年1月13日 下午3:56:32
 * @version 1.0
 */
public class AliPushRefundPriceThread extends Thread {

    private int random;

    //1:订单；2:改签
    private String busType;

    private String payTradeNos;

    //订单ID
    private long trainOrderId;

    //进行线上退
    private boolean onlineRefund;

    //订单或改签ID
    private long orderOrChangeId;

    private int specialFlag;//特殊标识，1：低改按退票处理，暂用于淘宝申请线下退款[ApplyOfflineTicketJob.java]

    public AliPushRefundPriceThread(String busType, String payTradeNos, long orderOrChangeId, int random,
            int specialFlag, long trainOrderId, boolean onlineRefund) {
        this.random = random;
        this.busType = busType;
        this.specialFlag = specialFlag;
        this.payTradeNos = payTradeNos;
        this.onlineRefund = onlineRefund;
        this.trainOrderId = trainOrderId;
        this.orderOrChangeId = orderOrChangeId;
    }

    public void run() {
        //无退款
        if (specialFlag == 1 && new LocalRefundUtil().aliRefundPrice(payTradeNos).size() <= 0) {
            //尝试走线上
            if (onlineRefund && trainOrderId > 0) {
                new ApplyOfflineTicketGoOnline(trainOrderId).operate();
            }
            //直接中断返回
            return;
        }
        //处理地址
        String url = PropertyUtil.getValue("AliRefundOperateUrl");
        //拼接参数
        url += "?orderId=" + orderOrChangeId + "&busType=" + busType + "&payTradeNos=" + payTradeNos + "&specialFlag="
                + specialFlag;
        //处理结果
        String operate = "";
        //处理次数
        int operateIndex = 0;
        //多次尝试
        for (int i = 0; i < 3; i++) {
            //次数
            operateIndex++;
            //请求处理
            operate = RequestUtil.get(url, AccountSystem.UTF8, AccountSystem.NullMap, 5 * 60 * 1000);
            //判断结果
            if (!ElongHotelInterfaceUtil.StringIsNull(operate)) {
                break;
            }
        }
        //结果转JSON
        JSONObject retobj = JSONObject.parseObject(operate);
        //结果为空
        if (retobj == null) {
            retobj = new JSONObject();
            retobj.put("result", "-1");
            retobj.put("success", false);
            retobj.put("operate", "处理结果为空");
        }
        //订单ID
        long orderId = retobj.containsKey("orderId") ? retobj.getLongValue("orderId") : 0;
        //记录日志
        if (orderId <= 0) {
            WriteLog.write("h火车票支付宝退款", random + " --> 第" + operateIndex + "次回复数据: " + retobj);
        }
        else {
            WriteLog.write("h火车票支付宝退款", random + " --> " + orderId + " --> 第" + operateIndex + "次回复数据: " + retobj);
        }
        //尝试走线上
        if (onlineRefund && specialFlag == 1 && trainOrderId > 0) {
            new ApplyOfflineTicketGoOnline(trainOrderId).operate();
        }
    }
}