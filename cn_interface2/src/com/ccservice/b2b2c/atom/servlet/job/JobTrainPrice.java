package com.ccservice.b2b2c.atom.servlet.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ccservice.b2b2c.atom.component.WriteLog;

public class JobTrainPrice {

    public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat HHmmss = new SimpleDateFormat("HH:mm");

    public static void stat() throws ParseException {
        int cishu = 0;
        for (int j2 = 60; j2 >= 0; j2--) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String, String> toMap = Train12306StationInfo.GetMap();
            String pattern = "2015-08-31";
            Date date = yyyyMMdd.parse(pattern);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(5, j2);

            String train_date = yyyyMMdd.format(c.getTime());
            int v = 0;
            String fo = "@hzh|杭州|HZH|hangzhou|hz|41@nji|南京|NJH|nanjing|nj|53@cdu|成都|CDW|chengdu|cd|23@hfe|合肥|HFH|hefei|hf|34@cqi|重庆|CQW|chongqing|cq|7@jna|济南|JNK|jinan|jn|43@tji|天津|TJP|tianjin|tj|15@szh|苏州|SZH|suzhou|sz|376@sya|沈阳|SYT|shenyang|sy|58@qda|青岛|QDK|qingdao|qd|334@xme|厦门|XMS|xiamen|xm|439@nch|南昌|NCG|nanchang|nc|52@lzh|兰州|LZJ|lanzhou|lz|50@lsa|拉萨|LSO|lasa|ls|48@kmi|昆明|KMM|kunming|km|46@cch|长春|CCT|changchun|cc|18@heb|哈尔滨|HBB|haerbin|heb|31@fzh|福州|FZS|fuzhou|fz|26@gya|贵阳|GIW|guiyang|gy|28@hht|呼和浩特|HHC|huhehaote|hhht|37@jna|济南|JNK|jinan|jn|43@nni|南宁|NNZ|nanning|nn|55@sjz|石家庄|SJP|shijiazhuang|sjz|57@tyu|太原|TYV|taiyuan|ty|63@hko|海口|VUQ|haikou|hk|39@ych|银川|YIJ|yinchuan|yc|71";
            String[] cc = fo.split("@");
            try {
                for (int i = 1; i < cc.length; i++) {
                    String aa = cc[(i)];
                    String foname = aa.split("[|]")[1];
                    String from_station = aa.split("[|]")[2];
                    for (Map.Entry<String, String> entry : toMap.entrySet()) {
                        String toname = entry.getKey();
                        if (!foname.equals(toname)) {
                            try {
                                String to_station = entry.getValue();
                                v++;
                                if (v == 40) {
                                    Thread.sleep(1500);
                                    v = 0;
                                }
                                ++cishu;
                                WriteLog.write("JobTrainPrice12306f", "出发站:" + foname + ";到达站:" + toname + ";日期:"
                                        + train_date + ";总次数:" + cishu);
                                String time = HHmmss.format(new Date());
                                String stop = "06:40";
                                if (!time.equals(stop)) {
                                    creatAddJobTrainPrice(train_date, from_station, to_station);
                                }
                                else {
                                    System.out.println("06:40程序停止16个小时!");
                                    Thread.sleep(1000 * 60 * 60 * 16);
                                }
                            }
                            catch (Exception e) {
                                System.out.println(df.format(new Date()));
                            }
                        }
                    }
                }
            }
            catch (Exception localException2) {

            }
            finally {
                System.out.println("-----程序结束-----");
            }
        }
    }

    public static void main(String[] args) {
        // for (int j2 = 0; j2 <= 60; j2++) {//正着跑
        // for (int j2 = 60; j2 >= 0; j2--) {//反着跑
        // System.out.println(j2);
        // }
        String time = HHmmss.format(new Date());
        String stop = "06:40";
        if (!time.equals(stop)) {
        }
        else {
            System.out.println("06:40程序停止16个小时!");
        }
    }

    public static void creatAddJobTrainPrice(String train_date, String from_station, String to_station) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Thread t1 = null;
        t1 = new TrainPriceJob(train_date, from_station, to_station);
        pool.execute(t1);
        pool.shutdown();
    }

}