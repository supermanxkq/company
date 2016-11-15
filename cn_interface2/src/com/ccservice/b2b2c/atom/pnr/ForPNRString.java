package com.ccservice.b2b2c.atom.pnr;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;

public class ForPNRString implements Serializable{

	/**
	 * PNR接口
	 * 高亮
	 * @date 2011-11-29
	 * @param args
	 */ 
	public String forPNRString(List<Segmentinfo> listsegment,List<Passenger> listpassenger)throws Exception{
		String strPNRinfo="";//返回值
		
		String pninfo="";
		String fltinfo="";

		//航班信息提取
		int strRoute=0;
		for(Segmentinfo segmentinfo:listsegment)
		{
			strRoute++;
			fltinfo+=strRoute+",";
			fltinfo+=segmentinfo.getStartairport()+",";
			fltinfo+=segmentinfo.getEndairport()+",";
			fltinfo+=segmentinfo.getAircomapnycode()+",";
			fltinfo+=segmentinfo.getFlightnumber()+",";
			fltinfo+=segmentinfo.getCabincode()+",";
			fltinfo+=segmentinfo.getDeparttime();
			fltinfo+=";";
		}
		//乘机人信息提取
		for(Passenger passenger:listpassenger){
			strRoute++;
			pninfo+=passenger.getName()+",";
			pninfo+=passenger.getPtype()+",";
			pninfo+=passenger.getIdnumber()+",";
			pninfo+=passenger.getIdtype()+",";
			pninfo+=strRoute+";";
		}
		java.io.InputStream in = null;
		String url="http://service2.travel-data.cn/ToolsService.asmx/GetPNR?";
		url+="u="+"6ed3fc98f2da5089a6144598a6a320e3";//用户ID
		url+="&Pninfo="+pninfo;//乘机人信息串
		url+="&Fltinfo="+fltinfo;//航班信息串
		java.net.URL Url;
		try {
			Url = new java.net.URL(url);
			 java.net.HttpURLConnection conn = (java.net.HttpURLConnection)
			 Url.openConnection();
			 conn.setDoInput(true);
			 conn.connect();
			 in = conn.getInputStream();
			 SAXBuilder build = new SAXBuilder();
			 Document doc = build.build(in);
			 Element lists = doc.getRootElement();

			 List str=lists.getChildren();
			 /**因为返回数据就三条固定记录 所以就不用循环了 直接取**/
			 Element pnr1=(Element) str.get(0);//得到的是是否有记录，true和false
			 Element pnr2=(Element) str.get(1);//得到的是错误信息
			 Element pnr3=(Element) str.get(2);//得到PNR字符串

			 
			 if(pnr1.getText().equals(true)){
				 strPNRinfo=pnr3.getText();//传入PNR数据
			 }else{
				 strPNRinfo=pnr2.getText(); //返回错误信息
			 }
 
			 in.close();
			 conn.disconnect();
			 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(strPNRinfo+"返回数据内容");
		return strPNRinfo;}
	
	
	public static void main(String[] args) {
		ForPNRString sb=new ForPNRString();
		try {
//			sb.forPNRString("张三,1,125698745874565478,NI,1","1,SZX,PEK,ZH,9959,Y,2011-11-30;");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
