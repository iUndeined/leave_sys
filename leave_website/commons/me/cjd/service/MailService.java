package me.cjd.service;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jfinal.plugin.activerecord.Db;

import me.cjd.em.EmailTaskEnum;
import me.cjd.pojo.EmailTask;
import me.cjd.pojo.Employee;
import me.cjd.pojo.Leave;
import me.cjd.pojo.Manager;
import me.cjd.pojo.ProcessesNode;
import me.cjd.pojo.ProcessesResult;
import me.cjd.utils.DateUtil;
import me.cjd.utils.MailSender;
import me.cjd.utils.StringUtil;

public class MailService {
	
	public final static MailService me = new MailService();
	
	public final ThreadLocal<Timer> localTimer = new ThreadLocal<>();
	
	public void executeTimer(){
		Timer timer = localTimer.get();
		
		if (timer != null) {
			timer.cancel();
		}
		
		// 声明 定时器
		timer = new Timer("email_sender_timer"); 
		// 声明 非处理的查询语句
		String sql = "select * from email_task as i where i.status = ? ";
		// 查询 未处理的列表
		List<EmailTask> list = EmailTask.me.find(sql, EmailTaskEnum.STATUS_NO.value);
		
		Date currentDate = new Date();
		// 循环加载定时器
		for (final EmailTask i : list) {
			try {
				Date executeDate = DateUtil.toDate(i.getExecuteDate());
				// 转换失败或执行日期在当前日期之前，取消任务，防止疯狂乱发
				if (executeDate == null || executeDate.after(currentDate)) {
					i.setStatus(EmailTaskEnum.STATUS_OK.value).update();
					continue;
				}
				
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						// 变更 执行状态
						i.setStatus(EmailTaskEnum.STATUS_OK.value).update();
						// 执行 发送
						onProcessPushedToManager(ProcessesNode.me.findById(i.getProcessesNodeId()), i.getLeaveId());
					}
				}, executeDate);
				
				
			} catch (Exception e) {
				continue;
			}
		}
		
		// 设置
		localTimer.set(timer);
	}
	
	public void cancelTask(int leaveId){
		// 声明 语句
		String sUpdate = "UPDATE email_task AS i SET i.`status` = ? WHERE i.leave_id = ? ";
		// 修改 任务状态
		Db.update(sUpdate, EmailTaskEnum.STATUS_OK.value, leaveId);
		// 重启定时器
		this.executeTimer();
	}
	
	/**
	 * 创建三日提醒任务，取消之前节点任务
	 * @param node 流程节点
	 * @param leaveId 假单ID
	 */
	public void buildThreeDayTaskAndCancelPrevTask(ProcessesNode node, int leaveId){
		if (node == null) {
			return;
		}
		
		// 获取 管理员ID
		int managerId = node.getManagerId();
		// 查找 管理员
		Manager manager = Manager.me.findById(managerId);
		// 获取 邮件
		String email = manager.getEmail();
		
		if (StringUtil.isEmpty(email)) {
			return;
		}
		
		// 获取 上一节点ID
		Integer prevNodeId = node.getPrevNodeId();
		// 有上一人
		if (prevNodeId != null) {
			// 声明 语句
			String sUpdate = "UPDATE email_task AS i SET i.`status` = ? WHERE i.leave_id = ? AND i.processes_node_id = ? ";
			// 修改 任务状态
			Db.update(sUpdate, EmailTaskEnum.STATUS_OK.value, leaveId, prevNodeId);
		}
		
		// 创建三条任务
		for (int i = 0; i < 3; i ++) {
			new EmailTask()
			.setLeaveId(leaveId)
			.setProcessesNodeId(node.getId())
			.setExecuteDate(DateUtil.addDayForTask(i + 1))
			.setCreatedDate(DateUtil.current())
			.setStatus(EmailTaskEnum.STATUS_NO.value)
			.save();
		}
		
		// 执行 定时器
		this.executeTimer();
	}
	
	public void onProcessPushedToManager(ProcessesNode node, int leaveId){
		// 获取 管理员id
		int managerId = node.getManagerId();
		Manager manager = Manager.me.findById(managerId);
		// 获取 管理员名称
		String managerName = manager.getName();
		String email = manager.getEmail();
		if (StringUtil.isEmpty(email)) {
			return;
		}
		// 
		Leave leave = Leave.me.findById(leaveId);
		String applyMan = leave.getApplyMan();
		String title = "ELEAVE: 关于 " + applyMan + " 的请假申请通知";
		// 声明 内容缓存
		StringBuffer content = new StringBuffer();
		
		// 内容拼接
		content
		.append("Dear {{:name}}<br/><br/>")
		.append("There are E-leave(s) pending for your approval. <br/><br/>")
		.append("Please click on the link below to E-leave system. <br/><br/>")
		.append("You will be able to view on your list of outstanding E-leave(s) Link to: review on your list of <a href=\"{{:link}}\">Outstanding Task</a><br/><br/>")
		.append("You can Approve OR Reject the request.<br/>");
		
		// 发送邮件通知
		new MailSender(email, title, content.toString()
			.replace("{{:name}}", managerName)
			.replace("{{:link}}", "http://116.239.24.10/leave/mi?l=" + leaveId + "&m=" + managerId + "&e=" + email));
	}
	
	public void onProcessPushedToEmpl(ProcessesResult result, int leaveId){
		Leave leave = Leave.me.findById(leaveId);
		// 获取 申请人ID
		int emplId = leave.getEmployId();
		// 查询 员工
		Employee empl = Employee.me.findById(emplId);
		// 获取 地址
		String email = empl.getEmail();
		if (StringUtil.isEmpty(email)) {
			return;
		}
		StringBuffer content = new StringBuffer();
		
		content
		.append("Dear {{:name}}, <br/><br/>")
		.append("Your leave application submitted on xxx was approved by your manager. You can access <a href=\"{{:link}}\">link</a> to review the details.<br/><br/>")
		.append("Thanks!<br/><br/>");
		
		// 发送邮件通知
		new MailSender(email, "ELEAVE: 关于 " + leaveId + " 号请假单被处理通知", 
				content.toString()
				.replace("{{:name}}", empl.getName())
				.replace("{{:link}}", "http://116.239.24.10/leave/ers?l=" + leaveId));
	}
	
	public void onProcessRejected(ProcessesResult result, int leaveId){
		Leave leave = Leave.me.findById(leaveId);
		// 获取 申请人ID
		int emplId = leave.getEmployId();
		// 查询 员工
		Employee empl = Employee.me.findById(emplId);
		// 获取 地址
		String email = empl.getEmail();
		if (StringUtil.isEmpty(email)) {
			return;
		}
		StringBuffer content = new StringBuffer();
		
		content
		.append("Dear {{:name}}, <br/><br/>")
		.append("Your leave application submitted on xxx was rejected by your manager. You can access <a href=\"{{:link}}\">link</a> to review the details. <br/><br/>")
		.append("Thanks!<br/><br/>");
		
		// 发送邮件通知
		new MailSender(email, "ELEAVE: 关于 " + leaveId + " 号请假单被否决通知", 
				content.toString()
				.replace("{{:name}}", empl.getName())
				.replace("{{:link}}", "http://116.239.24.10/leave/ers?l=" + leaveId));
	}
	
}
