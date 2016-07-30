package me.cjd.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import me.cjd.intr.AuthEmplIntr;
import me.cjd.intr.AuthIntr;
import me.cjd.intr.AuthIntrDontGo;
import me.cjd.pojo.Balance;
import me.cjd.pojo.Employee;
import me.cjd.pojo.Leave;
import me.cjd.pojo.Manager;
import me.cjd.pojo.ProcessesBinding;
import me.cjd.pojo.ProcessesResult;
import me.cjd.service.BalanceService;
import me.cjd.service.EmplService;
import me.cjd.service.EmplWechatService;
import me.cjd.service.LeaveService;
import me.cjd.service.ManagerService;
import me.cjd.service.ProcessesService;
import me.cjd.utils.ExcelReader;
import me.cjd.utils.ExcelWriter;
import me.cjd.utils.LoginedUtil;
import me.cjd.utils.ProcessesUtil;
import me.cjd.utils.StringUtil;
import me.cjd.utils.UUID;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;

public class LeaveController extends Controller {
	
	public void index(){
		// 是否登录
		if (LoginedUtil.logined(this)) {
			this.redirect("/list");
		} else {
			this.render("/login.html");
		}
	}
	
	public void login(){
		String username = this.getPara("username");
		String password = this.getPara("password");
		if (StringUtil.isNotEmpty(username) &&
			StringUtil.isNotEmpty(password)) {
			// 登录 管理员
			if (ManagerService.login(this, username, password)) {
				// 管理员登录
				Manager current = LoginedUtil.current(this);
				EmplService.loginForManager(this, current.getId());
				// 跳转至审批页
				this.redirect("/list");
				return;
			}
			// 登录 员工
			if (EmplService.login(this, username, password)) {
				Employee empl = LoginedUtil.currentEmpl(this);
				ManagerService.loginForEmployee(this, empl.getManagerId());
				this.redirect("/employee/profile");
				return;
			}
		}
		this.redirect("/");
	}
	
	public void logout(){
		// 注销 登录
		LoginedUtil.logout(this);
		LoginedUtil.logoutEmpl(this);
		// 返回 登录页
		this.redirect("/");
	}
	
	@Before(AuthIntr.class)
	public void list(){
		Boolean state = this.getParaToBoolean("s");
		// 获取 工号
		String emplNo = this.getPara("n");
		// 获取 开始时间
		Date start = this.getParaToDate("ds");
		// 获取 截止时间
		Date end = this.getParaToDate("de");
		Manager current = LoginedUtil.current(this);
		Integer currentId = current.getInt("id");
		
		StringBuffer sql = new StringBuffer("SELECT * FROM `leave` WHERE ");
		// 插入 条件
		sql
		.append("`leave`.id in ")
		// 流程 子查询
		.append("(select r.leave_id from processes_result as r where r.manager_id = ? and ");
		
		if (state == null || !state) {
			sql.append("r.state = 1) AND `leave`.state = 0 ");
		} else {
			sql.append("r.state > 1) AND `leave`.state <> 0 ");
		}
		
		List<Object> values = new ArrayList<>(3);
		// 参数 添入当前用户
		values.add(currentId);
		
		if (start != null) {
			values.add(start);
			sql.append("and `leave`.start_date >= ? ");
		}
		
		if (end != null) {
			values.add(end);
			sql.append("and `leave`.end_date <= ? ");
		}
		
		// 模糊查询工号
		if (StringUtil.isNotEmpty(emplNo)) {
			sql.append("and `leave`.employ_no like '%" + emplNo + "%' ");
		}
		
		// 插入 排序规则
		sql.append("ORDER BY `leave`.id ASC");
		
		List<Leave> leaveL = Leave.me.find(sql.toString(), values.toArray());
		
		for (Leave i : leaveL) {
			Integer leaveId = i.getInt("id");
			i.put("result", ProcessesService.getJson(leaveId, currentId));
			i.put("json", i.toJson());
		}
		
		// 保持参数
		this.keepPara("n", "ds", "de");
		// 参数回传
		this.setAttr("state", state);
		this.setAttr("leaveL", leaveL);
		this.render("/views/manager/list.html");
	}
	
