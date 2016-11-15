package com.ccservice.b2b2c.atom.servlet.tuniu;

import com.alibaba.fastjson.JSONObject;

/**
 * 途牛改签修饰错误码
 * @time 2016年9月10日 下午1:23:05
 * @author fiend
 */
public class TuNiuChangeCodeMethod {
    /**
     * 把同程错误码修饰成途牛的
     * 
     * @param reqjson
     * @return
     * @time 2016年9月10日 下午1:23:17
     * @author fiend
     */
    public static JSONObject changeCallBackCode(JSONObject reqjson, String partnerid) {
        if (partnerid.contains("tuniu")) {
            return changeCallBackCode(reqjson);
        }
        else {
            return reqjson;
        }
    }

    /**
     * 把同程错误码修饰成途牛的----确认改签
     * 
     * @param reqjson
     * @param partnerid
     * @return
     * @time 2016年9月10日 下午1:23:17
     * @author fiend
     */
    public static JSONObject changeConfirmCode(JSONObject reqjson, String partnerid) {
        int code = reqjson.getIntValue("code");
        boolean success = reqjson.getBooleanValue("success");
        if (partnerid.contains("tuniu")) {
            if (success) {
                code = 231000;
            }
            else if (code == 113) {
                code = 1600201;
            }
            else if (code == 107) {
                code = 1600201;
            }
            else if (code == 108) {
                code = 1600201;
            }
            else if (code == 402) {
                code = 1600203;
            }
            else if (code == 112) {
                code = 1600201;
            }
            else if (code == 999) {
                code = 1600201;
            }
            else if (code == 1003) {
                code = 1600202;
            }
            reqjson.put("code", code);
            return reqjson;
        }
        else {
            return reqjson;
        }
    }

    /**
     * 把同程错误码修饰成途牛的----确认改签
     * 
     * @param code 同城错误码
     * @param success 成功与否
     * @return  code 途牛错误码
     * @time 2016年9月10日 下午1:23:17
     * @author zlx
     */
    public static int changeConfirmCodeV1(int code, boolean success) {
        if (success) {
            code = 231000;
        }
        else if (code == 113) {
            code = 1600201;
        }
        else if (code == 107) {
            code = 1600201;
        }
        else if (code == 108) {
            code = 1600201;
        }
        else if (code == 402) {
            code = 1600203;
        }
        else if (code == 112) {
            code = 1600201;
        }
        else if (code == 999) {
            code = 1600201;
        }
        else if (code == 1003) {
            code = 1600202;
        }
        return code;
    }

    /**
     * 把同程错误码修饰成途牛的----取消改签
     * 
     * @param reqjson
     * @param partnerid
     * @return
     * @time 2016年9月10日 下午1:23:17
     * @author fiend
     */
    public static JSONObject changeCancelCode(JSONObject reqjson, String partnerid) {
        int code = reqjson.getIntValue("code");
        String msg = reqjson.getString("msg");
        boolean success = reqjson.getBooleanValue("success");
        if (partnerid.contains("tuniu")) {
            if (success) {
                code = 231000;
            }
            else if (code == 107) {
                code = 1600301;
            }
            else if (code == 402) {
                code = 1600303;
            }
            else if (code == 112) {
                code = 1600301;
            }
            else if (code == 113 && msg.equals("正在确认改签，不能进行取消")) {
                code = 1600302;
            }
            else if (code == 113) {
                code = 1600301;
            }
            else if (code == 1001) {
                code = 1600302;
            }
            else if (code == 999) {
                code = 1600301;
            }
            reqjson.put("code", code);
            return reqjson;
        }
        else {
            return reqjson;
        }
    }

