package me.cjd.service;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import me.cjd.pojo.Employee;
import me.cjd.statics.EemplGone;
import me.cjd.utils.LoginedUtil;
import me.cjd.utils.StringUtil;

public class EmplService {
	
	public final static Employee getByNoAndPswd(String username, String password){
		return Employee.me.findFirst("select * from employee as i where i.employ_no = ? and i.password = ? and i.gone = 0 ", username, password);
	}
	
	public final static Employee getByEmplNo(String emplNo){
		return Employee.me.findFirst("select * from employee i where i.employ_no = ? ", emplNo);
	}
	
	public final static boolean loginForManager(Controller c, int managerId){
		
		Employee currentLogined = getByManagerId(managerId);
		
		return register(c, currentLogined);
	}
	
	public final static boolean login(Controller c, String username, String password){
		
		Employee currentLogined = getByNoAndPswd(username, password);
		
		return register(c, currentLogined);
	}
	
	public final static boolean register(Controller c, Employee currentLogined){
		if (currentLogined == null) {
			return false;
		}
		
		// 记录 登录
		LoginedUtil.upgradeEmpl(c, currentLogined);
		
		return true;
	}
	
	public final static Employee getByManagerId(int managerId){
		return Employee.me.findFirst("select * from employee as i where i.manager_id = ? ", managerId);
	}
	
	public final static Page<Employee> page(Controller c){
		String sql = "from employee i where i.gone = 0 ";
		String name = c.getPara("name");
		
		if (StringUtil.isNotEmpty(name)) {
			sql += "and (i.employ_no like '%" + name + "%' or i.name like '%" + name + "%') ";
		}
		
		c.keepPara("name");
		
		return Employee.me.paginate(c.getParaToInt(0, 1), 10, "select *", sql);
	}
	
	public final static void goneAct(Controller c){
		// 获取 员工id
		Integer emplId = c.getParaToInt("id");
		
		if (emplId == null) {
			c.render("/fail.html");
			return;
		}
		
		// 查找 员工
		Employee empl = Employee.me.findById(emplId);
		
		if (empl == null) {
			c.render("/fail.html");
			return;
		}
		
		// 更新 存在字段
		empl.setManagerId(null).setGone(EemplGone.gone.getStatus()).update();
		
		c.render("/success.html");
	}
	
}
