package com.ccservice.b2b2c.atom.pay.gp;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;

/**
 * 支付工具类
 * @author wzc
 *
 */
public class GpUtil {
    public static void main(String[] args) {}

    /**
     * 虚拟代收款之后 加我们在供应商压的钱在添加回来
     * Gp机票分润
     * @param orderid
     * @param type  1 pos机
     */
    public void shareGpProfit(long orderid, long type, String moneyreal) {
        try {
            Orderinfo orderinfo = Server.getInstance().getAirService().findOrderinfo(orderid);
            String refordernumber = orderinfo.getExtorderid() != null ? orderinfo.getExtorderid() : "";//接口单号
            if (type == 1) {//post机
                String sharesql = "[dbo].[sp_rebaterecord_MoneyControl]@orderid = " + orderid + ",@ordernumber = N'"
                        + orderinfo.getOrdernumber() + "',@yewutye = 1,@rebateagentid = " + orderinfo.getPolicyagentid()
                        + ",@rebatemoney = " + moneyreal
                        + ",@vmenble = 1,@rebatetype = 6,@rebatememo = N'Pos代收款添加',@customerid = 0,@refordernumber = N'"
                        + refordernumber + "',@paymethod = 10,@paystate = 1";
                WriteLog.write("GP订单代收款添加", orderid + "==>" + sharesql);
                List resultlist = Server.getInstance().getSystemService().findMapResultByProcedure(sharesql);
                if (resultlist.size() > 0) {
                    Map mapresult = (Map) resultlist.get(0);
                    String result = mapresult.get("result").toString();
                    WriteLog.write("GP订单代收款添加", orderid + "==>POS==>" + result + "==>" + moneyreal);
                }
            }
            else {
                String sql = "SELECT * FROM dbo.GpShareProfit WITH(nolock) WHERE 1=1 and status=0 and  OId= " + orderid;
                List<Map> list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                for (int i = 0; i < list.size(); i++) {
                    Map map = list.get(i);
                    String id = map.get("ID").toString();
                    String btype = map.get("Btype").toString();
                    Long agentId = Long.valueOf(map.get("AgentId").toString());//需加款接口采购商
                    String momo = map.get("Note").toString();//备注
                    String money = map.get("Money").toString();//钱
                    if (Double.parseDouble(money) > 0) {//钱为正数
                        if ("1".equals(btype)) {//供应商分润
                            //分润
                            String sharesql = "[dbo].[sp_rebaterecord_MoneyControl]@orderid = " + orderid
                                    + ",@ordernumber = N'" + orderinfo.getOrdernumber()
                                    + "',@yewutye = 1,@rebateagentid = " + agentId + ",@rebatemoney = " + money
                                    + ",@vmenble = 1,@rebatetype = 6,@rebatememo = N'代收款添加',@customerid = 0,@refordernumber = N'"
                                    + refordernumber + "',@paymethod = 10,@paystate = 1";
                            WriteLog.write("GP订单代收款添加", orderid + "==>" + sharesql);
                            List resultlist = Server.getInstance().getSystemService()
                                    .findMapResultByProcedure(sharesql);
                            if (resultlist.size() > 0) {
                                Map mapresult = (Map) resultlist.get(0);
                                String result = mapresult.get("result").toString();
                                WriteLog.write("GP订单代收款添加", orderid + "==>" + result + "==>" + money);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String formatMoney_short(String s) {

        // String s = "123.456 ";
        float money = Float.valueOf(s).floatValue();

        DecimalFormat format = null;
        format = (DecimalFormat) NumberFormat.getInstance();
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            return Float.toString(money);
        }
    }
}
