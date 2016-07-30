package me.cjd.utils;

import java.util.List;
import org.apache.http.cookie.Cookie;
import com.alibaba.fastjson.JSON;
import com.jfinal.log.Logger;
import me.cjd.http.call.EmptyCallback;
import me.cjd.http.core.HttpUtil;
import me.cjd.http.inter.Callback;
import me.cjd.message.NewsMsg;
import me.cjd.message.TextMsg;
import me.cjd.model.AccessToken;
import me.cjd.model.QyApiConfig;

public class Wechat {
	
	private final static Logger LOG = Logger.getLogger(Wechat.class);
	
	public static String valPublic = null;
	
	public final static String getAccessToken(){
		return getAccessToken(null);
	}
	
	public final static String getAccessToken(final Callback callback){
		QyApiConfig config = Config.getQyApi();
		String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + config.getCorpId() + "&corpsecret=" + config.getSecret();
		return HttpUtil.get(url, callback == null ? null : new Callback() {
			@Override
			public void onSuccess(String content, List<Cookie> cookies) {
				callback.onSuccess(content, cookies);
			}
		});
	}
	
	public final static void sendTextMsg(final TextMsg msg){
		sendMsg(JSON.toJSONString(msg));
	}
	
	public final static void sendNewsMsg(final NewsMsg msg){
		sendMsg(JSON.toJSONString(msg));
	}
	
	public final static void sendMsg(final String json){
		getAccessToken(new Callback() {
			@Override
			public void onSuccess(String content, List<Cookie> cookies) {
				// 解析 token
				AccessToken token = JSON.parseObject(content, AccessToken.class);
				
				if (token.getErrcode() != -1) {
					LOG.error("sendTextMsg方法获取access_token失败！！");
					return;
				}
				String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token.getAccess_token();
				HttpUtil.json(url, json, new EmptyCallback());
			}
		});
	}
	
	public final static String getUserId(final String code){
		valPublic = null;
		getAccessToken(new Callback() {
			@Override
			public void onSuccess(String content, List<Cookie> cookies) {
				// 解析 token
				AccessToken token = JSON.parseObject(content, AccessToken.class);
				
				if (token.getErrcode() != -1) {
					LOG.error("getUserId方法获取access_token失败！！");
					return;
				}
				
				String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=" + token.getAccess_token() + "&code=" + code;
				HttpUtil.get(url, new Callback() {
					@Override
					public void onSuccess(String content, List<Cookie> cookies) {
						valPublic = content;
					}
				});
			}
		});
		return valPublic;
	}
	
}