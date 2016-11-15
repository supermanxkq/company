package com.ccservice.b2b2c.atom.servlet;

import java.util.Map;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class TongChengCallBackServletMeiTuan {
    /**
     * 回调美团出票失败
     * 
     * @param trainorderid
     * @param transactionid
     * @param errorCount
     * @param isSuccess
     * @param iskefu
     * @param dataString
     * @param map_data
     * @param reqtime
     * @param qunarordernumber
     * @return
     * @time 2015年8月18日 下午9:05:34
     * @author chendong
     */
    public static String payCallBack_meituan_fail(String trainorderid, String transactionid, int errorCount,
            String isSuccess, String iskefu, String dataString, Map map_data, String reqtime, String qunarordernumber) {
        String ret = "false";
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan_fail", trainorderid + ":transactionid:" + transactionid
                + ":dataString:" + dataString);
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan_fail", trainorderid + ":map_data:" + map_data);
        String zhanzuojieguoBackUrl_temp_other = getValueByMap(map_data, "C_PAYCALLBACKURL");
        try {
            String sign = "";
            String payCallbackUrl_temp = "";
            if (zhanzuojieguoBackUrl_temp_other != null && !"-1".equals(zhanzuojieguoBackUrl_temp_other)) {
                String partnerid = getValueByMap(map_data, "C_USERNAME");
                String key = getValueByMap(map_data, "C_KEY");
                sign = ElongHotelInterfaceUtil.MD5(key);
                sign = partnerid + reqtime + sign;
                sign = ElongHotelInterfaceUtil.MD5(sign);
                payCallbackUrl_temp = zhanzuojieguoBackUrl_temp_other;

                String data = dataString;
                String parm = data + "&reqtime=" + reqtime + "&sign=" + sign + "&orderid=" + qunarordernumber
                        + "&transactionid=" + transactionid.trim() + "&isSuccess=" + isSuccess;
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan_fail", trainorderid + ":payCallbackUrl_temp:"
                        + payCallbackUrl_temp + ":parm:" + parm);
                try {
                    ret = SendPostandGet.submitPost(payCallbackUrl_temp, parm, "utf-8").toString();
                }
                catch (Exception e) {
                    WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan_fail_Exception", "orderid:" + trainorderid + ":"
                            + e.fillInStackTrace().toString());
                }
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan_fail", trainorderid + ":回调接口返回:" + ret);
                if ("success".equalsIgnoreCase(ret)) { //成功
                    return "success";
                }
                else {
                    throw new Exception(ret);
                }
            }
        }
        catch (Exception e) {
        }
        return ret;
    }

    /**
     * 从map中获取对应的数据
     * @time 2015年7月25日 上午11:04:54
     * @author chendong
     */
    private static String getValueByMap(Map map, String key) {
        String value = "-1";
        if (map.get(key) != null) {
            try {
                value = map.get(key).toString();
            }
            catch (Exception e) {
            }
        }
        return value;
    }
}
