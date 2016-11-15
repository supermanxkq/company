package com.ccservice.b2b2c.atom.servlet.tn;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainAccountPassengerUtil;

public class TuNiuTrainAccountContactDelete {
    /**    
     * 2015年11月11日 13:19:59
     * 途牛删除常用联系人接口servlet
     * */
    public String cookie;

    public void DelPassenger(JSONObject jsonMen) {
        JSONObject jsonObjects = new JSONObject();
        TuNiuTrainAccountValidate tu = new TuNiuTrainAccountValidate();
        JSONObject cookieJson = tu.validateLoginNameAndPassword(jsonMen);
        cookie = cookieJson.getString("cookie");
        String ids = cookieJson.getString("id");
        try {
            jsonObjects.put("cookie", cookie);
            /*     jsonObjects.put("name", name);
                 jsonObjects.put("identyType", identyType);
                 jsonObjects.put("identy", identy);*/
            jsonObjects.put("type", "0");
            jsonObjects.put("ids", ids);
            String re = TrainAccountPassengerUtil.editPassengers(jsonObjects);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
