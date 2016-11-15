package com.ccservice.b2b2c.atom.pay.handle;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.service.IMemberService;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 保险订单支付成功后需要处理的方法
 * @author cd
 *
 */
public class InsuranceMoneyHandle implements PayHandle {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        //修改订单的状态
        String sql = "UPDATE T_TRADERECORD SET C_STATE=1," + Traderecord.COL_paymothed + "=1,C_MODIFYTIME='"
                + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERCODE='" + ordernumber + "' AND "
                + Traderecord.COL_type + "=7;";
        sql += "UPDATE T_INSURORDER SET C_STATUS=1,C_PAYMETHOD=1,C_PAYSTATUS=1,C_LIUSHUINO='" + tradeno
                + "' where C_ORDERNO='" + ordernumber + "'";
        WriteLog.write("InsuranceMoneyHandle", sql);
        try {
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        //        UPDATE T_TRADERECORD SET C_STATE=1,C_PAYMOTHED=1,C_MODIFYTIME='2014-11-19 18:56:54.531' 
        //                WHERE C_ORDERCODE='I14111918267' AND C_TYPE=7;
        //        UPDATE T_INSURORDER SET C_STATUS=1,C_PAYMETHOD=1,C_PAYSTATUS=1,
        //                C_LIUSHUINO='2014111900001000630040909140' where C_ORDERNO='I14111918267'
        catch (Exception e) {
            logger.error(e);
        }
        sql = " where C_ORDERNO='" + ordernumber + "' ";
        List<Insurorder> list = Server.getInstance().getAirService().findAllInsurorder(sql, "order by id desc", -1, 0);
        WriteLog.write("InsuranceMoneyHandle", list.size() + ":" + sql);
        if (list.size() > 0) {
            Insurorder insurorder = list.get(0);
            try {
                //创建分润信息
                Server.getInstance().getB2BAirticketService().createAirtciektProfitshare(insurorder.getId(), 6);
            }
            catch (Exception e) {
                logger.error(e);
            }
            //如果是系统自动买保险的类型才调用自动购买的接口
            if (insurorder.getInsuranttype() == 28 || insurorder.getInsuranttype() == 29) {
                List<Insuruser> insurusers = insurorder.getInsuruserlist();
                long insurorderid = 0;
                try {
                    int issuccess = 0;
                    for (int i = 0; i < insurusers.size(); i++) {
                        List<Insuruser> insurs = new ArrayList<Insuruser>();
                        Insuruser insuruser = insurusers.get(i);
                        insuruser.setPaystatus(1);
                        //只有这个保险状态为待投保的时候才会去调用投保接口
                        insurs.add(insuruser);
                        if (insurorder.getInsuranttype() == 29) {//机票
                            insurs = Server.getInstance().getAtomService().newOrderAplylist(null, insurs);
                        }
                        else if (insurorder.getInsuranttype() == 28) {//火车票
                            insurs = Server.getInstance().getAtomService().saveTrainOrderAplylist(null, insurs, 2);
                        }
                        insuruser = insurs.get(0);
                        if (insuruser.getInsurstatus() == 1) {
                            issuccess = 1;
                        }

                        Server.getInstance().getAirService().updateInsuruser(insuruser);
                        insurorderid = insuruser.getOrderid();
                    }
                    if (issuccess == 1) {
                        Server.getInstance()
                                .getAirService()
                                .excuteInsuranceTypeBySql(
                                        "update T_INSURORDER set  C_STATUS='2',C_PAYSTATUS='1' where id="
                                                + insurorderid);
                        /**
                         * 为投保完的保单分润
                         */
                        Server.getInstance().getB2BSystemService().shareProfit(insurorderid, 6);
                    }
                }
                catch (Exception e) {
                    logger.error(e);
                }
            }
            //增加新保险
            if (insurorder.getInsuranttype() == 43 || insurorder.getInsuranttype() == 44
                    || insurorder.getInsuranttype() == 45 || insurorder.getInsuranttype() == 46
                    || insurorder.getInsuranttype() == 47 || insurorder.getInsuranttype() == 48
                    || insurorder.getInsuranttype() == 49 || insurorder.getInsuranttype() == 50
                    || insurorder.getInsuranttype() == 51) {
                String url = PropertyUtil.getValue("insurorderUrl", "insurorder.properties");
                WriteLog.write("orderHandleinsurorder", "insurorder:" + JSONArray.toJSONString(insurorder));
                String resultStr = SendPostandGet.submitPost(url, "insurorder=" + JSONArray.toJSONString(insurorder),
                        "UTF-8").toString();
                WriteLog.write("orderHandleinsurorder", "re:" + resultStr);
            }
        }
    }

