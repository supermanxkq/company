package com.zhuna.hotel.test;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.chaininfo.Chaininfo;
import com.ccservice.b2b2c.base.service.IHotelService;




public class readchaininfo {


	public static void main(String[] args) throws Exception{
			String url = "http://localhost:8080/cn_service/service/";
    	
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			String response="";		
						try{
							response=getDateString();
							
						} catch (Exception e) {
							// TODO: handle exception
				
							}
						
						    System.out.println("response=="+response);
							Document document = DocumentHelper.parseText(response);
							
							Element root = document.getRootElement();
						
							List<Element> list = root.elements("liansuo");
							System.out.println(list.size());
							for(int a=0;a<list.size();a++){
								Chaininfo chaininfo = new  Chaininfo();
								
								System.out.println("name=="+list.get(a).attributeValue("name"));
								List<Chaininfo>listChaininfo=servier.findAllChaininfo(" where 1=1 and "+Chaininfo.COL_name+" ='"+list.get(a).attributeValue("name")+"'", "", -1, 0);
								System.out.println("listChaininfo=="+listChaininfo.size());
								if(listChaininfo.size()>0){
									
									chaininfo=listChaininfo.get(0);
								}
								chaininfo.setName(list.get(a).attributeValue("name"));
								chaininfo.setDescription(list.get(a).attributeValue("id"));//连锁酒店code
								chaininfo.setTotal(list.get(a).attributeValue("num"));//酒店数量
								chaininfo.setImagepic(list.get(a).attributeValue("picurl"));
								
								if(listChaininfo.size()>0){
									
									servier.updateChaininfoIgnoreNull(chaininfo);
									System.out.println("update=="+chaininfo);
								}else{
									
									servier.createChaininfo(chaininfo);
									System.out.println("add=="+chaininfo);
								}
								
							}
							
							
						 
						
				
			
		
			
			System.out.println("总算循环完了,");
			
			
			
			
			
			
	
		
	}
	public static String getDateString()
	{
		
		String urltemp="http://un.zhuna.cn/api/gbk/chain.asp?u=831713&m=54fee3e2184ce526";
		URL url;
		try {
			url = new URL(urltemp);
			URLConnection connection = url.openConnection();  
			connection.setDoOutput(true);  
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.flush();  
		    out.close();  
		    String sCurrentLine;  
		    String sTotalString;  
		    sCurrentLine = "";  
		    sTotalString = "";  
		    InputStream l_urlStream;  
		    l_urlStream = connection.getInputStream();  
		    BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));  
		    while ((sCurrentLine = l_reader.readLine()) != null) {  
		    sTotalString += sCurrentLine + "\r\n";  
		    }
		    return sTotalString;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return "";
	}
	 public static void download(String urlString, String filename) throws Exception {    
		    // 构造URL    
		    URL url = new URL(urlString);    
		    // 打开连接    
		    URLConnection con = url.openConnection();   
		    // 输入流    
		    InputStream is = con.getInputStream();   
		  
		    // 1K的数据缓冲    
		    byte[] bs = new byte[1024];    
		    // 读取到的数据长度    
		    int len;    
		    // 输出的文件流    
		    OutputStream os = new FileOutputStream(filename);    
		    // 开始读取    
		    while ((len = is.read(bs)) != -1) {    
		      os.write(bs, 0, len);    
		   }   
		    // 完毕，关闭所有链接    
		    os.close();   
		    is.close();   
		  }  


}

