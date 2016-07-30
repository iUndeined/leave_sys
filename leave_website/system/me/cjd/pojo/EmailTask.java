package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class EmailTask extends Model<EmailTask> {
	
	private static final long serialVersionUID = 1L;
	
	public static final EmailTask me = new EmailTask();
	
	public static final String id = "id";
	public static final String processesNodeId = "processes_node_id";
	public static final String leaveId = "leave_id";
	public static final String executeDate = "execute_date";
	public static final String createdDate = "created_date";
	public static final String status = "status";
	
	public List<EmailTask> all(){
		return this.find("select * from email_task");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from email_task as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<EmailTask> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from email_task");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public EmailTask setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public Integer getProcessesNodeId(){
		return this.getInt(processesNodeId);
	}
	
	public EmailTask setProcessesNodeId(Integer v){
		this.set(processesNodeId, v);
		return this;
	}
	
	public Integer getLeaveId(){
		return this.getInt(leaveId);
	}
	
	public EmailTask setLeaveId(Integer v){
		this.set(leaveId, v);
		return this;
	}
	
	public Timestamp getExecuteDate(){
		return this.getTimestamp(executeDate);
	}
	
	public EmailTask setExecuteDate(Timestamp v){
		this.set(executeDate, v);
		return this;
	}
	
	public Timestamp getCreatedDate(){
		return this.getTimestamp(createdDate);
	}
	
	public EmailTask setCreatedDate(Timestamp v){
		this.set(createdDate, v);
		return this;
	}
	
	public Integer getStatus(){
		return this.getInt(status);
	}
	
	public EmailTask setStatus(Integer v){
		this.set(status, v);
		return this;
	}
	
}