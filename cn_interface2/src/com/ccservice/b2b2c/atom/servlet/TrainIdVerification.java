package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.InterfaceTimeRuleUtil;
import com.ccservice.b2b2c.util.TrainIdverificationWatchDog;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 身份验证接口
 * 
 * @time 2015年4月8日 下午2:02:28
 * @author liangwei
 */
public class TrainIdVerification extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private boolean nowTime = false;

    //是否虚假审核（全部返还成功）
    private boolean isfalsitylverification = false;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            String path = config.getServletContext().getRealPath("/") + "TrainIdverification.xml";
            if (path != null && path.contains("file:/")) {
                path = path.replaceFirst("file:/", "");
            }
            TrainIdverificationWatchDog dog = new TrainIdverificationWatchDog(path);
            dog.setDelay(5000L);
            dog.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        int l1 = (int) (Math.random() * 10000000);
        long time1 = System.currentTimeMillis();
        //        try {
        //            this.isfalsitylverification = Boolean.valueOf(TrainIdverificationPropetyUtil
        //                    .getValue("isfalsitylverification"));
        //        }
        //        catch (Exception e2) {
        //            e2.printStackTrace();
        //        }
        JSONObject jsono = new JSONObject();

        String result = "";
        String jsonStr = "";
        // 获取淘宝request
        try {
            jsonStr = reqParStr(request, "datas");
            try {
                jsonStr = URLDecoder.decode(jsonStr, "UTF-8");
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JSONObject json = JSONObject.fromObject(jsonStr);
            JSONObject sta = new JSONObject();
            jsono.put("status", "407");
            sta.put("status", "-1");
            jsono.put("result", sta);
        }
        catch (Exception e) {

        }
        WriteLog.write("TB_身份验证接口", l1 + jsonStr);
        try {
            // 参数不能为空
            if (ElongHotelInterfaceUtil.StringIsNull(jsonStr)) {
                JSONObject sta = new JSONObject();
                sta.put("status", "-1");
                jsono.put("result", sta);
                jsono.put("status", "406");
                result = jsono.toString();
            }
            else {
                // POST请求数据
                nowTime = !InterfaceTimeRuleUtil.isNightCreateOrder();
                JSONObject json = JSONObject.fromObject(jsonStr);
                String pessengerCount = json.getString("total");
                if (Integer.parseInt(pessengerCount) > 5) {
                    JSONObject sta = new JSONObject();
                    sta.put("status", "-1");
                    jsono.put("result", sta);
                    jsono.put("status", "407");
                    result = jsono.toString();
                }
                else if (!nowTime) {
                    JSONObject sta = new JSONObject();
                    sta.put("status", "-1");
                    jsono.put("result", sta);
                    jsono.put("status", "405");
                    result = jsono.toString();
                }
                else if (ElongHotelInterfaceUtil.StringIsNull(pessengerCount)) {
                    JSONObject sta = new JSONObject();
                    sta.put("status", "-1");
                    jsono.put("result", sta);
                    jsono.put("status", "406");
                }
                else if (json.has("passengers")) {
                    JSONArray jap = json.getJSONArray("passengers");
                    List<Trainpassenger> plist = new ArrayList<Trainpassenger>();
                    for (int i = 0; i < jap.size(); i++) {
                        JSONObject jop = jap.getJSONObject(i);
                        if (jop.has("cardNo") && jop.has("passengerName") && jop.has("cardType")) {
                            Trainpassenger trainpassenger = new Trainpassenger();
                            trainpassenger.setId(i);
                            String id_no = jop.getString("cardNo");
                            if (ElongHotelInterfaceUtil.StringIsNull(id_no)) {
                                JSONObject sta = new JSONObject();
                                sta.put("status", "-1");
                                jsono.put("result", sta);
                                jsono.put("status", "406");
                                break;
                            }
                            trainpassenger.setIdnumber(id_no);
                            String name = jop.getString("passengerName");
                            if (ElongHotelInterfaceUtil.StringIsNull(name)) {
                                JSONObject sta = new JSONObject();
                                sta.put("status", "-1");
                                jsono.put("result", sta);
                                jsono.put("status", "406");
                                break;
                            }
                            trainpassenger.setName(name);
                            String id_type = jop.getString("cardType");
                            if (ElongHotelInterfaceUtil.StringIsNull(id_type)) {
                                JSONObject sta = new JSONObject();
                                sta.put("status", "-1");
                                jsono.put("result", sta);
                                jsono.put("status", "406");
                                break;
                            }
                            int id_type_code = getTypes(id_type);
                            trainpassenger.setIdtype(id_type_code);
                            plist.add(trainpassenger);
                        }
                    }
                    //TODO 
                    if (isfalsitylverification) {
                        jsono.putAll(falsityResults(plist));
                        jsono.put("status", 200);
                        result = jsono.toString();
                    }
                    else {
                        plist = tongchengIdVerification(plist);
                        jsono.putAll(results(plist));
                        jsono.put("status", 200);
                        result = jsono.toString();
                    }
                }
            }
        }
        catch (Exception e) {
            try {
                JSONObject sta = new JSONObject();
                sta.put("status", "-1");
                jsono.put("result", sta);
                jsono.put("status", "408");
                WriteLog.write("ID_身份验证接口异常", l1 + ":msg系统错误,未知服务异常");
                e.printStackTrace();
                throw new Exception("Request Data Error.");
            }
            catch (Exception e1) {
                JSONObject sta = new JSONObject();
                sta.put("status", "-1");
                jsono.put("result", sta);
                jsono.put("status", "406");
                WriteLog.write("ID_身份验证接口异常", l1 + ":验证出现错误，请重试");
            }
        }
        finally {
            PrintWriter out;
            try {// 接收到参数,返回SUCCESS
                WriteLog.write("ID_身份验证接口",
                        l1 + ":耗时:" + (System.currentTimeMillis() - time1) + ":返回结果====>" + jsono.toString());
                out = response.getWriter();
                String resultString = jsono.toString();
                out.print(resultString);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        this.doGet(request, response);
    }

    /**

    /**
     * 说明:审核状态类型变更
     * 
     * @param i
     * @return
     * @time 2014年8月30日 下午4:23:02
     * @author yinshubin
     */
    public static String getState(int i) {
        if (i == 1) {
            return "已通过";
        }
        else if (i == 0) {
            return "待核验";
        }
        return "未通过";
    }

    /**
     * 说明:审核状态类型变更
     * 
     * @param string
     * @return
     * @time 2014年8月30日 下午4:23:46
     * @author yinshubin
     */
    public static String getState12306(String string) {
        /*if ("待核验".equals(string)) {
            return "0";
        }
        else*/if ("已通过".equals(string)) {
            return "1";
        }
        else if (string.contains("冒用")) {
            return "-2";
        }
        return "-1";
    }

    /**
     * 说明:添加对去哪儿网的返回值并且将数据存入数据库
     * 
     * @param listp
     * @return
     * @time 2014年8月30日 下午4:23:54
     * @author yinshubin
     */
    public JSONObject results(List<Trainpassenger> listp) {
        JSONObject json = new JSONObject();
        JSONArray jsona = new JSONArray();
        for (Trainpassenger trainpassenger : listp) {
            JSONObject jsonp = new JSONObject();

            if (trainpassenger.getAduitstatus() != -1 && trainpassenger.getAduitstatus() != -2) {
                jsonp = new JSONObject();
                jsonp.put("cardType", getType(trainpassenger.getIdtype() + ""));
                jsonp.put("cardNo", trainpassenger.getIdnumber());
                jsonp.put("passengerName", trainpassenger.getName());
                jsonp.put("status", "1");
                jsona.add(jsonp);
            }
            else {
                jsonp = new JSONObject();
                jsonp.put("cardType", getType(trainpassenger.getIdtype() + ""));
                jsonp.put("cardNo", trainpassenger.getIdnumber());
                jsonp.put("passengerName", trainpassenger.getName());
                jsonp.put("status", trainpassenger.getAduitstatus());
                jsona.add(jsonp);
            }

        }
        json.put("result", jsona);
        return json;
    }

    /**
     * 说明:添加虚假的返回值
     * 
     * @param listp
     * @return
     * @time 2014年8月30日 下午4:23:54
     * @author yinshubin
     */
    public JSONObject falsityResults(List<Trainpassenger> listp) {
        JSONObject json = new JSONObject();
        JSONArray jsona = new JSONArray();
        for (Trainpassenger trainpassenger : listp) {
            JSONObject jsonp = new JSONObject();
            jsonp = new JSONObject();
            jsonp.put("cardType", getType(trainpassenger.getIdtype() + ""));
            jsonp.put("cardNo", trainpassenger.getIdnumber());
            jsonp.put("passengerName", trainpassenger.getName());
            jsonp.put("status", "1");
            jsona.add(jsonp);

        }
        json.put("result", jsona);
        return json;
    }

    /**
     * 是否是真的空
     * 
     * @param str
     * @return
     * @time 2015年3月18日 下午7:06:59
     * @author fiend
     */
    private boolean isRealEnnull(String str) {
        if (str == null) {
            return false;
        }
        if ("".equals(str)) {
            return false;
        }
        return true;
    }

    /**
     * 获取参数
     * 
     * @param req
     * @param str
     * @return
     * @time 2015年3月18日 下午7:13:23
     * @author fiend
     * @throws UnsupportedEncodingException
     */
    private String reqParStr(HttpServletRequest req, String str) throws UnsupportedEncodingException {
        // String result = req.getParameter(str);
        //System.out.println(req.getParameter(str));
        String result = new String(req.getParameter(str).getBytes("iso-8859-1"), "utf-8");
        // System.out.println(result);
        if (isRealEnnull(result)) {
            return result;
        }
        else {
            return result;
        }
    }

    public static void main(String[] args) {
        try {
            String str = ElongHotelInterfaceUtil
                    .MD5("yitong_test"
                            + "V0101"
                            + "1427446854334"
                            + "{\"passengers\":[{\"passenger_id_no\":\"44011030198010170038\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"二代身份证\",\"passenger_name\":\"容海燊\"},{\"passenger_id_no\":\"440224197907072877\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"二代身份证\",\"passenger_name\":\"汤利锋\"}]}"
                            + ElongHotelInterfaceUtil.MD5("6vogatwqvjd64mbz1qx756zj7169trte"));

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 说明:身份类型类型转换 String -->int
     * 
     * @param s
     * @return
     * @time 2014年8月30日 下午4:22:53
     * @author yinshubin
     */
    public static int getType(String s) {
        if (s.equals("1")) {
            return 0;
        }
        else if (s.equals("3")) {
            return 1;
        }
        else if (s.equals("4")) {
            return 4;
        }
        else if (s.equals("5")) {
            return 5;
        }
        else
            return -1;

    }

    public static int getTypes(String s) {
        if (s.equals("0")) {
            return 1;
        }
        else if (s.equals("1")) {
            return 3;
        }
        else if (s.equals("4")) {
            return 4;
        }
        else if (s.equals("5")) {
            return 5;
        }
        else
            return -1;

    }

    /**
     * 获取请求同城的URL
     * @param plist
     * @return
     * @throws Exception
     * @author fiend
     */
    private String getTongchengIdVerificationURL(List<Trainpassenger> plist) throws Exception {
        JSONObject jso = new JSONObject();
        String tongchengIdVerificationUrl = PropertyUtil.getValue("tongchengIdVerificationUrl", "Train.properties");
        String merchantCode = PropertyUtil.getValue("taobao_merchantCode", "Train.properties");
        String serviceID = PropertyUtil.getValue("taobao_serviceID", "Train.properties");
        String key = PropertyUtil.getValue("taobao_key", "Train.properties");
        String data = tongchengData(plist);
        String data_encode = tongchengDataEncode(plist);
        long timestamp = System.currentTimeMillis();
        String version = PropertyUtil.getValue("taobao_version", "Train.properties");
        String sign = ElongHotelInterfaceUtil.MD5(
                merchantCode + serviceID + timestamp + data + ElongHotelInterfaceUtil.MD5(key).toUpperCase())
                .toUpperCase();
        String url = tongchengIdVerificationUrl + "?merchantCode=" + merchantCode + "&serviceId=" + serviceID
                + "&version=" + version + "&timestamp=" + timestamp + "&data=" + data_encode + "&sign=" + sign;
        return url;
    }

    /**
     * 拼接请求同城身份验证的data参数
     * @param plist
     * @return
     * @author fiend
     */
    public String tongchengData(List<Trainpassenger> plist) {
        JSONObject dateJsonObject = new JSONObject();
        JSONArray passengersArray = new JSONArray();
        for (int i = 0; i < plist.size(); i++) {
            Trainpassenger trainpassenger = plist.get(i);
            JSONObject passengerJsonObject = new JSONObject();
            passengerJsonObject.put("passenger_id_no", trainpassenger.getIdnumber());
            passengerJsonObject.put("passenger_id_type_code", idTypeDB2Tongcheng(trainpassenger.getIdtype()));
            passengerJsonObject.put("passenger_id_type_name", trainpassenger.getIdtypestr());
            passengerJsonObject.put("passenger_name", trainpassenger.getName());
            passengersArray.add(passengerJsonObject);
        }
        dateJsonObject.put("passengers", passengersArray);
        return dateJsonObject.toString();
    }

    /**
     * 拼接请求同城身份验证的data参数
     * @param plist
     * @return
     * @author fiend
     * @throws UnsupportedEncodingException 
     */
    public String tongchengDataEncode(List<Trainpassenger> plist) throws UnsupportedEncodingException {
        JSONObject dateJsonObject = new JSONObject();
        JSONArray passengersArray = new JSONArray();
        for (int i = 0; i < plist.size(); i++) {
            Trainpassenger trainpassenger = plist.get(i);
            JSONObject passengerJsonObject = new JSONObject();
            passengerJsonObject.put("passenger_id_no", trainpassenger.getIdnumber());
            passengerJsonObject.put("passenger_id_type_code", idTypeDB2Tongcheng(trainpassenger.getIdtype()));
            passengerJsonObject
                    .put("passenger_id_type_name", URLEncoder.encode(trainpassenger.getIdtypestr(), "UTF-8"));
            passengerJsonObject.put("passenger_name", URLEncoder.encode(trainpassenger.getName(), "UTF-8"));
            passengersArray.add(passengerJsonObject);
        }
        dateJsonObject.put("passengers", passengersArray);
        return dateJsonObject.toString();
    }

    /**
     * 请求同城的身份证与 并且解析结果
     * @param plist
     * @param url
     * @return
     * @author fiend
     */
    public List<Trainpassenger> tongchengIdVerification(List<Trainpassenger> plist) {
        for (int i = 0; i < plist.size(); i++) {
            plist.get(i).setAduitstatus(1);
        }
        try {
            String randomString = System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
            String url = getTongchengIdVerificationURL(plist);
            WriteLog.write("TAOBAO_TRAINIDVERIFICATION_2_TONGCHENG", randomString + "--->" + url);
            String result = SendPostandGet2.doGet(url, "UTF-8");
            WriteLog.write("TAOBAO_TRAINIDVERIFICATION_2_TONGCHENG", randomString + "--->" + result);
            JSONObject resultJsonObject = JSONObject.fromObject(result);
            String code = resultJsonObject.has("code") ? resultJsonObject.getString("code") : "1200";
            if ("1100".equals(code)) {
                JSONArray passengersArray = resultJsonObject.getJSONObject("data").getJSONArray("passengers");
                for (int i = 0; i < plist.size(); i++) {
                    Trainpassenger trainpassenger = plist.get(i);
                    for (int j = 0; j < passengersArray.size(); j++) {
                        int verification_status = 0;
                        if (passengersArray.getJSONObject(j).containsKey("verification_status")) {
                            verification_status = passengersArray.getJSONObject(j).getInt("verification_status");
                        }
                        else if (passengersArray.getJSONObject(j).containsKey("verification_status_name")) {
                            String verification_status_nmae = passengersArray.getJSONObject(j).getString(
                                    "verification_status_name");
                            verification_status = Integer.valueOf(getState12306(verification_status_nmae));
                        }
                        String passenger_id_no = passengersArray.getJSONObject(j).getString("passenger_id_no");
                        String passenger_name = passengersArray.getJSONObject(j).getString("passenger_name");
                        if (trainpassenger.getName().equals(passenger_name)
                                && trainpassenger.getIdnumber().equals(passenger_id_no)) {
                            trainpassenger.setAduitstatus(verification_status);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return plist;
    }

    /**
     * 说明:身份类型类型转换 DB -->Tongcheng
     * @param s
     * @return
     * @time 2014年8月30日 下午4:22:53
     * @author yinshubin
     */
    public String idTypeDB2Tongcheng(int s) {
        if (4 == s) {
            return "C";
        }
        else if (3 == s) {
            return "B";
        }
        else if (5 == s) {
            return "G";
        }
        else {
            return "1";
        }
    }
}
// 98-待核验；99-已通过；还有一个未通过

// 1 已通过
// 0 待核验
// -1 未通过

/*
 * map.put(1, "身份证"); map.put(3, "护照"); map.put(4, "港澳通行证"); map.put(5,
 * "台湾通行证"); map.put(6, "台胞证"); map.put(7, "回乡证"); map.put(8, "军官证"); map.put(9,
 * "其它"); map.put(10, "学生证"); map.put(11, "国际海员证");
 */

// 1 身份证 C 港澳通行证 G 台湾通行证 B 护照ij