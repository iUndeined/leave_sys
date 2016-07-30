package me.cjd.utils;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.cjd.pojo.Balance;
import me.cjd.pojo.Employee;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelReader {
	
	/**
	 * 将数字字串四舍五入保留两位小数
	 * @param numStr 数字字串
	 * @return 
	 */
	public final static double db(String numStr){
		if (StringUtil.isEmpty(numStr)) {
			return 0;
		}
		try {
			return new BigDecimal(Double.valueOf(numStr)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public final static List<Employee> read(File excelFile){
		
		// 如果不是excel滚粗
		if (!ExcelUtil.isExcel(excelFile)) {
			return null;
		}
		
		List<Employee> resultL = new ArrayList<>();
		// 读取 excel
		Workbook book = ExcelUtil.createWorkBook(excelFile);
		// 获取 第一个工作薄
		Sheet sheet = book.getSheetAt(0);
		// 获取 最后一行是第几行
		int lastRowNum = sheet.getLastRowNum();
		// 循环读取，开始
		for (int i = 1; i <= lastRowNum; i ++) {
			// 声明 员工实体
			Employee empl = new Employee();
			// 获取 工号
			String emplNo = ExcelUtil.getStr(sheet, i, 0);
			// 获取 员工姓名
			String name = ExcelUtil.getStr(sheet, i, 1);
			// 获取 登录密码
			String password = ExcelUtil.getStr(sheet, i, 2);
			// 获取 部门
			String dept = ExcelUtil.getStr(sheet, i, 3);
			// 获取 职位
			String desig = ExcelUtil.getStr(sheet, i, 4);
			
			empl
			// 设置 工号
			.set("employ_no", emplNo)
			// 设置 姓名
			.set("name", name)
			// 设置 密码
			.set("password", password)
			// 设置 部门
			.set("dept", dept)
			// 设置 职位
			.set("desig", desig);
			
			// 添入列表
			resultL.add(empl);
		}
		
		return resultL;
	}
	
	public final static List<Balance> readBalance(File excelFile){
		
		// 如果不是excel滚粗
		if (!ExcelUtil.isExcel(excelFile)) {
			return null;
		}
		
		List<Balance> resultL = new ArrayList<>();
		// 读取 excel
		Workbook book = ExcelUtil.createWorkBook(excelFile);
		// 获取 第一个工作薄
		Sheet sheet = book.getSheetAt(0);
		// 获取 最后一行是第几行
		int lastRowNum = sheet.getLastRowNum();
		// 循环读取，开始
		for (int i = 1; i <= lastRowNum; i ++) {
			// 获取 有几行
			int numberOfCells = sheet.getRow(i).getPhysicalNumberOfCells();
			
			// 声明 人力资源信息
			Balance balance = new Balance();
			
			String emplNo = ExcelUtil.getStr(sheet, i, 0);
			
			if (numberOfCells > 3) {
				String emplName = ExcelUtil.getStr(sheet, i, 1);
				// 入职时间
				Date joinDate = ExcelUtil.getCellDate(sheet, i, 2);
				// 入职前工龄
				Double afterWork = ExcelUtil.getDouble(sheet, i, 3);
				// 总工龄
				Double totalWork = ExcelUtil.getDouble(sheet, i, 4);
				Double lastYearRestAl = ExcelUtil.getDouble(sheet, i, 5);
				Double lastYearRestLil = ExcelUtil.getDouble(sheet, i, 6);
				Double currYearAlQua = ExcelUtil.getDouble(sheet, i, 7);
				Double currEndAl = ExcelUtil.getDouble(sheet, i, 8);
				Double currEndLil = ExcelUtil.getDouble(sheet, i, 9);
				Double currYearApplyAl = ExcelUtil.getDouble(sheet, i, 10);
				Double currYearApplyLil = ExcelUtil.getDouble(sheet, i, 11);
				Double currYearAddAl = ExcelUtil.getDouble(sheet, i, 12);
				Double currYearAddLil = ExcelUtil.getDouble(sheet, i, 13);
				
				balance
				.setEmplNo(emplNo)
				.setEmplName(emplName)
				.setJoinDate(new Timestamp(joinDate.getTime()))
				.setAfterWork(afterWork)
				.setTotalWork(totalWork)
				.setLastYearRestAl(lastYearRestAl)
				.setLastYearRestLil(lastYearRestLil)
				.setCurrYearAlQua(currYearAlQua)
				.setCurrEndAl(currEndAl)
				.setCurrEndLil(currEndLil)
				.setCurrYearApplyAl(currYearApplyAl)
				.setCurrYearApplyLil(currYearApplyLil)
				.setCurrYearAddAl(currYearAddAl)
				.setCurrYearAddLil(currYearAddLil)
				.setCurrRestAl((currEndAl + currYearAddAl) - currYearApplyAl)
				.setCurrRestLil((currEndLil + currYearAddLil) - currYearApplyLil);
			} else {
				Double currYearAddAl = ExcelUtil.getDouble(sheet, i, 1);
				Double currYearAddLil = ExcelUtil.getDouble(sheet, i, 2);
				balance
				.setEmplNo(emplNo)
				.setCurrYearAddAl(currYearAddAl)
				.setCurrYearAddLil(currYearAddLil);
			}
			
			resultL.add(balance);
		}
		
		return resultL;
	}
	
}
