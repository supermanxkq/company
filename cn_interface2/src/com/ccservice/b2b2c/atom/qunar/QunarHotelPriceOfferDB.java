package com.ccservice.b2b2c.atom.qunar;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import com.ccservice.huamin.WriteLog;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.base.hotelgooddata.HotelGoodData;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

@SuppressWarnings("serial")
public class QunarHotelPriceOfferDB extends HttpServlet {

    @SuppressWarnings("unchecked")
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置编码方式
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        //去哪儿请求参数
        String hotelid = request.getParameter("hotelId");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        try {
            if (!ElongHotelInterfaceUtil.StringIsNull(hotelid) && !ElongHotelInterfaceUtil.StringIsNull(fromDate)
                    && !ElongHotelInterfaceUtil.StringIsNull(toDate)) {
                //查询酒店
                Hotel hotel = Server.getInstance().getHotelService().findHotel(Long.parseLong(hotelid.trim()));
                if (hotel == null || ElongHotelInterfaceUtil.StringIsNull(hotel.getQunarId())) {
                    throw new Exception("Hotel QunarId Is Null.");
                }
                if (hotel.getState() == null || hotel.getState().intValue() != 3) {
                    throw new Exception("酒店已禁用.");
                }
                //酒店开、关房，1：关房；其他：开房
                if (hotel.getQunarOpenOrClose() == null) {
                    hotel.setQunarOpenOrClose(0);
                }
                //城市、地址、电话
                String city = hotel.getQunarId().substring(0, hotel.getQunarId().lastIndexOf("_")).trim();
                String address = ElongHotelInterfaceUtil.StringIsNull(hotel.getAddress()) ? "" : hotel.getAddress()
                        .trim();
                String tel = ElongHotelInterfaceUtil.StringIsNull(hotel.getMarkettell()) ? "" : hotel.getMarkettell()
                        .trim();
                if (ElongHotelInterfaceUtil.StringIsNull(tel)) {
                    tel = ElongHotelInterfaceUtil.StringIsNull(hotel.getTortell()) ? "" : hotel.getTortell().trim();
                }
                //print
                PrintWriter out = response.getWriter();
                StringBuilder sb = new StringBuilder();
                sb.append("<?xml version='1.0' encoding='utf-8'?>");
                sb.append("<hotel id='" + hotel.getId() + "' city='" + city + "' name='" + hotel.getName().trim()
                        + "' address='" + address + "' tel='" + tel + "'>");
                sb.append("<rooms>");
                //格式化日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                fromDate = sdf.format(sdf.parse(fromDate));
                toDate = sdf.format(sdf.parse(toDate));
                int days = ElongHotelInterfaceUtil.getSubDays(fromDate, toDate);
                if (days > 0) {
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    String CurrentCloseTime = PropertyUtil.getValue("CurrentCloseTime").trim();
                    Date close = format.parse(CurrentCloseTime);
                    //房型价格
                    String sql = "where c_shijiprice > 0 and c_hotelid = " + hotel.getId() + " and c_datenum>='"
                            + fromDate + "' and c_datenum<'" + toDate + "'";
                    List<HotelGoodData> list = Server.getInstance().getHotelService()
                            .findAllHotelGoodData(sql, "order by c_datenum", -1, 0);
                    Map<String, Map<String, HotelGoodData>> map = new HashMap<String, Map<String, HotelGoodData>>();//roomid+prodid、date、gooddata
                    Map<String, List<String>> dayMap = new HashMap<String, List<String>>();//roomid+prodid、日期集合
                    Map<String, Boolean> repeatMap = new HashMap<String, Boolean>();//同一套餐重复，有问题数据
                    for (HotelGoodData l : list) {
                        if (l.getRoomtypeid() == null || l.getRoomtypeid().longValue() <= 0
                                || ElongHotelInterfaceUtil.StringIsNull(l.getRoomtypename())
                                || ElongHotelInterfaceUtil.StringIsNull(l.getRatetypeid())) {
                            continue;
                        }
                        String key = l.getRoomtypeid().longValue() + "@" + l.getRatetypeid().trim();
                        //判断日期是否重复
                        String currentDate = l.getDatenum().trim();//当前日期
                        List<String> dates = dayMap.get(key);
                        if (dates != null && dates.size() > 0) {
                            //日期重复、房型过滤
                            if (dates.contains(currentDate)) {
                                repeatMap.put(key, true);
                                continue;
                            }
                            else {
                                dates.add(currentDate);
                                dayMap.put(key, dates);
                            }
                        }
                        else {
                            dates = new ArrayList<String>();
                            dates.add(currentDate);
                            dayMap.put(key, dates);
                        }
                        //封装房型
                        Map<String, HotelGoodData> temp = map.get(key);
                        if (temp == null || temp.size() == 0) {
                            temp = new HashMap<String, HotelGoodData>();
                        }
                        temp.put(currentDate, l);
                        map.put(key, temp);
                    }
                    //重复的不显示
                    if (repeatMap.size() > 0) {
                        for (String key : repeatMap.keySet()) {
                            if (map.containsKey(key)) {
                                map.remove(key);
                            }
                        }
                    }
                    for (String key : map.keySet()) {
                        long roomid = Long.parseLong(key.split("@")[0]);
                        Map<String, HotelGoodData> prices = map.get(key);
                        if (prices == null || prices.size() == 0) {
                            continue;
                        }
                        HotelGoodData first = null;
                        //拼数据
                        String bfs = "";//早餐
                        String prs = "";//价格
                        String status = "";//房态
                        String counts = "";//房量
                        for (int i = 0; i < days; i++) {
                            String currentDay = ElongHotelInterfaceUtil.getAddDate(fromDate, i);
                            String currentDate = ElongHotelInterfaceUtil.getCurrentDate();
                            int tempDays = ElongHotelInterfaceUtil.getSubDays(currentDate, currentDay);
                            if (prices.containsKey(currentDay) && tempDays >= 0) {//价格日期 >= 当前日期
                                HotelGoodData p = prices.get(currentDay);
                                if (first == null) {
                                    first = p;
                                }
                                long bf = p.getBfcount() == null ? 0 : p.getBfcount().longValue();//0：无早
                                int pr = p.getShijiprice().intValue();
                                long roomstatus = p.getRoomstatus() == null ? 1 : p.getRoomstatus().longValue();//1：满房
                                bfs += bf + "|";
                                prs += pr + "|";
                                Date current = format.parse(format.format(new Date()));
                                //当天、关房时间后
                                if (tempDays == 0 && current.after(close)) {
                                    status += "1|";//0：表示有房； 1：表示满房
                                }
                                //房型关房、酒店关房
                                else if ("1".equals(p.getRoomflag()) || hotel.getQunarOpenOrClose().intValue() == 1) {
                                    status += "1|";//0：表示有房； 1：表示满房
                                }
                                else {
                                    status += roomstatus + "|";
                                }
                            }
                            else {
                                bfs += "0|";
                                prs += "0|";
                                status += "1|";//0：表示有房； 1：表示满房
                            }
                            counts += "0|";//房量设为0，不即时确认
                        }
                        if (first == null) {
                            continue;
                        }
                        bfs = bfs.substring(0, bfs.length() - 1);
                        prs = prs.substring(0, prs.length() - 1);
                        status = status.substring(0, status.length() - 1);
                        counts = counts.substring(0, counts.length() - 1);
                        //房型、床型、宽带等
                        String roomname = first.getRoomtypename().trim();
                        long bed = first.getBedtypeid() == null ? 2 : first.getBedtypeid().longValue(); //2：大/双床
                        int broadband = first.getWeb() == null ? 0 : first.getWeb().intValue();//0：无
                        int last = first.getMinday() == null || first.getMinday() < 1 ? 1 : first.getMinday()
                                .intValue();//连住
                        int advance = first.getBeforeday() == null ? 0 : first.getBeforeday().intValue();//提前
                        //拼接
                        if (roomid == first.getRoomtypeid().longValue()) {
                            sb.append("<room id='" + roomid + "_" + first.getRatetypeid() + "' name='" + roomname
                                    + "' breakfast='" + bfs + "' bed='" + bed + "' broadband='" + broadband
                                    + "' prepay='0' prices='" + prs + "' status='" + status + "' counts='" + counts
                                    + "' last='" + last + "' advance='" + advance + "'/>");
                        }
                    }
                }
                sb.append("</rooms>");
                sb.append("</hotel>");
                out.write(sb.toString());
                out.flush();
                out.close();
            }
            else {
                throw new Exception("Request Data Error.");
            }
        }
        catch (Exception e) {
            StackTraceElement stack = e.getStackTrace()[0];
            WriteLog.write("去哪儿报价接口",
                    "异常信息：" + e.getMessage() + "；类名：" + stack.getFileName() + "；方法名：" + stack.getMethodName() + "；行数："
                            + stack.getLineNumber());
            PrintWriter out = response.getWriter();
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version='1.0' encoding='utf-8'?>");
            sb.append("<hotel/>");
            out.write(sb.toString());
            out.flush();
            out.close();
        }
    }
}