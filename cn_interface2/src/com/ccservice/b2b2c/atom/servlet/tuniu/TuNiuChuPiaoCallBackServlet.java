package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengCallBackServletUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 
 *途牛新版 出票回调接口
 * @time 2016年4月20日 下午5:33:27
 * @author 杨荣强
 */
public class TuNiuChuPiaoCallBackServlet {
    
    public final static String logName = "tuniu_3_3_6_2_出票回调接口";

    private static TuNiuChuPiaoCallBackServlet tncpcbs = null;
    
    public String partnerid;

    public String payCallbackUrl;//支付回调

    public String key;
    
    public static TuNiuChuPiaoCallBackServlet getInstance() {
        if (tncpcbs == null) {
            tncpcbs = new TuNiuChuPiaoCallBackServlet();
        }
        return tncpcbs;
    }
    
    public String chupiao(JSONObject jsons) {
        int r1 = new Random().nextInt(10000);
        String result = "";
        String orderid = "";
        try {
            String param = jsons.toString();
            WriteLog.write(logName, r1 + "--->" + param);
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("returnCode", "101");
                obj.put("errorMsg", "请求参数为空");
                result = obj.toString();
            }
            else {
                JSONObject json = JSONObject.parseObject(param);
                //请求方法
                //参数
                orderid = json.getString("orderid");
                String transactionid = json.getString("transactionid");
                //出票回调
                String isSuccess_String = json.getString("isSuccess");
                String iskefu = json.getString("iskefu");
                String pkid = json.get("pkid") != null ? json.getString("pkid") : "";
                String isSuccess = "N".equals(isSuccess_String) ? "N" : "Y";
                WriteLog.write(logName, r1 + "--->orderid:" + orderid + "--->transactionid:" + transactionid
                        + "--->isSuccess:" + isSuccess + "--->iskefu:" + iskefu + "--->pkid:" + pkid);
                result = payCallBack(orderid, transactionid, 0, isSuccess, iskefu, pkid, r1);
            }
        }
        catch (Exception e) {
            JSONObject obj = new JSONObject();
            obj.put("success", false);
            obj.put("returnCode", "999");
            obj.put("errorMsg", "未知异常");
            result = obj.toString();
        }
        return result;
    }

    public String payCallBack(String qunarOrderid, String transactionid, int errorCount, String isSuccess,
            String iskefu, String pkid, int r1) {
        JSONObject jsonstr = new JSONObject();
        JSONObject obj = new JSONObject();
        int code = 120000;//    int 4   状态编码
        String ret = "false";
        Map traininfodataMap = (pkid != null && !"".equals(pkid)) ? getTrainorderstatusByPkid(pkid)
                : getTrainorderstatus(2, 0L, qunarOrderid, transactionid, r1);//得到表中的一些信息
        WriteLog.write(logName, r1 + "--->traininfodataMap" + traininfodataMap);
        if (transactionid == null) {
            transactionid = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERNUMBER");
        }
        String interfacetype = gettrainorderinfodatabyMapkey(traininfodataMap, "C_INTERFACETYPE");
        String enrefundable = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ENREFUNDABLE");// 1：不可以退票 0：可以退票
        String accountName = "";
        if (interfacetype == null || "".equals(interfacetype)) {
            Map map_data = getcallbackurl(0L, 3, qunarOrderid, transactionid, r1);
            WriteLog.write(logName, r1 + "--->map_data" + map_data);
            interfacetype = TongChengCallBackServletUtil.getValueByMap(map_data, "C_INTERFACETYPE");
            accountName = TongChengCallBackServletUtil.getValueByMap(map_data, "C_USERNAME");
        }
        jsonstr.put("account", accountName);
        String qunarordernumber = gettrainorderinfodatabyMapkey(traininfodataMap, "C_QUNARORDERNUMBER");
        obj.put("orderId", qunarordernumber);
        String Ordernumber = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERNUMBER");
        obj.put("vendorOrderId", Ordernumber);
        jsonstr.put("data", obj);
        try {
            String time = gettimeString(2);
            jsonstr.put("timestamp", time);
            String sign = ElongHotelInterfaceUtil.MD5(this.key);
            sign = this.partnerid + time + sign;
            sign = ElongHotelInterfaceUtil.MD5(sign);
            String payCallbackUrl_temp = this.payCallbackUrl;
            Map map_data = getcallbackurl((pkid == null || "".equals(pkid)) ? 0l : Long.valueOf(pkid), 2, qunarOrderid,
                    transactionid, r1);
            String payCallbackUrl_temp_other = TongChengCallBackServletUtil.getValueByMap(map_data, "C_PAYCALLBACKURL");
            //如果这里为true说明是非同程的订单
            WriteLog.write(logName, r1 + "--->payCallbackUrl_temp_other:" + payCallbackUrl_temp_other);
            if (payCallbackUrl_temp_other != null && !"-1".equals(payCallbackUrl_temp_other)) {
                payCallbackUrl_temp = payCallbackUrl_temp_other;
                String partnerid = TongChengCallBackServletUtil.getValueByMap(map_data, "C_USERNAME");
                String key = TongChengCallBackServletUtil.getValueByMap(map_data, "C_KEY");
                sign = SignUtil.generateSign(jsonstr.toString(), key);
                jsonstr.put("sign", sign);
            }

            String msg = "支付失败";
            msg = URLEncoder.encode(msg, "UTF-8");
            if ("Y".equals(isSuccess)) {
                msg = "支付成功";
                code = 120001;
            }
            jsonstr.put("returnCode", code);
            jsonstr.put("errorMsg", msg);
            WriteLog.write(logName, r1 + "--->jsonstr:" + jsonstr.toString());
            try {
                ret = SendPostandGet.submitPost(payCallbackUrl_temp, jsonstr.toString(), "utf-8").toString();
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(logName, e, r1 + "");
            }
            WriteLog.write(logName, r1 + "--->ret:" + ret);
            //成功
            if ("success".equalsIgnoreCase(ret)) {
                yuepiao(qunarOrderid);
                return "success";
            }
            else {
                throw new Exception(ret);
            }
        }
        catch (Exception e) {
            if (!"1".equals(iskefu)) {
                try {
                    Thread.sleep(15000L);
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                errorCount = errorCount + 1;
                if (errorCount >= 5) {
                    return "连续通知异常5次的,停止通知,需人工介入处理";
                }
                else {
                    ret = payCallBack(qunarOrderid, transactionid, errorCount, isSuccess, iskefu, pkid, r1);
                }
            }
        }
        return ret;
    }

    /**
     * 通过订单ID获取参数
     * @param pkid
     * @return
     * @time 2015年9月14日 上午11:09:02
     * @author fiend
     */
    private Map getTrainorderstatusByPkid(String pkid) {
        Map map = new HashMap();
        String sql = "SELECT ID,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,"
                + "C_INTERFACETYPE,C_ENREFUNDABLE from T_TRAINORDER where ID=" + pkid;
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知", pkid + ":sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 根据订单id获取 一些信息
     * 
     * @param type 1老的同程的 2新的美团的需要获取到信息判断接口类型然后回调
     * @param trainorderid
     * @return
     * @time 2015年1月22日 下午1:05:36
     * @author chendong
     * @param transactionid 
     * @param qunarOrderid 
     */
    private Map getTrainorderstatus(int type, Long trainorderid, String qunarOrderid, String transactionid, int r1) {
        Map map = new HashMap();
        String sql = "SELECT C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                + " from T_TRAINORDER where ID=" + trainorderid;
        if (type == 2) {
            if (transactionid != null) {
                sql = "SELECT ID,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                        + " from T_TRAINORDER where C_QUNARORDERNUMBER='"
                        + qunarOrderid
                        + "' and C_ORDERNUMBER='"
                        + transactionid + "'";
            }
            else {
                sql = "SELECT ID,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                        + " from T_TRAINORDER where C_QUNARORDERNUMBER='" + qunarOrderid + "' order by id desc";
            }
        }
        WriteLog.write(logName, r1 + "--->" + qunarOrderid + ":sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 根据查到的map信息获取value
     * 
     * @param key
     * @time 2015年1月22日 下午1:08:54
     * @author chendong
     */
    private String gettrainorderinfodatabyMapkey(Map map, String key) {
        String value = "";
        if (map.get(key) != null) {
            try {
                value = map.get(key).toString();
            }
            catch (Exception e) {
            }
        }
        return value;
    }

    /**
     * 
     * 根据订单号 和type类型获取到回调地址
     * 此方法是用来获取到除同程以外的其他接口用户的回调地址
     * @param orderid
     * @param type 获取回调连接url类型 1:占座结果callback 2:出票结果callback
     * @param orderid_no :接口用户订单号
     * @param transactionid : 交易单号
     * @time 2015年3月4日 上午10:43:26
     * @author chendong
     */
    public Map getcallbackurl(Long orderid, int type, String orderid_no, String transactionid, int r1) {
        String key = "";
        Map map = new HashMap();
        String sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE ID=" + orderid;
        if (type == 2) {
            if (orderid == 0) {
                sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE C_QUNARORDERNUMBER='"
                        + orderid_no + "' and C_ORDERNUMBER='" + transactionid + "'";
            }
        }
        else if (type == 3) {
            sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE C_QUNARORDERNUMBER='"
                    + orderid_no + "' order by id desc";
        }
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=(" + sql_agentid + ")";
        WriteLog.write(logName, r1 + "--->" + orderid + ":sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        else {
            sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE "
                    + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID="
                    + "(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE ID=" + orderid_no + ")";
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                map = (Map) list.get(0);
            }
        }
        WriteLog.write(logName, r1 + "--->" + orderid + ":map:" + map);
        return map;
    }

    private String gettimeString(int type) {
        if (type == 1) {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
        else if (type == 2) {
            return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        }
        else {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
    }

    /**
     * 
     * @param qunarOrderid
     * @time 2016年1月11日 下午8:34:12
     * @author chendong
     */
    private void yuepiao(String qunarOrderid) {
        String sql = " sp_TrainOrderIsBespeak_Insert @C_QUNARORDERNUMBER='" + qunarOrderid + "'";
        WriteLog.write("出票回调成功后实现约票兼容", qunarOrderid + ":" + sql);
        try {
            List list = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
            WriteLog.write("出票回调成功后实现约票兼容", qunarOrderid + ":" + JSONObject.toJSONString(list));
        }
        catch (Exception e) {
            WriteLog.write("ERROR_出票回调成功后实现约票兼容", sql);
            ExceptionUtil.writelogByException("ERROR_出票回调成功后实现约票兼容", e);
        }
    }

}
