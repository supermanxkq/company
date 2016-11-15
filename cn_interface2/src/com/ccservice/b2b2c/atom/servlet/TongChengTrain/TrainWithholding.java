package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 火车票代扣 
 * @time 2015年3月26日 上午11:13:09
 * @author fiend
 */
public class TrainWithholding extends TongchengSupplyMethod {
    //    代扣地址
    //    private final String URL_WITHHOLDING = "http://test.ws.hangtian123.com/AutopayJSON";

    //代扣类型
    private final String CMD_TRAIN = "TRAIN";

    /**
     * 内部调用代付接口 
     * @param username
     * @param key
     * @param ordernumber
     * @return
     * @time 2015年3月26日 下午1:01:05
     * @author fiend
     */
    public TrainWithholeResult withholding(String username, String key, String ordernumber) {
        String URL_WITHHOLDING = PropertyUtil.getValue("URL_WITHHOLDING", "Train.properties");//"http://test.ws.hangtian123.com/AutopayJSON";
        TrainWithholeResult rwr = new TrainWithholeResult();
        JSONObject req = new JSONObject();
        req.put("username", username);
        req.put("password", key);
        req.put("ordernum", ordernumber);
        req.put("cmd", this.CMD_TRAIN);
        try {
            WriteLog.write("代扣_Before", "扣款------>" + ordernumber + ":" + req.toString() + ":" + URL_WITHHOLDING);
            StringBuffer result = SendPostandGet.submitPost(URL_WITHHOLDING, req.toString(), "UTF-8");
            WriteLog.write("代扣_Before", "扣款------>" + ordernumber + ":" + result.toString());
            if (result != null && result.toString().length() > 5) {
                JSONObject obj = JSONObject.parseObject(result.toString());
                String Remark = obj.getString("Remark");//返回信息
                String OrderNo = obj.getString("OrderNo");
                String statuscode = obj.getString("statuscode");//支付状态
                rwr.setOrderNo(OrderNo);
                rwr.setRemark(Remark);
                rwr.setStatuscode(statuscode);
            }
            else {
                rwr.setOrderNo(ordernumber);
                rwr.setRemark("C");
                //请求或解析异常
                rwr.setStatuscode("Withholding interface exception");
            }
        }
        catch (Exception e) {
            rwr.setOrderNo(ordernumber);
            rwr.setRemark("C");
            //请求或解析异常
            rwr.setStatuscode("The request or the analysis of abnormal");
        }
        return rwr;
    }

    /**
     * 代付先扣款模式   如果是该模式订单 跟进扣款接口返回结果判定true false  如果不是该模式订单 直接返回true
     * @param orderid   订单ID
     * @param orderstatus 订单状态
     * @param ywtype    业务类型 1-出票  2-改签
     * @return
     * @time 2015年3月26日 下午1:14:14
     * @author fiend
     */
    public TrainWithholeResult withHoldingBefore(long orderid, int orderstatus, int ywtype) {
        TrainWithholeResult trainWithholeResult = new TrainWithholeResult();
        WriteLog.write("代扣_Before", "扣款------>" + orderid);
        Map map = getagentbytrainorderid(orderid);
        String agentid = getagentidbymap(map, "C_AGENTID");
        String ordernumber = getagentidbymap(map, "C_ORDERNUMBER");
        String username = getcustomeruserKEYandloginname(agentid, 1);
        String key = getcustomeruserKEYandloginname(agentid, 2);
        WriteLog.write("代扣_Before", "扣款------>" + orderid + ":" + username + ":" + key + ":" + ordernumber);
        trainWithholeResult = withholding(username, key, ordernumber);
        WriteLog.write("代扣_Before",
                "扣款结果------>" + trainWithholeResult.getOrderNo() + ":" + trainWithholeResult.getRemark() + ":"
                        + trainWithholeResult.getStatuscode());
        return trainWithholeResult;
    }

    private Map getagentbytrainorderid(long orderid2) {
        Long agentid = 0L;
        String sql = "select C_AGENTID,C_ORDERNUMBER from T_trainorder with(nolock) where id=" + orderid2;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        Map map = new HashMap();
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    private String getagentidbymap(Map map, String key) {
        String value = "-1";
        if (map.get(key) != null) {
            value = map.get(key).toString();
        }
        return value;
    }

    /**
     * 
     * @param agentid
     * @param type 1 获取username 2 获取key
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author chendong
     */
    private String getcustomeruserKEYandloginname(String agentid, int type) {
        String db_value = "-1";//dateHashMap
        String key_agent_username = "loginname_username_" + agentid;
        String key_agent_key = "loginname_workphone_" + agentid;
        String key = key_agent_username;
        if (type == 2) {
            key = key_agent_key;

        }
        if (Server.getInstance().getDateHashMap().get(key) == null) {
            String ziduan = "C_LOGINNAME";
            if (type == 2) {
                ziduan = "C_WORKPHONE";
            }
            try {
                String sql = "select " + ziduan + " from T_customeruser where C_AGENTID=" + agentid;
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    if (map.get(ziduan) != null) {
                        db_value = map.get(ziduan).toString();
                        Server.getInstance().getDateHashMap().put(key, db_value);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            db_value = Server.getInstance().getDateHashMap().get(key);
        }
        return db_value;
    }

    /**
     * 代付后扣款模式 
     * @param orderid   订单ID
     * @param orderstatus 订单状态
     * @param ywtype    业务类型 1-出票  2-改签
     * @return
     * @time 2015年3月26日 下午1:14:14
     * @author fiend
     */
    //    public boolean withHoldingAfter(long orderid, int orderstatus, int ywtype) {
    //        WriteLog.write("代扣", orderid + "");
    //        int interfacetype = getOrderAttribution(orderid);
    //        if (TrainInterfaceMethod.WITHHOLDING_AFTER == interfacetype) {
    //            WriteLog.write("代扣_AFTER", "符合后扣款模式------>" + orderid);
    //            TrainWithholeResult trainWithholeResult = withholding("test", "test", orderid + "");
    //            if ("S".equalsIgnoreCase(trainWithholeResult.getRemark())) {
    //                WriteLog.write("代扣成功_AFTER", "代扣成功------>" + orderid);
    //                writeRC(orderid, "代扣成功", "代扣接口", orderstatus, ywtype);
    //                return true;
    //            }
    //            else if ("F".equalsIgnoreCase(trainWithholeResult.getRemark())) {
    //                WriteLog.write("代扣失败_AFTER", "代扣失败------>" + orderid);
    //                writeRC(orderid, "代扣失败", "代扣接口", orderstatus, ywtype);
    //                return false;
    //            }
    //            else {
    //                //异常 需要客服后台操作 检查代扣是否成功  失败重新扣款
    //                WriteLog.write("代扣异常_AFTER", "代扣异常------>" + orderid);
    //                writeRC(orderid, "代扣失败", "代扣接口", orderstatus, ywtype);
    //                return false;
    //            }
    //        }
    //        else {
    //            WriteLog.write("代扣_AFTER", "不适用------>" + orderid);
    //            return true;
    //        }
    //    }

    public static void main(String[] args) {
        new TrainWithholding().withholding("test", "test", "test");
    }
}
