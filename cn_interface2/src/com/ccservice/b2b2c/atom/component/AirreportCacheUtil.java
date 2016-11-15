package com.ccservice.b2b2c.atom.component;

import java.net.MalformedURLException;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;

public class AirreportCacheUtil {

	private BaseCache baseche; 
	private static AirreportCacheUtil instance;      
    private static Object lock = new Object(); 
    
    public AirreportCacheUtil(String name,int second) {      
        //这个根据配置文件来，初始BaseCache而已;  
    	//缓存时间设置为8小时
    	baseche = new BaseCache(name,second);           
    }  
	
    /**
     * 缓存航班信息，此方法暂时没用
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
        	strReturn = (String) baseche.get(strKey);
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
        	baseche.put(strKey,strReturn); 
        	return strReturn;
        }
        
	}
	
	public void aircacheput(String strKey,Object obj){
		baseche.put(strKey,obj);
	}
}
