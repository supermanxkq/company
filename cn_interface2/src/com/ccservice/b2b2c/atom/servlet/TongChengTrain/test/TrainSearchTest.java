package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.callback.PropertyUtil;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.train.data.Wrapper_12306;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.base.train.Train;
import com.tenpay.util.MD5Util;

/**
 * 火车票查询测试类
 * 
 * @time 2015年4月7日 下午2:59:54
 * @author chendong
 */
public class TrainSearchTest {
    
    public static void main(String[] args) throws Exception {
    	String url ="http://121.41.51.91:39825/trainSearch";//本地查询地址
        String method = "train_query";  //带价格余票查询
        method = "train_query_remain";  //不带价格余票价格
//        url = "http://searchtrain.hangtian123.net/trainSearch";
        String partnerid = "tongcheng_train";
        String key = "x3z5nj8mnvl14nirtwlvhvuialo0akyt";
        String train_date = "2016-10-27";
//        url ="http://121.40.226.72:49525/trainSearch";
        //测试车次查询
//      method ="get_train_info";       //车从查询
//        String train_no ="5l0000G57630";
//        String train_Code ="G105";
//        String fromstation ="VNP";//三字码
//        String toStation ="AOH";//三字码
//        url="http://121.41.51.91:39825/trainSearch";
        		
        		
//        url = "http://test.trainsearch.hangtian123.net/trainSearch";
//        test_CheCIinterface(train_date, fromstation,toStation, partnerid, key, url, method, train_no, train_Code);
      
        
        partnerid = "hthy_test";
        key = "2pUjUHRFSvWLWoUrfiWiZ813Be8f0IQI";
        
        test_interface(train_date, "上海", "南京", partnerid, key, url, method);
        
        
        
        
        
        
    }
    
    
    
    
    
    public static void main111(String[] args) throws Exception {
		ss();
	}
    
    public static void ss() throws Exception {
    	int icount =0;
        int sIndex = 1;
        int count =30;//测试多少个
        int eIndex = sIndex + count;
        String partnerid = "yonyou";
        String key = "g6wexvtm7z51j5jzugs0ep9s3q5gzopv";
        String url = "http://localhost:8080/12306Search/trainSearch";//本地查询地址
        String method = "train_query";
//         method = "train_query_remain";
//       method ="get_train_info";
        partnerid = "hthy_test";
        key = "2pUjUHRFSvWLWoUrfiWiZ813Be8f0IQI";
        for (int i = sIndex; i < eIndex; i++) {
            String riqi = "";
            i=new Random().nextInt(29)+1;
            if (i < 10) {
                riqi += "0" + i;
            }
            else {
                riqi += i;
            }
           String train_date = "2016-10-" + riqi;
//           url = "http://121.40.226.72:29625/trainSearch";
//         train_date = "2016-06-24";
//           url ="http://121.40.226.72:48125/trainSearch";
           test_interface(train_date, "北京西", "广州南",partnerid, key, url, method);
           System.out.println(icount++);
//           test_interface(train_date, "广州", "北京西", partnerid, key, url, method);
//           test_interface(train_date, "天津", "上海", partnerid, key, url, method);
//           test_interface(train_date, "杭州", "天津西", partnerid, key, url, method);
//           test_interface(train_date, "北京南", "威海", partnerid, key, url, method);
        }
    }

    /**
     * 
     * @time 2015年8月19日 下午3:23:32
     * @author chendong
     * @param url 
     * @param key 
     * @param partnerid 
     * @param istest 
     * @throws Exception 
     */
    private static void test_main_search(String partnerid, String key, String url, boolean istest) throws Exception {

    }

    /**
     * 正晚点查询
     * @throws Exception 
     */
//    public static String testzwd_cx(String partnerid, String key) throws Exception {
//        String method = "get_train_zwdcx";
//        String reqtime = getreqtime();
//        String sign = getsign(partnerid, method, reqtime, key);
//        JSONObject json1 = new JSONObject();
//        json1.put("partnerid", partnerid);
//        json1.put("method", method);
//        json1.put("reqtime", reqtime);
//        json1.put("sign", sign);
//        json1.put("cxlx", "1");
//        json1.put("cz", "天津");
//        json1.put("cc", "K27");
////        String result = SendPostandGet.submitGet(TRAINSEARCH_URL + "?jsonStr=" + json1.toString(), "UTF-8");
//        System.out.println(result);
//        return result;
//    }

