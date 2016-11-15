package com.ccservice.b2b2c.atom.servlet;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.CachePriceInfo;
import com.ccservice.b2b2c.base.aircompany.Aircompany;
import com.ccservice.b2b2c.base.service.IAirService;

public class TicketJob implements Job{

	//price缓存
	private CachePriceInfo cacheprice=new CachePriceInfo();
	
	/**
	 * 查询航班价格方法
	 * 
	 */
	public void execute(JobExecutionContext arg0) throws JobExecutionException 
	{
		try
		{
			String url = "http://localhost:8080/lthk_service/service/";
			HessianProxyFactory factory = new HessianProxyFactory();
			IAirService service=(IAirService)factory.create(IAirService.class,url+IAirService.class.getSimpleName());

			//String[] strHotCityPair={"PEK","SHA","PVG","CAN","SZX","CTU","HGH","WUH","XIY","CKG","TAO","CSX","NKG","XMN","KMG","DLC","TSN","CGO","SYX","TNA","FOC"};
			String[] strHotCityPair={"PEK","SHA","PVG","CAN"};
			String strHotCityPari="";
			String strPriceInfo="";
			int intCount=0;
			

			//取得所有国内航空公司
			List<Aircompany> listAirCompany=service.findAllAircompany(" where "+Aircompany.COL_countrycode+"='CN'", "", -1, 0);
			
			//循环热门城市对
			for(int i=0;i<strHotCityPair.length;i++)
			{
				for(int j=0;j<strHotCityPair.length;j++)
				{
					//组成热门城市对
					if(!strHotCityPair[i].equals(strHotCityPair[j]))
					{
						strHotCityPari=strHotCityPair[i]+strHotCityPair[j];
						for(Aircompany aircompany:listAirCompany)
						{
							intCount++;
							strPriceInfo=cacheprice.getFDInfo(strHotCityPari+"_"+aircompany.getAircomcode(), strHotCityPari, aircompany.getAircomcode());
						    System.out.println("查询结果："+strPriceInfo);
						}
					}
				}
			}
			System.out.println("========执行完毕,共"+intCount+"条价格数据被更新！=======================");
		    
	
		}catch (Exception e) {
			e.printStackTrace();
		}
	} 
 }