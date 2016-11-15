package com.qunarprice;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.IOException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 抓取去哪儿酒店价格
 */
@SuppressWarnings({ "serial", "unchecked" })
public class ReptileGetQunarHotelPrice extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        PrintWriter out = null;
        req.setCharacterEncoding("UTF-8");
        res.setHeader("Content-type", "text/html;charset=UTF-8");
        String ret = "";
        try {
            out = res.getWriter();
            //请求参数
            String QunarHotelId = req.getParameter("QunarHotelId");
            String startDate = req.getParameter("startDate");
            String endDate = req.getParameter("endDate");
            String payType = req.getParameter("payType"); //1：现付；2：预付
            payType = StringIsNull(payType) ? "" : payType.trim();
            String reqType = req.getParameter("reqType");//请求类型，ComparePirce：比价上去哪儿
            String dataType = req.getParameter("dataType");//数据类型，0：取实时数据，其他：缓存
            if (!"0".equals(dataType) || "1".equals(PropertyUtil.getValue("QunarPriceCache"))) {
                ret = QunarCacheData.getPriceByQunarHotelId(QunarHotelId, payType);
            }
            else {
                String QunarCityCode = QunarHotelId.substring(0, QunarHotelId.lastIndexOf("_"));
                if (StringIsNull(QunarCityCode) || StringIsNull(startDate) || StringIsNull(endDate)
                        || StringIsNull(QunarHotelId)) {
                    throw new Exception("参数错误.");
                }
                //获取MixKey
                String MixKey = QunarMixKeyUtil.getKey();
                if (StringIsNull(MixKey)) {
                    MixKey = GetQunarMixKey(QunarCityCode);
                    if (!StringIsNull(MixKey)) {
                        QunarMixKeyUtil.setKey(MixKey);
                    }
                }
                //请求去哪儿价格
                String info = "";
                if (!StringIsNull(MixKey)) {
                    info = GetQunarPrice(MixKey, QunarCityCode, QunarHotelId, startDate, endDate);
                    if (!StringIsNull(info)) {
                        //MixKey失效
                        if ("{\"ret\":false,\"errcode\":110,\"errmsg\":\"invalid cookie\"}".equals(info.trim())) {
                            MixKey = GetQunarMixKey(QunarCityCode);
                            if (!StringIsNull(MixKey)) {
                                QunarMixKeyUtil.setKey(MixKey);
                                //重新请求去哪儿价格
                                info = GetQunarPrice(MixKey, QunarCityCode, QunarHotelId, startDate, endDate);
                            }
                        }
                        if ("{\"ret\":false,\"errcode\":110,\"errmsg\":\"suspect user\"}".equals(info.trim())) {
                            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            System.out.println(time + "===数据被限制：" + QunarHotelId + " / " + startDate + " / " + endDate);
                        }
                    }
                    if (StringIsNull(info)) {
                        throw new Exception("NoData.");
                    }
                    //解析去哪儿JSON
                    info = info.trim();
                    String json = info.substring(info.indexOf("(") + 1, info.lastIndexOf(")"));
                    JSONObject datas = JSONObject.fromObject(json);
                    JSONObject result = datas.getJSONObject("result");
                    Iterator<String> keys = result.keys();
                    //预付单独供应商加价-暂时
                    Map<String, Integer> adds = new HashMap<String, Integer>();//<去哪儿供应ID,加价>
                    if (!"ComparePirce".equals(reqType)) {
                        String text = getFileValue(1);
                        if (!StringIsNull(text) && text.contains(":")) {
                            String[] agents = text.split("@");
                            for (String agent : agents) {
                                String[] strs = agent.split(":");
                                adds.put(strs[1], Integer.parseInt(strs[2]));
                            }
                        }
                    }
                    //禁用代理
                    String DisableAgents = getFileValue(3);
                    List<String> DisableAgentList = new ArrayList<String>();
                    if (!StringIsNull(DisableAgents)) {
                        String[] DisableAgentArray = DisableAgents.split("@");
                        for (String agent : DisableAgentArray) {
                            if (StringIsNull(agent)) {
                                continue;
                            }
                            DisableAgentList.add(agent.trim());
                        }
                    }
                    //返回JSON
                    JSONArray retarray = new JSONArray();
                    //现付
                    JSONArray xfarray = new JSONArray();
                    Map<String, Integer> xfpriceflag = new HashMap<String, Integer>();//<去哪儿房型名称,价格>
                    Map<String, Integer> xfstatusflag = new HashMap<String, Integer>();//<去哪儿房型名称,房态>
                    Map<String, Integer> xfjsonflag = new HashMap<String, Integer>();//<去哪儿房型名称,JSONObject在JSONArray第几个>
                    //预付
                    JSONArray yfarray = new JSONArray();
                    Map<String, Integer> yfpriceflag = new HashMap<String, Integer>();//<去哪儿房型名称,价格>
                    Map<String, Integer> yfstatusflag = new HashMap<String, Integer>();//<去哪儿房型名称,房态>
                    Map<String, Integer> yfjsonflag = new HashMap<String, Integer>();//<去哪儿房型名称,JSONObject在JSONArray第几个>
                    while (keys.hasNext()) {
                        boolean replaceflag = false;//替换原有JSONObject
                        JSONObject retobj = new JSONObject();
                        //KEY
                        String key = keys.next();
                        //代理ID
                        String agentid = key.substring(0, key.trim().indexOf("|"));
                        if (StringIsNull(agentid)) {
                            continue;
                        }
                        retobj.put("agentid", agentid);
                        //代理价格信息
                        JSONArray array = JSONArray.fromObject(result.get(key));
                        if (array.get(3) == null || array.get(14) == null || array.get(16) == null) {//去哪正式房型名称、支付方式、rPrice
                            continue;
                        }
                        //去哪加盟商名称
                        String agentname = "";
                        if (array.get(5) != null && !StringIsNull(array.get(5).toString())) {
                            agentname = array.getString(5).trim();
                            if (DisableAgentList.contains(agentname)
                                    || (agentname.endsWith("团购") && DisableAgentList.contains("团购"))) {
                                continue;
                            }
                        }
                        retobj.put("agentname", agentname);
                        //货币类型：CNY
                        if (array.get(1) == null || !"CNY".equals(array.getString(1))) {
                            continue;
                        }
                        String roomname = array.getString(3);//房型名称
                        if (StringIsNull(roomname) || "其他".equals(roomname.trim()) || "其它".equals(roomname.trim())) {
                            continue;
                        }
                        retobj.put("roomname", roomname);
                        String paytype = array.getString(14);//0:现付 1:预付
                        if (!"0".equals(paytype) && !"1".equals(paytype)) {
                            continue;
                        }
                        //请求现付、去哪儿为预付
                        if ("1".equals(payType) && "1".equals(paytype)) {
                            continue;
                        }
                        //请求预付、去哪儿为现付
                        if ("2".equals(payType) && "0".equals(paytype)) {
                            continue;
                        }
                        retobj.put("paytype", Integer.parseInt(paytype));
                        double rPrice = 0d;//真实价格
                        double rPriceratio = 0d;//价格比例
                        try {
                            rPrice = Double.valueOf(array.get(16).toString());
                        }
                        catch (Exception e) {
                        }
                        try {
                            rPriceratio = Double.valueOf(array.get(17).toString());
                        }
                        catch (Exception e) {
                        }
                        if (rPrice <= 0) {
                            continue;
                        }
                        //计算价格，调用者不再计算
                        Double price = rPrice + Math.ceil(rPrice * rPriceratio / 100);//去哪儿价格
                        int sealprice = price.intValue();
                        if (sealprice <= 0) {
                            continue;
                        }
                        //预付加价
                        if ("1".equals(paytype) && adds.containsKey(agentid) && adds.get(agentid).intValue() > 0) {
                            sealprice = sealprice + adds.get(agentid);//加价
                        }
                        retobj.put("rPrice", sealprice);//计算后价格
                        retobj.put("rPriceratio", 0);//比例为0
                        //房态 1 开房；-1 关房；-2 休息中
                        Integer roomstatus = -1;
                        if (array.get(9) != null && !StringIsNull(array.get(9).toString())) {
                            try {
                                roomstatus = array.getInt(9);
                            }
                            catch (Exception e) {
                            }
                        }
                        retobj.put("roomstatus", roomstatus);
                        //比较价格，取最低的
                        if ("1".equals(paytype)) {//预付
                            if (yfpriceflag.containsKey(roomname)) {
                                int oldprice = yfpriceflag.get(roomname);
                                int oldstatus = yfstatusflag.get(roomname);
                                //已有房态开房
                                if (oldstatus == 1) {
                                    if (roomstatus == 1) {//现有开房
                                        //已有价格小一些
                                        if (oldprice <= sealprice)
                                            continue;
                                        replaceflag = true;
                                    }
                                    else {
                                        continue;//满房不覆盖开房
                                    }
                                }
                                else {
                                    if (roomstatus == 1) {//现有开房
                                        replaceflag = true;//无条件覆盖
                                    }
                                    else {
                                        //已有价格小一些
                                        if (oldprice <= sealprice)
                                            continue;
                                        replaceflag = true;
                                    }
                                }
                            }
                            yfpriceflag.put(roomname, sealprice);
                            yfstatusflag.put(roomname, roomstatus);
                        }
                        else {
                            if (xfpriceflag.containsKey(roomname)) {
                                int oldprice = xfpriceflag.get(roomname);
                                int oldstatus = xfstatusflag.get(roomname);
                                //已有房态开房
                                if (oldstatus == 1) {
                                    if (roomstatus == 1) {//现有开房
                                        //已有价格小一些
                                        if (oldprice <= sealprice)
                                            continue;
                                        replaceflag = true;
                                    }
                                    else {
                                        continue;//满房不覆盖开房
                                    }
                                }
                                else {
                                    if (roomstatus == 1) {//现有开房
                                        replaceflag = true;//无条件覆盖
                                    }
                                    else {
                                        //已有价格小一些
                                        if (oldprice <= sealprice)
                                            continue;
                                        replaceflag = true;
                                    }
                                }
                            }
                            xfpriceflag.put(roomname, sealprice);
                            xfstatusflag.put(roomname, roomstatus);
                        }
                        //宽带、早餐等
                        String roombf = "";
                        if (array.get(2) != null && !StringIsNull(array.get(2).toString())) {
                            roombf = getRoomBF(array.getString(2));
                        }
                        retobj.put("roombf", roombf);
                        //预订链接
                        String url = "";
                        if (array.get(4) != null && !StringIsNull(array.get(4).toString())) {
                            //url = array.getString(4);
                        }
                        retobj.put("url", url);
                        //返佣
                        Double fanyong = 0d;
                        if (array.get(10) != null && !StringIsNull(array.get(10).toString())) {
                            try {
                                fanyong = array.getDouble(10);
                            }
                            catch (Exception e) {
                            }
                        }
                        if (fanyong == null || fanyong.doubleValue() < 0)
                            fanyong = 0d;
                        retobj.put("fanyong", fanyong);
                        //HashCode
                        String hashcode = "";
                        if (array.get(12) != null && !StringIsNull(array.get(12).toString())) {
                            hashcode = array.getString(12);
                        }
                        retobj.put("hashcode", hashcode);
                        //担保 0不担保；1 担保
                        Integer danbaoflag = 0;
                        if (array.get(18) != null && !StringIsNull(array.get(18).toString())) {
                            try {
                                danbaoflag = array.getInt(18);
                            }
                            catch (Exception e) {
                            }
                        }
                        retobj.put("danbaoflag", danbaoflag);
                        //去哪儿代理房型
                        String agentRoom = "";
                        if (array.size() >= 24 && array.get(23) != null && !StringIsNull(array.getString(23))) {
                            agentRoom = array.getString(23).trim();
                        }
                        retobj.put("agentRoom", agentRoom);
                        //早餐
                        String bfstr = "无早";
                        if (array.size() >= 26 && array.get(25) != null && !StringIsNull(array.getString(25))) {
                            String tempbf = array.getString(25).trim();
                            if ("单早".equals(tempbf) || "双早".equals(tempbf) || "三早".equals(tempbf)
                                    || "四早".equals(tempbf)) {
                                bfstr = tempbf;
                            }
                            else if ("单份早餐".equals(tempbf) || "一份早餐".equals(tempbf)) {
                                bfstr = "单早";
                            }
                            else if ("两份早餐".equals(tempbf) || "双份早餐".equals(tempbf)) {
                                bfstr = "双早";
                            }
                            else if ("含早餐".equals(tempbf) || "含早".equals(tempbf) || "美式自助早餐".equals(tempbf)
                                    || "欧式早餐".equals(tempbf) || "自助早餐".equals(tempbf)) {
                                bfstr = "含早";
                            }
                        }
                        retobj.put("bfstr", bfstr);
                        //宽带
                        String webstr = "无";
                        if (array.size() >= 28 && array.get(27) != null && !StringIsNull(array.getString(27))) {
                            String tempweb = array.getString(27);
                            if ("免费".equals(tempweb) || "收费".equals(tempweb)) {
                                webstr = tempweb;
                            }
                        }
                        retobj.put("webstr", webstr);
                        //替换原有JSONObject
                        if ("1".equals(paytype)) {//预付
                            if (replaceflag) {
                                yfarray.set(yfjsonflag.get(roomname), retobj);
                            }
                            else {
                                int idx = yfarray.size();
                                yfarray.add(idx, retobj);
                                yfjsonflag.put(roomname, idx);
                            }
                        }
                        else {
                            if (replaceflag) {
                                xfarray.set(xfjsonflag.get(roomname), retobj);
                            }
                            else {
                                int idx = xfarray.size();
                                xfarray.add(idx, retobj);
                                xfjsonflag.put(roomname, idx);
                            }
                        }
                    }
                    if (yfarray.size() > 0)
                        retarray.addAll(yfarray);//优先显示预付
                    if (xfarray.size() > 0)
                        retarray.addAll(xfarray);
                    if (retarray.size() > 0)
                        ret = retarray.toString();
                }
            }
            out.print(ret);
        }
        catch (Exception e) {
            out.print("");
        }
        finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    private static String GetQunarMixKey(String QunarCityCode) throws Exception {
        String mixKey = "";
        String fromDate = getCurrentDate();
        String toDate = getAddDate(fromDate, 1);
        String strUrl = "http://hotel.qunar.com/city/" + QunarCityCode + "/#fromDate=" + fromDate + "&toDate=" + toDate
                + "&from=hotellist&QHFP=ZSL_A491C891&bs=&bc=";
        String html = QunarReqUtil.submit(strUrl, "", "", "", "", "get");
        int start = html.indexOf("<span id=\"eyKxim\" style=\"display:none\">");
        html = html.substring(start);
        html = html.substring(html.indexOf(">") + 1);
        mixKey = html.substring(0, html.indexOf("<"));
        return mixKey;
    }

    /**
     * @param AgentIp localhost：当前主机，代表不使用代理
     */
    public static String GetCookie(String Referer, String AgentIp) {
        //121.197.13.153:808:易订行-153
        String ip = "localhost".equals(AgentIp) ? AgentIp : AgentIp.split(":")[0];
        //取文本里的Cookie --> 5d8e1f5fab1414468375858211.103.207.130
        String vcd = QunarCookieUtil.getCookie(ip);
        //时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //判断vcd是否注释，用于去哪儿页面爬取，注释表示爬取完毕，不再进行Cookie获取
        if (vcd.startsWith("//")) {
            System.out.println(sdf.format(new Date()) + " ---> " + ip + "，Cookie已注释，中断操作，不再继续获取去哪儿Cookie");
            return "";
        }
        //是否要重新请求Cookie
        boolean reload = StringIsNull(vcd) ? true : false;
        //是否验证码破解，1：是；其他：否
        String flag = PropertyUtil.getValue("QunarVcodeFlag");
        //当前时间
        long current = System.currentTimeMillis();
        //非空，判断Cookie是否要重新加载
        if (!reload && "1".equals(flag)) {
            //获取Cookie生成时间
            long time = Long.parseLong(vcd.substring(10, 23));
            //Cookie有效时间
            long valid = Long.parseLong(PropertyUtil.getValue("QunarCookieValidTime").trim()) * 60 * 1000;
            if ((time + valid) < current) {
                reload = true;
            }
        }
        //加载Cookie
        if (reload && "1".equals(flag)) {
            //请求去哪儿验证码图片
            String url = PropertyUtil.getValue("QunarVcodeUrl") + "&_=" + Math.random();
            //QN25="d88b5307-d426-4f42-b7f4-e0fcfc3722babbd90a40d9478537265bc4f718dbb2e2{en7mni(z"; Version=1; Domain=.qunar.com; Path=/
            String SetCookie = QunarReqUtil.submit(url, AgentIp, "", "", "vcodeImage", "get");
            //Print
            System.out.println(sdf.format(new Date()) + " ---> HotelQN25Cookie：" + SetCookie);
            if (StringIsNull(SetCookie)) {
                return "";
            }
            String QN25 = SetCookie.split(";")[0];
            //请求接口，破解验证码
            String imgurl = PropertyUtil.getValue("VcodeImagePath");
            String CrackVcodeValidUrl = PropertyUtil.getValue("CrackVcodeValidUrl");
            String CrackVcode = QunarReqUtil.submit(CrackVcodeValidUrl + "?BusinessType=HotelVcodeValid&ImgUrl="
                    + imgurl, "", "", "", "", "post");
            //校验成功
            if (!StringIsNull(CrackVcode) && !"校验失败".equals(CrackVcode.trim())) {
                String[] code = CrackVcode.split(",");
                //请求去哪儿，验证是否破解成功
                String QunarVcodeValidUrl = PropertyUtil.getValue("QunarVcodeValidUrl") + code[1];
                //VCD=a872f555f71414478332919211.103.207.130; Domain=qunar.com; Expires=Tue, 28-Oct-2014 07:08:52 GMT; Path=/
                SetCookie = QunarReqUtil.submit(QunarVcodeValidUrl, AgentIp, QN25, Referer, "checkVcode", "get");
                System.out.println(sdf.format(new Date()) + " ---> HotelVCDCookie：" + SetCookie);
                //失败，请求接口，退破解验证码费用
                if (StringIsNull(SetCookie)) {
                    QunarReqUtil.submit(CrackVcodeValidUrl + "?BusinessType=HotelVcodeError&codes=" + CrackVcode, "",
                            "", "", "", "post");
                }
                else {
                    vcd = SetCookie.split(";")[0].split("=")[1];
                    QunarCookieUtil.setCookie(vcd, ip);
                }
            }
        }
        String cookie = "QunarGlobal=192.168.0.19|" + current + "; QN25=\"{en7mni(z\"; VCD=" + vcd;
        return cookie;
    }

    private static String GetQunarPrice(String MixKey, String QunarCityCode, String QunarHotelId, String startDate,
            String endDate) throws Exception {
        String Hid = QunarHotelId.substring(QunarHotelId.lastIndexOf("_") + 1);
        String QunarPriceUrl = "http://hotel.qunar.com/render/detailV2.jsp?fromDate=" + startDate + "&toDate="
                + endDate + "&cityurl=" + QunarCityCode + "&HotelSEQ=" + QunarHotelId + "&mixKey=" + MixKey
                + "&roomId=" + "&lastupdate=-1&basicData=1&v=" + System.currentTimeMillis()
                + "&cn=1&requestID=c0a8e97d-m055s-29p8n" + "&_=" + System.currentTimeMillis();
        String Referer = "http://hotel.qunar.com/city/" + QunarCityCode + "/dt-" + Hid + "/?tag=" + QunarCityCode;
        //获取代理IP
        String AgentIp = AgentIpUtil.getIp();
        //请求去哪儿
        return QunarReqUtil.submit(QunarPriceUrl, AgentIp, GetCookie(Referer, AgentIp), Referer, "", "get");
    }

    /**字符串是否为空*/
    private static boolean StringIsNull(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**获取当前日期，格式为yyyy-MM-dd*/
    private static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**指定日期yyyy-MM-dd加N天*/
    private static String getAddDate(String date, int days) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(date));
        cal.add(Calendar.DATE, days);
        return sdf.format(cal.getTime());
    }

    /**
     * 读取txt文件
     * @param type 1：加价；2：去哪儿禁用代理
     */
    public static String getFileValue(int type) {
        StringBuffer buf = new StringBuffer();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            String path = "";
            if (type == 1) {
                path = ReptileGetQunarHotelPrice.class.getResource("QunarAgentAddPrice.txt").getPath();
            }
            else if (type == 2) {
                path = ReptileGetQunarHotelPrice.class.getResource("QunarDisableAgents.txt").getPath();
            }
            else {
                return "";
            }
            File file = new File(path);
            if (file.exists()) {
                fis = new FileInputStream(path);
                isr = new InputStreamReader(fis, "utf-8");
                br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    buf.append(lineTxt);
                }
            }
        }
        catch (Exception e) {
            buf = new StringBuffer();
        }
        finally {
            try {
                if (br != null)
                    br.close();
            }
            catch (Exception e) {
            }
            try {
                if (isr != null)
                    isr.close();
            }
            catch (Exception e) {
            }
            try {
                if (fis != null)
                    fis.close();
            }
            catch (Exception e) {
            }
        }
        return buf.toString();
    }

    //去哪儿房型早餐、宽带等信息
    private static String getRoomBF(String text) {
        StringBuilder sb = new StringBuilder();
        int start = text.indexOf("<span class='enc1' style='display: block;");
        if (start >= 0) {
            text = text.substring(start, text.lastIndexOf("</span>"));
            String[] texts = text.split("<(\\S*?)[^>]*>.*?");
            for (int i = 0; texts != null && i < texts.length; i++) {
                String temp = texts[i].trim();
                if (temp.contains("提前")) {//temp.contains("早") || temp.contains("宽带") || 
                    sb.append(temp);
                }
            }
        }
        return sb.toString();
    }
}