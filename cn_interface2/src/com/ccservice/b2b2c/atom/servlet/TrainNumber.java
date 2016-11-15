package com.ccservice.b2b2c.atom.servlet;

/**
 * 火车车次
 * @author luoqingxin
 *
 */
public class TrainNumber {
    /**
     * 官方系统车次内部编码
     */
    private String train_no;

    /**
     * 车次号
     */
    private String train_code;

    /**
     * 列车始发站名
     */
    private String start_station_name;

    /**
     * 列车终到站名
     */
    private String end_station_name;

    /**
     * 列车类型
     */
    private String train_type;

    /**
     * 列车从出发站到达目的站的运行天数
     * 0：当日到达，1：次日到达
     * 2：三日到达，3：四日到达
     * 依此类推
     */
    private String arrive_days;

    /**
     * 站点序号
     */
    private String station_no;

    /**
     * 车站名
     */
    private String station_name;

    /**
     * 到站时刻
     */
    private String arrive_time;

    /**
     * 离站时刻
     */
    private String start_time;

    /**
     * 经停时间
     */
    private String stopover_time;

    public String getTrain_no() {
        return train_no;
    }

    public void setTrain_no(String train_no) {
        this.train_no = train_no;
    }

    public String getTrain_code() {
        return train_code;
    }

    public void setTrain_code(String train_code) {
        this.train_code = train_code;
    }

    public String getStart_station_name() {
        return start_station_name;
    }

    public void setStart_station_name(String start_station_name) {
        this.start_station_name = start_station_name;
    }

    public String getEnd_station_name() {
        return end_station_name;
    }

    public void setEnd_station_name(String end_station_name) {
        this.end_station_name = end_station_name;
    }

    public String getTrain_type() {
        return train_type;
    }

    public void setTrain_type(String train_type) {
        this.train_type = train_type;
    }

    public String getArrive_days() {
        return arrive_days;
    }

    public void setArrive_days(String arrive_days) {
        this.arrive_days = arrive_days;
    }

    public String getStation_no() {
        return station_no;
    }

    public void setStation_no(String station_no) {
        this.station_no = station_no;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getArrive_time() {
        return arrive_time;
    }

    public void setArrive_time(String arrive_time) {
        this.arrive_time = arrive_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStopover_time() {
        return stopover_time;
    }

    public void setStopover_time(String stopover_time) {
        this.stopover_time = stopover_time;
    }

}
