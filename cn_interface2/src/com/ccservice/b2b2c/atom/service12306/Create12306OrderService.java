package com.ccservice.b2b2c.atom.service12306;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.account.dubbo.util.DubboConsumer;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.service12306.bean.TrainOrderReturnBean;
import com.ccservice.b2b2c.atom.service12306.bean.TrainTicketReturnBean;
import com.ccservice.b2b2c.atom.service12306.thread.MyThreadSaveRefundOnlineDisable;
import com.ccservice.b2b2c.atom.service12306.mem.TrainAutoCreateOrderMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.b2b2c.util.RepLogUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 12306下单
 * @author WH
 */

public class Create12306OrderService extends TongchengSupplyMethod {

    private static final String datatypeflag = "13";//REP接口标识

    private static final String datatypeflagPhone = "1013";//REP接口标识

    private final static String SYSTEM_CONFIG_KEY = "IsNewWEB";

    /**
     * 下单12306
     * @param orderId 订单ID，TrainOrder.ID，没生成订单前，可传0，仅用于记录日志
     * @param train_date 乘车日期 yyyy-MM-dd
     * @param from_station 出发站三字码，可为空，建议传值
     * @param to_station 到达站三字码，可为空，建议传值
     * @param from_station_name 出发站名称
     * @param to_station_name 到达站名称
     * @param train_code 车次，如G101
     * @param passengers 乘客信息 JSONArray
     * >> 格式如：[{"ticket_type":"票类型","price":票价,"zwcode":"座位编码","passenger_id_type_code":"证件类型","passenger_name":"乘客姓名","passenger_id_no":"证件号"}]
     * >> ticket_type：1:成人票，2:儿童票，3:学生票，4:残军票
     * >> price：float类型
     * >> zwcode：9:商务座，P:特等座，M:一等座，O:二等座，8：二等软座，7：一等软座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
     * >> passenger_id_type_code：1:二代身份证，C:港澳通行证，G:台湾通 行证，B:护照
     * >> 均需有值
     * @return 下单结果
     */
    /**
     * 12306乘客参数拼法
     * ticket_type>>>1:成人票，2:儿童票，3:学生票，4:残军票，儿童乘客取大人，只用"_ "表示
     * var d = c.name + "," + c.id_type + "," + c.id_no + "," + c.passenger_type;
     * e += d + "_"
     * 
     * 12306车票参数拼法
     * getpassengerTickets = function() {
     *      var c = "";
     *      for (var b = 0; b < limit_tickets.length; b++) {
     *          var a = limit_tickets[b].seat_type + ",0," + limit_tickets[b].ticket_type + "," + limit_tickets[b].name + "," + 
     *                  limit_tickets[b].id_type + "," + limit_tickets[b].id_no + "," + 
     *                  (limit_tickets[b].phone_no == null ? "": limit_tickets[b].phone_no) + "," + 
     *                  (limit_tickets[b].save_status == "" ? "N": "Y");
     *          c += a + "_"
     *          }
     *      return c.substring(0, c.length - 1)
     * };
     */
    public TrainOrderReturnBean operate(long orderId, String train_date, String from_station, String to_station,
            String from_station_name, String to_station_name, String train_code, String passengers,
            Customeruser customeruser) {

        boolean isPhone = isPhoneOrder(orderId);
        if (!isPhone) {
            isPhone = changePhoneOrder(orderId);
        }
        int IsNewWebType = 0;
        if (!isPhone) {
            TrainAutoCreateOrderMethod.getIsAutoCreateOrder();//修改内存
            try {
                String result = getSystemConfig(SYSTEM_CONFIG_KEY);
                IsNewWebType = Integer.parseInt(result);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //查询是否需要下单过程中绑定乘客
        boolean isBindingPassengers = isBindingPassengers(orderId);
        orderId = orderId <= 0 ? new Random().nextInt(900000) + 100000 : orderId;
        //车站三字码
        to_station = getThree(to_station, to_station_name);
        from_station = getThree(from_station, from_station_name);
        //订单类型，1：去哪儿
        String orderType = "";
        //乘客信息
        JSONObject reqpassengers = new JSONObject();
        //粗略验证请求参数
        String msg = "";
        if (ElongHotelInterfaceUtil.StringIsNull(train_date)) {
            msg = "乘车日期为空。";
        }
        else if (ElongHotelInterfaceUtil.StringIsNull(from_station_name)) {
            msg = "出发站名称为空。";
        }
        else if (ElongHotelInterfaceUtil.StringIsNull(to_station_name)) {
            msg = "到达站名称为空。";
        }
        else if (ElongHotelInterfaceUtil.StringIsNull(from_station)) {
            msg = "出发站简码为空。";
        }
        else if (ElongHotelInterfaceUtil.StringIsNull(to_station)) {
            msg = "到达站简码为空。";
        }
        else if (ElongHotelInterfaceUtil.StringIsNull(train_code)) {
            msg = "车次为空。";
        }
        else if (from_station.equals(to_station) || from_station_name.equals(to_station_name)) {
            msg = "出发站、到达站相同。";
        }
        else if (ElongHotelInterfaceUtil.StringIsNull(passengers)) {
            msg = "乘客信息为空。";
        }
        else if (customeruser == null || ElongHotelInterfaceUtil.StringIsNull(customeruser.getCardnunber())) {
            msg = "获取下单账户失败。";
        }
        else {
            String ERROR = "-----ERROR-----";
            try {
                String prices = "";
                String zwcodes = "";
                String oldPassengerStr = "";
                String passengerTicketStr = "";
                int isQunarCount = 0;
                JSONArray passengerList = JSONArray.parseArray(passengers);
                int passengerSize = passengerList.size();
                for (int i = 0; i < passengerSize; i++) {
                    JSONObject passenger = passengerList.getJSONObject(i);
                    String zwcode = passenger.getString("zwcode");
                    String passenger_name = passenger.getString("passenger_name");
                    String ticket_type = passenger.getString("ticket_type");
                    String passenger_id_type_code = passenger.getString("passenger_id_type_code");
                    String passenger_id_no = passenger.getString("passenger_id_no");
                    float price = passenger.getFloatValue("price");
                    //去哪儿订单标识,2014.12.31修改，避免打jar包，暂定放在乘客信息里
                    if (passenger.containsKey("isqunarorder") && passenger.getBooleanValue("isqunarorder")) {
                        isQunarCount++;
                    }
                    if (ElongHotelInterfaceUtil.StringIsNull(zwcode)) {
                        throw new Exception(ERROR + "座席编码为空。");
                    }
                    zwcode = zwcode.trim();
                    if (ElongHotelInterfaceUtil.StringIsNull(passenger_name)
                            || ElongHotelInterfaceUtil.StringIsNull(passenger_id_type_code)
                            || ElongHotelInterfaceUtil.StringIsNull(passenger_id_no)) {
                        throw new Exception(ERROR + "乘客信息存在空。");
                    }
                    if (ElongHotelInterfaceUtil.StringIsNull(ticket_type)) {
                        throw new Exception(ERROR + "票种类型为空。");
                    }
                    ticket_type = ticket_type.trim();
                    //ticket_type>>>1:成人票，2:儿童票，3:学生票，4:残军票
                    if (!"1".equals(ticket_type) && !"2".equals(ticket_type) && !"3".equals(ticket_type)
                            && !"4".equals(ticket_type)) {
                        throw new Exception(ERROR + "票种类型[" + ticket_type + "]错误。");
                    }
                    //                    if (price <= 0) {
                    //                        throw new Exception(ERROR + "票价[" + price + "]错误。");
                    //                    }
                    prices += price + "@";
                    zwcodes += zwcode + "@";
                    if ("2".equals(ticket_type)) {
                        oldPassengerStr += "_ ";
                    }
                    else {
                        oldPassengerStr += passenger_name + "," + passenger_id_type_code + "," + passenger_id_no + ","
                                + ticket_type + "_";
                    }
                    //无座，12306下单参数为硬座
                    passengerTicketStr += ("0".equals(zwcode) ? "1" : zwcode) + ",0," + ticket_type + ","
                            + passenger_name + "," + passenger_id_type_code + "," + passenger_id_no + ",,N_";
                }
                reqpassengers.put("prices", prices.substring(0, prices.length() - 1));
                reqpassengers.put("zwcodes", zwcodes.substring(0, zwcodes.length() - 1));
                reqpassengers.put("oldPassengerStr", oldPassengerStr);//童婉玲,1,350781199511304020,1_
                passengerTicketStr = passengerTicketStr.substring(0, passengerTicketStr.length() - 1);
                reqpassengers.put("passengerTicketStr", passengerTicketStr);
                //去哪儿
                if (isQunarCount == passengerSize) {
                    orderType = "1";
                }
            }
            catch (Exception e) {
                String exception = e.getMessage();
                if (exception.startsWith(ERROR)) {
                    msg = exception.substring(ERROR.length());
                }
                else {
                    msg = "乘客信息错误。";
                }
                WriteLog.write("t同程火车票接口_4.5申请分配座位席别", orderId + ">>>>>乘客信息错误>>>>>" + passengers + ">>>>>" + exception);
            }
        }
        //返回
        TrainOrderReturnBean returnBean = new TrainOrderReturnBean();
        //判断
        if (!ElongHotelInterfaceUtil.StringIsNull(msg)) {
            returnBean.setSuccess(false);
            returnBean.setMsg(msg);
            return returnBean;
        }
        //封装请求JSON参数
        JSONObject reqobj = new JSONObject();
        reqobj.put("loginName", customeruser.getLoginname() == null ? "" : customeruser.getLoginname());
        reqobj.put("loginPwd", customeruser.getLogpassword() == null ? "" : customeruser.getLogpassword());
        reqobj.put("orderId", orderId);//用于日志记录
        reqobj.put("cookie", customeruser.getCardnunber());//cookie
        reqobj.put("train_date", train_date);//乘车日期
        reqobj.put("from_station", from_station);//出发站编码
        reqobj.put("to_station", to_station);//到达站编码
        reqobj.put("from_station_name", from_station_name);//出发站名
        reqobj.put("to_station_name", to_station_name);//到达站名
        reqobj.put("train_code", train_code);//车次
        reqobj.put("orderType", orderType);//1：去哪儿订单
        reqobj.put("queryLink", PropertyUtil.getValue("queryTicketLink"));//查询车票链接，防止12306变数据，从cn_interface传入
        reqobj.put("seatTypeOf12306", get12306SeatTypes());//12306座席
        reqobj.put("passengers", reqpassengers.toJSONString());
        if (isPhone) {
            reqobj.put("passengers", reqpassengers);
        }
        //不管手机端 WEB端都传这个参数  但只有WEB端用到，因为下面手机端下单返回未登录时会切换到WEB端
        if (IsNewWebType == 0) {
            reqobj.put("isAutoCreateOrder", false);//不使用新版WEB下单
        }
        else if (IsNewWebType == 1) {
            reqobj.put("isAutoCreateOrder", true);//使用新版WEB下单
        }
        else {
            reqobj.put("isAutoCreateOrder", false);//不使用新版WEB下单
        }
        reqobj.put("isBindingPassengers", isBindingPassengers);//查询是否需要下单过程中绑定乘客
        reqobj.put("goAliOcs", "true".equals(PropertyUtil.getValue("goAliOcs")));//#走阿里Ocs true走 false不走
        //客人账号标识
        reqobj.put("CustomerAccount", customeruser.isCustomerAccount());
        //针对淘宝客人账号已绑定乘客，添加乘客未通过时继续下单
        reqobj.put("dontBindingPassengers", "DontBindingPassengersIsTrue".equals(customeruser.getWorkphone()));
        //记录日志
        long start = System.currentTimeMillis();
        WriteLog.write("12306_4.5申请分配座位席别", orderId + ">>>>>请求REP参数>>>>>" + reqobj.toJSONString());
        //请求REP
        String repUrl = "";
        String repName = "";
        String retdata = "";
        JSONObject repobj = new JSONObject();
        RepServerBean rep = new RepServerBean();

        String accountPhone = "";
        String needJson = "";
        String normal_passengers = "";
        String searchTrain = "";
        String otherMessage = "";
        try {
            //获取REP
            rep = RepServerUtil.getRepServer(customeruser, true);
            //REP地址
            repUrl = rep.getUrl();
            WriteLog.write("12306_4.5申请分配座位席别", orderId + ">>>>>请求repUrl>>>>>" + repUrl);
            repName = rep.getName();
            if (ElongHotelInterfaceUtil.StringIsNull(repUrl)) {
                returnBean.setSuccess(false);
                if (rep.getId() == -1) {
                    retdata = "下单服务器超负荷。";
                }
                else {
                    retdata = "获取下单服务器失败。";
                }
                returnBean.setMsg(retdata);
                return returnBean;
            }
            //请求参数URLEncoder
            String jsonStr = URLEncoder.encode(reqobj.toJSONString(), "UTF-8");
            //请求参数
            String param = "datatypeflag=" + (isPhone ? datatypeflagPhone : datatypeflag) + "&jsonStr=" + jsonStr
                    + JoinCommonAccountInfo(customeruser, rep);
            String accoutPhone = "";
            if (isPhone) {
                //                repUrl = repUrl.replace("traininit", "MobileClient");
                accoutPhone = JoinCommonAccountPhone(customeruser);
                param += accoutPhone;
                WriteLog.write("12306_4.5申请分配座位席别_PHONE", orderId + ">>>>>请求repUrl>>>>>" + repUrl + ">>>>>请求REP参数>>>>>"
                        + param);
            }
            //设置6分钟超时时间
            int timeout = 6 * 60 * 1000;
            //请求REP
            retdata = SendPostandGet.submitPostTimeOutFiendOperate(repUrl, param, "UTF-8", timeout).toString();
            if (isPhone) {
                //下单后刷新手机端DB配置
                freshPhone(accoutPhone, retdata, customeruser);
            }
            //用户未登录
            if (retdata.contains("用户未登录") && RepServerUtil.changeRepServer(customeruser)) {
                //切换REP
                rep = RepServerUtil.getTaoBaoTuoGuanRepServer(customeruser, true);
                //类型正确
                if (rep.getType() == 1) {
                    //REP地址
                    repUrl = rep.getUrl();
                    repName = rep.getName();
                    //记录日志
                    WriteLog.write("12306_4.5申请分配座位席别", orderId + ">>>>>切换repUrl>>>>>" + repUrl);
                    //重拼参数
                    param = "datatypeflag=" + datatypeflag + "&jsonStr=" + jsonStr
                            + JoinCommonAccountInfo(customeruser, rep);
                    //重新请求
                    retdata = SendPostandGet.submitPostTimeOutFiendOperate(repUrl, param, "UTF-8", timeout).toString();
                }
            }
            //返回数据
            repobj = JSONObject.parseObject(retdata);
            if (retdata != null && !retdata.isEmpty()) {
                JSONObject repobjSplit = analysisJSON(retdata);
                if (repobjSplit.size() > 0) {
                    if (repobjSplit.containsKey("accountPhone")) {
                        accountPhone = repobjSplit.getString("accountPhone");
                    }
                    if (repobjSplit.containsKey("needJson")) {
                        needJson = repobjSplit.getString("needJson");
                    }
                    if (repobjSplit.containsKey("normal_passengers")) {
                        normal_passengers = repobjSplit.getString("normal_passengers");
                    }
                    if (repobjSplit.containsKey("searchTrain")) {
                        searchTrain = repobjSplit.getString("searchTrain");
                    }
                    if (repobjSplit.containsKey("otherMessage")) {
                        otherMessage = repobjSplit.getString("otherMessage");
                    }
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("Err_Create12306OrderService_operate", e);
            if (ElongHotelInterfaceUtil.StringIsNull(retdata)) {
                retdata = "ERROR：" + e.getMessage();
            }
        }
        finally {
            long sub = System.currentTimeMillis() - start;
            //日志
            WriteLog.write("12306_4.5申请分配座位席别mainLog", orderId + ">>>>>[ 消耗时间：" + sub / 1000 + "秒(" + sub
                    + ") ]>>>>>REP服务器地址>>>>>" + repUrl + "[" + repName + "]>>>>>REP返回数据>>>>>  normal_passengers------>"
                    + normal_passengers + ";accountPhone------>" + accountPhone + ";needJson------>" + needJson
                    + ";searchTrain------>" + searchTrain/*+
                                                         ";otherMessage------>"+otherMessage*/);
            WriteLog.write("12306_4.5申请分配座位席别", orderId + ">>>>>[ 消耗时间：" + sub / 1000 + "秒(" + sub
                    + ") ]>>>>>REP服务器地址>>>>>" + repUrl + "[" + repName + "]>>>>>REP返回数据>>>>>" + otherMessage);
        }
        //返回
        if (repobj == null || !repobj.containsKey("success")) {
            returnBean.setSuccess(false);
            returnBean.setMsg("下单失败。");
            return returnBean;
        }
        //刷新
        refreshAccountPassenger(repobj, customeruser);
        /**
         * retobj.put("success", false);
         * retobj.put("msg", "REP返回数据");
         */
        if (!repobj.getBooleanValue("success")) {
            returnBean.setSuccess(false);
            returnBean.setCode(ElongHotelInterfaceUtil.getJsonString(repobj, "code"));//REP中，暂时无余票错误存在值
            returnBean.setMsg(repobj.getString("msg"));
            returnBean.setRuntime(ElongHotelInterfaceUtil.getJsonString(repobj, "lishi"));
            sendThread2RepMsg(rep, repobj.getString("msg"));
            return returnBean;
        }
        try {
            //重新登陆cookie
            String retcookie = ElongHotelInterfaceUtil.getJsonString(repobj, "cookie");
            //不为空，表示下单重新获取了cookie
            if (!ElongHotelInterfaceUtil.StringIsNull(retcookie) && customeruser.getId() > 0) {
                customeruser.setState(1);
                customeruser.setCardnunber(retcookie);
                //Server.getInstance().getMemberService().updateCustomeruserIgnoreNull(customeruser);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        insertOCS(from_station_name, to_station_name, from_station, to_station, train_date);
        String sequence_no = repobj.getString("sequence_no");
        returnBean.setSuccess(true);
        returnBean.setSequence_no(sequence_no);
        returnBean.setJson(repobj.getString("12306"));
        returnBean.setMsg("提交订单成功，12306订单号为" + sequence_no + "。");
        returnBean.setRefundOnline(repobj.getIntValue("refund_online"));
        if (repobj.containsKey("needJson")) {
            returnBean.setNeedJson(repobj.getString("needJson"));
        }
        if (repobj.containsKey("searchTrain")) {
            returnBean.setSearchTrain(repobj.getString("searchTrain"));
        }
        //如果是不允许线上退票
        if (repobj.containsKey("refund_online") && 1 == repobj.getIntValue("refund_online")) {
            try {
                new MyThreadSaveRefundOnlineDisable(repobj.getString("12306")).start();
            }
            catch (Exception e) {
            }
        }
        //解析数据
        return parse12306(repobj, sequence_no, returnBean);
    }

    /**
     * @author zhaohongbo
     * @param from_station_name
     * @param to_station_name
     * @param from_station
     * @param to_station
     * @param train_date
     */
    private void insertOCS(String from_station_name, String to_station_name, String from_station, String to_station,
            String train_date) {
        try {
            SimpleDateFormat data = new SimpleDateFormat("HHmm");
            String newdate = data.format((new Date()));
            String key = "searchIDAccount_" + newdate;
            String str = from_station_name + "@" + to_station_name + "@" + from_station + "@" + to_station + "@"
                    + train_date;
            String result = OcsMethod.getInstance().get(key);
            if (result == null || result.trim().equals("")) {
                JSONArray array = new JSONArray();
                array.add(str);
                OcsMethod.getInstance().add(key, array.toString(), 180);
            }
            else {
                JSONArray json = JSONArray.parseArray(result);
                json.add(str);
                OcsMethod.getInstance().replace(key, json.toString(), 180);
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * @TODO 拆分jsonlog
     * <p>
     * @param jsonLog
     * @return
     * @throws Exception
     * <p>
     * @time:2016年5月10日  下午2:29:25
     * <p>
     * @author  fengfh
     */
    private static JSONObject analysisJSON(String jsonLog) throws Exception {
        String logname = "拆分json";
        if (jsonLog == null || jsonLog.isEmpty()) {
            WriteLog.write("501_error_" + logname, "参数非空验证 参数-->jsonLog:" + jsonLog);
            return new JSONObject();
        }
        JSONObject repobj = null;
        repobj = JSONObject.parseObject(jsonLog);

        JSONObject returnJson = new JSONObject();
        if (repobj.containsKey("accountPhone")) {
            JSONObject accountPhone = repobj.getJSONObject("accountPhone");
            repobj.remove("accountPhone");
            returnJson.put("accountPhone", accountPhone);
        }
        if (repobj.containsKey("needJson")) {
            JSONObject needJson = repobj.getJSONObject("needJson");
            repobj.remove("needJson");
            returnJson.put("needJson", needJson);
        }
        if (repobj.containsKey("normal_passengers")) {
            JSONArray normal_passengers = repobj.getJSONArray("normal_passengers");
            repobj.remove("normal_passengers");
            returnJson.put("normal_passengers", normal_passengers);
        }
        if (repobj.containsKey("searchTrain")) {
            JSONObject searchTrain = repobj.getJSONObject("searchTrain");
            repobj.remove("searchTrain");
            returnJson.put("searchTrain", searchTrain);
        }
        if (repobj.size() > 0) {
            returnJson.put("otherMessage", repobj);
        }
        return returnJson;
    }

    /**
     * 刷新账号乘客数量
     * @param reqobj 下单结果
     * @param customeruser 下单账号
     */
    private void refreshAccountPassenger(JSONObject repobj, Customeruser customeruser) {
        try {
            String accountName = customeruser.getLoginname();
            //乘客数量
            int accountPassengerCount = repobj.getIntValue("accountPassengerCount");
            //非客人账号、乘客数量大于0
            if (!customeruser.isCustomerAccount() && accountPassengerCount > 0
                    && !ElongHotelInterfaceUtil.StringIsNull(accountName)) {
                //刷新乘客
                JSONObject data = AccountSystemParam("RefreshAccount");
                //请求JSON
                data.put("name", accountName);
                data.put("count", accountPassengerCount);
                data.put("normal_passengers", repobj.getJSONArray("normal_passengers"));
                //Dubbo
                DubboConsumer.getInstance().getDubboAccount().refresh12306Account(data);
                /*
                //请求参数
                String param = "param=" + data.toJSONString();
                //请求地址
                String url = GetAccountSystemUrl();
                //地址非空
                if (!ElongHotelInterfaceUtil.StringIsNull(url)) {
                    RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), 5000);
                }
                */
            }
        }
        catch (Exception e) {

        }
    }

    //12306订单列表数据
    private TrainOrderReturnBean parse12306(JSONObject repobj, String sequence_no, TrainOrderReturnBean returnBean) {
        try {
            JSONObject obj12306 = new JSONObject();
            String orderHtml = repobj.getString("12306");
            JSONArray orderDBList = JSONObject.parseObject(orderHtml).getJSONObject("data").getJSONArray("orderDBList");
            for (int i = 0; i < orderDBList.size(); i++) {
                JSONObject temp12306 = orderDBList.getJSONObject(i);
                if (sequence_no.equals(temp12306.getString("sequence_no"))) {
                    obj12306 = temp12306;
                    break;
                }
            }
            returnBean.setOrder_date(ElongHotelInterfaceUtil.getJsonString(obj12306, "order_date"));
            /**
             * "start_time_page": "21:23",
             * "arrive_time_page": "09:12",
             */
            //发车时间、到达时间，如21:23
            returnBean.setStart_time(ElongHotelInterfaceUtil.getJsonString(obj12306, "start_time_page"));
            returnBean.setArrive_time(ElongHotelInterfaceUtil.getJsonString(obj12306, "arrive_time_page"));
            //运行时间
            returnBean.setRuntime(ElongHotelInterfaceUtil.getJsonString(repobj, "lishi"));
            //12306总价
            returnBean.setTotalPrice(obj12306.getFloatValue("ticket_total_price_page"));
            //车票信息
            List<TrainTicketReturnBean> ticketBeanList = new ArrayList<TrainTicketReturnBean>();
            JSONArray tickets = obj12306.getJSONArray("tickets");
            for (int i = 0; i < tickets.size(); i++) {
                TrainTicketReturnBean ticketBean = new TrainTicketReturnBean();
                //车票信息
                JSONObject ticket = tickets.getJSONObject(i);
                //证件号，标识，用于对应请求数据的乘客
                String passenger_id_no = ticket.getJSONObject("passengerDTO").getString("passenger_id_no");
                //SET
                ticketBean.setPassenger_id_no(passenger_id_no);
                ticketBean.setTicket_no(ticket.getString("ticket_no"));
                ticketBean.setCoach_no(ticket.getString("coach_no"));
                ticketBean.setCoach_name(ticket.getString("coach_name"));
                ticketBean.setSeat_no(ticket.getString("seat_no"));
                ticketBean.setSeat_name(ticket.getString("seat_name"));
                ticketBean.setSeat_type_code(ticket.getString("seat_type_code"));
                ticketBean.setSeat_type_name(ticket.getString("seat_type_name"));
                ticketBean.setTicket_type_code(ticket.getString("ticket_type_code"));
                ticketBean.setTicket_type_name(ticket.getString("ticket_type_name"));
                ticketBean.setPay_limit_time(ticket.getString("pay_limit_time"));
                ticketBean.setTicketPrice(ticket.getFloatValue("str_ticket_price_page"));
                //ADD
                ticketBeanList.add(ticketBean);
            }
            returnBean.setTickets(ticketBeanList);
        }
        catch (Exception e) {
        }
        return returnBean;
    }

    private String getThree(String station_code, String station_name) {
        if (ElongHotelInterfaceUtil.StringIsNull(station_code)) {
            try {
                station_code = Train12306StationInfoUtil.getThreeByName(station_name);
            }
            catch (Exception e) {
                station_code = "";
            }
        }
        return station_code;
    }

    /**
     * 发送错误信息到REP统计接口
     * 
     * @param repServerBean
     * @param contentstr
     * @time 2015年10月30日 下午3:58:51
     * @author fiend
     */
    private void sendThread2RepMsg(RepServerBean repServerBean, String contentstr) {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
            Thread t1 = new RepLogUtil(repServerBean, contentstr, 0);
            pool.execute(t1);
            // 关闭线程池
            pool.shutdown();
        }
        catch (Exception e) {
        }
    }
}