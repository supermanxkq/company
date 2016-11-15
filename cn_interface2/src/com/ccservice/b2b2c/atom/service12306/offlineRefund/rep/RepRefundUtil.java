package com.ccservice.b2b2c.atom.service12306.offlineRefund.rep;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.LocalRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.MatchTicketUtil;

/**
 * 退票工具类
 * @author WH
 * @time 2015年7月10日 下午6:17:08
 * @version 1.0
 */

public class RepRefundUtil extends TongchengSupplyMethod {

    private static final String URL = "url";

    private static final String REP = "rep";

    private static final String listflag = "108";

    private static final String detailflag = "104";

    private static final int timeout = 2 * 60 * 1000;

    private static final String come_from_flag = "my_order";

    /**
     * 重置>>全部设为unknown
     */
    public JSONObject resetMatchData(List<Trainticket> orderTicketList) {
        //退票、改签不明确
        JSONArray unknown = new JSONArray();
        //全部设为unknown
        for (Trainticket ticket : orderTicketList) {
            //NEW
            JSONArray matchArray = new JSONArray();
            JSONObject matchObject = new JSONObject();
            //ADD
            matchArray.add(ticket.getId());
            matchObject.put("ticketIds", matchArray);
            //ADD
            unknown.add(matchObject);
        }
        //结果
        JSONObject retobj = new JSONObject();
        //PUT
        retobj.put("success", true);
        retobj.put("unknown", unknown);
        retobj.put("gaiqian", new JSONArray());
        retobj.put("tuipiao", new JSONArray());
        retobj.put("gaiqianAndtuikuan", new JSONArray());
        retobj.put("tuipiaoAndtuikuan", new JSONArray());
        return retobj;
    }

