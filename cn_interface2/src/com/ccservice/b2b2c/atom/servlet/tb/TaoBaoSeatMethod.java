package com.ccservice.b2b2c.atom.servlet.tb; 
public class TaoBaoSeatMethod {
    /**
     * 
     * @author RRRRRR
     * @time 2016年11月6日 上午11:51:13
     * @Description TODO
     * @param seat
     * @param trainno
     * @param type
     * @return
     */
    public static String getTaoBaoSeatName(String seat,String trainno,int type){
        String seatName="";
        if(seat.contains("软卧")&&trainno.contains("D")&&type==1){
            seatName="动卧";
        }else{
            seatName=seat;
        }
        return seatName;
    }
}
