package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.tuniu.TuNiuChangeCodeMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.OcsMethod;

/**
 * 取消改签
 * @author WH
 */

public class TongChengCancelChange extends TrainSelectLoginWay {

    //当前不提供服务
    private static final String code113 = "113";

    private static final String code999 = "999";

    public String operate(JSONObject json, int romdon) {
        return operate(json, romdon, "");
    }

    @SuppressWarnings("rawtypes")
    public String operate(JSONObject json, int romdon, String partnerid) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //请求参数
        String orderid = json.containsKey("orderid") ? json.getString("orderid") : "";

        String transactionid = json.containsKey("transactionid") ? json.getString("transactionid") : "";
        
        String reqtoken = json.containsKey("reqtoken") ? json.getString("reqtoken") : "";
        String accountId = json.containsKey("accountId") ? json.getString("accountId") : "";
        //设置返回
        //参数缺失
        if (ElongHotelInterfaceUtil.StringIsNull(orderid) || ElongHotelInterfaceUtil.StringIsNull(transactionid)) {
            retobj.put("code", "107");
            retobj.put("msg", "业务参数缺失");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        retobj.put("orderid", orderid);
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        //订单不存在
        if (orders == null || orders.size() != 1) {
            retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        Trainorder order = orders.get(0);
        long orderId = order.getId();
        int status = order.getOrderstatus();
        String extnumber = order.getExtnumber();
        //保存
        saveThirdAccountInfo(order.getId(), json);
        //占座成功，12306订单号不为空
        if (status != Trainorder.ISSUED || ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
            retobj.put("code", "112");
            retobj.put("msg", "该订单状态下，不能取消改签");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toString();
        }
        //订单改签信息
        String changeSql = "select top 1 ID, C_TCSTATUS, C_TCCREATETIME, ISNULL(C_TCNUMBER,'') C_TCNUMBER, C_REQUESTREQTOKEN "
                + "from T_TRAINORDERCHANGE with(nolock) where C_ORDERID = " + orderId + " order by ID desc";
        List changeList = Server.getInstance().getSystemService().findMapResultBySql(changeSql, null);
        if (changeList == null || changeList.size() == 0) {
            retobj.put("code", code113);
            retobj.put("msg", "未找到可以取消的改签车票");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toString();
        }
        //最后一个
        Map changeMap = (Map) changeList.get(0);
        //改签状态
        String requestReqtoken = changeMap.get("C_REQUESTREQTOKEN").toString();
        if (partnerid.contains("tuniu") && !reqtoken.equals(requestReqtoken)) {
        	retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toJSONString();
		}
        String tcnumber = changeMap.get("C_TCNUMBER").toString();
        int tcStatus = Integer.parseInt(changeMap.get("C_TCSTATUS").toString());
        if (tcStatus == Trainorderchange.FINISHCHANGE) {
            retobj.put("code", "1001");
            retobj.put("msg", "已改签票不能取消");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toString();
        }
        else if (tcStatus == Trainorderchange.CANTCHANGE) {
            retobj.put("code", code113);
            retobj.put("msg", "改签票已是取消状态");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toString();
        }
        else if (tcStatus == Trainorderchange.CHANGEWAITPAY || tcStatus == Trainorderchange.CHANGEPAYING) {
            retobj.put("code", code113);
            retobj.put("msg", "正在确认改签，不能进行取消");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toJSONString();
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
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        long changeId = Long.parseLong(changeMap.get("ID").toString());
        //加载其他字段、乘客
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());
        //下单账户
        String createAccount = order.getSupplyaccount();
        if (ElongHotelInterfaceUtil.StringIsNull(createAccount) && order.getOrdertype() != 3
                && order.getOrdertype() != 4 && order.getOrdertype() != 6) {
            retobj.put("code", code999);
            retobj.put("msg", "取消改签失败");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        saveThirdAccountInfo(orderId, json);
        Customeruser user = new Customeruser();
        if (order.getOrdertype() == 6 && !ElongHotelInterfaceUtil.StringIsNull(accountId)) {
            user = getCustomeruserByusernameEncryption(accountId);
        }
        user = getCustomeruserBy12306Account(order, romdon, true);
        if (user == null || user.isDontRetryLogin() || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //释放账号
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
            //返回结果
            retobj.put("code", code999);
            retobj.put("msg", user != null && user.isDontRetryLogin() ? user.getNationality() : "取消改签失败");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toJSONString();
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
                    + order.getId() + JoinCommonAccountInfo(user, rep);
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
            if (result.contains("取消订单成功") || "无未支付订单".equals(result)) {
                cancelTrue = true;
            }
            else if (Account12306Util.accountNoLogin(result, user)) {
                accountNoLogin = true;
            }
        }
        catch (Exception e) {
        }
        WriteLog.write("t同程火车票接口_4.13.取消改签", romdon + ">>>>>" + order.getId() + ">>>>>REP服务器地址>>>>>" + url
                + ">>>>>REP返回>>>>>" + result);
        //取消失败
        if (!cancelTrue) {
            //释放账号
            freeCustomeruser(user, accountNoLogin ? AccountSystem.FreeNoLogin : AccountSystem.FreeNoCare,
                    AccountSystem.OneFree, AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
            //返回结果
            retobj.put("code", code999);
            retobj.put("msg", "取消改签失败");
            retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
            return retobj.toJSONString();
        }
        try {
            //释放账号
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.TwoFree, AccountSystem.OneCancel,
                    AccountSystem.NullDepartTime);
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
            createtrainorderrc(1, content, orderId, 0l, Trainticket.CANTCHANGE, "系统接口");
        }
        catch (Exception e) {
        }
        try {
            //KEY
            String key = "TrainChange=" + user.getLoginname();
            //移除
            OcsMethod.getInstance().remove(key);
        }
        catch (Exception e) {
        }
        retobj.put("success", true);
        retobj.put("code", "100");
        retobj.put("msg", "取消改签成功");
        retobj.put("orderid", orderid);
        retobj.put("transactionid", transactionid);
        //乘客
        try {
            JSONArray changeTickets = new JSONArray();
            for (Trainpassenger passenger : order.getPassengers()) {
                for (Trainticket ticket : passenger.getTraintickets()) {
                    //改签车票
                    if (ticket.getChangeid() == changeId) {
                        //乘客ID非空
                        if (!ElongHotelInterfaceUtil.StringIsNull(ticket.getTcPassengerId())) {
                            JSONObject changeTicket = new JSONObject();
                            changeTicket.put("passengerid", ticket.getTcPassengerId());
                            changeTickets.add(changeTicket);
                        }
                        //中断，循环下一个乘客
                        break;
                    }
                }
            }
            if (changeTickets.size() > 0) {
                retobj.put("changeTickets", changeTickets);
            }
        }
        catch (Exception e) {
        }
        //返回
        retobj = TuNiuChangeCodeMethod.changeCancelCode(retobj, partnerid);
        return retobj.toJSONString();
    }
}