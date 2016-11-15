package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import jxl.Cell;
import jxl.Sheet;
import java.util.*;
import jxl.Workbook;
import java.io.File;
import jxl.write.Label;
import java.sql.Timestamp;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.FuzzyRefund;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.uniontrade.Uniontrade;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.LocalRefundUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.local.AliPayRefundUtil;

/**
 * 帮客服导线下退票Excel表格，暂只支付Excel2003
 */

public class WriteRefundExcelDemo {

    public static void main(String[] args) throws Exception {

        int NameCol = 4;//乘客姓名列，从1开始，对应excel乘客姓名列

        int orderNumCol = 1;//订单号列，从1开始，对应excel订单号列

        String FileName = "HS车站退票";//文件名称，注意为.xls，如不是，请转换为excel2003

        LocalRefundUtil localUtil = new LocalRefundUtil();
        WriteRefundExcelDemo demo = new WriteRefundExcelDemo();
        Map<String, Float> aliRefundMap = new HashMap<String, Float>();
        Map<String, Trainorder> orderMap = new HashMap<String, Trainorder>();
        Map<Long, Trainorderchange> changeMap = new HashMap<Long, Trainorderchange>();
        Map<String, String> payUrlMap = new HashMap<String, String>();//AliPayRefundUtil.payUrlMap();
        Map<String, List<Trainticket>> matchMap = new HashMap<String, List<Trainticket>>();//key:订单ID+乘客姓名
        Map<Long, List<FuzzyRefund>> fuzzyRefundMap = new HashMap<Long, List<FuzzyRefund>>();//模糊退，key:订单ID

        String file = "C:/Users/WH/Desktop/" + FileName;
        Workbook readwb = Workbook.getWorkbook(new File(file + ".xls"));
        WritableWorkbook workbook = Workbook.createWorkbook(new File(file + "过完.xls"), readwb);
        WritableSheet sheet = workbook.createSheet("航天", 0);
        NameCol = NameCol - 1;
        orderNumCol = orderNumCol - 1;
        //获取第一张Sheet表   
        Sheet readsheet = readwb.getSheet(0);
        //获取Sheet表中所包含的总列数   
        int rsColumns = readsheet.getColumns();
        //获取Sheet表中所包含的总行数   
        int rsRows = readsheet.getRows();
        //获取指定单元格的对象引用   
        for (int i = 0; i < rsRows; i++) {
            String oid = "";
            String name = "";
            System.out.print(i + "----->");
            String refundMsg = "";
            String ticketStatus = "";
            Timestamp bookTime = null;
            long OrderId = 0;
            for (int j = 0; j < rsColumns; j++) {
                try {
                    Cell cell = readsheet.getCell(j, i);
                    String content = cell.getContents();
                    System.out.print(content + "####");
                    if (i > 0 && j == orderNumCol) {
                        oid = content.trim();
                    }
                    else if (i > 0 && j == NameCol) {
                        name = content.trim();
                    }
                    if (i > 0 && j == rsColumns - 1) {
                        //单号取订单
                        Trainorder order = demo.getTrainOrder(oid, orderMap);
                        OrderId = order.getId();
                        bookTime = order.getCreatetime();
                        //订单流水号
                        String tradeNo = order.getSupplytradeno();
                        //设置值
                        refundMsg = tradeNo;
                        //已匹配
                        String key = order.getId() + name;
                        //所有车票
                        List<Trainticket> orderTicketList = localUtil.orderTicket(order);
                        //查询订单退款
                        float aliRefundTotal = demo.aliRefundTotal(localUtil, tradeNo, aliRefundMap, payUrlMap, order,
                                null);
                        //车票累计退款
                        float ticketRefundTotal = localUtil.ticketRefundTotal(
                                demo.getFuzzyRefund(order, fuzzyRefundMap, localUtil), orderTicketList, false);
                        //匹配车票
                        Trainticket ticket = matchTicket(orderTicketList, name, matchMap.get(key));
                        //存在车票
                        if (ticket != null && ticket.getId() > 0) {
                            List<Trainticket> matchList = matchMap.containsKey(key) ? matchMap.get(key)
                                    : new ArrayList<Trainticket>();
                            matchList.add(ticket);
                            matchMap.put(key, matchList);
                            //改签
                            long changeId = ticket.getChangeid();
                            //新票价
                            float newPrice = ticket.getTcnewprice() == null ? 0 : ticket.getTcnewprice();
                            //高改订单退款
                            if (newPrice > ticket.getPrice() && changeId > 0) {
                                //改签订单
                                Trainorderchange change = demo.getChangeOrder(changeId, changeMap);
                                //交易号
                                refundMsg = change.getSupplytradeno();
                                //改签退款
                                float changeRefund = demo.aliRefundTotal(localUtil, refundMsg, aliRefundMap, payUrlMap,
                                        null, change);
                                //退款相加
                                aliRefundTotal = ElongHotelInterfaceUtil.floatAdd(aliRefundTotal, changeRefund);
                            }
                            //车票状态
                            ticketStatus = getStatusstr(ticket.getStatus(), ticket.getChangeType());
                            //已退票退款
                            if ("已退票退款".equals(ticketStatus)) {
                                refundMsg = "";
                            }
                        }
                        else {
                            refundMsg = "";
                        }
                        if (aliRefundTotal <= ticketRefundTotal || aliRefundTotal == 0) {
                            refundMsg = "";
                        }
                    }
                    sheet.addCell(new Label(j, i, content));
                }
                catch (Exception e) {

                }
            }
            if (i == 0) {
                sheet.addCell(new Label(rsColumns, i, "车票状态"));
                sheet.addCell(new Label(rsColumns + 1, i, "商户交易号"));
                sheet.addCell(new Label(rsColumns + 2, i, "预订时间"));
                sheet.addCell(new Label(rsColumns + 3, i, "订单ID"));
            }
            else {
                sheet.addCell(new Label(rsColumns, i, ticketStatus));
                sheet.addCell(new Label(rsColumns + 1, i, refundMsg));
                sheet.addCell(new Label(rsColumns + 2, i, bookTime == null ? "" : bookTime.toString()));
                sheet.addCell(new Label(rsColumns + 3, i, OrderId > 0 ? String.valueOf(OrderId) : ""));
            }
            System.out.println();
        }
        workbook.write();
        workbook.close();
        readwb.close();
    }

