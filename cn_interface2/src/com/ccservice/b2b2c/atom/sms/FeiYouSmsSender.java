package com.ccservice.b2b2c.atom.sms;

import java.sql.Timestamp;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.ymsend.Ymsend;
import com.ccservice.b2b2c.ben.Dnsbarends;

public class FeiYouSmsSender implements SmsSender {

    /**
     * 飞友定制短信接口
     */
    private String username;

    private String password;

    private String ipAddress;

    private String userid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //	/**
    //	 * 飞友短信定制接口
    //	 * @param strMobiles
    //	 * @param Flightdate
    //	 * @param Fno
    //	 * @param Dep
    //	 * @param Arr
    //	 * @param Pname
    //	 * @param Type
    //	 * @return
    //	 */
    //	public int sendSMS(String[] strMobiles,String Flightdate,String Fno,String Dep,String Arr,String Pname,String Type,String cancel,long Orderid)
    //	{
    //		if(strMobiles!=null && !strMobiles.equals(""))
    //		{
    //		try{
    //			int intReturn=0;
    //			java.io.InputStream in = null;
    //			String totalurl="";
    //			totalurl=ipAddress+"?Uid="+userid+"&Username="+username+"&Userpwd="+password;
    //			totalurl+="&Mobile="+strMobiles;
    //			totalurl+="&Flightdate="+Flightdate;
    //			totalurl+="&Fno="+Fno;
    //			totalurl+="&Dep="+Dep;
    //			totalurl+="&Arr="+Arr;
    //			totalurl+="&Pname="+Pname;
    //			totalurl+="&Type="+Type;
    //			if(cancel!=null&&cancel.trim().length()>0){
    //				totalurl+="&Cancel="+cancel;
    //			}
    //			
    //			try {
    //				java.net.URL Url = new java.net.URL(totalurl);
    //				java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
    //				conn.setDoInput(true);
    //				conn.connect();
    //				in = conn.getInputStream();
    //				org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
    //				org.jdom.Document doc = build.build(in);
    //				org.jdom.Element data = doc.getRootElement();
    //				Ymsend ymsend=new Ymsend();
    //				ymsend.setCreatetime(new Timestamp(System.currentTimeMillis()));
    //				ymsend.setOrdercode(Orderid);
    //				ymsend.setPhone(strMobiles);
    //				ymsend.setType(1);				
    //				if(data.getChildTextTrim("MmsOrderNote").equals("订制成功")||data.getChildTextTrim("MmsOrderNote").equals("取消成功"))
    //				{
    //					if(cancel!=null&&cancel.equals("1")){
    //						//取消定制
    ////						String upsql=" UPDATE T_YMSEND SET C_STATE=2 WHERE C_PHONE='"+strMobiles+"' AND C_TYPE=1";
    ////						Server.getInstance().getSystemService().findMapResultBySql(upsql, null);
    //						ymsend.setState(2);
    //						ymsend.setContent("手机号"+strMobiles+"，订单号:"+Orderid+"定制的航班动态短信取消定制，取消成功！");
    //						
    //					}else{
    //						ymsend.setState(0);
    //					ymsend.setContent("手机号"+strMobiles+"，订单号:"+Orderid+"定制的航班动态短信，定制成功！");					
    //					}
    //								
    //					intReturn=1;
    //					Server.getInstance().getMemberService().createYmsend(ymsend);
    //				
    //				
    //				}else{
    //					if(cancel!=null&&cancel.equals("1")){
    //						ymsend.setContent("手机号"+strMobiles+"，订单号:"+Orderid+"定制的航班动态短信取消定制，取消失败！");
    //					}else{					
    //					ymsend.setContent("手机号"+strMobiles+"，订单号:"+Orderid+"定制的航班动态短信，定制失败！");					
    //					}
    //					ymsend.setState(6);
    //					Server.getInstance().getMemberService().createYmsend(ymsend);
    //				}
    //				
    //				in.close();
    //				conn.disconnect();
    //
    //			} catch (Exception e) {
    //				e.printStackTrace();
    //				return -1;
    //			}
    //			return intReturn;
    //		}catch(Exception e)
    //		{
    //			return -1;
    //		}
    //		}
    //		else
    //		{
    //			return -1;
    //		}
    //	}

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public boolean sendSMS(String[] mobiles, String content, long ordercode, long sendagentid, Dnsbarends dns) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public String smsBalanceInquiry() {
        // TODO Auto-generated method stub
        return null;
    }
}
