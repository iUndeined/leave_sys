package me.cjd.utils;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.core.Controller;

import me.cjd.pojo.Leave;

public class ExcelWriter {
	
	// 单例
	public final static ExcelWriter me = new ExcelWriter();
	
	public String getProjectPath(Controller c, String... paths){
		// 获取 项目根目录
		String root = c.getRequest().getServletContext().getRealPath("").replaceAll("\\\\", "/");
		if (!root.endsWith("/")) {
			root += "/";
		}
		for (String i : paths) {
			root += i + "/";
		}
		return root.replaceAll("/$", "");
	}
	
	public void test(Controller c){
		c.renderText("haha");
	}
	
	public void controller(Controller c){
		// 获取 假单们
		String leaves = c.getPara("leaves");
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		ExcelExportUtil eeu = new ExcelExportUtil(workbook, sheet);
		
		// 转换 格式
		leaves = leaves
			.replaceAll("\"", "'")
			.replaceAll("\\[(.+)\\]", "($1)");
		
		// 查询 假单并创建成工具可识别的数据
		List<Leave> leaveL = Leave.me.find("SELECT * FROM `leave` AS i WHERE i.id IN " + leaves);
		
		if (leaveL == null ||
			leaveL.isEmpty()) {
			c.renderText("没有找到假单数据！！");
			return;
		}
		
		List<String[]> datas = new ArrayList<>(leaveL == null ? 0 : leaveL.size());
		
		for (Leave i : leaveL) {
			String stuts = "无";
			Timestamp createdDate = i.getCreatedDate();
			
			switch (i.getState()) {
				case 0:
					stuts = "在途";
					break;
				case 1:
					stuts = "批结";
					break;
				case 2:
					stuts = "否决";
					break;
				case 3:
					stuts = "作废";
					break;
			}
			
			String[] data = {
				String.valueOf(i.getId()),
				String.valueOf(i.getEmployNo()),
				String.valueOf(createdDate == null ? "无" : DateUtil.toStr(createdDate)),
				String.valueOf(i.getApplyMan()),
				String.valueOf(DateUtil.toStr(i.getStartDate())) + " to " + DateUtil.toStr(i.getEndDate()),
				String.valueOf(i.getType()),
				String.valueOf(i.getApplyDays()),
				String.valueOf(stuts)
			};
			// 添入 数据
			datas.add(data);
		}
		
		String[] heads = new String[] {"单号", "工号", "填报时间", "申请人", "申请时间", "假单类型", "请假天数", "假单状态"};
		// 创建 表头
		eeu.createColumnHeader(sheet, 0, 300, heads);
		// 获取 数据行数
		int dataRow = datas.size();
		// 创建 工作薄
		sheet = eeu.createColumnData(sheet, 1, datas.toArray(new String[dataRow][]), 869535088);
		// 导出
		String path = this.getProjectPath(c, "outtemp", System.currentTimeMillis() + ".xls");
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		// 导出 操作
		eeu.exportExcel(path);
		// 弹出下载
		c.renderFile(file);
	}
	
}
