package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;

public class ChendongqiangpiaoThread extends Thread {
    @Override
    public void run() {

    }

    //    static String repUrl = "http://121.40.18.42:8080/Reptile/traininit";

    //    static String repUrl = "http://localhost:9013/Reptile/traininit";

    //    static String cookieString = "JSESSIONID=426C80B734FE8496879C39E3851D934B; BIGipServerotn=1876492554.24610.0000; current_captcha_type=C;";
    //    static String cookieString = "JSESSIONID=D76C0B02AF3B018B62765212B10EB5C4; BIGipServerotn=987169034.64545.0000; current_captcha_type=C;";

    public static void main(String[] args) throws Exception {
        Long l1 = System.currentTimeMillis();
        //        String repUrl = "http://localhost:9013/Reptile/traininit";
        String repUrl = "http://120.26.56.83:8080/Reptile/traininit";
        String cookieString = "JSESSIONID=0A01D964FC608FDA703B9E702A70C64C3371225D92; BIGipServerotn=1691943178.64545.0000; current_captcha_type=Z";

        ChendongqiangpiaoThread chendongqp = new ChendongqiangpiaoThread();
        String zuoxie = "O";//9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
        String prices = "1@1@1@1@1";
        String zwcodess = zuoxie + "@" + zuoxie + "@" + zuoxie + "@" + zuoxie + "@" + zuoxie;//二等座

        zuoxie = "4";//9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
        //====================================================================陈栋

        //        passlist.add(new passengerinfo("张青", "412823199009261248"));
        //        zuoxie = "O";
        //        chendongqp.chendongqiang("2015-02-14", "北京", "驻马店", "G441", zuoxie);

        zuoxie = "O";//9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
        List<Passengerinfo> passlist = new ArrayList<Passengerinfo>();
        //        passlist.add(new Passengerinfo("赵明明", "412823199210044835"));
        //        passlist.add(new Passengerinfo("赵婷婷", "412823199006170041"));
        passlist.add(new Passengerinfo("吕晓均", "330624198411230932"));
        zuoxie = "3";//9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
        String train_code = "K1198";
        //        train_code = "T160";

        chendongqp.searchandcreateorder(passlist, "2015-06-23", "驻马店", "泰山", train_code, zuoxie, cookieString, repUrl);

        zuoxie = "M";//9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
        //        chendongqp.searchandcreateorder(passlist, "2015-02-14", "北京", "驻马店", "D2031", zuoxie);
        //        zuoxie = "2";//9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
        //        chendongqp.searchandcreateorder(passlist, "2015-02-14", "北京", "驻马店", "Z4177", zuoxie);
        System.out.println("耗时:" + (System.currentTimeMillis() - l1));
    }

    /**
     * 查询余票（如果有票）并创建订单
     * 
     * @param train_date
     * @param sname
     * @param ename
     * @param train_code
     * @param zuoxie //9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
     * @time 2015年1月31日 下午8:06:05
     * @author chendong
     * @throws Exception 
     */
    private void searchandcreateorder(List<Passengerinfo> passlist, String train_date, String sname, String ename,
            String train_code, String zuoxie, String cookieString, String repUrl) throws Exception {
        String result = "";
        String scode = sname;
        String ecode = ename;
        try {
            scode = Train12306StationInfoUtil.getThreeByName(sname);
            ecode = Train12306StationInfoUtil.getThreeByName(ename);
        }
        catch (Exception e) {
        }
        String ypinfo = "1";
        Train train = searchyupiao(scode, ecode, train_date, train_code);
        ypinfo = getypinfo(train, zuoxie);
        System.out.println(ypinfo);
        if (!"0".equals(ypinfo) && !"--".equals(ypinfo)) {//如果余票不为0去下单
            //            cookieString = rep12Method("cd1989929007", "nicaicai1", repUrl);
            System.out.println("cookieString:" + cookieString);
            String resultString = chendongqiang(passlist, train_date, sname, ename, train_code, zuoxie, cookieString,
                    repUrl);
            if (resultString.contains("12306订单号为")) {

            }
            System.out.println(resultString);
        }
    }

