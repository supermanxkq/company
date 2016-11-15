package com.ccservice.b2b2c.atom.service.travelskyhotel.bean;

import java.util.Map;
import java.io.Serializable;

import com.ccservice.b2b2c.base.hotel.Hotel;

/**
 * 航信单酒店价格查询回复 
 * @author WH
 */

@SuppressWarnings("serial")
public class HXHotelPriceResponse implements Serializable {

    /**
     * 航信酒店编码
     */
    private String hotelCode;

    /**
     * 本地房型ID
     */
    private long localRoomId;

    /**
     * 航信房型编码
     */
    private String roomTypeCode;

    /**
     * 房型名称
     */
    private String roomTypeName;

    /**
     * 床型
     */
    private int bedType;

    /**
     * 床型名称
     */
    private String bedName;

    /**
     * 价格计划代码
     */
    private String ratePlanCode;

    /**
     * 价格计划名(暑期特惠房/连住3晚及以上/提前三天预定/双早/单早/现付/默认航旅通-金色世纪联合价格计划 等等)
     */
    private String ratePlanName;

    /**
     * 供应商代码
     */
    private String vendorCode;

    /**
     * 宾客类型(内宾D; 外宾F; A或空则表示通用价)
     */
    private String guestTypeIndicator;

    /**
     * 宽带(0：无；2：免费；3：收费)
     */
    private int Internet;

    /**
     * 最少多少天(连住)
     */
    private int minDay;

    /**
     * 提前多少天
     */
    private int beforeDay;

    /**
     * 总价格
     */
    private double totalAmountPrice;

    /**
     * 价格明细 (<日期,价格明细>,利于取某天的价格)
     */
    private Map<String, HXHotelPriceDetail> rates;

    /**
     * 对应本地酒店
     */
    private Hotel localHotel;

    public String getHotelCode() {
        return hotelCode;
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public String getRoomTypeCode() {
        return roomTypeCode;
    }

    public void setRoomTypeCode(String roomTypeCode) {
        this.roomTypeCode = roomTypeCode;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public int getBedType() {
        return bedType;
    }

    public void setBedType(int bedType) {
        this.bedType = bedType;
    }

    public String getRatePlanCode() {
        return ratePlanCode;
    }

    public void setRatePlanCode(String ratePlanCode) {
        this.ratePlanCode = ratePlanCode;
    }

    public String getRatePlanName() {
        return ratePlanName;
    }

    public void setRatePlanName(String ratePlanName) {
        this.ratePlanName = ratePlanName;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getGuestTypeIndicator() {
        return guestTypeIndicator;
    }

    public void setGuestTypeIndicator(String guestTypeIndicator) {
        this.guestTypeIndicator = guestTypeIndicator;
    }

    public Map<String, HXHotelPriceDetail> getRates() {
        return rates;
    }

    public void setRates(Map<String, HXHotelPriceDetail> rates) {
        this.rates = rates;
    }

    public int getInternet() {
        return Internet;
    }

    public void setInternet(int internet) {
        this.Internet = internet;
    }

    public double getTotalAmountPrice() {
        return totalAmountPrice;
    }

    public void setTotalAmountPrice(double totalAmountPrice) {
        this.totalAmountPrice = totalAmountPrice;
    }

    public long getLocalRoomId() {
        return localRoomId;
    }

    public void setLocalRoomId(long localRoomId) {
        this.localRoomId = localRoomId;
    }

    public String getBedName() {
        return bedName;
    }

    public void setBedName(String bedName) {
        this.bedName = bedName;
    }

    public int getMinDay() {
        return minDay;
    }

    public void setMinDay(int minDay) {
        this.minDay = minDay;
    }

    public int getBeforeDay() {
        return beforeDay;
    }

    public void setBeforeDay(int beforeDay) {
        this.beforeDay = beforeDay;
    }

    public Hotel getLocalHotel() {
        return localHotel;
    }

    public void setLocalHotel(Hotel localHotel) {
        this.localHotel = localHotel;
    }

}
