package com.ccservice.b2b2c.atom.taobao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.taobao.thread.MyThreadTaobaoCallBackAll;
import com.ccservice.b2b2c.atom.taobao.thread.MyThreadTimeOutRefund;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TaobaoRefundMethod {

    static TaobaoRefundMethod taobaoRefundMethod;

    public static Object taobaoRefundSyn = new Object();

    public static Object taobaoRefundCallbackSyn = new Object();

    private TaobaoRefundMethod() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Thread t1 = new MyThreadTaobaoCallBackAll();
        pool.execute(t1);
        pool.shutdown();
    }

    public static TaobaoRefundMethod getInstance() {
        if (taobaoRefundMethod == null) {
            taobaoRefundMethod = new TaobaoRefundMethod();
        }
        return taobaoRefundMethod;
    }

    public boolean delete(String RefundOrderNo, String RefundPrice, int AgreeRefund, int RefuseReason, int RefundType,
            String TaoBaoTicketNo) {
        boolean isSuccess = false;
        String sql = "UPDATE TrainOrderRefund SET RefundType=" + RefundType + ",RefundPrice=" + RefundPrice
                + ",AgreeRefund=" + AgreeRefund + ",RefuseReason=" + RefuseReason + ",TaoBaoTicketNo='"
                + TaoBaoTicketNo + "' WHERE RefundOrderNo='" + RefundOrderNo + "' AND AgreeRefund=0";
        WriteLog.write("TaobaoRefundMethod_delete", RefundOrderNo + "--->" + sql);
        //更新结果
        try {
            int updateResult = 0;
            synchronized (taobaoRefundSyn) {
                updateResult = Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            }
            WriteLog.write("TaobaoRefundMethod_delete", RefundOrderNo + "--->" + updateResult);
            if (updateResult == 1) {
                isSuccess = true;
            }
        }
        catch (Exception e) {
            WriteLog.write("ERROR_TaobaoRefundMethod_delete", RefundOrderNo);
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundMethod_delete", e);
        }
        return isSuccess;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteAll() {
        String sql = "  [sp_TrainOrderRefund_CallBackAll] ";
        List<Map> list = new ArrayList<Map>();
        try {
            synchronized (taobaoRefundSyn) {
                list = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundMethod_deleteAll", e);
        }
        WriteLog.write("TaobaoRefundMethod_deleteAll", "超时回调:" + list.size());
        for (int i = 0; i < list.size(); i++) {
            try {
                Map map = (Map) list.get(i);
                try {
                    ExecutorService pool = Executors.newFixedThreadPool(1);
                    Thread t1 = new MyThreadTimeOutRefund(map);
                    pool.execute(t1);
                    pool.shutdown();
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException("ERROR_TaobaoRefundMethod_deleteAll", e);
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("ERROR_TaobaoRefundMethod_deleteAll", e);
            }
        }
    }

    public boolean callbackResultIntoDB(String RefundOrderNo, boolean isCallback) {
        int callback = isCallback ? 1 : 2;
        boolean isSuccess = false;
        String sql = "UPDATE TrainOrderRefund SET CallBack=" + callback + ",CallBackNum +=1 WHERE RefundOrderNo='" + RefundOrderNo
                + "' AND CallBack in (0,2)";
        WriteLog.write("TaobaoRefundMethod_callbackResultIntoDB", RefundOrderNo + "--->" + sql);
        //更新结果
        try {
            int updateResult = 0;
            synchronized (taobaoRefundCallbackSyn) {
                updateResult = Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
            }
            WriteLog.write("TaobaoRefundMethod_callbackResultIntoDB", RefundOrderNo + "--->" + updateResult);
            if (updateResult == 1) {
                isSuccess = true;
            }
        }
        catch (Exception e) {
            WriteLog.write("ERROR_TaobaoRefundMethod_callbackResultIntoDB", RefundOrderNo);
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundMethod_callbackResultIntoDB", e);
        }
        return isSuccess;
    }
}