    /**
     * 
     * 
     * @param train_date String train_date = "2015-02-07";
     * @param sname String sname = "北京";
     * @param ename String ename = "驻马店";
     * @param train_code String train_code = "G519";
     * @param zuoxie String zuoxie = "2";//9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
     * @throws Exception
     * @time 2015年1月30日 下午3:58:33
     * @author chendong
     */
    public String chendongqiang(List<Passengerinfo> passlist, String train_date, String sname, String ename,
            String train_code, String zuoxie, String cookieString, String repUrl) throws Exception {
        String prices = "1@1@1@1@1";
        String zwcodess = zuoxie + "@" + zuoxie + "@" + zuoxie + "@" + zuoxie + "@" + zuoxie;//二等座
        String oldPassengerStrs = getoldPassengerStrsstring(passlist);
        String passengerTicketStrs = getpassengerTicketStrsstring(passlist, zuoxie);
        String jieguo;
        jieguo = chendongqiangpiao(oldPassengerStrs, passengerTicketStrs, zwcodess, sname, ename, train_code,
                train_date, prices, cookieString, repUrl);
        System.out.println(train_date + "=" + train_code + "=" + jieguo);
        return jieguo;
    }

    /**
     * 张文娟  zhangwenjuan-101@163.com juan528798 关欣 北京 阜阳 2015-02-14 软卧 硬卧  342127198310010063 
     * 
     * @time 2015年1月29日 上午11:22:13
     * @author chendong
     * @throws Exception 
     */
    public void zhangwenjuanqiang(String train_date, String sname, String ename, String train_code, String zuoxie,
            String cookieString, String repUrl) throws Exception {
        //        String train_date = "2015-02-07";
        //        String sname = "北京";
        //        String ename = "阜阳";
        //        String train_code = "K4207";
        //        String zuoxie = "2";//9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]

        String prices = "1@1@1@1@1";
        String zwcodess = zuoxie + "@" + zuoxie + "@" + zuoxie + "@" + zuoxie + "@" + zuoxie;//二等座

        List<Passengerinfo> passlist = new ArrayList<Passengerinfo>();
        passlist.add(new Passengerinfo("关欣", "152201198112302010"));
        passlist.add(new Passengerinfo("张文娟", "342127198310010063"));

        //        String oldPassengerStrs = "关欣,1,152201198112302010,1_张文娟,1,342127198310010063,1_";
        String oldPassengerStrs = getoldPassengerStrsstring(passlist);
        //        String passengerTicketStrs = zuoxie + ",0,1,关欣,1,152201198112302010,,N_" + zuoxie
        //                + ",0,1,张文娟,1,342127198310010063,,N";
        String passengerTicketStrs = getpassengerTicketStrsstring(passlist, zuoxie);
        String jieguo = chendongqiangpiao(oldPassengerStrs, passengerTicketStrs, zwcodess, sname, ename, train_code,
                train_date, prices, cookieString, repUrl);
        System.out.println(train_code + "=" + jieguo);
    }

    private String getoldPassengerStrsstring(List<Passengerinfo> passlist) {
        String oldPassengerStrs = "";
        for (int i = 0; i < passlist.size(); i++) {
            String ticketType = "1";
            oldPassengerStrs += passlist.get(i).getName() + ",1," + passlist.get(i).getIdnumber() + "," + ticketType;
            if (i < passlist.size() - 1) {
                oldPassengerStrs += "_";
            }
        }
        return oldPassengerStrs;
    }

    private String getpassengerTicketStrsstring(List<Passengerinfo> passlist, String zuoxie) {
        String oldPassengerStrs = "";
        for (int i = 0; i < passlist.size(); i++) {
            String ticketType = "1";
            oldPassengerStrs += zuoxie + ",0," + ticketType + "," + passlist.get(i).getName() + ",1,"
                    + passlist.get(i).getIdnumber() + ",,N";
            if (i < passlist.size() - 1) {
                oldPassengerStrs += "_";
            }
        }
        return oldPassengerStrs;
    }

