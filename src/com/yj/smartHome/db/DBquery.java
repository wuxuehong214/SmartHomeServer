package com.yj.smartHome.db;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import com.yj.smartHome.pojo.GateStatus;

/**
 * 数据库操作
 * @author wuxuehong
 *
 * @date 2012-9-5
 */
public interface DBquery {
	
	/**
	 * 常量定义
	 */
	public static final String USER_NOT_EXIST = "userNotExist";//用户信息不存在
	public static final String PASSWROD_ERROR = "passwordError";//密码错误
    public static final String LEVEL2_LOCK="LEVEL2_LOCK";
    public static final int ON = 1;          //网关在线状态
    public static final int OFF = 0;         //网关在离线状态
	
	// 查询网关id,(1)若用户名不存在：则返回"userNotExist" (2)若密码错误
	// 则返回"passwordError"(3)若用户名密码都正确 ，则返回查询到的网关ID
    
    /**
     * 
     * @param username
     * @param password
     * @return   网关设备唯一ID
     * @throws SQLException
     */
	String gateIdQuery(String username, String password) throws SQLException;
	
	/**
	 *将网关设备状态清0 
	 *@throws SQLException
	 */
	void resetOnoff() throws SQLException;
	
	/**
	 * 
	 * @return  1/0
	 * @throws SQLException 
	 */
	int updateGateStatus(GateStatus gate) throws SQLException;
	
	
	/**
	 * 获取所有包含IP信息的网关信息
	 * @return
	 * @throws SQLException
	 */
	List<GateStatus> getAllGates() throws SQLException;
	
	/**
	 * 将指定的网关设备状态置为state
	 * @param device_id
	 * @param state
	 * @return  返回受影响的行数
	 * @throws SQLException
	 */
    int setOnoffOne(String device_id,int state)throws SQLException;
}
