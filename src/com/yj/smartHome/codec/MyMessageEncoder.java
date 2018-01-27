package com.yj.smartHome.codec;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.yj.smartHome.message.AbstrMessage;
import com.yj.smartHome.message.ClientIpResponseMsg;

/**
 * 协议编码器
 * @author wuxuehong
 *
 * @date 2012-9-10
 */
public class MyMessageEncoder implements MessageEncoder<AbstrMessage> {
	private Logger logger = Logger.getLogger(MyMessageEncoder.class);

	public MyMessageEncoder(Charset charset) {
	}

	
	@Override
	public void encode(IoSession session, AbstrMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);
		AbstrMessage msg = (AbstrMessage)message;
		byte key = msg.getRequestType();
		switch (key) {
		case AbstrMessage.CLIENT_IP_REQUEST:
			ClientIpResponseMsg ipResponse=(ClientIpResponseMsg)message;
			buf.put(AbstrMessage.HEAD);//把YJ放进缓冲区
			buf.put(AbstrMessage.CLIENT_IP_REQUEST);//0xf4:请求IP的命令
			buf.put((byte) 0x05);//把数据区长度（4）放进缓冲区
			buf.put(ipResponse.getLoginResult());//把请求结果信息（登陆成功或失败） 放进缓冲区
			buf.put(ipResponse.getIp());//把请求的IP放进去
			buf.put((byte) 0x01);//把帧结束符号放进缓冲区
			buf.flip();
//			logger.info("编码" + buf.toString());
			out.write(buf);
			break;
		default:
			break;
		}
	}

}
