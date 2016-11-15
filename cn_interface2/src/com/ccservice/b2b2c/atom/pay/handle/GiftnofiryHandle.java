package com.ccservice.b2b2c.atom.pay.handle;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;

public class GiftnofiryHandle implements PayHandle {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        logger.info("××交易成功订单处理：" + ordernumber);
        logger.error("购买支付成功");

        try {
            String where = "UPDATE T_REDEEM SET C_STATE='1' WHERE ID=" + ordernumber;
            Server.getInstance().getSystemService().findMapResultBySql(where, null);
            logger.error("支付成功");
        }
        catch (Exception e) {
            // TODO: handle exception
            logger.error("购买支付失败", e.fillInStackTrace());
        }

    }

}
