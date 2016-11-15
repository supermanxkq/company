package com.ccservice.b2b2c.atom.servlet;

import java.util.List;

import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

/**
 * 释放账号
 * 存储乘客
 * @time 2015年3月19日 下午2:01:16
 * @author fiend
 */
public class MyThreadIdVerification extends Thread {
    //需要释放的账号
    private Customeruser customeruser;

    //需要存储的乘客
    private List<Trainpassenger> plist;

    //随机数
    private int r1;

    public MyThreadIdVerification(Customeruser customeruser, List<Trainpassenger> plist, int r1) {
        this.customeruser = customeruser;
        this.plist = plist;
        this.r1 = r1;
    }

    public void run() {
        //freecustomeruser(this.customeruser);
        savePlist();
    }

    /**
     * 占用一个账号后的解锁，
     * 支付完成等调用
     * @param cust
     * @time 2014年12月23日 下午6:31:03
     * @author chendong
     */
    public void freecustomeruser(Customeruser cust) {
        try {
            String sql = "UPDATE T_CUSTOMERUSER SET C_ENNAME='1' where id=" + cust.getId();
            WriteLog.write("ID_身份验证接口_释放账号", r1 + "------>" + sql);
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
        catch (Exception e) {
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("customeruserObject", cust);
            WriteLog.write("ID_身份验证接口_释放账号", r1 + "------>" + "释放失败发送队列:" + cust.getLoginname());
        }
    }

    /**
     * 根据sysconfig的name获得value
     * 内存中
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getSysconfigString(String name) {
        String result = "-1";
        try {
            if (Server.getInstance().getDateHashMap().get(name) == null) {
                List<Sysconfig> sysoconfigs = Server.getInstance().getSystemService()
                        .findAllSysconfig("WHERE C_NAME='" + name + "'", "", -1, 0);
                if (sysoconfigs.size() > 0) {
                    result = sysoconfigs.get(0).getValue() != null ? sysoconfigs.get(0).getValue() : "-1";
                    Server.getInstance().getDateHashMap().put(name, result);
                }
            }
            else {
                result = Server.getInstance().getDateHashMap().get(name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将乘客存入数据库 
     * @time 2015年3月19日 下午1:34:11
     * @author fiend
     */
    private void savePlist() {
        for (int i = 0; i < this.plist.size(); i++) {
            savePassenger(plist.get(i).getName(), plist.get(i).getIdnumber(), plist.get(i).getIdtype(), plist.get(i)
                    .getAduitstatus());
        }
    }

    /**
     * 将乘客信息存入数据库 
     * @param pname
     * @param pidno
     * @param pidtype
     * @param paduitstatus
     * @time 2015年3月19日 下午1:25:14
     * @author fiend
     */
    private void savePassenger(String pname, String pidno, int pidtype, int paduitstatus) {
        try {
            if (1 == paduitstatus) {
                String sql = "INSERT INTO T_PASSINFO(C_NAME,C_IDNUMBER,C_IDTYPE,C_ADUITSTATUS) VALUES('" + pname
                        + "','" + pidno + "'," + pidtype + "," + paduitstatus + ")";
                WriteLog.write("ID_身份验证接口_存储乘客信息", r1 + "------>" + pname + "','" + pidno + "'," + pidtype + ","
                        + paduitstatus);
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            }
        }
        catch (Exception e) {
        }
    }
}
