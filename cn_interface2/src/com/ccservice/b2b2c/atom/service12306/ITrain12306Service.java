package com.ccservice.b2b2c.atom.service12306;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.service12306.bean.TrainOrderReturnBean;

/**
 * 12306火车票Service
 * @author WH
 */

public interface ITrain12306Service {

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
            Customeruser customeruser);

    /**
     * 改签退
     */
    public void ChangeReturnTicket(long trainOrderId, long trainTicketId);

    /**
     * 异步改签申请
     */
    public JSONObject AsyncChangeRequest(Trainorderchange trainOrderChange);

    /**
     * 异步改签确认
     */
    public JSONObject AsyncChangeConfirm(Trainorderchange trainOrderChange);

    /**
     * 获取下单或身份验证账号
     */
    public Customeruser getcustomeruser(Trainorder order);
}