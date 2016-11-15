package com.ccservice.elong.inter;

import java.rmi.RemoteException;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.RelateReserve;

/**
 * 获取关联订单接口
 * 
 * @author 师卫林
 * 
 */
public class ElongGetRelateReserve {
	public static void main(String[] args) throws RemoteException {
		//订单列表
		String ReserveNoList="";
		//关联类型 查询关联类型：1 根据原来订单号查询新生成的订单	2根据新生成的订单号查询原来订单
		String ReserveType="";
		getRelateAPI(ReserveNoList, ReserveType);
	}
	public static void getRelateAPI(String ReserveNoList,String ReserveType) throws RemoteException{
		NorthBoundAPIServiceStub stub=new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.GetRelatedReserveRequest request=new NorthBoundAPIServiceStub.GetRelatedReserveRequest();
		NorthBoundAPIServiceStub.GetRelateReserveResponse response=new NorthBoundAPIServiceStub.GetRelateReserveResponse();
		NorthBoundAPIServiceStub.GetRelateReserve getRelateReserve=new NorthBoundAPIServiceStub.GetRelateReserve();
		NorthBoundAPIServiceStub.GetRelatedReserveResponse getRelatedReserveResponse=new NorthBoundAPIServiceStub.GetRelatedReserveResponse();
		
		request.setRequestHead(ElongRequestHead.getRequestHead(""));
		request.setReserveType(ReserveType);
		request.setReserveNoList(ReserveNoList);
		
		getRelateReserve.setRequest(request);
		response=stub.getRelateReserve(getRelateReserve);
		
		getRelatedReserveResponse=response.getGetRelateReserveResult();
		//结果编号
		System.out.println(getRelatedReserveResponse.getResponseHead().getResultCode());
		//结果信息
		System.out.println(getRelatedReserveResponse.getResponseHead().getResultMessage());
		//结果生成的时间
		System.out.println(DateSwitch.SwitchString(getRelatedReserveResponse.getResponseHead().getTimeStamp()));
		RelateReserve[] relateReserves=getRelatedReserveResponse.getRelateReserveList().getRelateReserve();
		for(int i=0;i<relateReserves.length;i++){
			RelateReserve reserve=relateReserves[i];
			//原订单号
			System.out.println(reserve.getParentReserveNo());
			//新订单号
			System.out.println(reserve.getChildReserveNo());
		}
	}
}
