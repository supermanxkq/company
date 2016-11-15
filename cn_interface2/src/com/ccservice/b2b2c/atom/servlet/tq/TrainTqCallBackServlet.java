package com.ccservice.b2b2c.atom.servlet.tq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.component.sms.SMSTemplet;
import com.ccservice.component.sms.SMSType;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 114火车票出票业务类
 */

public class TrainTqCallBackServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        //接收参数
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String JsonStr = buf.toString();

        WriteLog.write("天衢火车票短信", "出票成功：JsonStr=" + JsonStr);
        if (JsonStr != null && JsonStr.length() > 0) {
            String str[] = JsonStr.split("&");
            Map map = new HashMap();
            for (String ss : str) {
                map.put(ss.substring(0, ss.indexOf("=")), ss.substring(ss.indexOf("=") + 1));
            }
            String transactionid = (String) map.get("transactionid");
            String isSuccess = (String) map.get("isSuccess");
            String orderid = (String) map.get("orderid");
            System.out.println("transactionid：" + transactionid + "isSuccess" + isSuccess);
            if (("Y").equalsIgnoreCase(isSuccess)) {
                Trainform form = new Trainform();
                form.setQunarordernumber(orderid);
                List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(form, null);
                if (orders != null && orders.size() > 0) {
                    Trainorder trainorder = Server.getInstance().getTrainService()
                            .findTrainorder(orders.get(0).getId());
                    try {
                        if (trainorder != null) {
                            sendmessage(trainorder);
                            BusInsur(trainorder);
                        }
                    }
                    catch (Exception e) {
                    }

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
     * 买保险
     */
    public static void BusInsur(Trainorder trainorder) {
        boolean flag = false;
        for (Trainpassenger passenger : trainorder.getPassengers()) {
            for (Trainticket ticket : passenger.getTraintickets()) {
                if (ticket.getInsurorigprice() != null && ticket.getInsurorigprice() > 0) {
                    flag = true;
                }
            }
        }
        if (flag) {
            String url = PropertyUtil.getValue("mqurl", "tqTrain.properties");
            JSONObject jsoseng = new JSONObject();
            jsoseng.put("type", "1");
            jsoseng.put("orderid", trainorder.getId());
            WriteLog.write("12306_TrainpayMqMSGUtil_GetUrl", jsoseng.toString());
            ActiveMQUtil.sendMessage(url, "TrainInsure", jsoseng.toString());
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
            SMSTemplet smstem = new SMSTemplet();
            String smstemple = smstem.getSMSTemplett(SMSType.TRAINTICKETISSUE, dns);// 单程
            if (isNotNullOrEpt(smstemple)) {
                for (Trainpassenger passenger : trainorder.getPassengers()) {
                    for (Trainticket ticket : passenger.getTraintickets()) {
                        // 取票单号[取票单号],[联系人]您已购[日期][车次][车厢][席位][出发站][出发时间]开。请尽快换取纸质车票。
                        //                        String sms = smstemple.replace("[订单号]", "e11");
                        String sms = smstemple.replace("[取票号]", trainorder.getExtnumber() + "");
                        sms = sms.replace("[联系人]", passenger.getName() + "先生/女士");
                        sms = sms.replace("[日期]",
                                formatchinaMMdd(formatStringToTime(ticket.getDeparttime(), "yyyy-MM-dd HH:mm")));
                        sms = sms.replace("[车次]", ticket.getTrainno() + "");
                        sms = sms.replace("[车厢]", ticket.getCoach() + "");
                        sms = sms.replace("[席位]", ticket.getSeatno() + "");
                        sms = sms.replace("[出发站]", ticket.getDeparture() + "");
                        sms = sms.replace("[到达站]", ticket.getArrival() + "");
                        sms = sms.replace("[出发时间]",
                                formatTimestampHHmm(formatStringToTime(ticket.getDeparttime(), "yyyy-MM-dd HH:mm")));
                        String mobiles[] = { trainorder.getContacttel() };
                        WriteLog.write("火车票短信", trainorder.getOrdernumber() + "火车票出票短信内容：" + sms);
                        Server.getInstance().getAtomService().sendSms(mobiles, sms, trainorder.getId(),
                                trainorder.getAgentid(), dns, 3);
                    }
                }

            }
            else {
                System.out.println("444");
                WriteLog.write("火车票短信", trainorder.getOrdernumber() + ":火车票出票短信模板不存在");
            }
        }
        catch (Exception e) {
            System.out.println("555");
            System.out.println(e.getMessage());
            WriteLog.write("火车票短信", trainorder.getOrdernumber() + "短信异常:" + e.fillInStackTrace());
        }
    }

    public Timestamp formatStringToTime(String date, String format) {
        try {
            SimpleDateFormat simplefromat = new SimpleDateFormat(format);
            return new Timestamp(simplefromat.parse(date).getTime());

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String formatchinaMMdd(Timestamp date) {
        return (new SimpleDateFormat("MM月dd日").format(date));
    }

    public String formatTimestampHHmm(Timestamp date) {
        try {
            return (new SimpleDateFormat("HH:mm").format(date));
        }
        catch (Exception e) {
            return "";
        }
    }
}
