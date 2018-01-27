package com.yj.smartHome.message;

import java.util.List;

/**
 * 网关请求发送报警信息
 * @author wuxuehong
 *
 * 2012-5-3
 */
public class GatewayWarnMsg extends AbstrMessage {

	private List<String> phones;  //发送对象  手机号码
	private String warnInfo;  //报警信息
	
	public List<String> getPhones() {
		return phones;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	public String getWarnInfo() {
		return warnInfo;
	}

	public void setWarnInfo(String warnInfo) {
		this.warnInfo = warnInfo;
	}

	@Override
	public byte getRequestType() {
		// TODO Auto-generated method stub
		return GATEWAY_WARN_REQUEST;
	}

}
