/**
 * 
 */
package com.ccservice.b2b2c.util;

import java.sql.Timestamp;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.base.mailaddress.MailAddress;
import com.ccservice.b2b2c.base.trainOrderOffline.TrainOrderOffline;
import com.ccservice.b2b2c.base.trainTicketOffline.TrainTicketOffline;
import com.ccservice.b2b2c.base.trainpassengerOffline.TrainPassengerOffline;

/**
 * 数据库操作工具类
 * 
 * @time 2015年7月15日 下午5:30:58
 * @author chendong
 */
public class DBoperationUtil {

	public static void main(String[] args) {
		String sql_temp = getCreateTrainorderOfficeProcedureSql(new TrainOrderOffline());

		TrainPassengerOffline trainPassengerOffline = new TrainPassengerOffline();
		trainPassengerOffline.setorderid(1L);
		trainPassengerOffline.setname("陈栋");
		trainPassengerOffline.setidtype(1);
		trainPassengerOffline.setidnumber("412823198909298010");
		sql_temp = getCreateTrainpassengerOfficeProcedureSql(trainPassengerOffline);
		try {
			List list = Server.getInstance().getSystemService()
					.findMapResultByProcedure(sql_temp);
			System.out.println(JSONObject.toJSONString(list));
		} catch (Exception e) {
			System.out.println(sql_temp);
			System.out.println(e.getLocalizedMessage());
			System.out.println("---------------");

		}

	}

	/**
	 * 根据传入的 TrainOrderOffline对象获取到插入到数据库 的存储过程的sql
	 * 
	 * @time 2015年7月15日 下午5:31:52
	 * @author chendong
	 */
	public static String getCreateTrainorderOfficeProcedureSql(
			TrainOrderOffline trainOrderOffline) {
		StringBuffer procedureSql = new StringBuffer();
		procedureSql.append("sp_TrainOrderOffline_insert1 ");
		String OrderNumber = trainOrderOffline.getOrderNumber();
		procedureSql.append("@OrderNumber = N'" + OrderNumber + "',");
		// String CreateTime = "";
		// procedureSql.append("@CreateTime = N'1',");
		Long AgentId = trainOrderOffline.getAgentId();
		procedureSql.append("@AgentId = " + AgentId + ",");
//		Long CreateUId = trainOrderOffline.getCreateUId();
		String CreateUser = trainOrderOffline.getCreateUser();
		String ContactUser = trainOrderOffline.getContactUser();
		String ContactTel = trainOrderOffline.getContactTel();
		Float OrderPrice = trainOrderOffline.getOrderPrice();
		// Integer OrderStatus = trainOrderOffline.getorderstatus();
		// String TradeNo = trainOrderOffline.gettradeno();
		Float AgentProfit = trainOrderOffline.getAgentProfit();
		// Integer SupplyPayWay = trainOrderOffline.getsupplypayway();
		Integer TicketCount = trainOrderOffline.getTicketCount();
		Integer Paystatus = trainOrderOffline.getPaystatus();
		Timestamp outtime=trainOrderOffline.getOrderTimeOut();
		procedureSql.append("@CreateUId = " + trainOrderOffline.getCreateUId() + ",");
		procedureSql.append("@CreateUser = N'" + CreateUser + "',");
		procedureSql.append("@ContactUser = N'" + ContactUser + "',");
		procedureSql.append("@ContactTel = N'" + ContactTel + "',");
		procedureSql.append("@OrderPrice = " + OrderPrice + ",");
//		 procedureSql.append("@OrderStatus = 1,");
		// procedureSql.append("@TradeNo = N'1',");
		procedureSql.append("@AgentProfit = " + AgentProfit + ",");
		// procedureSql.append("@SupplyPayWay = 1,");
		procedureSql.append("@TicketCount = " + TicketCount + ",");
		procedureSql.append("@Paystatus = " + Paystatus + ",");
		procedureSql.append("@OrderNumberOnline = '" + trainOrderOffline.getOrdernumberonline() + "',");
		procedureSql.append("@PaperType = " + trainOrderOffline.getPaperType() + ",");
		procedureSql.append("@PaperBackup = " + trainOrderOffline.getPaperBackup() + ",");
		procedureSql.append("@paperLowSeatCount = " + trainOrderOffline.getPaperLowSeatCount() + ",");
		procedureSql.append("@extSeat = '" + trainOrderOffline.getExtSeat()+"',");
		procedureSql.append("@TradeNo = '" + trainOrderOffline.getTradeNo()+"',");
		if(outtime==null || "".equals(outtime)){
			procedureSql.append("@OrderTimeout = '无'");
		}else{
			procedureSql.append("@OrderTimeout = '" + outtime+"'");
		}
		
		//
		// procedureSql.append("@OrderTimeout = N'1',");

		// sp_TrainOrderOffline_insert
		// @OrderNumber = N'1',
		// @CreateTime = N'1',
		// @AgentId = 1,
		// @CreateUId = 1,
		// @CreateUser = N'1',
		// @ContactUser = N'1',
		// @ContactTel = N'1',
		// @OrderPrice = 1,
		// @OrderStatus = 1,
		// @TradeNo = N'1',
		// @AgentProfit = 1,
		// @SupplyPayWay = 1,
		// @TicketCount = 1,
		// @RefundReason = 1,
		// @RefundReasonStr = N'1',
		// @OrderTimeout = N'1',
		// @ChuPiaoAgentid = 1,
		// @ChuPiaoTime = N'1',
		// @Remark = N'1'
		return procedureSql.toString();
	}

