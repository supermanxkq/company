package com.ccservice.b2b2c.atom.service;

import java.util.Map;
import java.util.List;
import java.sql.Timestamp;

import com.taobao.api.*;
import com.taobao.api.domain.*;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;

public interface ITaoBaoHotelSerice {

    /**
     * 酒店发布
     */
    public String hotelAdd(Hotel hotel, String imgpath, Long taobaoProvinceId, Long taobaoCityId, Long taobaoRegionId,
            String sessionkey) throws Exception;

    /**
     * 单个酒店审核、房型审核 
     */
    public String hotelGet(Long taobaoHotelId, boolean NeedRoom, boolean NeedHotelCheckStatus, String sessionkey)
            throws Exception;

    /**
     * 酒店更新 
     */
    public String hotelUpdate(Long taobaoHotelId, Hotel hotel, String imgpath, Long taobaoProvinceId,
            Long taobaoCityId, Long taobaoRegionId, String sessionkey) throws Exception;

    /**
     * 发布房型
     */
    public String roomAdd(String roomName, Long taobaoHotelId, String sessionkey) throws Exception;

    /**
     * 发布商品
     */
    public String productAdd(Long taobaoHotelId, Long taobaoRoomId, Roomtype roomtype, String imgpath,
            List<Hmhotelprice> prices, String sessionkey) throws Exception;

    /**
     * 商品批量审核
     */
    public String productsGet(String taobaoHotelIds, String taobaoRoomIds, String taobaoProdIds, long pageno,
            boolean needhotel, boolean needroom, String sessionkey) throws Exception;

    /**
     * 商品单个更新
     * @param taobaoProdId 淘宝商品ID，必填
     * @param roomtype 更新房型信息，可为空
     * @param prices 更新价格或早餐，可为空
     * @param status 商品状态 1：上架；2：下架；3：删除 --> 传入相应状态代表去执行相应的操作 -->可为空
     * 更新商品价格套餐时roomtype、prices不可为空
     * 更新商品状态时 status不可为空
     */
    public String productUpdate(Long taobaoProdId, Roomtype roomtype, List<Hmhotelprice> prices, Long status,
            String sessionkey) throws Exception;

    /**
     * 商品批量更新
     * @param prodPrices Map -- key：淘宝商品ID；value：更新的价格
     * @return 成功的商品list json
     */
    public String productBatchUpdate(Map<Long, List<Hmhotelprice>> prodPrices, String sessionkey) throws Exception;

    /**
     * 酒店、房型通过最后更新时间分页审核
     * @param startModifyTime 开始时间   不可为空
     * @param pageno 页码 >=1
     * @param pagesize 1-100
     * @param type 类型 1：酒店、2：房型
     */
    public String getStatusByModifyTime(Timestamp startModifyTime, long pageno, long pagesize, int type,
            String sessionkey);

}