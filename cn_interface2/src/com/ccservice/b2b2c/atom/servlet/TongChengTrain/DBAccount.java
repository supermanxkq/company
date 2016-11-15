package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

public class DBAccount {
    /**
     * 手机端取账号
     * @param top 人数
     * @param ContactNum 15
     * @param flag 1
     * @param ID
     * @return List<Customeruser>
     */
    public synchronized List<Customeruser> getDBCanUseAccount(int top, int ContactNum, int flag, long ID) {
        List<Customeruser> users = new ArrayList<Customeruser>();
        if (top <= 0) {
            return users;
        }
        int isenable = flag;
        //取账号排序分早上及其他时间段
        String sql = "select top " + top + " ID, C_LOGINNAME, C_LOGPASSWORD, C_LOGINNUM, C_MOBILE,"
                + " C_CARDNUNBER, C_MEMBEREMAIL, ISNULL(C_CANCELTOTAL, 0) C_CANCELTOTAL, C_ISENABLE"
                + " from T_CUSTOMERUSER with(nolock) where ID>" + ID + "  AND C_TYPE = 4 and C_ISENABLE = " + isenable
                + " and C_STATE = 0 and C_ENNAME = '1' and C_LOGINNUM < " + 15;
        //不用当天注册的账号
        sql += " and C_CREATETIME < '" + getCurrentDate() + "'";
        //乘客数小到大
        sql += " order by C_LOGINNUM,ID asc";
        //创建时间正序
        // sql += " order by C_CREATETIME";
        //非空，直接取
        //创建时间倒序
        // sql += " order by C_CREATETIME desc";
        //查询库中数据
        System.out.println(sql);
        DataTable dattaTable = DBHelperAccount.GetDataTable(sql, null);
        List<DataRow> dataRows = dattaTable.GetRow();
        for (DataRow dataRow : dataRows) {
            Customeruser user = new Customeruser();
            user.setId(dataRow.GetColumnLong("ID"));
            user.setLoginname(dataRow.GetColumnString("C_LOGINNAME"));
            System.out.println(dataRow.GetColumnString("C_LOGINNAME"));
            user.setLogpassword(dataRow.GetColumnString("C_LOGPASSWORD"));
            // user.setLoginnum(dataRow.GetColumnInt("C_LOGINNUM"));
            // user.setMobile(dataRow.GetColumnString("C_MOBILE"));
            // user.setCardnunber(dataRow.GetColumnString("C_CARDNUNBER"));
            // user.setMemberemail(dataRow.GetColumnString("C_MEMBEREMAIL"));
            // user.setIsenable(dataRow.GetColumnInt("C_ISENABLE"));
            // System.out.println(user.toString());
            users.add(user);

        }

        return users;
    }

    /**
     * 获取当前时间 格式为yyyy-MM-dd
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
}
