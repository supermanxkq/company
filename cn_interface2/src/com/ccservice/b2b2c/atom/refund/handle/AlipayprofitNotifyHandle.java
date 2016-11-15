package com.ccservice.b2b2c.atom.refund.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.util.Alipay_fuction;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.IB2BSystemService;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.b2b2c.ben.Refundtrade;
import com.pay.config.AlipayConfig;

@SuppressWarnings("serial")
public class AlipayprofitNotifyHandle extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // 获取支付宝POST过来反馈信息
        String responsestr = "fail";// success
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        String batch_no = request.getParameter("batch_no");// 批次号
        WriteLog.write("支付宝退款通知", "退款通知请求，批次号：" + batch_no);
        String result_details = request.getParameter("result_details");// 处理结果详情
        WriteLog.write("支付宝退款通知", "处理结果详情：" + result_details);
        String remark = "";
        String key = AlipayConfig.getInstance().getKey();
        String partner = AlipayConfig.getInstance().getPartnerID();
        String alipayNotifyURL = "http://notify.alipay.com/trade/notify_query.do?" + "partner=" + partner
                + "&notify_id=" + request.getParameter("notify_id");
        String responseTxt = Alipay_fuction.checkurl(alipayNotifyURL);
        String sign = request.getParameter("sign");
        String mysign = Alipay_fuction.sign(params, key);
        String ordernumber = batch_no.substring(14);
        Refundtrade fefundtrade = Server.getInstance().getMemberService().findRefundtrade(Long.valueOf(ordernumber));
        String handleclass = fefundtrade.getHandleclass();
        boolean success = false;
        if (sign.equals(mysign) && responseTxt.equals("true")) {// 验证成功
            WriteLog.write("支付宝退款通知", batch_no + "验证通过处理退废。退款结果：");
            // 原付款交易号^退交易金额^处理结果码^是否充退^充退处理结果。SUCCESS
            // 其中是否充退的可选值：true/false；充退处理结果的可选值：S（成功）、F（失败）、P（处理中）
            if (result_details != null && !result_details.trim().equalsIgnoreCase("")) {
                responsestr = "success";
                String[] batch_Data = result_details.split("#");// 退款笔数分割

                for (int loop = 0; loop < batch_Data.length; loop++) {
                    String[] layDetails = batch_Data[loop].split("[$|$$]");// 退款信息
                    for (int dLoop = 0; dLoop < layDetails.length; dLoop++) {
                        if (dLoop == 0) {
                            String[] details = layDetails[0].split("\\^");
                            if (details.length >= 3) {
                                WriteLog.write("支付宝退款通知", "details[2]:" + details[2]);
                                if (details[2].equalsIgnoreCase("SUCCESS")) {
                                    WriteLog.write("支付宝退款通知", batch_no + "退款成功 订单状态更改.支付交易号：" + details[0]);
                                    success = true;
                                    WriteLog.write("支付宝退款通知", batch_Data[loop]);
                                    String[] rebates = batch_Data[loop].split("\\|");
                                    IB2BSystemService service = Server.getInstance().getB2BSystemService();
                                    for (int i = 1; i < rebates.length; i++) {// 第一个为退款信息，故从1起
                                        String[] agentrebate = rebates[i].split("\\^");
                                        String account = agentrebate[0];// 支付宝帐号
                                        String price = agentrebate[4];// 金额
                                        String result = agentrebate[agentrebate.length - 1];// 退回分润结果
                                        WriteLog.write("支付宝退款通知", "获润账户：" + account + ";金额：" + price + ";分润扣回结果："
                                                + result);
                                        boolean reback = true;
                                        if (!result.equalsIgnoreCase("SUCCESS")) {// 扣回分润失败。
                                            reback = false;
                                            //////////updAgentprotocol(account);
                                        }
                                        try {
                                            List<Profitshare> shares = service.findProfitRefundByOid(fefundtrade
                                                    .getOrderid(), fefundtrade.getBtype());
                                            for (Profitshare share : shares) {
                                                if (account.equals(share.getAccount())) {
                                                    int pmethod = Paymentmethod.EBANKPAY;
                                                    if (reback) {
                                                        service.profitRefundsuccess(share.getId(), pmethod);
                                                    }
                                                    else {
                                                        service.profitRefundfail(share.getId(), pmethod);
                                                    }
                                                }
                                                if (AlipayConfig.getInstance().selfaccount && share.getPagentid() == 46) {
                                                    service.profitRefundsuccess(share.getId(), Paymentmethod.EBANKPAY);
                                                }
                                            }
                                        }
                                        catch (Exception e) {
                                            WriteLog.write("支付宝退款通知", "创建分润退回记录异常：" + e.fillInStackTrace());
                                        }
                                    }
                                }
                                else {
                                    remark = details[2];
                                    batch_no += ",REASON:";
                                    String[] rebates = batch_Data[loop].split("[$|$$]");
                                    for (int i = 1; i < rebates.length; i++) {// 第一个为退款信息，故从1起
                                        String[] agentrebate = rebates[i].split("\\^");
                                        String account = agentrebate[0];// 支付宝帐号
                                        //String price = agentrebate[4];// 金额
                                        String result = agentrebate[agentrebate.length - 1];// 退回分润结果
                                        if (!result.equalsIgnoreCase("CHECK_VALID_SUCCESS")) {
                                            batch_no += account + "|" + result + "|";
                                        }
                                    }
                                    WriteLog.write("支付宝退款通知", batch_no + "退款失败：" + remark);
                                    if (!details[2].equalsIgnoreCase("TRADE_HAS_CLOSED")) {
                                        responsestr = "fail";
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                WriteLog.write("支付宝退款通知", batch_no + "退款处理详情为空");
                remark = "退款处理详情为空";
            }
        }
        else {
            WriteLog.write("支付宝退款通知", batch_no + "验证失败，验证信息：mysign=" + mysign + "|request sign=" + sign
                    + "responseTxt:" + responseTxt);
        }
        try {
            RefundHandle refundhandle = (RefundHandle) Class.forName(
                    RefundHandle.class.getPackage().getName() + "." + handleclass).newInstance();
            refundhandle.refundedHandle(success, fefundtrade.getOrderid(), batch_no);
        }
        catch (Exception e) {
            WriteLog.write("支付宝退款通知", batch_no + "退款成功 订单状态更改异常" + e.fillInStackTrace());
        }
        try {
            PrintWriter out = response.getWriter();
            WriteLog.write("支付宝退款通知", "返回接受结果：" + responsestr);
            out.write(responsestr);
            out.flush();
            out.close();
        }
        catch (IOException e) {
        }
    }

    /**
     * 更改账户签约信息
     */
    public void updAgentprotocol(String alipaycount) {
        try {
            WriteLog.write("支付宝退款通知", alipaycount + "返润要回失败，修改其签约信息，不再分润");
            String sql = "UPDATE T_CUSTOMERAGENT SET C_ISPARTNER=0  WHERE C_ALIPAYACCOUNT='" + alipaycount + "'";
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {

        }

    }

    private static String getErrorReason(String reason) {
        if ("REASON_REFUND_ROYALTY_ERROR".equals(reason)) {
            return "退分润失败";
        }
        else if ("RESULT_ACCOUNT_NO_NOT_VALID".equals(reason)) {
            return "账号无效";
        }
        return reason;
    }

    // public Customeragent getAgentByAccount(String account){
    //		
    // }

}
