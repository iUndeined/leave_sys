package me.cjd.intr;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import me.cjd.utils.LoginedUtil;

public class AuthIntrDontGo implements Interceptor {

	@Override
	public void intercept(Invocation me) {
		Controller c = me.getController();
		// 登录则通过
		if (LoginedUtil.logined(c)) {
			c.setAttr("current_manager", LoginedUtil.current(c));
		}
		c.setAttr("current_connect_empl", LoginedUtil.current(c) != null && LoginedUtil.currentEmpl(c) != null);
		me.invoke();
	}

}
