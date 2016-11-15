package com.ccservice.b2b2c.atom.servlet;



import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.util.PageInfo;



public class UpdateHotelorderState implements Job{


	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		try{
			PageInfo pageinfo =new PageInfo();
		//	String sql="  SELECT COUNT(ID) FROM [LTHK_DB].[dbo].[T_HOTELORDER] ";
		//	int topage = Server.getInstance().getHotelService().excuteHotelorderBySql(sql);
		//	pageinfo.setTotalpage(topage);
			
			
			String where =" WHERE 1=1 AND "+Hotelorder.COL_waicode+" IS NOT NULL  ";
		System.out.println("where=="+where);
			/*
			
			
		
			
			for(int a=0;a<pageinfo.getTotalpage();a++){
				
				List<Hotelorder> listHotelorder=Server.getInstance().getHotelService().findAllHotelorderForPageinfo(where, " ORDER BY ID ", pageinfo);
				if(pageinfo.getTotalrow()>0 && listHotelorder.size()==0){
					pageinfo.setPagenum(a+1);
					listHotelorder = Server.getInstance().getHotelService().findAllHotelorderForPageinfo(where.toString()," ORDER BY ID ",pageinfo);	
				}
				
			
				MGHotelService hotelService=new MGHotelServiceProxy();
				
				DetailOrderRequest detailOrderRequest =new DetailOrderRequest();
				MangoAuthor author=new MangoAuthor();
				author.setChannel("SXTJ");
				author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
				detailOrderRequest.setAuthor(author);
				
				detailOrderRequest.setOrdercds(listHotelorder.get(a).getWaicode());
				try {
					DetailOrderResponse detailOrderResponse =hotelService.detailOrder(detailOrderRequest);
					
					
					if(detailOrderResponse.getResult().getValue()==1)
					{
						String orderstate=detailOrderResponse.getRoomOrder(0).getOrderStates();
						
						//1.前台订单未提交  2.已提交订单  3.已提交中台  4.已入住  5.提前退房  6，,正常退房 7.延住  8.已付款  9.已创建退款单 10.退款单已审批 11.财务已经退款  
						//12.退款成功  13.NOSHOW  14 已取消
						Hotelorder hotelorder=listHotelorder.get(a);
						
						if(orderstate.equals("4")){//入住
							hotelorder.setState(9);
							
						}
						if(orderstate.equals("14")){//取消
							hotelorder.setState(6);
							
						}
						if(orderstate.equals("13")){//NOSHOW
							hotelorder.setState(10);
							
						}
						Server.getInstance().getHotelService().updateHotelorderIgnoreNull(hotelorder);
						//getrebatetouser(hotelorder.getProfits()+"", hotelorder.getId()+"", "2");//添加各级返利记录
						
						
						
					}
					} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				
				}
		   
			}*/
	
		}catch (Exception e) {
			e.printStackTrace();
		}
	} 
 }