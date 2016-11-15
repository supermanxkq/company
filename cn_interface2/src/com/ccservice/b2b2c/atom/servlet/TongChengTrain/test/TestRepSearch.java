package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.net.URLEncoder;

import com.callback.SendPostandGet;
import com.callback.WriteLog;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;


/**
 * 测试rep是否可以请求12306
 * @author zhyxu
 *
 */
public class TestRepSearch {
    
    private static String time = "2016-08-20";
    
    private static String startcity = "北京";
    
    private static String endcity = "上海";
    
    private static String callDataBackUrl = "124.254.60.66:5389";
    
    public static void main(String[] args) {


        try {
            while (true) {
                TestThread thread = new TestThread();
                thread.start();
                thread.sleep(1000);
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //        TestRepSearch.testTBRepSearch("114.215.240.84:8080");
        //                TestRepSearch.testRepSearch("114.215.240.84:8080");
        //                TestRepSearch.testRepSearch("114.215.240.84:8080");
        //                TestRepSearch.testRepSearch("114.215.240.84:8080");
        //                TestRepSearch.testRepSearch("114.215.240.84:8080");
    }
    
    
    
    
    
    /**
     * 同步测试
     * @param rep 127.0.0.1:9016|127.0.0.1:9016
     */
    public static void testTBRepSearch(String rep){
        String []reps = rep.split("\\|");
        for (int i = 0; i < reps.length; i++) {
            testTBRepSearch(reps[i], time, startcity, endcity);
        }
    }
    
    
    /**
     * 
     * @param rep 127.0.0.1:9016|127.0.0.1:9016
     */
    public static void testRepSearch(String rep){
        String []reps = rep.split("\\|");
        for (int i = 0; i < reps.length; i++) {
            testRepSearch(reps[i],time, startcity, endcity);
        }
    }
    
    
    public static void testRepSearch(String repurl,String time,String startcity,String endcity){
        try {
            repurl ="http://"+repurl+"/Reptile/repTrainSearch";
            String callBackUrl ="http://"+callDataBackUrl+"/cn_interface/TestRepSearchCallBackServelt";
            String purposecodes="ADULT";
            startcity=  Train12306StationInfoUtil.getThreeByName(startcity);
            endcity = Train12306StationInfoUtil.getThreeByName(endcity);
            String ctxkey =startcity+endcity+time;
            String search_url = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=" + purposecodes + "&queryDate="
                    + time + "&from_station=" + startcity + "&to_station=" + endcity;
            search_url = URLEncoder.encode(search_url, "utf-8");
            String paramContent = "datatypeflag=query&search_url=" + search_url + "&callDataBackUrl=" + callBackUrl
                    + "&ctxkey=" + ctxkey;
            String resultString = SendPostandGet.submitPost(repurl, paramContent, "utf-8").toString();

            System.out.println(resultString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public static void testTBRepSearch(String repurl,String time,String startcity,String endcity){
        try {
            repurl ="http://"+repurl+"/Reptile/repTrainSearchTest";
            String purposecodes="ADULT";
            startcity=  Train12306StationInfoUtil.getThreeByName(startcity);
            endcity = Train12306StationInfoUtil.getThreeByName(endcity);
            String ctxkey =startcity+endcity+time;
            String search_url = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=" + purposecodes + "&queryDate="
                    + time + "&from_station=" + startcity + "&to_station=" + endcity;
            search_url = URLEncoder.encode(search_url, "utf-8");
            String paramContent = "datatypeflag=query&search_url=" + search_url + "&ctxkey=" + ctxkey;;
            String resultString = SendPostandGet.submitPost(repurl, paramContent, "utf-8").toString();
            System.out.println(resultString);
            WriteLog.write("查询测试结果3333", resultString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
