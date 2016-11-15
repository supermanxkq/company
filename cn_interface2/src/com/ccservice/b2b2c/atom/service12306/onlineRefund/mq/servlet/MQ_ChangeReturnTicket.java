package com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.util.ChangeReturnTicketMQUtil;

@SuppressWarnings("serial")
public class MQ_ChangeReturnTicket extends HttpServlet {

    @Override
    public void init() throws ServletException {
        //super
        super.init();
        //开启退票
        if ("1".equals(this.getInitParameter("open"))) {
            //打印
            System.out.println("退票MQ-----开启");
            //读配置
            int MqCount = Integer.valueOf(this.getInitParameter("MqCount"));
            //要加消费者
            if (MqCount > 0) {
                //读配置
                String MqName = this.getInitParameter("MqName");
                String MqAddress = this.getInitParameter("MqAddress");
                //加消费者
                try {
                    ChangeReturnTicketMQUtil.addMessageConsumer(MqAddress, MqName, MqCount);
                }
                catch (Exception e) {
                    System.out.println("退票MQ-----加" + MqCount + "个消费者异常-----" + e.getMessage());
                }
            }
        }
    }

}