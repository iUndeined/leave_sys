package me.cjd.intr;

import me.cjd.utils.LoginedUtil;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * 登录检查拦截器
 * @author Mr.cjd
 */
public class AuthIntr implements Interceptor {
	
	@Override
	public void intercept(Invocation me) {
		Controller c = me.getController();
		// 登录则通过
		if (LoginedUtil.logined(c)) {
			c.setAttr("current_manager", LoginedUtil.current(c));
			c.setAttr("current_connect_empl", LoginedUtil.currentEmpl(c) != null);
			me.invoke();
		} else {
			c.redirect("/");
		}
	}

}
