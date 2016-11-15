package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.train.data.thread.Tielu12306PriceThread;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 火车票相关数据
 * 
 * @time 2015年8月5日 下午6:25:31
 * @author chendong
 */
public class TrainSearchServiceServerlet extends HttpServlet {
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
        String resultstring = "-1";
        resp.setContentType("text/plain; charset=utf-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = null;
        String type = req.getParameter("type");
        try {
            out = resp.getWriter();
            if ("1".equals(type)) {//获取上中下铺的价格|over
                //                int licheng = 0;
                String Code = req.getParameter("Code");//类型     R:软卧 Y:硬卧
                String TrainCode = req.getParameter("TrainCode");//车次
                Double Price = 0D;
                try {
                    Price = Double.parseDouble(req.getParameter("Price"));//上铺价格
                }
                catch (Exception e) {
                }
                if (Price > 0) {
                    resultstring = Tielu12306PriceThread.getTrainSZXPrice(Code, TrainCode, Price);
                }
            }
            else if ("2".equals(type)) {//把价格信息插入到数据库里
                String mcckey = req.getParameter("mcckey");
                String value = req.getParameter("value");
                String time = req.getParameter("time");
                setpricefromdb(mcckey, value, time);
            }
            else if ("3".equals(type)) {//从库里获取价格信息
                String key = req.getParameter("key");
                String QueryDate = req.getParameter("QueryDate");
                String sqlselect = "SELECT top 1 C_PRICE FROM T_TRAINPRICE with(nolock) where (C_MCCKEY='" + key
                        + "' and QueryDate='" + QueryDate + "') or C_MCCKEY='" + key + "' order by QueryDate desc ";
                try {
                    List clist = getSystemService().findMapResultBySql(sqlselect, null);
                    if (clist.size() > 0) {
                        Map m = (Map) clist.get(0);
                        resultstring = JSONObject.toJSONString(m);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

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

    /**
     * 插入数据库里的价格
     * 
     * @param key
     * @param value
     * @param time
     * @time 2015年7月28日 上午11:13:47
     * @author chendong
     */
    public void setpricefromdb(String mcckey, String value, String time) {
        //如果值的长度小于10或者value是-1就什么都不操作
        if (value.length() < 10 || "-1".equals(value)) {
            return;
        }
        String sqlinsert = "";
        sqlinsert = "insert into T_TRAINPRICE(C_MCCKEY,C_PRICE,QueryDate) values ('" + mcckey + "','" + value + "','"
                + time + "')";
        try {
            getSystemService().excuteGiftBySql(sqlinsert);
        }
        catch (Exception e) {

        }
    }

    public static ISystemService getSystemService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        String search_12306yupiao_service_url = PropertyUtil.getValue("search_12306yupiao_service_url",
                "Train.properties");
        try {
            return (ISystemService) factory.create(ISystemService.class, search_12306yupiao_service_url
                    + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
