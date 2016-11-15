package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 淘宝线下火车票快递邮寄
 * @author guozhengju
 *2016-01-12
 */
public class TaoBaoTrainOfflineExpressServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TaobaoHotelInterfaceUtil tbiu;
	
	/** 间隔时间，以分钟为单位   */
	private static final long SCAN_TIME = 30L;
  
    public TaoBaoTrainOfflineExpressServlet()  throws ServletException{
    	super.init();
        tbiu = new TaobaoHotelInterfaceUtil();
        
        //扫描
        TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					String findFailStatusRecord = "SELECT t.Id , t.OrderId , t.Status FROM TaoBaoTrainOfflineExpressStatusRecord t WHERE t.Status <> " + TBTrainStatusRecordConst.SUCCESS.getVal();
					List listRes = getSystemServiceOldDB().findMapResultBySql(findFailStatusRecord, null);
					if(!CollectionUtils.isEmpty(listRes)){
						for (Object obj : listRes) {
							Map map = (Map) obj;
							String orderId = String.valueOf(map.get("OrderId"));
							JSONObject json = handleTaoBaoExpress(orderId);
							Integer id = json.getInteger("Id");
							if( ("true".equals(json.getString("isSuccess"))) && (id != null) ){
								String updateFailStatusRecordForSuccess = "UPDATE TaoBaoTrainOfflineExpressStatusRecord SET "
										+ " Status = " + TBTrainStatusRecordConst.SUCCESS.getVal()
										+ " ,"
										+ " UpdateTime = " + "\'" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) +"\'"
										+ " WHERE Id = " + id;
								getSystemServiceOldDB().findMapResultBySql(updateFailStatusRecordForSuccess, null);
							}
						}
					}
				} catch (Exception e) {
					WriteLog.write("淘宝邮寄返回系统异常重试", e.toString());
				}
			}
		};
        new Timer().schedule(task, new Date(),TimeUnit.MINUTES.toMillis(SCAN_TIME));
       
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String orderid=request.getParameter("orderid");
		JSONObject json = handleTaoBaoExpress(orderid);
		PrintWriter out = response.getWriter();
		out.print(json.getString("isSuccess"));
		out.flush();
		out.close();
	}
	
	protected JSONObject handleTaoBaoExpress(String orderid){
		JSONObject json = null;
		String sql="select O.OrderNumberOnline,M.ExpressNum,C.C_AGENTPHONE,C.C_AGENTADDRESS,M.ExpressAgent from T_CUSTOMERAGENT C,TrainOrderOffline O, mailaddress M where O.Id=M.ORDERID and O.AgentId=C.ID and O.Id="+orderid;
		List list=getSystemServiceOldDB().findMapResultBySql(sql, null);
		String express="0";
		String mobile="0";
		String add="";
		String empressname="SF";
		Map mp =new HashMap();
		String mainorderid="";
		if(list.size()>0){
			Map map=(Map)list.get(0);
			mainorderid=map.get("OrderNumberOnline").toString();
			mp.put("orderid",mainorderid);
			express=map.get("ExpressNum").toString();
			mp.put("express", express);
			mobile=map.get("C_AGENTPHONE").toString();
			mp.put("mobile", mobile);
			add=map.get("C_AGENTADDRESS").toString();
			mp.put("add", add);
			if("0".equals(map.get("ExpressAgent").toString())){
				empressname="SF";
			}else if("2".equals(map.get("ExpressAgent").toString())){
				empressname="EMS";
			}
			mp.put("empressname", empressname);
			WriteLog.write("淘宝线下票邮寄开始请求信息", "mainorderid="+mainorderid+";express="+express+";mobile="+mobile+";address="+add+",empressname="+empressname);
			try {
				json = tbiu.expressCallBack(mp);
				WriteLog.write("淘宝线下票邮寄return信息", json.getString("isSuccess"));
				if("true".equals(json.getString("isSuccess"))){
					//淘宝邮寄成功
					String updatesql1="update TrainOrderOffline set questionMail=2 where id="+orderid;
					WriteLog.write("淘宝线下票邮寄return信息sql1", updatesql1);
					getSystemServiceOldDB().findMapResultBySql(updatesql1, null);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String insert = "INSERT TrainOrderOfflineRecord(FKTrainOrderOfflineId,ProviderAgentid,DistributionTime,DealResult,RefundReasonStr) "
                            + " VALUES("
                            + orderid
                            + ",0,'"
                            + sdf.format(new Date())
                            + "',16,'" + "--------快递发件回调成功！--------')";
					WriteLog.write("淘宝线下票__根据发件返回信息添加操作记录", "insert:"
                            + insert);
					getSystemServiceOldDB().findMapResultBySql(insert, null);
                    
				}else{
					//淘宝邮寄失败
					String updatesql1="update TrainOrderOffline set questionMail=1 where id="+orderid;
					WriteLog.write("淘宝线下票邮寄return信息sql1", updatesql1);
					getSystemServiceOldDB().findMapResultBySql(updatesql1, null);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String insert = "INSERT TrainOrderOfflineRecord(FKTrainOrderOfflineId,ProviderAgentid,DistributionTime,DealResult,RefundReasonStr) "
                            + " VALUES("
                            + orderid
                            + ",0,'"
                            + sdf.format(new Date())
                            + "',116,'" + "---快递发件回调失败！---原因("+json.getString("errorMsgCode")+"):"+json.getString("errorMsg")+"')";
					WriteLog.write("淘宝线下票__根据发件返回信息添加操作记录", "insert:"
                            + insert);
					getSystemServiceOldDB().findMapResultBySql(insert, null);
					
					//记录状态
					String findStatusRecordSql = "select TOP (1) T.Id , T.OrderId , T.Status  from TaoBaoTrainOfflineExpressStatusRecord T "
							+ "WHERE T.OrderId = " + orderid;
					List rows = this.getSystemServiceOldDB().findMapResultBySql(findStatusRecordSql, null);
					if(CollectionUtils.isEmpty(rows)){
						String saveStatusRecordSql = "INSERT TaoBaoTrainOfflineExpressStatusRecord (OrderId , Status) VALUES (" 
							+ orderid + "," + TBTrainStatusRecordConst.FAIL.getVal() + ")";
						this.getSystemServiceOldDB().findMapResultBySql(saveStatusRecordSql, null);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
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

    /**
     * 淘宝邮寄返回系统信息
     * 0 成功
     * 1 失败
     * <p></P>
     * @author lzd
     * @time 2016年7月13日 下午3:48:44
     */
    enum TBTrainStatusRecordConst{
		SUCCESS(0),
		FAIL(1);
		private int val;
		
		private TBTrainStatusRecordConst(int val) {
			this.val = val;
		}

		public int getVal() {
			return val;
		}

		public void setVal(int val) {
			this.val = val;
		}
	}
}
