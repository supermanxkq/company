package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.BusinessmateHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.businessmate.Businessmate;
import com.ccservice.b2b2c.base.mateorder.Mateorder;

public class BusinessmateHelper extends Payhelperframework implements Payhelper {

    private Mateorder mateorder;

    public BusinessmateHelper(long orderid) {
        super(orderid);
        mateorder = Server.getInstance().getBusinessMateService().findMateorder(orderid);
    }

    private String ordername = null;

    public void setOrdername(String ordername) {
        this.ordername = ordername;
    }

    @Override
    public String getHandleName() {
        // TODO Auto-generated method stub
        return BusinessmateHandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        // TODO Auto-generated method stub
        return "商旅手伴订单信息:" + this.getOrdername();
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        if (ordername == null) {

            StringBuilder sb = new StringBuilder();
            Businessmate businessmate = Server.getInstance().getBusinessMateService()
                    .findBusinessmate(mateorder.getMateid());
            sb.append("手伴姓名:" + businessmate.getName() + ",");
            sb.append("手伴订单号:" + mateorder.getId() + ",");
            sb.append("手伴ID:" + mateorder.getMateid() + ",");
            sb.append("支付总金额:" + mateorder.getTotalprice() + "!");
            return ordername = sb.toString();
        }
        else {
            return ordername;
        }
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return mateorder.getOrdernumber();
    }

    @Override
    public double getOrderprice() {
        // TODO Auto-generated method stub
        return (float) mateorder.getTotalprice();
    }

    /**
     * 点击的时候,可以显示产品的详细信息
     */
    @Override
    public String getShwourl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTradetype() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
