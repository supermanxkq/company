package com.xukaiqiang.StoredProcedure;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

/**
 * Hello world!
 *
 */
public class App {

		public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
		public static final String URL = "jdbc:mysql://127.0.0.1:3306/test";
		public static final String USERNAME = "root";
		public static final String PASSWORD = "root";

		public static void main(String[] args) throws Exception {
			Class.forName(DRIVER_CLASS);
			Connection connection = DriverManager.getConnection(URL, USERNAME,
					PASSWORD);
			String sql = "{CALL add_pro(?,?,?)}"; // 调用存储过程
			CallableStatement cstm = connection.prepareCall(sql); // 实例化对象cstm
			cstm.setInt(1, 122);
			cstm.setInt(2, 2); 
			cstm.registerOutParameter(3, Types.INTEGER); // 设置返回值类型
			cstm.execute(); // 执行存储过程
			System.out.println(cstm.getInt(3));
			cstm.close();
			connection.close();
	}
}
