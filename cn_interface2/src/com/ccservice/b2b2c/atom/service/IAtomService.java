package com.ccservice.b2b2c.atom.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import com.ccservice.b2b2c.base.carorder.Carorder;
import com.ccservice.b2b2c.base.cars.Cars;
import com.ccservice.b2b2c.base.carstore.Carstore;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.fflight.InstancePrice;
import com.ccservice.b2b2c.base.fflight.InterZefees;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.interflight.SearchFligehtBean;
import com.ccservice.b2b2c.base.interticket.AllRouteBean;
import com.ccservice.b2b2c.base.interticket.RequestCreateOrderBean;
import com.ccservice.b2b2c.base.interticket.ResultSearchFlightByLinesBean;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.recharge.OFRechargeinfo;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;
import com.ccservice.b2b2c.base.specialprice.Specialprice;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.base.util.Insurances;
import com.ccservice.b2b2c.ben.Dnsbarends;
import com.ccservice.b2b2g.bean.GameProduct;
import com.ccservice.b2b2g.bean.PhoneInfo;
import com.ccservice.b2b2g.bean.PhoneProductInfo;

public interface IAtomService {

    /**
     * 网上支付分润
     * 
     * @param orderid
     * @param helpername
     * @param busstype
     *            业务类型 1 机票 2 酒店
     * @return
     */
    public String distribute(long orderid, int ywtype);

    /**
     * 支付宝支付商圈验证
     * 
     * @param useemail
     * @return
     */
    public boolean alipaypartnercheck(String useemail);

    public boolean aplipaycustomerunsign(String useemail);

    /**
     * 国际机票接口
     * 
     * @param strFromCity
     *            出发城市
     * @param strToCity
     *            到达城市
     * @param strFromDate
     *            出发日期
     * @param strseatType
     *            舱位级别
     * @return
     */
    public AllRouteInfo interTicketSearch(String strFromCity, String strToCity, String strFromDate,
            String strReturnDate, String strseatType, String tirptype);

    /**
     * Air86国际机票接口
     * 
     * @param fromcity
     *            出发城市
     * @param tocity
     *            到达城市
     * @param fromdate
     *            出发日期
     * @param cabin
     *            舱位代码
     * @param disc
     *            乘客类型
     * @param aircom
     *            航空公司
     * @return 国际航班数据
     */
    // 单程
    public AllRouteInfo AtmInterTicketSearch(String fromcity, String tocity, String fromdate, String cabin,
            String disc, String aircom);

    // 去程
    public AllRouteInfo getOutBound(String fromcity, String tocity, String fromdate, String todate, String cabin,
            String psgtype, String aircom, Integer psgcount);

    // 回城
    public AllRouteInfo getInBound(String fromcity, String tocity, String fromdate, String todate, String cabin,
            String psgtype, String aircom, Integer psgcount, String contractid);

    /**
     * 通过CBE接口查询航班列表
     * 
     * @param FromCity
     *            起飞城市
     * @param ToCity
     *            到达城市
     * @param TakeoffDate
     *            起飞日期(格式yyyy-MM-dd)
     * @param TakeoffTime
     *            起飞时间(格式HHmm,可以为空)
     * @param Carrier
     *            航空公司,为空则不指定航空公司
     * @param IsDirect
     *            是否只查询直飞航班(0=否,1=是)
     * @param IsShared
     *            是否显示共享航班(0=否,1=是)
     */
    public AllRouteInfo findInterTicketSearchByNBE(String FromCity1, String ToCity1, String TakeoffDate1,
            String TakeoffTime1, String Carrier1, String IsDirect1, String IsShared1);

    public String getAllFlightInfo(String triptype, String fromcity, String tocity, String fromdate, String fareid,
            String contractprice, String gm_f, Integer intTravelType, Integer backflag);

    public String getQFee(String triptype, String company, String qinfo, String hinfo);

    public List<InterZefees> getInterZefees(String fare_id_ob, String depart_date, String return_date, String fare_id_ib);

    public String getInterRules(String fareid);

    public List<InstancePrice> getQTEprice(String trivel_type, String carrier, String qinfo, String hinfo);

