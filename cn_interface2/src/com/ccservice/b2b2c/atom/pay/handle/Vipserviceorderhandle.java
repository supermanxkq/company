package com.ccservice.b2b2c.atom.pay.handle;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;

public class Vipserviceorderhandle implements PayHandle {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        try {
            logger.info("贵宾服务订单：" + ordernumber + "已成功支付!");
            String sql = "update t_vipserviceorder set c_state = 1 where c_ordernumber = " + ordernumber;
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            logger.info("贵宾服务订单：" + ordernumber + "支付失败,异常信息是：" + e.getMessage());
        }

    }

}
