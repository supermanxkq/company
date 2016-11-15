package com.ccservice.b2b2c.atom.service12306.bean;

import java.sql.Timestamp;
import java.io.Serializable;

/**
 * REP服务器
 * @author WH
 */

@SuppressWarnings("serial")
public class RepServerBean implements Serializable {
    /**
     * ID
     */
    private long id;

    /**
     * 服务器名称
     */
    private String name;

    /**
     * 服务器地址
     */
    private String url;

    /**
     * 最大并发订单数量
     */
    private int maxNumber;

    /**
     * 在下订单数量
     */
    private int useNumber;

    /**
     * 状态 0：不可用； 1：可用
     */
    private int status;

    /**
     * 最后交互时间
     */
    private Timestamp lastTime;

    /**
     * 当前用途是否是打码
     */
    private boolean isDama;

    /**
     * 是否要释放REP，前提FromRepSystem为true
     */
    private boolean needFreeRep;

    /**
     * 来自REP系统
     */
    private boolean FromRepSystem;

    /**
     * 用于标识服务器类型>>1：淘宝客人账号订单、改签、退票；其他：默认
     */
    private int type;

    private int serverPort;

    private String serverIp;

    private String serverPassword;

    /**
     * 用于淘宝托管未登录>>HOST>>12306的IP
     */
    private String special12306Ip;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public int getUseNumber() {
        return useNumber;
    }

    public void setUseNumber(int useNumber) {
        this.useNumber = useNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
    }

    public boolean isFromRepSystem() {
        return FromRepSystem;
    }

    public void setFromRepSystem(boolean fromRepSystem) {
        this.FromRepSystem = fromRepSystem;
    }

    public boolean isDama() {
        return isDama;
    }

    public void setDama(boolean isDama) {
        this.isDama = isDama;
    }

    public boolean isNeedFreeRep() {
        return needFreeRep;
    }

    public void setNeedFreeRep(boolean needFreeRep) {
        this.needFreeRep = needFreeRep;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getSpecial12306Ip() {
        return special12306Ip;
    }

    public void setSpecial12306Ip(String special12306Ip) {
        this.special12306Ip = special12306Ip;
    }

}