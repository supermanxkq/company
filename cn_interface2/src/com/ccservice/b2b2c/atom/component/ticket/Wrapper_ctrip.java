package com.ccservice.b2b2c.atom.component.ticket;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.ccservice.b2b2c.atom.component.PublicComponent;
import com.ccservice.b2b2c.atom.component.ticket.Interface.CcsTicketCrawler;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;

/**
 * 携程抓取机票特价信息
 * 国内
 * @author chendong
 *
 */
public class Wrapper_ctrip extends PublicComponent implements CcsTicketCrawler {

    public static void mai1n(String[] args) {

        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        CCSGetMethod get = new CCSGetMethod("");
        get.addRequestHeader("Cookie",
                "_abtest_userid=2567ee05-2835-4a2b-810f-41f50d81c25d; NSC_WT_Gmjhiut_80=ffffffff09001d2c45525d5f4f58455e445a4a423660");
        get.setRequestHeader("Referer", "http://flights.ctrip.com/booking/BJS-URC-day-1.html");

        try {
            httpClient.executeMethod(get);
            String responseBody = get.getResponseBodyAsString();
            System.out.println(responseBody);
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Wrapper_ctrip ctrip = new Wrapper_ctrip();
        FlightSearch param = new FlightSearch();
        param.setStartAirportCode("PEK");
        param.setStartAirPortName("");
        param.setEndAirportCode("URC");
        param.setEndAirPortName("");
        param.setFromDate("2014-11-28");
        String html = ctrip.getHtml(param);
        System.out.println(html);
    }

    @Override
    public String getHtml(FlightSearch param) {
        CCSPostMethod post = null;
        CCSGetMethod get = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
        String url = "http://flights.ctrip.com/domesticsearch/search/SearchFirstRouteFlights";
        String scity = param.getStartAirportCode();
        String ecity = param.getEndAirportCode();
        String data = param.getFromDate();
        scity = getcitycode(scity);
        ecity = getcitycode(ecity);
        String Referer_url = "http://flights.ctrip.com/booking/" + scity + "-" + ecity + "-day-1.html";
        post = new CCSPostMethod(Referer_url);
        String s = "DCity1=" + scity + "&ACity1=" + ecity + "&SearchType=S" + "&DDate1=" + data + "&EncryptUserId="
                + "&r=" + new Random(1).nextFloat();
        NameValuePair DCity1 = new NameValuePair("DCity1", scity);
        NameValuePair ACity1 = new NameValuePair("ACity1", ecity);
        NameValuePair SearchType = new NameValuePair("SearchType", "S");
        NameValuePair DDate1 = new NameValuePair("DDate1", data);
        NameValuePair DCity2 = new NameValuePair("DCity2", "");
        NameValuePair ACity2 = new NameValuePair("ACity2", "");
        NameValuePair DDate2 = new NameValuePair("DDate2", "");
        NameValuePair TransitCity = new NameValuePair("TransitCity", "");
        NameValuePair SendTicketCity = new NameValuePair("SendTicketCity", "");
        NameValuePair PassengerType = new NameValuePair("PassengerType", "ADU");
        NameValuePair DCityName1 = new NameValuePair("DCityName1", scity);
        NameValuePair DCityName2 = new NameValuePair("DCityName2", "");
        NameValuePair ACityName1 = new NameValuePair("ACityName1", ecity);
        NameValuePair ACityName2 = new NameValuePair("ACityName2", "");
        NameValuePair IsSingleSearchPost = new NameValuePair("IsSingleSearchPost", "T");
        NameValuePair SEOAirlineDibitCode = new NameValuePair("SEOAirlineDibitCode", "");
        NameValuePair FlightSearchType = new NameValuePair("FlightSearchType", "S");
        NameValuePair PassengerQuantity = new NameValuePair("PassengerQuantity", "1");
        NameValuePair ClassType = new NameValuePair("ClassType", "");

        NameValuePair EncryptUserId = new NameValuePair("EncryptUserId", "");
        NameValuePair r = new NameValuePair("r", new Random(1).nextFloat() * 1000 + "");

        NameValuePair[] names = { DCity1, ACity1, DDate1, DCity2, ACity2, DDate2, TransitCity, SendTicketCity,
                PassengerType, DCityName1, DCityName2, ACityName1, ACityName2, IsSingleSearchPost, SEOAirlineDibitCode,
                FlightSearchType, PassengerQuantity, ClassType };
        post.setRequestBody(names);
        String responseBody = "";
        try {

            httpClient.executeMethod(post);
            responseBody = post.getResponseBodyAsString();
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //============================第二个请求开始
        String cookie = "";
        Header[] hearders_cookie = post.getResponseHeaders("Set-Cookie");
        for (int i = 0; i < hearders_cookie.length; i++) {
            cookie += hearders_cookie[i].getValue().split(";")[0] + ";";
        }
        try {
            String CK_S = getCK(responseBody);
            String rk_S = getrk(responseBody);

            //        http: //flights.ctrip.com/domesticsearch/search/SearchFirstRouteFlights?DCity1=BJS&ACity1=URC&SearchType=S
            //            &DDate1=2014-11-07&CK=E92E7F0133CCD50D269753A4D7EF8179&rk=786.6262143943459&r=0.17169240912914808871512
            //        System.out.println(CK_S);
            url = url + "?DCity1=" + scity + "&ACity1=" + ecity + "&SearchType=S&DDate1=" + data + "&CK=" + CK_S
                    + "&rk=" + rk_S + "&r=0.17169240912914808871512";
            //        System.out.println(url);
            get = new CCSGetMethod(url);
            post = new CCSPostMethod(url);
            String data111 = "S$(" + scity + ")$" + scity + "$" + data + "$(" + ecity + ")$" + ecity;
            data111 = URLEncoder.encode(data111);
            String FD_SearchHistorty = "{\"type\":\"S\",\"" + data111 + "\":\"\"}";
            cookie += "FD_SearchHistorty=" + FD_SearchHistorty + "; ";
            get.addRequestHeader("Cookie", cookie);
            get.setRequestHeader("Referer", Referer_url);

            httpClient.executeMethod(get);
            responseBody = get.getResponseBodyAsString();
            //            System.out.println(responseBody);
            return responseBody;
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "Exception";
    }

    @Override
    public List<FlightInfo> process(String html, FlightSearch param) {
        return null;
    }

    public String getCK(String html) {
        String CK = "";
        try {
            CK = html.substring(html.indexOf("&CK=") + 4, html.indexOf("var _searchCount_c"));
            CK = CK.replace("\";", "").trim();
        }
        catch (Exception e) {
        }
        return CK;
    }

    public static String getrk(String html) {
        String CK = "";
        try {
            CK = "";
            CK = html.substring(html.indexOf("var rk = \"") + 10, html.indexOf("var t = null") - 3);
        }
        catch (Exception e) {
        }
        return CK;
    }
}
