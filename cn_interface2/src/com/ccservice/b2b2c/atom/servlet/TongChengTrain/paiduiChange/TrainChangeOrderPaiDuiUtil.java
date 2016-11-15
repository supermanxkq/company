package com.ccservice.b2b2c.atom.servlet.TongChengTrain.paiduiChange;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;
import TrainInterfaceMethod.TrainInterfaceMethod;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengReqChange;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;

/**
 * 改签排队工具类
 * @author WH
 * @time 2016年2月17日 下午3:28:26
 * @version 1.0
 */

public class TrainChangeOrderPaiDuiUtil extends TrainSelectLoginWay {

    private static final String code995 = "995";

    private static Object paiDuiDataLocker = new Object();

    private static Hashtable<Long, Boolean> paiDuiData = new Hashtable<Long, Boolean>();

    private static void paiDuiDataAdd(long changeId) {
        synchronized (paiDuiDataLocker) {
            if (!paiDuiData.containsKey(changeId)) {
                paiDuiData.put(changeId, true);
            }
        }
    }

    private static void paiDuiDataRemove(long changeId) {
        synchronized (paiDuiDataLocker) {
            if (paiDuiData.containsKey(changeId)) {
                paiDuiData.remove(changeId);
            }
        }
    }

    /**
     * 改签或变更到站排队中，更新数据
     */
    public boolean updatePaidui(String changeTSFlag, int refund_online, Trainorderchange change, Trainorder order) {
        //ID
        long changeId = change.getId();
        long orderId = change.getOrderid();
        //更新12306状态
        int updateResult = 0;
        int C_STATUS12306 = 9;//正在排队
        try {
            String updateSql = "update T_TRAINORDERCHANGE set C_STATUS12306 = " + C_STATUS12306 + " where ID = "
                    + changeId + " and C_TCSTATUS = " + Trainorderchange.APPLYROCESSING;
            updateResult = Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("TrainChangeOrderPaiDuiUtil_UpdatePaidui_Exception", e, orderId + "--"
                    + changeId);
        }
        //日志、排队数据入库
        if (updateResult > 0) {
            //类型
            changeTSFlag = "Y".equals(changeTSFlag) ? "变更到站" : "改签";
            //日志
            createtrainorderrc(1, "[" + changeTSFlag + " - " + changeId + "]已走排队机制", orderId, changeId, C_STATUS12306,
                    "系统接口");
            //超时时间
            String changeTimeOut = changeTimeOut(change, order);
            //排队数据入库
            String insertSql = "insert into TrainOrderChangePaiduiData(C_TrainOrderId, C_ChangeId, C_RefundOnline, "
                    + "C_DepartDate, C_TimeOut) values(" + orderId + ", " + changeId + ", " + refund_online + ", '"
                    + change.getTcdeparttime().split(" ")[0] + "', '" + changeTimeOut + "')";
            updateResult = Server.getInstance().getSystemService().excuteAdvertisementBySql(insertSql);
        }
        //返回
        return updateResult == 1;
    }

