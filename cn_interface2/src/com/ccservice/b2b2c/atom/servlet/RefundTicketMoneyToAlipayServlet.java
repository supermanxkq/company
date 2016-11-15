package com.ccservice.b2b2c.atom.servlet;

/**
 * 退票支付宝退款
 * ztj
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.pay.AlipayrefundPro;
import com.ccservice.b2b2c.atom.refund.helper.Refundinfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.elong.inter.PropertyUtil;

@SuppressWarnings("serial")
public class RefundTicketMoneyToAlipayServlet extends HttpServlet {
    public static void main(String[] args) {

    }

    /**
     * 退票退款状态  0：等待退款   1：退款中  2：退款成功 3：退款失败
     */
    private String returnMoneyState;

    /**
     * 退款结果回调地址
     */
    private String notifyurl = PropertyUtil.getValue("returnTicketnotifyurl", "Train.properties");

    @Override
    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String result = "";
        PrintWriter out = null;
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        int r1 = new Random().nextInt(10000);
        //返回信息
        String trainorderid = "";
        try {
            out = res.getWriter();
            String param = req.getParameter("data");

            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                result = "false";
            }
            else {
                WriteLog.write("天衢114退票支付宝退款", r1 + "param:" + param);
                JSONObject json = JSONObject.parseObject(param);
                //退票回调通知类型  0：表示线下退票退款； 1：表示线上退票退款；2：线下改签退款；3：线上改签退款
                String returntype = json.getString("returntype");
                //同程订单号 接口订单号
                String apiorderid = json.getString("apiorderid");
                //                String sign = json.getString("sign");
                //火车票取票单号
                trainorderid = json.getString("trainorderid");
                //（唯一）退票回调特征值(1.当回调内容是客人在线申请退票的退款，该值为在调用退票请求API时，由同程传入；
                //                2.当回调内容是客人在线下车站退票的退款，该值由供应商分配。)
                //                String reqtoken = json.getString("reqtoken");
                String returntickets = json.getString("returntickets");
                //退款的票信息
                JSONArray returnticketArr = JSONArray.parseArray(returntickets);
                JSONObject returnticketJOSN = returnticketArr.getJSONObject(0);
                String ticket_no = returnticketJOSN.getString("ticket_no");
                // 退票状态 true:表示成功  false:表示退票失败  
                String returnstate = json.getString("returnstate");
                //退款金额（成功需有值） 当为线上退票时，此值为退款总额
                String returnmoney = json.getString("returnmoney");
                //退票时间
                //                String timestamp = json.getString("timestamp");
                // 退票后消息描述（当returnstate=false时，需显示退票失败原因等）

                long l1 = System.currentTimeMillis();
                l1 = l1 / 1000;
                String timereturn = l1 + "";//退款时间
                String tradeno = "0";//交易号
                String orderid = "0";//订单id
                //查看tradeno
                String selectordersql = "select * from t_trainorder where c_ordernumber='" + trainorderid
                        + "'and c_qunarordernumber='" + apiorderid + "'";
                List ordelist = Server.getInstance().getSystemService().findMapResultBySql(selectordersql, null);

                if (ordelist.size() > 0) {
                    Map maps = (Map) ordelist.get(0);
                    tradeno = maps.get("C_TRADENO").toString();
                    orderid = maps.get("ID").toString();
                }
                if ("true".equals(returnstate) && ("0".equals(returntype) || "1".equals(returntype))) {
                    String sql = "select * from returnticketmoneytoalipay with(nolock) where trainorderid='"
                            + trainorderid + "' and apiorderid='" + apiorderid + "'";
                    List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    if (list.size() <= 0) {
                        String insertsql = "insert into returnticketmoneytoalipay(ReturnType,TrainorderId,returnmoneystate,returnmoney,returntime,apiorderId,TicketNo) "
                                + "values("
                                + returntype
                                + ",'"
                                + trainorderid
                                + "',1,"
                                + returnmoney
                                + ","
                                + timereturn + ",'" + apiorderid + "','" + ticket_no + "')";
                        Server.getInstance().getSystemService().findMapResultBySql(insertsql, null);

                        String selectidsql = "select * from returnticketmoneytoalipay with(nolock) where trainorderid='"
                                + trainorderid + "' and apiorderid='" + apiorderid + "'";
                        List lists = Server.getInstance().getSystemService().findMapResultBySql(selectidsql, null);
                        String id = "0";
                        if (lists.size() > 0) {
                            Map maps = (Map) lists.get(0);
                            id = maps.get("ID").toString();
                        }
                        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
                        Refundinfo refundinfo = new Refundinfo();
                        refundinfo.setRefundprice(Float.parseFloat(returnmoney));
                        refundinfo.setTradeno(tradeno);
                        refundinfos.add(refundinfo);
                        //申请退款
                        createTrainorderrc(1, Long.parseLong(orderid), "申请退款中，退款金额：" + returnmoney, "系统自动申请", 3, 0);
                        String applyret = applyRefund(76, notifyurl, Long.parseLong(id), apiorderid, refundinfos, 3,
                                "TrainTickRefundTianquHandle", r1, trainorderid);
                        result = applyret;
                        if ("success".equals(applyret)) {
                            createTrainorderrc(1, Long.parseLong(orderid), "申请退款成功，退款金额：" + returnmoney, "系统自动申请", 3, 0);
                        }
                        else {
                            createTrainorderrc(1, Long.parseLong(orderid), "申请退款失败，退款金额：" + returnmoney, "系统自动申请", 3, 0);
                        }
                        return;
                    }
                    else {
                        Map maps = (Map) list.get(0);
                        returnMoneyState = maps.get("ReturnMoneyState").toString();
                        String id = maps.get("ID").toString();
                        //退款中
                        if ("1".equals(returnMoneyState)) {
                            createTrainorderrc(1, Long.parseLong(orderid), "申请退款中，退款金额：" + returnmoney, "系统", 3, 0);
                            result = "false";
                            return;
                        }
                        //退款成功
                        else if ("2".equals(returnMoneyState)) {
                            createTrainorderrc(1, Long.parseLong(orderid), "退款成功，退款金额：" + returnmoney, "系统", 3, 0);
                            result = "success";
                            return;
                        }
                        //退款失败
                        else if ("3".equals(returnMoneyState)) {
                            createTrainorderrc(1, Long.parseLong(orderid), "申请退款失败，再次申请", "系统自动申请", 3, 0);
                            //申请退款结果通知地址
                            List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
                            Refundinfo refundinfo = new Refundinfo();
                            refundinfo.setRefundprice(Float.parseFloat(returnmoney));
                            refundinfo.setTradeno(tradeno);
                            refundinfos.add(refundinfo);
                            String applyret = applyRefund(76, notifyurl, Long.parseLong(id), apiorderid, refundinfos,
                                    3, "TrainTickRefundTianquHandle", r1, trainorderid);
                            result = applyret;
                            if ("success".equals(applyret)) {
                                createTrainorderrc(1, Long.parseLong(orderid), "申请退款成功，退款金额：" + returnmoney, "系统自动申请",
                                        3, 0);
                            }
                            else {
                                createTrainorderrc(1, Long.parseLong(orderid), "申请退款失败，退款金额：" + returnmoney, "系统自动申请",
                                        3, 0);
                            }
                            WriteLog.write("天衢114退票支付宝退款", r1 + "退款失败,重新申请退款:trainorderid:" + trainorderid + "result:"
                                    + result);
                            return;
                        }
                    }
                }
                else {
                    result = "false";
                }

            }
        }
        catch (Exception e) {
            result = "false";
        }
        finally {
            WriteLog.write("天衢114退票支付宝退款", r1 + ":trainorderid:" + trainorderid + "result:" + result);
            if (out != null) {
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 申请退款
     * @author zhangtingjia
     * @time 2016年8月26日 下午3:22:04
     * @param agentid
     * @param notifyurl
     * @param id
     * @param apiorderid
     * @param refundinfos
     * @param yewutype
     * @param handleClass
     * @param r1
     * @param trainorderid
     * @return
     */
    public static String applyRefund(long agentid, String notifyurl, long id, String apiorderid,
            List<Refundinfo> refundinfos, int yewutype, String handleClass, int r1, String trainorderid) {
        String result = "";
        WriteLog.write("天衢114退票支付宝退款", r1 + "agentid:" + agentid + "notifyurl:" + notifyurl + "id:" + id
                + "apiorderid:" + apiorderid + "refundinfos.get(0).getTradeno():" + refundinfos.get(0).getTradeno()
                + "refundinfos.get(0).getRefundprice():" + refundinfos.get(0).getRefundprice() + "yewutype:" + yewutype
                + "handleClass:" + handleClass + "trainorderid:" + trainorderid + "r1:" + r1);
        AlipayrefundPro alipayrefundPro = new AlipayrefundPro();
        String alipayrefundProresult = alipayrefundPro.refund(agentid, notifyurl, id, apiorderid, refundinfos,
                yewutype, handleClass);
        WriteLog.write("天衢114退票支付宝退款", r1 + ":trainorderid:" + trainorderid + "申请退款结果alipayrefundProresult:"
                + alipayrefundProresult);

        if (alipayrefundProresult.contains("申请退款成功")) {//申请退款成功，状态为 退款中1
            String updateReturnStatesql = "update returnticketmoneytoalipay set returnmoneystate=2 where trainorderid='"
                    + trainorderid + "' and apiorderid='" + apiorderid + "'";
            Server.getInstance().getSystemService().findMapResultBySql(updateReturnStatesql, null);
            result = "success";
        }
        else {//申请退款失败，状态为 退款失败3
            String updateReturnStatesql = "update returnticketmoneytoalipay set returnmoneystate=3 where trainorderid='"
                    + trainorderid + "' and apiorderid='" + apiorderid + "'";
            Server.getInstance().getSystemService().findMapResultBySql(updateReturnStatesql, null);
            result = "false";
        }
        return result;
    }

    public void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser, int status,
            long ticketid) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(trainorderid);
        rc.setContent(content);
        rc.setStatus(status);// Trainticket.ISSUED
        rc.setCreateuser(createurser);// "12306"
        rc.setTicketid(ticketid);
        rc.setYwtype(yewutype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }
}
