/**
 * 
 */
package test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.insurance.SunshineInsuranceTrain;

/**
 * 
 * @time 2016年6月6日 下午1:57:16
 * @author chendong
 */
public class SunshineInsuranceTrainTest {

    public static void main(String[] args) {
        String eMailPath = "";
        String urlStr = "http://114.251.229.211:7002/ifp/SyncInterface";
        String username = "G5RH4T1HJY6UKUK41UJ3TG236ED";
        String password = "1THTH1Y3KM5L6IOU4KI6L412F1";
        String interface_flag = "BAYG";
        String key = "f15r8gth2yjny6jy56";
        String produceCode = "QP000103";//[30元阳光航空意外险(舱到舱)

        //        测试嘞s
        urlStr = "http://1.202.235.81:7001/ifp/SyncInterface";
        username = "boang";
        password = "boangtest";
        interface_flag = "BAYG";
        key = "ldlhpwefhorehjgoer";
        produceCode = "QP000103";//[30元阳光航空意外险(舱到舱)
        double oremium = 30d;//保费
        double amount = 3205000.00;//保费
        int insurperiod = 1;//保险期间 (天)
        //        测试嘞e
        SunshineInsuranceTrain sunshineInsuranceTrain = new SunshineInsuranceTrain(eMailPath, urlStr, username,
                password, interface_flag, key, produceCode, oremium, amount, insurperiod);
        TestBuy(sunshineInsuranceTrain);
        //        cancel(sunshineInsuranceTrain);
        //        Timestamp flytime = new Timestamp(1475197485040L);

        //        System.out.println(System.currentTimeMillis());
    }

    //  [2016-04-14 15:19:51.180] 7823160:deduction:true
    //        [2016-04-14 15:19:51.696] 7823160:火易险请求:listinsuranceToStr:[{"agentid":1211,"begintime":1462760280000,"birthday":741456000000,"code":"321321199307017419","codeTypestr":"身份证","codetype":1,"flyno":"6021","flytime":1463015880000,"id":711,"insurprice":5,"insurstatus":1,"insurstatusstr":"投保成功","mobile":"13382019200","name":"张争严","orderid":25128,"ordernum":"T201604146787366314","paystatus":0,"paystatusstr":"未支付","policyno":"BA201604141520509405","statusstr":"投保成功","timeLimit":0,"userid":0}]
    //        [2016-04-14 15:19:51.731] 7823160:result:{"code":100,"msg":"投保请求已接受","success":true}
    //        [2016-04-14 15:21:29.650] 1163687:idtype:1:idno:321321199307017419:traindate:2016-05-09 10:18:00:birthday:1993-07-01:trainordernumber:T201604146787366314:name:张争严:tradeno:6021:mobile:13041234677:agentid:1211:insurprice:5.00:type:1:PayType:1
    //        [2016-04-14 15:21:29.671] 1163687:orderid:25128
    //        [2016-04-14 15:21:29.672] 1163687:insur:{"agentid":1211,"begintime":1462760280000,"birthday":741456000000,"code":"321321199307017419","codeTypestr":"身份证","codetype":1,"flyno":"6021","flytime":1462760280000,"id":0,"insurprice":5,"insurstatus":0,"insurstatusstr":"待投保","mobile":"13041234677","name":"张争严","orderid":25128,"ordernum":"T201604146787366314","paystatus":0,"paystatusstr":"未支付","statusstr":"待投保","timeLimit":0,"userid":0}
    //        [2016-04-14 15:21:29.693] 1163687:insur:{"agentid":1211,"begintime":1462760280000,"birthday":741456000000,"code":"321321199307017419","codeTypestr":"身份证","codetype":1,"flyno":"6021","flytime":1462760280000,"id":712,"insurprice":5,"insurstatus":0,"insurstatusstr":"待投保","mobile":"13041234677","name":"张争严","orderid":25128,"ordernum":"T201604146787366314","paystatus":0,"paystatusstr":"未支付","statusstr":"待投保","timeLimit":0,"userid":0}
    //        [2016-04-14 15:21:29.746] 1163687:deduction:true
    //        [2016-04-14 15:21:29.896] 1163687:火易险请求:listinsuranceToStr:[{"agentid":1211,"begintime":1462760280000,"birthday":741456000000,"code":"321321199307017419","codeTypestr":"身份证","codetype":1,"flyno":"6021","flytime":1463015880000,"id":712,"insurprice":5,"insurstatus":1,"insurstatusstr":"投保成功","mobile":"13041234677","name":"张争严","orderid":25128,"ordernum":"T201604146787366314","paystatus":0,"paystatusstr":"未支付","policyno":"BA201604141522299406","statusstr":"投保成功","timeLimit":0,"userid":0}]
    //        [2016-04-14 15:21:29.911] 1163687:result:{"code":100,"msg":"投保请求已接受","success":true}

