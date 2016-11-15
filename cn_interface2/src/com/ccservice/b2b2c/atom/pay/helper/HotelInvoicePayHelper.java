package com.ccservice.b2b2c.atom.pay.helper;

import java.math.BigDecimal;

import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.pay.handle.HotelInvoiceReturnHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelinvoice.HotelInvoice;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;

/**
 * 酒店补开发票
 * @author WH
 */
public class HotelInvoicePayHelper extends Payhelperframework implements Payhelper {

    private Hotelorder hotelOrder;

    private HotelInvoice hotelInvoice;

    public HotelInvoicePayHelper(long orderid) throws Exception {
        super(orderid);
        this.hotelOrder = Server.getInstance().getHotelService().findHotelorder(orderid);
        this.hotelInvoice = Server.getInstance().getHotelService()
                .findHotelInvoice(hotelOrder.getInvoiceid().longValue());
        if (hotelInvoice.getOrderid().longValue() != orderid) {
            throw new Exception("发票信息与订单信息对应不上！");
        }
    }

    //通知处理程序
    public String getHandleName() {
        return HotelInvoiceReturnHandle.class.getSimpleName();
    }

    //返回订单描述
    public String getOrderDescription() {
        return "订单" + hotelOrder.getOrderid() + "补开发票";
    }

    //返回订单订单名称
    public String getOrdername() {
        return "订单号：" + hotelOrder.getOrderid();
    }

    //获得订单号
    public String getOrdernumber() {
        return "HOTELINVOICE-" + hotelInvoice.getId();
    }

    //高开税费+快递费
    public double getOrderprice() {
        //税费
        double moneytax = 0d;
        if (hotelInvoice.getAddmoneyflag() != null && hotelInvoice.getAddmoneyflag().intValue() == 1) {
            moneytax = new BigDecimal(String.valueOf(hotelInvoice.getMoneytax())).doubleValue();
        }
        //快递费
        double postprice = 0d;
        if (hotelInvoice.getPosttype() != null && hotelInvoice.getPosttype().longValue() == 1) {
            postprice = hotelInvoice.getPostprice().doubleValue();
        }
        double tempprice = ElongHotelInterfaceUtil.add(postprice, moneytax);
        return Float.parseFloat(Double.toString(tempprice));
    }

    //商品展示路径
    public String getShwourl() {
        return "";
    }

    //交易号
    public String getTradeno() {
        return hotelInvoice.getTradenum();
    }

    //酒店交易代码：2
    public int getTradetype() {
        return 2;
    }

}
