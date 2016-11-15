package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.mateorder.Mateorder;
import com.ccservice.b2b2c.base.service.ISystemService;

public class BusinessmateHandle implements PayHandle {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @SuppressWarnings("unchecked")
    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        // TODO Auto-generated method stub
        logger.info("商旅手伴交易成功订单处理：");
        ISystemService service = Server.getInstance().getSystemService();
        try {
            logger.info("订单" + ordernumber + "交易成功！");
            String sql = "UPDATE T_MATEORDER SET C_PAYTPEY='在线支付,已完成' WHERE C_ORDERNUMBER='" + ordernumber.trim()
                    + "',C_MODIFYTIME='" + new Timestamp(System.currentTimeMillis()) + "'";
            service.findMapResultBySql(sql, null);
            //取得联系人手机号码
            try {
                java.util.List<Mateorder> listorder = Server.getInstance().getBusinessMateService()
                        .findAllMateorder(" where " + Mateorder.COL_ordernumber + "='" + ordernumber + "'", "", -1, 0);
                Mateorder mateorder = new Mateorder();
                if (listorder.size() > 0) {
                    String[] mobiles = { mateorder.getLinkmobile() };
                    String content = "尊敬的客户您好，您的商旅手伴订单：" + ordernumber + ",已经支付成功。";
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
