package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.PublicComponent;
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
import com.ccservice.b2b2c.atom.servlet.tuniu.thread.TuNiuCancelChangeCallBackThread;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.OcsMethod;

/**
 * 途牛取消改签接口
 * @time 2015年11月19日 下午5:43:30
 * 朱李旭
 **/
@SuppressWarnings("serial")
public class TuNiuTraintrainAccountChangeCancelServlet extends HttpServlet {
    
    private final String logname = "途牛_取消改签接口";

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    private final PublicComponent PublicComponent = new PublicComponent();

    //当前不提供服务
    private static final String code113 = "113";

    private static final String code999 = "999";

    private static final String datatypeflag = "101";

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String randomStr = UUID.randomUUID().toString();
        try {
            TrainSelectLoginWay TrainSelectLoginWay = new TrainSelectLoginWay();
            TongchengSupplyMethod TongchengSupplyMethod = new TongchengSupplyMethod();
            TuNiuDesUtil TuNiuDesUtil = new TuNiuDesUtil();
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
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(ctx.getRequest().getInputStream(), "UTF-8"));
                String line = "";
                StringBuffer buf = new StringBuffer(1024);
                while ((line = br.readLine()) != null) {
                    buf.append(line);
                }
                String reqString = buf.toString();
                WriteLog.write(logname, randomStr + "--->" + reqString);

                //请求json
                JSONObject reqjso = JSONObject.parseObject(reqString);
                String account = tuNiuServletUtil.getParamByJsonStr("account", reqjso);//账号
                String sign = tuNiuServletUtil.getParamByJsonStr("sign", reqjso);//加密结果
                String timestamp = tuNiuServletUtil.getParamByJsonStr("timestamp", reqjso);//请求时间
                String data = tuNiuServletUtil.getParamByJsonStr("data", reqjso);//加密的请求体
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
                WriteLog.write(logname, randomStr + "--->" + agentid + "--->" + key + "--->" + password);
                if ("".equals(agentid) || "".equals(key) || "".equals(password)) {
                    tuNiuServletUtil.respByUserNotExists(ctx, logname);
                    return;
                }
                String paramStr = data;
                WriteLog.write(logname, randomStr + "--->data:" + paramStr);
                JSONObject jsonString = new JSONObject();
                try {
                    jsonString = JSONObject.parseObject(paramStr);
                }
                catch (Exception e1) {
                    WriteLog.write(logname + "_EXCEPTION", randomStr);
                    ExceptionUtil.writelogByException(logname + "_EXCEPTION", e1);
                    tuNiuServletUtil.respByParamError(ctx, logname);
                    return;
                }
                JSONObject retobj = new JSONObject();
                retobj.put("success", false);
                //请求参数
                String vendorOrderId = jsonString.containsKey("vendorOrderId") ? jsonString.getString("vendorOrderId")
                        : "";//合作伙伴方订单号

                String orderid = jsonString.containsKey("orderId") ? jsonString.getString("orderId") : "";//途牛订单号

                String changeid = jsonString.containsKey("changeId") ? jsonString.getString("changeId") : "";//改签流水号

