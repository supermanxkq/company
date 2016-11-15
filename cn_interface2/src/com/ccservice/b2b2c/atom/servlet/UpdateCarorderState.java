package com.ccservice.b2b2c.atom.servlet;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.car.EhicarStub;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.carorder.Carorder;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.customerintegralrecord.Customerintegralrecord;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.integral.Integral;
import com.ccservice.b2b2c.base.rebaterecord.Rebaterecord;
import com.ccservice.b2b2c.base.rebaterule.Rebaterule;
import com.ccservice.b2b2c.base.util.PageInfo;

public class UpdateCarorderState implements Job {

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        try {
            PageInfo pageinfo = new PageInfo();
            String where = " WHERE 1=1 AND " + Carorder.COL_state + " <=2  and " + Carorder.COL_waicode
                    + " IS NOT NULL and " + Carorder.COL_orderfee + " >0";

            List<Carorder> listorder = Server.getInstance().getCarService().findAllCarorder(where, "", -1, 0);
            for (int h = 0; h < listorder.size(); h++) {

                Carorder carorder = listorder.get(h);
                EhicarStub stub = new EhicarStub();
                EhicarStub.GetSelfDriveOrder getSelfDriveOrder = new EhicarStub.GetSelfDriveOrder();

                getSelfDriveOrder.setConfNo(carorder.getWaicode());

                EhicarStub.CheckSoapE checksoap = new EhicarStub.CheckSoapE();
                EhicarStub.CheckSoap cs = new EhicarStub.CheckSoap();
                cs.setAccount("108097");
                cs.setPassword("9802E7E8133E2498");
                checksoap.setCheckSoap(cs);

                try {
                    EhicarStub.GetSelfDriveOrderResponse res = stub.getSelfDriveOrder(getSelfDriveOrder, checksoap);

                    String sub = res.getGetSelfDriveOrderResult();

                    //System.out.println(sub);
                    Document document = DocumentHelper.parseText(sub);
                    org.dom4j.Element root = document.getRootElement();
                    List<org.dom4j.Element> listcode = root.elements("returnvalue");
                    if (listcode.get(0).elementText("code") != null
                            && listcode.get(0).elementText("code").equals("ACK")) {//有数据

                        List<org.dom4j.Element> listorderlist = root.elements("orderlist");
                        List<org.dom4j.Element> list_order = listorderlist.get(0).elements("order");

                        String state = list_order.get(0).elementText("status");

                        System.out.println("订单状态==" + state);
                        //4种：预约中（R）,租赁中（L）,已完成（K）,已取消（F）
                        if (state.equals("K")) {
                            //开始
                            Double lirun = 0.00;//初始化一个利润...每返利一个就增加一个...最后平台得利润等于总利润减去这个利润
                            Customeruser cust = Server.getInstance().getMemberService()
                                    .findCustomeruser(carorder.getMemberid());//取创建人ID..相当如当前人
                            Customeragent customeragent = Server.getInstance().getMemberService()
                                    .findCustomeragent(cust.getAgentid());//当前加盟商ID
                            String login_jibie = customeragent.getAgentjibie() + "";//当前加盟商级别

                            Double price = Double.parseDouble(carorder.getOrderfee());//订单总利润
                            System.out.println("总利润==" + price);
                            //取出会员返利
                            Float userrebvaule = 0f;//初始化会员的返利比例
                            Double userfan = 0d;//初始化会员得返利值

                            List<Rebaterule> listuserrebate = Server
                                    .getInstance()
                                    .getMemberService()
                                    .findAllRebaterule(
                                            " where 1=1 and " + Rebaterule.COL_ruletypeid + " =4 and "
                                                    + Rebaterule.COL_agenttypeid + " =5", "", -1, 0);
                            if (listuserrebate.size() > 0) {
                                userrebvaule = listuserrebate.get(0).getRebatvalue(); //会员的返利比例
                                userfan = userrebvaule * Double.parseDouble(carorder.getOrderfee() + "");//会员的返利比例*总利润=会员的返利值
                            }
                            if (cust.getProfits() == null) {
                                cust.setProfits(0.0f);
                            }
                            cust.setProfits((float) (cust.getProfits() + userfan));//增加会员的返利
                            Server.getInstance().getMemberService().updateCustomeruserIgnoreNull(cust);
                            System.out.println("会员利润==" + userfan);
                            lirun += userfan;

                            //会员返利结束,开始添加记录
                            Rebaterecord record = new Rebaterecord();
                            record.setOrdernumber(String.valueOf(listorder.get(h).getId()));
                            record.setRebatemoney(userfan);
                            record.setYewutype(4);
                            record.setRebateagentid(customeragent.getId());
                            record.setRebatetime(getCurrentTime());
                            String memo = "通过" + getyewuleixing(4) + ",会员:" + cust.getLoginname();

                            memo += "得到返佣" + userfan + "元";
                            record.setRebatememo(memo);
                            Server.getInstance().getMemberService().createRebaterecord(record);

                            //添加记录结束

                            if (login_jibie.endsWith("4")) {//如果当前登陆者所属平台会员

                                System.out.println("判断加盟商级别,当前为平台");
                                //									if(customeragent.getRebatemoney()==null){
                                //										customeragent.setRebatemoney(0.00);
                                //									}
                                //customeragent.setRebatemoney(customeragent.getRebatemoney()+Double.parseDouble(price-lirun+""));
                                Server.getInstance().getMemberService().updateCustomeragent(customeragent);

                            }
                            else {
                                System.out.println("判断加盟商级别,当前不是平台");
                                String angenId = customeragent.getParentstr();//得到当前会员所属加盟商的ID串
                                if (angenId.indexOf(",") != -1) {//说明上级不是平台
                                    String[] listangent = angenId.split(",");

                                    for (int a = 0; a < listangent.length; a++) {

                                        if (listangent[a] != null && !listangent[a].toString().equals(" ")) {

                                            Customeragent cus = Server.getInstance().getMemberService()
                                                    .findCustomeragent(Long.parseLong(listangent[a].trim()));

                                            List<Rebaterule> listangentrebate = Server
                                                    .getInstance()
                                                    .getMemberService()
                                                    .findAllRebaterule(
                                                            " where 1=1 and " + Rebaterule.COL_ruletypeid + " =4 and "
                                                                    + Rebaterule.COL_agenttypeid + " ="
                                                                    + cus.getAgentjibie(), "", -1, 0);
                                            System.out.println("判断加盟商级别,当前不是平台,当前是"
                                                    + getAgentTypeName(cus.getAgentjibie()));

                                            if (listangentrebate.size() > 0) {
                                                Float angentRebatvalue = listangentrebate.get(0).getRebatvalue();//循环时候,当前加盟商返利比例
                                                Double angentfan = angentRebatvalue
                                                        * Double.parseDouble(carorder.getOrderfee() + "");//循环时候,当前加盟商返利比例计算出来得返利值

                                                //														if(cus.getRebatemoney()==null){
                                                //															
                                                //															cus.setRebatemoney(0.00);
                                                //														}

                                                //cus.setRebatemoney(cus.getRebatemoney()+angentfan);
                                                Server.getInstance().getMemberService().updateCustomeragent(cus);
                                                //返利结束,开始添加记录
                                                Rebaterecord record_cu = new Rebaterecord();
                                                record_cu.setOrdernumber(String.valueOf(listorder.get(h).getId()));
                                                record_cu.setRebatemoney(angentfan);
                                                record_cu.setYewutype(4);
                                                record_cu.setRebateagentid(cus.getId());
                                                record_cu.setRebatetime(getCurrentTime());
                                                String memo_cu = "通过" + getyewuleixing(4) + ","
                                                        + getAgentTypeName(cus.getAgentjibie());
                                                //if(fromagent!=null){
                                                //	record.setChildagentid(fromagent.getId());
                                                //	memo_cu+=" 从 " + getAgentTypeName(fromagent.getAgentjibie());
                                                //}
                                                memo_cu += "得到返佣" + angentfan + "元";

                                                record_cu.setRebatememo(memo_cu);
                                                Server.getInstance().getMemberService().createRebaterecord(record_cu);

                                                //添加记录结束

                                                System.out.println("当前是" + getAgentTypeName(cus.getAgentjibie())
                                                        + ",利润==" + angentfan);

                                                lirun += angentfan;
                                            }

                                        }

                                    }

                                }

                                //剩下全部返给当前加盟商

                                //									if(customeragent.getRebatemoney()==null){
                                //										customeragent.setRebatemoney(0.00);
                                //									}
                                //customeragent.setRebatemoney(customeragent.getRebatemoney()+Double.parseDouble(price-lirun+""));
                                Server.getInstance().getMemberService().updateCustomeragent(customeragent);
                                //返利结束,开始添加记录
                                Rebaterecord record_cu = new Rebaterecord();
                                record_cu.setOrdernumber(String.valueOf(listorder.get(h).getId()));
                                record_cu.setRebatemoney(Double.parseDouble((price - lirun + "")));
                                record_cu.setYewutype(4);
                                record_cu.setRebateagentid(customeragent.getId());
                                record_cu.setRebatetime(getCurrentTime());
                                String memo_cu = "通过" + getyewuleixing(4) + ","
                                        + getAgentTypeName(customeragent.getAgentjibie());
                                //if(fromagent!=null){
                                //	record.setChildagentid(fromagent.getId());
                                //	memo_cu+=" 从 " + getAgentTypeName(fromagent.getAgentjibie());
                                //}
                                memo_cu += "得到返佣" + Float.parseFloat((price - lirun + "")) + "元";

                                record_cu.setRebatememo(memo_cu);
                                Server.getInstance().getMemberService().createRebaterecord(record_cu);

                                //添加记录结束
                            }

                            //结束

                            carorder.setState(4);

                            //开始添加积分
                            Customeragent customeragent_JF = new Customeragent();
                            Customeruser customeruser_JF = new Customeruser();

                            Customerintegralrecord customerintegralrecord = new Customerintegralrecord();

                            int backorder = 1;//前台或者后台预订得积分体系
                            String zprice = "";//订单总价
                            Float xishu = 1f;//积分系数
                            List<Integral> listIntegral = Server.getInstance().getMemberService()
                                    .findAllIntegral(" where 1=1 and " + Integral.COL_agenttype + " =5", "", -1, 0);//取出用户计别得

                            customeruser_JF = Server.getInstance().getMemberService()
                                    .findCustomeruser(carorder.getMemberid());
                            customeragent_JF = Server.getInstance().getMemberService()
                                    .findCustomeragent(customeruser_JF.getAgentid());
                            List<Integral> list = Server
                                    .getInstance()
                                    .getMemberService()
                                    .findAllIntegral(
                                            " where 1=1 and " + Integral.COL_agenttype + " ="
                                                    + customeragent_JF.getAgentjibie(), "", -1, 0);

                            xishu = list.get(0).getHotelcoeft();

                            zprice = carorder.getPrice();
                            if (carorder.getProperty().equals("1")) {//后台预订

                                backorder = listIntegral.get(0).getBackorderscore();
                            }
                            else {//网站预订

                                backorder = listIntegral.get(0).getWeborderscore();
                            }

                            customerintegralrecord.setRefordernumber(carorder.getCode());

                            customerintegralrecord.setCreatetime(new Timestamp(System.currentTimeMillis()));
                            customerintegralrecord.setRefuid(customeruser_JF.getId());
                            customerintegralrecord.setScore(Integer.parseInt(Float.parseFloat(zprice) * xishu
                                    * backorder + ""));
                            Float pr = Float.parseFloat(zprice) * xishu * backorder;

                            Server.getInstance().getMemberService()
                                    .createCustomerintegralrecord(customerintegralrecord);
                            if (customeruser_JF.getTotalscore() == null) {

                                customeruser_JF.setTotalscore(0);
                            }
                            customeruser_JF.setTotalscore(customeruser_JF.getTotalscore()
                                    + customerintegralrecord.getScore());
                            Server.getInstance().getMemberService().updateCustomeruserIgnoreNull(customeruser_JF);

                            //结束

                            if (state.equals("F")) {//取消
                                carorder.setState(5);

                            }

                            Server.getInstance().getCarService().updateCarorderIgnoreNull(carorder);

                        }

                    }
                    else {
                        System.out.println("失败...原因==" + listcode.get(0).elementText("description"));

                    }

                }
                catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createRebateRecord(long orderid, double rebatmoney, int bustype, Customeragent getrebatagent,
            Customeragent fromagent) {
        try {
            Rebaterecord record = new Rebaterecord();
            record.setOrdernumber(String.valueOf(orderid));
            record.setRebatemoney(rebatmoney);
            record.setYewutype(bustype);
            record.setRebateagentid(getrebatagent.getId());
            record.setRebatetime(getCurrentTime());
            String memo = "通过" + getyewuleixing(bustype) + "," + getAgentTypeName(getrebatagent.getAgentjibie());
            if (fromagent != null) {
                memo += " 从 " + getAgentTypeName(fromagent.getAgentjibie());
            }
            memo += "得到返佣" + rebatmoney + "元";
            record.setRebatememo(memo);
            Server.getInstance().getMemberService().createRebaterecord(record);
        }
        catch (Exception e) {
            System.out.println("订单返佣记录创建失败：");
            e.printStackTrace();
        }

    }

    // 得到业务系统类型定义
    public static String getyewuleixing(long leixingid) {
        String strTypeName = "";
        if (leixingid == 1) {
            strTypeName = "机票业务";
        }
        else if (leixingid == 2) {
            strTypeName = "酒店业务";
        }
        else if (leixingid == 3) {
            strTypeName = "旅游业务";
        }
        else if (leixingid == 4) {
            strTypeName = "租车业务";
        }
        else if (leixingid == 5) {
            strTypeName = "充值业务";
        }
        return strTypeName;
    }

    /**
     * 根据代理商的类型id得到代理商是属于省代理，市代理，分销商
     * 
     * @return
     */
    public static String getAgentTypeName(long id) {
        String strReturn = "";
        if (id == 0) {
            strReturn = "省代理";
        }
        else if (id == 1) {
            strReturn = "市代理";
        }
        else if (id == 2) {
            strReturn = "区域代理";
        }
        else if (id == 3) {
            strReturn = "经纪人";
        }
        else if (id == 4) {
            strReturn = "平台";
        }
        else if (id == 5) {
            strReturn = "会员";
        }

        return strReturn;
    }

    /**
     * @return 获取当前时间
     */
    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());

    }
}