    /**
     * 
     * @param string
     * @time 2016年6月6日 下午3:55:08
     * @author chendong
     * @param sunshineInsuranceTrain 
     */
    private static void cancel(SunshineInsuranceTrain sunshineInsuranceTrain) {
        Insuruser insuruser = new Insuruser();
        insuruser.setPolicyno("BA201606061549542409");
        sunshineInsuranceTrain.cancelOrderAplylist(insuruser);
    }

    /**
     * 
     * @time 2016年6月6日 下午3:03:34
     * @author chendong
     * @param sunshineInsuranceTrain 
     */
    private static void TestBuy(SunshineInsuranceTrain sunshineInsuranceTrain) {
        Insuruser insuruser = new Insuruser();
        insuruser.setId(8888L);
        Timestamp flytime = new Timestamp(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 60));
        insuruser.setFlytime(flytime);
        String mobile = "13041234677";
        insuruser.setMobile(mobile);
        String name = "陈栋";
        insuruser.setName(name);
        long codetype = 1L;
        insuruser.setCodetype(codetype);
        String code = "412823198909298017";
        insuruser.setCode(code);
        String flyno = "CA1331";
        insuruser.setFlyno(flyno);
        String email = "";
        insuruser.setEmail(email);
        //        insuruser.setBirthday(birthday);//如果是身份证的话可不填

