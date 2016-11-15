package com.ccservice.b2b2c.atom.servlet.account.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.HttpsUtil;
import com.tenpay.util.MD5Util;

public class Test12306AndSearch {

    public static void maina(String[] args) {
        //  1475054964  1474941607     824 
//                System.out.println(System.currentTimeMillis());
        //身份核验
//                yanzheng("许婧妍", "二代身份证", "230103198108193961", "1");
        //************************************************************************************************************
        //帐号核验
        //        System.out.println(zhanghaoyanzheng12306("869570838@qq.com", "ren130301"));
        //************************************************************************************************************
        //查询常旅     一     "869570838@qq.com","ren130303"   二     "434432458@qq.com"    "QAZplm121030"
//                              System.out.println(cl12306("434432458@qq.com","QAZplm121030"));
        //返回的常旅信息需要解密
//                      String data="Q8EXcm4mPXLJnvOUDuERqXfe0QyjrJYgDlCqRi7NOBomsmcMvlyz_dnNjy9RZX-Yg-84gtH63PnVdCJkFeslpq6xOcedFv87NVuRt9bqY7KYCPihkFllt3QZ8ToXJSTJkYBicq_bgwsi8GwQkciBDnlwicQT1m9p1SiQM9VviP50lIG_BluCK14j4u-YxFjOoJWpnLnGPqH9KDkkwwkAT8l2xykE_U9UX73zuL_x0Og3PhxOw5okfIdvl_5fHq2-e7MmK-i7WLgbyh9skA-HOqzpbgep6G_qt5cpOhAp3kRZMhIqPDU7X9vrGUtAdbj7F8bZ-3av4H2BqaHnwme1JgYfXCeBxkAIZZppR_rIxpkxKyB5MQFuMJQ0VQLfr4GWnOEsg3sc6zAT9DXBqTuWvVJWiicWNZ97xXCgAbvsPNCn-I6mciv8fU4ulbfAq9gYfBTux1ZkE9CPnGidR5UYWKfMO73tDaHGd08H_zkYRJQTqLJjXwbjC-Be3dP3t_1V95ClypuJjW_m8hOWxPMxW0Xf3dXrUPE4HE9yu6kajcJZG7tRySEnePEKQVzCh5YA5PfwDSlAB5l_fV2bFxI3oB0oPZpmx8kKrHHFuGCoX7LDfKn7W1kB37iVJEXnNZYqCIxHCj217f2-9HbsxNAUJBK3NcEeL6jCmAW4iyEEHu-pafqp24lUoMYO1wNHzsJbSf8tJPUC_PeAMxn3EU9irpxjPcWgKjbmgBmmVX0ampB1EMwg0V1dg32aK8AmeMc17LZ0Gsrkvt9n2wyOUDGkgXfe0QyjrJYgDlCqRi7NOBozUUOJ21smSjl4yMO1BBrGR-J6bF4lxKuf5R0Wc6D6rc8c3hEuY15p4F7d0_e3_VX3kKXKm4mNb-byE5bE8zFbRd_d1etQ8TgSBgHCWuoO1qbvWvZTyC8r8QpBXMKHlgDk9_ANKUAHmX99XZsXEjeg0-vHtf_Y80-4lSRF5zWWKsYO1wNHzsJbwr58qlHZYzyqVbna_BOrge10IsrPH4Qd2Xn7CJvEOd-pvwBuse-pkpC3dE1IzG-D8cpv_6vClBXLCtX6YH9wthOKRxnoouGofC_BPfWNojsPe6bnqWUTKjJ9jmlzoOVGWRM-OCVMt9agpRWoY9kxvti58knJqqns6UbeafDQj14fdVlrxFAIdse6gXHzsuDTBKv6UYO_U4NBTpcMUxcn3LUO1fE-rQq3Pe8J8R4i9ygl70uk6NHaCezlnzNtWHEzV3MUX-YtAAQ7KZvX9jg6AP1mwL_0YB2RR-R4z8KnC_OionSGLzTwShfG2ft2r-B9gamh58JntSYGH1wngcZACDf1Bk456Sn7MSsgeTEBbjCUNFUC36-BlpwhWebb4voJE_Q1wak7lr1SVoonFjWfe3iXO28yJbhb6xzh1qrBZxhOLpW3wKvYGFCVnl4hQ5cAS3mmQPSurBMaZR62D3Aebupa7bTouBPOipbPX2cNGyIJvShKqmn5QGt814ddbaS7ukH01G_IApg9O9td0rzeleSCgCK7GTDCOQru84bjwOlWBxmFYRyuUuEL0JN9r_7OBXzyrKeBjAOD26bv5Hv6wBcsNGtEaCJOUyYCZFJkpSRPvorn8O9YXzwLuDTC9CV3TJpRgMAjG5aT83H6_BXG9R8TR7Oss9sQbIcBelbeNoIRZhxcL8Q08i_QkVN55rv87CCfO4TmzMYqJYub7VZT3qYfcV6Y1NHEj1wjaKuyhBheWsIFqcCN_INwmfpaqwvsl-tS4gXXZoRTMkfpNb7i1Npui7fEFOUtsaNPY2Pjxb54SVqxZNaX3Bhod3AIV-WAhF8zzjjLxUPCSOr_zCdH6jfbXY_bNQHzsrLqQtNvpBg0U2eG5CzwkdCeOzMJtFFL0GHL8FFAijTioo9wIOz51ME4cJpCHa8FE7M5qmRD8WyVdlaabRdcA9Y5If4AG9vi3Wmil816Jxly1wLK-iHZ4kAy0YNimGTrhEg2mkBoUt78lUuUE0uoQBWlFExCPp8BIsBY21doSufgXt3T97f9VXFC-8xrSquEBfcSZNAXDczoaRo9f_PEqu8NB3DQLLVYMn2OaXOg5UYogOoId-KBTcf1X1NFJkXaCLvm-tuWXb4NrprMLSP1DTTFsCsT6v2QRPGONszL2JB_pWbOW_q4VaFNkLtkJLfMYSEnq8WufH1UV3hEm2_AkosA1TMZ2xnbZ1oMO7MDDl1N1gJEmwc3GTYG0QUywajsChUSr8n8NU6bqmcGtc12E3GJycRKbMy4IfrFkcB4AeNO9CZkW6wPbu3nTtGAxKv-BLJz6VCliVrcnfGX7Eq_6U4ulbfAq9gYFHS38epUCjQLBwFqUcUOh_AU7xOM70_l6lrttOi4E86Kls9fZw0bIgm9KEqqaflAa3zXh11tpLu6QfTUb8gCmDTF9YG7atuRVbgBrLkyS50l70uk6NHaCezlnzNtWHEzV3MUX-YtAAQ7KZvX9jg6AP1mwL_0YB2R0fh72U3LbVE4OQQpTFfqyhfG2ft2r-B9gamh58JntSYGH1wngcZACDf1Bk456Sn7MSsgeTEBbjCUNFUC36-BlmEZusUe2OsgE_Q1wak7lr1bkb6GS4EfKxYeM09LP1RCg-84gtH63PkZEjOIAAhOc7Q0MRj4K373IL2R2AXJxJ6PXCNoq7KEGBfyUZBRGTJ7ZgSE-bt5QlNieW4hC89Cv86bPCAx3F7wzgnvqwvKTfA4_FW57J1QOs7Ew-hz-7yDD0AanUDh9IUUcNIYNj8IxXjZM0GkfgOlBX9ivkmSHALlFfGpwdsAe4Suk1P3rAIfgDMZ9xFPYq6cYz3FoCo25oAZplV9GpqQeX1C4U5pNDg";
//                      System.out.println(TuNiuDesUtil.decrypt(data));
        //************************************************************************************************************
        //增加修改  trainAccount":"434432458@qq.com"    pass":"QAZplm121030"
//        System.out.println(saveOupdate12306("434432458@qq.com", "QAZplm121030", "bbbb", "0", "B", "JDJDJJCF",
//                "2006-06-16", 182431056));//此处id，当修改时，id填入查询常旅返回时的乘客id
        //************************************************************************************************************
        //删除
        //        System.out.println(delete12306("baiwa2012", "ren130303", "1434345881", "1", "13043519930913005X"));
        //************************************************************************************************************ 
        //车次查询String partnerid,String key,String trainno,String traindate,String fstation,String tstation,String traincode
        //        getcheci("uddtest" ,"mM1dg8SJP9x982ub6qc4ZvN7zgHRP7qq" ,"240000G1050G","2016-10-30","VNP","AOH","G105");
        //余票查询（有价格）      cs12308    PQUKwS9vrkrX1YqX14R1IJG1qIYTlIC2
//                getypprice("cs12308","PQUKwS9vrkrX1YqX14R1IJG1qIYTlIC2","http://searchtrain.hangtian123.net/trainSearch","2016-10-16", "CWQ", "BXP");
        //************************************************************************************************************
        //余票查询（无价格）         
        //        getypnoprice("uddtest","mM1dg8SJP9x982ub6qc4ZvN7zgHRP7qq","http://searchtrain.hangtian123.net/trainSearch","2016-09-30","VNP","AOH");
    }

