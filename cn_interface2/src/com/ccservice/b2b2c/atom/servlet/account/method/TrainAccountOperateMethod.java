package com.ccservice.b2b2c.atom.servlet.account.method;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.net.URLEncoder;
import sun.misc.BASE64Decoder;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

/**
 * 12306账号及联系人处理方法
 * @author WH
 * @time 2015年12月15日 下午7:01:23
 * @version 1.0
 */

public class TrainAccountOperateMethod extends TongchengSupplyMethod {

    private static final String UTF8 = "UTF-8";

    private static final String dataTypeFlag = "110";

    private static final int retryTotal = 2;//REP重试次数

    private static final long retryWaitTime = 2000;//重试等待，单位：毫秒

    private static final String returnCodeQueryFail = "1001";//查询失败

    private static final String returnCodeAddFail = "1101";//新增失败

    private static final String returnCodeDeleteFail = "1201";//删除失败

    private static final String returnCodeModifyFail = "1102";//修改失败

    private static final String returnCodeSuccess = "231000";//请求成功

    private static final String returnCodeParamError = "231008";//入参异常

    private static final String returnCodeUnknownError = "231099";//未知异常

    private static final String userNoLoginDescription = "用户12306账号登录失败";//未知异常，统一按未登录返回

    private final String[] two_isOpenClick = { "93", "95", "97", "99" };

    private final String[] other_isOpenClick = { "93", "98", "99", "91", "95", "97" };

    private final Map<String, String> NullHeader = new HashMap<String, String>();//空请求头

    private static final int repTimeOut = Integer.parseInt(PropertyUtil.getValue("repTimeOut",
            "Train.GuestAccount.properties")) * 1000;//REP超时时间，单位：毫秒

    //途牛账号
    private static final String tuniuAccount = PropertyUtil.getValue("tuniu.account", "Train.GuestAccount.properties");
    
