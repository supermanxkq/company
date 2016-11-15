package com.ccservice.b2b2c.atom.service.travelskyhotel.bean;

import java.io.Serializable;
import com.ccservice.b2b2c.base.city.City;

/**
 *航信酒店价格查询请求(单酒店/多酒店)
 */

@SuppressWarnings("serial")
public class HXQueryHotelRequest implements Serializable {
    /**
     * 航信城市对应的本地城市[多酒店(必选)]
     */
    private City localCity;

    /**
     * 航信城市编码[多酒店(必选)]
     */
    private String cityCode;

    /**
     * 航信酒店编码[多酒店(可选)、单酒店(必选)]
     */
    private String hotelCode;

    /**
     * 本地酒店ID[单酒店(必选)，用于取本地房型，需与hotelCode对应酒店ID一致]
     */
    private Long hotelId;

    /**
     * 入住时间[多酒店(必选)、单酒店(必选)]
     */
    private String checkInDate;

    /**
     * 离店时间[多酒店(必选)、单酒店(必选)]
     */
    private String checkOutDate;

    /**
     * 当前页数[多酒店(必选)]
     */
    private int pageNum;

    public City getLocalCity() {
        return localCity;
    }

    public void setLocalCity(City localCity) {
        this.localCity = localCity;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getHotelCode() {
        return hotelCode;
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
}