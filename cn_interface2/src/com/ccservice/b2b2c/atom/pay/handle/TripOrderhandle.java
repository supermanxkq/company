package com.ccservice.b2b2c.atom.pay.handle;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;

public class TripOrderhandle implements PayHandle {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        try {
            logger.info("旅游订单：" + ordernumber + "已成功支付!");
            String sql = "update T_TRIPORDER set C_STATE = 2,C_PAYSTATUS=1 where C_CODE = " + ordernumber;
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            logger.info("旅游订单订单：" + ordernumber + "支付失败,异常信息是：" + e.getMessage());
        }
    }

}
