package com.ccservice.b2b2c.atom.component;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import client.ServiceStub;

import com.ccservice.b2b2c.atom.server.Server;

public class CachePriceInfo {

	private BaseCache priceCache; 
	private static CachePriceInfo instance;      
    private static Object lock = new Object(); 
    
    public CachePriceInfo() {      
        //这个根据配置文件来，初始BaseCache而已;  
    	//缓存时间设置为一个月
    	priceCache = new BaseCache("priceinfo",-1);           
    }  
	
	/**
	 * 缓存FD指令查询出来的结果
	 * @param strKey Key值
	 * @param strCItyPair  城市对
	 * @param strAirCompany 航空公司两字码
	 * @return
	 * @throws MalformedURLException
	 */
	public String getFDInfo(String strKey,String strCItyPair,String strAirCompany) throws MalformedURLException
	{
		try 
		{      
        	String strReturn="";
        	strReturn = (String) priceCache.get(strKey);
        	if(strReturn==null ||strReturn.equals("")){
            	throw new Exception("price is null");
            }
            return strReturn;      
  
        } catch (Exception e) 
        {
        	System.out.println("getFDInfo>>FDinfokey["+strKey+"]>>"+e.getMessage());  
        	//从接口查询价格
        	String strReturn=Server.getInstance().getTicketSearchService().commandFunction2("FD:"+strCItyPair+"/"+strAirCompany+"$PN$PN$PN", "", "");
        	//将价格存入缓存
        	priceCache.put(strKey,strReturn); 
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
	public String getLTQTEgetprice(String FromCity, String ToCity, String TakeoffDate,
			String FlightNum, String SeatClass, String PassengerType, String IsBottomPrice, String IsFSQ,String officeid) throws MalformedURLException
	{
		String key = FromCity + ToCity + TakeoffDate
				+ FlightNum + SeatClass + PassengerType
				+ IsBottomPrice + IsFSQ;
		String result = "-1";
		WriteLog.write("NBE", "kaYn_Internat_QTE_WithOutPNR:0:" + FromCity
				+ "," + ToCity + "," + TakeoffDate + "," + FlightNum + ","
				+ SeatClass + "," + PassengerType + "," + IsBottomPrice
				+ "," + IsFSQ+","+officeid);
		try 
		{      
        	String strReturn="";
        	strReturn = (String) priceCache.get(key);
        	if(strReturn==null ||strReturn.equals("")){
            	throw new Exception("price is null");
            }
        	WriteLog.write("NBE", "Cache:1:" + result);
            return strReturn;      
  
        } catch (Exception e) 
        {
     		try {
    			ServiceStub stub = new ServiceStub();
    			ServiceStub.KaYn_Internat_QTE_WithOutPNR kaYn_Internat_QTE_WithOutPNR = new ServiceStub.KaYn_Internat_QTE_WithOutPNR();
    			kaYn_Internat_QTE_WithOutPNR.setFromCity(FromCity);
    			kaYn_Internat_QTE_WithOutPNR.setToCity(ToCity);
    			kaYn_Internat_QTE_WithOutPNR.setTakeoffDate(TakeoffDate);
    			kaYn_Internat_QTE_WithOutPNR.setFlightNum(FlightNum);
    			kaYn_Internat_QTE_WithOutPNR.setSeatClass(SeatClass);
    			kaYn_Internat_QTE_WithOutPNR.setPassengerType(PassengerType);
    			kaYn_Internat_QTE_WithOutPNR.setIsBottomPrice(IsBottomPrice);
    			kaYn_Internat_QTE_WithOutPNR.setIsFSQ(IsFSQ);
    			kaYn_Internat_QTE_WithOutPNR.setOffice(officeid);
    			ServiceStub.KaYn_Internat_QTE_WithOutPNRResponse response = stub.kaYn_Internat_QTE_WithOutPNR(kaYn_Internat_QTE_WithOutPNR);
    			result = response.getKaYn_Internat_QTE_WithOutPNRResult();
    			WriteLog.write("NBE", "kaYn_Internat_QTE_WithOutPNR:1:" + result);
    			//将价格存入缓存
            	priceCache.put(key,result); 
    		} catch (AxisFault e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} catch (RemoteException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}
     		return result;
        }
	}
}
