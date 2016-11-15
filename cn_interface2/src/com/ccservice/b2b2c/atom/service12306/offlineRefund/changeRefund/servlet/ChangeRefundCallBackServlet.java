package com.ccservice.b2b2c.atom.service12306.offlineRefund.changeRefund.servlet;

import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.ExecutorService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.util.OnlineRefundUtil;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.thread.RefundOverThread;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.changeRefund.util.ChangeRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.changeRefund.method.ChangeRefundMethod;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.changeRefund.thread.ChangeRefundThread;

/**
 * 改签退回调
 */

@SuppressWarnings("serial")
public class ChangeRefundCallBackServlet extends HttpServlet {

    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //编码
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html; charset=UTF-8");
        //结果
        String result = "";
        //随机数
        int random = new Random().nextInt(900000) + 100000;
        //捕捉异常
        try {
            //处理标识
            boolean success = false;
            //请求参数
            String jsonStr = req.getParameter("jsonStr");
            //回调类型>>1：改签占座；2：改签确认
            String callBackType = req.getParameter("changeType");
            //解析请求
            JSONObject json = ElongHotelInterfaceUtil.StringIsNull(jsonStr) ? new JSONObject() : JSONObject
                    .parseObject(jsonStr);
            //改签占座
            if ("1".equals(callBackType)) {
                success = operate(callBackType, json, random);
            }
            //改签确认
            else if ("2".equals(callBackType)) {
                success = operate(callBackType, json, random);
            }
            else {
                throw new Exception("回调类型[" + callBackType + "]错误！");
            }
            //结果赋值
            result = success ? "success" : "false";
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("火车票改签退回调_Exception", e, String.valueOf(random));
        }
        //打印
        PrintWriter out = null;
        try {
            //GET
            out = res.getWriter();
            //输出
            out.print(result);
        }
        catch (IOException e) {

        }
        finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean operate(String callBackType, JSONObject json, int random) throws Exception {
        //结果
        boolean result = false;
        //接口单号
        String orderid = json.getString("orderid");
        //改签ID
        long changeId = json.getLongValue("changeId");
        //订单Id
        long trainOrderId = json.getLongValue("trainOrderId");
        //系统单号
        String transactionid = json.getString("transactionid");
        //校验数据
        if (changeId > 0 && trainOrderId > 0 && !ElongHotelInterfaceUtil.StringIsNull(orderid)
                && !ElongHotelInterfaceUtil.StringIsNull(transactionid)) {
            //状态
            int rightStatus = 0;
            List<Long> ticketIdList = new ArrayList<Long>();
            //SQL
            String sql = "select ID, C_STATUS from T_TRAINTICKET with(nolock) where C_CHANGEID = " + changeId;
            //查询车票
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            //查询失败
            if (list == null || list.size() <= 0) {
                throw new Exception("查询改签[ChangeId:" + changeId + "]车票失败！");
            }
            //判断状态
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                long ticketId = Long.parseLong(map.get("ID").toString());
                int ticketStatus = Integer.parseInt(map.get("C_STATUS").toString());
                //退票处理中
                if (ticketStatus == Trainticket.REFUNDROCESSING) {
                    rightStatus++;
                    ticketIdList.add(ticketId);
                }
                //同程退票走MQ，防止人为先处理了退票
                else if (OnlineRefundUtil.istc()) {
                    //线程
                    ExecutorService pool = Executors.newSingleThreadExecutor();
                    //执行
                    pool.execute(new RefundOverThread(trainOrderId, ticketId));
                    //关闭
                    pool.shutdown();
                }
            }
            //状态全正确
            if (rightStatus > 0 && rightStatus == list.size()) {
                //确认请求必须在最后>>占座成功、发确认请求、确认改签成功
                if ("1".equals(callBackType) && json.getBooleanValue("success")
                        && new ChangeRefundMethod().confirmChange(transactionid, orderid, changeId)) {
                    result = true;
                }
                //退票逻辑处理，改签添加内存成功
                else if (ChangeRefundUtil.changeRefundAdd(changeId)) {
                    try {
                        //0:查询失败；1:已回调；2:未回调
                        int check = hadRefund(changeId);
                        //已回调走退票
                        if (check == 1) {
                            result = true;
                        }
                        //未走退票
                        else if (check == 2 && updateRefund(changeId)) {
                            //起线程退票
                            ExecutorService pool = Executors.newFixedThreadPool(1);
                            pool.execute(new ChangeRefundThread(trainOrderId, ticketIdList));
                            pool.shutdown();
                            //赋值给结果
                            result = true;
                        }
                    }
                    catch (Exception e) {
                        ExceptionUtil.writelogByException("火车票改签退回调_Exception", e, String.valueOf(changeId));
                    }
                    //内存移除
                    finally {
                        ChangeRefundUtil.changeRefundRemove(changeId);
                    }
                }
            }
        }
        //记录日志
        WriteLog.write("火车票改签退回调", random + "-->" + result + "-->" + json);
        //返回结果
        return result;
    }

    /**
     * 判断是否已经走了退票
     * @return 0:查询改签失败；1:已回调走退票；2:未回调走退票
     */
    @SuppressWarnings("rawtypes")
    private int hadRefund(long changeId) {
        //校验
        int check = 0;
        //捕捉
        try {
            //SQL
            String sql = "select ISNULL(C_CHANGEREFUNDISCALLBACK, 0) FLAG "
                    + "from T_TRAINORDERCHANGE with(nolock) where ID = " + changeId;
            //查询改签
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            //判断状态
            if (list.size() == 1) {
                Map map = (Map) list.get(0);
                //标识>>1:已回调走退票
                check = "1".equals(map.get("FLAG").toString()) ? 1 : 2;
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("火车票改签退回调_Exception", e, String.valueOf(changeId));
        }
        //返回
        return check;
    }

    /**
     * 更新改签
     */
    private boolean updateRefund(long changeId) {
        //SQL
        String sql = "update T_TRAINORDERCHANGE set C_CHANGEREFUNDISCALLBACK = 1 where ID = " + changeId;
        //更新
        return Server.getInstance().getSystemService().excuteAdvertisementBySql(sql) == 1;
    }
}