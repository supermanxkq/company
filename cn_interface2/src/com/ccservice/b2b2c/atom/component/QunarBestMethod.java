package com.ccservice.b2b2c.atom.component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;

/**
 * 
 * @author wzc qunar优选数据
 *
 */
public class QunarBestMethod {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    static QunarBestBook qunar = new QunarBestBook();

    public static void main(String[] args) {
        getFlightTgq("CA1547", "PEK", "KHN", "20141204", "Q", "780", "ttsgndh0664", "143548318", "fxu.trade.qunar.com",
                "687", "687");
    }

    /**
     * 根据航班查询退改签信息
     * vppr     搜索的时候price里面的vppr 20141111
     * wid  String  是   搜索的时候price里面的wid
     * pid  String  是   搜索的时候price里面的pid
     * maxSellPrice String  是   搜索的时候price里面的bpr     （整型）
     * minSellPrice String  是   搜索的时候price里面的ipr     （整型）
     */
    public static String getFlightTgq(String flightnum, String startport, String endport, String deptDate,
            String cabin, String vppr, String wid, String pid, String client, String maxSellPrice, String minSellPrice) {
        String result = "";
        String url = "";
        try {
            String param = "flightNum=" + URLEncoder.encode(flightnum, "UTF-8") + "&deptAirport="
                    + URLEncoder.encode(startport, "UTF-8") + "&arrAirport=" + URLEncoder.encode(endport, "UTF-8")
                    + "&deptDate=" + URLEncoder.encode(deptDate, "UTF-8") + "&vppr=" + URLEncoder.encode(vppr, "UTF-8")
                    + "&wid=" + URLEncoder.encode(wid, "UTF-8") + "&cabin=" + URLEncoder.encode(cabin, "UTF-8")
                    + "&pid=" + URLEncoder.encode(pid, "UTF-8") + "&client=" + URLEncoder.encode(client, "UTF-8")
                    + "&maxSellPrice=" + URLEncoder.encode(maxSellPrice, "UTF-8") + "&minSellPrice="
                    + URLEncoder.encode(minSellPrice, "UTF-8") + "&vendorID=" + qunar.getVendorID();
            url = qunar.getQunarurl() + "/flight_queryTGQ?" + param;
            WriteLog.write("qunarbest", url);
            String resultt = SendPostandGet.submitGet(url, "UTF-8");
            WriteLog.write("qunarbest", resultt);
            result = resultt;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 实时查询qunar的退改签信息
     * @param orderid qunar对应的订单id
     * @param orderNo
     * @param site
     * @return
     * @time 2014年9月3日 下午3:23:57
     * @author wzc
     */
    public static String getTgqInfo(String orderid, String orderNo, String site) {
        String result = "";
        String url = "";
        try {
            String param = "orderId=" + URLEncoder.encode(orderid, "UTF-8") + "&orderNo="
                    + URLEncoder.encode(orderNo, "UTF-8") + "&quserName="
                    + URLEncoder.encode(qunar.getRegistname(), "UTF-8") + "&site=" + URLEncoder.encode(site, "UTF-8")
                    + "&vendorID=" + qunar.getVendorID();
            url = qunar.getQunarurl() + "/query_tgq?" + param;
            WriteLog.write("qunarbest", url);
            String resultt = SendPostandGet.submitGet(url, "UTF-8");
            WriteLog.write("qunarbest", resultt);
            result = resultt;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
       * 根据出发到达城市名称查询出去哪的价格
       */
    public static List<FlightInfo> getQunarFlightinfo(FlightSearch flightSearch) {
        String searchArrivalAirport = flightSearch.getEndAirportCode();
        String searchDepartureAirport = flightSearch.getStartAirportCode();
        String searchDepartureTime = flightSearch.getFromDate();
        List<FlightInfo> flightinfos = null;
        String url = "";
        try {
            String param = "begin=" + URLEncoder.encode(searchDepartureAirport, "utf-8") + "&end="
                    + URLEncoder.encode(searchArrivalAirport, "utf-8") + "&date="
                    + URLEncoder.encode(searchDepartureTime, "utf-8") + "&vendorID=" + qunar.getVendorID() + "&type=1";
            url = qunar.getQunarurl() + "/flight_search?" + param;
            WriteLog.write("qunarbest", url);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = SendPostandGet.submitGet(url, "UTF-8");
        WriteLog.write("qunarbest", result);
        flightinfos = getLastData(result);
        return flightinfos;

    }

    /**
     * 解析去哪返回数据
     * 
     * @return
     */
    public static List<FlightInfo> parseQunarData(String result) {
        List<FlightInfo> infos = new ArrayList<FlightInfo>();
        if (!"".equals(result)) {
            JSONObject jsonobj = JSONObject.fromObject(result);
            String status = jsonobj.getString("status");// 成功码 1（成功） 、0（失败）
            if (!"1".equals(status)) {
                return infos;
            }
            String track_id = jsonobj.getString("track_id");// 交易码
            JSONArray flightary = jsonobj.getJSONArray("retData");
            if (flightary.size() > 0) {
                for (int i = 0; i < flightary.size(); i++) {
                    FlightInfo flighttemp = new FlightInfo();
                    JSONObject flightobj = flightary.getJSONObject(i);
                    JSONObject flightstr = flightobj.getJSONObject("flight");
                    String dc = flightstr.getString("dc");// 出发城市
                    flighttemp.setStartAirportCity(dc);
                    String dcc = flightstr.getString("dcc");// 出发城市三字码
                    String ac = flightstr.getString("ac");// 到达城市
                    String acc = flightstr.getString("acc");// 到达城市三字码

                    String dd = flightstr.getString("dd");// 出发日期
                    String d = flightstr.getString("dt");// 出发时间
                    String ad = flightstr.getString("ad");// 到达日期
                    String at = flightstr.getString("at");// 到达时间
                    flighttemp.setDepartTime(format(dd + " " + d));// 出发日期
                    flighttemp.setArriveTime(format(ad + " " + at));// 到达日期

                    String da = flightstr.getString("da");// 出发机场
                    flighttemp.setStartAirportName(da);
                    String das = flightstr.getString("das");// 出发机场简称
                    String de = flightstr.getString("de");// 出发机场三字码
                    flighttemp.setStartAirport(de);

                    String aa = flightstr.getString("aa");// 到达机场名称
                    flighttemp.setEndAirportName(aa);
                    String aas = flightstr.getString("aas");// 到达机场简称
                    String ae = flightstr.getString("ae");// 到达机场三字码
                    flighttemp.setEndAirport(ae);

                    try {
                        String dT = flightstr.getString("dT");// 出发机场航站楼
                        String dTd = flightstr.getString("dTd");// 出发机场航站楼简称
                        String aT = flightstr.getString("aT");// 到达机场航站楼
                        String aTd = flightstr.getString("aTd");// 到达机场航站楼简称
                        flighttemp.setOffPointAT(dT);
                        flighttemp.setBorderPointAT(aT);
                    }
                    catch (Exception e1) {

                    }

                    String co = flightstr.getString("co");// 航班号
                    flighttemp.setAirline(co);

                    JSONObject flightprice = flightobj.getJSONObject("price");
                    String bpr = flightprice.getString("TN");// 裸票价
                    //String btag = flightprice.getString("btag");// 裸票销售价格条件
                    String fpr = flightprice.getString("fpr");// 全票价
                    String tof = flightprice.getString("tof");// 燃油税 ty
                    flighttemp.setFuelFee(Float.valueOf(tof));
                    String arf = flightprice.getString("arf");// 机场建设费
                    flighttemp.setAirportFee(Float.valueOf(arf));
                    String pid = flightprice.getString("tn_pid");// 对应查退改签信息的 pid
                    String vppr = flightprice.getString("tn_vppr");// 对应查退改签信息的
                    String wid = flightprice.getString("tn_wid");// 对应查退改签信息的 wid
                    flighttemp.setWid(wid);
                    flighttemp.setVppr(vppr);
                    flighttemp.setPid(pid);
                    String cb = flightstr.getString("tn_cb");// 舱位
                    CarbinInfo cabin = new CarbinInfo();
                    cabin.setCabintypename(cb);
                    cabin.setCabin(cb);
                    try {
                        DecimalFormat df = new DecimalFormat("#.0");
                        Double valstr = Double.parseDouble(bpr) / Double.parseDouble(fpr);
                        Double val = valstr * 10;
                        Float valt = Float.valueOf(df.format(val));
                        cabin.setDiscount(valt);
                    }
                    catch (Exception e) {
                        cabin.setDiscount(0F);
                    }
                    cabin.setSpecial(false);
                    cabin.setPrice(Float.parseFloat(bpr));

                    try {
                        String cs = flightstr.getString("cs");// 如果有指表示共享航班号
                        String csn = flightstr.getString("csn");// 共享航班承运人
                        if (cs != null && !"".equals(cs)) {
                            flighttemp.setIsShare(1);
                            flighttemp.setShareFlightNumber(cs);
                        }
                        else {
                            flighttemp.setIsShare(0);
                        }
                    }
                    catch (Exception e) {
                        flighttemp.setIsShare(0);
                    }

                    String cd = flightstr.getString("cd");// 是否跨天 0为当天，否则表示夸几天
                    String vy = flightstr.getString("vy");// 是否经停 0-直飞；1-经停
                    if (Long.valueOf(vy).longValue() == 1) {
                        flighttemp.setStop(true);
                    }
                    else {
                        flighttemp.setStop(false);
                    }
                    String st = flightstr.getString("st");// 航段 0-全航段；1-子航段
                    String loc = flightstr.getString("loc"); // 国内国际航班 0-国内；1-国际
                    String sd = flightstr.getString("sd");// 飞行距离（公里）
                    flighttemp.setDistance(sd);
                    String fdt = flightstr.getString("fdt");// 飞行时长 3小时30分钟
                    String pt = flightstr.getString("pt");// 机型
                    flighttemp.setAirplaneType(pt);
                    String pt_cn = flightstr.getString("pt_cn");// 机型中文
                    //String cb = flightstr.getString("cb");// 舱位

                    //String cb_cn = flightstr.getString("cb_cn");// 舱位中文名

                    String an = flightstr.getString("an");// 航空公司名称
                    String asn = flightstr.getString("asn");// 航空公司简称
                    String fc = flightstr.getString("fc");// 航空公司二字码
                    flighttemp.setAirCompany(fc);
                    flighttemp.setAirCompanyName(fc);

                    //String on = flightstr.getString("on");// ota 名称
                    String on = flightstr.getString("tn_on");// ota 名称
                    //String ota = flightstr.getString("ota");// ota 域名
                    String ota = flightstr.getString("tn_ota");// ota 域名
                    flighttemp.setSuplyname(on);// 供应商名称
                    flighttemp.setSuplyurl(ota);// 供应商域名
                    String ml = flightstr.getString("ml");// 是否有餐食 true
                    if ("true".equals(ml)) {
                        flighttemp.setMeal(true);
                    }
                    else {
                        flighttemp.setMeal(false);
                    }
                    String zhiji = flightstr.getString("zhiji");// 是否支持网上值机 true
                    String wifi = flightstr.getString("wifi");// 是否有 WiFi true
                    List<CarbinInfo> listCabinAll = new ArrayList<CarbinInfo>();

                    //cabin.setCabinRemark(btag);// 此属性存放裸票销售价格条件
                    JSONObject flighttgqInfo = flightobj.getJSONObject("tgqInfo");
                    String changeText = flighttgqInfo.getString("tn_changeText");// 改期说明文本
                    String returnText = flighttgqInfo.getString("tn_returnText");// 退票说明文本
                    String signText = flighttgqInfo.getString("tn_signText");// 签转说明可以签转

                    cabin.setRefundrule(returnText);// 退票规定
                    cabin.setChangerule(signText);// 签转规定
                    cabin.setUpdaterule(changeText);// 更改规定
                    if ("Y".equals(cb)) {
                        //flighttemp.setYPrice(cabin.getPrice());
                    }
                    if (cabin.getPrice() > 0) {
                        flighttemp.setLowCarbin(cabin);
                        flighttemp.setCarbins(listCabinAll);
                        String flightnumzs = "0";
                        String flightnumls = "1";
                        boolean flag = true;
                        for (int j = 0; j < infos.size(); j++) {
                            FlightInfo inf = infos.get(j);
                            if (flighttemp.getIsShare() != null && flighttemp.getIsShare() == 1) {//临时
                                flightnumls = flighttemp.getShareFlightNumber();
                            }
                            else {
                                flightnumls = flighttemp.getAirline();
                            }
                            if (inf.getIsShare() != null && inf.getIsShare() == 1) {//正式
                                flightnumzs = inf.getShareFlightNumber();
                            }
                            else {
                                flightnumzs = inf.getAirline();
                            }
                            if (flightnumzs.equals(flightnumls)) {
                                flag = false;
                                if (flighttemp.getLowCarbin().getPrice() < inf.getLowCarbin().getPrice()) {
                                    infos.add(flighttemp);
                                    infos.remove(inf);
                                }
                                break;
                            }
                        }
                        if (flag) {
                            infos.add(flighttemp);
                        }
                    }
                    else {
                        System.out.println("仓位没有价格……");
                    }
                }
            }
        }
        return infos;
    }

    /**
     * 解析返回最终集合数据
     * 
     * @return
     */
    public static List<FlightInfo> getLastData(String result) {
        List<FlightInfo> flightinfos = parseQunarData(result);
        Collections.sort(flightinfos, new Comparator<FlightInfo>() {
            @Override
            public int compare(FlightInfo o1, FlightInfo o2) {
                if (o1.getDepartTime().before(o2.getDepartTime())) {
                    return -1;
                }
                else {
                    return 1;
                }

            }
        });
        return flightinfos;
    }

    /**
     * 查询去哪订单状态
     * 
     */
    public static String getQunarorderState(String ordernum) {
        String url = "";
        try {
            String param = "orderNo=" + URLEncoder.encode(ordernum, "utf-8") + "&contactMob="
                    + URLEncoder.encode(qunar.getLinkmobile(), "utf-8") + "&vendorID=" + qunar.getVendorID();
            url = qunar.getQunarurl() + "/orderDetail?" + param;
            WriteLog.write("qunarbest", "获取订单状态：" + url);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = SendPostandGet.submitGet(url, "UTF-8");
        WriteLog.write("qunarbest", result);
        JSONObject obj = JSONObject.fromObject(result);
        String state = obj.getString("ostNo");
        return state;
    }

    /**
     * booking接口
     */
    public static String Qunarbooking(Segmentinfo seg, String supplyurl) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HHmm");
        String deptDate = sdf.format(seg.getDeparttime());// 出发日期
        String deptTime = sdf1.format(seg.getDeparttime());// 起飞时间
        String deptCity = seg.getStartairport();// 出发机场 3 字码
        String arriCity = seg.getEndairport();// 到达机场 3 字码
        String airline = seg.getFlightnumber();// 航班号
        String price = seg.getPrice() + "";// 价格
        String flightCode = seg.getAircomapnycode();// 航空公司 2 字码
        String clientSite = supplyurl;// 代理商域名
        //String tag = flightInfo.getLowCarbin().getCabinRemark();// 价格类型
        String cabin = seg.getCabincode();// 舱位
        String url = "";
        try {
            String param = "deptDate=" + URLEncoder.encode(deptDate, "utf-8") + "&cabin="
                    + URLEncoder.encode(cabin, "utf-8") + "&deptCity=" + URLEncoder.encode(deptCity, "utf-8")
                    + "&arriCity=" + arriCity + "&airline=" + URLEncoder.encode(airline, "utf-8") + "&price=" + price
                    + "&flightCode=" + URLEncoder.encode(flightCode, "utf-8") + "&deptTime=" + deptTime
                    + "&clientSite=" + URLEncoder.encode(clientSite, "utf-8") + "&tag=S&vendorID="
                    + qunar.getVendorID();
            url = qunar.getQunarurl() + "/booking?" + param;
            WriteLog.write("qunarbest", url);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = SendPostandGet2.doGet(url, "UTF-8");
        WriteLog.write("qunarbest", result);
        Map map = new HashMap();
        if (result != null && !"".equals(result)) {
            JSONObject obj = JSONObject.fromObject(result);
            String state = obj.getString("status");//接口状态
            if ("1".equals(state)) {//校验成功 返回数据
                String bookingTag = obj.getString("bookingTag");
                String cabinNum = obj.getString("cabinNum");
                String childCabin = obj.getString("childCabin");
                if (seatnumflag(cabinNum) > 0) {//有仓位
                    String childCabinNum = "";
                    if (obj.containsKey("childCabinNum")) {
                        childCabinNum = obj.getString("childCabinNum");//儿童座位数  数字 1-9 表示有 9 个以下座位(包括 9) A 表示有 9 个以上座位 Q 表示已关闭
                    }
                    String childFuelTax = "";
                    if (obj.containsKey("childFuelTax")) {
                        childFuelTax = obj.getString("childFuelTax");
                    }
                    String childPrintPrice = "";
                    if (obj.containsKey("childPrintPrice")) {
                        childPrintPrice = obj.getString("childPrintPrice");
                    }
                    String childSellPrice = "";
                    if (obj.containsKey("childSellPrice")) {
                        childSellPrice = obj.getString("childSellPrice");//儿童销售价
                    }
                    String constructionFee = "";
                    if (obj.containsKey("constructionFee")) {
                        constructionFee = obj.getString("constructionFee");
                    }
                    String ctgq = "";
                    if (obj.containsKey("ctgq")) {
                        ctgq = obj.getString("ctgq");//儿童退改签
                    }
                    String isSlae = "";
                    if (obj.containsKey("isSlae")) {
                        isSlae = obj.getString("isSlae");//是否特价
                    }
                    String tgq = "";
                    if (obj.containsKey("tgq")) {
                        tgq = obj.getString("tgq");//退改签
                    }
                    String isSupportChild = "";
                    if (obj.containsKey("isSupportChild")) {
                        isSupportChild = obj.getString("isSupportChild");//是否支 持儿童票
                    }
                    JSONObject priceTag = obj.getJSONObject("priceTag");
                    String cprice = "";
                    if (priceTag.containsKey("C")) {
                        cprice = priceTag.getString("C");//是否支 持儿童票   
                    }
                    String sprice = "";
                    if (priceTag.containsKey("S")) {
                        sprice = priceTag.getString("S");//是否支 持儿童票 
                    }
                    map.put("bookingTag", bookingTag);
                    map.put("cabinNum", cabinNum);
                    map.put("tgq", tgq);
                    map.put("isSlae", isSlae);
                    map.put("isSupportChild", isSupportChild);
                    map.put("ctgq", ctgq);
                    map.put("childCabin", childCabin);
                    map.put("childCabinNum", childCabinNum);
                    map.put("childFuelTax", childFuelTax);
                    map.put("childPrintPrice", childPrintPrice);
                    map.put("childSellPrice", childSellPrice);
                    map.put("constructionFee", constructionFee);
                    map.put("cprice", cprice);
                    map.put("sprice", sprice);
                }
            }
        }
        result = JSONObject.fromObject(map).toString();
        return result;
    }

    public static int seatnumflag(String seatflag) {
        int num = 0;
        if ("A".equalsIgnoreCase(seatflag)) {
            num = 9;
        }
        else if ("1".equalsIgnoreCase(seatflag) || "2".equalsIgnoreCase(seatflag) || "3".equalsIgnoreCase(seatflag)
                || "4".equalsIgnoreCase(seatflag) || "5".equalsIgnoreCase(seatflag) || "6".equalsIgnoreCase(seatflag)
                || "7".equalsIgnoreCase(seatflag) || "8".equalsIgnoreCase(seatflag) || "9".equalsIgnoreCase(seatflag)) {
            return Integer.parseInt(seatflag);
        }
        return num;
    }

    /**
     * 解析日期
     * 
     * @param timeinfo
     * @return
     */
    public static Timestamp format(String timeinfo) {
        try {
            Date date = sdf.parse(timeinfo);
            return new Timestamp(date.getTime());
        }
        catch (ParseException e) {

        }
        return null;
    }

}
