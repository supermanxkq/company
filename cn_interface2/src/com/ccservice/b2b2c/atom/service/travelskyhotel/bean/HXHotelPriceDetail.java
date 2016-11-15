package com.ccservice.b2b2c.atom.service.travelskyhotel.bean;

import java.io.Serializable;

/**
 * 价格明细 - 航信单酒店价格查询回复
 */

@SuppressWarnings("serial")
public class HXHotelPriceDetail implements Serializable {
    /**
     * 日期
     */
    private String priceDate;

    /**
     * 价格
     */
    private double amountPrice;

    /**
     * 早餐(0：无早；1：单早；2：双早；-1：有早)
     */
    private int freeMeal;

    /**
     * 房态(onRequest： 可申请；avail：即时确认；noavail：不可用)
     */
    private String roomStatus;

    /**
     * 房量
     */
    private int quantity;

    public String getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
    }

    public double getAmountPrice() {
        return amountPrice;
    }

    public void setAmountPrice(double amountPrice) {
        this.amountPrice = amountPrice;
    }

    public int getFreeMeal() {
        return freeMeal;
    }

    public void setFreeMeal(int freeMeal) {
        this.freeMeal = freeMeal;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
