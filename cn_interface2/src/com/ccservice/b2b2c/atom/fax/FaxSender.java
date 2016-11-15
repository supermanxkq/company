package com.ccservice.b2b2c.atom.fax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.uniproud.axis2.client.FaxServiceClient;
import com.uniproud.axis2.client.ServiceXMLAnalysis;
import com.uniproud.axis2.client.ToServerXML;



public class FaxSender {
	private String userID;
	private String password;
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 
	 * 发送传真
	 * 
	 */
	public int sendfax(String faxnum,String faxstr) {
		FaxServiceClient client = new FaxServiceClient();
		
		 String sendFaxToBack = client.SendReceive(ToServerXML.getSendFaxToClientXML(userID,password,"01068179400",faxstr),"urn:SendFaxToServer");                   
	     System.out.println("普通发送SendFaxToServer方法的反馈信息xml："+sendFaxToBack);                                            
	     ServiceXMLAnalysis.getSendFaxToBack(sendFaxToBack);//解析普通发送反馈信息 
		return 1;
	}
	/**
	 * 创建发送传真
	 * @return
	 */
	public String getHotelTemple(Map<String, String> map)
	{
		
		String fileStr="";
		String url = map.get("newfax").toString();
//		Util.copyfile(new File(map.get("faxtemple").toString()), new File(url));
			File f = new File(map.get("faxtemple").toString());
			FileInputStream fin;
			try {
				fin = new FileInputStream(map.get("faxtemple").toString());
				InputStreamReader fileIn = new InputStreamReader(fin, "UTF-8");
				BufferedReader infm = new BufferedReader(fileIn);
				String s = "";
				while (true) {
					s = infm.readLine();
					if (s == null)
						break;
					fileStr=fileStr+s;
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(fileStr.toString());
			System.out.println(fileStr.indexOf("%rname%"));
			fileStr=fileStr.replaceAll("%rname%", map.get("rname").toString());
			fileStr=fileStr.replaceAll("%rphone%", map.get("rphone").toString());
			fileStr=fileStr.replaceAll("%rfax%", map.get("rfax").toString());
			fileStr=fileStr.replaceAll("%sname%", map.get("sname").toString());
			fileStr=fileStr.replaceAll("%sphone%", map.get("sphone").toString());
			fileStr=fileStr.replaceAll("%sfax%", map.get("sfax").toString());
			fileStr=fileStr.replaceAll("%senddate%", map.get("senddate").toString());
			fileStr=fileStr.replaceAll("%hotelname%", map.get("hotelname").toString());
			fileStr=fileStr.replaceAll("%countty%", map.get("countty").toString());
			fileStr=fileStr.replaceAll("%peoplenum%", map.get("peoplenum").toString());
			fileStr=fileStr.replaceAll("%order%", map.get("order").toString());
			fileStr=fileStr.replaceAll("%name%", map.get("name").toString());
			fileStr=fileStr.replaceAll("%begindate%", map.get("begindate").toString());
			fileStr=fileStr.replaceAll("%enddate%", map.get("enddate").toString());
			fileStr=fileStr.replaceAll("%roommun%", map.get("roommun").toString());
			fileStr=fileStr.replaceAll("%breakfast%", map.get("breakfast").toString());
			fileStr=fileStr.replaceAll("%price%", map.get("price").toString());
			fileStr=fileStr.replaceAll("%content%", map.get("content").toString());
			fileStr=fileStr.replaceAll("%paymoney%", map.get("paymoney").toString());
			fileStr=fileStr.replaceAll("%makename%", map.get("makename").toString());
			System.out.println(fileStr);
			try {
				FileOutputStream fos = new FileOutputStream(new File(url));
				Writer out = new OutputStreamWriter(fos, "UTF-8");
				out.write(fileStr);
				out.close();
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{   
             String url2 = new File(url).toURI().toURL().toString();     
             String outputFile = map.get("newfaxpdf").toString();
             OutputStream os = new FileOutputStream(outputFile);     
             ITextRenderer renderer = new ITextRenderer();     
             renderer.setDocument(url2);     
  
             // 解决中文支持问题     
             ITextFontResolver fontResolver = renderer.getFontResolver();     
             fontResolver.addFont("C:/Windows/Fonts/SIMSUN.TTC", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);     
  
             // 解决图片的相对路径问题     
//             renderer.getSharedContext().setBaseURL("file:/D:");     
                  
             renderer.layout();     
             renderer.createPDF(os);     
                  
             os.close();    
			}catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return map.get("newfaxpdf").toString();
	}
}
