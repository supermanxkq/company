package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Insure;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;

public class TrainInsureMethod {
    /**
     * 保險扣款
     * 
     * @param trainorder
     * @param money
     * @time 2016年7月19日 下午3:06:59
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    public void pay(Trainorder trainorder, double money) {
        //分润
        String sharesql = "[dbo].[sp_rebaterecord_MoneyControl]@orderid = " + trainorder.getId() + ",@ordernumber = N'"
                + trainorder.getOrdernumber() + "',@yewutye = 6,@rebateagentid = " + trainorder.getAgentid()
                + ",@rebatemoney = " + money
                + ",@vmenble = 1,@rebatetype = 2,@rebatememo = N'火车票投保扣款',@customerid = 0,@refordernumber = N'"
                + trainorder.getQunarOrdernumber() + "',@paymethod = 10,@paystate = 1";
        WriteLog.write("火车票投保扣款", trainorder.getId() + "==>" + sharesql);
        List resultlist = Server.getInstance().getSystemService().findMapResultByProcedure(sharesql);
        if (resultlist.size() > 0) {
            Map mapresult = (Map) resultlist.get(0);
            String result = mapresult.get("result").toString();
            WriteLog.write("火车票投保扣款", trainorder.getId() + "==>" + result + "==>" + money);
        }
    }

    /**
     * 保險退款
     * 
     * @param trainorder
     * @param money
     * @time 2016年7月19日 下午3:06:59
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    public void refund(Trainorder trainorder, double money) {
        //分润
        String sharesql = "[dbo].[sp_rebaterecord_MoneyControl]@orderid = " + trainorder.getId() + ",@ordernumber = N'"
                + trainorder.getOrdernumber() + "',@yewutye = 6,@rebateagentid = " + trainorder.getAgentid()
                + ",@rebatemoney = " + money
                + ",@vmenble = 1,@rebatetype = 5,@rebatememo = N'火车票退保退款',@customerid = 0,@refordernumber = N'"
                + trainorder.getQunarOrdernumber() + "',@paymethod = 10,@paystate = 1";
        WriteLog.write("火车票退保退款", trainorder.getId() + "==>" + sharesql);
        List resultlist = Server.getInstance().getSystemService().findMapResultByProcedure(sharesql);
        if (resultlist.size() > 0) {
            Map mapresult = (Map) resultlist.get(0);
            String result = mapresult.get("result").toString();
            WriteLog.write("火车票退保退款", trainorder.getId() + "==>" + result + "==>" + money);
        }
    }

    /**
     * 书写操作记录
     * 
     * @param trainorderid
     * @param content
     * @param createurser
     * @time 2015年1月21日 下午7:05:04
     * @author fiend
     */
    public void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser, int status,
            long ticketid) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(trainorderid);
        rc.setContent(content);
        rc.setStatus(status);// Trainticket.ISSUED
        rc.setCreateuser(createurser);// "12306"
        rc.setTicketid(ticketid);
        rc.setYwtype(yewutype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    @SuppressWarnings("rawtypes")
    public Trainorder findTrainorderByOrderNumberAndInterfaceOrderNumber(String orderNumber, String interfaceOrderNumber) {
        Trainorder trainorder = new Trainorder();
        trainorder.setId(0);
        String sql = "SELECT ID FROM T_TRAINORDER WITH (NOLOCK) WHERE C_ORDERNUMBER='" + orderNumber
                + "' AND C_QUNARORDERNUMBER='" + interfaceOrderNumber + "'";
        try {
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() == 1) {
                Map map = (Map) list.get(0);
                long orderid = Long.valueOf(map.get("ID").toString());
                trainorder = Server.getInstance().getTrainService().findTrainorder(orderid);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return trainorder;
    }
}
