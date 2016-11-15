package com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.listener;

import java.util.Random;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.MessageListener;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.method.RefundTicketMethod;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.util.ReturnTicketDataManage;

/**
 * 退票处理MQ
 * @author WH
 * @time 2016年11月1日 下午4:58:39
 * @version 1.0
 */

public class ChangeReturnTicketMessageListener implements MessageListener {

    //是否使用
    private boolean isUse = false;

    //日志名称
    private static final String logName = "12306_MQ处理退票";

    public void onMessage(Message message) {
        //随机
        int random = 0;
        //通知
        String notice = "";
        //结果
        String result = "";
        //处理
        try {
            //使用
            this.isUse = true;
            //通知
            notice = ((TextMessage) message).getText();
            //随机
            random = new Random().nextInt(9000000) + 1000000;
            //数据
            JSONObject data = JSONObject.parseObject(notice);
            //取值
            long orderId = data.getLongValue("orderId");
            long ticketId = data.getLongValue("ticketId");
            //内部重试、重置问题失败
            if (data.getBooleanValue("retryRefund") && !resetQuestion(ticketId)) {
                result = "重置失败";
            }
            //开始退票
            else if (ReturnTicketDataManage.ticketStartRefund(orderId, ticketId, data)) {
                //标识
                result = "开始处理";
                //退票处理
                new RefundTicketMethod().refundOperate(orderId, ticketId, data);
            }
            else {
                result = "等待处理";
            }
        }
        catch (Exception e) {
            result = e.getMessage();
        }
        //结束
        finally {
            //标识
            this.isUse = false;
            //日志
            WriteLog.write(logName, random + "--" + notice + "--" + result);
        }
    }

    /**
     * 重置问题退票
     * @author WH
     * @time 2016年11月11日 下午5:45:54
     * @version 1.0
     * @return 是否重置成功
     */
    private boolean resetQuestion(long ticketId) {
        //SQL
        String sql = "update T_TRAINTICKET set C_ISQUESTIONTICKET = " + Trainticket.NOQUESTION + " where ID = "
                + ticketId + " and C_STATUS = " + Trainticket.REFUNDROCESSING;
        //重置成功
        return Server.getInstance().getSystemService().excuteAdvertisementBySql(sql) > 0;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean isUse) {
        this.isUse = isUse;
    }

}