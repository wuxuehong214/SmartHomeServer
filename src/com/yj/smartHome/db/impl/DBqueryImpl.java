package com.yj.smartHome.db.impl;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yj.smartHome.db.DBquery;
import com.yj.smartHome.db.DbFactory;
import com.yj.smartHome.pojo.GateStatus;

/**
 * 数据库操作实现类
 * @author wuxuehong
 *
 * @date 2012-9-10
 * 
 */
public class DBqueryImpl implements DBquery {
    
	private Logger logger = Logger.getLogger(DBqueryImpl.class);
	
	/**
	 * 根据用户名密码查询设备ID
	 * 
	 * return   USER_NOT_EXIST / PASSWORD_ERROR  /  LEVEL2_LOCK  / device_id
	 */
	public String gateIdQuery(String username, String password) throws SQLException{
		Connection conn=DbFactory.getConnection();
		String strsql = "select user_pwd,device_id,level2 from YJ_Device where user_id = ?";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(strsql);
			pstmt.setString(1,username);
			rs = pstmt.executeQuery();
			if (!rs.next()) {
				return USER_NOT_EXIST;
			}
			String dbpassword=rs.getString("user_pwd");//查询数据库中的password
			int level2=rs.getInt("level2");
			if (!password.equals(dbpassword)) {
				return PASSWROD_ERROR;
			}
			if(level2==1){
				return LEVEL2_LOCK;
			}
			String device_id=rs.getString("device_id");//查询数据库中的gateid
			rs.close();
			pstmt.close();
			return device_id;
		} catch (SQLException e) {
			logger.warn("query device id exception!", e);
			if(conn != null)
				 DbFactory.closeConn(conn);
			throw e;
		} 
	}
	
	@Override
	public void resetOnoff() throws SQLException{
		Connection conn=DbFactory.getConnection();
		String strsql = "update YJ_Device set onoff=0";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(strsql);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			logger.warn("rest onoff be zero exception!", e);
			if(conn != null)
				 DbFactory.closeConn(conn);
			throw e;
		}
	}
	
	@Override
	public int setOnoffOne(String device_id, int state) throws SQLException{
		int r = 0;
		Connection conn=DbFactory.getConnection();
		String strsql = "update YJ_Device set onoff=? where device_id=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(strsql);
			pstmt.setInt(1, state);
			pstmt.setString(2,device_id);
			r = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			logger.warn("set onoff of the device exception !", e);
			if(conn != null)
				 DbFactory.closeConn(conn);
			throw e;
		}
		return r;
	}

	@Override
	public int updateGateStatus(GateStatus gate) throws SQLException{
		// TODO Auto-generated method stub
		int r = 0;
		Connection conn=DbFactory.getConnection();
		String strsql = "update YJ_Device set ip=?,lasttime=? where device_id=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(strsql);
			pstmt.setString(1, GateStatus.ip2Str(gate.getIp()));
			pstmt.setTimestamp(2, new Timestamp(gate.getLatestHeartTime().getTime()));
			pstmt.setString(3, gate.getId());
			r = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			logger.warn("set onoff of the device exception !", e);
			if(conn != null)
				 DbFactory.closeConn(conn);
			throw e;
		}
		return r;
	}

	@Override
	public List<GateStatus> getAllGates() throws SQLException{
		List<GateStatus> list = new ArrayList<GateStatus>();
		Connection conn=DbFactory.getConnection();
		String strsql = "select user_id,device_id,ip,lasttime from YJ_Device";
		PreparedStatement pstmt;
		ResultSet rs = null;
		try{
			pstmt = conn.prepareStatement(strsql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				String ip = rs.getString("ip");
				byte[] b = GateStatus.str2Ip(ip);
				if(b == null) continue;
				GateStatus gate = new GateStatus();
				gate.setIp(b);
				gate.setUserid(rs.getString("user_id"));
				gate.setId(rs.getString("device_id"));
				Timestamp date = rs.getTimestamp("lasttime");
				gate.setLatestHeartTime(new java.util.Date(date.getTime()));
				list.add(gate);
			}
		}catch (SQLException e) {
			if(conn != null)
				 DbFactory.closeConn(conn);
			throw e;
		}
		return list;
	}
	
}
