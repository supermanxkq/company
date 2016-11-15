package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.qmoneyrecharge.Qmoneyrecharge;

public class QmoneyRechargeHandle implements PayHandle {

    private int step = 0;

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        Qmoneyrecharge recharge = (Qmoneyrecharge) Server.getInstance().getMemberService()
                .findAllQmoneyrecharge("WHERE " + Qmoneyrecharge.COL_ordernumber + "='" + ordernumber + "'", "", -1, 0)
                .get(0);
        recharge.setPaymethod(1);//网上支付
        recharge.setPaystate(1);//支付成功
        recharge.setTradeno(tradeno);//交易号
        String sql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='" + new Timestamp(System.currentTimeMillis())
                + "' WHERE C_ORDERCODE='" + ordernumber + "'";
        Server.getInstance().getSystemService().findMapResultBySql(sql, null);

        if (recharge.getRechstate() != 0 && recharge.getRechstate() != 1) {
            String message = Server
                    .getInstance()
                    .getAtomService()
                    .qmoneyRecharge(recharge.getOrdernumber(), recharge.getCardid(), recharge.getBuynum(),
                            recharge.getQqnumber());

            try {
                int state = Integer.valueOf(message);
                recharge.setRechstate(state);
            }
            catch (Exception e) {
                recharge.setRechstate(9);
            }
            if (recharge.getRechstate() == 9) {
                Timer timer = new Timer();
                TimerTask task = new QmoneyTask(recharge);
                timer.schedule(task, 60000l, 300000l);
            }
            Server.getInstance().getMemberService().updateQmoneyrechargeIgnoreNull(recharge);
        }
    }

    //	public boolean  timerhandle(Object obj){
    //		Qmoneyrecharge recharge =(Qmoneyrecharge)obj;
    //		String message="";
    //		if(recharge.getRechstate()==9){
    //			String ordernumber=recharge.getRefordernumber()==null?recharge.getOrdernumber()+"Q":recharge.getRefordernumber()+"Q";
    //			recharge.setRefordernumber(ordernumber);
    //			System.out.println(ordernumber);
    //		 message =Server.getInstance().getAtomService().qmoneyRecharge(
    //				 ordernumber, recharge.getCardid(),
    //				recharge.getBuynum(), recharge.getQqnumber());
    //		}
    //		try {
    //			int state = Integer.valueOf(message);
    //			recharge.setRechstate(state);
    //		} catch (Exception e) {				
    //			recharge.setRechstate(9);
    //		}
    //	   if(recharge.getRechstate()==0||recharge.getRechstate()==1){
    //		   try{
    //		   Server.getInstance().getMemberService().updateQmoneyrechargeIgnoreNull(recharge);
    //		   }catch(Exception e){
    //			   return true;
    //		   }
    //		   return true;
    //	   }
    //	   return false;
    //	}

    class QmoneyTask extends TimerTask {
        public QmoneyTask(Qmoneyrecharge recharge) {
            this.recharge = recharge;
        }

        Qmoneyrecharge recharge;

        @Override
        public void run() {
            step++;
            String message = Server
                    .getInstance()
                    .getAtomService()
                    .qmoneyRecharge(recharge.getOrdernumber(), recharge.getCardid(), recharge.getBuynum(),
                            recharge.getQqnumber());
            try {
                int state = Integer.valueOf(message);
                recharge.setRechstate(state);
            }
            catch (Exception e) {
                recharge.setRechstate(9);
            }
            if (recharge.getRechstate() == 1 || recharge.getRechstate() == 9 || step >= 5) {
                Server.getInstance().getMemberService().updateQmoneyrechargeIgnoreNull(recharge);
                this.cancel();
            }

        }

    }

}