                String callBackUrl = jsonString.containsKey("callBackUrl") ? jsonString.getString("callBackUrl") : "";//回调地址
                //设置返回
                //参数缺失
                if (ElongHotelInterfaceUtil.StringIsNull(vendorOrderId)
                        || ElongHotelInterfaceUtil.StringIsNull(orderid)
                        || ElongHotelInterfaceUtil.StringIsNull(changeid)
                        || ElongHotelInterfaceUtil.StringIsNull(callBackUrl)) {
                    retobj.put("code", "107");
                    retobj.put("msg", "业务参数缺失");
                    WriteLog.write(logname, randomStr + "--->" + retobj.toJSONString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    return;
                }
                retobj.put("vendorOrderId", vendorOrderId);
                retobj.put("orderid", orderid);
                Trainform trainform = new Trainform();
                trainform.setQunarordernumber(orderid);
                trainform.setOrdernumber(vendorOrderId);
                List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
                //订单不存在
                if (orders == null || orders.size() != 1) {
                    retobj.put("code", "402");
                    retobj.put("msg", "订单不存在");
                    WriteLog.write(logname, randomStr + "--->" + retobj.toJSONString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    return;
                }
                Trainorder order = orders.get(0);
                long orderId = order.getId();
                int status = order.getOrderstatus();
                String extnumber = order.getExtnumber();
                //保存
                TrainSelectLoginWay.saveThirdAccountInfo(order.getId(), jsonString);
                //占座成功，12306订单号不为空
                if (status != Trainorder.ISSUED || ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
                    retobj.put("code", "112");
                    retobj.put("msg", "该订单状态下，不能取消改签");
                    WriteLog.write(logname, randomStr + "--->status:" + status + "--->extnumber:" + extnumber
                            + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    return;
                }
                //订单改签信息
                String changeSql = "select top 1 ID, C_TCSTATUS, C_TCCREATETIME, ISNULL(C_TCNUMBER,'') C_TCNUMBER "
                        + "from T_TRAINORDERCHANGE with(nolock) where C_ORDERID = " + orderId + " order by ID desc";
                List changeList = Server.getInstance().getSystemService().findMapResultBySql(changeSql, null);
                if (changeList == null || changeList.size() == 0) {
                    retobj.put("code", code113);
                    retobj.put("msg", "未找到可以取消的改签车票");
                    WriteLog.write(logname,
                            randomStr + "--->changeList:" + changeList + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 1801, "未找到可以取消的改签车票", orderid,
                            changeid, callBackUrl).start();
                    return;
                }
                //最后一个
                Map changeMap = (Map) changeList.get(0);
                //改签状态
                String tcnumber = changeMap.get("C_TCNUMBER").toString();
                int tcStatus = Integer.parseInt(changeMap.get("C_TCSTATUS").toString());
                if (tcStatus == Trainorderchange.FINISHCHANGE) {
                    retobj.put("code", "1001");
                    retobj.put("msg", "已改签票不能取消");
                    WriteLog.write(logname, randomStr + "--->tcStatus:" + tcStatus + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 1801, "已改签票不能取消", orderid,
                            changeid, callBackUrl).start();
                    return;
                }
                else if (tcStatus == Trainorderchange.CANTCHANGE) {
                    retobj.put("code", code113);
                    retobj.put("msg", "改签票已是取消状态");
                    WriteLog.write(logname, randomStr + "--->tcStatus:" + tcStatus + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 1801, "改签票已是取消状态", orderid,
                            changeid, callBackUrl).start();
                    return;
                }
                else if (tcStatus == Trainorderchange.CHANGEWAITPAY || tcStatus == Trainorderchange.CHANGEPAYING) {
                    retobj.put("code", code113);
                    retobj.put("msg", "正在确认改签，不能进行取消");
                    WriteLog.write(logname, randomStr + "--->tcStatus:" + tcStatus + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 1801, "正在确认改签，不能进行取消", orderid,
                            changeid, callBackUrl).start();
                    return;
                }
                //最后一个预订成功改签为通过状态，判断时间，在改签预订成功之后的30分钟之内发起
                else if (tcStatus == Trainorderchange.THOUGHCHANGE) {
                    //判断超时、同程规定，暂不判断
                    /*
                    boolean timeout = false;
                    try {
                        //创建时间
                        String tcTime = changeMap.get("C_TCCREATETIME").toString();
                        long createTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tcTime).getTime();
                        //时间差
                        long subTime = (System.currentTimeMillis() - createTime) / 1000;//秒
                        //改签预订成功之后的30分钟之内发起
                        if (subTime > 30 * 60) {
                            timeout = true;
                        }
                    }
                    catch (Exception e) {
                    }
                    if (timeout) {
                        retobj.put("code", code999);
                        retobj.put("msg", "请求取消时间已超过规定的时间");
                        return retobj.toString();
                    }
                    */
                }
                else {
                    retobj.put("code", code113);
                    retobj.put("msg", "该状态下，不能取消改签");
                    WriteLog.write(logname, randomStr + "--->tcStatus:" + tcStatus + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 1801, "该状态下，不能取消改签", orderid,
                            changeid, callBackUrl).start();
                    return;
                }
                long changeId = Long.parseLong(changeMap.get("ID").toString());
                //加载其他字段、乘客
                order = Server.getInstance().getTrainService().findTrainorder(order.getId());
                //下单账户
                String createAccount = order.getSupplyaccount();
                if (ElongHotelInterfaceUtil.StringIsNull(createAccount) && order.getOrdertype() != 3
                        && order.getOrdertype() != 4) {
                    retobj.put("code", code999);
                    retobj.put("msg", "取消改签失败");
                    WriteLog.write(logname, randomStr + "--->createAccount:" + createAccount
                            + "--->order.getOrdertype():" + order.getOrdertype() + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 1801, "取消改签失败", orderid, changeid,
                            callBackUrl).start();
                    return;
                }
                TrainSelectLoginWay.saveThirdAccountInfo(orderId, jsonString);
                Customeruser user = TongchengSupplyMethod.getCustomeruserBy12306Account(order, 0, true);
                if (user == null || user.isDontRetryLogin()
                        || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
                    //账号系统，以未登录释放账号
                    if (user != null && user.isFromAccountSystem()) {
                        TongchengSupplyMethod.freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree,
                                AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                    }
                    retobj.put("code", code999);
                    retobj.put("msg", user != null && user.isDontRetryLogin() ? user.getNationality() : "取消改签失败");
                    WriteLog.write(logname, randomStr + "--->user:" + user + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 1801, "取消改签失败", orderid, changeid,
                            callBackUrl).start();
                    return;
                }
                boolean cancelTrue = false;
                boolean accountNoLogin = false;//账号未登录
                //URL、COOKIE
                String url = "";
                String cookie = user.getCardnunber();
                //调用接口，向12306取消
                String result = "";
                try {
                    RepServerBean rep = RepServerUtil.getRepServer(user, false);
                    url = rep.getUrl();
                    String param = "datatypeflag=10&cookie=" + cookie + "&extnumber=" + extnumber + "&trainorderid="
                            + order.getId() + TongchengSupplyMethod.JoinCommonAccountInfo(user, new RepServerBean());
                    result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
                    if (result.contains("取消订单成功") || "无未支付订单".equals(result)) {
                        cancelTrue = true;
                    }
                    else if (result.contains("用户未登录")) {
                        accountNoLogin = true;
                    }
                }
                catch (Exception e) {
                }
                WriteLog.write(logname, randomStr + "--->order.getId():" + order.getId() + "--->>REP服务器地址:" + url
                        + ">>>>>REP返回>>>>>" + result);
                //取消失败
                if (!cancelTrue) {
                    if (user.isFromAccountSystem()) {
                        TongchengSupplyMethod.freeCustomeruser(user, accountNoLogin ? AccountSystem.FreeNoLogin
                                : AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                                AccountSystem.NullDepartTime);
                    }
                    retobj.put("code", code999);
                    retobj.put("msg", "取消改签失败");
                    WriteLog.write(logname,
                            randomStr + "--->cancelTrue:" + cancelTrue + "--->retobj:" + retobj.toString());
                    tuNiuServletUtil.respByUnknownError(ctx, logname);
                    new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 1801, "取消改签失败", orderid, changeid,
                            callBackUrl).start();

                    return;
                }
                try {
                    //释放账号
                    TongchengSupplyMethod.freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.TwoFree,
                            AccountSystem.OneCancel, AccountSystem.NullDepartTime);
                    //本地取消
                    String updateSql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = 0, C_TCSTATUS = "
                            + Trainorderchange.CANTCHANGE + ", C_STATUS12306 = " + Trainorderchange.ORDERFALSE
                            + " where C_ORDERID = " + orderId + " and C_TCSTATUS = " + Trainorderchange.THOUGHCHANGE
                            + "; update T_TRAINTICKET set C_STATUS = " + Trainticket.ISSUED + ", C_STATE12306 = "
                            + Trainticket.ORDEREDPAYED + " where C_CHANGEID = " + changeId + " and C_STATUS = "
                            + Trainticket.THOUGHCHANGE;
                    Server.getInstance().getSystemService().findMapResultBySql(updateSql, null);
                    //变更到站
                    String changeFlag = tcnumber.startsWith("TS") ? "变更到站" : "改签";
                    //日志内容
                    String content = "[取消 - " + changeId + "]接口申请取消" + changeFlag
                            + "，12306取消<span style='color:red;'>成功</span>，还原车票状态。";
                    //保存日志
                    new TongchengSupplyMethod().createtrainorderrc(1, content, orderId, 0l, Trainticket.CANTCHANGE,
                            "系统接口");
                }
                catch (Exception e) {
                }
                try {
                    //KEY
                    String key1 = "TrainChange=" + user.getLoginname();
                    //移除
                    OcsMethod.getInstance().remove(key1);
                }
                catch (Exception e) {
                }
                JSONObject json = new JSONObject();
                json.put("vendorOrderId", vendorOrderId);
                WriteLog.write(logname, randomStr + "--->json:" + json.toString());
                tuNiuServletUtil.respBySuccess(ctx, logname, json);
                new TuNiuCancelChangeCallBackThread(account, vendorOrderId, key, 231000, "取消改签成功", orderid, changeid,
                        callBackUrl).start();
            }
            catch (Exception e) {
                e.printStackTrace();
                tuNiuServletUtil.respByUnknownError(ctx, logname);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
