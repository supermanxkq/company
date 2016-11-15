package com.aren.aeveryday;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.ccservice.b2b2c.util.db.DataColumn;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.elong.inter.PropertyUtil;

public class DBHelperGP {

    private static Connection GetCONN() {
        Connection result = null;
        try {
            result = cpds3.getConnection();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    };

    private static DruidDataSource cpds3 = null;

    //取得连接
    private static boolean GetPool() {
        if (cpds3 != null)
            return true;
        try {
            cpds3 = new DruidDataSource();
            String sDBUrl = PropertyUtil.getValue("GPDruid.url", "database-config.properties");
            String sUserName = PropertyUtil.getValue("GPDruid.username", "database-config.properties");
            String sPassword = PropertyUtil.getValue("GPDruid.password", "database-config.properties");
            String driverClass = PropertyUtil.getValue("GPDruid.driver", "database-config.properties");

            cpds3.setDriverClassName(driverClass);
            cpds3.setUrl(sDBUrl);
            cpds3.setUsername(sUserName);
            cpds3.setPassword(sPassword);
            //_CONN = cpds3.getConnection();

        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }

    //关闭连接
    private static void CloseConn() {
        try {
            cpds3.close();
            cpds3 = null;
        }
        catch (Exception ex) {
            //			System.out.println(ex.getMessage());
            cpds3 = null;
        }
    }

    private static void CloseConn(Connection myCon) {
        try {
            myCon.close();
            myCon = null;
        }
        catch (Exception ex) {
            //          System.out.println(ex.getMessage());
            myCon = null;
        }
    }

    //测试连接
    public static boolean TestConn() {
        if (!GetPool())
            return false;

        CloseConn();
        return true;
    }

    public ResultSet GetResultSet(String sSQL, Object[] objParams) {
        GetPool();
        ResultSet rs = null;
        Connection myCon = GetCONN();
        try {

            PreparedStatement ps = myCon.prepareStatement(sSQL);
            if (objParams != null) {
                for (int i = 0; i < objParams.length; i++) {
                    ps.setObject(i + 1, objParams[i]);
                }
            }
            rs = ps.executeQuery();
        }
        catch (Exception ex) {
            //			System.out.println(ex.getMessage());
            //CloseConn();
        }
        finally {
            CloseConn(myCon);
        }
        return rs;
    }

    public static Object GetSingle(String sSQL, Object... objParams) {
        GetPool();
        Connection myCon = GetCONN();
        try {
            PreparedStatement ps = myCon.prepareStatement(sSQL);
            if (objParams != null) {
                for (int i = 0; i < objParams.length; i++) {
                    ps.setObject(i + 1, objParams[i]);
                }
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);//索引从1开始
        }
        catch (Exception ex) {
            //			System.out.println(ex.getMessage());
        }
        finally {
            CloseConn(myCon);
        }
        return null;
    }

    public static int GetMaxID(String sTableName, String sKeyField) {
        GetPool();
        Connection myCon = GetCONN();
        try {

            String sSQL = "select isnull(max([" + sKeyField + "]),0) as MaxID from [" + sTableName + "]";
            PreparedStatement ps = myCon.prepareStatement(sSQL);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Integer.parseInt(rs.getString(1));//索引从1开始
        }
        catch (Exception ex) {
            //			System.out.println(ex.getMessage());
        }
        finally {
            CloseConn(myCon);
        }
        return 0;
    }

    @SuppressWarnings("static-access")
    public static DataTable GetDataTable(String sSQL, Object... objParams) {
        GetPool();
        DataTable dt = null;
        Connection myCon = GetCONN();
        try {

            PreparedStatement ps = myCon.prepareStatement(sSQL);
            if (objParams != null) {
                for (int i = 0; i < objParams.length; i++) {
                    ps.setObject(i + 1, objParams[i]);
                }
            }
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            List<DataRow> row = new ArrayList<DataRow>(); //表所有行集合
            List<DataColumn> col = null; //行所有列集合
            DataRow r = null;// 单独一行
            DataColumn c = null;//单独一列

            String columnName;
            Object value;
            int iRowCount = 0;
            while (rs.next())//开始循环读取，每次往表中插入一行记录
            {
                iRowCount++;
                col = new ArrayList<DataColumn>();//初始化列集合
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    columnName = rsmd.getColumnName(i);
                    value = rs.getObject(columnName);
                    c = new DataColumn(columnName, value);//初始化单元列
                    col.add(c); //将列信息加入到列集合
                }
                r = new DataRow(col);//初始化单元行
                row.add(r);//将行信息加入到行集合
            }
            dt = new DataTable(row);
            dt.RowCount = iRowCount;
            dt.ColumnCount = rsmd.getColumnCount();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            //			System.out.println(ex.getMessage());
        }
        finally {
            CloseConn(myCon);
        }
        return dt;
    }

    public static int UpdateData(String sSQL) {
        GetPool();
        int iResult = 0;
        Connection myCon = GetCONN();
        try {
            Statement st = myCon.createStatement();
            iResult = st.executeUpdate(sSQL);
        }
        catch (Exception ex) {
            //			System.out.println(ex.getMessage());
            return -1;
        }
        finally {
            CloseConn(myCon);
        }
        return iResult;
    }

    /**
     * 更新数据
     * @param sSQL
     * @return
     */
    public static int insertSql(String sSQL) {
        GetPool();
        Connection myCon = GetCONN();
        int id = 0;
        try {
            Statement st = myCon.createStatement();
            int row = st.executeUpdate(sSQL, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(row);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return id;
        }
        finally {
            CloseConn(myCon);
        }
        return id;
    }

    /**
     * 更新数据
     * @param sSQL
     * @return
     */
    public static boolean executeSql(String sSQL) {
        GetPool();
        Connection myCon = GetCONN();
        boolean iResult = false;
        try {
            Statement st = myCon.createStatement();
            iResult = st.execute(sSQL);
        }
        catch (Exception ex) {
            System.out.println(sSQL + ":" + ex.getMessage());
        }
        finally {
            CloseConn(myCon);
        }
        return !iResult;
    }
}