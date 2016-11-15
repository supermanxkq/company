package com.ccservice.b2b2c.atom.train;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.sms.SmsSender;
import com.ccservice.b2b2c.atom.train.data.thread.AddTrainDetailInfo;
import com.ccservice.b2b2c.base.dnsmaintenance.Dnsmaintenance;
import com.ccservice.b2b2c.framework.URLSupport;

public class TrainSupport extends URLSupport {
    Log log = LogFactory.getLog(TrainSupport.class);

    private String alermmobile;

    private String alermcontent;

    private String morningtime;

    private String nighttime;

    /**
     * 火车票订单客服通知短信
     * @param smssender
     * @return
     */
    public boolean sendAlermsms(SmsSender smssender) {
        try {
            SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
            Date date = ft.parse((ft.format(new Timestamp(System.currentTimeMillis()))));
            Date night = ft.parse(nighttime);
            Date morning = ft.parse(morningtime);
            if (date.after(night) && date.before(morning)) {
                Dnsmaintenance dns = Server.getInstance().getSystemService().findDnsmaintenance(1);
                String[] mobiles = alermmobile.split(",");
                smssender.sendSMS(mobiles, alermcontent, 0, 46, dns);
                return true;
            }
        }
        catch (Exception e) {
        }
        return false;
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
     * @param type 返回类型1：yyyy-MM-dd，2yyyyMMdd
     * @return
     * @time 2014年9月15日 上午11:51:36
     * @author chendong
     */
    public String addtime(int type) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); //设置当前日期  
        c.add(Calendar.HOUR, 2); //日期小时加2,Calendar.DATE(天),Calendar.HOUR(小时)  
        Date date = c.getTime(); //结果
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (type == 2) {
            df = new SimpleDateFormat("yyyyMMdd HH:mm");
        }
        return df.format(date);
    }

    private String gettraintype(String traincode) {
        String traintype = "普快";
        if (traincode != null && traincode.length() > 0) {
            if (traincode.startsWith("G")) {
                traintype = "高速";
            }
            else if (traincode.startsWith("T")) {
                traintype = "特快";
            }
            else if (traincode.startsWith("K")) {
                traintype = "快速";
            }
        }
        return traintype;

    }

    /**
     * 说明：获取火车站名对应的三字母
     * @param str
     * @return
     * @time 2014年8月30日 下午2:42:44
     * @author yinshubin
     */
    public String get_station_name_code(String str) {
        try {
            return Train12306StationInfoUtil.getThreeByName(str);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //        if (station_names_1.contains(str1)) {
        //            String[] strs1 = station_names_1.split(str);
        //            String[] strs2 = strs1[1].split("\\|");
        //            String str_1 = strs2[0];
        //            return str_1;
        //        }
        //        else if (station_names_2.contains(str1)) {
        //            String[] strs1 = station_names_2.split(str);
        //            String[] strs2 = strs1[1].split("\\|");
        //            String str_1 = strs2[0];
        //            return str_1;
        //        }
        return "";
    }

    /**
     * 说明：获取三字母对应的火车站名
     * @param str
     * @return
     * @time 2014年8月30日 下午2:42:44
     * @author yinshubin
     */
    public String get_station_code_name(String str) {
        try {
            return Train12306StationInfoUtil.getThreeByName(str);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //        String str1 = "|" + str + "|";
        //        str = "\\|" + str + "\\|";
        //        if (station_names_1.contains(str1)) {
        //            String[] strs1 = station_names_1.split(str);
        //            String[] strs2 = strs1[1].split("\\|");
        //            String str_1 = strs2[3];
        //            return str_1;
        //        }
        //        else if (station_names_2.contains(str1)) {
        //            String[] strs1 = station_names_2.split(str);
        //            String[] strs2 = strs1[1].split("\\|");
        //            String str_1 = strs2[3];
        //            return str_1;
        //        }
        return "";
    }

    public String getAlermmobile() {
        return alermmobile;
    }

    public void setAlermmobile(String alermmobile) {
        this.alermmobile = alermmobile;
    }

    public String getAlermcontent() {
        return alermcontent;
    }

    public void setAlermcontent(String alermcontent) {
        this.alermcontent = alermcontent;
    }

    public String getMorningtime() {
        return morningtime;
    }

    public void setMorningtime(String morningtime) {
        this.morningtime = morningtime;
    }

    public String getNighttime() {
        return nighttime;
    }

    public void setNighttime(String nighttime) {
        this.nighttime = nighttime;
    }

    /**
     * 统一线程处理数据
     * @param train_code   车次
     * @param TrainParticulars 12036返回的信息
     * @param from_station 起始站 
     * @param to_station   终点站
     * @param train_date   车次时间
     * @author luoqingxin
     */
    public void creatAddTrainDetailInfo(String train_code, String TrainParticulars, String from_station,
            String to_station, String train_date, long l2) {// 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(1);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t2 = null;
        // 将线程放入池中进行执行
        t2 = new AddTrainDetailInfo(train_code, TrainParticulars, from_station, to_station, train_date, l2);
        pool.execute(t2);
        pool.shutdown();
    }
}
