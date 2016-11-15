package com.ccservice.b2b2c.atom.service12306.onlineRefund.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainticket;

/**
 * 线上退票工具类
 * @author WH
 * @time 2016年11月3日 下午2:50:29
 * @version 1.0
 */

public class OnlineRefundUtil {

    /**
     * 同程平台 
     */
    public static boolean istc() {
        return "tc".equals(PropertyUtil.getValue("default_pingtaiStr", "Train.properties"));
    }

    /**
     * 退票走MQ
     * @param isApplyTicket 退票类型，1:线上；2:线下
     * @param RefundTicketByMq 退票走MQ，0:关；其他:开
     */
    public boolean gomq(int isApplyTicket, String refundTicketByMq) {
        //走MQ
        boolean gomq = false;
        //同程线上
        if (isApplyTicket == 1 && !"0".equals(refundTicketByMq) && istc()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                //当前
                Date current = sdf.parse(sdf.format(new Date()));
                //6点到23点
                gomq = current.after(sdf.parse("06:00:00")) && current.before(sdf.parse("23:00:00"));
            }
            catch (Exception e) {

            }
        }
        //返回结果
        return gomq;
    }

    /**
     * 退票走MQ
     */
    public void activeMQRefundTicket(long orderId, long ticketId, String departTime, String ticket_no) {
        //捕捉异常
        try {
            //请求数据
            JSONObject data = new JSONObject();
            //设置数据
            data.put("orderId", orderId);
            data.put("ticketId", ticketId);
            data.put("ticket_no", ticket_no);
            data.put("requestTime", System.currentTimeMillis());
            data.put("departTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(departTime).getTime());
            //MQ地址
            String MQ_URL = PropertyUtil.getValue("activeMQ_url", "Train.properties");
            //发送消息
            ActiveMQUtil.sendMessage(MQ_URL, "QueueMQ_TrainTicket_RefundTicket", data.toString());
        }
        //还原为申请退票，走JOB
        catch (Exception e) {
            //新状态
            int newStatus = Trainticket.APPLYTREFUND;
            //老状态
            int oldStatus = Trainticket.REFUNDROCESSING;
            //还原SQL
            String updateSql = "update T_TRAINTICKET set C_STATUS = " + newStatus + " where ID = " + ticketId
                    + " and C_STATUS = " + oldStatus + " and C_REFUNDTYPE = 0";
            //更新车票
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
        }
    }

    /**
     * 内部重试
     * @param retryRefund true:内部重试
     */
    public void retryRefund(long orderId, long ticketId, String ticket_no, String departTime, String requestTime,
            boolean retryRefund) {
        try {
            //请求数据
            JSONObject data = new JSONObject();
            //设置数据
            data.put("orderId", orderId);
            data.put("ticketId", ticketId);
            data.put("ticket_no", ticket_no);
            data.put("retryRefund", retryRefund);
            data.put("departTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(departTime).getTime());
            data.put("requestTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(requestTime).getTime());
            //MQ地址
            String MQ_URL = PropertyUtil.getValue("activeMQ_url", "Train.properties");
            //发送消息
            ActiveMQUtil.sendMessage(MQ_URL, "QueueMQ_TrainTicket_RefundTicket", data.toString());
        }
        catch (Exception e) {

        }
    }

}