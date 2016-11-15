package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;

public class TongChengTrainOrderInfo {
	
	public String queryOrderInfo(JSONObject json){
		JSONObject result = new JSONObject();
		 //请求参数
        String orderid = json.containsKey("orderid") ? json.getString("orderid") : "";
        String transactionid = json.containsKey("transactionid") ? json.getString("transactionid") : "";
        
        //验证参数
        if (ElongHotelInterfaceUtil.StringIsNull(orderid) || ElongHotelInterfaceUtil.StringIsNull(transactionid)) {
        	result.put("code", "107");
        	result.put("msg", "业务参数缺失");
            return result.toString();
        }
        //查询订单
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        WriteLog.write("t同程火车票接口_train_order_info", "orderid:" + orderid + ":transactionid:" + transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        //订单不存在
        if (orders == null || orders.size() != 1) {
        	result.put("code", "402");
        	result.put("msg", "订单不存在");
            return result.toString();
        }
        
        Trainorder order = orders.get(0);
        //加载其他信息
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());
        //设置返回
        result.put("orderid", orderid);
        result.put("transactionid", transactionid);
        result.put("ordernumber", strTrim(order.getExtnumber()));
        result.put("orderstatusname", TongChengOrderStatus.StatusStr(order));

        String checi = "";			//车次
        String fromstation = "";	//出发站
        String tostation = "";		//到达站
        String traintime = "";		//开车时间
      	//车票状态
        JSONArray ticketstatus = new JSONArray();
        //订单乘客
        List<Trainpassenger> passengers = order.getPassengers();
        //防止无乘客
        passengers = passengers == null ? new ArrayList<Trainpassenger>() : passengers;
        //循环乘客
        for (Trainpassenger passenger : passengers) {
            List<Trainticket> tickets = passenger.getTraintickets();
            //循环车票
            for (Trainticket ticket : tickets) {
                //乘客
                ticket.setTrainpassenger(passenger);
                //返回字段
                checi = strTrim(ticket.getTrainno());
                tostation = strTrim(ticket.getArrival());
                traintime = strTrim(ticket.getDeparttime());
                fromstation = strTrim(ticket.getDeparture());
                
                JSONObject oldTicket = new JSONObject();
                oldTicket.put("ticket_no", strTrim(ticket.getTicketno()));//票号
                oldTicket.put("passengersename", strTrim(passenger.getName()));//乘客姓名
                oldTicket.put("piaotypename", strTrim(ticket.getTickettypestr()));//车票种类
                oldTicket.put("price", ticket.getPrice() == null ? 0 : ticket.getPrice().floatValue());//票价
                oldTicket.put("status", getTicketStatus(ticket, true));//车票状态
                oldTicket.put("statusid", getTicketStatus(ticket, false));//状态ID
                //车箱
                if (ElongHotelInterfaceUtil.StringIsNull(ticket.getCoach())
                        || ElongHotelInterfaceUtil.StringIsNull(ticket.getSeatno())) {
                    oldTicket.put("cxin", "");
                }
                else {
                    oldTicket.put("cxin", ticket.getCoach() + "车厢," + ticket.getSeatno());
                }
                //ADD
                ticketstatus.add(oldTicket);
            }
        }
        result.put("code", "100");
        result.put("success", true);
        result.put("msg", "查询订单成功");
        result.put("checi", checi);
        result.put("traintime", traintime);
        result.put("tostation", tostation);
        result.put("fromstation", fromstation);
        result.put("ticketstatus", ticketstatus);
        WriteLog.write("t同程火车票接口_train_order_info", "result:" + result);
		return result.toString();
	}
	
    //字符串去空格
    private String strTrim(String str) {
        return ElongHotelInterfaceUtil.StringIsNull(str) ? "" : str.trim();
    }
    
    /**
     * 车票状态
     * @param isname true:状态名称；false：状态ID
     * @param oldTicket true:原车票；false：新车票
     * @remark statusid
     * 1.  已取票，表示客户已在车站窗口或取票机取得了车票，因此如果有退款，可能会有以下三种可能：取了之后退票；取了之后改签；还有可能取了票之后再改签，然后 再退票
     * 2.  已在线改签，表示通过我们双方的系统改签的
     * 3.  已线下改签，表示客户直接在窗口改签
     * 4.  已在线退票，表示通过我们双方的系统退票的
     * 5.  已线下退票，表示客户直接在窗口退票
     * 6.  已出票，表示客户已经支付购票款项
     * 7.  待出票，表示尚未支付票款
     */
    public static String getTicketStatus(Trainticket ticket, boolean isname) {
        String result = isname ? "待核实" : "8";
        //错误
        if (ticket == null || ticket.getId() <= 0) {
            return result;
        }
        //状态
        int status = ticket.getStatus();
        int state12306 = ticket.getState12306() == null ? 0 : ticket.getState12306().intValue();
        int changeType = ticket.getChangeType() == null ? 0 : ticket.getChangeType().intValue();
        int isapplyticket = ticket.getIsapplyticket() == null ? 0 : ticket.getIsapplyticket().intValue();
        //待出票
        if (status == Trainticket.WAITPAY || status == Trainticket.WAITISSUE) {
            result = isname ? "待出票" : "7";
        }
        //已在线改签	未启用
//            else if (changeType == 1) {
//                result = isname ? "已在线改签" : "2";
//            }
        //已在线退票
        else if ((status == Trainticket.WAITREFUND || status == Trainticket.REFUNDIING
                || status == Trainticket.REFUNDED || status == Trainticket.REFUNDFAIL)
                && isapplyticket == 1) {
            result = isname ? "已在线退票" : "4";
        }
        //已线下退票
        else if ((status == Trainticket.WAITREFUND || status == Trainticket.REFUNDIING
                || status == Trainticket.REFUNDED || status == Trainticket.REFUNDFAIL)
                && isapplyticket == 2) {
            result = isname ? "已线下退票" : "5";
        }
        //已线下改签
        else if (changeType > Trainticket.NONISSUEDABLE && changeType < Trainticket.APPLYCHANGE) {
            result = isname ? "已线下改签" : "3";
        }
        //已取票
        else if (state12306 == Trainticket.HASTICKET) {
            result = isname ? "已取票" : "1";
        }
        //已出票
        else if (status == Trainticket.ISSUED || status == Trainticket.APPLYTREFUND
                || status == Trainticket.REFUNDROCESSING || status == Trainticket.NONREFUNDABLE
                || status == Trainticket.APPLYCHANGE || status == Trainticket.THOUGHCHANGE) {
            result = isname ? "已出票" : "6";
        }
        WriteLog.write("t同程火车票接口_train_order_info", "result:" + result);
        return result;
    }
}