    /**
     * QTE获取价格
     * 
     * @param FromCity
     *            起飞机场(多航段用逗号分隔)
     * @param ToCity
     *            到达机场
     * @param TakeoffDate
     *            起飞日期(格式yyyy-MM-dd)
     * @param FlightNum
     *            航班号
     * @param SeatClass
     *            舱位
     * @param PassengerType
     *            乘机人类型(0=成人,1=儿童,2=婴儿,3=留学生)
     * @param IsBottomPrice
     *            是否只显示最低价(0=否,1=是)
     * @param IsFSQ
     *            多个价格是否FSQ(0=否,1=是)
     */
    public String getQTEpricebyLT(String FromCity, String ToCity, String TakeoffDate, String FlightNum,
            String SeatClass, String PassengerType, String IsBottomPrice, String IsFSQ);

    /**
     * 发送普通文本邮件
     * 
     * @param mails
     *            收件人组
     * @param title
     *            标题
     * @param body
     *            正文
     * 
     * @return 发送结果 0:发送成功 -1:发送失败
     */
    public int sendSimpleMails(String[] mails, String title, String body);

    /**
     * 发送html格式邮件
     * 
     * @param mails
     *            收件人组
     * @param title
     *            标题
     * @param body
     *            正文
     * 
     * @return 发送结果 0:发送成功 -1:发送失败
     */
    public int sendHTMLMails(String[] mails, String title, String body);

    /**
     * @param ordernumber
     * @return
     */
    public String getPaystate(String ordernumber);

    /**
     * 发送短信
     * 
     * @param mobiles
     *            手机号码组
     * @param content
     *            短信内容
     * @param type
     *            0 系统 1，机票，2，酒店。3.火车票（已无支付操作），4,手机,QQ充值，5，短信充值。 6.保险购买，7，旅游 8
     *            商旅手伴  9 系统安全短信，代理商不可见
     * @param agentid  当前发短信的加盟商id 
     * 
     * @return 发送结果
     */
    public boolean sendSms(String[] mobiles, String content, long ordercode, long agentid, Dnsbarends dns, int type);

    /**
     * 发送定时短信
     * 
     * @param phone手机号码组
     * @param message短信内容
     * @param addserial
     *            发送给予
     * @param sendTime
     *            即时短信发送时间
     * @return
     */
    public int sendTimeingSms(String[] phone, String message, String addserial, String sendTime);

    /**
     * 发送即时短信
     * 
     * @param phone手机号码组
     * @param message短信内容
     * @param addserial
     *            发送给予
     * @param sendTime
     *            即时短信发送时间
     * @return
     */
    public int sendLuckySms(String[] phone, String message, String addserial);

    public boolean sendTrainorderalarmsms();

    /**
     * 序列号注册
     * 
     * @return
     */
    public int Ymsregister();

    /**
     * 接收上行短信
     * 
     * @return
     */
    public int getmo();

    /**
     * 查询余额
     * 
     * @return
     */
    public int querybalance();

    /**
     * 充值
     * 
     * @param cardno卡号
     * @param cardpass充值密码
     * @return
     */
    public int chargeup(String cardno, String cardpass);

    /**
     * 定制航班动态短信
     * 
     * @param strMobiles
     * @param Flightdate
     * @param Fno
     * @param DepS
     * @param Arr
     * @param Pname
     * @param Type
     * @param Orderid
     * @return
     */
    public int sendFeiyouSms(String strMobiles, String Flightdate, String Fno, String Dep, String Arr, String Pname,
            String Type, String cancel, String Orderid);

    /**
     * 发送带附件的邮件
     * 
     * @param mails
     *            收件人组
     * @param title
     *            标题
     * @param body
     *            正文
     * @param filepaths
     *            附件路径组
     * 
     * @return 发送结果 0:发送成功 -1:发送失败
     */
    public int sendAttachmentMails(String[] mails, String subject, String content, String[] filepaths);

    /**
     * 发送传真
     * 
     * @param faxnum
     *            传真号码
     * 
     * faxstr 文件地址
     */
    public int sendFax(String faxnum, String faxstr);

    public String getMobileInfo(String phonenumber);

