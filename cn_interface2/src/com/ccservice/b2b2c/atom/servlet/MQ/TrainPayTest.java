package com.ccservice.b2b2c.atom.servlet.MQ;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.util.ActiveMQUtil;

public class TrainPayTest {
    public static void main(String[] args) {
        sendPay();
    }

    /**
     * 
     */
    public static void sendPay() {
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("orderid", "1121");
        jsoseng.put("loginname", "121341");
        jsoseng.put("extnumber", "12134561");
        jsoseng.put("ordernumber", "123145161");
        jsoseng.put("ordersource", "1");
        jsoseng.put("payflag", "1");//1 标识获取链接并去支付   0 只获取链接
        jsoseng.put("interfacetype", "1");//6 淘宝
        jsoseng.put("timeout", "");//6 淘宝
        jsoseng.put(
                "orderpayurl",
                "https://mapi.alipay.com/gateway.do?body=sdfasdfsdf&subject=ceshiorder0tt8927wsd&notify_url=http%3A%2F%2Fwww.baidu.com%2Fcn_interface%2FAlipayNotifyHandle&out_trade_no=ceshiorder0tt892720151022191020&credit_card_pay=N&_input_charset=UTF-8&extra_common_param=ceshiorder0tt8927Fg&total_fee=0.1&credit_card_default_display=N&service=create_direct_pay_by_user&paymethod=directPay&partner=2088701454373226&seller_email=hyccservice%40126.com&payment_type=1&sign=21799fb59002a77ef3cc8d1866a8bc10&sign_type=MD5");//支付宝支付链接
        WriteLog.write("12306_TrainpayMqMSGUtil_sendOrderPayMQmsgnew", jsoseng.toString());
        ActiveMQUtil.sendMessage("tcp://127.0.0.1:61616", "PayMQ_TrainGetURL", jsoseng.toString());
    }
}
