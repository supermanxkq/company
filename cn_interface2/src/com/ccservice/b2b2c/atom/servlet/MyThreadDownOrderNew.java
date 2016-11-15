package com.ccservice.b2b2c.atom.servlet;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;

/**
 * 
 * 下单线程
 *
 */
public class MyThreadDownOrderNew extends Thread {

    static Logger log = Logger.getLogger(MyThreadDownOrderNew.class);

    private String downorderordercount;

    private String downorderthreadcount;

    public MyThreadDownOrderNew() {
    }

    /**
     * 发送请求
     */
    public void run() {
        while (true) {
            List<String> agentidList = null;
            try {
                agentidList = getAllAgentid();
            }
            catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.out.println("TrainDownOrderJob:收单表:几家?:" + agentidList.size() + ":" + TimeUtil.gettodaydate(5));
            String str = "";
            String pingtai = PropertyUtil.getValue("pingtai", "train.DownOrder.properties");// 哪个平台 kt[空铁] tc[同程]
            for (int i = 0; i < agentidList.size(); i++) {
                boolean IsShoudan = getISShoudanByagentid(agentidList.get(i), pingtai);
                if (IsShoudan) {
                    str += agentidList.get(i) + ",";
                }
            }
            if (str.equals("")) {

            }
            else {
                str = str.substring(0, str.length() - 1);
                System.out.println(str + ":正在收单不放单:" + TimeUtil.gettodaydate(5));
            }
            long startTime = System.currentTimeMillis();
            execute_2016(str);
            long runTime = System.currentTimeMillis() - startTime;
            long sleep = 10000;
            if (runTime >= sleep) {
                sleep = 0;
            }
            else {
                sleep = sleep - runTime;
            }
            try {
                Thread.sleep(sleep); // 睡眠时间自动
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void execute_2016(String agentId) {
        if (getDownOrderTimeControl()) {
            System.out.println("TrainDownOrderJobNew4.6:正在放单:" + TimeUtil.gettodaydate(5));
            downorder_2016(agentId);
        }
        else {
            System.out.println("时间不在范围内");
        }
    }

    public void downorder_2016(String agentId) {
        downorderordercount = getDownOrderOrderCount();// 下单取订单的数量
        downorderthreadcount = getDownorderthreadcount();// 下单启动的线程数量
        System.out.println("下单取订单的数量:" + downorderordercount + "下单启动的线程数量:" + downorderthreadcount);
        try {
            List list = getTrainOrderMsbList_2016(downorderordercount, agentId);
            if (list != null && list.size() > 0) {
                ExecutorService pool = Executors.newFixedThreadPool(Integer.valueOf(downorderthreadcount).intValue());
                for (int i = 0; i < list.size(); i++) {
                    try {
                        Map map = (Map) list.get(i);
                        long sid = Long.valueOf(map.get("ID").toString());// 任务标识收单表id
                        long orderid = Long.valueOf(map.get("C_ORDERID").toString());// 任务标识订单id
                        int orderstatus = Integer.valueOf(map.get("C_ORDERSTATUS").toString());// 任务标识订单原状态
                        Thread thr = new MyThreadDownOrderNewReal(sid, orderid, orderstatus);
                        pool.execute(thr);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                pool.shutdown();
            }
        }
        catch (NumberFormatException e) {
            WriteLog.write("放单t4.6NumberFormatException", e + "");
            e.printStackTrace();
        }
        catch (Exception e) {
            WriteLog.write("放单t4.6Exception", e + "");
            e.printStackTrace();
        }
    }

    // public void DownOrderNew(long sid, long orderid, int orderstatus) {
    // WriteLog.write("t4.6收单下单", sid + ":" + sid + "订单id：" + orderid);
    // Trainorder trainorder = Server.getInstance().getTrainService()
    // .findTrainorder(orderid);
    // trainorder.setCreatetime(TimeUtil.getCurrentTime());
    // trainorder.setOrderstatus(orderstatus);
    // Server.getInstance().getTrainService().updateTrainorder(trainorder);
    // new TongchengSupplyMethod().activeMQroordering(trainorder.getId());//
    // 放单发mq
    // String sql = "delete  from T_TRAINORDERACQUIRING  where ID = " + sid;
    // Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
    // }

    public List getTrainOrderMsbList_2016(String downorderordercount, String agentId) {
        String sql = "";
        List countlist = null;
        int sum = Integer.parseInt(downorderordercount);
        if (agentId.equals("")) {
            sql = " exec [sp_T_TRAINORDERACQUIRING_top3] @number = " + sum;
            List starttimelist = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            countlist = starttimelist;
            if (starttimelist.size() < sum) {
                int ticketSum = sum - starttimelist.size();
                sql = " exec [sp_T_TRAINORDERACQUIRING_top4] @number = " + ticketSum;
                List ticketList = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                countlist = ticketList;
                if (ticketList.size() < ticketSum) {
                    int countsum = ticketSum - ticketList.size();
                    sql = " exec [sp_T_TRAINORDERACQUIRING_top] @number = " + countsum;
                    List timelist = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    countlist = timelist;
                }
            }
        }
        else {
//            sql = "select   *  into #temp from  (       SELECT          *,ROW_NUMBER() over(order by ROW1 asc) as rr    FROM (select *,C_DEPARTTIME  AS ROW1 from T_TRAINORDERACQUIRING  where C_STATUS=0 and C_AGENTID not in (" + agentId + ")) AS T ) as t2 where   rr<=" + sum + ";    update T_TRAINORDERACQUIRING set C_STATUS=1  where ID in (select ID from #temp);select * from  #temp;drop table #temp";
                  sql = "exec [sp_T_TRAINORDERACQUIRING_top2] @number = " + sum + ",@agentid='" + agentId+"'";
                  WriteLog.write("放单t4.6Exception", sql + "");
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            countlist = list;
        }

        return countlist;
    }

    /**
     * 根据agentid和平台判断这个agentid是否正在收单 收单返回true,不收单【正常接收订单】返回false
     * 
     * @param agentId
     * @return
     * @time 2015年12月4日 下午4:29:53
     * @author chendong
     * @param pingtai
     *            #哪个平台 kt[空铁] tc[同程]
     */
    private boolean getISShoudanByagentid(String agentId, String pingtai) {
        boolean IsShoudan = false;
        // 系统标识 1 同程 2 空铁
        String sysflag = "1";
        if ("kt".equals(pingtai)) {
            sysflag = "2";
        }
        String quiringOrderkey = "quiringOrderkey_" + sysflag + "_" + agentId;
        Object o_acquiringflagMsg = MemCached.getInstance().get(quiringOrderkey);
        if ("success".equals(o_acquiringflagMsg.toString())) {
            IsShoudan = true;
        }
        return IsShoudan;
    }

    private List<String> getAllAgentid() {
        List<String> agentidList = new ArrayList<String>();
        String sql = "select C_AGENTID from T_TRAINORDERACQUIRING with(nolock) group by C_AGENTID";
        List list = null;
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            if (map.get("C_AGENTID") != null) {
                agentidList.add(map.get("C_AGENTID").toString());
            }
        }
        return agentidList;
    }

    /**
     * 下单启动的线程数量
     * 
     * @return
     * @time 2015年12月3日 下午6:08:45
     * @author chendong
     */
    private String getDownorderthreadcount() {
        String downorderthreadcount = getSysconfigStringbydb("downorderthreadcount");
        if ("-1".equals(downorderthreadcount)) {
            downorderthreadcount = "60";
        }
        return downorderthreadcount;
    }

    /**
     * 根据sysconfig的name获得value 实时 如果是判断的必须调用实时接口
     * 
     * @param name
     * @return
     */
    public String getSysconfigStringbydb(String name) {
        String result = "-1";
        List<Sysconfig> sysoconfigs = Server.getInstance().getSystemService()
                .findAllSysconfig("WHERE C_NAME='" + name + "'", "", -1, 0);
        if (sysoconfigs.size() > 0) {
            result = sysoconfigs.get(0).getValue();
        }
        return result;
    }

    /**
     * 收单下单时间限制
     * 
     * @return
     */
    public boolean getDownOrderTimeControl() {
        // 06:00-23:00
        String timestr = PropertyUtil.getValue("shoudantimecontrol", "train.DownOrder.properties");
        return get12306WorkTime(timestr);
    }

    public static boolean get12306WorkTime(String timestr) {
        System.out.println("当前允许时间：" + timestr + "======" + TimeUtil.gettodaydate(4));
        String timebefore = timestr.split("-")[0];
        String timeend = timestr.split("-")[1];
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        try {
            Date dateBefor = df.parse(timebefore);
            Date dateAfter = df.parse(timeend);
            Date time = df.parse(df.format(new Date()));
            if (time.after(dateBefor) && time.before(dateAfter)) {
                return true;
            }
        }
        catch (ParseException e) {
            WriteLog.write("getNowTime", e + "");
        }
        return false;// 现在24小时,以后有需要再改为FALSE
    }

    /**
     * 下单取订单的数量
     * 
     * @return
     * @time 2015年12月4日 下午3:51:29
     * @author chendong
     */
    private String getDownOrderOrderCount() {
        String downorderordercount = getSysconfigStringbydb("downorderordercount");// 下单取订单的数量
        if ("-1".equals(downorderordercount)) {
            downorderordercount = "50";
        }
        return downorderordercount;
    }

    // private String getIdStr(List list) {
    // String idstr = "";
    // for (int i = 0; i < list.size(); i++) {
    // try {
    // Map map = (Map) list.get(i);
    // long sid = Long.valueOf(map.get("ID").toString());// 任务标识
    // if (i == list.size() - 1) {
    // idstr += sid;
    // } else {
    // idstr += sid + ",";
    // }
    // } catch (Exception e) {
    // }
    // }
    // return idstr;
    // }

    public static void main(String[] args) {
        // 31519266
        //		Trainorder trainorder = Server.getInstance().getTrainService()
        //				.findTrainorder(47150l);
        //		trainorder.setCreatetime(TimeUtil.getCurrentTime());
        //		trainorder.setOrderstatus(3);
        //		Server.getInstance().getTrainService().updateTrainorder(trainorder);
        //		String str = "11,22,22,33,";
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {

        }
        long runTime = System.currentTimeMillis() - startTime;
        System.out.println(runTime);
        //		System.out.println(trainorder.getCreatetime());
        //		System.out.println("截取最后一个字符串生成的新字符串为: "
        //				+ str.substring(0, str.length() - 1));
    }
}
