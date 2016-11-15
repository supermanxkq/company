package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.yl.ElongTrainOrderCallBack;
import com.ccservice.b2b2c.atom.servlet.yl.YiLongCallBackMethod;
import com.ccservice.b2b2c.base.train.Trainorder;

public class TongChengCallBack {
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
        String url = callbackurl;
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
            WriteLog.write("TongChengCallBack_callBackTongChengOrdered", ":callback_url:" + url + ":" + jso.toString());
            result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            if ("".equalsIgnoreCase(result.trim())) {
                result = "false";
            }
            WriteLog.write("TongChengCallBack_callBackTongChengOrdered", ":" + result);
        }
        catch (Exception e) {
            e.printStackTrace();
            result = "false";
        }
        return result;
    }

    /**
     * 三字码回调
     * 
     * @param trainorder
     * @time 2016.04.14
     * @author renshaonan
     * @param returnmsg
     * @param interfacetype
     *            接口类型 参考 TrainInterfaceMethod
     */
    public String train_order_callback(String returnmsg, String interfacetype, String reqtoken, String partnerid,
            String key, String callbackurl, String agentid, String orderid) {
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", ":returnmsg:" + returnmsg + ":interfacetype:" + interfacetype);
        JSONObject jsonstr = new JSONObject();
        boolean success = false;//        true:成功，false:失败
        boolean ordersuccess = false;
        int code = 999; //其他错误
        String res = returnmsg;
        String msg = returnmsg;//   1~256   提示信息 
        String zhanzuojieguoBackUrl_temp = callbackurl;
        String result = "";
        String transactionid = orderid;
        Trainorder trainorder = new Trainorder();
        //TODO error partnerid 是商户名   agentid是商户ID  order里属性不全，后面只要用的 ，必须补全  transactionid 我方订单号

        trainorder.setAgentid(Long.parseLong(agentid));
        trainorder.setQunarOrdernumber(orderid);
        trainorder.setExtnumber("");
        trainorder.setId(System.currentTimeMillis());
        trainorder.setOrderstatus(8);

        //取消订单交易关闭   说明没有占座成功
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", ":res:" + res);
        jsonstr.put("reqtoken", reqtoken);
        jsonstr.put("ordersuccess", ordersuccess);
        jsonstr.put("success", success);
        jsonstr.put("code", code);
        jsonstr.put("msg", msg);
        String parm = "data=" + jsonstr.toString();
        String ret = "-1";
        //请求同程
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", ":interfacetype:" + interfacetype);
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", ":parm=" + parm);
        if (interfacetype != null
                && ((TrainInterfaceMethod.MEITUAN + "").equals(interfacetype) || (TrainInterfaceMethod.YILONG2 + "")
                        .equals(interfacetype))) {
            //取消订单交易关闭   说明没有占座成功,美团的类型是不回调占座失败,而是直接回调给美团出票失败
            if ((TrainInterfaceMethod.YILONG2 + "").equals(interfacetype)) {
                String payCallbackUrl_temp = callbackurl;
                String merchantCode = partnerid;
                String keys = key;
                WriteLog.write("艺龙火车票接口_1.1申请分配座位席别回调", "|:回调返回:" + "|merchantCode号" + merchantCode + "|号"
                        + "|result结果：" + ret + "|key的值：" + keys + "|trainorder:" + trainorder + "|returnmsg:"
                        + returnmsg + "|interfacetype" + interfacetype);
                YiLongCallBackMethod yiLongCallBackMethod = new YiLongCallBackMethod(trainorder);
                String msgs = returnmsg;
                WriteLog.write("艺龙火车票接口_1.1msg", msgs);
                ret = yiLongCallBackMethod.payCallBack(trainorder, merchantCode, payCallbackUrl_temp, keys, msgs);
                WriteLog.write("艺龙火车票接口_1.1申请分配座位席别回调", ":回调返回:" + "result结果：" + ret + "|miyao：" + "|map取值key：" + key);
                if ("success".equalsIgnoreCase(ret)) {
                    ret = "SUCCESS";
                }
                else {
                    ret = "false";
                }
            }
            else {
                ret = "false";
            }
        }
        else {
            //TODO error 找回来
            WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调_meituan_callback", ":meituan:" + ":transactionid=" + transactionid);
            Map map_data = new HashMap();
            map_data.put("C_KEY", key);
            map_data.put("C_USERNAME", partnerid);
            map_data.put("C_PAYCALLBACKURL", callbackurl);
            String reqtime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            //            ret = TongChengCallBackServletMeiTuan.payCallBack_meituan_fail(0, "N", "0", parm, map_data, reqtime,
            //                    orderid);
        }
        //艺龙占座回调
        //TODO Auto-generated catch block
        if (interfacetype != null && (TrainInterfaceMethod.YILONG1 + "").equals(interfacetype)) {
            //            String zhanzuoCallBackUrl = TongChengCallBackServletUtil.getValueByMap(dataMap, "C_ZHANZUOHUIDIAO");
            String merchantCode = partnerid;
            //取消订单交易关闭   说明没有占座成功
            //trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);根据ID查询交易单号
            result = "FAIL";
            int failReason = code;
            String failReasonDesc = returnmsg;
            WriteLog.write("Elong_先占座后支付模式占座回调_ElongTrainOrderCallBackServlet", ":failReason:" + failReason
                    + ":failReasonDesc:" + failReasonDesc);
            ret = new ElongTrainOrderCallBack().trainOrderCallBackFail(merchantCode, key, callbackurl, result,
                    trainorder, failReasonDesc, failReason);
        }
        else {
            //TODO error 找回来
            WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", ":" + zhanzuojieguoBackUrl_temp + "?" + parm);
            //            ret = new TongChengCallBackServlet().train_order_callback_sendpostandget(zhanzuojieguoBackUrl_temp, parm);
        }
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", ":回调返回:" + ret);
        int i = 0;
        if ("SUCCESS".equalsIgnoreCase(ret)) {
            ret = "success";
        }
        else {
            if (interfacetype != null
                    && ((TrainInterfaceMethod.YILONG1 + "").equals(interfacetype) || (TrainInterfaceMethod.YILONG2 + "")
                            .equals(interfacetype))) {
                return ret;
            }
            else {
                while (i < 5) {
                    try {
                        Thread.sleep(15000L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //TODO error 找回来
                    //                    ret = new TongChengCallBackServlet().train_order_callback_sendpostandget(zhanzuojieguoBackUrl_temp, "data=" + jsonstr.toString());
                    WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", ":回调返回" + i + "返回:" + ret);
                    if ("SUCCESS".equalsIgnoreCase(ret)) {
                        ret = "success";
                        i = 5;
                    }
                    else {
                        i++;
                    }
                }
            }
        }
        return ret;
    }
}
