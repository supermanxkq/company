package com.ccservice.b2b2c.atom.servlet.yilong;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCancelTrain;
import com.ccservice.b2b2c.atom.servlet.yl.ElongCancelOrderDisposeMethod;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;


public class YiLongCancelOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final String logname="MeiTuan_3_10_先占座后支付模式取消占座";
	
    String key;
    
    Map<String, InterfaceAccount> interfaceAccountMap = new HashMap<String, InterfaceAccount>();


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=utf-8");
        int r1 = new Random().nextInt(10000000);
        JSONObject result = new JSONObject();
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String merchantId = request.getParameter("merchantId");
            String timeStamp = request.getParameter("timeStamp");
            String orderId = request.getParameter("orderId");
            String sign = request.getParameter("sign");

            WriteLog.write(logname, r1 + ":" + merchantId + ":" + timeStamp + ":"
                    + orderId + ":" + sign);
            if (ElongHotelInterfaceUtil.StringIsNull(merchantId) || ElongHotelInterfaceUtil.StringIsNull(timeStamp)
                    || ElongHotelInterfaceUtil.StringIsNull(orderId) || ElongHotelInterfaceUtil.StringIsNull(sign)) {
                result.put("retcode", "400");
                result.put("retdesc", "艺龙系统错误");
            }
            else {
                InterfaceAccount interfaceAccount = interfaceAccountMap.get(merchantId);
                if (interfaceAccount == null) {
                    interfaceAccount = getInterfaceAccountByLoginname(merchantId);
                    if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                            && interfaceAccount.getInterfacetype() != null) {
                        interfaceAccountMap.put(merchantId, interfaceAccount);
                    }
                }
                String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId;
                this.key = interfaceAccount.getKeystr();
                localSign = getSignMethod(localSign) + this.key;
                WriteLog.write(logname, ":第一个排序:" + localSign);
                localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
                WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign);
                if(localSign.equals(sign)){
                    result.put("retcode", "200");
                    WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign+",签名校检成功！");
                    result=cancelOrder(orderId, merchantId, timeStamp, r1);
                    
                }
                else{
                    result.put("retcode", "403");
                    result.put("retdesc", "签名校验失败");
                    WriteLog.write(logname, ":相比较：" + localSign + ":==:" + sign+",签名校检失败！");
                    return;
                }
                
            }
        }
        catch (Exception e) {
        }
        finally {
            WriteLog.write(logname, r1 + ":reslut:" + result);
            out.print(result);
            out.flush();
            out.close();
        }
	}
	
	public JSONObject cancelOrder(String orderId,String merchantId,String timeStamp,int r1){
	    JSONObject outjson=new JSONObject();
	    JSONObject json=new JSONObject();
	    String sql = "SELECT C_ORDERNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='" + orderId
                + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            String transactionid = map.get("C_ORDERNUMBER").toString();

            json.put("orderid", orderId);
            json.put("transactionid", transactionid);

            WriteLog.write("Elong_取消订单_ElongCancelOrderServlet", r1 + ":json:" + json.toString());
            String result = new TongChengCancelTrain().operate(json, r1);
            if (result.indexOf("取消订单成功") > -1) {
                outjson.put("retcode", "200");
                outjson.put("retdesc", "成功");
            }
            else {
                outjson = JSONObject.parseObject(result);
            }
        }
        else {
            outjson.put("retcode", "452");
            outjson.put("retdesc", "此订单不存在");
        }
        
        return outjson;
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
