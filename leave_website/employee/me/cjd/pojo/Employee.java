package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class Employee extends Model<Employee> {
	
	private static final long serialVersionUID = 1L;
	
	public static final Employee me = new Employee();
	
	public static final String id = "id";
	public static final String managerId = "manager_id";
	public static final String employNo = "employ_no";
	public static final String name = "name";
	public static final String password = "password";
	public static final String dept = "dept";
	public static final String desig = "desig";
	public static final String email = "email";
	public static final String status = "status";
	public static final String gone = "gone";
	
	public List<Employee> all(){
		return this.find("select * from employee");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from employee as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<Employee> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from employee");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public Employee setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public Integer getManagerId(){
		return this.getInt(managerId);
	}
	
	public Employee setManagerId(Integer v){
		this.set(managerId, v);
		return this;
	}
	
	public String getEmployNo(){
		return this.getStr(employNo);
	}
	
	public Employee setEmployNo(String v){
		this.set(employNo, v);
		return this;
	}
	
	public String getName(){
		return this.getStr(name);
	}
	
	public Employee setName(String v){
		this.set(name, v);
		return this;
	}
	
	public String getPassword(){
		return this.getStr(password);
	}
	
	public Employee setPassword(String v){
		this.set(password, v);
		return this;
	}
	
	public String getDept(){
		return this.getStr(dept);
	}
	
	public Employee setDept(String v){
		this.set(dept, v);
		return this;
	}
	
	public String getDesig(){
		return this.getStr(desig);
	}
	
	public Employee setDesig(String v){
		this.set(desig, v);
		return this;
	}
	
	public String getEmail(){
		return this.getStr(email);
	}
	
	public Employee setEmail(String v){
		this.set(email, v);
		return this;
	}
	
	public Integer getStatus(){
		return this.getInt(status);
	}
	
	public Employee setStatus(Integer v){
		this.set(status, v);
		return this;
	}
	
	public Integer getGone(){
		return this.getInt(gone);
	}
	
	public Employee setGone(Integer v){
		this.set(gone, v);
		return this;
	}
	
}