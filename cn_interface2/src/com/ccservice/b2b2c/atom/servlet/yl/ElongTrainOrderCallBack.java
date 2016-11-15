package com.ccservice.b2b2c.atom.servlet.yl;

import java.net.URLEncoder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

/**
 * 回调艺龙占座结果
 * 
 * @time 2015年12月11日 上午10:40:36
 * @author Mr.Wang
 */
public class ElongTrainOrderCallBack extends TrainSelectLoginWay {

    private final String msgLogWriter = "Elong_先占座后支付模式占座回调_ElongTrainOrderCallBackServlet";

    private long r1 = System.currentTimeMillis();

    /**
     * 回调艺龙占座失败
     * 
     * @param result SUCCESS:成功  FAIL:失败
     * @param trainorder 订单信息
     * @param failReasonDesc 出票失败原因描述
     * @param failReason 出票失败原因错误代码
     * @time 2015年12月11日 上午10:35:58
     * @author Mr.Wang
     */
    public String trainOrderCallBackFail(String merchantCode, String key, String ElongCallBackUrl, String result,
            Trainorder trainorder, String returnmsg) {
        //占座失败
        //        failReasonDesc = failReasonConversion(failReasonDesc);
        JSONObject failReasonJsonObject = getFailJson(returnmsg);
        int failReason = failReasonJsonObject.getIntValue("failReason");
        String failReasonDesc = failReasonJsonObject.getString("failReasonDesc");
        WriteLog.write(msgLogWriter, r1 + ":>>>>" + r1 + ":" + merchantCode + ":" + key + ":" + ElongCallBackUrl + ":"
                + result + ":trainorder:" + JSONObject.toJSONString(trainorder));
        String callBackParam = "merchantCode=" + merchantCode + "&orderId=" + trainorder.getQunarOrdernumber()
                + "&result=" + result + "&failReason=" + failReason + "&failReasonDesc=" + getURLEncode(failReasonDesc);
        String callBackParam2 = "merchantCode=" + merchantCode + "&orderId=" + trainorder.getQunarOrdernumber()
                + "&result=" + result + "&failReason=" + failReason + "&failReasonDesc=" + failReasonDesc;
        WriteLog.write(msgLogWriter, r1 + ":FailCallBackParam2------->>>>" + callBackParam2);
        String sign = getSignMethod(callBackParam2);
        try {
            WriteLog.write(msgLogWriter, r1 + ":FailCallBackParam2=====>>>" + sign + key);
            sign = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
        }
        catch (Exception e) {
        }
        callBackParam += "&sign=" + sign;
        WriteLog.write(msgLogWriter, r1 + ":占座失败回调>>>>>>:ElongCallBackUrl:" + ElongCallBackUrl + ":" + callBackParam);
        String resultStr = SendPostandGet.submitPost(ElongCallBackUrl, callBackParam, "UTF-8").toString();
        WriteLog.write(msgLogWriter, r1 + ":>>>--占座失败回调返回-->>>:" + resultStr);

        for (int i = 0; i < 5; i++) {
            if (resultStr.indexOf("成功") > -1 && resultStr.indexOf("200") > -1) {
                return "SUCCESS";
            }
            else {
                resultStr = SendPostandGet.submitPost(ElongCallBackUrl, callBackParam, "UTF-8").toString();
                WriteLog.write(msgLogWriter, r1 + ":>>>--占座失败回调[" + (i + 1) + "]次返回-->>>:" + resultStr);
            }
        }
        return "FALSE";
    }

