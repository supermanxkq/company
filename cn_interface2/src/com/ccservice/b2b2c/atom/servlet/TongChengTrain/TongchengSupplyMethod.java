package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;

import org.apache.log4j.Logger;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.PublicComponent;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CallBackPassengerUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.IAtomService;
import com.ccservice.b2b2c.atom.service.interfacetype.TrainInterfaceType;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.service12306.bean.TrainOrderReturnBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread.MyThreadFresh12306Account;
import com.ccservice.b2b2c.atom.servlet.account.method.TrainRefreshAccountCookieMethod;
import com.ccservice.b2b2c.atom.shoudanmethod.AcquiringMethod;
import com.ccservice.b2b2c.atom.train.idmongo.Thread.MyThreadTrainIdMongoInsertPassenger;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.dnsmaintenance.Dnsmaintenance;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.rebaterecord.Rebaterecord;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Payresult;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.b2b2c.util.SendMQmsgUtil;
import com.ccservice.b2b2c.util.TrainUtil;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.b2b2c.util.ALiMQUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 同程的servlet中方法的父类
 * @time 2014年12月11日 上午11:54:58
 * @author chendong
 */
public class TongchengSupplyMethod extends PublicComponent {

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    /**rt
     * @param str
     * @return 是否为null或""
     */
    public boolean isNotNullOrEpt(String str) {
        if (str != null && str.trim().length() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 请求REP账号公共参数
     */
    private String CommonAccountInfo(Customeruser user, RepServerBean rep) {
        try {
            JSONObject obj = new JSONObject();
            //REP相关
            obj.put("repType", rep.getType());
            obj.put("serverIp", rep.getServerIp());
            obj.put("serverPort", rep.getServerPort());
            obj.put("serverPassword", rep.getServerPassword());
            //账号相关
            obj.put("loginName", user.getLoginname());
            obj.put("loginPwd", user.getLogpassword());
            //取接口数据
            if (ElongHotelInterfaceUtil.StringIsNull(rep.getSpecial12306Ip())) {
                obj.put("login12306Ip", user.getPostalcode());
            }
            //取账号系统配置数据
            else {
                obj.put("login12306Ip", rep.getSpecial12306Ip());
            }
            return URLEncoder.encode(obj.toString(), "UTF-8");
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * 请求REP账号公共参数，注意必须要有CommonAccountInfo方法的参数
     */
    public String JoinCommonAccountInfo(Customeruser user, RepServerBean rep) {
        return "&accountInfo=" + CommonAccountInfo(user, rep);
    }

    /**
     * 账号系统公共参数
     * @author WH
     */
    public JSONObject AccountSystemParam(String method) {
        JSONObject param = new JSONObject();
        try {
            //方法
            param.put("method", method);
            //时间
            long longtime = System.currentTimeMillis();
            String reqtime = String.valueOf(longtime);
            param.put("reqtime", reqtime);
            //标志
            param.put("sign", ElongHotelInterfaceUtil.MD5(ElongHotelInterfaceUtil.MD5(method) + reqtime));
        }
        catch (Exception e) {
        }
        return param;
    }

    /**
     * 账号系统地址
     * @author WH
     */
    public String GetAccountSystemUrl() {
        return PropertyUtil.getValue("12306AccountUrl", "Train.properties");
    }

    /**
     * 走账号系统
     * @author WH
     */
    public boolean GoAccountSystem() {
        return true;
        //String AccountOpen = getSysconfigString("12306AccountOpen");
        //return "1".equals(AccountOpen) || "-1".equals(AccountOpen);
    }

    /**
     * 账号系统系统获取账号
     * @author WH
     * @param type 1:获取下单账号；2:获取身份验证账号；3:账号名获取
     * @param name 12306账号名，type为3时有效，其他type可为空
     * @param waitWhenNoAccount 无账号的时候是否等待，type为1、2时有效
     * @param backup 备用字段
     */
    public Customeruser GetUserFromAccountSystem(int type, String name, boolean waitWhenNoAccount,
            Map<String, String> backup) {
        return Account12306Util.get12306Account(type, name, waitWhenNoAccount, backup);
    }

    /**
     * 获取客户账号Cookie
     * @author WH
     * @param name 账号名称
     * @param password 账号密码
     */
    public Customeruser GetCustomerAccount(String name, String password) {
        if (ElongHotelInterfaceUtil.StringIsNull(name) || ElongHotelInterfaceUtil.StringIsNull(password)) {
            return new Customeruser();
        }
        else {
            Map<String, String> backup = new HashMap<String, String>();
            backup.put("password", password);
            Customeruser user = Account12306Util.get12306Account(4, name, true, backup);
            user.setCustomerAccount(true);
            return user;
        }
    }

    /**
     * 客人Cookie方式
     */
    private Customeruser GetCustomerAccountByCookieWay(Customeruser temp) {
        Customeruser user = new Customeruser();
        //虚拟值
        user.setId(1);
        user.setState(1);
        user.setIsenable(1);
        user.setMemberemail("");
        user.setNationality("");
        user.setLoginname("Cookie");
        user.setLogpassword("Cookie");
        user.setCustomerAccount(true);
        user.setFromAccountSystem(true);
        user.setCardnunber(temp.getCardnunber());
        user.setPostalcode(temp.getPostalcode());
        return user;
    }

    /**
     * 账号系统释放12306账号
     * @author WH
     * @param user 12306账号
     * @param freeType 释放类型 1:NoCare；2:仅当天使用；3:发车时间后才可使用；4:分配给其他业务(暂未用)；其他详见AccountSystem类
     * @param freeCount 释放次数，1或2次
     * @param cancalCount 取消次数，用于取消时释放账号，其他业务必须传0
     * @param departTime 发车时间，freeType为3时有效，其他请设为空
     */
    public void FreeUserFromAccountSystem(Customeruser user, int freeType, int freeCount, int cancalCount,
            Timestamp departTime) {
        Account12306Util.free12306Account(user, freeType, freeCount, cancalCount, departTime,
                !AccountSystem.checkPassenger);
    }

    /**
     * 确定订单归属
     */
    public int getOrderAttribution(Trainorder trainorder) {
        // long dangqianjiekouagentid =
        // Long.valueOf(getSysconfigString("dangqianjiekouagentid"));
        // long tongcheng_agentid =
        // Long.valueOf(getSysconfigString("tongcheng_agentid"));
        // long qunar_agentid =
        // Long.valueOf(getSysconfigString("qunar_agentid"));
        int interfacetype = 0;
        // if (qunar_agentid == trainorder.getAgentid() && 0 ==
        // dangqianjiekouagentid) {
        // interfacetype = TrainInterfaceMethod.QUNAR;
        // }
        // else if ((tongcheng_agentid == trainorder.getAgentid() && 1 ==
        // dangqianjiekouagentid)
        // || isOtherJiekou(trainorder.getId())) {
        // interfacetype = TrainInterfaceMethod.TONGCHENG;
        // }
        // else {   
        // interfacetype = TrainInterfaceMethod.HTHY;
        // }
        interfacetype = new TrainInterfaceType().getTrainInterfaceType(trainorder.getId());
        return interfacetype;
    }

    /**
     * 公共模块获取REP地址
     * @param isDama 是否是打码
     */
    /*
    public RepServerBean GetCommonRep(boolean isDama) {
        return Server.getInstance().getTrain12306Service().commonRepServer(isDama);
    }
    */

    /**
     * 下单REP服务器，需要打码
     */
    /*
    public RepServerBean useRep() {
        //公共模块获取
        RepServerBean common = GetCommonRep(true);
        if (common != null && !ElongHotelInterfaceUtil.StringIsNull(common.getUrl())) {
            return common;
        }
        // 原REP序列号
        long oldIdx = Server.getInstance().getDamaRepIdx();
        // REP序号+1
        Server.getInstance().setDamaRepIdx(oldIdx + 1);
        // 退票REP服务器
        String RefundTicketRepUrl = getSysconfigString("RefundTicketRepUrl");
        // 非空
        if (!ElongHotelInterfaceUtil.StringIsNull(RefundTicketRepUrl) && !"-1".equals(RefundTicketRepUrl.trim())) {
            // 取REP
            String[] urls = RefundTicketRepUrl.split("@");
            // REP URL
            String url = urls[(int) (oldIdx % urls.length)];
            // 返回REP
            RepServerBean rep = new RepServerBean();
            rep.setUrl(url);
            rep.setName("退票REP服务器");
            return rep;
        }
        // REP服务器
        List<RepServerBean> RepServers = getRepServer(false);
        int size = RepServers.size();
        if (size == 0) {
            return new RepServerBean();
        }
        // 取下单REP
        int current = (int) (oldIdx % size);
        // RETURN
        return getRep(current, -1, RepServers);
    }
    */

    /**
     * 判断当前下单数量与最大下单峰值
     * 
     * @param old
     *            本来下单服务器序号，如本来是3
     * @param current
     *            下一个下单服务器序号，默认-1，表示初始化
     */
    /*
    private RepServerBean getRep(int old, int current, List<RepServerBean> RepServers) {
        RepServerBean result = RepServers.get(current == -1 ? old : current);
        // 下单REP使用+1
        int use = result.getUseNumber() + 1;
        // 最大
        int max = result.getMaxNumber();
        // 判断
        if (use <= max) {
            result.setUseNumber(use);
            result.setLastTime(new Timestamp(System.currentTimeMillis()));
            return result;
        }
        // 表示走了一轮
        else if (old == current) {
            RepServerBean rep = new RepServerBean();
            rep.setId(-1);// 表示REP达到峰值
            return rep;
        }
        else {
            current = current == -1 ? old : current;
            // 取下一个REP
            current = current + 1;
            // 走到尾，重新头开始
            if (current >= RepServers.size()) {
                current = 0;
            }
            return getRep(old, current, RepServers);
        }
    }
    */

    /**
     * 释放REP、防止REP数据变化，不直接修改原RepServerBean
     */
    /*
    public void freeRep(RepServerBean result) {
        if (result == null || ElongHotelInterfaceUtil.StringIsNull(result.getUrl())) {
            return;
        }
        try {
            if (result.isFromRepSystem()) {
                if (result.isNeedFreeRep()) {
                    Server.getInstance().getTrain12306Service().commonFreeRep(result);
                }
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        List<RepServerBean> RepServers = getRepServer(false);
        int size = RepServers.size();
        for (int i = 0; i < size; i++) {
            RepServerBean rep = RepServers.get(i);
            if (result.getUrl().equals(rep.getUrl())) {
                int use = rep.getUseNumber();
                use = use > 1 ? use - 1 : 0;
                rep.setUseNumber(use);
                break;
            }
        }
    }
    */

    /**
     * 下单REP服务器
     * 
     * @url 初始化地址 http://localhost:9001/cn_interface/InitRepServer.jsp
     * @url 状态查看地址 http://localhost:9001/cn_interface/ShowRepServer.jsp
     * @param isInit
     *            是否是初始化REP
     */
    /*
    @SuppressWarnings("rawtypes")
    public List<RepServerBean> getRepServer(boolean isInit) {
        List<RepServerBean> RepServers = Server.getInstance().getRepServers();
        // 不存在REP或初始化
        boolean NoRep = false;
        if (RepServers == null || RepServers.size() == 0 || isInit) {
            NoRep = true;
            RepServers = new ArrayList<RepServerBean>();
        }
        // 重新查询数据库
        if (NoRep) {
            List<RepServerBean> newRepList = new ArrayList<RepServerBean>();
            // 查询可用REP
            String sql = "select * from T_REPSERVER where C_STATUS = 1 order by ID";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            int size = list == null ? 0 : list.size();
            for (int i = 0; i < size; i++) {
                Map map = (Map) list.get(i);
                // ID
                long id = Long.parseLong(map.get("ID").toString());
                // REP名称
                String name = map.get("C_NAME").toString();
                // REP地址
                String url = map.get("C_URL").toString();
                // 最大订单数
                int maxNumber = Integer.parseInt(map.get("C_MAXNUMBER").toString());
                // 在下订单数
                // int useNumber =
                // Integer.parseInt(map.get("C_USENUMBER").toString());
                // 最后交互时间
                Timestamp lastTime = new Timestamp(System.currentTimeMillis());
                // NEW
                RepServerBean rep = new RepServerBean();
                // SET
                rep.setId(id);
                rep.setName(name);
                rep.setUrl(url);
                rep.setMaxNumber(maxNumber);
                rep.setUseNumber(0);
                rep.setLastTime(lastTime);
                // ADD
                newRepList.add(rep);
            }
            // UPDATE
            RepServers = newRepList;
            Server.getInstance().setRepServers(newRepList);
        }
        // RETURN
        return RepServers;
    }
    */

    /**
     * 
     * 获取同程在我们系统中的agentid
     * 
     * @return
     * @time 2014年12月11日 下午12:00:47
     * @author chendong
     * @param partnerid
     */
    public String gettongchengagentid() {
        String agentid = "";
        agentid = getSysconfigString("tongcheng_agentid");
        return agentid;
    }

    /**
     * 
     * 获取同程在我们系统中的agentid
     * 
     * @return
     * @time 2014年12月11日 下午12:00:47
     * @author chendong
     * @param partnerid
     */
    @SuppressWarnings("rawtypes")
    public String gettongchengagentid(String partnerid) {
        String agentid = "";
        //同程
        if ("tongcheng_train".equals(partnerid)) {
            agentid = getSysconfigString("tongcheng_agentid");
        }
        else {
            //SQL
            String sql = "SELECT C_AGENTID FROM T_CUSTOMERUSER WITH(NOLOCK) WHERE C_LOGINNAME = '" + partnerid + "'";
            //查询
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            //取值
            if (list != null && list.size() > 0) {
                Map map = (Map) list.get(0);
                agentid = map.get("C_AGENTID") == null ? "" : map.get("C_AGENTID").toString();
            }
        }
        return agentid;
    }

    /**
     * 根据出发到达时间获取列车信息 来自缓存 train.getStart_station_name() 始发站名字
     * train.getSfz()始发站三字码 train.getEnd_station_name()终点站名字
     * train.getZdz()终点站名字三字码 train.getRun_time_minute()运行分钟数
     * 
     * @param fromcity
     *            出发三字码
     * @param tocity
     *            到达三字码
     * @param date
     *            时间 格式2014-12-11
     * @param traincode
     *            车次
     * @return
     * @time 2014年12月11日 下午12:28:51
     * @author chendong
     */
    public static Train getTrainbycache(String fromcity, String tocity, String date, String traincode) {
        FlightSearch flightSearch = new FlightSearch();
        try {
            List<Train> list = Server.getInstance().getAtomService()
                    .getDGTrainListcache(fromcity, tocity, date, flightSearch);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTraincode().equals(traincode)) {
                    return list.get(i);
                }
            }
        }
        catch (Exception e) {
        }
        Train train = new Train();
        return train;
    }

    /**
     * 根据出发到达时间获取列车信息 数据是实时返回的 train.getStart_station_name() 始发站名字
     * train.getSfz()始发站三字码 train.getEnd_station_name()终点站名字
     * train.getZdz()终点站名字三字码 train.getRun_time_minute()运行分钟数
     * 
     * @param fromcity
     *            出发三字码
     * @param tocity
     *            到达三字码
     * @param date
     *            时间 格式2014-12-11
     * @param traincode
     *            车次
     * @return
     * @time 2014年12月11日 下午12:28:51
     * @author chendong
     */
    public static Train getTrainby12306(String fromcity, String tocity, String date, String traincode) {
        FlightSearch flightSearch = new FlightSearch();
        flightSearch.setGeneral(2);
        //        flightSearch.setTravelType("");
        IAtomService iAtomService = getAtomService();
        //        //        iAtomService = Server.getInstance().getAtomService();
        List<Train> list = iAtomService.getDGTrainListcache(fromcity, tocity, date, flightSearch);
        WriteLog.write("t_TongchengSupplyMethod_getTrainby12306", fromcity + ":" + tocity + ":" + date + ":"
                + traincode + "返回:" + JSONObject.toJSONString(list));
        for (int i = 0; i < list.size(); i++) {
            String temp_traincode = list.get(i).getTraincode();
            if (temp_traincode.equalsIgnoreCase(traincode)) {
                return list.get(i);
            }
        }
        Train train = new Train();
        return train;
    }

    public static IAtomService getAtomService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        String search_12306yupiao_service_url = PropertyUtil.getValue("search_12306yupiao_cn_interface_url",
                "Train.properties");
        try {
            return (IAtomService) factory.create(IAtomService.class, search_12306yupiao_service_url
                    + IAtomService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 证件类型本地转换12306代码
     * 
     * @param idtype
     * @return
     * @time 2014年12月24日 上午11:21:59
     * @author wzc
     */
    public static String getIdtype12306(int idtype) {
        switch (idtype) {
        case 1:
            return "1";
        case 3:
            return "B";
        case 4:
            return "C";
        case 5:
            return "G";
        }
        return "";
    }

    /**
     * 证件类型 12306转换代码本地
     * 
     * @param idtype
     * @return
     * @time 2014年12月24日 上午11:21:59
     * @author wzc
     */
    public static int getIdtype12306tolocal(String idtype) {
        if (idtype.equals("1")) {
            return 1;
        }
        else if (idtype.equals("B")) {
            return 3;
        }
        else if (idtype.equals("C")) {
            return 4;
        }
        else if (idtype.equals("G")) {
            return 5;
        }
        else {
            return 0;
        }
    }

    /**
     * 座位代码转换12306
     * 
     * @param zwcode
     * @return
     * @time 2014年12月24日 上午11:15:33
     * @author wzc
     */
    public static String getzwname(String codename) {
        String str = "0";
        if ("商务座".equals(codename)) {
            str = "9";
        }
        else if ("特等座".equals(codename)) {
            str = "P";
        }
        else if ("一等座".equals(codename)) {
            str = "M";
        }
        else if ("二等座".equals(codename) || codename.contains("二等座")) {
            str = "O";
        }
        else if ("一等软座".equals(codename)) {
            str = "7";
        }
        else if ("二等软座".equals(codename)) {
            str = "8";
        }
        else if ("高级软卧".equals(codename)) {
            str = "6";
        }
        else if ("软卧".equals(codename)) {
            str = "4";
        }
        else if ("硬卧".equals(codename)) {
            str = "3";
        }
        else if ("软座".equals(codename)) {
            str = "2";
        }
        else if ("硬座".equals(codename) || "硬卧代硬座".equals(codename)) {
            str = "1";
        }
        else if (codename.contains("高级动卧")) {
            str = "A";
        }
        else if (codename.contains("动卧")) {
            str = "F";
        }

        return str;
    }

    /**
     * 座位代码转换12306
     * 
     * @param zwcode
     * @return
     * @time 2014年12月24日 上午11:15:33
     * @author wzc
     */
    public static String getzwname_Qunar(String codename) {
        String str = "0";
        if ("商务座".equals(codename)) {
            str = "9";
        }
        else if ("特等座".equals(codename)) {
            str = "P";
        }
        else if ("高级软卧下".equals(codename)) {
            str = "11";
        }
        else if ("高级软卧上".equals(codename)) {
            str = "10";
        }
        else if ("软卧下".equals(codename)) {
            str = "9";
        }
        else if ("软卧上".equals(codename)) {
            str = "8";
        }
        else if ("硬卧下".equals(codename)) {
            str = "7";
        }
        else if ("硬卧中".equals(codename)) {
            str = "6";
        }
        else if ("硬卧上".equals(codename)) {
            str = "5";
        }
        else if ("二等软座".equals(codename) || "二等座".equals(codename)) {
            str = "4";
        }
        else if ("一等软座".equals(codename) || "一等座".equals(codename)) {
            str = "3";
        }
        else if ("软座".equals(codename)) {
            str = "2";
        }
        else if ("硬座".equals(codename)) {
            str = "1";
        }
        else if ("无座".equals(codename)) {
            str = "0";
        }

        return str;
    }

    /**
     * 12306座席
     * P:特等座，M:一等座，O:二等座，F:动卧，E:特等软座，9:商务座，8:二等软座，7:一等软座，6:高级软卧，4:软卧，3:硬卧
     * ，2:软座，1:硬座，0:无座(自定义，12306无)
     */
    public String get12306SeatTypes() {
        try {
            return new String(PropertyUtil.getValue("seatTypeOf12306").getBytes("ISO8859-1"), "UTF-8");
        }
        catch (Exception e) {
            return "P#tz_num#特等座@M#zy_num#一等座@O#ze_num#二等座@F#rw_num#动卧@E#qt_num#特等软座@A#qt_num#高级动卧@9#swz_num#商务座@8#ze_num#二等软座@7#zy_num#一等软座@6#gr_num#高级软卧@4#rw_num#软卧@3#yw_num#硬卧@2#rz_num#软座@1#yz_num#硬座@0#wz_num#无座";
        }
    }

    /**
     * 
     * 座位编码。与座位名称对应关系:9:商务座，P:特等座，M:一等座，O:二等座， 6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座
     * ，0:无座[站票] 注意:当最低的一种座位，无票时，购买选择该座位种类， 买下的就是无座(也就说买无座的席别编码就是该车次的
     * 最低席别的编码)，另外，当最低席别的票卖完了的时候 才可以卖无座的票。
     * 
     * @param i_seat
     * @return seat
     * @time 2014年8月30日 上午11:07:38
     * @author yinshubin
     */
    public String getzwcode(String zwcode) {
        String str = "";
        if ("9".equals(zwcode)) {
            str = "商务座";
        }
        else if ("P".equals(zwcode)) {
            str = "特等座";
        }
        else if ("M".equals(zwcode)) {
            str = "一等软座";
        }
        else if ("O".equals(zwcode)) {
            str = "二等软座";
        }
        else if ("6".equals(zwcode)) {
            str = "高级软卧";
        }
        else if ("4".equals(zwcode)) {
            str = "软卧";
        }
        else if ("3".equals(zwcode)) {
            str = "硬卧";
        }
        else if ("2".equals(zwcode)) {
            str = "软座";
        }
        else if ("1".equals(zwcode)) {
            str = "硬座";
        }
        else if ("0".equals(zwcode)) {
            str = "无座";
        }
        return str;
    }

    /**
     * 创建火车票的操作记录
     * 
     * @param content
     * @param orderid
     * @param ticketid
     * @param status
     * @time 2014年12月16日 下午4:11:25
     * @author chendong
     */
    public static void createtrainorderrc(int ywtype, String content, Long orderid, Long ticketid, int status) {
        Trainorderrc rc = new Trainorderrc();
        rc.setContent(content);
        rc.setCreateuser("系统接口");// 创建者
        rc.setOrderid(orderid);
        rc.setStatus(status);
        rc.setTicketid(ticketid);
        rc.setYwtype(ywtype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    /**
     * 创建火车票的操作记录
     * 
     * @param content
     * @param orderid
     * @param ticketid
     * @param status
     * @time 2014年12月16日 下午4:11:25
     * @author chendong
     */
    public static void createtrainorderrc(int ywtype, String content, Long orderid, Long ticketid, int status,
            String createuser) {
        Trainorderrc rc = new Trainorderrc();
        rc.setContent(content);
        rc.setCreateuser(createuser);// 创建者
        rc.setOrderid(orderid);
        rc.setStatus(status);
        rc.setTicketid(ticketid);
        rc.setYwtype(ywtype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    /**
     * 随机调取下单接口，获取12306-cookie所在IP
     * 
     * @return cookie所在IP
     * @time 2014年12月16日 下午12:07:53
     * @author fiend
     */
    /*
    public String randomIp(int random) {
        //公共模块获取
        RepServerBean common = random == -20150409 ? null : GetCommonRep(false);
        if (common != null && !ElongHotelInterfaceUtil.StringIsNull(common.getUrl())) {
            return common.getUrl();
        }
        String RefundTicketRepUrl = getSysconfigString("RefundTicketRepUrl");
        if (!ElongHotelInterfaceUtil.StringIsNull(RefundTicketRepUrl) && !"-1".equals(RefundTicketRepUrl.trim())) {
            // 原REP序列号
            long oldIdx = Server.getInstance().getTrainRepIdx();
            // REP序号+1
            Server.getInstance().setTrainRepIdx(oldIdx + 1);
            // 取REP
            String[] urls = RefundTicketRepUrl.split("@");
            // 返回REP
            return urls[(int) (oldIdx % urls.length)];
        }
        String repURL = "";
        List<RepServerBean> RepServers = getRepServer(false);
        if (RepServers.size() > 0) {
            RepServerBean repServerBean = RepServers.get(new Random().nextInt(RepServers.size()));
            repURL = repServerBean.getUrl();
        }
        else {
            String canorderips = getSysconfigString("Reptile_traininit_strs");
            if (canorderips.equals("-1") || canorderips.trim().equals("")) {
                repURL = getSysconfigString("Reptile_traininit_url");
            }
            String[] iscanorderip = canorderips.split(",");
            int i = (int) (Math.random() * iscanorderip.length);
            repURL = getSysconfigString("Reptile_traininit_url" + iscanorderip[i]);

        }
        return repURL;
    }
    */

    /**
     * 随机调取下单接口，获取12306-cookie所在IP
     * 
     * @return cookie所在IP
     * @time 2014年12月16日 下午12:07:53
     * @author fiend
     */
    /*
    public String randomIp_old() {
        String canorderips = getSysconfigString("Reptile_traininit_strs");
        if (canorderips.equals("-1") || canorderips.trim().equals("")) {
            return getSysconfigString("Reptile_traininit_url");
        }
        String[] iscanorderip = canorderips.split(",");
        int i = (int) (Math.random() * iscanorderip.length);
        return getSysconfigString("Reptile_traininit_url" + iscanorderip[i]);
    }
    */

    /**
     * 重新登录12306的公用方法
     * 
     * @param customeruser
     * @return
     * @time 2014年12月22日 下午6:37:16
     * @author chendong
     */
    /*
    public Customeruser login12306(Customeruser customeruser, int r1) {
        String url = "";
        //公共模块获取
        RepServerBean common = GetCommonRep(true);
        if (common != null && !ElongHotelInterfaceUtil.StringIsNull(common.getUrl())) {
            url = common.getUrl();
        }
        else {
            return customeruser;
        }
        String logname = customeruser.getLoginname();
        String logpassword = customeruser.getLogpassword();
        String param = "datatypeflag=12&logname=" + logname + "&logpassword=" + logpassword;
        String result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
        if (result != null && !"".equals(result.trim()) && result.contains("JSESSIONID")) {
            customeruser.setState(1);
            customeruser.setCardnunber(result);
            customeruser.setId(customeruser.getId());
            //Server.getInstance().getMemberService().updateCustomeruserIgnoreNull(customeruser);
            WriteLog.write("12306Cookie", logname + ";登录12306账号成功:" + customeruser.getLoginname());
        }
        else {
            customeruser.setState(0);
            //Server.getInstance().getMemberService().updateCustomeruserIgnoreNull(customeruser);
        }
        return customeruser;
    }
    */

    /**
     * 同程3151逻辑处理
     * @author WH
     * @time 2016年6月22日 下午7:22:23
     * @version 1.0
     * @param object 账号系统回调的账号JSON
     */
    public void tongcheng3151Order(JSONObject obj) {
        //释放账号
        boolean free = true;
        //JSON转为账号
        Customeruser user = new Customeruser();
        //SET
        user.setFromAccountSystem(true);
        user.setId(obj.getLongValue("id"));
        user.setState(obj.getInteger("state"));
        user.setLoginname(obj.getString("name"));
        user.setLogpassword(obj.getString("pass"));
        user.setCardnunber(obj.getString("cookie"));
        user.setIsenable(obj.getInteger("isenable"));
        user.setMemberemail(obj.getString("repurl"));
        //捕捉异常
        try {
            //订单ID
            long orderId = obj.getLong("uniqueId");
            //查询订单
            Trainorder order = Server.getInstance().getTrainService().findTrainorder(orderId);
            //查询成功
            if (order != null && order.getId() == orderId) {
                //APP设置
                setPhoneOrder(orderId, order.getAgentid(), order.getOrdertype());
                //绑定乘客
                user = onceAgain(order, new Random().nextInt(1000000), user, 0, 0, isPhoneOrder(orderId), true);
                //取账号成功，丢MQ下单，包含账号信息
                JSONObject json = new JSONObject();
                json.put("orderId", orderId);
                json.put("customeruser", user == null ? new Customeruser() : user);
                //MQ配置
                String url = PropertyUtil.getValue("activeMQ_url", "Train.properties");
                String QUEUE_NAME = PropertyUtil.getValue("QueueMQ_trainorder_waitorder_orderid", "Train.properties");
                //丢到MQ
                ActiveMQUtil.sendMessage(url, QUEUE_NAME, json.toString());
                //不释放账号
                free = false;
            }
        }
        catch (Exception e) {

        }
        finally {
            //要释放且账号未释放
            if (free && user != null && !user.isCanCreateOrder()) {
                freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
        }
    }

    /**
     * 获取一个12306账号使用，获取后该账号变成使用中
     * 
     * 获取到一个customeruser 1、判断这个customeruser的id>0 2、获取cookie String cookieString
     * = customeruser.getCardnunber(); 3、cust.getIsenable()==1 可用
     * 4、cust.getIsenable()==0 不可用，原因在cust.getDescription()里
     * 
     * @param p
     * @return
     * @time 2014年12月12日 下午7:46:25
     * @author chendong
     */
    public Customeruser getCustomerUser(Trainorder order, int random) {
        //下单账号
        Customeruser customeruser = new Customeruser();
        Long start = System.currentTimeMillis();
        setPhoneOrder(order.getId(), order.getAgentid(), order.getOrdertype());
        boolean isPhone = isPhoneOrder(order.getId());
        customeruser = onceAgain(order, random, customeruser, 0, 0, isPhone, false);
        WriteLog.write("TongchengSupplyMethod_getcustomeruser", order.getId() + ":" + random
                + ":getCustomerUserByEnname:获取到了:" + customeruser.getLoginname() + ":耗时:"
                + (System.currentTimeMillis() - start) + ":" + customeruser.getDescription());
        return customeruser;
    }

    /**
     * 乘客已添加，针对客人账号订单，如淘宝、美团
     */
    private boolean PassengersHaveBeenAdded(List<Trainpassenger> passengers, int orderType, boolean checkStudentInfo) {
        //Cookie模式
        boolean added = orderType == 4;
        //判断是否有学生票，防止淘宝等添加学生信息失败
        if (added && checkStudentInfo) {
            //乘客
            for (Trainpassenger passenger : passengers) {
                //学生信息
                List<TrainStudentInfo> studentInfos = passenger.getTrainstudentinfos();
                //存在学生
                if (studentInfos != null && studentInfos.size() > 0) {
                    added = false;
                    break;
                }
            }
        }
        //返回结果
        return added;
    }

    /**
     * @param is3151CallBack true:同程3151预约账号
     */
    private Customeruser onceAgain(Trainorder order, int random, Customeruser customeruser, int num,
            int nopassengernum, boolean isPhone, boolean is3151CallBack) {
        boolean isFalseLy = false;
        //        int nopassengermax = 1;
        WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":冒用循环次数:" + num + ":Nopassenger循环次数:"
                + nopassengernum);
        WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":order:" + JSONObject.toJSONString(order));
        if (num > 1 || nopassengernum > 1) {
            if (customeruser != null && customeruser.getDescription() != null
                    && customeruser.getDescription().contains("nopassenger_")) {
                customeruser.setDescription(customeruser.getDescription().replace("nopassenger_", ""));
            }
            return customeruser;
        }
        List<Trainpassenger> TrainPassengerList = order.getPassengers();
        //无乘客
        if (TrainPassengerList == null || TrainPassengerList.size() <= 0) {
            customeruser = new Customeruser();
            customeruser.setDescription("Error：请求乘客为空！");
            return customeruser;
        }
        //是否独占账号，独占表示下单
        boolean isExclusive = TrainPassengerList.get(0).getChangeid() != -1;
        //去哪儿身份验证(QunarTrainIdVerification类)
        boolean isQunarTrainIdVerification = !isExclusive && TrainPassengerList.get(0).getCusOutTime() == -1;
        //接口类型
        int interfacetype = order.getInterfacetype() != null && order.getInterfacetype().intValue() > 0 ? order
                .getInterfacetype().intValue() : getOrderAttribution(order);
        WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":getCustomerUserByEnname:准备获取账号:");//记录日志
        Long start = System.currentTimeMillis();
        //订单类型
        int orderType = order.getOrdertype();
        //客人账号
        boolean CustomerAccount = orderType == 3 || orderType == 4;
        //同程3151
        if (is3151CallBack) {
            //直接用账号系统回调的账号
        }
        //客人账号名和密码
        else if (orderType == 3) {
            Customeruser temp = new Customeruser();
            //美团约票
            if (interfacetype == 11) {
                temp = TrainAccountSrcUtil.getTrainAccountSrcByIdBespeak(order.getId());
            }
            else {
                temp = TrainAccountSrcUtil.getTrainAccountSrcById(order.getId());
            }
            //走账号系统
            customeruser = GetCustomerAccount(temp.getLoginname(), temp.getLogpassword());
            //不要登录重试，针对第三方传账号和密码，防止重试锁账号等
            if (customeruser != null && customeruser.isDontRetryLogin()) {
                //释放标识
                customeruser.setCanCreateOrder(true);
                //释放账号
                freeCustomeruser(customeruser, AccountSystem.FreeNoCare, AccountSystem.OneFree,
                        AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                //返回账号
                return customeruser;
            }
        }
        else if (orderType == 4) {
            //取客人数据
            Customeruser temp = TrainAccountSrcUtil.getTrainAccountSrcById(order.getId());
            //虚拟一个账号
            customeruser = GetCustomerAccountByCookieWay(temp);
        }
        else if (GoAccountSystem()) {//走账号系统
            //获取参数
            int type = isExclusive ? AccountSystem.OrderAccount : AccountSystem.PassengerAccount;
            //订单乘客
            Map<String, String> passengerMap = passengerMap(TrainPassengerList, isPhone);
            isFalseLy = passengerMap.containsKey("isFalseLy") && Boolean.valueOf(passengerMap.get("isFalseLy"));
            //判断冒用乘客是否有可用帐号
            boolean isFalsely_no = passengerMap.containsKey("isFalsely_no")
                    && Boolean.valueOf(passengerMap.get("isFalsely_no"));
            //内存反向挡板
            if (isFalsely_no) {
                JSONArray jsonArray = JSONArray.parseArray(passengerMap.get("passengers"));
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (jsonArray.getJSONObject(i).getInteger("falsely") == 1) {
                        customeruser.setDescription(jsonArray.getJSONObject(i).getString("name")
                                + "_身份信息涉嫌被他人冒用，请持本人身份证件原件到就近铁路客运车站办理身份核验");
                        //反向挡板挡住了  查DB 或者存储过程直接搞的
                        Trainpassenger tp = new Trainpassenger();
                        tp.setOrderid(order.getId());
                        tp.setIdnumber(jsonArray.getJSONObject(i).getString("idnumber"));
                        tp.setName(jsonArray.getJSONObject(i).getString("name"));
                        setUnmatchedpasslist(tp, accountJsonObjectNull(), customeruser.getDescription());
                        break;
                    }
                }
                return customeruser;
            }
            WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":GetUserFromAccountSystem:开始获取账号");
            //获取账号
            customeruser = GetUserFromAccountSystem(type, AccountSystem.NullName, isExclusive, passengerMap);
            WriteLog.write("TongchengSupplyMethod_onceAgain",
                    order.getId() + ":customeruser:" + JSONObject.toJSONString(customeruser));
            if (customeruser != null
                    && customeruser.getId() == -1
                    && ("falsely_enable".equals(customeruser.getLoginname()) || "falsely_no".equals(customeruser
                            .getLoginname()))) {
                //falsely_enable  有账号 但是不能用
                //falsely_no  没有这样的账号
                JSONArray jsonArray = JSONArray.parseArray(passengerMap.get("passengers"));
                List<Long> idnolist = new ArrayList<Long>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (jsonArray.getJSONObject(i).getInteger("falsely") == 1) {
                        //拼接错误原因
                        customeruser.setDescription(jsonArray.getJSONObject(i).getString("name")
                                + "_身份信息涉嫌被他人冒用，请持本人身份证件原件到就近铁路客运车站办理身份核验");
                        if (TrainPassengerList.get(0).getId() > 0
                                && "falsely_enable".equals(customeruser.getLoginname())) {
                            //王宏那边那帐号有误 没有拿到    
                            Trainpassenger tp = new Trainpassenger();
                            tp.setOrderid(order.getId());
                            tp.setIdnumber(jsonArray.getJSONObject(i).getString("idnumber"));
                            tp.setName(jsonArray.getJSONObject(i).getString("name"));
                            setUnmatchedpasslist(tp, JSONArray.parseArray(customeruser.getChinaaddress()),
                                    customeruser.getDescription());
                        }
                        break;
                    }
                }
                String name = "";
                long idNumber = 0;
                if ("falsely_no".equals(customeruser.getLoginname())) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if (jsonArray.getJSONObject(i).getInteger("falsely") == 1) {
                            name = jsonArray.getJSONObject(i).getString("name");
                            idnolist.add(Long.valueOf(jsonArray.getJSONObject(i).getString("idnumber").toUpperCase()
                                    .replace("X", "10")));
                        }
                    }
                    if (idnolist.size() > 0) {
                        idnolist = disSortList(idnolist);
                        //                        String ocskeyno = "falsely_no_";
                        for (Long idno : idnolist) {
                            //                            ocskeyno += idno + "_";
                            idNumber = idno;
                        }
                        //                        ocskeyno = ocskeyno.substring(0, ocskeyno.length() - 1);
                        //入反向挡板
                        //                        boolean add = OcsMethod.getInstance().add(ocskeyno, "1");
                        String sql = "";
                        try {
                            //如果DB有该冒用乘客,将乘客改为没有可用帐号状态.反之,插入该冒用乘客为没有可用帐号状态
                            if (isFalseLy) {
                                sql = "UPDATE FalselyPassenger SET flag=2 WHERE IdNumber=" + idNumber;
                            }
                            else {
                                sql = "INSERT INTO FalselyPassenger (name,IdNumber,flag) VALUES ('" + name + "',"
                                        + idNumber + ",2)";
                            }
                            boolean add = DBHelperAccount.executeSql(sql);
                            WriteLog.write("冒用反向挡板_falsely_no", idNumber + "---->" + add);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //走原方法
        else {
            customeruser = getCustomerUserByEnname(true, random, isExclusive);
        }
        WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":getCustomerUserByEnname:获取一个:"
                + customeruser.getLoginname() + ":耗时:" + (System.currentTimeMillis() - start));
        //去哪儿身份验证
        if (isQunarTrainIdVerification) {
            return customeruser;
        }
        if (isPhone) {
            try {
                DataTable dataTable = DBHelperAccount.GetDataTable("exec [sp_Customeruser_Phone_Select] @Id="
                        + customeruser.getId());
                String sessionId = "";
                String __wl_deviceCtxSession = "";
                String cookie = "";
                String deviceno = "";
                for (DataRow dataRow : dataTable.GetRow()) {
                    cookie = dataRow.GetColumnString("C_CARDNUNBER");
                    sessionId = dataRow.GetColumnString("C_SESSIONID");
                    __wl_deviceCtxSession = dataRow.GetColumnString("C_WLDEVICECTXSESSION");
                    deviceno = dataRow.GetColumnString("C_DEVICENO");
                }
                if (!"".equals(cookie)) {
                    customeruser.setCardnunber(cookie);
                }
                customeruser.setSessionid(sessionId);
                customeruser.setDeviceno(deviceno);
                customeruser.setWldevicectxsession(__wl_deviceCtxSession);
                WriteLog.write("PHONE_去DB中查询手机端数据", customeruser.getLoginname() + "--->" + customeruser.getId()
                        + "--->" + cookie + "--->" + sessionId + "--->" + __wl_deviceCtxSession);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("PHONE_ERROR_去DB中查询手机端数据", e);
            }
        }
        //如果需要下单才绑定账号的话 直接返回账号即可,不参与后续逻辑
        //客人账号已绑定乘客，不直接走系统的添加常旅功能，目前只有淘宝
        if (customeruser.getId() > 0
                && (PassengersHaveBeenAdded(TrainPassengerList, orderType, true) || isBindingPassengers(order.getId()))) {
            return customeruser;
        }
        else {
            //已添加
            boolean passengerAdded = PassengersHaveBeenAdded(TrainPassengerList, orderType, false);
            //获取到账号
            if (customeruser.getId() > 0) {
                //绑定乘客
                customeruser = bindingPassenger(customeruser, TrainPassengerList, random, isExclusive, interfacetype,
                        isPhone, passengerAdded);
                //账号未登录
                if (customeruser.getDescription() != null
                        && ("未登录".equals(customeruser.getDescription()) || "已满".equals(customeruser.getDescription())
                                || customeruser.getDescription().contains("您的身份信息未通过核验")
                                || customeruser.getDescription().contains("您在12306网站注册时填写信息有误")
                                || customeruser.getDescription().contains("您注册的信息与其他用户重复")
                                || customeruser.getDescription().contains("手机核验")
                                || customeruser.getDescription().contains("操作乘车人过于频繁") || customeruser.getDescription()
                                .contains("您需要提供真实、准确的本人资料，为了保障您的个人信息安全，请您到就近办理客运售票业务的铁路车站完成身份核验"))) {
                    int j = 0;
                    //重拿账号次数，上面的判断如添加新的，注意同步判断
                    int i_enlogin_idverification_sum = geti_enlogin_idverification_sum(orderType,
                            customeruser.getDescription());
                    String shenfenzhengheyanjieguo = "未登录";
                    do {
                        if (i_enlogin_idverification_sum == 0) {
                            break;
                        }
                        if (GoAccountSystem()) {
                            //获取参数
                            int type = isExclusive ? AccountSystem.OrderAccount : AccountSystem.PassengerAccount;
                            if (orderType == 3) {
                                //取客人数据
                                Customeruser temp = TrainAccountSrcUtil.getTrainAccountSrcById(order.getId());
                                //走账号系统
                                customeruser = GetCustomerAccount(temp.getLoginname(), temp.getLogpassword());
                            }
                            //客人账号的Cookie
                            else if (orderType == 4) {
                                refreshCookieFromInterface(order);
                                //取客人数据
                                Customeruser temp = TrainAccountSrcUtil.getTrainAccountSrcById(order.getId());
                                //虚拟一个账号
                                customeruser = GetCustomerAccountByCookieWay(temp);
                            }
                            else {
                                //获取账号
                                customeruser = GetUserFromAccountSystem(type, AccountSystem.NullName, isExclusive,
                                        AccountSystem.NullMap);
                            }
                        }
                        else {
                            customeruser = getCustomerUserByEnname(false, random, isExclusive);
                        }
                        if (j > 0) {
                            WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":" + random + ":第" + j
                                    + "次重复获取账号:" + customeruser.getId());

                        }
                        if (customeruser.getId() > 0) {
                            //重新绑定乘客
                            customeruser = bindingPassenger(customeruser, TrainPassengerList, random, isExclusive,
                                    interfacetype, isPhone, passengerAdded);
                            if (j > 0) {
                                WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":" + random + ":第"
                                        + j + "次绑定乘客返回结果:" + customeruser.getDescription());
                            }
                            //绑定乘客返回
                            shenfenzhengheyanjieguo = customeruser.getDescription();
                            if (shenfenzhengheyanjieguo == null) {
                                break;
                            }
                            //日志
                            WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":" + random + ":"
                                    + customeruser.getLoginname() + "身份验证:4:返回结果:" + shenfenzhengheyanjieguo);
                            if ("未登录".equals(shenfenzhengheyanjieguo)
                                    || "已满".equals(shenfenzhengheyanjieguo)
                                    || shenfenzhengheyanjieguo.contains("您的身份信息未通过核验")
                                    || customeruser.getDescription().contains("您在12306网站注册时填写信息有误")
                                    || customeruser.getDescription().contains("您注册的信息与其他用户重复")
                                    || customeruser.getDescription().contains("手机核验")
                                    || customeruser.getDescription().contains("操作乘车人过于频繁")
                                    || customeruser.getDescription().contains(
                                            "您需要提供真实、准确的本人资料，为了保障您的个人信息安全，请您到就近办理客运售票业务的铁路车站完成身份核验")) {
                                if (!customeruser.isFromAccountSystem()) {
                                    //closeCus(customeruser.getId());
                                }
                            }
                        }
                        j++;
                    }
                    while (("未登录".equals(shenfenzhengheyanjieguo) || "已满".equals(shenfenzhengheyanjieguo)
                            || shenfenzhengheyanjieguo.contains("您的身份信息未通过核验")
                            || customeruser.getDescription().contains("您在12306网站注册时填写信息有误")
                            || customeruser.getDescription().contains("您注册的信息与其他用户重复")
                            || customeruser.getDescription().contains("手机核验")
                            || customeruser.getDescription().contains("操作乘车人过于频繁") || customeruser.getDescription()
                            .contains("您需要提供真实、准确的本人资料，为了保障您的个人信息安全，请您到就近办理客运售票业务的铁路车站完成身份核验"))
                            && j < i_enlogin_idverification_sum);
                    if (!passengerAdded
                            && shenfenzhengheyanjieguo != null
                            && ("未登录".equals(shenfenzhengheyanjieguo) || "已满".equals(shenfenzhengheyanjieguo)
                                    || shenfenzhengheyanjieguo.contains("您的身份信息未通过核验")
                                    || customeruser.getDescription().contains("您在12306网站注册时填写信息有误")
                                    || customeruser.getDescription().contains("您注册的信息与其他用户重复")
                                    || customeruser.getDescription().contains("手机核验")
                                    || customeruser.getDescription().contains("操作乘车人过于频繁") || customeruser
                                    .getDescription().contains("您需要提供真实、准确的本人资料，为了保障您的个人信息安全，请您到就近办理客运售票业务的铁路车站完成身份核验"))) {
                        if (CustomerAccount) {
                            WriteLog.write("托管账号身份验证真实原因", random + "--->" + customeruser.getDescription());
                            if ("已满".equals(shenfenzhengheyanjieguo)) {
                                customeruser.setDescription("已满");
                            }
                            else if ("未登录".equals(shenfenzhengheyanjieguo)) {
                                customeruser.setDescription("用户12306账号登录失败");
                            }
                            else if (shenfenzhengheyanjieguo.contains("您的身份信息未通过核验")) {
                                customeruser.setDescription(shenfenzhengheyanjieguo);
                            }
                            else if (customeruser.getDescription().contains("操作乘车人过于频繁")) {
                                customeruser.setDescription("用户12306账号登录失败");
                            }
                        }
                        else {
                            customeruser.setDescription("添加乘客 未通过身份效验 " + TrainPassengerList.get(0).getName()
                                    + TrainPassengerList.get(0).getIdnumber());
                        }
                        WriteLog.write("TongchengSupplyMethod_onceAgain", order.getId() + ":" + random + ":"
                                + customeruser.getLoginname() + "身份验证:5:返回结果:多次身份验证码全部失败了");
                    }
                }
            }
            //已添加
            if (passengerAdded) {
                return GetCustomerAccountByCookieWay(customeruser);
            }
            //如果是新审核出来的冒用  重新获取一次账号
            if (customeruser != null && customeruser.getId() > 0 && customeruser.getDescription() != null
                    && customeruser.getDescription().contains("冒用")) {
                //            freeCustomeruser(customeruser, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                //                    AccountSystem.NullDepartTime);
                num = num + 1;
                customeruser = onceAgain(order, random, customeruser, num, nopassengernum, isPhone, false);
            }
            if (customeruser != null && customeruser.getDescription() != null
                    && customeruser.getDescription().contains("nopassenger_")) {
                nopassengernum = nopassengernum + 1;
                customeruser = onceAgain(order, random, customeruser, num, nopassengernum, isPhone, false);
            }
        }
        if (isFalseLy && customeruser.getId() > 0 && customeruser.getLoginname() != null) {
            WriteLog.write("冒用乘客_账号复用", random + "--->" + customeruser.getLoginname());
        }
        return customeruser;
    }

    /**
     * 未登录时重新身份验证次数
     * @return
     * @time 2015年10月28日 上午11:42:23
     * @author chendong
     * @param index 
     */
    private int geti_enlogin_idverification_sum(int ordertype, String description) {
        int i_enlogin_idverification_sum = 5;
        try {
            //客人账号不重试>>非未登录
            if ((ordertype == 3 || ordertype == 4) && !"未登录".equals(description)) {
                i_enlogin_idverification_sum = 0;
            }
            else {
                i_enlogin_idverification_sum = Integer.valueOf(getSysconfigString("enlogin_idverification_sum"));
                if (i_enlogin_idverification_sum < 0) {
                    i_enlogin_idverification_sum = 5;
                }
            }
        }
        catch (Exception e1) {
            i_enlogin_idverification_sum = 5;
        }
        return i_enlogin_idverification_sum;
    }

    /**
     * 绑定乘客
     * @param cust
     * @param TrainPassengerList
     * @param isExclusive 是否独占账号，独占表示下单
     * @time 2014年12月31日 上午11:25:49
     * @author chendong
     */
    public Customeruser bindingPassenger(Customeruser user, List<Trainpassenger> TrainPassengerList, int random,
            boolean isExclusive, int interfacetype, boolean isPhone, boolean passengerAdded) {
        //是否释放账号
        boolean isfreecustomeruser = true;
        //Cookie为空
        if (ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //错误描述
            user.setIsenable(0);
            user.setDescription("未登录");
            //释放标识
            user.setCanCreateOrder(true);
            //释放账号
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
            //直接返回
            return user;
        }
        int freeType = AccountSystem.FreeNoCare;//释放类型，用于账号系统
        int TrainPassengerListSize = TrainPassengerList.size();
        JSONObject rep16Result = new JSONObject();
        long starttime = System.currentTimeMillis();
        //循环绑定乘客
        for (int i = 0; i < TrainPassengerListSize; i++) {
            isfreecustomeruser = true;//每次进来，默认释放账号
            Trainpassenger trainpassenger = TrainPassengerList.get(i);
            //已添加、非学生，不再添加常旅
            if (passengerAdded
                    && (trainpassenger.getTrainstudentinfos() == null || trainpassenger.getTrainstudentinfos().size() <= 0)) {
                continue;
            }
            //shenfenzhengheyanjieguo可能的值 ;NOPASSENGER|已满|待核验|未登录|已通过|true
            String shenfenzhengheyanjieguo = "";
            //            String shenfenzhengheyanjieguo = rep16Method(trainpassenger.getName(), trainpassenger.getIdnumber(),
            //                    getIdtype12306(trainpassenger.getIdtype()), user.getCardnunber(), r1);
            //            WriteLog.write("TongchengSupplyMethod_getcustomeruser", r1 + ":" + user.getLoginname() + ":"
            //                    + trainpassenger.getName() + ":" + trainpassenger.getIdnumber() + ":"
            //                    + getIdtype12306(trainpassenger.getIdtype()) + ":Cardnunber:" + user.getCardnunber() + ":"
            //                    + ":身份验证:1:返回结果:" + shenfenzhengheyanjieguo);
            int j = 0;
            do {//如果是NOPASSENGER再做下处理再往下执行 
                String rep16ResultStr = rep16Method(trainpassenger, trainpassenger.getIdnumber(),
                        getIdtype12306(trainpassenger.getIdtype()), user, random, isPhone);
                WriteLog.write("TongchengSupplyMethod_getcustomeruser", random + ":" + trainpassenger.getName() + ":"
                        + trainpassenger.getIdnumber() + ":" + ":身份验证:" + j + "次:返回结果:" + rep16ResultStr
                        + ":passengerAdded:" + passengerAdded);
                if (rep16ResultStr.contains("{")) {
                    //为了兼容旧版本 结果不是JSON
                    try {
                        rep16Result = JSONObject.parseObject(rep16ResultStr);
                        shenfenzhengheyanjieguo = rep16Result.getString("result");
                    }
                    catch (Exception e) {
                        shenfenzhengheyanjieguo = rep16ResultStr;
                    }
                }
                else {
                    shenfenzhengheyanjieguo = rep16ResultStr;
                }
                if (j > 0) {
                    try {
                        Thread.sleep(500);//休息0.5秒
                    }
                    catch (Exception e) {
                    }
                }
                j++;
            }
            while (("".equals(shenfenzhengheyanjieguo) || "NOPASSENGER".equals(shenfenzhengheyanjieguo)) && j < 4);
            //已添加
            if (passengerAdded) {
                //用户未登录
                if ("未登录".equals(shenfenzhengheyanjieguo) || "false".equals(shenfenzhengheyanjieguo)) {
                    user.setIsenable(0);
                    user.setDescription("未登录");
                    break;
                }
            }
            else if ("已通过".equals(shenfenzhengheyanjieguo) || "true".equals(shenfenzhengheyanjieguo)) {
                user.setIsenable(1);
                isfreecustomeruser = false;
                try {
                    ExecutorService pool = Executors.newFixedThreadPool(1);
                    Thread t1 = new MyThreadTrainIdMongoInsertPassenger(trainpassenger, user);
                    pool.execute(t1);
                    pool.shutdown();
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException("TongchengSupplyMethod_IdMongo_ERROR", e);
                }
                if (interfacetype == TrainInterfaceMethod.TONGCHENG) {
                    setUnmatchedpasslist(trainpassenger, accountJsonObject(user, "可用"), shenfenzhengheyanjieguo);
                }
            }
            else if ("待核验".equals(shenfenzhengheyanjieguo) || shenfenzhengheyanjieguo.contains("证件号码输入有误")) {
                try {
                    trainpassenger.setAduitstatus(1);
                    Server.getInstance().getTrainService().updateTrainpassenger(trainpassenger);
                    //                    user.setDescription("添加乘客" + trainpassenger.getName() + "未通过身份效验" + trainpassenger.getIdnumber());
                    user.setDescription("添加乘客 未通过身份效验 " + trainpassenger.getName() + trainpassenger.getIdnumber());
                    if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                        setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                    }
                }
                catch (Exception e) {
                }
                user.setIsenable(0);
                break;
            }
            else if ("已满".equals(shenfenzhengheyanjieguo)) {
                user.setIsenable(0);
                user.setDescription(shenfenzhengheyanjieguo);
                freeType = AccountSystem.FreePassengerFull;
                break;
            }
            else if ("未登录".equals(shenfenzhengheyanjieguo) || "false".equals(shenfenzhengheyanjieguo)) {
                user.setIsenable(0);
                user.setDescription("未登录");
                freeType = AccountSystem.FreeNoLogin;
                break;
            }
            else if (shenfenzhengheyanjieguo.contains("您的身份信息未通过核验")) {
                freeType = AccountSystem.FreeNoCheck;
                WriteLog.write("您的身份信息未通过核验", user.getLoginname());
                user.setDescription(shenfenzhengheyanjieguo);
                user.setIsenable(0);
                if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                    setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                }
                if (interfacetype == TrainInterfaceMethod.TONGCHENG) {
                    setUnmatchedpasslist(trainpassenger, accountJsonObject(user, "账号被封"), shenfenzhengheyanjieguo);
                    //将不可用的帐号同步推送给同程
                    checkAccountMethod(user, trainpassenger, 0);
                }
                break;
            }
            else if (shenfenzhengheyanjieguo.contains("您在12306网站注册时填写信息有误")) {
                freeType = 31;
                WriteLog.write("您在12306网站注册时填写信息有误", user.getLoginname());
                user.setDescription(shenfenzhengheyanjieguo);
                user.setIsenable(0);
                if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                    setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                }
                if (interfacetype == TrainInterfaceMethod.TONGCHENG) {
                    setUnmatchedpasslist(trainpassenger, accountJsonObject(user, "账号被封"), shenfenzhengheyanjieguo);
                    //将不可用的帐号同步推送给同程
                    checkAccountMethod(user, trainpassenger, 0);
                }
                break;
            }
            else if (shenfenzhengheyanjieguo.contains("您注册的信息与其他用户重复")) {
                freeType = 32;
                WriteLog.write("您注册的信息与其他用户重复", user.getLoginname());
                user.setDescription(shenfenzhengheyanjieguo);
                user.setIsenable(0);
                if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                    setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                }
                if (interfacetype == TrainInterfaceMethod.TONGCHENG) {
                    setUnmatchedpasslist(trainpassenger, accountJsonObject(user, "账号被封"), shenfenzhengheyanjieguo);
                    //将不可用的帐号同步推送给同程
                    checkAccountMethod(user, trainpassenger, 0);
                }
                break;
            }
            else if (shenfenzhengheyanjieguo.contains("手机核验")) {
                freeType = 33;
                WriteLog.write("手机核验", user.getLoginname());
                user.setDescription(shenfenzhengheyanjieguo);
                user.setIsenable(0);
                if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                    setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                }
                if (interfacetype == TrainInterfaceMethod.TONGCHENG) {
                    setUnmatchedpasslist(trainpassenger, accountJsonObject(user, "未绑定手机号"), shenfenzhengheyanjieguo);
                    //将不可用的帐号同步推送给同程
                    checkAccountMethod(user, trainpassenger, 0);
                }
                break;
            }
            else if (shenfenzhengheyanjieguo.contains("操作乘车人过于频繁")) {
                freeType = AccountSystem.FreeCurrent;
                WriteLog.write("操作乘车人过于频繁", user.getLoginname());
                user.setDescription(shenfenzhengheyanjieguo);
                user.setIsenable(0);
                if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                    setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                }
                if (interfacetype == TrainInterfaceMethod.TONGCHENG) {
                    setUnmatchedpasslist(trainpassenger, accountJsonObject(user, "可用"), shenfenzhengheyanjieguo);
                }
                break;
            }
            else if (shenfenzhengheyanjieguo.contains("您需要提供真实、准确的本人资料，为了保障您的个人信息安全，请您到就近办理客运售票业务的铁路车站完成身份核验")) {
                freeType = 39;
                WriteLog.write("您需要提供真实、准确的本人资料，为了保障您的个人信息安全，请您到就近办理客运售票业务的铁路车站完成身份核验", user.getLoginname());
                user.setDescription(shenfenzhengheyanjieguo);
                user.setIsenable(0);
                if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                    setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                }
                if (interfacetype == TrainInterfaceMethod.TONGCHENG) {
                    setUnmatchedpasslist(trainpassenger, accountJsonObject(user, "账号被封"), shenfenzhengheyanjieguo);
                    //将不可用的帐号同步推送给同程
                    checkAccountMethod(user, trainpassenger, 0);
                }
                break;
            }
            else if (isExclusive && shenfenzhengheyanjieguo.contains("身份信息涉嫌被他人冒用")) {
                String name = shenfenzhengheyanjieguo.split("的身份信息涉嫌被他人冒用")[0];
                user.setDescription(name + "_身份信息涉嫌被他人冒用，请持本人身份证件原件到就近铁路客运车站办理身份核验");
                if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                    setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                }
                user.setIsenable(0);
                try {
                    //                    String key = "falsely_" + trainpassenger.getIdnumber();
                    long idNumber = Long.parseLong(trainpassenger.getIdnumber().toUpperCase().replace("X", "10"));
                    //                    OcsMethod.getInstance().add(key, "falsely");
                    String sql = "INSERT INTO FalselyPassenger (name,IdNumber) VALUES ('" + name + "'," + idNumber
                            + ")";
                    DBHelperAccount.executeSql(sql);
                    //这里new Thread 写你的逻辑  trainpassenger就是那个冒用的乘客
                    checkAccountMethod(user, trainpassenger, 315);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            else {
                user.setDescription("nopassenger_添加乘客" + trainpassenger.getName() + "未通过身份效验"
                        + trainpassenger.getIdnumber());
                user.setIsenable(0);
                if (interfacetype == TrainInterfaceMethod.TAOBAO) {
                    setSubOrderIdMethod(trainpassenger, shenfenzhengheyanjieguo);
                }
                break;
            }
        }
        //已添加
        if (passengerAdded) {
            return user;
        }
        long endtime = System.currentTimeMillis() - starttime;
        int endtimes = (int) (endtime / 1000);
        WriteLog.write("TongchengSupplyMethod_getcustomeruser",
                random + ":耗时:" + endtimes + "秒(" + endtime + "):" + user.getLoginname() + ":isfreecustomeruser:"
                        + isfreecustomeruser + ":Description:" + user.getDescription());
        new MyThreadFresh12306Account(rep16Result, user).start();
        //身份验证
        if (!isExclusive) {
            //释放标识
            user.setCanCreateOrder(true);
            //释放账号>>不释放表示验证通过
            Account12306Util.free12306Account(user, freeType, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime, !isfreecustomeruser);
        }
        //下单>>释放账号
        else if (isfreecustomeruser) {
            //释放标识
            user.setCanCreateOrder(true);
            //释放账号
            freeCustomeruser(user, freeType, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
        }
        return user;
    }

    /**
     * @param isfirst
     *            是否是第一次获取
     * @return
     * @time 2015年1月2日 下午10:18:17
     * @author chendong
     * @param r1
     */
    public synchronized Customeruser getCustomerUserByEnname(boolean isfirst, int r1, boolean ischangeenname) {
        Customeruser cust = getonecustomeruser(ischangeenname);
        WriteLog.write("TongchengSupplyMethod_getcustomeruser", r1 + ":getCustomerUserByEnname:cust.getLoginname():"
                + cust.getLoginname() + ":cust.getId():" + cust.getId() + ":ischangeenname:" + ischangeenname);
        if (cust.getId() > 0) {// 如果获取到了一个账号
            /*cust = Server.getInstance().getMemberService().findCustomeruser(cust.getId());*/
            WriteLog.write("TongchengSupplyMethod_getcustomeruser",
                    r1 + ":getCustomerUserByEnname:Enname:" + cust.getEnname() + ":State:" + cust.getState());
            if ("1".equals(cust.getEnname()) && cust.getState() == 1) {
                if (ischangeenname) {//是否独占,这个账号如果true,把这个账号改为使用中,这里已经在存储过程中实现修改状态了,不用在这改了
                    //                    String sql = "UPDATE T_CUSTOMERUSER SET C_ENNAME='0' where ID = " + cust.getId() + " ";
                }
                else {
                    int updatecount = 0;
                    String sql = "UPDATE T_CUSTOMERUSER SET C_ENNAME='1' where ID = " + cust.getId() + " ";
                    try {
                        updatecount = Server.getInstance().getSystemService().excuteGiftBySql(sql);
                    }
                    catch (Exception e) {
                        logger.error("TongchengSupplyMethod_getcustomeruser", e.fillInStackTrace());
                    }
                    WriteLog.write("TongchengSupplyMethod_getcustomeruser", r1 + ":getCustomerUserByEnname:Enname:"
                            + cust.getEnname() + ":updatecount:" + updatecount);
                }
            }
            else if (isfirst) {
                cust = getCustomerUserByEnname(true, r1, ischangeenname);
            }
        }
        return cust;
    }

    /**
     * 从数据库获取一个可以使用的12306账号
     * 
     * @return
     * @time 2015年3月27日 下午5:07:19
     * @author chendong
     * @param ischangeenname 是否独占一个账号 true独占,false不独占
     */
    @SuppressWarnings("rawtypes")
    private Customeruser getonecustomeruser(boolean ischangeenname) {
        Customeruser cust = new Customeruser();
        /*String sql = "SELECT top 1 * FROM T_CUSTOMERUSER WITH(NOLOCK) "
                + "WHERE C_STATE=1 AND C_TYPE = 4 AND C_ENNAME='1' AND C_ISENABLE=1 "
                + "AND C_LOGINNUM>0 AND C_LOGINNUM<90 ORDER BY C_LOGINNUM ASC";
        List<Customeruser> customeruser12306account = Server.getInstance().getMemberService()
                .findAllCustomeruserBySql(sql, -1, 0);*/
        List list = Server.getInstance().getSystemService().findMapResultByProcedure("sp_CustomerUser_GetOne12306");
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            String id = objectisnull(map.get("ID"));
            String loginname = objectisnull(map.get("C_LOGINNAME"));
            String cardnunber = objectisnull(map.get("C_CARDNUNBER"));
            String description = objectisnull(map.get("C_DESCRIPTION"));
            String isenable = objectisnull(map.get("C_ISENABLE"));
            String state = objectisnull(map.get("C_STATE"));
            String enname = objectisnull(map.get("C_ENNAME"));
            cust.setId(Long.parseLong(id));
            cust.setLoginname(loginname);
            cust.setCardnunber(cardnunber);
            cust.setDescription(description);
            cust.setIsenable(Integer.parseInt(isenable));
            cust.setState(Integer.parseInt(state));
            cust.setEnname(enname);
        }
        return cust;
    }

    /**
     * 如果为空返回 "0"
     * 
     * @param object
     * @return
     * @time 2015年4月15日 下午1:57:54
     * @author chendong
     */
    private String objectisnull(Object object) {
        return object == null ? "0" : object.toString();
    }

    /**
     * 身份证核验，绑定账号,新身份验证中间方法
     * 
     * @param passengerName
     *            姓名
     * @param id_no
     *            身份证号码
     * @param id_type
     *            证件类型
     * @param cookieString
     *            cookie
     * @return
     * @time 2014年12月19日 下午7:41:51
     * @author chendong
     */
    public String rep16Method(Trainpassenger trainpassenger, String id_no, String id_type, Customeruser user, int r1,
            boolean isPhone) {
        String resultString = "";
        RepServerBean rep = RepServerUtil.getRepServer(user, false);
        String repUrl = rep.getUrl();
        String passengerName = trainpassenger.getName();
        try {
            passengerName = URLEncoder.encode(passengerName, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String paramContent = "datatypeflag=16&name=" + passengerName + "&id_no=" + id_no + "&id_type=" + id_type
                + "&cookie=" + user.getCardnunber() + JoinCommonAccountInfo(user, rep);
        String accoutPhone = "";
        if (isPhone) {
            paramContent = paramContent.replace("datatypeflag=16&", "datatypeflag=1016&");
            //            repUrl = repUrl.replace("traininit", "MobileClient");
            accoutPhone = JoinCommonAccountPhone(user);
            paramContent += accoutPhone;
        }
        try {
            WriteLog.write("TongchengSupplyMethod_rep16Method_Trainstudentinfos", r1
                    + ":getTrainstudentinfos:"
                    + (trainpassenger.getTrainstudentinfos() == null ? "null" : trainpassenger.getTrainstudentinfos()
                            .size()));
        }
        catch (Exception e1) {
            e1.printStackTrace();
            WriteLog.write("TongchengSupplyMethod_rep16Method_Trainstudentinfos", r1 + ":getTrainstudentinfos:null");
        }
        //学生信息
        String studentInfo = "";
        //是学生票
        if (trainpassenger.getTrainstudentinfos() != null && trainpassenger.getTrainstudentinfos().size() > 0) {
            try {
                //学生信息
                studentInfo = addStudentInfo(trainpassenger.getTrainstudentinfos().get(0));
                //拼接数据
                paramContent += "&passenger_data=" + studentInfo;
            }
            catch (Exception e) {
                WriteLog.write("TongchengSupplyMethod_rep16Method_error", r1 + ":" + trainpassenger.getName() + ":"
                        + trainpassenger.getIdnumber() + ":学生票信息获取失败");
            }
        }
        WriteLog.write("TongchengSupplyMethod_rep16Method", r1 + ":paramContent:" + paramContent);
        resultString = SendPostandGet.submitPost(repUrl, paramContent, "utf-8").toString();
        if ("".equals(resultString)) {//如果没有的话重新传一遍
            resultString = SendPostandGet.submitPostTimeOutFiend(repUrl, paramContent, "utf-8", 50 * 1000).toString();
        }
        if (isPhone) {
            freshPhone(accoutPhone, resultString, user);
        }
        //淘宝托管>>用户未登录
        if (resultString != null && resultString.contains("用户未登录")
                && !ElongHotelInterfaceUtil.StringIsNull(studentInfo) && RepServerUtil.changeRepServer(user)) {
            //切换REP
            rep = RepServerUtil.getTaoBaoTuoGuanRepServer(user, false);
            //类型正确
            if (rep.getType() == 1) {
                //记录日志
                WriteLog.write("TongchengSupplyMethod_rep16Method", r1 + ":切换Rep:" + rep.getUrl());
                //重拼参数
                paramContent = "datatypeflag=16&name=" + passengerName + "&id_no=" + id_no + "&id_type=" + id_type
                        + "&cookie=" + user.getCardnunber() + JoinCommonAccountInfo(user, rep) + "&passenger_data="
                        + studentInfo;
                //重新请求
                resultString = SendPostandGet.submitPostTimeOutFiend(rep.getUrl(), paramContent, "UTF-8", 50 * 1000)
                        .toString();
            }
        }
        WriteLog.write("TongchengSupplyMethod_rep16Method", r1 + ":repUrl:" + repUrl + ":resultString:" + resultString);
        return resultString;
    }

    /**
     * 身份验证添加学生票信息
     * @param trainStudentInfo
     * @return
     * @time 2015年5月5日 下午3:12:20
     * @author fiend
     */
    private String addStudentInfo(TrainStudentInfo trainStudentInfo) {
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("passenger_type", "3");
            if (trainStudentInfo.getSchoolprovincecode() != null
                    && !"".equals(trainStudentInfo.getSchoolprovincecode())) {
                jsonobject.put("province_code", trainStudentInfo.getSchoolprovincecode());
            }
            else {
                jsonobject.put("province_code", TrainUtil.getProvince2Code(trainStudentInfo.getSchoolprovince()));
            }

            if (null != trainStudentInfo.getSchoolnamecode() && !"".equals(trainStudentInfo.getSchoolnamecode())) {
                jsonobject.put("school_code", trainStudentInfo.getSchoolnamecode());
            }
            else {
                jsonobject.put("school_code", TrainUtil.getstationTelecodebychineseName_schoolname(
                        trainStudentInfo.getSchoolname(), jsonobject.get("province_code").toString()));
            }

            jsonobject.put("school_name", URLEncoder.encode(trainStudentInfo.getSchoolname(), "UTF-8"));
            jsonobject.put("department", URLEncoder.encode(trainStudentInfo.getDepartment(), "UTF-8"));
            jsonobject.put("school_class", URLEncoder.encode(trainStudentInfo.getClasses(), "UTF-8"));
            jsonobject.put("student_no", trainStudentInfo.getStudentno());
            jsonobject.put("school_system", trainStudentInfo.getEductionalsystem());
            jsonobject.put("enter_year", trainStudentInfo.getEntranceyear());
            jsonobject.put("preference_card_no", trainStudentInfo.getStudentcard());
            jsonobject.put("preference_from_station_name", URLEncoder.encode(trainStudentInfo.getFromcity(), "UTF-8"));
            if (null != trainStudentInfo.getFromcitycode() && !"".equals(trainStudentInfo.getFromcitycode())) {
                jsonobject.put("preference_from_station_code", trainStudentInfo.getFromcitycode());
            }
            else {
                jsonobject.put("preference_from_station_code",
                        Train12306StationInfoUtil.getThreeByName(trainStudentInfo.getFromcity()));
            }
            jsonobject.put("preference_to_station_name", URLEncoder.encode(trainStudentInfo.getTocity(), "UTF-8"));
        }
        catch (Exception e) {
        }
        try {
            if (null != trainStudentInfo.getTocitycode() && !"".equals(trainStudentInfo.getTocitycode())) {
                jsonobject.put("preference_to_station_code", trainStudentInfo.getTocitycode());
            }
            else {
                jsonobject.put("preference_to_station_code",
                        Train12306StationInfoUtil.getThreeByName(trainStudentInfo.getTocity()));
            }
        }
        catch (Exception e) {
        }
        return jsonobject.toJSONString();
    }

    /**
     * 占用一个账号后的解锁， 支付完成等调用
     * 
     * 以下为账号系统备注
     * @param freeType 释放类型 1:NoCare；2:仅当天使用；3:发车时间后才可使用；4:分配给其他业务(暂未用)
     * @param cancalCount 取消次数，用于取消时释放账号，其他业务必须传0
     * @param departTime 发车时间，freeType为3时有效，其他请设为空
     */
    public void freeCustomeruser(Customeruser user, int freeType, int freeCount, int cancalCount, Timestamp departTime) {
        if (user == null) {
            return;
        }
        //账号系统释放账号
        if (user.isFromAccountSystem()) {
            FreeUserFromAccountSystem(user, freeType, freeCount, cancalCount, departTime);
        }
        //原先系统释放账号
        else {
            try {
                String sql = "UPDATE T_CUSTOMERUSER SET C_ENNAME = '1' where id=" + user.getId();
                Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            }
            catch (Exception e) {
                logger.error("freecustomeruser", e.fillInStackTrace());
                JSONObject jsonobject = new JSONObject();
                jsonobject.put("customeruserObject", user);
                sendMQmessage("update_12306account_err", jsonobject.toJSONString());
            }
        }
    }

    /**
     * 拿到一个账号，把这个账号修改为使用中的状态
     */
    public boolean LockCustomeruser(Customeruser user) {
        if (user == null) {
            return false;
        }
        if (user.isFromAccountSystem()) {
            return true;
        }
        try {
            // enname>>>>>12306账号的当前使用状态，1和空代表可用，0代表使用中，2代表已经取消三次今日不可用
            boolean isTrue = false;
            if (ElongHotelInterfaceUtil.StringIsNull(user.getEnname())) {
                isTrue = true;
            }
            else if ("1".equals(user.getEnname().trim())) {
                isTrue = true;
            }
            if (isTrue && user.getState() == 1) {
                String sql = "UPDATE T_CUSTOMERUSER SET C_ENNAME = '0' where ID = " + user.getId();
                if (Server.getInstance().getSystemService().excuteAdvertisementBySql(sql) > 0) {
                    return true;
                }
            }
        }
        catch (Exception e) {
        }
        return false;
    }

    /**
     * 这个账号取消三次后今天不能使用了 支付完成等调用
     * 
     * @param cust
     * @time 2014年12月23日 下午6:31:03
     * @author chendong
     */
    public void isEnableTodayCustomeruser(Customeruser user) {
        if (user == null) {
            return;
        }
        if (user.isFromAccountSystem()) {
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ThreeCancel,
                    AccountSystem.NullDepartTime);
        }
        else {
            try {
                String loginName = user.getLoginname();
                String sql = "UPDATE T_CUSTOMERUSER SET C_ISENABLE = 4 where C_LOGINNAME = '" + loginName + "' ";
                Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 这个账号由于不能使用了把 isenable 改为0表示禁用了
     * 
     * @param cust
     * @time 2014年12月23日 下午6:31:03
     * @author chendong
     */
    public void isEnableForeverCustomeruser(Customeruser user, String description) {
        if (user == null) {
            return;
        }
        if (user.isFromAccountSystem()) {
            freeCustomeruser(user, AccountSystem.FreeNoCheck, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                    AccountSystem.NullDepartTime);
        }
        else {
            try {
                String sql = "UPDATE T_CUSTOMERUSER C_ISENABLE = 0 , C_STATE = 0, C_DESCRIPTION = '" + description
                        + "' where C_LOGINNAME = '" + user.getLoginname() + "'";
                Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 根据12306账号的用户名获取获取CustomerUser
     * @param refreshCookie 是否刷新Cookie，客人Cookie方式有效
     */
    public Customeruser getCustomeruserBy12306Account(Trainorder order, int random, boolean refreshCookie) {
        //客人账号名和密码
        if (order.getOrdertype() == 3) {
            //取客人数据
            Customeruser temp = TrainAccountSrcUtil.getTrainAccountSrcById(order.getId());
            //走账号系统
            return GetCustomerAccount(temp.getLoginname(), temp.getLogpassword());
        }
        //客人账号的Cookie
        else if (order.getOrdertype() == 4) {
            //刷新Cookie
            if (refreshCookie) {
                refreshCookieFromInterface(order);
            }
            //取客人数据
            Customeruser temp = TrainAccountSrcUtil.getTrainAccountSrcById(order.getId());
            //虚拟一个账号
            return GetCustomerAccountByCookieWay(temp);
        }
        //订单存储
        String loginname = order.getSupplyaccount();
        //12306用户名
        loginname = loginname.split("/")[0];
        //走账号系统
        if (GoAccountSystem()) {
            return GetUserFromAccountSystem(AccountSystem.LoginNameAccount, loginname,
                    !AccountSystem.waitWhenNoAccount, AccountSystem.NullMap);
        }
        Customeruser cust = new Customeruser();
        /*
        String sql = "SELECT * FROM T_CUSTOMERUSER WHERE " + Customeruser.COL_loginname + " ='" + loginname + "'";
        List<Customeruser> customerusers = Server.getInstance().getMemberService().findAllCustomeruserBySql(sql, 1, 0);
        if (customerusers.size() > 0) {
            cust = customerusers.get(0);
            if (cust.getState() == 0) {
                cust = login12306(cust, random);
            }
            // for (int i = 0; i < prainpassengerList.size(); i++) {
            // Trainpassenger trainpassenger = prainpassengerList.get(i);
            // String shenfenzhengheyanjieguo =
            // rep16Method(trainpassenger.getName(),
            // trainpassenger.getIdnumber(),
            // trainpassenger.getIdtype() + "", cust.getCardnunber());
            // if ("已通过".equals(shenfenzhengheyanjieguo)) {
            // }
            // else if ("未登录".equals(shenfenzhengheyanjieguo)) {
            // cust = login12306(cust);
            // i--;
            // }
            // else {
            // cust.setDescription(trainpassenger.getName() + ":身份未通过|"
            // + (cust.getDescription() == null ? "" : cust.getDescription()));
            // cust.setIsenable(0);
            // }
            // }
        }
        */
        return cust;
    }

    /**
     * 12306返回信息是否齐全
     * 
     * @param str
     *            12306字符串
     * @param passengers
     *            本地乘客list
     * @return
     */
    public static boolean isall(String str, List<Trainpassenger> passengers) {
        JSONObject jsono = JSONObject.parseObject(str);
        JSONObject jsonodata = jsono.getJSONObject("data");
        try {
            if (jsonodata != null) {
                JSONArray jsonaorderDBList = jsonodata.getJSONArray("orderDBList");
                if (jsonaorderDBList != null) {
                    for (int j = 0; j < jsonaorderDBList.size(); j++) {// 获取所有订单
                        JSONObject jsonoorderDBList = jsonaorderDBList.getJSONObject(j);
                        if (jsonoorderDBList.getJSONArray("tickets") != null) {
                            JSONArray tickets = jsonoorderDBList.getJSONArray("tickets");
                            if (tickets.size() == passengers.size() && str.contains(passengers.get(0).getName())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {

        }
        return false;
    }

    /**
     * 重新获取未支付订单
     * 
     * @param customeruser
     * @param passengers
     *            passengers=蔡秀娟|1|420117199005112741|1|二等座|1330,唐正|1|
     *            320882199011201830|1|二等座|1330
     * @return
     */
    public String getstr(Customeruser customeruser, Trainorder trainorder, String passengers) {
        RepServerBean rep = RepServerUtil.getRepServer(customeruser, false);
        String url = rep.getUrl();
        String par = "datatypeflag=18&cookie=" + customeruser.getCardnunber() + "&passengers=" + passengers
                + "&trainorderid=" + trainorder.getId() + JoinCommonAccountInfo(customeruser, rep);
        WriteLog.write("t同程火车票接口_4.5申请分配座位席别_getstr", "订单号:" + trainorder.getId() + ":获取未完成订单（问题订单使用） ，调取TrainInit:"
                + url + ":" + par);
        String infodata = "";
        try {
            infodata = SendPostandGet.submitPost(url, par, "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WriteLog.write("t同程火车票接口_4.5申请分配座位席别_getstr", "订单号:" + trainorder.getId() + ":获取未完成订单（问题订单使用） ，调取TrainInit:返回"
                + infodata);
        return infodata;
    }

    private String getpassengers(Trainorder trainorder) {
        String passengers = "";
        for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            String pname = trainpassenger.getName();
            String pidnumber = trainpassenger.getIdnumber();
            int pidtype = trainpassenger.getIdtype();
            Trainticket trainticket = trainpassenger.getTraintickets().get(0);
            String idtype = "1";
            if (1 == pidtype) {
                idtype = "1";
            }
            if (3 == pidtype) {
                idtype = "B";
            }
            if (4 == pidtype) {
                idtype = "C";
            }
            if (5 == pidtype) {
                idtype = "G";
            }
            String tickettype = trainticket.getTickettype() + "";
            if (null == trainorder.getQunarOrdernumber() || "".equals(trainorder.getQunarOrdernumber())) {
                tickettype = "1";
            }
            else {
                if ("0".equals(tickettype)) {
                    tickettype = "2";
                }
            }
            String passenger = pname + "|" + idtype + "|" + pidnumber + "|" + tickettype + "|"
                    + trainticket.getSeattype() + "|"
                    + Float.valueOf(trainticket.getPayprice()).toString().replace(".", "");
            passengers += passenger + ",";
        }
        passengers = passengers.substring(0, passengers.length() - 1);
        return passengers;
    }

    /**
     * 说明:将自动下单成功的订单信息存入数据库
     * 
     * @param str
     * @param trainorderid
     * @param loginname
     * @param isjointrip
     * @return 存储结果，电子订单号
     * @time 2014年8月30日 上午11:18:59
     * @author yinshubin
     * @param r1
     */
    public Trainorder saveOrderInfor(String msg, Trainorder trainorder, Customeruser customeruser, int r1) {
        for (int j = 0; j < 5; j++) {
            boolean isallinfo = isall(msg, trainorder.getPassengers());
            if (!isallinfo) {
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String passengers = getpassengers(trainorder);
                WriteLog.write("t同程火车票接口_4.5申请分配座位席别_saveOrderInfor", r1 + ":passengers:" + passengers);
                String msg_temp = getstr(customeruser, trainorder, passengers);
                WriteLog.write("t同程火车票接口_4.5申请分配座位席别_saveOrderInfor", r1 + ":" + j + "次:msg:" + msg);
                if (msg_temp.contains("data")) {
                    msg = msg_temp;
                }
            }
            else {
                break;
            }
        }
        try {
            WriteLog.write("t同程火车票接口_4.5申请分配座位席别", "12306返回信息:" + msg);
            JSONObject jsono = JSONObject.parseObject(msg);
            if (jsono.containsKey("data")) {
                JSONObject jsonodata = jsono.getJSONObject("data");
                if (jsonodata.containsKey("orderDBList")) {
                    JSONArray jsonaorderDBList = jsonodata.getJSONArray("orderDBList");
                    for (int j = 0; j < jsonaorderDBList.size(); j++) {// 获取所有订单
                        JSONObject jsonoorderDBList = jsonaorderDBList.getJSONObject(j);
                        if (jsonoorderDBList.toString().contains("待支付")) {// 拿到那个唯一的待支付订单
                            String sequence_no = jsonoorderDBList.getString("sequence_no");
                            //String order_date = jsonoorderDBList.getString("order_date");
                            if (jsonoorderDBList.containsKey("tickets")) {
                                JSONArray tickets = jsonoorderDBList.getJSONArray("tickets");
                                List<Trainpassenger> trainpassengers = trainorder.getPassengers();
                                trainpassengers = getpassengerfrom12306jsonbypassenger(trainpassengers, tickets);
                                Float orderprice = gettotalprice(trainpassengers);
                                trainorder.setOrderprice(orderprice);
                                trainorder.setSupplyprice(orderprice);
                            }
                            if (trainorder.getExtnumber() == null) {
                                trainorder.setExtnumber(sequence_no);
                            }
                            else {
                                trainorder.setExtnumber(sequence_no);
                            }
                            trainorder.setSupplyaccount(customeruser.getLoginname() == null ? "" : (customeruser
                                    .getLoginname() + "/UNION"));
                            trainorder.setChangesupplytradeno("请尽快支付:" + sequence_no);
                            try {
                                //DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                trainorder.setCreatetime(new Timestamp(System.currentTimeMillis()));
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            trainorder.setState12306(4);// 下单成功等待支付
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return trainorder;
    }

    /**
     * 根据12306返回的tickets,获取到对应人的票的信息,并且把人放进去 暂不用
     * 
     * @param trainpassenger
     * @param tickets
     * @time 2014年12月29日 下午5:49:51
     * @author chendong
     */
    public static List<Trainpassenger> getpassengerfrom12306jsonbypassenger(List<Trainpassenger> trainpassengers,
            JSONArray tickets) {
        for (int i = 0; i < tickets.size(); i++) {
            JSONObject jsonoticket = tickets.getJSONObject(i);
            int ticket_type_code = jsonoticket.getIntValue("ticket_type_code");// 乘客类型（票种）
            JSONObject jsonopassengerDTO = jsonoticket.getJSONObject("passengerDTO");
            String ticket_no = jsonoticket.getString("ticket_no");
            String coach_no = jsonoticket.getString("coach_no");
            String seat_name = jsonoticket.getString("seat_name");
            String seat_type_name = jsonoticket.getString("seat_type_name");
            Float str_ticket_price_page = jsonoticket.getFloatValue("str_ticket_price_page");
            //String passenger_name = jsonopassengerDTO.getString("passenger_name");
            //String id_cype_code = jsonopassengerDTO.getString("passenger_id_type_code");
            String passenger_id_no = jsonopassengerDTO.getString("passenger_id_no");
            /*
            int passenger_id_type_code = 1;
            if ("C".equals(id_cype_code)) {
                passenger_id_type_code = 4;
            }
            if ("B".equals(id_cype_code)) {
                passenger_id_type_code = 3;
            }
            if ("G".equals(id_cype_code)) {
                passenger_id_type_code = 5;
            }
            */
            if (ticket_type_code == 1 || ticket_type_code == 3) {// 成人、学生
                for (int j = 0; j < trainpassengers.size(); j++) {
                    Trainpassenger trainpassenger = trainpassengers.get(j);// 获取到对象的票的信息
                    // if (!"".equals(trainpassenger.getName()) &&
                    // !"".equals(trainpassenger.getIdnumber())
                    // && trainpassenger.getName().equals(passenger_name)
                    // &&
                    // trainpassenger.getIdnumber().toUpperCase().equals(passenger_id_no)
                    // && trainpassenger.getIdtype() == passenger_id_type_code)
                    // {
                    if (trainpassenger.getTraintickets().get(0).getTickettype() == ticket_type_code
                            && !"".equals(trainpassenger.getIdnumber())
                            && trainpassenger.getIdnumber().toUpperCase().equals(passenger_id_no)) {
                        trainpassenger.getTraintickets().get(0).setTicketno(ticket_no);
                        trainpassenger.getTraintickets().get(0).setTickettype(ticket_type_code);
                        trainpassenger.getTraintickets().get(0).setCoach(coach_no);
                        trainpassenger.getTraintickets().get(0).setSeatno(seat_name);
                        trainpassenger.getTraintickets().get(0).setPrice(str_ticket_price_page);
                        trainpassenger.getTraintickets().get(0).setSeattype(seat_type_name);
                        break;// 找到这个人了就不往下走了,继续找
                    }
                }
            }
            else if (ticket_type_code == 2) {// 儿童
                for (int j = 0; j < trainpassengers.size(); j++) {
                    Trainpassenger trainpassenger = trainpassengers.get(j);// 获取到对象的票的信息
                    if (trainpassenger.getTraintickets().get(0).getTickettype() == ticket_type_code
                            && trainpassenger.getTraintickets().get(0).getSeatno() == null
                            && !"".equals(trainpassenger.getIdnumber())
                            && trainpassenger.getIdnumber().toUpperCase().equals(passenger_id_no)) {
                        trainpassenger.getTraintickets().get(0).setTicketno(ticket_no);
                        trainpassenger.getTraintickets().get(0).setTickettype(ticket_type_code);
                        trainpassenger.getTraintickets().get(0).setCoach(coach_no);
                        trainpassenger.getTraintickets().get(0).setSeatno(seat_name);
                        trainpassenger.getTraintickets().get(0).setSeattype(seat_type_name);
                        trainpassenger.getTraintickets().get(0).setPrice(str_ticket_price_page);
                        break;// 找到这个人了就不往下走了,继续找
                    }
                    else {
                        continue;// 找不到这个人就继续走
                    }
                }
            }
        }
        return trainpassengers;
    }

    /**
     * 计算总价
     * 
     * @param trainpassengers
     * @return
     * @time 2014年12月29日 下午6:02:32
     * @author chendong
     */
    public Float gettotalprice(List<Trainpassenger> trainpassengers) {
        Float totalprice = 0F;
        for (int i = 0; i < trainpassengers.size(); i++) {
            try {
                totalprice += trainpassengers.get(i).getTraintickets().get(0).getPrice();
            }
            catch (Exception e) {
            }
        }
        return totalprice;
    }

    /**
     * 原来是返回String暂时把结果放到Changesupplytradeno里 如需要可以使用方法getChangesupplytradeno获取
     * 
     * @param trainorder
     * @param from_station
     *            出发站三字码，可不传
     * @param to_station
     *            到达站三字码，可不传
     * @return >> 格式如:[{"ticket_type":"票类型","price":票价,"zwcode":"座位编码",
     *         "passenger_id_type_code"
     *         :"证件类型","passenger_name":"乘客姓名","passenger_id_no":"证件号"}] >>
     *         ticket_type:1:成人票，2:儿童票，3:学生票，4:残军票 >> price:float类型 >>
     *         zwcode:9:商务座
     *         ，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座] >>
     *         passenger_id_type_code:1:二代身份证，C:港澳通行证，G:台湾通 行证，B:护照
     * @time 2014年12月14日 上午10:28:02
     * @author wzc
     */
    public Trainorder TCTrainOrdering(Trainorder trainorder, int r1, String from_station, String to_station) {
        //获取账号
        Customeruser user = getCustomerUser(trainorder, r1);
        //记录获取账号后的日志
        WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":s:登录模块:" + user.getLoginname() + ",pwd:" + user.getLogpassword()
                + ",状态:" + user.getState() + ",cookie:" + user.getCardnunber());
        //未获取到账号
        if (user == null || user.getState() == null || user.getState() == 0) {
            trainorder.setState12306(1);
            trainorder.setChangesupplytradeno("同步:未获取到账号");
            return trainorder;
        }
        //账号绑定乘客存在问题
        if (!user.isCanCreateOrder()) {
            trainorder.setState12306(1);
            trainorder.setChangesupplytradeno("同步:" + user.getDescription());
            return trainorder;
        }
        List<Trainpassenger> passengerlist = trainorder.getPassengers();
        String passengersstr = "";
        Trainticket ticket = null;
        if (passengerlist.size() > 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < passengerlist.size(); i++) {
                Trainpassenger p = passengerlist.get(i);
                List<Trainticket> tickets = p.getTraintickets();
                if (tickets != null && tickets.size() > 0) {
                    JSONObject obj = new JSONObject();
                    obj.put("passenger_id_no", p.getIdnumber());
                    obj.put("passenger_name", p.getName());
                    obj.put("ticket_type", tickets.get(0).getTickettype());
                    obj.put("price", tickets.get(0).getPrice());
                    obj.put("zwcode", getzwname(tickets.get(0).getSeattype()));
                    obj.put("passenger_id_type_code", getIdtype12306(p.getIdtype()));
                    //座席转换
                    String seatChange = PropertyUtil.getValue("seatChange", "Train.properties");
                    //空即转换
                    obj.put("seatChange",
                            ElongHotelInterfaceUtil.StringIsNull(seatChange) || "true".equalsIgnoreCase(seatChange));
                    array.add(obj);
                }
            }
            passengersstr = array.toJSONString();
            List<Trainticket> tickets = passengerlist.get(0).getTraintickets();
            if (tickets.size() > 0) {
                ticket = tickets.get(0);
            }
        }
        if (ticket != null && ticket.getDeparttime().length() >= 10) {
            long t1 = System.currentTimeMillis();
            String train_date = ticket.getDeparttime().substring(0, 10);
            trainorder.setSupplyaccount(user.getLoginname() == null ? "" : (user.getLoginname() + "/UNION"));
            TrainOrderReturnBean returnob = Server
                    .getInstance()
                    .getTrain12306Service()
                    .create12306Order(trainorder.getId(), train_date, from_station, to_station, ticket.getDeparture(),
                            ticket.getArrival(), ticket.getTrainno(), passengersstr, user);
            String code = returnob.getCode();
            String jsonmsg = returnob.getJson();
            String msg = returnob.getMsg();
            if (returnob.getRefundOnline() == 1) {
                trainorder.setChangesupplyaccount("1");
            }
            boolean success = returnob.getSuccess();
            WriteLog.write("t同程火车票接口_4.5申请分配座位席别",
                    r1 + ":1111:name:" + user.getLoginname() + ",pwd:" + user.getLogpassword() + "," + code + ":msg:"
                            + msg + ",是否成功:" + success + ":jsonmsg:" + jsonmsg);

            if (success) {
                saveOrderInfor(jsonmsg, trainorder, user, r1);
            }
            else if (msg.contains("还在排队中") || msg.contains("存在未完成订单")) {
                // jsonmsg = getstr(user);
                String passengers = getpassengers(trainorder);
                jsonmsg = getstr(user, trainorder, passengers);
                WriteLog.write("TCTrainOrdering_paidui", r1 + ":" + trainorder.getId() + ":进入排队:0:" + ":jsonmsg:"
                        + jsonmsg);
                if (jsonmsg.indexOf("改签待支付") < 0 && /* jsonmsg.indexOf("无未支付订单") < 0 && */jsonmsg.indexOf("用户未登录") < 0) {
                    // 290573:390070:进入排队:0::jsonmsg:无未支付订单:result:提交订单成功，程序自动排队，未获取到订单号，可能情况：1、占座失败；2：超过程序排队时间设定，还在排队中。
                    if ("".equals(jsonmsg)) {
                        jsonmsg = getstr(user, trainorder, passengers);
                        WriteLog.write("TCTrainOrdering_paidui", r1 + ":" + trainorder.getId() + ":进入排队:1:"
                                + ":jsonmsg:" + jsonmsg);
                    }
                    if ("".equals(jsonmsg)) {
                        jsonmsg = getstr(user, trainorder, passengers);
                        WriteLog.write("TCTrainOrdering_paidui", r1 + ":" + trainorder.getId() + ":进入排队:2:"
                                + ":jsonmsg:" + jsonmsg);
                    }
                    if (jsonmsg.contains("没有足够的票") || jsonmsg.contains("无未支付订单")) {
                        freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree,
                                AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                        trainorder.setChangesupplytradeno(jsonmsg);
                    }
                    else {
                        boolean isall = false;
                        for (int j = 0; j < 5; j++) {
                            isall = isall(jsonmsg, trainorder.getPassengers());
                            if (!isall) {
                                WriteLog.write("TCTrainOrdering_paidui", r1 + ":" + trainorder.getId() + ":排队:" + j
                                        + ":次:" + code + ":jsonmsg:" + jsonmsg);
                                try {
                                    Thread.sleep(100L);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                jsonmsg = getstr(user, trainorder, passengers);
                            }
                            else {
                                break;
                            }
                        }
                        if (isall) {
                            WriteLog.write("TCTrainOrdering_paidui", r1 + ":" + trainorder.getId() + ":排队成功了:"
                                    + jsonmsg);
                            saveOrderInfor(jsonmsg, trainorder, user, r1);
                        }
                        else {
                            trainorder.setChangesupplytradeno(msg);// 返回失败信息
                        }
                    }
                }
            }
            else if (msg.indexOf("已订") < 0 && msg.indexOf("不可购票") < 0 && msg.indexOf("没有足够的票") < 0
                    && msg.indexOf("排队人数现已超过余票数") < 0 && msg.indexOf("本次购票行程冲突") < 0 && msg.indexOf("已无余票") < 0
                    && msg.indexOf("未通过身份信息核验") < 0 && msg.indexOf("价格发生变化") < 0) {// 如果不是已订,等其他原因的才往下走
                trainorder.setChangesupplytradeno(msg);// 返回失败信息
                if (msg.indexOf("取消次数过多") > 0) {// 取消次数过多换账号
                    isEnableTodayCustomeruser(user);// 这个账号取消三次后今天不能使用了
                }
                else if (msg.indexOf("您的账号尚未通过身份信息核验") > 0) {
                    isEnableForeverCustomeruser(user, msg);// 您的账号尚未通过身份信息核验
                }
                /*
                 * 如果第一次下单失败了，重新下单第二次，这里不用了暂时，交给异步下单处理 user =
                 * getcustomeruser(trainorder.getPassengers(), r1);
                 * trainorder.setSupplyaccount(user.getLoginname() == null ? ""
                 * : (user.getLoginname() + "/UNION"));
                 * WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 +
                 * ":s:登录模块:cususer_new:" + user.getLoginname());
                 * TrainOrderReturnBean returnobt = Server .getInstance()
                 * .getTrain12306Service() .create12306Order(r1, train_date,
                 * from_station, to_station, ticket.getDeparture(),
                 * ticket.getArrival(), ticket.getTrainno(), passengersstr,
                 * user); String code2 = returnobt.getCode(); String jsonmsg2 =
                 * returnobt.getJson(); String msg2 = returnobt.getMsg();
                 * boolean success2 = returnobt.getSuccess();
                 * WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":222:" + code2 +
                 * ":" + msg2 + ",是否成功:" + success2 + ":msg2:" + msg2); if
                 * (success2) { saveOrderInfor(jsonmsg2, trainorder, user, r1);
                 * } else { freecustomeruser(user);
                 * trainorder.setChangesupplytradeno(msg);//返回失败信息 }
                 */
            }
            else {
                if (msg.contains("存在未完成订单")) {

                }
                else {
                    freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                            AccountSystem.NullDepartTime);
                }
                trainorder.setChangesupplytradeno(msg);// 返回失败信息
            }
            long t2 = System.currentTimeMillis();
            WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":下单用时:" + (t2 - t1) + ":msg:" + msg);
            tongbuzhuanyibusaveinfo(trainorder, r1);
        }
        else {
            WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":乘客票有问题");
        }
        return trainorder;
    }

    /**
     * 同步转异步的时候把订单状态更新一下
     * 
     * @param trainorder
     * @time 2015年1月11日 下午7:18:07
     * @author chendong
     */
    public void tongbuzhuanyibusaveinfo(Trainorder trainorder, int r1) {
        String reqtoken = trainorder.getContactuser();
        String sql_where = " WHERE C_CONTACTUSER='" + reqtoken + "' ";
        WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":sql_where:" + sql_where);
        List<Trainorder> trainorderList = Server.getInstance().getTrainService()
                .findAllTrainorderBySql(sql_where, "ORDER BY ID DESC", null);
        WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":trainorderList.size:" + trainorderList.size());
        // 如果是同步的并且已经同步转异步的话，数据库里会有订单找到以后进行保存和回调
        if (trainorderList.size() > 1) {
            trainorder.setId(trainorderList.get(1).getId());
            if (trainorder.getExtnumber() != null) {
                int C_ORDERSTATUS = trainorderList.get(1).getOrderstatus();
                WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":tongbuzhuanyibusaveinfo:id:"
                        + trainorderList.get(1).getId() + ":C_ORDERSTATUS:" + C_ORDERSTATUS);
                if (C_ORDERSTATUS == 1) {// 如果当前订单状态是等待支付
                    updateExtordercreatetime(trainorder.getId());
                    trainorder.setState12306(Trainorder.ORDEREDWAITPAY);// 下单成功等待支付
                    trainorder.setChangesupplytradeno("");
                    Server.getInstance().getTrainService().updateTrainorder(trainorder);
                    createtrainorderrc(1, "下单成功,电子单号:" + trainorder.getExtnumber(), trainorder.getId(), 0L,
                            trainorder.getOrderstatus(), "");
                    Trainorder trainorder_db = Server.getInstance().getTrainService()
                            .findTrainorder(trainorder.getId());
                    updatepassenger(trainorder_db.getPassengers(), trainorder);
                    callBackTongChengOrdered(trainorder, trainorder.getChangesupplytradeno());// 下单成功了回调
                }
            }
            else {// 下单失败了不回掉等着定时任务自动下单
                createtrainorderrc(1, "同步下单失败:(" + trainorder.getChangesupplytradeno() + "):走异步下单方法",
                        trainorder.getId(), 0L, trainorder.getOrderstatus(), "");
                String sql1 = "UPDATE T_TRAINORDER SET C_STATE12306=1 WHERE ID=" + trainorder.getId();// //如果没有的话12306状态是等待下单
                // String sql1 =
                // "UPDATE T_TRAINORDER SET C_ORDERSTATUS=1,C_STATE12306=1 WHERE ID="
                // + trainorder.getId()
                // + " AND C_STATE12306!=4";////如果没有的话变成等待支付//12306状态是等待下单
                int i1 = 0;
                try {
                    i1 = Server.getInstance().getSystemService().excuteGiftBySql(sql1);
                    // WriteLog.write("Tongchengtrainorder_tongbuzhuanyibu",
                    // trainorder.getId() + ":i1:" + i1);
                    // if (i1 > 0) {
                    // yibuchulidingdan(trainorder.getId());
                    activeMQroordering(trainorder.getId());
                    WriteLog.write("TongchengSupplyMethod_tongbufail_yibuchulidingdan", "id:" + trainorder.getId());

                    // }
                }
                catch (Exception e) {
                    logger.error("tongchengtrainorder_updateExtordercreatetime", e.fillInStackTrace());
                }
                WriteLog.write("TongchengSupplyMethod_tongbufail_yibuchulidingdan", trainorder.getId() + ":" + i1
                        + ":sql:" + sql1);
            }
        }
    }

    /**
     * 
     * 
     * @param passengers
     *            数据库的passengers
     * @param trainorder
     * @time 2015年1月12日 下午1:38:53
     * @author chendong
     */
    private void updatepassenger(List<Trainpassenger> passengers, Trainorder trainorder) {
        for (int i = 0; i < passengers.size(); i++) {
            Trainpassenger trainpassenger = passengers.get(i);
            String passengerid = trainpassenger.getPassengerid();
            for (int j = 0; j < trainorder.getPassengers().size(); j++) {
                Trainpassenger trainpassenger_new = trainorder.getPassengers().get(j);
                if (trainpassenger_new.getPassengerid().equals(passengerid)) {
                    try {
                        Trainticket ticket = trainpassenger_new.getTraintickets().get(0);
                        ticket.setId(trainpassenger.getTraintickets().get(0).getId());
                        Server.getInstance().getTrainService().updateTrainticket(ticket);
                    }
                    catch (Exception e) {
                    }
                    break;
                }
                else {
                    continue;
                }
            }
        }
    }

    /**
     * 同程回调占座结果
     * 
     * @param index
     * @param orderid
     * @param returnmsg
     *            回调具体内容 占座成功传true
     * @return
     * @time 2014年12月12日 下午2:20:30
     * @author fiend
     */
    public String callBackTongChengOrdered(Trainorder trainorder, String returnmsg) {
        String tcTrainCallBack = getSysconfigString("tcTrainCallBack");
        String result = "false";
        String url = tcTrainCallBack;
        // returnmsg = returnMsgStr(returnmsg);
        // try {
        // returnmsg = URLEncoder.encode(returnmsg, "utf-8");
        // }
        // catch (Exception e) {
        // }
        JSONObject jso = new JSONObject();
        jso.put("trainorderid", trainorder.getId());
        jso.put("method", "train_order_callback");
        jso.put("returnmsg", "");
        int i = 0;
        while (i < 5) {
            try {
                result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if ("success".equalsIgnoreCase(result)) {
                createtrainorderrc(1, "同步转异步回调成功", trainorder.getId(), 0L, trainorder.getOrderstatus(), "系统通知");
                i = 5;
            }
            else {
                try {
                    Thread.sleep(60000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                createtrainorderrc(1, "同步转异步回调" + i + "次失败", trainorder.getId(), 0L, trainorder.getOrderstatus(),
                        "系统通知");
            }
        }
        return result;
    }

    /**
     * 生成随即密码
     * 
     * @param pwd_len
     *            生成的密码的总长度
     * @param type
     *            1数字加字母2纯数字3纯字母
     * @return 密码的字符串
     */
    public static String getRandomNum(int pwd_len, int type) {
        // 35是因为数组是从0开始的，26个字母+10个 数字
        final int maxNum = 36;
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
                't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        if (type == 2) {
            str = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        }
        if (type == 3) {
            str = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
                    'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        }
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < pwd_len) {
            // 生成随机数，取绝对值，防止 生成负数，
            i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

    /**
     * 返还虚拟账号里面的钱
     * 
     * @param orderid
     *            订单ID
     * @param ordernumber
     *            订单号
     * @param orderprice
     *            订单支付金额，传负数
     * @param ywtype
     *            业务类型 3:火车票
     * @param refordernum
     *            同城关联订单号
     */
    public Payresult vmonyPayReturn(long orderid, String ordernumber, float orderprice, int ywtype, String refordernum,
            long agentid) {
        WriteLog.write("tc虚拟账户vmonyPayReturn", "订单号ID:" + orderid + ":订单号:" + ordernumber + ":订单价格:" + orderprice
                + ":业务类型:" + ywtype);
        Payresult payresult = new Payresult();
        payresult.setPaysuccess(true);
        payresult.setResultmessage("支付成功");
        try {
            Rebaterecord record = new Rebaterecord();
            record.setRebatemoney(0D - orderprice);
            record.setCustomerid(getuseridbyagentid(agentid));
            record.setRebatetype(Rebaterecord.TUIKUAN);
            record.setOrdernumber(ordernumber);
            record.setOrderid(orderid);
            record.setYewutype(ywtype);
            record.setRebate(0D);
            record.setVmenable(1);
            record.setPaymethod(10);
            if (refordernum != null) {
                record.setRefordernum(refordernum);
            }
            record.setRebateagentid(agentid);
            record.setRebatetime(getCurrentTime());
            String memo = "退款" + (0 - orderprice) + "元";
            addAgentvmoney(agentid, 0 - orderprice);
            record.setRebatememo(memo);
            record = Server.getInstance().getMemberService().createRebaterecord(record);
            payresult.setResultmessage(record.getTradeno());
            WriteLog.write("tc虚拟账户vmonyPayReturn",
                    "订单号ID:" + orderid + ":订单号:" + ordernumber + ":" + record.getTradeno());
        }
        catch (Exception e) {
            e.printStackTrace();
            payresult.setPaysuccess(false);
            payresult.setResultmessage("支付失败！");
            WriteLog.write("tc虚拟账户vmonyPayReturn", "订单号ID:" + orderid + ":订单号:" + ordernumber + "," + e.getMessage());
        }
        return payresult;
    }

    /**
     * 
     * 扣除虚拟账号里面的钱
     * 
     * @param orderid
     *            订单ID
     * @param ordernumber
     *            订单号
     * @param orderprice
     *            订单支付金额
     * @param ywtype
     *            业务类型 3:火车票
     * @param refordernum
     *            同城关联订单号
     */
    public Payresult vmonyPay(long orderid, String ordernumber, float orderprice, int ywtype, String refordernum,
            long agentid) {
        WriteLog.write("tc虚拟账户支付", "订单号ID:" + orderid + ":订单号:" + ordernumber + ":订单价格:" + orderprice + ":业务类型:"
                + ywtype);
        Payresult payresult = new Payresult();
        payresult.setPaysuccess(true);
        payresult.setResultmessage("支付成功");
        String msg = "订单";
        if (ywtype == 21) {
            msg = "发票";
            ywtype = 2;
        }
        if (orderprice == 0) {
            payresult.setPaysuccess(false);
            payresult.setResultmessage(msg + "支付失败,支付金额不能等于0！");
            WriteLog.write("tc虚拟账户支付", "订单号ID:" + orderid + ",订单号:" + ordernumber + "," + payresult.getResultmessage());
            return payresult;
        }
        // 代理虚拟账户余额
        Double agentvmoney = getTotalVmoney(agentid);
        // 短信提醒
        try {
            String moneybig = getSystemConfig("tcmoneybig");// 大金额段
            String moneysmall = getSystemConfig("tcmoneysmall");// 小金额提醒
            boolean flag = false;
            if (agentvmoney >= Float.valueOf(moneybig)
                    && (agentvmoney - orderprice) <= Float.valueOf(moneybig).floatValue()) {
                flag = true;
                String smstemple = getSystemConfig("tcsmstemple");
                String tetctelphonenum = getSystemConfig("tctelphonenum");// 通知手机号
                                                                          // 同城设置提醒金额，及字符串
                String content = smstemple.replace("[金额]", moneybig);
                Dnsmaintenance dns = Server.getInstance().getSystemService().findDnsmaintenance(1);
                WriteLog.write("tc资金提醒短信", content);
                Server.getInstance().getAtomService()
                        .sendSms(new String[] { "" + tetctelphonenum + "" }, content, orderid, agentid, dns, 0);
            }
            if (!flag) {
                if (agentvmoney >= Float.valueOf(moneysmall)
                        && (agentvmoney - orderprice) <= Float.valueOf(moneysmall).floatValue()) {
                    String smstemple = getSystemConfig("tcsmstemple");
                    String tetctelphonenum = getSystemConfig("tctelphonenum");// 通知手机号
                                                                              // 同城设置提醒金额，及字符串
                    String content = smstemple.replace("[金额]", moneysmall);
                    Dnsmaintenance dns = Server.getInstance().getSystemService().findDnsmaintenance(1);
                    WriteLog.write("tc资金提醒短信", content);
                    Server.getInstance().getAtomService()
                            .sendSms(new String[] { "" + tetctelphonenum + "" }, content, orderid, agentid, dns, 0);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // 虚拟账户余额不足
        if (agentvmoney < orderprice) {
            payresult.setPaysuccess(false);
            payresult.setResultmessage(msg + "支付失败,您的账户余额不足于当前" + msg + "支付！");
            WriteLog.write("tc虚拟账户支付", "订单号ID:" + orderid + ":订单号:" + ordernumber + ":" + payresult.getResultmessage());
            return payresult;
        }
        else {
            try {
                Rebaterecord record = new Rebaterecord();
                record.setRebatemoney(0D - orderprice);
                record.setCustomerid(getuseridbyagentid(agentid));
                record.setRebatetype(Rebaterecord.PINGTAIXIAOFEI);
                record.setOrdernumber(ordernumber);
                record.setOrderid(orderid);
                record.setYewutype(ywtype);
                record.setRebate(0D);
                record.setVmenable(1);
                record.setPaymethod(10);
                if (refordernum != null) {
                    record.setRefordernum(refordernum);
                }
                // record.setRebateagentjibie(getrebatagent.getAgentjibie());
                record.setRebateagentid(agentid);
                record.setRebatetime(getCurrentTime());
                String memo = msg + "支付扣除" + orderprice + "元";
                addAgentvmoney(agentid, 0 - orderprice);
                record.setRebatememo(memo);
                // record.setVmbalance(vmoney - orderprice);
                record = Server.getInstance().getMemberService().createRebaterecord(record);
                payresult.setResultmessage(record.getTradeno());
                WriteLog.write("tc虚拟账户支付", "订单号ID:" + orderid + ":订单号:" + ordernumber + ":" + record.getTradeno());
            }
            catch (Exception e) {
                e.printStackTrace();
                payresult.setPaysuccess(false);
                payresult.setResultmessage(msg + "支付失败！");
                WriteLog.write("tc虚拟账户支付", "订单号ID:" + orderid + ":订单号:" + ordernumber + "," + e.getMessage());
            }
        }
        return payresult;
    }

    /**
     * 根据agentid获取余额
     */
    @SuppressWarnings("rawtypes")
    public static Double getTotalVmoney(long agentid) {
        String sql = "SELECT C_VMONEY AS VMONEY FROM T_CUSTOMERAGENT WHERE ID=" + agentid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map m = (Map) list.get(0);
            Double vmoney = Double.parseDouble(m.get("VMONEY").toString());
            return vmoney;
        }
        return 0D;
    }

    /**
     * 操作虚拟账户钱
     */
    @SuppressWarnings("rawtypes")
    public static float addAgentvmoney(long agentid, float money) {
        String sql = "UPDATE T_CUSTOMERAGENT SET C_VMONEY=C_VMONEY+" + money + " WHERE ID=" + agentid + ";"
                + "SELECT C_VMONEY FROM T_CUSTOMERAGENT WHERE ID=" + agentid;
        WriteLog.write("tc支付sql", sql);
        Map map = (Map) Server.getInstance().getSystemService().findMapResultBySql(sql, null).get(0);
        WriteLog.write("tc支付sql", "成功:" + money);
        return Float.valueOf(map.get("C_VMONEY").toString());
    }

    @SuppressWarnings("rawtypes")
    public static Long getuseridbyagentid(Long agentid) {
        String sql = " select id from T_CUSTOMERUSER where C_AGENTID=" + agentid + " and C_ISADMIN=1";
        Map map = (Map) Server.getInstance().getSystemService().findMapResultBySql(sql, null).get(0);
        return Long.valueOf(map.get("id").toString());

    }

    /**
     * 获取当前时间
     */
    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 根据订单id获取 一些信息
     * 
     * @param trainorderid
     * @return
     * @time 2015年1月22日 下午1:05:36
     * @author chendong
     */
    @SuppressWarnings("rawtypes")
    public Map getTrainorderstatus(Long trainorderid) {
        Map map = new HashMap();
        String sql = "SELECT C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,"
                + "C_TOTALPRICE,C_STATE12306,C_EXTNUMBER,C_SUPPLYACCOUNT,ordertype from T_TRAINORDER with(nolock) where ID="
                + trainorderid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 根据查到的map信息获取value
     * 
     * @param key
     * @time 2015年1月22日 下午1:08:54
     * @author chendong
     */
    @SuppressWarnings("rawtypes")
    public String gettrainorderinfodatabyMapkey(Map map, String key) {
        String value = "";
        if (map.get(key) != null) {
            try {
                value = map.get(key).toString();
            }
            catch (Exception e) {
            }
        }
        return value;
    }

    /**
     * 获取实时下单地址
     * 
     * @return
     * @time 2015年1月20日 下午10:08:24
     * @author fiend
     */
    public String getSubmitUrl() {
        return getSysconfigString("submittrainorder");
    }

    /**
     * 修改12306下单成功时间
     * 
     * @param trainorderid
     * @time 2015年1月19日 下午2:52:46
     * @author chendong
     */
    private void updateExtordercreatetime(Long trainorderid) {
        String sql1 = "UPDATE T_TRAINORDER SET C_EXTORDERCREATETIME='" + new Timestamp(System.currentTimeMillis())
                + "' WHERE ID=" + trainorderid;
        try {
            Server.getInstance().getSystemService().excuteGiftBySql(sql1);
        }
        catch (Exception e) {
            logger.error("tongchengtrainorder_updateExtordercreatetime", e.fillInStackTrace());
        }
    }

    /**
     * 异步或者同步转异步需要的操作
     * 
     * @param id
     * @time 2015年2月10日 上午10:16:38
     * @author chendong
     */
    public void yibuchulidingdan(boolean resulr,Trainorder trainorder) {

        // gotoordering(id);//实时下单线程
    	if (resulr) {
    		AcquiringMethod.acquiring(trainorder);
		}else{
			 activeMQroordering(trainorder.getId());// 扔进队列等待下单去
		}
    }

    /**
     * 调用下单方法,生成线程(给俺去下单)
     * 
     * @return
     * @time 2015年1月21日 上午10:05:25
     * @author fiend
     */
    public void gotoordering(long id) {
        String url = getSubmitUrl() + id;
        try {
            SendPostandGet.submitGet(url);
            WriteLog.write("实时下单", "成功:ID:" + id + ":" + url);
        }
        catch (Exception e) {
            WriteLog.write("实时下单", "失败:ID:" + id + ":" + url);
        }
    }

    /**
     * 把异步的订单扔进队列里 等待下单
     * @param id
     * @time 2015年2月10日 上午10:12:52
     * @author chendong
     */
    public void activeMQroordering(long id) {
    	try {
    		SendMQmsgUtil.sendGetUrlMQmsg(id+""); //发送MQ消息
		} catch (JMSException e) {
			// TODO Auto-generated catch block 
			WriteLog.write("TongchengSupplyMethod_MQroordering", e.getMessage());
		}
    }
    
    public void newactiveMQroordering(boolean result,Trainorder trainorder) {
    	if (result) {
    		AcquiringMethod.acquiring(trainorder);
		}else {
			try {
	    		SendMQmsgUtil.sendGetUrlMQmsg(trainorder.getId()+""); //发送MQ消息
			} catch (JMSException e) {
				// TODO Auto-generated catch block 
				WriteLog.write("TongchengSupplyMethod_MQroordering", e.getMessage());
			}
		}
    }
    
    

    /**
     * 异步改签扔进队列里处理
     * @param changeId 改签ID
     * @param type 类型，1：请求改签(占座)；2:确认改签(支付)；3：改签支付审核
     */
    public void activeMQChangeOrder(long changeId, int type) {
        if (changeId > 0 && (type == 1 || type == 2 || type == 3)) {
            String typeMsg = "";
            String QUEUE_NAME = "";
            String MQ_URL = PropertyUtil.getValue("activeMQ_url", "Train.properties");
            //占座
            if (type == 1) {
                typeMsg = "占座";
                QUEUE_NAME = PropertyUtil.getValue("QueueMQ_TrainChange_WaitOrder_ChangeId", "Train.properties");
            }
            //支付
            else if (type == 2) {
                typeMsg = "确认";
                QUEUE_NAME = PropertyUtil.getValue("QueueMQ_TrainChange_ConfirmOrder_ChangeId", "Train.properties");
            }
            //支付审核
            else if (type == 3) {
                typeMsg = "审核";
                QUEUE_NAME = PropertyUtil.getValue("QueueMQ_TrainChange_PayExamine_ChangeId", "Train.properties");
            }
            try {
                ActiveMQUtil.sendMessage(MQ_URL, QUEUE_NAME, String.valueOf(changeId));
            }
            catch (Exception e) {
                WriteLog.write(QUEUE_NAME, e.getMessage() + "---" + ElongHotelInterfaceUtil.errormsg(e));
            }
            finally {
                System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + "-->改签" + typeMsg + "-->" + changeId);
            }
        }
    }

    /**
     * 获取回调地址
     * @param agentId 代理ID
     * @param callBackType 回调类型 1:请求改签; 2:确认改签
     * @return 回调地址
     */
    @SuppressWarnings("rawtypes")
    public String getTrainCallBackUrl(Long agentId, int callBackType) {
        String url = "";
        if (agentId != null && agentId > 0) {
            String columnName = "";
            //请求改签
            if (callBackType == 1) {
                columnName = "C_REQUESTCHANGECALLBACKURL";
            }
            //确认改签
            else if (callBackType == 2) {
                columnName = "C_CONFIRMCHANGECALLBACKURL";
            }
            if (!ElongHotelInterfaceUtil.StringIsNull(columnName)) {
                try {
                    String sql = "SELECT " + columnName + " FROM T_INTERFACEACCOUNT WITH(NOLOCK) "
                            + "WHERE C_AGENTID = " + agentId;
                    List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    if (list != null && list.size() == 1) {
                        Map map = (Map) list.get(0);
                        url = map.get(columnName) == null ? "" : map.get(columnName).toString();
                    }
                }
                catch (Exception e) {
                }
            }
        }
        return url;
    }

    /**
     * 判断是不是同程类型的接口订单
     * 
     * @return
     * @time 2015年3月10日 下午7:07:05
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    public boolean isOtherJiekou(long trainorderid) {
        try {
            int r1 = (int) (Math.random() * 10000);
            String sql = "SELECT ID FROM T_CALLBACK_TRAIN WHERE C_AGENTID=(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER WHERE ID="
                    + trainorderid + ")";
            WriteLog.write("接口用户判断", r1 + "--->" + sql);
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            WriteLog.write("接口用户判断", r1 + "--->" + list.size());
            if (list.size() > 0) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取MQ的地址 type 代表类型
     * 
     * @return
     * @time 2015年3月30日 下午5:30:38
     * @author chendong
     */
    protected String getActiveMQUrl(int type) {
        String url = getActiveMQUrl();
        return url;
    }

    /**
     * 获取MQ的地址
     * 
     * @return
     * @time 2015年3月30日 下午5:30:38
     * @author chendong
     */
    protected String getActiveMQUrl() {
        String url = PropertyUtil.getValue("PayMq_url", "Train.properties");
        return url;
    }

    /**
     * 获取静态ID
     * 
     * @return
     */
    public long getTaobaoAgetid() {
        Long taobaoAgentid = 0L;
        try {
            String taobaoAgentid_str = PropertyUtil.getValue("TaobaoAgentID", "Train.properties");
            taobaoAgentid = Long.valueOf(taobaoAgentid_str);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return taobaoAgentid;
    }

    /**
     * 关闭账号(账号未登录)
     * @time 2015年4月15日 下午1:26:21
     * @author fiend
     */
    public void closeCus(long custid) {
        String sql = "update T_CUSTOMERUSER set C_STATE=0 where ID=" + custid;
        try {
            WriteLog.write("TongchengSupplyMethod_closeCus", "关闭账号:" + sql);
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            WriteLog.write("TongchengSupplyMethod_closeCus_error", "关闭账号异常:" + sql);
        }
    }

    /**
     * 火车票下单系统是否正常
     * @param random_no
     * @param interfacetype
     * @return
     * @time 2015年4月16日 下午3:49:47
     * @author fiend
     */
    public String isTrainorderSystemError(int random_no, int interfacetype) {
        WriteLog.write("TongchengSupplyMethod_isTrainorderSystemError", random_no + ":当前接口类型:" + interfacetype);
        try {
            if (TrainInterfaceMethod.TONGCHENG == interfacetype) {
                String isError = getSystemConfig("isTrainorderSystemError");
                WriteLog.write("TongchengSupplyMethod_isTrainorderSystemError", random_no + ":isTrainorderSystemError:"
                        + isError);
                if (!"-1".equals(isError)) {
                    return isError;
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("TongchengSupplyMethod_isTrainorderSystemError", random_no + ":异常");
        }
        WriteLog.write("TongchengSupplyMethod_isTrainorderSystemError", random_no + ":系统正常");
        return "SUCCESS";
    }

    /**
     * 淘宝票类型转换为DB票类型
     * 0->1,1->2,2->3，
     * @param taobaoticketype
     * @return
     * @time 2015年5月5日 下午3:40:23
     * @author fiend
     */
    public int changeTicketTypeTaobao2DB(String taobaoticketype) {
        if ("1".equals(taobaoticketype)) {
            return 2;
        }
        if ("2".equals(taobaoticketype)) {
            return 3;
        }
        return 1;
    }

    /**
     * 订单确认出票时间添加
     */
    public void updateConfirmTime(long orderid) {
        try {
            String sql = "UPDATE T_TRAINORDER SET C_CONFIRMTIME='" + TimeUtil.gettodaydate(4) + "' WHERE ID=" + orderid;
            Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
        }
    }

    /**
     * 获取乘客MAP
     * @param TrainPassengerList
     * @return
     * @author fiend
     */
    public Map<String, String> passengerMap(List<Trainpassenger> TrainPassengerList, boolean isPhone) {
        boolean isFalseLy = false;
        boolean isFalsely_no = false;
        try {
            Map<String, String> passengerMap = new HashMap<String, String>();
            JSONArray passengersjsonArray = new JSONArray();
            for (int i = 0; i < TrainPassengerList.size(); i++) {
                Trainpassenger trainpassenger = TrainPassengerList.get(i);
                JSONObject passengerJsonObject = new JSONObject();
                passengerJsonObject.put("name", trainpassenger.getName());
                passengerJsonObject.put("idnumber", trainpassenger.getIdnumber());
                passengerJsonObject.put("idtype", trainpassenger.getIdtype());
                try {
                    //                    String key = "falsely_" + trainpassenger.getIdnumber();
                    long idNumber = Long.parseLong(trainpassenger.getIdnumber().toUpperCase().replace("X", "10"));
                    //                    String value = OcsMethod.getInstance().get(key);
                    DataTable result = null;
                    try {
                        String sql = "SELECT * FROM FalselyPassenger WITH(NOLOCK) WHERE IdNumber=" + idNumber;
                        result = DBHelperAccount.GetDataTable(sql);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    List<DataRow> dataRows = result.GetRow();
                    if (dataRows.size() > 0) {
                        DataRow dataRow = dataRows.get(0);
                        int flag = dataRow.GetColumnInt("flag");
                        if (flag == 2) {
                            isFalsely_no = true;
                            isFalseLy = true;
                        }
                        else if (flag == 1) {
                            isFalseLy = true;
                        }
                    }
                    if (isFalseLy) {
                        passengerJsonObject.put("falsely", 1);
                        try {
                            if (trainpassenger.getCusOutTime() > System.currentTimeMillis()) {
                                passengerJsonObject.put("timeout", trainpassenger.getCusOutTime());
                            }
                        }
                        catch (Exception e1) {
                        }
                    }
                    else {
                        passengerJsonObject.put("falsely", 0);
                    }
                }
                catch (Exception e1) {
                    passengerJsonObject.put("falsely", 0);
                }
                try {
                    passengerJsonObject.put("tickettype", trainpassenger.getTrainstudentinfos().size() > 0 ? 3 : 1);
                }
                catch (Exception e) {
                    passengerJsonObject.put("tickettype", 1);
                }
                passengersjsonArray.add(passengerJsonObject);
            }
            passengerMap.put("passengers", passengersjsonArray.toJSONString());
            passengerMap.put("isFalseLy", isFalseLy + "");
            passengerMap.put("isFalsely_no", isFalsely_no + "");
            if (isPhone) {
                passengerMap.put("AccountVirtualCookie", "true");
            }
            WriteLog.write("获取账号_乘客信息", passengerMap.toString());
            return passengerMap;
        }
        catch (Exception e) {
            return AccountSystem.NullMap;
        }
    }

    /**
     * list 去重 排序
     * 
     * @param list
     * @return
     * @time 2015年9月23日 下午12:39:26
     * @author fiend
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Long> disSortList(List<Long> list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        Collections.sort(list);
        return list;
    }

    /**
     * 添加乘客反向挡板拦截
     * @param TrainPassengerMap
     * @return
     * @time 2015年9月23日 下午12:21:36
     * @author fiend
     */
    public boolean falsePassenger(Map<String, String> passengerMap) {
        try {
            List<Long> idnolist = new ArrayList<Long>();
            JSONArray jsonArray = JSONArray.parseArray(passengerMap.get("passengers"));
            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.getJSONObject(i).getInteger("falsely") == 1) {
                    idnolist.add(Long.valueOf(jsonArray.getJSONObject(i).getString("idnumber").toUpperCase()
                            .replace("X", "10")));
                }
            }
            if (idnolist.size() > 0) {
                idnolist = disSortList(idnolist);
                String ocskeyno = "falsely_no_";
                for (Long idno : idnolist) {
                    ocskeyno += idno + "_";
                }
                ocskeyno = ocskeyno.substring(0, ocskeyno.length() - 1);
                String value = OcsMethod.getInstance().get(ocskeyno);
                WriteLog.write("冒用反向挡板_falsely_no_result", ocskeyno + "--->" + value);
                if (value != null && !"".equals(value)) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_falsePassenger", e);
        }
        return false;
    }

    //获取用户密码
    public String getCookie(JSONObject jsonObject) {
        String cookie = jsonObject.getString("cookie");
        return cookie;
    }

    //获取用户密码
    public String getUserPassword(JSONObject jsonObject) {
        String userpassword = jsonObject.getString("LoginUserPassword");
        if (userpassword == null) {
            userpassword = jsonObject.getString("userpassword");
        }
        return userpassword;
    }

    //获取用户名
    public String getUsername(JSONObject jsonObject) {
        String username = jsonObject.getString("LoginUserName");
        if (username == null) {
            username = jsonObject.getString("username");
        }
        return username;
    }

    /**
     * 如果需要存用户名密码或者cookie调用这个方法
     * 火车票接口
     * @param id 火车票订单id
     * @param UserName 客户12306用户名 
     * @param PassWord 客户12306密码 
     * @param partnerid  接口用户名
     * @param cookie cookie 【可为空 和12306用户名必须有一个不为空】
     * @time 2015年10月24日 下午4:55:43
     * @author chendong
     */
    public void diaoyongTrainAccountSrcUtildeinsertData(long id, String UserName, String PassWord, String partnerid,
            String cookie) {
        WriteLog.write("TrainAccountSrcUtil_insertData", UserName + ":" + PassWord + ":" + partnerid + ":" + id + ":"
                + cookie);
        if ((UserName != null && PassWord != null) || cookie != null) {
            TrainAccountSrcUtil.insertData(UserName, PassWord, partnerid, id, cookie);
        }
    }

    /**
     * Cookie无效时，调用接口刷新Cookie
     * @author WH
     */
    @SuppressWarnings("rawtypes")
    private void refreshCookieFromInterface(Trainorder order) {
        try {
            if (ElongHotelInterfaceUtil.StringIsNull(order.getQunarOrdernumber()) || order.getAgentid() == 0) {
                String sql = "select C_QUNARORDERNUMBER, C_AGENTID, ISNULL(C_INTERFACETYPE, 0) C_INTERFACETYPE "
                        + "from T_TRAINORDER with(nolock) where ID = " + order.getId();
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                //查询成功
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    order.setAgentid(Long.valueOf(map.get("C_AGENTID").toString()));
                    order.setQunarOrdernumber(map.get("C_QUNARORDERNUMBER").toString());
                    order.setInterfacetype(Integer.valueOf(map.get("C_INTERFACETYPE").toString()));
                }
            }
            int interfaceType = order.getInterfacetype() == null ? 0 : order.getInterfacetype();
            interfaceType = interfaceType > 0 ? interfaceType : getOrderAttribution(order);
            //淘宝
            if (interfaceType == TrainInterfaceMethod.TAOBAO) {
                //参数
                JSONObject param = new JSONObject();
                param.put("orderid", order.getId());
                param.put("agentid", order.getAgentid());
                param.put("interfaceOrderNumber", order.getQunarOrdernumber());
                //地址
                String url = PropertyUtil.getValue("fresh_cookie_taobao_url", "Train.properties");
                //请求
                SendPostandGet.submitGet(url + "?jsonStr=" + param, "UTF-8");
            }
            //其他
            else {
                //参数
                JSONObject json = new JSONObject();
                json.put("orderid", order.getId());
                json.put("agentid", order.getAgentid());
                json.put("interfaceType", interfaceType);
                json.put("interfaceOrderNumber", order.getQunarOrdernumber());
                /*
                //地址
                String url = PropertyUtil.getValue("TongChengCallBackServletURL", "Train.properties");
                //请求
                SendPostandGet.submitPost(url, json.toString(), "UTF-8");
                */
                String logName = "TrainRefreshAccountCookieServlet";
                int random = new Random().nextInt(9000000) + 1000000;
                new TrainRefreshAccountCookieMethod().refreshCookie(logName, json, random);
            }
        }
        catch (Exception exception) {
            ExceptionUtil.writelogByException("RefreshCookieFromInterface_Error", exception);
        }
    }

    /**
     * 将淘宝的有问题会导致出票失败的常旅相关信息入库
     * 
     * @param trainpassenger
     * @param message12306
     * @time 2015年11月11日 下午5:22:11
     * @author Administrator
     */
    private void setSubOrderIdMethod(Trainpassenger trainpassenger, String message12306) {
        String sql = "INSERT INTO [TaobaoAddSubOrderId]([orderId],[SubOrderId],[PassengerName],[Msg12306]) VALUES ("
                + trainpassenger.getOrderid() + ", '" + trainpassenger.getTraintickets().get(0).getInterfaceticketno()
                + "', '" + trainpassenger.getName() + "', '" + message12306 + "')";
        WriteLog.write("TongchengSupplyMethod_setSubOrderIdMethod", "sql:" + sql);
        int count = 0;
        try {
            count = Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("Err_TongchengSupplyMethod_setSubOrderIdMethod", e,
                    trainpassenger.getOrderid() + "");
        }
        WriteLog.write("TongchengSupplyMethod_setSubOrderIdMethod", "count:" + count + ":sql:" + sql);
    }

    /**
     * 同步给同程的身份冒用的数据
     * 
     * @param trainpassenger
     * @param jsonArray
     * @param message12306
     * @time 2015年12月10日 下午4:43:13
     * @author chendong
     */
    private void setUnmatchedpasslist(Trainpassenger trainpassenger, JSONArray jsonArray, String message12306) {
        if (trainpassenger.getOrderid() <= 0 || jsonArray == null) {
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json12306Account = jsonArray.getJSONObject(i);
            int accountId = json12306Account.getIntValue("accountId");
            String msg = json12306Account.getString("msg");
            try {
                String sql = "INSERT INTO [TongchengUnmatchedPassenger]([OrderId],[PassengerName],[IdCard],[Flag],[Msg],[CreateTime],[Account12306Id],[AccountMsg])VALUES("
                        + trainpassenger.getOrderid()
                        + ",'"
                        + trainpassenger.getName()
                        + "','"
                        + trainpassenger.getIdnumber()
                        + "',"
                        + 1
                        + ",'"
                        + message12306
                        + "','"
                        + new Timestamp(System.currentTimeMillis()) + "'," + accountId + ",'" + msg + "')";
                WriteLog.write("返回同程未匹配有效帐号的乘客", "sql:" + sql);
                int count = Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
                WriteLog.write("返回同程未匹配有效帐号的乘客", ":count:" + count);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("返回同程未匹配有效帐号的乘客_TongchengSupplyMethod_err", e);
            }
        }
    }

    /**
     * 将不可用的帐号同步推送给同程 OR 将315的乘客同步推送给同程
     * 
     * @param user 
     * @param trainpassenger
     * @time 2015年11月17日 下午5:49:28
     * @author w.c.l
     */
    public void checkAccountMethod(Customeruser user, Trainpassenger trainpassenger, int type) {

        Customeruser temp = new Customeruser();
        temp.setId(user.getId());
        temp.setIsenable(user.getIsenable());
        if (type == 315) {
            temp.setIsenable(1);
            temp.setLogpassword("315");
        }
        else {
            temp.setLogpassword("100");
        }

        List<Trainpassenger> trainpassengers = new ArrayList<Trainpassenger>();
        trainpassengers.add(trainpassenger);
        WriteLog.write("TongchengSupplyMethod_checkAccountMethod", "user:" + temp.getId() + "type:" + type);
        CallBackPassengerUtil.callBackTongcheng(temp, trainpassengers, 2);
    }

    /**
     * 没有任何帐号
     * 
     * @param customeruser
     * @param msg
     * @return
     * @time 2015年11月24日 上午11:46:55
     * @author fiend
     */
    private JSONArray accountJsonObjectNull() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonacc = new JSONObject();
        jsonacc.put("accountId", 0);
        jsonacc.put("msg", "其他");
        jsonArray.add(jsonacc);
        return jsonArray;
    }

    /**
     * 虚拟王宏返回
     * 
     * @param customeruser
     * @param msg
     * @return
     * @time 2015年11月24日 上午11:46:55
     * @author fiend
     */
    private JSONArray accountJsonObject(Customeruser customeruser, String msg) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonacc = new JSONObject();
        jsonacc.put("accountId", customeruser.getId());
        jsonacc.put("msg", msg);
        jsonArray.add(jsonacc);
        return jsonArray;
    }

    /**
     * 查询是否需要下单过程中绑定乘客
     * 
     * @param orderid
     * @return
     * @time 2015年12月4日 下午4:35:37
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    public boolean isBindingPassengers(long orderid) {
        boolean result = false;
        try {
            if (orderid > 0) {
                List list = Server.getInstance().getSystemService()
                        .findMapResultByProcedure(" [sp_TrainOrderBinding_Select] @OrderId=" + orderid);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    result = "1".equals(map.get("Type").toString());
                }
            }
        }
        catch (Exception e) {
        }
        return result;
    }

    /**
     * 请求REP账号手机端数据
     */
    public String JoinCommonAccountPhone(Customeruser user) {
        return "&accountPhone=" + CommonAccountPhone(user);
    }

    /**
     * 请求REP账号手机端数据
     */
    public String CommonAccountPhone(Customeruser user) {
        try {
            JSONObject obj = new JSONObject();
            //账号相关
            obj.put("account", user.getLoginname());
            obj.put("password", user.getLogpassword());
            obj.put("Cookie", user.getCardnunber());
            obj.put("WL-Instance-Id", user.getSessionid());
            obj.put("__wl_deviceCtxSession", user.getWldevicectxsession());
            obj.put("baseDTO.device_no", user.getDeviceno());
            obj.put("startTime", System.currentTimeMillis());
            return URLEncoder.encode(obj.toString(), "UTF-8");
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("PHONE_ERROR_请求REP账号手机端数据", e);
            return "";
        }
    }

    /**
     * 用完手机端后刷新手机端DB配置和user
     * 
     * @param accoutPhone   调用REP前的手机端配置
     * @param resultString  REP返回结果
     * @param user 传进来的时候注意，方法调用完毕，这里面数据我会修改
     * @time 2016年3月6日 上午11:40:15
     * @author fiend
     */
    public void freshPhone(String accoutPhone, String resultString, Customeruser user) {
        try {
            WriteLog.write("PHONE_添加常旅客后更新DB", resultString + "--->" + accoutPhone + "--->" + user.getId());
            JSONObject jsonObject = JSONObject.parseObject(resultString);
            if (jsonObject.containsKey("accountPhone")) {
                JSONObject newAccountJsonObject = jsonObject.getJSONObject("accountPhone");
                JSONObject accoutJsonObject = JSONObject.parseObject(URLDecoder.decode(
                        accoutPhone.replace("&accountPhone=", ""), "utf-8"));
                if (newAccountJsonObject.containsKey("Cookie")
                        && newAccountJsonObject.containsKey("WL-Instance-Id")
                        && newAccountJsonObject.containsKey("__wl_deviceCtxSession")
                        && newAccountJsonObject.containsKey("baseDTO.device_no")
                        && (!accoutJsonObject.containsKey("Cookie")
                                || !accoutJsonObject.getString("Cookie").equals(
                                        newAccountJsonObject.getString("Cookie"))
                                || !accoutJsonObject.containsKey("WL-Instance-Id")
                                || !accoutJsonObject.getString("WL-Instance-Id").equals(
                                        newAccountJsonObject.getString("WL-Instance-Id"))
                                || !accoutJsonObject.containsKey("__wl_deviceCtxSession")
                                || !accoutJsonObject.getString("__wl_deviceCtxSession").equals(
                                        newAccountJsonObject.getString("__wl_deviceCtxSession"))
                                || !accoutJsonObject.containsKey("baseDTO.device_no") || !accoutJsonObject.getString(
                                "baseDTO.device_no").equals(newAccountJsonObject.getString("baseDTO.device_no")))) {
                    user.setCardnunber(newAccountJsonObject.getString("Cookie"));
                    user.setSessionid(newAccountJsonObject.getString("WL-Instance-Id"));
                    user.setWldevicectxsession(newAccountJsonObject.getString("__wl_deviceCtxSession"));
                    user.setDeviceno(newAccountJsonObject.getString("baseDTO.device_no"));
                    String updateStr = "exec [sp_Customeruser_Phone_Update] @Id=" + user.getId() + " ,@cookie='"
                            + newAccountJsonObject.getString("Cookie") + "' ,@sessionId='"
                            + newAccountJsonObject.getString("WL-Instance-Id") + "' ,@__wl_deviceCtxSession='"
                            + newAccountJsonObject.getString("__wl_deviceCtxSession") + "' ,@device_no='"
                            + newAccountJsonObject.getString("baseDTO.device_no") + "'";
                    int updateSum = DBHelperAccount.UpdateData(updateStr);
                    WriteLog.write("PHONE_添加常旅客后更新DB", updateStr + "--->" + updateSum);
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("PHONE_ERROR_添加常旅客后更新DB", e);
        }
    }

    /**
     * DB控制是否走手机端下单
     * 
     * @param orderid
     * @return
     * @time 2016年3月10日 下午2:06:27
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    public boolean isPhoneOrder(long orderid) {
        try {
            List list = Server.getInstance().getSystemService()
                    .findMapResultByProcedure(" [Select_TrainOrderIsPhone] @OrderId=" + orderid);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                if ("1".equals(map.get("IsPhone").toString())) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("PHONE_ERROR_isPhoneOrder", e);
        }
        return false;
    }

    /**
     * DB插入是否走手机端下单
     * 
     * @param orderid
     * @return
     * @time 2016年3月10日 下午2:06:27
     * @author fiend
     */
    public void setPhoneOrder(long orderid, long agentId, int orderType) {
        try {
            Server.getInstance()
                    .getSystemService()
                    .findMapResultByProcedure(
                            " [INSTER_TrainOrderIsPhone] @OrderId =" + orderid + " ,@AgentId=" + agentId
                                    + " ,@orderType=" + agentId);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("PHONE_ERROR_setPhoneOrder", e);
        }
    }

    /**
     * DB修改是否走手机端下单
     * 
     * @param orderid
     * @return
     * @time 2016年3月16日 上午10:55:39
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    public boolean changePhoneOrder(long orderid) {
        try {
            List list = Server.getInstance().getSystemService()
                    .findMapResultByProcedure(" [CHANGE_TrainOrderIsPhone] @OrderId =" + orderid);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                if ("1".equals(map.get("IsPhone").toString())) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("PHONE_ERROR_changePhoneOrder", e);
        }
        return false;
    }
    
    
   
    
}
