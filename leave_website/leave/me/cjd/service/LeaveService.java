package me.cjd.service;

import java.io.File;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import me.cjd.pojo.Balance;
import me.cjd.pojo.Employee;
import me.cjd.pojo.Leave;
import me.cjd.pojo.Logs;
import me.cjd.statics.LeaveState;
import me.cjd.statics.LogsType;
import me.cjd.utils.Cache;
import me.cjd.utils.DateUtil;
import me.cjd.utils.ImageUtil;
import me.cjd.utils.LoginedUtil;
import me.cjd.utils.ProcessesUtil;

public class LeaveService {
	
	private final static UploadFile image(UploadFile scrip, String destName){
		if (scrip == null) {
			return null;
		}
		
		// 获取 文件
		File scripFile = scrip.getFile();
		// 声明 新文件
		File destFile = new File(scripFile.getParentFile(), destName);
		// 文件 更名
		scripFile.renameTo(destFile);
		// 返回 处理后的
		return new UploadFile(scrip.getParameterName(), 
				scrip.getSaveDirectory(), 
				destName, 
				scrip.getOriginalFileName(), 
				scrip.getContentType());
	}
	
	/**
	 * 假期 申请 凭证图片 保存方法
	 * @param leaveId
	 * @param scrip
	 */
	private final static void imageForLeave(int leaveId, UploadFile scrip){
		if (scrip == null) {
			return;
		}
		// 声明 新名字
		String destName = ImageUtil.get(leaveId, scrip.getOriginalFileName());
		// 重命名图片
		image(scrip, destName);
		// 设置 凭证名称
		Leave.me.findById(leaveId).setScrip(destName).update();
	}
	
	public final static void confirm(Controller c){
		// 获取 凭证图片
		UploadFile scrip = c.getFile("scrip");
		// 获取 令牌
		String token = c.getPara("token");
		// 获取 表彰数据
		Leave leave = c.getModel(Leave.class, "lv");
		// 保持 参数
		c.keepPara("token");
		// 保持 参数传递
		c.setAttr("lv", leave);
		// 存缓存
		Cache.put("leave_" + token, leave);
		// 有凭证
		if (scrip != null) {
			// 处理一下
			String originalFileName = scrip.getOriginalFileName();
			String cacheImageName = ImageUtil.getCacheName(token, originalFileName);
			// 变更 图片名称
			scrip = image(scrip, cacheImageName);
			// 缓存起来
			Cache.put("leave_" + token + "_scrip", scrip);
			// 传值页面
			c.setAttr("cacheImageName", cacheImageName);
		}
	}
	
	/**
	 * 假期申请方法
	 */
	public final static void action(Controller c){
		// 获取 token
		String token = c.getPara("token");
		// 获取 凭证 图片
		UploadFile scrip = Cache.remove("leave_" + token + "_scrip");
		// 获取 假期信息
		Leave leave = Cache.remove("leave_" + token);
		// 获取 流程id
		Integer pid = leave.getProcessesId();
		
		if (pid == null) {
			c.renderText("没指定审批流程");
			return;
		}
		
		// 当前时间
		leave.setCreatedDate(DateUtil.current());
		
		// 保存
		boolean result = leave.save();
		
		if (result) {
			Integer leaveId = leave.getId();
			// 保存 图片
			imageForLeave(leaveId, scrip);
			// 保存 申请日志
			applyLogs(c, leave);
			// 运行 流程
			ProcessesUtil.run(pid, leaveId);
			// 执行 微信模块
			WechatService.me.onApplySuccessFeedback(leave);
			// 跳转 至回执页
			c.redirect("/result/" + leaveId);
		} else {
			c.renderText("申请发生错误");
		}
	}
	
	public final static void applyLogs(Controller c, Leave lv){
		// 获取 id
		Integer leaveId = lv.getId();
		// 获取 请假天数
		Double applyDays = lv.getApplyDays();
		// 获取 假期类型
		String lType = lv.getType();
		// 获取 当前登录员工
		Employee current = LoginedUtil.currentEmpl(c);
		// 保存 日志
		LogsService.me.insert(LogsType.apply, leaveId, current.getId(), current.getName(), "申请" + applyDays + "天" + lType);
	}
	
	public final static void cancel(Controller c){
		// 获取 假单信息
		int leaveId = c.getParaToInt();
		Leave leave = Leave.me.findById(leaveId);
		// 将假单改为作废
		leave.setState(LeaveState.STATE_3).update();
		// 获取 假单类型
		String type = leave.getType();
		
		boolean isAl = type.equalsIgnoreCase("年假");
		boolean isLil = type.equalsIgnoreCase("调休");
		
		// 如果是年假
		if (isAl || isLil) {
			// 获取 申请人id
			int manId = leave.getApplyManId();
			// 获取 申请天数
			double applyDays = leave.getApplyDays();
			// 查询 年假信息
			Balance balance = BalanceService.findByEmplId(manId);
			// 防空
			if (balance != null) {
				if (isAl) {
					// 获取 剩余年假
					double total = balance.getCurrRestAl();
					double duang = balance.getCurrYearApplyAl();
					// 把年假加回去
					balance
					.setCurrYearApplyAl(duang - applyDays)
					.setCurrRestAl(total + applyDays).update();
				} else {
					// 获取 剩余年假
					double total = balance.getCurrRestLil();
					double duang = balance.getCurrYearApplyLil();
					// 把年假加回去
					balance
					.setCurrYearApplyLil(duang - applyDays)
					.setCurrRestLil(total + applyDays).update();
				}
			}
		}
		// 记录 日志
		cancelLogs(LogsType.invalid, leave);
		// 停止 流程
		ProcessesUtil.stopAllProcesses(leaveId);
		// 停止 邮件定时器
		MailService.me.cancelTask(leaveId);
		// 返回 成功
		c.renderJson("success", true);
	}
	
	public final static void cancelEmpl(Controller c){
		// 获取 假单id
		int leaveId = c.getParaToInt();
		// 查询 假单
		Leave leave = Leave.me.findById(leaveId);
		
		if (leave == null) {
			c.renderJson("success", false);
			return;
		}
		
		// 作废假单
		leave.setState(LeaveState.STATE_3).update();
		// 记录 日志
		cancelLogs(LogsType.cancel, leave);
		// 停止流程
		ProcessesUtil.stopAllProcesses(leaveId);
		// 停止邮件定时器
		MailService.me.cancelTask(leaveId);
		// 成功
		c.renderJson("success", true);
	}
	
	public final static void cancelLogs(LogsType type, Leave lv){
		LogsService.me.insert(type, lv.getId(), lv.getApplyManId(), lv.getApplyMan(), type.name + "了假单");
	}
	
	public final static void ologs(Controller c){
		// 查询 系统日志
		List<Logs> list = Logs.me.find("select * from logs i order by i.created_date desc ");
		// 传出页面
		c.setAttr("list", list);
	}
	
}
