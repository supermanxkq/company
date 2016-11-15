package com.ccservice.b2b2c.atom.service12306.offlineRefund.job;

import java.util.Map;
import java.util.List;
import org.quartz.Job;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.concurrent.ExecutorService;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.util.RefundTicketUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.thread.RefuseApplyOfflineTicketThread;

/**
 * 拒绝接口申请的线下退票
 * @author WH
 * @time 2016年4月5日 下午4:26:55
 * @version 1.0
 */

public class RefuseApplyOfflineTicketJob extends TongchengSupplyMethod implements Job {

    //默认时间，单位：天
    private int defaultDay = 3;//要与OperateApplyOfflineTicketJob类保持一致

    //预留时间，单位：分钟
    private int reserveTime = 5;//要与OperateApplyOfflineTicketJob类保持一致

    //JOB
    public void execute(JobExecutionContext context) throws JobExecutionException {
        getRefundTickets();
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
        String refundRequestTime = refundRequestTime(refundTicketDay, refundTicketReserve);
        //退票查询
        String sql = "select p.C_ORDERID, t.ID, t.C_INTERFACETYPE from T_TRAINTICKET t with(nolock)"
                + " inner join T_TRAINPASSENGER p with(nolock) on p.ID = t.C_TRAINPID"
                + " where t.C_STATUS = 5 and t.C_REFUNDREQUESTTIME <= '" + refundRequestTime
                + "' and t.C_ISAPPLYTICKET = 2 and t.C_APPLYTICKETFLAG in (1, -1)";
        //退票结果
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        //PRINT
        System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + "=====开始---拒绝---接口线下退票=====" + list.size());
        //拒绝退票
        if (list.size() > 0) {
            //回调地址
            String trainorderNonRefundable = getSysconfigString("trainorderNonRefundable");
            //地址为空
            if (ElongHotelInterfaceUtil.StringIsNull(trainorderNonRefundable)) {
                return;
            }
            //线程池
            ExecutorService threadPool = Executors.newFixedThreadPool(1);
            //循环处理
            for (int i = 0; i < list.size(); i++) {
                //MAP
                Map map = (Map) list.get(i);
                //车票ID
                long ticketId = RefundTicketUtil.getLongMapValue(map, "ID");
                //订单ID
                long orderId = RefundTicketUtil.getLongMapValue(map, "C_ORDERID");
                //接口类型
                int interfaceType = RefundTicketUtil.getIntMapValue(map, "C_INTERFACETYPE");
                //线程处理
                threadPool.execute(new RefuseApplyOfflineTicketThread(orderId, ticketId, interfaceType,
                        trainorderNonRefundable));
            }
            //关闭
            threadPool.shutdown();
        }
    }

    /**
     * 申请时间
     * @param day 处理天数
     * @param reserve 预留时间
     * @return 申请时间
     */
    private String refundRequestTime(int day, int reserve) {
        //当前时间 - 处理天数 - 预留时间
        long time = System.currentTimeMillis() - day * 24 * 60 * 60 * 1000 - reserve * 60 * 1000;
        //格式化，返回结果
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(time));
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