package com.ccservice.b2b2c.atom.refund.handle;

import java.sql.Timestamp;
import java.util.List;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

public class Insurrefundhandle implements RefundHandle {

    @Override
    public void refundedHandle(boolean success, long orderid, String batchno) {
        Insurorder insurorder = Server.getInstance().getAirService().findInsurorder(orderid);
        WriteLog.write("保险退款", insurorder.getOrderno() + "退款通知：" + batchno);
        if (insurorder.getPaystatus() == 1) {
            float chajia = 0.0f;
            String where = " where c_orderid=" + insurorder.getId();
            List<Insuruser> users = Server.getInstance().getAirService().findAllInsuruser(where, "", -1, 0);
            int insurcount = users.size();//保险数量
            int failcount = 0;
            for (Insuruser insuruser : users) {
                if (insuruser.getPolicyno() == null) {
                    failcount++;
                }
            }
            if (failcount > 0) {
                chajia = (float) (insurorder.getTotalmoney() / insurcount) * failcount;
            }
            AirticketPaymentrecord payment = new AirticketPaymentrecord();
            payment.setOrderid(insurorder.getId());
            payment.setYwtype(6);
            payment.setTradeprice(chajia);
            payment.setPaymethod(Paymentmethod.EBANKPAY);
            payment.setStatus(3);
            payment.setTradetype(AirticketPaymentrecord.REFUNDSPREAD);
            payment.setTradetime(new Timestamp(System.currentTimeMillis()));

            payment = Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
            insurorder.setPaystatus(2l);
            Server.getInstance().getAirService().updateInsurorderIgnoreNull(insurorder);
        }
    }
}
