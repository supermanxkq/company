package com.ccservice.b2b2c.atom.servlet.QunarTrain;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.Trainform;

public class QunarCancelTrain extends TongchengSupplyMethod {
    public String operate(String orderNo, String reqFrom, String reqTime, String HMAC, int r1, String key) {
        JSONObject retobj = new JSONObject();
        //存在空值
        if (ElongHotelInterfaceUtil.StringIsNull(orderNo) || ElongHotelInterfaceUtil.StringIsNull(reqFrom)
                || ElongHotelInterfaceUtil.StringIsNull(reqTime) || ElongHotelInterfaceUtil.StringIsNull(HMAC)) {
            retobj.put("errCode", "111");
            retobj.put("ret", false);
            retobj.put("errMsg", "业务参数缺失");

            return retobj.toJSONString();
        }
        try {
            String hmac = ElongHotelInterfaceUtil.MD5(key + orderNo + reqFrom + reqTime).toUpperCase();
            if (!hmac.equals(HMAC)) {
                retobj.put("errCode", "111");
                retobj.put("ret", false);
                retobj.put("errMsg", "传输加密有误");
                return retobj.toJSONString();
            }
        }
        catch (Exception e1) {
            e1.printStackTrace();
            retobj.put("errCode", "111");
            retobj.put("ret", false);
            retobj.put("errMsg", "业务参数有误");
            return retobj.toJSONString();
        }
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderNo);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        //订单不存在
        if (orders == null || orders.size() != 1) {
            retobj.put("errCode", "111");
            retobj.put("ret", false);
            retobj.put("errMsg", "订单不存在");
            return retobj.toJSONString();
        }
        Trainorder order = orders.get(0);
        //订单状态
        int status = order.getOrderstatus();
        if (status != Trainorder.WAITPAY) {
            retobj.put("errCode", "112");
            retobj.put("ret", false);
            retobj.put("errMsg", "订单状态不正确");
            return retobj.toJSONString();
        }
        //12306订单号为空
        String extnumber = order.getExtnumber();
        WriteLog.write("QUNAR火车票接口_取消占座", r1 + ":extnumber:" + extnumber);
        if (ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
            retobj.put("errCode", "111");
            retobj.put("ret", false);
            retobj.put("errMsg", "处理失败");
            return retobj.toJSONString();
        }
        //加载其他字段、乘客
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());
        //下单账户
        String createAccount = order.getSupplyaccount();
        WriteLog.write("QUNAR火车票接口_取消占座", r1 + ":createAccount:" + createAccount);
        if (ElongHotelInterfaceUtil.StringIsNull(createAccount)) {
            retobj.put("errCode", "111");
            retobj.put("ret", false);
            retobj.put("errMsg", "处理失败");
            return retobj.toJSONString();
        }
        Customeruser user = getCustomeruserBy12306Account(order, r1, true);
        WriteLog.write("QUNAR火车票接口_取消占座", r1 + ":user.getCardnunber():" + (user == null ? "" : user.getCardnunber()));
        if (user == null || ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //账号系统，以未登录释放账号
            if (user != null && user.isFromAccountSystem()) {
                freeCustomeruser(user, AccountSystem.FreeNoLogin, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            retobj.put("errCode", "111");
            retobj.put("ret", false);
            retobj.put("errMsg", "处理失败");
            return retobj.toJSONString();
        }
        boolean cancelTrue = false;
        //请求12306
        String url = "";
        String result = "";
        try {
            String cookie = user.getCardnunber();
            RepServerBean rep = RepServerUtil.getRepServer(user, false);
            url = rep.getUrl();
            String param = "datatypeflag=10&cookie=" + cookie + "&extnumber=" + extnumber + "&trainorderid="
                    + order.getId() + JoinCommonAccountInfo(user, rep);
            result = SendPostandGet.submitPost(url, param, "UTF-8").toString();
            if (result.contains("取消订单成功") || "无未支付订单".equals(result)) {
                cancelTrue = true;
            }
        }
        catch (Exception e) {
            result += ">>>>>Exception>>>>>" + e.getMessage();
        }
        WriteLog.write("QUNAR火车票接口_取消占座", order.getId() + ">>>>>REP服务器地址>>>>>" + url + ">>>>>REP返回>>>>>" + result);
        //取消失败
        if (!cancelTrue) {
            if (user.isFromAccountSystem()) {
                freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                        AccountSystem.NullDepartTime);
            }
            retobj.put("errCode", "111");
            retobj.put("ret", false);
            retobj.put("errMsg", "处理失败");
            return retobj.toJSONString();
        }
        try {
            freeCustomeruser(user, AccountSystem.FreeNoCare, AccountSystem.TwoFree, AccountSystem.OneCancel,
                    AccountSystem.NullDepartTime);
            //本地取消
            order.setSupplyprice(0f);
            order.setSupplypayway(100);
            order.setSupplyaccount("");
            order.setSupplytradeno("");
            order.setExtnumber("");
            order.setChangesupplytradeno("");
            order.setOrderstatus(Trainorder.CANCLED);
            order.setState12306(Trainorder.ORDERFALSE);
            Server.getInstance().getTrainService().updateTrainorder(order);
            //日志
            Trainorderrc rz = new Trainorderrc();
            rz.setYwtype(1);
            rz.setCreateuser("系统接口");
            rz.setOrderid(order.getId());
            rz.setContent("接口申请取消订单，12306取消<span style='color:red;'>成功</span>，交易关闭。");
            Server.getInstance().getTrainService().createTrainorderrc(rz);
            //文件日志
            WriteLog.write("QUNAR火车票接口_取消占座", "订单号：" + order.getId() + "：取消订单，消除订单支付信息.");
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + " QUNAR接口取消订单，订单ID：" + order.getId() + "，"
                    + e.getMessage());
        }
        retobj.put("ret", true);
        //返回
        return retobj.toJSONString();
    }
}