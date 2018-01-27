package com.yj.smartHome.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;
import java.util.Scanner;

/**
 * 模拟网关设备
 * @author Administrator
 *
 */
public class SimulatedGateWay {
	
	private String ip;
	private String file;
	private int period = 1;
	
	public SimulatedGateWay(String[] args)throws IOException{
		if(args == null || args.length != 3){
			System.out.println("bad paramaters!");
			return;
		}
		ip = args[0];
		file = args[1];
		period = Integer.parseInt(args[2]);
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String str = br.readLine();
		Scanner s = null;
		while(str != null){
			s = new Scanner(str);
			s.next();
			new Thread(new GateWay(s.next())).start();
			str = br.readLine();
		}
	}
	
	class GateWay implements Runnable{
		String byteArr = null;
		public GateWay(String byteArr) {
			this.byteArr = byteArr;
		}
		@Override
		public void run() {
			String[] str = byteArr.split(":");
			byte[] id = new byte[str.length];
			for(int i=0;i<str.length;i++)
				id[i] = Byte.parseByte(str[i]);
			
			int len = 5+id.length+1;
			byte[] request = new byte[len];
			request[0] = 0x59;
			request[1] = 0x4a;
			request[2] = (byte)0xe0;
			request[3] = (byte)(id.length+1);
			request[4] = (byte)id.length;
			System.arraycopy(id, 0, request, 5, id.length);
			request[len-1] = 0x01;
			
			Random rand = new Random();
			int delay = rand.nextInt(60000);
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while(true){
				SocketAddress address = new InetSocketAddress(ip, 6000);
				Socket socket = new Socket();
				try {
					socket.connect(address);
					OutputStream os = socket.getOutputStream();
					os.write(request);
					os.flush();
//					System.out.println("Login to internet:"+Thread.currentThread());
					os.close();
					socket.close();
				} catch (IOException e) {
					System.out.println("connect fail!"+Thread.currentThread());
				}
				try {
					Thread.sleep(period*60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void main(String args[]) throws IOException{
		new SimulatedGateWay(args);
	}

}
