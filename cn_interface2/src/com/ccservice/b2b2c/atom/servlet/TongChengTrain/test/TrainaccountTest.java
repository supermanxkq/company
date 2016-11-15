package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.util.PageInfo;

public class TrainaccountTest {
    static String accountUrl = "http://localhost:9018/trainAccount/train12306account";

    public static void main(String[] args) {
        test12306account();
    }

    private static void test12306account() {
        PageInfo pageinfo = new PageInfo();
        List list = Server
                .getInstance()
                .getSystemService()
                .findMapResultSortBySql(
                        "SELECT TOP 100 C_NAME,C_IDTYPE,C_IDNUMBER,C_ADUITSTATUS FROM T_TRAINPASSENGER",
                        "ORDER BY ID DESC", null);
        //        String passengerinfo = "陈栋,412823198909298017,1,1,1|王战朝,412823199109298017,1,0,0|";//姓名，证件号，证件类型，是否核验状态，是否已注册
        //        String[] passengerinfos = passengerinfo.split("[|]");

        String paramstr = "";
        JSONArray jsonarray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject json1 = new JSONObject();
            //            String passengerinfo_t = passengerinfos[i];
            //            String[] passengerinfo_ts = passengerinfo_t.split(",");
            Map map = (Map) list.get(i);
            json1.put("name", map.get("C_NAME"));//姓名
            json1.put("idnumber", map.get("C_IDNUMBER"));//证件号
            json1.put("idtype", map.get("C_IDTYPE"));//证件类型
            json1.put("aduitstatus", map.get("C_ADUITSTATUS"));//是否核验状态
            json1.put("changeid", 0);//是否已注册
            jsonarray.add(json1);
        }
        paramstr = JSONObject.toJSONString(jsonarray);
        paramstr = "data=" + paramstr;
        System.out.println(paramstr);
        //        String result = SendPostandGet.submitPost(accountUrl, paramstr, "UTF-8").toString();
        //        System.out.println(result);

    }
}
