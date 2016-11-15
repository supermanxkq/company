package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.base.zrate.Zrate;

/**
 * 遍历每个车次漏掉的所有车站
 * 
 * @time 2014年12月24日 下午3:01:26
 * @author chendong
 */
public class SearchLoudechezhanThread extends Thread {
    String lieche;

    int i1;

    public SearchLoudechezhanThread(String lieche, int i1) {
        this.lieche = lieche;
        this.i1 = i1;
    }

    @Override
    public void run() {
        List<Zrate> liecheinfoList = getliecheinfoList(getcheciinfo(this.lieche));

        if (liecheinfoList.size() > 0) {
            lieche += "_";
            List clist = getpricefromdb(lieche);
            getnoliecheinfo(clist, liecheinfoList);
        }
    }

    private void getnoliecheinfo(List clist, List<Zrate> liecheinfoList) {
        // TODO Auto-generated method stub

    }

    /**
     * 
     * 
     * @param liecheno
     * @param liecheinfo
     * @time 2014年12月24日 下午2:27:44
     * @author chendong
     */
    public void getnoliecheinfo_old(List<String> liecheno, List<Zrate> liecheinfoList) {

        for (int i = 0; i < liecheno.size(); i++) {
            String liechecode = liecheno.get(i);
            if (i + 1 < liecheno.size()) {
                String liechecode2 = liecheno.get(i + 1);
                String[] liechecodes = liechecode.split("_");
                String[] liechecodes2 = liechecode2.split("_");
                String code = liechecodes[0];
                String code2 = liechecodes2[0];
                int s = Integer.parseInt(liechecodes[1]);
                int s2 = Integer.parseInt(liechecodes2[1]);
                if (s == s2) {
                    int e = Integer.parseInt(liechecodes[2]);
                    int e2 = Integer.parseInt(liechecodes2[2]);
                    int temp_s = s + 1;
                    int temp_e = e - 1;

                    s = s;
                    e = e2 - 1;
                    if ((e) > s) {
                        try {
                            long l1 = System.currentTimeMillis();
                            WriteLog.write("no_lieche", i + ":" + l1 + "-----" + "----" + s + "------" + e);
                            String sszm = liecheinfoList.get(s).getDepartureport();
                            String eszm = liecheinfoList.get(e).getDepartureport();
                            sszm = Train12306StationInfoUtil.getThreeByName(sszm);
                            eszm = Train12306StationInfoUtil.getThreeByName(eszm);
                            if (sszm != null && eszm != null) {
                                String method = "train_query";
                                String json1 = SendPostandGet
                                        .submitPost(
                                                "http://tcsearchtrain.hangtian123.net/trainSearch",
                                                "jsonStr={\"train_date\":\"2015-02-12\",\"from_station\":\""
                                                        + sszm
                                                        + "\",\"to_station\":\""
                                                        + eszm
                                                        + "\",\"purpose_codes\":\"ADULT\",\"partnerid\":\"tongcheng_train_test\",\"method\":\""
                                                        + method
                                                        + "\",\"reqtime\":\"20141216101349\",\"sign\":\"f78eebbfc09ede00688d785fb1c77cbf\"}",
                                                "utf-8").toString();
                                WriteLog.write("no_lieche", i + "-时:" + l1 + ":间:" + (System.currentTimeMillis() - l1)
                                        + "-----" + json1 + "----" + sszm + "------" + eszm);
                            }
                        }
                        catch (Exception e1) {
                        }

                    }
                }
            }
        }
    }

    public static List<String> getpricefromdb(String key) {
        List<String> liecheno = new ArrayList<String>();
        String sqlselect = "SELECT C_MCCKEY FROM T_TRAINPRICE where C_MCCKEY like '" + key + "%'";
        List clist = new ArrayList();
        try {
            clist = Server.getInstance().getSystemService().findMapResultBySql(sqlselect, null);
            if (clist.size() > 0) {
                for (int i = 0; i < clist.size(); i++) {
                    Map m = (Map) clist.get(i);
                    String C_MCCKEY = m.get("C_MCCKEY").toString();
                    liecheno.add(C_MCCKEY);
                }
            }
        }
        catch (Exception e) {
        }
        return liecheno;
    }

    public static String getcheciinfo(String code) {
        String url = "http://train.qunar.com/qunar/checiInfo.jsp?method_name=buy&ex_track=&q=" + code
                + "&date=20141225&format=json&cityname=123456&ver=" + System.currentTimeMillis()
                + "&callback=XQScript_6";
        url = SendPostandGet.submitGet(url, "utf-8");
        return url;
    }

    public static List<Zrate> getliecheinfoList(String jsonString) {
        List<Zrate> zrates = new ArrayList<Zrate>();
        Zrate z = new Zrate();
        z.setRatevalue(-1f);
        zrates.add(z);
        jsonString = jsonString.replace("XQScript_6(", "").replace(");", "");
        try {
            JSONObject jsonobject = JSONObject.parseObject(jsonString);
            JSONArray jsonarray = (JSONArray) jsonobject.get("trainScheduleBody");
            for (int i = 0; i < jsonarray.size(); i++) {
                String content = jsonarray.getJSONObject(i).getString("content");
                String[] contents = content.split(",");
                Zrate zrate = new Zrate();
                zrate.setRatevalue(Float.parseFloat(contents[6].replace("公里", "").replaceAll("\"", "")));
                zrate.setDepartureport(contents[1].replaceAll("\"", ""));
                zrates.add(zrate);
            }
            Collections.sort(zrates, new Comparator<Zrate>() {
                @Override
                public int compare(Zrate o1, Zrate o2) {
                    return (int) (o1.getRatevalue().compareTo(o2.getRatevalue()));
                }
            });
        }
        catch (Exception e) {
        }
        return zrates;
    }
}
