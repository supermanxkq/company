package com.ccservice.b2b2c.atom.servlet.job.thread;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.server.Server;

public class Jobupdate12306priceThreadSKD {
    private String startStationName;

    private String endStationName;

    private String station_train_code;

    private String startStationNameCode;

    private String endStationNameCode;

    private String prices_yw;

    private String prices_rw;

    private String date = "2015-03-29";

    public Jobupdate12306priceThreadSKD(String startStationName, String endStationName, String station_train_code,
            String startStationNameCode, String endStationNameCode) {
        this.startStationName = startStationName;
        this.endStationName = endStationName;
        this.station_train_code = station_train_code;
        this.startStationNameCode = startStationNameCode;
        this.endStationNameCode = endStationNameCode;
    }

    //    @Override
    //    public void run() {
    //        getprice(startStationName, endStationName, "2015-03-29", station_train_code);
    //    }
    /**
     * SKD查询价格   
     * @param fromstation
     * @param endstation
     * @time 2015年3月14日 上午10:31:21
     * @author fiend
     */
    public void getpriceSKD() {
        String url = "http://www.soukd.com/DGTrain.Asp";
        String b = "代购查询";
        try {
            b = URLEncoder.encode(b, "GB2312");
            String fromstation1 = URLEncoder.encode(this.startStationName, "GB2312");
            String endstation1 = URLEncoder.encode(this.endStationName, "GB2312");
            String par = "FromCity=" + fromstation1 + "&ToCity=" + endstation1 + "&sDate=" + this.date + "&B1=" + b;
            String str = new String(SendPostandGet.submitPost(url, par, "GB2312"));
            //        System.out.println(str);
            jixipriceSKD(this.startStationName, this.endStationName, str, this.station_train_code);
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getpricetest();
    }

    /**
     * 火车票查询价格   
     * @param fromstation
     * @param endstation
     * @time 2015年3月14日 上午10:31:21
     * @author fiend
     */
    public void getprice() {
        try {
            String fromstation1 = URLEncoder.encode(this.startStationName, "UTF-8");
            String endstation1 = URLEncoder.encode(this.endStationName, "UTF-8");
            String url = "http://www.huoche.com.cn/yd-" + fromstation1 + "-" + endstation1 + "/?date=" + this.date;
            String str = new String(SendPostandGet.submitPost(url, "", "UTF-8"));
            jixiprice(this.startStationName, this.endStationName, str, this.station_train_code);
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void getpricetest() {
        try {
            String fromstation1 = URLEncoder.encode("镇安", "UTF-8");
            String endstation1 = URLEncoder.encode("万源", "UTF-8");
            String url = "http://www.huoche.com.cn/yd-" + fromstation1 + "-" + endstation1 + "/?date=" + "2015-03-19";
            String str = new String(SendPostandGet.submitPost(url, "", "UTF-8"));
            jixipricetest("镇安", "万源", str, "K1002");
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void jixipricetest(String fromstation, String endstation, String str, String trainno) {
        try {
            System.out.println(str);
            String[] sp_trainno = str.split("<tr train=\"" + trainno);
            String yw_price_str = sp_trainno[1].split("硬卧")[1].split("元")[0];
            String rw_price_str = sp_trainno[1].split("软卧")[1].split("元")[0];
            System.out.println(yw_price_str);
            System.out.println(rw_price_str);
        }
        catch (Exception e) {
            //            WriteLog.write("火车票价格解析异常", fromstation + "----->" + endstation + "----->" + trainno);
            System.out.println("解析车次异常--->" + fromstation + "----->" + endstation + "----->" + trainno);
        }
    }

    /**
     * 火车票解析票价 
     * @param fromstation
     * @param endstation
     * @param str
     * @param trainno
     * @param seatno
     * @time 2015年3月14日 下午1:04:42
     * @author fiend
     */
    public void jixiprice(String fromstation, String endstation, String str, String trainno) {
        try {
            String[] sp_trainno = str.split("<tr train=\"" + trainno);
            String yw_price_str = sp_trainno[1].split("硬卧")[1].split("元")[0];
            String rw_price_str = sp_trainno[1].split("软卧")[1].split("元")[0];
            if (yw_price_str.contains("/") && yw_price_str.contains(".") && rw_price_str.contains("/")
                    && rw_price_str.contains(".")) {
                this.prices_yw = yw_price_str;
                this.prices_rw = rw_price_str;
                saveprice();
            }
            else if (yw_price_str.contains("/") && yw_price_str.contains(".")) {
                this.prices_yw = yw_price_str;
                saveywprice();
            }
            else if (rw_price_str.contains("/") && rw_price_str.contains(".")) {
                this.prices_rw = rw_price_str;
                saverwprice();
            }
        }
        catch (Exception e) {
            //            WriteLog.write("火车票价格解析异常", fromstation + "----->" + endstation + "----->" + trainno);
            System.out.println("解析车次异常--->" + fromstation + "----->" + endstation + "----->" + trainno);
        }
    }

    /**
     * SKD解析票价 
     * @param fromstation
     * @param endstation
     * @param str
     * @param trainno
     * @param seatno
     * @time 2015年3月14日 下午1:04:42
     * @author fiend
     */
    public void jixipriceSKD(String fromstation, String endstation, String str, String trainno) {
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
                if (skd_price_after > 0 && skd_price > 0 && skd_price_before > 0) {
                    if (skd_price_before > skd_price_after) {
                        this.prices_yw = skd_price_after + "/" + skd_price + "/" + skd_price_before;
                    }
                    else {
                        this.prices_yw = skd_price_before + "/" + skd_price + "/" + skd_price_after;
                    }
                }
            }
            catch (Exception e) {
                //                WriteLog.write("解析硬卧票价异常", fromstation + "----->" + endstation + "----->" + trainno);
                System.out.println("解析硬卧票价异常--->" + fromstation + "----->" + endstation + "----->" + trainno);
            }
            try {
                //软卧前面的价格
                String str_rw_price = sp_price[2].split("color=\"blue\">")[1];
                if (!"-".equals(str_rw_price)) {
                    skd_price_rw_before = Float.valueOf(str_rw_price);
                }
                //软卧后面价格
                str_rw_price = sp_price[3].split("</font>")[0];
                if (!"-".equals(str_rw_price)) {
                    skd_price_rw_after = Float.valueOf(str_rw_price);
                }
                if (skd_price_rw_before > 0 && skd_price_rw_after > 0) {
                    if (skd_price_rw_before > skd_price_rw_after) {
                        this.prices_rw = skd_price_rw_after + "/" + skd_price_rw_before;
                    }
                    else {
                        this.prices_rw = skd_price_rw_before + "/" + skd_price_rw_after;
                    }
                }
            }
            catch (Exception e) {
                //                WriteLog.write("解析软卧票价异常", fromstation + "----->" + endstation + "----->" + trainno);
                System.out.println("解析软卧票价异常--->" + fromstation + "----->" + endstation + "----->" + trainno);
            }
            //            System.out.println(this.prices_yw + "--->" + this.prices_rw);
            saveprice();
        }
        catch (Exception e) {
            //            WriteLog.write("解析车次异常", fromstation + "----->" + endstation + "----->" + trainno);
            System.out.println("解析车次异常--->" + fromstation + "----->" + endstation + "----->" + trainno);
        }
    }

    private void saveprice() {
        String mcckey = this.station_train_code + "_" + this.startStationNameCode + "_" + this.endStationNameCode;
        String sql = "UPDATE T_TRAINPRICE SET C_YWPRICE='" + this.prices_yw + "',C_RWPRICE='" + this.prices_rw
                + "' WHERE C_MCCKEY='" + mcckey + "'";
        int i = Server.getInstance().getSystemService().excuteGiftBySql(sql);
        if (0 >= i) {
            //            WriteLog.write("SQLc存储失败", sql);
            System.out.println("SQLc存储失败--->" + sql);
        }
        else {
            //            WriteLog.write("SQLc存储成功", sql);
            System.out.println("SQLc存储成功--->" + sql);
        }
    }

    private void saveywprice() {
        String mcckey = this.station_train_code + "_" + this.startStationNameCode + "_" + this.endStationNameCode;
        String sql = "UPDATE T_TRAINPRICE SET C_YWPRICE='" + this.prices_yw + "' WHERE C_MCCKEY='" + mcckey + "'";
        int i = Server.getInstance().getSystemService().excuteGiftBySql(sql);
        if (0 >= i) {
            //          WriteLog.write("SQLc存储失败", sql);
            System.out.println("SQLc存储失败--->" + sql);
        }
        else {
            //            WriteLog.write("SQLc存储成功", sql);
            System.out.println("SQLc存储成功--->" + sql);
        }
    }

    private void saverwprice() {
        String mcckey = this.station_train_code + "_" + this.startStationNameCode + "_" + this.endStationNameCode;
        String sql = "UPDATE T_TRAINPRICE SET C_RWPRICE='" + this.prices_rw + "' WHERE C_MCCKEY='" + mcckey + "'";
        int i = Server.getInstance().getSystemService().excuteGiftBySql(sql);
        if (0 >= i) {
            //          WriteLog.write("SQLc存储失败", sql);
            System.out.println("SQLc存储失败--->" + sql);
        }
        else {
            //            WriteLog.write("SQLc存储成功", sql);
            System.out.println("SQLc存储成功--->" + sql);
        }
    }
}
