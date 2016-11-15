package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.util.HashMap;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class YiBuChangeTest {

    public static void main(String[] args) {
        
    }

    private static void train_request_change() {
        JSONObject obj = new JSONObject();
        obj.put("reqtoken", "FT556C0B8121003DF678");
        obj.put("success", true);
        obj.put("code", "100");
        obj.put("msg", "改签占座成功");
        obj.put("method", "train_request_change");
        obj.put("callBackUrl", "http://61.155.159.8:8081/train/services/changeOrderNotify");
        obj.put("help_info", "改签占座成功");
        obj.put("transactionid", "T1506011514141535793");
        obj.put("pricedifference", 0);
        obj.put("priceinfotype", 2);
        obj.put("orderid", "TGT_S556C064621003DF122");
        obj.put("priceinfo", "改签票款差价：0.0元");
        obj.put("agentId", 1175);
        JSONArray ticketmsg = new JSONArray();
        JSONObject ticket = new JSONObject();
        ticket.put("old_ticket_no", "E6109470061050003");
        ticket.put("new_ticket_no", "E6109470062110001");
        ticket.put("cxin", "11车厢,001号");
        ticket.put("price", "1.0");
        ticket.put("piaotype", "1");
        ticket.put("passportseno", "142732199204126426");
        ticketmsg.add(ticket);
        obj.put("newtickets", ticketmsg);
        //回调
        String result = RequestUtil.post("http://121.40.125.114:19004/cn_interface/tcTrainCallBack", obj.toString(),
                "UTF-8", new HashMap<String, String>(), 0);
        System.out.println(result);
    }

    private static void train_confirm_change() {
        JSONArray ticketmsg = new JSONArray();
        JSONObject ticket = new JSONObject();
        ticket.put("old_ticket_no", "E6451973371100001");
        ticket.put("new_ticket_no", "E6451973373050021");
        ticket.put("cxin", "05车厢,021号");
        ticketmsg.add(ticket);
        JSONObject obj = new JSONObject();
        obj.put("code", "100");
        obj.put("success", true);
        obj.put("msg", "确认改签成功");
        obj.put("newticketcxins", ticketmsg);
        obj.put("agentId", 1175);
        obj.put("orderid", "TGT_S556U73F321003DF389");
        //退还原票票款记录的同程资金变动流水号    1~32    string
        obj.put("oldticketchangeserial", "B15060319244010794");
        //收取新票票款记录的同程资金变动流水号    1~32    string
        obj.put("newticketchangeserial", "B15060319244009353");
        obj.put("method", "train_confirm_change");
        obj.put("reqtoken", "FT556UA8C921003DF123");
        obj.put("callBackUrl", "http://61.155.159.8:8081/train/services/confirmTicChangeNotify");
        //回调
        String result = RequestUtil.post("http://121.40.125.114:19004/cn_interface/tcTrainCallBack", obj.toString(),
                "UTF-8", new HashMap<String, String>(), 0);
        System.out.println(result);
    }
}