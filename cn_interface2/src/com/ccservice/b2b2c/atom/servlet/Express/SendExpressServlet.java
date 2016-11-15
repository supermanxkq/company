package com.ccservice.b2b2c.atom.servlet.Express;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.tenpay.util.MD5Util;

/**
 * 快递二道贩子
 */
@WebServlet("/SendExpressServlet")
public class SendExpressServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SendExpressServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("UTF-8");
	    String timeStamp=request.getParameter("reqtime");
        String partnerName=request.getParameter("partnerid");
        String messageIdentity=request.getParameter("sign");
        
        String gname = request.getParameter("gname");
        String gaddress = request.getParameter("gaddress");
        String gmobile = request.getParameter("gmobile");
        //***********************************
        String ordernumber = request.getParameter("ordernumber");//平台订单号
        String address = request.getParameter("address");//收件人地址
        String sname = request.getParameter("sname");//收件人名字
        String smobuile = request.getParameter("smobuile");//收件人手机号
        
        String expresstype=request.getParameter("expresstype");// 发送快递类型     1sf   2jd
        int extype=Integer.valueOf(expresstype);
        String companyname=request.getParameter("companyname");// //request.getParameter("companyname");//发件所属公司名称
        
        String ordernum=ordernumber;
        String j_company = companyname;//sf
        String j_name = gname;
        String j_tel = gmobile;
        String j_address = gaddress;
        String d_name = sname;
        String d_tel = smobuile;
        String d_address = address;
        String sql = "SELECT keys from TrainOfflineAgentKey where partnerName='"+partnerName+"'";
        List list=null;
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        JSONObject responsejson =new JSONObject();
        String key="";
        String code="";
        String msg="";
        boolean success=false;
        if(list.size()>0){
            Map map=(Map)list.get(0);
            key=map.get("keys").toString();
           String sign = partnerName  + timeStamp + MD5Util.MD5Encode(key, "utf-8").toUpperCase();
            sign = MD5Util.MD5Encode(sign, "utf-8").toUpperCase();
            if(messageIdentity.equals(sign)){//验证通过
                    if (extype==1) {
                            WriteLog.write("线下票__快递下单jdsf", "param:"+j_company+j_name+j_tel+j_address+d_name+d_tel+d_address);
                            try {
                                j_company = java.net.URLEncoder.encode(j_company, "UTF-8");
                                j_name = java.net.URLEncoder.encode(j_name, "UTF-8");
                                j_address = java.net.URLEncoder.encode(j_address, "UTF-8");
                                d_name = java.net.URLEncoder.encode(d_name, "UTF-8");
                                d_address = java.net.URLEncoder.encode(d_address, "UTF-8");
                            }
                            catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            String sfurlString ="http://121.40.241.126:9038/SfExpressHthy/OrderServiceServlet";//
                            String param="orderid="+ordernum+"&j_company="+j_company+"&j_contact="+j_name+"&j_tel="+j_tel+"&j_address="+j_address+"&d_company="+d_name+"&d_contact="+d_name+"&d_tel="+d_tel+"&d_address="+d_address;
                            String result=SendPostandGet.submitPost(sfurlString, param,
                                    "UTF-8").toString();
                            WriteLog.write("线下票__快递下单jdsf", "sf  param:"+param+"-->result:"+result);
                            String sb1="0";
                            try {
                                Document document = DocumentHelper.parseText(result);
                                Element root = document.getRootElement();
                                Element body=root.element("Body");
                                Element orderResponse=body.element("OrderResponse");
                                if ("OK".equals(root.elementText("Head"))) {
                                    sb1=orderResponse.attributeValue("mailno");
                                    responsejson.put("expressnum", sb1);//运单号
                                    success=true;
                                    code="4";
                                }else{
                                    code="2";
                                    msg="下单失败请重试!"; 
                                }
                            }
                            catch (DocumentException e1) {
                                e1.printStackTrace();
                            }
                    }else if (extype==2) {
                        String orderid = "";//request.getParameter("orderid");//运单号    jd  数据库中获取
                        String sql1 = "select top 1 * from JDEXPRESSNUM where status=0 order by createtime asc";
                        List list1 = Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
                        if (list1.size()>0) {
                            Map map1=(Map)list1.get(0);
                            orderid = map1.get("EXPRESSNUM").toString();//京东运单单号
                            String jdurl = "http://120.26.100.206:12345/JD/jdsubmitorder";
                            try {
                                address = java.net.URLEncoder.encode(address, "UTF-8");
                                sname = java.net.URLEncoder.encode(sname, "UTF-8");
                                gname = java.net.URLEncoder.encode(gname, "UTF-8");
                                gaddress = java.net.URLEncoder.encode(gaddress, "UTF-8");
                            }
                            catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            String jdparam = "address="+address
                                    + "&orderid="+orderid
                                    + "&ordernumber="+ordernumber
                                    + "&sname="+sname
                                    + "&smobuile="+smobuile
                                    + "&gname="+gname
                                    + "&gaddress="+gaddress
                                    + "&gmobile="+gmobile;
                            WriteLog.write("线下票__快递下单jdsf", "url:"+jdurl+",param:"+j_company+j_name+j_tel+j_address+d_name+d_tel+d_address);
                            String jdresultjson = SendPostandGet.submitPost(jdurl, jdparam, "UTF-8").toString();
                            String sql11 = "update JDEXPRESSNUM set status = 1 where EXPRESSNUM = '"+orderid+"'";
                            Server.getInstance().getSystemService().findMapResultBySql(sql11, null);
                            JSONObject jj=JSONObject.parseObject(jdresultjson);
                            JSONObject jjj=jj.getJSONObject("jingdong_etms_waybill_send_responce");//baseAreaServiceResponse
                            JSONObject jjjj=jjj.getJSONObject("resultInfo");
                            String jdcode=jjjj.getString("code");
                            if (Integer.valueOf(jdcode)==100) {
                                String deliveryId=jjjj.getString("deliveryId");
                              responsejson.put("expressnum", deliveryId);//运单号
                                success=true;
                                code="4";
                            }else{
                                msg=jjjj.getString("message");
                                code="103";
                            }
                        }else{
                            code="102";
                            msg="库内暂无jd运单号，请等待!";
                        }
                }
            }else{
                code="1";
                msg="账号核验失败！";
            }
        }else {
            code="1";
            msg="账号核验失败！";   
        }   
      responsejson.put("ordernumber", ordernumber);
      responsejson.put("success", success);
      responsejson.put("code", code);
      responsejson.put("msg", msg);
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json; charset=utf-8");
      PrintWriter out = null;
      try {
          out = response.getWriter();
          out.print(responsejson.toString());
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          if (out != null) {
              out.close();
          }
      } 
	}

}
