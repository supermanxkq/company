package com.ccservice.b2b2c.atom.service12306.onlineRefund.method;

import java.util.Map;
import java.util.List;
import java.util.concurrent.Executors;
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.ExecutorService;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.service12306.ChangeReturnTicketService;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.thread.CheckRefundThread;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.thread.RefusedRefundThread;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.util.ReturnTicketDataManage;

/**
 * 退票方法
 * @author WH
 * @time 2016年11月4日 上午10:32:23
 * @version 1.0
 */

public class RefundTicketMethod {

    /**
     * 拒绝退票
     */
    public void refusedRefund(String callBackUrl) {
        try {
            //线程
            ExecutorService pool = Executors.newSingleThreadExecutor();
            //执行
            pool.execute(new RefusedRefundThread(callBackUrl));
            //关闭
            pool.shutdown();
        }
        catch (Exception e) {
        }
    }

    /**
     * 退票结束
     * @author WH
     * @time 2016年11月4日 上午11:45:13
     * @version 1.0
     */
    public void refundOver(long orderId, long ticketId) {
        //下一个
        JSONObject nextTicket = ReturnTicketDataManage.ticketEndRefund(orderId, ticketId);
        //还有退票、继续处理
        if (nextTicket.containsKey("ticketId")) {
            refundOperate(orderId, nextTicket.getLongValue("ticketId"), nextTicket);
        }
    }

    /**
     * 退票处理
     * @author WH
     * @time 2016年11月4日 上午10:32:52
     * @version 1.0
     * @param orderId 订单ID
     * @param ticketId 车票ID
     * @param ticket_no 车票号码，记录日志用
     */
    @SuppressWarnings("rawtypes")
    public void refundOperate(long orderId, long ticketId, JSONObject data) {
        //错误
        boolean error = true;
        //捕捉
        try {
            //查询
            String querySql = "select ISNULL(C_STATUS, 0) C_STATUS, ISNULL(C_REFUNDTYPE, 0) C_REFUNDTYPE, "
                    + "ISNULL(C_ISAPPLYTICKET, 0) C_ISAPPLYTICKET, ISNULL(C_ISQUESTIONTICKET, 0) C_ISQUESTIONTICKET "
                    + "from T_TRAINTICKET with(nolock) where ID = " + ticketId;
            //结果
            List list = Server.getInstance().getSystemService().findMapResultBySql(querySql, null);
            //唯一
            if (list != null && list.size() == 1) {
                //第一个
                Map map = (Map) list.get(0);
                //车票状态
                int status = Integer.parseInt(map.get("C_STATUS").toString());
                //退票状态
                int refundType = Integer.parseInt(map.get("C_REFUNDTYPE").toString());
                //退票类型
                int isApplyTicket = Integer.parseInt(map.get("C_ISAPPLYTICKET").toString());
                //退票问题
                int isQuestionTicket = Integer.parseInt(map.get("C_ISQUESTIONTICKET").toString());
                //状态校验
                if (status == Trainticket.REFUNDROCESSING && isApplyTicket == 1 && refundType == 0
                        && isQuestionTicket == 0) {
                    //更新
                    String updateSql = "update T_TRAINTICKET set C_REFUNDTYPE = -1 where ID = " + ticketId
                            + " and C_STATUS = " + status;
                    //成功
                    if (Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) > 0) {
                        try {
                            //请求数据
                            String ticket_no = data.getString("ticket_no");
                            boolean retryRefund = data.getBooleanValue("retryRefund");
                            //日志内容
                            String content = "开始异步处理退票，票号：[" + ticket_no + "]";
                            //操作记录
                            TongchengSupplyMethod.createtrainorderrc(1, content, orderId, ticketId, status, "退票系统");
                            //退票处理
                            new ChangeReturnTicketService().mq(orderId, ticketId, false, retryRefund);
                            //没有出错
                            error = false;
                        }
                        catch (Exception e) {
                            //申请状态
                            int applyStatus = Trainticket.APPLYTREFUND;
                            //还原SQL
                            updateSql = "update T_TRAINTICKET set C_REFUNDTYPE = 0, C_STATUS = " + applyStatus
                                    + " where ID = " + ticketId + " and C_STATUS = " + status;
                            //更新车票
                            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
                        }
                    }
                }
            }
        }
        catch (Exception e) {

        }
        //有错
        finally {
            if (error) {
                refundOver(orderId, ticketId);
            }
        }
    }

    /**
     * 退票审核
     * @author WH
     * @time 2016年11月4日 上午11:29:18
     * @version 1.0
     */
    public void refundCheck(long orderId, long ticketId, Customeruser user, boolean retryRefund, int random) {
        //审核
        try {
            //线程
            ExecutorService pool = Executors.newSingleThreadExecutor();
            //执行
            pool.execute(new CheckRefundThread(orderId, ticketId, user, retryRefund, random));
            //关闭
            pool.shutdown();
        }
        catch (Exception e) {
        }
        //结束
        finally {
            refundOver(orderId, ticketId);
        }
    }

}