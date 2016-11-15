package com.ccservice.b2b2c.atom.component;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tempuri.ServiceLocator;
import org.tempuri.ServiceSoap;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.aircompany.Aircompany;
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

public class TicketYastComponent implements ITicketSearchComponent {
    private String username;

    private String password;

    private String ipAddress;

    private String officeNumber;

    private String ipbigAddress;

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

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    // 调用接口AVOpen
    public String AVOpen(FlightSearch flightSearch) {

        ServiceLocator lo = new ServiceLocator();

        try {
            ServiceSoap ss = lo.getServiceSoap();

            StringBuffer identity = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf- 8\"?>");
            identity.append("<Identity_1_0>");
            identity.append("<Operator>" + username + "</Operator>"); // 登录操作工号  
            identity.append("<Pwd>" + password + "</Pwd>"); // 密码  
            identity.append("<Terminal></Terminal>"); // 终端号  
            identity.append("</Identity_1_0>");

            StringBuffer request = new StringBuffer("<?xml version=\"1.0\"?>");

            request.append("<QueryGridFlights_1_1>");
            request.append("<From>" + flightSearch.getStartAirportCode() + "</From>"); // 销售城市的三字代码(例如：PEK 北京)或行政区号(例如：010 北京)  
            request.append("<Arrive>" + flightSearch.getEndAirportCode() + "</Arrive>"); // 城市三字代码或城市行政区号  
            request.append("<Date>" + flightSearch.getFromDate() + "</Date>"); // 航班日期  
            request.append("<Carrier></Carrier>"); // 航空公司两字代码  
            request.append("<TimeFrom>0000</TimeFrom>"); // 起飞时间范围开始  
            request.append("<TimeTo>2400</TimeTo>"); // 起飞时间范围结束  
            request.append("<StopType>D</StopType>"); // D:直飞（默认） S:经停 A:所有  
            request.append("<Option/>"); // 系统保留  
            request.append("<SortType/>"); // T到达时间(默认)/P价格 /C 航空公司  
            request.append("<MaxNum/>"); // 最大返回结果，0：表示返回全部查询结果  
            request.append("</QueryGridFlights_1_1>");

            String response = ss.xmlSubmit(identity.toString(), request.toString(), "");
            System.out.println(response);
            return response;

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

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
        // 返回List
        List<FlightInfo> listFlightInfoAll = new ArrayList<FlightInfo>();

        ServiceLocator lo = new ServiceLocator();

        try {
            ServiceSoap ss = lo.getServiceSoap();

            StringBuffer identity = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf- 8\"?>");
            identity.append("<Identity_1_0>");
            identity.append("<Operator>" + username + "</Operator>"); // 登录操作工号  
            identity.append("<Pwd>" + password + "</Pwd>"); // 密码  
            identity.append("<Terminal></Terminal>"); // 终端号  
            identity.append("</Identity_1_0>");

            StringBuffer request = new StringBuffer("<?xml version=\"1.0\"?>");

            request.append("<QueryWebFlights_1_1>");
            request.append("<From>" + flightSearch.getStartAirportCode().trim() + "</From>"); // 销售城市的三字代码(例如：PEK 北京)或行政区号(例如：010 北京)  
            request.append("<Arrive>" + flightSearch.getEndAirportCode().trim() + "</Arrive>"); // 城市三字代码或城市行政区号  
            request.append("<Date>" + flightSearch.getFromDate().trim() + "</Date>"); // 航班日期  
            request.append("<Carrier></Carrier>"); // 航空公司两字代码  
            request.append("<Time>0000</Time>"); // 起飞时间范围开始  
            request.append("<StopType>D</StopType>"); // D:直飞（默认） S:经停 A:所有  
            request.append("<Option/>"); // 系统保留  
            //request.append("<SortType/>");          // T到达时间(默认)/P价格 /C 航空公司  
            request.append("<MaxNum/>"); // 最大返回结果，0：表示返回全部查询结果  
            request.append("</QueryWebFlights_1_1>");

            String response = ss.xmlSubmit(identity.toString(), request.toString(), "");
            System.out.println(response);
            SAXBuilder build = new SAXBuilder();
            Document document = build.build(new StringReader(response));
            Element root = document.getRootElement();
            Element result = root.getChild("Yeesky.AOIS.AV.Results");
            List<Element> list = result.getChildren("AV.Result");

            for (Element e : list) {

                // 航班信息类
                FlightInfo flighInfo = new FlightInfo();
                // 舱位信息类
                CarbinInfo cabinInfo = null;
                CarbinInfo lowCabinInfo = new CarbinInfo();
                List<CarbinInfo> listCabinAll = new ArrayList<CarbinInfo>();
                //航空公司名称
                flighInfo.setAirCompanyName(e.getChildTextTrim("CarrierName"));
                // 航班号
                flighInfo.setAirline(e.getChildTextTrim("Carrier") + e.getChildTextTrim("FlightNo"));
                // 出发机场三字码
                flighInfo.setStartAirport(e.getChildTextTrim("BoardPoint"));
                //出发机场名称
                flighInfo.setStartAirportName(getAirportName(e.getChildTextTrim("BoardPoint")));
                // 到达机场三字码
                flighInfo.setEndAirport(e.getChildTextTrim("OffPoint"));
                //到达机场名称
                flighInfo.setEndAirportName(getAirportName(e.getChildTextTrim("OffPoint")));
                // 航空公司代码
                flighInfo.setAirCompany(e.getChildTextTrim("Carrier"));
                flighInfo.setBorderPointAT(e.getChildTextTrim("BoardPointAT"));
                flighInfo.setOffPointAT(e.getChildTextTrim("OffPointAT"));
                // 出发时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date startDate = dateFormat.parse(e.getChildTextTrim("DepartureDate") + " "
                        + e.getChildTextTrim("DepartureTime"));
                flighInfo.setDepartTime(new Timestamp(startDate.getTime()));
                Date arriveDate = null;
                try {
                    // 到达时间
                    arriveDate = dateFormat.parse(e.getChildTextTrim("ArrivalDate") + " "
                            + e.getChildTextTrim("ArrivalTime"));
                }
                catch (Exception ee) {
                    ee.printStackTrace();
                    arriveDate = dateFormat.parse(e.getChildTextTrim("ArrivalDate") + " 01:55");
                }

                flighInfo.setArriveTime(new Timestamp(arriveDate.getTime()));

                String Aircraft = e.getChildTextTrim("Aircraft");
                if (Aircraft.indexOf("\n") > 0) {
                    Aircraft = Aircraft.substring(0, Aircraft.indexOf("\n"));
                }
                // 机型
                flighInfo.setAirplaneType(Aircraft);
                // 机型描述
                List<Flightmodel> listModel = Server
                        .getInstance()
                        .getAirService()
                        .findAllFlightmodel(
                                "WHERE " + Flightmodel.COL_modelnum + "='" + flighInfo.getAirplaneType() + "'", "", -1,
                                0);
                if (listModel != null && listModel.size() > 0) {
                    String strTypeDesc = listModel.get(0).getModeldesc() + "#" + listModel.get(0).getPicpath();
                    flighInfo.setAirplaneTypeDesc(strTypeDesc);
                }

                // 燃油费/燃油费（成人）
                String AirportTax = e.getChildTextTrim("AirportTax");
                if (AirportTax != null && AirportTax.length() > 0) {
                    flighInfo.setAirportFee((int) (Float.parseFloat(AirportTax)));
                }

                String FuelSurTax = e.getChildTextTrim("FuelSurTax");
                if (FuelSurTax != null && FuelSurTax.length() > 0) {
                    flighInfo.setFuelFee((int) (Float.parseFloat(FuelSurTax)));
                }

                //循环舱位信息
                List<Element> classlist = e.getChild("Class").getChildren();
                for (Element ee : classlist) {
                    cabinInfo = new CarbinInfo();
                    // 舱位码
                    String cabinCode = "";
                    String cabinNum = "0";
                    String cname = ee.getName();
                    String Seat = ee.getChildTextTrim("Seat");
                    String code = ee.getChildTextTrim("Code");
                    String zk = "100";
                    if (Seat.length() >= 2) {
                        if (Seat.substring(1).equals("A")) {
                            cabinNum = ">9";
                        }
                        else {
                            cabinNum = Seat.substring(1);
                        }
                    }

                    if (code != null && code.length() > 0) {
                        cabinCode = code;
                    }
                    else {
                        continue;
                    }
                    //						if(cname.startsWith("F") && cname.length()>0 ){
                    //							cabinCode = "F"; 
                    //							zk = cname.substring(1);
                    //						}
                    cabinInfo.setCabin(cabinCode);
                    if (cabinNum.equals("A") || isNumber(cabinNum)) {
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
                            break;
                        }
                        cabinInfo.setSeatNum(strCabinNum);

                    }
                    cabinInfo.setPrice(Float.parseFloat(ee.getChildTextTrim("F")));
                    //取得退改签规定
                    Element trules = root.getChild("Yeesky.AOIS.AV.TicketRules");
                    if (trules != null && !trules.equals("")) {
                        List<Element> listrules = trules.getChildren("TicketRule");
                        for (Element rulemodel : listrules) {
                            if (ee.getChildTextTrim("TRID").equals(rulemodel.getChildText("TRID"))) {
                                cabinInfo.setCabinRules(rulemodel.getChildText("Refund") + "<br />"
                                        + rulemodel.getChildText("Endorsement"));
                                cabinInfo.setCabinRemark(rulemodel.getChildText("Refund") + "<br />"
                                        + rulemodel.getChildText("Endorsement"));
                            }
                        }
                    }
                    else {
                        cabinInfo.setCabinRules("暂无退改签规定!");
                        cabinInfo.setCabinRemark("暂无退改签规定!");
                    }

                    if (ee.getName().equals("F")) {
                        cabinInfo.setCabintypename("头等舱");
                    }
                    else if (ee.getName().equals("C")) {
                        cabinInfo.setCabintypename("商务舱");
                    }
                    else if (ee.getName().equals("Y")) {
                        cabinInfo.setCabintypename("经济舱");
                    }
                    else {
                        cabinInfo.setCabintypename("经济舱");
                    }
                    cabinInfo.setDiscount(Float.parseFloat(ee.getChildTextTrim("R")));
                    cabinInfo.setRatevalue(Float.parseFloat(ee.getChildTextTrim("C")));
                    // 是否特价，因为取不到特价信息所有，暂时都设为false
                    cabinInfo.setSpecial(false);
                    listCabinAll.add(cabinInfo);
                }
                //临时舱位类
                if (listCabinAll != null && listCabinAll.size() > 0) {
                    CarbinInfo tempCabinInfo = (CarbinInfo) listCabinAll.get(0);

                    for (int i = 0; i < listCabinAll.size(); i++) {
                        if (tempCabinInfo.getPrice() > listCabinAll.get(i).getPrice()) {
                            tempCabinInfo = listCabinAll.get(i);
                        }
                    }
                    //设置最低舱位
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
                    lowCabinInfo.setSpecial(false);

                    flighInfo.setLowCarbin(lowCabinInfo);

                    flighInfo.setCarbins(listCabinAll);
                    listFlightInfoAll.add(flighInfo);
                }
                else {
                    continue;
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return listFlightInfoAll;

    }

