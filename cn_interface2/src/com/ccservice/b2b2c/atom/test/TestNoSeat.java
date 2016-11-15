package com.ccservice.b2b2c.atom.test;

import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtilNoSeatThread;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;

public class TestNoSeat {

    public static void main(String[] args) {
        testNoSeatTB();
    }
    
    private  static void testNoSeatTB(){
        Trainticket trainticket = new Trainticket(); 
        trainticket.setSeattype("无座");
        trainticket.setTrainno("G651");
        trainticket.setDeparttime("2016-06-04");
        trainticket.setDeparture("北京西");
        trainticket.setArrival("西安北");
        trainticket.setPrice(155.5f);
        Trainorder trainorder = new Trainorder();
        trainticket.setId(40822);
        trainorder.setId(4268l);
       new TaobaoHotelInterfaceUtilNoSeatThread(trainticket, trainorder).start();
    }

}