    //余票查询（无价格）
    public static String getypnoprice(String partnerid, String key, String url, String traindate, String fstation,
            String tstation) {
        String result = "";
        JSONObject jb = new JSONObject();
        String reqtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); //请求时间  格式：yyyyMMddHHmmss（非空）
        String sign = ""; //数字签名
        String method = "train_query_remain"; // 操作功能名  投保insure;退保cancel_insurance 
        sign = partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8");
        sign = MD5Util.MD5Encode(sign, "utf-8");
        jb.put("reqtime", reqtime);
        jb.put("partnerid", partnerid);
        jb.put("sign", sign);
        jb.put("method", method);

        jb.put("train_date", traindate);
        jb.put("from_station", fstation);
        jb.put("to_station", tstation);
        jb.put("purpose_codes", "ADULT");
        result = SendPostandGet.submitPost(url, "jsonStr=" + jb.toJSONString(), "utf-8").toString();
        System.out.println("=======>接口:" + (System.currentTimeMillis()) + "===" + result);
        return result;
    }

    //余票查询（有价格）
    public static String getypprice(String partnerid, String key, String url, String traindate, String fstation,
            String tstation) {
        String result = "";
        JSONObject jb = new JSONObject();
        String reqtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); //请求时间  格式：yyyyMMddHHmmss（非空）
        String sign = ""; //数字签名、下单与退票
        String method = "train_query"; // 操作功能名  投保insure;退保cancel_insurance 
        sign = partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8");
        sign = MD5Util.MD5Encode(sign, "utf-8");
        jb.put("reqtime", reqtime);
        jb.put("partnerid", partnerid);
        jb.put("sign", sign);
        jb.put("method", method);

        jb.put("train_date", traindate);
        jb.put("from_station", fstation);
        jb.put("to_station", tstation);
        jb.put("purpose_codes", "ADULT");
        System.out.println(jb.toJSONString());
        result = SendPostandGet.submitPost(url, "jsonStr=" + jb.toJSONString(), "utf-8").toString();
        System.out.println("=======>接口:" + (System.currentTimeMillis()) + "===" + result);
        return result;
    }

    //车次查询     get_train_info
    public static String getcheci(String partneridq, String keyq, String trainno, String traindate, String fstation,
            String tstation, String traincode) {
        String result = "";
        //xmfk_test 8VeyivCI0F838jFQLGmi12Jlq6bsz1Uj  
        JSONObject jb = new JSONObject();
        String reqtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); //请求时间  格式：yyyyMMddHHmmss（非空）
        String partnerid = partneridq;//uddtest  mM1dg8SJP9x982ub6qc4ZvN7zgHRP7qq
        String key = keyq;
        String sign = ""; //数字签名
        String method = "get_train_info"; // 操作功能名  投保insure;退保cancel_insurance 
        sign = partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8");
        sign = MD5Util.MD5Encode(sign, "utf-8");

        jb.put("reqtime", reqtime);
        jb.put("partnerid", partnerid);
        jb.put("sign", sign);
        jb.put("method", "get_train_info");

        jb.put("train_no", trainno);
        jb.put("train_date", traindate);
        jb.put("from_station", fstation);
        jb.put("to_station", tstation);
        jb.put("train_code", traincode);

        String url = "http://searchtrain.hangtian123.net/trainSearch";
        String ss = "";//"{\"sign\":\"1c5f91214f603c531945ae3809b6a312\",\"to_station":\"AOH\",\"partnerid\":\"wanda_train\",\"train_date\":\"2016-09-30\",\"reqtime\":\"2016-09-19 17:32:57\",\"from_station\":\"VNP\",\"method\":\"get_train_info\",\"train_code\":\"G105\",\"train_no\":\"5l0000G57630\"}";
        String s = "";
        try {
            s = URLEncoder.encode(jb.toString(), "utf-8");
            ss = URLEncoder.encode(ss, "utf-8");
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        result = SendPostandGet.submitPost(url, "jsonStr=" + s, "utf-8").toString();
        System.out.println("=======>接口耗时:" + (System.currentTimeMillis()) + "===" + result);
        return result;
    }

    //  12306账号验证接口
    public static String zhanghaoyanzheng12306(String loginname, String pass) {
        String result = "";
        //        String url = "http://trainorder.ws.hangtian123.com/cn_interface/trainAccount/validate";
        String url = "http://120.26.100.206:19362/cn_interface/trainAccount/validate";
        JSONObject jb = new JSONObject();
        jb.put("trainAccount", loginname);
        jb.put("pass", pass);
        //        TuNiuDesUtil
        String q = null;
        try {
            q = TuNiuDesUtil.encrypt(jb.toJSONString());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject jj = new JSONObject();
        jj.put("data", q);
        jj.put("accountversion", "2");
        System.out.println(jj.toJSONString());
        long qq = System.currentTimeMillis();
        result = SendPostandGet.submitPost(url, jj.toJSONString(), "utf-8").toString();
        System.out.println("=======>接口耗时:" + (System.currentTimeMillis() - qq));
        return result;
    }

    // 查常旅
    public static String cl12306(String loginname, String pass) {
        String result = "";
        //        String url = "http://trainorder.ws.hangtian123.com/cn_interface/trainAccount/contact/query";
        String url = "http://120.26.100.206:19362/cn_interface/trainAccount/contact/query";
        JSONObject jb = new JSONObject();
        jb.put("trainAccount", loginname.trim());
        jb.put("pass", pass.trim());
        JSONArray ja = new JSONArray();
        String q = null;
        try {
            q = TuNiuDesUtil.encrypt(jb.toJSONString());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject jj = new JSONObject();
        jj.put("data", q);
        jj.put("accountversion", "2");
        long qq = System.currentTimeMillis();
        result = SendPostandGet.submitPost(url, jj.toJSONString(), "utf-8").toString();
        System.out.println("=======>接口耗时:" + (System.currentTimeMillis() - qq));
        return result;
    }

    //增加   和   修改    
    public static String saveOupdate12306(String loginname, String pass, String name, String sex, String idtype,
            String idno, String birthday, Integer id) {
        String result = "";
        //        String url="http://trainorder.test.hangtian123.net/cn_interface/trainAccount/contact/saveOrUpdate";
        //        String url = "http://trainorder.ws.hangtian123.com/trainAccount/contact/saveOrUpdate";
        String url = "http://120.26.100.206:19362/cn_interface/trainAccount/contact/saveOrUpdate";
        JSONObject jb = new JSONObject();
        jb.put("trainAccount", loginname.trim());
        jb.put("pass", pass.trim());
        String address = "江苏省南京市栖霞区神农路1号";
        try {
            name = URLEncoder.encode(name, "utf-8");
            address = URLEncoder.encode(address, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray ja = new JSONArray();
        JSONObject jjb = new JSONObject();
        jjb.put("id", id);
        jjb.put("name", name);
        jjb.put("sex", sex);
        jjb.put("birthday", birthday);
        jjb.put("country", "CN");
        jjb.put("identyType", idtype);
        jjb.put("identy", idno);
        jjb.put("personType", "1");
        jjb.put("phone", "13839871817");
        jjb.put("tel", "025-8888888");
        jjb.put("email", "869570838@qq.com");
        jjb.put("address", address);
        ja.add(jjb);
        jb.put("contacts", ja);
        String q = null;
        try {
            q = TuNiuDesUtil.encrypt(jb.toJSONString());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject jj = new JSONObject();
        jj.put("data", q);
        jj.put("accountversion", "2");
        result = SendPostandGet.submitPost(url, jj.toJSONString(), "utf-8").toString();
        return result;
    }

    //删除 12306   
    public static String delete12306(String loginname, String pass, String ids, String idtype, String idno) {
        String result = "";
        String url = "http://trainorder.ws.hangtian123.com/cn_interface/trainAccount/contact/delete";
        //        String url = "http://120.26.100.206:19362/cn_interface/trainAccount/contact/delete";
        JSONObject jb = new JSONObject();
        jb.put("trainAccount", loginname);
        jb.put("pass", pass);
        jb.put("ids", ids);
        jb.put("identyType", idtype);
        jb.put("identy", idno);
        String q = null;
        try {
            q = TuNiuDesUtil.encrypt(jb.toJSONString());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject jj = new JSONObject();
        jj.put("data", q);
        jj.put("accountversion", "2");
        result = SendPostandGet.submitPost(url, jj.toJSONString(), "utf-8").toString();
        return result;
    }

    //身份验证  demo
    public static String yanzheng(String username, String useridentitytype, String useridentity, String num) {
        String result = "";
        try {  //miyou_test   C1Qgrrmy7KrcfqSqMxfVslCkQfZDmXDW   miyou_test C1Qgrrmy7KrcfqSqMxfVslCkQfZDmXDW
            String merchantCode = "miyou_test";//uddtest
            String key = "C1Qgrrmy7KrcfqSqMxfVslCkQfZDmXDW";//73kgpL0TA2ql1fp98e91HSDoVEaxNtvQ
            long timestamp = System.currentTimeMillis();
            String serviceId = "V0101";
            String version = "1.0.0";
            //加密的数据
            
            JSONObject jso1 = new JSONObject();
            JSONArray jsa1 = new JSONArray();
            JSONObject jsob1 = new JSONObject();
//            jsob1.put("passenger_id_no", "36220319911137311");
//            jsob1.put("passenger_name", "周垒");
//            jsob1.put("passenger_id_type_name", "二代身份证");
//            jsob1.put("passenger_id_type_code", 1);
            
            jsob1.put("passenger_id_no", useridentity);
            jsob1.put("passenger_name", username);
            jsob1.put("passenger_id_type_name", useridentitytype);
            jsob1.put("passenger_id_type_code", num);
            jsa1.add(jsob1);
            jso1.put("passengers", jsa1);
            String data1 = jso1.toString();
            String str_sign = MD5Util.MD5Encode(
                    merchantCode + serviceId + Long.toString(timestamp) + data1
                            + MD5Util.MD5Encode(key, "utf-8").toUpperCase(), "utf-8").toUpperCase();
            System.out.println("在请求中str_sign" + str_sign);
            String url = "http://trainorder.hangtian123.com/cn_home/trainidverification";
            String paramContent = "merchantCode=" + merchantCode + "&serviceId=" + serviceId + "&version=" + version
                    + "&timestamp=" + timestamp + "&sign=" + str_sign + "&data=" + data1;
//            String paramContent = "merchantCode=" + merchantCode + "&serviceId=" + serviceId + "&version=" + version
//                    + "&timestamp=" + "1476359557663" + "&sign=" + "51BC67ADCFA65E6D1B0E45690E32087C" + "&data=" + data1;
            System.out.println(paramContent);
            result = SendPostandGet.submitPost(url, paramContent, "UTF-8").toString();
            System.out.println(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //*********************************************************************************************************************************************************************************************
    //*********************************************************************************我是************************************************************************************************
    //**********************************************************************************分割线*********************************************************************************************************
    //***********************************************************************************啊*******************************************************************************************************
    //************************************************************************************哈********************************************************************************************************
    //************************************************************************************哈********************************************************************************************************
    //************************************************************************************哈********************************************************************************************************
    //************************************************************************************来********************************************************************************************************
    //************************************************************************************打********************************************************************************************************
    //************************************************************************************我********************************************************************************************************
    //************************************************************************************啊********************************************************************************************************
    //*********************************************************************************************************************************************************************************************

    private static String key;

    private static String Url;

    public static void main(String[] args) throws UnsupportedEncodingException {

//        Url = "http://trainorder.test.hangtian123.net/cn_interface/tcTrain";
        Url = "http://120.26.100.206:19362/cn_interface/tcTrain";
        //        Url = "http://searchtrain.hangtian123.net/trainSearch";
//        public String partnerid = "TestshoudanX";
//        public String key = "ydsszfvupijrdeojsgrawumvlqdekdss";
        key = "ydsszfvupijrdeojsgrawumvlqdekdss";
        
        //创建一个乘客      xmfk_test 8VeyivCI0F838jFQLGmi12Jlq6bsz1Uj
        //        public String partnerid = "hthyzz_test";
        //        https://test.miutrip.com:7802/Account/showResult/api   http://140.207.47.210:8129/udd-ticketcheck/supplier/getCallBackResult.do
        //        String result = SendPostandGet.submitPost("https://test.miutrip.com:7802/Account/showResult/api", "data=a", "UTF-8").toString();
//        String result = SendPostandGet
//                .submitPost(
//                        "http://test.miutrip.com:7802/Account/showResult/api",
//                        "data={\"reqtoken\":\"\",\"runtime\":\"05:39\",\"from_station_name\":\"%E4%B8%8A%E6%B5%B7%E8%99%B9%E6%A1%A5\",\"ordernumber\":\"E623848489\",\"checi\":\"G102\",\"code\":100,\"msg\":\"%E5%A4%84%E7%90%86%E6%88%96%E6%93%8D%E4%BD%9C%E6%88%90%E5%8A%9F\",\"from_station_code\":\"AOH\",\"orderamount\":\"553.00\",\"to_station_name\":\"%E5%8C%97%E4%BA%AC%E5%8D%97\",\"arrive_time\":\"2016-10-27 12:18:00\",\"passengers\":[{\"reason\":0,\"piaotype\":\"1\",\"passporttypeseidname\":\"%E4%BA%8C%E4%BB%A3%E8%BA%AB%E4%BB%BD%E8%AF%81\",\"passporttypeseid\":\"1\",\"zwname\":\"%E4%BA%8C%E7%AD%89%E5%BA%A7\",\"price\":\"553.0\",\"piaotypename\":\"%E6%88%90%E4%BA%BA%E7%A5%A8\",\"ticket_no\":\"E623848489104001D\",\"passengersename\":\"%E5%91%A8%E5%9E%92\",\"zwcode\":\"O\",\"passportseno\":\"362203199111037311\",\"passengerid\":\"1001\",\"cxin\":\"04%E8%BD%A6%E5%8E%A2%2C01D%E5%BA%A7\"}],\"to_station_code\":\"VNP\",\"accountlist\":[{\"accountstatusid\":5,\"accountstatusname\":\"%E5%85%B6%E4%BB%96\",\"accountname\":\"0\"}],\"train_date\":\"2016-10-27\",\"ordersuccess\":true,\"transactionid\":\"T1610120956539104126\",\"start_time\":\"2016-10-27 06:39:00\",\"orderid\":\"T1610120957010\",\"success\":true}",
//                        "UTF-8").toString();
        //        byte[] aa = HttpsUtil.post
        //        String result=new String(aa,"utf-8");
//        System.out.print(result);
        //        public String key = "o2s26g1zdpj7zgf32mj2ehqnmpeeg4y9"; 
        //wandatest 84db7fgmitfd229gvqsuehpowmtabt01
        //        JSONArray json = CreatePassengers(1, "E5280940421120001", "陈莹璐", "310110198812094426", "1", "二代身份证", "1", "成人票", "1", "硬座", "8");
        //生成火车票信息
        //        JSONArray json = CreateTicketinfo("王大龙", "1", "152201198505080519", "成人票", "E3496671731013090");

        //创建一个订单
        //        JSONObject jsonobject = CreateOrder("hbsl_test", "train_order", "201608011704", "k243", "WXN", "武穴", "JJG",
        //                "九江", "2016-09-01", false, json, "", "", "");
        //        System.out.println(jsonobject.toJSONString());
        //查询订单详情
        //        JSONObject jsonobject = QueryOrderDetails("12308_test", "train_query_info", "2016071217458987",
        //                "T1607131355514375324");
        //        System.out.println(jsonobject.toJSONString());
        //取消订单
                JSONObject jsonobject = QueryOrderDetails("TestshoudanX", "train_cancel", "TC_20150127_fiendyibutest_1476432430103_20_84_97_16",
                        "T1610146D94C3D30ABBF042070A31F0EE5B327FD5A7");
                System.out.println(jsonobject.toJSONString());
        //确认出票
        //        JSONObject jsonobject = ConfirmTrainTicket("12308_test", "train_confirm", "2016071217458987",
        //                "T1607131355514375324");
        //        System.out.println(jsonobject.toJSONString());
        //请求改签
        //                JSONObject jsonobject = RequestChange("wandatest", "train_request_change", "WXN", "武穴", "JJG", "九江",
        //                        "10110811258871", "T1609291048235179888", "E528094042", "G1275", "2016-10-18 00:00:00", "O", "1",
        //                        json, "Y", "http://m12308.f3322.net:8100/hthyTrain/api/requestChange.html",
        //                        "aaaabbbb", false, "agent2323009@126.com", "ren130300");
        //                System.out.println(jsonobject.toJSONString());
        //取消改签
        //        JSONObject jsonobject = CannalRequestChange("fbt_test", "train_cancel_change", "10052", "T1608021352240976843",
        //                "changeTO0000229120160725115728");
        //        System.out.println(jsonobject.toJSONString());
        //确认改签
        //        JSONObject jsonobject = CoformRequestChange("12308_test", "train_confirm_change", "2016071217458987",
        //                "T1607131355514375324",
        //                "1467530720143|181607030036_1", "Y", "http://m12308.f3322.net:8100/hthyTrain/api/confirmChange.html");
        //        System.out.println(jsonobject.toJSONString());
        //退票
        //                JSONObject jsonobject = returnTicket("wandatest", "return_ticket", "10110811258871", "T1609291048235179888",
        //                        "E528094042", "1qqaaaaaq",
        //                        "http://m12308.f3322.net:8100/hthyTrain/api/refundTicket.html", "agent2323009@126.com", "ren130300", json);
        //                System.out.println(jsonobject.toJSONString());
        //查询余票
        //        JSONObject jsonobject = QueryTicket("12308_test", "train_query", "2016-08-21", "WXN", "JJG", "ADULT", "");
        //        System.out.println(jsonobject.toJSONString());

    }

    /**
     * 此方法用于确认改签
     * @author zhaohongbo
     * @param partnerid 使用方id
     * @param method 调用方法名称
     * @param orderid 使用方订单号
     * @param transactionid 交易单号
     * @param reqtoken 唯一标识
     * @param isasync  是否为异步改签Y 或N：Y：异步改签；N：同步改签
     * @param callbackurl 回调地址
     * @return 同步返回的信息
     */
    public static JSONObject CoformRequestChange(String partnerid, String method, String orderid, String transactionid,
            String reqtoken, String isasync, String callbackurl) {
        JSONObject jsonObject = new JSONObject();

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        String reqtime = date.format(new Date());
        String sign;
        try {
            sign = MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8"), "utf-8");
            JSONObject json = new JSONObject();
            json.put("sign", sign);
            json.put("reqtime", reqtime);
            json.put("partnerid", partnerid);
            json.put("method", method);
            json.put("orderid", orderid);
            json.put("transactionid", transactionid);
            json.put("reqtoken", reqtoken);
            String result = SendPostandGet.submitPost(Url, "jsonStr=" + json, "UTF-8").toString();
            jsonObject.put("result", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 此方法用于取消改签
     * @author zhaohongbo
     * @param partnerid 用户帐号
     * @param method 调用接口的方法名
     * @param orderid 使用方订单号 
     * @param transactionid 交易单号
     * @param reqtoken 唯一标识
     * @return 同步返回的信息
     */

    public static JSONObject CannalRequestChange(String partnerid, String method, String orderid, String transactionid,
            String reqtoken) {
        JSONObject jsonObject = new JSONObject();

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        String reqtime = date.format(new Date());
        String sign;
        try {
            sign = MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8"), "utf-8");
            JSONObject json = new JSONObject();
            json.put("sign", sign);
            json.put("reqtime", reqtime);
            json.put("partnerid", partnerid);
            json.put("method", method);
            json.put("orderid", orderid);
            json.put("transactionid", transactionid);
            json.put("reqtoken", reqtoken);
            String result = SendPostandGet.submitPost(Url, "jsonStr=" + json, "UTF-8").toString();
            jsonObject.put("result", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 此方法用于请求改签
     * {"method":"train_request_change","partnerid":"shanglvelutong","reqtime":"20160703152520",
     * "sign":"31199780520865de9448b4262ac1110c","orderid":"181607030036",
     * "transactionid":"T160703B7352DFD04E33047790A9460B6D6F7A0B5BF","ordernumber":"E971327585",
     * "change_checi":"G1275","change_datetime":"2016-07-03 00:00:00","change_zwcode":"O",
     * "old_zwcode":"O","isasync":"Y","callbackurl":"http://op.bee2c.com/services/ws/http/applychangecallback",
     * "reqtoken":"1467530720143|181607030036_1","LoginUserName":null,"LoginUserPassword":null,
     * "ticketinfo":[{"passengersename":"李亚楠","passporttypeseid":"1","passportseno":"210701199110049417",
     * "piaotype":"1","old_ticket_no":"E971327585107006A"}]}
     * @author zhaohongbo
     * @param partnerid 用户帐号
     * @param method 调用接口的方法名
     * @param from_station_code 出发站code 
     * @param from_station_name 出发站名
     * @param to_station_code 到达站code
     * @param to_station_name 到达站名
     * @param orderid 使用方订单号
     * @param transactionid 交易单号
     * @param ordernumber  取票单号
     * @param change_checi 改签新车票的车次
     * @param change_datetime 改签新车票出发时间，格式yyyy-MM-dd HH:mm:ss，如：2014-05-30 17:32:00
     * @param change_zwcode 改签新车票的座位席别编码
     * @param old_zwcode 原票的座位席别编码
     * @param ticketinfo 改签车票信息，可以是多张车票，因此是json数组格式
     * @param isasync 是否为异步改签Y 或N:Y：异步改签；N：同步改签
     * @param callbackurl 回调地址
     * @param reqtoken 唯一标识 
     * @param isTs 是否是变站（ 如果为true ， 表示变站。to_station_name 必须要有值）
     * @param LoginUserName 12306 用户名（字母、数字或“_”,字母开头）
     * @param LoginUserPassword 12306 密码（字母、数字或符号）
     * @return 同步返回的信息
     */
    public static JSONObject RequestChange(String partnerid, String method, String from_station_code,
            String from_station_name, String to_station_code, String to_station_name, String orderid,
            String transactionid, String ordernumber, String change_checi, String change_datetime,
            String change_zwcode, String old_zwcode, JSONArray ticketinfo, String isasync, String callbackurl,
            String reqtoken, boolean isTs, String LoginUserName, String LoginUserPassword) {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        String reqtime = date.format(new Date());
        String sign;
        //        ticketinfo = CreateTicketinfo();
        try {
            sign = MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8"), "utf-8");
            JSONObject json = new JSONObject();
            json.put("sign", sign);
            json.put("partnerid", partnerid);
            json.put("reqtime", reqtime);
            json.put("isTs", isTs);
            json.put("from_station_code", from_station_code);
            json.put("from_station_name", URLEncoder.encode(from_station_name, "UTF-8"));
            json.put("to_station_name", URLEncoder.encode(to_station_name, "UTF-8"));
            json.put("to_station_code,", to_station_code);
            json.put("method", method);
            json.put("orderid", orderid);
            json.put("transactionid", transactionid);
            json.put("ordernumber", ordernumber);
            json.put("change_checi", change_checi);
            jsonObject.put("reqtime", reqtime);
            json.put("change_datetime", change_datetime);
            json.put("change_zwcode", change_zwcode);
            json.put("old_zwcode", old_zwcode);
            json.put("isasync", isasync);
            json.put("callbackurl", callbackurl);
            json.put("reqtoken", reqtoken);
            json.put("LoginUserName", LoginUserName);
            json.put("LoginUserPassword", LoginUserPassword);
            json.put("ticketinfo", ticketinfo);
            System.out.println(json);
            String result = SendPostandGet.submitPost(Url, "jsonStr=" + json, "UTF-8").toString();
            jsonObject.put("result", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 此方法用于查询订单详情和取消订单   method方法区分
     * @author zhaohongbo
     * @param partnerid 用户帐号
     * @param method 调用接口的方法名
     * @param orderid  使用方订单号
     * @param transactionid 交易单号
     * @return 同步返回的信息
     */
    public static JSONObject QueryOrderDetails(String partnerid, String method, String orderid, String transactionid) {
        JSONObject json = new JSONObject();

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        String reqtime = date.format(new Date());
        String sign;
        try {
            sign = MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8"), "utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sign", sign);
            jsonObject.put("partnerid", partnerid);
            jsonObject.put("method", method);
            jsonObject.put("orderid", orderid);
            jsonObject.put("transactionid", transactionid);
            jsonObject.put("reqtime", reqtime);
            String result = SendPostandGet.submitPost(Url, "jsonStr=" + jsonObject, "UTF-8").toString();
            json.put("result", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 此方法用于确认出票
     * @author zhaohongbo
     * @param partnerid    用户账号
     * @param method 接口调用的方法名
     * @param orderid 使用方订单号
     * @param transactionid 交易单号
     * @return 同步返回的信息
     */
    public static JSONObject ConfirmTrainTicket(String partnerid, String method, String orderid, String transactionid) {
        JSONObject json = new JSONObject();

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        String reqtime = date.format(new Date());
        String sign;
        try {
            sign = MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8"), "utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sign", sign);
            jsonObject.put("partnerid", partnerid);
            jsonObject.put("method", method);
            jsonObject.put("orderid", orderid);
            jsonObject.put("reqtime", reqtime);
            jsonObject.put("transactionid", transactionid);
            jsonObject.put("reqtime", reqtime);
            String result = SendPostandGet.submitPost(Url, "jsonStr=" + jsonObject, "UTF-8").toString();
            json.put("result", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 此方法用于查询余票 （有价格和无价格，根据method判断）
     * @param partnerid 用户帐号
     * @param method 接口调用方法
     * @param train_date 乘车日期（yyyy-MM-dd）
     * @param from_station 出发站code
     * @param to_station 到达站code
     * @param purpose_codes 订票类别，如“ADULT”表示普通票
     * @param needdistance 是否需要里程(“1”需要；其他值不需要),默认为0
     * @return
     */
    public static JSONObject QueryTicket(String partnerid, String method, String train_date, String from_station,
            String to_station, String purpose_codes, String needdistance) {
        JSONObject json = new JSONObject();

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");

        String reqtime = date.format(new Date());
        try {
            JSONObject jsonObject = new JSONObject();
            String sign = MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8"), "utf-8");
            jsonObject.put("sign", sign);
            jsonObject.put("partnerid", partnerid);
            jsonObject.put("method", method);
            jsonObject.put("from_station", from_station);
            jsonObject.put("to_station", to_station);
            jsonObject.put("reqtime", reqtime);
            jsonObject.put("train_date", train_date);
            jsonObject.put("purpose_codes", purpose_codes);
            jsonObject.put("needdistance", needdistance);
            String result = SendPostandGet.submitPost(Url, "jsonStr=" + jsonObject, "UTF-8").toString();
            json.put("json", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 此方法用于提交订单
     * @param partnerid 用户账号
     * @param method 接口调用方法
     * @param orderid 使用方订单号
     * @param checi 车次
     * @param from_station_code 出发站简码
     * @param from_station_name 出发站名称
     * @param to_station_code 到达站简码
     * @param to_station_name 到达站名称
     * @param train_date 乘车日期
     * @param is_accept_standing  是否要无座票，true 要;false 或者不传不要
     * @param passengers 乘客信息的json 字符串。可以是多个乘客信息，最多5 个
     * @param LoginUserName 12306 用户名
     * @param LoginUserPassword 12306 密码
     * @param callbackurl 占座成功回调地址
     * @return 同步返回信息
     */

    public static JSONObject CreateOrder(String partnerid, String method, String orderid, String checi,
            String from_station_code, String from_station_name, String to_station_code, String to_station_name,
            String train_date, boolean is_accept_standing, JSONArray passengers, String LoginUserName,
            String LoginUserPassword, String callbackurl) {
        String result = "提交请求失败";
        JSONObject json = new JSONObject();
        try {

            SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
            String reqtime = date.format(new Date());
            //     passengers = CreatePassengers();
            String sign = MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8"), "utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sign", sign);
            jsonObject.put("partnerid", partnerid);
            jsonObject.put("passengers", passengers);
            jsonObject.put("method", method);
            jsonObject.put("is_accept_standing", is_accept_standing);
            jsonObject.put("to_station_code", to_station_code);
            jsonObject.put("train_date", train_date);
            jsonObject.put("callbackurl", callbackurl);
            jsonObject.put("reqtime", reqtime);
            jsonObject.put("from_station_name", URLEncoder.encode(from_station_name, "UTF-8"));
            jsonObject.put("checi", checi);
            jsonObject.put("orderid", orderid);
            jsonObject.put("from_station_code", from_station_code);
            jsonObject.put("to_station_name", URLEncoder.encode(to_station_name, "UTF-8"));
            jsonObject.put("LoginUserName", LoginUserName);
            jsonObject.put("LoginUserPassword", LoginUserPassword);
            result = SendPostandGet.submitPost(Url, "jsonStr=" + jsonObject, "UTF-8").toString();
            json.put("json", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 此方法用于退票
     * @author zhaohongbo
     * @param partnerid 用户帐号
     * @param method 接口调用方法
     * @param orderid 使用方订单号 
     * @param transactionid 交易单号
     * @param ordernumber  取票单号（取票的单号）
     * @param reqtoken 唯一标识 
     * @param callbackurl 回调地址
     * @param LoginUserName 12306帐号
     * @param LoginUserPassword 12306密码
     * @param tickets 车票信息 json 字符串数组形式，主要包含车票的乘车人信息，乘车人姓名、乘车人证件类型ID 和乘车人证件号码
     * @return 同步返回参数
     */
    public static JSONObject returnTicket(String partnerid, String method, String orderid, String transactionid,
            String ordernumber, String reqtoken, String callbackurl, String LoginUserName, String LoginUserPassword,
            JSONArray tickets) {
        String result = "提交请求失败";
        JSONObject json = new JSONObject();
        try {

            SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
            String reqtime = date.format(new Date());
            //     passengers = CreatePassengers();
            String sign = MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8"), "utf-8");
            //            tickets = CreateTicketinfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sign", sign);
            jsonObject.put("partnerid", partnerid);
            jsonObject.put("method", method);
            jsonObject.put("callbackurl", callbackurl);
            jsonObject.put("reqtime", reqtime);
            jsonObject.put("orderid", orderid);
            jsonObject.put("LoginUserName", LoginUserName);
            jsonObject.put("LoginUserPassword", LoginUserPassword);
            jsonObject.put("tickets", tickets);
            jsonObject.put("ordernumber", ordernumber);
            jsonObject.put("transactionid", transactionid);
            jsonObject.put("reqtoken", reqtoken);
            System.out.println(jsonObject);
            result = SendPostandGet.submitPost(Url, "jsonStr=" + jsonObject, "UTF-8").toString();
            json.put("json", result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     *此方法用于创建ticket列表(个别接口ticket列表参数不同，注意！！！)
     * @author zhaohongbo
     * "ticketinfo":[{"passengersename":"李亚楠","passporttypeseid":"1","passportseno":"210701199110049417",
     * "piaotype":"1","old_ticket_no":"E971327585107006A"}]
     * @param passengersename 乘客姓名
     * @param passporttypeseid 证件类型编号
     * @param passportseno 证件号
     * @param piaotype 车票类型（成人票，儿童票）
     * @param ticket_no  车票票号
     * @return 返回结果
     * @throws UnsupportedEncodingException 
     */
    public static JSONArray CreateTicketinfo(String passengersename, String passporttypeseid, String passportseno,
            String piaotype, String old_ticket_no) throws UnsupportedEncodingException {
        JSONArray json = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        //        jsonObject.put("passengersename", URLEncoder.encode(passengersename, "UTF-8"));
        jsonObject.put("passengername", URLEncoder.encode(passengersename, "UTF-8"));
        jsonObject.put("passporttypeseid", passporttypeseid);
        jsonObject.put("passportseno", passportseno);
        jsonObject.put("piaotype", URLEncoder.encode(piaotype, "UTF-8"));
        //        jsonObject.put("old_ticket_no", old_ticket_no);
        jsonObject.put("ticket_no", old_ticket_no);
        json.add(jsonObject);
        return json;
    }

    /**
     * 此方法用于创建乘客信息json字段 
     * @author zhaohongbo
     * @param passengerid 乘客序列号
     * @param ticket_no 票号（此票在本订单中的唯一标识）
     * @param passengersename 乘客姓名
     * @param passportseno 乘客证件号码
     * @param passporttypeseid 证件类型ID 名称对应关系:1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通行证，B:护照
     * @param passporttypeseidname 证件类型名称
     * @param piaotype 票种ID。票种名称对应关系：1:成人票，2:儿童票，3:学生票，4:残军票
     * @param piaotypename 票种名称
     * @param zwcode 座位编码。与座位名称对应关系：9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，
    4:软卧，3:硬卧，2:软座，1:硬座注意：当最低的一种座位，无票时，购买选择该
    座位种类， 买下的就是无座(也就说买无座的席别编码就是该车次的最低席别的
    编码)，另外，当最低席别的票卖完了的时候才可以卖无座的票。
     * @param zwname 座位名称
     * @param cxin 几车厢几座（在订票成功后才会有值，如：‘15 车厢，20 号上铺’）
     * @return 返回结果
     * @throws UnsupportedEncodingException 
     */

    public static JSONArray CreatePassengers(int passengerid, String ticket_no, String passengersename,
            String passportseno, String passporttypeseid, String passporttypeseidname, String piaotype,
            String piaotypename, String zwcode, String zwname, String price) throws UnsupportedEncodingException {
        JSONArray json = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        //退票
        //        jsonObject.put("ticket_no", ticket_no);
        //        jsonObject.put("passengername", URLEncoder.encode(passengersename, "UTF-8"));
        //        jsonObject.put("passportseno", passportseno);
        //        jsonObject.put("passporttypeseid", passporttypeseid);
        //申请改签
        jsonObject.put("old_ticket_no", ticket_no);
        jsonObject.put("passengersename", URLEncoder.encode(passengersename, "UTF-8"));
        jsonObject.put("passportseno", passportseno);
        jsonObject.put("passporttypeseid", passporttypeseid);
        jsonObject.put("piaotype", piaotype);
        //        piaotype
        //下单
        //        jsonObject.put("passengerid", passengerid);
        //        jsonObject.put("ticket_no", ticket_no);
        //        jsonObject.put("passengersename", URLEncoder.encode(passengersename, "UTF-8"));
        //        jsonObject.put("passportseno", passportseno);
        //        jsonObject.put("passporttypeseid", passporttypeseid);
        //        jsonObject.put("passporttypeseidname", URLEncoder.encode(passporttypeseidname, "UTF-8"));
        //        jsonObject.put("piaotype", piaotype);
        //        jsonObject.put("piaotypename", URLEncoder.encode(piaotypename, "UTF-8"));
        //        jsonObject.put("zwcode", zwcode);
        //        jsonObject.put("zwname", URLEncoder.encode(zwname, "UTF-8"));
        //        //        jsonObject.put("cxin", cxin);
        //        jsonObject.put("price", price);
        //        jsonObject.put("reason", reason);
        json.add(jsonObject);
        return json;
    }

}
