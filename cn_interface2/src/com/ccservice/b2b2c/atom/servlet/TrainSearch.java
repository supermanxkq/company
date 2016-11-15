package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread.SearchThread;
import com.ccservice.b2b2c.atom.train.data.Wrapper_12306;
import com.ccservice.b2b2c.atom.train.data.Wrapper_tieyou;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.base.train.Train;
import com.tenpay.util.MD5Util;
import com.travelGuide.TrainEnquiries;
import com.travelGuide.TravelGuideRequest;

/**
 * 
 *
 */
public class TrainSearch extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 152548944651L;

    public String default_data;

    public String key;// = "x3z5nj8mnvl14nirtwlvhvuialo0akyt";

    public String partnerid;// = "tongcheng_train";

    Map<String, InterfaceAccount> interfaceAccountMap;

    Wrapper_12306 wrapper_12306;

    TravelGuideRequest travelguide;

    TrainEnquiries enquiries;

    @Override
    public void init() throws ServletException {
        this.default_data = this.getInitParameter("default_data");
        this.key = this.getInitParameter("key");
        this.partnerid = this.getInitParameter("partnerid");
        interfaceAccountMap = new HashMap<String, InterfaceAccount>();
        wrapper_12306 = new Wrapper_12306();
        enquiries = new TrainEnquiries();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        doPost(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        long l1 = System.currentTimeMillis();
        PrintWriter out = null;
        String resultJSONString = "{\"success\":false,\"code\":999,\"msg\":\"nodata\"}";
        int r1 = new Random().nextInt(10000000);
        try {
            out = res.getWriter();
            String op = req.getParameter("op");
            String jsonstr = "-1";
            if (req.getParameter("jsonStr") == null) {
                resultJSONString = "{\"success\":false,\"code\":101,\"msg\":\"传入的 json 为空对象\"}";
                if ("1".equals(req.getParameter("initdata"))) {// 初始化账号信息!!!!!!
                    new Thread(new TrainSearchThread(interfaceAccountMap, 2)).start();
                    interfaceAccountMap = new HashMap<String, InterfaceAccount>();
                    resultJSONString = "OK";
                }
                else if ("2".equals(req.getParameter("initdata"))) {// 缓存同步到数据库
                    updateDbdata();
                }
                else if ("3".equals(req.getParameter("initdata"))) {// 查看缓存的数据
                    resultJSONString = JSONObject.toJSONString(interfaceAccountMap);
                }
            }
            else {
                jsonstr = req.getParameter("jsonStr") == null ? "" : req.getParameter("jsonStr");
                JSONObject jsonstrJSON = (JSONObject) JSON.parse(jsonstr);
                String partnerid = jsonstrJSON.getString("partnerid");//
                InterfaceAccount interfaceAccount = interfaceAccountMap.get(partnerid);
                if (interfaceAccount == null) {
                    interfaceAccount = getInterfaceAccountByLoginname(partnerid);
                    if (interfaceAccount != null && interfaceAccount.getKeystr() != null) {
                        interfaceAccountMap.put(partnerid, interfaceAccount);
                    }
                }
                String method = jsonstrJSON.getString("method");//
                String reqtime = jsonstrJSON.getString("reqtime");//
                String sign = jsonstrJSON.getString("sign");// 数字签名 =md5(partnerid+method+reqtime+md5(key))，其中key 由开放平台分配。md5 算法得到的字符串全部为小写
                String keystr = interfaceAccount.getKeystr().trim();
                String keyString = partnerid + method + reqtime + MD5Util.MD5Encode(keystr, "UTF-8");
                String checkSign = MD5Util.MD5Encode(keyString, "UTF-8");
                if (sign == null) {
                    resultJSONString = "{\"success\":false,\"code\":103,\"msg\":\"通用参数缺失\"}";
                }
                else if (sign.equals(checkSign)) {
                    if ("train_query".equals(method)) {//车票预订查询
                        if (interfaceAccount.getLimittrainnum() == null) {
                            interfaceAccount.setLimittrainnum(1000L);
                        }
                        if (interfaceAccount.getTrainnum() == null) {
                            interfaceAccount.setTrainnum(0L);
                        }
                        Long trainnum = 0L;
                        String mem_key = "trainSearch_trainnum_" + partnerid;
                        Object MemCached_o = MemCached.getInstance().get(mem_key);
                        if (MemCached_o == null) {
                            trainnum = interfaceAccount.getTrainnum();
                        }
                        else {
                            trainnum = Long.parseLong(MemCached_o.toString());//
                        }
                        // 如果限制次数不等于0&&使用次数大于登录限制次数,就返回错误
                        if (interfaceAccount.getLimittrainnum() != 0 && trainnum >= interfaceAccount.getLimittrainnum()) {
                            resultJSONString = "{\"success\":false,\"code\":203,\"msg\":\"剩余查询次数不足\"}";
                        }
                        else {
                            //在子线程中执行业务调用，并由其负责输出响应，主线程退出  
                            //                            
                            String s = jsonstrJSON.getString("from_station");// 出发站简码
                            String e = jsonstrJSON.getString("to_station");// 出发站简码
                            String d = jsonstrJSON.getString("train_date");// 乘车日期（yyyy-MM-dd）
                            
                            if (compareTime(d)) {
                            	// 清除缓存数据
                            	if ("4".equals(jsonstrJSON.getString("initdata"))) {
                            		String mcckey = s + e + d;
                            		mcckey = mcckey.replaceAll("-", "");
                            		MemCached.getInstance().delete(mcckey);
                            		println(mcckey + "清除缓存成功！");
                            	}
                            	String needdistance = jsonstrJSON.getString("needdistance") == null ? "0" : jsonstrJSON
                            			.getString("needdistance");// 是否需要里程(“1”需要；其他值不需要),默认不需要要
                            	String purposecodes = jsonstrJSON.getString("purpose_codes");// 订票类别 ADULT:普通票
                            	// 0X00:学生票
                            	List<Train> list = new ArrayList<Train>();
                            	list = get12306data(s, e, d, method, needdistance, purposecodes);
                            	if (list == null) {
                            		list = new ArrayList<Train>();
                            	}
                            	if (list.size() > 0) {
                            		resultJSONString = gettongchengTrain(list, method);
                            		resultJSONString = "{\"success\":true,\"code\":200,\"msg\":\"正常获得结果\",\"data\":"
                            				+ resultJSONString + "}";
                            	}
                            	else {
                            		resultJSONString = "{\"success\":false,\"code\":201,\"msg\":\"没有符合条件的车次信息\"}";
                            	}
                            	
                            	interfaceAccount.setTrainnum(interfaceAccount.getTrainnum() + 1);
                            	new Thread(new TrainSearchThread(mem_key, interfaceAccount.getTrainnum(), 1)).start();
							} else {
								resultJSONString = "{\"success\":false,\"code\":201,\"msg\":\"没有符合条件的车次信息\"}";
							}
                        }
                    }
                    else if ("get_train_zwdcx".equals(method)) {
                        WriteLog.write("t同程火车票接口_4.1正晚点查询", r1 + ":jsonStr:" + jsonstr);
                        resultJSONString = travelguide.getzwdinfo(jsonstrJSON, r1);
                        WriteLog.write("t同程火车票接口_4.1正晚点查询", r1 + ":result:" + resultJSONString);
                    }
                    //车次查询
                    else if ("get_train_info".equals(method)) {
                        WriteLog.write("t同程火车票接口_4.19车次查询", r1 + ":jsonStr:" + jsonstr);
                        resultJSONString = enquiries.getTrainEnquiries(jsonstrJSON, r1);
                        WriteLog.write("t同程火车票接口_4.19车次查询", r1 + ":result:" + resultJSONString);
                    }
                }
                else {
                    resultJSONString = "{\"success\":false,\"code\":105,\"msg\":\"加密错误\"}";
                }
            }
        }
        catch (UnsupportedEncodingException e2) {
            resultJSONString = "{\"success\":false,\"code\":202,\"msg\":\"查询失败\"}";
            e2.printStackTrace();
        }
        catch (IOException e1) {
            resultJSONString = "{\"success\":false,\"code\":202,\"msg\":\"查询失败\"}";
            e1.printStackTrace();
        }
        catch (Exception e3) {
            resultJSONString = "{\"success\":false,\"code\":202,\"msg\":\"查询失败\"}";
            e3.printStackTrace();
        }
        finally {
            out.write(resultJSONString);
            out.flush();
            out.close();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        updateDbdata();
    }

    /**
     * 把内存中的数据更新到数据库
     * 
     * @time 2015年5月16日 下午5:54:12
     * @author chendong
     */
    private void updateDbdata() {
        StringBuffer sqlString = new StringBuffer();
        for (Map.Entry<String, InterfaceAccount> entry : interfaceAccountMap.entrySet()) {
            sqlString.append("update T_INTERFACEACCOUNT set C_TRAINNUM='" + entry.getValue().getTrainnum()
                    + "' where C_USERNAME='" + entry.getKey() + "';");
        }
        // 关闭的时候把数据同步到数据库里
        Server.getInstance().getSystemService().excuteGiftBySql(sqlString.toString());
    }

    /**
     * 12306
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @param method
     *            余票查询：train_query_remain，车票预订查询： train_query
     * @param needdistance
     *            是否需要里程(“1”需要；其他值不需要),默认不需要要
     * @return
     * @time 2014年12月5日 上午11:04:35
     * @author chendong
     */
    private List<Train> get12306data(String startcity, String endcity, String time, String method, String needdistance,
            String purposecodes) {
        List<Train> sktrainlist = new ArrayList<Train>();
        FlightSearch param = new FlightSearch();
        try {
            param.setTravelType(method);// 这里把traveltype用作查列车时刻还是只查余票
            param.setGeneral(1);// 带缓存
            param.setTypeFlag(needdistance);// 是否需要里程(“1”需要；其他值不需要),默认不需要要
            sktrainlist = wrapper_12306.process("", startcity, endcity, time, param, purposecodes);
        }
        catch (Exception e) {
        }
        return sktrainlist;
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

            html = wrapper_tieyou.getHtml(startcity, endcity, time, param);

            startcity = wrapper_tieyou.get_station_code_name(startcity);
            endcity = wrapper_tieyou.get_station_code_name(endcity);
            sktrainlist = wrapper_tieyou.process(html, startcity, endcity, time, param);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sktrainlist;
    }

    /**
     * 把train转化为同程的对象
     * 
     * @param lists
     * @return
     * @time 2014年12月8日 下午7:02:23
     * @author chendong
     * @param method
     * @param issearchlicheng
     */
    public String gettongchengTrain(List<Train> lists, String method) {
        NameFilter filter = new NameFilter() {
            public String process(Object source, String name, Object value) {
                if (name.equals("traincode")) {
                    return "train_code";
                }
                else if (name.equals("startcity")) {
                    return "from_station_name";
                }
                else if (name.equals("endcity")) {
                    return "to_station_name";
                }
                else if (name.equals("starttime")) {
                    return "start_time";
                }
                else if (name.equals("endtime")) {
                    return "arrive_time";
                }
                else if (name.equals("costtime")) {
                    return "run_time";
                }
                else if (name.equals("wzyp")) {
                    return "wz_num";// 无座的余票数量
                }
                else if (name.equals("wzprice")) {
                    return "wz_price";// 无座票价
                }
                else if (name.equals("yzyp")) {
                    return "yz_num";// 硬座的余票数量
                }
                else if (name.equals("yzprice")) {
                    return "yz_price";// 硬座票价
                }
                else if (name.equals("rz2yp")) {
                    return "edz_num";// 二等座的余票数量
                }
                else if (name.equals("rz2price")) {
                    return "edz_price";// 二等座票价
                }
                else if (name.equals("rz1yp")) {
                    return "ydz_num";// 一等座的余票数量
                }
                else if (name.equals("rz1price")) {
                    return "ydz_price";// 一等座票价
                }
                else if (name.equals("ywyp")) {
                    return "yw_num";// 硬卧的余票数量
                }
                else if (name.equals("ywsprice")) {
                    return "yw_price";// 硬卧票价
                }
                else if (name.equals("rwyp")) {
                    return "rw_num";// 软卧余票数量
                }
                else if (name.equals("rwsprice")) {
                    return "rw_price";// 软卧票价
                }
                else if (name.equals("gwyp")) {
                    return "gjrw_num";// 高级软卧余票数量
                }
                else if (name.equals("gwxprice")) {
                    return "gjrw_price";// 高级软卧票价
                }
                else if (name.equals("rzyp")) {
                    return "rz_num";// 软座的余票数量
                }
                else if (name.equals("rzprice")) {
                    return "rz_price";// 软座的票价
                }
                else if (name.equals("swzyp")) {
                    return "swz_num";// 商务座的余票数据
                }
                else if (name.equals("swzprice")) {
                    return "swz_price";// 商务座票价
                }
                else if (name.equals("tdzyp")) {
                    return "tdz_num";// 特等座的余票数量
                }
                else if (name.equals("tdzprice")) {
                    return "tdz_price";// 特等座票价
                }
                else if (name.equals("traintype")) {
                    return "train_type";// 特等座票价
                }
                else if (name.equals("memo")) {
                    return "note";// 备注（起售时间）
                }
                return name;
            }
        };

        PropertyFilter filter1 = new PropertyFilter() {
            @Override
            public boolean apply(Object arg0, String arg1, Object arg2) {
                if (arg1.equals("sale_date_time") || arg1.equals("can_buy_now") || arg1.equals("arrive_days")
                        || arg1.equals("train_start_date") || arg1.equals("train_code")
                        || arg1.equals("access_byidcard") || arg1.equals("train_no") || arg1.equals("train_type")
                        || arg1.equals("from_station_name") || arg1.equals("from_station_code")
                        || arg1.equals("to_station_name") || arg1.equals("to_station_code")
                        || arg1.equals("start_station_name") || arg1.equals("end_station_name")
                        || arg1.equals("start_time") || arg1.equals("arrive_time") || arg1.equals("run_time")
                        || arg1.equals("run_time_minute") || arg1.equals("gjrw_num") || arg1.equals("gjrw_price")
                        || arg1.equals("qtxb_num") || arg1.equals("qtxb_price") || arg1.equals("rw_num")
                        || arg1.equals("rw_price") || arg1.equals("rz_num") || arg1.equals("rz_price")
                        || arg1.equals("swz_num") || arg1.equals("swz_price") || arg1.equals("tdz_num")
                        || arg1.equals("tdz_price") || arg1.equals("wz_num") || arg1.equals("wz_price")
                        || arg1.equals("yw_num") || arg1.equals("yw_price") || arg1.equals("yz_num")
                        || arg1.equals("yz_price") || arg1.equals("edz_num") || arg1.equals("edz_price")
                        || arg1.equals("ydz_num") || arg1.equals("ydz_price") || arg1.equals("note")
                        || arg1.equals("distance")) {
                    return true;
                }
                return false;
            }
        };

        if ("train_query_remain".equals(method)) {// 余票查询
            filter1 = new PropertyFilter() {
                @Override
                public boolean apply(Object arg0, String arg1, Object arg2) {
                    if (arg1.equals("sale_date_time") || arg1.equals("can_buy_now") || arg1.equals("arrive_days")
                            || arg1.equals("train_start_date") || arg1.equals("train_code")
                            || arg1.equals("access_byidcard") || arg1.equals("train_no") || arg1.equals("train_type")
                            || arg1.equals("from_station_name") || arg1.equals("from_station_code")
                            || arg1.equals("to_station_name") || arg1.equals("to_station_code")
                            || arg1.equals("start_station_name") || arg1.equals("end_station_name")
                            || arg1.equals("start_time") || arg1.equals("arrive_time") || arg1.equals("run_time")
                            || arg1.equals("run_time_minute") || arg1.equals("gjrw_num") || arg1.equals("qtxb_num")
                            || arg1.equals("rw_num") || arg1.equals("rz_num") || arg1.equals("swz_num")
                            || arg1.equals("tdz_num") || arg1.equals("wz_num") || arg1.equals("yw_num")
                            || arg1.equals("yz_num") || arg1.equals("edz_num") || arg1.equals("ydz_num")
                            || arg1.equals("distance")) {
                        return true;
                    }
                    return false;
                }
            };
        }
        String reslut1 = "{}";
        reslut1 = JSON.toJSONString(lists, filter);
        JSONArray o1 = JSON.parseArray(reslut1);
        return JSON.toJSONString(o1, filter1);
    }

    public String getDefault_data() {
        return default_data;
    }

    public void setDefault_data(String default_data) {
        this.default_data = default_data;
    }

    public static void cachetrainprice(String[] ss) {
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(5);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        for (int i = 0; i < ss.length; i++) {
            // 将线程放入池中进行执行
            t1 = new SearchThread(ss[i], i);
            pool.execute(t1);
        }
        // 关闭线程池
        pool.shutdown();

    }

    public void gettestcity() {
        String station_names_1 = "bjb|北京北|VAP|beijingbei|bjb|0@bjd|北京东|BOP|beijingdong|bjd|1@bji|北京|BJP|beijing|bj|2@bjn|北京南|VNP|beijingnan|bjn|3@bjx|北京西|BXP|beijingxi|bjx|4@gzn|广州南|IZQ|guangzhounan|gzn|5@cqb|重庆北|CUW|chongqingbei|cqb|6@cqi|重庆|CQW|chongqing|cq|7@cqn|重庆南|CRW|chongqingnan|cqn|8@gzd|广州东|GGQ|guangzhoudong|gzd|9@sha|上海|SHH|shanghai|sh|10@shn|上海南|SNH|shanghainan|shn|11@shq|上海虹桥|AOH|shanghaihongqiao|shhq|12@shx|上海西|SXH|shanghaixi|shx|13@tjb|天津北|TBP|tianjinbei|tjb|14@tji|天津|TJP|tianjin|tj|15@tjn|天津南|TIP|tianjinnan|tjn|16@tjx|天津西|TXP|tianjinxi|tjx|17@cch|长春|CCT|changchun|cc|18@ccn|长春南|CET|changchunnan|ccn|19@ccx|长春西|CRT|changchunxi|ccx|20@cdd|成都东|ICW|chengdudong|cdd|21@cdn|成都南|CNW|chengdunan|cdn|22@cdu|成都|CDW|chengdu|cd|23@csh|长沙|CSQ|changsha|cs|24@csn|长沙南|CWQ|changshanan|csn|25@fzh|福州|FZS|fuzhou|fz|26@fzn|福州南|FYS|fuzhounan|fzn|27@gya|贵阳|GIW|guiyang|gy|28@gzh|广州|GZQ|guangzhou|gz|29@gzx|广州西|GXQ|guangzhouxi|gzx|30@heb|哈尔滨|HBB|haerbin|heb|31@hed|哈尔滨东|VBB|haerbindong|hebd|32@hex|哈尔滨西|VAB|haerbinxi|hebx|33@hfe|合肥|HFH|hefei|hf|34@hhd|呼和浩特东|NDC|huhehaotedong|hhhtd|35@hht|呼和浩特|HHC|huhehaote|hhht|36@hkd|海口东|HMQ|haikoudong|hkd|37@hko|海口|VUQ|haikou|hk|38@hzd|杭州东|HGH|hangzhoudong|hzd|39@hzh|杭州|HZH|hangzhou|hz|40@hzn|杭州南|XHH|hangzhounan|hzn|41@jna|济南|JNK|jinan|jn|42@jnd|济南东|JAK|jinandong|jnd|43@jnx|济南西|JGK|jinanxi|jnx|44@kmi|昆明|KMM|kunming|km|45@kmx|昆明西|KXM|kunmingxi|kmx|46@lsa|拉萨|LSO|lasa|ls|47@lzd|兰州东|LVJ|lanzhoudong|lzd|48@lzh|兰州|LZJ|lanzhou|lz|49@lzx|兰州西|LAJ|lanzhouxi|lzx|50@nch|南昌|NCG|nanchang|nc|51@nji|南京|NJH|nanjing|nj|52@njn|南京南|NKH|nanjingnan|njn|53@nni|南宁|NNZ|nanning|nn|54@sjb|石家庄北|VVP|shijiazhuangbei|sjzb|55@sjz|石家庄|SJP|shijiazhuang|sjz|56@sya|沈阳|SYT|shenyang|sy|57@syb|沈阳北|SBT|shenyangbei|syb|58@syd|沈阳东|SDT|shenyangdong|syd|59@tyb|太原北|TBV|taiyuanbei|tyb|60@tyd|太原东|TDV|taiyuandong|tyd|61@tyu|太原|TYV|taiyuan|ty|62@wha|武汉|WHN|wuhan|wh|63@wjx|王家营西|KNM|wangjiayingxi|wjyx|64@wln|乌鲁木齐南|WMR|wulumuqinan|wlmq|65@xab|西安北|EAY|xianbei|xab|66@xan|西安|XAY|xian|xa|67@xan|西安南|CAY|xiannan|xan|68@xnx|西宁西|XXO|xiningxi|xnx|69@ych|银川|YIJ|yinchuan|yc|70@zzh|郑州|ZZF|zhengzhou|zz|71@aes|阿尔山|ART|aershan|aes|72@aka|安康|AKY|ankang|ak|73@aks|阿克苏|ASR|akesu|aks|74@alh|阿里河|AHX|alihe|alh|75@alk|阿拉山口|AKR|alashankou|alsk|76@api|安平|APT|anping|ap|77@aqi|安庆|AQH|anqing|aq|78@ash|安顺|ASW|anshun|as|79@ash|鞍山|AST|anshan|as|80@aya|安阳|AYF|anyang|ay|81@ban|北安|BAB|beian|ba|82@bbu|蚌埠|BBH|bengbu|bb|83@bch|白城|BCT|baicheng|bc|84@bha|北海|BHZ|beihai|bh|85@bhe|白河|BEL|baihe|bh|86@bji|白涧|BAP|baijian|bj|87@bji|宝鸡|BJY|baoji|bj|88@bji|滨江|BJB|binjiang|bj|89@bkt|博克图|BKX|boketu|bkt|90@bse|百色|BIZ|baise|bs|91@bss|白山市|HJL|baishanshi|bss|92@bta|北台|BTT|beitai|bt|93@btd|包头东|BDC|baotoudong|btd|94@bto|包头|BTC|baotou|bt|95@bts|北屯市|BXR|beitunshi|bts|96@bxi|本溪|BXT|benxi|bx|97@byb|白云鄂博|BEC|baiyunebo|byeb|98@byx|白银西|BXJ|baiyinxi|byx|99@bzh|亳州|BZH|bozhou|bz|100@cbi|赤壁|CBN|chibi|cb|101@cde|常德|VGQ|changde|cd|102@cde|承德|CDP|chengde|cd|103@cdi|长甸|CDT|changdian|cd|104@cfe|赤峰|CFD|chifeng|cf|105@cli|茶陵|CDG|chaling|cl|106@cna|苍南|CEH|cangnan|cn|107@cpi|昌平|CPP|changping|cp|108@cre|崇仁|CRG|chongren|cr|109@ctu|昌图|CTT|changtu|ct|110@ctz|长汀镇|CDB|changtingzhen|ctz|111@cxi|曹县|CXK|caoxian|cx|112@cxi|楚雄|COM|chuxiong|cx|113@cxt|陈相屯|CXT|chenxiangtun|cxt|114@czb|长治北|CBF|changzhibei|czb|115@czh|长征|CZJ|changzheng|cz|116@czh|池州|IYH|chizhou|cz|117@czh|常州|CZH|changzhou|cz|118@czh|郴州|CZQ|chenzhou|cz|119@czh|长治|CZF|changzhi|cz|120@czh|沧州|COP|cangzhou|cz|121@czu|崇左|CZZ|chongzuo|cz|122@dab|大安北|RNT|daanbei|dab|123@dch|大成|DCT|dacheng|dc|124@ddo|丹东|DUT|dandong|dd|125@dfh|东方红|DFB|dongfanghong|dfh|126@dgd|东莞东|DMQ|dongguandong|dgd|127@dhs|大虎山|DHD|dahushan|dhs|128@dhu|敦煌|DHJ|dunhuang|dh|129@dhu|敦化|DHL|dunhua|dh|130@dhu|德惠|DHT|dehui|dh|131@djc|东京城|DJB|dongjingcheng|djc|132@dji|大涧|DFP|dajian|dj|133@djy|都江堰|DDW|dujiangyan|djy|134@dlb|大连北|DFT|dalianbei|dlb|135@dli|大理|DKM|dali|dl|136@dli|大连|DLT|dalian|dl|137@dna|定南|DNG|dingnan|dn|138@dqi|大庆|DZX|daqing|dq|139@dsh|东胜|DOC|dongsheng|ds|140@dsq|大石桥|DQT|dashiqiao|dsq|141@dto|大同|DTV|datong|dt|142@dyi|东营|DPK|dongying|dy|143@dys|大杨树|DUX|dayangshu|dys|144@dyu|都匀|RYW|duyun|dy|145@dzh|邓州|DOF|dengzhou|dz|146@dzh|达州|RXW|dazhou|dz|147@dzh|德州|DZP|dezhou|dz|148@ejn|额济纳|EJC|ejina|ejn|149@eli|二连|RLC|erlian|el|150@esh|恩施|ESN|enshi|es|151@fdi|福鼎|FES|fuding|fd|152@fld|风陵渡|FLV|fenglingdu|fld|153@fli|涪陵|FLW|fuling|fl|154@flj|富拉尔基|FRX|fulaerji|flej|155@fsb|抚顺北|FET|fushunbei|fsb|156@fsh|佛山|FSQ|foshan|fs|157@fxi|阜新|FXD|fuxin|fx|158@fya|阜阳|FYH|fuyang|fy|159@gem|格尔木|GRO|geermu|gem|160@gha|广汉|GHW|guanghan|gh|161@gji|古交|GJV|gujiao|gj|162@glb|桂林北|GBZ|guilinbei|glb|163@gli|古莲|GRX|gulian|gl|164@gli|桂林|GLZ|guilin|gl|165@gsh|固始|GXN|gushi|gs|166@gsh|广水|GSN|guangshui|gs|167@gta|干塘|GNJ|gantang|gt|168@gyu|广元|GYW|guangyuan|gy|169@gzb|广州北|GBQ|guangzhoubei|gzb|170@gzh|赣州|GZG|ganzhou|gz|171@gzl|公主岭|GLT|gongzhuling|gzl|172@gzn|公主岭南|GBT|gongzhulingnan|gzln|173@han|淮安|AUH|huaian|ha|174@hbe|鹤北|HMB|hebei|hb|175@hbe|淮北|HRH|huaibei|hb|176@hbi|淮滨|HVN|huaibin|hb|177@hbi|河边|HBV|hebian|hb|178@hch|潢川|KCN|huangchuan|hc|179@hch|韩城|HCY|hancheng|hc|180@hda|邯郸|HDP|handan|hd|181@hdz|横道河子|HDB|hengdaohezi|hdhz|182@hga|鹤岗|HGB|hegang|hg|183@hgt|皇姑屯|HTT|huanggutun|hgt|184@hgu|红果|HEM|hongguo|hg|185@hhe|黑河|HJB|heihe|hh|186@hhu|怀化|HHQ|huaihua|hh|187@hko|汉口|HKN|hankou|hk|188@hld|葫芦岛|HLD|huludao|hld|189@hle|海拉尔|HRX|hailaer|hle|190@hll|霍林郭勒|HWD|huolinguole|hlgl|191@hlu|海伦|HLB|hailun|hl|192@hma|侯马|HMV|houma|hm|193@hmi|哈密|HMR|hami|hm|194@hna|淮南|HAH|huainan|hn|195@hna|桦南|HNB|huanan|hn|196@hnx|海宁西|EUH|hainingxi|hnx|197@hqi|鹤庆|HQM|heqing|hq|198@hrb|怀柔北|HBP|huairoubei|hrb|199@hro|怀柔|HRP|huairou|hr|200@hsd|黄石东|OSN|huangshidong|hsd|201@hsh|华山|HSY|huashan|hs|202@hsh|黄石|HSN|huangshi|hs|203@hsh|黄山|HKH|huangshan|hs|204@hsh|衡水|HSP|hengshui|hs|205@hya|衡阳|HYQ|hengyang|hy|206@hze|菏泽|HIK|heze|hz|207@hzh|贺州|HXZ|hezhou|hz|208@hzh|汉中|HOY|hanzhong|hz|209@hzh|惠州|HCQ|huizhou|hz|210@jan|吉安|VAG|jian|ja|211@jan|集安|JAL|jian|ja|212@jbc|江边村|JBG|jiangbiancun|jbc|213@jch|晋城|JCF|jincheng|jc|214@jcj|金城江|JJZ|jinchengjiang|jcj|215@jdz|景德镇|JCG|jingdezhen|jdz|216@jfe|嘉峰|JFF|jiafeng|jf|217@jgq|加格达奇|JGX|jiagedaqi|jgdq|218@jgs|井冈山|JGG|jinggangshan|jgs|219@jhe|蛟河|JHL|jiaohe|jh|220@jhn|金华南|RNH|jinhuanan|jhn|221@jhx|金华西|JBH|jinhuaxi|jhx|222@jji|九江|JJG|jiujiang|jj|223@jli|吉林|JLL|jilin|jl|224@jme|荆门|JMN|jingmen|jm|225@jms|佳木斯|JMB|jiamusi|jms|226@jni|济宁|JIK|jining|jn|227@jnn|集宁南|JAC|jiningnan|jnn|228@jqu|酒泉|JQJ|jiuquan|jq|229@jsh|江山|JUH|jiangshan|js|230@jsh|吉首|JIQ|jishou|js|231@jta|九台|JTL|jiutai|jt|232@jts|镜铁山|JVJ|jingtieshan|jts|233@jxi|鸡西|JXB|jixi|jx|234@jxi|蓟县|JKP|jixian|jx|235@jxx|绩溪县|JRH|jixixian|jxx|236@jyg|嘉峪关|JGJ|jiayuguan|jyg|237@jyo|江油|JFW|jiangyou|jy|238@jzh|锦州|JZD|jinzhou|jz|239@jzh|金州|JZT|jinzhou|jz|240@kel|库尔勒|KLR|kuerle|kel|241@kfe|开封|KFF|kaifeng|kf|242@kla|岢岚|KLV|kelan|kl|243@kli|凯里|KLW|kaili|kl|244@ksh|喀什|KSR|kashi|ks|245@ksn|昆山南|KNH|kunshannan|ksn|246@ktu|奎屯|KTR|kuitun|kt|247@kyu|开原|KYT|kaiyuan|ky|248@lan|六安|UAH|liuan|la|249@lba|灵宝|LBF|lingbao|lb|250@lcg|芦潮港|UCH|luchaogang|lcg|251@lch|隆昌|LCW|longchang|lc|252@lch|陆川|LKZ|luchuan|lc|253@lch|利川|LCN|lichuan|lc|254@lch|临川|LCG|linchuan|lc|255@lch|潞城|UTP|lucheng|lc|256@lda|鹿道|LDL|ludao|ld|257@ldi|娄底|LDQ|loudi|ld|258@lfe|临汾|LFV|linfen|lf|259@lgz|良各庄|LGP|lianggezhuang|lgz|260@lhe|临河|LHC|linhe|lh|261@lhe|漯河|LON|luohe|lh|262@lhu|绿化|LWJ|lvhua|lh|263@lhu|隆化|UHP|longhua|lh|264@lji|丽江|LHM|lijiang|lj|265@lji|临江|LQL|linjiang|lj|266@lji|龙井|LJL|longjing|lj|267@lli|吕梁|LHV|lvliang|ll|268@lli|醴陵|LLG|liling|ll|269@lln|柳林南|LKV|liulinnan|lln|270@lpi|滦平|UPP|luanping|lp|271@lps|六盘水|UMW|liupanshui|lps|272@lqi|灵丘|LVV|lingqiu|lq|273@lsh|旅顺|LST|lvshun|ls|274@lxi|陇西|LXJ|longxi|lx|275@lxi|澧县|LEQ|lixian|lx|276@lxi|兰溪|LWH|lanxi|lx|277@lxi|临西|UEP|linxi|lx|278@lya|龙岩|LYS|longyan|ly|279@lya|耒阳|LYQ|leiyang|ly|280@lya|洛阳|LYF|luoyang|ly|281@lyd|洛阳东|LDF|luoyangdong|lyd|282@lyd|连云港东|UKH|lianyungangdong|lygd|283@lyi|临沂|LVK|linyi|ly|284@lym|洛阳龙门|LLF|luoyanglongmen|lylm|285@lyu|柳园|DHR|liuyuan|ly|286@lyu|凌源|LYD|lingyuan|ly|287@lyu|辽源|LYL|liaoyuan|ly|288@lzh|立志|LZX|lizhi|lz|289@lzh|柳州|LZZ|liuzhou|lz|290@lzh|辽中|LZD|liaozhong|lz|291@mch|麻城|MCN|macheng|mc|292@mdh|免渡河|MDX|mianduhe|mdh|293@mdj|牡丹江|MDB|mudanjiang|mdj|294@meg|莫尔道嘎|MRX|moerdaoga|medg|295@mgu|满归|MHX|mangui|mg|296@mgu|明光|MGH|mingguang|mg|297@mhe|漠河|MVX|mohe|mh|298@mmd|茂名东|MDQ|maomingdong|mmd|299@mmi|茂名|MMZ|maoming|mm|300@msh|密山|MSB|mishan|ms|301@msj|马三家|MJT|masanjia|msj|302@mwe|麻尾|VAW|mawei|mw|303@mya|绵阳|MYW|mianyang|my|304@mzh|梅州|MOQ|meizhou|mz|305@mzl|满洲里|MLX|manzhouli|mzl|306@nbd|宁波东|NVH|ningbodong|nbd|307@nbo|宁波|NGH|ningbo|nb|308@nch|南岔|NCB|nancha|nc|309@nch|南充|NCW|nanchong|nc|310@nda|南丹|NDZ|nandan|nd|311@ndm|南大庙|NMP|nandamiao|ndm|312@nfe|南芬|NFT|nanfen|nf|313@nhe|讷河|NHX|nehe|nh|314@nji|嫩江|NGX|nenjiang|nj|315@nji|内江|NJW|neijiang|nj|316@npi|南平|NPS|nanping|np|317@nto|南通|NUH|nantong|nt|318@nya|南阳|NFF|nanyang|ny|319@nzs|碾子山|NZX|nianzishan|nzs|320@pds|平顶山|PEN|pingdingshan|pds|321@pji|盘锦|PVD|panjin|pj|322@pli|平凉|PIJ|pingliang|pl|323@pln|平凉南|POJ|pingliangnan|pln|324@pqu|平泉|PQP|pingquan|pq|325@psh|坪石|PSQ|pingshi|ps|326@pxi|萍乡|PXG|pingxiang|px|327@pxi|凭祥|PXZ|pingxiang|px|328@pxx|郫县西|PCW|pixianxi|pxx|329@pzh|攀枝花|PRW|panzhihua|pzh|330@qch|蕲春|QRN|qichun|qc|331@qcs|青城山|QSW|qingchengshan|qcs|332@qda|青岛|QDK|qingdao|qd|333@qhc|清河城|QYP|qinghecheng|qhc|334@qji|黔江|QNW|qianjiang|qj|335@qji|曲靖|QJM|qujing|qj|336@qjz|前进镇|QEB|qianjinzhen|qjz|337@qqe|齐齐哈尔|QHX|qiqihaer|qqhe|338@qth|七台河|QTB|qitaihe|qth|339@qxi|沁县|QVV|qinxian|qx|340@qzd|泉州东|QRS|quanzhoudong|qzd|341@qzh|泉州|QYS|quanzhou|qz|342@qzh|衢州|QEH|quzhou|qz|343@ran|融安|RAZ|rongan|ra|344@rjg|汝箕沟|RQJ|rujigou|rqg|345@rji|瑞金|RJG|ruijin|rj|346@rzh|日照|RZK|rizhao|rz|347@scp|双城堡|SCB|shuangchengpu|scb|348@sfh|绥芬河|SFB|suifenhe|sfh|349@sgd|韶关东|SGQ|shaoguandong|sgd|350@shg|山海关|SHD|shanhaiguan|shg|351@shu|绥化|SHB|suihua|sh|352@sjf|三间房|SFX|sanjianfang|sjf|353@sjt|苏家屯|SXT|sujiatun|sjt|354@sla|舒兰|SLL|shulan|sl|355@smi|三明|SMS|sanming|sm|356@smu|神木|OMY|shenmu|sm|357@smx|三门峡|SMF|sanmenxia|smx|358@sna|商南|ONY|shangnan|sn|359@sni|遂宁|NIW|suining|sn|360@spi|四平|SPT|siping|sp|361@sqi|商丘|SQF|shangqiu|sq|362@sra|上饶|SRG|shangrao|sr|363@ssh|韶山|SSQ|shaoshan|ss|364@sso|宿松|OAH|susong|ss|365@sto|汕头|OTQ|shantou|st|366@swu|邵武|SWS|shaowu|sw|367@sxi|涉县|OEP|shexian|sx|368@sya|三亚|SEQ|sanya|sy|369@sya|邵阳|SYQ|shaoyang|sy|370@sya|十堰|SNN|shiyan|sy|371@sys|双鸭山|SSB|shuangyashan|sys|372@syu|松原|VYT|songyuan|sy|373@szh|深圳|SZQ|shenzhen|sz|374@szh|苏州|SZH|suzhou|sz|375@szh|随州|SZN|suizhou|sz|376@szh|宿州|OXH|suzhou|sz|377@szh|朔州|SUV|shuozhou|sz|378@szx|深圳西|OSQ|shenzhenxi|szx|379@tba|塘豹|TBQ|tangbao|tb|380@teq|塔尔气|TVX|taerqi|teq|381@tgu|潼关|TGY|tongguan|tg|382@tgu|塘沽|TGP|tanggu|tg|383@the|塔河|TXX|tahe|th|384@thu|通化|THL|tonghua|th|385@tla|泰来|TLX|tailai|tl|386@tlf|吐鲁番|TFR|tulufan|tlf|387@tli|通辽|TLD|tongliao|tl|388@tli|铁岭|TLT|tieling|tl|389@tlz|陶赖昭|TPT|taolaizhao|tlz|390@tme|图们|TML|tumen|tm|391@tre|铜仁|RDQ|tongren|tr|392@tsb|唐山北|FUP|tangshanbei|tsb|393@tsf|田师府|TFT|tianshifu|tsf|394@tsh|泰山|TAK|taishan|ts|395@tsh|唐山|TSP|tangshan|ts|396@tsh|天水|TSJ|tianshui|ts|397@typ|通远堡|TYT|tongyuanpu|tyb|398@tys|太阳升|TQT|taiyangsheng|tys|399@tzh|泰州|UTH|taizhou|tz|400@tzi|桐梓|TZW|tongzi|tz|401@tzx|通州西|TAP|tongzhouxi|tzx|402@wch|五常|WCB|wuchang|wc|403@wch|武昌|WCN|wuchang|wc|404@wfd|瓦房店|WDT|wafangdian|wfd|405@wha|威海|WKK|weihai|wh|406@whu|芜湖|WHH|wuhu|wh|407@whx|乌海西|WXC|wuhaixi|whx|408@wjt|吴家屯|WJT|wujiatun|wjt|409@wlo|武隆|WLW|wulong|wl|410@wlt|乌兰浩特|WWT|wulanhaote|wlht|411@wna|渭南|WNY|weinan|wn|412@wsh|威舍|WSM|weishe|ws|413@wts|歪头山|WIT|waitoushan|wts|414@wwe|武威|WUJ|wuwei|ww|415@wwn|武威南|WWJ|wuweinan|wwn|416@wxi|无锡|WXH|wuxi|wx|417@wxi|乌西|WXR|wuxi|wx|418@wyl|乌伊岭|WPB|wuyiling|wyl|419@wys|武夷山|WAS|wuyishan|wys|420@wyu|万源|WYY|wanyuan|wy|421@wzh|万州|WYW|wanzhou|wz|422@wzh|梧州|WZZ|wuzhou|wz|423@wzh|温州|RZH|wenzhou|wz|424@wzn|温州南|VRH|wenzhounan|wzn|425@xch|西昌|ECW|xichang|xc|426@xch|许昌|XCF|xuchang|xc|427@xcn|西昌南|ENW|xichangnan|xcn|428@xfa|香坊|XFB|xiangfang|xf|429@xga|轩岗|XGV|xuangang|xg|430@xgu|兴国|EUG|xingguo|xg|431@xha|宣汉|XHY|xuanhan|xh|432@xhu|新会|EFQ|xinhui|xh|433@xhu|新晃|XLQ|xinhuang|xh|434@xlt|锡林浩特|XTC|xilinhaote|xlht|435@xlx|兴隆县|EXP|xinglongxian|xlx|436@xmb|厦门北|XKS|xiamenbei|xmb|437@xme|厦门|XMS|xiamen|xm|438@xmq|厦门高崎|XBS|xiamengaoqi|xmgq|439@xsh|秀山|ETW|xiushan|xs|440@xsh|小市|XST|xiaoshi|xs|441@xta|向塘|XTG|xiangtang|xt|442@xwe|宣威|XWM|xuanwei|xw|443@xxi|新乡|XXF|xinxiang|xx|444@xya|信阳|XUN|xinyang|xy|445@xya|咸阳|XYY|xianyang|xy|446@xya|襄阳|XFN|xiangyang|xy|447@xyc|熊岳城|XYT|xiongyuecheng|xyc|448@xyi|兴义|XRZ|xingyi|xy|449@xyi|新沂|VIH|xinyi|xy|450@xyu|新余|XUG|xinyu|xy|451@xzh|徐州|XCH|xuzhou|xz|452@yan|延安|YWY|yanan|ya|453@ybi|宜宾|YBW|yibin|yb|454@ybn|亚布力南|YWB|yabulinan|ybln|455@ybs|叶柏寿|YBD|yebaishou|ybs|456@ycd|宜昌东|HAN|yichangdong|ycd|457@ych|永川|YCW|yongchuan|yc|458@ych|宜昌|YCN|yichang|yc|459@ych|盐城|AFH|yancheng|yc|460@ych|运城|YNV|yuncheng|yc|461@ych|伊春|YCB|yichun|yc|462@yci|榆次|YCV|yuci|yc|463@ycu|杨村|YBP|yangcun|yc|464@ycx|宜春西|YCG|yichunxi|ycx|465@yes|伊尔施|YET|yiershi|yes|466@yga|燕岗|YGW|yangang|yg|467@yji|永济|YIV|yongji|yj|468@yji|延吉|YJL|yanji|yj|469@yko|营口|YKT|yingkou|yk|470@yks|牙克石|YKX|yakeshi|yks|471@yli|阎良|YNY|yanliang|yl|472@yli|玉林|YLZ|yulin|yl|473@yli|榆林|ALY|yulin|yl|474@ymp|一面坡|YPB|yimianpo|ymp|475@yni|伊宁|YMR|yining|yn|476@ypg|阳平关|YAY|yangpingguan|ypg|477@ypi|玉屏|YZW|yuping|yp|478@ypi|原平|YPV|yuanping|yp|479@yqi|延庆|YNP|yanqing|yq|480@yqq|阳泉曲|YYV|yangquanqu|yqq|481@yqu|玉泉|YQB|yuquan|yq|482@yqu|阳泉|AQP|yangquan|yq|483@ysh|玉山|YNG|yushan|ys|484@ysh|营山|NUW|yingshan|ys|485@ysh|燕山|AOP|yanshan|ys|486@ysh|榆树|YRT|yushu|ys|487@yta|鹰潭|YTG|yingtan|yt|488@yta|烟台|YAK|yantai|yt|489@yth|伊图里河|YEX|yitulihe|ytlh|490@ytx|玉田县|ATP|yutianxian|ytx|491@ywu|义乌|YWH|yiwu|yw|492@yxi|阳新|YON|yangxin|yx|493@yxi|义县|YXD|yixian|yx|494@yya|益阳|AEQ|yiyang|yy|495@yya|岳阳|YYQ|yueyang|yy|496@yzh|永州|AOQ|yongzhou|yz|497@yzh|扬州|YLH|yangzhou|yz|498@zbo|淄博|ZBK|zibo|zb|499@zcd|镇城底|ZDV|zhenchengdi|zcd|500@zgo|自贡|ZGW|zigong|zg|501@zha|珠海|ZHQ|zhuhai|zh|502@zhb|珠海北|ZIQ|zhuhaibei|zhb|503@zji|湛江|ZJZ|zhanjiang|zj|504@zji|镇江|ZJH|zhenjiang|zj|505@zjj|张家界|DIQ|zhangjiajie|zjj|506@zjk|张家口|ZKP|zhangjiakou|zjk|507@zjn|张家口南|ZMP|zhangjiakounan|zjkn|508@zko|周口|ZKN|zhoukou|zk|509@zlm|哲里木|ZLC|zhelimu|zlm|510@zlt|扎兰屯|ZTX|zhalantun|zlt|511@zmd|驻马店|ZDN|zhumadian|zmd|512@zqi|肇庆|ZVQ|zhaoqing|zq|513@zsz|周水子|ZIT|zhoushuizi|zsz|514@zto|昭通|ZDW|zhaotong|zt|515@zwe|中卫|ZWJ|zhongwei|zw|516@zya|资阳|ZYW|ziyang|zy|517@zyi|遵义|ZIW|zunyi|zy|518@zzh|枣庄|ZEK|zaozhuang|zz|519@zzh|资中|ZZW|zizhong|zz|520@zzh|株洲|ZZQ|zhuzhou|zz|521@zzx|枣庄西|ZFK|zaozhuangxi|zzx|522@aax|昂昂溪|AAX|angangxi|aax|523@ach|阿城|ACB|acheng|ac|524@ada|安达|ADX|anda|ad|525@ade|安德|ARW|ande|ad|526@adi|安定|ADP|anding|ad|527@agu|安广|AGT|anguang|ag|528@ahe|艾河|AHP|aihe|ah|529@ahu|安化|PKQ|anhua|ah|530@ajc|艾家村|AJJ|aijiacun|ajc|531@aji|鳌江|ARH|aojiang|aj|532@aji|安家|AJB|anjia|aj|533@aji|阿金|AJD|ajin|aj|534@akt|阿克陶|AER|aketao|akt|535@aky|安口窑|AYY|ankouyao|aky|536@alg|敖力布告|ALD|aolibugao|albg|537@alo|安龙|AUZ|anlong|al|538@als|阿龙山|ASX|alongshan|als|539@alu|安陆|ALN|anlu|al|540@ame|阿木尔|JTX|amuer|ame|541@anz|阿南庄|AZM|ananzhuang|anz|542@aqx|安庆西|APH|anqingxi|aqx|543@asx|鞍山西|AXT|anshanxi|asx|544@ata|安塘|ATV|antang|at|545@atb|安亭北|ASH|antingbei|atb|546@ats|阿图什|ATR|atushi|ats|547@atu|安图|ATL|antu|at|548@axi|安溪|AXS|anxi|ax|549@bao|博鳌|BWQ|boao|ba|550@bbe|北碚|BPW|beibei|bb|551@bbg|白壁关|BGV|baibiguan|bbg|552@bbn|蚌埠南|BMH|bengbunan|bbn|553@bch|巴楚|BCR|bachu|bc|554@bch|板城|BUP|bancheng|bc|555@bdh|北戴河|BEP|beidaihe|bdh|556@bdi|保定|BDP|baoding|bd|557@bdi|宝坻|BPP|baodi|bd|558@bdl|八达岭|ILP|badaling|bdl|559@bdo|巴东|BNN|badong|bd|560@bgu|柏果|BGM|baiguo|bg|561@bha|布海|BUT|buhai|bh|562@bhd|白河东|BIY|baihedong|bhd|563@bho|贲红|BVC|benhong|bh|564@bhs|宝华山|BWH|baohuashan|bhs|565@bhx|白河县|BEY|baihexian|bhx|566@bjg|白芨沟|BJJ|baijigou|bjg|567@bjg|碧鸡关|BJM|bijiguan|bjg|568@bji|北滘|IBQ|beijiao|b|569@bji|碧江|BLQ|bijiang|bj|570@bjp|白鸡坡|BBM|baijipo|bjp|571@bjs|笔架山|BSB|bijiashan|bjs|572@bjt|八角台|BTD|bajiaotai|bjt|573@bka|保康|BKD|baokang|bk|574@bkp|白奎堡|BKB|baikuipu|bkb|575@bla|白狼|BAT|bailang|bl|576@bla|百浪|BRZ|bailang|bl|577@ble|博乐|BOR|bole|bl|578@blg|宝拉格|BQC|baolage|blg|579@bli|巴林|BLX|balin|bl|580@bli|宝林|BNB|baolin|bl|581@bli|北流|BOZ|beiliu|bl|582@bli|勃利|BLB|boli|bl|583@blk|布列开|BLR|buliekai|blk|584@bls|宝龙山|BND|baolongshan|bls|585@bmc|八面城|BMD|bamiancheng|bmc|586@bmq|班猫箐|BNM|banmaoqing|bmj|587@bmt|八面通|BMB|bamiantong|bmt|588@bmz|北马圈子|BRP|beimaquanzi|bmqz|589@bpn|北票南|RPD|beipiaonan|bpn|590@bqi|白旗|BQP|baiqi|bq|591@bql|宝泉岭|BQB|baoquanling|bql|592@bqu|白泉|BQL|baiquan|bq|593@bsh|白沙|BSW|baisha|bs|594@bsh|巴山|BAY|bashan|bs|595@bsj|白水江|BSY|baishuijiang|bsj|596@bsp|白沙坡|BPM|baishapo|bsp|597@bss|白石山|BAL|baishishan|bss|598@bsz|白水镇|BUM|baishuizhen|bsz|599@bti|坂田|BTQ|bantian|bt|600@bto|泊头|BZP|botou|bt|601@btu|北屯|BYP|beitun|bt|602@bxh|本溪湖|BHT|benxihu|bxh|603@bxi|博兴|BXK|boxing|bx|604@bxt|八仙筒|VXD|baxiantong|bxt|605@byg|白音察干|BYC|baiyinchagan|bycg|606@byh|背荫河|BYB|beiyinhe|byh|607@byi|北营|BIV|beiying|by|608@byl|巴彦高勒|BAC|bayangaole|bygl|609@byl|白音他拉|BID|baiyintala|bytl|610@byq|鲅鱼圈|BYT|bayuquan|byq|611@bys|白银市|BNJ|baiyinshi|bys|612@bys|白音胡硕|BCD|baiyinhushuo|byhs|613@bzh|巴中|IEW|bazhong|bz|614@bzh|霸州|RMP|bazhou|bz|615@bzh|北宅|BVP|beizhai|bz|616@cbb|赤壁北|CIN|chibibei|cbb|617@cbg|查布嘎|CBC|chabuga|cbg|618@cch|长城|CEJ|changcheng|cc|619@cch|长冲|CCM|changchong|cc|620@cdd|承德东|CCP|chengdedong|cdd|621@cfx|赤峰西|CID|chifengxi|cfx|622@cga|嵯岗|CAX|cuogang|cg|623@cga|柴岗|CGT|chaigang|cg|624@cge|长葛|CEF|changge|cg|625@cgp|柴沟堡|CGV|chaigoupu|cgb|626@cgu|城固|CGY|chenggu|cg|627@cgy|陈官营|CAJ|chenguanying|cgy|628@cgz|成高子|CZB|chenggaozi|cgz|629@cha|草海|WBW|caohai|ch|630@che|柴河|CHB|chaihe|ch|631@che|册亨|CHZ|ceheng|ch|632@chk|草河口|CKT|caohekou|chk|633@chk|崔黄口|CHP|cuihuangkou|chk|634@chu|巢湖|CIH|chaohu|ch|635@cjg|蔡家沟|CJT|caijiagou|cjg|636@cjh|成吉思汗|CJX|chengjisihan|cjsh|637@cji|岔江|CAM|chajiang|cj|638@cjp|蔡家坡|CJY|caijiapo|cjp|639@cle|昌乐|CLK|changle|cl|640@clg|超梁沟|CYP|chaolianggou|clg|641@cli|慈利|CUQ|cili|cl|642@cli|昌黎|CLP|changli|cl|643@clz|长岭子|CLT|changlingzi|clz|644@cmi|晨明|CMB|chenming|cm|645@cno|长农|CNJ|changnong|cn|646@cpb|昌平北|VBP|changpingbei|cpb|647@cpi|常平|DAQ|changping|cp|648@cpl|长坡岭|CPM|changpoling|cpl|649@cqi|辰清|CQB|chenqing|cq|650@csh|楚山|CSB|chushan|cs|651@csh|长寿|EFW|changshou|cs|652@csh|磁山|CSP|cishan|cs|653@csh|苍石|CST|cangshi|cs|654@csh|草市|CSL|caoshi|cs|655@csq|察素齐|CSC|chasuqi|csq|656@cst|长山屯|CVT|changshantun|cst|657@cti|长汀|CES|changting|ct|658@ctx|昌图西|CPT|changtuxi|ctx|659@cwa|春湾|CQQ|chunwan|cw|660@cxi|磁县|CIP|cixian|cx|661@cxi|岑溪|CNZ|cenxi|cx|662@cxi|辰溪|CXQ|chenxi|cx|663@cxi|磁西|CRP|cixi|cx|664@cxn|长兴南|CFH|changxingnan|cxn|665@cya|磁窑|CYK|ciyao|cy|666@cya|朝阳|CYD|chaoyang|cy|667@cya|春阳|CAL|chunyang|cy|668@cya|城阳|CEK|chengyang|cy|669@cyc|创业村|CEX|chuangyecun|cyc|670@cyc|朝阳川|CYL|chaoyangchuan|cyc|671@cyd|朝阳地|CDD|chaoyangdi|cyd|672@cyu|长垣|CYF|changyuan|cy|673@cyz|朝阳镇|CZL|chaoyangzhen|cyz|674@czb|滁州北|CUH|chuzhoubei|czb|675@czb|常州北|ESH|changzhoubei|czb|676@czh|滁州|CXH|chuzhou|cz|677@czh|潮州|CKQ|chaozhou|cz|678@czh|常庄|CVK|changzhuang|cz|679@czl|曹子里|CFP|caozili|czl|680@czw|车转湾|CWM|chezhuanwan|czw|681@czx|郴州西|ICQ|chenzhouxi|czx|682@czx|沧州西|CBP|cangzhouxi|czx|683@dan|德安|DAG|dean|da|684@dan|大安|RAT|daan|da|685@dba|大坝|DBJ|daba|db|686@dba|大板|DBC|daban|db|687@dba|大巴|DBD|daba|db|688@dba|到保|RBT|daobao|db|689@dbi|定边|DYJ|dingbian|db|690@dbj|东边井|DBB|dongbianjing|dbj|691@dbs|德伯斯|RDT|debosi|dbs|692@dcg|打柴沟|DGJ|dachaigou|dcg|693@dch|德昌|DVW|dechang|dc|694@dda|滴道|DDB|didao|dd|695@ddg|大磴沟|DKJ|dadenggou|ddg|696@ded|刀尔登|DRD|daoerdeng|ded|697@dee|得耳布尔|DRX|deerbuer|debe|698@dfa|东方|UFQ|dongfang|df|699@dfe|丹凤|DGY|danfeng|df|700@dfe|东丰|DIL|dongfeng|df|701@dge|都格|DMM|duge|dg|702@dgt|大官屯|DTT|daguantun|dgt|703@dgu|大关|RGW|daguan|dg|704@dgu|东光|DGP|dongguang|dg|705@dha|东海|DHB|donghai|dh|706@dhc|大灰厂|DHP|dahuichang|dhc|707@dhq|大红旗|DQD|dahongqi|dhq|708@dhx|东海县|DQH|donghaixian|dhx|709@dhx|德惠西|DXT|dehuixi|dhx|710@djg|达家沟|DJT|dajiagou|djg|711@dji|东津|DKB|dongjin|dj|712@dji|杜家|DJL|dujia|dj|713@dkt|大口屯|DKP|dakoutun|dkt|714@dla|东来|RVD|donglai|dl|715@dlh|德令哈|DHO|delingha|dlh|716@dlh|大陆号|DLC|daluhao|dlh|717@dli|带岭|DLB|dailing|dl|718@dli|大林|DLD|dalin|dl|719@dlq|达拉特旗|DIC|dalateqi|dltq|720@dlt|独立屯|DTX|dulitun|dlt|721@dlu|豆罗|DLV|douluo|dl|722@dlx|达拉特西|DNC|dalatexi|dltx|723@dmc|东明村|DMD|dongmingcun|dmc|724@dmh|洞庙河|DEP|dongmiaohe|dmh|725@dmx|东明县|DNF|dongmingxian|dmx|726@dni|大拟|DNZ|dani|dn|727@dpf|大平房|DPD|dapingfang|dpf|728@dps|大盘石|RPP|dapanshi|dps|729@dpu|大埔|DPI|dapu|dp|730@dpu|大堡|DVT|dapu|db|731@dqh|大其拉哈|DQX|daqilaha|dqlh|732@dqi|道清|DML|daoqing|dq|733@dqs|对青山|DQB|duiqingshan|dqs|734@dqx|德清西|MOH|deqingxi|dqx|735@dqx|大庆西|RHX|daqingxi|dqx|736@dsh|东升|DRQ|dongsheng|ds|737@dsh|独山|RWW|dushan|ds|738@dsh|砀山|DKH|dangshan|ds|739@dsh|登沙河|DWT|dengshahe|dsh|740@dsp|读书铺|DPM|dushupu|dsp|741@dst|大石头|DSL|dashitou|dst|742@dsx|东胜西|DYC|dongshengxi|dsx|743@dsz|大石寨|RZT|dashizhai|dsz|744@dta|东台|DBH|dongtai|dt|745@dta|定陶|DQK|dingtao|dt|746@dta|灯塔|DGT|dengta|dt|747@dtb|大田边|DBM|datianbian|dtb|748@dth|东通化|DTL|dongtonghua|dth|749@dtu|丹徒|RUH|dantu|dt|750@dtu|大屯|DNT|datun|dt|751@dwa|东湾|DRJ|dongwan|dw|752@dwk|大武口|DFJ|dawukou|dwk|753@dwp|低窝铺|DWJ|diwopu|dwp|754@dwt|大王滩|DZZ|dawangtan|dwt|755@dwz|大湾子|DFM|dawanzi|dwz|756@dxg|大兴沟|DXL|daxinggou|dxg|757@dxi|大兴|DXX|daxing|dx|758@dxi|定西|DSJ|dingxi|dx|759@dxi|甸心|DXM|dianxin|dx|760@dxi|东乡|DXG|dongxiang|dx|761@dxi|代县|DKV|daixian|dx|762@dxi|定襄|DXV|dingxiang|dx|763@dxu|东戌|RXP|dongxu|dx|764@dxz|东辛庄|DXD|dongxinzhuang|dxz|765@dya|丹阳|DYH|danyang|dy|766@dya|大雁|DYX|dayan|dy|767@dya|德阳|DYW|deyang|dy|768@dya|当阳|DYN|dangyang|dy|769@dyb|丹阳北|EXH|danyangbei|dyb|770@dyd|大英东|IAW|dayingdong|dyd|771@dyd|东淤地|DBV|dongyudi|dyd|772@dyi|大营|DYV|daying|dy|773@dyu|定远|EWH|dingyuan|dy|774@dyu|岱岳|RYV|daiyue|dy|775@dyu|大元|DYZ|dayuan|dy|776@dyz|大营镇|DJP|dayingzhen|dyz|777@dyz|大营子|DZD|dayingzi|dyz|778@dzc|大战场|DTJ|dazhanchang|dzc|779@dzd|德州东|DIP|dezhoudong|dzd|780@dzh|低庄|DVQ|dizhuang|dz|781@dzh|东镇|DNV|dongzhen|dz|782@dzh|道州|DFZ|daozhou|dz|783@dzh|东至|DCH|dongzhi|dz|784@dzh|东庄|DZV|dongzhuang|dz|785@dzh|兑镇|DWV|duizhen|dz|786@dzh|豆庄|ROP|douzhuang|dz|787@dzh|定州|DXP|dingzhou|dz|788@dzy|大竹园|DZY|dazhuyuan|dzy|789@dzz|大杖子|DAP|dazhangzi|dzz|790@dzz|豆张庄|RZP|douzhangzhuang|dzz|791@ebi|峨边|EBW|ebian|eb|792@edm|二道沟门|RDP|erdaogoumen|edgm|793@edw|二道湾|RDX|erdaowan|edw|794@elo|二龙|RLD|erlong|el|795@elt|二龙山屯|ELA|erlongshantun|elst|796@eme|峨眉|EMW|emei|em|797@emh|二密河|RML|ermihe|emh|798@eyi|二营|RYJ|erying|ey|799@ezh|鄂州|ECN|ezhou|ez|800@fan|福安|FAS|fuan|fa|801@fch|丰城|FCG|fengcheng|fc|802@fcn|丰城南|FNG|fengchengnan|fcn|803@fdo|肥东|FIH|feidong|fd|804@fer|发耳|FEM|faer|fe|805@fha|富海|FHX|fuhai|fh|806@fha|福海|FHR|fuhai|fh|807@fhc|凤凰城|FHT|fenghuangcheng|fhc|808@fhu|奉化|FHH|fenghua|fh|809@fji|富锦|FIB|fujin|fj|810@fjt|范家屯|FTT|fanjiatun|fjt|811@flt|福利屯|FTB|fulitun|flt|812@flz|丰乐镇|FZB|fenglezhen|flz|813@fna|阜南|FNH|funan|fn|814@fni|阜宁|AKH|funing|fn|815@fni|抚宁|FNP|funing|fn|816@fqi|福清|FQS|fuqing|fq|817@fqu|福泉|VMW|fuquan|fq|818@fsc|丰水村|FSJ|fengshuicun|fsc|819@fsh|丰顺|FUQ|fengshun|fs|820@fsh|繁峙|FSV|fanshi|fs|821@fsh|抚顺|FST|fushun|fs|822@fsk|福山口|FKP|fushankou|fsk|823@fsu|扶绥|FSZ|fusui|fs|824@ftu|冯屯|FTX|fengtun|ft|825@fty|浮图峪|FYP|futuyu|fty|826@fxd|富县东|FDY|fuxiandong|fxd|827@fxi|凤县|FXY|fengxian|fx|828@fxi|富县|FEY|fuxian|fx|829@fxi|费县|FXK|feixian|fx|830@fya|凤阳|FUH|fengyang|fy|831@fya|汾阳|FAV|fenyang|fy|832@fyb|扶余北|FBT|fuyubei|fyb|833@fyi|分宜|FYG|fenyi|fy|834@fyu|富源|FYM|fuyuan|fy|835@fyu|扶余|FYT|fuyu|fy|836@fyu|富裕|FYX|fuyu|fy|837@fzb|抚州北|FBG|fuzhoubei|fzb|838@fzh|凤州|FZY|fengzhou|fz|839@fzh|丰镇|FZC|fengzhen|fz|840@fzh|范镇|VZK|fanzhen|fz|841@gan|固安|GFP|guan|ga|842@gan|广安|VJW|guangan|ga|843@gbd|高碑店|GBP|gaobeidian|gbd|844@gbz|沟帮子|GBD|goubangzi|gbz|845@gcd|甘草店|GDJ|gancaodian|gcd|846@gch|谷城|GCN|gucheng|gc|847@gch|藁城|GEP|gaocheng|gc|848@gcu|高村|GCV|gaocun|gc|849@gcz|古城镇|GZB|guchengzhen|gcz|850@gde|广德|GRH|guangde|gd|851@gdi|贵定|GTW|guiding|gd|852@gdn|贵定南|IDW|guidingnan|gdn|853@gdo|古东|GDV|gudong|gd|854@gga|贵港|GGZ|guigang|gg|855@gga|官高|GVP|guangao|gg|856@ggm|葛根庙|GGT|gegenmiao|ggm|857@ggo|干沟|GGL|gangou|gg|858@ggu|甘谷|GGJ|gangu|gg|859@ggz|高各庄|GGP|gaogezhuang|ggz|860@ghe|甘河|GAX|ganhe|gh|861@ghe|根河|GEX|genhe|gh|862@gjd|郭家店|GDT|guojiadian|gjd|863@gjz|孤家子|GKT|gujiazi|gjz|864@gla|古浪|GLJ|gulang|gl|865@gla|皋兰|GEJ|gaolan|gl|866@glf|高楼房|GFM|gaoloufang|glf|867@glh|归流河|GHT|guiliuhe|glh|868@gli|关林|GLF|guanlin|gl|869@glu|甘洛|VOW|ganluo|gl|870@glz|郭磊庄|GLP|guoleizhuang|glz|871@gmi|高密|GMK|gaomi|gm|872@gmz|公庙子|GMC|gongmiaozi|gmz|873@gnh|工农湖|GRT|gongnonghu|gnh|874@gns|广宁寺|GNT|guangningsi|gns|875@gnw|广南卫|GNM|guangnanwei|gnw|876@gpi|高平|GPF|gaoping|gp|877@gqb|甘泉北|GEY|ganquanbei|gqb|878@gqc|共青城|GAG|gongqingcheng|gqc|879@gqk|甘旗卡|GQD|ganqika|gqk|880@gqu|甘泉|GQY|ganquan|gq|881@gqz|高桥镇|GZD|gaoqiaozhen|gqz|882@gsh|赶水|GSW|ganshui|gs|883@gsh|灌水|GST|guanshui|gs|884@gsk|孤山口|GSP|gushankou|gsk|885@gso|果松|GSL|guosong|gs|886@gsz|高山子|GSD|gaoshanzi|gsz|887@gsz|嘎什甸子|GXD|gashidianzi|gsdz|888@gta|高台|GTJ|gaotai|gt|889@gta|高滩|GAY|gaotan|gt|890@gti|古田|GTS|gutian|gt|891@gti|官厅|GTP|guanting|gt|892@gtx|官厅西|KEP|guantingxi|gtx|893@gxi|贵溪|GXG|guixi|gx|894@gya|涡阳|GYH|guoyang|gy|895@gyi|巩义|GXF|gongyi|gy|896@gyi|高邑|GIP|gaoyi|gy|897@gyn|巩义南|GYF|gongyinan|gyn|898@gyu|固原|GUJ|guyuan|gy|899@gyu|菇园|GYL|guyuan|gy|900@gyz|公营子|GYD|gongyingzi|gyz|901@gze|光泽|GZS|guangze|gz|902@gzh|古镇|GNQ|guzhen|gz|903@gzh|瓜州|GZJ|guazhou|gz|904@gzh|高州|GSQ|gaozhou|gz|905@gzh|固镇|GEH|guzhen|gz|906@gzh|盖州|GXT|gaizhou|gz|907@gzj|官字井|GOT|guanzijing|gzj|908@gzp|革镇堡|GZT|gezhenpu|gzb|909@gzs|冠豸山|GSS|guanzhishan|gzs|910@gzx|盖州西|GAT|gaizhouxi|gzx|911@han|红安|HWN|hongan|ha|912@han|淮安南|AMH|huaiannan|han|913@hax|红安西|VXN|honganxi|hax|914@hax|海安县|HIH|haianxian|hax|915@hba|黄柏|HBL|huangbai|hb|916@hbe|海北|HEB|haibei|hb|917@hbi|鹤壁|HAF|hebi|hb|918@hch|华城|VCQ|huacheng|hc|919@hch|合川|WKW|hechuan|hc|920@hch|河唇|HCZ|hechun|hc|921@hch|汉川|HCN|hanchuan|hc|922@hch|海城|HCT|haicheng|hc|923@hct|黑冲滩|HCJ|heichongtan|hct|924@hcu|黄村|HCP|huangcun|hc|925@hcx|海城西|HXT|haichengxi|hcx|926@hde|化德|HGC|huade|hd|927@hdo|洪洞|HDV|hongdong|hd|928@hes|霍尔果斯|HFR|huoerguosi|hegs|929@hfe|横峰|HFG|hengfeng|hf|930@hfw|韩府湾|HXJ|hanfuwan|hfw|931@hgu|汉沽|HGP|hangu|hg|932@hgz|红光镇|IGW|hongguangzhen|hgz|933@hhe|浑河|HHT|hunhe|hh|934@hhg|红花沟|VHD|honghuagou|hhg|935@hht|黄花筒|HUD|huanghuatong|hht|936@hjd|贺家店|HJJ|hejiadian|hjd|937@hji|和静|HJR|hejing|hj|938@hji|红江|HFM|hongjiang|hj|939@hji|黑井|HIM|heijing|hj|940@hji|获嘉|HJF|huojia|hj|941@hji|河津|HJV|hejin|hj|942@hji|涵江|HJS|hanjiang|hj|943@hji|华家|HJT|huajia|hj|944@hjx|河间西|HXP|hejianxi|hjx|945@hjz|花家庄|HJM|huajiazhuang|hjz|946@hkn|河口南|HKJ|hekounan|hkn|947@hko|黄口|KOH|huangkou|hk|948@hko|湖口|HKG|hukou|hk|949@hla|呼兰|HUB|hulan|hl|950@hlb|葫芦岛北|HPD|huludaobei|hldb|951@hlh|浩良河|HHB|haolianghe|hlh|952@hlh|哈拉海|HIT|halahai|hlh|953@hli|鹤立|HOB|heli|hl|954@hli|桦林|HIB|hualin|hl|955@hli|黄陵|ULY|huangling|hl|956@hli|海林|HRB|hailin|hl|957@hli|虎林|VLB|hulin|hl|958@hli|寒岭|HAT|hanling|hl|959@hlo|和龙|HLL|helong|hl|960@hlo|海龙|HIL|hailong|hl|961@hls|哈拉苏|HAX|halasu|hls|962@hlt|呼鲁斯太|VTJ|hulusitai|hlst|963@hlz|火连寨|HLT|huolianzhai|hlz|964@hme|黄梅|VEH|huangmei|hm|965@hmt|蛤蟆塘|HMT|hamatang|gmt|966@hmy|韩麻营|HYP|hanmaying|hmy|967@hnh|黄泥河|HHL|huangnihe|hnh|968@hni|海宁|HNH|haining|hn|969@hno|惠农|HMJ|huinong|hn|970@hpi|和平|VAQ|heping|hp|971@hpz|花棚子|HZM|huapengzi|hpz|972@hqi|花桥|VQH|huaqiao|hq|973@hqi|宏庆|HEY|hongqing|hq|974@hre|怀仁|HRV|huairen|hr|975@hro|华容|HRN|huarong|hr|976@hsb|华山北|HDY|huashanbei|hsb|977@hsd|黄松甸|HDL|huangsongdian|hsd|978@hsg|和什托洛盖|VSR|heshituoluogai|hstlg|979@hsh|红山|VSB|hongshan|hs|980@hsh|汉寿|VSQ|hanshou|hs|981@hsh|衡山|HSQ|hengshan|hs|982@hsh|黑水|HOT|heishui|hs|983@hsh|惠山|VCH|huishan|hs|984@hsh|虎什哈|HHP|hushiha|hsh|985@hsp|红寺堡|HSJ|hongsipu|hsb|986@hst|虎石台|HUT|hushitai|hst|987@hsw|海石湾|HSO|haishiwan|hsw|988@hsx|衡山西|HEQ|hengshanxi|hsx|989@hsx|红砂岘|VSJ|hongshaxian|hsj|990@hta|黑台|HQB|heitai|ht|991@hta|桓台|VTK|huantai|ht|992@hti|和田|VTR|hetian|ht|993@hto|会同|VTQ|huitong|ht|994@htz|海坨子|HZT|haituozi|htz|995@hwa|黑旺|HWK|heiwang|hw|996@hwa|海湾|RWH|haiwan|hw|997@hxi|红星|VXB|hongxing|hx|998@hxi|徽县|HYY|huixian|hx|999@hxl|红兴隆|VHB|hongxinglong|hxl|1000@hxt|换新天|VTB|huanxintian|hxt|1001@hxt|红岘台|HTJ|hongxiantai|hxt|1002@hya|红彦|VIX|hongyan|hy|1003@hya|合阳|HAY|heyang|hy|1004@hya|海阳|HYK|haiyang|hy|1005@hyd|衡阳东|HVQ|hengyangdong|hyd|1006@hyi|华蓥|HUW|huaying|hy|1007@hyi|汉阴|HQY|hanyin|hy|1008@hyt|黄羊滩|HGJ|huangyangtan|hyt|1009@hyu|汉源|WHW|hanyuan|hy|1010@hyu|湟源|HNO|huangyuan|hy|1011@hyu|河源|VIQ|heyuan|hy|1012@hyu|花园|HUN|huayuan|hy|1013@hyz|黄羊镇|HYJ|huangyangzhen|hyz|1014@hzh|湖州|VZH|huzhou|hz|1015@hzh|化州|HZZ|huazhou|hz|1016@hzh|黄州|VON|huangzhou|hz|1017@hzh|霍州|HZV|huozhou|hz|1018@hzx|惠州西|VXQ|huizhouxi|hzx|1019@jba|巨宝|JRT|jubao|jb|1020@jbi|靖边|JIY|jingbian|jb|1021@jbt|金宝屯|JBD|jinbaotun|jbt|1022@jcb|晋城北|JEF|jinchengbei|jcb|1023@jch|金昌|JCJ|jinchang|jc|1024@jch|鄄城|JCK|juancheng|jc|1025@jch|交城|JNV|jiaocheng|jc|1026@jch|建昌|JFD|jianchang|jc|1027@jde|峻德|JDB|junde|jd|1028@jdi|井店|JFP|jingdian|jd|1029@jdo|鸡东|JOB|jidong|jd|1030@jdu|江都|UDH|jiangdu|jd|1031@jgs|鸡冠山|JST|jiguanshan|jgs|1032@jgt|金沟屯|VGP|jingoutun|jgt|1033@jha|静海|JHP|jinghai|jh|1034@jhe|金河|JHX|jinhe|jh|1035@jhe|锦河|JHB|jinhe|jh|1036@jhe|精河|JHR|jinghe|jh|1037@jhn|精河南|JIR|jinghenan|jhn|1038@jhu|江华|JHZ|jianghua|jh|1039@jhu|建湖|AJH|jianhu|jh|1040@jjg|纪家沟|VJD|jijiagou|jjg|1041@jji|晋江|JJS|jinjiang|jj|1042@jji|江津|JJW|jiangjin|jj|1043@jji|姜家|JJB|jiangjia|jj|1044@jke|金坑|JKT|jinkeng|jk|1045@jli|芨岭|JLJ|jiling|jl|1046@jmc|金马村|JMM|jinmacun|jmc|1047@jme|江门|JWQ|jiangmen|jm|1048@jme|角美|JES|jiaomei|jm|1049@jna|莒南|JOK|junan|jn|1050@jna|井南|JNP|jingnan|jn|1051@jou|建瓯|JVS|jianou|jo|1052@jpe|经棚|JPC|jingpeng|jp|1053@jqi|江桥|JQX|jiangqiao|jq|1054@jsa|九三|SSX|jiusan|js|1055@jsb|金山北|EGH|jinshanbei|jsb|1056@jsh|京山|JCN|jingshan|js|1057@jsh|建始|JRN|jianshi|js|1058@jsh|嘉善|JSH|jiashan|js|1059@jsh|稷山|JVV|jishan|js|1060@jsh|吉舒|JSL|jishu|js|1061@jsh|建设|JET|jianshe|js|1062@jsh|甲山|JOP|jiashan|js|1063@jsj|建三江|JIB|jiansanjiang|jsj|1064@jsn|嘉善南|EAH|jiashannan|jsn|1065@jst|金山屯|JTB|jinshantun|jst|1066@jst|江所田|JOM|jiangsuotian|jst|1067@jta|景泰|JTJ|jingtai|jt|1068@jtn|九台南|JNL|jiutainan|jtn|1069@jwe|吉文|JWX|jiwen|jw|1070@jxi|进贤|JUG|jinxian|jx|1071@jxi|莒县|JKK|juxian|jx|1072@jxi|嘉祥|JUK|jiaxiang|jx|1073@jxi|介休|JXV|jiexiu|jx|1074@jxi|井陉|JJP|jingxing|jx|1075@jxi|嘉兴|JXH|jiaxing|jx|1076@jxn|嘉兴南|EPH|jiaxingnan|jxn|1077@jxz|夹心子|JXT|jiaxinzi|jxz|1078@jya|简阳|JYW|jianyang|jy|1079@jya|揭阳|JRQ|jieyang|jy|1080@jya|建阳|JYS|jianyang|jy|1081@jya|姜堰|UEH|jiangyan|jy|1082@jye|巨野|JYK|juye|jy|1083@jyo|江永|JYZ|jiangyong|jy|1084@jyu|靖远|JYJ|jingyuan|jy|1085@jyu|缙云|JYH|jinyun|jy|1086@jyu|江源|SZL|jiangyuan|jy|1087@jyu|济源|JYF|jiyuan|jy|1088@jyx|靖远西|JXJ|jingyuanxi|jyx|1089@jzb|胶州北|JZK|jiaozhoubei|jzb|1090@jzd|焦作东|WEF|jiaozuodong|jzd|1091@jzh|靖州|JEQ|jingzhou|jz|1092@jzh|荆州|JBN|jingzhou|jz|1093@jzh|金寨|JZH|jinzhai|jz|1094@jzh|晋州|JXP|jinzhou|jz|1095@jzh|胶州|JXK|jiaozhou|jz|1096@jzn|锦州南|JOD|jinzhounan|jzn|1097@jzu|焦作|JOF|jiaozuo|jz|1098@jzw|旧庄窝|JVP|jiuzhuangwo|jzw|1099@jzz|金杖子|JYD|jinzhangzi|jzz|1100@kan|开安|KAT|kaian|ka|1101@kch|库车|KCR|kuche|kc|1102@kch|康城|KCP|kangcheng|kc|1103@kde|库都尔|KDX|kuduer|kde|1104@kdi|宽甸|KDT|kuandian|kd|1105@kdo|克东|KOB|kedong|kd|1106@kji|开江|KAW|kaijiang|kj|1107@kjj|康金井|KJB|kangjinjing|kjj|1108@klq|喀喇其|KQX|kalaqi|klq|1109@klu|开鲁|KLC|kailu|kl|1110@kly|克拉玛依|KHR|kelamayi|klmy|1111@kqi|口前|KQL|kouqian|kq|1112@ksh|奎山|KAB|kuishan|ks|1113@ksh|昆山|KSH|kunshan|ks|1114@ksh|克山|KSB|keshan|ks|1115@kto|开通|KTT|kaitong|kt|1116@kxl|康熙岭|KXZ|kangxiling|kxl|1117@kya|昆阳|KAM|kunyang|ky|1118@kyh|克一河|KHX|keyihe|kyh|1119@kyx|开原西|KXT|kaiyuanxi|kyx|1120@kzh|康庄|KZP|kangzhuang|kz|1121@lbi|来宾|UBZ|laibin|lb|1122@lbi|老边|LLT|laobian|lb|1123@lbx|灵宝西|LPF|lingbaoxi|lbx|1124@lch|龙川|LUQ|longchuan|lc|1125@lch|乐昌|LCQ|lechang|lc|1126@lch|黎城|UCP|licheng|lc|1127@lch|聊城|UCK|liaocheng|lc|1128@lcu|蓝村|LCK|lancun|lc|1129@ldo|林东|LRC|lindong|ld|1130@ldu|乐都|LDO|ledu|ld|1131@ldx|梁底下|LDP|liangdixia|ldx|1132@ldz|六道河子|LVP|liudaohezi|ldhz|1133@lfa|鲁番|LVM|lufan|lf|1134@lfa|廊坊|LJP|langfang|lf|1135@lfa|落垡|LOP|luofa|lf|1136@lfb|廊坊北|LFP|langfangbei|lfb|1137@lfu|老府|UFD|laofu|lf|1138@lga|兰岗|LNB|langang|lg|1139@lgd|龙骨甸|LGM|longgudian|lgd|1140@lgo|芦沟|LOM|lugou|lg|1141@lgo|龙沟|LGJ|longgou|lg|1142@lgu|拉古|LGB|lagu|lg|1143@lha|临海|UFH|linhai|lh|1144@lha|林海|LXX|linhai|lh|1145@lha|拉哈|LHX|laha|lh|1146@lha|凌海|JID|linghai|lh|1147@lhe|柳河|LNL|liuhe|lh|1148@lhe|六合|KLH|liuhe|lh|1149@lhu|龙华|LHP|longhua|lh|1150@lhy|滦河沿|UNP|luanheyan|lhy|1151@lhz|六合镇|LEX|liuhezhen|lhz|1152@ljd|亮甲店|LRT|liangjiadian|ljd|1153@ljd|刘家店|UDT|liujiadian|ljd|1154@ljh|刘家河|LVT|liujiahe|ljh|1155@lji|连江|LKS|lianjiang|lj|1156@lji|李家|LJB|lijia|lj|1157@lji|罗江|LJW|luojiang|lj|1158@lji|廉江|LJZ|lianjiang|lj|1159@lji|庐江|UJH|lujiang|lj|1160@lji|两家|UJT|liangjia|lj|1161@lji|龙江|LJX|longjiang|lj|1162@lji|龙嘉|UJL|longjia|lj|1163@ljk|莲江口|LHB|lianjiangkou|ljk|1164@ljl|蔺家楼|ULK|linjialou|ljl|1165@ljp|李家坪|LIJ|lijiaping|ljp|1166@lka|兰考|LKF|lankao|lk|1167@lko|林口|LKB|linkou|lk|1168@lkp|路口铺|LKQ|lukoupu|lkp|1169@lla|老莱|LAX|laolai|ll|1170@lli|拉林|LAB|lalin|ll|1171@lli|陆良|LRM|luliang|ll|1172@lli|龙里|LLW|longli|ll|1173@lli|零陵|UWZ|lingling|ll|1174@lli|临澧|LWQ|linli|ll|1175@lli|兰棱|LLB|lanling|ll|1176@llo|卢龙|UAP|lulong|ll|1177@lmd|喇嘛甸|LMX|lamadian|lmd|1178@lmd|里木店|LMB|limudian|lmd|1179@lme|洛门|LMJ|luomen|lm|1180@lna|龙南|UNG|longnan|ln|1181@lpi|梁平|UQW|liangping|lp|1182@lpi|罗平|LPM|luoping|lp|1183@lpl|落坡岭|LPP|luopoling|lpl|1184@lps|六盘山|UPJ|liupanshan|lps|1185@lps|乐平市|LPG|lepingshi|lps|1186@lqi|临清|UQK|linqing|lq|1187@lqs|龙泉寺|UQJ|longquansi|lqs|1188@lsb|乐山北|UTW|leshanbei|ls|1189@lsc|乐善村|LUM|leshancun|lsc|1190@lsd|冷水江东|UDQ|lengshuijiangdong|lsjd|1191@lsg|连山关|LGT|lianshanguan|lsg|1192@lsg|流水沟|USP|liushuigou|lsg|1193@lsh|陵水|LIQ|lingshui|ls|1194@lsh|罗山|LRN|luoshan|ls|1195@lsh|鲁山|LAF|lushan|ls|1196@lsh|丽水|USH|lishui|ls|1197@lsh|梁山|LMK|liangshan|ls|1198@lsh|灵石|LSV|lingshi|ls|1199@lsh|露水河|LUL|lushuihe|lsh|1200@lsh|庐山|LSG|lushan|ls|1201@lsp|林盛堡|LBT|linshengpu|lsp|1202@lst|柳树屯|LSD|liushutun|lst|1203@lsz|龙山镇|LAS|longshanzhen|lsz|1204@lsz|梨树镇|LSB|lishuzhen|lsz|1205@lsz|李石寨|LET|lishizhai|lsz|1206@lta|黎塘|LTZ|litang|lt|1207@lta|轮台|LAR|luntai|lt|1208@lta|芦台|LTP|lutai|lt|1209@ltb|龙塘坝|LBM|longtangba|ltb|1210@ltu|濑湍|LVZ|laituan|lt|1211@ltx|骆驼巷|LTJ|luotuoxiang|ltx|1212@lwa|李旺|VLJ|liwang|lw|1213@lwd|莱芜东|LWK|laiwudong|lwd|1214@lws|狼尾山|LRJ|langweishan|lws|1215@lwu|灵武|LNJ|lingwu|lw|1216@lwx|莱芜西|UXK|laiwuxi|lwx|1217@lxi|朗乡|LXB|langxiang|lx|1218@lxi|陇县|LXY|longxian|lx|1219@lxi|临湘|LXQ|linxiang|lx|1220@lxi|芦溪|LUG|luxi|lx|1221@lxi|莱西|LXK|laixi|lx|1222@lxi|林西|LXC|linxi|lx|1223@lxi|滦县|UXP|luanxian|lx|1224@lya|略阳|LYY|lueyang|ly|1225@lya|辽阳|LYT|liaoyang|ly|1226@lyb|临沂北|UYK|linyibei|lyb|1227@lyd|凌源东|LDD|lingyuandong|lyd|1228@lyg|连云港|UIH|lianyungang|lyg|1229@lyi|临颍|LNF|linying|ly|1230@lyi|老营|LXL|laoying|ly|1231@lyo|龙游|LMH|longyou|ly|1232@lyu|罗源|LVS|luoyuan|ly|1233@lyu|林源|LYX|linyuan|ly|1234@lyu|涟源|LAQ|lianyuan|ly|1235@lyu|涞源|LYP|laiyuan|ly|1236@lyx|耒阳西|LPQ|leiyangxi|lyx|1237@lze|临泽|LEJ|linze|lz|1238@lzg|龙爪沟|LZT|longzhaogou|lzg|1239@lzh|雷州|UAQ|leizhou|lz|1240@lzh|六枝|LIW|liuzhi|lz|1241@lzh|鹿寨|LIZ|luzhai|lz|1242@lzh|来舟|LZS|laizhou|lz|1243@lzh|龙镇|LZA|longzhen|lz|1244@lzh|拉鲊|LEM|lazha|lz|1245@mas|马鞍山|MAH|maanshan|mas|1246@mba|毛坝|MBY|maoba|mb|1247@mbg|毛坝关|MGY|maobaguan|mbg|1248@mcb|麻城北|MBN|machengbei|mcb|1249@mch|渑池|MCF|mianchi|mc|1250@mch|明城|MCL|mingcheng|mc|1251@mch|庙城|MAP|miaocheng|mc|1252@mcn|渑池南|MNF|mianchinan|mcn|1253@mcp|茅草坪|KPM|maocaoping|mcp|1254@mdh|猛洞河|MUQ|mengdonghe|mdh|1255@mds|磨刀石|MOB|modaoshi|mds|1256@mdu|弥渡|MDF|midu|md|1257@mes|帽儿山|MRB|maoershan|mes|1258@mga|明港|MGN|minggang|mg|1259@mhk|梅河口|MHL|meihekou|mhk|1260@mhu|马皇|MHZ|mahuang|mh|1261@mjg|孟家岗|MGB|mengjiagang|mjg|1262@mla|美兰|MHQ|meilan|ml|1263@mld|汨罗东|MQQ|miluodong|mld|1264@mlh|马莲河|MHB|malianhe|mlh|1265@mli|茅岭|MLZ|maoling|ml|1266@mli|庙岭|MLL|miaoling|ml|1267@mli|茂林|MLD|maolin|ml|1268@mli|穆棱|MLB|muling|ml|1269@mli|马林|MID|malin|ml|1270@mlo|马龙|MGM|malong|ml|1271@mlo|汨罗|MLQ|miluo|ml|1272@mlt|木里图|MUD|mulitu|mlt|1273@mnh|玛纳斯湖|MNR|manasihu|mnsh|1274@mni|冕宁|UGW|mianning|mn|1275@mpa|沐滂|MPQ|mupang|mp|1276@mqh|马桥河|MQB|maqiaohe|mqh|1277@mqi|闽清|MQS|minqing|mq|1278@mqu|民权|MQF|minquan|mq|1279@msh|明水河|MUT|mingshuihe|msh|1280@msh|麻山|MAB|mashan|ms|1281@msh|眉山|MSW|meishan|ms|1282@msw|漫水湾|MKW|manshuiwan|msw|1283@msz|茂舍祖|MOM|maoshezu|msz|1284@msz|米沙子|MST|mishazi|msz|1285@mxi|美溪|MEB|meixi|mx|1286@mxi|勉县|MVY|mianxian|mx|1287@mya|麻阳|MVQ|mayang|my|1288@myb|密云北|MUP|miyunbei|myb|1289@myi|米易|MMW|miyi|my|1290@myu|麦园|MYS|maiyuan|my|1291@myu|墨玉|MUR|moyu|my|1292@mzh|庙庄|MZJ|miaozhuang|mz|1293@mzh|米脂|MEY|mizhi|mz|1294@mzh|明珠|MFQ|mingzhu|mz|1295@nan|宁安|NAB|ningan|na|1296@nan|农安|NAT|nongan|na|1297@nbs|南博山|NBK|nanboshan|nbs|1298@nch|南仇|NCK|nanchou|nc|1299@ncs|南城司|NSP|nanchengsi|ncs|1300@ncu|宁村|NCZ|ningcun|nc|1301@nde|宁德|NES|ningde|nd|1302@ngc|南观村|NGP|nanguancun|ngc|1303@ngd|南宫东|NFP|nangongdong|ngd|1304@ngl|南关岭|NLT|nanguanling|ngl|1305@ngu|宁国|NNH|ningguo|ng|1306@nha|宁海|NHH|ninghai|nh|1307@nhc|南河川|NHJ|nanhechuan|nhc|1308@nhu|南华|NHS|nanhua|nh|1309@nhz|泥河子|NHD|nihezi|nhz|1310@nji|宁家|NVT|ningjia|nj|1311@nji|南靖|NJS|nanjing|nj|1312@nji|牛家|NJB|niujia|nj|1313@nji|能家|NJD|nengjia|nj|1314@nko|南口|NKP|nankou|nk|1315@nkq|南口前|NKT|nankouqian|nkq|1316@nla|南朗|NNQ|nanlang|nl|1317@nli|乃林|NLD|nailin|nl|1318@nlk|尼勒克|NIR|nileke|nlk|1319@nlu|那罗|ULZ|naluo|nl|1320@nlx|宁陵县|NLF|ninglingxian|nlx|1321@nma|奈曼|NMD|naiman|nm|1322@nmi|宁明|NMZ|ningming|nm|1323@nmu|南木|NMX|nanmu|nm|1324@npn|南平南|NNS|nanpingnan|npn|1325@npu|那铺|NPZ|napu|np|1326@nqi|南桥|NQD|nanqiao|nq|1327@nqu|那曲|NQO|naqu|nq|1328@nqu|暖泉|NQJ|nuanquan|nq|1329@nta|南台|NTT|nantai|nt|1330@nto|南头|NOQ|nantou|nt|1331@nwu|宁武|NWV|ningwu|nw|1332@nwz|南湾子|NWP|nanwanzi|nwz|1333@nxb|南翔北|NEH|nanxiangbei|nxb|1334@nxi|宁乡|NXQ|ningxiang|nx|1335@nxi|内乡|NXF|neixiang|nx|1336@nxt|牛心台|NXT|niuxintai|nxt|1337@nyu|南峪|NUP|nanyu|ny|1338@nzg|娘子关|NIP|niangziguan|nzg|1339@nzh|南召|NAF|nanzhao|nz|1340@nzm|南杂木|NZT|nanzamu|nzm|1341@pan|平安|PAL|pingan|pa|1342@pan|蓬安|PAW|pengan|pa|1343@pay|平安驿|PNO|pinganyi|pay|1344@paz|磐安镇|PAJ|pananzhen|paz|1345@paz|平安镇|PZT|pinganzhen|paz|1346@pcd|蒲城东|PEY|puchengdong|pcd|1347@pch|蒲城|PCY|pucheng|pc|1348@pde|裴德|PDB|peide|pd|1349@pdi|偏店|PRP|piandian|pd|1350@pdx|平顶山西|BFF|pingdingshanxi|pdsx|1351@pdx|坡底下|PXJ|podixia|pdx|1352@pet|瓢儿屯|PRT|piaoertun|pet|1353@pfa|平房|PFB|pingfang|pf|1354@pga|平岗|PGL|pinggang|pg|1355@pgu|平关|PGM|pingguan|pg|1356@pgu|盘关|PAM|panguan|pg|1357@pgu|平果|PGZ|pingguo|pg|1358@phb|徘徊北|PHP|paihuibei|phb|1359@phk|平河口|PHM|pinghekou|phk|1360@pjb|盘锦北|PBD|panjinbei|pjb|1361@pjd|潘家店|PDP|panjiadian|pjd|1362@pko|皮口|PKT|pikou|pk|1363@pld|普兰店|PLT|pulandian|pld|1364@pli|偏岭|PNT|pianling|pl|1365@psh|平山|PSB|pingshan|ps|1366@psh|彭山|PSW|pengshan|ps|1367@psh|皮山|PSR|pishan|ps|1368@psh|彭水|PHW|pengshui|ps|1369@psh|磐石|PSL|panshi|ps|1370@psh|平社|PSV|pingshe|ps|1371@pta|平台|PVT|pingtai|pt|1372@pti|平田|PTM|pingtian|pt|1373@pti|莆田|PTS|putian|pt|1374@ptq|葡萄菁|PTW|putaoqing|ptj|1375@pwa|普湾|PWT|puwan|pw|1376@pwa|平旺|PWV|pingwang|pw|1377@pxg|平型关|PGV|pingxingguan|pxg|1378@pxi|普雄|POW|puxiong|px|1379@pxi|郫县|PWW|pixian|px|1380@pya|平洋|PYX|pingyang|py|1381@pya|彭阳|PYJ|pengyang|py|1382@pya|平遥|PYV|pingyao|py|1383@pyi|平邑|PIK|pingyi|py|1384@pyp|平原堡|PPJ|pingyuanpu|pyp|1385@pyu|平原|PYK|pingyuan|py|1386@pyu|平峪|PYP|pingyu|py|1387@pze|彭泽|PZG|pengze|pz|1388@pzh|邳州|PJH|pizhou|pz|1389@pzh|平庄|PZD|pingzhuang|pz|1390@pzi|泡子|POD|paozi|pz|1391@pzn|平庄南|PND|pingzhuangnan|pzn|1392@qan|乾安|QOT|qianan|qa|1393@qan|庆安|QAB|qingan|qa|1394@qan|迁安|QQP|qianan|qa|1395@qdb|祁东北|QRQ|qidongbei|qd|1396@qdi|七甸|QDM|qidian|qd|1397@qfd|曲阜东|QAK|qufudong|qfd|1398@qfe|庆丰|QFT|qingfeng|qf|1399@qft|奇峰塔|QVP|qifengta|qft|1400@qfu|曲阜|QFK|qufu|qf|1401@qha|琼海|QYQ|qionghai|qh|1402@qhd|秦皇岛|QTP|qinhuangdao|qhd|1403@qhe|千河|QUY|qianhe|qh|1404@qhe|清河|QIP|qinghe|qh|1405@qhm|清河门|QHD|qinghemen|qhm|1406@qhy|清华园|QHP|qinghuayuan|qhy|1407@qji|渠旧|QJZ|qujiu|qj|1408@qji|綦江|QJW|qijiang|qj|1409@qji|潜江|QJN|qianjiang|qj|1410@qji|全椒|INH|quanjiao|qj|1411@qji|秦家|QJB|qinjia|qj|1412@qjp|祁家堡|QBT|qijiapu|qjb|1413@qjx|清涧县|QNY|qingjianxian|qjx|1414@qjz|秦家庄|QZV|qinjiazhuang|qjz|1415@qlh|七里河|QLD|qilihe|qlh|1416@qli|渠黎|QLZ|quli|ql|1417@qli|秦岭|QLY|qinling|ql|1418@qls|青龙山|QGH|qinglongshan|qls|1419@qme|祁门|QIH|qimen|qm|1420@qmt|前磨头|QMP|qianmotou|qmt|1421@qsh|青山|QSB|qingshan|qs|1422@qsh|确山|QSN|queshan|qs|1423@qsh|清水|QUJ|qingshui|qs|1424@qsh|前山|QXQ|qianshan|qs|1425@qsy|戚墅堰|QYH|qishuyan|qsy|1426@qti|青田|QVH|qingtian|qt|1427@qto|桥头|QAT|qiaotou|qt|1428@qtx|青铜峡|QTJ|qingtongxia|qtx|1429@qwe|前卫|QWD|qianwei|qw|1430@qwt|前苇塘|QWP|qianweitang|qwt|1431@qxi|渠县|QRW|quxian|qx|1432@qxi|祁县|QXV|qixian|qx|1433@qxi|青县|QXP|qingxian|qx|1434@qxi|桥西|QXJ|qiaoxi|qx|1435@qxu|清徐|QUV|qingxu|qx|1436@qxy|旗下营|QXC|qixiaying|qxy|1437@qya|千阳|QOY|qianyang|qy|1438@qya|沁阳|QYF|qinyang|qy|1439@qya|泉阳|QYL|quanyang|qy|1440@qyb|祁阳北|QVQ|qiyangbei|qy|1441@qyi|七营|QYJ|qiying|qy|1442@qys|庆阳山|QSJ|qingyangshan|qys|1443@qyu|清远|QBQ|qingyuan|qy|1444@qyu|清原|QYT|qingyuan|qy|1445@qzd|钦州东|QDZ|qinzhoudong|qzd|1446@qzh|钦州|QRZ|qinzhou|qz|1447@qzs|青州市|QZK|qingzhoushi|qzs|1448@ran|瑞安|RAH|ruian|ra|1449@rch|荣昌|RCW|rongchang|rc|1450@rch|瑞昌|RCG|ruichang|rc|1451@rga|如皋|RBH|rugao|rg|1452@rgu|容桂|RUQ|ronggui|rg|1453@rqi|任丘|RQP|renqiu|rq|1454@rsh|乳山|ROK|rushan|rs|1455@rsh|融水|RSZ|rongshui|rs|1456@rsh|热水|RSD|reshui|rs|1457@rxi|容县|RXZ|rongxian|rx|1458@rya|饶阳|RVP|raoyang|ry|1459@rya|汝阳|RYF|ruyang|ry|1460@ryh|绕阳河|RHD|raoyanghe|ryh|1461@rzh|汝州|ROF|ruzhou|rz|1462@sba|石坝|OBJ|shiba|sb|1463@sbc|上板城|SBP|shangbancheng|sbc|1464@sbi|施秉|AQW|shibing|sb|1465@sbn|上板城南|OBP|shangbanchengnan|sbcn|1466@sby|世博园|ZWT|shiboyuan|sby|1467@scb|双城北|SBB|shuangchengbei|scb|1468@sch|商城|SWN|shangcheng|sc|1469@sch|莎车|SCR|shache|sc|1470@sch|顺昌|SCS|shunchang|sc|1471@sch|舒城|OCH|shucheng|sc|1472@sch|神池|SMV|shenchi|sc|1473@sch|沙城|SCP|shacheng|sc|1474@sch|石城|SCT|shicheng|sc|1475@scz|山城镇|SCL|shanchengzhen|scz|1476@sda|山丹|SDJ|shandan|sd|1477@sde|顺德|ORQ|shunde|sd|1478@sde|绥德|ODY|suide|sd|1479@sdo|邵东|SOQ|shaodong|sd|1480@sdo|水洞|SIL|shuidong|sd|1481@sdu|商都|SXC|shangdu|sd|1482@sdu|十渡|SEP|shidu|sd|1483@sdw|四道湾|OUD|sidaowan|sdw|1484@sdy|顺德学院|OJQ|shundexueyuan|sdxy|1485@sfa|绅坊|OLH|shenfang|sf|1486@sfe|双丰|OFB|shuangfeng|sf|1487@sft|四方台|STB|sifangtai|sft|1488@sfu|水富|OTW|shuifu|sf|1489@sgk|三关口|OKJ|sanguankou|sgk|1490@sgl|桑根达来|OGC|sanggendalai|sgdl|1491@sgu|韶关|SNQ|shaoguan|sg|1492@sgz|上高镇|SVK|shanggaozhen|sgz|1493@sha|上杭|JBS|shanghang|sh|1494@sha|沙海|SED|shahai|sh|1495@she|松河|SBM|songhe|sh|1496@she|沙河|SHP|shahe|sh|1497@shk|沙河口|SKT|shahekou|shk|1498@shl|赛汗塔拉|SHC|saihantala|shtl|1499@shs|沙河市|VOP|shaheshi|shs|1500@shs|沙后所|SSD|shahousuo|shs|1501@sht|山河屯|SHL|shanhetun|sht|1502@shx|三河县|OXP|sanhexian|shx|1503@shy|四合永|OHD|siheyong|shy|1504@shz|三汇镇|OZW|sanhuizhen|shz|1505@shz|双河镇|SEL|shuanghezhen|shz|1506@shz|石河子|SZR|shihezi|shz|1507@shz|三合庄|SVP|sanhezhuang|shz|1508@sjd|三家店|ODP|sanjiadian|sjd|1509@sjh|水家湖|SQH|shuijiahu|sjh|1510@sjh|沈家河|OJJ|shenjiahe|sjh|1511@sjh|松江河|SJL|songjianghe|sjh|1512@sji|尚家|SJB|shangjia|sj|1513@sji|孙家|SUB|sunjia|sj|1514@sji|沈家|OJB|shenjia|sj|1515@sji|松江|SAH|songjiang|sj|1516@sjk|三江口|SKD|sanjiangkou|sjk|1517@sjl|司家岭|OLK|sijialing|sjl|1518@sjn|松江南|IMH|songjiangnan|sjn|1519@sjn|石景山南|SRP|shijingshannan|sjsn|1520@sjt|邵家堂|SJJ|shaojiatang|sjt|1521@sjx|三江县|SOZ|sanjiangxian|sjx|1522@sjz|三家寨|SMM|sanjiazhai|sjz|1523@sjz|十家子|SJD|shijiazi|sjz|1524@sjz|松江镇|OZL|songjiangzhen|sjz|1525@sjz|施家嘴|SHM|shijiazui|sjz|1526@sjz|深井子|SWT|shenjingzi|sjz|1527@sld|什里店|OMP|shilidian|sld|1528@sle|疏勒|SUR|shule|sl|1529@slh|疏勒河|SHJ|shulehe|slh|1530@slh|舍力虎|VLD|shelihu|slh|1531@sli|石磷|SPB|shilin|sl|1532@sli|双辽|ZJD|shuangliao|sl|1533@sli|绥棱|SIB|suiling|sl|1534@sli|石岭|SOL|shiling|sl|1535@sli|石林|SLM|shilin|sl|1536@sln|石林南|LNM|shilinnan|sln|1537@slo|石龙|SLQ|shilong|sl|1538@slq|萨拉齐|SLC|salaqi|slq|1539@slu|索伦|SNT|suolun|sl|1540@slu|商洛|OLY|shangluo|sl|1541@slz|沙岭子|SLP|shalingzi|slz|1542@smb|石门县北|VFQ|shimenxianbei|smxb|1543@smn|三门峡南|SCF|sanmenxianan|smxn|1544@smx|三门县|OQH|sanmenxian|smx|1545@smx|石门县|OMQ|shimenxian|smx|1546@smx|三门峡西|SXF|sanmenxiaxi|smxx|1547@sni|肃宁|SYP|suning|sn|1548@son|宋|SOB|song|s|1549@spa|双牌|SBZ|shuangpai|sp|1550@spd|四平东|PPT|sipingdong|spd|1551@spi|遂平|SON|suiping|sp|1552@spt|沙坡头|SFJ|shapotou|spt|1553@sqn|商丘南|SPF|shangqiunan|sqn|1554@squ|水泉|SID|shuiquan|sq|1555@sqx|石泉县|SXY|shiquanxian|sqx|1556@sqz|石桥子|SQT|shiqiaozi|sqz|1557@src|石人城|SRB|shirencheng|src|1558@sre|石人|SRL|shiren|sr|1559@ssh|山市|SQB|shanshi|ss|1560@ssh|神树|SWB|shenshu|ss|1561@ssh|鄯善|SSR|shanshan|ss|1562@ssh|三水|SJQ|sanshui|ss|1563@ssh|泗水|OSK|sishui|ss|1564@ssh|石山|SAD|shishan|ss|1565@ssh|松树|SFT|songshu|ss|1566@ssh|首山|SAT|shoushan|ss|1567@ssj|三十家|SRD|sanshijia|ssj|1568@ssp|三十里堡|SST|sanshilipu|sslb|1569@ssz|松树镇|SSL|songshuzhen|ssz|1570@sta|松桃|MZQ|songtao|st|1571@sth|索图罕|SHX|suotuhan|sth|1572@stj|三堂集|SDH|santangji|stj|1573@sto|石头|OTB|shitou|st|1574@sto|神头|SEV|shentou|st|1575@stu|沙沱|SFM|shatuo|st|1576@swa|上万|SWP|shangwan|sw|1577@swu|孙吴|SKB|sunwu|sw|1578@swx|沙湾县|SXR|shawanxian|swx|1579@sxi|遂溪|SXZ|suixi|sx|1580@sxi|沙县|SAS|shaxian|sx|1581@sxi|绍兴|SOH|shaoxing|sx|1582@sxi|歙县|OVH|shexian|sx|1583@sxi|石岘|SXL|shixian|sj|1584@sxp|上西铺|SXM|shangxipu|sxp|1585@sxz|石峡子|SXJ|shixiazi|sxz|1586@sya|绥阳|SYB|suiyang|sy|1587@sya|沭阳|FMH|shuyang|sy|1588@sya|寿阳|SYV|shouyang|sy|1589@sya|水洋|OYP|shuiyang|sy|1590@syc|三阳川|SYJ|sanyangchuan|syc|1591@syd|上腰墩|SPJ|shangyaodun|syd|1592@syi|三营|OEJ|sanying|sy|1593@syi|顺义|SOP|shunyi|sy|1594@syj|三义井|OYD|sanyijing|syj|1595@syp|三源浦|SYL|sanyuanpu|syp|1596@syu|三原|SAY|sanyuan|sy|1597@syu|上虞|BDH|shangyu|sy|1598@syu|上园|SUD|shangyuan|sy|1599@syu|水源|OYJ|shuiyuan|sy|1600@syz|桑园子|SAJ|sangyuanzi|syz|1601@szb|绥中北|SND|suizhongbei|szb|1602@szb|苏州北|OHH|suzhoubei|szb|1603@szd|宿州东|SRH|suzhoudong|szd|1604@szd|深圳东|BJQ|shenzhendong|szd|1605@szh|深州|OZP|shenzhou|sz|1606@szh|孙镇|OZY|sunzhen|sz|1607@szh|绥中|SZD|suizhong|sz|1608@szh|尚志|SZB|shangzhi|sz|1609@szh|师庄|SNM|shizhuang|sz|1610@szi|松滋|SIN|songzi|sz|1611@szo|师宗|SEM|shizong|sz|1612@szq|苏州园区|KAH|suzhouyuanqu|szyq|1613@szq|苏州新区|ITH|suzhouxinqu|szxq|1614@tan|泰安|TMK|taian|ta|1615@tan|台安|TID|taian|ta|1616@tay|通安驿|TAJ|tonganyi|tay|1617@tba|桐柏|TBF|tongbai|tb|1618@tbe|通北|TBB|tongbei|tb|1619@tch|汤池|TCX|tangchi|tc|1620@tch|桐城|TTH|tongcheng|tc|1621@tch|郯城|TZK|tancheng|tc|1622@tch|铁厂|TCL|tiechang|tc|1623@tcu|桃村|TCK|taocun|tc|1624@tda|通道|TRQ|tongdao|td|1625@tdo|田东|TDZ|tiandong|td|1626@tga|天岗|TGL|tiangang|tg|1627@tgl|土贵乌拉|TGC|tuguiwula|tgwl|1628@tgo|通沟|TOL|tonggou|tg|1629@tgu|太谷|TGV|taigu|tg|1630@tha|塔哈|THX|taha|th|1631@tha|棠海|THM|tanghai|th|1632@the|唐河|THF|tanghe|th|1633@the|泰和|THG|taihe|th|1634@thu|太湖|TKH|taihu|th|1635@tji|团结|TIX|tuanjie|tj|1636@tjj|谭家井|TNJ|tanjiajing|tjj|1637@tjt|陶家屯|TOT|taojiatun|tjt|1638@tjw|唐家湾|PDQ|tangjiawan|tjw|1639@tjz|统军庄|TZP|tongjunzhuang|tjz|1640@tka|泰康|TKX|taikang|tk|1641@tld|吐列毛杜|TMD|tuliemaodu|tlmd|1642@tlh|图里河|TEX|tulihe|tlh|1643@tli|亭亮|TIZ|tingliang|tl|1644@tli|田林|TFZ|tianlin|tl|1645@tli|铜陵|TJH|tongling|tl|1646@tli|铁力|TLB|tieli|tl|1647@tlx|铁岭西|PXT|tielingxi|tlx|1648@tme|天门|TMN|tianmen|tm|1649@tmn|天门南|TNN|tianmennan|tmn|1650@tms|太姥山|TLS|taimushan|tms|1651@tmt|土牧尔台|TRC|tumuertai|tmet|1652@tmz|土门子|TCJ|tumenzi|tmz|1653@tna|潼南|TVW|tongnan|tn|1654@tna|洮南|TVT|taonan|tn|1655@tpc|太平川|TIT|taipingchuan|tpc|1656@tpz|太平镇|TEB|taipingzhen|tpz|1657@tqi|图强|TQX|tuqiang|tq|1658@tqi|台前|TTK|taiqian|tq|1659@tql|天桥岭|TQL|tianqiaoling|tql|1660@tqz|土桥子|TQJ|tuqiaozi|tqz|1661@tsc|汤山城|TCT|tangshancheng|tsc|1662@tsh|桃山|TAB|taoshan|ts|1663@tsz|塔石嘴|TIM|tashizui|tsz|1664@ttu|通途|TUT|tongtu|tt|1665@twh|汤旺河|THB|tangwanghe|twh|1666@txi|同心|TXJ|tongxin|tx|1667@txi|土溪|TSW|tuxi|tx|1668@txi|桐乡|TCH|tongxiang|tx|1669@tya|田阳|TRZ|tianyang|ty|1670@tyi|天义|TND|tianyi|ty|1671@tyi|汤阴|TYF|tangyin|ty|1672@tyl|驼腰岭|TIL|tuoyaoling|tyl|1673@tys|太阳山|TYJ|taiyangshan|tys|1674@tyu|汤原|TYB|tangyuan|ty|1675@tyy|塔崖驿|TYP|tayayi|tyy|1676@tzd|滕州东|TEK|tengzhoudong|tzd|1677@tzh|台州|TZH|taizhou|tz|1678@tzh|天祝|TZJ|tianzhu|tz|1679@tzh|滕州|TXK|tengzhou|tz|1680@tzh|天镇|TZV|tianzhen|tz|1681@tzl|桐子林|TEW|tongzilin|tzl|1682@tzs|天柱山|QWH|tianzhushan|tzs|1683@wan|文安|WBP|wenan|wa|1684@wan|武安|WAP|wuan|wa|1685@waz|王安镇|WVP|wanganzhen|waz|1686@wca|旺苍|WEW|wangcang|wc|1687@wcg|五叉沟|WCT|wuchagou|wcg|1688@wch|文昌|WEQ|wenchang|wc|1689@wch|温春|WDB|wenchun|wc|1690@wdc|五大连池|WRB|wudalianchi|wdlc|1691@wde|文登|WBK|wendeng|wd|1692@wdg|五道沟|WDL|wudaogou|wdg|1693@wdh|五道河|WHP|wudaohe|wdh|1694@wdi|文地|WNZ|wendi|wd|1695@wdo|卫东|WVT|weidong|wd|1696@wds|武当山|WRN|wudangshan|wds|1697@wdu|望都|WDP|wangdu|wd|1698@weh|乌尔旗汗|WHX|wuerqihan|weqh|1699@wfa|潍坊|WFK|weifang|wf|1700@wft|万发屯|WFB|wanfatun|wft|1701@wfu|王府|WUT|wangfu|wf|1702@wfx|瓦房店西|WXT|wafangdianxi|wfdx|1703@wga|王岗|WGB|wanggang|wg|1704@wgo|武功|WGY|wugong|wg|1705@wgo|湾沟|WGL|wangou|wg|1706@wgt|吴官田|WGM|wuguantian|wgt|1707@wha|乌海|WVC|wuhai|wh|1708@whe|苇河|WHB|weihe|wh|1709@whu|卫辉|WHF|weihui|wh|1710@wjc|吴家川|WCJ|wujiachuan|wjc|1711@wji|五家|WUB|wujia|wj|1712@wji|威箐|WAM|weiqing|wq|1713@wji|午汲|WJP|wuji|wj|1714@wji|渭津|WJL|weijin|wj|1715@wjw|王家湾|WJJ|wangjiawan|wjw|1716@wke|倭肯|WQB|woken|wk|1717@wks|五棵树|WKT|wukeshu|wks|1718@wlb|五龙背|WBT|wulongbei|wlb|1719@wld|乌兰哈达|WLC|wulanhada|wlhd|1720@wle|万乐|WEB|wanle|wl|1721@wlg|瓦拉干|WVX|walagan|wlg|1722@wli|温岭|VHH|wenling|wl|1723@wli|五莲|WLK|wulian|wl|1724@wlq|乌拉特前旗|WQC|wulateqianqi|wltqq|1725@wls|乌拉山|WSC|wulashan|wls|1726@wlt|卧里屯|WLX|wolitun|wlt|1727@wnb|渭南北|WBY|weinanbei|wnb|1728@wne|乌奴耳|WRX|wunuer|wne|1729@wni|万宁|WNQ|wanning|wn|1730@wni|万年|WWG|wannian|wn|1731@wnn|渭南南|WVY|weinannan|wnn|1732@wnz|渭南镇|WNJ|weinanzhen|wnz|1733@wpi|沃皮|WPT|wopi|wp|1734@wpu|吴堡|WUY|wupu|wb|1735@wqi|吴桥|WUP|wuqiao|wq|1736@wqi|汪清|WQL|wangqing|wq|1737@wqi|武清|WWP|wuqing|wq|1738@wsh|武山|WSJ|wushan|ws|1739@wsh|文水|WEV|wenshui|ws|1740@wsz|魏善庄|WSP|weishanzhuang|wsz|1741@wto|王瞳|WTP|wangtong|wt|1742@wts|五台山|WSV|wutaishan|wts|1743@wtz|王团庄|WZJ|wangtuanzhuang|wtz|1744@wwu|五五|WVR|wuwu|ww|1745@wxd|无锡东|WGH|wuxidong|wxd|1746@wxi|卫星|WVB|weixing|wx|1747@wxi|闻喜|WXV|wenxi|wx|1748@wxi|武乡|WVV|wuxiang|wx|1749@wxq|无锡新区|IFH|wuxixinqu|wxxq|1750@wxu|武穴|WXN|wuxue|wx|1751@wxu|吴圩|WYZ|wuxu|wy|1752@wya|王杨|WYB|wangyang|wy|1753@wyi|五营|WWB|wuying|wy|1754@wyi|武义|RYH|wuyi|wy|1755@wyt|瓦窑田|WIM|wayaotian|wjt|1756@wyu|五原|WYC|wuyuan|wy|1757@wzg|苇子沟|WZL|weizigou|wzg|1758@wzh|韦庄|WZY|weizhuang|wz|1759@wzh|五寨|WZV|wuzhai|wz|1760@wzt|王兆屯|WZB|wangzhaotun|wzt|1761@wzz|微子镇|WQP|weizizhen|wzz|1762@wzz|魏杖子|WKD|weizhangzi|wzz|1763@xan|新安|EAM|xinan|xa|1764@xan|兴安|XAZ|xingan|xa|1765@xax|新安县|XAF|xinanxian|xax|1766@xba|新保安|XAP|xinbaoan|xba|1767@xbc|下板城|EBP|xiabancheng|xbc|1768@xbl|西八里|XLP|xibali|xbl|1769@xch|宣城|ECH|xuancheng|xc|1770@xch|兴城|XCD|xingcheng|xc|1771@xcu|小村|XEM|xiaocun|xc|1772@xcy|新绰源|XRX|xinchuoyuan|xcy|1773@xcz|下城子|XCB|xiachengzi|xcz|1774@xcz|新城子|XCT|xinchengzi|xcz|1775@xde|喜德|EDW|xide|xd|1776@xdj|小得江|EJM|xiaodejiang|xdj|1777@xdm|西大庙|XMP|xidamiao|xdm|1778@xdo|小董|XEZ|xiaodong|xd|1779@xdo|小东|XOD|xiaodong|xdo|1780@xfe|息烽|XFW|xifeng|xf|1781@xfe|信丰|EFG|xinfeng|xf|1782@xfe|襄汾|XFV|xiangfen|xf|1783@xga|新干|EGG|xingan|xg|1784@xga|孝感|XGN|xiaogan|xg|1785@xgc|西固城|XUJ|xigucheng|xgc|1786@xgy|夏官营|XGJ|xiaguanying|xgy|1787@xgz|西岗子|NBB|xigangzi|xgz|1788@xhe|襄河|XXB|xianghe|xh|1789@xhe|新和|XIR|xinhe|xh|1790@xhe|宣和|XWJ|xuanhe|xh|1791@xhj|斜河涧|EEP|xiehejian|xhj|1792@xht|新华屯|XAX|xinhuatun|xht|1793@xhu|新华|XHB|xinhua|xh|1794@xhu|新化|EHQ|xinhua|xh|1795@xhu|宣化|XHP|xuanhua|xh|1796@xhx|兴和西|XEC|xinghexi|xhx|1797@xhy|小河沿|XYD|xiaoheyan|xhy|1798@xhy|下花园|XYP|xiahuayuan|xhy|1799@xhz|小河镇|EKY|xiaohezhen|xhz|1800@xji|徐家|XJB|xujia|xj|1801@xji|峡江|EJG|xiajiang|xj|1802@xji|新绛|XJV|xinjiang|xj|1803@xji|辛集|ENP|xinji|xj|1804@xji|新江|XJM|xinjiang|xj|1805@xjk|西街口|EKM|xijiekou|xjk|1806@xjt|许家屯|XJT|xujiatun|xjt|1807@xjt|许家台|XTJ|xujiatai|xjt|1808@xjz|谢家镇|XMT|xiejiazhen|xjz|1809@xka|兴凯|EKB|xingkai|xk|1810@xla|小榄|EAQ|xiaolan|xl|1811@xla|香兰|XNB|xianglan|xl|1812@xld|兴隆店|XDD|xinglongdian|xld|1813@xle|新乐|ELP|xinle|xl|1814@xli|新林|XPX|xinlin|xl|1815@xli|小岭|XLB|xiaoling|xl|1816@xli|新李|XLJ|xinli|xl|1817@xli|西林|XYB|xilin|xl|1818@xli|西柳|GCT|xiliu|xl|1819@xli|仙林|XPH|xianlin|xl|1820@xlt|新立屯|XLD|xinlitun|xlt|1821@xlz|兴隆镇|XZB|xinglongzhen|xlz|1822@xlz|新立镇|XGT|xinlizhen|xlz|1823@xmi|新民|XMD|xinmin|xm|1824@xms|西麻山|XMB|ximashan|xms|1825@xmt|下马塘|XAT|xiamatang|xmt|1826@xna|孝南|XNV|xiaonan|xn|1827@xnb|咸宁北|XRN|xianningbei|xnb|1828@xni|兴宁|ENQ|xingning|xn|1829@xni|咸宁|XNN|xianning|xn|1830@xpd|犀浦东|XAW|xipudong|xpd|1831@xpi|西平|XPN|xiping|xp|1832@xpi|兴平|XPY|xingping|xp|1833@xpt|新坪田|XPM|xinpingtian|xpt|1834@xpu|霞浦|XOS|xiapu|xp|1835@xpu|溆浦|EPQ|xupu|xp|1836@xpu|犀浦|XIW|xipu|xp|1837@xqi|新青|XQB|xinqing|xq|1838@xqi|新邱|XQD|xinqiu|xq|1839@xqp|兴泉堡|XQJ|xingquanpu|xqp|1840@xrq|仙人桥|XRL|xianrenqiao|xrq|1841@xsg|小寺沟|ESP|xiaosigou|xsg|1842@xsh|杏树|XSB|xingshu|xs|1843@xsh|夏石|XIZ|xiashi|xs|1844@xsh|浠水|XZN|xishui|xs|1845@xsh|下社|XSV|xiashe|xs|1846@xsh|徐水|XSP|xushui|xs|1847@xsh|小哨|XAM|xiaoshao|xs|1848@xsp|新松浦|XOB|xinsongpu|xsp|1849@xst|杏树屯|XDT|xingshutun|xst|1850@xsw|许三湾|XSJ|xusanwan|xsw|1851@xta|湘潭|XTQ|xiangtan|xt|1852@xta|邢台|XTP|xingtai|xt|1853@xtx|仙桃西|XAN|xiantaoxi|xtx|1854@xtz|下台子|EIP|xiataizi|xtz|1855@xwe|徐闻|XJQ|xuwen|xw|1856@xwp|新窝铺|EPD|xinwopu|xwp|1857@xwu|修武|XWF|xiuwu|xw|1858@xxi|新县|XSN|xinxian|xx|1859@xxi|西乡|XQY|xixiang|xx|1860@xxi|湘乡|XXQ|xiangxiang|xx|1861@xxi|西峡|XIF|xixia|xx|1862@xxi|孝西|XOV|xiaoxi|xx|1863@xxj|小新街|XXM|xiaoxinjie|xxj|1864@xxx|新兴县|XGQ|xinxingxian|xxx|1865@xxz|西小召|XZC|xixiaozhao|xxz|1866@xxz|小西庄|XXP|xiaoxizhuang|xxz|1867@xya|向阳|XDB|xiangyang|xy|1868@xya|旬阳|XUY|xunyang|xy|1869@xyb|旬阳北|XBY|xunyangbei|xyb|1870@xyd|襄阳东|XWN|xiangyangdong|xyd|1871@xye|兴业|SNZ|xingye|xy|1872@xyg|小雨谷|XHM|xiaoyugu|xyg|1873@xyi|信宜|EEQ|xinyi|xy|1874@xyj|小月旧|XFM|xiaoyuejiu|xyj|1875@xyq|小扬气|XYX|xiaoyangqi|xyq|1876@xyu|祥云|EXM|xiangyun|xy|1877@xyu|襄垣|EIF|xiangyuan|xy|1878@xyx|夏邑县|EJH|xiayixian|xyx|1879@xyy|新友谊|EYB|xinyouyi|xyy|1880@xyz|新阳镇|XZJ|xinyangzhen|xyz|1881@xzd|徐州东|UUH|xuzhoudong|xzd|1882@xzf|新帐房|XZX|xinzhangfang|xzf|1883@xzh|悬钟|XRP|xuanzhong|xz|1884@xzh|新肇|XZT|xinzhao|xz|1885@xzh|忻州|XXV|xinzhou|xz|1886@xzi|汐子|XZD|xizi|xz|1887@xzm|西哲里木|XRD|xizhelimu|xzlm|1888@xzz|新杖子|ERP|xinzhangzi|xzz|1889@yan|姚安|YAC|yaoan|ya|1890@yan|依安|YAX|yian|ya|1891@yan|永安|YAS|yongan|ya|1892@yax|永安乡|YNB|yonganxiang|yax|1893@ybl|亚布力|YBB|yabuli|ybl|1894@ybs|元宝山|YUD|yuanbaoshan|ybs|1895@yca|羊草|YAB|yangcao|yc|1896@ycd|秧草地|YKM|yangcaodi|ycd|1897@ych|阳澄湖|AIH|yangchenghu|ych|1898@ych|迎春|YYB|yingchun|yc|1899@ych|叶城|YER|yecheng|yc|1900@ych|盐池|YKJ|yanchi|yc|1901@ych|砚川|YYY|yanchuan|yc|1902@ych|阳春|YQQ|yangchun|yc|1903@ych|宜城|YIN|yicheng|yc|1904@ych|应城|YHN|yingcheng|yc|1905@ych|禹城|YCK|yucheng|yc|1906@ych|羊场|YED|yangchang|yc|1907@ych|阳城|YNF|yangcheng|yc|1908@ych|阳岔|YAL|yangcha|yc|1909@ych|郓城|YPK|yuncheng|yc|1910@ych|雁翅|YAP|yanchi|yc|1911@ycl|云彩岭|ACP|yuncailing|ycl|1912@ycx|虞城县|IXH|yuchengxian|ycx|1913@ycz|营城子|YCT|yingchengzi|ycz|1914@yde|永登|YDJ|yongdeng|yd|1915@yde|英德|YDQ|yingde|yd|1916@ydi|尹地|YDM|yindi|yd|1917@ydi|永定|YGS|yongding|yd|1918@yds|雁荡山|YGH|yandangshan|yds|1919@ydu|于都|YDG|yudu|yd|1920@ydu|园墩|YAJ|yuandun|yd|1921@ydx|英德西|IIQ|yingdexi|ydx|1922@yfy|永丰营|YYM|yongfengying|yfy|1923@yga|杨岗|YRB|yanggang|yg|1924@yga|阳高|YOV|yanggao|yg|1925@ygu|阳谷|YIK|yanggu|yg|1926@yha|友好|YOB|youhao|yh|1927@yha|余杭|EVH|yuhang|yh|1928@yhc|沿河城|YHP|yanhecheng|yhc|1929@yhu|岩会|AEP|yanhui|yh|1930@yjh|羊臼河|YHM|yangjiuhe|yjh|1931@yji|永嘉|URH|yongjia|yj|1932@yji|营街|YAM|yingjie|yj|1933@yji|盐津|AEW|yanjin|yj|1934@yji|余江|YHG|yujiang|yj|1935@yji|叶集|YCH|yeji|yj|1936@yji|燕郊|AJP|yanjiao|yj|1937@yji|姚家|YAT|yaojia|yj|1938@yjj|岳家井|YGJ|yuejiajing|yjj|1939@yjp|一间堡|YJT|yijianpu|yjb|1940@yjs|英吉沙|YIR|yingjisha|yjs|1941@yjs|云居寺|AFP|yunjusi|yjs|1942@yjz|燕家庄|AZK|yanjiazhuang|yjz|1943@yka|永康|RFH|yongkang|yk|1944@ykd|营口东|YGT|yingkoudong|ykd|1945@yla|银浪|YJX|yinlang|yl|1946@yla|永郎|YLW|yonglang|yl|1947@ylb|宜良北|YSM|yiliangbei|ylb|1948@yld|永乐店|YDY|yongledian|yld|1949@ylh|伊拉哈|YLX|yilaha|ylh|1950@yli|伊林|YLB|yilin|yl|1951@yli|杨陵|YSY|yangling|yl|1952@yli|彝良|ALW|yiliang|yl|1953@yli|杨林|YLM|yanglin|yl|1954@ylp|余粮堡|YLD|yuliangpu|ylb|1955@ylq|杨柳青|YQP|yangliuqing|ylq|1956@ylt|月亮田|YUM|yueliangtian|ylt|1957@ylw|亚龙湾|TWQ|yalongwan|ylw|1958@yma|义马|YMF|yima|ym|1959@yme|玉门|YXJ|yumen|ym|1960@yme|云梦|YMN|yunmeng|ym|1961@ymo|元谋|YMM|yuanmou|ym|1962@ymp|阳明堡|YVV|yangmingpu|ymp|1963@yms|一面山|YST|yimianshan|yms|1964@yna|沂南|YNK|yinan|yn|1965@yna|宜耐|YVM|yinai|yn|1966@ynd|伊宁东|YNR|yiningdong|ynd|1967@yps|营盘水|YZJ|yingpanshui|yps|1968@ypu|羊堡|ABM|yangpu|yp|1969@yqb|阳泉北|YPP|yangquanbei|yqb|1970@yqi|乐清|UPH|yueqing|yq|1971@yqi|焉耆|YSR|yanqi|yq|1972@yqi|源迁|AQK|yuanqian|yq|1973@yqt|姚千户屯|YQT|yaoqianhutun|yqht|1974@yqu|阳曲|YQV|yangqu|yq|1975@ysg|榆树沟|YGP|yushugou|ysg|1976@ysh|月山|YBF|yueshan|ys|1977@ysh|玉石|YSJ|yushi|ys|1978@ysh|偃师|YSF|yanshi|ys|1979@ysh|沂水|YUK|yishui|ys|1980@ysh|榆社|YSV|yushe|ys|1981@ysh|窑上|ASP|yaoshang|ys|1982@ysh|元氏|YSP|yuanshi|ys|1983@ysl|杨树岭|YAD|yangshuling|ysl|1984@ysp|野三坡|AIP|yesanpo|ysp|1985@yst|榆树屯|YSX|yushutun|yst|1986@yst|榆树台|YUT|yushutai|yst|1987@ysz|鹰手营子|YIP|yingshouyingzi|ysyz|1988@yta|源潭|YTQ|yuantan|yt|1989@ytp|牙屯堡|YTZ|yatunpu|ytb|1990@yts|烟筒山|YSL|yantongshan|yts|1991@ytt|烟筒屯|YUX|yantongtun|ytt|1992@yws|羊尾哨|YWM|yangweishao|yws|1993@yxi|越西|YHW|yuexi|yx|1994@yxi|攸县|YOG|youxian|yx|1995@yxi|玉溪|YXM|yuxi|yx|1996@yxi|永修|ACG|yongxiu|yx|1997@yya|弋阳|YIG|yiyang|yy|1998@yya|酉阳|AFW|youyang|yy|1999@yya|余姚|YYH|yuyao|yy|2000@yyd|岳阳东|YIQ|yueyangdong|yyd|2001@yyi|阳邑|ARP|yangyi|yy|2002@yyu|鸭园|YYL|yayuan|yy|2003@yyz|鸳鸯镇|YYJ|yuanyangzhen|yyz|2004@yzb|燕子砭|YZY|yanzibian|yzb|2005@yzh|宜州|YSZ|yizhou|yz|2006@yzh|仪征|UZH|yizheng|yz|2007@yzh|兖州|YZK|yanzhou|yz|2008@yzi|迤资|YQM|yizi|yz|2009@yzw|羊者窝|AEM|yangzhewo|wzw|2010@yzz|杨杖子|YZD|yangzhangzi|yzz|2011@zan|镇安|ZEY|zhenan|za|2012@zan|治安|ZAD|zhian|za|2013@zba|招柏|ZBP|zhaobai|zb|2014@zbw|张百湾|ZUP|zhangbaiwan|zbw|2015@zch|枝城|ZCN|zhicheng|zc|2016@zch|子长|ZHY|zichang|zc|2017@zch|诸城|ZQK|zhucheng|zc|2018@zch|邹城|ZIK|zoucheng|zc|2019@zch|赵城|ZCV|zhaocheng|zc|2020@zda|章党|ZHT|zhangdang|zd|2021@zdo|肇东|ZDB|zhaodong|zd|2022@zfp|照福铺|ZFM|zhaofupu|zfp|2023@zgt|章古台|ZGD|zhanggutai|zgt|2024@zgu|赵光|ZGB|zhaoguang|zg|2025@zhe|中和|ZHX|zhonghe|zh|2026@zhm|中华门|VNH|zhonghuamen|zhm|2027@zjb|枝江北|ZIN|zhijiangbei|zjb|2028@zjc|钟家村|ZJY|zhongjiacun|zjc|2029@zjg|朱家沟|ZUB|zhujiagou|zjg|2030@zjg|紫荆关|ZYP|zijingguan|zjg|2031@zji|周家|ZOB|zhoujia|zj|2032@zji|诸暨|ZDH|zhuji|zj|2033@zjn|镇江南|ZEH|zhenjiangnan|zjn|2034@zjt|周家屯|ZOD|zhoujiatun|zjt|2035@zjw|褚家湾|CWJ|zhujiawan|cjw|2036@zjx|湛江西|ZWQ|zhanjiangxi|zjx|2037@zjy|朱家窑|ZUJ|zhujiayao|zjy|2038@zjz|曾家坪子|ZBW|zengjiapingzi|zjpz|2039@zla|张兰|ZLV|zhanglan|zla|2040@zla|镇赉|ZLT|zhenlai|zl|2041@zli|枣林|ZIV|zaolin|zl|2042@zlt|扎鲁特|ZLD|zhalute|zlt|2043@zlx|扎赉诺尔西|ZXX|zhalainuoerxi|zlnex|2044@zmt|樟木头|ZOQ|zhangmutou|zmt|2045@zmu|中牟|ZGF|zhongmu|zm|2046@znd|中宁东|ZDJ|zhongningdong|znd|2047@zni|中宁|VNJ|zhongning|zn|2048@znn|中宁南|ZNJ|zhongningnan|znn|2049@zpi|镇平|ZPF|zhenping|zp|2050@zpi|漳平|ZPS|zhangping|zp|2051@zpu|泽普|ZPR|zepu|zp|2052@zqi|枣强|ZVP|zaoqiang|zq|2053@zqi|张桥|ZQY|zhangqiao|zq|2054@zqi|章丘|ZTK|zhangqiu|zq|2055@zrh|朱日和|ZRC|zhurihe|zrh|2056@zrl|泽润里|ZLM|zerunli|zrl|2057@zsb|中山北|ZGQ|zhongshanbei|zsb|2058@zsd|樟树东|ZOG|zhangshudong|zsd|2059@zsh|中山|ZSQ|zhongshan|zs|2060@zsh|柞水|ZSY|zhashui|zs|2061@zsh|钟山|ZSZ|zhongshan|zs|2062@zsh|樟树|ZSG|zhangshu|zs|2063@zwo|珠窝|ZOP|zhuwo|zw|2064@zwt|张维屯|ZWB|zhangweitun|zwt|2065@zwu|彰武|ZWD|zhangwu|zw|2066@zxi|棕溪|ZOY|zongxi|zx|2067@zxi|钟祥|ZTN|zhongxiang|zx|2068@zxi|资溪|ZXS|zixi|zx|2069@zxi|镇西|ZVT|zhenxi|zx|2070@zxi|张辛|ZIP|zhangxin|zx|2071@zxq|正镶白旗|ZXC|zhengxiangbaiqi|zxbq|2072@zya|紫阳|ZVY|ziyang|zy|2073@zya|枣阳|ZYN|zaoyang|zy|2074@zyb|竹园坝|ZAW|zhuyuanba|zyb|2075@zye|张掖|ZYJ|zhangye|zy|2076@zyu|镇远|ZUW|zhenyuan|zy|2077@zyx|朱杨溪|ZXW|zhuyangxi|zyx|2078@zzd|漳州东|GOS|zhangzhoudong|zzd|2079@zzh|漳州|ZUS|zhangzhou|zz|2080@zzh|壮志|ZUX|zhuangzhi|zz|2081@zzh|子洲|ZZY|zizhou|zz|2082@zzh|中寨|ZZM|zhongzhai|zz|2083@zzh|涿州|ZXP|zhuozhou|zz|2084@zzi|咋子|ZAL|zhazi|zz|2085@zzs|卓资山|ZZC|zhuozishan|zzs|2086@zzx|株洲西|ZAQ|zhuzhouxi|zzx|2087@are|安仁|ARG|anren|ar|2088@ayd|安阳东|ADF|anyangdong|ayd|2089@bch|栟茶|FWH|bencha|bc|2090@bdd|保定东|BMP|baodingdong|bdd|2091@bha|滨海|FHP|binhai|bh|2092@bhb|滨海北|FCP|binhaibei|bhb|2093@bjn|宝鸡南|BBY|baojinan|bjn|2094@cln|茶陵南|CNG|chalingnan|cln|2095@csb|长寿北|COW|changshoubei|csb|2096@csh|潮汕|CBQ|chaoshan|cs|2097@cxi|长兴|CBH|changxing|cx|2098@cya|长阳|CYN|changyang|cy|2099@cya|潮阳|CNQ|chaoyang|cy|2100@dad|东安东|DCZ|dongandong|dad|2101@ddh|东戴河|RDD|dongdaihe|ddh|2102@deh|东二道河|DRB|dongerdaohe|dedh|2103@dgu|东莞|RTQ|dongguan|dg|2104@dju|大苴|DIM|daju|dj|2105@dli|大荔|DNY|dali|dl|2106@dqg|大青沟|DSD|daqinggou|dqg|2107@dqi|德清|DRH|deqing|dq|2108@dyb|大冶北|DBN|dayebei|dyb|2109@dzd|定州东|DOP|dingzhoudong|dzd|2110@ezd|鄂州东|EFN|ezhoudong|ezd|2111@fcb|防城港北|FBZ|fangchenggangbei|fcgb|2112@fch|富川|FDZ|fuchuan|fc|2113@fdu|丰都|FUW|fengdu|fd|2114@flb|涪陵北|FEW|fulingbei|flb|2115@fyu|抚远|FYB|fuyuan|fy|2116@fzh|抚州|FZG|fuzhou|fz|2117@gan|广安南|VUW|guangannan|gan|2118@gbd|高碑店东|GMP|gaobeidiandong|gbdd|2119@gdn|葛店南|GNN|gediannan|gdn|2120@gju|革居|GEM|geju|gj|2121@gmc|光明城|IMQ|guangmingcheng|gmc|2122@gpi|桂平|GAZ|guiping|gp|2123@gtb|广通北|GPM|guangtongbei|gtb|2124@gyx|高邑西|GNP|gaoyixi|gyx|2125@hbd|鹤壁东|HFF|hebidong|hbd|2126@hcg|寒葱沟|HKB|hanconggou|hcg|2127@hdd|邯郸东|HPP|handandong|hdd|2128@hdo|惠东|KDQ|huidong|hd|2129@hdx|洪洞西|HTV|hongdongxi|hdx|2130@hfc|合肥北城|COH|hefeibeicheng|hfbc|2131@hga|黄冈|KGN|huanggang|hg|2132@hgd|黄冈东|KAN|huanggangdong|hgd|2133@hgd|横沟桥东|HNN|henggouqiaodong|hgqd|2134@hgx|黄冈西|KXN|huanggangxi|hgx|2135@hhe|洪河|HPB|honghe|hh|2136@hhu|花湖|KHN|huahu|hh|2137@hme|鲘门|KMQ|houmen|hm|2138@hme|虎门|IUQ|humen|hm|2139@hmn|哈密南|HLR|haminan|hmn|2140@hmx|侯马西|HPV|houmaxi|hmx|2141@hna|衡南|HNG|hengnan|hn|2142@hnd|淮南东|HOH|huainandong|hnd|2143@hpu|合浦|HVZ|hepu|hp|2144@hqi|霍邱|FBH|huoqiu|hq|2145@hrd|怀仁东|HFV|huairendong|hrd|2146@hrd|华容东|HPN|huarongdong|hrd|2147@hrn|华容南|KRN|huarongnan|hrn|2148@hsb|黄石北|KSN|huangshibei|hsb|2149@hsd|贺胜桥东|HLN|heshengqiaodong|hsqd|2150@hsn|花山南|KNN|huashannan|hsn|2151@hzd|霍州东|HWV|huozhoudong|hzd|2152@hzn|惠州南|KNQ|huizhounan|hzn|2153@jlb|军粮城北|JMP|junliangchengbei|jlcb|2154@jle|将乐|JLS|jiangle|jl|2155@jnb|建宁县北|JCS|jianningxianbei|jnxb|2156@jni|江宁|JJH|jiangning|jn|2157@jrx|句容西|JWH|jurongxi|jrx|2158@jsh|建水|JSM|jianshui|js|2159@jss|界首市|JUN|jieshoushi|jss|2160@jxd|介休东|JDV|jiexiudong|jxd|2161@jzh|晋中|JZV|jinzhong|jz|2162@klu|库伦|KLD|kulun|kl|2163@kta|葵潭|KTQ|kuitan|kt|2164@lbb|来宾北|UCZ|laibinbei|lbb|2165@lbi|灵璧|GMH|lingbi|lb|2166@ldy|离堆公园|INW|liduigongyuan|ldgy|2167@lfe|陆丰|LLQ|lufeng|lf|2168@lfn|禄丰南|LQM|lufengnan|lfn|2169@lfx|临汾西|LXV|linfenxi|lfx|2170@lhe|滦河|UDP|luanhe|lh|2171@lhx|漯河西|LBN|luohexi|lhx|2172@lsd|灵石东|UDV|lingshidong|lsd|2173@lsh|龙市|LAG|longshi|sh|2174@lsh|溧水|LDH|lishui|ls|2175@ltx|黎塘西|UKZ|litangxi|ltx|2176@lya|溧阳|LEH|liyang|ly|2177@mgd|明港东|MDN|minggangdong|mgd|2178@mns|玛纳斯|MSR|manasi|mns|2179@msh|庙山|MSN|miaoshan|ms|2180@mzb|蒙自北|MBM|mengzibei|mzb|2181@nch|南城|NDG|nancheng|nc|2182@ncx|南昌西|NXG|nanchangxi|ncx|2183@nfe|南丰|NFG|nanfeng|nf|2184@nhd|南湖东|NDN|nanhudong|nhd|2185@nmu|尼木|NMO|nimu|nm|2186@pan|普安|PAN|puan|pa|2187@pni|普宁|PEQ|puning|pn|2188@pnn|平南南|PAZ|pingnannan|pn|2189@pyc|平遥古城|PDV|pingyaogucheng|pygc|2190@pzh|彭州|PMW|pengzhou|pz|2191@qdb|青岛北|QHK|qingdaobei|qdb|2192@qdo|祁东|QMQ|qidong|qd|2193@qfe|前锋|QFB|qianfeng|qf|2194@qsh|岐山|QAY|qishan|qs|2195@qsh|庆盛|QSQ|qingsheng|qs|2196@qsx|曲水县|QSO|qushuixian|qsx|2197@qxd|祁县东|QGV|qixiandong|qxd|2198@qya|祁阳|QWQ|qiyang|qy|2199@qzn|全州南|QNZ|quanzhounan|qzn|2200@rbu|仁布|RUO|renbu|rb|2201@rdo|如东|RIH|rudong|rd|2202@rkz|日喀则|RKO|rikaze|rkz|2203@rpi|饶平|RVQ|raoping|rp|2204@sho|泗洪|GQH|sihong|sh|2205@smb|三明北|SHS|sanmingbei|smb|2206@spd|山坡东|SBN|shanpodong|spd|2207@sqi|沈丘|SQN|shenqiu|sq|2208@swe|汕尾|OGQ|shanwei|sw|2209@sxb|绍兴北|SLH|shaoxingbei|sxb|2210@sxi|泗县|GPH|sixian|sx|2211@sya|泗阳|MPH|siyang|sy|2212@syb|上虞北|SSH|shangyubei|syb|2213@syi|山阴|SNV|shanyin|sy|2214@szb|深圳北|IOQ|shenzhenbei|szb|2215@szh|神州|SRQ|shenzhou|sz|2216@szs|深圳坪山|IFQ|shenzhenpingshan|szps|2217@szs|石嘴山|QQJ|shizuishan|szs|2218@szx|石柱县|OSW|shizhuxian|szx|2219@tdd|土地堂东|TTN|tuditangdong|tdtd|2220@tgx|太谷西|TIV|taiguxi|tgx|2221@tha|通海|TAM|tonghai|th|2222@thx|通化县|TXL|tonghuaxian|thx|2223@tni|泰宁|TNS|taining|tn|2224@txh|汤逊湖|THN|tangxunhu|txh|2225@txi|藤县|TAZ|tengxian|tx|2226@tyn|太原南|TNV|taiyuannan|tyn|2227@wln|乌龙泉南|WFN|wulongquannan|wlqn|2228@wns|五女山|WET|wunvshan|wns|2229@wws|瓦屋山|WAH|wawushan|wws|2230@wxx|闻喜西|WOV|wenxixi|wxx|2231@wzn|梧州南|WBZ|wuzhounan|wzn|2232@xab|兴安北|XDZ|xinganbei|xab|2233@xcd|许昌东|XVF|xuchangdong|xcd|2234@xch|项城|ERN|xiangcheng|xc|2235@xfe|西丰|XFT|xifeng|xf|2236@xfx|襄汾西|XTV|xiangfenxi|xfx|2237@xgb|孝感北|XJN|xiaoganbei|xgb|2238@xnd|咸宁东|XKN|xianningdong|xnd|2239@xnn|咸宁南|UNN|xianningnan|xnn|2240@xro|协荣|ROO|xierong|xr|2241@xtd|邢台东|EDP|xingtaidong|xtd|2242@xxd|新乡东|EGF|xinxiangdong|xxd|2243@xyc|西阳村|XQF|xiyangcun|xyc|2244@xyd|信阳东|OYN|xinyangdong|xyd|2245@xyd|咸阳秦都|XOY|xianyangqindu|xyqd|2246@ybl|迎宾路|YFW|yingbinlu|ybl|2247@ycb|运城北|ABV|yunchengbei|ycb|2248@ych|岳池|AWW|yuechi|yc|2249@yfn|永福南|YBZ|yongfunan|yfn|2250@yge|雨格|VTM|yuge|yg|2251@yhe|洋河|GTH|yanghe|yh|2252@yjb|永济北|AJV|yongjibei|yjb|2253@yli|炎陵|YAG|yanling|yl|2254@yln|杨陵南|YEY|yanglingnan|yln|2255@yta|永泰|YTS|yongtai|yt|2256@yxi|尤溪|YXS|youxi|yx|2257@yxi|云霄|YBS|yunxiao|yx|2258@yxi|宜兴|YUH|yixing|yx|2259@yxi|应县|YZV|yingxian|yx|2260@yxn|攸县南|YXG|youxiannan|yxn|2261@yyb|余姚北|CTH|yuyaobei|yyb|2262@zan|诏安|ZDS|zhaoan|za|2263@zdc|正定机场|ZHP|zhengdingjichang|zdjc|2264@zfd|纸坊东|ZMN|zhifangdong|zfd|2265@zji|织金|IZW|zhijin|zj|2266@zli|左岭|ZSN|zuoling|zl|2267@zmx|驻马店西|ZLN|zhumadianxi|zmdx|2268@zpu|漳浦|ZCS|zhangpu|zp|2269@zqi|庄桥|ZQH|zhuangqiao|zq|2270@zzd|涿州东|ZAP|zhuozhoudong|zzd|2271@zzd|卓资东|ZDC|zhuozidong|zzd|2272@zzd|郑州东|ZAF|zhengzhoudong|zzd|2273";
        String[] ss = station_names_1.split("@");
        for (int i = 0; i < 250; i++) {
            try {
                int randominti = new Random().nextInt(ss.length - 1);
                String s1 = ss[randominti];
                String scity = s1.split("[|]")[2];
                randominti = new Random().nextInt(ss.length - 1);
                String e1 = ss[randominti];
                String ecity = e1.split("[|]")[2];
                int data = new Random().nextInt(30);
                scity = URLEncoder.encode(scity);
                ecity = URLEncoder.encode(ecity);
                println(scity + ":" + ecity + "|2015-01-" + data);
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    private InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
        }
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        else {
            if ("tongcheng_train".equals(loginname)) {
                interfaceAccount.setUsername("tongcheng_train");
                interfaceAccount.setKeystr(this.key);
            }
            else {
                interfaceAccount.setUsername(loginname);
                interfaceAccount.setKeystr("-1");
            }
            interfaceAccount.setInterfacetype(TrainInterfaceMethod.TONGCHENG);
        }
        return interfaceAccount;
    }
    
    /**
     * 判断查询日期
     * @param date
     * @return
     */
    public boolean compareTime(String date){
    	boolean flag = true;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	try {
    		Date today = new Date();
			Date searchDay = sdf.parse(date);
			if (today.compareTo(searchDay) > 0) {
				flag = false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return flag;
    }
    /**
     * 
     * @time 2015年7月26日 下午5:15:36
     * @author chendong
     */
    public static void println(Object info) {

    }
}