    /**
     * 测试访问接口
     * 
     * @time 2015年4月30日 下午2:30:51
     * @author chendong
     * @throws Exception 
     */
    private static void test_interface(String train_date, String from_station, String to_station, String partnerid,
            String key, String url, String method) throws Exception {
        long l1 = System.currentTimeMillis();
        //        url = "http://trainorder.test.hangtian123.net/cn_interface/trainSearch";
        //        url = "http://searchtrain.hangtian123.net/trainSearch";
        //        jsonStr={"partnerid":"hqsc_test","method":"train_query","reqtime":"20150430062534","sign":"c87a607f60b897ae2ab43315dbb0d0a4"
        //,"train_date":"2015-05-13","from_station":"BJP","to_station":"GZQ","purpose_codes":"ADULT"}
        System.out.println("partnerid:" + partnerid + "-->" + train_date + "-->" + from_station + "-->" + to_station);
        String reqtime = getreqtime();
        //        reqtime = "20150824053943";
        String sign = getsign(partnerid, method, reqtime, key);
        JSONObject json1 = new JSONObject();
        json1.put("partnerid", partnerid);
        json1.put("method", method);
        json1.put("reqtime", reqtime);
        json1.put("sign", sign);
        json1.put("train_date", train_date);
        json1.put("from_station", Train12306StationInfoUtil.getThreeByName(from_station));
        json1.put("to_station", Train12306StationInfoUtil.getThreeByName(to_station));
        json1.put("purpose_codes", "ADULT");
        //        json1.put("ischeck", "no");
        String paramContent = "jsonStr=" + json1.toJSONString();
        System.out.println("===========================================================url:" + url);
        System.out.println(paramContent);
        l1 = System.currentTimeMillis();
        String resultString = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
        System.out.println("=======>接口耗时:" + (System.currentTimeMillis() - l1) + "===" + resultString);
    }
    
    
    
   private static void test_interface2(String train_date, String from_station, String to_station, String partnerid,
           String key, String url, String method) throws Exception {
       long l1 = System.currentTimeMillis();
       //        url = "http://trainorder.test.hangtian123.net/cn_interface/trainSearch";
       //        url = "http://searchtrain.hangtian123.net/trainSearch";
       //        jsonStr={"partnerid":"hqsc_test","method":"train_query","reqtime":"20150430062534","sign":"c87a607f60b897ae2ab43315dbb0d0a4"
       //,"train_date":"2015-05-13","from_station":"BJP","to_station":"GZQ","purpose_codes":"ADULT"}
       System.out.println("partnerid:" + partnerid + "-->" + train_date + "-->" + from_station + "-->" + to_station);
       String reqtime = getreqtime();
       //        reqtime = "20150824053943";
       String sign = getsign(partnerid, method, reqtime, key);
       JSONObject json1 = new JSONObject();
       json1.put("partnerid", partnerid);
       json1.put("method", method);
       json1.put("reqtime", reqtime);
       json1.put("sign", sign);
       json1.put("train_date", train_date);
       json1.put("from_station", from_station);
       json1.put("to_station",to_station);
       json1.put("purpose_codes", "ADULT");
       //        json1.put("ischeck", "no");
       String paramContent = "jsonStr=" + json1.toJSONString();
       System.out.println("===========================================================url:" + url);
       System.out.println(paramContent);
       l1 = System.currentTimeMillis();
       String resultString = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
       System.out.println("=======>接口耗时:" + (System.currentTimeMillis() - l1) + "===" + resultString);
   }
   
   
  private static void test_CheCIinterface(String train_date, String from_station, String to_station, String partnerid,
          String key, String url, String method,String train_no,String train_Code) throws Exception {
      long l1 = System.currentTimeMillis();
      //        url = "http://trainorder.test.hangtian123.net/cn_interface/trainSearch";
      //        url = "http://searchtrain.hangtian123.net/trainSearch";
      //        jsonStr={"partnerid":"hqsc_test","method":"train_query","reqtime":"20150430062534","sign":"c87a607f60b897ae2ab43315dbb0d0a4"
      //,"train_date":"2015-05-13","from_station":"BJP","to_station":"GZQ","purpose_codes":"ADULT"}
      System.out.println("partnerid:" + partnerid + "-->" + train_date + "-->" + from_station + "-->" + to_station);
      String reqtime = getreqtime();
      //        reqtime = "20150824053943";
      String sign = getsign(partnerid, method, reqtime, key);
      JSONObject json1 = new JSONObject();
      json1.put("partnerid", partnerid);
      json1.put("method", method);
      json1.put("reqtime", reqtime);
      json1.put("sign", sign);
      json1.put("train_date", train_date);
      json1.put("from_station", from_station);
      json1.put("to_station",to_station);
      json1.put("train_no", train_no);
      json1.put("train_code", train_Code);
      json1.put("purpose_codes", "ADULT");
      //        json1.put("ischeck", "no");
      String paramContent = "jsonStr=" + json1.toJSONString();
      System.out.println("===========================================================url:" + url);
      System.out.println(paramContent);
      l1 = System.currentTimeMillis();
      String resultString = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
      System.out.println("=======>接口耗时:" + (System.currentTimeMillis() - l1) + "===" + resultString);
  }
  
   
    
    
    public static String test_price_interface(String train_date, String from_station, String to_station, String partnerid,
            String key, String url, String method) throws Exception {
        
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        JSONObject json1 = new JSONObject();
        json1.put("partnerid", partnerid);
        json1.put("method", method);
        json1.put("reqtime", reqtime);
        json1.put("sign", sign);
        json1.put("train_date", train_date);
        json1.put("from_station", Train12306StationInfoUtil.getThreeByName(from_station));
        json1.put("to_station", Train12306StationInfoUtil.getThreeByName(to_station));
        json1.put("purpose_codes", "ADULT");
        String paramContent = "jsonStr=" + json1.toJSONString();
        
        return  SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
    }

