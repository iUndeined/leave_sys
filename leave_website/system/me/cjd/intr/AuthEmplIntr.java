package me.cjd.intr;

import me.cjd.utils.LoginedUtil;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class AuthEmplIntr implements Interceptor  {

	@Override
	public void intercept(Invocation me) {
		Controller c = me.getController();
		// 登录则通过
		if (LoginedUtil.loginedEmpl(c)) {
			c.setAttr("current_connect_empl", LoginedUtil.current(c) != null && LoginedUtil.currentEmpl(c) != null);
			me.invoke();
		} else {
			c.redirect("/");
		}
	}

}
