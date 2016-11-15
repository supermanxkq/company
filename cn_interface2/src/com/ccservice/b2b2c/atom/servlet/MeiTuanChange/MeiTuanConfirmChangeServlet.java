package com.ccservice.b2b2c.atom.servlet.MeiTuanChange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.MeiTuanChange.Method.GetReqTokenByResignId;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengConfirmChange;
import com.ccservice.elong.inter.PropertyUtil;

@SuppressWarnings("serial")
public class MeiTuanConfirmChangeServlet extends HttpServlet {

	private final String logname = "meituan美团_确认改签";

	private final String errorlogname = "meituan美团_确认改签_error";

	private final int r1 = new Random().nextInt(10000000);

	private TongChengConfirmChange tongChengConfirmChange;
	
	@Override
	public void init() throws ServletException {
		super.init();
		tongChengConfirmChange = new TongChengConfirmChange();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        String result = "";
        String param = "";
        try {
        	out = resp.getWriter();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            String line ="";
            StringBuffer buffer = new StringBuffer(1024);
            if((line=bufferedReader.readLine())!=null){
                buffer.append(line);
            }
            param = buffer.toString();
            bufferedReader.close();
            WriteLog.write(logname, r1 + "-->请求参数:" + param);
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "传入的json为空对象");
                result = obj.toString();
            } else {
            	try {
            		JSONObject json = JSONObject.parseObject(param);
            		String partnerid = json.containsKey("partnerid") ? json.getString("partnerid") : "";
                	String key = getKeyByPartnerid(partnerid);
                	//请求时间
                	String reqtime = json.containsKey("reqtime") ? json.getString("reqtime") : "";
                    //数字签名
                    String sign = json.containsKey("sign") ? json.getString("sign") : "";
                    //请求方法
                    String method = json.containsKey("method") ? json.getString("method") : "";
                    WriteLog.write(logname, r1 + ":Key:" + key);
                    key = ElongHotelInterfaceUtil.MD5(key);
                    WriteLog.write(logname, r1 + ":Key1:" + key);
                    String signflag = partnerid +GetReqTokenByResignId.Method.CONFIRM_RESIGN + reqtime + key;
                    WriteLog.write(logname, r1 + ":signflag:" + signflag);
                    signflag = ElongHotelInterfaceUtil.MD5(signflag);
                    WriteLog.write(logname, r1 + ":signflag1:" + signflag+"method:"+method);
                    if (signflag.equalsIgnoreCase(sign)) {
                    	String orderid = json.containsKey("orderId") ? json.getString("orderId") : "";
                    	String transactionid = json.containsKey("agentOrderId") ? json.getString("agentOrderId") : "";
                    	String reqtoken = json.containsKey("resignId") ? json.getString("resignId") : "";
                    	JSONObject resultJson = new JSONObject();
						if (!ElongHotelInterfaceUtil.StringIsNull(orderid)
								&& !ElongHotelInterfaceUtil
										.StringIsNull(transactionid)
								&& !ElongHotelInterfaceUtil
										.StringIsNull(reqtoken)) {
							String callbackurl = PropertyUtil.getValue("MeiTun_ChangeConfirm", "Train.properties");
							//拼接成我们需要的参数
							JSONObject reqobj = new JSONObject();
							reqobj.put("orderid", orderid);
							reqobj.put("transactionid", transactionid);
							reqobj.put("isasync", "Y");
							reqobj.put("reqtoken", reqtoken);
							reqobj.put("callbackurl", callbackurl);
						
							result = tongChengConfirmChange.operate(reqobj, r1);	//调用我们的接口
							JSONObject resJson = new JSONObject();
							resJson = JSONObject.parseObject(result);
							 WriteLog.write(logname, r1 + ":resJson:" + result);
							String msg =resJson.containsKey("msg")?resJson.getString("msg"):"";
							if(resJson.containsKey("success")&&resJson.getBoolean("success")){
							    JSONObject returnJson = new JSONObject();
                                returnJson.put("msg", msg);
                                returnJson.put("code", "100");
                                returnJson.put("success", true);
                                result = returnJson.toString();
							}else{
							    JSONObject returnJson = new JSONObject();
                                returnJson.put("msg", msg);
                                returnJson.put("code", "102");
                                returnJson.put("success", false);
                                result = returnJson.toString();
							}
						} else {
							resultJson.put("msg", "业务参数缺失");
							resultJson.put("success", false);
							resultJson.put("code", "107");
							result = resultJson.toString();
						}
                    } else {
                    	WriteLog.write(logname, r1 + ":jsonStr:" + json);
                    	JSONObject obj = new JSONObject();
                        obj.put("success", false);
                        obj.put("code", "105");
                        obj.put("msg", "签名错误");
                        result = obj.toString();
					}
				} catch (Exception e) {
					WriteLog.write(errorlogname, r1 + ":error:" + e);
		        	JSONObject obj = new JSONObject();
		            obj.put("success", false);
		            obj.put("code", "113");
		            obj.put("msg", "系统错误");
		            result = obj.toString();
				}
            }
        }  catch (Exception e) {
        	WriteLog.write(errorlogname, r1 + ":error:" + e);
        	JSONObject obj = new JSONObject();
            obj.put("success", false);
            obj.put("code", "113");
            obj.put("msg", "系统错误");
            result = obj.toString();
		} finally {
			if (out != null) {
                WriteLog.write(logname, r1 + ":reslut:" + result);
                out.print(result);
                out.flush();
                out.close();
            }
		}
	}
	
    /**
     * 获取美团KEY
     * @param partnerid
     * @return
     */
    public String getKeyByPartnerid(String partnerid){
    	String key = "";
    	String sql = "SELECT C_KEY FROM T_INTERFACEACCOUNT WHERE C_USERNAME = '" + partnerid + "'";
    	List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
        	Map map = (Map) list.get(0);
            key = map.get("C_KEY") != null ? map.get("C_KEY").toString() : "";
        }
    	return key;
    }
}
