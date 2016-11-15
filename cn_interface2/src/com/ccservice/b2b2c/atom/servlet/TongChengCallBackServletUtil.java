package com.ccservice.b2b2c.atom.servlet;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.callback.PropertyUtil;

public class TongChengCallBackServletUtil {

    /**
     * 
     * @return
     * @time 2016年1月19日 下午12:01:48
     * @author chendong
     * @param res 
     * @param returncode 
     * @param dataMap 
     */
    public static Map getMapInfo(String res, int returncode, Map dataMap) {
        res = res == null ? "" : res.replace("提交订单失败：", "");
        String partnerid = getValueByMap(dataMap, "C_USERNAME");
        String agentid = getValueByMap(dataMap, "C_AGENTID");
        String Agentid = PropertyUtil.getValue("Agentid", "Train.properties");
        Integer code = 999;
        String msg = "";
        Map map = new HashMap<String, Object>();
        /**
         * res 里的信息包含12306返回的信息
         */
        if (res.indexOf("ErrorType506") > -1) {
            code = 506;
            if (agentid.equals(Agentid)) {
                code = 99000100;
            }
            msg = res.replace("ErrorType506", "");
            msg = geturlencode(msg);
        }
        else if (res.indexOf("没有余票") > -1 || res.indexOf("此车次无票") > -1 || res.indexOf("已无余票") > -1
                || res.indexOf("没有足够的票") > -1 || res.indexOf("余票不足") > -1 || res.indexOf("非法的席别") > -1) {
            code = 301;
            if (agentid.equals(Agentid)) {
                code = 110000;
            }
            msg = "没有余票";
            msg = geturlencode(msg);
            //            helpinfo = res;//helpinfo可以直接返回
        }
        else if (res.indexOf("其他订单行") > -1 || res.indexOf("本次购票行程冲突") > -1) {
            code = 310;
            if (agentid.equals(Agentid)) {
                code = 110002;
            }
            //                msg = "本次购票与其他订单行程冲突";
            //                msg = geturlencode(msg);
            msg = geturlencode(res);
            //                helpinfo = res;
        }
        else if (res.indexOf("已订") > -1 || res.indexOf("已购买") > -1) {
            code = 305;
            if (agentid.equals(Agentid)) {
                code = 110002;
            }
            //            msg = "乘客已经预订过该车次";
            msg = geturlencode(res);
            //            helpinfo = res;
        }
        else if (res.indexOf("当前提交订单用户过多") > -1 || res.indexOf("包含排队中的订单") > -1) {
            code = 307;
            if (agentid.equals(Agentid)) {
                code = 110010;
            }
            msg = "当前提交订单用户过多";
            msg = geturlencode(msg);
            //            helpinfo = res;
        }
        else if (res.indexOf("冒用") > -1) {
            code = 315;
            if (agentid.equals(Agentid)) {
                code = 110005;
            }
            msg = res;
            msg = geturlencode(msg);
        }
        else if (res.indexOf("出票失败，您的身份信息正在进行网上核验，您也可持居民身份证原件到车站售票窗口即时办理核验，详见《铁路互联网购票身份核验须知》") > -1) {
            code = 999;
            if (agentid.equals(Agentid)) {
                code = 99000109;
            }
            msg = "订单失败";
            msg = geturlencode(msg);
            //            helpinfo = msg;
        }
        else if (res.indexOf("身份") > -1 || res.contains("证件号码输入有误")) {
            code = 308;
            if (agentid.equals(Agentid)) {
                code = 110003;
            }
            //msg = "乘客身份信息未通过验证订票失败|";
            msg = "乘客身份信息未通过验证_订票失败";
            try {
                res = res.split(":")[1];
                msg += res;
            }
            catch (Exception e) {
            }
            msg = geturlencode(msg);
        }
        else if (res.indexOf("距离开车时间太近") > -1) {
            code = 700;
            if (agentid.equals(Agentid)) {
                code = 110004;
            }
            msg = "距离开车时间太近";
            msg = geturlencode(msg);
            //            helpinfo = msg;
        }
        else if (res.indexOf("限制高消费") > -1) {
            code = 313;
            if (agentid.equals(Agentid)) {
                code = 110012;
            }
            msg = geturlencode(res);
            //            helpinfo = msg;
        }
        else if (res.indexOf("12306排队") > -1 || res.contains("排队人数现已超过余票数")) {
            // 二十八、12306进入排队情况调整机制  供应商针对12306进入排队状态的订单
            //1.供应商返回同程排队code（995）
            //2.供应商在返回同程排队code（995）时，需要关闭该订单12306排队机制，不继续进行占座流程，即该订单处于占座失败状态
            msg = geturlencode("12306排队失败");
            code = 995;
            if (agentid.equals(Agentid)) {
                code = 110000;
            }
        }
        else if (res.indexOf("不允许发售该票种的席位") > -1) {
            code = 999;
            msg = "票种(学)不在允许的售票时间内,不允许发售该票种的席位!";
            msg = geturlencode(msg);
            //            helpinfo = msg;
        }
        else if (res.indexOf("validatorMessage") > -1) {
            code = 999;
            if (agentid.equals(Agentid)) {
                code = 99000109;
            }
            msg = "订单失败";
            msg = geturlencode(msg);
            //            helpinfo = msg;
        }
        //如果失败原因是这个的话就是12306提示的完整的信息，一并回调给客户
        else if (res.startsWith("userAccountTrainOrder")) {
            code = 999;
            if (agentid.equals(Agentid)) {
                code = 99000109;
            }
            res = res.replace("userAccountTrainOrder", "");
            msg = geturlencode(res);
        }
        //客人账号返回拒单真实原因，针对美团等客人账号订单
        else if (returncode > 0) {
            code = returncode;
            msg = geturlencode(res);
        }
        else {
            code = 999;
            if (agentid.equals(Agentid)) {
                code = 99000109;
            }
            //                msg = "订单失败";
            //证件号<span style='color:red'><i><b>230231198309181829</b></i></span>只能购买2015年02月07日车次K1451的一张车票。
            msg = geturlencode(res);
            //            helpinfo = msg;
        }
        if (partnerid.contains("gaotie")) {//只给高铁管家增加
            if (res.indexOf("证件号码输入有误") > -1) {//占座失败的证件号码错误的原因的
                code = 10001;
                msg = geturlencode(res);//证件号码输入有误
            }
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;
    }

    public static String geturlencode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    /**
     * 从map中获取对应的数据
     * @time 2015年7月25日 上午11:04:54
     * @author chendong
     */
    public static String getValueByMap(Map map, String key) {
        String value = "-1";
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
