package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;

/**
 * 查看和操作缓存的sysconfig信息
 * 
 * 
 */
public class SysconfiginfoServerlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 68779843216117L;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String resultstring = "";
        resp.setContentType("text/plain; charset=utf-8");
        resp.setCharacterEncoding("UTF-8");
        int r1 = new Random().nextInt(10000000);
        Long starttime = System.currentTimeMillis();
        PrintWriter out = null;
        String type = req.getParameter("type");//类型，1查看,2清空
        if (type == null) {
            type = "1";
        }
        JSONObject jsonobject = new JSONObject();
        try {
            out = resp.getWriter();
            if ("1".equals(type)) {
                Map<String, String> dateHashMap = Server.getInstance().getDateHashMap();
                Iterator<String> keys = dateHashMap.keySet().iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = dateHashMap.get(key);
                    jsonobject.put(key, value);
                }
                resultstring = jsonobject.toJSONString();
            }
            else if ("2".equals(type)) {
                String name = req.getParameter("name");
                String value = req.getParameter("value");
                if (Server.getInstance().getDateHashMap().get(name) != null) {
                    Server.getInstance().getDateHashMap().put(name, value);
                }
            }
            else if ("3".equals(type)) {//获取一个火车票接口账号
                String username = req.getParameter("username");
                List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
                InterfaceAccount interfaceAccount = new InterfaceAccount();
                try {
                    list_interfaceAccount = Server.getInstance().getMemberService()
                            .findAllInterfaceAccount("where C_USERNAME = '" + username + "'", null, -1, 0);
                }
                catch (Exception e) {
                }
                if (list_interfaceAccount.size() > 0) {
                    interfaceAccount = list_interfaceAccount.get(0);
                }
                resultstring = JSONObject.toJSONString(interfaceAccount);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                out.print(resultstring);
                out.flush();
                out.close();
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
