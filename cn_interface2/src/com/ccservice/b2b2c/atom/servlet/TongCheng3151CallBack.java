package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

@SuppressWarnings("serial")
public class TongCheng3151CallBack extends HttpServlet {
    @SuppressWarnings("rawtypes")
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        String result = "false";
        String OrderId = "";
        PrintWriter out = null;
        try {
            out = res.getWriter();
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            WriteLog.write("同程3151_回调_NEW", "接收参数:" + buf.toString());
            JSONObject jsonObject = JSONObject.parseObject(buf.toString());
            if (jsonObject != null && jsonObject.getBooleanValue("success")) {
                String CreateTime = "";
                OrderId = jsonObject.getString("uniqueId");
                if (Is(OrderId)) {
                    String sql = "SELECT CreateTime FROM AccountWait WITH(NOLOCK) WHERE Status=0 AND OrderId="
                            + OrderId;
                    List list1 = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    if (list1.size() > 0) {
                        Map map = (Map) list1.get(0);
                        CreateTime = map.get("CreateTime").toString();
                        WriteLog.write("同程3151_回调_NEW", "OrderId:" + OrderId + " ;超时时间:" + CreateTime);
                    }
                    if (!CreateTime.equals("")) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date a = df.parse(CreateTime);
                        Date b = df.parse(df.format(new Date()));
                        if (a.after(b)) {
                            //SQL
                            String updateSql = "UPDATE AccountWait SET Status = 1 WHERE OrderId = " + OrderId;
                            //更新成功
                            if (Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql) > 0) {
                                //异步
                                new TongCheng3151CallBackThread(jsonObject, OrderId, buf.toString()).start();
                                //结果
                                result = "success";
                            }
                        }
                        else {
                            result = "break";
                        }
                    }
                    else {
                        result = "break";
                    }
                }
                else {
                    result = "break";
                }
            }
            else {
                result = "false";
            }
        }
        catch (Exception e) {
            result = "break";
            e.printStackTrace();
        }
        finally {
            WriteLog.write("同程3151_回调_NEW", "OrderId:" + OrderId + " ;回调内容:" + result);
            out.print(result);
            out.flush();
            out.close();
        }
    }

    @SuppressWarnings("rawtypes")
    public boolean Is(String OrderId) {
        boolean result = false;
        String sql = "SELECT COUNT(1) AS count FROM AccountWait WITH(NOLOCK) WHERE Status=0 AND OrderId=" + OrderId;
        try {
            List list1 = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list1.size() > 0) {
                Map map = (Map) list1.get(0);
                if (map.get("count").toString().equals("1")) {
                    result = true;
                }
                WriteLog.write("同程3151_回调_NEW", "OrderId:" + OrderId + " ;count:" + map.get("count").toString());
            }
        }
        catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

}

class TongCheng3151CallBackThread extends Thread {

    private String orderId;

    @SuppressWarnings("unused")
    private String stringBuffer;

    private JSONObject jsonObject;

    public TongCheng3151CallBackThread(JSONObject jsonObject, String orderId, String stringBuffer) {
        this.orderId = orderId;
        this.jsonObject = jsonObject;
        this.stringBuffer = stringBuffer;
    }

    public void run() {
        try {
            //OCS
            //OcsMethod.getInstance().add(orderId, stringBuffer);
            //日志
            WriteLog.write("同程3151_回调_NEW", "OrderId:" + orderId + ":发送下单");
        }
        catch (Exception e) {

        }
        //下单
        finally {
            new TongchengSupplyMethod().tongcheng3151Order(jsonObject);
        }
    }

}