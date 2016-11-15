package com.ccservice.b2b2c.atom.service12306.bean;

@SuppressWarnings("serial")
public class TrainTicketReturnBean implements java.io.Serializable {

    /**
     * 证件号
     */
    private String passenger_id_no;

    /**
     * 车票编号
     */
    private String ticket_no;

    /**
     * 车箱编码
     */
    private String coach_no;

    /**
     * 车箱名称
     */
    private String coach_name;

    /**
     * 座席编码，如033b
     */
    private String seat_no;

    /**
     * 座席名称，如33b号
     */
    private String seat_name;

    /**
     * 座席类型编码，如1,表示硬座
     */
    private String seat_type_code;

    /**
     * 座席类型名称，如软卧代二等座、硬座
     */
    private String seat_type_name;

    /**
     * 票类型编码，如1表示成人票
     */
    private String ticket_type_code;

    /**
     * 票类型名称，如成人票、儿童票
     */
    private String ticket_type_name;

    /**
     * 支付超时时间
     */
    private String pay_limit_time;

    /**
     * 12306车票价格，区分请求的总价，如同程
     */
    private float ticketPrice;

    public String getPassenger_id_no() {
        return passenger_id_no;
    }

    public void setPassenger_id_no(String passenger_id_no) {
        this.passenger_id_no = passenger_id_no;
    }

    public String getTicket_no() {
        return ticket_no;
    }

    public void setTicket_no(String ticket_no) {
        this.ticket_no = ticket_no;
    }

    public String getCoach_no() {
        return coach_no;
    }

    public void setCoach_no(String coach_no) {
        this.coach_no = coach_no;
    }

    public String getCoach_name() {
        return coach_name;
    }

    public void setCoach_name(String coach_name) {
        this.coach_name = coach_name;
    }

    public String getSeat_no() {
        return seat_no;
    }

    public void setSeat_no(String seat_no) {
        this.seat_no = seat_no;
    }

    public String getSeat_name() {
        return seat_name;
    }

    public void setSeat_name(String seat_name) {
        this.seat_name = seat_name;
    }

    public String getSeat_type_code() {
        return seat_type_code;
    }

    public void setSeat_type_code(String seat_type_code) {
        this.seat_type_code = seat_type_code;
    }

    public String getSeat_type_name() {
        return seat_type_name;
    }

    public void setSeat_type_name(String seat_type_name) {
        this.seat_type_name = seat_type_name;
    }

    public String getPay_limit_time() {
        return pay_limit_time;
    }

    public void setPay_limit_time(String pay_limit_time) {
        this.pay_limit_time = pay_limit_time;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(float ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getTicket_type_code() {
        return ticket_type_code;
    }

    public void setTicket_type_code(String ticket_type_code) {
        this.ticket_type_code = ticket_type_code;
    }

    public String getTicket_type_name() {
        return ticket_type_name;
    }

    public void setTicket_type_name(String ticket_type_name) {
        this.ticket_type_name = ticket_type_name;
    }

}