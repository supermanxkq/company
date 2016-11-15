package com.ccservice.b2b2c.atom.servlet.job.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.servlet.job.Jobupdate12306price;
import com.ccservice.b2b2c.base.trainno.TrainNo;

public class Jobupdate12306priceby58Thread extends Thread {
    String train_no;

    String station_train_code;

    public Jobupdate12306priceby58Thread(String train_no, String station_train_code) {
        super();
        this.train_no = train_no;
        this.station_train_code = station_train_code;
    }

    @Override
    public void run() {
        System.out.println("------------------------------------------------------------------------------");
        update12306pricefor(train_no, station_train_code);
    }

    /**
     * 根据车次和车号更新这个车次所有的出发到达站的价格
     * 
     * @param list1
     * @param train_no 列车号 ：380000T19706
     * @param station_train_code 车次
     * @time 2015年3月3日 上午9:52:52
     * @author chendong
     */
    private void update12306pricefor(String train_no, String station_train_code) {
        List list1 = Server
                .getInstance()
                .getTrainService()
                .findAllTrainNo("where C_STATION_TRAIN_CODE='" + station_train_code + "' ", " ORDER BY C_STATION_NO ",
                        -1, 0);
        for (int z1 = 0; z1 < list1.size(); z1++) {
            TrainNo trainno_z1 = (TrainNo) list1.get(z1);
            for (int z2 = 1; z2 < list1.size(); z2++) {
                TrainNo trainno_z2 = (TrainNo) list1.get(z2);
                String s_no = trainno_z1.getStation_no().trim();
                String e_no = trainno_z2.getStation_no().trim();
                if (Integer.parseInt(s_no) < Integer.parseInt(e_no)) {
                    boolean exist = isExist(trainno_z1, trainno_z2);
                    if (exist) {
                    }
                    else {
                        update12306price(trainno_z1, trainno_z2);
                    }
                }
                else {
                    continue;
                }
            }
        }
    }

