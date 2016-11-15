package com.ccservice.b2b2c.atom.test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;



public class TestSMS {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args) throws JDOMException, IOException {
		// TODO Auto-generated method stub
	
	/*	for( int a=1;a<=2;a++){//rateType,1,普通 2,高反
			
			for(int b=1;b<=3;b++){//userType  1:散客,2:团队,3:散客或团队
				
				for(int c=1;c<=3;c++){//voyagetype  1单程,2:往返，3:单程或往返
					
					
					final int aa=a;
					final int bb=b;
					final int cc=c;
					
					System.out.println("aa=="+aa+",bb="+bb+",cc=="+cc);
					
				}}}*/
		
/*		String aa="<?xml version='1.0' encoding='GB2312' standalone='yes'?><jinri><is_success>T</is_success><response><OrderInformation>  <OrderItem>    <OrderNo>B10929103251752892</OrderNo>    <OrderStatus>0</OrderStatus>    <RefundType>0</RefundType>    <Pr" +
"oxyerPNo>feiji2010                                         </ProxyerPNo>    <SalesmanPNo>feiji2010                                         </SalesmanPNo>    <PassengerType>1</PassengerType>    <IsChangePnr>1</IsChangePnr>    <Pnr>JN64H1</Pn"+
"r>    <BigPnr>NWXBCX</BigPnr>    <OrderTime>2011-09-29 10:35:10</OrderTime>    <"+
"ParValue>680.0000</ParValue>    <JJRY>190.0000</JJRY>    <RateType>1</RateType>"+
 " <RateRemark>479%bf%aa%cd%b7%b5%c4%c6%b1%ba%c5%ce%aa%c9%ee%ba%bd%b1%be%c6%b1%c"+
"6%b1%ba%c5</RateRemark>    <PassengerCount>1</PassengerCount>    <OutTicketType>"+
"1</OutTicketType>    <RateId>00002291654L</RateId>  </OrderItem><FlightItems>  <"+
"FlightItem>    <Sdate>2011-10-13 07:00:00</Sdate>    <Scity>PEK</Scity>    <Edat"+
"e>2011-10-13 09:10:00</Edate>    <Ecity>SHA</Ecity>    <FlightNo>MU5138    </Fli"+
"ghtNo>    <Cabin>S</Cabin>    <IsReturnFlight>0</IsReturnFlight>    <Terminal>T2"+
"</Terminal>  </FlightItem></FlightItems><PassengerItems>  <PassengerItem>    <Pa"+
"ssengerName>%ba%ab%c3%c9%bb%d4</PassengerName>    <CardType>1</CardType>    <Car"+
"dNO />  </PassengerItem></PassengerItems><ProfitItems>  <ProfitItem>    <UserNam"+
"e>feiji2010                                         </UserName>    <UserType>1</"+
"UserType>    <Discount>6.4000</Discount>    <Amount>827.0000</Amount>  </ProfitI"+
"tem></ProfitItems></OrderInformation></response></jinri>";
		
		SAXBuilder builder = new SAXBuilder();
		
		Document doc = builder.build(new StringReader(aa.trim()));
		Element root = doc.getRootElement();
		
		if (root.getChild("is_success").getTextTrim().equals("T")) {

			Element response = root.getChild("response");
			if (response != null) {
				Element items = response.getChild("OrderInformation")
						.getChild("OrderItem");
				if (items != null) {

				System.out.println(items.getChildTextTrim("OrderNo"));
					

				}

			}

		
		
		}*/
	
		String re="<?xml version='1.0' encoding='GB2312' standalone='yes' ?> "+
						"<jinri>"+
						"<is_success>T</is_success>"+
						"<response>"+
						"<RateItems>"+
						"<RateItem>"+
						"<Cabins>F/C/Y/T/H/L/Q/E/V/X/R/N</Cabins><Aircom>CZ</Aircom><FlightNos>CZ</FlightNos><Scitys>NKG/</Scitys><Ecitys>CAN/</Ecitys><isUse>1</isUse><PayValue>0.00</PayValue><DisCount>15.0</DisCount><WorkTime>09:30-22:30</WorkTime><Efficiency>8</Efficiency><WorkStatus>[空闲]</WorkStatus><TicketsNum>0</TicketsNum><RateType>1</RateType><RateNo>100902000087</RateNo><Remark></Remark><CheckType>1</CheckType><BeginDate>2010-9-6 0:00:00</BeginDate><specialLimitEnumStr></specialLimitEnumStr><TicketType>1</TicketType><PayConfig>1</PayConfig><AgentFee>0</AgentFee>"+
						"</RateItem>"+
						"<RateItem>"+
						"<Cabins>F/C/Y/T/H/L/Q/E/V/X/R/N</Cabins><Aircom>CZ</Aircom><FlightNos>CZ</FlightNos><Scitys>NKG/</Scitys><Ecitys>CAN/</Ecitys><isUse>1</isUse><PayValue>0.00</PayValue><DisCount>17.0</DisCount><WorkTime>09:30-22:30</WorkTime><Efficiency>8</Efficiency><WorkStatus>[空闲]</WorkStatus><TicketsNum>0</TicketsNum><RateType>1</RateType><RateNo>100902000087</RateNo><Remark></Remark><CheckType>1</CheckType><BeginDate>2010-9-6 0:00:00</BeginDate><specialLimitEnumStr></specialLimitEnumStr><TicketType>1</TicketType><PayConfig>1</PayConfig><AgentFee>0</AgentFee>"+
						"</RateItem>"+
						"</RateItems>"+
						"</response>"+
						"</jinri>";

		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(re.trim()));
		Element root = doc.getRootElement();
		if(root.getChild("is_success").getTextTrim().equals("T")){
			
			Element response = root.getChild("response");
			
			Element items = response.getChild("RateItems");
			if (items != null) {

				List<Element> list = items.getChildren("RateItem");
				for (Element e : list) {
					
					System.out.println(e.getChildTextTrim("DisCount"));
				}
				
			}
			/*if(response!=null){
				 Element items = response.getChild("RateItems");
				 System.out.println(items.getContentSize());
				 for(int a=0;a<items.getContentSize();a++){
					 
					 System.out.println(items.getContent(a));
					
					 
					
				 }
				 	

					//JSONObject o = JSONObject.fromObject(response.getValue());
					
					//System.out.println(o.getString("DisCount"));
					
				
				
			}*/
			
		}
		
		
		
		}

}
