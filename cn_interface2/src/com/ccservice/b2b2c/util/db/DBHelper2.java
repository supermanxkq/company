package com.ccservice.b2b2c.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.ccservice.elong.inter.PropertyUtil;

public class DBHelper2 {

    private static Connection GetCONN() {
        Connection result = null;
        try {
            result = cpds.getConnection();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    };

    private static DruidDataSource cpds = null;

    //取得连接
    private static boolean GetPool() {
        if (cpds != null)
            return true;
        try {
            cpds = new DruidDataSource();
            String sDBUrl = PropertyUtil.getValue("druid2.url", "database-config.properties");//"jdbc:jtds:sqlserver://120.26.205.161:1433/B2B_DB_BESPEAK";
            String sUserName = PropertyUtil.getValue("druid2.username", "database-config.properties");//"sa";
            String sPassword = PropertyUtil.getValue("druid2.password", "database-config.properties");//"5n0wbIrd";
            String driverClass = PropertyUtil.getValue("druid2.driver", "database-config.properties");//"net.sourceforge.jtds.jdbc.Driver";

            cpds.setDriverClassName(driverClass);
            cpds.setUrl(sDBUrl);
            cpds.setUsername(sUserName);
            cpds.setPassword(sPassword);
            //_CONN = cpds.getConnection();

        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }

    //关闭连接
    private static void CloseConn() {
        try {
            cpds.close();
            cpds = null;
        }
        catch (Exception ex) {
            //			System.out.println(ex.getMessage());
            cpds = null;
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
            System.out.println(ex.getMessage());
        }
        finally {
            CloseConn(myCon);
        }
        return !iResult;
    }
}