package com.ccservice.b2b2c.atom.train;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 退票以及无法退票统一方法的公用模块
 * @author fiend
 *
 */
public class TrainorderRefundMethod {
    /**
     * QUNAR是否可以做退票或者无法退票操作
     * @param ticketid
     * @param isrefund
     * @return
     * @author fiend
     */
    public boolean isCanCallbackQuanr(long orderid, long ticketid, boolean isrefund) {
        try {
            String strSP = "[dbo].[sp_TrainRefundAppoint_RefundResult] " + "@ticketid = " + ticketid + ","
                    + "@status = " + (isrefund ? 1 : 2);
            List list = Server.getInstance().getSystemService().findMapResultByProcedure(strSP);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                String result = map.get("result").toString();
                if ("1".equals(result) || "2".equals(result)) {
                    WriteLog.write("TRAINORDERREFUNDMETHOD_ISCANCALLBACKQUANR", orderid + "--->"
                            + (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,正在处理");
                    createTrainorderrc(1, orderid, (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,正在处理", "QUNAR接口",
                            Trainticket.REFUNDIING, ticketid);
                    return true;
                }
                else if ("3".equals(result)) {
                    WriteLog.write("TRAINORDERREFUNDMETHOD_ISCANCALLBACKQUANR", orderid + "--->"
                            + (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,请求不一致,转为问题订单");
                    createTrainorderrc(1, orderid, (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,请求不一致,转为问题订单", "QUNAR接口",
                            Trainticket.REFUNDIING, ticketid);
                    String sql = "UPDATE T_TRAINTICKET SET C_ISQUESTIONTICKET=1 "
                            + "WHERE ID IN (SELECT TICKETID FROM TRAINREFUNDAPPOINT WITH (NOLOCK) "
                            + "WHERE IDENTIFICATION=( SELECT TOP 1 IDENTIFICATION FROM TRAINREFUNDAPPOINT WITH (NOLOCK) "
                            + "WHERE TICKETID=" + ticketid + " ORDER BY PKID DESC ))";
                    try {
                        WriteLog.write("TRAINORDERREFUNDMETHOD_CHANGETICKET", orderid + "--->"
                                + (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,请求不一致,转为问题订单--->" + sql);
                        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    }
                    catch (Exception e) {
                        WriteLog.write("TRAINORDERREFUNDMETHOD_CHANGETICKET_ERROR", orderid + "--->" + sql);
                        ExceptionUtil.writelogByException("TRAINORDERREFUNDMETHOD_CHANGETICKET_ERROR", e);
                    }
                    return false;
                }
                else {
                    WriteLog.write("TRAINORDERREFUNDMETHOD_ISCANCALLBACKQUANR", orderid + "--->"
                            + (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,等待统一处理");
                    createTrainorderrc(1, orderid, (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,等待统一处理", "QUNAR接口",
                            Trainticket.REFUNDIING, ticketid);
                    return false;
                }
            }
            else {
                WriteLog.write("TRAINORDERREFUNDMETHOD_ISCANCALLBACKQUANR", orderid + "--->"
                        + (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,处理异常");
                createTrainorderrc(1, orderid, (isrefund ? "同意退票" : "拒绝退票") + "--->操作已接收,处理异常", "QUNAR接口",
                        Trainticket.REFUNDIING, ticketid);
                return false;
            }
        }
        catch (Exception e) {
            WriteLog.write("TRAINORDERREFUNDMETHOD_ISCANCALLBACKQUANR", orderid + "--->" + (isrefund ? "同意退票" : "拒绝退票")
                    + "--->操作接收异常");
            WriteLog.write("TRAINORDERREFUNDMETHOD_ISCANCALLBACKQUANR_ERROR", orderid + ":" + isrefund);
            ExceptionUtil.writelogByException("TRAINORDERREFUNDMETHOD_ISCANCALLBACKQUANR_ERROR", e);
            try {
                createTrainorderrc(1, orderid, (isrefund ? "同意退票" : "拒绝退票") + "--->操作接收异常", "QUNAR接口",
                        Trainticket.REFUNDIING, ticketid);
            }
            catch (Exception e1) {
                ExceptionUtil.writelogByException("TRAINORDERREFUNDMETHOD_ISCANCALLBACKQUANR_ERROR", e1);
            }
            return false;
        }
    }

    /**
     * 书写操作记录
     * 
     * @param trainorderid
     * @param content
     * @param createurser
     * @time 2015年1月21日 下午7:05:04
     * @author fiend
     */
    public void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser, int status,
            long ticketid) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(trainorderid);
        rc.setContent(content);
        rc.setStatus(status);// Trainticket.ISSUED
        rc.setCreateuser(createurser);// "12306"
        rc.setTicketid(ticketid);
        rc.setYwtype(yewutype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    public static void main(String[] args) {
        String strSP = "[dbo].[sp_TrainRefundAppoint_RefundResult] " + "@ticketid = " + 3 + "," + "@status = " + 1;
        List list = Server.getInstance().getSystemService().findMapResultByProcedure(strSP);
        Map map = (Map) list.get(0);
        String result = map.get("result").toString();
        System.out.println(result);
    }
}
