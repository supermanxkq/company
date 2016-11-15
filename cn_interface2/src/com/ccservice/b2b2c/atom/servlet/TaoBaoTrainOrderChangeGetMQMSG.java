package com.ccservice.b2b2c.atom.servlet;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import com.ccservice.b2b2c.atom.servlet.listener.TaoBaoTrainOrderChangeGetMQMSGListener;

/**
 * 
 * @ClassName: TaoBaoTrainOrderChangeGetMQMSG 
 * @Description: TODO(从队列里获取每个改签订单信息并去12306核实) 
 * @author wangwei 
 * @date 2015年4月9日 上午10:30:07 
 *
 */
public class TaoBaoTrainOrderChangeGetMQMSG extends HttpServlet {

    private String mqaddress = "";// MQ地址

    private String mqusername = "";// MQ 用户名

    //    private String trainOrderChangeResult = "";// 从MQ中接收到得信息

    private int createordernum = 10;

    @Override
    public void init() throws ServletException {
        super.init();
        String initnum = this.getInitParameter("initnum");
        if (initnum.equals("1")) {
            mqaddress = this.getInitParameter("TB_MQURL");
            // mqaddress = "tcp://192.168.0.5:61616";
            mqusername = this.getInitParameter("TB_MQusername");
            // mqusername = "TB_order_change";
            String temp_createordernum = getInitParameter("createordernum");
            try {
                createordernum = Integer.parseInt(temp_createordernum);
            }
            catch (Exception e) {
            }
            System.out.println("淘宝改签订单处理mq:开启" + mqaddress);
            trainOrderChange();
        }
        else {
            System.out.println("淘宝改签订单处理mq:关闭");
        }

    }

    public void trainOrderChange() {

        ConnectionFactory cf = new ActiveMQConnectionFactory(mqaddress);
        Connection conn = null;
        Session session = null;
        try {
            conn = cf.createConnection();
            for (int i = 0; i < createordernum; i++) {
                session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = new ActiveMQQueue(mqusername);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new TaoBaoTrainOrderChangeGetMQMSGListener());
            }
            conn.start();
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /*   public static void main(String[] args) throws ServletException {
       	TaoBaoTrainOrderChangeGetMQMSG t = new TaoBaoTrainOrderChangeGetMQMSG();
       	t.init();
    }*/
}
