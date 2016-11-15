<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>


<%
    //人民币账号
    //数字串
    //与提交订单时的快钱账号保持一致
    String merchantAcctId = request.getParameter("merchantAcctId");

    // 网关版本
    //固定值：v2.0
    //与提交订单时的网关版本号保持一致
    String version = request.getParameter("version");

    //网言种页类显示语
    //固定选择值：1
    //1表示快钱支付网关网页是中文显示s
    String language = request.getParameter("language");
    //签名类型
    //固定值：1与提交订单时的签名类型保持一致
    String signType = request.getParameter("signType");
    //支付方式
    //固定选择值：00、10、11、12、13、14
    //与提交订单时的支付方式保持一致
    String payType = request.getParameter("payType");

    //银行代码 字符串
    //返回用户在实际支付时所使用的银行代码
    String bankId = request.getParameter("bankId");

    //商户订单号
    //字母、数字、-_及其组合
    //与提交订单时的商户订单号保持一致
    String orderId = request.getParameter("orderId");

    //交商时户间订单提
    //数字串
    //与提交订单时的商户订单提交时间保持一致

    String orderTime = request.getParameter("orderTime");

    //商额户订单金
    //整型数字以分为单位。比方10元，提交时金额应为1000
    //与提交订单时的商户订单金额保持一致
    String orderAmount = request.getParameter("orderAmount");
    // 快钱交易号
    //数字串 该交易在快钱系统中对应的交易号
    String dealId = request.getParameter("dealId");
    //银行交易号
    //数字串
    //该为空交易在银行支付时对应的交易号，如果不是通过银行卡支付，则为空
    String bankDealId = request.getParameter("bankDealId");
    //快间钱交易时间
    //数字串
    //快钱对交易进行处理的时间,格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
    String dealTime = request.getParameter("dealTime");

    //订付金单额实际支
    //整型数字
    //返回在使用优惠券等情况后，用户实际支付的金额
    //以分为单位。比方10元，提交时金额应为1000
    String payAmount = request.getParameter("payAmount");
    //费用快整型钱收数取字 商户的手续费，单位为分。
    String fee = request.getParameter("fee");
    //扩展字段1
    //字符串
    //与提交订单时的扩展字段1 保持一致
    String ext1 = request.getParameter("ext1");

    //扩展字段2
    //字符串
    //与提交订单时的扩展字段1 保持一致
    String ext2 = request.getParameter("ext2");

    //处理结果
    //10支付成功
    //11 支付失败
    //00订单申请成功
    // 01 订单申请失败
    String payResult = request.getParameter("payResult");
    //错误代码
    //失详细败资时料返见回下的文错参误代考码资料，可。以为空。
    String errCode = request.getParameter("errCode");
    String signMsg = request.getParameter("signMsg");
    System.out.println(signMsg + "dddddd");

    String merchantSignMsgVal = "";
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId", merchantAcctId);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "language", language);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType", signType);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime", orderTime);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount", orderAmount);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId", bankDealId);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime", dealTime);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount", payAmount);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult", payResult);
    merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
    //MD5加密
    //  System.out.println(signMsgVal);
    //  String signMsg = MD5Util.md5Hex(signMsgVal.getBytes("gb2312")).toUpperCase();
    //  System.out.println(signMsg);
    //  if(signMsg==signMsg1){
    // out.println("验签成功");
    //   }
    //out.println("signMsg="+signMsg);
    // out.println("<br>signMsg1="+signMsg1);

    Pkipair pki = new Pkipair();

    boolean flag = pki.enCodeByCer(merchantSignMsgVal, signMsg, "");
    System.out.println(flag);
    //初始化结果及地址
    int rtnOK = 0;
    String rtnUrl = "";
    //验证签名字符串
    //if(usd.getVerifySignResult())
    if (flag) {
        switch (Integer.parseInt(payResult)) {
        case 10:

            //*  
            // 商户网站逻辑处理，比方更新订单支付状态为成功
            List<Traderecord> listtr = Server.getInstance().getMemberService()
                    .findAllTraderecord(" where " + Traderecord.COL_code + " = '" + orderTime + "'", "", -1, 0);
            if (listtr != null && listtr.size() > 0) {
                Traderecord traderecord = listtr.get(0);
                traderecord.setState(1);
                traderecord.setCode(dealId);
                Server.getInstance().getMemberService().updateTraderecordIgnoreNull(traderecord);
                String where = " where 1=1 and " + Orderinfo.COL_ordernumber + " = '" + ext2 + "'";
                List<Orderinfo> list = Server.getInstance().getAirService().findAllOrderinfo(where, "", -1, 0);
                if (list != null && list.size() > 0) {
                    Orderinfo orderinfo = list.get(0);
                    orderinfo.setPaymethod(1);
                    orderinfo.setPaystatus(1);
                    orderinfo.setOrderstatus(2);
                    Server.getInstance().getAirService().updateOrderinfoIgnoreNull(orderinfo);
                    if (orderinfo.getRelationorderid() != null && orderinfo.getRelationorderid() > 0) {
                        Orderinfo orderinfo2 = Server.getInstance().getAirService()
                                .findOrderinfo(orderinfo.getRelationorderid());
                        orderinfo2.setPaymethod(1);
                        orderinfo2.setPaystatus(1);
                        orderinfo2.setOrderstatus(2);
                        Server.getInstance().getAirService().updateOrderinfoIgnoreNull(orderinfo2);
                    }
                }
            }
            // 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，且payResult=10，才表示支付成功！同时将订单金额与提交订单前的订单金额进行对比校验。
            //*

            //报告给快钱处理结果，并提供将要重定向的地址。
            rtnOK = 1;
            rtnUrl = "http://115.236.41.218:80/hz_interface/billpay/show.jsp?msg=success!";
            break;

        default:

            rtnOK = 1;
            rtnUrl = "http://115.236.41.218:80/hz_interface/billpay/show.jsp?msg=false!";
            break;
        }
    }
    else {
        rtnOK = 1;
        rtnUrl = "http://115.236.41.218:80/hz_interface/billpay/show.jsp?msg=error!";
    }
%>




<%!public String appendParam(String returns, String paramId, String paramValue) {
        if (returns != "") {
            if (paramValue != "") {

                returns += "&" + paramId + "=" + paramValue;
            }

        }
        else {

            if (paramValue != "") {
                returns = paramId + "=" + paramValue;
            }
        }

        return returns;
    }%>
<%@page import="java.util.List"%>
<%@page import="com.ccservice.b2b2c.base.traderecord.Traderecord"%>
<%@page import="com.ccservice.b2b2c.atom.server.Server"%>
<%@page import="com.ccservice.b2b2c.base.orderinfo.Orderinfo"%>
<%@page import="com.billpay.pki.Pkipair"%>
<result><%=rtnOK%></result>
<redirecturl><%=rtnUrl%></redirecturl>

