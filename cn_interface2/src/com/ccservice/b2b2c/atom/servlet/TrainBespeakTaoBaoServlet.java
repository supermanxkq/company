package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;

/**
 * Servlet implementation class TrainBespeakTaoBaoServlet
 */
public class TrainBespeakTaoBaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;



    /**
     * @see HttpServlet#HttpServlet()
     */
    public TrainBespeakTaoBaoServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        WriteLog.write("淘宝抢票下单1", "进入开启---");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        String url = "http://121.41.171.147:30002/cn_interface/TrainBespeakTaoBaoCallBackServlet";
        String orderId = request.getParameter("orderid");
        String flag = request.getParameter("flag");
        String refuseMsg = request.getParameter("refuseMsg");
        refuseMsg = URLDecoder.decode(refuseMsg, "utf-8");
        WriteLog.write("淘宝抢票下单1", "数据------>订单号：" + orderId + "flag：" + flag + "原因：" + refuseMsg);
        String result = "";
        String sql1 = "update T_TRAINORDER set C_INTERFACETYPE=6,ordertype=5 where ID=" + orderId;
        WriteLog.write("淘宝抢票下单1", "数据------>订单号：" + orderId + "sql:"+sql1);
        Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
        String sql = "select * from T_TRAINORDER where ID=" + orderId;
        List list = null;
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        WriteLog.write("淘宝抢票下单1", "订单号：" + orderId + "查询：" + list);
        Map map = null;
        Map ma = null;
        String obj = "";
        int i = 0;
        String rest = "";
        JSONObject exception = new JSONObject();
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        String orderNum = map.get("C_QUNARORDERNUMBER").toString();
        int ordertype = Integer.parseInt(map.get("ordertype").toString());
        String FailMsg = refuMsg(refuseMsg, ordertype);
        WriteLog.write("淘宝抢票下单1", "失败原因转换：refuseMsg=" + refuseMsg + "----->" + FailMsg);
        if (flag.equals("true")) {
            WriteLog.write("淘宝抢票下单1", "进入淘宝锁单，订单号：orderNum=" + orderNum);
            String suodan = "";
            try {
                suodan = SendPostandGet.submitPost(url,
                        "orderid=" + orderNum + "&&refuseMsg=" + FailMsg + "&&method=suodan", "utf-8").toString();
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }//锁单
            WriteLog.write("淘宝抢票下单1", "淘宝锁单：" + suodan);
            if (suodan.equals("success")) {//锁单成功
                //走空铁支付
                    WriteLog.write("淘宝抢票下单1", "进入淘宝支付，订单号：orderId=" + orderId);
                    String urls="http://120.26.223.234:19701/ticket_inter/TrainTaoBaoBespeakPay";
                    try {
                        WriteLog.write("淘宝抢票下单1", "即将开始请求淘宝抢票支付:" + urls+"?"+"orderId=" + orderId + "&urltype=1");
                        String pays = SendPostandGet.submitPost(urls,
                                "orderId=" + orderId + "&urltype=1", "utf-8").toString();
                        WriteLog.write("淘宝抢票下单1", "请求淘宝抢票支付结束:" + pays);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                exception.put("success", true);
                exception.put("msg", "淘宝抢票锁单成功！");
                result = exception.toString();
            }
            else {
                exception.put("success", false);
                exception.put("msg", "淘宝接口锁单失败，错误信息：");
                result = exception.toString();
            }
        }
        else {//失败,返回淘宝
            try {
                WriteLog.write("淘宝抢票下单1", "请求147参数值:" + url+"?"+"orderid=" + orderId + "&refuseMsg=" + FailMsg + "&method=callbackMsg");
                rest = SendPostandGet.submitPost(url,
                        "orderid=" + orderId + "&refuseMsg=" + FailMsg + "&method=callbackMsg", "utf-8").toString();
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            WriteLog.write("淘宝抢票下单1", "请求淘宝参数值:" + result);
            if (rest.equals("success")) {
                exception.put("success", true);
                exception.put("msg", "淘宝抢票失败回调成功！");
                result = exception.toString();
            }
            else if (rest.equals("error")) {
                exception.put("success", true);
                exception.put("msg", "淘宝抢票失败回调成功！该订单不存在！");
                result = exception.toString();
            }
            else {
                result = rest;
            }
        }
        WriteLog.write("淘宝抢票下单1", "返回信息:" + result);
        PrintWriter out = null;
        out = response.getWriter();
        out.print(result);
        out.flush();
        out.close();
    }

    public String refuMsg(String result, int ordertype) {
        //客人账号
        boolean CustomerAccount = ordertype == 3 || ordertype == 4;
        if (ElongHotelInterfaceUtil.StringIsNull(result)) {
            result = "0";
        }
        else if (result.contains("没有足够的票") || result.contains("已无余票") || result.contains("余票不足")
                || result.contains("排队人数过多") || result.contains("排队人数现已超过余票数")
                || (result.contains("在12306未获取到车次") && result.contains("车票预订查询结果")) || result.contains("12306排队")) {
            result = "1";
        }
        else if (result.contains("与12306票价不符")) {
            result = "2";
        }
        else if (result.contains("行程冲突")) {//得放在已订和已购买前边
            result = "13";
        }
        else if (result.contains("已订") || result.contains("已购买")) {
            result = "3";
        }
        else if (result.contains("超时") || result.contains("多次打码失败")) {
            result = "4";
        }

        /********************客人账号开始********************/
        //用户12306账号登录失败
        else if (CustomerAccount && (result.contains("未登录") || result.contains("用户12306账号登录失败"))) {
            result = "15";
        }
        //12306账号存在未支付订单
        else if (CustomerAccount
                && (result.contains("存在未完成订单") || result.contains("尚有订单未支付") || result.contains("包含未付款订单"))) {
            result = "16";
        }
        //用户常旅客已满
        else if (CustomerAccount && result.contains("已满")) {
            result = "17";
        }
        //Session登录失败
        else if (CustomerAccount && result.contains("获取下单账户失败")) {
            result = "21";
        }
        //用户已在其它地方登录
        else if (CustomerAccount && result.contains("用户已在其他地点登录")) {
            result = "22";
        }
        //帐号手机未核验
        else if (CustomerAccount
                && (result.contains("手机核验") || result.contains("手机双向核验") || result.contains("手机进行双向核验"))) {
            result = "23";
        }
        //账号取消订单次数达到上限
        else if (CustomerAccount && result.contains("取消次数过多")) {
            result = "24";
        }
        //帐号持有人身份未核验
        else if (CustomerAccount && accountIdentityCheck(result)) {
            result = "25";
        }
        /********************客人账号结束********************/

        else if (result.contains("身份信息涉嫌被他人冒用")) {
            result = "10";
        }
        else if (result.contains("不能购买学生票")) {
            result = "9";
        }
        else if (result.contains("身份") || result.contains("待核验")) {
            result = "5";
        }
        else if (result.contains("发车时间变动")) {
            result = "6";
        }
        else if (result.contains("车次信息变更")) {
            result = "7";
        }
        else if (result.contains("网络繁忙") || result.contains("系统忙") || result.contains("查询失败")
                || result.contains("当前提交订单用户过多") || result.contains("您的操作频率过快")) {
            result = "8";
        }
        else if (result.contains("高消费")) {
            result = "11";
        }
        else if (result.contains("坐票已售完")) {
            result = "12";
        }
        else if (result.contains("预售期变更")) {
            result = "14";
        }
        else if (result.contains("乘客信息有误") || result.contains("非法的证件类型")) {
            result = "18";
        }
        else if (result.contains("非法的席别") || result.contains("座席编码为空")) {
            result = "19";
        }
        else if (result.contains("车次停运") || result.contains("列车运行图调整,暂停发售")) {
            result = "20";
        }
        else if (result.contains("下单失败")) {
            result = "8";
        }
        else {
            result = "0";
        }

        return result;
    }

    /**
     * 针对淘宝客人账号身份未核验
     */
    private boolean accountIdentityCheck(String result) {
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
        else if (result.contains("请您到就近办理客运售票业务的铁路车站完成注册用户身份核验")) {
            error = true;
        }
        return error;
    }

}
