package com.yj.smartHome.hander;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;

import com.yj.smartHome.db.DBquery;
import com.yj.smartHome.db.impl.DBqueryImpl;
import com.yj.smartHome.main.MinaServer;
import com.yj.smartHome.message.AbstrMessage;
import com.yj.smartHome.message.GatewayStatusMsg;
import com.yj.smartHome.message.ClientIpRequestMsg;
import com.yj.smartHome.message.ClientIpResponseMsg;
import com.yj.smartHome.pojo.GateStatus;

/**
 * 服务器业务处理
 * 
 * @author wuxuehong
 * 
 *         2012-5-3
 */
public class ServerHandler extends IoHandlerAdapter {
	
	public static Logger logger = Logger.getLogger(ServerHandler.class);
	private MinaServer mina; 
	
	private int count;

	public ServerHandler(MinaServer mina) {
		this.mina = mina;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
//		 logger.info("服务端与客户端创建连接..."+Thread.currentThread());
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
//		 logger.info("服务端与客户端连接打开...");
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		AbstrMessage msg = (AbstrMessage) message;
		byte requestType = msg.getRequestType();
		switch (requestType) {
		// 网关发送身份信息请求
		case AbstrMessage.GATEWAY_STATUS_REQUEST: 
			GatewayStatusMsg gsm = (GatewayStatusMsg) message;
			String id = gsm.getId();// 得到网关ID
			logger.info("此网关ID: " + id);
			byte[] clientIP = null;// 网关IP
			clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getAddress();
			logger.info("此网关IP: " + byte2Int(clientIP[0]) + "."+ byte2Int(clientIP[1]) + "." + byte2Int(clientIP[2]) + "."+ byte2Int(clientIP[3]));
			GateStatus gate = new GateStatus(id, clientIP, new Date());
			mina.addToMap(gate);
			session.close(true);
			break;
		//客户端请求网关IP
		case AbstrMessage.CLIENT_IP_REQUEST:     
			ClientIpRequestMsg cim = (ClientIpRequestMsg) message;
			String username = cim.getUsername();
			String password = cim.getPassword();
			ClientIpResponseMsg cirm = new ClientIpResponseMsg();

			//首先查询数据库  验证身份信息
			DBquery query = new DBqueryImpl();
			//查询网关设备ID
			String result = query.gateIdQuery(username, password);
			if (DBquery.USER_NOT_EXIST.equals(result)) {
				logger.info("用户名不存在!");
				cirm.setLoginResult(AbstrMessage.USERINFO_NOTEXIST_FAIL);// 登陆结果设为用户名不存在
				cirm.setIp(new byte[] { 0x00, 0x00, 0x00, 0x00 });
			} else if (DBquery.PASSWROD_ERROR.equals(result)) {
				logger.info("密码错误!");
				cirm.setLoginResult(AbstrMessage.USERINFO_ERROR_FAIL);// 登陆结果设为用户名或密码错误
				cirm.setIp(new byte[] { 0x00, 0x00, 0x00, 0x00 });
			} else if (DBquery.LEVEL2_LOCK.equals(result)) {
				logger.info("网关已被二级锁定!");
				cirm.setLoginResult(AbstrMessage.GATEWAY_LOCK_LEVEL2);
				cirm.setIp(new byte[] { 0x00, 0x00, 0x00, 0x00 });
			} else {
				logger.info("用户身份验证通过!");
				GateStatus gateStatus = mina.getGate(result);
				if (gateStatus != null) { // 当前map中存在这个id
					gateStatus.setUserid(username); //更新网关对象中用户名信息  一旦出现用户名信息 则说明该网关设备经远程登陆过
					byte[] ip = gateStatus.getIp();
					cirm.setLoginResult((byte) 0xE1);// 登陆结果设为请求成功成功
					cirm.setIp(ip);
					logger.info("服务器回复给设备ID为【" + result + "】的用户【" + username+ "】 的 IP 为： " + byte2Int(ip[0]) + "."+ byte2Int(ip[1]) + "." + byte2Int(ip[2]) + "."	+ byte2Int(ip[3]));
				} else {// 当前map中不存在这个id
					logger.info("当前map中不存在这个id");
					cirm.setLoginResult(AbstrMessage.GATEWAY_OUTLINE_FAIL);// 网关不在线
					cirm.setIp(new byte[] { 0x00, 0x00, 0x00, 0x00 });
				}
			}
			session.write(cirm);
			break;
		default:
			logger.info("未知请求！");
			break;
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		//服务器主动关闭与客户端连接
		session.close(true);// 客户端在向远程服务器发送请求后，服务器在给客户端反馈后，由服务器断开连接
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
//		logger.info("服务端与客户端连接关闭...");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		   logger.info("线程进入idle状态，则立即关闭此session");
	       session.close(true);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("服务端抛出异常...", cause);
	}

	/**
	 * 将一个字节转换成整数时候 前三个字节补0
	 * @param data
	 * @return
	 */
	public int byte2Int(byte data) {
		byte[] datas = new byte[4];
		datas[3] = data;
		int value = 0;
		for (int i = 0; i < datas.length; i++) {
			int shift = (datas.length - 1 - i) * 8;
			value += (datas[i] & 0xFF) << shift;
		}
		return value;
	}
}
