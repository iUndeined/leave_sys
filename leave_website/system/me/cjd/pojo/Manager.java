package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class Manager extends Model<Manager> {
	
	private static final long serialVersionUID = 1L;
	
	public static final Manager me = new Manager();
	
	public static final String id = "id";
	public static final String account = "account";
	public static final String name = "name";
	public static final String password = "password";
	public static final String isuper = "super";
	public static final String hr = "hr";
	public static final String email = "email";
	public static final String status = "status";
	
	public List<Manager> all(){
		return this.find("select * from manager");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from manager as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<Manager> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from manager");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public Manager setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public String getAccount(){
		return this.getStr(account);
	}
	
	public Manager setAccount(String v){
		this.set(account, v);
		return this;
	}
	
	public String getName(){
		return this.getStr(name);
	}
	
	public Manager setName(String v){
		this.set(name, v);
		return this;
	}
	
	public String getPassword(){
		return this.getStr(password);
	}
	
	public Manager setPassword(String v){
		this.set(password, v);
		return this;
	}
	
	public Integer getSuper(){
		return this.getInt(isuper);
	}
	
	public Manager setSuper(Integer v){
		this.set(isuper, v);
		return this;
	}
	
	public Integer getHr(){
		return this.getInt(hr);
	}
	
	public Manager setHr(Integer v){
		this.set(hr, v);
		return this;
	}
	
	public String getEmail(){
		return this.getStr(email);
	}
	
	public Manager setEmail(String v){
		this.set(email, v);
		return this;
	}
	
	public Integer getStatus(){
		return this.getInt(status);
	}
	
	public Manager setStatus(Integer v){
		this.set(status, v);
		return this;
	}
	
}