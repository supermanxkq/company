package com.ccservice.b2b2c.atom.hotel;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.base.addvalue.Addvalue;
import com.ccservice.b2b2c.base.bookingrules.BookingRule;
import com.ccservice.b2b2c.base.creditcard.Creditcard;
import com.ccservice.b2b2c.base.garanteerule.GaranteeRule;
import com.ccservice.b2b2c.base.guest.Contacter;
import com.ccservice.b2b2c.base.guest.Fax;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.guest.Phone;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotel.HotelsResult;
import com.ccservice.b2b2c.base.hotel.IsVouchResult;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorder.OrderResult;
import com.ccservice.b2b2c.base.hotelorder.Room;
import com.ccservice.b2b2c.base.roomtype.Rate;
import com.ccservice.b2b2c.base.roomtype.RatePlan;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.base.roomtype.UseTime;
import com.ccservice.elong.base.NorthBoundAPIServiceStub;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.ArrayOfHotel;
import com.ccservice.elong.inter.ELongGetHotelInventory;
import com.ccservice.elong.inter.ELongGetHotelList;
import com.ccservice.elong.inter.ELongGetHotelOrderDetailByOrderId;
import com.ccservice.elong.inter.ELongGetHotelOrderListById;
import com.ccservice.elong.inter.ELongGetHotelRoomPriceInfo;
import com.ccservice.elong.inter.ELongHotelProductVouchForAPI;
import com.ccservice.elong.inter.ELongInstantCofirm;
import com.ccservice.elong.inter.ELongSubmitHotelOrder;
import com.ccservice.elong.inter.ElongCancelHotelOrderById;

public class ELongHotel implements IELongHotel {

