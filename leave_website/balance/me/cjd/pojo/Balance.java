package me.cjd.pojo;

import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Model;

public class Balance extends Model<Balance> {
	
	private static final long serialVersionUID = 1L;
	
	public static final Balance me = new Balance();
	
	public static final String id = "id";
	public static final String emplId = "empl_id";
	public static final String emplNo = "empl_no";
	public static final String emplName = "empl_name";
	public static final String joinDate = "join_date";
	public static final String afterWork = "after_work";
	public static final String totalWork = "total_work";
	public static final String lastYearRestAl = "last_year_rest_al";
	public static final String lastYearRestLil = "last_year_rest_lil";
	public static final String currYearAlQua = "curr_year_al_qua";
	public static final String currEndAl = "curr_end_al";
	public static final String currEndLil = "curr_end_lil";
	public static final String currYearApplyAl = "curr_year_apply_al";
	public static final String currYearApplyLil = "curr_year_apply_lil";
	public static final String currYearAddAl = "curr_year_add_al";
	public static final String currYearAddLil = "curr_year_add_lil";
	public static final String currRestAl = "curr_rest_al";
	public static final String currRestLil = "curr_rest_lil";
	
	public List<Balance> all(){
		return this.find("select * from balance");
	}
	
	public long count(){
		BigDecimal count = this.findFirst("select count(i.id) from balance as i").getBigDecimal("count(i.id)");
		return count == null ? 0 : count.longValue();
	}
	
	public Page<Balance> paginate(int pageNumber, int pageSize){
		return this.paginate(pageNumber, pageSize, "select *", "from balance");
	}
	
	public Integer getId(){
		return this.getInt(id);
	}
	
	public Balance setId(Integer v){
		this.set(id, v);
		return this;
	}
	
	public Integer getEmplId(){
		return this.getInt(emplId);
	}
	
	public Balance setEmplId(Integer v){
		this.set(emplId, v);
		return this;
	}
	
	public String getEmplNo(){
		return this.getStr(emplNo);
	}
	
	public Balance setEmplNo(String v){
		this.set(emplNo, v);
		return this;
	}
	
	public String getEmplName(){
		return this.getStr(emplName);
	}
	
	public Balance setEmplName(String v){
		this.set(emplName, v);
		return this;
	}
	
	public Timestamp getJoinDate(){
		return this.getTimestamp(joinDate);
	}
	
	public Balance setJoinDate(Timestamp v){
		this.set(joinDate, v);
		return this;
	}
	
	public Double getAfterWork(){
		return this.getDouble(afterWork);
	}
	
	public Balance setAfterWork(Double v){
		this.set(afterWork, v);
		return this;
	}
	
	public Double getTotalWork(){
		return this.getDouble(totalWork);
	}
	
	public Balance setTotalWork(Double v){
		this.set(totalWork, v);
		return this;
	}
	
	public Double getLastYearRestAl(){
		return this.getDouble(lastYearRestAl);
	}
	
	public Balance setLastYearRestAl(Double v){
		this.set(lastYearRestAl, v);
		return this;
	}
	
	public Double getLastYearRestLil(){
		return this.getDouble(lastYearRestLil);
	}
	
	public Balance setLastYearRestLil(Double v){
		this.set(lastYearRestLil, v);
		return this;
	}
	
	public Double getCurrYearAlQua(){
		return this.getDouble(currYearAlQua);
	}
	
	public Balance setCurrYearAlQua(Double v){
		this.set(currYearAlQua, v);
		return this;
	}
	
	public Double getCurrEndAl(){
		return this.getDouble(currEndAl);
	}
	
	public Balance setCurrEndAl(Double v){
		this.set(currEndAl, v);
		return this;
	}
	
	public Double getCurrEndLil(){
		return this.getDouble(currEndLil);
	}
	
	public Balance setCurrEndLil(Double v){
		this.set(currEndLil, v);
		return this;
	}
	
	public Double getCurrYearApplyAl(){
		return this.getDouble(currYearApplyAl);
	}
	
	public Balance setCurrYearApplyAl(Double v){
		this.set(currYearApplyAl, v);
		return this;
	}
	
	public Double getCurrYearApplyLil(){
		return this.getDouble(currYearApplyLil);
	}
	
	public Balance setCurrYearApplyLil(Double v){
		this.set(currYearApplyLil, v);
		return this;
	}
	
	public Double getCurrYearAddAl(){
		return this.getDouble(currYearAddAl);
	}
	
	public Balance setCurrYearAddAl(Double v){
		this.set(currYearAddAl, v);
		return this;
	}
	
	public Double getCurrYearAddLil(){
		return this.getDouble(currYearAddLil);
	}
	
	public Balance setCurrYearAddLil(Double v){
		this.set(currYearAddLil, v);
		return this;
	}
	
	public Double getCurrRestAl(){
		return this.getDouble(currRestAl);
	}
	
	public Balance setCurrRestAl(Double v){
		this.set(currRestAl, v);
		return this;
	}
	
	public Double getCurrRestLil(){
		return this.getDouble(currRestLil);
	}
	
	public Balance setCurrRestLil(Double v){
		this.set(currRestLil, v);
		return this;
	}
	
}