    public String getAirportName(String strAirPortCode) {
        List<Cityairport> listairport = Server.getInstance().getAirService()
                .findAllCityairport("WHERE " + Cityairport.COL_airportcode + " ='" + strAirPortCode + "'", "", -1, 0);
        return listairport.get(0).getAirportname();
    }

    // 提取PNR信息
    public String getPNRInfo(String strPNR) {
        strPNR = strPNR.trim();
        // 对PNR进行格式验证
        if ((strPNR == "")) {
            return "PNR不能为空!";

        }
        int len = strPNR.length();
        if (len != 5) {
            return "PNR长度不等于5位!";
        }
        if (!IsValidPnr(strPNR)) {
            return "PNR格式不正确";
        }
        String strFinalStr; // 返回信息
        String strTongdaoCode; // 通道码
        // 进行rtPNR的操作，并且查看其返回的结果，如果正确，则进行下一步，如果不正确释放资源
        String strReturnInfo = commonCmdFunc("0", "-1", "0", "0", "0", "d", "RT" + strPNR, ipAddress, username,
                password);
        int i;
        strFinalStr = "";
        strFinalStr = formatHTML(strReturnInfo);
        strTongdaoCode = getTDcode(strReturnInfo);
        String pnstr = strReturnInfo;
        pnstr = commonCmdFunc("0", strTongdaoCode, "0", "0", "0", "d", "IG", ipAddress, username, password);
        return strFinalStr;
    }

