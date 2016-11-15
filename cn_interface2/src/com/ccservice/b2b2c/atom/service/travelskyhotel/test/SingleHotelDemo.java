package com.ccservice.b2b2c.atom.service.travelskyhotel.test;

import java.util.List;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service.travelskyhotel.TravelskyHotelService;
import com.ccservice.b2b2c.atom.service.travelskyhotel.bean.HXHotelPriceDetail;
import com.ccservice.b2b2c.atom.service.travelskyhotel.bean.HXHotelPriceResponse;
import com.ccservice.b2b2c.atom.service.travelskyhotel.bean.HXQueryHotelRequest;

public class SingleHotelDemo {

    public static void main(String[] args) throws Exception {
        HXQueryHotelRequest req = new HXQueryHotelRequest();
        req.setHotelCode("SOHOTO0101");
        req.setHotelId(90264l);
        req.setCheckInDate(ElongHotelInterfaceUtil.getCurrentDate());
        req.setCheckOutDate(ElongHotelInterfaceUtil.getAddDate(req.getCheckInDate(), 7));
        List<HXHotelPriceResponse> list = new TravelskyHotelService().travelskySingleHotels(req);
        for (int i = 0; i < list.size(); i++) {
            HXHotelPriceResponse res = list.get(i);
            System.out.println((i + 1) + "---" + res.getRoomTypeCode() + "---" + res.getRoomTypeName() + "---"
                    + res.getBedType() + "---" + res.getRatePlanName());
            for (String key : res.getRates().keySet()) {
                HXHotelPriceDetail detail = res.getRates().get(key);
                System.out.println(detail.getPriceDate() + "/" + detail.getAmountPrice() + "/" + detail.getFreeMeal()
                        + "/" + detail.getQuantity() + "/" + detail.getRoomStatus());
            }
        }
    }
}
