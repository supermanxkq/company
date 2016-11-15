package com.ccservice.b2b2c.atom.pay.gp;

import java.sql.Date;

/**
 * 
 * @author wzc
 * gp分润信息
 */
public class GpShareProfit {
    private Long OId;//订单ID

    private Long PayMethod;//支付方式

    private Long AgentId;//代理商ID

    private String Account;

    private Double Money;

    private Date Ptime;

    private String Note;//备注

    private Integer status;//

    private String TradeNum;//交易号

    private Integer Btype;//1 分给供应的钱  2 分给采购的钱 3 平台的钱

    public Integer getBtype() {
        return Btype;
    }

    public void setBtype(Integer btype) {
        Btype = btype;
    }

    public Long getOId() {
        return OId;
    }

    public void setOId(Long oId) {
        OId = oId;
    }

    public Long getPayMethod() {
        return PayMethod;
    }

    public void setPayMethod(Long payMethod) {
        PayMethod = payMethod;
    }

    public Long getAgentId() {
        return AgentId;
    }

    public void setAgentId(Long agentId) {
        AgentId = agentId;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public Double getMoney() {
        return Money;
    }

    public void setMoney(Double money) {
        Money = money;
    }

    public Date getPtime() {
        return Ptime;
    }

    public void setPtime(Date ptime) {
        Ptime = ptime;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTradeNum() {
        return TradeNum;
    }

    public void setTradeNum(String tradeNum) {
        TradeNum = tradeNum;
    }

}
