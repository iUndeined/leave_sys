package me.cjd.web;

import me.cjd.intr.AuthIntr;
import me.cjd.pojo.Processes;
import me.cjd.pojo.ProcessesNode;
import me.cjd.service.ProcessesService;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

public class ProcessesController extends Controller {
	
	@Before(AuthIntr.class)
	public void index(){
		// 初始化信息
		ProcessesService.initManagerPage(this);
		
		this.render("/views/manager/processes.html");
	}
	
	public void save(){
		// 获取 流程名称
		String name = this.getPara("name");
		// 实例 流程实体
		Processes pro = new Processes();
		// 设置 属性
		pro.set("name", name).set("created_date", new Timestamp(System.currentTimeMillis())).save();
		// 获取 流程节点
		String json = this.getPara("json");
		// 解析 数组
		JSONArray ja = JSON.parseArray(json);
		
		int len = ja.size();
		List<ProcessesNode> handleL = new ArrayList<>(len);
		// 循环添加节点
		for (int i = 0; i < len; i ++) {
			// 获取 对象
			JSONObject jo = ja.getJSONObject(i);
			// 声明 节点
			ProcessesNode node = new ProcessesNode();
			
			// 首尾节点判断
			boolean isFirst = i == 0;
			boolean isLast = i == (len - 1);
			
			if (isFirst) {
				node.set("first", 1);
			}
			
			if (isLast) {
				node.set("last", 1);
			}
			
			node
			// 设置 流程id
			.set("processes_id", pro.get("id"))
			.set("manager_id", jo.getString("manager_id"))
			.set("manager_name", jo.getString("manager_name"))
			.save();
			
			if (!isFirst) {
				ProcessesNode prev = handleL.get(i - 1);
				// 更新 上一节点 的 下一节点
				prev.set("next_node_id", node.get("id")).update();
				// 更新 本节点 的 上一节点
				node.set("prev_node_id", prev.get("id")).update();
			}
			
			handleL.add(node);
		}
		
		this.renderJson("{'success': true, 'message': '保存成功'}");
	}
	
	public void handleManJson(){
		String id = this.getPara("id");
		// 查询 节点
		List<ProcessesNode> nodes = ProcessesNode.me.find("select i.manager_name from processes_node as i where i.processes_id = ? order by i.id asc ", id);
	
		JSONArray a = new JSONArray();
		
		for (ProcessesNode n : nodes) {
			JSONObject o = new JSONObject();
			o.put("name", n.get("manager_name"));
			a.add(o);
		}
		
		// 返回 json
		this.renderJson(a.toJSONString());
	}
	
}
