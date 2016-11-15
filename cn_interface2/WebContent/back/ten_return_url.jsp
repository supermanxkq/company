<%@ page language="java" contentType="text/html; charset=GBK"
	pageEncoding="GBK"%>

<%@ page import="com.tenpay.util.TenpayUtil"%>
<%@ page import="com.tenpay.PayResponseHandler"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    //密钥
    String key = "8934e7d15453e97507ef794cf7b0519d";

    //创建PayResponseHandler实例
    PayResponseHandler resHandler = new PayResponseHandler(request, response);

    resHandler.setKey(key);

    //判断签名 测试参数 http://localhost:8080/sj_interface/ten_return_url.jsp?pay_result=0&sp_billno=AIR20100702010794
    //把if改成true   reshandler 改成request
    if (resHandler.isTenpaySign()) {
        //交易单号
        String transaction_id = resHandler.getParameter("transaction_id");

        //金额金额,以分为单位
        String total_fee = resHandler.getParameter("total_fee");

        //支付结果
        String pay_result = resHandler.getParameter("pay_result");

        //商家订单号
        String sp_billno = resHandler.getParameter("sp_billno");

        //注意交易单不要重复处理
        Traderecord traderecord = new Traderecord();
        traderecord.setCode(transaction_id);
        traderecord.setCreatetime(new Timestamp(System.currentTimeMillis()));
        traderecord.setCreateuser("创建用户");
        traderecord.setDescription("机票订单备注：" + sp_billno);
        traderecord.setGoodsdesc("机票订单描述：" + sp_billno);
        traderecord.setGoodsname("机票订单：" + sp_billno);
        traderecord.setModifytime(new Timestamp(System.currentTimeMillis()));
        traderecord.setModifyuser("修改用户");
        traderecord.setOrdercode(sp_billno);
        traderecord.setPayname("服务名称");
        traderecord.setPaytype(1);//0支付宝 1财付通
        traderecord.setRetcode("返回码");
        traderecord.setState(Integer.parseInt(pay_result));//0成功，1支付成功2支付失败
        traderecord.setTotalfee(Integer.parseInt(total_fee));//支付金额
        traderecord.setType(1);//订单类型
        try {
            traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("交易失败！");
            e.printStackTrace();
        }
        if ("0".equals(pay_result)) {
            //------------------------------
            //处理业务开始
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
            //注意判断返回金额

            //------------------------------
            //处理业务完毕
            //------------------------------

            //调用doShow, 打印meta值跟js代码,告诉财付通处理成功,并在用户浏览器显示$show页面.
            resHandler.doShow("http://localhost:8080/sj_interface/show.jsp");
        }
        else {
            //当做不成功处理
            out.println("支付失败");
        }

    }
    else {
        out.println("认证签名失败");
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
<title>财付通支付回调处理</title>
</head>
<body>

</body>
</html>