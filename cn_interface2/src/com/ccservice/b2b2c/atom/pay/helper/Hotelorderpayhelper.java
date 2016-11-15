package com.ccservice.b2b2c.atom.pay.helper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.pay.handle.HotelorderreturnHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.hotelinvoice.HotelInvoice;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorderrc.Hotelorderrc;
import com.ccservice.b2b2c.base.service.IHotelService;

/**
 * 
 * @author 王战朝
 * 
 */
public class Hotelorderpayhelper extends Payhelperframework implements Payhelper {

    public Hotelorderpayhelper(long orderid) throws Exception {
        super(orderid);

        this.hotelOrder = this.getHotelService().findHotelorder(orderid);
        Hotelorderrc rc = new Hotelorderrc();
        rc.setContent("客户开始支付……");
        rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
        rc.setOrderid(hotelOrder.getOrderid());
        rc.setHandleuser(hotelOrder.getMemberid().toString());
        rc.setLanguage(0);
        Server.getInstance().getHotelService().createHotelorderrc(rc);
        //		if(hotelOrder.getState()==7){
        //			throw new Exception("供应商已确认无房……");
        //		}else if(hotelOrder.getState()==2){
        //			throw new Exception("已支付完成，重复支付…");
        //		}
    }

    private IHotelService service;

    private Hotelorder hotelOrder;

    private List<Guest> listguest;

    private String ordername = null;

    /**
     * 通知处理程序
     */
    @Override
    public String getHandleName() {
        return HotelorderreturnHandle.class.getSimpleName();

    }

    // 返回订单描述
    @Override
    public String getOrderDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getOrdername());
        sb.append(",酒店名称：" + hotelOrder.getName());
        sb.append(",房型名称：" + hotelOrder.getRoomtypename());
        sb.append(",总价：" + getOrderprice());
        sb.append(",房型数量：" + hotelOrder.getPrerooms());
        return sb.toString();
    }

    // 返回订单订单名称
    @Override
    public String getOrdername() {
        if (ordername == null) {
            hotelOrder = this.getHotelorder();
            StringBuilder sb = new StringBuilder();
            sb.append("订单号：" + hotelOrder.getOrderid());
            return sb.toString();
        }
        else {
            return ordername;
        }
    }

    /**
     * 获得订单号
     */
    @Override
    public String getOrdernumber() {
        return this.getHotelorder().getOrderid();
    }

    /**
     * 返回订单总价
     */
    @Override
    public double getOrderprice() {
        //订单
        Hotelorder order = this.getHotelorder();
        if (order.getState() == 1 || order.getState() == 4) {//待确认和预订成功等待支付
            float pricesum = Float.parseFloat(order.getPrice());
            //百达、B2C、预付、四月份
            if (order != null && order.getCreateuserid() != null && order.getCreateuserid().longValue() == 2054
                    && order.getType() != null && order.getType().intValue() == 1 && "0".equals(order.getOrdertype())
                    && order.getPaytype() != null && order.getPaytype().longValue() == 2 && order.getPretime() != null
                    && order.getPretime().toString().startsWith("2014-04")) {
                double tempprcie = order.getYufuprice() == null ? 0 : order.getYufuprice().doubleValue();
                pricesum = Float.parseFloat(Double.toString(ElongHotelInterfaceUtil.subtract(
                        Double.parseDouble(order.getPrice()), tempprcie)));
            }
            //发票
            Long InvoiceId = order.getInvoiceid();
            if (InvoiceId != null && InvoiceId.longValue() > 0) {
                HotelInvoice hotelInvoice = Server.getInstance().getHotelService().findHotelInvoice(InvoiceId);
                if (hotelInvoice != null) {
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
                    if (moneytax > 0 || postprice > 0) {
                        double tempprice = ElongHotelInterfaceUtil.add(postprice, moneytax);
                        tempprice = ElongHotelInterfaceUtil.add(tempprice, pricesum);
                        pricesum = Float.parseFloat(Double.toString(tempprice));
                    }
                }
            }
            return pricesum;
        }
        else {
            return 0.0f;
        }

    }

    /**
     * 商品展示路径
     */
    @Override
    public String getShwourl() {
        return "";
    }

    // 酒店交易代码：2
    @Override
    public int getTradetype() {
        return 2;
    }

    private IHotelService getHotelService() {
        if (this.service != null) {
            return this.service;
        }
        else {
            return service = Server.getInstance().getHotelService();
        }
    }

    // 获取关联订单对象
    private Hotelorder getHotelorder() {
        return this.hotelOrder;
    }

    public List<Guest> getGuests() {
        if (listguest == null) {
            hotelOrder = this.getHotelorder();
            List listguest = Server
                    .getInstance()
                    .getHotelService()
                    .findAllGuest(" WHERE 1=1 AND " + Guest.COL_orderid + " =" + this.getHotelorder().getId(),
                            " ORDER BY ID ", -1, 0);
            if (listguest.size() > 0) {
                return listguest;
            }
        }
        return listguest;
    }

    @Override
    public String getTradeno() {
        return this.hotelOrder.getTradenum();
    }

}