    /**
     * 12306数据匹配车票
     * 1、有退票或改签退款明细
     * 2、仅显示退票或改签
     * 3、退票或改签未显示
     * @param orderTicketList 订单车票 或 线上高改车票
     * @param listTicketMap 车票在12306信息
     * @param listInfoList  车票在12306信息
     */
    public JSONObject matchTicketBy12306Data(Trainorder order, Trainorderchange change,
            List<Trainticket> orderTicketList, String detailInfo, Map<Long, JSONObject> listTicketMap,
            List<JSONObject> listInfoList, Map<Long, Trainticket> idTicketMap) {
        //结果
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //退票、改签不明确
        JSONArray unknown = new JSONArray();
        //仅明确改签
        JSONArray gaiqian = new JSONArray();
        //仅明确退票
        JSONArray tuipiao = new JSONArray();
        //明确改签、退款
        JSONArray gaiqianAndtuikuan = new JSONArray();
        //明确退票、退款
        JSONArray tuipiaoAndtuikuan = new JSONArray();
        //工具类
        MatchTicketUtil matchTicketUtil = new MatchTicketUtil();
        //解析
        JSONObject repData = ElongHotelInterfaceUtil.StringIsNull(detailInfo) ? new JSONObject() : JSONObject
                .parseObject(detailInfo);
        //共匹配票数
        int totalMatched = 0;
        //已匹配的车票
        Map<Long, Boolean> matchedTicketMap = new HashMap<Long, Boolean>();
        //成功
        if (repData.getBooleanValue("success")) {
            //12306退款信息
            JSONArray refundInfo = repData.getJSONArray("RefundInfo");
            //12306订单详情
            JSONArray orderDetail = repData.getJSONArray("OrderDetail");
            //获取12306退款明细，key:交易号、value:退款明细JSON
            Map<String, JSONObject> allRefundMap = refundMap(refundInfo, false);//所有明细
            Map<String, JSONObject> tuikuanRefundMap = refundMap(refundInfo, true);//有退款的明细
            //退款信息不一致
            if (allRefundMap.size() != refundInfo.size()) {
                retobj.put("result", "12306退款明细的交易号可能存在重复");
                return retobj;
            }
            //12306退款JSON，key:交易号、value:多个车票JSON
            Map<String, List<JSONObject>> detailRefundMap = detailRefundMap(orderDetail, allRefundMap);
            //退款与交易号长度不一致
            if (detailRefundMap.size() != allRefundMap.size()) {
                retobj.put("result", "12306订单与退款交易号对应长度不一致，退款查询可能存在失败");
                return retobj;
            }
            //已匹配的交易号
            Map<String, Boolean> matchedTradeNoMap = new HashMap<String, Boolean>();
            //1、有退票或改签退款明细
            for (String trade_no : detailRefundMap.keySet()) {
                //无退款，跳过
                if (!tuikuanRefundMap.containsKey(trade_no)) {
                    continue;
                }
                JSONObject tuikuanRefund = tuikuanRefundMap.get(trade_no);
                //退款参数
                String transDate = tuikuanRefund.getString("transDate");
                String origin_name = tuikuanRefund.getString("origin_name");
                //单位：分
                float transAmount = tuikuanRefund.getFloatValue("transAmount");
                //金额转换为元
                transAmount = ElongHotelInterfaceUtil.floatMultiply(transAmount, 0.01f);
                //金额为0
                if (transAmount == 0) {
                    continue;
                }
                //退款错误
                if (transAmount < 0) {
                    retobj.put("result", "12306退款金额错误，交易号: " + trade_no + "，金额: " + transAmount);
                    return retobj;
                }
                //判断线下
                if (!"网下退票".equals(origin_name) && !"网下改签".equals(origin_name)) {
                    continue;
                }
                String operateTime = "";
                List<Long> matchTicketList = new ArrayList<Long>();
                List<JSONObject> detailRefundList = detailRefundMap.get(trade_no);
                //循环匹配本地车票
                for (JSONObject detail : detailRefundList) {
                    //非互联网
                    if (isoffline(detail.getString("office_name"))
                            && origin_name.equals(detail.getString("origin_name"))) {
                        //匹配车票
                        long ticketId = matchTicketUtil.matchTicket(order, change, detail, listInfoList).getId();
                        //匹配成功
                        if (ticketId > 0) {
                            matchTicketList.add(ticketId);
                            operateTime = detail.getString("operate_time");
                        }
                    }
                }
                //退款对应的多个退票全匹配上
                if (matchTicketList.size() == detailRefundList.size()) {
                    JSONArray matchArray = new JSONArray();
                    JSONObject matchObject = new JSONObject();
                    //循环
                    for (long ticketId : matchTicketList) {
                        totalMatched++;
                        matchArray.add(ticketId);
                        matchedTicketMap.put(ticketId, true);
                        matchedTradeNoMap.put(trade_no, true);
                    }
                    matchObject.put("transDate", transDate);
                    matchObject.put("ticketIds", matchArray);
                    matchObject.put("refundPrice", transAmount);
                    matchObject.put("operateTime", operateTime);
                    //ADD
                    if ("网下退票".equals(origin_name)) {
                        tuipiaoAndtuikuan.add(matchObject);
                    }
                    else if ("网下改签".equals(origin_name)) {
                        gaiqianAndtuikuan.add(matchObject);
                    }
                }
            }
            //2、仅显示退票或改签
            for (int i = 0; i < orderDetail.size(); i++) {
                JSONObject detail = orderDetail.getJSONObject(i);
                //参数
                String status = detail.getString("status");
                String trade_no = detail.getString("trade_no");
                String office_name = detail.getString("office_name");
                String status_code = detail.getString("status_code");
                String origin_name = detail.getString("origin_name");
                String operateTime = detail.getString("operate_time");
                //非互联网
                if (isoffline(office_name) && !matchedTradeNoMap.containsKey(trade_no)) {
                    //匹配车票
                    Trainticket ticket = matchTicketUtil.matchTicket(order, change, detail, listInfoList);
                    //车票ID
                    long ticketId = ticket.getId();
                    //车票状态
                    int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                    //匹配成功
                    if (ticketId > 0) {
                        boolean matchFlag = false;
                        //网下退票
                        if (offlineRefund(office_name, getStatusName(status_code, status), origin_name)) {
                            matchFlag = true;
                            //NEW
                            JSONArray matchArray = new JSONArray();
                            JSONObject matchObject = new JSONObject();
                            //ADD
                            matchArray.add(ticketId);
                            matchObject.put("ticketIds", matchArray);
                            matchObject.put("operateTime", operateTime);
                            //ADD
                            tuipiao.add(matchObject);
                        }
                        //网下改签
                        else if (changeType != 1 && changeType != 2
                                && offlineChange(office_name, getStatusName(status_code, status), origin_name)) {
                            matchFlag = true;
                            //NEW
                            JSONArray matchArray = new JSONArray();
                            JSONObject matchObject = new JSONObject();
                            //ADD
                            matchArray.add(ticketId);
                            matchObject.put("ticketIds", matchArray);
                            matchObject.put("operateTime", operateTime);
                            //ADD
                            gaiqian.add(matchObject);
                        }
                        if (matchFlag) {
                            totalMatched++;
                            matchedTicketMap.put(ticketId, true);
                        }
                    }
                }
            }
        }
        if (totalMatched != matchedTicketMap.size()) {
            retobj.put("result", "匹配的车票张数不一致[" + totalMatched + "][" + matchedTicketMap.size() + "]");
            return retobj;
        }
        //判断车票在12306状态
        for (long ticketId : listTicketMap.keySet()) {
            if (matchedTicketMap.containsKey(ticketId)) {
                continue;
            }
            JSONObject list = listTicketMap.get(ticketId);
            //车票
            Trainticket ticket = idTicketMap.get(ticketId);
            //车票状态
            int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
            //状态名称
            String ticket_status_name = list.getString("ticket_status_name");
            //网下退票，线上包含业务流水号
            if ("已退票".equals(ticket_status_name)) {
                //NEW
                JSONArray matchArray = new JSONArray();
                JSONObject matchObject = new JSONObject();
                //ADD
                matchArray.add(ticketId);
                matchObject.put("ticketIds", matchArray);
                //ADD
                tuipiao.add(matchObject);
                //PUT
                matchedTicketMap.put(ticketId, true);
            }
            //线下改签
            else if ("已改签".equals(ticket_status_name) && changeType != 1 && changeType != 2) {
                //NEW
                JSONArray matchArray = new JSONArray();
                JSONObject matchObject = new JSONObject();
                //ADD
                matchArray.add(ticketId);
                matchObject.put("ticketIds", matchArray);
                //ADD
                gaiqian.add(matchObject);
                //PUT
                matchedTicketMap.put(ticketId, true);
            }
        }
        for (Trainticket ticket : orderTicketList) {
            //ID
            long ticketId = ticket.getId();
            //未匹配
            if (!matchedTicketMap.containsKey(ticketId)) {
                //NEW
                JSONArray matchArray = new JSONArray();
                JSONObject matchObject = new JSONObject();
                //ADD
                matchArray.add(ticketId);
                matchObject.put("ticketIds", matchArray);
                //ADD
                unknown.add(matchObject);
                //PUT
                matchedTicketMap.put(ticketId, true);
            }
        }
        //票数不一样
        if (orderTicketList.size() != matchedTicketMap.size()) {
            retobj.put("result", "匹配完，与票张数不一致[" + orderTicketList.size() + "][" + matchedTicketMap.size() + "]");
            return retobj;
        }
        //PUT
        retobj.put("success", true);
        retobj.put("unknown", unknown);
        retobj.put("gaiqian", gaiqian);
        retobj.put("tuipiao", tuipiao);
        retobj.put("gaiqianAndtuikuan", gaiqianAndtuikuan);
        retobj.put("tuipiaoAndtuikuan", tuipiaoAndtuikuan);
        return retobj;
    }