    /**
     * 获取艺龙先占座模式的失败原因和code值
     * 
     * @param returnmsg
     * @return
     * @time 2016年5月27日 下午12:53:48
     * @author fiend
     */
    private JSONObject getFailJson(String returnmsg) {
        JSONObject jsonObject = new JSONObject();
        if (returnmsg.indexOf("没有余票") > -1 || returnmsg.indexOf("此车次无票") > -1 || returnmsg.indexOf("已无余票") > -1
                || returnmsg.indexOf("没有足够的票") > -1 || returnmsg.indexOf("余票不足") > -1
                || returnmsg.indexOf("非法的席别") > -1) {
            jsonObject.put("failReason", 1);
            jsonObject.put("failReasonDesc", "所购买的车次坐席已无票");
        }
        else if (returnmsg.indexOf("其他订单行") > -1 || returnmsg.indexOf("本次购票行程冲突") > -1 || returnmsg.indexOf("已订") > -1
                || returnmsg.indexOf("已购买") > -1) {
            jsonObject.put("failReason", 2);
            jsonObject.put("failReasonDesc", "身份证件已经实名制购票，不能再次购买同日期同车次的车票");
        }
        else if (returnmsg.indexOf("不符") > -1) {
            jsonObject.put("failReason", 3);
            jsonObject.put("failReasonDesc", "票价和12306不符");
        }
        else if (returnmsg.indexOf("不一致") > -1) {
            jsonObject.put("failReason", 4);
            jsonObject.put("failReasonDesc", "车次数据与12306不一致");
        }
        else if (returnmsg.indexOf("冒用") > -1) {
            jsonObject.put("failReason", 5);
            jsonObject.put("failReasonDesc", returnmsg.contains("_身份信息涉嫌被他人冒用") ? returnmsg.split("_身份信息涉嫌被他人冒用")[0]
                    + "_乘客身份信息被冒用" : returnmsg);
        }
        else if (returnmsg.indexOf("身份") > -1) {
            jsonObject.put("failReason", 6);
            //                添加乘客 未通过身份效验 赵晓丽452230196702183027
            if (returnmsg.contains("添加乘客 未通过身份效验 ")) {
                String passengerString = returnmsg.split("添加乘客 未通过身份效验 ")[1];
                jsonObject.put("failReasonDesc", getName(passengerString) + "_乘客身份信息核验失败");
            }
            else {
                jsonObject.put("failReasonDesc", returnmsg);
            }
        }
        else {
            jsonObject.put("failReason", 0);
            jsonObject.put("failReasonDesc", returnmsg);
        }
        return jsonObject;

    }

