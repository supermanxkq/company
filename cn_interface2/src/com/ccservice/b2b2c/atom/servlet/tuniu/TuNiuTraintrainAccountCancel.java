package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.thread.MyThreadTuNiuTrainAccountCancelCallback;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 途牛取消订单接口
 * @time 2014年12月11日 上午11:54:58
 * @author 朱李旭
 */

@SuppressWarnings("serial")
public class TuNiuTraintrainAccountCancel extends HttpServlet {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private final String logname = "tuniu_3_4_3_取消订单接口";

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int random = (int) (Math.random() * 1000000);
        TongchengSupplyMethod TongchengSupplyMethod = new TongchengSupplyMethod();
        TrainSelectLoginWay TrainSelectLoginWay = new TrainSelectLoginWay();
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        final AsyncContext ctx = req.startAsync();
        ctx.setTimeout(50000L);
        //监听
        ctx.addListener(new AsyncListener() {
            public void onTimeout(AsyncEvent event) throws IOException {
                tuNiuServletUtil.respByUnknownError(ctx, logname);
            }

            public void onError(AsyncEvent event) throws IOException {
                tuNiuServletUtil.respByUnknownError(ctx, logname);
            }

            public void onComplete(AsyncEvent event) throws IOException {
            }

            public void onStartAsync(AsyncEvent event) throws IOException {

            }
        });
        BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getRequest().getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String reqString = buf.toString();
        WriteLog.write(logname, random + "--->reqString" + reqString);

