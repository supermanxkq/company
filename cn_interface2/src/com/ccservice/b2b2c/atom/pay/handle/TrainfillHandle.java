package com.ccservice.b2b2c.atom.pay.handle;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

public class TrainfillHandle implements PayHandle {
    Log log = LogFactory.getLog(TrainfillHandle.class);

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        long id = Long.valueOf(ordernumber);
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(id);
        rc.setCreateuser("");
        rc.setContent("客户补款完成，支付方式:" + Paymentmethod.getPaymethod(Paymentmethod.EBANKPAY) + ",金额：" + payprice + ",交易号："
                + tradeno);
        int tempintisupdateywtype = 0;
        try {
            rc.setYwtype(2);
        }
        catch (Exception e) {
            log.error(e);
            tempintisupdateywtype = 1;
        }
        rc = Server.getInstance().getTrainService().createTrainorderrc(rc);
        if (tempintisupdateywtype == 1) {
            String sqlstring = "UPDATE T_TRAINORDERRC SET C_YWTYPE=2 where C_ORDERID=" + rc.getOrderid()
                    + " and C_CONTENT='" + rc.getContent() + "'";
            WriteLog.write("TrainfillHandle", sqlstring);
            Server.getInstance().getSystemService().excuteSysconfigBySql(sqlstring);
        }
        String sql1 = "select * from T_AIRTICKETPAYMENTRECORD  where C_YWTYPE=4 and C_STATUS=0 and C_TRADETYPE=2 and C_ORDERID="
                + id;
        List<AirticketPaymentrecord> records = Server.getInstance().getB2BSystemService()
                .findAllPaymentrecordBySql(sql1);
        AirticketPaymentrecord record = new AirticketPaymentrecord();
        for (AirticketPaymentrecord a : records) {
            record.setId(a.getId());
            record.setYwtype(4);
            record.setOrderid(id);
            record.setPaymethod(Paymentmethod.EBANKPAY);
            record.setStatus(1);
            record.setTradetype(AirticketPaymentrecord.SUBSIDY);
            record.setTradeno(tradeno);
            Server.getInstance().getB2BAirticketService().updateAirticketPaymentrecord(record);
        }
        Trainorderchange change = new Trainorderchange();
        change = Server.getInstance().getTrainService().findTrainorcerchange(id);
        Server.getInstance().getTrainService().trainfillSuccessful(id, change.getOrderid(), Paymentmethod.EBANKPAY);//修改订单状态
    }

}
