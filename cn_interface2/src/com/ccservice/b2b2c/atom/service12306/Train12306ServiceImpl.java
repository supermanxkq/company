package com.ccservice.b2b2c.atom.service12306;

import java.util.Random;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.service12306.bean.TrainOrderReturnBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengReqChange;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengConfirmChange;

public class Train12306ServiceImpl extends TongchengSupplyMethod implements ITrain12306Service {

    public Create12306OrderService create12306orderservice;

    /**
     * 下单12306
     * @param orderId 订单ID，TrainOrder.ID，没生成订单前，可传0，仅用于记录日志
     * @param train_date 乘车日期 yyyy-MM-dd
     * @param from_station 出发站三字码，可为空，建议传值
     * @param to_station 到达站三字码，可为空，建议传值
     * @param from_station_name 出发站名称
     * @param to_station_name 到达站名称
     * @param train_code 车次，如G101
     * @param passengers 乘客信息 JSONArray
     * >> 格式如：[{"ticket_type":"票类型","price":票价,"zwcode":"座位编码","passenger_id_type_code":"证件类型","passenger_name":"乘客姓名","passenger_id_no":"证件号"}]
     * >> ticket_type：1:成人票，2:儿童票，3:学生票，4:残军票
     * >> price：float类型
     * >> zwcode：9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
     * >> passenger_id_type_code：1:二代身份证，C:港澳通行证，G:台湾通 行证，B:护照
     * >> 均需有值
     * @return 下单结果
     */
    public TrainOrderReturnBean create12306Order(long orderId, String train_date, String from_station,
            String to_station, String from_station_name, String to_station_name, String train_code, String passengers,
            Customeruser customeruser) {
        return create12306orderservice.operate(orderId, train_date, from_station, to_station, from_station_name,
                to_station_name, train_code, passengers, customeruser);
    }

    /**
     * 改签退
     */
    public void ChangeReturnTicket(long trainOrderId, long trainTicketId) {
        new ChangeReturnTicketService().operate(trainOrderId, trainTicketId, false);
    }

    /**
     * 异步改签申请
     */
    public JSONObject AsyncChangeRequest(Trainorderchange trainOrderChange) {
        JSONObject retobj = new TongChengReqChange().AsyncChangeMQ(trainOrderChange);
        retobj.put("method", "train_request_change");
        retobj.put("reqtoken", trainOrderChange.getRequestReqtoken());
        retobj.put("callBackUrl", trainOrderChange.getRequestCallBackUrl());
        return retobj;
    }

    /**
     * 异步改签确认
     */
    public JSONObject AsyncChangeConfirm(Trainorderchange trainOrderChange) {
        JSONObject retobj = new TongChengConfirmChange().AsyncChangeMQ(trainOrderChange);
        retobj.put("method", "train_confirm_change");
        retobj.put("reqtoken", trainOrderChange.getConfirmReqtoken());
        retobj.put("callBackUrl", trainOrderChange.getConfirmCallBackUrl());
        return retobj;
    }

    /**
     * 获取下单或身份验证账号
     */
    public Customeruser getcustomeruser(Trainorder order) {
        return getCustomerUser(order, new Random().nextInt(1000000));
    }

    public Create12306OrderService getCreate12306orderservice() {
        return create12306orderservice;
    }

    public void setCreate12306orderservice(Create12306OrderService create12306orderservice) {
        this.create12306orderservice = create12306orderservice;
    }

}