        insuruser = sunshineInsuranceTrain.Buyinsur(insuruser);//测试投保
        System.out.println(insuruser);
    }

    //    String maildizhi = "249016428@qq.com";
    public static void main2(String[] args) {
        String mobile = "18888832156";
        //    static String mobile = "13041234677";
        //    f15r8gth2yjny6jy56<?xml version="1.0" encoding="GBK"?><INSURENCEINFO><USERNAME>G5RH4T1HJY6UKUK41UJ3TG236ED</USERNAME><PASSWORD>1THTH1Y3KM5L6IOU4KI6L412F1</PASSWORD><ORDER><ORDERID>143252710556403378</ORDERID><POLICYINFO><PRODUCTCODE>QP010901</PRODUCTCODE><PLANCODE></PLANCODE><INSURDATE>2015-05-25 12:11:45</INSURDATE><INSURSTARTDATE>2015-06-04 11:11:45</INSURSTARTDATE><INSURENDDATE>2015-06-07 11:11:45</INSURENDDATE><APPNTMOBILE>13041234677</APPNTMOBILE><APPNTNAME>陈栋</APPNTNAME><APPNTIDTYPE>10</APPNTIDTYPE><APPNTIDNO>412823198909298017</APPNTIDNO><APPNTBIRTHDAY>19890929</APPNTBIRTHDAY><INSURPERIOD>3</INSURPERIOD><PERIODFLAG>D</PERIODFLAG><MULT>1</MULT><AGREEMENTNO>860114020001</AGREEMENTNO><PREMIUM>20.0</PREMIUM><AMOUNT>805000</AMOUNT><BENEFMODE>0</BENEFMODE><AIRLINEDATE>2015-06-04 12:11:45</AIRLINEDATE><AIRLINENO>2549</AIRLINENO><INSUREDLIST><INSURED><INSUREDNAME>陈栋</INSUREDNAME><RELATIONSHIP>10</RELATIONSHIP><INSUREDIDNO>412823198909298017</INSUREDIDNO><INSUREDIDTYPE>10</INSUREDIDTYPE><INSUREDBIRTHDAY>19890929</INSUREDBIRTHDAY><INSUREDMOBILE>13041234677</INSUREDMOBILE><INSUREDEMAIL>249016428@qq.com</INSUREDEMAIL></INSURED></INSUREDLIST></POLICYINFO></ORDER></INSURENCEINFO>
        //        String urlStr = "http://1.202.235.81:7001/ifp/SyncInterface";
        String urlStr = "http://1.202.235.81:7001/ifp-R201506034A/SyncInterface";//测试10岁的保险
        String username = "boang";
        String password = "boangtest";
        String interfaceFlag = "BAYG";
        String key = "ldlhpwefhorehjgoer";
        int iszhengshi = 0;
        if (iszhengshi == 1) {//正式地址和配置
            //TEST========================E
            urlStr = "http://114.251.229.211:7002/ifp/SyncInterface";
            username = "G5RH4T1HJY6UKUK41UJ3TG236ED";
            password = "1THTH1Y3KM5L6IOU4KI6L412F1";
            interfaceFlag = "BAYG";
            key = "f15r8gth2yjny6jy56";
        }
        String data = "<?xml version=\"1.0\" encoding=\"GBK\"?><INSURENCEINFO><USERNAME>G5RH4T1HJY6UKUK41UJ3TG236ED</USERNAME><PASSWORD>1THTH1Y3KM5L6IOU4KI6L412F1</PASSWORD><ORDER><ORDERID>143252710556403378</ORDERID><POLICYINFO><PRODUCTCODE>QP010901</PRODUCTCODE><PLANCODE></PLANCODE><INSURDATE>2015-05-25 12:11:45</INSURDATE><INSURSTARTDATE>2015-06-04 11:11:45</INSURSTARTDATE><INSURENDDATE>2015-06-07 11:11:45</INSURENDDATE><APPNTMOBILE>13041234677</APPNTMOBILE><APPNTNAME>陈栋</APPNTNAME><APPNTIDTYPE>10</APPNTIDTYPE><APPNTIDNO>412823198909298017</APPNTIDNO><APPNTBIRTHDAY>19890929</APPNTBIRTHDAY><INSURPERIOD>3</INSURPERIOD><PERIODFLAG>D</PERIODFLAG><MULT>1</MULT><AGREEMENTNO>860114020001</AGREEMENTNO><PREMIUM>20.0</PREMIUM><AMOUNT>805000</AMOUNT><BENEFMODE>0</BENEFMODE><AIRLINEDATE>2015-06-04 12:11:45</AIRLINEDATE><AIRLINENO>2549</AIRLINENO><INSUREDLIST><INSURED><INSUREDNAME>陈栋</INSUREDNAME><RELATIONSHIP>10</RELATIONSHIP><INSUREDIDNO>412823198909298017</INSUREDIDNO><INSUREDIDTYPE>10</INSUREDIDTYPE><INSUREDBIRTHDAY>19890929</INSUREDBIRTHDAY><INSUREDMOBILE>13041234677</INSUREDMOBILE><INSUREDEMAIL>249016428@qq.com</INSUREDEMAIL></INSURED></INSUREDLIST></POLICYINFO></ORDER></INSURENCEINFO>";
        SunshineInsuranceTrain si = new SunshineInsuranceTrain();
        String sign = si.getSign(key, data, "SunshineInsuranceTrain_test", 111L);
        System.out.println(System.currentTimeMillis() + 8640000000L);

    }

    public static void main3(String[] args) {
        String maildizhi = "zengqingquan@clbao.com";

        //    String maildizhi = "249016428@qq.com";

        String mobile = "18888832156";

        //    static String mobile = "13041234677";
        Insuruser insuruser = new Insuruser();
        Long l1 = System.currentTimeMillis();
        SunshineInsuranceTrain si = new SunshineInsuranceTrain();

        String resultString = "";
        //TEST========================S
        //        String urlStr = "http://1.202.235.81:7001/ifp/SyncInterface";
        String urlStr = "http://1.202.235.81:7001/ifp-R201506034A/SyncInterface";//测试10岁的保险
        String username = "boang";
        String password = "boangtest";
        String interfaceFlag = "BAYG";
        String key = "ldlhpwefhorehjgoer";
        int iszhengshi = 1;
        if (iszhengshi == 0) {
            //TEST========================E
            urlStr = "http://114.251.229.211:7002/ifp/SyncInterface";
            username = "G5RH4T1HJY6UKUK41UJ3TG236ED";
            password = "1THTH1Y3KM5L6IOU4KI6L412F1";
            interfaceFlag = "BAYG";
            key = "f15r8gth2yjny6jy56";
        }

        si.setUrlStr(urlStr);
        si.setUsername(username);
        si.setPassword(password);
        si.setInterfaceFlag(interfaceFlag);
        si.setKey(key);

        //=======退保测试开始
        insuruser.setPolicyno("BA201505251211058804");
        //        resultString = si.cancelOrderAplylist(insuruser);
        //        System.out.println(resultString);
        //=======退保测试结束
        //=======承保测试开始
        insuruser.setId(l1);
        insuruser.setMobile(mobile);
        insuruser.setName("陈栋");
        insuruser.setCodetype(1L);
        insuruser.setCode("412823200009298017");
        insuruser.setFlytime(new Timestamp(1441168110093L));
        insuruser.setFlyno("2549");
        List<Insuruser> insurusers = new ArrayList<Insuruser>();
        insurusers.add(insuruser);
        insuruser = (Insuruser) si.saveTrainOrderAplylist(null, insurusers, 1).get(0);
        //=======承保测试结束

    }
}
