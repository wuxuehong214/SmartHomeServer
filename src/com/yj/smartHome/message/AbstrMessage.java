package com.yj.smartHome.message;

/**
 * 抽象消息类型
 * @author wuxuehong
 *
 * @date 2012-9-10
 */
public abstract class AbstrMessage {
	
	public static final byte[] HEAD = {(byte)0x59,(byte)0x4A}; //包协议头  YJ
	public static final byte CLIENT_IP_REQUEST = (byte)0xF4; //客户端请求网关IP
	public static final byte GATEWAY_STATUS_REQUEST = (byte)0xE0; //网关发送身份信息
	public static final byte GATEWAY_WARN_REQUEST = (byte)0xF6; //网关发送报警信息
	
	public static final byte USERINFO_ERROR_FAIL = (byte)0xE0;  //用户信息验证失败
	public static final byte IP_REQUESST_SUCCESS = (byte)0xE1;//IP请求成功
	public static final byte USERINFO_NOTEXIST_FAIL = (byte)0xE2; //账户信息不存在
	public static final byte GATEWAY_OUTLINE_FAIL = (byte)0xE3; //网关不在线
	public static final byte GATEWAY_LOCK_LEVEL2=(byte) 0xF7;

	/**
	 * 请求类型
	 * @return
	 */
	public abstract byte getRequestType();
}
