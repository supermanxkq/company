package com.ccservice.b2b2c.atom.service12306.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class MyThreadSaveRefundOnlineDisable extends Thread {
    private String _Json12306Str;

    public MyThreadSaveRefundOnlineDisable(String Json12306Str) {
        this._Json12306Str = Json12306Str;
    }

    @Override
    public void run() {
        try {
            //防空判断
            JSONObject json = new JSONObject();
            try {
                json = JSONObject.parseObject(_Json12306Str);
            }
            catch (Exception e) {
            }
            if (json != null && !json.isEmpty()) {
                JSONObject data = json.containsKey("data") ? json.getJSONObject("data") : new JSONObject();
                JSONArray orderDBList = data.containsKey("orderDBList") ? data.getJSONArray("orderDBList")
                        : new JSONArray();
                if (!orderDBList.isEmpty() && orderDBList.size() == 1) {
                    JSONObject orderDBJsonObject = orderDBList.getJSONObject(0);
                    JSONArray tickets = orderDBJsonObject.containsKey("tickets") ? orderDBJsonObject
                            .getJSONArray("tickets") : new JSONArray();
                    if (!tickets.isEmpty()) {
                        JSONObject ticketJsonObject = tickets.getJSONObject(0);
                        JSONObject stationTrainDTO = ticketJsonObject.containsKey("stationTrainDTO") ? ticketJsonObject
                                .getJSONObject("stationTrainDTO") : new JSONObject();
                        if (!stationTrainDTO.isEmpty()) {
                            String from_station_name = stationTrainDTO.containsKey("from_station_name") ? stationTrainDTO
                                    .getString("from_station_name") : "";
                            String to_station_name = stationTrainDTO.containsKey("to_station_name") ? stationTrainDTO
                                    .getString("to_station_name") : "";
                            String station_train_code = stationTrainDTO.containsKey("station_train_code") ? stationTrainDTO
                                    .getString("station_train_code") : "";
                            String train_date = ticketJsonObject.containsKey("train_date") ? ticketJsonObject
                                    .getString("train_date") : "";
                            String sql = " [sp_TrainRefundOnlineDisable_insert]  @StartStationName='',@EndStationName='',@TrainCode='"
                                    + station_train_code
                                    + "',@StratDate='',@DepStationName='"
                                    + from_station_name
                                    + "',@ArrStationName='" + to_station_name + "',@DepDate='" + train_date + "'";
                            try {
                                WriteLog.write("MyThreadSaveRefundOnlineDisable", sql);
                                Server.getInstance().getSystemService().findMapResultByProcedure(sql);
                            }
                            catch (Exception e) {
                                ExceptionUtil.writelogByException("MyThreadSaveRefundOnlineDisable_Exception", e);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("MyThreadSaveRefundOnlineDisable_Exception", e);
        }
    }
    //TODO fiend 下面是12306返回结果例子，所有代码写到这个上面
    //    {
    //        "validateMessagesShowId": "_validatorMessage",
    //        "status": true,
    //        "httpstatus": 200,
    //        "data": {
    //            "orderDBList": [
    //                {
    //                    "sequence_no": "EA70859005",
    //                    "order_date": "2016-10-31 22:00:19",
    //                    "ticket_totalnum": 1,
    //                    "ticket_price_all": 9550,
    //                    "cancel_flag": "Y",
    //                    "resign_flag": "4",
    //                    "return_flag": "N",
    //                    "print_eticket_flag": "N",
    //                    "pay_flag": "Y",
    //                    "pay_resign_flag": "N",
    //                    "confirm_flag": "N",
    //                    "tickets": [
    //                        {
    //                            "stationTrainDTO": {
    //                                "trainDTO": {},
    //                                "station_train_code": "K656",
    //                                "from_station_telecode": "YWY",
    //                                "from_station_name": "延安",
    //                                "start_time": "1970-01-01 11:43:00",
    //                                "to_station_telecode": "ALY",
    //                                "to_station_name": "榆林",
    //                                "arrive_time": "1970-01-01 15:45:00",
    //                                "distance": "269"
    //                            },
    //                            "passengerDTO": {
    //                                "passenger_name": "李竹梅",
    //                                "passenger_id_type_code": "1",
    //                                "passenger_id_type_name": "二代身份证",
    //                                "passenger_id_no": "612724199202050248",
    //                                "total_times": "98"
    //                            },
    //                            "ticket_no": "EA708590051120101",
    //                            "sequence_no": "EA70859005",
    //                            "batch_no": "1",
    //                            "train_date": "2016-11-01 00:00:00",
    //                            "coach_no": "12",
    //                            "coach_name": "12",
    //                            "seat_no": "0101",
    //                            "seat_name": "10号下铺",
    //                            "seat_flag": "0",
    //                            "seat_type_code": "3",
    //                            "seat_type_name": "硬卧",
    //                            "ticket_type_code": "1",
    //                            "ticket_type_name": "成人票",
    //                            "reserve_time": "2016-10-31 22:00:19",
    //                            "limit_time": "2016-10-31 22:00:19",
    //                            "lose_time": "2016-10-31 22:30:19",
    //                            "pay_limit_time": "2016-10-31 22:30:19",
    //                            "ticket_price": 9550,
    //                            "print_eticket_flag": "N",
    //                            "resign_flag": "4",
    //                            "return_flag": "N",
    //                            "confirm_flag": "N",
    //                            "pay_mode_code": "Y",
    //                            "ticket_status_code": "i",
    //                            "ticket_status_name": "待支付",
    //                            "cancel_flag": "Y",
    //                            "amount_char": 0,
    //                            "trade_mode": "",
    //                            "start_train_date_page": "2016-11-01 11:43",
    //                            "str_ticket_price_page": "95.5",
    //                            "come_go_traveller_ticket_page": "N",
    //                            "return_deliver_flag": "N",
    //                            "deliver_fee_char": "",
    //                            "is_need_alert_flag": false,
    //                            "is_deliver": "N",
    //                            "dynamicProp": "",
    //                            "fee_char": "",
    //                            "insure_query_no": ""
    //                        }
    //                    ],
    //                    "reserve_flag_query": "p",
    //                    "if_show_resigning_info": "N",
    //                    "recordCount": "1",
    //                    "isNeedSendMailAndMsg": "N",
    //                    "array_passser_name_page": [
    //                        "李竹梅"
    //                    ],
    //                    "from_station_name_page": [
    //                        "延安"
    //                    ],
    //                    "to_station_name_page": [
    //                        "榆林"
    //                    ],
    //                    "start_train_date_page": "2016-11-01 11:43",
    //                    "start_time_page": "11:43",
    //                    "arrive_time_page": "15:45",
    //                    "train_code_page": "K656",
    //                    "ticket_total_price_page": "95.5",
    //                    "come_go_traveller_order_page": "N",
    //                    "canOffLinePay": "N",
    //                    "if_deliver": "N",
    //                    "insure_query_no": ""
    //                }
    //            ],
    //            "to_page": "db"
    //        },
    //        "messages": [],
    //        "validateMessages": {}
    //    }
}
