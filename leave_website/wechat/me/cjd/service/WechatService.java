package me.cjd.service;

import java.util.ArrayList;
import java.util.List;
import me.cjd.http.core.HttpUtil;
import me.cjd.http.inter.IParam;
import me.cjd.http.params.StrParam;
import me.cjd.pojo.Employee;
import me.cjd.pojo.EmployeeWechat;
import me.cjd.pojo.Leave;
import me.cjd.pojo.ProcessesNode;
import me.cjd.pojo.ProcessesResult;
import me.cjd.utils.StringUtil;

public class WechatService {
	
	/** 单例 实例 */
	public final static WechatService me = new WechatService();
	
	public String getUserId(int emplId){
		// 声明 语句
		String sql = "select i.user_id from employee_wechat i where i.empl_id = ? ";
		// 查询 微信绑定
		EmployeeWechat wechat = EmployeeWechat.me.findFirst(sql, emplId);
		// 返回 微信用户id
		return wechat == null ? null : wechat.getUserId();
	}
	
	public void onApplySuccessFeedback(Leave leave){
		// 获取 员工id
		int emplId = leave.getEmployId();
		// 发送 消息
		this.sendText(emplId, "您申请 " + leave.getApplyDays() + "天 '" + leave.getType() + "'，已成功送达管理者，请耐心等候处理！");
	}
	
	public void onProcessPassed(int leaveId){
		Leave leave = Leave.me.findById(leaveId);
		int emplId = leave.getEmployId();
		this.sendText(emplId, "您的假单申请已通过，"
			+ "\r\n假单号：" + leave.getId() + "；"
			+ "\r\n假单类型：" + leave.getType() + "；"
			+ "\r\n申请天数：" + leave.getApplyDays());
	}
	
	public void onProcessRejected(ProcessesResult result, int leaveId){
		Leave leave = Leave.me.findById(leaveId);
		int emplId = leave.getEmployId();
		this.sendText(emplId, "您的假单申请已被否决，"
			+ "\r\n假单号：" + leave.getId() + "；"
			+ "\r\n假单类型：" + leave.getType() + "；"
			+ "\r\n申请天数：" + leave.getApplyDays()
			+ "\r\n否决意见：" + result.getSuggestion());
	}
	
	public void onProcessPushed(ProcessesNode node, int leaveId){
		// 获取 流程节点对应的管理员id
		int managerId = node.getManagerId();
		// 查询 绑定的个人账号
		Employee empl = EmplService.getByManagerId(managerId);
		
		if (empl == null) {
			return;
		}
		// 获取 员工id
		int emplId = empl.getId();
		// 
		Leave leave = Leave.me.findById(leaveId);
		String applyMan = leave.getApplyMan();
		Double applyDays = leave.getApplyDays();
		String title = applyMan + "请假通知";
		// 将申请信息发给审批者
		this.sendNews(emplId, leaveId, title, 
				applyMan + "想要申请" + 
				applyDays + "天" + leave.getType() +
				"\r\n点击进入审批页处理");
	}
	
	public void sendText(int emplId, String content){
		// 查询 微信用户id
		String userId = this.getUserId(emplId);
		// 没有绑定
		if (StringUtil.isEmpty(userId)) {
			return;
		}
		List<IParam> params = new ArrayList<>(2);
		params.add(new StrParam("userId", userId));
		params.add(new StrParam("content", content));
		// 使用线程发送 申请成功消息
		new SendTextThread(params).start();
	}
	
	/**
	 * 发送 新闻消息方法
	 * @param emplId 员工id
	 * @param url 点击跳转的页面
	 * @param title 标题
	 * @param describe 描述
	 */
	public void sendNews(int emplId, int leaveId, String title, String describe){
		// 查询 微信用户id
		String userId = this.getUserId(emplId);
		// 没有绑定
		if (StringUtil.isEmpty(userId)) {
			return;
		}
		List<IParam> params = new ArrayList<>(2);
		params.add(new StrParam("userId", userId));
		params.add(new StrParam("leaveId", String.valueOf(leaveId)));
		params.add(new StrParam("title", title));
		params.add(new StrParam("describe", describe));
		// 使用线程发送 申请成功消息
		new SendNotiThread(params).start();
	}
	
}

class SendTextThread extends SendThread {
	public SendTextThread(List<IParam> params) {
		super("http://localhost/leave_wechat/sendText", params);
	}
}

class SendNotiThread  extends SendThread {
	public SendNotiThread(List<IParam> params) {
		super("http://localhost/leave_wechat/sendProcessNoti", params);
	}
}

class SendThread extends Thread {
	
	private String url;
	private List<IParam> params;
	
	public SendThread(String url, List<IParam> params) {
		super();
		this.url = url;
		this.params = params;
	}

	@Override
	public void run() {
		HttpUtil.post(this.url, this.params);
	}
	
}
