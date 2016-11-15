package com.ccservice.b2b2c.atom.component.ticket;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.ticket.Interface.CcsInterTicketCrawler;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.interticket.AirSegementBean;
import com.ccservice.b2b2c.base.interticket.AllRouteBean;
import com.ccservice.b2b2c.base.interticket.FlightinfoBean;
import com.ccservice.b2b2c.base.interticket.RouteBean;

/**
 * 携程抓取国际机票信息
 * 作者：邹远超
 * 日期：2014年8月27日
 * 
 * 已经移动到ticket_inter项目里修改
 * @time 2014年11月6日 下午4:22:32
 * @author chendong
 */
public class fWrapper_ctripInter implements CcsInterTicketCrawler {

    public static void main(String[] args) {
        fWrapper_ctripInter MU = new fWrapper_ctripInter();
        FlightSearch param = new FlightSearch();

        param.setStartAirportCode("BJS");
        param.setStartAirPortName("北京");
        param.setEndAirportCode("TYO");
        param.setEndAirPortName("东京");
        param.setFromDate("2014-09-21");

        String html = MU.getHtml(param, "", "");
        System.out.println(html);
        String ss = "    ";
        AllRouteBean allRoute = MU.process(html, param);
        for (RouteBean route : allRoute.getRoutes()) {
            System.out.println(route.getRairCo() + ss + route.getRfromCity());
            for (AirSegementBean airSegement : route.getAirSegements()) {
                System.out.println(airSegement.getAairCo() + ss + airSegement.getAtoAirport());
                for (FlightinfoBean flight : airSegement.getFlightinfos()) {
                    System.out.println(flight.getDestination() + "--" + flight.getFarrivalDate() + "--");
                }
            }
        }
    }

