package com.ccservice.b2b2c.atom.servlet;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * 测试淘宝数据
 * 
 * @time 2015年5月4日 下午9:35:42
 * @author fiend
 */
public class TaobaoTrainTest {

    public static String test() {
        int taobao_ordernumber = Integer.valueOf(new Random().nextInt(100) + "" + new Random().nextInt(100) + ""
                + new Random().nextInt(100) + "" + new Random().nextInt(100));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDateTime = sdf.format(System.currentTimeMillis() + 30 * 60 * 1000l); //得到精确到秒的表示：08/31/2006 21:08:00
        System.out.println(sDateTime);
        return "{\"train_agent_order_get_response\":{\"mailing\":true,\"is_success\":true,\"request_id\":\"9wy6bwf6jpo4\",\"relation_name\":\"梁伟\",\"company_name\":\"no\",\"address\":\"改了啊^^^海南省^^^中沙群岛的岛礁及其海域^^^ ^^^啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊^^^100074^^^13810065191^^^\",\"main_order_id\":"
                + taobao_ordernumber
                + ",\"total_price\":2100,\"telephone\":\"18514281458\",\"tickets\":{\"to_agent_ticket_info\":[{\"birthday\":\"1992-05-29\",\"train_num\":\"6233\",\"tag\":1,\"certificate_num\":\"140221199205293017\",\"passenger_type\":0,\"to_station\":\"哈尔滨东\",\"insurance_price\":2000,\"certificate_type\":\"0\",\"insurance_unit_price\":2000,\"seat\":1,\"ticket_price\":100,\"from_time\":\"2015-06-18 10:39:00\",\"sub_order_id\":\"193525834921223\",\"from_station\":\"哈尔滨\",\"passenger_name\":\"梁伟\",\"to_time\":\"2015-06-18 10:54:00\"}]},\"order_status\":0,\"latest_issue_time\":\""
                + sDateTime + "\"}}";
    }

    public static void main(String[] args) {
        System.out.println(test());
    }
}
