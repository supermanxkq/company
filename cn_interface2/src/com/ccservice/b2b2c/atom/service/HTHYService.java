package com.ccservice.b2b2c.atom.service;


import com.opensymphony.xwork.ActionSupport;



public class HTHYService extends ActionSupport{
	
	private String api;

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("hello");
		if("getproplist".equals(api)){
			
		}
		
		return super.execute();
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	

}
