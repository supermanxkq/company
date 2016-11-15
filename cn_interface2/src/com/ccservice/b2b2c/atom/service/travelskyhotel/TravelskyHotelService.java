package com.ccservice.b2b2c.atom.service.travelskyhotel;

import java.util.*;
import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Document;
import java.text.SimpleDateFormat;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.atom.service.travelskyhotel.bean.*;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service.travelskyhotel.util.TravelskyHelper;

/**
 * 中航信酒店接口实现类
 * @author WH
 */
public class TravelskyHotelService implements ITravelskyHotelService {

    @SuppressWarnings("unchecked")
    public void travelskyCitys() throws Exception {
        //本地城市
        List<City> citys = Server.getInstance().getHotelService()
                .findAllCity("where C_TYPE = 1 and C_PROVINCEID > 0", "", -1, 0);
        if (citys == null || citys.size() == 0) {
            return;
        }
        Map<String, City> cityMap = new HashMap<String, City>();
        for (City city : citys) {
            cityMap.put(city.getProvinceid().longValue() + "_" + city.getName(), city);
        }
        //XML请求模板
        Document doc = TravelskyHelper.getDocument("CityDetailsSearchRQ");
        //请求XML
        String reqXml = doc.asXML();
        //返回Document
        doc = TravelskyHelper.postXML(reqXml);
        //解析返回的XML
        if (doc != null) {
            String resXml = doc.asXML();
            String checkmsg = TravelskyHelper.hxErrorCheck(doc, "CityDetailsSearchRS", resXml);
            if (!"ResultIsTrue".equals(checkmsg)) {
                return;
            }
            List<Node> cityEles = TravelskyHelper.getNodes(doc, "OTResponse/CityDetailsSearchRS/CityInfos/CityInfo");
            for (Node node : cityEles) {
                /**<CityCode>OHE</CityCode><CityName>漠河</CityName><CityPinYin>MOHE</CityPinYin>
                   <CityPYFW>MH</CityPYFW><Province>黑龙江</Province><CountryCode>CN</CountryCode>*/
                Element ele = (Element) node;
                String CityCode = ele.selectSingleNode("CityCode").getText();
                String CityName = ele.selectSingleNode("CityName").getText();
                String Province = ele.selectSingleNode("Province").getText();
                long hxPid = TravelskyHelper.getProvinceId(Province);
                if (hxPid == 0 || ElongHotelInterfaceUtil.StringIsNull(CityCode)
                        || ElongHotelInterfaceUtil.StringIsNull(CityName)) {
                    continue;
                }
                City local = cityMap.get(hxPid + "_" + CityName);
                if (local == null && (CityName.endsWith("县") || CityName.endsWith("市"))) {
                    CityName = CityName.substring(0, CityName.length() - 1);
                    local = cityMap.get(hxPid + "_" + CityName);
                }
                if (local == null) {
                    local = cityMap.get(hxPid + "_" + CityName + "市");
                    if (local == null) {
                        local = cityMap.get(hxPid + "_" + CityName + "县");
                    }
                }
                if (local != null) {
                    if (ElongHotelInterfaceUtil.StringIsNull(local.getTravelSkyCode())) {
                        local.setTravelSkyCode(CityCode);
                        Server.getInstance().getHotelService().updateCityIgnoreNull(local);
                        System.out.println(hxPid + "---" + Province + "---" + CityName + "---" + CityCode);
                    }
                }
                else {
                    System.out.println(hxPid + "~~~" + Province + "~~~" + CityName + "~~~" + CityCode);
                }
            }
        }
        else {
            throw new Exception("获取HX数据失败，请查看日志。");
        }
    }

