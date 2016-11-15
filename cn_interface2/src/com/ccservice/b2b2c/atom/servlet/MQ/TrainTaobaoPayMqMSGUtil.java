package com.ccservice.b2b2c.atom.servlet.MQ;

import java.util.Random;

import javax.jms.JMSException;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.util.ActiveMQUtil;

public class TrainTaobaoPayMqMSGUtil extends TongchengSupplyMethod {

    
    /**
     * 发送
     * @param order 订单
     * @param urltype 1 标识获取链接并去支付   0 只获取链接
     * @throws JMSException 
     */
    public void sendPayMQmsgByUrltype(long orderid, long urltype) throws JMSException {
        WriteLog.write("淘宝抢票支付测试", "orderid:"+orderid+";urltype:"+urltype);
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderid);
        WriteLog.write("淘宝抢票支付测试", "order:"+order);
        Customeruser user = getCustomeruserBy12306Account(order, new Random().nextInt(100000), true);
        WriteLog.write("淘宝抢票支付测试", "user:"+user);
        String loginname = user.getLoginname() == null ? "" : user.getLoginname();
        WriteLog.write("淘宝抢票支付测试", "loginname:"+loginname);
        String cookieString = user.getCardnunber() == null ? "" : user.getCardnunber();
        WriteLog.write("淘宝抢票支付测试", "cookieString:"+cookieString);
        float price = order.getOrderprice() == null ? 0 : order.getOrderprice();
        WriteLog.write("淘宝抢票支付测试", "price:"+price);
        String password = user.getLogpassword() == null ? "" : user.getLogpassword();
        WriteLog.write("淘宝抢票支付测试", "password:"+password);
        String mqname = MQMethod.ORDERGETURL_NAME;
        WriteLog.write("淘宝抢票支付测试", "mqname:"+mqname);
        int interfacetype = order.getInterfacetype() == null ? 0 : order.getInterfacetype().intValue();
        WriteLog.write("淘宝抢票支付测试", "interfacetype:"+interfacetype);
        String ordersource = getSysconfigString("ordersource");//订单来源
        WriteLog.write("淘宝抢票支付测试", "ordersource:"+ordersource);
        String paymqurl = getSysconfigString("payactiveMQ_url");//支付url
        WriteLog.write("淘宝抢票支付测试", "paymqurl:"+paymqurl);
        String timeout = "";
        if (order.getOrdertimeout() != null && !"".equals(order.getOrdertimeout())) {
            timeout = TimeUtil.parseDateToStringtime(order.getOrdertimeout(), 0, 4);
        }
        WriteLog.write("淘宝抢票支付测试", "timeout:"+timeout);
        String payurl = order.getChangesupplytradeno() == null ? "" : order.getChangesupplytradeno();
        WriteLog.write("淘宝抢票支付测试", "payurl:"+payurl);
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("orderid", orderid);
        jsoseng.put("loginname", loginname);
        jsoseng.put("cookieString", cookieString);
        jsoseng.put("password", password);
        jsoseng.put("extnumber", order.getExtnumber());
        jsoseng.put("price", price);
        jsoseng.put("ordernumber", order.getOrdernumber());
        jsoseng.put("ordersource", ordersource + "");
        jsoseng.put("payflag", urltype);//1 标识获取链接并去支付   0 只获取链接
        jsoseng.put("interfacetype", interfacetype);//6 淘宝
        jsoseng.put("timeout", timeout);//6 淘宝
        jsoseng.put("orderpayurl", payurl);//支付宝支付链接
        jsoseng.put("ordertype", order.getOrdertype());//订单类型  //3途牛提供12306账号逻辑      //4Cookie获取
        jsoseng.put("repInfo", user.getMemberemail() == null ? "" : user.getMemberemail());
        jsoseng.put("login12306Ip", user.getPostalcode() == null ? "" : user.getPostalcode());
//        try {
//            String sql = "sp_Select_TrainOrderPhoneMethod_IsCallBack @AgentId=" + order.getAgentid() + ",@OrderType="
//                    + order.getOrdertype();
//            List list1 = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
//            WriteLog.write("Put_payCallBackFlag", "ListSize:" + list1.size());
//            if (list1.size() > 0) {
//                Map map = (Map) list1.get(0);
//                String IsCallBack = map.get("IsCallBack").toString();
//                jsoseng.put("payCallBackFlag", Integer.valueOf(IsCallBack));
//
//            }
//            else {
                jsoseng.put("payCallBackFlag", 0);
//            }
//        }
//        catch (Exception e) {
//            WriteLog.write("Put_payCallBackFlag", "Exception:" + e.getMessage());
//            jsoseng.put("payCallBackFlag", 0);
//            e.printStackTrace();
//        }
        WriteLog.write("淘宝抢票支付测试", "jsoseng:"+jsoseng.toJSONString());
        WriteLog.write("12306_TrainpayMqMSGUtil_sendOrderPayMQmsgnew", jsoseng.toString());
        ActiveMQUtil.sendMessage(paymqurl, mqname, jsoseng.toString());
//        writeRC(orderid, "开始获取链接", "自动支付", 2, 1);
    }
    
    /**
     * 发送
     * @param order 淘宝抢票订单
     * @param urltype 1 标识获取链接并去支付   0 只获取链接
     * @throws JMSException 
     */
    public void sendPayMQmsgTaoBao(long orderid) throws JMSException {
        WriteLog.write("淘宝抢票支付z", "淘宝抢票进入支付");
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderid);
        String loginname1 = order.getSupplyaccount();
        WriteLog.write("淘宝抢票支付z", "淘宝抢票进入支付-----GO,logname=" + loginname1);
        //12306用户名
        Customeruser user = getCustomeruserBy12306Account(order, new Random().nextInt(100000),true);
        String loginname = user.getLoginname() == null ? "" : user.getLoginname();
        String password = user.getLogpassword() == null ? "asd123456" : user.getLogpassword();
        WriteLog.write("淘宝抢票支付z", "淘宝抢票进入支付-----GO1");
        String mqname = MQMethod.ORDERGETURL_NAME;
        int interfacetype = order.getInterfacetype() == null ? 0 : order.getInterfacetype().intValue();
        String ordersource = getSysconfigString("ordersource");//订单来源
        String paymqurl = getSysconfigString("payactiveMQ_url");//支付url
        String timeout = "";
        WriteLog.write("淘宝抢票支付z", "淘宝抢票进入支付-----GO2");
        if (order.getOrdertimeout() != null && !"".equals(order.getOrdertimeout())) {
            timeout = TimeUtil.parseDateToStringtime(order.getOrdertimeout(), 0, 4);
        }
        String cookieString = "";
        cookieString = user.getCardnunber() == null ? "" : user.getCardnunber();
        String payurl = order.getChangesupplytradeno() == null ? "" : order.getChangesupplytradeno();
        WriteLog.write("淘宝抢票支付z", "淘宝抢票进入支付-----GO3:COOK=" + cookieString + "Url=" + payurl);
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("orderid", orderid);
        jsoseng.put("loginname", loginname);
        jsoseng.put("cookieString", cookieString);
        jsoseng.put("password", password);
        jsoseng.put("extnumber", order.getExtnumber());
        jsoseng.put("ordernumber", order.getOrdernumber());
        jsoseng.put("ordersource", ordersource + "");
        jsoseng.put("payflag", 1);//1 标识获取链接并去支付   0 只获取链接
        jsoseng.put("interfacetype", interfacetype);//6 淘宝
        jsoseng.put("timeout", timeout);//6 淘宝
        jsoseng.put("orderpayurl", payurl);//支付宝支付链接
        jsoseng.put("ordertype", order.getOrdertype());//订单类型  //3途牛提供12306账号逻辑      //4Cookie获取
        jsoseng.put("repInfo", "");
        jsoseng.put("login12306Ip", user.getPostalcode() == null ? "" : user.getPostalcode());
        jsoseng.put("payCallBackFlag", 0);
        jsoseng.put("price", order.getOrderprice());
        WriteLog.write("淘宝抢票支付z", "paymqurl:" + paymqurl + " ;mqname:" + mqname);
        WriteLog.write("淘宝抢票支付z", jsoseng.toString());
        WriteLog.write("淘宝抢票支付z", "淘宝抢票发送支付啦~~！");
        ActiveMQUtil.sendMessage(paymqurl, mqname, jsoseng.toString());
    }

}
