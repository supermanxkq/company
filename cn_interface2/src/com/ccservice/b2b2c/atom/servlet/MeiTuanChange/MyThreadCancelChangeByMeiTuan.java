package com.ccservice.b2b2c.atom.servlet.MeiTuanChange;

import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;
import com.callback.WriteLog;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.MeiTuanChange.Method.GetReqTokenByResignId;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MeiTuanSendUtils;

public class MyThreadCancelChangeByMeiTuan extends Thread {

    private String res ;

    private long resignId;
    
    
    private int logid;
    
 


    public MyThreadCancelChangeByMeiTuan(String res, long resignId, int logid) {
        this.res = res;
        this.resignId = resignId;
        this.logid = logid;
    }




    @Override
    public void run() {
        if(res!=null&&!"".equals(res)){
            WriteLog.write("meituan美团_取消改签","r-->"+logid+"res--->"+res);
            JSONObject result = new JSONObject();
            try {
                result = JSONObject.parseObject(res);
                boolean success = result.getBoolean("success");
                String msg = result.getString("msg");
                if(msg.equals("改签票已是取消状态")){
                    success = true;
                }
                JSONObject reqMeiTuan = new JSONObject();
                String sign = GetReqTokenByResignId.getChangeReqToken(GetReqTokenByResignId.Method.CANCEL_RESIGN, 0,
                        resignId, null, null, null, null, null);
                reqMeiTuan.put("resignId", resignId);
                reqMeiTuan.put("reqToken", sign);
                reqMeiTuan.put("success", success);
                Thread.sleep(10000L);
                WriteLog.write("meituan美团_取消改签","r-->"+logid+"reqMeiTuan---->"+ reqMeiTuan.toString());
                String callBackUrl = PropertyUtil.getValue("MeiTuan_changeCancel", "Train.properties");
                String backResult = new MeiTuanSendUtils().callService(callBackUrl, reqMeiTuan.toString());;
                WriteLog.write("meituan美团_取消改签","r-->"+logid+"backResult---->"+ backResult);
            }
            catch (Exception e) {
           
            }
            
            
            
        }
    
    
    }
    
    
}
