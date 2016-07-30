package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class ProcessesBinding extends Model<ProcessesBinding> {
	
	private static final long serialVersionUID = 1L;
	
	public static final ProcessesBinding me = new ProcessesBinding();
	
	public static final String id = "id";
	public static final String manId = "man_id";
	public static final String processesId = "processes_id";
	
	public List<ProcessesBinding> all(){
		return this.find("select * from processes_binding");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from processes_binding as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<ProcessesBinding> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from processes_binding");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public ProcessesBinding setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public Integer getManId(){
		return this.getInt(manId);
	}
	
	public ProcessesBinding setManId(Integer v){
		this.set(manId, v);
		return this;
	}
	
	public Integer getProcessesId(){
		return this.getInt(processesId);
	}
	
	public ProcessesBinding setProcessesId(Integer v){
		this.set(processesId, v);
		return this;
	}
	
}