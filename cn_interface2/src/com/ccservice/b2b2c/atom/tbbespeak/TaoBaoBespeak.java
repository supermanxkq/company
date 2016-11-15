package com.ccservice.b2b2c.atom.tbbespeak;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;







import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 淘宝订单发起抢票需求
 * 2016-08-31 13:50
 * @author guozhengju
 *
 */
public class TaoBaoBespeak {

       SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");

       SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

       SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 淘宝订单发起抢票
     * @param orderNum
     * @return
     * guozhengju
     * 2016-08-31
     */
    public  void sendBespeak(JSONObject json, long orderid) {
        WriteLog.write("淘宝订单发抢票_JSON_original", "orderid=" + orderid + ";json=" + json);
        String extend_paramsStr = json.getString("extend_params");
        JSONObject extend_params = JSONObject.parseObject(extend_paramsStr);
        json.put("extend_params", extend_params);
        WriteLog.write("淘宝订单发抢票_JSON_update", "orderid=" + orderid + ";json=" + json);
        JSONObject jsonRes = new JSONObject();
        String partnerid = "tb_bespeak";
        String key = "NZ4rw0cs8JuK0sug7jO22skEaiD1tdEI";
        String method = "qiang_piao_order";
        JSONObject extendsParam = json.getJSONObject("extend_params");
        String train_codes = extendsParam.getString("grab_train_nubmers");
        String seat_type = extendsParam.getString("grab_seats");
        String qorder_end_time = "";
        try {
            qorder_end_time = sdf2.format(sdf3.parse(extendsParam.getString("grab_end_time")));
        }
        catch (ParseException e1) {
            e1.printStackTrace();
        }
        jsonRes.put("partnerid", partnerid);
        jsonRes.put("method", method);
        jsonRes.put("AgentId", "112");
        jsonRes.put("interfacetype", "13");
        jsonRes.put("trainOldorderid", orderid);
        String reqtime = getCurrentTime(1);
        jsonRes.put("reqtime", reqtime);
        String sign = "";
        try {
            sign = MD5(partnerid + method + reqtime + MD5(key).toLowerCase()).toLowerCase();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        jsonRes.put("sign", sign);
        jsonRes.put("qorderid", json.getString("main_order_id"));
        jsonRes.put("callback_url", "");//-------------------------
        JSONObject info1 = JSONObject.parseObject(json.getString("tickets"));
        JSONArray tickets = info1.getJSONArray("to_agent_ticket_info");
        String from_station_name = "";
        String to_station_name = "";
        String start_date = "";
        JSONArray passengers = new JSONArray();
        for (int i = 0; i < tickets.size(); i++) {
            JSONObject object = tickets.getJSONObject(i);
            JSONObject jsonPas = new JSONObject();
            jsonPas.put("passengerid", object.get("sub_order_id"));
            jsonPas.put("passengersename", object.get("passenger_name"));
            jsonPas.put("passportseno", object.get("certificate_num"));
            jsonPas.put("passporttypeseid", findPassportTypeId(object.get("certificate_type").toString(), 1));
            jsonPas.put("passporttypeidname", findPassportTypeId(object.get("certificate_type").toString(), 2));
            jsonPas.put("piaotype", findPassengerTypeId(object.get("passenger_type").toString(), 1));
            jsonPas.put("piaotypename", findPassengerTypeId(object.get("passenger_type").toString(), 2));
            //----------------新加学生票逻辑--------------------
            if("2".equals(object.get("passenger_type").toString())){
                String student_info=object.get("student_info").toString();
                JSONObject studentInfo=JSONObject.parseObject(student_info);
                jsonPas.put("school_name", studentInfo.get("school_name"));//   string  学校名称
                jsonPas.put("enter_year", studentInfo.get("entrance_year"));// string  入学年份：yyyy
                jsonPas.put("province_name", studentInfo.get("school_province"));//   string  省份名称  
                jsonPas.put("preference_from_station_name", studentInfo.get("from_city"));// string  优惠区间起始地名称【选填】  
                jsonPas.put("preference_to_station_name", studentInfo.get("to_city"));// string  优惠区间到达地名称【选填】
                jsonPas.put("school_system", studentInfo.get("eductional_system"));//   string  学制  
                jsonPas.put("student_no", studentInfo.get("student_no"));// string  学号
            }
            
            passengers.add(jsonPas);
            if (i == 0) {
                from_station_name = object.getString("from_station");
                to_station_name = object.getString("to_station");
                try {
                    start_date = sdf1.format(sdf2.parse(object.getString("from_time")));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        boolean hasseat = true;//是否需要无座  需要为1  不需要为0
        if (seat_type.contains("17")) {
            hasseat = false;
        }
        String qorder_start_time = sdf2.format(new Date());
        jsonRes.put("train_type", findTrainTypes(train_codes));
        jsonRes.put("from_station_code", "");
        jsonRes.put("from_station_name", from_station_name);
        jsonRes.put("to_station_code", "");
        jsonRes.put("to_station_name", to_station_name);
        jsonRes.put("start_date", start_date);
        jsonRes.put("start_begin_time", "00:00");
        jsonRes.put("start_end_time", "24:00");
        jsonRes.put("train_codes", findTrainCodes(train_codes));
        jsonRes.put("seat_type", findSeatTypes(seat_type));
        jsonRes.put("qorder_type", "100");
        jsonRes.put("qorder_start_time", qorder_start_time);
        jsonRes.put("qorder_end_time", qorder_end_time);
        jsonRes.put("max_price", 9999);
        jsonRes.put("hasseat", hasseat);
        jsonRes.put("qpriority", "0");
        jsonRes.put("passengers", passengers);
                String url = "http://121.41.35.117:19044/trainorder_bespeak/TrainorderBespeakServelt";
//        String url = PropertyUtil.getValue("TBTrainorderBespeakServlet_URL", "Train.properties");
        WriteLog.write("淘宝订单发抢票Request",
                json.getString("main_order_id") + "-->" + url + "?jsonStr=" + jsonRes.toString());
        String paramContent = "";
        try {
            paramContent = "jsonStr=" + URLEncoder.encode(jsonRes.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = SendPostandGet.submitPost(url, paramContent, "UTF-8").toString();
        WriteLog.write("淘宝订单发抢票Request", json.getString("main_order_id") + ";结果:" + result);

    }
//    public static void main(String[] args) {
//      String paramContent = "{\"mailing\":false,\"service_price\":0,\"transport_price\":0,\"total_price\":100,\"order_status\":1,\"paper_type\":0,\"ttp_order_id\":199170009433,\"is_success\":true,\"request_id\":\"1475k2oh86w2g\",\"paper_backup\":0,\"address\":\"no address\",\"main_order_id\":2606278460563334,\"paper_low_seat_count\":0,\"telephone\":\"13521547189\",\"tickets\":{\"to_agent_ticket_info\":[{\"birthday\":\"1991-02-10\",\"train_num\":\"6064\",\"tag\":1,\"certificate_num\":\"342423199102106218\",\"passenger_type\":0,\"to_station\":\"阳平关\",\"insurance_price\":0,\"certificate_type\":\"0\",\"insurance_unit_price\":0,\"ticket_price\":100,\"seat\":1,\"from_time\":\"2016-12-08 08:48:00\",\"sub_order_id\":\"251680013\",\"from_station\":\"燕子砭\",\"passenger_name\":\"黄继猛\",\"to_time\":\"2016-12-08 09:04:00\"}]},\"extend_params\":\"{\\\"grab_end_time\\\":\\\"2016-12-08 08:00:00\\\",\\\"grab_seats\\\":\\\"1\\\",\\\"grab_train_nubmers\\\":\\\"6064\\\"}\",\"latest_issue_time\":\"2016-12-08 08:00:00\",\"order_type\":3}";
//      sendBespeak(JSONObject.parseObject(paramContent),123);
////      String result = SendPostandGet.submitPost(url, paramContent, "UTF-8").toString();
////     System.out.println(result);
//    }

    /**
     * 获取车次的简码
     * @param trainCodes
     * @return
     */
    public String findTrainTypes(String trainCodes) {
        String result = "";
        String[] codes = trainCodes.split("[|]");
        Set<String> codeSet = new HashSet<String>();
        //        List<String> codeSet = new ArrayList<String>();
        for (int i = 0; i < codes.length; i++) {
            String start = codes[i].substring(0, 1);
            if ("1".equals(start) || "2".equals(start) || "3".equals(start) || "4".equals(start) || "5".equals(start)
                    || "6".equals(start) || "7".equals(start) || "8".equals(start) || "9".equals(start)) {
                codeSet.add("Q");
            }
            else {
                codeSet.add(start);
            }
        }
        for (String name : codeSet) {
            result += name + ",";
        }
        if (result.contains(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 获取车次抢票类型
     * @param trainCode
     * @return
     */
    public String findTrainCodes(String trainCode) {
        String result = "";
        if (trainCode.contains("/")) {
            trainCode = trainCode.replaceAll("[/]", "|");
        }
        if (trainCode.contains("|")) {
            result = trainCode.replaceAll("[|]", ",");
        }
        else {
            result = trainCode;
        }
        return result;
    }


    /**
     * 获取抢票需要的坐席类型
     * @param seattype
     * @return
     */
    public String findSeatTypes(String seattype) {
        String result = "";
        String[] seats = seattype.split("[|]");
        for (int i = 0; i < seats.length; i++) {
            String str = "";
            int seat = Integer.parseInt(seats[i]);
            switch (seat) {
            case 1:
                str = "硬座";
                break;
            case 2:
                str = "硬卧";
                break;
            case 3:
                str = "硬卧";
                break;
            case 4:
                str = "硬卧";
                break;
            case 5:
                str = "软座";
                break;
            case 6:
                str = "软卧";
                break;
            case 7:
                str = "软卧";
                break;
            case 8:
                str = "软卧";
                break;
            case 9:
                str = "商务座";
                break;
            case 10:
                str = "一等包座";
                break;
            case 11:
                str = "观光座";
                break;
            case 12:
                str = "特等座";
                break;
            case 13:
                str = "一等座";
                break;
            case 14:
                str = "二等座";
                break;
            case 15:
                str = "高级软卧";
                break;
            case 16:
                str = "高级软卧";
                break;
            case 17:
                str = "无座";
                break;
            default:
                break;
            }
            result += str + ",";
        }
        if (result.contains(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 获取身份类型int
     * @param type
     * @return
     */
    public String findPassportTypeId(String passType, int type) {
        String result = "";
        if (type == 1) {//返回身份类型int
            if ("0".equals(passType)) {
                result = "1";
            }
            else if ("1".equals(passType)) {
                result = "B";
            }
            else if ("4".equals(passType)) {
                result = "C";
            }
            else if ("5".equals(passType)) {
                result = "G";
            }
        }
        else if (type == 2) {//返回身份类型String
            if ("0".equals(passType)) {
                result = "二代身份证";
            }
            else if ("1".equals(passType)) {
                result = "护照";
            }
            else if ("4".equals(passType)) {
                result = "港澳通行证";
            }
            else if ("5".equals(passType)) {
                result = "台湾通行证";
            }
        }
        return result;
    }

    /**
     * 获取票类型
     * @param ticketType
     * @param type
     * @return
     */
    public String findPassengerTypeId(String ticketType, int type) {
        String result = "";
        if (type == 1) {
            if ("0".equals(ticketType)) {
                result = "1";
            }
            else if ("1".equals(ticketType)) {
                result = "2";
            }
            else if ("2".equals(ticketType)) {
                result = "3";
            }
        }
        else if (type == 2) {
            if ("0".equals(ticketType)) {
                result = "成人票";
            }
            else if ("1".equals(ticketType)) {
                result = "儿童票";
            }
            else if ("2".equals(ticketType)) {
                result = "学生票";
            }
        }
        return result;

    }

    public String getCurrentTime(int i) {
        SimpleDateFormat df = null;
        if (i == 1) {
            df = new SimpleDateFormat("yyyyMMddHHmmss");
        }
        else {
            df = new SimpleDateFormat("yyyy-MM-dd");
        }
        return (df.format(new Date()));
    }

    public String MD5(String input) throws Exception {
        if (StringIsNull(input)) {
            return "";
        }
        byte[] buf = input.getBytes("utf-8");
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(buf);
        byte[] md = m.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < md.length; i++) {
            int val = ((int) md[i]) & 0xff;
            if (val < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**字符串是否为空*/
    public boolean StringIsNull(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

}