    /**
     * 去结尾
     */
    public String deleteEndChar(String str, String end) {
        return str != null && end != null && str.endsWith(end) ? str.substring(0, str.length() - end.length()) : str;
    }

    /**
     * 12306退款车票
     */
    public Map<String, List<JSONObject>> detailRefundMap(JSONArray orderDetail, Map<String, JSONObject> allRefundMap) {
        //结果
        Map<String, List<JSONObject>> detailMap = new HashMap<String, List<JSONObject>>();
        //循环
        for (int i = 0; i < orderDetail.size(); i++) {
            JSONObject detail = orderDetail.getJSONObject(i);
            //退款交易号
            String trade_no = detail.getString("trade_no");
            //退款交易号非空
            if (!ElongHotelInterfaceUtil.StringIsNull(trade_no)) {
                //退款明细中不存在
                if (!allRefundMap.containsKey(trade_no)) {
                    return new HashMap<String, List<JSONObject>>();
                }
                //一个交易号可能对应多个JSON记录
                List<JSONObject> refundList = detailMap.get(trade_no);
                if (refundList == null) {
                    refundList = new ArrayList<JSONObject>();
                }
                //ADD
                refundList.add(detail);
                //PUT
                detailMap.put(trade_no, refundList);
            }
        }
        return detailMap;
    }