	/**
	 * 根据传入的 trainPassengerOffline对象获取到插入到数据库 的存储过程的sql
	 * 
	 * @time 2015年7月16日 下午1:20:17
	 * @author chendong
	 */
	public static String getCreateTrainpassengerOfficeProcedureSql(
			TrainPassengerOffline trainPassengerOffline) {
		StringBuffer procedureSql = new StringBuffer();
		procedureSql.append("sp_TrainPassengerOffline_insert ");
		Long OrderId = trainPassengerOffline.getorderid();
		procedureSql.append("@OrderId = " + OrderId + ",");
		String Name = trainPassengerOffline.getname();
		procedureSql.append("@Name = '" + Name + "',");
		Integer IdType = trainPassengerOffline.getidtype();
		procedureSql.append("@IdType = " + IdType + ",");
		String IdNumber = trainPassengerOffline.getidnumber();
		procedureSql.append("@IdNumber = '" + IdNumber + "',");
		String Birthday = trainPassengerOffline.getbirthday() == null ? TimeUtil
				.gettodaydate(4) : trainPassengerOffline.getbirthday();
		procedureSql.append("@Birthday = '" + Birthday + "'");
		return procedureSql.toString();
	}

	/**
	 * 根据传入的 TrainTicketOffline对象获取到插入到数据库 的存储过程的sql
	 * 
	 * @time 2015年7月16日 下午1:20:17
	 * @author chendong
	 */
	public String getCreateTrainticketOfficeProcedureSql(
			TrainTicketOffline trainticketoffline) {
		StringBuffer procedureSql = new StringBuffer();
		procedureSql.append("sp_TrainTicketOffline_insert ");
		Long trainPid = trainticketoffline.getTrainPid();
		Long orderId = trainticketoffline.getOrderid();
		Timestamp departTime = trainticketoffline.getDepartTime();
		String departure = trainticketoffline.getDeparture();
		String arrival = trainticketoffline.getArrival();
		String trainno = trainticketoffline.getTrainno();
		int tickettype = trainticketoffline.getTicketType();
		String seattype = trainticketoffline.getSeatType();
		String seatno = trainticketoffline.getSeatNo();
		String coach = trainticketoffline.getCoach();
		float price = trainticketoffline.getPrice();
		String costtime = trainticketoffline.getCostTime();
		String starttime = trainticketoffline.getStartTime();
		String arrivaltime = trainticketoffline.getArrivalTime();
		String suborderid=trainticketoffline.getSubOrderId();
		procedureSql.append("@TrainPid= " + trainPid + ",");
		procedureSql.append("@OrderId= " + orderId + ",");
		procedureSql.append("@DepartTime= '" + departTime + "',");
		procedureSql.append("@Departure ='" + departure + "',");
		procedureSql.append("@Arrival ='" + arrival + "',");
		procedureSql.append("@TrainNo ='" + trainno + "',");
		procedureSql.append("@TicketType =" + tickettype + ",");
		procedureSql.append("@SeatType ='" + seattype + "',");
		procedureSql.append("@SeatNo ='" + seatno + "',");
		procedureSql.append("@Coach ='" + coach + "',");
		procedureSql.append("@Price =" + price + ",");
		procedureSql.append("@CostTime ='" + costtime + "',");
		procedureSql.append("@StartTime='" + starttime + "',");
		procedureSql.append("@ArrivalTime ='" + arrivaltime + "',");
		if(suborderid==null || "".equals(suborderid)){
			procedureSql.append("@SubOrderId ='0'");
		}else{
			procedureSql.append("@SubOrderId ='"+suborderid+"'");
		}
		return procedureSql.toString();
	}

	/**
	 * 根据传入的 MailAddress对象获取到插入到数据库 的存储过程的sql
	 * 
	 * @time 2015年7月28日 11:20:17
	 * @author guozhengju
	 */
	public String getMailAddressProcedureSql(MailAddress mailAddress) {
		StringBuffer procedureSql = new StringBuffer();
		procedureSql.append("sp_MailAddress1_insert ");

		procedureSql.append("@MailName= '" + mailAddress.getMailName() + "',");
		procedureSql.append("@MailTel= '" + mailAddress.getMailTel() + "',");
		procedureSql.append("@Postcode= '" + mailAddress.getPostcode() + "',");
		procedureSql.append("@Address ='" + mailAddress.getAddress() + "',");
		procedureSql.append("@ProvinceName ='" + mailAddress.getProvinceName()
				+ "',");
		procedureSql.append("@CityName ='" + mailAddress.getCityName() + "',");
		procedureSql.append("@RegionName ='" + mailAddress.getRegionName()
				+ "',");
		procedureSql.append("@TownName ='" + mailAddress.getTownName() + "',");
		procedureSql.append("@Orderid ='" + mailAddress.getOrderid() + "',");
		procedureSql.append("@Mail='"+mailAddress.getMail()+"',");
		procedureSql.append("@Note='"+mailAddress.getNote()+"',");
		procedureSql.append("@Busstype="+mailAddress.getBusstype());
		return procedureSql.toString();
	}
	// sp_TrainOrderOfflineRecord_insert
	// public String
	// getCreateTrainorderOfficeRecordProcedureSql(TrainOrderRcOffline
	// trainOrderOfflineRecord){
	// return "";
	// }

}
