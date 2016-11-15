package com.ccservice.b2b2c.atom.train.data.thread;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.util.HttpUtils;
import com.ccservice.elong.inter.PropertyUtil;

public class Tielu12306PriceThread implements Callable<Train> {
    public static void main(String[] args) {

        //        testzhuanfaisenable();
        //        JSONObject jsonobject = JSONObject
        //                .parseObject("{\"validateMessagesShowId\":\"_validatorMessage\",\"status\":true,\"httpstatus\":200,\"data\":{\"OT\":[],\"train_no\":\"58000D62010C\"},\"messages\":[],\"validateMessages\":{}}");
        //        JSONObject jsonobject_data = jsonobject.getJSONObject("data");
        //        System.out.println(jsonobject_data.keySet().size());

        Double Price = 750.0;
        int licheng = 2294;
        //        dataString:316.5|328.5
        String dataString = Tielu12306PriceThread.getTrainSZXPrice("Y", "Z126", 491.5, licheng);
        System.out.println("dataString:" + dataString);
    }

    String train_no;

    String from_station_no;

    String to_station_no;

    String seat_types;

    String time;

    String traincode;

    int randominti;

    Train train;

    boolean isSearchprice;

    boolean issearchlicheng;

    int isusecache = 1;//是否用的是缓存,默认为1,如果取得是12306的数据则变为0

    String ywprice = "-1";

    String rwprice = "-1";

    String gjrwprice = "-1";

    public Tielu12306PriceThread(Train train, String train_no, String from_station_no, String to_station_no,
            String seat_types, String time, String traincode, int randominti, Long temp, boolean isSearchprice,
            boolean issearchlicheng) {
        this.train_no = train_no;
        this.from_station_no = from_station_no;
        this.to_station_no = to_station_no;
        this.seat_types = seat_types;
        this.time = time;
        this.randominti = randominti;
        this.traincode = traincode;
        this.train = train;
        this.isSearchprice = isSearchprice;
        this.issearchlicheng = issearchlicheng;
    }

