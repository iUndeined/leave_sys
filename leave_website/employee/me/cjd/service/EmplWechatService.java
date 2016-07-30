package me.cjd.service;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;

import me.cjd.model.OAuth2;
import me.cjd.pojo.Employee;
import me.cjd.pojo.EmployeeWechat;
import me.cjd.pojo.Manager;
import me.cjd.utils.LoginedUtil;
import me.cjd.utils.StringUtil;

public class EmplWechatService {
	
	public final static EmplWechatService me = new EmplWechatService();
	
	/**
	 * 通过 员工ID 获取微信绑定实体
	 * @param emplId 员工id
	 * @return 返回实体
	 */
	public EmployeeWechat get(int emplId){
		return EmployeeWechat.me.findFirst("select * from employee_wechat i where i.empl_id = ? ", emplId);
	}
	
	/**
	 * 通过 用户ID 获取微信绑定实体
	 * @param userId 用户id
	 * @return 返回实体
	 */
	public EmployeeWechat getByUserId(String userId){
		return EmployeeWechat.me.findFirst("select * from employee_wechat i where i.user_id = ? ", userId);
	}
	
	public void login(Controller c){
		// 获取 神秘代码
		String code = c.getPara("code");
		try {
			// 解析 信息
			OAuth2 auth = JSON.parseObject(code, OAuth2.class);
			// 获取 错误代码
			Integer errcode = auth.getErrcode();
			// 获取 用户信息错误
			if (errcode != null) {
				this.renderWechatAlert(c, "无法获取用户信息，错误代码 = " + errcode + "；错误信息 = " + auth.getErrmsg());
				return;
			}
			
			// 获取 用户ID
			String userId = auth.getUserId();
			// 查询 是否该微信已绑定
			EmployeeWechat wechat = this.getByUserId(userId);
			
			if (wechat == null) {
				this.renderWechatAlert(c, "该微信账号未绑定");
				return;
			}
			
			Employee empl = Employee.me.findById(wechat.getEmplId());
			
			if (empl == null) {
				this.renderWechatAlert(c, "员工账户不存在");
				return;
			}
			
			// 获取 管理员id
			Integer managerId = empl.getManagerId();
			
			if (managerId != null) {
				LoginedUtil.upgrade(c, Manager.me.findById(managerId));
			}
			
			// 更新 登录信息
			LoginedUtil.upgradeEmpl(c, empl);
			
			int state = -1;
			int leaveId = -1;
			String stateStr = c.getPara("state");
			if (stateStr.indexOf("_") != -1) {
				String[] states = stateStr.split("_");
				state = Integer.valueOf(states[0]);
				leaveId = Integer.valueOf(states[1]);
			} else {
				state = Integer.valueOf(stateStr);
			}
			
			// 跳转至申请假单页
			switch (state) {
				case 0:
					c.redirect("/applyToken");
					break;
				case 1:
					c.redirect("/employee/profile");
					break;
				case 2:
					c.redirect("/gun_wechat_page/" + leaveId);
					break;
				default:
					this.renderWechatAlert(c, "无法识别的state");
					break;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			this.renderWechatAlert(c, "微信回传信息不正确，无法绑定");
		}
	}
	
	/**
	 * 绑定账户信息
	 */
	public String connectBinding(int emplId, String code){
		if (emplId < 1 || StringUtil.isEmpty(code)) {
			return "微信回传参数信息为空，无法绑定";
		}
		
		try {
			// 解析 信息
			OAuth2 auth = JSON.parseObject(code, OAuth2.class);
			// 获取 错误代码
			Integer errcode = auth.getErrcode();
			// 获取 用户信息错误
			if (errcode != null) {
				return "无法获取用户信息，错误代码 = " + errcode + "；错误信息 = " + auth.getErrmsg();
			}
			
			// 获取 用户ID
			String userId = auth.getUserId();
			
			if (StringUtil.isEmpty(userId)) {
				return "请先关注微信企业号";
			}
			
			// 查询 账户是否绑定过
			EmployeeWechat checkEmpl = this.get(emplId);
			
			if (checkEmpl != null) {
				return "该个人账号已绑定有一个微信！！";
			}
			
			// 查询 是否该微信已绑定
			EmployeeWechat checkWechat = this.getByUserId(userId);
			
			if (checkWechat != null) {
				return "该微信账号已经绑定过了！！";
			}
			
			// 添加绑定
			new EmployeeWechat()
			.setUserId(userId)
			.setEmplId(emplId)
			.save();
		} catch (Exception e) {
			return "微信回传信息不正确，无法绑定";
		}
		
		return "恭喜您 ，绑定成功 ！！";
	}
	
	public void renderWechatAlert(Controller c, String msg){
		c.setAttr("resultMsg", msg);
		c.render("/views/employee/empl-connect-wehcat-result.html");
	}
	
}
