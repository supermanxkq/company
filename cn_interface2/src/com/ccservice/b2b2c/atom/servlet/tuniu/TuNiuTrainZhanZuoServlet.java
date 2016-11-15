package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.AirUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.InterfaceTimeRuleUtil;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

public class TuNiuTrainZhanZuoServlet extends TrainSelectLoginWay {
    
    String res = "false";

    private final String logname = "tuniu_3_2_3_占座接口";

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    private boolean acquiringresult = false;//是否收单

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);

        int random = (int) (Math.random() * 1000000);
        final AsyncContext ctx = request.startAsync();

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
        WriteLog.write(logname, random + "--->" + reqString);
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
            if ("".equals(account) || "".equals(sign) || "".equals(timestamp) || "".equals(data)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            TuNiuTraintrainAccountGrab tuNiuTraintrainAccountGrab = new TuNiuTraintrainAccountGrab();
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
            WriteLog.write(logname, random + "--->localsign:" + localsign + "--->sign:" + sign);
            if (!sign.equalsIgnoreCase(localsign)) {
                tuNiuServletUtil.respBySignatureError(ctx, logname);
                return;
            }
            String paramStr = TuNiuDesUtil.decrypt(data);
            WriteLog.write(logname, random + "--->paramStr:" + paramStr);
            JSONObject jsonString = new JSONObject();
            try {
                jsonString = JSONObject.parseObject(paramStr);
            }
            catch (Exception e1) {
                ExceptionUtil.writelogByException(paramStr, e1);
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            int status = getInterfaceAccountStatus(Long.parseLong(agentid));
            if(status == 0){
                //判断余额状态     0继续
            }else{
                tuNiuServletUtil.respByNoMoney(ctx, logname);
                return;
            }
            String orderId = tuNiuServletUtil.getParamByJsonStr("orderId", jsonString);//    string  Y       途牛订单号
            String cheCi = tuNiuServletUtil.getParamByJsonStr("cheCi", jsonString);//  string  Y       车次
            String fromStationCode = tuNiuServletUtil.getParamByJsonStr("fromStationCode", jsonString);//    string  Y       出发站简码
            String fromStationName = tuNiuServletUtil.getParamByJsonStr("fromStationName", jsonString);//    string  Y       出发站名称
            String toStationCode = tuNiuServletUtil.getParamByJsonStr("toStationCode", jsonString);//  string  Y       到达站简码
            String toStationName = tuNiuServletUtil.getParamByJsonStr("toStationName", jsonString);//  string  Y       到达站名称
            String trainDate = tuNiuServletUtil.getParamByJsonStr("trainDate", jsonString);//  string  Y       乘车日期
            //===========异步  s
            String callBackUrl = tuNiuServletUtil.getParamByJsonStr("callBackUrl", jsonString);//    string  Y       回调地址
            boolean hasSeat = tuNiuServletUtil.getParamByJsonBoolean("hasSeat", jsonString);//    boolean  Y      是否需要无座
            String passengers = tuNiuServletUtil.getParamByJsonStr("passengers", jsonString);// string  Y       乘客信息的json字符串。可以是多个乘客信息，最多5个，如：[{乘客1信息},{乘客2信息},...]，也可以只有一个，[{乘客1信息}]。乘客参数见附注1。重要提示：不能只购买儿童票，如果购买儿童票，必须使用随行成人的成人票证件信息（包括姓名、证件号码）。
            String contact = tuNiuServletUtil.getParamByJsonStr("contact", jsonString);//    string  Y       联系人姓名
            String phone = tuNiuServletUtil.getParamByJsonStr("phone", jsonString);//  string  Y       联系人手机
            String userName = tuNiuServletUtil.getParamByJsonStr("userName", jsonString);//   string  N       12306用户名
            String userPassword = tuNiuServletUtil.getParamByJsonStr("userPassword", jsonString);//   string  N       12306密码
            String insureCode = tuNiuServletUtil.getParamByJsonStr("insureCode", jsonString);//String N 保险产品编号
            String shoudan = tuNiuServletUtil.getParamByJsonStr("shoudan", reqjso);
            int ordertype = getOrdertype(userName, userPassword, "");
            if ("".equals(orderId) || "".equals(cheCi) || "".equals(fromStationCode) || "".equals(fromStationName)
                    || "".equals(toStationCode) || "".equals(toStationName) || "".equals(trainDate)
                    || "".equals(callBackUrl) || "".equals(passengers)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            Trainorder trainorder = new Trainorder();
            trainorder.setAgentid(Long.parseLong(agentid));// 代理ID
            trainorder.setCreateuid(Long.parseLong(agentid));//
            List<Trainpassenger> passengerlist = gettrainpassenger(JSONArray.parseArray(passengers), null, cheCi,
                    toStationName, trainDate, fromStationName);
            trainorder.setPassengers(passengerlist);
            trainorder.setOrderstatus(Trainorder.WAITPAY);
            trainorder.setQunarOrdernumber(orderId);
            trainorder.setOrderprice(0f);
            trainorder.setAgentprofit(0f);// 采购利润
            trainorder.setInterfacetype(Integer.valueOf(interfacetype));
            trainorder.setCommission(0f);
            trainorder.setSupplyprice(0f);
            trainorder.setCreateuser("接口");
            trainorder.setPaymethod(4);
            trainorder.setState12306(Trainorder.WAITORDER);// 12306状态--等待下单
            trainorder.setContactuser(contact);
            trainorder.setContacttel(phone);
            trainorder.setOrdertype(ordertype);
            boolean isTimeoutOrder = false;
            try {
                String sql = "SELECT C_ORDERNUMBER FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='"
                        + orderId + "' AND C_AGENTID=" + agentid;
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map1 = (Map) list.get(0);
                    String ordernumberString = map1.get("C_ORDERNUMBER").toString();
                    orderId = ordernumberString;
                    isTimeoutOrder = true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (!isTimeoutOrder) {
                try {
                    //存老库
                    String msgOperate="";
                    trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
                    orderId = tuNiuTraintrainAccountGrab.getordernumberbyid(trainorder.getId());
                    if (InterfaceTimeRuleUtil.isNightCreateOrder() && !"true".equals(shoudan)) {
                        acquiringresult = true;
                        reqjso.put("shoudan", "true");
                        String msgorder = reqjso.toJSONString();
                        saveOrderInfoShouDan(msgorder, orderId, agentid);
                        msgOperate="创建订单成功，已收单";
                    }else{
                        msgOperate="提交订单成功"; 
                    }
                    tuNiuTraintrainAccountGrab.createtrainorderrc(1, msgOperate, trainorder.getId(), 0L,
                            trainorder.getOrderstatus(), "占座接口");
                        createTrainOrderExtSeat(trainorder, reqjso);
                    new TongchengSupplyMethod().newyibuchulidingdan(acquiringresult, trainorder);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    tuNiuServletUtil.respByParamError(ctx, logname);
                    return;
                }
                JSONObject json = new JSONObject();
                json.put("vendorOrderId", orderId);
                tuNiuServletUtil.respBySuccess(ctx, logname, json);
            }
            else {
                tuNiuServletUtil.respByHighFrequencyError(ctx, logname);
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logname, e, random + "");
            tuNiuServletUtil.respByUnknownError(ctx, logname);
        }

    }

    //格式化出发日期
    private String formate(String trainDate) {
        String riqi = trainDate.substring(0, 4) + trainDate.substring(5, 7) + trainDate.substring(8, 10);
        return riqi;
    }

    /**
     * 1、线上 订单
     * 2、线上 订单使用客户 账号下单
     * 3、线上 订单使用客户 账号下单 cookie方式下单
     * @param username
     * @param userpassword
     * @param cookie
     * @time 2015年12月3日 下午3:47:22
     * @author QingXin
     */
    public static int getOrdertype(String username, String userpassword, String cookie) {
        int ordertype = 1;
        try {
            if (!ElongHotelInterfaceUtil.StringIsNull(username) && !ElongHotelInterfaceUtil.StringIsNull(userpassword)) {
                ordertype = 3;
            }
            else if (!ElongHotelInterfaceUtil.StringIsNull(cookie)) {
                ordertype = 4;
            }
        }
        catch (Exception e) {
        }
        return ordertype;
    }

    /**
     * 把json里的对象转换成我们自己的 List<Trainpassenger>
     * 
     * @param jsons
     * @param traininfo
     * @param checi
     * @param to_station_name
     * @param train_date
     * @param from_station_name
     * @return
     * @time 2016年4月27日 下午2:11:23
     * 
     */

    public List<Trainpassenger> gettrainpassenger(JSONArray jsons, Train traininfo, String checi,
            String to_station_name, String train_date, String from_station_name) {
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        for (int i = 0; i < jsons.size(); i++) {
            Trainpassenger trainpassenger = new Trainpassenger();// 订单的人员信息
            Trainticket ticket = new Trainticket();// 票
            String passengerid = jsons.getJSONObject(i).getString("passengerId");// 乘客的顺序号
            String ticket_no = jsons.getJSONObject(i).getString("ticketNo");// 票号
            String passengersename = jsons.getJSONObject(i).getString("passengerName");// 乘客姓名
            String passportseno = jsons.getJSONObject(i).getString("passportNo");// 乘客证件号码
            String passporttypeseid = jsons.getJSONObject(i).getString("passportTypeId");// 证件类型ID
            String passporttypeseidname = jsons.getJSONObject(i).getString("passportTypeName");// 证件类型名称
            String piaotype = jsons.getJSONObject(i).getString("piaoType");// 票种ID
            String piaotypename = jsons.getJSONObject(i).getString("piaoTypeName");// 票种名称
            String zwcode = jsons.getJSONObject(i).getString("zwCode");// 座位编码
            String zwname = jsons.getJSONObject(i).getString("zwName");// 座位名称
            //      zwname = zwname.replace("上", "").replace("中", "").replace("下", "");
            String cxin = jsons.getJSONObject(i).getString("cxin");// 几车厢几座
            String price = jsons.getJSONObject(i).getString("price");// 票价
            //TODO 学生票
            String province_name = jsons.getJSONObject(i).getString("provinceName") == null ? "" : jsons.getJSONObject(
                    i).getString("provinceName");// 省份名称
            String province_code = jsons.getJSONObject(i).getString("provinceCode") == null ? "" : jsons.getJSONObject(
                    i).getString("provinceCode");// 省份编号
            String school_code = jsons.getJSONObject(i).getString("schoolCode") == null ? "" : jsons.getJSONObject(i)
                    .getString("schoolCode");// 学校代号
            String school_name = jsons.getJSONObject(i).getString("schoolName") == null ? "" : jsons.getJSONObject(i)
                    .getString("schoolName");// 学校名称
            String student_no = jsons.getJSONObject(i).getString("studentNo") == null ? "" : jsons.getJSONObject(i)
                    .getString("studentNo");// 学号
            String school_system = jsons.getJSONObject(i).getString("schoolSystem") == null ? "" : jsons.getJSONObject(
                    i).getString("schoolSystem");// 学制
            String enter_year = jsons.getJSONObject(i).getString("enterYear") == null ? "" : jsons.getJSONObject(i)
                    .getString("enterYear"); // 入学年份
            String preference_from_station_name = jsons.getJSONObject(i).getString("preferenceFromStationName") == null ? ""
                    : jsons.getJSONObject(i).getString("preferenceFromStationName");// 起始名称
            String preference_from_station_code = jsons.getJSONObject(i).getString("preferenceFromStationCode") == null ? ""
                    : jsons.getJSONObject(i).getString("preferenceFromStationCode");// 起始地代号
            String preference_to_station_name = jsons.getJSONObject(i).getString("preferenceToStationName") == null ? ""
                    : jsons.getJSONObject(i).getString("preferenceToStationName");// 到达名称
            String preference_to_station_code = jsons.getJSONObject(i).getString("preferenceToStationCode") == null ? ""
                    : jsons.getJSONObject(i).getString("preferenceToStationCode");// 到达地代号
            passportseno = AirUtil.ToDBC(passportseno);
            // 乘客信息
            if (passportseno.length() > 14) {
                trainpassenger.setBirthday(passportseno.substring(6, 14));
            }
            else {
                trainpassenger.setBirthday("");
            }
            trainpassenger.setName(passengersename);
            trainpassenger.setIdtype(TuNiuServletUtil.getIdtype12306tolocal(passporttypeseid));
            trainpassenger.setIdnumber(passportseno);
            trainpassenger.setAduitstatus(0);
            trainpassenger.setChangeid(0);
            // 票信息
            ticket.setTrainno(checi);
            ticket.setPrice(Float.valueOf(price));
            ticket.setPayprice(Float.valueOf(price));
            // ticket.setCoach(cxin);
            // ticket.setSeatno(zwcode);
            ticket.setSeattype(tuNiuServletUtil.tuniuSeatCode2DBSeatName(zwcode));
            ticket.setArrival(to_station_name);
            ticket.setTcseatno(passengerid);
            ticket.setInterfaceticketno(passengerid);
            if (traininfo != null && traininfo.getStarttime() != null) {
                ticket.setDeparttime(train_date + " " + traininfo.getStarttime());
            }
            else {
                ticket.setDeparttime(train_date);
            }
            if (traininfo != null && traininfo.getEndtime() != null) {
                ticket.setArrivaltime(traininfo.getEndtime());
            }
            else {
                ticket.setArrivaltime(null);
            }
            ticket.setDeparture(from_station_name);
            if (traininfo != null && traininfo.getCosttime() != null) {
                ticket.setCosttime(traininfo.getCosttime());
            }
            else {
                ticket.setCosttime(null);// 历时
            }
            ticket.setStatus(Trainticket.WAITPAY);
            ticket.setInsurenum(0);
            piaotype = (piaotype == null || "".equals(piaotype)) ? "1" : piaotype;
            ticket.setTickettype(Integer.parseInt(piaotype));
            List<Trainticket> tickets = new ArrayList<Trainticket>();
            tickets.add(ticket);
            trainpassenger.setTraintickets(tickets);
            List<TrainStudentInfo> trainStudentInfos = new ArrayList<TrainStudentInfo>();
            TrainStudentInfo trainStudentInfo = new TrainStudentInfo();
            if (3 == ticket.getTickettype()) {
                trainStudentInfo.setStudentcard("");//优惠卡号
                trainStudentInfo.setClasses(""); //所在班级
                trainStudentInfo.setDepartment(""); //所在院系
                trainStudentInfo.setEductionalsystem(school_system); //学制
                trainStudentInfo.setEntranceyear(enter_year); //入学年份
                trainStudentInfo.setFromcity(preference_from_station_name); //出发城市
                trainStudentInfo.setTocity(preference_to_station_name); //到达城市
                trainStudentInfo.setSchoolname(school_name); //学校名称
                trainStudentInfo.setStudentno(student_no); //学生证号
                trainStudentInfo.setSchoolnamecode(school_code); //学校代号
                trainStudentInfo.setSchoolprovincecode(province_code); //学校所在省代号
                trainStudentInfo.setSchoolprovince(province_name);
                trainStudentInfo.setFromcitycode(preference_from_station_code); //出发城市代号
                trainStudentInfo.setTocitycode(preference_to_station_code); //到达城市代号
                trainStudentInfo.setArg1(0l); //备用参数1
                trainStudentInfo.setArg2(""); //备用参数2
                trainStudentInfo.setArg3(0l); //备用参数3
                trainStudentInfos.add(trainStudentInfo);
                trainpassenger.setTrainstudentinfos(trainStudentInfos);
            }
            passengers.add(trainpassenger);
        }
        return passengers;
    }

    /**
     * 根据单个供应商判断收单
     * 
     * 
     * 
     * @param agentid
     * @return 收单返回true 不收单返回false
     * @time 2015年11月5日 下午2:35:52
     * @author chendong
     */
    public boolean getAcquiringflagByAgentID(String agentid) {
        String AcquiringflagMsg = "";
        try {
            String sysflag = getSysconfigString("sysflag");//系统标识 系统标识 1 同程 2 空铁
            String quiringOrderkey = "quiringOrderkey_" + sysflag + "_" + agentid;
            if ("".equals(MemCached.getInstance().get(quiringOrderkey))) {
                return false;
            }
            else {
                Object o_AcquiringflagMsg = MemCached.getInstance().get(quiringOrderkey);//系统整体收单标识
                AcquiringflagMsg = o_AcquiringflagMsg == null ? "" : o_AcquiringflagMsg.toString();
                if ("success".equals(AcquiringflagMsg)) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            WriteLog.write(logname, "allAcquiringflag:" + e.fillInStackTrace());
        }
        return false;
    }

    /**
     * 供应商收单处理
     * 收单模式 保存数据库
     */
    public void saveOrderInfoShouDan(String ordermsg, String key, String agentid) {
        try {
            String countsql = "select count(id) from T_TRAINORDERMSG where C_KEY='" + key + "'";
            int c1 = Server.getInstance().getSystemService().countGiftBySql(countsql);
            WriteLog.write("t同程火车票接口_4.5收单", ordermsg);
            if (c1 <= 0) {
                String msgsql = "insert into T_TRAINORDERMSG(C_MSG,C_TIME,C_STATE,C_USERID,C_KEY,C_MSGTYPE,C_INTERFACETYPE,C_AGENTID) values('"
                        + ordermsg
                        + "','"
                        + new Timestamp(System.currentTimeMillis())
                        + "',1,0,'"
                        + key
                        + "',1,1,"
                        + agentid + ")";
                int i1 = Server.getInstance().getSystemService().excuteGiftBySql(msgsql);
            }
            else {
                WriteLog.write("t同程火车票接口_4.5收单", "已存在：" + key);
            }
        }
        catch (Exception e) {
            WriteLog.write(logname, "tongchengtrainorder_updateExtordercreatetime：" + e.fillInStackTrace());
        }
    }
    
    /**
     * 余额是否可以拦截
     * 
     * @param agentid
     * @return
     * @time 2016年9月6日 下午6:04:47
     * @author fiend
     */
    private int getInterfaceAccountStatus(long agentid) {
        int status = 0;
        try {
            String sql = "select top 1 * from T_INTERFACEACCOUNT WITH (NOLOCK) where C_agentid=" + agentid;
            DataTable datatable = DBHelper.GetDataTable(sql);
            List<DataRow> dataRows = datatable.GetRow();

            for (DataRow datacolumn : dataRows) {
                status = datacolumn.GetColumnInt("C_STATUS");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}
