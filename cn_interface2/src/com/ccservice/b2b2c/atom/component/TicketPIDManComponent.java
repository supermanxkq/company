package com.ccservice.b2b2c.atom.component;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sun.misc.BASE64Decoder;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.ITicketSearchService;
import com.ccservice.b2b2c.base.aircompany.Aircompany;
import com.ccservice.b2b2c.base.airfee.Airfee;
import com.ccservice.b2b2c.base.cabin.Cabin;
import com.ccservice.b2b2c.base.cityairport.Cityairport;
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

public class TicketPIDManComponent implements ITicketSearchComponent {

    private String username;

    private String password;

    private String ipAddress;

    private String officeNumber;

    private String ipbigAddress;

    //是否是缓存价格数据
    private String iscacheprice;

    //192.168.16.223
    //117.88.72.149
    private CacheFlightInfo cacheflight = new CacheFlightInfo();

    private CacheBaseData cachebasedata = new CacheBaseData();

    private CachePriceInfo cacheprice = new CachePriceInfo();

    WriteLog writeLog = new WriteLog();

    //用户名
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //密码
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //IP地址或者接口调用地址
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    //Office号
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
     * @param FlightSearch
     * @return StrReturn
     * Author:HTHY
     *  avh/PEKCAN/ 22AUG/
     *  22AUG(SUN) BJSCAN  
    	1- *FM2921  DS# C8 YA BS KA LA MA T1 ES HS QS  NAYFUO 0830   1100   738 0    E  >   KN2921      VS WS GS
    	2   KN2921  DS# C8 JS DS YA BS EA HA LA M1 NS  NAYFUO 0830   1100   738 0    E  >               RS SS VS TS WS GS QS KS XS US ZS IS 
    	3   CA1321  DS# FA A6 CA DA Z7 R1 YA BS MS HS  PEKCAN 0900   1200   777 0^B  E  >               KS LS QS GS SA NS VS WS TS ES                       T3 --   
    	               ** M1S V1S   
    	4  *MU7216  AS# FA YA BQ EQ HQ LQ MQ NQ RQ KA  PEKCAN 0915   1220   321 0^L  E  >   CZ3108      VQ TQ QA WQ                                         T2 --   
    	5   CZ3108  AS# FA PQ WA YA TQ KQ HQ MQ GQ SQ  PEKCAN 0915   1220   321 0^L  E  >               LQ QQ UA EQ VQ BA XQ NA RQ                          T2 --   
    	6  *MU7122  AS# F4 YA BQ EQ HQ LQ MQ NQ RQ KA  PEKCAN 1015   1310   321 0^L  E  >   CZ3162      VQ TQ QA WQ                                         T2 --   
    	7+  CZ3162  AS# F4 P4 WA YA TQ KQ HQ MQ GQ SQ  PEKCAN 1015   1310   321 0^L  E  >               LQ QQ UA EQ VQ BA XQ NA RQ                          T2 --   
    	**  PLEASE CHECK YI:CZ/TZ144 FOR ET SELF-SERVICE CHECK-IN KIOS  
     */
    public String AVOpen(FlightSearch flightSearch) {
        //接口地址
        String strUrl = this.ipAddress;
        //命令参数
        String strCmd = "?cmd=";
        //城市对
        String strCityPair = flightSearch.getStartAirportCode() + flightSearch.getEndAirportCode();
        //出发日期
        String strDateCmd = ChangeDateMode(flightSearch.getFromDate().toString());
        if (flightSearch.getFromTime() != null && !flightSearch.getFromTime().equals("")) {
            strDateCmd += "/" + flightSearch.getFromTime();
        }
        //航空公司二字码
        String strCompany = "";
        String strReutrnTemp = "";
        if (flightSearch.getAirCompanyCode() == null) {
            strCompany = "";
        }
        else {
            strCompany = "/" + flightSearch.getAirCompanyCode();
        }
        //翻页命令
        String strPage = "$PN$PN$PN$PN$PN$PN";
        if (flightSearch.getStartAirportCode().equals("PEK") || flightSearch.getStartAirportCode().equals("SHA")
                || flightSearch.getStartAirportCode().equals("PVG") || flightSearch.getEndAirportCode().equals("PEK")
                || flightSearch.getEndAirportCode().equals("SHA") || flightSearch.getEndAirportCode().equals("PVG")) {
            strPage = "$PN$PN$PN$PN$PN$PN$PN$PN";
        }

        //黑屏查询指令：avh/peksha/20OCT
        strCmd += getBASE64("avh/" + strCityPair + "/" + strDateCmd.toUpperCase() + strCompany + strPage);

        String strReturn = "";
        //接口调用地址和命令参数
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
                //返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
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

    /**
     * 根据航班查询参数类得到航班信息列表
     * @param FlightSearch
     * @return FlightInfo
     * Author:HTHY
     */
    public List findAllFlightinfo(FlightSearch flightSearch) {
        //定义返回List
        List<FlightInfo> listFlightInfoAll = new ArrayList<FlightInfo>();
        //在航班信息里面的舱位信息
        String strFlightCabinTemp = "";
        try {
            //调用AVOPen方法查询黑屏航班信息
            //String recvStr=AVOpen(flightSearch); //实时查询航班信息
            String recvStr = cacheflight.getFlightInfo(
                    flightSearch.getStartAirportCode() + "_" + flightSearch.getEndAirportCode() + "_"
                            + flightSearch.getFromDate() + "_" + flightSearch.getFromTime() + "_"
                            + flightSearch.getAirCompanyCode(), flightSearch);

            // 如果返回结果不为空，对返回字符串进行解析
            if (recvStr.length() > 0 && recvStr.split(",").length >= 2) {
                SimpleDateFormat tempDate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String datetime1 = tempDate1.format(new java.util.Date());
                System.out.println("开始解析字符串时间：" + datetime1);

                String[] strarr = recvStr.split(",");
                if ((strarr[0].equals("0") && recvStr.indexOf("FORMAT") > 0) || recvStr.indexOf("没有找到该用户对应的民航配置") > 0) {
                    System.out.println("查询航班失败，请重试!原因：" + strarr[1].toString());
                }
                else {
                    String strRetInfo = strarr[1];
                    Pattern pattern = Pattern.compile("[\\r\\n]");
                    // 以换行符将字符串拆分
                    String[] airInfo = pattern.split(strRetInfo);
                    //list航班
                    List<String> listhangbaninfo = new ArrayList<String>();
                    for (int f = 0; f < airInfo.length; f++) {
                        listhangbaninfo.add(airInfo[f]);
                    }
                    //共享航班重组
                    //*CZ5031  DS# F3 YA UA TQ KQ HQ GQ SQ LQ QQ  
                    for (int f = 0; f < airInfo.length; f++) {
                        String regEx = "[*]{1}([a-zA-Z]{2}|[0-9]{1}[a-zA-Z]{1})[0-9]{3,4}";
                        String strAirCompanyCode = "";
                        Pattern patt = Pattern.compile(regEx);
                        Matcher m = patt.matcher(airInfo[f]);
                        String strOtherShaFliNum = "";
                        if (m.find()) {
                            //取得其他航空公司共享航班代码
                            if (airInfo[f].indexOf(">") >= 0) {
                                String[] strSharearr = airInfo[f].split(">");
                                if (strSharearr.length == 2) {
                                    String strShareTempFlig = strSharearr[1].trim();
                                    Pattern pSharearr = Pattern.compile("\\s{1,2}");
                                    String[] strshareFliNumarr = pSharearr.split(strShareTempFlig); //取得共享航班号所在数组
                                    if (strshareFliNumarr.length >= 1) {
                                        strOtherShaFliNum = strshareFliNumarr[0];
                                    }
                                }
                            }
                            //如果是带星号共享航班，则将共享其他航空公司航班号提取出来，添加一条航线信息
                            String strNewShareFlight = airInfo[f].replace(strOtherShaFliNum, "").replace(m.group(),
                                    strOtherShaFliNum);
                            //判断是否是带有航空公司编码进行查询
                            if (flightSearch.getAirCompanyCode() != null
                                    && !flightSearch.getAirCompanyCode().equals("")) {

                            }
                            else {
                                listhangbaninfo.add(strNewShareFlight);
                            }

                        }
                    }
                    airInfo = null;
                    String strlisthangban = "";
                    for (int o = 0; o < listhangbaninfo.size(); o++) {
                        strlisthangban += listhangbaninfo.get(o) + "@";
                    }
                    airInfo = strlisthangban.split("@");
                    for (int f = 0; f < airInfo.length; f++) {
                        if (airInfo[f].indexOf("**") > 0) {
                            airInfo[f - 1] = airInfo[f - 1] + "|" + airInfo[f];
                            airInfo[f] = "";
                        }
                    }
                    // 总记录数
                    int count = airInfo.length - 1;
                    // 定义航班信息中可以用到的字符串
                    String[] airAndCabin, CabinInfoArr, airDetails;
                    String airdetails; // 航班信息
                    String allCangWei; // 所有舱位信息字符串
                    String cabinCode = "";
                    String cabinNum = "";
                    for (int k = 1; k < count; k++)// 对每条航班信息进行拆分
                    {
                        // 航班信息类
                        FlightInfo flighInfo = new FlightInfo();
                        // 舱位信息类
                        CarbinInfo cabinInfo = null;
                        CarbinInfo lowCabinInfo = new CarbinInfo();
                        List<CarbinInfo> listCabinAll = new ArrayList<CarbinInfo>();
                        // 判断每条航班信息是否有效
                        if (airInfo[k].length() > 2) {
                            //判断是否是起飞当天的航班数据 02OCT(SAT) BJSDLC 
                            Pattern pndate = Pattern.compile("[0-9]{2}[A-Z]{3}[(][A-Z]{3}[)]\\s{0,}[A-Z]{6}");
                            Matcher mndate = pndate.matcher(airInfo[k]);
                            if (mndate.find()) {
                                if (mndate.group().toString().indexOf(ChangeDateMode(flightSearch.getFromDate())) == -1) {
                                    break;
                                }
                            }
                            if (airInfo[k].indexOf("AS#") < 0 && airInfo[k].indexOf("DS#") < 0
                                    && airInfo[k].indexOf(">") < 0) {
                                continue;
                            }
                            else if (airInfo[k].indexOf("PLEASE CHECK YI") > 0) {
                                continue;
                            }

                            airInfo[k] = airInfo[k].substring(2, airInfo[k].length());
                        }
                        if (airInfo[k].equals("") || airInfo[k].length() == 0) {
                            continue;
                        }
                        //以>分割航班信息和舱位信息，但是航班信息里面还存在部分舱位信息，已经存放在strFlightCabinTemp变量中
                        Pattern p = Pattern.compile("[\\>]");
                        airAndCabin = p.split(airInfo[k]);
                        airdetails = airAndCabin[0].trim(); // 航班基础信息
                        allCangWei = airAndCabin[1].trim(); // 所有舱位字符串
                        Pattern pDetails = Pattern.compile("\\s{1,2}");
                        airDetails = pDetails.split(airdetails); // 每条航班基础信息数组
                        strFlightCabinTemp = "";
                        for (int n = 2; n < airDetails.length; n++) {
                            if (airDetails[n].length() == 2) {
                                strFlightCabinTemp += airDetails[n].toString() + " ";
                            }
                        }
                        //取得城市对并判断行程是否正确
                        String regCitypairex = "[A-Z]{6}";
                        String strcitypairde = "";
                        Pattern pattcitypair = Pattern.compile(regCitypairex);
                        Matcher mcitypair = pattcitypair.matcher(airdetails);
                        if (mcitypair.find()) {
                            strcitypairde = mcitypair.group();
                        }
                        if (!strcitypairde
                                .equals(flightSearch.getStartAirportCode() + flightSearch.getEndAirportCode())) {
                            continue;
                        }

                        String strAirComCode = "";
                        // 对航班基础信息进行赋值
                        // 航空公司代码
                        String regEx = "([a-zA-Z]{2}|[0-9]{1}[a-zA-Z]{1})[0-9]{3,4}";
                        String strAirCompanyCode = "";
                        Pattern patt = Pattern.compile(regEx);
                        Matcher m = patt.matcher(airdetails);
                        if (m.find()) {
                            strAirCompanyCode = m.group();
                        }
                        //经停次数
                        String strJingTing = "0";
                        for (int u = 0; u < airDetails.length; u++) {
                            if (airDetails[u].trim().replace("^", "").length() == 1
                                    && IsNumberStr(airDetails[u].trim().replace("^", ""))) {
                                strJingTing = airDetails[u].trim().replace("^", "");
                            }
                            else if (airDetails[u].trim().indexOf("^") >= 0) {
                                try {
                                    //Pattern pstop=Pattern.compile("[^]");
                                    String[] strarrstop = airDetails[u].trim().replace("^", ",").split(",");
                                    if (strarrstop.length >= 1) {
                                        strJingTing = strarrstop[0];
                                    }
                                }
                                catch (Exception ex) {

                                }
                            }
                        }

                        //出发时间
                        String regExTime = "[0-9]{4}";
                        String strStratTime = "";
                        String strEndTime = "";
                        Pattern pattTime = Pattern.compile(regExTime);
                        Matcher mTime = pattTime.matcher(airdetails);
                        while (mTime.find()) {
                            strStratTime += mTime.group().toString() + ",";
                        }
                        //取得航空公司的代码，判断了一个是否是共享航班
                        if (strAirCompanyCode.indexOf("*") < 0) {
                            strAirComCode = strAirCompanyCode.substring(0, 2);
                        }
                        else {
                            continue;
                        }

                        Aircompany aircompany = new Aircompany();
                        //缓存读取航空公司名称 开始
                        aircompany = cachebasedata.getAirCompanyInfo(strAirComCode);
                        //缓存读取航空公司名称 结束
                        if (aircompany != null) {
                            //航班公司简称
                            String strAirCompanyName = aircompany.getAircomjname();
                            //航班公司简称
                            flighInfo.setAirCompanyName(strAirCompanyName);
                            // 航班号
                            flighInfo.setAirline(strAirCompanyCode);
                            // 出发机场三字码
                            flighInfo.setStartAirport(flightSearch.getStartAirportCode());
                            // 到达机场三字码
                            flighInfo.setEndAirport(flightSearch.getEndAirportCode());
                            // 航空公司代码
                            flighInfo.setAirCompany(strAirComCode);
                            //是否经停
                            if (!strJingTing.equals("0")) {
                                flighInfo.setStop(true);
                                flighInfo.setIsStopInfo(strJingTing);
                            }
                            else {
                                flighInfo.setStop(false);
                                flighInfo.setIsStopInfo("0");
                            }
                            //将匹配出的起飞时间和降落时间放到数组中
                            String[] strTimeArr = strStratTime.split(",");
                            //起飞时间
                            String strFlightDate = "";
                            //降落时间
                            String strArriveDate = "";
                            //如果截取出来的时间里面，带有航班编号 如：CA1321
                            if (strTimeArr.length == 3) {
                                strFlightDate = strTimeArr[1];
                                strArriveDate = strTimeArr[2];
                            }
                            //不带有带有航班编号，只有起降时间
                            else if (strTimeArr.length == 2) {
                                strFlightDate = strTimeArr[0];
                                strArriveDate = strTimeArr[1];
                            }
                            else {
                                strFlightDate = "0000";
                                strArriveDate = "0000";
                            }
                            // 出发时间
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmm");
                            Date startDate = dateFormat.parse(flightSearch.getFromDate() + " " + strFlightDate);
                            flighInfo.setDepartTime(new Timestamp(startDate.getTime()));
                            // 到达时间
                            Date arriveDate = dateFormat.parse(flightSearch.getFromDate() + " " + strArriveDate);
                            flighInfo.setArriveTime(new Timestamp(arriveDate.getTime()));
                            flighInfo.setIsShare(0);
                            // 机型
                            try {
                                if (airDetails[17] != null && airDetails[17].length() == 3
                                        && !airDetails[17].toString().equals("0^C")) {
                                    flighInfo.setAirplaneType(airDetails[17]);
                                }
                                else if (flighInfo.getAirplaneType() == null || flighInfo.getAirplaneType().equals("")) {
                                    String regFlightType = "[0-9]{3}";
                                    Pattern pattype = Pattern.compile(regFlightType);
                                    Matcher mftype = pattype.matcher(airDetails[18]);
                                    if (mftype.find()) {
                                        flighInfo.setAirplaneType(mftype.group());
                                    }
                                    else {
                                        Matcher mftype1 = pattype.matcher(airDetails[19]);
                                        if (mftype1.find()) {
                                            flighInfo.setAirplaneType(mftype1.group());
                                        }
                                        else {
                                            Matcher mftype2 = pattype.matcher(airDetails[16]);
                                            if (mftype2.find()) {
                                                flighInfo.setAirplaneType(mftype2.group());
                                            }
                                        }
                                    }
                                }
                            }
                            catch (Exception ex) {
                                flighInfo.setAirplaneType("737");
                            }
                            // 机型描述
                            List<Flightmodel> listModel = Server
                                    .getInstance()
                                    .getAirService()
                                    .findAllFlightmodel(
                                            "WHERE " + Flightmodel.COL_modelnum + "='" + flighInfo.getAirplaneType()
                                                    + "'", "", -1, 0);
                            if (listModel != null && listModel.size() > 0) {
                                String strTypeDesc = listModel.get(0).getModeldesc() + "#"
                                        + listModel.get(0).getPicpath();
                                flighInfo.setAirplaneTypeDesc(strTypeDesc);
                            }
                            // 起飞机场名称
                            List<Cityairport> listAirPort = Server
                                    .getInstance()
                                    .getAirService()
                                    .findAllCityairport(
                                            "WHERE " + Cityairport.COL_airportcode + " ='"
                                                    + flighInfo.getStartAirport() + "'", "", -1, 0);
                            if (listAirPort != null && listAirPort.size() > 0) {
                                String strSAirPort = listAirPort.get(0).getAirportname();
                                flighInfo.setStartAirportName(strSAirPort);
                                String strSCity = listAirPort.get(0).getCityname();
                                flighInfo.setStartAirportCity(strSCity);
                            }

                            // 到达机场名称
                            List<Cityairport> listEAirPort = Server
                                    .getInstance()
                                    .getAirService()
                                    .findAllCityairport(
                                            "WHERE " + Cityairport.COL_airportcode + " ='" + flighInfo.getEndAirport()
                                                    + "'", "", -1, 0);
                            if (listEAirPort != null && listEAirPort.size() > 0) {
                                String strEAirPort = listEAirPort.get(0).getAirportname();
                                flighInfo.setEndAirportName(strEAirPort);
                            }
                            /***************************直接从黑屏中取得舱位价格*****************************************************/
                            String strPriceInfo = "";

                            //FD指令取得全价价格
                            String strCityPair = flighInfo.getStartAirport() + flighInfo.getEndAirport();
                            //strPriceInfo=commandFunction2("FD:"+strCityPair+"/"+flighInfo.getAirCompany(), "","");
                            //建立缓存机制OSCache
                            strPriceInfo = cacheprice.getFDInfo(strCityPair + "_" + flighInfo.getAirCompany(),
                                    strCityPair, flighInfo.getAirCompany());
                            //建立缓存机制结束

                            if (strPriceInfo.indexOf("NO FARE AVAILABLE ON THIS SECTOR TODAY") >= 0) {
                                continue;
                            }
                            else if (strPriceInfo.indexOf("本日没有适用运价") >= 0) {
                                continue;
                            }

                            /*********************************直接从黑屏中FD取得舱位价格*************************************************/
                            // 燃油费/燃油费（成人）
                            List<Airfee> listAirFee = Server.getInstance().getAirService()
                                    .findAllAirfee("WHERE 1=1", "", -1, 0);
                            if (listAirFee != null && listAirFee.size() > 0) {
                                int intMiles = 0;
                                //燃油费
                                int intFuelFee = 0;
                                //机建费
                                int intAirportfee = 0;
                                //List<Airbaseprice> airbaseprice=Server.getInstance().getAirService().findAllAirbaseprice(" WHERE "+Airbaseprice.COL_aircompanycode+"='"+flighInfo.getAirCompany()+"' AND "+Airbaseprice.COL_sairportcode+"='"+flighInfo.getStartAirport()+"' AND "+Airbaseprice.COL_eairportcode+"='"+flighInfo.getEndAirport()+"'", "", -1, 0);
                                //								if(airbaseprice.size()>0)
                                //								{
                                //									
                                //								}
                                //								else
                                //								{
                                //									intFuelFee = listAirFee.get(0).getAdultfuelfee();
                                //								}
                                intMiles = GetFlightMiles(strCityPair, strPriceInfo);
                                //公里数是否大于800  燃油费
                                if (intMiles > 0 && intMiles < 800) {
                                    intFuelFee = listAirFee.get(0).getNearadultfuelfee();
                                }
                                else {
                                    intFuelFee = listAirFee.get(0).getAdultfuelfee();
                                }
                                //判断是否为支线航班  D38 AT7 CRJ ERJ EM4 DH8 YN2 
                                if (flighInfo.getAirplaneType().equals("EMB")
                                        || flighInfo.getAirplaneType().equals("D38")
                                        || flighInfo.getAirplaneType().equals("AT7")
                                        || flighInfo.getAirplaneType().equals("CRJ")
                                        || flighInfo.getAirplaneType().equals("ERJ")
                                        || flighInfo.getAirplaneType().equals("EM4")
                                        || flighInfo.getAirplaneType().equals("DH8")
                                        || flighInfo.getAirplaneType().equals("YN2")) {
                                    intAirportfee = listAirFee.get(0).getNearadultairpotyfee();
                                }
                                else {
                                    intAirportfee = listAirFee.get(0).getAdultairportfee();
                                }
                                flighInfo.setAirportFee(intAirportfee);
                                flighInfo.setFuelFee(intFuelFee);
                            }

                            //对舱位进行处理 还要截取航站楼和子舱位信息
                            Pattern pCabintotal = Pattern.compile("\\s{10,}");
                            String strCabinTotal = strFlightCabinTemp + allCangWei;
                            String[] strCabinTArr = pCabintotal.split(strCabinTotal);
                            String strCabinText = "";
                            String strHangzhanluo = "";
                            String strSubCabin = "";
                            String strHangzhanInfo = "";
                            String strHangzhanLuo1 = "";
                            String strHangzhanLuo2 = "";
                            if (strCabinTArr.length == 2) {
                                strCabinText = strCabinTArr[0];
                                strHangzhanluo = strCabinTArr[1];
                                //如果存在子舱位
                                try {
                                    if (strHangzhanluo.length() > 0 && strHangzhanluo.indexOf("**") > 0) {
                                        String[] strHangZhanArr = strHangzhanluo.split("**");
                                        if (strHangZhanArr.length == 2) {
                                            strHangzhanInfo = strHangZhanArr[0];
                                            strSubCabin = strHangZhanArr[1];
                                            strCabinText += "" + strSubCabin;
                                            Pattern pHangzhan1 = Pattern.compile("\\s");
                                            String[] strHangZhanArr2 = pHangzhan1.split(strHangzhanInfo);
                                            if (strHangZhanArr2.length == 2) {
                                                strHangzhanLuo1 = strHangZhanArr2[0];
                                                strHangzhanLuo2 = strHangZhanArr2[1];
                                            }
                                        }
                                    }

                                    else {
                                        Pattern pHangzhan = Pattern.compile("\\s");
                                        String[] strHangZhanArr1 = pHangzhan.split(strHangzhanluo);
                                        if (strHangZhanArr1.length == 2) {
                                            strHangzhanLuo1 = strHangZhanArr1[0];
                                            strHangzhanLuo2 = strHangZhanArr1[1];
                                        }
                                    }
                                }
                                catch (Exception ex) {
                                    System.out.println("航站楼解析出错：" + strHangzhanluo);
                                }
                            }
                            else if (strCabinTArr.length == 1) {
                                strCabinText = strCabinTArr[0];
                            }
                            else if (strCabinTArr.length == 3) {
                                strCabinText = strCabinTArr[0];
                                strHangzhanluo = strCabinTArr[1];
                                strSubCabin = strCabinTArr[2];
                                if (strSubCabin.length() > 0) {
                                    strSubCabin = strSubCabin.replace("**", "");
                                    strCabinText += "" + strSubCabin;
                                }

                            }

                            Pattern pCabin = Pattern.compile("\\s");
                            CabinInfoArr = pCabin.split(strCabinText);
                            Float tmpDiscount = (float) 2.0;
                            Float temPrice = 0f;
                            int cabinIndex = 0;
                            int typei = -1;
                            Float fDiscount = 0f;
                            Float fpricetemp = 0f;
                            for (int i = 0; i < CabinInfoArr.length - 1; i++) {
                                if (CabinInfoArr[i].equals("")) {
                                    continue;
                                }
                                cabinInfo = new CarbinInfo();
                                // 舱位码
                                if (CabinInfoArr[i].length() == 2) {
                                    cabinCode = CabinInfoArr[i].substring(0, 1);
                                    cabinNum = CabinInfoArr[i].substring(1, CabinInfoArr[i].toString().length());
                                }
                                else if (CabinInfoArr[i].length() == 3) {
                                    cabinCode = CabinInfoArr[i].substring(0, 2);
                                    cabinNum = CabinInfoArr[i].substring(2, CabinInfoArr[i].toString().length());
                                }

                                //共享航班号
                                if (CabinInfoArr[i].length() == 5 || CabinInfoArr[i].length() == 6) {
                                    String regExshare = "[a-zA-Z]{2}[0-9]{3,4}";
                                    Pattern pattshare = Pattern.compile(regExshare);
                                    Matcher mshare = patt.matcher(CabinInfoArr[i]);
                                    if (mshare.find()) {
                                        flighInfo.setShareFlightNumber(mshare.group());
                                        flighInfo.setIsShare(1);
                                    }
                                }
                                if (cabinNum.equals("A") || isNumber(cabinNum)) {
                                    cabinInfo.setCabin(cabinCode);
                                    // 剩余座位数
                                    String strCabinNum = "";
                                    if (isNumber(cabinNum)) {
                                        strCabinNum = cabinNum;
                                    }
                                    else if (cabinNum.equals("A")) {
                                        strCabinNum = "9";
                                    }
                                    else {
                                        strCabinNum = "0";
                                    }
                                    cabinInfo.setSeatNum(strCabinNum);

                                    // 是否特价，因为取不到特价信息所有，暂时都设为false
                                    cabinInfo.setSpecial(false);

                                    //读取缓存舱位信息 开始
                                    Cabin cabin = new Cabin();
                                    //cabin=cachebasedata.getCabinInfo(flighInfo.getAirCompany()+"_"+cabinInfo.getCabin(),flighInfo.getAirCompany().toString(),cabinInfo.getCabin().toString());
                                    //读取缓存舱位信息 结束
                                    if (1 == 1) {
                                        try {
                                            // 根据舱位折扣计算舱位价格(四舍五入)

                                            String strCabinPrice = ""; //String.format("%d", intCabinPrice);

                                            Pattern patternprice = Pattern.compile(flighInfo.getAirCompany() + "/"
                                                    + cabinInfo.getCabin());
                                            // 以换行符将字符串拆分
                                            String[] strpriceinfoarr = patternprice.split(strPriceInfo);
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
                                                            strCabinPrice = strtempcab[0].trim();
                                                        }
                                                    }
                                                    else {
                                                        strCabinPrice = strTruePriceinfo;
                                                    }
                                                }
                                            }

                                            if (strCabinPrice.equals("")) {
                                                strCabinPrice = "0";
                                                continue;
                                            }
                                            cabinInfo.setPrice(Float.parseFloat(strCabinPrice));
                                            fpricetemp = cabinInfo.getPrice();
                                            if (i == 0) {
                                                temPrice = cabinInfo.getPrice();
                                            }
                                            if (cabinInfo.getCabin().equals("Y")) {
                                                flighInfo.setYPrice((int) Double.parseDouble(strCabinPrice));
                                            }
                                            else {
                                                Pattern patternprice1 = Pattern.compile(flighInfo.getAirCompany()
                                                        + "/Y");
                                                // 以换行符将字符串拆分
                                                String[] strpriceinfoarr1 = patternprice1.split(strPriceInfo);
                                                if (strpriceinfoarr1.length >= 2) {
                                                    String strPPricecabin1 = strpriceinfoarr1[1].toString();
                                                    Pattern patcab1 = Pattern.compile("\\/");
                                                    String[] strParr1 = patcab1.split(strPPricecabin1);
                                                    String strTruePriceinfo1 = "";
                                                    if (strParr1.length >= 2) {
                                                        strTruePriceinfo1 = strParr1[1].toString().trim();
                                                        if (strTruePriceinfo1.indexOf("=") >= 0) {
                                                            Pattern patcabtemp1 = Pattern.compile("=");
                                                            String[] strtempcab1 = patcabtemp1.split(strTruePriceinfo1);
                                                            if (strtempcab1.length == 2) {
                                                                flighInfo.setYPrice((int) Double
                                                                        .parseDouble(strtempcab1[0].trim()));
                                                            }
                                                        }
                                                        else {
                                                            flighInfo.setYPrice((int) Double
                                                                    .parseDouble(strTruePriceinfo1));
                                                        }
                                                    }
                                                }
                                            }
                                            String strCabinRules = cabin.getRefundrule();
                                            cabinInfo.setCabinRules(strCabinRules);
                                            //计算折扣信息
                                            try {
                                                float ddiscount = cabinInfo.getPrice() / flighInfo.getYPrice() * 100;
                                                float mdiscount = (float) Math.round(ddiscount);
                                                cabinInfo.setDiscount(Float.parseFloat(formatMoney(mdiscount)));
                                                fDiscount = cabinInfo.getDiscount();

                                            }
                                            catch (Exception ex) {
                                                fDiscount = 0f;
                                            }
                                            if (cabin.getCabintypename() != null) {
                                                cabinInfo.setCabintypename(cabin.getCabintypename());
                                            }
                                            if (cabinInfo.getPrice() != null && cabinInfo.getPrice() > 0) {
                                                listCabinAll.add(cabinInfo);
                                            }
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }

                            int newcabinindex = 0;
                            float fcabtemp = 0;
                            try {
                                fcabtemp = listCabinAll.get(0).getPrice();
                            }
                            catch (Exception ex) {
                                fcabtemp = 0;
                            }
                            for (int i = 1; i < listCabinAll.size(); i++) {
                                if (fcabtemp > listCabinAll.get(i).getPrice()) {
                                    fcabtemp = listCabinAll.get(i).getPrice();
                                    cabinIndex = i;

                                }
                            }
                            System.out.println("本航班的最低价格index是：" + cabinIndex);
                            //cabinIndex=listCabinAll.size()-1;
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
                                if (tempCabinInfo.getCabinRules() != null) {
                                    lowCabinInfo.setCabinRules(tempCabinInfo.getCabinRules());
                                }
                                else {
                                    lowCabinInfo.setCabinRules("暂无");
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

                        }
                    }

                }
            }
            else {
                System.out.println("查询航班失败，请重试！");
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        //listFlightInfoAll=removeDuplicateWithOrder(listFlightInfoAll);

        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        for (int q = 0; q < listFlightInfoAll.size(); q++) {
            //如果PN分页查询出的航班信息有重复的过滤掉
            if (!flightalltemp.contains(listFlightInfoAll.get(q)) && listFlightInfoAll.get(q).getIsShare() == 0) {
                flightalltemp.add(listFlightInfoAll.get(q));
            }

        }
        //		for(int p=0;p<listFlightInfoAll.size();p++)
        //		{
        //				//如果PN分页查询出的航班信息有重复的过滤掉
        //				flightalltemp.add(listFlightInfoAll.get(p));
        //		}
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
     * @param strFDinfo FD价格信息
     * @param strCityPair 城市对
     * @return 公里数
     * >PFDXIYINC/CA    b
        FD:XIYINC/25JUL11/CA                   /CNY /TPM  560 / 
        01 CA/F       /   900.00=  1800.00/F/F/  /   .   /01APR11        /FC1C 
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
            //循环遍历字符串			
            if (Character.isDigit(str.charAt(i))) {
                //用char包装类中的判断数字的方法判断每一个字符				
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

    /** 通用命令查询接口方法
    * @param strCmd 黑屏命令
    * @return StrReturn  黑屏返回结果
    * Author:HTHY
    */
    public String commandFunction(String strcmd, String strPageinfo) {
        //接口地址
        String strUrl = ipbigAddress;
        //命令参数
        String strCmd = "?cmd=";

        //翻页命令
        String strPage = strPageinfo;
        //黑屏查询指令：
        strCmd += getBASE64(strcmd);
        //清楚缓存数据
        String strIG = "$IG";

        String strReturn = "";
        //接口调用地址和命令参数
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
                //返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
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

    /** 通用命令查询接口方法
    * @param strCmd 黑屏命令
    * @return StrReturn  黑屏返回结果
    * Author:HTHY
    */
    public String commandFunction2(String strcmd, String strPageinfo, String strIG) {
        //接口地址
        String strUrl = this.ipAddress;
        //命令参数
        String strCmd = "?cmd=";

        //翻页命令
        String strPage = strPageinfo;
        //黑屏查询指令：
        strCmd += getBASE64(strcmd);
        //清楚缓存数据

        String strReturn = "";
        //接口调用地址和命令参数
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
                //返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
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

    /** 通用命令查询接口方法
     * @param strCmd 黑屏命令
     * @return StrReturn  黑屏返回结果
     * Author:HTHY
     */
    public String commandFunctionHK(String strcmd, String strPageinfo, String strIG) {
        //接口地址
        String strUrl = this.ipAddress;
        //命令参数
        String strCmd = "?cmd=";

        //翻页命令
        String strPage = strPageinfo;
        System.out.println("no base64:---" + strcmd);
        //黑屏查询指令：
        strCmd += getBASE64(strcmd);
        //清楚缓存数据

        String strReturn = "";
        //接口调用地址和命令参数
        String totalurl = strUrl + strCmd + strPage + strIG;
        System.out.println("===============×××××××××××url:" + totalurl);
        java.io.InputStream in = null;
        try {
            java.net.URL Url = new java.net.URL(totalurl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            //conn.connect();
            in = conn.getInputStream();
            org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
            org.jdom.Document doc = build.build(in);
            org.jdom.Element data = doc.getRootElement();
            List<Element> muticmdlist = data.getChildren("CMDLIST");
            int i = 0;
            for (Element cmdlist : muticmdlist) {
                i++;
                String status = cmdlist.getChildTextTrim("STATUS");
                //返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
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

    /** 通用命令查询接口方法
     * @param strCmd 黑屏命令
     * @return StrReturn  黑屏返回结果,只返回第一条黑屏指令
     * Author:HTHY
     */
    public String commandFunction3(String strcmd, String strPageinfo, String strIG) {
        //接口地址
        String strUrl = this.ipAddress;
        //命令参数
        String strCmd = "?cmd=";

        //翻页命令
        String strPage = strPageinfo;
        //黑屏查询指令：
        strCmd += getBASE64(strcmd);
        //清楚缓存数据

        String strReturn = "";
        //接口调用地址和命令参数
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
                //返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
                if (status != null && status.equals("0")) {
                    strReturn = cmdlist.getChildTextTrim("MESSAGE");
                }
                else {
                    if (i == 1) {
                        strReturn = cmdlist.getChildTextTrim("RESPONSE");
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
     * 创建PNR信息
     * @param SS 航段组
     * @return NM 姓名组
     * @param CT 联系方式
     * @return TK 出票时限
     * @param NUM 乘机人数
     * @return depTime 出发时间
     * @param SSR 证件信息
     * @return PNR
     * Author:HTHY
     */
    public String CreatePnr(String SS, String NM, String CT, String TK, String NUM, String depTime, String SSR) {
        return "RMFC2";
    }

    /**
     * 取消PNR功能
     * @param strPNR PNR编码
     * @return
     */
    public String XEPNR(String strPNR) {
        String strReturn = "";
        if (!strPNR.equals("NOPNR")) {
            strReturn = commandFunction2("RT" + strPNR + "$XEPNR@", "", "");
        }
        return strReturn;
    }

    /**
     * 分离PNR功能
     * @param strPNR PNR编码
     * @param strNumber  要分离乘机人所在序号，必须要与黑屏中的序号一致
     * @return
     */
    public String SPPNR(String strPNR, String strNumber) {
        String strReturn = "";
        if (!strPNR.equals("NOPNR")) {
            strReturn = commandFunction2("RT" + strPNR + "$SP:" + strNumber + "$@", "", "");
        }
        return strReturn;
    }

    /**
     * Q信箱查询
     * @param strPNR PNR编码
     * @param strNumber  要分离乘机人所在序号，必须要与黑屏中的序号一致
     * @return
     */
    public String QMailQT() {
        String strReturn = "";
        strReturn = commandFunction2("QT", "", "");
        return strReturn;
    }

    /**
     * 处理某种Q信箱
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQS(String strQueryType) {
        String strReturn = "";
        //处理完要释放结束原来的处理
        strReturn = commandFunction3("QS:" + strQueryType, "", "");
        return strReturn;
    }

    /**
     * 释放Q信箱
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQNE() {
        String strReturn = "";
        //处理完要释放结束原来的处理
        strReturn = commandFunction3("QNE", "", "");
        return strReturn;
    }

    /**
     * 释放当前处理下一个
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQN() {
        String strReturn = "";
        //处理完要释放结束原来的处理
        strReturn = commandFunction3("QD", "", "");
        return strReturn;
    }

    /**
     * 调用51book接口生成PNR
     * @param listsegmenginf  //航程信息类
     * @param listpassengers  //乘机人类
     * @param strCustomerCode //大客户编码
     * @return
     */
    public String CreatePNRByInterFace(List<Segmentinfo> listsegment, List<Passenger> listpassenger,
            String strCustomerCode) {
        String strPNRinfo = "";//返回值
        String pninfo = "";
        String fltinfo = "";

        //航班信息提取
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
        //乘机人信息提取
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
        java.io.InputStream in = null;
        String url = "http://service2.travel-data.cn/ToolsService.asmx/GetPNR?";
        url += "u=" + "6ed3fc98f2da5089a6144598a6a320e3";//用户ID
        url += "&Pninfo=" + pninfo;//乘机人信息串
        url += "&Fltinfo=" + fltinfo;//航班信息串
        java.net.URL Url;
        try {
            Url = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            in = conn.getInputStream();
            SAXBuilder build = new SAXBuilder();
            Document doc = build.build(in);
            Element lists = doc.getRootElement();

            List str = lists.getChildren();
            /**因为返回数据就三条固定记录 所以就不用循环了 直接取**/
            Element pnr1 = (Element) str.get(0);//得到的是是否有记录，true和false
            Element pnr2 = (Element) str.get(1);//得到的是错误信息
            Element pnr3 = (Element) str.get(2);//得到PNR字符串

            if (pnr1.getText().equals("true")) {
                strPNRinfo = pnr3.getText();//传入PNR数据
            }
            else {
                strPNRinfo = pnr2.getText(); //返回错误信息
            }

            in.close();
            conn.disconnect();

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(strPNRinfo + "返回数据内容");
        return strPNRinfo;
    }

    /**
     * 在黑屏中生成PNR
     * @param listsegmenginf  //航程信息类
     * @param listpassengers  //乘机人类
     * @param strCustomerCode //大客户编码
     * @return
     */
    public String CreatePNRByCmd(List<Segmentinfo> listsegmenginf, List<Passenger> listpassengers,
            String strCustomerCode) {
        String strRetrun = "";
        String SS = ""; // 航段组
        String NM = "NM"; // 姓名组
        String SSR = ""; // 证件组
        String CT = "CT " + this.officeNumber.substring(0, 3) + "/" + this.username + "\r"; // 联系方式
        String strDDate = "";
        // 出票时限
        // ChangeDateMode

        String TKTL = ""; // 出票时限
        String FK = "\\"; // 封口
        String TKTLTime = "2000"; // 出票时限具体时间，应该为起飞前半个小时
        String strAirCompany = listsegmenginf.get(0).getAircomapnycode().toString();
        // 判断单程还是往返,单程
        for (int j = 1; j <= listsegmenginf.size(); j++) {
            // 出票时限
            strDDate = ChangeDateMode(listsegmenginf.get(0).getDeparttime().toString());
            // 取得出票时限具体时间
            TKTLTime = formatTimestampPID(dateToTimestamp(GetMintue(listsegmenginf.get(0).getDeparttime().toString(),
                    -120)));
            System.out.println("出票时限日期:" + strDDate);
            System.out.println("出票时限时间:" + TKTLTime);
            if (j == 1) {
                TKTL = "TKTL" + TKTLTime + "/" + strDDate + "/" + this.officeNumber + "\r"; // 出票时限
                System.out.println("出票时限日期字符串:" + TKTL);
            }
            // 单程
            if (j == 1) {
                // 航段组
                // 子舱位 预订SS:HU7238 M 06MAY XIYPEK 1
                if (listsegmenginf.get(0).getCabincode().length() == 2) {
                    SS = "SS:" + listsegmenginf.get(0).getFlightnumber() + " "
                            + listsegmenginf.get(0).getCabincode().substring(0, 1) + " "
                            + ChangeDateMode(listsegmenginf.get(0).getDeparttime().toString()) + " "
                            + listsegmenginf.get(0).getStartairport() + listsegmenginf.get(0).getEndairport() + " "
                            + listsegmenginf.get(0).getCabincode().substring(1, 2) + "\r";
                }
                else {
                    SS = "SS " + listsegmenginf.get(0).getFlightnumber() + "/" + listsegmenginf.get(0).getCabincode()
                            + "/" + ChangeDateMode(listsegmenginf.get(0).getDeparttime().toString()) + "/"
                            + listsegmenginf.get(0).getStartairport() + listsegmenginf.get(0).getEndairport() + "/NN"
                            + listpassengers.size() + "/" + formatTimestampPID(listsegmenginf.get(0).getDeparttime())
                            + " " + formatTimestampPID(listsegmenginf.get(0).getArrivaltime()) + "\r";
                }

            }
            // 往返或联程
            if (j == 2) {
                // 出票时限
                strDDate = ChangeDateMode(GetDate(listsegmenginf.get(1).getDeparttime().toString(), -1));
                // TKTL="TKTL/2000/"+strDDate+"/"+this.officeNumber+"$"; //出票时限
                SS += "SS " + listsegmenginf.get(1).getFlightnumber() + "/" + listsegmenginf.get(1).getCabincode()
                        + "/" + ChangeDateMode(listsegmenginf.get(1).getDeparttime().toString()) + "/"
                        + listsegmenginf.get(1).getStartairport() + listsegmenginf.get(1).getEndairport() + "/NN"
                        + listpassengers.size() + "\r";
            }

        }
        int index = 0;
        for (int i = 0; i < listpassengers.size(); i++) {
            index++;
            String strPassName = "";
            strPassName = listpassengers.get(i).getName();
            System.out.println("*****&&&&&&&&**********Name:" + strPassName);
            // 如果是儿童
            if (listpassengers.get(i).getPtype() == 2) {
                String strBirthDay = listpassengers.get(i).getIdnumber().toString();
                if (!strAirCompany.equals("CZ")) {
                    strPassName = listpassengers.get(i).getName() + "CHD";
                }
                else {
                    strPassName = listpassengers.get(i).getName();
                }

            }
            if (listpassengers.get(i).getPtype() == 3) {
                strPassName = listpassengers.get(i).getName() + "INF";
            }

            // 姓名组
            NM += "1" + strPassName;
            // 证件组
            if (strAirCompany.equals("CZ") && listpassengers.get(0).getPtype() == 2) {
                String strbirthday = "";
                strbirthday = ChangeDateModeYear(listpassengers.get(i).getBirthday());
                SSR += "SSR CHLD " + listsegmenginf.get(0).getAircomapnycode() + " HK1/" + strbirthday + "/P" + index
                        + "\r";
            }
            else {
                SSR += "SSR FOID " + listsegmenginf.get(0).getAircomapnycode() + " HK/NI"
                        + listpassengers.get(i).getIdnumber() + "/P" + index + "\r";
            }
        }
        NM += "\r";
        // 如果乘机人类型是儿童，则备注成人PNR编码
        if (listpassengers.get(0).getPtype() == 2) {
            SSR += "SSR OTHS " + listsegmenginf.get(0).getAircomapnycode() + " ADULT PNR IS " + strCustomerCode + "\r";
        }
        String CustomerCodestr = "";
        // 判断是否是大客户，如果是大客户
        // 取得预订航班的航空公司

        // if(!strCustomerCode.equals(",") && strCustomerCode.trim().length()>3)
        // {
        // String[] strarr=strCustomerCode.split(",");
        // if(strAirCompany.equals("MU"))
        // {
        // if(strarr.length>=1)
        // {
        // if(strarr[0]!=null && !strarr[0].equals(""))
        // {
        // CustomerCodestr="FP:CASH,CNY/*"+strarr[0]+"\r";
        // }
        // }
        // }
        // else if(strAirCompany.equals("CA"))
        // {
        // if(strarr.length>=2)
        // {
        // if(strarr[1]!=null && !strarr[1].equals(""))
        // {
        // CustomerCodestr="FP CASH,CNY/*"+strCustomerCode+"\rSSR OTHS CA
        // "+strarr[1]+"\r";
        // }
        // }
        // }
        // else if(strAirCompany.equals("CZ"))
        // {
        // if(strarr.length>=4)
        // {
        // if(strarr[2]!=null && !strarr[2].equals(""))
        // {
        // CustomerCodestr="RMK IC CZ/"+strarr[2]+"\r";
        // }
        // }
        // }
        // }

        String strcmd = "IG$" + SS + NM + SSR + CT + TKTL + CustomerCodestr + FK;

        strRetrun = commandFunctionHK(strcmd, "", "");
        writeLog.write("生成PNR日志", "创建PNR返回字符串:" + strcmd);
        // 对返回值进行解析
        // 以换行符将字符串拆分
        Pattern pattern = Pattern.compile("[\\r\\n]");
        String[] strReturnarr = pattern.split(strRetrun);
        for (int i = 0; i < strReturnarr.length; i++) {
            if (strReturnarr[i].toString().indexOf("UNABLE TO SELL") >= 0) {
                strRetrun = "NOPNR";
            }
            else if (strReturnarr[i].toString().indexOf("TKT TIME LIMIT, PLEASE CHECK PNR") >= 0) {
                if (strReturnarr[i].toString().indexOf("-") >= 0) {
                    String[] strPNRarr = strReturnarr[i].toString().split("-");
                    String strPNR = strPNRarr[0];
                    strRetrun = strPNR;
                    break;
                }
            }
            else if (strReturnarr[i].toString().indexOf("EOT SUCCESSFUL, BUT ASR UNUSED FOR 1 OR MORE SEGMENTS") >= 0) {
                if (strReturnarr[i].toString().indexOf("-") >= 0) {
                    String[] strPNRarr = strReturnarr[i].toString().split("-");
                    String strPNR = strPNRarr[0];
                    strRetrun = strPNR;
                    break;
                }
            }
            else if (strReturnarr[i].toString().indexOf("航空公司使用自动出票时限") >= 0) {
                if (strReturnarr[i].toString().indexOf("-") >= 0) {
                    String[] strPNRarr = strReturnarr[i].toString().split("-");
                    String strPNR = strPNRarr[0];
                    if (strPNR.length() > 6) {
                        Pattern patternnew = Pattern.compile("\\s{1,}");
                        String[] strReturnarrnew = patternnew.split(strPNR);
                        strPNR = strReturnarrnew[strReturnarrnew.length - 1].toString();
                        strRetrun = strPNR;
                        break;
                    }
                    else {
                        strRetrun = strPNR;
                        break;
                    }

                }
            }
            else if (strReturnarr[i].toString().trim().length() == 6) {
                strRetrun = strReturnarr[i].trim();
            }
            else {
                strRetrun = "NOPNR";
            }
        }
        // strRetrun="123456";
        if (strRetrun.equals("NOPNR")) {
            commandFunction2("IG", "", "");
        }
        writeLog.write("生成PNR日志", "生成PNR编码:" + strRetrun.trim());
        return strRetrun.trim();

    }

    //转化时间格式 HH:MM
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
        String strFinalStr = commandFunction("RT" + strPNR + "$PN$PN$PAT:A", "");
        return formatHTML(strFinalStr);
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
        String strFinalStr = commandFunction2("RT" + strPNR + "$PN$PN", "", "");

        Pattern pbigPnr = Pattern.compile("\\n");
        String[] strBigArr = pbigPnr.split(strFinalStr);
        if (strBigArr.length > 0) {
            for (int i = 0; i < strBigArr.length; i++) {
                String strreg = "[0-9]{1,}[\\.][\\s]{0,}[R][M][K][\\s]{0,}[C][A][/][\\w]{5,6}";
                Pattern pattFlight = Pattern.compile(strreg);
                Matcher mFlight = pattFlight.matcher(strBigArr[i].trim());
                if (mFlight.find()) {
                    if (strBigArr[i].toString().indexOf("/") >= 0) {
                        String[] strBigPnrinfo = strBigArr[i].split("/");
                        if (strBigPnrinfo.length >= 2) {
                            strBigPNR = strBigPnrinfo[1].trim();
                            if (strBigPNR.length() > 6) {
                                Pattern pbigPnr1 = Pattern.compile("\\s");
                                String[] strBigPNRTemp = pbigPnr1.split(strBigPNR);
                                if (strBigPNRTemp.length >= 1) {
                                    strBigPNR = strBigPNRTemp[0].trim();
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return strBigPNR.replace("+", "").trim();
    }

    /*
     * 根据PNR取得票号信息
     */
    public String getTicketNumber(String strPnrInfo, String strPnmber) {
        //		M DETR:CN/YP0DC/C
        //		 DETR:TN/898-1654160863  	             NAME:朱君
        //		    FOID:NI340702198401027526               JD5128 /29OCT10/LJGNKG FLOW 
        //		 DETR:TN/898-1654160862  	             NAME:王云飞  
        //		    FOID:NI142201198304139017               JD5128 /29OCT10/LJGNKG FLOW 
        //		 DETR:TN/880-1708466876  	             NAME:罗招贵  
        //		    FOID:RP1055981178                       HU7113 /14NOV10/CANNKG OPEN 
        //		 DETR:TN/880-1708466878  	             NAME:张晶晶  
        //		    FOID:RP1055981180                       HU7113 /14NOV10/CANNKG OPEN 
        //		 DETR:TN/880-1708466877  	             NAME:宋智
        //		    FOID:RP1055981179                       HU7113 /14NOV10/CANNKG OPEN 
        //		END OF SELECTION LIST
        //SSR TKNE SC HK1 NKGKMG 4695 H25NOV 3241710452688/1/P1

        //如果记录已经取消 票号格式是：TN/781-2365546732/P1 
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
            //只根据TN/来取得票号
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
            //如果记录已经取消 票号格式是：TN/781-2365546732/P1
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
            //
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
        String tongdaoCode;

        // 进行rtPNR的操作，并且查看其返回的结果，如果正确，则进行下一步，如果不正确释放资源
        String rtpnrstr = commandFunction2("RT" + pnrcode, "", "");
        int i;
        i = rtpnrstr.indexOf("     +"); // 判断是否有+
        if (i != -1)// 还有next page
        {
            rtpnrstr = commandFunction2("PN", "", "");
        }
        if (!checkPnrReturn(rtpnrstr)) {
            rtpnrstr = commandFunction2("IG", "", "");
            return "自动出票提取PNR失败！";
        }
        String XeRrStr = getXeRrCmd(rtpnrstr);
        String Carrier = getCarrier(rtpnrstr);
        // 发送xe rr命令
        rtpnrstr = commandFunction2(XeRrStr, "", "");
        rtpnrstr = commandFunction2("RT" + pnrcode, "", "");
        String pnstr;
        pnstr = rtpnrstr;
        while (1 == 1) {
            i = pnstr.indexOf("     +");
            if (i != -1)// 还有next page
            {
                pnstr = commandFunction2("PN", "", "");
                rtpnrstr = rtpnrstr + pnstr;
                continue;
            }
            else {
                break;
            }
        }
        if ((CheckFNFCFP(rtpnrstr)) && (checkRRTK(rtpnrstr))) {
            // rt返回的数值应该包含fp fn fc项,如果包含,则说明
            if (strEI != "")// 查看是否有Ei
            {
                rtpnrstr = commandFunction2("EI:" + strEI, "", "IG");
            }
            rtpnrstr = commandFunction2(strEtdz, "", "IG");
            // 反提PNR，获得票号信息
            rtpnrstr = commandFunction2(strPNR, "", "");
            // pnstr;
            pnstr = rtpnrstr;
            while (1 == 1) {
                i = pnstr.indexOf("     +");
                if (i != -1)// 还有next page
                {
                    pnstr = commandFunction2("PN", "", "");
                    rtpnrstr = rtpnrstr + pnstr;
                    continue;
                }
                else {
                    break;
                }
            }
            String ticketNo = getTicketNo(rtpnrstr);
            // 释放资源
            rtpnrstr = commandFunction2("IG", "", "");
            return ticketNo;
        }
        else {
            // rrXE不成功
            rtpnrstr = commandFunction2("IG", "", "");
            return "Err08";
        }
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
        //String regEx = "\\*{0,1}[0..9,A-Z]{2}\\d{3,4}\\s{2,3}\\w{2}[#,\\*](\\s[A-Z][\\w])*\\s{2,5}\\w{6}\\s\\d{4}\\s{3}\\d{4}(\\+1)*\\s{1,3}\\w{3}\\s\\d[\\^,\\s]\\w{0,1}\\s{2,3}\\w\\s{2}\\>\\s{3}([0..9,A-Z]{2}\\d{3,4})*(\\s){6,11}(\\s[A-Z][\\w])*";
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
     * @param strIn PNR信息
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
        //2009-03-19
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
        //2009-03-19
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
        //通过接口获取pnr
        //String pnrstr=getPNRInfo(pnr);
        //通过接口获取的pnr字符串
        String pnrstr = "?<Qp>> 7o <0 **ELECTRONIC TICKET PNR** <br> 1.YIXUAN/SHICHD XP8VR <br> 2. FM9311 Y FR19MAR SHACAN RR1 0930 1130 E <br> 3.FUO/T FUO/T 0757-82263555/FUO SHUN AN DA AIR SERVICE CO.,LTD/CHEN MING JUN <br> ABCDEFG <br> 4.SHISHAN1 <br> 5.0757-86688155 MEI 13535661430<br> 6.T<br> 7.SSR FOID <br> 8.SSR ADTK 1E BY FUO17MAR10/0930 OR CXL FM9311 Y19MAR <br> 9.SSR TKNE FM HK1 SHACAN 9311 Y19MAR 7743868306688/1/P1<br>10.RMK AUTOMATIC FARE QUOTE <br>11.RMK CA/K7231 <br>12.FN/A/FCNY640.00/SCNY640.00/C3.00/XCNY30.00/TEXEMPTCN/TCNY30.00YQ/ACNY670.00 <br>13.TN/774-3868306688/P1 <br>14.FP/CASH,CNY <br>j<Qp>> 7o <015.FUO112 <br> - ";

        //截取pnr信息
        /*
         * int personNumber=0;
        String nm="";
        
        String[] patRow = pnrstr.split("[P][A][T]");  //用PAT将PNR分为PatRow数组
        String[] rows = patRow[0].split("[0-9]{1,}[///.]");
        String strPersons =pnrstr.replace(pnr, "#").split("#")[0].replace("<br>", "");
        String[] person = strPersons.split("[0-9]{1,}[///.]");
        for (int j = 1; j < person.length; j++) {
        	if (person[j].trim().length() > 0) {
        		personNumber = personNumber + 1;
        		nm += person[j].trim() + "#"; // 每个乘机人姓名后面加一个#
        	}
        }
        for(int i=0;i<rows.length;i++)
        {
        	System.out.println(rows[i]);
        }
        for(int j=0;j<person.length;j++)
        {
        	System.out.println(person[j]);
        }
        System.out.println(personNumber+nm);
        */
        //添加行程单
        List<Segmentinfo> listSegmentinfo = new ArrayList<Segmentinfo>();
        Segmentinfo segmentinfo = new Segmentinfo();
        segmentinfo.setAircomapnycode("CA");
        Segmentinfo segmentinfo2 = new Segmentinfo();
        segmentinfo2.setAircomapnycode("CA");
        listSegmentinfo.add(segmentinfo);
        listSegmentinfo.add(segmentinfo2);
        //添加订单
        List<Orderinfo> listOrderinfo = new ArrayList<Orderinfo>();
        Orderinfo orderinfo = new Orderinfo();
        orderinfo.setAddresa("beijing");
        Orderinfo orderinfo2 = new Orderinfo();
        orderinfo2.setAddresa("beijing");
        listOrderinfo.add(orderinfo);
        listOrderinfo.add(orderinfo2);
        //添加乘机人信息	
        List<Passenger> listPassenger = new ArrayList<Passenger>();
        Passenger passenger = new Passenger();
        passenger.setName("bian");
        Passenger passenger2 = new Passenger();
        passenger2.setName("bian");
        listPassenger.add(passenger);
        listPassenger.add(passenger2);
        //添加pnr到list里面
        list.add(pnrstr);
        //将行程单订单乘机人信息添加到list里面
        list.add(listSegmentinfo);
        list.add(listOrderinfo);
        list.add(listPassenger);
        //返回list
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
        //航班线路类
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
        //航班线路详细信息
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
        //航班信息
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
        //第二条航班
        AllFlight allflight1 = new AllFlight();
        allflight1.setNo(2);
        allflight1.setFromCity("BJS");
        allflight1.setDestCity("SFO");
        allflight1.setAirCompany("DL");
        allflight1.setFromCity("2010-05-24 11:30");
        allflight1.setToDate("2010-05-25 14:15");
        allflight1.setFlightNumber("DL526");
        listAllFlight.add(allflight1);

        //第三条航班
        AllFlight allflight2 = new AllFlight();
        allflight2.setNo(3);
        allflight2.setFromCity("BJS");
        allflight2.setDestCity("SFO");
        allflight2.setAirCompany("DL");
        allflight2.setFromCity("2010-05-24 14:30");
        allflight2.setToDate("2010-05-25 16:15");
        allflight2.setFlightNumber("DL111");
        listAllFlight.add(allflight2);

        //第三条航班
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

        //第二条航线
        //航班线路类
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
        //航班线路详细信息
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
        //航班信息
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
        //第二条结束

        allRouteinfo.setRoutes(listRoute);
        return allRouteinfo;
    }

    /**
     * 	通过时间获取星期几
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
     * @param time		指定时间
     * @param intDay	指定时间的后几天天数
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
     * @param time		指定时间
     * @param intDay	指定时间的前几小时
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
     * 根据NFD信息获取舱位价格和舱位折扣
     * NDF信息(NFD:PEKCAN/22OCT11/CA)
     * @param strFromCity  出发城市三字码
     * @param strToCity  到达城市三字码
     * @param strFromDate  出发日期三字码
     * @param strAirCom  航空公司代码码
     * @param strCabinCode  舱位码
     * @return 返回舱位价格和舱位折扣，格式：舱位价格|舱位折扣
     * Created By:sunbin
     * Date:2011-10-08
     */
    public String GetPriceAndDiscountByNFD(String strFromCity, String strToCity, String strFromDate, String strAirCom,
            String strCabinCode) {
        String strReturn = "";
        String strRet = "";
        try {
            String strcmd = "NFD:" + strFromCity + strToCity + "/" + ChangeDateModeYear(strFromDate) + "/" + strAirCom
                    + "$PN$PN";
            strReturn = commandFunction2(strcmd, "", "");
            String[] strarr = strReturn.split("TRVDATE");
            //计算折扣信息
            String strYPrice = GetYPriceByFD(strFromCity, strToCity, strAirCom);
            System.out.println(strYPrice);
            if (strarr.length >= 1) {
                //strReturn=strarr[1];
                Pattern patmiles = Pattern.compile("\\n");
                String[] strNFDArr = patmiles.split(strReturn);
                for (int i = 0; i < strNFDArr.length; i++) {
                    if (strNFDArr[i].indexOf("NFN:") > 0) {
                        Pattern patitems = Pattern.compile("\\s{1,}");
                        String[] strItems = patitems.split(strNFDArr[i]);
                        if (strRet.equals("") || strRet.equals("0|0")) {
                            if (strItems.length >= 4 && strItems[4].equals(strCabinCode)) {
                                for (int j = 0; j < strItems.length; j++) {
                                    //计算折扣
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
            strRet = "0|0";
        }
        return strRet;
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

    /**
     * 取得所有缓存航空公司信息
     * @return 航空公司list
     */
    public List<Aircompany> getAircompanyCache() {
        try {
            List<Aircompany> list = cachebasedata.getAirCompanyList("ALLAirCompanyList");
            return list;
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * 取得所有缓存的机型信息
     * @return  机型信息list
     */
    public List<Flightmodel> getFlightModelCache() {
        try {
            List<Flightmodel> list = cachebasedata.getFlightModel("ALLFlightModelList");
            return list;
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * 
     * @param strKey
     * @param strAirCompanycode
     * @param strCabinCode
     * @return
     */
    public Cabin getCabinCache(String strKey, String strAirCompanycode, String strCabinCode) {
        return null;
    }

    public static void main(String[] args) throws MalformedURLException {
        // TODO Auto-generated method stub
        String url = "http://localhost:8080/cn_interface/service/";

        HessianProxyFactory factory = new HessianProxyFactory();
        ITicketSearchService servier = (ITicketSearchService) factory.create(ITicketSearchService.class, url
                + ITicketSearchService.class.getSimpleName());
        //查询航班
        //		FlightSearch flightSearch=new FlightSearch();
        //		flightSearch.setFromDate("2011-04-30");
        //		flightSearch.setStartAirportCode("XIY");
        //		flightSearch.setEndAirportCode("PEK");
        //		List<FlightInfo> listFlightInfo=servier.findAllFlightinfo(flightSearch);
        //提取PNR
        //String strReturn=servier.getFullRTPnrResult("HP74GQ");
        //servier.Etdz("YPVRP", "", "ETDZ:6", "");
        //String strNumber=servier.getTicketNumber("YP0DC");
        //System.out.println(strReturn);
        //String strxing=servier.getRpNumber(strNumber);
        //查询特价价格
        String strxing = servier.GetPriceAndDiscountByNFD("PEK", "SHA", "2011-11-05", "FM", "P");
        System.out.println(strxing);

    }

    public String getIscacheprice() {
        return iscacheprice;
    }

    public void setIscacheprice(String iscacheprice) {
        this.iscacheprice = iscacheprice;
    }

    @Override
    public String AUTHpnr(String pnr, String office) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRealRT(String pnr, String type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRealPat(String pnr, String type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPatPrice(String pnr, String type, String isAll) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getpnrStatus(String pnr) {
        // TODO Auto-generated method stub
        return null;
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
