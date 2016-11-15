package com.ccservice.b2b2c.atom.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.fax.FaxSender;
import com.ccservice.b2b2c.atom.interticket.ATMInternalTicket;
import com.ccservice.b2b2c.atom.interticket.InterTicker;
import com.ccservice.b2b2c.atom.interticket.InternationalAirTickets;
import com.ccservice.b2b2c.atom.mail.MailService;
import com.ccservice.b2b2c.atom.pay.AlipayAcePay;
import com.ccservice.b2b2c.atom.pay.Alipaycustomerunsign;
import com.ccservice.b2b2c.atom.pay.Alipaydistribute;
import com.ccservice.b2b2c.atom.pay.Alipaypartnercheck;
import com.ccservice.b2b2c.atom.pay.Billpaydistribute;
import com.ccservice.b2b2c.atom.pay.Chinapnrdistribute;
import com.ccservice.b2b2c.atom.pay.OFCard;
import com.ccservice.b2b2c.atom.pay.OneNineEPayRecharge;
import com.ccservice.b2b2c.atom.pay.Pay;
import com.ccservice.b2b2c.atom.pay.Tenpaydistribute;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.sms.SmsSender;
import com.ccservice.b2b2c.atom.sms.YMSmsSender;
import com.ccservice.b2b2c.atom.car.SupermacyMethod;
import com.ccservice.b2b2c.atom.ticketorder.CreateTicketOrder;
import com.ccservice.b2b2c.atom.train.ITrainHelper;
import com.ccservice.b2b2c.base.carorder.Carorder;
import com.ccservice.b2b2c.base.cars.Cars;
import com.ccservice.b2b2c.base.carstore.Carstore;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.fflight.InstancePrice;
import com.ccservice.b2b2c.base.fflight.InterZefees;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.interflight.SearchFligehtBean;
import com.ccservice.b2b2c.base.interticket.AllRouteBean;
import com.ccservice.b2b2c.base.interticket.RequestCreateOrderBean;
import com.ccservice.b2b2c.base.interticket.ResultSearchFlightByLinesBean;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.recharge.OFRechargeinfo;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;
import com.ccservice.b2b2c.base.specialprice.Specialprice;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.base.util.Insurances;
import com.ccservice.b2b2c.base.ymsend.Ymsend;
import com.ccservice.b2b2c.ben.Dnsbarends;
import com.ccservice.b2b2c.ben.PayInfo;
import com.ccservice.b2b2g.bean.GameProduct;
import com.ccservice.b2b2g.bean.PhoneInfo;
import com.ccservice.b2b2g.bean.PhoneProductInfo;
import com.ccservice.component.sms.SMSContentSys;
import com.ccservice.elong.inter.PropertyUtil;
import com.insurance.IInsuranceBook;
import com.insurance.ITianQuInsurance;
import com.insurance.TianQuInsurance;

public class AtomService implements IAtomService {

    public final Log logger = LogFactory.getLog(AtomService.class.getSimpleName());

    public static int accounttype = 0;

    // private String joyFaxCode;

    // private String joyFaxNumber;

    // private int newOrderTemplate;

    // private int modifyOrderTemplate;

    // private int cannelOrderTemplate;

    // private int extendedStayOrderTemplate;
    private InterTicker interTicker = null;

    public SmsSender smsSender = null;

    private MailService mailService = null;

    private FaxSender faxSender = null;

    private YMSmsSender ymsmsSender = null;

    private ITianQuInsurance itianquinsurance = new TianQuInsurance();

    // private XJSmsSender xjsmsSender = null;
    // private LCSmsSender lcsmsSender = null;
    // private FeiYouSmsSender feiyousmsSender = null;
    // private TianXunTongSmsSender tianxuntongsmsSender = null;
    // private YMHttpSmsSender ymhttpsmsSender = null;
    private OFCard ofcard;

    private OneNineEPayRecharge onenineepayrecharge;

    private ITrainHelper trainhelper = null;

    private InternationalAirTickets lechenginterticket = null;

    private ATMInternalTicket atminterticket = null;

    private CreateTicketOrder ticketorder = null;

    // 创建保单接口对象
    private IInsuranceBook insurancebook = null;

    public IInsuranceBook getInsurancebook() {
        return insurancebook;
    }

    public void setInsurancebook(IInsuranceBook insurancebook) {
        this.insurancebook = insurancebook;
    }

