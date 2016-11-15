package com.ccservice.b2b2c.atom.servlet.account.method;

import java.util.HashMap;
import com.tenpay.util.MD5Util;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import TrainInterfaceMethod.TrainInterfaceMethod;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class TrainRefreshAccountCookieMethod {

    public void refreshCookie(String logName, JSONObject json, int random) {
        try {
            Trainorder trainorder = new Trainorder();
            trainorder.setId(json.getLongValue("orderid"));//订单ID
            trainorder.setAgentid(json.getLongValue("agentid"));//代理ID
            int interfaceType = json.getIntValue("interfaceType");
            trainorder.setInterfacetype(interfaceType);//接口类型，判断是美团还是其他代理
            trainorder.setQunarOrdernumber(json.getString("interfaceOrderNumber"));//接口订单号
            //美团
            if (interfaceType == TrainInterfaceMethod.MEITUAN) {
                meiTuanCookie(logName, trainorder, random);
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
        }
    }

    //美团
    private void meiTuanCookie(String logName, Trainorder trainorder, int random) throws Exception {
        //请求参数
        JSONObject request = new JSONObject();
        //配置文件
        String filename = "Train.GuestAccount.properties";
        //参数设置
        request.put("orderId", trainorder.getQunarOrdernumber());//美团订单id
        request.put("agentCode", PropertyUtil.getValue("meituan.agentCode", filename));//代理商编码
        //加密KEY
        String key = PropertyUtil.getValue("meituan.key", filename);
        //加密验证
        String sign = MD5Util.MD5Encode(request + key, "UTF-8");
        //请求地址
        String url = PropertyUtil.getValue("meituan.url", filename) + sign;
        //调用请求
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json; charset=utf-8");
        //3分钟
        String result = RequestUtil.post(url, request.toString(), "UTF-8", header, 3 * 60 * 1000);
        //记录日志
        WriteLog.write(logName, random + "-->oid-->" + trainorder.getId() + "-->res-->" + result);
        //解析回复
        JSONObject response = JSONObject.parseObject(result);
        //调用成功
        if (response != null && "0".equals(response.getString("status"))) {
            StringBuffer cookie = new StringBuffer();
            String[] datas = response.getString("data").split(";");
            //循环
            for (String data : datas) {
                String temp = data.toLowerCase().trim();
                //后续自己拼
                if (temp.startsWith("path") || temp.startsWith("current_captcha_type")) {
                    continue;
                }
                cookie.append(data.trim() + "; ");
            }
            cookie.append("current_captcha_type=Z");
            insertTrainAccountSrc(trainorder.getId(), trainorder.getAgentid(), cookie.toString(), random);
        }
    }

    /**
     * 调用存储过程入库
     */
    private void insertTrainAccountSrc(long orderid, long agentid, String cookie, int random) {
        //Cookie非空
        if (!ElongHotelInterfaceUtil.StringIsNull(cookie)) {
            String sql = "";
            try {
                sql = "[dbo].[sp_TrainAccountSrc_Insert_CookieAndIP] @Agentid = " + agentid + ", @TrainOrderId = "
                        + orderid + ", @Cookie='" + cookie + "', @IP12306=''";
                //保存数据
                Server.getInstance().getSystemService().findMapResultByProcedure(sql);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("InsertTrainAccountSrc_Error", e, random + "-->" + sql);
            }
        }
    }
}