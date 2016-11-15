package com.ccservice.b2b2c.atom.servlet.MeiTuanTrain;

import com.ccservice.b2b2c.atom.service.SendPostandGet;

public class MeiTuanTest {

    public static void main(String[] args) {
        String url = "http://localhost:9004/cn_interface/MeiTuanTrainSeatOrder";
        String res = "{\"orderid\":3425252352424,\"trainCode\":\"T1\",\"start_time\":\"2016-09-06 13:05:00\",\"arrive_time\":\"2016-09-06 13:35:00\",\"from_station_code\":\"BJP\",\"from_station_name\":\"北京\",\"to_station_code\":\"TJP\",\"to_station_name\":\"天津\",\"callback_url\":\"sadsa\",\"req_token\":\"26546\",\"sign\":\"123\",\"reqtime\":\"20160905103202\",\"partnerid\":\"104\",\"passengers\":[{ \"ticket_id\":\"1244245\", \"passenger_name\":\"张三\", \"certificate_type\":\"1\", \"certificate_no\":\"1246546132165\", \"seat_type\":\"1\", \"ticket_type\":\"1\", \"price\":54.5 }]}";
        try {
            String result = SendPostandGet.submitPost(url, res, "utf-8").toString();
            System.out.println(result);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
