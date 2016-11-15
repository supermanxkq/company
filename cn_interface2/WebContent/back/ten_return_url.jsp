<%@ page language="java" contentType="text/html; charset=GBK"
	pageEncoding="GBK"%>

<%@ page import="com.tenpay.util.TenpayUtil"%>
<%@ page import="com.tenpay.PayResponseHandler"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    //��Կ
    String key = "8934e7d15453e97507ef794cf7b0519d";

    //����PayResponseHandlerʵ��
    PayResponseHandler resHandler = new PayResponseHandler(request, response);

    resHandler.setKey(key);

    //�ж�ǩ�� ���Բ��� http://localhost:8080/sj_interface/ten_return_url.jsp?pay_result=0&sp_billno=AIR20100702010794
    //��if�ĳ�true   reshandler �ĳ�request
    if (resHandler.isTenpaySign()) {
        //���׵���
        String transaction_id = resHandler.getParameter("transaction_id");

        //�����,�Է�Ϊ��λ
        String total_fee = resHandler.getParameter("total_fee");

        //֧�����
        String pay_result = resHandler.getParameter("pay_result");

        //�̼Ҷ�����
        String sp_billno = resHandler.getParameter("sp_billno");

        //ע�⽻�׵���Ҫ�ظ�����
        Traderecord traderecord = new Traderecord();
        traderecord.setCode(transaction_id);
        traderecord.setCreatetime(new Timestamp(System.currentTimeMillis()));
        traderecord.setCreateuser("�����û�");
        traderecord.setDescription("��Ʊ������ע��" + sp_billno);
        traderecord.setGoodsdesc("��Ʊ����������" + sp_billno);
        traderecord.setGoodsname("��Ʊ������" + sp_billno);
        traderecord.setModifytime(new Timestamp(System.currentTimeMillis()));
        traderecord.setModifyuser("�޸��û�");
        traderecord.setOrdercode(sp_billno);
        traderecord.setPayname("��������");
        traderecord.setPaytype(1);//0֧���� 1�Ƹ�ͨ
        traderecord.setRetcode("������");
        traderecord.setState(Integer.parseInt(pay_result));//0�ɹ���1֧���ɹ�2֧��ʧ��
        traderecord.setTotalfee(Integer.parseInt(total_fee));//֧�����
        traderecord.setType(1);//��������
        try {
            traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("����ʧ�ܣ�");
            e.printStackTrace();
        }
        if ("0".equals(pay_result)) {
            //------------------------------
            //����ҵ��ʼ
            //------------------------------ 
            if (traderecord.getId() > 0) {
                String where = " where 1=1 and " + Orderinfo.COL_ordernumber + " = '" + sp_billno + "'";
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
            //ע���жϷ��ؽ��

            //------------------------------
            //����ҵ�����
            //------------------------------

            //����doShow, ��ӡmetaֵ��js����,���߲Ƹ�ͨ����ɹ�,�����û��������ʾ$showҳ��.
            resHandler.doShow("http://localhost:8080/sj_interface/show.jsp");
        }
        else {
            //�������ɹ�����
            out.println("֧��ʧ��");
        }

    }
    else {
        out.println("��֤ǩ��ʧ��");
        //String debugInfo = resHandler.getDebugInfo();
        //System.out.println("debugInfo:" + debugInfo);
    }
%>
<%@page import="com.ccservice.b2b2c.base.traderecord.Traderecord"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.ccservice.b2b2c.atom.server.Server"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.util.List"%>
<%@page import="com.ccservice.b2b2c.base.orderinfo.Orderinfo"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>�Ƹ�֧ͨ���ص�����</title>
</head>
<body>

</body>
</html>