    /**
     * @param cardid
     * @return 获取商品信息
     */
    public Map<String, String> getQQmoney(String cardid);

    /**
     * @param cardtype
     *            充值类型：移动联通电信
     * @param cardnum
     *            充值金额对应值 0.2 10，0.4 20
     * @param ordernumber
     *            商户订单号
     * @param mobilenumber
     *            充值手机号
     * @return在线充值
     */
    public String onlineRecharge(int cardtype, String cardnum, String ordernumber, String mobilenumber);

    /**
     * Q币充值
     * 
     * @param ordernumber
     *            商家订单号
     * @param cardid
     *            所需提货商品的编码
     * @param buynum
     *            所需提货商品的数量
     * @param qqnumber
     *            QQ号
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public String qmoneyRecharge(String ordernumber, String cardid, int buynum, String qqnumber);

    /**
     * @return 获取Q币信息
     * @throws IOException
     */
    public List<Map<String, String>> getQmoneyInfo() throws IOException;

    public OFRechargeinfo getPhonerechargeinfo(String phonenumber, float money);

    /**
     * 创建传真发送文件
     */
    public String getHotelTemple(Map<String, String> map);

    public Map<String, String> getUserInfo();

    /**
     * 获得火车列车时刻表
     * 
     * @param fromcity
     * @param tocity
     * @param date
     * @return
     */
    public List<Train> getSKTrainList(String fromcity, String tocity, String date);

    /**
     * 自驾 生成门店信息
     * 
     * @return
     */
    public List<Carstore> GetChooseJourney();

    /**
     * 获得火车列车余票表
     * 
     * @param fromcity
     * @param tocity
     * @param date
     * @return
     */
    public List<Train> getYPTrainList(String fromcity, String tocity, String date);

    /**
     * 获得火车列车代购表
     * 
     * @param fromcity
     * @param tocity
     * @param date
     * @return
     */
    public List<Train> getDGTrainList(String fromcity, String tocity, String date);

    /*
     * 获取火车票的数据在缓存里
     */
    public List<Train> getDGTrainListcache(String fromcity, String tocity, String date, FlightSearch param);

    public List<Map<String, String>> getTrainInfo(String traincode, String date);

    public void createTrainrebate(Train train);

    /**
     * 电子保险接口
     * 
     * @author 赵晓晓
     * @param 保险订单创建
     */
    public List<Insurances> orderAply(String jyNo, Customeruser user, List list, String begintime, String[] fltno)
            throws Exception;

    /**
     * 电子保险单获取
     * 
     * @author 赵晓晓
     * @param
     */
    public DataHandler PolicyReprint(Insurorder order) throws Exception;

    public List newOrderAplylist(String[] jyNo, List list);

    /**
     * 取消或者退保
     * @param insuruserId 保单订单id
     * @return
     */
    public String cancelInsuruser(Insuruser insur);

    public String createticketorder(List<Segmentinfo> listsegment, List<Passenger> listpassenger, Orderinfo orderinfo);

    /**
     * 自驾 得到车型选择列表
     * 
     * @param rentStoreID
     * @param returnStoreID
     * @param rentTime
     * @param returnTime
     * @param rentD
     * @param returnD
     * @return
     */
    public List<Cars> searchCars(int rentStoreID, int returnStoreID, String rentTime, String returnTime, String rentD,
            String returnD);

    /**
     * 代驾 汽车列表
     * 
     * @param productClassID
     * @param preGetOnTime
     * @param preGetOffTime
     * @param getOnAreaID
     * @param getoffAreaID
     * @param personCount
     * @param getOnTime
     * @param getOffTime
     * @param useMileage
     * @param hasDriverBide
     * @param GoOnAddress
     * @param GoOffAddress
     * @param GoOnAddressXCoordinate
     * @param GoOnAddressYCoordinate
     * @param GoOffAddressXCoordinate
     * @param GoOffAddressYCoordinate
     * @param MapType
     * @return
     */
    public List<Cars> searchDriverCars(int productClassID, String preGetOnTime, String preGetOffTime, int getOnAreaID,
            int getoffAreaID, int personCount, String getOnTime, String getOffTime, double useMileage,
            boolean hasDriverBide, String GoOnAddress, String GoOffAddress, double GoOnAddressXCoordinate,
            double GoOnAddressYCoordinate, double GoOffAddressXCoordinate, double GoOffAddressYCoordinate, int MapType);