    /**
     * 把同程错误码修饰成途牛的
     * 
     * @param reqjson
     * @return
     * @time 2016年9月10日 下午1:23:17
     * @author fiend
     */
    public static JSONObject changeCallBackCode(JSONObject reqjson) {
        String priceinfo = reqjson.containsKey("priceinfo") ? reqjson.getString("priceinfo") : "";
        if (priceinfo.contains("收取新票款")) {
            reqjson.put("totalpricediff", 0);
        }
        int code = reqjson.getIntValue("code");
        String msg = reqjson.getString("msg");
        boolean success = reqjson.getBooleanValue("success");
        if (success) {
            code = 231000;
        }
        else if (code == 301) {
            code = 1600114;
        }
        else if (code == 310) {
            code = 1600120;
        }
        else if (code == 305) {
            code = 1600120;
        }
        else if (code == 307) {
            code = 1600115;
        }
        else if (code == 1002 && msg.contains("开车前48小时以内")) {
            code = 1600119;
        }
        else if (code == 1002) {
            code = 1600103;
        }
        else if (code == 313) {
            code = 1600116;
        }
        else if (code == 998) {
            code = 1600106;
        }
        else if (code == 1004) {
            code = 1600102;
        }
        else if (code == 301) {
            code = 1600119;
        }
        else if (code == 314) {
            code = 1600106;
        }
        else if (code == 999 && msg.contains("车票已退票")) {
            code = 1600109;
        }
        else if (code == 999 && msg.contains("车票已改签")) {
            code = 1600108;
        }
        else if (code == 999 && msg.contains("车票已出票")) {
            code = 1600107;
        }
        else if (code == 999) {
            code = 1600101;
        }
        else if (code == 108 && msg.contains("取票单号")) {
            code = 1600105;
        }
        else if (code == 108 && msg.contains("存在重复")) {
            code = 1600112;
        }
        else if (code == 108 && msg.contains("不能是卧铺")) {
            code = 1600113;
        }
        else if (code == 108) {
            code = 1600101;
        }
        else if (code == 118) {
            code = 1600105;
        }
        else if (code == 112) {
            code = 1600111;
        }
        else if (code == 107) {
            code = 1600101;
        }
        else if (code == 402) {
            code = 1600101;
        }
        else if (code == 113) {
            code = 1600101;
        }

        if (code == 1600108) {
            msg = msg.split("车票已改签")[0] + "车票已改签";
            reqjson.put("msg", msg);
            reqjson.put("help_info", "已改签，不能再次改签");
        }
        reqjson.put("code", code);
        return reqjson;
    }

    /**
     * 把同程错误码修饰成途牛的
     * 
     * @param code同城错误码 msg原因  success成功与否
     * @return code途牛错误码
     * @time 2016年10月28日 下午1:23:17
     * @author zlx
     */
    public static int changeCallBackCodeV1(int code, String msg, boolean success) {
        if (success) {
            code = 231000;
        }
        else if (code == 301) {
            code = 1600114;
        }
        else if (code == 310) {
            code = 1600120;
        }
        else if (code == 305) {
            code = 1600120;
        }
        else if (code == 307) {
            code = 1600115;
        }
        else if (code == 1002 && msg.contains("开车前48小时以内")) {
            code = 1600119;
        }
        else if (code == 1002) {
            code = 1600103;
        }
        else if (code == 313) {
            code = 1600116;
        }
        else if (code == 998) {
            code = 1600106;
        }
        else if (code == 1004) {
            code = 1600102;
        }
        else if (code == 301) {
            code = 1600119;
        }
        else if (code == 314) {
            code = 1600106;
        }
        else if (code == 999 && msg.contains("车票已退票")) {
            code = 1600109;
        }
        else if (code == 999 && msg.contains("车票已改签")) {
            code = 1600108;
        }
        else if (code == 999 && msg.contains("车票已出票")) {
            code = 1600107;
        }
        else if (code == 999) {
            code = 1600101;
        }
        else if (code == 108 && msg.contains("取票单号")) {
            code = 1600105;
        }
        else if (code == 108 && msg.contains("存在重复")) {
            code = 1600112;
        }
        else if (code == 108 && msg.contains("不能是卧铺")) {
            code = 1600113;
        }
        else if (code == 108) {
            code = 1600101;
        }
        else if (code == 118) {
            code = 1600105;
        }
        else if (code == 112) {
            code = 1600111;
        }
        else if (code == 107) {
            code = 1600101;
        }
        else if (code == 402) {
            code = 1600101;
        }
        else if (code == 113) {
            code = 1600101;
        }

        return code;
    }
}