    public static void main(String[] args) {
        String ordernumber = "I14111918267";
        HessianProxyFactory factory = new HessianProxyFactory();
        String url = "http://:/cn_service/service/";
        String urlAtom = "http://:/cn_interface/service/";
        //        String url = "http://localhost:9001/cn_service/service/";
        try {
            com.ccservice.b2b2c.base.service.IAirService iAirService = (com.ccservice.b2b2c.base.service.IAirService) factory
                    .create(com.ccservice.b2b2c.base.service.IAirService.class, url
                            + com.ccservice.b2b2c.base.service.IAirService.class.getSimpleName());
            com.ccservice.b2b2c.base.service.IB2BAirticketService iB2BAirticketService = (com.ccservice.b2b2c.base.service.IB2BAirticketService) factory
                    .create(com.ccservice.b2b2c.base.service.IB2BAirticketService.class, url
                            + com.ccservice.b2b2c.base.service.IB2BAirticketService.class.getSimpleName());
            com.ccservice.b2b2c.atom.service.IAtomService iAtomService = (com.ccservice.b2b2c.atom.service.IAtomService) factory
                    .create(com.ccservice.b2b2c.atom.service.IAtomService.class, urlAtom
                            + com.ccservice.b2b2c.atom.service.IAtomService.class.getSimpleName());

            IMemberService iMemberService = (IMemberService) factory.create(IMemberService.class, url
                    + IMemberService.class.getSimpleName());
            List<Insurorder> insurorderList = iAirService.findAllInsurorder("where C_ORDERNO='" + ordernumber + "'",
                    "order by id desc", -1, 0);
            System.out.println(insurorderList.size());
            Insurorder insurorder = insurorderList.get(0);
            List<Insuruser> insurusers = insurorder.getInsuruserlist();
            long insurorderid = 0;
            try {
                int issuccess = 0;
                for (int i = 0; i < insurusers.size(); i++) {
                    List<Insuruser> insurs = new ArrayList<Insuruser>();
                    Insuruser insuruser = insurusers.get(i);
                    insuruser.setPaystatus(1);
                    //只有这个保险状态为待投保的时候才会去调用投保接口
                    insurs.add(insuruser);
                    if (insurorder.getInsuranttype() == 29) {//机票
                        insurs = iAtomService.newOrderAplylist(null, insurs);
                    }
                    else if (insurorder.getInsuranttype() == 28) {//火车票
                        insurs = iAtomService.saveTrainOrderAplylist(null, insurs, 2);
                    }
                    insuruser = insurs.get(0);
                    if (insuruser.getInsurstatus() == 1) {
                        issuccess = 1;
                    }

                    iAirService.updateInsuruser(insuruser);
                    insurorderid = insuruser.getOrderid();
                }
                if (issuccess == 1) {
                    iAirService
                            .excuteInsuranceTypeBySql("update T_INSURORDER set  C_STATUS='2',C_PAYSTATUS='1' where id="
                                    + insurorderid);
                    /**
                     * 为投保完的保单分润
                     */
                    Server.getInstance().getB2BSystemService().shareProfit(insurorderid, 6);
                }
            }
            catch (Exception e) {
            }

            //            String sql = "where C_PARENTID=46";
            //            sql = "where ID=47";
            //            List<Customeragent> agents = iMemberService.findAllCustomeragent(sql, "order by id", -1, 0);
            //            for (int i = 0; i < agents.size(); i++) {
            //                int j = i + 1;
            //                System.err.println("insert into T_INSURANCEPRICEINFO values(" + j + ",29," + agents.get(i).getId()
            //                        + ",5.4,'2014-04-23 19:26:15.187','cd',10)");
            //            }

            //            iB2BAirticketService.createAirtciektProfitshare(8, 6);

        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
