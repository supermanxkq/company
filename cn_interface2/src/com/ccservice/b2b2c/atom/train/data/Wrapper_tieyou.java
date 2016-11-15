package com.ccservice.b2b2c.atom.train.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpMethodParams;

import net.sf.json.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.train.TrainSupport;
import com.ccservice.b2b2c.atom.train.data.Interface.CcsTrainCrawler;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.util.HttpUtils;
import com.sun.java.swing.plaf.windows.WindowsBorders.DashedBorder;

/**
 * 铁友抓取火车票数据的方法
 * 
 * @time 2014年12月3日 上午11:36:18
 * @author chendong
 */
public class Wrapper_tieyou extends TrainSupport implements CcsTrainCrawler {
    private static HanyuPinyinOutputFormat spellFormat = new HanyuPinyinOutputFormat();

    public Wrapper_tieyou() {
        spellFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        spellFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        spellFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public static void main(String[] args) {
        Wrapper_tieyou Wrapper_tieyou = new Wrapper_tieyou();
        //        String html = Wrapper_tieyou.getHtml("乌鲁木齐南", "北京", "2014-12-15");
        //        Wrapper_tieyou.process(html, "乌鲁木齐南", "北京", "2014-12-15");

        //列车获取
        //        Wrapper_tieyou
        //                .gettraincode("<li class=\"w100 lst_number\">                <strong>Z70</strong><br /><label target-show=\"stationView_0\" sign=\"trainStation\" num=\"0\" checi=\"Z70\" dates=\"20141215\" from=\"乌鲁木齐南\" reach=\"北京西\">经停站<emclass=\"ico_dw2\"></em></label>            </li>");
        //列车获取出发城市
        //        Wrapper_tieyou
        //                .getstartCity(
        //                        "<li class=\"w90 lst_place\">                                    <em class=\"ico_shifa\"></em>乌鲁木齐南<br />                                                    <emclass=\"ico_zhongdian\"></em>北京西                            </li>            <li class=\"w100 lst_duration\">",
        //                        2);
        //        Wrapper_tieyou
        //                .getpasstype(
        //                        "<li class=\"w90 lst_place\">                                    <em class=\"ico_shifa\"></em>乌鲁木齐南<br />                                                    <emclass=\"ico_zhongdian\"></em>北京西                            </li>            <li class=\"w100 lst_duration\">",
        //                        2);
        //列车获取出发时间
        //        Wrapper_tieyou
        //                .getstartTime(
        //                        "<li class=\"w95 lst_time\">                <strong>13:06</strong><br /><span>20:20</span>                                <em>+1</em>                            </li>            <li class=\"w100 lst_number\">                <strong>Z70</strong><br /><label target-show=\"stationView_0\" sign=\"trainStation\" num=\"0\" checi=\"Z70\" dates=\"20141215\" from=\"乌鲁木齐南\" reach=\"北京西\">经停站<em class=\"ico_dw2\"></em></label>            </li>",
        //                        2);
        //运行时间
        //        Wrapper_tieyou
        //                .getcostTime(" <li class=\"w90 lst_place\">                                    <em class=\"ico_shifa\"></em>乌鲁木齐南<br />                                                    <em class=\"ico_zhongdian\"></em>北京西                            </li>           <li class=\"w100 lst_duration\">                1天7时14分                <input type=\"hidden\" sign=\"travel_km\" value=\"3165\" />            </li>            <li class=\"w130 lst_seat\">");
        //        Wrapper_tieyou
        //                .getDistance(" <li class=\"w90 lst_place\">                                    <em class=\"ico_shifa\"></em>乌鲁木齐南<br />                                                    <em class=\"ico_zhongdian\"></em>北京西                            </li>           <li class=\"w100 lst_duration\">                1天7时14分                <input type=\"hidden\" sign=\"travel_km\" value=\"3165\" />            </li>            <li class=\"w130 lst_seat\">");
        //运行时间
        //        Train train = new Train();
        //        Wrapper_tieyou
        //                .setYPdata(
        //                        " <li class=\"w100 lst_duration\">                1天7时14分                <input type=\"hidden\" sign=\"travel_km\" value=\"3165\" />            </li>            <liclass=\"w130 lst_seat\">       <span sign=\"Z70_1_name\" >硬&nbsp;&nbsp;&nbsp;座</span><dfn sign=\"Z70_1_sg\">&yen;</dfn><span sign=\"Z70_1_price\" class=\"base_price\" >317<em class=\"ico_ticket\">余<b>193</b>张</em>                            </span><br/>                                                                                                                <span sign=\"Z70_4_name\" >硬&nbsp;&nbsp;&nbsp;卧</span><dfn sign=\"Z70_4_sg\">&yen;</dfn><span sign=\"Z70_4_price\" class=\"base_price\" >575<em class=\"ico_ticket\">余<b>327</b>张</em>                            </span><br/>                                                                                        <span sign=\"Z70_8_name\" >软&nbsp;&nbsp;&nbsp;卧</span><dfn sign=\"Z70_8_sg\">&yen;</dfn><span sign=\"Z70_8_price\" class=\"base_price\" >887<em class=\"ico_ticket\">余<b>14</b>张</em>                            </span><br/>                                <span sign=\"D2287_13_name\" >手机订</span>                        <dfn sign=\"D2287_13_sg\">&yen;</dfn>                        <span class=\"base_price green_price\" >0.01<em class=\"ico_ctr_mb\"></em></span><br/>                                       </li><li class=\"lst_btn\">",
        //                        train, "Z70");
    }

    @Override
    public String getHtml(String startcity, String endcity, String time, FlightSearch param) {
        String url = gettieyouurl(startcity, endcity, time);
        CCSGetMethod get = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "gbk");
        get = new CCSGetMethod(url.toString());
        get.setFollowRedirects(false);
        get.setRequestHeader("Referer", "http://www.tieyou.com");
        try {
            httpClient.executeMethod(get);
            String responseBody = get.getResponseBodyAsString();
            return responseBody;
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String gettieyouurl(String startcity, String endcity, String time) {
        try {
            startcity = PinyinHelper.toHanyuPinyinString(startcity, spellFormat, "");
            endcity = PinyinHelper.toHanyuPinyinString(endcity, spellFormat, "");
        }
        catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        String url = "http://www.tieyou.com/daigou/" + startcity + "-" + endcity + ".html?date=" + time
                + "&utm_source=tieyou";
        return url;
    }

    @Override
    public List<Train> process(String html, String startcity, String endcity, String time, FlightSearch param) {
        long l1 = System.currentTimeMillis();
        int randominti = new Random().nextInt(10000000);
        WriteLog.write("火车票查询_tieyou", randominti + "");
        List<Train> listtrain = new ArrayList<Train>();
        html = html.split("id=\"searchList\"").length > 1 ? html.split("id=\"searchList\"")[1] : "";
        if (!"".equals(html)) {
            String[] htmls = html.split("trainItem");
            for (int i = 1; i < htmls.length; i++) {
                String dataString = htmls[i];
                Train train = new Train();
                System.out.println("=======================================");

                //                String id = element.elementTextTrim("ID");// 排列序号
                String traincode = gettraincode(dataString);// 车次
                train.setTraincode(traincode);
                String traintype = gettraintype(traincode);// 列车等级
                train.setTraintype(traintype);
                String startCity = getstartCity(dataString, 1);// 出发城市
                train.setStartcity(startCity);
                String passtype_s = getpasstype(dataString, 1, startCity);// 出发经停信息
                train.setSfz(passtype_s);
                String endCity = getstartCity(dataString, 2);// 到达城市
                train.setEndcity(endCity);
                String passtype_e = getpasstype(dataString, 2, endCity);// 出发经停信息
                train.setZdz(passtype_e);
                String startTime = getstartTime(dataString, 1);// 出发时间
                train.setStarttime(startTime);
                String endTime = getstartTime(dataString, 2);// 到达时间
                train.setEndtime(endTime);
                String distance = "0";// 站站距离
                distance = getDistance(dataString);// 运行时间
                try {
                    train.setDistance(Float.valueOf(distance));
                }
                catch (Exception e) {
                    train.setDistance(0);
                }
                String costTime = getcostTime(dataString);// 运行时间
                train.setCosttime(costTime);
                train = setYPdata(dataString, train, traincode);
                listtrain.add(train);
            }
        }
        WriteLog.write("火车票查询_tieyou", randominti + ":" + (System.currentTimeMillis() - l1) + ":listtrain.size"
                + listtrain.size());
        return listtrain;
    }

    /**
     * 获取车站是都类型，始发过路终点
     * 
     * @param dataString
     * @param type 1出发2到达
     * @param City 城市
     * @return S始发P过路E终点
     * @time 2014年12月5日 下午1:45:44
     * @author chendong
     */
    private String getpasstype(String dataString, int type, String city) {
        dataString = dataString.substring(dataString.indexOf("w90 lst_place"));
        if (type == 1) {
            dataString = dataString.substring(0, dataString.indexOf("<br />"));
        }
        else {
            dataString = dataString.substring(dataString.indexOf("<br />"));
        }
        if (dataString.indexOf("ico_shifa") > 0) {
            dataString = "S";
            dataString = city;
        }
        else if (dataString.indexOf("ico_guo") > 0) {
            dataString = "P";
        }
        else if (dataString.indexOf("ico_zhongdian") > 0) {
            dataString = "E";
            dataString = city;
        }
        else {
            dataString = "P";
        }
        return dataString;
    }

    /**
     * 获取运行时间
     * 
     * @param dataString
     * @return
     * @time 2014年12月5日 上午11:42:20
     * @author chendong
     */
    private String getDistance(String dataString) {
        dataString = dataString.substring(dataString.indexOf("travel_km"), dataString.indexOf("w130 lst_seat"));
        dataString = dataString.substring(dataString.indexOf("value=\"") + 7, dataString.indexOf("/>"));
        dataString = dataString.replace('"', ' ');
        dataString = dataString.trim();
        return dataString;
    }

    /**
     * 设置余票信息
     * 和价格信息
     * @param dataString
     * @param train
     * @param traincode 车次
     * @return
     * @time 2014年12月3日 下午2:21:38
     * @author chendong
     */
    private Train setYPdata(String dataString, Train train, String traincode) {
        dataString = dataString.substring(dataString.indexOf("lst_seat") + 8, dataString.indexOf("lst_btn"));
        String[] yps = dataString.split("_name");
        for (int i = 1; i < yps.length; i++) {
            String ypdata = yps[i];
            ypdata = ypdata.replaceAll("&nbsp;", "");
            train = getypdata_info(dataString, train, traincode, ypdata);
        }
        //        yps = dataString.split("base_price gray_txt");
        //        for (int i = 1; i < yps.length; i++) {
        //            String ypdata = yps[i];
        //            ypdata = ypdata.replaceAll("&nbsp;", "");
        //            train = getypdata_info(dataString, train, traincode, ypdata);
        //        }
        return train;
    }

    private Train getypdata_info(String dataString, Train train, String traincode, String ypdata) {
        if (ypdata.indexOf("硬座") >= 0) {
            String yzyp = getyp(ypdata);// 硬座余票
            train.setYzyp(yzyp);
            String yz = getpj(ypdata);// 硬座票价

            train.setYzprice(foamtPrice(yz));
        }
        else if (ypdata.indexOf("无座") >= 0) {
            String wzyp = getyp(ypdata);// 无座
            train.setWzyp(wzyp);
            String wz = getpj(ypdata);// 无座票价
            train.setWzprice(foamtPrice(wz));
        }
        else if (ypdata.indexOf("硬卧") >= 0) {
            String ywyp = getyp(ypdata);// 硬卧余票
            train.setYwyp(ywyp);
            if (!"0".equals(ywyp) && !"-".equals(ywyp)) {
                try {
                    String yws = getpj(ypdata);// 硬卧上铺票价
                    train.setYwsprice(foamtPrice(yws) + 20);
                    train.setYwzprice(foamtPrice(yws) + 20);
                    train.setYwxprice(foamtPrice(yws) + 20);
                }
                catch (Exception e) {
                }
            }
            else {
                train.setYwsprice(0F);
                train.setYwzprice(0F);
                train.setYwxprice(0F);
            }
        }
        else if (ypdata.indexOf("软卧") >= 0) {
            String rwyp = getyp(ypdata);// 软卧余票
            train.setRwyp(rwyp);
            String rws = getpj(ypdata);// 软卧上票价
            train.setRwsprice(foamtPrice(rws));
            train.setRwxprice(foamtPrice(rws));
        }
        else if (ypdata.indexOf("二等座") >= 0) {
            String rz2yp = getyp(ypdata);// 二等软座余票
            train.setRz2yp(rz2yp);
            String rz2 = getpj(ypdata);// 二等软座票价
            train.setRz2price(foamtPrice(rz2));
        }
        else if (ypdata.indexOf("一等座") >= 0) {
            String rz1yp = getyp(ypdata);// 一等软余票
            train.setRz1yp(rz1yp);
            String rz1 = getpj(ypdata);// 一等软座票价
            train.setRz1price(foamtPrice(rz1));
        }
        else if (ypdata.indexOf("商务座") >= 0) {
            String swzyp = getyp(ypdata);// 商务座余票
            train.setSwzyp(swzyp);
            String swxz = getpj(ypdata);// 商务座
            train.setSwzprice(foamtPrice(swxz));
        }
        else if (ypdata.indexOf("高级软卧") >= 0) {
            String gwyp = getyp(ypdata);// 高级软卧余票
            train.setGwyp(gwyp);
            String gws = getpj(ypdata);// 高级软卧上票价
            train.setGwsprice(foamtPrice(gws));
            train.setGwxprice(foamtPrice(gws));
        }
        //        System.out.println(ypdata);
        //        
        //        String rzyp = data_index.getString("rz_num");// 软座余票
        //        train.setRzyp(rzyp);

        //        String tdzyp = data_index.getString("tz_num");// 特等座余票
        //        train.setTdzyp(tdzyp);

        //        String wzyp = data_index.getString("wz_num");// 站票余票
        //        train.setWzyp(wzyp);

        //        if (!"--".equals(rzyp)) {
        //            try {
        //                String rz = pricejsonobject.getString("A2");// 软座票价
        //                train.setRzprice(foamtPrice(rz));
        //            }
        //            catch (Exception e) {
        //                // TODO: handle exception
        //            }
        //        }

        //        if (!"--".equals(tdzyp)) {
        //            try {
        //                String tdz = pricejsonobject.getString("P");// 特等软座票价
        //                train.setTdzprice(foamtPrice(tdz));
        //            }
        //            catch (Exception e) {
        //                // TODO: handle exception
        //            }
        //        }

        //        if (!"--".equals(gwyp)) {
        //            try {
        //                
        //            }
        //            catch (Exception e) {
        //                // TODO: handle exception
        //            }
        //        }

        return train;
    }

    /**
     * 获取票价
     * 
     * @param ypdata
     * @return
     * @time 2014年12月3日 下午2:46:01
     * @author chendong
     */
    private String getpj(String ypdata) {
        try {
            ypdata = ypdata.substring(ypdata.indexOf("price\" >") + 5, ypdata.indexOf("<em class=\"ico_ticket"));
            ypdata = ypdata.substring(ypdata.indexOf(">") + 1);
            ypdata = ypdata.trim();
        }
        catch (Exception e) {
            e.printStackTrace();
            ypdata = "0";
        }
        return ypdata;
    }

    /**
     * 获取余票
     * 
     * @param ypdata
     * @return
     * @time 2014年12月3日 下午2:45:52
     * @author chendong
     */
    private String getyp(String ypdata) {
        try {
            ypdata = ypdata.substring(ypdata.indexOf("余<b>") + 4, ypdata.indexOf("</b>张"));
        }
        catch (Exception e) {
            e.printStackTrace();
            ypdata = "0";
        }
        return ypdata;
    }

    /**
     * 运行时间
     * 
     * @param dataString
     * @return
     * @time 2014年12月3日 下午2:16:55
     * @author chendong
     */
    private String getcostTime(String dataString) {
        try {
            dataString = dataString.substring(dataString.indexOf("lst_duration") + 14, dataString.indexOf("<input"));
            dataString = dataString.trim();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return dataString;
    }

    /**
     * 获取列车的时间
     * 
     * @param dataString
     * @param type 1出发时间2到达时间
     * @return
     * @time 2014年12月3日 下午2:02:57
     * @author chendong
     */
    private String getstartTime(String dataString, int type) {
        try {
            dataString = dataString.substring(dataString.indexOf("lst_time"));
            if (type == 1) {
                dataString = dataString.substring(dataString.indexOf("strong") + 7, dataString.indexOf("</strong"));
                //            dataString = dataString.substring(dataString.indexOf(">"));
            }
            else {
                dataString = dataString.substring(dataString.indexOf("<span>") + 6, dataString.indexOf("</span>"));
            }
            dataString = dataString.trim();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return dataString;
    }

    /**
     * 获取铁友的html的列车的出发城市
     * 
     * @param dataString
     * @param type      1：出发2：到达
     * @return
     * @time 2014年12月3日 下午12:22:07
     * @author chendong
     */
    private String getstartCity(String dataString, int type) {
        try {
            dataString = dataString.substring(dataString.indexOf("w90 lst_place"));
            if (type == 1) {
                dataString = dataString.substring(dataString.indexOf("</em>") + 5, dataString.indexOf("<br />"));
            }
            else {
                String[] dataStrings = dataString.split("</em>");
                if (dataStrings.length >= 3) {
                    dataString = dataStrings[2];
                    dataString = dataString.substring(0, dataString.indexOf("</li>"));
                }
                else {
                    dataString = "";
                }
            }
            if (dataString.indexOf("</em>") >= 0) {
                dataString = dataString.substring(dataString.indexOf("</em>") + 5);
            }
            dataString = dataString.trim();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return dataString;
    }

    /**
     * 获取铁友的html的列车的车次
     * 
     * @param dataString
     * @return
     * @time 2014年12月3日 下午12:20:41
     * @author chendong
     */
    private String gettraincode(String dataString) {
        try {
            dataString = dataString.substring(dataString.indexOf("w100 lst_number"));
            dataString = dataString.substring(dataString.indexOf("<strong>") + 8, dataString.indexOf("</strong>"));
            dataString = dataString.trim();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return dataString;
    }

    public float foamtPrice(String price) {
        float fprice = 0F;
        try {
            price = price.replace("¥", "").replace("?", "").replace("楼", "");
            fprice = Float.valueOf(price);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return fprice;
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

    @Override
    public String getHtml(String startcity, String endcity, String time, FlightSearch param, String purposecodes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Train> process(String html, String startcity, String endcity, String time, FlightSearch param,
            String purposecodes) {
        // TODO Auto-generated method stub
        return null;
    }

}
