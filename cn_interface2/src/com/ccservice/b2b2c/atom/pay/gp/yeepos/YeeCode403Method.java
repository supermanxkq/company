package com.ccservice.b2b2c.atom.pay.gp.yeepos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.callback.WriteLog;
import com.ccservice.b2b2c.atom.pay.gp.AirSupper;
import com.ccservice.b2b2c.atom.pay.handle.PayHandle;
import com.ccservice.b2b2c.atom.server.Server;

/**
 * 易宝403支付
 * @author wzc
 *
 */
public class YeeCode403Method extends AirSupper {
    /**
     * 支付成功处理
     * @param dom
     * @param ind
     * @return
     */
    public String payMethod(Element SessionHead, Element dom, int ind) {
        String returnMsg = "";
        //登陆工号， 要求此工号必须在接入方公司系 统中可以唯一标识一个操作员。（ 为了方便POS 机输入建议全部为数字）
        String EmployeeID = dom.elementText("EmployeeID");
        String PosSn = dom.elementText("PosSn");//POS 机编号
        String OrderNo = dom.elementText("OrderNo");//订单号
        String Amount = dom.elementText("Amount");//金额
        String PosRequestID = dom.elementText("PosRequestID");//凭证号
        String ReferNo = dom.elementText("ReferNo");//交易参考号
        String PayTypeCode = dom.elementText("PayTypeCode");//支付方式标识 1 银行卡 2 储值卡 3 微信支付
        String PayMethod = dom.elementText("PayMethod");//支付类型（ 1 整单支付 2 拆单支付）
        String ChequeNo = dom.elementText("ChequeNo");//支票号
        String BankCardNo = dom.elementText("BankCardNo");//银行卡号(中间会有星号)
        String BankCardName = dom.elementText("BankCardName");//发卡行名称
        String BankOrderNo = dom.elementText("BankOrderNo");//银行订单号
        String YeepayOrderNo = dom.elementText("YeepayOrderNo");//易宝订单号
        String CustomerNo = dom.elementText("CustomerNo");//易宝商编
        String sql = "SELECT OrderId,PayAmount,GpOrderNumber,OrderStatus FROM GpYeePosOrder WITH(nolock) WHERE Id="
                + OrderNo + " AND CustomerNo='" + CustomerNo + "'";
        List<Map> list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = list.get(0);
            String orderNumber = map.get("GpOrderNumber").toString();
            int OrderStatus = Integer.parseInt(map.get("OrderStatus").toString());
            if (OrderStatus == 1) {
                try {
                    String updateSql = " UPDATE dbo.GpYeePosOrder SET OrderStatus=2,EmployeeID='" + EmployeeID
                            + "',PosSn='" + PosSn + "',OrderNo='" + OrderNo + "',Amount=" + Amount + ",PosRequestID='"
                            + PosRequestID + "',ReferNo='" + ReferNo + "',PayTypeCode='" + PayTypeCode + "',PayMethod='"
                            + PayMethod + "',ChequeNo='" + ChequeNo + "',BankCardNo='" + BankCardNo + "',BankCardName='"
                            + BankCardName + "',BankOrderNo='" + BankOrderNo + "',YeepayOrderNo='" + YeepayOrderNo
                            + "' WHERE Id=" + OrderNo + " AND CustomerNo='" + CustomerNo + "'";
                    WriteLog.write("PosYeeCode403Method", OrderNo + ":" + updateSql);
                    Server.getInstance().getSystemService().findMapResultBySql(updateSql, null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                String ext2 = orderNumber + "FgGpAirWithHoldHandle";
                String[] infos = ext2.split("Fg");// 支付时传入参数规范
                String ordernumber = infos[0];// 获取订单号
                String handleName = infos[1];// 获取handle类名
                PayHandle payhandle = null;
                try {
                    payhandle = (PayHandle) Class.forName(PayHandle.class.getPackage().getName() + "." + handleName)
                            .newInstance();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                float f = Float.valueOf(Amount);
                payhandle.orderHandle(ordernumber, YeepayOrderNo, f, 17, CustomerNo);
            }
            String sqlselect = "SELECT KeyStr FROM dbo.GPPayInfo WITH(NOLOCK) WHERE PId='" + CustomerNo
                    + "' AND AccountType=1";
            List<Map> listselect = Server.getInstance().getSystemService().findMapResultBySql(sqlselect, null);
            if (listselect.size() > 0) {
                String key = listselect.get(0).get("KeyStr").toString();
                StringBuffer sb = new StringBuffer();
                String TransactionID = CustomerNo
                        + new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()))
                        + new SimpleDateFormat("MMddHHmmss").format(new Date(System.currentTimeMillis()));
                String RespTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                sb.append("<COD-MS>");
                sb.append("<SessionHead><Version>1.0</Version>");
                sb.append("<ServiceCode>COD403</ServiceCode>");
                sb.append("<TransactionID>" + TransactionID + "</TransactionID>");
                sb.append("<SrcSysID>yeepay</SrcSysID><DstSysID>" + CustomerNo + "</DstSysID>");
                sb.append("<ResultCode>2</ResultCode>");
                sb.append("<ResultMsg>接收成功</ResultMsg>");
                sb.append("<RespTime>" + RespTime + "</RespTime>");
                sb.append("<HMAC></HMAC>");
                sb.append("</SessionHead><SessionBody><OrderNo>" + OrderNo + "</OrderNo>");
                sb.append("<ReferNo>" + ReferNo + "</ReferNo>");
                sb.append("</SessionBody>");
                sb.append("</COD-MS>");
                returnMsg = new YeeCodeUtil().putMD5(sb.toString(), key);
            }
        }
        return returnMsg;
    }

}
