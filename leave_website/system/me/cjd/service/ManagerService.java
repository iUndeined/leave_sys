package me.cjd.service;

import java.util.List;

import com.jfinal.core.Controller;

import me.cjd.pojo.Employee;
import me.cjd.pojo.Manager;
import me.cjd.utils.LoginedUtil;

public class ManagerService {
	
	public final static List<Manager> list(){
		return Manager.me.find("select i.id, i.name from manager as i ");
	}
	
	/**
	 * 通过ID查找管理员名称
	 * @param id
	 * @return
	 */
	public final static String name(String id){
		return Manager.me.findFirst("select i.name from manager as i where i.id = ? ", id).getStr("name");
	}
	
	public final static boolean login(Controller c, String username, String password){
		
		Manager currentLogined = Manager.me.findFirst("select * from manager as i where i.account = ? and i.password = ? ", username, password);
		
		return register(c, currentLogined);
	}
	
	public final static boolean loginForEmployee(Controller c, Integer managerId){
		
		if (managerId == null) {
			return false;
		}
		
		Manager currentLogined = Manager.me.findById(managerId);
		
		return register(c, currentLogined);
	}
	
	public final static boolean register(Controller c, Manager currentLogined){
		if (currentLogined == null) {
			return false;
		}
		
		// 记录 登录
		LoginedUtil.upgrade(c, currentLogined);
		
		return true;
	}
	
}