        try {
            if (reqString == null || "".equals(reqString)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //请求json
            JSONObject reqjso = JSONObject.parseObject(reqString);
            String account = tuNiuServletUtil.getParamByJsonStr("account", reqjso);//账号
            String sign = tuNiuServletUtil.getParamByJsonStr("sign", reqjso);//加密结果
            String timestamp = tuNiuServletUtil.getParamByJsonStr("timestamp", reqjso);//请求时间
            String data = tuNiuServletUtil.getParamByJsonStr("data", reqjso);//加密的请求体
            WriteLog.write(logname, random + "--->account:" + account + "--->sign:" + sign + "--->timestamp:"
                    + timestamp + "--->data:" + data);
            if ("".equals(account) || "".equals(sign) || "".equals(timestamp) || "".equals(data)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //获取账户信息
            Map map = tuNiuServletUtil.getInterfaceAccount(account);
            String agentid = tuNiuServletUtil.getParamByMapStr("C_AGENTID", map);
            String key = tuNiuServletUtil.getParamByMapStr("C_KEY", map);
            String password = tuNiuServletUtil.getParamByMapStr("C_ARG2", map);
            String interfacetype = tuNiuServletUtil.getParamByMapStr("C_INTERFACETYPE", map);
            WriteLog.write(logname, random + "--->agentid:" + agentid + "--->key:" + key + "--->password:" + password
                    + "--->interfacetype:" + interfacetype);
            if ("".equals(agentid) || "".equals(key) || "".equals(password)) {
                tuNiuServletUtil.respByUserNotExists(ctx, logname);
                return;
            }
            JSONObject object = JSONObject.parseObject(reqString);
            object.put("sign", "");
            //获取key加密
            String localsign = SignUtil.generateSign(object.toString(), key);
            WriteLog.write(logname, random + "--->localsign:" + localsign + "--->sign:" + sign);
            if (!sign.equalsIgnoreCase(localsign)) {
                tuNiuServletUtil.respBySignatureError(ctx, logname);
                return;
            }
            String paramStr = TuNiuDesUtil.decrypt(data);
            WriteLog.write(logname, random + "--->data:" + paramStr);
            JSONObject jsonString = new JSONObject();
            try {
                jsonString = JSONObject.parseObject(paramStr);
            }
            catch (Exception e1) {
                ExceptionUtil.writelogByException(logname + "_Exception", e1);
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }

            JSONObject retobj = new JSONObject();
            //途牛订单号
            String orderid = jsonString.containsKey("orderId") ? jsonString.getString("orderId") : "";
            //交易单号
            String vendororderid = jsonString.containsKey("vendorOrderId") ? jsonString.getString("vendorOrderId") : "";//合作伙伴方订单号
            String callBackUrl = jsonString.containsKey("callBackUrl") ? jsonString.getString("callBackUrl") : "";//回调地址
            //存在空值
            if (ElongHotelInterfaceUtil.StringIsNull(orderid) || ElongHotelInterfaceUtil.StringIsNull(vendororderid)
                    || ElongHotelInterfaceUtil.StringIsNull(callBackUrl)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            Trainform trainform = new Trainform();
            trainform.setQunarordernumber(orderid);
            trainform.setOrdernumber(vendororderid);
            List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
            WriteLog.write(logname, random + "--->orders:" + orders.size());
            Trainorder order = new Trainorder();
            //订单不存在
            if (orders == null || orders.size() == 0) {
                WriteLog.write(logname, random + "--->订单不存在");
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            else if (orders.size() > 0) {
                order = orders.get(0);
            }
            if (order.getId() == 0) {
                WriteLog.write(logname, random + "--->订单不存在");
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            boolean success = false;
            String msg = "";
            String code = "";
            //订单状态
            int status = order.getOrderstatus();
            int state12306 = order.getState12306();
            boolean cancelTrue = false;//是否在12306成功取消
            boolean accountNoLogin = false;//账号未登录
            WriteLog.write(logname, random + "--->status:" + status + "--->state12306:" + state12306);
            if ((status > 1 && status < 8) || (state12306 >= 5 && state12306 <= 8)) {
                if (status == 3) {
                    try {
                        new MyThreadTuNiuTrainAccountCancelCallback(account, vendororderid, key, 130000, "订单已出票，不能取消")
                                .start();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    success = true;
                    code = "130000";
                    msg = "";
                }
            }
            else if (status == 1) {
                Map map1 = TongchengSupplyMethod.getTrainorderstatus(order.getId());
                //12306订单号为空
                //        String extnumber = order.getExtnumber();
                String extnumber = TongchengSupplyMethod.gettrainorderinfodatabyMapkey(map1, "C_EXTNUMBER");
                String ordertype = TongchengSupplyMethod.gettrainorderinfodatabyMapkey(map1, "ordertype");
                //下单账户
                //            String createAccount = order.getSupplyaccount();
                String createAccount = TongchengSupplyMethod.gettrainorderinfodatabyMapkey(map1, "C_SUPPLYACCOUNT");
                //日志
                WriteLog.write(logname, random + "--->extnumber:" + extnumber + "--->createAccount:" + createAccount
                        + "--->ordertype:" + ordertype);
                //账户存在、电子单号存在
                if ((!ElongHotelInterfaceUtil.StringIsNull(createAccount) || "3".equals(ordertype) || "4"
                        .equals(ordertype)) && !ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
                    //保存
                    TrainSelectLoginWay.saveThirdAccountInfo(order.getId(), jsonString);
                    //查询账户
                    order.setSupplyaccount(createAccount);
                    if ("3".equals(ordertype) || "4".equals(ordertype)) {
                        order.setOrdertype(Integer.valueOf(ordertype));
                    }
                    Customeruser user = TongchengSupplyMethod.getCustomeruserBy12306Account(order, random, true);
                    //                    账号名不存在、密码错误等，不登录重试，针对第三方传账号和密码，防止重试锁账号等
                    if (user != null && user.isDontRetryLogin()) {
                        if (status == 3) {
                            try {
                                new MyThreadTuNiuTrainAccountCancelCallback(account, vendororderid, key, 130001, "取消失败")
                                        .start();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            success = true;
                            code = "130001";
                            msg = "";
                        }
                    }
                    else {
                        //记录日志
                        WriteLog.write(logname, random + "--->Cookie:" + (user == null ? "" : user.getCardnunber()));
                        //未获取到账号或Cookie为空
                        if (user == null || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
                            //账号系统，以未登录释放账号
                            if (user != null && user.isFromAccountSystem()) {
                                TongchengSupplyMethod.freeCustomeruser(user, AccountSystem.FreeNoLogin,
                                        AccountSystem.OneFree, AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                            }
                            if (status == 3) {
                                try {
                                    new MyThreadTuNiuTrainAccountCancelCallback(account, vendororderid, key, 130001,
                                            "取消失败").start();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                success = true;
                                code = "130001";
                                msg = "";
                            }
                        }
                        else {
                            //请求12306
                            String url = "";
                            String result = "";
                            try {
                                url = RepServerUtil.getRepServer(user, false).getUrl();
                                String param = "datatypeflag=10&cookie=" + user.getCardnunber() + "&extnumber="
                                        + extnumber + "&trainorderid=" + order.getId()
                                        + TongchengSupplyMethod.JoinCommonAccountInfo(user, new RepServerBean());
                                WriteLog.write(logname, random + "--->url:" + url + "--->param:" + param);
                                result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
                                WriteLog.write(logname, random + "--->result:" + result);
                                if (result.contains("取消订单成功") || "无未支付订单".equals(result)) {
                                    cancelTrue = true;
                                }
                                else if (result.contains("用户未登录")) {
                                    accountNoLogin = true;
                                }
                            }
                            catch (Exception e) {
                                result += ">>>>>Exception>>>>>" + e.getMessage();
                            }
                            WriteLog.write(logname, random + ">>>>>" + order.getId() + ">>>>>REP服务器地址>>>>>" + url
                                    + ">>>>>REP返回>>>>>" + result + "cancelTrue" + cancelTrue);
                            //释放账号
                            if (cancelTrue) {
                                try {
                                    new MyThreadTuNiuTrainAccountCancelCallback(account, vendororderid, key, 130002,
                                            "取消成功").start();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                success = true;
                                code = "130002";
                                msg = "";
                                TongchengSupplyMethod.freeCustomeruser(user, AccountSystem.FreeNoCare,
                                        AccountSystem.TwoFree, AccountSystem.OneCancel, AccountSystem.NullDepartTime);
                                try {
                                    //本地取消
                                    String sql1 = "update T_TRAINORDER set C_SUPPLYPRICE=0,C_SUPPLYPAYWAY=100,C_SUPPLYTRADENO=''"
                                            + ",C_EXTNUMBER='',C_CHANGESUPPLYTRADENO='',C_ORDERSTATUS="
                                            + Trainorder.CANCLED
                                            + ",C_STATE12306="
                                            + Trainorder.ORDERFALSE
                                            + " WHERE ID=" + order.getId();
                                    WriteLog.write(logname, random + "--->sql1" + sql1);
                                    try {
                                        int i1 = Server.getInstance().getSystemService().excuteGiftBySql(sql1);
                                        WriteLog.write(logname, random + "--->修改数据库:" + i1);
                                    }
                                    catch (Exception e) {
                                        ExceptionUtil.writelogByException(logname + "_Exception", e, random + "");
                                    }
                                    //日志
                                    Trainorderrc rz = new Trainorderrc();
                                    rz.setYwtype(1);
                                    rz.setCreateuser("系统接口");
                                    rz.setOrderid(order.getId());
                                    rz.setStatus(Trainorder.CANCLED);
                                    rz.setContent("接口申请取消订单,12306取消<span style='color:red;'>成功</span>,交易关闭。");
                                    Server.getInstance().getTrainService().createTrainorderrc(rz);
                                    //文件日志
                                    WriteLog.write(logname, random + "--->订单号:" + order.getId() + ":取消订单,消除订单支付信息.");
                                }
                                catch (Exception e) {
                                    ExceptionUtil.writelogByException(logname + "_Exception", e, random + "");
                                }
                            }
                            else {
                                WriteLog.write(logname, random + "--->user" + user.isFromAccountSystem());
                                if (user.isFromAccountSystem()) {
                                    TongchengSupplyMethod.freeCustomeruser(user,
                                            accountNoLogin ? AccountSystem.FreeNoLogin : AccountSystem.FreeNoCare,
                                            AccountSystem.OneFree, AccountSystem.ZeroCancel,
                                            AccountSystem.NullDepartTime);
                                }
                                try {
                                    new MyThreadTuNiuTrainAccountCancelCallback(account, vendororderid, key, 130001,
                                            "取消失败").start();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                success = true;
                                code = "130001";
                                msg = "";
                            }
                        }
                    }
                }
                else {
                    try {
                        new MyThreadTuNiuTrainAccountCancelCallback(account, vendororderid, key, 130001, "取消失败")
                                .start();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    success = true;
                    code = "231000";
                    msg = "";
                }
            }
            else {
                try {
                    new MyThreadTuNiuTrainAccountCancelCallback(account, vendororderid, key, 130002, "取消成功").start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                success = true;
                code = "231000";
                msg = "";
            }
            JSONObject datajJsonObject = new JSONObject();
            datajJsonObject.put("vendorOrderId", vendororderid);
            //返回
            tuNiuServletUtil.respBySuccess(ctx, logname, datajJsonObject);
            return;

        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logname + "_Exception", e, random + "");
            tuNiuServletUtil.respByUnknownError(ctx, logname);
        }
    }

}