    //12306帐号接口  加密升级     新加参数   
    private static final String accountVersion = PropertyUtil.getValue("accountVersion", "Train.GuestAccount.properties");
    /**
     * 走手机客户端
     */
    @SuppressWarnings("rawtypes")
    private boolean GoPhone() {
        //结果
        boolean result = false;
        //读库
        try {
            //查询
            java.util.List list = Server.getInstance().getSystemService()
                    .findMapResultByProcedure("[TrainOrderPhoneMethod_Account_Select]");
            //判断
            if (list != null && list.size() > 0) {
                //MAP
                Map map = (Map) list.get(0);
                //结果
                result = map.get("IsPhone") != null && "1".equals(map.get("IsPhone").toString());
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("TrainAccountOperateMethod_Exception", e);
        }
        //返回
        return result;
    }

    /**
     * 是否DES
     * @param paramObject 请求JSON>>{"account":"tuniulvyou","sign":"","timestamp":"2016-04-10 14:02:13","data":""}
     */
    private boolean desRequestAndRespose(JSONObject paramObject) {
        return (paramObject != null && tuniuAccount != null && tuniuAccount.equals(paramObject.getString("account")))
                ||(paramObject != null && accountVersion != null && accountVersion.equals(paramObject.getString("accountversion")));
    }

    /**
     * 公共请求
     */
    private JSONObject commomRequest(String param, boolean isArray, String logName, int random) {
        JSONObject req = new JSONObject();
        //DES
        boolean desRequestAndRespose = false;
        //解析请求
        try {
            JSONObject paramObject = JSONObject.parseObject(param);
            //DATA
            String data = paramObject.getString("data");
            //DES判断
            desRequestAndRespose = desRequestAndRespose(paramObject);
            //新老兼容
            String jianRong = "";
            //进行解密
            if (desRequestAndRespose) {
                //DES解密
                jianRong = TuNiuDesUtil.decrypt(data);//加密如果上线，则返回解密数据；如果未上线，则返回原始数据
                //解密异常，数据未变化，认为未上线加密
                if (data.equals(jianRong)) {
                    desRequestAndRespose = false;//重置加密判断
                }
            }
            //密文转换
            String jiemi = desRequestAndRespose ? jianRong : new String(new BASE64Decoder().decodeBuffer(data), UTF8);
            //记录日志
            WriteLog.write(logName, random + "-->" + isArray + "-->jiemi-->" + jiemi);
            //取第一个
            req = isArray && !desRequestAndRespose ? JSONArray.parseArray(jiemi).getJSONObject(0) : JSONObject
                    .parseObject(jiemi);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
        }
        req = req == null ? new JSONObject() : req;
        //DES设置
        req.put("desRequestAndRespose", desRequestAndRespose);
        //返回结果
        return req;
    }

    /**
     * 公共输出
     * @param interfaceType 接口类型
     *              |-->1：账号验证接口
     *              |-->2：查询常用联系人接口
     *              |-->3：增加常用联系人接口
     *              |-->4：修改常用联系人接口
     *              |-->5：删除常用联系人接口
     * @param dataIsArray 返回数据是JSONArray，true时data赋值ArrayData，false时data赋值ObjectData
     */
    private JSONObject commonRespose(boolean success, String returnCode, String errorMsg, JSONObject ObjectData,
            JSONArray ArrayData, boolean dataIsArray, String realMsg, boolean desRequestAndRespose, int interfaceType,
            RepServerBean oldRep) {
        //释放REP
        RepServerUtil.freeRepServer(oldRep);
        //真实原因
        if (!ElongHotelInterfaceUtil.StringIsNull(realMsg)) {
            //直接返回给调用方 
            if (realMsg.contains("删除常用联系人失败") || realMsg.contains("不允许删除") || realMsg.contains("操作乘车人过于频繁")
                    || realMsg.contains("常用联系人数量已超过上限") || realMsg.contains("身份信息未通过核验，不能添加常用联系人")
                    || realMsg.contains("身份信息涉嫌被他人冒用") || realMsg.contains("该联系人已存在") || realMsg.contains("修改常用联系人失败")
                    || realMsg.contains("系统忙，请稍后再试") || realMsg.contains("当前访问用户过多,请稍候重试")
                    || (interfaceType == 4 && realMsg.contains("操作失败"))
                    || realMsg.contains("证件号码输入有误") || realMsg.contains("请填写正确的国家/地区") 
                    || realMsg.contains("该常用联系人基本信息不允许修改") || realMsg.contains("该邮箱不") || realMsg.contains("登录名不")|| realMsg.contains("添加失败")) {
                returnCode=returnCodeParamError;
                errorMsg = realMsg;
                //查询常用联系人接口
                if (interfaceType == 2) {
                    returnCode = returnCodeQueryFail;
                }
                //增加常用联系人接口
                else if (interfaceType == 3) {
                    returnCode = returnCodeAddFail;
                }
                //修改常用联系人接口
                else if (interfaceType == 4) {
                    returnCode = returnCodeModifyFail;
                }
                //删除常用联系人接口
                else if (interfaceType == 5) {
                    returnCode = returnCodeDeleteFail;
                }
            }
        }
        //维护时间
        if (userNoLoginDescription.equals(errorMsg) && isNight()) {
            errorMsg = "6:00-23:00之外12306不提供服务";
        }
        //返回结果
        JSONObject result = new JSONObject();
        result.put("success", success);
        result.put("errorMsg", errorMsg);
        result.put("returnCode", returnCode);
        //进行DES
        if (desRequestAndRespose) {
            //返回
            String data = "";
            //DES
            try {
                data = TuNiuDesUtil.encrypt(dataIsArray ? ArrayData.toString() : ObjectData.toString());
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("TrainAccountOperateMethod_Exception", e, "CommonRespose");
            }
            //赋值
            result.put("data", data);
        }
        else {
            result.put("data", dataIsArray ? ArrayData : ObjectData);
        }
        //返回结果
        return result;
    }

    /**
     * 公共获取REP和Cookie方法，正确返回REP地址和Cookie，错误直接返回失败，可直接返回给请求方
     * @param getCookie 是否取Cookie
     * @param getRepUrl 是否取REP地址
     * @param GoPhone 是否走手机客户端
     */
    private JSONObject commonRepAndCookie(JSONObject request, JSONObject ObjectData, JSONArray ArrayData,
            boolean dataIsArray, boolean getCookie, boolean getRepUrl, boolean desRequestAndRespose, int interfaceType,
            boolean GoPhone, RepServerBean oldRep) {
        //参数
        String pass = request.getString("pass");
        String trainAccount = request.getString("trainAccount");
        if (pass == null || ElongHotelInterfaceUtil.StringIsNull(trainAccount)) {
            return commonRespose(false, returnCodeParamError, "入参异常", ObjectData, ArrayData, dataIsArray, "",
                    desRequestAndRespose, interfaceType, oldRep);
        }
        //中文数据
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        //包含中文
        if (trainAccount.contains("?") || pattern.matcher(trainAccount).find()) {
            return commonRespose(false, returnCodeParamError, "登录名不存在", ObjectData, ArrayData, dataIsArray, "",
                    desRequestAndRespose, interfaceType, oldRep);
        }
        if (pass.contains("?") || pass.length() < 6 || pattern.matcher(pass).find()) {
            return commonRespose(false, returnCodeParamError, "密码输入错误", ObjectData, ArrayData, dataIsArray, "",
                    desRequestAndRespose, interfaceType, oldRep);
        }
        //维护时间
        if (isNight()) {
            return commonRespose(false, returnCodeSuccess, userNoLoginDescription, ObjectData, ArrayData, dataIsArray,
                    "", desRequestAndRespose, interfaceType, oldRep);
        }
        Customeruser user = new Customeruser();
        RepServerBean rep = new RepServerBean();
        //释放REP
        if (getRepUrl) {
            RepServerUtil.freeRepServer(oldRep);
        }
        //获取账号
        if (getCookie) {
            //取账号
            user = getAccount(trainAccount.trim(), pass);
            //判断账号
            if (user.isDontRetryLogin()) {
                //释放老REP
                if (!getRepUrl) {
                    RepServerUtil.freeRepServer(oldRep);
                }
                //释放新取的REP
                RepServerUtil.freeRepServerByAccount(user);
                //不登录重试，返回
                return commonRespose(false, returnCodeParamError, user.getNationality(), ObjectData, ArrayData,
                        dataIsArray, "", desRequestAndRespose, interfaceType, rep);
            }
        }
        //获取REP
        if (getRepUrl) {
            rep = getRepServer(user, GoPhone);
        }
        //结果信息
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("cookie", user);
        result.put("repurl", rep);
        //返回结果
        return result;
    }

    /**
     * 获取账号
     */
    private Customeruser getJsonUser(JSONObject repAndCookie) {
        return repAndCookie.getObject("cookie", Customeruser.class);
    }

    /**
     * 获取REP
     */
    private RepServerBean getJsonRep(JSONObject repAndCookie) {
        return repAndCookie.getObject("repurl", RepServerBean.class);
    }

    /**
     * 根据REP结果，重新取REP地址
     * @param RepResult REP结果
     * @param getCookie true：重新取账号
     */
    private boolean ReGetRep(String RepResult, RepServerBean rep, Customeruser user, boolean getCookie) {
        //用同一个REP
        if (RepServerUtil.UseSameRep(user)) {
            return getCookie;//重取账号则重取REP
        }
        else {
            return ElongHotelInterfaceUtil.StringIsNull(rep.getUrl())
                    || ElongHotelInterfaceUtil.StringIsNull(RepResult) || RepResult.contains("网络繁忙")
                    || RepResult.contains("您的操作频率过快") || RepResult.contains("网络忙，请刷新重试")
                    || RepResult.contains("系统忙，请稍后再试") || RepResult.contains("当前访问用户过多,请稍候重试");
        }
    }

    /**
     * 根据REP结果，重新登录账号取Cookie判断
     * @param RepResult REP结果
     * @param cookieIsNull true：直接获取拿Cookie
     */
    private boolean ReGetCookie(String RepResult, Customeruser user, boolean GoPhone) {
        return !GoPhone
                && (ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber()) || Account12306Util.accountNoLogin(
                        RepResult, user));
    }

