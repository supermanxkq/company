package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.elong.inter.PropertyUtil;


public class TaoBaoTrainOfflineConfirmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TaobaoHotelInterfaceUtil tbiu;   
   
    public TaoBaoTrainOfflineConfirmServlet() throws ServletException {
    	super.init();
        tbiu = new TaobaoHotelInterfaceUtil();
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String orderNum=request.getParameter("orderNum");
		WriteLog.write("cn_home淘宝线下票锁单开始", "orderNum："+orderNum);
		boolean suodan=tbiu.taobaoHandleOrder(orderNum);
		WriteLog.write("淘宝线下票锁单return信息", "订单号："+orderNum+";是否成功："+suodan);
		if(suodan){
			String updateOrder="UPDATE TrainOrderOffline SET lockedStatus=1 WHERE OrderNumberOnline='"+orderNum+"'";
			WriteLog.write("淘宝线下票锁单return信息后修改订单锁单状态", "updateOrder："+updateOrder);
			getSystemServiceOldDB().excuteAdvertisementBySql(updateOrder);
		}else{
			String updateOrder="UPDATE TrainOrderOffline SET lockedStatus=2 WHERE OrderNumberOnline='"+orderNum+"'";
			WriteLog.write("淘宝线下票锁单return信息后修改订单锁单状态", "updateOrder："+updateOrder);
			getSystemServiceOldDB().excuteAdvertisementBySql(updateOrder);
		}
		PrintWriter out = null;
        out = response.getWriter();
        out.print(suodan);
        out.flush();
        out.close();
	}
	
	/**
     * 获取对应系统的ITrainService
     * @param systemdburlString
     * @return
     */
    private ISystemService getSystemServiceOldDB() {
    	String systemdburlString=PropertyUtil.getValue("offlineservice", "Train.properties");
//    	String systemdburlString="http://121.40.241.126:9001/cn_service/service/";
    	HessianProxyFactory factory = new HessianProxyFactory();
        try {
            return (ISystemService) factory.create(ISystemService.class,
                    systemdburlString + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
