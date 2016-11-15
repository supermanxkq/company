package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Fresh12306AccountMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;

public class MyThreadFresh12306Account extends Thread {
    private JSONObject result;

    private Customeruser customeruser;

    public MyThreadFresh12306Account(JSONObject result, Customeruser customeruser) {
        this.result = result;
        this.customeruser = customeruser;
    }

    public void run() {
        new Fresh12306AccountMethod().refreshAccountPassenger(result, customeruser);
    }
}