    @Override
    public Train call() throws Exception {
        Long l1 = System.currentTimeMillis();
        if (this.isSearchprice) {//查价格
            String json1 = "-1";
            String mcckey = traincode + "_" + from_station_no + "_" + to_station_no;
            try {
                int getjson = 0;
                do {
                    json1 = getjson1(mcckey, getjson);
                    getjson++;
                    if (getjson > 0) {
                        Thread.sleep(50);
                    }
                }
                while (json1.length() < 10 && getjson < 10);
            }
            catch (Exception e) {
                System.out.println(e.getMessage() + ":" + mcckey + ":异常了json1:" + json1);
                e.printStackTrace();
            }
            JSONObject pricejsonobject = new JSONObject();
            JSONObject jsonobject1 = new JSONObject();
            try {
                jsonobject1 = JSON.parseObject(json1);
                if (jsonobject1.get("data") != null) {
                    pricejsonobject = jsonobject1.getJSONObject("data");
                }
            }
            catch (Exception e) {
                //                System.out.println("json1:" + json1);
            }
            String wzyp = train.getWzyp();// 无座余票
            String yzyp = train.getYzyp();// 硬座余票
            String rzyp = train.getRzyp();// 软座余票
            String rz2yp = train.getRz2yp();// 二等软座余票
            String rz1yp = train.getRz1yp();// 一等软余票
            String tdzyp = train.getTdzyp();// 特等座余票
            String swzyp = train.getSwzyp();// 商务座余票
            String ywyp = train.getYwyp();// 硬卧余票
            String rwyp = train.getRwyp();// 软卧余票
            String gwyp = train.getGwyp();// 高级软卧余票
            String qtxb_num = train.getQtxb_num();//其他席别余票数量
            int ishaveprice = 0;//是否获取到价格如果有价格就把这个加1
            if (!"--".equals(wzyp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "WZ");
            }
            if (!"--".equals(yzyp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "A1");
            }
            if (!"--".equals(rzyp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "A2");
            }
            if (!"--".equals(rz2yp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "O");
            }
            if (!"--".equals(rz1yp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "M");
            }
            if (!"--".equals(tdzyp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "P");
            }
            if (!"--".equals(swzyp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "A9");
            }
            if (!"--".equals(ywyp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "A3");
            }
            if (!"--".equals(rwyp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "A4");
            }
            if (!"--".equals(gwyp)) {
                toParsePrice(pricejsonobject, json1, jsonobject1, mcckey, ishaveprice, "A6");
            }
            if (!"-1".equals(json1) && ishaveprice > 0 && isusecache == 0) {
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                try {
                    Date d1 = df.parse(df.format(new Date()));
                    Date d2 = df.parse("24:00:00");
                    long diff = d2.getTime() - d1.getTime();
                    long day = diff / (24 * 60 * 60 * 1000);
                    long hour = (diff / (60 * 60 * 1000) - day * 24);
                    long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                    Date date = new Date(1000 * s * min * hour);//缓存到凌晨00:00释放掉
                    creatAddMemcachedThreadThread(mcckey, json1, date, 2, time);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        if (this.issearchlicheng) {//查里程
            String start_station_name = train.getStart_station_name();
            String end_station_name = train.getEnd_station_name();
            String mcckey = "licheng" + train.getTraincode() + train.getFrom_station_code()
                    + train.getTo_station_code();
            int licheng = getlicheng(mcckey, start_station_name, end_station_name);
            train.setDistance(licheng);
        }
        return train;
    }

    /**
     * 根据里程,类型,车次,上铺价格计算其他铺位的价格
     * @param licheng    里程
     * @param Code       类型     R:软卧 Y:硬卧
     * @param TrainCode  车次
     * @param Price      上铺价格
     * @return luoqingxin
     */
    public static String getTrainSZXPrice(String Code, String TrainCode, Double Price, int... licheng) {
        String Moneys = "";
        String Moneyz = "";
        String Moneyx = "";
        try {
            List sqlResultList = new ArrayList();
            //            sqlResultList = getListlqx(licheng);//罗庆鑫
            sqlResultList = getListcd(Code, TrainCode, Price);//陈栋
            if (sqlResultList.size() > 0) {
                Map map = (Map) sqlResultList.get(0);
                //判断是否为特快、快速
                if (TrainCode.indexOf("Z") >= 0 || TrainCode.indexOf("T") >= 0 || TrainCode.indexOf("K") >= 0) {
                    Double tkTrains = Double.parseDouble(map.get(Code + "tkTrain_S").toString());
                    Moneys = "" + Price;
                    tkTrains = tkTrains - Price;//计算数据差价
                    if (!Code.equals("R")) {
                        Double tkTrainz = Double.parseDouble(map.get(Code + "tkTrain_Z").toString());
                        tkTrainz = tkTrainz - tkTrains;
                        Moneyz = "" + tkTrainz;
                    }
                    Double tkTrainx = Double.parseDouble(map.get(Code + "tkTrain_X").toString());
                    tkTrainx = tkTrainx - tkTrains;
                    Moneyx = tkTrainx + "";
                }
                else {
                    int traincode = 0;
                    try {
                        traincode = Integer.parseInt(TrainCode);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    //判断是否为普快
                    if (traincode >= 1001 && traincode <= 5998) {
                        Double pkTrains = Double.parseDouble(map.get(Code + "pkTrain_S").toString());
                        Moneys = "" + pkTrains;
                        pkTrains = pkTrains - Price;//计算数据差价
                        if (!Code.equals("R")) {
                            Double pkTrainz = Double.parseDouble(map.get(Code + "pkTrain_Z").toString());
                            pkTrainz = pkTrainz - pkTrains;
                            Moneyz = "" + pkTrainz;
                        }
                        Double pkTrainx = Double.parseDouble(map.get(Code + "pkTrain_X").toString());
                        pkTrainx = pkTrainx - pkTrains;
                        Moneyx = "" + pkTrainx;
                    }
                    else {
                        Double ptTrains = Double.parseDouble(map.get(Code + "ptTrain_S").toString());
                        Moneys = "" + ptTrains;
                        ptTrains = ptTrains - Price;//计算数据差价
                        if (!Code.equals("R")) {
                            Double ptTrainz = Double.parseDouble(map.get(Code + "ptTrain_Z").toString());
                            ptTrainz = ptTrainz - ptTrains;
                            Moneyz = "" + ptTrainz;
                        }
                        Double ptTrainx = Double.parseDouble(map.get(Code + "ptTrain_X").toString());
                        ptTrainx = ptTrainx - ptTrains;
                        Moneyx = "" + ptTrainx;
                    }

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            //            System.out.println(Moneys + Moneyz + Moneyx);
        }
        String dataString = Moneyz + "|" + Moneyx;
        if (Code.equals("R")) {
            dataString = Moneyx;
        }
        return dataString;
    }

    /**
     * 
     * @param licheng
     * @return
     * @time 2015年8月5日 下午4:49:58
     * @author chendong
     */
    private static List getListlqx(int licheng) {
        List sqlResultList = new ArrayList();
        String sqlString = "SELECT * FROM TrainFareListp WHERE " + licheng + ">MileageStart AND " + licheng
                + "<MileageEnd";
        sqlResultList = Server.getInstance().getSystemService().findMapResultBySql(sqlString, null);
        return sqlResultList;
    }

    /**
     * 
     * @return
     * @time 2015年8月5日 下午4:45:56
     * @author chendong
     */
    private static List getListcd(String Code, String TrainCode, Double Price) {
        List sqlResultList = new ArrayList();
        String TrainCode_COL = "YptTrain_S";
        String sqlString = "";
        int traincode = 0;
        try {
            traincode = Integer.parseInt(TrainCode);
        }
        catch (Exception e) {
        }
        //判断是否为普快
        if (TrainCode.toUpperCase().startsWith("Z") || TrainCode.toUpperCase().startsWith("T")
                || TrainCode.toUpperCase().startsWith("K")) {
            TrainCode_COL = "YtkTrain_S";
            if ("R".equals(Code)) {
                TrainCode_COL = "RtkTrain_S";
            }
        }
        else if (traincode >= 1001 && traincode <= 5998) {
            TrainCode_COL = "YpkTrain_S";
            if ("R".equals(Code)) {
                TrainCode_COL = "RpkTrain_S";
            }
        }
        for (int i = 1; i < 6; i++) {
            Double sPrice = Price - i;
            Double ePrice = Price + i;
            sqlString = "SELECT * FROM TrainFareListp where " + TrainCode_COL + " between " + sPrice + " and " + ePrice;
            //            System.out.println(sqlString);
            sqlResultList = Server.getInstance().getSystemService().findMapResultBySql(sqlString, null);
            if (sqlResultList.size() > 0) {
                break;
            }
        }
        return sqlResultList;
    }

    /**
     * 从各种地方获取到里程数
     * 
     * @param mcckey
     * @param start_station_name
     * @param end_station_name
     * @return
     * @time 2015年5月5日 下午7:02:51
     * @author chendong
     */
    public int getlicheng(String mcckey, String start_station_name, String end_station_name) {
        int licheng = 0;
        if (MemCached.getInstance().get(mcckey) == null) {
            String sql = "select C_DISTANCE from T_TRAINNO with(nolock) where C_STATION_TRAIN_CODE='"
                    + train.getTraincode() + "' and C_STATION_NAME in('" + start_station_name + "','"
                    + end_station_name + "') " + "and C_DISTANCE is not null";
            List list = getSystemService().findMapResultBySql(sql, null);
            if (list.size() >= 2) {
                try {
                    Map map = (Map) list.get(0);
                    String distance = map.get("C_DISTANCE") == null ? "0" : map.get("C_DISTANCE").toString();
                    Map map1 = (Map) list.get(1);
                    String distance1 = map1.get("C_DISTANCE") == null ? "0" : map1.get("C_DISTANCE").toString();
                    licheng = Math.abs(Integer.parseInt(distance) - Integer.parseInt(distance1));
                    Date date = new Date(1000 * 1 * 60 * 60 * 24 * 30);//30天缓存,因为这里是获取到了数据
                    MemCached.getInstance().add(mcckey, licheng);//
                }
                catch (Exception e) {
                }
            }
            else {
                Date date = new Date(1000 * 1 * 60 * 60 * 12);//12小时缓存,因为这里是没有获取到
                MemCached.getInstance().add(mcckey, licheng, date);
            }
        }
        else {
            try {
                licheng = Integer.parseInt(MemCached.getInstance().get(mcckey).toString());
            }
            catch (Exception e) {
                e.printStackTrace();
                MemCached.getInstance().delete(mcckey);
            }
        }
        return licheng;
    }

    /**
     * 从数据源里获取到价格数据
     * 
     * @param mcckey
     * @param isusecache
     * @return
     * @time 2015年5月5日 下午6:57:48
     * @author chendong
     * @param getjson 
     */
    private String getjson1(String mcckey, int getjson) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        int data_from = 0;//0缓存 1数据库 2:12306
        String json1 = getpricefrommcc(mcckey);//先去memcached取数据 String json1 = "-1";
        if (!data_isvalid(json1, 0)) {//如果memcached里没有去数据库取
            data_from = 1;
            json1 = getpricefromdb(mcckey);
            this.isusecache = 1;
            try {
                Date d1 = df.parse(df.format(new Date()));
                Date d2 = df.parse("24:00:00");
                long diff = d2.getTime() - d1.getTime();
                long day = diff / (24 * 60 * 60 * 1000);
                long hour = (diff / (60 * 60 * 1000) - day * 24);
                long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                Date date = new Date(1000 * s * min * hour);//缓存到凌晨00:00释放掉
                creatAddMemcachedThreadThread(mcckey, json1, date, 2, time);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!data_isvalid(json1, 1)) {//如果数据库还没有去12306取
            json1 = getpricefrom12306(from_station_no, to_station_no, seat_types, time);
            data_from = 2;
            this.isusecache = 0;
            if (data_isvalid(json1, 2)) {
                try {
                    Date d1 = df.parse(df.format(new Date()));
                    Date d2 = df.parse("24:00:00");
                    long diff = d2.getTime() - d1.getTime();
                    long day = diff / (24 * 60 * 60 * 1000);
                    long hour = (diff / (60 * 60 * 1000) - day * 24);
                    long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                    Date date = new Date(1000 * s * min * hour);//缓存到凌晨00:00释放掉
                    creatAddMemcachedThreadThread(mcckey, json1, date, 2, time);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        if (getjson > 0 || json1 == null) {
            //            System.out.println(data_from + ":" + getjson + ":" + json1);
        }
        return json1;
    }

    /**
     * 价格json是否有效
     * 
     * @param json1 
     * @param i  数据来源  0缓存 1数据库 2:12306
     * @return false 无效 true 有效
     * @time 2015年5月11日 上午11:54:38
     * @author chendong
     */
    private boolean data_isvalid(String json1, int i) {
        boolean isvalid = true;//默认是有效的
        if (json1 == null || "-1".equals(json1)) {
            isvalid = false;
        }
        else {
            try {
                if (JSONObject.parseObject(json1).getJSONObject("data").keySet().size() <= 4) {
                    isvalid = false;
                }
            }
            catch (Exception e) {
                //                System.out.println(json1);
                //                e.printStackTrace();
                isvalid = false;
            }
        }
        return isvalid;
    }

    /**
     * 从MemCached里获取到缓存数据
     * 
     * @param mcckey
     * @return
     * @time 2015年5月5日 下午5:14:49
     * @author chendong
     */
    private String getpricefrommcc(String mcckey) {
        String resultstring = null;
        try {
            Object tempdata = MemCached.getInstance().get(mcckey);
            resultstring = "-1";
            if (tempdata != null) {
                resultstring = tempdata.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return resultstring;
    }

    private String getpricefrom12306(String from_station_no2, String to_station_no2, String seat_types2, String time2) {
        String json1 = "-1";
        String url1 = "https://kyfw.12306.cn/otn/leftTicket/queryTicketPrice?train_no=" + train_no
                + "&from_station_no=" + from_station_no + "&to_station_no=" + to_station_no + "&seat_types="
                + seat_types + "&train_date=" + time;
        json1 = HttpUtils.Get_https(url1, 4000);
        //        try {
        //            json1 = ZhuanfaUtil.zhuanfa(url1, "", "", "", "get", 2000);
        //        }
        //        catch (Exception e) {
        //            json1 = HttpUtils.Get_https(url1);
        //        }
        return json1;
    }

    public String getpricefromdb(String key) {
        String resultjson = "-1";
        String sqlselect = "SELECT C_PRICE,C_YWPRICE,C_RWPRICE,C_GJRWPRICE FROM T_TRAINPRICE with(nolock) where C_MCCKEY='"
                + key + "'";
        try {
            //            List clist = Server.getInstance().getSystemService().findMapResultBySql(sqlselect, null);
            List clist = getSystemService().findMapResultBySql(sqlselect, null);
            if (clist.size() > 0) {
                Map m = (Map) clist.get(0);
                //                System.out.println(key + ":" + m);
                resultjson = m.get("C_PRICE").toString();
                Object o_ywprice = m.get("C_YWPRICE");
                if (o_ywprice != null && o_ywprice.toString().length() > 0) {
                    ywprice = o_ywprice.toString();
                }
                Object o_rwprice = m.get("C_RWPRICE");
                if (o_rwprice != null && o_rwprice.toString().length() > 0) {
                    rwprice = o_rwprice.toString();
                }
                Object o_gjrwprice = m.get("C_GJRWPRICE");
                if (o_gjrwprice != null && o_gjrwprice.toString().length() > 0) {
                    gjrwprice = o_gjrwprice.toString();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return resultjson;
    }

    public float foamtPrice(String price) {
        float fprice = 0F;
        try {
            price = price.replace("¥", "").replace("?", "").replace("楼", "").replace("￥", "");
            fprice = Float.valueOf(price);
        }
        catch (Exception e) {
        }
        return fprice;
    }

    public void toParsePrice(JSONObject pricejsonobject, String json1, JSONObject jsonobject1, String mcckey,
            int ishaveprice, String zuoxi) {
        try {
            String price = pricejsonobject.getString(zuoxi);
            String pricez = price;
            String pricex = price;
            if (!"-1".equals(ywprice)) {
                String[] ywprices = ywprice.split("/");
                pricex = ywprices[1];
            }
            if (!"-1".equals(rwprice)) {
                String[] ywprices = rwprice.split("/");
                pricez = ywprices[1];
                pricex = ywprices[2];
            }
            if (!"-1".equals(gjrwprice)) {
                String[] ywprices = gjrwprice.split("/");
                pricez = ywprices[1];
                pricex = ywprices[2];
            }
            if (price == null) {
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                json1 = getpricefrom12306(from_station_no, to_station_no, seat_types, time);
                isusecache = 0;//是否用缓存
                try {
                    Date d1 = df.parse(df.format(new Date()));
                    Date d2 = df.parse("24:00:00");
                    long diff = d2.getTime() - d1.getTime();
                    long day = diff / (24 * 60 * 60 * 1000);
                    long hour = (diff / (60 * 60 * 1000) - day * 24);
                    long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                    Date date = new Date(1000 * s * min * hour);//缓存到凌晨00:00释放掉
                    creatAddMemcachedThreadThread(mcckey, json1, date, 2, time);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                jsonobject1 = JSON.parseObject(json1);
                pricejsonobject = (JSONObject) jsonobject1.get("data");
            }
            price = pricejsonobject.getString(zuoxi);
            if ("A9".equals(zuoxi)) {
                train.setSwzprice(foamtPrice(price));
            }
            if ("WZ".equals(zuoxi)) {
                train.setWzprice(foamtPrice(price));
            }
            if ("A1".equals(zuoxi)) {
                train.setYzprice(foamtPrice(price));
            }
            if ("A2".equals(zuoxi)) {
                train.setRzprice(foamtPrice(price));
            }
            if ("O".equals(zuoxi)) {
                train.setRz2price(foamtPrice(price));
            }
            if ("M".equals(zuoxi)) {
                train.setRz1price(foamtPrice(price));
            }
            if ("P".equals(zuoxi)) {
                train.setTdzprice(foamtPrice(price));
            }
            if ("A3".equals(zuoxi)) {
                train.setYwsprice(foamtPrice(price));
                train.setYwzprice(foamtPrice(pricez));
                train.setYwxprice(foamtPrice(pricex));
            }
            if ("A4".equals(zuoxi)) {
                train.setRwsprice(foamtPrice(price));
                train.setRwxprice(foamtPrice(pricex));
            }
            if ("A6".equals(zuoxi)) {
                train.setGwsprice(foamtPrice(price));
                train.setGwxprice(foamtPrice(pricex));
            }
            ishaveprice++;
        }
        catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 线程处理价格数据
     * 
     * @param mcckey
     * @param value
     * @param date
     * @param type //1:存余票缓存,2:存价格信息到数据库和缓存
     * @time 2015年5月5日 下午6:41:37
     * @author chendong
     */
    public void creatAddMemcachedThreadThread(String mcckey, String value, Date date, int type, String time) {
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(1);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        // 将线程放入池中进行执行
        t1 = new AddMemcachedThread(mcckey, value, date, type, time);
        pool.execute(t1);
        pool.shutdown();
    }

    public static ISystemService getSystemService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        String search_12306yupiao_service_url = PropertyUtil.getValue("search_12306yupiao_service_url",
                "Train.properties");
        try {
            return (ISystemService) factory.create(ISystemService.class, search_12306yupiao_service_url
                    + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
