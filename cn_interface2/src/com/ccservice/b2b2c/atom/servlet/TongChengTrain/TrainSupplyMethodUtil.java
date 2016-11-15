package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MD5Util;

/**
 * 原 TongchengSupplyMethod 类的方法
 * @time 2014年12月11日 上午11:54:58
 * @author chendong
 */
public class TrainSupplyMethodUtil {

    /**
     * 同程： 占座失败原因转换
     * @param jsonObject REP结果
     * @param msgType 1下单 2改签
     * @param msg REP结果
     * @return REP结果
     */
    public static JSONObject codeMsgTongcheng(JSONObject jsonObject, int msgType, String msg) {
        //默认
        int code = 999;
        //为空
        msg = ElongHotelInterfaceUtil.StringIsNull(msg) ? "" : msg;
        //判断
        if (msg.indexOf("ErrorType506") > -1) {
            code = 506;
            msg = msg.replace("ErrorType506", "");
        }
        else if (msg.indexOf("没有余票") > -1 || msg.indexOf("此车次无票") > -1 || msg.indexOf("已无余票") > -1
                || msg.indexOf("没有足够的票") > -1 || msg.indexOf("余票不足") > -1 || msg.indexOf("非法的席别") > -1
                || msg.indexOf("排队人数过多") > -1 || msg.indexOf("排队人数现已超过余票数") > -1 || msg.indexOf("在12306未获取到车次") > -1
                || msg.indexOf("12306排队") > -1) {
            code = 301;
            msg = "没有余票";
        }
        else if (msg.indexOf("其他订单行") > -1 || msg.indexOf("本次购票行程冲突") > -1 || msg.indexOf("已购买") > -1) {
            code = 310;
        }
        else if (msg.indexOf("已订") > -1) {
            code = 305;
            msg = "乘客已经预订过该车次";
        }
        else if (msg.indexOf("当前提交订单用户过多 ") > -1 || msg.indexOf("提交订单失败：包含排队中的订单") > -1) {
            code = 307;
            msg = "当前提交订单用户过多 ";
        }
        else if (msgType == 1 && msg.indexOf("身份") > -1) {
            code = 308;
            try {
                msg = "乘客身份信息未通过验证_订票失败  " + msg.split(":")[1];
            }
            catch (Exception e) {
                msg = "乘客身份信息未通过验证_订票失败  ";
            }
        }
        else if (msg.indexOf("距离开车时间太近") > -1) {
            if (msgType == 1) {
                code = 700;
                msg = "距离开车时间太近";
            }
            else if (msgType == 2) {
                code = 1002;
                msg = "距离开车时间太近无法改签";
            }
        }
        else if (msg.indexOf("限制高消费") > -1) {
            code = 313;
        }
        else if (msgType == 2 && accountIdentityCheck(msg)) {
            code = 998;
        }
        else if (msgType == 2 && msg.indexOf("取消次数过多") > -1) {
            code = 1004;
            msg = "取消改签次数超过上限，无法继续操作";
        }
        else if (msgType == 2 && msg.startsWith("车次") && msg.endsWith("点起售")) {
            code = 301;
        }
        else if (msgType == 2 && (msg.contains("手机核验") || msg.contains("手机双向核验") || msg.contains("手机进行双向核验"))) {
            code = 314;
        }
        else if (ElongHotelInterfaceUtil.StringIsNull(msg) || msg.indexOf("validatorMessage") > -1) {
            if (msgType == 1) {
                msg = "订单失败";
            }
            else if (msgType == 2) {
                msg = "改签失败";
            }
        }
        if (msgType == 1) {
            msg = geturlencode(msg);
        }
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        return jsonObject;
    }

