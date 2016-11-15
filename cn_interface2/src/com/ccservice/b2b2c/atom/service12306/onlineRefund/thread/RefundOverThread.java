package com.ccservice.b2b2c.atom.service12306.onlineRefund.thread;

import com.ccservice.b2b2c.atom.service12306.onlineRefund.method.RefundTicketMethod;

public class RefundOverThread extends Thread {

    private long orderId;

    private long ticketId;

    public RefundOverThread(long orderId, long ticketId) {
        this.orderId = orderId;
        this.ticketId = ticketId;
    }

    public void run() {
        new RefundTicketMethod().refundOver(orderId, ticketId);
    }

}