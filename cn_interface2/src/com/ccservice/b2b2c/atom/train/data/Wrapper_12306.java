package com.ccservice.b2b2c.atom.train.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet2;
//import com.ccservice.b2b2c.atom.server.MemCached;
//import com.ccservice.b2b2c.atom.servlet.TrainSearch;
import com.ccservice.b2b2c.atom.train.TrainSupport;
import com.ccservice.b2b2c.atom.train.data.Interface.CcsTrainCrawler;
import com.ccservice.b2b2c.atom.train.data.thread.AddMemcachedThread;
import com.ccservice.b2b2c.atom.train.data.thread.Get12306dataThread;
import com.ccservice.b2b2c.atom.train.data.thread.Tielu12306PriceThread;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.util.HttpUtils;

public class Wrapper_12306 extends TrainSupport implements CcsTrainCrawler {
    public static void main(String[] args) {
        String search_url = "http://www.ganhuoche.com/nanjing-shanghai.html?date=2015-8-21";
        String html = "";
        //        html = ZhuanfaUtil.gethttpclientdata(search_url, 40000L);
        html = SendPostandGet2.doGet(search_url, "utf-8");

        System.out.println("-------html----" + html);
    }

    @Override
    public String getHtml(String startcity, String endcity, String time, FlightSearch param, String purposecodes) {
        return null;
    }