    /**
     * 自驾 至尊创建订单方法
     * 
     * @param custName
     * @param custPaperNo
     * @param custPaperTypeID
     * @param gender
     * @param mobilePhone
     * @param modelID
     * @param rentStoreID
     * @param rentTime
     * @param returnTime
     * @param rentD
     * @param returnD
     * @param reservationType
     * @param returnStoreID
     * @return
     */
    public String createCarOrder(String custName, String custPaperNo, String custPaperTypeID, String gender,
            String mobilePhone, String modelID, String rentStoreID, String rentTime, String returnTime, String rentD,
            String returnD, String reservationType, String returnStoreID);

    /**
     * 自驾特价 创建订单
     * 
     * @param strSchemeID
     *            特价车方案ID
     * @param returnStoreID
     *            还车门店ID
     * @param rentTime
     *            租车时间
     * @param returnTime
     *            还车时间
     * @param isNeedGPS
     *            是否需要GPS
     * @param customerName
     *            客户姓名
     * @param mobilePhoneNo
     *            手机号
     * @param custPaperID
     *            证件类型ID (43二代身份证 1419 回乡证 1420台胞证 123 国际护照 519其它证件)
     * @param paperNo
     *            证件号
     * @param gender
     *            姓别（true为男性，false为女性）
     * @param isHasSixMonthDrivingLicense
     *            是否有6个月以上驾驶证
     * @param linkmanName
     *            联系人姓名
     * @param linkmanmobilePhoneNo
     *            联系人手机号
     * @param remark
     *            备注（可为空）
     * @param reservationType
     *            预订类型（1代表预定；2代表预约）
     * @return
     */
    public String specialCarSaveOrder(String strSchemeID, int returnStoreID, String rentTime, String returnTime,
            boolean isNeedGPS, String customerName, String mobilePhoneNo, int custPaperID, String paperNo,
            boolean gender, boolean isHasSixMonthDrivingLicense, String linkmanName, String linkmanmobilePhoneNo,
            String remark, int reservationType);

    /**
     * 自驾 得到支付的跳转地址
     * 
     * @param billID
     * @return
     */
    public String getPayUrl(int billID);

    public String GetChauffeurDriveOrderSave(String custname, boolean gender, String mobilePhone, String passengerName,
            boolean passengerGender, String passengerMobilePhone, int personCount, String preGetOnTime,
            String preGetOffTime, int getOnAreaID, String GoOnAddress, int getoffAreaID, String GoOffAddress,
            boolean isNeedInvoice, String invoiceHead, String invoiceMailAddress, String travelDescription,
            int carGroupID, int productClassID, int airportTypeID, String flightCode, boolean isNeedPickBrand,
            String pickBrandContent, int LuggageCount, String getOnTime, String getOffTime, boolean hasDriverBide,
            String operatorSN, double GoOnAddressXCoordinate, double GoOnAddressYCoordinate,
            double GoOffAddressXCoordinate, double GoOffAddressYCoordinate, int MapType, int CouponID, String memo);

    /**
     * 取消订单
     * 
     * @param orderID
     * @return
     */
    public String CancelOrder(int orderID);

    /**
     * 自驾 取消订单
     * 
     * @param orderID
     * @return
     */
    public String selfCancelOrder(int orderID);

    public Carorder GetChauffeurDriveOrderInfo(int orderID);

    public List<Cars> getSpecialCarList(long cityid);

    public String[] getSpecialCarList1(long cityid);

    /**
     * 
     * @param isinternal
     *            是否国际 1国际0国内
     * @param startport
     *            起始地
     * @param arrivalport
     *            抵达地
     * @return
     */
    public List<Specialprice> getSpecialpriceList(String isinternal, String startport, String arrivalport);

    // =====================国际机票==start==huc=====================================================
    /**
     * 国际机票查询参数类得到航班信息列表 huc
     * 
     * @param paramsByjson
     */
    public AllRouteBean searchingFligehts(SearchFligehtBean searchFligehtBean);

