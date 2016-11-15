package com.ccservice.b2b2c.atom.service12306.offlineRefund.changeRefund.thread;

import java.util.List;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.service12306.ChangeReturnTicketService;

/**
 * 改签退成功或失败后的退票线程
 * @author WH
 * @time 2016年3月23日 上午11:28:07
 * @version 1.0
 */
public class ChangeRefundThread extends Thread {

    private long trainOrderId;//订单ID

    private List<Long> ticketIdList;//退票ID集合

    private ChangeReturnTicketService service;

    public ChangeRefundThread(long trainOrderId, List<Long> ticketIdList) {
        this.trainOrderId = trainOrderId;
        this.ticketIdList = ticketIdList;
        this.service = new ChangeReturnTicketService();
    }

    public void run() {
        for (Long ticketId : ticketIdList) {
            try {
                service.operate(trainOrderId, ticketId, true);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("12306_GT_火车票改签退_Exception", e, "Refund_" + trainOrderId + "_"
                        + ticketId);
            }
        }
    }

}