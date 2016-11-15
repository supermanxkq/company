package com.ccservice.hotelorderinterface;

import java.util.List;

import com.ccservice.b2b2c.base.creditcard.Creditcard;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.horderdayprice.HOrderDayPrice;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelinvoice.HotelInvoice;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.component.sms.SMSType;

/**
 * 
 * @author wzc 酒店订单-酒店接口
 */
public interface IHotelOrderInterface {
    /*补开发票同步Start*/
    public void createHotelInvoice(Hotelorder hotelorder, HotelInvoice hotelInvoice, String username);

    public void updateHotelInvoicePay(Hotelorder hotelorder, HotelInvoice hotelInvoice, Integer ydxUpdateCustomer);

    public void syncHotelInvoicePay(Hotelorder hotelorder, HotelInvoice hotelInvoice, Integer ydxUpdateCustomer);

    /*补开发票同步End*/

    /**
     * 订单同步接口
     * 
     * @param hotelorder
     * @param guests
     * @param orderdayprices
     * @param username
     * @return
     */
    public String createHotelOrder(Hotelorder hotelorder, List<Guest> guests, List<HOrderDayPrice> orderdayprices,
            String username, Creditcard creditcard, HotelInvoice hotelInvoice);

    //旧接口、部分接口平台未更新代码
    public String createHotelOrder(Hotelorder hotelorder, List<Guest> guests, List<HOrderDayPrice> orderdayprices,
            String username);

    /**
     * 易订行---》客户服务器 订单状态同步 systype 同步类型 1 客户端 2 服务端
     */
    public String synchotelorder(Hotelorder hotelorder, Hotel hotel, SMSType type, Long systype);

    /**
     * 更新订单
     */
    public int synchupdatehotelorderingnull(Hotelorder hotelorder, Hotel hotel, SMSType type, Long systype);

    /**
     * 虚拟账户退款
     */
    public void SysVmrecord(Hotelorder hotelorder, double rebatmoney, int xiaofeileixing, boolean vmenable);

    public void createHotelvmrecord(Hotelorder hotelorder, double rebatmoney, int xiaofeileixing, boolean vmenable);

    /**
     * 分润接口
     */
    public String HotelsharingRebate(Hotelorder hotelordere, Long type) throws Exception;

    public Hotelorder findCustomerOrder(Hotelorder hotelorder);

    public void sharingRebate(Hotelorder order, Long type) throws Exception;
}
