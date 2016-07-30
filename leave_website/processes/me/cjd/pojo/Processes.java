package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class Processes extends Model<Processes> {
	
	private static final long serialVersionUID = 1L;
	
	public static final Processes me = new Processes();
	
	public static final String id = "id";
	public static final String name = "name";
	public static final String createdDate = "created_date";
	public static final String status = "status";
	
	public List<Processes> all(){
		return this.find("select * from processes");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from processes as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<Processes> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from processes");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public Processes setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public String getName(){
		return this.getStr(name);
	}
	
	public Processes setName(String v){
		this.set(name, v);
		return this;
	}
	
	public Timestamp getCreatedDate(){
		return this.getTimestamp(createdDate);
	}
	
	public Processes setCreatedDate(Timestamp v){
		this.set(createdDate, v);
		return this;
	}
	
	public Integer getStatus(){
		return this.getInt(status);
	}
	
	public Processes setStatus(Integer v){
		this.set(status, v);
		return this;
	}
	
}