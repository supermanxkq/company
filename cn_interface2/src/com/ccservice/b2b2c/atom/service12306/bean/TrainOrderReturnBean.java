package com.ccservice.b2b2c.atom.service12306.bean;

import java.util.List;

/**
 * 火车票下单12306，REP结果
 * @author WH
 */

@SuppressWarnings("serial")
public class TrainOrderReturnBean extends TrainReturnCommonBean {

    /**
     * 12306订单号，如EC28890001
     */
    private String sequence_no;

    /**
     * 12306订单生成时间，如2014-12-23 20:31:17
     */
    private String order_date;

    /**
     * 发车时间，如21:23
     */
    private String start_time;

    /**
     * 到达时间，如09:12
     */
    private String arrive_time;

    /**
     * 运行时间，如09:08
     */
    private String runtime;

    /**
     * 12306返回总价格，区分请求的总价，如同程
     */
    private float totalPrice;

    /**
     * 订单车票
     */
    private List<TrainTicketReturnBean> tickets;

    /**
     * 手机端获取未完成订单用的
     */
    private String needJson;

    /**
     * 手机端获取未完成订单用的
     */
    private String searchTrain;

    public String getSequence_no() {
        return sequence_no;
    }

    public void setSequence_no(String sequence_no) {
        this.sequence_no = sequence_no;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getArrive_time() {
        return arrive_time;
    }

    public void setArrive_time(String arrive_time) {
        this.arrive_time = arrive_time;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<TrainTicketReturnBean> getTickets() {
        return tickets;
    }

    public void setTickets(List<TrainTicketReturnBean> tickets) {
        this.tickets = tickets;
    }

    public String getNeedJson() {
        return needJson;
    }

    public void setNeedJson(String needJson) {
        this.needJson = needJson;
    }

    public String getSearchTrain() {
        return searchTrain;
    }

    public void setSearchTrain(String searchTrain) {
        this.searchTrain = searchTrain;
    }

}