package com.yj.smartHome.codec;

import java.nio.charset.Charset;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import com.yj.smartHome.message.AbstrMessage;
import com.yj.smartHome.message.GatewayStatusMsg;
import com.yj.smartHome.message.ClientIpRequestMsg;

/**
 * 解码器
 * @author wuxuehong
 *
 * @date 2012-9-10
 */
public class MyMessageDecoder implements MessageDecoder {

	private Logger logger = Logger.getLogger(MyMessageDecoder.class);

	public MyMessageDecoder() {

	}

	public MyMessageDecoder(Charset charset) {
	}

	// 检查给定的IoBuffer是否适合解码
	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		// 报头长度==4 : 数据头（2）+请求类型（1）+数据区长度（1）
		if (in.remaining() < 4) {
			return MessageDecoderResult.NEED_DATA;
		}
		// 获取包协议数据头
		byte head1 = in.get();
		byte head2 = in.get();
		if (head1 != AbstrMessage.HEAD[0] || head2 != AbstrMessage.HEAD[1]) {
			logger.error("未知的包数据头....");
			return MessageDecoderResult.NOT_OK;
		}
		// 检查请求类型tag正常
		byte requestType = in.get();
		if (requestType == AbstrMessage.CLIENT_IP_REQUEST
				|| requestType == AbstrMessage.GATEWAY_STATUS_REQUEST
				|| requestType == AbstrMessage.GATEWAY_WARN_REQUEST) {
			// logger.info("请求标识符：" + Integer.toHexString(requestType));
		} else {
			logger.error("未知的解码类型....");
			return MessageDecoderResult.NOT_OK;
		}
		int len = in.get();// 数据区长度
		if (in.remaining() < len + 1) { // len+1表示数据区长度+帧结束符号（1）
			return MessageDecoderResult.NEED_DATA;
		}
		return MessageDecoderResult.OK;
	}

	// 解码过程
	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		// logger.info("解码：" + in.toString());
		AbstrMessage message = null;
		in.getShort();// 略去两个字节的数据头（YJ）
		byte requestType = in.get(); // 得到请求类型
		byte len = in.get(); // 得到数据区长度
		byte[] temp = new byte[len];
		in.get(temp); // 把数据区内容放到temp中
		in.get(); // 取出结尾表示符
		switch (requestType) {
		// 网关发送身份信息
		case AbstrMessage.GATEWAY_STATUS_REQUEST:
			GatewayStatusMsg msg = new GatewayStatusMsg();
			byte idlength = temp[0];// 得到网关ID的长度
			String id = null;
			if (idlength > 0) {
				id = bytesToHexString(temp, 1, temp.length);
			}
			msg.setId(id);
			message = msg;
			break;

		// 客户端请求IP
		case AbstrMessage.CLIENT_IP_REQUEST:
			ClientIpRequestMsg msg3 = new ClientIpRequestMsg();
			byte userLen = temp[0];// 得到用户名长度
			String username = null;
			if (userLen > 0) {
				username = new String(temp, 1, userLen);// 得到用户名
			}
			msg3.setUsername(username);
			byte passLen = temp[userLen + 1];// 得到密码长度
			String password = null;
			if (passLen > 0) {
				password = new String(temp, userLen + 2, passLen);// 得到密码
			}
			msg3.setPassword(password);
			message = msg3;
			break;
		default:
			logger.error("未找到解码器....");
			break;
		}
		out.write(message);
		// ================解码成功=========================
		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
			throws Exception {
	}

	/**
	 * 将字节数组转换成十六进制字符串
	 * @param buf
	 * @param offset
	 * @param length
	 * @return
	 */
	private String bytesToHexString(byte[] buf, int offset, int length) {
		StringBuffer sb = new StringBuffer("");
		if (buf == null || buf.length <= 0)
			return "";
		for (int i = offset; i < buf.length; i++) {
			int v = buf[i] & 0xFF;// &用来转16进制
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append("0");
			}
			sb.append(hv);
		}
		return sb.toString();
	}

}
