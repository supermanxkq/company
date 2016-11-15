package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.util.Alipay_fuction;
import com.pay.config.AlipayConfig;

public abstract class AlipayReturnHandle extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String partner = AlipayConfig.getInstance().getPartnerID(); // 支付宝合作伙伴id (账户内提取)
        String privateKey = AlipayConfig.getInstance().getKey(); // 支付宝安全校验码(账户内提取)
        // **********************************************************************************
        // 如果您服务器不支持https交互，可以使用http的验证查询地址
        // *注意下面的注释，如果在测试的时候导致response等于空值的情况，请将下面一个注释，打开上面一个验证连接，另外检查本地端口，
        // 请打开80或者443端口
        // String alipayNotifyURL =
        // "https://www.alipay.com/cooperate/gateway.do?service=notify_verify"
        String alipayNotifyURL = "http://notify.alipay.com/trade/notify_query.do?" + "partner=" + partner
                + "&notify_id=" + request.getParameter("notify_id");
        // **********************************************************************************
        // 获得支付成功后支付宝返回的sign值
        String sign = request.getParameter("sign");
        // 获取支付宝ATN返回结果，true是正确的订单信息，false 是无效的
        String responseTxt = Alipay_fuction.checkurl(alipayNotifyURL);

        // 获得POST 过来参数设置到新的params中
        Map params = new HashMap();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化（现在已经使用）
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
            params.put(name, valueStr);
        }

        String mysign = Alipay_fuction.sign(params, privateKey);

        // mysign.equals(request.getParameter("sign")判断交易信息是否一致
        // responseTxt.equals("true")判断数据来源是否是支付宝
        if (mysign.equals(request.getParameter("sign")) && responseTxt.equals("true")) {
            if (request.getParameter("trade_status").equals("TRADE_FINISHED")
                    || request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
                // 以下输出测试时候用，可以删除
                String get_order = request.getParameter("out_trade_no");
                String trade_no = request.getParameter("trade_no");
                String get_total_fee = request.getParameter("total_fee");
                String get_subject = new String(request.getParameter("subject").getBytes("ISO-8859-1"), "UTF-8");
                String get_body = new String(request.getParameter("body").getBytes("ISO-8859-1"), "UTF-8");
                String extra_common_param = new String(request.getParameter("extra_common_param")
                        .getBytes("ISO-8859-1"), "UTF-8");
                String[] infos = extra_common_param.split("Fg");//支付时传入参数规范
                String ordernumber = infos[0];//获取订单号
                String handleName = infos[1];//获取handle类名
                PayHandle payhandle = null;
                try {
                    payhandle = (PayHandle) Class.forName(PayHandle.class.getPackage().getName() + "." + handleName)
                            .newInstance();
                }
                catch (Exception e) {

                }
                payhandle.orderHandle(ordernumber, trade_no, Float.valueOf(get_total_fee), 1, "");
                //						List<Traderecord> listtr=Server.getInstance().getMemberService().findAllTraderecord(" where "+Traderecord.COL_code+" = '"+get_order+"'","",-1,0);
                //						if(listtr!=null&&listtr.size()>0)
                //						{
                //							Traderecord traderecord=listtr.get(0);
                //							traderecord.setState(1);
                //							Server.getInstance().getMemberService().updateTraderecordIgnoreNull(traderecord);
                //							String where=" where 1=1 and "+Orderinfo.COL_ordernumber+" = '"+extra_common_param+"'";
                //							List<Orderinfo> list=Server.getInstance().getAirService().findAllOrderinfo(where, "", -1, 0);
                //							if(list!=null&&list.size()>0){
                //								Orderinfo orderinfo= list.get(0);
                //								orderinfo.setPaymethod(1);
                //								orderinfo.setPaystatus(1);
                //								//orderinfo.set
                //								orderinfo.setOrderstatus(2);
                //								Server.getInstance().getAirService().updateOrderinfoIgnoreNull(orderinfo);
                //								if(orderinfo.getRelationorderid()!=null&&orderinfo.getRelationorderid()>0)
                //								{
                //									Orderinfo orderinfo2=Server.getInstance().getAirService().findOrderinfo(orderinfo.getRelationorderid());
                //									orderinfo2.setPaymethod(1);
                //									orderinfo2.setPaystatus(1);
                //									orderinfo2.setOrderstatus(2);
                //									Server.getInstance().getAirService().updateOrderinfoIgnoreNull(orderinfo2);
                //								}
                //							}
                //						}
                response.sendRedirect("http://localhost:8080/lthk_interface/alipay/show.jsp");

            }
        }
        else {
            // out.println("支付失败");
            // 打印，收到消息比对sign的计算结果和传递来的sign是否匹配
            // out.println("mysign="+mysign +
            // "--------------------sign=" + sign + "<br>");
            // out.println("responseTxt=" + responseTxt + "<br>");

        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        this.doGet(request, response);
    }

    public abstract void orderhandle(String ordernumber);
}
