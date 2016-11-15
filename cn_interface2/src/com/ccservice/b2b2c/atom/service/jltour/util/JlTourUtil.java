package com.ccservice.b2b2c.atom.service.jltour.util;

import java.net.URLEncoder;
import net.sf.json.JSONObject;
import com.ccservice.b2b2c.atom.qunar.PHUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 深捷旅JSON接口工具类
 * @author WH
 */

public class JlTourUtil {

    //数据中转
    public static String getMiddleData(JSONObject req) throws Exception {
        //用户编号、授权码
        req.put("usercd", PropertyUtil.getValue("jlUserCd"));
        req.put("authno", PropertyUtil.getValue("jlAuthNo"));
        //中转地址
        String url = "http://" + PropertyUtil.getValue("jlMiddleServer") + "/cn_interface/JlTourHotel.jsp?req="
                + URLEncoder.encode(req.toString(), "utf-8");
        //POST
        String ret = PHUtil.submitPost(url, "").toString();
        if (!ElongHotelInterfaceUtil.StringIsNull(ret)) {
            ret = ret.substring(ret.indexOf("{"));
        }
        return ret;
    }

    //国家编码，用于判断是否是国内酒店
    public static boolean isChina(int country) {
        boolean flag = false;
        //中国
        if (country == 70007) {
            flag = true;
        }
        //香港
        else if (country == 70008) {
            flag = true;
        }
        //澳门
        else if (country == 70009) {
            flag = true;
        }
        //台湾
        else if (country == 70555) {
            flag = true;
        }
        return flag;
    }

    //酒店星级
    public static int getStar(int jlstar) {
        int star = 0;
        if (jlstar == 55) {
            star = 5;
        }
        else if (jlstar == 50) {
            star = 16;
        }
        else if (jlstar == 45) {
            star = 4;
        }
        else if (jlstar == 40) {
            star = 13;
        }
        else if (jlstar == 35) {
            star = 3;
        }
        else if (jlstar == 30) {
            star = 10;
        }
        return star;
    }

    //酒店床型
    public static int getBed(String jlbed) {
        int bed = 0;
        if ("single".equals(jlbed)) {
            bed = 1;
        }
        else if ("big".equals(jlbed) || "bigsing".equals(jlbed)) {
            bed = 2;
        }
        else if ("double".equals(jlbed) || "sindou".equals(jlbed)) {
            bed = 3;
        }
        else if ("bigdou".equals(jlbed)) {
            bed = 4;
        }
        return bed;
    }

    //酒店设施
    public static String getFacilitiy(String jltype) {
        String facilitiy = "";
        if ("11".equals(jltype)) {
            facilitiy = "停车场";
        }
        else if ("12".equals(jltype)) {
            facilitiy = "会议室";
        }
        else if ("13".equals(jltype)) {
            facilitiy = "游泳池";
        }
        else if ("14".equals(jltype)) {
            facilitiy = "健身房";
        }
        else if ("15".equals(jltype)) {
            facilitiy = "洗衣服务";
        }
        else if ("16".equals(jltype)) {
            facilitiy = "中餐厅";
        }
        else if ("17".equals(jltype)) {
            facilitiy = "西餐厅";
        }
        else if ("18".equals(jltype)) {
            facilitiy = "宴会厅";
        }
        else if ("19".equals(jltype)) {
            facilitiy = "租车服务";
        }
        else if ("20".equals(jltype)) {
            facilitiy = "外币兑换";
        }
        else if ("21".equals(jltype)) {
            facilitiy = "咖啡厅";
        }
        else if ("22".equals(jltype)) {
            facilitiy = "ATM机";
        }
        else if ("23".equals(jltype)) {
            facilitiy = "酒吧";
        }
        else if ("24".equals(jltype)) {
            facilitiy = "叫醒服务";
        }
        else if ("25".equals(jltype)) {
            facilitiy = "网球场";
        }
        else if ("26".equals(jltype)) {
            facilitiy = "歌舞厅";
        }
        else if ("27".equals(jltype)) {
            facilitiy = "美容美发";
        }
        else if ("30".equals(jltype)) {
            facilitiy = "前台贵重物品保险柜";
        }
        else if ("31".equals(jltype)) {
            facilitiy = "送餐服务";
        }
        else if ("32".equals(jltype)) {
            facilitiy = "礼宾司服务";
        }
        else if ("33".equals(jltype)) {
            facilitiy = "商务中心";
        }
        else if ("34".equals(jltype)) {
            facilitiy = "旅游服务";
        }
        return facilitiy;
    }
}
