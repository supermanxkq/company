package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.Util_Insurance.henghao.SinosigHengHaoMethod;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Insure.TrainInsureMethod;
import com.ccservice.b2b2c.atom.servlet.chongdong.WormholeTradeCallBack;
import com.ccservice.b2b2c.atom.servlet.chongdong.WormholeUtil;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 订单保险接口（暂时YDX使用）
 * 
 * @time 2016年7月19日 下午2:50:50
 * @author fiend
 */
public class TongChengTrainInsure extends TrainInsureMethod {

    private final String logName = "订单投保接口_tongChengTrainInsure";

    private final String logNameException = "订单投保接口_tongChengTrainInsure_Exception";

    public String insure(JSONObject json) {
        int r1 = (int) (Math.random() * 100000);
        WriteLog.write(logName, r1 + "--->" + json.toString());
        JSONObject resultObject = new JSONObject();
        String orderNumber = json.containsKey("OrderNumber") ? json.getString("OrderNumber") : "";
        String interfaceOrderNumber = json.containsKey("InterfaceOrderNumber") ? json.getString("InterfaceOrderNumber")
                : "";
        JSONArray ticketArray = json.containsKey("Tickets") ? json.getJSONArray("Tickets") : new JSONArray();
        if ("".equals(orderNumber) || "".equals(interfaceOrderNumber) || ticketArray.size() == 0) {
            resultObject.put("success", false);
            resultObject.put("msg", "业务参数缺失");
        }
        else {
            Trainorder trainorder = findTrainorderByOrderNumberAndInterfaceOrderNumber(orderNumber,
                    interfaceOrderNumber);
            if (trainorder.getId() == 0) {
                resultObject.put("success", false);
                resultObject.put("msg", "无此订单");
            }
            else {
                resultObject = sendInsure(ticketArray, trainorder, r1);
                //TODO 扣款
                try {
                    if (resultObject.containsKey("success") && resultObject.getBooleanValue("success")) {
                        if (WormholeUtil.checkTrainOrderIsWormhole(trainorder.getQunarOrdernumber())) {
                            WormholeTradeCallBack wormholeTradeCallBack = new WormholeTradeCallBack();
                            String resultString = wormholeTradeCallBack.trade(trainorder.getQunarOrdernumber(), 1);
                            String sql = "EXEC [dbo].[sp_T_TRAINTICKET_queryid] @qunarordernumber='"
                                    + trainorder.getQunarOrdernumber() + "'";
                            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                            for (int i = 0; i < list.size(); i++) {
                                Map map = (Map) list.get(i);
                                String ID = map.get("ID").toString();
                                if ("SUCCESS".equals(resultString)) {
                                    String sql_1 = "EXEC [dbo].[sp_T_TRAINTICKET_updateWormholeCallBackIsSuccess] @id="
                                            + ID + ",@WormholeCallBackIsSuccess=" + 1;
                                    Server.getInstance().getSystemService().findMapResultBySql(sql_1, null);
                                }
                                else {
                                    String sql_2 = "EXEC [dbo].[sp_T_TRAINTICKET_updateWormholeCallBackIsSuccess] @id="
                                            + ID + ",@WormholeCallBackIsSuccess=" + 2;
                                    Server.getInstance().getSystemService().findMapResultBySql(sql_2, null);
                                }
                            }
                            if ("SUCCESS".equals(resultString)) {
                                resultObject.put("payWormholeSuccess", true);
                            }
                            else {
                                resultObject.put("payWormholeSuccess", false);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    WriteLog.write(logNameException, r1 + "");
                    ExceptionUtil.writelogByException(logNameException, e);
                    resultObject.put("payWormholeSuccess", false);
                }
            }
        }
        WriteLog.write(logName, r1 + "--->" + resultObject.toString());
        return resultObject.toString();
    }

    /**
     *  投保
     * 
     * @param ticketArray
     * @param trainorder
     * @return
     * @time 2016年7月19日 下午2:51:26
     * @author fiend
     */
    public JSONObject sendInsure(JSONArray ticketArray, Trainorder trainorder, int r1) {
        JSONObject resultObject = new JSONObject();
        JSONArray resultArray = new JSONArray();
        String VisitorName = PropertyUtil.getValue("InsureVisitorName", "Train.insure.properties");
        String Password = PropertyUtil.getValue("InsurePassword", "Train.insure.properties");
        String insProductNo = PropertyUtil.getValue("InsureinsProductNo", "Train.insure.properties");
        //保险产品代码(我方提供)
        String printNo = trainorder.getId() + ""; //保险凭证号
        List<Trainpassenger> trainpassengers = trainorder.getPassengers();
        for (int i = 0; i < trainpassengers.size(); i++) {
            Trainpassenger trainpassenger = trainpassengers.get(i);
            Trainticket ticket = trainpassenger.getTraintickets().get(0);
            for (int j = 0; j < ticketArray.size(); j++) {
                JSONObject ticketObject = ticketArray.getJSONObject(j);
                ticketObject.put("success", false);
                ticketObject.put("msg", "未知原因，请联系客服");
                if (ticketObject.getString("TicketNo").equals(ticket.getTicketno())) {
                    if (ticket.getRealinsureno() != null && "".equals(ticket.getRealinsureno())) {
                        ticketObject.put("msg", "已经投保，请勿二次投保");
                        ticketObject.put("policyNo", ticket.getRealinsureno());
                    }
                    else {
                        try {
                            int cardType = trainpassenger.getIdtype(); //证件类型   
                            String flightDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm").parse(ticket.getDeparttime()));
                            String flightNumber = ticket.getTrainno();//车次
                            String serialNo = UUID.randomUUID().toString();//交易流水号
                            String contractName = trainpassenger.getName(); //保险人姓名    
                            int contractType = 1;
                            String cardNo = trainpassenger.getIdnumber().toUpperCase(); //证件号
                            String gender = "M";
                            String birthday = "";
                            if (birthday == null || "".equals(birthday)) {
                                if (18 == cardNo.length()) {
                                    int i1 = Integer.valueOf(String.valueOf(cardNo.charAt(16)));
                                    int i2 = (i1 - (2 * (i1 / 2))) % 2;
                                    if (i2 == 0) {
                                        gender = "F";
                                    }
                                    birthday = cardNo.substring(6, 10) + "-" + cardNo.substring(10, 12) + "-"
                                            + cardNo.substring(12, 14);
                                }
                                else {
                                    birthday = "1984-06-01";
                                    WriteLog.write(logName, r1 + "-->生成保险生日日期-->护照转换保险生日--->" + trainorder.getId());
                                }
                            }
                            String phone = trainorder.getContacttel();

                            if (cardType != 1 && cardType != 3) { // 若果不是二代身份证 和 护照 则证件类型则是其他
                                cardType = 9;
                            }
                            WriteLog.write(logName, r1 + "-->VisitorName:" + VisitorName + "Password:" + Password
                                    + "insProductNo:" + insProductNo + "printNo:" + printNo + "flightDate:"
                                    + flightDate + "flightNumber:" + flightNumber + "serialNo:" + serialNo
                                    + "contractName:" + contractName + "contractType：" + contractType + "性别：" + gender
                                    + "证件类型:" + cardType + "证件号：" + cardNo + "出生日期:" + birthday + "电话号:" + phone);

                            /*********************************创建保险订单***********************************************************/
                            Insurorder insurorder = new Insurorder();
                            insurorder.setLiushuino(serialNo);
                            insurorder.setAgentid(trainorder.getAgentid());
                            insurorder.setComputerid(trainorder.getAgentid());
                            insurorder.setUserid(1);
                            insurorder.setStatus(Long.parseLong("1"));
                            insurorder.setInsuruserid(trainorder.getId() + "");//保存被保人的id
                            insurorder.setTime(new Timestamp(new Date().getTime()));//获得当前时间
                            insurorder.setPaystatus(1L);
                            insurorder.setPaymethod(trainorder.getPaymethod());
                            //添加保险数量
                            insurorder.setInsurantcount(1);
                            insurorder.setInsurmoney(20.0);
                            //保存总价钱
                            insurorder.setTotalmoney(20.0);
                            insurorder.setBegintime(new Timestamp(System.currentTimeMillis()));
                            insurorder.setEndtime(new Timestamp(System.currentTimeMillis()));
                            insurorder = Server.getInstance().getAirService().createInsurorder(insurorder);
                            WriteLog.write(logName, r1 + "-->" + insurorder.getId());
                            /**************************创建保险**************************************************/
                            Insuruser insuruser = new Insuruser();
                            insuruser.setAgentid(trainorder.getAgentid());
                            insuruser.setCodetype(Long.valueOf(cardType));
                            insuruser.setCode(cardNo);
                            insuruser.setFlytime(sunshineTimestamp(ticket.getDeparttime(), "yyyy-MM-dd HH:mm"));
                            insuruser.setOrdernum(trainorder.getOrdernumber());
                            insuruser.setOrderid(insurorder.getId());
                            insuruser.setBegintime(new Timestamp(System.currentTimeMillis()));
                            insuruser.setBirthday(formatStringToTime(birthday, "yyyy-MM-dd"));
                            insuruser.setName(contractName);
                            insuruser.setFlyno(flightNumber);
                            insuruser.setMobile(phone);
                            insuruser.setEmail("baoxian@clbao.com");
                            insuruser.setInsurstatus(0);
                            insuruser.setPaystatus(1);
                            insuruser = Server.getInstance().getAirService().createInsuruser(insuruser);

                            /*******************************************************************************************/
                            JSONObject res = new SinosigHengHaoMethod(VisitorName, Password).postPolicyOrder(
                                    insProductNo, printNo, flightDate, flightNumber, serialNo, contractName,
                                    contractType, gender, cardType, cardNo, birthday, phone);
                            WriteLog.write(logName, r1 + "-->返回的消息" + res.toString());
                            int resultId = res.getIntValue("resultId");
                            String print = res.getString("printNo"); //保险凭证号
                            String policyNo = res.getString("policyNo");
                            String resultErrDesc = res.getString("resultErrDesc");
                            if (resultId == 0) {//投保成功
                                String sql = "update T_INSURUSER set C_POLICYNO ='" + policyNo
                                        + "' ,C_INSURSTATUS= 1,C_REMARK='" + resultErrDesc + "' where ID ="
                                        + insuruser.getId();
                                Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
                            }
                            else {
                                String sql = "update T_INSURUSER set C_INSURSTATUS= 2,C_REMARK='" + resultErrDesc
                                        + "' where ID =" + insuruser.getId();
                                Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
                            }
                            if (policyNo != null && !"".equals(policyNo)) {
                                ticket.setRealinsureno(policyNo);
                                ticket.setInsurenum(1);
                                ticket.setInsurinprice(20f);
                                ticket.setInsurorigprice(20f);
                                ticket.setInsurprice(20f);
                                Server.getInstance().getTrainService().updateTrainticket(ticket);
                                WriteLog.write(logName,
                                        r1 + "-->" + trainorder.getId() + ":" + trainpassenger.getName() + ":"
                                                + insuruser.getPolicyno());
                                createTrainorderrc(1, trainorder.getId(), "投保接口：" + contractName
                                        + "<span style='color:red;'>" + resultErrDesc + "</span>保单号：" + policyNo
                                        + "保单凭证：" + print, "投保接口", 1, 0);
                                ticketObject.put("success", true);
                                ticketObject.put("policyNo", policyNo);
                                ticketObject.put("msg", "投保成功");
                                pay(trainorder, 20f);
                            }
                            else {
                                createTrainorderrc(1, trainorder.getId(), "投保接口:" + contractName
                                        + "<span style='color:red;'>投保失败</span>。投保接口返回：" + resultErrDesc + "", "投保接口",
                                        1, 0);
                                ticketObject.put("success", true);
                                ticketObject.put("msg", resultErrDesc);
                            }
                        }
                        catch (Exception e) {
                            WriteLog.write(logNameException, r1 + "");
                            ExceptionUtil.writelogByException(logNameException, e);
                        }
                    }
                }
                resultArray.add(ticketObject);
            }
        }
        resultObject.put("Tickets", resultArray);
        resultObject.put("success", true);
        return resultObject;
    }

    /**
     * 获取一个可以投保的发车时间  如果实际时间不满足 就+1个小时
     * @param traindate
     * @param format
     * @return
     * @author fiend
     */
    private Timestamp sunshineTimestamp(String traindate, String format) {
        try {
            if (isNeedFalsityTrainDate(traindate, format)) {
                return falsityTrainDate(traindate, format);
            }
            else {
                return formatStringToTime(traindate, format);
            }
        }
        catch (ParseException e) {
            return formatStringToTime(traindate, format);
        }
    }

    /**
     * 发车时间-系统当前时间 是否大于63分钟
     * @param traindate
     * @param format
     * @return
     * @author fiend
     */
    private boolean isNeedFalsityTrainDate(String traindate, String format) {
        try {
            SimpleDateFormat simplefromat = new SimpleDateFormat(format);
            return (simplefromat.parse(traindate).getTime() - System.currentTimeMillis()) < (63 * 60 * 1000);
        }
        catch (ParseException e) {
            return false;
        }
    }

    /**
     * 获取虚假发车时间 原发车时间+1小时
     * @param traindate
     * @param format
     * @return
     * @author fiend
     * @throws ParseException 
     */
    private Timestamp falsityTrainDate(String traindate, String format) throws ParseException {
        SimpleDateFormat simplefromat = new SimpleDateFormat(format);
        return new Timestamp(simplefromat.parse(traindate).getTime() + (60 * 60 * 1000));
    }

    public Timestamp formatStringToTime(String date, String format) {
        try {
            SimpleDateFormat simplefromat = new SimpleDateFormat(format);
            return new Timestamp(simplefromat.parse(date).getTime());

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
