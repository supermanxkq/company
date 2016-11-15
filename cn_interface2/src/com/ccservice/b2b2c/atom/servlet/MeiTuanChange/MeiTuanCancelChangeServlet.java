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
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCancelChange;

@SuppressWarnings("serial")
public class MeiTuanCancelChangeServlet extends HttpServlet {

	private final String logname = "meituan美团_取消改签";

    private final String errorlogname = "meituan美团_取消改签_error";
    
    private final int r1 = new Random().nextInt(10000000);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
                      
                    long resignId =json.containsKey("resignId") ? json.getLong("resignId") : 0;
                    WriteLog.write(logname, r1 + ":Key:" + key);
                    key = ElongHotelInterfaceUtil.MD5(key);
                    WriteLog.write(logname, r1 + ":Key1:" + key);
                    String signflag = partnerid + GetReqTokenByResignId.Method.CANCEL_RESIGN + reqtime + key;
                    WriteLog.write(logname, r1 + ":signflag:" + signflag+"method:"+method);
                    signflag = ElongHotelInterfaceUtil.MD5(signflag);
                    WriteLog.write(logname, r1 + ":signflag1:" + signflag);
                    if (signflag.equalsIgnoreCase(sign)) {
                    	String orderid = json.containsKey("orderId") ? json.getString("orderId") : "";
                    	String transactionid = json.containsKey("agentOrderId") ? json.getString("agentOrderId") : "";
                    	TongChengCancelChange tongchengcancelchange = new TongChengCancelChange();
                    	JSONObject cancelJSON = new JSONObject();
                    	cancelJSON.put("orderid", orderid);
                    	cancelJSON.put("transactionid", transactionid);
                    	String operateRes =	 tongchengcancelchange.operate(cancelJSON, r1);
                    	new MyThreadCancelChangeByMeiTuan(operateRes, resignId,r1).start();;
                    	JSONObject resultJson = new JSONObject();
						if (!ElongHotelInterfaceUtil.StringIsNull(orderid)
								&& !ElongHotelInterfaceUtil
										.StringIsNull(transactionid)) {
							resultJson.put("msg", "取消改签请求已接受");
							resultJson.put("success", true);
							resultJson.put("code", "100");
						} else {
							resultJson.put("msg", "业务参数缺失");
							resultJson.put("success", false);
							resultJson.put("code", "107");
						}
						result = resultJson.toString();
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
        } catch(Exception e){
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
    @SuppressWarnings("rawtypes")
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