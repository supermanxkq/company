package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alipay.client.base.PartnerConfig;
import com.alipay.client.security.RSASignature;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.dnsmaintenance.Dnsmaintenance;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * @author hanmh Alipay_WAP支付通知
 * 
 */
@SuppressWarnings("serial")
public class AlipayWapNotifyHandle extends NotifyHandleSupport {

    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WriteLog.write("Alipay_WAP支付通知", "支付宝Alipay_WAP支付通知");
        // 获得通知参数
        Map map = request.getParameterMap();
        // 获得通知签名
        String sign = (String) ((Object[]) map.get("sign"))[0];
        // 获得待验签名的数据
        String verifyData = getVerifyData(map);
        boolean verified = false;

        WriteLog.write("Alipay_WAP支付通知", verifyData);
        WriteLog.write("Alipay_WAP支付通知", sign);
        // 使用支付宝公钥验签名
        try {
            verified = RSASignature.doCheck(verifyData, sign, PartnerConfig.RSA_ALIPAY_PUBLIC);
        }
        catch (Exception e) {
            WriteLog.write("Alipay_WAP支付通知", "手机客户端支付 支付宝公钥签名验证异常" + e.fillInStackTrace());
        }
        PrintWriter out = response.getWriter();
        WriteLog.write("Alipay_WAP支付通知", verified + "");
        // 验证签名通过
        if (verified || 1 == 1) {
            // 根据交易状态处理业务逻辑
            // 当交易状态成功，处理业务逻辑成功。回写success
            String text = (String) ((Object[]) map.get("notify_data"))[0];
            Document document = null;
            try {
                document = DocumentHelper.parseText(text);
            }
            catch (DocumentException e) {
                e.printStackTrace();
            }
            Element root = document.getRootElement();
            String trade_status = root.elementTextTrim("trade_status");

            WriteLog.write("Alipay_WAP支付通知", "交易结果：" + trade_status);
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                String out_trade_no = root.elementTextTrim("out_trade_no");// 商户订单号
                String total_fee = root.elementTextTrim("total_fee");// 交易金额
                String trade_no = root.elementTextTrim("trade_no");// 外部交易号
                String buyer_email = root.elementTextTrim("buyer_email");
                String subject = root.elementTextTrim("subject");//订单类型  酒店预订、飞机票预订
                WriteLog.write("Alipay_WAP支付通知", subject + "----->交易订单号：" + out_trade_no + ",交易金额：" + total_fee
                        + ",外部交易号：" + trade_no + ",买家帐号：" + buyer_email);
                if ("酒店预订".equals(subject)) {
                    super.orderHandle(out_trade_no + "Fg" + HotelorderreturnHandle.class.getSimpleName(), trade_no,
                            Float.valueOf(total_fee), Paymentmethod.ALIPAY, "");
                }
                else if ("飞机票预订".equals(subject)) {
                    super.orderHandle(out_trade_no + "Fg" + AirnofiryHandle.class.getSimpleName(), trade_no,
                            Float.valueOf(total_fee), Paymentmethod.ALIPAY, "");
                }
                sendSmsbyOrderOnpay(out_trade_no, "交易号:" + trade_no);
                out.print("success");
            }
            else if ("WAIT_BUYER_PAY".equals(trade_status)) {
                out.print("success");
            }
        }
        else {
            WriteLog.write("Alipay_WAP支付通知", "接收支付宝系统通知验证签名失败，请检查！");
            out.print("fail");
        }
    }

    /**
     * 当支付后自动根据订单号发送提醒短信
     * 
     * @param out_trade_no 订单号
     * @return
     */
    public boolean sendSmsbyOrderOnpay(String out_trade_no, String con) {

        boolean issend = false;
        try {
            Orderinfo orderinfo = (Orderinfo) Server.getInstance().getAirService()
                    .findAllOrderinfo("WHERE C_ORDERNUMBER='" + out_trade_no + "'", "", -1, 0).get(0);

            Dnsmaintenance dns = Server.getInstance().getSystemService().findDnsmaintenance(1);
            if (dns != null) {
                String[] mobiles = new String[] { getSysconfigString("tq_sendsmsmobile") };
                issend = Server
                        .getInstance()
                        .getAtomService()
                        .sendSms(mobiles, "订单提醒:请注意订单:" + orderinfo.getOrdernumber() + ",已支付需要处理" + con,
                                orderinfo.getId(), 46, dns, 0);
            }
            if ("1".equals(getSysconfigString("tq_iscreatepnronpay"))) {//支付完之后是否创建PNR
                String pnr = createPnrbyOrderOnpay(orderinfo);
                if (!"NOPNR".equals(pnr)) {
                    String ticketnumsql = "UPDATE T_ORDERINFO SET C_PNR='" + pnr + "' WHERE ID=" + orderinfo.getId();
                    Server.getInstance().getSystemService().findMapResultBySql(ticketnumsql, null);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return issend;
    }

    /**
     * 当支付后自动根据订单生成pnr
     * @param orderid
     * @return
     */
    public static String createPnrbyOrderOnpay(Orderinfo orderinfo) {
        List<Segmentinfo> listsegmenginf = Server.getInstance().getAirService()
                .findAllSegmentinfo("WHERE C_ORDERID=" + orderinfo.getId(), "ORDER BY ID", -1, 0);
        List<Passenger> listpassengers = Server.getInstance().getAirService()
                .findAllPassenger("WHERE C_ORDERID=" + orderinfo.getId(), "ORDER BY ID", -1, 0);
        String pnr = "NOPNR";
        pnr = Server.getInstance().getTicketSearchService().CreatePNRByCmd(listsegmenginf, listpassengers, "");
        return pnr;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    /**
     * 获得验签名的数据
     * 
     * @param map
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String getVerifyData(Map map) {
        String notify_data = (String) ((Object[]) map.get("notify_data"))[0];
        return "notify_data=" + notify_data;
    }
    // public abstract void orderhandle(String ordernumber);

}
