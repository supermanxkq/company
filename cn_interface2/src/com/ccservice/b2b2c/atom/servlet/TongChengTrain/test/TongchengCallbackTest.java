package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.net.URLEncoder;

import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.SendPostandGet;

public class TongchengCallbackTest {

    public static void main(String[] args) {
        String trainorderid = "234756";
        String[] trainorderids = trainorderid.split(",");
        for (int i = trainorderids.length - 1; i >= 0; i--) {
            Long orderid = Long.parseLong(trainorderids[i]);
            String ss = callBackTongChengOrdered(orderid, "");
            System.out.println(trainorderids[i] + ":" + ss);

        }
    }

    /**
     * 同程回调占座结果
     * @param index
     * @param orderid
     * @param returnmsg 回调具体内容  占座成功传true
     * @return
     * @time 2014年12月12日 下午2:20:30
     * @author fiend
     */
    public static String callBackTongChengOrdered(long orderid, String returnmsg) {
        String result = "false";
        String url = "http://121.199.25.199:29016/cn_interface/tcTrainCallBack";
        try {
            returnmsg = URLEncoder.encode(returnmsg, "utf-8");
        }
        catch (Exception e) {
        }
        JSONObject jso = new JSONObject();
        jso.put("trainorderid", orderid);
        jso.put("method", "train_order_callback");
        jso.put("returnmsg", returnmsg);
        try {
            result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
