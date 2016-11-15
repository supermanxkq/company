package com.ccservice.b2b2c.atom.servlet.MeiTuanChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.servlet.MeiTuanChange.Method.ChangeTicket;
import com.ccservice.b2b2c.atom.servlet.MeiTuanChange.Method.GetReqTokenByResignId;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class MeiTuanCallBackMethod {
    
    /**
     * 确认改签出票回调参数 
     * @param reqjsonString
     * @return
     */
    public static String getNewJSONString(String reqjsonString) {
        JSONObject MeiTuanJSON = new JSONObject();
        JSONObject callbackJSON = new JSONObject();
        try {
            MeiTuanJSON = JSONObject.parseObject(reqjsonString);
            String resignId = MeiTuanJSON.containsKey("reqtoken") ? MeiTuanJSON.getString("reqtoken") : "";
            boolean success = MeiTuanJSON.containsKey("success") ? MeiTuanJSON.getBoolean("success") : false;
            //    JSONArray newticketcxins = MeiTuanJSON.containsKey("newticketcxins")
            //          ? MeiTuanJSON.getJSONArray("newticketcxins") : new JSONArray();
            //            String ticket_no = "";
            //            for (int i = 0; i < newticketcxins.size(); i++) {
            //                JSONObject ticketObject = newticketcxins.getJSONObject(i);
            //                String old_ticket_no = ticketObject.getString("ticketObject");
            //                ticket_no += "'" + old_ticket_no + "'";
            //            }
            //            JSONArray jsonArray = GetReturn(ticket_no);
            //            double orderAmount = jsonArray.getJSONObject(0).getDouble("C_TCPRICE"); //改签之后订单价格
            //            double C_TCORIGINALPRICE = jsonArray.getJSONObject(0).getDouble("C_TCORIGINALPRICE"); //改签旧的总价格
            //            double C_CHANGEPROCEDURE = jsonArray.getJSONObject(0).getDouble("C_CHANGEPROCEDURE"); //改签产生的手续费
            //            double returnFact = 0;
            //            if (C_TCORIGINALPRICE > orderAmount) {//从高改到低 
            //                returnFact = C_TCORIGINALPRICE - orderAmount - C_CHANGEPROCEDURE;
            //            }
            String sign = GetReqTokenByResignId.getChangeReqToken(GetReqTokenByResignId.Method.CONFIRM_RESIGN, 0,
                    Long.valueOf(resignId), null, null, null, null, null);
            if (success) {
                callbackJSON.put("resignId", resignId);
                callbackJSON.put("reqToken", sign);
                //                callbackJSON.put("returnFact", returnFact);
                callbackJSON.put("success", true);
            }
            else {
                callbackJSON.put("resignId", resignId);
                callbackJSON.put("reqToken", sign);
                callbackJSON.put("success", false);
            }
        }
        catch (Exception e) {
        }
        return callbackJSON.toString();
    }
    
    /**
     * 占座回调参数
     * @param reqjsonString
     * @return
     */
    public static String getZHANZUONewJSONString(String reqjsonString) {
        long r = new Random().nextLong();
        WriteLog.write("meituan美团_确认改签", "r--->" + r + "reqjsonString---->" + reqjsonString);
        JSONObject reJSON = new JSONObject();
        JSONObject meituanJSON = new JSONObject();
        try {
            meituanJSON = JSONObject.parseObject(reqjsonString);
            boolean success = meituanJSON.getBooleanValue("success");
            String orderid = meituanJSON.getString("orderid");
            String resignId = meituanJSON.getString("reqtoken");
            String msg = meituanJSON.getString("msg");
            String help_info = meituanJSON.containsKey("help_info") ? meituanJSON.getString("help_info") : msg;
            if (success) {
                String to_station_name = meituanJSON.getString("to_station_name");
                String from_station_name = meituanJSON.getString("from_station_name");
                String from_station_code = meituanJSON.getString("from_station_code");
                String to_station_code = meituanJSON.getString("to_station_code");
                String arrive_time = meituanJSON.getString("arrive_time");
                String train_date = meituanJSON.getString("train_date");
                String start_time = meituanJSON.getString("start_time");
                String checi = meituanJSON.getString("checi");
                JSONArray newtickets = meituanJSON.getJSONArray("newtickets");
                List<ChangeTicket> changeTickets = new ArrayList<ChangeTicket>();
                reJSON.put("resignId", resignId);
                reJSON.put("orderId", orderid);
                reJSON.put("trainCode", checi);
                reJSON.put("trainDate", train_date);
                reJSON.put("fromStationCode", from_station_code);
                reJSON.put("fromStationName", from_station_name);
                reJSON.put("toStationCode", to_station_code);
                reJSON.put("toStationName", to_station_name);
                reJSON.put("startTime", start_time);
                reJSON.put("arriveTime", arrive_time);
                reJSON.put("order12306Serial", "");
                reJSON.put("success", true);
                JSONArray meituanArray = new JSONArray();
                String ticket_no = "";
                for (int i = 0; i < newtickets.size(); i++) {
                    JSONObject newticket = newtickets.getJSONObject(i);
                    String old_ticket_no = newticket.getString("old_ticket_no");
                    ticket_no += "'" + old_ticket_no + "',";
                }
                ticket_no = ticket_no.substring(0, ticket_no.length()-1);
                WriteLog.write("meituan美团_确认改签", "r--->" + r + "ticket_no---->" + ticket_no);
                JSONArray jsonArray = GetReturn(ticket_no);
                WriteLog.write("meituan美团_确认改签", "r--->" + r + "查表jsonArray---->" + jsonArray.toString());
                for (int i = 0; i < newtickets.size(); i++) {
                    JSONObject newticket = newtickets.getJSONObject(i);
                    String zwname = newticket.getString("zwname");
                    String new_ticket_no = newticket.getString("new_ticket_no");
                    String price = newticket.getString("price");
                    String piaotype = newticket.getString("piaotype");
                    String zwcode = newticket.getString("zwcode");
                    String passportseno = newticket.getString("passportseno");
                    String old_ticket_no = newticket.getString("old_ticket_no");
                    String cxin = newticket.getString("cxin");
                    for (int j = 0; j < jsonArray.size(); j++) {
                        ChangeTicket changeTicket = new ChangeTicket();
                        String C_TICKETNO = jsonArray.getJSONObject(j).getString("C_TICKETNO");
                        String passengerName = jsonArray.getJSONObject(j).getString("C_NAME");
                        long ticketId = jsonArray.getJSONObject(j).getLong("C_INTERFACETICKETNO");
                        if (C_TICKETNO != null && old_ticket_no.equals(C_TICKETNO)) {
                            JSONObject meituanTicket = new JSONObject();
                            meituanTicket.put("ticketId", ticketId);
                            changeTicket.setTicketId(ticketId);
                            meituanTicket.put("passengerName", passengerName);
                            changeTicket.setPassengerName(passengerName);
                            meituanTicket.put("certificateNo", passportseno);
                            changeTicket.setCertificateNo(passportseno);
                            meituanTicket.put("ticketPrice", Double.valueOf(price));
                            changeTicket.setTicketPrice(price);
                            meituanTicket.put("coachNo", cxin.split(",")[0]);
                            changeTicket.setCoachNo(cxin.split(",")[0]);
                            meituanTicket.put("seatNo", cxin.split(",")[1]);
                            changeTicket.setSeatNo(cxin.split(",")[1]);
                            meituanTicket.put("ticketNo", new_ticket_no);
                            changeTicket.setTicketNo(new_ticket_no);
                            meituanTicket.put("seatType", PublicMTMethod.gethtname(zwcode, zwname, cxin.split(",")[1]));
                            changeTicket.setSeatType(PublicMTMethod.gethtname(zwcode, zwname, cxin.split(",")[1]));
                            meituanTicket.put("ticketType", PublicMTMethod.getMTpiaotypecode(piaotype));
                            changeTicket.setTicketType(PublicMTMethod.getMTpiaotypecode(piaotype));
                            
                            meituanArray.add(meituanTicket);
                            changeTickets.add(changeTicket);
                        }
                    }
                }
                double orderAmount = jsonArray.getJSONObject(0).getDouble("C_TCPRICE"); //改签之后订单价格
                reJSON.put("orderAmount", orderAmount);
                reJSON.put("tickets", meituanArray);
                double C_TCORIGINALPRICE = jsonArray.getJSONObject(0).getDouble("C_TCORIGINALPRICE"); //改签旧的总价格
                double C_CHANGEPROCEDURE = jsonArray.getJSONObject(0).getDouble("C_CHANGEPROCEDURE"); //改签产生的手续费
                double returnFact = 0;
                if (C_TCORIGINALPRICE > orderAmount) {//从高改到低 
                    returnFact = C_TCORIGINALPRICE - orderAmount - C_CHANGEPROCEDURE;
                }
                reJSON.put("returnFact", returnFact);
                String sign = GetReqTokenByResignId.getChangeReqToken(GetReqTokenByResignId.Method.RESIGN_TICKET,
                        Long.valueOf(orderid), Long.valueOf(resignId), checi, from_station_code, to_station_code,
                        train_date, changeTickets);
                WriteLog.write("meituan美团_确认改签", "r--->" + r + "sign---->" + sign);
                reJSON.put("reqToken", sign);
                WriteLog.write("meituan美团_确认改签", "r--->" + r + "reJSON---->" + reJSON.toString());
            }
            else {
                JSONObject reParam = GetReturnPara(resignId);
                WriteLog.write("meituan美团_确认改签", "r-失败-->" + r + "reJSON---->" + reParam.toString());

                String fromStationName=reParam.getString("fromStationName");
                String toStationName = reParam.getString("toStationName");
                String fronStationCode = Train12306StationInfoUtil.getSZMByName(fromStationName);
                String toStationCode =Train12306StationInfoUtil.getSZMByName(toStationName);
                String  trainCode =reParam.getString("trainno");
                String  trainDate =reParam.getString("starttime");
                String  titicketids = reParam.getString("titicketids");
                String []titicketid=titicketids.split("@");
                long   trainorderid = reParam.getLong("orderid");
                reJSON.put("resignId", resignId);
                reJSON.put("orderId", orderid);
                reJSON.put("trainCode", trainCode);
                reJSON.put("trainDate", trainDate);
                reJSON.put("fromStationCode", fronStationCode);
                reJSON.put("fromStationName", fromStationName);
                reJSON.put("toStationCode", toStationCode);
                reJSON.put("toStationName", toStationName);
                reJSON.put("success", false);
                reJSON.put("seatHoldFailReason", help_info);
                JSONArray meituanArray = new JSONArray();
                List<ChangeTicket> changeTickets = new ArrayList<ChangeTicket>();
                Trainorder trainorder=Server.getInstance().getTrainService().findTrainorder(trainorderid);
                if(trainorder.getId()>0){
                  List<Trainpassenger> trainpassengers =trainorder.getPassengers();
                  for (int i = 0; i < trainpassengers.size(); i++) {
                      Trainpassenger trainpassenger = trainpassengers.get(i);
                      String name =trainpassenger.getName();
                      String idnum = trainpassenger.getIdnumber();
                      Trainticket trainticket=trainpassenger.getTraintickets().get(0);
                      for (int j = 0; j < titicketid.length; j++) {
                        String idstring = titicketid[j];
                        if(trainticket.getId()>0&&idstring.equals(trainticket.getId()+"")){
                            System.out.println(name);
                            long ticketId = Long.valueOf(trainticket.getInterfaceticketno());
                            ChangeTicket changeTicket = new ChangeTicket();
                            JSONObject meituanTicket = new JSONObject();
                            meituanTicket.put("ticketId", ticketId);
                            changeTicket.setTicketId(ticketId);
                            meituanTicket.put("passengerName", name);
                            changeTicket.setPassengerName(name);
                            meituanTicket.put("certificateNo", idnum);
                            changeTicket.setCertificateNo(idnum);
                            meituanArray.add(meituanTicket);
                            changeTickets.add(changeTicket);
                            continue;
                        }
                    }
                      
                  }
                }
                WriteLog.write("meituan美团_确认改签", "r-失败-->" + r + "meituanArray---->" + meituanArray.toString());
                reJSON.put("tickets", meituanArray);
                String sign = GetReqTokenByResignId.getChangeReqToken(GetReqTokenByResignId.Method.RESIGN_TICKET,
                        Long.valueOf(orderid), Long.valueOf(resignId), trainCode, fronStationCode, toStationCode,
                        trainDate, changeTickets);
                WriteLog.write("meituan美团_确认改签", "r-失败-->" + r + "sign---->" + sign);
                reJSON.put("reqToken", sign);
                WriteLog.write("meituan美团_确认改签", "r-失败-->" + r + "reJSON---->" +reJSON.toString());
            }

        }
        catch (Exception e) {
        }
        return reJSON.toString();
    }

    @SuppressWarnings("rawtypes")
    public static JSONObject GetReturnPara(String reqToken) {
        JSONObject jsonObject = new JSONObject();
        if(reqToken!=null&&!"".equals(reqToken)){
            String sql = "exec SelectTrainOrderChangeByToken @reqtoken ='" + reqToken + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list != null && list.size() > 0) {
                Map map = (Map) list.get(0);
                String C_STATION_NAME = map.get("C_STATION_NAME") != null ? map.get("C_STATION_NAME").toString() : "";
                String C_TCTICKETID = map.get("C_TCTICKETID") != null ? map.get("C_TCTICKETID").toString() : "";
                String C_ORDERID = map.get("C_ORDERID") != null ? map.get("C_ORDERID").toString() : "";
                String C_TCTRAINNO = map.get("C_TCTRAINNO") != null ? map.get("C_TCTRAINNO").toString() : "";
                String C_TCDEPARTTIME = map.get("C_TCDEPARTTIME") != null ? map.get("C_TCDEPARTTIME").toString() : "";
                if ("".equals(C_STATION_NAME) || "".equals(C_TCTICKETID) || "".equals(C_ORDERID) || "".equals(C_TCTRAINNO)
                        || "".equals(C_TCDEPARTTIME)) {
                    return new JSONObject();
                }
                String fromStationName = C_STATION_NAME.split("-")[0];
                String toStationName = C_STATION_NAME.split("-")[1];
                jsonObject.put("fromStationName", fromStationName.trim());
                jsonObject.put("toStationName", toStationName.trim());
                jsonObject.put("titicketids", C_TCTICKETID);//38947977@38947976@
                jsonObject.put("orderid", C_ORDERID);
                jsonObject.put("trainno", C_TCTRAINNO);
                jsonObject.put("starttime", C_TCDEPARTTIME.split(" ")[0]);
            } 
        }
        return jsonObject;

    }

    @SuppressWarnings("rawtypes")
    public static JSONArray GetReturn(String ticket_no) {
        JSONArray jsonArray = new JSONArray();
        String sql = " select p.C_NAME,t.C_INTERFACETICKETNO,C_TICKETNO,c.C_TCORIGINALPRICE,"
                + "c.C_TCPRICE,c.C_CHANGEPROCEDURE,t.C_PRICE,t.C_TCNEWPRICE " + "from T_TRAINTICKET t with (nolock) "
                + "join T_TRAINPASSENGER p with (nolock)" + " on t.C_TRAINPID = p.ID"
                + " join T_TRAINORDERCHANGE c with (nolock)" + " on t.C_CHANGEID=c.ID where  C_TICKETNO in(" + ticket_no
                + ")";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                Map map = (Map) list.get(i);
                String C_NAME = map.get("C_NAME") != null ? map.get("C_NAME").toString() : "";
                String C_INTERFACETICKETNO = map.get("C_INTERFACETICKETNO") != null
                        ? map.get("C_INTERFACETICKETNO").toString() : "";
                String C_TICKETNO = map.get("C_TICKETNO") != null ? map.get("C_TICKETNO").toString() : "";
                String C_CHANGEPROCEDURE = map.get("C_CHANGEPROCEDURE") != null
                        ? map.get("C_CHANGEPROCEDURE").toString() : "";
                String C_TCORIGINALPRICE = map.get("C_TCORIGINALPRICE") != null
                        ? map.get("C_TCORIGINALPRICE").toString() : "";
                String C_TCPRICE = map.get("C_TCPRICE") != null ? map.get("C_TCPRICE").toString() : "";
                String C_PRICE = map.get("C_PRICE") != null ? map.get("C_PRICE").toString() : "";
                String C_TCNEWPRICE = map.get("C_TCNEWPRICE") != null ? map.get("C_TCNEWPRICE").toString() : "";
                jsonObject.put("C_NAME", C_NAME);
                jsonObject.put("C_INTERFACETICKETNO", C_INTERFACETICKETNO);
                jsonObject.put("C_TICKETNO", C_TICKETNO);
                jsonObject.put("C_TCORIGINALPRICE", C_TCORIGINALPRICE);
                jsonObject.put("C_CHANGEPROCEDURE", C_CHANGEPROCEDURE);
                jsonObject.put("C_TCPRICE", C_TCPRICE);
                jsonObject.put("C_PRICE", C_PRICE);
                jsonObject.put("C_TCNEWPRICE", C_TCNEWPRICE);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    public static String getOld_zwcode(String oldSeatType) {
        if ("商务座".equals(oldSeatType)) {
            return "9";
        }
        else if ("特等座".equals(oldSeatType)) {
            return "P";
        }
        else if ("一等座".equals(oldSeatType)) {
            return "M";
        }
        else if ("二等座".equals(oldSeatType)) {
            return "O";
        }
        else if ("高级软卧".equals(oldSeatType)) {
            return "6";
        }
        else if ("软卧".equals(oldSeatType)) {
            return "4";
        }
        else if ("硬卧".equals(oldSeatType)) {
            return "3";
        }
        else if ("软座".equals(oldSeatType)) {
            return "2";
        }
        else if ("硬座".equals(oldSeatType)) {
            return "1";
        }
        else if ("一等软座".equals(oldSeatType)) {
            return "7";
        }
        else if ("二等软座".equals(oldSeatType)) {
            return "8";
        }
        else if ("动卧".equals(oldSeatType)) {
            return "F";
        }
        else if ("高级动卧".equals(oldSeatType)) {
            return "A";
        }
        return "";
    }
    
}
