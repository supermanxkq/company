package com.ccservice.b2b2c.atom.train;

import java.sql.Timestamp;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.rebaterecord.Rebaterecord;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 火车票虚拟帐户记录
 * @author WH
 * @time 2015年8月13日 下午12:03:35
 * @version 1.0
 */

public class TrainVmoneyRecord {

    /**
     * 新增退款记录
     * @param agentId 代理ID
     * @param money 退款
     * @param ticketId 车票ID
     * @param orderNumber 订单号
     * @param refOrderNum 接口单号
     * @param mohutui 模糊退
     * @param offlineChange 线下改签
     */
    public void refund(long agentId, double money, long ticketId, String orderNumber, String refOrderNum,
            boolean mohutui, boolean offlineChange) {
        try {
            //业务类型
            int ywType = 0;
            //备注
            String memo = "";
            //模糊退
            if (mohutui) {
                ywType = 35;
                memo = "模糊退款,账户返还" + money + "元.";
            }
            else if (offlineChange) {
                ywType = 34;
                memo = "线下改签退款,账户返还" + money + "元.";
            }
            else {
                ywType = 31;
                memo = "退票退款,账户返还" + money + "元.";
            }
            //总数
            changeAgentVmoney(agentId, money);
            //参数
            Rebaterecord record = new Rebaterecord();
            record.setId(0);
            record.setRebate(0d);
            record.setVmenable(1);
            record.setPaymethod(10);
            record.setCustomerid(62);
            record.setYewutype(ywType);
            record.setRebatememo(memo);
            record.setOrderid(ticketId);
            record.setRebatemoney(money);
            record.setRebateagentid(agentId);
            record.setOrdernumber(orderNumber);
            record.setRebatetime(getCurrentTime());
            record.setRebatetype(Rebaterecord.TUIKUAN);
            record.setRefordernum(ElongHotelInterfaceUtil.StringIsNull(refOrderNum) ? "" : refOrderNum);
            //保存
            Server.getInstance().getMemberService().createRebaterecord(record);
        }
        catch (Exception e) {
        }
    }

    /**
     * 操作虚拟账户钱
     */
    private void changeAgentVmoney(long agentId, double money) {
        //SQL
        String sql = "update T_CUSTOMERAGENT set C_VMONEY = C_VMONEY + " + money + " where ID = " + agentId;
        //执行
        Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
    }

    /**
     * 获取当前时间
     */
    private Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

}