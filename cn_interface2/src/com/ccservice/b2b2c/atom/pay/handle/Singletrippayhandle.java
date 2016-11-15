package com.ccservice.b2b2c.atom.pay.handle;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Singletripapply;

public class Singletrippayhandle implements PayHandle {

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        Singletripapply apply = Server.getInstance().getSingleTripInService()
                .findSingletripapply(Long.valueOf(ordernumber));
        if (apply != null) {
            String sql = "UPDATE T_SINGLEAPPLY SET C_APPLYSTATE=2 WHERE ID=" + ordernumber;
            sql += ";UPDATE T_PASSENGER SET C_FET=" + apply.getId() + " WHERE ID IN(" + apply.getPidstr() + ")";
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
    }

}
