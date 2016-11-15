package com.travelGuide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import TrainInterfaceMethod.TrainInterfaceMethod;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TrainEnquiriesImpl;
import com.ccservice.b2b2c.atom.servlet.TrainNumber;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;

/**
 * 车次查询|列车时刻
 * @author luoqingxin
 *
 *
 */
public class TrainEnquiries {

    private static final long serialVersionUID = 1L;

    public String key;

    public String partnerid;

    TrainEnquiriesImpl trainEnquiriesImpl;

    public String getTrainEnquiries(JSONObject json, int r1) {
        this.key = json.getString("key");
        this.partnerid = json.getString("partnerid");
        this.trainEnquiriesImpl = new TrainEnquiriesImpl();
        WriteLog.write("Update12306TrainDetail", "进入车次查询接口");
        long l1 = System.currentTimeMillis();
        JSONObject jsonObjectResult = new JSONObject();
        try {
            String jsonstr = "-1";
            if (json.equals(jsonObjectResult)) {
                jsonObjectResult.put("success", Boolean.valueOf(false));
                jsonObjectResult.put("code", Integer.valueOf(202));
                jsonObjectResult.put("msg", "传入的json的对象为空");
                WriteLog.write("Update12306TrainDetail_Error", "传入的json的对象为空");
            }
            else {
                jsonstr = json.toString();
                jsonstr = new String(jsonstr.getBytes("ISO-8859-1"), "UTF-8");
                JSONObject jsonstrJSON = (JSONObject) JSON.parse(jsonstr);
                String method = jsonstrJSON.getString("method");
                String train_date = jsonstrJSON.getString("train_date");
                String from_station = jsonstrJSON.getString("from_station");
                String to_station = jsonstrJSON.getString("to_station");
                String train_no = jsonstrJSON.getString("train_no");
                String train_code = jsonstrJSON.getString("train_code");
                if (train_date.equals("") || from_station.equals("") || to_station.equals("") || train_code.equals("")) {
                    jsonObjectResult.put("success", Boolean.valueOf(true));
                    jsonObjectResult.put("code", Integer.valueOf(202));
                    jsonObjectResult.put("msg", "传入的查询参数为空");
                }
                else if (train_no == null) {
                    jsonObjectResult.put("success", Boolean.valueOf(true));
                    jsonObjectResult.put("code", Integer.valueOf(202));
                    jsonObjectResult.put("msg", "train_no是选填项,但是必须要传过来");
                }
                else {
                    List lt = new ArrayList();
                    List list = get12306data(train_date, from_station, to_station, train_no, train_code);
                    if (!list.equals(lt)) {
                        String resultJSONString = gettongchengTrain(list, method);
                        jsonObjectResult.put("success", Boolean.valueOf(true));
                        jsonObjectResult.put("code", Integer.valueOf(200));
                        jsonObjectResult.put("msg", "正常获得结果");
                        jsonObjectResult.put("data", JSONArray.parseArray(resultJSONString));
                    }
                    else {
                        jsonObjectResult.put("success", Boolean.valueOf(false));
                        jsonObjectResult.put("code", Integer.valueOf(201));
                        jsonObjectResult.put("msg", "没有符合条件的车次信息");
                        WriteLog.write("Update12306TrainDetail", "没有符合条件的车次信息");
                    }
                }
            }
        }
        catch (Exception e) {
            jsonObjectResult.put("success", Boolean.valueOf(false));
            jsonObjectResult.put("code", Integer.valueOf(201));
            jsonObjectResult.put("msg", "查询失败");
            e.printStackTrace();
        }
        finally {
            long l2 = System.currentTimeMillis();
            long cc = l2 - l1;
            System.out.println(partnerid + "查询火车车次耗时:" + (cc / 1000) + "秒");
        }

        return jsonObjectResult.toString();
    }

    private List<TrainNumber> get12306data(String train_date, String from_station, String to_station, String train_no,
            String train_code) {
        List sktrainlist = new ArrayList();
        try {
            sktrainlist = this.trainEnquiriesImpl.process(train_date, from_station, to_station, train_no, train_code);
        }
        catch (Exception localException) {
            WriteLog.write("Update12306TrainDetail_Error", "获取车次信息失败");
        }
        return sktrainlist;
    }

