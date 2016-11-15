package com.ccservice.b2b2c.atom.servlet.tq;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.IAtomService;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 同城保险类
 * @author 
 *
 */
public class TrainInsure {
    private static TrainInsure tcti = null;

    private TrainInsure() {

    }

    public static TrainInsure getTongChengTrainInsure() {
        if (tcti == null) {
            tcti = new TrainInsure();
        }
        return tcti;
    }

    /**
     * 投保
     * 
     * @param trainorder
     * @return
     * @time 2015年5月18日 下午4:37:39
     * 
     */
    public Trainorder insuranceSynchronous(Trainorder trainorder) {
        for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                /**
                 * 票中包含有保单号就不在投保
                 */
                if (trainticket.getInsurorigprice() > 0
                        && (trainticket.getRealinsureno() == null || "".equals(trainticket.getRealinsureno())))//票里的保险价钱大于0说明客户需要投保
                {
                    int r1 = new Random().nextInt(10000000);
                    Insuruser insuruser = insureTrain(trainpassenger.getIdtype() + "", trainpassenger.getIdnumber(),
                            trainticket.getDeparttime(), trainpassenger.getBirthday(), trainorder.getQunarOrdernumber(),
                            trainpassenger.getName(), trainticket.getTrainno(), trainorder.getContacttel(),
                            trainorder.getAgentid(), r1, trainorder.getId(), trainorder.getInsureadreess(),
                            trainorder.getOrdernumber(), trainorder.getCreatetime(), trainorder.getQunarOrdernumber(),
                            "0", trainorder.getId());
                    if (insuruser != null && insuruser.getPolicyno() != null && !"".equals(insuruser.getPolicyno())) {
                        trainticket.setRealinsureno(insuruser.getPolicyno());
                        Server.getInstance().getTrainService().updateTrainticket(trainticket);
                        WriteLog.write("TongCheng_toubao",
                                trainorder.getId() + ":" + trainpassenger.getName() + ":" + insuruser.getPolicyno());
                    }
                }
            }
        }
        return trainorder;
    }

    /**
     * 保险方法
     * 
     * @param idtype
     * @param idno
     * @param traindate
     * @param birthday
     * @param trainordernumber
     * @param name
     * @param tradeno
     * @param mobile
     * @param agentid
     * @param r1
     * @return
     * @time 2015年5月18日 下午4:40:53
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    public Insuruser insureTrain(String idtype, String idno, String traindate, String birthday, String trainordernumber,
            String name, String tradeno, String mobile, long agentid, int r1, long id, String adds, String ordernum,
            Timestamp createdate, String qunarordernum, String flag, long orderid) {
        WriteLog.write("同城保险接口_投保",
                r1 + ":idtype:" + idtype + ":idno:" + idno + ":traindate:" + traindate + ":birthday:" + birthday
                        + ":trainordernumber:" + trainordernumber + ":name:" + name + ":tradeno:" + tradeno + ":mobile:"
                        + mobile + ":agentid:" + agentid);
        Insuruser insuruser = new Insuruser();
        if (orderid == 0l) {
            return insuruser;
        }
        else {
            try {
                insuruser.setAgentid(agentid);
                insuruser.setCodetype(Long.valueOf(idtype));
                insuruser.setCode(idno);
                insuruser.setFlytime(sunshineTimestamp(traindate, "yyyy-MM-dd HH:mm"));
                insuruser.setOrdernum(trainordernumber);
                insuruser.setOrderid(orderid);
                //TODO point 投保时间 无论是否成功 都需要set
                insuruser.setBegininsuretime(new Timestamp(System.currentTimeMillis()));
                try {
                    if (birthday == null || "".equals(birthday)) {
                        if (idno.length() == 18) {
                            birthday = idno.substring(6, 10) + "-" + idno.substring(10, 12) + "-"
                                    + idno.substring(12, 14);
                            if (!isAdult(birthday)) {
                                WriteLog.write("生成保险生日日期", "未成年转换保险生日--->" + orderid);
                                birthday = "1984-06-01";
                            }
                        }
                        else {
                            birthday = "1984-06-01";
                            WriteLog.write("生成保险生日日期", "护照转换保险生日--->" + orderid);
                        }
                    }
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException("生成保险生日日期_ERROR", e);
                }
                try {
                    insuruser.setBirthday(formatStringToTime(birthday, "yyyy-MM-dd"));
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException("赋值保险生日日期_ERROR", e);
                }
                insuruser.setName(name);
                insuruser.setFlyno(tradeno);
                insuruser.setMobile(mobile);
                insuruser.setEmail("baoxian@clbao.com");
                insuruser.setInsurstatus(0);
                List<Insuruser> insurlist = new ArrayList<Insuruser>();
                insuruser = Server.getInstance().getAirService().createInsuruser(insuruser);
                insurlist.add(insuruser);
                List<Insuruser> listinsurance = new ArrayList<Insuruser>();
                //调用投保保险接口
                listinsurance = gethyxInsureAtomService().newOrderAplylist(null, insurlist);
                if (listinsurance.size() > 0) {
                    WriteLog.write("同城保险接口_投保", r1 + ":投保成功");
                    insuruser.setPolicyno(listinsurance.get(0).getPolicyno());
                    insuruser.setInsurstatus(listinsurance.get(0).getInsurstatus());
                    insuruser.setRemark(listinsurance.get(0).getRemark());
                }
                WriteLog.write("同城保险接口_投保", r1 + ":信息保存完成");
                Server.getInstance().getAirService().updateInsuruser(insuruser);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("投保_ERROR", e);
            }
        }
        WriteLog.write("同城保险接口_投保", r1 + ":id:" + insuruser.getId() + ":policyno:" + insuruser.getPolicyno()
                + ":insurstatus:" + insuruser.getInsurstatus());
        return insuruser;
    }

    /**
     * 获取保险的cn_interface地址
     * 
     * @return
     * @time 2015年5月25日 下午3:44:20
     * @author Auser
     */
    public static IAtomService gethyxInsureAtomService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        String search_12306yupiao_service_url = PropertyUtil.getValue("hyxInsure_cn_interface_url", "Train.properties");
        try {
            return (IAtomService) factory.create(IAtomService.class,
                    search_12306yupiao_service_url + IAtomService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断是否是成人
     * 
     * @param brithday
     *            生日 yyyy-MM-dd
     * @return 年龄
     * @author fiend
     */
    public static boolean isAdult(String brithday) {
        try {
            int brithday_year = Integer.valueOf(brithday.split("-")[0]);
            int brithday_month = Integer.valueOf(brithday.split("-")[1]);
            int brithday_day = Integer.valueOf(brithday.split("-")[2]);
            Calendar now = Calendar.getInstance();
            if ((now.get(Calendar.YEAR) - brithday_year) > 18) {
                return true;
            }
            if ((now.get(Calendar.YEAR) - brithday_year) == 18) {
                if (((now.get(Calendar.MONTH) + 1) - brithday_month) > 0) {
                    return true;
                }
                if (((now.get(Calendar.MONTH) + 1) - brithday_month) == 0) {
                    if (now.get(Calendar.DAY_OF_MONTH) > brithday_day) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (Exception e) {
            return true;
        }
    }

    /**
     * 时间转换 
     * @param date
     * @param format
     * @return
     * @time 2014年10月9日 下午6:45:34
     * @author yinshubin
     */
    public Timestamp formatStringToTime(String date, String format) {
        try {
            SimpleDateFormat simplefromat = new SimpleDateFormat(format);
            return new Timestamp(simplefromat.parse(date).getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取一个可以投保的发车时间  如果实际时间不满足 就+1个小时
     * @param traindate
     * @param format
     * @return
     * @author fiend
     */
    private Timestamp sunshineTimestamp(String traindate, String format) {
        try {
            if (isNeedFalsityTrainDate(traindate, format)) {
                return falsityTrainDate(traindate, format);
            }
            else {
                return formatStringToTime(traindate, format);
            }
        }
        catch (ParseException e) {
            return formatStringToTime(traindate, format);
        }
    }

    /**
     * 发车时间-系统当前时间 是否大于63分钟
     * @param traindate
     * @param format
     * @return
     * @author fiend
     */
    private boolean isNeedFalsityTrainDate(String traindate, String format) {
        try {
            SimpleDateFormat simplefromat = new SimpleDateFormat(format);
            return (simplefromat.parse(traindate).getTime() - System.currentTimeMillis()) < (63 * 60 * 1000);
        }
        catch (ParseException e) {
            return false;
        }
    }

    /**
     * 获取虚假发车时间 原发车时间+1小时
     * @param traindate
     * @param format
     * @return
     * @author fiend
     * @throws ParseException   
     */
    private Timestamp falsityTrainDate(String traindate, String format) throws ParseException {
        SimpleDateFormat simplefromat = new SimpleDateFormat(format);
        return new Timestamp(simplefromat.parse(traindate).getTime() + (60 * 60 * 1000));
    }

    /**
     * 退保
     * @return
     */
    public boolean tuibao(long trainorderid, String msg, String Insureno) {
        if (Insureno == null || Insureno.equals("")) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            int r1 = new Random().nextInt(10000000);
            jsonObject = cancelOrderAplylist(Insureno, msg, r1);
            if (jsonObject.getBoolean("success")) {
                return uorderisu1("", Insureno);
            }
            WriteLog.write("同城保险接口_退保",
                    "trainorder" + trainorderid + " Insureno" + Insureno + "jsonObject" + jsonObject);
            return false;
        }
        catch (Exception e) {
            WriteLog.write("同城保险接口_退保", "Insureno" + Insureno + "" + e.toString() + ":" + e.getMessage());
            return false;

        }

    }

    /**
     * 退保
     * @param policyno
     * @param cancelReason
     * @param r1
     * @return
     */
    public JSONObject cancelOrderAplylist(String policyno, String cancelReason, int r1) {
        //TODO 退保需要修改
        JSONObject jsonObject = new JSONObject();
        long insereid = qorderids(policyno);
        if (0 == insereid) {
            jsonObject.put("success", false);
            jsonObject.put("code", 108);
            jsonObject.put("msg", "退保0接口参数校验错误");
        }
        else {
            Insuruser insuruser = Server.getInstance().getAirService().findInsuruser(insereid);
            //找到订单对应的保险
            try {
                String result = "-1";
                //当保险状态是1:投保成功的话才调用退保或者取消订单的接口
                if (insuruser.getInsurstatus() == 1) {
                    result = getInsrueAtomService(insereid).cancelInsuruser(insuruser);
                    System.out.println(result);
                    WriteLog.write("同城保险接口_退保_boang", r1 + ":" + result);
                }
                if (result.contains("3,")) {
                    jsonObject.put("success", true);
                    jsonObject.put("code", 100);
                    jsonObject.put("msg", "成功");
                    //4退保成功
                    insuruser.setInsurstatus(3);
                    try {
                        Server.getInstance().getAirService().updateInsuruser(insuruser);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        WriteLog.write("同城保险接口_退保", r1 + ":数据库更新异常");
                    }
                }
                else {
                    jsonObject.put("success", false);
                    jsonObject.put("code", 108);
                    jsonObject.put("msg", "退保失败");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("code", 105);
                jsonObject.put("success", false);
                jsonObject.put("msg", "接口处理异常");
            }
        }
        WriteLog.write("同城保险接口_退保", r1 + ":result:" + jsonObject.toString());
        return jsonObject;
    }

    /**
     * 根据投保时间确定新老保险
     * 获取对应的项目地址
     * @return
     * @time 2015年5月25日 下午2:40:35
     * @author fiend
     * @throws Exception 
     */
    public IAtomService getInsrueAtomService(long insureid) throws Exception {
        String str_old_insure_endid = PropertyUtil.getValue("str_old_insure_endid", "Train.properties");
        if (insureid > Long.valueOf(str_old_insure_endid)) {
            WriteLog.write("TongCheng_tuibao", insureid + ":火意险");
            return gethyxInsureAtomService();
        }
        else {
            WriteLog.write("TongCheng_tuibao", insureid + ":航意险");
            return Server.getInstance().getAtomService();
        }
    }

    /**
     * 退票退保更新
     * @param insid
     * @param oldinsid
     * @return
     */
    public boolean uorderisu1(String insid, String oldinsid) {
        if (oldinsid != null && insid != null) {
            String up = "update T_TRAINTICKET set C_REALINSURENO='" + insid + "' where  C_REALINSURENO= '" + oldinsid
                    + "'";
            Server.getInstance().getSystemService().excuteEaccountBySql(up);
            return true;
        }
        return false;
    }

    /**
     * 退保订单查询
     * @param policyno
     * @return
     */
    public long qorderids(String policyno) {
        String sql = "SELECT ID FROM T_INSURUSER with(nolock) WHERE C_POLICYNO='" + policyno + "'";
        try {
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                return Long.valueOf(map.get("ID").toString());
            }
            return 0l;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0l;
        }

    }

    public static void main(String arg[]) {
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(46520);
        Trainticket trainticket = trainorder.getPassengers().get(0).getTraintickets().get(0);
        testcancelinsure(trainorder, trainticket);
        testinsure();
        /**
         * 
         */
    }

    /**
     * 测试取消保险
     * @param trainorder
     * @param trainticket
     * @author fiend 
     */
    public static void testcancelinsure(Trainorder trainorder, Trainticket trainticket) {
        Map<String, Object> mp = new HashMap<String, Object>();
        String refundfee = "0";
        boolean b = false;
        for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket tticket : trainpassenger.getTraintickets()) {
                if (tticket.getId() == trainticket.getId()) {
                    trainticket = tticket;
                }
            }
        }
        System.out.println(trainticket.getRealinsureno());
        if (trainticket.getRealinsureno() != null) {
            try {
                b = TrainInsure.getTongChengTrainInsure().tuibao(Integer.parseInt(trainorder.getId() + ""), "客户退票",
                        trainticket.getRealinsureno());
                System.out.println(b);
                if (b) {
                    WriteLog.write("同城退票", "退票退保成功" + Integer.parseInt(trainorder.getId() + "") + "   Insureno  "
                            + trainticket.getRealinsureno());
                }
                else {
                    WriteLog.write("同城退票", "退票退保失败" + Integer.parseInt(trainorder.getId() + "") + "   Insureno  "
                            + trainticket.getRealinsureno());
                }
            }
            catch (Exception e) {
                WriteLog.write("同城退票", "退票退保失败" + Integer.parseInt(trainorder.getId() + "") + "   Insureno  "
                        + trainticket.getRealinsureno());
            }
        }
        if (trainticket.getTcnewprice() != null && trainticket.getTcnewprice().floatValue() > 0) {
            int n = (int) ((trainticket.getTcnewprice() - trainticket.getProcedure()) * 100);
            if (b) {
                int y = (int) (trainticket.getInsurorigprice() * 100);
                n = n + y;
            }
            refundfee = n + "";
        }
        else {
            int n = (int) ((trainticket.getPrice() - trainticket.getProcedure()) * 100);
            if (b) {
                int y = (int) (trainticket.getInsurorigprice() * 100);
                n = n + y;
            }
            refundfee = n + "";
        }
        mp.put("refund_fee", refundfee);
        mp.put("agree_return", true);
        mp.put("refuse_return_reason", "no");
        mp.put("main_order_id", trainorder.getQunarOrdernumber());//
        mp.put("sub_biz_order_id", trainticket.getInterfaceticketno());
        mp.put("buyerid", trainorder.getTaobaosendid());
        System.out.println(mp.toString());
    }

    /**
     * 测试投保
     * @author fiend
     */
    public static void testinsure() {
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(46481);
        trainorder = TrainInsure.getTongChengTrainInsure().insuranceSynchronous(trainorder);
        Server.getInstance().getTrainService().updateTrainorder(trainorder);
        try {
            Map mp = new HashMap();
            mp.put("main_order_id", trainorder.getQunarOrdernumber());
            mp.put("status", true);
            mp.put("failMsg", "no");
            StringBuffer sb = new StringBuffer();
            int num = 0;
            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                Trainpassenger tp = trainorder.getPassengers().get(i);
                for (int y = 0; y < tp.getTraintickets().size(); y++) {
                    Trainticket tk = tp.getTraintickets().get(y);
                    if (y == 0) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        mp.put("depDate", df.format(df.parse(tk.getDeparttime())));
                    }

                    num = num + 1;
                    // 火车票订单id，单价，坐席，座位号，车次，乘车人姓名，证件名称，证件号码，保单号，保单价格
                    int x = (int) (tk.getPrice() * 100);
                    sb.append(tk.getInterfaceticketno() + ";");
                    sb.append(x + ";");
                    sb.append(TaobaoHotelInterfaceUtil.CackBackSuccessseao(tk.getSeattype(), tk.getSeatno()) + ";");
                    if (tk.getSeatno() == null) {
                        sb.append("no" + ";");
                    }
                    else {

                        sb.append(tk.getSeattype() + "_" + tk.getCoach() + "_" + tk.getSeatno().toString() + ";");
                    }
                    sb.append(tk.getTrainno() + ";");
                    sb.append(tp.getName().toString() + ";");
                    String certTypeValue = tp.getIdtypestr().toString();
                    sb.append(IDnumberType(certTypeValue) + ";");
                    sb.append(tp.getIdnumber() + ";");
                    if (tk.getRealinsureno() == null || "".equals(tk.getRealinsureno())) {
                        sb.append("0;");
                        sb.append("0");
                    }
                    else {
                        sb.append(tk.getRealinsureno() + ";");
                        sb.append((int) (tk.getInsurorigprice() * 100));
                    }
                    if (trainorder.getPassengers().size() > i + 1) {
                        sb.append(",");
                    }
                }
            }
            mp.put("tickets", sb);
            if (trainorder.getExtnumber() == null) {
                mp.put("ticket12306Id", "no");
            }
            else {
                mp.put("ticket12306Id", trainorder.getExtnumber().toString());
            }
            mp.put("ticketNum", num);// 火车票
            System.out.println(mp.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试使用证件类型
     * @param val
     * @return
     * @author fiend
     */
    public static String IDnumberType(String val) {
        if (val.equals("二代身份证")) {
            return "0";
        }
        else if (val.equals("一代身份证")) {
            return "0";
        }
        else if (val.equals("护照")) {
            return "1";
        }
        else if (val.equals("港澳通行证")) {
            return "4";
        }
        else if (val.equals("台湾通行证")) {
            return "5";
        }
        return "-1";
    }
}