    /**
     * 判断这个发到站是否存在于数据库
     * 
     * @param trainno_z1
     * @param trainno_z2
     * @return
     * @time 2015年5月5日 下午12:58:51
     * @author chendong
     */
    private boolean isExist(TrainNo trainno_z1, TrainNo trainno_z2) {
        String s_no = trainno_z1.getStation_no().trim();
        String e_no = trainno_z2.getStation_no().trim();
        String mcckey = station_train_code + "_" + s_no + "_" + e_no;
        int trainprice_count = 0;
        try {
            trainprice_count = Server
                    .getInstance()
                    .getTrainService()
                    .countTrainNoBySql(
                            "SELECT COUNT(*) FROM T_TRAINPRICE WHERE C_MCCKEY = '" + mcckey
                                    + "' and (C_YWPRICE is null )");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return trainprice_count > 0;
    }

    public static void main(String[] args) {
        String s = "&nbsp;&nbsp;硬座&nbsp;54.5&nbsp;&nbsp;硬卧&nbsp;100.5/105.5/108.5<br/>&nbsp;&nbsp;软卧&nbsp;155.5/161.5";

    }

    private void update12306price(TrainNo trainno_z1, TrainNo trainno_z2) {
        String s_name = trainno_z1.getStation_name().trim();
        s_name = getpinyin(s_name);
        s_name = qudiao_dxnb(s_name);

        String e_name = trainno_z2.getStation_name().trim();
        e_name = getpinyin(e_name);
        e_name = qudiao_dxnb(e_name);
        System.out.println("-------------------------------------" + s_name + ":" + e_name);

        //        System.out.println(getrandomdate() + "-------------------");
        String url = "http://lieche.58.com/" + s_name + "-" + e_name + "/?" + getrandomdate();
        String html = gethttpclientdata(url, 3000L);
        String[] htmls = html.split("id=\"tr");
        for (int i = 2; i < htmls.length; i++) {
            updatepricebyhtml(htmls[i]);
        }
    }

    private void updatepricebyhtml(String html) {
        String s_name = "-1";
        String e_name = "-1";
        String price_html = "-1";
        try {
            if (html.indexOf("下一页") >= 0) {
                String html2 = html.substring(html.indexOf("class='next'") + 12, html.indexOf("下一页"));
                html2 = html2.substring(html2.indexOf("href='") + 6, html2.indexOf("'><span>"));
                String url = "http://lieche.58.com/" + html2;
                html2 = gethttpclientdata(url, 3000L);
                String[] htmls2 = html2.split("id=\"tr");
                for (int i = 2; i < htmls2.length; i++) {
                    updatepricebyhtml(htmls2[i]);
                }
            }
            else {
                int stc_s = html.indexOf("code=\"") + 6;
                int stc_e = html.indexOf("\" id=\"openmoresorts");
                String station_train_code = "-1";
                if (stc_s == 5) {
                    stc_s = html.indexOf("href=") + 13;
                    stc_e = html.indexOf("btnShowDetail") - 10;
                    station_train_code = html.substring(stc_s, stc_e);
                    System.out.println("========================" + station_train_code);
                }
                else {
                    station_train_code = html.substring(stc_s, stc_e);
                }
                int s_s = html.indexOf("chezhan");
                int s_e = html.lastIndexOf("glc");
                if (s_s == -1) {
                    s_s = html.indexOf("glc") + 2;
                }
                if (s_e + 2 == s_s) {
                    s_s = html.indexOf("sfz") + 2;
                }
                if (s_s > s_e) {
                    s_e = html.lastIndexOf("zdz");
                }
                s_name = html.substring(s_s, s_e);
                s_name = s_name.substring(s_name.indexOf("\">") + 2, s_name.lastIndexOf("</a>")).trim();
                int e_s = html.lastIndexOf("chezhan");
                int e_e = html.lastIndexOf("spantime");
                if (e_s == -1) {
                    e_s = html.indexOf("glc") + 2;
                }
                if (e_s > e_e) {
                    if (html.lastIndexOf("zdz") > 0) {
                        e_s = html.lastIndexOf("zdz");
                    }
                    else {
                        e_s = html.lastIndexOf("glc") + 2;
                    }
                    //                    if (e_e == -1) {
                    //                        e_e = html.lastIndexOf("glc") + 2;
                    //                    }
                    //                    else {
                    //                        e_e = html.lastIndexOf("zdz");
                    //                    }
                }
                e_name = html.substring(e_s, e_e);
                e_name = e_name.substring(e_name.lastIndexOf("/\">") + 3, e_name.indexOf("</a>")).trim();
                int price_s = html.indexOf("tatxt2") + 8;
                int price_e = html.indexOf("SpreaderShowForm");
                if (price_s == -1) {
                    price_s = html.indexOf("txtr") + 6;
                }
                if (price_e == -1) {
                    price_e = html.indexOf("panelDetail");
                }
                price_html = html.substring(price_s, price_e).trim();
                price_html = price_html.substring(0, price_html.indexOf("</td>")).trim();
                updatepricebydata(station_train_code, s_name, e_name, price_html);
            }
        }
        catch (Exception e) {
            System.out.println(html);
            e.printStackTrace();
        }
    }

    /**
     * 
     * 
     * @param station_train_code 车次
     * @param s_name 出发站
     * @param e_name 到达站
     * @param price_html 价格信息
     * @time 2015年3月14日 下午4:25:02
     * @author chendong
     */
    private void updatepricebydata(String station_train_code, String s_name, String e_name, String price_html) {
        String s_station_no = "";
        String e_station_no = "";
        if (station_train_code.startsWith("G") || station_train_code.startsWith("C")) {
            return;
        }
        String[] station_train_codes = station_train_code.split("/");
        List<TrainNo> list = new ArrayList<TrainNo>();
        for (int j = 0; j < station_train_codes.length; j++) {
            String temp_station_train_code = station_train_codes[j];
            list = Server
                    .getInstance()
                    .getTrainService()
                    .findAllTrainNo("where C_STATION_TRAIN_CODE='" + temp_station_train_code + "'",
                            "order by C_STATION_NO", -1, 0);
            if (list.size() > 0) {
                break;
            }
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStation_name().trim().equals(s_name)) {
                s_station_no = list.get(i).getStation_no().trim();
            }
            if (list.get(i).getStation_name().trim().equals(e_name)) {
                e_station_no = list.get(i).getStation_no().trim();
            }
        }
        String[] ss = price_html.split("卧");
        String ywprice = "-1";
        String rwprice = "-1";
        String gjrwprice = "-1";
        for (int i = 0; i < ss.length; i++) {
            String t_string = ss[i];
            //            System.out.println("==============================================" + t_string + ":" + t_string.length());
            if (t_string.lastIndexOf("硬") + 1 == t_string.length()) {
                String[] ywprices = ss[i + 1].split("&nbsp;");

                ywprice = (ywprices.length > 1 ? ywprices[1] : ywprices[0]).replace("<br/>", "");
            }
            if (t_string.lastIndexOf("软") + 1 == t_string.length()) {
                String[] rwprices = ss[i + 1].split("&nbsp;");
                rwprice = (rwprices.length > 1 ? rwprices[1] : rwprices[0]).replace("<br/>", "");
            }
        }
        int temp_i = 0;
        //update T_TRAINPRICE set c_ywprice='',c_rwprice='',c_gjrwprice='' where C_MCCKEY=''
        String sql = "update T_TRAINPRICE set ";
        if (!"-1".equals(ywprice)) {
            sql += "c_ywprice='" + ywprice + "'";
            temp_i += 1;
        }
        if (!"-1".equals(rwprice)) {
            if (temp_i > 0) {
                sql += ",c_rwprice='" + rwprice + "'";
            }
            else {
                sql += " c_rwprice='" + rwprice + "'";
            }
            temp_i += 1;
        }

        String tempsql = sql;
        sql = tempsql + " where C_MCCKEY='" + station_train_code + "_" + s_station_no + "_" + e_station_no + "' ;";
        int count = 0;
        if (!"".equals(s_station_no) && !"".equals(e_station_no) && temp_i > 0) {
            try {
                count = Server.getInstance().getSystemService().excuteEaccountBySql(sql);
            }
            catch (Exception e) {
            }
        }
        else {
            //                System.out.println("=========================================" + station_train_code + ":" + s_name
            //                        + ":" + e_name + ":" + price_html);
        }
        //            System.out.println(count + ":" + sql);
        if (count == 0 && !"".equals(s_station_no) && !"".equals(e_station_no)) {
            updatenodata(station_train_code, s_station_no, e_station_no, s_name, e_name, price_html);
        }

    }

