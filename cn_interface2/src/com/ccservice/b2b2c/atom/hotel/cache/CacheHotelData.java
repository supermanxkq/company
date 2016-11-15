package com.ccservice.b2b2c.atom.hotel.cache;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.atom.qunar.PHUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.landmark.Landmark;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.atom.component.BaseCache;

import com.ccservice.b2b2c.base.hotelall.Hotelall;
import com.ccservice.b2b2c.base.chaininfo.Chaininfo;
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.ccservice.b2b2c.base.hotellandtype.HotelLandType;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class CacheHotelData {

    private static BaseCache BaseDataCache = new BaseCache("H", Integer.MAX_VALUE);

    public CacheHotelData() {
        // 这个根据配置文件来，初始BaseCache而已;
        // 缓存时间设置为1个月
    }

    /**
     * 创建缓存的统一方法,没有就创建，有就获取
     * @param cacheindex 缓存索引
     * @param obj 放入缓存的对象
     */
    public Object putHCache(String cacheindex, Object obj) throws NeedsRefreshException {
        try {
            obj = BaseDataCache.get(cacheindex);
            return obj;
        }
        catch (Exception e) {
            BaseDataCache.put(cacheindex, obj);
            return obj;
        }
    }

    /**
     * 删除缓存
     */
    public void delHCache(String cacheindex) throws NeedsRefreshException {
        try {
            BaseDataCache.remove(cacheindex);
        }
        catch (Exception e) {
        }
    }

    /**
     * 获取缓存
     */
    public Object getHCache(String cacheindex) throws NeedsRefreshException {
        Object obj = new Object();
        try {
            obj = BaseDataCache.get(cacheindex);
            return obj;
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * 加载酒店图片
     */
    @SuppressWarnings("unchecked")
    public List<Hotelimage> getHotelimage(String Hotelid) throws NeedsRefreshException {
        List<Hotelimage> hotelimages = new ArrayList<Hotelimage>();
        try {
            hotelimages = (List<Hotelimage>) BaseDataCache.get(HotelCacheIndex.HOTELIMAGE + "_" + Hotelid);
            return hotelimages;
        }
        catch (Exception e) {
            hotelimages = Server
                    .getInstance()
                    .getHotelService()
                    .findAllHotelimage("where (c_sizetype = 1 or c_sizetype is null) and c_hotelid=" + Hotelid, "", -1,
                            0);
            if (hotelimages != null && hotelimages.size() > 0) {
                Hotel h = Server.getInstance().getHotelService().findHotel(Long.parseLong(Hotelid));
                for (int i = 0; i < hotelimages.size(); i++) {
                    Hotelimage img = hotelimages.get(i);
                    if (img.getIsNewFlag() != null) {
                        //艺龙
                        if (img.getIsNewFlag().intValue() == 1) {
                            img.setImageSourcetype(1l);
                        }
                        //去哪儿
                        else if (img.getIsNewFlag().intValue() == 2) {
                            img.setImageSourcetype(168l);
                        }
                    }
                    if (img.getImageSourcetype() == null)
                        img.setImageSourcetype(h.getSourcetype());
                }
                BaseDataCache.put(HotelCacheIndex.HOTELIMAGE + "_" + Hotelid, hotelimages);
            }
            return hotelimages;
        }
    }

    /**
     * 获取正式酒店图片
     * @param zsHotelId 正式酒店ID
     */
    @SuppressWarnings("unchecked")
    public List<Hotelimage> getZsHotelImage(long zsHotelId) throws NeedsRefreshException {
        try {
            return (List<Hotelimage>) BaseDataCache.get(HotelCacheIndex.ZSHOTELIMAGE + "_" + zsHotelId);
        }
        catch (Exception e) {
            //正式酒店图片
            List<Hotelimage> hotelimages = Server.getInstance().getHotelService()
                    .findAllHotelimage("where c_sizetype = 1 and c_zshotelid = " + zsHotelId, "", -1, 0);
            boolean flag = false;
            if (hotelimages == null || hotelimages.size() == 0) {
                //取临时酒店酒店
                List<Hotel> allHotels = Server.getInstance().getHotelService()
                        .findAllHotel("where c_zshotelid = " + zsHotelId, "order by c_sourcetype", -1, 0);
                for (Hotel h : allHotels) {
                    hotelimages = getHotelimage(Long.toString(h.getId()));
                    if (hotelimages != null && hotelimages.size() > 0) {
                        flag = true;
                        break;
                    }
                }
            }
            if (hotelimages != null && hotelimages.size() > 0) {
                if (!flag) {
                    for (int i = 0; i < hotelimages.size(); i++) {
                        Hotelimage img = hotelimages.get(i);
                        if (img.getIsNewFlag() != null) {
                            //艺龙
                            if (img.getIsNewFlag().intValue() == 1) {
                                img.setImageSourcetype(1l);
                            }
                            //去哪儿
                            else if (img.getIsNewFlag().intValue() == 2) {
                                img.setImageSourcetype(168l);
                            }
                        }
                        //目前理论不可能
                        if (img.getImageSourcetype() == null && img.getHotelid() != null
                                && img.getHotelid().longValue() > 0) {
                            Hotel h = Server.getInstance().getHotelService().findHotel(img.getHotelid());
                            img.setImageSourcetype(h.getSourcetype());
                        }
                    }
                }
                BaseDataCache.put(HotelCacheIndex.ZSHOTELIMAGE + "_" + zsHotelId, hotelimages);
            }
            return hotelimages;
        }
    }

    /**
     * 获取酒店列表图片
     * @param zsHotelId 正式酒店ID
     */
    @SuppressWarnings("unchecked")
    public Hotelimage getListHotelImage(long zsHotelId) throws NeedsRefreshException {
        try {
            return (Hotelimage) BaseDataCache.get(HotelCacheIndex.LISTHOTELIMAGE + "_" + zsHotelId);
        }
        catch (Exception e) {
            Hotelimage img = new Hotelimage();
            //取小图
            List<Hotelimage> hotelimages = Server.getInstance().getHotelService()
                    .findAllHotelimage("where c_sizetype = 3 and c_zshotelid = " + zsHotelId, "", -1, 0);
            //取大图
            boolean flag = false;
            if (hotelimages == null || hotelimages.size() == 0) {
                flag = true;
                hotelimages = getZsHotelImage(zsHotelId);
            }
            if (hotelimages != null && hotelimages.size() > 0) {
                img = hotelimages.get(0);
                if (!flag) {
                    if (img.getIsNewFlag() != null) {
                        //艺龙
                        if (img.getIsNewFlag().intValue() == 1) {
                            img.setImageSourcetype(1l);
                        }
                        //去哪儿
                        else if (img.getIsNewFlag().intValue() == 2) {
                            img.setImageSourcetype(168l);
                        }
                    }
                    if (img.getImageSourcetype() == null && img.getHotelid() != null
                            && img.getHotelid().longValue() > 0) {
                        Hotel h = Server.getInstance().getHotelService().findHotel(img.getHotelid());
                        img.setImageSourcetype(h.getSourcetype());
                    }
                }
                BaseDataCache.put(HotelCacheIndex.LISTHOTELIMAGE + "_" + zsHotelId, img);
            }
            return img;
        }
    }

    /**
     * 所有城市
     * 
     * @throws NeedsRefreshException
     */
    public String getAllCityCount() throws NeedsRefreshException {
        String citycount = "0";
        try {
            citycount = (String) BaseDataCache.get("citycount");
            return citycount;
        }
        catch (Exception e) {
            String citysql = " SELECT COUNT(ID) FROM " + City.TABLE + " where " + City.COL_type + "=1";
            citycount = Server.getInstance().getHotelService().countCityBySql(citysql) + "";
            BaseDataCache.put("citycount", citycount);
            return citycount;
        }
    }

    /**
     * 所有酒店数量
     */
    public int getAllHotelCount() throws NeedsRefreshException {
        int hotelcount = 0;
        try {
            hotelcount = (Integer) BaseDataCache.get("hotelcount");
            return hotelcount;
        }
        catch (Exception e) {
            String hotelsql = " SELECT COUNT(ID) FROM " + Hotelall.TABLE + " where " + Hotelall.COL_type + "=1";
            hotelcount = Server.getInstance().getHotelService().countHotelallBySql(hotelsql);
            BaseDataCache.put("hotelcount", hotelcount);
            return hotelcount;
        }
    }

    /**
     * 连锁酒店
     * 
     * @throws NeedsRefreshException
     */
    @SuppressWarnings("unchecked")
    public List<Chaininfo> getListChainInfo() throws NeedsRefreshException {
        List<Chaininfo> ListChaininfo = new ArrayList<Chaininfo>();
        try {
            ListChaininfo = (List<Chaininfo>) BaseDataCache.get("chaininfo");
            return ListChaininfo;
        }
        catch (Exception e) {
            String whereChaininfo = " where 1=1 and " + Chaininfo.COL_total
                    + " >30 and C_IMAGEPIC is not null and  C_NAME not like '%7天%'";// 不能显示七天的连锁酒店
            ListChaininfo = Server.getInstance().getHotelService()
                    .findAllChaininfo(whereChaininfo, " ORDER BY C_TOTAL ", 14, 0);
            BaseDataCache.put("chaininfo", ListChaininfo);
            return ListChaininfo;
        }
    }

    @SuppressWarnings("unchecked")
    public Integer getWindFlag(String hotelid, String roomcode) throws NeedsRefreshException {
        Integer flag = 2;
        try {
            flag = (Integer) BaseDataCache.get(hotelid + "_" + roomcode);
            return flag;
        }
        catch (Exception e) {
            Roomtype roomtype = null;
            String where = "where " + Roomtype.COL_hotelid + "=" + hotelid + " and " + Roomtype.COL_roomcode + "='"
                    + roomcode + "'";
            List<Roomtype> roomtypes = Server.getInstance().getHotelService().findAllRoomtype(where, "", 1, 0);
            if (roomtypes.size() > 0) {
                roomtype = roomtypes.get(0);
                BaseDataCache.put(hotelid + "_" + roomcode, roomtype.getWideband());
                return roomtype.getWideband();
            }
            else {
                BaseDataCache.put(hotelid + "_" + roomcode, flag);
                return flag;
            }
        }
    }

    //手机客户端缓存start
    /**
     * 酒店品牌、行政区、商业区、机场/车站、地铁线
     */
    @SuppressWarnings("unchecked")
    public String getHotelKeyWordJson(int type, long cityid, String reqfrom) throws NeedsRefreshException {
        String result = "";
        try {
            //酒店国家
            if (type == -1) {
                result = (String) BaseDataCache.get("HotelCountryJson");
            }
            //酒店国内城市
            if (type == 0) {
                result = (String) BaseDataCache.get("HotelDomesticCityJson");
            }
            //酒店品牌
            if (type == 1) {
                if ("B2B".equals(reqfrom) || "B2C".equals(reqfrom)) {
                    result = (String) BaseDataCache.get("HotelBrandContainLogoJson");
                }
                else {
                    result = (String) BaseDataCache.get("HotelBrandJson");
                }
            }
            //商业区
            if (type == 2) {
                result = (String) BaseDataCache.get("HotelBusinessZoneJson_" + cityid);
            }
            //机场/车站
            if (type == 3) {
                result = (String) BaseDataCache.get("HotelAirportAndRailwayJson_" + cityid);
            }
            //行政区
            if (type == 4) {
                result = (String) BaseDataCache.get("HotelDistrictJson_" + cityid);
            }
            //地铁线
            if (type == 5) {
                result = (String) BaseDataCache.get("HotelSubwayJson_" + cityid);
            }
            return result;
        }
        catch (Exception e) {
            JSONArray jsonlist = new JSONArray();
            //酒店国家
            if (type == -1) {
                List<Country> countrys = Server.getInstance().getInterHotelService()
                        .findAllCountry("where c_zhname is not null", "order by c_zhname", -1, 0);
                for (Country c : countrys) {
                    JSONObject json = new JSONObject();
                    json.put("cid", c.getId());
                    json.put("name", c.getZhname());
                    json.put("ename", ElongHotelInterfaceUtil.StringIsNull(c.getName()) ? "" : c.getName()
                            .toLowerCase().trim());
                    jsonlist.add(json.toString());
                }
                BaseDataCache.put("HotelCountryJson", jsonlist.toString());
                return jsonlist.toString();
            }
            //酒店国内城市
            if (type == 0) {
                //Search
                String sql = "where C_TYPE = 1 and C_NAME is not null and LTRIM(RTRIM(C_NAME)) != ''";
                String orderby = "order by case when C_ENNAME is null then 1 else 0 end , C_ENNAME";
                List<City> citys = Server.getInstance().getHotelService().findAllCity(sql, orderby, -1, 0);
                //Hot
                sql = "SELECT TOP 8 * FROM T_CITY WHERE C_ENNAME IS NOT NULL AND C_ELONGCITYID IS NOT NULL AND C_SORT IS NOT NULL ORDER BY C_SORT";
                List<City> hots = Server.getInstance().getHotelService().findAllCityBySql(sql, -1, 0);
                if (citys != null && citys.size() > 0) {
                    //Hot
                    Map<Long, Boolean> maps = new HashMap<Long, Boolean>();
                    for (City c : hots) {
                        if (ElongHotelInterfaceUtil.StringIsNull(c.getName())
                                || ElongHotelInterfaceUtil.StringIsNull(c.getEnname()))
                            continue;
                        maps.put(c.getId(), true);
                    }
                    for (City c : citys) {
                        if (ElongHotelInterfaceUtil.StringIsNull(c.getName()))
                            continue;
                        JSONObject json = new JSONObject();
                        json.put("cityid", c.getId());
                        json.put("name", c.getName().trim());
                        String pinyin = c.getEnname();
                        if (ElongHotelInterfaceUtil.StringIsNull(pinyin)) {
                            pinyin = "z";
                        }
                        else {
                            pinyin = pinyin.trim().toLowerCase();
                        }
                        json.put("pinyin", pinyin);
                        //Hot
                        if (maps.get(c.getId()) != null && maps.get(c.getId())) {
                            json.put("type", "hot");
                        }
                        else {
                            json.put("type", pinyin.substring(0, 1));
                        }
                        jsonlist.add(json.toString());
                    }
                    BaseDataCache.put("HotelDomesticCityJson", jsonlist.toString());
                    return jsonlist.toString();
                }
            }
            //酒店品牌
            if (type == 1) {
                String sql = "where C_TOTAL > 20 order by cast(C_TOTAL as DECIMAL(200)) desc";
                List<Chaininfo> chains = Server.getInstance().getHotelService().findAllChaininfo(sql, "", -1, 0);
                if (chains != null && chains.size() > 0) {
                    for (Chaininfo c : chains) {
                        JSONObject json = new JSONObject();
                        json.put("id", c.getId());
                        json.put("name", c.getName().trim());
                        if ("B2B".equals(reqfrom) || "B2C".equals(reqfrom)) {
                            json.put("logo", c.getImagepic2());
                        }
                        json.put("subway", "[]");//地铁站数组置为空
                        jsonlist.add(json.toString());
                    }
                    result = getResult(type, jsonlist);
                }
                if ("B2B".equals(reqfrom) || "B2C".equals(reqfrom)) {
                    BaseDataCache.put("HotelBrandContainLogoJson", result);
                }
                else {
                    BaseDataCache.put("HotelBrandJson", result);
                }
            }
            //商业区、行政区
            if (type == 2 || type == 4) {
                City city = Server.getInstance().getHotelService().findCity(cityid);
                if (city == null || city.getType() == null || city.getType().longValue() != 1) {
                    return "";
                }
                String sql = "where C_NAME not like '%nil%' and C_CITYID = " + cityid;
                if (type == 2) {
                    sql += " and  C_TYPE = 1";
                }
                else {
                    sql += " and  C_TYPE = 2";
                }
                String orderby = "order by case when C_CITYINDEX is null then 1 else 0 end , C_CITYINDEX , C_NAME";
                List<Region> regions = Server.getInstance().getHotelService().findAllRegion(sql, orderby, -1, 0);
                if (regions != null && regions.size() > 0) {
                    for (Region r : regions) {
                        if (ElongHotelInterfaceUtil.StringIsNull(r.getName())
                                || "null".equals(r.getName().trim().toLowerCase()))
                            continue;
                        JSONObject json = new JSONObject();
                        json.put("id", r.getId());
                        json.put("name", r.getName().trim());
                        json.put("subway", "[]");//地铁站数组置为空
                        jsonlist.add(json.toString());
                    }
                    result = getResult(type, jsonlist);
                }
                if (type == 2) {
                    BaseDataCache.put("HotelBusinessZoneJson_" + cityid, result);
                }
                else {
                    BaseDataCache.put("HotelDistrictJson_" + cityid, result);
                }
            }
            //机场、车站
            if (type == 3) {
                City city = Server.getInstance().getHotelService().findCity(cityid);
                if (city == null || city.getType() == null || city.getType().longValue() != 1) {
                    return "";
                }
                String sql = "where C_TYPE in (3,4) and C_CITYID = " + cityid;
                String orderby = "order by case when C_CITYINDEX is null then 1 else 0 end , C_CITYINDEX , C_NAME";
                List<Landmark> marks = Server.getInstance().getHotelService().findAllLandmark(sql, orderby, -1, 0);
                if (marks != null && marks.size() > 0) {
                    for (Landmark m : marks) {
                        JSONObject json = new JSONObject();
                        json.put("id", m.getId());
                        json.put("name", m.getName().trim());
                        json.put("subway", "[]");//地铁站数组置为空
                        jsonlist.add(json.toString());
                    }
                    result = getResult(type, jsonlist);
                }
                BaseDataCache.put("HotelAirportAndRailwayJson_" + cityid, result);
            }
            //地铁
            if (type == 5) {
                City city = Server.getInstance().getHotelService().findCity(cityid);
                if (city == null || city.getType() == null || city.getType().longValue() != 1) {
                    return "";
                }
                //地铁线路
                String sql = "where C_PARENTID = 2 and C_CITYID = " + cityid;
                List<HotelLandType> types = Server.getInstance().getHotelService().findAllHotelLandType(sql, "", -1, 0);
                if (types != null && types.size() > 0) {
                    for (HotelLandType t : types) {
                        //地铁站
                        String tempsql = "where C_CITYID = " + cityid + " and C_TYPE = " + t.getId();
                        String orderby = "ORDER BY CASE WHEN C_SUBWAYINDEX IS null THEN 1 ELSE 0 END , C_SUBWAYINDEX";
                        List<Landmark> subways = Server.getInstance().getHotelService()
                                .findAllLandmark(tempsql, orderby, -1, 0);
                        if (subways != null && subways.size() > 0) {
                            JSONObject json = new JSONObject();
                            json.put("id", t.getId());
                            json.put("name", t.getName().trim());
                            //subway
                            JSONArray jsonArray = new JSONArray();
                            for (Landmark s : subways) {
                                JSONObject sjson = new JSONObject();
                                sjson.put("subwayid", s.getId());
                                sjson.put("subwayname", s.getName().trim());
                                jsonArray.add(sjson.toString());
                            }
                            json.put("subway", jsonArray.toString());
                            jsonlist.add(json.toString());
                        }
                    }
                    result = getResult(type, jsonlist);
                }
                BaseDataCache.put("HotelSubwayJson_" + cityid, result);
            }
        }
        return result;
    }

    private String getResult(int type, JSONArray jsonlist) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("info", jsonlist.toString());
        return json.toString();
    }

    //手机客户端缓存end

    /**
     * 酒店静态信息缓存
     * 酒店国内城市、品牌、商圈、行政区、火车站/机场、地铁站
     * @param type 类型 1：酒店城市；2：品牌；3：商圈；4：行政区；5：火车站/机场；6：地铁站；7：地铁线路；8：景点；9：大学；10：医院；11：展馆会场
     * @param cityid 城市ID type为1、2时，城市ID不用传
     */
    @SuppressWarnings("unchecked")
    public List getHotelStaticData(int type, long cityid) throws NeedsRefreshException {
        //地标索引
        String LandmarkIndex = "";
        if (type == 5) {
            LandmarkIndex = HotelCacheIndex.HotelAirportAndRailway;
        }
        else if (type == 6) {
            LandmarkIndex = HotelCacheIndex.HotelSubwayStation;
        }
        else if (type == 8) {
            LandmarkIndex = HotelCacheIndex.HotelScenic;
        }
        else if (type == 9) {
            LandmarkIndex = HotelCacheIndex.HotelUniversity;
        }
        else if (type == 10) {
            LandmarkIndex = HotelCacheIndex.HotelHospital;
        }
        else if (type == 11) {
            LandmarkIndex = HotelCacheIndex.HotelExhibition;
        }
        try {
            if (type == 1) {
                return (List<City>) BaseDataCache.get(HotelCacheIndex.HotelDomesticCity);
            }
            if (type == 2) {
                return (List<Chaininfo>) BaseDataCache.get(HotelCacheIndex.HotelChaininfo);
            }
            if (type == 3) {
                return (List<Region>) BaseDataCache.get(HotelCacheIndex.HotelBusinessZone + "_" + cityid);
            }
            if (type == 4) {
                return (List<Region>) BaseDataCache.get(HotelCacheIndex.HotelDistrict + "_" + cityid);
            }
            if (type == 7) {
                return (List<HotelLandType>) BaseDataCache.get(HotelCacheIndex.HotelSubwayLine + "_" + cityid);
            }
            if (!ElongHotelInterfaceUtil.StringIsNull(LandmarkIndex)) {
                return (List<Landmark>) BaseDataCache.get(LandmarkIndex + "_" + cityid);
            }
        }
        catch (Exception e) {
            //城市
            if (type == 1) {
                String sql = "where C_TYPE = 1 and C_NAME is not null and LTRIM(RTRIM(C_NAME)) != ''";
                String orderby = "order by case when C_SORT is null then 1 else 0 end , C_SORT";
                List<City> list = Server.getInstance().getHotelService().findAllCity(sql, orderby, -1, 0);
                if (list == null || list.size() == 0)
                    list = new ArrayList<City>();
                BaseDataCache.put(HotelCacheIndex.HotelDomesticCity, list);
                return list;
            }
            //品牌
            if (type == 2) {
                String sql = "where C_TOTAL > 20";
                String orderby = "order by cast(C_TOTAL as DECIMAL(200)) desc";
                List<Chaininfo> list = Server.getInstance().getHotelService().findAllChaininfo(sql, orderby, 50, 0);
                if (list == null || list.size() == 0)
                    list = new ArrayList<Chaininfo>();
                BaseDataCache.put(HotelCacheIndex.HotelChaininfo, list);
                return list;
            }
            //商圈、行政区
            if (type == 3 || type == 4) {
                String sql = "where C_NAME not like '%nil%' and C_CITYID = " + cityid;
                if (type == 3) {
                    sql += " and  C_TYPE = 1";
                }
                else {
                    sql += " and  C_TYPE = 2";
                }
                String orderby = "order by case when C_CITYINDEX is null then 1 else 0 end , C_CITYINDEX";
                List<Region> list = Server.getInstance().getHotelService().findAllRegion(sql, orderby, -1, 0);
                if (list == null || list.size() == 0)
                    list = new ArrayList<Region>();
                if (type == 3) {
                    BaseDataCache.put(HotelCacheIndex.HotelBusinessZone + "_" + cityid, list);
                }
                else {
                    BaseDataCache.put(HotelCacheIndex.HotelDistrict + "_" + cityid, list);
                }
                return list;
            }
            //5：火车站/机场；8：景点；9：大学；10：医院；11：展馆会场
            if (type == 5 || type == 8 || type == 9 || type == 10 || type == 11) {
                String sql = "";
                if (type == 5) {
                    sql = "where C_TYPE in (3,4) and C_CITYID = " + cityid;
                }
                else {
                    int landtype = 0;
                    if (type == 8) {
                        landtype = 85;//景点
                    }
                    else if (type == 9) {
                        landtype = 86;//大学
                    }
                    else if (type == 10) {
                        landtype = 87;//医院
                    }
                    else if (type == 11) {
                        landtype = 88;//大学
                    }
                    sql = "where C_TYPE = " + landtype + " and C_CITYID = " + cityid;
                }
                String orderby = "order by case when C_CITYINDEX is null then 1 else 0 end , C_CITYINDEX";
                List<Landmark> list = Server.getInstance().getHotelService().findAllLandmark(sql, orderby, -1, 0);
                if (list == null || list.size() == 0)
                    list = new ArrayList<Landmark>();
                BaseDataCache.put(LandmarkIndex + "_" + cityid, list);
                return list;
            }
            //地铁站
            if (type == 6) {
                String sql = "where C_CITYID = " + cityid
                        + " and C_TYPE in (select ID from T_HOTELLANDTYPE where C_PARENTID=2 and C_CITYID = " + cityid
                        + ")";
                String otherby = "order by case when C_SUBWAYINDEX is null then 1 else 0 end , C_SUBWAYINDEX";
                List<Landmark> list = Server.getInstance().getHotelService().findAllLandmark(sql, otherby, -1, 0);
                if (list == null || list.size() == 0)
                    list = new ArrayList<Landmark>();
                BaseDataCache.put(LandmarkIndex + "_" + cityid, list);
                return list;
            }
            //地铁线
            if (type == 7) {
                List<HotelLandType> list = Server.getInstance().getHotelService()
                        .findAllHotelLandType("where C_PARENTID=2 and C_CITYID = " + cityid, "", -1, 0);
                if (list == null || list.size() == 0)
                    list = new ArrayList<HotelLandType>();
                BaseDataCache.put(HotelCacheIndex.HotelSubwayLine + "_" + cityid, list);
                return list;
            }
        }
        return new ArrayList();
    }

    /**
     * 去哪儿房型信息 
     */
    @SuppressWarnings("unchecked")
    public Map<String, String[]> getQunarRoomInfos(String QunarHotelId) throws NeedsRefreshException {
        Map<String, String[]> maps = new HashMap<String, String[]>();
        try {
            maps = (Map<String, String[]>) BaseDataCache.get("QunarRoomInfos_" + QunarHotelId);
        }
        catch (Exception e) {
            String QunarCityCode = QunarHotelId.substring(0, QunarHotelId.lastIndexOf("_"));
            String QunarHotelCode = QunarHotelId.substring(QunarHotelId.lastIndexOf("_") + 1);
            //http://hotel.qunar.com/city/beijing_city/dt-257/?tag=beijing_city
            String url = "http://hotel.qunar.com/city/" + QunarCityCode + "/dt-" + QunarHotelCode + "/?tag="
                    + QunarCityCode;
            //Return
            String html = PHUtil.submitPost(url, "").toString();
            //解析HTML
            if (html.contains("roomDesc")) {
                int start = html.indexOf("\"roomDesc\":");
                html = html.substring(start + 11);
                int end = html.indexOf("}");
                html = html.substring(0, end + 1);
                JSONObject json = JSONObject.fromObject(html);
                Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONArray infos = JSONArray.fromObject(json.get(key));
                    //去哪儿数据
                    String area = infos.getString(0);//面积
                    String floor = infos.getString(1);//楼层
                    String bed = infos.getString(2);//床型
                    String web = infos.getString(3);//宽带
                    //格式化数据
                    if (!ElongHotelInterfaceUtil.StringIsNull(area) && area.endsWith("㎡")) {//15㎡
                        area = area.replace("㎡", "平方米");
                    }
                    else {
                        area = "";
                    }
                    if (ElongHotelInterfaceUtil.StringIsNull(floor) || !floor.endsWith("层")) {//1-2层
                        floor = "";
                    }
                    if (!ElongHotelInterfaceUtil.StringIsNull(bed)) {
                        if (bed.contains("大床") && bed.contains("双床")) {
                            bed = "大床/双床";
                        }
                        else if (bed.contains("大床")) {
                            bed = "大床";
                        }
                        else if (bed.contains("双床")) {
                            bed = "双床";
                        }
                        else if (bed.contains("单人床")) {
                            bed = "单人床";
                        }
                        else {
                            bed = "其他";
                        }
                    }
                    else {
                        bed = "查";
                    }
                    int broadnet = 3;//查
                    if ("无".equals(web)) {
                        broadnet = 0;
                    }
                    else if ("免费".equals(web)) {
                        broadnet = 1;
                    }
                    else if ("收费".equals(web)) {
                        broadnet = 2;
                    }
                    String[] values = { area, floor, bed, Integer.toString(broadnet) };
                    maps.put(key, values);
                }
                if (maps.size() > 0) {
                    BaseDataCache.put("QunarRoomInfos_" + QunarHotelId, maps);
                }
            }
        }
        return maps;
    }

    /**
     * 去哪儿房型图片
     */
    public String getQunarRoomImages(String QunarHotelId) throws NeedsRefreshException {
        try {
            return (String) BaseDataCache.get("QunarRoomImages_" + QunarHotelId);
        }
        catch (Exception e) {
            //请求地址
            String url = "http://hotel.qunar.com/render/hotelDetailAllImage.jsp?hotelseq=" + QunarHotelId;
            String json = PHUtil.submitPost(url, "").toString();
            if (!ElongHotelInterfaceUtil.StringIsNull(json)) {
                BaseDataCache.put("QunarRoomImages_" + QunarHotelId, json);
            }
            return json;
        }
    }

    /**
     * 用于深捷旅推送价格时，验证国际酒店房型，直接跳过国际酒店房型
     * @param jlRoomId 深捷旅国际房型ID
     * @param jlHotelName 深捷旅国际酒店名称，验证时为空，赋值不为空
     * @return 国际酒店名称
     * @remark 验证是否是国际酒店房型时，jlHotelName需设为空，如果返回空，则表示非国际或无缓存，需调深捷旅酒店基础信息接口
     */
    public String checkJlTourInternationalRoom(String jlRoomId, String jlHotelName) {
        try {
            return (String) BaseDataCache.get(HotelCacheIndex.JlTourInternationalRoom + "_" + jlRoomId);
        }
        catch (Exception e) {
            if (!ElongHotelInterfaceUtil.StringIsNull(jlHotelName)) {
                BaseDataCache.put(HotelCacheIndex.JlTourInternationalRoom + "_" + jlRoomId, jlHotelName);
            }
            else {
                jlHotelName = "";
            }
            return jlHotelName;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, JSONArray> getQunarHotelPrice(String qunarId, Map<String, JSONArray> datas, int type) {
        try {
            //强制更新缓存
            if (type == 1 && datas != null && datas.size() > 0) {
                throw new Exception("更新缓存.");
            }
            return (Map<String, JSONArray>) BaseDataCache.get(HotelCacheIndex.QunarHotelPrice + "_" + qunarId);
        }
        catch (Exception e) {
            if (datas != null && datas.size() > 0) {
                BaseDataCache.put(HotelCacheIndex.QunarHotelPrice + "_" + qunarId, datas);
                return datas;
            }
            else {
                return new HashMap<String, JSONArray>();
            }
        }
    }
}