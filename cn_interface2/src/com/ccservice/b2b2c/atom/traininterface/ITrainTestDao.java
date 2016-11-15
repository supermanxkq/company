package com.ccservice.b2b2c.atom.traininterface;

import java.util.List;

import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public interface ITrainTestDao {
    /**
     * 创建乘客信息
     **/
    public Trainpassenger getTrainpassenger(String name, String idnumber, int idtype, String seattype, Float price,
            String ticketno, int tickettype);

    /**
     * 申请分配座位席别
     **/
    public String train_order(String orderid, String checi, String from_station_code, String from_station_name,
            String to_station_code, String to_station_name, String train_date, List<Trainpassenger> passengers,
            String callbackurl, String partnerid, String reqtoken, String hasseat, String waitfororder, String shoudan);

    /**
     * 火车票确认出票
     **/
    public String train_confirm(String orderid, String transactionid);

    /**
     * 查询订单详情
     **/
    public String train_query_info(String orderid, String transactionid);

    /**
     * 取消火车票订单
     **/
    public String train_cancel(String orderid, String transactionid);

    /**
     * 在线退票
     **/
    public String return_ticket(String orderid, String transactionid, String ordernumber, String reqtoken,
            String callbackurl, Trainorder trainorder);

    /**
     * 请求改签
     **/
    public String train_request_change(String orderid, String transactionid, String ordernumber, String change_checi,
            String change_datetime, String change_zwcode, String old_zwcode, String passengersename,
            String passporttypeseid, String passportseno, String piaotype, String old_ticket_no);

    /**
     * 取消改签
     **/
    public String train_cancel_change(String orderid, String transactionid);

    /**
     * 确认改签
     **/
    public String train_confirm_change(String orderid, String transactionid);

    /**
     * 余票查询（有价格）
     **/
    public String train_query(String train_date, String from_station, String to_station, String purpose_codes,
            String needdistance);

    /**
     * 余票查询（无价格）
     **/
    public String train_query_remain(String train_date, String from_station, String to_station, String purpose_codes,
            String needdistance);

    /**
     * 车次查询
     **/
    public String get_train_info(String train_date, String from_station, String to_station, String train_no,
            String train_code);
}
