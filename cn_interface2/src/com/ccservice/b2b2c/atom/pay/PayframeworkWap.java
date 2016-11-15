package com.ccservice.b2b2c.atom.pay;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;

/**
 * 手机端支付
 * @author wzc
 * @version 创建时间：2015年7月20日 下午6:04:57
 */
@SuppressWarnings("serial")
public class PayframeworkWap extends HttpServlet {

    Map<String, InterfaceAccount> interfaceAccountMap;

    @Override
    public void init() throws ServletException {
        interfaceAccountMap = new HashMap<String, InterfaceAccount>();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        try {
            String param = "";
            String result = "";
            String partnerid = "";
            param = request.getParameter("params");
            JSONObject json = JSONObject.parseObject(param);
            //                    //客户端账号
            //                    String partnerid = json.containsKey("partnerid") ? json.getString("partnerid") : "";
            //请求时间
            String reqtime = json.containsKey("reqtime") ? json.getString("reqtime") : "";
            //数字签名
            String sign = json.containsKey("sign") ? json.getString("sign") : "";
            //请求方法
            String method = json.containsKey("method") ? json.getString("method") : "";
            partnerid = json.containsKey("username") ? json.getString("username") : "";//传过来的partnerid

            //-----加缓存机制不用每次都去数据库查-----S
            //chendong 2015年4月11日19:18:11
            InterfaceAccount interfaceAccount = interfaceAccountMap.get(partnerid);
            //if (interfaceAccount == null) {
            interfaceAccount = getInterfaceAccountByLoginname(partnerid);
            if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                    && interfaceAccount.getInterfacetype() != null) {
                interfaceAccountMap.put(partnerid, interfaceAccount);
            }
            //}
            //-----加缓存机制不用每次都去数据库查-----E
            String key = interfaceAccount.getKeystr();
            //============================================================
            //判断签名，数字签名=md5(partnerid+method+reqtime+md5(key))
            WriteLog.write("Payframework", "param:" + param);
            String signflag = ElongHotelInterfaceUtil.MD5(partnerid + method + reqtime
                    + ElongHotelInterfaceUtil.MD5(key));
            if (signflag.equals(sign)) {
                String payname = "WapAlipay";// request.getParameter("payname");
                WriteLog.write("Payframework", "WapAlipay");
                String helpername = json.getString("helpername");
                String orderidstr = json.containsKey("orderid") ? json.getString("orderid") : "";
                long orderid = 0;
                if (orderidstr == null || "".equals(orderidstr)) {
                    String refordernumber = json.containsKey("refordernumber") ? json.getString("refordernumber") : "";
                    orderid = getOrderIdByRef(refordernumber, interfaceAccount.getAgentid());
                }
                else {
                    orderid = IsAgentOrder(orderidstr, interfaceAccount.getAgentid());
                }
                if (orderid == 0) {
                    PrintWriter out = null;
                    out = response.getWriter();
                    JSONObject obj = new JSONObject();
                    obj.put("success", false);
                    obj.put("code", "105");
                    obj.put("msg", "未查到订单");
                    result = obj.toString();
                    out.print(result);
                    out.flush();
                    out.close();
                }
                WriteLog.write("Payframework", payname + ":" + helpername);
                Payhelper payhelper = (Payhelper) Class
                        .forName(Payhelper.class.getPackage().getName() + "." + helpername).getConstructor(long.class)
                        .newInstance(orderid);
                String className1 = Pay.class.getPackage().getName() + "." + payname;
                Pay pay = (Pay) Class.forName(className1)
                        .getConstructor(HttpServletRequest.class, HttpServletResponse.class, Payhelper.class)
                        .newInstance(request, response, payhelper);
                pay.pay(interfaceAccount.getAgentid());
            }
            else {
                PrintWriter out = null;
                out = response.getWriter();
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("code", "105");
                obj.put("msg", "签名错误");
                result = obj.toString();
                out.print(result);
                out.flush();
                out.close();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("Payframework", "" + e.getMessage());
        }

    }

    /**
     * 判断该订单是否是供应商所属订单
     * @param id
     * @param agentid
     * @return
     */
    public long IsAgentOrder(String id, long agentid) {
        String sql = "select id from t_trainorder with(nolock) where C_AGENTID=" + agentid + " and  C_CREATETIME>'"
                + TimeUtil.gettodaydate(1) + "' and ID='" + id + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        long orderid = 0;
        if (list.size() > 0) {
            orderid = Long.valueOf(((Map) list.get(0)).get("id").toString());
            return orderid;
        }
        return orderid;

    }

    /**
     * 
     * @return
     */
    public long getOrderIdByRef(String refordernumber, long agentid) {
        String sql = "select id from t_trainorder with(nolock) where C_AGENTID=" + agentid + " and  C_CREATETIME>'"
                + TimeUtil.gettodaydate(1) + "' and C_QUNARORDERNUMBER='" + refordernumber + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        long orderid = 0;
        if (list.size() > 0) {
            orderid = Long.valueOf(((Map) list.get(0)).get("id").toString());
            return orderid;
        }
        return orderid;
    }

    public static void main(String[] args) {
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        list_interfaceAccount = Server.getInstance().getMemberService()
                .findAllInterfaceAccount("where C_USERNAME = 'tongcheng_train_test'", null, -1, 0);
        System.out.println(list_interfaceAccount);
    }

    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    private InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);

        }
        catch (Exception e) {
        }
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        else {
            interfaceAccount.setUsername(loginname);
            interfaceAccount.setKeystr("-1");
            interfaceAccount.setInterfacetype(TrainInterfaceMethod.TONGCHENG);
        }
        return interfaceAccount;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.doGet(request, response);
    }

}
