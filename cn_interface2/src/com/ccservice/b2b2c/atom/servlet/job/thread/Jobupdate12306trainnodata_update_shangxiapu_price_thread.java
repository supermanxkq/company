package com.ccservice.b2b2c.atom.servlet.job.thread;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.server.Server;

/**
 * 更新上下铺的价格
 * 
 * @time 2015年5月5日 下午1:01:07
 * @author chendong
 */
public class Jobupdate12306trainnodata_update_shangxiapu_price_thread extends Thread {

    String checi_String;//车次

    int updatetype;//1:有html 0:无html

    int htmltype; //1:58同城

    String html;

    public Jobupdate12306trainnodata_update_shangxiapu_price_thread(String checi_String) {
        super();
        this.checi_String = checi_String;
    }

    @Override
    public void run() {
        excute();
    }

    private void excute() {
        String sql = "select count(*) count from T_TRAINNO where C_STATION_TRAIN_CODE='" + this.checi_String + "' ";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        Map map = (Map) list.get(0);
        int count = Integer.parseInt(map.get("count").toString());
        if (count > 0) {//如果数据库里有
            updategonglishu();
        }
        else {//如果数据库里没有,去执行添加的方法

        }
    }

    /**
     * 真正更新公里数的方法
     * 
     * @time 2015年5月5日 下午12:45:46
     * @author chendong
     */
    private void updategonglishu() {
        String checi_url = "http://lieche.58.com/checi/" + checi_String.replace('/', '-') + "/";
        String checi_url_html = SendPostandGet.submitGet(checi_url);
        String[] checi_list_htmls = checi_url_html.split("class=\"trbgoff\"");
        String sqlstring = "update T_TRAINNO set C_DISTANCE=0 where C_STATION_TRAIN_CODE='" + checi_String + "'";
        if (checi_list_htmls.length == 1) {
            sqlstring = "update T_TRAINNO set C_DISTANCE=0 where C_STATION_TRAIN_CODE='" + checi_String + "'";
        }
        else {
            sqlstring = "update T_TRAINNO set C_DISTANCE=0 where C_STATION_TRAIN_CODE='" + checi_String
                    + "' AND C_STATION_NO='01'";
        }
        int count = Server.getInstance().getSystemService().excuteGiftBySql(sqlstring);
        for (int j = 1; j < checi_list_htmls.length; j++) {
            String checi_String_1 = checi_list_htmls[j];
            String[] checi_list_htmls_1 = checi_String_1.split("<td");
            int gonglishu = 0;
            String daodazhan = "";
            for (int k = 1; k < checi_list_htmls_1.length; k++) {//获取公里数
                String infodata = checi_list_htmls_1[k];
                infodata = infodata.replace("</td>", "").replace(">", "").trim();
                if (k == 2) {
                    daodazhan = infodata.split("blank")[1].replace("\"", "").replace("</a", "").trim();
                }
                if (infodata.indexOf("公里") >= 0) {//处理公里数
                    infodata = infodata.replace("公里", "");
                    try {
                        gonglishu = Integer.parseInt(infodata);
                    }
                    catch (Exception e) {
                    }
                    //                        break;
                }
            }
            sqlstring = "update T_TRAINNO set C_DISTANCE=" + gonglishu + " where C_STATION_NAME='" + daodazhan
                    + "' and C_STATION_TRAIN_CODE='" + checi_String + "'";
            count = Server.getInstance().getSystemService().excuteGiftBySql(sqlstring);
            System.out.println(count + ":" + sqlstring);
        }
    }
}
