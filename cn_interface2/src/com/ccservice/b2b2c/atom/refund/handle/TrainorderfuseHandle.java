package com.ccservice.b2b2c.atom.refund.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.Paymentmethod;

public class TrainorderfuseHandle implements RefundHandle {
    Log log = LogFactory.getLog(TrainorderfuseHandle.class);

    @Override
    public void refundedHandle(boolean success, long orderid, String batchno) {
        Trainorder order = new Trainorder();
        order.setId(orderid);
        order.setOrderstatus(Trainorder.REFUSED);
        Server.getInstance().getTrainService().updateTrainorder(order);
        Trainorderrc rc = new Trainorderrc();
        rc.setContent("拒单退款成功。退款方式：" + Paymentmethod.getPaymethod(Paymentmethod.EBANKPAY) + ",退款批次号：" + batchno);
        rc.setOrderid(orderid);
        int tempintisupdateywtype=0;
        try {
            rc.setYwtype(1);
        }
        catch (Exception e) {
            log.error(e);
            tempintisupdateywtype=1;
        }
        rc.setCreateuser("");
        rc = Server.getInstance().getTrainService().createTrainorderrc(rc);
        if (tempintisupdateywtype==1) {
            String sqlstring = "UPDATE T_TRAINORDERRC SET C_YWTYPE=1 where C_ORDERID=" + rc.getOrderid() + " and C_CONTENT='" + rc.getContent() + "'";
            WriteLog.write("TrainorderfuseHandle", sqlstring);
            Server.getInstance().getSystemService().excuteSysconfigBySql(sqlstring);
        }
        
    }

}
