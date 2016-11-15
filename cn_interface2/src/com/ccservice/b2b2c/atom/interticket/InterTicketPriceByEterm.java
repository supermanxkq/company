package com.ccservice.b2b2c.atom.interticket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import com.ccservice.b2b2c.atom.component.WriteLog;

import sun.misc.BASE64Decoder;

public class InterTicketPriceByEterm {

	//接口地址
	private String ipAddress;
	public static void main(String[] args) throws Exception {

		 String strCityPair="PEKFCO";
		 String strDate="25JUL";
		 String strAirCompanyCode="CA";
		 
		 String strReturn=XSFSDCmd(strCityPair,strDate,strAirCompanyCode,"").replace("b", "");
		 System.out.println("查询国际机票结束,返回结果："+strReturn);
		 
		 Pattern PricePattern = Pattern.compile("\\n");
		 String[] arrPriceItem = PricePattern.split(strReturn);
		 //取得总页数  RFSONLN/1E /DB1/PAGE 1/6
		 int intpagecount=0;
		 String strPageinfo="";
         //如果最后一行含有PAGE信息则最后一行就是分页信息
		 if(arrPriceItem[arrPriceItem.length-1].indexOf("PAGE")>0)
		 {
			  strPageinfo=arrPriceItem[arrPriceItem.length-1].toString();
		 }
		 else
		 {
			 for(int i=0;i<arrPriceItem.length;i++)
			 {
				 if(arrPriceItem[i].indexOf("RFSONLN")>=0 || arrPriceItem[i].indexOf("PAGE")>=0)
				 {
					 strPageinfo=arrPriceItem[i].toString();
				 }
			 }
		 }
		 if(strPageinfo.length()>0)
		 {
			 String[] arrpage=strPageinfo.split("[P][A][G][E]");
			 for(int i=0;i<arrpage.length;i++){
				 if(arrpage[i].indexOf("/")>=0){
					 String[] arrpagenum=arrpage[i].toString().trim().split("/");
					 if(arrpagenum.length==2)
					 {
						 try
						 {
						   intpagecount=Integer.parseInt(arrpagenum[1]);
						 }
						 catch(Exception ex)
						 {
							 intpagecount=1;
							 System.out.println("取得总页数异常："+ex.getMessage());
						 }
					 }
				 }
			 }
		 }
		 //分页次数
		 String strpageinfo="";
		 for(int p=0;p<intpagecount-1;p++){
			strpageinfo+="$XS FSPN";
		 }
		 String strReturnAll=XSFSDCmd(strCityPair,strDate,strAirCompanyCode,strpageinfo).replace("b", "");
		 Pattern PricePatternAll = Pattern.compile("\\n");
		 String[] arrPriceItemAll = PricePatternAll.split(strReturnAll);
		 //所有价格信息
		 String StrPriceInfo="";
		 for(int i=0;i<arrPriceItemAll.length;i++)
		 {
			 String strreg = "^[0-9]{2}[\\s]{1,}.{1,}";
			 Pattern pattFlight = Pattern.compile(strreg);
			 Matcher mFlight = pattFlight.matcher(arrPriceItemAll[i].trim());
			 if (mFlight.find()) {
				 StrPriceInfo+=arrPriceItemAll[i].trim();
				 if(arrPriceItemAll.length>i+1){
					 //将周期与日期合并一行
					 String strregday="[D][\\s]{1,}[0-9]{1,}";
					 Pattern pattday=Pattern.compile(strregday);
					 Matcher mday=pattday.matcher(arrPriceItemAll[i+1].trim());
					 if(mday.find()){
						 StrPriceInfo+=" "+arrPriceItemAll[i+1].trim()+"\n";
					 }
					 else
					 {
						 StrPriceInfo+="\n";
					 }
				 }
			 }
		 }
		 System.out.println("新价格字符串："+StrPriceInfo);
		 String[] ArrPriceInfo=StrPriceInfo.split("[\\n]");
		 //循环所有价格信息
		 String strPriceinfoAll="";
		 for(int i=0;i<ArrPriceInfo.length;i++){
			 int intindex=i+1;
			 String strSeasoninfo=XSFSN(String.valueOf(intindex));
			 strPriceinfoAll+=ArrPriceInfo[i]+"\n"+strSeasoninfo+"\n\n";
		 }
		 writelog("国际机票黑屏数据"+strCityPair+"-"+strDate+"-"+strAirCompanyCode,strPriceinfoAll);
		 System.out.println(strPriceinfoAll);
	}
	
	
	
	
	//查一条航线 相应航空公司的舱位 
	//xs fsd pekjfk/15jul/ca/x   XS FSD PEKNCE/21JUL/CZ/X
	public static String XSFSDCmd(String CityPair,String strDate,String AirCompanyCode,String strpage)
	{
		// 接口地址
		String strUrl ="http://192.168.0.103:10088/api.aspx";
		// 命令参数
		String strCmd = "?cmd=";
		strCmd+=getBASE64("XS FSD "+CityPair+"/"+strDate+"/"+AirCompanyCode+"/X"+strpage);
		
		String strReturn = "";
		// 接口调用地址和命令参数
		String totalurl = strUrl + strCmd;
		java.io.InputStream in = null;
		try {
			java.net.URL Url = new java.net.URL(totalurl);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			in = conn.getInputStream();
			org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
			org.jdom.Document doc = build.build(in);
			org.jdom.Element data = doc.getRootElement();
			List<Element> muticmdlist = data.getChildren("CMDLIST");
			System.out.println(muticmdlist.size());
			int i = 0;
			for (Element cmdlist : muticmdlist) {
				i++;
				String status = cmdlist.getChildTextTrim("STATUS");
				// 返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
				if (status != null && status.equals("0")) {
					strReturn = "0," + cmdlist.getChildTextTrim("MESSAGE");
				} else {
					if (i == 1) {
						strReturn = "1," + cmdlist.getChildTextTrim("RESPONSE");
					} else {
						strReturn += "\r\n"
								+ cmdlist.getChildTextTrim("RESPONSE");
					}
				}
			}
			in.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturn;
	}
	
	public static String XSFSN(String index)
	{
		// 接口地址
		String strUrl ="http://192.168.0.103:10088/api.aspx";
		// 命令参数
		String strCmd = "?cmd=";
		strCmd+=getBASE64("xsfsn"+index+"//3");
		
		String strReturn = "";
		// 接口调用地址和命令参数
		String totalurl = strUrl + strCmd;
		java.io.InputStream in = null;
		try {
			java.net.URL Url = new java.net.URL(totalurl);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			in = conn.getInputStream();
			org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
			org.jdom.Document doc = build.build(in);
			org.jdom.Element data = doc.getRootElement();
			List<Element> muticmdlist = data.getChildren("CMDLIST");
			System.out.println(muticmdlist.size());
			int i = 0;
			for (Element cmdlist : muticmdlist) {
				i++;
				String status = cmdlist.getChildTextTrim("STATUS");
				// 返回1表示查询成功，并返回黑屏航班信息,否则查询失败并返回，失败原因
				if (status != null && status.equals("0")) {
					strReturn = "0," + cmdlist.getChildTextTrim("MESSAGE");
				} else {
					if (i == 1) {
						strReturn = "1," + cmdlist.getChildTextTrim("RESPONSE");
					} else {
						strReturn += "\r\n"
								+ cmdlist.getChildTextTrim("RESPONSE");
					}
				}
			}
			in.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturn;
	}
	
	
	
	public static String addZero(int i) {

		if (i<10) {

		String tmpString="0"+i;

		return tmpString;

		}

		else {

		return String.valueOf(i);

		}  

		}

	
	
	public static void writelog(String fileNameHead,String logString) {

		try {

		String logFilePathName=null;

		Calendar cd = Calendar.getInstance();//��־�ļ�ʱ��

		int year=cd.get(Calendar.YEAR);

		String month=addZero(cd.get(Calendar.MONTH)+1);

		String day=addZero(cd.get(Calendar.DAY_OF_MONTH));

		String hour=addZero(cd.get(Calendar.HOUR_OF_DAY));

		String min=addZero(cd.get(Calendar.MINUTE));

		String sec=addZero(cd.get(Calendar.SECOND));

		 

		 

		File fileParentDir=new File("D:/userlog");//�ж�logĿ¼�Ƿ����

		if (!fileParentDir.exists()) {

		fileParentDir.mkdir();

		}

		if (fileNameHead==null||fileNameHead.equals("")) {

		logFilePathName="D:/userlog/"+year+month+day+".log";//��־�ļ���

		}else {

		logFilePathName="D:/userlog/"+fileNameHead+year+month+day+".log";//��־�ļ���

		}

		 

		PrintWriter printWriter=new PrintWriter(new FileOutputStream(logFilePathName, true));//�����ļ�βд����־�ַ�

		String time="["+year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec+"] ";

		printWriter.println(time+logString);

		printWriter.flush();

		 

		} catch (FileNotFoundException e) {

		// TODO Auto-generated catch block

		e.getMessage();

		}

		}
	
	
	// 将 s 进行 BASE64 编码
	public static String getBASE64(String s) {
		if (s == null)
			return null;

		String re = (new sun.misc.BASE64Encoder()).encode(s.getBytes());
		return URLEncoder.encode(re);
	}

	// 将 BASE64 编码的字符串 s 进行解码
	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
