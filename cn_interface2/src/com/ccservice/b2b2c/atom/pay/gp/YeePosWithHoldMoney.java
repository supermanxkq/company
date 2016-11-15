package com.ccservice.b2b2c.atom.pay.gp;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.pay.PayEntryInfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;

/**
 * 易宝Pos机票接口
 */
public class YeePosWithHoldMoney extends AirSupper {

    public static void main(String[] args) throws Exception {
        Random r = new Random();
        JSONObject obj = new JSONObject();
        for (int i = 0; i < 1; i++) {
            String msg = new YeePosWithHoldMoney("YDXA201606231532238").pay(obj, 984.6d, 0);
            System.out.println(msg);
        }
    }

    private Orderinfo orderinfo = null;

    /**
     * 
     * @param orderid
     * @param OrderNumber
     */
    public YeePosWithHoldMoney(String OrderNumber) {
        String sql = "select Id from T_ORDERINFO with(nolock) WHERE C_ORDERNUMBER='" + OrderNumber + "'";
        List<Map> list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        long orderid = 0;
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            orderid = Long.parseLong(map.get("Id").toString());
        }
        orderinfo = Server.getInstance().getAirService().findOrderinfo(orderid);
    }

    /**
     * 
     * @param ordernumber
     * @param money 保留两位小数
     * @param goodsDesc
     * @param agentid 供应商Agentid
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unused")
    public String pay(JSONObject msgobj, double money, int rand) throws Exception {
        JSONObject returnMsg = new JSONObject();
        if (orderinfo == null) {
            returnMsg.put("msg", "未查询到订单");
            returnMsg.put("orderid", 0);
            returnMsg.put("money", "");
            return returnMsg.toJSONString();
        }
        if (orderinfo.getPolicyagentid() == null || orderinfo.getPolicyagentid() <= 0) {
            returnMsg.put("msg", "无效订单供应商");
            returnMsg.put("orderid", 0);
            returnMsg.put("money", "");
            return returnMsg.toJSONString();
        }
        String ordernumber = orderinfo.getOrdernumber();
        long orderid = orderinfo.getId();
        PayEntryInfo info = findAgentInfo(orderinfo.getPolicyagentid().longValue() + "", 1);
        String merchantId = "";
        if (info == null) {
            returnMsg.put("msg", "快钱账户缺失");
            returnMsg.put("orderid", 0);
            returnMsg.put("money", "");
            return returnMsg.toJSONString();
        }
        else {
            merchantId = info.getPid();
        }
        String rate = PropertyUtil.getValue("yeePosRate", "GpAir.properties");
        Double payMoney = (double) money + money * Double.parseDouble(rate);
        double insertpaymoney = Math.ceil(payMoney * 10d) / 10;
        String sql = "INSERT INTO dbo.GpYeePosOrder(OrderId,PayAmount,GpOrderNumber,CustomerNo,OrderStatus) VALUES  ("
                + orderid + "," + insertpaymoney + ",'" + ordernumber + "','" + merchantId
                + "',1);select @@IDENTITY as id ;";
        sql += ";INSERT INTO dbo.GpAgencyProfitRecord(OrderId ,DaiShouFee ,DaiShouServFee ,DaiShouState) SELECT "
                + orderinfo.getId() + "," + payMoney + "," + 0
                + ",0 WHERE NOT EXISTS(SELECT 1 FROM dbo.GpAgencyProfitRecord WITH(nolock) WHERE OrderId=" + orderid
                + ")";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            String id = map.get("id").toString();
            returnMsg.put("msg", "操作成功");
            returnMsg.put("orderid", id);
            returnMsg.put("money", insertpaymoney);
        }
        else {
            returnMsg.put("msg", "操作失败");
            returnMsg.put("orderid", 0);
            returnMsg.put("money", 0);
        }
        return returnMsg.toJSONString();
    }

    //功能函数。将变量值不为空的参数组成字符串
    public static String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        }
        else {
            if (!paramValue.equals("")) {
                returnStr = paramId + "=" + paramValue;
            }
        }
        return returnStr;
    }

    //功能函数。将变量值不为空的参数组成字符串。结束
    // 功能函数。将变量值不为空的参数组成字符串
    public static StringBuilder appendParam(StringBuilder returnStr, String paramId, String paramValue) {
        if (returnStr.length() > 0) {
            if (!paramValue.equals("")) {
                returnStr = returnStr.append("&" + paramId + "=" + paramValue);
            }
        }
        else {
            if (!paramValue.equals("")) {
                returnStr.append(paramId + "=" + paramValue);
            }
        }
        return returnStr;
    }

    DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

    public String formatMoney(Double money) {
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    public String formatMoneyInt(Double money) {
        format.applyPattern("###0");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    public double formatMoneyDouble(Double money) {
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return Double.parseDouble(result);
        }
        catch (Exception e) {
            if (money != null) {
                return money;
            }
            else {
                return 0d;
            }
        }
    }
}