package com.ccservice.b2b2c.atom.component;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccservice.b2b2c.base.aircompany.Aircompany;
import com.ccservice.b2b2c.base.cabin.Cabin;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.flightmodel.Flightmodel;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;
import com.travelsky.ibe.client.AV;
import com.travelsky.ibe.client.AvItem;
import com.travelsky.ibe.client.AvResult;
import com.travelsky.ibe.client.AvSegment;
import com.travelsky.ibe.client.FD;
import com.travelsky.ibe.client.FDResult;
import com.travelsky.ibe.client.pnr.SSResult;
import com.travelsky.ibe.client.pnr.SellSeat;

public class TicketTraveSkyComponent implements ITicketSearchComponent {
    private String username;

    private String password;

    private String ipAddress;

    private String officeNumber;

    private String ipbigAddress;

    private Hashtable<String, List<CarbinInfo>> canbintabel = null;

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * xiaohan
     * 
     * @param search
     * @param flightinfo
     */
    public void setFlightCarbins(FlightSearch search, FlightInfo flightinfo, AvSegment segment) {

        if (canbintabel == null || canbintabel.size() == 0) {
            canbintabel = new java.util.Hashtable<String, List<CarbinInfo>>();
            FD fd = new FD();
            FDResult fs;
            try {
                DateFormat format = new SimpleDateFormat("ddMMMyy", Locale.US);
                Date date = DateFormat.getDateInstance().parse(search.getFromDate());
                fs = fd.findPrice(search.getStartAirportCode(), search.getEndAirportCode(), format.format(date),
                        search.getAirCompanyCode(), "0", "AD", true);
                for (int i = 0; i < fs.getElementNum(); i++) {
                    CarbinInfo cabin = new CarbinInfo();
                    cabin.setCabin(fs.getCabinType(i));
                    try {
                        cabin.setPrice(Float.valueOf(fs.getSinglePrice(i)));
                        if (cabin.getPrice() == 0) {
                            continue;
                        }
                    }
                    catch (Exception e) {
                        continue;
                    }
                    try {
                        cabin.setDiscount(Float.valueOf(fs.getDiscountRate(i)));
                    }
                    catch (Exception e) {
                        cabin.setDiscount(0f);
                    }
                    try {
                        cabin.setAirportprice(Float.valueOf(fs.getAirportTax(i)));
                        cabin.setFuelprice(Float.valueOf(fs.getFuelTax(i)));
                    }
                    catch (Exception e) {
                        continue;
                    }

                    char type = cabin.getCabin().charAt(0);
                    String cw = segment.getCangweiinfoOf(type);
                    if (cw == null && cw.equals("0") && cw.length() > 0) {
                        continue;
                    }
                    cabin.setSeatNum(cw);
                    String air = fs.getAirline(i).toUpperCase();
                    if (canbintabel.get(air) == null) {
                        List<CarbinInfo> cabins = new ArrayList<CarbinInfo>();
                        cabins.add(cabin);
                        canbintabel.put(air, cabins);
                    }
                    else {
                        List<CarbinInfo> cabins = canbintabel.get(air);
                        cabins.add(cabin);
                        canbintabel.put(air, cabins);
                    }

                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        String air = flightinfo.getAirCompany();
        List<CarbinInfo> cabins = canbintabel.get(air);
        if (cabins == null || cabins.size() == 0) {
            flightinfo = null;

        }
        else {
            flightinfo.setCarbins(cabins);
            CarbinInfo cabin = cabins.get(0);
            flightinfo.setAirportFee(cabin.getAirportprice());
            flightinfo.setFuelFee(cabin.getFuelprice());
            flightinfo.setLowCarbin(this.getLowestCarbin(cabins));
        }

    }

    public CarbinInfo getLowestCarbin(List<CarbinInfo> listcarbin) {

        CarbinInfo lowestcarbin = listcarbin.get(0);
        for (CarbinInfo carbin : listcarbin) {
            if (carbin.getPrice() < lowestcarbin.getPrice()) {
                lowestcarbin = carbin;
            }
        }
        return lowestcarbin;

    }

    /**
     * 航班查询方法 说明：此接口对查询出的IBE航班数据进行封装成系统中对应的FlightInfo对象集合 *
     * 
     * @param flightSearch
     *            航班查询类
     * @return 航班数据List
     * @author 孙斌
     * @serialData 2011-11-10
     */
    public List<FlightInfo> findAllFlightinfo(FlightSearch flightSearch) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmm");
        List<FlightInfo> listFlightInfoAll = new ArrayList<FlightInfo>();
        // IBE对象AVResult
        AvResult avresult = new AvResult();
        try {
            avresult = AV(flightSearch);
        }
        catch (Exception ex) {
            System.out.println("调用IBE接口AV查询出错：" + ex.getMessage());
        }
        try {
            // 对AVResult数据进行解析，并封装到listFlightInfoAll
            for (int i = 0; i < avresult.getItemamount(); i++) {
                // 取得每项的AvItem
                AvItem avitem = avresult.getItem(i);
                for (int j = 0; j < avitem.getSegmentnumber(); j++) {
                    // 航班信息类
                    FlightInfo flightinfo = new FlightInfo();
                    AvSegment avsegment = avitem.getSegment(j);
                    flightinfo.setStartAirport(flightSearch.getStartAirportCode());
                    flightinfo.setStartAirportName(avsegment.getOrgcity());
                    flightinfo.setEndAirport(flightSearch.getEndAirportCode());
                    flightinfo.setEndAirportName(avsegment.getDstcity());
                    flightinfo.setStartAirportCity(avsegment.getOrgcity());
                    flightinfo.setAirline(avsegment.getAirline());
                    flightinfo.setAirCompany(avsegment.getAirline().substring(0, 2));
                    Date startDate = dateFormat.parse(flightSearch.getFromDate() + " " + avsegment.getDeptime());
                    flightinfo.setDepartTime(new Timestamp(startDate.getTime()));
                    // 到达时间
                    Date arriveDate = dateFormat.parse(flightSearch.getFromDate() + " " + avsegment.getArritime());
                    flightinfo.setArriveTime(new Timestamp(arriveDate.getTime()));
                    // 是否是共享航班
                    flightinfo.setIsShare(0);
                    flightinfo.setShareFlightNumber("");
                    // 飞机型号
                    flightinfo.setAirplaneType(avsegment.getPlanestyle());
                    // 起飞航站楼
                    flightinfo.setBorderPointAT(avsegment.getDepTerm());
                    // 到达航站楼
                    flightinfo.setOffPointAT(avsegment.getArriTerm());
                    // 经停次数
                    if (avsegment.getStopnumber() > 0) {
                        flightinfo.setStop(true);
                    }
                    this.setFlightCarbins(flightSearch, flightinfo, avsegment);
                    if (flightinfo != null && flightinfo.getLowCarbin() != null)
                        listFlightInfoAll.add(flightinfo);
                }
            }
        }
        catch (Exception ex) {
            System.out.println("解析IBE接口AVResult结果出错：" + ex.getMessage());
            ex.printStackTrace();
        }

        return listFlightInfoAll;
    }

    /**
     * 对接IBE接口AV方法，查询IBE航班数据 说明：对接IBE接口AV方法，查询IBE航班数据
     * 
     * @param flightSearch
     *            航班查询类
     * @return 航班数据List
     * @author 孙斌
     * @serialData 2011-11-10
     */
    public AvResult AV(FlightSearch flightSearch) {
        AV av = new AV();
        AvResult avresult = new AvResult();
        try {
            // 格式化出发时间
            String strFromDate = flightSearch.getFromDate();
            strFromDate = strFromDate + " 00:00:00";

            if (flightSearch.getAirCompanyCode().length() == 0) {
                flightSearch.setAirCompanyCode("ALL");
            }
            avresult = av.getAvailability(flightSearch.getStartAirportCode(), flightSearch.getEndAirportCode(),
                    strFromDate.replace("-", ""), flightSearch.getAirCompanyCode(), true, true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("航班查询接口异常：" + ex.getMessage().toString());
        }
        return avresult;
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

    /**
     * 通过pnr获取订单信息
     * 
     * @param pnr
     * @return
     */
    public List getOrderbypnr(String pnr) {
        return null;
    }

    // 生成PNR
    public String CreatePnr(String SS, String NM, String CT, String TK, String NUM, String depTime, String SSR) {
        SS = SS.trim(); // 航段组
        NM = NM.trim(); // 姓名组
        CT = CT.trim(); // 联系方式
        TK = TK.trim(); // 出票时限
        NUM = NUM.trim(); // 乘机人数
        SSR = SSR.trim(); // 证件信息
        depTime = depTime.trim(); // 出发时间
        String strReturnInfo = ""; // 返回值
        // 判断参数的准确性结束
        String yanzhengstr = "????" + username + "!!!!" + password + "@";
        // s3:='????'+s1+'!!!!'+s2+'@';
        String sendcmd = "PNR&" + SS + "&" + NM + "&" + CT + "&" + TK + "&" + NUM + "&" + depTime + "&" + SSR + "&"
                + officeNumber;

        try {
            // 定义访问端口
            int port = 6000;
            // 得到InetAddress 对象
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ipAddress, port);
            // ------------创建连接------------//
            Socket socket = new Socket();
            InputStream input = null; // 输出流
            OutputStream output = null; // 输入流
            socket.connect(inetSocketAddress, 10 * 1000);
            // 链接成功
            if (socket.isConnected()) {
                // 初始化变量
                String sendStr;
                String recvStr;
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
                recvStr = "" + firstbyte;
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
                // 需要修改成XML格式
                recvStr = recvStr.replace("\r", "<br>");
                strReturnInfo = recvStr.replaceAll("[^\\x20-\\xff]", "");
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // 查询异常出错
        }
        if (strReturnInfo.length() > 0) {
            return strReturnInfo;
        }
        else {
            return "PNR生成失败";
        }
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

    public String dateToTimestampyyyyMMdd(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            if (date.length() == 10) {
                date = date + " 00:00:00";
            }
            dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            return dateFormat.parse(date).toString();

        }
        catch (Exception e) {
            return null;
        }

    }

    public String formatTimestampyyyyMMdd(Timestamp date) {
        try {
            return (new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(date));

        }
        catch (Exception e) {
            return "";
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

    /**
     * 取消PNR功能
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String XEPNR(String strPNR) {

        return "";
    }

    // 小PNR转大编码
    public String getPNRBigInfo(String strPNR) {
        return "";
    }

    /**
     * 分离PNR功能
     */
    public String SPPNR(String strPNR, String strNumber) {
        return "";
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
        return "";
    }

    /**
     * 处理某种Q信箱
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQS(String strQueryType) {
        return "";
    }

    /**
     * 释放Q信箱
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQNE() {
        return "";
    }

    /**
     * 释放当前处理下一个
     * 
     * @param strPNR
     *            PNR编码
     * @return
     */
    public String QMailQN() {
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
     * 得到大PNR
     * 
     * @param strPNR
     *            PNR
     * @return
     */
    public String getBigPNRInfo(String strPNR) {
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

    public String commandFunction2(String strcmd, String strPageinfo, String strIG) {
        return "";
    }

    public String CreatePNRByInterFace(List<Segmentinfo> listsegment, List<Passenger> listpassenger,
            String strCustomerCode) {
        return "";
    }

    /**
     * 国际查询机票接口
     */
    public AllRouteInfo getIntelFlightInfo(String strFromCity, String strTocity, String strFromDate, String ToDate,
            String strTripType, String strAirCoType) {
        return null;
    }

    public String getFullRTPnrResult(String strPNR) {
        String strReturn = "";
        return strReturn;
    }

    public List<Flightmodel> getFlightModelCache() {
        return null;
    }

    public String GetPriceAndDiscountByNFD(String strFromCity, String strToCity, String strFromDate, String strAirCom,
            String strCabinCode) {
        String strReturn = "";

        return strReturn;
    }

    public String commandFunction(String strcmd, String strPageinfo) {
        return "";
    }

    @SuppressWarnings("deprecation")
    public String createPNROFSingleSeg(Segmentinfo segent, List<Passenger> listpassengers, String connecttel) {

        try {
            SellSeat SellSeatexample = new SellSeat();
            /* 添加旅客姓名 */

            String airline = segent.getAircomapnycode();
            ;
            System.out.println("airline:" + airline);
            String idtype = "NI";
            System.out.println("idtype:" + idtype);
            /* 添加旅客身份证信息 */
            for (Passenger passenger : listpassengers) {
                String name = passenger.getName();
                System.out.println("name:" + name);
                String id = passenger.getIdnumber();
                System.out.println("id:" + id);
                SellSeatexample.addSSR_FOID(airline, idtype, id, name);
                SellSeatexample.addAdult(name);
            }

            /* 添加旅客乘坐航段信息 */
            String airNo = segent.getFlightnumber();
            System.out.println("airNo:" + airNo);
            char fltClass = segent.getCabincode().charAt(0);
            String orgCity = segent.getStartairport();
            System.out.println("orgCity:" + orgCity);
            String dstCity = segent.getEndairport();
            System.out.println("dstCity:" + dstCity);
            String actionCode = "NN";
            int tktNum = listpassengers.size();
            System.out.println("tktNum:" + tktNum);
            String departureTime = format.format(segent.getDeparttime());
            System.out.println("departureTime:" + departureTime);
            SellSeatexample.addAirSeg(airNo, fltClass, orgCity, dstCity, actionCode, tktNum, departureTime);

            /* 添加旅客联系组信息 */
            String contactinfo = connecttel;
            System.out.println("contactinfo:" + contactinfo);
            SellSeatexample.addContact(contactinfo);
            /* 添加旅客出票时限 */

            String dateLimit = departureTime + " 00:00:00";
            System.out.println("dateLimit:" + dateLimit);
            SellSeatexample.setTimelimit(dateLimit); /* 完成PNR必需信息输入，递交主机，生成PNR */
            SSResult ssr = SellSeatexample.commit1(); /* PNR结果 */
            System.out.println(ssr.getPnrno());

            return ssr.getPnrno();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String CreatePNRByCmd(List<Segmentinfo> listsegmenginf, List<Passenger> listpassengers,
            String strCustomerCode) {
        if (listsegmenginf.size() == 1) {
            String pnr = "";
            for (int i = 0; i < 3; i++) {
                pnr = this.createPNROFSingleSeg(listsegmenginf.get(0), listpassengers, strCustomerCode);
                if (pnr.length() > 0) {
                    break;
                }
            }
            return pnr;
        }
        // /* * 成人单人单航段预定示例 * * */ /*生成预定类对象*/

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
    public String AVOpen(FlightSearch flightSearch) {
        // TODO Auto-generated method stub
        return null;
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

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
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
