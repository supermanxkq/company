package com.ccservice.b2b2c.atom.servlet.MQ;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jms.JMSException;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.elong.inter.PropertyUtil;

public class TrainpayMqMSGUtil extends TongchengSupplyMethod {

    private String callbackurl;

    private String url;

    private String QUEUE_NAME = "";

    public TrainpayMqMSGUtil(String QUEUE_NAME) {
        this.QUEUE_NAME = QUEUE_NAME;
    }

    /**
     * 
     * @param order 订单
     * @param msgtype 发送信息类型
     * @throws JMSException 
     */
    public void sendGetUrlMQmsg(long orderid) throws JMSException {
        this.url = getActiveMQUrl(0);
        System.out.println(url);
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("type", MQMethod.ORDERGETURL);
        jsoseng.put("orderid", orderid);
        WriteLog.write("12306_TrainpayMqMSGUtil_GetUrl", jsoseng.toString());
        ActiveMQUtil.sendMessage(url, QUEUE_NAME, jsoseng.toString());
    }

    /**
     * 发送
     * @param order 淘宝抢票订单
     * @param urltype 1 标识获取链接并去支付   0 只获取链接
     * @throws JMSException 
     */
    public void sendPayMQmsgTaoBao(long orderid) throws JMSException {
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderid);
        Customeruser user = getCustomeruserBy12306Account(order, new Random().nextInt(100000), true);
        String loginname = user.getLoginname() == null ? "" : user.getLoginname();
        String password = user.getLogpassword() == null ? "asd123456" : user.getLogpassword();
        String mqname = MQMethod.ORDERGETURL_NAME;
        int interfacetype = order.getInterfacetype() == null ? 0 : order.getInterfacetype().intValue();
        String ordersource = getSysconfigString("ordersource");//订单来源
        String paymqurl = getSysconfigString("payactiveMQ_url");//支付url
        String timeout = "";
        if (order.getOrdertimeout() != null && !"".equals(order.getOrdertimeout())) {
            timeout = TimeUtil.parseDateToStringtime(order.getOrdertimeout(), 0, 4);
        }
        String cookieString = "";
        cookieString = user.getCardnunber() == null ? "" : user.getCardnunber();
        String payurl = order.getChangesupplytradeno() == null ? "" : order.getChangesupplytradeno();
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
        WriteLog.write("12306_TrainpayMqMSGUtil_sendOrderPayMQmsgnew", "paymqurl:" + paymqurl + " ;mqname:" + mqname);
        WriteLog.write("12306_TrainpayMqMSGUtil_sendOrderPayMQmsgnew", jsoseng.toString());
        ActiveMQUtil.sendMessage(paymqurl, mqname, jsoseng.toString());
    }

    /**
     * 
     * @param order 订单
     * @param msgtype 发送信息类型
     * @throws JMSException 
     */
    public void sendGetUrlMQmsgnew(long orderid) throws JMSException {
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderid);
        Customeruser user = getCustomeruserBy12306Account(order, new Random().nextInt(100000), true);
        String loginname = user.getLoginname() == null ? "" : user.getLoginname();
        String cookieString = user.getCardnunber() == null ? "" : user.getCardnunber();
        String password = user.getLogpassword() == null ? "" : user.getLogpassword();
        String mqname = MQMethod.ORDERPAY_NAME;
        int interfacetype = order.getInterfacetype() == null ? 0 : order.getInterfacetype().intValue();
        if (interfacetype == 6) {
            mqname = MQMethod.OrderPay_TaoBaoName;
        }
        String ordersource = getSysconfigString("ordersource");//订单来源
        this.url = PropertyUtil.getValue("PayMq_url", "Train.properties");
        String callbackurl = PropertyUtil.getValue("pay_callback", "Train.properties");
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("orderid", orderid);
        jsoseng.put("loginname", loginname);
        jsoseng.put("cookieString", cookieString);
        jsoseng.put("callbackurl", callbackurl);
        jsoseng.put("mqname", mqname);
        jsoseng.put("extnumber", order.getExtnumber());
        jsoseng.put("ordernumber", order.getOrdernumber());
        jsoseng.put("password", password);
        jsoseng.put("ordersource", ordersource + "");
        jsoseng.put("payflag", "1");//1 标识获取链接并去支付   0 只获取链接
        jsoseng.put("ordertype", order.getOrdertype());//订单类型
        jsoseng.put("repInfo", user.getMemberemail() == null ? "" : user.getMemberemail());
        jsoseng.put("login12306Ip", user.getPostalcode() == null ? "" : user.getPostalcode());
        WriteLog.write("12306_TrainpayMqMSGUtil_GetUrl", jsoseng.toString());
        ActiveMQUtil.sendMessage(url, QUEUE_NAME, jsoseng.toString());
    }

    /**
     * 高改支付
     * @param order 订单
     * @param change 改签
     * @param user 下单账户
     * @param timeout 支付超时时间，目前淘宝有，其他传空
     */
    public void sendPayMQmsgGQ(Trainorder order, Trainorderchange change, Customeruser user, String timeout)
            throws JMSException {
        JSONObject json = new JSONObject();
        json.put("timeout", timeout);
        json.put("orderid", change.getId());
        json.put("busstype", "2");//业务类型>>改签
        json.put("loginname", user.getLoginname());
        json.put("extnumber", order.getExtnumber());
        json.put("password", user.getLogpassword());
        json.put("ordernumber", change.getTcnumber());
        json.put("cookieString", user.getCardnunber());
        json.put("repInfo", user.getMemberemail() == null ? "" : user.getMemberemail());
        json.put("login12306Ip", user.getPostalcode() == null ? "" : user.getPostalcode());
        json.put("payflag", 1);//1:获取链接并去支付；0:只获取链接
        json.put("ordertype", order.getOrdertype());//订单类型
        json.put("interfacetype", order.getInterfacetype());
        json.put("ordersource", getSysconfigString("ordersource"));
        json.put("orderpayurl", change.getPayaddress() == null ? "" : change.getPayaddress());//支付宝支付链接
        //日志
        WriteLog.write("12306_TrainpayMqMSGUtil_sendPayMQmsgGQ", json.toString());
        //MQ
        String mqname = MQMethod.GQPay_NAME;
        String paymqurl = getSysconfigString("payactiveMQ_url");
        ActiveMQUtil.sendMessage(paymqurl, mqname, json.toString());
        //日志内容
        String changeFlag = change.getChangeArrivalFlag() == 1 ? "变更到站" : "改签";
        trainRC(order.getId(), "[确认 - " + change.getId() + "]开始获取" + changeFlag + "支付链接");
    }

    /**
     * 
     * @param order 订单
     * @param msgtype 发送信息类型
     * @throws JMSException 
     */
    public void sendGetUrlMQmsgOnlyUrl(long orderid) throws JMSException {
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderid);
        Customeruser user = getCustomeruserBy12306Account(order, new Random().nextInt(100000), true);
        String loginname = user.getLoginname() == null ? "" : user.getLoginname();
        String cookieString = user.getCardnunber() == null ? "" : user.getCardnunber();
        String password = user.getLogpassword() == null ? "" : user.getLogpassword();
        String mqname = MQMethod.ORDERPAY_NAME;
        int interfacetype = order.getInterfacetype() == null ? 0 : order.getInterfacetype().intValue();
        if (interfacetype == 6) {
            mqname = MQMethod.OrderPay_TaoBaoName;
        }
        String ordersource = getSysconfigString("ordersource");//订单来源
        this.url = PropertyUtil.getValue("PayMq_url", "Train.properties");
        String callbackurl = PropertyUtil.getValue("pay_callback", "Train.properties");
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("orderid", orderid);
        jsoseng.put("loginname", loginname);
        jsoseng.put("cookieString", cookieString);
        jsoseng.put("callbackurl", callbackurl);
        jsoseng.put("mqname", mqname);
        jsoseng.put("extnumber", order.getExtnumber());
        jsoseng.put("ordernumber", order.getOrdernumber());
        jsoseng.put("password", password);
        jsoseng.put("ordersource", ordersource + "");
        jsoseng.put("payflag", "0");
        jsoseng.put("ordertype", order.getOrdertype());//订单类型
        jsoseng.put("repInfo", user.getMemberemail() == null ? "" : user.getMemberemail());
        jsoseng.put("login12306Ip", user.getPostalcode() == null ? "" : user.getPostalcode());
        WriteLog.write("12306_TrainpayMqMSGUtil_GetUrl", jsoseng.toString());
        ActiveMQUtil.sendMessage(url, QUEUE_NAME, jsoseng.toString());
    }

    /**
     * 改签获取支付链接
     * @param order 订单
     * @param msgtype 发送信息类型
     * @throws JMSException 
     */
    public void sendGetGQUrlMQmsg(Trainorder order, Trainorderchange orderchange) throws JMSException {
        this.url = getActiveMQUrl(0);
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("type", MQMethod.GQORDERURL);
        jsoseng.put("orderid", order.getId());
        jsoseng.put("changeorderid", orderchange.getId());
        WriteLog.write("12306_TrainpayMqMSGUtil_GQGetURL_MQ", jsoseng.toString());
        ActiveMQUtil.sendMessage(url, QUEUE_NAME, jsoseng.toString());
    }

    /**
     * 发送
     * @param order 订单
     * @param urltype 1 标识获取链接并去支付   0 只获取链接
     * @throws JMSException 
     */
    @SuppressWarnings("rawtypes")
    public void sendPayMQmsg(Trainorder o, int paytype, int paytimes) throws JMSException {
        Trainorder order = Server.getInstance().getTrainService().findTrainorder(o.getId());
        Customeruser user = getCustomeruserBy12306Account(order, new Random().nextInt(100000), true);
        String loginname = user.getLoginname() == null ? "" : user.getLoginname();
        String cookieString = user.getCardnunber() == null ? "" : user.getCardnunber();
        String password = user.getLogpassword() == null ? "" : user.getLogpassword();
        float price = order.getOrderprice() == null ? 0 : order.getOrderprice();
        String mqname = MQMethod.ORDERGETURL_NAME;
        int interfacetype = order.getInterfacetype() == null ? 0 : order.getInterfacetype().intValue();
        String payurl = order.getChangesupplytradeno() == null ? "" : order.getChangesupplytradeno();
        String ordersource = getSysconfigString("ordersource");//订单来源
        String paymqurl = getSysconfigString("payactiveMQ_url");//支付mq
        String timeout = "";
        if (order.getOrdertimeout() != null && !"".equals(order.getOrdertimeout())) {
            timeout = TimeUtil.parseDateToStringtime(order.getOrdertimeout(), 0, 4);
        }
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("orderid", order.getId());
        jsoseng.put("loginname", loginname);
        jsoseng.put("cookieString", cookieString);
        jsoseng.put("extnumber", order.getExtnumber());
        jsoseng.put("ordernumber", order.getOrdernumber());
        jsoseng.put("password", password);
        jsoseng.put("price", price);
        jsoseng.put("ordersource", ordersource + "");
        jsoseng.put("payflag", 1);//1 标识获取链接并去支付   0 只获取链接
        jsoseng.put("interfacetype", interfacetype);//6 淘宝
        jsoseng.put("timeout", timeout);//6 淘宝
        jsoseng.put("orderpayurl", payurl);//支付宝支付链接
        jsoseng.put("ordertype", order.getOrdertype());//订单类型  //3途牛提供12306账号逻辑      //4Cookie获取
        jsoseng.put("repInfo", user.getMemberemail() == null ? "" : user.getMemberemail());
        jsoseng.put("login12306Ip", user.getPostalcode() == null ? "" : user.getPostalcode());
        try {
            String sql = "sp_Select_TrainOrderPhoneMethod_IsCallBack @AgentId=" + order.getAgentid() + ",@OrderType="
                    + order.getOrdertype();
            List list1 = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
            WriteLog.write("Put_payCallBackFlag", "ListSize:" + list1.size());
            if (list1.size() > 0) {
                Map map = (Map) list1.get(0);
                String IsCallBack = map.get("IsCallBack").toString();
                jsoseng.put("payCallBackFlag", Integer.valueOf(IsCallBack));

            }
            else {
                jsoseng.put("payCallBackFlag", 0);
            }
        }
        catch (Exception e) {
            WriteLog.write("Put_payCallBackFlag", "Exception:" + e.getMessage());
            jsoseng.put("payCallBackFlag", 0);
            e.printStackTrace();
        }
        WriteLog.write("12306_TrainpayMqMSGUtil_sendOrderPayMQmsgnew", jsoseng.toString());
        ActiveMQUtil.sendMessage(paymqurl, mqname, jsoseng.toString());
        trainRC(order.getId(), "开始获取链接");
    }

    /**
     *  改签支付mq信息
     * @param paytype 1 支付宝  2网银
     * @throws JMSException 
     */
    public void sendGQPayMQmsg(Trainorder order, int paytype, int paytimes, Trainorderchange orderchange)
            throws JMSException {
        this.url = getActiveMQUrl(0);
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("type", MQMethod.GQPayNumberUPDATE_NAME);
        jsoseng.put("orderid", order.getId());
        jsoseng.put("paymethod", paytype);
        jsoseng.put("paytimes", ++paytimes);
        jsoseng.put("changeorderid", orderchange.getId());
        WriteLog.write("12306_GQ_TrainpayMqMSGUtil_MQ", jsoseng.toString());
        ActiveMQUtil.sendMessage(url, QUEUE_NAME, jsoseng.toString());
    }

    /**
     * 淘宝火车票改签队列
     * @param JSONObject
     */
    public void sendTBChangeOrderMQmsg(JSONObject jsoseng) {
        //        this.url = getActiveMQUrl(0);
        String url = PropertyUtil.getValue("activeMQ_url", "Train.properties");
        try {
            WriteLog.write("TB_changeorder", jsoseng.toString());
            ActiveMQUtil.sendMessage(url, QUEUE_NAME, jsoseng.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过订单ID支付mq信息
     * @param orderid
     * @param paytype
     * @param paytimes
     * @throws JMSException
     * @time 2015年4月29日 下午1:45:53
     * @author fiend
     */
    public void sendPayMQmsgById(long orderid, int paytype, int paytimes) throws JMSException {
        //        this.url = getActiveMQUrl(0);
        String url = PropertyUtil.getValue("activeMQ_url", "Train.properties");
        JSONObject jsoseng = new JSONObject();
        jsoseng.put("type", MQMethod.ORDERPAY);
        jsoseng.put("orderid", orderid);
        jsoseng.put("paymethod", paytype);
        jsoseng.put("paytimes", paytimes++);
        WriteLog.write("12306_TrainpayMqMSGUtil_PAY", jsoseng.toString());
        ActiveMQUtil.sendMessage(url, QUEUE_NAME, jsoseng.toString());
    }

    /**
     * 获取链接日志
     * @param trainorderid
     * @param msg
     */
    public void trainRC(long trainorderid, String msg) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(trainorderid);
        rc.setContent(msg);
        rc.setCreateuser("自动支付");
        rc.setYwtype(1);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQUEUE_NAME() {
        return QUEUE_NAME;
    }

    public void setQUEUE_NAME(String qUEUE_NAME) {
        QUEUE_NAME = qUEUE_NAME;
    }

    public String getCallbackurl() {
        return callbackurl;
    }

    public void setCallbackurl(String callbackurl) {
        this.callbackurl = callbackurl;
    }

}
