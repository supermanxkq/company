package com.ccservice.b2b2c.atom.component.ticket;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.ticket.Interface.CcsTicketCrawler;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.util.AirUtil;

/**
 * 春秋机票信息
 * 
 * @author chendong
 *
 */
public class Wrapper_CH implements CcsTicketCrawler {

    public static void main(String[] args) {
        Wrapper_CH MU = new Wrapper_CH();
        FlightSearch param = new FlightSearch();

        param.setStartAirportCode("SHA");
        param.setStartAirPortName("上海");
        param.setEndAirportCode("SJW");
        param.setEndAirPortName("石家庄");
        param.setFromDate("2015-06-19");
        //        String html = "{\"Currency\":0,\"Step\":\"New\",\"IfExistLC\":false,\"Packages\":[[{\"Transport\":0,\"LyId\":\"0\",\"No\":\"9C8844\",\"RouteType\":11,\"Channel\":1,\"RouteArea\":1,\"Id\":\"565311\",\"Type\":\"A320\",\"IsDiscount\":false,\"DepartureTime\":\"2015-06-21 10:15:00\",\"DepartureTimeBJ\":\"2015-06-21 10:15:00\",\"Departure\":\"大连\",\"DepartureStation\":\"周水子\",\"DepartureCode\":\"DLC\",\"ArrivalTime\":\"2015-06-21 12:05:00\",\"ArrivalTimeBJ\":\"2015-06-21 12:05:00\",\"Arrival\":\"上海\",\"ArrivalStation\":\"浦东2号航站楼\",\"ArrivalCode\":\"SHA\",\"Bus\":false,\"IsReturn\":false,\"Stopovers\":[],\"CabinInfos\":[{\"CabinLevel\":0,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"U\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":530,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[{\"Must\":false,\"ProductCompanyId\":0,\"ProductId\":25,\"ProductName\":\"商务经济座服务\",\"ProductNum\":1,\"ProductMaxNum\":1,\"ProductPrice\":0,\"ProductType\":7,\"ProductCategory\":null,\"ProductInfo\":\"1238672\",\"AllProductInfo\":\"[{\"Name\":\"U\",\"Id\":25,\"Info\":1238672},{\"Name\":\"X\",\"Id\":25,\"Info\":1238671},{\"Name\":\"T\",\"Id\":25,\"Info\":1238670},{\"Name\":\"Q\",\"Id\":25,\"Info\":1238669},{\"Name\":\"N\",\"Id\":25,\"Info\":1238668},{\"Name\":\"M\",\"Id\":25,\"Info\":1238667},{\"Name\":\"L\",\"Id\":25,\"Info\":1238666},{\"Name\":\"K\",\"Id\":25,\"Info\":1238665},{\"Name\":\"V\",\"Id\":25,\"Info\":1238664},{\"Name\":\"H\",\"Id\":25,\"Info\":1238663},{\"Name\":\"S\",\"Id\":25,\"Info\":1238662},{\"Name\":\"Y1\",\"Id\":25,\"Info\":1238661}]\",\"Discount\":0,\"SeatId\":null,\"SeatNo\":null,\"Selected\":false,\"DownLoadLink\":null,\"IncreaseProductOrderDetailId\":null,\"PaymentStatus\":0}]}]},{\"CabinLevel\":1,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"E\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":470,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[]}]},{\"CabinLevel\":2,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"R3\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":300,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[]}]}],\"SWCabinInfos\":[]}],[{\"Transport\":0,\"LyId\":\"0\",\"No\":\"9C8858\",\"RouteType\":11,\"Channel\":1,\"RouteArea\":1,\"Id\":\"601232\",\"Type\":\"A320\",\"IsDiscount\":false,\"DepartureTime\":\"2015-06-21 12:05:00\",\"DepartureTimeBJ\":\"2015-06-21 12:05:00\",\"Departure\":\"大连\",\"DepartureStation\":\"周水子\",\"DepartureCode\":\"DLC\",\"ArrivalTime\":\"2015-06-21 13:45:00\",\"ArrivalTimeBJ\":\"2015-06-21 13:45:00\",\"Arrival\":\"上海\",\"ArrivalStation\":\"浦东2号航站楼\",\"ArrivalCode\":\"SHA\",\"Bus\":false,\"IsReturn\":false,\"Stopovers\":[],\"CabinInfos\":[{\"CabinLevel\":0,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"U\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":530,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[{\"Must\":false,\"ProductCompanyId\":0,\"ProductId\":25,\"ProductName\":\"商务经济座服务\",\"ProductNum\":1,\"ProductMaxNum\":1,\"ProductPrice\":0,\"ProductType\":7,\"ProductCategory\":null,\"ProductInfo\":\"1482473\",\"AllProductInfo\":\"[{\"Name\":\"U\",\"Id\":25,\"Info\":1482473},{\"Name\":\"X\",\"Id\":25,\"Info\":1482472},{\"Name\":\"T\",\"Id\":25,\"Info\":1482471},{\"Name\":\"Q\",\"Id\":25,\"Info\":1482470},{\"Name\":\"N\",\"Id\":25,\"Info\":1482469},{\"Name\":\"M\",\"Id\":25,\"Info\":1482468},{\"Name\":\"L\",\"Id\":25,\"Info\":1482467},{\"Name\":\"K\",\"Id\":25,\"Info\":1482466},{\"Name\":\"V\",\"Id\":25,\"Info\":1482465},{\"Name\":\"H\",\"Id\":25,\"Info\":1482464},{\"Name\":\"S\",\"Id\":25,\"Info\":1482463},{\"Name\":\"Y1\",\"Id\":25,\"Info\":1482462}]\",\"Discount\":0,\"SeatId\":null,\"SeatNo\":null,\"Selected\":false,\"DownLoadLink\":null,\"IncreaseProductOrderDetailId\":null,\"PaymentStatus\":0}]}]},{\"CabinLevel\":1,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"E\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":470,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[]}]},{\"CabinLevel\":2,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"R3\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":300,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[]}]}],\"SWCabinInfos\":[]}],[{\"Transport\":0,\"LyId\":\"0\",\"No\":\"9C8592\",\"RouteType\":11,\"Channel\":1,\"RouteArea\":1,\"Id\":\"602423\",\"Type\":\"A320\",\"IsDiscount\":false,\"DepartureTime\":\"2015-06-21 18:10:00\",\"DepartureTimeBJ\":\"2015-06-21 18:10:00\",\"Departure\":\"大连\",\"DepartureStation\":\"周水子\",\"DepartureCode\":\"DLC\",\"ArrivalTime\":\"2015-06-21 20:05:00\",\"ArrivalTimeBJ\":\"2015-06-21 20:05:00\",\"Arrival\":\"上海\",\"ArrivalStation\":\"浦东2号航站楼\",\"ArrivalCode\":\"SHA\",\"Bus\":false,\"IsReturn\":false,\"Stopovers\":[],\"CabinInfos\":[{\"CabinLevel\":0,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"U\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":530,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[{\"Must\":false,\"ProductCompanyId\":0,\"ProductId\":25,\"ProductName\":\"商务经济座服务\",\"ProductNum\":1,\"ProductMaxNum\":1,\"ProductPrice\":0,\"ProductType\":7,\"ProductCategory\":null,\"ProductInfo\":\"1472774\",\"AllProductInfo\":\"[{\"Name\":\"U\",\"Id\":25,\"Info\":1472774},{\"Name\":\"X\",\"Id\":25,\"Info\":1472773},{\"Name\":\"T\",\"Id\":25,\"Info\":1472772},{\"Name\":\"Q\",\"Id\":25,\"Info\":1472771},{\"Name\":\"N\",\"Id\":25,\"Info\":1472770},{\"Name\":\"M\",\"Id\":25,\"Info\":1472769},{\"Name\":\"L\",\"Id\":25,\"Info\":1472768},{\"Name\":\"K\",\"Id\":25,\"Info\":1472767},{\"Name\":\"V\",\"Id\":25,\"Info\":1472766},{\"Name\":\"H\",\"Id\":25,\"Info\":1472765},{\"Name\":\"S\",\"Id\":25,\"Info\":1472764},{\"Name\":\"Y1\",\"Id\":25,\"Info\":1472763}]\",\"Discount\":0,\"SeatId\":null,\"SeatNo\":null,\"Selected\":false,\"DownLoadLink\":null,\"IncreaseProductOrderDetailId\":null,\"PaymentStatus\":0}]}]},{\"CabinLevel\":1,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"E\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":470,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[]}]},{\"CabinLevel\":2,\"Cabins\":[{\"CabinId\":null,\"CabinIntegral\":0,\"CabinName\":\"R2\",\"CabinRemain\":0,\"TotalLimit\":0,\"CabinType\":0,\"CabinPrice\":360,\"CabinPricing\":0,\"AirPortFee\":0,\"FuelFee\":0,\"OtherFee\":0,\"CabinBaggage\":0,\"Products\":[]}]}],\"SWCabinInfos\":[]}]],\"IfSuccess\":\"Y\",\"Code\":\"0\",\"MaxDiscountAmount\":null,\"MaxDiscountRate\":null,\"OtherInfo\":null,\"TargetId\":null,\"CustomText\":null}";
        String html = MU.getHtml(param);
        //        html = html.replaceAll("\\\"", "\"");
        System.out.println(html);
        List<FlightInfo> flightinfos = MU.process(html, param);
        System.out.println(JSONObject.toJSONString(flightinfos));
    }

