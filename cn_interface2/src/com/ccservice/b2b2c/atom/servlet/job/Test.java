package com.ccservice.b2b2c.atom.servlet.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.HttpUtils;

public class Test {
    public static void main(String[] args) {
        long a = System.currentTimeMillis();
        String checi_url = "https://kyfw.12306.cn/otn/resources/js/query/train_list.js?scriptVersion=1.5165";
        String train_listJson = HttpUtils.Get_https(checi_url, 30000);
        train_listJson = train_listJson.replace("var train_list =", "");
        for (int i = 0; i < 61; i++) {
            insert(datetime(i), train_listJson);
        }
        System.out.println("\r执行耗时 : " + (System.currentTimeMillis() - a) / 1000f + " 秒 ");
    }

    public static void insert(String TrainCode, String train_listJson) {
        try {
            System.out.println("执行插入" + TrainCode + "的列车数据");
            JSONObject json = new JSONObject(train_listJson);
            JSONObject json1 = null;
            String b = json.get(TrainCode).toString();
            json = new JSONObject(b);
            String[] ABC = { "D", "T", "G", "C", "O", "K", "Z" };
            for (int j = 0; j < ABC.length; j++) {
                b = json.get(ABC[j]).toString();
                String[] ab = b.split("},");
                ab[0] = ab[0].substring(1);
                ab[ab.length - 1] = ab[ab.length - 1].substring(0, ab[ab.length - 1].length() - 1);
                for (int i = 0; i < ab.length; i++) {
                    if (i < ab.length - 1) {
                        ab[i] = ab[i] + "}";
                    }
                    json1 = new JSONObject(ab[i]);
                    String c = (String) json1.get("station_train_code");
                    c = c.substring(0, c.indexOf("("));
                    String sqlinsert = "INSERT INTO TrainDetailCode(TrainCode,TrainDetailsCode,TrainDepartTime) values('"
                            + c + "','" + json1.get("train_no") + "','" + TrainCode + "')";
                    Server.getInstance().getSystemService().excuteEaccountBySql(sqlinsert);
                }
                System.out.println("插入" + ABC[j] + "次列车");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String datetime(int i) {
        String date = null;
        if (i >= 1) {
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = df.format(new Date(d.getTime() + (long) i * 24 * 60 * 60 * 1000));
        }
        else {
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = df.format(d);
        }
        return date;
    }
}
