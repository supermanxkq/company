package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 途牛催退款接口
 * 
 * @author fantao
 */

public class TuNiuRefundQueryServlet extends HttpServlet {

    private final String logname = "tuniu途牛_线下退款进度接口";

    private final String errorlogname = "tuniu途牛_线下退款进度_error";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int random = new Random().nextInt();
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        JSONObject rusultJsonObject = new JSONObject();
        try {
            out = resp.getWriter();
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }

            String param = buf.toString();
            JSONObject paramoJsonObject = new JSONObject();
            WriteLog.write(logname, random + "-->请求参数:" + param);
            // 解密
            try {
                paramoJsonObject = JSONObject.parseObject(param);
                if (paramoJsonObject.containsKey("data") && !paramoJsonObject.getString("data").isEmpty()) {
                    String data = paramoJsonObject.getString("data");
                    data = TuNiuDesUtil.decrypt(data);
                    JSONObject dataJsonObject = JSONObject.parseObject(data);
                    paramoJsonObject.put("data", dataJsonObject);
                }
            }
            catch (Exception e) {
                WriteLog.write(errorlogname, random + "");
                ExceptionUtil.writelogByException(errorlogname, e);
                rusultJsonObject.put("success", false);
                rusultJsonObject.put("returnCode", "2001");
                rusultJsonObject.put("errorMsg", "解密错误");
                rusultJsonObject.put("data", "");
                return;
            }
            WriteLog.write(logname, random + "--->请求参数(解密后):" + paramoJsonObject.toString());
            if (!ElongHotelInterfaceUtil.StringIsNull(paramoJsonObject.toString())) {
                JSONObject dataJson = paramoJsonObject.getJSONObject("data");
                String queryMsg = getRefundQueryMsg(dataJson.toString(), random);
                WriteLog.write(logname, random + "--->queryMsg--->" + queryMsg);
                if (ElongHotelInterfaceUtil.StringIsNull(queryMsg) || "[]".equals(queryMsg)) {
                    rusultJsonObject.put("success", false);
                    rusultJsonObject.put("returnCode", "2001");
                    rusultJsonObject.put("errorMsg", "催退款失败");
                    rusultJsonObject.put("data", "");
                    return;
                }
                JSONArray returnList = JSONArray.parseArray(queryMsg);
                JSONObject data = new JSONObject();
                data.put("refundList", returnList);
                WriteLog.write(logname, random + "--->" + data.toString());
                rusultJsonObject.put("success", true);
                rusultJsonObject.put("returnCode", "231000");
                rusultJsonObject.put("errorMsg", "");
                rusultJsonObject.put("data", data);
                return;
            }
            else {
                rusultJsonObject.put("success", false);
                rusultJsonObject.put("returnCode", "2001");
                rusultJsonObject.put("errorMsg", "param error");
                rusultJsonObject.put("data", "");
                return;
            }
        }
        catch (Exception e) {
            WriteLog.write(errorlogname, random + "");
            ExceptionUtil.writelogByException(errorlogname, e);
            rusultJsonObject.put("success", false);
            rusultJsonObject.put("returnCode", "2001");
            rusultJsonObject.put("errorMsg", "催退款失败");
            rusultJsonObject.put("data", "");
            return;
        }
        finally {
            //记录日志
            WriteLog.write(logname, random + "--->" + "返回参数:" + rusultJsonObject.toString());
            out.print(rusultJsonObject.toString());
            out.flush();
            out.close();
        }

    }

    public String getRefundQueryMsg(String dataStr, int random) {
        Map map = null;
        JSONArray resultjson = new JSONArray();
        String ticketNo = "";
        String returnMoney = "";
        String returnTime = "";
        String returnStatus = "";
        String returnMsg = "";
        try {
            JSONObject dataJson = JSONObject.parseObject(dataStr);
            String vendorOrderId = dataJson.containsKey("vendorOrderId") ? dataJson.getString("vendorOrderId") : "";
            String orderId = dataJson.containsKey("orderId") ? dataJson.getString("orderId") : "";

            String passengerSql = "SELECT C_TICKETNO,C_STATUS, C_PRICE, C_PROCEDURE, C_REFUNDREQUESTTIME, C_REFUNDSUCCESSTIME FROM T_TRAINTICKET WHERE C_TRAINPID in"
                    + "(SELECT ID FROM T_TRAINPASSENGER WHERE C_ORDERID = (SELECT ID FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER = '"
                    + orderId + "' AND C_ORDERNUMBER = '" + vendorOrderId + "'))";
            List passengerList = Server.getInstance().getSystemService().findMapResultBySql(passengerSql, null);
            if (passengerList.size() > 0) {
            	for (int i = 0; i < passengerList.size(); i++) {
            		JSONObject json = new JSONObject();
            		map = (Map) passengerList.get(i);
                    ticketNo = map.get("C_TICKETNO") != null ? map.get("C_TICKETNO").toString() : "";
                    String status = map.get("C_STATUS") != null ? map.get("C_STATUS").toString() : "";
                    float price = map.get("C_PRICE") != null ? Float.parseFloat(map.get("C_PRICE").toString()) : 0;
                    float procedure = map.get("C_PROCEDURE") != null ? Float.parseFloat(map.get("C_PROCEDURE").toString())
                            : 0;
                    returnMoney = (price - procedure) + "";
                    if (map.get("C_REFUNDSUCCESSTIME") != null) {
                        returnTime = map.get("C_REFUNDSUCCESSTIME").toString();
                    } else {
						returnTime = "2016-07-08 16:10:19";
					}
                    
                    if (status.equals("5") || status.equals("6") || status.equals("8") || status.equals("9")) {
                        returnStatus = "1";
                        returnMsg = "退款处理中";
                    }
                    else if (status.equals("10")) {
                        returnStatus = "2";
                        returnMsg = "已退款";
                    }
                    else if (status.equals("11")) {
                        returnStatus = "3";
                        returnMsg = "退款失败";
                    } else {
                    	returnStatus = "4";
                    	returnMsg = "其他";
                    }
                    json.put("ticketNo", ticketNo);
                    json.put("returnMoney", returnMoney);
                    json.put("returnTime", returnTime);
                    json.put("returnStatus", returnStatus);
                    json.put("returnMsg", returnMsg);
                    resultjson.add(json);
				}
            }
            
            return resultjson.toString();
        }
        catch (Exception e) {
            WriteLog.write(errorlogname, random + "");
            ExceptionUtil.writelogByException(errorlogname, e);
            return "";
        }
    }
}
