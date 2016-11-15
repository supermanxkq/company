package com.ccservice.b2b2c.atom.car;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.axis2.AxisFault;

import client.CommonFunctionWebServiceStub;
import client.SelfDriveSerivceStub;
import client.SpecialPriceCarServiceStub;
import client.TopOneChauffeurDriverServiceStub;
import client.SelfDriveSerivceStub.ArrayOfCarModelSimple;
import client.SelfDriveSerivceStub.CarModelSimple;
import client.SelfDriveSerivceStub.ChooseCarInfo;
import client.SelfDriveSerivceStub.JourneyData;
import client.SelfDriveSerivceStub.Store;
import client.SpecialPriceCarServiceStub.SpecialPrice;
import client.TopOneChauffeurDriverServiceStub.ActivityCoupon;
import client.TopOneChauffeurDriverServiceStub.Area;
import client.TopOneChauffeurDriverServiceStub.ArrayOfChauffeurDrivePerDayDetails;
import client.TopOneChauffeurDriverServiceStub.ArrayOfCustomerOrderList;
import client.TopOneChauffeurDriverServiceStub.ArrayOfDayChargeDetails;
import client.TopOneChauffeurDriverServiceStub.ArrayOfDayDetails;
import client.TopOneChauffeurDriverServiceStub.CancelOrder;
import client.TopOneChauffeurDriverServiceStub.CancelOrderResponse;
import client.TopOneChauffeurDriverServiceStub.ChauffeurDriveCostDetail;
import client.TopOneChauffeurDriverServiceStub.ChauffeurDriveOrderBaseInfo;
import client.TopOneChauffeurDriverServiceStub.ChauffeurDriveOrderInfo;
import client.TopOneChauffeurDriverServiceStub.ChauffeurDrivePerDayDetails;
import client.TopOneChauffeurDriverServiceStub.CustomerOrderList;
import client.TopOneChauffeurDriverServiceStub.DayChargeDetails;
import client.TopOneChauffeurDriverServiceStub.DayDetails;
import client.TopOneChauffeurDriverServiceStub.GetActivityCouponListByDistributor;
import client.TopOneChauffeurDriverServiceStub.GetActivityCouponListByDistributorResponse;
import client.TopOneChauffeurDriverServiceStub.GetChauffeurDriveCostDetail;
import client.TopOneChauffeurDriverServiceStub.GetChauffeurDriveCostDetailResponse;
import client.TopOneChauffeurDriverServiceStub.GetChauffeurDriveOrderInfo;
import client.TopOneChauffeurDriverServiceStub.GetChauffeurDriveOrderInfoResponse;
import client.TopOneChauffeurDriverServiceStub.GetChauffeurDriveOrderSave;
import client.TopOneChauffeurDriverServiceStub.GetChauffeurDriveOrderSaveResponse;
import client.TopOneChauffeurDriverServiceStub.GetChooseCarLevelListResponse;
import client.TopOneChauffeurDriverServiceStub.GetCustomerOrder;
import client.TopOneChauffeurDriverServiceStub.GetCustomerOrderResponse;
import client.TopOneChauffeurDriverServiceStub.GetOffCityArea;
import client.TopOneChauffeurDriverServiceStub.SaveResult;
import client.TopOneChauffeurDriverServiceStub.ThirdCarGroupPrice;
import client.TopOneChauffeurDriverServiceStub.ValidationSoapHeader;
import client.TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.carbrand.Carbrand;
import com.ccservice.b2b2c.base.carimages.Carimages;
import com.ccservice.b2b2c.base.carinfo.Carinfo;
import com.ccservice.b2b2c.base.carorder.Carorder;
import com.ccservice.b2b2c.base.cars.Cars;
import com.ccservice.b2b2c.base.carstore.Carstore;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.eaccount.Eaccount;

/**
 * 至尊自驾租车方法 2012-5-10 12:00:00
 * 
 * @author 陈栋
 * 
 */
@SuppressWarnings("serial")
public class SupermacyMethod implements Serializable {

