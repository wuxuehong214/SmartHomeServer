package com.yj.smartHome.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import com.yj.smartHome.pojo.GateStatus;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DetailGateWayDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTable table;
	private ArrayList<GateStatus> list;  //网关信息

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
//			DetailGateWayDialog dialog = new DetailGateWayDialog();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DetailGateWayDialog(ArrayList<GateStatus> list) {
		this.list = list;
		setTitle("在线网关信息");
		setBounds(100, 100, 620, 478);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			{
				textField = new JTextField();
				panel.add(textField);
				textField.setColumns(30);
			}
			{
				JButton btnNewButton = new JButton("搜索");
				panel.add(btnNewButton);
			}
		}
		{
			DefaultTableModel model = new DefaultTableModel(null,new String[]{"用户名","网关ID","IP地址","最后验证时间"}){
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table = new JTable(model);
			table.setVisible(true);
			table.setColumnSelectionAllowed(true);
			table.setCellSelectionEnabled(true);
			table.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(int i=0;i<list.size();i++){
					GateStatus gs = list.get(i);
					model.addRow(new String[]{gs.getUserid(),gs.getId(),getIp(gs.getIp()),sdf.format(gs.getLatestHeartTime())});
			}
			JScrollPane js=new JScrollPane();
			js.setViewportView(table);
			contentPanel.add(js, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						  DetailGateWayDialog.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	public String getIp(byte[] ip){
		if(ip.length != 4) return "";
		String r;
		r  = ""+(ip[0]&0xff)+":"+(ip[1]&0xff)+":"+(ip[2]&0xff)+":"+(ip[3]&0xff);
		return r;
	}
	

}
