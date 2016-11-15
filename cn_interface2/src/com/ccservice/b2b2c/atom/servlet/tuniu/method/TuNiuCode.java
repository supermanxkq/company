package com.ccservice.b2b2c.atom.servlet.tuniu.method;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;

public class TuNiuCode {

    private Integer type;//1 占座     2 取消占座     3   确认出票      4 退票

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public static boolean tf(String parentid){
        boolean result=false;
        if(parentid!=null&&!"".equals(parentid)){
            if(parentid.contains("tuniu")){
                result=true;
            }
        }
        return result;
    }
    /**
     * 公共输出
     * @param type 1 占座     2 取消占座     3   确认出票      4 退票    5 public
     * @param js 返回参数
     */
    public static JSONObject getcodeandmsg(JSONObject js, Integer type) {
        String msg = getmsg(js);
        String code = getcode(js);
        if (type == 1) {
            js = getcode1(js, msg, code);
        }
        else if (type == 2) {
            js = getcode2(js, msg, code);
        }
        else if (type == 3) {
            js = getcode3(js, msg, code);
        }
        else if (type == 4) {
            js = getcode4(js, msg, code);
        }
        else if (type == 5) {
            js = getcode5(js, msg, code);
        }
        return js;
    }
    /**
     * 占座 
     * @param js
     * @param msg
     * @param code
     * @return
     */
    public static JSONObject getcode1(JSONObject js, String msg, String code) {
        if (msg.contains("创建订单成功")) {
            js.put("code", "110016");
            js.put("msg", "订单正在预定，请勿重复提交");
        }
        else if (msg.contains("乘客身份信息未通过验证")) {
            js.put("code", "110003");
            js.put("msg", msg);
        }
        else if (msg.contains("车次未到起售时间")) {
            js.put("code", "110017");
            js.put("msg", "该车次未到起售时间，请稍后再试");
        }
        else if (msg.contains("车站对应三字码有误")) {
            js.put("code", "110018");
            js.put("msg", msg);//"车站三字码有误"
        }
        else if (msg.contains("不允许发售该票种的席位")) {
            js.put("code", "110019");
            js.put("msg", "暂不支持学生票的车票类型");
        }
        else if (msg.contains("账户余额不足以支付此订单")) {
            js.put("code", "110020");
            js.put("msg", "途牛账户余额不足");
        }
        else if (msg.contains("车票信息已过期，请重新查询最新车票信息")||msg.contains("车票预订查询结果")) {
            js.put("code", "110021");
            js.put("msg", "12306暂未查询到该车次信息或车次已经停运");
        }
        else if (msg.contains("密码输入错误")||msg.contains("用户名不存在")||msg.contains("邮箱不存在")) {
            js.put("code", "110022");
            js.put("msg", msg);
        }
        else if (msg.contains("您的手机号码尚未进行核验")) {
            js.put("code", "110006");
            js.put("msg", msg);//12306帐号已被锁定，请稍后再试
        }
        else if (msg.contains("密码输入错误次数过多")) {
            js.put("code", "110023");
            js.put("msg", msg);//12306帐号已被锁定，请稍后再试
        }
        else if ("506".equals(code)) {
            js.put("code", "110024");
            js.put("msg", "系统异常，操作失败");
        }
        return js;
    }
    /**
     * 取消订单
     * @param js
     * @param msg
     * @param code
     * @return
     */
    public static JSONObject getcode2(JSONObject js, String msg, String code) {
        if (msg.contains("取消订单成功")) {
            js.put("code", "130003");
            js.put("msg", "订单已经取消成功，请勿重复提交");
        }
        else if (msg.contains("取消请求已接收,占座中")) {
            js.put("code", "130004");
            js.put("msg", "订单正在取消，请勿重复提交");
        }
        else if (msg.contains("该订单状态下,不能取消")) {
            js.put("code", "130005");
            js.put("msg", "当前的订单状态不能取消订单");
        }
        else if (msg.contains("12306已经自动取消")) {
            js.put("code", "130006");
            js.put("msg", "支付超时，12306已经自动取消");
        }
        else if (msg.contains("您的手机号码尚未进行核验")) {
            js.put("code", "130007");
            js.put("msg", msg);//乘客身份信息（姓名）（证件号）未通过验证
        }
        else if (msg.contains("密码输入错误")||msg.contains("用户名不存在")||msg.contains("邮箱不存在")) {
            js.put("code", "13008");
            js.put("msg", msg);
        }
        else if (msg.contains("密码输入错误次数过多")) {
            js.put("code", "130009");
            js.put("msg", msg);//12306帐号已被锁定，请稍后再试
        }
        else if (msg.contains("取消订单失败>>>执行错误")) {
            js.put("code", "130010");
            js.put("msg", "系统异常，操作失败");
        }
        return js;
    }
    /**
     * 出票
     * @param js
     * @param msg
     * @param code
     * @return
     */
    public static JSONObject getcode3(JSONObject js, String msg, String code) {
        if (msg.contains("确认出票的请求时间已超过规定的时间")) {
            js.put("code", "120002");
            js.put("msg", "支付超时，12306已经自动取消");
        }
        else if (msg.contains("账户余额不足以支付此订单")) {
            js.put("code", "120003");
            js.put("msg", "途牛账户余额不足");
        }
        else if (msg.contains("您的手机号码尚未进行核验")) {//您的手机号码尚未进行核验，目前暂无法用于登录，请您先使用用户名或邮箱登录，然后选择手机核验，核验通过后即可使用手机号码登录功能，谢谢。
            js.put("code", "120004");
            js.put("msg", msg);//乘客身份信息（姓名）（证件号）未通过验证
        }
        else if (msg.contains("该订单状态下，不能确认出票")) {
            js.put("code", "120005");
            js.put("msg", "您已在12306取消订单，出票失败");
        }
        else if (msg.contains("密码输入错误")||msg.contains("用户名不存在")||msg.contains("邮箱不存在")) {
            js.put("code", "120006");
            js.put("msg", msg);
        }
        else if (msg.contains("密码输入错误次数过多")) {
            js.put("code", "120007");
            js.put("msg", msg);//12306帐号已被锁定，请稍后再试
        }
        else if ("403".equals(code)) {
            js.put("code", "120008");
            js.put("msg", "系统异常，操作失败");
        }
        return js;
    }
    /**
     * 
     * @author RRRRRR
     * @time 2016年11月10日 上午10:03:48
     * @Description 确认出票异步code转换
     * @param js
     * @param msg
     * @param code
     * @return
     */
    public static JSONObject getcode3V2( String msg) {
        try {
            msg=URLDecoder.decode(msg, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject json=new JSONObject();
        if (msg.contains("确认出票的请求时间已超过规定的时间")) {
            json.put("code", "120002");
            json.put("msg", "支付超时，12306已经自动取消");
        }
        else if (msg.contains("账户余额不足以支付此订单")) {
            json.put("code", "120003");
            json.put("msg", "途牛账户余额不足");
        }
        else if (msg.contains("您的手机号码尚未进行核验")) {//您的手机号码尚未进行核验，目前暂无法用于登录，请您先使用用户名或邮箱登录，然后选择手机核验，核验通过后即可使用手机号码登录功能，谢谢。
            json.put("code", "120004");
            json.put("msg", msg);//乘客身份信息（姓名）（证件号）未通过验证
        }
        else if (msg.contains("该订单状态下，不能确认出票")) {
            json.put("code", "120005");
            json.put("msg", "您已在12306取消订单，出票失败");
        }
        else if (msg.contains("密码输入错误")||msg.contains("用户名不存在")||msg.contains("邮箱不存在")) {
            json.put("code", "120006");
            json.put("msg", msg);
        }
        else if (msg.contains("密码输入错误次数过多")) {
            json.put("code", "120007");
            json.put("msg", msg);//12306帐号已被锁定，请稍后再试
        }
        else {
            json.put("code", "120008");
            json.put("msg", "系统异常，操作失败");
        }
        return json;
    }
    
    public static JSONObject getcode4(JSONObject js, String msg, String code) {
        if (msg.contains("退票请求已接收")) {
            js.put("code", "140004");
            js.put("msg", "退票请求已接收，请勿重复提交");//订单已经退票成功
        }
        else if (msg.contains("退票请求已接收，正在处理")) {
            js.put("code", "140005");
            js.put("msg", "订单正在退票，请勿重复提交");
        }
        else if (msg.contains("该订单状态下，不能退票")) {
            js.put("code", "140006");
            js.put("msg", "该订单状态下，不能退票");//当前时间不支持退票业务，请稍后再试
        }
        else if (msg.contains("请持乘车人身份证件原件就近车站办理，待办时还需持代办人的身份证原件")) {
            js.put("code", "140007");
            js.put("msg", "尊敬的旅客，为防止网上囤票倒票，给广大旅客创造一个公平的购票环境，凡通过互联网或手机购买的本次列车车票，如需办理退票，改签和变更到站等变更业务，请持乘车人身份证件原件就近车站办理，待办时还需持代办人的身份证原件");
        }
        else if (msg.contains("您的手机号码尚未进行核验")) {
            js.put("code", "140008");
            js.put("msg", "您的手机号码尚未进行核验，目前暂无法用于登录，请您先使用用户名或邮箱登录，然后选择手机核验，核验通过后即可使用手机号码登录功能，谢谢。");//乘客身份信息（姓名）（证件号）未通过验证
        }
        else if (msg.contains("密码输入错误")||msg.contains("用户名不存在")||msg.contains("邮箱不存在")) {
            js.put("code", "140009");
            js.put("msg", msg);//12306帐号密码错误
        }
        else if (msg.contains("密码输入错误次数过多")) {
            js.put("code", "140010");
            js.put("msg", msg);//12306帐号已被锁定，请稍后再试
        }
        else if (msg.contains("退票请求失败")) {
            js.put("code", "140011");
            js.put("msg", "系统异常，操作失败,退票请求失败");
        }
        return js;
    }
    
    public static JSONObject getcode5(JSONObject js, String msg, String code) {
        if (msg.contains("当前时间不提供服务")) {
            js.put("code", "110024");
            js.put("msg", "系统异常，操作失败");
        }
        return js;
    }

    public static String getmsg(JSONObject js) {
        String msg="";
        if(js.containsKey("msg")){
            msg = js.getString("msg");
            try {
                msg=URLDecoder.decode(msg, "utf-8");
            }
            catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return msg;
    }

    public static String getcode(JSONObject js) {
        String code = "";
        if(js.containsKey("code")){
            code = js.getString("code");
        }
        return code;
    }
    
    public static void main(String[] args) {
        String str="tuniu_test";
        if(str.contains("tuniu")){
            System.out.println("1111111111111111");
        }
    }
}
