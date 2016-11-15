package com.ccservice.b2b2c.atom.servlet.job;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.job.thread.Jobupdate12306priceThread;
import com.ccservice.b2b2c.atom.servlet.job.thread.Jobupdate12306priceThreadSKD;
import com.ccservice.b2b2c.atom.train.data.ZhuanfaUtil;
import com.ccservice.b2b2c.base.trainno.TrainNo;
import com.ccservice.b2b2c.util.FileUtils;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 定时更新12306价格
 * 获取搜可得的接口
 * @time 2015年3月2日 下午6:28:44
 * @author chendong
 */
public class Jobupdate12306priceSKD implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        execute();
    }

    private static void execute() {
        String chexing = PropertyUtil.getValue("chexing", "Train.properties");
        String[] chexings = chexing.split("[|]");
        for (int j = 0; j < chexings.length; j++) {
            String chexing_t = chexings[j];
            for (int i = 1; i < 10; i++) {
                update12306price_wp(chexing_t + i);
            }
        }
    }

    public static void main(String[] args) {
        update12306price_wp("K");
        //        execute();
        //        for (int i = 1; i < 10; i++) {
        //            update12306pricebytrain_class("G" + i);
        //            update12306pricebytrain_class("D" + i);
        //            update12306pricebytrain_class("T" + i);
        //            update12306pricebytrain_class("Z" + i);
        //            update12306pricebytrain_class("K" + i);
        //            update12306pricebytrain_class("C" + i);
        //            update12306pricebytrain_class("S" + i);
        //            update12306pricebytrain_class("Y" + i);
        //            update12306pricebytrain_class(i + "");
        //        }

    }

    @SuppressWarnings("rawtypes")
    private static void update12306price_wp(String train_class) {
        Map<String, List<TrainNo>> station_code_map = new HashMap<String, List<TrainNo>>();
        List<TrainNo> trainNoList = new ArrayList<TrainNo>();
        String station_train_code_before = null;
        String temp_train_class = train_class;
        List list = getTrainnoListbysql("WHERE 1=1 AND C_STATION_TRAIN_CODE LIKE '" + temp_train_class + "%'",
                " ORDER BY C_STATION_TRAIN_CODE,C_STATION_NO ASC");
        for (int i = 0; i < list.size(); i++) {
            TrainNo trainno = (TrainNo) list.get(i);
            String station_train_code = trainno.getStation_train_code().trim();
            if (station_code_map.isEmpty() || !station_code_map.containsKey(station_train_code)) {
                station_code_map.put(station_train_code, null);
                if (station_train_code_before != null) {
                    station_code_map.put(station_train_code_before, trainNoList);
                    trainNoList = new ArrayList<TrainNo>();
                }
            }
            if (station_code_map.containsKey(station_train_code)) {
                trainNoList.add(trainno);
            }
            station_train_code_before = station_train_code;
            if (i == list.size() - 1) {
                station_code_map.put(station_train_code_before, trainNoList);
            }
        }
        jiexiMap(station_code_map);
    }

    private static void jiexiMap(Map<String, List<TrainNo>> station_code_map) {
        for (Map.Entry<String, List<TrainNo>> entry : station_code_map.entrySet()) {
            for (TrainNo trainno : entry.getValue()) {
                for (TrainNo trainno_else : entry.getValue()) {
                    if (Integer.valueOf(trainno.getStation_no().trim()) < Integer.valueOf(trainno_else.getStation_no()
                            .trim())) {
                        update12306pricebytrain_class(trainno.getStation_name().trim(), trainno_else.getStation_name()
                                .trim(), entry.getKey(), trainno.getStation_no().trim(), trainno_else.getStation_no()
                                .trim());
                    }
                }
            }
        }
    }

    private static void update12306pricebytrain_class(String startStationName, String endStationName,
            String station_train_code, String startStationNameCode, String endStationNameCode) {
        // 创建一个可重用固定线程数的线程池
        //        ExecutorService pool = Executors.newFixedThreadPool(1);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        //        Thread t1 = null;
        //        for (int i = 0; i < list.size(); i++) {
        //            TrainNo trainno = (TrainNo) list.get(i);
        //            String station_train_code = trainno.getStation_train_code().trim();
        //            String station_no = trainno.getStation_no().trim();
        //            String station_name = trainno.getStation_name().trim();
        //
        //            // 将线程放入池中进行执行
        //            WriteLog.write("update12306pricebytrain_class", station_no + ":" + station_name + ":" + station_train_code);
        new Jobupdate12306priceThreadSKD(startStationName, endStationName, station_train_code, startStationNameCode,
                endStationNameCode).getprice();
        //        pool.execute(t1);
        //        }
        //        station_code_map.put(station_train_code, trainno);
        //        pool.shutdown();
    }

    public static List getTrainnoListbysql(String sql, String orderby) {
        //        "where C_STATION_TRAIN_CODE='" + station_train_code + "' "
        List list1 = Server.getInstance().getTrainService().findAllTrainNo(sql, orderby, -1, 0);
        return list1;
    }

    /**
     * 
     * 根据js更新,
     * 
     * @time 2015年3月3日 上午11:15:32
     * @author chendong
     */
    private void updateTrain_nobyjsfile() {
        String TrainStationNames_path = Jobupdate12306trainnodata.class.getClassLoader().getResource("").toString()
                .substring(6)
                + "train_list.js";
        System.out.println(TrainStationNames_path);
        String filedata = "-1";
        try {
            filedata = FileUtils.readFile(TrainStationNames_path, "UTF-8");
            if (filedata.length() > 0) {
                filedata = filedata.replace("var train_list =", "");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (filedata.length() > 0) {
            JSONObject jsonobject = JSONObject.parseObject(filedata);
            jsonobject = jsonobject.getJSONObject("2015-03-11");
            JSONArray JSONArray_G = jsonobject.getJSONArray("G");
            JSONArray_G = jsonobject.getJSONArray("D");
            JSONArray_G = jsonobject.getJSONArray("T");
            JSONArray_G = jsonobject.getJSONArray("Z");
            JSONArray_G = jsonobject.getJSONArray("K");
            JSONArray_G = jsonobject.getJSONArray("C");
            JSONArray_G = jsonobject.getJSONArray("O");
            for (int i = 0; i < JSONArray_G.size(); i++) {
                JSONObject jsonobject_checi = JSONArray_G.getJSONObject(i);
                String station_train_code = jsonobject_checi.getString("station_train_code").split("[(]")[0];
                String train_no = jsonobject_checi.getString("train_no");
            }
        }
    }

    /**
     * 根据车次和车号更新这个车次在数据库里的那个车号
     * 
     * @param train_no
     * @param station_train_code
     * @time 2015年3月3日 上午10:49:31
     * @author chendong
     */
    private void update_train_no(String train_no, String station_train_code) {
        String sql = "UPDATE T_TRAINNO SET C_TRAIN_NO='" + train_no + "' WHERE C_STATION_TRAIN_CODE='"
                + station_train_code + "' AND C_STATION_NO='01'";
        System.out.println(sql);
        try {
            Server.getInstance().getSystemService().excuteEaccountBySql(sql);
        }
        catch (Exception e) {
        }
    }

    private static String getdatabyhttpclient(String url) {
        CCSGetMethod get = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 10000L);
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        get = new CCSGetMethod(url);
        //        get.addRequestHeader("Cookie", cookiestring);
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        get.setFollowRedirects(false);
        String json = "-1";
        try {
            httpClient.executeMethod(get);
            json = get.getResponseBodyAsString();
        }
        catch (HttpException e) {
        }
        catch (IOException e) {
        }
        return json;
    }

    private static void testzhuanfaurl() {
        String url = "https://kyfw.12306.cn/otn/leftTicket/queryTicketPrice?train_no=930000T19810&from_station_no=01&to_station_no=02&seat_types=14613&train_date=2015-03-13";
        String t = ZhuanfaUtil.zhuanfa(url, "", "GBK", "", "get", 2000);
        System.out.println(t);
    }

    //TODO ----------------------------------------------------------------抓取SKD卧铺票价---------------------------------------------------------------------------------------
    /**
     * 获取数据库数据 
     * @time 2015年3月14日 上午10:34:05
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    public static void getDBsuju() {
        String sql = "select top 1 C_DEPARTTIME,C_DEPARTURE,C_ARRIVAL,C_TRAINNO,C_SEATNO,C_PRICE from T_TRAINTICKET where C_SEATTYPE = '硬卧' AND ( C_SEATNO like '%上%' or C_SEATNO like '%中%' or C_SEATNO like '%下%' ) AND C_PRICE>0 order by ID desc";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            String fromstation = map.get("C_DEPARTURE").toString();
            String endstation = map.get("C_ARRIVAL").toString();
            String date = map.get("C_DEPARTTIME").toString().substring(0, 10);
            String trainno = map.get("C_TRAINNO").toString();
            String price = map.get("C_PRICE").toString();
            String seatno = map.get("C_SEATNO").toString();
            getprice(fromstation, endstation, date, trainno, price, seatno);
        }
    }

    /**
     * SKD查询价格   2015-03-15 09:24 2015-03-19
     * @param fromstation
     * @param endstation
     * @time 2015年3月14日 上午10:31:21
     * @author fiend
     */
    public static void getprice(String fromstation, String endstation, String date, String trainno, String price,
            String seatno) {
        String url = "http://www.soukd.com/DGTrain.Asp";
        String b = "代购查询";
        try {
            b = URLEncoder.encode(b, "GB2312");
            String endstation1 = URLEncoder.encode(endstation, "GB2312");
            String fromstation1 = URLEncoder.encode(fromstation, "GB2312");
            String par = "FromCity=" + fromstation1 + "&ToCity=" + endstation1 + "&sDate=2015-03-19&B1=" + b;
            String str = new String(SendPostandGet.submitPost(url, par, "GB2312"));
            //        System.out.println(str);
            jixiprice(fromstation, endstation, str, trainno);
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 解析票价 
     * @param fromstation
     * @param endstation
     * @param str
     * @param trainno
     * @param seatno
     * @time 2015年3月14日 下午1:04:42
     * @author fiend
     */
    public static void jixiprice(String fromstation, String endstation, String str, String trainno) {
        float skd_price_before = 0;
        float skd_price = 0;
        float skd_price_after = 0;
        float skd_price_rw_before = 0;
        float skd_price_rw_after = 0;
        try {
            String[] sp_trainno = str.split("color=\"#FF6600\">" + trainno + "</font></b></a></td>");
            String[] sp_price = sp_trainno[1].split("</font></b>/<b><font color=\"blue\">");
            try {
                int sp_length = sp_price[0].split("<td><b><font color=\"blue\">").length;
                //硬卧前面的价格
                String str_price = sp_price[0].split("<td><b><font color=\"blue\">")[sp_length - 1];
                if (!"-".equals(str_price)) {
                    skd_price_before = Float.valueOf(str_price);
                }
                //硬卧中间价格
                str_price = sp_price[1];
                if (!"-".equals(str_price)) {
                    skd_price = Float.valueOf(str_price);
                }
                str_price = sp_price[2].split("</font>")[0];
                if (!"-".equals(str_price)) {
                    //硬卧后面价格
                    skd_price_after = Float.valueOf(str_price);
                }
            }
            catch (Exception e) {
                //                WriteLog.write("解析硬卧票价异常", fromstation + "----->" + endstation + "----->" + trainno);
                System.out.println("解析硬卧票价异常--->" + fromstation + "----->" + endstation + "----->" + trainno);
            }
            try {
                //软卧前面的价格
                skd_price_rw_before = Float.valueOf(sp_price[2].split("color=\"blue\">")[1]);
                //软卧后面价格
                skd_price_rw_after = Float.valueOf(sp_price[3].split("</font>")[0]);
            }
            catch (Exception e) {
                //                WriteLog.write("解析软卧票价异常", fromstation + "----->" + endstation + "----->" + trainno);
                System.out.println("解析软卧票价异常--->" + fromstation + "----->" + endstation + "----->" + trainno);
            }
            System.out.println(skd_price_before + ":" + skd_price + ":" + skd_price_after + ":" + skd_price_rw_before
                    + ":" + skd_price_rw_after);
        }
        catch (Exception e) {
            //            WriteLog.write("解析车次异常", fromstation + "----->" + endstation + "----->" + trainno);
            System.out.println("解析车次异常--->" + fromstation + "----->" + endstation + "----->" + trainno);
        }

    }
}