    /**
     * 不再循环REP重试，跳出循环>>结果不为空、不重新拿REP、不重新拿Cookie
     */
    private boolean DontRetry(String RepResult, Customeruser user, RepServerBean rep, boolean GoPhone) {
        //REP结果为空
        if (ElongHotelInterfaceUtil.StringIsNull(RepResult)) {
            return false;
        }
        //重新拿账号
        boolean getCookie = ReGetCookie(RepResult, user, GoPhone);
        //重新拿REP
        boolean getRepUrl = ReGetRep(RepResult, rep, user, getCookie);
        //联合判断返回
        return !getCookie && !getRepUrl;
    }

    /**
     * 账号验证
     * @param reqdata 请求参数
     * @param logName 日志名称
     * @param random 随机数，用于记录日志
     */
    public JSONObject accountValidate(String reqdata, String logName, int random) {
        JSONObject data = new JSONObject();
        data.put("isPass", 1);//是否通过>>0:通过，1:不通过
        //类型
        int interfaceType = 1;
        //DES
        boolean desRequestAndRespose = false;
        //REP
        RepServerBean rep = new RepServerBean();
        //为空
        if (ElongHotelInterfaceUtil.StringIsNull(reqdata)) {
            return commonRespose(false, returnCodeParamError, "入参异常", data, new JSONArray(), false, "",
                    desRequestAndRespose, interfaceType, rep);
        }
        //判断走手机
        boolean GoPhone = GoPhone();
        //请求参数
        JSONObject request = commomRequest(reqdata, false, logName, random);
        //请求REP
        String msg = "";
        String RepResult = "";
        Customeruser user = new Customeruser();
        try {
            //REP结果
            for (int i = 0; i < retryTotal; i++) {
                //当前循环
                int current = i + 1;
                //逻辑处理
                try {
                    boolean getCookie = ReGetCookie(RepResult, user, GoPhone);
                    boolean getRepUrl = ReGetRep(RepResult, rep, user, getCookie);
                    //取REP和Cookie
                    JSONObject repAndCookie = commonRepAndCookie(request, data, new JSONArray(), false, getCookie,
                            getRepUrl, desRequestAndRespose, interfaceType, GoPhone, rep);
                    //直接返回给请求方
                    if (!repAndCookie.getBooleanValue("success")) {
                        return repAndCookie;
                    }
                    rep = getRepUrl ? getJsonRep(repAndCookie) : rep;
                    user = getCookie ? getJsonUser(repAndCookie) : user;
                    //取值
                    String repurl = rep.getUrl();
                    String cookie = user.getCardnunber();
                    //存在空的
                    if ((ElongHotelInterfaceUtil.StringIsNull(cookie) && !GoPhone)
                            || ElongHotelInterfaceUtil.StringIsNull(repurl)) {
                        continue;//中断当前循环
                    }
                    //请求参数
                    String param = "";
                    //走手机端
                    if (GoPhone) {
                        JSONObject accountPhone = new JSONObject();
                        accountPhone.put("password", request.getString("pass"));
                        accountPhone.put("startTime", System.currentTimeMillis());
                        accountPhone.put("account", request.getString("trainAccount").trim());
                        param = "datatypeflag=1012&accountPhone=" + URLEncoder.encode(accountPhone.toString(), UTF8);
                    }
                    //走PC端
                    else {
                        //请求参数
                        JSONObject callBackJson = new JSONObject();
                        callBackJson.put("newMethod", true);
                        callBackJson.put("keyWords", "快速注册");
                        param = "datatypeflag=3&cookie=" + cookie + "&callBackJson=" + callBackJson;
                    }
                    //请求日志
                    WriteLog.write(logName, random + "-->第" + current + "次-->" + repurl + "-->" + param);
                    //请求结果
                    RepResult = RequestUtil.post(repurl, param, UTF8, NullHeader, repTimeOut);
                    //结果日志
                    WriteLog.write(logName, random + "-->第" + current + "次-->" + "-->RepResult-->" + RepResult);
                    //跳出循环
                    if (DontRetry(RepResult, user, rep, GoPhone)) {
                        break;
                    }
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException(logName + "Exception", e, random + "[第" + current + "次]");
                }
            }
            //解析结果
            if (!ElongHotelInterfaceUtil.StringIsNull(RepResult)) {
                msg = JSONObject.parseObject(RepResult).getString("msg");
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
        }
        //走手机端
        if (GoPhone && !ElongHotelInterfaceUtil.StringIsNull(msg)) {
            if ("登录成功".equals(msg)) {
                data.put("isPass", 0);
                data = checkLoginMsg(RepResult, logName);
            }
            return commonRespose("登录成功".equals(msg), returnCodeSuccess, "登录成功".equals(msg) ? "已通过" : msg, data,
                    new JSONArray(), false,msg , desRequestAndRespose, interfaceType, rep);
        }
        //正常返回
        else if ("已通过".equals(msg) || "待核验".equals(msg) || "手机待核验".equals(msg)) {
            if ("已通过".equals(msg)) {
                data.put("isPass", 0);
            }
            return commonRespose("已通过".equals(msg), returnCodeSuccess, msg, data, new JSONArray(), false, msg,
                    desRequestAndRespose, interfaceType, rep);
        }
        //REP交互失败、网络繁忙、用户未登录等
        else {
            return commonRespose(false, returnCodeUnknownError, userNoLoginDescription, data, new JSONArray(), false,
                    msg, desRequestAndRespose, interfaceType, rep);
        }
    }

    private JSONObject checkLoginMsg(String RepResult, String logName) {
        WriteLog.write(logName, "RepResult====>:" + RepResult);
        JSONObject data = new JSONObject();
        JSONObject json = JSONObject.parseObject(RepResult).getJSONObject("accountPhone");
        JSONArray jsonArray = json.getJSONArray("passengerResult");
        WriteLog.write("rep帐号校验已通过与待核验", "jsonArray.size()====>:" + jsonArray.size());
        if (jsonArray.size() == 1) {
            for (int i = 0; jsonArray != null && i < jsonArray.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                String total_times = jsonObject.getString("total_times") == null ? null : jsonObject
                        .getString("total_times");
                if ("1".equals(jsonObject.getString("id_type"))) {
                    if ("93".equals(total_times) || "95".equals(total_times) || "97".equals(total_times)
                            || "99".equals(total_times)) {
                        data.put("isPass", 0);
                    }
                    else if ("92".equals(total_times) || "98".equals(total_times)) {//待核验
                        data.put("isPass", 1);
                    }
                    else {
                        data.put("isPass", 1);
                    }
                }
                else {
                    if ("93".equals(total_times) || "98".equals(total_times) || "99".equals(total_times)
                            || "91".equals(total_times) || "95".equals(total_times) || "97".equals(total_times)) {
                        data.put("isPass", 0);
                    }
                    else {
                        data.put("isPass", 1);
                    }

                }
            }
        }
        else {
            data.put("isPass", 0);
        }
        WriteLog.write(logName, "data====>:" + data.toJSONString());
        return data;
    }

    /**
     * 查询联系人
     */
    public JSONObject contactQuery(String reqdata, String logName, int random) {
        JSONArray data = new JSONArray();
        //类型
        int interfaceType = 2;
        //DES
        boolean desRequestAndRespose = false;
        //REP
        RepServerBean rep = new RepServerBean();
        //为空
        if (ElongHotelInterfaceUtil.StringIsNull(reqdata)) {
            return commonRespose(false, returnCodeParamError, "入参异常", new JSONObject(), data, true, "",
                    desRequestAndRespose, interfaceType, rep);
        }
        //判断走手机
        boolean GoPhone = false;
        //请求参数
        JSONObject request = commomRequest(reqdata, true, logName, random);
        //重置
        desRequestAndRespose = request.getBooleanValue("desRequestAndRespose");
        //请求REP
        String RepResult = "";
        Customeruser user = new Customeruser();
        for (int i = 0; i < retryTotal; i++) {
            int current = i + 1;
            try {
                boolean getCookie = ReGetCookie(RepResult, user, GoPhone);
                boolean getRepUrl = ReGetRep(RepResult, rep, user, getCookie);
                //取REP和Cookie
                JSONObject repAndCookie = commonRepAndCookie(request, new JSONObject(), data, true, getCookie,
                        getRepUrl, desRequestAndRespose, interfaceType, GoPhone, rep);
                //直接返回给请求方
                if (!repAndCookie.getBooleanValue("success")) {
                    return repAndCookie;
                }
                rep = getRepUrl ? getJsonRep(repAndCookie) : rep;
                user = getCookie ? getJsonUser(repAndCookie) : user;
                //取值
                String repurl = rep.getUrl();
                String cookie = user.getCardnunber();
                //存在空的
                if (ElongHotelInterfaceUtil.StringIsNull(cookie) || ElongHotelInterfaceUtil.StringIsNull(repurl)) {
                    continue;//中断当前循环
                }
                JSONObject deleteObj = new JSONObject();
                deleteObj.put("busType", "4");
                deleteObj.put("cookie", cookie);
                //请求参数URLEncoder
                String jsonStr = URLEncoder.encode(deleteObj.toString(), UTF8);
                //请求参数
                String param = "datatypeflag=" + dataTypeFlag + "&jsonStr=" + jsonStr;
                //请求日志
                WriteLog.write(logName, random + "-->第" + current + "次-->" + repurl + "-->" + param);
                //REP结果
                RepResult = RequestUtil.post(repurl, param, UTF8, NullHeader, repTimeOut);
                //结果日志
                WriteLog.write(logName, random + "-->第" + current + "次-->" + "-->RepResult-->" + RepResult);
                //跳出循环
                if (DontRetry(RepResult, user, rep, GoPhone)) {
                    break;
                }
                //停1秒
                if (i < retryTotal - 1) {
                    Thread.sleep(retryWaitTime);
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
            }
        }
        JSONObject RepJson = new JSONObject();
        //非空
        if (!ElongHotelInterfaceUtil.StringIsNull(RepResult)) {
            try {
                RepJson = JSONObject.parseObject(RepResult);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
            }
            RepJson = RepJson == null ? new JSONObject() : RepJson;
        }
        //操作成功
        if (RepJson.getBooleanValue("success")) {
            /**
             {
                "code": "6",
                "passenger_name": "曹多意",
                "sex_code": "F",
                "sex_name": "女",
                "born_date": "2015-06-09 00:00:00",
                "country_code": "CN",
                "passenger_id_type_code": "1",
                "passenger_id_type_name": "二代身份证",
                "passenger_id_no": "420115198903271624",
                "passenger_type": "1",
                "passenger_flag": "0",
                "passenger_type_name": "成人",
                "mobile_no": "",
                "phone_no": "",
                "email": "",
                "address": "",
                "postalcode": "",
                "first_letter": "CDY",
                "recordCount": "13",
                "isUserSelf": "N",
                "total_times": "99"
            }
             */
            JSONArray passengers = RepJson.getJSONArray("passengers");
            //循环乘客
            for (int i = 0; i < passengers.size(); i++) {
                JSONObject passenger = passengers.getJSONObject(i);
                //解析数据
                String sex_code = passenger.getString("sex_code");
                String name = passenger.getString("passenger_name");
                int isUserSelf = "Y".equals(passenger.getString("isUserSelf")) ? 0 : 1;
                String country = passenger.getString("country_code");
                String identyType = passenger.getString("passenger_id_type_code");
                String identy = passenger.getString("passenger_id_no");
                int personType = passenger.getIntValue("passenger_type") - 1;
                String phone = passenger.getString("mobile_no");
                String tel = passenger.getString("phone_no");
                String email = passenger.getString("email");
                String address = passenger.getString("address");
                String birthday = getBirthday(identyType, identy);
                int checkStatus = passengerCheck(identyType, passenger.getString("total_times"));
                int id = getPassengerId(passenger.getString("isUserSelf"), name, identy, identyType);
                //请选择
                int sex = -1;
                //女
                if ("F".equals(sex_code)) {
                    sex = 1;
                }
                //男
                else if ("M".equals(sex_code)) {
                    sex = 0;
                }
                //设置数据
                JSONObject temp = new JSONObject();
                temp.put("id", id);
                temp.put("name", name);
                temp.put("isUserSelf", isUserSelf);
                temp.put("sex", sex);
                temp.put("birthday", birthday);
                temp.put("country", country);
                temp.put("identyType", identyType);
                temp.put("identy", identy);
                temp.put("personType", personType);
                temp.put("phone", phone);
                temp.put("tel", tel);
                temp.put("email", email);
                temp.put("address", address);
                temp.put("checkStatus", checkStatus);
                //添加数据
                data.add(temp);
            }
            //返回结果
            return commonRespose(true, returnCodeSuccess, "查询成功", new JSONObject(), data, true, "",
                    desRequestAndRespose, interfaceType, rep);
        }
        else {
            return commonRespose(false, returnCodeUnknownError, userNoLoginDescription, new JSONObject(), data, true,
                    RepJson.getString("msg"), desRequestAndRespose, interfaceType, rep);
        }
    }

    /**
     * 增加或修改联系人
     */
    public JSONObject contactSaveOrUpdate(String reqdata, String logName, int random) {
        JSONObject data = new JSONObject();
        //DES
        boolean desRequestAndRespose = false;
        //REP
        RepServerBean rep = new RepServerBean();
        //为空
        if (ElongHotelInterfaceUtil.StringIsNull(reqdata)) {
            return commonRespose(false, returnCodeParamError, "入参异常", data, new JSONArray(), false, "",
                    desRequestAndRespose, 3, rep);
        }
        //判断走手机
        boolean GoPhone = false;
        //请求参数
        JSONObject request = commomRequest(reqdata, false, logName, random);
        //重置
        desRequestAndRespose = request.getBooleanValue("desRequestAndRespose");
        //请求参数
        JSONArray contactArray = new JSONArray();
        //异常捕捉
        try {
            if (desRequestAndRespose) {
                contactArray = request.getJSONArray("contacts");
            }
            else {
                JSONObject contacts = request.getJSONObject("contacts");
                contactArray.add(contacts == null ? new JSONObject() : contacts);
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("TrainAccountOperateMethod_Exception", e, "ContactSaveOrUpdate");
        }
        //判断请求
        if (contactArray == null || contactArray.size() <= 0) {
            return commonRespose(false, returnCodeParamError, "入参异常", data, new JSONArray(), false, "",
                    desRequestAndRespose, 3, rep);
        }
        //请求REP
        Customeruser user = new Customeruser();
        //成功个数
        int successCount = 0;
        //最后一个
        String lastPersonMsg = "";
        JSONObject lastRepResult = new JSONObject();
        //业务集合
        String busTypeTotal = "";
        Map<String, String> busTypeMap = new HashMap<String, String>();
        //循环联系人
        for (int i = 0; i < contactArray.size(); i++) {
            //REP结果
            JSONObject RepJson = new JSONObject();
            //循环处理
            try {
                //当前请求
                JSONObject contacts = contactArray.getJSONObject(i);
                //请求参数
                int id = contacts.getIntValue("id");//必须
                String busType = id == 0 ? "1" : "2";//业务类型
                String name = contacts.getString("name");//必须
                String identy = contacts.getString("identy");//必须
                String identyType = contacts.getString("identyType");//必须
                int personType = contacts.getIntValue("personType") + 1;//必须
                //数据校验
                if (!contacts.containsKey("id") || id < 0 || ElongHotelInterfaceUtil.StringIsNull(name)
                        || personType < 1 || ElongHotelInterfaceUtil.StringIsNull(identyType)
                        || ElongHotelInterfaceUtil.StringIsNull(identy)) {
                    return commonRespose(false, returnCodeParamError, "入参异常", data, new JSONArray(), false, "",
                            desRequestAndRespose, id == 0 ? 3 : 4, rep);
                }
                //数据校验
                String dataCheck = dataCheck(request, contacts, identyType);
                //数据错误
                if (!ElongHotelInterfaceUtil.StringIsNull(dataCheck)) {
                    return commonRespose(false, returnCodeParamError, dataCheck, data, new JSONArray(), false, "",
                            desRequestAndRespose, id == 0 ? 3 : 4, rep);
                }
                //业务类型
                busTypeTotal = busType;
                busTypeMap.put(busType, busType);
                lastPersonMsg = "第" + (i + 1) + "个联系人[" + name + "][" + identy + "]";
                //请求参数
                contacts.put("busType", busType);
                contacts.put("personType", personType);
                contacts.put("identyType", identyType);
                contacts.put("sex", getSex(contacts.getIntValue("sex"), identyType, identy));
                //ERP结果
                String RepResult = "";
                //多次尝试
                for (int j = 0; j < retryTotal; j++) {
                    //当前循环
                    int current = j + 1;
                    //逻辑处理
                    try {
                        boolean getCookie = ReGetCookie(RepResult, user, GoPhone);
                        boolean getRepUrl = ReGetRep(RepResult, rep, user, getCookie);
                        //取REP和Cookie
                        JSONObject repAndCookie = commonRepAndCookie(request, data, new JSONArray(), false, getCookie,
                                getRepUrl, desRequestAndRespose, id == 0 ? 3 : 4, GoPhone, rep);
                        //直接返回给请求方
                        if (!repAndCookie.getBooleanValue("success")) {
                            return repAndCookie;
                        }
                        rep = getRepUrl ? getJsonRep(repAndCookie) : rep;
                        user = getCookie ? getJsonUser(repAndCookie) : user;
                        //取值
                        String repurl = rep.getUrl();
                        String cookie = user.getCardnunber();
                        //存在空的
                        if (ElongHotelInterfaceUtil.StringIsNull(cookie)
                                || ElongHotelInterfaceUtil.StringIsNull(repurl)) {
                            continue;//中断当前循环
                        }
                        //请求REP参数
                        contacts.put("cookie", cookie);
                        //请求参数URLEncoder
                        String jsonStr = URLEncoder.encode(contacts.toString(), UTF8);
                        //请求参数
                        String param = "datatypeflag=" + dataTypeFlag + "&jsonStr=" + jsonStr;
                        //请求日志
                        WriteLog.write(logName, random + "-->第" + current + "次-->" + repurl + "-->" + param);
                        //REP结果
                        RepResult = RequestUtil.post(repurl, param, UTF8, NullHeader, repTimeOut);
                        //结果日志
                        WriteLog.write(logName, random + "-->第" + current + "次-->" + "-->RepResult-->" + RepResult);
                        //跳出循环
                        if (DontRetry(RepResult, user, rep, GoPhone)) {
                            break;
                        }
                        //不等于重试次数停1.5秒
                        if (j < retryTotal - 1) {
                            Thread.sleep(retryWaitTime);
                        }
                    }
                    catch (Exception e) {
                        ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
                    }
                }
                //解析结果
                if (!ElongHotelInterfaceUtil.StringIsNull(RepResult)) {
                    RepJson = JSONObject.parseObject(RepResult);
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
            }
            //为空
            RepJson = RepJson == null ? new JSONObject() : RepJson;
            //当前
            lastRepResult = RepJson;
            //成功
            if (RepJson.getBooleanValue("success")) {
                successCount++;
            }
            else {
                break;//失败>>直接中断，后续乘客不添加或修改
            }
        }
        //操作成功
        if (successCount == contactArray.size()) {
            String msg = "";
            //不唯一
            if (busTypeMap.size() != 1) {
                msg = "操作成功";
            }
            else {
                msg = "1".equals(busTypeTotal) ? "新增成功" : "修改成功";
            }
            //返回结果
            return commonRespose(true, returnCodeSuccess, msg, data, new JSONArray(), false, "", desRequestAndRespose,
                    "1".equals(busTypeTotal) ? 3 : 4, rep);
        }
        else {
            return commonRespose(false, returnCodeUnknownError, userNoLoginDescription, data, new JSONArray(), false,
                    lastPersonMsg + "_" + lastRepResult.getString("msg"), desRequestAndRespose,
                    "1".equals(busTypeTotal) ? 3 : 4, rep);
        }
    }

    /**
     * 删除联系人
     */
    public JSONObject contactDelete(String reqdata, String logName, int random) {
        JSONObject data = new JSONObject();
        //类型
        int interfaceType = 5;
        //DES
        boolean desRequestAndRespose = false;
        //REP
        RepServerBean rep = new RepServerBean();
        //为空
        if (ElongHotelInterfaceUtil.StringIsNull(reqdata)) {
            return commonRespose(false, returnCodeParamError, "入参异常", data, new JSONArray(), false, "",
                    desRequestAndRespose, interfaceType, rep);
        }
        //判断走手机
        boolean GoPhone = false;
        //请求参数
        JSONObject request = commomRequest(reqdata, false, logName, random);
        //重置
        desRequestAndRespose = request.getBooleanValue("desRequestAndRespose");
        //证件号码
        String identy = request.getString("identy");
        //证件类型
        String identyType = request.getString("identyType");
        //ID
        int id = desRequestAndRespose ? request.getIntValue("id") : request.getIntValue("ids");
        //判断请求
        if (id <= 0 && ElongHotelInterfaceUtil.StringIsNull(identy) && ElongHotelInterfaceUtil.StringIsNull(identyType)) {
            return commonRespose(false, returnCodeParamError, "入参异常", data, new JSONArray(), false, "",
                    desRequestAndRespose, interfaceType, rep);
        }
        //请求REP
        String RepResult = "";
        JSONObject RepJson = new JSONObject();
        Customeruser user = new Customeruser();
        try {
            for (int i = 0; i < retryTotal; i++) {
                //当前循环
                int current = i + 1;
                try {
                    boolean getCookie = ReGetCookie(RepResult, user, GoPhone);
                    boolean getRepUrl = ReGetRep(RepResult, rep, user, getCookie);
                    //取REP和Cookie
                    JSONObject repAndCookie = commonRepAndCookie(request, data, new JSONArray(), false, getCookie,
                            getRepUrl, desRequestAndRespose, interfaceType, GoPhone, rep);
                    //直接返回给请求方
                    if (!repAndCookie.getBooleanValue("success")) {
                        return repAndCookie;
                    }
                    rep = getRepUrl ? getJsonRep(repAndCookie) : rep;
                    user = getCookie ? getJsonUser(repAndCookie) : user;
                    //取值
                    String repurl = rep.getUrl();
                    String cookie = user.getCardnunber();
                    //存在空的
                    if (ElongHotelInterfaceUtil.StringIsNull(cookie) || ElongHotelInterfaceUtil.StringIsNull(repurl)) {
                        continue;//中断当前循环
                    }
                    //请求REP
                    JSONObject deleteObj = new JSONObject();
                    deleteObj.put("id", id);
                    deleteObj.put("busType", "3");
                    deleteObj.put("cookie", cookie);
                    deleteObj.put("identy", ElongHotelInterfaceUtil.StringIsNull(identy) ? "" : identy);
                    deleteObj.put("identyType", ElongHotelInterfaceUtil.StringIsNull(identyType) ? "" : identyType);
                    //请求参数URLEncoder
                    String jsonStr = URLEncoder.encode(deleteObj.toString(), UTF8);
                    //请求参数
                    String param = "datatypeflag=" + dataTypeFlag + "&jsonStr=" + jsonStr;
                    //请求日志
                    WriteLog.write(logName, random + "-->第" + current + "次-->" + repurl + "-->" + param);
                    //REP结果
                    RepResult = RequestUtil.post(repurl, param, UTF8, NullHeader, repTimeOut);
                    //结果日志
                    WriteLog.write(logName, random + "-->第" + current + "次-->" + "-->RepResult-->" + RepResult);
                    //跳出循环
                    if (DontRetry(RepResult, user, rep, GoPhone)) {
                        break;
                    }
                    //不等于重试次数停1.5秒
                    if (i < retryTotal - 1) {
                        Thread.sleep(retryWaitTime);
                    }
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
                }
            }
            //解析结果
            if (!ElongHotelInterfaceUtil.StringIsNull(RepResult)) {
                RepJson = JSONObject.parseObject(RepResult);
                RepJson = RepJson == null ? new JSONObject() : RepJson;
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
        }
        //操作成功
        if (RepJson.getBooleanValue("success")) {
            return commonRespose(true, returnCodeSuccess, "删除成功", data, new JSONArray(), false, "",
                    desRequestAndRespose, interfaceType, rep);
        }
        else {
            return commonRespose(false, returnCodeUnknownError, userNoLoginDescription, data, new JSONArray(), false,
                    RepJson.getString("msg"), desRequestAndRespose, interfaceType, rep);
        }

    }

    /**
     * 取REP地址
     * @param GoPhone 是否走手机客户端
     */
    private RepServerBean getRepServer(Customeruser user, boolean GoPhone) {
        //尝试3次
        for (int i = 0; i < 3; i++) {
            try {
                RepServerBean rep = new RepServerBean();
                //手机端
                if (GoPhone) {
                    rep = RepServerUtil.dontNeedLoginRepServer();
                }
                //判断账号
                else {
                    rep = RepServerUtil.getRepServer(user, false);
                }
                //获取到URL
                if (rep != null && !ElongHotelInterfaceUtil.StringIsNull(rep.getUrl())) {
                    return rep;
                }
            }
            catch (Exception e) {

            }
        }
        return new RepServerBean();
    }

    /**
     * 取账号Cookie
     */
    private Customeruser getAccount(String trainAccount, String pass) {
        Map<String, String> backup = new HashMap<String, String>();
        backup.put("password", pass);
        backup.put("checkAccountInfo", "true");
        Customeruser user = Account12306Util.get12306Account(4, trainAccount, true, backup);
        user.setCustomerAccount(true);
        return user;
    }

    /**
     * 取证件生日
     */
    private String getBirthday(String identyType, String identy) {
        String birthday = "";
        try {
            //二代身份证>>420154 1999 08 15 7841
            if ("1".equals(identyType.trim()) && identy.trim().length() == 18) {
                String year = identy.substring(6, 10);
                String month = identy.substring(10, 12);
                String day = identy.substring(12, 14);
                birthday = year + "-" + month + "-" + day;
            }
        }
        catch (Exception e) {

        }
        return birthday;
    }

    /**
     * 取证件性别>>0-男，1-女
     */
    private int getSex(int sex, String identyType, String identy) {
        try {
            //二代身份证
            if ("1".equals(identyType.trim()) && identy.trim().length() == 18) {
                sex = Integer.parseInt(identy.substring(16, 17)) % 2 == 0 ? 1 : 0;
            }
        }
        catch (Exception e) {

        }
        //返回身份性别
        return sex;
    }

    /**
     * 乘客核验判断
     */
    private int passengerCheck(String id_type, String total_times) {
        //二代身份证
        if ("1".equals(id_type)) {
            for (int i = 0; i < two_isOpenClick.length; i++) {
                if (two_isOpenClick[i].equals(total_times)) {
                    return 0;//通过
                }
            }
        }
        else {
            for (int i = 0; i < other_isOpenClick.length; i++) {
                if (other_isOpenClick[i].equals(total_times)) {
                    return 0;//通过
                }
            }
        }
        return 1;//不通过
    }

    /**
     * 查询乘客序列号
     */
    private int getPassengerId(String isUserSelf, String passenger_name, String passenger_id_no,
            String passenger_id_type_code) {
        //标识乘客
        String keyflag = isUserSelf + "_" + passenger_name + "_" + passenger_id_no + "_" + passenger_id_type_code;
        //取Hash
        int hashCode = keyflag.hashCode();
        //统一取正数
        return hashCode < 0 ? -hashCode : hashCode;
    }

    /**
     * 请求数据校验
     */
    private String dataCheck(JSONObject request, JSONObject contacts, String identyType) {
        if (!idTypeRight(identyType)) {
            return "证件类型暂不支持";
        }
        //中文数据
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        //包含中文
        String phone = contacts.getString("phone");
        if (!ElongHotelInterfaceUtil.StringIsNull(phone)
                && (phone.trim().length() != 11 || pattern.matcher(phone).find())) {
            return "手机号码错误";
        }
        String email = contacts.getString("email");
        if (!ElongHotelInterfaceUtil.StringIsNull(email) && email.indexOf("@") <= 0) {
            return "电子邮箱错误";
        }
        return "";
    }

    /**
     * 证件类型校验
     */
    private boolean idTypeRight(String identyType) {
        return "1".equals(identyType) || "B".equals(identyType) || "C".equals(identyType) || "G".equals(identyType);
    }

    /**
     * 判断当前时间
     */
    private boolean isNight() {
        boolean isNight = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            //当前
            Date current = sdf.parse(sdf.format(new Date()));
            //判断
            isNight = current.before(sdf.parse("06:00"));
        }
        catch (Exception e) {

        }
        return isNight;
    }
}