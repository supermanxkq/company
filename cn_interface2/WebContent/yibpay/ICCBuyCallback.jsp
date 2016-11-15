<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
<%@page import="java.util.Map"%>
<%
// Map map = ICC.getICCBuyCallbackMap(request);
Map map=null;

if(((String)request.getParameter("r9_BType")).equals("2")){
out.println("success</br>");
//在接收到支付结果通知后，判断是否进行过业务逻辑处理，不要重复进行业务逻辑处理
}
	if((Boolean)map.get("checkHmac")){
	out.println("接口类型:" + request.getParameter("r0_Cmd") + "</br>");
	out.println("返回码:" + request.getParameter("r1_Code") + "</br>");
	out.println("交易流水号:" + request.getParameter("r2_TrxId") + "</br>");
	out.println("商户编号:" + request.getParameter("p1_MerId") + "</br>");
	out.println("商户平台订单号:" + request.getParameter("p2_Order") + "</br>");
	out.println("交易金额:" + request.getParameter("p3_Amt") + "</br>");
	out.println("交易币种:" + request.getParameter("p4_cur") + "</br>");
	out.println("订单完成时间:" + request.getParameter("rp_PayDate") + "</br>");
	out.println("交易结果返回类型:" + request.getParameter("r9_BType") + "</br>");
	out.println("商户自定义信息1:" + request.getParameter("pe_extInfo1") + "</br>");
	out.println("商户自定义信息2:" + request.getParameter("pe_extInfo2") + "</br>");
	out.println("商户自定义信息3:" + request.getParameter("pe_extInfo3") + "</br>");
	out.println("商户自定义信息4:" + request.getParameter("pe_extInfo4") + "</br>");
	out.println("错误信息:" + request.getParameter("errorMsg") + "</br>");

	logicAtCallback();
	
	}else{
	out.println("交易签名无效");
	}
%>
<%!
public void logicAtCallback(){

}
%>