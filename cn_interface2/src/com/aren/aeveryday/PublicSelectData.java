package com.aren.aeveryday;

import java.util.List;

import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

public class PublicSelectData {

    public static void main(String[] args) {
//        String time=TimeUtil.dateToString("2016-11-01", TimeUtil.yyyyMMdd);
        String time="2016-11-03";
        System.out.println(time+"------------------------------啊，啊啊，西湖美景，三月天------------------------------------------------------------->");
        getcgl(time);
        getCardCgl(time);
        getGPNum(time);
        getktoffline(time);
        getRobTicket(time);
        gettccgl(time);
        System.out.println(time+"------------------------------断桥边，三生缘，哈哈，呵呵------------------------------------------------------------->");
    }

    /**
     * 
     * @author RRRRRR
     * @time 2016年11月2日 上午11:52:47
     * @Description 空铁成功率   途牛   淘宝   托管成功率 
     * @param time
     */
    public static void getcgl(String time) {
        System.out.println("空铁-----------------"+time+"-------------start------------------------------------------------------------->");
        String sql = " select  OrderAccountSuccess as tunius,OrderAccountSum as tuniusum,"
                + "OrderCookieSuccess as taobaos,OrderCookieSum as taobaosum,"
                + "OrderSuccess as kts,OrderSum as ktsum "
                + "from TrainSuccessRate with(nolock) where  convert(varchar(10),OrderDate,120)='" + time + "'";
        DataTable dt = DBHelperKT4.GetDataTable(sql);
        List<DataRow> dataRows = dt.GetRow();
        DataRow datarow = dataRows.get(0);
        String tunius = datarow.GetColumnString("tunius").toString();
        String tuniusum = datarow.GetColumnString("tuniusum").toString();
        String taobaos = datarow.GetColumnString("taobaos").toString();
        String taobaosum = datarow.GetColumnString("taobaosum").toString();
        String kts = datarow.GetColumnString("kts").toString();
        String ktsum = datarow.GetColumnString("ktsum").toString();
        System.out.println("途牛托管成功率：  "+(Float.valueOf(tunius)/Float.valueOf(tuniusum)*100));
        System.out.println("淘宝托管成功率：  "+(Float.valueOf(taobaos)/Float.valueOf(taobaosum))*100);
        System.out.println("空铁成功率：  "+(Float.valueOf(kts)/Float.valueOf(ktsum))*100);
        System.out.println("空铁---------------------------------------over------------------------------------------------------------->");
    }

    /**
     * 
     * @author RRRRRR
     * @time 2016年11月2日 下午12:06:03
     * @Description 线下票  出票数  成功率
     * @param time
     */
    public static void getktoffline(String time) {
        System.out.println("线下-----------------"+time+"-------------start------------------------------------------------------------->");
        String sql = "select COUNT(1) as success from TrainOrderOffline  O with(nolock),TrainTicketOffline T with(nolock) where O.Id=T.OrderId and  convert(varchar(10),O.CreateTime,120)='"
                + time + "' and O.OrderStatus=2 ";
        DataTable dt = DBHelperKT4offline.GetDataTable(sql);
        List<DataRow> dataRows = dt.GetRow();
        DataRow datarow = dataRows.get(0);
        String success = datarow.GetColumnString("success").toString();
        String sql2 = "select COUNT(1) as sum from TrainOrderOffline O ,TrainTicketOffline T where O.Id=T.OrderId and convert(varchar(10),O.CreateTime,120)='"+time+"' ";
        DataTable dt2 = DBHelperKT4offline.GetDataTable(sql2);
        List<DataRow> dataRows2 = dt2.GetRow();
        DataRow datarow2 = dataRows2.get(0);
        String sum = datarow2.GetColumnString("sum").toString();
        System.out.println("线下当日出票数：  "+success+" 线下当日订单数：  "+sum);
        System.out.println("线下成功率：  "+(Float.valueOf(success)/Float.valueOf(sum))*100);
        System.out.println("线下---------------------------------------over------------------------------------------------------------->");
    }

    /**
     * 
     * @author RRRRRR
     * @time 2016年11月2日 下午12:54:08
     * @Description 同程成功率
     * @param time1
     */
    public static void gettccgl(String time1) {
        System.out.println("同程-----------------"+time1+"-------------start------------------------------------------------------------->");
        String sql = "select ordersuccess,ordersum from trainordersuccessrate with (nolock) where convert(varchar(10),createtime,120)='"
                + time1 + "'";
        DataTable dt = DBHelperTC.GetDataTable(sql);
        List<DataRow> dataRows = dt.GetRow();
        DataRow datarow = dataRows.get(0);
        String ordersuccess = datarow.GetColumnString("ordersuccess").toString();
        String ordersum = datarow.GetColumnString("ordersum").toString();
        System.out.println("同程出票数：  "+ordersuccess+" 同程订单总数:"+ordersum);
        System.out.println("同程成功率：  "+(Float.valueOf(ordersuccess)/Float.valueOf(ordersum))*100);
        System.out.println("同程---------------------------------------over------------------------------------------------------------->");
    }

