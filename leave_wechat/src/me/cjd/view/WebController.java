package me.cjd.view;

import me.cjd.message.NewsMsg;
import me.cjd.message.TextMsg;
import me.cjd.model.QyApiConfig;
import me.cjd.utils.Config;
import me.cjd.utils.Wechat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;

public class WebController extends Controller {
	
	private final static Logger LOG = Logger.getLogger(WebController.class);
	
	public void index(){
		this.render("/index.html");
	}
	
	public void getUserId(){
		String state = this.getPara("state");
		String userId = Wechat.getUserId(this.getPara("code"));
		String method = this.getPara("method", "wechat_binding");
		LOG.info("开始微信授权绑定，userId = " + userId + "; state = " + state);
		// 转至的链接
		this.setAttr("method", method);
		this.setAttr("state", state);
		this.setAttr("code", userId);
		this.render("/to-leave-binding.html");
	}
	
	public void oauth2(){
		String state = this.getPara("state");
		String userId = Wechat.getUserId(this.getPara("code"));
		LOG.info("开始微信授权登录，userId = " + userId + "; state = " + state);
		this.setAttr("method", "wechat_login");
		this.setAttr("state", state);
		this.setAttr("code", userId);
		this.render("/to-leave-binding.html");
	}
	
	public void sendText(){
		String userId = this.getPara("userId");
		String content = this.getPara("content");
		if (StrKit.notBlank(content)) {
			Wechat.sendTextMsg(new TextMsg(userId, content));
		}
		this.renderNull();
	}
	
	/**
	 * 发送审批通知
	 */
	public void sendProcessNoti() throws UnsupportedEncodingException{
		String userId = this.getPara("userId");
		String title = this.getPara("title");
		String describe = this.getPara("describe");
		String leaveId = this.getPara("leaveId");
		QyApiConfig config = Config.getQyApi();
		String domain = config.getDomain();
		String corpId = config.getCorpId();
		String uri = URLEncoder.encode(domain + "leave_wechat/oauth2", "UTF-8");
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + corpId + "&redirect_uri=" + uri + "&response_type=code&scope=snsapi_base&state=2_" + leaveId + "#wechat_redirect";
		Wechat.sendNewsMsg(new NewsMsg(userId, title, describe, url));
		this.renderNull();
	}
	
}
