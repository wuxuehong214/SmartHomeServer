package com.yj.smartHome.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.yj.smartHome.db.DbFactory;


public class DataBaseOperator {

	
	public void insertId(String userId, String password, String deviceId,
			String remark) {
		Connection conn = DbFactory.getConnection();
		String strsql = "INSERT INTO YJ_Device(user_id,user_pwd,device_id,remark) VALUES (?,?,?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(strsql);
			pstmt.setString(1, userId);
			pstmt.setString(2, password);
			pstmt.setString(3, deviceId);
			pstmt.setString(4, remark);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}
