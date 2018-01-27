package com.yj.smartHome.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterGateWay {
	
	Map<String,Byte> map = new HashMap<String,Byte>();
	
	String prefix = "f";
	
	public RegisterGateWay() throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("gate_f.txt")));
		DataBaseOperator database = new DataBaseOperator();
		for(int i=0;i<500;i++){
			String[] a = produceId();
			String deviceid = a[0];  //设备ID存储到数据库 长度24
			String byteArray = a[1]; //转换成字符串的字节数组  以：隔开
			if(map.get(deviceid) != null){
				System.err.println("重复了!"+deviceid);
				 i--;
				 continue;
			}
			map.put(deviceid, (byte)1);
			int index = 10000+(i+1);
			String userId = prefix+String.valueOf(index).substring(1);
			database.insertId(userId, "123456", deviceid, byteArray);
			bw.write(userId+"\t"+byteArray);
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
	
	
	public static void main(String args[]) throws IOException{
		new RegisterGateWay();
	}
	
	
	/**
	 * 生成12字节组成的字符串
	 * 0-16进制字符串  1-十进制字符串以:隔开
	 * @return
	 */
	public String[] produceId(){
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		Random random = new Random();
		for(int i=0;i<12;i++){
			int a = random.nextInt(255);
			String temp = Integer.toHexString(a);
			if(temp.length()<2)sb.append("0");
				sb.append(temp);
			sb2.append((byte)a);
			if(i != 11) sb2.append(":");
		}
		return new String[]{sb.toString(),sb2.toString()};
	}

}
