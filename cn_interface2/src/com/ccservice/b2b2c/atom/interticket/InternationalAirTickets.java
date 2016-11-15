package com.ccservice.b2b2c.atom.interticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.dom4j.DocumentHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import client.ServiceStub;

import com.ccservice.b2b2c.atom.component.CacheBaseData;
import com.ccservice.b2b2c.atom.component.CacheFlightInfo;
import com.ccservice.b2b2c.atom.component.CachePriceInfo;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.fflight.AllFlight;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.fflight.Route;
import com.ccservice.b2b2c.base.fflight.RouteDetailInfo;
import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;

public class InternationalAirTickets implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 123456L;

    private String username;

    private String password;

    private String ipAddress;

    private String userid;

    private String officeNumber;

    private CacheFlightInfo cacheflight = new CacheFlightInfo();

    private CacheBaseData cachebasedata = new CacheBaseData();

    private CachePriceInfo cacheprice = new CachePriceInfo();

    /**
     * 国际机票查询接口 高亮
     * 
     * @date 2011-11-25
     * @param args
     * @throws IOException
     * @throws JDOMException
     */
    public AllRouteInfo airTicketSearch(String strFromCity, String strToCity, String strFromDate, String strReturnDate,
            String strseatType, String tirptype) throws Exception {
        // 航班全部路线
        AllRouteInfo allrouteinfo = new AllRouteInfo();
        //java.io.InputStream in = null;
        String totalurl = "http://122.115.62.70/cn_interface/airtickgj.jsp?test=";
        totalurl += "getInterDetailFltValuesV3?";
        //String totalurl = "http://service2.travel-data.cn/service.asmx/getInterDetailFltValuesV3?";
        totalurl += "u=" + "6ed3fc98f2da5089a6144598a6a320e3"; // 用户ID
        totalurl += "&sc=" + strFromCity;// 出发城市
        totalurl += "&ec=" + strToCity;// 到达城市
        totalurl += "&sd=" + strFromDate;// 出发日期
        totalurl += "&ed=" + strReturnDate;// 返回日期
        totalurl += "&Air=" + "";// 预留字段
        totalurl += "&classtype=" + strseatType;// 类型 不清楚 规定为"Y"
        totalurl += "&ft=" + tirptype;// 1为单程，2为往返

        // 准备访问
        //String url = "d://123.xml";
        System.out.println("-----------查询国际机票开始！--------------");
        try {

            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            //in=conn.getInputStream();	
            InputStream is = conn.getInputStream();
            InputStreamReader isr = null;
            // use default characterset
            isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(isr);
            StringWriter out = new StringWriter();
            int c = -1;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            String strReturn = out.toString().replace("[\r]", "").replace("[\n]", "");
            String[] strArr = strReturn.split("[\r]");
            String strTemp = "";
            for (int i = 0; i < strArr.length; i++) {
                if (!strArr[i].equals("")) {
                    strTemp += strArr[i].replaceAll("[\n]", "").trim();
                }
            }

            SAXBuilder build = new SAXBuilder();
            Document doc = build.build(new StringReader(strTemp.trim()));
            Element lists = doc.getRootElement();// 获取第一个父节点lists
            Element re = lists.getChild("flights");
            List<Element> routes1 = re.getChildren("flight");
            System.out.println(routes1.size() + "个航空公司");

            if (routes1.size() > 0) {
                List<Route> listroute = new ArrayList<Route>(); // 航班线路Route类集合

                int indexroute = 0;
                // 添加路线基本信息
                allrouteinfo.setAllRouteID(1);
                allrouteinfo.setRouteStr(strFromCity + "-" + strToCity);

                /** flightlist循环开始* */
                for (int j = 0; j < routes1.size(); j++) { // 循环flight集合
                    Element flight = routes1.get(j);
                    /** 第二个载入信息开始* */
                    List<RouteDetailInfo> listroutedetailinfo = new ArrayList<RouteDetailInfo>();// 航班线路详情类
                    Route routeinfo = new Route();// 实例化航班线路

                    routeinfo.setDestCity(flight.getChildText("depport"));// ok得到出发城市三字码
                    System.out.println("出发城市" + flight.getChildText("depport"));
                    routeinfo.setFromCity(flight.getChildText("arrport"));// 到达城市三字码

                    routeinfo.setAirCompany(flight.getChildText("airline"));// 航空公司标示

                    //国际机票调整价格
                    Double daddprice = 10d;
                    try {
                        List<Sysconfig> listsysconfig = com.ccservice.b2b2c.atom.server.Server.getInstance()
                                .getSystemService().findAllSysconfig("where C_NAME='interticketaddprice'", "", -1, 0);
                        daddprice = Double.parseDouble(listsysconfig.get(0).getValue());
                    }
                    catch (Exception ex) {
                        daddprice = 10d;
                    }
                    routeinfo.setTotalFare(Double.parseDouble(flight.getChildText("adultprice")) + daddprice);// 成人价格

                    routeinfo.setTotalChlidFare(Double.parseDouble(flight.getChildText("childprice")) + daddprice);// 儿童价格

                    routeinfo.setPolicyInfo(flight.getChildText("rulestr"));// 政策标识

                    routeinfo.setTotalTax(Double.parseDouble(flight.getChildText("tax")));// 税金

                    // 是否转机
                    Element deproute = flight.getChild("deproute");
                    routeinfo.setIsChangeFlight(Integer.parseInt(deproute.getChildText("transfer")));
                    //出发时间
                    routeinfo.setDepdateTime(deproute.getChildText("depdate") + "," + deproute.getChildText("deptime"));
                    //到达时间
                    routeinfo.setArrdateTime(deproute.getChildText("arrdate") + "," + deproute.getChildText("arrtime"));
                    routeinfo.setPolicyMark(flight.getChildText("currency"));// 价格标识

                    routeinfo.setRouteStr(strFromCity + "-" + strToCity);// 航班线路段
                    // 航班详细路线 也在下一个节点中 等待传入List<RouteDetailInfo>。。。
                    indexroute++;
                    routeinfo.setID(indexroute);// 记录数ID

                    //装载routeDetailInfo信息开始

                    Element flightdatas = flight.getChild("flightdatas");
                    List<Element> segmentlist = flightdatas.getChildren();
                    System.out.println("路线个数" + segmentlist.size());

                    for (int k = 0; k < segmentlist.size(); k++) {
                        RouteDetailInfo routeDetailInfo = new RouteDetailInfo();
                        Element segment = segmentlist.get(k);
                        List<AllFlight> allflight = new ArrayList<AllFlight>();// 4.4//// 航班线路详情明细类

                        routeDetailInfo.setAirCompany(segment.getChildText("air"));// 航空公司编号

                        routeDetailInfo.setDestCity(segment.getChildText("arr"));// 到达城市
                        System.out.println("路线出发城市" + segment.getChildText("dep"));
                        routeDetailInfo.setFromCity(segment.getChildText("dep"));// 出发城市

                        routeDetailInfo.setCabin(segment.getChildText("seat"));// 仓位

                        routeDetailInfo.setFromAirport(segment.getChildText("depairport"));// 出发机场。没有，暂时用出发城市代替

                        routeDetailInfo.setToAirport(segment.getChildText("arrairport"));// 同上

                        routeDetailInfo.setFlyTime(segment.getChildText("flytime"));//飞行时长

                        routeDetailInfo.setPlane(segment.getChildText("plane"));//机型

                        routeDetailInfo.setFromDate(segment.getChildText("depdate") + ","
                                + segment.getChildText("deptime"));// 出发时间

                        routeDetailInfo.setToDate(segment.getChildText("arrdate") + ","
                                + segment.getChildText("arrtime"));// 到达时间

                        routeDetailInfo.setTotalFlightNo(Integer.parseInt(segment.getChildText("stop")));// 本段航程数量
                        routeDetailInfo.setFlightNumber(segment.getChildText("flightno"));//航班号

                        routeDetailInfo.setFlightInfos(allflight);

                        listroutedetailinfo.add(routeDetailInfo);
                    }

                    System.out.println("几个" + listroutedetailinfo.size());
                    //装载routeDetailInfo信息结束

                    routeinfo.setRouteDetailInfos(listroutedetailinfo);

                    listroute.add(routeinfo);

                }
                /** flightlist循环结束* */

                allrouteinfo.setRoutes(listroute);// ALLRoute完毕
                System.out.println(allrouteinfo + "第一个");// 第一个
                /** 第一个载入信息结束* */
            }
            else {
                // 没有查到信息
            }
            in.close();
            conn.disconnect();
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("-----------查询国际机票结束！--------------");
        return allrouteinfo;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    /////////////////////////////////////////国内机票接口
    /**
     * @author 赵晓晓
     * 查询最低价机票接口
     */
    public String lowAirTicket(String strFromCity, String strToCity, String strFromDate, String strReturnDate)
            throws Exception {
        //保存路径和需要的参数
        String totalurl = "http://220.113.41.88/cn_interface/airtickgj.jsp?test=";
        totalurl += "getFormatValuesV2?";
        totalurl += "u=" + "6ed3fc98f2da5089a6144598a6a320e3"; // 请求ID
        totalurl += "&sc=" + strFromCity;// 出发城市
        totalurl += "&ec=" + strToCity;// 到达城市
        totalurl += "&sd=" + strFromDate;// 出发日期
        totalurl += "&ed=" + strReturnDate;// 返回日期
        totalurl += "&showround=" + "";// 预留字段
        totalurl += "&f=" + "";//航空公司二字码
        totalurl += "&t=" + "";//预留字段
        System.out.println("url==" + totalurl);
        try {
            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = null;
            isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(isr);
            StringWriter out = new StringWriter();
            int c = -1;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            String strReturn = out.toString().replace("[\r]", "").replace("[\n]", "");
            String[] strArr = strReturn.split("[\r]");
            String strTemp = "";
            for (int i = 0; i < strArr.length; i++) {
                if (!strArr[i].equals("")) {
                    strTemp += strArr[i].replaceAll("[\n]", "").trim();
                }
            }
            SAXBuilder build = new SAXBuilder();
            Document doc = build.build(new StringReader(strTemp.trim()));
            Element element = doc.getRootElement();
            //获得xml文件中的字符串
            String returnstrs = element.getValue();
            //获取查询的结果的总记录数
            String returnstr = returnstrs.replace("$", ",");
            //获得！分割的数组
            String[] str = returnstr.split("!");
            //如果查询成功
            if (str[0].equals("0")) {
                //获取成功后的信息
                String flag = str[0];
                //获得出发城市
                String fromCity = str[1];
                //到达城市
                String toCity = str[2];
                //出发日期
                String fromDate = str[3];
                //判断是否有航班
                if (str.length > 4 && !str[4].equals("")) {
                    //获取不同航班的没组数据
                    String[] ticketLength = str[4].split(",");
                    //循环获取每组数据
                    for (int i = 0; i < ticketLength.length; i++) {
                        String messaage = ticketLength[i];
                        String[] ticketstr = messaage.split("@");
                        //出发城市
                        String fcit = ticketstr[0];
                        //到达城市
                        String tcity = ticketstr[1];
                        //出发日期
                        String fdate = ticketstr[2];
                        //航班号
                        String flyno = ticketstr[3];
                        System.out.println("航班号：" + flyno);
                        //航空公司二字段
                        String flycomputer = ticketstr[4];
                        //机型
                        String flytype = ticketstr[5];
                        //出发时间
                        String fromtime = ticketstr[6];
                        //到达时间
                        String totime = ticketstr[7];
                        //是否经停
                        Long isstop = Long.parseLong(ticketstr[8]);
                        //是否有餐
                        if (ticketstr[9] != "") {
                            //Long isfood=Long.parseLong(ticketstr[9]);
                        }
                        //是否中转
                        Long ischance = Long.parseLong(ticketstr[10]);
                        //是否电子客票
                        Long isticket = Long.parseLong(ticketstr[11]);
                        //全价
                        String allprice = ticketstr[12];
                        //机建费
                        String jprice = ticketstr[13];
                        //燃油费
                        String ryprice = ticketstr[14];
                        //读取这些信息*折扣名称*舱位数量*舱位代码*采购航空公司
                        String[] zkstr = ticketstr[15].split("[*]");
                        System.out.println(ticketstr[15]);
                        //票面价
                        String ticketprice = zkstr[0];
                        //折扣名称
                        String zkname = zkstr[1];
                        System.out.println("折扣名称：" + zkname);
                        //舱位数量
                        Long number = Long.parseLong(zkstr[2]);
                        //舱位代码
                        String flycode = zkstr[3];
                        System.out.println(flycode);
                        //采购航空公司
                        //String flycom=zkstr[4];
                        //其它舱位
                        if (ticketstr[1] != "") {
                            String othfly = ticketstr[16];
                        }
                        //航班信息验证串
                        String checkmess = ticketstr[17];
                        System.out.println(checkmess);
                    }
                }
            }
            //查询失败
            else {
                String message = str[0];
                System.out.println(message + "失败");
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "null";
    }

    /**
     * 查询其他舱位接口
     * @param args
     * @throws IOException
     * @throws JDOMException
     */
    public String otherAirSearch(String strFromCity, String strToCity, String strFromDate, String strFlyno,
            String strflymess) throws Exception {
        //保存路径和需要的参数
        String totalurl = "http://220.113.41.88/cn_interface/airtickgj.jsp?test=";
        totalurl += "getFormatSubValues?";
        totalurl += "u=" + "6ed3fc98f2da5089a6144598a6a320e3"; // 请求ID
        totalurl += "&sc=" + strFromCity;// 出发城市
        totalurl += "&ec=" + strToCity;// 到达城市
        totalurl += "&sd=" + strFromDate;// 出发日期
        totalurl += "&fn=" + strFlyno;//航班号
        totalurl += "&v=" + strflymess;//航班信息验证串
        try {
            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = null;
            isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(isr);
            StringWriter out = new StringWriter();
            int c = -1;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            String strReturn = out.toString().replace("[\r]", "").replace("[\n]", "");
            String[] strArr = strReturn.split("[\r]");
            String strTemp = "";
            for (int i = 0; i < strArr.length; i++) {
                if (!strArr[i].equals("")) {
                    strTemp += strArr[i].replaceAll("[\n]", "").trim();
                }
            }
            SAXBuilder build = new SAXBuilder();
            Document doc = build.build(new StringReader(strTemp.trim()));
            Element element = doc.getRootElement();
            //获得xml文件中的字符串
            String returnstr = element.getValue();
            //获取查询的结果的总记录数
            String[] ticketLength = returnstr.split("$");
            //循环获取机票的详细信息
            for (int i = 0; i < ticketLength.length; i++) {
                String[] str = returnstr.split("!");
                //如果查询成功
                if (str[0].equals("0")) {
                    //保存成功信息
                    String flag = str[0];
                    //获得出发城市
                    String fromCity = str[1];
                    System.out.println("出发城市" + fromCity);
                    //到达城市
                    String toCity = str[2];
                    System.out.println("到达城市：" + toCity);
                    //出发日期
                    String fromDate = str[3];
                    //获得@截取的值
                    String[] ticketstr = str[4].split("@");
                    //出发城市
                    String fcit = ticketstr[0];
                    //到达城市
                    String tcity = ticketstr[1];
                    //航班号
                    String flyno = ticketstr[2];

                    //出发时间
                    String fromtime = ticketstr[3];
                    //到达时间
                    String totime = ticketstr[4];
                    //机型
                    String flytype = ticketstr[5];
                    //全价
                    String allprice = ticketstr[6];
                    //机建费
                    String jprice = ticketstr[7];
                    //燃油费
                    String ryprice = ticketstr[8];
                    //获取舱位代码#舱位数量#票面价
                    String[] infostr = ticketstr[9].split("[-]");
                    for (int j = 0; j < infostr.length; j++) {
                        String[] infofly = infostr[j].split("#");
                        //获取舱位信息
                        String flycode = infofly[0];
                        //舱位数量
                        Long number = Long.parseLong(infofly[1]);
                        //获得票面价
                        String ticketprice = infofly[2];
                    }
                }
                //查询失败
                else {
                    String message = str[0];
                    System.out.println(message + "失败");
                }
            }

            System.out.println(strTemp);
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "null";
    }

    /**
    * 通过CBE接口查询航班列表
    * @param FromCity	起飞城市
    * @param ToCity 到达城市
    * @param TakeoffDate	起飞日期(格式yyyy-MM-dd)
    * @param TakeoffTime	起飞时间(格式HHmm,可以为空)
    * @param Carrier	航空公司,为空则不指定航空公司
    * @param IsDirect	是否只查询直飞航班(0=否,1=是)
    * @param IsShared	是否显示共享航班(0=否,1=是)
    */
    /**
     * @param FromCity1
     * @param ToCity1
     * @param TakeoffDate1
     * @param TakeoffTime1
     * @param Carrier1
     * @param IsDirect1
     * @param IsShared1
     * @return
     */
    public AllRouteInfo searchFlightByLT(String FromCity1, String ToCity1, String TakeoffDate1, String TakeoffTime1,
            String Carrier1, String IsDirect1, String IsShared1) {
        AllRouteInfo allRouteInfo = new AllRouteInfo();
        allRouteInfo.setRouteStr(FromCity1 + "-" + ToCity1);
        try {
            //写入缓存
            String result = cacheflight.searchFlightByLT(FromCity1, ToCity1, TakeoffDate1, TakeoffTime1, Carrier1,
                    IsDirect1, IsShared1, officeNumber);
            org.dom4j.Document document = DocumentHelper.parseText(result);
            org.dom4j.Element root = document.getRootElement();
            String Is_Success = root.elementText("Is_Success");
            if (Is_Success.equals("1")) {
                List<Route> listRoutes = new ArrayList<Route>();
                List<org.dom4j.Element> AvhInfos = root.elements("AvhInfo");
                if (AvhInfos.size() > 0) {
                    for (int i = 0; i < AvhInfos.size(); i++) {
                        // 航班线路类-Routes
                        Route routeinfo = new Route();// 实例化航班线路
                        org.dom4j.Element avhInfoElement = AvhInfos.get(i);
                        String fromCity = avhInfoElement.elementText("FromCity");
                        routeinfo.setFromCity(fromCity);
                        String toCity = avhInfoElement.elementText("ToCity");
                        routeinfo.setDestCity(toCity);
                        String totalFlightTime = avhInfoElement.elementText("TotalFlightTime");
                        routeinfo.setFlyTotalTime(totalFlightTime);
                        List<org.dom4j.Element> flightInfoElements = avhInfoElement.elements("FlightInfo");
                        List<RouteDetailInfo> listroutedetails = new ArrayList<RouteDetailInfo>();
                        String routeRouteStr = "";
                        String routeAirCompany = "";
                        int flightinfo = flightInfoElements.size();
                        for (int j = 0; j < flightInfoElements.size(); j++) {
                            RouteDetailInfo routedetail = new RouteDetailInfo();
                            org.dom4j.Element FlightInfo = flightInfoElements.get(j);
                            String Carrier = FlightInfo.elementText("Carrier");
                            routedetail.setAirCompany(Carrier);
                            String FlightNum = FlightInfo.elementText("FlightNum");
                            routedetail.setFlightNumber(FlightNum);
                            String IsShared = FlightInfo.elementText("IsShared");
                            routedetail.setIsshareflight(IsShared);
                            String SharedFlightNum = FlightInfo.elementText("SharedFlightNum");
                            String FromCityCode = FlightInfo.elementText("FromCityCode");
                            routedetail.setFromAirport(FromCityCode);
                            String ToCityCode = FlightInfo.elementText("ToCityCode");
                            routedetail.setToAirport(ToCityCode);
                            if (j == 0) {
                                routeRouteStr += FromCityCode + "-" + ToCityCode;
                            }
                            else {
                                routeRouteStr += "-" + ToCityCode;

                            }
                            String TakeoffDate = FlightInfo.elementText("TakeoffDate");
                            routedetail.setFromDate(TakeoffDate);
                            String TakeoffTime = FlightInfo.elementText("TakeoffTime");
                            routedetail.setFromTime(TakeoffTime);
                            String ArriveDate = FlightInfo.elementText("ArriveDate");
                            routedetail.setToDate(ArriveDate);
                            String ArriveTime = FlightInfo.elementText("ArriveTime");
                            routedetail.setToTime(ArriveTime);
                            //F8 A2 CA DA ZS JS RS YA BA MA HS KS LS QS GS SS NS VS US TS ES
                            String SeatClass = FlightInfo.elementText("SeatClass");
                            routedetail.setCabin(SeatClass);
                            routedetail.setCarbinInfos(parseCabin(SeatClass, routedetail));
                            if (routedetail.getCarbinInfos().size() > 0) {
                                routedetail.setCabin(routedetail.getCarbinInfos().get(0).getCabin());
                            }
                            String ChildSeatClass = FlightInfo.elementText("ChildSeatClass");
                            //连接协议
                            String ConnProtocol = FlightInfo.elementText("ConnProtocol");
                            String AirplaneType = FlightInfo.elementText("AirplaneType");
                            routedetail.setPlane(AirplaneType);
                            String IsStop = FlightInfo.elementText("IsStop");
                            String StopCity = FlightInfo.elementText("StopCity");
                            routedetail.setStopovers(IsStop + "-" + StopCity);
                            //B 早餐 C 免费酒精饮料 D 正餐 F 供采购的食物 G 供采购的食物和饮料 H 热的膳食 K 轻快早餐 L 午餐 M 膳食 N 没有饭食供应 O 冷的膳食 
                            //P 供采购的酒精饮料 R 茶点 S 快餐 V 供采购的茶点 
                            String FoodMark = FlightInfo.elementText("FoodMark");
                            routedetail.setFoodMark(FoodMark);
                            //航站楼T3 T1
                            String Terminal = FlightInfo.elementText("Terminal");
                            if (Terminal != "") {
                                try {
                                    routedetail.setDepart_terminal(Terminal.split(" ")[0].equals("--") ? "" : Terminal
                                            .split(" ")[0].trim());
                                    routedetail.setArrival_terminal(Terminal.split(" ")[1].equals("--") ? "" : Terminal
                                            .split(" ")[1].trim());
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            String FlightTime = FlightInfo.elementText("FlightTime");
                            routedetail.setFlyTime(FlightTime);
                            String StopTime = FlightInfo.elementText("StopTime");
                            routedetail.setStopTime(StopTime);
                            if (routedetail.getCarbinInfos().size() > 0) {
                                listroutedetails.add(routedetail);
                            }
                            else {

                            }
                            if (j > 0) {
                                routeAirCompany += "|";
                            }
                            routeAirCompany += FlightNum;
                        }
                        routeinfo.setIsChangeFlight(listroutedetails.size());
                        routeinfo.setAirCompany(routeAirCompany);
                        routeinfo.setRouteStr(routeRouteStr);
                        routeinfo.setRouteDetailInfos(listroutedetails);
                        if (listroutedetails.size() > 0 && flightinfo == routeinfo.getRouteDetailInfos().size()) {
                            listRoutes.add(routeinfo);
                        }
                    }
                    allRouteInfo.setRoutes(listRoutes);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        allRouteInfo = setPrice(allRouteInfo);
        setothercabin(allRouteInfo);
        return allRouteInfo;
    }

    private void setothercabin(AllRouteInfo allRouteInfo) {
        for (int i = 0, size = allRouteInfo.getRoutes().size(); i < size; i++) {
            Route route = allRouteInfo.getRoutes().get(i);
            String cabinstring = "";
            String cabinstring1 = "";
            for (int j = 0, jsize = route.getRouteDetailInfos().size(); j < jsize; j++) {
                RouteDetailInfo routeDetailInfo = route.getRouteDetailInfos().get(j);
                try {
                    cabinstring += routeDetailInfo.getCarbinInfos().get(jsize > 1 ? 1 : 0).getCabin();
                }
                catch (Exception e) {
                }
                cabinstring1 += routeDetailInfo.getCarbinInfos().get(routeDetailInfo.getCarbinInfos().size() - 1)
                        .getCabin();

            }
        }
    }

    /**
     * 转化舱位信息返回可用舱位
     * @param cabinString	F8 A2 CA DA ZS JS RS YA BA MA HS KS LS QS GS SS NS VS US TS ES
     * @return
     */
    public static List<CarbinInfo> parseCabin(String cabinString, RouteDetailInfo routedetail) {
        List<CarbinInfo> carbinInfos = new ArrayList<CarbinInfo>();
        List<CarbinInfo> economyCabin = new ArrayList<CarbinInfo>();
        List<CarbinInfo> businessCabin = new ArrayList<CarbinInfo>();
        List<CarbinInfo> firstCabin = new ArrayList<CarbinInfo>();
        String[] cabins = cabinString.split(" ");
        int flag = 0;
        for (int i = cabins.length - 1; i >= 0; i--) {
            try {
                char numString = cabins[i].charAt(cabins[i].length() - 1);
                if (numString == 'A' || (numString > 48 && numString < 58)) {
                    CarbinInfo carbinInfo = new CarbinInfo();
                    carbinInfo.setCabin(cabins[i].substring(0, cabins[i].length() - 1));
                    carbinInfo.setSeatNum(numString + "");
                    carbinInfos.add(carbinInfo);
                    if (flag == 0) {
                        economyCabin.add(carbinInfo);
                    }
                    if (flag == 1) {
                        businessCabin.add(carbinInfo);
                    }
                    if (flag == 2) {
                        firstCabin.add(carbinInfo);
                    }
                }
                if (cabins[i].charAt(0) == 'Y') {
                    flag = 1;
                    if (cabinString.indexOf('J') == -1) {
                        flag = 2;
                    }
                }
                if (cabins[i].charAt(0) == 'J') {
                    flag = 2;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        routedetail.setEconomyCabin(economyCabin);
        routedetail.setBusinessCabin(businessCabin);
        routedetail.setFirstCabin(firstCabin);
        return carbinInfos;
    }

    public static void main(String[] args) {}

    public AllRouteInfo setPrice(AllRouteInfo allRouteInfo) {
        List<Route> Routes = allRouteInfo.getRoutes();
        if (Routes != null) {
            for (int i = 0; i < Routes.size(); i++) {
                String FromCity = "";
                String ToCity = "";
                String TakeoffDate = "";
                String FlightNum = "";
                String SeatClass = "";
                String PassengerType = "";
                String IsBottomPrice = "";
                String IsFSQ = "";
                List<RouteDetailInfo> RouteDetailInfos = Routes.get(i).getRouteDetailInfos();
                for (int j = 0; j < RouteDetailInfos.size(); j++) {
                    RouteDetailInfo routeDetailInfo = RouteDetailInfos.get(j);
                    if (j > 0) {
                        FromCity += ",";
                        ToCity += ",";
                        TakeoffDate += ",";
                        FlightNum += ",";
                        SeatClass += ",";
                    }
                    FromCity += routeDetailInfo.getFromAirport();
                    ToCity += routeDetailInfo.getToAirport();
                    TakeoffDate += routeDetailInfo.getFromDate();
                    FlightNum += routeDetailInfo.getFlightNumber();
                    SeatClass += routeDetailInfo.getCarbinInfos().get(0).getCabin();

                }
                Routes.get(i).setCabin(SeatClass);
                PassengerType += "0";
                IsBottomPrice += "1";
                IsFSQ += "0";
                String result = "-1";
                try {
                    result = cacheprice.getLTQTEgetprice(FromCity, ToCity, TakeoffDate, FlightNum, SeatClass,
                            PassengerType, IsBottomPrice, IsFSQ, officeNumber);
                }
                catch (MalformedURLException e1) {
                    e1.printStackTrace();
                    result = QTEgetprice(FromCity, ToCity, TakeoffDate, FlightNum, SeatClass, PassengerType,
                            IsBottomPrice, IsFSQ);
                }

                if (!result.equals("-1")) {
                    org.dom4j.Document document;
                    try {
                        document = DocumentHelper.parseText(result);
                        org.dom4j.Element root = document.getRootElement();
                        String Is_Success = root.elementText("Is_Success");
                        if (Is_Success.equals("1")) {
                            org.dom4j.Element QteInfo = root.element("QteInfo");
                            String FsiCmd = QteInfo.elementText("FsiCmd");
                            org.dom4j.Element PriceInfo = QteInfo.element("PriceInfo");
                            String ID = PriceInfo.elementText("ID");
                            String FareBasis = PriceInfo.elementText("FareBasis");
                            String Citys = PriceInfo.elementText("Citys");
                            String Fare = PriceInfo.elementText("Fare") == "" ? "0" : PriceInfo.elementText("Fare");
                            String Tax = PriceInfo.elementText("Tax");
                            String XT = PriceInfo.elementText("XT");
                            String Total = PriceInfo.elementText("Total");
                            if (Fare.equals("0")) {
                                Fare = Total;
                            }
                            Routes.get(i).setTotalFare(Double.parseDouble(Fare));
                            String Currency = PriceInfo.elementText("Currency");
                            Routes.get(i).setCurrency(Currency);
                            double TotalTax = Double.parseDouble(Total) - Double.parseDouble(Fare);
                            Routes.get(i).setTotalTax(TotalTax);
                        }
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return allRouteInfo;
    }

    /**
     * QTE获取价格
     * @param FromCity	起飞机场(多航段用逗号分隔)
     * @param ToCity	到达机场
     * @param TakeoffDate	起飞日期(格式yyyy-MM-dd)
     * @param FlightNum	航班号
     * @param SeatClass	舱位
     * @param PassengerType	乘机人类型(0=成人,1=儿童,2=婴儿,3=留学生)
     * @param IsBottomPrice	是否只显示最低价(0=否,1=是)
     * @param IsFSQ	多个价格是否FSQ(0=否,1=是)
     */
    public String QTEgetprice(String FromCity, String ToCity, String TakeoffDate, String FlightNum, String SeatClass,
            String PassengerType, String IsBottomPrice, String IsFSQ) {
        String result = "-1";
        try {
            WriteLog.write("NBE", "kaYn_Internat_QTE_WithOutPNR:0:" + FromCity + "," + ToCity + "," + TakeoffDate + ","
                    + FlightNum + "," + SeatClass + "," + PassengerType + "," + IsBottomPrice + "," + IsFSQ);
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_Internat_QTE_WithOutPNR kaYn_Internat_QTE_WithOutPNR = new ServiceStub.KaYn_Internat_QTE_WithOutPNR();
            kaYn_Internat_QTE_WithOutPNR.setFromCity(FromCity);
            kaYn_Internat_QTE_WithOutPNR.setToCity(ToCity);
            kaYn_Internat_QTE_WithOutPNR.setTakeoffDate(TakeoffDate);
            kaYn_Internat_QTE_WithOutPNR.setFlightNum(FlightNum);
            kaYn_Internat_QTE_WithOutPNR.setSeatClass(SeatClass);
            kaYn_Internat_QTE_WithOutPNR.setPassengerType(PassengerType);
            kaYn_Internat_QTE_WithOutPNR.setIsBottomPrice(IsBottomPrice);
            kaYn_Internat_QTE_WithOutPNR.setIsFSQ(IsFSQ);
            kaYn_Internat_QTE_WithOutPNR.setOffice(officeNumber);
            ServiceStub.KaYn_Internat_QTE_WithOutPNRResponse response = stub
                    .kaYn_Internat_QTE_WithOutPNR(kaYn_Internat_QTE_WithOutPNR);
            //			result = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><Letoing><Is_Success>1</Is_Success><QteInfo><FsiCmd>XS FSI/*HUS HU  7951X30MAY PEK0645 1230MLE0S    767</FsiCmd><PriceInfo><ID>01</ID><FareBasis>MOWCNI</FareBasis><Citys>BJS,MLE</Citys><Fare>4500</Fare><Tax>90CN,37YQ,400YR</Tax><XT></XT><Total>5027</Total><Currency>CNY</Currency><WarnFlag>RB</WarnFlag></PriceInfo></QteInfo></Letoing>";
            result = response.getKaYn_Internat_QTE_WithOutPNRResult();
            WriteLog.write("NBE", "kaYn_Internat_QTE_WithOutPNR:1:" + result);
        }
        catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

}
