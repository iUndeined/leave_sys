package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class Leave extends Model<Leave> {
	
	private static final long serialVersionUID = 1L;
	
	public static final Leave me = new Leave();
	
	public static final String id = "id";
	public static final String employId = "employ_id";
	public static final String employNo = "employ_no";
	public static final String processesId = "processes_id";
	public static final String applyManId = "apply_man_id";
	public static final String applyMan = "apply_man";
	public static final String applyDept = "apply_dept";
	public static final String designation = "designation";
	public static final String type = "type";
	public static final String reason = "reason";
	public static final String startDate = "start_date";
	public static final String endDate = "end_date";
	public static final String applyDays = "apply_days";
	public static final String ytd = "ytd";
	public static final String mtd = "mtd";
	public static final String scrip = "scrip";
	public static final String state = "state";
	public static final String createdDate = "created_date";
	
	public List<Leave> all(){
		return this.find("select * from leave");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from leave as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<Leave> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from leave");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public Leave setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public Integer getEmployId(){
		return this.getInt(employId);
	}
	
	public Leave setEmployId(Integer v){
		this.set(employId, v);
		return this;
	}
	
	public String getEmployNo(){
		return this.getStr(employNo);
	}
	
	public Leave setEmployNo(String v){
		this.set(employNo, v);
		return this;
	}
	
	public Integer getProcessesId(){
		return this.getInt(processesId);
	}
	
	public Leave setProcessesId(Integer v){
		this.set(processesId, v);
		return this;
	}
	
	public Integer getApplyManId(){
		return this.getInt(applyManId);
	}
	
	public Leave setApplyManId(Integer v){
		this.set(applyManId, v);
		return this;
	}
	
	public String getApplyMan(){
		return this.getStr(applyMan);
	}
	
	public Leave setApplyMan(String v){
		this.set(applyMan, v);
		return this;
	}
	
	public String getApplyDept(){
		return this.getStr(applyDept);
	}
	
	public Leave setApplyDept(String v){
		this.set(applyDept, v);
		return this;
	}
	
	public String getDesignation(){
		return this.getStr(designation);
	}
	
	public Leave setDesignation(String v){
		this.set(designation, v);
		return this;
	}
	
	public String getType(){
		return this.getStr(type);
	}
	
	public Leave setType(String v){
		this.set(type, v);
		return this;
	}
	
	public String getReason(){
		return this.getStr(reason);
	}
	
	public Leave setReason(String v){
		this.set(reason, v);
		return this;
	}
	
	public Timestamp getStartDate(){
		return this.getTimestamp(startDate);
	}
	
	public Leave setStartDate(Timestamp v){
		this.set(startDate, v);
		return this;
	}
	
	public Timestamp getEndDate(){
		return this.getTimestamp(endDate);
	}
	
	public Leave setEndDate(Timestamp v){
		this.set(endDate, v);
		return this;
	}
	
	public Double getApplyDays(){
		return this.getDouble(applyDays);
	}
	
	public Leave setApplyDays(Double v){
		this.set(applyDays, v);
		return this;
	}
	
	public Double getYtd(){
		return this.getDouble(ytd);
	}
	
	public Leave setYtd(Double v){
		this.set(ytd, v);
		return this;
	}
	
	public Double getMtd(){
		return this.getDouble(mtd);
	}
	
	public Leave setMtd(Double v){
		this.set(mtd, v);
		return this;
	}
	
	public String getScrip(){
		return this.getStr(scrip);
	}
	
	public Leave setScrip(String v){
		this.set(scrip, v);
		return this;
	}
	
	public Integer getState(){
		return this.getInt(state);
	}
	
	public Leave setState(Integer v){
		this.set(state, v);
		return this;
	}
	
	public Timestamp getCreatedDate(){
		return this.getTimestamp(createdDate);
	}
	
	public Leave setCreatedDate(Timestamp v){
		this.set(createdDate, v);
		return this;
	}
	
}