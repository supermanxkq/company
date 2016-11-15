package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

/**
 * Servlet implementation class TuNiuChuPiaoServlet
 */
@SuppressWarnings("serial")
public class TuNiuChuPiaoServlet extends HttpServlet {
    
    String res = "false";

    private final String logname = "tuniu_3_3_3_确认出票接口";

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        int r1 = new Random().nextInt(10000);
        JSONObject retobj = new JSONObject();
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        final AsyncContext ctx = request.startAsync();
        BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getRequest().getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String reqString = buf.toString();
        WriteLog.write(logname, r1 + "--->" + reqString);
        try {
            if (reqString == null || "".equals(reqString)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //请求json
            JSONObject reqjso = JSONObject.parseObject(reqString);
            String account = tuNiuServletUtil.getParamByJsonStr("account", reqjso);//账号
            String sign = tuNiuServletUtil.getParamByJsonStr("sign", reqjso);//
            String timestamp = tuNiuServletUtil.getParamByJsonStr("timestamp", reqjso);//请求时间
            String data = tuNiuServletUtil.getParamByJsonStr("data", reqjso);
            WriteLog.write(logname, r1 + "--->account:" + account + "--->sign:" + sign + "--->timestamp:" + timestamp
                    + "--->data:" + data);
            if ("".equals(account) || "".equals(sign) || "".equals(timestamp) || "".equals(data)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            TuNiuTraintrainAccountGrab tuNiuTraintrainAccountGrab = new TuNiuTraintrainAccountGrab();
            //获取账户信息
            Map map = tuNiuServletUtil.getInterfaceAccount(account);
            String agentid = tuNiuServletUtil.getParamByMapStr("C_AGENTID", map);
            String key = tuNiuServletUtil.getParamByMapStr("C_KEY", map);
            String password = tuNiuServletUtil.getParamByMapStr("C_ARG2", map);
            int interfacetype = Integer.parseInt(tuNiuServletUtil.getParamByMapStr("C_INTERFACETYPE", map));
            WriteLog.write(logname, r1 + "--->agentid:" + agentid + "--->key:" + key + "--->password:" + password);
            if ("".equals(agentid) || "".equals(key) || "".equals(password)) {
                tuNiuServletUtil.respByUserNotExists(ctx, logname);
                return;
            }
            int status = getInterfaceAccountStatus(Long.parseLong(agentid));
            if(status == 0){
                //判断余额状态     0继续
            }else{
                tuNiuServletUtil.respByNoMoney(ctx, logname);
                return;
            }
            JSONObject json = JSONObject.parseObject(data);
            String vendorOrderId = json.containsKey("vendorOrderId") ? json.getString("vendorOrderId") : "";//合作伙伴方订单号
            String orderId = json.containsKey("orderId") ? json.getString("orderId") : "";//途牛订单号
            String callBackUrl = json.containsKey("callBackUrl") ? json.getString("callBackUrl") : "";
            WriteLog.write(logname, r1 + "--->vendorOrderId:" + vendorOrderId + "--->orderId:" + orderId);
            if ("".equals(vendorOrderId) || "".equals(orderId) || "".equals(callBackUrl)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            boolean timeout = false;
            //            List list1 = Server.getInstance().getSystemService()
            //                    .findMapResultByProcedure("[sp_TongChengCancel_select] @InterfaceNumber=" + orderId);
            //            WriteLog.write("出票五", list1.toString());
            //            if (list1.size() > 0 && list1.get(0) != null && !"".equals(list1.get(0))) {
            //                return;
            //            }
            Trainform trainform = new Trainform();
            trainform.setQunarordernumber(orderId);
            trainform.setOrdernumber(vendorOrderId);
            List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
            WriteLog.write(logname, r1 + "--->orderId:" + orderId + "--->orders:" + orders.size());
            //订单不存在
            if (orders == null || orders.size() == 0) {
                tuNiuServletUtil.respByUnknownError(ctx, logname);
                return;
            }
            Trainorder order = new Trainorder();
            if (orders.size() == 1) {
                order = orders.get(0);
            }
            else if (orders.size() == 2) {
                if (orders.get(0).getOrderstatus() == Trainorder.WAITPAY) {
                    order = orders.get(0);
                }
                else {
                    order = orders.get(1);
                }
            }
            //加载其他字段、乘客
            order = Server.getInstance().getTrainService().findTrainorder(order.getId());
            String changeserial = "";
            order.setPaymethod(4);
            //更新订单
            order.setOrderstatus(Trainorder.WAITISSUE);
            order.setTradeno(changeserial);
            Server.getInstance().getTrainService().updateTrainorder(order);
            //更新车票
            for (Trainpassenger trainpassenger : order.getPassengers()) {
                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                    trainticket.setStatus(Trainticket.WAITISSUE);
                    Server.getInstance().getTrainService().updateTrainticket(trainticket);
                }
            }
            try {//日志
                Trainorderrc rz = new Trainorderrc();
                rz.setYwtype(1);
                rz.setCreateuser("系统接口");
                rz.setOrderid(order.getId());
                rz.setContent("接口确认出票[" + vendorOrderId + "]，等待支付12306.");
                rz.setStatus(Trainorder.WAITPAY);

                Server.getInstance().getTrainService().createTrainorderrc(rz);
            }
            catch (Exception e) {
            }
            //返回
            JSONObject dataJsonObject = new JSONObject();
            dataJsonObject.put("vendorOrderId", vendorOrderId);
            dataJsonObject.put("orderId", orderId);
            tuNiuServletUtil.respBySuccess(ctx, logname, dataJsonObject);
            WriteLog.write(logname, r1 + "--->orderId:" + orderId + "--->dataJsonObject:" + dataJsonObject.toJSONString());
            return;

        }

        catch (Exception e) {
            tuNiuServletUtil.respByUnknownError(ctx, logname);
        }
    }

    /**
     * 书写操作记录 
     * @param orderid
     * @param i_koukuantype
     * @time 2015年3月26日 下午4:58:09
     * @author fiend
     */
    public void train_write(long orderid, int i_koukuantype) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(orderid);
        rc.setContent("扣款失败");
        if (1 == i_koukuantype) {
            rc.setContent("扣款成功");
        }
        if (3 == i_koukuantype) {
            rc.setContent("扣款异常，请客服操作拒单退款");
        }
        rc.setStatus(2);
        rc.setCreateuser("自动扣款");//"12306"
        rc.setTicketid(0);
        rc.setYwtype(1);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    /**
     * 修改订单和书写操作记录
     * @param orderid
     * @param i_koukuantype
     * @time 2015年3月26日 下午4:34:57
     * @author fiend
     */
    public void train_change_write(long orderid, int i_koukuantype) {
        try {
            if (1 == i_koukuantype) {
                //扣款成功
                Server.getInstance()
                        .getSystemService()
                        .findMapResultBySql("UPDATE T_TRAINORDER SET C_ISPLACEING=" + 1 + " WHERE ID =" + orderid, null);
            }
            if (2 == i_koukuantype) {
                //扣款失败
                Server.getInstance()
                        .getSystemService()
                        .findMapResultBySql("UPDATE T_TRAINORDER SET C_ISPLACEING=" + 4 + " WHERE ID =" + orderid, null);
            }
            if (3 == i_koukuantype) {
                //扣款异常
                Server.getInstance()
                        .getSystemService()
                        .findMapResultBySql("UPDATE T_TRAINORDER SET C_ISPLACEING=" + 6 + " WHERE ID =" + orderid, null);
            }
            train_write(orderid, i_koukuantype);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 余额是否可以拦截
     * 
     * @param agentid
     * @return
     * @time 2016年9月6日 下午6:04:47
     * @author fiend
     */
    private int getInterfaceAccountStatus(long agentid) {
        int status = 0;
        try {
            String sql = "select top 1 * from T_INTERFACEACCOUNT WITH (NOLOCK) where C_agentid=" + agentid;
            DataTable datatable = DBHelper.GetDataTable(sql);
            List<DataRow> dataRows = datatable.GetRow();

            for (DataRow datacolumn : dataRows) {
                status = datacolumn.GetColumnInt("C_STATUS");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}
