package com.ccservice.b2b2c.atom.pay.handle;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.carorder.Carorder;
import com.ccservice.b2b2c.base.service.ISystemService;

public class CarpayHandle implements PayHandle {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        // TODO Auto-generated method stub
        logger.info("租车交易成功订单处理：");
        ISystemService service = Server.getInstance().getSystemService();
        try {
            logger.info("订单" + ordernumber + "交易成功！");
            String sql = "UPDATE T_CARORDER SET C_PAY=1 WHERE C_ORDERNUMBER='" + ordernumber.trim() + "'";
            service.findMapResultBySql(sql, null);
            //取得联系人手机号码
            try {
                java.util.List<Carorder> listorder = Server.getInstance().getCarService()
                        .findAllCarorder(" WHERE " + Carorder.COL_code + "='" + ordernumber + "'", "", -1, 0);
                Carorder carorder = new Carorder();
                if (listorder.size() > 0) {
                    String[] mobiles = { carorder.getLinkmobile() };
                    String content = "尊敬的客户您好，您的租车订单：" + ordernumber + ",已经支付成功。";
                    //Server.getInstance().getAtomService().sendSms(mobiles, content, ordernumber, null);
                }
            }
            catch (Exception ex) {
                System.out.println("支付成功后发送短信失败，异常信息:" + ex.getMessage());
            }

        }
        catch (Exception e) {
            logger.info("订单修改失败：", e.fillInStackTrace());
            e.printStackTrace();
        }
    }

}
