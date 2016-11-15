package com.ccservice.b2b2c.atom.servlet.job.thread;

import java.sql.Timestamp;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.service12306.TrainUtil;
import com.ccservice.b2b2c.atom.train.data.ZhuanfaUtil;

public class Jobupdate12306priceThread_update12306pricefor extends Thread {

    String train_no;

    String station_train_code;

    String s_no;

    String e_no;

    public Jobupdate12306priceThread_update12306pricefor(String train_no, String station_train_code, String s_no,
            String e_no) {
        super();
        this.train_no = train_no;
        this.station_train_code = station_train_code;
        this.s_no = s_no;
        this.e_no = e_no;
    }

    public void run() {
        update12306price(train_no, station_train_code, s_no, e_no);
    }

    /**
     * @param train_no 列车号
     * @param station_train_code 车次
     * @param from_station_no 出发站号
     * @param to_station_no 到达站号
     * @time 2015年3月2日 下午6:50:55
     * @author chendong
     */
    private void update12306price(String train_no, String station_train_code, String from_station_no,
            String to_station_no) {
        String mcckey = station_train_code + "_" + from_station_no + "_" + to_station_no;
        int trainprice_count = 0;
        try {
            trainprice_count = Server
                    .getInstance()
                    .getTrainService()
                    .countTrainNoBySql(
                            "SELECT COUNT(*) FROM T_TRAINPRICE with(nolock) WHERE C_MCCKEY = '" + mcckey + "' ");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String json = getpriceJsonbyurl(train_no, station_train_code, from_station_no, to_station_no);
        if (!"-1".equals(json) && json.indexOf("3\":\"-2\"") < 0 && json.indexOf("\"1\":\"-2\"") < 0
                && json.indexOf("\"6\":\"-2\"") < 0 && json.indexOf("\"4\":\"-2\"") < 0) {
            if (trainprice_count == 0) {//数据库里没有数据就插入
                addpricefromdb(mcckey, json);
            }
            else {//数据库里有数据就update
                updatepricefromdb(mcckey, json);
            }
        }
        else {
            try {
                String sqlinsert = "update T_trainprice set c_mtime='" + new Timestamp(System.currentTimeMillis())
                        + "' where C_MCCKEY like '" + station_train_code + "_%'";
                //+ key + "','" + value                   + "','" + new Timestamp(System.currentTimeMillis()) + "')";
                Server.getInstance().getSystemService().excuteEaccountBySql(sqlinsert);
            }
            catch (Exception e) {
            }

        }
    }

    private String getpriceJsonbyurl(String train_no, String station_train_code, String from_station_no,
            String to_station_no) {
        String train_date = TimeUtil.gettodaydatebyfrontandback(1, 10);
        String json = "-1";
        int temp_i = 0;
        for (int i = 0; i < 35; i++) {
            temp_i = i;
            int temp_type = i % 4;
            String url = "https://kyfw.12306.cn/otn/leftTicket/queryTicketPrice?train_no=" + train_no
                    + "&from_station_no=" + from_station_no + "&to_station_no=" + to_station_no + "&seat_types="
                    + TrainUtil.getseat_typesby_trainclasstype(station_train_code, temp_type) + "&train_date="
                    + train_date;
            int temp_i_2 = 0;
            do {
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                json = ZhuanfaUtil.zhuanfa(url, "", "GBK", "", "get", 2000);
                temp_i_2++;
            }
            while (json.equals("-1") && temp_i_2 < 20);
            //            json = ZhuanfaUtil.gethttpclientdata(url, 2000L);
            //            System.out.println(json + ":" + url);
            if ("-1".equals(json)) {
                continue;
            }
            else {
                break;
            }
        }
        //        System.out.println(temp_i);
        return json;
    }

    public void addpricefromdb(String key, String value) {
        try {
            String sqlinsert = "insert into T_TRAINPRICE(C_MCCKEY,C_PRICE,C_CTIME) values ('" + key + "','" + value
                    + "','" + new Timestamp(System.currentTimeMillis()) + "')";
            Server.getInstance().getSystemService().excuteEaccountBySql(sqlinsert);
        }
        catch (Exception e) {
        }
    }

    public void updatepricefromdb(String key, String value) {
        try {
            String sqlinsert = "update T_trainprice set c_price='" + value + "',c_mtime='"
                    + new Timestamp(System.currentTimeMillis()) + "' where c_mcckey='" + key + "'";
            //+ key + "','" + value                   + "','" + new Timestamp(System.currentTimeMillis()) + "')";
            Server.getInstance().getSystemService().excuteEaccountBySql(sqlinsert);
        }
        catch (Exception e) {
        }
    }

}
