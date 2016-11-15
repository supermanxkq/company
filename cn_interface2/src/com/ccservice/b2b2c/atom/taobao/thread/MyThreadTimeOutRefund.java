package com.ccservice.b2b2c.atom.taobao.thread;

import java.util.Map;

import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.taobao.TaobaoRefundMethod;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class MyThreadTimeOutRefund extends Thread {
    @SuppressWarnings("rawtypes")
    private Map map;

    @SuppressWarnings("rawtypes")
    public MyThreadTimeOutRefund(Map map) {
        this.map = map;
    }

    public void run() {
        try {
            String TaoBaoOrderNo = map.get("TaoBaoOrderNo").toString();
            String UserId = map.get("UserId").toString();
            String SellerId = map.get("SellerId").toString();
            String RefundOrderNo = map.get("RefundOrderNo").toString();
            String TTPId = map.get("TTPId").toString();
            String AlipayTradeNo = map.get("AlipayTradeNo").toString();
            String callbackResult = null;
            callbackResult = new TaobaoHotelInterfaceUtil().refundfee(TaoBaoOrderNo, TTPId, 0, "", SellerId,
                    AlipayTradeNo, UserId, "", RefundOrderNo, 2, 5);
            if (callbackResult == null || !"SUCCESS".equalsIgnoreCase(callbackResult)) {
                TaobaoRefundMethod.getInstance().callbackResultIntoDB(AlipayTradeNo, false);
                return;
            }
            else if ("SUCCESS".equalsIgnoreCase(callbackResult)) {
                TaobaoRefundMethod.getInstance().callbackResultIntoDB(AlipayTradeNo, true);
                return;
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundMethod_deleteAll", e);
        }
    }
}
