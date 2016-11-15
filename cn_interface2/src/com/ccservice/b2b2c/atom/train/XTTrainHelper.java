package com.ccservice.b2b2c.atom.train;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

/**
 * @author Administrator
 *
 */
public class XTTrainHelper extends TrainSupport implements ITrainHelper {

    private String ipAddress;

    private String sktrainurl;

    private String method;

    private String channel;

    private String count;

    private final String FROMCTIY = "from";

    private final String TOCITY = "to";

    private final String SDATE = "departureDate";

    private final String CITYNAME = "cityName";

    private final String SEATMETHOD = "selTrainInfoSeatNumber";

    private final String MONTH = "month";

    private final String DAY = "day";

    private final String SELTRAINBYTNNO = "selTrainByTnNo";

    private final String TN_NO = "tn_no";

    private final String CITYMETHOD = "selDictCityByCityName";

    private final String PROVMETHOD = "selDictCityByCityProv";

    private final String CITYPROV = "cityProv";

    private final String SAVERDERMETHOD = "saveOrder";

    @Override
    public List<Train> getTrainlistFromInterface(String urlstr, String time) {
        System.out.println(urlstr);
        List<Train> listtrain = new ArrayList<Train>();
        try {
            HttpURLConnection connection = this.openConnection(urlstr);
            System.out.println(connection);
            SAXReader reader = new SAXReader();

            Document document = reader.read(connection.getInputStream());
            Element root = document.getRootElement();
            String ord_on_duty = root.elementTextTrim("ord_on_duty");
            String ord_off_duty = root.elementTextTrim("ord_off_duty");

            Iterator<Element> elementlist = root.elementIterator("train_info");
            while (elementlist.hasNext()) {
                Train train = new Train();
                Element element = elementlist.next();
                String sta_tn_depart = element.elementTextTrim("sta_tn_depart");
                String type_code = element.elementTextTrim("type_code");
                String type_name = element.elementTextTrim("type_name");
                String sta_depart = element.elementTextTrim("sta_depart");
                String sta_arrive = element.elementTextTrim("sta_arrive");
                String tn_sta_depart = element.elementTextTrim("tn_sta_depart");
                String tn_sta_arrive = element.elementTextTrim("tn_sta_arrive");
                String sta_time_depart = element.elementTextTrim("sta_time_depart");
                String sta_time_arrive = element.elementTextTrim("sta_time_arrive");
                String tn_time_depart = element.elementTextTrim("tn_time_depart");
                String tn_time_arrive = element.elementTextTrim("tn_time_arrive");
                String tn_air_con = element.elementTextTrim("tn_air_con");
                String sta_days = element.elementTextTrim("sta_days");
                String sta_duration = element.elementTextTrim("sta_duration");
                String sta_dist = element.elementTextTrim("sta_dist");
                String sta_is_start = element.elementTextTrim("sta_is_start");
                String presell = element.elementTextTrim("presell");
                String presell_min = element.elementTextTrim("presell_min");
                String presell_max = element.elementTextTrim("presell_max");

                Element tik_price = element.element("tik_price_info");
                List<Element> seat_list = tik_price.elements("seat");
                Iterator it = seat_list.iterator();
                while (it.hasNext()) {
                    Element ik_price_info = (Element) it.next();
                    String name = ik_price_info.elementTextTrim("name");
                    String typeCode = ik_price_info.elementTextTrim("typeCode");
                    String unitPrice = ik_price_info.elementTextTrim("unitPrice");

                    float price = Float.valueOf(opreationPrice(unitPrice));
                    if ("硬座".equals(name)) {
                        train.setYzprice(price);
                    }
                    else if ("软座".equals(name)) {
                        train.setRzprice(price);
                    }
                    else if ("二等软座".equals(name)) {
                        train.setRz2price(price);
                    }
                    else if ("一等软座".equals(name)) {
                        train.setRz1price(price);
                    }
                    else if ("硬卧上".equals(name)) {
                        train.setYwsprice(price);
                    }
                    else if ("硬卧下".equals(name)) {
                        train.setYwxprice(price);
                    }
                    else if ("硬卧中".equals(name)) {
                        train.setYwzprice(price);
                    }
                    else if ("软卧上".equals(name)) {
                        train.setRwsprice(price);
                    }
                    else if ("软卧下".equals(name)) {
                        train.setRwxprice(price);
                    }
                    else if ("高级软卧上".equals(name)) {
                        train.setGwsprice(price);
                    }
                    else if ("高级软卧下".equals(name)) {
                        train.setGwxprice(price);
                    }
                    String available = ik_price_info.elementTextTrim("available");
                    String limited = ik_price_info.elementTextTrim("limited");
                }

                train.setTraincode(sta_tn_depart);
                //train.setTraintype(type_code);
                train.setTraintype(type_name);
                train.setStartcity(sta_depart);
                train.setEndcity(sta_arrive);
                train.setSfz(tn_sta_depart);
                train.setZdz(tn_sta_arrive);

                sta_time_depart = sta_time_depart.substring(0, 2) + ":" + sta_time_depart.substring(2);
                sta_time_arrive = sta_time_arrive.substring(0, 2) + ":" + sta_time_arrive.substring(2);
                try {
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                    Date cttime = ft.parse(time);
                    Date nowdate = ft.parse(GetNowDate());
                    if (cttime.getDate() == nowdate.getDate()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String stime = time + " " + sta_time_depart;
                        Date begintime = sdf.parse(stime);
                        Date endtime = sdf.parse(addtime());
                        if (begintime.getTime() < endtime.getTime()) {
                            continue;
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                train.setStarttime(sta_time_depart);
                train.setEndtime(sta_time_arrive);
                train.setCosttime(opreationTime(sta_duration));
                try {
                    train.setDistance(Float.valueOf(sta_dist));
                }
                catch (Exception e) {
                    train.setDistance(0);
                }
                listtrain.add(train);
            }
        }
        catch (Exception e) {
            System.out.println("火车票接口异常" + e.getMessage());
        }
        return listtrain;
    }

    /**
     * 日期小时加2
     * @return
     */
    public String addtime() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); //设置当前日期  
        c.add(Calendar.HOUR, 2); //日期小时加2,Calendar.DATE(天),Calendar.HOUR(小时)  
        Date date = c.getTime(); //结果
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(date);
    }

    /**
     * 得到当前时间
     * @return
     */
    public String GetNowDate() {
        String temp_str = "";
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        temp_str = sdf.format(dt);
        return temp_str;
    }

    /**
     * 火车列车时刻信息
     * @param startcity
     * @param edncity
     * @param time
     * @return
     */
    @Override
    public List<Train> getSKTrainList(String startcity, String endcity, String time) {
        String urlstr = "";
        try {
            urlstr = this.getIpAddress() + "/" + this.getSktrainurl() + "?method=" + method + "&" + this.SDATE + "="
                    + time + "&" + this.FROMCTIY + "=" + URLEncoder.encode(startcity, "UTF-8") + "&" + TOCITY + "="
                    + URLEncoder.encode(endcity, "UTF-8") + "&" + this.CITYNAME + "="
                    + URLEncoder.encode(startcity, "UTF-8") + "&channel=" + channel + "&count=" + count;
            System.out.println(urlstr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return getTrainlistFromInterface(urlstr, time);
    }

    /**
     * 火车票余票信息
     */
    @Override
    public List<Train> getYPTrainList(String startcity, String endcity, String time) {

        String timestr = time.substring(5);
        String[] times = timestr.split("-");
        String month = times[0];
        String day = times[1];
        String urlstr = "";
        try {
            urlstr = this.getIpAddress() + "/" + this.getSktrainurl() + "?method=" + this.SEATMETHOD + "&" + this.MONTH
                    + "=" + month + "&" + this.DAY + "=" + day + "&" + this.FROMCTIY + "="
                    + URLEncoder.encode(startcity, "UTF-8") + "&" + TOCITY + "=" + URLEncoder.encode(endcity, "UTF-8");
            System.out.println(urlstr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // http://118.186.246.152:8080/trainWeb/trainServlet?method=selTrainInfoSeatNumber&month=03&day=16&from=%E5%8C%97%E4%BA%AC&to=%E4%B8%8A%E6%B5%B7
        return getYPTrainlistFromInterface(urlstr);
    }

    @Override
    public List<Train> getDGTrainList(String startcity, String endcity, String time) {
        List<Train> yptrainlist = this.getYPTrainList(startcity, endcity, time);
        List<Train> sktrainlist = this.getSKTrainList(startcity, endcity, time);
        System.out.println("开始合并：");
        for (Train train : sktrainlist) {
            for (int i = 0; i < yptrainlist.size(); i++) {
                Train yptrain = yptrainlist.get(i);
                if (train.getTraincode().equals(yptrain.getTraincode())) {
                    train.setYzyp(yptrain.getYzyp());
                    train.setYwyp(yptrain.getYwyp());
                    train.setRzyp(yptrain.getRzyp());
                    train.setRz1yp(yptrain.getRz1yp());
                    train.setRz2yp(yptrain.getRz2yp());
                    train.setRwyp(yptrain.getRwyp());
                    train.setGwyp(yptrain.getGwyp());
                    train.setWzyp(yptrain.getWzyp());
                    yptrainlist.remove(i);
                    break;
                }
            }
        }
        System.out.println("合并OVER");
        return sktrainlist;
    }

    @Override
    public List<Train> getYPSKUnionTrainList(String startcity, String endcity, String time) {
        List<Train> yptrainlist = this.getYPTrainList(startcity, endcity, time);
        List<Train> sktrainlist = this.getSKTrainList(startcity, endcity, time);
        System.out.println("开始合并：");
        for (Train train : sktrainlist) {
            for (int i = 0; i < yptrainlist.size(); i++) {
                Train yptrain = yptrainlist.get(i);
                System.out.println(train.getTraincode() + ":" + yptrain.getTraincode());
                if (train.getTraincode().equals(yptrain.getTraincode())) {
                    train.setYzyp(yptrain.getYzyp());
                    train.setYwyp(yptrain.getYwyp());
                    train.setRzyp(yptrain.getRzyp());
                    train.setRz1yp(yptrain.getRz1yp());
                    train.setRz2yp(yptrain.getRz2yp());
                    train.setRwyp(yptrain.getRwyp());
                    train.setGwyp(yptrain.getGwyp());
                    train.setWzyp(yptrain.getWzyp());
                    yptrainlist.remove(i);
                    break;
                }
            }
        }
        System.out.println("合并OVER");
        return sktrainlist;
    }

    /**
     * 列车详细信息
     */
    @Override
    public List<Map<String, String>> getTraininfo(String traincode, String time) {
        String urlstr = this.getIpAddress() + "/" + this.getSktrainurl() + "?method=" + this.SELTRAINBYTNNO + "&"
                + this.TN_NO + "=" + traincode;
        System.out.println(urlstr);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HttpURLConnection connection = this.openConnection(urlstr);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(connection.getInputStream());
            Element root = document.getRootElement();
            String error_code = root.elementTextTrim("error_code");

            if (error_code.trim().equals("111111")) {

                Iterator<Element> elemendict_station = root.elementIterator("dict_station");
                while (elemendict_station.hasNext()) {
                    Element element = elemendict_station.next();
                    String sno = element.elementTextTrim("sta_number");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("sno", sno);
                    String sname = element.elementTextTrim("sta_arrive");
                    map.put("sname", sname);
                    String gotime = element.elementTextTrim("sta_time_depart");
                    if (gotime.equals("0")) {
                        map.put("gotime", "0");
                    }
                    else {
                        gotime = gotime.substring(0, 2) + ":" + gotime.substring(2);
                        map.put("gotime", gotime);
                    }
                    String arrtime = element.elementTextTrim("sta_time_arrive");
                    if (arrtime.equals("0")) {
                        map.put("arrtime", arrtime);
                    }
                    else {
                        arrtime = arrtime.substring(0, 2) + ":" + arrtime.substring(2);
                        map.put("arrtime", arrtime);
                    }
                    String sta_days = element.elementTextTrim("sta_days");
                    map.put("sta_days", sta_days);
                    String costtime = element.elementTextTrim("sta_duration");
                    map.put("costtime", opreationTime(costtime));
                    String distance = element.elementTextTrim("sta_dist");
                    map.put("distance", distance);
                    String tik_price_info = element.elementTextTrim("tik_price_info");
                    if (tik_price_info.equals("0")) {
                        map.put("yz", "-");
                        map.put("yws", "-");
                        map.put("ywz", "-");
                        map.put("ywx", "-");
                        map.put("rws", "-");
                        map.put("rwx", "-");
                    }
                    else {
                        System.out.println(tik_price_info);
                        String[] tiks = tik_price_info.split("\\|");
                        for (String tik : tiks) {
                            String[] pinfo = tik.split(":");
                            int code = Integer.valueOf(pinfo[0]);
                            String price = pinfo[1];
                            switch (code) {
                            case 101:
                                map.put("yz", opreationPrice(price));
                                break;
                            case 301:
                                map.put("yws", opreationPrice(price));
                                break;
                            case 302:
                                map.put("ywz", opreationPrice(price));
                                break;
                            case 303:
                                map.put("ywx", opreationPrice(price));
                                break;
                            case 401:
                                map.put("rws", opreationPrice(price));
                                break;
                            case 402:
                                map.put("rwx", opreationPrice(price));
                                break;
                            }
                        }
                    }
                    map.put("residencetime", residencetime(arrtime, gotime));
                    if (map.get("yz") == null) {
                        map.put("yz", "-");
                    }
                    if (map.get("yws") == null) {
                        map.put("yws", "-");
                    }
                    if (map.get("ywz") == null) {
                        map.put("ywz", "-");
                    }
                    if (map.get("ywx") == null) {
                        map.put("ywx", "-");
                    }
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
     * 网络订单
     */
    @Override
    public String getNetworkOrder(Train train, Trainpassenger passenger) {
        String error_msg = "";

        /*	String Channel="tenpay001";
        	String usrId=String.valueOf(train.getCreateuid());
        	String frt_recev_cert_id=String.valueOf(passenger.getIdtype());
        	String frt_recev_cert_no=passenger.getIdnumber();
        	String ordExtNum="";
        	String ordPaymentNo="";
        	String xmlType="4";
        	String max_time="";
        	
        	String requestMemo="";
        	String tikAddr="";
        	String sendMemo="";
        	String tikMemo=train.getAcceptseat();
        	
        	String isChange="";
        	String frt_type_code="";
        	String rt_recev_nam=passenger.getName();
        	String frt_recev_link="";
        	String frt_recev_mobile="";
        	String frt_recev_phone="";
        	String frtRecevAddrProvince=train.getStartcity();
        	String cityID="";
        	String cityName ="";
        	String distID="";
        	String disName="";
        	String road="";
        	String lane="";
        	String no="";
        	String roomNo="";
        	String detailAddr="";
        	String zipcode="";
        	String tikDay="";
        	String tikTime="";
        	String sendRegn="";
        	String frt_carriage_fee="";
        	String priceTtlAmt="";
        	String priceArtl="";
        	String priceServ="";
        	String pricePrepaid="";
        	String priceArtlCnt="";
        	String tik_group_code="0";
        	String tik_depart_time=train.getStarttime();
        	String trainNumber=train.getTraincode();
        	String departureStation=train.getStartcity();
        	String arrivalStation=train.getEndcity();
        	String tn_sta_depart=train.getStartcity();
        	String tn_sta_arrive=train.getEndcity();
        	String tik_seat_type_code="";
        	String tik_price="";
        	String piece=String.valueOf(train.getCount());
        	String tikCertInfo="";
        	String orderxml = "";
        	orderxml = appendParam(orderxml, "Channel", Channel);
        	orderxml = appendParam(orderxml, "usrId", usrId);
        	orderxml = appendParam(orderxml, "frt_recev_cert_id", frt_recev_cert_id);
        	orderxml = appendParam(orderxml, "frt_recev_cert_no", frt_recev_cert_no);
        	orderxml = appendParam(orderxml, "ordExtNum", ordExtNum);
        	orderxml = appendParam(orderxml, "ordPaymentNo", ordPaymentNo);
        	orderxml = appendParam(orderxml, "xmlType", xmlType);
        	orderxml = appendParam(orderxml, "max_time", max_time);
        	orderxml = appendParam(orderxml, "requestMemo", requestMemo);
        	orderxml = appendParam(orderxml, "tikAddr", requestMemo);
        	orderxml = appendParam(orderxml, "requestMemo", tikAddr);
        	orderxml = appendParam(orderxml, "sendMemo", sendMemo);
        	orderxml = appendParam(orderxml, "", tikMemo);
        	orderxml = appendParam(orderxml, "isChange", isChange);
        	orderxml = appendParam(orderxml, "frt_type_code", frt_type_code);
        	orderxml = appendParam(orderxml, "rt_recev_nam", rt_recev_nam);
        	orderxml = appendParam(orderxml, "frt_recev_link", frt_recev_link);
        	orderxml = appendParam(orderxml, "frt_recev_mobile", frt_recev_mobile);
        	orderxml = appendParam(orderxml, "frt_recev_phone", frt_recev_phone);
        	orderxml = appendParam(orderxml, "frtRecevAddrProvince", frtRecevAddrProvince);
        	orderxml = appendParam(orderxml, "cityID", cityID);
        	orderxml = appendParam(orderxml, "cityName", cityName);
        	orderxml = appendParam(orderxml, "distID", distID);
        	orderxml = appendParam(orderxml, "disName", disName);
        	orderxml = appendParam(orderxml, "road", road);
        	orderxml = appendParam(orderxml, "lane", lane);
        	orderxml = appendParam(orderxml, "no", no);
        	orderxml = appendParam(orderxml, "roomNo", roomNo);
        	orderxml = appendParam(orderxml, "detailAddr", detailAddr);
        	orderxml = appendParam(orderxml, "zipcode", zipcode);
        	orderxml = appendParam(orderxml, "tikDay", tikDay);
        	orderxml = appendParam(orderxml, "tikTime", tikTime);
        	orderxml = appendParam(orderxml, "sendRegn", sendRegn);
        	orderxml = appendParam(orderxml, "frt_carriage_fee", frt_carriage_fee);
        	orderxml = appendParam(orderxml, "priceTtlAmt", priceTtlAmt);
        	orderxml = appendParam(orderxml, "priceArtl", priceArtl);
        	orderxml = appendParam(orderxml, "priceServ", priceServ);
        	orderxml = appendParam(orderxml, "pricePrepaid", pricePrepaid);
        	orderxml = appendParam(orderxml, "priceArtlCnt", priceArtlCnt);
        	orderxml = appendParam(orderxml, "tik_group_code", tik_group_code);
        	orderxml = appendParam(orderxml, "tik_depart_time", tik_depart_time);
        	orderxml = appendParam(orderxml, "trainNumber", trainNumber);
        	orderxml = appendParam(orderxml, "departureStation", departureStation);
        	orderxml = appendParam(orderxml, "arrivalStation", arrivalStation);
        	orderxml = appendParam(orderxml, "tn_sta_depart", tn_sta_depart);
        	orderxml = appendParam(orderxml, "tn_sta_arrive", tn_sta_arrive);
        	orderxml = appendParam(orderxml, "tik_seat_type_code", tik_seat_type_code);
        	orderxml = appendParam(orderxml, "tik_price", tik_price);
        	orderxml = appendParam(orderxml, "piece", piece);
        	orderxml = appendParam(orderxml, "tn_sta_arrive", tikCertInfo);
        	String urlstr = "";
        	
        	try {
        		urlstr=this.getIpAddress() + "/" + this.getSktrainurl() + "?method="+this.SAVERDERMETHOD+"&"+orderxml;
        		System.out.println(urlstr);
        		HttpURLConnection connection = this.openConnection(urlstr);
        		SAXReader reader = new SAXReader();
        		Document document = reader.read(connection.getInputStream());
        		
        		Element root = document.getRootElement();
        		String error_code = root.elementTextTrim("error_code");
        		if (error_code.trim().equals("111111")) {
        			error_msg = root.elementTextTrim("error_msg");
        		}
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	*/
        return error_msg;
    }

    /**
     * 获取余票信息接口
     */
    public List<Train> getYPTrainlistFromInterface(String urlstr) {
        System.out.println(urlstr);
        List<Train> listtrain = new ArrayList<Train>();
        try {
            HttpURLConnection connection = this.openConnection(urlstr);
            System.out.println("返回地址：" + connection);
            SAXReader reader = new SAXReader();
            Document document = reader.read(connection.getInputStream());

            Element root = document.getRootElement();
            Iterator<Element> elementlist = root.elementIterator("train_info");
            String error_code = root.elementTextTrim("error_code");
            if (error_code.trim().equals("111111")) {
                while (elementlist.hasNext()) {
                    Train train = new Train();
                    Element element = elementlist.next();
                    String sta_tn_depart = element.elementTextTrim("sta_tn_depart");
                    train.setTraincode(sta_tn_depart);
                    List<Element> seat_list = element.elements("seat");
                    Iterator it = seat_list.iterator();
                    while (it.hasNext()) {
                        Element ik_Number_info = (Element) it.next();
                        String seatName = ik_Number_info.elementTextTrim("seatName");
                        String typeCode = ik_Number_info.elementTextTrim("typeCode");
                        String seatNumber = ik_Number_info.elementTextTrim("seatNumber");
                        if (seatNumber.equals("0")) {
                            seatNumber = "-";
                        }
                        if ("硬座".equals(seatName)) {
                            train.setYzyp(seatNumber);
                        }
                        else if ("软座".equals(seatName)) {
                            train.setRzyp(seatNumber);
                        }
                        else if ("一等座".equals(seatName)) {
                            train.setRz1yp(seatNumber);
                        }
                        else if ("二等座".equals(seatName)) {
                            train.setRz2yp(seatNumber);
                        }
                        else if ("硬卧".equals(seatName)) {
                            train.setYwyp(seatNumber);
                        }
                        else if ("软卧".equals(seatName)) {
                            train.setRwyp(seatNumber);
                        }
                        else if ("高级软卧".equals(seatName)) {
                            train.setGwyp(seatNumber);
                        }
                        else if ("无座".equals(seatName)) {
                            train.setWzyp(seatNumber);
                        }
                    }
                    listtrain.add(train);
                }
            }

        }
        catch (Exception e) {
            System.out.println("火车票余票接口异常" + e.getMessage());
        }
        return listtrain;
    }

    /**
     * 获取城市信息
     */
    @Override
    public List<Map<String, String>> getCityinfo(String cityName) {
        String urlstr = "";
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            urlstr = this.getIpAddress() + "/" + this.getSktrainurl() + "?method=" + this.CITYMETHOD + "&"
                    + this.CITYPROV + "=" + URLEncoder.encode(cityName, "UTF-8");
            System.out.println(urlstr);

            HttpURLConnection connection = this.openConnection(urlstr);
            SAXReader reader = new SAXReader();
            Document document = reader.read(connection.getInputStream());

            Element root = document.getRootElement();
            String error_code = root.elementTextTrim("error_code");
            if (error_code.trim().equals("111111")) {
                Iterator<Element> provlist = root.elementIterator("dict_prov");
                while (provlist.hasNext()) {
                    Element element = provlist.next();
                    Map<String, String> map = new HashMap<String, String>();
                    String prov_code = element.elementTextTrim("prov_code");
                    map.put("prov_code", prov_code);
                    String prov_nam = element.elementTextTrim("prov_nam");
                    map.put("prov_nam", prov_nam);
                    list.add(map);
                }
                Iterator<Element> citylist = root.elementIterator("dict_city");
                while (citylist.hasNext()) {
                    Map<String, String> map = new HashMap<String, String>();
                    Element element = citylist.next();
                    String city_id = element.elementTextTrim("city_id");
                    map.put("city_id", city_id);
                    String city_code = element.elementTextTrim("city_code");
                    map.put("city_code", city_code);
                    String city_name = element.elementTextTrim("city_name");
                    map.put("city_name", city_name);
                    list.add(map);
                }
                Element dict_district = root.element("dict_ district");
                List<Element> district_list = dict_district.elements("dict_district");
                Iterator it = district_list.iterator();
                while (it.hasNext()) {
                    Map<String, String> map = new HashMap<String, String>();
                    Element element = (Element) it.next();
                    String dis_id = element.elementTextTrim("dis_id");
                    map.put("dis_id", dis_id);
                    String dis_name = element.elementTextTrim("dis_name");
                    map.put("dis_name", dis_name);
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
     * 获取省份信息
     */
    @Override
    public List<Map<String, String>> getcityProv(String cityProv) {
        String urlstr = "";
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            urlstr = this.getIpAddress() + "/" + this.getSktrainurl() + "?method=" + this.PROVMETHOD + "&"
                    + this.CITYNAME + "=" + URLEncoder.encode(cityProv, "UTF-8") + "&channel=" + channel;
            System.out.println(urlstr);
            HttpURLConnection connection = this.openConnection(urlstr);
            SAXReader reader = new SAXReader();
            Document document = reader.read(connection.getInputStream());

            Element root = document.getRootElement();
            String error_code = root.elementTextTrim("error_code");
            if (error_code.trim().equals("111111")) {
                Iterator<Element> provlist = root.elementIterator("dict_prov");
                while (provlist.hasNext()) {
                    Element element = provlist.next();
                    Map<String, String> map = new HashMap<String, String>();
                    String prov_code = element.elementTextTrim("prov_code");
                    map.put("prov_code", prov_code);
                    String prov_nam = element.elementTextTrim("prov_nam");
                    map.put("prov_nam", prov_nam);

                    List<Element> dict_city = element.elements("dict_city");
                    Iterator it = dict_city.iterator();
                    while (it.hasNext()) {
                        Element ik_dictt_info = (Element) it.next();
                        String city_id = ik_dictt_info.elementTextTrim("city_id");
                        map.put("city_id", city_id);
                        String city_code = ik_dictt_info.elementTextTrim("city_code");
                        map.put("city_code", city_code);
                        String city_name = ik_dictt_info.elementTextTrim("city_name");
                        map.put("city_name", city_name);

                        List<Element> seat_list = ik_dictt_info.elements("dict_district");
                        Iterator dis = seat_list.iterator();
                        while (dis.hasNext()) {
                            Element dis_element = (Element) it.next();
                            String dis_id = dis_element.elementTextTrim("dis_id");
                            map.put("dis_id", dis_id);
                            String dis_name = dis_element.elementTextTrim("dis_name");
                            map.put("dis_name", dis_name);
                        }
                    }
                    list.add(map);

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<Customeragent, Float> getAgentroyalty(Train train) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createTrainrebate(Train train) {
        // TODO Auto-generated method stub

    }

    /**
     * 时间处理
     * @param sta_duration
     * @return
     */
    public static String opreationTime(String sta_duration) {
        int h = 0, d = 0;
        int costtime = Integer.valueOf(sta_duration);
        costtime = costtime / 60;
        d = costtime % 60;
        h = costtime / 60;
        sta_duration = h + "时" + d + "分";
        return sta_duration;
    }

    /**
     * 票价处理
     * @param unitPrice
     * @return
     */
    public static String opreationPrice(String unitPrice) {
        if (null == unitPrice || "".equals(unitPrice)) {
            unitPrice = "-";
        }
        unitPrice = unitPrice.substring(0, unitPrice.length() - 2) + "."
                + unitPrice.substring(unitPrice.length() - 2, unitPrice.length());
        return unitPrice;
    }

    /**
     * 停留时间
     * @param arrivetime
     * @param gotime
     * @return
     */
    public static String residencetime(String arrivetime, String gotime) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String residencetime = "";
        try {
            if (arrivetime.equals("0")) {
                residencetime = "0";
            }
            else if (gotime.equals("0")) {
                residencetime = "0";
            }
            else {
                Date arr_time = formatter.parse(arrivetime);
                Date go_time = formatter.parse(gotime);
                long timestr = go_time.getTime() - arr_time.getTime();
                timestr = timestr / 1000 / 60;
                residencetime = String.valueOf(timestr);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return residencetime;
    }

    /**
     * 功能函数。将变量值不为空的参数组成字符串
     * 
     * @param returnStr
     * @param paramId
     * @param paramValue
     * @return
     */
    public static String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        }
        else {
            returnStr = paramId + "=" + paramValue;
        }
        return returnStr;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public List<Train> getDGTrainListcache(String fromcity, String tocity, String date, FlightSearch param) {
        // TODO Auto-generated method stub
        return null;
    }

}