    //存在高改
    public static Trainticket matchTicket(List<Trainticket> orderTicketList, String name, List<Trainticket> matchList) {
        if (matchList == null) {
            matchList = new ArrayList<Trainticket>();
        }
        Map<Long, Trainticket> map = new HashMap<Long, Trainticket>();
        for (Trainticket ticket : matchList) {
            map.put(ticket.getId(), ticket);
        }
        int count = 0;
        int current = matchList.size();
        for (Trainticket ticket : orderTicketList) {
            if (name.equals(ticket.getTrainpassenger().getName())) {
                count++;
            }
        }
        for (Trainticket ticket : orderTicketList) {
            if (!name.equals(ticket.getTrainpassenger().getName())) {
                continue;
            }
            if (current >= count || !map.containsKey(ticket.getId())) {
                return ticket;
            }
        }
        return new Trainticket();
    }

    //支付宝退款
    public float aliRefundTotal(LocalRefundUtil localUtil, String tradeNo, Map<String, Float> aliRefundMap,
            Map<String, String> payUrlMap, Trainorder order, Trainorderchange change) {
        if (ElongHotelInterfaceUtil.StringIsNull(tradeNo) || !tradeNo.startsWith("2")) {
            return 0;
        }
        if (!aliRefundMap.containsKey(tradeNo)) {
            //直接查支付宝
            String aliPayPath = order != null ? order.getSupplyaccount() : change.getPayflag();
            String aliPayAcount = order != null ? order.getAutounionpayurlsecond() : change.getPayaccount();
            float aliRefundTotal = AliPayRefundUtil.refundPrice(aliPayPath, aliPayAcount, tradeNo, payUrlMap);
            //查询失败
            if (aliRefundTotal < 0) {
                //支付宝退款
                List<Uniontrade> aliRefundPrice = localUtil.aliRefundPrice(tradeNo);
                //支付宝累计退款
                aliRefundTotal = localUtil.aliRefundTotal(aliRefundPrice);
            }
            //设置
            aliRefundMap.put(tradeNo, aliRefundTotal);
            //返回
            return aliRefundTotal;
        }
        else {
            return aliRefundMap.get(tradeNo);
        }
    }

