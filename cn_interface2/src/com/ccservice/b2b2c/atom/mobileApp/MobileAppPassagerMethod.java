package com.ccservice.b2b2c.atom.mobileApp;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customercredit.Customercredit;
import com.ccservice.b2b2c.base.customerpassenger.Customerpassenger;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.useraddress.Useraddress;

/**
 * 
 * 为mobileApp添加常用旅客提供的方法
 * @time 2015年5月28日 下午1:52:57
 * @author baiyushan
 */

public class MobileAppPassagerMethod {
    /**
     * 添加常用旅客的方法
     */
    public Customerpassenger customerpassenger = new Customerpassenger();

    public ShenfenyanzhengMethod yanzheng = new ShenfenyanzhengMethod();

    public Customeruser customeruser = new Customeruser();

    //证件对象
    public Customercredit customercredit = new Customercredit();

    public static String result = null;

    public Customerpassenger addPassger(String username, String useridentitytype, String useridentity, String usertype,
            String curphone) {
        //常用旅客的证件类型

        int num = 0;
        if ("二代身份证".equals(useridentitytype)) {
            num = 1;
        }
        if ("港澳通行证".equals(useridentitytype)) {
            num = 4;
        }
        if ("台湾通行证".equals(useridentitytype)) {
            num = 5;
        }
        if ("护照".equals(useridentitytype)) {
            num = 3;
        }
        //常用旅客类型

        int usernum = 0;
        if ("成人".equals(usertype)) {
            usernum = 1;
        }
        if ("学生".equals(usertype)) {
            usernum = 2;
        }
        if ("儿童".equals(usertype)) {
            usernum = 3;
        }

        //常用旅客调用身份核验接口时的返回结果   1 已通过 0 待核验 -1 未通过
        int retype = 0;
        result = yanzheng.yanzheng(username, useridentitytype, useridentity, num);
        if (!"".equals(result)) {
            if (result.contains("已通过")) {
                result = "通过";
                retype = 1;
            }
            if (result.contains("待核验")) {
                result = "待核验";
                retype = 0;
            }
            if (result.contains("未通过")) {
                result = "未通过";
                retype = -1;
            }
        }

        //当前用户对应的ID
        long customeruserId = this.getCustomeruserId(curphone);
        //常用旅客信息
        //常用旅客姓名
        customerpassenger.setUsername(username);
        //创建者的手机号码（相当于创建者）
        customerpassenger.setCreateuser(curphone);
        //创建时间
        customerpassenger.setCreatetime(new Timestamp(System.currentTimeMillis()));
        //修改者
        customerpassenger.setModifyuser(curphone);
        //修改时间
        customerpassenger.setModifytime(new Timestamp(System.currentTimeMillis()));
        //旅客类型（号码）
        customerpassenger.setType(usernum);
        customerpassenger.setTypestr(usertype);
        //关联的当前账户的ID
        customerpassenger.setCustomeruserid(customeruserId);
        //证件类型
        customerpassenger.setLivingcardtype(useridentitytype);
        //证件号码
        customerpassenger.setLivingcardnum(useridentity);
        //证件的状态
        customerpassenger.setState(retype);
        try {
            customerpassenger = Server.getInstance().getMemberService().createCustomerpassenger(customerpassenger);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return customerpassenger;
    }

    /**
     * 根据当前用户的手机号码来获取当前用户的ID
     */
    public long getCustomeruserId(String curphone) {
        long a = 0l;
        List<Customeruser> list = new ArrayList<Customeruser>();
        String where = "WHERE C_LOGINNAME = '" + curphone + "';";
        list = Server.getInstance().getMemberService().findAllCustomeruser(where, null, -1, 0);
        if (list != null && list.size() == 1) {
            customeruser = list.get(0);
            a = customeruser.getId();
        }
        return a;
    }

    /**
     * 根据当前手机号码获取当前手机号码的对象
     */
    public Customeruser getCustomeruser(String curphone) {
        Customeruser customeruser = new Customeruser();
        List<Customeruser> list = new ArrayList<Customeruser>();
        String where = "WHERE C_LOGINNAME = '" + curphone + "';";
        list = Server.getInstance().getMemberService().findAllCustomeruser(where, null, -1, 0);
        if (list != null && list.size() == 1) {
            customeruser = list.get(0);
        }
        return customeruser;

    }

    /**
     * 删除常用旅客
     */
    public int deletePassger(long id) {

        int n = Server.getInstance().getMemberService().deleteCustomerpassenger(id);

        return n;

    }

    /**
     * 查看常用旅客信息
     */
    public List getCustomerPassenger(long id) {
        List<Customerpassenger> list = new ArrayList<Customerpassenger>();
        String where = "WHERE C_CUSTOMERUSERID = '" + id + "';";
        list = Server.getInstance().getMemberService().findAllCustomerpassenger(where, null, -1, 0);
        return list;
    }

    /**
     * 修改常用旅客信息
     */
    public Customerpassenger updateCustomerPassenger(String id, String username, String useridentitytype,
            String useridentity, String usertype, String curphone) {
        long ID = Long.parseLong(id);
        customerpassenger = Server.getInstance().getMemberService().findCustomerpassenger(ID);
        if (customerpassenger != null) {
            int num = 0;
            if ("二代身份证".equals(useridentitytype)) {
                num = 1;
            }
            if ("港澳通行证".equals(useridentitytype)) {
                num = 4;
            }
            if ("台湾通行证".equals(useridentitytype)) {
                num = 5;
            }
            if ("护照".equals(useridentitytype)) {
                num = 3;
            }
            //常用旅客类型
            int usernum = 0;
            if ("成人".equals(usertype)) {
                usernum = 1;
            }
            if ("学生".equals(usertype)) {
                usernum = 2;
            }
            if ("儿童".equals(usertype)) {
                usernum = 3;
            }
            //常用旅客调用身份核验接口时的返回结果   1 已通过 0 待核验 -1 未通过
            int retype = 0;
            result = yanzheng.yanzheng(username, useridentitytype, useridentity, num);
            if (result.contains("已通过")) {
                result = "通过";
                retype = 1;
            }
            if (result.contains("待核验")) {
                result = "待核验";
                retype = 0;
            }
            if (result.contains("未通过")) {
                result = "未通过";
                retype = -1;
            }
            //当前用户对应的ID
            //常用旅客信息
            //常用旅客姓名
            customerpassenger.setUsername(username);
            //创建者的手机号码（相当于创建者）
            customerpassenger.setCreateuser(curphone);
            //修改者
            customerpassenger.setModifyuser(curphone);
            //修改时间
            customerpassenger.setModifytime(new Timestamp(System.currentTimeMillis()));
            //旅客类型（号码）
            customerpassenger.setType(usernum);
            customerpassenger.setTypestr(usertype);
            //证件类型
            customerpassenger.setLivingcardtype(useridentitytype);
            //证件号码
            customerpassenger.setLivingcardnum(useridentity);
            //证件的状态
            customerpassenger.setState(retype);
            Server.getInstance().getMemberService().updateCustomerpassenger(customerpassenger);
        }
        return customerpassenger;

    }

    /**
     * 根据身份证号码查找当前账号的常用旅客
     */
    public List getCustomerPassenger(String useridentity) {
        List<Customerpassenger> list = new ArrayList<Customerpassenger>();
        String where = "WHERE C_LIVINGCARDNUM = '" + useridentity + "';";
        list = Server.getInstance().getMemberService().findAllCustomerpassenger(where, null, -1, 0);
        return list;
    }

    //-------------------------------------------------手机客户点的邮寄地址的方法-------------------------------------------------//
    /**
     * 添加当前用户的邮件地址
     * 返回-1添加失败
     */
    public int addUserAddress(String username, String userphone, String postcode, String address, String curphone) {
        int num = -1;
        try {
            //根据当前用户的手机号码获取当前用户的ID
            long customeruserId = this.getCustomeruserId(curphone);
            if (customeruserId != 0l) {
                Useraddress useraddress = new Useraddress();
                useraddress.setName(username);
                useraddress.setPostalcode(postcode);
                useraddress.setAddress(address);
                useraddress.setMemberid(customeruserId);
                useraddress.setCreatetime(new Timestamp(System.currentTimeMillis()));
                useraddress.setTel(userphone);
                Object ob = new Object();
                ob = Server.getInstance().getMemberService().createUseraddress(useraddress);
                if (ob != null) {
                    num = 1;
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return num;
    }

    /**
     * 删除邮寄地址的操作方法
     * 返回-1删除失败
     */
    public int deleteAddress(long id) {
        int num = -1;
        int result = 0;
        result = Server.getInstance().getMemberService().deleteUseraddress(id);
        if (result != 0) {
            num = 1;
        }
        return num;
    }

    /**
     * 查看邮寄地址
     */
    @SuppressWarnings("unchecked")
    public List<Useraddress> selectAddress(String curphone) {
        long customeruserId = this.getCustomeruserId(curphone);
        List<Useraddress> list = new ArrayList<Useraddress>();
        String where = "where C_MEMBERID = '" + customeruserId + "';";
        list = Server.getInstance().getMemberService().findAllUseraddress(where, "", -1, 0);
        //        list = (List<Useraddress>) Server.getInstance().getMemberService().findUseraddressbymemberid(customeruserId);
        return list;
    }

    /**
     * 修改邮寄地址
     * 返回-1修改失败
     */
    public int updateUseraddress(long id, String username, String userphone, String postcode, String address) {
        int num = -1;
        Useraddress useraddress = Server.getInstance().getMemberService().findUseraddress(id);
        if (useraddress != null) {
            useraddress.setName(username);
            useraddress.setTel(userphone);
            useraddress.setPostalcode(postcode);
            useraddress.setAddress(address);
        }
        int resultnum = Server.getInstance().getMemberService().updateUseraddress(useraddress);
        if (resultnum != 0) {
            num = 1;
        }
        return num;
    }

}
