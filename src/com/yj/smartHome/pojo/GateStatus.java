package com.yj.smartHome.pojo;

import java.util.Arrays;
import java.util.Date;

/**
 * 网关设备信息
 * @author wuxuehong
 *
 * @date 2012-9-5
 */
public class GateStatus implements Comparable{

	//设备对应的用户名
	private String userid;
	//网关设备唯一ID
	private String id;
	//网关设备对外IP
	private byte[] ip;
	//网关设备上一次发送身份请求时间
	private Date latestHeartTime;
	

	public GateStatus() {
	}
	
	public GateStatus(String id, byte[] ip, Date latestHeartTime) {
		this.id = id;
		this.ip = ip;
		this.latestHeartTime = latestHeartTime;
	}

	public byte[] getIp() {
		return ip;
	}

	public void setIp(byte[] ip) {
		this.ip = ip;
	}

	public Date getLatestHeartTime() {
		return latestHeartTime;
	}

	public void setLatestHeartTime(Date latestHeartTime) {
		this.latestHeartTime = latestHeartTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		GateStatus gs = (GateStatus)o;
		return this.getId().compareTo(gs.getId());
	}
	
	/**
	 * byte arrays to str
	 * @param ip
	 * @return
	 */
	public static String ip2Str(byte[] ip){
		if(ip == null ||ip.length != 4) return "";
		String r;
		r  = ""+(ip[0]&0xff)+"."+(ip[1]&0xff)+"."+(ip[2]&0xff)+"."+(ip[3]&0xff);
		return r;
	}
	
	/**
	 * str to byte arrays
	 * @param ip
	 * @return
	 */
	public static byte[] str2Ip(String ip){
		if(ip == null || "".equals(ip)) return null;
		String[] str = ip.split("\\.");
		if(str.length != 4) return null;
		byte[] d = new byte[4];
		for(int i=0;i<4;i++)d[i] = (byte) Integer.parseInt(str[i]);
		return d;
	}
	
	public static void main(String args[]){
		String a = "221";
		byte b = (byte) Integer.parseInt(a);
		System.out.println(b&0xff);
	}
}
