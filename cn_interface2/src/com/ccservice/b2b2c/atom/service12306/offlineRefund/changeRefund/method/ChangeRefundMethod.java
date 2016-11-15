package com.ccservice.b2b2c.atom.service12306.offlineRefund.changeRefund.method;

import java.util.Random;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengReqChange;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengConfirmChange;

/**
 * 改签退方法
 * @author WH
 * @time 2016年3月21日 下午2:15:39
 * @version 1.0
 */

public class ChangeRefundMethod {

    /**
     * 申请改签
     * @param train 改签退筛选的车次
     * @param refundTicket 第一个退票返回信息
     *          |-->{"ticket_price":21.5,"return_price":17,"msg":"不直接退票，获取退票手续费成功","success":false,"return_cost":4.5}
     */
    public boolean requestChange(Trainorder order, Trainticket ticket, Trainpassenger passenger, Train train,
            String refundTicket) {
        //改签请求
        JSONObject reqobj = new JSONObject();
        //验证价格
        reqobj.put("orderType", "1");
        //判断西藏等地区不能退票
        reqobj.put("checkCanRefund", "1");
        //验证时间
        reqobj.put("compareDateTime", "1");
        //原订单信息
        reqobj.put("ordernumber", order.getExtnumber());
        reqobj.put("orderid", order.getQunarOrdernumber());
        reqobj.put("transactionid", order.getOrdernumber());
        //原车票座席
        reqobj.put("old_zwcode", ticket.getSeattype());
        //原车票信息
        JSONObject ticketObject = new JSONObject();
        ticketObject.put("old_ticket_no", ticket.getTicketno());
        ticketObject.put("passengersename", passenger.getName());
        ticketObject.put("passportseno", passenger.getIdnumber());
        ticketObject.put("piaotype", String.valueOf(ticket.getTickettype()));
        ticketObject.put("passporttypeseid", TongChengTrainUtil.localIdTypeToTongCheng(passenger.getIdtype()));
        //添加原车票
        JSONArray tickets = new JSONArray();
        tickets.add(ticketObject);
        reqobj.put("ticketinfo", tickets);
        //改签车次信息
        reqobj.put("change_price", train.getDistance());
        reqobj.put("change_checi", train.getTraincode());
        reqobj.put("change_zwcode", train.getSeattypeval());
        reqobj.put("change_datetime", train.getStartdate() + " " + train.getStarttime() + ":00");
        //原退票手续费暂存改签表
        JSONObject return_cost_object = new JSONObject();
        return_cost_object.put("return_cost", JSONObject.parseObject(refundTicket).getFloatValue("return_cost"));
        //正式环境数据库字段长度不够，移除无用的
        reqobj.put("refundTicket", return_cost_object.toString());
        //改签走异步
        reqobj.put("isasync", "Y");
        reqobj.put("reqtoken", String.valueOf(System.currentTimeMillis()));
        reqobj.put("callbackurl", PropertyUtil.getValue("changeRefundCallBackUrl"));
        //改签退请求
        String syncResult = "";
        //捕捉异常
        try {
            syncResult = new TongChengReqChange().RequestCheck(order, reqobj, 1);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("12306_GT_火车票改签退_Exception", e, "RequestChange_" + order.getId() + "_"
                    + ticket.getId());
        }
        //解析请求结果，并返回
        return syncResult != null && syncResult.contains("改签请求已接受")
                && JSONObject.parseObject(syncResult).getBooleanValue("success");
    }

    /**
     * 确认改签
     * @param orderid 接口订单号
     * @param changeId 改签表ID
     * @param transactionid 系统订单号
     */
    public boolean confirmChange(String transactionid, String orderid, long changeId) {
        //确认请求
        JSONObject reqobj = new JSONObject();
        //改签退
        reqobj.put("changeType", 1);
        //确认参数
        reqobj.put("orderid", orderid);
        reqobj.put("changeid", changeId);
        reqobj.put("transactionid", transactionid);
        //确认走异步
        reqobj.put("isasync", "Y");
        reqobj.put("reqtoken", String.valueOf(System.currentTimeMillis()));
        reqobj.put("callbackurl", PropertyUtil.getValue("changeRefundCallBackUrl"));
        //确认改签请求
        String syncResult = "";
        //捕捉异常
        try {
            syncResult = new TongChengConfirmChange().operate(reqobj, new Random().nextInt(900000) + 100000);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("12306_GT_火车票改签退_Exception", e, "ConfirmChange_" + changeId);
        }
        //解析请求结果，并返回
        return syncResult != null && syncResult.contains("确认请求已接受")
                && JSONObject.parseObject(syncResult).getBooleanValue("success");
    }

}