    /**
     * 通过查询12306来获取列车数据
     * 
     * @param startcity
     *            出发三字码
     * @param endcity
     *            到达三字码
     * @param time
     *            出发时间 2014-12-25
     * @param purposecodes
     *            订票类别
     * @param param
     *            param.getTravelType() == "train_query" 才查询价格否则只查询时刻
     *            param.getGeneral() == 2 说明是使用实时的数据
     * @time 2014年9月12日 下午4:50:04
     * @author chendong
     */
    @Override
    public List<Train> process(String html, String startcity, String endcity, String time, FlightSearch param,
            String purposecodes) {
        long l1 = System.currentTimeMillis();
        int randominti = new Random().nextInt(10000000);
        List<Train> listtrain = new ArrayList<Train>();
        boolean isSearchprice = param != null && "train_query".equals(param.getTravelType());// 是否查询价格
        boolean isfrom12306 = param != null && param.getGeneral() == 2;// 是否实时的
        boolean issearchlicheng = param != null && "1".equals(param.getTypeFlag());// 是否需要里程,默认不需要要 //是否查询里程
        String json = "-1";
        try {
            json = get12306searchJSON(l1, randominti, startcity, endcity, time, isfrom12306, purposecodes);
            if ("-1".equals(json)) {
                return listtrain;
            }
            if (json.lastIndexOf('}') != json.length() - 1) {
                json = get12306searchJSON(l1, randominti, startcity, endcity, time, isfrom12306, purposecodes);
            }
            JSONObject jsonobject = JSON.parseObject(json);
            jsonobject = (JSONObject) jsonobject.get("data");
            JSONArray datasArray = jsonobject.getJSONArray("datas");
            ExecutorService threadPool2 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<Train>> futures = new ArrayList<Future<Train>>(datasArray.size());
            CompletionService<Train> completionService = new ExecutorCompletionService<Train>(threadPool2);
            if (datasArray != null) {
                for (int i = 0; i < datasArray.size(); i++) {
                    JSONObject data_index = (JSONObject) datasArray.get(i);
                    Train train = new Train();
                    String startTime = data_index.getString("start_time");// 出发时间
                    String traincode = data_index.getString("station_train_code");// 车次
                    train.setTraincode(traincode);
                    String traintype = gettraintype(traincode);// 列车等级
                    train.setTraintype(traintype);
                    String startCity = data_index.getString("from_station_name");// 出发城市
                    train.setStartcity(startCity);
                    String from_station_code = data_index.getString("from_station_telecode");// 出发车站简码
                    train.setFrom_station_code(from_station_code);
                    train.setStarttime(startTime);
                    String endCity = data_index.getString("to_station_name");// 到达城市
                    train.setEndcity(endCity);
                    String to_station_code = data_index.getString("to_station_telecode");// 到达车站简码
                    train.setTo_station_code(to_station_code);
                    String endTime = data_index.getString("arrive_time");// 到达时间
                    train.setEndtime(endTime);
                    String arrive_days = data_index.getString("day_difference");// 列车从出发站到达目的站的运行天数
                                                                                // 0:当日到达，1:
                                                                                // 次日到达，2:三日到达，3:四日到达，依此类推
                    train.setArrive_days(arrive_days);
                    String train_start_date = data_index.getString("start_train_date");// 列车从始发站出发的日期
                    train.setTrain_start_date(train_start_date);
                    String access_byidcard = data_index.getString("is_support_card");// 是否可凭二代身份证直接进出站
                    train.setAccess_byidcard(access_byidcard);
                    String sale_date_time = data_index.getString("sale_time");// 车票开售时间
                    train.setSale_date_time(sale_date_time);
                    train.setTradeno(sale_date_time);
                    String can_buy_now = data_index.getString("canWebBuy");// 当前是否可以接受预定（Y:可以，N:不可以）
                    train.setCan_buy_now(can_buy_now);
                    String train_no = data_index.getString("train_no");// train_no列车号
                    train.setTrain_no(train_no);
                    String distance = "0";// 站站距离
                    String sfz = data_index.getString("start_station_name");// 这列车的始发站
                    train.setSfz(sfz);
                    train.setStart_station_name(sfz);
                    String zdz = data_index.getString("end_station_name");// 这列车的终点站
                    train.setZdz(zdz);
                    train.setEnd_station_name(zdz);
                    try {
                        train.setDistance(Float.valueOf(distance));
                    }
                    catch (Exception e) {
                        train.setDistance(0);
                    }
                    String costTime = data_index.getString("lishi");// 运行时间
                    train.setCosttime(costTime);
                    String run_time_minute = data_index.getString("lishiValue");// 历时分钟合计
                    train.setRun_time_minute(run_time_minute);
                    String wzyp = data_index.getString("wz_num");// 无座余票
                    wzyp = chuliyupiao(wzyp);// 处理 余票数量是有的问题
                    train.setWzyp(wzyp);
                    String yzyp = data_index.getString("yz_num");// 硬座余票
                    // String s_yzyp = getyzyp(wzyp, yzyp);
                    yzyp = chuliyupiao(yzyp);// 处理 余票数量是有的问题
                    train.setYzyp(yzyp);
                    String rzyp = data_index.getString("rz_num");// 软座余票
                    rzyp = chuliyupiao(rzyp);
                    train.setRzyp(rzyp);
                    String rz2yp = data_index.getString("ze_num");// 二等软座余票
                    rz2yp = chuliyupiao(rz2yp);
                    train.setRz2yp(rz2yp);
                    String rz1yp = data_index.getString("zy_num");// 一等软余票
                    rz1yp = chuliyupiao(rz1yp);
                    train.setRz1yp(rz1yp);
                    String tdzyp = data_index.getString("tz_num");// 特等座余票
                    tdzyp = chuliyupiao(tdzyp);
                    train.setTdzyp(tdzyp);
                    String swzyp = data_index.getString("swz_num");// 商务座余票
                    swzyp = chuliyupiao(swzyp);
                    train.setSwzyp(swzyp);
                    String ywyp = data_index.getString("yw_num");// 硬卧余票
                    ywyp = chuliyupiao(ywyp);
                    train.setYwyp(ywyp);
                    String rwyp = data_index.getString("rw_num");// 软卧余票
                    rwyp = chuliyupiao(rwyp);
                    train.setRwyp(rwyp);
                    String gwyp = data_index.getString("gr_num");// 高级软卧余票
                    gwyp = chuliyupiao(gwyp);
                    train.setGwyp(gwyp);
                    String qtxb_num = data_index.getString("qt_num");// 其他席别余票数量
                    qtxb_num = chuliyupiao(qtxb_num);
                    train.setQtxb_num(qtxb_num);
                    String memo = data_index.getString("note");
                    train.setMemo(memo);
                    if (isSearchprice || issearchlicheng) {
                        String json1 = "";
                        String from_station_no = data_index.getString("from_station_no");
                        String to_station_no = data_index.getString("to_station_no");
                        String seat_types = data_index.getString("seat_types");
                        String mcckey = traincode + from_station_no + to_station_no + seat_types;
                        mcckey = mcckey.replaceAll("-", "");

                        futures.add(completionService.submit(new Tielu12306PriceThread(train, train_no,
                                from_station_no, to_station_no, seat_types, time, traincode, randominti, 0L,
                                isSearchprice, issearchlicheng)));
                    }
                    else {
                        listtrain.add(train);
                    }
                }

                if (isSearchprice || issearchlicheng) {
                    try {
                        for (int t = 0; t < datasArray.size(); t++) {
                            // System.out.println("Invocation:" + t);
                            Future<Train> result = completionService.poll(3000, TimeUnit.MILLISECONDS);
                            if (result == null) {
                                // System.out.println(new Date() +
                                // ":Worker Timedout:" + t);
                                // So lets cancel the first futures we find that
                                // havent completed
                                for (Future future : futures) {
                                    // System.out.println("Checking future");
                                    if (future.isDone()) {
                                        continue;
                                    }
                                    else {
                                        future.cancel(true);
                                        // System.out.println("Cancelled");
                                        break;
                                    }
                                }
                                continue;
                            }
                            else {
                                try {
                                    if (result.isDone() && !result.isCancelled() && result.get() != null) {// &&
                                                                                                           // !result.isCancelled()
                                                                                                           // &&
                                                                                                           // result.get()
                                        Train train = result.get();
                                        listtrain.add(train);
                                    }
                                    else {
                                        continue;
                                    }
                                    // else if (result.isDone() &&
                                    // !result.isCancelled() && !result.get()) {
                                    // System.out.println(new Date() +
                                    // ":Worker Failed");
                                    // }
                                }
                                catch (ExecutionException ee) {
                                    ee.printStackTrace(System.out);
                                }
                            }
                        }
                    }
                    catch (InterruptedException ie) {
                        // ie.printStackTrace();
                    }
                    finally {
                        for (Future<Train> f : futures) {
                            f.cancel(true);
                        }
                        threadPool2.shutdown();
                    }
                }
            }
        }
        catch (Exception e) {
            // e.printStackTrace();
            // System.out.println("jsonerr::" + json);
        }
        // System.out.println(startcity + ":" + endcity + ":" + time +
        // ":process2:" + (System.currentTimeMillis() - l1));
        return listtrain;
    }

