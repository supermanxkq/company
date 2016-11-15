package com.ccservice.b2b2c.atom.service12306;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;
import com.ccservice.b2b2c.util.FileUtils;
import com.ccservice.elong.inter.PropertyUtil;

public class MyThreadSimpleCode {
    public static void main(String[] args) {
        execute();
    }

    public static void execute() {
        String data = get12306station_namedata();
        //            String TrainStationNames_path = Jobupdate12306station_name.class.getClassLoader().getResource("")
        //                    .toString().substring(6)
        //                    + "TrainStationNames.txt";
        //        String station_name_path = getSystemConfig("station_name_path");//X:\HTHY\workspace\cn_interface\src\TrainStationNames.txt
        String station_name_path = PropertyUtil.getValue("station_name_path", "Train.properties");
        String[] station_name_paths = station_name_path.split("[|]");
        for (int i = 0; i < station_name_paths.length; i++) {
            try {
                updatestation_name(data, station_name_paths[i]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * 
     * @param data 12306最新车站数据
     * @param TrainStationNames_path 准备更新的文件地址
     * @throws Exception
     * @time 2015年3月10日 上午11:31:05
     * @author chendong
     */
    private static void updatestation_name(String data, String TrainStationNames_path) throws Exception {
        String filedata = FileUtils.readFile(TrainStationNames_path, "UTF-8");
        System.out.println(data);
        System.out.println(TrainStationNames_path);
        System.out.println(data.length() + ":" + filedata.length() + ":" + (data.length() > filedata.length()));
        if (data.length() > filedata.length()) {
            FileUtils.writeFileUTF8(data, TrainStationNames_path);
        }
    }

    /**
    * 获取到12306城市的js数据
    * 
    * @time 2015年3月1日 下午3:30:40
    * @author chendong
    */
    private static String get12306station_namedata() {
        String htmlString = "-1";
        CCSGetMethod get = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        get = new CCSGetMethod("https://kyfw.12306.cn/otn/leftTicket/init");
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        get.setFollowRedirects(false);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            httpClient.executeMethod(get);
            htmlString = get.getResponseBodyAsString();
        }
        catch (HttpException e) {
            //            e.printStackTrace();
        }
        catch (IOException e) {
            //            e.printStackTrace();
        }
        String tempString = "station_name.js?station_version=";
        int s = htmlString.indexOf(tempString) + tempString.length();
        int e = s + 10;
        htmlString = htmlString.substring(s, e);
        htmlString = htmlString.split(" ")[0].replace("\"", "");
        String jsurl = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=" + htmlString;
        get = new CCSGetMethod(jsurl);
        get.setFollowRedirects(false);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            httpClient.executeMethod(get);
            htmlString = get.getResponseBodyAsString();
        }
        catch (HttpException e1) {
        }
        catch (IOException e1) {
        }
        htmlString = htmlString.substring(htmlString.indexOf("='@") + 3).replace("';", "");
        return htmlString;
    }

    /**
     * 读取文件并分析
     * 
     * @time 2015年3月10日 上午10:57:01
     * @author chendong
     */
    private static void readtongbulog() {
        //        execute();
        for (int i = 1; i < 19; i++) {
            try {
                File file = new File("X:/TT/t同程火车票接口_4.5申请分配座位席别 (" + i + ").log");
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "GBK"));
                String fileContent = "";
                String temp = "";
                while ((temp = br.readLine()) != null) {
                    if (temp.indexOf(":时间:") > 0 && temp.indexOf("TC_") > 0) {
                        System.out.println(temp);
                    }
                }
                br.close();
                fis.close();
            }
            catch (Exception e) {
            }
        }

    }

}
