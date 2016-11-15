package com.ccservice.b2b2c.atom.train;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.IAtomService;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.train.data.Wrapper_12306;
import com.ccservice.b2b2c.atom.train.data.Wrapper_tieyou;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MD5Util;
import com.weixin.util.RequestUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 火车票查询接口 
 * 搜可得火车票查选接口
 * @author hanmh
 */
public class TrainHelper extends TrainSupport implements ITrainHelper {

    public final static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private String userid;

    private String password;

    private String ipAddress;

    private String sktrainurl;

    private String yptrainurl;

    private String infotrainurl;

    private String dgtrainurl;

    private final String FROMCTIY = "FromCity";

    private final String TOCITY = "ToCity";

    private final String SDATE = "sDate";

    private final String USERID = "UserID";

    private final String CHECKCODE = "CheckCode";

    /**
     * 余票、价格查询，用于火车票改签退
     * @备注，余票查询接口是30秒超时，请求超时时间暂定为32秒
     */
    public List<Train> queryTrain(String from_station_name, String to_station_name, String date) {
        //三字码
        String from_station = get_station_name_code(from_station_name);
        String to_station = get_station_name_code(to_station_name);
        //码为空
        if (ElongHotelInterfaceUtil.StringIsNull(from_station) || ElongHotelInterfaceUtil.StringIsNull(to_station)) {
            return new ArrayList<Train>();
        }
        else {
            //余票结果
            String result = "";
            //超时时间
            int timeout = 30 * 1000;
            //请求头，暂空
            Map<String, String> header = new HashMap<String, String>();
            //空铁查询余票
            String url = PropertyUtil.getValue("KongTie_url", "Train.properties");
            String key = PropertyUtil.getValue("KongTie_key", "Train.properties");
            String partnerid = PropertyUtil.getValue("KongTie_partnerid", "Train.properties");
            //多次尝试查询
            for (int i = 0; i < 3; i++) {
                try {
                    String reqtime = getreqtime();
                    String method = "train_query";//车票预订页查询
                    String sign = getsign(partnerid, method, reqtime, key);
                    //请求参数
                    JSONObject json = new JSONObject();
                    json.put("sign", sign);
                    json.put("method", method);
                    json.put("reqtime", reqtime);
                    json.put("train_date", date);
                    json.put("needdistance", "0");
                    json.put("partnerid", partnerid);
                    json.put("purpose_codes", "ADULT");
                    json.put("to_station", to_station);
                    json.put("from_station", from_station);
                    //请求
                    result = RequestUtil.post(url, "jsonStr=" + json, "UTF-8", header, timeout);
                    //判断
                    if (!ElongHotelInterfaceUtil.StringIsNull(result) && result.contains("200")) {
                        break;
                    }
                }
                catch (Exception e) {
                }
            }
            //解析并返回结果
            return train(result);
        }
    }

    /**
     * 火车票查询余票和时刻的方法
     * 
     */
    public List<Train> getYPSKUnionTrainList(String startcity, String endcity, String time) {
        long l1 = System.currentTimeMillis();
        List<Train> sktrainlist = new ArrayList<Train>();
        //                sktrainlist = getSKDdata(startcity, endcity, time);
        startcity = get_station_name_code(startcity);
        endcity = get_station_name_code(endcity);
        sktrainlist = Listtrain(startcity, endcity, time);
        if (sktrainlist.size() == 0) {
            FlightSearch param = new FlightSearch();
            param.setTravelType("train_query");
            //            get_station_name_code(startcity), get_station_name_code(endcity), time, param
            WriteLog.write("查询余票", l1 + ":startcity:" + startcity + ":endcity:" + endcity + ":time:" + time);
            startcity = get_station_name_code(startcity);
            endcity = get_station_name_code(endcity);
            sktrainlist = Listtrain(startcity, endcity, time);
            WriteLog.write("查询余票", l1 + ":startcity:" + startcity + ":endcity:" + endcity + ":time:" + time + ":"
                    + "12306----" + sktrainlist.size());
        }

        return sktrainlist;
    }