    /**
     * 通过姓名证件号截取姓名
     * 
     * @param passengerString
     * @return
     * @time 2016年5月27日 下午12:52:00
     * @author fiend
     */
    private static String getName(String passengerString) {
        try {
            char[] num = passengerString.toCharArray();
            if (num.length > 19) {
                boolean isIdentificationCard = true;
                for (int i = num.length - 18; i < num.length - 1; i++) {
                    if (!Character.isDigit(num[i])) {
                        isIdentificationCard = false;
                    }
                }
                if (isIdentificationCard) {
                    return passengerString.substring(0, passengerString.length() - 18);
                }
            }
            int notNumber = num.length;
            for (int i = num.length - 1; i > 0; i--) {
                if (!Character.isDigit(num[i])) {
                    notNumber = i;
                    break;
                }
            }
            return passengerString.substring(0, notNumber);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return passengerString;
    }

    public static void main(String[] args) {
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(25626L);//根据ID查询交易单号
        new ElongTrainOrderCallBack().trainOrderCallBackSuccess("", "", "", "", trainorder, "");
    }

    /**
     * 回调艺龙占座成功
     * 
     * @param result SUCCESS:成功  FAIL:失败
     * @param trainorder 订单信息
     * @param holdingSeatSuccessTime 占座成功时间 yyyy-MM-dd hh:mm:ss
     * @time 2015年12月11日 上午10:38:59
     * @author Mr.Wang
     */
    public String trainOrderCallBackSuccess(String merchantCode, String key, String ElongCallBackUrl, String result,
            Trainorder trainorder, String holdingSeatSuccessTime) {
        String callBackParam = "";
        String callBackParam2 = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        for (int i = 0; i < trainorder.getPassengers().size(); i++) {
            Trainpassenger trainpassenger = trainorder.getPassengers().get(i);
            for (int j = 0; j < trainpassenger.getTraintickets().size(); j++) {
                Trainticket trainticket = trainpassenger.getTraintickets().get(j);
                JSONObject jsonTrainticket = new JSONObject();
                JSONObject jsonTrainticket2 = new JSONObject();
                jsonTrainticket.put("orderItemId", trainpassenger.getPassengerid());
                jsonTrainticket2.put("orderItemId", trainpassenger.getPassengerid());
                jsonTrainticket.put("seatType", getzwnameByYlseatTypeCode(trainticket.getSeattype()));
                jsonTrainticket2.put("seatType", getzwnameByYlseatTypeCode(trainticket.getSeattype()));
                String cxin = trainticket.getCoach() + "车" + trainticket.getSeatno();
                jsonTrainticket.put("seatNo", getURLEncode(cxin));
                jsonTrainticket2.put("seatNo", cxin);
                jsonTrainticket.put("price", trainticket.getPrice());
                jsonTrainticket2.put("price", trainticket.getPrice());
                jsonTrainticket.put("passengerName", getURLEncode(trainpassenger.getName()));
                jsonTrainticket2.put("passengerName", trainpassenger.getName());
                jsonTrainticket.put("certNo", trainpassenger.getIdnumber());
                jsonTrainticket2.put("certNo", trainpassenger.getIdnumber());
                jsonTrainticket.put("ticketType", getPiaoTypeByticketType(trainticket.getTickettype()));
                jsonTrainticket2.put("ticketType", getPiaoTypeByticketType(trainticket.getTickettype()));
                jsonArray.add(jsonTrainticket);
                jsonArray2.add(jsonTrainticket2);
            }
        }
        jsonObject.put("tickets", jsonArray);
        jsonObject2.put("tickets", jsonArray2);
        jsonObject.put("ticketNo", trainorder.getExtnumber());
        jsonObject2.put("ticketNo", trainorder.getExtnumber());
        jsonObject.put("orderId", trainorder.getQunarOrdernumber());
        jsonObject2.put("orderId", trainorder.getQunarOrdernumber());
        jsonObject.put("holdingSeatSuccessTime", holdingSeatSuccessTime);
        jsonObject2.put("holdingSeatSuccessTime", holdingSeatSuccessTime);
        jsonObject.put("payTimeDeadLine", addDateMinut(holdingSeatSuccessTime, PAYTIME_DEAD));
        jsonObject2.put("payTimeDeadLine", addDateMinut(holdingSeatSuccessTime, PAYTIME_DEAD));

        callBackParam = "merchantCode=" + merchantCode + "&orderId=" + trainorder.getQunarOrdernumber() + "&result="
                + result + "&tickets=" + jsonObject.toString();
        callBackParam2 = "merchantCode=" + merchantCode + "&orderId=" + trainorder.getQunarOrdernumber() + "&result="
                + result + "&tickets=" + jsonObject2.toString();
        WriteLog.write(msgLogWriter, r1 + ":SuccessCallBackParam2------->>>>" + callBackParam2);
        String sign = getSignMethod(callBackParam2);
        try {
            WriteLog.write(msgLogWriter, r1 + ":SuccessCallBackParam2======>>>>" + sign + key);
            sign = ElongHotelInterfaceUtil.MD5(sign + key).toUpperCase();
        }
        catch (Exception e) {
        }
        callBackParam += "&sign=" + sign;
        WriteLog.write(msgLogWriter, r1 + ":占座成功回调>>>>>>:ElongCallBackUrl:" + ElongCallBackUrl + ":" + callBackParam);
        String resultStr = SendPostandGet.submitPost(ElongCallBackUrl, callBackParam, "UTF-8").toString();
        WriteLog.write(msgLogWriter, r1 + ":>>>--占座成功回调返回-->>>:" + resultStr);

        for (int i = 0; i < 5; i++) {
            if (resultStr.indexOf("成功") > -1 && resultStr.indexOf("200") > -1) {
                return "SUCCESS";
            }
            else {
                resultStr = SendPostandGet.submitPost(ElongCallBackUrl, callBackParam, "UTF-8").toString();
                WriteLog.write(msgLogWriter, r1 + ":>>>--占座成功回调[" + (i + 1) + "]次返回-->>>:" + resultStr);
            }
        }
        return "FALSE";

    }

    /**
     * @param oldstring 需要encoder的参数
     * @return 返回encoder后的参数
     * @time 2015年12月10日 上午11:47:46
     * @author Mr.Wang
     */
    private String getURLEncode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    /**
     * 错误信息转换
     * 
     * @param failReason
     * @return
     * @time 2016年1月7日 下午3:42:00
     * @author Administrator
     */
    public String failReasonConversion(String failReason) {

        if (failReason.indexOf("已满") > -1) {
            return "已取纸质车票";
        }
        return failReason;
    }

    /**
    * @param ticketType 票类型  票种类型  0 儿童票 1 成人票 2 学生票
    * @time 2015年12月8日 下午2:03:53
    * @author chendong
    * @return //1:成人票，2:儿童票，3:学生票，4:残军票
    */
    private Integer getPiaoTypeByticketType(int ticketType) {
        Integer piaotype = 1;
        if (1 == ticketType) {
            piaotype = 1;
        }
        else if (2 == ticketType) {
            piaotype = 0;
        }
        else if (3 == ticketType) {
            piaotype = 2;
        }
        return piaotype;
    }
}
