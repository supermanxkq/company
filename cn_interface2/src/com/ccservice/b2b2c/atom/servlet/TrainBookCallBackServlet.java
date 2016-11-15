package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.dnsmaintenance.Dnsmaintenance;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.ben.Trainform;

/**
 * 占座完回调
 * @author wzc
 *
 */

public class TrainBookCallBackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {}

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        String JsonStr = req.getParameter("jsonStr");//处理业务数据
        WriteLog.write("火车票短信", "jsonstr:" + JsonStr);
        if (JsonStr != null && JsonStr.length() > 0) {
            JSONObject json = JSONObject.parseObject(JsonStr);
            boolean ordersuccess = json.getBoolean("ordersuccess");
            if (ordersuccess) {
                String transactionid = json.getString("transactionid");//交易单号
                Trainform form = new Trainform();
                form.setOrdernumber(transactionid);
                List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(form, null);
                try {
                    if (orders != null && orders.size() > 0) {
                        Trainorder trainorder = Server.getInstance().getTrainService()
                                .findTrainorder(orders.get(0).getId());
                        if (trainorder != null) {
                            sendmessage(trainorder);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            out.print("success");
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 转换null
     * 
     * @param <T>
     * @param t
     * @param v
     * @return
     */
    public <T> T converNull(T t, T v) {
        if (t != null) {
            return t;
        }
        return v;
    }

    /**
     * 从map转换为对象
     * 
     * @param <T>
     * @param t
     * @param map
     * @return tt
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     */
    public <T> T setFiledfrommap(Class t, Map map)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, InstantiationException, NoSuchFieldException {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        T tt = (T) t.newInstance();
        for (Map.Entry<String, String> entry = null; iterator.hasNext();) {
            entry = iterator.next();
            String paraname = entry.getKey();
            Object val = entry.getValue();
            paraname = paraname.substring(0, 1).toUpperCase() + paraname.substring(1);
            Method getm = t.getMethod("get" + paraname, null);
            String type = getm.getReturnType().getSimpleName();
            if (type.equals("Integer") || type.equals("int")) {
                try {
                    val = Integer.valueOf(val.toString());
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    val = 0;
                }

            }
            else if (type.equals("long") || type.equals("Long")) {
                try {
                    val = Long.valueOf(converNull(val, '0').toString());
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    val = 0l;
                }

            }
            else if (type.equals("float") || type.equals("Float")) {
                try {
                    val = Float.valueOf(val.toString());
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    val = 0f;
                }
            }
            else if (type.equals("byte") || type.equals("Byte")) {
                try {
                    val = Byte.valueOf(val.toString());
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    val = 0f;
                }
            }
            Method method = t.getMethod("set" + paraname, getm.getReturnType());

            method.invoke(tt, val);

        }
        return tt;
    }

    /**
     * 根据类型，sql 查询 获取所需信息
     * 
     * @param <T>
     * @param cls
     * @param sql
     * @return
     */
    public <T> T findBySql(Class<T> cls, String sql) {
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map m = (Map) list.get(0);
            try {
                return (T) this.setFiledfrommap(cls, m);
            }
            catch (Exception e) {
                return null;
            }
        }
        return null;

    }

    /**
     * 根据agentid获取这个代理所属的DNS
     * @param agentid 要查的agentid
     * @return Dnsmaintenance
     */
    public Dnsmaintenance getSmsDnsByAgentid(long agentid) {
        String sql = "SELECT  C_AGENTID agentid, C_B2BSMSCOUNTER smscounter,C_B2BSMSPWD smspwd,"
                + "C_AGENTSMSENABLE agentsmsenable,C_COMPANYNAME companyname,ID id FROM T_DNSMAINTENANCE WHERE C_AGENTID= "
                + agentid
                + "OR CHARINDEX(','+CONVERT(NVARCHAR,C_AGENTID)+',',(SELECT ','+C_PARENTSTR+',' FROM T_CUSTOMERAGENT WHERE ID="
                + agentid + "))>0 " + "ORDER BY C_AGENTID DESC";
        return this.findBySql(Dnsmaintenance.class, sql);
    }

    /**
     * @param str
     * @return 是否为null或""
     */
    public boolean isNotNullOrEpt(String str) {
        if (str != null && str.trim().length() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 拆分开的短信模块
     * 
     * @param trainorder
     * @time 2015年1月6日 下午5:32:26
     * @author fiend
     */
    public void sendmessage(Trainorder trainorder) {
        try {
            Dnsmaintenance dns = this.getSmsDnsByAgentid(trainorder.getAgentid());
            String sms = "亲爱的用户，您预订的火车票订单已占座，请尽快登录114生活助手客户端完成订单支付，感谢您使用114。";
            String mobiles[] = { trainorder.getContacttel() };
            Server.getInstance().getAtomService().sendSms(mobiles, sms, trainorder.getId(), trainorder.getAgentid(),
                    dns, 3);
        }
        catch (Exception e) {
            WriteLog.write("火车票短信", trainorder.getOrdernumber() + "短信异常:" + e.fillInStackTrace());
            e.fillInStackTrace();
        }
    }

}
