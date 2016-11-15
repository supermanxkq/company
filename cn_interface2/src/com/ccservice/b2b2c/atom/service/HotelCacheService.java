package com.ccservice.b2b2c.atom.service;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import net.sf.json.JSONArray;

import com.ccservice.b2b2c.base.chaininfo.Chaininfo;
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;
import com.ccservice.b2b2c.atom.hotel.cache.CacheHotelData;
import com.opensymphony.oscache.base.NeedsRefreshException;

public class HotelCacheService implements IHotelCacheService {

    private CacheHotelData hotelcache = new CacheHotelData();

    @SuppressWarnings("rawtypes")
    public List getHotelStaticData(int type, long cityid) throws NeedsRefreshException {
        try {
            return hotelcache.getHotelStaticData(type, cityid);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAllCityCount() throws NeedsRefreshException {
        try {
            return hotelcache.getAllCityCount();
        }
        catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public int getAllHotelCount() throws NeedsRefreshException {
        try {
            return hotelcache.getAllHotelCount();
        }
        catch (RuntimeException e) {
            return 0;
        }
    }

    @Override
    public List<Hotelimage> getHotelimage(String Hotelid) {
        try {
            return hotelcache.getHotelimage(Hotelid);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Hotelimage> getZsHotelImage(long zsHotelId) {
        try {
            return hotelcache.getZsHotelImage(zsHotelId);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public Hotelimage getListHotelImage(long zsHotelId) {
        try {
            return hotelcache.getListHotelImage(zsHotelId);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Chaininfo> getListChainInfo() {
        try {
            return hotelcache.getListChainInfo();
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getHotelKeyWordJson(int type, long cityid, String reqfrom) {
        try {
            return hotelcache.getHotelKeyWordJson(type, cityid, reqfrom);
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * 去哪儿房型信息 
     */
    public Map<String, String[]> getQunarRoomInfos(String QunarHotelId) {
        try {
            return hotelcache.getQunarRoomInfos(QunarHotelId);
        }
        catch (Exception e) {
            return new HashMap<String, String[]>();
        }
    }

    /**
     * 去哪儿房型图片
     */
    public String getQunarRoomImages(String QunarHotelId) {
        try {
            return hotelcache.getQunarRoomImages(QunarHotelId);
        }
        catch (Exception e) {
            return "";
        }
    }

    public String checkJlTourInternationalRoom(String jlRoomId, String jlHotelName) {
        return hotelcache.checkJlTourInternationalRoom(jlRoomId, jlHotelName);
    }

    public Map<String, JSONArray> getQunarHotelPrice(String qunarId, Map<String, JSONArray> datas, int type) {
        return hotelcache.getQunarHotelPrice(qunarId, datas, type);
    }

    @Override
    public Integer getWindFlag(String hotelid, String roomcode) throws NeedsRefreshException {
        try {
            return hotelcache.getWindFlag(hotelid, roomcode);
        }
        catch (RuntimeException e) {
            return null;
        }
    }

    public CacheHotelData getHotelcache() {
        return hotelcache;
    }

    public void setHotelcache(CacheHotelData hotelcache) {
        this.hotelcache = hotelcache;
    }

    @Override
    public Object putHCache(String cacheindex, Object obj) throws NeedsRefreshException {
        return hotelcache.putHCache(cacheindex, obj);
    }

    @Override
    public void delHCache(String cacheindex) throws NeedsRefreshException {
        hotelcache.delHCache(cacheindex);

    }

    @Override
    public Object getHCache(String cacheindex) throws NeedsRefreshException {
        return hotelcache.getHCache(cacheindex);
    }

}
