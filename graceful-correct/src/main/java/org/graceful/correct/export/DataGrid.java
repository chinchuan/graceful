package org.graceful.correct.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface DataGrid {
	
	public int StartDataIndex = 1;  //数据开始行数
	
	/**
	 * 获取鱼单元格数据内容
	 * @param stream
	 * @param sheet
	 * @param nDateCell
	 * @return
	 * @throws IOException
	 */
	List<Map<Integer, Object>> getExcelInfo(InputStream stream,int sheet, short... nDateCell) throws IOException;
	
	/**
	 * 导出excel 表格
	 * @param data
	 * @param out
	 * @param sheetName
	 */
	void export(ExportData data, OutputStream out,String sheetName);
	
	/**
	 * 导出excel 表格
	 * @param data
	 * @param workbook   
	 * @param fileName  文件名称
	 */
	void export(ExportData data,HSSFWorkbook workbook,String sheetName);
}
