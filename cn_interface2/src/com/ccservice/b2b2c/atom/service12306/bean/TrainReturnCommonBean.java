package com.ccservice.b2b2c.atom.service12306.bean;

import java.util.Map;

/**
 * 火车票返回公共类
 * @author WH
 */

@SuppressWarnings("serial")
public class TrainReturnCommonBean implements java.io.Serializable {

    private boolean success;

    private String code;//编码，如：301表示车次无票，可参考同程文档

    private String msg;//返回信息，如车次已无票

    private String json;//REP或12306返回JSON数据，用于需要时获取类中没有的字段

    private int refundOnline;//西藏退票、改签问题，1：是

    private Map<String, String> backup;//备用

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getRefundOnline() {
        return refundOnline;
    }

    public void setRefundOnline(int refundOnline) {
        this.refundOnline = refundOnline;
    }

    public Map<String, String> getBackup() {
        return backup;
    }

    public void setBackup(Map<String, String> backup) {
        this.backup = backup;
    }

}