    public void travelskyMultiHotels(HXQueryHotelRequest req) throws Exception {
        //验证日期
        checkDate(req);
        //城市
        if (ElongHotelInterfaceUtil.StringIsNull(req.getCityCode())) {
            throw new Exception("请求HX，HX城市编码为空。");
        }
        if (req.getLocalCity() == null || req.getLocalCity().getId() <= 0
                || !req.getCityCode().equals(req.getLocalCity().getTravelSkyCode())) {
            throw new Exception("请求HX，HX城市编码不一致。");
        }
        //页数
        if (req.getPageNum() <= 0) {
            throw new Exception("请求HX，页数错误。");
        }
        //XML请求模板
        Document doc = TravelskyHelper.getDocument("HotelMultiAvailRQ");
        /**替换XML动态内容*/
        //城市编码
        Element ele = TravelskyHelper.getSingleNode(doc,
                "OTRequest/HotelAvailRQ/HotelAvailCriteria/HotelSearchCriteria/HotelRef");
        ele.attribute("CityCode").setText(req.getCityCode());
        //酒店编码
        if (!ElongHotelInterfaceUtil.StringIsNull(req.getHotelCode())) {
            ele.addAttribute("HotelCode", req.getHotelCode().trim());//新增属性 <HotelRef CityCode="城市代码" HotelCode="酒店代码 (非必须)"/>
        }
        //入、离店时间
        ele = TravelskyHelper.getSingleNode(doc, "OTRequest/HotelAvailRQ/HotelAvailCriteria/StayDateRange");
        ele.attribute("CheckInDate").setText(req.getCheckInDate());
        ele.attribute("CheckOutDate").setText(req.getCheckOutDate());
        //分页请求
        try {
            pageRequest(doc, req);
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.errormsg(e));
        }
    }

    @SuppressWarnings("unchecked")
    public List<HXHotelPriceResponse> travelskySingleHotels(HXQueryHotelRequest req) throws Exception {
        //验证日期
        checkDate(req);
        //酒店编码
        if (ElongHotelInterfaceUtil.StringIsNull(req.getHotelCode())) {
            throw new Exception("请求HX，酒店编码为空。");
        }
        if (req.getHotelId() == null || req.getHotelId().longValue() == 0) {
            throw new Exception("请求HX，本地酒店ID为空。");
        }
        //本地酒店
        Hotel localHotel = Server.getInstance().getHotelService().findHotel(req.getHotelId().longValue());
        if (!req.getHotelCode().equals(localHotel.getHotelcode())) {
            throw new Exception("请求HX，酒店编码与本地酒店ID对应酒店的编码不一致。");
        }
        //XML请求模板
        Document doc = TravelskyHelper.getDocument("HotelSingleAvailRQ");
        /**替换XML动态内容*/
        Element ele = TravelskyHelper.getSingleNode(doc,
                "OTRequest/HotelAvailRQ/HotelAvailCriteria/HotelSearchCriteria/HotelRef");
        //酒店编码
        ele.attribute("HotelCode").setText(req.getHotelCode());
        //入、离店时间
        ele = TravelskyHelper.getSingleNode(doc, "OTRequest/HotelAvailRQ/HotelAvailCriteria/StayDateRange");
        ele.attribute("CheckInDate").setText(req.getCheckInDate());
        ele.attribute("CheckOutDate").setText(req.getCheckOutDate());
        //预订天数
        int bookingDays = ElongHotelInterfaceUtil.getSubDays(req.getCheckInDate(), req.getCheckOutDate());
        //请求XML
        String reqXml = doc.asXML();
        //向HX请求
        Document resDoc = TravelskyHelper.postXML(reqXml);
        //解析返回的XML
        if (resDoc != null) {
            String resXml = resDoc.asXML();
            //Response
            List<HXHotelPriceResponse> resList = new ArrayList<HXHotelPriceResponse>();
            //Check
            String checkmsg = TravelskyHelper.hxErrorCheck(resDoc, "HotelAvailRS", resXml);
            if (!"ResultIsTrue".equals(checkmsg)) {
                return resList;
            }
            //本地房型
            String sql = "where C_HOTELID = " + req.getHotelId().longValue();
            List<Roomtype> roomList = Server.getInstance().getHotelService().findAllRoomtype(sql, "", -1, 0);
            Map<String, Roomtype> roomMap = new HashMap<String, Roomtype>();
            for (Roomtype r : roomList) {
                roomMap.put(r.getRoomcode(), r);
            }
            //价格信息
            List<Node> RoomRates = TravelskyHelper.getNodes(resDoc,
                    "OTResponse/HotelAvailRS/RoomStays/RoomStay/RoomRates/RoomRate");
            //价格唯一标示
            List<String> uniqueList = new ArrayList<String>();//唯一标示数据
            List<String> repeatList = new ArrayList<String>();//重复问题数据
            out: for (Node node : RoomRates) {
                try {
                    HXHotelPriceResponse res = new HXHotelPriceResponse();
                    res.setLocalHotel(localHotel);
                    //analy
                    Element RoomRate = (Element) node;
                    String HotelCode = RoomRate.attributeValue("HotelCode");
                    String RoomTypeCode = RoomRate.attributeValue("RoomTypeCode");
                    String Payment = RoomRate.attributeValue("Payment");//支付类型，S：预付
                    String RatePlanCode = RoomRate.attributeValue("RatePlanCode");
                    String RatePlanName = RoomRate.attributeValue("RatePlanName");
                    String VendorCode = RoomRate.attributeValue("VendorCode");
                    if (!req.getHotelCode().equals(HotelCode) || ElongHotelInterfaceUtil.StringIsNull(RoomTypeCode)
                            || !"S".equals(Payment) || ElongHotelInterfaceUtil.StringIsNull(RatePlanCode)
                            || (!ElongHotelInterfaceUtil.StringIsNull(RatePlanName) && RatePlanName.contains("机加酒"))
                            || ElongHotelInterfaceUtil.StringIsNull(VendorCode)) {
                        continue;
                    }
                    res.setHotelCode(HotelCode);
                    res.setRoomTypeCode(RoomTypeCode);
                    Roomtype local = roomMap.get(res.getRoomTypeCode());
                    String RoomTypeName = RoomRate.attributeValue("RoomTypeName");
                    if (ElongHotelInterfaceUtil.StringIsNull(RoomTypeName)) {
                        continue;
                    }
                    res.setRoomTypeName(RoomTypeName.trim());
                    //新增房型
                    if (local == null) {
                        local = new Roomtype();
                        local.setState(1);
                        local.setLanguage(0);
                        local.setHotelid(req.getHotelId().longValue());
                        local.setName(res.getRoomTypeName());
                        local.setRoomcode(res.getRoomTypeCode());
                        local.setRoomdesc(RoomRate.attributeValue("RoomTypeDesc"));
                        local.setBed(TravelskyHelper.BedTypeToLocal(RoomRate.attributeValue("BedType")));
                        local.setLastupdatetime(ElongHotelInterfaceUtil.getCurrentTime());//最后更新时间
                        local = Server.getInstance().getHotelService().createRoomtype(local);
                        roomMap.put(local.getRoomcode(), local);
                    }
                    if (local.getHotelid().longValue() != req.getHotelId().longValue()) {
                        continue;
                    }
                    res.setLocalRoomId(local.getId());
                    res.setBedName(RoomRate.attributeValue("BedType"));
                    res.setBedType(TravelskyHelper.BedTypeToQunar(res.getBedName()));
                    res.setRatePlanCode(RatePlanCode);
                    res.setRatePlanName(RatePlanName);
                    res.setVendorCode(VendorCode);
                    res.setGuestTypeIndicator(RoomRate.attributeValue("GuestTypeIndicator"));
                    res.setBeforeDay(TravelskyHelper.RatePlanNameToDays(RatePlanName, 1));
                    res.setMinDay(TravelskyHelper.RatePlanNameToDays(RatePlanName, 2));
                    //价格唯一标识
                    String uniqueId = local.getId() + "@.@" + RatePlanCode + "@.@" + VendorCode;
                    //已存在
                    if (uniqueList.contains(uniqueId)) {
                        repeatList.add(uniqueId);
                        continue;
                    }
                    else {
                        uniqueList.add(uniqueId);
                    }
                    //宽带
                    String Internet = "N";
                    try {
                        Internet = RoomRate.selectSingleNode("Internet").getText();
                    }
                    catch (Exception e) {
                    }
                    res.setInternet(TravelskyHelper.InternetToInt(Internet));
                    //总价格
                    Element TotalEle = (Element) RoomRate.selectSingleNode("Total");
                    res.setTotalAmountPrice(Double.parseDouble(TotalEle.attributeValue("AmountPrice")));
                    //总房态：酒店是否含available的房型(onRequest：可申请；avail：即时确认；noavail：不可用)
                    String AvailabilityStatus = RoomRate.attributeValue("AvailabilityStatus");
                    //Quotas：房态房量明细
                    Map<String, Element> RoomStatusMap = new HashMap<String, Element>();//key：日期
                    List<Node> Quotas = RoomRate.selectNodes("Quotas/Quota");
                    if (Quotas != null && Quotas.size() > 0) {
                        for (Node Quota : Quotas) {
                            Element RoomStatus = (Element) Quota;
                            RoomStatusMap.put(RoomStatus.attributeValue("Date"), RoomStatus);
                        }
                    }
                    //价格格明细
                    List<Integer> freeMealList = new ArrayList<Integer>();//用于验证早餐是否一致，不一致舍弃
                    Map<String, HXHotelPriceDetail> rates = new HashMap<String, HXHotelPriceDetail>();
                    List<Node> RateEles = RoomRate.selectNodes("Rates/Rate");
                    for (Node Rate : RateEles) {
                        Element rate = (Element) Rate;
                        if (!"CNY".equals(rate.attributeValue("Currency"))) {
                            continue;
                        }
                        String StartDate = rate.attributeValue("StartDate");
                        String EndDate = rate.attributeValue("EndDate");
                        double AmountPrice = Double.parseDouble(rate.attributeValue("AmountPrice"));
                        String FreeMeal = rate.selectSingleNode("FreeMeal") == null ? "0" : rate.selectSingleNode(
                                "FreeMeal").getText();
                        int freeMeal = 0;//早餐(0：无早；1：单早；2：双早；-1：有早)
                        if ("1".equals(FreeMeal) || "2".equals(FreeMeal) || "-1".equals(FreeMeal)) {
                            freeMeal = Integer.parseInt(FreeMeal);
                        }
                        if (!freeMealList.contains(freeMeal)) {
                            freeMealList.add(freeMeal);
                        }
                        if (freeMealList.size() != 1) {
                            break;
                        }
                        int days = ElongHotelInterfaceUtil.getSubDays(StartDate, EndDate) + 1;
                        for (int i = 0; i < days; i++) {
                            String current = ElongHotelInterfaceUtil.getAddDate(StartDate, i);
                            HXHotelPriceDetail detail = new HXHotelPriceDetail();
                            detail.setPriceDate(current);
                            detail.setAmountPrice(AmountPrice);
                            detail.setFreeMeal(freeMeal);
                            //默认房态
                            detail.setRoomStatus(AvailabilityStatus);
                            detail.setQuantity(0);
                            //取明细房态
                            Element RoomStatus = RoomStatusMap.get(current);
                            if (RoomStatus != null) {
                                try {
                                    detail.setRoomStatus(RoomStatus.attributeValue("RoomStatus"));
                                    detail.setQuantity(Integer.parseInt(RoomStatus.attributeValue("Avail")));
                                }
                                catch (Exception e) {
                                }
                            }
                            //日期重复
                            if (rates.containsKey(current)) {
                                continue out;
                            }
                            rates.put(current, detail);
                        }
                    }
                    if (rates.size() == bookingDays && freeMealList.size() == 1) {
                        res.setRates(rates);
                        resList.add(res);
                    }
                }
                catch (Exception e) {
                    System.out.println("解析HX-XML异常：" + ElongHotelInterfaceUtil.errormsg(e));
                }
            }
            if (repeatList.size() > 0 && resList.size() > 0) {
                List<HXHotelPriceResponse> repeatRes = new ArrayList<HXHotelPriceResponse>();
                for (HXHotelPriceResponse res : resList) {
                    String uniqueId = res.getLocalRoomId() + "@.@" + res.getRatePlanCode() + "@.@"
                            + res.getVendorCode();
                    if (repeatList.contains(uniqueId)) {
                        repeatRes.add(res);
                    }
                }
                resList.removeAll(repeatRes);//移除重复的
            }
            return resList;
        }
        else {
            throw new Exception("获取HX数据失败，请查看日志。");
        }
    }

    /**
     * 验证日期
     */
    private void checkDate(HXQueryHotelRequest req) throws Exception {
        //验证
        if (req == null) {
            throw new Exception("请求HX，参数为空。");
        }
        if (ElongHotelInterfaceUtil.StringIsNull(req.getCheckInDate())) {
            throw new Exception("请求HX，入住时间为空。");
        }
        if (ElongHotelInterfaceUtil.StringIsNull(req.getCheckOutDate())) {
            throw new Exception("请求HX，离店时间为空。");
        }
        //入、离店时间验证
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(new Date());
        try {
            if (sdf.parse(req.getCheckInDate()).getTime() < sdf.parse(now).getTime()) {
                throw new Exception("入住日期小于当前日期。");
            }
            if (sdf.parse(req.getCheckInDate()).getTime() >= sdf.parse(req.getCheckOutDate()).getTime()) {
                throw new Exception("离店日期须大于入住日期。");
            }
        }
        catch (Exception e) {
            throw new Exception("请求HX，日期错误。");
        }
    }

    /**
     * 多酒店分页请求
     */
    private void pageRequest(Document reqDoc, HXQueryHotelRequest req) throws Exception {
        //页数
        Element ele = TravelskyHelper.getSingleNode(reqDoc,
                "OTRequest/HotelAvailRQ/HotelAvailCriteria/ReqPageInfo/ReqPageNo");
        ele.setText(Integer.toString(req.getPageNum()));
        //请求XML
        String reqXml = reqDoc.asXML();
        //向HX请求
        Document resDoc = TravelskyHelper.postXML(reqXml);
        //解析返回的XML
        if (resDoc != null) {
            String resXml = resDoc.asXML();
            String checkmsg = TravelskyHelper.hxErrorCheck(resDoc, "HotelAvailRS", resXml);
            if (!"ResultIsTrue".equals(checkmsg)) {
                return;
            }
            //酒店信息
            List<Node> hotelEles = TravelskyHelper.getNodes(resDoc, "OTResponse/HotelAvailRS/RoomStays/RoomStay");
            for (Node node : hotelEles) {
                try {
                    Element HotelEle = (Element) node;
                    //酒店信息
                    Element BasicProperty = (Element) HotelEle.selectSingleNode("BasicProperty");
                    Hotel hotel = analyHotelInfo(BasicProperty, req.getLocalCity());
                    if (hotel == null || hotel.getId() <= 0) {
                        continue;
                    }
                    //房型信息
                    Element RoomTypes = (Element) HotelEle.selectSingleNode("RoomTypes");
                    analyRoomInfo(hotel, RoomTypes);
                }
                catch (Exception e) {
                    System.out.println("解析HX-XML异常：" + ElongHotelInterfaceUtil.errormsg(e));
                }
            }
            //分页信息
            Element pageEle = TravelskyHelper.getSingleNode(resDoc, "OTResponse/HotelAvailRS/RespPageInfo");
            //总页数
            String TotalPageNum = pageEle.selectSingleNode("TotalPageNum").getText();
            if (req.getPageNum() < Integer.parseInt(TotalPageNum)) {
                req.setPageNum(req.getPageNum() + 1);
                //继续请求
                pageRequest(reqDoc, req);
            }
        }
        else {
            throw new Exception("获取HX数据失败，请查看日志。");
        }
    }

    /**
     * 酒店静态信息
     */
    @SuppressWarnings("unchecked")
    private Hotel analyHotelInfo(Element ele, City city) throws Exception {
        Hotel hotel = new Hotel();
        //酒店编码
        String HotelCode = ele.attributeValue("HotelCode");
        //查询本地酒店
        boolean newflag = false;
        String sql = "where C_SOURCETYPE = 12 and C_HOTELCODE = '" + HotelCode + "'";
        List<Hotel> hotelList = Server.getInstance().getHotelService().findAllHotel(sql, "", 1, 0);
        if (hotelList != null && hotelList.size() > 0) {
            hotel = hotelList.get(0);
        }
        else {
            newflag = true;
        }
        //航信酒店属性
        String HotelName = ele.attributeValue("HotelName").trim();
        String HotelEnglishName = ele.attributeValue("HotelEnglishName");
        int Rank = TravelskyHelper.RankToLocal(ele.selectSingleNode("Rank").getText());
        String Address = ele.selectSingleNode("Address").getText();
        String Tel = ele.selectSingleNode("Tel").getText();
        String PostCode = ele.selectSingleNode("PostCode") == null ? "" : ele.selectSingleNode("PostCode").getText();
        String LongDesc = ele.selectSingleNode("LongDesc").getText();
        double MinRate = 0;
        try {
            MinRate = Double.parseDouble(ele.selectSingleNode("MinRate").getText());
        }
        catch (Exception e) {
        }
        double Latitude = 0, Longitude = 0;
        try {
            Element PositionEle = (Element) ele.selectSingleNode("Position");
            Latitude = Double.parseDouble(PositionEle.attributeValue("Latitude"));
            Longitude = Double.parseDouble(PositionEle.attributeValue("Longitude"));
        }
        catch (Exception e) {
        }
        //判断是否更新
        boolean updateflag = false;
        if (!newflag) {
            //原酒店属性
            String oldName = hotel.getName();
            String oldEname = hotel.getEnname();
            String oldStar = Integer.toString(hotel.getStar() == null ? 0 : hotel.getStar().intValue());
            String oldAddress = hotel.getAddress();
            String oldTel = hotel.getMarkettell();
            String oldPostCode = hotel.getPostcode();
            String oldDesc = hotel.getDescription();
            String oldStartPrice = Double.toString(hotel.getStartprice() == null ? 0 : hotel.getStartprice()
                    .doubleValue());
            String oldLat = Double.toString(hotel.getLat() == null ? 0 : hotel.getLat().doubleValue());
            String oldLng = Double.toString(hotel.getLng() == null ? 0 : hotel.getLng().doubleValue());
            String oldStatus = Integer.toString(hotel.getState() == null ? 0 : hotel.getState().intValue());
            //判断是否更新
            updateflag = checkStrChange(updateflag, oldName, HotelName);
            updateflag = checkStrChange(updateflag, oldEname, HotelEnglishName);
            updateflag = checkStrChange(updateflag, oldStar, Integer.toString(Rank));
            updateflag = checkStrChange(updateflag, oldAddress, Address);
            updateflag = checkStrChange(updateflag, oldTel, Tel);
            updateflag = checkStrChange(updateflag, oldPostCode, PostCode);
            updateflag = checkStrChange(updateflag, oldDesc, LongDesc);
            updateflag = checkStrChange(updateflag, oldStartPrice, Double.toString(MinRate));
            updateflag = checkStrChange(updateflag, oldLat, Double.toString(Latitude));
            updateflag = checkStrChange(updateflag, oldLng, Double.toString(Longitude));
            updateflag = checkStrChange(updateflag, oldStatus, "3");
        }
        //赋值
        hotel.setHotelcode(HotelCode);
        hotel.setSourcetype(12l);//酒店来源  12：中航信
        hotel.setPaytype(2l);//预付
        hotel.setType(1);//国内
        hotel.setState(3);//可用
        hotel.setLanguage(0);
        hotel.setCityid(city.getId());
        hotel.setProvinceid(city.getProvinceid());
        hotel.setCountryid(city.getCountryid());
        //航信属性
        hotel.setName(HotelName);//酒店名称
        hotel.setEnname(HotelEnglishName);//英文名称
        hotel.setStar(Rank);//星级
        hotel.setAddress(Address);//地址
        hotel.setMarkettell(Tel);//电话
        hotel.setPostcode(PostCode);//邮编
        hotel.setDescription(LongDesc);//描述
        hotel.setStartprice(MinRate);//最低价格
        //坐标
        if (Latitude > 0 && Longitude > 0) {
            hotel.setLat(Latitude);
            hotel.setLng(Longitude);
        }
        hotel.setLastupdatetime(ElongHotelInterfaceUtil.getCurrentTime());//最后更新时间
        if (newflag) {
            hotel = Server.getInstance().getHotelService().createHotel(hotel);
            System.out.println("=====新增航信酒店=====" + hotel.getName());
        }
        else if (updateflag) {
            Server.getInstance().getHotelService().updateHotelIgnoreNull(hotel);
            System.out.println("=====更新航信酒店=====" + hotel.getName());
        }
        return hotel;
    }

    /**
     * 房型静态信息
     */
    @SuppressWarnings("unchecked")
    private void analyRoomInfo(Hotel hotel, Element ele) {
        //本地房型
        String sql = "where C_HOTELID = " + hotel.getId();
        List<Roomtype> roomList = Server.getInstance().getHotelService().findAllRoomtype(sql, "", -1, 0);
        Map<String, Roomtype> roomMap = new HashMap<String, Roomtype>();
        for (Roomtype r : roomList) {
            roomMap.put(r.getRoomcode(), r);
        }
        //航信房型
        List<Node> RoomTypes = ele.selectNodes("RoomType");
        for (Node node : RoomTypes) {
            try {
                Element RoomEle = (Element) node;
                //房型名称
                String RoomTypeName = RoomEle.attributeValue("RoomTypeName");
                //房型编码
                String RoomTypeCode = RoomEle.attributeValue("RoomTypeCode");
                if (ElongHotelInterfaceUtil.StringIsNull(RoomTypeName)
                        || ElongHotelInterfaceUtil.StringIsNull(RoomTypeCode)) {
                    continue;
                }
                Roomtype room = roomMap.get(RoomTypeCode);
                //新增
                boolean newflag = false;
                if (room == null) {
                    newflag = true;
                    room = new Roomtype();
                }
                //航信房型属性
                String RoomArea = RoomEle.attributeValue("RoomArea");
                if (!ElongHotelInterfaceUtil.StringIsNull(RoomArea)) {
                    RoomArea = RoomArea.trim();
                    if (RoomArea.endsWith("平方米")) {
                        RoomArea = RoomArea.substring(0, RoomArea.length() - 3);
                    }
                    else if (RoomArea.endsWith("平米")) {
                        RoomArea = RoomArea.substring(0, RoomArea.length() - 2);
                    }
                    else if (RoomArea.endsWith("平")) {
                        RoomArea = RoomArea.substring(0, RoomArea.length() - 1);
                    }
                    else if (RoomArea.endsWith("(建筑面积)")) {
                        RoomArea = RoomArea.substring(0, RoomArea.length() - 6);
                    }
                    else if ("不详".equals(RoomArea)) {
                        RoomArea = "";
                    }
                }
                String RoomDescription = RoomEle.selectSingleNode("RoomDescription") == null ? "" : RoomEle
                        .selectSingleNode("RoomDescription").getText();
                String Floor = "-".equals(RoomEle.attributeValue("Floor")) ? "" : RoomEle.attributeValue("Floor");
                if (!ElongHotelInterfaceUtil.StringIsNull(Floor)) {
                    Floor = Floor.trim();
                    if (Floor.endsWith("层") || Floor.endsWith("楼") || Floor.endsWith("F")) {
                        Floor = Floor.substring(0, Floor.length() - 1);
                    }
                    else if ("不详".equals(Floor) || "贵宾".equals(Floor) || "别墅".equals(Floor)) {
                        Floor = "";
                    }
                }
                int BedType = TravelskyHelper.BedTypeToLocal(RoomEle.attributeValue("BedType"));
                //判断是否更新
                boolean updateflag = false;
                if (!newflag) {
                    //原房型属性
                    String oldName = room.getName();
                    String oldArea = room.getAreadesc();
                    String oldDesc = room.getRoomdesc();
                    String oldFloor = room.getLayer();//楼层
                    String oldBed = Integer.toString(room.getBed() == null ? 0 : room.getBed().intValue());
                    //判断是否更新
                    updateflag = checkStrChange(updateflag, oldName, RoomTypeName);
                    updateflag = checkStrChange(updateflag, oldArea, RoomArea);
                    updateflag = checkStrChange(updateflag, oldDesc, RoomDescription);
                    updateflag = checkStrChange(updateflag, oldFloor, Floor);
                    updateflag = checkStrChange(updateflag, oldBed, Integer.toString(BedType));
                }
                room.setState(1);
                room.setLanguage(0);
                room.setHotelid(hotel.getId());
                room.setName(RoomTypeName);
                room.setRoomcode(RoomTypeCode);
                room.setAreadesc(RoomArea);
                room.setRoomdesc(RoomDescription);
                room.setLayer(Floor);
                room.setBed(BedType);
                room.setLastupdatetime(ElongHotelInterfaceUtil.getCurrentTime());//最后更新时间
                if (newflag) {
                    room = Server.getInstance().getHotelService().createRoomtype(room);
                    roomMap.put(room.getRoomcode(), room);
                    System.out.println("新增航信房型：" + hotel.getName() + "---" + room.getName());
                }
                else if (updateflag) {
                    Server.getInstance().getHotelService().updateRoomtype(room);
                    System.out.println("更新航信房型：" + hotel.getName() + "---" + room.getName());
                }
            }
            catch (Exception e) {
                System.out.println("解析HX-XML-ROOM异常：" + ElongHotelInterfaceUtil.errormsg(e));
            }
        }
    }

    //通过字符判断是否更新
    private boolean checkStrChange(boolean updateflag, String oldVal, String newVal) {
        if (!updateflag) {
            oldVal = ElongHotelInterfaceUtil.StringIsNull(oldVal) ? "" : oldVal.trim();
            newVal = ElongHotelInterfaceUtil.StringIsNull(newVal) ? "" : newVal.trim();
            if (!oldVal.equalsIgnoreCase(newVal)) {
                updateflag = true;
            }
        }
        return updateflag;
    }
}