	@Before(AuthEmplIntr.class)
	public void applyToken(){
		String token = UUID.random();
		this.redirect("/apply?token=" + token);
	}
	
	@Before(AuthEmplIntr.class)
	public void apply(){
		Employee current = LoginedUtil.currentEmpl(this);
		// 获取 员工id
		int currentEmplId = current.getId();
		// 查询 流程绑定
		ProcessesBinding binding = ProcessesService.getBinding(currentEmplId);
		if (binding != null) {
			this.setAttr("processesId", binding.getProcessesId());
		}
		
		Balance balance = BalanceService.findByEmplId(currentEmplId);
		
		if (balance == null) {
			balance = new Balance();
		}
		
		// 生成 令牌
		this.setAttr("token", this.getPara("token"));
		this.setAttr("crt", current);
		this.setAttr("balance", balance);
		this.setAttr("currentEmpl", LoginedUtil.currentEmpl(this));
		// 查找 管理者列表
		this.setAttr("proL", ProcessesService.list());
		// 返回 申请页面
		this.render("/views/employee/apply.html");
	}
	
	@Before(AuthEmplIntr.class)
	public void applyConfirm(){
		LeaveService.confirm(this);
		// 确认页
		this.render("/views/employee/apply-confirm.html");
	}
	
	@Before(AuthEmplIntr.class)
	public void applyAction(){
		LeaveService.action(this);
	}
	
	@Before(AuthIntrDontGo.class)
	public void result(){
		int id = this.getParaToInt(0);
		// 1 = 作废
		int cancel = this.getParaToInt(1, 0);
		Leave leave = Leave.me.findById(id);
		if (leave == null) {
			this.render("/404.html");
			return;
		} else {
			// 管理员 登录了 然后 又是这个状态，才会有操作
			this.setAttr("cancel", LoginedUtil.logined(this) && cancel == 1);
			this.setAttr("lv", leave);
			this.setAttr("res", ProcessesService.listResult(id));
			this.render("/views/employee/result.html");
		}
	}
	
	@Before(AuthIntr.class)
	public void gun_wechat_page(){
		// 获取 假单id
		int id = this.getParaToInt(0);
		Leave leave = Leave.me.findById(id);
		if (leave == null) {
			this.render("/404.html");
			return;
		} else {
			// 获取 状态
			int state = leave.getState();
			if (state > 0) {
				this.render("/already.html");
				return;
			}
			Manager manager = LoginedUtil.current(this);
			int managerId = manager.getId();
			// 获取 流程结果
			ProcessesResult result = ProcessesService.getResult(id, managerId);
			
			if (result == null) {
				this.render("/authorize.html");
				return;
			}
			
			int rState = result.getState();
			if (rState == 0) {
				this.render("/authorize.html");
				return;
			}
			
			if (rState > 1) {
				this.render("/already.html");
				return;
			}
			
			this.setAttr("lv", leave);
		}
		this.render("/views/manager/processes-wechat.html");
	}
	
	public void gun_wechat(){
		Leave leave = this.getModel(Leave.class, "lv");
		boolean result = ProcessesUtil.push(leave.getId(), leave.getState(), leave.getReason());
		if (result) {
			this.render("/success.html");
		} else {
			this.render("/fail.html");
		}
	}
	
	@Before(AuthIntr.class)
	public void gun(){
		Leave leave = this.getModel(Leave.class, "lv");
		// 获取 审批id
		int id = leave.getId();
		// 获取 审批意见
		int state = leave.getState();
		// 获取 审批回复
		String suggestion = this.getPara("suggestion");
		// 推进流程
		boolean result = ProcessesUtil.push(id, state, suggestion);
		// 成功
		if (result) {
			this.renderText("{'success': true, 'message': '审批成功'}");
		} else {
			this.renderText("{'success': false, 'message': '审批失败'}");
		}
	}
	
	public void gunForWechat(){
		int id = this.getParaToInt("id");
		String result = this.getPara("result");
		String reason = this.getPara("reason");
		
		Leave leave = Leave.me.findById(id);
		if (leave == null) {
			return;
		}
		Integer iState = leave.getState();
		// 防止乱来
		if (iState == null || iState != 0) {
			return;
		}
		
		int state = -1;
		switch (result) {
			case "同意":
				state = 1;
				break;
			case "否绝":
				state = 2;
				break;
			default:
				return;
		}
		
		// 推进流程
		ProcessesUtil.push(id, state, reason);
		// 返回空
		this.renderNull();
	}
	
