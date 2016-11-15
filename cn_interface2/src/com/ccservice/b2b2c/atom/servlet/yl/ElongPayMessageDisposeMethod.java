package com.ccservice.b2b2c.atom.servlet.yl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCancelTrain;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengConfirmTrain;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;

public class ElongPayMessageDisposeMethod extends TrainSelectLoginWay {

    String key;

    /**
     * 
     * @param result
     * @param orderId
     * @param partnerid
     * @time 2015年12月11日 下午1:00:19
     * @author Mr.Wang
     */
    @SuppressWarnings("rawtypes")
    public JSONObject payMsgDisposeMethod(String result, String orderId, String merchantId, String timeStamp,
            String ticketPrice, String sign, int r1) {
        JSONObject resultJson = new JSONObject();
        JSONObject json = new JSONObject();

        InterfaceAccount interfaceAccount = getInterfaceAccount(merchantId);
        WriteLog.write(
                "Elong_艺龙支付通知_ElongPayMessageServlet",
                r1 + ":InterfaceAccount:" + JSONObject.toJSONString(interfaceAccount) + "key有没有："
                        + interfaceAccount.getKeystr() + "传参sign" + sign);
        this.key = interfaceAccount.getKeystr();
        try {
            String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
                    + "&ticketPrice=" + ticketPrice + "&result=" + result;
            localSign = getSignMethod(localSign) + this.key;
            WriteLog.write("Elong_艺龙支付通知_ElongPayMessageServlet", r1 + ":第一个排序:" + localSign);
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
            WriteLog.write("Elong_艺龙支付通知_ElongPayMessageServlet", r1 + ":相比较：" + localSign + ":==:" + sign);
            if (localSign.equals(sign)) {
                String sql = "SELECT C_TOTALPRICE,C_ORDERNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='"
                        + orderId + "'";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    String transactionid = map.get("C_ORDERNUMBER").toString();
                    String orderPrice = map.get("C_TOTALPRICE").toString();
                    String resultStr = "";
                    if (Float.parseFloat(ticketPrice) < Float.parseFloat(orderPrice)) { //如果艺龙传过来的支付价格小于订单价格
                        resultJson.put("retcode", "444");
                        resultJson.put("retdesc", "出票金额错误");
                    }
                    else {
                        if ("SUCCESS".equals(result)) { //出票
                            json.put("orderid", orderId);
                            json.put("transactionid", transactionid);
                            json.put("partnerid", merchantId);
                            WriteLog.write("Elong_艺龙支付通知_ElongPayMessageServlet", r1 + ":json:" + json.toString()
                                    + ":orderPrice:" + orderPrice);
                            resultStr = new TongChengConfirmTrain().opeate(json, interfaceAccount);
                        }
                        else if ("FAIL".equals(result)) {//等同取消占座
                            resultStr = new TongChengCancelTrain().operate(json, r1);
                        }

                        if (resultStr.indexOf("出票请求已接收") > -1) {
                            resultJson.put("retcode", "200");
                            resultJson.put("retdesc", "成功");
                        }
                        else {
                            resultJson = JSONObject.parseObject(resultStr);
                        }
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
        }

        return resultJson;
    }

}
