package com.yj.smartHome.main;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import com.yj.smartHome.dialogs.DetailGateWayDialog;
import com.yj.smartHome.pojo.GateStatus;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.SampleModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * 湖南永景科技网关通讯服务器主界面
 * @author Administrator
 *
 */
public class MainFrame extends JFrame{
	
	private int port = 6000;           //服务器监听端口
	private int threadsNum = 5;        //IO处理线程个数
	private MinaServer minaServer;
	
	private JButton lb_curNum;         //用户显示当前在线的网关数目
	private JButton btn_start;         //启动服务按钮
	private JButton btn_stop;          //停止服务按钮
	private JComboBox com_thread;      //处理连接的线程个数
	private JLabel lb_time;            //服务器启动时间
	private JTextField textField;      //服务器端口编辑框
	
	public MainFrame() {
		//界面启动的时候  初始化服务器操作实例
		minaServer = new MinaServer(this);
		setTitle("湖南永景科技网关通讯服务器");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(410, 269);
		this.setLocation(400, 100);
		this.setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel("服务器端口:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(10, 20, 187, 26);
		getContentPane().add(label);
		
		textField = new JTextField();
//		textField.setEditable(false);
		textField.setText("6000");
		textField.setBounds(207, 23, 165, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		com_thread = new JComboBox();
		com_thread.setModel(new DefaultComboBoxModel(new String[] {"默认", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
		com_thread.setSelectedIndex(0);
		com_thread.setBounds(207, 64, 94, 21);
		getContentPane().add(com_thread);
		
		lb_time = new JLabel("----");
		lb_time.setBounds(207, 160, 165, 21);
		getContentPane().add(lb_time);
		
		btn_stop = new JButton("停止服务");
		btn_stop.setEnabled(false);
		btn_stop.setBounds(207, 206, 153, 23);
		getContentPane().add(btn_stop);
		
		btn_start = new JButton("启动服务");
		btn_start.setBounds(44, 206, 153, 23);
		getContentPane().add(btn_start);
		
		JLabel label_3 = new JLabel("当前在线网关数:");
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);
		label_3.setBounds(10, 110, 187, 26);
		getContentPane().add(label_3);
		
		lb_curNum = new JButton("0");
		lb_curNum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<GateStatus> list = new ArrayList<GateStatus>(minaServer.getMap().values());
				Collections.sort(list);
				DetailGateWayDialog dialog = new DetailGateWayDialog(list);
				dialog.setVisible(true);
			}
		});
		lb_curNum.setBounds(207, 112, 94, 23);
		getContentPane().add(lb_curNum);
		
		JLabel label_4 = new JLabel("服务器启动时间:");
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		label_4.setBounds(10, 157, 187, 26);
		getContentPane().add(label_4);
		
		JLabel lblio = new JLabel("服务器IO处理线程个数:");
		lblio.setHorizontalAlignment(SwingConstants.RIGHT);
		lblio.setBounds(10, 61, 187, 26);
		getContentPane().add(lblio);
		
		/**
		 * 启动服务
		 */
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				port = Integer.parseInt(textField.getText());
				if(com_thread.getSelectedIndex() == 0) threadsNum = 0;
				else threadsNum = Integer.parseInt(com_thread.getSelectedItem().toString());
				System.out.println(port+"\t"+threadsNum);
				if(minaServer.startService()){
					serverStarted();
				}
			}
		});
		
		/**
		 * 停止服务
		 */
		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				minaServer.stopServer();
				serverStopped();
			}
		});
	}
	
	/**
	 * 服务启动后   界面更新
	 */
	public void serverStarted(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		lb_time.setText(sdf.format(new Date()));
		textField.setEnabled(false);
		com_thread.setEnabled(false);
		btn_start.setEnabled(false);
		btn_stop.setEnabled(true);
	}
	/**
	 * 服务器异常后   服务停止后  界面更新
	 */
	public void serverStopped(){
		textField.setEnabled(true);
		btn_start.setEnabled(true);
		btn_stop.setEnabled(false);
		com_thread.setEnabled(true);
		lb_time.setText("----");
	}
	
	/**
	 * 设置当前在线的网关数目
	 */
	public void setCurGateWayNum(int curNum){
		if(lb_curNum != null)
			lb_curNum.setText(curNum+"");
	}
	
	public static void main(String args[]){
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
	}

	public int getPort() {
		return port;
	}

	public int getThreadsNum() {
		return threadsNum;
	}
}
