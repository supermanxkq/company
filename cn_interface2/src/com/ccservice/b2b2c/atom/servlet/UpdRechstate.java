package com.ccservice.b2b2c.atom.servlet;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.IAtomService;
import com.ccservice.b2b2c.base.qmoneyrecharge.Qmoneyrecharge;
import com.ccservice.b2b2c.base.recharge.Recharge;

public class UpdRechstate implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("手机，Q币状态更改定时任务执行：");
		List<Recharge> listrecharge=Server.getInstance().getMemberService().findAllRecharge(" WHERE C_STATE=0 ", "ORDER BY ID DESC", -1, 0);
	    IAtomService atomservice=Server.getInstance().getAtomService();
	    StringBuilder sqlbuilder=new StringBuilder("");
		for(Recharge recharge:listrecharge){
			String ordernumber=recharge.getRefordernumber()==null?recharge.getOrdernumber():recharge.getRefordernumber();
			String statestr = atomservice.getPaystate(ordernumber);
			try{
				int state=Integer.valueOf(statestr);
				if(state!=-1&&state!=0){
					sqlbuilder.append(" UPDATE T_RECHARGE SET C_STATE="+state+" WHERE ID="+recharge.getId()+";");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(sqlbuilder.length()>0){
		//	System.out.println(sqlbuilder.toString());
			Server.getInstance().getSystemService().findMapResultBySql(sqlbuilder.toString(), null);
		}
		sqlbuilder=new StringBuilder("");
		List<Qmoneyrecharge> listqmoneyrecharge=Server.getInstance().getMemberService().findAllQmoneyrecharge(" WHERE C_RECHSTATE=0 ", "ORDER BY ID DESC", -1, 0);
		for(Qmoneyrecharge qrecharge:listqmoneyrecharge){
			String ordernumber=qrecharge.getRefordernumber()==null?qrecharge.getOrdernumber():qrecharge.getRefordernumber();
			String statestr = atomservice.getPaystate(ordernumber);
			try{
				int state=Integer.valueOf(statestr);
				if(state!=-1&&state!=0){
					sqlbuilder.append(" UPDATE T_QMONEYRECHARGE SET C_RECHSTATE="+state+" WHERE ID="+qrecharge.getId()+";");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(sqlbuilder.length()>0){
			//System.out.println(sqlbuilder.toString());
			Server.getInstance().getSystemService().findMapResultBySql(sqlbuilder.toString(), null);
		}
		
		
	}

}
