package com.ccservice.b2b2c.atom.component;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import client.ServiceStub;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;

public class CacheFlightInfo {

	private BaseCache flightCache; 
	private static CacheFlightInfo instance;      
    private static Object lock = new Object(); 
    
    public CacheFlightInfo() {      
        //这个根据配置文件来，初始BaseCache而已;  
    	//缓存时间设置为8小时
		flightCache = new BaseCache("flightinfo",-1);           
    }  
	
    /**
     * 缓存航班信息
     * @param strKey Key值
     * @param flightsearch  航班查询类
     * @return
     * @throws MalformedURLException
     */
	public String getFlightInfo(String strKey,FlightSearch flightsearch) throws MalformedURLException
	{
		try 
		{      
        	String strReturn="";
        	strReturn = (String) flightCache.get(strKey);
        	if(strReturn==null ||strReturn.equals("")){
            	throw new Exception("flight is null");
            }
            return strReturn;      
  
        } catch (Exception e) 
        {
        	System.out.println("getFlightInfo>>flightinfokey["+strKey+"]>>"+e.getMessage());  
        	//从接口查询航班
        	String strReturn=Server.getInstance().getTicketSearchService().AVOpen(flightsearch);
        	//将航班信息存入缓存
        	flightCache.put(strKey,strReturn); 
        	return strReturn;
        }
        
	}
	
	/**
	 * 缓存FD指令查询出来的结果
	 * @param strKey Key值
	 * @param strCItyPair  城市对
	 * @param strAirCompany 航空公司两字码
	 * @return
	 * @throws MalformedURLException
	 */
	public String getFDInfo(String strKey,String strCItyPair,String strAirCompany,String strDate) throws MalformedURLException
	{
		try 
		{      
        	String strReturn="";
        	strReturn = (String) flightCache.get(strKey);
        	if(strReturn==null ||strReturn.equals("")){
            	throw new Exception("price is null");
        	}
            return strReturn;      
            
        } catch (Exception e) 
        {
        	System.out.println("getFlightInfo>>flightinfokey["+strKey+"]>>"+e.getMessage());  
        	//从接口查询航班
        	String strReturn=Server.getInstance().getTicketSearchService().commandFunction2("FD:"+strCItyPair+"/"+strAirCompany+"/"+strDate+"$PN$PN$PN", "", "");
        	//将航班信息存入缓存
        	flightCache.put(strKey,strReturn); 
        	return strReturn;
        }
	}
	
	public String searchFlightByLT(String FromCity1, String ToCity1,
 			String TakeoffDate1, String TakeoffTime1, String Carrier1,
			String IsDirect1, String IsShared1, String officeID) {
		String key = FromCity1 + ToCity1 + TakeoffDate1 + TakeoffTime1
				+ Carrier1 + IsDirect1 + IsShared1;
		String result = "-1";
		WriteLog.write("NBE", "kaYn_AVH_New:0:" + FromCity1+","+ToCity1+","+
 				TakeoffDate1+","+TakeoffTime1+","+Carrier1+","+
 				IsDirect1+","+IsShared1+","+officeID);
		try {
			String strReturn = "";
			strReturn = (String) flightCache.get(key);
			if (strReturn == null || strReturn.equals("")) {
				throw new Exception("price is null");
			}
			WriteLog.write("NBE", "Cache:1:" +result);
			return strReturn;

		} catch (Exception e) {
			ServiceStub stub;
			try {
				stub = new ServiceStub();
				ServiceStub.KaYn_AVH_New kaYn_AVH_New = new ServiceStub.KaYn_AVH_New();
				kaYn_AVH_New.setFromCity(FromCity1);
				kaYn_AVH_New.setToCity(ToCity1);
				kaYn_AVH_New.setTakeoffDate(TakeoffDate1);
				kaYn_AVH_New.setTakeoffTime(TakeoffTime1);
				kaYn_AVH_New.setCarrier(Carrier1);
				kaYn_AVH_New.setIsDirect(IsDirect1);
				kaYn_AVH_New.setIsShared(IsShared1);
				kaYn_AVH_New.setOffice(officeID);
				ServiceStub.KaYn_AVH_NewResponse response = stub
						.kaYn_AVH_New(kaYn_AVH_New);
				result = response.getKaYn_AVH_NewResult();
				WriteLog.write("NBE", "kaYn_AVH_New:1:" +result);
				// 将航班信息存入缓存
				if(result!=null && result.indexOf("无航班")==-1 && !"-1".equals(result)){
					flightCache.put(key, result);
				}else{
					System.out.println("未查询到有用数据，不加人缓存");
				}
			} catch (AxisFault e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block`
				e1.printStackTrace();
			}
			return result;
		}

	}
}
