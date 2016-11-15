package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TrainSelectLoginWay extends TongchengSupplyMethod {
    public static void main(String[] args) {
        JSONObject jsonObject = JSONObject
                .parseObject("{\"callbackurl\":\"https://jt.rsscc.com/trainwap/platform/tasks/buyCallbackHthy.action\",\"reqtime\":\"20160115190425\",\"LoginUserName\":\"\",\"from_station_name\":\"温州\",\"checi\":\"K944\",\"from_station_code\":\"\",\"to_station_name\":\"凯里\",\"sign\":\"e66bfd90f75c8de59ae27c270b8ebd98\",\"partnerid\":\"gaotie_train\",\"passengers\":[{\"piaotype\":\"1\",\"reason\":\"\",\"passporttypeseidname\":\"二代身份证\",\"passporttypeseid\":\"1\",\"zwname\":\"硬座\",\"price\":201,\"piaotypename\":\"成人票\",\"ticket_no\":\"\",\"passengersename\":\"彭庭兴\",\"zwcode\":\"1\",\"passportseno\":\"522725199906266834\",\"passengerid\":\"528206748422406144\",\"cxin\":\"\"}],\"to_station_code\":\"\",\"train_date\":\"2016-01-16\",\"is_ accept _ standing\":true,\"extSeat\":\"\",\"method\":\"train_order\",\"orderid\":\"DG1011519040407102\",\"LoginUserPassword\":\"\"}");
        System.out.println(jsonObject.getBooleanValue("is_ accept _ standing"));

    }

    public final static int PAYTIME_DEAD = 25;

    Map<String, InterfaceAccount> interfaceAccountMap;

    /**
     * 保存第三方账号信息
     * @param orderId 订单ID
     * @param json 接口传过来的json
     * @time 2015年10月22日 下午9:55:22
     * @author 王成亮
     */
    public void saveThirdAccountInfo(long orderId, JSONObject json) {
        //cookie
        String cookie = json.getString("cookie");
        //账号名称
        String accountName = getUsername(json);
        //账号密码
        String accontPassword = getUserPassword(json);
        //partnerid
        String partnerid = json.getString("partnerid");
        //逻辑判断
        if (!ElongHotelInterfaceUtil.StringIsNull(cookie)
                || (!ElongHotelInterfaceUtil.StringIsNull(accountName) && !ElongHotelInterfaceUtil
                        .StringIsNull(accontPassword))) {
            addTrainAccountInfo(accountName, accontPassword, partnerid, orderId, cookie);
        }
    }

    /**
     * 将登录用的帐号名密码或Cookie存入数据库
     * 
     * @param username12306  帐号名
     * @param userpassword12306 密码
     * @param partnerid 代理商ID
     * @param trainorderid 订单号ID
     * @param cookie12306 
     * @time 2015年10月23日 下午7:26:00
     * @author Administrator
     */
    private void addTrainAccountInfo(String username12306, String userpassword12306, String partnerid,
            long trainorderid, String cookie12306) {
        TrainAccountSrcUtil.insertData(username12306, userpassword12306, partnerid, trainorderid, cookie12306);
    }

    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    public InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        WriteLog.write("艺龙支付通知_ElongPayMessageServlet_payMsgDisposeMethod", "loginname:" + loginname);
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
        }
        WriteLog.write("艺龙支付通知_ElongPayMessageServlet_payMsgDisposeMethod", "list_interfaceAccount:"
                + list_interfaceAccount.size());
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        return interfaceAccount;
    }

    /**
     * 
     * 
     * @param json
     * @return
     * @time 2015年12月10日 上午11:47:17
     * @author Mr.Wang
     */
    public String getSignMethod(String sign) {
        if (!ElongHotelInterfaceUtil.StringIsNull(sign)) {
            String[] signParam = sign.split("&");
            sign = ElongHotelInterfaceUtil.sort(signParam);
            return sign;
        }
        return "";
    }

    /**
     * 
     * 
     * @param merchantId
     * @return
     * @time 2015年12月24日 下午8:23:55
     * @author w.c.l
     */
    public InterfaceAccount getInterfaceAccount(String merchantId) {
        if (interfaceAccountMap == null) {
            interfaceAccountMap = new HashMap<String, InterfaceAccount>();
        }
        //-----加缓存机制不用每次都去数据库查-----S
        //chendong 2015年4月11日19:18:11
        InterfaceAccount interfaceAccount = interfaceAccountMap.get(merchantId);
        if (interfaceAccount == null) {
            interfaceAccount = getInterfaceAccountByLoginname(merchantId);
            if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                    && interfaceAccount.getInterfacetype() != null) {
                interfaceAccountMap.put(merchantId, interfaceAccount);
            }
        }

        return interfaceAccount;
    }

    /**
     * 
     * 
     * @param day 占座成功日期
     * @param minute 向后过多少分钟
     * @return 返回的是字符串型的时间
     * @time 2015年12月11日 下午2:33:19
     * @author Mr.Wang
     */
    public static String addDateMinut(String day, int minute) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制  
        Date date = null;
        try {
            if (ElongHotelInterfaceUtil.StringIsNull(day))
                return "";
            date = format.parse(day);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null)
            return "";
        //        System.out.println("front:" + format.format(date)); //显示输入的日期  
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);// 24小时制   
        date = cal.getTime();
        //        System.out.println("after:" + format.format(date)); //显示更新后的日期 
        cal = null;
        return format.format(date);
    }

    /**
     * 根据艺龙座位name 返回座位type
     * @time 2015年12月8日 下午1:44:19
     * @author Mr.Wang
      * @param seatType 
      * //坐席类型 0 站票 1 硬座  2 软座  3 硬卧  4 软卧  5 高级软卧  6 一等软座  7 二等软座  8 商务座  
                  9 一等座  10  二等座  11  特等座  12  观光座  13  特等软座  14  一人软包  15  动软
      * @return 
     */
    public String getzwnameByYlseatTypeCode(String seatType) {
        String str = "";
        if ("动软".equals(seatType)) {
            str = "15";
        }
        else if ("一人软包".equals(seatType)) {
            str = "14";
        }
        else if ("特等软座".equals(seatType)) {
            str = "13";
        }
        else if ("观光座".equals(seatType)) {
            str = "12";
        }
        else if ("特等座".equals(seatType)) {
            str = "11";
        }
        else if ("二等座".equals(seatType)) {
            str = "10";
        }
        else if ("一等座".equals(seatType)) {
            str = "9";
        }
        else if ("商务座".equals(seatType)) {
            str = "8";
        }
        else if ("二等软座".equals(seatType)) {
            str = "7";
        }
        else if ("一等软座".equals(seatType)) {
            str = "6";
        }
        else if ("高级软卧".equals(seatType)) {
            str = "5";
        }
        else if ("软卧".equals(seatType)) {
            str = "4";
        }
        else if ("硬卧".equals(seatType)) {
            str = "3";
        }
        else if ("软座".equals(seatType)) {
            str = "2";
        }
        else if ("硬座".equals(seatType)) {
            str = "1";
        }
        else if ("无座".equals(seatType)) {
            str = "0";
        }
        return str;
    }

    /**
     * 是否接受站票
     * @param jsonObject2 
     * @param trainorder 
     * 
     * @param orderid
     * @param acceptStand 
     * @param ticketPrice  1 代表接受站票
     */
    public void createTrainOrderExtSeat(Trainorder trainorder, JSONObject jsonObject1) {
        try {
            long orderid = trainorder.getId();
            long agentid = trainorder.getAgentid();
            boolean isNeedStandingSeat = false;
            if (agentid == 59) {//在采购商是聚合数据的情况下，下单中如果    is_accept_standing，是否接受无座   字段   为空时，我们默认他接受无座 
                if (!jsonObject1.containsKey("is_ accept _ standing") && !jsonObject1.containsKey("is_accept_standing")) {
                    isNeedStandingSeat = true;
                }
                else {
                    if (jsonObject1.containsKey("is_ accept _ standing")) {
                        isNeedStandingSeat = jsonObject1.getBooleanValue("is_ accept _ standing");
                    }
                    else if (jsonObject1.containsKey("is_accept_standing")) {
                        isNeedStandingSeat = jsonObject1.getBooleanValue("is_accept_standing");
                    }
                    else if (jsonObject1.containsKey("hasseat")) {
                        isNeedStandingSeat = !jsonObject1.getBooleanValue("hasseat");
                    }
                }
            }
            else {
                if (jsonObject1.containsKey("is_ accept _ standing")) {
                    isNeedStandingSeat = jsonObject1.getBooleanValue("is_ accept _ standing");
                }
                else if (jsonObject1.containsKey("is_accept_standing")) {
                    isNeedStandingSeat = jsonObject1.getBooleanValue("is_accept_standing");
                }
                else if (jsonObject1.containsKey("hasseat")) {
                    isNeedStandingSeat = !jsonObject1.getBooleanValue("hasseat");
                }
                else if (jsonObject1.containsKey("hasSeat")) {
                    isNeedStandingSeat = jsonObject1.getBooleanValue("hasSeat");
                }
            }

            //闪联接受无座票会传无座
            String zwName = "";
            for (int i = 0; i < jsonObject1.getJSONArray("passengers").size(); i++) {
                zwName = jsonObject1.getJSONArray("passengers").getJSONObject(i).getString("zwname");// 座位名称;
                if ("无座".equals(zwName))
                    break;
            }
            Float ticketPrice = trainorder.getOrderprice();
            WriteLog.write("createTrainOrderExtSeat", orderid + ":" + (isNeedStandingSeat || "无座".equals(zwName)));
            if (isNeedStandingSeat || "无座".equals(zwName)) {
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("0", ticketPrice);
                    jsonArray.add(jsonObject);
                    String extseats = jsonArray.toJSONString();//[{"5":389},{"6":428}]
                    String sql = "INSERT INTO TrainOrderExtSeat (OrderId ,ExtSeat ,ReMark) VALUES ( " + orderid + ",'"
                            + extseats + "' ,'')";
                    WriteLog.write("createTrainOrderExtSeat", orderid + ":sql:" + sql);
                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    WriteLog.write("Error_MyThreadQunarOrder_createTrainOrderExtSeat", orderid + "");
                    ExceptionUtil.writelogByException("Error_MyThreadQunarOrder_createTrainOrderExtSeat", e);
                }
            }
        }
        catch (Exception e) {
        }
    }
}