    /**
     * 
     * @author RRRRRR
     * @time 2016年11月2日 下午1:18:42
     * @Description 卡商相关
     * @param time1
     */
    public static void getCardCgl(String time1) {
        //   58
        System.out.println("卡商-----------------"+time1+"-------------start------------------------------------------------------------->");
        String sql = "  select RegCount from TrainAccountRegCountRecord with(nolock) where convert(varchar(10),RegDate,120)='"
                + time1 + "'";
        DataTable dt = DBHelperAccount.GetDataTable(sql);
        List<DataRow> dataRows = dt.GetRow();
        DataRow datarow = dataRows.get(0);
        String RegCount = datarow.GetColumnString("RegCount").toString();//注册数   58
        //18
        String sql1 = "  select AllCardCount,AllSmsCount,NotUsedCount FROM StatisticsSmsCount with(nolock) where convert(varchar(10),Date,120)='"+time1+"'";
        DataTable dt1 = DBHelperAccount2.GetDataTable(sql1);
        List<DataRow> dataRows1 = dt1.GetRow();
        DataRow datarow1 = dataRows1.get(0);
        String AllCardCount = datarow1.GetColumnString("AllCardCount").toString();//总卡数
        String AllSmsCount = datarow1.GetColumnString("AllSmsCount").toString();//发短信数
        String NotUsedCount = datarow1.GetColumnString("NotUsedCount").toString();//
        System.out.println("注册成功数：  "+RegCount);
        System.out.println("注册成功率：  "+(Float.valueOf(RegCount)/Float.valueOf(AllSmsCount))*100);
        System.out.println("走卡率        ：  "+((Float.valueOf(NotUsedCount)+Float.valueOf(AllSmsCount))/Float.valueOf(AllCardCount))*100);
        System.out.println("卡商---------------------------------------over------------------------------------------------------------->");
    }

    /**
     * 
     * @author RRRRRR
     * @time 2016年11月2日 下午1:47:06
     * @Description 机票
     * @param time1
     */
    public static void getGPNum(String time1) {
        System.out.println("机票-----------------"+time1+"-------------start------------------------------------------------------------->");
        String sql = "select gpTicketCount,airTicketCount from OrderStatistics with (nolock) where convert(varchar(10),time,120)='"
                + time1 + "'";
        DataTable dt = DBHelperGP.GetDataTable(sql);
        List<DataRow> dataRows = dt.GetRow();
        DataRow datarow = dataRows.get(0);
        String gpTicketCount = datarow.GetColumnString("gpTicketCount").toString();
        String airTicketCount = datarow.GetColumnString("airTicketCount").toString();
        System.out.println("机票------------------->GP ： "+gpTicketCount+" 国内： "+airTicketCount+" ");
        System.out.println("机票---------------------------------------over------------------------------------------------------------->");
    }

    public static void getRobTicket(String time1) {
        System.out.println("抢票-----------------"+time1+"-------------start------------------------------------------------------------->");
        String sql = "select COUNT(1) as num from TrainOrderBespeak with (nolock) where  convert(varchar(10),CreateTime,120)='"+time1+"'";
        DataTable dt = DBHelperRob.GetDataTable(sql);
        List<DataRow> dataRows = dt.GetRow();
        DataRow datarow = dataRows.get(0);
        String num = datarow.GetColumnString("num").toString();//新增
        String sql1="select COUNT(1) as success from TrainOrderBespeak with (nolock) where  convert(varchar(10),bespeakrealendtime,120)='"+time1+"'  and CreateStatus=2";
        DataTable dt1 = DBHelperRob.GetDataTable(sql1);
        List<DataRow> dataRows1 = dt1.GetRow();
        DataRow datarow1 = dataRows1.get(0);
        String success = datarow1.GetColumnString("success").toString();//成功
        System.out.println("抢票------------------->成功数："+success+" 新增数："+num+" 成功率："+(Float.valueOf(success)/Float.valueOf(num))*100);
        System.out.println("抢票---------------------------------------over------------------------------------------------------------->");
    }
}
