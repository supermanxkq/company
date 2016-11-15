package com.pay.config;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.PayInfo;

/**
 * @author Administrator
 *汇付天下支付 需要文件：商户号merid，密钥文件：MerPrK+商户号 例如MerPrK870841.key;
 *
 */
public class Chinapnrconfig extends PayConfig {
	
	
	private Chinapnrconfig(){};
	private static Chinapnrconfig chinapnrconfig=null;
	public static Chinapnrconfig getInstance(){
		if(chinapnrconfig==null){
			chinapnrconfig = new Chinapnrconfig();
			PayInfo payinfo=Server.getInstance().getB2BSystemService().findPayInfoBytypeId(3);			
			chinapnrconfig.setPartnerID(payinfo.getPartnerId());		
			chinapnrconfig.setSelfaccount(payinfo.getAccounttype()==0);
		}
		return chinapnrconfig;
	}
	
	public String partnerID="872193";//惠民兴业 航天华有
 ///  public static  String merid="870841";//商户号 航天华有
	//public static  String merid="871691";//商户号 自由飞翔
	

   public static void main(String[]args){
	   System.out.println(Chinapnrconfig.class.getClassLoader().getResource("/web-inf"));
   }

  
}


