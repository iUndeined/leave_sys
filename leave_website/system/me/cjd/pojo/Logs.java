package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class Logs extends Model<Logs> {
	
	private static final long serialVersionUID = 1L;
	
	public static final Logs me = new Logs();
	
	public static final String id = "id";
	public static final String man = "man";
	public static final String manId = "man_id";
	public static final String type = "type";
	public static final String fromId = "from_id";
	public static final String content = "content";
	public static final String createdDate = "created_date";
	
	public List<Logs> all(){
		return this.find("select * from logs");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from logs as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<Logs> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from logs");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public Logs setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public String getMan(){
		return this.getStr(man);
	}
	
	public Logs setMan(String v){
		this.set(man, v);
		return this;
	}
	
	public Integer getManId(){
		return this.getInt(manId);
	}
	
	public Logs setManId(Integer v){
		this.set(manId, v);
		return this;
	}
	
	public String getType(){
		return this.getStr(type);
	}
	
	public Logs setType(String v){
		this.set(type, v);
		return this;
	}
	
	public Integer getFromId(){
		return this.getInt(fromId);
	}
	
	public Logs setFromId(Integer v){
		this.set(fromId, v);
		return this;
	}
	
	public String getContent(){
		return this.getStr(content);
	}
	
	public Logs setContent(String v){
		this.set(content, v);
		return this;
	}
	
	public Timestamp getCreatedDate(){
		return this.getTimestamp(createdDate);
	}
	
	public Logs setCreatedDate(Timestamp v){
		this.set(createdDate, v);
		return this;
	}
	
}