    // 生成PNR
    public String CreatePnr(String SS, String NM, String CT, String TK, String NUM, String depTime, String SSR) {
        //		SS = SS.trim(); // 航段组
        //		NM = NM.trim(); // 姓名组
        //		CT = CT.trim(); // 联系方式
        //		TK = TK.trim(); // 出票时限
        //		NUM = NUM.trim(); // 乘机人数
        //		SSR = SSR.trim(); // 证件信息
        //		depTime = depTime.trim(); // 出发时间
        //		String strReturnInfo = ""; // 返回值
        //		// 判断参数的准确性结束
        //		String yanzhengstr = "????" + username + "!!!!" + password + "@";
        //		// s3:='????'+s1+'!!!!'+s2+'@';
        //		String sendcmd = "PNR&" + SS + "&" + NM + "&" + CT + "&" + TK + "&"
        //				+ NUM + "&" + depTime + "&" + SSR + "&" + officeNumber;
        //
        //		try {
        //			// 定义访问端口
        //			int port = 6000;
        //			// 得到InetAddress 对象
        //			InetSocketAddress inetSocketAddress = new InetSocketAddress(
        //					ipAddress, port);
        //			// ------------创建连接------------//
        //			Socket socket = new Socket();
        //			InputStream input = null; // 输出流
        //			OutputStream output = null; // 输入流
        //			socket.connect(inetSocketAddress, 10 * 1000);
        //			// 链接成功
        //			if (socket.isConnected()) {
        //				// 初始化变量
        //				String sendStr;
        //				String recvStr;
        //				byte[] bs;
        //				// 发送验证信息结束
        //				sendStr = yanzhengstr + sendcmd;
        //				bs = sendStr.getBytes("ASCII");
        //				output = socket.getOutputStream();
        //				input = new DataInputStream(socket.getInputStream());
        //				output.write(bs);
        //				output.flush();// 发送信息至服务器
        //				int avali = 0;
        //				char firstbyte = (char) input.read();
        //				avali = input.available();
        //				recvStr = "" + firstbyte;
        //				while ((avali = input.available()) > 0) {
        //					try {
        //						// 在此睡眠0.1秒,很重要
        //						Thread.sleep(100);
        //					} catch (Exception t) {
        //						t.printStackTrace();
        //					}
        //					byte aval[] = new byte[avali];
        //					int bytesRead = input.read(aval, 0, avali);
        //					// 将Byte[] 转换成string
        //					recvStr += new String(aval, "UTF-8");
        //				}
        //				output.close();
        //				input.close();
        //				socket.close();
        //				// 需要修改成XML格式
        //				recvStr = recvStr.replace("\r", "<br>");
        //				strReturnInfo = recvStr.replaceAll("[^\\x20-\\xff]", "");
        //			}
        //		} catch (Exception e) {
        //			e.printStackTrace(); // 查询异常出错
        //		}
        //		if (strReturnInfo.length() > 0) {
        //			return strReturnInfo;
        //		} else {
        //			return "PNR生成失败";
        //		}
        return "XSDCS";
    }

