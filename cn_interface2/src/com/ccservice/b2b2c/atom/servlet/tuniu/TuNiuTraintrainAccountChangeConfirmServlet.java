package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
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
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 途牛确认改签接口
 * @time 2015年11月19日 下午5:43:30
 * 朱李旭
 **/
@SuppressWarnings("serial")
public class TuNiuTraintrainAccountChangeConfirmServlet extends HttpServlet {
    private final String logname = "途牛_确认改签接口";

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    TrainSelectLoginWay TrainSelectLoginWay = new TrainSelectLoginWay();

    TongchengSupplyMethod TongchengSupplyMethod = new TongchengSupplyMethod();

    //当前不提供服务
    private static final String code113 = "113";

    private static final String code999 = "999";

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
        TuNiuDesUtil TuNiuDesUtil = new TuNiuDesUtil();
        String randomStr = UUID.randomUUID().toString();
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
        WriteLog.write(logname, reqString);

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

        try {
            //获取账户信息
            Map map = tuNiuServletUtil.getInterfaceAccount(account);
            String agentid = tuNiuServletUtil.getParamByMapStr("C_AGENTID", map);
            String key = tuNiuServletUtil.getParamByMapStr("C_KEY", map);
            String password = tuNiuServletUtil.getParamByMapStr("C_ARG2", map);
            String interfacetype = tuNiuServletUtil.getParamByMapStr("C_INTERFACETYPE", map);
            if ("".equals(agentid) || "".equals(key) || "".equals(password)) {
                tuNiuServletUtil.respByUserNotExists(ctx, logname);
                return;
            }
            JSONObject object = JSONObject.parseObject(reqString);
            object.remove("sign");
            //获取key加密
            String localsign = SignUtil.generateSign(object.toString(), key);
            WriteLog.write(logname, randomStr + "--->" + object.toString() + "--->key:" + key + "--->localsign:"
                    + localsign);
            if (!sign.equalsIgnoreCase(localsign)) {
                WriteLog.write(logname + "_ERROR", randomStr + "--->" + "key:" + key + "--->sign:" + sign
                        + ";localsign:" + localsign);
                tuNiuServletUtil.respBySignatureError(ctx, logname);
                return;
            }
            String paramStr = TuNiuDesUtil.decrypt(data);
            WriteLog.write(logname, randomStr + "--->" + "paramStr:" + paramStr);
            JSONObject jsonString = new JSONObject();
            try {
                jsonString = JSONObject.parseObject(paramStr);
            }
            catch (Exception e1) {
                ExceptionUtil.writelogByException(logname + "_Exception", e1);
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }

            int random = 0;
            JSONObject retobj = new JSONObject();
            retobj.put("success", false);

            String vendorOrderId = jsonString.containsKey("vendorOrderId") ? jsonString.getString("vendorOrderId") : "";//合作伙伴方订单号
            String changeid = jsonString.containsKey("changeId") ? jsonString.getString("changeId") : "";//改签流水订单号
            String orderid = jsonString.containsKey("orderId") ? jsonString.getString("orderId") : "";//途牛订单号
            String callbackurl = jsonString.containsKey("callBackUrl") ? jsonString.getString("callBackUrl") : "";
            WriteLog.write(logname, randomStr + "--->" + vendorOrderId + "--->" + changeid + "--->" + orderid + "--->"
                    + callbackurl);
            if (ElongHotelInterfaceUtil.StringIsNull(vendorOrderId) || ElongHotelInterfaceUtil.StringIsNull(changeid)
                    || ElongHotelInterfaceUtil.StringIsNull(orderid)
                    || ElongHotelInterfaceUtil.StringIsNull(callbackurl)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //            }
            //不再支持同步
            //            else {
            //                retobj.put("code", "108");
            //                retobj.put("msg", "错误的业务参数，请走异步");
            //                retobj.toString();
            //            }
            //查询订单
            Trainform trainform = new Trainform();
            //            trainform.setQunarordernumber(orderid);
            trainform.setOrdernumber(vendorOrderId);
            List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
            //订单不存在
            if (orders == null || orders.size() != 1) {
                retobj.put("code", "402");
                retobj.put("msg", "订单不存在");
                WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
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
                retobj.put("msg", "该订单状态下，不能确认改签");
                WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
            change(orderId, order, random, callbackurl, orderid, changeid, randomStr, ctx);

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tuNiuServletUtil.respByUnknownError(ctx, logname);
        }
    }

    /**
    * 确认改签
    * @param changeType 改签类型，0：改签；1：改签退
    * @param changeid taobao改签单会传changeid
    */
    @SuppressWarnings("rawtypes")
    public void change(long orderId, Trainorder order, int random, String callbackurl, String qunarordernumber,
            String changeid, String randomStr, AsyncContext ctx) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //订单改签信息
        String changeSql = "select top 1 ID, C_TCSTATUS, C_TCCREATETIME, C_TCDEPARTTIME, C_CONFIRMREQTOKEN, "
                + "ISNULL(C_ISQUESTIONCHANGE, 0) C_ISQUESTIONCHANGE, ISNULL(C_TCISCHANGEREFUND, 0) C_TCISCHANGEREFUND "
                + "from T_TRAINORDERCHANGE with(nolock) where C_ORDERID = " + orderId + " order by ID desc";
        //如果传了changeid  可以直接用ID查找
        if (!ElongHotelInterfaceUtil.StringIsNull(changeid)) {
            changeSql = "select ID, C_TCSTATUS, C_TCCREATETIME, C_TCDEPARTTIME, C_CONFIRMREQTOKEN, "
                    + "ISNULL(C_ISQUESTIONCHANGE, 0) C_ISQUESTIONCHANGE, ISNULL(C_TCISCHANGEREFUND, 0) C_TCISCHANGEREFUND "
                    + "from T_TRAINORDERCHANGE with(nolock) where C_REQUESTREQTOKEN = '" + changeid + "'";
        }
        List changeList = Server.getInstance().getSystemService().findMapResultBySql(changeSql, null);
        if (changeList == null || changeList.size() == 0) {
            retobj.put("code", code113);
            retobj.put("msg", "未找到可以确认的改签车票");
            WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        //最后一个
        Map changeMap = (Map) changeList.get(0);
        //改签ID
        long changeId = Long.parseLong(changeMap.get("ID").toString());
        //改签状态
        int tcStatus = Integer.parseInt(changeMap.get("C_TCSTATUS").toString());
        //改签退
        int changeRefund = Integer.parseInt(changeMap.get("C_TCISCHANGEREFUND").toString());
        //重置采购问题
        boolean resetCaiGouQuestion = false;
        //防止占座采购问题
        if (tcStatus == Trainorderchange.THOUGHCHANGE) {
            resetCaiGouQuestion = true;//认为回调成功
        }
        //请求特征，相同时表示同一次请求
        String confirmReqtoken = changeMap.get("C_CONFIRMREQTOKEN") == null ? "" : changeMap.get("C_CONFIRMREQTOKEN")
                .toString();
        //同一次请求
        //        if (reqtoken.equals(confirmReqtoken)) {
        //            retobj.put("code", "100");
        //            retobj.put("success", true);
        //            retobj.put("msg", "确认请求已接受");//固定值，勿调整
        //            retobj.put("reqtoken", reqtoken);
        //            retobj.put("orderid", qunarordernumber);
        //            return retobj.toString();
        //        }
        //        //改签退
        //        if (changeType == 1 && changeRefund != 1) {
        //            retobj.put("code", code113);
        //            retobj.put("msg", "非法的业务参数");
        //            return retobj.toString();
        //        }
        //状态判断
        if (tcStatus == Trainorderchange.FINISHCHANGE) {
            retobj.put("code", code113);
            retobj.put("msg", "改签已确认，不能再次确认");
            WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        else if (tcStatus == Trainorderchange.FAILCHANGE) {
            retobj.put("code", code113);
            retobj.put("msg", "改签占座失败，不能确认");
            WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        else if (tcStatus == Trainorderchange.CANTCHANGE) {
            retobj.put("code", code113);
            retobj.put("msg", "改签已取消，不能继续确认");
            WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        else if (tcStatus == Trainorderchange.CHANGEWAITPAY || tcStatus == Trainorderchange.CHANGEPAYING) {
            retobj.put("code", code113);
            retobj.put("msg", "正在确认改签，不能再次确认");
            WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        else if (tcStatus == Trainorderchange.THOUGHCHANGE) {
            //判断超时
            boolean timeout = false;
            try {
                SimpleDateFormat shiFenFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat totalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //创建时间
                String tcTime = changeMap.get("C_TCCREATETIME").toString();
                long createTime = totalFormat.parse(tcTime).getTime();
                //发车时间
                String tcDepartTime = changeMap.get("C_TCDEPARTTIME").toString();
                long departTime = shiFenFormat.parse(tcDepartTime).getTime();
                //当前时间
                long currentTime = System.currentTimeMillis();
                //时间差
                long subTime = (currentTime - createTime) / 1000;//秒
                long createToDepart = departTime - createTime;//毫秒
                //12306在晚上11点半后不可支付了
                //开车前2小时内所购车票请于10分钟内完成支付
                //判断是否超时，22:44:59前成功申请的单子，供应商应该保留  30分钟的付款时间；22:45:00-23:00:00下的单子,供应商应该保留支付时限到23:30
                if (createToDepart < 120 * 60 * 1000 && subTime > 9 * 60) {
                    timeout = true;
                }
                else if (subTime > 29 * 60) {//25分钟付款时间
                    timeout = true;
                }
            }
            catch (Exception e) {
            }
            if (timeout) {
                JSONObject resultJsonObject = new JSONObject();
                resultJsonObject.put("returnCode", 1901);
                resultJsonObject.put("success", false);
                resultJsonObject.put("errorMsg", "确认改签时间已超时，确认改签失败");
                JSONObject dataJsonObject = new JSONObject();
                dataJsonObject.put("vendorOrderId", order.getOrdernumber());
                dataJsonObject.put("orderId", order.getQunarOrdernumber());
                resultJsonObject.put("data", dataJsonObject);
                WriteLog.write(logname, randomStr + "--->result:" + resultJsonObject.toJSONString());
                tuNiuServletUtil.getResponeOut(ctx, resultJsonObject.toString(), logname);
                return;
            }
        }
        else {
            retobj.put("code", code113);
            retobj.put("msg", "该状态下，不能确认改签");
            WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        AsyncChange(changeId, orderId, callbackurl, qunarordernumber, resetCaiGouQuestion, ctx, randomStr, order);
    }

    /**
     * 异步确认
     */
    public void AsyncChange(long changeId, long orderId, String callbackurl, String qunarordernumber,
            boolean resetCaiGouQuestion, AsyncContext ctx, String randomStr, Trainorder order) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //        retobj.put("reqtoken", reqtoken);
        //更新改签
        int C_TCSTATUS = Trainorderchange.CHANGEWAITPAY;
        int C_STATUS12306 = Trainorderchange.ORDEREDWAITPAY;
        //更新SQL
        String updateSql = "";
        //重置问题
        if (resetCaiGouQuestion) {
            updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + C_TCSTATUS + ", C_STATUS12306 = "
                    + C_STATUS12306 + ", C_CONFIRMISASYNC = 1, C_CONFIRMCALLBACKURL = '" + callbackurl
                    + "', C_ISQUESTIONCHANGE = 0 where ID = " + changeId + " and C_TCSTATUS = "
                    + Trainorderchange.THOUGHCHANGE;
        }
        else {
            updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + C_TCSTATUS + ", C_STATUS12306 = "
                    + C_STATUS12306 + ", C_CONFIRMISASYNC = 1, C_CONFIRMCALLBACKURL = '" + callbackurl
                    + "' where ID = " + changeId + " and C_TCSTATUS = " + Trainorderchange.THOUGHCHANGE;
        }
        //更新失败
        if (Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) != 1) {
            retobj.put("code", code999);
            retobj.put("msg", "确认改签失败");
            WriteLog.write(logname, randomStr + "--->result:" + retobj.toJSONString());
            tuNiuServletUtil.respByUnknownError(ctx, logname);
            return;
        }
        else {
            retobj.put("success", true);
            retobj.put("code", "100");
            retobj.put("msg", "确认请求已接受");//固定值，勿调整
            retobj.put("orderid", qunarordernumber);
            TongchengSupplyMethod.activeMQChangeOrder(changeId, 2);
        }
        JSONObject dataJsonObject = new JSONObject();
        dataJsonObject.put("vendorOrderId", order.getOrdernumber());
        dataJsonObject.put("orderId", order.getQunarOrdernumber());
        WriteLog.write(logname, randomStr + "--->result:" + dataJsonObject.toJSONString());
        tuNiuServletUtil.respBySuccess(ctx, logname, dataJsonObject);
        return;
    }

}
