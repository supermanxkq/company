package com.ccservice.b2b2c.atom.servlet.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;

public class JobTaobaoTrainPrice implements Job {
    private int BEGIN_TICKET_ID = 0;

    private int END_TICKET_ID = 10000;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            String str_begin_ticket_id = PropertyUtil.getValue("begin_ticket_id", "Train.properties");
            String str_end_ticket_id = PropertyUtil.getValue("end_ticket_id", "Train.properties");
            if (str_begin_ticket_id != null && !"".equals(str_begin_ticket_id)) {
                BEGIN_TICKET_ID = Integer.valueOf(str_begin_ticket_id);
            }
            if (str_end_ticket_id != null && !"".equals(str_end_ticket_id)) {
                END_TICKET_ID = Integer.valueOf(str_end_ticket_id);
            }
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        TaobaoHotelInterfaceUtil tbiu = new TaobaoHotelInterfaceUtil();
        String sql = "SELECT   DISTINCT " + "C_DEPARTURE dep,C_ARRIVAL arr,C_TRAINNO trainno,"
                + "C_DEPARTTIME deptime,C_SEATTYPE seattype,C_PRICE price,"
                + "SUBSTRING(C_SEATNO,CHARINDEX('号',C_SEATNO)+1,2) seatno " + "FROM T_TRAINTICKET WITH(NOLOCK) "
                + "WHERE  ID>" + BEGIN_TICKET_ID + " AND ID<=" + END_TICKET_ID
                + "  AND C_SEATTYPE IN ('硬卧','软卧','高级软卧') AND C_STATUS>2 AND C_STATUS!=4 AND C_TICKETTYPE=1";
        WriteLog.write("JobTaobaoTrainPrice", sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        try {
            WriteLog.write("JobTaobaoTrainPrice", list.size() + "");
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                try {
                    if (!"上铺".equals(map.get("seatno").toString()) && !"中铺".equals(map.get("seatno").toString())) {
                        String seattype = TaobaoHotelInterfaceUtil.CackBackSuccessseao(map.get("seattype").toString(),
                                map.get("seatno").toString());
                        String fromstationname = map.get("dep").toString();
                        String tostationname = map.get("arr").toString();
                        String traincode = map.get("trainno").toString();
                        String depdate = map.get("deptime").toString();
                        String seatprice = map.get("price").toString();
                        int seatpriceint = (int) (Float.valueOf(seatprice) * 100);
                        WriteLog.write("JobTaobaoTrainPrice", traincode + "@" + tostationname + "@" + seatprice + "@"
                                + fromstationname + "@" + depdate + "@" + seattype);
                        tbiu.TrainAgentseatpriceSet(traincode, tostationname, seatpriceint + "", fromstationname,
                                depdate, seattype);
                        try {
                            Thread.sleep(100L);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        //                updateTrainpriceBySP(traincode, tostationname, seatprice, fromstationname, map.get("seattype")
                        //                        .toString(), map.get("seatno").toString());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过存储过程更新车次卧铺价格
     * @param traincode
     * @param tostationname
     * @param seatprice
     * @param fromstationname
     * @param depdate
     * @param seattype
     * @author fiend
     */
    private void updateTrainpriceBySP(String traincode, String tostationname, String seatprice, String fromstationname,
            String seattype, String seatno) {
        Map<String, String> pricemap = priceMap(seattype, seatno, seatprice);
        try {
            Server.getInstance()
                    .getSystemService()
                    .findMapResultByProcedure(
                            "sp_Trainno_Trainprice_Update @stationnostart='" + fromstationname + "',@stationnoend='"
                                    + tostationname + "',@stationtraincode='" + traincode + "',@ywsprice='"
                                    + pricemap.get("ywsprice").toString() + "',@ywzprice='"
                                    + pricemap.get("ywzprice").toString() + "',@ywxprice='"
                                    + pricemap.get("ywxprice").toString() + "',@rwsprice='"
                                    + pricemap.get("rwsprice").toString() + "',@rwxprice='"
                                    + pricemap.get("rwxprice").toString() + "'");
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 通过坐席类型和坐席号获取价格MAP集合
     * @param seattype
     * @param seatprice
     * @return
     * @author fiend
     */
    private Map<String, String> priceMap(String seattype, String seatno, String seatprice) {
        try {
            seatprice = seatprice.substring(0, seatprice.length() - 1);
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("ywsprice", "");
        map.put("ywzprice", "");
        map.put("ywxprice", "");
        map.put("rwsprice", "");
        map.put("rwxprice", "");
        try {
            if ("硬卧".equals(seattype)) {
                if (seatno.contains("上")) {
                    map.put("ywsprice", seatprice);
                }
                else if (seatno.contains("中")) {
                    map.put("ywzprice", seatprice);
                }
                else if (seatno.contains("下")) {
                    map.put("ywxprice", seatprice);
                }
            }
            if ("软卧".equals(seattype) || "高级软卧".equals(seattype)) {
                if (seatno.contains("上")) {
                    map.put("rwsprice", seatprice);
                }
                else if (seatno.contains("下")) {
                    map.put("rwxprice", seatprice);
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 通过存储过程更新车次卧铺价格
     * @param traincode
     * @param tostationname
     * @param seatprice
     * @param fromstationname
     * @param depdate
     * @param seattype
     * @author fiend
     */
    public static void updateTrainpriceBySPTest(String traincode, String tostationname, String seatprice,
            String fromstationname, String seattype, String seatno) {
        Map<String, String> pricemap = priceMapTest(seattype, seatno, seatprice);
        try {
            System.out
                    .println("sp_Trainno_Trainprice_Update @stationnostart='" + fromstationname + "',@stationnoend='"
                            + tostationname + "',@stationtraincode='" + traincode + "',@ywsprice='"
                            + pricemap.get("ywsprice").toString() + "',@ywzprice='"
                            + pricemap.get("ywzprice").toString() + "',@ywxprice='"
                            + pricemap.get("ywxprice").toString() + "',@rwsprice='"
                            + pricemap.get("rwsprice").toString() + "',@rwxprice='"
                            + pricemap.get("rwxprice").toString() + "'");
            Server.getInstance()
                    .getSystemService()
                    .findMapResultByProcedure(
                            "sp_Trainno_Trainprice_Update @stationnostart='" + fromstationname + "',@stationnoend='"
                                    + tostationname + "',@stationtraincode='" + traincode + "',@ywsprice='"
                                    + pricemap.get("ywsprice").toString() + "',@ywzprice='"
                                    + pricemap.get("ywzprice").toString() + "',@ywxprice='"
                                    + pricemap.get("ywxprice").toString() + "',@rwsprice='"
                                    + pricemap.get("rwsprice").toString() + "',@rwxprice='"
                                    + pricemap.get("rwxprice").toString() + "'");
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 通过坐席类型和坐席号获取价格MAP集合
     * @param seattype
     * @param seatprice
     * @return
     * @author fiend
     */
    public static Map<String, String> priceMapTest(String seattype, String seatno, String seatprice) {
        try {
            seatprice = seatprice.substring(0, seatprice.length() - 1);
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("ywsprice", "");
        map.put("ywzprice", "");
        map.put("ywxprice", "");
        map.put("rwsprice", "");
        map.put("rwxprice", "");
        try {
            if ("硬卧".equals(seattype)) {
                if (seatno.contains("上")) {
                    map.put("ywsprice", seatprice);
                }
                else if (seatno.contains("中")) {
                    map.put("ywzprice", seatprice);
                }
                else if (seatno.contains("下")) {
                    map.put("ywxprice", seatprice);
                }
            }
            if ("软卧".equals(seattype) || "高级软卧".equals(seattype)) {
                if (seatno.contains("上")) {
                    map.put("rwsprice", seatprice);
                }
                else if (seatno.contains("下")) {
                    map.put("rwxprice", seatprice);
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) {
        updateTrainpriceBySPTest("1461", "廊坊北", "1.00", "北京", "硬卧", "上铺");
    }
}
