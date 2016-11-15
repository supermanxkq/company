package com.ccservice.b2b2c.atom.servlet.tuniu.method;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TuNiuServletUtil {
    /**
     * 生成返回信息
     * @param ctx
     * @param result
     * @param logName
     * @time 2015年11月20日 下午4:50:30
     * @author fiend
     */
    public void getResponeOut(AsyncContext ctx, String result, String logName) {
        try {
            ServletResponse response = ctx.getResponse();
            //编码
            response.setCharacterEncoding("UTF-8");
            //输出
            response.getWriter().write(result);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_" + logName, e);
        }
        finally {
            try {
                ctx.complete();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 通过map获取String型的数据
     * 
     * @param key
     * @param map
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public String getParamByMapStr(String key, Map map) {
        if (map == null || key == null || "".equals(key)) {
            return "";
        }
        if (map.get(key) == null || "".equals(map.get(key).toString())) {
            return "";
        }
        return map.get(key).toString();
    }

    /**
     * 通过json获取String型的数据
     * 
     * @param key
     * @param jsonObject
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public String getParamByJsonStr(String key, JSONObject jsonObject) {
        if (jsonObject == null || key == null || "".equals(key)) {
            return "";
        }
        if (!jsonObject.containsKey(key)) {
            return "";
        }
        return jsonObject.getString(key);
    }

    /**
     * 通过json获取Boolean型的数据
     * 
     * @param key
     * @param jsonObject
     * @return
     * @time 2015年11月20日 下午4:27:55
     * @author fiend
     */
    public boolean getParamByJsonBoolean(String key, JSONObject jsonObject) {
        if (jsonObject == null || key == null || "".equals(key)) {
            return false;
        }
        if (!jsonObject.containsKey(key)) {
            return false;
        }
        return jsonObject.getBoolean(key);
    }

    /**
     * 拼接参数错误返回体
     * 
     * @param ctx
     * @time 2015年11月20日 下午4:30:30
     * @author fiend
     */
    public void respByParamError(AsyncContext ctx, String logName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "false");
        jsonObject.put("returnCode", "231008");
        jsonObject.put("errorMsg", "param error");
        getResponeOut(ctx, jsonObject.toString(), logName);
    }
    
    /**
     * 
     * @author RRRRRR
     * @time 2016年10月31日 下午5:11:42
     * @param ctx
     * @param logName
     * @return
     */
    public JSONObject respByParamErrorV2(AsyncContext ctx, String logName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "false");
        jsonObject.put("returnCode", "231008");
        jsonObject.put("errorMsg", "param error");
        return jsonObject;
    }
    /**
     * 请求用户不存在错误返回体
     * 
     * @param ctx
     * @time 2015年11月20日 下午4:30:30
     * @author fiend
     */
    public void respByUserNotExists(AsyncContext ctx, String logName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "false");
        jsonObject.put("returnCode", "231001");
        jsonObject.put("errorMsg", "user not exists");
        getResponeOut(ctx, jsonObject.toString(), logName);
    }

    /**
     * 拼接未知异常返回体
     * 
     * @param ctx
     * @time 2015年11月20日 下午4:30:30
     * @author fiend
     */
    public void respByUnknownError(AsyncContext ctx, String logName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "false");
        jsonObject.put("returnCode", "231008");
        jsonObject.put("errorMsg", "param error");
        getResponeOut(ctx, jsonObject.toString(), logName);
    }

    /**
     * 拼接未知异常返回体
     * 
     * @param ctx
     * @time 2015年11月20日 下午4:30:30
     * @author fiend
     */
    public void respBySignatureError(AsyncContext ctx, String logName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "false");
        jsonObject.put("returnCode", "231007");
        jsonObject.put("errorMsg", "signature error");
        getResponeOut(ctx, jsonObject.toString(), logName);
    }
    
    /**
     * 
    * @Title: respByNoMoney
    * @Description: 途牛账户余额不足
    * @param @param ctx
    * @param @param logName    
    * @return void   
    * @author RRRRRR
    * @throws
     */
    public void respByNoMoney(AsyncContext ctx, String logName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "false");
        jsonObject.put("returnCode", "110020");
        jsonObject.put("errorMsg", "途牛账户余额不足");
        getResponeOut(ctx, jsonObject.toString(), logName);
    }
    
    /**
     * 拼接成功返回体
     * 
     * @param ctx
     * @time 2015年11月20日 下午4:30:30
     * @author fiend
     */
    public void respBySuccess(AsyncContext ctx, String logName, JSONObject json) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "true");
        jsonObject.put("returnCode", "231000");
        jsonObject.put("errorMsg", "");
        jsonObject.put("data", json);
        getResponeOut(ctx, jsonObject.toString(), logName);
    }

    /**
     * 拼接访问频率过快返回体
     * 
     * @param ctx
     * @time 2015年11月20日 下午4:30:30
     * @author fiend
     */
    public void respByHighFrequencyError(AsyncContext ctx, String logName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", "false");
        jsonObject.put("returnCode", "231004");
        jsonObject.put("errorMsg", "high frequency error");
        getResponeOut(ctx, jsonObject.toString(), logName);
    }

    /**
     * 证件类型 12306转换代码本地
     * 
     * @param idtype
     * @return
     * @time 2014年12月24日 上午11:21:59
     * @author wzc
     */
    public static int getIdtype12306tolocal(String idtype) {
        if (idtype.equals("1")) {
            return 1;
        }
        else if (idtype.equals("B")) {
            return 3;
        }
        else if (idtype.equals("C")) {
            return 4;
        }
        else if (idtype.equals("G")) {
            return 5;
        }
        else {
            return 0;
        }
    }

    /**
     * 获取约票在我们系统中的agentid,key,password
     * 
     * @param partnerid
     * @return
     * @time 2015年11月20日 下午3:55:47
     * @author fiend
     */
    public Map getInterfaceAccount(String partnerid) {
        Map map = new HashMap();
        List list = Server.getInstance().getSystemService()
                .findMapResultByProcedure(" sp_INTERFACEACCOUNT_agentid @partnerid='" + partnerid + "'");
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 通过途牛传的坐席CODE 转换成咱们BD中座位名
     * 
     * @param tuniuSeatCode
     * @return
     * @time 2016年5月12日 下午2:10:57
     * @author fiend
     */
    public String tuniuSeatCode2DBSeatName(String tuniuSeatCode) {
        String seatname = "";
        if (tuniuSeatCode.equals("0")) {
            seatname = "棚车";
        }
        else if (tuniuSeatCode.equals("1")) {
            seatname = "硬座";
        }
        else if (tuniuSeatCode.equals("2")) {
            seatname = "软座";
        }
        else if (tuniuSeatCode.equals("3")) {
            seatname = "硬卧";
        }
        else if (tuniuSeatCode.equals("4")) {
            seatname = "软卧";
        }
        else if (tuniuSeatCode.equals("5")) {
            //            seatname = "包厢硬卧";
            seatname = "硬卧";
        }
        else if (tuniuSeatCode.equals("6")) {
            seatname = "高级软卧";
        }
        else if (tuniuSeatCode.equals("7")) {
            //            seatname = "一等软座";
            seatname = "一等座";
        }
        else if (tuniuSeatCode.equals("8")) {
            //            seatname = "二等软座";
            seatname = "二等座";
        }
        else if (tuniuSeatCode.equals("9")) {
            seatname = "商务座";
        }
        else if (tuniuSeatCode.equals("A")) {
            //            seatname = "高级动卧";
            seatname = "高级软卧";
        }
        else if (tuniuSeatCode.equals("B")) {
            //            seatname = "混编硬座";
            seatname = "硬座";
        }
        else if (tuniuSeatCode.equals("C")) {
            //            seatname = "混编硬卧";
            seatname = "硬卧";
        }
        else if (tuniuSeatCode.equals("D")) {
            //            seatname = "包厢软座";
            seatname = "软座";
        }
        else if (tuniuSeatCode.equals("E")) {
            //            seatname = "特等软座";
            seatname = "特等座";
        }
        else if (tuniuSeatCode.equals("F")) {
            //            seatname = "动卧";
            seatname = "软卧";
        }
        else if (tuniuSeatCode.equals("G")) {
            //            seatname = "二人软包";
            seatname = "软卧";
        }
        else if (tuniuSeatCode.equals("H")) {
            //            seatname = "一人软包";
            seatname = "软卧";
        }
        else if (tuniuSeatCode.equals("I")) {
            //            seatname = "一等双软";
            seatname = "一等座";
        }
        else if (tuniuSeatCode.equals("J")) {
            //            seatname = "二等双软";
            seatname = "二等座";
        }
        else if (tuniuSeatCode.equals("K")) {
            //            seatname = "混编软座";
            seatname = "软座";
        }
        else if (tuniuSeatCode.equals("L")) {
            //            seatname = "混编软卧";
            seatname = "软卧";
        }
        else if (tuniuSeatCode.equals("M")) {
            seatname = "一等座";
        }
        else if (tuniuSeatCode.equals("O")) {
            seatname = "二等座";
        }
        else if (tuniuSeatCode.equals("P")) {
            seatname = "特等座";
        }
        else if (tuniuSeatCode.equals("Q")) {
            //            seatname = "观光座";
            seatname = "硬座";
        }
        else if (tuniuSeatCode.equals("S")) {
            //            seatname = "一等包座";
            seatname = "一等座";
        }

        return seatname;
    }

    //获取当前请求时间
    public String getCurrTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