    /**
     * 排队核心处理逻辑
     */
    public void paiDuiOperate(long changeId, long paiDuiId, int idx, boolean isLastPaidui, int refundOnline) {
        //排队中
        if (paiDuiData.containsKey(changeId)) {
            return;//中断
        }
        //排队中
        paiDuiDataAdd(changeId);
        //当前次数
        idx = idx + 1;
        //是否需要在排队信息表里删除这个订单的记录
        boolean isDeleteQueue = true;
        //改签订单信息
        Trainorderchange change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
        //订单ID
        long orderId = change.getOrderid();
        String changeTSFlag = change.getChangeArrivalFlag() == 1 ? "变更到站" : "改签";
        //检测排队结果
        if (change.getStatus12306() == 9) {
            //状态
            updatePaiDuiStatus(paiDuiId, 1, idx);
            //订单
            Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderId);
            //日志
            createtrainorderrc(1, "[" + changeTSFlag + " - " + changeId + "][第" + idx + "次]开始检测排队结果", orderId,
                    changeId, change.getStatus12306(), "排队系统");
            //账号
            Customeruser user = getCustomeruser(order);
            //不重试
            if (user.isDontRetryLogin()) {
                //拒单
                paiDuiFail(order, change, user, new JSONObject(), paiDuiId, user.getNationality(), idx, false);
                //返回
                return;
            }
            //获取12306未完成订单
            JSONObject orderInfoAndFee = catchOrderInfoAndFee(user, order, change);
            //校验标识>>0：默认；1：订单正确；2：订单错误
            int MyOrderNoCompleteFlag = orderInfoAndFee.getIntValue("flag");
            //提示信息
            String paiDuiResult = orderInfoAndFee.getString("paiDuiResult");
            paiDuiResult = ElongHotelInterfaceUtil.StringIsNull(paiDuiResult) ? "" : paiDuiResult;
            //出票失败
            if (paiDuiResult.contains("出票失败了")) {
                String failResult = "";
                try {
                    failResult = paiDuiResult.substring(paiDuiResult.indexOf("<span>") + 6,
                            paiDuiResult.indexOf("</span>"));
                }
                catch (Exception e) {
                }
                //拒单
                paiDuiFail(order, change, user, orderInfoAndFee, paiDuiId,
                        ElongHotelInterfaceUtil.StringIsNull(failResult) ? "出票失败了" : failResult, idx, false);
            }
            //没有足够的票
            else if (paiDuiResult.contains("没有足够的票")) {
                paiDuiFail(order, change, user, orderInfoAndFee, paiDuiId, "没有足够的票", idx, false);
            }
            //最后一次、无未支付订单
            else if (isLastPaidui && paiDuiResult.contains("无未支付订单")) {
                paiDuiFail(order, change, user, orderInfoAndFee, paiDuiId, "无未支付订单", idx, true);
            }
            //排队成功
            else if (MyOrderNoCompleteFlag == 1 && isAllReturn(order, change, orderInfoAndFee.getString("12306"))) {
                //新状态
                int newStatus = newStatus(changeId);
                //还在占座
                if (newStatus == Trainorderchange.APPLYROCESSING) {
                    orderInfoAndFee.put("refund_online", refundOnline);
                    continuePaidui("", false, change, paiDuiId, idx, true, user);
                    paiDuiSuccess(order, change, orderInfoAndFee, user, paiDuiId, idx);
                }
                else {
                    continuePaidui("订单状态变化[" + change.getStatusstr() + "]", false, change, paiDuiId, idx, false, user);
                    //已取消改签
                    if (newStatus == Trainorderchange.CANTCHANGE || newStatus == Trainorderchange.FAILCHANGE) {
                        new TongChengReqChange().cancelChange(orderId, orderInfoAndFee.getString("sequence_no"), user);
                    }
                    //释放账号
                    else {
                        freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree,
                                AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                    }
                }
            }
            //非自己的
            else if (MyOrderNoCompleteFlag == 1 || MyOrderNoCompleteFlag == 2) {
                paiDuiFail(order, change, user, orderInfoAndFee, paiDuiId, "", idx, isLastPaidui);
            }
            //最后一次
            else if (isLastPaidui) {
                paiDuiFail(order, change, user, orderInfoAndFee, paiDuiId, paiDuiResult, idx, true);
            }
            //重新排队
            else {
                //排队时间
                isDeleteQueue = false;
                freshQueueResult(orderId, paiDuiResult);
                //继续排队
                continuePaidui("订单信息不全", true, change, paiDuiId, idx, false, user);
                //释放账号
                freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
        }
        //删除时间
        if (isDeleteQueue) {
            deleteQueueResult(orderId);
        }
        //结束排队
        paiDuiDataRemove(changeId);
    }

    /**
     * 改签新状态
     */
    private int newStatus(long changeId) {
        return Server.getInstance().getTrainService().findTrainOrderChangeById(changeId).getTcstatus();
    }

