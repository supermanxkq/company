package com.ccservice.b2b2c.atom.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.ITicketSearchComponent;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.aircompany.Aircompany;
import com.ccservice.b2b2c.base.cabin.Cabin;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.flightmodel.Flightmodel;
import com.ccservice.b2b2c.base.intermanager.Intermanager;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;

public class TicketSearchService implements ITicketSearchService {

    private final static Log log = LogFactory.getLog(TicketSearchService.class);

    private String addr;

    // 爱特博-亿思凯接口
    private ITicketSearchComponent ticketYastComponent;

    // 黑屏接口
    private ITicketSearchComponent ticketPIDManComponent;

    // TraveSky接口
    private ITicketSearchComponent ticketTraveSkyComponent;

    // 郑州86200接口
    private ITicketSearchComponent ticket86200Component;

    private ITicketSearchComponent ticketSearchComponent;

    //BBC数据接口
    private ITicketSearchComponent ticketBBCComponent;

    //乐途接口
    private ITicketSearchComponent ticketNBEComponent;

    //易思凯接口
    private ITicketSearchComponent ticketYSKComponent;

    public ITicketSearchComponent getTicketYSKComponent() {
        return ticketYSKComponent;
    }

    public void setTicketYSKComponent(ITicketSearchComponent ticketYSKComponent) {
        this.ticketYSKComponent = ticketYSKComponent;
    }

    public ITicketSearchComponent getTicketNBEComponent() {
        return ticketNBEComponent;
    }

    public void setTicketNBEComponent(ITicketSearchComponent ticketNBEComponent) {
        this.ticketNBEComponent = ticketNBEComponent;
    }

    public ITicketSearchComponent getTicketSearchComponent() {
        // TODO Auto-generated method stub
        return ticketBBCComponent;
    }

    public void setTicketSearchComponent(ITicketSearchComponent ticketSearchComponent) {
        this.ticketBBCComponent = ticketSearchComponent;

    }

    public ITicketSearchComponent getTicket86200Component() {
        return ticket86200Component;
    }

    public void setTicket86200Component(ITicketSearchComponent ticket86200Component) {
        this.ticket86200Component = ticket86200Component;
    }

    public ITicketSearchComponent getTicketYastComponent() {
        return ticketYastComponent;
    }

    public void setTicketYastComponent(ITicketSearchComponent ticketYastComponent) {
        this.ticketYastComponent = ticketYastComponent;
    }

    public ITicketSearchComponent getTicketPIDManComponent() {
        return ticketPIDManComponent;
    }

    public void setTicketPIDManComponent(ITicketSearchComponent ticketPIDManComponent) {
        this.ticketPIDManComponent = ticketPIDManComponent;
    }

    public ITicketSearchComponent getTicketTraveSkyComponent() {
        return ticketTraveSkyComponent;
    }

    public void setTicketTraveSkyComponent(ITicketSearchComponent ticketTraveSkyComponent) {
        this.ticketTraveSkyComponent = ticketTraveSkyComponent;
    }

    /*
     * 接口管理 根据用户请求取得用户ip，管理用户查询 @return
     */
    private ITicketSearchComponent selectSearchComponent() {
        String state = "1";
        if (Server.getInstance().getDateHashMap().get("Intermanager1L") == null) {
            Intermanager inter = Server.getInstance().getSystemService().findIntermanager(1L);
            Server.getInstance().getDateHashMap().put("Intermanager1L", inter.getState() + "");
        }
        else {
            state = Server.getInstance().getDateHashMap().get("Intermanager1L");
        }
        //选择启用机票接口的组件
        if ("1".equals(state)) {
            //			CBE接口(LT)
            return this.ticketNBEComponent;
        }
        else if ("2".equals(state)) {
            //			CCPLUSE
            return this.ticketBBCComponent;
        }

        List<Intermanager> list = new ArrayList<Intermanager>();
        //		List<Intermanager> list = Server.getInstance().getSystemService()
        //		.findAllIntermanager(
        //				"WHERE " + Intermanager.COL_resourceip + " ='"
        //						+ this.getRemoteAddr() + "'", "", 1, 0);

        Intermanager intermanager = list.get(0);

        Calendar cur = Calendar.getInstance();
        Calendar tmp = Calendar.getInstance();

        cur.setTime(new Date());
        tmp.setTime(intermanager.getCurtime());

        int type = intermanager.getLimittype();
        switch (type) {
        case 0:

            break;
        case 1:// 年
            if (cur.get(Calendar.YEAR) == tmp.get(Calendar.YEAR)) {
                if (intermanager.getEffecttimes() > intermanager.getLimittimes()) {
                    return null;
                }

            }
            else {
                intermanager.setEffecttimes(0);
                intermanager.setCurtime(new Timestamp(cur.getTimeInMillis()));

            }
            break;
        case 2:// 月
            if (cur.get(Calendar.MONTH) == tmp.get(Calendar.MONTH)) {
                if (intermanager.getEffecttimes() > intermanager.getLimittimes()) {
                    return null;
                }

            }
            else {
                intermanager.setEffecttimes(0);
                intermanager.setCurtime(new Timestamp(cur.getTimeInMillis()));

            }
            break;
        case 3:// 日
            if (cur.get(Calendar.DAY_OF_YEAR) == tmp.get(Calendar.DAY_OF_YEAR)) {
                if (intermanager.getEffecttimes() > intermanager.getLimittimes()) {
                    return null;
                }

            }
            else {
                intermanager.setEffecttimes(0);
                intermanager.setCurtime(new Timestamp(cur.getTimeInMillis()));

            }
            break;

        default:
            break;
        }

        intermanager.setEffecttimes(intermanager.getEffecttimes() + 1);
        Server.getInstance().getSystemService().updateIntermanager(intermanager);

        log.debug(intermanager);

        if (intermanager.getInterurl().equals("ticketYastComponent")) {
            return ticketYastComponent;
        }
        else if (intermanager.getInterurl().equals("ticketPIDManComponent")) {
            return ticketPIDManComponent;
        }
        else if (intermanager.getInterurl().equals("ticketTraveSkyComponent")) {
            return ticketTraveSkyComponent;
        }
        else if (intermanager.getInterurl().equals("ticket86200Component")) {
            return ticket86200Component;
        }
        else if (intermanager.getInterurl().equals("ticketBBCComponent")) {
            return ticketBBCComponent;
        }
        return ticketNBEComponent;
    }

