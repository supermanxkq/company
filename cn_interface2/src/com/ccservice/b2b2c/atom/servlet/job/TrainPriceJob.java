package com.ccservice.b2b2c.atom.servlet.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.tenpay.util.MD5Util;

public class TrainPriceJob extends Thread {

    String train_date;

    String from_station;

    String to_station;

    SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public TrainPriceJob(String train_date, String from_station, String to_station) {
        this.train_date = train_date;
        this.from_station = from_station;
        this.to_station = to_station;
    }

    @Override
    public void run() {
        String result = train_query(this.train_date, this.from_station, this.to_station);
        System.out.println("结果:" + result);
    }

    public String train_query(String train_date, String from_station, String to_station) {
        String method = "train_query";
        String partnerid = "meituan";
        String key = "92l2w9s745is8djyh0hbpyfg8v812pvw";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"train_date\":\"" + train_date + "\",\"from_station\":\""
                + from_station + "\",\"to_station\":\"" + to_station
                + "\",\"purpose_codes\":\"ADULT\",\"needdistance\":\"0\"}";
        String resultString = SendPostandGet.submitPost("http://searchtrain.hangtian123.net/trainSearch",
                "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    public String getreqtime() {
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public String getsign(String partnerid, String method, String reqtime, String key) {
        return MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "UTF-8"), "UTF-8");
    }

}
