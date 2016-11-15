package com.travelGuide;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;

public class Test12306Back {

    static String url = "http://localhost:9004/cn_interface/TravelGuideRequest";

    //    static String url = "http://tctrainorder.test.hangtian123.net/cn_interface/TravelGuideRequest";

    public static void main(String[] args) throws ConnectException, UnsupportedEncodingException {
        for (int i = 0; i < 1; i++) {
            testTravelGuideRequest();
        }
    }

    public static void testTravelGuideRequest() {
        long t1 = System.currentTimeMillis();
        String msg = "";
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("cxlx", "1");
        jsonobject.put("cz", "天津");
        jsonobject.put("cc", "12127");
        jsonobject.put("method", "get_train_zwdcx");
        msg = jsonobject.toJSONString();
        String result = SendPostandGet.submitPost(url, "JsonStr=" + msg, "UTF-8").toString();
        long t2 = System.currentTimeMillis();
        System.out.println(result);
        WriteLog.write("t正晚点查寻", "耗时：" + (t2 - t1) / 1000 + "s,结果：" + result);
    }

}