    // 小PNR转大编码
    public String getPNRBigInfo(String strPNR) {
        return "";
    }

    // 自动出票指令
    public String Etdz(String pnrcode, String ei, String ETDZcmd, String officeNo) {
        String strPNR = pnrcode.trim();
        String strEI = ei.trim();

        String strEtdz = ETDZcmd.trim();
        String officeNostr = officeNo.trim();

        if ((strPNR == "") || (strEtdz == "")) {
            return "PNR或者Etdz指令为空，请重试!";

        }
        int len = strPNR.length();
        if (len != 5) {
            return "PNR长度不是5位，请重试！";

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
        String rtpnrstr = ETDZCmdFunc("1", "-1", "1", "0", "1", officeNostr, "RT" + strPNR, ipAddress, username,
                password);
        int i;
        i = rtpnrstr.indexOf("     +"); // 判断是否有+
        if (i != -1)// 还有next page
        {
            tongdaoCode = getTDcode(rtpnrstr);
            rtpnrstr = ETDZCmdFunc("1", tongdaoCode, "0", "0", "1", officeNostr, "PG1", ipAddress, username, password);
        }
        if (!checkPnrReturn(rtpnrstr)) {
            tongdaoCode = getTDcode(rtpnrstr);
            rtpnrstr = ETDZCmdFunc("0", tongdaoCode, "0", "0", "0", officeNostr, "IG", ipAddress, username, password);
            return "Err06";
        }
        tongdaoCode = getTDcode(rtpnrstr);// 获得通道号码
        String XeRrStr = getXeRrCmd(rtpnrstr);
        String Carrier = getCarrier(rtpnrstr);
        // 发送xe rr命令
        rtpnrstr = ETDZCmdFunc("1", tongdaoCode, "0", "0", "1", officeNostr, XeRrStr, ipAddress, username, password);
        if (Carrier == "SC") {
            rtpnrstr = ETDZCmdFunc("0", tongdaoCode, "0", "0", "0", officeNostr, "IG", ipAddress, username, password);
            return "Err07";
        }
        else {
            if (true) {
                rtpnrstr = ETDZCmdFunc("1", tongdaoCode, "0", "0", "1", officeNostr, "RT", ipAddress, username,
                        password);
                String pnstr;
                pnstr = rtpnrstr;
                while (1 == 1) {
                    i = pnstr.indexOf("     +");
                    if (i != -1)// 还有next page
                    {
                        pnstr = ETDZCmdFunc("1", tongdaoCode, "0", "0", "1", officeNostr, "PN", ipAddress, username,
                                password);
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
                        rtpnrstr = ETDZCmdFunc("1", tongdaoCode, "0", "0", "1", officeNostr, "EI:" + strEI, ipAddress,
                                username, password);

                    }
                    rtpnrstr = ETDZCmdFunc("1", tongdaoCode, "0", "0", "1", officeNostr, strEtdz, ipAddress, username,
                            password);
                    // 反提PNR，获得票号信息

                    rtpnrstr = ETDZCmdFunc("1", tongdaoCode, "0", "0", "1", officeNostr, "RT" + strPNR, ipAddress,
                            username, password);
                    // pnstr;
                    pnstr = rtpnrstr;
                    while (1 == 1) {
                        i = pnstr.indexOf("     +");
                        if (i != -1)// 还有next page
                        {
                            pnstr = ETDZCmdFunc("1", tongdaoCode, "0", "0", "1", officeNostr, "PN", ipAddress,
                                    username, password);
                            rtpnrstr = rtpnrstr + pnstr;
                            continue;
                        }
                        else {
                            break;
                        }

                    }

                    String ticketNo = getTicketNo(rtpnrstr);
                    // 释放资源
                    rtpnrstr = ETDZCmdFunc("0", tongdaoCode, "0", "0", "1", officeNostr, "IG", ipAddress, username,
                            password);

                    return ticketNo;
                }
                else {
                    // rrXE不成功
                    rtpnrstr = ETDZCmdFunc("0", tongdaoCode, "0", "0", "1", officeNostr, "IG", ipAddress, username,
                            password);

                    return "Err08";
                }

            }
            else {
                // 如果获得pat命令不成功的话,释放通道并退出
                rtpnrstr = ETDZCmdFunc("0", tongdaoCode, "0", "0", "1", officeNostr, "IG", ipAddress, username,
                        password);
                return "Err09";
            }
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
        String regEx = "\\*{0,1}[0..9,A-Z]{2}\\d{3,4}\\s{2,3}\\w{2}[#,\\*](\\s[A-Z][\\w])*\\s{2,5}\\w{6}\\s\\d{4}\\s{3}\\d{4}(\\+1)*\\s{1,3}\\w{3}\\s\\d[\\^,\\s]\\w{0,1}\\s{2,3}\\w\\s{2}\\>\\s{3}([0..9,A-Z]{2}\\d{3,4})*(\\s){6,11}(\\s[A-Z][\\w])*";
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
        Matcher matcher = pattern.matcher(strIn);
        String[] rr = new String[matcher.groupCount()];
        String rrs = "";
        while (matcher.find()) {

            int i = 0;
            rr[i] = matcher.group();
            i++;
        }
        rrs = rr[0].substring(0, 1) + "RR";
        for (int k = 1; k < matcher.groupCount(); k++) {
            rrs = rrs + "#" + rr[k].substring(0, 1) + "RR";
        }
        // 获取tl项的信息
        String regEx = "\\d\\.TL";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(strIn);
        if (m.find()) {
            String tlstr = m.group();
            String tls = "XE" + tlstr.substring(0, 1);
            return rrs + "#" + tls;

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

        if ((m.find()) && (m1.find())) {
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
            carrier = carrier.substring(carrier.indexOf(" ") + 2, 2);
            return carrier.trim();

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
        int bz = strIn.indexOf("&");
        if (bz > 0) {
            char[] temps = strIn.substring(bz + 1, strIn.length() - bz - 1).toCharArray();
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
        segmentinfo.setAircompanyname("国航");
        segmentinfo.setAirportfee(50f);
        segmentinfo.setArrivaltime(new Timestamp(System.currentTimeMillis()));
        segmentinfo.setCabincode("F");
        segmentinfo.setDeparttime(new Timestamp(System.currentTimeMillis()));
        segmentinfo.setDiscount(4.5f);
        segmentinfo.setEndairport("上海虹桥机场");
        segmentinfo.setFlightmodelnum("波音747");
        segmentinfo.setFlightnumber("CA15424");
        segmentinfo.setFuelfee(50f);
        segmentinfo.setIsspecial(0);
        segmentinfo.setPrice(1700f);
        segmentinfo.setRules("不能退改签");
        segmentinfo.setStartairport("北京国际机场");
        segmentinfo.setTraveltype(1);
        segmentinfo.setYprice(2000f);
        segmentinfo.setZrateid(1l);
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
        passenger.setPtype(1);
        passenger.setIdtype(1);
        passenger.setIdnumber("1234567899745");
        Passenger passenger2 = new Passenger();
        passenger2.setName("chen");
        passenger2.setPtype(1);
        passenger2.setIdtype(1);
        passenger2.setIdnumber("21313123");
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
     * 取消PNR功能
     * @param strPNR PNR编码
     * @return
     */
    public String XEPNR(String strPNR) {

        return "";
    }

    /***
     * 分离PNR功能
     */
    public String SPPNR(String strPNR, String strNumber) {
        return "";
    }

    /**
     * Q信箱查询
     * @param strPNR PNR编码
     * @param strNumber  要分离乘机人所在序号，必须要与黑屏中的序号一致
     * @return
     */
    public String QMailQT() {
        return "";
    }

    /**
     * 处理某种Q信箱
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQS(String strQueryType) {
        return "";
    }

    /**
     * 释放Q信箱
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQNE() {
        return "";
    }

    /**
     * 得到大PNR
     * @param strPNR PNR
     * @return
     */
    public String getBigPNRInfo(String strPNR) {
        return "";
    }

    /**
     * 释放当前处理下一个
     * @param strPNR PNR编码
     * @return
     */
    public String QMailQN() {
        return "";
    }

    public String commandFunction2(String strcmd, String strPageinfo, String strIG) {
        return "";
    }

    /*
     * 根据PNR取得票号信息
     */
    public String getTicketNumber(String strPnrInfo, String strPnmber) {

        return "";
    }

    /*
     * 根据PNR取得行程单号信息
     */
    public String getRpNumber(String strTicketNumber) {

        return "";
    }

    public String CreatePNRByInterFace(List<Segmentinfo> listsegment, List<Passenger> listpassenger,
            String strCustomerCode) {
        return "";
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
        allflight.setFromDate("2010-05-24 09:00");
        allflight.setToDate("2010-05-25 10:05");
        allflight.setFlightNumber("DL634");
        listAllFlight.add(allflight);
        //第二条航班
        AllFlight allflight1 = new AllFlight();
        allflight1.setNo(2);
        allflight1.setFromCity("BJS");
        allflight1.setDestCity("SFO");
        allflight1.setAirCompany("DL");
        allflight1.setFromDate("2010-05-24 11:30");
        allflight1.setToDate("2010-05-25 14:15");
        allflight1.setFlightNumber("DL526");
        listAllFlight.add(allflight1);

        //第三条航班
        AllFlight allflight2 = new AllFlight();
        allflight2.setNo(3);
        allflight2.setFromCity("BJS");
        allflight2.setDestCity("SFO");
        allflight2.setAirCompany("DL");
        allflight2.setFromDate("2010-05-24 14:30");
        allflight2.setToDate("2010-05-25 16:15");
        allflight2.setFlightNumber("DL111");
        listAllFlight.add(allflight2);

        //第三条航班
        AllFlight allflight3 = new AllFlight();
        allflight3.setNo(4);
        allflight3.setFromCity("BJS");
        allflight3.setDestCity("SFO");
        allflight3.setAirCompany("DL");
        allflight3.setFromDate("2010-05-24 15:00");
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
        allflight4.setDestCity("SFO");
        allflight4.setAirCompany("CO");
        allflight4.setFromDate("2010-05-24 08:00");
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

    public String GetPriceAndDiscountByNFD(String strFromCity, String strToCity, String strFromDate, String strAirCom,
            String strCabinCode) {
        String strReturn = "";

        return strReturn;
    }

    public String commandFunction(String strcmd, String strPageinfo) {
        return "";
    }

    public String getFullRTPnrResult(String strPNR) {
        String strReturn = "";
        return strReturn;
    }

    public List<Flightmodel> getFlightModelCache() {
        return null;
    }

    public String CreatePNRByCmd(List<Segmentinfo> listsegmenginf, List<Passenger> listpassengers,
            String strCustomerCode) {
        return "";
    }

    public List<Aircompany> getAircompanyCache() {
        return null;
    }

    public String getIpbigAddress() {
        return ipbigAddress;
    }

    public void setIpbigAddress(String ipbigAddress) {
        this.ipbigAddress = ipbigAddress;
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
    public String commandFunction3(String strcmd, String strPageinfo, String strIG) {
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
