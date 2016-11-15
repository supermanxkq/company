package com.ccservice.b2b2c.atom.component;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis2.AxisFault;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import sun.misc.BASE64Decoder;
import client.ServiceStub;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.ISearchFlightService;
import com.ccservice.b2b2c.base.aircompany.Aircompany;
import com.ccservice.b2b2c.base.cabin.Cabin;
import com.ccservice.b2b2c.base.fflight.AllFlight;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.fflight.Route;
import com.ccservice.b2b2c.base.fflight.RouteDetailInfo;
import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.flightmodel.Flightmodel;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;
import com.ccservice.service.IRateService;

public class TicketYSKComponent extends PublicComponent implements ITicketSearchComponent {

    private String username;

    private String password;

    private String ipAddress;

    private String ipAddress2;

    private String officeNumber;

    private String ipbigAddress;

    // 是否是缓存价格数据
    private String iscacheprice;

    // 192.168.16.223
    // 117.88.72.149
    private CacheFlightInfo cacheflight = new CacheFlightInfo();

    private CacheBaseData cachebasedata = new CacheBaseData();

    private CachePriceInfo cacheprice = new CachePriceInfo();

    // 定义一个标示区分调用的是那个接口的机票查询
    private String isinterface;

    private String userid;

    private String printerNo;

    private String linkMobile;

    public String getLinkMobile() {
        return linkMobile;
    }

    public void setLinkMobile(String linkMobile) {
        this.linkMobile = linkMobile;
    }

    public String getIsinterface() {
        return isinterface;
    }

    public void setIsinterface(String isinterface) {
        this.isinterface = isinterface;
    }

    // 用户名
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // 密码
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // IP地址或者接口调用地址
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // Office号
    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public String getIpbigAddress() {
        return ipbigAddress;
    }

    public void setIpbigAddress(String ipbigAddress) {
        this.ipbigAddress = ipbigAddress;
    }

