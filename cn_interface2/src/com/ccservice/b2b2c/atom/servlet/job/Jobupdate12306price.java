package com.ccservice.b2b2c.atom.servlet.job;

import java.io.IOException;
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
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.job.thread.Jobupdate12306priceThread;
import com.ccservice.b2b2c.atom.servlet.job.thread.Jobupdate12306priceby58Thread;
import com.ccservice.b2b2c.atom.train.data.ZhuanfaUtil;
import com.ccservice.b2b2c.base.trainno.TrainNo;
import com.ccservice.b2b2c.util.FileUtils;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 定时更新12306价格
 * 
 * @time 2015年3月2日 下午6:28:44
 * @author chendong
 */
public class Jobupdate12306price implements Job {

    public static void main(String[] args) {
        Jobupdate12306price jobupdate12306price = new Jobupdate12306price();
        jobupdate12306price.executebyTrainpriceLastupdate();
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        //        execute();
        executebyTrainpriceLastupdate();
    }

    /**
     * 更新价格机制根据最后更新的时间
     * 定时去重新更新价格
     * 12306价格
     * @time 2015年5月5日 下午7:27:33
     * @author chendong
     */
    private void executebyTrainpriceLastupdate() {
        String jobupdate12306price_update12306price = PropertyUtil.getValue("jobupdate12306price_update12306price",
                "Train.properties");
        String jobupdate12306price_update_shangxiapu_price = PropertyUtil.getValue(
                "jobupdate12306price_update_shangxiapu_price", "Train.properties");
        List customerusers = Server.getInstance().getSystemService()
                .findMapResultByProcedure("sp_TrainPrice_getlastUpdateTrainprice");
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(customerusers.size() * 2);
        for (int i = 0; i < customerusers.size(); i++) {
            Map map = (Map) customerusers.get(i);
            String mcckey = map.get("mcckey").toString();
            String station_train_code = mcckey.split("_")[0].trim();//车次
            // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
            Thread t1 = null;
            int updatetype = 1;//0更新12306的价格信息(老) 1更新12306的价格信息(新)  2更新上下铺的价格
            t1 = new Jobupdate12306priceThread(station_train_code, updatetype);//将线程放入池中进行执行
            if ("1".equals(jobupdate12306price_update_shangxiapu_price)) {
                updatetype = 2;//0更新12306的价格信息(老) 1更新12306的价格信息(新)  2更新上下铺的价格
                t1 = new Jobupdate12306priceThread(station_train_code, updatetype);
            }
            pool.execute(t1);
        }
        pool.shutdown();
    }

    private static void old_main() {
        //        execute();
        update12306pricebytrain_class("G2");
        for (int i = 1; i < 10; i++) {
            //            update12306pricebytrain_class("Z" + i);
            //            update12306pricebytrain_class(i + "");
            //            update12306pricebytrain_class("G" + i);
            //            update12306pricebytrain_class("D" + i);
            //            update12306pricebytrain_class("T" + i);
            //            update12306pricebytrain_class("K" + i);
            //            update12306pricebytrain_class("C" + i);
            //            update12306pricebytrain_class("S" + i);
            //            update12306pricebytrain_class("Y" + i);
        }
        update12306priceby58("11");
        for (int i = 9; i > 0; i--) {
            //            update12306priceby58("1" + i);
            //            update12306priceby58("2" + i);
            //            update12306priceby58("3" + i);
            //            update12306priceby58("4" + i);
            //            update12306priceby58("5" + i);
            //            update12306priceby58("6" + i);
            //            update12306priceby58("7" + i);
            //            update12306priceby58("8" + i);
            //            update12306priceby58("9" + i);
            //                        update12306pricebytrain_class("D" + i);
            //                        update12306pricebytrain_class("T" + i);
            //                        update12306pricebytrain_class("Z" + i);
            //                        update12306pricebytrain_class("K" + i);
            //                        update12306pricebytrain_class("C" + i);
            //                        update12306pricebytrain_class("S" + i);
            //                        update12306pricebytrain_class("Y" + i);
            //                        update12306pricebytrain_class(i + "");
        }

    }

    /**
     * 从配置文件Train.properties读取更新的车型,通过读取12306的价格更新价格
     * 
     * @time 2015年3月14日 下午1:17:00
     * @author chendong
     */
    private static void execute() {
        //        String chexing = PropertyUtil.getValue("chexing", "Train.properties");
        String chexing = "8166";
        String[] chexings = chexing.split("[|]");
        for (int j = 0; j < chexings.length; j++) {
            String chexing_t = chexings[j];
            for (int i = 1; i < 10; i++) {
                update12306pricebytrain_class(chexing_t + i);
            }
        }
    }

    /**
     * 更新12306的价格
     * 去12306抓取数据
     * @param train_class
     * @time 2015年3月21日 下午6:39:14
     * @author chendong
     */
    public static void update12306pricebytrain_class(String train_class) {
        String temp_train_class = train_class;
        List list = getTrainnoListbysql("WHERE 1=1 AND C_STATION_TRAIN_CODE LIKE '" + temp_train_class
                + "%' AND C_STATION_NO='01'", " ORDER BY ID DESC ");
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(3);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        for (int i = 0; i < list.size(); i++) {
            TrainNo trainno = (TrainNo) list.get(i);
            String train_no = trainno.getTrain_no().trim();//列车号
            String station_train_code = trainno.getStation_train_code().trim();//车次
            // 将线程放入池中进行执行
            WriteLog.write("update12306pricebytrain_class", train_no + ":" + station_train_code);
            int updatetype = 0;//0更新12306的价格信息(老) 1更新12306的价格信息(新)  2更新上下铺的价格
            t1 = new Jobupdate12306priceThread(train_no, station_train_code, updatetype);

            //            new Jobupdate12306priceThread(train_no, station_train_code).run();
            pool.execute(t1);
        }
        pool.shutdown();
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

    public static void update12306priceby58(String temp_train_class) {
        //        String temp_train_class = "1148";
        List list = getTrainnoListbysql("WHERE 1=1 AND C_STATION_TRAIN_CODE LIKE '" + temp_train_class
                + "%' AND C_STATION_NO='01'", " ORDER BY ID DESC ");
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(1);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        for (int i = 0; i < list.size(); i++) {
            TrainNo trainno = (TrainNo) list.get(i);
            String station_train_code = trainno.getStation_train_code().trim();
            String train_no = trainno.getTrain_no().trim();
            // 将线程放入池中进行执行
            t1 = new Jobupdate12306priceby58Thread(train_no, station_train_code);
            pool.execute(t1);
        }
        pool.shutdown();
    }
}
