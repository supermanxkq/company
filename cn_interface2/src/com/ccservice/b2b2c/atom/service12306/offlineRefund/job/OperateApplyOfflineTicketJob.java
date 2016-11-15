package com.ccservice.b2b2c.atom.service12306.offlineRefund.job;

import java.util.Map;
import java.util.List;
import org.quartz.Job;
import java.util.Random;
import java.util.HashMap;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.concurrent.ExecutorService;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.util.RefundTicketUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.thread.AliPushRefundPriceThread;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.method.ApplyOfflineTicketGoOnline;

/**
 * 处理接口申请的线下退票
 * @author WH
 * @time 2016年4月1日 上午11:21:36
 * @version 1.0
 * @remark  淘宝规则：如果有回款72小时之内同意退款，如果没有回款72小时之外可以拒绝，但是在96小时之内都必须处理完成，否则会执行强制退款
 */

public class OperateApplyOfflineTicketJob implements Job {

    //线程数量
    private int threadNum = 5;

    //标识开关
    private String openFlag = "ApplyOfflineOpen";

    //默认时间，单位：天
    private int defaultDay = 3;//要与RefuseApplyOfflineTicketJob类保持一致

    //预留时间，单位：分钟
    private int reserveTime = 5;//要与RefuseApplyOfflineTicketJob类保持一致

