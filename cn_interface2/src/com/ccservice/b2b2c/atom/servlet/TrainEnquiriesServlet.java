package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.tenpay.util.MD5Util;

/**
 * 车次查询|列车时刻
 * @author luoqingxin
 */
public class TrainEnquiriesServlet extends javax.servlet.http.HttpServlet {

    private static final long serialVersionUID = 1L;

    Map<String, InterfaceAccount> interfaceAccountMap;

    public String key;

    TrainEnquiriesImpl trainEnquiriesImpl;

    @Override
    public void init() throws ServletException {
        super.init();
        interfaceAccountMap = new HashMap<String, InterfaceAccount>();
        trainEnquiriesImpl = new TrainEnquiriesImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        long l1 = System.currentTimeMillis();
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = null;
        JSONObject jsonObjectResult = new JSONObject();
        try {
            out = response.getWriter();
            request.setCharacterEncoding("utf-8");
            String jsonstr = "-1";
            if (request.getParameter("jsonStr") == null) {
                jsonObjectResult.put("success", false);
                jsonObjectResult.put("code", 202);
                jsonObjectResult.put("msg", "传入的json的对象为空");
            }
            else {
                jsonstr = request.getParameter("jsonStr") == null ? "" : request.getParameter("jsonStr");
                jsonstr = new String(jsonstr.getBytes("ISO-8859-1"), "UTF-8");
                JSONObject jsonstrJSON = (JSONObject) JSON.parse(jsonstr);
                String partnerid = jsonstrJSON.getString("partnerid");
                String train_no = jsonstrJSON.getString("train_no");
                String train_date = jsonstrJSON.getString("partnerid");//乘车日期
                String from_station = jsonstrJSON.getString("from_station");//出发站简码
                String to_station = jsonstrJSON.getString("to_station");//到达站简码
                String train_code = jsonstrJSON.getString("train_code");//车次号
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

                    InterfaceAccount interfaceAccount = interfaceAccountMap.get(partnerid);
                    if (interfaceAccount == null) {
                        interfaceAccount = getInterfaceAccountByLoginname(partnerid);
                        if (interfaceAccount != null && interfaceAccount.getKeystr() != null) {
                            interfaceAccountMap.put(partnerid, interfaceAccount);
                        }
                    }
                    String method = jsonstrJSON.getString("method");
                    String reqtime = jsonstrJSON.getString("reqtime");
                    String sign = jsonstrJSON.getString("sign");
                    String keystr = interfaceAccount.getKeystr().trim();
                    String keyString = partnerid + method + reqtime + MD5Util.MD5Encode(keystr, "UTF-8");
                    String checkSign = MD5Util.MD5Encode(keyString, "UTF-8");
                    if (sign == null) {
                        jsonObjectResult.put("success", false);
                        jsonObjectResult.put("code", 103);
                        jsonObjectResult.put("msg", "通用参数缺失");
                    }
                    else if (!sign.equals(checkSign)) {
                        if (interfaceAccount.getTrainnum() == null) {
                            interfaceAccount.setTrainnum(0L);
                        }
                        Long trainnum = 0L;
                        String mem_key = "trainSearch_trainnum_" + partnerid;
                        Object MemCached_o = MemCached.getInstance().get(mem_key);
                        if (MemCached_o == null) {
                            trainnum = interfaceAccount.getTrainnum();
                        }
                        else {
                            trainnum = Long.parseLong(MemCached_o.toString());//
                        }

                        //                    if (interfaceAccount.getLimittrainnum() != 0 && trainnum >= interfaceAccount.getLimittrainnum()) {
                        //                        jsonObjectResult.put("success", false);
                        //                        jsonObjectResult.put("code", 201);
                        //                        jsonObjectResult.put("msg", "剩余查询次数不足");
                        //                    }
                        //                    else {
                        String date = jsonstrJSON.getString("train_date");// 乘车日期（yyyy-MM-dd）
                        String from = jsonstrJSON.getString("from_station");// 出发站简码
                        String to = jsonstrJSON.getString("to_station");// 到达站简码
                        String no = jsonstrJSON.getString("train_no");//【选填】官方系统的车次内部编码，如：54000G703931
                        String code = jsonstrJSON.getString("train_code");//车次号，如：G7039
                        List<TrainNumber> list = new ArrayList<TrainNumber>();
                        list = get12306data(date, from, to, no, code);
                        if (list == null) {
                            list = new ArrayList<TrainNumber>();
                        }
                        if (list.size() > 0) {
                            String resultJSONString = gettongchengTrain(list, method);
                            jsonObjectResult.put("success", true);
                            jsonObjectResult.put("code", 200);
                            jsonObjectResult.put("msg", "正常获得结果");
                            jsonObjectResult.put("data", JSONArray.parseArray(resultJSONString));

                            interfaceAccount.setTrainnum(interfaceAccount.getTrainnum() + 1);
                            new Thread(new TrainSearchThread(mem_key, interfaceAccount.getTrainnum(), 1)).start();
                        }
                        else {
                            jsonObjectResult.put("success", false);
                            jsonObjectResult.put("code", 201);
                            jsonObjectResult.put("msg", "没有符合条件的车次信息");
                        }
                        //                    }
                    }
                    else {
                        jsonObjectResult.put("success", false);
                        jsonObjectResult.put("code", 105);
                        jsonObjectResult.put("msg", "加密错误");
                    }

                }
                WriteLog.write("trainSearch", "总耗时1:" + (System.currentTimeMillis() - l1) + "=" + jsonstr + "="
                        + jsonObjectResult.toJSONString());
            }
        }
        catch (UnsupportedEncodingException e2) {
            jsonObjectResult = getExceptionJson(1);
            e2.printStackTrace();
        }
        catch (IOException e1) {
            jsonObjectResult = getExceptionJson(2);
            e1.printStackTrace();
        }
        catch (Exception e3) {
            jsonObjectResult = getExceptionJson(3);
            e3.printStackTrace();
        }
        finally {
            out.write(jsonObjectResult.toJSONString());
            out.flush();
            out.close();
        }
    }

    private JSONObject getExceptionJson(int i) {
        JSONObject jsonObjectResult = new JSONObject();
        jsonObjectResult.put("success", false);
        jsonObjectResult.put("code", 202);
        jsonObjectResult.put("msg", "查询失败" + i);
        return jsonObjectResult;
    }

    private List<TrainNumber> get12306data(String train_date, String from_station, String to_station, String train_no,
            String train_code) {
        List<TrainNumber> sktrainlist = new ArrayList<TrainNumber>();

        try {
            sktrainlist = trainEnquiriesImpl.process(train_date, from_station, to_station, train_no, train_code);
        }
        catch (Exception e) {
        }
        return sktrainlist;
    }

    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    private InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
        }
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        else {
            if ("tongcheng_train".equals(loginname)) {
                interfaceAccount.setUsername("tongcheng_train");
                interfaceAccount.setKeystr(this.key);
            }
            else {
                interfaceAccount.setUsername(loginname);
                interfaceAccount.setKeystr("-1");
            }
            interfaceAccount.setInterfacetype(TrainInterfaceMethod.TONGCHENG);
        }
        return interfaceAccount;
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
            jo1.put("station_no", jo2.get("station_no").toString());
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
