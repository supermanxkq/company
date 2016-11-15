package com.ccservice.b2b2c.atom.servlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.train.TrainSupport;
import com.ccservice.b2b2c.util.HttpUtils;

public class TrainEnquiriesImpl extends TrainSupport implements ITrainEnquiriesServletDao {

    public List<TrainNumber> process(String train_date, String from_station, String to_station, String train_no,
            String train_code) {
        long l1 = System.currentTimeMillis();
        List listtrain = new ArrayList();
        String json = "-1";
        try {

            json = get12306searchJSON(l1, from_station, to_station, train_date, train_no, train_code);
            if ("-1".equals(json) || "".equals(json)) {
                return listtrain;
            }
            else if (json.indexOf("网络繁忙") > 1) {
                return listtrain;
            }
            else {
                WriteLog.write("Update12306TrainDetail", "获取信息失败:" + json);
                JSONObject jsonobject = JSON.parseObject(json);
                String trainno = jsonobject.getString("train_no");
                jsonobject = jsonobject.getJSONObject("data");
                JSONArray datasArray = jsonobject.getJSONArray("data");
                if (jsonobject != null) {
                    if (datasArray != null) {
                        for (int i = 0; i < datasArray.size(); ++i) {
                            JSONObject data_index = (JSONObject) datasArray.get(i);
                            TrainNumber train = new TrainNumber();
                            if (i == 0) {
                                train.setTrain_no(trainno);
                                train.setTrain_code(train_code);
                                String start_station_name = data_index.getString("start_station_name");
                                train.setStart_station_name(start_station_name);
                                String end_station_name = data_index.getString("end_station_name");
                                train.setEnd_station_name(end_station_name);
                            }
                            String train_type = data_index.getString("train_class_name");
                            train.setTrain_type(train_type);
                            String station_no = data_index.getString("station_no");
                            train.setStation_no(station_no);
                            String station_name = data_index.getString("station_name");
                            train.setStation_name(station_name);
                            String arrive_time = data_index.getString("arrive_time");
                            train.setArrive_time(arrive_time);
                            String start_time = data_index.getString("start_time");
                            train.setStart_time(start_time);
                            String stopover_time = data_index.getString("stopover_time");
                            train.setStopover_time(stopover_time);
                            listtrain.add(train);
                        }
                    }
                }
            }
        }
        catch (Exception localException) {
            localException.printStackTrace();
            WriteLog.write("Update12306TrainDetail_Error", "获取信息失败:" + json);
        }
        return listtrain;

    }

    public String get12306searchJSON(long l1, String from_station, String to_station, String train_date,
            String train_on, String train_code) {
        String json = "";
        WriteLog.write("Update12306TrainDetail", l1 + ":get12306searchJSON:" + from_station + ":" + to_station + ":"
                + train_date + ":12306耗时_-1:" + (System.currentTimeMillis() - l1));
        l1 = System.currentTimeMillis();
        Date date = new Date(40000L);
        String sql = "SELECT *  FROM TrainDetailCode WITH(NOLOCK) WHERE TrainDepartTime='" + train_date
                + "' and TrainCode='" + train_code + "'";
        List ll = new ArrayList();
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list == ll || list.size() == 0) {
            WriteLog.write("Update12306TrainDetail_Error", "没有查询的车次或日期" + sql);
        }
        else {
            String TrainDetailsCode = "";
            for (int j = 0; j < 1; j++) {
                Map map = (Map) list.get(j);
                TrainDetailsCode = map.get("TrainDetailsCode").toString();
            }
            train_on = "";
            train_on = TrainDetailsCode;
            String TrainDepartTime = train_date;
            if (!TrainDetailsCode.equals("") && !TrainDepartTime.equals("") && !train_on.equals("")) {
                String url = "https://kyfw.12306.cn/otn/czxx/queryByTrainNo?train_no=" + train_on
                        + "&from_station_telecode=" + from_station + "&to_station_telecode=" + to_station
                        + "&depart_date=" + train_date;
                do {
                    json = HttpUtils.Get_https(url, 4000);
                }
                while (json.equals(""));
                long l2 = System.currentTimeMillis();
                JSONObject jsonObject = JSONObject.parseObject(json);
                jsonObject.put("train_no", train_on);
                json = jsonObject.toString();
                creatAddTrainDetailInfo(train_code, json, from_station, to_station, train_date, l2);
            }
        }
        l1 = System.currentTimeMillis();
        return json;
    }
}