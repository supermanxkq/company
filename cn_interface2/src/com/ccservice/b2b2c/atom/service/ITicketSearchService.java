package com.ccservice.b2b2c.atom.service;

import java.util.List;

import com.ccservice.b2b2c.atom.component.ITicketSearchComponent;
import com.ccservice.b2b2c.base.aircompany.Aircompany;
import com.ccservice.b2b2c.base.cabin.Cabin;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.flightmodel.Flightmodel;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;

public interface ITicketSearchService {

    public ITicketSearchComponent getTicketSearchComponent();

    public void setTicketSearchComponent(ITicketSearchComponent ticketSearchComponent);

    public ITicketSearchComponent getTicket86200Component();

    public void setTicket86200Component(ITicketSearchComponent ticket86200Component);

    public ITicketSearchComponent getTicketYastComponent();

    public void setTicketYastComponent(ITicketSearchComponent ticketYastComponent);

    public ITicketSearchComponent getTicketPIDManComponent();

    public void setTicketPIDManComponent(ITicketSearchComponent ticketPIDManComponent);

    public ITicketSearchComponent getTicketTraveSkyComponent();

    public void setTicketTraveSkyComponent(ITicketSearchComponent ticketTraveSkyComponent);

    public ITicketSearchComponent getTicketBBCComponent();

    public void setTicketBBCComponent(ITicketSearchComponent ticketBBCComponent);

    public String getRemoteAddr();

    public void setRemoteAddr(String addr);

    //调用接口AVOpen
    public String AVOpen(FlightSearch flightSearch);

    /**
     * 根据航班查询参数类得到航班信息列表
     * @param FlightSearch
     * @return FlightInfo 
     */
    public List findAllFlightinfo(FlightSearch flightSearch);

    /**
     * 根据PNR信息得到PNR信息
     * @param PNR
     * @return String 
     */
    public String getPNRInfo(String strPNR);

    /**
     * 生成PNR
     */
    public String CreatePnr(String SS, String NM, String CT, String TK, String NUM, String depTime, String SSR);

    /**
     * 自动出票 ETDZ
     */
    public String Etdz(String pnrcode, String ei, String ETDZcmd, String officeNo);

    /**
     * 提前pnr信息返回list
     * @param pnr
     * @return
     */
    public List getOrderbypnr(String pnr);

    /**
     * 
     * @param strFromCity 出发城市代码
     * @param strTocity   到达城市代码
     * @param strFromDate  出发日期
     * @param ToDate      到达日期
     * @param strTripType 航程类型
     * @param strAirCoType 航空公司代码
     * @return
     */
    public AllRouteInfo getIntelFlightInfo(String strFromCity, String strTocity, String strFromDate, String ToDate,
            String strTripType, String strAirCoType);

    /**
     * 在黑屏创建PNR
     * @param listsegmenginf
     *            //航程信息类
     * @param listpassengers
     *            //乘机人类
     * @param strCustomerCode
     *            //儿童PNR编码中备注的成人PNR编码
     * @return
     */
    public String CreatePNRByCmd(List<Segmentinfo> listsegmenginf, List<Passenger> listpassengers,
            String strCustomerCode);

    /**
     * 取消PNR功能
     * @param strPNR PNR编码
     * @return
     */
    public String XEPNR(String strPNR);

    /**
     * 分离PNR功能
     * @param strPNR PNR编码
     * @param strNumber 分离的乘机人在黑屏中的序号
     * @return
     */
    public String SPPNR(String strPNR, String strNumber);

    /**
     * Q信箱查询
     * @param strPNR PNR编码
     * @param strNumber  要分离乘机人所在序号，必须要与黑屏中的序号一致
     * @return
     */
    public String QMailQT();

    /**
     * 处理某种Q信箱
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQS(String strQueryType);

    /**
     * 释放Q信箱
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQNE();

    /**
     * 释放当前处理下一个
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQN();

    /**
     * 得到大PNR
     * @param strPNR PNR
     * @return
     */
    public String getBigPNRInfo(String strPNR);

    // 小PNR转大编码
    public String getPNRBigInfo(String strPNR);

    public String commandFunction2(String strcmd, String strPageinfo, String strIG);

    public String commandFunction3(String strcmd, String strPageinfo, String strIG);

    public String commandFunction(String strcmd, String strPageinfo);

    public String getTicketNumber(String strPnrInfo, String strPnmber);

    public String getRpNumber(String strTicketNumber);

    public String GetPriceAndDiscountByNFD(String strFromCity, String strToCity, String strFromDate, String strAirCom,
            String strCabinCode);

    public String getFullRTPnrResult(String strPNR);

    /**
     * 获得原始的rt信息
     * @param pnr	pnr
     * @param type	客人类型1成人2儿童3婴儿
     * @return
     * @author chend 	2013-4-16
     */
    public String getRealRT(String pnr, String type);

    /**
     * 获得原始的PAT信息
     * @param pnr	pnr
     * @param type	客人类型1成人2儿童3婴儿
     * @return
     * @author chend 	2013-4-16
     */
    public String getRealPat(String pnr, String type);

    public List<Aircompany> getAircompanyCache();

    public List<Flightmodel> getFlightModelCache();

    public String CreatePNRByInterFace(List<Segmentinfo> listsegment, List<Passenger> listpassenger,
            String strCustomerCode);

    public Cabin getCabinCache(String strKey, String strAirCompanycode, String strCabinCode);

    /**
     * 授权
     * @param pnr	要授权的pnr
     * @param office	officeid
     * @return
     * @author chend	2013-4-16
     */
    public String AUTHpnr(String pnr, String office);

    /**
     * 获得格式化后的价格
     * @param pnr	pnr
     * @param type	客人类型1成人2儿童3婴儿
     * @param isAll	是否获得全部价格1是0返回最低的
     * @return	0000,Q1,580.00,50.00,130.00,760.00|Q,630.00,50.00,130.00,810.00
     * @author chend 	2013-4-16
     */
    public String getPatPrice(String pnr, String type, String isAll);

    /**
     * 获取pnr的状态
     * @param pnr
     * @return
     */
    public String getpnrStatus(String pnr);

    public String getOfficeNumber();

    /**
     * 方法说明：创建行程单(新)
    参数说明：
    PiaoHao：票号
    XCD：行程单号
    OfficeID：OFFICE号
     * 
     * @return
     * @time 2015年6月4日 下午1:43:45
     * @author chendong
     */
    public String Create_XingChengDan_New(String PiaoHao, String XCD, String OfficeID);

    /**
     * 方法说明：作废行程单
    参数说明：
    PiaoHao：票号
    XCD：行程单号
    OfficeID：OFFICE号
     * 
     * @param PiaoHao
     * @param XCD
     * @param OfficeID
     * @return
     * @time 2015年6月4日 下午1:44:50
     * @author chendong
     */
    public String Delete_XingChengDan(String PiaoHao, String XCD, String OfficeID);

}
