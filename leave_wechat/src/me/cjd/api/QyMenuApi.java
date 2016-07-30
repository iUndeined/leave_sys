package me.cjd.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.cookie.Cookie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Logger;

import me.cjd.model.AccessToken;
import me.cjd.model.QyApiConfig;
import me.cjd.utils.Config;
import me.cjd.utils.Wechat;

public class QyMenuApi {
	
	private final static Logger LOG = Logger.getLogger(QyMenuApi.class);
	
	public final static QyMenuApi me = new QyMenuApi();
	
	public static String oauth2 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={{corpId}}&redirect_uri={{uri}}&response_type=code&scope=snsapi_base&state={{state}}#wechat_redirect";
	
	public static String createUrl = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token={{token}}&agentid={{appId}}";
	
	public static String queryUrl = "https://qyapi.weixin.qq.com/cgi-bin/menu/get?access_token={{token}}&agentid={{appId}}";
	
	static {
		// 获取 文本配置信息
		QyApiConfig config = Config.getQyApi();
		// 获取 应用id
		int appId = config.getAppId();
		// 获取 corpId
		String corpId = config.getCorpId();
		// 获取 域名
		String domain = config.getDomain();
		// 替换 应用id
		createUrl = createUrl.replace("{{appId}}", String.valueOf(appId));
		queryUrl = queryUrl.replace("{{appId}}", String.valueOf(appId));
		try {
		oauth2 = oauth2
				.replace("{{corpId}}", corpId)
				.replace("{{uri}}", URLEncoder.encode(domain + "leave_wechat/", "UTF-8") + "oauth2");
		} catch (UnsupportedEncodingException e) {
			LOG.error("转换授权地址失败", e);
		}
	}
	
	/**
	 * 创建 菜单方法
	 * @param jsonStr 菜单json串
	 * @return 返回json
	 */
	public JSONObject createMenu(String jsonStr){
		AccessToken token = JSON.parseObject(Wechat.getAccessToken(), AccessToken.class);
		String url = createUrl.replace("{{token}}", token.getAccess_token());
		String result = HttpKit.post(url, jsonStr);
		return JSON.parseObject(result);
	}
	
	/**
	 * 创建请假应用菜单
	 * @return
	 */
	public boolean createLeaveAppMenu(){
		String applyUrl = oauth2.replace("{{state}}", "0");
		String queryUrl = oauth2.replace("{{state}}", "1");
		String json = "{\"button\":["
				+ "{\"type\": \"view\", \"name\": \"申请\", \"url\": \"" + applyUrl + "\"}, "
				+ "{\"type\": \"view\", \"name\": \"查询\", \"url\": \"" + queryUrl + "\"}, "
			+ "]}";
		JSONObject result = this.createMenu(json);
		// 获取 错误码
		int errcode = result.getIntValue("errcode");
		if (errcode == 0) {
			return true;
		}
		LOG.info("leave应用创建自定义菜单失败，错误码 = " + errcode);
		return false;
	}
	
}