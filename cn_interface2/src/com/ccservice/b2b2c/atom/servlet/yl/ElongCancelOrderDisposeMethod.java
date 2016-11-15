package com.ccservice.b2b2c.atom.servlet.yl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCancelTrain;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class ElongCancelOrderDisposeMethod extends TrainSelectLoginWay {

    @SuppressWarnings("rawtypes")
    public JSONObject CancelOrderDispose(String merchantId, String orderId, String timeStamp, String sign, int r1) {

        JSONObject resultJson = new JSONObject();
        JSONObject json = new JSONObject();
        InterfaceAccount interfaceAccount = getInterfaceAccount(merchantId);
        String key = interfaceAccount.getKeystr();

        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId;
        localSign = getSignMethod(localSign) + key;
        WriteLog.write("Elong_取消订单_ElongCancelOrderServlet", r1 + ":localSign:" + localSign);
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
            if (sign.equals(localSign)) {
                String sql = "SELECT C_ORDERNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='" + orderId
                        + "'";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    String transactionid = map.get("C_ORDERNUMBER").toString();

                    json.put("orderid", orderId);
                    json.put("transactionid", transactionid);

                    WriteLog.write("Elong_取消订单_ElongCancelOrderServlet", r1 + ":json:" + json.toString());
                    String result = new TongChengCancelTrain().operate(json, r1);
                    if (result.indexOf("取消订单成功") > -1) {
                        resultJson.put("retcode", "200");
                        resultJson.put("retdesc", "成功");
                    }
                    else {
                        resultJson = JSONObject.parseObject(result);
                    }
                }
                else {
                    resultJson.put("retcode", "452");
                    resultJson.put("retdesc", "此订单不存在");
                }
            }
            else {
                resultJson.put("retcode", "403");
                resultJson.put("retdesc", "签名校验失败");
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ElongCancelOrderDisposeMethod_err", e, "艺龙取消接口异常");
        }

        return resultJson;
    }
}
