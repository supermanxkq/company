package com.ccservice.b2b2c.util.db;

import java.sql.ResultSet;
import java.util.List;

import com.ccservice.b2b2c.base.train.Trainorderchange;

public class DBSQLHelper extends DBHelper {
    /**
     * 获取Sysconfig配置
     **/
    public String GetSysconfig(String name) throws Exception {
        if (name == null) {
            name = "";
        }
        String value = "";
        DataTable resultset = GetDataTable("EXEC [dbo].[sp_SYSCONFIG_SELECT] '" + name + "'");
        List<DataRow> dataRows = resultset.GetRow();
        if (dataRows.size() > 0) {
            DataRow datarow = dataRows.get(0);
            value = datarow.GetColumnString("C_VALUE");
        }
        ;
        return value;
    }

    /**
     * 获取TrainOrder
     **/
    public ResultSet GetTrainOrder(long OrderId) throws Exception {
        ResultSet resultset = GetResultSet("EXEC [dbo].[sp_TRAINORDER_SELECT] " + OrderId, null);
        return resultset;
    }

    /**
     * 更新Trainticket
     **/
    public ResultSet SetTrainticket(long changeid, int status, int statusw, String updateTicketId) throws Exception {
        ResultSet resultset = GetResultSet("EXEC [dbo].[sp_TRAINTICKET_UPDATE] " + changeid + "," + status + "," + statusw + ",'" + updateTicketId + "'", null);
        return resultset;
    }

    /**
     * 查询订单所有改签
     **/
    public ResultSet GetTrainorderChange(long orderId) throws Exception {
        ResultSet resultset = GetResultSet("EXEC [dbo].[sp_TRAINORDERCHANGE_SELECT] " + orderId, null);
        return resultset;
    }

    /**
     * 插入改签表
     **/
    public ResultSet SetTrainorderchange(Trainorderchange trainorderchange) throws Exception {
        ResultSet resultset = GetResultSet("EXEC [dbo].[sp_TRAINORDERCHANGE_INSERT] " + trainorderchange.getOrderid(), null);
        return resultset;
    }

    /**
     * 插入改签表
     **/
    public ResultSet GetTrain12306StationInfoUtil(String name) throws Exception {
        ResultSet resultset = GetResultSet("EXEC sp_StationName_getThreeByNamefromdb @SName = '" + name + "'", null);
        return resultset;
    }

    public static void main(String[] args) {
        try {
            String resultSet = new DBSQLHelper().GetSysconfig("12306AccountUrl");
            System.out.println(resultSet);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 向TrainAccountSrc表插入数据
     * 
     * @time 2015年10月21日 下午12:50:28
     * @author lubing
     */
    public static boolean insertTrainAccountSrc(String UserName, String PassWord, int AccountSrc, long TrainOrderId, String Cookie) {
        return executeSql("EXEC sp_TrainAccountSrc_insert  @UserName= '" + UserName + "'," + " @PassWord= '" + PassWord + "'," + " @AccountSrc= " + AccountSrc + ", @TrainOrderId= " + TrainOrderId
                + "@Cookie=" + Cookie);
    }

}
