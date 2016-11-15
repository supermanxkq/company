package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.base.mailaddress.MailAddress;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.base.util.PageInfo;
import com.ccservice.b2b2c.ben.Trainform;

/**
 * 查询订单
 */
public class TongChengTrainQueryInfo extends TongchengSupplyMethod {

    private InterfaceAccount getInterfaceAccountByLoginname(String partnerid) {
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + partnerid + "'", null, -1, 0);
        }
        catch (Exception e) {
        }
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        return interfaceAccount;
    }

    /**
     * 查看订单列表
     */
    public String trainListInfo(JSONObject jsonObject) throws Exception {
        String partnerid = jsonObject.getString("partnerid");//传过来的partnerid
        String mobile = jsonObject.getString("mobile");//
        String loginpassword = jsonObject.getString("loginpassword");//
        InterfaceAccount interagent = getInterfaceAccountByLoginname(partnerid);
        WriteLog.write("t订单列表", jsonObject.toJSONString());
        JSONObject jsonstr = new JSONObject();
        if (interagent.getAgentid() == null || interagent.getAgentid() <= 0) {
            jsonstr.put("code", "103");
            jsonstr.put("success", "false");
            jsonstr.put("msg", "接口非法");
            return jsonstr.toString();
        }
        Customeruser user = TrainSupplyMethodUtil.getLoginUser(mobile, loginpassword, interagent.getAgentid());
        if (user == null) {
            jsonstr.put("code", "103");
            jsonstr.put("success", "false");
            jsonstr.put("msg", "未查到用户");
            return jsonstr.toString();
        }
        PageInfo pageinfo = new PageInfo();
        int pagecount = jsonObject.getInteger("pagecount") == null ? 10 : jsonObject.getInteger("pagecount");//一页数量
        int pagenum = jsonObject.getInteger("pagenum") == null ? 1 : jsonObject.getInteger("pagenum");//当前页数
        String begin_time = jsonObject.getString("begin_time") == null ? "" : jsonObject.getString("begin_time");//开始日期
        String end_time = jsonObject.getString("end_time") == null ? "" : jsonObject.getString("end_time");//结束日期
        if (pagecount > 20) {
            pageinfo.setPagerow(pagecount);
        }
        else {
            pageinfo.setPagerow(10);
        }
        pageinfo.setPagenum(pagenum);
        int s = (pageinfo.getPagenum() - 1) * pageinfo.getPagerow();
        s = s < 0 ? 0 : s;
        int e = pageinfo.getPagenum() * pageinfo.getPagerow();
        String where = " where 1=1 and O.C_CREATEUID=" + user.getId() + " and O.C_AGENTID=" + interagent.getAgentid();
        Trainform trainform = new Trainform();
        if (isNotNullOrEpt(begin_time)) {
            where = where + " AND O.C_CREATETIME >= '" + begin_time + "'";
        }
        else {
            begin_time = TimeUtil.gettodaydate(4);
            Timestamp obtstartime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(begin_time)
                    .getTime());
            trainform.setStarttime(obtstartime);
            begin_time = begin_time.split(" ")[0];
            where += " AND O.C_CREATETIME >= '" + begin_time + "'";
        }
        if (isNotNullOrEpt(end_time)) {
            Timestamp obtendtime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    end_time + " 23:59:59").getTime());
            trainform.setEndtime(obtendtime);
            where += " AND O.C_CREATETIME <= '" + end_time + "'";
        }
        // 1:取热表,10:取冷表,100:取回收站表
        List list = new ArrayList();
        List list_new = new ArrayList();
        int searchtype = getsearchtype(trainform, begin_time, end_time, 0);// 获取到数据来源是哪里
        list_new = getrownumberfenyebysql(where, s, e, searchtype);
        pageinfo.setTotalrow(gettotalcount(list_new));
        list = gettrainorderlistbyListmap(list_new);
        jsonstr.put("success", true);
        jsonstr.put("code", "100");
        jsonstr.put("msg", "处理或操作成功");
        jsonstr.put("pagecount", list.size());
        jsonstr.put("pagenum", pagenum);
        jsonstr.put("totalpage", pageinfo.getTotalpage());
        jsonstr.put("totalcount", pageinfo.getTotalrow());
        jsonstr.put("begin_time", begin_time);
        jsonstr.put("end_time", end_time);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            Trainorder order = (Trainorder) list.get(i);
            JSONObject jsobj = new JSONObject();
            List listinfo = getTicketbyPassList(order.getId(), searchtype);
            jsobj.put("sid", order.getId());
            jsobj.put("orderid", order.getQunarOrdernumber());
            jsobj.put("transactionid", order.getOrdernumber());
            jsobj.put("createtime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(order.getCreatetime()));
            jsobj.put("orderstatus", order.getOrderstatus());
            jsobj.put("state12306", order.getState12306());
            JSONArray ticketarray = new JSONArray();
            double insuprice = 0;
            for (int j = 0; j < listinfo.size(); j++) {
                JSONObject ticket = new JSONObject();
                Map map = (Map) listinfo.get(j);
                String C_DEPARTURE = map.get("C_DEPARTURE").toString();
                String C_ARRIVAL = map.get("C_ARRIVAL").toString();
                String C_DEPARTTIME = map.get("C_DEPARTTIME").toString();
                String C_TRAINNO = map.get("C_TRAINNO") == null ? "" : map.get("C_TRAINNO").toString();
                String C_INSURENO = map.get("C_INSURENO") == null ? "" : map.get("C_INSURENO").toString();//保单号
                String C_INSURORIGPRICE = map.get("C_INSURORIGPRICE") == null ? "0" : map.get("C_INSURORIGPRICE")
                        .toString();//保险价格
                ticket.put("departure", C_DEPARTURE);
                ticket.put("arrival", C_ARRIVAL);
                ticket.put("departtime", C_DEPARTTIME);
                ticket.put("trainno", C_TRAINNO);
                ticket.put("insurno", C_INSURENO);
                ticket.put("insurprice", C_INSURORIGPRICE);
                try {
                    if (Double.parseDouble(C_INSURORIGPRICE) > 0) {
                        insuprice += Double.parseDouble(C_INSURORIGPRICE);
                    }
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
                ticketarray.add(ticket);
            }
            double mailprice = 0.0d;
            try {
                String mailsql = "select * from mailaddress with(nolock) where ORDERID=" + order.getId();
                List<MailAddress> maillist = Server.getInstance().getMemberService()
                        .findAllMailAddressBySql(mailsql, -1, 0);
                if (maillist.size() > 0) {
                    mailprice = 20;
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            double orderprice = order.getOrderprice() + insuprice + mailprice;
            if (order.getState12306() == 1 && order.getState12306() == 2 && order.getState12306() == 3) {
                orderprice = 0;
            }
            if (order.getOrderstatus() == 8) {
                orderprice = 0;
            }
            jsobj.put("totalprice", orderprice);
            jsobj.put("ticketinfo", ticketarray);
            array.add(jsobj);
        }
        jsonstr.put("orderlist", array);
        return jsonstr.toJSONString();
    }

    /**
     * 根据trainorderid 获取票信息
     * 
     * @param ticketList
     * @return
     * @time 2014年12月22日 下午5:38:15
     * @author chendong
     * @param searchtype2
     */
    public List getTicketbyPassList(Long trainorderid, int searchtype) {
        List listinfo = new ArrayList();
        String nameString = "";// C_DEPARTURE,C_ARRIVAL,C_DEPARTTIME
        String search_lie = " C_TRAINNO,C_DEPARTURE,C_ARRIVAL,C_DEPARTTIME,C_INSURORIGPRICE,C_INSURENO ";
        String where = " WHERE C_TRAINPID = (SELECT TOP(1)ID FROM T_TRAINPASSENGER with(nolock) WHERE C_ORDERID="
                + trainorderid + ")";
        String sql = "SELECT " + search_lie + " FROM T_TRAINTICKET with(nolock) " + where;
        if (searchtype / 10 > 0) {// 有冷表
            sql += "union all SELECT " + search_lie + " FROM T_TRAINTICKET_COLD with(nolock) " + where;
        }
        if (searchtype / 100 > 0) {// 有回收站表
            sql += "union all SELECT " + search_lie + " FROM T_TRAINTICKET_RECYCLE with(nolock) " + where;
        }
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                listinfo.add(map);
            }
        }
        return listinfo;
    }

    private int gettotalcount(List list_new) {
        int i_totalcount = 0;
        try {
            Map map = (Map) list_new.get(0);
            String totalcount = map.get("TOTALCOUNT").toString();
            i_totalcount = Integer.parseInt(totalcount);
        }
        catch (Exception e) {
        }
        return i_totalcount;
    }

    /**
     * 自写sql分页
     * 
     * @param where
     * @param s
     *            起始位置
     * @param e
     *            结束位置
     * @param searchtype
     *            1:取热表,2:取冷表,3:取回收站表
     * @return
     * @time 2015年4月9日 下午9:37:02
     * @author chendong
     * @param searchtype
     */
    private List getrownumberfenyebysql(String where, int s, int e, int searchtype) {
        List list_new = new ArrayList();
        String search_lie = " ID,C_ORDERNUMBER,C_EXTNUMBER,C_ISQUESTIONORDER,C_STATE12306,C_AGENTID,C_CREATETIME,C_TOTALPRICE,C_AGENTPROFIT,C_ORDERSTATUS,C_OPERATEUID,C_QUNARORDERNUMBER,C_ISPLACEING,C_ORDERTIMEOUT ";
        String sql = "SELECT *,TOTALNUMBER+ROWNUMBER-1 AS TOTALCOUNT FROM ("
                + "select ROW_NUMBER() OVER(ORDER BY W1.ID ASC) AS TOTALNUMBER,ROW_NUMBER() OVER( ORDER BY W1.ID DESC) AS ROWNUMBER,* from(";
        sql += "SELECT " + search_lie + " from T_TRAINORDER WITH(NOLOCK) " + where.replace("O.", "") + " ";
        if (searchtype / 10 > 0) {// 有冷表
            sql += "union all " + "select " + search_lie + "from T_TRAINORDER_COLD WITH(NOLOCK)  "
                    + where.replace("O.", "") + " ";
        }
        if (searchtype / 100 > 0) {// 有回收站表
            sql += "union all " + "select " + search_lie + "from T_TRAINORDER_RECYCLE WITH(NOLOCK)  "
                    + where.replace("O.", "") + " ";
        }
        sql += ") as W1)as w11 where (w11.rownumber > " + s + " and w11.rownumber<=" + e
                + ") order by w11.rownumber asc";
        WriteLog.write("t订单列表", sql);
        list_new = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        return list_new;
    }

    /**
     * 根据条件获取从哪里(三张表,热|冷|回收)获取订单列表
     * 
     * @param trainform
     * @param searchtime
     *            第几次查询
     * @return 1:取热表,10:取冷表,100:取回收站表
     */
    private int getsearchtype(Trainform trainform, String begin_time, String end_time, int searchtime) {
        int searchtype = 0;
        // 如果起始日期或者结束日期是今天就把热表数据加上
        if (TimeUtil.gettodaydate(1).equals(begin_time) || TimeUtil.gettodaydate(1).equals(end_time)) {
            searchtype += 1;
        }
        if (searchtype == 0) {// 不是当天
            if (trainform.getOrderstatus() != 8) {// 不是订单已取消
                searchtype += 10;// 查冷表
            }
            else if (trainform.getOrderstatus() == 8) {// 是订单已取消
                searchtype += 100;// 查回收表
            }
        }
        return searchtype;
    }

    /**
     * 
     * @param list_new
     * @return
     * @time 2015年3月30日 下午9:59:39
     * @author chendong
     */
    protected List gettrainorderlistbyListmap(List list_new) {
        List list = new ArrayList();
        for (int i = 0; i < list_new.size(); i++) {
            Map map = (Map) list_new.get(i);
            Trainorder trainorder = getTrainorderbyMap(map);
            list.add(trainorder);
        }
        return list;
    }

    private String getvaluebykey(Map map, String key) {
        String value = "0";
        try {
            value = map.get(key) == null ? "0" : map.get(key).toString();
        }
        catch (Exception e) {
        }
        return value;
    }

    private Trainorder getTrainorderbyMap(Map map) {
        Trainorder trainorder = new Trainorder();
        try {
            Long id = Long.parseLong(getvaluebykey(map, "ID"));
            trainorder.setId(id);
            trainorder.setOrdernumber(getvaluebykey(map, "C_ORDERNUMBER"));
            trainorder.setExtnumber(getvaluebykey(map, "C_EXTNUMBER"));
            trainorder.setIsquestionorder(Integer.parseInt(getvaluebykey(map, "C_ISQUESTIONORDER")));
            trainorder.setState12306(Integer.parseInt(getvaluebykey(map, "C_STATE12306")));
            trainorder.setAgentid(Long.parseLong(getvaluebykey(map, "C_AGENTID")));
            trainorder.setOrderprice(Float.parseFloat(getvaluebykey(map, "C_TOTALPRICE")));
            trainorder.setAgentprofit(Float.parseFloat(getvaluebykey(map, "C_AGENTPROFIT")));
            trainorder.setOrderstatus(Integer.parseInt(getvaluebykey(map, "C_ORDERSTATUS")));
            trainorder.setCreatetime(TimeUtil.parseTimestampbyString(getvaluebykey(map, "C_CREATETIME")));
            trainorder.setOperateuid(Long.parseLong(getvaluebykey(map, "C_OPERATEUID")));
            trainorder.setQunarOrdernumber(getvaluebykey(map, "C_QUNARORDERNUMBER"));
            trainorder.setIsplaceing(Integer.parseInt(getvaluebykey(map, "C_ISPLACEING")));
            trainorder.setOrdertimeout(TimeUtil.parseTimestampbyStringReal(getvaluebykey(map, "C_ORDERTIMEOUT")));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        // w1.C_ORDERNUMBER,C_EXTNUMBER,C_ISQUESTIONORDER,C_STATE12306,C_AGENTID,C_CREATETIME,C_TOTALPRICE,C_AGENTPROFIT,C_ORDERSTATUS
        return trainorder;
    }

    /**
     * 座位code转换
     * @param str
     * @return
     */
    public String getZwcode(String str) {
        String result = "0";
        if (str == null) {

        }
        else if ("硬座".equals(str)) {
            result = "1";
        }
        else if ("软座".equals(str)) {
            result = "2";
        }
        else if ("硬卧".equals(str)) {
            result = "3";
        }
        else if ("软卧".equals(str)) {
            result = "4";
        }
        else if ("高级软卧".equals(str)) {
            result = "6";
        }
        else if ("一等软座".equals(str)) {
            result = "7";
        }
        else if ("二等软座".equals(str)) {
            result = "8";
        }
        else if ("商务座".equals(str)) {
            result = "9";
        }
        else if ("特等座".equals(str)) {
            result = "P";
        }
        else if ("一等座".equals(str)) {
            result = "M";
        }
        else if ("二等座".equals(str)) {
            result = "O";
        }
        else if ("动卧".equals(str)) {
            result = "F";
        }
        else if ("高级动卧".equals(str)) {
            result = "A";
        }
        return result;
    }

    /**
     * 查看订单详情
     */
    @SuppressWarnings("rawtypes")
    public String trainqueryinfo(JSONObject reqobj) {
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //请求参数
        String orderid = reqobj.containsKey("orderid") ? reqobj.getString("orderid") : "";
        String transactionid = reqobj.containsKey("transactionid") ? reqobj.getString("transactionid") : "";

        String mobile = reqobj.containsKey("mobile") ? reqobj.getString("mobile") : "";//登录帐号
        String loginpassword = reqobj.containsKey("loginpassword") ? reqobj.getString("loginpassword") : "";//登录密码

        //验证参数
        if (ElongHotelInterfaceUtil.StringIsNull(orderid) || ElongHotelInterfaceUtil.StringIsNull(transactionid)) {
            retobj.put("code", "107");
            retobj.put("msg", "业务参数缺失");
            return retobj.toString();
        }
        //查询订单
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        double totalprice = 0.0d;
        float price = 0.0f;
        double mailprice = 0.0d;
        double insurinprice = 0.0d;
        //订单不存在
        if (orders == null || orders.size() != 1) {

            retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            return retobj.toString();
        }
        Trainorder order = orders.get(0);
        //加载其他信息
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());

        //获取微信客户端流水交易号
        String wx_transactionid = "";
        try {
            String queryWXSql = "select * from trainorderpaybywx where orderid='" + orderid + "' ";
            List list = Server.getInstance().getSystemService().findMapResultBySql(queryWXSql, null);
            if (list.size() > 0) {
                Map maps = (Map) list.get(0);
                wx_transactionid = maps.get("wx_transactionid").toString();
            }
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        //设置返回
        retobj.put("orderid", orderid);
        retobj.put("transactionid", transactionid);
        retobj.put("wx_transactionid", wx_transactionid);
        retobj.put("cashchange", new JSONArray());//资金变动情况
        retobj.put("ordernumber", strTrim(order.getExtnumber()));
        retobj.put("orderstatusname", TongChengOrderStatus.StatusStr(order));
        retobj.put("orderDate", order.getCreatetime());//下单时间
        retobj.put("ticketPrice", order.getOrderprice());//订单金额
        retobj.put("holdingSeatSuccessTime", order.getOrderprice());// 占座成功时间
        retobj.put("contactName", order.getContactuser());// 联系人姓名

        if (TrainSupplyMethodUtil.getMobileFlag(mobile, loginpassword)) {
            retobj.put("orderstatus", order.getOrderstatus());//订单状态
            retobj.put("state12306", order.getState12306());//12306状态
            retobj.put("orderstatus12306name", TongChengOrderStatus.StatusStr12306(order));
            try {
                String mailsql = "select * from mailaddress with(nolock) where ORDERID=" + order.getId();
                retobj.put("contactusername", order.getContactuser());
                retobj.put("contacttel", order.getContacttel());
                List<MailAddress> maillist = Server.getInstance().getMemberService()
                        .findAllMailAddressBySql(mailsql, -1, 0);
                if (maillist.size() > 0) {
                    mailprice = 20;
                    retobj.put("mailflag", true);
                    MailAddress address = maillist.get(0);
                    retobj.put("mailname", address.getMailName());
                    retobj.put("mailtel", address.getMailTel());
                    retobj.put("mailcode", address.getPostcode());
                    retobj.put("mailaddress", address.getAddress());
                }
                else {
                    retobj.put("mailflag", false);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //车次等
        String checi = "";
        String costtime = "";
        String traintime = "";
        String tostation = "";
        String arrivetime = "";
        String fromstation = "";
        //原车票
        JSONArray ticketstatus = new JSONArray();
        //改签车票
        Map<Long, List<Trainticket>> changeTicketMap = new HashMap<Long, List<Trainticket>>();
        //订单乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //防止无乘客
        passengers = passengers == null ? new ArrayList<Trainpassenger>() : passengers;
        //循环乘客
        for (Trainpassenger passenger : passengers) {
            List<Trainticket> tickets = passenger.getTraintickets();
            //循环车票
            for (Trainticket ticket : tickets) {

                price += ticket.getPrice() == null ? 0 : ticket.getPrice().floatValue();
                //乘客
                ticket.setTrainpassenger(passenger);
                //原车票
                checi = strTrim(ticket.getTrainno());
                costtime = strTrim(ticket.getCosttime());
                tostation = strTrim(ticket.getArrival());
                traintime = strTrim(ticket.getDeparttime());
                fromstation = strTrim(ticket.getDeparture());
                //到达时间
                if (ElongHotelInterfaceUtil.StringIsNull(arrivetime)) {
                    arrivetime = arrivetime(traintime, costtime);
                }
                JSONObject oldTicket = new JSONObject();
                oldTicket.put("ticket_no", strTrim(ticket.getTicketno()));
                oldTicket.put("passengersename", strTrim(passenger.getName()));
                oldTicket.put("orderItemId", strTrim(passenger.getPassengerid()));//艺龙的票item号
                oldTicket.put("zwname", strTrim(ticket.getSeattype()));//座位名称
                oldTicket.put("zwcode", getZwcode(ticket.getSeattype()));//座位编码
                oldTicket.put("piaotypename", strTrim(ticket.getTickettypestr()));
                oldTicket.put("idnumber", strTrim(passenger.getIdnumber()));//证件号
                oldTicket.put("idtype", getIdtype12306(passenger.getIdtype()));//证件类型
                oldTicket.put("t_tatus", ticket.getStatus());//票的状态
                if (order.getOrderstatus() == 8) {
                    oldTicket.put("t_tatus", 0);//票的状态
                }
                oldTicket.put("price", ticket.getPrice() == null ? 0 : ticket.getPrice().floatValue());
                oldTicket.put("status", TongChengOrderStatus.getTicketStatus(ticket, true, true));//车票状态
                oldTicket.put("statusid", TongChengOrderStatus.getTicketStatus(ticket, false, true));//状态ID
                try {
                    if (TrainSupplyMethodUtil.getMobileFlag(mobile, loginpassword)) {
                        oldTicket.put("insurno", strTrim(ticket.getInsureno()));//保单号
                        oldTicket.put("insurprice", strTrim(ticket.getInsurorigprice() == null ? "0" : ticket
                                .getInsurorigprice().floatValue() + ""));//保险价格
                        insurinprice += ticket.getInsurorigprice() == null ? 0 : ticket.getInsurorigprice();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                //车箱
                if (ElongHotelInterfaceUtil.StringIsNull(ticket.getCoach())
                        || ElongHotelInterfaceUtil.StringIsNull(ticket.getSeatno())) {
                    oldTicket.put("cxin", "");
                }
                else {
                    oldTicket.put("cxin", ticket.getCoach() + "车厢," + ticket.getSeatno());
                }
                oldTicket.put("outticketdetail", strTrim(ticket.getOldOutTicketDetail()));
                //ADD
                ticketstatus.add(oldTicket);
                //状态
                int status = ticket.getStatus();
                long changeid = ticket.getChangeid();
                int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType().intValue();
                //正在改签、或已改签
                if (changeid > 0
                        && (changeType == 1 || status == Trainticket.APPLYCHANGE || status == Trainticket.THOUGHCHANGE)) {
                    List<Trainticket> changeTicketList = changeTicketMap.get(changeid);
                    changeTicketList = changeTicketList == null ? new ArrayList<Trainticket>() : changeTicketList;
                    //ADD
                    changeTicketList.add(ticket);
                    //同一批改签
                    changeTicketMap.put(changeid, changeTicketList);
                }

            }
        }
        //改签车票
        JSONArray changeorderdetail = new JSONArray();
        //存在改签
        if (changeTicketMap.size() > 0) {
            //所有改签
            String changeSql = "select ID, C_TCSTATUS, C_STATION_NAME, C_REQUESTREQTOKEN, C_FROMSTATIONNAME, "
                    + "C_TOSTATIONNAME from T_TRAINORDERCHANGE with(nolock) where C_ORDERID = " + order.getId();
            List changeList = Server.getInstance().getSystemService().findMapResultBySql(changeSql, null);
            //改签数量
            int changeSize = changeList == null ? 0 : changeList.size();
            //循环改签
            for (int i = 0; i < changeSize; i++) {
                Map map = (Map) changeList.get(i);
                //改签状态
                int status = getIntMapValue(map, "C_TCSTATUS");
                //改签ID
                long changeid = Long.parseLong(map.get("ID").toString());
                //车票集合
                List<Trainticket> changeTicketList = changeTicketMap.get(changeid);
                //没有改签
                if (status == Trainorderchange.CANTCHANGE || status == Trainorderchange.FAILCHANGE
                        || changeTicketList == null || changeTicketList.size() <= 0) {
                    continue;
                }
                //第一个
                Trainticket first = changeTicketList.get(0);
                //改签参数
                String reqtoken = getStringMapValue(map, "C_REQUESTREQTOKEN");
                String stationName = getStringMapValue(map, "C_STATION_NAME");
                String toStationName = getStringMapValue(map, "C_TOSTATIONNAME");
                String fromStationName = getStringMapValue(map, "C_FROMSTATIONNAME");
                //名称为空
                if (ElongHotelInterfaceUtil.StringIsNull(toStationName)
                        || ElongHotelInterfaceUtil.StringIsNull(fromStationName)) {
                    if (!ElongHotelInterfaceUtil.StringIsNull(stationName) && stationName.contains("-")) {
                        //石家庄北 - 北京
                        String[] stationNames = stationName.split("-");
                        //石家庄北
                        fromStationName = strTrim(stationNames[0]);
                        //北京
                        toStationName = strTrim(stationNames[1]);
                    }
                }
                //改签车票
                JSONObject newTicket = new JSONObject();
                newTicket.put("reqtoken", reqtoken);
                newTicket.put("changestatus", true);
                newTicket.put("tostation", toStationName);
                newTicket.put("fromstation", fromStationName);
                newTicket.put("traintime", strTrim(first.getTtcdeparttime()));
                //车票状态
                JSONArray changeTicketStatus = new JSONArray();
                //循环改签车票
                for (Trainticket ticket : changeTicketList) {
                    //乘客
                    Trainpassenger passenger = ticket.getTrainpassenger();
                    //改签
                    JSONObject changeTicket = new JSONObject();
                    changeTicket.put("newcheci", strTrim(ticket.getTctrainno()));//改签车次
                    changeTicket.put("ticket_no", strTrim(ticket.getTcticketno()));
                    changeTicket.put("passengersename", strTrim(passenger.getName()));
                    changeTicket.put("zwname", strTrim(ticket.getTtcseattype()));//座位名称
                    changeTicket.put("zwcode", getZwcode(ticket.getTtcseattype()));//座位编码
                    changeTicket.put("piaotypename", strTrim(ticket.getTickettypestr()));
                    changeTicket.put("idnumber", strTrim(passenger.getIdnumber()));//证件号
                    changeTicket.put("idtype", getIdtype12306(passenger.getIdtype()));//证件类型
                    changeTicket.put("status", TongChengOrderStatus.getTicketStatus(ticket, true, false));//车票状态
                    changeTicket.put("statusid", TongChengOrderStatus.getTicketStatus(ticket, false, false));//状态ID
                    changeTicket.put("price", ticket.getTcPrice() == null ? 0 : ticket.getTcPrice().floatValue());
                    //车箱
                    if (ElongHotelInterfaceUtil.StringIsNull(ticket.getTccoach())
                            || ElongHotelInterfaceUtil.StringIsNull(ticket.getTcseatno())) {
                        changeTicket.put("cxin", "");
                    }
                    else {
                        changeTicket.put("cxin", ticket.getTccoach() + "车厢," + ticket.getTcseatno());
                    }
                    changeTicket.put("outticketdetail", strTrim(ticket.getNewOutTicketDetail()));
                    //ADD
                    changeTicketStatus.add(changeTicket);
                }
                //PUT
                newTicket.put("ticketstatus", changeTicketStatus);
                //ADD
                changeorderdetail.add(newTicket);
            }
        }
        totalprice = price + mailprice + insurinprice;
        if (order.getOrderstatus() == 8) {
            totalprice = 0;
        }
        //订单的总价
        //PUT
        retobj.put("code", "100");
        retobj.put("checi", checi);
        retobj.put("success", true);
        retobj.put("msg", "查询订单成功");
        retobj.put("costtime", costtime);
        retobj.put("traintime", traintime);
        retobj.put("tostation", tostation);
        retobj.put("arrivetime", arrivetime);
        retobj.put("fromstation", fromstation);
        retobj.put("ticketstatus", ticketstatus);
        retobj.put("ordertotalprice", totalprice);
        //改签
        if (changeorderdetail.size() > 0) {
            retobj.put("changeorderdetail", changeorderdetail);
        }
        //返回
        return retobj.toString();
    }

    /**
     * 到达时间
     */
    private String arrivetime(String traintime, String costtime) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(traintime));
            //历时
            String[] costtimes = costtime.split(":");
            cal.add(Calendar.HOUR, Integer.parseInt(costtimes[0]));
            cal.add(Calendar.MINUTE, Integer.parseInt(costtimes[1]));
            //返回
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cal.getTime());
        }
        catch (Exception e) {
            return "";
        }
    }

    //字符串去空格
    private String strTrim(String str) {
        return ElongHotelInterfaceUtil.StringIsNull(str) ? "" : str.trim();
    }

    @SuppressWarnings("rawtypes")
    private String getStringMapValue(Map map, String key) {
        return map.get(key) == null ? "" : map.get(key).toString();
    }

    @SuppressWarnings("rawtypes")
    private int getIntMapValue(Map map, String key) {
        return map.get(key) == null ? 0 : Integer.parseInt(map.get(key).toString());
    }
}
