package com.insurance;

import java.util.List;

import javax.activation.DataHandler;

import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.util.Insurances;





public interface IInsuranceBook {
	/**
	 * 保险订单创建
	 * 赵晓晓
	 * @param user
	 * @param list
	 * @param jyNo:交易流水号
	 * @param city:城市编号
	 * @throws Exception
	 */
	public List<Insurances> orderAplylist(String jyNo,Customeruser user,List list,String begintime,String[] fltno)throws Exception;
	/**
	 * 电子保险单获取
	 * 赵晓晓
	 * param no交易流水号
	 */
	public DataHandler PolicyReprint(Insurorder order)throws Exception;
	
	/**
	 * 机票下单接口
	 * @param jyNo
	 * @param list 被保险人信息
	 * @return 
	 */
	public List newOrderAplylist(String [] jyNo,List list);
	
	/**
	 * 火车票保险下单接口
	 * @param jyNo
	 * @param list
	 * @param type
	 * @return
	 */
	public List saveTrainOrderAplylist(String [] jyNo,List list,int type);
	
	/**
	 * 退保接口
	 * @param insur
	 * @return 
	 */
	public String cancelOrderAplylist(Insuruser insur);
	
}
