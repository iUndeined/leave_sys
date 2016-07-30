package me.cjd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.jfinal.log.Logger;

public class ExcelUtil {
	
	private final static Logger LOG = Logger.getLogger(ExcelUtil.class);
	
	public static boolean isExcel(File file){
		return FileUtil.hasExtension(file, "xls") || 
			   FileUtil.hasExtension(file, "xlsx");
	}
	
	public static Workbook createWorkBook(File file){
		try {
			// 获取 扩展名
			String extension = FileUtil.getExtension(file);
			// 实例 流
			InputStream is = new FileInputStream(file);
			// 生成 excel类
			return extension.toLowerCase().equals("xls") ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
		} catch (Exception e) {
			throw new RuntimeException("读取excel表发生错误.", e);
		}
	}
	
	public static Cell getCell(Sheet sheet, int rowIdx, int cellIdx){
		// 获取 行
		Row row = sheet.getRow(rowIdx);
		// 获取 单元格
		Cell cell = row.getCell(cellIdx);
		// 返回 获取到的单元格
		return cell;
	}
	
	public static Date getCellDate(Sheet sheet, int rowIdx, int cellIdx){
		Cell cell = getCell(sheet, rowIdx, cellIdx);
		
		if (cell == null) {
			return null;
		}
		
		return cell.getDateCellValue();
	}
	
	public final static String getStr(Sheet sheet, int rowIdx, int cellIdx){
		Cell cell = getCell(sheet, rowIdx, cellIdx);
		
		if (cell == null) {
			return null;
		}
		
		try {
			int cellType = cell.getCellType();
			switch (cellType) {
				case Cell.CELL_TYPE_NUMERIC:
					Number number = cell.getNumericCellValue();
					if ((number.doubleValue() % 1) > 0) {
						return String.valueOf(number.longValue());
					}
					return String.valueOf(number.intValue());
				default:
					return cell.getStringCellValue();
			}
		} catch (Exception e) {
			LOG.error("获取字符型单元格出错，第" + rowIdx + "行；第" + cellIdx + "列。", e);
			return null;
		}
	}
	
	public final static Integer getInt(Sheet sheet, int rowIdx, int cellIdx){
		Cell cell = getCell(sheet, rowIdx, cellIdx);
		
		if (cell == null) {
			return null;
		}
		
		try {
			Number number = cell.getNumericCellValue();
			return number == null ? null : number.intValue();
		} catch (Exception e) {
			LOG.error("获取整型单元格出错，第" + rowIdx + "行；第" + cellIdx + "列。", e);
			return null;
		}
	}
	
	public final static double getDouble(Sheet sheet, int rowIdx, int cellIdx){
		Cell cell = getCell(sheet, rowIdx, cellIdx);
		
		if (cell == null) {
			return 0;
		}
		
		try {
			return new BigDecimal(cell.getNumericCellValue()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception e) {
			LOG.error("获取小数型单元格出错，第" + rowIdx + "行；第" + cellIdx + "列。", e);
			return 0;
		}
	}
	
	public static void setCellStr(Sheet sheet, String text, int rowIdx, int cellIdx){
		Row row = sheet.getRow(rowIdx);
		Cell cell = row.getCell(cellIdx);
		
		if (cell == null) {
			cell = row.createCell(cellIdx);
		}
		
		cell.setCellValue(text);
	}
	
	public static int getRowCount(Sheet sheet, int startIdx){
		int lastRowNum = sheet.getLastRowNum();
		if (lastRowNum > startIdx) {
			return lastRowNum - startIdx;
		}
		return 0;
	}
	
	public static void save(Workbook workbook, File file){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			workbook.write(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
