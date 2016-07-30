package me.cjd.utils;

import com.jfinal.kit.PropKit;

import me.cjd.model.QyApiConfig;

public class Config {
	
	public final static QyApiConfig getQyApi(){
		QyApiConfig ac = new QyApiConfig();
		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setCorpId(PropKit.get("corpId"));
		ac.setAppId(PropKit.getInt("appId"));
		ac.setSecret(PropKit.get("secret"));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey"));
		ac.setDomain(PropKit.get("domain"));
		return ac;
	}
	
}