    public String gettongchengTrain(List<TrainNumber> lists, String method) throws ParseException {
        NameFilter filter = new NameFilter() {
            public String process(Object source, String name, Object value) {
                if (name.equals("trainno")) {
                    return "train_no";
                }
                else if (name.equals("stationtraincode")) {
                    return "train_code";
                }
                else if (name.equals("startstationname")) {
                    return "start_station_name";
                }
                else if (name.equals("endstationname")) {
                    return "end_station_name";
                }
                else if (name.equals("traintype")) {
                    return "train_type";
                }
                else if (name.equals("arrivedaystr")) {
                    return "arrive_days";
                }
                else if (name.equals("stationno")) {
                    return "station_no";
                }
                else if (name.equals("stationname")) {
                    return "station_name";
                }
                else if (name.equals("arrivetime")) {
                    return "arrive_time";
                }
                else if (name.equals("starttime")) {
                    return "start_time";
                }
                else if (name.equals("stopovertime")) {
                    return "stopover_time";
                }
                return name;
            }
        };

        PropertyFilter filter1 = new PropertyFilter() {
            @Override
            public boolean apply(Object arg0, String arg1, Object arg2) {
                if (arg1.equals("train_no") || arg1.equals("train_code") || arg1.equals("start_station_name")
                        || arg1.equals("end_station_name") || arg1.equals("train_type") || arg1.equals("data")
                        || arg1.equals("arrive_days") || arg1.equals("station_no") || arg1.equals("station_name")
                        || arg1.equals("arrive_time") || arg1.equals("start_time") || arg1.equals("stopover_time")) {
                    return true;
                }
                return false;
            }
        };
        String reslut1 = "{}";
        reslut1 = JSON.toJSONString(lists, filter);
        JSONArray o1 = JSON.parseArray(reslut1);
        JSONArray o2 = new JSONArray();
        JSONObject jo4 = new JSONObject();
        Date date1 = new Date();
        int a = 0;
        for (int i = 0; i < o1.size(); i++) {
            SimpleDateFormat sim = new SimpleDateFormat("HH:mm:ss");
            String time = "";
            Date date2 = new Date();
            JSONObject jo1 = new JSONObject();
            JSONObject jo2 = o1.getJSONObject(i);
            if (i == 0) {
                jo4.put("train_no", jo2.get("train_no").toString());
                jo4.put("train_code", jo2.get("train_code").toString());
                jo4.put("start_station_name", jo2.get("start_station_name").toString());
                jo4.put("end_station_name", jo2.get("end_station_name").toString());
                jo4.put("train_type", jo2.get("train_type").toString());
            }
            String c = jo2.get("arrive_time").toString() + ":00";
            if (!c.equals("----:00")) {
                date2 = sim.parse(c);
                long k = date1.getTime();
                long j = date2.getTime();
                long s = k - j;
                if (s <= 0) {
                    jo1.put("arrive_days", a);
                }
                else {
                    ++a;
                    jo1.put("arrive_days", a);
                }
                date1 = sim.parse(jo2.get("start_time").toString() + ":00");
            }
            else {
                date1 = sim.parse(jo2.get("start_time").toString() + ":00");
                jo1.put("arrive_days", a);
            }
            String so = jo2.get("station_no").toString();
            if (so.indexOf("0") == 0) {
                so = so.replaceAll("0", "");
            }
            jo1.put("station_no", so);
            jo1.put("station_name", jo2.get("station_name").toString());
            jo1.put("arrive_time", jo2.get("arrive_time").toString());
            jo1.put("start_time", jo2.get("start_time").toString());
            jo1.put("stopover_time", jo2.get("stopover_time").toString());
            o2.add(jo1);
        }
        jo4.put("data", o2);
        JSONArray o4 = new JSONArray();
        o4.add(jo4);
        return JSON.toJSONString(o4, filter1);
    }
}
