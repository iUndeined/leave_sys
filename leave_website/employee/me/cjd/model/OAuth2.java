package me.cjd.model;

/**
 * 微信用户信息裸体
 * @author Mr.cjd
 */
public class OAuth2 {
	
	private Integer errcode;
	private String errmsg;
	private String UserId;
	private String DeviceId;
	
	public Integer getErrcode() {
		return errcode;
	}

	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getDeviceId() {
		return DeviceId;
	}

	public void setDeviceId(String deviceId) {
		DeviceId = deviceId;
	}

}
