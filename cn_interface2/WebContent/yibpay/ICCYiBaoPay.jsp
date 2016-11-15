
<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
<%@ page contentType="text/html; charset=GBK"%>
<%@ taglib uri="webwork" prefix="ww"%>
<%@page import="java.util.Map"%>
<%@page import="com.ccservice.b2b2c.atom.server.Server"%>
<%@page import="com.ccservice.b2b2c.base.hotelorder.Hotelorder"%>
<%@page import="com.ccservice.b2b2c.base.traderecord.Traderecord"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.sql.SQLException"%>


<%
    String orderid = request.getParameter("p5_Pid");// 订单ID
			String orderprice = request.getParameter("p3_Amt"); //订单价格
			String pricetype = request.getParameter("p4_Cur"); //价格单位
			System.out.println("--------------orderid==" + orderid
					+ ",orderprice==" + orderprice + ",pricetype=" + pricetype);
%>

<%!// 生成表单前处理业务逻辑
	public void logicBeforeSendReq(long id, String orderprice, String pricetype) {
		//HttpServletRequest request = ServletActionContext.getRequest();
		//String orderid= request.getParameter("orderid");

		System.out.println("orderid==" + id);
		Hotelorder hotelorder = Server.getInstance().getHotelService().findHotelorder(id);
		System.out.println("hotelorder==" + hotelorder);
		if (hotelorder != null) {
			String subject = "中视国际酒店支付";
			String body = "中视国际酒店支付订单号:" + hotelorder.getOrderid();
			if (hotelorder.getPrice().equals(orderprice)
					&& hotelorder.getPricecurrency().equals(pricetype)) {

				System.out.println("OKOK");
				//写入支付记录
				Traderecord traderecord = new Traderecord();
				//traderecord.setCode(get_order);
				traderecord.setCreatetime(new Timestamp(System
						.currentTimeMillis()));
				traderecord.setCreateuser("创建用户");
				traderecord.setDescription(subject);
				traderecord.setGoodsdesc(body);
				traderecord.setGoodsname(subject);
				traderecord.setModifytime(new Timestamp(System
						.currentTimeMillis()));
				traderecord.setModifyuser("修改用户");
				//traderecord.setOrdercode(extra_common_param);
				traderecord.setPayname("服务名称");
				traderecord.setPaytype(2);//0支付宝 1财付通 2易宝
				traderecord.setRetcode("返回码");
				traderecord.setState(0);//0等待支付1支付成功2支付失败
				traderecord.setTotalfee((int) Double.parseDouble(hotelorder
						.getPrice()) * 100);//支付金额分为单位
				traderecord.setType(1);//订单类型
				//traderecord.setPaymothed(paymethod);//支付方式
				//traderecord.setBankcode(defaultbank);//支付银行
				try {
					traderecord = Server.getInstance().getMemberService()
							.createTraderecord(traderecord);
					System.out.println("traderecord==" + traderecord);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("交易失败！");
					e.printStackTrace();
					return;
				}

			} else {//非法请求,篡改参数...

			}
		}

	}
	// 生成表单后处理业务逻辑 
	public void logicAfterSendReq() {

	}%>
<%
    logicBeforeSendReq(Long.parseLong(orderid), orderprice, pricetype);
%>







<html>
<head>
<link href="tip-yellowsimple.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="jquery.blockUI.js"></script>
<script type="text/javascript">
	$(function() {
		loading("正在跳转到支付页面");
		document.form.submit();
		//遮罩效果  
	});
	function loading(context) {
		//遮罩效果  
		$.blockUI({
			message : context + ',请稍候...'
		});
	}
</script>
</head>
<body>
	<%
	    try {
	        // out.println(ICC.getICCBuyRequestForm(request,"form","提交"));

	    }
	    catch (Exception e) {
	        out.println(e);
	    }
	%>
	logicAfterSendReq(); %>
</body>
</html>
