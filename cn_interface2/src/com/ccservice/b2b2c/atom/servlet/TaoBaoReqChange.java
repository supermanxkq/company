package com.ccservice.b2b2c.atom.servlet;

import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengReqChange;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 淘宝改签，直接调同程
 * @author WH
 */

public class TaoBaoReqChange extends TongChengReqChange {

    public String operate(JSONObject reqobj) {
        String taobaoReqChangeUrl = PropertyUtil.getValue("taobaoreqchange_url", "Train.properties");
        try {
            //淘宝改签，1:比较改签出发时间
            reqobj.put("compareDateTime", "1");
            //远程请求
            if (taobaoReqChangeUrl != null && !"".equals(taobaoReqChangeUrl)) {
                System.out.println(reqobj.toJSONString());
                return SendPostandGet2.doGet(taobaoReqChangeUrl + URLEncoder.encode(reqobj.toJSONString(), "UTF-8"),
                        "UTF-8");
            }
            else {
                return new TongChengReqChange().operate(reqobj);
            }
        }
        catch (Exception e) {
            JSONObject retobj = new JSONObject();
            retobj.put("success", false);
            retobj.put("code", 999);
            retobj.put("msg", "请求改签失败");
            return (retobj.toJSONString());
        }
    }

    public static void main(String[] args) {
        try {
            String string = SendPostandGet2
                    .doGet("http://120.26.100.206:58016/cn_interface/TaobaoReqChange.jsp?jsonStr="
                            + URLEncoder
                                    .encode("{\"change_datetime\":\"2015-06-25 07:00:00\",\"change_checi\":\"K7092\",\"transactionid\":\"T15051213572922089  3  3\",\"old_zwcode\":\"1\",\"ticketinfo\":[{\"piaotype\":1,\"passengersename\":\"赵一雷\",\"old_ticket_no\":\"E659368377101003b\",\"passportseno\":\"110105197711260410\",\"passporttypeseid\":1}],\"ordernumber\":\"E659368377\",\"orderid\":\"1044324822281945\",\"change_zwcode\":\"3\"}",
                                            "UTF-8"), "UTF-8");
            JSONObject jso = JSONObject.parseObject(string);
            System.out.println(jso.toJSONString());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}