    /**
     * 账号身份未核验
     */
    private static boolean accountIdentityCheck(String result) {
        boolean error = false;
        //为空
        if (ElongHotelInterfaceUtil.StringIsNull(result)) {
            return error;
        }
        //真实资料
        boolean realInfo = result.contains("根据本网站的服务条款，您需要提供真实、准确的本人资料")
                || result.contains("根据本网站服务条款，您需要提供真实、准确的本人资料");
        //身份核验
        boolean identityCheck = result.contains("请您尽快到就近的办理客运售票业务的铁路车站完成身份核验")
                || result.contains("请您到就近办理客运售票业务的铁路车站完成身份核验") || result.contains("请您到就近的办理客运售票业务的铁路车站完成身份核验");
        //账号被封
        if (realInfo && identityCheck) {
            error = true;
        }
        else if (result.contains("您的账号尚未通过身份信息核验")) {
            error = true;
        }
        else if (result.contains("未能通过国家身份信息管理权威部门核验")) {
            error = true;
        }
        else if (result.contains("不能给证件类型为身份证的乘客办理购票业务")) {
            error = true;
        }
        else if (result.contains("用户信息被他人冒用，请重新在网上注册新的账户")) {
            error = true;
        }
        else if (result.contains("请您到就近办理客运售票业务的铁路车站完成注册用户身份核验")) {
            error = true;
        }
        return error;
    }

    /**
     * encode
     * @param oldstring
     * @return
     */
    private static String geturlencode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    /**
     * 改签是否未超时（淘宝逻辑,如果不是淘宝订单 全部返回没有超时）
     * @param trainorder
     * @param reqType
     * @param trainorderchange
     * @return
     * @author fiend
     */
    public static boolean isEnChangeTimeOut(Trainorder trainorder, int reqType, Trainorderchange trainorderchange) {
        if (trainorder.getInterfacetype() != null && TrainInterfaceMethod.TAOBAO == trainorder.getInterfacetype()) {
            try {
                if (trainorderchange.getChangetimeout() != null) {
                    //淘宝超时时间
                    long changetimeout = trainorderchange.getChangetimeout().getTime();
                    //如果是请求改签
                    if (reqType == 1) {
                        //预留时间
                        long timeout_time = 15 * 60 * 1000;
                        //                        String change_order_timeout_time_Str = getSysconfigString("change_order_timeout_time");
                        String change_order_timeout_time_Str = PropertyUtil.getValue("change_order_timeout_time",
                                "Train.properties");
                        if (!ElongHotelInterfaceUtil.StringIsNull(change_order_timeout_time_Str)) {
                            timeout_time = Long.valueOf(change_order_timeout_time_Str) * 60 * 1000;
                        }
                        //当前时间+预留时间 <淘宝超时时间 可以继续改签流程
                        if ((System.currentTimeMillis() + timeout_time) < changetimeout) {
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                    //如果是确认改签
                    else if (reqType == 2) {
                        //预留时间
                        long timeout_time = 5 * 60 * 1000;
                        String change_confirm_timeout_time_Str = PropertyUtil.getValue("change_confirm_timeout_time",
                                "Train.properties");
                        //                        String change_confirm_timeout_time_Str = getSysconfigString("change_confirm_timeout_time");
                        if (!ElongHotelInterfaceUtil.StringIsNull(change_confirm_timeout_time_Str)) {
                            timeout_time = Long.valueOf(change_confirm_timeout_time_Str) * 60 * 1000;
                        }
                        //当前时间+预留时间 <淘宝超时时间 可以继续改签流程
                        if ((System.currentTimeMillis() + timeout_time) < changetimeout) {
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                    return true;
                }
                else {
                    return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        else {
            return true;
        }
    }

    /**
     * 判断是不是网站客户
     * wzc
     */
    public static boolean getMobileFlag(String mobile, String loginpassword) {
        if (mobile != null && loginpassword != null && !mobile.equals("") && !loginpassword.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 获取网站用户
     * @param username
     * @param password
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Customeruser getLoginUser(String username, String password, long agentid) {
        Customeruser customeruser = null;
        String where = " where " + Customeruser.COL_loginname + " = '" + username + "' and " + Customeruser.COL_agentid
                + "=" + agentid;
        List<Customeruser> list = new ArrayList<Customeruser>();
        try {
            list = Server.getInstance().getMemberService().findAllCustomeruser(where, "", 1, 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            customeruser = list.get(0);
            try {
                if (MD5Util.MD5Encode(password, "utf-8").equals(list.get(0).getLogpassword())) {
                    return customeruser;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
