package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.util.Map;
import java.util.List;
import java.net.URLEncoder;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class RefundCallBackDemo {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
        /*
        String trainRefundPriceUrl = "";
        //SQL
        String sql = "select top 150 p.C_ORDERID, t.ID, t.C_PROCEDURE from T_TRAINTICKET t "
                + "join T_TRAINPASSENGER p on p.ID = t.C_TRAINPID "
                + "where t.C_STATUS = 11 and t.C_REFUNDREQUESTTIME > '2015-04-02 00:00:00' "
                + "order by t.C_REFUNDREQUESTTIME";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            long orderId = Long.parseLong(map.get("C_ORDERID").toString());
            long ticketId = Long.parseLong(map.get("ID").toString());
            float procedure = Float.parseFloat(map.get("C_PROCEDURE").toString());
            String url = trainRefundPriceUrl + "?trainorderid=" + orderId + "&ticketid=" + ticketId
                    + "&interfacetype=3&procedure=" + procedure + "&responseurl=" + trainRefundPriceUrl;
            SendPostandGet.submitGet(url, "UTF-8");
            System.out.println(orderId + "---" + ticketId + "---" + procedure + "---" + url);
        }*/
        String callbackurl = "http://api.tuniu.org/aln/train/aerospace/refundFeedback";

        JSONObject param = new JSONObject();

        long longTimestamp = System.currentTimeMillis();
        longTimestamp = longTimestamp / 1000;
        String timestamp = String.valueOf(longTimestamp);
        param.put("timestamp", timestamp);

        String partnerid = "tuniulvyou";
        String returntype = "0";
        String apiorderid = "12324399";
        String trainorderid = "EC38470449";
        String returnmoney = "26";
        boolean returnstate = true;
        String key = "v66r9ogtcvtxv3v4xq3gog8fqdbhwmt0";
        String sign = partnerid + returntype + timestamp + apiorderid + trainorderid + returnmoney + returnstate
                + ElongHotelInterfaceUtil.MD5(key);

        param.put("sign", ElongHotelInterfaceUtil.MD5(sign));
        param.put("reqtoken", System.currentTimeMillis());
        param.put("apiorderid", apiorderid);
        param.put("token", "");
        param.put("returnmoney", returnmoney);
        param.put("trainorderid", trainorderid);
        param.put("returnmsg", "");
        param.put("returnstate", returnstate);
        param.put("returntype", returntype);

        JSONArray returntickets = new JSONArray();
        JSONObject ticket = new JSONObject();
        ticket.put("ticket_no", "EC384704491030046");
        ticket.put("passengername", URLEncoder.encode("纪良伟", "utf-8"));
        ticket.put("passporttypeseid", "1");
        ticket.put("passportseno", "34290119941019065x");
        ticket.put("returnsuccess", returnstate);
        ticket.put("returnmoney", returnmoney);
        ticket.put("returntime", TimeUtil.gettodaydate(4));
        ticket.put("returnfailid", "");
        ticket.put("returnfailmsg", "");
        returntickets.add(ticket);
        param.put("returntickets", returntickets);

        System.out.println(callbackurl + "?data=" + param);

    }
}