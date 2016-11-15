package com.ccservice.b2b2c.atom.servlet.yl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 艺龙火车票回调的方法
 * 
 * @time 2015年12月8日 上午11:49:50
 * @author Administrator
 */
public class YiLongCallBackMethod {
    /**
     * 
     */
    //    public static void main(String[] args) {
    //        String currentUserDistId = "pos78";
    //        String pt = "78lo";
    //        String cc = "sdf78";
    //        String fd = "78oop";
    //        String gd = "gpd";
    //        String Sign = currentUserDistId + pt + cc + fd + gd;
    //        try {
    //            Sign = ElongHotelInterfaceUtil.MD5(Sign);
    //            String kk = "currentUserDistId=" + currentUserDistId + "&sign=" + Sign + "&pt=" + pt + "&cc=" + cc + "&fd="
    //                    + fd + "&gd=" + gd;
    //            String result = SendPostandGet.submitPost(
    //                    "http://192.168.0.144:9018/iftInterface/host/obtweb/QteFlightQuery", kk, "utf-8").toString();
    //            System.out.println(result);
    //        }
    //        catch (Exception e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //    }

    Trainorder trainorder;

    public YiLongCallBackMethod(Trainorder trainorder) {
        this.trainorder = trainorder;
    }

    /**
     * 3.3出票结果 回调
     * 代理商code  merchantCode    必填  由艺龙分配   
    订单号 orderId 必填  订单号 为下单时订单号
    出票结果    result  必填  SUCCESS或者FAIL   成功或者失败
    出票信息    tickets result为SUCCESS时必填   出票信息    
    出票失败原因  failReason  result为FAIL时必填  出票失败原因错误代码  
    失败原因描述  failReasonDesc  result为FAIL时必填  出票失败原因描述    
    备注  comment 非必填 备注  
    签名  sign    必填  数据签名    参照签名机制

     * @time 2015年12月8日 上午11:52:07
     * @author yangtao
     */
    public String payCallBack(Trainorder trainorder, String merchantCode, String payCallbackUrl_temp, String key,
            String msgs) {
        //        String result = "";
        try {
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":msgs:" + msgs);
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":merchantCode:" + merchantCode);
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":key:" + key);
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":payCallbackUrl_temp:" + payCallbackUrl_temp);
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":" + JSONObject.toJSONString(trainorder));
        }
        catch (Exception e) {
        }
        String cnMsgString = "";//中文失败原因
        boolean orderIsSuccess = false;//订单是否成功
        if (trainorder.getOrderstatus() == 8 || (msgs != null && ("false".equals(msgs) || msgs.length() > 0))) {//如果msgs 不为空并且 为false 或者成都大于0 就是失败的
            orderIsSuccess = false;//订单失败了
            try {
                cnMsgString = URLDecoder.decode(msgs.equals("false") ? "支付失败" : msgs, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else if (msgs == null || "".equals(msgs)) {
            orderIsSuccess = true;//订单是否成功
        }

        String sign = "";
        String jilu = "";
        String ticketNo = trainorder.getExtnumber();
        String orderId = trainorder.getQunarOrdernumber();
        String singmd = "";
        String failReason = "";
        String failReasonDesc = cnMsgString;
        // JSONObject jsonObject = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("ticketNo", ticketNo);
        json.put("orderId", orderId);
        JSONArray tickets = gettickets();
        json.put("tickets", tickets);
        WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":tickets" + tickets);
        WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":jsonS2W这里:" + json);
        WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":result:cnMsgString:回调结果是什么:" + cnMsgString);
        WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":result:failReasonDesc:" + failReasonDesc);
        if (orderIsSuccess) {
            /* jsonObject.put("retCode", "200");
             jsonObject.put("retDesc", "成功");*/
            jilu = "SUCCESS";
            try {
                String jsons = URLDecoder.decode(json.toJSONString(), "UTF-8");
                String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "result=" + jilu,
                        "tickets=" + jsons };
                sign = sort(parameters) + key;
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":" + ":成功排序之后的字段：" + sign);
        }
        else {
            //            result = msg;
            try {
                String jsons = URLDecoder.decode(json.toJSONString(), "UTF-8");
                failReason = failReasonDescerr(URLDecoder.decode(msgs, "UTF-8"));
                //                failReasonDesc = msg;
                if (failReasonDesc.contains("已满")) {
                    failReasonDesc = "已取纸质车票";
                }
                jilu = "FAIL";
                String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "result=" + jilu,
                        "tickets=" + jsons, "failReasonDesc=" + failReasonDesc, "failReason=" + failReason };
                sign = sort(parameters) + key;
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":" + ":失败排序之后的字段：" + sign);
        }
        String result = "";
        try {
            //singmd = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
            singmd = ElongHotelInterfaceUtil.MD5(sign).toUpperCase();
            String parm = "merchantCode=" + merchantCode + "&orderId=" + orderId + "&result=" + jilu + "&tickets="
                    + json + "&failReasonDesc=" + URLEncoder.encode(failReasonDesc, "UTF-8") + "&failReason="
                    + failReason + "&comment=" + "&sign=" + singmd;
            //{"message":"","retcode":200,"retdesc":"成功","serverIp":"172.21.27.41","trainItfControllerFlag":"0"}
            result = SendPostandGet.submitPost(payCallbackUrl_temp, parm, "utf-8").toString();
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":jilu:" + jilu + ":最终返回给艺龙的结果" + parm + "url499"
                    + payCallbackUrl_temp + "艺龙返回的结果：" + result);
            JSONObject jsonObjectrest = JSONObject.parseObject(result);
            Integer retcode = (Integer) jsonObjectrest.get("retcode");
            String retdesc = (String) jsonObjectrest.get("retdesc");
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":" + "retcode:" + retcode + ":艺龙返回的结果retdesc："
                    + retdesc);
            if (!retdesc.contains("成功") || retcode != 200) {
                for (int i = 1; i < 6; i++) {
                    result = SendPostandGet.submitPost(payCallbackUrl_temp, parm, "utf-8").toString();
                    if (retdesc.contains("成功") || retcode == 200) {
                        result = "success";
                        break;
                    }
                }
            }
            else {
                if (jsonObjectrest.getBooleanValue("成功") || jsonObjectrest.getBooleanValue("200")
                        || jsonObjectrest.getBooleanValue("success") || retcode.equals(200) || retdesc.contains("成功")
                        || retcode == 200) {
                    result = "success";
                }
                else {
                    result = "false";
                }
            }
        }
        catch (Exception e) {
            System.out.println("异常:" + e);
        }
        return result;
    }

    /**
     * 
     * @return
     * @time 2016年4月1日 下午7:09:36
     * @author chendong
     */
    private JSONArray gettickets() {
        //        String ticketNo = "";
        //        String orderId = "";
        JSONArray tickets = new JSONArray();
        JSONArray passengers = new JSONArray();
        for (int i = 0; i < trainorder.getPassengers().size(); i++) {
            Trainpassenger trainpassenger = trainorder.getPassengers().get(i);
            Trainticket trainticket = trainpassenger.getTraintickets().get(0);
            JSONObject passengerjson = new JSONObject();
            //            ticketNo = trainorder.getExtnumber();
            //            orderId = trainorder.getQunarOrdernumber();
            //orderId = trainorder.getOrdernumber();
            //            passengerid int 乘客的顺序号
            /*        String passengerid = trainpassenger.getPassengerid() == null ? "0" : trainpassenger.getPassengerid();
                    passengerjson.put("passengerid", passengerid);*/
            //            ticket_no   string ok  票号（此票在本订单中的唯一标识，订票成功后才有值）

            //            passengersename string ok  乘客姓名
            String name = geturlencode(trainpassenger.getName());
            passengerjson.put("passengerName", name);
            /* try {
                 String Names = URLDecoder.decode(name, "UTF-8");
             }
             catch (UnsupportedEncodingException e1) {
                 // TODO Auto-generated catch block
                 e1.printStackTrace();
             }*/
            passengerjson.put("orderItemId", trainpassenger.getPassengerid());
            //            passportseno ok    string  乘客证件号码
            passengerjson.put("certNo", geturlencode(trainpassenger.getIdnumber()));
            //            passporttypeseid    string  证件类型ID
            //            与名称对应关系:  ok
            //            1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通行证，B:护照
            //   passengerjson.put("seatType", TongchengTrainOrder.getIdtype12306(trainpassenger.getIdtype()));
            //      ok      1:成人票，2:儿童票，3:学生票，4:残军票
            int types = trainticket.getTickettype();
            passengerjson.put("ticketType", YiLongCallBackMethod.TicketTypeWhere(types) + "");
            //            zwcode  string  座位编码。
            //            与座位名称对应关系： ok
            //            9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，
            //            4:软卧，3:硬卧，2:软座，1:硬座
            //            注意：当最低的一种座位，无票时，购买选择该座位种类，买下的就是无座(也就说买无座的席别编码就是该车次的最低席别的编码)，另外，当最低席别的票卖完了的时候才可以卖无座的票。
            //            passengerjson.put("seatType", geturlencode(TicketZw(trainticket.getSeattype())));
            //TODO 
            String seatCode = selectYLSeatCode(trainticket);
            passengerjson.put("seatType", geturlencode(seatCode.equals("") ? TicketZw(trainticket.getSeattype())
                    : seatCode));
            try {
                //cxin    string  几车厢几座（在订票成功后才会有值）
                String cxin = trainticket.getCoach() + "车" + trainticket.getSeatno() + "号";
                cxin = geturlencode(cxin);
                // trainorder.getPassengers().get(0).getPassengerid();
                passengerjson.put("seatNo", cxin);
            }
            catch (Exception e) {
            }
            //            price   string  票价
            passengerjson.put("price", trainticket.getPrice() + "");
            //passengerjson.put("agentid", agentid);
            /*   
             //reason  int 身份核验状态 0：正常 1：待审核 2：未通过
               passengerjson.put("reason", trainpassenger.getAduitstatus());
             */
            WriteLog.write("yiLong-3.3出票结果回调", trainorder.getId() + ":" + ":passengerjson" + passengerjson);
            passengers.add(passengerjson);
            tickets = passengers;
        }
        return tickets;
    }

    /**
     * 退票结果
     * 代理商code  merchantCode    必填  由艺龙分配   
    订单号 orderId 必填  订单号 
    订单项号    orderItemId 必填  订单项号    
    退票结果    result  必填  SUCCESS或者FAIL   成功或者失败
    退票金额    amount  result为SUCCESS时必填   退款金额    线上退票成功，退款金额
    备注  comment 非必填 备注  
    失败原因    failReason  result为FAIL时必填  出票失败原因错误代码  
    失败原因描述  failReasonDesc  result为FAIL时必填  出票失败原因错误描述
    签名  sign    必填  数据签名    参照签名机制  
    2015年12月9日 16:00:12
    yangtao
     * @return 
     * 
     * */
    public static String refunCallBackYl(String merchantCode, String orderId, String orderItemId, Boolean result,
            String payCallbackUrl_temp, String key, String amount, String returnmsg, String returntype, String ticket_no) {
        WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":merchantCode:" + merchantCode + ":orderId:" + orderId
                + ":orderItemId:" + orderItemId + ":result:" + result + ":payCallbackUrl_temp:" + payCallbackUrl_temp
                + ":key:" + key + ":amount:" + amount + ":returnmsg:" + returnmsg + ":returntype:" + returntype
                + ":ticket_no:" + ticket_no);
        String sign = "";
        String jilu = "";
        String failReason = "";
        String failReasonDesc = "";
        String results = "";
        String comment = "";

        if ("线下退".equals(ticket_no)) {
            comment = "线下退退款退票elong";
            WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":" + "进了选择了");
        }
        else {
            UUID uuid = UUID.randomUUID();
            ticket_no = String.valueOf(uuid);
        }

        //  JSONArray jsonarr = new JSONArray();
        //线下退款操作
        if ("0".equals(returntype) || "2".equals(returntype)) {
            /***
             * 退票结果退款
             * 艺龙
             * 2015年12月9日 18:25:05
             * */
            results = YiLongCallBackMethod.refunCallBack(merchantCode, orderId, orderItemId, result, key, amount,
                    returnmsg, returntype, ticket_no);
            WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":看看线上还是线下" + returntype + ":key:" + key + "看看是否正确！");
        }
        else {
            try {
                if (result == true) {
                    jilu = "SUCCESS";
                    if ("线下退退款退票elong".equals(comment) || !"".equals(comment)) {
                        String[] parameterst = { "merchantCode=" + merchantCode, "orderId=" + orderId,
                                "result=" + jilu, "orderItemId=" + orderItemId, "amount=" + amount,
                                "comment=" + comment };
                        sign = sort(parameterst);
                        WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":成功排序之后的字段：" + sign + "|看看是不是重复退"
                                + returntype + "|comment:" + comment);
                    }
                    else {

                        String[] parameter = { "merchantCode=" + merchantCode, "orderId=" + orderId, "result=" + jilu,
                                "orderItemId=" + orderItemId, "amount=" + amount };
                        sign = sort(parameter);

                        WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":成功排序之后的字段：" + sign + "|看看线上还是线下"
                                + returntype);
                    }
                }
                else if (result == false) {
                    failReason = failReasonDescerr(URLDecoder.decode(returnmsg, "UTF-8"));
                    failReasonDesc = returnmsg;
                    jilu = "FAIL";
                    String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "result=" + jilu,
                            "failReasonDesc=" + URLDecoder.decode(failReasonDesc, "UTF-8"), "failReason=" + failReason,
                            "orderItemId=" + orderItemId };

                    sign = sort(parameters);
                    WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":失败排序之后的字段：" + sign + "|看看线上还是线下"
                            + returntype + "|结果" + returnmsg);
                }
                sign = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
                String parm = "merchantCode=" + merchantCode + "&orderId=" + orderId + "&result=" + jilu
                        + "&failReasonDesc=" + failReasonDesc + "&failReason=" + failReason + "&sign=" + sign
                        + "&amount=" + amount + "&orderItemId=" + orderItemId + "&comment=" + comment;
                results = SendPostandGet.submitPost(payCallbackUrl_temp, parm, "utf-8").toString();
                WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":payCallbackUrl_temp:" + payCallbackUrl_temp);
                WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":parm:" + parm);
                WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":sign:" + sign + ":key:" + key);
                WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":results:" + results);
                JSONObject jsonObjectrest = JSONObject.parseObject(results);
                Integer retcode = jsonObjectrest.getInteger("retcode");
                String retdesc = jsonObjectrest.getString("retdesc");
                //成功
                if ("200".equals(retcode) || "成功".equalsIgnoreCase(retdesc)) {
                    results = "success";
                }
                else {
                    results = "false";
                }
            }
            catch (Exception e) {

            }
        }

        return results;
    }

    public static void main(String[] args) throws Exception {

        //        Trainorder trainorder = new Trainorder();
        //        String re = new YiLongCallBackMethod(trainorder).CreatereFuntiketNo();
        //        System.out.println(re);
        //        String key = "72d97c2acb5923234b2f80a7dbe8d85c";
        //        String sign = "amount=136.5&comment=成功&merchantCode=hangtian111&orderId=20160720346362544&orderItemId=20160720346362545&tradeNo="
        //                + re;
        //        //        amount=91&merchantCode=hangtian111&orderId=20160720346559742&orderItemId=20160720346559744&result=SUCCESS
        //        //          amount=492&comment=成功&merchantCode=hangtian111&orderId=20160718344818378&orderItemId=20160718344818381&tradeNo=E6894161131150083      
        //        String singmd = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
        //        String parm = "merchantCode=hangtian111&orderId=20160720346362544&orderItemId=20160720346362545&comment=%E6%88%90%E5%8A%9F&amount=136.5&tradeNo="
        //                + re + "&sign="
        // + singmd;
        //        //请求艺龙
        //        String payCallbackUrl_temp = "http://trainapi.elong.com/open_api/process_refund";
        //        WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", re + ":parm:" + parm + ":payCallbackUrl_temp:"
        //                + payCallbackUrl_temp);
        //        re = SendPostandGet.submitPost(payCallbackUrl_temp, parm, "utf-8").toString();
        //        WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", re + ":parm:" + parm + ":payCallbackUrl_temp:"
        //                + payCallbackUrl_temp);
        for (int i = 0; i < 1000; i++) {
            UUID uuid = UUID.randomUUID();
            System.out.println(uuid);
        }

    }

    /**
     * 创建退票流水号
     * @return
     * @time 2016年7月21日 下午3:39:08
     * @author yangtao
     */
    public static String CreatereFuntiketNo() {
        int lnumber = (int) (10 + Math.random() * (90 - 1 + 1));
        int lnumbers = (int) (100 + Math.random() * (90 - 1 + 1));
        String orderNumber = getDateByPattern("yyyyMMddHHmmssSSS") + lnumber + lnumbers;
        return orderNumber;
    }

    public static String getDateByPattern(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * 代理商code  merchantCode    必填  由艺龙分配   
    订单号 orderId 必填  订单号 
    订单项号    orderItemId 必填  订单项号    
    退款流水号   tradeNo 必填  由供应商提供的唯一的流水编号。不允许重复    退款流水号
    退款金额    amount  必填  退款金额    大于0
    退款说明    comment 必填  备注  
    签名  sign    必填  数据签名    参照签名机制
     * 退款
     * yangtao
     * 2015年12月11日 18:03:14
     */
    public static String refunCallBack(String merchantCode, String orderId, String orderItemId, boolean result,
            String key, String amount, String returnmsg, String returntype, String ticket_no) {
        String comment = "";
        String singmd = "";
        String ret = "";
        String sign = "";
        //0：表示线下退票退款  2：线下改签退款
        WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", "|key：" + key + ":returntype:" + returntype + "|returnmsg"
                + returnmsg);
        try {
            if (result || result == true) {
                comment = "成功";
                comment = URLEncoder.encode(comment, "UTF-8");
            }
            else {
                comment = returnmsg;
            }
            String[] parameters = { "merchantCode=" + merchantCode, "orderId=" + orderId, "orderItemId=" + orderItemId,
                    "amount=" + amount, "comment=" + URLDecoder.decode(comment, "UTF-8"), "tradeNo=" + ticket_no };
            sign = sort(parameters);
            WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":sign:" + sign + "←看下排序后的结果" + "result：" + result
                    + "←看下result进来的什么结果" + ":key:" + key);
            singmd = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
            String parm = "merchantCode=" + merchantCode + "&orderId=" + orderId + "&orderItemId=" + orderItemId
                    + "&comment=" + comment + "&amount=" + amount + "&tradeNo=" + ticket_no + "&sign=" + singmd;
            //请求艺龙
            String payCallbackUrl_temp = PropertyUtil.getValue("httpYiLong", "Train.properties");
            WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":parm:" + parm + ":payCallbackUrl_temp:"
                    + payCallbackUrl_temp);
            ret = SendPostandGet.submitPost(payCallbackUrl_temp, parm, "utf-8").toString();
            WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":ret:" + ret + ":singmd:" + singmd + "看看编译后的字段");
            JSONObject jsonObjectrest = JSONObject.parseObject(ret);
            Integer retcode = (Integer) jsonObjectrest.get("retcode");
            String retdesc = (String) jsonObjectrest.get("retdesc");
            if (jsonObjectrest.getBooleanValue("成功") || jsonObjectrest.getBooleanValue("200")
                    || jsonObjectrest.getBooleanValue("success") || retcode.equals(200) || retdesc.contains("成功")
                    || retcode == 200) {
                ret = "success";
            }
            else {
                ret = "false";
            }
            WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", orderId + ":orderId:" + orderItemId + ":orderItemId:"
                    + ":yl艺龙返回:" + ret);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 
     * 
     * @param str
     * @time 2015年12月10日 上午11:39:31
     * @author Mr.Wang
     */
    private static String sort(String[] str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            String maxStr = str[i];
            int index = i;
            //  System.out.println(str.length + ":第" + i + "次循环");
            for (int j = i + 1; j < str.length; j++) {
                if (maxStr.compareTo(str[j]) >= 0) {
                    maxStr = str[j];
                    index = j;
                }
            }
            str[index] = str[i];
            str[i] = maxStr;
            // System.out.println(i + ":" + maxStr);
            sb.append(maxStr + "&");
        }
        String sign = sb.toString();
        if (sign.endsWith("&")) {
            sign = sign.substring(0, sb.toString().length() - 1);
        }
        return sign;
    }

    /**
     * 
    成人，小孩。儿童 1:成人票，2:儿童票，3:学生票，4:残军票
    0   儿童票
    1   成人票
    2   学生票
    2015年12月8日 15:47:38
    票类型
    yangtao
     * */
    private static int TicketTypeWhere(int ticketWen) {
        // 1:成人票
        if (ticketWen == 1) {
            ticketWen = 1;
        }
        //2:儿童票，
        else if (ticketWen == 2) {
            ticketWen = 0;
        }
        //3:学生票
        else if (ticketWen == 3) {
            ticketWen = 2;
        }
        return ticketWen;
    }

    /**
     * 2015年12月9日 17:42:46
     * 以下给出的出票原因以及对应码，供应商需要提供根据失败原因填写上对应的编码
     * ，失败原因应该将从12306获得的失败原因原封不动的返回给艺龙。
     * 。”错误信息。出票失败原因代码表
    编码  说明  备注
    0   其他  
    1   所购买的车次坐席已无票 
    2   身份证件已经实名制购票，不能再次购买同日期同车次的车票 
    3   票价和12306不符  
    4   车次数据与12306不一致   
    5   乘客信息错误  
    6   12306乘客身份信息核验失败 12306乘客身份信息核验失败，passengerReason必填
    yangtao
     * @return 
     * */
    private static String failReasonDescerr(String erreResult) {
        WriteLog.write("艺龙火车票接口_1.1申请分配座位席别回调", ":错误信息：" + erreResult);

        if (erreResult.contains("无票 ")) {
            erreResult = "1";
        }
        else if (erreResult.contains("同日期同车次的车票 ")) {
            erreResult = "2";
        }
        else if (erreResult.contains("不符  ")) {
            erreResult = "3";
        }
        else if (erreResult.contains("不一致")) {
            erreResult = "4";
        }
        else if (erreResult.contains("信息错误 ")) {
            erreResult = "5";
        }
        else if (erreResult.contains("身份信息核验失败")) {
            erreResult = "6";
        }
        else {
            erreResult = "0";
        }
        WriteLog.write("艺龙火车票接口_1.1申请分配座位席别回调", ":错误码返回：" + erreResult);

        return erreResult;
    }

    private String geturlencode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    /**
    0   站票，1   硬座，2   软座
    3   硬卧，4   软卧，5   高级软卧
    6   一等软座，7   二等软座，8   商务座
    9   一等座，10  二等座，11  特等座
    12  观光座，13  特等软座，14  一人软包，15  动软  16 高级动软
    yangtao
    2015年12月9日 17:39:46
                 坐席类型
     * */
    private static String TicketZw(String zuowei) {
        String str = "";
        if ("动软".equals(zuowei) || "动卧".equals(zuowei)) {
            str = "15";
        }
        else if ("高级动软".equals(zuowei) || "高级动卧".equals(zuowei)) {
            str = "16";
        }
        else if ("一人软包".equals(zuowei)) {
            str = "14";
        }
        else if ("特等软座".equals(zuowei)) {
            str = "13";
        }
        else if ("观光座".equals(zuowei)) {
            str = "12";
        }
        else if ("特等座".equals(zuowei)) {
            str = "11";
        }
        else if ("二等座".equals(zuowei)) {
            str = "10";
        }
        else if ("一等座".equals(zuowei)) {
            str = "9";
        }
        else if ("商务座".equals(zuowei)) {
            str = "8";
        }
        else if ("二等软座".equals(zuowei)) {
            str = "7";
        }
        else if ("一等软座".equals(zuowei)) {
            str = "6";
        }
        else if ("高级软卧".equals(zuowei)) {
            str = "5";
        }
        else if ("软卧".equals(zuowei)) {
            str = "4";
        }
        else if ("硬卧".equals(zuowei)) {
            str = "3";
        }
        else if ("软座".equals(zuowei)) {
            str = "2";
        }
        else if ("硬座".equals(zuowei)) {
            str = "1";
        }
        else if ("站票".equals(zuowei) || "无座".equals(zuowei)) {
            str = "0";
        }
        return str;
    }

    /**
     * 将艺龙的坐席从DB中取出
     * 
     * @param trainorder
     * @param jsonObject
     * @time 2016年6月21日 下午5:54:28
     * @author fiend
     */
    private String selectYLSeatCode(Trainticket trainticket) {
        String seatCode = "";
        String sql = " sp_YiLongTicketSeatCode_Select @TicketId=" + trainticket.getId();
        WriteLog.write("YiLongTrainOrderMethod_selectYLSeatCode", trainorder.getId() + "--->" + sql);
        try {
            List list = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                seatCode = map.get("SeatCode").toString();
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("YiLongTrainOrderMethod_selectYLSeatCode_error", e);
        }
        return seatCode;
    }
}
