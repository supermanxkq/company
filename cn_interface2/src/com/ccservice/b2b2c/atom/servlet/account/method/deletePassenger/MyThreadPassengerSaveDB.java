package com.ccservice.b2b2c.atom.servlet.account.method.deletePassenger;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
/**
 * 把乘客列表中的乘客轉存到58Passenger表裡面
 * @author zhyxu
 *
 */
public class MyThreadPassengerSaveDB extends Thread{

	private JSONObject passengerObject;
	
	public MyThreadPassengerSaveDB(JSONObject passengerObject) {
		this.passengerObject = passengerObject;
	}
	
	@Override
	public void run() {
		try {
			if(passengerObject!=null&&!passengerObject.isEmpty()){
				String id_type = passengerObject.containsKey("passenger_id_type_code")?passengerObject.getString("passenger_id_type_code"):"";
				String idnum = passengerObject.containsKey("passenger_id_no")?passengerObject.getString("passenger_id_no").toUpperCase():"";
				String name = passengerObject.containsKey("passenger_name")? passengerObject.getString("passenger_name"):"";
				//证件类型是身份证才要
				if(!idnum.equals("")&&!name.equals("")
						&&!id_type.equals("")&&id_type.equals("1")){
					String bornDate=idnum.substring(6, 10)+"-"+idnum.substring(10, 12)+"-"+idnum.substring(12, 14);
					String sSQL = "exec insert_Passenger @Name='"+name+"',@IdNum='"+idnum+"',@BornDate='"+bornDate+"'";
					DBHelperAccount.executeSql(sSQL);
				}
			}
		} catch (Exception e) {
		}
	
	}
	

	
}
