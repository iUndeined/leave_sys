package me.cjd.itor;

import me.cjd.model.QyApiConfig;
import me.cjd.utils.Config;
import me.cjd.utils.QyInMsgParser;
import me.cjd.view.QyMsgController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.encrypt.AesException;
import com.jfinal.weixin.sdk.encrypt.WXBizMsgCrypt;

public class QyMsgInterceptor implements Interceptor {
	
	private WXBizMsgCrypt wxcpt;
	
	private QyApiConfig config;
	
	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		if (c instanceof QyMsgController == false) {
			throw new RuntimeException("控制器需要继承 QyMsgController");
		}
		
		// 获取 配置
		this.config = Config.getQyApi();
		// 实例加密类
		try {
			this.wxcpt = new WXBizMsgCrypt(config.getToken(), config.getEncodingAesKey(), config.getCorpId());
			// 如果是服务器配置请求，则配置服务器并返回
			if (this.isConfigServerRequest(c)) {
				configServer(c);
				return ;
			}
			
			// 读取用户消息进行解密
			if (this.readEncyMsg(c)) {
				inv.invoke();
			}
			
		} catch (AesException e) {
			e.printStackTrace();
			c.renderText("实例加密模块错误");
			return;
		}
	}
	
	/**
	 * 是否为开发者中心保存服务器配置的请求
	 */
	private boolean isConfigServerRequest(Controller c) {
		return StrKit.notBlank(c.getPara("echostr"));
	}
	
	private boolean readEncyMsg(Controller c){
		String encyMsg = HttpKit.readIncommingRequestData(c.getRequest());
		if (StrKit.notBlank(encyMsg)) {
			// 获取 信息
			String signature = c.getPara("msg_signature");
			String timestamp = c.getPara("timestamp");
			String nonce = c.getPara("nonce");
			
			if (StrKit.isBlank(signature) ||
				StrKit.isBlank(timestamp) ||
				StrKit.isBlank(nonce)) {
				c.renderText("加密消息参数不正确！！");
				return false;
			}
			
			try {
				String sMsg = wxcpt.decryptMsg(signature, timestamp, nonce, encyMsg);
				c.setAttr("sXml", sMsg);
				c.setAttr("sMsg", QyInMsgParser.parse(sMsg));
			} catch (AesException e) {
				e.printStackTrace();
				c.renderText("解密消息失败！！");
				return false;
			}
		}
		return true;
	}
	
	public void configServer(Controller c) {
		// 获取 信息
		String signature = c.getPara("msg_signature");
		String timestamp = c.getPara("timestamp");
		String nonce = c.getPara("nonce");
		String echostr = c.getPara("echostr");
		
		if (StrKit.isBlank(signature) ||
			StrKit.isBlank(timestamp) ||
			StrKit.isBlank(nonce) ||
			StrKit.isBlank(echostr)) {
			c.renderText("回调模式接入数据不正确！！");
			return;
		}
		
		//需要返回的明文
		String sEchoStr; 
		
		try {
			sEchoStr = wxcpt.verifyUrl(signature, timestamp, nonce, echostr);
			// 验证通过，直接返回明文
			c.renderText(sEchoStr);
		} catch (AesException e) {
			//验证URL失败，错误原因请查看异常
			e.printStackTrace();
			c.renderText("微信验证失败");
		}
	}
	
}
