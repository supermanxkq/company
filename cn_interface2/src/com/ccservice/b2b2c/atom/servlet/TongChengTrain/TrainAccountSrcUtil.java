package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.util.DesUtil;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.b2b2c.util.db.DBSQLHelper;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.elong.inter.PropertyUtil;

public class TrainAccountSrcUtil extends Thread {

    //OCS  360账号 用于存储cookie时拼接key
    public static final String BESPEAK_OCS_ACCOUNTID_COOKIE_STRING = "bespeakAccountIdCookie_";

    public static void insertData(String UserName, String PassWord, String partnerid, long TrainOrderId, String cookie) {
        int AccountSrc = 0;
        //途牛
        if (partnerid.contains("tuniu")) {
            AccountSrc = 1;
        }
        else if (partnerid.contains("tongcheng")) {
            AccountSrc = 2;
        }
        //为空
        if (ElongHotelInterfaceUtil.StringIsNull(cookie)) {
            cookie = "";
        }
        if (ElongHotelInterfaceUtil.StringIsNull(UserName)) {
            UserName = "";
        }
        else {
            if (partnerid.contains("wanda")) {
                try {
                    UserName = TuNiuDesUtil.decrypt(UserName);
                }
                catch (Exception e) {
                }
            }
        }
        if (ElongHotelInterfaceUtil.StringIsNull(PassWord)) {
            PassWord = "";
        }
        else {
            try {
                if (partnerid.contains("wanda")) {
                    PassWord = TuNiuDesUtil.decrypt(PassWord);
                }
                PassWord = DesUtil.encrypt(PassWord, "A1B2C3D4E5F60708");
            }
            catch (Exception e) {
            }
        }
        //SQL
        String insertSql = "EXEC sp_TrainAccountSrc_insert  @UserName= '" + UserName + "',@PassWord= '" + PassWord
                + "',@AccountSrc=" + AccountSrc + ",@TrainOrderId= " + TrainOrderId + ",@Cookie='" + cookie + "'";
        //执行
        DBSQLHelper.executeSql(insertSql);
    }

    /**
     * 1、线上 订单
     * 3、线上 订单使用客户 账号下单
     * 4、线上 订单使用客户 账号下单 cookie方式下单
     * 6、线上 订单使用360 账号下单 
     * @param username
     * @param userpassword
     * @param cookie
     * @return
     * @time 2015年10月23日 下午6:58:11
     * @author chendong
     */
    public static int getOrdertype(String username, String userpassword, String cookie, String accountId) {
        int ordertype = 1;
        try {
            if (!ElongHotelInterfaceUtil.StringIsNull(username) && !ElongHotelInterfaceUtil.StringIsNull(userpassword)
                    && ElongHotelInterfaceUtil.StringIsNull(accountId)) {
                ordertype = 3;
            }
            else if (!ElongHotelInterfaceUtil.StringIsNull(cookie)) {
                ordertype = 4;
            }
            else if (!ElongHotelInterfaceUtil.StringIsNull(username)
                    && !ElongHotelInterfaceUtil.StringIsNull(userpassword)
                    && !ElongHotelInterfaceUtil.StringIsNull(accountId)) {

            }
        }
        catch (Exception e) {
        }
        return ordertype;
    }

    /**
     * 根据订单ID获取cookie 360版本（暂时只支持身份验证走）
     * 
     * @param trainOrderId
     * @return
     * @time 2016年10月26日 下午4:40:45
     * @author fiend
     */
    public static Customeruser getTrainAccountSrcById360(long trainOrderId) {
        Customeruser customeruser = new Customeruser();
        try {
            String accountStr = OcsMethod.getInstance().get(BESPEAK_OCS_ACCOUNTID_COOKIE_STRING + trainOrderId);
            if (!ElongHotelInterfaceUtil.StringIsNull(accountStr)) {
                JSONObject accountJsonObject = JSONObject.parseObject(accountStr);
                String accountId = accountJsonObject.getString("accountId");
                String cookie = accountJsonObject.getString("cookie");
                customeruser.setLogpassword(accountId);
                customeruser.setCardnunber(cookie);
                customeruser.setLoginname(accountId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return customeruser;
    }

    /**
     * 根据trainorderid查 
     * cookie 或者用户名
     * 
     * @param trainOrderId
     * @return
     * @time 2015年10月26日 下午5:20:04
     * @author lubing
     */
    public static Customeruser getTrainAccountSrcById(long trainOrderId) {
        Customeruser customeruser = new Customeruser();
        String sql = "select * from TrainAccountSrc with(nolock) where TrainOrderid = " + trainOrderId;
        DataTable resultset = DBSQLHelper.GetDataTable(sql);
        List<DataRow> dataRows = resultset.GetRow();
        if (dataRows.size() > 0) {
            DataRow DataRow = dataRows.get(dataRows.size() - 1);
            //密码
            String PassWord = DataRow.GetColumnString("PassWord");
            //解密
            try {
                PassWord = DesUtil.decrypt(PassWord, "A1B2C3D4E5F60708");
            }
            catch (Exception e) {
            }
            customeruser.setLogpassword(PassWord);
            customeruser.setCardnunber(DataRow.GetColumnString("Cookie"));
            customeruser.setLoginname(DataRow.GetColumnString("UserName"));
            customeruser.setPostalcode(DataRow.GetColumnString("IP12306"));
        }
        return customeruser;
    }

    /**
     * 根据PKId去约票服务去查账号密码
     * 
     * @param trainOrderId
     * @return
     * @time 2015年12月11日 下午2:25:52
     * @author fiend
     */
    public static Customeruser getTrainAccountSrcByIdBespeak(long trainOrderId) {
        Customeruser customeruser = new Customeruser();
        try {
            String bespeakAccountUrl = PropertyUtil.getValue("bespeakAccountUrl", "Train.properties");
            String result = SendPostandGet.submitGet(bespeakAccountUrl + "?PKId=" + trainOrderId, "utf-8");
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.containsKey("success") && jsonObject.getBooleanValue("success")
                    && jsonObject.containsKey("AccountLoginName") && jsonObject.containsKey("AccountLoginPassword")) {
                customeruser.setLoginname(jsonObject.getString("AccountLoginName"));
                customeruser.setLogpassword(jsonObject.getString("AccountLoginPassword"));
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return customeruser;
    }

    public static void main(String[] args) throws Exception {
        //          50C0A9084D71B563F207E8365C52F873
        //          50C0A9084D71B563F207E8365C52F873
        //        insertData("111111", "11111", "11111", 123456);
        String jiami = DesUtil.encrypt("whc13598779009", "A1B2C3D4E5F60708");
        System.out.println(jiami);
        System.out.println(DesUtil.decrypt("jlQCvSld4d078ZJNVqw91w==", "A1B2C3D4E5F60708"));
    }
}
