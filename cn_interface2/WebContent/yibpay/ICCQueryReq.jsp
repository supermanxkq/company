<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
<%@page import="java.util.Map"%>
<%! 
// 提交前处理业务逻辑
public void logicBeforeSendReq() {
}
// 提交后处理业务逻辑
public void logicAfterSendReq() {
}
%>
<%
logicBeforeSendReq();
// Map map = ICC.getICCQueryRequestBackMap(request);
Map map = null;

if((Boolean)map.get("httpConnection") == false){
out.println("通讯失败" + "<br/>");
}else{
Map parameter = (Map)map.get("parameter");
Boolean checkHmac = (Boolean)map.get("checkHmac");
if((Boolean)map.get("checkHmac") ){

out.println("接口类型:" + parameter.get("r0_Cmd") + "<br>");
out.println("查询结果:" + parameter.get("r1_Code") + "<br>");
out.println("商户编号:" + parameter.get("p1_MerId") + "<br>");
out.println("交易流水号:" + parameter.get("r2_TrxId") + "<br>");
out.println("交易金额:" + parameter.get("r3_Amt") + "<br>");
out.println("交易币种:" + parameter.get("r4_Cur") + "<br>");
out.println("商户订单号:" + parameter.get("r6_Order") + "<br>");
out.println("支付状态:" + parameter.get("rb_PayStatus") + "<br>");
out.println("订单完成时间:" + parameter.get("sd_paySuccessTime") + "<br>");
out.println("退款次数:" + parameter.get("rc_RefundCount") + "<br>");
out.println("已退款金额:" + parameter.get("rd_RefundAmt") + "<br>");
out.println("商户自定义信息1:" + parameter.get("pe_extInfo1") + "<br>");
out.println("商户自定义信息2:" + parameter.get("pe_extInfo2") + "<br>");
out.println("商户自定义信息3:" + parameter.get("pe_extInfo3") + "<br>");
out.println("商户自定义信息4:" + parameter.get("pe_extInfo4") + "<br>");
out.println("返回信息:" + parameter.get("errorMsg") + "<br>");

logicAfterSendReq();
}else{
out.println("交易签名无效" + "<br/>");
}
}
%> 