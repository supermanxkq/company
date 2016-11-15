package com.ccservice.b2b2c.atom.refund.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TrainticketLowChangehandle implements RefundHandle {
    Log log = LogFactory.getLog(TrainticketLowChangehandle.class);

    @Override
    public void refundedHandle(boolean success, long orderid, String batchno) {
        WriteLog.write("低改退款", success + "--->" + orderid + "--->" + batchno);
        try {
            Trainticket ticket = Server.getInstance().getTrainService().findTrainticket(orderid);
            Trainorderrc rc = new Trainorderrc();
            Trainorderchange change = Server.getInstance().getTrainService()
                    .findTrainchangeid(ticket.getTrainpassenger().getOrderid());

            if (success) {
                rc.setContent("低改退款成功。退款方式：" + Paymentmethod.getPaymethod(Paymentmethod.EBANKPAY) + ",退款批次号：" + batchno);
                Server.getInstance()
                        .getSystemService()
                        .excuteSysconfigBySql(
                                "UPDATE T_TRAINTICKET SET C_STATUS=" + Trainticket.FINISHCHANGE + " WHERE ID="
                                        + ticket.getId());
            }
            else {
                rc.setContent("低改退款失败");
                Server.getInstance()
                        .getSystemService()
                        .excuteSysconfigBySql(
                                "UPDATE T_TRAINTICKET SET C_STATUS=" + Trainticket.THOUGHCHANGE + " WHERE ID="
                                        + ticket.getId());
            }
            getTrainRefundAlipayMethod(orderid);
            int tempintisupdateywtype = 0;
            try {
                if (ticket.getTcnewprice() > 0) {
                    rc.setYwtype(2);
                    rc.setOrderid(change.getId());
                }
                else {
                    rc.setYwtype(1);
                    rc.setOrderid(ticket.getTrainpassenger().getOrderid());
                }

            }
            catch (Exception e) {
                tempintisupdateywtype = 1;
                log.equals(e);
            }

            rc.setCreateuser("");
            rc = Server.getInstance().getTrainService().createTrainorderrc(rc);
            String sqlstring = "";
            if (tempintisupdateywtype == 1) {
                if (ticket.getTcnewprice() > 0) {
                    sqlstring = "UPDATE T_TRAINORDERRC SET C_YWTYPE=2 where C_ORDERID=" + change.getId()
                            + " and C_CONTENT='" + rc.getContent() + "'";
                }
                else {
                    sqlstring = "UPDATE T_TRAINORDERRC SET C_YWTYPE=1 where C_ORDERID=" + rc.getOrderid()
                            + " and C_CONTENT='" + rc.getContent() + "'";
                }
                WriteLog.write("TrainticketRefundHandle", sqlstring);
                Server.getInstance().getSystemService().excuteSysconfigBySql(sqlstring);
            }
        }
        catch (Exception e) {
            WriteLog.write("低改退款_Exception", orderid + "");
            ExceptionUtil.writelogByException("低改退款_Exception", e);
        }

    }

    /**
     * 获取交易记录
     * 
     * @return
     * @time 2016年7月25日 下午2:48:28
     * @author fiend
     */
    private void getTrainRefundAlipayMethod(long ticketid) {
        String sql = " [sp_TrainLowChangeAlipayMethod_Delete] @Ticketid=" + ticketid;
        Server.getInstance().getSystemService().findMapResultByProcedure(sql);
    }
}
