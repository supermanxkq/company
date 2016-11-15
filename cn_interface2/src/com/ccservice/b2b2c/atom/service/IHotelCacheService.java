package com.ccservice.b2b2c.atom.service;

import java.util.Map;
import java.util.List;

import net.sf.json.JSONArray;

import com.ccservice.b2b2c.base.chaininfo.Chaininfo;
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;
import com.opensymphony.oscache.base.NeedsRefreshException;

public interface IHotelCacheService {
    /**
     * 酒店静态信息缓存
     * 酒店国内城市、品牌、商圈、行政区、火车站/机场、地铁站
     * @param type 类型 1：酒店城市；2：品牌；3：商圈；4：行政区；5：火车站/机场；6：地铁站；7：地铁线路
     * @param cityid 城市ID type为1、2时，城市ID不用传
     */
    @SuppressWarnings("rawtypes")
    public List getHotelStaticData(int type, long cityid) throws NeedsRefreshException;

    /**
     * 获取对应酒店的图片集合
     * @param Hotelid 临时酒店ID
     */
    public List<Hotelimage> getHotelimage(String Hotelid) throws NeedsRefreshException;

    /**
     * 获取正式酒店图片
     * @param zsHotelId 正式酒店ID
     */
    public List<Hotelimage> getZsHotelImage(long zsHotelId) throws NeedsRefreshException;

    /**
     * 获取酒店列表图片
     * @param zsHotelId 正式酒店ID
     */
    public Hotelimage getListHotelImage(long zsHotelId) throws NeedsRefreshException;

    /**
     * 获取所有城市数量
     */
    public String getAllCityCount() throws NeedsRefreshException;

    /**
     * 获取所有酒店数量
     */
    public int getAllHotelCount() throws NeedsRefreshException;

    /**
     * 获取所有的连锁酒店集合
     */
    public List<Chaininfo> getListChainInfo() throws NeedsRefreshException;

    /**
     * 去哪儿房型信息 
     */
    public Map<String, String[]> getQunarRoomInfos(String QunarHotelId) throws NeedsRefreshException;

    /**
     * 去哪儿房型图片
     */
    public String getQunarRoomImages(String QunarHotelId) throws NeedsRefreshException;

    /**
     * 获取宽带信息
     */
    public Integer getWindFlag(String hotelid, String roomcode) throws NeedsRefreshException;

    /**
     * 手机客户端缓存  酒店品牌、行政区、商业区、机场/车站、地铁线
     */
    public String getHotelKeyWordJson(int type, long cityid, String reqfrom);

    /**
     * 创建缓存的统一方法,没有就创建，有就获取
     * @param cacheindex 缓存索引
     * @param obj 放入缓存的对象
     */
    public Object putHCache(String cacheindex, Object obj) throws NeedsRefreshException;

    /**
     * 删除缓存
     */
    public void delHCache(String cacheindex) throws NeedsRefreshException;

    /**
     * 获取缓存
     */
    public Object getHCache(String cacheindex) throws NeedsRefreshException;

    public String checkJlTourInternationalRoom(String jlRoomId, String jlHotelName);

    /**
     * type=1 && datas.size>0，用于强制更新缓存
     * 缓存不存在，且datas.size>0表示存缓存
     */
    public Map<String, JSONArray> getQunarHotelPrice(String qunarId, Map<String, JSONArray> datas, int type);
}
