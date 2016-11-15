package com.ccservice.b2b2c.atom.servlet.QunarTrain.Thread;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 取消订单、取消改签订单 追加异步方法
 * @time 2015年4月8日 下午7:50:50
 * @author fiend
 */
public class MyThreadCancelMessageAgain extends Thread {
    /**
     * 1、train_cancel 2、train_cancel_change
     */
    private int methodtype;

    /**
     * 回调地址
     */
    private String callbackurl;

    /**
     * 是否成功
     */
    private boolean success;

    private String code;

    private String msg;

    /**
     * 接口账号
     */
    private String partnerid;

    /**
     * 接口key
     */
    private String key;

    /**
     * 接口订单号
     */
    private String jiekouorderno;

    /**
     * 改签Token
     */
    private String changereqtoken;

    public MyThreadCancelMessageAgain(boolean success, String code, String msg, String partnerid, String key,
            String jiekouorderno, int methodtype, String callbackurl, String changereqtoken) {
        this.methodtype = methodtype;
        this.callbackurl = callbackurl;
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.partnerid = partnerid;
        this.key = key;
        this.jiekouorderno = jiekouorderno;
        this.changereqtoken = changereqtoken;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        cancelMessageAgain();
    }

    public void cancelMessageAgain() {
        JSONObject jso = new JSONObject();
        jso.put("success", this.success);
        jso.put("code", this.code);
        try {
            jso.put("msg", URLEncoder.encode(this.msg, "UTF-8"));
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        jso.put("partnerid", this.partnerid);
        String reqtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        jso.put("reqtime", reqtime);
        try {
            jso.put("sign", ElongHotelInterfaceUtil.MD5(partnerid + reqtime + key));
        }
        catch (Exception e) {
            e.printStackTrace();
            jso.put("sign", "-1");
        }
        jso.put("orderid", this.jiekouorderno);
        if (1 == this.methodtype) {
            jso.put("method", "train_cancel");
        }
        else if (2 == this.methodtype) {
            jso.put("method", "train_cancel_change");
            jso.put("changereqtoken", this.changereqtoken);
        }
        else {
            jso.put("method", "-1");
        }
        String result = round(this.callbackurl + "?backjson=" + jso.toString());
        if (!"SUCCESS".equalsIgnoreCase(result)) {
            String sql = "UPDATE T_TRAINORDER SET C_ISQUESTIONORDER= " + Trainorder.CAIGOUQUESTION
                    + "  WHERE C_QUNARORDERNUMBER='" + jiekouorderno + "'";
            WriteLog.write("t同程火车票接口_4.8确认取消回调通知", jiekouorderno + "------>" + sql);
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
    }

    /**
     * 循环通知
     * @param url
     * @return
     * @time 2015年4月8日 下午8:24:59
     * @author fiend
     */
    public String round(String url) {
        String str = "";
        for (int i = 0; i < 5; i++) {
            WriteLog.write("t同程火车票接口_4.8确认取消回调通知", jiekouorderno + "------>" + url);
            str = SendPostandGet.submitGet(url, "UTF-8");
            WriteLog.write("t同程火车票接口_4.8确认取消回调通知", jiekouorderno + "------>" + str);
            if ("SUCCESS".equalsIgnoreCase(str)) {
                break;
            }
            else {
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    public static void main(String[] args) {
        try {
            System.out.println(URLEncoder.encode(("获取账号失败"), "UTF-8"));
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
