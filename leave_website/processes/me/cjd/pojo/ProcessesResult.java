package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class ProcessesResult extends Model<ProcessesResult> {
	
	private static final long serialVersionUID = 1L;
	
	public static final ProcessesResult me = new ProcessesResult();
	
	public static final String id = "id";
	public static final String leaveId = "leave_id";
	public static final String processesNodeId = "processes_node_id";
	public static final String managerId = "manager_id";
	public static final String managerName = "manager_name";
	public static final String state = "state";
	public static final String suggestion = "suggestion";
	public static final String replyDate = "reply_date";
	public static final String createdDate = "created_date";
	
	public List<ProcessesResult> all(){
		return this.find("select * from processes_result");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from processes_result as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<ProcessesResult> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from processes_result");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public ProcessesResult setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public Integer getLeaveId(){
		return this.getInt(leaveId);
	}
	
	public ProcessesResult setLeaveId(Integer v){
		this.set(leaveId, v);
		return this;
	}
	
	public Integer getProcessesNodeId(){
		return this.getInt(processesNodeId);
	}
	
	public ProcessesResult setProcessesNodeId(Integer v){
		this.set(processesNodeId, v);
		return this;
	}
	
	public Integer getManagerId(){
		return this.getInt(managerId);
	}
	
	public ProcessesResult setManagerId(Integer v){
		this.set(managerId, v);
		return this;
	}
	
	public String getManagerName(){
		return this.getStr(managerName);
	}
	
	public ProcessesResult setManagerName(String v){
		this.set(managerName, v);
		return this;
	}
	
	public Integer getState(){
		return this.getInt(state);
	}
	
	public ProcessesResult setState(Integer v){
		this.set(state, v);
		return this;
	}
	
	public String getSuggestion(){
		return this.getStr(suggestion);
	}
	
	public ProcessesResult setSuggestion(String v){
		this.set(suggestion, v);
		return this;
	}
	
	public Timestamp getReplyDate(){
		return this.getTimestamp(replyDate);
	}
	
	public ProcessesResult setReplyDate(Timestamp v){
		this.set(replyDate, v);
		return this;
	}
	
	public Timestamp getCreatedDate(){
		return this.getTimestamp(createdDate);
	}
	
	public ProcessesResult setCreatedDate(Timestamp v){
		this.set(createdDate, v);
		return this;
	}
	
}