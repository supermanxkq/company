package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;

public class SingletripHandle implements PayHandle {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        logger.info("××交易成功订单处理：" + ordernumber); //订单数量
        try {
            logger.info("");
            String sql = "UPDATE T_SINGLETRIPGRANT SET C_STATE=10 WHERE ID=" + ordernumber;
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);

            String succpay = "UPDATE T_SINGLETRIP SET C_USETIME='" + new Timestamp(System.currentTimeMillis())
                    + "',C_PAYTYPE='网上支付',C_STATE=2 WHERE C_GRANTID=" + ordernumber;

            Server.getInstance().getSystemService().findMapResultBySql(succpay, null);

        }
        catch (Exception e) {
            logger.info("支付成功交易信息修改失败，异常信息:" + e.getMessage());
        }
    }

}
