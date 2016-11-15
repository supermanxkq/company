/** @Title: StationName.java	   <BR>
* @Package com.ccservice.b2b2c.atom.service12306.bean   <BR>
* @Description: TODO(用一句话描述该文件做什么)<BR>
* @author   Anki   <BR>
* @date 2016年1月6日 下午8:14:57  <BR>
* @version V1.0     
*/
package com.ccservice.b2b2c.atom.service12306.bean;

/**
* @ClassName: StationName <BR>
* @Description: TODO(这里用一句话描述这个类的作用)<BR>
* @author  車站實體類 <BR>
* @date 2016年1月6日 下午8:14:57 <BR> <BR>
*/
public class StationName {

    private Integer id;

    private String jp;

    private String sName;

    private String qp;

    private String scode;

    private String jp1;

    private String sindex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJp() {
        return jp;
    }

    public void setJp(String jp) {
        this.jp = jp;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getQp() {
        return qp;
    }

    public void setQp(String qp) {
        this.qp = qp;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getJp1() {
        return jp1;
    }

    public void setJp1(String jp1) {
        this.jp1 = jp1;
    }

    public String getSindex() {
        return sindex;
    }

    public void setSindex(String sindex) {
        this.sindex = sindex;
    }

}
