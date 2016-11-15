package com.ccservice.b2b2c.atom.refund.handle;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.recharge.Recharge;
import com.ccservice.b2b2c.ben.Paymentmethod;

public class RechargeorderfuseHandle implements RefundHandle {

    Log log = LogFactory.getLog(RechargeorderfuseHandle.class);

    @Override
    public void refundedHandle(boolean success, long orderid, String batchno) {
        Recharge order = Server.getInstance().getMemberService().findRecharge(orderid);
        order.setState(Recharge.TOSINGLEREFUNDALREADYREFUND);
        Server.getInstance().getMemberService().updateRecharge(order);
        String sql = "insert into T_RECHARGEREFUNDRECORD(C_ORDERID,C_CREATETIME,C_CREATEUSER,C_CONTENT) values('"
                + order.getOrdernumber() + "','" + new Timestamp(System.currentTimeMillis()) + "',''," + "'" + "订单号："
                + order.getOrdernumber() + "，退款成功。退款方式：" + Paymentmethod.getPaymethod(Paymentmethod.EBANKPAY)
                + ",退款批次号：" + batchno + "')";
        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
    }
}