    public List<FuzzyRefund> getFuzzyRefund(Trainorder order, Map<Long, List<FuzzyRefund>> fuzzyRefundMap,
            LocalRefundUtil localUtil) {
        long oid = order.getId();
        List<FuzzyRefund> list = new ArrayList<FuzzyRefund>();
        if (!fuzzyRefundMap.containsKey(oid)) {
            list = localUtil.orderFuzzy(order);
            fuzzyRefundMap.put(oid, list);
        }
        else {
            list = fuzzyRefundMap.get(oid);
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    public Trainorder getTrainOrder(String oid, Map<String, Trainorder> orderMap) {
        Trainorder order = new Trainorder();
        if (!orderMap.containsKey(oid)) {
            String sql = "select ID from T_TRAINORDER with(nolock) where C_QUNARORDERNUMBER = '" + oid + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() == 1) {
                Map map = (Map) list.get(0);
                order = Server.getInstance().getTrainService().findTrainorder(Long.parseLong(map.get("ID").toString()));
            }
            orderMap.put(oid, order);
        }
        else {
            order = orderMap.get(oid);
        }
        return order == null ? new Trainorder() : order;
    }

    public Trainorderchange getChangeOrder(long changeId, Map<Long, Trainorderchange> changeMap) {
        Trainorderchange change = new Trainorderchange();
        if (!changeMap.containsKey(changeId)) {
            change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
            changeMap.put(changeId, change);
        }
        else {
            change = changeMap.get(changeId);
        }
        return change == null ? new Trainorderchange() : change;
    }

    public static String getStatusstr(int status, Integer changeType) {
        //车站改签状态标识
        changeType = changeType == null ? 0 : changeType.intValue();
        //车站改签状态名称
        if (status == Trainticket.FINISHCHANGE && changeType > Trainticket.NONISSUEDABLE
                && changeType < Trainticket.APPLYCHANGE) {
            if (changeType == Trainticket.APPLYTREFUND) {
                return "线下改签申请";
            }
            if (changeType == Trainticket.REFUNDROCESSING) {
                return "线下改签处理中";
            }
            if (changeType == Trainticket.NONREFUNDABLE) {
                return "无法线下改签退款";
            }
            if (changeType == Trainticket.WAITREFUND) {
                return "线下改签等待退款";
            }
            if (changeType == Trainticket.REFUNDIING) {
                return "线下改签退款中";
            }
            if (changeType == Trainticket.REFUNDED) {
                return "线下改签已退款";
            }
            if (changeType == Trainticket.REFUNDFAIL) {
                return "线下改签回调失败";
            }
        }
        switch (status) {
        case Trainticket.WAITPAY:
            return "等待支付";
        case Trainticket.WAITISSUE:
            return "等待出票";
        case Trainticket.ISSUED:
            return "已出票";
        case Trainticket.NONISSUEDABLE:
            return "无法出票";
        case Trainticket.APPLYTREFUND:
            return "申请退票";
        case Trainticket.REFUNDROCESSING:
            return "退票处理中";
        case Trainticket.NONREFUNDABLE:
            return "无法退票";
        case Trainticket.WAITREFUND:
            return "等待退款";
        case Trainticket.REFUNDIING:
            return "退款中";
        case Trainticket.REFUNDED:
            return "已退票退款";
        case Trainticket.REFUNDFAIL:
            return "退票回调失败";
        case Trainticket.APPLYCHANGE:
            return "申请改签";
        case Trainticket.APPLYROCESSING:
            return "改签处理中";
        case Trainticket.CANTCHANGE:
            return "无法改签";
        case Trainticket.THOUGHCHANGE:
            return "改签通过";
        case Trainticket.FILLMONEY:
            return "补款成功";
        case Trainticket.FINISHCHANGE:
            return "改签完成";
        }
        return "";
    }
}