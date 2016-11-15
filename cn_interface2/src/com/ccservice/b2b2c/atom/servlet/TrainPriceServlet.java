package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 更新卧铺价格
 * @time 2015年7月28日13:49:43
 * @author luoqingxin
 *
 */
public class TrainPriceServlet extends HttpServlet {

    String mcckey;

    String time;

    String price;

    String zuoxi;

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        JSONObject jsonObject = new JSONObject();
        try {
            out = res.getWriter();
            this.mcckey = req.getParameter("mcckey") != null ? req.getParameter("mcckey") : "";
            this.time = req.getParameter("time") != null ? req.getParameter("time") : "";
            this.price = req.getParameter("price") != null ? req.getParameter("price") : "";
            this.zuoxi = req.getParameter("zuoxi") != null ? req.getParameter("zuoxi") : "";
            if (!mcckey.equals("") && !time.equals("")) {
                String shang = "";
                String zhong = "";
                String xia = "";
                String str = "";
                String bb = zuoxi.substring(0, 1);
                if (bb.equals("软")) {
                    str = DB(this.mcckey, 1);//软卧
                    if (!str.equals("")) {
                        int s = getIndex(str, "/", 1);//第一次出现/的下标
                        shang = str.substring(0, s);//截取上铺价格
                        xia = str.substring(s + 1, str.length());//截取下铺价格
                    }
                }
                else {
                    str = DB(this.mcckey, 2);//硬卧
                    if (!str.equals("")) {
                        int s = getIndex(str, "/", 1);//第一次出现/的下标
                        int x = getIndex(str, "/", 2);//第二次出现/的下标
                        shang = str.substring(0, s);//截取上铺价格
                        zhong = str.substring(s + 1, x);//截取中铺价格
                        xia = str.substring(x + 1, str.length());//截取下铺价格
                    }
                }
                int one = zuoxi.indexOf("上");
                int z = 0;
                if (one >= 1) {
                    if (bb.equals("软")) {
                        z = 1;
                        this.price = this.price + "/" + xia;
                    }
                    else {
                        this.price = this.price + "/" + zhong + "/" + xia;
                    }
                }
                int two = zuoxi.indexOf("中");
                if (two >= 1) {
                    this.price = shang + "/" + this.price + "/" + xia;
                }
                int three = zuoxi.indexOf("下");
                if (three >= 1) {
                    if (bb.equals("软")) {
                        z = 1;
                        this.price = shang + "/" + this.price;
                    }
                    else {
                        this.price = shang + "/" + zhong + "/" + this.price;
                    }
                }
                if (bb.equals("软")) {
                    //软卧
                    setpricefromdbrw(mcckey, price, time);
                }
                else {
                    //硬卧
                    setpricefromdbyw(mcckey, price, time);
                }

                jsonObject.put("ret", true);
            }
            else {
                jsonObject.put("ret", "请求参数有误");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ret", false);
        }
        finally {
            out.write(jsonObject.toString());
            out.flush();
            out.close();
        }
    }

    public void setpricefromdbrw(String mcckey, String price, String time) {
        String sql = "select * from T_TRAINPRICE where C_MCCKEY='" + mcckey + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        List lists = new ArrayList();
        String sqlinsert = "";
        if (list != list || !list.equals(lists)) {
            sqlinsert = "update T_TRAINPRICE set C_RWPRICE='" + price + "',QueryDate='" + time + "' where C_MCCKEY='"
                    + mcckey + "'";
        }
        else {
            sqlinsert = "delete from T_TRAINPRICE where C_MCCKEY='" + mcckey + "' AND QueryDate='" + time
                    + "';insert into T_TRAINPRICE(C_MCCKEY,C_PRICE,C_RWPRICE,QueryDate) values ('" + mcckey + "','','"
                    + price + "','" + time + "')";
        }
        try {
            Server.getInstance().getSystemService().findMapResultBySql(sqlinsert, null);
        }
        catch (Exception e) {
        }
    }

    public void setpricefromdbyw(String mcckey, String price, String time) {
        String sql = "select * from T_TRAINPRICE where C_MCCKEY='" + mcckey + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        List lists = new ArrayList();
        String sqlinsert = "";
        if (list != list || !list.equals(lists)) {
            sqlinsert = "update T_TRAINPRICE set C_YWPRICE='" + price + "',QueryDate='" + time + "' where C_MCCKEY='"
                    + mcckey + "'";
        }
        else {
            sqlinsert = "delete from T_TRAINPRICE where C_MCCKEY='" + mcckey + "' AND QueryDate='" + time
                    + "';insert into T_TRAINPRICE(C_MCCKEY,C_PRICE,C_YWPRICE,QueryDate) values ('" + mcckey + "','','"
                    + price + "','" + time + "')";
        }
        try {
            Server.getInstance().getSystemService().findMapResultBySql(sqlinsert, null);
        }
        catch (Exception e) {
        }
    }

    public static int getIndex(String str, String c, int times) {
        int index = 0;
        String[] arr = str.split(c);
        int length = arr.length > times ? times : arr.length;
        for (int i = 0; i < length; i++) {
            index += arr[i].length();
        }
        return index + times - 1;
    }

    public String DB(String mcckey, int j) {
        String rw = "";
        String yw = "";
        try {
            String sqlstr = "SELECT top 1  * FROM T_TRAINPRICE WHERE C_MCCKEY='" + mcckey + "'";
            List sqlResultList = Server.getInstance().getSystemService().findMapResultBySql(sqlstr, null);
            for (int i = 0; i < sqlResultList.size(); ++i) {
                Map map = (Map) sqlResultList.get(i);
                rw = map.get("C_RWPRICE").toString();
                yw = map.get("C_YWPRICE").toString();
            }
            if (j == 1) {
                return rw;
            }
            else {
                return yw;
            }
        }
        catch (Exception e) {
            return yw;
        }
    }
}
