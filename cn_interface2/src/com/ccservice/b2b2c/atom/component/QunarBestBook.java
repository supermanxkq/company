package com.ccservice.b2b2c.atom.component;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.eaccount.Eaccount;

/**
 * qunar优选
 * @author wzc
 *
 */
public class QunarBestBook {
    private String qunarurl = "";// qunar接口访问地址

    private String vendorID = "";// 客户端来源

    private String registname = "";

    private String signKey = "";

    private String linkmobile = "";

    private String status;

    public QunarBestBook() {
        qunarurl = "http://biz.flight.qunar.com";
        registname = "jszjexd1177";
        vendorID = "CCS";
        linkmobile = "13439311111";
    }

    public String getQunarurl() {
        return qunarurl;
    }

    public void setQunarurl(String qunarurl) {
        this.qunarurl = qunarurl;
    }

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    public String getRegistname() {
        return registname;
    }

    public void setRegistname(String registname) {
        this.registname = registname;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getLinkmobile() {
        return linkmobile;
    }

    public void setLinkmobile(String linkmobile) {
        this.linkmobile = linkmobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