    /**
     * @param onlyHaveRefundPrice 只看有退款的
     * @reamrk 12306退款明细 key:交易号、value:退款明细JSON，车站改签可能有退款明细，但没具体金额，雷同于只知道改签了
     */
    public Map<String, JSONObject> refundMap(JSONArray RefundInfo, boolean onlyHaveRefundPrice) {
        //结果
        Map<String, JSONObject> tuikuanMap = new HashMap<String, JSONObject>();
        //循环
        for (int i = 0; i < RefundInfo.size(); i++) {
            //JSON
            JSONObject refund = RefundInfo.getJSONObject(i);
            //交易号
            String trade_no = refund.getString("trade_no");
            //要退款
            if (onlyHaveRefundPrice) {
                if (refund.containsKey("transAmount")) {
                    tuikuanMap.put(trade_no, refund);
                }
            }
            else {
                tuikuanMap.put(trade_no, refund);
            }
        }
        return tuikuanMap;
    }

    /**
     * 订单列表车票状态
     */
    public Map<String, JSONObject> ticketStatusIn12306(String listInfo) {
        Map<String, JSONObject> result = new HashMap<String, JSONObject>();
        //空
        if (ElongHotelInterfaceUtil.StringIsNull(listInfo)) {
            return result;
        }
        //JSON
        JSONObject obj = JSONObject.parseObject(listInfo);
        //获取到订单
        if (obj.getBooleanValue("success")) {
            //INFO
            String info = obj.getString("order");
            //JSON
            JSONObject OrderDTOData = JSONObject.parseObject(info);
            //车票数组
            JSONArray tickets = OrderDTOData.getJSONArray("tickets");
            //循环12306车票
            for (int i = 0; i < tickets.size(); i++) {
                JSONObject ticket = tickets.getJSONObject(i);
                //PUT
                result.put(ticket.getString("ticket_no"), ticket);
            }
        }
        return result;
    }

