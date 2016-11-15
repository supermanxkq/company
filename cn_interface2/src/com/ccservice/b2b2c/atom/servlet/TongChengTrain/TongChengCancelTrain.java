package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 火车票取消订单
 * method --> train_cancel
 * @author WH
 */

public class TongChengCancelTrain extends TrainSelectLoginWay {

    @SuppressWarnings("rawtypes")
    public String operate(JSONObject json, int random) {
        JSONObject retobj = new JSONObject();
        //同程订单号
        String orderid = json.containsKey("orderid") ? json.getString("orderid") : "";
        //交易单号
        String transactionid = json.containsKey("transactionid") ? json.getString("transactionid") : "";
        String mobile = json.containsKey("mobile") ? json.getString("mobile") : "";
        String loginpassword = json.containsKey("loginpassword") ? json.getString("loginpassword") : "";//登录密码
        if (TrainSupplyMethodUtil.getMobileFlag(mobile, loginpassword)) {
            String partnerid = json.getString("partnerid");// 传过来的partnerid
            String agentid = gettongchengagentid(partnerid);
            Customeruser currentuser = TrainSupplyMethodUtil.getLoginUser(mobile, loginpassword, Long.valueOf(agentid));
            if (currentuser == null) {
                retobj.put("code", "103");
                retobj.put("success", "false");
                retobj.put("msg", "未查到用户");
                return retobj.toString();
            }
        }

        //存在空值
        if (ElongHotelInterfaceUtil.StringIsNull(orderid) || ElongHotelInterfaceUtil.StringIsNull(transactionid)) {
            retobj.put("success", false);
            retobj.put("code", "107");
            retobj.put("msg", "业务参数缺失");
            return retobj.toJSONString();
        }
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        Trainorder order = new Trainorder();
        //订单不存在
        if (orders == null || orders.size() == 0) {
            retobj.put("success", false);
            retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            return retobj.toJSONString();
        }
        else if (orders.size() > 0) {
            order = orders.get(0);
        }
        if (order.getId() == 0) {
            retobj.put("success", false);
            retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            return retobj.toJSONString();
        }
        boolean success = false;
        String msg = "";
        String code = "";
        //订单状态
        int status = order.getOrderstatus();
        int state12306 = order.getState12306();
        boolean cancelTrue = false;//是否在12306成功取消
        boolean accountNoLogin = false;//账号未登录
        WriteLog.write("t同程火车票接口_4.7取消火车票订单-test",
                random + ">>>>>" + order.getId() + ":order:" + JSONObject.toJSONString(order));
        if ((status > 1 && status < 8) || (state12306 >= 5 && state12306 <= 8)) {
            retobj.put("success", false);
            retobj.put("code", "112");
            retobj.put("msg", "该订单状态下,不能取消");
            msg = "该订单状态下,不能取消";
            code = "112";
            //            return retobj.toJSONString();
        }
        else if (status == 1) {
            String pingtaiStr = PropertyUtil.getValue("default_pingtaiStr", "Train.properties");
            if ("tc".equals(pingtaiStr)) {
                if (state12306 == 1) {
                    String sql = "UPDATE T_TRAINORDER SET C_ORDERSTATUS=8 WHERE ID=" + order.getId();
                    int res = Server.getInstance().getSystemService().excuteGiftBySql(sql);
                    WriteLog.write("tc同程取消订单-test", "订单号:" + order.getId() + ":订单未与12306产生交互同程发起取消:修改DB:" + res + ":"
                            + sql);
                    retobj.put("success", true);
                    retobj.put("code", "100");
                    retobj.put("msg", "取消订单成功");
                    return retobj.toJSONString();
                }
                else if (state12306 == 2 || state12306 == 3 || state12306 == 18) {
                    try {
                        Server.getInstance().getSystemService()
                                .findMapResultByProcedure(" [sp_TrainorderCanceling_Insert] @OrderId=" + order.getId());
                        WriteLog.write("同程取消请求-test", "请求单号---->" + orderid + "--->取消结果入库");
                        //                        if (insertresult == 1) {
                        retobj.put("success", true);
                        retobj.put("code", "502");
                        retobj.put("msg", "取消请求已接收,占座中");
                        //占座中的话 先不回调
                        retobj.put("isDisCallBack", true);
                        Trainorderrc rz = new Trainorderrc();
                        rz.setYwtype(1);
                        rz.setCreateuser("系统接口");
                        rz.setOrderid(order.getId());
                        rz.setStatus(Trainorder.CANCLED);
                        rz.setContent("接口申请取消订单,取消请求已接收,占座中。");
                        Server.getInstance().getTrainService().createTrainorderrc(rz);
                        return retobj.toString();
                        //                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        WriteLog.write("同程取消请求_ERROR", "请求单号---->" + orderid + "--->取消结果入库失败");
                        ExceptionUtil.writelogByException("同程取消请求_ERROR", e);
                        retobj.put("success", true);
                        retobj.put("code", "999");
                        retobj.put("msg", "取消订单失败>>>执行错误");
                        return retobj.toString();
                    }
                }
            }
            Map map = getTrainorderstatus(order.getId());

            //12306订单号为空
            //        String extnumber = order.getExtnumber();
            String extnumber = gettrainorderinfodatabyMapkey(map, "C_EXTNUMBER");
            String ordertype = gettrainorderinfodatabyMapkey(map, "ordertype");
            //下单账户
            //            String createAccount = order.getSupplyaccount();
            String createAccount = gettrainorderinfodatabyMapkey(map, "C_SUPPLYACCOUNT");
            //日志
            WriteLog.write("t同程火车票接口_4.7取消火车票订单-test", random + ">>>>>" + order.getId() + ">>>>>" + extnumber + ">>>>>"
                    + createAccount + ">>>>>>>ordertype:" + ordertype);
            //账户存在、电子单号存在
            if ((!ElongHotelInterfaceUtil.StringIsNull(createAccount) || "3".equals(ordertype) || "4".equals(ordertype))
                    && !ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
                //保存
                saveThirdAccountInfo(order.getId(), json);
                WriteLog.write("t同程火车票接口_4.7取消火车票订单-test", "orderid:" + order.getQunarOrdernumber());
                //查询账户
                order.setSupplyaccount(createAccount);
                if ("3".equals(ordertype) || "4".equals(ordertype)) {
                    order.setOrdertype(Integer.valueOf(ordertype));
                }
                Customeruser user = getCustomeruserBy12306Account(order, random, true);
                //账号名不存在、密码错误等，不登录重试，针对第三方传账号和密码，防止重试锁账号等
                if (user != null && user.isDontRetryLogin()) {
                    retobj.put("success", false);
                    retobj.put("code", "999");
                    retobj.put("msg", "取消订单失败>>>" + user.getNationality());
                    return retobj.toJSONString();
                }
                //记录日志
                WriteLog.write("t同程火车票接口_4.7取消火车票订单-test", random + ">>>>>" + order.getId() + ">>>>>Cookie>>>>>"
                        + (user == null ? "" : user.getCardnunber()));
                //未获取到账号或Cookie为空
                if (user == null || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
                    //账号系统，以未登录释放账号
                    if (user != null && user.isFromAccountSystem()) {
                        freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree,
                                AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                    }
                    retobj.put("success", false);
                    retobj.put("code", "999");
                    retobj.put("msg", "取消订单失败");
                    return retobj.toJSONString();
                }
                //请求12306
                String url = "";
                String result = "";
                boolean isPhone = isPhoneCancelTrainOrder();

                if (isPhone) {
                    try {
                        DataTable dataTable = DBHelperAccount.GetDataTable("exec [sp_Customeruser_Phone_Select] @Id="
                                + user.getId());
                        String sessionId = "";
                        String __wl_deviceCtxSession = "";
                        String cookie = "";
                        String deviceno = "";
                        for (DataRow dataRow : dataTable.GetRow()) {
                            cookie = dataRow.GetColumnString("C_CARDNUNBER");
                            sessionId = dataRow.GetColumnString("C_SESSIONID");
                            __wl_deviceCtxSession = dataRow.GetColumnString("C_WLDEVICECTXSESSION");
                            deviceno = dataRow.GetColumnString("C_DEVICENO");
                        }
                        if (!"".equals(cookie)) {
                            user.setCardnunber(cookie);
                        }
                        user.setSessionid(sessionId);
                        user.setDeviceno(deviceno);
                        user.setWldevicectxsession(__wl_deviceCtxSession);
                        WriteLog.write("取消订单PHONE_去DB中查询手机端数据-test", user.getLoginname() + "--->" + user.getId()
                                + "--->" + cookie + "--->" + sessionId + "--->" + __wl_deviceCtxSession);
                    }
                    catch (Exception e) {
                        ExceptionUtil.writelogByException("取消订单PHONE_ERROR_去DB中查询手机端数据", e);
                    }

                }

                try {
                    url = RepServerUtil.getRepServer(user, false).getUrl();
                    String param = "";
                    if (!isPhone) {
                        param = "datatypeflag=10&cookie=" + user.getCardnunber() + "&extnumber=" + extnumber
                                + "&trainorderid=" + order.getId();
                    }
                    else {
                        param = "datatypeflag=1010&cookie=" + user.getCardnunber() + "&extnumber=" + extnumber
                                + "&accountPhone=" + CommonAccountPhone(user);
                    }
                    result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
                    if (result.contains("取消订单成功") || "无未支付订单".equals(result)) {
                        cancelTrue = true;
                    }
                    else if (Account12306Util.accountNoLogin(result, user)) {
                        accountNoLogin = true;
                    }
                }
                catch (Exception e) {
                    result += ">>>>>Exception>>>>>" + e.getMessage();
                }
                WriteLog.write("t同程火车票接口_4.7取消火车票订单-test", random + ">>>>>" + order.getId() + ">>>>>REP服务器地址>>>>>"
                        + url + ">>>>>REP返回>>>>>" + result);
                //释放账号
                if (cancelTrue) {
                    success = true;
                    code = "100";
                    msg = "取消订单成功";
                    freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.TwoFree, AccountSystem.OneCancel,
                            AccountSystem.NullDepartTime);
                }
                else {
                    if (user.isFromAccountSystem()) {
                        freeCustomeruser(user, accountNoLogin ? AccountSystem.FreeNoLogin : AccountSystem.FreeNoCare,
                                AccountSystem.OneFree, AccountSystem.ZeroCancel, AccountSystem.NullDepartTime);
                    }
                    retobj.put("success", false);
                    retobj.put("code", "999");
                    retobj.put("msg", "取消订单失败");
                    return retobj.toJSONString();
                }
            }
            else {
                retobj.put("success", false);
                retobj.put("code", "999");
                retobj.put("msg", "取消订单失败");
                return retobj.toJSONString();
            }
            try {
                //本地取消
                //                order.setSupplyprice(0f);
                //                order.setSupplypayway(100);
                //                order.setSupplyaccount("");
                //                order.setSupplytradeno("");
                //                order.setExtnumber("");
                //                order.setChangesupplytradeno("");
                //                order.setOrderstatus(Trainorder.CANCLED);
                //                order.setState12306(Trainorder.ORDERFALSE);
                //                Server.getInstance().getTrainService().updateTrainorder(order);
                String sql1 = "update T_TRAINORDER set C_SUPPLYPRICE=0,C_SUPPLYPAYWAY=100,C_SUPPLYTRADENO=''"
                        + ",C_EXTNUMBER='',C_CHANGESUPPLYTRADENO='',C_ORDERSTATUS=" + Trainorder.CANCLED
                        + ",C_STATE12306=" + Trainorder.ORDERFALSE + " WHERE ID=" + order.getId();
                try {
                    int i1 = Server.getInstance().getSystemService().excuteGiftBySql(sql1);
                    WriteLog.write("tc同程取消订单", "订单号:" + order.getId() + ":修改数据库:" + i1 + ":" + sql1);
                }
                catch (Exception e) {
                    logger.error("tongchengtrainorder_updateExtordercreatetime", e.fillInStackTrace());
                }
                //日志
                Trainorderrc rz = new Trainorderrc();
                rz.setYwtype(1);
                rz.setCreateuser("系统接口");
                rz.setOrderid(order.getId());
                rz.setStatus(Trainorder.CANCLED);
                rz.setContent("接口申请取消订单,12306取消<span style='color:red;'>成功</span>,交易关闭。");
                Server.getInstance().getTrainService().createTrainorderrc(rz);
                //文件日志
                WriteLog.write("tc同程取消订单", "订单号:" + order.getId() + ":取消订单,消除订单支付信息.");
            }
            catch (Exception e) {
                System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + " tc接口取消订单,订单ID:" + order.getId() + ","
                        + e.getMessage());
            }
        }
        else {
            success = true;
            code = "100";
            msg = "取消订单成功";
        }
        /*retobj.put("success", true);
        retobj.put("code", "100");
        retobj.put("msg", "取消订单成功");
        retobj.put("orderid", orderid);*/
        retobj.put("success", success);
        retobj.put("code", code);
        retobj.put("msg", msg);
        retobj.put("orderid", orderid);
        //返回
        return retobj.toJSONString();
    }

    @SuppressWarnings("rawtypes")
    public boolean isPhoneCancelTrainOrder() {
        try {
            List list = Server.getInstance().getSystemService()
                    .findMapResultByProcedure("sp_TrainOrderIsPhone_Select_CancelTrainOrder");
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                if ("1".equals(map.get("IsPhone").toString())) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("PHONE_ERROR_isPhoneOrder", e);
        }
        return false;
    }

    /**
     * 请求REP账号手机端数据
     */
    public String CommonAccountPhone(Customeruser user) {
        try {
            JSONObject obj = new JSONObject();
            //账号相关
            obj.put("account", user.getLoginname());
            obj.put("password", user.getLogpassword());
            obj.put("Cookie", user.getCardnunber());
            obj.put("WL-Instance-Id", user.getSessionid());
            obj.put("__wl_deviceCtxSession", user.getWldevicectxsession());
            obj.put("baseDTO.device_no", user.getDeviceno());
            obj.put("startTime", System.currentTimeMillis());
            return URLEncoder.encode(obj.toString(), "UTF-8");
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("取消订单PHONE_ERROR_请求REP账号手机端数据", e);
            return "";
        }
    }

}