    /**
     * 创建订单
     */
    public String createElongHotelOrder(String HotelId, String RoomTypeID, int RoomAmount, String RatePlanID,
            String checkindate, String checkoutdate, String arrivalearlytime, String arrivalatetime,
            String GuestTypeCode, int GuestAmount, String PaymentTypeCode, String CurrencyCode, double TotalPrice,
            String ConfirmTypeCode, String ConfirmLanguageCode, Contacter Contacters, Creditcard creditCard,
            List<Guest> Guests) {
        NorthBoundAPIServiceStub.SubmitHotelOrderResponseE response = new NorthBoundAPIServiceStub.SubmitHotelOrderResponseE();
        try {
            response = ELongSubmitHotelOrder.submitOrder(HotelId, RoomTypeID, RoomAmount, RatePlanID, checkindate,
                    checkoutdate, arrivalearlytime, arrivalatetime, GuestTypeCode, GuestAmount, PaymentTypeCode,
                    CurrencyCode, TotalPrice, ConfirmTypeCode, ConfirmLanguageCode, Contacters, creditCard, Guests);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // 1.成功 0.失败
        String resultCode = response.getSubmitHotelOrderResult().getResponseHead().getResultCode();
        if (resultCode.equals("0")) {
            int hotelOrderID = response.getSubmitHotelOrderResult().getSubmitHotelOrderResult().getHotelOrderID();
            return "1," + hotelOrderID;
        }
        else {
            String resultMessage = response.getSubmitHotelOrderResult().getResponseHead().getResultMessage();
            return "0," + resultMessage;
        }
    }

    /**
     * 取消订单
     */
    public String cancelElongHotelOrder(int hotelOrderId) {
        NorthBoundAPIServiceStub.CancelHotelOrderByIdResponseE cancelResult = new NorthBoundAPIServiceStub.CancelHotelOrderByIdResponseE();
        try {
            cancelResult = ElongCancelHotelOrderById.cancelOrder(hotelOrderId);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        String resultCode = cancelResult.getCancelHotelOrderByIdResult().getResponseHead().getResultCode();
        // 1.成功 0.失败
        if (resultCode.equals("0")) {
            return "1";
        }
        else {
            String resultMessage = cancelResult.getCancelHotelOrderByIdResult().getResponseHead().getResultMessage();
            return "0," + resultMessage;
        }
    }

    /**
     * 
     * @param OrderId
     *            订单ID
     * @return
     */
    public String confirmInfo(int orderId) {
        // TODO Auto-generated method stub
        NorthBoundAPIServiceStub.InstantConfirmResponse confirmInfo = new NorthBoundAPIServiceStub.InstantConfirmResponse();
        try {
            confirmInfo = ELongInstantCofirm.confirm(orderId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // 1-即时确认订单 0-非即时确认订单, 2-查询超时 3-输入的订单号无效
        String resultCode = confirmInfo.getInstantConfirmResult().getResponseHead().getResultCode();
        if (resultCode.equals("0")) {
            int InstantConfirm = confirmInfo.getInstantConfirmResult().getInstantConfirmInfo().getInstantConfirm();
            String OrderState = confirmInfo.getInstantConfirmResult().getInstantConfirmInfo().getOrderState();
            return InstantConfirm + "," + OrderState;
        }
        else {
            String resultMessage = confirmInfo.getInstantConfirmResult().getResponseHead().getResultMessage();
            return resultMessage;
        }
    }

    /**
     * 酒店价格查询
     */
    public HotelsResult getHotelPrice(String cityId, String hotelName, String hotelId, String checkInDate,
            String checkOutDate) {
        // TODO Auto-generated method stub
        HotelsResult hotelsresult = new HotelsResult();
        NorthBoundAPIServiceStub.GetHotelListResponseE response = new NorthBoundAPIServiceStub.GetHotelListResponseE();
        try {
            response = ELongGetHotelList.getHotelPrice(cityId, hotelName, hotelId, checkInDate, checkOutDate);
            String resultCode = response.getGetHotelListResult().getResponseHead().getResultCode();
            if (resultCode.equals("0")) {
                hotelsresult.setResultCode(resultCode);
                NorthBoundAPIServiceStub.HotelForGetHotelList[] hotelLists = response.getGetHotelListResult()
                        .getHotels().getHotel();
                if (hotelLists != null && hotelLists.length > 0) {
                    List<Hotel> listhotel = new ArrayList<Hotel>();
                    for (int i = 0; i < hotelLists.length; i++) {
                        Hotel hotel = new Hotel();
                        NorthBoundAPIServiceStub.HotelForGetHotelList hotelList = hotelLists[i];
                        // 酒店名称
                        hotel.setName(hotelList.getHotelName());
                        // 酒店ID
                        hotel.setHotelcode(hotelList.getHotelId());
                        // 星级代码
                        if (hotelList.getStarCode() != null && !"".equals(hotelList.getStarCode())
                                && !"null".equals(hotelList.getStarCode())) {
                            hotel.setStar(Integer.valueOf(hotelList.getStarCode()));
                        }
                        // 酒店地址
                        hotel.setAddress(hotelList.getHotelAddress());
                        // 酒店房态 0-正常 1-部分日期满房，2-全部满房
                        //hotel.setHotelInvStatusCode(Integer.valueOf(hotelList.getHotelInvStatusCode()));
                        // 酒店最低价
                        hotel.setLowestPrice(hotelList.getLowestPrice());
                        NorthBoundAPIServiceStub.ArrayOfRoomForGetHotelList roomlist = hotelList.getRooms();
                        if (roomlist != null) {
                            NorthBoundAPIServiceStub.RoomForGetHotelList[] roomLists = roomlist.getRoom();
                            if (roomLists != null && roomLists.length > 0) {
                                List<Roomtype> listRoomType = new ArrayList<Roomtype>();
                                if (roomLists != null && roomLists.length > 0) {
                                    for (int j = 0; j < roomLists.length; j++) {
                                        Roomtype roomType = new Roomtype();
                                        NorthBoundAPIServiceStub.RoomForGetHotelList roomList = roomLists[j];
                                        // 房型名称
                                        roomType.setName(roomList.getRoomName());
                                        // 房型ID
                                        roomType.setRoomcode(roomList.getRoomTypeId());
                                        // 房型房态 0-正常 1-部分日期满房，2-全部满房
                                        roomType.setState(Integer.valueOf(roomList.getRoomInvStatusCode()));
                                        NorthBoundAPIServiceStub.RatePlanForGetHotelList[] ratePlans = roomList
                                                .getRatePlans().getRatePlan();
                                        List<RatePlan> listRatePlan = new ArrayList<RatePlan>();
                                        if (ratePlans != null && ratePlans.length > 0) {
                                            for (int k = 0; k < ratePlans.length; k++) {
                                                RatePlan ratePlan = new RatePlan();
                                                NorthBoundAPIServiceStub.RatePlanForGetHotelList ratePlanlist = ratePlans[k];
                                                // RPID
                                                ratePlan.setRateplanid(ratePlanlist.getRatePlanID());
                                                // RP业务编码
                                                if (ratePlanlist.getRatePlanCode() != null
                                                        && !"".equals(ratePlanlist.getRatePlanCode())) {
                                                    ratePlan.setRatePlanCode(Integer.valueOf(ratePlanlist
                                                            .getRatePlanCode()));
                                                }
                                                // RP名称
                                                ratePlan.setRateplanname(ratePlanlist.getRatePlanName());
                                                // 客人类型代码
                                                ratePlan.setGuestTypeCode(Integer.valueOf(ratePlanlist
                                                        .getGuestTypeCode()));
                                                NorthBoundAPIServiceStub.RatesForGetHotelList rates = ratePlanlist
                                                        .getRates();
                                                // 总价
                                                ratePlan.setTotelPrice(rates.getTotalPrice());
                                                // 货币代码
                                                ratePlan.setCurrencyCode(rates.getCurrencyCode());
                                                NorthBoundAPIServiceStub.RateForGetHotelList[] Rate = rates.getRates()
                                                        .getRate();
                                                List<Rate> listRate = new ArrayList<Rate>();
                                                if (Rate != null && Rate.length > 0) {
                                                    for (int l = 0; l < Rate.length; l++) {
                                                        Rate rate = new Rate();
                                                        NorthBoundAPIServiceStub.RateForGetHotelList ratelist = Rate[l];
                                                        // 对应日期
                                                        rate.setDate(ratelist.getDate());
                                                        // 货币代码
                                                        rate.setCurrencyCode(ratelist.getCurrencyCode());
                                                        // 每日房态 0-正常 1、2-全部满房
                                                        if (ratelist.getInvStatusCode() == null
                                                                || "null".equals(ratelist.getInvStatusCode())
                                                                || "".equals(ratelist.getInvStatusCode())) {
                                                            rate.setInvStatusCode(2);
                                                        }
                                                        else {
                                                            rate.setInvStatusCode(Integer.valueOf(ratelist
                                                                    .getInvStatusCode()));
                                                        }
                                                        // 门市价
                                                        rate.setRetailrate(ratelist.getRetailRate());
                                                        // 会员价格
                                                        rate.setMemberrate(ratelist.getMemberRate());
                                                        // 加床价格
                                                        rate.setAddbedrate(ratelist.getAddBedRate());
                                                        listRate.add(rate);
                                                    }
                                                }
                                                ratePlan.setListrate(listRate);
                                                listRatePlan.add(ratePlan);
                                            }
                                        }
                                        roomType.setListrateplan(listRatePlan);
                                        listRoomType.add(roomType);
                                    }
                                }
                                hotel.setListroomtype(listRoomType);
                            }
                        }
                        listhotel.add(hotel);
                        hotelsresult.setListhotel(listhotel);
                    }
                }
            }
            else {
                String resultMessage = response.getGetHotelListResult().getResponseHead().getResultMessage();
                hotelsresult.setResultCode(resultCode);
                hotelsresult.setResultMessage(resultMessage);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return hotelsresult;
    }

    /**
     * 订单是否需要担保
     */
    public IsVouchResult isVouch(String hotelId, String roomTypeId, int ratePlanId, String checkindate,
            String checkoutdate, String arrivalearlytime, String arrivalatetime, int roomNum) {
        IsVouchResult isVouchResult = new IsVouchResult();
        // TODO Auto-generated method stub
        NorthBoundAPIServiceStub.GetHotelProductVouchResponse response = new NorthBoundAPIServiceStub.GetHotelProductVouchResponse();
        try {
            response = ELongHotelProductVouchForAPI.isVouch(hotelId, roomTypeId, ratePlanId, checkindate, checkoutdate,
                    arrivalearlytime, arrivalatetime, roomNum);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String resultCode = response.getGetHotelProductVouchResult().getResponseHead().getResultCode();
        if (resultCode.equals("0")) {
            isVouchResult.setResultCode(resultCode);
            String isVouch = response.getGetHotelProductVouchResult().getVouchInfo().getIsVouch();
            isVouchResult.setIsVouch(isVouch);
            int vouchMoneyType = response.getGetHotelProductVouchResult().getVouchInfo().getVouchMoneyType();
            isVouchResult.setVouchMoneyType(Integer.valueOf(vouchMoneyType).toString());
            isVouchResult.setCNDescription(response.getGetHotelProductVouchResult().getVouchInfo().getCNDescription());
            isVouchResult.setENDescription(response.getGetHotelProductVouchResult().getVouchInfo().getENDescription());
            isVouchResult
                    .setLastCancelTime(response.getGetHotelProductVouchResult().getVouchInfo().getLastCannelTime());
        }
        else {
            String resultMessage = response.getGetHotelProductVouchResult().getResponseHead().getResultMessage();
            isVouchResult.setResultCode(resultCode);
            isVouchResult.setResultMessage(resultMessage);
        }
        return isVouchResult;
    }

    /**
     * 根据酒店ID获取价格信息
     */
    public HotelsResult getPriceByHotelId(String StartDate, String EndDate, String HotelID) {
        HotelsResult hotelsresult = new HotelsResult();
        NorthBoundAPIServiceStub.GetHotelRoomPriceInfoResponse response = new NorthBoundAPIServiceStub.GetHotelRoomPriceInfoResponse();
        try {
            response = ELongGetHotelRoomPriceInfo.getPriceInfo(StartDate, EndDate, HotelID);
            String resultCode = response.getGetHotelRoomPriceInfoResult().getResponseHead().getResultCode();
            if (resultCode.equals("0")) {
                hotelsresult.setResultCode(resultCode);
                NorthBoundAPIServiceStub.PriceHotel[] priceHotels = response.getGetHotelRoomPriceInfoResult()
                        .getHotels().getPriceHotel();
                if (priceHotels != null && priceHotels.length > 0) {
                    List<Hotel> listhotel = new ArrayList<Hotel>();
                    for (int i = 0; i < priceHotels.length; i++) {
                        Hotel hotel = new Hotel();
                        NorthBoundAPIServiceStub.PriceHotel priceHotel = priceHotels[i];
                        // 酒店ID
                        hotel.setHotelcode(priceHotel.getHotelID());
                        NorthBoundAPIServiceStub.PriceRoomType[] Rooms = priceHotel.getRooms().getPriceRoomType();
                        List<Roomtype> listroomtype = new ArrayList<Roomtype>();
                        if (Rooms != null && Rooms.length > 0) {
                            for (int j = 0; j < Rooms.length; j++) {
                                Roomtype roomType = new Roomtype();
                                NorthBoundAPIServiceStub.PriceRoomType room = Rooms[j];
                                // 房型ID
                                roomType.setRoomcode(room.getRoomTypeID().toString());
                                // 房型名称
                                roomType.setName(room.getRoomTypeName());
                                NorthBoundAPIServiceStub.RatePlan[] RatePlans = room.getRatePlans().getRatePlan();
                                List<RatePlan> listrateplan = new ArrayList<RatePlan>();
                                if (RatePlans != null & RatePlans.length > 0) {
                                    for (int k = 0; k < RatePlans.length; k++) {
                                        RatePlan ratePlan = new RatePlan();
                                        NorthBoundAPIServiceStub.RatePlan RatePlan = RatePlans[k];
                                        // RPID
                                        ratePlan.setRateplanid(Integer.valueOf(RatePlan.getRatePlanID()));
                                        // RP名称
                                        ratePlan.setRateplanname(RatePlan.getRatePlanName());
                                        NorthBoundAPIServiceStub.Rate[] Rate = RatePlan.getRates().getRate();
                                        List<Rate> listrate = new ArrayList<Rate>();
                                        if (Rate != null && Rate.length > 0) {
                                            for (int m = 0; m < Rate.length; m++) {
                                                Rate rates = new Rate();
                                                NorthBoundAPIServiceStub.Rate rate = Rate[m];
                                                // 开始日期
                                                rates.setStartdate(rate.getStartDate());
                                                // 结束日期
                                                rates.setEnddate(rate.getEndDate());
                                                // 门市价
                                                rates.setRetailrate(rate.getRetailRate());
                                                // 会员价格
                                                rates.setMemberrate(rate.getMemberRate());
                                                // 加床价格
                                                rates.setAddbedrate(rate.getAddBedRate());
                                                // 周末会员价格
                                                rates.setWeekendmemberrate(rate.getWeekendMemberRate());
                                                listrate.add(rates);
                                            }
                                        }
                                        ratePlan.setListrate(listrate);
                                        listrateplan.add(ratePlan);
                                    }
                                }
                                roomType.setListrateplan(listrateplan);
                                listroomtype.add(roomType);
                            }
                        }
                        hotel.setListroomtype(listroomtype);
                        listhotel.add(hotel);
                        hotelsresult.setListhotel(listhotel);
                    }
                }
            }
            else {
                String resultMessage = response.getGetHotelRoomPriceInfoResult().getResponseHead().getResultMessage();
                hotelsresult.setResultCode(resultCode);
                hotelsresult.setResultMessage(resultMessage);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return hotelsresult;
    }

    /**
     * 获取房态
     */
    public HotelsResult getHotelInventory(String startDate, String endDate, String hotelId) {
        HotelsResult hotelsresult = new HotelsResult();
        NorthBoundAPIServiceStub.GetHotelInventoryResponseE response = new NorthBoundAPIServiceStub.GetHotelInventoryResponseE();
        try {
            response = ELongGetHotelInventory.gethotelInventory(startDate, endDate, hotelId);
            String resultCode = response.getGetHotelInventoryResult().getResponseHead().getResultCode();
            if (resultCode.equals("0")) {
                hotelsresult.setResultCode(resultCode);
                ArrayOfHotel arrayOfHotel = response.getGetHotelInventoryResult().getHotels();
                if (arrayOfHotel == null) {
                    hotelsresult.setResultMessage("没有空余房间!......");
                }
                else {
                    NorthBoundAPIServiceStub.Hotel[] hotels = arrayOfHotel.getHotel();
                    if (hotels != null && hotels.length > 0) {
                        List<Hotel> listHotel = new ArrayList<Hotel>();
                        for (int i = 0; i < hotels.length; i++) {
                            Hotel hotel = new Hotel();
                            NorthBoundAPIServiceStub.Hotel hotel2 = hotels[i];
                            // 酒店Id
                            hotel.setHotelcode(hotel2.getHotelID());
                            NorthBoundAPIServiceStub.RoomType[] rooms = hotel2.getRooms().getRoomType();
                            List<Roomtype> listRoomType = new ArrayList<Roomtype>();
                            if (rooms != null && rooms.length > 0) {
                                for (int j = 0; j < rooms.length; j++) {
                                    Roomtype roomType = new Roomtype();
                                    NorthBoundAPIServiceStub.RoomType room = rooms[j];
                                    // 房型ID
                                    roomType.setRoomcode(room.getRoomTypeID());
                                    // 有效时间列表
                                    NorthBoundAPIServiceStub.Date[] Dates = room.getDates().getDate();
                                    List<UseTime> listDates = new ArrayList<UseTime>();
                                    if (Dates != null && Dates.length > 0) {
                                        for (int m = 0; m < Dates.length; m++) {
                                            UseTime usetime = new UseTime();
                                            NorthBoundAPIServiceStub.Date date = Dates[m];
                                            // 日期
                                            usetime.setDate(date.getAvailableDate());
                                            // 房量
                                            usetime.setAliableAmount(Integer.valueOf(date.getAvailableAmount()));
                                            // 房型房态
                                            usetime.setRoomInvStatusCode(Integer.valueOf(date.getRoomInvStatusCode()));
                                            // 是否可以超售: 0-可以超售，1-不可以
                                            usetime.setIsOverBooking(Integer.valueOf(date.getIsOverBooking()));
                                            // 房量可以开始预订的日期
                                            usetime.setBookingStartDate(date.getBookingStartDate());
                                            // 房量结束预订的日期
                                            usetime.setBookingEndate(date.getBookingEndDate());
                                            // 房量开始预订的时间
                                            usetime.setBookingBeginTime(date.getBookingBeginTime());
                                            // 房量结束预订的时间
                                            usetime.setBookingEndTime(date.getBookingEndTime());
                                            listDates.add(usetime);
                                        }
                                    }
                                    roomType.setRoomStatuslistDate(listDates);
                                    listRoomType.add(roomType);
                                }
                            }
                            hotel.setListroomtype(listRoomType);
                            listHotel.add(hotel);
                            hotelsresult.setListhotel(listHotel);
                        }
                    }
                }
            }
            else {
                String resultMessage = response.getGetHotelInventoryResult().getResponseHead().getResultMessage();
                hotelsresult.setResultCode(resultCode);
                hotelsresult.setResultMessage(resultMessage);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return hotelsresult;
    }

    /**
     * 根据订单号ID获得多个订单信息GetHotelOrderListById 
     */
    public Map<String, String> GetHotelOrderListById(String orderidstr) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            result = ELongGetHotelOrderListById.getOrderList(orderidstr);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据订单号查询订单
     */
    public OrderResult getHotelOrderDetailByOrderId(int hotelOrderId) {
        // TODO Auto-generated method stub
        OrderResult orderResult = new OrderResult();
        NorthBoundAPIServiceStub.GetHotelOrderDetailByIdResponse response = new NorthBoundAPIServiceStub.GetHotelOrderDetailByIdResponse();
        try {
            response = ELongGetHotelOrderDetailByOrderId.getDetail(hotelOrderId);
            String resultCode = response.getGetHotelOrderDetailByIdResult().getResponseHead().getResultCode();
            if (resultCode.equals("0")) {
                orderResult.setResultCode(resultCode);
                NorthBoundAPIServiceStub.HotelOrderForGetOrderByID hotelOrderForGetOrderByID = response
                        .getGetHotelOrderDetailByIdResult().getHotelOrder();
                Hotelorder hotelOrder = new Hotelorder();
                // 订单ID
                hotelOrder.setId(hotelOrderForGetOrderByID.getHotelOrderId());
                // 订单状态
                hotelOrder.setOrderStates(hotelOrderForGetOrderByID.getOrderStatusCode());
                // 货币代码
                hotelOrder.setCurrencyCode(hotelOrderForGetOrderByID.getCurrencyCode());
                hotelOrder.setPrice(hotelOrderForGetOrderByID.getOrderTotalPrice().toString());
                // 总价
                NorthBoundAPIServiceStub.RoomForGetOrderByID[] roomForGetOrderByID = hotelOrderForGetOrderByID
                        .getRoomGroups().getRoom();
                List<Room> roomList = new ArrayList<Room>();
                if (roomForGetOrderByID != null && roomForGetOrderByID.length > 0) {
                    for (int i = 0; i < roomForGetOrderByID.length; i++) {
                        Room room = new Room();
                        NorthBoundAPIServiceStub.RoomForGetOrderByID roomForGetOrderByID2 = roomForGetOrderByID[i];
                        // 酒店ID
                        room.setHotelID(roomForGetOrderByID2.getHotelId());
                        // 房型ID
                        room.setRoomTypeId(roomForGetOrderByID2.getRoomTypeId());
                        // RatePlanID
                        room.setRatePlanID(roomForGetOrderByID2.getRatePlanID());
                        // 价格代码code
                        room.setRatePlanCode(roomForGetOrderByID2.getRatePlanCode());
                        // 入住时间
                        room.setCheckInDate(roomForGetOrderByID2.getCheckInDate());
                        // 离店时间
                        room.setCheckOutDate(roomForGetOrderByID2.getCheckOutDate());
                        // ELONG卡号
                        room.setElongCardNo(roomForGetOrderByID2.getElongCardNo());
                        // 宾客类型代码
                        room.setGuestTypeCode(roomForGetOrderByID2.getGuestTypeCode());
                        // 房间数量
                        room.setRoomAmount(roomForGetOrderByID2.getRoomAmount());
                        // 入住人数
                        room.setGuestAmount(roomForGetOrderByID2.getGuestAmount());
                        // 支付方式
                        room.setPaymentTypeCode(roomForGetOrderByID2.getPaymentTypeCode());
                        // 最早到达时间
                        room.setArrivalEarlyTime(roomForGetOrderByID2.getArrivalEarlyTime());
                        // 最晚到达时间
                        room.setArrivalLateTime(roomForGetOrderByID2.getArrivalLateTime());
                        // 货币代码
                        room.setCurrencyCode(roomForGetOrderByID2.getCurrencyCode());
                        // 总价
                        room.setRoomTotalPrice(roomForGetOrderByID2.getTotalPrice());
                        // 订单确认方式
                        room.setConfirmTypeCode(roomForGetOrderByID2.getConfirmTypeCode());
                        // 订单确认语言
                        room.setConfirmLanguageCode(roomForGetOrderByID2.getConfirmLanguageCode());
                        // 给酒店备注
                        room.setNoteToElong(roomForGetOrderByID2.getNoteToHotel());
                        // 给艺龙备注
                        room.setNoteToHotel(roomForGetOrderByID2.getNoteToElong());
                        // 最晚取消时间
                        room.setCancelDeadLine(roomForGetOrderByID2.getCancelDeadline());
                        NorthBoundAPIServiceStub.ContacterForGetOrderByID[] contacterForGetOrderByIDs = roomForGetOrderByID2
                                .getContacters().getContacter();
                        Contacter contacter = new Contacter();
                        if (contacterForGetOrderByIDs != null && contacterForGetOrderByIDs.length > 0) {
                            for (int j = 0; j < contacterForGetOrderByIDs.length; j++) {
                                NorthBoundAPIServiceStub.ContacterForGetOrderByID contacterForGetOrderByID = contacterForGetOrderByIDs[j];
                                // 姓名
                                contacter.setName(contacterForGetOrderByID.getName());
                                // 姓名代码
                                contacter.setGenderCode(contacterForGetOrderByID.getGenderCode());
                                // Email
                                contacter.setEmail(contacterForGetOrderByID.getEmail());
                                // 手机
                                contacter.setMobile(contacterForGetOrderByID.getMobile());
                                // 证件类别代码
                                contacter.setIdTypeCode(contacterForGetOrderByID.getIdTypeCode());
                                // 证件号码
                                contacter.setIdNumber(contacterForGetOrderByID.getIdNumber());
                                NorthBoundAPIServiceStub.PhoneForGetOrderByID phoneForGetOrderByID = contacterForGetOrderByID
                                        .getPhone();
                                Phone phone = new Phone();
                                // 国际区号
                                phone.setInterCode(phoneForGetOrderByID.getInterCode());
                                // 国内区号
                                phone.setAreaCode(phoneForGetOrderByID.getAreaCode());
                                // 电话号码
                                phone.setNumber(phoneForGetOrderByID.getNmber());
                                // 分机号
                                phone.setExt(phoneForGetOrderByID.getExt());
                                NorthBoundAPIServiceStub.FaxForGetOrderByID faxForGetOrderByID = contacterForGetOrderByID
                                        .getFax();
                                Fax fax = new Fax();
                                // 国际区号
                                fax.setInterCode(faxForGetOrderByID.getInterCode());
                                // 国内区号
                                fax.setAreaCode(faxForGetOrderByID.getAreaCode());
                                // 电话号码
                                fax.setNumber(faxForGetOrderByID.getNmber());
                                // 分机号
                                fax.setExt(faxForGetOrderByID.getExt());
                                contacter.setPhone(phone);
                                contacter.setFax(fax);
                            }
                        }
                        room.setContacter(contacter);
                        Creditcard creditCard = new Creditcard();
                        NorthBoundAPIServiceStub.CreditCardForGetOrderByID cardForGetOrderByID = roomForGetOrderByID2
                                .getCreditCard();
                        if (null != cardForGetOrderByID) {
                            // 信用卡号
                            if (null != cardForGetOrderByID.getNumber()) {
                                creditCard.setIdNumber(cardForGetOrderByID.getNumber());
                            }
                            // 校验码
                            if (null != cardForGetOrderByID.getVeryfyCode()) {
                                creditCard.setCreditcheckcode(cardForGetOrderByID.getVeryfyCode());
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                            String validtime = cardForGetOrderByID.getValidYear() + "-"
                                    + cardForGetOrderByID.getValidMonth();
                            creditCard.setCreditexpiry(new Timestamp(sdf.parse(validtime).getTime()));
                            // 持卡人姓名
                            if (null != cardForGetOrderByID.getCardHolderName()) {
                                creditCard.setCardholdername(cardForGetOrderByID.getCardHolderName());
                            }

                            // 证件类别代码
                            if (null != cardForGetOrderByID.getIdTypeCode()) {
                                creditCard.setIdTypeCode(cardForGetOrderByID.getIdTypeCode());
                            }
                            // 证件号码
                            if (null != cardForGetOrderByID.getIdNumber()) {
                                creditCard.setIdNumber(cardForGetOrderByID.getIdNumber());
                            }
                        }

                        // room.setCreditCard(creditCard); 有时间在调
                        NorthBoundAPIServiceStub.GuestForGetOrderByID[] guestForGetOrderByIDs = roomForGetOrderByID2
                                .getGuests().getGuest();
                        Guest[] guests = new Guest[guestForGetOrderByIDs.length];
                        if (guestForGetOrderByIDs != null && guestForGetOrderByIDs.length > 0) {
                            for (int j = 0; j < guestForGetOrderByIDs.length; j++) {
                                NorthBoundAPIServiceStub.GuestForGetOrderByID guestForGetOrderByID = guestForGetOrderByIDs[j];
                                Guest guest = guests[i];
                                if (null != guest) {
                                    // 姓名
                                    if (null != guestForGetOrderByID.getName()) {
                                        guest.setName(guestForGetOrderByID.getName());
                                    }

                                    // 性别代码
                                    if (null != guestForGetOrderByID.getGenderCode()) {
                                        guest.setGenderCode(guestForGetOrderByID.getGenderCode());
                                    }

                                    // Email
                                    if (null != guestForGetOrderByID.getEmail()) {
                                        guest.setEmail(guestForGetOrderByID.getEmail());
                                    }

                                    // 手机
                                    if (null != guestForGetOrderByID.getMobile()) {
                                        guest.setMobile(guestForGetOrderByID.getMobile());
                                    }

                                    // 证件类别
                                    if (null != guestForGetOrderByID.getIdTypeCode()) {
                                        guest.setIdTypeCode(guestForGetOrderByID.getIdTypeCode());
                                    }

                                    // 证件号码
                                    if (null != guestForGetOrderByID.getIdNumber()) {
                                        guest.setIdNumber(guestForGetOrderByID.getIdNumber());
                                    }

                                    Phone phone = new Phone();
                                    NorthBoundAPIServiceStub.PhoneForGetOrderByID phoneForGetOrderByID = guestForGetOrderByID
                                            .getPhone();
                                    if (null != phoneForGetOrderByID) {
                                        // 国际区号
                                        phone.setInterCode(phoneForGetOrderByID.getInterCode());
                                        // 国内区号
                                        phone.setAreaCode(phoneForGetOrderByID.getAreaCode());
                                        // 电话号码
                                        phone.setNumber(phoneForGetOrderByID.getNmber());
                                        // 分机号
                                        phone.setExt(phoneForGetOrderByID.getExt());
                                    }
                                    guest.setPhone(phone);
                                    Fax fax = new Fax();
                                    NorthBoundAPIServiceStub.FaxForGetOrderByID faxForGetOrderByID = guestForGetOrderByID
                                            .getFax();
                                    if (null != faxForGetOrderByID) {
                                        // 国际区号
                                        fax.setInterCode(faxForGetOrderByID.getInterCode());
                                        // 国内区号
                                        fax.setAreaCode(faxForGetOrderByID.getAreaCode());
                                        // 电话号码
                                        fax.setNumber(faxForGetOrderByID.getNmber());
                                        // 分机号
                                        fax.setExt(faxForGetOrderByID.getExt());
                                    }

                                    guest.setFax(fax);
                                }

                            }
                        }
                        room.setGuests(guests);
                        NorthBoundAPIServiceStub.RatesForGetOrderByID ratesForGetOrderByID = roomForGetOrderByID2
                                .getRates();
                        NorthBoundAPIServiceStub.RateForGetOrderByID[] rateForGetOrderByIDs = ratesForGetOrderByID
                                .getRates().getRate();
                        Rate[] rates = new Rate[rateForGetOrderByIDs.length];
                        if (rateForGetOrderByIDs != null && rateForGetOrderByIDs.length > 0) {
                            for (int j = 0; j < rateForGetOrderByIDs.length; j++) {
                                Rate rate = rates[j];
                                if (null != rate) {
                                    NorthBoundAPIServiceStub.RateForGetOrderByID rateForGetOrderByID = rateForGetOrderByIDs[j];
                                    // 对应日期
                                    if (null != rateForGetOrderByID) {
                                        rate.setDate(rateForGetOrderByID.getDate());
                                        // 货币代码
                                        rate.setCurrencyCode(rateForGetOrderByID.getCurrencyCode());
                                        // 门市价
                                        rate.setRetailrate(rateForGetOrderByID.getRetailRate());
                                        // 会员价格
                                        rate.setMemberrate(rateForGetOrderByID.getMemberRate());
                                        // 加床价格
                                        rate.setAddbedrate(rateForGetOrderByID.getAddBedRate());
                                    }
                                }
                            }
                        }
                        room.setRates(rates);
                        NorthBoundAPIServiceStub.ArrayOfGaranteeRuleForGetOrderByID rules = roomForGetOrderByID2
                                .getGaranteeRules();
                        if (rules != null) {
                            NorthBoundAPIServiceStub.GaranteeRuleForGetOrderByID[] garanteeRules = rules
                                    .getGaranteeRule();
                            GaranteeRule[] garanteerules = new GaranteeRule[garanteeRules.length];
                            if (null != garanteeRules) {
                                if (garanteeRules.length > 0) {
                                    for (int m = 0; m < garanteeRules.length; m++) {
                                        GaranteeRule garanteerule = new GaranteeRule();
                                        NorthBoundAPIServiceStub.GaranteeRuleForGetOrderByID garanteeRule = garanteeRules[m];
                                        // 担保规则类型
                                        if (null != garanteeRule) {
                                            garanteerule.setGaranteeRulesTypeCode(garanteeRule
                                                    .getGaranteeRulesTypeCode());
                                            if (null != garanteeRule.getDescription()) {
                                                garanteerule.setDescription(garanteeRule.getDescription());
                                            }
                                        }
                                        garanteerules[m] = garanteerule;
                                    }
                                }
                            }
                            room.setGaranteeRule(garanteerules);
                        }
                        NorthBoundAPIServiceStub.ArrayOfBookingRuleForGetOrderByID bookd = roomForGetOrderByID2
                                .getBookingRules();
                        if (bookd != null) {
                            NorthBoundAPIServiceStub.BookingRuleForGetOrderByID[] bookingRuless = bookd
                                    .getBookingRule();
                            if (bookingRuless != null) {
                                BookingRule[] bookingrules = new BookingRule[bookingRuless.length];
                                if (null != bookingRuless) {
                                    if (bookingRuless.length > 0) {
                                        for (int m = 0; m < bookingRuless.length; m++) {
                                            NorthBoundAPIServiceStub.BookingRuleForGetOrderByID bookingRule = bookingRuless[m];
                                            BookingRule bookingrule = bookingrules[m];
                                            // 预定规则类型代码
                                            bookingrule.setBookingRuleTypeCode(bookingRule.getBookingRuleTypeCode());
                                            bookingrule.setDescription(bookingRule.getDescription());
                                        }
                                    }
                                }
                                room.setBookingrule(bookingrules);
                            }
                        }
                        NorthBoundAPIServiceStub.ArrayOfAddValueForGetOrderByID addvaluetemp = roomForGetOrderByID2
                                .getAddValues();
                        if (addvaluetemp != null) {
                            NorthBoundAPIServiceStub.AddValueForGetOrderByID[] addVaules = addvaluetemp.getAddValue();
                            Addvalue[] addvalues = new Addvalue[addVaules.length];
                            if (addVaules != null && addVaules.length > 0) {
                                for (int m = 0; m < addVaules.length; m++) {
                                    NorthBoundAPIServiceStub.AddValueForGetOrderByID addVaule = addVaules[m];
                                    Addvalue addvalue = addvalues[m];
                                    if (addvalue != null) {
                                        // 增值服务ID
                                        addvalue.setAddValueID(Integer.valueOf(addVaule.getAddValueID()).toString());
                                        // 业务代码
                                        addvalue.setBusinessCode(addVaule.getBusinessCode());
                                        // 是否包含在房费中
                                        addvalue.setIsInclude(addVaule.getIsInclude());
                                        // 包含的分数
                                        addvalue.setIncludeCount(addVaule.getIncludedCount());
                                        // 货币代码
                                        addvalue.setCurrencyCode(addVaule.getCurrencyCode());
                                        // 单价默认选项
                                        addvalue.setPriceDefauleOption(addVaule.getPriceDefaultOption());
                                        // 单价
                                        addvalue.setPriceNumber(addVaule.getPriceNumber());
                                        // 是否单加
                                        addvalue.setIsAdd(addVaule.getIsAdd());
                                        // 单加单价默认选项
                                        addvalue.setSinglePriceOption(addVaule.getSinglePriceOption());
                                        // 单加价格
                                        addvalue.setSinglePrice(addVaule.getSinglePrice());
                                        // 附加服务描述
                                        addvalue.setDescription(addVaule.getDescription());
                                    }
                                }
                            }
                            room.setAddvalues(addvalues);
                        }
                        // 订单生成时间
                        room.setBookingTime(roomForGetOrderByID2.getBookingTime());
                        roomList.add(room);
                    }
                    hotelOrder.setRoomList(roomList);
                }
                orderResult.setHotelOrder(hotelOrder);

            }
            else {
                String resultMessage = response.getGetHotelOrderDetailByIdResult().getResponseHead().getResultMessage();
                orderResult.setResultCode(resultCode);
                orderResult.setResultMessage(resultMessage);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return orderResult;
    }
}
