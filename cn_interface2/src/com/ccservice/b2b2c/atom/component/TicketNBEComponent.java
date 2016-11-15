package com.ccservice.b2b2c.atom.component;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis2.AxisFault;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import sun.misc.BASE64Decoder;
import client.ServiceStub;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
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

public class TicketNBEComponent extends PublicComponent implements ITicketSearchComponent {
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
        String strReturn = "";
        try {
            WriteLog.write("AVOpen", "FlightSearch" + JSONArray.toJSONString(flightSearch));
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_AVH_New kaYn_AVH_New = new ServiceStub.KaYn_AVH_New();
            kaYn_AVH_New.setFromCity(flightSearch.getStartAirportCode());//fromCity：起飞城市
            kaYn_AVH_New.setToCity(flightSearch.getEndAirportCode()); //toCity：到达城市
            kaYn_AVH_New.setTakeoffDate(flightSearch.getFromDate());//takeoffDate：起飞日期(格式yyyy-MM-dd)
            kaYn_AVH_New.setTakeoffTime(flightSearch.getFromTime());//takeoffTime：起飞时间(格式HHmm,可以为空)
            kaYn_AVH_New.setCarrier(flightSearch.getAirCompanyCode());//carrier：航空公司,为空则不指定航空公司
            kaYn_AVH_New.setIsDirect("1"); //isDirect：是否只查询直飞航班(0=否,1=是)
            kaYn_AVH_New.setIsShared("0"); //isShared：是否显示共享航班(0=否,1=是)
            kaYn_AVH_New.setOffice(this.officeNumber);//office：OFFICE号  PKE242
            String jsonarr = JSONObject.toJSONString(stub.kaYn_AVH_New(kaYn_AVH_New));
            JSONObject json = JSONObject.parseObject(jsonarr);
            WriteLog.write("AVOpen", "ServiceStub" + json);
            strReturn = xmlElements(json.getString("kaYn_AVH_NewResult"));
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 
     * 解析xml的航班信息 
     * @param xmlDoc
     * @return
     * @time 2016年7月8日 下午12:19:38
     * @author 
     */
    public String xmlElements(String xmlDoc) {
        WriteLog.write("AVOpen", "=====xmlDoc===" + xmlDoc);
        List<FlightInfo> flightInfo = new ArrayList<FlightInfo>();
        org.dom4j.Document document;
        try {
            document = DocumentHelper.parseText(xmlDoc);
            org.dom4j.Element root = document.getRootElement();
            root.elementText("Is_Success");
            List list = root.elements("AvhInfo");
            Iterator it = list.iterator();
            while (it.hasNext()) {
                FlightInfo flightInfos = null;
                org.dom4j.Element elmt = (org.dom4j.Element) it.next();
                String FromCity = elmt.elementText("FromCity");
                String ToCity = elmt.elementText("ToCity");
                String TotalFlightTime = elmt.elementText("TotalFlightTime");
                List listFlightInfo = elmt.elements("FlightInfo");
                Iterator its = listFlightInfo.iterator();
                while (its.hasNext()) {
                    flightInfos = new FlightInfo();
                    List<CarbinInfo> carbins = new ArrayList<CarbinInfo>();
                    org.dom4j.Element elmts = (org.dom4j.Element) its.next();
                    String Carrier = elmts.elementText("Carrier");
                    String FlightNum = elmts.elementText("FlightNum");
                    String IsShared = elmts.elementText("IsShared");
                    String SharedFlightNum = elmts.elementText("SharedFlightNum");
                    String FromCityCode = elmts.elementText("FromCityCode");
                    String ToCityCode = elmts.elementText("ToCityCode");
                    String TakeoffDate = elmts.elementText("TakeoffDate");
                    String TakeoffTime = elmts.elementText("TakeoffTime");
                    String ArriveDate = elmts.elementText("ArriveDate");
                    String ArriveTime = elmts.elementText("ArriveTime");
                    String SeatClass = elmts.elementText("SeatClass");
                    String ChildSeatClass = elmts.elementText("ChildSeatClass");
                    String ConnProtocol = elmts.elementText("ConnProtocol");
                    String AirplaneType = elmts.elementText("AirplaneType");
                    String IsStop = elmts.elementText("IsStop");
                    String StopCity = elmts.elementText("StopCity");
                    String FoodMark = elmts.elementText("FoodMark");
                    String Terminal = elmts.elementText("Terminal");
                    String FlightTime = elmts.elementText("FlightTime");
                    String StopTime = elmts.elementText("StopTime");
                    WriteLog.write("AVOpen", "=====SeatClass===" + SeatClass);
                    WriteLog.write("AVOpen", "=====ChildSeatClass===" + ChildSeatClass);
                    if (SeatClass != "") {
                        String[] a = SeatClass.split(" ");
                        for (int i = 0; i < a.length; i++) {
                            CarbinInfo carbinInfo = new CarbinInfo();
                            String Cabin = a[i].substring(0, 1);
                            String seatNum = a[i].substring(1);
                            carbinInfo.setCabin(Cabin);
                            if ("A".equals(seatNum)) {
                                carbinInfo.setSeatNum("9");
                            }
                            else if (isInteger(seatNum)) {
                                carbinInfo.setSeatNum(seatNum);
                            }
                            carbins.add(carbinInfo);
                        }
                    }
                    if (ChildSeatClass != "") {
                        String[] as = ChildSeatClass.split(" ");

                        for (int i = 0; i < as.length; i++) {
                            CarbinInfo carbinInfo = new CarbinInfo();
                            String Cabin = as[i].substring(0, 2);
                            String seatNum = as[i].substring(2, 3);
                            carbinInfo.setCabin(Cabin);
                            if ("A".equals(seatNum)) {
                                carbinInfo.setSeatNum("9");
                            }
                            else if (isInteger(seatNum)) {
                                carbinInfo.setSeatNum(seatNum);
                            }
                            carbins.add(carbinInfo);
                        }
                    }
                    String timestar = TakeoffTime.substring(0, 2);
                    String timeend = TakeoffTime.substring(2, 4);
                    String timesum = timestar + ":" + timeend;
                    String timestars = ArriveTime.substring(0, 2);
                    String timeends = ArriveTime.substring(2, 4);
                    String timesums = timestars + ":" + timeends;
                    flightInfos.setCarbins(carbins);
                    flightInfos.setAirCompany(Carrier);
                    flightInfos.setStartAirport(FromCityCode);
                    flightInfos.setEndAirport(ToCityCode);
                    flightInfos.setAirline(FlightNum);
                    flightInfos.setAirplaneType(AirplaneType);
                    flightInfos.setDepartTime(Timestamp.valueOf(TakeoffDate + " " + timesum + ":00"));
                    flightInfos.setArriveTime(Timestamp.valueOf(ArriveDate + " " + timesums + ":00"));

                }
                flightInfo.add(flightInfos);

            }
            WriteLog.write("AVOpen", "=====ServiceStub===" + JSONArray.toJSONString(flightInfo));
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return JSONArray.toJSONString(flightInfo);
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public List<FlightInfo> findAllFlightinfo(FlightSearch flightSearch) {
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        flightalltemp = searchbaseFlightinfo(flightSearch, isinterface);

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

        try {
            if ("1".equals(getSysConfigByProcedure("useQunarData"))) {
                flightalltemp = addQunarFlightData(flightalltemp, flightSearch);
            }
            // 如果是B2B单程查航班的时候才使用携程的数据
            // 并且时间在早上8点和晚上八点之间
            //            if ("1".equals(flightSearch.getTravelType()) && "1".equals(getSysConfigByProcedure("useCtripData"))
            //                    && "1".equals(flightSearch.getTypeFlag())) {
            //                if (flightalltemp.size() > 0) {
            if ("1".equals(getSysConfigByProcedure("useCtripData"))) {
                flightalltemp = addCtripFlightData(flightalltemp, flightSearch);
            }
            //                }
            //            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //取消重复的舱位
        for (int i = 0; i < flightalltemp.size(); i++) {
            try {
                if ((flightalltemp.get(i) != null && flightalltemp.get(i) != null)
                        && (flightalltemp.get(i).getCarbins() != null && flightalltemp.get(i).getLowCarbin() != null)
                        && (flightalltemp.get(i).getCarbins().size() > 0)
                        && (flightalltemp.get(i).getCarbins().get(0).getCabin().equals(flightalltemp.get(i)
                                .getLowCarbin().getCabin()))) {
                    flightalltemp.get(i).getCarbins().remove(0);
                }
            }
            catch (Exception e) {
                //                e.printStackTrace();
            }
        }

        return flightalltemp;
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
        // 接口调用地址和命令参数
        try {
            WriteLog.write("NBE", "crsCommand_Screen:0:" + strcmd);
            ServiceStub stub = new ServiceStub();
            ServiceStub.CrsCommand_Screen crsCommand_Screen = new ServiceStub.CrsCommand_Screen();
            crsCommand_Screen.setKey("");
            crsCommand_Screen.setNr(strcmd);
            crsCommand_Screen.setOFFICE(officeNumber);
            ServiceStub.CrsCommand_ScreenResponse response = stub.crsCommand_Screen(crsCommand_Screen);
            strReturn = response.getCrsCommand_ScreenResult();
            WriteLog.write("NBE", "crsCommand_Screen:1:" + strReturn);
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
     * @return	SUCCESS表示成功
     */
    public String XEPNR(String strPNR) {
        String strReturn = "-1";
        try {
            WriteLog.write("NBE", "kaYn_XEPNR:0:" + strPNR);
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_XEPNR kaYn_XEPNR = new ServiceStub.KaYn_XEPNR();
            kaYn_XEPNR.setOffice(officeNumber);
            kaYn_XEPNR.setPNR(strPNR);
            ServiceStub.KaYn_XEPNRResponse response = stub.kaYn_XEPNR(kaYn_XEPNR);
            String result = response.getKaYn_XEPNRResult();
            WriteLog.write("NBE", "kaYn_XEPNR:0:" + result);
            String[] results = result.split(",");
            //0000表示返回结果是成功的
            if (results[0].equals("0000")) {
                strReturn = "SUCCESS";
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
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
                //<string>0000,HYKTWK,JWB9ZH</string>
                String result = response.getKaYn_PNR_SPLITResult();
                WriteLog.write("NBE", "kaYn_PNR_SPLIT:1:" + result);
                String[] results = result.split(",");
                //0000表示返回结果是成功的
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
        long orderId = System.currentTimeMillis();
        if (listsegmenginf.size() > 0 && listsegmenginf.get(0).getOrderid() != null) {
            orderId = listsegmenginf.get(0).getOrderid();
        }

        String strRetrun = "NOPNR";
        // * @param flying_off 起飞城市三字码(如果是联程或往返用逗号隔开)
        String flying_off = "";
        // * @param arrive 到达城市三字码(如果是联程或往返用逗号隔开)
        String arrive = "";
        // * @param takeoff_date 起飞日期(YYYY-MM-DD,如果是联程或往返用逗号隔开)
        String takeoff_date = "";
        // * @param time 起飞时间(0000,如果是联程或往返用逗号隔开)
        String time = "";
        // * @param company 航空公司名字(CA1408,如果是联程或往返用逗号隔开)
        String company = "";
        // * @param accommodation 舱位(如果是联程或往返用逗号隔开)
        String accommodation = "";
        // * @param name 姓名(多个用逗号隔开)
        String name = "";
        // * @param userid 证件号(多个用逗号隔开)
        String userid = "";
        // * @param phone 电话号码
        String phone = username;
        if (phone != "") {
            phone += ",";
        }
        phone += password;
        // * @param birthday 出生日期或证件号码(格式:2008-08-08或证件号,多人用逗号隔开.注:南航必须输入出生日期)
        String birthday = "";
        // * @param adultPNR 成人pnr
        String adultPNR = "";

        String ptype = "1";

        // 判断单程还是往返,单程
        for (int j = 1; j <= listsegmenginf.size(); j++) {
            // 单程
            if (j == 1) {
                flying_off += listsegmenginf.get(0).getStartairport();
                arrive += listsegmenginf.get(0).getEndairport();
                String[] data = listsegmenginf.get(0).getDeparttime().toString().split(" ");
                takeoff_date += data[0];
                time += data[1].substring(0, 5).replace(":", "");
                company += listsegmenginf.get(0).getFlightnumber();
                accommodation += listsegmenginf.get(0).getCabincode();

            }
            // 往返或联程
            if (j == 2) {
                flying_off += "," + listsegmenginf.get(1).getStartairport();
                arrive += "," + listsegmenginf.get(1).getEndairport();
                String[] data = listsegmenginf.get(1).getDeparttime().toString().split(" ");
                takeoff_date += "," + data[0];
                time += "," + data[1].substring(0, 5).replace(":", "");
                company += "," + listsegmenginf.get(1).getFlightnumber();
                accommodation += "," + listsegmenginf.get(1).getCabincode();
            }

        }
        int index = 0;
        for (int i = 0; i < listpassengers.size(); i++) {
            if (i > 0) {
                name += "," + listpassengers.get(i).getName();
                userid += "," + listpassengers.get(i).getIdnumber();
            }
            else {
                name += listpassengers.get(i).getName();
                userid += listpassengers.get(i).getIdnumber();
            }
            // 如果是儿童
            if (listpassengers.get(i).getPtype() == 2) {
                if (i > 0) {
                    birthday += "," + listpassengers.get(i).getBirthday();
                }
                else {
                    birthday += listpassengers.get(i).getBirthday();

                }
                ptype = "2";
                userid += listpassengers.get(i).getIdnumber();//又让使用身份证创建编码 2016年5月18日10:55:30
            }
            // 如果是婴儿
            if (listpassengers.get(i).getPtype() == 3) {
                ptype = "3";
                //				strPassName = listpassengers.get(i).getName() + "INF";
            }

        }
        // 如果乘机人类型是儿童，则备注成人PNR编码
        if (listpassengers.get(0).getPtype() == 2) {
            adultPNR = strCustomerCode;
        }

        //LT开始--------------------------------------------------------

        try {
            //成人
            if (ptype.equals("1")) {
                //PEK:CGO:2013-04-20 12:10:00.0:2013-04-20 12:10:00.0:CA1325:H
                WriteLog.write("NBE", orderId + ":LT-成人创建pnr-request:" + flying_off + ":" + arrive + ":" + takeoff_date
                        + ":" + time + ":" + company + ":" + accommodation + ":" + name + ":" + userid + ":"
                        + officeNumber + ":" + phone);
                strRetrun = createPNRbyletu(flying_off, arrive, takeoff_date, time, company, accommodation, name,
                        userid, officeNumber, phone, orderId);
            }
            //儿童
            if (ptype.equals("2")) {
                WriteLog.write("NBE", orderId + ":LT-儿童创建pnr-request:" + flying_off + ":" + arrive + ":" + takeoff_date
                        + ":" + time + ":" + company + ":" + accommodation + ":" + name + ":" + birthday + ":"
                        + officeNumber + ":" + phone + ":" + adultPNR);
                strRetrun = createCHDPNRbyletu(flying_off, arrive, takeoff_date, time, company, accommodation, name,
                        birthday, officeNumber, phone, adultPNR, orderId);
            }
            //婴儿
            if (ptype.equals("3")) {
                WriteLog.write("NBE", orderId + ":LT-婴儿创建pnr-request:" + adultPNR + ":" + arrive + ":" + takeoff_date
                        + ":" + time + ":" + company + ":" + accommodation + ":" + name + ":" + userid + ":"
                        + officeNumber + ":" + phone);
                //strRetrun = createBABYPNRbyletu(adultPNR, adultName, name, babyPY, babyBirthday, office);
            }
        }
        catch (Exception e) {
        }
        //LT结束--------------------------------------------------------
        WriteLog.write("NBE", orderId + ":生成的PNR:" + strRetrun);
        return strRetrun.trim();
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
        //0000,HPDBCY,CZ,3174,X,20APR,2335,0055+1,PEK,CGO,1,王大毛,412920197309281209,成人,HK,^PEK449^MFNRHB^1
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
        String result = "-1";
        try {
            WriteLog.write("NBE_ETDZ", "Etdz:0:" + pnrcode + ":" + ei + ":" + ETDZcmd + ":" + officeNo);
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_ETDZ_CN kaYn_ETDZ_CN = new ServiceStub.KaYn_ETDZ_CN();
            kaYn_ETDZ_CN.setOffice(officeNo);
            kaYn_ETDZ_CN.setPassengerType("0");//乘机人类型(0=成人,1=儿童,2=婴儿)
            kaYn_ETDZ_CN.setPnr(pnrcode);
            kaYn_ETDZ_CN.setPrintNo("1");//打票机端口号
            ServiceStub.KaYn_ETDZ_CNResponse response = stub.kaYn_ETDZ_CN(kaYn_ETDZ_CN);
            result = response.getKaYn_ETDZ_CNResult();
            System.out.println(result);
            WriteLog.write("NBE_ETDZ", "Etdz:1:" + result);
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------
        return result;
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
            //			String strYPrice = GetYPriceByFD(strFromCity, strToCity, strAirCom);
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
     * @param flying_off	起飞城市三字码(如果是联程或往返用逗号隔开)
     * @param arrive	到达城市三字码(如果是联程或往返用逗号隔开)
     * @param takeoff_date	起飞日期(YYYY-MM-DD,如果是联程或往返用逗号隔开)
     * @param time	起飞时间(0000,如果是联程或往返用逗号隔开)
     * @param company	航空公司名字(CA1408,如果是联程或往返用逗号隔开)
     * @param accommodation	舱位(如果是联程或往返用逗号隔开)
     * @param name	姓名(多个用逗号隔开)
     * @param userid	证件号(多个用逗号隔开)
     * @param userOfficeId	OFFICE号
     * @param phone	电话号码
     * @param l12 
     */
    public String createPNRbyletu(String flying_off, String arrive, String takeoff_date, String time, String company,
            String accommodation, String name, String userid, String userOfficeId, String phone, long orderId) {
        String pnr = "NOPNR";
        try {
            WriteLog.write("NBE", orderId + ":kaYn_PNR_SPLIT:0:" + flying_off + "," + arrive + "," + takeoff_date + ","
                    + time + "," + company + "," + accommodation + "," + name + "," + userid + "," + userOfficeId + ","
                    + phone);
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_eTerm_Tk_Str kaYn_eTerm_Tk_Str = new ServiceStub.KaYn_eTerm_Tk_Str();
            //起飞城市三字码(如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setFlying_off(flying_off);
            //到达城市三字码(如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setArrive(arrive);
            //起飞日期(YYYY-MM-DD,如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setTakeoff_date(takeoff_date);
            //起飞时间(0000,如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setTime(time);
            //航空公司名字(CA1408,如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setCompany(company);
            //舱位(如果是联程或往返用逗号隔开)
            kaYn_eTerm_Tk_Str.setAccommodation(accommodation);
            //姓名(多个用逗号隔开)
            kaYn_eTerm_Tk_Str.setName(name);
            //证件号(多个用逗号隔开)
            kaYn_eTerm_Tk_Str.setUserid(userid);
            //OFFICE号
            kaYn_eTerm_Tk_Str.setUserOfficeId(officeNumber);
            //电话号码
            kaYn_eTerm_Tk_Str.setPhone(phone);
            ServiceStub.KaYn_eTerm_Tk_StrResponse response = stub.kaYn_eTerm_Tk_Str(kaYn_eTerm_Tk_Str);
            //<string>0000,HPDBCY,PEK449,</string>
            String result = response.getKaYn_eTerm_Tk_StrResult();
            WriteLog.write("NBE", orderId + ":kaYn_eTerm_Tk_Str:1:" + result);
            String[] results = result.split(",");
            boolean isSuccess = false;//是否创建pnr成功
            String failReason = "";//创建pnr失败原因
            //0000表示返回结果是成功的
            if (results[0].equals("0000")) {
                pnr = results[1];
                isSuccess = true;
            }
            else {
                failReason = results[1];
            }
            recordPnrCreateResult(orderId, isSuccess, failReason, pnr);

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
     * 记录pnr生成 失败或者成功 
     * 
     * @param orderId
     * @param isSuccess
     * @param failReason
     * @time 2016年10月24日 上午11:01:41
     * @author chendong
     * @param pnr 
     */
    public void recordPnrCreateResult(long orderId, boolean isSuccess, String failReason, String pnr) {
        try {
            String sql = "";
            if (isSuccess) {//创建pnr成功
                sql = "EXEC [dbo].[sp_CreatePnrResult_success_Insert]@OrderId = " + orderId + ",@pnr = N'" + pnr + "'";

                createOrderinforc(orderId, 1, 0L, "pnr:" + pnr, 1, 0L);
            }
            else {//创建pnr失败
                sql = "EXEC [dbo].[sp_CreatePnrResult_Insert]@OrderId = " + orderId + ",@CreatePnrFailReason = N'"
                        + failReason + "'";
                WriteLog.write("NBE", orderId + ":recordPnrCreateResult:" + sql);
                createOrderinforc(orderId, 1, 0L, failReason, 1, 0L);
            }
            WriteLog.write("NBE", orderId + ":" + isSuccess + ":" + failReason + ":" + pnr
                    + ":recordPnrCreateResult:" + sql);
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            //          Server.getInstance().getSystemService().findMapResultByProcedure(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 订儿童票
     * @param flying_off	起飞城市三字码(如果是联程或往返用逗号隔开)
     * @param arrive	到达城市三字码(如果是联程或往返用逗号隔开)
     * @param takeoff_date	起飞日期(YYYY-MM-DD,如果是联程或往返用逗号隔开)
     * @param time	起飞时间(0000,如果是联程或往返用逗号隔开)
     * @param company	航班号(CA1408,如果是联程或往返用逗号隔开)
     * @param accommodation	舱位(如果是联程或往返用逗号隔开)
     * @param name	姓名(格式:王二小,多人用逗号隔开)
     * @param birthday	出生日期或证件号码(格式:2008-08-08或证件号,多人用逗号隔开.注:南航必须输入出生日期)
     * @param userOfficeId	OFFICE号
     * @param phone	电话号码
     * @param adultPNR	成人PNR(用于儿童与成人关联,为空则不关联)
     * @param orderId 
     * @return
     */
    public String createCHDPNRbyletu(String flying_off, String arrive, String takeoff_date, String time,
            String company, String accommodation, String name, String birthday, String userOfficeId, String phone,
            String adultPNR, long orderId) {
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
            boolean isSuccess = false;//是否创建pnr成功
            String failReason = "";//创建pnr失败原因
            //0000表示返回结果是成功的
            if (results[0].equals("0000")) {
                pnr = results[1];
                isSuccess = true;
            }
            else {
                failReason = results[1];
            }
            recordPnrCreateResult(orderId, isSuccess, failReason, pnr);
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
     * @param adultPNR	成人PNR号
     * @param adultName	成人姓名
     * @param babyName	婴儿姓名(王二小)
     * @param babyPY	婴儿姓名拼音(wang/erxiao)
     * @param babyBirthday	婴儿出生日期(2009-09-09)
     * @param office	OFFICE号
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
            //0000表示返回结果是成功的
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
     * @param pnr	PNR号
     * @return	0000,HPDBCY,CZ,3174,X,20APR,2335,0055+1,PEK,CGO,1,王大毛,412920197309281209,成人,HK,^PEK449^MFNRHB^1
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
            //<string>0000,HPDBCY,CZ,3174,X,20APR,2335,0055+1,PEK,CGO,1,王大毛,412920197309281209,成人,HK,^PEK449^MFNRHB^1</string>
            result = response.getPNR_DemandResult();
            WriteLog.write("NBE", "pNR_Demand:1:" + result);
            String[] results = result.split(",");
            //0000表示返回结果是成功的
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
        int tempint = new Random().nextInt(1000);
        try {
            WriteLog.write("NBE", "kaYn_PNR_SX" + tempint + ":0:" + pnr + ":" + office);
            if (office.trim().length() == 6) {
                ServiceStub stub = new ServiceStub();
                ServiceStub.KaYn_PNR_SX kaYn_PNR_SX = new ServiceStub.KaYn_PNR_SX();
                kaYn_PNR_SX.setOfficeID(officeNumber);
                kaYn_PNR_SX.setPNR(pnr);
                kaYn_PNR_SX.setSX_OFFICE(office);
                ServiceStub.KaYn_PNR_SXResponse response = stub.kaYn_PNR_SX(kaYn_PNR_SX);
                result = response.getKaYn_PNR_SXResult();
            }
            WriteLog.write("NBE", "kaYn_PNR_SX" + tempint + ":1:" + result);
        }
        catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        return result;
    }

    @Override
    public String getRealRT(String pnr, String type) {
        // TODO Auto-generated method stub
        return commandFunction2("RT" + pnr + "#RT^$^", "", "");
    }

    @Override
    public String getRealPat(String pnr, String type) {
        if (type.equals("1")) {
            return commandFunction2("RT" + pnr + "#PAT:A", "", "");
        }
        else if (type.equals("2")) {
            return commandFunction2("RT" + pnr + "#PAT:A*CH", "", "");
        }
        else {
            return commandFunction2("RT" + pnr + "#PAT:A", "", "");
        }
    }

    @Override
    public String getPatPrice(String pnr, String type, String isAll) {
        // TODO Auto-generated method stub
        String result = "-1";
        WriteLog.write("NBE", "getPatPrice:0:" + pnr + ":" + type + ":" + isAll);
        try {
            if (type.equals("1")) {
                WriteLog.write("NBE", "guest_PAT_Y:0:" + pnr);
                ServiceStub stub = new ServiceStub();
                ServiceStub.Guest_PAT_Y guest_PAT_Y = new ServiceStub.Guest_PAT_Y();
                guest_PAT_Y.setOFFICE(officeNumber);
                guest_PAT_Y.setPNR(pnr);
                guest_PAT_Y.setStart("0");
                ServiceStub.Guest_PAT_YResponse response = stub.guest_PAT_Y(guest_PAT_Y);
                result = response.getGuest_PAT_YResult();
                WriteLog.write("NBE", "guest_PAT_Y:1:" + result);
                String[] results = result.split(",");
                //0000表示返回结果是成功的
                if (!results[0].equals("0000")) {
                    result = "-1";
                }
            }
            else if (type.equals("2")) {
                WriteLog.write("NBE", "guest_PAT_CH:0:" + pnr);
                ServiceStub stub = new ServiceStub();
                ServiceStub.Guest_PAT_CH guest_PAT_CH = new ServiceStub.Guest_PAT_CH();
                guest_PAT_CH.setOFFICE(officeNumber);
                guest_PAT_CH.setPNR(pnr);
                guest_PAT_CH.setStart("0");
                ServiceStub.Guest_PAT_CHResponse response = stub.guest_PAT_CH(guest_PAT_CH);
                result = response.getGuest_PAT_CHResult();
                WriteLog.write("NBE", "guest_PAT_CH:1:" + result);
                String[] results = result.split(",");
                //0000表示返回结果是成功的
                if (!results[0].equals("0000")) {
                    result = "-1";
                }
                else {
                    //0000,400.00,0,30.00,430.00,HR5N9Z,>PAT:A*CH  
                    String[] resultss = result.split(pnr);
                    result = resultss[0].substring(0, resultss[0].length() - 1).replace("0000,", "0000,Y,");

                }
            }
            else if (type.equals("3")) {
                WriteLog.write("NBE", "guest_PAT_IN:0:" + pnr);
                ServiceStub stub = new ServiceStub();
                ServiceStub.Guest_PAT_IN guest_PAT_IN = new ServiceStub.Guest_PAT_IN();
                guest_PAT_IN.setOFFICE(officeNumber);
                guest_PAT_IN.setPNR(pnr);
                guest_PAT_IN.setStart("0");
                ServiceStub.Guest_PAT_INResponse response = stub.guest_PAT_IN(guest_PAT_IN);
                result = response.getGuest_PAT_INResult();
                WriteLog.write("NBE", "guest_PAT_IN:1:" + result);
                String[] results = result.split(",");
                //0000表示返回结果是成功的
                if (!results[0].equals("0000")) {
                    result = "-1";
                }
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        WriteLog.write("NBE", "getPatPrice:1:" + result);
        return result;
    }

    @Override
    public String getpnrStatus(String pnr) {
        //0000,HNVQE5,CA,1385,Y,18MAY,0745,1000,PEK,XFN,2,韩妮蓉,622101196311260720,成人,王宁,11010819540306459X,成人,HK,^PEK449^NXHH3N^1
        String pnrString = getFormatPnrRT(pnr);
        String result = "-1";
        if (!pnrString.equals("-1")) {
            result = pnrString.split("\\^")[0];
            String[] pnrStrings = result.split(",");
            result = pnrStrings[pnrStrings.length - 1];
        }
        WriteLog.write("NBE", "getpnrStatus:" + pnr + ":" + result);
        return result;
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

    @Override
    public String Create_XingChengDan_New(String PiaoHao, String XCD, String OfficeID) {
        Long tempint = System.currentTimeMillis();
        String resultString = "-1";
        try {
            ServiceStub stub = new ServiceStub();
            ServiceStub.Create_XingChengDan_New create_XingChengDan_New = new ServiceStub.Create_XingChengDan_New();
            create_XingChengDan_New.setOfficeID(officeNumber);
            create_XingChengDan_New.setPiaoHao(PiaoHao);
            create_XingChengDan_New.setXCD(XCD);
            WriteLog.write("NBE_Create_XingChengDan_New", tempint + ":PiaoHao:" + PiaoHao + ":XCD:" + XCD
                    + ":officeNumber:" + officeNumber);
            ServiceStub.Create_XingChengDan_NewResponse response = stub
                    .create_XingChengDan_New(create_XingChengDan_New);
            String xingChengDan_NewResult = response.getCreate_XingChengDan_NewResult();
            WriteLog.write("NBE_Create_XingChengDan_New", tempint + ":xingChengDan_NewResult:" + xingChengDan_NewResult);
            resultString = xingChengDan_NewResult;
            //0000,^^^ 马光红 510502198112105023 不得签转/变更退票收费^^ NGJ5G6^^ 杭州 东航MU5878 S 2015-05-06 18:35 S 20K^^ 泸州 VOID^^ VOID^^^^ CNY 770.00CN 50.00YQ EXEMPT CNY 820.00^ 7812191168545 7000 XXX^^ SHA666 2015-06-04^ 08688888北京创程航空服务有限公司^

        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

        return resultString;
    }

    @Override
    public String Delete_XingChengDan(String PiaoHao, String XCD, String OfficeID) {
        //0000,作废行程单成功！
        Long tempint = System.currentTimeMillis();
        String resultString = "-1";
        try {
            ServiceStub stub = new ServiceStub();
            ServiceStub.Delete_XingChengDan delete_XingChengDan = new ServiceStub.Delete_XingChengDan();
            delete_XingChengDan.setOfficeID(officeNumber);
            delete_XingChengDan.setPiaoHao(PiaoHao);
            delete_XingChengDan.setXCD(XCD);
            WriteLog.write("NBE_Delete_XingChengDan", tempint + ":PiaoHao:" + PiaoHao + ":XCD:" + XCD
                    + ":officeNumber:" + officeNumber);
            ServiceStub.Delete_XingChengDanResponse response = stub.delete_XingChengDan(delete_XingChengDan);
            String Delete_XingChengDanResult = response.getDelete_XingChengDanResult();
            WriteLog.write("NBE_Delete_XingChengDan", tempint + ":Delete_XingChengDanResult:"
                    + Delete_XingChengDanResult);
            resultString = Delete_XingChengDanResult;
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return resultString;
    }
}
