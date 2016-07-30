package me.cjd.utils;

import java.util.List;
import com.jfinal.plugin.activerecord.Db;
import me.cjd.pojo.Leave;
import me.cjd.pojo.Logs;
import me.cjd.pojo.ProcessesNode;
import me.cjd.pojo.ProcessesResult;
import me.cjd.service.MailService;
import me.cjd.service.ProcessesService;
import me.cjd.service.WechatService;
import me.cjd.statics.LogsType;
import me.cjd.statics.ProcessesResultState;

public class ProcessesUtil {
	
	public final static void run(Integer processesId, Integer leaveId){
		if (processesId == null || leaveId == null) {
			throw new RuntimeException("缺少运行流程必要参数，启动失败");
		}
		
		// 声明 是首节点
		boolean isFirst = true;
		// 查询 节点
		List<ProcessesNode> nodeL = ProcessesNode.me.find("select i.id, i.manager_id, i.manager_name from processes_node as i where i.processes_id = ? order by i.id asc ", processesId);
		// 循环 生成 流程路径
		for (ProcessesNode node : nodeL) {
			// 实例 实体
			ProcessesResult rs = new ProcessesResult();
			
			// 将首节点状态改为待处理
			if (isFirst) {
				isFirst = false;
				rs.setState(ProcessesResultState.state_1);
				// 执行 微信模块
				WechatService.me.onProcessPushed(node, leaveId);
				MailService.me.onProcessPushedToManager(node, leaveId);
				// 创建 三天邮件发送任务
				MailService.me.buildThreeDayTaskAndCancelPrevTask(node, leaveId);
			}
			
			// 保存路径
			rs
			.set("manager_id", node.getInt("manager_id"))
			.set("manager_name", node.getStr("manager_name"))
			.set("processes_node_id", node.getInt("id"))
			.set("leave_id", leaveId)
			.setCreatedDate(DateUtil.current())
			.save();
		}
	}
	
	/**
	 * 流程推进方法
	 * @param state 状态 
	 * @param suggestion 审批意见
	 */
	public final static boolean push(int leaveId, int state, String suggestion){
		// 获取 当前路径
		ProcessesResult currentRs = ProcessesResult.me.findFirst("select * from processes_result as i where i.leave_id = ? and i.state = ? ", leaveId, ProcessesResultState.state_1);
		// 查询 当前假单
		Leave current = Leave.me.findById(leaveId);
		// 保存 当前处理结果
		currentRs
		.setState((state + 1))
		.setSuggestion(suggestion)
		.setReplyDate(DateUtil.current())
		.update();
		
		// 保存 流程日志
		logsProcesses(state, current, currentRs);
		
		switch (state) {
			// 同意，向下推进
			case 1:
				// 获取 当前流程节点
				int currentNodeId = currentRs.getInt("processes_node_id");
				// 查询 当前节点
				ProcessesNode currentNode = ProcessesNode.me.findById(currentNodeId);
				// 判断是否是最后节点
				boolean last = currentNode.getInt("last") == 1;
				// 最后节点
				if (last) {
					// 处理 批结信息
					ProcessesService.afterProcessesHandleFreeleave(current);
					current.set("state", state).update();
					// 执行 微信模块
					WechatService.me.onProcessPassed(leaveId);
					// 将结果推给申请人
					MailService.me.onProcessPushedToEmpl(currentRs, leaveId);
					// 取消 该假单的所有提醒任务
					MailService.me.cancelTask(leaveId);
				} else {
					// 查询 下一节点
					ProcessesNode nextNode = ProcessesNode.me.findFirst("select * from processes_node as i where i.prev_node_id = ? ", currentNodeId);
					int nextNodeId = nextNode.getInt("id");
					Db.update("update processes_result as i set i.state = ? where i.leave_id = ? and i.processes_node_id = ? and i.state = ? ", 
							ProcessesResultState.state_1, 
							leaveId, nextNodeId, 
							ProcessesResultState.state_0);
					// 执行 微信模块
					WechatService.me.onProcessPushed(nextNode, leaveId);
					// 执行 邮件发送模块
					MailService.me.onProcessPushedToManager(nextNode, leaveId);
					// 将结果推给申请人
					MailService.me.onProcessPushedToEmpl(currentRs, leaveId);
					// 创建 三天邮件发送任务
					MailService.me.buildThreeDayTaskAndCancelPrevTask(nextNode, leaveId);
				}
				return true;
			// 否决，终止流程
			case 2:
				stopAfterCurrentProcesses(leaveId);
				current.set("state", state).update();
				WechatService.me.onProcessRejected(currentRs, leaveId);
				MailService.me.onProcessRejected(currentRs, leaveId);
				// 取消 该假单的所有提醒任务
				MailService.me.cancelTask(leaveId);
				return true;
		}
		
		return false;
	}
	
	/**
	 * 停止所有流程（在途的，待处理的）
	 * @param leaveId
	 * @return
	 */
	public final static boolean stopAllProcesses(int leaveId){
		return stopProcesses(leaveId, ProcessesResultState.state_0) && 
			stopProcesses(leaveId, ProcessesResultState.state_1);
	}
	
	/**
	 * 停止当前审批人之后的流程
	 * @param leaveId 假单id
	 * @return
	 */
	public final static boolean stopAfterCurrentProcesses(int leaveId){
		return stopProcesses(leaveId, ProcessesResultState.state_0);
	}
	
	/**
	 * 停止流程
	 * @param leaveId 假单id
	 * @param stopState 需要停止的状态
	 * @return
	 */
	public final static boolean stopProcesses(int leaveId, int stopState){
		int result = Db.update("update processes_result as i set i.state = ? where i.state = ? and i.leave_id = ? ", 
			ProcessesResultState.state_4, 
			stopState, leaveId);
		return result > 0;
	}
	
	private final static void logsProcesses(int state, Leave lv, ProcessesResult result){
		// 获取 假期id
		Integer leaveId = lv.getId();
		Logs logs = new Logs()
		.setFromId(leaveId)
		.setMan(result.getManagerName())
		.setManId(result.getManagerId())
		.setType(LogsType.approval.name)
		.setCreatedDate(DateUtil.current());
		
		switch (state) {
			case 1:
				logs.setContent("同意了假单").save();
				break;
			case 2:
				logs.setContent("否决了假单").save();
				break;
		}
	}
	
}