    /**
     * 获取订单列表、订单详情
     */
    public Map<String, String> get12306OrderInfo(Trainorder order, Customeruser user, int random) {
        Map<String, String> result = new HashMap<String, String>();
        //TRYCATCH
        try {
            //Util
            LocalRefundUtil localUtil = new LocalRefundUtil();
            //所有票
            List<Trainticket> orderTicketList = localUtil.orderTicket(order);
            //取车票
            Map<String, List<Trainticket>> map = localUtil.departTicket(orderTicketList);
            //线上改签
            List<Trainticket> onlineChangeList = localUtil.orderOnlineChanged(orderTicketList);
            //未发车
            List<Trainticket> G = map.containsKey("G") ? map.get("G") : new ArrayList<Trainticket>();
            //已发车
            List<Trainticket> H = map.containsKey("H") ? map.get("H") : new ArrayList<Trainticket>();
            //出错了
            if ((G.size() + H.size()) != orderTicketList.size()) {
                return result;
            }
            //REP
            String DetailInfo = "";
            String YiFaCheInfo = "";
            String WeiFaCheInfo = "";
            //未发车
            if (G.size() > 0) {
                Map<String, String> WeiFaChe = WeiFaChe(order, user, G, onlineChangeList, random);
                //REP结果
                String RepResult = WeiFaChe.get(REP);
                //用户未登录
                if (Account12306Util.accountNoLogin(RepResult, user)) {
                    //重新拿账号
                    user = getUserByOrder(order, user, random);
                    //重新拿12306数据
                    WeiFaChe = WeiFaChe(order, user, G, onlineChangeList, random);
                }
                WeiFaCheInfo = WeiFaChe.get(REP);
            }
            //已发车
            if (H.size() > 0) {
                Map<String, String> YiFaChe = YiFaChe(order, user, H, random);
                //REP结果
                String RepResult = YiFaChe.get(REP);
                //用户未登录
                if (Account12306Util.accountNoLogin(RepResult, user)) {
                    //重新拿账号
                    user = getUserByOrder(order, user, random);
                    //重新拿12306数据
                    YiFaChe = YiFaChe(order, user, H, random);
                }
                YiFaCheInfo = YiFaChe.get(REP);
            }
            //获取详情
            DetailInfo = orderDetail(order, user);
            //设置结果
            result.put("DetailInfo", DetailInfo);
            result.put("YiFaCheInfo", YiFaCheInfo);
            result.put("WeiFaCheInfo", WeiFaCheInfo);
        }
        catch (Exception e) {
            result = new HashMap<String, String>();
        }
        //释放账号
        finally {
            //账号系统，释放账号
            if (user != null && user.isFromAccountSystem()) {
                freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
        }
        //返回结果
        return result;
    }

    /**
     * 订单详情
     */
    private String orderDetail(Trainorder order, Customeruser user) throws Exception {
        //cookie
        String cookie = user.getCardnunber();
        //12306单号
        String sequence_no = order.getExtnumber();
        //订单详情
        JSONObject detailRequest = new JSONObject();
        detailRequest.put("cookie", cookie);
        detailRequest.put("sequence_no", sequence_no);
        //REP
        RepServerBean repServer = RepServerUtil.getRepServer(user, false);
        //请求参数
        String param = "datatypeflag=" + detailflag + "&jsonStr="
                + URLEncoder.encode(detailRequest.toString(), AccountSystem.UTF8)
                + JoinCommonAccountInfo(user, repServer);
        //请求REP
        String result = RequestUtil.post(repServer.getUrl(), param, AccountSystem.UTF8, AccountSystem.NullMap, timeout);
        //用户未登录
        if (result.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
            //切换REP
            repServer = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
            //类型正确
            if (repServer.getType() == 1) {
                //重拼参数
                param = "datatypeflag=" + detailflag + "&jsonStr="
                        + URLEncoder.encode(detailRequest.toString(), AccountSystem.UTF8)
                        + JoinCommonAccountInfo(user, repServer);
                //重新请求
                result = RequestUtil
                        .post(repServer.getUrl(), param, AccountSystem.UTF8, AccountSystem.NullMap, timeout);
            }
        }
        //返回结果
        return result;
    }

    /**
     * 未发车，按订票日期查询
     */
    private Map<String, String> WeiFaChe(Trainorder order, Customeruser user, List<Trainticket> G,
            List<Trainticket> onlineChangeList, int random) throws Exception {
        Map<String, String> result = new HashMap<String, String>();
        //cookie
        String cookie = user.getCardnunber();
        //12306单号
        String sequence_no = order.getExtnumber();
        //开始日期
        String currentDate = ElongHotelInterfaceUtil.getCurrentDate();
        String queryStartDate = order.getCreatetime().toString().split(" ")[0];
        //订单列表
        JSONObject listRequest = new JSONObject();
        //请求参数
        listRequest.put("cookie", cookie);
        listRequest.put("sequence_no", sequence_no);
        listRequest.put("queryStartDate", queryStartDate);
        listRequest.put("query_where", "G");//G：未发车；H：已发车
        listRequest.put("queryType", "1");//1：按订票日期查询；2：按乘车日期查询
        listRequest.put("passanger_name", G.get(0).getTrainpassenger().getName());//乘客姓名，用于关键字查询
        listRequest.put("come_from_flag", come_from_flag);//查询类型，my_order：全部；my_resign：可改签； my_refund：可退票
        listRequest.put("queryEndDate", onlineChangeList.size() > 0 ? currentDate : queryStartDate);//存在改签，取当天，否则取订票日期
        //REP
        RepServerBean repServer = RepServerUtil.getRepServer(user, false);
        //请求参数
        String param = "datatypeflag=" + listflag + "&jsonStr="
                + URLEncoder.encode(listRequest.toString(), AccountSystem.UTF8)
                + JoinCommonAccountInfo(user, repServer);
        //请求REP
        String rep = RequestUtil.post(repServer.getUrl(), param, AccountSystem.UTF8, AccountSystem.NullMap, timeout);
        //用户未登录
        if (rep.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
            //切换REP
            repServer = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
            //类型正确
            if (repServer.getType() == 1) {
                //重拼参数
                param = "datatypeflag=" + listflag + "&jsonStr="
                        + URLEncoder.encode(listRequest.toString(), AccountSystem.UTF8)
                        + JoinCommonAccountInfo(user, repServer);
                //重新请求
                rep = RequestUtil.post(repServer.getUrl(), param, AccountSystem.UTF8, AccountSystem.NullMap, timeout);
            }
        }
        //设置结果
        result.put(REP, rep);
        result.put(URL, repServer.getUrl());
        //返回结果
        return result;
    }

    /**
     * 已发车，按乘车日期查询
     */
    private Map<String, String> YiFaChe(Trainorder order, Customeruser user, List<Trainticket> H, int random)
            throws Exception {
        Map<String, String> result = new HashMap<String, String>();
        //cookie
        String cookie = user.getCardnunber();
        //12306单号
        String sequence_no = order.getExtnumber();
        //Util
        LocalRefundUtil localUtil = new LocalRefundUtil();
        //查询日期
        String queryEndDate = localUtil.minOrMaxDpartTime(H, 2);
        String queryStartDate = localUtil.minOrMaxDpartTime(H, 1);
        //订单列表
        JSONObject listRequest = new JSONObject();
        //请求参数
        listRequest.put("cookie", cookie);
        listRequest.put("sequence_no", sequence_no);
        listRequest.put("queryEndDate", queryEndDate);
        listRequest.put("queryStartDate", queryStartDate);
        listRequest.put("query_where", "H");//G：未发车；H：已发车
        listRequest.put("queryType", "2");//1：按订票日期查询；2：按乘车日期查询
        listRequest.put("passanger_name", H.get(0).getTrainpassenger().getName());//乘客姓名，用于关键字查询
        listRequest.put("come_from_flag", come_from_flag);//查询类型，my_order：全部；my_resign：可改签； my_refund：可退票
        //REP
        RepServerBean repServer = RepServerUtil.getRepServer(user, false);
        //请求参数
        String param = "datatypeflag=" + listflag + "&jsonStr="
                + URLEncoder.encode(listRequest.toString(), AccountSystem.UTF8)
                + JoinCommonAccountInfo(user, repServer);
        //请求REP
        String rep = RequestUtil.post(repServer.getUrl(), param, AccountSystem.UTF8, AccountSystem.NullMap, timeout);
        //用户未登录
        if (rep.contains("用户未登录") && RepServerUtil.changeRepServer(user)) {
            //切换REP
            repServer = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
            //类型正确
            if (repServer.getType() == 1) {
                //重拼参数
                param = "datatypeflag=" + listflag + "&jsonStr="
                        + URLEncoder.encode(listRequest.toString(), AccountSystem.UTF8)
                        + JoinCommonAccountInfo(user, repServer);
                //重新请求
                rep = RequestUtil.post(repServer.getUrl(), param, AccountSystem.UTF8, AccountSystem.NullMap, timeout);
            }
        }
        //设置结果
        result.put(REP, rep);
        result.put(URL, repServer.getUrl());
        //返回结果
        return result;
    }

    /**
     * 通过订单拿账号
     * @param order 订单
     * @param user 下单账号，存在时表示账号未登录，先释放再拿账号
     */
    public Customeruser getUserByOrder(Trainorder order, Customeruser user, int random) {
        try {
            //不重试
            if (user != null && user.isDontRetryLogin()) {
                return user;
            }
            //下单用户名
            String createAccount = order.getSupplyaccount();
            //下单用户为空
            if (ElongHotelInterfaceUtil.StringIsNull(createAccount)) {
                return new Customeruser();
            }
            //重新登录用户
            if (user != null && !ElongHotelInterfaceUtil.StringIsNull(user.getLoginname())) {
                //账号系统，先释放
                freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
                //账号系统，重新获取
                return getCustomeruserBy12306Account(order, random, true);
            }
            else {
                return getCustomeruserBy12306Account(order, random, true);
            }
        }
        catch (Exception e) {
            return new Customeruser();
        }
    }

    /**
     * 未出票判断，在线退票按未出票
     */
    public boolean weiChuPiao(JSONObject obj) {
        //状态
        String ticket_status_name = obj.getString("ticket_status_name");
        ticket_status_name = ticket_status_name == null ? "" : ticket_status_name.trim();
        //未出票 或 特殊
        return "已支付".equals(ticket_status_name) || "改签票".equals(ticket_status_name)
                || ticket_status_name.contains("进站") || ticket_status_name.contains("出站")
                || "变更到站票".equals(ticket_status_name);
    }

    /**
     * 可按退票处理
     * @param ticket 本地车票
     * @param obj 12306订单列表信息
     */
    public boolean canGoRefund(JSONObject obj, Trainticket ticket) {
        //结果
        boolean canGoRefund = false;
        //车票状态
        int status = ticket.getStatus();
        //已出票、改签完成、无法退票
        if (status == Trainticket.ISSUED || status == Trainticket.FINISHCHANGE || status == Trainticket.NONREFUNDABLE) {
            //判断12306
            canGoRefund = "已出票".equals(obj.getString("ticket_status_name")) || "N".equals(obj.getString("return_flag"));
            //判断本地车票
            if (!canGoRefund) {
                //发车时间
                String departTime = ticket.getDeparttime();
                //车票状态
                int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType();
                //已线上改签、改签退已审核
                if (changeType == 1 || changeType == 2) {
                    departTime = ticket.getTtcdeparttime();
                }
                //判断已过发车时间
                try {
                    //格式化
                    SimpleDateFormat shiFenFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    //开车Long时间
                    long trainStartTime = shiFenFormat.parse(departTime).getTime();
                    //开车时间-当前时间
                    long timesub = trainStartTime - System.currentTimeMillis();
                    //距发车时间30分钟以上才能在线退票
                    canGoRefund = timesub < 30 * 60 * 1000;
                }
                catch (Exception e) {

                }
            }
        }
        //返回结果
        return canGoRefund;
    }

    /**
     * 线下退票
     */
    public boolean offlineRefund(String office_name, String statusName, String origin_name) {
        return isoffline(office_name) && !"网上退票".equals(origin_name)
                && ("办理退票".equals(statusName) || "网下退票".equals(origin_name) || "出票后退票".equals(statusName));
    }

    /**
     * 线下改签
     */
    public boolean offlineChange(String office_name, String statusName, String origin_name) {
        return isoffline(office_name) && !"网上改签".equals(origin_name)
                && ("办理改签".equals(statusName) || "网下改签".equals(origin_name) || "出票后改签".equals(statusName));
    }

    /**
     * 线下
     */
    public boolean isoffline(String office_name) {
        return !"互联网".equals(officeNameFormat(office_name));
    }

    /**
     * 车站名称
     * @param f office_name-->12306车站名称
     */
    public String officeNameFormat(String f) {
        String e = f;
        if (e.indexOf("网售") > 0) {
            return "互联网";
        }
        else {
            return e;
        }
    }

    /**
     * 12306显示的车票状态
     * @param status_code-->12306状态编码
     * @param status-->12306状态中文
     */
    public String getStatusName(String status_code, String status) {
        String f = "";
        if ("a".equals(status_code)) {
            f = "支付成功";
        }
        else {
            if ("b".equals(status_code)) {
                f = "制票成功";
            }
            else {
                if ("c".equals(status_code)) {
                    f = "办理退票";
                }
                else {
                    if ("d".equals(status_code)) {
                        f = "办理改签";
                    }
                    else {
                        if ("f".equals(status_code)) {
                            f = "改签成功";
                        }
                        else {
                            if ("l".equals(status_code)) {
                                f = "检票进站";
                            }
                            else {
                                if ("m".equals(status_code)) {
                                    f = "检票出站";
                                }
                                else {
                                    if ("p".equals(status_code)) {
                                        f = "已变更到站";
                                    }
                                    else {
                                        if ("r".equals(status_code)) {
                                            f = "变更到站票";
                                        }
                                        else {
                                            if ("q".equals(status_code)) {
                                                f = "办理改签";
                                            }
                                            else {
                                                f = status;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return f;
    }

}