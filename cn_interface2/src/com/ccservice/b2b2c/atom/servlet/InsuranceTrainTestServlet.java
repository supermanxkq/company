package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 保险的压力测试servlet
 * 
 * @time 2015年5月21日 下午12:43:07
 * @author chendong
 */
public class InsuranceTrainTestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private boolean nowTime = false;

    public void init() throws ServletException {
        super.init();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        int l1 = (int) (Math.random() * 10000000);
        long time1 = System.currentTimeMillis();
        String TaoBaoTrainIdVerification_Url = PropertyUtil.getValue("TaoBaoTrainIdVerification_Url",
                "Train.properties");
        /**
         * 走转发
         */
        if (ElongHotelInterfaceUtil.StringIsNull(TaoBaoTrainIdVerification_Url)) {
            PrintWriter out = null;
            String dataString = request.getParameter("datas");
            String taobao_callbackstr = SendPostandGet.submitGet(
                    TaoBaoTrainIdVerification_Url + "?datas=" + dataString, "iso-8859-1");
            try {// 接收到参数,返回SUCCESS
                WriteLog.write("ID_身份验证接口", l1 + ":耗时:" + (System.currentTimeMillis() - time1) + ":返回结果====>"
                        + taobao_callbackstr);
                out = response.getWriter();
                out.print(taobao_callbackstr);
            }
            catch (IOException e) {
                e.printStackTrace();
                out.flush();
                out.close();
            }
        }
        /**
         * 走原有的类
         */
        else {

            JSONObject jsono = new JSONObject();

            String result = "";
            String jsonStr = "";
            // 获取淘宝request
            try {
                jsonStr = reqParStr(request, "datas");

                JSONObject json = JSONObject.fromObject(jsonStr);
                JSONObject sta = new JSONObject();
                jsono.put("status", "407");
                sta.put("status", "0");
                jsono.put("result", sta);
            }
            catch (Exception e) {

            }
            WriteLog.write("TB_身份验证接口", l1 + jsonStr);
            try {
                // 参数不能为空
                if (ElongHotelInterfaceUtil.StringIsNull(jsonStr)) {
                    JSONObject sta = new JSONObject();
                    sta.put("status", "0");
                    jsono.put("result", sta);
                    jsono.put("status", "406");
                    result = jsono.toString();
                }
                else {
                    // String param = new
                    // String(request.getParameter("datas").getBytes("UTF-8"),
                    // "UTF-8");
                    // POST请求数据
                    nowTime = getNowTime(new Date());
                    JSONObject json = JSONObject.fromObject(jsonStr);
                    String pessengerCount = json.getString("total");
                    if (Integer.parseInt(pessengerCount) > 5) {
                        JSONObject sta = new JSONObject();
                        sta.put("status", "0");
                        jsono.put("result", sta);
                        jsono.put("status", "407");
                        result = jsono.toString();
                    }
                    else if (!nowTime) {
                        JSONObject sta = new JSONObject();
                        sta.put("status", "0");
                        jsono.put("result", sta);
                        jsono.put("status", "405");
                        result = jsono.toString();
                    }
                    else if (ElongHotelInterfaceUtil.StringIsNull(pessengerCount)) {
                        JSONObject sta = new JSONObject();
                        sta.put("status", "0");
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
                                    sta.put("status", "0");
                                    jsono.put("result", sta);
                                    jsono.put("status", "406");
                                    break;
                                }
                                trainpassenger.setIdnumber(id_no);
                                String name = jop.getString("passengerName");
                                if (ElongHotelInterfaceUtil.StringIsNull(name)) {
                                    JSONObject sta = new JSONObject();
                                    sta.put("status", "0");
                                    jsono.put("result", sta);
                                    jsono.put("status", "406");
                                    break;
                                }
                                trainpassenger.setName(name);
                                String id_type = jop.getString("cardType");
                                if (ElongHotelInterfaceUtil.StringIsNull(id_type)) {
                                    JSONObject sta = new JSONObject();
                                    sta.put("status", "0");
                                    jsono.put("result", sta);
                                    jsono.put("status", "406");
                                    break;
                                }
                                int id_type_code = getTypes(id_type);
                                trainpassenger.setIdtype(id_type_code);
                                trainpassenger.setChangeid(-1);
                                trainpassenger.setOrderid(-2);//-2代表淘宝聚石塔配置走外网访问地址
                                plist.add(trainpassenger);
                            }
                        }
                        Customeruser customeruser;
                        for (int i = 0; i < plist.size(); i++) {
                            List<Trainpassenger> plistS = new ArrayList<Trainpassenger>();
                            plistS.add(plist.get(i));
                            customeruser = getcustomeruser(plistS);
                            if (customeruser.getId() > 0 && customeruser.getIsenable() == 1) {

                                plist.get(i).setAduitstatus(1);

                                jsono.put("status", 200);
                                // jsono.put("result", results(plist));
                                /* jsono.putAll(results(plist));
                                 result = jsono.toString();*/
                            }
                            else if (customeruser.getId() > 0 && customeruser.getIsenable() == 0) {
                                // 身份验证失败:添加乘客姚鹏飞未通过身份效验142733198506225130
                                if (customeruser.getDescription().length() > 1) {

                                    if (customeruser.getDescription().contains(plist.get(i).getIdnumber())) {
                                        plist.get(i).setAduitstatus(0);
                                    }
                                    else {
                                        plist.get(i).setAduitstatus(1);
                                    }

                                }
                                else {
                                    plist.get(i).setAduitstatus(0);

                                }
                            }
                            else {
                                plist.get(i).setAduitstatus(0);
                            }

                            freeAndSave(customeruser, plist, l1);
                        }
                        jsono.putAll(results(plist));
                        jsono.put("status", 200);
                        result = jsono.toString();

                        // Aduitstatus-1:成功 Aduitstatus-0:失败

                        /*
                         * jsono.put("status", 200); // jsono.put("result",
                         * results(plist)); jsono.putAll(results(plist)); result =
                         * jsono.toString(); System.out.println(result);
                         */
                        // TODO 释放账号,存储乘客

                    }
                }

            }
            catch (Exception e) {
                try {
                    JSONObject sta = new JSONObject();
                    sta.put("status", "0");
                    jsono.put("result", sta);
                    jsono.put("status", "408");
                    WriteLog.write("ID_身份验证接口异常", l1 + ":msg系统错误,未知服务异常");
                    e.printStackTrace();
                    throw new Exception("Request Data Error.");
                }
                catch (Exception e1) {
                    JSONObject sta = new JSONObject();
                    sta.put("status", "0");
                    jsono.put("result", sta);
                    jsono.put("status", "406");
                    WriteLog.write("ID_身份验证接口异常", l1 + ":验证出现错误，请重试");
                }
            }
            finally {
                PrintWriter out;
                try {// 接收到参数,返回SUCCESS
                    WriteLog.write("ID_身份验证接口", l1 + ":耗时:" + (System.currentTimeMillis() - time1) + ":返回结果====>"
                            + jsono.toString());
                    out = response.getWriter();
                    String resultString = jsono.toString();
                    out.print(resultString);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
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
     * 说明:接口可调用时间:早7晚11
     * 
     * @param date
     * @return
     * @time 2014年8月30日 下午4:23:20
     * @author yinshubin
     */
    public static boolean getNowTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        try {
            Date dateBefor = df.parse("07:00:00");
            Date dateAfter = df.parse("23:00:00");
            Date time = df.parse(df.format(date));
            if (time.after(dateBefor) && time.before(dateAfter)) {
                return true;
            }
        }
        catch (ParseException e) {
            WriteLog.write("ID_身份验证接口异常", "获取时间出错");
        }
        return false;// 现在24小时,以后有需要再改为FALSE
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
        if ("待核验".equals(string)) {
            return "0";
        }
        else if ("已通过".equals(string)) {
            return "1";
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

            if (trainpassenger.getAduitstatus() == 1) {
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
                jsonp.put("status", "0");
                jsona.add(jsonp);
            }

        }
        json.put("result", jsona);
        return json;
    }

    /**
     * 通过接口身份验证
     * 
     * @param plist
     * @return
     * @time 2015年2月3日 下午4:09:10
     * @author fiend
     */
    public Customeruser getcustomeruser(List<Trainpassenger> plist) {
        // Train12306ServiceImpl trainServiceImpl = new Train12306ServiceImpl();
        // Customeruser customeruser = trainServiceImpl.getcustomeruser(plist);
        Trainorder order = new Trainorder();
        order.setPassengers(plist);
        Customeruser customeruser = Server.getInstance().getTrain12306Service().getcustomeruser(order);
        return customeruser;
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

    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author chendong
     */
    @SuppressWarnings("rawtypes")
    private String getcustomeruserKEYbyloginname(String loginname) {
        String dbkey = "-1";
        try {
            String sql = "SELECT " + Customeruser.COL_workphone + " FROM T_CUSTOMERUSER WHERE C_LOGINNAME='"
                    + loginname + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                dbkey = map.get("C_WORKPHONE").toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return dbkey;
    }

    /**
     * 调用线程释放账号,存储乘客信息
     * 
     * @param customeruser
     * @param plist
     * @time 2015年3月19日 下午12:29:15
     * @author fiend
     */
    private void freeAndSave(Customeruser customeruser, List<Trainpassenger> plist, int r1) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        try {
            t1 = new MyThreadIdVerification(customeruser, plist, r1);
            pool.execute(t1);
            // 关闭线程池
            pool.shutdown();
        }
        catch (Exception e) {
            pool.shutdown();
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