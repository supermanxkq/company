package com.ccservice.b2b2c.atom.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import client.JinRiFlightServerStub;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.ticket.Wrapper_CA;
import com.ccservice.b2b2c.atom.component.ticket.Wrapper_CH;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.ISearchFlightService;
import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.base.service.IAirService;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.zrate.Zrate;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.service.IRateService;
import com.tenpay.util.MD5Util;

/**
 * 接口公用方法
 * 
 * @author chendong 2013年6月12日14:17:16
 * 
 */
public class PublicComponent {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    //    Wrapper_CA wrapper_CA = new Wrapper_CA();

    Wrapper_CH wrapper_CH = new Wrapper_CH();

    /**
     * 获取携程的数据并融合到查询航班数据里面<br/>
     * 航班数大于0&&早八点到晚八点之间&&单程&&flightSearch.getTypeFlag()==1
     * @param flightalltemp
     * @param flightSearch
     * @return
     */
    protected List<FlightInfo> addCtripFlightData(List<FlightInfo> flightalltemp, FlightSearch flightSearch) {
        WriteLog.write(
                "SEARCH_ctrip",
                "addCtripFlightData:" + (flightalltemp.size() > 0) + ":" + isTimego() + ":"
                        + "1".equals(flightSearch.getTravelType()) + ":" + ("1".equals(flightSearch.getTypeFlag()))
                        + ":TypeFlag" + flightSearch.getTypeFlag() + ":" + flightSearch.getStartAirportCode()
                        + flightSearch.getEndAirportCode() + ":" + flightSearch.getFromDate());
        if (flightalltemp.size() > 0 && isTimego() && "1".equals(flightSearch.getTravelType())
        //        if (flightalltemp.size() > 0 && "1".equals(flightSearch.getTravelType())
                && "1".equals(flightSearch.getTypeFlag())) {
            flightalltemp = CtripMethod.getCtripFlightinfo(flightSearch.getStartAirportCode(),
                    flightSearch.getEndAirportCode(), flightSearch.getFromDate(), flightSearch.getFromDate(),
                    flightalltemp);
        }
        return flightalltemp;
    }

