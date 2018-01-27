package com.yj.smartHome.main;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.yj.smartHome.codec.MyMessageCodecFactory;
import com.yj.smartHome.codec.MyMessageDecoder;
import com.yj.smartHome.codec.MyMessageEncoder;
import com.yj.smartHome.db.DBquery;
import com.yj.smartHome.db.impl.DBqueryImpl;
import com.yj.smartHome.hander.ServerHandler;
import com.yj.smartHome.main.MinaServer;
import com.yj.smartHome.pojo.GateStatus;

/**
 * 主服务器
 * @author wuxuehong
 *
 * @date 2012-9-4
 */
public class MinaServer {
	
	//日志对象
	private Logger logger = Logger.getLogger(MinaServer.class);
	//设备ID 到设备信息存储容器
	private Map<String, GateStatus> map;// 维持一个公共的map;
	//数据库操作
	private DBquery dBqueryImpl;
	//临时队列用于存储需要移除的设备ID
	private List<String> list;
	//接收器对象
	private IoAcceptor acceptor;
	//标识当前服务是否启动
	private boolean running = false;
	
	private MainFrame frame;
	/**
	 * 构造函数
	 */
	public MinaServer(MainFrame frame) {
		dBqueryImpl = new DBqueryImpl();
		list = new ArrayList<String>();
		map = new HashMap<String, GateStatus>();
		this.frame = frame;
	}
	
	/**
	 * 启动服务
	 * @param period
	 * @param timeout
	 * @return
	 */
	public boolean startService(){
		try {
			// 创建一个非阻塞的server端的Socket;默认为处理器个数+1
			if(frame.getThreadsNum() <= 0)
				acceptor = new NioSocketAcceptor();
			else
				acceptor = new NioSocketAcceptor(frame.getThreadsNum());
			
			// 设置过滤器（添加自带的编解码器）
			acceptor.getFilterChain().addLast(
					"codec",
					new ProtocolCodecFilter(new MyMessageCodecFactory(
							new MyMessageDecoder(Charset.forName("utf-8")),
							new MyMessageEncoder(Charset.forName("utf-8")))));
			
			// 绑定逻辑处理器
			acceptor.setHandler(new ServerHandler(this));
			
			//5秒后连接内没有数据传输  则收回连接处理线程 连接进入空闲状态
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 20);
			
			// 绑定端口
			acceptor.bind(new InetSocketAddress(frame.getPort()));
			logger.info("主服务器启动成功...     端口号为：" + frame.getPort());
			
			if(!running){
				running = true;
				//初始化包含IP信息的 网关信息
				initMap();
			}
		} catch (Exception e) {
			running = false;
			logger.error("服务端启动异常....", e);
			stopServer();
			return false;
		}
		return true;
	}
	
	/**
	 * 停止服务
	 */
	public void stopServer(){
		acceptor.unbind();
		acceptor.dispose();
		acceptor = null;
	}
	
	/**
	 * 根据设备ID 获取网关设备信息
	 * @param id
	 * @return
	 */
	public GateStatus getGate(String id){
		return map.get(id);
	}

	/**
	 * 新增上线网关信息到map
	 * @param gate
	 * @throws SQLException 
	 */
	public void addToMap(GateStatus gate) throws SQLException{
		GateStatus temp = map.get(gate.getId());
		//如果map中不存在 则将其加入map并且将设备状态置为在线
		if(temp == null){
			//更新网关设备状态为在线
			int r = 0;
			r = dBqueryImpl.setOnoffOne(gate.getId(), DBquery.ON);
			if(r > 0){
				synchronized (map) {
					map.put(gate.getId(), gate);
				}
				logger.info("新增网关状态信息到map,并且更新网关状态为在线!");
			}else{
				logger.warn("网关在线状态信息更新失败，可能该网关未注册，或者数据库操作失败!!");
			}
		}else{
			//如果已经存在于map中则更新网关ip信息 以及最新登陆时间
			temp.setIp(gate.getIp());
			temp.setLatestHeartTime(gate.getLatestHeartTime());
			logger.info("更新网关状态信息到map");
		}
		dBqueryImpl.updateGateStatus(gate);
		frame.setCurGateWayNum(map.size());
	}
	
	/**
	 * 系统启动 初始化MAP
	 * @throws SQLException 
	 */
	public void initMap() throws SQLException{
		try{
			//初始化将所有网关在线状态置为离线
			dBqueryImpl.resetOnoff();
			List<GateStatus> list = dBqueryImpl.getAllGates();
			for(int i=0;i<list.size();i++){
				GateStatus gate = list.get(i);
				map.put(gate.getId(), gate);
			}
			frame.setCurGateWayNum(map.size());
		}catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, GateStatus> getMap() {
		return map;
	}
	
}