    private String chuliyupiao(String s_yzyp) {
        if ("有".equals(s_yzyp.trim())) {
            s_yzyp = (new Random().nextInt(100) + 100) + "";
        }
        if ("无".equals(s_yzyp.trim())) {
            s_yzyp = "0";
        }
        return s_yzyp;
    }

    private String getyzyp(String wzyp, String yzyp) {
        Integer int_wzyp = 0;
        Integer int_yzyp = 0;
        try {
            int_wzyp = Integer.parseInt(wzyp);
            int_yzyp = Integer.parseInt(yzyp);

            if (int_wzyp > 0) {
                int_yzyp = int_yzyp + int_wzyp;
            }
            yzyp = int_yzyp + "";
        }
        catch (Exception e) {
        }
        return yzyp;
    }

    /**
     * 获取12306的JSON数据
     * 
     * @param l1
     * @param randominti
     * @param startcity
     * @param endcity
     * @param time
     * @param isfrom12306
     *            数据是否来至12306,否则缓存缓存
     * @param purposecodes
     *            订票类别
     * @return
     * @time 2014年12月12日 下午6:59:22
     * @author chendong
     */
    private String get12306searchJSON(long l1, int randominti, String startcity, String endcity, String time,
            boolean isfrom12306, String purposecodes) {
        String mcckey = startcity + endcity + time;
        mcckey = mcckey.replaceAll("-", "");
        String json = "-1";
        //        if (MemCached.getInstance().get(mcckey) == null || isfrom12306) {
        if (1 == 1) {
            l1 = System.currentTimeMillis();
            String url = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=" + purposecodes + "&queryDate=" + time
                    + "&from_station=" + startcity + "&to_station=" + endcity;
            String search_url = url;
            // json = getjsondata(search_url, randominti, l1, time, startcity,
            // endcity);
            // json = HttpUtils.Get_https(search_url, 4000);

            for (int i = 0; i < 5; i++) {
                json = getjsondatabythreadFrom12306(search_url, l1);
                if (json.indexOf("没有符合") <= 0) {
                    break;
                }
            }

            long l2 = System.currentTimeMillis();
            if (!"-1".equals(json) && json.indexOf("\"flag\":true") > 0) {
                Date date = new Date(1000 * 1 * 40);// 40秒的缓存

                Long l3 = System.currentTimeMillis();
                creatAddMemcachedThreadThread(mcckey, json, date, 2, time);
            }
        }
        else {
            l1 = System.currentTimeMillis();
            //            json = MemCached.getInstance().get(mcckey).toString();

        }
        return json;
    }

    /**
     * 统一线程处理数据
     * 
     * @param mcckey
     * @param value
     * @param date
     * @param type //1:存余票缓存,2:存价格信息到数据库和缓存
     * @time 2015年5月5日 下午6:41:37
     * @author chendong
     */
    public void creatAddMemcachedThreadThread(String mcckey, String value, Date date, int type, String time) {// 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(1);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        // 将线程放入池中进行执行
        t1 = new AddMemcachedThread(mcckey, value, date, type, time);
        pool.execute(t1);
        pool.shutdown();
    }