    public ResultSearchFlightByLinesBean getInterTicket(SearchFligehtBean searchFligehtBean);

    /**
     * 国际机票创建订单 huc
     * 
     * @param resultStr
     */
    public String createOrder(RequestCreateOrderBean requestCreateOrderBean);

    /**
     * 国际机票接受价格变化接口 huc
     * 
     * @param searchFligehtBean
     */
    public String priceChange(String orderid);

    /**
     * 国际机票接受行程变化接口 huc
     * 
     * @param searchFligehtBean
     */
    public String strokeChange(String orderid);

    /**
     * 国际机票订单查询接口 huc
     * 
     * @param orderid
     */
    public String searchingOrder(String orderid);

    /**
     * 国际机票退票接口 huc
     * 
     * @param orderid
     */
    public String returnTicket(String orderid);

    // =====================国际机票==end==huc=====================================================

    public String getMobileSMS(String MCode);

    /**
     * 查询air86国际机票接口(新接口 2013-06-20 air86 v2.0)
     * 
     * @param tripType
     *            旅行类型 1单程 2往返
     * @param peoNum
     *            人数 (返回数据都是一个人的,可能以后有变化)
     * @param proType
     *            乘客类型  1成人 2儿童 3婴儿（不占座） (返回数据都是成人的,可能以后有变化)
     * @param cbinType
     *            仓位 F头等舱 C商务舱 Y 经济舱
     * @param origCity
     *            出发城市
     * @param destCity
     *            抵达城市
     * @param srcDate
     *            去程出发日期
     * @param retDate
     *            返程出发日期
     * @return list集合数组 list[0]去程信息 list[1]返程信息 返程信息可能为空
     */
    public List[] searchingRouteInfo(String tripType, String peoNum, String proType, String cbinType, String origCity,
            String destCity, String srcDate, String retDate);

    /**
     * 查询air86国际机票税费的接口(新接口 2013-06-20 air86 v2.0)
     * 
     * @param tripType
     *            是否有多个行程 1 单个行程 2 2个行程
     * @param peoNum
     *            人数 (返回数据都是一个人的,可能以后有变化)
     * @param proType
     *            乘客类型 1成人 2儿童 3婴儿（不占座） (返回数据都是成人的,可能以后有变化)
     * @param str1
     *            第一程 数据字符串 数据格式是逗号分隔
     *            数据顺序：航空公司代码，航班号，仓位信息（航班的仓位），出发日期，起飞机场代码，抵达机场代码，出发时间(8000)，抵达时间
     * @param str2
     *            第二程 数据字符串 同上
     * @return 税费
     */
    public String searchingTaxQuery(String tripType, String peoNum, String proType, String str1, String str2);

    /**
     * 实时获取机票真实价格和税费 如(1350,105)
     * 
     * @param tripType
     *            单程 1 往返 2
     * @param proNum
     *            人数 (返回数据都是成人的,可能以后有变化)
     * @param proType
     *            乘客类型 1成人 2儿童 3婴儿（不占座）(返回数据都是成人的,可能以后有变化)
     * @param str1
     *            第一程 数据字符串(如出现中转用@再拼接一个一样的字符串) 数据格式是逗号分隔
     *            数据顺序：航空公司代码，航班号，仓位信息(A,B,C,D..)，出发日期，起飞机场代码，抵达机场代码，出发时间(8000)，抵达时间
     * @param str2
     *            第二程 数据字符串 同上
     * @return
     */
    public String searchingFareVerify(String tripType, String peoNum, String proType, String str1, String str2);

    /**
     * 天衢保险 创建订单
     * 
     * @param ainame
     *            投保人姓名
     * @param aimobile
     *            投保人电话
     * @param aiaddress
     *            投保人地址 可以为空
     * @param aiidcard
     *            证件类型:1 身份证 2 护照 3 军人证 4 港台同胞证 5 其它证件
     * @param aiidnumber
     *            证件号
     * @param aieffectivedate
     *            生效日期
     * @param aiemail
     *            电子邮件
     * @param aisex
     *            投保人性别 -1保密 1 男 0 女
     * @param aiflightnumber
     *            航班号
     * @return
     */
    public String tianQuCreateOrder(String ainame, String aimobile, String aiaddress, String aiidcard,
            String aiidnumber, String aieffectivedate, String aiemail, String aisex, String aiflightnumber);

