package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * @author hanmh 手机客户端支付通知
 * 
 */
@SuppressWarnings("serial")
public class AlipayMobileNotifyHandle extends NotifyHandleSupport {

    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WriteLog.write("手机客户端支付通知", "支付宝手机客户端支付通知");
        // 获得通知参数
        Map map = request.getParameterMap();
        // 获得通知签名
        String sign = (String) ((Object[]) map.get("sign"))[0];
        // 获得待验签名的数据
        String verifyData = getVerifyData(map);
        boolean verified = false;
        // 使用支付宝公钥验签名
        try {
            verified = RSASignature.doCheck(verifyData, sign, PartnerConfig.RSA_ALIPAY_PUBLIC);
        }
        catch (Exception e) {
            WriteLog.write("手机客户端支付通知", "手机客户端支付 支付宝公钥签名验证异常" + e.fillInStackTrace());
        }
        PrintWriter out = response.getWriter();
        // 验证签名通过
        if (verified) {
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

            WriteLog.write("手机客户端支付通知", "交易结果：" + trade_status);
            if ("TRADE_SUCCESS".equals(trade_status)) {
                String out_trade_no = root.elementTextTrim("out_trade_no");// 商户订单号
                String total_fee = root.elementTextTrim("total_fee");// 交易金额
                String trade_no = root.elementTextTrim("trade_no");// 外部交易号
                String buyer_email = root.elementTextTrim("buyer_email");
                String subject = root.elementTextTrim("subject");//订单类型  酒店预订、飞机票预订
                WriteLog.write("手机客户端支付通知", subject + "----->交易订单号：" + out_trade_no + ",交易金额：" + total_fee + ",外部交易号："
                        + trade_no + ",买家帐号：" + buyer_email);
                if ("酒店预订".equals(subject)) {
                    super.orderHandle(out_trade_no + "Fg" + HotelorderreturnHandle.class.getSimpleName(), trade_no,
                            Float.valueOf(total_fee), Paymentmethod.ALIPAY, "");
                }
                else {
                    super.orderHandle(out_trade_no + "Fg" + AirnofiryHandle.class.getSimpleName(), trade_no,
                            Float.valueOf(total_fee), Paymentmethod.ALIPAY, "");
                }
                out.print("success");
            }
            else if ("WAIT_BUYER_PAY".equals(trade_status)) {
                out.print("success");
            }
        }
        else {
            WriteLog.write("手机客户端支付通知", "接收支付宝系统通知验证签名失败，请检查！");
            out.print("fail");
        }
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