    /**
     * 排队成功
     */
    private void paiDuiSuccess(Trainorder order, Trainorderchange change, JSONObject repobj, Customeruser user,
            long paiDuiId, int idx) {
        //订单乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //解析参数
        int changeType = 0;
        String old_zwcode = "";
        long orderId = order.getId();
        JSONArray tickets = new JSONArray();
        String change_zwcode = change.getTcseattype();
        List<Integer> piaoTypes = new ArrayList<Integer>();
        String changeTSFlag = change.getChangeArrivalFlag() == 1 ? "Y" : "N";
        Map<String, JSONObject> reqTickets = new HashMap<String, JSONObject>();
        Map<String, Trainticket> oldTicketMap = new HashMap<String, Trainticket>();
        Map<String, Trainpassenger> passengerMap = new HashMap<String, Trainpassenger>();
        //循环乘客
        for (Trainpassenger passenger : passengers) {
            List<Trainticket> traintickets = passenger.getTraintickets();
            //循环车票
            for (Trainticket trainticket : traintickets) {
                //匹配改签
                if (trainticket.getChangeid() == change.getId()) {
                    //改签车票信息
                    JSONObject changeTicket = new JSONObject();
                    changeTicket.put("passengersename", passenger.getName());
                    changeTicket.put("passportseno", passenger.getIdnumber());
                    changeTicket.put("piaotype", trainticket.getTickettype());
                    changeTicket.put("old_ticket_no", trainticket.getTicketno());
                    changeTicket.put("passporttypeseid", getIdtype12306(passenger.getIdtype()));
                    //相关参数赋值
                    tickets.add(changeTicket);
                    old_zwcode = trainticket.getSeattype();
                    passengerMap.put(trainticket.getTicketno(), passenger);
                    oldTicketMap.put(trainticket.getTicketno(), trainticket);
                    reqTickets.put(trainticket.getTicketno(), changeTicket);
                }
            }
        }
        //下单请求
        JSONObject reqobj = new JSONObject();
        reqobj.put("ticketinfo", tickets);
        reqobj.put("old_zwcode", old_zwcode);
        reqobj.put("change_zwcode", change_zwcode);
        reqobj.put("ordernumber", order.getExtnumber());
        reqobj.put("change_checi", change.getTctrainno());
        reqobj.put("orderid", order.getQunarOrdernumber());
        reqobj.put("transactionid", order.getOrdernumber());
        reqobj.put("change_datetime", change.getTcdeparttime() + ":00");
        //变更到站
        if ("Y".equals(changeTSFlag)) {
            reqobj.put("to_station_name", change.getStationName().split("-")[1].trim());
        }
        TongChengReqChange req = new TongChengReqChange();
        //解析数据
        JSONObject retobj = req.parse12306(order, reqobj, repobj, oldTicketMap, passengerMap, reqTickets, piaoTypes,
                changeType, change, changeTSFlag, user);
        //改签成功
        if (retobj.getBooleanValue("success")) {
            RepServerUtil.freeRepServerByAccount(user);
        }
        //不要无座等
        else {
            //取消改签
            req.cancelChange(orderId, repobj.getString("sequence_no"), user);
            //修改改签和车票状态
            updateChangeAndTicketStatus(change.getId());
            //OCS
            deleteMemCached(order);
        }
        //相关ID
        callBack(change, order, retobj);
    }

    /**
     * 排队失败
     */
    private void paiDuiFail(Trainorder order, Trainorderchange change, Customeruser user, JSONObject orderInfoAndFee,
            long paiDuiId, String paiDuiResult, int idx, boolean isLastPaidui) {
        String json = orderInfoAndFee.toString();
        //操作记录
        continuePaidui(isLastPaidui ? "达到规定排队次数" : paiDuiResult, false, change, paiDuiId, idx, false, user);
        //取消排队
        cancelPaiDui(order, change, user, paiDuiResult, isLastPaidui);
        //未登录
        boolean NoLogin = Account12306Util.accountNoLogin(json, user);
        //释放账号        
        freeCustomeruser(user, NoLogin ? AccountSystem.FreeNoLogin : AccountSystem.FreeNoCare, AccountSystem.OneFree,
                AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
        //修改改签和车票状态
        updateChangeAndTicketStatus(change.getId());
        //OCS
        deleteMemCached(order);
        //回调失败
        callBack(change, order, failResult(order, change));
    }

    /**
     * 拒单处理>>超时或达到规定排队次数
     */
    public void refuseChange(long changeId, long paiDuiId, int idx) {
        //改签订单信息
        Trainorderchange change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
        //正在排队状态
        if (change.getStatus12306() == 9) {
            //订单ID
            long orderId = change.getOrderid();
            //账号
            Customeruser user = new Customeruser();
            //订单
            Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderId);
            //拒单
            paiDuiFail(order, change, user, new JSONObject(), paiDuiId, "超时或达到规定排队次数", idx, false);
            //临时赋值
            user.setFromAccountSystem(true);
            user.setLoginname(order.getSupplyaccount().split("/")[0]);
            user.setCustomerAccount(order.getOrdertype() == 3 || order.getOrdertype() == 4);
            //释放账号
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
        }
    }