    @Override
    public String getHtml(FlightSearch param) {
        CCSPostMethod post = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String url = "http://flights.ch.com/default/SearchByTime";
        post = new CCSPostMethod(url.toString());
        post.setFollowRedirects(false);
        /**
         * 起飞机场
         */
        String StartAirportCode = param.getStartAirportCode();

        /**
         * 到达机场
         */
        String EndAirportCode = param.getEndAirportCode();
        /**
         * 出发日期 
         */
        String FromDate = param.getFromDate();
        /**
         * 起飞机场名字
         */
        String StartAirPortName = param.getStartAirPortName();
        /**
         * 到达机场名字
         */
        String EndAirPortName = param.getEndAirPortName();

        NameValuePair NameValuePairSType = new NameValuePair("SType", "0");
        NameValuePair NameValuePairIfRet = new NameValuePair("IfRet", "false");
        NameValuePair NameValuePairOriCity = new NameValuePair("OriCity", StartAirPortName);
        NameValuePair NameValuePairDestCity = new NameValuePair("DestCity", EndAirPortName);
        NameValuePair NameValuePairMType = new NameValuePair("MType", "0");
        NameValuePair NameValuePairFDate = new NameValuePair("FDate", FromDate);
        NameValuePair NameValuePairANum = new NameValuePair("ANum", "1");
        NameValuePair NameValuePairCNum = new NameValuePair("CNum", "0");
        NameValuePair NameValuePairINum = new NameValuePair("INum", "0");
        NameValuePair NameValuePairPostType = new NameValuePair("PostType", "");

        NameValuePair[] names = { NameValuePairSType, NameValuePairIfRet, NameValuePairOriCity, NameValuePairDestCity,
                NameValuePairMType, NameValuePairFDate, NameValuePairANum, NameValuePairCNum, NameValuePairINum,
                NameValuePairPostType };
        post.setRequestBody(names);
        String referer = "http://flights.ch.com/" + StartAirportCode + "-" + EndAirportCode + ".html?oricity="
                + StartAirPortName + "&destcity=" + EndAirPortName + "&fdate=" + FromDate
                + "&MType=0&ANum%20=1&CNum%20=0&INum%20=0&SType=0";
        post.setRequestHeader("Referer", referer);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            httpClient.executeMethod(post);
            String responseBody = post.getResponseBodyAsString();
            responseBody = responseBody.replaceAll("flightSearchResultDto =flightSearchResultDto = ", "").trim();
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
        List<FlightInfo> flightinfos = new ArrayList<FlightInfo>();
        JSONObject jsonobject = JSONObject.parseObject(html);
        System.out.println(html);
        if (jsonobject.getJSONArray("Packages").size() == 0) {
            for (int i = 0; i < 5; i++) {
                html = getHtml(param);
                jsonobject = JSONObject.parseObject(html);
                if (jsonobject.getJSONArray("Packages").size() == 0) {
                    continue;
                }
                else {
                    break;
                }
            }
        }
        if (jsonobject.getJSONArray("Packages").size() == 0) {
            return flightinfos;
        }
        JSONArray jsonarray_airRoutingList = jsonobject.getJSONArray("Packages").getJSONArray(0);

        for (int i = 0; i < jsonarray_airRoutingList.size(); i++) {
            FlightInfo flightInfo = new FlightInfo();
            Float basePrice = 0f;
            JSONObject flight_JSONObject = jsonarray_airRoutingList.getJSONObject(i);
            JSONArray CabinInfos_JSONArray = flight_JSONObject.getJSONArray("CabinInfos");
            if (CabinInfos_JSONArray.size() > 0) {
                JSONObject flight = flight_JSONObject;
                //                if ("MU".equals(flight.getString("carrier"))) {
                flightInfo.setAirline(flight.getString("No"));// 航线
                flightInfo.setStartAirport(flight.getString("DepartureCode"));// 起飞机场
                flightInfo.setStartAirportName(flight.getString("DepartureStation"));// 起飞起场名称
                flightInfo.setEndAirport(flight.getString("ArrivalCode"));// 到达机场
                flightInfo.setEndAirportName(flight.getString("ArrivalStation"));// 到达机场名称
                flightInfo.setStartAirportCity(flight.getString("Departure"));// 起飞起场城市名称
                flightInfo.setAirCompany("9C");// 航空公司
                flightInfo.setAirCompanyName("春秋航空");// 航空公司名称airlineName
                flightInfo.setAirportFee(0);// 机场建设费
                flightInfo.setFuelFee(50F);// 燃油费
                //                    flightInfo.setDistance(flight.getString("carrier"));// 里程数
                //                    flightInfo.setMeal(flight.getString("carrier"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSS");
                Date startDate;
                Date arriveDate;
                try {
                    startDate = dateFormat.parse(flight.getString("DepartureTimeBJ"));
                    arriveDate = dateFormat.parse(flight.getString("ArrivalTimeBJ"));
                    //                    if (Integer.valueOf(arriTime) < Integer.valueOf(depTime)) {
                    //                        arriveDate = new Date(arriveDate.getTime() + 24 * 3600 * 1000);
                    //                    }
                    flightInfo.setDepartTime(new Timestamp(startDate.getTime()));// 起飞时间
                    flightInfo.setArriveTime(new Timestamp(arriveDate.getTime()));// 到达时间
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                flightInfo.setIsShare(0);
                flightInfo.setAirplaneType(flight.getString("Type"));// 飞机型号
                flightInfo.setAirplaneTypeDesc("");// 飞机型号描述
                flightInfo.setOffPointAT("");// 出发航站楼
                flightInfo.setBorderPointAT("");// 到达航站楼
                flightInfo.setStop(false);
            }
            else {
                continue;
            }
            JSONArray productInfoList = flight_JSONObject.getJSONArray("CabinInfos");
            List<CarbinInfo> listCabinAll = new ArrayList<CarbinInfo>();
            if (productInfoList.size() > 0) {
                for (int j = 0; j < productInfoList.size(); j++) {
                    JSONObject productInfo = (JSONObject) productInfoList.get(j);
                    JSONArray jsonarray_Cabins = productInfo.getJSONArray("Cabins");
                    if (jsonarray_Cabins.size() == 0) {
                        continue;
                    }
                    JSONObject cabins_productInfo = jsonarray_Cabins.getJSONObject(0);
                    CarbinInfo cabin = new CarbinInfo();
                    cabin.setCabin(cabins_productInfo.getString("CabinName"));
                    cabin.setCabintypename(cabins_productInfo.getString("CabinName"));
                    //                    if (seatStatus != null && seatStatus.equals("A")) {
                    cabin.setSeatNum("9");
                    //                    }
                    //                    else {
                    //                        cabin.setSeatNum(seatStatus);
                    //                    }
                    //                    if (discount >= 0) {

                    cabin.setDiscount(10F);
                    //                    }

                    //                    if (wsSeatWithPriceAndComisionItem.getSeatType() == 3) {
                    //                    cabin.setSpecial(true);
                    //                    }
                    cabin.setPrice(Float.parseFloat(cabins_productInfo.getString("CabinPrice")));
                    if ("Y".equals(productInfo.getString("cabinCode"))) {
                        basePrice = Float.parseFloat(productInfo.getString("priceAmountAdt"));
                    }
                    listCabinAll.add(cabin);
                }
            }
            else {
                continue;
            }
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
}
