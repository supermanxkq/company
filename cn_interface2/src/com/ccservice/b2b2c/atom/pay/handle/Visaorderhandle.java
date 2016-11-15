package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.IVisaService;
import com.ccservice.b2b2c.base.visaorder.Visaorder;

public class Visaorderhandle implements PayHandle {

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        IVisaService service = Server.getInstance().getVisaService();
        String where = " WHERE C_ORDERID='" + ordernumber + "'";
        List<Visaorder> visaorderlist = service.findAllVisa(where, " ORDER BY ID DESC ", -1, 0);
        Visaorder visaorder = visaorderlist.get(0);
        visaorder.setPaystatus(1l);
        visaorder.setOrderstatus(3l);
        try {
            service.updateVisaorderIgnoreNull(visaorder);
            String sql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
                    + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERCODE='" + ordernumber + "'";
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            String sql = "UPDATE T_VISAORDER SET C_ORDERSTATUS=4 AND C_PAYSTATUS=1 WHERE C_ORDERID='"
                    + ordernumber.trim() + "'";
            service.findAllVisaorderBySql(sql, -1, 0);
        }
    }

}
