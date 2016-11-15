package com.ccservice.b2b2c.atom.pay;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.insuranceinfo.Insuranceinfo;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;
import com.pay.config.AlipayConfig;


public class Pay_function
{

	/**
	 * 通过订单ID获取订单
	 * @param id
	 * @return
	 */
	public static Orderinfo getOrderinfobyid(long id)
	{
		Orderinfo orderinfo=Server.getInstance().getAirService().findOrderinfo(id);
		return orderinfo;
	}
	
	
	/**
	 * 取得分润参数
	 * @param orderinfo
	 * @return
	 */
	public static String getSplid(Orderinfo orderinfo){
		Customeragent customeragent = Server.getInstance().getMemberService().findCustomeragent(orderinfo.getSaleagentid());
	
		java.util.List<Segmentinfo>listSegmentinfo=Server.getInstance().getAirService().findAllSegmentinfo(" where 1=1 and "+Segmentinfo.COL_orderid+" ="+orderinfo.getId(), " ", -1, 0);
		java.util.List<Passenger>listPassenger=Server.getInstance().getAirService().findAllPassenger(" where 1=1 and "+Passenger.COL_orderid+" ="+orderinfo.getId(), " ", -1, 0);
		Float Pprice=0f;
		
		for(int b=0;b<listPassenger.size();b++){
			if(listPassenger.get(b).getPtype()==1){//1 成人  
			
				Pprice+=listSegmentinfo.get(0).getParvalue()-listSegmentinfo.get(0).getParvalue()*listSegmentinfo.get(0).getRatevalue()/100;
				
				Pprice+=listPassenger.get(b).getFuelprice();
				Pprice+=listPassenger.get(b).getAirportfee();
			}else{//2 儿童  3婴儿
				
				Pprice+=listPassenger.get(b).getPrice();
				Pprice+=listPassenger.get(b).getFuelprice();
				Pprice+=listPassenger.get(b).getAirportfee();
			}
		}
		
		
		/*for(int a=0;a<listSegmentinfo.size();a++){
			Float price=listSegmentinfo.get(a).getParvalue();
			Pprice+=price-price*listSegmentinfo.get(a).getRatevalue()/100;
			Pprice+=listSegmentinfo.get(a).getAirportfee();
			Pprice+=listSegmentinfo.get(a).getFuelfee();
		}*/
		
		//供应商支付宝账号  分给供应商金钱   
		String split = customeragent.getAlipayaccount()+"^"+  Pprice +"^fencheng";
		
		int f =  AlipayConfig.getInstance().getSplit();
		/*String where = " WHERE "+Passenger.COL_orderid + " = " + orderinfo.getId();
		java.util.List list =Server.getInstance().getAirService().findAllPassenger(where, "", -1, 0);
		if(list!=null && list.size()>1){
			f =  AlipayConfig.split*list.size();
		}
		
		//采购商 
		if(orderinfo.getRelationorderid()!=null){
			Orderinfo orderinfo2=Server.getInstance().getAirService().findOrderinfo(orderinfo.getRelationorderid());
			if(orderinfo2!=null){
				where = " WHERE "+Passenger.COL_orderid + " = " + orderinfo2.getId();
				list =Server.getInstance().getAirService().findAllPassenger(where, "", -1, 0);
				if(list!=null && list.size()>0){
					f +=  AlipayConfig.split*list.size();
				}
			}
			
		}*/
		
		//split = AlipayConfig.splitEmail+"^"+ f/100 +"^fencheng";
		return split;	
	}
	
	/**
	 * 取分销商email
	 * @param orderinfo
	 * @return
	 */
	public static String getPayemail(Orderinfo orderinfo){
		String email = "xxxx@xxx.com";
		if(orderinfo.getBuyagentid()!=null){
			Customeragent agent =	Server.getInstance().getMemberService().findCustomeragent(orderinfo.getBuyagentid());	
			if(agent!=null){
				email = agent.getAlipayaccount();
				if(email==null || email.length()<4){
					email = "xxxx@xxx.com";
				}
			}
		}
			
		return email;
	}
	
	/**
	 * 通过订单获取订单总金额
	 * @param id
	 * @return
	 */
	public static String getorderpricesum(Orderinfo orderinfo)
	{
		
		if(orderinfo!=null)
		{
			Double pricesum=0d;
			if(orderinfo.getTotalticketprice()!=null&&orderinfo.getTotalticketprice()>0){
			pricesum+=orderinfo.getTotalticketprice();
			}
			if(orderinfo.getTotalfuelfee()!=null){
			pricesum+=orderinfo.getTotalfuelfee();
			}
			if(orderinfo.getTotalairportfee()!=null){
			pricesum+=orderinfo.getTotalairportfee();
			}
			//保险金额
			pricesum+=getIssurByOrderid(orderinfo.getId());
			if(orderinfo.getRelationorderid()!=null)
			{
				Orderinfo orderinfo2=Server.getInstance().getAirService().findOrderinfo(orderinfo.getRelationorderid());
				if(orderinfo2.getTotalticketprice()!=null&&orderinfo2.getTotalticketprice()>0){
				pricesum+=orderinfo2.getTotalticketprice();
				}
				if(orderinfo2.getTotalfuelfee()!=null){
				pricesum+=orderinfo2.getTotalfuelfee();
				}
				if(orderinfo2.getTotalairportfee()!=null){
				pricesum+=orderinfo2.getTotalairportfee();
				}
				if(orderinfo2.getPostmoney()!=null){
				pricesum+=orderinfo2.getPostmoney();
				}
				
			}
			DecimalFormat format = null;
			format = (DecimalFormat) NumberFormat.getInstance();
			format.applyPattern( "###0.00" );
			try{
				String result = format.format( pricesum );
				return result;
			}catch ( Exception e ){
				return Double.toString( pricesum );
			}
		}
		return "";
	}
	/**
	 * 获取保险金额
	 * @param sum
	 * @return
	 */
	/**
	 * 根据订单号获得保险金额
	 * 
	 * @param orderid
	 * @return
	 */
	public static Double getIssurByOrderid(long orderid) {
		String sql="SELECT SUM(C_INSURPRICE) FROM T_PASSENGER WHERE C_ORDERID="+orderid+" AND C_STATE<>12";
		int i=Server.getInstance().getAirService().countPassengerBySql(sql);
		
		return (double)i;
	}
	
	/**
	 * 获取保险金额
	 * 
	 * @param sum
	 * @return
	 */
	public static Double getInsurancPrice(Long insruid) {
		Double price = 0d;
		if (insruid != null && insruid > 0l) {
			Insuranceinfo insurance = Server.getInstance().getMemberService()
					.findInsuranceinfo(insruid);
			price = Double.valueOf(insurance.getInsurancefee());
		}
		return price;
	}

}
