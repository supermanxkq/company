package com.ccservice.b2b2c.atom.pay.helper;

import java.util.List;

import com.ccservice.b2b2c.atom.pay.handle.TrainnofiryHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.mailaddress.MailAddress;
import com.ccservice.b2b2c.base.service.ITrainService;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

/**
 * 接口web支付订单
 * @author wzc
 * @version 创建时间：2015年9月6日 下午2:50:04
 */
public class TrainInterfacepayhelper extends Payhelperframework implements Payhelper {

    public TrainInterfacepayhelper(long orderid) {
        super(orderid);
        this.train = this.getTrainservice().findTrainorder(orderid);
    }

    private ITrainService service;

    private Trainorder train;

    @Override
    public String getOrderDescription() {
        Trainticket ticket = train.getPassengers().get(0).getTraintickets().get(0);
        return "车票信息：" + this.getOrdername() + "(" + ticket.getDeparture() + "-" + ticket.getArrival() + ")";
    }

    @Override
    public String getOrdername() {
        return this.train.getOrdernumber();
    }

    @Override
    public String getOrdernumber() {
        return train.getOrdernumber();
    }

    @Override
    public double getOrderprice() {
        Float summoney = 0.0f;
        List<Trainpassenger> p = train.getPassengers();
        summoney = train.getOrderprice() + train.getCommission();
        for (Trainpassenger trainpassenger : p) {
            List<Trainticket> ticketlist = trainpassenger.getTraintickets();
            for (Trainticket trainticket : ticketlist) {
                float price = trainticket.getInsurorigprice() == null ? 0.0f : trainticket.getInsurorigprice();
                summoney += price;
            }

        }
        String mailsql = "select * from mailaddress with(nolock) where ORDERID=" + train.getId();
        List<MailAddress> maillist = Server.getInstance().getMemberService().findAllMailAddressBySql(mailsql, -1, 0);
        if (maillist.size() > 0) {
            summoney += 20;
        }

        return summoney;
    }

    private ITrainService getTrainservice() {
        if (this.service != null) {
            return this.service;
        }
        else {
            return service = Server.getInstance().getTrainService();
        }
    }

    @Override
    public int getTradetype() {
        return 3;
    }

    @Override
    public String getShwourl() {
        return "";
    }

    @Override
    public String getHandleName() {
        return TrainnofiryHandle.class.getSimpleName();
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }
}
