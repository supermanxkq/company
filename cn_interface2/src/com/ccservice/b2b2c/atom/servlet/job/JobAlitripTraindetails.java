package com.ccservice.b2b2c.atom.servlet.job;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.service12306.TrainUtil;
import com.ccservice.b2b2c.atom.servlet.job.thread.JobAlitripTraindetailsThread;
import com.ccservice.b2b2c.atom.train.data.ZhuanfaUtil;
import com.ccservice.b2b2c.base.trainno.TrainNo;

/**
 * 阿里旅行 2.3.5车次详细信息接口 定时任务
 * 
 * @time 2015年3月17日 上午11:22:06
 * @author chendong
 */
public class JobAlitripTraindetails implements Job {
    static String base_chexing = "G,D,T,Z,K,C,S,Y,1,2,3,4,5,6,7,8,9";

    // static String base_chexing = "G";
    // static String base_chexing = "D,T,Z,K,C,S,Y";

    // static String base_chexing = "C,S,Y,1,2,3,4,5,6,7,8,9";
    // static String base_chexing = "1,2,3,4,5,6,7,8,9";
    // static String base_chexing = "8166";

    // static String base_chexing = "K50";

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        execute();
    }

    public static void main(String[] args) {
        // execute();
        // execute_thread();
        gzip();
    }

    private static void execute() {
        String biaoti = "traincode\ttraincodetype\tstopno\ttraintypeid\ttraintype\tfromstation\ttostation\tisfirst\tislast\tarrivetime\tsfromtime\tfromtime\tstotime\ttotime\tstaytime\truntime\truntime_minute\tarriveday\tdistance\tyz\trz\tywx\tywz\tyws\trwx\trws\tgjrwx\tgjrws\tedz\tydz\ttdz\tggz\tbz\tswz\tremark\tstatus";
        String temp_line = biaoti;
        write("traindetails.txt", temp_line);
        String[] base_chexings = base_chexing.split(",");
        for (int i = 0; i < base_chexings.length; i++) {

            List<TrainNo> list = Server
                    .getInstance()
                    .getTrainService()
                    .findAllTrainNo(
                            "WHERE C_STATION_NO='01' AND C_STATION_TRAIN_CODE like '" + base_chexings[i] + "%'",
                            " ORDER BY C_STATION_TRAIN_CODE ", -1, 0);
            for (int j = 0; j < list.size(); j++) {
                TrainNo trainno = list.get(j);
                List<TrainNo> list2 = Server
                        .getInstance()
                        .getTrainService()
                        .findAllTrainNo("WHERE C_STATION_TRAIN_CODE = '" + trainno.getStation_train_code() + "'",
                                " ORDER BY C_STATION_NO ", -1, 0);

                getonelinebytrainno(list2);

            }
            // System.out.println(base_chexings[i] + ":" + list.size());
        }
        gzip();
    }

    private static void execute_thread() {
        String[] base_chexings = base_chexing.split(",");
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(base_chexings.length);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        for (int i = 0; i < base_chexings.length; i++) {
            List<TrainNo> list = Server
                    .getInstance()
                    .getTrainService()
                    .findAllTrainNo(
                            "WHERE C_STATION_NO='01' AND C_STATION_TRAIN_CODE like '" + base_chexings[i] + "%'",
                            " ORDER BY C_STATION_TRAIN_CODE ", -1, 0);
            // 将线程放入池中进行执行
            t1 = new JobAlitripTraindetailsThread(list);
            pool.execute(t1);
        }
        pool.shutdown();
    }

    public static void getonelinebytrainno(List<TrainNo> trainnos) {
        String train_class_name = trainnos.get(0).getTrain_class_name() == null ? "" : trainnos.get(0)
                .getTrain_class_name().trim();

        for (int j = 0; j < trainnos.size(); j++) {
            TrainNo trainno_s = trainnos.get(j);
            for (int i = j + 1; i < trainnos.size(); i++) {
                TrainNo trainno = trainnos.get(i);
                String Traincode = isnullbyString(trainno.getStation_train_code());// 车次
                String Traincodetype = Traincode.substring(0, 1);// 车次类别
                String Stopno = isnullbyString(trainno.getStation_no());// 站次

                String Traintypeid = getTraintypeid(Traincodetype, train_class_name);// 车型id
                String Traintype = getTraintypebyTraintypeid(Traintypeid);// 车型
                int Isfirst = 0 == j ? 1 : 0;// 是否为始发站
                int Islast = (trainnos.size() - 1 == i) ? 1 : 0;// 是否为终点站

                String Fromstation = isnullbyString(trainno_s.getStation_name());// 出发站
                String Tostation = isnullbyString(trainno.getStation_name());// 到达站
                String Arrivetime = isnullbyString(trainno.getArrive_time());// 到达时间
                                                                             // Arrivetime
                                                                             // 是指火车到达出发站的时间
                String Sfromtime = isnullbyString(trainno_s.getStart_time()).replace(":", "");// 排序 出发时间
                String Fromtime = isnullbyString(trainno.getStart_time());// 出发时间
                String Stotime = isnullbyString(trainno_s.getArrive_time()).replace(":", "");// 排序 到达时间
                String Totime = Arrivetime;// 到达时间 Totime火车到达到达站是时间
                String Staytime = isnullbyString(trainno.getStopover_time()).replace("分钟", "");// 停留时间
                String Runtime = isnullbyString(trainno.getRuntime() == null ? "00:00" : trainno.getRuntime());// 运行时间
                Integer runtime_minute = getruntime_minutebyruntime(Runtime);// isnullbyString(trainno.getRuntime()
                                                                             // ==
                                                                             // null
                                                                             // ?
                                                                             // "0"
                                                                             // :
                                                                             // trainno.getRuntime());//运行时间（分钟）
                if (runtime_minute > 60) {
                    Runtime = Runtime.replace(":", "小时") + "分";
                }
                else {
                    Runtime = Runtime.replace("00:", "") + "分";
                }
                String Arriveday = "-1";// 到达天数
                String Distance = "-1";// 距离（公里）
                String trainprice_mcckey = Traincode + "_" + isnullbyString(trainno_s.getStation_no()) + "_" + Stopno;
                List clist = getpricefromdb(trainprice_mcckey);

                String Yz = getpricebyjson(clist, "Yz");// 硬座票价
                String Rz = getpricebyjson(clist, "Rz");// 软座票价

                String Ywx = "-1";// 硬卧下票价
                String Ywz = "-1";// 硬卧中票价
                String Yws = "-1";// 硬卧上票价
                String ywprice = getywprice(clist);
                if (!"-1".equals(ywprice)) {
                    String[] ywprices = ywprice.split("/");
                    Ywx = ywprices[2];
                    Ywz = ywprices[1];
                    Yws = ywprices[0];
                }
                String Rwx = "-1";// 软卧下票价
                String Rws = "-1";// 软卧上票价
                String rwprice = getrwprice(clist);
                if (!"-1".equals(rwprice)) {
                    String[] rwprices = rwprice.split("/");
                    Rwx = rwprices[1];
                    Rws = rwprices[0];
                }

                String Gjrwx = "-1";// 高级软卧下票价
                String Gjrws = "-1";// 高级软卧上票价
                String gjrwprice = getgjrwprice(clist);
                if (!"-1".equals(gjrwprice)) {
                    String[] gjrwprices = gjrwprice.split("/");
                    Gjrwx = gjrwprices[0];
                    Gjrws = gjrwprices[1];
                }

                String Edz = getpricebyjson(clist, "Edz");// 二等座票价
                String Ydz = getpricebyjson(clist, "Ydz");// 一等座票价
                String Tdz = getpricebyjson(clist, "Tdz");// 特等座票价
                String Ggz = getpricebyjson(clist, "Ggz");// 观光座票价
                String Bz = getpricebyjson(clist, "Bz");// 包座票价
                String Swz = getpricebyjson(clist, "Swz");// 商务座票价
                // 是否更新了数据
                boolean isupdate = updatenopricetoupdateprice(trainno_s, trainno, trainnos.get(0), Yz, ywprice,
                        rwprice, gjrwprice, Edz);

                if (isupdate) {
                }
                else {
                    Stopno = Integer.parseInt(Stopno) + "";

                    String temp_line = Traincode + "\t" + Traincodetype + "\t" + Stopno + "\t" + Traintypeid + "\t"
                            + Traintype + "\t" + Fromstation + "\t" + Tostation + "\t" + Isfirst + "\t" + Islast + "\t"
                            + Arrivetime + "\t" + Sfromtime + "\t" + Fromtime + "\t" + Stotime + "\t" + Totime + "\t"
                            + Staytime + "\t" + Runtime + "\t" + runtime_minute + "\t" + Arriveday + "\t" + Distance
                            + "\t" + Yz + "\t" + Rz + "\t" + Ywx + "\t" + Ywz + "\t" + Yws + "\t" + Rwx + "\t" + Rws
                            + "\t" + Gjrwx + "\t" + Gjrws + "\t" + Edz + "\t" + Ydz + "\t" + Tdz + "\t" + Ggz + "\t"
                            + Bz + "\t" + Swz;
                    write("traindetails.txt", temp_line);

                    // System.out.print(temp_line);
                }
            }
        }
    }

    private static Integer getruntime_minutebyruntime(String runtime) {
        Integer runtime_minute = 0;
        String[] runtime_minutes = runtime.split(":");
        try {
            runtime_minute = Integer.parseInt(runtime_minutes[0]) * 60 + Integer.parseInt(runtime_minutes[1]);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return runtime_minute;
    }

    /**
     * 处理没有价格的那些车次
     * 
     * @time 2015年3月18日 下午7:19:45
     * @author chendong
     * @param trainno
     * @param trainno_s
     * @param edz
     * @param gjrwprice
     * @param rwprice
     * @param ywprice
     * @param yz
     * @param trainNo2
     */
    private static boolean updatenopricetoupdateprice(TrainNo trainno_s, TrainNo trainno, TrainNo trainNo_0, String yz,
            String ywprice, String rwprice, String gjrwprice, String edz) {
        boolean isupdate = false;// 是否更新了数据
        String station_no_s = trainno_s.getStation_no().trim();
        String station_no_e = trainno.getStation_no().trim();
        String station_train_code = trainNo_0.getStation_train_code().trim();
        String train_no = trainNo_0.getTrain_no().trim();
        // Yz + "\t" + Rz + "\t" + Ywx + "\t" + Ywz + "\t" + Yws + "\t" + Rwx +
        // "\t" + Rws + "\t"
        // + Gjrwx + "\t" + Gjrws + "\t" + Edz + "\t" + Ydz + "\t" + Tdz + "\t"
        // + Ggz + "\t" + Bz + "\t"
        // + Swz

        if (station_train_code.startsWith("D") || station_train_code.startsWith("G")
                || station_train_code.startsWith("C")) {
            if ("0".equals(edz)) {
                // update12306price(train_no, station_train_code, station_no_s,
                // station_no_e);
                // System.out.println(train_no + "=" + yz + ":" + ywprice + ":"
                // + rwprice + ":" + gjrwprice + ":" + edz
                // + "==========" + station_train_code + "_" + station_no_s +
                // "_" + station_no_e);
                // System.out.println(train_no + ":" + station_train_code +
                // ":update");
                isupdate = true;
            }
        }
        else if (station_train_code.startsWith("T") || station_train_code.startsWith("Z")
                || station_train_code.startsWith("K")) {
            if ("0".equals(yz)) {
                // update12306price(train_no, station_train_code, station_no_s,
                // station_no_e);
                System.out.println(train_no + "=" + yz + ":" + ywprice + ":" + rwprice + ":" + gjrwprice + ":" + edz
                        + "==========" + station_train_code + "_" + station_no_s + "_" + station_no_e);
                System.out.println(train_no + ":" + station_train_code + ":update");
                isupdate = true;
            }
        }
        return isupdate;
    }

    /**
     * 
     * 
     * @param train_no
     *            列车号
     * @param station_train_code
     *            车次
     * @param from_station_no
     *            出发站号
     * @param to_station_no
     *            到达站号
     * @time 2015年3月2日 下午6:50:55
     * @author Administrator
     */
    private static void update12306price(String train_no, String station_train_code, String from_station_no,
            String to_station_no) {
        String json = getpriceJsonbyurl(train_no, station_train_code, from_station_no, to_station_no);
        String mcckey = station_train_code + "_" + from_station_no + "_" + to_station_no;
        if (!"-1".equals(json)) {
            if (json.indexOf("\"3\":\"-2\",\"1\":\"-2\",\"4\":\"-2\"") >= 0) {
                System.out.println(mcckey + ":" + json);
            }
            else {
                deletepricefromdb(mcckey);
                setpricefromdb(mcckey, json);
            }
        }
    }

    private static void deletepricefromdb(String mcckey) {
        try {
            String sqldelete = "delete from T_trainprice where C_MCCKEY='" + mcckey + "'";
            Server.getInstance().getSystemService().excuteEaccountBySql(sqldelete);
        }
        catch (Exception e) {
        }
    }

    public static void setpricefromdb(String key, String value) {
        try {
            String sqlinsert = "insert into T_TRAINPRICE(C_MCCKEY,C_PRICE,C_CTIME) values ('" + key + "','" + value
                    + "','" + new Timestamp(System.currentTimeMillis()) + "')";
            Server.getInstance().getSystemService().excuteEaccountBySql(sqlinsert);
        }
        catch (Exception e) {
        }
    }

    private static String getpriceJsonbyurl(String train_no, String station_train_code, String from_station_no,
            String to_station_no) {
        String train_date = TimeUtil.gettodaydatebyfrontandback(1, 50);
        String json = "-1";
        int temp_i = 0;
        for (int i = 0; i < 35; i++) {
            temp_i = i;
            int temp_type = i % 4;
            String url = "https://kyfw.12306.cn/otn/leftTicket/queryTicketPrice?train_no=" + train_no
                    + "&from_station_no=" + from_station_no + "&to_station_no=" + to_station_no + "&seat_types="
                    + TrainUtil.getseat_typesby_trainclasstype(station_train_code, temp_type) + "&train_date="
                    + train_date;
            json = ZhuanfaUtil.zhuanfa(url, "", "GBK", "", "get", 2000);
            if (json.indexOf("\"3\":\"-2\",\"1\":\"-2\",\"4\":\"-2\"") >= 0) {
                url = "https://kyfw.12306.cn/otn/leftTicket/queryTicketPrice?train_no=" + getnewtrain_no(train_no, 0)
                        + "&from_station_no=" + from_station_no + "&to_station_no=" + to_station_no + "&seat_types="
                        + TrainUtil.getseat_typesby_trainclasstype(station_train_code, temp_type) + "&train_date="
                        + train_date;
                System.out.println(url);
                json = ZhuanfaUtil.zhuanfa(url, "", "GBK", "", "get", 2000);
            }
            if (json.indexOf("\"3\":\"-2\",\"1\":\"-2\",\"4\":\"-2\"") >= 0) {
                url = "https://kyfw.12306.cn/otn/leftTicket/queryTicketPrice?train_no=" + getnewtrain_no(train_no, 1)
                        + "&from_station_no=" + from_station_no + "&to_station_no=" + to_station_no + "&seat_types="
                        + TrainUtil.getseat_typesby_trainclasstype(station_train_code, temp_type) + "&train_date="
                        + train_date;
                System.out.println(url);
                json = ZhuanfaUtil.zhuanfa(url, "", "GBK", "", "get", 2000);
            }
            System.out.println("json:" + json);
            // json = ZhuanfaUtil.gethttpclientdata(url, 2000L);
            if ("-1".equals(json)) {
                continue;
            }
            else {
                break;
            }
        }
        // System.out.println(temp_i);
        return json;
    }

    private static String getnewtrain_no(String train_no, int count) {
        if (train_no.lastIndexOf("E0") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "F0";
        }
        else if (train_no.lastIndexOf("11") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "12";
        }
        else if (train_no.lastIndexOf("0N") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0O";
        }
        else if (train_no.lastIndexOf("0T") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0U";
        }
        else if (train_no.lastIndexOf("01") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0B";
        }
        else if (train_no.lastIndexOf("02") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "03";
        }
        else if (train_no.lastIndexOf("05") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "06";
        }
        else if (train_no.lastIndexOf("06") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "08";
        }
        else if (train_no.lastIndexOf("07") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "08";
        }
        else if (train_no.lastIndexOf("09") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0A";
        }
        else if (train_no.lastIndexOf("82") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "84";
        }
        else if (train_no.lastIndexOf("0X") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0Y";
        }
        else if (train_no.lastIndexOf("0B") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0C";
        }
        else if (train_no.lastIndexOf("0D") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0E";
        }
        else if (train_no.lastIndexOf("0E") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0F";
        }
        else if (train_no.lastIndexOf("0F") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0G";
        }
        else if (train_no.lastIndexOf("0G") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0H";
        }
        else if (train_no.lastIndexOf("0R") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "09";
        }
        else if (train_no.lastIndexOf("0U") == train_no.length() - 2) {
            train_no = train_no.substring(0, train_no.length() - 2) + "0W";
        }
        else if (train_no.lastIndexOf("0S") == train_no.length() - 2) {
            if (count == 1) {
                train_no = train_no.substring(0, train_no.length() - 2) + "0T";
            }
            else {
                train_no = train_no.substring(0, train_no.length() - 2) + "09";
            }
        }
        return train_no;
    }

    /**
     * 根据车型id获取车型 //1普快2新空普快3普客4快速5新空普客6城际高速7动车组8高速动车9新空快速10新空特快11特快12新空直达
     * 
     * @param traintypeid
     * @return
     * @time 2015年3月18日 下午12:29:37
     * @author chendong
     */
    private static String getTraintypebyTraintypeid(String traintypeid) {
        String traintype = "普快";
        if ("1".equals(traintypeid)) {
            traintype = "普快";
        }
        else if ("2".equals(traintypeid)) {
            traintype = "新空普快";
        }
        else if ("3".equals(traintypeid)) {
            traintype = "普客";
        }
        else if ("4".equals(traintypeid)) {
            traintype = "快速";
        }
        else if ("5".equals(traintypeid)) {
            traintype = "新空普客";
        }
        else if ("6".equals(traintypeid)) {
            traintype = "城际高速";
        }
        else if ("7".equals(traintypeid)) {
            traintype = "动车组";
        }
        else if ("8".equals(traintypeid)) {
            traintype = "高速动车";
        }
        else if ("9".equals(traintypeid)) {
            traintype = "新空快速";
        }
        else if ("10".equals(traintypeid)) {
            traintype = "新空特快";
        }
        else if ("11".equals(traintypeid)) {
            traintype = "特快";
        }
        else if ("12".equals(traintypeid)) {
            traintype = "新空直达";
        }
        return traintype;
    }

    /**
     * //1普快2新空普快3普客4快速5新空普客6城际高速7动车组8高速动车9新空快速10新空特快11特快12新空直达
     * 
     * @param Traincode
     * @return
     * @time 2015年3月18日 下午12:01:52
     * @author chendong
     */
    private static String getTraintypeid(String Traincode, String train_class_name) {
        String traintypeid = "1";
        if ("普快".equals(train_class_name)) {
            traintypeid = "1";
        }
        else if (Traincode.startsWith("KAAAA")) {
            traintypeid = "2";
        }
        else if ("快慢".equals(train_class_name) || "普客".equals(train_class_name)) {
            traintypeid = "3";
        }
        else if ("快速".equals(train_class_name)) {
            traintypeid = "4";
        }
        else if ("1111111111".equals(train_class_name)) {
            traintypeid = "5";
        }
        else if (Traincode.startsWith("C")) {
            traintypeid = "6";
        }
        else if (Traincode.startsWith("D")) {
            traintypeid = "7";
        }
        else if (Traincode.startsWith("G")) {
            traintypeid = "8";
        }
        else if ("普快".equals(train_class_name)) {
            traintypeid = "9";
        }
        else if ("特快".equals(train_class_name)) {
            traintypeid = "10";
        }
        else if (Traincode.startsWith("11111111111111111")) {
            traintypeid = "11";
        }
        else if (Traincode.startsWith("Z")) {
            traintypeid = "12";
        }
        return traintypeid;
    }

    /**
     * 根据不同的坐席获取价格
     * 
     * @param clist
     * @param string
     * @return
     * @time 2015年3月17日 下午6:23:24
     * @author chendong
     */
    private static String getpricebyjson(List clist, String string) {
        String resultstring = "0";
        if (clist.size() > 0) {
            Map m = (Map) clist.get(0);
            try {
                String resultstring_1 = m.get("C_PRICE").toString();
                JSONObject jsonobject = JSONObject.parseObject(resultstring_1);
                JSONObject pricejsonobject = new JSONObject();
                pricejsonobject = (JSONObject) jsonobject.get("data");
                if ("Yz".equals(string)) {
                    try {
                        resultstring = pricejsonobject.getString("A1");// 硬座票价
                    }
                    catch (Exception e) {
                    }
                }
                else if ("Rz".equals(string)) {
                    try {
                        resultstring = pricejsonobject.getString("A2");// 软座票价
                    }
                    catch (Exception e) {
                    }
                }
                else if ("Edz".equals(string)) {
                    try {
                        resultstring = pricejsonobject.getString("O");// 二等软座票价
                    }
                    catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                else if ("Ydz".equals(string)) {
                    try {
                        resultstring = pricejsonobject.getString("M");// 一等软座票价
                    }
                    catch (Exception e) {
                    }
                }
                else if ("Tdz".equals(string)) {
                    try {
                        resultstring = pricejsonobject.getString("P");// 特等软座票价
                    }
                    catch (Exception e) {
                    }
                }
                else if ("Swz".equals(string)) {
                    try {
                        resultstring = pricejsonobject.getString("A9");// 商务座
                    }
                    catch (Exception e) {
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (resultstring == null) {
            resultstring = "0";

        }
        return resultstring.replace("￥", "");
    }

    private static String getgjrwprice(List clist) {
        String resultstring = "-1";
        if (clist.size() > 0) {
            Map m = (Map) clist.get(0);
            try {
                resultstring = m.get("C_GJRWPRICE").toString();
            }
            catch (Exception e) {
            }
        }
        return resultstring;
    }

    /**
     * 
     * 获取软卧的票价
     * 
     * @param clist
     * @return
     * @time 2015年3月17日 下午6:20:12
     * @author chendong
     */
    private static String getrwprice(List clist) {
        String resultstring = "-1";
        if (clist.size() > 0) {
            Map m = (Map) clist.get(0);
            try {
                resultstring = m.get("C_RWPRICE").toString();
            }
            catch (Exception e) {
                // TODO: handle exception
            }
        }
        return resultstring;
    }

    /**
     * 获取硬卧的票价
     * 
     * @param clist
     * @return
     * @time 2015年3月17日 下午5:22:49
     * @author chendong
     */
    private static String getywprice(List clist) {
        String resultstring = "-1";
        if (clist.size() > 0) {
            Map m = (Map) clist.get(0);
            try {
                resultstring = m.get("C_YWPRICE").toString();
            }
            catch (Exception e) {
            }
        }
        return resultstring;
    }

    public String getvaluebykey(List clist, String key) {
        String resultstring = "";
        if (clist.size() > 0) {
            Map m = (Map) clist.get(0);
            try {
                resultstring = m.get(key).toString();
            }
            catch (Exception e) {
                // TODO: handle exception
            }
        }
        return resultstring;
    }

    public static List getpricefromdb(String key) {
        String resultjson = "-1";
        String sqlselect = "SELECT TOP 1 C_PRICE,C_YWPRICE,C_RWPRICE,C_GJRWPRICE FROM T_TRAINPRICE WHERE C_MCCKEY='"
                + key + "'";
        List clist = new ArrayList();
        try {
            clist = Server.getInstance().getSystemService().findMapResultBySql(sqlselect, null);
        }
        catch (Exception e) {
        }
        return clist;
    }

    private static String isnullbyString(String data) {
        return data == null ? "-1" : data.trim();
    }

    /**
     * 写日志<br>
     * 
     * 写logString字符串到./log目录下的文件中
     * 
     * @param logString
     *            日志字符串
     * 
     * @author tower
     * 
     */
    public static void write(String fileNameHead, String logString) {
        PrintWriter printWriter = null;
        try {
            String logFilePathName = null;
            Calendar cd = Calendar.getInstance();// 日志文件时间
            String path = "D:/traindetails/"
                    + TimeUtil.gettodaydate(1).replace(" ", "").replace("-", "").replace(":", "") + "/";

            File fileParentDir = new File(path);// 判断log目录是否存在
            if (!fileParentDir.exists()) {
                fileParentDir.mkdirs();
            }
            if (fileNameHead == null || fileNameHead.equals("")) {
                logFilePathName = path + fileNameHead;// 日志文件名
            }
            else {
                logFilePathName = path + "/traindetails.txt";// 日志文件名

            }
            printWriter = new PrintWriter(new FileOutputStream(logFilePathName, true));// 紧接文件尾写入日志字符串
            printWriter.println(logString);

        }
        catch (FileNotFoundException e) {
            e.getMessage();
        }
        finally {
            if (printWriter != null) {
                printWriter.close();
                printWriter.flush();
            }
        }

    }

    /**
     * 压缩成gzip
     */
    public static void gzip() {
        String path = "D:/traindetails/" + TimeUtil.gettodaydate(1).replace(" ", "").replace("-", "").replace(":", "")
                + "/";
        try {
            String charset = "UTF-8";
            String outfile = path + "traindetails.gz";
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path + "traindetails.txt"), charset));

            BufferedOutputStream out = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(outfile)));

            String c;
            while ((c = in.readLine()) != null)
                out.write((c + " \n ").getBytes(charset));
            in.close();
            out.close();

            BufferedReader in2 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(
                    outfile)), charset));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 压缩traindetails.txt
     * 
     * @time 2015年3月26日 下午13:08:49
     * @author liangruiqing
     */

    public static void zip() {
        String path = "D:/traindetails/" + TimeUtil.gettodaydate(1).replace(" ", "").replace("-", "").replace(":", "")
                + "/";
        // 定义要压缩的文件
        File file = new File(path + "traindetails.txt");
        // 定义压缩文件的名称
        File zipFile = new File(path + "traindetails.zip");
        // 定义输入文件流
        InputStream input = null;
        // 定义压缩输出流
        ZipOutputStream zipOut = null;
        try {
            input = new FileInputStream(file);
            // 实例化压缩输出流,并制定压缩文件的输出路径 名字叫 traindetails.zip
            zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            // 设置注释
            zipOut.setComment("文件压缩");
            int temp = 0;
            while ((temp = input.read()) != -1) {
                zipOut.write(temp);
            }
            input.close();
            zipOut.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
