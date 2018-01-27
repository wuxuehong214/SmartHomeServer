package com.yj.smartHome.message;

/**
 * 网关身份信息   包含网关唯一ID
 * @author wuxuehong
 *
 * 2012-5-3
 */
public class GatewayStatusMsg extends AbstrMessage {
	
    private String id;//网关id
    
	public String getId() {
		return id; 
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte getRequestType() {
		return GATEWAY_STATUS_REQUEST;
	}

}