    // 调用接口AVOpen
    public String AVOpen(FlightSearch flightSearch) {
        ticketSearchComponent = selectSearchComponent();

        return ticketSearchComponent.AVOpen(flightSearch);
    }

    // 查询航班
    /**
     * 根据航班查询参数类得到航班信息列表
     * 
     * @param FlightSearch
     * @return
     * @return FlightInfo
     */
    public List findAllFlightinfo(FlightSearch flightSearch) {
        //使用黑屏接口或者IBE接口
        //        System.out.println("IBE查询开始:" + addr);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.findAllFlightinfo(flightSearch);

        // ×××××××××××××××××以下为艾特伯接口调用------------------------
        //return Server.getInstance().getTicketSearchService2().findAllFlightinfo2(flightSearch, "122.11.0.195", "", "");
        // ×××××××××××××××××以下为艾特伯接口调用-----------------------------

    }

    // 提取PNR信息
    public String getPNRInfo(String strPNR) {
        //        System.out.println("查询开始:" + addr);

        ticketSearchComponent = selectSearchComponent();

        return ticketSearchComponent.getPNRInfo(strPNR);
    }

    // 生成PNR
    public String CreatePnr(String SS, String NM, String CT, String TK, String NUM, String depTime, String SSR) {
        System.out.println("查询开始:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.CreatePnr(SS, NM, CT, TK, NUM, depTime, SSR);
    }

    /**
     * 在黑屏创建PNR
     * 
     * @param listsegmenginf
     *            航程类
     * @param listpassengers
     *            乘机人类
     * @return
     */
    public String CreatePNRByCmd(List<Segmentinfo> listsegmenginf, List<Passenger> listpassengers,
            String strCustomerCode) {
        System.out.println("开始黑屏创建PNR:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.CreatePNRByCmd(listsegmenginf, listpassengers, strCustomerCode);
    }

    /**
     * 取消PNR功能
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String XEPNR(String strPNR) {
        System.out.println("开始取消PNR:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.XEPNR(strPNR);
    }

    /**
     * 分离PNR功能
     * 
     * @param strPNR
     *            PNR编码
     * @param strNumber
     *            分离的乘机人在黑屏中的序号
     * @return
     */
    public String SPPNR(String strPNR, String strNumber) {
        System.out.println("开始分离PNR:" + addr);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.SPPNR(strPNR, strNumber);
    }

    /**
     * Q信箱查询
     * 
     * @param strPNR
     *            PNR编码
     * @param strNumber
     *            要分离乘机人所在序号，必须要与黑屏中的序号一致
     * @return
     */
    public String QMailQT() {
        System.out.println("开始Q信箱查询:" + addr);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.QMailQT();
    }

    /**
     * 处理某种Q信箱
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQS(String strQueryType) {
        System.out.println("开始处理Q信箱:" + addr);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.QMailQS(strQueryType);
    }

    /**
     * 释放Q信箱
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQNE() {
        System.out.println("开始释放Q信箱:" + addr);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.QMailQNE();
    }

    /**
     * 释放当前处理下一个
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQN() {
        System.out.println("开始处理下一个Q信箱:" + addr);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.QMailQN();
    }

    // 自动出票指令
    public String Etdz(String pnrcode, String ei, String ETDZcmd, String officeNo) {
        System.out.println("查询开始:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.Etdz(pnrcode, ei, ETDZcmd, officeNo);
    }

    /**
     * 通过pnr创建订单
     */
    public List getOrderbypnr(String pnr) {
        System.out.println("查询开始:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getOrderbypnr(pnr);
    }

    /**
     * 得到大PNR
     * 
     * @param strPNR
     *            PNR
     * @return
     */
    public String getBigPNRInfo(String strPNR) {
        System.out.println("正在获取大PNR:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getBigPNRInfo(strPNR);
    }

    /**
     * 获取票号
     */
    public String getTicketNumber(String strPnrInfo, String strPnmber) {
        System.out.println("正在获取票号:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getTicketNumber(strPnrInfo, strPnmber);
    }

    public String getRpNumber(String strTicketNumber) {
        System.out.println("正在获取行程单号:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getRpNumber(strTicketNumber);
    }

    // 小PNR转大编码
    public String getPNRBigInfo(String strPNR) {

        System.out.println("正在把小编码转换成大编码:" + addr);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getPNRBigInfo(strPNR);
    }

    public String commandFunction(String strcmd, String strPageinfo) {
        System.out.println("正在执行通用指令:" + addr);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.commandFunction(strcmd, strPageinfo);
    }

    public String commandFunction2(String strcmd, String strPageinfo, String strIG) {
        System.out.println("正在执行通用指令:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.commandFunction2(strcmd, strPageinfo, strIG);
    }

    public String CreatePNRByInterFace(List<Segmentinfo> listsegment, List<Passenger> listpassenger,
            String strCustomerCode) {
        System.out.println("正在通过接口生成PNR:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.CreatePNRByInterFace(listsegment, listpassenger, strCustomerCode);
    }

    /**
     * 
     * @param strFromCity
     *            出发城市代码
     * @param strTocity
     *            到达城市代码
     * @param strFromDate
     *            出发日期
     * @param ToDate
     *            到达日期
     * @param strTripType
     *            航程类型
     * @param strAirCoType
     *            航空公司代码
     * @return
     */
    public AllRouteInfo getIntelFlightInfo(String strFromCity, String strTocity, String strFromDate, String ToDate,
            String strTripType, String strAirCoType) {
        System.out.println("查询开始:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getIntelFlightInfo(strFromCity, strTocity, strFromDate, ToDate, strTripType,
                strAirCoType);
    }

    public String getRemoteAddr() {

        return addr;
    }

    public void setRemoteAddr(String addr) {
        this.addr = addr;

    }

    public String GetPriceAndDiscountByNFD(String strFromCity, String strToCity, String strFromDate, String strAirCom,
            String strCabinCode) {
        System.out.println("开始提取特价价格:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.GetPriceAndDiscountByNFD(strFromCity, strToCity, strFromDate, strAirCom,
                strCabinCode);
    }

    public String getFullRTPnrResult(String strPNR) {
        System.out.println("开始提取PNR全部信息:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getFullRTPnrResult(strPNR);
    }

    public List<Aircompany> getAircompanyCache() {
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getAircompanyCache();
    }

    public List<Flightmodel> getFlightModelCache() {
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getFlightModelCache();
    }

    public Cabin getCabinCache(String strKey, String strAirCompanycode, String strCabinCode) {
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getCabinCache(strKey, strAirCompanycode, strCabinCode);
    }

    public ITicketSearchComponent getTicketBBCComponent() {
        return ticketBBCComponent;
    }

    public void setTicketBBCComponent(ITicketSearchComponent ticketBBCComponent) {
        this.ticketBBCComponent = ticketBBCComponent;
    }

    @Override
    public String AUTHpnr(String pnr, String office) {
        System.out.println("开始授权" + pnr + ":" + office);
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.AUTHpnr(pnr, office);
        // TODO Auto-generated method stub
    }

    @Override
    public String getRealRT(String pnr, String type) {
        // TODO Auto-generated method stub
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getRealRT(pnr, type);
    }

    @Override
    public String getRealPat(String pnr, String type) {
        // TODO Auto-generated method stub
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getRealPat(pnr, type);
    }

    @Override
    public String getPatPrice(String pnr, String type, String isAll) {
        // TODO Auto-generated method stub
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getPatPrice(pnr, type, isAll);
    }

    @Override
    public String getpnrStatus(String pnr) {
        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.getpnrStatus(pnr);
    }

    @Override
    public String commandFunction3(String strcmd, String strPageinfo, String strIG) {
        System.out.println("正在执行通用指令3:" + addr);

        ticketSearchComponent = selectSearchComponent();
        return ticketSearchComponent.commandFunction2(strcmd, strPageinfo, strIG);
    }

    @Override
    public String getOfficeNumber() {
        return selectSearchComponent().getOfficeNumber();
    }

    @Override
    public String Create_XingChengDan_New(String PiaoHao, String XCD, String OfficeID) {
        return selectSearchComponent().Create_XingChengDan_New(PiaoHao, XCD, OfficeID);
    }

    @Override
    public String Delete_XingChengDan(String PiaoHao, String XCD, String OfficeID) {
        return selectSearchComponent().Delete_XingChengDan(PiaoHao, XCD, OfficeID);
    }

}