	public final static String UPLOAD_KEY = "upload_list";
	
	@Before(AuthIntr.class)
	public void upload(){
		this.removeSessionAttr(UPLOAD_KEY);
		this.render("/views/manager/empl-upload.html");
	}
	
	@Before(AuthIntr.class)
	public void upload_read(){
		// 获取 上传文件类
		UploadFile uFile = this.getFile("excel");
		// 读取 excel转换成列表
		List<Employee> emplL = ExcelReader.read(uFile.getFile());
		
		// 保存进session
		this.removeSessionAttr(UPLOAD_KEY);
		this.setSessionAttr(UPLOAD_KEY, emplL);
		
		// 有东西
		this.setAttr("isome", emplL != null && emplL.size() > 0);
		this.setAttr("emplL", emplL);
		this.render("/views/manager/empl-upload.html");
	}
	
	@Before(AuthIntr.class)
	public void upload_act(){
		// 获取 上传列表
		List<Employee> uploadL = this.getSessionAttr(UPLOAD_KEY);
		
		// 删除 session
		this.removeSessionAttr(UPLOAD_KEY);
		
		if (uploadL == null || uploadL.isEmpty()) {
			this.renderText("{'success': false, 'message': '上传失败，没有找到要上传的内容'}");
			return;
		}
		
		for (Employee e : uploadL) {
			// 根据工号判断存在于否
			String no = e.getStr("employ_no");
			Employee exs = Employee.me.findFirst("select e.id from employee as e where e.employ_no = ? ", no);
			// 如果不存在 
			if (exs == null) {
				e.save();
				// 绑定 流程
				ProcessesService.bindingProcesses(e);
			}
		}
		
		this.renderText("{'success': true, 'message': '上传成功'}");
	}
	
	@Before(AuthEmplIntr.class)
	public void leave_list(){
		Employee empl = LoginedUtil.currentEmpl(this);
		List<Leave> leaveL = Leave.me.find("SELECT * FROM `leave` WHERE `leave`.apply_man_id = ? ORDER BY `leave`.id DESC", empl.getInt("id"));
		this.setAttr("leaveL", leaveL);
		this.render("/views/employee/leave-list.html");
	}
	
	@Before(AuthIntr.class)
	public void leaveManage(){
		// 获取 开始时间
		Date start = this.getParaToDate("ds");
		// 获取 截止时间
		Date end = this.getParaToDate("de");
		// 声明 查询语句
		StringBuffer sql = new StringBuffer("SELECT * FROM `leave` WHERE `leave`.state = 1 ");
		// 声明 列表
		List<Date> values = new ArrayList<>(2);
		
		if (start != null) {
			values.add(start);
			sql.append("and `leave`.start_date >= ? ");
		}
		
		if (end != null) {
			values.add(end);
			sql.append("and `leave`.end_date <= ? ");
		}
		
		sql.append("ORDER BY `leave`.id DESC ");
		
		List<Leave> leaveL = Leave.me.find(sql.toString(), values.toArray());
		
		this.keepPara("ds", "de");
		this.setAttr("leaveL", leaveL);
		this.render("/views/manager/leave-finish-list.html");
	}
	
	public void leaveOut(){
		ExcelWriter.me.controller(this);
	}
	
	/**
	 * 假单作废方法
	 */
	@Before(AuthIntr.class)
	public void cancel(){
		LeaveService.cancel(this);
	}
	
	/**
	 * 员工取消假单方法
	 */
	@Before(AuthEmplIntr.class)
	public void cancelEmpl(){
		LeaveService.cancelEmpl(this);
	}
	
	@Before(AuthIntr.class)
	public void duang(){
		this.render("/duang.html");
	}
	
	public void duangAct(){
		Manager manager = this.getModel(Manager.class, "m");
		this.renderJson("success", manager.save());
	}
	
	@Before(AuthIntr.class)
	public void updateManagerPassword(){
		this.setAttr("current", LoginedUtil.current(this));
		this.render("/views/manager/manager-password.html");
	}
	
