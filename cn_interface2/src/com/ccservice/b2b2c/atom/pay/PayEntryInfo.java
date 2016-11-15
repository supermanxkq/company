package com.ccservice.b2b2c.atom.pay;

/**
 * 支付信息实体类
 * @author wzc
 * @version 创建时间：2015年9月6日 下午4:19:49
 */
public class PayEntryInfo {
    private long agentid;

    private long id;

    private String pid;

    private String key;

    private String sellemail;

    private long accounttype;

    private String privateKey;

    private String publicKey;

    private String CompayName;

    public void setCompayName(String compayName) {
        CompayName = compayName;
    }

    public String getCompayName() {
        return CompayName;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSellemail() {
        return sellemail;
    }

    public void setSellemail(String sellemail) {
        this.sellemail = sellemail;
    }

    public long getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(long accounttype) {
        this.accounttype = accounttype;
    }

}
