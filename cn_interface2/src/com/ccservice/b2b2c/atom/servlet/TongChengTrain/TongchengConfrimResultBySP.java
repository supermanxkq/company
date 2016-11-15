package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

/**
 * 通过存储过程，处理确认出票
 * @time 2015年4月17日 下午6:41:39
 * @author fiend
 */
public class TongchengConfrimResultBySP {
    //订单ID
    private long trainorderid;

    //是否确认出票成功
    private boolean issuccess;

    //确认出票存储过程 返回的同程code
    private String code;

    //确认出票存储过程 返回的同程msg
    private String msg;

    //确认出票存储过程 返回的12306订单号
    private String extnumber;

    public long getTrainorderid() {
        return trainorderid;
    }

    public void setTrainorderid(long trainorderid) {
        this.trainorderid = trainorderid;
    }

    public boolean isIssuccess() {
        return issuccess;
    }

    public void setIssuccess(boolean issuccess) {
        this.issuccess = issuccess;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getExtnumber() {
        return extnumber;
    }

    public void setExtnumber(String extnumber) {
        this.extnumber = extnumber;
    }

}