    /**
     * 调用黑屏接口并返回黑屏航班信息(AVOPRN)
     * 
     * @param FlightSearch
     * @return StrReturn Author:HTHY avh/PEKCAN/ 22AUG/ 22AUG(SUN) BJSCAN 1-
     *         *FM2921 DS# C8 YA BS KA LA MA T1 ES HS QS NAYFUO 0830 1100 738 0
     *         E > KN2921 VS WS GS 2 KN2921 DS# C8 JS DS YA BS EA HA LA M1 NS
     *         NAYFUO 0830 1100 738 0 E > RS SS VS TS WS GS QS KS XS US ZS IS 3
     *         CA1321 DS# FA A6 CA DA Z7 R1 YA BS MS HS PEKCAN 0900 1200 777 0^B
     *         E > KS LS QS GS SA NS VS WS TS ES T3 -- * M1S V1S 4 *MU7216 AS#
     *         FA YA BQ EQ HQ LQ MQ NQ RQ KA PEKCAN 0915 1220 321 0^L E > CZ3108
     *         VQ TQ QA WQ T2 -- 5 CZ3108 AS# FA PQ WA YA TQ KQ HQ MQ GQ SQ
     *         PEKCAN 0915 1220 321 0^L E > LQ QQ UA EQ VQ BA XQ NA RQ T2 -- 6
     *         *MU7122 AS# F4 YA BQ EQ HQ LQ MQ NQ RQ KA PEKCAN 1015 1310 321
     *         0^L E > CZ3162 VQ TQ QA WQ T2 -- 7+ CZ3162 AS# F4 P4 WA YA TQ KQ
     *         HQ MQ GQ SQ PEKCAN 1015 1310 321 0^L E > LQ QQ UA EQ VQ BA XQ NA
     *         RQ T2 -- * PLEASE CHECK YI:CZ/TZ144 FOR ET SELF-SERVICE CHECK-IN
     *         KIOS
     */
    public String AVOpen(FlightSearch flightSearch) {
        // 接口地址
        String strUrl = this.ipAddress;
        // 命令参数
        String strCmd = "?cmd=";
        // 城市对
        String strCityPair = flightSearch.getStartAirportCode() + flightSearch.getEndAirportCode();
        // 出发日期
        String strDateCmd = ChangeDateMode(flightSearch.getFromDate().toString());
        if (flightSearch.getFromTime() != null && !flightSearch.getFromTime().equals("")) {
            strDateCmd += "/" + flightSearch.getFromTime();
        }
        // 航空公司二字码
        String strCompany = "";
        String strReutrnTemp = "";
        if (flightSearch.getAirCompanyCode() == null) {
            strCompany = "";
        }
        else {
            strCompany = "/" + flightSearch.getAirCompanyCode();
        }
        // 翻页命令
        String strPage = "$PN$PN$PN$PN$PN$PN";
        if (flightSearch.getStartAirportCode().equals("PEK") || flightSearch.getStartAirportCode().equals("SHA")
                || flightSearch.getStartAirportCode().equals("PVG") || flightSearch.getEndAirportCode().equals("PEK")
                || flightSearch.getEndAirportCode().equals("SHA") || flightSearch.getEndAirportCode().equals("PVG")) {
            strPage = "$PN$PN$PN$PN$PN$PN$PN$PN";
        }

        // 黑屏查询指令：avh/peksha/20OCT
        strCmd += getBASE64("avh/" + strCityPair + "/" + strDateCmd.toUpperCase() + strCompany + strPage);

        String strReturn = "";
        // 接口调用地址和命令参数
        String totalurl = strUrl + strCmd;
        java.io.InputStream in = null;
        try {
            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            in = conn.getInputStream();
            org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
            org.jdom.Document doc = build.build(in);
            org.jdom.Element data = doc.getRootElement();
            List<Element> muticmdlist = data.getChildren("CMDLIST");
            System.out.println(muticmdlist.size());
            int i = 0;
            for (Element cmdlist : muticmdlist) {
                i++;
                String status = cmdlist.getChildTextTrim("STATUS");
                // 返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
                if (status != null && status.equals("0")) {
                    strReturn = "0," + cmdlist.getChildTextTrim("MESSAGE");
                }
                else {
                    if (i == 1) {
                        strReturn = "1," + cmdlist.getChildTextTrim("RESPONSE");
                    }
                    else {
                        strReturn += "\r\n" + cmdlist.getChildTextTrim("RESPONSE");
                    }
                }
            }
            in.close();
            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    public static String getDateString(String depCityCode, String arrCityCode, String depDate, String AirCompName) {
        if (AirCompName != null && AirCompName.length() > 0) {

            System.out.println("code==" + AirCompName);
        }
        else {

            AirCompName = "";
        }
        // System.out.println("开始查询航班：");
        // System.out.println("出发城市："+depCityCode);
        // System.out.println("到达城市："+arrCityCode);
        // System.out.println("出发日期："+depDate);
        // System.out.println("航空公司："+AirCompName);
        // if()
        int ran = (int) (Math.random() * 99999 + 1);
        String run = ran + "";

        String urltemp = "http://fd2.tripnew.com/WebService/AV.aspx?BeginCity=" + depCityCode + "&EndCity="
                + arrCityCode + "&BeginDate=" + depDate + "&AirLine=" + AirCompName
                + "&Key=3E474CC0A116E009&DataType=0";

        URL url;
        // System.out.println("urltemp=="+urltemp);
        try {
            url = new URL(urltemp);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            String sCurrentLine;
            String sTotalString;
            sCurrentLine = "";
            sTotalString = "";
            InputStream l_urlStream;
            l_urlStream = connection.getInputStream();
            BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream, "GB2312"));
            while ((sCurrentLine = l_reader.readLine()) != null) {
                sTotalString += sCurrentLine;
            }
            return sTotalString;
        }
        catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 国内机票查询
     */
    public List<FlightInfo> findAllFlightinfo(FlightSearch flightSearch) {

        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        System.out.println("isinterface:" + isinterface);
        // 类型为1调用新的查询接口 51book接口
        if (isinterface.equals("1")) {
            flightalltemp = searchFlightByNewInteface(flightSearch);
            if (flightalltemp.size() == 0) {
                flightalltemp = searchFlightBybbxInterface(flightSearch);
            }
        }
        else if (isinterface.equals("0")) {
            flightalltemp = searchFlightBybbxInterface(flightSearch);
            if (flightalltemp.size() == 0) {
                flightalltemp = searchFlightByNewInteface(flightSearch);
            }
        }
        else if (isinterface.equals("2")) {
            TicketPIDManComponent pidcomponent = new TicketPIDManComponent();
            flightalltemp = pidcomponent.findAllFlightinfo(flightSearch);
        }
        else if (isinterface.equals("3")) {
            flightalltemp = searchFlightByNewInteface(flightSearch);
        }
        else if (isinterface.equals("4")) {
            flightalltemp = searchFlightBybbxInterface(flightSearch);
        }
        else if (isinterface.equals("5")) {
            try {
                flightalltemp = searchFlightByFiveoneBookInterface(flightSearch);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (flightalltemp.size() == 0) {
                // flightalltemp = searchFlightBybbxInterface(flightSearch);
                flightalltemp = searchFlightByNewInteface(flightSearch);
            }
        }
        else if (isinterface.equals("6")) {
            flightalltemp = getFormatValuesV2tByKABEInterface(flightSearch);
        }
        try {
            if ("1".equals(getSysconfigString("useQunarData"))) {
                flightalltemp = addQunarFlightData(flightalltemp, flightSearch);
            }
            // if ("1".equals(getSysconfigString("useCtripData"))) {
            // if (flightalltemp.size() > 0) {
            // flightalltemp = addCtripFlightData(flightalltemp,
            // flightSearch);
            // }
            // }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return flightalltemp;
    }

    private List<FlightInfo> getFormatValuesV2tByKABEInterface(FlightSearch flightSearch) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dfyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        String url = "http://122.115.62.70/cn_interface/airtickgj.jsp?test=";
        url += "getFormatValuesV2?";
        url += "u=" + "6ed3fc98f2da5089a6144598a6a320e3"; // 用户ID
        url += "&sc=" + flightSearch.getStartAirportCode();// 出发城市
        url += "&ec=" + flightSearch.getEndAirportCode();// 到达城市
        url += "&sd=" + flightSearch.getFromDate();// 出发日期
        url += "&ed=" + flightSearch.getBackDate();// 返回日期
        url += "&showround=" + "";// 预留字段
        url += "&f=" + flightSearch.getAirCompanyCode();// 类型 不清楚 规定为"Y"
        url += "&t=" + "";// 1为单程，2为往返
        try {

            String result = SendPostandGet.submitPost(url, "", "GB2312").toString();
            System.out.println(result);
            String strReturn = result.replace("[\r]", "").replace("[\n]", "");
            String[] strArr = strReturn.split("[\r]");
            String strTemp = "";
            for (int i = 0; i < strArr.length; i++) {
                if (!strArr[i].equals("")) {
                    strTemp += strArr[i].replaceAll("[\n]", "").trim();
                }
            }
            SAXBuilder build = new SAXBuilder();
            System.out.println(strTemp);
            Document doc = build.build(new StringReader(strTemp.trim()));
            Element element = doc.getRootElement();
            // 获得xml文件中的字符串
            String returnstrs = element.getValue();
            // 获取查询的结果的总记录数
            String returnstr = returnstrs.replace("$", ",");
            // 获得！分割的数组
            String[] str = returnstr.split("!");
            // 如果查询成功
            if (str[0].equals("0")) {
                // 获取成功后的信息
                String flag = str[0];
                // 获得出发城市
                String fromCity = str[1];
                // 到达城市
                String toCity = str[2];
                // 出发日期
                String fromDate = str[3];
                // 判断是否有航班
                if (str.length > 4 && !str[4].equals("")) {
                    // 获取不同航班的没组数据
                    String[] ticketLength = str[4].split(",");
                    // 循环获取每组数据
                    for (int i = 0; i < ticketLength.length; i++) {
                        FlightInfo flightInfo = new FlightInfo();
                        String messaage = ticketLength[i];
                        String[] ticketstr = messaage.split("@");
                        // 出发城市
                        String fcit = ticketstr[0];
                        flightInfo.setStartAirport(fcit);
                        // 到达城市
                        String tcity = ticketstr[1];
                        flightInfo.setEndAirport(tcity);
                        // 出发日期
                        String fdate = ticketstr[2];
                        flightInfo.setDepartTime(new Timestamp(dfyyyyMMdd.parse(fdate).getTime()));
                        // 航空公司二字段
                        String flycomputer = ticketstr[4];
                        flightInfo.setAirCompany(flycomputer);
                        flightInfo.setAirCompanyName(flycomputer);
                        // 航班号
                        String flyno = ticketstr[3];
                        flightInfo.setAirline(flycomputer + flyno);
                        // 机型
                        String flytype = ticketstr[5];
                        flightInfo.setAirplaneType(flytype);
                        // 出发时间
                        String fromtime = ticketstr[6];
                        Long fl = df.parse(fdate + " " + fromtime).getTime();
                        flightInfo.setDepartTime(new Timestamp(fl));
                        // 到达时间
                        String totime = ticketstr[7];
                        Long tl = df.parse(fdate + " " + totime).getTime();
                        if (fl > tl) {
                            flightInfo.setArriveTime(new Timestamp(tl + 3600 * 24 * 1000));
                        }
                        else {
                            flightInfo.setArriveTime(new Timestamp(tl));
                        }

                        // 是否经停
                        Long isstop = Long.parseLong(ticketstr[8]);
                        if (isstop == 1) {
                            flightInfo.setStop(true);
                        }
                        else {
                            flightInfo.setStop(false);
                        }
                        // 是否有餐
                        if (ticketstr[9] != "") {
                            // Long isfood=Long.parseLong(ticketstr[9]);
                        }
                        // 是否中转
                        Long ischance = Long.parseLong(ticketstr[10]);
                        // 是否电子客票
                        Long isticket = Long.parseLong(ticketstr[11]);
                        // 全价
                        String allprice = ticketstr[12];
                        flightInfo.setYPrice(Float.valueOf(allprice));
                        // 机建费
                        String jprice = ticketstr[13];
                        flightInfo.setAirportFee(Float.valueOf(jprice));
                        // 燃油费
                        String ryprice = ticketstr[14];
                        flightInfo.setFuelFee(Float.valueOf(ryprice));

                        CarbinInfo lowCarbin = new CarbinInfo();
                        // 读取这些信息*折扣名称*舱位数量*舱位代码*采购航空公司
                        String[] zkstr = ticketstr[15].split("[*]");
                        // 票面价
                        String ticketprice = zkstr[0];
                        lowCarbin.setPrice(Float.valueOf(ticketprice));
                        // 折扣名称
                        String zkname = zkstr[1];
                        if ("全价舱".equals(zkname)) {
                            lowCarbin.setDiscount(100f);
                        }
                        else if ("头等舱".equals(zkname)) {
                            lowCarbin.setDiscount(100f);
                        }
                        else {
                            lowCarbin.setDiscount(Float.valueOf(zkname.replace("折", "")) * 10);
                        }
                        // 舱位数量
                        Long number = Long.parseLong(zkstr[2]);
                        lowCarbin.setSeatNum(number.toString());
                        // 舱位代码
                        String flycode = zkstr[3];
                        lowCarbin.setCabin(flycode);
                        // 采购航空公司
                        // String flycom=zkstr[4];
                        // 其它舱位
                        if (ticketstr[1] != "") {
                            String othfly = ticketstr[16];
                        }
                        // 航班信息验证串
                        String checkmess = ticketstr[17];

                        flightInfo.setLowCarbin(lowCarbin);
                        // 通过舱位接口获得航班的所有舱位
                        // List<CarbinInfo> carbinInfos = new
                        // ArrayList<CarbinInfo>();
                        List<CarbinInfo> carbinInfos = this.otherAirSearch(fcit, tcity, fdate, flycomputer + flyno,
                                checkmess);
                        if (carbinInfos.size() > 0) {
                            // 获取查询的结果的总记录数
                            flightInfo.setCarbins(carbinInfos);
                        }
                        flightalltemp.add(flightInfo);
                    }
                }
            }
            // 查询失败
            else {
                String message = str[0];
                System.out.println(message + "查询航班失败");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return flightalltemp;
    }

    /**
     * 查询其他舱位接口
     * 
     * @param args
     * @throws IOException
     * @throws JDOMException
     */
    public List<CarbinInfo> otherAirSearch(String strFromCity, String strToCity, String strFromDate, String strFlyno,
            String strflymess) throws Exception {
        List<CarbinInfo> carbinInfos = new ArrayList<CarbinInfo>();
        // 保存路径和需要的参数
        String totalurl = "http://122.115.62.70/cn_interface/airtickgj.jsp?test=";
        totalurl += "getFormatSubValues?";
        totalurl += "u=" + "6ed3fc98f2da5089a6144598a6a320e3"; // 请求ID
        totalurl += "&sc=" + strFromCity;// 出发城市
        totalurl += "&ec=" + strToCity;// 到达城市
        totalurl += "&sd=" + strFromDate;// 出发日期
        totalurl += "&fn=" + strFlyno;// 航班号
        totalurl += "&v=" + strflymess;// 航班信息验证串
        String returnstr = null;
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
            // 获得xml文件中的字符串
            returnstr = element.getValue();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String data = returnstr;
        data = data.split("!")[data.split("!").length - 1];
        String[] flightInfo = data.split("@");
        // 全价
        String allpricesub = flightInfo[6];
        // 机建费
        String jpricesub = flightInfo[7];
        // 燃油费
        String rypricesub = flightInfo[8];
        data = flightInfo[flightInfo.length - 1];
        String[] carbinInfoStrings = data.split("-");
        // 获取舱位代码#舱位数量#票面价
        for (int j = 0; j < carbinInfoStrings.length; j++) {
            CarbinInfo carbinInfo = new CarbinInfo();
            String[] infofly = carbinInfoStrings[j].split("#");
            // 获取舱位信息
            String flycodesub = infofly[0];
            // 舱位数量
            Long numbersub = Long.parseLong(infofly[1]);
            // 获得票面价
            String ticketpricesub = infofly[2];
            carbinInfo.setCabin(flycodesub);
            carbinInfo.setSeatNum(numbersub.toString());
            carbinInfo.setPrice(Float.valueOf(ticketpricesub));
            float discount = Float.valueOf(ticketpricesub) / Float.valueOf(allpricesub) * 100;
            carbinInfo.setDiscount(Float.valueOf(formatDis(discount)));
            carbinInfos.add(carbinInfo);
        }
        return carbinInfos;
    }

    private List<FlightInfo> searchFlightByFiveoneBookInterface(FlightSearch flightSearch) {
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        String url = getSysconfigString("51flighturl") + "/ticket_inter/service/";
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            IRateService servier11 = (IRateService) factory.create(IRateService.class,
                    url + IRateService.class.getSimpleName());
            flightalltemp = servier11.getAvailableFlightWithPriceAndCommision(flightSearch.getStartAirportCode(),
                    flightSearch.getEndAirportCode(), flightSearch.getFromDate(), flightSearch.getAirCompanyCode(), 1,
                    1, 0, 0);
            System.out.println("航班总条数：" + flightalltemp.size());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return flightalltemp;
        }
        for (FlightInfo flighInfo : flightalltemp) {
            try {
                flighInfo.setStartAirportName(cachebasedata.getCityAirport(flighInfo.getStartAirport())
                        .getAirportname());// 起飞机场名称
                flighInfo.setEndAirportName(cachebasedata.getCityAirport(flighInfo.getEndAirport()).getAirportname());// 到达机场名称
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("取得机场名称出错：异常:" + ex.getMessage());
            }
            // 机型描述
            try {
                List<Flightmodel> listModel = cachebasedata.getFlightModel("ALLFlightModelList");
                if (listModel != null && listModel.size() > 0) {
                    for (Flightmodel model : listModel) {
                        if (model.getModelnum().equals(flighInfo.getAirplaneType())) {
                            String strTypeDesc = model.getModeldesc().replaceAll("\r\n", "").replaceAll("\r", "")
                                    .replaceAll("\n", "")
                                    + "#" + model.getPicpath().trim();
                            flighInfo.setAirplaneTypeDesc(strTypeDesc);
                        }
                    }
                }
                else {
                    flighInfo.setAirplaneTypeDesc(flighInfo.getAirplaneType());
                }
            }
            catch (Exception ex) {
                System.out.println("取得机型信息出错，异常：" + ex.getMessage());
            }
        }
        return flightalltemp;
    }

    /**
     * 通过51book接口调用机票查询接口
     * 
     * @param list
     * @return
     */
    public List searchFlightByNewInteface(FlightSearch flightSearch) {
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        // 苍南51book查询原始接口
        // flightalltemp=Server.getInstance().getRateService().SeachFlight(flightSearch.getStartAirportCode(),
        // flightSearch.getEndAirportCode(), flightSearch.getAirCompanyCode(),
        // flightSearch.getFromDate(), "");
        // flightalltemp=Server.getInstance().getSearchFiveoneFlightService().SeachFiveoneFlight(flightSearch.getStartAirportCode(),
        // flightSearch.getEndAirportCode(), flightSearch.getAirCompanyCode(),
        // flightSearch.getFromDate(), "");
        String url = "http://210.83.81.120:8088/cn_interface/service/";

        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            ISearchFlightService servier = (ISearchFlightService) factory.create(ISearchFlightService.class, url
                    + ISearchFlightService.class.getSimpleName());

            flightalltemp = servier.SeachFiveoneFlight(flightSearch.getStartAirportCode(),
                    flightSearch.getEndAirportCode(), flightSearch.getAirCompanyCode(), flightSearch.getFromDate(), "");
            System.out.println("航班总条数：" + flightalltemp.size());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        for (FlightInfo flighInfo : flightalltemp) {
            try {
                flighInfo.setStartAirportName(cachebasedata.getCityAirport(flighInfo.getStartAirport())
                        .getAirportname());// 起飞机场名称
                flighInfo.setEndAirportName(cachebasedata.getCityAirport(flighInfo.getEndAirport()).getAirportname());// 到达机场名称
            }
            catch (Exception ex) {
                System.out.println("取得机场名称出错：异常:" + ex.getMessage());
            }
            // 机型描述
            try {
                List<Flightmodel> listModel = cachebasedata.getFlightModel("ALLFlightModelList");
                if (listModel != null && listModel.size() > 0) {
                    for (Flightmodel model : listModel) {
                        if (model.getModelnum().equals(flighInfo.getAirplaneType())) {
                            String strTypeDesc = model.getModeldesc() + "#" + model.getPicpath();
                            flighInfo.setAirplaneTypeDesc(strTypeDesc);
                        }
                    }
                }
                else {
                    flighInfo.setAirplaneTypeDesc(flighInfo.getAirplaneType());
                }
            }
            catch (Exception ex) {
                System.out.println("取得机型信息出错，异常：" + ex.getMessage());
            }
        }
        return flightalltemp;
    }

    /**
     * 使用网创接口查询机票
     * 
     * @param list
     * @return
     */
    public List searchFlightBybbxInterface(FlightSearch flightSearch) {
        // 定义返回List
        List<FlightInfo> listFlightInfoAll = new ArrayList<FlightInfo>();
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        try {
            String sub = getDateString(flightSearch.getStartAirportCode(), flightSearch.getEndAirportCode(),
                    flightSearch.getFromDate(), flightSearch.getAirCompanyCode());
            // System.out.println("sub==" + sub);
            if (sub.length() > 45) {
                org.dom4j.Document document = DocumentHelper.parseText(sub);
                org.dom4j.Element root = document.getRootElement();
                if (1 == 1) {// 有数据
                    List<org.dom4j.Element> listflights = root.elements("FlightInfo");
                    ;// 获取航班list
                    CarbinInfo cabinInfo = null;
                    if (listflights.size() > 0) {// 如果航班list>0 有航班
                        for (int a = 0; a < listflights.size(); a++) {// 循环航班
                            String company = listflights.get(a).elementText("AirLine");// 航空公司代码
                            String line_number = listflights.get(a).elementText("FlightNo");// 航航班号
                            if (1 == 1) {// 如果是春秋航空得..跳出循环

                                List<org.dom4j.Element> listcabs = listflights.get(a).elements("Cabin");// 获取一个航班得仓位list
                                CarbinInfo lowCabinInfo = new CarbinInfo();// 第一个显示得特价得类
                                FlightInfo flighInfo = new FlightInfo();
                                String com = line_number.substring(0, 2);
                                flighInfo.setAirCompany(com);// 航空公司二字码
                                flighInfo.setAirCompanyName(company);// 航空公司名字
                                flighInfo.setAirline(line_number);// 航班号
                                flighInfo.setAirplaneType(listflights.get(a).elementText("Plane"));// 机型
                                flighInfo.setAirportFee(Integer.parseInt(listflights.get(a).elementText("Tax")));// 基建
                                flighInfo.setFuelFee(Integer.parseInt(listflights.get(a).elementText("AduOil")));// 燃油费
                                flighInfo.setStartAirport(listflights.get(a).elementText("BeginCity"));// 起飞城市三字码
                                flighInfo.setEndAirport(listflights.get(a).elementText("EndCity"));// 到达城市三字码
                                flighInfo.setOffPointAT(listflights.get(a).elementText("BeginTerm"));
                                flighInfo.setBorderPointAT(listflights.get(a).elementText("EndTerm"));
                                String strFlightDate = listflights.get(a).elementText("BeginTime").replace(":", "");

                                String strArriveDate = listflights.get(a).elementText("EndTime").replace(":", "");
                                // 出发时间
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmm");
                                Date startDate = dateFormat.parse(flightSearch.getFromDate() + " " + strFlightDate);
                                flighInfo.setDepartTime(new Timestamp(startDate.getTime()));// 起飞时间
                                // 到达时间
                                Date arriveDate = dateFormat.parse(flightSearch.getFromDate() + " " + strArriveDate);
                                flighInfo.setArriveTime(new Timestamp(arriveDate.getTime()));// 降落时间

                                try {
                                    flighInfo.setStartAirportName(cachebasedata.getCityAirport(
                                            listflights.get(a).elementText("BeginCity")).getAirportname());// 起飞机场名称
                                    flighInfo.setEndAirportName(cachebasedata.getCityAirport(
                                            listflights.get(a).elementText("EndCity")).getAirportname());// 到达机场名称
                                }
                                catch (Exception ex) {
                                    System.out.println("取得机场名称出错：异常:" + ex.getMessage());
                                }
                                // 机型描述
                                try {
                                    List<Flightmodel> listModel = cachebasedata.getFlightModel("ALLFlightModelList");
                                    if (listModel != null && listModel.size() > 0) {
                                        for (Flightmodel model : listModel) {
                                            if (model.getModelnum().equals(flighInfo.getAirplaneType())) {
                                                String strTypeDesc = model.getModeldesc() + "#" + model.getPicpath();
                                                flighInfo.setAirplaneTypeDesc(strTypeDesc);
                                            }
                                        }
                                    }
                                    else {
                                        flighInfo.setAirplaneTypeDesc(flighInfo.getAirplaneType());
                                    }
                                }
                                catch (Exception ex) {
                                    System.out.println("取得机型信息出错，异常：" + ex.getMessage());
                                }
                                List<CarbinInfo> listCabinAll = new ArrayList<CarbinInfo>();

                                Float YPrice = 0f;
                                YPrice = Float.parseFloat(listflights.get(a).elementText("PriceY"));
                                flighInfo.setYPrice(YPrice);
                                if (listcabs.size() > 0) {// 如果仓位list>0 有仓位
                                    for (int b = 0; b < listcabs.size(); b++) {// 循环仓位
                                        if (Integer.parseInt(listcabs.get(b).elementText("Num")) > 0
                                                && Integer.parseInt(listcabs.get(b).elementText("Price")) > 0) {// 只有A活着数字
                                            // 有位置得才保留..其他干掉

                                            String zhekou = "";

                                            // String
                                            // zhekou=listcabs.get(b).attributeValue("d");//折扣
                                            String price = listcabs.get(b).elementText("Price");// 价格

                                            if (Double.parseDouble(price) > Double.parseDouble(YPrice + "")) {// 票面价>Y仓价...全价票

                                                zhekou = "150";
                                            }
                                            else if (Double.parseDouble(price) == Double.parseDouble(YPrice + "")) {

                                                zhekou = "100";
                                            }
                                            else {

                                                Double z = Double.parseDouble(price) / Double.parseDouble(YPrice + "");

                                                BigDecimal big = new BigDecimal(z);
                                                double f1 = big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                                                zhekou = f1 * 100 + "";

                                            }
                                            // 判断过滤
                                            // List<Limitcabin>list=Server.getInstance().getAirService().findAllLimitcabin("
                                            // WHERE 1=1 AND
                                            // "+Limitcabin.COL_name+"
                                            // ='"+com+"' AND
                                            // "+Limitcabin.COL_cabin+"
                                            // ='"+listcabs.get(b).attributeValue("Code")+"'",
                                            // " ORDER BY ID DESC", -1, 0);
                                            if (false) {// 如果list>0，说明有过滤...过滤掉

                                                System.out.println("该仓位被过滤===航空公司为==" + com + "----仓位为=="
                                                        + listcabs.get(b).elementText("Code"));
                                            }
                                            else {
                                                CarbinInfo cabin = new CarbinInfo();
                                                String zuoweishu = listcabs.get(b).elementText("Num");
                                                String cabintypename = listcabs.get(b).elementText("Class");
                                                if (cabintypename.indexOf('[') > 0) {
                                                    cabin.setCabintypename(cabintypename.split("\\[")[0]);
                                                }
                                                cabin.setSeatNum(zuoweishu);// 座位数

                                                cabin.setCabin(listcabs.get(b).elementText("Code"));// 仓位吗

                                                if (listcabs.get(b).elementText("Price").equals("-1")) {// 价格为0,特价
                                                    System.out
                                                            .println("有特价!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                                    cabin.setDiscount(00f);// 仓位折扣
                                                    cabin.setPrice(00f);// 仓位价格
                                                    if (flightSearch.getFromTime() != null
                                                            && flightSearch.getFromTime().equals("1")) {
                                                        listCabinAll.add(cabin);
                                                    }

                                                }
                                                else {

                                                    cabin.setDiscount(Float.parseFloat(zhekou + ""));// 仓位折扣
                                                    cabin.setPrice(Float.parseFloat(listcabs.get(b)
                                                            .elementText("Price")));// 仓位价格
                                                    listCabinAll.add(cabin);
                                                }
                                            }

                                        }

                                    }
                                }
                                else {// 无仓位
                                }
                                /**
                                 * *********************Created by sunbin
                                 * Begin********************************
                                 */
                                // 处理海航4折机票未显示价格问题
                                try {
                                    for (int j = 0; j < listCabinAll.size(); j++) {
                                        if (listCabinAll.get(j).getPrice() == 0) {
                                            // 如果价格为0的舱位，使用舱位表折扣数据进行价格匹配
                                            String where = " where " + Cabin.COL_aircompanycode + "='"
                                                    + flighInfo.getAirCompany() + "' and " + Cabin.COL_cabincode + "='"
                                                    + listCabinAll.get(j).getCabin() + "'";
                                            List<Cabin> listcabin = Server.getInstance().getAirService()
                                                    .findAllCabin(where, "", -1, 0);
                                            if (listcabin.size() > 0) {
                                                Cabin cabin = listcabin.get(0);
                                                // 数据库折扣值
                                                int intdiscount = cabin.getDiscount();
                                                // 实际折扣值
                                                float fdiscount = Float.parseFloat(intdiscount + "");
                                                // 全价价格
                                                Float fprice = flighInfo.getYPrice() * fdiscount / 1000;
                                                int intprice = fprice.intValue() * 10;

                                                float fcabinprice = Float.parseFloat(intprice + "");
                                                listCabinAll.get(j).setPrice(fcabinprice);
                                                listCabinAll.get(j).setDiscount(fdiscount);

                                            }
                                        }
                                    }

                                }
                                catch (Exception ex) {
                                    System.out.println("处理四折机票出现异常:" + ex.getMessage());
                                }
                                // 处理海航4折机票未显示价格问题
                                /**
                                 * **********************Created by sunbin
                                 * End*******************************
                                 */
                                // 循环list,排序最低
                                int cabinIndex = 0;
                                int newcabinindex = 0;
                                float fcabtemp = 0;
                                try {
                                    fcabtemp = listCabinAll.get(0).getPrice();
                                }
                                catch (Exception ex) {
                                    fcabtemp = 0;
                                }
                                for (int i = 1; i < listCabinAll.size(); i++) {
                                    if (listCabinAll.get(i).getPrice() != 0) {
                                        if (fcabtemp > listCabinAll.get(i).getPrice()) {
                                            fcabtemp = listCabinAll.get(i).getPrice();
                                            cabinIndex = i;

                                        }
                                    }
                                }
                                // 设置最低折扣
                                if (listCabinAll.size() > 0 && listCabinAll.size() >= cabinIndex) {
                                    CarbinInfo tempCabinInfo = (CarbinInfo) listCabinAll.get(cabinIndex);
                                    if (tempCabinInfo.getCabin() != null) {
                                        lowCabinInfo.setCabin(tempCabinInfo.getCabin());
                                    }
                                    if (tempCabinInfo.getRatevalue() != null) {
                                        lowCabinInfo.setRatevalue(tempCabinInfo.getRatevalue());
                                    }
                                    if (tempCabinInfo.getCabinRemark() != null) {
                                        lowCabinInfo.setCabinRemark(tempCabinInfo.getCabinRemark());
                                    }
                                    else {
                                        lowCabinInfo.setCabinRemark("暂无");
                                    }
                                    // 更改规定
                                    if (tempCabinInfo.getUpdaterule() != null) {
                                        lowCabinInfo.setUpdaterule(tempCabinInfo.getUpdaterule());
                                    }
                                    else {
                                        lowCabinInfo.setUpdaterule("暂无");
                                    }
                                    // 退票规定
                                    if (tempCabinInfo.getRefundrule() != null) {
                                        lowCabinInfo.setRefundrule(tempCabinInfo.getRefundrule());
                                    }
                                    else {
                                        lowCabinInfo.setRefundrule("暂无");
                                    }
                                    // 签转规定
                                    if (tempCabinInfo.getChangerule() != null) {
                                        lowCabinInfo.setChangerule(tempCabinInfo.getChangerule());
                                    }
                                    else {
                                        lowCabinInfo.setRefundrule("暂无");
                                    }
                                    if (tempCabinInfo.getDiscount() != null) {
                                        lowCabinInfo.setDiscount(tempCabinInfo.getDiscount());
                                    }
                                    if (tempCabinInfo.getLevel() != null) {
                                        lowCabinInfo.setLevel(tempCabinInfo.getLevel());
                                    }
                                    else {
                                        lowCabinInfo.setLevel(1);
                                    }
                                    if (tempCabinInfo.getPrice() != null) {
                                        lowCabinInfo.setPrice(tempCabinInfo.getPrice());
                                    }
                                    else {
                                        lowCabinInfo.setPrice(0f);
                                    }
                                    if (tempCabinInfo.getSeatNum() != null) {
                                        lowCabinInfo.setSeatNum(tempCabinInfo.getSeatNum());
                                    }
                                    else {
                                        lowCabinInfo.setSeatNum("0");
                                    }
                                    if (tempCabinInfo.getCabintypename() != null) {
                                        lowCabinInfo.setCabintypename(tempCabinInfo.getCabintypename());
                                    }
                                    else {
                                        lowCabinInfo.setCabintypename("经济舱");
                                    }
                                    if (tempCabinInfo.isSpecial()) {
                                        lowCabinInfo.setSpecial(true);
                                    }
                                    else {
                                        lowCabinInfo.setSpecial(false);
                                    }

                                    flighInfo.setLowCarbin(lowCabinInfo);

                                    flighInfo.setCarbins(listCabinAll);
                                    listFlightInfoAll.add(flighInfo);
                                }
                                else {
                                    continue;
                                }

                                flighInfo.setLowCarbin(lowCabinInfo);// 特价第一个显示得
                                // 循环结束
                                Collections.sort(listCabinAll, new Comparator() {
                                    public int compare(Object a, Object b) {
                                        Float one = ((CarbinInfo) a).getDiscount();
                                        Float two = ((CarbinInfo) b).getDiscount();
                                        return (int) (one - two);
                                    }
                                });
                                flighInfo.setCarbins(listCabinAll);
                                listFlightInfoAll.add(flighInfo);
                            }
                        }
                        System.out.println("航班总个数==" + listFlightInfoAll.size());

                    }
                    else {
                        listFlightInfoAll = null;
                    }

                }
                else {// 没有数据
                    listFlightInfoAll = null;
                }
            }
            else {
                listFlightInfoAll = null;
            }
            if (listFlightInfoAll != null) {
                for (int q = 0; q < listFlightInfoAll.size(); q++) {
                    // 如果PN分页查询出的航班信息有重复的过滤掉
                    if (!flightalltemp.contains(listFlightInfoAll.get(q))) {
                        flightalltemp.add(listFlightInfoAll.get(q));
                    }
                }
            }
        }
        catch (Exception e) {
            flightalltemp = null;
        }
        SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = tempDate.format(new java.util.Date());
        System.out.println("解析航班数据并返回List时间：" + datetime);
        return flightalltemp;

    }

    public static List<FlightInfo> removeDuplicateWithOrder(List<FlightInfo> list) {

        for (FlightInfo fi : list) {

        }

        return list;
    }

    /**
     * 根据FD指令信息得到城市对的公里数
     * 
     * @param strFDinfo
     *            FD价格信息
     * @param strCityPair
     *            城市对
     * @return 公里数 >PFDXIYINC/CA b FD:XIYINC/25JUL11/CA /CNY /TPM 560 / 01 CA/F /
     *         900.00= 1800.00/F/F/ / . /01APR11 /FC1C
     */
    public int GetFlightMiles(String strCityPair, String strFDinfo) {
        int intmiles = 0;
        String strMiles = "0";
        Pattern patmiles = Pattern.compile("\\n");
        String[] strFDArr = patmiles.split(strFDinfo);
        try {
            for (int i = 0; i < strFDArr.length; i++) {
                if (strFDArr[i].indexOf("FD:" + strCityPair + "/") >= 0) {
                    String[] strArr = strFDArr[i].split("/");
                    for (int j = 0; j < strArr.length; j++) {
                        if (strArr[j].indexOf("TPM") >= 0) {
                            String[] strArr2 = strArr[j].split("\\s");
                            for (int w = 0; w < strArr2.length; w++) {
                                if (IsNumberStr(strArr2[w])) {
                                    intmiles = Integer.parseInt(strArr2[w]);
                                }
                            }

                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            intmiles = 0;
        }
        return intmiles;
    }

    public boolean IsNumberStr(String str) {
        boolean isNumber = false;
        for (int i = 0; i < str.length(); i++) {
            // 循环遍历字符串
            if (Character.isDigit(str.charAt(i))) {
                // 用char包装类中的判断数字的方法判断每一个字符
                isNumber = true;
            }
        }
        return isNumber;
    }

    /**
     * 将money格式化为类似于2,243,234.00的格式
     * 
     * @param money
     * @return
     */
    public String formatMoney(Float money) {
        DecimalFormat format = null;
        format = (DecimalFormat) NumberFormat.getInstance();
        format.applyPattern("#,##0.0");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Float.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    /**
     * 通用命令查询接口方法
     * 
     * @param strCmd
     *            黑屏命令
     * @return StrReturn 黑屏返回结果 Author:HTHY
     */
    public String commandFunction(String strcmd, String strPageinfo) {
        // 接口地址
        String strUrl = ipbigAddress;
        // 命令参数
        String strCmd = "?cmd=";

        // 翻页命令
        String strPage = strPageinfo;
        // 黑屏查询指令：
        strCmd += getBASE64(strcmd);
        // 清楚缓存数据
        String strIG = "$IG";

        String strReturn = "";
        // 接口调用地址和命令参数
        String totalurl = strUrl + strCmd + strPage + strIG;
        java.io.InputStream in = null;
        try {
            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            in = conn.getInputStream();
            org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
            org.jdom.Document doc = build.build(in);
            org.jdom.Element data = doc.getRootElement();
            List<Element> muticmdlist = data.getChildren("CMDLIST");
            int i = 0;
            for (Element cmdlist : muticmdlist) {
                i++;
                String status = cmdlist.getChildTextTrim("STATUS");
                // 返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
                if (status != null && status.equals("0")) {
                    strReturn = cmdlist.getChildTextTrim("MESSAGE");
                }
                else {
                    if (i == 1) {
                        strReturn = cmdlist.getChildTextTrim("RESPONSE");
                    }
                    else {
                        strReturn += "\r\n" + cmdlist.getChildTextTrim("RESPONSE");
                    }
                }
            }
            in.close();
            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 通用命令查询接口方法
     * 
     * @param strCmd
     *            黑屏命令
     * @return StrReturn 黑屏返回结果 Author:HTHY
     */
    public String commandFunction2(String strcmd, String strPageinfo, String strIG) {
        String strReturn = "";
        try {
            String identityXml = "<Identity_1_0><UserId>"
                    + userid
                    + "</UserId><UserPassword></UserPassword><UserIdLv2></UserIdLv2><UserType></UserType><User></User></Identity_1_0>";
            String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            requestXml += "<ABE_SendCommand_1_0>";
            requestXml += "<CommandString>" + strcmd + "</CommandString>";
            requestXml += "</ABE_SendCommand_1_0>";
            WriteLog.write("易思凯", "ABE_SendCommand接口，向接口中传入的数据[identityXml:" + identityXml + "---requestXml:"
                    + requestXml + "]");
            com.ysk.interticket.ServiceStub.ABESubmit aBESubmit = new com.ysk.interticket.ServiceStub.ABESubmit();
            aBESubmit.setIdentity(identityXml);
            aBESubmit.setRequest(requestXml);
            com.ysk.interticket.ServiceStub stub = new com.ysk.interticket.ServiceStub();
            com.ysk.interticket.ServiceStub.ABESubmitResponse response = stub.aBESubmit(aBESubmit);
            String result = response.getABESubmitResult();
            WriteLog.write("易思凯", "ABE_SendCommand接口，接口返回的数据[" + result + "]");
            if (result != null && !result.equals("")) {
                org.dom4j.Document document = DocumentHelper.parseText(result);
                org.dom4j.Element root = document.getRootElement();
                if (document.selectSingleNode(root.getName() + "/HostResult") != null) {
                    if (root.elementText("HostResult") != null && !root.elementText("HostResult").equals("")) {
                        strReturn = root.elementText("HostResult");
                    }
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 通用命令查询接口方法
     * 
     * @param pnr
     * @param strcmd
     *            黑屏命令
     * @return
     */
    public String commandFunction4(String pnr, String strcmd) {
        String strReturn = "";
        try {
            String identityXml = "<Identity_1_0><UserId>"
                    + userid
                    + "</UserId><UserPassword></UserPassword><UserIdLv2></UserIdLv2><UserType></UserType><User></User></Identity_1_0>";
            String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            requestXml += "<ABE_SendCommandsByPNR_1_0>";
            requestXml += "<PNR>" + pnr + "</PNR>";
            requestXml += "<CommandStrings>";
            requestXml += "<CommandString>" + strcmd + "</CommandString>";
            requestXml += "</CommandStrings>";
            requestXml += "</ABE_SendCommandsByPNR_1_0>";
            WriteLog.write("易思凯", "ABE_SendCommandsByPNR接口，向接口中传入的数据[identityXml:" + identityXml + "---requestXml:"
                    + requestXml + "]");
            com.ysk.interticket.ServiceStub.ABESubmit aBESubmit = new com.ysk.interticket.ServiceStub.ABESubmit();
            aBESubmit.setIdentity(identityXml);
            aBESubmit.setRequest(requestXml);
            com.ysk.interticket.ServiceStub stub = new com.ysk.interticket.ServiceStub();
            com.ysk.interticket.ServiceStub.ABESubmitResponse response = stub.aBESubmit(aBESubmit);
            String result = response.getABESubmitResult();
            WriteLog.write("易思凯", "ABE_SendCommandsByPNR接口，接口返回的数据[" + result + "]");
            if (result != null && !result.equals("")) {
                org.dom4j.Document document = DocumentHelper.parseText(result);
                org.dom4j.Element root = document.getRootElement();
                if (document.selectSingleNode(root.getName() + "/HostResult") != null) {
                    if (root.elementText("HostResult") != null && !root.elementText("HostResult").equals("")) {
                        strReturn = root.elementText("HostResult");
                    }
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 通用命令查询接口方法
     * 
     * @param strCmd
     *            黑屏命令
     * @param strUrl
     *            黑屏地址
     * @return StrReturn 黑屏返回结果 Author:HTHY
     */
    public String commandFunction3(String strcmd, String strPageinfo, String strIG, String strUrl) {
        // 接口地址
        // String strUrl = this.ipAddress;
        // 命令参数
        String strCmd = "?cmd=";

        // 翻页命令
        String strPage = strPageinfo;
        // 黑屏查询指令：
        strCmd += getBASE64(strcmd);
        // 清楚缓存数据

        String strReturn = "";
        // 接口调用地址和命令参数
        String totalurl = strUrl + strCmd + strPage + strIG;
        java.io.InputStream in = null;
        try {
            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            in = conn.getInputStream();
            org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
            org.jdom.Document doc = build.build(in);
            org.jdom.Element data = doc.getRootElement();
            List<Element> muticmdlist = data.getChildren("CMDLIST");
            int i = 0;
            for (Element cmdlist : muticmdlist) {
                i++;
                String status = cmdlist.getChildTextTrim("STATUS");
                // 返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
                if (status != null && status.equals("0")) {
                    strReturn = cmdlist.getChildTextTrim("MESSAGE");
                }
                else {
                    if (i == 1) {
                        strReturn = cmdlist.getChildTextTrim("RESPONSE");
                    }
                    else {
                        strReturn += "\r\n" + cmdlist.getChildTextTrim("RESPONSE");
                    }
                }
            }
            in.close();
            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 通用命令查询接口方法
     * 
     * @param strCmd
     *            黑屏命令
     * @return StrReturn 黑屏返回结果 Author:HTHY
     */
    public String commandFunctionHK(String strcmd, String strPageinfo, String strIG) {
        // 接口地址
        String strUrl = this.ipAddress;
        // 命令参数
        String strCmd = "?cmd=";

        // 翻页命令
        String strPage = strPageinfo;
        System.out.println("no base64:---" + strcmd);
        // 黑屏查询指令：
        strCmd += getBASE64(strcmd);
        // 清楚缓存数据

        String strReturn = "";
        // 接口调用地址和命令参数
        String totalurl = strUrl + strCmd + strPage + strIG;
        System.out.println("===============×××××××××××url:" + totalurl);
        java.io.InputStream in = null;
        try {
            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            // conn.connect();
            in = conn.getInputStream();
            org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
            org.jdom.Document doc = build.build(in);
            org.jdom.Element data = doc.getRootElement();
            List<Element> muticmdlist = data.getChildren("CMDLIST");
            int i = 0;
            for (Element cmdlist : muticmdlist) {
                i++;
                String status = cmdlist.getChildTextTrim("STATUS");
                // 返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
                if (status != null && status.equals("0")) {
                    strReturn = cmdlist.getChildTextTrim("MESSAGE");
                }
                else {
                    if (i == 1) {
                        strReturn = cmdlist.getChildTextTrim("RESPONSE");
                    }
                    else {
                        strReturn += "\r\n" + cmdlist.getChildTextTrim("RESPONSE");
                    }
                }
            }
            in.close();
            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 通用命令查询接口方法
     * 
     * @param strCmd
     *            黑屏命令
     * @return StrReturn 黑屏返回结果,只返回第一条黑屏指令 Author:HTHY
     */
    public String commandFunction3(String strcmd, String strPageinfo, String strIG) {
        WriteLog.write("BBC", "NBE_commandFunction3:0:" + strcmd);
        // 接口地址
        String strUrl = this.ipAddress;
        // 命令参数
        String strCmd = "?cmd=";
        // 翻页命令
        String strPage = strPageinfo;
        // 黑屏查询指令：
        strCmd += getBASE64(strcmd);
        // 清楚缓存数据

        String strReturn = "";
        // 接口调用地址和命令参数
        String totalurl = strUrl + strCmd + strPage + strIG;
        java.io.InputStream in = null;
        try {
            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            in = conn.getInputStream();
            org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
            org.jdom.Document doc = build.build(in);
            org.jdom.Element data = doc.getRootElement();
            List<Element> muticmdlist = data.getChildren("CMDLIST");
            int i = 0;
            for (Element cmdlist : muticmdlist) {
                i++;
                String status = cmdlist.getChildTextTrim("STATUS");
                // 返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
                if (status != null && status.equals("0")) {
                    strReturn = cmdlist.getChildTextTrim("MESSAGE");
                }
                else {
                    if (i == 1) {
                        strReturn = cmdlist.getChildTextTrim("RESPONSE");
                    }
                    else {
                        strReturn += "\r\n" + cmdlist.getChildTextTrim("RESPONSE");
                    }
                }
            }
            in.close();
            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WriteLog.write("BBC", "NBE_commandFunction3:1:" + strReturn);
        return strReturn;
    }

    /**
     * 创建PNR信息
     * 
     * @param SS
     *            航段组
     * @return NM 姓名组
     * @param CT
     *            联系方式
     * @return TK 出票时限
     * @param NUM
     *            乘机人数
     * @return depTime 出发时间
     * @param SSR
     *            证件信息
     * @return PNR Author:HTHY
     */
    public String CreatePnr(String SS, String NM, String CT, String TK, String NUM, String depTime, String SSR) {
        return "RMFC2";
    }

    /**
     * 取消PNR功能
     * 
     * @param strPNR
     *            PNR编码
     * @return SUCCESS表示成功
     */
    public String XEPNR(String strPNR) {
        String strReturn = "-1";
        try {
            String identityXml = "<Identity_1_0><UserId>"
                    + userid
                    + "</UserId><UserPassword></UserPassword><UserIdLv2></UserIdLv2><UserType></UserType><User></User></Identity_1_0>";
            String requestXml = "<?xml version=\"1.0\"?>";
            requestXml += "<ABE_XEPNR_1_0>";
            requestXml += "<PNR>" + strPNR + "</PNR>";
            requestXml += "</ABE_XEPNR_1_0>";
            WriteLog.write("易思凯", "ABE_XEPNR接口，向接口传入的数据[identityXml:" + identityXml + "---requestXml:" + requestXml
                    + "]");
            com.ysk.interticket.ServiceStub.ABESubmit aBESubmit = new com.ysk.interticket.ServiceStub.ABESubmit();
            aBESubmit.setIdentity(identityXml);
            aBESubmit.setRequest(requestXml);
            com.ysk.interticket.ServiceStub stub = new com.ysk.interticket.ServiceStub();
            com.ysk.interticket.ServiceStub.ABESubmitResponse response = stub.aBESubmit(aBESubmit);
            String result = response.getABESubmitResult();
            WriteLog.write("易思凯", "ABE_XEPNR接口，接口返回的数据[" + result + "]");
            if (result != null && !result.equals("")) {
                org.dom4j.Document document = DocumentHelper.parseText(result);
                org.dom4j.Element root = document.getRootElement();
                if (root.getText() != null && root.getText().contains("PNR CANCELLED")) {
                    strReturn = "SUCCESS";
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 分离PNR功能
     * 
     * @param strPNR
     *            PNR编码
     * @param strNumber
     *            要分离乘机人所在序号，必须要与黑屏中的序号一致
     * @return
     */
    public String SPPNR(String strPNR, String strNumber) {
        String strReturn = "NOPNR";
        if (!strPNR.equals("NOPNR")) {
            try {
                WriteLog.write("NBE", "kaYn_PNR_SPLIT:0:" + strPNR);
                ServiceStub stub = new ServiceStub();
                ServiceStub.KaYn_PNR_SPLIT kaYn_PNR_SPLIT = new ServiceStub.KaYn_PNR_SPLIT();

                ServiceStub.KaYn_PNR_SPLITResponse response = stub.kaYn_PNR_SPLIT(kaYn_PNR_SPLIT);
                // <string>0000,HYKTWK,JWB9ZH</string>
                String result = response.getKaYn_PNR_SPLITResult();
                WriteLog.write("NBE", "kaYn_PNR_SPLIT:1:" + result);
                String[] results = result.split(",");
                // 0000表示返回结果是成功的
                if (results[0].equals("0000")) {
                    strReturn = results[1];
                }
            }
            catch (AxisFault e) {
                e.printStackTrace();
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return strReturn;
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
        String strReturn = "";
        strReturn = commandFunction2("QT", "", "");
        return strReturn;
    }

    /**
     * 处理某种Q信箱
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQS(String strQueryType) {
        String strReturn = "";
        // 处理完要释放结束原来的处理
        strReturn = commandFunction3("QS:" + strQueryType, "", "");
        return strReturn;
    }

    /**
     * 释放Q信箱
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQNE() {
        String strReturn = "";
        // 处理完要释放结束原来的处理
        strReturn = commandFunction3("QNE", "", "");
        return strReturn;
    }

    /**
     * 释放当前处理下一个
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQN() {
        String strReturn = "";
        // 处理完要释放结束原来的处理
        strReturn = commandFunction3("QD", "", "");
        return strReturn;
    }

    /**
     * 调用51book接口生成PNR
     * 
     * @param listsegmenginf
     *            //航程信息类
     * @param listpassengers
     *            //乘机人类
     * @param strCustomerCode
     *            //大客户编码
     * @return
     */
    public String CreatePNRByInterFace(List<Segmentinfo> listsegment, List<Passenger> listpassenger,
            String strCustomerCode) {
        String strPNRinfo = "";// 返回值
        String pninfo = "";
        String fltinfo = "";

        // 航班信息提取
        int strRoute = 0;
        int intpassengerindex = 0;
        for (Segmentinfo segmentinfo : listsegment) {
            strRoute++;
            fltinfo += strRoute + ",";
            fltinfo += segmentinfo.getStartairport() + ",";
            fltinfo += segmentinfo.getEndairport() + ",";
            fltinfo += segmentinfo.getAircomapnycode() + ",";
            fltinfo += segmentinfo.getFlightnumber().substring(2, segmentinfo.getFlightnumber().length()) + ",";
            fltinfo += segmentinfo.getCabincode() + ",";
            fltinfo += formatTimestampyyyyMMdd(segmentinfo.getDeparttime());
            if (strRoute != listsegment.size()) {
                fltinfo += ";";
            }
        }
        // 乘机人信息提取
        for (Passenger passenger : listpassenger) {
            intpassengerindex++;
            try {
                pninfo += URLEncoder.encode(passenger.getName(), "utf-8") + ",";
                pninfo += passenger.getPtype() + ",";
                pninfo += passenger.getIdnumber() + ",";
                String stridtype = "";
                if (passenger.getIdtype() == 1) {
                    stridtype = "NI";
                }
                else if (passenger.getIdtype() == 2) {
                    stridtype = "PP";
                }
                else {
                    stridtype = "ID";
                }
                pninfo += stridtype + ",";
                pninfo += intpassengerindex;
                if (intpassengerindex != listpassenger.size()) {
                    pninfo += ";";
                }
            }
            catch (Exception ex) {
                System.out.println("生成PNR拼接乘机人异常：" + ex.getMessage());
            }
        }
        // java.io.InputStream in = null;
        String url = "http://220.113.41.88/cn_interface/createpnr.jsp?";
        url += "u=" + "6ed3fc98f2da5089a6144598a6a320e3";// 用户ID
        url += "&Pninfo=" + pninfo;// 乘机人信息串
        url += "&Fltinfo=" + fltinfo;// 航班信息串
        WriteLog.write("生成PNR字符串", "生成PNR字符串:" + url);
        java.net.URL Url;
        try {
            Url = new java.net.URL(url);
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
            Element lists = doc.getRootElement();

            List str = lists.getChildren();
            /** 因为返回数据就三条固定记录 所以就不用循环了 直接取* */
            Element pnr1 = (Element) str.get(0);// 得到的是是否有记录，true和false
            Element pnr2 = (Element) str.get(1);// 得到的是错误信息
            Element pnr3 = (Element) str.get(2);// 得到PNR字符串

            if (pnr1.getText().equals("true")) {
                strPNRinfo = pnr3.getText();// 传入PNR数据
                WriteLog.write("生成PNR", "接口生成PNR:" + strPNRinfo);
            }
            else {
                strPNRinfo = pnr2.getText(); // 返回错误信息
                WriteLog.write("返回错误", "返回错误:" + strPNRinfo);
            }

            in.close();
            conn.disconnect();

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(strPNRinfo + "返回数据内容");

        // 验证PNR格式是否正确
        String regEx = "[a-zA-Z0-9]{6}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strPNRinfo);
        if (!m.find() && strPNRinfo.length() != 6) {
            strPNRinfo = "NOPNR";
        }
        return strPNRinfo;
    }

    /**
     * 在黑屏中生成PNR
     * 
     * @param listsegmenginf
     *            //航程信息类
     * @param listpassengers
     *            //乘机人类
     * @param strCustomerCode
     *            //儿童PNR编码中备注的成人PNR编码
     * @return
     */
    public String CreatePNRByCmd(List<Segmentinfo> listsegmenginf, List<Passenger> listpassengers,
            String strCustomerCode) {
        String strRetrun = "NOPNR";
        String ptype = "1";
        try {
            String identityXml = "<Identity_1_0><UserId>"
                    + userid
                    + "</UserId><UserPassword></UserPassword><UserIdLv2></UserIdLv2><UserType></UserType><User></User></Identity_1_0>";
            String requestXml = "<?xml version=\"1.0\" encoding=\"gb2312\"?>";
            requestXml += "<ABE_CreatePNR_1_2>";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            String dateStr = sdf1.format(sdf.parse(listsegmenginf.get(0).getDeparttime().toString()));
            requestXml += "<TicketLimitDate>" + dateStr + "</TicketLimitDate>";
            requestXml += "<TicketLimitTime>00:00</TicketLimitTime>";
            requestXml += "<Office>" + officeNumber + "</Office>";
            requestXml += "<Telephone></Telephone>";
            requestXml += "<RMKS><RMK /></RMKS>";
            requestXml += "<Flights>";
            for (int i = 1; i <= listsegmenginf.size(); i++) {
                if (i == 1) {// 单程
                    requestXml += "<Flight>";
                    requestXml += "<Carrier>" + listsegmenginf.get(0).getAircomapnycode() + "</Carrier>";
                    String str = listsegmenginf.get(0).getFlightnumber();
                    String newStr = "";
                    if (str != null && !str.equals("") && str.length() > 0) {
                        newStr = str.substring(2, str.length());
                    }
                    requestXml += "<FlightNo>" + newStr + "</FlightNo>";
                    requestXml += "<ClassCode>" + listsegmenginf.get(0).getCabincode() + "</ClassCode>";
                    String str1 = sdf1.format(sdf.parse(listsegmenginf.get(0).getDeparttime().toString()));
                    String str2 = sdf2.format(sdf.parse(listsegmenginf.get(0).getDeparttime().toString()));
                    requestXml += "<DepartureDate>" + str1 + "</DepartureDate>";
                    requestXml += "<DepartureTime>" + str2 + "</DepartureTime>";
                    requestXml += "<BoardPoint>" + listsegmenginf.get(0).getStartairport() + "</BoardPoint>";
                    requestXml += "<OffPoint>" + listsegmenginf.get(0).getEndairport() + "</OffPoint>";
                    requestXml += "</Flight>";
                }
                if (i == 2) {// 往返
                    requestXml += "<Flight>";
                    requestXml += "<Carrier>" + listsegmenginf.get(1).getAircomapnycode() + "</Carrier>";
                    String str = listsegmenginf.get(1).getFlightnumber();
                    String newStr = "";
                    if (str != null && !str.equals("") && str.length() > 0) {
                        newStr = str.substring(2, str.length());
                    }
                    requestXml += "<FlightNo>" + newStr + "</FlightNo>";
                    requestXml += "<ClassCode>" + listsegmenginf.get(1).getCabincode() + "</ClassCode>";
                    String str1 = sdf1.format(sdf.parse(listsegmenginf.get(1).getDeparttime().toString()));
                    String str2 = sdf2.format(sdf.parse(listsegmenginf.get(1).getDeparttime().toString()));
                    requestXml += "<DepartureDate>" + str1 + "</DepartureDate>";
                    requestXml += "<DepartureTime>" + str2 + "</DepartureTime>";
                    requestXml += "<BoardPoint>" + listsegmenginf.get(1).getStartairport() + "</BoardPoint>";
                    requestXml += "<OffPoint>" + listsegmenginf.get(1).getEndairport() + "</OffPoint>";
                    requestXml += "</Flight>";
                }
            }
            requestXml += "</Flights>";
            requestXml += "<Passengers>";
            for (int j = 0; j < listpassengers.size(); j++) {
                requestXml += "<Passenger>";
                if (listpassengers.get(j).getPtype().equals("2")) {
                    requestXml += "<Name>" + listpassengers.get(j).getName() + "CHD" + "</Name>";
                    ptype = "2";
                }
                else {
                    requestXml += "<Name>" + listpassengers.get(j).getName() + "</Name>";
                    ptype = "1";
                }
                requestXml += "<CardNo>" + listpassengers.get(j).getIdnumber() + "</CardNo>";
                requestXml += "<BirthDay>" + listpassengers.get(j).getBirthday() + "</BirthDay>";
                requestXml += "<Country></Country>";
                requestXml += "<MobilePhone>" + listpassengers.get(j).getMobile() + "</MobilePhone>";
                requestXml += "<CarrierCard></CarrierCard>";
                requestXml += "</Passenger>";
            }
            requestXml += "</Passengers>";
            requestXml += "<OtherSegments>";
            requestXml += "<OtherSegment>OSI " + listsegmenginf.get(0).getAircomapnycode() + " CTCT" + linkMobile
                    + "</OtherSegment>";
            requestXml += "</OtherSegments>";
            requestXml += "</ABE_CreatePNR_1_2>";
            if (ptype.equals("1")) {
                WriteLog.write("生成PNR日志（易思凯）", "创建成人pnr，ABE_CreatePNR接口，传入接口的数据[identityXml:" + identityXml
                        + "---requestXml:" + requestXml + "]");
            }
            else if (ptype.equals("2")) {
                WriteLog.write("生成PNR日志（易思凯）", "创建儿童pnr，ABE_CreatePNR接口，传入接口的数据[identityXml:" + identityXml
                        + "---requestXml:" + requestXml + "]");
            }
            // 创建成人pnr和创建儿童pnr调用的都是同一个接口
            com.ysk.interticket.ServiceStub.ABESubmit aBESubmit = new com.ysk.interticket.ServiceStub.ABESubmit();
            aBESubmit.setIdentity(identityXml);
            aBESubmit.setRequest(requestXml);
            com.ysk.interticket.ServiceStub stub = new com.ysk.interticket.ServiceStub();
            com.ysk.interticket.ServiceStub.ABESubmitResponse response = stub.aBESubmit(aBESubmit);
            String result = response.getABESubmitResult();
            result = result.replaceAll("&lt;", "<");
            result = result.replaceAll("&gt;", ">");
            WriteLog.write("生成PNR日志（易思凯）", "ABE_CreatePNR接口，接口返回的数据[" + result + "]");
            if (result != null && !result.equals("")) {
                org.dom4j.Document document = DocumentHelper.parseText(result);
                org.dom4j.Element root = document.getRootElement();
                if (document.selectSingleNode(root.getName() + "/PNR") != null) {
                    if (root.elementText("PNR") != null && !root.elementText("PNR").equals("")) {
                        strRetrun = root.elementText("PNR");
                    }
                }
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return strRetrun.trim();
    }

    // 转化时间格式 HH:MM
    public String formatTimestampPID(Timestamp date) {
        try {
            return (new SimpleDateFormat("HHmm").format(date));

        }
        catch (Exception e) {
            return "";
        }

    }

    public String formatTimestampyyyyMMdd(Timestamp date) {
        try {
            return (new SimpleDateFormat("yyyy-MM-dd").format(date));

        }
        catch (Exception e) {
            return "";
        }

    }

    public static Timestamp dateToTimestamp(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            if (date.length() == 10) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            return (new Timestamp(dateFormat.parse(date).getTime()));

        }
        catch (Exception e) {
            return null;
        }

    }

    // 提取PNR信息
    public String getPNRInfo(String strPNR) {
        return getFormatPnrRT(strPNR);
    }

    // 提取PNR信息
    public String getsmallPNRInfo(String strPNR) {
        strPNR = strPNR.trim();
        // 对PNR进行格式验证
        if ((strPNR == "")) {
            return "PNR不能为空!";

        }
        int len = strPNR.length();
        if (len != 5 && len != 6) {
            return "PNR格式不正确!";
        }
        if (!IsValidPnr(strPNR)) {
            return "PNR格式不正确";
        }
        String strFinalStr = commandFunction2("RT" + strPNR + "$PN$PN$PAT:A", "", "");
        return formatHTML(strFinalStr);
    }

    // 小PNR转大编码
    public String getPNRBigInfo(String strPNR) {
        strPNR = strPNR.trim();
        // 对PNR进行格式验证
        if ((strPNR == "")) {
            return "PNR不能为空!";

        }
        int len = strPNR.length();
        if (len != 5 && len != 6) {
            return "PNR格式不正确!";
        }
        if (!IsValidPnr(strPNR)) {
            return "PNR格式不正确";
        }
        String strFinalStr = commandFunction("RT:X/" + strPNR + "$PN$PN$PAT:A", "");
        return formatHTML(strFinalStr);
    }

    // 提取大PNR信息
    public String getBigPNRInfo(String strPNR) {
        String strBigPNR = "";
        strPNR = strPNR.trim();
        // 对PNR进行格式验证
        if ((strPNR == "")) {
            return "";

        }
        int len = strPNR.length();
        if (len != 5 && len != 6) {
            return "";
        }
        if (!IsValidPnr(strPNR)) {
            return "";
        }
        // 0000,HPDBCY,CZ,3174,X,20APR,2335,0055+1,PEK,CGO,1,王大毛,412920197309281209,成人,HK,^PEK449^MFNRHB^1
        String pnrinfo = getFormatPnrRT(strPNR);
        String[] pnrs = pnrinfo.split(",");
        return pnrs[pnrs.length - 1].split("\\^")[2];
    }

    /*
     * 根据PNR取得票号信息
     */
    public String getTicketNumber(String strPnrInfo, String strPnmber) {
        // M DETR:CN/YP0DC/C
        // DETR:TN/898-1654160863 NAME:朱君
        // FOID:NI340702198401027526 JD5128 /29OCT10/LJGNKG FLOW
        // DETR:TN/898-1654160862 NAME:王云飞
        // FOID:NI142201198304139017 JD5128 /29OCT10/LJGNKG FLOW
        // DETR:TN/880-1708466876 NAME:罗招贵
        // FOID:RP1055981178 HU7113 /14NOV10/CANNKG OPEN
        // DETR:TN/880-1708466878 NAME:张晶晶
        // FOID:RP1055981180 HU7113 /14NOV10/CANNKG OPEN
        // DETR:TN/880-1708466877 NAME:宋智
        // FOID:RP1055981179 HU7113 /14NOV10/CANNKG OPEN
        // END OF SELECTION LIST
        // SSR TKNE SC HK1 NKGKMG 4695 H25NOV 3241710452688/1/P1

        // 如果记录已经取消 票号格式是：TN/781-2365546732/P1
        String strTicketNumber = "";
        Pattern pticketinfo = Pattern.compile("<br>");
        String[] strpnrinfo = pticketinfo.split(strPnrInfo);
        String[] strTemptic = null;
        boolean isbaby = false;
        if (strPnmber.indexOf("INF") >= 0) {
            strPnmber = "P1";
            isbaby = true;
        }
        for (int i = 0; i < strpnrinfo.length; i++) {
            if (strpnrinfo[i].indexOf("SSR TKNE") >= 0 && strpnrinfo[i].indexOf(strPnmber) >= 0) {
                strTemptic = strpnrinfo[i].split("/");
                if (strTemptic.length > 0) {
                    String strTicketall = strTemptic[0].substring(strTemptic[0].length() - 13, strTemptic[0].length());
                    strTicketNumber = strTicketall.substring(0, 3) + "-"
                            + strTicketall.substring(3, strTicketall.length());
                }
            }
            if (isbaby) {
                if (strpnrinfo[i].indexOf("SSR TKNE") >= 0 && strpnrinfo[i].indexOf(strPnmber) >= 0
                        && strpnrinfo[i].indexOf("INF") >= 0) {
                    strTemptic = strpnrinfo[i].split("/");
                    if (strTemptic.length > 0) {
                        String strTicketall = strTemptic[0].substring(strTemptic[0].length() - 13,
                                strTemptic[0].length());
                        strTicketNumber = strTicketall.substring(0, 3) + "-"
                                + strTicketall.substring(3, strTicketall.length());
                    }
                }
            }
            // 只根据TN/来取得票号
            if (strpnrinfo[i].indexOf("TN/") >= 0) {
                strTemptic = strpnrinfo[i].split("/");
                if (strTemptic.length >= 2) {
                    try {
                        if (strTemptic[1].length() >= 14) {
                            strTicketNumber = strTemptic[1].substring(0, 14);
                        }
                        else {
                            if (strTemptic[1].length() >= 13) {
                                strTicketNumber = strTemptic[1].substring(0, 3) + "-" + strTemptic[1].substring(3, 13);
                            }
                        }
                    }
                    catch (Exception ex) {
                        strTicketNumber = strTemptic[1];
                    }

                }
            }
            // 如果记录已经取消 票号格式是：TN/781-2365546732/P1
            if (strpnrinfo[i].indexOf("TN/") >= 0 && strpnrinfo[i].indexOf(strPnmber) >= 0) {
                strTemptic = strpnrinfo[i].split("/");
                if (strTemptic.length >= 2) {
                    try {
                        if (strTemptic[1].trim().length() >= 14 && strTemptic[1].trim().indexOf("-") >= 0) {
                            strTicketNumber = strTemptic[1].substring(0, 14);
                            break;
                        }
                        else {
                            strTicketNumber = strTemptic[1];
                            break;
                        }
                    }
                    catch (Exception ex) {
                        strTicketNumber = strTemptic[1];
                        break;
                    }

                }
            }
            if (strpnrinfo[i].indexOf("SSR OTHS ") >= 0 && strpnrinfo[i].indexOf("TICKET") >= 0) {
                strTemptic = strpnrinfo[i].split("[R][E][N]");
                if (strTemptic.length >= 2) {
                    try {
                        if (strTemptic[1].trim().length() >= 14 && strTemptic[1].trim().indexOf("-") >= 0) {
                            strTicketNumber = strTemptic[1].substring(0, 14);
                            break;
                        }
                        else {
                            strTicketNumber = strTemptic[1];
                            break;
                        }
                    }
                    catch (Exception ex) {
                        strTicketNumber = strTemptic[1];
                        break;
                    }

                }
            }

        }
        return strTicketNumber;

    }

    /*
     * 根据PNR取得行程单号信息
     */
    public String getRpNumber(String strTicketNumber) {
        String strRPNumber = "";
        String strCmd = "DETR:tn/" + strTicketNumber + ",f";
        strRPNumber = commandFunction2(strCmd, "", "");
        if (strRPNumber.length() > 0) {
            String strreg = "[R][P][0-9]{10}";
            Pattern pattFlight = Pattern.compile(strreg);
            Matcher mFlight = pattFlight.matcher(strRPNumber);
            if (mFlight.find()) {
                strRPNumber = mFlight.group().toString().replace("RP", "").trim();
            }
            else {
                strRPNumber = "";
            }
        }
        return strRPNumber;
    }

    // 自动出票指令
    public String Etdz(String pnrcode, String ei, String ETDZcmd, String officeNo) {
        String strPNR = pnrcode.trim();
        String strEI = ei.trim();
        officeNo = this.officeNumber;
        String strEtdz = ETDZcmd.trim();
        String officeNostr = officeNo.trim();

        if ((strPNR == "") || (strEtdz == "")) {
            return "PNR或者Etdz指令为空，请重试!";
        }
        int len = strPNR.length();
        if (len != 5 && len != 6) {
            return "PNR格式不正确，请重试！";
        }
        if (!IsValidPnr(strPNR)) {
            return "无效的PNR格式，请重试！";
        }
        if (!IsValidEtdz(strEtdz)) {
            return "无效的Etdz指令";
        }
        if (!IsValidOffice(officeNostr)) {
            return "无效的Office号";
        }
        try {
            String identityXml = "<Identity_1_0><UserId>"
                    + userid
                    + "</UserId><UserPassword></UserPassword><UserIdLv2></UserIdLv2><UserType></UserType><User></User></Identity_1_0>";
            String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            requestXml = "<ABE_ETDZ_1_0>";
            requestXml = "<PNR>" + strPNR + "</PNR>";
            requestXml = "<PrinterNo>" + printerNo + "</PrinterNo>";
            requestXml = "<Rebate>A</Rebate>";
            requestXml = "<EI>" + strEI + "</EI>";
            requestXml = "<TC></TC>";
            requestXml = "<Farebase></Farebase>";
            requestXml = "<PassengerElements></PassengerElements>";
            requestXml = "<Price>0</Price>";
            requestXml = "<Tax>0</Tax>";
            requestXml = "<DepartureDate></DepartureDate>";
            requestXml = "<AutoTSS>false</AutoTSS>";
            requestXml = "</ABE_ETDZ_1_0>";
            com.ysk.interticket.ServiceStub.ABESubmit aBESubmit = new com.ysk.interticket.ServiceStub.ABESubmit();
            aBESubmit.setIdentity(identityXml);
            aBESubmit.setRequest(requestXml);
            com.ysk.interticket.ServiceStub stub = new com.ysk.interticket.ServiceStub();
            com.ysk.interticket.ServiceStub.ABESubmitResponse response = stub.aBESubmit(aBESubmit);
            String result = response.getABESubmitResult();
            // 乘机人与票号之间用‘:’分割，人与人之间用‘;’分割
            // 黄四郎:880-1106211623;黄三郎:880-1102611623;
            String ticketNos = "";
            if (result != null && !result.equals("")) {
                org.dom4j.Document document = DocumentHelper.parseText(result);
                org.dom4j.Element root = document.getRootElement();
                if (document.selectSingleNode(root.getName() + "/Passengers") != null) {
                    org.dom4j.Element passengersEle = root.element("Passengers");
                    List passengerList = passengersEle.elements("Passenger");
                    Iterator it = passengerList.iterator();
                    while (it.hasNext()) {
                        org.dom4j.Element passengerEle = (org.dom4j.Element) it.next();
                        ticketNos += passengerEle.attributeValue("Name") + ":"
                                + passengerEle.attributeValue("TicketNo") + ";";
                    }
                    return ticketNos;
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return "Err08";
    }

    // 检查返回航班信息参数正确性
    private Boolean CheckAirInfo(String strIn) {
        String regEx = "\\r\\n"; // 换行
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        if (m.find()) {
            return true;
        }
        else {
            return false;
        }
    }

    // 检查航班信息是否有效
    private Boolean IsValidAVdata(String strIn) {
        // String regEx =
        // "\\*{0,1}[0..9,A-Z]{2}\\d{3,4}\\s{2,3}\\w{2}[#,\\*](\\s[A-Z][\\w])*\\s{2,5}\\w{6}\\s\\d{4}\\s{3}\\d{4}(\\+1)*\\s{1,3}\\w{3}\\s\\d[\\^,\\s]\\w{0,1}\\s{2,3}\\w\\s{2}\\>\\s{3}([0..9,A-Z]{2}\\d{3,4})*(\\s){6,11}(\\s[A-Z][\\w])*";
        String regEx = "[*]{0,1}[0-9]{0,2}\\s{0,}[a-zA-Z]{2}[0-9]{3,4}\\w{0,}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        return m.find();
    }

    // 检查是否是数字
    private Boolean isNumber(String strIn) {
        String regEx = "[1-9]{1}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        return m.find();
    }

    private String getXeRrCmd(String strIn) {
        Pattern pattern = Pattern.compile("\\d\\.\\s{2}\\w\\w\\d{3,4}");
        Matcher matcher1 = pattern.matcher(strIn);
        String[] rr = new String[10];
        String rrs = "";
        while (matcher1.find()) {

            int i = 0;
            rr[i] = matcher1.group();
            i++;
        }
        rrs = rr[0].substring(0, 1) + "RR";
        for (int k = 1; k < matcher1.groupCount(); k++) {
            rrs = rrs + "$" + rr[k].substring(0, 1) + "RR";
        }
        // 获取tl项的信息
        String regEx = "\\d\\.TL";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        if (m.find()) {
            String tlstr = m.group();
            String tls = "XE" + tlstr.substring(0, 1);
            return rrs + "$" + tls;

        }

        return "";
    }

    // 是否是有效的Etdz指令
    private Boolean IsValidEtdz(String strIn) {
        String regEx = "[eE][tT][dD][zZ]:[1-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        return m.find();
    }

    /**
     * 取得票号
     * 
     * @param strIn
     *            PNR信息
     * @return
     */
    private String getTicketNo(String strIn) {
        int i = 0;
        Pattern pattern = Pattern.compile("\\d{3}-\\d{10}");
        Matcher matcher = pattern.matcher(strIn);
        String[] TicketNo = new String[matcher.groupCount()];
        String ticketNos = "";
        while (matcher.find()) {
            TicketNo[i] = matcher.group();
            i++;
        }
        for (int k = 0; k < matcher.groupCount(); k++) {
            ticketNos = ticketNos + "/" + TicketNo[k];
        }
        return ticketNos;
    }

    private Boolean checkRRTK(String strIn) {
        String regEx = "\\d*\\.TK";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        String regEx1 = "RR\\d";
        Pattern p1 = Pattern.compile(regEx);
        Matcher m1 = p.matcher(strIn);

        if ((!m.find()) && (m1.find())) {
            return true;
        }
        else {
            return false;
        }
    }

    private Boolean CheckFNFCFP(String strIn) {
        String regEx = "\\d*\\.FN";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        String regEx1 = "\\d*\\.FP";
        Pattern p1 = Pattern.compile(regEx);
        Matcher m1 = p.matcher(strIn);

        if ((m.find()) && (m1.find())) {
            return true;
        }
        else {
            return false;
        }
    }

    // 检查PNR具体信息格式
    private Boolean checkPnrReturn(String strIn) {
        String regEx = "[0-9]\\.";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        return m.find();
    }

    private String getCarrier(String strIn) {
        String regEx = "[0-9]*\\.\\s{2}\\w\\w\\d{3,4}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        if (m.find()) {
            String carrier = m.group();
            String strreg = "\\s{1,}";
            Pattern pcar = Pattern.compile(strreg);
            String[] strcarrierarr = pcar.split(carrier);
            if (strcarrierarr.length == 2) {
                carrier = strcarrierarr[1].substring(0, 2);
                return carrier.trim();
            }
            else {
                return "-1";
            }

        }
        else {
            return "-1";
        }
    }

    // 是否是有效的票号
    private Boolean IsValidOffice(String strIn) {
        String regEx = "[a-zA-Z]{3}[0-9]{3}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        return m.find();
    }

    // 检查PNR格式
    private Boolean IsValidPnr(String strIn) {
        String regEx = "[a-zA-Z][a-zA-Z0-9]{4}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        return m.find();
    }

    /**
     * 
     * @param duzhan
     *            独占
     * @param tongdaohaoma
     *            通道码
     * @param frontIG
     *            前IG
     * @param houIG
     *            后IG
     * @param isNeedOffice
     *            是否需要Office
     * @param officeNo
     *            Office号
     * @param cmdStr
     *            命令字符串
     * @param ServerAdd
     *            服务器地址
     * @param UserName
     *            用户名
     * @param pass_word
     *            密码
     * @return 执行结果字符串
     */
    // CommFunction
    private String commonCmdFunc(String duzhan, String tongdaohaoma, String frontIG, String houIG, String isNeedOffice,
            String officeNo, String cmdStr, String ServerAdd, String UserName, String pass_word) {
        // 数据包格式：STD&独占标志&通道号码&前IG&后IG&是否需要指定office&Office号码&命令
        // 打包协议信息
        duzhan = duzhan.trim();
        tongdaohaoma = tongdaohaoma.trim();
        frontIG = frontIG.trim();
        houIG = houIG.trim();
        isNeedOffice = isNeedOffice.trim();
        officeNo = officeNo.trim();
        cmdStr = cmdStr.trim();
        ServerAdd = ServerAdd.trim();
        UserName = UserName.trim();
        pass_word = pass_word.trim();
        // 判断参数的准确性结束
        String yanzhengstr = "????" + UserName + "!!!!" + pass_word + "@";
        String sendcmd = "STD&" + duzhan + "&" + tongdaohaoma + "&" + frontIG + "&" + houIG + "&" + isNeedOffice + "&"
                + officeNo + "&" + cmdStr;
        String recvStr = "";
        try {
            // 定义访问端口
            int port = 6000;
            // 得到InetAddress 对象
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ServerAdd, port);
            // ------------创建连接------------//
            Socket socket = new Socket();
            InputStream input = null; // 输出流
            OutputStream output = null; // 输入流
            socket.connect(inetSocketAddress, 10 * 1000);
            // 链接成功
            if (socket.isConnected()) {
                // 初始化变量
                String sendStr;
                byte[] bs;
                // 发送验证信息结束
                sendStr = yanzhengstr + sendcmd;
                bs = sendStr.getBytes("ASCII");
                output = socket.getOutputStream();
                input = new DataInputStream(socket.getInputStream());
                output.write(bs);
                output.flush();// 发送信息至服务器
                int avali = 0;
                char firstbyte = (char) input.read();
                avali = input.available();
                recvStr += firstbyte;
                while ((avali = input.available()) > 0) {
                    try {
                        // 在此睡眠0.1秒,很重要
                        Thread.sleep(100);
                    }
                    catch (Exception t) {
                        t.printStackTrace();
                    }
                    byte aval[] = new byte[avali];
                    int bytesRead = input.read(aval, 0, avali);
                    // 将Byte[] 转换成string
                    recvStr += new String(aval, "UTF-8");

                }
                output.close();
                input.close();
                socket.close();
            }
            if (recvStr.length() > 0) {
                if (recvStr.endsWith("@format@")) {
                    return "1006"; // 接口连接失败，通用指令未执行
                }
                else {
                    return recvStr;
                }
            }
            else {
                return "1007"; // 通用指令执行失败
            }
        }
        catch (Exception e) {
            return "1008"; // 通用指令异常出错
        }
    }

    // 得到通道码
    public String getTDcode(String strIn) {
        String TDcode = strIn;
        Pattern pattern = Pattern.compile("[0-9]*&");
        Matcher matcher = pattern.matcher(strIn);
        while (matcher.find()) {
            TDcode = matcher.group();
            return TDcode.replace("&", "");
        }
        if (TDcode == strIn) {
            return "-1";
        }
        else {
            return TDcode.replace('&', ' ');
        }
    }

    public String ChangeDateMode(String dateStr) {
        // 2009-03-19
        String newmon = "";
        System.out.println(dateStr);
        String daystr = dateStr.substring(8, 10);
        String yearstr = dateStr.substring(2, 4);
        String monstr = dateStr.substring(5, 7);
        if (monstr.equals("01")) {
            newmon = "JAN";
        }
        else if (monstr.equals("02")) {
            newmon = "FEB";
        }
        else if (monstr.equals("03")) {
            newmon = "MAR";
        }
        else if (monstr.equals("04")) {
            newmon = "APR";
        }
        else if (monstr.equals("05")) {
            newmon = "MAY";
        }
        else if (monstr.equals("06")) {
            newmon = "JUN";
        }
        else if (monstr.equals("07")) {
            newmon = "JUL";
        }
        else if (monstr.equals("08")) {
            newmon = "AUG";
        }
        else if (monstr.equals("09")) {
            newmon = "SEP";
        }
        else if (monstr.equals("10")) {
            newmon = "OCT";
        }
        else if (monstr.equals("11")) {
            newmon = "NOV";
        }
        else if (monstr.equals("12")) {
            newmon = "DEC";
        }
        return daystr + newmon;
    }

    public String ChangeDateModeYear(String dateStr) {
        // 2009-03-19
        String newmon = "";
        System.out.println(dateStr);
        String daystr = dateStr.substring(8, 10);
        String yearstr = dateStr.substring(2, 4);
        String monstr = dateStr.substring(5, 7);
        if (monstr.equals("01")) {
            newmon = "JAN";
        }
        else if (monstr.equals("02")) {
            newmon = "FEB";
        }
        else if (monstr.equals("03")) {
            newmon = "MAR";
        }
        else if (monstr.equals("04")) {
            newmon = "APR";
        }
        else if (monstr.equals("05")) {
            newmon = "MAY";
        }
        else if (monstr.equals("06")) {
            newmon = "JUN";
        }
        else if (monstr.equals("07")) {
            newmon = "JUL";
        }
        else if (monstr.equals("08")) {
            newmon = "AUG";
        }
        else if (monstr.equals("09")) {
            newmon = "SEP";
        }
        else if (monstr.equals("10")) {
            newmon = "OCT";
        }
        else if (monstr.equals("11")) {
            newmon = "NOV";
        }
        else if (monstr.equals("12")) {
            newmon = "DEC";
        }
        return daystr + newmon + yearstr;
    }

    // 将字符串格式化成HTML
    private String formatHTML(String strIn) {
        int bz = strIn.length();
        if (bz > 0) {
            char[] temps = strIn.toCharArray();
            String temp1 = "";
            int count = 0;
            for (int i = 0; i < temps.length; i++) {
                count++;
                if (count == 80) {
                    count = 0;
                    temp1 = temp1 + "<br>";
                    continue;
                }
                if (temps[i] == (char) 13) {
                    count = 0;
                    temp1 = temp1 + "<br>";
                    continue;
                }
                if (temps[i] == (char) 10) {
                    count = 0;
                    temp1 = temp1 + "<br>";
                    continue;
                }
                if (temps[i] == (char) 27) {
                    temp1 = temp1 + ">>";
                    continue;
                }
                if (temps[i] == (char) 0x1E) {
                    temp1 = temp1 + "&&";
                    continue;
                }
                if (temps[i] == (char) 0x00) {
                    continue;
                }
                temp1 = temp1 + temps[i];

            }
            temp1 = temp1.replace(">>b", "");
            temp1 = temp1.replace("&&", "");
            return temp1;
        }
        else {
            return "";
        }
    }

    private String ETDZCmdFunc(String duzhan, String tongdaohaoma, String frontIG, String houIG, String isNeedOffice,
            String officeNo, String cmdStr, String ServerAdd, String UserName, String pass_word) {
        // 数据包格式：EDZ&独占标志&通道号码&前IG&后IG&是否需要指定office&Office号码&命令
        duzhan = duzhan.trim();
        tongdaohaoma = tongdaohaoma.trim();
        frontIG = frontIG.trim();
        houIG = houIG.trim();
        isNeedOffice = isNeedOffice.trim();
        officeNo = officeNo.trim();
        cmdStr = cmdStr.trim();
        ServerAdd = ServerAdd.trim();
        UserName = UserName.trim();
        pass_word = pass_word.trim();
        // 判断参数的准确性结束
        String yanzhengstr = "????" + UserName + "!!!!" + pass_word + "@";
        // s3:='????'+s1+'!!!!'+s2+'@';
        String sendcmd = "EDZ&" + duzhan + "&" + tongdaohaoma + "&" + frontIG + "&" + houIG + "&" + isNeedOffice + "&"
                + officeNo + "&" + cmdStr;
        String recvStr = "";
        try {
            // 定义访问端口
            int port = 6000;
            // 得到InetAddress 对象
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ServerAdd, port);
            // ------------创建连接------------//
            Socket socket = new Socket();
            InputStream input = null; // 输出流
            OutputStream output = null; // 输入流
            socket.connect(inetSocketAddress, 10 * 1000);
            // 链接成功
            if (socket.isConnected()) {
                // 初始化变量
                String sendStr;
                byte[] bs;
                // 发送验证信息结束
                sendStr = yanzhengstr + sendcmd;
                bs = sendStr.getBytes("ASCII");
                output = socket.getOutputStream();
                input = new DataInputStream(socket.getInputStream());
                output.write(bs);
                output.flush();// 发送信息至服务器
                int avali = 0;
                char firstbyte = (char) input.read();
                avali = input.available();
                recvStr += firstbyte;
                while ((avali = input.available()) > 0) {
                    try {
                        // 在此睡眠0.1秒,很重要
                        Thread.sleep(100);
                    }
                    catch (Exception t) {
                        t.printStackTrace();
                    }
                    byte aval[] = new byte[avali];
                    int bytesRead = input.read(aval, 0, avali);
                    // 将Byte[] 转换成string
                    recvStr += new String(aval, "UTF-8");

                }
                output.close();
                input.close();
                socket.close();
            }
            if (recvStr.length() > 0) {
                if (recvStr.endsWith("@format@")) {
                    return "1006"; // 接口连接失败，通用指令未执行
                }
                else {
                    return recvStr;
                }
            }
            else {
                return "1007"; // 通用指令执行失败
            }
        }
        catch (Exception e) {
            return "1008"; // 通用指令异常出错
        }
    }

    public List getOrderbypnr(String pnr) {
        // TODO Auto-generated method stub
        List list = new ArrayList();
        pnr = "XP8VR";
        // 通过接口获取pnr
        // String pnrstr=getPNRInfo(pnr);
        // 通过接口获取的pnr字符串
        String pnrstr = "";

        // 截取pnr信息
        /*
         * int personNumber=0; String nm="";
         * 
         * String[] patRow = pnrstr.split("[P][A][T]"); //用PAT将PNR分为PatRow数组
         * String[] rows = patRow[0].split("[0-9]{1,}[///.]"); String strPersons
         * =pnrstr.replace(pnr, "#").split("#")[0].replace("<br>", "");
         * String[] person = strPersons.split("[0-9]{1,}[///.]"); for (int j =
         * 1; j < person.length; j++) { if (person[j].trim().length() > 0) {
         * personNumber = personNumber + 1; nm += person[j].trim() + "#"; //
         * 每个乘机人姓名后面加一个# } } for(int i=0;i<rows.length;i++) {
         * System.out.println(rows[i]); } for(int j=0;j<person.length;j++) {
         * System.out.println(person[j]); } System.out.println(personNumber+nm);
         */
        // 添加行程单
        List<Segmentinfo> listSegmentinfo = new ArrayList<Segmentinfo>();
        Segmentinfo segmentinfo = new Segmentinfo();
        segmentinfo.setAircomapnycode("CA");
        Segmentinfo segmentinfo2 = new Segmentinfo();
        segmentinfo2.setAircomapnycode("CA");
        listSegmentinfo.add(segmentinfo);
        listSegmentinfo.add(segmentinfo2);
        // 添加订单
        List<Orderinfo> listOrderinfo = new ArrayList<Orderinfo>();
        Orderinfo orderinfo = new Orderinfo();
        orderinfo.setAddresa("beijing");
        Orderinfo orderinfo2 = new Orderinfo();
        orderinfo2.setAddresa("beijing");
        listOrderinfo.add(orderinfo);
        listOrderinfo.add(orderinfo2);
        // 添加乘机人信息
        List<Passenger> listPassenger = new ArrayList<Passenger>();
        Passenger passenger = new Passenger();
        passenger.setName("bian");
        Passenger passenger2 = new Passenger();
        passenger2.setName("bian");
        listPassenger.add(passenger);
        listPassenger.add(passenger2);
        // 添加pnr到list里面
        list.add(pnrstr);
        // 将行程单订单乘机人信息添加到list里面
        list.add(listSegmentinfo);
        list.add(listOrderinfo);
        list.add(listPassenger);
        // 返回list
        return list;
    }

    /**
     * 国际查询机票接口
     */
    public AllRouteInfo getIntelFlightInfo(String strFromCity, String strTocity, String strFromDate, String ToDate,
            String strTripType, String strAirCoType) {
        AllRouteInfo allRouteinfo = new AllRouteInfo();
        allRouteinfo.setAllRouteID(1);
        allRouteinfo.setRouteStr("BJS-SFO");
        // 航班线路类
        List<Route> listRoute = new ArrayList<Route>();
        Route routeinfo = new Route();
        routeinfo.setID(1);
        routeinfo.setPolicyInfo("MATK");
        routeinfo.setTotalFare(4364f);
        routeinfo.setTotalTax(1034);
        routeinfo.setPolicyMark("TYY");
        routeinfo.setFromCity("BJS");
        routeinfo.setDestCity("SFO");
        routeinfo.setAirCompany("DL");
        routeinfo.setRouteStr("BJS-SFO");
        // 航班线路详细信息
        List<RouteDetailInfo> listRouteDetail = new ArrayList<RouteDetailInfo>();
        RouteDetailInfo routedetail = new RouteDetailInfo();
        routedetail.setFromCity("BJS");
        routedetail.setDestCity("SFO");
        routedetail.setAirCompany("DL");
        routedetail.setCabin("Y");
        routedetail.setFromAirport("BJS");
        routedetail.setToAirport("SFO");
        routedetail.setFromDate("2010-05-24 09:00");
        routedetail.setToDate("2010-05-25 10:05");
        routedetail.setFlightNumber("DL634");
        routedetail.setTotalFlightNo(4);
        // 航班信息
        List<AllFlight> listAllFlight = new ArrayList<AllFlight>();
        AllFlight allflight = new AllFlight();
        allflight.setNo(1);
        allflight.setFromCity("BJS");
        allflight.setDestCity("SFO");
        allflight.setAirCompany("DL");
        allflight.setFromCity("2010-05-24 09:00");
        allflight.setToDate("2010-05-25 10:05");
        allflight.setFlightNumber("DL634");
        listAllFlight.add(allflight);
        // 第二条航班
        AllFlight allflight1 = new AllFlight();
        allflight1.setNo(2);
        allflight1.setFromCity("BJS");
        allflight1.setDestCity("SFO");
        allflight1.setAirCompany("DL");
        allflight1.setFromCity("2010-05-24 11:30");
        allflight1.setToDate("2010-05-25 14:15");
        allflight1.setFlightNumber("DL526");
        listAllFlight.add(allflight1);

        // 第三条航班
        AllFlight allflight2 = new AllFlight();
        allflight2.setNo(3);
        allflight2.setFromCity("BJS");
        allflight2.setDestCity("SFO");
        allflight2.setAirCompany("DL");
        allflight2.setFromCity("2010-05-24 14:30");
        allflight2.setToDate("2010-05-25 16:15");
        allflight2.setFlightNumber("DL111");
        listAllFlight.add(allflight2);

        // 第三条航班
        AllFlight allflight3 = new AllFlight();
        allflight3.setNo(4);
        allflight3.setFromCity("BJS");
        allflight3.setDestCity("SFO");
        allflight3.setAirCompany("DL");
        allflight3.setFromCity("2010-05-24 15:00");
        allflight3.setToDate("2010-05-25 17:25");
        allflight3.setFlightNumber("DL873");
        listAllFlight.add(allflight3);

        routedetail.setFlightInfos(listAllFlight);
        listRouteDetail.add(routedetail);
        routeinfo.setRouteDetailInfos(listRouteDetail);
        listRoute.add(routeinfo);

        // 第二条航线
        // 航班线路类
        Route routeinfo1 = new Route();
        routeinfo1.setID(1);
        routeinfo1.setPolicyInfo("MATK");
        routeinfo1.setTotalFare(4700f);
        routeinfo1.setTotalTax(1060);
        routeinfo1.setPolicyMark("TYY");
        routeinfo1.setFromCity("BJS");
        routeinfo1.setDestCity("SFO");
        routeinfo1.setAirCompany("CO");
        routeinfo1.setRouteStr("BJS-HKG-SFO");
        // 航班线路详细信息
        List<RouteDetailInfo> listRouteDetail1 = new ArrayList<RouteDetailInfo>();
        RouteDetailInfo routedetail1 = new RouteDetailInfo();
        routedetail1.setFromCity("BJS");
        routedetail1.setDestCity("SFO");
        routedetail1.setAirCompany("CO");
        routedetail1.setCabin("C");
        routedetail1.setFromAirport("BJS");
        routedetail1.setToAirport("SFO");
        routedetail1.setFromDate("2010-05-24 08:00");
        routedetail1.setToDate("2010-05-25 10:05");
        routedetail1.setFlightNumber("CO777");
        routedetail1.setTotalFlightNo(2);
        // 航班信息
        List<AllFlight> listAllFlight1 = new ArrayList<AllFlight>();
        AllFlight allflight4 = new AllFlight();
        allflight4.setNo(1);
        allflight4.setFromCity("BJS");
        allflight4.setDestCity("SFo");
        allflight4.setAirCompany("CO");
        allflight4.setFromCity("2010-05-24 08:00");
        allflight4.setToDate("2010-05-25 10:05");
        allflight4.setFlightNumber("CO777");
        listAllFlight1.add(allflight4);

        routedetail1.setFlightInfos(listAllFlight1);
        listRouteDetail1.add(routedetail1);
        routeinfo1.setRouteDetailInfos(listRouteDetail1);
        listRoute.add(routeinfo1);
        // 第二条结束

        allRouteinfo.setRoutes(listRoute);
        return allRouteinfo;
    }

    /**
     * 通过时间获取星期几
     * 
     * @param time
     * @return
     */
    public String getWeekStr(String time) {
        String strReturn = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(time);
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int weekday = cal.get(cal.DAY_OF_WEEK);

        if (weekday == 1) {
            strReturn = "Sun";
        }
        else if (weekday == 2) {
            strReturn = "Mon";
        }
        else if (weekday == 3) {
            strReturn = "Tues";
        }
        else if (weekday == 4) {
            strReturn = "Wed";
        }
        else if (weekday == 5) {
            strReturn = "Thur";
        }
        else if (weekday == 6) {
            strReturn = "Fri";
        }
        else if (weekday == 7) {
            strReturn = "Sat";
        }
        return strReturn;
    }

    /**
     * 获取指定时间的后几天
     * 
     * @param time
     *            指定时间
     * @param intDay
     *            指定时间的后几天天数
     * @return
     */
    public String GetDate(String time, int intDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String strReturn = "";
        try {
            date = sdf.parse(time);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            cd.add(Calendar.DATE, intDay);// 增加一天
            date = cd.getTime();
            strReturn = sdf.format(date).toString();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 获取指定时间的前几小时
     * 
     * @param time
     *            指定时间
     * @param intDay
     *            指定时间的前几小时
     * @return
     */
    public String GetMintue(String time, int intMinute) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        String strReturn = "";
        try {
            date = sdf.parse(time);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            cd.add(Calendar.MINUTE, intMinute);// 分钟相加减
            date = cd.getTime();
            strReturn = sdf.format(date).toString();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    // 将 s 进行 BASE64 编码
    public static String getBASE64(String s) {
        if (s == null)
            return null;

        String re = (new sun.misc.BASE64Encoder()).encode(s.getBytes());
        return URLEncoder.encode(re);
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s) {
        if (s == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询特价或者其他舱位PAT价格信息
     * 
     * @param strFromCity
     *            出发城市
     * @param strToCity
     *            到达城市
     * @param strFromDate
     *            出发日期
     * @param strFromTime
     *            出发时间
     * @param strAirCom
     *            航空公司代码
     * @param strCabinCode
     *            舱位代码
     * @return
     */
    public String GetPriceAndDiscountByPATA(String strFromCity, String strToCity, String strFromDate,
            String strFromTime, String strAirCom, String strCabinCode) {
        String strReturn = "";
        // avh/peksha/05JAN/0800/MU$SD1Y1$pat:a$ig
        String stravh = "avh/" + strFromCity + strToCity + "/" + ChangeDateModeYear(strFromDate) + "/" + strFromTime
                + "/" + strAirCom.substring(0, 2);
        String strSD = "sd1" + strCabinCode + "1";
        String strPAT = "pat:a";
        String strIG = "i";
        String strCmdinfo = stravh + "$" + strSD + "$" + strPAT + "$" + strIG;
        try {
            WriteLog.write("GetPrice", "0:" + ipAddress2 + ":" + strCmdinfo);
            strReturn = commandFunction3(strCmdinfo, "", "", ipAddress2);
            WriteLog.write("GetPrice", "1.5:" + strReturn);
            strReturn = strReturn.substring(strReturn.indexOf(">PAT:A"), strReturn.indexOf("SFC:01"));
            System.out.println(strReturn);
            strReturn = strReturn.substring(strReturn.indexOf("FARE:CNY") + 8, strReturn.indexOf("TAX:CNY") - 1);
            // Y舱价格:原来的通过黑屏取
            // String strYPrice = GetYPriceByFD(strFromCity, strToCity,
            // strAirCom);
            // Y舱价格:现在的从航班号里面截取
            String strYPrice = strAirCom.split("[,]")[1];
            Float fdiscount = 100f;
            fdiscount = Float.parseFloat(strReturn) / Float.parseFloat(strYPrice) * 10;
            strReturn = strReturn + "|" + formatZrate(fdiscount);
            System.out.println(strReturn);
        }
        catch (Exception e) {
            e.printStackTrace();
            strReturn = "0|0";
        }
        WriteLog.write("GetPrice", "2:" + strReturn);
        return strReturn;
    }

    /**
     * 根据NFD信息获取舱位价格和舱位折扣 NDF信息(NFD:PEKCAN/22OCT11/CA)
     * 
     * @param strFromCity
     *            出发城市三字码
     * @param strToCity
     *            到达城市三字码
     * @param strFromDate:00:00
     *            出发日期
     * @param strAirCom
     *            航空公司代码码
     * @param strCabinCode
     *            舱位码
     * @return 返回舱位价格和舱位折扣，格式：舱位价格|舱位折扣 Created By:sunbin Date:2011-10-08
     */
    public String GetPriceAndDiscountByNFD(String strFromCity, String strToCity, String strFromDate, String strAirCom,
            String strCabinCode) {
        String[] strFromDateStrings = strFromDate.split("~");
        strFromDate = strFromDateStrings[0];
        String strFromTime = "0000";
        if (strFromDateStrings.length > 1) {
            strFromTime = strFromDateStrings[1].replace(":", "");
        }
        if (ipAddress2.length() < 4 || ipAddress2 == null) {
            String strReturn = "";
            String strRet = "";
            try {
                String strcmd = "NFD:" + strFromCity + strToCity + "/" + ChangeDateModeYear(strFromDate) + "/"
                        + strAirCom + "$PN$PN";
                strReturn = commandFunction2(strcmd, "", "");
                String[] strarr = strReturn.split("TRVDATE");
                // 计算折扣信息
                String strYPrice = GetYPriceByFD(strFromCity, strToCity, strAirCom);
                System.out.println(strYPrice);
                if (strarr.length >= 1) {
                    // strReturn=strarr[1];
                    Pattern patmiles = Pattern.compile("\\n");
                    String[] strNFDArr = patmiles.split(strReturn);
                    for (int i = 0; i < strNFDArr.length; i++) {
                        if (strNFDArr[i].indexOf("NFN:") > 0) {
                            Pattern patitems = Pattern.compile("\\s{1,}");
                            String[] strItems = patitems.split(strNFDArr[i]);
                            if (strRet.equals("") || strRet.equals("0|0")) {
                                if (strItems.length >= 4 && strItems[4].equals(strCabinCode)) {
                                    for (int j = 0; j < strItems.length; j++) {
                                        // 计算折扣
                                        Float fdiscount = 0f;
                                        try {
                                            if (!strYPrice.equals("") && !strItems[2].equals("")) {
                                                fdiscount = Float.parseFloat(strItems[2].toString())
                                                        / Float.parseFloat(strYPrice) * 10;
                                            }
                                        }
                                        catch (Exception ex) {
                                            fdiscount = 0f;
                                        }
                                        strRet = strItems[2] + "|" + formatZrate(fdiscount);
                                    }
                                }
                                else {
                                    strRet = "0|0";
                                    continue;
                                }
                            }
                            else {
                                break;
                            }
                        }
                    }
                }
                else {
                    strRet = "0|0";
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                strRet = "0|0";
            }
            return strRet;
        }
        else {
            return GetPriceAndDiscountByPATA(strFromCity, strToCity, strFromDate, strFromTime, strAirCom, strCabinCode);
        }
    }

    /**
     * 格式政策
     * 
     * @param num
     * @return
     */
    public String formatZrate(Float num) {
        DecimalFormat format = null;
        format = (DecimalFormat) NumberFormat.getInstance();
        format.applyPattern("###0.0");
        try {
            String result = format.format(num);
            return result;
        }
        catch (Exception e) {
            return Float.toString(num);
        }
    }

    /**
     * 格式折扣
     * 
     * @param num
     * @return
     */
    public static String formatDis(Float num) {
        DecimalFormat format = null;
        format = (DecimalFormat) NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.HALF_DOWN);
        format.applyPattern("##00");
        try {
            String result = format.format(num);
            return result;
        }
        catch (Exception e) {
            return Float.toString(num);
        }
    }

    public String GetYPriceByFD(String strFromCity, String strToCity, String strAirCom) {
        // 全价价格
        String strYPRICE = "";
        // 从黑屏中得到全价价格
        String strPriceinfo = getYPriceByFdcmd(strFromCity, strToCity, strAirCom);
        // 解析Y舱价格
        Pattern patternprice = Pattern.compile(strAirCom + "/Y");
        // 以换行符将字符串拆分
        String[] strpriceinfoarr = patternprice.split(strPriceinfo);
        if (strpriceinfoarr.length >= 2) {
            String strPPricecabin = strpriceinfoarr[1].toString();
            Pattern patcab = Pattern.compile("\\/");
            String[] strParr = patcab.split(strPPricecabin);
            String strTruePriceinfo = "";
            if (strParr.length >= 2) {
                strTruePriceinfo = strParr[1].toString().trim();
                if (strTruePriceinfo.indexOf("=") >= 0) {
                    Pattern patcabtemp = Pattern.compile("=");
                    String[] strtempcab = patcabtemp.split(strTruePriceinfo);
                    if (strtempcab.length == 2) {
                        strYPRICE = strtempcab[0].trim();
                    }
                }
                else {
                    strYPRICE = strTruePriceinfo;
                }
            }
        }
        // 解析完毕
        return strYPRICE;
    }

    /**
     * 使用查询价格专用黑屏查询Y舱价格
     * 
     * @param strFromCity
     * @param strToCity
     * @param strAirCom
     * @return
     */
    public String GetYPriceByFD1(String strFromCity, String strToCity, String strAirCom) {
        // 全价价格
        String strYPRICE = "";
        // 从黑屏中得到全价价格
        String strcmd = "FD:" + strFromCity + strToCity + "/" + strAirCom;
        String strPriceinfo = commandFunction3(strcmd, "", "", ipAddress2);

        // 解析Y舱价格
        Pattern patternprice = Pattern.compile(strAirCom + "/Y");
        // 以换行符将字符串拆分
        String[] strpriceinfoarr = patternprice.split(strPriceinfo);
        if (strpriceinfoarr.length >= 2) {
            String strPPricecabin = strpriceinfoarr[1].toString();
            Pattern patcab = Pattern.compile("\\/");
            String[] strParr = patcab.split(strPPricecabin);
            String strTruePriceinfo = "";
            if (strParr.length >= 2) {
                strTruePriceinfo = strParr[1].toString().trim();
                if (strTruePriceinfo.indexOf("=") >= 0) {
                    Pattern patcabtemp = Pattern.compile("=");
                    String[] strtempcab = patcabtemp.split(strTruePriceinfo);
                    if (strtempcab.length == 2) {
                        strYPRICE = strtempcab[0].trim();
                    }
                }
                else {
                    strYPRICE = strTruePriceinfo;
                }
            }
        }
        // 解析完毕
        return strYPRICE;
    }

    /**
     * 在黑屏中FD查询基础价格
     * 
     * @param strSPort
     *            出发机场三字码
     * @param strEPort
     *            到达机场三字码
     * @param strDate
     *            航空公司代码
     * @return 基础价格
     */
    public String getYPriceByFdcmd(String strSPort, String strEPort, String strAirCompany) {
        String strReturn = "";
        String strcmd = "FD:" + strSPort + strEPort + "/" + strAirCompany;
        strReturn = Server.getInstance().getTicketSearchService().commandFunction2(strcmd, "", "");
        return strReturn;
    }

    public String getFullRTPnrResult(String strPNR) {
        String strReturn = "";
        strReturn = Server.getInstance().getTicketSearchService().commandFunction2("RT" + strPNR + "$PAT:A", "", "");
        if (strReturn.indexOf("+") > 0) {
            strReturn = Server.getInstance().getTicketSearchService()
                    .commandFunction2("RT" + strPNR + "$PN$PAT:A", "", "");
        }
        else if (strReturn.indexOf("+") > 0) {
            strReturn = Server.getInstance().getTicketSearchService()
                    .commandFunction2("RT" + strPNR + "$PN$PN$PAT:A", "", "");
        }
        else if (strReturn.indexOf("+") > 0) {
            strReturn = Server.getInstance().getTicketSearchService()
                    .commandFunction2("RT" + strPNR + "$PN$PN$PN$PAT:A", "", "");
        }

        return strReturn;
    }

    public List<Aircompany> getAircompanyCache() {
        try {
            List<Aircompany> list = cachebasedata.getAirCompanyList("ALLAirCompanyList");
            return list;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public List<Flightmodel> getFlightModelCache() {
        try {
            List<Flightmodel> list = cachebasedata.getFlightModel("ALLFlightModelList");
            return list;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public Cabin getCabinCache(String strKey, String strAirCompanycode, String strCabinCode) {
        try {
            Cabin cabin = cachebasedata.getCabinInfo(strKey, strAirCompanycode, strCabinCode);
            return cabin;
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * 通过LT接口生成PNR
     * 
     * @param flying_off
     *            起飞城市三字码(如果是联程或往返用逗号隔开)
     * @param arrive
     *            到达城市三字码(如果是联程或往返用逗号隔开)
     * @param takeoff_date
     *            起飞日期(YYYY-MM-DD,如果是联程或往返用逗号隔开)
     * @param time
     *            起飞时间(0000,如果是联程或往返用逗号隔开)
     * @param company
     *            航空公司名字(CA1408,如果是联程或往返用逗号隔开)
     * @param accommodation
     *            舱位(如果是联程或往返用逗号隔开)
     * @param name
     *            姓名(多个用逗号隔开)
     * @param userid
     *            证件号(多个用逗号隔开)
     * @param userOfficeId
     *            OFFICE号
     * @param phone
     *            电话号码
     */
    public String createPNRbyletu(String flying_off, String arrive, String takeoff_date, String time, String company,
            String accommodation, String name, String userid, String userOfficeId, String phone) {
        String pnr = "NOPNR";
        try {
            WriteLog.write("NBE", "kaYn_PNR_SPLIT:0:" + flying_off + "," + arrive + "," + takeoff_date + "," + time
                    + "," + company + "," + accommodation + "," + name + "," + userid + "," + userOfficeId + ","
                    + phone);
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_eTerm_Tk_Str kaYn_eTerm_Tk_Str = new ServiceStub.KaYn_eTerm_Tk_Str();
            // 起飞城市三字码(如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setFlying_off(flying_off);
            // 到达城市三字码(如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setArrive(arrive);
            // 起飞日期(YYYY-MM-DD,如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setTakeoff_date(takeoff_date);
            // 起飞时间(0000,如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setTime(time);
            // 航空公司名字(CA1408,如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setCompany(company);
            // 舱位(如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setAccommodation(accommodation);
            // 姓名(多个用逗号隔开)
            kaYn_eTerm_Tk_Str.setName(name);
            // 证件号(多个用逗号隔开)
            kaYn_eTerm_Tk_Str.setUserid(userid);
            // OFFICE号
            kaYn_eTerm_Tk_Str.setUserOfficeId(officeNumber);
            // 电话号码
            kaYn_eTerm_Tk_Str.setPhone(phone);
            ServiceStub.KaYn_eTerm_Tk_StrResponse response = stub.kaYn_eTerm_Tk_Str(kaYn_eTerm_Tk_Str);
            // <string>0000,HPDBCY,PEK449,</string>
            String result = response.getKaYn_eTerm_Tk_StrResult();
            WriteLog.write("NBE", "kaYn_eTerm_Tk_Str:1:" + result);
            String[] results = result.split(",");
            // 0000表示返回结果是成功的
            if (results[0].equals("0000")) {
                pnr = results[1];
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

        return pnr;
    }

    /**
     * 订儿童票
     * 
     * @param flying_off
     *            起飞城市三字码(如果是联程或往返用逗号隔开)
     * @param arrive
     *            到达城市三字码(如果是联程或往返用逗号隔开)
     * @param takeoff_date
     *            起飞日期(YYYY-MM-DD,如果是联程或往返用逗号隔开)
     * @param time
     *            起飞时间(0000,如果是联程或往返用逗号隔开)
     * @param company
     *            航班号(CA1408,如果是联程或往返用逗号隔开)
     * @param accommodation
     *            舱位(如果是联程或往返用逗号隔开)
     * @param name
     *            姓名(格式:王二小,多人用逗号隔开)
     * @param birthday
     *            出生日期或证件号码(格式:2008-08-08或证件号,多人用逗号隔开.注:南航必须输入出生日期)
     * @param userOfficeId
     *            OFFICE号
     * @param phone
     *            电话号码
     * @param adultPNR
     *            成人PNR(用于儿童与成人关联,为空则不关联)
     * @return
     */
    public String createCHDPNRbyletu(String flying_off, String arrive, String takeoff_date, String time,
            String company, String accommodation, String name, String birthday, String userOfficeId, String phone,
            String adultPNR) {
        String pnr = "NOPNR";
        try {
            WriteLog.write("NBE", "kaYn_CHD_Ticket:0:" + flying_off + "," + arrive + "," + takeoff_date + "," + time
                    + "," + company + "," + accommodation + "," + name + "," + birthday + "," + userOfficeId + ","
                    + phone + "," + adultPNR);
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_CHD_Ticket kaYn_CHD_Ticket = new ServiceStub.KaYn_CHD_Ticket();
            kaYn_CHD_Ticket.setFlying_off(flying_off);
            kaYn_CHD_Ticket.setArrive(arrive);
            kaYn_CHD_Ticket.setTakeoff_date(takeoff_date);
            kaYn_CHD_Ticket.setTime(time);
            kaYn_CHD_Ticket.setCompany(company);
            kaYn_CHD_Ticket.setAccommodation(accommodation);
            kaYn_CHD_Ticket.setName(name);
            kaYn_CHD_Ticket.setBirthday(birthday);
            kaYn_CHD_Ticket.setPhone(phone);
            kaYn_CHD_Ticket.setAdultPNR(adultPNR);
            kaYn_CHD_Ticket.setOffice(officeNumber);
            ServiceStub.KaYn_CHD_TicketResponse response = stub.kaYn_CHD_Ticket(kaYn_CHD_Ticket);
            String result = response.getKaYn_CHD_TicketResult();
            WriteLog.write("NBE", "kaYn_CHD_Ticket:1:" + result);
            String[] results = result.split(",");
            // 0000表示返回结果是成功的
            if (results[0].equals("0000")) {
                pnr = results[1];
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return pnr;
    }

    /**
     * 订婴儿票
     * 
     * @param adultPNR
     *            成人PNR号
     * @param adultName
     *            成人姓名
     * @param babyName
     *            婴儿姓名(王二小)
     * @param babyPY
     *            婴儿姓名拼音(wang/erxiao)
     * @param babyBirthday
     *            婴儿出生日期(2009-09-09)
     * @param office
     *            OFFICE号
     * @return
     */
    public String createBABYPNRbyletu(String adultPNR, String adultName, String babyName, String babyPY,
            String babyBirthday, String office) {
        String pnr = "NOPNR";
        try {
            WriteLog.write("NBE", "kaYn_Baby_Ticket:0:" + adultPNR + "," + adultName + "," + babyName + "," + babyPY
                    + "," + babyBirthday + "," + office);
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_Baby_Ticket kaYn_Baby_Ticket = new ServiceStub.KaYn_Baby_Ticket();
            kaYn_Baby_Ticket.setPNR(adultPNR);
            kaYn_Baby_Ticket.setAdultName(adultName);
            kaYn_Baby_Ticket.setBabyName(babyName);
            kaYn_Baby_Ticket.setBabyPY(babyPY);
            kaYn_Baby_Ticket.setBabyBirthday(babyBirthday);
            kaYn_Baby_Ticket.setOFFICE(officeNumber);
            ServiceStub.KaYn_Baby_TicketResponse response = stub.kaYn_Baby_Ticket(kaYn_Baby_Ticket);
            String result = response.getKaYn_Baby_TicketResult();
            WriteLog.write("NBE", "kaYn_Baby_Ticket:0:" + result);
            String[] results = result.split(",");
            // 0000表示返回结果是成功的
            if (results[0].equals("0000")) {
                pnr = results[1];
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return pnr;
    }

    /**
     * 方法说明：读取PNR信息
     * 
     * @param pnr
     *            PNR号
     * @return 0000,HPDBCY,CZ,3174,X,20APR,2335,0055+1,PEK,CGO,1,王大毛,412920197309281209,成人,HK,^PEK449^MFNRHB^1
     * @author chend 2013-4-16
     */
    public String getFormatPnrRT(String pnr) {
        String result = "-1";
        try {
            WriteLog.write("NBE", "pNR_Demand:0:" + pnr);
            ServiceStub stub = new ServiceStub();
            ServiceStub.PNR_Demand pNR_Demand = new ServiceStub.PNR_Demand();
            pNR_Demand.setPNR(pnr);
            pNR_Demand.setOfficeID(officeNumber);
            ServiceStub.PNR_DemandResponse response = stub.pNR_Demand(pNR_Demand);
            // <string>0000,HPDBCY,CZ,3174,X,20APR,2335,0055+1,PEK,CGO,1,王大毛,412920197309281209,成人,HK,^PEK449^MFNRHB^1</string>
            result = response.getPNR_DemandResult();
            WriteLog.write("NBE", "pNR_Demand:1:" + result);
            String[] results = result.split(",");
            // 0000表示返回结果是成功的
            if (!results[0].equals("0000")) {
                result = "-1";
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getIscacheprice() {
        return iscacheprice;
    }

    public void setIscacheprice(String iscacheprice) {
        this.iscacheprice = iscacheprice;
    }

    public String getIpAddress2() {
        return ipAddress2;
    }

    public void setIpAddress2(String ipAddress2) {
        this.ipAddress2 = ipAddress2;
    }

    @Override
    public String AUTHpnr(String pnr, String office) {
        String result = "-1";
        try {
            String identityXml = "<Identity_1_0><UserId>"
                    + userid
                    + "</UserId><UserPassword></UserPassword><UserIdLv2></UserIdLv2><UserType></UserType><User></User></Identity_1_0>";
            String requestXml = "<ABE_AddRMK_1_0>";
            requestXml += "<PNR>" + pnr + "</PNR>";
            requestXml += "<RMKType>RMK TJ</RMKType>";
            requestXml += "<Text>AUTH " + office + "</Text>";
            requestXml += "<Option></Option>";
            requestXml += "</ABE_AddRMK_1_0>";
            WriteLog.write("易思凯", "ABE_AddRMK接口，向接口中传入的数据[identityXml:" + identityXml + "---requestXml:" + requestXml
                    + "]");
            com.ysk.interticket.ServiceStub.ABESubmit aBESubmit = new com.ysk.interticket.ServiceStub.ABESubmit();
            aBESubmit.setIdentity(identityXml);
            aBESubmit.setRequest(requestXml);
            com.ysk.interticket.ServiceStub stub = new com.ysk.interticket.ServiceStub();
            com.ysk.interticket.ServiceStub.ABESubmitResponse response = stub.aBESubmit(aBESubmit);
            String resultStr = response.getABESubmitResult();
            WriteLog.write("易思凯", "ABE_AddRMK接口，接口返回的数据[" + resultStr + "]");
            if (resultStr != null && !resultStr.equals("")) {
                org.dom4j.Document document = DocumentHelper.parseText(resultStr);
                org.dom4j.Element root = document.getRootElement();
                if (root.getText() != null && root.getText().equals("OK")) {
                    result = root.getText();
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getRealRT(String pnr, String type) {
        String cmdCode = "rt " + pnr;
        WriteLog.write("易思凯", "查询rt信息命令[" + cmdCode + "]");
        String result = commandFunction2(cmdCode, "", "");
        WriteLog.write("易思凯", "查询rt信息返回的数据[" + result + "]");
        return result;
    }

    @Override
    public String getRealPat(String pnr, String type) {
        String cmdCode = "";
        String result = "";
        if (type.equals("1")) {
            cmdCode = "PAT:A";
        }
        else if (type.equals("2")) {
            cmdCode = "PAT:A*CH";
        }
        else {
            cmdCode = "PAT:A";
        }
        WriteLog.write("易思凯", "查询pat信息命令[" + cmdCode + "]");
        result = commandFunction2(cmdCode, "", "");
        WriteLog.write("易思凯", "查询pat信息返回的数据[" + result + "]");
        return result;
    }

    @Override
    public String getPatPrice(String pnr, String type, String isAll) {
        String result = "-1";
        try {
            String identityXml = "<Identity_1_0><UserId>"
                    + userid
                    + "</UserId><UserPassword></UserPassword><UserIdLv2></UserIdLv2><UserType></UserType><User></User></Identity_1_0>";
            String requestXml = "<?xml version=\"1.0\"?>";
            requestXml += "<ABE_PATByFlights_1_2>";
            if (type.equals("1")) {
                requestXml += "<PatPara>PAT:A</PatPara>";
            }
            else if (type.equals("2")) {
                requestXml += "<PatPara>PAT:A*CH</PatPara>";
            }
            else {
                requestXml += "<PatPara>PAT:A</PatPara>";
            }
            requestXml += "<FlightSegments></FlightSegments>";
            requestXml += "<PNR>" + pnr + "</PNR>";
            requestXml += "<PriceType></PriceType>";
            requestXml += "<Farebasis></Farebasis>";
            requestXml += "<AllowActionCode></AllowActionCode>";
            requestXml += "</ABE_PATByFlights_1_2>";
            WriteLog.write("易思凯", "ABE_PATByFlights接口，向接口中传入的数据[identityXml:" + identityXml + "---requestXml:"
                    + requestXml + "]");
            com.ysk.interticket.ServiceStub.ABESubmit aBESubmit = new com.ysk.interticket.ServiceStub.ABESubmit();
            aBESubmit.setIdentity(identityXml);
            aBESubmit.setRequest(requestXml);
            com.ysk.interticket.ServiceStub stub = new com.ysk.interticket.ServiceStub();
            com.ysk.interticket.ServiceStub.ABESubmitResponse response = stub.aBESubmit(aBESubmit);
            String resultStr = response.getABESubmitResult();
            WriteLog.write("易思凯", "ABE_PATByFlights接口，接口传出的数据[" + resultStr + "]");
            if (resultStr != null && !resultStr.equals("")) {
                org.dom4j.Document document = DocumentHelper.parseText(resultStr);
                org.dom4j.Element root = document.getRootElement();
                if (document.selectSingleNode(root.getName() + "/FNs") != null) {
                    org.dom4j.Element fnsEle = root.element("FNs");
                    List fnList = fnsEle.elements("FN");
                    Iterator it = fnList.iterator();
                    while (it.hasNext()) {
                        org.dom4j.Element fnEle = (org.dom4j.Element) it.next();
                        result = fnEle.attributeValue("PriceNo") + ",";// 序号
                        result += fnEle.attributeValue("Farebasis") + ",";// 舱位
                        List itemList = fnEle.elements("Item");
                        Iterator itemIt = itemList.iterator();
                        while (itemIt.hasNext()) {
                            org.dom4j.Element itemEle = (org.dom4j.Element) itemIt.next();
                            if (itemEle.attributeValue("Type").equals("F")) {
                                result += itemEle.attributeValue("Value") + ",";// 票面价
                            }
                            else if (itemEle.attributeValue("Type").equals("T")) {
                                if (itemEle.attributeValue("ExtType").equals("CN")) {
                                    result += itemEle.attributeValue("Value") + ",";// 基建费
                                }
                                else if (itemEle.attributeValue("ExtType").equals("YQ")) {
                                    result += itemEle.attributeValue("Value") + ",";// 燃油费
                                }
                            }
                            else if (itemEle.attributeValue("Type").equals("A")) {
                                result += itemEle.attributeValue("Value") + "|";// 总价格
                            }
                        }
                    }
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getpnrStatus(String pnr) {
        String resultStr = "-1";
        try {
            String identityXml = "<Identity_1_0><UserId>"
                    + userid
                    + "</UserId><UserPassword></UserPassword><UserIdLv2></UserIdLv2><UserType></UserType><User></User></Identity_1_0>";
            String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            requestXml += "<ABE_RTPNR_1_0>";
            requestXml += "<PNR>" + pnr + "</PNR>";
            requestXml += "<XmlVersion></XmlVersion>";
            requestXml += "</ABE_RTPNR_1_0>";
            WriteLog.write("易思凯", "ABE_RTPNR接口，传入接口的数据[identityXml:" + identityXml + "---requestXml:" + requestXml
                    + "]");
            com.ysk.interticket.ServiceStub.ABESubmit aBESubmit = new com.ysk.interticket.ServiceStub.ABESubmit();
            aBESubmit.setIdentity(identityXml);
            aBESubmit.setRequest(requestXml);
            com.ysk.interticket.ServiceStub stub = new com.ysk.interticket.ServiceStub();
            com.ysk.interticket.ServiceStub.ABESubmitResponse response = stub.aBESubmit(aBESubmit);
            String result = response.getABESubmitResult();
            result = result.replaceAll("&lt;", "<");
            result = result.replaceAll("&gt;", ">");
            WriteLog.write("易思凯", "ABE_RTPNR接口，接口传出的数据[" + result + "]");
            if (result != null && !result.equals("")) {
                org.dom4j.Document document = DocumentHelper.parseText(result);
                org.dom4j.Element root = document.getRootElement();
                if (document.selectSingleNode(root.getName() + "/Flights") != null) {
                    org.dom4j.Element flightsEle = root.element("Flights");
                    List<org.dom4j.Element> flightList = flightsEle.elements("Flight");
                    org.dom4j.Element flightEle = flightList.get(0);
                    if (flightEle.attributeValue("ActionCode") != null
                            && !flightEle.attributeValue("ActionCode").equals("")) {
                        resultStr = flightEle.attributeValue("ActionCode");
                    }
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public static void main(String[] args) {
        TicketYSKComponent t = new TicketYSKComponent();

        // t.CreatePNRByCmd(listsegmenginf, listpassengers, "");
    }

    public String getPrinterNo() {
        return printerNo;
    }

    public void setPrinterNo(String printerNo) {
        this.printerNo = printerNo;
    }

    @Override
    public String Create_XingChengDan_New(String PiaoHao, String XCD, String OfficeID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String Delete_XingChengDan(String PiaoHao, String XCD, String OfficeID) {
        // TODO Auto-generated method stub
        return null;
    }
}
