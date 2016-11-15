package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import com.alibaba.fastjson.JSONObject;
import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;

public class TongchengShoudanErrorCallback {
    public static void main(String[] args) {
        System.out.println(PropertyUtil.getValue("TongchengCallBackUrl", "Train.properties"));
    }

    /**
     ******************************* 同程回调占座结果
     * 
     * @param i
     * @param orderid
     * @param returnmsg
     *            回调具体内容 占座成功传true
     * @return
     * @time 2016.04.14
     * @author renshaonan
     */
    public String callBackTongChengOrdered(String agentid, String returnmsg, Integer interfacetype, String reqtoken,
            String partnerid, String callbackurl, String key, String orderid) {
        Boolean shoudan = true;
        String result = "false";
        String returncode = "999";
        String url = PropertyUtil.getValue("TongchengCallBackUrl", "Train.properties");
        JSONObject jso = new JSONObject();
        jso.put("agentid", agentid);
        jso.put("method", "train_order_callback");
        jso.put("returnmsg", returnmsg);
        jso.put("returncode", returncode);
        jso.put("shoudan", shoudan);
        jso.put("interfacetype", interfacetype);
        jso.put("reqtoken", reqtoken);
        jso.put("partnerid", partnerid);
        jso.put("key", key);
        jso.put("callbackurl", callbackurl);
        jso.put("orderid", orderid);

        try {
            WriteLog.write("TongchengShoudanErrorCallback_callBackTongChengOrdered",
                    ":callback_url:" + url + ":" + jso.toString());
            result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            if ("".equalsIgnoreCase(result.trim())) {
                result = "false";
            }
            WriteLog.write("TongchengShoudanErrorCallback_callBackTongChengOrdered", ":" + result);
        }
        catch (Exception e) {
            e.printStackTrace();
            result = "false";
        }
        return result;
    }

}
