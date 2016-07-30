package me.cjd.web;

import me.cjd.intr.AuthEmplIntr;
import me.cjd.intr.AuthIntr;
import me.cjd.pojo.Balance;
import me.cjd.pojo.Employee;
import me.cjd.pojo.EmployeeWechat;
import me.cjd.pojo.Manager;
import me.cjd.pojo.ProcessesBinding;
import me.cjd.service.BalanceService;
import me.cjd.service.EmplService;
import me.cjd.service.EmplWechatService;
import me.cjd.service.ProcessesService;
import me.cjd.utils.LoginedUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

public class EmployeeController extends Controller {
	
	@Before(AuthIntr.class)
	public void index(){
		Page<Employee> emplPage = EmplService.page(this);
		this.setAttr("emplL", emplPage.getList());
		this.setAttr("emplPage", emplPage);
		this.render("/views/manager/empl-list.html");
	}
	
	@Before(AuthIntr.class)
	public void save(){
		Employee empl = this.getModel(Employee.class, "el");
		Integer id = empl.getId();
		if (id == null) {
			empl.save();
			id = empl.getId();
		} else {
			empl.update();
		}
		
		// 变更 流程绑定
		Integer processesId = this.getParaToInt("processes_id");
		ProcessesService.bindingUpdate(id, processesId);
		
		this.redirect("/employee");
	}
	
	@Before(AuthIntr.class)
	public void add(){
		// 查询 流程
		ProcessesService.initManagerPage(this);
		this.setAttr("empl", new Employee());
		this.render("/views/manager/empl-edit.html");
	}
	
	@Before(AuthIntr.class)
	public void edit(){
		// 获取 id
		int emplId = this.getParaToInt();
		// 查询 员工
		Employee empl = Employee.me.findById(emplId);
		// 返回 列表页
		if (empl == null) {
			this.redirect("/employee");
			return;
		}
		
		// 查询 流程
		ProcessesService.initManagerPage(this);
		// 查询 流程绑定
		ProcessesBinding binding = ProcessesService.getBinding(emplId);
		
		if (binding != null) {
			this.setAttr("currentProcesses", binding.getProcessesId());
		}
		
		// 传值并返回页面
		this.setAttr("empl", empl);
		this.render("/views/manager/empl-edit.html");
	}
	
	/**
	 * 删除员工方法
	 */
	public void goneAct(){
		EmplService.goneAct(this);
	}
	
	@Before(AuthEmplIntr.class)
	public void profile(){
		Employee empl = LoginedUtil.currentEmpl(this);
		int emplId = empl.getId();
		// 查询 假期信息
		Balance be = BalanceService.findByEmplId(emplId);
		if (be == null) {
			be = new Balance();
		}
		// 查询 微信绑定信息
		EmployeeWechat wechat = EmplWechatService.me.get(emplId);
		this.setAttr("el", empl);
		this.setAttr("be", be);
		this.setAttr("wechat", wechat);
		this.render("/views/employee/profile.html");
	}
	
	@Before(AuthIntr.class)
	public void connect(){
		this.render("/views/manager/empl-connect.html");
	}
	
	@Before(AuthIntr.class)
	public void connectAct(){
		Manager current = LoginedUtil.current(this);
		int currentId = current.getId();
		Employee model = this.getModel(Employee.class, "ep");
		Employee connect = EmplService.getByNoAndPswd(model.getEmployNo(), model.getPassword());
		
		if (connect == null) {
			this.renderJson("success", false);
		} else {
			connect.setManagerId(currentId).update();
			this.renderJson("success", true);
		}
	}
	
	@Before(AuthEmplIntr.class)
	public void updateMyPassword(){
		this.setAttr("current", LoginedUtil.currentEmpl(this));
		this.render("/views/employee/empl-password.html");
	}
	
	public void updateMyPasswordAct(){
		Employee current = LoginedUtil.currentEmpl(this);
		String queryPswd = current.getPassword();
		String oldPswd = this.getPara("pswd1");
		String newPswd = this.getPara("pswd2");
		if (queryPswd.equals(oldPswd)) {
			current.setPassword(newPswd).update();
			this.renderJson("success", true);
			return;
		}
		this.renderJson("success", false);
	}
}
