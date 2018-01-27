package com.yj.smartHome.message;

/**
 * 客户端请求网关IP  消息类型 包含用户名和密码
 * @author wuxuehong
 *
 * @date 2012-9-10
 */
public class ClientIpRequestMsg extends AbstrMessage {

	private String username; //用户名
	private String password; //密码

	public String getUsername() {
		return username;  
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte getRequestType() {
		return CLIENT_IP_REQUEST;
	}

}
