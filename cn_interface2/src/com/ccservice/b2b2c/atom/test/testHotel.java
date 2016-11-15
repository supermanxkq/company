//package com.ccservice.b2b2c.atom.test;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.StringReader;
//import java.rmi.RemoteException;
//import java.util.List;
//
//import javax.xml.rpc.ServiceException;
//
//import jxl.Workbook;
//import jxl.write.Label;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//import jxl.write.WriteException;
//import jxl.write.biff.RowsExceededException;
//
//import org.dom4j.DocumentException;
//import org.jdom.Document;
//import org.jdom.Element;
//import org.jdom.JDOMException;
//import org.jdom.input.SAXBuilder;
//
//import com.bizexpress.hotel.AgentWebService;
//
//public class testHotel {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		com.bizexpress.hotel.AgentWebServiceServiceLocator lo=new com.bizexpress.hotel.AgentWebServiceServiceLocator();
//		try {
//			File file=new File("d://hotel.xls");
//			WritableWorkbook workbook=Workbook.createWorkbook(file); 
//			WritableSheet sheet = workbook.createSheet("Hotel", 0); 
//			Label label0 = new Label(0, 0, "ID");    
//			sheet.addCell(label0); 
//			Label label1 = new Label(1, 0, "名称");    
//			sheet.addCell(label1);
//			int size=800;
//			for(int i=80;i<=120;i++)
//			{
//				try{
//				AgentWebService ss=lo.getAgentWebService();
//				String strXML=ss.searchHotel("<Request><City>CNSNPM0</City><CheckInDate>2010-07-27</CheckInDate><CheckOutDate>2010-07-30</CheckOutDate><RoomQuantity>"+i+"</RoomQuantity><LowPrice></LowPrice><HighPrice></HighPrice><HotelRating>ALL</HotelRating><HotelName></HotelName><HotelAddress></HotelAddress><HotelDistrict></HotelDistrict><PageNum>"+i+"</PageNum><PageSize>10</PageSize><CompanyId>1200000231</CompanyId></Request>");
//				System.out.println(strXML);
//				SAXBuilder build = new SAXBuilder();
//				Document document = build.build(new StringReader(strXML));			
//				Element root = document.getRootElement();
//				List<Element> listresponse = ((Element)root.getChildren("Hotels").get(0)).getChildren("Hotel");
//				for(int x=0;x<listresponse.size();x++)
//				{
//					Element e=listresponse.get(x);
//					Label labelid = new Label(0, size+1, e.getChildText("HotelId"));    
//			        sheet.addCell(labelid); 
//			        Label labelcity = new Label(1, size+1, e.getChildText("HotelName"));    
//			        sheet.addCell(labelcity); 
//			        size++;
//			        System.out.println(size+"--"+e.getChildText("HotelId")+"--"+e.getChildText("HotelName"));
//				}
//				}catch (Exception e) {
//					System.out.println(e.getLocalizedMessage());
//					// TODO: handle exception
//				}
//			}
//			workbook.write();    
//	        workbook.close();
//			System.out.println(size);
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RowsExceededException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (WriteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
