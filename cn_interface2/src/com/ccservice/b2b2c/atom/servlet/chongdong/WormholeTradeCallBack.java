package com.ccservice.b2b2c.atom.servlet.chongdong;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class WormholeTradeCallBack {

    public final static String userName = "hangtianhuayou.api";

    //测试
    //    public final static String password = "Op8^Aa5%";

    //测试
    //    public final static String congDongAuthUrl = "https://stage.wfinance.com.cn:10443/cdi/v2/transactions/submit";

    //正式
    public final static String password = "Vy2$Tw8^";

    //正式
    public final static String congDongAuthUrl = "https://www.wfinance.com.cn/cdi/v2/transactions/submit";

    public static final String TRANSACTIONCURRENCY = "CNY";

    public static final String BUSINESSTYPE = "RAILWAY_TICKET";

    public static final String INSURER = "阳光保险";

    public static final String REMARK = "";

    public static final String REMARK_1 = "";

    //sign :1代表   ORDER - 订票 ;2 代表   RESIGN - 改签;3代表  REFUND - 退票.
    public String trade(String qunarordernumber, int sign) {
        return trade(qunarordernumber, sign, new JSONArray());
    }

    /**
     * 如果改签或者退票，要传票信息
     * 
     * @param qunarordernumber
     * @param sign
     * @param ticketArray
     * @return
     * @time 2016年7月18日 下午4:26:25
     * @author fiend
     */
    public String trade(String qunarordernumber, int sign, JSONArray ticketArray) {

        int r1 = new Random().nextInt(10000000);
        WriteLog.write("回调虫洞交易接口", r1 + "--->" + qunarordernumber + "--->" + sign);
        String result = "";
        int agentid = 0;
        JSONObject jsonObject = new JSONObject();
        JSONArray transactions = new JSONArray();
        String TRANSACTIONTYPE = null;

        //        String sql = "select *  from T_TRAINORDER a,T_TRAINPASSENGER b,T_TRAINTICKET c where a.ID=b.C_ORDERID and b.ID=c.C_TRAINPID  and a.C_QUNARORDERNUMBER='"
        //                + qunarordernumber + "'";
        String sql = null;
        if (sign == 1 || sign == 3) {
            sql = "exec [sp_T_TRAINORDER_LinkedQuery] @qunarordernumber='" + qunarordernumber + "'";//出票  退票
        }
        if (sign == 2) {
            sql = "exec [sp_T_TRAINORDERCHANGE_LinkedQuery] @C_QUNARORDERNUMBER='" + qunarordernumber + "'";//改签
        }
        List list = new ArrayList();
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            WriteLog.write("回调虫洞交易接口_Exception", r1 + "------>sql" + sql);
            ExceptionUtil.writelogByException("回调虫洞交易接口_Exception", e);
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            JSONObject transaction = new JSONObject();
            Map map = (Map) list.get(i);
            String ticketNumber = map.get("C_TICKETNO") == null ? null : map.get("C_TICKETNO").toString();
            String tcticketNumber = map.get("C_TCTICKETNO") == null ? null : map.get("C_TCTICKETNO").toString();
            if (ticketArray.size() > 0) {
                boolean haveThidTicket = false;
                for (int j = 0; j < ticketArray.size(); j++) {
                    JSONObject ticketJsonObject = ticketArray.getJSONObject(j);
                    if (ticketJsonObject.getString("ticket_no").equals(ticketNumber)
                            || ticketJsonObject.getString("ticket_no").equals(tcticketNumber)) {
                        haveThidTicket = true;
                        break;
                    }
                }
                if (!haveThidTicket) {
                    continue;
                }
            }
            agentid = Integer.parseInt(map.get("C_AGENTID").toString());
            int orderid = Integer.parseInt(map.get("C_ORDERID").toString());
            transaction.put("accountNumber", getAccount(qunarordernumber));
            Date date = (Date) map.get("C_CREATETIME") == null ? new Date() : (Date) map.get("C_CREATETIME");
            transaction.put("transactionDate", gettime(date));
            transaction.put("transactionType", TRANSACTIONTYPE);
            transaction.put("businessType", BUSINESSTYPE);
            Double price = (Double) (map.get("C_PRICE") == null ? 0.00 : Double.parseDouble(map.get("C_PRICE")
                    .toString()));
            Double procedure = (Double) (map.get("C_PROCEDURE") == null ? 0.00 : Float.parseFloat(map
                    .get("C_PROCEDURE").toString()));
            Double newprice = (Double) (map.get("C_TCPRICE") == null ? 0.00 : Float.parseFloat(map.get("C_TCPRICE")
                    .toString()));
            transaction.put("transactionDate", gettime(date));
            if (sign == 1 || (sign == 2 && (newprice - price) > 0)) {
                TRANSACTIONTYPE = "DB";
            }
            if (sign == 3 || (sign == 2 && (newprice - price) < 0)) {
                TRANSACTIONTYPE = "CR";
            }
            transaction.put("transactionType", TRANSACTIONTYPE);
            transaction.put("businessType", BUSINESSTYPE);
            transaction.put("transactionCurrency", TRANSACTIONCURRENCY);
            transaction.put("remark", REMARK);
            JSONObject railway = new JSONObject();
            String serviceType = getWormholeserviceType(sign);
            railway.put("serviceType", serviceType);
            railway.put("ticketNumber", ticketNumber);
            String fromStationName = map.get("C_DEPARTURE") == null ? "" : map.get("C_DEPARTURE").toString();
            String fromStationCode = getstationcode(fromStationName);
            railway.put("fromStationCode", fromStationCode);
            String toStationName = map.get("C_ARRIVAL") == null ? "" : map.get("C_ARRIVAL").toString();
            String toStationCode = getstationcode(toStationName);
            railway.put("toStationCode", toStationCode);
            railway.put("fromStationName", fromStationName);
            railway.put("toStationName", toStationName);
            int ticketType = map.get("C_TICKETTYPE") == null ? 0 : Integer.parseInt(map.get("C_TICKETTYPE").toString());
            String ticketType_1 = getticketType(ticketType);
            railway.put("ticketType", ticketType_1);
            String trainCode = map.get("C_TRAINNO") == null ? "" : map.get("C_TRAINNO").toString();
            railway.put("trainCode", trainCode);
            String trainType = gettraintypeByCode(trainCode);
            railway.put("trainType", trainType);
            String seatno = map.get("C_SEATNO") == null ? "" : map.get("C_SEATNO").toString();
            String coach = map.get("C_COACH") == null ? "" : map.get("C_COACH").toString();
            String location = getLocation(seatno, coach);
            railway.put("location", location);
            String seatCode = map.get("C_SEATTYPE") == null ? "" : map.get("C_SEATTYPE").toString();
            String seatCode_1 = getcode(seatCode);
            railway.put("seatCode", seatCode_1);
            railway.put("seatName", seatCode);
            String orderNumber = map.get("C_ORDERNUMBER") == null ? "" : map.get("C_ORDERNUMBER").toString();
            railway.put("orderNumber", map.get("C_EXTNUMBER") == null ? "" : map.get("C_EXTNUMBER").toString());
            String departureTime = map.get("C_DEPARTTIME") == null ? null : map.get("C_DEPARTTIME").toString();
            railway.put("departureTime", getDepTimeWormhole(departureTime, r1));
            Double ticketAmount = (Double) (map.get("C_PRICE") == null ? 0.00 : Double.parseDouble(map.get("C_PRICE")
                    .toString()));
            ticketAmount = (Double) (map.get("C_TCPRICE") == null || "".equals(map.get("C_TCPRICE").toString())
                    || Double.parseDouble(map.get("C_TCPRICE").toString()) == 0 ? ticketAmount : Double.parseDouble(map
                    .get("C_TCPRICE").toString()));
            railway.put("ticketAmount", ticketAmount);
            Double insuranceFeeAmount = (Double) (map.get("C_INSURPRICE") == null ? 0.00 : Double.parseDouble(map.get(
                    "C_INSURPRICE").toString()));
            railway.put("insuranceFeeAmount", insuranceFeeAmount);
            Double transactionAmount = null;
            if (sign == 1) {
                transactionAmount = (Double) (ticketAmount + insuranceFeeAmount);
            }
            else if (sign == 2) {
                if (newprice > price) {
                    transactionAmount = (Double) (newprice - price);
                }
                else if (newprice < price) {
                    transactionAmount = (Double) (price - newprice);
                }
                else {
                    transactionAmount = (double) 0;
                }
            }
            if (sign == 3) {
                String insureNumber = map.get("C_REALINSURENO") + "";
                if (insureNumber.equals("") || insureNumber.equals("NULL")) {
                    insuranceFeeAmount = 0d;
                }
                transactionAmount = (ticketAmount + insuranceFeeAmount - procedure);
            }
            transaction.put("transactionAmount", transactionAmount);
            Float surchargeFeeAmount = (float) (map.get("C_PROCEDURE") == null ? 0.00 : Float.parseFloat(map.get(
                    "C_PROCEDURE").toString()));
            railway.put("surchargeFeeAmount", 0);
            String insurer = INSURER;
            //            try {
            //                insurer = URLEncoder.encode(insurer, "UTF-8");
            //            }
            //            catch (UnsupportedEncodingException e1) {
            //                // TODO Auto-generated catch block
            //                e1.printStackTrace();
            //            }
            railway.put("insurer", insurer);
            String policyNumber = map.get("C_INSURENO") == null ? "" : map.get("C_INSURENO").toString();
            railway.put("policyNumber", policyNumber);
            railway.put("remark", REMARK_1);
            JSONObject passengers = new JSONObject();
            String passengerName = map.get("C_NAME").toString();
            //            try {
            //                passengerName = URLEncoder.encode(passengerName, "UTF-8");
            //            }
            //            catch (UnsupportedEncodingException e) {
            //                e.printStackTrace();
            //            }
            passengers.put("passengerName", passengerName);
            int idType = map.get("C_IDTYPE") == null ? 1 : Integer.parseInt(map.get("C_IDTYPE").toString());
            passengers.put("idType", idType);
            String idNumber = map.get("C_IDNUMBER") == null ? "" : map.get("C_IDNUMBER").toString();
            passengers.put("idNumber", idNumber);
            railway.put("passenger", passengers);
            transaction.put("railway", railway);
            JSONObject extData = new JSONObject();
            extData.put("supplierOrderNumber", orderNumber);
            extData.put("originalSupplierOrderNumber", "");
            extData.put("externalAgentCode", "");
            extData.put("externalTransactionId", "");
            extData.put("externalInvoiceNumber", "");
            extData.put("externalCorporationId", "");
            extData.put("externalEmployeeId", "");
            extData.put("orderDateTime", "");
            extData.put("orderNumber", map.get("C_EXTNUMBER") == null ? "" : map.get("C_EXTNUMBER").toString());
            extData.put("ticketOrderNumber", "");
            extData.put("receiptOrderNumber", "");
            extData.put("contactName", "");
            extData.put("contactPhone", "");
            extData.put("contactEmail", "");
            extData.put("contactFax", "");
            extData.put("reasonCode", "");
            extData.put("reasonRemark", "");
            extData.put("advanceDays", "");
            extData.put("approvalSubmitTime", "");
            extData.put("approvalFinalTime", "");
            extData.put("approver", "");
            extData.put("feeOwner", "");
            extData.put("feeType", "");
            extData.put("departmentCode", "");
            extData.put("departmentName", "");
            extData.put("costCenter", "");
            extData.put("profitCenter", "");
            extData.put("settleMainCode", "");
            extData.put("settleMainName", "");
            extData.put("accountUnit", "");
            extData.put("internalAccount", "");
            extData.put("projectName", "");
            extData.put("projectNumber", "");
            extData.put("employeeId", "");
            extData.put("employeeTitle", "");
            extData.put("employeeLevel", "");
            extData.put("passengerNickName", "");
            extData.put("base", "");
            extData.put("vocationType", "");
            extData.put("discountAmount", "");
            extData.put("projectPhase", "");
            transaction.put("extData", extData);
            transactions.add(transaction);
        }
        jsonObject.put("transactions", transactions);
        String sqlString = "EXEC [dbo].[sp_T_INTERFACEACCOUNT_Query] @agentid=" + agentid;
        Map map = new HashMap();
        try {
            map = (Map) Server.getInstance().getSystemService().findMapResultBySql(sqlString, null).get(0);
        }
        catch (Exception e) {
            WriteLog.write("回调虫洞交易接口_Exception", r1 + "------>sqlString" + sqlString);
            ExceptionUtil.writelogByException("回调虫洞交易接口_Exception", e);
            e.printStackTrace();
        }
        String urlString = map.get("C_WormholetradeCallBackUrl") == null ? "" : map.get("C_WormholetradeCallBackUrl")
                .toString();
        //        result = SendPostandGet.submitGet2(urlString, jsonObject.toString(), "UTF-8");
        WriteLog.write("回调虫洞交易接口",
                r1 + "------>congDongAuthUrl:" + congDongAuthUrl + "--->jsonObject:" + jsonObject.toString());
        try {
            String result_1 = WormholeTradeCallBack.submitHttpclient(congDongAuthUrl, jsonObject.toString());
            CallBackFailCause callBackFailCause = new CallBackFailCause();
            WriteLog.write("回调虫洞交易接口", r1 + "------>result_1:" + result_1);
            result = callBackFailCause.dealreturndata(congDongAuthUrl, result_1, jsonObject.toString());
            WriteLog.write("回调虫洞交易接口", r1 + "------>result:" + result);
        }
        catch (Exception e) {
            WriteLog.write("回调虫洞交易接口_Exception", r1 + "");
            ExceptionUtil.writelogByException("回调虫洞交易接口_Exception", e);
            e.printStackTrace();
        }
        return result;
    }

    private String getWormholeserviceType(int sign) {
        String serviceType = "";
        if (sign == 1) {
            serviceType = "ORDER";
        }
        else if (sign == 2) {
            serviceType = "RESIGN";
        }
        else if (sign == 3) {
            serviceType = "REFUND";
        }

        return serviceType;
    }

    private String getstationcode(String fromStationName) {
        String fromstationcodeString = "";
        if (fromStationName != null && "!".equals(fromStationName)) {
            fromstationcodeString = Train12306StationInfoUtil.getSZMByName(fromStationName);
        }
        return fromstationcodeString;
    }

    private String gettraintypeByCode(String trainCode) {
        String trainType = "";
        if (trainCode != null && !"".equals(trainCode)) {

            String trainCode_1 = trainCode.substring(0, 1);
            if ("D".equals(trainCode_1)) {
                trainType = "D";
            }
            else if ("Z".equals(trainCode_1)) {
                trainType = "Z";
            }
            else if ("T".equals(trainCode_1)) {
                trainType = "KT";
            }
            else if ("K".equals(trainCode_1)) {
                trainType = "PK";
            }
            else if ("G".equals(trainCode_1)) {
                trainType = "GD";
            }
            else {
                trainType = "PM";
            }
        }

        return trainType;
    }

    private String getLocation(String seatno, String coach) {
        String location = "";
        if (!"".equals(seatno) && !"".equals(coach)) {
            location = coach + "车厢， " + seatno;
        }
        return location;
    }

    private String getcode(String seatCode) {
        String seatCode_1 = "";
        if ("硬座".equals(seatCode)) {
            seatCode_1 = "1";
        }
        else if ("软座".equals(seatCode)) {
            seatCode_1 = "2";
        }
        else if ("硬卧".equals(seatCode)) {
            seatCode_1 = "3";
        }
        else if ("软卧".equals(seatCode)) {
            seatCode_1 = "4";
        }
        else if ("高级软卧".equals(seatCode)) {
            seatCode_1 = "6";
        }
        else if ("二等座".equals(seatCode)) {
            seatCode_1 = "O";
        }
        else if ("一等座".equals(seatCode)) {
            seatCode_1 = "M";
        }
        else if ("特等座".equals(seatCode)) {
            seatCode_1 = "P";
        }
        else if ("商务座".equals(seatCode)) {
            seatCode_1 = "9";
        }
        return seatCode_1;
    }

    private String getticketType(int ticketType) {
        String ticketType_1 = "";
        if (ticketType == 1) {
            ticketType_1 = "ADULT";
        }
        else if (ticketType == 2) {
            ticketType_1 = "CHILD";
        }
        else if (ticketType == 3) {
            ticketType_1 = "STUDENT";
        }
        else if (ticketType == 4) {
            ticketType_1 = "DISABLED_OR_ARMY";
        }

        return ticketType_1;
    }

    public static Date gettime_1(String time, int r1) {
        Date date = new Date();
        if (time != null && !"".equals(time)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                date = sdf.parse(time);
            }
            catch (ParseException e) {
                WriteLog.write("回调虫洞交易接口_Exception", r1 + "------>time" + time);
                ExceptionUtil.writelogByException("回调虫洞交易接口_Exception", e);
                e.printStackTrace();
            }
        }
        return date;
    }

    public static String gettime(Date date) {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
        String time = sdformat.format(date);
        return time;
    }

    /**
     * 
     * @time 2016年5月19日 下午1:41:14
     * @author chendong
     * @param requestBody 
     * @param requestBody2 
     * @throws IOException 
     * @throws HttpException 
     */
    public static String submitHttpclient(String congDongAuthUrl, String requestBody) throws HttpException, IOException {
        CCSPostMethod post = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        post = new CCSPostMethod(congDongAuthUrl);
        String authorization = userName + ":" + password;
        authorization = "Basic " + new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        post.setRequestBody(requestBody);
        post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        post.addRequestHeader(new Header("Authorization", authorization));
        post.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
        httpClient.executeMethod(post);
        String dataHtml = post.getResponseBodyAsString();
        return dataHtml;
    }

    /**
     * 获取虫洞账号
     * 
     * @param interfaceNumber
     * @return
     * @time 2016年6月20日 下午1:56:55
     * @author fiend
     */
    private String getAccount(String interfaceNumber) {
        String account = "";
        try {
            String sql = "exec [sp_TrainOrderIsWormhole_select2] @orderid='" + interfaceNumber + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                account = map.get("WormholeAccount").toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return account;

    }

    /**
     * 获取正确的时间
     * 
     * @param departureTime
     * @param r1
     * @return
     * @time 2016年7月7日 下午12:56:12
     * @author fiend
     */
    private String getDepTimeWormhole(String departureTime, int r1) {
        String depTimeWormhole = departureTime;
        try {
            depTimeWormhole = gettime(gettime_1(depTimeWormhole, r1));
        }
        catch (Exception e) {
            WriteLog.write("回调虫洞交易接口_Exception", r1 + "------>time" + departureTime);
            ExceptionUtil.writelogByException("回调虫洞交易接口_Exception", e);
        }
        return depTimeWormhole;
    }

    public static void main(String[] args) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ticket_no", "EC949564212120094");
        jsonArray.add(jsonObject);
        new WormholeTradeCallBack().trade("T160804BB84775E0AC8F0468408826005D32D7187C4", 3, jsonArray);
    }
}
