package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.base.train.Train;

/**
 * Servlet implementation class TaoBaoTickChenk
 */
public class TaoBaoTickChenk extends HttpServlet {
    private ElongHotelInterfaceUtil el;

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaoBaoTickChenk() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=utf-8");
        res.setCharacterEncoding("utf-8");
        JSONObject resultjson = new JSONObject();
        JSONArray resultjsons = new JSONArray();
        PrintWriter out = null;
        out = res.getWriter();
        try {

            Map mp = new HashMap();
            long l1 = System.currentTimeMillis();
            String fromStation = new String(req.getParameter("fromStation").getBytes("iso-8859-1"), "utf-8");
            String toStation = new String(req.getParameter("toStation").getBytes("iso-8859-1"), "utf-8");
            mp.put("fromStation", fromStation);
            mp.put("toStation", toStation);
            mp.put("trainNo", req.getParameter("trainNo"));
            mp.put("departDate", req.getParameter("departDate"));
            mp.put("seatType", req.getParameter("seatType"));
            mp.put("bookNum", req.getParameter("bookNum").toString());
            TaobaoTickChangServer TCS = new TaobaoTickChangServer();
            List<Train> list = TCS.getlistTrains(mp);
            if (list == null) {
                resultjson.put("status", "3");
            }
            else {

                resultjson = Quertick(mp, list);

            }
            out.print(resultjson);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            resultjson.put("status", "3");
            out.print(resultjson);
            out.flush();
            out.close();
        }
    }

    public JSONObject Quertick(Map mp, List<Train> ls) {
        String[] st = null;
        JSONObject results = new JSONObject();
        JSONObject resultdata = new JSONObject();
        // 接受车次符合这K588/K585类型的数据判断
        if (mp.get("trainNo").toString().contains("/")) {
            st = mp.get("trainNo").toString().split("/");
        }
        else {

            st = new String[1];
            st[0] = mp.get("trainNo").toString();
        }
        int x = 0;

        String yb = "-1";
        for (int y = 0; y < ls.size(); y++) {
            for (int i = 0; i < st.length; i++) {
                if (st[i].equals(ls.get(y).getTraincode()))// 对比车次
                {
                    x = 1;

                    switch (Integer.parseInt(mp.get("seatType").toString()))// 作为类型
                    {
                    case 1:
                        yb = ls.get(y).getYzyp();
                        break;
                    case 2:
                        yb = ls.get(y).getYwyp();
                        break;
                    case 3:
                        yb = ls.get(y).getYwyp();
                        break;
                    case 4:
                        yb = ls.get(y).getYwyp();
                        break;
                    case 5:
                        yb = ls.get(y).getRzyp();
                        break;
                    case 6:
                        yb = ls.get(y).getRwyp();
                        break;
                    case 7:
                        yb = ls.get(y).getRwyp();
                        break;
                    case 8:
                        yb = ls.get(y).getRwyp();
                        break;
                    case 9:
                        yb = ls.get(y).getSwzyp();
                        break;
                    case 11:
                        yb = "0";
                        break;
                    case 12:
                        yb = ls.get(y).getTdzyp();
                        break;
                    case 13:
                        yb = ls.get(y).getRz1yp();
                        break;
                    case 14:
                        yb = ls.get(y).getRz2yp();
                        break;
                    case 15:
                        yb = ls.get(y).getGwyp();
                        break;
                    case 16:
                        yb = ls.get(y).getGwyp();
                        break;
                    case 17:
                        yb = "-1";
                    case 18:
                        yb = "-1";
                    case -1:
                        yb = "-1";
                    }

                }
            }
        }
        if (x == 0) {
            results.put("status", 1);
        }
        else {

            if (yb.equals("-1")) {
                results.put("status", 2);
            }
            else if (Integer.parseInt(yb) > 0
                    && Integer.parseInt(yb) - Integer.parseInt(mp.get("bookNum").toString()) >= 0) {
                results.put("status", 0);
                resultdata.put("fromStation", mp.get("fromStation"));
                resultdata.put("toStation", mp.get("toStation"));
                resultdata.put("date", mp.get("departDate"));
                resultdata.put("trainNo", mp.get("trainNo"));
                resultdata.put("seatType", mp.get("seatType"));
                resultdata.put("bookNum", mp.get("bookNum"));
                resultdata.put("stockNum", yb);
                resultdata.put("canBook", "1");

            }
            else if (Integer.parseInt(yb) == 0
                    || Integer.parseInt(yb) - Integer.parseInt(mp.get("bookNum").toString()) < 0) {
                results.put("status", 0);
                resultdata.put("fromStation", mp.get("fromStation"));
                resultdata.put("toStation", mp.get("toStation"));
                resultdata.put("date", mp.get("departDate"));
                resultdata.put("trainNo", mp.get("trainNo"));
                resultdata.put("seatType", mp.get("seatType"));
                resultdata.put("bookNum", mp.get("bookNum"));
                resultdata.put("stockNum", yb);
                resultdata.put("canBook", "0");
            }

            results.put("data", resultdata);
        }

        return results;
    }

}
