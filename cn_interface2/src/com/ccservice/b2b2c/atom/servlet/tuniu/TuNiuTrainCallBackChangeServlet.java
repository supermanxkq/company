package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.format.json.ValueFilterTuniuChangeValue;

/**
 * 途牛改签回调接口
 * @time 2015年11月19日 下午5:43:30
 * 朱李旭
 **/

public class TuNiuTrainCallBackChangeServlet {

    public String partnerid;

    public String key;

    /**
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected String oper(String param) {
        String result1 = "";
        String orderid = "";

        try {
            JSONObject json = JSONObject.parseObject(param);
            int type = 0;
            //结果
            //日志名称
            String logName = "t途牛火车票接口_异步改签回调";
            if (type == 1) {
                logName = "t途牛火车票接口_4.12.改签占座回调";
            }
            else if (type == 2) {
                logName = "t同途牛火车票接口_4.14.改签确认回调";
            }
            try {
                //代理ID
                String agentId = json.getString("agentId");
                //回调地址
                String callBackUrl = json.getString("callBackUrl");
                //移除属性
                json.remove("agentId");
                json.remove("callBackUrl");
                //获取KEY
                String key = this.key;
                String partnerid = this.partnerid;
                Map map = getkeybyagentid(agentId);
                String C_LOGINNAME = gettrainorderinfodatabyMapkey(map, "C_USERNAME");
                //                    //非同程
                //                    if (C_LOGINNAME.indexOf("tongcheng") < 0) {
                //                        partnerid = C_LOGINNAME;
                //                        key = gettrainorderinfodatabyMapkey(map, "C_KEY");
                //                    }
                String reqJsonString = json.toJSONString();
                try {
                    reqJsonString = URLDecoder.decode(reqJsonString, "UTF-8");
                    json = JSONObject.parseObject(reqJsonString);
                }
                catch (Exception e) {
                }
                //拼参数
                json.put("partnerid", partnerid);
                //请求时间
                String reqtime = gettimeString(2);
                json.put("reqtime", reqtime);
                WriteLog.write(logName, orderid + ":callBackUrl:" + callBackUrl + ":partnerid=" + partnerid
                        + ":reqtime=" + reqtime + ":key=" + key);
                //数字签名
                json.put("sign", ElongHotelInterfaceUtil.MD5(partnerid + reqtime + ElongHotelInterfaceUtil.MD5(key)));
                //记录日志
                WriteLog.write(logName, orderid + ":callBackUrl:" + callBackUrl + ":param:backjson=" + json);
                String reqjsonString = json.toJSONString();
                if (partnerid.contains("tuniu")) {
                    WriteLog.write(logName, orderid + ":tuniu:reqjsonString:before:" + reqjsonString);
                    reqjsonString = ValueFilterTuniuChangeValue.getNewJSONString(reqjsonString);
                }
                else {
                    reqjsonString = URLEncoder.encode(reqjsonString, "UTF-8");
                }
                WriteLog.write(logName, orderid + ":reqjsonString:" + reqjsonString);
                //连续通知5次，直到成功
                String backResult = "";
                for (int i = 1; i <= 5; i++) {
                    if (i > 1) {
                        Thread.sleep(15000l);
                    }
                    //请求代理商
                    //                backResult = RequestUtil.post(callBackUrl, "backjson=" + reqjson, "UTF-8",
                    //                        new HashMap<String, String>(), 0);//这个方法有乱码
                    //中兴商旅>>Content-Type>>application/x-www-form-urlencoded
                    //                        if (partnerid.contains("shanglvelutong")) {
                    //                            backResult = SendPostandGet.submitPostMeiTuan(callBackUrl, "backjson=" + reqjsonString, "UTF-8")
                    //                                    .toString();
                    //                        }
                    //                        else {
                    backResult = SendPostandGet.submitPost(callBackUrl, "backjson=" + reqjsonString, "UTF-8")
                            .toString();
                    //                        }
                    //记录日志
                    WriteLog.write(logName, orderid + ":第" + i + "次回调返回:" + backResult);
                    //成功
                    if ("success".equalsIgnoreCase(backResult)) {
                        break;
                    }
                }
                //成功
                if ("success".equalsIgnoreCase(backResult)) {
                    result1 = "success";
                }
                else {
                    result1 = "连续通知异常5次,停止通知,需人工介入处理"
                            + (ElongHotelInterfaceUtil.StringIsNull(backResult) ? "" : "---" + backResult);
                }
            }
            catch (Exception e) {
                WriteLog.write(logName, orderid + ":Exception:" + ElongHotelInterfaceUtil.errormsg(e));
            }

        }
        catch (Exception e) {
            WriteLog.write("", orderid + ":Exception:" + ElongHotelInterfaceUtil.errormsg(e));
        }
        return result1;
    }

    /**
    * 根据12306订单号找到这个接口用户的key和loginname
    * 
    * @param trainorderid
    * @return
    * @time 2015年3月30日 下午7:55:59
    * @author chendong
    */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getkeybyagentid(String agentid) {
        if (Server.getInstance().getKeyMap().get(agentid) == null) {
            Map keymapbydb = null;
            try {
                Map keyMap = Server.getInstance().getKeyMap();
                keymapbydb = getkeybyagentidDB(agentid);
                keyMap.put(agentid, keymapbydb);
                //                Server.getInstance().setKeyMap(keyMap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return keymapbydb;
        }
        else {
            return Server.getInstance().getKeyMap().get(agentid);
        }

    }

    /**
     * 修改这个方法不用customeruser里的workphone了
     * 
     * @param agentid
     * @return
     * @time 2015年7月31日 下午1:02:02
     * @author chendong
     */
    private Map getkeybyagentidDB(String agentid) {
        Map map = new HashMap();
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE,C_REFUNDCALLBACKURL "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=(" + agentid + ")";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
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

}
