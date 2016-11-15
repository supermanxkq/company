package com.ccservice.b2b2c.atom.service.travelskyhotel;

import java.util.List;
import com.ccservice.b2b2c.atom.service.travelskyhotel.bean.HXQueryHotelRequest;
import com.ccservice.b2b2c.atom.service.travelskyhotel.bean.HXHotelPriceResponse;

/**
 * 中航信酒店接口
 * @author WH
 */
public interface ITravelskyHotelService {
    /**
     * 城市接口、用于同步城市到本地
     */
    public void travelskyCitys() throws Exception;

    /**
     * 酒店、房型静态信息、用于同步酒店和房型到本地
     */
    public void travelskyMultiHotels(HXQueryHotelRequest req) throws Exception;

    /**
     * 单酒店、酒店价格、只查动态信息、用于同步价格到本地
     */
    public List<HXHotelPriceResponse> travelskySingleHotels(HXQueryHotelRequest req) throws Exception;
}