package com.ccservice.b2b2c.atom.refund.handle;

public interface RefundHandle {
	
	public void refundedHandle(boolean success,long orderid,String batchno);

}
