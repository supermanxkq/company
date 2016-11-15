package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorderrc.Hotelorderrc;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * 
 * @author wzc
 * 补款支付后处理类
 *
 */
public class HotelorderreturnmoreHandle implements PayHandle {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        logger.info("××酒店订单补款交易成功订单处理：" + ordernumber);
        try {

            String sql = "select * from T_AIRTICKETPAYMENTRECORD where C_YWTYPE=2 and C_TRADETYPE=2 and C_STATUS=0 and id="
                    + ordernumber + " order by ID asc";
            List<AirticketPaymentrecord> records = Server.getInstance().getB2BSystemService()
                    .findAllPaymentrecordBySql(sql);
            if (records.size() > 0) {

                AirticketPaymentrecord record = records.get(0);
                record.setPaymethod(Paymentmethod.EBANKPAY);
                record.setStatus(1);
                record.setTradeno(tradeno);
                record.setTradetime(new Timestamp(System.currentTimeMillis()));
                Server.getInstance().getB2BAirticketService().updateAirticketPaymentrecord(record);

                Hotelorder hotelorder = Server.getInstance().getHotelService()
                        .findHotelorder(Long.valueOf(record.getOrderid()));
                if (hotelorder != null) {
                    logger.info("订单" + hotelorder.getOrderid() + "补款交易成功！");

                    Hotelorderrc rc = new Hotelorderrc();
                    rc.setOrderid(hotelorder.getOrderid());
                    rc.setHandleuser(hotelorder.getMemberid() + "");
                    rc.setState(1);
                    rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    rc.setContent("客户完成补款，外部交易号：" + tradeno + ",支付金额：" + payprice);
                    Server.getInstance().getHotelService().createHotelorderrc(rc);
                }

            }
        }
        catch (Exception e) {
            logger.info("支付成功交易信息修改失败，异常信息:" + e.getMessage());
            e.printStackTrace();
        }
    }

}