    public List<Train> Listtrain(String startcity, String endcity, String time) {
        String url = PropertyUtil.getValue("KongTie_url", "Train.properties");
        String partnerid = PropertyUtil.getValue("KongTie_partnerid", "Train.properties");
        String key = PropertyUtil.getValue("KongTie_key", "Train.properties");

        String json = "";
        int i = 0;
        do {
            json = train_query(time, startcity, endcity, partnerid, key, url);
            i++;
        }
        while (json.equals("") && json.indexOf("200") < 0 && i < 2);

        List<Train> sktrainlist = train(json);
        return sktrainlist;
    }

    public String train_query(String train_date, String from_station, String to_station, String partnerid, String key,
            String url) {
        String method = "train_query";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"train_date\":\"" + train_date + "\",\"from_station\":\""
                + from_station + "\",\"to_station\":\"" + to_station
                + "\",\"purpose_codes\":\"ADULT\",\"needdistance\":\"1\"}";
        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "UTF-8").toString();
        return resultString;
    }

    public static String getreqtime() {
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getsign(String partnerid, String method, String reqtime, String key) {
        return MD5Util.MD5Encode(partnerid + method + reqtime + MD5Util.MD5Encode(key, "UTF-8"), "UTF-8");
    }

    public List<Train> train(String json) {
        List<Train> listtrain = new ArrayList<Train>();
        //为空
        if (ElongHotelInterfaceUtil.StringIsNull(json)) {
            return listtrain;
        }
        JSONObject jsonobject = JSON.parseObject(json);
        JSONArray datasArray = jsonobject == null ? new JSONArray() : jsonobject.getJSONArray("data");
        for (int i = 0; datasArray != null && i < datasArray.size(); i++) {
            JSONObject data_index = (JSONObject) datasArray.get(i);
            Train train = new Train();
            String startTime = data_index.getString("start_time");// 出发时间
            String traincode = data_index.getString("train_code");// 车次
            train.setTraincode(traincode);
            String traintype = gettraintype(traincode);// 列车等级
            train.setTraintype(traintype);
            String startCity = data_index.getString("from_station_name");// 出发城市
            train.setStartcity(startCity);
            String from_station_code = data_index.getString("from_station_code");// 出发车站简码
            train.setFrom_station_code(from_station_code);
            train.setStarttime(startTime);
            String endCity = data_index.getString("to_station_name");// 到达城市
            train.setEndcity(endCity);
            String to_station_code = data_index.getString("to_station_code");// 到达车站简码
            train.setTo_station_code(to_station_code);
            String endTime = data_index.getString("arrive_time");// 到达时间
            train.setEndtime(endTime);
            String arrive_days = data_index.getString("arrive_days");// 列车从出发站到达目的站的运行天数
                                                                     // 0:当日到达，1:
                                                                     // 次日到达，2:三日到达，3:四日到达，依此类推
            train.setArrive_days(arrive_days);
            String train_start_date = data_index.getString("train_start_date");// 列车从始发站出发的日期
            train.setTrain_start_date(train_start_date);
            String access_byidcard = data_index.getString("access_byidcard");// 是否可凭二代身份证直接进出站
            train.setAccess_byidcard(access_byidcard);
            String sale_date_time = data_index.getString("sale_date_time");// 车票开售时间
            train.setSale_date_time(sale_date_time);
            train.setTradeno(sale_date_time);
            String can_buy_now = data_index.getString("can_buy_now");// 当前是否可以接受预定（Y:可以，N:不可以）
            train.setCan_buy_now(can_buy_now);
            String train_no = data_index.getString("train_no");// train_no列车号
            train.setTrain_no(train_no);
            String distance = "0";// 站站距离
            String sfz = data_index.getString("start_station_name");// 这列车的始发站
            train.setSfz(sfz);
            train.setStart_station_name(sfz);
            String zdz = data_index.getString("end_station_name");// 这列车的终点站
            train.setZdz(zdz);
            train.setEnd_station_name(zdz);
            try {
                train.setDistance(Float.valueOf(distance));
            }
            catch (Exception e) {
                train.setDistance(0);
            }
            String costTime = data_index.getString("run_time");// 运行时间
            train.setCosttime(costTime);
            String run_time_minute = data_index.getString("run_time_minute");// 历时分钟合计
            train.setRun_time_minute(run_time_minute);
            String wzyp = data_index.getString("wz_num");// 无座余票
            wzyp = chuliyupiao(wzyp);// 处理 余票数量是有的问题
            train.setWzyp(wzyp);
            String yzyp = data_index.getString("yz_num");// 硬座余票
            // String s_yzyp = getyzyp(wzyp, yzyp);
            yzyp = chuliyupiao(yzyp);// 处理 余票数量是有的问题
            train.setYzyp(yzyp);
            String rzyp = data_index.getString("rz_num");// 软座余票
            rzyp = chuliyupiao(rzyp);
            train.setRzyp(rzyp);
            String rz2yp = data_index.getString("edz_num");// 二等软座余票
            rz2yp = chuliyupiao(rz2yp);
            train.setRz2yp(rz2yp);
            String rz1yp = data_index.getString("ydz_num");// 一等软余票
            rz1yp = chuliyupiao(rz1yp);
            train.setRz1yp(rz1yp);
            String tdzyp = data_index.getString("tdz_num");// 特等座余票
            tdzyp = chuliyupiao(tdzyp);
            train.setTdzyp(tdzyp);
            String swzyp = data_index.getString("swz_num");// 商务座余票
            swzyp = chuliyupiao(swzyp);
            train.setSwzyp(swzyp);
            String ywyp = data_index.getString("yw_num");// 硬卧余票
            ywyp = chuliyupiao(ywyp);
            train.setYwyp(ywyp);
            String rwyp = data_index.getString("rw_num");// 软卧余票
            rwyp = chuliyupiao(rwyp);
            train.setRwyp(rwyp);
            String gwyp = data_index.getString("gjrw_num");// 高级软卧余票
            gwyp = chuliyupiao(gwyp);
            train.setGwyp(gwyp);
            String qtxb_num = data_index.getString("qtxb_num");// 其他席别余票数量
            qtxb_num = chuliyupiao(qtxb_num);
            train.setQtxb_num(qtxb_num);
            String memo = data_index.getString("note");
            train.setMemo(memo);
            Float yz_price = Float.parseFloat(data_index.getString("yz_price"));
            train.setYzprice(yz_price);
            Float swz_price = Float.parseFloat(data_index.getString("swz_price"));
            train.setSwzprice(swz_price);
            Float wz_price = Float.parseFloat(data_index.getString("wz_price"));
            train.setWzprice(wz_price);
            Float tdz_price = Float.parseFloat(data_index.getString("tdz_price"));
            train.setTdzprice(tdz_price);
            Float qtxb_price = Float.parseFloat(data_index.getString("qtxb_price"));
            train.setQtxb_price(qtxb_price);
            Float yw_price = Float.parseFloat(data_index.getString("yw_price"));
            train.setYwsprice(yw_price);
            Float ywz_price = Float.parseFloat(data_index.getString("ywz_price"));
            train.setYwzprice(ywz_price);
            Float ywx_price = Float.parseFloat(data_index.getString("ywx_price"));
            train.setYwxprice(ywx_price);
            Float rw_price = Float.parseFloat(data_index.getString("rw_price"));
            train.setRwsprice(rw_price);
            Float rwx_price = Float.parseFloat(data_index.getString("rwx_price"));
            train.setRwxprice(rwx_price);
            Float gjrw_price = Float.parseFloat(data_index.getString("gjrw_price"));
            train.setGwsprice(gjrw_price);
            Float edz_price = Float.parseFloat(data_index.getString("edz_price"));
            train.setRz2price(edz_price);
            Float ydz_price = Float.parseFloat(data_index.getString("ydz_price"));
            train.setRz1price(ydz_price);
            Float rz_price = Float.parseFloat(data_index.getString("rz_price"));
            train.setRzprice(rz_price);
            listtrain.add(train);
        }
        return listtrain;
    }

    private String chuliyupiao(String s_yzyp) {
        if ("有".equals(s_yzyp.trim())) {
            s_yzyp = (new Random().nextInt(100) + 100) + "";
        }
        if ("无".equals(s_yzyp.trim())) {
            s_yzyp = "0";
        }
        return s_yzyp;
    }

    private String gettraintype(String traincode) {
        // String traintype = "普快";
        // if (traincode != null && traincode.length() > 0) {
        // if (traincode.startsWith("G") || traincode.startsWith("C")) {
        // traintype = "高铁/城际";
        // }
        // else if (traincode.startsWith("D")) {
        // traintype = "动车";
        // }
        // else if (traincode.startsWith("Z")) {
        // traintype = "直达";
        // }
        // else if (traincode.startsWith("T")) {
        // traintype = "特快";
        // }
        // else if (traincode.startsWith("K")) {
        // traintype = "快速";
        // }
        // }
        return traincode.substring(0, 1);
    }

    /**
     * 铁友
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @return
     * @time 2014年12月5日 上午11:04:27
     * @author chendong
     */
    private List<Train> gettieyoudata(String startcity, String endcity, String time) {
        List<Train> sktrainlist = new ArrayList<Train>();
        Wrapper_tieyou wrapper_tieyou = new Wrapper_tieyou();
        FlightSearch param = new FlightSearch();
        try {
            String html = "";
            if (Server.getInstance().getDateHashMap().get("tieyouhtml") != null) {
                html = Server.getInstance().getDateHashMap().get("tieyouhtml");
            }
            else {
                html = wrapper_tieyou.getHtml(startcity, endcity, time, param);
                Server.getInstance().getDateHashMap().put("tieyouhtml", html);
            }
            sktrainlist = wrapper_tieyou.process(html, startcity, endcity, time, param);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sktrainlist;
    }

    /**
     * 12306
     * 
     * @param startcity 出发三字码
     * @param endcity   到达三字码
     * @param time  时间  格式2014-12-11
     * @param param param.getTravelType() == "train_query" 才查询价格否则只查询时刻
     * @return
     * @time 2014年12月5日 上午11:04:35
     * @author chendong
     */
    private List<Train> get12306data(String startcity, String endcity, String time, FlightSearch param) {
        List<Train> sktrainlist = new ArrayList<Train>();
        Wrapper_12306 wrapper_12306 = new Wrapper_12306();
        //        FlightSearch param = new FlightSearch();
        try {
            sktrainlist = wrapper_12306.process("", startcity, endcity, time, param);
        }
        catch (Exception e) {
        }
        return sktrainlist;
    }

    public IAtomService get12306Train_AtomService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        String search_12306yupiao_service_url = PropertyUtil.getValue("search_12306yupiao_cn_interface_url",
                "Train.properties");
        try {
            return (IAtomService) factory.create(IAtomService.class, search_12306yupiao_service_url
                    + IAtomService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 搜可得的火车票查询接口
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @return
     * @time 2014年12月3日 上午11:32:51
     * @author chendong
     */
    private List<Train> getSKDdata(String startcity, String endcity, String time) {
        List<Train> sktrainlist = this.getSKTrainList(startcity, endcity, time);//时刻
        List<Train> yptrainlist = this.getYPTrainList(startcity, endcity, time);//余票
        for (Train train : sktrainlist) {
            for (int i = 0; i < yptrainlist.size(); i++) {
                Train yptrain = yptrainlist.get(i);
                if (train.getTraincode().equals(yptrain.getTraincode())) {
                    train.setYzyp(yptrain.getYzyp());
                    train.setYwyp(yptrain.getYwyp());
                    train.setRzyp(yptrain.getRzyp());
                    train.setTdzyp(yptrain.getTdzyp());
                    train.setRz1yp(yptrain.getRz1yp());
                    train.setRz2yp(yptrain.getRz2yp());
                    train.setSwzyp(yptrain.getSwzyp());
                    train.setRwyp(yptrain.getRwyp());
                    train.setGwyp(yptrain.getGwyp());
                    train.setWzyp(yptrain.getWzyp());
                    yptrainlist.remove(i);
                    break;
                }
            }
        }
        return sktrainlist;
    }

    public List<Train> getTrainlistFromInterface(String urlstr, String time) {
        long l1 = System.currentTimeMillis();
        int randominti = new Random().nextInt(10000000);
        List<Train> listtrain = new ArrayList<Train>();
        try {
            //            HttpURLConnection connection = this.openConnection(urlstr);
            //            SAXReader reader = new SAXReader();
            //            Document document = reader.read(connection.getInputStream());
            String s = SendPostandGet2.doGet(urlstr, "GBK");
            WriteLog.write("火车票查询_SKD", randominti + ":" + (System.currentTimeMillis() - l1) + ":返回结果:" + s + urlstr);
            if (null != s && !"".equals(s)) {
                Document document = DocumentHelper.parseText(s);
                Element root = document.getRootElement();
                String code = root.elementTextTrim("Code");
                if (code.trim().equals("1")) {
                    Iterator<Element> elementlist = root.elementIterator("Data");
                    while (elementlist.hasNext()) {
                        Train train = new Train();
                        Element element = elementlist.next();
                        String startTime = element.elementTextTrim("StartTime");// 出发时间
                        try {
                            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                            Date cttime = ft.parse(time);
                            Date nowdate = ft.parse(TimeUtil.gettodaydate(1));
                            if (cttime.getDate() == nowdate.getDate()) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                String stime = time + " " + startTime;
                                Date begintime = sdf.parse(stime);
                                Date endtime = sdf.parse(addtime(1));
                                if (begintime.getTime() < endtime.getTime()) {
                                    continue;
                                }
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        String id = element.elementTextTrim("ID");// 排列序号
                        String traincode = element.elementTextTrim("TrainCode");// 车次
                        train.setTraincode(traincode);
                        String traintype = element.elementTextTrim("TrainType");// 列车等级
                        train.setTraintype(traintype);
                        String startCity = element.elementTextTrim("StartCity");// 出发城市
                        train.setStartcity(startCity);

                        train.setStarttime(startTime);
                        String endCity = element.elementTextTrim("EndCity");// 出发城市
                        train.setEndcity(endCity);
                        String endTime = element.elementTextTrim("EndTime");// 到达城市
                        train.setEndtime(endTime);
                        String distance = element.elementTextTrim("Distance");// 站站距离
                        String sfz = element.elementTextTrim("SFZ");
                        train.setSfz(sfz);
                        String zdz = element.elementTextTrim("ZDZ");
                        train.setZdz(zdz);
                        try {
                            train.setDistance(Float.valueOf(distance));
                        }
                        catch (Exception e) {
                            train.setDistance(0);
                        }

                        String costTime = element.elementTextTrim("CostTime");// 运行时间
                        train.setCosttime(costTime);
                        String yz = element.elementTextTrim("YZ");// 硬座票价
                        train.setYzprice(foamtPrice(yz));
                        String rz = element.elementTextTrim("RZ");// 软座票价
                        train.setRzprice(foamtPrice(rz));

                        String rz2 = element.elementTextTrim("RZ2");// 二等软座票价
                        train.setRz2price(foamtPrice(rz2));

                        String rz1 = element.elementTextTrim("RZ1");// 一等软座票价
                        train.setRz1price(foamtPrice(rz1));

                        String tdz = element.elementTextTrim("TDZ");// 特等软座票价
                        train.setTdzprice(foamtPrice(tdz));

                        String swxz = element.elementTextTrim("SWZ");// 商务座
                        train.setSwzprice(foamtPrice(swxz));

                        String yws = element.elementTextTrim("YWS");// 硬卧上铺票价
                        train.setYwsprice(foamtPrice(yws));

                        String ywz = element.elementTextTrim("YWZ");// 硬卧中铺票价
                        train.setYwzprice(foamtPrice(ywz));

                        String ywx = element.elementTextTrim("YWX");// 硬卧下铺票价
                        train.setYwxprice(foamtPrice(ywx));

                        String rws = element.elementTextTrim("RWS");// 软卧上票价
                        train.setRwsprice(foamtPrice(rws));

                        String rwx = element.elementTextTrim("RWX");// 软卧下票价
                        train.setRwxprice(foamtPrice(rwx));

                        String gws = element.elementTextTrim("GWS");// 高级软卧上票价
                        train.setGwsprice(foamtPrice(gws));

                        String gwx = element.elementTextTrim("GWX");// 高级软卧下票价
                        train.setGwxprice(foamtPrice(gwx));
                        // 以下信息为余票和代购信息
                        String overNight = element.elementTextTrim("OverNight");// 隔夜天数
                        String priceCode = element.elementTextTrim("PriceCode");// 票价浮动率
                        String seatCode = element.elementTextTrim("SeatCode");// 座位类型参数
                        String yzyp = element.elementTextTrim("YZ_YP");// 硬座余票
                        train.setYzyp(yzyp);
                        String rzyp = element.elementTextTrim("RZ_YP");// ruanzuo余票
                        train.setRzyp(rzyp);
                        String rz2yp = element.elementTextTrim("RZ2_YP");// 二等软座余票
                        train.setRz2yp(rz2yp);
                        String rz1yp = element.elementTextTrim("RZ1_YP");// 一等软余票
                        train.setRz1yp(rz1yp);
                        String tdzyp = element.elementTextTrim("TDZ_YP");// 特等座余票
                        train.setTdzyp(tdzyp);
                        String swzyp = element.elementTextTrim("SWZ_YP");// 商务座余票
                        train.setSwzyp(swzyp);
                        String ywyp = element.elementTextTrim("YW_YP");// 硬卧余票
                        train.setYwyp(ywyp);
                        String rwyp = element.elementTextTrim("RW_YP");// 软卧余票
                        train.setRwyp(rwyp);
                        String gwyp = element.elementTextTrim("GW_YP");// 高级软卧余票
                        train.setGwyp(gwyp);
                        String wzyp = element.elementTextTrim("WZ_YP");// 站票余票
                        train.setWzyp(wzyp);
                        listtrain.add(train);

                    }
                }
            }
        }
        catch (DocumentException e1) {
            e1.printStackTrace();
        }
        return listtrain;
    }

    /**
     * 火车列车时刻信息
     * 
     * @param startcity
     * @param edncity
     * @param time
     * @return
     */
    public List<Train> getSKTrainList(String startcity, String edncity, String time) {
        String urlstr = this.getIpAddress() + "/" + this.getSktrainurl() + "?" + this.FROMCTIY + "=" + startcity + "&"
                + TOCITY + "=" + edncity + "&" + this.SDATE + "=" + time + "&" + USERID + "=" + getUserid() + "&"
                + this.CHECKCODE + "=" + this.getPassword();
        return getTrainlistFromInterface(urlstr, time);

    }

    /**
     * 火车余票信息
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @return
     */
    public List<Train> getYPTrainList(String startcity, String endcity, String time) {
        String urlstr = this.getIpAddress() + "/" + this.getYptrainurl() + "?" + this.FROMCTIY + "=" + startcity + "&"
                + TOCITY + "=" + endcity + "&" + this.SDATE + "=" + time + "&" + USERID + "=" + getUserid() + "&"
                + this.CHECKCODE + "=" + this.getPassword();
        return getTrainlistFromInterface(urlstr, time);
    }

    /**
     * 代购时刻表
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @return
     */
    public List<Train> getDGTrainList(String startcity, String endcity, String time) {
        if (true) {
            List<Train> trains = this.getYPSKUnionTrainList(startcity, endcity, time);
            if (trains.size() == 0) {
                trains = new ArrayList<Train>();
                FlightSearch param = new FlightSearch();
                param.setGeneral(2);
                trains = get12306data(startcity, endcity, time, param);
            }
            return trains;
        }
        String urlstr = this.getIpAddress() + "/" + this.getDgtrainurl() + "?" + this.FROMCTIY + "=" + startcity + "&"
                + TOCITY + "=" + endcity + "&" + this.SDATE + "=" + time + "&" + USERID + "=" + getUserid() + "&"
                + this.CHECKCODE + "=" + this.getPassword();
        return getTrainlistFromInterface(urlstr, time);
    }

    /**
     * 列车详细信息
     * 
     * @param traincode
     * @param startcity
     * @param endcity
     * @param time
     * @return
     */
    public List<Map<String, String>> getTraininfo(String traincode, String time) {
        long l1 = System.currentTimeMillis();
        int randominti = new Random().nextInt(10000000);
        String urlstr = this.getIpAddress() + "/" + this.getInfotrainurl() + "?" + "TrainCode=" + traincode + "&"
                + this.SDATE + "=" + time + "&" + USERID + "=" + getUserid() + "&" + this.CHECKCODE + "="
                + this.getPassword();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HttpURLConnection connection = this.openConnection(urlstr);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(connection.getInputStream());
            Element root = document.getRootElement();
            String code = root.elementTextTrim("Code");
            WriteLog.write("火车票查询_SKD", randominti + ":" + (System.currentTimeMillis() - l1) + ":详情请求地址:getTraininfo:"
                    + urlstr);
            if (code.trim().equals("1")) {
                Iterator<Element> elementiteraotr = root.elementIterator("Data");
                while (elementiteraotr.hasNext()) {
                    Map<String, String> map = new HashMap();
                    Element element = elementiteraotr.next();
                    String triancode = element.elementTextTrim("TrainCode");
                    map.put("traincode", triancode);
                    String sname = element.elementTextTrim("SName");
                    map.put("sname", sname);
                    String sno = element.elementTextTrim("SNo");
                    map.put("sno", sno);
                    String arrtime = element.elementTextTrim("ArrTime");
                    map.put("arrtime", arrtime);
                    String gotime = element.elementTextTrim("GoTime");
                    map.put("gotime", gotime);
                    String costtime = element.elementTextTrim("CostTime");
                    map.put("costtime", costtime);
                    String distance = element.elementTextTrim("Distance");
                    map.put("distance", distance);
                    String yz = element.elementTextTrim("YZ");
                    map.put("yz", yz);
                    String rz = element.elementTextTrim("RZ");
                    map.put("rz", rz);
                    String rz2 = element.elementTextTrim("RZ2");
                    map.put("rz2", rz2);
                    String rz1 = element.elementTextTrim("RZ1");
                    map.put("rz1", rz1);
                    String swz = element.elementTextTrim("SWZ");
                    map.put("swz", swz);
                    String tdz = element.elementTextTrim("TDZ");
                    map.put("tdz", tdz);
                    String yws = element.elementTextTrim("YWS");
                    map.put("yws", yws);
                    String ywz = element.elementTextTrim("YWZ");
                    map.put("ywz", ywz);
                    String ywx = element.elementTextTrim("YWX");
                    map.put("ywx", ywx);
                    String rws = element.elementTextTrim("RWS");
                    map.put("rws", rws);
                    String rwx = element.elementTextTrim("RWX");
                    map.put("rwx", rwx);
                    String gws = element.elementTextTrim("GWS");
                    map.put("gws", gws);
                    String gwx = element.elementTextTrim("GWX");
                    map.put("gwx", gwx);
                    map.put("residencetime", new XTTrainHelper().residencetime(arrtime, gotime));
                    list.add(map);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取火车票分润资料
     * 
     * @param train
     * @return
     */
    public Map<Customeragent, Float> getAgentroyalty(Train train) {
        return null;
    }

    /**
     * @param train
     *            创建火车票分润记录
     */
    public void createTrainrebate(Train train) {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSktrainurl() {
        return sktrainurl;
    }

    public void setSktrainurl(String sktrainurl) {
        this.sktrainurl = sktrainurl;
    }

    public String getYptrainurl() {
        return yptrainurl;
    }

    public void setYptrainurl(String yptrainurl) {
        this.yptrainurl = yptrainurl;
    }

    public String getInfotrainurl() {
        return infotrainurl;
    }

    public void setInfotrainurl(String infotrainurl) {
        this.infotrainurl = infotrainurl;
    }

    public String getDgtrainurl() {
        return dgtrainurl;
    }

    public void setDgtrainurl(String dgtrainurl) {
        this.dgtrainurl = dgtrainurl;
    }

    @Override
    public List<Train> getYPTrainlistFromInterface(String urlstr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNetworkOrder(Train train, Trainpassenger passenger) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getCityinfo(String cityName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getcityProv(String cityProv) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 12306
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @param param param.getTravelType() == "train_query" 才查询价格否则只查询时刻
     * @return
     * @time 2014年12月5日 上午11:04:35
     * @author chendong
     */
    public List<Train> getDGTrainListcache(String fromcity, String tocity, String date, FlightSearch param) {
        List<Train> trainlist = new ArrayList<Train>();
        if (MemCached.getInstance().get("trainlist" + fromcity + tocity + date) != null && param.getGeneral() != 2) {
            try {
                trainlist = (List<Train>) MemCached.getInstance().get("trainlist" + fromcity + tocity + date);
            }
            catch (Exception e) {
                trainlist = get12306data(fromcity, tocity, date, param);
            }
        }
        else {
            trainlist = get12306data(fromcity, tocity, date, param);
        }

        return trainlist;
    }
}