    /*
     * 手机在线充值。 (non-Javadoc)
     * 
     * @see com.ccservice.b2b2c.atom.service.IAtomService#onlineRecharge(int,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public String onlineRecharge(int cardtype, String cardnum, String ordernumber, String mobilenumber) {
        return ofcard.mobileRecharge(cardtype, cardnum, ordernumber, mobilenumber);
    }

    public List<Map<String, String>> getQmoneyInfo() throws IOException {
        return ofcard.getQmoneyInfo();
    }

    public Map<String, String> getQQmoney(String cardid) {

        return ofcard.getQQmoney(cardid);
    }

    /**
     * 国际机票接口
     * 
     * @param strFromCity
     *            出发城市
     * @param strToCity
     *            到达城市
     * @param strFromDate
     *            出发日期
     * @param strseatType
     *            舱位级别
     * @return
     */
    public AllRouteInfo interTicketSearch(String strFromCity, String strToCity, String strFromDate,
            String strReturnDate, String strseatType, String tirptype) {
        try {
            return this.lechenginterticket.airTicketSearch(strFromCity, strToCity, strFromDate, strReturnDate,
                    strseatType, tirptype);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Air86国际机票接口
     * 
     * @param fromcity
     *            出发城市
     * @param tocity
     *            到达城市
     * @param fromdate
     *            出发日期
     * @param cabin
     *            舱位代码
     * @param disc
     *            乘客类型
     * @param aircom
     *            航空公司
     * @return 国际航班数据
     */
    public AllRouteInfo AtmInterTicketSearch(String fromcity, String tocity, String fromdate, String cabin,
            String disc, String aircom) {
        try {
            return this.atminterticket.searchInterTicketData(fromcity, tocity, fromdate, cabin, disc, aircom);

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public AllRouteInfo getOutBound(String fromcity, String tocity, String fromdate, String todate, String cabin,
            String psgtype, String aircom, Integer psgcount) {
        // TODO Auto-generated method stub

        return this.atminterticket.getOutBound(fromcity, tocity, fromdate, todate, cabin, psgtype, aircom, psgcount);
    }

    @Override
    public AllRouteInfo getInBound(String fromcity, String tocity, String fromdate, String todate, String cabin,
            String psgtype, String aircom, Integer psgcount, String contractid) {
        // TODO Auto-generated method stub

        return this.atminterticket.getInBound(fromcity, tocity, fromdate, todate, cabin, psgtype, aircom, psgcount,
                contractid);
    }

    public String getAllFlightInfo(String triptype, String fromcity, String tocity, String fromdate, String fareid,
            String contractprice, String gm_f, Integer intTravelType, Integer backflag) {
        try {
            return this.atminterticket.getAllFlightInfo(triptype, fromcity, tocity, fromdate, fareid, contractprice,
                    gm_f, intTravelType, backflag);

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public List<InstancePrice> getQTEprice(String trivel_type, String carrier, String qinfo, String hinfo) {
        try {
            return this.atminterticket.getQTEprice(trivel_type, carrier, qinfo, hinfo);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public String getQFee(String triptype, String company, String qinfo, String hinfo) {
        try {
            return this.atminterticket.getQFee(triptype, company, qinfo, hinfo);

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public List<InterZefees> getInterZefees(String fare_id_ob, String depart_date, String return_date, String fare_id_ib)

    {
        try {
            return this.atminterticket.getInterZefees(fare_id_ob, depart_date, return_date, fare_id_ib);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public String getInterRules(String fareid) {
        try {
            return this.atminterticket.getInterRules(fareid);

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public String getMobileInfo(String phonenumber) {
        return "";

    }

    /**
     * 发送短信
     */
    public boolean sendSms(String[] mobiles, String content, long ordercode, long agentid, Dnsbarends dns, int type) {
        if (dns.getAgentsmsenable() == null) {
            dns.setAgentsmsenable(0);
        }
        List<String> list = new ArrayList<String>();
        for (String m : mobiles) {
            if (m != null && m.length() > 0) {
                list.add(m);
            }
        }
        WriteLog.write("sendOutticketmsg", ordercode + ":agentid:" + agentid);
        WriteLog.write("sendOutticketmsg", ordercode + ":" + Arrays.toString(mobiles));
        WriteLog.write("sendOutticketmsg", ordercode + ":" + dns.getAgentid());
        mobiles = new String[list.size()];
        boolean sended = false;
        int state = 0;
        if (list.size() > 0) {
            mobiles = list.toArray(mobiles);
            // 短信内容超过70字另算一条
            int length = getStringNum(content, 60);//航天华有发送的都是超长短信，60字一条已确认
            boolean sendable = true;
            long sendagentid = agentid;
            // 下级代理发送短信使用贴牌的短信条数不用充值
            if (0 == dns.getAgentsmsenable()) {
                sendagentid = dns.getAgentid();
            }
            boolean sendsmsable = sendsmsable(dns, sendagentid, mobiles.length * length, ordercode);
            if (!sendsmsable) {
                //content = "短信条数不足请充值";
                sendable = false;
                state = 3;// "短信条数不足请充值";
            }
            if (content != null && !"".equals(content)) {

            }
            else {
                state = 2;//未找到短信模版
                sendable = false;
            }
            String isSendSms = PropertyUtil.getValue("isSendSms", "sms.properties");//是否调接口发送短信 0 不发 1发
            if (sendable && "1".equals(isSendSms)) {
                sended = this.smsSender.sendSMS(mobiles, content, ordercode, agentid, dns);
                if (sended) {
                    state = 1;//发送成功
                }
                if (!sended) {
                    state = 0;//发送失败
                }
            }

            for (int j = 0; j < mobiles.length; j++) {
                Ymsend ymsend = new Ymsend();
                ymsend.setContent(content);
                ymsend.setCreatetime(new Timestamp(System.currentTimeMillis()));
                ymsend.setOrdercode(ordercode);
                String mobile = mobiles[j];
                ymsend.setPhone(mobile);
                ymsend.setState(state);
                ymsend.setType(type);
                try {
                    ymsend.setAgentid(agentid);
                    Server.getInstance().getMemberService().createYmsend(ymsend);
                }
                catch (Exception ex) {
                    this.logger.error("", ex.fillInStackTrace());
                }
            }
            WriteLog.write("sendOutticketmsg", ordercode + ":发送结果:" + sended);
            // 发送成功
            if (sended) {
                //if (1 == dns.getAgentsmsenable()) {// 控制下级代理短信条数
                String sql = "UPDATE T_CUSTOMERAGENT SET C_SMSCOUNT=C_SMSCOUNT-" + mobiles.length * length
                        + " WHERE ID=" + sendagentid;
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                sendclocksms(sendagentid, dns, mobiles.length * length);//短信剩余条数提醒
                //}
            }
        }
        return sended;

    }

    /**
     * 发送短信数量提醒短信
     * @param sendagentid 当前扣除用户短信id 
     * @param smscount  当前要发送的短信条数
     */
    public void sendclocksms(long sendagentid, Dnsbarends dns, long smscount) {
        String sql = "SELECT C_SMSCOUNT smscount,ISNULL(C_SMSCLOCKMOUNT,0) mount,ISNULL(C_SMSMOUNTCLOCK,0) clock,ISNULL(C_SMSGETTELPHONE,ISNULL(c_agentphone,'')) phone FROM T_CUSTOMERAGENT WHERE ID="
                + sendagentid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        int count = 0;//当前短信条数
        int mount = 0;//提醒条数
        int clock = 0;//提醒flag
        String tel = "";//提醒号码
        try {
            Map map = (Map) list.get(0);
            count = Integer.valueOf(map.get("smscount").toString());
            mount = Integer.valueOf(map.get("mount").toString());
            clock = Integer.valueOf(map.get("clock").toString());
            tel = map.get("phone").toString();
            if (clock == 1 && mount > 0) {
                if (mount < (count + smscount) && mount >= count) {
                    sendSms(new String[] { tel }, SMSContentSys.getSmsAccountclockcontent(mount + "条"), 0, sendagentid,
                            dns, 0);
                }
            }
        }
        catch (Exception e) {

        }
    }

    /**
     * 把字符串按长度计算条数 短信超过70就加1
     * 
     * @param content
     * @param length
     * @return
     */
    public int getStringNum(String content, int length) {
        float strLength = (float) content.length();
        int X = (int) Math.ceil(strLength / length);
        if (X < 1) {
            X = 1;
        }
        return X;
    }

    /**
     * 判断是否可以发送短信
     * @param dns
     * @param agentid
     * @param smscount
     * @param ordercode 
     * @return 能返回true 不能返回false
     */
    private boolean sendsmsable(Dnsbarends dns, long agentid, int smscount, long ordercode) {
        String sql = "SELECT C_SMSCOUNT smscount FROM T_CUSTOMERAGENT WHERE ID=" + agentid;
        boolean sendsmsable = true;
        if (dns.getAgentsmsenable() == 1) {
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            int count = 0;
            try {
                Map map = (Map) list.get(0);
                count = Integer.valueOf(map.get("smscount").toString());
            }
            catch (Exception e) {
                logger.error(agentid + ":" + smscount, e.fillInStackTrace());
                e.printStackTrace();
            }
            WriteLog.write("sendOutticketmsg", ordercode + ":agentid:" + agentid + ":count:" + count + ":smscount:"
                    + smscount);

            if (count < smscount) {
                sendsmsable = false;
            }
            WriteLog.write("sendOutticketmsg", ordercode + ":sendsmsable:" + sendsmsable);
        }
        return sendsmsable;
    }

    /**
     * 定制航班动态短信-飞友 String strMobiles,//手机号码 String Flightdate,//起飞日期 String Fno,
     * 航班号 String Dep, 出发城市三字码 String Arr, 到达城市三字码 String Pname, 乘机人姓名 String
     * Type, String Orderid 订单号
     */
    public int sendFeiyouSms(String strMobiles, String Flightdate, String Fno, String Dep, String Arr, String Pname,
            String Type, String cancel, long Orderid) {
        // return this.feiyousmsSender.sendFeiyouSms(strMobiles, Flightdate,
        // Fno,
        // Dep, Arr, Pname, Type, cancel, Orderid);
        return 1;
    }

    /**
     * 发送普通文本邮件
     */
    public int sendSimpleMails(String[] mails, String title, String body) {
        int result = -1;
        try {
            this.mailService.sendSimpleMails(mails, title, body);
            result = 0;
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            result = -1;
            e.printStackTrace();
        }
        return result;

    }

    /**
     * 发送HTML格式邮件
     */
    public int sendHTMLMails(String[] mails, String title, String body) {
        int result = -1;
        try {
            this.mailService.sendHTMLMails(mails, title, body);
            result = 0;
        }
        catch (Exception e) {
            result = -1;
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送带附件的邮件
     */

    public int sendAttachmentMails(String[] mails, String title, String body, String[] filepaths) {
        int result = -1;
        try {
            this.mailService.sendAttachmentMails(mails, title, body, filepaths);
            result = 0;
        }
        catch (Exception e) {
            result = -1;
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送传真
     */
    public int sendFax(String faxnum, String faxstr) {
        int result = -1;
        try {
            result = faxSender.sendfax(faxnum, faxstr);
        }
        catch (Exception e) {
            result = -1;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建传真发送文件
     */
    public String getHotelTemple(Map<String, String> map) {
        return faxSender.getHotelTemple(map);
    }

    /*---GETTER SETTER 方法---*/
    public SmsSender getSmsSender() {
        return smsSender;
    }

    public void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public FaxSender getFaxSender() {
        return faxSender;
    }

    public void setFaxSender(FaxSender faxSender) {
        this.faxSender = faxSender;
    }

    public OFCard getOfcard() {
        return ofcard;
    }

    public void setOfcard(OFCard ofcard) {
        this.ofcard = ofcard;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ccservice.b2b2c.atom.service.IAtomService#getPhonerechargeinfo(java.lang.String)
     *      根据手机号码获取充值信息
     */
    @Override
    public OFRechargeinfo getPhonerechargeinfo(String phonenumber, float money) {
        return this.ofcard.getPhonerechargeinfo(phonenumber, money);
    }

    @Override
    /**
     * Q币充值
     * 
     * @param ordernumber
     *            商家订单号
     * @param cardid
     *            所需提货商品的编码
     * @param buynum
     *            所需提货商品的数量
     * @param qqnumber
     *            QQ号
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public String qmoneyRecharge(String ordernumber, String cardid, int buynum, String qqnumber) {
        System.out.println(ofcard == null);
        return ofcard.qmoneyRecharge(ordernumber, cardid, buynum, qqnumber);
    }

    @Override
    public String getPaystate(String ordernumber) {
        try {
            return ofcard.getPaystate(ordernumber);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public Map<String, String> getUserInfo() {
        return ofcard.getUserInof();
    }

    @Override
    public List<Train> getSKTrainList(String fromcity, String tocity, String date) {
        System.out.println("**********ATOMSERVCIE:getSKTrainList*****");
        return trainhelper.getSKTrainList(fromcity, tocity, date);
    }

    public void setTrainhelper(ITrainHelper trainhelper) {
        this.trainhelper = trainhelper;
    }

    @Override
    public List<Train> getYPTrainList(String fromcity, String tocity, String date) {
        System.out.println("YPSKUnionTrainList开始接口对接查询");
        List<Train> trainlist = trainhelper.getYPSKUnionTrainList(fromcity, tocity, date);
        System.out.println("YPSKUnionTrainList接口对接查询结束");
        return trainlist;

    }

    public String createticketorder(List<Segmentinfo> listsegment, List<Passenger> listpassenger, Orderinfo orderinfo) {
        System.out.println("开始创建本地订单");
        return ticketorder.CreateOrderByIF(listsegment, listpassenger, orderinfo);
    }

    @Override
    public List<Map<String, String>> getTrainInfo(String traincode, String date) {
        return this.trainhelper.getTraininfo(traincode, date);
    }

    @Override
    public List<Train> getDGTrainList(String fromcity, String tocity, String date) {
        return trainhelper.getDGTrainList(fromcity, tocity, date);
    }

    @Override
    public List<Train> getDGTrainListcache(String fromcity, String tocity, String date, FlightSearch param) {
        return trainhelper.getDGTrainListcache(fromcity, tocity, date, param);
    }

    @Override
    public void createTrainrebate(Train train) {
        trainhelper.createTrainrebate(train);

    }

    public CreateTicketOrder getTicketorder() {
        return ticketorder;
    }

    public void setTicketorder(CreateTicketOrder ticketorder) {
        this.ticketorder = ticketorder;
    }

    public InternationalAirTickets getLechenginterticket() {
        return lechenginterticket;
    }

    public void setLechenginterticket(InternationalAirTickets lechenginterticket) {
        this.lechenginterticket = lechenginterticket;
    }

    /**
     * 发送定时短信
     */
    @Override
    public int sendTimeingSms(String[] phone, String message, String addserial, String sendTime) {
        // return this.ymhttpsmsSender.sendTimeingSms(phone, message, addserial,
        // sendTime);
        return 1;
    }

    /**
     * 发送即时短信
     */
    @Override
    public int sendLuckySms(String[] phone, String message, String addserial) {
        // return this.ymhttpsmsSender.sendLuckySms(phone, message, addserial);
        return 1;
    }

    /**
     * 注册
     */
    @Override
    public int Ymsregister() {
        // return this.ymhttpsmsSender.Ymsregister();
        return 1;
    }

    /**
     * 充值
     */
    @Override
    public int chargeup(String cardno, String cardpass) {
        // return this.ymhttpsmsSender.chargeup(cardno, cardpass);
        return 1;
    }

    /**
     * 接收上行短信
     */
    @Override
    public int getmo() {
        // return this.ymhttpsmsSender.getmo();
        return 1;
    }

    /**
     * 查询余额
     */
    @Override
    public int querybalance() {
        // return this.ymhttpsmsSender.querybalance();
        return 1;
    }

    /**
     * 电子保险接口
     * 
     * @author 赵晓晓 电子保险单创建
     */
    @Override
    public List<Insurances> orderAply(String jyNo, Customeruser user, List list, String begintime, String[] fltno)
            throws Exception {
        return insurancebook.orderAplylist(jyNo, user, list, begintime, fltno);
    }

    /**
     * 电子保险单获取
     */
    @Override
    public DataHandler PolicyReprint(Insurorder order) throws Exception {
        return insurancebook.PolicyReprint(order);
    }

    @Override
    public List newOrderAplylist(String[] jyNo, List list) {
        return insurancebook.newOrderAplylist(jyNo, list);
    }

    @Override
    public List saveTrainOrderAplylist(String[] jyNo, List list, int type) {
        return insurancebook.saveTrainOrderAplylist(jyNo, list, type);
    }

    @Override
    public String cancelInsuruser(Insuruser insur) {
        // TODO Auto-generated method stub
        return insurancebook.cancelOrderAplylist(insur);
    }

    SupermacyMethod supermacyMethod = new SupermacyMethod();

    /**
     * 自驾 得到车型选择列表
     */
    @Override
    public List<Cars> searchCars(int rentStoreID, int returnStoreID, String rentTime, String returnTime, String rentD,
            String returnD) {
        return supermacyMethod.getCarList(rentStoreID, returnStoreID, rentTime, returnTime, rentD, returnD);
    }

    /**
     * 代驾 汽车列表
     */
    @Override
    public List<Cars> searchDriverCars(int productClassID, String preGetOnTime, String preGetOffTime, int getOnAreaID,
            int getoffAreaID, int personCount, String getOnTime, String getOffTime, double useMileage,
            boolean hasDriverBide, String GoOnAddress, String GoOffAddress, double GoOnAddressXCoordinate,
            double GoOnAddressYCoordinate, double GoOffAddressXCoordinate, double GoOffAddressYCoordinate, int MapType) {
        // TODO Auto-generated method stub
        return supermacyMethod.GetChooseCarLevelList(productClassID, preGetOnTime, preGetOffTime, getOnAreaID,
                getoffAreaID, personCount, getOnTime, getOffTime, useMileage, hasDriverBide, GoOnAddress, GoOffAddress,
                GoOnAddressXCoordinate, GoOnAddressYCoordinate, GoOffAddressXCoordinate, GoOffAddressYCoordinate,
                MapType);
    }

    /**
     * 自驾 至尊创建订单方法
     */
    @Override
    public String createCarOrder(String custName, String custPaperNo, String custPaperTypeID, String gender,
            String mobilePhone, String modelID, String rentStoreID, String rentTime, String returnTime, String rentD,
            String returnD, String reservationType, String returnStoreID) {
        return supermacyMethod.CreateCarOrder(custName, custPaperNo, custPaperTypeID, gender, mobilePhone, modelID,
                rentStoreID, rentTime, returnTime, rentD, returnD, reservationType, returnStoreID);
    }

    /**
     * 自驾 得到支付的跳转地址
     */
    @Override
    public String getPayUrl(int billID) {
        // TODO Auto-generated method stub
        return supermacyMethod.getPayUrl(billID);
    }

    /**
     * 取消订单
     */
    @Override
    public String CancelOrder(int orderID) {
        // TODO Auto-generated method stub
        return supermacyMethod.CancelOrder(orderID);
    }

    /**
     * 自驾 取消订单
     */
    @Override
    public String selfCancelOrder(int orderID) {
        return supermacyMethod.selfCancelOrder(orderID);
    }

    @Override
    public String GetChauffeurDriveOrderSave(String custname, boolean gender, String mobilePhone, String passengerName,
            boolean passengerGender, String passengerMobilePhone, int personCount, String preGetOnTime,
            String preGetOffTime, int getOnAreaID, String GoOnAddress, int getoffAreaID, String GoOffAddress,
            boolean isNeedInvoice, String invoiceHead, String invoiceMailAddress, String travelDescription,
            int carGroupID, int productClassID, int airportTypeID, String flightCode, boolean isNeedPickBrand,
            String pickBrandContent, int LuggageCount, String getOnTime, String getOffTime, boolean hasDriverBide,
            String operatorSN, double GoOnAddressXCoordinate, double GoOnAddressYCoordinate,
            double GoOffAddressXCoordinate, double GoOffAddressYCoordinate, int MapType, int CouponID, String memo) {
        // TODO Auto-generated method stub

        return supermacyMethod.GetChauffeurDriveOrderSave(custname, gender, mobilePhone, passengerName,
                passengerGender, passengerMobilePhone, personCount, preGetOnTime, preGetOffTime, getOnAreaID,
                GoOnAddress, getoffAreaID, GoOffAddress, isNeedInvoice, invoiceHead, invoiceMailAddress,
                travelDescription, carGroupID, productClassID, airportTypeID, flightCode, isNeedPickBrand,
                pickBrandContent, LuggageCount, getOnTime, getOffTime, hasDriverBide, operatorSN,
                GoOnAddressXCoordinate, GoOnAddressYCoordinate, GoOffAddressXCoordinate, GoOffAddressYCoordinate,
                MapType, CouponID, memo);

    }

    @Override
    public Carorder GetChauffeurDriveOrderInfo(int orderID) {
        // TODO Auto-generated method stub
        return supermacyMethod.GetChauffeurDriveOrderInfo(orderID);
    }

    /**
     * 得到特价车列表
     * 
     * @param cityid
     * @return 特价车列表
     */
    public List<Cars> getSpecialCarList(long cityid) {
        return supermacyMethod.getSpecialCarList(cityid);
    }

    public String[] getSpecialCarList1(long cityid) {
        return supermacyMethod.getSpecialCarList1(cityid);
    }

    public ATMInternalTicket getAtminterticket() {
        return atminterticket;
    }

    public void setAtminterticket(ATMInternalTicket atminterticket) {
        this.atminterticket = atminterticket;
    }

    @Override
    public int sendFeiyouSms(String strMobiles, String Flightdate, String Fno, String Dep, String Arr, String Pname,
            String Type, String cancel, String Orderid) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String distribute(long orderid, int ywtype) {
        WriteLog.write("订单分润日志", "AtomService调用分润接口，网上支付异步分润：" + orderid + ":" + accounttype);
        if (accounttype == 0) {
            List<PayInfo> pays = Server.getInstance().getB2BSystemService().findAllAccountinfo();
            if (pays == null || pays.size() == 0) {
                WriteLog.write("订单分润日志", "AtomService 支付账户未维护");
            }
            accounttype = Integer.valueOf(pays.get(0).getId() + "");
            WriteLog.write("订单分润日志", "AtomService accounttype:" + accounttype);
        }

        if (accounttype == 3) {
            // 汇付天下 主动结算。
            return Chinapnrdistribute.distribute(orderid, ywtype);
        }
        else if (accounttype == 1) {
            return Alipaydistribute.distribute(orderid, ywtype);
        }
        else if (accounttype == 2) {
            return Billpaydistribute.distribute(orderid, ywtype);
        }
        else if (accounttype == 5) {
            return Tenpaydistribute.distribute(orderid, ywtype);
        }

        return "";

    }

    @Override
    public boolean alipaypartnercheck(String useemail) {
        return Alipaypartnercheck.partnercheck(useemail);
    }

    // =====================国际机票==start==huc=====================================================
    /**
     * 国际机票查询参数类得到航班信息列表 huc
     * 
     * @param paramsByjson
     */
    public AllRouteBean searchingFligehts(SearchFligehtBean searchFligehtBean) {
        return this.interTicker.searchingFligehts(searchFligehtBean);
    }

    public ResultSearchFlightByLinesBean getInterTicket(SearchFligehtBean searchFligehtBean) {
        return this.interTicker.getInterTicket(searchFligehtBean);
    }

    /**
     * 国际机票创建订单 huc
     * 
     * @param resultStr
     */
    public String createOrder(RequestCreateOrderBean requestCreateOrderBean) {
        return this.interTicker.createOrder(requestCreateOrderBean);
    }

    /**
     * 国际机票接受价格变化接口 huc
     * 
     * @param searchFligehtBean
     */
    public String priceChange(String orderid) {
        return this.interTicker.priceChange(orderid);
    }

    /**
     * 国际机票接受行程变化接口 huc
     * 
     * @param searchFligehtBean
     */
    public String strokeChange(String orderid) {
        return this.interTicker.strokeChange(orderid);
    }

    /**
     * 国际机票 订单查询接口 huc
     * 
     * @param orderid
     */
    public String searchingOrder(String orderid) {
        return this.interTicker.searchingOrder(orderid);
    }

    /**
     * 国际机票 退票接口 huc
     * 
     * @param orderid
     */
    public String returnTicket(String orderid) {
        return this.interTicker.returnTicket(orderid);
    }

    // =====================国际机票==end==huc=====================================================

    public InterTicker getInterTicker() {
        return interTicker;
    }

    public void setInterTicker(InterTicker interTicker) {
        this.interTicker = interTicker;
    }

    @Override
    public List<Specialprice> getSpecialpriceList(String isinternal, String startport, String arrivalport) {
        // TODO Auto-generated method stub
        try {
            String url = "http://jipiao.quna.com/ShowChartServlet";
            String paramContent = "srcCity=" + startport + "&dstCity=" + arrivalport;
            StringBuffer strb = submitPost(url, paramContent);
            JSONObject jsonObject = JSONObject.fromObject(strb.toString().replace("\n", "").replace("null,", "")
                    .replace("null", ""));
            String elementsString = jsonObject.getString("elements");
            elementsString = elementsString.substring(1, elementsString.length() - 1);
            if (elementsString.indexOf("values") == -1) {
                return null;
            }

            jsonObject = JSONObject.fromObject(elementsString);

            String valString = jsonObject.getString("values");

            valString = valString.substring(1, valString.length() - 1);
            String str[] = valString.split("}");
            List<Specialprice> list = new ArrayList<Specialprice>();
            Specialprice sp = null;
            for (int i = 0; i < str.length; i++) {
                valString = str[i].replace(",{", "").replace("{", "");
                if (valString.indexOf("value") == -1 || valString.indexOf("tip") == -1
                        || valString.indexOf("#val#") == -1 || valString.indexOf("折") == -1
                        || valString.indexOf("周") == -1) {
                    System.out.println("没有价格，或者数据不全，该数据作废");
                }
                else {
                    sp = new Specialprice();

                    // 起飞机场
                    sp.setStartport(startport);
                    // 目的机场
                    sp.setArrivalport(arrivalport);
                    // 更新时间
                    sp.setUpdatetime(new Timestamp(System.currentTimeMillis()));
                    // 创建者
                    sp.setCreateuser("jipiao.quna.com");
                    // 创建时间
                    sp.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    // 修改者
                    sp.setModifyuser("jipiao.quna.com");
                    // 修改时间
                    sp.setModifytime(new Timestamp(System.currentTimeMillis()));
                    sp.setIsinternal(Integer.parseInt(isinternal));
                    valString = "{" + valString + "}";
                    jsonObject = JSONObject.fromObject(valString);
                    // 价格
                    sp.setPrice(Float.valueOf(jsonObject.getString("value")));
                    String temp = jsonObject.getString("tip").split("#val#")[1];
                    // 折扣
                    sp.setDiscount(Float.valueOf(temp.split("折")[0].substring(2)));
                    // 起飞时间
                    sp.setStarttime(dateToTimestamp2(temp.split("折")[1].split("周")[0].replace("(", "").replace(")", "")));
                    list.add(sp);
                    // System.out.println(jsonObject.getString("value")+"--"+jsonObject.getString("tip")+"--"+jsonObject.getString("on-click"));
                    // System.out.println("航空公司："+jsonObject.getString("tip").split("#val#")[0]);
                    // String temp =
                    // jsonObject.getString("tip").split("#val#")[1];
                    // System.out.println("折扣："+temp.split("折")[0].substring(2));
                    // 出发日期：)2013-04-17(周三)北京-上海06:50-09:10天涯行
                    // System.out.println("出发日期："+temp.split("折")[1].split("周")[0].replace("(",
                    // "").replace(")", ""));
                    // System.out.println("");
                }

            }
            return list;
        }
        catch (NumberFormatException e) {
            return null;
        }

    }

    /**
     * java.net实现 HTTP POST方法提交
     * 
     * @param url
     * @param paramContent
     * @return
     */
    public static StringBuffer submitPost(String url, String paramContent) {
        StringBuffer responseMessage = null;
        java.net.URLConnection connection = null;
        java.net.URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;
        String param = paramContent;
        try {

            // System.out.println("url=" + url + "?" + paramContent + "\n");
            // System.out.println("===========post method start=========");
            responseMessage = new StringBuffer();
            reqUrl = new java.net.URL(url);
            connection = reqUrl.openConnection();
            connection.setDoOutput(true);
            reqOut = new OutputStreamWriter(connection.getOutputStream());
            reqOut.write(param);
            reqOut.flush();
            int charCount = -1;
            in = connection.getInputStream();

            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            while ((charCount = br.read()) != -1) {
                responseMessage.append((char) charCount);
            }
            // System.out.println(responseMessage);
            // System.out.println("===========post method end=========");
        }
        catch (Exception ex) {
            System.out.println("url=" + url + "?" + paramContent + "\n e=" + ex);
        }
        finally {
            try {
                in.close();
                reqOut.close();
            }
            catch (Exception e) {
                System.out.println("paramContent=" + paramContent + "|err=" + e);
            }
        }
        return responseMessage;
    }

    public static Timestamp dateToTimestamp2(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            if (date.length() == 10) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
            return (new Timestamp(dateFormat.parse(date).getTime()));

        }
        catch (Exception e) {
            return null;
        }

    }

    @Override
    public String getMobileSMS(String MCode) {
        return this.ymsmsSender.getMobileSMS(MCode);
    }

    @Override
    public AllRouteInfo findInterTicketSearchByNBE(String FromCity1, String ToCity1, String TakeoffDate1,
            String TakeoffTime1, String Carrier1, String IsDirect1, String IsShared1) {
        return this.lechenginterticket.searchFlightByLT(FromCity1, ToCity1, TakeoffDate1, TakeoffTime1, Carrier1,
                IsDirect1, IsShared1);
    }

    @Override
    public boolean aplipaycustomerunsign(String useemail) {
        return Alipaycustomerunsign.customerunsign(useemail);
    }

    @Override
    public String getQTEpricebyLT(String FromCity, String ToCity, String TakeoffDate, String FlightNum,
            String SeatClass, String PassengerType, String IsBottomPrice, String IsFSQ) {
        return this.lechenginterticket.QTEgetprice(FromCity, ToCity, TakeoffDate, FlightNum, SeatClass, PassengerType,
                IsBottomPrice, IsFSQ);
    }

    public YMSmsSender getYmsmsSender() {
        return ymsmsSender;
    }

    public void setYmsmsSender(YMSmsSender ymsmsSender) {
        this.ymsmsSender = ymsmsSender;
    }

    /**
     * 自驾 生成门店信息
     */
    @Override
    public List<Carstore> GetChooseJourney() {
        // TODO Auto-generated method stub
        return supermacyMethod.GetChooseJourney();
    }

    @Override
    public List[] searchingRouteInfo(String tripType, String peoNum, String proType, String cbinType, String origCity,
            String destCity, String srcDate, String retDate) {
        // TODO Auto-generated method stub
        List[] list = atminterticket.getRouteInfo(tripType, peoNum, proType, cbinType, origCity, destCity, srcDate,
                retDate);
        return list;
    }

    @Override
    public String searchingTaxQuery(String tripType, String peoNum, String proType, String str1, String str2) {
        // TODO Auto-generated method stub
        String tax = atminterticket.getTaxQuery(tripType, peoNum, proType, str1, str2);
        return tax;
    }

    @Override
    public String searchingFareVerify(String tripType, String peoNum, String proType, String str1, String str2) {
        // TODO Auto-generated method stub
        String price = atminterticket.getFareVerify(tripType, peoNum, proType, str1, str2);
        return price;
    }

    public ITianQuInsurance getItianquinsurance() {
        return itianquinsurance;
    }

    public void setItianquinsurance(ITianQuInsurance itianquinsurance) {
        this.itianquinsurance = itianquinsurance;
    }

    /**
     * 天衢保险 创建订单
     * 
     * @param ainame
     *            投保人姓名
     * @param aimobile
     *            投保人电话
     * @param aiaddress
     *            投保人地址 可以为空
     * @param aiidcard
     *            证件类型:1 身份证 2 护照 3 军人证 4 港台同胞证 5 其它证件
     * @param aiidnumber
     *            证件号
     * @param aieffectivedate
     *            生效日期
     * @param aiemail
     *            电子邮件
     * @param aisex
     *            投保人性别 -1保密 1 男 0 女
     * @param aiflightnumber
     *            航班号
     * @return
     */
    @Override
    public String tianQuCreateOrder(String ainame, String aimobile, String aiaddress, String aiidcard,
            String aiidnumber, String aieffectivedate, String aiemail, String aisex, String aiflightnumber) {
        return itianquinsurance.createOrder(ainame, aimobile, aiaddress, aiidcard, aiidnumber, aieffectivedate,
                aiemail, aisex, aiflightnumber);
    }

    /**
     * 天衢保险 查询订单详细
     * 
     * @param orderid
     *            订单号
     * @return
     */
    @Override
    public String tianQuOrderInfo(String orderid) {
        // TODO Auto-generated method stub
        return itianquinsurance.orderInfo(orderid);
    }

    /**
     * 天衢保险 取消订单
     * 
     * @param orderid
     *            订单号
     * @return
     */
    @Override
    public String tianQuCancleOrder(String orderid) {
        // TODO Auto-generated method stub
        return itianquinsurance.cancleOrder(orderid);
    }

    /**
     * 天衢保险 第一次投保
     * 
     * @param orderid
     *            订单号
     * @return
     */
    @Override
    public String tianQuPayOrder(String orderid) {
        // TODO Auto-generated method stub
        return itianquinsurance.payOrder(orderid);
    }

    /**
     * 天衢保险 退保
     * 
     * @param orderNumber
     *            保单号
     * @return
     */
    @Override
    public String tianQuBackOrder(String orderNumber) {
        // TODO Auto-generated method stub
        return itianquinsurance.backOrder(orderNumber);
    }

    /**
     * 天衢保险 投保失败时再次投保
     * 
     * @param orderid
     *            订单号
     * @param ainame
     *            投保人姓名
     * @param aisex
     *            投保人性别 -1保密 1 男 0 女
     * @param aiidnumber
     *            证件号
     * @param aimobile
     *            投保人电话
     * @param aiemail
     *            电子邮件
     * @param aiaddress
     *            投保人地址 可以为空
     * @param aiflightnumber
     *            航班号
     * @return
     */
    @Override
    public String tianQuPayOrderAgain(String orderid, String ainame, String aisex, String aiidnumber, String aimobile,
            String aiemail, String aiaddress, String aiflightnumber) {
        // TODO Auto-generated method stub
        return itianquinsurance.payOrderAgain(orderid, ainame, aisex, aiidnumber, aimobile, aiemail, aiaddress,
                aiflightnumber);
    }

    /**
     * 自驾特价 创建订单
     * 
     * @param strSchemeID
     *            特价车方案ID
     * @param returnStoreID
     *            还车门店ID
     * @param rentTime
     *            租车时间
     * @param returnTime
     *            还车时间
     * @param isNeedGPS
     *            是否需要GPS
     * @param customerName
     *            客户姓名
     * @param mobilePhoneNo
     *            手机号
     * @param custPaperID
     *            证件类型ID (43二代身份证 1419 回乡证 1420台胞证 123 国际护照 519其它证件)
     * @param paperNo
     *            证件号
     * @param gender
     *            姓别（true为男性，false为女性）
     * @param isHasSixMonthDrivingLicense
     *            是否有6个月以上驾驶证
     * @param linkmanName
     *            联系人姓名
     * @param linkmanmobilePhoneNo
     *            联系人手机号
     * @param remark
     *            备注（可为空）
     * @param reservationType
     *            预订类型（1代表预定；2代表预约）
     * @return
     */
    @Override
    public String specialCarSaveOrder(String strSchemeID, int returnStoreID, String rentTime, String returnTime,
            boolean isNeedGPS, String customerName, String mobilePhoneNo, int custPaperID, String paperNo,
            boolean gender, boolean isHasSixMonthDrivingLicense, String linkmanName, String linkmanmobilePhoneNo,
            String remark, int reservationType) {
        // TODO Auto-generated method stub
        return supermacyMethod.specialCarSaveOrder(strSchemeID, returnStoreID, rentTime, returnTime, isNeedGPS,
                customerName, mobilePhoneNo, custPaperID, paperNo, gender, isHasSixMonthDrivingLicense, linkmanName,
                linkmanmobilePhoneNo, remark, reservationType);
    }

    @Override
    public String getNetworkOrder(Train train, Trainpassenger passenger) {
        return trainhelper.getNetworkOrder(train, passenger);
    }

    @Override
    public boolean sendTrainorderalarmsms() {
        return this.trainhelper.sendAlermsms(this.smsSender);
    }

    public OneNineEPayRecharge getOnenineepayrecharge() {
        return onenineepayrecharge;
    }

    public void setOnenineepayrecharge(OneNineEPayRecharge onenineepayrecharge) {
        this.onenineepayrecharge = onenineepayrecharge;
    }

    @Override
    public PhoneInfo accsegment(String mobilenum) {
        return this.onenineepayrecharge.accsegment(mobilenum);
    }

    @Override
    public List<PhoneProductInfo> productQuery() {
        return this.onenineepayrecharge.productQuery();
    }

    @Override
    public String directFill(String prodid, String orderid, String mobilenum) {
        return this.onenineepayrecharge.directFill(prodid, orderid, mobilenum);
    }

    @Override
    public String orderQuery(String orderid) {
        return this.onenineepayrecharge.orderQuery(orderid);
    }

    @Override
    public String canRechargeGameQuery() {
        return onenineepayrecharge.canRechargeGameQuery();
    }

    @Override
    public List<GameProduct> gameProductQuery(String gameid) {
        return onenineepayrecharge.gameProductQuery(gameid);
    }

    @Override
    public String directRecharge(String gameid, String parvalue, String orderid, String chargeaccount, int fillnum,
            String clientip) {
        return onenineepayrecharge.directRecharge(gameid, parvalue, orderid, chargeaccount, fillnum, clientip);
    }

    @Override
    public String orderResultQuery(String orderid) {
        return onenineepayrecharge.orderResultQuery(orderid);
    }

    @Override
    public String autopayace(long orderid, String helpername, String payacount) {
        String result = "";
        WriteLog.write("Payframework", "AlipayAcePay" + ":" + helpername + ":");
        try {
            Payhelper payhelper = (Payhelper) Class.forName(Payhelper.class.getPackage().getName() + "." + helpername)
                    .getConstructor(long.class).newInstance(orderid);
            String className1 = Pay.class.getPackage().getName() + "." + "AlipayAcePay";
            AlipayAcePay pay = (AlipayAcePay) Class.forName(className1).getConstructor(Payhelper.class)
                    .newInstance(payhelper);
            result = pay.pay(payacount);
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("Payframework", "AlipayAcePay:" + e.getMessage());
        }
        return result;
    }

}