package me.cjd.utils;

import com.jfinal.core.Controller;

import me.cjd.pojo.Employee;
import me.cjd.pojo.Manager;

/**
 * 登录工具
 * @author Mr.cjd
 */
public class LoginedUtil {
	
	public final static String LOGINED_KEY = "YXL_LEAVE_CURRENT_LOGIN";
	
	public final static boolean logined(Controller c){
		return current(c) != null;
	}
	
	public final static void logout(Controller c){
		c.removeSessionAttr(LOGINED_KEY);
	}
	
	public final static Manager current(Controller c){
		return c.getSessionAttr(LOGINED_KEY);
	}
	
	public final static void upgrade(Controller c, Manager manager){
		c.setSessionAttr(LOGINED_KEY, manager);
	}
	
	public final static String LOADINE_EMPL_KEY = "YXL_LEAVE_CURRENT_EMPL_LOGIN";
	
	public final static boolean loginedEmpl(Controller c){
		return currentEmpl(c) != null;
	}
	
	public final static void logoutEmpl(Controller c){
		c.removeSessionAttr(LOADINE_EMPL_KEY);
	}
	
	public final static Employee currentEmpl(Controller c){
		return c.getSessionAttr(LOADINE_EMPL_KEY);
	}
	
	public final static void upgradeEmpl(Controller c, Employee empl){
		c.setSessionAttr(LOADINE_EMPL_KEY, empl);
	}
	
}
