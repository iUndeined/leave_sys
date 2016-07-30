package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class EmployeeWechat extends Model<EmployeeWechat> {
	
	private static final long serialVersionUID = 1L;
	
	public static final EmployeeWechat me = new EmployeeWechat();
	
	public static final String id = "id";
	public static final String userId = "user_id";
	public static final String emplId = "empl_id";
	
	public List<EmployeeWechat> all(){
		return this.find("select * from employee_wechat");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from employee_wechat as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<EmployeeWechat> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from employee_wechat");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public EmployeeWechat setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public String getUserId(){
		return this.getStr(userId);
	}
	
	public EmployeeWechat setUserId(String v){
		this.set(userId, v);
		return this;
	}
	
	public Integer getEmplId(){
		return this.getInt(emplId);
	}
	
	public EmployeeWechat setEmplId(Integer v){
		this.set(emplId, v);
		return this;
	}
	
}