    /**
     * 回调参数
     */
    private JSONObject failResult(Trainorder order, Trainorderchange change) {
        //结果
        JSONObject retobj = new JSONObject();
        retobj.put("code", code995);
        retobj.put("success", false);
        retobj.put("msg", "提交" + (change.getChangeArrivalFlag() == 1 ? "变更到站" : "改签") + "失败:12306排队!");
        return retobj;
    }

    /**
     * 取账号
     */
    private Customeruser getCustomeruser(Trainorder order) {
        //取账号
        Customeruser user = new Customeruser();
        //多次尝试
        for (int i = 0; i < 3; i++) {
            //账号系统，以未登录释放账号
            if (user != null && user.isFromAccountSystem()) {
                freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            user = getCustomeruserBy12306Account(order, i, i > 0);
            //为空
            user = user == null ? new Customeruser() : user;
            //不用重试、成功获取cookie跳出循环
            if (user.isDontRetryLogin() || !ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
                break;
            }
        }
        return user;
    }

    /**
     * 继续下一次排队
     * @param paiDuiResult 本次失败原因
     * @param isContinuePaidui 是否继续排队
     */
    private void continuePaidui(String paiDuiResult, boolean isContinuePaidui, Trainorderchange change, long paiDuiId,
            int idx, boolean paiDuiSuccess, Customeruser user) {
        //不继续排队>>保存排队数据时，释放了REP，未释放账号
        if (!isContinuePaidui) {
            //REP信息
            String repInfo = user.getMemberemail();
            //REP信息置空，不同时释放
            user.setMemberemail("");
            //释放账号
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
            //重新赋值
            user.setMemberemail(repInfo);
        }
        //更新
        updatePaiDuiStatus(paiDuiId, isContinuePaidui ? 0 : 2, idx);
        //标识
        String changeTSFlag = change.getChangeArrivalFlag() == 1 ? "变更到站" : "改签";
        //日志
        String content = "";
        //成功
        if (paiDuiSuccess) {
            content = "[" + changeTSFlag + " - " + change.getId() + "][第" + idx + "次]排队成功";
        }
        //继续
        else if (isContinuePaidui) {
            content = "[" + changeTSFlag + " - " + change.getId() + "][第" + idx + "次]排队失败：订单信息不全，等待下次检测";
        }
        else {
            paiDuiResult = ElongHotelInterfaceUtil.StringIsNull(paiDuiResult) ? "false" : paiDuiResult;
            content = "[" + changeTSFlag + " - " + change.getId() + "][第" + idx + "次]排队失败：" + paiDuiResult + "：暂停排队";
        }
        createtrainorderrc(1, content, change.getOrderid(), 0L, 0, "排队系统");
    }

    /**
     * 取消排队
     */
    private void cancelPaiDui(Trainorder order, Trainorderchange change, Customeruser user, String paiDuiResult,
            boolean isLastPaidui) {
        //包含排队日期
        if (isLastPaidui && paiDuiResult.contains("订单排队中")
                && paiDuiResult.contains(change.getTcdeparttime().split(" ")[0])) {
            boolean paiDuiRight = false;
            //比较车次
            for (String trainNo : change.getTctrainno().split("/")) {
                if (paiDuiResult.contains(trainNo)) {
                    paiDuiRight = true;
                    break;
                }
            }
            if (!paiDuiRight) {
                return;
            }
            //重置
            paiDuiRight = false;
            //所有乘客
            List<Trainpassenger> passengers = order.getPassengers();
            //本地系统数据
            for (int i = 0; i < passengers.size(); i++) {
                Trainpassenger localPassenger = passengers.get(i);
                //本地车票
                Trainticket localTicket = localPassenger.getTraintickets().get(0);
                //改签不匹配
                if (localTicket.getChangeid() != change.getId()) {
                    continue;
                }
                if (paiDuiResult.contains(localPassenger.getName())) {
                    paiDuiRight = true;
                    break;
                }
            }
            if (!paiDuiRight) {
                return;
            }
            //获取REP
            RepServerBean rep = RepServerUtil.getRepServer(user, false);
            //参数
            String param = "datatypeflag=32&cookie=" + user.getCardnunber() + JoinCommonAccountInfo(user, rep);
            //请求
            String html = RequestUtil.post(rep.getUrl(), param, "UTF-8", new HashMap<String, String>(), 1 * 60 * 1000);
            //用户未登录
            if (html.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
                //切换REP
                rep = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
                //类型正确
                if (rep.getType() == 1) {
                    //重拼参数
                    param = "datatypeflag=32&cookie=" + user.getCardnunber() + JoinCommonAccountInfo(user, rep);
                    //重新请求
                    html = RequestUtil.post(rep.getUrl(), param, "UTF-8", new HashMap<String, String>(), 1 * 60 * 1000);
                }
            }
            //日志
            WriteLog.write("TrainChangeOrderPaiDuiUtil_cancelPaiDui", change.getId() + "-->" + html);
        }
    }

    /**
     * 排队失败还原状态>>0等待检测,1检测中,2检测结束
     */
    private void updatePaiDuiStatus(long paiDuiId, int status, int idx) {
        //SQL
        String sql = "update TrainOrderChangePaiduiData set C_CheckStatus = " + status;
        //排队开始
        if (status == 1) {
            sql += ", C_CheckCount = " + idx + ", C_LastCheckTime = '" + ElongHotelInterfaceUtil.getCurrentTime() + "'";
        }
        Server.getInstance().getSystemService().excuteAdvertisementBySql(sql + " where C_ID = " + paiDuiId);
    }

    /**
     * 更新改签和车票状态
     */
    private void updateChangeAndTicketStatus(long changeId) {
        //改签状态
        int C_TCSTATUS = Trainorderchange.FAILCHANGE;
        int C_STATUS12306 = Trainorderchange.ORDERFALSE;
        String updateSql = "update T_TRAINORDERCHANGE set C_TCSTATUS = " + C_TCSTATUS + ", C_STATUS12306 = "
                + C_STATUS12306 + " where ID = " + changeId + " and C_TCSTATUS = " + Trainorderchange.APPLYROCESSING;
        Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
        //车票状态
        updateSql = "update T_TRAINTICKET set C_STATUS = " + Trainticket.ISSUED + " where C_CHANGEID = " + changeId
                + " and C_STATUS = " + Trainticket.APPLYCHANGE;
        //更新车票
        Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
    }

    /**
     * 改签结束删除MemCached中的账号
     */
    public void deleteMemCached(Trainorder order) {
        try {
            //账号
            String supplyaccount = order.getSupplyaccount();
            //删除
            if (!ElongHotelInterfaceUtil.StringIsNull(supplyaccount) && !"".equals(supplyaccount.split("/")[0])) {
                OcsMethod.getInstance().remove("TrainChange=" + supplyaccount.split("/")[0]);
            }
        }
        catch (Exception e) {

        }
    }

    /**
     * 回调方法
     */
    private void callBack(Trainorderchange change, Trainorder order, JSONObject retobj) {
        retobj.put("agentId", order.getAgentid());
        retobj.put("method", "train_request_change");
        retobj.put("orderid", order.getQunarOrdernumber());
        retobj.put("transactionid", order.getOrdernumber());
        retobj.put("reqtoken", change.getRequestReqtoken());
        retobj.put("callBackUrl", change.getRequestCallBackUrl());
        //接口类型
        int interfacetype = order.getInterfacetype() != null && order.getInterfacetype() > 0 ? order.getInterfacetype()
                : getOrderAttribution(order);
        //回调淘宝
        if (interfacetype == TrainInterfaceMethod.TAOBAO) {
            callBackTaoBao(retobj, change);
        }
        //回调同程
        else if (interfacetype == TrainInterfaceMethod.TONGCHENG || interfacetype == TrainInterfaceMethod.MEITUAN) {
            callBackTongCheng(retobj, change);
        }
    }

    /**
     * 回调淘宝
     */
    private void callBackTaoBao(JSONObject retobj, Trainorderchange change) {
        long changeId = change.getId();
        //参数
        retobj.put("changeorderid", change.getId());
        retobj.put("apply_id", change.getTaobaoapplyid());
        retobj.put("refund_online", change.getIscanrefundonline());
        //日志
        WriteLog.write("TrainChangeOrderPaiDuiUtil_callBackTaoBao", retobj.toString());
        //地址
        String url = PropertyUtil.getValue("TaoBao_Change_CallBack_Url", "Train.properties");
        //回调
        String result = RequestUtil.post(url, retobj.toString(), "UTF-8", new HashMap<String, String>(), 0);
        //回调成功
        boolean callbacktrue = "success".equalsIgnoreCase(result) ? true : false;
        //采购问题
        if (!callbacktrue) {
            String updateSql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = " + Trainorderchange.CAIGOUQUESTION
                    + " where ID = " + changeId;
            //占座成功
            if (retobj.getBooleanValue("success")) {
                updateSql += " and C_TCSTATUS = " + Trainorderchange.THOUGHCHANGE;
            }
            else {
                updateSql += " and C_TCSTATUS = " + Trainorderchange.FAILCHANGE;
            }
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
        }
        //日志
        String changeResult = retobj.getBooleanValue("success") ? "成功" : "失败";
        String callbackResult = callbacktrue ? "成功" : "失败---" + result;
        createtrainorderrc(1, "[改签 - " + changeId + "]---改签占座" + changeResult + "---回调" + callbackResult,
                change.getOrderid(), 0L, 1, "系统");

    }

    /**
     * 回调同程
     */
    private void callBackTongCheng(JSONObject retobj, Trainorderchange change) {
        long changeId = change.getId();
        //地址
        String url = PropertyUtil.getValue("tcTrainCallBack", "Train.ChangeOrder.properties");
        //日志
        WriteLog.write("TrainChangeOrderPaiDuiUtil_callBackTongCheng", url + "-->" + retobj.toString());
        //回调
        String result = RequestUtil.post(url, retobj.toString(), "UTF-8", new HashMap<String, String>(), 0);
        //回调成功
        boolean callbacktrue = "success".equalsIgnoreCase(result) ? true : false;
        //采购问题
        if (!callbacktrue) {
            String updateSql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = " + Trainorderchange.CAIGOUQUESTION
                    + " where ID = " + changeId;
            //占座成功
            if (retobj.getBooleanValue("success")) {
                updateSql += " and C_TCSTATUS = " + Trainorderchange.THOUGHCHANGE;
            }
            else {
                updateSql += " and C_TCSTATUS = " + Trainorderchange.FAILCHANGE;
            }
            Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
        }
        //日志
        String changeResult = retobj.getBooleanValue("success") ? "成功" : "失败";
        String callbackResult = callbacktrue ? "成功" : "失败---" + result;
        createtrainorderrc(1, "[改签 - " + changeId + "]---改签占座" + changeResult + "---回调" + callbackResult,
                change.getOrderid(), 0L, 1, "系统");
    }

    /**
     * 判断改签订单手续费
     */
    public static boolean changeFeeError(Trainorder order, JSONObject repobj) {
        String msg = repobj.getString("msg");
        msg = ElongHotelInterfaceUtil.StringIsNull(msg) ? "" : msg;
        return repobj.containsKey("payOrderInitHtml") && msg.contains("获取") && msg.contains("手续费失败")
                && order.getExtnumber().equals(repobj.getString("sequence_no"));
    }

    /**
     * 判断改签订单信息完整性
     * @param orderHtml 订单信息
     * @param changeFees 手续费信息
     * @param isPaiDui 排队
     */
    public static boolean isAllReturn(Trainorder trainOrder, Trainorderchange change, String orderHtml) {
        boolean right = false;
        try {
            orderHtml = ElongHotelInterfaceUtil.StringIsNull(orderHtml) ? "" : orderHtml;
            //解析订单
            JSONArray orderDBList = orderHtml.contains("orderDBList") ? JSONObject.parseObject(orderHtml)
                    .getJSONObject("data").getJSONArray("orderDBList") : new JSONArray();
            //未完成订单唯一
            if (orderDBList.size() == 1) {
                //第一个
                JSONObject firstOrder = orderDBList.getJSONObject(0);
                //单号一致
                if (trainOrder.getExtnumber().equals(firstOrder.getString("sequence_no"))) {
                    JSONObject stationTrainDTO = new JSONObject();
                    //匹配
                    int matchTotal = 0;
                    List<String> matchList = new ArrayList<String>();
                    //车票
                    JSONArray tickets = firstOrder.getJSONArray("tickets");
                    //循环车票
                    for (int i = 0; i < tickets.size(); i++) {
                        JSONObject ticket = tickets.getJSONObject(i);
                        //乘客信息
                        JSONObject passengerDTO = ticket.getJSONObject("passengerDTO");
                        String passenger_name = passengerDTO.getString("passenger_name");
                        String passenger_id_no = passengerDTO.getString("passenger_id_no").toUpperCase();
                        String passenger_id_type_code = passengerDTO.getString("passenger_id_type_code");
                        //座席编码
                        String seat_type_code = ticket.getString("seat_type_code");
                        //车票类型，1：成人票
                        String ticket_type_code = ticket.getString("ticket_type_code");
                        //车次信息
                        if (i == 0) {
                            stationTrainDTO = ticket.getJSONObject("stationTrainDTO");
                        }
                        //拼匹配数据
                        String keyfalg = passenger_name + "@" + passenger_id_no + "@" + passenger_id_type_code + "@"
                                + seat_type_code + "@" + ticket_type_code;
                        //用于匹配本地
                        matchList.add(keyfalg);
                    }
                    //所有乘客
                    List<Trainpassenger> passengers = trainOrder.getPassengers();
                    //本地系统数据
                    for (int i = 0; i < passengers.size(); i++) {
                        Trainpassenger localPassenger = passengers.get(i);
                        //本地车票
                        Trainticket localTicket = localPassenger.getTraintickets().get(0);
                        //改签不匹配
                        if (localTicket.getChangeid() != change.getId()) {
                            continue;
                        }
                        //座席编码
                        String seat_type_code = change.getTcseattype();
                        //乘客信息
                        String passenger_name = localPassenger.getName();
                        String passenger_id_no = localPassenger.getIdnumber().toUpperCase();
                        String passenger_id_type_code = getIdtype12306(localPassenger.getIdtype());
                        //车票类型，1：成人票
                        String ticket_type_code = String.valueOf(localTicket.getTickettype());
                        //拼匹配数据
                        String keyfalg = passenger_name + "@" + passenger_id_no + "@" + passenger_id_type_code + "@"
                                + seat_type_code + "@" + ticket_type_code;
                        //匹配数据
                        if (matchList.contains(keyfalg)) {
                            matchTotal++;
                            matchList.remove(keyfalg);
                        }
                    }
                    //车次
                    String trainCodeLocal = "";
                    String trainCode12306 = firstOrder.getString("train_code_page");
                    //本地可能多个，T101/T102
                    for (String trainNo : change.getTctrainno().split("/")) {
                        //12306匹配到本地
                        if (trainCode12306.equals(trainNo)) {
                            trainCodeLocal = trainNo;
                            break;
                        }
                    }
                    //到达车站
                    String toStationLocal = change.getStationName().split("-")[1].trim();
                    String toStation12306 = stationTrainDTO.getString("to_station_name");
                    //发车时间
                    String startDateLocal = change.getTcdeparttime().substring(0, 10);
                    String startDate12306 = firstOrder.getString("start_train_date_page").substring(0, 10);
                    //全匹配、车次比较、日期比较
                    if (trainCodeLocal.equals(trainCode12306) && startDateLocal.equals(startDate12306)
                            && matchList.size() == 0 && matchTotal == change.getPassengerName().split("<br/>").length) {
                        right = change.getChangeArrivalFlag() == 1 ? orderHtml.contains("变更到站待支付")
                                && toStationLocal.equals(toStation12306) : orderHtml.contains("改签待支付");
                    }
                }
            }
        }
        catch (Exception e) {
        }
        return right;
    }

    /**
     * 获取未完成订单信息及手续费
     */
    public JSONObject catchOrderInfoAndFee(Customeruser user, Trainorder order, Trainorderchange change) {
        //结果
        JSONObject retobj = new JSONObject();
        //记录日志
        int random = Integer.parseInt(Long.toString(change.getId()));
        //改签乘客
        String[] passengerNames = change.getPassengerName().split("<br/>");
        //乘客数组
        JSONArray passengers = new JSONArray();
        for (String passengerName : passengerNames) {
            passengers.add(passengerName);
        }
        //请求参数
        JSONObject params = new JSONObject();
        params.put("passengers", passengers);
        params.put("sequence_no", order.getExtnumber());
        //多次尝试
        for (int i = 0; i < 5; i++) {
            try {
                //Cookie
                params.put("cookie", user.getCardnunber());
                //获取REP
                RepServerBean rep = RepServerUtil.getRepServer(user, false);
                //REP地址
                String repUrl = rep.getUrl();
                //请求参数URLEncoder
                String jsonStr = URLEncoder.encode(params.toString(), "UTF-8");
                //请求参数
                String param = "datatypeflag=111&jsonStr=" + jsonStr + JoinCommonAccountInfo(user, rep);
                //请求REP
                String retdata = RequestUtil.post(repUrl, param, "UTF-8", new HashMap<String, String>(), 1 * 60 * 1000);
                //用户未登录
                if (retdata.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
                    //切换REP
                    rep = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
                    //类型正确
                    if (rep.getType() == 1) {
                        //REP地址
                        repUrl = rep.getUrl();
                        //重拼参数
                        param = "datatypeflag=111&jsonStr=" + jsonStr + JoinCommonAccountInfo(user, rep);
                        //重新请求
                        retdata = RequestUtil
                                .post(repUrl, param, "UTF-8", new HashMap<String, String>(), 1 * 60 * 1000);
                    }
                }
                //记录日志
                WriteLog.write("TrainChangeOrderPaiDuiUtil_CatchOrderInfoAndFee", i + ":OrderId:" + order.getId()
                        + ":ChangeId:" + change.getId() + ":RepUrl:" + repUrl + ":Result:" + retdata);
                //结果为空
                if (ElongHotelInterfaceUtil.StringIsNull(retdata)) {
                    continue;
                }
                //解析REP结果
                retobj = JSONObject.parseObject(retdata);
                //用户未登录
                if (Account12306Util.accountNoLogin(retdata, user)) {
                    //释放未登录
                    freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                            AccountSystem.NullDepartTime);
                    //重新获取
                    user = getCustomeruserBy12306Account(order, random, true);
                    //账号问题
                    if (user != null && user.isDontRetryLogin()) {
                        break;
                    }
                    else {
                        continue;
                    }
                }
                //未完成订单标识
                int MyOrderNoCompleteFlag = retobj.getIntValue("flag");
                //存在未完成订单
                if (MyOrderNoCompleteFlag == 1 || MyOrderNoCompleteFlag == 2) {
                    break;
                }
            }
            catch (Exception e) {
            }
        }
        return retobj == null ? new JSONObject() : retobj;
    }