    /**
     * 天衢保险 查询订单详细
     * 
     * @param orderid
     *            订单号
     * @return
     */
    public String tianQuOrderInfo(String orderid);

    /**
     * 天衢保险 取消订单
     * 
     * @param orderid
     *            订单号
     * @return
     */
    public String tianQuCancleOrder(String orderid);

    /**
     * 天衢保险 第一次投保
     * 
     * @param orderid
     *            订单号
     * @return
     */
    public String tianQuPayOrder(String orderid);

    /**
     * 天衢保险 退保
     * 
     * @param orderNumber
     *            保单号
     * @return
     */
    public String tianQuBackOrder(String orderNumber);

    /**
     * 天衢保险 投保失败时再次投保
     * 
     * @param orderid
     *            订单号
     * @param ainame
     *            投保人姓名
     * @param aisex
     *            投保人性别 -1保密 1 男 0 女
     * @param aiidnumber
     *            证件号
     * @param aimobile
     *            投保人电话
     * @param aiemail
     *            电子邮件
     * @param aiaddress
     *            投保人地址 可以为空
     * @param aiflightnumber
     *            航班号
     * @return
     */
    public String tianQuPayOrderAgain(String orderid, String ainame, String aisex, String aiidnumber, String aimobile,
            String aiemail, String aiaddress, String aiflightnumber);

    public String getNetworkOrder(Train train, Trainpassenger passenger);

    /**
     * 火车票保险下单接口
     * @param jyNo
     * @param list
     * @param type
     * @return
     */
    public List saveTrainOrderAplylist(String[] jyNo, List list, int type);

    /**
     * 手机号码归属地查询
     * 
     * @param mobilenum 手机号码
     * @return
     */
    public PhoneInfo accsegment(String mobilenum);

    /**
     * 话费充值产品查询
     * 
     * @return
     */
    public List<PhoneProductInfo> productQuery();

    /**
     * 话费直冲
     * 
     * @param prodid  产品id
     * @param orderid   我们系统生成的订单号(注：该订单号由代理商商城系统生成。orderid唯一确定一条订单。)
     * @param mobilenum  充值号码
     * 
     * @return  json格式:{"prodidValue":"","orderidValue":"","tranidValue":"","resultnoValue":"","markValue":"","verifystringValue":""}
     */
    public String directFill(String prodid, String orderid, String mobilenum);

    /**
     * 话费订单查询
     * 
     * @param orderid  我们系统生成的订单号(注：该订单号由代理商商城系统生成。orderid唯一确定一条订单。)
     * 
     * @return  json格式:{"orderidValue":"","resultnoValue":"","finishmoneyValue":"","verifystringValue":""}
     */
    public String orderQuery(String orderid);

    /**
     * 可充值游戏查询接口（注：只获取Q币相关数据）
     * 
     * @return
     */
    public String canRechargeGameQuery();

    /**
     * 游戏产品查询接口（注：只获取Q币相关数据）
     * 
     * @param gameid  游戏编号
     * @return
     */
    public List<GameProduct> gameProductQuery(String gameid);

    /**
     * 直接充值接口（注：只对Q币进行充值）
     * 
     * @param gameid 游戏编号
     * @param parvalue 游戏面值 格式：10.0
     * @param orderid  本地系统订单号
     * @param chargeaccount  用户账号
     * @param fillnum  购买数量，正整数
     * @return
     */
    public String directRecharge(String gameid, String parvalue, String orderid, String chargeaccount, int fillnum,
            String clientip);

    /**
     * Q币订单结果查询
     * 
     * @param orderid 本地系统订单号
     * 
     * @return  json格式：{"orderstatus":"","fillmoney":"","finishmoney":""}
     */
    public String orderResultQuery(String orderid);

    /**
     * 系统自动代扣接口
     * @param orderid 待支付订单号
     * @param helpername  支付处理类
     * @param payacount 要支付的支付宝帐号
     */
    public String autopayace(long orderid, String helpername, String payacount);
}
