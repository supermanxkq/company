package com.ccservice.b2b2c.atom.server;

public class PhoneChangeWebs {
    private String dateTime;

    private String ip;

    private String msg;

    public PhoneChangeWebs() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public PhoneChangeWebs(String dateTime, String ip, String msg) {
        this.ip = ip;
        this.dateTime = dateTime;
        this.msg = msg;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
