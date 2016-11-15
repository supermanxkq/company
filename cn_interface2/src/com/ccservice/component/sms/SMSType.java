package com.ccservice.component.sms;

import java.io.Serializable;

public class SMSType implements Serializable {

    public SMSType(int type) {
        this.type = type;
    }

    public static SMSType setSmstype(int type) {
        return new SMSType(type);
    }

    private int type = 0;

    // 注册短信
    public static final SMSType REGISTRATION = setSmstype(1);

    // 机票预定成功短信
    public static final SMSType FLIGHTSUCCESSOF = setSmstype(2);

    // 机票出票短信
    public static final SMSType TICKETOUTOFTHETICKET = setSmstype(3);

    // 酒店订单确认短信
    public static final SMSType HOTELORDERSCONFIRMATION = setSmstype(4);

    // 酒店订单取消短信
    public static final SMSType HOTELCANCELLATIONOFORDERS = setSmstype(5);

    // 酒店订单付款通知短信
    public static final SMSType HOTELORDERSPAYMENTNOTIFICATION = setSmstype(6);

    // 酒店预订成功短信
    public static final SMSType HOTELBOOKINGSUCCESSFUL = setSmstype(7);

    // 机票退费票
    public static final SMSType TICKETREFUNDTICKET = setSmstype(8);

    // 机票出票通知(往返)
    public static final SMSType TICKETROUNDTRIP = setSmstype(9);

    // 机票出票通知(单程)
    public static final SMSType TICKETONEWAY = setSmstype(10);

    // 采购商开户
    public static final SMSType OPENACCOUNT = setSmstype(11);

    // 酒店确认有房提醒支付
    public static final SMSType HOTELORDERPAY = setSmstype(12);

    // 酒店确认有房支付成功
    public static final SMSType HOTELORDERPAYCONFIRM = setSmstype(13);

    // 酒店确认有房安排房间
    public static final SMSType HOTELORDEROVER = setSmstype(14);

    public static final SMSType TRAINTICKETISSUE = setSmstype(17);

    //虚拟货币充值成功短信 财务网银操作
    public static final SMSType VMONEYCHARGESUCCESS = setSmstype(21);

    //订单消费短信
    public static final SMSType ORDERFEE = setSmstype(22);

    //财务扣款短信  财务虚拟账户操作
    public static final SMSType ACOUNTMONEY = setSmstype(23);

    public int getType() {
        return type;
    }

    public void setType(int type) {

        this.type = type;
    }

}
