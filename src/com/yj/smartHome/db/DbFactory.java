package com.yj.smartHome.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 用于取得数据库连接的类
 * @author wuxuehong
 *
 * @date 2012-9-8
 */
public class DbFactory {

	// 获取数据库连接
	/**
	 * 创建一个数据库连接
	 * 
	 * @return 一个数据库连接
	 */
	 private static String DRIVER =	"com.microsoft.sqlserver.jdbc.SQLServerDriver";
	 private static String url = "jdbc:sqlserver://124.232.154.54:1433;databaseName=YongJing";
	 private static String username = "sa";
	 private static String password = "17q98790556bz13";
	 private static Connection conn = null;

	 private DbFactory(){}
	 
	 /**0
	  * 获取数据库连接
	  * @return
	  */
	 public static Connection getConnection() {
		if (conn != null)
			return conn;
		else {
			// 创建数据库连接
			try {
				 try {
				 Class.forName(DRIVER);
				 } catch (ClassNotFoundException e) {
					 e.printStackTrace();
					 conn = null;
				 }
				conn = DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {
				e.printStackTrace();
				conn = null;
			}
			return conn;
		}

	}

	/**
	 * 关闭数据库连接
	 * @param conn
	 */
	public  static void closeConn(Connection conn) {
		if (conn == null)
			return;
		try {
			if (!conn.isClosed()) {
				// 关闭数据库连接
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		conn = null;
	}
	
	public static void main(String[] args) {
		Connection conn = DbFactory.getConnection();
		if (conn != null) {
			System.out.println("database sussess");
		}
	}
}