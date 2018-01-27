package com.yj.smartHome.message;

/**
 * 服务器端反馈给客户端请求IP结果    包含 请求结果以及IP地址信息
 * @author wuxuehong
 *
 * @date 2012-9-10
 */
public class ClientIpResponseMsg extends AbstrMessage {

	private byte[] ip = null;
	private byte loginResult;

	public byte[] getIp() {
		return ip;
	}

	public void setIp(byte[] ip) {
		this.ip = ip;
	}

	public byte getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(byte loginResult) {
		this.loginResult = loginResult;
	}

	@Override
	public byte getRequestType() {
		// TODO Auto-generated method stub
		return CLIENT_IP_REQUEST;
	}

}
