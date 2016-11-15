package com.ccservice.b2b2c.atom.service;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Collections;
import java.text.SimpleDateFormat;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.roomtype.Rate;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.guest.Contacter;
import com.ccservice.b2b2c.base.hotelorder.Room;
import com.ccservice.b2b2c.base.roomtype.RatePlan;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.atom.hotel.IELongHotel;
import com.ccservice.b2b2c.base.hotel.HotelsResult;
import com.ccservice.b2b2c.base.hotel.IsVouchResult;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.creditcard.Creditcard;
import com.ccservice.b2b2c.base.hotelorder.OrderResult;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.base.garanteerule.GaranteeRule;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 艺龙酒店的接口实现类
 */
public class ElongHotelService implements IELongHotelService {

    private static String PaymentType = "SelfPay";//支付方式:现付

    IELongHotel elongHotel;

    public void setElongHotel(IELongHotel elongHotel) {
        this.elongHotel = elongHotel;
    }

    /**创建订单*/
    @SuppressWarnings("deprecation")
    public String createElongHotelOrder(String HotelId, String RoomTypeID, int RoomAmount, String RatePlanID,
            String checkindate, String checkoutdate, String arrivalearlytime, String arrivalatetime,
            String GuestTypeCode, int GuestAmount, String PaymentTypeCode, String CurrencyCode, double TotalPrice,
            String ConfirmTypeCode, String ConfirmLanguageCode, Contacter Contacters, Creditcard creditCard,
            List<Guest> Guests) {
        String returnmsg = "";
        try {
            //将请求参数转换为艺龙需要
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                checkindate = sdf.format(sdf.parse(checkindate));
                checkoutdate = sdf.format(sdf.parse(checkoutdate));
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                arrivalearlytime = sdf.format(sdf.parse(arrivalearlytime));
                arrivalatetime = sdf.format(sdf.parse(arrivalatetime));
            }
            catch (Exception e) {
                checkindate = checkindate.substring(0, 10);//yyyy-MM-dd
                checkoutdate = checkoutdate.substring(0, 10);//yyyy-MM-dd
                arrivalearlytime = arrivalearlytime.substring(0, 16);//yyyy-MM-dd HH:mm
                arrivalatetime = arrivalatetime.substring(0, 16);//yyyy-MM-dd HH:mm
            }
            //次日最早为23:59
            int istomorrow = 0;
            String tempstart = arrivalearlytime.substring(0, 10);
            String tempend = arrivalatetime.substring(0, 10);
            try {
                if (ElongHotelInterfaceUtil.getSubDays(tempstart, tempend) == 1) {
                    istomorrow = 1;//次日
                }
            }
            catch (Exception e) {
                if (!tempstart.equals(tempend)) {
                    istomorrow = 1;//次日
                }
            }
            if (istomorrow == 1) {
                arrivalearlytime = tempstart + " 23:59";
            }
            else if (ElongHotelInterfaceUtil.getSubDays(checkindate, ElongHotelInterfaceUtil.getCurrentDate()) == 0) {
                //最早到店时间早于当前时间，重新设置最早到店时间
                if (ElongHotelInterfaceUtil.CurrentTimeGreaterThanThisTime(arrivalearlytime + ":00")) {
                    Date current = new Date();
                    int h = current.getHours();
                    int m = current.getMinutes();
                    if (m <= 25 || h == 23) {
                        arrivalearlytime = tempstart + " " + (h < 10 ? "0" + h : h) + ":30";
                    }
                    else {
                        arrivalearlytime = tempstart + " " + (h + 1 < 10 ? "0" + (h + 1) : (h + 1)) + ":00";
                    }
                }
            }
            //调用方法
            String elmethod = "hotel.order.create";
            //艺龙请求参数
            JSONObject reqjson = new JSONObject();
            //通过Guests查询订单ID
            if (Guests == null || Guests.size() == 0) {
                throw new Exception("客人信息为空.");
            }
            Hotelorder hotelorder = Server.getInstance().getHotelService().findHotelorder(Guests.get(0).getOrderid());
            if (hotelorder == null) {
                throw new Exception("查询订单失败.");
            }
            reqjson.put("AffiliateConfirmationId", hotelorder.getOrderid());
            reqjson.put("HotelId", HotelId);
            reqjson.put("RoomTypeId", RoomTypeID);
            reqjson.put("RatePlanId", Integer.parseInt(RatePlanID));
            reqjson.put("ArrivalDate", checkindate);
            reqjson.put("DepartureDate", checkoutdate);
            reqjson.put("CustomerType", "Chinese");//内宾
            reqjson.put("PaymentType", "SelfPay");//现付
            reqjson.put("NumberOfRooms", RoomAmount);
            reqjson.put("NumberOfCustomers", GuestAmount);
            reqjson.put("EarliestArrivalTime", arrivalearlytime);
            reqjson.put("LatestArrivalTime", arrivalatetime);
            reqjson.put("CurrencyCode", "RMB");//货币
            reqjson.put("TotalPrice", TotalPrice);
            reqjson.put("CustomerIPAddress", "121.197.13.153");//客人访问IP
            reqjson.put("IsForceGuarantee", false);//是否强制担保 强制担保对应的担保金额是首晚
            reqjson.put("IsGuaranteeOrCharged", false);//是否已担保或已付款，开通了公司担保业务的合作伙伴才能使用该属性
            reqjson.put("SupplierCardNo", "");//供应商卡号
            reqjson.put("ConfirmationType", "SMS_cn");//确认类型
            reqjson.put("NoteToHotel", "");//给酒店备注
            reqjson.put("NoteToElong", "");//给艺龙备注
            //联系人
            JSONObject Contact = new JSONObject();
            Contact.put("Name", Contacters.getName().trim());
            if (!ElongHotelInterfaceUtil.StringIsNull(Contacters.getEmail())) {
                Contact.put("Email", Contacters.getEmail().trim());
            }
            if (!ElongHotelInterfaceUtil.StringIsNull(Contacters.getMobile())) {
                Contact.put("Mobile", Contacters.getMobile().trim());
            }
            Contact.put("Phone", "");
            Contact.put("Fax", "");
            Contact.put("Gender", getSex(Contacters.getGenderCode()));//Female  女，Maile 男, Unknown 未知
            reqjson.put("Contact", Contact.toString());
            //信用卡
            if (creditCard != null && !ElongHotelInterfaceUtil.StringIsNull(creditCard.getCreditnumber())) {
                //验证信用卡号
                boolean IsNeedVerifyCode = true;//是否需要提供CVV验证码
                /*String checkflag = creditcardValidate(creditCard.getCreditnumber());
                if(ElongHotelInterfaceUtil.StringIsNull(checkflag)){
                	throw new Exception("验证信用卡出错.");
                }
                if(checkflag.startsWith("0")){
                	throw new Exception(checkflag.split(",")[1]);
                }else if(checkflag.startsWith("1")){
                	if("false".equals(checkflag.split(",")[1])){
                		throw new Exception("信用卡不可用.");
                	}
                	if("false".equals(checkflag.split(",")[2])){
                		IsNeedVerifyCode = false;
                	}
                }*/
                JSONObject CreditCard = new JSONObject();
                //DES加密信用卡号
                CreditCard.put("Number", ElongHotelInterfaceUtil.DES(creditCard.getCreditnumber().trim()));
                if (IsNeedVerifyCode) {
                    CreditCard.put("CVV", creditCard.getCreditcheckcode());
                }
                try {
                    String[] validdate = new SimpleDateFormat("yyyy-MM").format(creditCard.getCreditexpiry())
                            .split("-");//2014-12-01 00:00:00.000
                    CreditCard.put("ExpirationYear", validdate[0]);
                    CreditCard.put("ExpirationMonth", validdate[1]);
                }
                catch (Exception e) {
                    CreditCard.put("ExpirationYear", 0);
                    CreditCard.put("ExpirationMonth", 0);
                }
                CreditCard.put("HolderName", creditCard.getCardholdername());
                CreditCard.put("IdType", creditCard.getIdTypeCode());
                CreditCard.put("IdNo", creditCard.getIdNumber());
                reqjson.put("CreditCard", CreditCard.toString());
            }
            //客人信息
            JSONArray OrderRooms = new JSONArray();
            for (int i = 0; i < Guests.size(); i++) {
                JSONObject OrderRoom = new JSONObject();
                Guest guest = Guests.get(i);
                //每个房间的入住人
                JSONArray Customers = new JSONArray();
                //入住人信息
                JSONObject Customer = new JSONObject();
                Customer.put("Name", guest.getName());
                if (!ElongHotelInterfaceUtil.StringIsNull(guest.getEmail())) {
                    Customer.put("Email", guest.getEmail().trim());
                }
                if (!ElongHotelInterfaceUtil.StringIsNull(guest.getMobile())) {
                    Customer.put("Mobile", guest.getMobile().trim());
                }
                Customer.put("Phone", "");
                Customer.put("Fax", "");
                Customer.put("Gender", getSex(guest.getGenderCode()));
                Customer.put("Nationality",
                        ElongHotelInterfaceUtil.StringIsNull(guest.getNationality()) ? "" : guest.getNationality());
                Customers.add(Customer);
                OrderRoom.put("Customers", Customers.toString());
                OrderRooms.add(OrderRoom);
            }
            reqjson.put("OrderRooms", OrderRooms.toString());
            //请求艺龙
            //String retstr = ElongHotelInterfaceUtil.postEl(elmethod, reqjson.toString(), "https");
            String retstr = SendPostandGet.submitPost(
                    ElongHotelInterfaceUtil.getMiddleUrl(elmethod, reqjson.toString(), "https"), "", "utf-8")
                    .toString();
            if (ElongHotelInterfaceUtil.StringIsNull(retstr)) {
                throw new Exception("供应返回信息为空.");
            }
            if (!retstr.contains("Code")) {
                throw new Exception(retstr);
            }
            //解析返回数据
            JSONObject all = JSONObject.fromObject(retstr);
            String Code = all.getString("Code");
            if ("0".equals(Code)) {
                String Result = all.getString("Result");
                JSONObject result = JSONObject.fromObject(Result);
                returnmsg = "1," + result.getLong("OrderId");// 1.成功 0.失败
            }
            else {
                returnmsg = "0," + Code.replace(",", "，"); // {"Code":"H002001|入住日期范围无效 request.Request"}
            }
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (ElongHotelInterfaceUtil.StringIsNull(msg)) {
                msg = "下单供应出错.";
            }
            else if (msg.contains("艺龙")) {
                msg = msg.replace("艺龙", "");
            }
            returnmsg = "0," + msg.replace(",", "，"); // 1.成功 0.失败
        }
        return returnmsg;
    }

    /**
     * 获取性别 GenderCode 0：男 1：女 2：未知
     */
    private String getSex(String GenderCode) {
        String str = "Unknown";//Female  女，Maile 男, Unknown 保密
        if ("0".equals(GenderCode)) {
            str = "Maile";
        }
        if ("1".equals(GenderCode)) {
            str = "Female";
        }
        return str;
    }

    /** 取消订单*/
    public String cancelElongHotelOrder(int HotelOrderId) {
        String returnmsg = "";
        try {
            //调用方法
            String elmethod = "hotel.order.cancel";
            //艺龙请求参数
            JSONObject reqjson = new JSONObject();
            reqjson.put("OrderId", HotelOrderId);
            reqjson.put("CancelCode", "其它");
            //请求艺龙
            //String retstr = ElongHotelInterfaceUtil.postEl(elmethod, reqjson.toString(), "https");
            String retstr = SendPostandGet.submitPost(
                    ElongHotelInterfaceUtil.getMiddleUrl(elmethod, reqjson.toString(), "https"), "", "utf-8")
                    .toString();
            if (ElongHotelInterfaceUtil.StringIsNull(retstr)) {
                throw new Exception("供应返回信息为空.");
            }
            if (!retstr.contains("Code")) {
                throw new Exception(retstr);
            }
            //解析返回数据
            JSONObject all = JSONObject.fromObject(retstr);
            String Code = all.getString("Code");
            if ("0".equals(Code)) {
                String Result = all.getString("Result");
                JSONObject result = JSONObject.fromObject(Result);
                //{"Code":"0","Result":{"Successs":true}}
                if (result.getBoolean("Successs")) {
                    returnmsg = "1";
                }
                else {
                    returnmsg = "0," + retstr.replace(",", "，");
                }
            }
            else {
                returnmsg = "0," + Code.replace(",", "，"); // {"Code":"H002001|入住日期范围无效 request.Request"}
            }
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (ElongHotelInterfaceUtil.StringIsNull(msg)) {
                msg = "取消下单出错.";
            }
            else if (msg.contains("艺龙")) {
                msg = msg.replace("艺龙", "");
            }
            returnmsg = "0," + msg.replace(",", "，"); // 1.成功 0.失败
        }
        return returnmsg;
    }

    /**订单信息确认*/
    public String confirmInfo(int OrderId) {
        return elongHotel.confirmInfo(OrderId);
    }

    /**
     * 获取酒店价格
     * @param CityId 城市ID 暂无效
     * @param HotelName 酒店名字 暂无效
     * @param HotelId 酒店ID 多个用“,”号隔开，最多10个
     * @param checkInDate 入住日期
     * @param checkOutDate 离店日期
     */
    public HotelsResult getHotelPrice(String CityId, String HotelName, String HotelId, String checkInDate,
            String checkOutDate) {
        HotelsResult hotelsresult = new HotelsResult();
        //调用方法
        String elmethod = "hotel.detail";
        //艺龙请求参数
        JSONObject reqjson = new JSONObject();
        reqjson.put("ArrivalDate", checkInDate);
        reqjson.put("DepartureDate", checkOutDate);
        reqjson.put("HotelIds", HotelId);
        reqjson.put("PaymentType", PaymentType);
        reqjson.put("Options", "2");//房型、单酒店有效
        //艺龙返回数据
        try {
            //String retstr = ElongHotelInterfaceUtil.postEl(elmethod, reqjson.toString(), "http");
            String retstr = SendPostandGet.submitPost(
                    ElongHotelInterfaceUtil.getMiddleUrl(elmethod, reqjson.toString(), "http"), "", "utf-8").toString();
            //解析返回数据
            JSONObject all = JSONObject.fromObject(retstr);
            String Code = all.getString("Code");
            if ("0".equals(Code)) {
                hotelsresult.setResultCode(Code);
                //Local
                String[] HotelIds = HotelId.split(",");
                int days = ElongHotelInterfaceUtil.getSubDays(checkInDate, checkOutDate);//入、离店之间天数
                //EL
                String Result = all.getString("Result");
                JSONObject result = JSONObject.fromObject(Result);
                if (result.getInt("Count") > 0 && result.getJSONArray("Hotels") != null
                        && result.getJSONArray("Hotels").size() > 0) {
                    JSONArray elHotels = result.getJSONArray("Hotels");
                    List<Hotel> listhotel = new ArrayList<Hotel>();
                    for (int i = 0; i < elHotels.size(); i++) {
                        Hotel hotel = new Hotel();
                        //艺龙信息
                        JSONObject elhotel = elHotels.getJSONObject(i);
                        String elHotelId = elhotel.getString("HotelId");
                        boolean have = false;
                        for (String id : HotelIds) {
                            if (elHotelId.equals(id)) {
                                have = true;
                                break;
                            }
                        }
                        if (!have || !"RMB".equals(elhotel.getString("CurrencyCode"))) {
                            continue;
                        }
                        //酒店ID
                        hotel.setHotelcode(elHotelId);
                        //酒店最低价
                        hotel.setLowestPrice(new BigDecimal(elhotel.getDouble("LowRate") + ""));
                        //房型
                        if (!elhotel.containsKey("Rooms")) {
                            continue;
                        }
                        JSONArray elRooms = elhotel.getJSONArray("Rooms");
                        if (elRooms != null && elRooms.size() > 0) {
                            List<Roomtype> listRoomType = new ArrayList<Roomtype>();
                            for (int j = 0; j < elRooms.size(); j++) {
                                JSONObject elroom = elRooms.getJSONObject(j);
                                String RoomTypeId = elroom.getString("RoomTypeId");
                                String RoomName = elroom.getString("Name");
                                if (!elroom.containsKey("RatePlans")) {
                                    continue;
                                }
                                JSONArray RatePlans = elroom.getJSONArray("RatePlans");
                                if (ElongHotelInterfaceUtil.StringIsNull(RoomTypeId)
                                        || ElongHotelInterfaceUtil.StringIsNull(RoomName) || RatePlans == null
                                        || RatePlans.size() == 0) {
                                    continue;
                                }
                                Roomtype roomType = new Roomtype();
                                //房型ID
                                roomType.setRoomcode(RoomTypeId);
                                //房型名称
                                roomType.setName(RoomName.trim());
                                //取价格计划
                                if (RatePlans != null && RatePlans.size() > 0) {
                                    List<RatePlan> listRatePlan = new ArrayList<RatePlan>();
                                    for (int m = 0; m < RatePlans.size(); m++) {
                                        JSONObject rp = RatePlans.getJSONObject(m);
                                        int RatePlanId = rp.getInt("RatePlanId");//ID
                                        String RatePlanName = rp.getString("RatePlanName").replace("艺龙", "")
                                                .replace(",", "，").trim();//名称
                                        if (RatePlanName.endsWith("."))
                                            RatePlanName = RatePlanName.substring(0, RatePlanName.length() - 1);
                                        boolean Status = rp.getBoolean("Status");//总房态 false--不可销售（可能是满房、部分日期满房、缺少价格）、true--可销售
                                        String CustomerType = rp.getString("CustomerType");//宾客类型
                                        int CurrentAlloment = rp.getInt("CurrentAlloment");//房量限额
                                        //满房
                                        if (CurrentAlloment < 0) {
                                            Status = false;
                                            CurrentAlloment = -1;
                                        }
                                        String PaymentType = rp.getString("PaymentType");//支付方式
                                        double TotalRate = rp.getDouble("TotalRate");//总价格
                                        double AverageRate = rp.getDouble("AverageRate");//日均价
                                        String CurrencyCode = rp.getString("CurrencyCode");//货币
                                        int MinAmount = rp.getInt("MinAmount");//最少预订数量
                                        int MinDays = rp.getInt("MinDays");//最少入住天数
                                        int MaxDays = rp.getInt("MaxDays");//最多入住天数
                                        JSONArray NightlyRates = rp.getJSONArray("NightlyRates");
                                        //判断
                                        if (RatePlanId > 0
                                                && TotalRate > 0
                                                && AverageRate > 0
                                                && "RMB".equals(CurrencyCode)
                                                && "SelfPay".equals(PaymentType)
                                                && days >= MinDays
                                                && days <= MaxDays
                                                && NightlyRates.size() > 0
                                                && ("All".equals(CustomerType) || "Chinese".equals(CustomerType) || "OtherForeign"
                                                        .equals(CustomerType))) {
                                            RatePlan ratePlan = new RatePlan();
                                            //RPID
                                            ratePlan.setRateplanid(RatePlanId);
                                            //RP名称
                                            ratePlan.setRateplanname(RatePlanName);
                                            //客人类型代码
                                            if ("All".equals(CustomerType)) {
                                                ratePlan.setGuestTypeCode(1);//统一价
                                            }
                                            else if ("Chinese".equals(CustomerType)) {
                                                ratePlan.setGuestTypeCode(2);//内宾
                                            }
                                            else if ("OtherForeign".equals(CustomerType)) {
                                                ratePlan.setGuestTypeCode(3);//外宾
                                            }
                                            //总价
                                            ratePlan.setTotelPrice(new BigDecimal(TotalRate + ""));//已经通过DRR的计算，可以直接显示给客人。价格为-1表示不能销售。
                                            //平均价
                                            ratePlan.setAvgPrice(new BigDecimal(AverageRate + ""));
                                            //货币代码
                                            ratePlan.setCurrencyCode(CurrencyCode);
                                            //RP房态 0-正常 1-部分日期满房，2-全部满房
                                            ratePlan.setStatus(Status ? 0 : 1);
                                            //预定最少数量
                                            ratePlan.setMinAmount(MinAmount);
                                            //剩余房量
                                            ratePlan.setCurrentAlloment(CurrentAlloment);
                                            //每天
                                            if (NightlyRates != null && NightlyRates.size() == days) {
                                                List<Rate> listRate = new ArrayList<Rate>();
                                                for (int x = 0; x < NightlyRates.size(); x++) {
                                                    JSONObject NightlyRate = NightlyRates.getJSONObject(x);
                                                    Rate rate = new Rate();
                                                    //对应日期  "Date": "2013-10-10T00:00:00+08:00"
                                                    rate.setDate(ElongHotelInterfaceUtil.toCalendar(NightlyRate
                                                            .getString("Date").split("T")[0], "yyyy-MM-dd"));
                                                    //货币代码
                                                    rate.setCurrencyCode(CurrencyCode);
                                                    //每日房态 0-正常 1、2-全部满房
                                                    if (NightlyRate.getBoolean("Status")
                                                            && NightlyRate.getDouble("Member") > 0) {//表示当天库存是否可用
                                                        rate.setInvStatusCode(0);
                                                    }
                                                    else {
                                                        rate.setInvStatusCode(1);
                                                    }
                                                    //会员价格 已经通过DRR的计算可以直接显示给客人。价格为-1表示不能销售。
                                                    rate.setMemberrate(new BigDecimal(NightlyRate.getDouble("Member")
                                                            + ""));
                                                    //加床价格 -1表示不能加床
                                                    if (NightlyRate.containsKey("AddBed")) {
                                                        rate.setAddbedrate(new BigDecimal(NightlyRate
                                                                .getDouble("AddBed") + ""));
                                                    }
                                                    listRate.add(rate);
                                                }
                                                ratePlan.setListrate(listRate);
                                                listRatePlan.add(ratePlan);
                                            }
                                        }
                                    }
                                    //一个房型、多个产品，进行排序
                                    if (listRatePlan.size() > 1) {
                                        Collections.sort(listRatePlan, new Comparator<RatePlan>() {
                                            @Override
                                            public int compare(RatePlan rpA, RatePlan rpB) {
                                                List<Rate> rA = rpA.getListrate();
                                                List<Rate> rB = rpB.getListrate();
                                                for (Rate rate : rA) {
                                                    for (Rate r : rB) {
                                                        if (rate.getInvStatusCode() == 1 && r.getInvStatusCode() != 1) {
                                                            return 1;
                                                        }
                                                        if (r.getInvStatusCode() == 1 && rate.getInvStatusCode() != 1) {
                                                            return -1;
                                                        }
                                                        if (rate.getMemberrate().doubleValue() >= r.getMemberrate()
                                                                .doubleValue()) {
                                                            return 1;
                                                        }
                                                        return -1;
                                                    }
                                                }
                                                return 0;
                                            }
                                        });
                                    }
                                    if (listRatePlan.size() > 0) {
                                        roomType.setListrateplan(listRatePlan);
                                        listRoomType.add(roomType);
                                    }
                                }
                            }
                            if (listRoomType.size() > 0)
                                hotel.setListroomtype(listRoomType);
                        }
                        listhotel.add(hotel);
                    }
                    if (listhotel.size() > 0)
                        hotelsresult.setListhotel(listhotel);
                }
            }
            else {
                //{"Code":"H002001|入住日期范围无效 request.Request"}
                hotelsresult.setResultCode(Code.split("\\|")[0]);
                hotelsresult.setResultMessage(Code.split("\\|")[1]);
            }
        }
        catch (Exception e) {
            hotelsresult.setResultCode("ERROR");
            String msg = e.getMessage();
            if (ElongHotelInterfaceUtil.StringIsNull(msg)) {
                msg = "GET HOTEL PRICE ERROR.";
            }
            else if (msg.contains("艺龙")) {
                msg = msg.replace("艺龙", "");
            }
            hotelsresult.setResultMessage(msg);
        }
        return hotelsresult;
    }

    /**
     * 酒店是否需要担保
     * @param HotelId 酒店ID
     * @param RoomTypeId  房型ID
     * @param RatePlanId 产品ID
     * @param checkindate 入住时间 yyyy-MM-dd HH:mm:ss
     * @param checkoutdate 离店时间 yyyy-MM-dd HH:mm:ss
     * @param arrivalearlytime 最早到达时间 yyyy-MM-dd HH:mm:ss
     * @param arrivalatetime 最晚到达时间 yyyy-MM-dd HH:mm:ss
     * @param RoomNum 房间数量
     */
    public IsVouchResult isVouch(String HotelId, String RoomTypeId, int RatePlanId, String checkindate,
            String checkoutdate, String arrivalearlytime, String arrivalatetime, int RoomNum) {
        //将请求参数转换为艺龙需要
        checkindate = checkindate.substring(0, 10);//yyyy-MM-dd
        checkoutdate = checkoutdate.substring(0, 10);//yyyy-MM-dd
        int istomorrow = 0;//最晚到店时间 0：当天；1：次日
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            arrivalearlytime = sdf.format(sdf.parse(arrivalearlytime));
            arrivalatetime = sdf.format(sdf.parse(arrivalatetime));
            if (ElongHotelInterfaceUtil.getSubDays(arrivalearlytime.substring(0, 10), arrivalatetime.substring(0, 10)) == 1) {
                istomorrow = 1;//次日
            }
            arrivalearlytime = arrivalearlytime.split(" ")[1].substring(0, 5);//HH:mm
            arrivalatetime = arrivalatetime.split(" ")[1].substring(0, 5);//HH:mm
        }
        catch (Exception e) {
            if (!arrivalearlytime.substring(0, 10).equals(arrivalatetime.substring(0, 10))) {
                istomorrow = 1;//次日
            }
            arrivalearlytime = arrivalearlytime.split(" ")[1].substring(0, 5);//HH:mm
            arrivalatetime = arrivalatetime.split(" ")[1].substring(0, 5);//HH:mm
        }
        IsVouchResult isVouchResult = new IsVouchResult();
        //调用方法
        String elmethod = "hotel.detail";
        //请求艺龙参数
        JSONObject reqjson = new JSONObject();
        reqjson.put("ArrivalDate", checkindate);
        reqjson.put("DepartureDate", checkoutdate);
        reqjson.put("HotelIds", HotelId);
        reqjson.put("RoomTypeId", RoomTypeId);
        reqjson.put("RatePlanId", RatePlanId);
        reqjson.put("PaymentType", PaymentType);
        reqjson.put("Options", "2");//房型、单酒店有效
        //艺龙返回数据
        try {
            //String retstr = ElongHotelInterfaceUtil.postEl(elmethod, reqjson.toString(), "http");
            String retstr = SendPostandGet.submitPost(
                    ElongHotelInterfaceUtil.getMiddleUrl(elmethod, reqjson.toString(), "http"), "", "utf-8").toString();
            //解析返回数据
            JSONObject all = JSONObject.fromObject(retstr);
            String Code = all.getString("Code");
            if ("0".equals(Code)) {
                isVouchResult.setResultCode(Code);
                //EL
                String Result = all.getString("Result");
                JSONObject result = JSONObject.fromObject(Result);
                //解析
                if (result.getInt("Count") != 1) {
                    throw new Exception("酒店为空或有多个.");
                }
                JSONArray elHotels = result.getJSONArray("Hotels");
                if (elHotels == null || elHotels.size() != 1) {
                    throw new Exception("酒店为空或有多个.");
                }
                JSONObject elhotel = elHotels.getJSONObject(0);
                String elHotelId = elhotel.getString("HotelId");
                if (!HotelId.equals(elHotelId)) {
                    throw new Exception("供应酒店ID不一致.");
                }
                //艺龙房型 
                JSONArray elRooms = elhotel.getJSONArray("Rooms");
                if (elRooms.size() != 1) {
                    throw new Exception("房型为空或有多个.");
                }
                JSONObject elRoom = elRooms.getJSONObject(0);
                String elRoomTypeId = elRoom.getString("RoomTypeId");
                if (!RoomTypeId.equals(elRoomTypeId)) {
                    throw new Exception("供应房型ID不一致.");
                }
                //艺龙产品
                JSONArray RatePlans = elRoom.getJSONArray("RatePlans");
                if (RatePlans.size() != 1) {
                    throw new Exception("产品为空或有多个.");
                }
                JSONObject rateplan = RatePlans.getJSONObject(0);
                int elRatePlanId = rateplan.getInt("RatePlanId");
                if (RatePlanId != elRatePlanId) {
                    throw new Exception("供应产品ID不一致.");
                }
                //产品担保
                if (!rateplan.containsKey("GuaranteeRuleIds")) {
                    isVouchResult.setIsVouch("0");//是否需要担保 0-不需要 1-需要
                    //不用担保订单的最后取消时间为：0001-01-01 00:00:00 无意义
                    isVouchResult.setLastCancelTime(ElongHotelInterfaceUtil.toCalendar("0001-01-01 00:00:00",
                            "yyyy-MM-dd HH:mm:ss"));
                    return isVouchResult;
                }
                String GuaranteeRuleIds = rateplan.getString("GuaranteeRuleIds");
                if (ElongHotelInterfaceUtil.StringIsNull(GuaranteeRuleIds)) {
                    isVouchResult.setIsVouch("0");//是否需要担保 0-不需要 1-需要
                    //不用担保订单的最后取消时间为：0001-01-01 00:00:00 无意义
                    isVouchResult.setLastCancelTime(ElongHotelInterfaceUtil.toCalendar("0001-01-01 00:00:00",
                            "yyyy-MM-dd HH:mm:ss"));
                    return isVouchResult;
                }
                //担保规则
                Map<Integer, JSONObject> maps = new HashMap<Integer, JSONObject>();
                JSONArray GuaranteeRules = elhotel.containsKey("GuaranteeRules") ? elhotel
                        .getJSONArray("GuaranteeRules") : new JSONArray();
                for (int i = 0; i < GuaranteeRules.size(); i++) {
                    JSONObject g = GuaranteeRules.getJSONObject(i);
                    maps.put(g.getInt("GuranteeRuleId"), g);
                }
                //解析
                Integer id = Integer.parseInt(GuaranteeRuleIds);
                JSONObject g = maps.get(id);
                if (g == null) {
                    throw new Exception("查询担保规则失败.");
                }
                //开始、结束日期
                String StartDate = g.getString("StartDate").split("T")[0];
                String EndDate = g.getString("EndDate").split("T")[0];
                //担保描述
                String description = "在" + ElongHotelInterfaceUtil.getLocalDate(StartDate) + "至"
                        + ElongHotelInterfaceUtil.getLocalDate(EndDate) + "入住，";
                //IsAmountGuarantee	IsTimeGuarantee	
                //    False	           False	无条件强制担保订单，即必须担保
                //    True	           False	房量担保，检查Amount
                //    False	           True	到店时间担保，检查StartTime 和EndTime
                //    True	           True	房量担保和到店时间担保 同时担保。需要检查Amount、StartTime和EndTime
                int constraint = 0; // 强制担保，false次数
                boolean needguarantee = false;//是否需要担保
                //到店时间担保
                if (g.containsKey("IsTimeGuarantee")) {
                    boolean IsTimeGuarantee = g.getBoolean("IsTimeGuarantee");
                    if (IsTimeGuarantee) {
                        //EL
                        boolean IsTomorrow = g.getBoolean("IsTomorrow");
                        String StartTime = g.getString("StartTime");
                        String EndTime = IsTomorrow ? "24:00" : g.getString("EndTime");
                        /**
                         * 判断 [arrivalearlytime,arrivalatetime)
                         * 开始时间arrivalearlytime的三种情况
                         * 1.arrivalearlytime在StartTime之前
                         * 2.arrivalearlytime在StartTime与EndTime之间
                         * 3.arrivalearlytime在EndTime之后 --> 无担保，弃用
                         */
                        //Local结束时间为当天
                        if (istomorrow == 0) {
                            //1.arrivalearlytime在艺龙StartTime之前、arrivalatetime大于艺龙StartTime
                            if (ElongHotelInterfaceUtil.subTime(arrivalearlytime, StartTime) > 0
                                    && ElongHotelInterfaceUtil.subTime(StartTime, arrivalatetime) > 0) {
                                needguarantee = true;
                            }
                            //2.arrivalearlytime在艺龙StartTime与艺龙EndTime之间
                            if (!needguarantee && ElongHotelInterfaceUtil.subTime(StartTime, arrivalearlytime) >= 0
                                    && ElongHotelInterfaceUtil.subTime(arrivalearlytime, EndTime) >= 0) {
                                needguarantee = true;
                            }
                        }
                        //Local结束时间为次日
                        else {
                            if (ElongHotelInterfaceUtil.subTime(arrivalearlytime, EndTime) >= 0) {
                                needguarantee = true;
                            }
                        }
                        if (needguarantee) {
                            description += "在" + StartTime + "至" + g.getString("EndTime") + "到店，";
                        }
                    }
                    else {
                        constraint++;
                    }
                }
                //房量担保
                if (g.containsKey("IsAmountGuarantee")) {
                    boolean IsAmountGuarantee = g.getBoolean("IsAmountGuarantee");
                    if (IsAmountGuarantee) {//预定几间房以上要担保
                        int Amount = g.getInt("Amount");
                        if (RoomNum >= Amount) {
                            needguarantee = true;
                            description += "预订房量大于或等于" + Amount + "间，";
                        }
                    }
                    else {
                        constraint++;
                    }
                }
                if (constraint == 2) {
                    needguarantee = true; //当 isTimeGuarantee和 isAmountGuarantee都等于false时候表示无条件强制担保
                }
                if (needguarantee) {
                    description += "需要您提供信用卡担保。";
                    isVouchResult.setIsVouch("1");//是否需要担保 0-不需要 1-需要
                    //取消规则
                    String ChangeRule = g.getString("ChangeRule");
                    String Time = "";//统一在艺龙时间基础上加1小时，为客服争取时间
                    //允许变更/取消,需在XX日YY时之前通知
                    if ("NeedSomeDay".equals(ChangeRule)) {
                        Time = ElongHotelInterfaceUtil.getSubTime(
                                g.getString("Day").split("T")[0] + " " + g.getString("Time") + ":00", 1);
                    }
                    //允许变更/取消,需在最早到店时间之前几小时通知
                    else if ("NeedCheckinTime".equals(ChangeRule)) {
                        Time = ElongHotelInterfaceUtil.getSubTime(checkindate + " " + arrivalearlytime + ":00",
                                g.getInt("Hour") + 1);
                    }
                    //允许变更/取消,需在到店日期的24点之前几小时通知
                    else if ("NeedCheckin24hour".equals(ChangeRule)) {
                        Time = ElongHotelInterfaceUtil.getSubTime(checkindate + " 24:00:00", g.getInt("Hour") + 1);
                    }
                    if (ElongHotelInterfaceUtil.StringIsNull(Time)) {
                        Time = new SimpleDateFormat("yyyy-MM-dd HH").format(new Date()) + ":00:00";//临时值  理论不可出现
                    }
                    isVouchResult.setLastCancelTime(ElongHotelInterfaceUtil.toCalendar(Time, "yyyy-MM-dd HH:mm:ss"));
                    if (ElongHotelInterfaceUtil.CurrentTimeGreaterThanThisTime(Time)) {
                        description += "预订后无法变更取消，";
                    }
                    else {
                        //yyyy-MM-dd HH:mm:ss
                        String[] Times = Time.split(" ");
                        description += "允许变更或取消，需在" + ElongHotelInterfaceUtil.getLocalDate(Times[0])
                                + Times[1].substring(0, 5) + "之前通知，";
                    }
                    //担保金额
                    String GuaranteeType = g.getString("GuaranteeType");
                    if ("FirstNightCost".equals(GuaranteeType)) {//首晚
                        isVouchResult.setVouchMoneyType("1");//担保类型	1-首晚担保 2-全额担保
                        description += "如未入住，将扣除首晚房费作为违约金。";

                    }
                    else {
                        isVouchResult.setVouchMoneyType("2");
                        description += "如未入住，将扣除全额房费作为违约金。";
                    }
                    isVouchResult.setCNDescription(description);
                }
                else {
                    isVouchResult.setIsVouch("0");//是否需要担保 0-不需要 1-需要
                    //不用担保订单的最后取消时间为：0001-01-01 00:00:00 无意义
                    isVouchResult.setLastCancelTime(ElongHotelInterfaceUtil.toCalendar("0001-01-01 00:00:00",
                            "yyyy-MM-dd HH:mm:ss"));
                }
            }
            else {
                isVouchResult.setResultCode(Code.split("\\|")[0]);
                isVouchResult.setResultMessage(Code.split("\\|")[1]);
            }
        }
        catch (Exception e) {
            isVouchResult.setResultCode("ERROR");
            String msg = e.getMessage();
            if (ElongHotelInterfaceUtil.StringIsNull(msg)) {
                msg = "GET HOTEL VOUCH ERROR.";
            }
            else if (msg.contains("艺龙")) {
                msg = msg.replace("艺龙", "");
            }
            isVouchResult.setResultMessage(msg);
        }

        return isVouchResult;
    }

    /**获取价格数据接口*/
    public HotelsResult getPriceByHotelId(String StartDate, String EndDate, String HotelID) {
        HotelsResult hotelsresult = new HotelsResult();
        //调用方法
        String elmethod = "hotel.detail";
        //艺龙请求参数
        JSONObject reqjson = new JSONObject();
        reqjson.put("ArrivalDate", StartDate);
        reqjson.put("DepartureDate", EndDate);
        reqjson.put("HotelIds", HotelID);
        reqjson.put("PaymentType", PaymentType);
        reqjson.put("Options", "2");//房型、单酒店有效
        //艺龙返回数据
        try {
            //String retstr = ElongHotelInterfaceUtil.postEl(elmethod, reqjson.toString(), "http");
            String retstr = SendPostandGet.submitPost(
                    ElongHotelInterfaceUtil.getMiddleUrl(elmethod, reqjson.toString(), "http"), "", "utf-8").toString();
            //解析返回数据
            JSONObject all = JSONObject.fromObject(retstr);
            String Code = all.getString("Code");
            if ("0".equals(Code)) {
                hotelsresult.setResultCode(Code);
                //Local
                int days = ElongHotelInterfaceUtil.getSubDays(StartDate, EndDate);//入、离店之间天数
                //EL
                String Result = all.getString("Result");
                JSONObject result = JSONObject.fromObject(Result);
                if (result.getInt("Count") == 1 && result.getJSONArray("Hotels") != null
                        && result.getJSONArray("Hotels").size() == 1) {
                    JSONArray elHotels = result.getJSONArray("Hotels");
                    List<Hotel> listhotel = new ArrayList<Hotel>();
                    //仅循环一次
                    for (int i = 0; i < elHotels.size(); i++) {
                        Hotel hotel = new Hotel();
                        //艺龙信息
                        JSONObject elhotel = elHotels.getJSONObject(i);
                        String elHotelId = elhotel.getString("HotelId");
                        if (!elHotelId.equals(HotelID) || !"RMB".equals(elhotel.getString("CurrencyCode"))) {
                            continue;
                        }
                        //酒店ID
                        hotel.setHotelcode(elHotelId);
                        //酒店最低价
                        hotel.setLowestPrice(new BigDecimal(elhotel.getDouble("LowRate") + ""));
                        //房型
                        if (!elhotel.containsKey("Rooms")) {
                            continue;
                        }
                        JSONArray elRooms = elhotel.getJSONArray("Rooms");
                        if (elRooms != null && elRooms.size() > 0) {
                            List<Roomtype> listRoomType = new ArrayList<Roomtype>();
                            for (int j = 0; j < elRooms.size(); j++) {
                                JSONObject elroom = elRooms.getJSONObject(j);
                                String RoomTypeId = elroom.getString("RoomTypeId");
                                String RoomName = elroom.getString("Name");
                                if (!elroom.containsKey("RatePlans")) {
                                    continue;
                                }
                                JSONArray RatePlans = elroom.getJSONArray("RatePlans");
                                if (ElongHotelInterfaceUtil.StringIsNull(RoomTypeId)
                                        || ElongHotelInterfaceUtil.StringIsNull(RoomName) || RatePlans == null
                                        || RatePlans.size() == 0) {
                                    continue;
                                }
                                Roomtype roomType = new Roomtype();
                                //房型ID
                                roomType.setRoomcode(RoomTypeId);
                                //房型名称
                                roomType.setName(RoomName.trim());
                                //取价格计划
                                if (RatePlans != null && RatePlans.size() > 0) {
                                    List<RatePlan> listRatePlan = new ArrayList<RatePlan>();
                                    for (int m = 0; m < RatePlans.size(); m++) {
                                        JSONObject rp = RatePlans.getJSONObject(m);
                                        int RatePlanId = rp.getInt("RatePlanId");//ID
                                        String RatePlanName = rp.getString("RatePlanName").replace("艺龙", "")
                                                .replace(",", "，").trim();//名称
                                        if (RatePlanName.endsWith("."))
                                            RatePlanName = RatePlanName.substring(0, RatePlanName.length() - 1);
                                        boolean Status = rp.getBoolean("Status");//总房态 false--不可销售（可能是满房、部分日期满房、缺少价格）、true--可销售
                                        String CustomerType = rp.getString("CustomerType");//宾客类型
                                        int CurrentAlloment = rp.getInt("CurrentAlloment");//房量限额
                                        //满房
                                        if (CurrentAlloment < 0) {
                                            Status = false;
                                            CurrentAlloment = -1;
                                        }
                                        String PaymentType = rp.getString("PaymentType");//支付方式
                                        double TotalRate = rp.getDouble("TotalRate");//总价格
                                        double AverageRate = rp.getDouble("AverageRate");//日均价
                                        String CurrencyCode = rp.getString("CurrencyCode");//货币
                                        int MinAmount = rp.getInt("MinAmount");//最少预订数量
                                        int MinDays = rp.getInt("MinDays");//最少入住天数
                                        int MaxDays = rp.getInt("MaxDays");//最多入住天数
                                        JSONArray NightlyRates = rp.getJSONArray("NightlyRates");
                                        //判断
                                        if (RatePlanId > 0
                                                && TotalRate > 0
                                                && AverageRate > 0
                                                && "RMB".equals(CurrencyCode)
                                                && "SelfPay".equals(PaymentType)
                                                && days >= MinDays
                                                && days <= MaxDays
                                                && NightlyRates.size() > 0
                                                && ("All".equals(CustomerType) || "Chinese".equals(CustomerType) || "OtherForeign"
                                                        .equals(CustomerType))) {
                                            RatePlan ratePlan = new RatePlan();
                                            //RPID
                                            ratePlan.setRateplanid(RatePlanId);
                                            //RP名称
                                            ratePlan.setRateplanname(RatePlanName);
                                            //客人类型代码
                                            if ("All".equals(CustomerType)) {
                                                ratePlan.setGuestTypeCode(1);//统一价
                                            }
                                            else if ("Chinese".equals(CustomerType)) {
                                                ratePlan.setGuestTypeCode(2);//内宾
                                            }
                                            else if ("OtherForeign".equals(CustomerType)) {
                                                ratePlan.setGuestTypeCode(3);//外宾
                                            }
                                            //总价
                                            ratePlan.setTotelPrice(new BigDecimal(TotalRate + ""));//已经通过DRR的计算，可以直接显示给客人。价格为-1表示不能销售。
                                            //平均价
                                            ratePlan.setAvgPrice(new BigDecimal(AverageRate + ""));
                                            //货币代码
                                            ratePlan.setCurrencyCode(CurrencyCode);
                                            //RP房态 0-正常 1-部分日期满房，2-全部满房
                                            ratePlan.setStatus(Status ? 0 : 1);
                                            //预定最少数量
                                            ratePlan.setMinAmount(MinAmount);
                                            //剩余房量
                                            ratePlan.setCurrentAlloment(CurrentAlloment);
                                            //每天
                                            if (NightlyRates != null && NightlyRates.size() == days) {
                                                List<Rate> listRate = new ArrayList<Rate>();
                                                for (int x = 0; x < NightlyRates.size(); x++) {
                                                    JSONObject NightlyRate = NightlyRates.getJSONObject(x);
                                                    Rate rate = new Rate();
                                                    //对应日期  "Date": "2013-10-10T00:00:00+08:00"
                                                    rate.setDate(ElongHotelInterfaceUtil.toCalendar(NightlyRate
                                                            .getString("Date").split("T")[0], "yyyy-MM-dd"));
                                                    //用于B2C跳至预订页
                                                    rate.setStartdate(rate.getDate());
                                                    rate.setEnddate(rate.getDate());
                                                    //货币代码
                                                    rate.setCurrencyCode(CurrencyCode);
                                                    //每日房态 0-正常 1、2-全部满房
                                                    if (NightlyRate.getBoolean("Status")
                                                            && NightlyRate.getDouble("Member") > 0) {//表示当天库存是否可用
                                                        rate.setInvStatusCode(0);
                                                    }
                                                    else {
                                                        rate.setInvStatusCode(1);
                                                    }
                                                    //会员价格 已经通过DRR的计算可以直接显示给客人。价格为-1表示不能销售。
                                                    rate.setMemberrate(new BigDecimal(NightlyRate.getDouble("Member")
                                                            + ""));
                                                    //加床价格 -1表示不能加床
                                                    if (NightlyRate.containsKey("AddBed")) {
                                                        rate.setAddbedrate(new BigDecimal(NightlyRate
                                                                .getDouble("AddBed") + ""));
                                                    }
                                                    listRate.add(rate);
                                                }
                                                ratePlan.setListrate(listRate);
                                                listRatePlan.add(ratePlan);
                                            }
                                        }
                                    }
                                    //一个房型、多个产品，进行排序
                                    if (listRatePlan.size() > 1) {
                                        Collections.sort(listRatePlan, new Comparator<RatePlan>() {
                                            @Override
                                            public int compare(RatePlan rpA, RatePlan rpB) {
                                                List<Rate> rA = rpA.getListrate();
                                                List<Rate> rB = rpB.getListrate();
                                                for (Rate rate : rA) {
                                                    for (Rate r : rB) {
                                                        if (rate.getInvStatusCode() == 1 && r.getInvStatusCode() != 1) {
                                                            return 1;
                                                        }
                                                        if (r.getInvStatusCode() == 1 && rate.getInvStatusCode() != 1) {
                                                            return -1;
                                                        }
                                                        if (rate.getMemberrate().doubleValue() >= r.getMemberrate()
                                                                .doubleValue()) {
                                                            return 1;
                                                        }
                                                        return -1;
                                                    }
                                                }
                                                return 0;
                                            }
                                        });
                                    }
                                    if (listRatePlan.size() > 0) {
                                        roomType.setListrateplan(listRatePlan);
                                        listRoomType.add(roomType);
                                    }
                                }
                            }
                            if (listRoomType.size() > 0)
                                hotel.setListroomtype(listRoomType);
                        }
                        listhotel.add(hotel);
                    }
                    if (listhotel.size() > 0)
                        hotelsresult.setListhotel(listhotel);
                }
            }
            else {
                //{"Code":"H002001|入住日期范围无效 request.Request"}
                hotelsresult.setResultCode(Code.split("\\|")[0]);
                hotelsresult.setResultMessage(Code.split("\\|")[1]);
            }
        }
        catch (Exception e) {
            hotelsresult.setResultCode("ERROR");
            String msg = e.getMessage();
            if (ElongHotelInterfaceUtil.StringIsNull(msg)) {
                msg = "GET HOTEL PRICE ERROR.";
            }
            else if (msg.contains("艺龙")) {
                msg = msg.replace("艺龙", "");
            }
            hotelsresult.setResultMessage(msg);
        }
        return hotelsresult;
    }

    /**获取房态*/
    public HotelsResult getHotelInventory(String startDate, String endDate, String HotelId) throws Exception {
        return elongHotel.getHotelInventory(startDate, endDate, HotelId);
    }

    /**根据订单ID查询订单信息*/
    @SuppressWarnings("unchecked")
    public OrderResult getHotelOrderDetailByOrderId(long HotelOrderId) {
        OrderResult orderResult = new OrderResult();
        try {
            if (HotelOrderId <= 0) {
                throw new Exception("外部订单号错误.");
            }
            //判断半年内
            String where = "where LTRIM(RTRIM(C_WAICODE)) = '" + HotelOrderId + "'";
            List<Hotelorder> locals = Server.getInstance().getHotelService().findAllHotelorder(where, "", -1, 0);
            if (locals == null || locals.size() != 1) {
                throw new Exception("通过外部订单号查询本地订单错误.");
            }
            Hotelorder local = locals.get(0);
            //预订时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String bookingtime = sdf.format(local.getPretime());
            //当前时间
            String currenttime = sdf.format(new Date());
            int days = ElongHotelInterfaceUtil.getSubDays(bookingtime, currenttime);
            if (days >= 180) {
                throw new Exception("不支持查询半年前艺龙订单.");
            }
            //调用方法
            String elmethod = "hotel.order.detail";
            //艺龙请求参数
            JSONObject reqjson = new JSONObject();
            reqjson.put("OrderId", HotelOrderId);
            //请求艺龙
            //String retstr = ElongHotelInterfaceUtil.postEl(elmethod, reqjson.toString(), "https");
            String retstr = SendPostandGet.submitPost(
                    ElongHotelInterfaceUtil.getMiddleUrl(elmethod, reqjson.toString(), "https"), "", "utf-8")
                    .toString();
            if (ElongHotelInterfaceUtil.StringIsNull(retstr)) {
                throw new Exception("供应返回信息为空");
            }
            if (!retstr.contains("Code")) {
                throw new Exception(retstr);
            }
            //解析返回数据
            JSONObject all = JSONObject.fromObject(retstr);
            String Code = all.getString("Code");
            if ("0".equals(Code)) {
                orderResult.setResultCode(Code);
                String Result = all.getString("Result");
                JSONObject result = JSONObject.fromObject(Result);
                Hotelorder hotelOrder = new Hotelorder();
                //订单ID
                hotelOrder.setId(result.getLong("OrderId"));
                //订单状态
                hotelOrder.setOrderStates(result.getString("Status"));
                //货币代码
                hotelOrder.setCurrencyCode(result.getString("CurrencyCode"));
                //总价
                hotelOrder.setPrice(Double.toString(result.getDouble("TotalPrice")));
                //房型
                List<Room> roomList = new ArrayList<Room>();
                Room room = new Room();
                //酒店ID
                room.setHotelID(result.getString("HotelId"));
                //房型ID
                room.setRoomTypeId(result.getString("RoomTypeId"));
                //RatePlanID
                room.setRatePlanID(result.getInt("RatePlanId"));
                //入住时间
                room.setCheckInDate(ElongHotelInterfaceUtil.toCalendar(result.getString("ArrivalDate").split("T")[0],
                        "yyyy-MM-dd"));//2013-10-31T00:00:00+08:00
                //离店时间
                room.setCheckOutDate(ElongHotelInterfaceUtil.toCalendar(
                        result.getString("DepartureDate").split("T")[0], "yyyy-MM-dd"));
                //ELONG卡号
                if (result.containsKey("ElongCardNo")
                        && !ElongHotelInterfaceUtil.StringIsNull(result.getString("ElongCardNo"))) {
                    room.setElongCardNo(result.getString("ElongCardNo"));
                }
                //宾客类型代码
                String GuestTypeCode = result.getString("CustomerType");
                if ("All".equals(GuestTypeCode)) {
                    GuestTypeCode = "1";//统一价
                }
                if ("Chinese".equals(GuestTypeCode)) {
                    GuestTypeCode = "2";//内宾
                }
                if ("OtherForeign".equals(GuestTypeCode)) {
                    GuestTypeCode = "3";//外宾
                }
                if ("HongKong".equals(GuestTypeCode)) {
                    GuestTypeCode = "4";//港澳台
                }
                if ("Japanese".equals(GuestTypeCode)) {
                    GuestTypeCode = "5";//日本
                }
                room.setGuestTypeCode(GuestTypeCode);
                //房间数量
                room.setRoomAmount(result.getInt("NumberOfRooms"));
                //入住人数量
                room.setGuestAmount(result.getInt("NumberOfCustomers"));
                //支付方式
                room.setPaymentTypeCode("SelfPay".equals(result.getString("PaymentType")) ? "0" : "1");//0:前台自付 1:预付
                //最早、晚到达时间
                String format = "yyyy-MM-dd HH:mm:ss";
                String EarliestArrivalTime = result.getString("EarliestArrivalTime").replace("T", " ")
                        .substring(0, format.length());//2013-10-31T13:00:00+08:00
                String LatestArrivalTime = result.getString("LatestArrivalTime").replace("T", " ")
                        .substring(0, format.length());//2013-10-31T13:00:00+08:00
                room.setArrivalEarlyTime(ElongHotelInterfaceUtil.toCalendar(EarliestArrivalTime, format));
                room.setArrivalLateTime(ElongHotelInterfaceUtil.toCalendar(LatestArrivalTime, format));
                //货币代码
                room.setCurrencyCode(result.getString("CurrencyCode"));
                //总价
                room.setRoomTotalPrice(new BigDecimal(result.getDouble("TotalPrice") + ""));
                //订单确认方式
                room.setConfirmTypeCode(result.getString("ConfirmationType"));
                //订单确认语言
                room.setConfirmLanguageCode("");
                //给酒店备注
                if (result.containsKey("NoteToHotel")) {
                    room.setNoteToHotel(ElongHotelInterfaceUtil.StringIsNull(result.getString("NoteToHotel")) ? ""
                            : result.getString("NoteToHotel").trim());
                }
                //给艺龙备注
                if (result.containsKey("NoteToElong")) {
                    room.setNoteToElong(ElongHotelInterfaceUtil.StringIsNull(result.getString("NoteToElong")) ? ""
                            : result.getString("NoteToElong").trim());
                }
                //最晚取消时间
                String CancelTime = result.getString("CancelTime").replace("T", " ").substring(0, format.length());//2013-11-01T00:00:00+08:00
                room.setCancelDeadLine(ElongHotelInterfaceUtil.toCalendar(CancelTime, format));
                //联系人
                JSONObject Contact = result.getJSONObject("Contact");
                Contacter contacter = new Contacter();
                //姓名
                contacter.setName(Contact.getString("Name"));
                //性别
                String GenderCode = "2";//未知
                if (Contact.containsKey("Gender")) {
                    if ("Maile".equals(Contact.getString("Gender"))) {
                        GenderCode = "0";
                    }
                    if ("Female".equals(Contact.getString("Gender"))) {
                        GenderCode = "1";
                    }
                }
                contacter.setGenderCode(GenderCode);
                //Email
                if (Contact.containsKey("Email")) {
                    contacter.setEmail(Contact.getString("Email"));
                }
                //手机
                if (Contact.containsKey("Mobile")) {
                    contacter.setMobile(Contact.getString("Mobile"));
                }
                //证件类别代码
                if (Contact.containsKey("IdType")) {
                    contacter.setIdTypeCode(Contact.getString("IdType"));
                }
                //证件号码
                if (Contact.containsKey("IdNo")) {
                    contacter.setIdNumber(Contact.getString("IdNo"));
                }
                room.setContacter(contacter);
                //信用卡
                if (result.containsKey("CreditCard")) {
                    JSONObject CreditCard = result.getJSONObject("CreditCard");
                    Creditcard creditCard = new Creditcard();
                    //信用卡号
                    if (CreditCard.containsKey("Number")) {
                        creditCard.setCreditnumber(CreditCard.getString("Number"));
                    }
                    //证件类型
                    if (CreditCard.containsKey("IdType")) {
                        String IdType = CreditCard.getString("IdType");
                        if ("IdentityCard".equals(IdType)) {
                            IdType = "0";//身份证
                        }
                        if ("Passport".equals(IdType)) {
                            IdType = "1";//护照
                        }
                        if ("Other".equals(IdType)) {
                            IdType = "2";//其他
                        }
                        creditCard.setIdTypeCode(IdType);
                    }
                    //证件号码
                    if (CreditCard.containsKey("IdNo")) {
                        creditCard.setIdNumber(CreditCard.getString("IdNo"));
                    }
                    //交易类型
                    if (CreditCard.containsKey("ProcessType")) {
                        creditCard.setProcessType(CreditCard.getString("ProcessType"));
                    }
                    //交易类型
                    if (CreditCard.containsKey("Status")) {
                        creditCard.setProcessStatus(CreditCard.getString("Status"));
                    }
                    room.setCreditCard(creditCard);
                }
                //客人
                if (result.containsKey("OrderRooms")) {
                    Guest[] guests = new Guest[room.getRoomAmount()];
                    JSONArray OrderRooms = result.getJSONArray("OrderRooms");
                    if (OrderRooms != null && OrderRooms.size() > 0) {
                        List<Guest> guestList = new ArrayList<Guest>();
                        for (int i = 0; i < OrderRooms.size(); i++) {
                            JSONArray Customers = OrderRooms.getJSONObject(i).getJSONArray("Customers");
                            if (Customers != null && Customers.size() > 0) {
                                for (int j = 0; j < Customers.size(); j++) {
                                    Guest guest = new Guest();
                                    JSONObject Customer = Customers.getJSONObject(j);
                                    //姓名
                                    guest.setName(Customer.getString("Name"));
                                    //性别
                                    String Gender = "2";//未知
                                    if (Customer.containsKey("Gender")) {
                                        if ("Maile".equals(Customer.getString("Gender"))) {
                                            Gender = "0";
                                        }
                                        if ("Female".equals(Customer.getString("Gender"))) {
                                            Gender = "1";
                                        }
                                    }
                                    guest.setGenderCode(Gender);
                                    //Email
                                    if (Customer.containsKey("Email")) {
                                        guest.setEmail(Customer.getString("Email"));
                                    }
                                    //手机
                                    if (Customer.containsKey("Mobile")) {
                                        guest.setMobile(Customer.getString("Mobile"));
                                    }
                                    //证件类别代码
                                    if (Customer.containsKey("IdType")) {
                                        guest.setIdTypeCode(Customer.getString("IdType"));
                                    }
                                    //证件号码
                                    if (Customer.containsKey("IdNo")) {
                                        guest.setIdNumber(Customer.getString("IdNo"));
                                    }
                                    //国籍
                                    if (Customer.containsKey("Nationality")) {
                                        guest.setNationality(Customer.getString("Nationality"));
                                    }
                                    //酒店确认号
                                    if (Customer.containsKey("ConfirmationNumber")) {
                                        guest.setHotelbackcode(Customer.getString("ConfirmationNumber"));
                                    }
                                    guestList.add(guest);
                                }
                            }
                        }
                        if (guestList.size() == room.getRoomAmount()) {
                            for (int i = 0; i < guestList.size(); i++) {
                                guests[i] = guestList.get(i);
                            }
                        }
                    }
                    room.setGuests(guests);
                }
                //每日价格
                if (result.containsKey("NightlyRates")) {
                    JSONArray NightlyRates = result.getJSONArray("NightlyRates");
                    if (NightlyRates != null && NightlyRates.size() > 0) {
                        Rate[] rates = new Rate[NightlyRates.size()];
                        for (int i = 0; i < NightlyRates.size(); i++) {
                            Rate rate = new Rate();
                            JSONObject NightlyRate = NightlyRates.getJSONObject(i);
                            //日期
                            rate.setDate(ElongHotelInterfaceUtil.toCalendar(
                                    NightlyRate.getString("Date").split("T")[0], "yyyy-MM-dd"));
                            //会员价格
                            rate.setMemberrate(new BigDecimal(NightlyRate.getDouble("Member") + ""));
                            //加床价格
                            if (NightlyRate.containsKey("AddBed")) {
                                rate.setAddbedrate(new BigDecimal(NightlyRate.getDouble("AddBed") + ""));
                            }
                            rates[i] = rate;
                        }
                        room.setRates(rates);
                    }
                }
                //担保规则
                if (result.containsKey("GuaranteeRule")
                        && result.getJSONObject("GuaranteeRule").containsKey("Description")) {
                    GaranteeRule[] garanteerules = new GaranteeRule[1];
                    GaranteeRule garanteerule = new GaranteeRule();
                    garanteerule.setDescription(result.getJSONObject("GuaranteeRule").getString("Description"));
                    garanteerules[0] = garanteerule;
                    room.setGaranteeRule(garanteerules);
                }
                roomList.add(room);
                hotelOrder.setRoomList(roomList);
                orderResult.setHotelOrder(hotelOrder);
            }
            else {
                //{"Code":"A101010003|错误原因：调用该方法必须使用https","Result":null}
                orderResult.setResultCode(Code.split("\\|")[0]);
                orderResult.setResultMessage(Code.split("\\|")[1]);
            }
        }
        catch (Exception e) {
            orderResult.setResultCode("ERROR");
            String msg = e.getMessage();
            if (ElongHotelInterfaceUtil.StringIsNull(msg)) {
                msg = "GET HOTEL ORDER ERROR.";
            }
            else if (msg.contains("艺龙")) {
                msg = msg.replace("艺龙", "");
            }
            orderResult.setResultMessage(msg);
        }
        return orderResult;
    }

    /**根据多个订单号(英文逗号隔开)获得多个订单信息，返回<订单ID,订单状态>*/
    public Map<String, String> GetHotelOrderListById(String orderidstr) {
        return elongHotel.GetHotelOrderListById(orderidstr);
    }

    /**
     * 验证信用卡是否可用、是否需要CVV码
     * @param CreditCardNo 信用卡号 
     */
    public String creditcardValidate(String CreditCardNo) {
        String returnmsg = "";
        try {
            if (ElongHotelInterfaceUtil.StringIsNull(CreditCardNo)) {
                CreditCardNo = "";
            }
            //调用方法
            String elmethod = "common.creditcard.validate";
            //艺龙请求参数
            JSONObject reqjson = new JSONObject();
            reqjson.put("CreditCardNo", ElongHotelInterfaceUtil.DES(CreditCardNo.trim()));
            //请求艺龙
            //String retstr = ElongHotelInterfaceUtil.postEl(elmethod, reqjson.toString(), "https");
            String retstr = SendPostandGet.submitPost(
                    ElongHotelInterfaceUtil.getMiddleUrl(elmethod, reqjson.toString(), "https"), "", "utf-8")
                    .toString();
            if (ElongHotelInterfaceUtil.StringIsNull(retstr)) {
                throw new Exception("供应返回信息为空");
            }
            if (!retstr.contains("Code")) {
                throw new Exception(retstr);
            }
            //解析返回数据
            JSONObject all = JSONObject.fromObject(retstr);
            String Code = all.getString("Code");
            //{"Code":"0","Result":{"IsValid":false,"IsNeedVerifyCode":false}}
            if ("0".equals(Code)) {
                String Result = all.getString("Result");
                JSONObject result = JSONObject.fromObject(Result);
                returnmsg = "1," + result.getBoolean("IsValid") + "," + result.getBoolean("IsNeedVerifyCode");
            }
            else {
                returnmsg = "0," + Code.replace(",", "，"); // {"Code":"H002001|入住日期范围无效 request.Request"}
            }
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (ElongHotelInterfaceUtil.StringIsNull(msg)) {
                msg = "验证信用卡出错.";
            }
            else if (msg.contains("艺龙")) {
                msg = msg.replace("艺龙", "");
            }
            returnmsg = "0," + msg.replace(",", "，"); // 1.成功 0.失败
        }
        return returnmsg;
    }
}
