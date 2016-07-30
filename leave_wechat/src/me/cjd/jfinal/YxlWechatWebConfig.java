package me.cjd.jfinal;

import org.beetl.ext.jfinal.BeetlRenderFactory;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;

import me.cjd.api.QyMenuApi;
import me.cjd.view.DuangController;
import me.cjd.view.WebController;

public class YxlWechatWebConfig extends JFinalConfig {
	
	@Override
	public void configConstant(Constants me) {
		// 调用 配置文件
		loadPropertyFile("config.txt");
		// 获取 配置
		me.setDevMode(getPropertyToBoolean("devMode"));
		// 集成 Beetl
		me.setMainRenderFactory(new BeetlRenderFactory());
		// 项目 启动之初 创建微信菜单
		boolean createMenu = getPropertyToBoolean("createMenu", false);
		if (createMenu) {
			if (QyMenuApi.me.createLeaveAppMenu()) {
				System.out.println("信息：自定义菜单创建成功！！！");
			} else {
				System.out.println("信息：自定义菜单创建失败！！！");
			}
		}
	}

	@Override
	public void configHandler(Handlers me) {
		
	}

	@Override
	public void configInterceptor(Interceptors me) {
		
	}

	@Override
	public void configPlugin(Plugins me) {
		
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/", WebController.class);
		me.add("/duang", DuangController.class);
	}

}
