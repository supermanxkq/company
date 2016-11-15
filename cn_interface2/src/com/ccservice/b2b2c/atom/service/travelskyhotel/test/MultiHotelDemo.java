package com.ccservice.b2b2c.atom.service.travelskyhotel.test;

import java.util.List;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service.travelskyhotel.TravelskyHotelService;
import com.ccservice.b2b2c.atom.service.travelskyhotel.bean.HXQueryHotelRequest;

public class MultiHotelDemo {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        System.out.println("开始航信酒店同步=====" + ElongHotelInterfaceUtil.getCurrentTime());
        long start = System.currentTimeMillis();
        String sql = "where C_TRAVELSKYCODE != '' and C_TYPE = 1 and ID = 1394";
        List<City> citys = Server.getInstance().getHotelService().findAllCity(sql, "order by Id", -1, 0);
        for (City c : citys) {
            HXQueryHotelRequest req = new HXQueryHotelRequest();
            req.setPageNum(1);
            req.setLocalCity(c);
            req.setHotelCode("SOHOTO31896");
            req.setCityCode(c.getTravelSkyCode());
            req.setCheckInDate(ElongHotelInterfaceUtil.getCurrentDate());
            req.setCheckOutDate(ElongHotelInterfaceUtil.getAddDate(req.getCheckInDate(), 1));
            //req
            try {
                new TravelskyHotelService().travelskyMultiHotels(req);
            }
            catch (Exception e) {
                System.out.println(ElongHotelInterfaceUtil.errormsg(e));
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("结束航信酒店同步，耗时：" + (end - start) / 1000 + "秒。");
    }

}
