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
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.train.data.Wrapper_12306;
import com.ccservice.b2b2c.atom.train.data.Wrapper_tieyou;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;

public class TicketCheckingServlet extends HttpServlet {
    private ElongHotelInterfaceUtil el;

    private static final long serialVersionUID = 1L;

    public void init() throws ServletException {
        super.init();
        el = new ElongHotelInterfaceUtil();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=utf-8");
        res.setCharacterEncoding("utf-8");
        Map mp = new HashMap();

        JSONObject resultjson = new JSONObject();
        JSONArray resultjsons = new JSONArray();
        JSONObject DATA = new JSONObject();
        PrintWriter out = null;
        out = res.getWriter();
        long l1 = System.currentTimeMillis();
        try {

            String fromStation = new String(req.getParameter("fromStation").getBytes("iso-8859-1"), "utf-8");
            String toStation = new String(req.getParameter("toStation").getBytes("iso-8859-1"), "utf-8");
            mp.put("fromStation", fromStation);
            mp.put("toStation", toStation);
            mp.put("trainNo", req.getParameter("trainNo"));
            mp.put("departDate", req.getParameter("departDate"));
            mp.put("seatType", req.getParameter("seatType"));
            mp.put("bookNum", req.getParameter("bookNum").toString());

            List<Train> ls = tickCheck(mp);
            // System.out.println(ls);
            if (ls == null) {
                resultjson.put("status", "3");
            }
            else {

                resultjson = Quertick(mp, ls);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            resultjson.put("status", "3");
            out.print(resultjson);
            out.flush();
            out.close();

        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mp.size(); i++) {
            sb.append(mp.get(i) + "  ");
        }
        WriteLog.write("taobaocheckingtrain", sb.toString() + ":" + (System.currentTimeMillis() - l1) + ":"
                + resultjson);
        out.print(resultjson);
        out.flush();
        out.close();

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
                        yb = ls.get(i).getYzyp();
                        break;
                    case 2:
                        yb = ls.get(i).getYwyp();
                        break;
                    case 3:
                        yb = ls.get(i).getYwyp();
                        break;
                    case 4:
                        yb = ls.get(i).getYwyp();
                        break;
                    case 5:
                        yb = ls.get(i).getRzyp();
                        break;
                    case 6:
                        yb = ls.get(i).getRwyp();
                        break;
                    case 7:
                        yb = ls.get(i).getRwyp();
                        break;
                    case 8:
                        yb = ls.get(i).getRwyp();
                        break;
                    case 9:
                        yb = ls.get(i).getSwzyp();
                        break;
                    case 11:
                        yb = "0";
                        break;
                    case 12:
                        yb = ls.get(i).getTdzyp();
                        break;
                    case 13:
                        yb = ls.get(i).getRz1yp();
                        break;
                    case 14:
                        yb = ls.get(i).getRz2yp();
                        break;
                    case 15:
                        yb = ls.get(i).getGwyp();
                        break;
                    case 16:
                        yb = ls.get(i).getGwyp();
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
                resultdata.put("date", el.getCurrentDate());
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
                resultdata.put("date", el.getCurrentDate());
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

    public List<Train> tickCheck(Map mp) {

        Wrapper_12306 wrapper_12306 = new Wrapper_12306();
        FlightSearch param = new FlightSearch();
        String html = "";
        Wrapper_tieyou wrapper_tieyou = new Wrapper_tieyou();
        param.setTravelType("train_query_remain");//
        // 这里把traveltype用作查列车时刻还是只查余票
        try {
            List<Train> list = null;
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    System.out.println(mp.get("departDate").toString() + " " + mp.get("toStation"));
                    list = wrapper_12306.process("",

                    Train12306StationInfoUtil.getThreeByName(mp.get("fromStation").toString()),
                            Train12306StationInfoUtil.getThreeByName(mp.get("toStation").toString()),
                            mp.get("departDate").toString(), param);

                }
                else if (i > 0) {

                    html = wrapper_tieyou.getHtml(
                            Train12306StationInfoUtil.getThreeByName(mp.get("fromStation").toString()),
                            Train12306StationInfoUtil.getThreeByName(mp.get("toStation").toString()),
                            mp.get("departDate").toString(), param);
                    list = wrapper_tieyou.process(html,
                            Train12306StationInfoUtil.getThreeByName(mp.get("fromStation").toString()),
                            Train12306StationInfoUtil.getThreeByName(mp.get("toStation").toString()),
                            mp.get("departDate").toString(), param);
                }
                if (list != null || list.size() > 0)
                    break;
            }
            return list;

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // 1::12306

        return null;
    }
}
