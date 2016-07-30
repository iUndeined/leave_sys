package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class ProcessesNode extends Model<ProcessesNode> {
	
	private static final long serialVersionUID = 1L;
	
	public static final ProcessesNode me = new ProcessesNode();
	
	public static final String id = "id";
	public static final String processesId = "processes_id";
	public static final String managerId = "manager_id";
	public static final String managerName = "manager_name";
	public static final String prevNodeId = "prev_node_id";
	public static final String nextNodeId = "next_node_id";
	public static final String first = "first";
	public static final String last = "last";
	
	public List<ProcessesNode> all(){
		return this.find("select * from processes_node");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from processes_node as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<ProcessesNode> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from processes_node");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public ProcessesNode setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public Integer getProcessesId(){
		return this.getInt(processesId);
	}
	
	public ProcessesNode setProcessesId(Integer v){
		this.set(processesId, v);
		return this;
	}
	
	public Integer getManagerId(){
		return this.getInt(managerId);
	}
	
	public ProcessesNode setManagerId(Integer v){
		this.set(managerId, v);
		return this;
	}
	
	public String getManagerName(){
		return this.getStr(managerName);
	}
	
	public ProcessesNode setManagerName(String v){
		this.set(managerName, v);
		return this;
	}
	
	public Integer getPrevNodeId(){
		return this.getInt(prevNodeId);
	}
	
	public ProcessesNode setPrevNodeId(Integer v){
		this.set(prevNodeId, v);
		return this;
	}
	
	public Integer getNextNodeId(){
		return this.getInt(nextNodeId);
	}
	
	public ProcessesNode setNextNodeId(Integer v){
		this.set(nextNodeId, v);
		return this;
	}
	
	public Integer getFirst(){
		return this.getInt(first);
	}
	
	public ProcessesNode setFirst(Integer v){
		this.set(first, v);
		return this;
	}
	
	public Integer getLast(){
		return this.getInt(last);
	}
	
	public ProcessesNode setLast(Integer v){
		this.set(last, v);
		return this;
	}
	
}