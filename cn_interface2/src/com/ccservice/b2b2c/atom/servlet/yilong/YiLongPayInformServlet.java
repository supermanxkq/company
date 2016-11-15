package com.ccservice.b2b2c.atom.servlet.yilong;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCancelTrain;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengConfirmTrain;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

/**
 * Servlet implementation class YiLongPayInformServlet
 */
public class YiLongPayInformServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static final String logname="MeiTuan_3_9_先占座后支付模式支付通知"; 

    String key;
    
    Map<String, InterfaceAccount> interfaceAccountMap = new HashMap<String, InterfaceAccount>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	    request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        JSONObject outjson=new JSONObject();
        int r1 = new Random().nextInt(10000000);
        try {
            out = response.getWriter();
            String merchantId=request.getParameter("merchantId");
            String timeStamp=request.getParameter("timeStamp");
            String orderId=request.getParameter("orderId");
            String ticketPrice=request.getParameter("ticketPrice");
            String result=request.getParameter("result");
            String sign=request.getParameter("sign");
            WriteLog.write(logname,"merchantId:"+merchantId+"timeStamp:"+timeStamp+"orderId:"+
            orderId+"ticketPrice:"+ticketPrice+"result:"+result+"sign:"+sign);
            if (ElongHotelInterfaceUtil.StringIsNull(merchantId) || ElongHotelInterfaceUtil.StringIsNull(timeStamp)
                    || ElongHotelInterfaceUtil.StringIsNull(orderId)
                    || ElongHotelInterfaceUtil.StringIsNull(ticketPrice)
                    || ElongHotelInterfaceUtil.StringIsNull(result) || ElongHotelInterfaceUtil.StringIsNull(sign)) {
                outjson.put("retcode", "400");
                outjson.put("retdesc", "艺龙系统错误");
            }
            else{
                InterfaceAccount interfaceAccount = interfaceAccountMap.get(merchantId);
                if (interfaceAccount == null) {
                    interfaceAccount = getInterfaceAccountByLoginname(merchantId);
                    if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                            && interfaceAccount.getInterfacetype() != null) {
                        interfaceAccountMap.put(merchantId, interfaceAccount);
                    }
                }
                String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
                        + "&ticketPrice=" + ticketPrice + "&result=" + result;
                this.key = interfaceAccount.getKeystr();
                localSign = getSignMethod(localSign) + this.key;
                WriteLog.write(logname, ":第一个排序:" + localSign);
                localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
                WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign);
                if(localSign.equals(sign)){
                    outjson.put("retcode", "200");
                    WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign+",签名校检成功！");
                    outjson=PayInform(ticketPrice, result, timeStamp, orderId, merchantId, r1, interfaceAccount);
                }
                else{
                    outjson.put("retcode", "403");
                    outjson.put("retdesc", "签名校验失败");
                    WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign+",签名校检失败！");
                    return;
                }
                
            }
            
        }
        catch (Exception e) {
            WriteLog.write(logname, "艺龙数据参数获取异常~~===》");
            e.printStackTrace();
        }
        finally{
            WriteLog.write(logname, r1 + ":reslut:" + outjson);
            out.print(outjson);
            out.flush();
            out.close();
        }
	    
	}
	
	
	public static JSONObject PayInform(String ticketPrice,String result,String timeStamp,String orderId
            ,String merchantId,int r1,InterfaceAccount interfaceAccount){
        
        JSONObject outjson=new JSONObject();
        JSONObject json=new JSONObject();
        String sql = "SELECT C_TOTALPRICE,C_ORDERNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='"
                + orderId + "'";
        
        
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        
        
        if(list.size()>0) {
            Map map=(Map) list.get(0);
            String transactionid = map.get("C_ORDERNUMBER").toString();
            String orderPrice = map.get("C_TOTALPRICE").toString();
            String resultStr = "";
            if (Float.parseFloat(ticketPrice) < Float.parseFloat(orderPrice)) { //如果艺龙传过来的支付价格小于订单价格
                WriteLog.write(logname, ":相比较：艺龙金额" + ticketPrice + ":订单价格:" + orderPrice+",价格金额不符！");
                outjson.put("retcode", "444");
                outjson.put("retdesc", "出票金额错误");
            }
            else {
                if ("SUCCESS".equals(result)) { //出票
                    json.put("orderid", orderId);
                    json.put("transactionid", transactionid);
                    json.put("partnerid", merchantId);
                    WriteLog.write(logname, r1 + ":json:" + json.toString()
                            + ":orderPrice:" + orderPrice);
                    resultStr = new TongChengConfirmTrain().opeate(json, interfaceAccount);
                }
                else if ("FAIL".equals(result)) {//等同取消占座
                    resultStr = new TongChengCancelTrain().operate(json, r1);
                }

                if (resultStr.indexOf("出票请求已接收") > -1) {
                    outjson.put("retcode", "200");
                    outjson.put("retdesc", "成功");
                    WriteLog.write(logname, "订单：" + orderId + "支付交易成功！");
                }
                else {
                    outjson = JSONObject.parseObject(resultStr);
                }
            }
        }
        else {
            outjson.put("retcode", "452");
            outjson.put("retdesc", "此订单不存在");
            WriteLog.write(logname, "订单：" + orderId + "订单号："+orderId+"不存在！");
        }
        return outjson;

        
           
    }   
    
    
     public void getResponeOut(AsyncContext ctx, String result, String logName) {
            try {
                ServletResponse response = ctx.getResponse();
                //编码
                response.setCharacterEncoding("UTF-8");
                //输出
                response.getWriter().write(result);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("ERROR_" + logName, e);
            }
            finally {
                try {
                    ctx.complete();
                }
                catch (Exception e) {
                }
            }
        }
     
     
    
    /**
     * 
     * 
     * @param json
     * @return
     * @time 2015年12月10日 上午11:47:17
     * @author Mr.Wang
     */
    public String getSignMethod(String sign) {
        if (!ElongHotelInterfaceUtil.StringIsNull(sign)) {
            String[] signParam = sign.split("&");
            sign = ElongHotelInterfaceUtil.sort(signParam);
            return sign;
        }
        return "";
    }
    

    
    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    public static InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        WriteLog.write("艺龙支付通知_ElongPayMessageServlet_payMsgDisposeMethod", "loginname:" + loginname);
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WriteLog.write("艺龙支付通知_ElongPayMessageServlet_payMsgDisposeMethod", "list_interfaceAccount:"
                + list_interfaceAccount.size());
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        return interfaceAccount;
    }

}
