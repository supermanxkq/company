package com.ccservice.b2b2c.atom.service;

import java.util.Map;
import java.util.List;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hmhotelprice.JLOrder;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hmhotelprice.JLPriceResult;
import com.ccservice.b2b2c.base.hotelgooddata.HotelGoodData;

/**
 * 捷旅接口
 */

public interface IJLHotelService {

    /********************深捷旅新接口、JSON接口开始********************/

    /**
     * 深捷旅屏蔽酒店，禁用处理
     */
    public void newShieldHotel();

    /**
     * 酒店基本信息，用于同步深捷旅基础信息到本地
     * @param hotelIds 深捷旅的酒店ID，支持多个查询，用“/”隔开，如 1/2/3/4/5，每次请求最多查询20个
     * @param hotelMap key：深捷旅的酒店ID，用于通过深捷旅酒店ID取本地酒店，如为空则实时查询本地酒店
     * @param errorflag 出现错误，0：默认；1：出错了，用于多个查询后深捷旅抛异常，拆分ID，单个请求。非本方法内调用此接口均设为0。
     */
    public void newUpdateHotelInfo(String hotelIds, Map<String, Hotel> hotelMap, int errorflag) throws Exception;

    /**
     * 酒店房型价格
     * @param type 查询类型
     *          |--> hotelpriceall ：申请+即时确认；
     *          |--> hotelpricecomfirm ：只要即时确认，不填默认置为hotelpriceall；
     *          |--> UpdateChange：特殊，用于深捷旅变价通知时，异步更新淘宝数据
     * @param hotelIds深 捷旅酒店ID，多个ID用斜杠分开，例如：1/2/3/4/5，如果按酒店查询，则查询的是整个酒店的指定时段的价格数据.
     * @param roomIds 深捷旅房型ID，多个ID用斜杠分开，例如：1/2/3/4，如果按房型查询，则查询的是指定房型的价格数据，如果hotelIds和roomIds均有值，则优先取roomIds数据，忽略hotelIds中的数据.
     * @param checkInDate checkOutDate 入离店时间，时间范围最多一个月，如果超过一个月，则捷旅自动截取.
     */
    public List<JLPriceResult> newUpdateHotelPrice(String type, String hotelIds, String roomIds, String checkInDate,
            String checkOutDate);

    /********************深捷旅新接口、JSON接口结束********************/

    /**
     * 更新行政区域
     */
    public void getZone();

    /**
     * 更新区域信息(商业区)
     */
    public void getBizZone();

    /**
     * 更新城市信息
     */
    public void getCity();

    /**
     * 得到酒店列表
     */
    public void getHotels();

    /**
     * 得到房型列表
     */
    public void getRoomType(long hotelid, String hotelcode);

    /**
     * 比价用取价格
     */
    public List<HotelGoodData> getGDHotelPrice(String hotelid, String startDate, String endDate);

    /**
     * 取价格
     */
    public List<JLPriceResult> getHotelPrice(String hotelid, String startDate, String endDate);

    /**
     * 下单用取价格
     */
    public List<JLPriceResult> getHotelPriceByRoom(String hotelid, String Fprice, String roomtypeid, String startDate,
            String endDate);

    /**
     * 更新价格
     */
    public List<JLPriceResult> getUpdateHotelPrice(String hotelid, String startDate, String endDate, long time,
            String citycode);

    /**
     * 获取删除表信息
     */
    public List<JLPriceResult> getDelHotel(long time);

    /**
     * 获取更新的酒店信息
     */
    public void getUpHotel(long time);

    /**
     * 根据单个酒店选择是否更新或添加
     */
    public void getHotelSingle(String hotelcode);

    /**
     * 获取更新的房型信息
     */
    public void getUpRoomType(long time);

    /**
     * 取消订单申请接口
     */
    public String cancleOrder(String ordercd, String customerordercd, String hotelremark);

    /**
     * 新增订单
     */
    public Hotelorder newOrder(JLOrder order);
}