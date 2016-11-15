package com.ccservice.b2b2c.atom.component.ticket;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.ticket.Interface.CcsTicketCrawler;
import com.ccservice.b2b2c.atom.component.ticket.api.DaMaCommon;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;
import com.ccservice.b2b2c.util.AirUtil;

/**
 * 国航的数据
 * 
 * @time 2015年6月11日 下午5:12:50
 * @author chendong
 */
public class Wrapper_CA implements CcsTicketCrawler {

    final int isLogintoCreate = 0;//是否登录下单 0 不登陆下单，1，先登录再下单

    final String dirPath = "D:/CA_img/";

    String username;

    String password;

    String travelArranger_lastName;

    String travelArranger_firstName;

    String emailAddress;

    String mobilePhonePhoneNumber;

    String homePhonePhoneNumber;

    public Wrapper_CA(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2421.0 Safari/537.36";

    /**
     * 我的信息
     */
    final static String URL_MYINFO = "http://et.airchina.com.cn/www/servlet/myinfo";

    /**
     * 我的订单
     */
    final static String URL_RESERVATIONSEARCH = "http://et.airchina.com.cn/InternetBooking/ReservationSearch.do";

    private String ss;

    public static void main(String[] args) {
        Wrapper_CA CA = new Wrapper_CA("13522543333", "nicaicai123");
        //        登录测试start        CA.loginGetCookie("", "");
        //登录测试end          getMyinfo(cookieString, 2);
        //下单start
        List<Passenger> passengers = getPassengers();
        List<Segmentinfo> segments = getSegments();
        String cookieString = "JSESSIONID=A66A51AAB5D3D156455AAB1E18F5741B; current_PoS=AIRCHINA_CN; "
                + "currentLang=zh_CN; loginCookie=\"username=cd1989929~rememberMe=false~lastname=?~firstname=?~\"; "
                //                + " _pzfxuvpc=1434014136310%7C2528387236714191596%7C25%7C1435299325219%7C16%7C7364019167948187093%7C1105714623738227546; _pzfxsfc=; "
                //                + "mbox=session#1435299107175-52873#1435301199|check#true#1435299399; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3D%3B; "
                + "userName=Y2QxOTg5OTI5; masterSessionId=NDI1MTY1MzE2NDM1; "
                //                + "_pzfxsvpc=1105714623738227546%7C1435299325218%7C1%7Chttp%3A%2F%2Fwww.airchina.com.cn%2Fwww%2Fjsp%2FuserManager%2Fmyinfo.jsp; "
                //                + "vvvvvvv=cb82982dvvvvvvv_cb82982d; "
                + "BIGipServerWeb_http=1460281004.20480.0000";

        FlightSearch param = getFlightSearch(cookieString, passengers);
        //创建订单
        CA.createOrder(cookieString, passengers, segments, param);

        //        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        //打开行程概括的页面
        cookieString = "JSESSIONID=083124B3821973094308D3AB224E314F; BIGipServerWeb_http=1292508844.20480.0000;";
        try {
            //            CA.getUrlToxingchengGaikuo(cookieString, "17");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //打开行程概括的页面
        //        CA.ItinerarySummary(cookieString, httpClient);
        //        验证数据的正确性
        //        String rand_code = CA.ValidateFormAction(cookieString, passengers, segments, param);
        String rand_code = "9u64";
        String responseBodyString_ReservationPrepareToCreate = CA.ValidateFormAction1(cookieString, passengers,
                segments, param, rand_code);
        responseBodyString_ReservationPrepareToCreate = CA.ValidateFormAction2(cookieString, passengers, segments,
                param, rand_code);

        //单独测试去下单打码的页面
        //        CA.ReservationPrepareToCreate(cookieString, passengers, segments, param, rand_code);
        //        CA.doAirSelectOWCFlight(cookieString, "37");
        //验证订单并登录
        //        cookieString = "JSESSIONID=28DF2AA9368817C99B0CBB4B1C957DC4; current_PoS=AIRCHINA_CN; currentLang=zh_CN; _pzfxuvpc=1434014136310%7C2528387236714191596%7C65%7C1435824503996%7C38%7C1223983382765133299%7C3432430242706759206; _pzfxsfc=; ooooooo=d22c9c4cooooooo_d22c9c4c; BIGipServerWeb_http=1359617708.20480.0000; mbox=session#1435823797171-706489#1435826383|check#true#1435824583; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3D%3B; vvvvvvv=fa9d7569vvvvvvv_fa9d7569; _pzfxsvpc=3432430242706759206%7C1435824503995%7C1%7Chttp%3A%2F%2Fet.airchina.com.cn%2FInternetBooking%2FTravelersDetailsForwardAction.do";
        //        CA.ValidateFormActionLogin(cookieString, passengers, segments, param);
        //        CA.doAirSelectOWCFlight(cookieString, "37");
        //            CA.createOrder_tempTo(cookieString);
    }

    /**
     * 
     * @param cookieString
     * @return
     * @time 2015年7月1日 下午5:46:46
     * @author chendong
     * @param passengers 
     */
    private static FlightSearch getFlightSearch(String cookieString, List<Passenger> passengers) {
        FlightSearch param = new FlightSearch();
        param.setStartAirportCode("PEK");
        param.setStartAirPortName("北京");
        param.setEndAirportCode("SHA");
        param.setEndAirPortName("上海");
        param.setFromDate("2015-07-13");
        param.setTravelType("3");//这里的2是代表带cookie的查询。3是代表获取一个新的cookie
        param.setGeneral(passengers.size());
        param.setThirdAirportCode(cookieString);//cookie放到了ThirdAirportCode这里面
        return param;
    }

    /**
     * 
     * @return
     * @time 2015年7月1日 下午5:45:22
     * @author chendong
     */
    private static List<Segmentinfo> getSegments() {
        List<Segmentinfo> segments = new ArrayList<Segmentinfo>();
        Segmentinfo segmentinfo = new Segmentinfo();
        segmentinfo.setFlightnumber("CA1531");
        segmentinfo.setParvalue(1230F);
        segments.add(segmentinfo);
        return segments;
    }

    /**
     * 
     * @return
     * @time 2015年7月1日 下午5:44:56
     * @author chendong
     */
    private static List<Passenger> getPassengers() {
        List<Passenger> passengers = new ArrayList<Passenger>();
        Passenger p1 = new Passenger();
        Passenger p2 = new Passenger();
        passengers.add(p1);
        passengers.add(p2);
        return passengers;
    }

    /**
     * 根据html1获取到 radio的value的值
     * @param html1
     * @time 2015年6月30日 下午4:23:18
     * @author chendong
     * @param segments 
     * @param httpClient 
     */
    private static String getRadioValuebyHtml(String html1, List<Segmentinfo> segments) {
        String radioValue = "0";
        JSONObject jsonobject = JSONObject.parseObject(html1);
        Set<String> sets = jsonobject.keySet();
        Iterator iterator = sets.iterator();
        sets = jsonobject.keySet();
        iterator = sets.iterator();
        String flightnumber = segments.get(0).getFlightnumber();
        Float parPrice = segments.get(0).getParvalue();
        while (iterator.hasNext()) {
            String array_key = iterator.next().toString();
            String dataString = jsonobject.getString(array_key);//D|T|G||C|O|Z|K
            //            System.out.println("------------------------------------------dataString");
            //            System.out.println(dataString);
            if (dataString.indexOf(flightnumber) >= 0) {
                String[] dataString_itineraryPriceCell = dataString.split("itineraryPriceCell");
                for (int i = 0; i < dataString_itineraryPriceCell.length; i++) {
                    String temp_dataString_itineraryPriceCell = dataString_itineraryPriceCell[i].replace(",", "");
                    if (temp_dataString_itineraryPriceCell.indexOf(parPrice.intValue() + "") >= 0) {
                        temp_dataString_itineraryPriceCell = temp_dataString_itineraryPriceCell.substring(
                                temp_dataString_itineraryPriceCell.indexOf("radio"),
                                temp_dataString_itineraryPriceCell.length() - 1);
                        temp_dataString_itineraryPriceCell = temp_dataString_itineraryPriceCell.substring(
                                temp_dataString_itineraryPriceCell.indexOf("value") + 7,
                                temp_dataString_itineraryPriceCell.indexOf("id=") - 2).trim();
                        radioValue = temp_dataString_itineraryPriceCell;
                        break;
                    }
                }
            }
        }
        return radioValue;
    }

    /**
     * 打开行程概括和打码的那个页面
     * @time 2015年6月30日 上午11:54:20
     * @author chendong
     * @param cookieString 
     */
    public void openXingchengGaikuoAndDama(String cookieString) {
        Wrapper_CAHttpClient httpClient = new Wrapper_CAHttpClient(false, 60000L);
        String ItinerarySummary = "http://et.airchina.com.cn/InternetBooking/ItinerarySummary.do";
        CCSGetMethod get_ItinerarySummary = new CCSGetMethod(ItinerarySummary);
        get_ItinerarySummary.setFollowRedirects(false);
        get_ItinerarySummary.setRequestHeader("Cookie", cookieString);
        int status_get_ItinerarySummary;
        try {
            status_get_ItinerarySummary = httpClient.executeMethod(get_ItinerarySummary);
            //          String cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            System.out.println("======================status_get_ItinerarySummary=============");
            System.out.println(status_get_ItinerarySummary);
            String responseBodyString_ItinerarySummary = get_ItinerarySummary.getResponseBodyAsString();
            //            System.out.println("======================responseBodyString_ItinerarySummary=============");
            //            System.out.println(responseBodyString_ItinerarySummary);
        }
        catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static String getMyinfo(String cookieString, int infoType) {
        CCSGetMethod get = null;
        Wrapper_CAHttpClient httpClient = new Wrapper_CAHttpClient(false, 60000L);
        if (infoType == 1) {//我的信息
            get = new CCSGetMethod(URL_MYINFO);
        }
        else if (infoType == 2) {//我的订单
            get = new CCSGetMethod(URL_RESERVATIONSEARCH);
        }
        get.setFollowRedirects(false);
        get.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        get.addRequestHeader("Accept-Encoding", "gzip, deflate");
        get.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        get.addRequestHeader("Cache-Control", "max-age=0");
        get.addRequestHeader("Connection", "keep-alive");
        get.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");//
        get.addRequestHeader("Host", "www.airchina.com.cn");
        get.addRequestHeader("Origin", "http://www.airchina.com.cn");
        get.addRequestHeader("User-Agent", USER_AGENT);
        get.addRequestHeader("Cookie", cookieString);
        try {
            int status = httpClient.executeMethod(get);
            String responseBodyString = get.getResponseBodyAsString();
            //            System.out.println("----------------responseBodyString----------------");
            //            System.out.println(responseBodyString);
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String loginGetCookie(String userName, String password) {
        String cookieString = "";
        String B2CUserLogin_url = "http://www.airchina.com.cn/www/servlet/com.ace.um.userLogin.servlet.B2CUserLogin";
        CCSPostMethod post = new CCSPostMethod(B2CUserLogin_url);
        //        PostMethod post = new PostMethod(B2CUserLogin_url);

        CCSGetMethod get = null;
        //        GetMethod get = null;
        Wrapper_CAHttpClient httpClient = new Wrapper_CAHttpClient(false, 60000L);
        //        HttpClient httpClient = new HttpClient();

        String cookiestring_temp = Wrapper_CAUtil.getcookie(httpClient);
        StringBuffer cookiestring = new StringBuffer(cookiestring_temp);
        System.out.println("====cookiestring==============");
        System.out.println(cookiestring.toString());
        String url_tupian_path = "http://www.airchina.com.cn/www/servlet/com.ace.um.common.verify.VerifyCodeServlet?"
                + "t=" + System.currentTimeMillis() + "?timestamp=" + System.currentTimeMillis() + "?timestamp="
                + System.currentTimeMillis();
        String picturepath = Wrapper_CAUtil.downloadimgbyhttpclient(url_tupian_path, cookiestring.toString(), dirPath);
        System.out.println("====picturepath==============");
        System.out.println(picturepath);
        DaMaCommon dmc = WrapperUtil.getcheckcodebydama(picturepath, 0);//验证码打码
        String rand_code = dmc.getResult().toUpperCase();
        //        String rand_code = "ASHJ";//验证码打码
        System.out.println("====rand_code==============");
        System.out.println(rand_code);
        String ewjy = rand_code;//验证码

        //        StringBuffer cookiestring = new StringBuffer(
        //                "JSESSIONID=EEBFA0A94BEB57898E76D77544781329; current_PoS=AIRCHINA_CN; currentLang=zh_CN; _pzfxuvpc=1434014136310%7C2528387236714191596%7C20%7C1435051228644%7C12%7C6919797455605180217%7C6317771193423166396; _pzfxsfc=; BIGipServerWeb_http=1359617708.20480.0000; mbox=session#1435112631184-473548#1435116117|check#true#1435114317; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3Dacna-dom2-sc-prd%253D%252526pid%25253Dhttp%2525253A%2525252F%2525252Fwww.airchina.com.cn%2525252Fwww%2525252Fjsp%2525252FuserManager%2525252Flogin.jsp%252526oid%25253Dhttp%2525253A%2525252F%2525252Fwww.airchina.com.cn%2525252Fwww%2525252Fjsp%2525252FuserManager%2525252Flogin.jsp%25252523%252526ot%25253DA%3B");
        //        String ewjy = "uzks";//验证码

        post.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.addRequestHeader("Accept-Encoding", "gzip, deflate");
        post.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        post.addRequestHeader("Cache-Control", "max-age=0");
        post.addRequestHeader("Connection", "keep-alive");
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");//
        post.addRequestHeader("Host", "www.airchina.com.cn");
        post.addRequestHeader("Origin", "http://www.airchina.com.cn");
        post.addRequestHeader("User-Agent", USER_AGENT);

        //        post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

        //        cookiestring = "loginCookie=\"username=cd1989929~rememberMe=false~lastname=?~firstname=?~\"; JSESSIONID=B1D47C923FFCCE959FB2F890733F84A7; current_PoS=AIRCHINA_CN; currentLang=zh_CN; _pzfxuvpc=1434014136310%7C2528387236714191596%7C11%7C1434450176873%7C7%7C9241676462334910252%7C1302013347742216119; BIGipServerWeb_http=1342840492.20480.0000; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3D%3B; _pzfxsfc=; zzzzzzz=6e649322zzzzzzz_6e649322; Countdown=37; mobilePhoneS=13522543333; mbox=session#1434448655006-112844#1434453601|check#true#1434451801; userName=MTM1MjI1NDMzMzM=; masterSessionId=NDI1MDAyNzc1NjYx";
        //        System.out.println("====cookiestring==============");
        //        System.out.println(cookiestring);
        //        String cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
        //        cookiestring.append(cookiestring_temp_2);

        //        Cookie[] arg0 = getCookies(cookiestring.toString());
        //        httpClient.getState().addCookies(arg0);

        String typeselect = URLEncoder.encode("用户名");
        NameValuePair NameValuePair_typeselect = new NameValuePair("typeselect", typeselect);
        NameValuePair NameValuePair_userFlag = new NameValuePair("userFlag", "");
        //        userName = getSignString(userName);
        //        userName = "2fe37d9753e0d0961091a0d2adc9c33c2169a5a216ee1880f8d24c9f76f32d106d25990e96833839410c40cd7a33cdc98e1e678a4732f60cb511a3f91a8e485c6571ba506f3e7568d7cd3d71747206f59b86186cc89a7b6ca474d093748bda8f26026dad90219605a326ee5fdddfcf9bb7431f967a0c0bfeb834b784822c0deb";
        userName = "1f87b94e98615aaf470a0215eed12e72d62e22ec1eb5832b31d104ca5560af581c5d91e35c2f459ec8c40d02dc7a7e44a3339050ff051df91104f60fc742286eaa5d66d86071b57c3252ee35adc0717362932d1a626ba3b5a2989a4ee534e219350349e189e3af12c4470024f0bec7abf421455158c6ec752ed3103fc0d3c446";
        NameValuePair NameValuePair_userName = new NameValuePair("userName", userName);
        //        password = getSignString(password);
        password = "2fe37d9753e0d0961091a0d2adc9c33c2169a5a216ee1880f8d24c9f76f32d106d25990e96833839410c40cd7a33cdc98e1e678a4732f60cb511a3f91a8e485c6571ba506f3e7568d7cd3d71747206f59b86186cc89a7b6ca474d093748bda8f26026dad90219605a326ee5fdddfcf9bb7431f967a0c0bfeb834b784822c0deb";
        NameValuePair NameValuePair_password = new NameValuePair("password", password);
        NameValuePair NameValuePair_ewjy = new NameValuePair("ewjy", ewjy.toUpperCase());
        NameValuePair NameValuePair_directURL = new NameValuePair("directURL", "");
        NameValuePair NameValuePair_userLoginType = new NameValuePair("userLoginType", "2");
        NameValuePair NameValuePair_userLoginFlag = new NameValuePair("userLoginFlag", "userLoginFlag");

        NameValuePair[] nameValuePairs = { NameValuePair_typeselect, NameValuePair_userFlag, NameValuePair_userName,
                NameValuePair_password, NameValuePair_ewjy, NameValuePair_directURL, NameValuePair_userLoginType,
                NameValuePair_userLoginFlag };
        post.setRequestBody(nameValuePairs);
        post.addRequestHeader("Content-Length", Wrapper_CAUtil.getContentLength(nameValuePairs));

        //        String cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
        //        System.out.println("===cookiestring_temp_2==========================");
        //        System.out.println(cookiestring_temp_2);
        //        cookiestring.append(cookiestring_temp_2);
        post.addRequestHeader("Cookie", cookiestring.toString());

        String toLoginUrl_referer = "http://www.airchina.com.cn/www/jsp/userManager/login.jsp";
        post.addRequestHeader("Referer", toLoginUrl_referer);

        try {
            int status = httpClient.executeMethod(post);
            System.out.println("----status--------------");
            System.out.println(status);
            String responseBodyString = post.getResponseBodyAsString();
            WriteLog.write(System.currentTimeMillis() + "", responseBodyString);
            if (status == 302) {
                Header[] responseHears = post.getResponseHeaders();
                System.out.println("---responseHears-----------------------");
                String Location = Wrapper_CAUtil.get302Location(responseHears);
                System.out.println("---responseHears-----------------------");
                if ("/".equals(Location)) {
                    Location = "http://www.airchina.com.cn/";
                }
                else {

                }
                String cookie_new = Wrapper_CAUtil.getCookieString(httpClient);
                System.out.println("----cookie_new--------------");
                System.out.println(cookie_new);
                get = new CCSGetMethod(Location);
                //                get = new GetMethod(Location);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                status = httpClient.executeMethod(get);
                System.out.println("----status2--------------");
                System.out.println(status);

                responseBodyString = post.getResponseBodyAsString();
                System.out.println("----------------responseBodyString2----------------");
                System.out.println(responseBodyString);
            }
            else if (status == 404) {
                System.out.println("----------------404----------------");
            }
            else {
                if (responseBodyString.indexOf("您输入的验证码不正确，请重新输入！") >= 0) {
                    System.out.println("您输入的验证码不正确，请重新输入！");
                }
                else if (responseBodyString.indexOf("用户名和密码不匹配") >= 0) {
                    System.out.println("用户名和密码不匹配");
                }
                else {
                }
            }
            System.out.println("----------------responseBodyString----------------");
            System.out.println(responseBodyString);

        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return cookieString;
    }

    public static String getSignString(String userName) {
        //        var a = [];
        //        var sl = s.length;
        //        var i = 0;
        //        while (i < sl) {
        //            a[i] = s.charCodeAt(i);
        //            i++;
        //        }
        //        while (a.length % key.chunkSize != 0) {
        //            a[i++] = 0;
        //        }

        char[] a = userName.toCharArray();
        int sl = userName.length();
        int i = a.length;
        int keyChunkSize = 126;
        if (a.length % keyChunkSize != 0) {
            a[i++] = 0;
        }

        //        var al = a.length;
        int al = a.length;
        //        var result = "";
        String result = "";
        //        var j, k, block;
        Long block, j, k;

        //        for (i = 0; i < al; i += key.chunkSize) {
        for (int ij = 0; ij < al; ij += keyChunkSize) {
            //            block = new Long();
            //            block = new BigInt();
            //            j = 0;
            j = 0L;
            //            for (k = i; k < i + key.chunkSize; ++j) {
            for (int kj = 0; kj < i + keyChunkSize; ++j) {
                //#TODO
                //                block.digits[j] = a[k++];
                //                block.digits[j] += a[k++] << 8;
                //            }
            }
        }

        //            var crypt = key.barrett.powMod(block, key.e);
        //            var text = key.radix == 16 ? RSAUtils.biToHex(crypt) : RSAUtils
        //                    .biToString(crypt, key.radix);
        //            result += text + " ";
        //        }

        return userName;

    }

    public static String getSignString2(String userName) {
        //执行JS，获取value
        ScriptEngineManager engineManager = new ScriptEngineManager();
        String shortName = "JavaScript";
        //        shortName = "js";
        //        shortName = "application/javascript";
        for (ScriptEngineFactory available : engineManager.getEngineFactories()) {
            System.out.println(available.getEngineName());
        }

        ScriptEngine engine = engineManager.getEngineByName(shortName);
        String security_js_path = Wrapper_CA.class.getResource("security.js").getPath();
        System.out.println(security_js_path);
        try {
            FileReader reader = new FileReader(security_js_path);
            engine.eval(reader);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ScriptException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Invocable inv = (Invocable) engine;
        try {
            //            key = RSAUtils.getKeyPair("010001","","0098471b9a05c816ee949b4fe93520a8681a14e65d7a0501221136951a52a3b76cf9e2375e45aca1ad6fc9f00b401ece966a1f8fb521dd9de4215c90b7e9cd77b1c7d2f6e9b7aba6f94322d7375cbb321be653826d921030b6ef9fd453a7ece0ae4785a6166dd5d1560f3992cbad493201bb18616251610890bd0ea6736c346e15"); 
            String encryptionExponent = "010001";
            String modulus = "0098471b9a05c816ee949b4fe93520a8681a14e65d7a0501221136951a52a3b76cf9e2375e45aca1ad6fc9f00b401ece966a1f8fb521dd9de4215c90b7e9cd77b1c7d2f6e9b7aba6f94322d7375cbb321be653826d921030b6ef9fd453a7ece0ae4785a6166dd5d1560f3992cbad493201bb18616251610890bd0ea6736c346e15";
            Object key = inv.invokeFunction("RSAUtils.getKeyPair", encryptionExponent, "", modulus);
            userName = String.valueOf(inv.invokeFunction("RSAUtils.encryptedString", key, userName));
        }
        catch (ScriptException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return userName;
    }

    public void createOrder(String cookieString, List<Passenger> passengers, List<Segmentinfo> segments,
            FlightSearch param) {
        //        httpClient = getNewCookieString(httpClient, cookieString);
        //        String flightItineraryId = searchAndGetRadioValue(cookieString, passengers, segments, param,httpClient);//查询获取到带cookieString的查询内容
        //        String html = "{\"397663916\":\"<tbody id='397663916'>\n\t\t\t\t\t\t<tr class='rowOdd'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=4136&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=A&operatingAirlineCode=&cabinClass=First');return false;'>CA4136</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >20:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >23:00</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_8_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_8_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=73T');return false;'>73T</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_47' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='47' id='flightSelectGr_0_47'  onclick='selectFareFamily(0, 8, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_47'>2,460</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<div class='specialFaresTick' onMouseOver='toolTip.over(this,event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t不得签转外航</p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_ADZJ_first.gif?version=201505131931' width='24' height='23'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余4 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>4</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_8_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_36' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='36' id='flightSelectGr_0_36'  onclick='selectFareFamily(0, 8, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_36'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_8_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_21' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='21' id='flightSelectGr_0_21'  onclick='selectFareFamily(0, 8, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_21'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_8_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_10' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='10' id='flightSelectGr_0_10'  onclick='selectFareFamily(0, 8, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_10'>810</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_8_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"1506565247\":\"<tbody id='1506565247'>\n\t\t\t\t\t\t<tr class='rowLast rowOdd'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=986&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=F&operatingAirlineCode=&cabinClass=First');return false;'>CA986</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >20:15</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >22:45</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_12_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_12_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=747');return false;'>747</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_66' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='66' id='flightSelectGr_0_66'  onclick='selectFareFamily(0, 12, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_66'>4,870</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_12_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_49' class='colCost colCost2 colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='49' id='flightSelectGr_0_49'  onclick='selectFareFamily(0, 12, 'DC'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_49'>3,730</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_12_0_DC' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_38' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='38' id='flightSelectGr_0_38'  onclick='selectFareFamily(0, 12, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_38'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_12_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_24' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='24' id='flightSelectGr_0_24'  onclick='selectFareFamily(0, 12, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_24'>1,190</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_12_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost5 colCostNotAvail colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"-348881376\":\"<tbody id='-348881376'>\n\t\t\t\t\t\t<tr class='rowEven'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=4130&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=A&operatingAirlineCode=&cabinClass=First');return false;'>CA4130</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >12:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >15:00</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_11_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_11_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=73G');return false;'>73G</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_43' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='43' id='flightSelectGr_0_43'  onclick='selectFareFamily(0, 11, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_43'>2,460</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<div class='specialFaresTick' onMouseOver='toolTip.over(this,event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t不得签转外航</p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_ADZJ_first.gif?version=201505131931' width='24' height='23'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余2 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>2</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_11_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_28' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='28' id='flightSelectGr_0_28'  onclick='selectFareFamily(0, 11, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_28'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_11_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_23' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='23' id='flightSelectGr_0_23'  onclick='selectFareFamily(0, 11, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_23'>1,190</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_11_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost5 colCostNotAvail colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"-1995500606\":\"<tbody id='-1995500606'>\n\t\t\t\t\t\t<tr class='rowEven'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=1431&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=A&operatingAirlineCode=&cabinClass=First');return false;'>CA1431</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >08:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >10:55</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_3_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_3_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=330');return false;'>330</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_41' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='41' id='flightSelectGr_0_41'  onclick='selectFareFamily(0, 3, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_41'>2,460</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<div class='specialFaresTick' onMouseOver='toolTip.over(this,event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t不得签转外航</p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_ADZJ_first.gif?version=201505131931' width='24' height='23'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余7 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>7</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_3_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_26' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='26' id='flightSelectGr_0_26'  onclick='selectFareFamily(0, 3, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_26'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_3_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_12' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='12' id='flightSelectGr_0_12'  onclick='selectFareFamily(0, 3, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_12'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_3_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_5' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='5' id='flightSelectGr_0_5'  onclick='selectFareFamily(0, 3, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_5'>810</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余4 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>4</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_3_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"1283841477\":\"<tbody id='1283841477'>\n\t\t\t\t\t\t<tr class='rowOdd'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=1156&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=F&operatingAirlineCode=SC&cabinClass=First');return false;'>CA1156</a><span class='refAirlines' onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_op_airline.gif?version=201505131931' width='15' height='15' />\n\t\t\t\t\t\t\t\t\n\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>承运航空公司：山东航空股份有限公司</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >20:05</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >23:10</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_2_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_2_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=738');return false;'>738</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_68' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='68' id='flightSelectGr_0_68'  onclick='selectFareFamily(0, 2, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_68'>4,920</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余5 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>5</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_2_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_40' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='40' id='flightSelectGr_0_40'  onclick='selectFareFamily(0, 2, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_40'>1,640</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_2_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_22' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='22' id='flightSelectGr_0_22'  onclick='selectFareFamily(0, 2, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_22'>1,030</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_2_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_4' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='4' id='flightSelectGr_0_4'  onclick='selectFareFamily(0, 2, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_4'>740</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_2_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"-1237857038\":\"<tbody id='-1237857038'>\n\t\t\t\t\t\t<tr class='rowOdd'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=1439&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=F&operatingAirlineCode=&cabinClass=First');return false;'>CA1439</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >14:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >16:50</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_4_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_4_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=738');return false;'>738</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_56' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='56' id='flightSelectGr_0_56'  onclick='selectFareFamily(0, 4, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_56'>4,870</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余4 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>4</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_4_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_30' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='30' id='flightSelectGr_0_30'  onclick='selectFareFamily(0, 4, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_30'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_4_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_15' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='15' id='flightSelectGr_0_15'  onclick='selectFareFamily(0, 4, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_15'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_4_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_6' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='6' id='flightSelectGr_0_6'  onclick='selectFareFamily(0, 4, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_6'>810</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_4_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"2025858984\":\"<tbody id='2025858984'>\n\t\t\t\t\t\t<tr class='rowEven'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=1411&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=F&operatingAirlineCode=&cabinClass=First');return false;'>CA1411</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >14:55</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >17:55</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_5_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_5_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=738');return false;'>738</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_58' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='58' id='flightSelectGr_0_58'  onclick='selectFareFamily(0, 5, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_58'>4,870</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余5 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>5</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_5_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_31' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='31' id='flightSelectGr_0_31'  onclick='selectFareFamily(0, 5, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_31'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_5_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_16' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='16' id='flightSelectGr_0_16'  onclick='selectFareFamily(0, 5, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_16'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_5_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_7' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='7' id='flightSelectGr_0_7'  onclick='selectFareFamily(0, 5, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_7'>810</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_5_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"-206161425\":\"<tbody id='-206161425'>\n\t\t\t\t\t\t<tr class='rowEven'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=4144&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=A&operatingAirlineCode=&cabinClass=First');return false;'>CA4144</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >19:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >22:00</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_1_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_1_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=73T');return false;'>73T</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_46' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='46' id='flightSelectGr_0_46'  onclick='selectFareFamily(0, 1, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_46'>2,460</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<div class='specialFaresTick' onMouseOver='toolTip.over(this,event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t不得签转外航</p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_ADZJ_first.gif?version=201505131931' width='24' height='23'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余3 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>3</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_1_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_35' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='35' id='flightSelectGr_0_35'  onclick='selectFareFamily(0, 1, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_35'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_1_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_20' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='20' id='flightSelectGr_0_20'  onclick='selectFareFamily(0, 1, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_20'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_1_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_1' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='1' id='flightSelectGr_0_1'  onclick='selectFareFamily(0, 1, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_1'>650</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_1_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"-2010377237\":\"<tbody id='-2010377237'>\n\t\t\t\t\t\t<tr class='rowEven'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=1429&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=A&operatingAirlineCode=&cabinClass=First');return false;'>CA1429</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >09:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >11:55</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_9_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_9_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=738');return false;'>738</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_42' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='42' id='flightSelectGr_0_42'  onclick='selectFareFamily(0, 9, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_42'>2,460</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<div class='specialFaresTick' onMouseOver='toolTip.over(this,event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t不得签转外航</p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_ADZJ_first.gif?version=201505131931' width='24' height='23'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余2 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>2</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_9_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_27' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='27' id='flightSelectGr_0_27'  onclick='selectFareFamily(0, 9, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_27'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_9_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_13' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='13' id='flightSelectGr_0_13'  onclick='selectFareFamily(0, 9, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_13'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_9_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost5 colCostNotAvail colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"567604551\":\"<tbody id='567604551'>\n\t\t\t\t\t\t<tr class='rowOdd'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=1409&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=A&operatingAirlineCode=&cabinClass=First');return false;'>CA1409</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >16:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >19:00</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_6_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_6_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=738');return false;'>738</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_48' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='48' id='flightSelectGr_0_48'  onclick='selectFareFamily(0, 6, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_48'>2,950</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<div class='specialFaresTick' onMouseOver='toolTip.over(this,event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t不得签转外航</p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_ADZJ_first.gif?version=201505131931' width='24' height='23'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot colLimSeatOne' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余1 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>1</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_6_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_32' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='32' id='flightSelectGr_0_32'  onclick='selectFareFamily(0, 6, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_32'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_6_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_17' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='17' id='flightSelectGr_0_17'  onclick='selectFareFamily(0, 6, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_17'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_6_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_8' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='8' id='flightSelectGr_0_8'  onclick='selectFareFamily(0, 6, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_8'>810</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_6_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"1707842339\":\"<tbody id='1707842339'>\n\t\t\t\t\t\t<tr class='rowOdd'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=4138&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=A&operatingAirlineCode=&cabinClass=First');return false;'>CA4138</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >13:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >16:05</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_10_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_10_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=73T');return false;'>73T</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_44' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='44' id='flightSelectGr_0_44'  onclick='selectFareFamily(0, 10, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_44'>2,460</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<div class='specialFaresTick' onMouseOver='toolTip.over(this,event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t不得签转外航</p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_ADZJ_first.gif?version=201505131931' width='24' height='23'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余4 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>4</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_10_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_29' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='29' id='flightSelectGr_0_29'  onclick='selectFareFamily(0, 10, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_29'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_10_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_14' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='14' id='flightSelectGr_0_14'  onclick='selectFareFamily(0, 10, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_14'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余8 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>8</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_10_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost5 colCostNotAvail colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"-1068665939\":\"<tbody id='-1068665939'>\n\t\t\t\t\t\t<tr class='rowEven'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=1435&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=F&operatingAirlineCode=&cabinClass=First');return false;'>CA1435</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >17:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >20:00</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_7_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_7_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=738');return false;'>738</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_61' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='61' id='flightSelectGr_0_61'  onclick='selectFareFamily(0, 7, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_61'>4,870</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余8 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>8</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_7_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_33' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='33' id='flightSelectGr_0_33'  onclick='selectFareFamily(0, 7, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_33'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_7_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_18' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='18' id='flightSelectGr_0_18'  onclick='selectFareFamily(0, 7, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_18'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_7_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_9' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='9' id='flightSelectGr_0_9'  onclick='selectFareFamily(0, 7, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_9'>810</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_7_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\",\"1684660499\":\"<tbody id='1684660499'>\n\t\t\t\t\t\t<tr class='rowFirst rowOdd'>\n\t\t<td class='colFlight'>\n\t\t\t<div>\n\t\t\t\t<a href='#' onclick='javascript:showFlightDetailsPopUp('AirFlightDetailsGetAction.do?airlineCode=CA&flightNumber=4142&origin=PEK&destination=CKG&departureDay=13&departureMonth=5&departureYear=2015&classOfTravel=A&operatingAirlineCode=&cabinClass=First');return false;'>CA4142</a></div>\n\t\t</td>\n\t\t<td class='colDepart'>\n\t\t\t<div >18:00</div>\n            </td>\n\t\t<td class='colArrive'>\n\t\t\t<div >21:00</div>\n\t\t\t</td>\n\t\t<td class='colAirports'>\n\t\t\t\t<div>\n\t\t\t\t\t<span onMouseOver='toolTip.over(this, event);'>\n\t\t\t\t\t\t\t\tPEK-CKG<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t北京首都国际机场 (PEK) - 重庆江北机场 (CKG)</p></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t<span id='ORIGIN_DESTINATION_0_0_0' style='display:none'>PEK-CKG</span>\n\t\t\t\t\t<span id='ITINERARY_0_0_0' style='display:none'/>\n\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td class='colType'>\n\t\t\t\t<div>\t\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t<a href='#' onclick='javascript:showEquipmentTypePopUp('AirFlightEquipmentTypeAction.do?equipmentType=73T');return false;'>73T</a>\n\t\t\t\t\t\t</div>\n\t\t\t</td>\n\t\t<td id='itineraryPriceCell_0_45' class='colCost colCost1 colCost_DF' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='45' id='flightSelectGr_0_45'  onclick='selectFareFamily(0, 0, 'DF'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_45'>2,460</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<div class='specialFaresTick' onMouseOver='toolTip.over(this,event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t不得签转外航</p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='//static.airchina.wscdns.com/InternetBooking/pictures/icons/i_ADZJ_first.gif?version=201505131931' width='24' height='23'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余2 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>2</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_0_0_DF' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_' class='colCost colCost2 colCostNotAvail colCost_DC' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t-</div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_34' class='colCost colCost3 colCost_DY1' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='34' id='flightSelectGr_0_34'  onclick='selectFareFamily(0, 0, 'DY1'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_34'>1,620</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_0_0_DY1' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_19' class='colCost colCost4 colCost_DY2' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='19' id='flightSelectGr_0_19'  onclick='selectFareFamily(0, 0, 'DY2'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_19'>1,020</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_0_0_DY2' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t<td id='itineraryPriceCell_0_0' class='colCost colCost5 colCost_DY3 colCostLast' rowspan='1'>\n\t\t\t\t\t\t\t<div >\n\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colRadio'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<input type='radio' name='flightItineraryId[0]' value='0' id='flightSelectGr_0_0'  onclick='selectFareFamily(0, 0, 'DY3'); fareFamiliesFlightSelection.selectFlight(this); reloadItinerarySummaryInfo(this)' />\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='colPrice'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<label for='flightSelectGr_0_0'>650</label>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t<div class='colCostDetailsWrap'>\n\t\t\t\t\t\t\t\t\t\t\t<!---->\n\t\t\t\t\t\t\t\t\t\t\t<div class='colLimSeat colLimSeatHot' onMouseOver='toolTip.over(this, event)'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='toolTipInfo'>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class='simpleToolTip'><p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t这个价格还<b>剩余2 个座位</b><br /></p></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<b>2</b></div>\n\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span id='FARE_FAMILY_0_0_0_DY3' style='display:none'/></div>\n\t\t\t\t\t\t</td>\n\t\t\t\t\t</tr>\n</tbody>\n\t\t\t\t\"}|50,0";
        String html = getHtml(param);
        //        System.out.println(html);
        String html1 = html.split("[|]")[0];
        cookieString = html.split("[|]")[2];
        //        cookieString += " mbox=" + getmboxCookieString();
        String toxingchenggaikuoUrl = getXingchenggaikuoUrl(cookieString, html1, segments);

        //到了行程概括,这个页面打开后是需要打码的
        if (toxingchenggaikuoUrl.indexOf("ItinerarySummary") >= 0) {
            //#TODO 获取到行程信息
            //                        http://et.airchina.com.cn/InternetBooking/InsuranceCrossSellAddToShoppingCartAction.do?insuranceSelection=-1158824742:false&flowStep=ITINERARY_SUMMARY
            //                    http://et.airchina.com.cn/InternetBooking/ItinerarySummary.do
            //打开行程概括的页面
            String result_ItinerarySummary = ItinerarySummary(cookieString);
            //
            String reuslt_ValidateFlow = ValidateFlow(cookieString);
            reuslt_ValidateFlow = getNewResponseBodyString(reuslt_ValidateFlow);
            System.out.println("====================reuslt_ValidateFlow====================");
            System.out.println(reuslt_ValidateFlow);
            //验证数据的正确性
            //                                        String rand_code = ValidateFormAction(cookieString, passengers, segments, param);
            //开始转到打码后的页面去
            //                    String TravelersDetailsForwardAction = ReservationPrepareToCreate(cookieString, passengers,
            //                            segments, param, rand_code);
            //
            //验证数据的正确性
            String rand_code = ValidateFormActionLogin(cookieString, passengers, segments, param);
            if (!"XXXX".equals(rand_code)) {
                //http://et.airchina.com.cn/InternetBooking/ReservationLogin.do
                String TravelersDetailsForwardAction = ReservationLogin(cookieString, passengers, segments, param,
                        rand_code);
            }
        }
    }

    /**
     * 
     * @param cookieString
     * @return
     * @time 2015年7月2日 下午8:06:09
     * @author chendong
     * @param html1 
     * @param segments 
     */
    private String getXingchenggaikuoUrl(String cookieString, String html1, List<Segmentinfo> segments) {
        String toxingchenggaikuoUrl = "-1";

        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        //        Cookie[] Cookies = getCookiesBycookieString(cookieString);
        //        httpClient.getState().addCookies(Cookies);
        String flightItineraryId = getRadioValuebyHtml(html1, segments);
        System.out.println("================radioValue=flightItineraryId========================");
        System.out.println(flightItineraryId);
        //      http://et.airchina.com.cn/InternetBooking/AirSelectOWCFlight.do
        //选择一个价格异步请求数据
        doAirSelectOWCFlight(cookieString, flightItineraryId);
        try {
            //            String responseBodyString_ValidateFlow = ValidateFlow(cookieString);
            //            responseBodyString_ValidateFlow = getNewResponseBodyString(responseBodyString_ValidateFlow);
            //            System.out.println("======================responseBodyString_ValidateFlow=============");
            //            System.out.println(responseBodyString_ValidateFlow);
            //            if (responseBodyString_ValidateFlow.indexOf("OK") >= 0) {
            String ValidateFormAction = "http://et.airchina.com.cn/InternetBooking/ValidateFormAction.do";
            CCSPostMethod post_ValidateFormAction = new CCSPostMethod(ValidateFormAction);
            post_ValidateFormAction.addRequestHeader("Cookie", cookieString);
            String hiddenFlightSelection = "0," + flightItineraryId;
            //                String flightItineraryId = hiddenFlightSelection.split(",")[1];
            NameValuePair NameValuePair_flightItineraryId = new NameValuePair("flightItineraryId[0]", flightItineraryId);
            NameValuePair NameValuePair_hiddenFlightSelection = new NameValuePair("hiddenFlightSelection",
                    hiddenFlightSelection);
            NameValuePair NameValuePair_markUpMoneyAmount = new NameValuePair("markUpMoneyAmount", "");
            NameValuePair NameValuePair_validateAction = new NameValuePair("validateAction",
                    "AirVerifyFareFamiliesItinerary");
            NameValuePair NameValuePair_vsessionid = new NameValuePair("vsessionid", "");
            NameValuePair[] nameValuePairs_ValidateFormAction = { NameValuePair_flightItineraryId,
                    NameValuePair_hiddenFlightSelection, NameValuePair_markUpMoneyAmount, NameValuePair_validateAction,
                    NameValuePair_vsessionid };
            post_ValidateFormAction.setRequestBody(nameValuePairs_ValidateFormAction);
            int status_post_post_ValidateFormAction = httpClient.executeMethod(post_ValidateFormAction);
            //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            System.out.println("#################################cookieString:577:" + cookieString);
            //                System.out.println("======================status_post_post_ValidateFormAction=2============");
            //                System.out.println(status_post_post_ValidateFormAction);
            String responseBodyString_ValidateFormAction = post_ValidateFormAction.getResponseBodyAsString();
            responseBodyString_ValidateFormAction = getNewResponseBodyString(responseBodyString_ValidateFormAction);
            System.out.println("======================responseBodyString_ValidateFormAction=============");
            System.out.println(responseBodyString_ValidateFormAction);
            if (status_post_post_ValidateFormAction == 200
                    && responseBodyString_ValidateFormAction.indexOf("success") >= 0) {
                toxingchenggaikuoUrl = getUrlToxingchengGaikuo(cookieString, flightItineraryId);
                //                    System.out.println("======================toxingchenggaikuoUrl=============");
                //                    System.out.println(toxingchenggaikuoUrl);
            }
            //            }
        }
        catch (HttpException e1) {
            e1.printStackTrace();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        return toxingchenggaikuoUrl;
    }

    /**
     * 
     * @param cookieString
     * @return
     * @time 2015年7月2日 下午6:06:20
     * @author chendong
     */
    private Cookie[] getCookiesBycookieString(String cookieString) {
        String[] cookie_Strings = cookieString.split(";");
        Cookie[] Cookies = new Cookie[cookie_Strings.length];
        for (int i = 0; i < cookie_Strings.length; i++) {
            String key = "";
            String value = "";
            try {
                value = cookie_Strings[i].split("=")[1];
                key = cookie_Strings[i].split("=")[0];
            }
            catch (Exception e) {
            }
            if (key != null) {
                try {
                    Cookie cookie = new Cookie("/", key.trim(), value.trim());
                    Cookies[i] = cookie;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return Cookies;
    }

    /**
     * 
     * @param cookieString
     * @param passengers
     * @param segments
     * @param param
     * @param rand_code
     * @return
     * @time 2015年7月2日 下午2:14:18
     * @author chendong
     */
    private String ReservationLogin(String cookieString, List<Passenger> passengers, List<Segmentinfo> segments,
            FlightSearch param, String rand_code) {
        //        http://et.airchina.com.cn/InternetBooking/ReservationLogin.do
        String ReservationLogin_url = "http://et.airchina.com.cn/InternetBooking/ReservationLogin.do";
        CCSPostMethod post_ReservationLogin = new CCSPostMethod(ReservationLogin_url);
        NameValuePair NameValuePair_ajaxAction = new NameValuePair("ajaxAction", "true");
        NameValuePair NameValuePair_flowStep = new NameValuePair("usernameType", "USERNAME");
        NameValuePair NameValuePair_sliderSelectedIndex = new NameValuePair("username", username);
        NameValuePair NameValuePair_sliderSelectedIndexes = new NameValuePair("password", password);
        NameValuePair NameValuePair_method = new NameValuePair("isBookingFlowSignIn", "true");
        NameValuePair NameValuePair_insuranceElementId = new NameValuePair("method", "LOGIN_BOOK");
        NameValuePair NameValuePair_insuranceConfirmationCkb = new NameValuePair("flowStep", "ITINERARY_SUMMARY");
        NameValuePair NameValuePair_acceptInsuranceTermsAndConditions = new NameValuePair("sliderSelectedIndex", "");
        NameValuePair NameValuePair_taxElementId = new NameValuePair("sliderSelectedIndexes", "");
        NameValuePair NameValuePair_acceptTaxTermsAndConditions = new NameValuePair("vsessionid", "");
        System.out.println("===createOrder=rand_code==============");
        System.out.println(rand_code);
        String captcha = rand_code;
        NameValuePair NameValuePair_captcha = new NameValuePair("captcha", captcha);
        NameValuePair NameValuePair_vsessionid = new NameValuePair("vsessionid", "");
        NameValuePair[] nameValuePairs_ReservationLogin = { NameValuePair_ajaxAction, NameValuePair_flowStep,
                NameValuePair_sliderSelectedIndex, NameValuePair_sliderSelectedIndexes, NameValuePair_method,
                NameValuePair_insuranceElementId, NameValuePair_insuranceConfirmationCkb,
                NameValuePair_acceptInsuranceTermsAndConditions, NameValuePair_taxElementId,
                NameValuePair_acceptTaxTermsAndConditions, NameValuePair_captcha, NameValuePair_vsessionid };
        post_ReservationLogin.setRequestBody(nameValuePairs_ReservationLogin);
        String ContentLength = Wrapper_CAUtil.getContentLength(nameValuePairs_ReservationLogin);
        post_ReservationLogin.addRequestHeader("Content-Length", ContentLength);
        //        post_ReservationLogin.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        post_ReservationLogin.addRequestHeader("Cookie", cookieString);
        post_ReservationLogin.addRequestHeader("Referer",
                "http://et.airchina.com.cn/InternetBooking/ItinerarySummary.do");
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        int status_post_post_ReservationLogin = 0;
        String responseBodyString_ReservationLogin = "-1";
        try {
            status_post_post_ReservationLogin = httpClient.executeMethod(post_ReservationLogin);
            System.out.println("======================status_post_post_ReservationLogin============");
            System.out.println(status_post_post_ReservationLogin);
            responseBodyString_ReservationLogin = post_ReservationLogin.getResponseBodyAsString();
            responseBodyString_ReservationLogin = getNewResponseBodyString(responseBodyString_ReservationLogin);
            System.out.println("======================responseBodyString_ReservationLogin=============");
            System.out.println(responseBodyString_ReservationLogin);
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return responseBodyString_ReservationLogin;
    }

    /**
     * 打开行程概括的页面
     * @param cookieString
     * @return
     * @time 2015年7月2日 上午11:19:36
     * @author chendong
     * @param httpClient2 
     */
    private String ItinerarySummary(String cookieString) {
        //        cookieString = "JSESSIONID=E144CDE70E79A0EA66FB30E74D62AEFD; BIGipServerWeb_http=1275731628.20480.0000; current_PoS=AIRCHINA_CN; zzzzzzz=551bdea5zzzzzzz_551bdea5; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3D%3B; currentLang=zh_CN";
        System.out.println(cookieString);
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String ResponseBodyAs_ItinerarySummary = "-1";
        String ItinerarySummary_url = "http://et.airchina.com.cn/InternetBooking/ItinerarySummary.do";
        CCSGetMethod get_ItinerarySummary = new CCSGetMethod(ItinerarySummary_url);
        get_ItinerarySummary.setFollowRedirects(false);
        get_ItinerarySummary.addRequestHeader("Cookie", cookieString);
        get_ItinerarySummary.addRequestHeader("Content-Type", "text/html;charset=UTF-8");
        //        Map<String, String> keymapcookie = getMapcookieBycookieString(cookieString);
        //        ResponseBodyAs_ItinerarySummary = SendPostandGet2.submitGet(ItinerarySummary_url, "utf-8", keymapcookie)
        //                .toString();
        //        System.out.println("====================ResponseBodyAs_ItinerarySummary=============");
        //        System.out.println(ResponseBodyAs_ItinerarySummary);

        //        get_ItinerarySummary.addRequestHeader("Accept",
        //                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        //        get_ItinerarySummary.addRequestHeader("Accept-Encoding ", "gzip, deflate");
        //        get_ItinerarySummary.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        //        get_ItinerarySummary.addRequestHeader("Cache-Control", "max-age=0");
        //        get_ItinerarySummary.addRequestHeader("Connection ", "keep-alive");
        //        get_ItinerarySummary.addRequestHeader("Host", "www.airchina.com.cn");
        //        get_ItinerarySummary.addRequestHeader("Referer",
        //                "http://et.airchina.com.cn/InternetBooking/AirFareFamiliesFlexibleForward.do");
        try {
            int status_post_ItinerarySummary = httpClient.executeMethod(get_ItinerarySummary);
            System.out.println("====================cookieString================================");
            System.out.println(cookieString);
            ResponseBodyAs_ItinerarySummary = get_ItinerarySummary.getResponseBodyAsString();
            System.out.println("====================ResponseBodyAs_ItinerarySummary=============");
            if (ResponseBodyAs_ItinerarySummary.indexOf("行程概括") >= 0) {
                System.out.println("行程概括");
            }
            else {
                System.out.println("no_行程概括");
            }
            System.out.println("#################################cookieString:getUrlToxingchengGaikuo:"
                    + Wrapper_CAUtil.getCookieString(httpClient));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseBodyAs_ItinerarySummary;
    }

    /**
     * 
     * @param cookieString
     * @return
     * @time 2015年7月2日 下午12:16:55
     * @author chendong
     */
    private Map<String, String> getMapcookieBycookieString(String cookieString) {
        Map<String, String> keymapcookie = new HashMap<String, String>();
        String[] cookies = cookieString.split(";");
        for (int i = 0; i < cookies.length; i++) {
            String key = "";
            String value = "";
            ;
            try {
                value = cookies[i].split("=")[1];
                key = cookies[i].split("=")[0];
            }
            catch (Exception e) {
            }
            keymapcookie.put(key, value);
        }
        return keymapcookie;
    }

    /**
     * 获取到一个全新的cookie
     * @param httpClient
     * @return
     * @time 2015年7月2日 上午10:10:30
     * @author chendong
     * @param cookieString 
     */
    private CCSHttpClient getNewCookieString(CCSHttpClient httpClient, String cookieString) {
        String index_url = "http://www.airchina.com.cn/cn/index.shtml";
        CCSGetMethod get_index = new CCSGetMethod(index_url);
        get_index.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        get_index.addRequestHeader("Accept-Encoding ", "gzip, deflate");
        get_index.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        get_index.addRequestHeader("Connection ", "keep-alive");
        get_index.addRequestHeader("Host", "www.airchina.com.cn");
        get_index.addRequestHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
        get_index.setRequestHeader("Cookie", cookieString);
        try {
            int status_post_index = httpClient.executeMethod(get_index);
            cookieString = Wrapper_CAUtil.getCookieString(httpClient);
            System.out.println("====================cookieString=getNewCookieString============");
            System.out.println(cookieString);
            String ResponseBodyAs_index = get_index.getResponseBodyAsString();
            //            System.out.println("====================ResponseBodyAs_index=getNewCookieString============");
            //            System.out.println(ResponseBodyAs_index);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return httpClient;
    }

    /**
     * 去下单打码的页面
     * 
     * @param cookieString
     * @param reservationPrepareToCreate
     * @return
     * @time 2015年6月30日 下午5:34:20
     * @author chendong
     * @param param 
     * @param segments 
     * @param passengers 
     */
    private String ReservationPrepareToCreate(String cookieString, List<Passenger> passengers,
            List<Segmentinfo> segments, FlightSearch param, String rand_code, CCSHttpClient httpClient) {
        //        InsuranceCrossSellAddToShoppingCartAction(cookieString);//请求验证保险是否买

        String ReservationPrepareToCreate_url = "http://et.airchina.com.cn/InternetBooking/ReservationPrepareToCreate.do ";
        CCSPostMethod post_ReservationPrepareToCreate = new CCSPostMethod(ReservationPrepareToCreate_url);
        //        http://et.airchina.com.cn/InternetBooking/ReservationPrepareToCreate.do
        //        NameValuePair NameValuePair_ajaxAction = new NameValuePair("ajaxAction", "true");
        NameValuePair NameValuePair_ajaxAction = new NameValuePair("ajaxAction", "true");
        NameValuePair NameValuePair_flowStep = new NameValuePair("flowStep", "ITINERARY_SUMMARY");
        NameValuePair NameValuePair_sliderSelectedIndex = new NameValuePair("sliderSelectedIndex", "");
        NameValuePair NameValuePair_sliderSelectedIndexes = new NameValuePair("sliderSelectedIndexes", "");
        NameValuePair NameValuePair_method = new NameValuePair("method", "BOOK");
        NameValuePair NameValuePair_insuranceElementId = new NameValuePair("insuranceElementId", "noInsurancesRequired");
        NameValuePair NameValuePair_insuranceConfirmationCkb = new NameValuePair("insuranceConfirmationCkb",
                "noConfirmationCheckboxRequired");
        NameValuePair NameValuePair_acceptInsuranceTermsAndConditions = new NameValuePair(
                "acceptInsuranceTermsAndConditions", "true");
        NameValuePair NameValuePair_taxElementId = new NameValuePair("taxElementId", "noTaxRequired");
        NameValuePair NameValuePair_acceptTaxTermsAndConditions = new NameValuePair("acceptTaxTermsAndConditions",
                "true");
        NameValuePair NameValuePair_ancillaryPricesValid = new NameValuePair("ancillaryPricesValid", "true");
        NameValuePair NameValuePair_acceptRepricing = new NameValuePair("acceptRepricing", "true");
        NameValuePair NameValuePair_acceptTermsAndConditions = new NameValuePair("acceptTermsAndConditions", "true");
        //        String TripName = "PEK - SHA [2015/07/13]";
        String TripName = param.getStartAirportCode() + " - " + param.getEndAirportCode() + " ["
                + param.getFromDate().replace("-", "/") + "]";
        NameValuePair NameValuePair_TripName = new NameValuePair("TripName", TripName);
        System.out.println("===createOrder=rand_code==============");
        System.out.println(rand_code);
        String captcha = rand_code;
        NameValuePair NameValuePair_captcha = new NameValuePair("captcha", captcha);
        NameValuePair NameValuePair_vsessionid = new NameValuePair("vsessionid", "");
        NameValuePair[] nameValuePairs_ReservationPrepareToCreate = { NameValuePair_ajaxAction, NameValuePair_flowStep,
                NameValuePair_sliderSelectedIndex, NameValuePair_sliderSelectedIndexes, NameValuePair_method,
                NameValuePair_insuranceElementId, NameValuePair_insuranceConfirmationCkb,
                NameValuePair_acceptInsuranceTermsAndConditions, NameValuePair_taxElementId,
                NameValuePair_acceptTaxTermsAndConditions, NameValuePair_ancillaryPricesValid,
                NameValuePair_acceptRepricing, NameValuePair_acceptTermsAndConditions, NameValuePair_TripName,
                NameValuePair_captcha, NameValuePair_vsessionid };
        post_ReservationPrepareToCreate.setRequestBody(nameValuePairs_ReservationPrepareToCreate);
        String ContentLength = Wrapper_CAUtil.getContentLength(nameValuePairs_ReservationPrepareToCreate);
        //        ContentLength = "467";
        post_ReservationPrepareToCreate.addRequestHeader("Content-Length", ContentLength);
        post_ReservationPrepareToCreate.addRequestHeader("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");

        post_ReservationPrepareToCreate.addRequestHeader("Cookie", cookieString);
        post_ReservationPrepareToCreate.addRequestHeader("Referer",
                "http://et.airchina.com.cn/InternetBooking/ItinerarySummary.do");
        post_ReservationPrepareToCreate.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        int status_post_post_ReservationPrepareToCreate;
        try {
            status_post_post_ReservationPrepareToCreate = httpClient.executeMethod(post_ReservationPrepareToCreate);
            //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            System.out.println("======================status_post_post_ReservationPrepareToCreate============");
            System.out.println(status_post_post_ReservationPrepareToCreate);
            String responseBodyString_ReservationPrepareToCreate = post_ReservationPrepareToCreate
                    .getResponseBodyAsString();
            responseBodyString_ReservationPrepareToCreate = getNewResponseBodyString(responseBodyString_ReservationPrepareToCreate);
            System.out.println("======================responseBodyString_ReservationPrepareToCreate=============");
            System.out.println(responseBodyString_ReservationPrepareToCreate);
        }
        catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * @param cookieString
     * @param passengers
     * @param segments
     * @param param
     * @time 2015年7月1日 下午5:29:12
     * @author chendong
     * @return 
     */
    private String ValidateFormAction(String cookieString, List<Passenger> passengers, List<Segmentinfo> segments,
            FlightSearch param) {
        String validateflow = ValidateFlow(cookieString);
        validateflow = getNewResponseBodyString(validateflow);
        String ShoppingCartCheck = ShoppingCartCheck(cookieString);
        ShoppingCartCheck = getNewResponseBodyString(ShoppingCartCheck);
        String ReservationCheckLogin = ReservationCheckLogin(cookieString);
        ReservationCheckLogin = getNewResponseBodyString(ReservationCheckLogin);
        String ValidateFormAction_url = "http://et.airchina.com.cn/InternetBooking/ValidateFormAction.do";
        CCSPostMethod post_ValidateFormAction = new CCSPostMethod(ValidateFormAction_url);
        NameValuePair NameValuePair_ajaxAction = new NameValuePair("validateAction", "ReservationPrepareToCreate");
        NameValuePair NameValuePair_flowStep = new NameValuePair("flowStep", "ITINERARY_SUMMARY");
        NameValuePair NameValuePair_sliderSelectedIndex = new NameValuePair("sliderSelectedIndex", "");
        NameValuePair NameValuePair_sliderSelectedIndexes = new NameValuePair("sliderSelectedIndexes", "");
        NameValuePair NameValuePair_method = new NameValuePair("method", "BOOK");
        NameValuePair NameValuePair_insuranceElementId = new NameValuePair("insuranceElementId", "noInsurancesRequired");
        NameValuePair NameValuePair_insuranceConfirmationCkb = new NameValuePair("insuranceConfirmationCkb",
                "noConfirmationCheckboxRequired");
        NameValuePair NameValuePair_acceptInsuranceTermsAndConditions = new NameValuePair(
                "acceptInsuranceTermsAndConditions", "true");
        NameValuePair NameValuePair_taxElementId = new NameValuePair("taxElementId", "noTaxRequired");
        NameValuePair NameValuePair_acceptTaxTermsAndConditions = new NameValuePair("acceptTaxTermsAndConditions",
                "true");
        NameValuePair NameValuePair_ancillaryPricesValid = new NameValuePair("ancillaryPricesValid", "true");
        NameValuePair NameValuePair_acceptRepricing = new NameValuePair("acceptRepricing", "true");
        NameValuePair NameValuePair_acceptTermsAndConditions = new NameValuePair("acceptTermsAndConditions", "true");
        //        String TripName = "PEK - SHA [2015/07/13]";
        String TripName = param.getStartAirportCode() + "%20-%20" + param.getEndAirportCode() + "%20%5B"
                + param.getFromDate().replace("-", "%2F") + "%5D";
        try {
            //            TripName = URLEncoder.encode(TripName, "utf-8");
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        NameValuePair NameValuePair_TripName = new NameValuePair("TripName", TripName);
        //开始打码
        String url_tupian_path = "http://et.airchina.com.cn/InternetBooking/GenerateCaptcha.do?noCache="
                + System.currentTimeMillis();
        String picturepath = Wrapper_CAUtil.downloadimgbyhttpclient(url_tupian_path, cookieString, dirPath);
        System.out.println("====picturepath==============");
        System.out.println(picturepath);
        //        DaMaCommon dmc = WrapperUtil.getcheckcodebydama(picturepath, 0);//验证码打码
        //        String rand_code = dmc.getResult().toUpperCase();
        //打码结束
        String rand_code = SendPostandGet.submitGet("http://localhost:9004/cn_home/dama.html");//验证码打码
        System.out.println("===createOrder=rand_code==============");
        System.out.println(rand_code);
        NameValuePair NameValuePair_captcha = new NameValuePair("captcha", rand_code);
        NameValuePair NameValuePair_vsessionid = new NameValuePair("vsessionid", "");
        NameValuePair[] nameValuePairs_ValidateFormAction = { NameValuePair_ajaxAction, NameValuePair_flowStep,
                NameValuePair_sliderSelectedIndex, NameValuePair_sliderSelectedIndexes, NameValuePair_method,
                NameValuePair_insuranceElementId, NameValuePair_insuranceConfirmationCkb,
                NameValuePair_acceptInsuranceTermsAndConditions, NameValuePair_taxElementId,
                NameValuePair_acceptTaxTermsAndConditions, NameValuePair_ancillaryPricesValid,
                NameValuePair_acceptRepricing, NameValuePair_acceptTermsAndConditions, NameValuePair_TripName,
                NameValuePair_captcha, NameValuePair_vsessionid };
        post_ValidateFormAction.setRequestBody(nameValuePairs_ValidateFormAction);
        String ContentLength = Wrapper_CAUtil.getContentLength(nameValuePairs_ValidateFormAction);
        post_ValidateFormAction.addRequestHeader("Content-Length", ContentLength);
        post_ValidateFormAction.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        post_ValidateFormAction.addRequestHeader("Cookie", cookieString);
        post_ValidateFormAction.addRequestHeader("Referer",
                "http://et.airchina.com.cn/InternetBooking/ItinerarySummary.do");
        post_ValidateFormAction.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        int status_post_post_ReservationPrepareToCreate;
        try {
            status_post_post_ReservationPrepareToCreate = httpClient.executeMethod(post_ValidateFormAction);
            //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            System.out.println("======================status_post_post_ReservationPrepareToCreate============");
            System.out.println(status_post_post_ReservationPrepareToCreate);
            String responseBodyString_ReservationPrepareToCreate = post_ValidateFormAction.getResponseBodyAsString();
            responseBodyString_ReservationPrepareToCreate = getNewResponseBodyString(responseBodyString_ReservationPrepareToCreate);
            System.out.println("======================responseBodyString_ReservationPrepareToCreate=============");
            System.out.println(responseBodyString_ReservationPrepareToCreate);
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return rand_code;
    }

    /**
     * 
     * @param cookieString
     * @param passengers
     * @param segments
     * @param param
     * @time 2015年7月1日 下午5:29:12
     * @author chendong
     * @return 
     */
    private String ValidateFormActionLogin(String cookieString, List<Passenger> passengers, List<Segmentinfo> segments,
            FlightSearch param) {
        String rand_code = getrand_code(cookieString);

        //        String ReservationCheckLogin = ReservationCheckLogin(cookieString);
        //        ReservationCheckLogin = getNewResponseBodyString(ReservationCheckLogin);
        String ValidateFormAction1 = ValidateFormAction1(cookieString, passengers, segments, param, rand_code);
        String ValidateFormAction2 = ValidateFormAction2(cookieString, passengers, segments, param, rand_code);
        if (ValidateFormAction1.indexOf("success") >= 0 && ValidateFormAction2.indexOf("success") >= 0) {
        }
        else {
            rand_code = "XXXX";
        }
        return rand_code;
    }

    /**
     * 
     * @param cookieString
     * @param passengers
     * @param segments
     * @param param
     * @param rand_code
     * @return
     * @time 2015年7月2日 下午4:30:48
     * @author chendong
     */
    private String ValidateFormActionAddPassenger(String cookieString, List<Passenger> passengers,
            List<Segmentinfo> segments, FlightSearch param, String rand_code) {
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String ValidateFormAction_url = "http://et.airchina.com.cn/InternetBooking/ValidateFormAction.do";
        CCSPostMethod post_ValidateFormAction = new CCSPostMethod(ValidateFormAction_url);
        post_ValidateFormAction.setFollowRedirects(false);
        post_ValidateFormAction.addRequestHeader("Cookie", cookieString);

        NameValuePair NameValuePair_validateAction = new NameValuePair("validateAction", "UpdateReservation");
        NameValuePair NameValuePair_usernameType = new NameValuePair("operation", "PAYMENT");
        NameValuePair NameValuePair_username = new NameValuePair("travellersCount", passengers.size() + "");
        NameValuePair NameValuePair_password = new NameValuePair("travelArranger.lastName", travelArranger_lastName);
        NameValuePair NameValuePair_isBookingFlowSignIn = new NameValuePair("travelArranger.firstName",
                travelArranger_firstName);

        NameValuePair NameValuePair_method = new NameValuePair("travellersInfo[0].emailAddress", "LOGIN_BOOK");
        NameValuePair NameValuePair_captcha = new NameValuePair("travellersInfo[0].mobilePhone.phoneNumber",
                mobilePhonePhoneNumber);
        NameValuePair NameValuePair_flowStep = new NameValuePair("travellersInfo[0].homePhone.phoneNumber",
                homePhonePhoneNumber);
        NameValuePair NameValuePair_sliderSelectedIndex = new NameValuePair("acceptTermsAndConditions", "true");
        NameValuePair NameValuePair_vsessionid = new NameValuePair("vsessionid", "");

        NameValuePair NameValuePair_sliderSelectedIndexes = new NameValuePair("sliderSelectedIndexes", "");
        NameValuePair[] nameValuePairs_ValidateFormAction = { NameValuePair_usernameType,
                NameValuePair_isBookingFlowSignIn, NameValuePair_flowStep, NameValuePair_username,
                NameValuePair_password, NameValuePair_method, NameValuePair_captcha, NameValuePair_sliderSelectedIndex,
                NameValuePair_sliderSelectedIndexes, NameValuePair_validateAction, NameValuePair_vsessionid };
        post_ValidateFormAction.setRequestBody(nameValuePairs_ValidateFormAction);
        //        post_ValidateFormAction.addRequestHeader("Content-Length",
        //                Wrapper_CAUtil.getContentLength(nameValuePairs_ValidateFormAction));
        int status_post_post_ReservationPrepareToCreate = 0;
        String responseBodyString_ReservationPrepareToCreate = "-1";
        try {
            status_post_post_ReservationPrepareToCreate = httpClient.executeMethod(post_ValidateFormAction);
            //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            System.out.println("======================status_post_post_ValidateFormAction2============");
            System.out.println(status_post_post_ReservationPrepareToCreate);
            responseBodyString_ReservationPrepareToCreate = post_ValidateFormAction.getResponseBodyAsString();
            responseBodyString_ReservationPrepareToCreate = getNewResponseBodyString(responseBodyString_ReservationPrepareToCreate);
            System.out.println("======================responseBodyString_ValidateFormAction2=============");
            System.out.println(responseBodyString_ReservationPrepareToCreate);
            if (status_post_post_ReservationPrepareToCreate == 302) {
                String Location = "http://et.airchina.com.cn"
                        + Wrapper_CAUtil.get302Location(post_ValidateFormAction.getResponseHeaders());
                CCSGetMethod get_Location = new CCSGetMethod(ValidateFormAction_url);
                post_ValidateFormAction.setFollowRedirects(false);
                post_ValidateFormAction.addRequestHeader("Cookie", cookieString);
                int status_get_Location = httpClient.executeMethod(get_Location);
                System.out.println("======================status_get_Location============");
                System.out.println(status_get_Location);
                String responseBodyString_get_Location = get_Location.getResponseBodyAsString();
                System.out.println("======================responseBodyString_get_Location=============");
                System.out.println(responseBodyString_get_Location);
            }
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return responseBodyString_ReservationPrepareToCreate;
    }

    /**
     * 
     * @param cookieString
     * @param passengers
     * @param segments
     * @param param
     * @param rand_code
     * @return
     * @time 2015年7月2日 下午4:30:48
     * @author chendong
     */
    private String ValidateFormAction2(String cookieString, List<Passenger> passengers, List<Segmentinfo> segments,
            FlightSearch param, String rand_code) {
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String ValidateFormAction_url = "http://et.airchina.com.cn/InternetBooking/ValidateFormAction.do";
        CCSPostMethod post_ValidateFormAction = new CCSPostMethod(ValidateFormAction_url);
        post_ValidateFormAction.setFollowRedirects(false);
        post_ValidateFormAction.addRequestHeader("Cookie", cookieString);
        NameValuePair NameValuePair_validateAction = new NameValuePair("validateAction", "ReservationLogin");
        NameValuePair NameValuePair_usernameType = new NameValuePair("usernameType", "USERNAME");
        NameValuePair NameValuePair_username = new NameValuePair("username", username);
        NameValuePair NameValuePair_password = new NameValuePair("password", password);
        NameValuePair NameValuePair_isBookingFlowSignIn = new NameValuePair("isBookingFlowSignIn", "true");
        NameValuePair NameValuePair_method = new NameValuePair("method", "LOGIN_BOOK");
        NameValuePair NameValuePair_captcha = new NameValuePair("captcha", rand_code);
        NameValuePair NameValuePair_flowStep = new NameValuePair("flowStep", "ITINERARY_SUMMARY");
        NameValuePair NameValuePair_sliderSelectedIndex = new NameValuePair("sliderSelectedIndex", "");
        NameValuePair NameValuePair_sliderSelectedIndexes = new NameValuePair("sliderSelectedIndexes", "");
        NameValuePair NameValuePair_vsessionid = new NameValuePair("vsessionid", "");
        NameValuePair[] nameValuePairs_ValidateFormAction = { NameValuePair_usernameType,
                NameValuePair_isBookingFlowSignIn, NameValuePair_flowStep, NameValuePair_username,
                NameValuePair_password, NameValuePair_method, NameValuePair_captcha, NameValuePair_sliderSelectedIndex,
                NameValuePair_sliderSelectedIndexes, NameValuePair_validateAction, NameValuePair_vsessionid };
        post_ValidateFormAction.setRequestBody(nameValuePairs_ValidateFormAction);
        //        post_ValidateFormAction.addRequestHeader("Content-Length",
        //                Wrapper_CAUtil.getContentLength(nameValuePairs_ValidateFormAction));
        int status_post_post_ReservationPrepareToCreate = 0;
        String responseBodyString_ReservationPrepareToCreate = "-1";
        try {
            status_post_post_ReservationPrepareToCreate = httpClient.executeMethod(post_ValidateFormAction);
            //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            System.out.println("======================status_post_post_ValidateFormAction2============");
            System.out.println(status_post_post_ReservationPrepareToCreate);
            responseBodyString_ReservationPrepareToCreate = post_ValidateFormAction.getResponseBodyAsString();
            responseBodyString_ReservationPrepareToCreate = getNewResponseBodyString(responseBodyString_ReservationPrepareToCreate);
            System.out.println("======================responseBodyString_ValidateFormAction2=============");
            System.out.println(responseBodyString_ReservationPrepareToCreate);
            if (status_post_post_ReservationPrepareToCreate == 302) {
                String Location = "http://et.airchina.com.cn"
                        + Wrapper_CAUtil.get302Location(post_ValidateFormAction.getResponseHeaders());
                CCSGetMethod get_Location = new CCSGetMethod(ValidateFormAction_url);
                post_ValidateFormAction.setFollowRedirects(false);
                post_ValidateFormAction.addRequestHeader("Cookie", cookieString);
                int status_get_Location = httpClient.executeMethod(get_Location);
                System.out.println("======================status_get_Location============");
                System.out.println(status_get_Location);
                String responseBodyString_get_Location = get_Location.getResponseBodyAsString();
                System.out.println("======================responseBodyString_get_Location=============");
                System.out.println(responseBodyString_get_Location);
            }
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return responseBodyString_ReservationPrepareToCreate;
    }

    /**
     * 
     * @param cookieString
     * @param passengers
     * @param segments
     * @param param
     * @return
     * @time 2015年7月2日 下午4:19:45
     * @author chendong
     * @param rand_code 
     * @param httpClient2 
     */
    private String ValidateFormAction1(String cookieString, List<Passenger> passengers, List<Segmentinfo> segments,
            FlightSearch param, String rand_code) {
        String validateflow = getNewResponseBodyString(ValidateFlow(cookieString));
        System.out.println("--------------------------------------------validateflow--------");
        System.out.println(validateflow);
        String ShoppingCartCheck = getNewResponseBodyString(ShoppingCartCheck(cookieString));
        System.out.println("--------------------------------------------ShoppingCartCheck--------");
        System.out.println(ShoppingCartCheck);
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String ValidateFormAction_url = "http://et.airchina.com.cn/InternetBooking/ValidateFormAction.do";
        CCSPostMethod post_ValidateFormAction = new CCSPostMethod(ValidateFormAction_url);
        post_ValidateFormAction.setFollowRedirects(false);
        post_ValidateFormAction.addRequestHeader("Cookie", cookieString);
        //        post_ValidateFormAction.addRequestHeader("Referer",
        //                "http://et.airchina.com.cn/InternetBooking/ItinerarySummary.do");
        //        post_ValidateFormAction.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

        NameValuePair NameValuePair_username = new NameValuePair("idUserName", username);
        NameValuePair NameValuePair_password = new NameValuePair("idPassword", password);
        NameValuePair NameValuePair_method = new NameValuePair("method", "LOGIN_BOOK");
        NameValuePair NameValuePair_sliderSelectedIndex = new NameValuePair("sliderSelectedIndex", "");
        NameValuePair NameValuePair_sliderSelectedIndexes = new NameValuePair("sliderSelectedIndexes", "");
        //        String TripName = param.getStartAirportCode() + "%20-%20" + param.getEndAirportCode() + "%20%5B"
        //                + param.getFromDate().replace("-", "%2F") + "%5D";
        String TripName = param.getStartAirportCode() + " - " + param.getEndAirportCode() + " ["
                + param.getFromDate().replace("-", "/") + "]";
        try {
            //            TripName = URLEncoder.encode(TripName, "utf-8");
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        NameValuePair NameValuePair_TripName = new NameValuePair("TripName", TripName);

        NameValuePair NameValuePair_flowStep = new NameValuePair("flowStep", "ITINERARY_SUMMARY");
        NameValuePair NameValuePair_acceptInsuranceTermsAndConditions = new NameValuePair(
                "acceptInsuranceTermsAndConditions", "true");
        NameValuePair NameValuePair_acceptRepricing = new NameValuePair("acceptRepricing", "true");
        NameValuePair NameValuePair_acceptTaxTermsAndConditions = new NameValuePair("acceptTaxTermsAndConditions",
                "true");
        NameValuePair NameValuePair_captcha = new NameValuePair("captcha", rand_code);
        NameValuePair NameValuePair_acceptTermsAndConditions = new NameValuePair("acceptTermsAndConditions", "true");
        NameValuePair NameValuePair_ancillaryPricesValid = new NameValuePair("ancillaryPricesValid", "true");
        NameValuePair NameValuePair_insuranceConfirmationCkb = new NameValuePair("insuranceConfirmationCkb",
                "noConfirmationCheckboxRequired");
        NameValuePair NameValuePair_insuranceElementId = new NameValuePair("insuranceElementId", "noInsurancesRequired");
        NameValuePair NameValuePair_radiogr1_f1 = new NameValuePair("radiogr1-f1", "on");
        NameValuePair NameValuePair_taxElementId = new NameValuePair("taxElementId", "noTaxRequired");
        NameValuePair NameValuePair_validateAction = new NameValuePair("validateAction",
                "ReservationInlineLoginForward");
        NameValuePair NameValuePair_vsessionid = new NameValuePair("vsessionid", "");
        NameValuePair[] nameValuePairs_ValidateFormAction = { NameValuePair_TripName,
                NameValuePair_acceptInsuranceTermsAndConditions, NameValuePair_acceptRepricing,
                NameValuePair_acceptTaxTermsAndConditions, NameValuePair_acceptTermsAndConditions,
                NameValuePair_ancillaryPricesValid, NameValuePair_captcha, NameValuePair_flowStep,
                NameValuePair_username, NameValuePair_password, NameValuePair_insuranceConfirmationCkb,
                NameValuePair_insuranceElementId, NameValuePair_method, NameValuePair_radiogr1_f1,
                NameValuePair_sliderSelectedIndex, NameValuePair_sliderSelectedIndexes, NameValuePair_taxElementId,
                NameValuePair_validateAction, NameValuePair_vsessionid };
        post_ValidateFormAction.setRequestBody(nameValuePairs_ValidateFormAction);
        //        post_ValidateFormAction.addRequestHeader("Content-Length",
        //                Wrapper_CAUtil.getContentLength(nameValuePairs_ValidateFormAction));
        int status_post_post_ReservationPrepareToCreate = 0;
        String responseBodyString_ReservationPrepareToCreate = "-1";
        try {
            status_post_post_ReservationPrepareToCreate = httpClient.executeMethod(post_ValidateFormAction);
            //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            System.out.println("======================status_post_post_ValidateFormActionLogin============");
            System.out.println(status_post_post_ReservationPrepareToCreate);
            responseBodyString_ReservationPrepareToCreate = post_ValidateFormAction.getResponseBodyAsString();
            responseBodyString_ReservationPrepareToCreate = getNewResponseBodyString(responseBodyString_ReservationPrepareToCreate);
            System.out.println("======================responseBodyString_ValidateFormActionLogin=============");
            System.out.println(responseBodyString_ReservationPrepareToCreate);
            if (status_post_post_ReservationPrepareToCreate == 302) {
                String Location = "http://et.airchina.com.cn"
                        + Wrapper_CAUtil.get302Location(post_ValidateFormAction.getResponseHeaders());
                CCSGetMethod get_Location = new CCSGetMethod(ValidateFormAction_url);
                post_ValidateFormAction.setFollowRedirects(false);
                post_ValidateFormAction.addRequestHeader("Cookie", cookieString);
                int status_get_Location = httpClient.executeMethod(get_Location);
                System.out.println("======================status_get_Location============");
                System.out.println(status_get_Location);
                String responseBodyString_get_Location = get_Location.getResponseBodyAsString();
                System.out.println("======================responseBodyString_get_Location=============");
                System.out.println(responseBodyString_get_Location);
            }
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return responseBodyString_ReservationPrepareToCreate;
    }

    /**
     * 
     * @param cookieString
     * @return
     * @time 2015年7月2日 下午4:02:21
     * @author chendong
     */
    private String getrand_code(String cookieString) {
        //开始打码
        String url_tupian_path = "http://et.airchina.com.cn/InternetBooking/GenerateCaptcha.do?noCache="
                + System.currentTimeMillis();
        String picturepath = Wrapper_CAUtil.downloadimgbyhttpclient(url_tupian_path, cookieString, dirPath);
        System.out.println("====picturepath==============");
        System.out.println(picturepath);
        //验证码打码
        DaMaCommon dmc = WrapperUtil.getcheckcodebydama(picturepath, 0);
        String rand_code = dmc.getResult().toUpperCase();
        //打码结束
        //        String rand_code = SendPostandGet.submitGet("http://localhost:9004/cn_home/dama.html");//验证码打码
        System.out.println("===createOrder=rand_code==============");
        System.out.println(rand_code);
        return rand_code;
    }

    /**
     * 选择是否购买保险
     * @time 2015年7月1日 下午12:31:53
     * @author chendong
     * @param cookieString 
     */
    private void InsuranceCrossSellAddToShoppingCartAction(String cookieString) {
        String reuslt_InsuranceCrossSellAddToShoppingCartAction = "";
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        CCSPostMethod post_InsuranceCrossSellAddToShoppingCartAction = new CCSPostMethod(
                "http://et.airchina.com.cn/InternetBooking/InsuranceCrossSellAddToShoppingCartAction.do?insuranceSelection=-850983883:false&flowStep=ITINERARY_SUMMARY");
        post_InsuranceCrossSellAddToShoppingCartAction.setRequestHeader("Cookie", cookieString);
        NameValuePair NameValuePair_ajaxAction = new NameValuePair("ajaxAction", "true");
        NameValuePair NameValuePair_alignment = new NameValuePair("alignment", "horizontal");
        NameValuePair NameValuePair_context = new NameValuePair("context", "shoppingCartReservation");
        NameValuePair NameValuePair_elementId = new NameValuePair("elementId", "-850983883");
        NameValuePair NameValuePair_flowStep = new NameValuePair("flowStep", "ITINERARY_SUMMARY");
        NameValuePair[] nameValuePairs_InsuranceCrossSellAddToShoppingCartAction = { NameValuePair_ajaxAction,
                NameValuePair_alignment, NameValuePair_context, NameValuePair_elementId, NameValuePair_flowStep };
        post_InsuranceCrossSellAddToShoppingCartAction
                .setRequestBody(nameValuePairs_InsuranceCrossSellAddToShoppingCartAction);
        try {
            int status_post_InsuranceCrossSellAddToShoppingCartAction = httpClient
                    .executeMethod(post_InsuranceCrossSellAddToShoppingCartAction);
            //            String cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            if (status_post_InsuranceCrossSellAddToShoppingCartAction == 302) {
                status_post_InsuranceCrossSellAddToShoppingCartAction = httpClient
                        .executeMethod(post_InsuranceCrossSellAddToShoppingCartAction);
                //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            }
            //            System.out.println("======================status_post_InsuranceCrossSellAddToShoppingCartAction=1============");
            //            System.out.println(status_post_InsuranceCrossSellAddToShoppingCartAction);
            System.out
                    .println("====================getCookieString=status_post_InsuranceCrossSellAddToShoppingCartAction============");
            System.out.println(Wrapper_CAUtil.getCookieString(httpClient));
            reuslt_InsuranceCrossSellAddToShoppingCartAction = post_InsuranceCrossSellAddToShoppingCartAction
                    .getResponseBodyAsString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //        return reuslt_InsuranceCrossSellAddToShoppingCartAction;

    }

    /**
     * 选择一个价格异步请求数据
     * @param cookieString
     * @time 2015年6月30日 下午5:17:45
     * @author chendong
     * @param flightItineraryId 
     */
    private void doAirSelectOWCFlight(String cookieString, String flightItineraryId) {
        //        JSESSIONID=E5386FA4F611AC62136A58BA5F9C949C; userName=Y2QxOTg5OTI5; masterSessionId=NDI1MTcxMDA4MjEw; currentLang=zh_CN; qqqqqqq=e5772375qqqqqqq_e5772375; s_sess= s_cc=true; s_sq=;
        //        cookieString = "loginCookie=username=13522543333~rememberMe=false~lastname=chen~firstname=dong~; JSESSIONID=AAE17103A011A95E5C887FF5EE00FCC3; current_PoS=AIRCHINA_CN; currentLang=zh_CN;_pzfxuvpc=1434014136310%7C2528387236714191596%7C57%7C1435805718942%7C33%7C1084944275135268591%7C1164843880857386109; _pzfxsfc=; BIGipServerWeb_http=1460281004.20480.0000; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3D%3B; vvvvvvv=7dd179ccvvvvvvv_7dd179cc; mbox=session#1435802097421-902422#1435807588|check#true#1435805788; _pzfxsvpc=1164843880857386109%7C1435805718932%7C1%7Chttp%3A%2F%2Fet.airchina.com.cn%2FInternetBooking%2FItinerarySummary.do";
        //        JSESSIONID=F6C7C68A712B47E7EB07A369F50E9B0B; BIGipServerWeb_http=1275731628.20480.0000; current_PoS=AIRCHINA_CN; mbox=check#true#1435806202|session#1435806123786-544910#1435808002; currentLang=zh_CN; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3D%3B
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String AirSelectOWCFlight = "http://et.airchina.com.cn/InternetBooking/AirSelectOWCFlight.do";
        CCSPostMethod post_AirSelectOWCFlight = new CCSPostMethod(AirSelectOWCFlight);
        post_AirSelectOWCFlight.addRequestHeader("Cookie", cookieString);
        post_AirSelectOWCFlight.addRequestHeader("Host", "et.airchina.com.cn");
        //        post_AirSelectOWCFlight.addRequestHeader("User-Agent",
        //                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
        post_AirSelectOWCFlight.addRequestHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post_AirSelectOWCFlight.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        post_AirSelectOWCFlight.addRequestHeader("Accept-Encoding", "gzip, deflate");
        post_AirSelectOWCFlight.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post_AirSelectOWCFlight.addRequestHeader("Connection", "keep-alive");
        post_AirSelectOWCFlight.addRequestHeader("Pragma", "no-cache");
        post_AirSelectOWCFlight.addRequestHeader("Cache-Control", "no-cache");
        post_AirSelectOWCFlight.addRequestHeader("Referer",
                "http://et.airchina.com.cn/InternetBooking/AirFareFamiliesFlexibleForward.do");
        String hiddenFlightSelection = "0," + flightItineraryId;
        NameValuePair NameValuePair_isFareFamilySearchResult = new NameValuePair("isFareFamilySearchResult", "true");
        NameValuePair NameValuePair_hiddenFlightSelection = new NameValuePair("selectedItineraries",
                hiddenFlightSelection);
        NameValuePair NameValuePair_selectedFlightIds = new NameValuePair("selectedFlightIds", hiddenFlightSelection);
        NameValuePair NameValuePair_combinabilityReloadRequired = new NameValuePair("combinabilityReloadRequired",
                "false");
        NameValuePair NameValuePair_flightIndex = new NameValuePair("flightIndex", "");
        NameValuePair NameValuePair_flowStep = new NameValuePair("flowStep",
                "AIR_COMBINABLE_FARE_FAMILIES_FLEXIBLE_SEARCH_RESULTS");
        NameValuePair NameValuePair_alignment = new NameValuePair("alignment", "horizontal");
        NameValuePair NameValuePair_context = new NameValuePair("context", "airSelection");
        NameValuePair[] nameValuePairs_AirSelectOWCFlight = { NameValuePair_isFareFamilySearchResult,
                NameValuePair_hiddenFlightSelection, NameValuePair_selectedFlightIds,
                NameValuePair_combinabilityReloadRequired, NameValuePair_flightIndex, NameValuePair_flowStep,
                NameValuePair_alignment, NameValuePair_context };
        post_AirSelectOWCFlight.setRequestBody(nameValuePairs_AirSelectOWCFlight);
        post_AirSelectOWCFlight.addRequestHeader("Content-Length",
                Wrapper_CAUtil.getContentLength(nameValuePairs_AirSelectOWCFlight));
        int status_post_post_AirSelectOWCFlight;
        try {
            System.out.println("======================getCookieString============");
            System.out.println(Wrapper_CAUtil.getCookieString(httpClient));
            status_post_post_AirSelectOWCFlight = httpClient.executeMethod(post_AirSelectOWCFlight);
            System.out.println("======================status_post_post_AirSelectOWCFlight=2============");
            System.out.println(status_post_post_AirSelectOWCFlight);
            String responseBodyString_AirSelectOWCFlight = post_AirSelectOWCFlight.getResponseBodyAsString();
            responseBodyString_AirSelectOWCFlight = getNewResponseBodyString(responseBodyString_AirSelectOWCFlight);
            System.out.println("======================responseBodyString_AirSelectOWCFlight=============");
            System.out.println(responseBodyString_AirSelectOWCFlight);
            if (status_post_post_AirSelectOWCFlight == 200
                    && responseBodyString_AirSelectOWCFlight.indexOf("success") >= 0) {
            }
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("#################################cookieString:doAirSelectOWCFlight:"
                + Wrapper_CAUtil.getCookieString(httpClient));
    }

    /**
     * 
     * @param cookieString
     * @return
     * @time 2015年6月26日 下午4:34:38
     * @author Administrator
     */
    private String ReservationCheckLogin(String cookieString) {
        String reuslt_ReservationCheckLogin = "";
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String ReservationCheckLogin = "http://et.airchina.com.cn/InternetBooking/ReservationCheckLogin.do?noCache="
                + System.currentTimeMillis();
        CCSGetMethod get_ReservationCheckLogin = new CCSGetMethod(ReservationCheckLogin);
        get_ReservationCheckLogin.setRequestHeader("Cookie", cookieString);
        try {
            int status_post_ReservationCheckLogin = httpClient.executeMethod(get_ReservationCheckLogin);
            System.out.println("====================getCookieString=status_post_ReservationCheckLogin============");
            System.out.println(Wrapper_CAUtil.getCookieString(httpClient));
            reuslt_ReservationCheckLogin = get_ReservationCheckLogin.getResponseBodyAsString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return reuslt_ReservationCheckLogin;
    }

    /**
     * 
     * @param cookieString
     * @return
     * @time 2015年6月26日 下午4:34:38
     * @author Administrator
     */
    private String ShoppingCartCheck(String cookieString) {
        String reuslt_ShoppingCartCheck = "";
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        //航班选择开始
        String ShoppingCartCheck = "http://et.airchina.com.cn/InternetBooking/ShoppingCartCheck.do";
        //航班选择开始
        CCSPostMethod post_ShoppingCartCheck = new CCSPostMethod(ShoppingCartCheck);
        post_ShoppingCartCheck.setRequestHeader("Cookie", cookieString);
        NameValuePair NameValuePair_amount = new NameValuePair("amount", "");
        NameValuePair NameValuePair_domain = new NameValuePair("domain", "");
        NameValuePair[] nameValuePairs_ShoppingCartCheck = { NameValuePair_amount, NameValuePair_domain };
        post_ShoppingCartCheck.setRequestBody(nameValuePairs_ShoppingCartCheck);
        try {
            int status_post_ShoppingCartCheck = httpClient.executeMethod(post_ShoppingCartCheck);
            //            String cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            if (status_post_ShoppingCartCheck == 302) {
                status_post_ShoppingCartCheck = httpClient.executeMethod(post_ShoppingCartCheck);
                //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            }
            //            System.out.println("======================status_post_ShoppingCartCheck=1============");
            //            System.out.println(status_post_ShoppingCartCheck);
            System.out.println("====================getCookieString=status_post_ShoppingCartCheck============");
            System.out.println(Wrapper_CAUtil.getCookieString(httpClient));
            reuslt_ShoppingCartCheck = post_ShoppingCartCheck.getResponseBodyAsString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return reuslt_ShoppingCartCheck;
    }

    /**
     * 
     * @param cookieString
     * @return
     * @time 2015年6月26日 下午4:34:38
     * @author Administrator
     */
    private String ValidateFlow(String cookieString) {
        String reuslt_ValidateFlow = "";
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        //航班选择开始
        String ValidateFlow = "http://et.airchina.com.cn/InternetBooking/ValidateFlow.do";
        //        /InternetBooking/ValidateFlow.do
        //航班选择开始
        CCSPostMethod post_ValidateFlow = new CCSPostMethod(ValidateFlow);
        post_ValidateFlow.setRequestHeader("Cookie", cookieString);
        post_ValidateFlow.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        NameValuePair NameValuePair_validator = new NameValuePair("validator", "SHOPPING_ON_SUBMIT");
        NameValuePair[] nameValuePairs_ValidateFlow = { NameValuePair_validator };
        post_ValidateFlow.setRequestBody(nameValuePairs_ValidateFlow);
        try {
            int status_post_ValidateFlow = httpClient.executeMethod(post_ValidateFlow);
            //            String cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            if (status_post_ValidateFlow == 302) {
                status_post_ValidateFlow = httpClient.executeMethod(post_ValidateFlow);
                //                cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
            }
            //            System.out.println("======================status_post_ValidateFlow=1============");
            //            System.out.println(status_post_ValidateFlow);
            System.out.println("====================getCookieString=status_post_ValidateFlow============");
            System.out.println(Wrapper_CAUtil.getCookieString(httpClient));
            reuslt_ValidateFlow = post_ValidateFlow.getResponseBodyAsString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return reuslt_ValidateFlow;
    }

    private String getUrlToxingchengGaikuo(String cookiestring, String flightItineraryId2) throws IOException {
        Wrapper_CAHttpClient httpClient = new Wrapper_CAHttpClient(false, 60000L);

        //        String airVerifyPrice = "http://static.airchina.wscdns.com/InternetBooking/zh_CN/pages/tdp/interstitials/airVerifyPrice.html?version=201505131931";
        //        CCSGetMethod get_AirVerifyFareFamiliesItinerary = new CCSGetMethod(airVerifyPrice);
        //        get_AirVerifyFareFamiliesItinerary.setRequestHeader("Cookie", cookiestring_temp_2);
        //        int status_get_airVerifyPrice = httpClient.executeMethod(get_AirVerifyFareFamiliesItinerary);
        //        System.out.println("======================status_get_airVerifyPrice=============");
        //        System.out.println(status_get_airVerifyPrice);
        //        String responseBodyString_airVerifyPrice = get_AirVerifyFareFamiliesItinerary.getResponseBodyAsString();
        //        System.out.println("======================responseBodyString_airVerifyPrice=============");
        //        System.out.println(responseBodyString_airVerifyPrice);
        String AirVerifyFareFamiliesItinerary = "http://et.airchina.com.cn/InternetBooking/AirVerifyFareFamiliesItinerary.do";
        CCSPostMethod post_AirVerifyFareFamiliesItinerary = new CCSPostMethod(AirVerifyFareFamiliesItinerary);
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Cookie", cookiestring);
        NameValuePair NameValuePair_ajaxAction = new NameValuePair("ajaxAction", "true");
        String hiddenFlightSelection = "0," + flightItineraryId2;
        NameValuePair NameValuePair_flightItineraryId = new NameValuePair("flightItineraryId[0]", flightItineraryId2);
        NameValuePair NameValuePair_hiddenFlightSelection = new NameValuePair("hiddenFlightSelection",
                hiddenFlightSelection);
        NameValuePair NameValuePair_markUpMoneyAmount = new NameValuePair("markUpMoneyAmount", "");
        NameValuePair NameValuePair_vsessionid = new NameValuePair("vsessionid", "");
        NameValuePair[] nameValuePairs_AirVerifyFareFamiliesItinerary = { NameValuePair_ajaxAction,
                NameValuePair_flightItineraryId, NameValuePair_hiddenFlightSelection, NameValuePair_markUpMoneyAmount,
                NameValuePair_vsessionid };
        post_AirVerifyFareFamiliesItinerary.setRequestBody(nameValuePairs_AirVerifyFareFamiliesItinerary);
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Content-Length",
                Wrapper_CAUtil.getContentLength(nameValuePairs_AirVerifyFareFamiliesItinerary));

        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Accept-Encoding", "gzip, deflate");
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Connection", "keep-alive");
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");//
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Host", "www.airchina.com.cn");
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("Origin", "http://www.airchina.com.cn");
        post_AirVerifyFareFamiliesItinerary.addRequestHeader("User-Agent", USER_AGENT);
        int status_post_post_AirVerifyFareFamiliesItinerary = httpClient
                .executeMethod(post_AirVerifyFareFamiliesItinerary);
        //        cookiestring_temp_2 = Wrapper_CAUtil.getCookieString(httpClient);
        //        System.out.println("======================cookiestring_temp_2============");
        //        System.out.println(cookiestring_temp_2);
        System.out.println("======================status_post_post_AirVerifyFareFamiliesItinerary=============");
        System.out.println(status_post_post_AirVerifyFareFamiliesItinerary);
        String responseBodyString_AirVerifyFareFamiliesItinerary = post_AirVerifyFareFamiliesItinerary
                .getResponseBodyAsString();
        responseBodyString_AirVerifyFareFamiliesItinerary = getNewResponseBodyString(responseBodyString_AirVerifyFareFamiliesItinerary);
        System.out.println("======================responseBodyString_AirVerifyFareFamiliesItinerary=============");
        System.out.println(responseBodyString_AirVerifyFareFamiliesItinerary);
        JSONObject dataHtml_jsonobject = JSONObject.parseObject(responseBodyString_AirVerifyFareFamiliesItinerary);
        String toxingchenggaikuoUrl = dataHtml_jsonobject.getString("redirect");
        System.out.println("#################################cookieString:getUrlToxingchengGaikuo:"
                + Wrapper_CAUtil.getCookieString(httpClient));
        return toxingchenggaikuoUrl;
    }

    @Override
    public String getHtml(FlightSearch param) {
        CCSPostMethod post = null;
        CCSGetMethod get = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String departureDay = param.getFromDate().split("-")[2];
        String departureMonth = param.getFromDate().split("-")[1];
        String departureYear = param.getFromDate().split("-")[0];

        int guestTypes0_amount = 1;//成人数
        if (param.getGeneral() > 0) {
            guestTypes0_amount = param.getGeneral();
        }

        String url_1 = "http://et.airchina.com.cn/InternetBooking/AirLowFareSearchExternal.do?&tripType=OW"
                + "&searchType=FARE&flexibleSearch=false&directFlightsOnly=false&fareOptions=1.FAR.X"
                + "&outboundOption.originLocationCode=" + param.getStartAirportCode()
                + "&outboundOption.destinationLocationCode=" + param.getEndAirportCode() + ""
                + "&outboundOption.departureDay=" + departureDay + "&outboundOption.departureMonth=" + departureMonth
                + "&outboundOption.departureYear=" + departureYear
                + "&outboundOption.departureTime=NA&guestTypes%5B0%5D.type=ADT&guestTypes%5B0%5D.amount="
                + guestTypes0_amount
                + "&guestTypes%5B1%5D.type=CNN&guestTypes%5B1%5D.amount=0&pos=AIRCHINA_CN&lang=zh_CN"
                + "&guestTypes%5B2%5D.type=INF&guestTypes%5B2%5D.amount=0";

        String url_3 = getAirFareFamiliesFlexibleForwardurl(param, httpClient, post, get, url_1, 0);
        String dataHtml = "-1";
        String suifeiJson = "-1";
        if (!"-1".equals(url_3)) {
            String cookieString = param.getThirdAirportCode();//获取里面带的cookie
            cookieString = url_3.split("[|]")[1];
            url_3 = url_3.split("[|]")[0];

            get = new CCSGetMethod(url_3);
            if (cookieString != null) {//如果travelTravel为2，并且cookie不为空就setcookie
                get.setRequestHeader("Cookie", cookieString);
                get.setRequestHeader("Referer", url_1);
                try {
                    int status_3 = httpClient.executeMethod(get);
                    //                System.out.println("========status_3======" + status_3);
                    dataHtml = get.getResponseBodyAsString();
                    //                System.out.println("========dataHtml======");
                    //                System.out.println(dataHtml);
                }
                catch (HttpException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                suifeiJson = getsuifei(param, httpClient, post, get, url_1, 0);
            }
            if ("3".equals(param.getTravelType())) {//如果travelTravel为3，是请求这个地址获取新的cookie
                //                try {
                //                    CCSGetMethod CCSGetMethod_flightSearch = new CCSGetMethod(
                //                            "http://et.airchina.com.cn/www/jsp/aals/flightSearch.jsp");
                //                    CCSGetMethod_flightSearch.addRequestHeader("Cookie", Wrapper_CAUtil.getCookieString(httpClient));
                //                    httpClient.executeMethod(CCSGetMethod_flightSearch);
                //                }
                //                catch (HttpException e) {
                //                    e.printStackTrace();
                //                }
                //                catch (IOException e) {
                //                    e.printStackTrace();
                //                }
            }
        }
        JSONObject dataHtml_jsonobject = new JSONObject();
        if (!"-1".equals(dataHtml)) {
            dataHtml = dataHtml.substring(dataHtml.indexOf("tdGroupData[0] ="),
                    dataHtml.indexOf("flightItineraryGroupSearchEngParams[0]"));
            dataHtml = dataHtml.substring(dataHtml.indexOf("{"), dataHtml.lastIndexOf("}") + 1);
            //            System.out.println("========dataHtml_new======");
            dataHtml_jsonobject = JSONObject.parseObject(dataHtml);
            //            System.out.println("========dataHtml_new_JSONString======");
            //            System.out.println(dataHtml_jsonobject.toJSONString());
        }

        String cookieString = Wrapper_CAUtil.getCookieString(httpClient);
        System.out.println("#################################cookieString:getHtml:"
                + Wrapper_CAUtil.getCookieString(httpClient));
        return dataHtml_jsonobject.toJSONString() + "|" + suifeiJson + "|" + cookieString;
    }

    /**
     * 
     * 
     * @param param
     * @param httpClient
     * @param post
     * @param get
     * @param url_1
     * @param getcount 第几次获取
     * @return
     * @time 2015年6月11日 下午7:01:30
     * @author chendong
     */
    private String getAirFareFamiliesFlexibleForwardurl(FlightSearch param, CCSHttpClient httpClient,
            CCSPostMethod post, CCSGetMethod get, String url_1, int getcount) {
        get = new CCSGetMethod(url_1);
        //        System.out.println("========get=url=====");
        //        System.out.println(url_1);
        String cookieString = param.getThirdAirportCode();//获取里面带的cookie
        if ("2".equals(param.getTravelType()) && cookieString != null) {//如果travelTravel为2，并且cookie不为空就setcookie
            get.addRequestHeader("Cookie", cookieString);
        }
        get.addRequestHeader("Referer", "http://www.airchina.com.cn/cn/index.shtml");
        String result = "";
        try {
            int status = httpClient.executeMethod(get);
            System.out.println("========status======" + status);
            result = get.getResponseBodyAsString();
            //            System.out.println("========get======");
            //            System.out.println(result);
            //            System.out.println("========get========================");
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        String url_2 = "http://et.airchina.com.cn/InternetBooking/AirLowFareSearchExt.do";
        post = new CCSPostMethod(url_2);
        post.setFollowRedirects(false);
        if ("2".equals(param.getTravelType()) && cookieString != null) {
            post.setRequestHeader("Cookie", cookieString);
        }
        NameValuePair NameValuePairajaxAction = new NameValuePair("ajaxAction", "true");
        NameValuePair[] names2 = { NameValuePairajaxAction };
        post.setRequestBody(names2);
        post.setRequestHeader("Referer", url_1);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
        String url_3 = "-1";
        try {
            int status2 = httpClient.executeMethod(post);
            System.out.println("========status2=getAirFareFamiliesFlexibleForwardurl=====" + status2);
            String responseBody_2 = post.getResponseBodyAsString();
            //            System.out.println("========get2======");
            //            System.out.println(responseBody_2);
            //            System.out.println("========get2========================");
            responseBody_2 = getNewResponseBodyString(responseBody_2);
            //            System.out.println("========responseBody_2========================");
            //            System.out.println(responseBody_2);
            JSONObject jsonobject_2 = JSONObject.parseObject(responseBody_2);
            String status_2 = jsonobject_2.getString("status");
            //            System.out.println("========jsonobject_2========================");
            //            System.out.println(jsonobject_2.toJSONString());
            if ("success".equals(status_2)) {
                url_3 = jsonobject_2.getString("redirect");
            }
            else {
                if (getcount < 5) {
                    getcount++;
                    url_3 = getAirFareFamiliesFlexibleForwardurl(param, httpClient, post, get, url_1, getcount);
                }
            }
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("#################################cookieString:getAirFareFamiliesFlexibleForwardurl:"
                + Wrapper_CAUtil.getCookieString(httpClient));
        return url_3 + "|" + Wrapper_CAUtil.getCookieString(httpClient);
    }

    @Override
    public List<FlightInfo> process(String html, FlightSearch param) {
        List<FlightInfo> flightinfos = new ArrayList<FlightInfo>();
        String html1 = html.split("[|]")[0];
        String html2 = html.split("[|]")[1];
        float FuelFee = 0;
        float AirportFee = 0;
        try {
            FuelFee = Float.parseFloat(html2.split(",")[0]);
            AirportFee = Float.parseFloat(html2.split(",")[1]);
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        JSONObject jsonobject = JSONObject.parseObject(html1);
        Set<String> sets = jsonobject.keySet();
        Iterator iterator = sets.iterator();
        sets = jsonobject.keySet();
        iterator = sets.iterator();
        while (iterator.hasNext()) {
            FlightInfo flightInfo = new FlightInfo();
            String array_key = iterator.next().toString();
            String dataString = jsonobject.getString(array_key);//D|T|G||C|O|Z|K
            //            System.out.println(array_key + "==s=============================");
            //            System.out.println(dataString);
            String[] dataString_td = dataString.split("<td");
            String[] dataString_itineraryPriceCell = dataString.split("itineraryPriceCell");
            //            System.out.println("dataString_td]==s=============================");
            String airline = "";
            String sTime = "";
            String eTime = "";
            String scityCode = "";
            String ecityCode = "";
            String scityname = "";
            String ecityname = "";
            String AirplaneType = "";
            for (int i = 1; i < dataString_td.length; i++) {
                String dataString_i = dataString_td[i];
                //                System.out.println(dataString_i);
                if (i == 1) {
                    airline = getDataInfo(dataString_i, i);
                }
                else if (i == 2) {
                    sTime = getDataInfo(dataString_i, i);
                }
                else if (i == 3) {
                    eTime = getDataInfo(dataString_i, i);
                }
                else if (i == 4) {
                    String scityCode_ecityCode = getDataInfo(dataString_i, i);
                    scityCode = scityCode_ecityCode.split("-")[0];
                    ecityCode = scityCode_ecityCode.split("-")[1];
                    String scityName_ecityName = getDataInfo(dataString_i, i + 100);
                    scityname = scityName_ecityName.split("-")[0];
                    scityname = scityname.substring(0, scityname.indexOf("(")).trim();
                    ecityname = scityName_ecityName.split("-")[1];
                    ecityname = ecityname.substring(0, ecityname.indexOf("(")).trim();
                }
                else if (i == 5) {
                    AirplaneType = getDataInfo(dataString_i, i);
                }
            }
            List<CarbinInfo> listCabinAll = new ArrayList<CarbinInfo>();
            Float basePrice = 0f;
            for (int i = 1; i < dataString_itineraryPriceCell.length; i++) {
                String dataString_itineraryPriceCell_string = dataString_itineraryPriceCell[i];
                if (dataString_itineraryPriceCell_string.indexOf("type=\"radio\"") >= 0) {
                    CarbinInfo cabin = getCabinInfo(dataString_itineraryPriceCell_string, i);
                    if (i == 3) {
                        basePrice = cabin.getPrice();
                    }
                    listCabinAll.add(cabin);
                }
            }
            System.out.println(airline + ":" + sTime + "--->" + eTime + ":" + scityCode + "--->" + ecityCode + ":"
                    + AirplaneType);
            //            System.out.println("dataString_td]==e=============================");
            //            System.out.println(array_key + "==e=============================");
            //            System.out.println("");
            //            System.out.println("");

            flightInfo.setAirline(airline);// 航线
            flightInfo.setStartAirport(scityCode);// 起飞机场
            flightInfo.setStartAirportName(scityname);// 起飞起场名称
            flightInfo.setEndAirport(ecityCode);// 到达机场
            flightInfo.setEndAirportName(ecityname);// 到达机场名称
            flightInfo.setStartAirportCity("");// 起飞起场城市名称
            flightInfo.setAirCompany("CA");// 航空公司
            flightInfo.setAirCompanyName("国航");// 航空公司名称airlineName
            flightInfo.setAirportFee(AirportFee);// 机场建设费float AirportFee;
            flightInfo.setFuelFee(FuelFee);// 燃油费
            //                    flightInfo.setDistance(flight.getString("carrier"));// 里程数
            //                    flightInfo.setMeal(flight.getString("carrier"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate;
            Date arriveDate;
            try {
                startDate = dateFormat.parse(param.getFromDate() + " " + sTime);
                arriveDate = dateFormat.parse(param.getFromDate() + " " + eTime);
                flightInfo.setDepartTime(new Timestamp(startDate.getTime()));// 起飞时间
                flightInfo.setArriveTime(new Timestamp(arriveDate.getTime()));// 到达时间
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            flightInfo.setIsShare(0);
            flightInfo.setAirplaneType(AirplaneType);// 飞机型号
            flightInfo.setAirplaneTypeDesc("");// 飞机型号描述
            flightInfo.setOffPointAT("");// 出发航站楼
            flightInfo.setBorderPointAT("");// 到达航站楼
            flightInfo.setStop(false);
            listCabinAll = AirUtil.reSetDiscount(listCabinAll, basePrice);
            listCabinAll = AirUtil.sortListCabinAll(listCabinAll);
            flightInfo.setCarbins(listCabinAll);// 加入 仓位信息
            flightInfo.setYPrice(basePrice);// 全价价格
            CarbinInfo lowCabinInfo = AirUtil.getlowCabinInfo(listCabinAll);
            flightInfo.setLowCarbin(lowCabinInfo);
            flightinfos.add(flightInfo);
        }
        return flightinfos;
    }

    private CarbinInfo getCabinInfo(String dataString_itineraryPriceCell_string, int i) {
        CarbinInfo cabin = new CarbinInfo();
        String price = getDataInfoCabinPrice(dataString_itineraryPriceCell_string, 1);
        String cabintypename = getCabintypeName(i);//头等                     公务                    全价                    折扣                    特价
        cabin.setCabintypename(cabintypename);
        String Cabin = getCabin(i);
        cabin.setCabin(Cabin);
        String SeatNum = getDataInfoCabinPrice(dataString_itineraryPriceCell_string, 2);
        cabin.setSeatNum(SeatNum);
        cabin.setDiscount(10F);
        cabin.setPrice(Float.parseFloat(price));
        return cabin;
    }

    /**
     *  //        头等
        //        公务
        //        全价
        //        折扣
        //        特价
        //        P,F,A
        //        C,D,Z,J
        //        W, Y
        //        B, M, H, K, L, Q
        //        G,V,S,U,E,T,N
     * 
     * @param i
     * @return
     * @time 2015年6月12日 下午2:46:11
     * @author chendong
     */
    private String getCabin(int i) {
        String cabintypename = "Y";
        if (i == 1) {
            cabintypename = "F";
        }
        else if (i == 2) {
            cabintypename = "C";
        }
        else if (i == 3) {
            cabintypename = "Y";
        }
        else if (i == 4) {
            cabintypename = "B";
        }
        else if (i == 5) {
            cabintypename = "G";
        }
        return cabintypename;
    }

    private String getCabintypeName(int i) {
        //        头等                     公务                    全价                    折扣                    特价
        String cabintypename = "经济舱";
        if (i == 1) {
            cabintypename = "头等舱";
        }
        else if (i == 2) {
            cabintypename = "公务舱";
        }
        else if (i == 3) {
            cabintypename = "经济舱";
        }
        else if (i == 4) {
            cabintypename = "经济舱";
        }
        else if (i == 5) {
            cabintypename = "特价舱";
        }
        return cabintypename;
    }

    public String getDataInfo(String dataHtml, int datatype) {
        String datainfo = "";
        if (datatype == 1) {//获取航班号
            datainfo = dataHtml.substring(dataHtml.indexOf(">CA") + 1, dataHtml.indexOf("</a>"));
        }
        else if (datatype == 2 || datatype == 3) {
            datainfo = dataHtml.substring(dataHtml.indexOf("<div >") + 6, dataHtml.indexOf("</div>"));
        }
        else if (datatype == 4) {
            try {
                datainfo = dataHtml.substring(dataHtml.indexOf("toolTip.over(this, event);\">") + 29,
                        dataHtml.indexOf("<div class=\"toolTipInfo\">"));
            }
            catch (Exception e) {
                System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                System.out.println(dataHtml);
                System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            }
        }
        else if (datatype == 104) {
            datainfo = dataHtml.substring(dataHtml.indexOf("<p>") + 3, dataHtml.indexOf("</p>"));
        }
        else if (datatype == 5) {
            datainfo = dataHtml.substring(dataHtml.indexOf("return false;\">") + 15, dataHtml.indexOf("</a>"));
        }
        return datainfo.trim();
    }

    public String getDataInfoCabinPrice(String dataHtml, int datatype) {
        String datainfo = "";
        if (datatype == 1) {//获取价格
            datainfo = dataHtml.substring(dataHtml.lastIndexOf("flightSelectGr") + 15, dataHtml.indexOf("</label>"));
            datainfo = datainfo.substring(datainfo.indexOf(">") + 1, datainfo.length()).replaceAll(",", "");
        }
        else if (datatype == 2) {//获取余票数
            if (dataHtml.indexOf("剩余") >= 0) {
                datainfo = dataHtml.substring(dataHtml.lastIndexOf("剩余") + 2, dataHtml.indexOf("个座位"));
            }
            else {
                datainfo = "9";
            }
        }
        else if (datatype == 3) {
            datainfo = dataHtml.substring(dataHtml.indexOf("<div >") + 6, dataHtml.indexOf("</div>"));
        }
        else if (datatype == 4) {
            datainfo = dataHtml.substring(dataHtml.indexOf("'toolTip.over(this, event);'>") + 30,
                    dataHtml.indexOf("<div class='toolTipInfo'>"));
        }
        else if (datatype == 104) {
            datainfo = dataHtml.substring(dataHtml.indexOf("<p>") + 3, dataHtml.indexOf("</p>"));
        }
        else if (datatype == 5) {
            datainfo = dataHtml.substring(dataHtml.indexOf("return false;'>") + 15, dataHtml.indexOf("</a>"));
        }
        return datainfo.trim();
    }

    private String getsuifei(FlightSearch param, CCSHttpClient httpClient, CCSPostMethod post, CCSGetMethod get,
            String url_1, int getcount) {
        String suifeiString = "50,0";
        post = new CCSPostMethod("http://www.airchina.com.cn/InternetBooking/AirSelectOWCFlight.do");
        post.setFollowRedirects(false);
        post.addRequestHeader("Cookie", Wrapper_CAUtil.getCookieString(httpClient));
        NameValuePair NameValuePair_isFareFamilySearchResult = new NameValuePair("isFareFamilySearchResult", "true");
        NameValuePair NameValuePair_selectedItineraries = new NameValuePair("selectedItineraries", "0,0");
        NameValuePair NameValuePair_selectedFlightIds = new NameValuePair("selectedFlightIds", "0,0");
        NameValuePair NameValuePair_combinabilityReloadRequired = new NameValuePair("combinabilityReloadRequired",
                "false");
        NameValuePair NameValuePair_flightIndex = new NameValuePair("flightIndex", "");
        NameValuePair NameValuePair_flowStep = new NameValuePair("flowStep",
                "AIR_COMBINABLE_FARE_FAMILIES_FLEXIBLE_SEARCH_RESULTS");
        NameValuePair NameValuePair_alignment = new NameValuePair("alignment", "horizontal");
        NameValuePair NameValuePair_context = new NameValuePair("context", "airSelection");

        NameValuePair[] names2 = { NameValuePair_isFareFamilySearchResult, NameValuePair_selectedItineraries,
                NameValuePair_selectedFlightIds, NameValuePair_combinabilityReloadRequired, NameValuePair_flightIndex,
                NameValuePair_flowStep, NameValuePair_alignment, NameValuePair_context };
        post.setRequestBody(names2);
        post.setRequestHeader("Referer", "http://www.airchina.com.cn/InternetBooking/AirFareFamiliesFlexibleForward.do");
        //        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
        String url_3 = "-1";
        try {
            int status2 = httpClient.executeMethod(post);
            System.out.println("========status2=getsuifei====================" + status2);
            String responseBody_2 = post.getResponseBodyAsString();
            //            System.out.println("========get2======");
            //            System.out.println(responseBody_2);
            //            System.out.println("========get2========================");
            responseBody_2 = responseBody_2.substring(0, responseBody_2.lastIndexOf("}") + 1);
            //            System.out.println("========responseBody_2========================");
            //            System.out.println(responseBody_2);
            JSONObject jsonobject_2 = JSONObject.parseObject(responseBody_2);
            String status_2 = jsonobject_2.getString("status") == null ? "-1" : jsonobject_2.getString("status");
            //            System.out.println("========jsonobject_2========================");
            //            System.out.println(jsonobject_2.toJSONString());
            if (jsonobject_2.toJSONString().indexOf("税费") >= 0) {
                url_3 = jsonobject_2.toJSONString().substring(jsonobject_2.toJSONString().indexOf("税费"),
                        jsonobject_2.toJSONString().indexOf("totalPrice"));
                url_3 = url_3.substring(url_3.indexOf("price"), url_3.indexOf("</tr>"));
                url_3 = url_3.substring(url_3.indexOf("<div>"), url_3.indexOf("</div>"));
                url_3 = url_3.substring(url_3.lastIndexOf("t") + 1, url_3.length());
                System.out.println(url_3);
                suifeiString = url_3 + ",0";
            }
            else {
                if (getcount < 5) {
                    getcount++;
                    url_3 = getsuifei(param, httpClient, post, get, url_1, getcount);
                }
            }

        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return suifeiString;
    }

    /**
     * 
     * @param string
     * @time 2015年7月1日 下午12:41:58
     * @author chendong
     */
    private String getNewResponseBodyString(String responseBodyString) {
        try {
            responseBodyString = responseBodyString.substring(responseBodyString.indexOf("{"),
                    responseBodyString.lastIndexOf("}") + 1);
            responseBodyString = JSONObject.parseObject(responseBodyString).toJSONString();
        }
        catch (Exception e) {
        }
        return responseBodyString;
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

    public String getmboxCookieString() {
        String mboxCookie = "check#true#"
                + ((Double) Math.ceil((System.currentTimeMillis() / 1000 + 60))).toString().replace(".", "")
                        .replace("E", "")
                + "|session#"
                + System.currentTimeMillis()
                + "-"
                + (int) (new Random().nextFloat() * 999999)
                + "#"
                + ((Double) Math.ceil((System.currentTimeMillis() / 1000 + 60))).toString().replace(".", "")
                        .replace("E", "");
        return mboxCookie;
    }
}
