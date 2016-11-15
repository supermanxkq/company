package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Insure;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;

public class TrainInsureMoneyMethod {
    /**
     * 保險扣款
     * 
     * @param trainorder
     * @param money
     * @time 2016年7月19日 下午3:06:59
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    private void pay(Trainorder trainorder, double money) {
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
    private void refund(Trainorder trainorder, double money) {
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
}
