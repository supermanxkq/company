
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
    String orderid = request.getParameter("p5_Pid");// ����ID
			String orderprice = request.getParameter("p3_Amt"); //�����۸�
			String pricetype = request.getParameter("p4_Cur"); //�۸�λ
			System.out.println("--------------orderid==" + orderid
					+ ",orderprice==" + orderprice + ",pricetype=" + pricetype);
%>

<%!// ���ɱ�ǰ����ҵ���߼�
	public void logicBeforeSendReq(long id, String orderprice, String pricetype) {
		//HttpServletRequest request = ServletActionContext.getRequest();
		//String orderid= request.getParameter("orderid");

		System.out.println("orderid==" + id);
		Hotelorder hotelorder = Server.getInstance().getHotelService().findHotelorder(id);
		System.out.println("hotelorder==" + hotelorder);
		if (hotelorder != null) {
			String subject = "���ӹ��ʾƵ�֧��";
			String body = "���ӹ��ʾƵ�֧��������:" + hotelorder.getOrderid();
			if (hotelorder.getPrice().equals(orderprice)
					&& hotelorder.getPricecurrency().equals(pricetype)) {

				System.out.println("OKOK");
				//д��֧����¼
				Traderecord traderecord = new Traderecord();
				//traderecord.setCode(get_order);
				traderecord.setCreatetime(new Timestamp(System
						.currentTimeMillis()));
				traderecord.setCreateuser("�����û�");
				traderecord.setDescription(subject);
				traderecord.setGoodsdesc(body);
				traderecord.setGoodsname(subject);
				traderecord.setModifytime(new Timestamp(System
						.currentTimeMillis()));
				traderecord.setModifyuser("�޸��û�");
				//traderecord.setOrdercode(extra_common_param);
				traderecord.setPayname("��������");
				traderecord.setPaytype(2);//0֧���� 1�Ƹ�ͨ 2�ױ�
				traderecord.setRetcode("������");
				traderecord.setState(0);//0�ȴ�֧��1֧���ɹ�2֧��ʧ��
				traderecord.setTotalfee((int) Double.parseDouble(hotelorder
						.getPrice()) * 100);//֧������Ϊ��λ
				traderecord.setType(1);//��������
				//traderecord.setPaymothed(paymethod);//֧����ʽ
				//traderecord.setBankcode(defaultbank);//֧������
				try {
					traderecord = Server.getInstance().getMemberService()
							.createTraderecord(traderecord);
					System.out.println("traderecord==" + traderecord);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("����ʧ�ܣ�");
					e.printStackTrace();
					return;
				}

			} else {//�Ƿ�����,�۸Ĳ���...

			}
		}

	}
	// ���ɱ�����ҵ���߼� 
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
		loading("������ת��֧��ҳ��");
		document.form.submit();
		//����Ч��  
	});
	function loading(context) {
		//����Ч��  
		$.blockUI({
			message : context + ',���Ժ�...'
		});
	}
</script>
</head>
<body>
	<%
	    try {
	        // out.println(ICC.getICCBuyRequestForm(request,"form","�ύ"));

	    }
	    catch (Exception e) {
	        out.println(e);
	    }
	%>
	logicAfterSendReq(); %>
</body>
</html>
