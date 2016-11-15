package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.billpay.pki.Pkipair;
import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Paymentmethod;

@SuppressWarnings("serial")
public class BillpayfenNotifyHandle extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        this.doPost(request, response);
    }

    public String parseStr(String str) {
        if (str != null && str.length() > 0) {
            str = str.trim();
        }
        else {
            str = "";
        }
        return str;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            WriteLog.write("快钱支付返回", name + ":" + valueStr);
        }
        WriteLog.write("快钱支付返回", "------------------------------------------------");
        // 获取网关版本.固定值
        // /快钱会根据版本号来调用对应的接口处理程序。
        // /本代码版本号固定为v2.0
        String version = (String) request.getParameter("version");
        version = parseStr(version);
        // 获取语言种类.固定选择值。
        // /只能选择1、2、3
        // /1代表中文；2代表英文
        // /默认值为1
        String language = (String) request.getParameter("language");
        language = parseStr(language);
        // 签名类型.固定值
        // /1代表MD5签名
        // /当前版本固定为1
        String signType = (String) request.getParameter("signType");
        signType = parseStr(signType);
        // 获取支付方式
        // /值为：10、11、12、13、14
        // /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
        String payType = (String) request.getParameter("payType");
        payType = parseStr(payType);
        // 获取银行代码
        // /参见银行代码列表
        String bankId = (String) request.getParameter("bankId");
        bankId = parseStr(bankId);
        // 获取商户订单号
        String orderId = (String) request.getParameter("orderId");
        orderId = parseStr(orderId);
        // 获取订单提交时间
        // /获取商户提交订单时的时间.14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
        // /如：20080101010101
        String orderTime = (String) request.getParameter("orderTime");
        orderTime = parseStr(orderTime);
        // 获取原始订单金额
        // /订单提交到快钱时的金额，单位为分。
        // /比方2 ，代表0.02元merchantAcctId
        String orderAmount = (String) request.getParameter("orderAmount");
        orderAmount = parseStr(orderAmount);
        //快钱合作伙伴账户号
        String pid = request.getParameter("pid");
        pid = parseStr(pid);
        // String suppleAmount =
        // (String)request.getParameter("suppleAmount").trim();

        String merchantAcctId = (String) request.getParameter("merchantAcctId");
        merchantAcctId = parseStr(merchantAcctId);
        // 获取快钱交易号
        // /获取该交易在快钱的交易号
        String dealId = (String) request.getParameter("dealId");
        dealId = parseStr(dealId);
        // 获取银行交易号
        // /如果使用银行卡支付时，在银行的交易号。如不是通过银行支付，则为空
        String bankDealId = (String) request.getParameter("bankDealId");
        bankDealId = parseStr(bankDealId);
        // 获取在快钱交易时间
        // /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
        // /如；20080101010101
        String dealTime = (String) request.getParameter("dealTime");
        dealTime = parseStr(dealTime);
        // 获取实际支付金额
        // /单位为分
        // /比方 2 ，代表0.02元
        String payAmount = (String) request.getParameter("payAmount");
        payAmount = parseStr(payAmount);
        // 获取交易手续费
        // /单位为分
        // /比方 2 ，代表0.02元
        String fee = (String) request.getParameter("fee");
        fee = parseStr(fee);
        // 获取扩展字段1-agentid
        String ext1 = (String) request.getParameter("ext1");
        ext1 = parseStr(ext1);
        // 获取扩展字段2
        String ext2 = (String) request.getParameter("ext2");
        ext2 = parseStr(ext2);
        // 获取处理结果
        // /10代表 成功11代表 失败
        // /00代表 下订单成功（仅对电话银行支付订单返回）;01代表 下订单失败（仅对电话银行支付订单返回）
        String payResult = (String) request.getParameter("payResult");
        payResult = parseStr(payResult);
        String payeeContactType = request.getParameter("payeeContactType");
        payeeContactType = parseStr(payeeContactType);
        String payeeContact = request.getParameter("payeeContact");
        payeeContact = parseStr(payeeContact);
        String payeeAmount = request.getParameter("payeeAmount");
        payeeAmount = parseStr(payeeAmount);
        String sharingResult = request.getParameter("sharingResult");
        sharingResult = parseStr(sharingResult);
        // 获取错误代码
        // /详细见文档错误代码列表
        String errCode = (String) request.getParameter("errCode");
        errCode = parseStr(errCode);
        // 获取加密签名串
        String signMsg = (String) request.getParameter("signMsg");
        signMsg = parseStr(signMsg);
        // 生成加密串。必须保持如下顺序。
        String merchantSignMsgVal = "";
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "language", language);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType", signType);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "pid", pid);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime", orderTime);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount", orderAmount);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId", bankDealId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime", dealTime);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount", payAmount);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payeeContactType", payeeContactType);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payeeContact", payeeContact);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payeeAmount", payeeAmount);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult", payResult);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "sharingResult", sharingResult);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
        Pkipair Pkipair = new Pkipair();
        boolean b = Pkipair.enCodeByCer(merchantSignMsgVal, signMsg, ext1);
        String resultMsg = "";
        String temp = "";
        String notifydomain = PropertyUtil.getValue("notifydomain", "GpAir.properties");
        if (b && "10".equals(payResult)) {
            insertPayCer(requestParams);
            System.out.println("支付成功：" + ext2);
            resultMsg = "成功";
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
            float f = Float.valueOf(payAmount) / 100;
            if (version != null && version.contains("mobile")) {
                payhandle.orderHandle(ordernumber, orderId, f, 16, merchantAcctId);
            }
            else {
                payhandle.orderHandle(ordernumber, orderId, f, Paymentmethod.BILLPAY, merchantAcctId);
            }
            temp = "http://" + notifydomain + "/cn_interface/billpay/show.jsp?payresult=1";
        }
        else {
            resultMsg = "失败";
            temp = "http://" + notifydomain + "/cn_interface/billpay/show.jsp?payresult=3";

        }
        response.setContentType("text/html;charset=GBK");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.print("<result>1</result><redirecturl>http://" + notifydomain
                    + "/cn_interface/paysuccess.jsp?msg=success</redirecturl>");
            out.flush();
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 生成凭证快照
     * @param requestParams
     */
    public void insertPayCer(Map requestParams) {
        try {
            String sql = "INSERT INTO dbo.GpPayCertificate( signType,version,orderAmount,payeeContactType,orderId ,dealTime ,payAmount ,payeeContact ,errCode ,"
                    + "merchantAcctId ,orderTime ,dealId ,payeeAmount ,sharingResult ,bankId ,fee ,bankDealId ,payResult ,ext1 ,ext2 ,payType ,language ,orderVersion)   "
                    + "select '" + findValue(requestParams, "signType") + "' ,'" + findValue(requestParams, "version")
                    + "' ,'" + findValue(requestParams, "orderAmount") + "' ,'"
                    + findValue(requestParams, "payeeContactType") + "' ,'" + findValue(requestParams, "orderId")
                    + "' ,'" + findValue(requestParams, "dealTime") + "' ,'" + findValue(requestParams, "payAmount")
                    + "' ,'" + findValue(requestParams, "payeeContact") + "' ,'" + findValue(requestParams, "errCode")
                    + "' ,'" + findValue(requestParams, "merchantAcctId") + "' ,'"
                    + findValue(requestParams, "orderTime") + "' ,'" + findValue(requestParams, "dealId") + "' ,'"
                    + findValue(requestParams, "payeeAmount") + "' ,'" + findValue(requestParams, "sharingResult")
                    + "' ,'" + findValue(requestParams, "bankId") + "' ,'" + findValue(requestParams, "fee") + "' ,'"
                    + findValue(requestParams, "bankDealId") + "' ,'" + findValue(requestParams, "payResult") + "' ,'"
                    + findValue(requestParams, "ext1") + "' ,'" + findValue(requestParams, "ext2") + "' ,'"
                    + findValue(requestParams, "payType") + "' ,'" + findValue(requestParams, "language") + "' ,'"
                    + findValue(requestParams, "orderVersion") + "' where dealId='" + findValue(requestParams, "dealId")
                    + "' having count(1)<=0";
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String findValue(Map requestParams, String key) {
        if (requestParams.containsKey(key)) {
            return requestParams.get(key) == null ? "" : requestParams.get(key).toString();
        }
        else {
            return "";
        }
    }

    // 功能函数。将变量值不为空的参数组成字符串
    public String appendParam(String returnStr, String paramId, String paramValue) {
        if (paramValue == null) {
            return returnStr;
        }
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
    // 功能函数。将变量值不为空的参数组成字符串。结束

    // 以下报告给快钱处理结果，并提供将要重定向的地址

}
