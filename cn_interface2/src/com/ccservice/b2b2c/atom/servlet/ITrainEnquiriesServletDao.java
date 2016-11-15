package com.ccservice.b2b2c.atom.servlet;

import java.util.List;

public interface ITrainEnquiriesServletDao {

    /**
     * 
     * @param train_date  乘车日期（yyyy-MM-dd）
     * @param from_station  出发站简码
     * @param to_station  到达站简码
     * @param train_no  【选填】官方系统的车次内部编码，如：54000G703931
     * @param train_code  车次号，如：G7039
     * @return luoqingxin
     */
    List<TrainNumber> process(String train_date, String from_station, String to_station, String train_no,
            String train_code);
}
