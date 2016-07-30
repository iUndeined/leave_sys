package me.cjd.web;

import java.util.List;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import me.cjd.intr.AuthIntr;
import me.cjd.pojo.Balance;
import me.cjd.pojo.Employee;
import me.cjd.service.BalanceService;
import me.cjd.service.EmplService;
import me.cjd.utils.DoubleUtil;
import me.cjd.utils.ExcelReader;

public class BalanceController extends Controller {
	
	public final static String UPLOAD_KEY = "upload_ban_list";
	
	public final static String UPLOAD_SP_KEY = "upload_ban_sp_list";
	
	/**
	 * 年假信息列表
	 */
	@Before(AuthIntr.class)
	public void list(){
		Page<Employee> emplPage = EmplService.page(this);
		List<Employee> emplL = emplPage.getList();
		for (Employee e : emplL) {
			int id = e.getId();
			// 查询 年假信息
			Balance balance = BalanceService.findByEmplId(id);
			e.put("ba", balance);
		}
		this.setAttr("emplL", emplL);
		this.setAttr("emplPage", emplPage);
		this.keepPara("title", "redirectUrl");
		this.render("/views/manager/balance-list.html");
	}
	
	@Before(AuthIntr.class)
	public void save(){
		// 获取
		Balance balance = this.getModel(Balance.class, "be");
		// 获取 id
		Integer id = balance.getId();
		if (id == null) {
			balance.save();
		} else {
			balance.update();
		}
		this.redirect("/balance/list");
	}
	
	@Before(AuthIntr.class)
	public void manager(){
		int id = this.getParaToInt();
		// 查找 雇员
		Employee employee = Employee.me.findById(id);
		// 查询 年假信息
		Balance balance = BalanceService.findByEmplId(id);
		if (balance == null) {
			balance = new Balance();
		}
		this.setAttr("el", employee);
		this.setAttr("be", balance);
		this.render("/views/manager/balance-edit.html");
	}
	
	/**
	 * 查找员工年假
	 */
	public void findFreeLeave(){
		Balance ban = Balance.me.findFirst("select i.curr_rest_al from balance i where i.empl_id = ? ", this.getParaToInt());
		this.renderJson(ban == null ? 0 : ban.getCurrRestAl());
	}
	
	/**
	 * 查询剩余调休
	 */
	public void findRestLil(){
		Balance ban = Balance.me.findFirst("select i.curr_rest_lil from balance i where i.empl_id = ? ", this.getParaToInt());
		this.renderJson(ban == null ? 0 : ban.getCurrRestLil());
	}
	
	@Before(AuthIntr.class)
	public void upload(){
		this.removeSessionAttr(UPLOAD_KEY);
		this.render("/views/manager/hr-upload.html");
	}
	
	@Before(AuthIntr.class)
	public void upload_read(){
		// 获取 上传文件类
		UploadFile uFile = this.getFile("excel");
		// 读取 excel转换成列表
		List<Balance> emplL = ExcelReader.readBalance(uFile.getFile());
		
		for (Balance i : emplL) {
			// 根据工号判断账户是否存在
			String no = i.getStr("empl_no");
			Employee exs = Employee.me.findFirst("select e.id from employee as e where e.employ_no = ? ", no);
			i.setEmplId(exs == null ? null : exs.getId());
		}
		
		// 保存进session
		this.removeSessionAttr(UPLOAD_KEY);
		this.setSessionAttr(UPLOAD_KEY, emplL);
		
		// 有东西
		this.setAttr("isome", emplL != null && emplL.size() > 0);
		this.setAttr("banL", emplL);
		this.render("/views/manager/hr-upload.html");
	}
	
	@Before(AuthIntr.class)
	public void uploadSp(){
		this.upload();
		this.render("/views/manager/month-upload.html");
	}
	
	@Before(AuthIntr.class)
	public void uploadSpRead(){
		this.upload_read();
		this.render("/views/manager/month-upload.html");
	}
	
	@Before(AuthIntr.class)
	public void uploadSpAct(){
		// 获取 上传列表
		List<Balance> uploadL = this.getSessionAttr(UPLOAD_KEY);
		
		if (uploadL == null || uploadL.isEmpty()) {
			this.renderText("{'success': false, 'message': '上传失败，没有找到要上传的内容'}");
			return;
		}
		
		for (Balance e : uploadL) {
			this.upradeMonthByEmplNo(e.getEmplNo(), e.getCurrYearAddAl(), e.getCurrYearAddLil());
		}
		
		this.renderText("{'success': true, 'message': '上传成功'}");
	}
	
	@Before(AuthIntr.class)
	public void upload_act(){
		// 获取 上传列表
		List<Balance> uploadL = this.getSessionAttr(UPLOAD_KEY);
		
		// 删除 session
		this.removeSessionAttr(UPLOAD_KEY);
		
		if (uploadL == null || uploadL.isEmpty()) {
			this.renderText("{'success': false, 'message': '上传失败，没有找到要上传的内容'}");
			return;
		}
		
		for (Balance e : uploadL) {
			Integer id = e.getEmplId();
			if (id == null) {
				continue;
			}
			
			Balance old = BalanceService.findByEmplId(id);
			
			if (old == null) {
				e.save();
			} else {
				e.set("id", old.get("id")).update();
			}
		}
		
		this.renderText("{'success': true, 'message': '上传成功'}");
	}
	
	/**
	 * 每月调整方法
	 */
	@Before(AuthIntr.class)
	public void monthUpgrade(){
		this.manager();
		this.render("/views/manager/balance-month-upgrade.html");
	}
	
	public boolean upradeMonthByEmplNo(String emplNo, double currYearAddAl, double currYearAddLil){
		// 根据工号查找员工
		Employee empl = EmplService.getByEmplNo(emplNo);
		if (empl == null) {
			return false;
		}
		
		Balance balance = BalanceService.findByEmplId(empl.getId());
		
		if (balance == null) {
			balance = new Balance()
					.setEmplId(empl.getId())
					.setEmplName(empl.getName())
					.setCurrYearAddAl(0D)
					.setCurrYearAddLil(0D)
					.setCurrRestAl(0D)
					.setCurrRestLil(0D)
					.setEmplNo(emplNo);
			balance.save();
		}
		
		return this.upradeMonthAddLeave(balance.getId(), currYearAddAl, currYearAddLil);
	}
	
	public boolean upradeMonthAddLeave(int id, double currYearAddAl, double currYearAddLil){
		Balance beOld = Balance.me.findById(id);
		
		return beOld
			.setCurrYearAddAl(currYearAddAl)
			.setCurrYearAddLil(currYearAddLil)
			.setCurrRestAl((currYearAddAl + DoubleUtil.get(beOld.getCurrEndAl())) - DoubleUtil.get(beOld.getCurrYearApplyAl()))
			.setCurrRestLil((currYearAddLil + DoubleUtil.get(beOld.getCurrEndLil())) - DoubleUtil.get(beOld.getCurrYearApplyLil()))
			.update();
	}
	
	@Before(AuthIntr.class)
	public void monthUpgradeAct(){
		Balance be = this.getModel(Balance.class, "be");
		
		// 获取 id
		Integer id = be.getId();
		
		if (id == null) {
			be.save();
		} else {
			this.upradeMonthAddLeave(id, be.getCurrYearAddAl(), be.getCurrYearAddLil());
		}
		
		this.redirect("/balance/list?redirectUrl=monthUpgrade&title=每月调整");
	}
	
}
