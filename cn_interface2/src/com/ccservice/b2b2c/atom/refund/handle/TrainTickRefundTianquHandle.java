package com.ccservice.b2b2c.atom.refund.handle;

/**
 * 天衢退款消息通知
 * 接收到退款消息更新消息状态，操作记录添加退款是否成功及批次号
 * @author wzc
 *
 */
public class TrainTickRefundTianquHandle implements RefundHandle {

    @Override
    public void refundedHandle(boolean success, long orderid, String batchno) {

    }

}
