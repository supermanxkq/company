package com.ccservice.b2b2c.atom.refund.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

public class TrainChangeSpreadRefundHandle implements RefundHandle {
    Log log = LogFactory.getLog(TrainChangeSpreadRefundHandle.class);

    @Override
    public void refundedHandle(boolean success, long orderid, String batchno) {
        AirticketPaymentrecord record = Server.getInstance().getB2BAirticketService().findAirticketPaymentrecord(
                orderid);
        Trainorderrc rc = new Trainorderrc();
        String rsql = "";
        int tempintisupdateywtype = 0;
        if (success) {
            rc.setContent("差价退还成功。退款金额：" + record.getTradeprice() + ",退款方式："
                    + Paymentmethod.getPaymethod(Paymentmethod.EBANKPAY) + ",退款批次号：" + batchno);
            rsql += "UPDATE T_AIRTICKETPAYMENTRECORD SET C_TRADENO=" + batchno + ",C_STATUS=" + 3 + ",C_ORDERID="
                    + record.getOrderid() + " where ID=" + record.getId() + ";";
        }
        else {
            rc.setContent("差价退还失败");
            rsql += "UPDATE T_AIRTICKETPAYMENTRECORD SET C_TRADENO=" + batchno + ",C_STATUS=" + 4 + ",C_ORDERID="
                    + record.getOrderid() + " where ID=" + record.getId() + ";";
        }
        try {
            rc.setYwtype(2);
        }
        catch (Exception e) {
            tempintisupdateywtype = 1;
            log.equals(e);
        }
        rc.setOrderid(record.getOrderid());
        rc.setCreateuser("");
        rc = Server.getInstance().getTrainService().createTrainorderrc(rc);
        if (tempintisupdateywtype == 1) {
            rsql += "UPDATE T_TRAINORDERRC SET C_YWTYPE=2 where C_ORDERID=" + rc.getOrderid() + " and C_CONTENT='"
                    + rc.getContent() + "';";
            WriteLog.write("TrainSpreadRefundHandle", rsql);
        }
        Server.getInstance().getSystemService().excuteSysconfigBySql(rsql);

    }
}