    public static String getjsondatabythreadFrom12306(String search_url, long l1) {
        String resultJson = "-1";
        int countthread = 1;
        ExecutorService threadPool2 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futures = new ArrayList<Future<String>>(countthread);
        CompletionService<String> completionService = new ExecutorCompletionService<String>(threadPool2);
        futures.add(completionService.submit(new Get12306dataThread(search_url, l1)));
        try {
            Thread.sleep(200L);
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        futures.add(completionService.submit(new Get12306dataThread(search_url, l1)));
        // futures.add(completionService.submit(new
        // Get12306dataThread(search_url)));
        // Long timeouttime = getOuttime();
        Long timeouttime = 6000L;
        try {
            for (int i = 0; i < 1; i++) {
                Future<String> result = completionService.poll(timeouttime, TimeUnit.MILLISECONDS);
                if (result == null) {
                    for (Future future : futures) {
                        if (future.isDone()) {
                            continue;
                        }
                        else {
                            future.cancel(true);
                            break;
                        }
                    }
                }
                else {
                    try {
                        if (result.isDone() && !result.isCancelled() && result.get() != null) {
                            resultJson = result.get();
                        }
                        else {
                        }
                    }
                    catch (ExecutionException ee) {
                        ee.printStackTrace(System.out);
                    }
                }

            }
        }
        catch (Exception e) {
        }
        finally {
            for (Future<String> f : futures) {
                f.cancel(true);
            }
            threadPool2.shutdown();
        }
        return resultJson;
    }

    private String getjsondata(String search_url, int randominti, long l1, String time, String startcity, String endcity) {
        l1 = System.currentTimeMillis();
        String json = "-1";
        // String rep_url =
        // "http://www.rep.com/Reptile/traininit?datatypeflag=103";
        String rep_url = "";
        json = ZhuanfaUtil.zhuanfa1(time, startcity, endcity, 4000, rep_url);
        if ("-1".equals(json) || "".equals(json)) {
            json = ZhuanfaUtil.gethttpclientdata(search_url, 4000L);
        }
        if ("-1".equals(json) || "".equals(json)) {
            json = HttpUtils.Get_https(search_url, 4000);
        }
        return json;
    }

    public float foamtPrice(String price) {
        float fprice = 0F;
        try {
            price = price.replace("¥", "").replace("?", "").replace("楼", "");
            fprice = Float.valueOf(price);
        }
        catch (Exception e) {
        }
        return fprice;
    }

    /**
     * 日期的小时加2
     * 
     * @param type
     *            返回类型1:yyyy-MM-dd，2yyyyMMdd
     * @return
     * @time 2014年9月15日 上午11:51:36
     * @author chendong
     */
    public String addtime(int type) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // 设置当前日期
        c.add(Calendar.HOUR, 2); // 日期小时加2,Calendar.DATE(天),Calendar.HOUR(小时)
        Date date = c.getTime(); // 结果
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (type == 2) {
            df = new SimpleDateFormat("yyyyMMdd HH:mm");
        }
        return df.format(date);
    }

    /**
     * 得到当前时间
     * 
     * @param type
     *            返回类型1:yyyy-MM-dd，2yyyyMMdd
     * @return
     * @time 2014年9月12日 下午7:08:54
     * @author chendong
     */
    public String GetNowDate(int type) {
        String temp_str = "";
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (type == 2) {
            sdf = new SimpleDateFormat("yyyyMMdd");
        }
        temp_str = sdf.format(dt);
        return temp_str;
    }

    private String gettraintype(String traincode) {
        // String traintype = "普快";
        // if (traincode != null && traincode.length() > 0) {
        // if (traincode.startsWith("G") || traincode.startsWith("C")) {
        // traintype = "高铁/城际";
        // }
        // else if (traincode.startsWith("D")) {
        // traintype = "动车";
        // }
        // else if (traincode.startsWith("Z")) {
        // traintype = "直达";
        // }
        // else if (traincode.startsWith("T")) {
        // traintype = "特快";
        // }
        // else if (traincode.startsWith("K")) {
        // traintype = "快速";
        // }
        // }
        return traincode.substring(0, 1);
    }

    @Override
    public String getHtml(String startcity, String endcity, String time, FlightSearch param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Train> process(String html, String startcity, String endcity, String time, FlightSearch param) {
        // TODO Auto-generated method stub
        return process(html, startcity, endcity, time, param, "ADULT");
    }

}