    public String getHtml(FlightSearch param, String url, String cookie) {
        url = "http://flights.ctrip.com/international/AjaxRequest/UI2_0/SearchResultHandler.ashx";
        cookie = "zdata=zdata=3JK7HD1zaH/YIVZP9sf7v1ZjLCo=; _abtest_=ff049164-cc65-4d3e-811a-cedfa1e2c9ca; "
                + "_abtest_userid=af1777a2-eefc-4e7e-b0dd-4099853ebb7e; appFloatCnt=3; manualclose=1; "
                + "DomesticUserHostCity=BJS|%b1%b1%be%a9; LastSearch_S=S%24%u5317%u4EAC%28BJS%29%24BJS%242014-09-21%24%u91CD%u5E86%28CKG%29%24CKG;"
                + " LastSearchSearchType=S; __zpa=9.1.1409192474.1409192548.3.0; preferenceDegree=searchByCF=false; "
                + "LowestPriceCalendarCity=SHA|"
                + param.getStartAirportCode()
                + "|"
                + param.getStartAirPortName()
                + "|"
                + param.getEndAirPortName()
                + "; Session=SmartLinkLanguage=zh&SmartLinkHost=&SmartLinkQuary=&SmartLinkKeyWord=&SmartLinkCode=U155952;"
                + " Union=OUID=baidu81%7Cindex%7C%7C%7C&AllianceID=4897&SID=155952; traceExt=campaign=CHNbaidu81&adid=index;"
                + " ASP.NET_SessionId=v15mh3hem25n03im1ydmys22; ASP.NET_SessionSvc=MTAuOC45Mi4xMjF8OTA5MHxqaW5xaWFvfGRlZmF1bHR8MTQwNjExNjY5OTIxMw;"
                + " __utma=1.406296820.1409111294.1409215754.1409534815.11; __utmb=1.8.10.1409534815; __utmc=1; "
                + "__utmz=1.1409534815.11.5.utmcsr=baidu|utmccn=baidu81|utmcmd=cpc|utmctr=%E6%90%BA%E7%A8%8B%E6%97%85%E8%A1%8C%E7%BD%91;"
                + " _bfa=1.1409111293800.306nzu.1.1409215754282.1409534815308.10.50; _bfs=1.4; bid=bid=F; "
                + "zdatactrip=zdatactrip=0.8701910630334169; _bfi=p1%3D104001%26p2%3D104002%26v1%3D50%26v2%3D49; "
                + "FlightIntl=Search=%5B%22Beijing%7C%E5%8C%97%E4%BA%AC(" + param.getStartAirportCode() + ")%7C1%7C"
                + param.getStartAirportCode() + "%22%2C%22Seoul%7C%E9%A6%96%E5%B0%94(" + param.getEndAirportCode()
                + ")%7C274%7CSEL%22%2C%222014-09-04%22%5D; " + "AX-20480-flights_international=KAADAIAKFAAA";
        String paramcond = "SearchMode=Search&condition=";
        String paramContent = "{\"FlightWay\":\"S\",\"SegmentList\":[{" + "\"DCityCode\":\""
                + param.getStartAirportCode() + "\"," + "\"ACityCode\":\"" + param.getEndAirportCode() + "\","
                + "\"DCity\":\"Beijing|" + param.getStartAirPortName() + "(" + param.getStartAirportCode() + ")|" + 1
                + "|" + param.getStartAirportCode() + "\"," + "\"ACity\":\"Hong Kong|" + param.getEndAirPortName()
                + "(" + param.getEndAirportCode() + ")|" + 58 + "|" + param.getEndAirportCode() + "\","
                + "\"DepartDate\":\"" + param.getFromDate() + "\"}],"

                /*+ "\"RefUrl\":" + "\"" + url + "?flighttype=S&relddate="
                + param.getFromDate() + "&dcity=" + param.getStartAirportCode().toLowerCase() + "&acity="
                + param.getEndAirportCode().toLowerCase() + "\","
                */+ "\"TransNo\":\"2614082816000134730\",\"SID\":22643073,\"IsWifiGift\":\"T\"}";
        try {
            paramContent = URLEncoder.encode(paramContent, "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //        System.out.println(paramContent);
        //        System.out.println(cookie);
        String result = SendPostandGet.submitPost2(url, paramcond + paramContent, "GBK", cookie).toString();
        return result;
    }

    public AllRouteBean process(String html, FlightSearch param) {
        AllRouteBean allRoute = new AllRouteBean();
        List<RouteBean> routes = new ArrayList<RouteBean>();

        JSONObject jsonobject = JSONObject.fromObject(html);

        if (jsonobject.has("FlightList")) {

            Float basePrice = 0f;
            JSONArray FlightDetail = jsonobject.getJSONArray("FlightList");
            for (int i = 0; i < FlightDetail.size(); i++) {
                List<AirSegementBean> airSegements = new ArrayList<AirSegementBean>();
                JSONObject flight = FlightDetail.getJSONObject(i);
                JSONArray fl = flight.getJSONArray("FareList");
                JSONArray fd = flight.getJSONArray("FlightDetail");
                JSONObject jd = fd.getJSONObject(0);
                JSONObject jd2 = fd.getJSONObject(fd.size() - 1);
                JSONObject jl = fl.getJSONObject(0);
                RouteBean routBean = new RouteBean();//航线

                routBean.setRfromCity(jd.getString("DPort"));// 起飞机场代码
                routBean.setRtoCity(jd2.getString("APort"));// 到达机场代码
                routBean.setRairCo(flight.getString("OwnerAirline"));
                routBean.setRtotalFare(Float.valueOf(Float.valueOf(jl.getString("Price"))
                        + Float.valueOf(jl.getString("OilFee"))));
                routBean.setRtotalTax(Float.valueOf(jl.getString("Tax")));
                routBean.setRtruencountback(flight.getString("TransferCount"));//转机次数
                routBean.setRtruencount(Integer.valueOf(flight.getString("TotalTransfer")));//总转机次

                for (int fdi = 0; fdi < fd.size(); fdi++) {
                    AirSegementBean airSegement = new AirSegementBean();
                    jd = fd.getJSONObject(fdi);
                    airSegement.setAfromCity(jd.getString("DPortName"));
                    airSegement.setAtoCity(jd.getString("APortName"));
                    airSegement.setAairCo(jd.getString("AirlineCode"));
                    airSegement.setAflightNumber(jd.getString("FlightNo"));
                    airSegement.setAfromAirport(jd.getString("DPort"));
                    airSegement.setAtoAirport(jd.getString("APort"));
                    airSegement.setAfromDate(jd.getString("DepartTime").split(" ")[0]);
                    airSegement.setAfromTime(jd.getString("DepartTime").split(" ")[1]);
                    airSegement.setAtoDate(jd.getString("ArrivalTime").split(" ")[0]);
                    airSegement.setAtoTime(jd.getString("ArrivalTime").split(" ")[1]);
                    //                    System.out.println(i + "  " + fl.size() + "  " + fdi);
                    if (fdi < fl.size())
                        jl = fl.getJSONObject(fdi);
                    if (jd.has("ClassName"))
                        airSegement.setAseatType(jl.getString("ClassName"));// 舱位类型编码
                    //airSegement.setAstagemoney(jl.getString(""));// 分段价格
                    //airSegement.setAdeparttype(Integer.valueOf(s_departtype));// 出发为1（返回为0）单程始终为1
                    airSegement.setGroup(Integer.valueOf(fdi + 1));// 航段序号（去程和回程都从1开始，可以根据这个计算转机次数）

                    FlightinfoBean flightInfo = new FlightinfoBean();
                    flightInfo.setFlightNumber(jd.getString("FlightNo"));// 航班号
                    flightInfo.setOrigin(jd.getString("DPort"));// 起飞机场代码
                    flightInfo.setDestination(jd.getString("APort"));// 到达机场代码
                    flightInfo.setCarrier(jd.getString("AirlineCode"));// 航空公司代码
                    flightInfo.setFequipType(jd.getString("CraftType"));// 机型
                    flightInfo.setDepartureTime(jd.getString("DepartTime").split(" ")[1]);
                    flightInfo.setArrivalTime(jd.getString("ArrivalTime").split(" ")[1]);
                    flightInfo.setFdepartureDate(jd.getString("DepartTime").split(" ")[0]);
                    flightInfo.setFarrivalDate(jd.getString("ArrivalTime").split(" ")[0]);
                    flightInfo.setFduration(jd.getString("FlightTime"));// 飞行时长
                    if (jl.has("TicketLack"))
                        flightInfo.setFspaceremainamount(jl.getString("TicketLack"));// 舱位剩余数量
                    //flightinfo.setFisenjoyflight(// 是否代码共享航班(0-非共享航班
                    // 1-共享航班)
                    //flightinfo.setFisstopflight(// 是否经停航班(0-否 1-是)
                    if (jd.has("DTerminal"))
                        flightInfo.setFstarterminal(jd.getString("DTerminal"));// 起飞航站楼

                    if (jd.has("ATerminal"))
                        flightInfo.setFendterminal(jd.getString("ATerminal"));// 到达航站楼
                    if (jd.has("APortName") && fdi != fd.size() - 1)
                        flightInfo.setFendterminal(jd.getString("APortName"));// 经停机场
                    if (jd.has("TransferTime") && fdi != fd.size() - 1)
                        flightInfo.setFstoptime(jd.getString("TransferTime"));// 停留时间
                    flightInfo.setFrealflight(jd.getString("AirlineName"));// 实际承运航班

                    List<FlightinfoBean> flightinfos = new ArrayList<FlightinfoBean>();
                    flightinfos.add(flightInfo);

                    airSegement.setFlightinfos(flightinfos);
                    airSegements.add(airSegement);
                    // tempMap.put(temp_, airSegements);
                    // temp_++;
                }
                routBean.setRnewPrice(Float.valueOf(basePrice));// 全价价格 
                routBean.setAirSegements(airSegements);
                routes.add(routBean);
            }
            allRoute.setRoutes(routes);

        }
        return allRoute;
    }
    /*   String cookie2 = "Session=SmartLinkLanguage=zh&SmartLinkHost=&SmartLinkQuary=&SmartLinkKeyWord=&SmartLinkCode=U155952;"
     + "Union=OUID=baidu81|index|||&AllianceID=48971&SID=155952;__utma=1.1452636035.1409212649.1409212649.1409212649.1;"
     + "__utmb=1.14.10.1409212649;__utmc=1;_bfa=1.1409212648911.3k8psf.1.1409212648911.1409212648911.1.9;_bfs=1.9;"
     + "appFloatCnt=1;traceExt=campaign=CHNbaidu81&adid=index;zdata=zdata=bwde/6zGiXUfdPo9Lf9o1z2whgM=;bid=bid=F;"
     + "zdatactrip=zdatactrip=0.8759384706481551;manualclose=1;AX-20480-flights_international=HMAEAIAKFAAA;AX-20480-flights_domestic=FGAOAIAKFAAA;ASP.NET_SessionId=xqi0g2fsyowtff3wt20bnbi4;"
     + "_abtest_=3875eb6c-0da7-45e1-86da-e0ff29317e25;ASP.NET_SessionSvc=MTAuOC45Mi4xMjR8OTA5MHxqaW5xaWFvfGRlZmF1bHR8MTQwNjExNzg2ODA0OA;"
     + "LastSearchSearchType=S;_abtest_userid=b82f6640-c317-4cde-9c02-545eef8d3425;preferenceDegree=searchByCF=false;"
     + "__zpa=9.1.1409215450.1409216272.2.0;__zpb=9;__zpc=9;Union=OUID=baidu81|index|||&AllianceID=4897&SID=155952;"
     + "__utmz=1.1409212649.1.1.utmcsr=baidu|utmccn=baidu81|utmcmd=cpc|utmctr=携程;_bfi=p1=104002&p2=101027&v1=9&v2=8;"
     + "FlightIntl=Search=[\"Beijing|北京(BJS)|1|BJS\",\"Hong Kong|香港(HKG)|58|HKG\",\"2014-09-21\"];"
     + "DomesticUserHostCity=BJS|北京;LowestPriceCalendarCity=SHA|"
     + param.getStartAirportCode()
     + "|上海|北京;LastSearch_S=S$北京(BJS)$BJS$2014-09-21$香港(HKG)$HKG;" + "__zpr=flights.ctrip.com|";*/
}
