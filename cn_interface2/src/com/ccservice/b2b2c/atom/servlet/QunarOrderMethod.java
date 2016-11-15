package com.ccservice.b2b2c.atom.servlet;

import net.sf.json.JSONObject;

public class QunarOrderMethod {
    private String qunarordernumber;

    private long id;

    private int orderstatus;

    private String createtime;

    private int isquestionorder;

    private int state12306;

    private int interfaceType;

    private JSONObject orderjson;

    public int getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(int interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getQunarordernumber() {
        return qunarordernumber;
    }

    public void setQunarordernumber(String qunarordernumber) {
        this.qunarordernumber = qunarordernumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(int orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getIsquestionorder() {
        return isquestionorder;
    }

    public void setIsquestionorder(int isquestionorder) {
        this.isquestionorder = isquestionorder;
    }

    public int getState12306() {
        return state12306;
    }

    public void setState12306(int state12306) {
        this.state12306 = state12306;
    }

    public JSONObject getOrderjson() {
        return orderjson;
    }

    public void setOrderjson(JSONObject orderjson) {
        this.orderjson = orderjson;
    }

}
