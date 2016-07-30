package me.cjd.jfinal;

import me.cjd.pojo.Balance;
import me.cjd.pojo.EmailTask;
import me.cjd.pojo.Employee;
import me.cjd.pojo.EmployeeWechat;
import me.cjd.pojo.Leave;
import me.cjd.pojo.Logs;
import me.cjd.pojo.Manager;
import me.cjd.pojo.Processes;
import me.cjd.pojo.ProcessesBinding;
import me.cjd.pojo.ProcessesNode;
import me.cjd.pojo.ProcessesResult;
import me.cjd.service.MailService;
import me.cjd.web.BalanceController;
import me.cjd.web.EmployeeController;
import me.cjd.web.LeaveController;
import me.cjd.web.ProcessesController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import org.beetl.ext.jfinal.BeetlRenderFactory;

public class ViewConfig extends JFinalConfig {
	
	private static String DB_ADDR;
	private static String DB_PORT;
	private static String DB_NAME;
	private static String DB_USER;
	private static String DB_PSWD;
	
	@Override
	public void configConstant(Constants me) {
		// 加载配置
		PropKit.use("config.txt");
		
		// 获取 自定义配置
		DB_ADDR = PropKit.get("DataBaseAddr");
		DB_PORT = PropKit.get("DataBasePort");
		DB_NAME = PropKit.get("DataBaseName");
		DB_USER = PropKit.get("DataBaseUser");
		DB_PSWD = PropKit.get("DataBasePassword");
		
		// 开发模式
		me.setDevMode(PropKit.getBoolean("devModel"));
		// 集成 Beetl
		me.setMainRenderFactory(new BeetlRenderFactory());
	}

	@Override
	public void configHandler(Handlers me) {
	}

	@Override
	public void configInterceptor(Interceptors me) {
	}
	
	@Override
	public void configPlugin(Plugins me) {
		C3p0Plugin cp = new C3p0Plugin("jdbc:mysql://" + DB_ADDR + ":" + DB_PORT + "/" + DB_NAME, DB_USER, DB_PSWD);
		me.add(cp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
		me.add(arp);
		arp.addMapping("logs", Logs.class);
		arp.addMapping("manager", Manager.class);
		arp.addMapping("employee", Employee.class);
		arp.addMapping("employee_wechat", EmployeeWechat.class);
		arp.addMapping("leave", Leave.class);
		arp.addMapping("balance", Balance.class);
		arp.addMapping("processes", Processes.class);
		arp.addMapping("processes_node", ProcessesNode.class);
		arp.addMapping("processes_result", ProcessesResult.class);
		arp.addMapping("processes_binding", ProcessesBinding.class);
		arp.addMapping("email_task", EmailTask.class);
	}

	@Override
	public void configRoute(Routes me) {
		// 设置 根路径
		me.add("/", LeaveController.class);
		me.add("/employee", EmployeeController.class);
		me.add("/processes", ProcessesController.class);
		me.add("/balance", BalanceController.class);
	}
	
	@Override
	public void afterJFinalStart() {
		// 执行 邮件定时器
		MailService.me.executeTimer();
	}
	
}