    //JOB
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (isopen()) {
            try {
                //暂停
                stop();
                //退票
                getRefundTickets();
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("ApplyOfflineTicketJob_Exception", e);
            }
            finally {
                //开启
                start();
            }
        }
    }

    //查询退票
    @SuppressWarnings("rawtypes")
    private void getRefundTickets() {
        //内存数据
        Map<String, String> dataMap = Server.getInstance().getDateHashMap();
        //预留时间
        int refundTicketReserve = Integer.parseInt(getMemoryData(dataMap, "RefundTicketReserve",
                String.valueOf(reserveTime)));
        //处理天数
        int refundTicketDay = Integer.parseInt(getMemoryData(dataMap, "RefundTicketDay", String.valueOf(defaultDay)));
        //处理天数
        refundTicketDay = refundTicketDay < 0 ? -refundTicketDay : refundTicketDay;
        //申请时间
        @SuppressWarnings("unused")
        String refundRequestTime = refundRequestTime(refundTicketDay, refundTicketReserve);
        //退票查询
        String sql = "select p.C_ORDERID, t.C_CHANGEID, t.C_CHANGETYPE, t.C_PRICE,"
                + " t.C_TCNEWPRICE, t.C_REFUNDREQUESTTIME, t.C_STATE12306, t.C_DEPARTTIME,"
                + " t.C_TTCDEPARTTIME from T_TRAINTICKET t with(nolock)"
                + " inner join T_TRAINPASSENGER p with(nolock) on p.ID = t.C_TRAINPID"
                + " where t.C_STATUS = 5 and t.C_ISAPPLYTICKET = 2 and t.C_APPLYTICKETFLAG = 1";// and t.C_REFUNDREQUESTTIME > '" + refundRequestTime + "'"; 
        //退票结果
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        //PRINT
        System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + "=====开始处理接口线下退票=====" + list.size());
        //退票判断
        if (list.size() > 0) {
            operateRefundTickets(list, dataMap);
        }
    }

    //退票处理
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void operateRefundTickets(List list, Map<String, String> dataMap) {
        //排序：先申请的放前
        Collections.sort(list, new Comparator<Map>() {
            public int compare(Map mapA, Map mapB) {
                //订单ID
                long idA = RefundTicketUtil.getLongMapValue(mapA, "C_ORDERID");
                long idB = RefundTicketUtil.getLongMapValue(mapB, "C_ORDERID");
                //格式化
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //申请时间
                String timeA = RefundTicketUtil.getStringMapValue(mapA, "C_REFUNDREQUESTTIME");
                String timeB = RefundTicketUtil.getStringMapValue(mapB, "C_REFUNDREQUESTTIME");
                //比较返回
                try {
                    //时间转化
                    long longA = sdf.parse(timeA).getTime();
                    long longB = sdf.parse(timeB).getTime();
                    //比较返回
                    if (longA == longB) {
                        return idA < idB ? -1 : 1;
                    }
                    else {
                        return longA < longB ? -1 : 1;
                    }
                }
                catch (Exception e) {
                    return idA < idB ? -1 : 1;
                }
            }
        });
        //处理数量
        int operateNum = 0;
        //时间格式化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //进行线上退票
        Map<Long, Boolean> onlineRefundMap = new HashMap<Long, Boolean>();
        //key:订单ID；value:改签ID集合
        Map<Long, List<Long>> orderMap = new HashMap<Long, List<Long>>();
        //退票时间限制
        long minTime = ApplyOfflineTicketGoOnline.RefundTimeLimitIntValue * 60 * 1000;
        //循环
        for (int i = 0; i < list.size(); i++) {
            //MAP
            Map map = (Map) list.get(i);
            //改签ID
            long changeId = 0;
            //订单ID
            long orderId = RefundTicketUtil.getLongMapValue(map, "C_ORDERID");
            //改签类型
            int changeType = RefundTicketUtil.getIntMapValue(map, "C_CHANGETYPE");
            //车票价格
            float oldPrice = RefundTicketUtil.getFloatMapValue(map, "C_PRICE");
            //改签价格
            float newPrice = RefundTicketUtil.getFloatMapValue(map, "C_TCNEWPRICE");
            //状态、发车时间
            int status12306 = RefundTicketUtil.getIntMapValue(map, "C_STATE12306");
            String departTime = RefundTicketUtil.getStringMapValue(map, "C_DEPARTTIME");
            String tcDepartTime = RefundTicketUtil.getStringMapValue(map, "C_TTCDEPARTTIME");
            //线上高改
            if (changeType == 1 && newPrice > oldPrice) {
                changeId = RefundTicketUtil.getLongMapValue(map, "C_CHANGEID");
            }
            //改签集合
            List<Long> changeIdList = orderMap.containsKey(orderId) ? orderMap.get(orderId) : new ArrayList<Long>();
            //添加数据
            if (!changeIdList.contains(changeId)) {
                operateNum++;
                changeIdList.add(changeId);
            }
            //设置数据
            orderMap.put(orderId, changeIdList);
            //未改签或线上改签、状态还能退、不包含
            if ((changeType == 0 || changeType == 1) && !RefundTicketUtil.dontRefundStatus(status12306)
                    && !onlineRefundMap.containsKey(orderId)) {
                //捕捉异常
                try {
                    //发车时间
                    departTime = changeType == 0 ? departTime : tcDepartTime;
                    //开车时间-当前时间在30分钟以上
                    if (sdf.parse(departTime).getTime() - System.currentTimeMillis() > minTime) {
                        onlineRefundMap.put(orderId, true);
                    }
                }
                catch (Exception e) {
                    onlineRefundMap.put(orderId, true);//发生异常，按走退票处理
                }
            }
        }
        //线程数
        int nThreads = Integer.parseInt(getMemoryData(dataMap, "RefundTicketThreadNum", String.valueOf(threadNum)));
        //线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(operateNum < nThreads ? operateNum : nThreads);
        //循环订单
        for (Entry<Long, List<Long>> entry : orderMap.entrySet()) {
            //ID
            long orderId = entry.getKey();
            List<Long> changeIdList = entry.getValue();
            //循环改签
            for (long changeId : changeIdList) {
                //随机数
                int random = new Random().nextInt(900000) + 100000;
                //类型
                String busType = changeId > 0 ? "2" : "1";//1:订单；2:改签
                //ID
                long orderOrChangeId = changeId > 0 ? changeId : orderId;
                //流水号
                String payTradeNos = changeId > 0 ? changeTradeNum(changeId) : orderTradeNum(orderId);
                //线程处理
                threadPool.execute(new AliPushRefundPriceThread(busType, payTradeNos, orderOrChangeId, random, 1,
                        orderId, onlineRefundMap.containsKey(orderId) && onlineRefundMap.get(orderId)));
            }
        }
        //关闭
        threadPool.shutdown();
    }

    /**
     * 订单流水号
     */
    @SuppressWarnings("rawtypes")
    private String orderTradeNum(long orderId) {
        String num = "";
        //SQL
        String sql = "select C_SUPPLYTRADENO from T_TRAINORDER with(nolock) where ID = " + orderId;
        //LIST
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        //MAP
        if (list.size() == 1) {
            Map map = (Map) list.get(0);
            num = RefundTicketUtil.getStringMapValue(map, "C_SUPPLYTRADENO");
        }
        //返回
        return num;
    }

    /**
     * 改签流水号
     */
    @SuppressWarnings("rawtypes")
    private String changeTradeNum(long changeId) {
        String num = "";
        //SQL
        String sql = "select C_SUPPLYTRADENO from T_TRAINORDERCHANGE with(nolock) where ID = " + changeId;
        //LIST
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        //MAP
        if (list.size() == 1) {
            Map map = (Map) list.get(0);
            num = RefundTicketUtil.getStringMapValue(map, "C_SUPPLYTRADENO");
        }
        //返回
        return num;
    }

    /**
     * 申请时间
     * @param day 处理天数
     * @param reserve 预留时间
     * @return 申请时间
     */
    private String refundRequestTime(int day, int reserve) {
        //当前时间 - 处理天数 + 预留时间
        long time = System.currentTimeMillis() - day * 24 * 60 * 60 * 1000 + reserve * 60 * 1000;
        //格式化，返回结果
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(time));
    }

    //开关退票
    private void stop() {
        Server.getInstance().getDateHashMap().put(openFlag, "0");
    }

    //开关退票
    private void start() {
        Server.getInstance().getDateHashMap().put(openFlag, "1");
    }

    //判断开关
    private boolean isopen() {
        //标识
        String flag = Server.getInstance().getDateHashMap().get(openFlag);
        //返回
        return ElongHotelInterfaceUtil.StringIsNull(flag) || "1".equals(flag);
    }

    /**
     * 内存取数据，无的时候设置默认值
     * @param defaultValue 默认值
     */
    private String getMemoryData(Map<String, String> dataMap, String key, String defaultValue) {
        //结果
        String result = defaultValue;
        //包含
        if (dataMap.containsKey(key)) {
            result = dataMap.get(key);
        }
        else {
            dataMap.put(key, defaultValue);
        }
        //返回
        return result;
    }

}