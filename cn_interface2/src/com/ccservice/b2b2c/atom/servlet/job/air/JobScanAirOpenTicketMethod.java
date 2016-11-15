package com.ccservice.b2b2c.atom.servlet.job.air;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import client.ServiceStub;

import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 
 * @time 2015年11月24日 下午2:25:52
 * @author chendong
 */
public class JobScanAirOpenTicketMethod {
    String ticket_NO;

    String OfficeID;

    int index;

    /**
     * 
     * @param i 下标
     * @param ticket_NO 票号
     * @param officeID officeid
     */
    public JobScanAirOpenTicketMethod(int i, String ticket_NO, String officeID) {
        super();
        this.ticket_NO = ticket_NO;
        OfficeID = officeID;
        this.index = i;
    }

    /**
     * 检查出完票没有使用的票号
     * @author chendong
     */
    public String check_open_ticket() {
        int j = this.index;
        String result = "";
        try {
            ServiceStub stub = new ServiceStub();
            ServiceStub.KaYn_DETR_TN KaYn_DETR_TN = new ServiceStub.KaYn_DETR_TN();
            KaYn_DETR_TN.setOfficeID(OfficeID);
            if (!"NULL".equals(ticket_NO)) {
                KaYn_DETR_TN.setVotes(ticket_NO);
                result = stub.kaYn_DETR_TN(KaYn_DETR_TN).getKaYn_DETR_TNResult();
                //                System.out.println(j + ":" + ticket_NO + ":" + result);
                WriteLog.write("OPEN_TICKET", j + ":" + ticket_NO + ":" + result);
            }
        }
        catch (AxisFault e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