	public void updateManagerPasswordAct(){
		Manager current = LoginedUtil.current(this);
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
	
	@Before(AuthIntr.class)
	public void mail(){
		Manager current = LoginedUtil.current(this);
		String email = current.getEmail();
		this.setAttr("email", email);
		this.render("/views/manager/mail-manage.html");
	}
	
	@Before(AuthEmplIntr.class)
	public void mailEmpl(){
		// 获取 当前登录员工
		Employee empl = LoginedUtil.currentEmpl(this);
		String email = empl.getEmail();
		this.setAttr("email", email);
		// 保存 类型为员工
		this.setAttr("type", "empl");
		// 返回 邮件管理
		this.render("/views/manager/mail-manage.html");
	}
	
	public void mailAct(){
		String type = this.getPara("type");
		String email = this.getPara("mail");
		boolean success = false;
		if (StringUtil.isEmpty(type)) {
			Manager current = LoginedUtil.current(this);
			// 更新 对应员工邮箱
			success = Db.update("UPDATE employee AS i SET i.email = ? WHERE i.manager_id = ?", email, current.getId()) > 0;
			success = current.setEmail(email).update();
			LoginedUtil.upgrade(this, current);
		} else {
			Employee empl = LoginedUtil.currentEmpl(this);
			success = empl.setEmail(email).update();
			LoginedUtil.upgradeEmpl(this, empl);
		}
		this.renderText(String.valueOf(success));
	}
	
	/**
	 * Mail Into
	 */
	public void mi(){
		// 获取 邮件地址
		String mail = this.getPara("e");
		// 获取 假单ID
		Integer leaveId = this.getParaToInt("l");
		Integer managerId = this.getParaToInt("m");
		// 查询
		Manager emailInMa = Manager.me.findById(managerId);
		
		if (emailInMa == null) {
			throw new RuntimeException("管理员不存在");
		}
		
		if (!emailInMa.getEmail().equals(mail)) {
			throw new RuntimeException("您无授权，无法查询假单");
		}
		
		// 查询 假单
		Leave emailInL = Leave.me.findById(leaveId);
		
		if (emailInL == null) {
			throw new RuntimeException("假单不存在");
		}
		
		// 登录
		LoginedUtil.upgrade(this, emailInMa);
		// 管理员登录
		EmplService.loginForManager(this, managerId);
		
		this.setAttr("current_manager", LoginedUtil.current(this));
		this.setAttr("current_connect_empl", LoginedUtil.current(this) != null && LoginedUtil.currentEmpl(this) != null);
		this.setAttr("mailView", leaveId);
		// 执行原方法
		this.list();
	}
	
	public void ers(){
		// 获取 假单ID
		int leaveId = this.getParaToInt("l");
		Leave leave = Leave.me.findById(leaveId);
		if (leave == null) {
			this.render("/404.html");
			return;
		} else {
			// 获取 员工ID
			int emplId = leave.getEmployId();
			// 查找 员工记录
			Employee employee = Employee.me.findById(emplId);
			// 登录
			LoginedUtil.upgradeEmpl(this, employee);
			// 一些琐碎
			this.setAttr("current_connect_empl", 
				LoginedUtil.current(this) != null && 
				LoginedUtil.currentEmpl(this) != null);
			this.setAttr("cancel", false);
			this.setAttr("lv", leave);
			this.setAttr("res", ProcessesService.listResult(leaveId));
			this.render("/views/employee/result.html");
		}
	}
	
	@Before(AuthIntr.class)
	public void logs(){
		LeaveService.ologs(this);
		this.render("/views/manager/ologs.html");
	}
	
	public void wechat_login(){
		EmplWechatService.me.login(this);
	}
	
	public void wechat_binding(){
		// 获取 参数
		int emplId = this.getParaToInt("state", 0);
		String code = this.getPara("code");
		// 绑定
		String resultMsg = EmplWechatService.me.connectBinding(emplId, code);
		this.setAttr("resultMsg", resultMsg);
		this.render("/views/employee/empl-connect-wehcat-result.html");
	}
	
	public void success(){
		this.render("/success.html");
	}
	
	public void fail(){
		this.render("/fail.html");
	}
	
}