package me.cjd.service;

import me.cjd.pojo.Balance;
import me.cjd.pojo.Employee;
import me.cjd.pojo.Leave;
import me.cjd.pojo.Processes;
import me.cjd.pojo.ProcessesBinding;
import me.cjd.pojo.ProcessesResult;
import me.cjd.utils.DoubleUtil;
import me.cjd.utils.StringUtil;

import java.util.List;
import com.jfinal.core.Controller;

public class ProcessesService {
	
	public final static ProcessesResult getResult(Integer leaveId, Integer managerId){
		return ProcessesResult.me.findFirst("select * from processes_result as i where i.leave_id = ? and i.manager_id = ? ", leaveId, managerId);
	}
	
	public final static String getJson(Integer leaveId, Integer managerId){
		return getResult(leaveId, managerId).toJson();
	}
	
	public final static int getId(Integer leaveId, Integer managerId){
		return getResult(leaveId, managerId).getInt("id");
	}
	
	public final static List<Processes> list(){
		return Processes.me.find("select * from processes ");
	}
	
	public final static List<ProcessesResult> listResult(Integer leaveId){
		return ProcessesResult.me.find("select * from processes_result as i where i.leave_id = ? order by i.id asc ", leaveId);
	}
	
	public final static void initManagerPage(Controller c){
		// 查找 流程列表
		List<Processes> proL = Processes.me.find("select * from processes ");
		
		// 传值
		c.setAttr("proL", proL);
		c.setAttr("managerL", ManagerService.list());
	}
	
	public final static ProcessesBinding getBinding(int emplId){
		return ProcessesBinding.me.findFirst("select * from processes_binding i where i.man_id = ? ", emplId);
	}
	
	public final static void bindingProcesses(Employee empl){
		// 获取 部门
		String dept = empl.getDept();
		// 部门为空无法绑定
		if (StringUtil.isEmpty(dept)) {
			return;
		}
		// 查询 流程id
		Processes processes = Processes.me.findFirst("select i.id from processes i where i.name = ? ", dept);
		// 没有找到对应流程
		if (processes == null) {
			return;
		}
		// 获取 员工id
		int emplId = empl.getId();
		int processesId = processes.getInt("id");
		// 查询 是否有过绑定
		ProcessesBinding binding = getBinding(emplId);
		// 没有则新添绑定
		if (binding == null) {
			binding = new ProcessesBinding();
			binding.setManId(emplId).setProcessesId(processesId).save();
		// 有则变更绑定
		} else {
			binding.setProcessesId(processesId).update();
		}
	}
	
	public final static void bindingUpdate(int emplId, Integer processesId){
		if (processesId == null) {
			return;
		}
		// 查询 流程绑定
		ProcessesBinding binding = ProcessesService.getBinding(emplId);
		// 处理流程
		if (binding == null) {
			new ProcessesBinding().setManId(emplId).setProcessesId(processesId).save();
		} else {
			binding.setProcessesId(processesId).update();
		}
	}
	
	/**
	 * 审批通过后年假扣数
	 * @param leave
	 */
	public final static void afterProcessesHandleFreeleave(Leave leave){
		String type = leave.getType();
		boolean isAl = type.equalsIgnoreCase("年假");
		boolean isLil = type.equalsIgnoreCase("调休");
		// 不是年假不用处理
		if (isAl || isLil) {
			int emplId = leave.getApplyManId();
			Balance balance = BalanceService.findByEmplId(emplId);
			double apply = leave.getApplyDays();
			if (isAl) {
				double total = balance.getCurrRestAl();
				balance
				.setCurrYearApplyAl(DoubleUtil.get(balance.getCurrYearApplyAl()) + apply)
				.setCurrRestAl(total - apply).update();
			} else {
				double total = balance.getCurrRestLil();
				balance
				.setCurrYearApplyLil(DoubleUtil.get(balance.getCurrYearApplyLil()) + apply)
				.setCurrRestLil(total - apply)
				.update();
			}
		}
	}
	
}
