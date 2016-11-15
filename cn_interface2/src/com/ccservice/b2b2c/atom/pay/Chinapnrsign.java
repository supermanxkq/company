package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chinapnr.SecureLink;

import com.pay.config.Chinapnrconfig;

/**
 * @author hanmenghui
 * 汇付天下签约
 * 接口功能说明：用于在平台上绑定供应商在汇付的账户
 *
 */
@SuppressWarnings("serial")
public class Chinapnrsign extends HttpServlet {
	
	
	private final String gateurl="http://mas.chinapnr.com/gau/UnifiedServlet";
	private final String Version="10";//版本号
	private final String MerId=Chinapnrconfig.getInstance().getPartnerID();//商户号 由钱管家系统分配的6位数字代码，商户的唯一标识
	private final String CmdId="Sign";//消息类型 每一种消息类型代表一种交易
	private final DateFormat dateformat=new SimpleDateFormat("yyyyMMdd");
	private final DateFormat timeformat=new SimpleDateFormat("HHmmss");
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		
		String UsrId=request.getParameter("usrid");
		Date date=new Date();
		String MerDate=dateformat.format(date);//MerDate商户日期定长8位格式为：YYYYMMDD
		String MerTime=timeformat.format(date);// 商户时间 定长8位 格式为：HHMMSS
		String BgRetUrl="http://"+request.getServerName()+":"+request.getServerPort()+"/cn_interface/";
		
		//签名
		String 	MerKeyFile	= request.getSession().getServletContext().getRealPath("/") + "MerPrK"+MerId+".key";			//商户私钥文件路径  请将MerPrK510010.key改为你的私钥文件名称
		String	MerData = Version + CmdId + MerId + UsrId + MerDate + MerTime + BgRetUrl;
		System.out.println(MerData);
		SecureLink sl=new SecureLink();
		String ChkValue="";
		int ret=sl.SignMsg(MerId,MerKeyFile,MerData);
		if (ret != 0) 
		{
			System.out.println("签名错误 ret=" + ret );
			return ;
		}

		ChkValue = sl.getChkValue( );
		
		Map<String,String> map=new HashMap<String,String>();
		map.put("Version", Version);
		map.put("CmdId", CmdId);
		map.put("MerId", MerId);
		map.put("UsrId", UsrId);
		map.put("MerDate",MerDate);
		map.put("MerTime",MerTime);
		map.put("BgRetUrl",BgRetUrl);
		map.put("ChkValue",ChkValue);
		String param=createLinkString(map);
		try {
			response.sendRedirect(gateurl+"?"+param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if(value.length()==0){
				continue;
			}
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
        System.out.print("拼接后的字符串："+prestr);
		return prestr;
	}

}
