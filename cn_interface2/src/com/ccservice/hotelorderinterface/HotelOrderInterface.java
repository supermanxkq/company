package com.ccservice.hotelorderinterface;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.creditcard.Creditcard;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.dnsmaintenance.Dnsmaintenance;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.horderdayprice.HOrderDayPrice;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelinvoice.HotelInvoice;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorderrc.Hotelorderrc;
import com.ccservice.b2b2c.base.hotelsupplier.HotelSupplier;
import com.ccservice.b2b2c.base.rebaterecord.Rebaterecord;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.component.sms.SMSTemplet;
import com.ccservice.component.sms.SMSType;

/**
 * 
 * @author wzc 酒店订单-酒店接口
 */

public class HotelOrderInterface implements IHotelOrderInterface {

    /*补开发票同步Start*/
    @SuppressWarnings("unchecked")
    public void createHotelInvoice(Hotelorder hotelorder, HotelInvoice hotelInvoice, String username) {
        try {
            List<Customeruser> users = Server.getInstance().getMemberService()
                    .findAllCustomeruser("where C_LOGINNAME='" + username.trim() + "'", "", -1, 0);
            if (users != null && users.size() == 1) {
                Customeruser user = users.get(0);
                String ordercode = hotelorder.getYeeordernum();
                String where = "where c_orderid = '" + ordercode + "'";
                List<Hotelorder> orders = Server.getInstance().getHotelService().findAllHotelorder(where, "", -1, 0);
                if (orders != null && orders.size() == 1) {
                    Hotelorder ydxorder = orders.get(0);
                    hotelInvoice.setOrderid(ydxorder.getId());
                    HotelInvoice invoice = Server.getInstance().getHotelService().createHotelInvoice(hotelInvoice);
                    //Update Order
                    ydxorder.setInvoiceid(invoice.getId());
                    ydxorder.setHasinvoice("开具发票");
                    Server.getInstance().getHotelService().updateHotelorderIgnoreNull(ydxorder);
                    //日志
                    Hotelorderrc rc = new Hotelorderrc();
                    rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    rc.setContent("补开发票");
                    rc.setHandleuser(user.getId() + "");
                    rc.setOrderid(ydxorder.getOrderid());
                    rc = Server.getInstance().getHotelService().createHotelorderrc(rc);
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("订单同步", e.fillInStackTrace() + "");
        }
    }

    @SuppressWarnings("unchecked")
    public void updateHotelInvoicePay(Hotelorder hotelorder, HotelInvoice invoice, Integer ydxUpdateCustomer) {
        try {
            if (invoice != null && invoice.getPaystate() != null && invoice.getPaystate().intValue() == 1
                    && ydxUpdateCustomer != null
                    && (ydxUpdateCustomer.intValue() == 0 || ydxUpdateCustomer.intValue() == 1)) {
                String where = "where ";
                //易订行向客户同步
                if (ydxUpdateCustomer == 1) {
                    where = "C_YEEORDERNUM = '" + hotelorder.getOrderid() + "'";
                }
                //客户向易订行同步
                else {
                    where += "C_ORDERID = '" + hotelorder.getYeeordernum() + "'";
                }
                List<Hotelorder> orders = Server.getInstance().getHotelService().findAllHotelorder(where, "", -1, 0);
                if (orders != null && orders.size() == 1) {
                    Hotelorder order = orders.get(0);
                    Long invoiceid = order.getInvoiceid();
                    if (invoiceid != null && invoiceid.longValue() > 0) {
                        HotelInvoice hotelInvoice = Server.getInstance().getHotelService().findHotelInvoice(invoiceid);
                        if (hotelInvoice != null) {
                            //税费
                            double moneytax = 0d;
                            //快递费
                            double postprice = 0d;
                            if (hotelInvoice.getAddmoneyflag() != null
                                    && hotelInvoice.getAddmoneyflag().intValue() == 1) {
                                moneytax = new BigDecimal(String.valueOf(hotelInvoice.getMoneytax())).doubleValue();
                            }
                            if (hotelInvoice.getPosttype() != null && hotelInvoice.getPosttype().longValue() == 1) {
                                postprice = hotelInvoice.getPostprice().doubleValue();
                            }
                            if (moneytax > 0 || postprice > 0) {
                                if (order.getCustomerconfig() != null && order.getCustomerconfig().doubleValue() == 2) {
                                    hotelInvoice.setPayoffer(invoice.getPayoffer());
                                }
                                else {
                                    hotelInvoice.setPaystate(invoice.getPaystate());
                                    hotelInvoice.setPaymethod(invoice.getPaymethod());
                                    hotelInvoice.setTradenum(invoice.getTradenum());
                                }
                                Server.getInstance().getHotelService().updateHotelInvoiceIgnoreNull(hotelInvoice);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("订单同步", e.fillInStackTrace() + "");
        }
    }

    public void syncHotelInvoicePay(Hotelorder hotelorder, HotelInvoice hotelInvoice, Integer ydxUpdateCustomer) {
        if (ydxUpdateCustomer != null && ydxUpdateCustomer == 1) {
            IHotelOrderInterface orderinterface = getFromCustomer(hotelorder);
            orderinterface.updateHotelInvoicePay(hotelorder, hotelInvoice, ydxUpdateCustomer);
        }
    }

    /*补开发票同步End*/

    public String createHotelOrder(Hotelorder hotelorder, List<Guest> guests, List<HOrderDayPrice> orderdayprices,
            String username) {
        return this.createHotelOrder(hotelorder, guests, orderdayprices, username, null, null);
    }

    @SuppressWarnings("unchecked")
    public String createHotelOrder(Hotelorder hotelorder, List<Guest> guests, List<HOrderDayPrice> orderdayprices,
            String username, Creditcard creditcard, HotelInvoice hotelInvoice) {
        String result = "";
        try {
            List<Customeruser> users = Server.getInstance().getMemberService()
                    .findAllCustomeruser("where C_LOGINNAME='" + username.trim() + "'", "", -1, 0);
            if (users.size() == 1) {
                //接口平台订单号
                hotelorder.setYeeordernum(hotelorder.getOrderid());
                //用户
                Customeruser user = users.get(0);
                hotelorder.setCreateuserid(user.getAgentid());
                hotelorder.setMemberid(user.getId());
                if (hotelorder.getCustomerconfig() == 2 && hotelorder.getPaytype() == 2) {
                    hotelorder.setPrice(hotelorder.getPaygongying().toString());
                }
                //供应价格
                Double baseprice = 0.0d;
                for (int i = 0; hotelorder.getPaytype() == 2 && i < orderdayprices.size(); i++) {
                    HOrderDayPrice currentprice = orderdayprices.get(i);
                    baseprice = ElongHotelInterfaceUtil.add(baseprice, currentprice.getBaseprice());
                }
                hotelorder.setPaygongying(ElongHotelInterfaceUtil.multiply(baseprice, hotelorder.getPrerooms()));

                //通过来源查询供应属性
                int commissiontype = 0;//佣金类型
                double commission = 0d;//佣金
                int handlingchargetype = 0;//手续费类型
                double handlingcharge = 0d;//手续费
                try {
                    Long agentid = hotelorder.getOrdersource();
                    HotelSupplier hotelSupplier = Server.getInstance().getHotelService()
                            .findHotelSupplierByCustomeragentID(agentid);
                    if (hotelSupplier != null) {
                        if (hotelSupplier.getCommissiontype() != null) {
                            commissiontype = hotelSupplier.getCommissiontype().intValue();
                        }
                        if (hotelSupplier.getCommission() != null) {
                            commission = hotelSupplier.getCommission().doubleValue();
                        }
                        if (hotelSupplier.getHandlingchargetype() != null) {
                            handlingchargetype = hotelSupplier.getHandlingchargetype().intValue();
                        }
                        if (hotelSupplier.getHandlingcharge() != null) {
                            handlingcharge = hotelSupplier.getHandlingcharge().doubleValue();
                        }
                        double suppliercommsum = 0;
                        if (commissiontype > 0 && commission > 0) {
                            if (commissiontype == 1) {
                                hotelorder.setSuppliercommtype(commissiontype);
                                commission = ElongHotelInterfaceUtil.multiply(commission, 0.01);
                                suppliercommsum = ElongHotelInterfaceUtil.multiply(commission,
                                        hotelorder.getPaygongying());
                            }
                            else if (commissiontype == 2) {
                                hotelorder.setSuppliercommtype(commissiontype);
                                suppliercommsum = commission;
                            }
                            hotelorder.setSuppliercomm(suppliercommsum);
                        }
                        double supplierhandsum = 0;
                        if (handlingchargetype > 0 && handlingcharge > 0) {
                            if (handlingchargetype == 1) {
                                hotelorder.setSupplierhandtype(handlingchargetype);
                                handlingcharge = ElongHotelInterfaceUtil.multiply(handlingcharge, 0.01);
                                supplierhandsum = ElongHotelInterfaceUtil.multiply(handlingcharge,
                                        hotelorder.getPaygongying());
                            }
                            else if (handlingchargetype == 2) {
                                hotelorder.setSupplierhandtype(handlingchargetype);
                                supplierhandsum = handlingcharge;
                            }
                            hotelorder.setSupplierhand(supplierhandsum);
                        }
                    }
                }
                catch (Exception e) {
                    WriteLog.write("订单同步", e.fillInStackTrace() + "");
                }

                //发票ID
                long invoiceid = hotelorder.getInvoiceid() == null ? 0 : hotelorder.getInvoiceid().longValue();
                hotelorder.setInvoiceid(null);//发票置空
                Hotelorder order = Server.getInstance().getHotelService().createHotelorder(hotelorder);
                //日志
                try {
                    Hotelorderrc rc = new Hotelorderrc();
                    rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    rc.setContent("创建订单");
                    rc.setHandleuser(user.getId() + "");
                    rc.setOrderid(order.getOrderid());
                    rc = Server.getInstance().getHotelService().createHotelorderrc(rc);
                }
                catch (Exception e) {
                    WriteLog.write("订单同步", e.fillInStackTrace() + "");
                }
                //订单号
                String ordernum = order.getOrderid();
                //客人信息
                try {
                    for (int i = 0; i < guests.size(); i++) {
                        guests.get(i).setOrderid(order.getId());
                        Server.getInstance().getHotelService().createGuest(guests.get(i));
                    }
                }
                catch (Exception e) {
                    WriteLog.write("订单同步", e.fillInStackTrace() + "");
                }
                //每日价格
                try {
                    for (int i = 0; i < orderdayprices.size(); i++) {
                        HOrderDayPrice currentprice = orderdayprices.get(i);
                        currentprice.setOrderid(ordernum);
                        //返点
                        String backpointinfo = currentprice.getBackpointinfo();
                        String[] info = backpointinfo.split("@");
                        String addstr = "";
                        if (info.length > 1) {
                            String indexstr = info[0];
                            Double zongfan = currentprice.getSumfandian();
                            Double lastfan = Double.valueOf(indexstr.split(",")[3])
                                    + Double.valueOf(indexstr.split(",")[2]);
                            if (zongfan.doubleValue() != lastfan.doubleValue()) {
                                Double liudian = ElongHotelInterfaceUtil.subtract(zongfan, lastfan);
                                addstr = "46," + user.getAgentid() + "," + liudian + "," + lastfan + "@"
                                        + user.getAgentid() + "," + lastfan + "@";
                                currentprice.setBackpointinfo(addstr);
                            }
                        }
                        else if (info.length == 1) {
                            String indexstr = info[0];
                            Double zongfan = currentprice.getSumfandian();
                            Double lastfan = Double.valueOf(indexstr.split(",")[1]);
                            if (zongfan.doubleValue() != lastfan.doubleValue()) {
                                Double liudian = ElongHotelInterfaceUtil.subtract(zongfan, lastfan);
                                addstr = "46," + user.getAgentid() + "," + liudian + "," + lastfan + "@"
                                        + user.getAgentid() + "," + lastfan + "@";
                                currentprice.setBackpointinfo(addstr);
                            }
                        }
                        Server.getInstance().getHotelService().createHOrderDayPrice(currentprice);
                    }
                }
                catch (Exception e) {
                    WriteLog.write("订单同步", e.fillInStackTrace() + "");
                }
                result = "1," + ordernum;
                WriteLog.write("订单同步", username + "成功,单号" + ordernum);
                //信用卡
                try {
                    if (creditcard != null && hotelorder.getDanbao() != null
                            && (hotelorder.getDanbao().intValue() == 1 || hotelorder.getDanbao().intValue() == 2)) {//1为首晚担保 2 全额担保
                        creditcard.setHotelOrderId(order.getOrderid());
                        Server.getInstance().getMemberService().createCreditcard(creditcard);
                    }
                }
                catch (Exception e) {
                    WriteLog.write("订单同步", e.fillInStackTrace() + "");
                }
                //发票
                try {
                    if (invoiceid > 0 && hotelInvoice != null
                            && !ElongHotelInterfaceUtil.StringIsNull(hotelInvoice.getType())) {
                        HotelInvoice invoice = Server.getInstance().getHotelService().createHotelInvoice(hotelInvoice);
                        //Update Order
                        order.setInvoiceid(invoice.getId());
                        Server.getInstance().getHotelService().updateHotelorderIgnoreNull(order);
                    }
                }
                catch (Exception e) {
                    WriteLog.write("订单同步", e.fillInStackTrace() + "");
                }
            }
            else {
                result = "0,用户名为空或者不存在" + username;
                WriteLog.write("订单同步", result);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("订单同步", e.fillInStackTrace() + "");
        }
        return result;
    }

    /**
     * 同步订单
     */
    @Override
    public String synchotelorder(Hotelorder hotelorder, Hotel hotel, SMSType type, Long systype) {
        IHotelOrderInterface orderinterface = getFromCustomer(hotelorder);
        if (orderinterface != null
                && orderinterface.synchupdatehotelorderingnull(hotelorder, hotel, type, systype) == 1) {
            hotelorder.setState(8);
            Server.getInstance().getHotelService().updateHotelorderIgnoreNull(hotelorder);
        }
        return null;
    }

    /**
     * 获取客户服务器service
     * @param hotelorder
     * @return
     */
    public IHotelOrderInterface getFromCustomer(Hotelorder hotelorder) {
        Customeragent agent = Server.getInstance().getMemberService().findCustomeragent(hotelorder.getCreateuserid());
        if (agent != null) {
            String notifyurl = agent.getAgentother();
            String urlend = "";
            URL url;
            try {
                url = new URL(notifyurl);
                String port = url.getPort() == -1 ? "" : ":" + url.getPort();
                String beforeurl = url.getProtocol() + "://" + url.getHost() + port;
                urlend = beforeurl + "/cn_interface/service/";
            }
            catch (MalformedURLException e1) {
                WriteLog.write("订单同步", e1.fillInStackTrace() + "");
                e1.printStackTrace();
            }
            HessianProxyFactory factory = new HessianProxyFactory();
            try {
                IHotelOrderInterface orderinterface = (IHotelOrderInterface) factory.create(IHotelOrderInterface.class,
                        urlend + IHotelOrderInterface.class.getSimpleName());
                return orderinterface;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                WriteLog.write("订单同步", e.fillInStackTrace() + "");
            }
        }
        return null;
    }

    /**
     * 更新订单操作
     */
    @Override
    public int synchupdatehotelorderingnull(Hotelorder hotelorder, Hotel hotel, SMSType type, Long systype) {
        String where = "where 1=1 ";
        if (systype == 1) {
            where += " and c_orderid='" + hotelorder.getYeeordernum() + "'";
        }
        else if (systype == 2) {
            where += " and C_YEEORDERNUM='" + hotelorder.getOrderid() + "'";
        }
        List<Hotelorder> orders = Server.getInstance().getHotelService().findAllHotelorder(where, "", -1, 0);
        int flag = 0;
        if (orders.size() > 0) {
            Hotelorder sendinfo = orders.get(0);
            boolean flagflag = false;
            if (sendinfo.getPayoffer() == null || sendinfo.getPayoffer() != 3) {
                flagflag = true;
            }
            if (flagflag) {
                Hotelorder temp = orders.get(0);
                Customeragent agent = Server.getInstance().getMemberService().findCustomeragent(temp.getCreateuserid());
                //订单
                hotelorder.setCreateuserid(null);
                hotelorder.setYeeordernum(null);
                hotelorder.setOrderid(null);
                hotelorder.setPretime(null);
                hotelorder.setMemberid(null);
                hotelorder.setLockstatus(null);
                hotelorder.setControlname(null);
                hotelorder.setPrice(null);
                hotelorder.setPaygongying(null);
                if (temp.getCustomerconfig() == 2) {
                    hotelorder.setPayment(null);
                    hotelorder.setPaystate(null);
                    hotelorder.setTradenum(null);
                    hotelorder.setInvoiceid(null);
                }
                hotelorder.setId(temp.getId());
                temp = hotelorder;
                Server.getInstance().getHotelService().updateHotelorderIgnoreNull(temp);
                temp = Server.getInstance().getHotelService().findHotelorder(temp.getId());
                //发票
                Long invoiceid = temp.getInvoiceid();
                if (temp.getPaystate().longValue() == 1 && invoiceid != null && invoiceid.longValue() > 0
                        && temp.getCustomerconfig() != 2) {
                    HotelInvoice hotelInvoice = Server.getInstance().getHotelService().findHotelInvoice(invoiceid);
                    if (hotelInvoice != null) {
                        //税费
                        double moneytax = 0d;
                        //快递费
                        double postprice = 0d;
                        if (hotelInvoice.getAddmoneyflag() != null && hotelInvoice.getAddmoneyflag().intValue() == 1) {
                            moneytax = new BigDecimal(String.valueOf(hotelInvoice.getMoneytax())).doubleValue();
                        }
                        if (hotelInvoice.getPosttype() != null && hotelInvoice.getPosttype().longValue() == 1) {
                            postprice = hotelInvoice.getPostprice().doubleValue();
                        }
                        if (moneytax > 0 || postprice > 0) {
                            hotelInvoice.setPaystate(1);//已支付
                            hotelInvoice.setPaymethod(temp.getPayment());
                            hotelInvoice.setTradenum(temp.getTradenum());
                            Server.getInstance().getHotelService().updateHotelInvoiceIgnoreNull(hotelInvoice);
                        }
                    }
                }
                if (type != null) {
                    String agentstr = agent.getId() + "," + agent.getParentstr();
                    List<Dnsmaintenance> dnses = Server
                            .getInstance()
                            .getSystemService()
                            .findAllDnsmaintenance("where c_agentid in (" + agentstr + ")", "order by c_agentid desc",
                                    -1, 0);
                    if (dnses != null && dnses.size() > 0) {
                        Dnsmaintenance dns = dnses.get(0);
                        if (type != null) {
                            sendConfimSms(hotel, sendinfo, type, dns);
                        }
                    }
                }
                if (flag > 0) {
                    System.out.println("更新成功……");
                }
            }
            else {
                flag = 1;
            }
        }
        else {
            //System.out.println("未找到订单……");
        }
        return flag;
    }

    private SimpleDateFormat simplefromatyymmdd = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat simplefromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String formatStringTimetoyyyymmdd(String date) {
        try {
            return this.simplefromatyymmdd.format((simplefromat.parse(date)));
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * 发送酒店订单短信
     */
    public void sendConfimSms(Hotel hotel, Hotelorder hotelorder, SMSType type, Dnsmaintenance dns) {
        try {
            // 发送短信
            String smstemple = "";
            smstemple = new SMSTemplet().getSMSTemplett(type, dns);
            if (smstemple != null) {
                smstemple = smstemple.replaceAll(" ", "");
                System.out.println(smstemple);
            }
            // 您好，您预订的[入住时间]入住的，[酒店名称],[房型名称],订单号[订单号]已确认，酒店地址：[酒店地址]电话：[电话]。如行程有变，请及时与易订行客服联系，客服电话:010-57793325
            smstemple = smstemple.replace("[用户名]", hotelorder.getLinkname());
            smstemple = smstemple.replace("[入住时间]", formatStringTimetoyyyymmdd(hotelorder.getComedate().toString()));
            smstemple = smstemple.replace("[酒店名称]", hotelorder.getName());
            smstemple = smstemple.replace("[房型名称]", hotelorder.getRoomtypename());
            smstemple = smstemple.replace("[夜间数]", hotelorder.getPrerooms() + "间" + hotelorder.getManyday() + "夜");
            smstemple = smstemple.replace("[订单号]", hotelorder.getOrderid());
            smstemple = smstemple.replace("[酒店地址]", hotel.getAddress());
            if (hotel.getSourcetype() == 1) {
                String tall = hotel.getMarkettell();
                if (tall != null) {
                    if (tall.contains("艺龙")) {
                        tall = tall.replaceAll("艺龙", "");
                    }
                }
                smstemple = smstemple.replace("[酒店电话]", tall);
            }
            else {
                smstemple = smstemple.replace("[酒店电话]", hotel.getTortell());
            }
            Server.getInstance()
                    .getAtomService()
                    .sendSms(new String[] { "" + hotelorder.getLinkmobile() + "" }, smstemple, hotelorder.getId(),
                            hotelorder.getCreateuserid(), dns, 2);
            System.out.println(smstemple);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            WriteLog.write("订单同步", "发送短信失败:" + ex.getMessage());
        }
    }

    public float addAgentvmoney(long agentid, double money) {
        String sql = "UPDATE T_CUSTOMERAGENT SET C_VMONEY=C_VMONEY+" + money + " WHERE ID=" + agentid + ";"
                + "SELECT C_VMONEY FROM T_CUSTOMERAGENT WHERE ID=" + agentid;
        Map map = (Map) Server.getInstance().getSystemService().findMapResultBySql(sql, null).get(0);

        return Float.valueOf(map.get("C_VMONEY").toString());

    }

    public float getTotalVmoney(long id) {
        String sql = "SELECT C_VMONEY AS VMONEY FROM T_CUSTOMERAGENT WHERE ID=" + id;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map m = (Map) list.get(0);
            float vmoney = Float.valueOf(m.get("VMONEY").toString());
            return vmoney;
        }

        return 0;
    }

    /**
     * 酒店订单虚拟账户消费记录
     */
    public void SysVmrecord(Hotelorder hotelorder, double rebatmoney, int xiaofeileixing, boolean vmenable) {
        IHotelOrderInterface orderinterface = getFromCustomer(hotelorder);
        orderinterface.createHotelvmrecord(hotelorder, rebatmoney, xiaofeileixing, vmenable);
        orderinterface.synchupdatehotelorderingnull(hotelorder, null, null, 2l);

    }

    /**
     * 酒店订单虚拟账户消费记录
     */
    public void createHotelvmrecord(Hotelorder hotelorder, double rebatmoney, int xiaofeileixing, boolean vmenable) {
        if (rebatmoney != 0) {
            Rebaterecord record = new Rebaterecord();
            record.setRebatemoney(rebatmoney);
            record.setCustomerid(0);
            record.setRebatetype(xiaofeileixing);//平台消费
            record.setYewutype(2);//
            record.setRebate(0d);
            if (vmenable) {
                record.setVmenable(1);
            }
            else {
                record.setVmenable(0);
            }
            List<Hotelorder> orders = Server.getInstance().getHotelService()
                    .findAllHotelorder(" where C_YEEORDERNUM='" + hotelorder.getOrderid() + "'", "", -1, 0);
            long vmagentid = 0;
            if (orders.size() > 0) {
                record.setOrderid(orders.get(0).getId());
                record.setOrdernumber(orders.get(0).getOrderid());
                vmagentid = orders.get(0).getCreateuserid();
            }
            record.setRebateagentid(vmagentid);
            record.setRebatetime(new Timestamp(System.currentTimeMillis()));
            String memo = "通过" + record.getYewutypestr() + (rebatmoney > 0 ? "获得" : "扣除") + rebatmoney + "元";
            if (vmenable) {// 如果返金计入虚拟账户
                float vmoney = getTotalVmoney(vmagentid);
                memo += ".当前账户余额：" + (vmoney + rebatmoney);
                addAgentvmoney(vmagentid, rebatmoney);
            }
            record.setRebatememo(memo);
            try {
                Server.getInstance().getMemberService().createRebaterecord(record);
            }
            catch (SQLException e) {
                WriteLog.write("订单同步", e.fillInStackTrace() + "");
            }
        }

    }

    /**
     * 酒店订单分润
     */
    public void sharingRebate(Hotelorder order, Long type) throws Exception {
        //4 虚拟账户 1 网上支付
        if (order.getPaytype() != null && order.getPaytype().longValue() == 2 && order.getPaystate() != null
                && order.getPaystate() == 1) {
            if (Integer.parseInt(order.getOrdertype()) == 1) {//接口订单
                String HotelSharingRebateType = getSystemConfig("HotelSharingRebateType");
                if ("1".equals(HotelSharingRebateType)) {// 分润到虚拟账户
                    Server.getInstance().getB2BSystemService().shareProfit(order.getId(), 2, true);
                }
                else if ("3".equals(HotelSharingRebateType)) {// 按照支付方式分润
                    Server.getInstance().getB2BSystemService().shareProfit(order.getId(), 2, false);
                }
            }

        }
    }

    /**
     * 接口订单分润接口实现
     * @throws Exception 
     */
    @Override
    public String HotelsharingRebate(Hotelorder hotelorder, Long type) throws Exception {
        IHotelOrderInterface ite = getFromCustomer(hotelorder);
        if (ite != null) {
            Hotelorder order = ite.findCustomerOrder(hotelorder);
            if (order != null) {
                ite.sharingRebate(order, type);
            }
            else {
                //System.out.println("未找到订单");
            }
        }
        return null;
    }

    /**
     * 获取系统配置属性
     */
    public String getSystemConfig(String name) {
        List<Sysconfig> configs = Server.getInstance().getSystemService()
                .findAllSysconfig("where c_name='" + name + "'", "", -1, 0);
        if (configs != null && configs.size() == 1) {
            Sysconfig config = configs.get(0);
            return config.getValue();
        }
        return "";
    }

    /**
     * 酒店订单账户分润记录
     */
    /**
     * @param hotelorder
     *            酒店订单 包含id和订单号两个基本参数
     * @param vmagentid
     *            获得分润的代理ID
     * @param rebate
     *            返点
     * @param rebatmoney
     *            返金
     * @param vmenable
     *            返金是否计入虚拟账户。如若已分润到网银账户，则返金不计入虚拟账户
     */
    public void createHotelRebaterecord(Hotelorder hotelorder, long vmagentid, double rebate, double rebatmoney,
            boolean vmenable) {
        if (rebatmoney != 0) {
            Rebaterecord record = new Rebaterecord();
            record.setRebatemoney(rebatmoney);
            record.setCustomerid(-1);
            record.setRebatetype(Rebaterecord.FANLI);// 订单分润
            record.setOrdernumber(hotelorder.getOrderid());
            record.setOrderid(hotelorder.getId());
            record.setYewutype(2);//
            record.setRebate(rebate);
            if (vmenable) {
                record.setVmenable(1);
            }
            else {
                record.setVmenable(0);
            }
            record.setRebateagentid(vmagentid);
            record.setRebatetime(new Timestamp(System.currentTimeMillis()));
            String memo = "订单分润" + ",返金：" + rebatmoney;
            if (vmenable) {// 如果返金计入虚拟账户
                float vmoney = getTotalVmoney(vmagentid);
                memo += ".当前账户余额：" + (vmoney + rebatmoney);
                addAgentvmoney(vmagentid, rebatmoney);
            }
            record.setRebatememo(memo);
            try {
                Server.getInstance().getMemberService().createRebaterecord(record);
            }
            catch (SQLException e) {
                WriteLog.write("订单同步", e.fillInStackTrace() + "");
            }
        }
    }

    /**
     * 获取订单各级加盟商分润信息
     */
    public Map<Long, Double> getHotelAgentlevelrebate(Hotelorder order, Long type) {
        Map<Long, Double> map = new HashMap<Long, Double>();
        if (order != null) {
            int roomcount = order.getPrerooms();
            double customerconfig = order.getCustomerconfig();
            List<HOrderDayPrice> prices = Server.getInstance().getHotelService()
                    .findAllHOrderDayPrice("where c_orderid='" + order.getOrderid() + "'", "order by id asc ", -1, 0);
            if (customerconfig == 3) {
                Double sum = 0.0d;
                for (int i = 0; i < prices.size(); i++) {
                    HOrderDayPrice price = prices.get(i);
                    String liudianinfo = price.getBackpointinfo();
                    String[] info = liudianinfo.split("@");
                    sum += (price.getDayPrice() - price.getBaseprice());
                    if (order.getPaytype() == 2) {// 预付
                        for (int j = 0; j < info.length - 1; j++) {
                            Long agenttemp = Long.valueOf(info[j].split(",")[0]);// 遍历当前的加盟商
                            if (agenttemp == 46 || agenttemp == 1) {
                                continue;
                            }
                            if (price.getHotelmode() != null && price.getHotelmode() == 2) {
                                Double profitemp = Double.parseDouble(info[j].split(",")[2]);
                                sum -= profitemp;
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                            else {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[2]) / 100);
                                sum -= profitemp;
                                mapaddcontrol(map, agenttemp, profitemp);

                            }
                        }
                    }
                    else {
                        for (int j = 0; j < info.length; j++) {
                            Long agenttemp = Long.valueOf(info[j].split(",")[0]);// 遍历当前的加盟商
                            if (agenttemp == 46 || agenttemp == 1) {
                                continue;
                            }
                            if (j == info.length - 1) {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[1]) / 100);
                                sum -= profitemp;
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                            else {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[2]) / 100);
                                sum -= profitemp;
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                        }
                    }
                }
                mapaddcontrol(map, 46l, sum);
            }
            else if (customerconfig == 2) {// 订单同步，不支付到易订行
                for (int i = 0; i < prices.size(); i++) {
                    HOrderDayPrice price = prices.get(i);
                    String liudianinfo = price.getBackpointinfo();
                    String[] info = liudianinfo.split("@");
                    if (order.getPaytype() == 2) {// 预付
                        double lirun = 0.0d;
                        for (int j = 0; j < info.length - 1; j++) {
                            Long agenttemp = Long.valueOf(info[j].split(",")[0]);// 遍历当前的加盟商
                            if (agenttemp == 1) {
                                continue;
                            }
                            if (price.getHotelmode() != null && price.getHotelmode() == 2) {
                                Double profitemp = Double.parseDouble(info[j].split(",")[2]);
                                mapaddcontrol(map, agenttemp, profitemp);
                                lirun += profitemp;
                            }
                            else {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[2]) / 100);
                                mapaddcontrol(map, agenttemp, profitemp);
                                lirun += profitemp;
                            }
                        }
                    }
                    else {
                        for (int j = 0; j < info.length; j++) {
                            Long agenttemp = Long.valueOf(info[j].split(",")[0]);// 遍历当前的加盟商
                            if (agenttemp == 1) {
                                continue;
                            }
                            if (j == info.length - 1) {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[1]) / 100);
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                            else {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[2]) / 100);
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                        }
                    }
                }
            }
            else if (customerconfig == 1) {
                // 订单同步，支付到易订行
                Double sum = 0.0d;
                for (int i = 0; i < prices.size(); i++) {
                    HOrderDayPrice price = prices.get(i);
                    String liudianinfo = price.getBackpointinfo();
                    String[] info = liudianinfo.split("@");
                    sum += (price.getDayPrice() - price.getBaseprice());
                    if (order.getPaytype() == 2) {// 预付
                        for (int j = 0; j < info.length - 1; j++) {
                            Long agenttemp = Long.valueOf(info[j].split(",")[0]);// 遍历当前的加盟商
                            if (agenttemp == 1) {
                                continue;
                            }
                            if (price.getHotelmode() != null && price.getHotelmode() == 2) {
                                Double profitemp = Double.parseDouble(info[j].split(",")[2]);
                                sum -= profitemp;
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                            else {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[2]) / 100);
                                sum -= profitemp;
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                        }
                    }
                    else {
                        for (int j = 0; j < info.length; j++) {
                            Long agenttemp = Long.valueOf(info[j].split(",")[0]);// 遍历当前的加盟商
                            if (agenttemp == 1) {
                                continue;
                            }
                            if (j == info.length - 1) {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[1]) / 100);
                                sum -= profitemp;
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                            else {
                                Double profitemp = Math.floor(price.getAdviceprice()
                                        * Double.parseDouble(info[j].split(",")[2]) / 100);
                                sum -= profitemp;
                                mapaddcontrol(map, agenttemp, profitemp);
                            }
                        }
                    }
                }
                mapaddcontrol(map, 46l, sum);
            }

            if (map.size() > 0) {
                Set<Long> keys = map.keySet();
                Iterator<Long> ite = keys.iterator();
                while (ite.hasNext()) {
                    Long key = ite.next();
                    Double value = map.get(key);
                    if (type == 1) {
                        map.put(key, value * roomcount);
                    }
                    else if (type == 2) {
                        map.put(key, -value * roomcount);
                    }
                }
            }
        }
        return map;
    }

    /**
     * 
     * @param map 返点集合
     * @param agentid 加盟商id
     * @param money  返佣
     */
    public void mapaddcontrol(Map<Long, Double> map, Long agentid, Double money) {
        if (map.containsKey(agentid)) {
            Double value = map.get(agentid);
            value += money;
            map.put(agentid, value);
        }
        else {
            map.put(agentid, money);
        }
    }

    public Hotelorder findCustomerOrder(Hotelorder hotelorder) {
        String where = " where 1=1 and C_YEEORDERNUM='" + hotelorder.getOrderid() + "'";
        List<Hotelorder> orders = Server.getInstance().getHotelService().findAllHotelorder(where, "", -1, 0);
        if (orders.size() > 0) {
            Hotelorder sendinfo = orders.get(0);
            return sendinfo;
        }
        else {
            return null;
        }
    }
}