    /**
     * 超时时间
     */
    private String changeTimeOut(Trainorderchange change, Trainorder order) {
        //结果
        String timeout = "";
        //处理
        try {
            if (change.getChangetimeout() != null) {
                timeout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(change.getChangetimeout());
            }
        }
        catch (Exception e) {
        }
        //返回
        return ElongHotelInterfaceUtil.StringIsNull(timeout) ? "" : timeout;
    }

    /**
     * 保存>>12306排队时间
     * @author 殷树斌
     */
    private void freshQueueResult(long orderId, String paiduiResult) {
        try {
            if (!ElongHotelInterfaceUtil.StringIsNull(paiduiResult) && paiduiResult.contains("最新预估等待时间为<span><strong>")
                    && paiduiResult.contains("</strong>分钟</span>")) {
                int waitTime = Integer.valueOf(paiduiResult.split("最新预估等待时间为<span><strong>")[1]
                        .split("</strong>分钟</span>")[0]) * 60;
                if (waitTime > 0) {
                    String freshQueueSql = " [sp_TrainOrderQueue_Update] @OrderId = " + orderId + ", @WaitTime = "
                            + waitTime + ", @Type = 2";
                    WriteLog.write("将排队数据刷新DB_改签", orderId + "--->" + freshQueueSql);
                    Server.getInstance().getSystemService().findMapResultByProcedure(freshQueueSql);
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("将排队数据刷新DB_改签_ERROR", e, String.valueOf(orderId));
        }
    }

    /**
     * 删除>>12306排队时间
     * @author 殷树斌
     */
    private void deleteQueueResult(long orderId) {
        try {
            String deleteQueueSql = " [sp_TrainOrderQueue_Delete] @OrderId = " + orderId + ", @Type = 2";
            WriteLog.write("将排队数据删除DB_改签", orderId + "--->" + deleteQueueSql);
            Server.getInstance().getSystemService().findMapResultByProcedure(deleteQueueSql);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("将排队数据删除DB_改签_ERROR", e, String.valueOf(orderId));
        }

    }

}