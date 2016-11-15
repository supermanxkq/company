package com.ccservice.b2b2c.atom.service12306.onlineRefund.thread;

import com.ccservice.b2b2c.atom.component.SendPostandGet;

/**
 * 拒绝退票
 * @author WH
 * @time 2016年11月10日 下午3:38:35
 * @version 1.0
 */

public class RefusedRefundThread extends Thread {

    private String callBackUrl;

    public RefusedRefundThread(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    @Override
    public void run() {
        SendPostandGet.submitGet(callBackUrl, "UTF-8");
    }

}