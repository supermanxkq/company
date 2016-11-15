package com.ccservice.b2b2c.atom.refund.handle;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.ben.Orderchange;

public class OrderchangerefuseRefundHandle implements RefundHandle {

    @Override
    public void refundedHandle(boolean success, long orderid, String batchno) {

        Orderchange change = Server.getInstance().getB2BAirticketService().findOrderchange(orderid);
        if (change.getChangestate() == 10) {
            int changestue = 11;
            String memo = "退款成功.退款批次号:" + batchno;
            if (!success) {
                changestue = 12;
                memo = "退款失败.退款批次号:" + batchno + ",请联系财务线下退款";
            }
            String sql = "UPDATE T_ORDERCHANGE SET C_CHANGESTATE=" + changestue + " WHERE ID=" + change.getId();
            WriteLog.write("支付宝退款", "退款成功：" + sql);
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            Orderinforc rc = new Orderinforc();
            rc.setChangeid(change.getId());
            rc.setContent(memo);
            rc.setCustomeruserid(0l);
            rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
            try {
                Server.getInstance().getAirService().createOrderinforc(rc);
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