    /**
     * 获取去哪的数据并融合到查询航班数据里面
     * @param flightalltemp
     * @param flightSearch
     * @return
     */
    protected List<FlightInfo> addQunarFlightData(List<FlightInfo> flightalltemp, FlightSearch flightSearch) {
        try {
            Map<String, CarbinInfo> qunarPrice = QunarMethod.getQunarFlightinfo(flightSearch.getEndAirPortName(),
                    flightSearch.getStartAirPortName(), flightSearch.getFromDate(), flightSearch.getFromDate());
            for (int i = 0, size = flightalltemp.size(); i < size; i++) {
                String airline = flightalltemp.get(i).getAirline();
                String lowCabin = flightalltemp.get(i).getLowCarbin().getCabin();
                CarbinInfo speCarbinInfo = new CarbinInfo();
                if (qunarPrice.get(airline + "|" + flightSearch.getFromDate()) != null) {
                    CarbinInfo qunarspeCarbinInfo = qunarPrice.get(airline + "|" + flightSearch.getFromDate());

                    speCarbinInfo.setCabintypename(qunarspeCarbinInfo.getCabintypename());
                    speCarbinInfo.setCabin(qunarspeCarbinInfo.getCabin());
                    speCarbinInfo.setPrice(qunarspeCarbinInfo.getPrice());
                    speCarbinInfo.setDiscount(qunarspeCarbinInfo.getDiscount());
                    if (!speCarbinInfo.getCabin().equals(lowCabin)) {
                        flightalltemp.get(i).setSpeCarbinInfo(speCarbinInfo);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return flightalltemp;
    }

    /**
     * 根据sysconfig的name获得value
     * 内存中
     * @param name
     * @return
     */
    protected String getSysconfigString(String name) {
        String result = "-1";
        try {
            if (Server.getInstance().getDateHashMap().get(name) == null) {
                List<Sysconfig> sysoconfigs = Server.getInstance().getSystemService()
                        .findAllSysconfig("WHERE C_NAME='" + name + "'", "", -1, 0);
                if (sysoconfigs.size() > 0) {
                    WriteLog.write("TongchengSupplyMethod_getcustomeruser", "" + sysoconfigs.size());
                    result = sysoconfigs.get(0).getValue() != null ? sysoconfigs.get(0).getValue() : "-1";
                    Server.getInstance().getDateHashMap().put(name, result);
                }
            }
            else {
                result = Server.getInstance().getDateHashMap().get(name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取系统配置属性 实时
     */
    public static String getSystemConfig(String name) {
        List<Sysconfig> configs = Server.getInstance().getSystemService()
                .findAllSysconfig("where c_name='" + name + "'", "", -1, 0);
        if (configs != null && configs.size() == 1) {
            Sysconfig config = configs.get(0);
            return config.getValue();
        }
        return "-1";
    }

    /**
     * 根据存储过程获取信息
     * @time 2016年3月16日 上午10:17:23
     * @author chendong
     */
    public String getSysConfigByProcedure(String name) {
        //        String procedure = "[dbo].[sp_T_SysConfig_selectByName] @name = N'IsCreateInterPnr'";
        String procedure = "[dbo].[sp_T_SysConfig_selectByName] @name = N'" + name + "'";
        List list = Server.getInstance().getSystemService().findMapResultByProcedure(procedure);
        String value = "";
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            try {
                value = map.get("value") == null ? "" : map.get("value").toString();
            }
            catch (Exception e) {
            }
        }
        return value;
    }

    public List<FlightInfo> getFlightINfoby12580(FlightSearch flightSearch) {
        DateFormat fromat = new SimpleDateFormat("yyyy-MM-dd");
        String url = "http://www.yeebooking.com:9893/ccs_interface/Retransmission12580";

        String CHANNELID = "QD00016";
        String TERMINALID = "0006000001";
        //		String TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String TIMESTAMP = System.currentTimeMillis() + "";
        String REQUESTTYPE = "UNION_FLIGHTSEARCH";
        String SECURYKEY = "HTYHSECURYKEY";

        String DEPARTCITY = flightSearch.getStartAirportCode();
        String ARRIVECITY = flightSearch.getEndAirportCode();
        String DEPARTDATE = flightSearch.getFromDate();
        String CARRIER = flightSearch.getAirCompanyCode() == null ? "" : flightSearch.getAirCompanyCode();

        //API输入参数签名结果
        String REQUESTXML = "<REQUESTXML><SEARCHTYPE>S</SEARCHTYPE><ADTPSGCOUNT>1</ADTPSGCOUNT>"
                + "<CHDPSGCOUNT>0</CHDPSGCOUNT><INFPSGCOUNT>0</INFPSGCOUNT><ROUTES><FLIGHTROUTE SEQ=\"0\"><DEPARTCITY>"
                + DEPARTCITY + "</DEPARTCITY>" + "<ARRIVECITY>" + ARRIVECITY + "</ARRIVECITY><DEPARTDATE>" + DEPARTDATE
                + "</DEPARTDATE><TIME>0000</TIME><CARRIER>" + CARRIER + "</CARRIER>"
                + "<ISDIRECT>TRUE</ISDIRECT></FLIGHTROUTE></ROUTES></REQUESTXML>";
        String SIGNATURE = CHANNELID + TERMINALID + TIMESTAMP + REQUESTTYPE + SECURYKEY + REQUESTXML;
        WriteLog.write("12580", "加密前:" + SIGNATURE.replaceAll(" ", ""));
        SIGNATURE = MD5Util.MD5Encode(SIGNATURE.replaceAll(" ", ""), "utf-8");
        WriteLog.write("12580", "加密后:" + SIGNATURE);
        String paramContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?><REQUEST><HEADER CHANNELID=\"" + CHANNELID
                + "\" " + "CHANNELDEPID=\"\"  TERMINALID=\"" + TERMINALID + "\"  TIMESTAMP=\"" + TIMESTAMP
                + "\" REQUESTTYPE=\"" + REQUESTTYPE + "\" " + "SIGNATURE=\"" + SIGNATURE + "\" />" + REQUESTXML
                + "</REQUEST>";

        paramContent = "url=http://61.49.29.40:8181/openapi/ApiServlet&paramContent=" + paramContent;
        WriteLog.write("12580", url);
        WriteLog.write("12580", paramContent);
        String resultData = SendPostandGet.submitPost(url, paramContent, "UTF-8").toString().trim();
        WriteLog.write("12580", resultData);

        List<FlightInfo> listFlightInfoAll = new ArrayList<FlightInfo>();

        if (resultData.trim().length() < 10) {
            return listFlightInfoAll;
        }

        try {

            org.dom4j.Document document = org.dom4j.DocumentHelper.parseText(resultData);
            org.dom4j.Element root = document.getRootElement();
            org.dom4j.Element RESULTMSGelement = root.element("RESULTMSG");
            //请求处理成功！
            String resultString = RESULTMSGelement.getTextTrim();
            org.dom4j.Element HEADERelement = root.element("HEADER");
            //SUCCESS
            String RESULTString = HEADERelement.attributeValue("RESULT");

            org.dom4j.Element REQUESTRESULTelement = root.element("REQUESTRESULT");

            org.dom4j.Element ONEWAYFAREelement = REQUESTRESULTelement.element("ONEWAYFARE");

            List listPOLICY = REQUESTRESULTelement.element("ONEWAYFARE").elements("POLICY");
            Map<String, org.dom4j.Element> listPOLICYMap = new HashMap<String, org.dom4j.Element>();
            Iterator itlistPOLICY = listPOLICY.iterator();
            while (itlistPOLICY.hasNext()) {
                org.dom4j.Element elmt = (org.dom4j.Element) itlistPOLICY.next();
                String PID = elmt.attributeValue("PID");
                listPOLICYMap.put(PID, elmt);
            }

            List list = REQUESTRESULTelement.element("ROUTE").element("SEGMENTS").element("SEGMENT").element("FLIGHTS")
                    .elements("FLIGHT");
            Iterator it = list.iterator();

            List<Zrate> zrates = new ArrayList<Zrate>();
            String deleteWhere = "DELETE FROM T_ZRATE WHERE 1=2 ";
            String zratesave12580 = getSysconfigString("12580zratesave");
            while (it.hasNext()) {
                org.dom4j.Element elmt = (org.dom4j.Element) it.next();
                String DEPCITY = elmt.elementText("DEPCITY");
                String ARRCITY = elmt.elementText("ARRCITY");
                CARRIER = elmt.elementText("CARRIER");
                String ARRDATE = elmt.elementText("ARRDATE");
                String DEPTIME = elmt.elementText("DEPTIME");
                String ARRTIME = elmt.elementText("ARRTIME").split("[+]")[0];
                String FLIGHTNO = elmt.elementText("FLIGHTNO");
                String AIRPORTFEE = elmt.elementText("AIRPORTFEE");
                String FUELTAX = elmt.elementText("FUELTAX");
                String OUTTERMINAL = elmt.elementText("OUTTERMINAL");
                String PLANESTYLE = elmt.elementText("PLANESTYLE");
                String TPM = elmt.elementText("TPM");
                String YPRICE = elmt.elementText("YPRICE");
                String VIAPORT = elmt.elementText("VIAPORT");
                String INTERMINAL = elmt.elementText("INTERMINAL");
                String OWLOWPRICEPOLICYID = elmt.elementText("OWLOWPRICEPOLICYID");
                List CABINLIST = elmt.element("CABINLIST").elements("CABIN");
                //				System.out.println(CARRIER+FLIGHTNO+":"+CABINLIST.size());
                FlightInfo flightInfo = new FlightInfo();

                if ("BJS".equals(DEPCITY)) {
                    DEPCITY = "PEK";
                }
                if ("BJS".equals(ARRCITY)) {
                    ARRCITY = "PEK";
                }
                flightInfo.setStartAirport(DEPCITY);// 起飞机场
                flightInfo.setStartAirportName("");// 起飞起场名称
                flightInfo.setEndAirport(ARRCITY);// 到达机场
                flightInfo.setEndAirportName("");// 到达机场名称
                flightInfo.setStartAirportCity(DEPCITY);// 起飞起场城市名称
                flightInfo.setAirline(CARRIER + FLIGHTNO);// 航线
                flightInfo.setAirCompany(CARRIER);// 航空公司
                flightInfo.setAirCompanyName(CARRIER);// 航空公司名称
                flightInfo.setAirportFee(Float.parseFloat(AIRPORTFEE));// 机场建设费
                flightInfo.setFuelFee(Float.parseFloat(FUELTAX));// 燃油费
                flightInfo.setDistance(TPM);// 里程数
                flightInfo.setMeal(false);//有无餐食
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmm");
                try {
                    Date startDate = dateFormat.parse(ARRDATE + " " + DEPTIME);
                    Date arriveDate = dateFormat.parse(ARRDATE + " " + ARRTIME);
                    if (Integer.valueOf(ARRTIME) < Integer.valueOf(DEPTIME)) {
                        arriveDate = new Date(arriveDate.getTime() + 24 * 3600 * 1000);
                    }
                    flightInfo.setDepartTime(new Timestamp(startDate.getTime()));// 起飞时间
                    flightInfo.setArriveTime(new Timestamp(arriveDate.getTime()));// 到达时间
                }
                catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (false) {
                    flightInfo.setIsShare(1);// 是否是共享航班
                }
                else {
                    flightInfo.setIsShare(0);
                }
                if (false) {
                    flightInfo.setShareFlightNumber("");// 共享航班号
                }
                flightInfo.setAirplaneType(PLANESTYLE);// 飞机型号
                flightInfo.setAirplaneTypeDesc("");// 飞机型号描述
                if (INTERMINAL != null) {
                    flightInfo.setOffPointAT(INTERMINAL);// 到达航站楼
                }
                if (OUTTERMINAL != null) {
                    flightInfo.setBorderPointAT(OUTTERMINAL);// 出发航站楼
                }

                if (Integer.parseInt(VIAPORT) > 0) {
                    flightInfo.setStop(true);
                    flightInfo.setIsStopInfo(VIAPORT);
                }
                else {
                    flightInfo.setStop(false);
                }

                List<CarbinInfo> listCabinAll = new ArrayList<CarbinInfo>();

                Iterator CABINLISTit = CABINLIST.iterator();

                String TempYPrice = YPRICE;

                while (CABINLISTit.hasNext()) {
                    org.dom4j.Element CABINelmt = (org.dom4j.Element) CABINLISTit.next();
                    String CABINCODE = CABINelmt.elementText("CABINCODE");
                    String SETNUM = CABINelmt.elementText("SETNUM");
                    List PIDLIST = CABINelmt.element("ALLOWOWPOLICY").element("PIDS").elements("PID");
                    String ID = ((org.dom4j.Element) PIDLIST.get(0)).elementText("ID");
                    String[] ids = ID.split("[-]");
                    org.dom4j.Element elmtPOLICY = listPOLICYMap.get(ID);
                    if (elmtPOLICY == null)
                        continue;
                    org.dom4j.Element elmtPRICES = (org.dom4j.Element) elmtPOLICY.element("PRICES").elements("PRICE")
                            .get(0);
                    String TERMINALTKTBALANCEAMOUNT = elmtPRICES.elementText("TERMINALTKTBALANCEAMOUNT");
                    String DISTREALRATE = elmtPRICES.elementText("DISTREALRATE");
                    //退票规定
                    String REFUNDRMK = elmtPOLICY.elementText("REFUNDRMK").trim();
                    //改签规定
                    String ENDORSEMENTRMK = elmtPOLICY.elementText("ENDORSEMENTRMK").trim();
                    //签转规定
                    String SIGNRMK = elmtPOLICY.elementText("SIGNRMK").trim();
                    //ETTYPE  1(BSP)2(B2B)
                    String ETTYPE = elmtPOLICY.elementText("ETTYPE");

                    //政策特殊备注
                    String SPECIALRMK = elmtPOLICY.elementText("SPECIALRMK");
                    //供应商工作时间
                    String VENDORWORKTIMEHOUR = elmtPOLICY.elementText("VENDORWORKTIMEHOUR");

                    String ISSPECIAL = elmtPOLICY.elementText("ISSPECIAL");
                    String discount = elmtPOLICY.elementText("RATE");
                    String TICKETENDDATE = elmtPOLICY.elementText("TICKETENDDATE");

                    if (TempYPrice.length() == 0 && "Y".equals(CABINCODE)) {
                        TempYPrice = TERMINALTKTBALANCEAMOUNT;
                    }
                    //					System.out.println(DEPCITY+":"+ARRCITY+":"+CARRIER+FLIGHTNO+":"+OUTTERMINAL+"-"+INTERMINAL+":"+CABINCODE+":"
                    //							+SETNUM+":"+TERMINALTKTBALANCEAMOUNT+":"+AIRPORTFEE+":"+FUELTAX+":"+ENDORSEMENTRMK+"--------");

                    //是否保存12580的政策
                    if ("1".equals(zratesave12580)) {
                        Zrate zrate = new Zrate();
                        try {
                            //需要保存到数据库的12580的政策
                            zrate.setAgentid(11L);
                            zrate.setDepartureport(DEPCITY);
                            zrate.setArrivalport(ARRCITY);
                            zrate.setFlightnumber(CARRIER + FLIGHTNO);
                            zrate.setCabincode(CABINCODE);
                            zrate.setCreateuser("12580");
                            zrate.setCreatetime(new Timestamp(System.currentTimeMillis()));
                            zrate.setIsenable(1);
                            zrate.setAircompanycode(CARRIER);
                            zrate.setTickettype(Integer.parseInt(ETTYPE));
                            zrate.setOutid(ID);
                            zrate.setRemark("");
                            if (VENDORWORKTIMEHOUR.length() > 0) {
                                zrate.setWorktime(VENDORWORKTIMEHOUR.split(",")[0].split("-")[0]);
                                zrate.setAfterworktime(VENDORWORKTIMEHOUR.split(",")[0].split("-")[1]);
                            }
                            else {
                                zrate.setWorktime("09:00");
                                zrate.setAfterworktime("18:00");
                            }

                            zrate.setGeneral(1L);
                            zrate.setRatevalue(Float.parseFloat(DISTREALRATE));
                            zrate.setBegindate(new Timestamp(fromat.parse(DEPARTDATE).getTime()));

                            if (zrate.getRatevalue() > 2.5) {
                                deleteWhere += " OR (C_OUTID ='" + zrate.getOutid()
                                        + "' AND C_AGENTID=11 and C_DEPARTUREPORT='" + DEPCITY
                                        + "' and C_ARRIVALPORT='" + ARRCITY + "' and C_AIRCOMPANYCODE='" + CARRIER
                                        + "' and C_FLIGHTNUMBER='" + CARRIER + FLIGHTNO + "' and C_CABINCODE='"
                                        + CABINCODE + "')";
                                zrates.add(zrate);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    CarbinInfo cabin = new CarbinInfo();
                    String fsseatCode = CABINelmt.elementText("CABINCODE");// 舱位码
                    String seatStatus = SETNUM;// 舱位状态
                    if (discount.length() > 0) {
                        cabin.setDiscount(Float.parseFloat(discount) * 10);
                    }
                    if (seatStatus != null && seatStatus.equals("A")) {
                        cabin.setSeatNum("9");
                    }
                    else {
                        cabin.setSeatNum(seatStatus);
                    }
                    String cabinTypeName = "";//舱位名称
                    cabin.setCabin(fsseatCode);
                    if (cabinTypeName != null) {
                        cabin.setCabintypename(cabinTypeName);//舱位名称
                    }

                    if ("1".equals(ISSPECIAL)) {
                        cabin.setSpecial(true);
                    }
                    else {
                        cabin.setSpecial(false);
                    }
                    cabin.setPrice(Float.parseFloat(TERMINALTKTBALANCEAMOUNT));// 票面价

                    cabin.setUpdaterule(ENDORSEMENTRMK);
                    cabin.setRefundrule(REFUNDRMK);
                    cabin.setChangerule(SIGNRMK);

                    if (cabin.getPrice() > 0) {
                        listCabinAll.add(cabin);
                    }

                }

                flightInfo.setYPrice(Float.parseFloat(TempYPrice));// 全价价格

                //循环舱位计算折扣
                for (int i = 0; i < listCabinAll.size(); i++) {
                    Float discount = listCabinAll.get(i).getPrice() * 100 / flightInfo.getYPrice();// 折扣
                    discount = (float) discount.intValue();
                    if (listCabinAll.get(i).getDiscount() == null && discount >= 0) {
                        listCabinAll.get(i).setDiscount(discount);
                    }
                }

                Collections.sort(listCabinAll, new Comparator<CarbinInfo>() {
                    @Override
                    public int compare(CarbinInfo o1, CarbinInfo o2) {
                        if (o1.getPrice() == null || o2.getPrice() == null) {
                            return 1;
                        }
                        else {
                            // TODO Discount排序
                            if (o1.getPrice() > o2.getPrice()) {
                                return 1;
                            }
                            else if (o1.getPrice() < o2.getPrice()) {
                                return -1;

                            }
                        }
                        return 0;
                    }
                });

                flightInfo.setCarbins(listCabinAll);// 加入 仓位信息

                int cabinIndex = 0;
                CarbinInfo lowCabinInfo = new CarbinInfo();
                for (int i = 0; i < listCabinAll.size(); i++) {
                    if (listCabinAll.get(i).getPrice() > 0) {
                        cabinIndex = i;
                        break;
                    }
                }
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
                        lowCabinInfo.setCabinRemark("");
                    }
                    if (tempCabinInfo.getCabinRules() != null) {
                        lowCabinInfo.setCabinRules(tempCabinInfo.getCabinRules());
                    }
                    else {
                        lowCabinInfo.setCabinRules("");
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
                        lowCabinInfo.setCabintypename("");
                    }
                    if (tempCabinInfo.isSpecial()) {
                        lowCabinInfo.setSpecial(true);
                    }
                    else {
                        lowCabinInfo.setSpecial(false);
                    }

                    flightInfo.setLowCarbin(lowCabinInfo);
                    //					flightInfo.getCarbins().remove(0);
                }
                else {
                    continue;
                }
                listFlightInfoAll.add(flightInfo);
            }
            //System.out.println(deleteWhere);.

            try {
                if ("1".equals(zratesave12580)) {
                    //先删除后添加的12580的政策信息
                    String zrateurl = getSysconfigString("12580zrateurl");
                    String url1 = zrateurl + "/cn_service/service/";
                    HessianProxyFactory factory = new HessianProxyFactory();
                    IAirService iAirService = (IAirService) factory.create(IAirService.class,
                            url1 + IAirService.class.getSimpleName());
                    iAirService.excuteZrateBySql(deleteWhere);
                    if (zrates.size() > 0) {
                        iAirService.createZrateList(zrates);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }

        }
        catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return listFlightInfoAll;

    }

    public static List<FlightInfo> searchFlightByJINRI(FlightSearch flightSearch) {
        List<FlightInfo> listFlightInfoAll = new ArrayList<FlightInfo>();
        try {
            JinRiFlightServerStub stub = new JinRiFlightServerStub();
            JinRiFlightServerStub.GetFlightList_V1 getFlightList_V1 = new JinRiFlightServerStub.GetFlightList_V1();
            StringBuffer str = new StringBuffer();
            str.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
            str.append("<JIT-Flight-Request>");
            str.append("<Request username=\"hthuayou\" scity=\"" + flightSearch.getStartAirportCode() + "\" ecity=\""
                    + flightSearch.getEndAirportCode() + "\" date=\"" + flightSearch.getFromDate()
                    + "\" cabin=\"A\" isshosspecial=\"T\" />");
            str.append("</JIT-Flight-Request>");
            //			System.out.println(str.toString());
            getFlightList_V1.setData(str.toString());
            JinRiFlightServerStub.GetFlightList_V1Response response = stub.getFlightList_V1(getFlightList_V1);
            String result = response.getGetFlightList_V1Result();
            //			String result = "<?xml version=\"1.0\" encoding=\"gb2312\"?><JIT-Flight-Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"HO1252\" AirLine=\"HO\" FlightType=\"320\" Stime=\"06:35\" Etime=\"08:45\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"R\" N=\"A\" D=\"45\" P=\"510\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"50\" P=\"570\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"55\" P=\"620\" T=\"0\" /><Cabin C=\"V\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"65\" P=\"730\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"70\" P=\"790\" T=\"0\" /><Cabin C=\"T\" N=\"A\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"A\" N=\"8\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"8\" D=\"270\" P=\"3050\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5138\" AirLine=\"MU\" FlightType=\"333\" Stime=\"07:00\" Etime=\"09:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Z\" N=\"A\" D=\"\" P=\"380\" T=\"1\" /><Cabin C=\"V\" N=\"A\" D=\"50\" P=\"570\" T=\"0\" /><Cabin C=\"S\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"R\" N=\"A\" D=\"65\" P=\"730\" T=\"0\" /><Cabin C=\"N\" N=\"A\" D=\"70\" P=\"780\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"73\" P=\"820\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"77\" P=\"870\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"81\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"95\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"P\" N=\"A\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1831\" AirLine=\"CA\" FlightType=\"33A\" Stime=\"07:30\" Etime=\"09:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"V\" N=\"A\" D=\"45\" P=\"510\" T=\"0\" /><Cabin C=\"G\" N=\"A\" D=\"50\" P=\"570\" T=\"0\" /><Cabin C=\"Q\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"70\" P=\"790\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"O\" N=\"3\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"990\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"100\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"160\" P=\"1130\" T=\"0\" /><Cabin C=\"A\" N=\"6\" D=\"80\" P=\"2830\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"FM9108\" AirLine=\"FM\" FlightType=\"75B\" Stime=\"07:35\" Etime=\"09:45\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Z\" N=\"A\" D=\"\" P=\"380\" T=\"1\" /><Cabin C=\"V\" N=\"A\" D=\"45\" P=\"570\" T=\"0\" /><Cabin C=\"S\" N=\"A\" D=\"50\" P=\"680\" T=\"0\" /><Cabin C=\"R\" N=\"A\" D=\"55\" P=\"730\" T=\"0\" /><Cabin C=\"N\" N=\"A\" D=\"60\" P=\"780\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"65\" P=\"820\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"70\" P=\"870\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"75\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"80\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"85\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"90\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"P\" N=\"8\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"230\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"HU7607\" AirLine=\"HU\" FlightType=\"340\" Stime=\"07:50\" Etime=\"09:55\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T1,T2\"><Cabins><Cabin C=\"Q\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"70\" P=\"790\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"A\" N=\"2\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"Z\" N=\"6\" D=\"0\" P=\"1470\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"300\" P=\"2830\" T=\"0\" /><Cabin C=\"R\" N=\"8\" D=\"380\" P=\"3500\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5102\" AirLine=\"MU\" FlightType=\"333\" Stime=\"08:00\" Etime=\"10:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Z\" N=\"A\" D=\"\" P=\"380\" T=\"1\" /><Cabin C=\"S\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"R\" N=\"A\" D=\"65\" P=\"730\" T=\"0\" /><Cabin C=\"N\" N=\"A\" D=\"70\" P=\"780\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"73\" P=\"820\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"77\" P=\"870\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"81\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"95\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"P\" N=\"8\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1501\" AirLine=\"CA\" FlightType=\"773\" Stime=\"08:30\" Etime=\"10:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"G\" N=\"A\" D=\"50\" P=\"570\" T=\"0\" /><Cabin C=\"Q\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"70\" P=\"790\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"O\" N=\"3\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"990\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"100\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"A\" N=\"8\" D=\"80\" P=\"2830\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /><Cabin C=\"P\" N=\"8\" D=\"300\" P=\"3390\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5166\" AirLine=\"MU\" FlightType=\"76A\" Stime=\"08:30\" Etime=\"10:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Z\" N=\"A\" D=\"\" P=\"380\" T=\"1\" /><Cabin C=\"V\" N=\"A\" D=\"50\" P=\"570\" T=\"0\" /><Cabin C=\"S\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"R\" N=\"A\" D=\"65\" P=\"730\" T=\"0\" /><Cabin C=\"N\" N=\"A\" D=\"70\" P=\"780\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"73\" P=\"820\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"77\" P=\"870\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"81\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"95\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"P\" N=\"8\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"HU7605\" AirLine=\"HU\" FlightType=\"767\" Stime=\"08:45\" Etime=\"10:55\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T1,T2\"><Cabins><Cabin C=\"Q\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"70\" P=\"790\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"A\" N=\"7\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"Z\" N=\"A\" D=\"0\" P=\"1470\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"300\" P=\"2830\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5104\" AirLine=\"MU\" FlightType=\"333\" Stime=\"09:00\" Etime=\"11:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Z\" N=\"A\" D=\"\" P=\"450\" T=\"1\" /><Cabin C=\"S\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"R\" N=\"A\" D=\"65\" P=\"730\" T=\"0\" /><Cabin C=\"N\" N=\"A\" D=\"70\" P=\"780\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"73\" P=\"820\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"77\" P=\"870\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"81\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"95\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"P\" N=\"7\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1519\" AirLine=\"CA\" FlightType=\"77S\" Stime=\"09:30\" Etime=\"11:35\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"G\" N=\"A\" D=\"50\" P=\"570\" T=\"0\" /><Cabin C=\"Q\" N=\"A\" D=\"60\" P=\"680\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"70\" P=\"790\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"O\" N=\"2\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"990\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"100\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"160\" P=\"1130\" T=\"0\" /><Cabin C=\"A\" N=\"A\" D=\"80\" P=\"2830\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5106\" AirLine=\"MU\" FlightType=\"333\" Stime=\"09:55\" Etime=\"11:55\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Z\" N=\"A\" D=\"\" P=\"450\" T=\"1\" /><Cabin C=\"N\" N=\"A\" D=\"70\" P=\"780\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"73\" P=\"820\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"77\" P=\"870\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"81\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"95\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"P\" N=\"A\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1531\" AirLine=\"CA\" FlightType=\"77S\" Stime=\"10:30\" Etime=\"12:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"L\" N=\"A\" D=\"70\" P=\"790\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"990\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"100\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"160\" P=\"1130\" T=\"0\" /><Cabin C=\"A\" N=\"2\" D=\"80\" P=\"2830\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5108\" AirLine=\"MU\" FlightType=\"333\" Stime=\"11:00\" Etime=\"13:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Z\" N=\"A\" D=\"\" P=\"450\" T=\"1\" /><Cabin C=\"H\" N=\"A\" D=\"81\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"95\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1557\" AirLine=\"CA\" FlightType=\"77L\" Stime=\"11:30\" Etime=\"13:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"H\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"990\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"100\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"160\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5146\" AirLine=\"MU\" FlightType=\"76A\" Stime=\"11:30\" Etime=\"13:45\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Z\" N=\"A\" D=\"\" P=\"450\" T=\"1\" /><Cabin C=\"H\" N=\"A\" D=\"81\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"95\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"P\" N=\"7\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5110\" AirLine=\"MU\" FlightType=\"333\" Stime=\"11:55\" Etime=\"14:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5112\" AirLine=\"MU\" FlightType=\"333\" Stime=\"12:55\" Etime=\"15:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1517\" AirLine=\"CA\" FlightType=\"773\" Stime=\"13:30\" Etime=\"15:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"H\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"990\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"100\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /><Cabin C=\"P\" N=\"8\" D=\"300\" P=\"3390\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5114\" AirLine=\"MU\" FlightType=\"33E\" Stime=\"14:00\" Etime=\"16:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1521\" AirLine=\"CA\" FlightType=\"773\" Stime=\"14:30\" Etime=\"16:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"H\" N=\"3\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"M\" N=\"4\" D=\"90\" P=\"990\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"100\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /><Cabin C=\"P\" N=\"4\" D=\"300\" P=\"3390\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5140\" AirLine=\"MU\" FlightType=\"76A\" Stime=\"14:30\" Etime=\"16:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5116\" AirLine=\"MU\" FlightType=\"333\" Stime=\"15:00\" Etime=\"17:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1515\" AirLine=\"CA\" FlightType=\"773\" Stime=\"15:30\" Etime=\"17:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /><Cabin C=\"P\" N=\"8\" D=\"300\" P=\"3390\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5200\" AirLine=\"MU\" FlightType=\"333\" Stime=\"15:45\" Etime=\"18:00\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5118\" AirLine=\"MU\" FlightType=\"333\" Stime=\"16:00\" Etime=\"18:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1549\" AirLine=\"CA\" FlightType=\"77S\" Stime=\"16:30\" Etime=\"18:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"160\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5120\" AirLine=\"MU\" FlightType=\"333\" Stime=\"17:00\" Etime=\"19:15\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1855\" AirLine=\"CA\" FlightType=\"77S\" Stime=\"17:30\" Etime=\"19:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"160\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"HU7601\" AirLine=\"HU\" FlightType=\"787\" Stime=\"17:35\" Etime=\"19:45\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T1,T2\"><Cabins><Cabin C=\"L\" N=\"5\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"A\" N=\"6\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"Z\" N=\"A\" D=\"0\" P=\"1700\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"300\" P=\"2830\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5122\" AirLine=\"MU\" FlightType=\"333\" Stime=\"18:00\" Etime=\"20:15\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CZ3907\" AirLine=\"CZ\" FlightType=\"333\" Stime=\"18:05\" Etime=\"20:20\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"M\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"250\" P=\"3160\" T=\"0\" /><Cabin C=\"A\" N=\"4\" D=\"400\" P=\"4520\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1885\" AirLine=\"CA\" FlightType=\"32A\" Stime=\"18:30\" Etime=\"20:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5124\" AirLine=\"MU\" FlightType=\"333\" Stime=\"19:00\" Etime=\"21:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1857\" AirLine=\"CA\" FlightType=\"77L\" Stime=\"19:30\" Etime=\"21:50\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"W\" N=\"A\" D=\"160\" P=\"1130\" T=\"0\" /><Cabin C=\"A\" N=\"1\" D=\"80\" P=\"2830\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5126\" AirLine=\"MU\" FlightType=\"333\" Stime=\"20:00\" Etime=\"22:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"CA1589\" AirLine=\"CA\" FlightType=\"330\" Stime=\"20:30\" Etime=\"22:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T3,T2\"><Cabins><Cabin C=\"H\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"990\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"100\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"A\" N=\"1\" D=\"80\" P=\"2830\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"2\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"FM9106\" AirLine=\"FM\" FlightType=\"75A\" Stime=\"20:30\" Etime=\"22:45\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"230\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"HU7603\" AirLine=\"HU\" FlightType=\"738\" Stime=\"20:50\" Etime=\"22:55\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T1,T2\"><Cabins><Cabin C=\"M\" N=\"A\" D=\"70\" P=\"790\" T=\"0\" /><Cabin C=\"L\" N=\"A\" D=\"75\" P=\"850\" T=\"0\" /><Cabin C=\"K\" N=\"A\" D=\"80\" P=\"900\" T=\"0\" /><Cabin C=\"H\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"Z\" N=\"5\" D=\"0\" P=\"1470\" T=\"1\" /><Cabin C=\"F\" N=\"7\" D=\"300\" P=\"2830\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5128\" AirLine=\"MU\" FlightType=\"33E\" Stime=\"21:00\" Etime=\"23:10\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5162\" AirLine=\"MU\" FlightType=\"76A\" Stime=\"21:30\" Etime=\"23:40\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response><Response Sdate=\"2013-11-29\" Scity=\"PEK\" Ecity=\"SHA\" FlightNo=\"MU5182\" AirLine=\"MU\" FlightType=\"333\" Stime=\"22:00\" Etime=\"23:55\" Stop=\"0\" EPiao=\"E\" Tax=\"170\" AirTerminal=\"T2,T2\"><Cabins><Cabin C=\"H\" N=\"A\" D=\"81\" P=\"920\" T=\"0\" /><Cabin C=\"E\" N=\"A\" D=\"85\" P=\"960\" T=\"0\" /><Cabin C=\"M\" N=\"A\" D=\"90\" P=\"1020\" T=\"0\" /><Cabin C=\"B\" N=\"A\" D=\"95\" P=\"1070\" T=\"0\" /><Cabin C=\"Y\" N=\"A\" D=\"100\" P=\"1130\" T=\"0\" /><Cabin C=\"P\" N=\"A\" D=\"0\" P=\"1130\" T=\"1\" /><Cabin C=\"F\" N=\"A\" D=\"280\" P=\"3160\" T=\"0\" /></Cabins></Response></JIT-Flight-Response>";

            if (result.length() > 10) {
                org.dom4j.Document document = DocumentHelper.parseText(result);
                org.dom4j.Element root = document.getRootElement();
                List list = root.elements("Response");
                Iterator it = list.iterator();
                System.out.println("JINRI:" + list.size() + "-" + flightSearch.getStartAirportCode() + "-"
                        + flightSearch.getEndAirportCode() + "-" + flightSearch.getFromDate());
                while (it.hasNext()) {
                    try {
                        org.dom4j.Element elmt = (org.dom4j.Element) it.next();
                        String Sdate = elmt.attributeValue("Sdate");
                        String Scity = elmt.attributeValue("Scity");
                        String Ecity = elmt.attributeValue("Ecity");
                        String FlightNo = elmt.attributeValue("FlightNo");
                        String AirLine = elmt.attributeValue("AirLine");
                        String FlightType = elmt.attributeValue("FlightType");
                        String Stime = elmt.attributeValue("Stime");//08:15
                        String Etime = elmt.attributeValue("Etime");
                        String Stop = elmt.attributeValue("Stop");
                        String EPiao = elmt.attributeValue("EPiao");
                        String Tax = elmt.attributeValue("Tax");
                        String AirTerminal = elmt.attributeValue("AirTerminal");
                        //					System.out.println(Sdate + "-" + Scity + "-" + Ecity + "-"
                        //							+ FlightNo + "-" + AirLine + "-" + FlightType + "-"
                        //							+ Stime + "-" + Etime + "-" + Stop + "-" + EPiao
                        //							+ "-" + Tax + "-" + AirTerminal);
                        FlightInfo flightInfo = new FlightInfo();
                        flightInfo.setStartAirport(Scity);// 起飞机场
                        flightInfo.setStartAirportName("");// 起飞起场名称
                        flightInfo.setEndAirport(Ecity);// 到达机场
                        flightInfo.setEndAirportName("");// 到达机场名称
                        flightInfo.setStartAirportCity(Scity);// 起飞起场城市名称
                        flightInfo.setAirline(FlightNo);// 航线
                        flightInfo.setAirCompany(AirLine);// 航空公司
                        flightInfo.setAirCompanyName(AirLine);// 航空公司名称
                        flightInfo.setAirportFee(50);// 机场建设费
                        flightInfo.setFuelFee(Float.parseFloat(Tax) - 50);// 燃油费
                        flightInfo.setDistance("0");// 里程数
                        flightInfo.setMeal(false);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm");
                        Date startDate = dateFormat.parse(Sdate + Stime);
                        Date arriveDate = dateFormat.parse(Sdate + Etime);
                        flightInfo.setDepartTime(new Timestamp(startDate.getTime()));// 起飞时间
                        flightInfo.setArriveTime(new Timestamp(arriveDate.getTime()));// 到达时间
                        //				flightInfo.setYPrice(basePrice.floatValue());// 全价价格
                        //				if (codeShare != null && codeShare) {
                        //					flightInfo.setIsShare(1);// 是否是共享航班
                        //				} else {
                        //					flightInfo.setIsShare(0);
                        //				}
                        //				if (shareNum != null) {
                        //					flightInfo.setShareFlightNumber(shareNum);// 共享航班号
                        //				}
                        flightInfo.setAirplaneType(FlightType);// 飞机型号
                        flightInfo.setAirplaneTypeDesc("");// 飞机型号描述
                        flightInfo.setOffPointAT(AirTerminal.split(",")[0]);//出发航站楼
                        flightInfo.setBorderPointAT(AirTerminal.split(",")[1]);//到达航站楼
                        if (Stop.equals("1")) {
                            flightInfo.setStop(true);
                            flightInfo.setIsStopInfo("1");
                        }
                        else {
                            flightInfo.setStop(false);
                        }
                        List cabinlist = elmt.element("Cabins").elements("Cabin");
                        List<CarbinInfo> listCabinAll = new ArrayList<CarbinInfo>();
                        CarbinInfo lowCabinInfo = new CarbinInfo();
                        for (int i = 0; i < cabinlist.size(); i++) {
                            org.dom4j.Element cabinelmt = (Element) cabinlist.get(i);
                            CarbinInfo cabin = new CarbinInfo();
                            cabin.setCabintypename(cabinelmt.attributeValue("C"));
                            cabin.setCabin(cabinelmt.attributeValue("C"));
                            if (cabinelmt.attributeValue("N") != null && cabinelmt.attributeValue("N").equals("A")) {
                                cabin.setSeatNum("9");
                            }
                            else {
                                cabin.setSeatNum(cabinelmt.attributeValue("N"));
                            }
                            if (cabinelmt.attributeValue("D").length() > 0) {
                                cabin.setDiscount(Float.parseFloat(cabinelmt.attributeValue("D")));
                            }
                            if ("1".equals(cabinelmt.attributeValue("T"))) {
                                cabin.setSpecial(true);
                            }
                            else {
                                cabin.setSpecial(false);
                            }
                            cabin.setPrice(Float.parseFloat(cabinelmt.attributeValue("P")));
                            if ("Y".equals(cabinelmt.attributeValue("C"))) {
                                flightInfo.setYPrice(cabin.getPrice());
                            }
                            Zrate zrate = new Zrate();
                            zrate.setRatevalue(2.5F);
                            cabin.setZrate(zrate);
                            listCabinAll.add(cabin);
                        }
                        if (listCabinAll.size() > 0) {
                            Collections.sort(listCabinAll, new Comparator<CarbinInfo>() {
                                @Override
                                public int compare(CarbinInfo o1, CarbinInfo o2) {
                                    if (o1.getPrice() == null || o2.getPrice() == null) {
                                        return 1;
                                    }
                                    else {
                                        // TODO Discount排序
                                        if (o1.getPrice() > o2.getPrice()) {
                                            return 1;
                                        }
                                        else if (o1.getPrice() < o2.getPrice()) {
                                            return -1;
                                        }
                                    }
                                    return 0;
                                }
                            });
                        }
                        flightInfo.setCarbins(listCabinAll);

                        for (int j = 0; j < listCabinAll.size(); j++) {
                            if ((listCabinAll.get(j).getDiscount() == null || listCabinAll.get(j).getDiscount() == 0)
                                    && flightInfo.getYPrice() != null) {
                                Float discount = listCabinAll.get(j).getPrice() / flightInfo.getYPrice() * 100;
                                DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();
                                format.applyPattern("###00");
                                try {
                                    String discount_S = format.format(discount);
                                    listCabinAll.get(j).setDiscount(Float.parseFloat(discount_S));
                                }
                                catch (Exception e) {
                                }
                            }
                        }

                        CarbinInfo tempCabinInfo = (CarbinInfo) listCabinAll.get(0);
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
                            lowCabinInfo.setCabinRemark("");
                        }
                        if (tempCabinInfo.getCabinRules() != null) {
                            lowCabinInfo.setCabinRules(tempCabinInfo.getCabinRules());
                        }
                        else {
                            lowCabinInfo.setCabinRules("");
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
                            lowCabinInfo.setCabintypename("");
                        }
                        if (tempCabinInfo.isSpecial()) {
                            lowCabinInfo.setSpecial(true);
                        }
                        else {
                            lowCabinInfo.setSpecial(false);
                        }

                        flightInfo.setLowCarbin(lowCabinInfo);

                        listFlightInfoAll.add(flightInfo);
                    }
                    catch (ParseException e) {
                        // TODO: handle exception
                    }
                }
            }
            else {
                WriteLog.write("jinriSearchflightERR", str.toString());
                WriteLog.write("jinriSearchflightERR", result);
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
            return listFlightInfoAll;
        }
        catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return listFlightInfoAll;
        }
        catch (Error e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return listFlightInfoAll;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return listFlightInfoAll;
        }
        return listFlightInfoAll;
    }

    /**
     * 判断当前时间是否在某一个时间段
     * 目前是判断当前时间是否在周一到周五的早8点(含)到晚8点(不含)之间
     * 
     */
    public boolean isTimego() {
        int hours = new Date().getHours();
        int days = new Date().getHours();
        //        if (hours >= 8 && hours < 20 && !isweekday()) {
        if (hours >= 8 && hours < 20) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 判断当前时间是否周六日，如果在周六日返回true
     */
    public boolean isweekday() {
        if (new Date().getDay() == 0 || new Date().getDay() == 6) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 不带年份
     * 把20140102转化为02JAN
     * @param dateStr
     * @return
     */
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

    /**
     * 带年份
     * 把20140102转化为02JAN2014
     * @param dateStr
     * @return
     */
    public String ChangeDateModeYear(String dateStr) {
        // 2009-03-19
        String newmon = "";
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

    /**
     * 将money格式化为类似于2,243,234.00的格式
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

    /**
     * 把Timestamp转化为HHmm格式的时间
     * @param date
     * @return
     */
    public String formatTimestampPID(Timestamp date) {
        try {
            return (new SimpleDateFormat("HHmm").format(date));
        }
        catch (Exception e) {
            return "";
        }

    }

    /**
     * 把Timestamp转化为yyyy-MM-dd格式的时间
     * @param date
     * @return
     */
    public String formatTimestampyyyyMMdd(Timestamp date) {
        try {
            return (new SimpleDateFormat("yyyy-MM-dd").format(date));
        }
        catch (Exception e) {
            return "";
        }

    }

    /**
     * 把date(yyyy-MM-dd或者yyyy-MM-dd HH:mm:ss)
     * 转化为Timestamp格式的时间
     * @param date
     * @return
     */
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

    public List<FlightInfo> searchbaseFlightinfo(FlightSearch flightSearch, String isinterface) {
        Long t1 = System.currentTimeMillis();
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        // 类型为1调用新的查询接口 51book接口
        if ("1".equals(isinterface)) {
            flightalltemp = searchFlightByNewInteface(flightSearch);
            if (flightalltemp.size() == 0) {
                flightalltemp = searchFlightBy12580(flightSearch);
            }
        }
        else if (isinterface.equals("0")) {//都是用携程的航班查询
            try {
                flightalltemp = CtripMethod.getCtripFlightinfo_byinferface(flightSearch.getStartAirportCode(),
                        flightSearch.getEndAirportCode(), flightSearch.getFromDate(), flightSearch.getBackDate(),
                        flightSearch.getAirCompanyCode(), null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (flightalltemp == null || flightalltemp.size() == 0) {
                try {
                    flightalltemp = searchFlightByFiveoneBookInterface(flightSearch);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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
            flightalltemp = searchFlightBy12580(flightSearch);
        }
        else if (isinterface.equals("5")) {
            try {
                flightalltemp = searchFlightByFiveoneBookInterface(flightSearch);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (flightalltemp.size() == 0) {//
            //                flightalltemp = getCtripData(flightSearch);//携程的数据
            }
        }
        else if (isinterface.equals("6")) {
            //            flightalltemp = getFormatValuesV2tByKABEInterface(flightSearch);
            flightalltemp = searchFlightByFiveoneBookInterface(flightSearch);
        }
        else if (isinterface.equals("7")) {
            flightalltemp = getFlightINfoby12580(flightSearch);
            if (flightalltemp.size() == 0) {
                try {
                    flightalltemp = searchFlightByFiveoneBookInterface(flightSearch);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (flightalltemp.size() == 0) {
                    flightalltemp = searchFlightByNewInteface(flightSearch);
                }
            }
        }
        else if (isinterface.equals("8")) {
            flightalltemp = searchFlightByJINRI(flightSearch);

            if (flightalltemp.size() == 0) {
                try {
                    flightalltemp = searchFlightByFiveoneBookInterface(flightSearch);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (flightalltemp.size() == 0) {
                    flightalltemp = searchFlightByNewInteface(flightSearch);
                }
            }
        }
        else if (isinterface.equals("9")) {
            try {
                flightalltemp = CtripMethod.getCtripFlightinfo_byinferface(flightSearch.getStartAirportCode(),
                        flightSearch.getEndAirportCode(), flightSearch.getFromDate(), flightSearch.getBackDate(),
                        flightSearch.getAirCompanyCode(), null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (isinterface.equals("10")) {//航空公司官网的数据
            //            String html = wrapper_CA.getHtml(flightSearch);
            //            flightalltemp = wrapper_CA.process(html, flightSearch);
            String html = wrapper_CH.getHtml(flightSearch);
            flightalltemp = wrapper_CH.process(html, flightSearch);
        }
        System.out.println(flightSearch.getStartAirportCode() + flightSearch.getEndAirportCode()
                + flightSearch.getFromDate() + ":" + flightalltemp.size() + ":time:"
                + (System.currentTimeMillis() - t1));
        return flightalltemp;
    }

    /**
     * 
     * @param flightSearch
     * @return
     * @time 2016年7月1日 上午10:44:48
     * @author chendong
     */
    private List<FlightInfo> getCtripData(FlightSearch flightSearch) {
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        try {
            flightalltemp = CtripMethod.getCtripFlightinfo_byinferface(flightSearch.getStartAirportCode(),
                    flightSearch.getEndAirportCode(), flightSearch.getFromDate(), flightSearch.getBackDate(),
                    flightSearch.getAirCompanyCode(), null);
        }
        catch (Exception e) {
        }
        return flightalltemp;
    }

    private List<FlightInfo> searchFlightByFiveoneBookInterface(FlightSearch flightSearch) {
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        //        String url = getSysconfigString("51flighturl") + "/ticket_inter/service/";
        //        String url = "http://192.168.0.40:19010/ticket_inter/service/";
        String url = PropertyUtil.getValue("51flighturl", "air.properties");
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            IRateService servier11 = (IRateService) factory.create(IRateService.class,
                    url + IRateService.class.getSimpleName());
            int onlyNormalCommision = flightSearch.getGeneral() - 1;
            flightalltemp = servier11.getAvailableFlightWithPriceAndCommision(flightSearch.getStartAirportCode(),
                    flightSearch.getEndAirportCode(), flightSearch.getFromDate(), flightSearch.getAirCompanyCode(), 1,
                    onlyNormalCommision, 0, 0);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
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
        //       flightalltemp=Server.getInstance().getRateService().SeachFlight(flightSearch.getStartAirportCode(),
        //       flightSearch.getEndAirportCode(), flightSearch.getAirCompanyCode(),
        //       flightSearch.getFromDate(), "");
        //       flightalltemp=Server.getInstance().getSearchFiveoneFlightService().SeachFiveoneFlight(flightSearch.getStartAirportCode(),
        //       flightSearch.getEndAirportCode(), flightSearch.getAirCompanyCode(),
        //       flightSearch.getFromDate(), "");
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

        return flightalltemp;
    }

    /**
     * 使用htmlunit查询12580国内机票
     * 
     * @param list
     * @return
     */
    public List<FlightInfo> searchFlightBy12580(FlightSearch flightSearch) {
        List<FlightInfo> flightalltemp = new ArrayList<FlightInfo>();
        //      String url = "http://localhost:8080/Reptile/ReptileGet12580";
        String url = "http://223.4.155.3:9822/Reptile/ReptileGet12580";
        String paramContent = "";
        //出发城市名
        paramContent += "StartAirPortName=" + flightSearch.getStartAirPortName()
        //出发机场3字码
                + "&StartAirportCode=" + flightSearch.getStartAirportCode()
                //抵达城市名
                + "&EndAirPortName=" + flightSearch.getEndAirPortName()
                //抵达机场3字码
                + "&EndAirportCode=" + flightSearch.getEndAirportCode()
                //出发时间
                + "&FromDate=" + flightSearch.getFromDate();
        StringBuffer flightStr = SendPostandGet.submitPost(url, paramContent, "utf-8");
        String sub = flightStr.toString();
        String date = flightSearch.getFromDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        JSONArray flights = JSONArray.fromObject(sub);
        try {
            if (flights != null && flights.size() > 0) {
                for (int i = 0; i < flights.size(); i++) {
                    JSONObject flightObj = flights.getJSONObject(i);
                    FlightInfo flight = new FlightInfo();
                    String flightNum = flightObj.getString("ShareFlightNumber");
                    //航班号
                    flight.setAirline(flightNum);
                    //航空公司代码
                    flight.setAirCompany(flightNum.substring(0, 2));
                    //起飞机场名称
                    String StartAirportName = flightObj.getString("StartAirportName");
                    //抵达机场名称
                    String EndAirportName = flightObj.getString("StartAirportName");
                    //起飞机场3字码
                    if ("PEK".equals(flightSearch.getStartAirportCode())
                            || "SHA".equals(flightSearch.getStartAirportCode())) {
                        if (StartAirportName.indexOf("首都国际") > -1) {
                            flight.setStartAirport("PEK");
                        }
                        else if (StartAirportName.indexOf("南苑") > -1) {
                            flight.setStartAirport("NAY");
                        }
                        else if (StartAirportName.indexOf("虹桥") > -1) {
                            flight.setStartAirport("SHA");
                        }
                        else if (StartAirportName.indexOf("浦东") > -1) {
                            flight.setStartAirport("PVG");
                        }

                    }
                    else {
                        flight.setStartAirport(flightSearch.getStartAirportCode());
                    }
                    //设置起飞机场名称
                    flight.setStartAirportName(StartAirportName);
                    //抵达机场3字码
                    if ("PEK".equals(flightSearch.getEndAirportCode())
                            || "SHA".equals(flightSearch.getEndAirportCode())) {
                        if (EndAirportName.indexOf("首都国际") > -1) {
                            flight.setEndAirport("PEK");
                        }
                        else if (EndAirportName.indexOf("南苑") > -1) {
                            flight.setEndAirport("NAY");
                        }
                        else if (EndAirportName.indexOf("虹桥") > -1) {
                            flight.setEndAirport("SHA");
                        }
                        else if (EndAirportName.indexOf("浦东") > -1) {
                            flight.setEndAirport("PVG");
                        }

                    }
                    else {
                        flight.setEndAirport(flightSearch.getEndAirportCode());
                    }
                    //设置抵达机场名称
                    flight.setEndAirportName(EndAirportName);
                    //航空公司名称
                    flight.setAirCompanyName(flightObj.getString("AirCompanyName"));
                    //机型
                    flight.setAirplaneType(flightObj.getString("AirplaneType"));
                    //燃油
                    flight.setFuelFee(Float.parseFloat(flightObj.getString("FuelFee")));
                    String DepartTime = date + " " + flightObj.getString("DepartTime");
                    String ArriveTime = date + " " + flightObj.getString("ArriveTime");
                    //出发时间
                    flight.setDepartTime(new Timestamp(dateFormat.parse(DepartTime).getTime()));
                    //抵达时间
                    flight.setArriveTime(new Timestamp(dateFormat.parse(ArriveTime).getTime()));
                    List<CarbinInfo> Carbins = new ArrayList<CarbinInfo>();
                    JSONArray cabins = flightObj.getJSONArray("cabins");
                    //用来记录没有折扣的仓位
                    String cbinStr = "";
                    for (int j = 0; j < cabins.size(); j++) {
                        JSONObject cbin = cabins.getJSONObject(j);
                        CarbinInfo carbinInfo = new CarbinInfo();
                        //票面价
                        carbinInfo.setPrice(Float.parseFloat(cbin.getString("Price")));
                        //燃油
                        carbinInfo.setFuelprice(Float.parseFloat(cbin.getString("fuelprice")));
                        //折扣
                        if (!"".equals(cbin.getString("Discount"))) {
                            carbinInfo.setDiscount(Float.parseFloat(cbin.getString("Discount")) * 10);
                        }
                        else {
                            if ("Y".equals(cbin.getString("Cabin"))) {
                                carbinInfo.setDiscount(100f);
                                flight.setYPrice(Float.parseFloat(cbin.getString("Price")));
                            }
                            else {
                                cbinStr += cbin.getString("Cabin") + ",";
                            }
                        }
                        //仓位名
                        carbinInfo.setCabintypename(cbin.getString("cabintypename"));
                        //仓位
                        carbinInfo.setCabin(cbin.getString("Cabin"));
                        //票量
                        carbinInfo.setSeatNum(cbin.getString("SeatNum"));
                        if (j == 0) {
                            flight.setLowCarbin(carbinInfo);
                        }
                        else {
                            Carbins.add(carbinInfo);
                        }
                    }

                    if (Carbins != null && Carbins.size() > 0 && !"".equals(cbinStr)) {
                        String[] num = cbinStr.split(",");
                        for (int m = 0; m < num.length; m++) {
                            for (int j = 0; j < Carbins.size(); j++) {
                                if (!"".equals(num[m]) && num[m].equals(Carbins.get(j).getCabin())) {
                                    CarbinInfo car = Carbins.get(j);
                                    Float zekou = car.getPrice() / flight.getYPrice();
                                    zekou = (float) (Math.round(zekou * 100));
                                    car.setDiscount(zekou);
                                    Carbins.set(j, car);
                                }
                            }
                        }
                    }
                    flight.setCarbins(Carbins);
                    flightalltemp.add(flight);
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            flightalltemp = null;
            e.printStackTrace();
        }

        SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = tempDate.format(new java.util.Date());
        System.out.println("解析航班数据并返回List时间：" + datetime);
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
            org.jdom.Document doc = build.build(new StringReader(strTemp.trim()));
            org.jdom.Element element = doc.getRootElement();
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
            org.jdom.Document doc = build.build(new StringReader(strTemp.trim()));
            org.jdom.Element element = doc.getRootElement();
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

    /**
     * 
     * 携程的三字码和本地三字码转换
     * @param scity
     * @return
     * @time 2014年11月6日 下午7:20:47
     * @author chendong
     */
    public static String getcitycode(String scity) {
        scity = scity.equals("PEK") ? "BJS" : scity;
        scity = scity.equals("XIY") ? "SIA" : scity;
        return scity;
    }

    /**
     * 
     * @param QUEUE_NAME
     * @param message
     * 
     * @time 2015年1月27日 上午11:48:04
     * @author chendong
     */
    public void sendMQmessage(String QUEUE_NAME, String message) {
        String url = getSysconfigString("activeMQ_url");
        //        String url = "tcp://192.168.0.5:61616";
        try {
            ActiveMQUtil.sendMessage(url, QUEUE_NAME, message);
        }
        catch (Exception e) {
            logger.error("MQ_err", e.fillInStackTrace());
        }
    }

    /**
     * 
     * 
     * @param orderinfoid
     * @param state
     * @param customeruserid
     * @param content
     * @param prestate 1只有管理员可以看到0所有人可以看到
     * @param Suouserid
     * @time 2016年6月16日 下午1:36:47
     * @author chendong
     */
    protected void createOrderinforc(Long orderinfoid, Integer state, Long customeruserid, String content,
            Integer prestate, Long Suouserid) {
        try {
            Orderinforc orderinforc = new Orderinforc();
            orderinforc.setOrderinfoid(orderinfoid);
            orderinforc.setCreatetime(new Timestamp(System.currentTimeMillis()));
            orderinforc.setContent(content);
            orderinforc.setSuouserid(Suouserid);
            orderinforc.setState(state);
            orderinforc.setPrestate(prestate);
            orderinforc.setCustomeruserid(customeruserid);
            Server.getInstance().getAirService().createOrderinforc(orderinforc);
        }
        catch (Exception ex) {
            logger.error("创建操作记录异常[createOrderinforc]:" + orderinfoid, ex.fillInStackTrace());
        }
    }

    public static void main(String[] args) throws NoSuchMethodException {
        //		FlightSearch flightSearch = new FlightSearch();
        //		flightSearch.setStartAirportCode("PEK");
        //		flightSearch.setEndAirportCode("CGO");
        //		flightSearch.setFromDate("2013-11-29");
        //		searchFlightByJINRI(flightSearch);

        System.out.println();

    }
}
