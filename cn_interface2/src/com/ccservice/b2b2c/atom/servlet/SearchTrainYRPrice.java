package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;
import com.callback.WriteLog;
import com.tenpay.util.MD5Util;

public class SearchTrainYRPrice extends HttpServlet {

    public final static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private static final long serialVersionUID = 1L;

    public SearchTrainYRPrice() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        String jsonStr = request.getParameter("jsonStr");
        String result = searchPrice(jsonStr);

        PrintWriter out = null;
        out = response.getWriter();
        out.write(result);
        out.flush();
        out.close();

    }

    private String searchPrice(String jsonStr) {
        JSONObject resObject = new JSONObject();
        JSONObject searchParam = new JSONObject();
        JSONArray tickets = new JSONArray();
        JSONObject resultJson = new JSONObject();
        try {
            searchParam = JSONObject.parseObject(jsonStr);
        } catch (Exception e) {
            WriteLog.write("priceSearchInterface", "入参异常---》"+jsonStr+"异常："+e.getMessage());
            resObject.put("success", false);
            resObject.put("msg", "参数异常");
            resObject.put("code", 203);
            resObject.put("data", new JSONObject());
            return resObject.toString();
        }
        if(searchParam==null){
            resObject.put("success", false);
            resObject.put("msg", "参数异常");
            resObject.put("code", 203);
            resObject.put("data", new JSONObject());
            return resObject.toString();
        }
        String partnerid=searchParam.containsKey("partnerid")?searchParam.getString("partnerid"):"";
        String sign=searchParam.containsKey("sign")?searchParam.getString("sign"):"";
        String train_date=searchParam.containsKey("train_date")?searchParam.getString("train_date"):"";
        String from_station=searchParam.containsKey("from_station")?searchParam.getString("from_station"):"";
        String to_station=searchParam.containsKey("to_station")?searchParam.getString("to_station"):"";
        String reqtime=searchParam.containsKey("reqtime")?searchParam.getString("reqtime"):"";

        if(partnerid==null||"".equals(partnerid)||sign==null||"".equals(sign)||train_date==null||"".equals(train_date)
                ||from_station==null||"".equals(from_station)||to_station==null||"".equals(to_station)||reqtime==null||"".equals(reqtime)){
            WriteLog.write("priceSearchInterface", "请求参数--->>"+searchParam.toString());
            resObject.put("success", false);
            resObject.put("msg", "参数异常");
            resObject.put("code", 203);
            resObject.put("data", new JSONObject());
            return resObject.toString();
        }   
        String searchResult = search(partnerid, sign, train_date, from_station, to_station, reqtime);
        
        JSONObject psr = new JSONObject();
        try {
          psr = JSONObject.parseObject(searchResult);
        } catch (Exception localException1) {
        }
        boolean success = (psr.containsKey("success")) ? psr.getBoolean("success").booleanValue() : false;
        String msg = psr.getString("msg");
        int code = psr.getInteger("code").intValue();
        if (!(success)) {
          resultJson.put("success", Boolean.valueOf(false));
          resultJson.put("msg", msg);
          resultJson.put("code", Integer.valueOf(code));
          resultJson.put("data", new JSONObject());
          return resultJson.toString();
        }
        JSONObject dates = new JSONObject();
        JSONArray searchDate = psr.containsKey("data") ? psr.getJSONArray("data") : new JSONArray();
        for (int i = 0; i < searchDate.size(); ++i) {
          JSONObject json = new JSONObject();
          String train_code = (searchDate.getJSONObject(i).containsKey("train_code")) ? searchDate.getJSONObject(i).getString("train_code") : "";
          String Y_price = (searchDate.getJSONObject(i).containsKey("ywx_price")) ? searchDate.getJSONObject(i).getString("ywx_price") : "";
          String R_price = (searchDate.getJSONObject(i).containsKey("rwx_price")) ? searchDate.getJSONObject(i).getString("rwx_price") : "";
          if ("0".equals(Y_price)) {
            Y_price = "";
          }
          if ("0".equals(R_price)) {
            R_price = "";
          }
          json.put("Y_price", Y_price);
          json.put("R_price", R_price);
          dates.put(train_code, json);
        }
        resultJson.put("success", Boolean.valueOf(true));
        resultJson.put("msg", msg);
        resultJson.put("code", Integer.valueOf(code));
        resultJson.put("data", dates);

        return resultJson.toString();
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public String search(String partnerid, String sign, String train_date, String from_station, String to_station,
            String reqtime) {
        String method = "train_query";
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"train_date\":\"" + train_date + "\",\"from_station\":\""
                + from_station + "\",\"to_station\":\"" + to_station
                + "\",\"purpose_codes\":\"ADULT\",\"needdistance\":\"0\"}";
        String resultString = SendPostandGet.submitPost("http://searchtrain.hangtian123.net/trainSearch", "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    public static String getreqtime() {
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getsign(String partnerid, String method, String reqtime, String key) {
        return MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "UTF-8"), "UTF-8");
    }
}