    /**
     * @param train_code
     * @param from_station
     * @param to_station
     * @param from_station_name
     * @param to_station_name
     * @param train_date
     * @param loginName
     * @param logpassword
     * @param cookieString
     * @param orderid
     * @param oldPassengerStr    闫青寿,1,142232196111120032,1_王战朝,1,410883199006281010,1_陶蕾,1,341222198912259192,1_
     * @param zwcodes 1@1@1   9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
     * @param passengerTicketStr 1,0,1,闫青寿,1,142232196111120032,,N_1,0,1,王战朝,1,410883199006281010,,N_1,0,1,陶蕾,1,341222198912259192,,N
     * @param price  60.0@60.0@60.0
     * @return
     * 
     * @time 2015年1月27日 下午4:50:16
     * @author chendong
     * @throws Exception 
     */
    private static String chendongqiangpiao(String oldPassengerStrs, String passengerTicketStrs, String zwcodess,
            String sname, String ename, String train_code, String train_date, String prices, String cookieString,
            String repUrl) throws Exception {

        String scode = Train12306StationInfoUtil.getThreeByName(sname);
        String ecode = Train12306StationInfoUtil.getThreeByName(ename);

        String qiangpiaaodata = train_code + "|" + scode + "|" + ecode + "|" + sname + "|" + ename + "|train_date|a|a|"
                + cookieString + "|T_123456|oldPassengerStrs|1|1|prices";
        String[] qiangpiaodatas = qiangpiaaodata.split("[|]");
        //        String train_code = qiangpiaodatas[0];
        String from_station = qiangpiaodatas[1];
        String to_station = qiangpiaodatas[2];
        String from_station_name = qiangpiaodatas[3];
        String to_station_name = qiangpiaodatas[4];
        //        String train_date = qiangpiaodatas[5];
        String loginName = qiangpiaodatas[6];
        String logpassword = qiangpiaodatas[7];
        //        String cookieString = qiangpiaodatas[8];
        String orderid = qiangpiaodatas[9];
        String oldPassengerStr = oldPassengerStrs;
        //        String zwcodes = zwcodess;
        String passengerTicketStr = passengerTicketStrs;
        String price = prices;

        JSONObject jsonobject = new JSONObject();
        JSONObject passinfo_o = new JSONObject();
        passinfo_o.put("oldPassengerStr", oldPassengerStr);
        passinfo_o.put("prices", price);
        passinfo_o.put("zwcodes", zwcodess);
        passinfo_o.put("passengerTicketStr", passengerTicketStr);

        jsonobject.put("cookie", cookieString);
        jsonobject.put("passengers", passinfo_o);
        jsonobject.put("to_station", to_station);
        jsonobject.put("train_date", train_date);
        jsonobject.put("from_station", from_station);
        jsonobject.put("from_station_name", from_station_name);
        jsonobject.put("loginPwd", logpassword);
        jsonobject.put("train_code", train_code);
        jsonobject.put("orderId", orderid);
        jsonobject.put("to_station_name", to_station_name);
        jsonobject.put("loginName", loginName);
        String paramContent = "datatypeflag=13&jsonStr=" + jsonobject.toJSONString();
        System.out.println(paramContent);
        String resultString = SendPostandGet.submitPost(repUrl, paramContent, "utf-8").toString();
        return resultString;
    }

    private static Train searchyupiao(String fromcity, String tocity, String date, String traincode) {
        FlightSearch flightSearch = new FlightSearch();
        flightSearch.setGeneral(2);
        try {
            List<Train> list = Server.getInstance().getAtomService()
                    .getDGTrainListcache(fromcity, tocity, date, flightSearch);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTraincode().equals(traincode)) {
                    return list.get(i);
                }
            }
        }
        catch (Exception e) {
        }
        Train train = new Train();
        return train;
    }

    /**
     * 根据Train和坐席获取余票信息
     * 
     * @param train
     * @param zuoxie
     * @time 2015年1月31日 下午8:07:42
     * @author chendong
     */
    private String getypinfo(Train train, String zuoxie) {
        //        9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座，0:站票[无座]
        String ypsum = "0";
        if ("9".equals(zuoxie)) {
            ypsum = train.getSwzyp();
        }
        else if ("P".equals(zuoxie)) {
            ypsum = train.getTdzyp();
        }
        else if ("M".equals(zuoxie)) {
            ypsum = train.getRz1yp();
        }
        else if ("O".equals(zuoxie)) {
            ypsum = train.getRz2yp();
        }
        else if ("6".equals(zuoxie)) {
            ypsum = train.getGwyp();
        }
        else if ("4".equals(zuoxie)) {
            ypsum = train.getRwyp();
        }
        else if ("3".equals(zuoxie)) {
            ypsum = train.getYwyp();
        }
        else if ("2".equals(zuoxie)) {
            ypsum = train.getRzyp();
        }
        else if ("1".equals(zuoxie)) {
            ypsum = train.getYzyp();
        }
        else if ("0".equals(zuoxie)) {
            ypsum = train.getYzyp();
        }
        return ypsum;
    }

    /**
     * 全自动登录,返回cookie
     * 
     * @param logname 12306账号  
     * @param logpassword 12306密码
     * @return
     * @time 2014年12月19日 下午7:41:51
     * @author chendong
     */
    public String rep12Method(String logname, String logpassword, String repUrl) {
        String resultString = "";
        String paramContent = "datatypeflag=12&logname=" + logname + "&logpassword=" + logpassword;
        resultString = SendPostandGet.submitPost(repUrl, paramContent, "utf-8").toString();
        return resultString;
    }
}
