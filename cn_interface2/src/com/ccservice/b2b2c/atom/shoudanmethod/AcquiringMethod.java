package com.ccservice.b2b2c.atom.shoudanmethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.tenpay.util.MD5Util;

public class AcquiringMethod {

    public static void acquiring(Trainorder trainorder) {
        //淘宝   agentid  57    同程 47   出发日期   缺少   hh:mm    全部都去查一次   转化格式   yyyy-MM-dd hh:mm
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date departtime = null;
        try {
            departtime = sdf.parse(trainorder.getPassengers().get(0).getTraintickets().get(0).getDeparttime());
        }
        catch (ParseException e1) {
            WriteLog.write("t火车票接口_4.6收单", "发车日期  "+departtime);
            e1.printStackTrace();
        }//出发日期
        String departtimeq = sdf.format(departtime);//传入查询接口的参数
        String trainno = trainorder.getPassengers().get(0).getTraintickets().get(0).getTrainno();//车次no
        String departure = trainorder.getPassengers().get(0).getTraintickets().get(0).getDeparture();//出发站
        String arrival = trainorder.getPassengers().get(0).getTraintickets().get(0).getArrival();//到达站
        String partnerid = "hthy_test";
        String key = "2pUjUHRFSvWLWoUrfiWiZ813Be8f0IQI";
        String method = "train_query_remain";
        String url = "http://searchtrain.hangtian123.net/trainSearch";
        String ftime = "";
        String json = "";
        try {
            json = test_interface(departtimeq, departure.trim(), arrival.trim(), partnerid, key, url, method);
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("t火车票接口_4.6收单", "查询出错,返回结果" + json);
        }
        if (!"".equals(json)) {
            ftime = getdeparttime(trainno, json);
        }else{
            ftime="";
        }
        if ("".equals(ftime)) {
            departtimeq += " 23:00";
        }
        else {
            departtimeq += " " + ftime;
        }
        String stime = departtimeq;
        String sqlselect = "  [sp_T_TRAINORDERACQUIRING_selectbyorderid] @orderid=" + trainorder.getId();
        List list = null;
        try {
            list = Server.getInstance().getSystemService().findMapResultByProcedure(sqlselect);
        }
        catch (Exception e) {
            WriteLog.write("t火车票接口_4.6收单", "查询收单表出错" + trainorder.getId());
            e.printStackTrace();
        }
        if (null == list || list.size() < 1) {
            String sql = "exec  [sp_T_TRAINORDERACQUIRING_insert] @departtime= '" + stime + "' ,@agentid='"
                    + trainorder.getAgentid() + "',@orderstatus=" + trainorder.getOrderstatus() + ",@orderid="
                    + trainorder.getId();
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            WriteLog.write("t火车票接口_4.6收单", "sql:" + sql);
            trainorder.setOrderstatus(501);
            Server.getInstance().getTrainService().updateTrainorder(trainorder);
        }
        else {
            WriteLog.write("t火车票接口_4.6收单", "已存在：" + trainorder.getId());
        }
    }

    //查询车次信息
    private static String test_interface(String train_date, String from_station, String to_station, String partnerid,
            String key, String url, String method) throws Exception {
        long l1 = System.currentTimeMillis();
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        JSONObject json1 = new JSONObject();
        json1.put("partnerid", partnerid);
        json1.put("method", method);
        json1.put("reqtime", reqtime);
        json1.put("sign", sign);
        json1.put("train_date", train_date);
        json1.put("from_station", Train12306StationInfoUtil.getSZMByName(from_station));
        json1.put("to_station", Train12306StationInfoUtil.getSZMByName(to_station));
        json1.put("purpose_codes", "ADULT");
        String paramContent = "jsonStr=" + json1.toJSONString();
        l1 = System.currentTimeMillis();
        String resultString = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
        return resultString;
    }

    //根据已有信息  循环判断拿出出发具体时间
    public static String getdeparttime(String trainno, String json) {
        String result = "";
        JSONObject jsono = JSONObject.parseObject(json);
        if (jsono.getBoolean("success")) {
            String dataa = jsono.getString("data").toString();
            JSONArray jsona = JSONArray.parseArray(dataa);
            for (int i = 0; i < jsona.size(); i++) {
                JSONObject jbt = JSONObject.parseObject(jsona.get(i).toString());
                if (trainno.equals(jbt.getString("train_code"))) {
                    result = jbt.getString("start_time");
                    break;
                }
            }
        }
        return result;
    }

    public static String getreqtime() {
        SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getsign(String partnerid, String method, String reqtime, String key) {
        key = MD5Util.MD5Encode(key, "UTF-8");
        String jiamiqian = partnerid + method + reqtime + key;
        String sign = MD5Util.MD5Encode(jiamiqian, "UTF-8");
        return sign;
    }

    public static void main(String[] args) {

    }
}
