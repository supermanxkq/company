package com.ccservice.b2b2c.atom.servlet.yilong;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainQueryInfo;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;

/**
 * Servlet implementation class YiLongQueryOrderServlet
 */
public class YiLongQueryOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final String logname="MeiTuan_3_11_先占座后支付模式订单查询";
	
	public final static int PAYTIME_DEAD = 25;
	
    String key;
    
    Map<String, InterfaceAccount> interfaceAccountMap = new HashMap<String, InterfaceAccount>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=utf-8");
        int r1 = new Random().nextInt(10000000);
        JSONObject result = new JSONObject();
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String merchantId = request.getParameter("merchantId");
            String timeStamp = request.getParameter("timeStamp");
            String orderId = request.getParameter("orderId");
            String sign = request.getParameter("sign");

            WriteLog.write(logname, r1 + ":" + merchantId + ":" + timeStamp + ":"
                    + orderId + ":" + sign);
            if (ElongHotelInterfaceUtil.StringIsNull(merchantId) || ElongHotelInterfaceUtil.StringIsNull(timeStamp)
                    || ElongHotelInterfaceUtil.StringIsNull(orderId) || ElongHotelInterfaceUtil.StringIsNull(sign)) {
                result.put("retcode", "400");
                result.put("retdesc", "艺龙系统错误");
            }
            else {
                InterfaceAccount interfaceAccount = interfaceAccountMap.get(merchantId);
                if (interfaceAccount == null) {
                    interfaceAccount = getInterfaceAccountByLoginname(merchantId);
                    if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                            && interfaceAccount.getInterfacetype() != null) {
                        interfaceAccountMap.put(merchantId, interfaceAccount);
                    }
                }
                String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId;
                this.key = interfaceAccount.getKeystr();
                localSign = getSignMethod(localSign) + this.key;
                WriteLog.write(logname, ":第一个排序:" + localSign);
                localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
                WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign);
                if(localSign.equals(sign)){
                    result.put("retcode", "200");
                    WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign+",签名校检成功！");
                    result=queryOrder(orderId, r1);
                    
                }
                else{
                    result.put("retcode", "403");
                    result.put("retdesc", "签名校验失败");
                    WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign+",签名校检失败！");
                    return;
                }
                
            }
        }
        catch (Exception e) {
        }
        finally {
            WriteLog.write(logname, r1 + ":reslut:" + result);
            out.print(result);
            out.flush();
            out.close();
        }
	}
	
	
	public JSONObject queryOrder(String orderId,int r1){
	    JSONObject outjosn=new JSONObject();
	    JSONObject json=new JSONObject();
	    String sql = "SELECT ID,C_EXTORDERCREATETIME,C_ORDERNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='"
                + orderId + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            long trainorderId = Long.parseLong(map.get("ID").toString());
            String transactionid = map.get("C_ORDERNUMBER").toString();
            String holdingSeatSuccessTime = map.containsValue("C_EXTORDERCREATETIME") ? map.get(
                    "C_EXTORDERCREATETIME").toString() : "";

            json.put("orderid", orderId);
            json.put("transactionid", transactionid);
            WriteLog.write(logname, r1 + ":json:" + json.toString());
            String result = new TongChengTrainQueryInfo().trainqueryinfo(json);
            if (result.indexOf("查询订单成功") > -1) {
                WriteLog.write(logname, "tongchengQueryDataResult:" + result);
                JSONObject orderDetail = tongchengConversionElongDataMethod(trainorderId,
                        holdingSeatSuccessTime, result);
                outjosn.put("retcode", "200");
                outjosn.put("retdesc", "成功");
                outjosn.put("orderDetail", orderDetail);

            }
            else {
                outjosn = JSONObject.parseObject(result);
            }
        }
        else {
            outjosn.put("retcode", "452");
            outjosn.put("retdesc", "此订单不存在");
        }
	    return outjosn;
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
    public static InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        WriteLog.write(logname, "loginname:" + loginname);
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WriteLog.write(logname, "list_interfaceAccount:"
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
     * 将同程查询订单数据转换成艺龙的
     * 
     * @param result
     * @return
     * @time 2015年12月31日 下午12:00:11
     * @author Administrator
     */
    private JSONObject tongchengConversionElongDataMethod(long trainorderId, String holdingSeatSuccessTime,
            String result) {
        JSONObject json = new JSONObject();
        JSONArray passengers = new JSONArray();
        JSONObject tongchengResult = JSONObject.parseObject(result);
        JSONArray ticketstatus = tongchengResult.getJSONArray("ticketstatus");

        for (int i = 0; i < ticketstatus.size(); i++) {
            JSONObject passJson = new JSONObject();
            JSONObject details = ticketstatus.getJSONObject(i);
            passJson.put("certNo", details.getString("idnumber")); // 证件号
            passJson.put("certType", details.getString("idtype")); // 证件类型
            passJson.put("name", details.getString("passengersename")); //姓名
            passJson.put("orderItemId", details.getString("orderItemId")); //票item号
            passJson.put("ticketType", details.getString("piaotypename")); //票类型
            passJson.put("seatNo", details.getString("cxin")); //坐席号
            passJson.put("price", details.getString("price")); //单张票的价格
            json.put("seatType", getzwnameByYlseatTypeCode(details.getString("zwname"))); // 坐席类型
            passengers.add(passJson);
        }
        json.put("passengers", passengers); // 乘客信息列表
        json.put("arrStation", tongchengResult.getString("fromstation")); // 始发站名
        json.put("contactName", tongchengResult.getString("contactName")); // 联系人姓名
        json.put("dptStation", tongchengResult.getString("tostation")); // 到达站名
        json.put("orderDate", tongchengResult.getString("orderDate")); // 下单时间
        json.put("orderId", tongchengResult.getString("orderid")); // 订单号
        json.put("ticketNo", tongchengResult.getString("ordernumber")); // 12306订单号

        json.put("ticketPrice", tongchengResult.getString("ticketPrice")); // 票价总额
        json.put("trainEndTime", tongchengResult.getString("arrivetime")); // 到站时间
        json.put("trainNo", tongchengResult.getString("checi")); // 车次
        json.put("trainStartTime", tongchengResult.getString("traintime")); // 发车时间
        json.put("payTimeDeadLine", addDateMinut(holdingSeatSuccessTime, PAYTIME_DEAD)); // 用户支付截止时间
        json.put("holdingSeatSuccessTime", holdingSeatSuccessTime); // 占座成功时间
        Map<String, Object> orderStatus = orderStatusDescConversion(tongchengResult.getString("orderstatusname"));
        json.put("orderStatus", orderStatus.get("orderStatus")); // 订单状态码（见4.5）
        json.put("orderStatusDesc", orderStatus.get("orderStatusDesc")); // 订单状态描述（见4.5）
        Map<String, Object> failureReasonMap = failureReasonConversion(getFailureReasonDesc(trainorderId));
        json.put("failureReason", failureReasonMap.get("failureReason")); // 订单失败码
        json.put("failureReasonDesc", failureReasonMap.get("failureReasonDesc")); // 订单失败原因

        return json;
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
     * 获取下单失败原因
     * 
     * @param orderId
     * @return
     * @time 2015年12月31日 下午4:33:02
     * @author w.c.l
     */
    @SuppressWarnings("rawtypes")
    public String getFailureReasonDesc(long trainorderId) {
        String failureReasonDesc = "";
        String sql = "SELECT C_CONTENT FROM T_TRAINORDERRC WITH(NOLOCK) WHERE C_STATUS=2 and C_CREATEUSER='12306' AND C_ORDERID="
                + trainorderId;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            failureReasonDesc = map.containsValue("C_CONTENT") ? map.get("C_CONTENT").toString() : "";
        }
        return failureReasonDesc;
    }

    /**
     * 订单状态转换
     * 
     * @return
     * @time 2015年12月31日 下午2:17:51
     * @author w.c.l
     */
    public Map<String, Object> orderStatusDescConversion(String orderstatusname) {
        Map<String, Object> orderStatus = new HashMap<String, Object>();
        int statusCode = 0;

        if ("等待下单".equals(orderstatusname) || "正在下单".equals(orderstatusname)) {
            orderstatusname = "占座中";
            statusCode = 4;
        }
        else if ("下单失败".equals(orderstatusname)) {
            orderstatusname = "占座失败";
            statusCode = 8;
        }
        else if ("下单成功等待支付".equals(orderstatusname)) {
            orderstatusname = "占座成功";
            statusCode = 7;
        }
        else if ("下单成功支付中".equals(orderstatusname) || "支付审核中".equals(orderstatusname)) {
            orderstatusname = "出票中";
            statusCode = 12;
        }
        else if ("支付成功".equals(orderstatusname)) {
            orderstatusname = "出票成功";
            statusCode = 9;
        }
        else if ("支付失败".equals(orderstatusname)) {
            orderstatusname = "出票失败";
            statusCode = 11;
        }
        orderStatus.put("orderStatus", statusCode);
        orderStatus.put("orderStatusDesc", orderstatusname);
        return orderStatus;
    }

    /**
     * 失败理由转换
     * 
     * @param failureReasonDesc
     * @return
     * @time 2016年1月4日 下午1:21:13
     * @author w.c.l
     */
    public Map<String, Object> failureReasonConversion(String failureReasonDesc) {
        Map<String, Object> failureReasonMap = new HashMap<String, Object>();
        int failureReason = -1;
        if (failureReasonDesc.indexOf("没有余票") > -1 || failureReasonDesc.indexOf("此车次无票") > -1
                || failureReasonDesc.indexOf("已无余票") > -1 || failureReasonDesc.indexOf("没有足够的票") > -1
                || failureReasonDesc.indexOf("余票不足") > -1 || failureReasonDesc.indexOf("非法的席别") > -1) {

            failureReason = 1;
            failureReasonDesc = "所购买的车次坐席已无票";
        }
        else if (failureReasonDesc.indexOf("已订") > -1) {
            failureReason = 2;
            failureReasonDesc = "所购买的车次坐席已无票";
        }
        else if (failureReasonDesc.indexOf("身份验证失败") > -1) {
            failureReason = 6;
            failureReasonDesc = "12306乘客身份信息核验失败";
        }
        else if (failureReasonDesc.indexOf("多次打码失败") > -1 || failureReasonDesc.indexOf("获取12306账号失败") > -1) {
            failureReason = 8;
            failureReasonDesc = "12306服务错误";
        }
        else {
            failureReason = 0;
        }

        failureReasonMap.put("failureReason", failureReason);
        failureReasonMap.put("failureReasonDesc", failureReasonDesc);
        return failureReasonMap;
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

}