    /**
     * 更新数据库价格库没有价格的
     * 
     * @time 2015年3月14日 下午6:07:32
     * @author chendong
     * @param price_html 
     * @param e_name 
     * @param s_name 
     * @param station_train_code2 
     * @param price_html2 
     * @param e_name2 
     */
    private void updatenodata(String station_train_code2, String s_station_no, String e_station_no, String s_name,
            String e_name, String price_html) {
        Jobupdate12306price.update12306pricebytrain_class(station_train_code2);
        System.out.println("=========================================" + station_train_code2 + ":" + s_name + ":"
                + e_name + ":" + price_html);
    }

    /**
     * 
     * 去掉后面的东西南北的拼音
     * @time 2015年3月14日 下午2:23:50
     * @author chendong
     */
    private static String qudiao_dxnb(String pinyin_name) {
        String temp_pinyin_name = "";
        boolean bd = pinyin_name.indexOf("dong") >= 0;
        boolean bx = pinyin_name.indexOf("xi") >= 0;
        boolean bn = pinyin_name.indexOf("nan") >= 0;
        boolean bb = pinyin_name.indexOf("bei") >= 0;
        if (bd) {
            temp_pinyin_name = pinyin_name.substring(0, pinyin_name.lastIndexOf("dong"));
            if (temp_pinyin_name.length() == pinyin_name.length() - 4) {
                pinyin_name = temp_pinyin_name;
            }
        }
        if (bx) {
            temp_pinyin_name = pinyin_name.substring(0, pinyin_name.lastIndexOf("xi"));
            if (temp_pinyin_name.length() == pinyin_name.length() - 2) {
                pinyin_name = temp_pinyin_name;
            }
        }
        if (bn) {
            temp_pinyin_name = pinyin_name.substring(0, pinyin_name.lastIndexOf("nan"));
            if (temp_pinyin_name.length() == pinyin_name.length() - 3) {
                pinyin_name = temp_pinyin_name;
            }
        }
        if (bb) {
            temp_pinyin_name = pinyin_name.substring(0, pinyin_name.lastIndexOf("bei"));
            if (temp_pinyin_name.length() == pinyin_name.length() - 3) {
                pinyin_name = temp_pinyin_name;
            }
        }

        return pinyin_name;
    }

    /**
     *  get访问url
     * 
     * @param search_url 访问的url
     * @param outtime 超时时间
     * @return
     * @time 2015年3月2日 上午11:22:34
     * @author chendong
     */
    public static String gethttpclientdata(String search_url, Long outtime) {
        Long l1 = System.currentTimeMillis();
        String json = "-1";
        CCSGetMethod get = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, outtime);
        get = new CCSGetMethod(search_url);
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        get.setFollowRedirects(false);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            httpClient.executeMethod(get);
            json = get.getResponseBodyAsString();
        }
        catch (HttpException e) {
            //            e.printStackTrace();
        }
        catch (IOException e) {
            //            e.printStackTrace();
        }
        //        System.out.println("耗时1:" + (System.currentTimeMillis() - l1));
        return json;
    }

    private String getrandomdate() {
        int r1 = new Random().nextInt(50);
        String date = TimeUtil.gettodaydatebyfrontandback(1, r1 + 5).replace("-", "/");
        return date;
    }

    private String getpinyin(String hanzi) {
        String result = "";
        for (int i = 0; i < hanzi.length(); i++) {
            String pinyin = PinyinHelper.toHanyuPinyinStringArray(hanzi.charAt(i))[0];
            pinyin = pinyin.substring(0, pinyin.length() - 1);
            result += pinyin;
        }
        return result;
    }

}
