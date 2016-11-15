package com.insurance;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.axis2.AxisFault;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.util.Insurances;
import com.hzins.www.ServiceStub;
import com.hzins.www.ServiceStub.PdfResponse;


public class InsuranceBook implements IInsuranceBook {
	
	/**
	 * 产生随机数
	 * @param num
	 * @param len
	 * @return
	 */
	public String getNumString(long num,int len){
		StringBuffer code =  new StringBuffer(len);
		for(int i=0;i<len ;i++){
			code.append('0');
		}
		String snum = (""+num);
		int slen = snum.length();
		if(slen>len){
			snum = snum.substring(0,len);
		}
		code.replace(len-snum.length(),len,snum);
		
		return code.toString();
	}
	/**
	 *  保险订单创建
	 *  @author 赵晓晓
	 *  @createtime 
	 *  @param jyNo:交易流水号user:担保人，list被担保人(可以担保多个人)
	 * @return
	 */
	
	@Override
	public List<Insurances> orderAplylist(String jyNo,Customeruser user,List list,String begintime,String[] fltno) throws Exception
	{
		ServiceStub stub=new ServiceStub();
		ServiceStub.OrderApply request=new ServiceStub.OrderApply();
		ServiceStub.OrderApplyResponse response=new ServiceStub.OrderApplyResponse();
		//保存返回的信息
		List<Insurances> inlist=new ArrayList<Insurances>();
		//获得系统时间
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//请求参数赋值
		String strParam="";
        //拼参数XML
		StringBuffer orderInfo=new StringBuffer("<?xml version='1.0' encoding='utf-8'?>");
		orderInfo.append("<OrderApplyRequest>\r\n");//订单请求
		orderInfo.append("<CaseCode>2074330010171</CaseCode>\r\n");//方案代码
		orderInfo.append("<TransrNo>"+jyNo+"</TransrNo>\r\n");//交易流水号
		orderInfo.append("<ApplicationDate>"+df.format(d)+"</ApplicationDate>\r\n");//接收时间
		orderInfo.append("<InputDate>"+df.format(d)+"</InputDate>\r\n");//录入时间
		orderInfo.append("<partnerinfo>\r\n");//交易账户信息
		orderInfo.append("<unpartnerID>"+Encrypt("2074")+"</unpartnerID >\r\n");//代理公司加密后的
		orderInfo.append("<partnerID>2074</partnerID>\r\n");//代理公司编号
		orderInfo.append("</partnerinfo>\r\n");
		orderInfo.append("<orderData>\r\n");//订单数据
		orderInfo.append("<planCode>1001</planCode>\r\n	");//险种代码
		orderInfo.append("<productCode>2074331001</productCode>\r\n");//产品代码
		orderInfo.append("<applicationDate>"+df.format(d)+"</applicationDate>\r\n");//投保时间
		orderInfo.append("<beginDate>"+begintime+"</beginDate>\r\n");//起保时间
		//将字符串转换成时间类型
		Date da=df.parse(begintime);
		da.setDate(da.getDate()+10);
		orderInfo.append("<endDate>"+df.format(da)+"</endDate>\r\n");//终保时间
		orderInfo.append("<applicant>\r\n");//投保人
		orderInfo.append("<customerName>"+user.getMembername()+"</customerName >\r\n");//姓名
		if(user.getCardtype()!=null&&!user.getCardtype().equals("")){
			orderInfo.append("<cardType>"+user.getCardtype()+"</cardType>\r\n");//证件号
		}else{
		orderInfo.append("<cardType>3</cardType>\r\n");//证件号
		}
		if(user.getCardnunber()!=null&&!user.getCardnunber().equals("")){
			orderInfo.append("<cardCode>"+user.getCardnunber()+"</cardCode>\r\n");
		}else{
		orderInfo.append("<cardCode>12345678</cardCode>\r\n");
		}
		if(user.getMembersex()!=null){
			int sex=1;
			if(user.getMembersex().equals("女")){
				sex=0;
			}
		orderInfo.append("<sex>"+sex+"</sex>\r\n");
		}
		if(user.getBirthday()!=null&&!user.getBirthday().equals("")){
			orderInfo.append("<birthday>"+user.getBirthday()+"</birthday>\r\n");//出生日期
		}else{
		orderInfo.append("<birthday>1990-12-12</birthday>\r\n");//出生日期
		}
		orderInfo.append("<mobile>"+user.getMobile()+"</mobile>\r\n");//电话
		orderInfo.append("<email>"+user.getMemberemail()+"</email>\r\n");
		orderInfo.append("</applicant>\r\n");
		if(list.size()>0){
		   for(int i=0;i<list.size();i++){
			   Insuruser users=(Insuruser) list.get(i);
			   orderInfo.append("<Insurant>\r\n");
			   //产生随机数
			   long ran = (int) (Math.random() * 99999999 + 1);
			   String number=getNumString(ran,7);
			   orderInfo.append("<UniqueFlag>"+number+"</UniqueFlag>\r\n");//被保人的唯一标示
			   orderInfo.append("<insurantName>"+users.getName()+"</insurantName>\r\n");
			   orderInfo.append("<cardType>"+users.getCodetype()+"</cardType>\r\n");
			   orderInfo.append("<cardCode>"+users.getCode()+"</cardCode>\r\n");
			   String birthday=users.getBirthday().toString().substring(0,10);
			   orderInfo.append("<birthday>"+birthday+"</birthday>\r\n");
			   orderInfo.append("<FLTNo>"+fltno[i]+"</FLTNo>\r\n");
			   orderInfo.append("<city>"+users.getCity()+"</city>\r\n");
			   orderInfo.append("<insurantRelation>1</insurantRelation>\r\n");
			   orderInfo.append("</Insurant>\r\n");
		   }
		}
		orderInfo.append("</orderData>\r\n");
		orderInfo.append("</OrderApplyRequest>");
		request.setOrderrequest(strParam+orderInfo.toString());
		response=stub.orderApply(request);
		String strReturn=response.getOrderApplyResult();
     	//解析返回的信息
		SAXBuilder build = new SAXBuilder();
	    org.jdom.Document document = build.build(new StringReader(strReturn));
	    org.jdom.Element root = document.getRootElement();
	    //流水号
	    String TransrNo=null;
	    if(root.getChildTextTrim("TransrNo")!=null&&!root.getChildTextTrim("TransrNo").equals("")){
		TransrNo=root.getChildTextTrim("TransrNo");
	    }
		//成功标志
	    int Flag=0;
	    if(root.getChildTextTrim("Flag")!=null&&!root.getChildTextTrim("Flag").equals("")){
		Flag=Integer.parseInt(root.getChildTextTrim("Flag"));
	    }
		//返回消息
	    String message=null;
	    if(root.getChildTextTrim("Message")!=null&&!root.getChildTextTrim("Message").equals("")){
	    message=root.getChildTextTrim("Message");
	    }
		Element root1=root.getChild("partnerinfo");
		if(Flag==1){
			//代理公司编号
			String partnerID=root1.getChildTextTrim("partnerID");
			Element root2=root.getChild("orderData");
			List Insurantlist=root2.getChildren("Insurant");
			String applicationNo=null;
			String PolicyCode=null;
			String customerName=null;
			String cardCode=null;
			int Flags=0;
			String messages=null;
			for (Iterator iter = Insurantlist.iterator(); iter.hasNext();) {
				Element root3 = (Element) iter.next();
				//投保单号
				if(root3.getChildTextTrim("applicationNo")!=null&&!root3.getChildTextTrim("applicationNo").equals("")){
				applicationNo=root3.getChildTextTrim("applicationNo");
				}
				//保单号
				if(root3.getChildTextTrim("PolicyCode")!=null&&!root3.getChildTextTrim("PolicyCode").equals("")){
				PolicyCode=root3.getChildTextTrim("PolicyCode");
				}
				//被保人姓名
				if(root3.getChildTextTrim("customerName")!=null&&!root3.getChildTextTrim("customerName").equals("")){
			    customerName=root3.getChildTextTrim("customerName");
				}
				//被保人证件号
				if(root3.getChildTextTrim("cardCode")!=null&&!root3.getChildTextTrim("cardCode").equals("")){
				cardCode=root3.getChildTextTrim("cardCode");
				}
				//交易状态
				if(root3.getChildTextTrim("Flag")!=null&&!root3.getChildTextTrim("Flag").equals("")){
			    Flags=Integer.parseInt(root3.getChildTextTrim("Flag"));
				}
				//交易信息
				if(root3.getChildTextTrim("Message")!=null&&!root3.getChildTextTrim("Message").equals("")){
			    messages=root3.getChildTextTrim("Message");
				}
				System.out.println("交易流水号："+TransrNo);
				System.out.println("成功状态："+Flag);
				System.out.println("消息状态："+message);
				System.out.println("代理公司编号："+partnerID);
				System.out.println("投保单号："+applicationNo);
				System.out.println("保单号："+PolicyCode);
				System.out.println("投保人："+customerName);
				System.out.println("证件号："+cardCode);
			    System.out.println("交易状态："+Flags);
			    System.out.println("交易信息："+messages);
			    //保存返回的信息
				Insurances insurance=new Insurances(Flag,message,TransrNo,partnerID,applicationNo,PolicyCode,customerName,cardCode,Flags,messages);
				inlist.add(insurance);
			}	
		}else{
			//订单没有创建成功
			//代理公司编号
			String partnerID=root1.getChildTextTrim("partnerID");
			Element root2=root.getChild("orderData");
			List Insurantlist=root2.getChildren("Insurant");
			String customerName=null;
			String cardCode=null;
			int Flags=0;
			String messages=null;
			for (Iterator iter = Insurantlist.iterator(); iter.hasNext();) {
				Element root3 = (Element) iter.next();
				//被保人姓名
			    customerName=root3.getChildTextTrim("customerName");
				//被保人证件号
				cardCode=root3.getChildTextTrim("cardCode");
				//交易状态
			    Flags=Integer.parseInt(root3.getChildTextTrim("Flag"));
				//交易信息
			    messages=root3.getChildTextTrim("Message");
			}
			Insurances insurance=new Insurances(Flag,message,TransrNo,partnerID,customerName,cardCode,Flags,messages);
			inlist.add(insurance);
		}
		return inlist;
	}
    /**
     * 电子保险单获取
     * @param no交易流水号，toubaoNo投保单号，baodanNo保单号
     */
	@Override
	public DataHandler PolicyReprint(Insurorder order) throws Exception{
		// TODO Auto-generated method stub
		ServiceStub stub=new ServiceStub();
		ServiceStub.PolicyReprint request=new ServiceStub.PolicyReprint();
		ServiceStub.PolicyReprintResponse response=new ServiceStub.PolicyReprintResponse();
		//请求参数赋值
		//获得系统时间
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String strParam="";
        //拼参数XML
		StringBuffer orderInfo=new StringBuffer("<?xml version='1.0' encoding='utf-8'?>\r\n");
		orderInfo.append("<Reprintrequest>\r\n");
		orderInfo.append("<TransrNo>"+order.getLiushuino()+"</TransrNo>\r\n");
		orderInfo.append("<applicationNo>"+order.getOrderno()+"</applicationNo>\r\n");
		orderInfo.append("<PolicyCode>"+order.getOrderno()+"</PolicyCode>\r\n");
		orderInfo.append("<ApplicationDate>"+order.getTime()+"</ApplicationDate>\r\n");
		orderInfo.append("<InputDate>"+order.getTime()+"</InputDate>\r\n");
		orderInfo.append("<partnerinfo>\r\n");
		orderInfo.append("<unpartnerID>"+Encrypt("2074")+"</unpartnerID>\r\n");
		orderInfo.append("<partnerID>2074</partnerID>\r\n");
		orderInfo.append("</partnerinfo>\r\n");
		orderInfo.append("</Reprintrequest>");
		request.setReprintRequest(strParam+orderInfo);
		response=stub.policyReprint(request);
		PdfResponse strReturn=response.getPolicyReprintResult();
		boolean flay=strReturn.getFlag();
		System.out.println(strReturn.getMessage());
		DataHandler returnpdf=null;
		if(flay==true){
        returnpdf=strReturn.getPdf();
		}
		return returnpdf;
	}
	
