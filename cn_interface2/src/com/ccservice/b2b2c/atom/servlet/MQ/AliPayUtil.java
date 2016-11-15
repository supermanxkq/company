package com.ccservice.b2b2c.atom.servlet.MQ;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * 支付宝同步支付
 * 原线上改签使用，现统一走支付池，已弃用
 */
public class AliPayUtil extends TongchengSupplyMethod {
    /**
     * 说明:获取支付支付链接
     * @param isjointrip 是否为联程
     * @time 2014年8月30日 上午11:20:49
     */
    public boolean orderpayment(Customeruser user, Trainorder trainorder, Trainorderchange trainOrderChange, int random) {
        if (user == null) {
            WriteLog.write("12306支付宝GQ自动支付链接", "未获取到用户");
            return false;
        }
        Customeruser old = user;
        boolean resultmsg = false;
        String loginname = user.getLoginname();
        String tcnumber = trainOrderChange.getTcnumber();
        String extnumber = trainorder.getExtnumber();//12306订单号
        int isjointrip = trainorder.getIsjointtrip() == null ? 0 : trainorder.getIsjointtrip();//是否为联程
        try {
            if (isjointrip == 0) {
                String result = "";
                long t1 = System.currentTimeMillis();
                for (int i = 0; i < 5; i++) {
                    //REP地址
                    RepServerBean rep = RepServerUtil.getRepServer(user, false);
                    //请求参数
                    String par = "datatypeflag=9&payurlflag=2&cookie=" + user.getCardnunber() + "&extnumber="
                            + extnumber + "&trainorderid=" + trainorder.getId() + JoinCommonAccountInfo(user, rep);
                    //请求REP
                    result = SendPostandGet.submitPost(rep.getUrl(), par, "UTF-8").toString();
                    //记录日志
                    WriteLog.write("12306支付宝GQ自动支付链接", random + ":循环次数：" + i + ":" + tcnumber + "支付宝返回数据:" + result);
                    //用户未登录
                    if ("用户未登录".equals(result)) {
                        //账号系统
                        if (user.isFromAccountSystem()) {
                            freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree,
                                    AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                        }
                        //重新获取
                        user = getCustomeruserBy12306Account(trainorder, random, true);
                        //未获取到账号
                        if (user == null) {
                            user = old;
                            user.setState(0);
                        }
                        //登陆成功
                        if (user.getState() == 1) {
                            par = "datatypeflag=9&payurlflag=2&cookie=" + user.getCardnunber() + "&extnumber="
                                    + extnumber + "&trainorderid=" + trainorder.getId()
                                    + JoinCommonAccountInfo(user, rep);
                            WriteLog.write("12306支付宝GQ自动支付链接", random + ":" + tcnumber + "支付宝访问地址:" + rep.getUrl()
                                    + "?" + par);
                            try {
                                result = SendPostandGet.submitPost(rep.getUrl(), par, "UTF-8").toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            WriteLog.write("12306支付宝GQ自动支付链接", random + ":" + "2次" + tcnumber + "支付宝返回数据:" + result);
                        }
                        else {
                            WriteLog.write(
                                    "12306支付宝GQ自动支付链接",
                                    random + ":" + tcnumber + ",登录失败，name:" + user.getLoginname() + ",pwd:"
                                            + user.getLogpassword());
                        }
                    }
                    if (result.indexOf("gateway.do") >= 0) {
                        break;
                    }
                }
                long t2 = System.currentTimeMillis();
                WriteLog.write("12306支付宝GQ自动支付链接", random + ":获取支付链接用时:" + (t2 - t1));
                if (result.indexOf("gateway.do") >= 0) {
                    String supplytradeno = result.split("ord_id_ext=")[1].split("&ord_name")[0];
                    float supplyprice = Float.valueOf(result.split("ord_amt=")[1].split("&ord_cur")[0]);//支付金额
                    //价格不一致
                    if (supplyprice != trainOrderChange.getTcprice()) {
                        return false;
                    }
                    trainOrderChange.setPayaddress(result);
                    trainOrderChange.setSupplyprice(supplyprice);
                    trainOrderChange.setSupplytradeno(supplytradeno);
                    String sql = "update T_TRAINORDERCHANGE set C_SUPPLYPRICE = " + supplyprice
                            + ", C_SUPPLYTRADENO = '" + supplytradeno + "', C_PAYADDRESS = '" + result
                            + "' where ID = " + trainOrderChange.getId();
                    Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
                    WriteLog.write("12306支付宝GQ自动支付链接", random + ":" + "存储支付信息正确" + tcnumber + "," + loginname + ","
                            + extnumber);
                    resultmsg = true;
                }
            }
            else if (isjointrip == 2) {//联程暂时不处理
                WriteLog.write("12306支付宝GQ自动支付链接", random + ":" + tcnumber + "联程订单不处理");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("12306支付宝GQ自动支付链接", random + ":" + "存储支付信息错误:" + e);
        }
        return resultmsg;
    }

    /**
     *交通获取交易金额 
     * @param args
     * @time 2014年12月4日 下午8:01:13
     * @author wzc
     */
    public static String searWnumber(String params) {
        String retrunval = "";
        try {
            Pattern p = Pattern.compile("ord_id_ext=W\\d*");
            Matcher m = p.matcher(params);
            retrunval = "";
            if (m.find()) {
                String count = m.group();
                retrunval = count.replace("ord_id_ext=", "");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if ("".equals(retrunval)) {
            retrunval = searWnumberTradeNum(params);
        }
        return retrunval;
    }

    /**
     *交通获取交易金额 
     * @param args
     * @time 2014年12月4日 下午8:01:13
     * @author wzc
     */
    public static String searWnumberTradeNum(String params) {
        String retrunval = "";
        try {
            String[] param = params.split("&");
            for (String str : param) {
                if (str.contains("out_trade_no")) {
                    retrunval = str.replaceAll("out_trade_no=", "");
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return retrunval;
    }

    /**
     * 支付方法
     * @param trainorder 订单
     * @param t 标识
     * @param times 支付次数
     * @return flag 0 支付失败  1 支付成功  2不确定是否支付成功
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
    public int autoalipayPay(Trainorder trainorder, int t, int times, Trainorderchange trainOrderChange) {
        int flag = 0;
        int isquestionstat = trainOrderChange.getIsQuestionChange() == null ? 0 : trainOrderChange
                .getIsQuestionChange();
        if (isquestionstat > 0) {
            WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":改签订单号" + trainOrderChange.getTcnumber()
                    + "问题订单无法支付,问题状态：" + isquestionstat);
            return flag;
        }
        String alipayurl = "-1";
        String[] strs = getSysconfigStringbydb("alipayurlnum").split(",");
        String i_url = strs[new Random().nextInt(strs.length)];
        if (trainorder.getAgentid() == Long.valueOf(getSysconfigString("TaobaoAgentID")).longValue()) {
            i_url = getSysconfigString("Taobaopayurl");
        }
        //支付宝支付
        if (trainOrderChange.getPayflag() != null && trainOrderChange.getPayflag().contains("alipayurl")) {
            alipayurl = getSysconfigString(trainOrderChange.getPayflag());
            i_url = trainOrderChange.getPayflag().replaceAll("alipayurl", "");
            WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":二次支付：" + alipayurl + ",支付索引：" + i_url
                    + ",支付账户：" + trainOrderChange.getPayflag());
        }
        else {
            alipayurl = getSysconfigString("alipayurl" + i_url);
        }
        WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":支付：" + alipayurl + ",支付索引：" + i_url + ",支付账户："
                + trainOrderChange.getPayflag());
        String payurl = trainOrderChange.getPayaddress();
        if (!ElongHotelInterfaceUtil.StringIsNull(payurl) && !ElongHotelInterfaceUtil.StringIsNull(alipayurl)
                && !"-1".equals(alipayurl)) {
            String wnumber = "";
            if (payurl.contains("gateway.do")) {
                wnumber = searWnumber(payurl);
            }
            JSONObject jso = new JSONObject();
            jso.put("username", "trainone");
            jso.put("sign", "FF1E07242CE2C86A");
            jso.put("postdata", payurl);
            jso.put("paymode", "1");
            jso.put("servid", "1");
            jso.put("ordernum", trainOrderChange.getTcnumber());
            jso.put("paytype", "1");
            jso.put("WNumber", wnumber);
            int paycount = 0;
            try {
                paycount = Integer.valueOf(getSysconfigString("paycount"));//支付次数
                if (times > paycount) {
                    changeQuestionOrder(trainOrderChange, "[支付 - " + trainOrderChange.getId() + "]改签订单多次支付失败");
                    return flag;
                }
                if (times > 0) {
                    jso.put("RePay", "true");
                    WriteLog.write("12306_GQTrainorderPayMessageListener_MQ",
                            t + ":" + times + "次支付:" + jso.getString("RePay"));
                }
            }
            catch (Exception e) {
            }
            trainRC(trainorder.getId(), "[确认 - " + trainOrderChange.getId() + "]开始支付改签订单");
            trainOrderChange.setPayflag("alipayurl" + i_url);
            String supplyaccount = trainOrderChange.getPayflag();
            //更新
            String payFlagSql = "update T_TRAINORDERCHANGE set C_PAYFLAG = '" + supplyaccount + "' where ID = "
                    + trainOrderChange.getId();
            Server.getInstance().getSystemService().excuteAdvertisementBySql(payFlagSql);
            //日志
            WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":订单号：" + trainorder.getOrdernumber()
                    + ",请求数据：" + jso.toString() + ",支付hou支付账号：" + supplyaccount);
            Map listdata = new HashMap();
            listdata.put("data", jso.toString());
            String result = HttpsClientUtils.posthttpclientdata(alipayurl, listdata, 70000l);
            WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":70t返回结果：" + result);
            if (result == null || "".equals(result) || !(result.contains("status"))) {
                changeQuestionOrder(trainOrderChange, "[支付 - " + trainOrderChange.getId() + "]支付改签订单返回数据异常");
                flag = 2;
                return flag;
            }
            if (result.contains("已经存在")) {
                String par = "{'ordernum':'" + trainOrderChange.getTcnumber()
                        + "','cmd':'seachliushuihao','paytype':'1'}";
                Map listdatatt = new HashMap();
                listdatatt.put("data", par);
                result = HttpsClientUtils.posthttpclientdata(alipayurl, listdatatt, 60000l);
                WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ",订单已存在，查询订单结果：" + result);
            }
            if (result != null && result.contains("status")) {
                JSONObject obj = JSONObject.parseObject(result);
                boolean status = obj.getBoolean("status");
                if (status) {
                    String liushuihao = obj.getString("AliTradeNo");
                    String state = "";
                    if (obj.containsKey("orderstatus")) {
                        state = obj.getString("orderstatus");
                    }
                    //查询失败成功信息
                    String msg = "";
                    if (obj.containsKey("info")) {
                        msg = obj.getString("info");
                    }
                    String AliPayUserName = "";
                    if (obj.containsKey("AliPayUserName")) {
                        AliPayUserName = obj.getString("AliPayUserName");//当前支付宝账号
                    }
                    float balance = -1;
                    String Banlancestr = "";
                    if (obj.containsKey("Banlance")) {
                        Banlancestr = obj.getString("Banlance");
                        if (Banlancestr != null && !"".equals(Banlancestr) && !"null".equals(Banlancestr)
                                && "支付成功".equals(state)) {
                            balance = Float.valueOf(Banlancestr);//当前账户余额
                        }
                    }
                    //解析银联参数成功  已经有交易号  必须返回RunOK才能确认已经支付
                    if (liushuihao != null && !"".equals(liushuihao) && liushuihao.length() > 0 && "支付成功".equals(state)) {
                        //payclock(AliPayUserName, balance, t);
                        String sqlpaytime = "";
                        WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":" + sqlpaytime + ":"
                                + liushuihao);
                        trainOrderChange.setSupplytradeno(liushuihao);
                        trainOrderChange.setSupplypaymethod(Paymentmethod.ALIPAY);
                        trainOrderChange.setPayaccount(AliPayUserName);//使用的银行卡号
                        trainRC(trainorder.getId(), "[确认 - " + trainOrderChange.getId() + "]支付改签订单成功");
                        //更新
                        String paySuccessSql = "update T_TRAINORDERCHANGE set C_SUPPLYTRADENO = '"
                                + trainOrderChange.getSupplytradeno() + "', C_PAYACCOUNT = '"
                                + trainOrderChange.getPayaccount() + "', C_SUPPLYPAYMETHOD = "
                                + trainOrderChange.getSupplypaymethod() + " where ID = " + trainOrderChange.getId();
                        Server.getInstance().getSystemService().excuteAdvertisementBySql(paySuccessSql);
                        //日志
                        WriteLog.write("12306_GQTrainorderPayMessageListener_MQ",
                                t + ":" + trainOrderChange.getTcnumber() + ":" + msg);
                        WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":更新结束");
                        flag = 1;
                    }
                    else if ("支付失败".equals(state) && getpaycontrol(msg)) {//运行错误
                        WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":更新结束");
                        return flag;
                    }
                    else {
                        WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":该状态下不用处理。");
                    }
                }
                else {
                    WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":未支付成功，重新发送队列支付");
                    return flag;
                }
            }
        }
        else {
            WriteLog.write("12306_GQTrainorderPayMessageListener_MQ", t + ":" + trainOrderChange.getTcnumber()
                    + ":发送支付链接队列。");
        }
        return flag;
    }

    /**
     * 根据返回信息是否确定支付失败
     * @param info
     * @return
     */
    public boolean getpaycontrol(String info) {
        if (info != null && !"".equals(info)) {
            if ("支付失败:CASHIER_ACCESS_GAP_CONTROL_TIP".equals(info)) {
                return false;
            }
            //            else if (info.contains("登录失效")) {
            //                return false;
            //            }
        }
        return true;
    }

    public void trainRC(long trainorderid, String msg) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(trainorderid);
        rc.setContent(msg);
        rc.setCreateuser("自动支付");
        rc.setYwtype(1);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    /**
     * 变成问题订单
     */
    public void changeQuestionOrder(Trainorderchange orderchange, String msg) {
        //日志
        try {
            trainRC(orderchange.getOrderid(), msg);
        }
        catch (Exception e) {
        }
        //支付问题
        try {
            int question = Trainorderchange.PAYINGQUESTION;
            String sql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = " + question + " where ID = "
                    + orderchange.getId();
            Server.getInstance().getSystemService().excuteGiftBySql(sql);
        }
        catch (Exception e) {
        }
    }

    /**
     * 根据sysconfig的name获得value
     * 实时 如果是判断的必须调用实时接口
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getSysconfigStringbydb(String name) {
        String result = "-1";
        List<Sysconfig> sysoconfigs = Server.getInstance().getSystemService()
                .findAllSysconfig("WHERE C_NAME='" + name + "'", "", -1, 0);
        if (sysoconfigs.size() > 0) {
            result = sysoconfigs.get(0).getValue();
        }
        return result;
    }

}
