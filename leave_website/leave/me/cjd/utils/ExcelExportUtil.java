package me.cjd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelExportUtil {
	
	private HSSFWorkbook workbook = null;
	@SuppressWarnings("unused")
	private HSSFSheet sheet = null;

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	// public HSSFSheet getSheet() {
	// return sheet;
	// }

	// public void setSheet(HSSFSheet sheet) {
	// this.sheet = sheet;
	// }

	public ExcelExportUtil(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	public ExcelExportUtil(HSSFWorkbook workbook, HSSFSheet sheet) {
		super();
		this.workbook = workbook;
		this.sheet = sheet;
	}

	/**
	 * 创建通用的Excel空白行信息
	 * 
	 * @param workbook
	 *            如果为空 则没有样式
	 * @param sheet
	 *            (创建sheet)
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param rowHeight
	 *            报表的单行行高
	 * @param colNum
	 *            报表的总列数 (合并)
	 */
	public void createExcelRow(HSSFWorkbook workbook, HSSFSheet sheet, int rowNO, int rowHeight, int colNum) {
		createExcelRow(workbook, sheet, rowNO, -1, colNum, null, -1, null, null);
	}

	/**
	 * 创建通用的Excel带标题行信息
	 * 
	 * @param workbook
	 *            如果为空 则没有样式
	 * @param sheet
	 *            (创建sheet)
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param rowHeight
	 *            报表的单行行高
	 * @param colNum
	 *            报表的总列数 (合并)
	 * @param fontCaption
	 *            报表行中显示的字符
	 */
	public void createExcelRow(HSSFWorkbook workbook, HSSFSheet sheet, int rowNO, int rowHeight, int colNum,
			String fontCaption) {
		createExcelRow(workbook, sheet, rowNO, -1, colNum, fontCaption, -1, null, null);
	}

	/**
	 * 创建通用的Excel行信息
	 * 
	 * @param workbook
	 *            如果为空 则没有样式
	 * @param sheet
	 *            (创建sheet)
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param rowHeight
	 *            报表的单行行高
	 * @param colNum
	 *            报表的总列数 (合并)
	 * @param fontCaption
	 *            报表行中显示的字符
	 * @param fontSize
	 *            字体的大小 (字体大小 默认 200)
	 * @param fontWeight
	 *            报表表头显示的字符
	 * @param align
	 *            字体水平位置 (center中间 right右 left左)
	 * @param colNum
	 *            报表的列数
	 */
	@SuppressWarnings("deprecation")
	public void createExcelRow(HSSFWorkbook workbook, HSSFSheet sheet, int rowNO, int rowHeight, int colNum,
			String fontCaption, int fontSize, String fontWeight, String align) {
		if (colNum < 0) {
			colNum = 100;
		}

		HSSFRow row = sheet.createRow(rowNO); // 创建第一行
		row.setHeight((short) (rowHeight < 1 ? 300 : rowHeight)); // 设置行高

		HSSFCell cell = row.createCell(0);// 设置第一行
		cell.setCellType(HSSFCell.ENCODING_UTF_16); // 定义单元格为字符串类型
		cell.setCellValue(new HSSFRichTextString(fontCaption));

		sheet.addMergedRegion(new Region(rowNO, (short) 0, rowNO, (short) (colNum - 1))); // 指定合并区域

		HSSFCellStyle cellStyle = createCellFontStyle(workbook, fontSize, fontWeight, align); // 设定样式
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
	}

	/**
	 * 设置报表列头
	 * 
	 * @param sheet
	 *            (创建sheet)
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param rowHeight
	 *            报表的单行行高
	 * @param columnHeader
	 *            报表行中显示的字符
	 */
	public void createColumnHeader(HSSFSheet sheet, int rowNO, int rowHeight, String[] columnHeader) {
		createColumnHeader(sheet, rowNO, rowHeight, columnHeader, -1, null, null);
	}

	/**
	 * 设置报表列头
	 * 
	 * @param sheet
	 *            (创建sheet)
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param rowHeight
	 *            报表的单行行高
	 * @param columnHeader
	 *            报表行中显示的字符
	 * @param fontSize
	 *            字体的大小 (字体大小 默认 200)
	 */
	public void createColumnHeader(HSSFSheet sheet, int rowNO, int rowHeight, String[] columnHeader, int fontSize) {
		createColumnHeader(sheet, rowNO, rowHeight, columnHeader, fontSize, null, null);
	}

	/**
	 * 设置报表列头
	 * 
	 * @param sheet
	 *            (创建sheet)
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param rowHeight
	 *            报表的单行行高
	 * @param columnHeader
	 *            报表行中显示的字符
	 * @param fontSize
	 *            字体的大小 (字体大小 默认 200)
	 * @param fontWeight
	 *            报表表头显示的字符
	 * @param align
	 *            字体水平位置 (center中间 right右 left左)
	 */
	public void createColumnHeader(HSSFSheet sheet, int rowNO, int rowHeight, String[] columnHeader, int fontSize,
			String fontWeight, String align) {
		if (columnHeader == null || columnHeader.length < 1) {
			return;
		}
		HSSFRow row = sheet.createRow(rowNO);
		row.setHeight((short) rowHeight);

		HSSFCellStyle cellStyle = createCellFontStyle(workbook, fontSize, fontWeight, align);
		if (cellStyle != null) {
			// 设置单元格背景色
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		}

		HSSFCell cell = null;
		for (int i = 0; i < columnHeader.length; i++) {
			sheet.setColumnWidth(i, 20 * 256); // 设置列宽，20个字符宽度。宽度参数为1/256，故乘以256
			cell = row.createCell(i);
			cell.setCellType(HSSFCell.ENCODING_UTF_16);
			if (cellStyle != null) {
				cell.setCellStyle(cellStyle);
			}
			cell.setCellValue(new HSSFRichTextString(columnHeader[i]));
		}
	}

	/**
	 * 创建数据行
	 * 
	 * @param sheet
	 *            (创建sheet)
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param rowHeight
	 *            报表的单行行高
	 * @param columnData
	 *            报表行中显示的数据
	 * @param maxValue
	 *            Excel显示的最大上限
	 */
	public HSSFSheet createColumnData(HSSFSheet sheet, int rowNO, String[][] columnData, int maxValue) {
		maxValue = (maxValue < 1 || maxValue > 65535) ? 65535 : maxValue;
		int currRowNO = rowNO;
		for (int numNO = currRowNO; numNO < columnData.length + currRowNO; numNO++) {
			if (numNO % maxValue == 0) {
				sheet = workbook.createSheet();
				rowNO = 0;
			}
			createColumnDataDesc(sheet, numNO, rowNO, currRowNO, -1, columnData);
			rowNO++;
		}
		return sheet;
	}

	/**
	 * 创建数据行
	 * 
	 * @param sheet
	 *            (创建sheet)
	 * @param numNO
	 *            序列号
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param currRowNO
	 *            初始行号
	 * @param rowHeight
	 *            报表的单行行高
	 * @param columnData
	 *            报表行中显示的数据
	 */
	private void createColumnDataDesc(HSSFSheet sheet, int numNO, int rowNO, int currRowNO, int rowHeight,
			String[][] columnData) {
		createColumnDataDesc(sheet, numNO, rowNO, currRowNO, rowHeight, columnData, -1, null, null);
	}

	/**
	 * 创建数据行
	 * 
	 * @param sheet
	 *            (创建sheet)
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param rowHeight
	 *            报表的单行行高
	 * @param columnData
	 *            报表行中显示的数据
	 * @param fontSize
	 *            字体大小 默认 200
	 * @param fontWeight
	 *            字体粗细 ( 值为bold 为加粗)
	 * @param align
	 *            字体水平位置 (center中间 right右 left左)
	 * @param maxValue
	 *            Excel显示的最大上限
	 */
	public HSSFSheet createColumnData(HSSFSheet sheet, int rowNO, int rowHeight, String[][] columnData, int fontSize,
			String fontWeight, String align, int maxValue) {
		maxValue = (maxValue < 1 || maxValue > 65535) ? 65535 : maxValue;
		int currRowNO = rowNO;
		for (int numNO = currRowNO; numNO < columnData.length + currRowNO; numNO++) {
			if (numNO % maxValue == 0) {
				sheet = workbook.createSheet();
				rowNO = 0;
			}
			createColumnDataDesc(sheet, numNO, rowNO, currRowNO, rowHeight, columnData, fontSize, fontWeight, align);
			rowNO++;
		}
		return sheet;
	}

	/**
	 * 创建数据行
	 * 
	 * @param sheet
	 *            (创建sheet)
	 * @param numNO
	 *            序列号
	 * @param rowNO
	 *            报表的单行行号(创建第几行)
	 * @param currRowNO
	 *            初始行号
	 * @param rowHeight
	 *            报表的单行行高
	 * @param columnData
	 *            报表行中显示的数据
	 * @param fontSize
	 *            字体的大小 (字体大小 默认 200)
	 * @param fontWeight
	 *            报表表头显示的字符
	 * @param align
	 *            字体水平位置 (center中间 right右 left左)
	 */
	private void createColumnDataDesc(HSSFSheet sheet, int numNO, int rowNO, int currRowNO, int rowHeight,
			String[][] columnData, int fontSize, String fontWeight, String align) {
		if (columnData == null || columnData.length < 1) {
			// return ;
		}
		HSSFRow row = sheet.createRow(rowNO);
		row.setHeight((short) rowHeight);

		HSSFCellStyle cellStyle = null;// createCellFontStyle(workbook,
										// fontSize, fontWeight, align);
		if (cellStyle != null) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 指定单元格居中对齐
		} else {
			cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION); // 指定单元格居中对齐
		}
		HSSFCell cell = null;
		for (int i = 0; i < columnData[numNO - currRowNO].length; i++) {
			sheet.setColumnWidth(i, 20 * 256); // 设置列宽，20个字符宽度。宽度参数为1/256，故乘以256
			cell = row.createCell(i);
			cell.setCellType(HSSFCell.ENCODING_UTF_16);
			if (cellStyle != null) {
				cell.setCellStyle(cellStyle);
			}
			cell.setCellValue(new HSSFRichTextString(columnData[numNO - currRowNO][i]));
		}
	}

	/**
	 * 创建内容单元格
	 * 
	 * @param workbook
	 *            HSSFWorkbook
	 * @param row
	 *            HSSFRow
	 * @param columnNumber
	 *            short型的列索引
	 * @param alignType
	 *            对齐方式 (默认居中对齐,如果 alignType=true 则左对齐)
	 * @param value
	 *            列值
	 */
	@SuppressWarnings("deprecation")
	public void cteateDataCell(HSSFWorkbook workbook, HSSFRow row, int columnNumber, boolean alignType, String value) {
		HSSFCell cell = row.createCell(((short) columnNumber));
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(value));

		HSSFCellStyle cellstyle = workbook.createCellStyle();
		short align = HSSFCellStyle.ALIGN_CENTER_SELECTION;
		if (alignType) {
			align = HSSFCellStyle.ALIGN_LEFT;
		}
		cellstyle.setAlignment(align); // 指定单元格居中对齐
		cell.setCellStyle(cellstyle);
	}

	/**
	 * 创建通用的Excel最后一行的信息 (创建合计行 (最后一行))
	 * 
	 * @param workbook
	 *            如果为空 则没有样式
	 * @param sheet
	 * @param colNum
	 *            报表的总列数 (合并)
	 * @param fontCaption
	 *            报表行中显示的字符
	 * @param fontSize
	 *            字体的大小 (字体大小 默认 200)
	 * @param fontWeight
	 *            报表表头显示的字符
	 * @param align
	 *            字体水平位置 (center中间 right右 left左)
	 * @param colNum
	 *            报表的列数 (需要合并到的列索引)
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void createSummaryRow(HSSFWorkbook workbook, HSSFSheet sheet, int colNum, String fontCaption, int fontSize,
			String fontWeight, String align) {

		HSSFCellStyle cellStyle = createCellFontStyle(workbook, fontSize, fontWeight, align);

		HSSFRow lastRow = sheet.createRow((short) (sheet.getLastRowNum() + 1));
		HSSFCell sumCell = lastRow.createCell(0);

		sumCell.setCellValue(new HSSFRichTextString(fontCaption));
		if (cellStyle != null) {
			sumCell.setCellStyle(cellStyle);
		}
		sheet.addMergedRegion(
				new Region(sheet.getLastRowNum(), (short) 0, sheet.getLastRowNum(), (short) (colNum - 1)));// 指定合并区域
	}

	/**
	 * 设置字体样式 (字体为宋体 ，上下居中对齐，可设置左右对齐，字体粗细，字体大小 )
	 * 
	 * @param workbook
	 *            如果为空 则没有样式
	 * @param fontSize
	 *            字体大小 默认 200
	 * @param fontWeight
	 *            字体粗细 ( 值为bold 为加粗)
	 * @param align
	 *            字体水平位置 (center中间 right右 left左)
	 */
	public HSSFCellStyle createCellFontStyle(HSSFWorkbook workbook, int fontSize, String fontWeight, String align) {
		if (workbook == null) {
			return null;
		}

		HSSFCellStyle cellStyle = workbook.createCellStyle();

		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		if (align != null && align.equalsIgnoreCase("left")) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 指定单元格居中对齐
		}
		if (align != null && align.equalsIgnoreCase("right")) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 指定单元格居中对齐
		}

		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
		cellStyle.setWrapText(true);// 指定单元格自动换行

		// 单元格字体
		HSSFFont font = workbook.createFont();
		if (fontWeight != null && fontWeight.equalsIgnoreCase("normal")) {
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		} else {
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		}

		font.setFontName("宋体");
		font.setFontHeight((short) (fontSize < 1 ? 200 : fontSize));
		cellStyle.setFont(font);

		// 设置字体
		// HSSFFont font = workbook.createFont();
		// font.setFontHeightInPoints((short) 20); //字体高度
		// font.setColor(HSSFFont.COLOR_RED); //字体颜色
		// font.setFontName("黑体"); //字体
		// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //宽度
		// font.setItalic(true); //是否使用斜体
		// font.setStrikeout(true); //是否使用划线
		// // 添加单元格注释
		// // 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器.
		// HSSFPatriarch patr = sheet.createDrawingPatriarch();
		// // 定义注释的大小和位置,详见文档
		// HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0,
		// 0, 0, (short)4, 2, (short) 6, 5));
		// // 设置注释内容
		// comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// // 设置注释作者. 当鼠标移动到单元格上是可以在状态栏中看到该内容.
		// comment.setAuthor("Xuys.");

		return cellStyle;
	}

	/**
	 * 导出EXCEL文件
	 * 
	 * @param fileName
	 *            文件名称 测试程序
	 * 
	 *            // private static HSSFWorkbook workbook = new HSSFWorkbook();
	 *            // private static HSSFSheet sheet = workbook.createSheet();
	 *            HSSFWorkbook workbook = new HSSFWorkbook(); HSSFSheet sheet =
	 *            workbook.createSheet(); ExportExcelUtil eeu = new
	 *            ExportExcelUtil(workbook, sheet);
	 * 
	 *            String[] strArr = new String[] { "序号", "姓名", "性 别", "出生年月",
	 *            "民族", "籍贯", "备注" }; int colNum = strArr.length;
	 * 
	 *            int rowNO = 0; //1. titleCaption eeu.createExcelRow(workbook,
	 *            sheet, rowNO, -1, colNum, "统计报表"); // , 250, "bold", "center"
	 *            //2. rowNO++; eeu.createExcelRow(workbook, sheet, rowNO, 200,
	 *            colNum, " 制 表 人: 赵小明       制 表 日 期: " + new
	 *            java.text.SimpleDateFormat("yyyy-MM-dd").format(new
	 *            java.util.Date()), 180, "normal", "right");
	 *            //3.columnTitleHeader rowNO++; eeu.createColumnHeader(sheet,
	 *            rowNO, 300, strArr);
	 * 
	 *            //4.数据行 循环创建中间的单元格的各项的值 rowNO++; String[][] columnData = new
	 *            String[][]{{ "1", "zhangsan", "男", "1985-10-06 21:00:00",
	 *            "汉族", "西安", "学生" }, { "2", "猪猪", "女", "出生年月", "民族", "籍贯", "备注"
	 *            }, { "3", "明明", "男", "1980-07-08", "汉族", "西安", "学生" }, { "4",
	 *            "光光", "女", "1985-06-30", "汉族", "西安", "学生" }, { "5", "先民", "男",
	 *            "1987-06-06", "汉族", "西安", "学生" }, { "6", "数据", "女",
	 *            "1985-04-06", "汉族", "西安", "学生" }, { "7", "历史", "女",
	 *            "1985-06-06", "汉族", "西安", "学生" }, { "8", "妩媚", "女",
	 *            "1990-10-06", "汉族", "西安", "学生" }, { "9", "李斯", "男",
	 *            "1985-06-20", "汉族", "西安", "学生" }, { "10", "犀利", "女",
	 *            "1985-06-06", "汉族", "西安", "学生" }, { "11", "绅士", "男",
	 *            "1985-01-06", "汉族", "西安", "学生" }, { "12", "先知", "女",
	 *            "1984-06-06", "汉族", "西安", "学生" }, { "13", "精明", "男",
	 *            "1985-06-12", "汉族", "西安", "学生" }, { "14", "科技", "男",
	 *            "1972-02-03", "汉族", "西安", "学生" }, { "15", "软件", "女",
	 *            "1985-02-06", "汉族", "西安", "学生" }, { "16", "世道", "男",
	 *            "1999-03-06", "汉族", "西安", "学生" }, { "17", "明了", "女",
	 *            "1985-06-06", "汉族", "西安", "学生" }, { "18", "小明", "男",
	 *            "1984-06-09", "汉族", "西安", "学生" }, { "19", "高娟", "女",
	 *            "1985-03-06", "汉族", "西安", "学生" }, { "20", "小莉", "女",
	 *            "1987-12-25", "汉族", "西安", "学生" }} ; sheet =
	 *            eeu.createColumnData(sheet, rowNO, columnData, 7);
	 *            eeu.createSummaryRow(workbook, sheet, colNum, "合计：" +
	 *            columnData.length, 180, "normal", "right");
	 *            eeu.exportExcel("f://Test2011-07-29//test.xls");
	 */
	public void exportExcel(String fileName) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(fileName));
			workbook.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 利用模板导出Excel
	 * 
	 * @param inputFile
	 *            输入模板文件路径
	 * @param outputFile
	 *            输入文件存放于服务器路径
	 * @param dataList
	 *            待导出数据
	 * @throws Exception
	 * @roseuid:
	 */
	@SuppressWarnings("deprecation")
	public void exportExcelFile(String inputFileName, String outputFileName, List<?> dataList) throws Exception {
		// 用模板文件构造poi
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(inputFileName));
		// 创建模板工作表
		HSSFWorkbook templatewb = new HSSFWorkbook(fs);
		// 直接取模板第一个sheet对象
		HSSFSheet templateSheet = templatewb.getSheetAt(1);
		if (dataList.size() % 65535 == 0) {
			templateSheet = templatewb.createSheet();
		}
		// 得到模板的第一个sheet的第一行对象 为了得到模板样式
		HSSFRow templateRow = templateSheet.getRow(0);

		// HSSFSheet timplateSheet = templatewb.getSheetAt(1);
		// 取得Excel文件的总列数
		int columns = templateSheet.getRow((short) 0).getPhysicalNumberOfCells();
		System.out.println("columns   is   :   " + columns);
		// 创建样式数组
		HSSFCellStyle styleArray[] = new HSSFCellStyle[columns];

		// 一次性创建所有列的样式放在数组里
		for (int s = 0; s < columns; s++) {
			// 得到数组实例
			styleArray[s] = templatewb.createCellStyle();
		}
		// 循环对每一个单元格进行赋值
		// 定位行
		for (int rowId = 1; rowId < dataList.size(); rowId++) {
			// 依次取第rowId行数据 每一个数据是valueList
			List<?> valueList = (List<?>) dataList.get(rowId - 1);
			// 定位列
			for (int columnId = 0; columnId < columns; columnId++) {
				// 依次取出对应与colunmId列的值
				// 每一个单元格的值
				String dataValue = (String) valueList.get(columnId);
				// 取出colunmId列的的style
				// 模板每一列的样式
				HSSFCellStyle style = styleArray[columnId];
				// 取模板第colunmId列的单元格对象
				// 模板单元格对象
				HSSFCell templateCell = templateRow.getCell((short) columnId);
				// 创建一个新的rowId行 行对象
				// 新建的行对象
				HSSFRow hssfRow = templateSheet.createRow(rowId);
				// 创建新的rowId行 columnId列 单元格对象
				// 新建的单元格对象
				HSSFCell cell = hssfRow.createCell((short) columnId);
				// 如果对应的模板单元格 样式为非锁定
				if (templateCell.getCellStyle().getLocked() == false) {
					// 设置此列style为非锁定
					style.setLocked(false);
					// 设置到新的单元格上
					cell.setCellStyle(style);
				}
				// 否则样式为锁定
				else {
					// 设置此列style为锁定
					style.setLocked(true);
					// 设置到新单元格上
					cell.setCellStyle(style);
				}
				// 设置编码
				// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				// Debug.println( "dataValue : " + dataValue);
				// 设置值 统一为String
				cell.setCellValue(dataValue);
			}
		}
		// 设置输入流
		FileOutputStream fOut = new FileOutputStream(outputFileName);
		// 将模板的内容写到输出文件上
		templatewb.write(fOut);
		fOut.flush();

		// 操作结束，关闭文件
		fOut.close();

	}

	public static void main(String[] args) {
		// private static HSSFWorkbook workbook = new HSSFWorkbook();
		// private static HSSFSheet sheet = workbook.createSheet();
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		ExcelExportUtil eeu = new ExcelExportUtil(workbook, sheet);

		String[] strArr = new String[] { "序号", "姓名", "性 别", "出生年月", "民族", "籍贯", "备注" };
		int colNum = strArr.length;

		int rowNO = 0;
		// 1. titleCaption
		eeu.createExcelRow(workbook, sheet, rowNO, -1, colNum, "统计报表"); // ,
																		// 250,
																		// "bold",
																		// "center"
		// 2.
		rowNO++;
		eeu.createExcelRow(workbook, sheet, rowNO, 200, colNum,
				" 制 表 人: 赵小明       制 表 日 期: "
						+ new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()),
				180, "normal", "right");
		// 3.columnTitleHeader
		rowNO++;
		eeu.createColumnHeader(sheet, rowNO, 300, strArr);

		// 4.数据行 循环创建中间的单元格的各项的值
		rowNO++;
		String[][] columnData = new String[][] { 
			{ "1", "zhangsan", "男", "1985-10-06 21:00:00", "汉族", "西安", "学生" },
			{ "2", "猪猪", "女", "出生年月", "民族", "籍贯", "备注" }, 
			{ "3", "明明", "男", "1980-07-08", "汉族", "西安", "学生" },
			{ "4", "光光", "女", "1985-06-30", "汉族", "西安", "学生" }, 
			{ "5", "先民", "男", "1987-06-06", "汉族", "西安", "学生" },
			{ "6", "数据", "女", "1985-04-06", "汉族", "西安", "学生" }, 
			{ "7", "历史", "女", "1985-06-06", "汉族", "西安", "学生" },
			{ "8", "妩媚", "女", "1990-10-06", "汉族", "西安", "学生" }, 
			{ "9", "李斯", "男", "1985-06-20", "汉族", "西安", "学生" },
			{ "10", "犀利", "女", "1985-06-06", "汉族", "西安", "学生" },
			{ "11", "绅士", "男", "1985-01-06", "汉族", "西安", "学生" },
			{ "12", "先知", "女", "1984-06-06", "汉族", "西安", "学生" },
			{ "13", "精明", "男", "1985-06-12", "汉族", "西安", "学生" },
			{ "14", "科技", "男", "1972-02-03", "汉族", "西安", "学生" },
			{ "15", "软件", "女", "1985-02-06", "汉族", "西安", "学生" },
			{ "16", "世道", "男", "1999-03-06", "汉族", "西安", "学生" },
			{ "17", "明了", "女", "1985-06-06", "汉族", "西安", "学生" },
			{ "18", "小明", "男", "1984-06-09", "汉族", "西安", "学生" },
			{ "19", "高娟", "女", "1985-03-06", "汉族", "西安", "学生" },
			{ "20", "小莉", "女", "1987-12-25", "汉族", "西安", "学生" } 
		};
		sheet = eeu.createColumnData(sheet, rowNO, columnData, 7);
		eeu.createSummaryRow(workbook, sheet, colNum, "合计：" + columnData.length, 180, "normal", "right");
		eeu.exportExcel("f://Test2011-07-29//test.xls");
		/*
		 * 在用java 编写生成报表时发现了个问题，将行，列隐藏，将列隐藏很简单用
		 * this.sheet.setColumnHidden((short)12, true);将第13列隐藏注意excel的第一列用0表示
		 * 
		 * 隐藏行：
		 * 
		 * HSSFRow row = sheet.getRow(8); row.setZeroHeight(true);
		 * 
		 * 将第8行隐藏就是将他的高度设为0也等同为隐藏
		 */
	}

}