    /**
     * 自驾 得到支付的跳转地址
     * 
     * @param billID
     * @return
     */
    public String getPayUrl(int billID) {
        String result = "";
        try {
            Supermacy supermacy = new Supermacy();
            CommonFunctionWebServiceStub.ValidationSoapHeader param = new CommonFunctionWebServiceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            CommonFunctionWebServiceStub.ValidationSoapHeaderE validationSoapHeaderE = new CommonFunctionWebServiceStub.ValidationSoapHeaderE();
            validationSoapHeaderE.setValidationSoapHeader(param);

            CommonFunctionWebServiceStub.GetPaymentURL getPaymentURL = new CommonFunctionWebServiceStub.GetPaymentURL();
            getPaymentURL.setBillID(billID);
            getPaymentURL.setPayTypeID(4);
            getPaymentURL.setErrorUrl("");
            getPaymentURL.setBackUrl("");
            getPaymentURL.setBillID(1);

            CommonFunctionWebServiceStub stub = new CommonFunctionWebServiceStub();
            CommonFunctionWebServiceStub.GetPaymentURLResponse response = stub.getPaymentURL(getPaymentURL,
                    validationSoapHeaderE);

            if (response.getGetPaymentURLResult().getErrrorMsg() != null
                    && !response.getGetPaymentURLResult().getErrrorMsg().equals("")) {
                try {
                    System.out.println("getErrrorMsg====" + response.getGetPaymentURLResult().getErrrorMsg());
                    result = "";
                }
                catch (Exception e) {
                    System.out.println(e);
                }
            }
            else {
                result = response.getGetPaymentURLResult().getURL();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 自驾 至尊现有车型品牌列表
     * 
     */
    public void GetCarModelBrandList() {
        try {
            Supermacy supermacy = new Supermacy();
            SelfDriveSerivceStub.ValidationSoapHeader param = new SelfDriveSerivceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            SelfDriveSerivceStub.ValidationSoapHeaderE validationSoapHeaderE = new SelfDriveSerivceStub.ValidationSoapHeaderE();
            validationSoapHeaderE.setValidationSoapHeader(param);

            SelfDriveSerivceStub.GetCarModelBrandList getCarModelBrandList = new SelfDriveSerivceStub.GetCarModelBrandList();

            SelfDriveSerivceStub stub = new SelfDriveSerivceStub();
            SelfDriveSerivceStub.GetCarModelBrandListResponse response = stub.getCarModelBrandList(
                    getCarModelBrandList, validationSoapHeaderE);

            SelfDriveSerivceStub.CarModelBrand[] carModelBrands = response.getGetCarModelBrandListResult()
                    .getCarModelBrand();
            for (SelfDriveSerivceStub.CarModelBrand carModelBrand : carModelBrands) {
                Carbrand carbrand = new Carbrand();
                carbrand.setCode(carModelBrand.getBrandID() + "");
                carbrand.setName(carModelBrand.getBrandName());
                carbrand.setComment(carModelBrand.getBrandName());
                Server.getInstance().getCarService().createCarbrand(carbrand);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自驾 至尊创建订单方法
     * 
     * @author 陈栋 2012-5-15 11:28:52
     * @param custName
     *            客户姓名
     * @param custPaperNo
     *            证件号
     * @param custPaperTypeID
     *            证件类型ID 43二代身份证1419回乡证1420台胞证123 国际护照519其它证件
     * @param gender
     *            性别（true代表男性false代表女性）
     * @param mobilePhone
     *            手机号
     * @param modelID
     *            车型ID
     * @param rentStoreID
     *            租车门店ID
     * @param rentTime
     *            租车日期yyyy-MM-dd
     * @param returnTime
     *            换车日期yyyy-MM-dd
     * @param rentD
     *            租车时间hh:mm
     * @param returnD
     *            还车时间hh:mm
     * @param reservationType
     *            预订类型（1代表预定；2代表预约）
     * @param returnStoreID
     *            还车门店ID
     */
    public String CreateCarOrder(String custName, String custPaperNo, String custPaperTypeID, String gender,
            String mobilePhone, String modelID, String rentStoreID, String rentTime, String returnTime, String rentD,
            String returnD, String reservationType, String returnStoreID) {
        String resultString = new String();
        try {
            Supermacy supermacy = new Supermacy();
            SelfDriveSerivceStub.ValidationSoapHeaderE validationSoapHeaderE = new SelfDriveSerivceStub.ValidationSoapHeaderE();
            SelfDriveSerivceStub.ValidationSoapHeader param = new SelfDriveSerivceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            validationSoapHeaderE.setValidationSoapHeader(param);

            SelfDriveSerivceStub.SelfDriveOrderSaveExt selfDriveOrderSaveExt = new SelfDriveSerivceStub.SelfDriveOrderSaveExt();
            selfDriveOrderSaveExt.setCouponProductID(0);// 分销商优惠券ID（无就传0）
            selfDriveOrderSaveExt.setCustName(custName);// 客户姓名
            selfDriveOrderSaveExt.setCustPaperNo(custPaperNo);// 证件号
            // 证件类型ID: 43二代身份证 1419回乡证 1420台胞证 123国际护照 519其它证件
            selfDriveOrderSaveExt.setCustPaperTypeID(Integer.parseInt(custPaperTypeID));
            // 性别（true代表男性；false代表女性）
            if (gender.equals("1")) {
                selfDriveOrderSaveExt.setGender(true);
            }
            else {
                selfDriveOrderSaveExt.setGender(false);
            }
            // 是否使用GPS导航仪(true表示使用，false表示不使用)
            selfDriveOrderSaveExt.setIsUseGPSNavigator(false);
            selfDriveOrderSaveExt.setMobilePhone(mobilePhone);// 手机号
            selfDriveOrderSaveExt.setModelID(Integer.parseInt(modelID));// 车型ID
            selfDriveOrderSaveExt.setRemark("");
            selfDriveOrderSaveExt.setRentStoreID(Integer.parseInt(rentStoreID));// 租车门店ID
            selfDriveOrderSaveExt.setReservationType(Integer.parseInt(reservationType));// 预订类型（1代表预定；2代表预约）
            selfDriveOrderSaveExt.setReturnStoreID(Integer.parseInt(returnStoreID));// 还车门店ID
            Calendar rentcal = GregorianCalendar.getInstance();
            Calendar returncal = GregorianCalendar.getInstance();
            Date date = new Date();
            date.setMonth(Integer.parseInt(rentTime.split("[-]")[1]) - 1);
            date.setDate(Integer.parseInt(rentTime.split("[-]")[2]));
            date.setHours(Integer.parseInt(rentD.split("[:]")[0]));
            date.setMinutes(Integer.parseInt(rentD.split("[:]")[1]));
            rentcal.setTime(date);
            rentcal.set(Integer.parseInt(rentTime.split("[-]")[0]), Integer.parseInt(rentTime.split("[-]")[1]) - 1,
                    Integer.parseInt(rentTime.split("[-]")[2]), Integer.parseInt(rentD.split("[:]")[0]),
                    Integer.parseInt(rentD.split("[:]")[1]));
            int hour = rentcal.get(Calendar.HOUR_OF_DAY);
            rentcal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(rentD.split("[:]")[0]));
            int minute = rentcal.get(Calendar.MINUTE);
            rentcal.set(Calendar.MINUTE, Integer.parseInt(rentD.split("[:]")[1]));
            Date returndate = new Date();
            returndate.setMonth(Integer.parseInt(returnTime.split("[-]")[1]) - 1);
            returndate.setDate(Integer.parseInt(returnTime.split("[-]")[2]));
            returndate.setHours(Integer.parseInt(returnD.split("[:]")[0]));
            returndate.setMinutes(Integer.parseInt(returnD.split("[:]")[1]));
            returncal.setTime(returndate);
            returncal.set(Integer.parseInt(returnTime.split("[-]")[0]),
                    Integer.parseInt(returnTime.split("[-]")[1]) - 1, Integer.parseInt(returnTime.split("[-]")[2]),
                    Integer.parseInt(returnD.split("[:]")[0]), Integer.parseInt(returnD.split("[:]")[1]));
            returncal.get(Calendar.HOUR_OF_DAY);
            returncal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(returnD.split("[:]")[0]));
            returncal.get(Calendar.MINUTE);
            returncal.set(Calendar.MINUTE, Integer.parseInt(returnD.split("[:]")[1]));
            selfDriveOrderSaveExt.setRentTime(rentcal);// 租车时间
            selfDriveOrderSaveExt.setReturnTime(returncal);// 还车时间

            SelfDriveSerivceStub stub = new SelfDriveSerivceStub();
            SelfDriveSerivceStub.SelfDriveOrderSaveExtResponse response = stub.selfDriveOrderSaveExt(
                    selfDriveOrderSaveExt, validationSoapHeaderE);
            SelfDriveSerivceStub.SaveResult result = response.getSelfDriveOrderSaveExtResult();
            if (result.getIsSuccess()) {
                result.getBillID();// 订单号
                result.getRealCharge();// 支付金额
                resultString = result.getBillID() + "," + result.getRealCharge();
            }
            else {
                try {
                    if (result.getErrrorMsg().length() > 0) {
                        System.out.println("getErrrorMsg===" + result.getErrrorMsg());
                        return "";
                    }
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 自驾 得到车型选择列表
     * 
     * @param rentStoreID
     * @param returnStoreID
     * @param rentTime
     * @param returnTime
     * @param rentD
     * @param returnD
     * @return 车型列表
     */
    @SuppressWarnings("deprecation")
    public List<Cars> getCarList(int rentStoreID, int returnStoreID, String rentTime, String returnTime, String rentD,
            String returnD) {
        List<Cars> carss = new ArrayList<Cars>();

        Calendar rentcal = Calendar.getInstance();
        Calendar returncal = Calendar.getInstance();
        String str = rentTime + " " + rentD;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(str);
            rentcal.setTime(date);
        }
        catch (ParseException e1) {
            e1.printStackTrace();
        }
        String str1 = returnTime + " " + returnD;
        try {
            Date date1 = sdf.parse(str1);
            returncal.setTime(date1);
        }
        catch (ParseException e1) {
            e1.printStackTrace();
        }
        try {
            Supermacy supermacy = new Supermacy();
            SelfDriveSerivceStub.ValidationSoapHeader param = new SelfDriveSerivceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            SelfDriveSerivceStub stub = new SelfDriveSerivceStub();
            SelfDriveSerivceStub.ValidationSoapHeaderE validationSoapHeaderE = new SelfDriveSerivceStub.ValidationSoapHeaderE();
            validationSoapHeaderE.setValidationSoapHeader(param);

            SelfDriveSerivceStub.GetChooseCarList getChooseCarList = new SelfDriveSerivceStub.GetChooseCarList();
            getChooseCarList.setRentStoreID(rentStoreID);
            getChooseCarList.setRentTime(rentcal);
            getChooseCarList.setReturnStoreID(returnStoreID);
            getChooseCarList.setReturnTime(returncal);

            SelfDriveSerivceStub.GetChooseCarListResponse response = stub.getChooseCarList(getChooseCarList,
                    validationSoapHeaderE);
            SelfDriveSerivceStub.ChooseCarListResult result = response.getGetChooseCarListResult();
            ChooseCarInfo[] chooseCarInfos = null;
            if (result.getErrrorMsg().length() != 0) {
                return carss;
            }
            if (result.getListChooseCarInfo().getChooseCarInfo() != null) {
                chooseCarInfos = result.getListChooseCarInfo().getChooseCarInfo();
                for (int i = 0; i < chooseCarInfos.length; i++) {
                    ChooseCarInfo chooseCarInfo = chooseCarInfos[i];
                    Cars car = new Cars();
                    // StoreID int 门店ID
                    car.setCarstoreid((long) chooseCarInfo.getStoreID());
                    // CityID int 城市ID
                    car.setCityid((long) chooseCarInfo.getCityID());
                    // ModelID int 车型 ID
                    car.setCode(chooseCarInfo.getModelID() + "");
                    // ModelName string 车型名称
                    car.setName(chooseCarInfo.getModelName());
                    // LimitMileage double 限制里程
                    car.setMile("不限");
                    // ExceedDistanceCharge double 超里程单价
                    // DeliveryCapacity string 排量
                    chooseCarInfo.getDeliveryCapacity();// 排量
                    // OilVolume string 油箱
                    chooseCarInfo.getOilVolume();
                    // Seating string 座位数
                    car.setMaxpassenger(chooseCarInfo.getSeating());
                    // CoachCount string 厢数
                    car.setCoachCount(chooseCarInfo.getCoachCount());
                    // Synthe ticalServiceFeePerDay double 基本保险费/日
                    car.setInsurancefee(chooseCarInfo.getSyntheticalServiceFeePerDay() + "");
                    // OverTimeFeePerHour double 超时费/小时
                    car.setExtimefee(chooseCarInfo.getOverTimeFeePerHour() + "");

                    // ServiceFee double 服务费
                    car.setServicefee(chooseCarInfo.getServiceFee() + "");
                    // PreAuthFee double 预授权
                    car.setPreauthfee(chooseCarInfo.getPreAuthFee() + "");
                    // Description string 描述
                    // ImageUrl string 车型图片
                    car.setImgurl(chooseCarInfo.getImageUrl());
                    // Gear string 排挡
                    if (chooseCarInfo.getGear().equals("AT")) {
                        car.setGear("自动");
                    }
                    else if (chooseCarInfo.getGear().equals("MT")) {
                        car.setGear("手动");
                    }
                    else if (chooseCarInfo.getGear().equals("CVT")) {
                        car.setGear("无级变速");
                    }
                    else {
                        car.setGear("手动");
                    }
                    // LaterPayFeePerDay double 预约价
                    car.setLaterPayFeePerDay(chooseCarInfo.getLaterPayFeePerDay());
                    // NowPayFeePerDay double 预定价
                    car.setNowPayFeePerDay(chooseCarInfo.getNowPayFeePerDay());
                    // SchemeID Int 特价车方案ID
                    // SpecialPriceFeePerDay double 特价价格/天
                    // SpecialPriceDays int 享受特价方案天数
                    carss.add(car);
                }
            }
            else {
                return carss;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return carss;
    }

    /**
     * 自驾 得到特价车列表
     * 
     * @param cityid
     * @return
     */
    public List<Cars> getSpecialCarList(long cityid) {
        List<Cars> carss = new ArrayList<Cars>();
        try {
            Supermacy supermacy = new Supermacy();
            SpecialPriceCarServiceStub.ValidationSoapHeader param = new SpecialPriceCarServiceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            SpecialPriceCarServiceStub.ValidationSoapHeaderE validationSoapHeader = new SpecialPriceCarServiceStub.ValidationSoapHeaderE();
            validationSoapHeader.setValidationSoapHeader(param);

            SpecialPriceCarServiceStub.GetSpecialPriceList getSpecialPriceList = new SpecialPriceCarServiceStub.GetSpecialPriceList();
            getSpecialPriceList.setCityName(getCityName(cityid));// 提车城市名称
            getSpecialPriceList.setCarModelID(0);// 车型ID(可为0)
            Calendar activityTime = Calendar.getInstance();
            activityTime.setTime(new Date());
            getSpecialPriceList.setBeginTime(activityTime);// 活动开始时间
            getSpecialPriceList.setEndTime(activityTime);// 活动结束时间
            getSpecialPriceList.setSortExpression("");// 排序(可为空)
            getSpecialPriceList.setPageSize(8);// PageSize int 每页显示的条数
            getSpecialPriceList.setPageNum(1);// PageNum int 当前页码
            getSpecialPriceList.setTotalRecord(0);// TotalRecord int 总记录条数

            SpecialPriceCarServiceStub stub = new SpecialPriceCarServiceStub();
            SpecialPriceCarServiceStub.GetSpecialPriceListResponse response = stub.getSpecialPriceList(
                    getSpecialPriceList, validationSoapHeader);
            SpecialPrice[] specialPrices = response.getGetSpecialPriceListResult().getSpecialPrice();
            if (specialPrices != null && specialPrices.length > 0) {
                for (int i = 0; i < specialPrices.length; i++) {
                    SpecialPrice specialPrice = specialPrices[i];
                    Cars car = new Cars();
                    // CityID int 城市ID
                    car.setCityid((long) specialPrice.getCityID());
                    // ModelName string 车型名称
                    car.setName(specialPrice.getModelName());
                    // Seating string 座位数
                    car.setMaxpassenger(specialPrice.getSeatCount() + "");
                    // LimitMileage double 限制里程
                    car.setMile(specialPrice.getLimitMileage() + "");
                    // Synthe ticalServiceFeePerDay double 基本保险费/
                    car.setInsurancefee(specialPrice.getSyntheticalServiceFeePerDay() + "");
                    // OverTimeFeePerHour double 超时费/小时
                    car.setExtimefee(specialPrice.getOverTimeFeePerHour() + "");
                    // PreAuthFee double 预授权
                    car.setPreauthfee(specialPrice.getPreAuthFee() + "");
                    // Description string 描述
                    car.setDescription(specialPrice.getDescription());
                    // ImageUrl string 车型图片
                    car.setImgurl(specialPrice.getImageUrl());
                    // CoachCount string 厢数
                    car.setCoachCount(specialPrice.getCoachCount());
                    // StoreID int 门店ID
                    car.setCarstoreid((long) specialPrice.getFetchStoreID());
                    // 活动开始时间
                    // 活动结束时间
                    // 活动天数
                    // 执行日租金
                    // 原价
                    // 油量
                    // 排量
                    // 超里程单价

                    // // ModelID int 车型 ID
                    // car.setCode(specialPrice.getModelID()+"");
                    // ServiceFee double 服务费
                    // car.setServicefee(chooseCarInfo.getServiceFee() + "");

                    // Gear string 排挡
                    // if (chooseCarInfo.getGear().equals("AT")) {
                    // car.setGear("自动");
                    // } else if (chooseCarInfo.getGear().equals("MT")) {
                    // car.setGear("手动");
                    // } else if (chooseCarInfo.getGear().equals("CVT")) {
                    // car.setGear("无级变速");
                    // } else {
                    // car.setGear("手动");
                    // }
                    // LaterPayFeePerDay double 预约价
                    // car.setLaterPayFeePerDay(chooseCarInfo
                    // .getLaterPayFeePerDay());
                    // NowPayFeePerDay double 预定价
                    car.setNowPayFeePerDay(specialPrice.getDayHire());
                    // SchemeID Int 特价车方案ID
                    // SpecialPriceFeePerDay double 特价价格/天
                    // SpecialPriceDays int 享受特价方案天数
                    carss.add(car);
                }
            }
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return carss;
    }

    /**
     * 自驾 得到特价车列表
     * 
     * @param cityid
     * @return
     */
    public String[] getSpecialCarList1(long cityid) {
        try {
            Supermacy supermacy = new Supermacy();
            SpecialPriceCarServiceStub.ValidationSoapHeader param = new SpecialPriceCarServiceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            SpecialPriceCarServiceStub.ValidationSoapHeaderE validationSoapHeader = new SpecialPriceCarServiceStub.ValidationSoapHeaderE();
            validationSoapHeader.setValidationSoapHeader(param);

            SpecialPriceCarServiceStub.GetSpecialPriceList getSpecialPriceList = new SpecialPriceCarServiceStub.GetSpecialPriceList();
            if (cityid > 0) {
                getSpecialPriceList.setCityName(getCityName(cityid));// 提车城市名称
            }
            else {
                getSpecialPriceList.setCityName("全国"); // 全国特价车
            }
            getSpecialPriceList.setCarModelID(0);// 车型ID(可为0)
            Calendar activityTime = Calendar.getInstance();
            activityTime.setTime(new Date());
            getSpecialPriceList.setBeginTime(activityTime);// 活动开始时间
            getSpecialPriceList.setEndTime(activityTime);// 活动结束时间
            getSpecialPriceList.setSortExpression("");// 排序(可为空)
            getSpecialPriceList.setPageSize(10);// PageSize int 每页显示的条数
            getSpecialPriceList.setPageNum(1);// PageNum int 当前页码
            getSpecialPriceList.setTotalRecord(0);// TotalRecord int 总记录条数

            SpecialPriceCarServiceStub stub = new SpecialPriceCarServiceStub();
            SpecialPriceCarServiceStub.GetSpecialPriceListResponse response = stub.getSpecialPriceList(
                    getSpecialPriceList, validationSoapHeader);
            if (response != null && response.getGetSpecialPriceListResult() != null) {
                SpecialPrice[] specialPrices = response.getGetSpecialPriceListResult().getSpecialPrice();
                if (specialPrices != null && specialPrices.length > 0) {
                    String[] str = new String[specialPrices.length];
                    for (int i = 0; i < specialPrices.length; i++) {
                        String strSpecialPrice = "{";
                        strSpecialPrice += "\"SchemeID\":\"" + specialPrices[i].getSchemeID() + "\",";
                        strSpecialPrice += "\"CityID\":\"" + specialPrices[i].getCityID() + "\",";
                        strSpecialPrice += "\"ModelName\":\"" + specialPrices[i].getModelName() + "\",";
                        strSpecialPrice += "\"SeatCount\":\"" + specialPrices[i].getSeatCount() + "\",";
                        strSpecialPrice += "\"LimitMileage\":\"" + specialPrices[i].getLimitMileage() + "\",";
                        strSpecialPrice += "\"SyntheticalServiceFeePerDay\":\""
                                + specialPrices[i].getSyntheticalServiceFeePerDay() + "\",";
                        strSpecialPrice += "\"OverTimeFeePerHour\":\"" + specialPrices[i].getOverTimeFeePerHour()
                                + "\",";
                        strSpecialPrice += "\"ExceedDistanceCharge\":\"" + specialPrices[i].getExceedDistanceCharge()
                                + "\",";
                        strSpecialPrice += "\"PreAuthFee\":\"" + specialPrices[i].getPreAuthFee() + "\",";
                        strSpecialPrice += "\"Description\":\"" + specialPrices[i].getDescription() + "\",";
                        strSpecialPrice += "\"ImageUrl\":\"" + specialPrices[i].getImageUrl() + "\",";
                        strSpecialPrice += "\"DeliveryCapacity\":\"" + specialPrices[i].getDeliveryCapacity() + "\",";
                        strSpecialPrice += "\"OilVolume\":\"" + specialPrices[i].getOilVolume() + "\",";
                        strSpecialPrice += "\"CoachCount\":\"" + specialPrices[i].getCoachCount() + "\",";
                        strSpecialPrice += "\"FetchCityName\":\"" + specialPrices[i].getFetchCityName() + "\",";
                        strSpecialPrice += "\"FetchStoreID\":\"" + specialPrices[i].getFetchStoreID() + "\",";
                        strSpecialPrice += "\"FetchStoreName\":\"" + specialPrices[i].getFetchStoreName() + "\",";
                        // /////////////////////////////////////////////////////////////////////
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Calendar date1 = specialPrices[i].getBeginTime();
                        Calendar date2 = specialPrices[i].getEndTime();
                        Date d1 = date1.getTime();
                        Date d2 = date2.getTime();
                        String str1 = sdf.format(d1);
                        String str2 = sdf.format(d2);
                        strSpecialPrice += "\"BeginTime\":\"" + str1 + "\",";
                        strSpecialPrice += "\"EndTime\":\"" + str2 + "\",";
                        // /////////////////////////////////////////////////////////////////////
                        strSpecialPrice += "\"Days\":\"" + specialPrices[i].getDays() + "\",";
                        strSpecialPrice += "\"DayHire\":\"" + specialPrices[i].getDayHire() + "\",";
                        strSpecialPrice += "\"GeneralDayHire\":\"" + specialPrices[i].getGeneralDayHire() + "\"";
                        strSpecialPrice += "}";
                        str[i] = strSpecialPrice;
                        strSpecialPrice = "";
                    }
                    return str;
                }
            }
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

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
            String remark, int reservationType) {
        try {
            Supermacy supermacy = new Supermacy();
            SpecialPriceCarServiceStub.ValidationSoapHeader param = new SpecialPriceCarServiceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            SpecialPriceCarServiceStub.ValidationSoapHeaderE validationSoapHeader = new SpecialPriceCarServiceStub.ValidationSoapHeaderE();
            validationSoapHeader.setValidationSoapHeader(param);

            SpecialPriceCarServiceStub.SaveOrder saveorder = new SpecialPriceCarServiceStub.SaveOrder();
            saveorder.setStrSchemeID(strSchemeID);
            saveorder.setReturnStoreID(returnStoreID);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar rentDate = Calendar.getInstance();
            Calendar returnDate = Calendar.getInstance();
            Date d1 = sdf.parse(rentTime);
            rentDate.setTime(d1);
            Date d2 = sdf.parse(returnTime);
            returnDate.setTime(d2);
            saveorder.setRentTime(rentDate);
            saveorder.setReturnTime(returnDate);
            saveorder.setIsNeedGPS(isNeedGPS);
            saveorder.setCustomerName(customerName);
            saveorder.setMobilePhoneNo(mobilePhoneNo);
            saveorder.setCustPaperID(custPaperID);
            saveorder.setPaperNo(paperNo);
            saveorder.setGender(gender);
            saveorder.setIsHasSixMonthDrivingLicense(isHasSixMonthDrivingLicense);
            saveorder.setLinkmanName(linkmanName);
            saveorder.setLinkmanmobilePhoneNo(linkmanmobilePhoneNo);
            saveorder.setRemark(remark);
            saveorder.setReservationType(reservationType);

            SpecialPriceCarServiceStub stub = new SpecialPriceCarServiceStub();
            SpecialPriceCarServiceStub.SaveOrderResponse response = stub.saveOrder(saveorder, validationSoapHeader);

            SpecialPriceCarServiceStub.SaveResult saveResult = response.getSaveOrderResult();
            if (saveResult != null && !saveResult.equals("")) {
                if (saveResult.getErrrorMsg() != null && !saveResult.getErrrorMsg().equals("")) {
                    System.out.println("ErrrorMsg==" + saveResult.getErrrorMsg());
                }
                else {
                    String billID = String.valueOf(saveResult.getBillID());
                    return billID;
                }
            }
            else {
                System.out.println("ErrrorMsg==" + saveResult.getErrrorMsg());
            }
        }
        catch (Exception e) {
            System.out.println("Exception==" + e);
        }
        return null;
    }

    /**
     * 自驾 获取门店信息
     * 
     * @author 陈栋 2012-5-10 17:59:32
     * @param loginName
     *            username
     * @param LoginPwd
     *            password
     * @return 门店列表
     */
    public List<Carstore> GetChooseJourney() {
        List<Carstore> list = new ArrayList<Carstore>();
        try {
            Supermacy supermacy = new Supermacy();
            SelfDriveSerivceStub.ValidationSoapHeader param = new SelfDriveSerivceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            SelfDriveSerivceStub.ValidationSoapHeaderE validationSoapHeaderE = new SelfDriveSerivceStub.ValidationSoapHeaderE();
            validationSoapHeaderE.setValidationSoapHeader(param);

            SelfDriveSerivceStub.GetChooseJourney getChooseJourney = new SelfDriveSerivceStub.GetChooseJourney();

            SelfDriveSerivceStub stub = new SelfDriveSerivceStub();
            SelfDriveSerivceStub.GetChooseJourneyResponse response = stub.getChooseJourney(getChooseJourney,
                    validationSoapHeaderE);

            SelfDriveSerivceStub.JourneyData[] journeyDatas = response.getGetChooseJourneyResult().getJourneyData();
            if (journeyDatas != null) {
                for (JourneyData journeyData : journeyDatas) {
                    City city = new City();
                    city.setName(journeyData.getCity());
                    city.setCarcode(journeyData.getCityID() + "");
                    city.setType(2L);
                    Server.getInstance().getHotelService().createCity(city);
                    Store[] stores = journeyData.getListStore().getStore();
                    if (stores != null) {
                        for (Store store : stores) {
                            Carstore carstore = new Carstore();
                            carstore.setStorecode(store.getStoreID() + "");
                            carstore.setName(store.getStoreName());
                            carstore.setAddress(store.getAddress());
                            carstore.setFormtime(store.getOpenningTime().split("-")[0]);
                            carstore.setTotime(store.getOpenningTime().split("-")[1]);
                            carstore.setCityid(store.getCityID() + "");
                            // //所在省编号
                            // private String provincecode;
                            // //创建人
                            // private Long createuserid;
                            carstore.setTel(store.getTel());
                            // //所在区域
                            // private String district;
                            carstore.setAbbrname(store.getStoreName());
                            if (store.getTrafficGuide() != null) {
                                carstore.setComment(store.getTrafficGuide());
                            }
                            Server.getInstance().getCarService().createCarstore(carstore);
                            list.add(carstore);
                        }
                    }
                }
            }
            else {
                return list;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 自驾 得到至尊车型列表
     * 
     * @author 陈栋 2012-5-10 17:59:38
     * @param loginName
     *            userName
     * @param LoginPwd
     *            Password
     * @return 至尊车型列表
     */
    public List<Carinfo> GetCarModelList(String loginName, String LoginPwd) {
        List<Carinfo> carinfos = new ArrayList<Carinfo>();
        try {
            Supermacy supermacy = new Supermacy();
            SelfDriveSerivceStub stub = new SelfDriveSerivceStub();
            SelfDriveSerivceStub.GetCarModelList getCarModelList = new SelfDriveSerivceStub.GetCarModelList();
            SelfDriveSerivceStub.ValidationSoapHeaderE validationSoapHeaderE = new SelfDriveSerivceStub.ValidationSoapHeaderE();
            SelfDriveSerivceStub.ValidationSoapHeader param = new SelfDriveSerivceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            validationSoapHeaderE.setValidationSoapHeader(param);
            SelfDriveSerivceStub.GetCarModelListResponse response = stub.getCarModelList(getCarModelList,
                    validationSoapHeaderE);
            ArrayOfCarModelSimple arrayOfCarModelSimple = response.getGetCarModelListResult();
            CarModelSimple[] carModelSimples = arrayOfCarModelSimple.getCarModelSimple();

            for (CarModelSimple carModelSimple : carModelSimples) {
                Carinfo carinfo = new Carinfo();
                carinfo.setName(carModelSimple.getModelName());
                carinfo.setCode(carModelSimple.getModelID() + "");
                carinfo.setBrandcode(carModelSimple.getBrandID() + "");
                carinfo.setCarriage(carModelSimple.getCoachCount());
                carinfo.setDeliverycapacity(carModelSimple.getDeliveryCapacity() + "");
                carinfo.setOilvolume(carModelSimple.getOilVolume() + "");
                carinfo.setSeatingcount(carModelSimple.getSeating() + "");
                carinfo.setImageurl(carModelSimple.getImagePath());
                Server.getInstance().getCarService().createCarinfo(carinfo);

                Carimages carimages = new Carimages();
                carimages.setCarurl(carinfo.getImageurl());
                carimages.setCreatetime(new Timestamp(new Date().getTime()));
                carimages.setCreateuser("admin");
                carimages.setCarid(Long.parseLong(carinfo.getCode()));
                carimages.setName(carinfo.getName());
                Server.getInstance().getCarService().createCarimages(carimages);

                carinfos.add(carinfo);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return carinfos;
    }

    /**
     * 
     * 
     * 自驾 取消订单
     * @param orderID
     * @return
     */
    public String selfCancelOrder(int orderID) {
        try {
            Supermacy supermacy = new Supermacy();
            SelfDriveSerivceStub.ValidationSoapHeader param = new SelfDriveSerivceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            SelfDriveSerivceStub.ValidationSoapHeaderE validationSoapHeaderE = new SelfDriveSerivceStub.ValidationSoapHeaderE();
            validationSoapHeaderE.setValidationSoapHeader(param);

            SelfDriveSerivceStub.CancelOrder cancelOrder = new SelfDriveSerivceStub.CancelOrder();
            cancelOrder.setOrderID(orderID);

            SelfDriveSerivceStub stub = new SelfDriveSerivceStub();
            SelfDriveSerivceStub.CancelOrderResponse response = stub.cancelOrder(cancelOrder, validationSoapHeaderE);

            String result = response.getCancelOrderResult();
            if (result != null && result.length() > 0 && !result.equals("")) {
                return result;
            }
            else {
                return "";
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /* ================================================================================================ */

    /**
     * 代驾 得到产品分类列表 代驾现阶段分成三种产品类型，此方法是得到三种产品分类，开发商把三种产品展示到网站 让客户选择
     * 
     * @param loginName
     * @param LoginPwd
     */
    public void getProductClass(String loginName, String LoginPwd) {
        try {
            Supermacy supermacy = new Supermacy();
            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();
            TopOneChauffeurDriverServiceStub.GetProductClass getProductClass = new TopOneChauffeurDriverServiceStub.GetProductClass();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader7 = new TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeader param = new TopOneChauffeurDriverServiceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            validationSoapHeader7.setValidationSoapHeader(param);
            TopOneChauffeurDriverServiceStub.GetProductClassResponse res = stub.getProductClass(getProductClass,
                    validationSoapHeader7);
            TopOneChauffeurDriverServiceStub.ThirdProductClass[] ThirdProductClasss = res.getGetProductClassResult()
                    .getThirdProductClass();
            for (int i = 0; i < ThirdProductClasss.length; i++) {
                TopOneChauffeurDriverServiceStub.ThirdProductClass product = ThirdProductClasss[i];
                System.out.println(product.getClassID());
                System.out.println(product.getClassName());
                // 1
                // 机场接送
                // 2
                // 市内接送,城际接送
                // 3
                // 日租
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 代驾 得到提供产品所提供的上下车城市与区域
     * 
     * @param loginName
     * @param LoginPwd
     */
    public void getCityAreaList(String loginName, String LoginPwd) {
        try {
            Supermacy supermacy = new Supermacy();
            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();
            TopOneChauffeurDriverServiceStub.GetCityAreaList getCityAreaList = new TopOneChauffeurDriverServiceStub.GetCityAreaList();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader2 = new TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeader param = new TopOneChauffeurDriverServiceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            validationSoapHeader2.setValidationSoapHeader(param);
            TopOneChauffeurDriverServiceStub.GetCityAreaListResponse resp = stub.getCityAreaList(getCityAreaList,
                    validationSoapHeader2);
            // TopOneChauffeurDriverServiceStub.GetChooseCarLevelList
            TopOneChauffeurDriverServiceStub.ProductInfo[] ProductInfos = resp.getGetCityAreaListResult()
                    .getProductInfo();

            TopOneChauffeurDriverServiceStub.ProductInfo productInfo = ProductInfos[1];

            System.out.println(productInfo.getProductID());
            // System.out.println(productInfo.getProductPropertyID());
            System.out.println(productInfo.getProductName());
            // 对应的城市区域组
            TopOneChauffeurDriverServiceStub.GroupCity[] groupCitys = productInfo.getLiGroupCity().getGroupCity();

            for (int j = 0; j < groupCitys.length; j++) {
                TopOneChauffeurDriverServiceStub.GroupCity groupCity = groupCitys[j];

                TopOneChauffeurDriverServiceStub.GetOnCityArea[] getOnCityAreas = groupCity.getLiGetOnCityArea()
                        .getGetOnCityArea();

                for (int k = 0; k < getOnCityAreas.length; k++) {
                    // city.setAreacode(areacode);
                    TopOneChauffeurDriverServiceStub.GetOnCityArea getOnCityArea = getOnCityAreas[k];
                    // getOnCityArea.getCityCode();

                    City city = new City();
                    String cityid = String.valueOf(getOnCityArea.getCityID());

                    String cityName = getOnCityArea.getCityName();

                    city.setName(cityName);

                    city.setType(2l);

                    city.setCarcode(cityid);
                    System.out.println("城市名字：：" + cityName);
                    System.out.println();

                    // city.setUcode(1l);

                    // city = Server.getInstance().getHotelService().createCity(
                    // city);
                    Area[] onCityArea = getOnCityArea.getLiArea().getArea();

                    for (int l = 0; l < onCityArea.length; l++) {
                        Carstore cs = new Carstore();
                        getOnCityArea.getLiGetOffCityArea().getGetOffCityArea();
                        String onCityAreaId = String.valueOf(onCityArea[l].getAreaID());
                        cs.setStorecode(onCityAreaId);
                        String onCityAreaName = onCityArea[l].getAreaName();
                        System.out.println("上车城市名字：" + onCityAreaName);
                        cs.setName(onCityAreaName);
                        cs.setDistrict("on");
                        cs.setCityid(cityid);

                        System.out.println("城市所属市：" + cityName);
                        // System.out.println();
                        Server.getInstance().getCarService().createCarstore(cs);

                    }
                    GetOffCityArea[] offCity = getOnCityArea.getLiGetOffCityArea().getGetOffCityArea();
                    for (int i = 0; i < offCity.length; i++) {
                        Area[] offCityArea = offCity[i].getLiArea().getArea();

                        System.out.println(offCityArea.length);

                        for (int m = 0; m < offCityArea.length; m++) {
                            Carstore css = new Carstore();
                            String offCityId = String.valueOf(offCityArea[m].getAreaID());
                            css.setStorecode(offCityId);
                            String offCityName = offCityArea[m].getAreaName();
                            css.setName(offCityName);
                            css.setDistrict("off");

                            String citycityid = String.valueOf(offCity[i].getCityID());// 下车城市所属市
                            css.setCityid(citycityid);// 上车城所属市
                            css.setProvincecode(cityid);

                            System.out.println("上车所属城市名字：" + city);
                            System.out.println("下车所属城市名字：" + offCity[i].getCityName());

                            // System.out.println("城市所属市：" +
                            // offCity[i].getCityName());
                            System.out.println();
                            Server.getInstance().getCarService().createCarstore(css);
                        }
                    }
                    getOnCityArea.getLiGetOffCityArea().getGetOffCityArea();
                    getOnCityArea.getPinyin();
                }
            }

        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 代驾 得到可供服务的车组
     * 
     * @param productClassID
     * @param preGetOnTime
     * @param preGetOffTime
     * @param getOnAreaID
     * @param getoffAreaID
     * @param personCount
     * @param DayDetails
     * @param GoOnAddress
     * @param GoOffAddress
     * @param GoOnAddressXCoordinate
     * @param GoOnAddressYCoordinate
     * @param GoOffAddressXCoordinate
     * @param GoOffAddressYCoordinate
     * @param MapType
     * @return 车型列表
     */
    public List<Cars> GetChooseCarLevelList(int productClassID, String preGetOnTime, String preGetOffTime,
            int getOnAreaID, int getoffAreaID, int personCount, String getOnTime, String getOffTime, double useMileage,
            boolean hasDriverBide, String GoOnAddress, String GoOffAddress, double GoOnAddressXCoordinate,
            double GoOnAddressYCoordinate, double GoOffAddressXCoordinate, double GoOffAddressYCoordinate, int MapType) {
        List<Cars> carList = new ArrayList<Cars>();
        try {
            Supermacy supermacy = new Supermacy();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeader param = new TopOneChauffeurDriverServiceStub.ValidationSoapHeader();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader4 = new TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE();
            validationSoapHeader4.setValidationSoapHeader(param);

            TopOneChauffeurDriverServiceStub.GetChooseCarLevelList getChooseCarLevelList = new TopOneChauffeurDriverServiceStub.GetChooseCarLevelList();
            Calendar ontime = Calendar.getInstance();
            Calendar offtime = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date rentontime = sdf.parse(getOnTime + ":00");
            Date rentofftime = sdf.parse(getOnTime + ":00");
            rentofftime.setHours(rentofftime.getHours() + 4);
            Date rentontime2 = sdf.parse(preGetOnTime + ":00");
            Date rentofftime2 = sdf.parse(preGetOffTime + ":00");
            ontime.setTime(rentontime);
            offtime.setTime(rentofftime);
            getChooseCarLevelList.setProductClassID(productClassID);
            getChooseCarLevelList.setPreGetOnTime(ontime);
            getChooseCarLevelList.setPreGetOffTime(offtime);
            getChooseCarLevelList.setPersonCount(personCount);
            ArrayOfChauffeurDrivePerDayDetails daydatail = new ArrayOfChauffeurDrivePerDayDetails();
            TopOneChauffeurDriverServiceStub.ChauffeurDrivePerDayDetails[] chs = new ChauffeurDrivePerDayDetails[1];
            TopOneChauffeurDriverServiceStub.ChauffeurDrivePerDayDetails ch = new ChauffeurDrivePerDayDetails();
            ch.setGetOffTime(ontime);
            ch.setGetOnTime(offtime);
            ch.setHasDriverBide(hasDriverBide);
            ch.setUseMileage(useMileage);
            chs[0] = ch;
            daydatail.setChauffeurDrivePerDayDetails(chs);
            getChooseCarLevelList.setDayDetails(daydatail);
            getChooseCarLevelList.setGoOnAddress(GoOnAddress);
            getChooseCarLevelList.setGoOffAddress(GoOffAddress);
            getChooseCarLevelList.setGoOnAddressXCoordinate(GoOnAddressXCoordinate);
            getChooseCarLevelList.setGoOffAddressXCoordinate(GoOffAddressXCoordinate);
            getChooseCarLevelList.setGoOnAddressYCoordinate(GoOnAddressYCoordinate);
            getChooseCarLevelList.setGoOffAddressYCoordinate(GoOffAddressYCoordinate);
            getChooseCarLevelList.setMapType(MapType);
            getChooseCarLevelList.setGetOnAreaID(getOnAreaID);
            getChooseCarLevelList.setGetoffAreaID(getoffAreaID);

            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();
            GetChooseCarLevelListResponse resp = stub.getChooseCarLevelList(getChooseCarLevelList,
                    validationSoapHeader4);

            ThirdCarGroupPrice[] carGroupPrice = resp.getGetChooseCarLevelListResult().getThirdCarGroupPrice();
            if (carGroupPrice != null) {
                for (int i = 0; i < carGroupPrice.length; i++) {
                    ThirdCarGroupPrice thirdCarGroupPrice = carGroupPrice[i];
                    Cars ca = new Cars();
                    // 车型编号
                    ca.setCode(String.valueOf(thirdCarGroupPrice.getCarGroupID()));
                    // 车型描述
                    ca.setDescription(thirdCarGroupPrice.getCarGroupDescription());
                    // 超里程费
                    ca.setExmilefee(String.valueOf(thirdCarGroupPrice.getExceedMileagePerCharge()));
                    // 超时间费
                    ca.setExtimefee(String.valueOf(thirdCarGroupPrice.getOvertimePerHourCharge()));
                    // 图片路径
                    ca.setImgurl(thirdCarGroupPrice.getImageUrl());
                    // 最大限乘数
                    ca.setMaxpassenger(String.valueOf(thirdCarGroupPrice.getMaxSeatCount()));
                    // 车型名称
                    ca.setName(thirdCarGroupPrice.getCarGroupName());
                    // 每天可用里程
                    ca.setMile(String.valueOf(thirdCarGroupPrice.getLimitMileagePerDay()));
                    // 是否包含路桥费
                    ca.setRoadbridge(String.valueOf(thirdCarGroupPrice.getIncludeRoadBridgeCharge()));
                    // 基本总费用
                    ca.setNowPayFeePerDay(thirdCarGroupPrice.getTotalCharge());
                    carList.add(ca);
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return carList;
    }

    /**
     * 得到分销商优惠券
     * 
     * @param productClassID
     * @param GoOnCityID
     * @param preGetOnTime
     * @param preGetOffTime
     */
    public void GetActivityCouponListByDistributor(int productClassID, int GoOnCityID, Calendar preGetOnTime,
            Calendar preGetOffTime) {

        try {
            Supermacy supermacy = new Supermacy();
            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();

            TopOneChauffeurDriverServiceStub.GetActivityCouponListByDistributor getActivityCouponListByDistributor = new GetActivityCouponListByDistributor();

            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader1 = new ValidationSoapHeaderE();

            TopOneChauffeurDriverServiceStub.ValidationSoapHeader param = new ValidationSoapHeader();

            param.setLoginName(supermacy.getLoginName());

            param.setLoginPassword(supermacy.getLoginPwd());

            validationSoapHeader1.setValidationSoapHeader(param);

            getActivityCouponListByDistributor.setGoOnCityID(GoOnCityID);

            getActivityCouponListByDistributor.setProductClassID(productClassID);

            getActivityCouponListByDistributor.setPreGetOnTime(preGetOnTime);

            getActivityCouponListByDistributor.setPreGetOffTime(preGetOffTime);

            GetActivityCouponListByDistributorResponse resp = stub.getActivityCouponListByDistributor(
                    getActivityCouponListByDistributor, validationSoapHeader1);

            ActivityCoupon[] activeCoupon = resp.getGetActivityCouponListByDistributorResult().getActivityCoupon();
            for (int i = 0; i < activeCoupon.length; i++) {
                System.out.println("优惠券ID：" + activeCoupon[i].getCouponID());
                System.out.println("优惠券名称：" + activeCoupon[i].getCouponName());
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 得到费用明细
     * 
     * @param productClassID
     * @param preGetOnTime
     * @param preGetOffTime
     * @param getOnAreaID
     * @param getoffAreaID
     * @param personCount
     * @param carGroupID
     * @param DayDetails
     * @param GoOnAddress
     * @param GoOffAddress
     * @param GoOnAddressXCoordinate
     * @param GoOnAddressYCoordinate
     * @param GoOffAddressXCoordinate
     * @param GoOffAddressYCoordinate
     * @param MapType
     * @param CouponID
     */

    public ChauffeurDriveCostDetail GetChauffeurDriveCostDetail(int productClassID, String preGetOnTime,
            String preGetOffTime, int getOnAreaID, int getoffAreaID, int personCount, int carGroupID, String getOnTime,
            String getOffTime, boolean hasDriverBide, String GoOnAddress, String GoOffAddress,
            double GoOnAddressXCoordinate, double GoOnAddressYCoordinate, double GoOffAddressXCoordinate,
            double GoOffAddressYCoordinate, int MapType, int CouponID) {

        try {
            Supermacy supermacy = new Supermacy();
            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();
            TopOneChauffeurDriverServiceStub.GetChauffeurDriveCostDetail getChauffeurDriveCostDetail = new GetChauffeurDriveCostDetail();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader = new ValidationSoapHeaderE();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeader parm = new ValidationSoapHeader();

            validationSoapHeader.setValidationSoapHeader(parm);
            parm.setLoginName(supermacy.getLoginName());
            parm.setLoginPassword(supermacy.getLoginPwd());

            Calendar ontime = Calendar.getInstance();

            Calendar offtime = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            /*
             * Calendar getOnTime1 = Calendar.getInstance();
             * 
             * Calendar getOffTime1 = Calendar.getInstance();
             * 
             * sdf.parse(getOnTime);
             * 
             * sdf.parse(getOffTime)
             */

            Date preGetOnTime1 = sdf.parse(preGetOnTime);

            Date preGetOffTime1 = sdf.parse(preGetOffTime);

            ontime.setTime(preGetOnTime1);

            offtime.setTime(preGetOffTime1);

            getChauffeurDriveCostDetail.setProductClassID(productClassID);

            getChauffeurDriveCostDetail.setPreGetOnTime(ontime);

            getChauffeurDriveCostDetail.setPreGetOffTime(offtime);

            getChauffeurDriveCostDetail.setGetOnAreaID(getOnAreaID);

            getChauffeurDriveCostDetail.setGetoffAreaID(getoffAreaID);

            getChauffeurDriveCostDetail.setPersonCount(personCount);

            getChauffeurDriveCostDetail.setCarGroupID(carGroupID);

            getChauffeurDriveCostDetail.setGoOnAddress(GoOnAddress);

            getChauffeurDriveCostDetail.setGoOffAddress(GoOffAddress);

            getChauffeurDriveCostDetail.setGoOffAddressXCoordinate(GoOnAddressXCoordinate);

            getChauffeurDriveCostDetail.setGoOffAddressYCoordinate(GoOnAddressYCoordinate);

            getChauffeurDriveCostDetail.setGoOffAddressXCoordinate(GoOffAddressXCoordinate);

            getChauffeurDriveCostDetail.setGoOffAddressYCoordinate(GoOffAddressYCoordinate);

            TopOneChauffeurDriverServiceStub.ArrayOfChauffeurDrivePerDayDetails adp = new ArrayOfChauffeurDrivePerDayDetails();

            ChauffeurDrivePerDayDetails dt = new ChauffeurDrivePerDayDetails();

            List<ChauffeurDrivePerDayDetails> dtlist = new ArrayList<ChauffeurDrivePerDayDetails>();

            dtlist.add(dt);

            ChauffeurDrivePerDayDetails[] tt = null;

            tt = new ChauffeurDrivePerDayDetails[] { new ChauffeurDrivePerDayDetails() };

            // System.out.println(tt.length);

            tt[0].setGetOffTime(offtime);

            tt[0].setGetOnTime(ontime);

            tt[0].setHasDriverBide(hasDriverBide);

            adp.setChauffeurDrivePerDayDetails(tt);

            getChauffeurDriveCostDetail.setDayDetails(adp);

            GetChauffeurDriveCostDetailResponse resp = stub.getChauffeurDriveCostDetail(getChauffeurDriveCostDetail,
                    validationSoapHeader);

            ChauffeurDriveCostDetail detailResault = resp.getGetChauffeurDriveCostDetailResult();

            ArrayOfDayChargeDetails resault = detailResault.getDayChargeDetail();

            // System.out.println(resault);

            ChauffeurDriveCostDetail cct = new ChauffeurDriveCostDetail();

            // 优惠券抵扣费用
            detailResault.getACounponChagre();

            System.out.println("优惠券费用：" + detailResault.getACounponChagre());

            cct.setACounponChagre(detailResault.getACounponChagre());

            // 得到总费用
            detailResault.getTotalCharge();
            System.out.println("总费用：" + detailResault.getTotalCharge());

            cct.setTotalCharge(detailResault.getTotalCharge());
            // 其他费用
            detailResault.getOtherCharge();
            System.out.println("其他费用：" + detailResault.getOtherCharge());

            cct.setOtherCharge(detailResault.getOtherCharge());
            // 折扣费用
            detailResault.getDiscountCash();
            System.out.println("折扣费用：" + detailResault.getDiscountCash());

            cct.setDiscountCash(detailResault.getDiscountCash());
            // 实际费用
            detailResault.getRealAmount();

            System.out.println("实际费用：" + detailResault.getRealAmount());

            cct.setRealAmount(detailResault.getRealAmount());
            // 还需支付
            detailResault.getNeedPayCharge();

            System.out.println("还需支付：" + detailResault.getNeedPayCharge());

            cct.setNeedPayCharge(detailResault.getNeedPayCharge());
            // 是否有错
            detailResault.getIsError();
            // 错误消息
            detailResault.getErrrorMsg();
            ArrayOfDayChargeDetails param = new ArrayOfDayChargeDetails();

            if (resault != null) {

                DayChargeDetails[] dayChargeDetail = resault.getDayChargeDetails();

                param.setDayChargeDetails(dayChargeDetail);

                cct.setDayChargeDetail(param);

                for (int i = 0; i < dayChargeDetail.length; i++) {

                    // 日期
                    dayChargeDetail[i].getDate();

                    // 上车时间
                    dayChargeDetail[i].getGetOnTime();
                    // 下车时间
                    dayChargeDetail[i].getGetOffTime();
                    // 使用时间
                    dayChargeDetail[i].getUseHour();
                    System.out.println("使用时间：" + dayChargeDetail[i].getUseHour());
                    // 使用里程
                    dayChargeDetail[i].getUseMileage();
                    System.out.println("使用里程：" + dayChargeDetail[i].getUseMileage());
                    // 燃油附加费
                    dayChargeDetail[i].getOilAllowanceCharge();
                    System.out.println("燃油附加费：" + dayChargeDetail[i].getOilAllowanceCharge());
                    // 油费
                    dayChargeDetail[i].getOilCharge();
                    System.out.println("油费：" + dayChargeDetail[i].getOilCharge());
                    // 司机服务费
                    dayChargeDetail[i].getDriverCharge();
                    System.out.println("司机服务费：" + dayChargeDetail[i].getDriverCharge());
                    // 路桥费
                    dayChargeDetail[i].getRoadBridgeCharge();
                    System.out.println("路桥费：" + dayChargeDetail[i].getRoadBridgeCharge());
                    // 超时费(当超出基准计费时间时会收取超时费)
                    dayChargeDetail[i].getRealOvertimeCharge();
                    System.out.println("超时费：" + dayChargeDetail[i].getRealOvertimeCharge());
                    // 超里程费(当超出基准计费里程时会收取超里程费)
                    dayChargeDetail[i].getExceedMileageCharge();

                    System.out.println("实际租金费用：" + dayChargeDetail[i].getRealRentCharge());

                    System.out.println("车租金：" + dayChargeDetail[i].getVoitureRentCharge());

                    System.out.println("加班费：" + dayChargeDetail[i].getNightServiceCharge());

                    System.out.println("超里程费:" + dayChargeDetail[i].getExceedMileageCharge());

                    System.out.println("每日费用：" + dayChargeDetail[i].getDayCharge());

                    System.out.println("司机住宿问题：" + dayChargeDetail[i].getDriverBideCharge());
                }
                return cct;
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 订单保存
     * 
     * @param custname
     * @param gender
     * @param mobilePhone
     * @param passengerName
     * @param passengerGender
     * @param passengerMobilePhone
     * @param personCount
     * @param preGetOnTime
     * @param preGetOffTime
     * @param getOnAreaID
     * @param GoOnAddress
     * @param getoffAreaID
     * @param GoOffAddress
     * @param isNeedInvoice
     * @param invoiceHead
     * @param invoiceMailAddress
     * @param travelDescription
     * @param carGroupID
     * @param productClassID
     * @param airportTypeID
     * @param flightCode
     * @param isNeedPickBrand
     * @param pickBrandContent
     * @param LuggageCount
     * @param PreDayDetails
     * @param operatorSN
     * @param GoOnAddressXCoordinate
     * @param GoOnAddressYCoordinate
     * @param GoOffAddressXCoordinate
     * @param GoOffAddressYCoordinate
     * @param MapType
     * @param CouponID
     * @param memo
     * @return SaveResult
     */
    public String GetChauffeurDriveOrderSave(String custname, boolean gender, String mobilePhone, String passengerName,
            boolean passengerGender, String passengerMobilePhone, int personCount, String preGetOnTime,
            String preGetOffTime, int getOnAreaID, String GoOnAddress, int getoffAreaID, String GoOffAddress,
            boolean isNeedInvoice, String invoiceHead, String invoiceMailAddress, String travelDescription,
            int carGroupID, int productClassID, int airportTypeID, String flightCode, boolean isNeedPickBrand,
            String pickBrandContent, int LuggageCount, String getOnTime, String getOffTime, boolean hasDriverBide,
            String operatorSN, double GoOnAddressXCoordinate, double GoOnAddressYCoordinate,
            double GoOffAddressXCoordinate, double GoOffAddressYCoordinate, int MapType, int CouponID, String memo) {

        try {
            Supermacy supermacy = new Supermacy();

            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();

            TopOneChauffeurDriverServiceStub.GetChauffeurDriveOrderSave getChauffeurDriveOrderSave = new GetChauffeurDriveOrderSave();

            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader6 = new ValidationSoapHeaderE();

            TopOneChauffeurDriverServiceStub.ValidationSoapHeader parm = new ValidationSoapHeader();
            parm.setLoginName(supermacy.getLoginName());
            parm.setLoginPassword(supermacy.getLoginPwd());

            ArrayOfDayDetails ddt = new ArrayOfDayDetails();

            Calendar ontime = Calendar.getInstance();

            Calendar offtime = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date getontime = sdf.parse(preGetOnTime);

            Date getofftime = sdf.parse(preGetOffTime);

            ontime.setTime(getontime);

            offtime.setTime(getofftime);

            DayDetails[] lt = new DayDetails[] { new DayDetails() };

            lt[0].setGetOnTime(ontime);

            lt[0].setGetOffTime(offtime);

            lt[0].setHasDriverBide(hasDriverBide);

            ddt.setDayDetails(lt);

            getChauffeurDriveOrderSave.setPreDayDetails(ddt);
            validationSoapHeader6.setValidationSoapHeader(parm);
            getChauffeurDriveOrderSave.setCustname(custname);
            getChauffeurDriveOrderSave.setGender(gender);
            getChauffeurDriveOrderSave.setMobilePhone(passengerMobilePhone);
            getChauffeurDriveOrderSave.setPersonCount(personCount);
            getChauffeurDriveOrderSave.setPreGetOnTime(ontime);
            getChauffeurDriveOrderSave.setPreGetOffTime(offtime);
            getChauffeurDriveOrderSave.setGetOnAreaID(getOnAreaID);
            getChauffeurDriveOrderSave.setGoOnAddress(GoOnAddress);
            getChauffeurDriveOrderSave.setGetoffAreaID(getoffAreaID);
            getChauffeurDriveOrderSave.setGoOffAddress(GoOffAddress);
            getChauffeurDriveOrderSave.setIsNeedInvoice(isNeedInvoice);
            getChauffeurDriveOrderSave.setInvoiceHead(invoiceHead);
            getChauffeurDriveOrderSave.setInvoiceMailAddress(invoiceMailAddress);
            getChauffeurDriveOrderSave.setTravelDescription(travelDescription);
            getChauffeurDriveOrderSave.setCarGroupID(carGroupID);
            getChauffeurDriveOrderSave.setProductClassID(productClassID);
            getChauffeurDriveOrderSave.setAirportTypeID(airportTypeID);
            getChauffeurDriveOrderSave.setFlightCode(flightCode);
            getChauffeurDriveOrderSave.setIsNeedPickBrand(isNeedPickBrand);
            getChauffeurDriveOrderSave.setPickBrandContent(pickBrandContent);
            getChauffeurDriveOrderSave.setLuggageCount(LuggageCount);

            getChauffeurDriveOrderSave.setOperatorSN(operatorSN);
            getChauffeurDriveOrderSave.setGoOnAddressXCoordinate(GoOnAddressXCoordinate);
            getChauffeurDriveOrderSave.setGoOnAddressYCoordinate(GoOnAddressYCoordinate);
            getChauffeurDriveOrderSave.setGoOffAddressXCoordinate(GoOffAddressXCoordinate);
            getChauffeurDriveOrderSave.setGoOffAddressYCoordinate(GoOffAddressYCoordinate);
            getChauffeurDriveOrderSave.setMapType(MapType);
            getChauffeurDriveOrderSave.setCouponID(CouponID);
            getChauffeurDriveOrderSave.setMemo(memo);

            GetChauffeurDriveOrderSaveResponse resp = stub.getChauffeurDriveOrderSave(getChauffeurDriveOrderSave,
                    validationSoapHeader6);

            SaveResult driverOrderSaveResualt = resp.getGetChauffeurDriveOrderSaveResult();
            // 订单号
            String billid = String.valueOf(driverOrderSaveResualt.getBillID());
            // 会员id
            driverOrderSaveResualt.getCustomerID();
            // 支付金额
            driverOrderSaveResualt.getRealCharge();
            // 返回错误信息
            driverOrderSaveResualt.getErrrorMsg();

            System.out.println(driverOrderSaveResualt.getBillID());

            System.out.println(driverOrderSaveResualt.getErrrorMsg());

            System.out.println(driverOrderSaveResualt.getRealCharge() + "       "
                    + driverOrderSaveResualt.getCustomerID());

            return billid;
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自驾 通过证件类型、证件号码、手机号码定位查询该会员下个人的订单信息
     * 
     * @param customerID
     * @param orderTimeBegin
     * @param orderTimeEnd
     * @param pageSize
     * @param pageNum
     * @param totalRecord
     */
    public void GetCustomerOrder(int customerID, Calendar orderTimeBegin, Calendar orderTimeEnd, int pageSize,
            int pageNum, int totalRecord) {
        try {
            Supermacy supermacy = new Supermacy();
            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();

            TopOneChauffeurDriverServiceStub.GetCustomerOrder getCustomerOrder = new GetCustomerOrder();

            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader3 = new ValidationSoapHeaderE();

            TopOneChauffeurDriverServiceStub.ValidationSoapHeader pram = new ValidationSoapHeader();

            validationSoapHeader3.setValidationSoapHeader(pram);

            pram.setLoginName(supermacy.getLoginName());
            pram.setLoginPassword(supermacy.getLoginPwd());
            getCustomerOrder.setCustomerID(customerID);
            getCustomerOrder.setOrderTimeBegin(orderTimeBegin);
            getCustomerOrder.setOrderTimeEnd(orderTimeEnd);
            getCustomerOrder.setPageSize(pageSize);
            getCustomerOrder.setPageNum(pageNum);
            getCustomerOrder.setTotalRecord(totalRecord);

            GetCustomerOrderResponse resp = stub.getCustomerOrder(getCustomerOrder, validationSoapHeader3);

            ArrayOfCustomerOrderList customerOrderResault = resp.getGetCustomerOrderResult();
            CustomerOrderList[] custList = customerOrderResault.getCustomerOrderList();
            for (int i = 0; i < custList.length; i++) {

            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 取消订单
     * 
     * @param orderID
     * @return
     */
    public String CancelOrder(int orderID) {
        try {
            Supermacy supermacy = new Supermacy();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeader parm = new ValidationSoapHeader();
            parm.setLoginName(supermacy.getLoginName());
            parm.setLoginPassword(supermacy.getLoginPwd());
            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader0 = new ValidationSoapHeaderE();
            validationSoapHeader0.setValidationSoapHeader(parm);

            TopOneChauffeurDriverServiceStub.CancelOrder cancelOrder = new CancelOrder();
            cancelOrder.setOrderID(orderID);

            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();
            CancelOrderResponse resp = stub.cancelOrder(cancelOrder, validationSoapHeader0);

            String result = resp.getCancelOrderResult();
            if (result != null && result.length() > 0 && !result.equals("")) {
                return result;
            }
            else {
                return null;
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 订单明细查询（取得司机电话及牌号）
     * 
     * @param orderID
     * @return
     */
    public Carorder GetChauffeurDriveOrderInfo(int orderID) {
        TopOneChauffeurDriverServiceStub stub;
        try {
            Supermacy supermacy = new Supermacy();
            stub = new TopOneChauffeurDriverServiceStub();
            TopOneChauffeurDriverServiceStub.GetChauffeurDriveOrderInfo getChauffeurDriveOrderInfo = new GetChauffeurDriveOrderInfo();

            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader5 = new ValidationSoapHeaderE();

            TopOneChauffeurDriverServiceStub.ValidationSoapHeader parm = new ValidationSoapHeader();

            parm.setLoginName(supermacy.getLoginName());

            parm.setLoginPassword(supermacy.getLoginPwd());

            validationSoapHeader5.setValidationSoapHeader(parm);

            getChauffeurDriveOrderInfo.setOrderID(orderID);

            GetChauffeurDriveOrderInfoResponse resp = stub.getChauffeurDriveOrderInfo(getChauffeurDriveOrderInfo,
                    validationSoapHeader5);

            ChauffeurDriveOrderInfo result = resp.getGetChauffeurDriveOrderInfoResult();

            ChauffeurDriveOrderBaseInfo info = result.getChauffeurDriveOrderBaseInfo();
            if (info != null) {

                Carorder ca = new Carorder();

                ca.setDrivername(info.getDriverName());

                ca.setDrivertel(info.getDriverMobileNo());

                ca.setCarnum(info.getShopSign());

                ca.setCarmodel(info.getCarModel());

                System.out.println("司机名字：" + info.getDriverName());

                System.out.println("司机电话：" + info.getDriverMobileNo());

                System.out.println("车牌号码：" + info.getShopSign());

                System.out.println("车型：" + info.getCarModel());
                return ca;
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;

    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        SupermacyMethod temp = new SupermacyMethod();
        temp.GetChooseCarLevelList(3, "2012-12-25 09:00", "2012-12-20 09:00", 1, 1, 1, "2012-12-20 09:00",
                "2012-12-20 09:00", 0D, false, "", "", 0D, 0D, 0D, 0D, 0);
    }

    public void getChooseCarLevelList(int productClassID, String preGetOnTime, String preGetOffTime, int getOnAreaID,
            int getoffAreaID, int personCount,
            TopOneChauffeurDriverServiceStub.ChauffeurDrivePerDayDetails[] DayDetails, String GoOnAddress,
            String GoOffAddress, double GoOnAddressXCoordinate, double GoOnAddressYCoordinate,
            double GoOffAddressXCoordinate, double GoOffAddressYCoordinate, int MapType) {
        try {
            TopOneChauffeurDriverServiceStub stub = new TopOneChauffeurDriverServiceStub();

            TopOneChauffeurDriverServiceStub.GetChooseCarLevelList getChooseCarLevelList = new TopOneChauffeurDriverServiceStub.GetChooseCarLevelList();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE validationSoapHeader4 = new TopOneChauffeurDriverServiceStub.ValidationSoapHeaderE();
            TopOneChauffeurDriverServiceStub.ValidationSoapHeader param = new TopOneChauffeurDriverServiceStub.ValidationSoapHeader();
            Supermacy supermacy = new Supermacy();
            param.setLoginName(supermacy.getLoginName());
            param.setLoginPassword(supermacy.getLoginPwd());
            validationSoapHeader4.setValidationSoapHeader(param);
            TopOneChauffeurDriverServiceStub.GetChooseCarLevelListResponse resp = stub.getChooseCarLevelList(
                    getChooseCarLevelList, validationSoapHeader4);
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class Supermacy {
        private String loginName = "";

        private String LoginPwd = "";

        private String staus = "";// 接口是否启用 1,启用 0,禁用

        private String where = " where 1=1 and " + Eaccount.COL_name + " ='至尊'";

        public Supermacy() {
            List<Eaccount> listEaccountTop1 = Server.getInstance().getSystemService()
                    .findAllEaccount(where, " ORDER BY ID ", -1, 0);
            if (listEaccountTop1.size() > 0) {
                if (listEaccountTop1.get(0).getState() != null && listEaccountTop1.get(0).getState().equals("1")) {
                    loginName = listEaccountTop1.get(0).getUsername();
                    LoginPwd = listEaccountTop1.get(0).getPassword();
                    staus = "1";
                }
                else {
                    System.out.println("-------------至尊被禁用------------------");
                    staus = "0";
                }
            }
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getLoginPwd() {
            return LoginPwd;
        }

        public void setLoginPwd(String loginPwd) {
            LoginPwd = loginPwd;
        }

    }

    /**
     * 根据城市ID获取城市名称
     */
    public static String getCityNameByStr(long cityid) {
        City city = Server.getInstance().getHotelService().findCity(cityid);
        return city != null && city.getName() != null && !"".equals(city.getName()) ? city.getName() : "";
    }

    /**
     * 根据城市code获取城市名称
     * 
     * @param cityid
     * @return
     */
    public static String getCityName(long cityid) {
        City city = Server.getInstance().getHotelService().findCitybyCarCode(String.valueOf(cityid));
        return city != null && city.getName() != null && !"".equals(city.getName()) ? city.getName() : "";
    }
}
