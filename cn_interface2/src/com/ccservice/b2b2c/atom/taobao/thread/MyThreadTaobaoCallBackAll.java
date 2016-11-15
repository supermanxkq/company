package com.ccservice.b2b2c.atom.taobao.thread;

import com.ccservice.b2b2c.atom.taobao.TaobaoRefundMethod;

public class MyThreadTaobaoCallBackAll extends Thread {

    public void run() {
        callback();
    };

    private void callback() {
        while (true) {
            try {
                TaobaoRefundMethod.getInstance().deleteAll();
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                Thread.sleep(10000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