    /**
     * 直接测试类
     * 
     * @throws Exception
     * @time 2015年4月30日 下午2:30:33
     * @author chendong
     */
    private static void testwrapper_12306() throws Exception {
        Wrapper_12306 wrapper_12306 = new Wrapper_12306();
        FlightSearch param = new FlightSearch();
        List<Train> list;
        list = wrapper_12306.process("", Train12306StationInfoUtil.getThreeByName("上海"),
                Train12306StationInfoUtil.getThreeByName("深圳"), "2015-05-22", param);
        for (int yi = 0; yi < list.size(); yi++) {
            Train train = list.get(yi);
        }
    }

    /**
     * md5(partnerid+method+reqtime+md5(key))，
     * 
     * @time 2014年12月12日 下午2:44:31
     * @author chendong
     */
    public static String getsign(String partnerid, String method, String reqtime, String key) {
        //        String keyString = partnerid + method + reqtime + MD5Util.MD5Encode(key, "UTF-8");
        //        keyString = MD5Util.MD5Encode(keyString, "UTF-8");
        key = MD5Util.MD5Encode(key, "UTF-8");
        String jiamiqian = partnerid + method + reqtime + key;
        String sign = MD5Util.MD5Encode(jiamiqian, "UTF-8");
        return sign;
    }

    public static String getreqtime() {
        SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getcurrentTimeMillis() {
        return System.currentTimeMillis() + "";
    }

    /**
     * 
     * 
     * @param inittype //1.初始化账号信息!!!!!! 2.缓存同步到数据库 3.查看缓存的数据
     * @param partnerid 账号
     * @time 2015年8月21日 下午9:22:11 
     * @author chendong
     */
    private static void initdata(int inittype, String partnerid, String key, String url) {
        StringBuffer sbParamContent = new StringBuffer("initdata=" + inittype + "&partnerid=" + partnerid);
        if (inittype == 4) {
            sbParamContent.append("&key=" + key);
        }
        String paramContent = sbParamContent.toString();
        String resultString = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
        System.out.println("resultString:" + resultString + ":paramContent:" + paramContent);
    }
    
    
    public static void main1(String[] args) {
        long a =1460432165848l;
        Date date = new Date(a);
        System.out.println(date);
        System.out.println(new Date());
        long b = System.currentTimeMillis()-a;
        System.out.println(b/1000);
    }
    

   public static void main2(String[] args) throws Exception {
          SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
          String sql = "SELECT top 10 * FROM TongChengPrice " ;
          List list = getSystemServiceOldDB().findMapResultBySql(sql, null);
          if(list.size()>0){
              for(int i=0;i<list.size();i++){
                  Map map=(Map)list.get(i);
                  String time=map.get("time").toString();
                  Date date = sdf.parse(time);
                  String ss=sdf.format(date);
                  String from=map.get("fromstation").toString();
                  String to=map.get("totation").toString();
                  String traincode=map.get("traincode").toString();
                  System.out.println(ss+":"+from+"-->"+to+":"+traincode);
                 String s= test_price_interface(ss, from, to, "yonyou", "g6wexvtm7z51j5jzugs0ep9s3q5gzopv", "http://searchtrain.hangtian123.net/trainSearch", "train_query");
                 System.out.println(s);
              }
          }
      }
      private static ISystemService getSystemServiceOldDB() {
          String systemdburlString = PropertyUtil.getValue("offlineservice",
                  "Train.properties");
          // String
          // systemdburlString="http://121.40.241.126:9001/cn_service/service/";
          HessianProxyFactory factory = new HessianProxyFactory();
          try {
              return (ISystemService) factory.create(ISystemService.class,
                      systemdburlString + ISystemService.class.getSimpleName());
          } catch (MalformedURLException e) {
              e.printStackTrace();
              return null;
          }
      }
    
    
    
    
    
}
