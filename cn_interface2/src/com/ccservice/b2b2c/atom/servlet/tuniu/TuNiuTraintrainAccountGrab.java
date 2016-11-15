package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.AirUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TuNiuTraintrainAccountGrab {
    /**
     * 把json里的对象转换成我们自己的 List<Trainpassenger>
     * @param jsons
     * @param traininfo
     * @param checi
     * @param to_station_name
     * @param train_date
     * @param from_station_name
     * @return
     * @time 2015年1月15日 上午9:35:03
     * @author chendong
     */
    public List<Trainpassenger> gettrainpassenger(JSONArray jsons, Train traininfo, String checi,
            String to_station_name, String train_date, String from_station_name) {
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        for (int i = 0; i < jsons.size(); i++) {
            Trainpassenger trainpassenger = new Trainpassenger();// 订单的人员信息
            Trainticket ticket = new Trainticket();// 票
            String passengerid = jsons.getJSONObject(i).getString("passengerid");// 乘客的顺序号
            String ticket_no = jsons.getJSONObject(i).getString("ticket_no");// 票号
            String passengersename = jsons.getJSONObject(i).getString("passengersename");// 乘客姓名
            String passportseno = jsons.getJSONObject(i).getString("passportseno");// 乘客证件号码
            String passporttypeseid = jsons.getJSONObject(i).getString("passporttypeseid");// 证件类型ID
            String passporttypeseidname = jsons.getJSONObject(i).getString("passporttypeseidname");// 证件类型名称
            String piaotype = jsons.getJSONObject(i).getString("piaotype");// 票种ID
            String piaotypename = jsons.getJSONObject(i).getString("piaotypename");// 票种名称
            String zwcode = jsons.getJSONObject(i).getString("zwcode");// 座位编码
            String zwname = jsons.getJSONObject(i).getString("zwname");// 座位名称
            zwname = zwname.replace("上", "").replace("中", "").replace("下", "");
            String cxin = jsons.getJSONObject(i).getString("cxin");// 几车厢几座
            String price = jsons.getJSONObject(i).getString("price");// 票价
            //TODO 学生票

            String province_name = jsons.getJSONObject(i).getString("provinceName") == null ? "" : jsons.getJSONObject(
                    i).getString("provinceName");// 省份名称     途牛没有
            String province_code = jsons.getJSONObject(i).getString("provinceCode") == null ? "" : jsons.getJSONObject(
                    i).getString("provinceCode");// 省份编号
            String school_code = jsons.getJSONObject(i).getString("schoolCode") == null ? "" : jsons.getJSONObject(i)
                    .getString("schoolCode");// 学校代号
            String school_name = jsons.getJSONObject(i).getString("schoolName") == null ? "" : jsons.getJSONObject(i)
                    .getString("schoolName");// 学校名称
            String student_no = jsons.getJSONObject(i).getString("studentNo") == null ? "" : jsons.getJSONObject(i)
                    .getString("studentNo");// 学号
            String school_system = jsons.getJSONObject(i).getString("schoolSystem") == null ? "" : jsons.getJSONObject(
                    i).getString("schoolSystem");// 学制
            String enter_year = jsons.getJSONObject(i).getString("enterYear") == null ? "" : jsons.getJSONObject(i)
                    .getString("enterYear"); // 入学年份
            String preference_from_station_name = jsons.getJSONObject(i).getString("preferenceFromStationName") == null ? ""
                    : jsons.getJSONObject(i).getString("preferenceFromStationName");// 起始名称
            String preference_from_station_code = jsons.getJSONObject(i).getString("preferenceFromStationCode") == null ? ""
                    : jsons.getJSONObject(i).getString("preferenceFromStationCode");// 起始地代号
            String preference_to_station_name = jsons.getJSONObject(i).getString("preferenceToStationName") == null ? ""
                    : jsons.getJSONObject(i).getString("preferenceToStationName");// 到达名称
            String preference_to_station_code = jsons.getJSONObject(i).getString("preferenceToStationCode") == null ? ""
                    : jsons.getJSONObject(i).getString("preferenceToStationCode");// 到达地代号

            // if (price != null && !"".equals(price)) {
            // totalprice += Float.valueOf(price);
            // }
            passportseno = AirUtil.ToDBC(passportseno);
            // 乘客信息
            if (passportseno.length() > 14) {
                trainpassenger.setBirthday(passportseno.substring(6, 14));
            }
            else {
                trainpassenger.setBirthday("");
            }
            trainpassenger.setName(passengersename);
            trainpassenger.setIdtype(TuNiuServletUtil.getIdtype12306tolocal(passporttypeseid));
            trainpassenger.setIdnumber(passportseno);
            trainpassenger.setAduitstatus(0);
            trainpassenger.setChangeid(0);
            trainpassenger.setPassengerid(passengerid);
            // 票信息
            ticket.setTrainno(checi);
            ticket.setPrice(Float.valueOf(price));
            ticket.setPayprice(Float.valueOf(price));
            // ticket.setCoach(cxin);
            // ticket.setSeatno(zwcode);
            ticket.setSeattype(zwname);
            ticket.setArrival(to_station_name);
            ticket.setTcseatno(passengerid);
            if (traininfo != null && traininfo.getStarttime() != null) {
                ticket.setDeparttime(train_date + " " + traininfo.getStarttime());
            }
            else {
                ticket.setDeparttime(null);
            }
            if (traininfo != null && traininfo.getEndtime() != null) {
                ticket.setArrivaltime(traininfo.getEndtime());
            }
            else {
                ticket.setArrivaltime(null);
            }
            ticket.setDeparture(from_station_name);
            if (traininfo != null && traininfo.getCosttime() != null) {
                ticket.setCosttime(traininfo.getCosttime());
            }
            else {
                ticket.setCosttime(null);// 历时
            }
            ticket.setStatus(Trainticket.WAITPAY);
            ticket.setInsurenum(0);
            piaotype = (piaotype == null || "".equals(piaotype)) ? "1" : piaotype;
            ticket.setTickettype(Integer.parseInt(piaotype));
            List<Trainticket> tickets = new ArrayList<Trainticket>();
            tickets.add(ticket);
            trainpassenger.setTraintickets(tickets);
            List<TrainStudentInfo> trainStudentInfos = new ArrayList<TrainStudentInfo>();
            TrainStudentInfo trainStudentInfo = new TrainStudentInfo();

            if (3 == ticket.getTickettype()) {
                trainStudentInfo.setStudentcard("");//优惠卡号
                trainStudentInfo.setClasses(""); //所在班级
                trainStudentInfo.setDepartment(""); //所在院系
                trainStudentInfo.setEductionalsystem(school_system); //学制
                trainStudentInfo.setEntranceyear(enter_year); //入学年份
                trainStudentInfo.setFromcity(preference_from_station_name); //出发城市
                trainStudentInfo.setTocity(preference_to_station_name); //到达城市
                trainStudentInfo.setSchoolprovince(province_name); //学校所在省
                trainStudentInfo.setSchoolname(school_name); //学校名称
                trainStudentInfo.setStudentno(student_no); //学生证号
                trainStudentInfo.setSchoolnamecode(school_code); //学校代号
                trainStudentInfo.setSchoolprovincecode(province_code); //学校所在省代号
                trainStudentInfo.setFromcitycode(preference_from_station_code); //出发城市代号
                trainStudentInfo.setTocitycode(preference_to_station_code); //到达城市代号
                trainStudentInfo.setArg1(0l); //备用参数1
                trainStudentInfo.setArg2(""); //备用参数2
                trainStudentInfo.setArg3(0l); //备用参数3
                trainStudentInfos.add(trainStudentInfo);
                trainpassenger.setTrainstudentinfos(trainStudentInfos);
            }
            passengers.add(trainpassenger);
        }
        return passengers;
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
    public void createtrainorderrc(int ywtype, String content, Long orderid, Long ticketid, int status,
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
     * 根据火车票订单id获取订单号
     * 
     * @param id
     * @return
     * @time 2015年1月5日 下午10:42:33
     * @author chendong
     */
    public String getordernumberbyid(long id) {
        String orderid = "";
        try {
            List list_trainorder = Server.getInstance().getSystemService()
                    .findMapResultBySql("SELECT C_ORDERNUMBER FROM T_TRAINORDER WHERE ID=" + id, null);
            if (list_trainorder.size() > 0) {
                Map maptrainnumber = (Map) list_trainorder.get(0);
                Object C_ORDERNUMBER_Object = maptrainnumber.get("C_ORDERNUMBER");
                if (C_ORDERNUMBER_Object != null) {
                    orderid = C_ORDERNUMBER_Object.toString();
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_bespesk_getordernumberbyid", e);
        }
        return orderid;
    }

    /**
     * 把途牛的请求修改为美团请求
     * 
     * @param tuniuJson
     * @return
     * @time 2015年11月24日 下午1:35:56
     * @author fiend
     */
    public JSONObject changeTuniu2Meituan(JSONObject tuniuJson) {
        TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();
        String orderId = tuNiuServletUtil.getParamByJsonStr("orderId", tuniuJson);//    string  Y       途牛订单号
        String cheCi = tuNiuServletUtil.getParamByJsonStr("cheCi", tuniuJson);//  string  Y       车次
        String fromStationCode = tuNiuServletUtil.getParamByJsonStr("fromStationCode", tuniuJson);//    string  Y       出发站简码
        String fromStationName = tuNiuServletUtil.getParamByJsonStr("fromStationName", tuniuJson);//    string  Y       出发站名称
        String toStationCode = tuNiuServletUtil.getParamByJsonStr("toStationCode", tuniuJson);//  string  Y       到达站简码
        String toStationName = tuNiuServletUtil.getParamByJsonStr("toStationName", tuniuJson);//  string  Y       到达站名称
        String trainDate = tuNiuServletUtil.getParamByJsonStr("trainDate", tuniuJson);//  string  Y       乘车日期
        String callBackUrl = tuNiuServletUtil.getParamByJsonStr("callBackUrl", tuniuJson);//    string  Y       回调地址
        boolean hasSeat = tuNiuServletUtil.getParamByJsonBoolean("hasSeat", tuniuJson);//    boolean Y       是否出无座票  true:不出无座票false:允许出无座票
        String passengers = tuNiuServletUtil.getParamByJsonStr("passengers", tuniuJson);// string  Y       乘客信息的json字符串。可以是多个乘客信息，最多5个，如：[{乘客1信息},{乘客2信息},...]，也可以只有一个，[{乘客1信息}]。乘客参数见附注1。重要提示：不能只购买儿童票，如果购买儿童票，必须使用随行成人的成人票证件信息（包括姓名、证件号码）。
        String contact = tuNiuServletUtil.getParamByJsonStr("contact", tuniuJson);//    string  Y       联系人姓名
        String phone = tuNiuServletUtil.getParamByJsonStr("phone", tuniuJson);//  string  Y       联系人手机
        String trainAccount = tuNiuServletUtil.getParamByJsonStr("trainAccount", tuniuJson);//   string  N       12306用户名
        String pass = tuNiuServletUtil.getParamByJsonStr("pass", tuniuJson);//   string  N       12306密码
        String insureCode = tuNiuServletUtil.getParamByJsonStr("insureCode", tuniuJson);// string  N       保险产品编号（有值，则表示此单购买保险）
        JSONObject meituanJson = new JSONObject();
        meituanJson.put("qorderid", orderId);
        meituanJson.put("callback_url", callBackUrl);
        meituanJson.put("from_station_code", fromStationCode);
        meituanJson.put("from_station_name", fromStationName);
        meituanJson.put("to_station_code", toStationCode);
        meituanJson.put("to_station_name", toStationName);
        meituanJson.put("start_date", trainDate);
        meituanJson.put("train_codes", cheCi);
        meituanJson.put("qorder_type", 100);
        meituanJson.put("is_need_standing", hasSeat);//是否需要无座票
        meituanJson.put("train_account", trainAccount);//12306账号
        meituanJson.put("pass", pass);//12306密码
        meituanJson = meituanJsonPassengerByTuniuJson(meituanJson, tuniuJson);
        return meituanJson;
    }

    /**
     * 获取美团json请求的乘客信息
     * 
     * @param meituanJson
     * @param tuniuJson
     * @return
     * @time 2015年11月24日 下午2:07:32
     * @author fiend
     */
    public JSONObject meituanJsonPassengerByTuniuJson(JSONObject meituanJson, JSONObject tuniuJson) {
        String passengers = tuniuJson.getString("passengers");
        JSONArray tuniuPassengerList = JSONArray.parseArray(passengers);
        JSONArray meituanPassengerList = new JSONArray();
        String zwname = "";
        for (int i = 0; i < tuniuPassengerList.size(); i++) {
            JSONObject meituanPassengerJson = new JSONObject();
            String passengerid = tuniuPassengerList.getJSONObject(i).getString("passengerId");// 乘客的顺序号
            String passengersename = tuniuPassengerList.getJSONObject(i).getString("passengerName");// 乘客姓名
            String passportseno = tuniuPassengerList.getJSONObject(i).getString("passportNo");// 乘客证件号码
            String passporttypeseid = tuniuPassengerList.getJSONObject(i).getString("passportTypeId");// 证件类型ID
            String passporttypeseidname = tuniuPassengerList.getJSONObject(i).getString("passportTypeName");// 证件类型名称
            String piaotype = tuniuPassengerList.getJSONObject(i).getString("piaoType");// 票种ID
            String piaotypename = tuniuPassengerList.getJSONObject(i).getString("piaoTypeName");// 票种名称
            zwname = tuniuPassengerList.getJSONObject(i).getString("zwName");// 座位名称
            zwname = zwname.replace("上", "").replace("中", "").replace("下", "");
            //TODO 学生票

            String province_name = tuniuPassengerList.getJSONObject(i).getString("provinceName") == null ? ""
                    : tuniuPassengerList.getJSONObject(i).getString("provinceName");// 省份名称     途牛没有
            String province_code = tuniuPassengerList.getJSONObject(i).getString("provinceCode") == null ? ""
                    : tuniuPassengerList.getJSONObject(i).getString("provinceCode");// 省份编号
            String school_code = tuniuPassengerList.getJSONObject(i).getString("schoolCode") == null ? ""
                    : tuniuPassengerList.getJSONObject(i).getString("schoolCode");// 学校代号
            String school_name = tuniuPassengerList.getJSONObject(i).getString("schoolName") == null ? ""
                    : tuniuPassengerList.getJSONObject(i).getString("schoolName");// 学校名称
            String student_no = tuniuPassengerList.getJSONObject(i).getString("studentNo") == null ? ""
                    : tuniuPassengerList.getJSONObject(i).getString("studentNo");// 学号
            String school_system = tuniuPassengerList.getJSONObject(i).getString("schoolSystem") == null ? ""
                    : tuniuPassengerList.getJSONObject(i).getString("schoolSystem");// 学制
            String enter_year = tuniuPassengerList.getJSONObject(i).getString("enterYear") == null ? ""
                    : tuniuPassengerList.getJSONObject(i).getString("enterYear"); // 入学年份
            String preference_from_station_name = tuniuPassengerList.getJSONObject(i).getString(
                    "preferenceFromStationName") == null ? "" : tuniuPassengerList.getJSONObject(i).getString(
                    "preferenceFromStationName");// 起始名称
            String preference_from_station_code = tuniuPassengerList.getJSONObject(i).getString(
                    "preferenceFromStationCode") == null ? "" : tuniuPassengerList.getJSONObject(i).getString(
                    "preferenceFromStationCode");// 起始地代号
            String preference_to_station_name = tuniuPassengerList.getJSONObject(i)
                    .getString("preferenceToStationName") == null ? "" : tuniuPassengerList.getJSONObject(i).getString(
                    "preferenceToStationName");// 到达名称
            String preference_to_station_code = tuniuPassengerList.getJSONObject(i)
                    .getString("preferenceToStationCode") == null ? "" : tuniuPassengerList.getJSONObject(i).getString(
                    "preferenceToStationCode");// 到达地代号
            meituanPassengerJson.put("passengerid", passengerid);
            meituanPassengerJson.put("passengersename", passengersename);
            meituanPassengerJson.put("passportseno", passportseno);
            meituanPassengerJson.put("passporttypeseid", passporttypeseid);
            meituanPassengerJson.put("passporttypeidname", passporttypeseidname);
            meituanPassengerJson.put("piaotype", piaotype);
            meituanPassengerJson.put("piaotypename", piaotypename);
            meituanPassengerJson.put("province_name", province_name);
            meituanPassengerJson.put("province_code", province_code);
            meituanPassengerJson.put("school_code", school_code);
            meituanPassengerJson.put("school_name", school_name);
            meituanPassengerJson.put("student_no", student_no);
            meituanPassengerJson.put("school_system", school_system);
            meituanPassengerJson.put("enter_year", enter_year);
            meituanPassengerJson.put("preference_from_station_name", preference_from_station_name);
            meituanPassengerJson.put("preference_from_station_code", preference_from_station_code);
            meituanPassengerJson.put("preference_to_station_name", preference_to_station_name);
            meituanPassengerJson.put("preference_to_station_code", preference_to_station_code);
            meituanPassengerJson.put("preference_from_station_name", preference_from_station_name);
            meituanPassengerList.add(meituanPassengerJson);
        }
        meituanJson.put("passengers", meituanPassengerList);
        meituanJson.put("seat_type", zwname);
        return meituanJson;
    }
}
