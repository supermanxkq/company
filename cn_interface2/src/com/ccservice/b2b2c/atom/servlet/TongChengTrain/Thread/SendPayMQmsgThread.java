/**
 * 
 */
package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.MQ.MQMethod;
import com.ccservice.b2b2c.atom.servlet.MQ.TrainpayMqMSGUtil;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 确认出票修改为线程的扔消息
 * @time 2015年12月29日 上午10:55:04
 * @author chendong
 */
public class SendPayMQmsgThread extends Thread {
    Trainorder order;

    String orderid;

    /**
     * @param order
     * @param orderid
     */
    public SendPayMQmsgThread(Trainorder order, String orderid) {
        this.order = order;
        this.orderid = orderid;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        super.run();
        try {
            Long l2 = System.currentTimeMillis();
            new TrainpayMqMSGUtil(MQMethod.ORDERPAY_NAME).sendPayMQmsg(this.order, 1, 0);
            WriteLog.write("TongChengConfirmTrain", this.orderid + ":确认出票[mq耗时:" + (System.currentTimeMillis() - l2)
                    + "]:" + this.order.getOrdernumber());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