	/**
	 * 生成加密后的公司编号
	 * @param str
	 * @return
	 */
	public static String Encrypt(String str){
		String strReturn="";
		java.io.InputStream is = null;
		String totalurl="http://192.168.0.5/Encrypt.aspx?encryptstring="+str;
		try {
			java.net.URL Url = new java.net.URL(totalurl);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			InputStreamReader isr = null;

			// use default characterset
		    isr = new InputStreamReader(is);
		    BufferedReader in = new BufferedReader(isr);
		    StringWriter out = new StringWriter();
		    int c = -1;

		    while ((c = in.read()) != -1) {
		     out.write(c);
		    }
		    strReturn=out.toString();
			in.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturn;
	
	}
	@Override
	public List newOrderAplylist(String[] jyNo, List list) {
		// TODO Auto-generated method stub
		ServiceStub stub=null;
		try {
			stub = new ServiceStub();
		} catch (AxisFault e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		ServiceStub.OrderApply request=new ServiceStub.OrderApply();
		ServiceStub.OrderApplyResponse response=new ServiceStub.OrderApplyResponse();
		//保存返回的信息
		List<Insurances> inlist=new ArrayList<Insurances>();
		//获得系统时间
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//请求参数赋值
		String strParam="";
		//判断多少个人在投保险
        for(int i=0;i<list.size();i++){
		//获取被保人与投保人信息
		Insuruser user=(Insuruser)list.get(i);
		String birthday=user.getBirthday().toString().substring(0,10);
        //拼参数XML
		StringBuffer orderInfo=new StringBuffer("<?xml version='1.0' encoding='utf-8'?>");
		orderInfo.append("<OrderApplyRequest>\r\n");//订单请求
		orderInfo.append("<CaseCode>2074330010171</CaseCode>\r\n");//方案代码
		orderInfo.append("<TransrNo>"+jyNo[i]+"</TransrNo>\r\n");//交易流水号
		orderInfo.append("<ApplicationDate>"+df.format(d)+"</ApplicationDate>\r\n");//接收时间
		orderInfo.append("<InputDate>"+df.format(d)+"</InputDate>\r\n");//录入时间
		orderInfo.append("<partnerinfo>\r\n");//交易账户信息
		orderInfo.append("<unpartnerID>"+Encrypt("2074")+"</unpartnerID >\r\n");//代理公司加密后的
		orderInfo.append("<partnerID>2074</partnerID>\r\n");//代理公司编号
		orderInfo.append("</partnerinfo>\r\n");
		orderInfo.append("<orderData>\r\n");//订单数据
		orderInfo.append("<planCode>1001</planCode>\r\n	");//险种代码
		orderInfo.append("<productCode>2074331001</productCode>\r\n");//产品代码
		orderInfo.append("<applicationDate>"+df.format(d)+"</applicationDate>\r\n");//投保时间
		orderInfo.append("<beginDate>"+user.getBegintime()+"</beginDate>\r\n");//起保时间
		//将字符串转换成时间类型
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp now = user.getBegintime();
		String begintime = sdf.format(now);
		Date da=null;
		try {
			da = df.parse(begintime);
			da.setDate(da.getDate()+10);
			System.out.println(df.format(da));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		orderInfo.append("<endDate>"+df.format(da)+"</endDate>\r\n");//终保时间
		orderInfo.append("<applicant>\r\n");//投保人
		orderInfo.append("<customerName>"+user.getName()+"</customerName >\r\n");//姓名
	    orderInfo.append("<cardType>"+user.getCodetype()+"</cardType>\r\n");//证件号
		orderInfo.append("<cardCode>"+user.getCode()+"</cardCode>\r\n");
		orderInfo.append("<sex>"+user.getSex()+"</sex>\r\n");
		orderInfo.append("<birthday>"+user.getBirthday()+"</birthday>\r\n");//出生日期
		orderInfo.append("<mobile>"+user.getMobile()+"</mobile>\r\n");//电话
		orderInfo.append("<email>"+user.getEmail()+"</email>\r\n");
		orderInfo.append("</applicant>\r\n");
	    orderInfo.append("<Insurant>\r\n");
	    //产生随机数
	    long ran = (int) (Math.random() * 99999999 + 1);
	    String number=getNumString(ran,7);
	    orderInfo.append("<UniqueFlag>"+number+"</UniqueFlag>\r\n");//被保人的唯一标示
		orderInfo.append("<insurantName>"+user.getName()+"</insurantName>\r\n");
		orderInfo.append("<cardType>"+user.getCodetype()+"</cardType>\r\n");
		orderInfo.append("<cardCode>"+user.getCode()+"</cardCode>\r\n");
	    orderInfo.append("<birthday>"+birthday+"</birthday>\r\n");
		orderInfo.append("<FLTNo>"+user.getFlyno()+"</FLTNo>\r\n");
		orderInfo.append("<city>"+user.getCity()+"</city>\r\n");
	    orderInfo.append("<insurantRelation>1</insurantRelation>\r\n");
	    orderInfo.append("</Insurant>\r\n");
		orderInfo.append("</orderData>\r\n");
		orderInfo.append("</OrderApplyRequest>");
		request.setOrderrequest(strParam+orderInfo.toString());
		try {
			response=stub.orderApply(request);
			String strReturn=response.getOrderApplyResult();
			//解析返回的信息
			SAXBuilder build = new SAXBuilder();
		    org.jdom.Document document = build.build(new StringReader(strReturn));
		    org.jdom.Element root = document.getRootElement();
		    //流水号
		    String TransrNo=null;
		    if(root.getChildTextTrim("TransrNo")!=null&&!root.getChildTextTrim("TransrNo").equals("")){
			TransrNo=root.getChildTextTrim("TransrNo");
		    }
			//成功标志
		    int Flag=0;
		    if(root.getChildTextTrim("Flag")!=null&&!root.getChildTextTrim("Flag").equals("")){
			Flag=Integer.parseInt(root.getChildTextTrim("Flag"));
		    }
			//返回消息
		    String message=null;
		    if(root.getChildTextTrim("Message")!=null&&!root.getChildTextTrim("Message").equals("")){
		    message=root.getChildTextTrim("Message");
		    }
			Element root1=root.getChild("partnerinfo");
			if(Flag==1){
				//代理公司编号
				String partnerID=root1.getChildTextTrim("partnerID");
				Element root2=root.getChild("orderData");
				Element root3=root2.getChild("Insurant");
				String applicationNo=null;
				String PolicyCode=null;
				String customerName=null;
				String cardCode=null;
				int Flags=0;
				String messages=null;
				//投保单号
				if(root3.getChildTextTrim("applicationNo")!=null&&!root3.getChildTextTrim("applicationNo").equals("")){
				applicationNo=root3.getChildTextTrim("applicationNo");
				}
				//保单号
				if(root3.getChildTextTrim("PolicyCode")!=null&&!root3.getChildTextTrim("PolicyCode").equals("")){
				PolicyCode=root3.getChildTextTrim("PolicyCode");
				}
				//被保人姓名
				if(root3.getChildTextTrim("customerName")!=null&&!root3.getChildTextTrim("customerName").equals("")){
			    customerName=root3.getChildTextTrim("customerName");
				}
				//被保人证件号
				if(root3.getChildTextTrim("cardCode")!=null&&!root3.getChildTextTrim("cardCode").equals("")){
				cardCode=root3.getChildTextTrim("cardCode");
				}
				//交易状态
				if(root3.getChildTextTrim("Flag")!=null&&!root3.getChildTextTrim("Flag").equals("")){
			    Flags=Integer.parseInt(root3.getChildTextTrim("Flag"));
				}
				//交易信息
				if(root3.getChildTextTrim("Message")!=null&&!root3.getChildTextTrim("Message").equals("")){
			    messages=root3.getChildTextTrim("Message");
				}
				System.out.println("交易流水号："+TransrNo);
				System.out.println("成功状态："+Flag);
				System.out.println("消息状态："+message);
				System.out.println("代理公司编号："+partnerID);
				System.out.println("投保单号："+applicationNo);
				System.out.println("保单号："+PolicyCode);
				System.out.println("投保人："+customerName);
				System.out.println("证件号："+cardCode);
			    System.out.println("交易状态："+Flags);
			    System.out.println("交易信息："+messages);
			    //保存返回的信息
				Insurances insurance=new Insurances(Flag,message,TransrNo,partnerID,applicationNo,PolicyCode,customerName,cardCode,Flags,messages);
				inlist.add(insurance);
			}else{//创建失败
				//订单没有创建成功
				//代理公司编号
				String partnerID=root1.getChildTextTrim("partnerID");
				Element root2=null;
				if(root.getChild("orderData")!=null&&!root.getChild("orderData").equals("")){
				root2=root.getChild("orderData");
				}
				Element root3=null;
				if(root2.getChild("Insurant")!=null&&!root2.getChild("Insurant").equals("")){
				root3=root2.getChild("Insurant");
				}
				String customerName=null;
				String cardCode=null;
				int Flags=0;
				String messages=null;
					//被保人姓名
				    if(root3.getChildTextTrim("customerName")!=null&&!root3.getChildTextTrim("customerName").equals("")){
				    customerName=root3.getChildTextTrim("customerName");
				    }
					//被保人证件号
				    if(root3.getChildTextTrim("cardCode")!=null&&!root3.getChildTextTrim("cardCode").equals("")){
					cardCode=root3.getChildTextTrim("cardCode");
				    }
					//交易状态
				    if(root3.getChildTextTrim("Flag")!=null&&!root3.getChildTextTrim("Flag").equals("")){
				    Flags=Integer.parseInt(root3.getChildTextTrim("Flag"));
				    }
					//交易信息
				    if(root3.getChildTextTrim("Message")!=null&&!root3.getChildTextTrim("Message").equals("")){
				    messages=root3.getChildTextTrim("Message");
				    }
				Insurances insurance=new Insurances(Flag,message,TransrNo,partnerID,customerName,cardCode,Flags,messages);
				inlist.add(insurance);
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
		
		return inlist;
	}


	
	public static void main(String[] args) {
		InsuranceBook book = new InsuranceBook();
		// /**
		// * 测试OrderAply()方法
		// */
		// //获得投保人
		// Customeruser
		// user=Server.getInstance().getMemberService().findCustomeruser(90893);
		// Customeruser
		// users=Server.getInstance().getMemberService().findCustomeruser(90901);
		// Customeruser
		// user2=Server.getInstance().getMemberService().findCustomeruser(90883);
		// List<Customeruser> list=new ArrayList();
		// list.add(users);
		// list.add(user2);
		// Date d = new Date();
		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		// d.setDate(d.getDate()+10);
		// System.out.println(df.format(d));
		// String [] no={"W_20","w_12"};
		// System.out.println(Encrypt("123456789"));
		// try {
		// //String message=book.OrderAply("WZ20110922000001",user, list,
		// "2011-11-30", no);
		// //System.out.println(message);
		// } catch (Exception e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// }
		/**
		 * 测试PolicyReprint()方法
		 */
		try {
			Insurorder order = new Insurorder();
			order.setLiushuino("201112209119389I");
			order.setOrderno("OL0000001053");
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			order.setTime(new Timestamp(format.parse("2011-12-20 11:06:45.563")
					.getTime()));
			book.PolicyReprint(order);
			// System.out.println(aa.getFlag());
			// System.out.println(aa.getMessage());
			// DataHandler bb=aa.getPdf();
			// System.out.println(aa.getPdf().getAllCommands());
			// System.out.println(bb.getContentType());
			// System.out.println(bb.getName());
			// System.out.println(bb.getPreferredCommands());
			// aa.getPdf().getInputStream();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public String cancelOrderAplylist(Insuruser insur) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List saveTrainOrderAplylist(String[] jyNo, List list, int type) {
		// TODO Auto-generated method stub
		return null;
	}
}
