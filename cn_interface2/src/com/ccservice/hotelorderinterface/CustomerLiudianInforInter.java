package com.ccservice.hotelorderinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.hotelpricecontroltype.HotelPriceControlType;
import com.ccservice.b2b2c.base.hpricecontrolrecord.HpriceControlRecord;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
/**
 * 
 * @author wzc
 *  查询留点政策
 */
public class CustomerLiudianInforInter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unchecked")
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置编码方式
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		String line = "";
		StringBuffer buffer = new StringBuffer(1024);
		while ((line = br.readLine()) != null) {
			buffer.append(line);
		}
		String username="";
		String method="";
		String pwd="";
		String busstype="";
		try {
			 JSONObject obj=JSONObject.fromObject(buffer.toString());
			 username=obj.getString("username").toString();
			 pwd=obj.getString("pwd").toString();
			 method=obj.getString("method").toString();
			 if(method != null && method.equals("liudian")){
				 busstype=obj.getString("busstype").toString();
			 } 
		} catch (Exception e) {
			 username=request.getParameter("username");
			 method=request.getParameter("method");
			 pwd=request.getParameter("pwd");
			 if(method != null && method.equals("liudian")){
				 busstype=request.getParameter("busstype");
			 }
		}
		
		if(method!=null&&!"".equals(method)){
			if(method.equals("liudian")){
				List<Customeruser> users=Server.getInstance().getMemberService().findAllCustomeruser("where C_LOGINNAME='"+username.trim()+"'", "", -1, 0);
				if(users.size()==1){
					String sql="SELECT LI.C_FANDIANSTART as start ,LI.C_FANDIANEND as endt ,C_LIUDIAN as liudian,S.C_AGENTID AS liudianagent," +
					"LR.C_AGENTID as agentid,S.C_LIUDIANID as hotelmode FROM T_LIUDIANRECORD LI "
					+" LEFT JOIN T_LIUDIANREFINFO LR ON  LR.C_TYPEID=LI.C_TYPEID"
					+" LEFT JOIN T_CUSTOMERAGENT C ON C.ID=LR.C_AGENTID "
					+" LEFT JOIN T_SETTLEMENTTYPE S ON S.ID=LR.C_TYPEID "
					+" WHERE 1=1 and LR.C_BUSSTYPE="+busstype+" and ( CHARINDEX(','+CONVERT(NVARCHAR,C.ID)+',',','+(SELECT C_PARENTSTR FROM T_CUSTOMERAGENT " +
					"WHERE ID="+users.get(0).getAgentid()+")+',')>0"
					+" OR C.ID="+users.get(0).getAgentid()+") order by LR.C_AGENTID";
					List list=Server.getInstance().getSystemService().findMapResultBySql(sql, null);
					sb.append(JSONArray.fromObject(list));
				}
			}else if(method.equals("jiajia")){
				List<Customeruser> users=Server.getInstance().getMemberService().findAllCustomeruser("where C_LOGINNAME='"+username.trim()+"'", "", -1, 0);
				if(users.size()==1){
					List<HotelPriceControlType> type=Server.getInstance().getHotelService().findAllHotelPriceControlType("where c_status=1", "", -1, 0);
					if(type!=null&&type.size()==1){
						List<HpriceControlRecord> record=Server.getInstance().getHotelService().findAllHpriceControlRecord("where c_typeid="+type.get(0).getId(), "", -1, 0);
						sb.append("{type:"+type.get(0).getType()+",data:");
						sb.append(JSONArray.fromObject(record));
						sb.append("}");
					}
				}
			}else if("elFianDian".equals(method)){
				List<Customeruser> users=Server.getInstance().getMemberService().findAllCustomeruser("where C_LOGINNAME='"+username.trim()+"'", "", -1, 0);
				if(users.size()==1){
					Double fandian=7d;
					List<Sysconfig> config=Server.getInstance().getSystemService().findAllSysconfig("where c_name='xifudian'", "", -1, 0);
					if(config!=null&&config.size()>0){
						fandian = Double.valueOf(config.get(0).getValue());
					}
					sb.append("{elFianDian:"+fandian+"}");
				}
			}else if("Hotelspecialpoint".equals(method)){
				List<Customeruser> users=Server.getInstance().getMemberService().findAllCustomeruser("where C_LOGINNAME='"+username.trim()+"'", "", -1, 0);
				if(users.size()==1){
					Double fandian=7d;
					List<Sysconfig> config=Server.getInstance().getSystemService().findAllSysconfig("where c_name='Hotelspecialpoint'", "", -1, 0);
					if(config!=null&&config.size()>0){
						fandian = Double.valueOf(config.get(0).getValue());
					}
					sb.append("{Hotelspecialpoint:"+fandian+"}");
				}
			}
		}
		out.write(sb.toString());
		out.flush();
		out.close();
	}
}
