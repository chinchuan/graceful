package org.graceful.correct.export;

import org.graceful.correct.export.excel.PoiExcel;

 

public class ExportHelperFactory {
	private static ExportHelperFactory instance;
	
	private ExportHelperFactory(){}
	
	public static ExportHelperFactory getInstance(){
		if(instance==null)
			instance = new ExportHelperFactory();
		return instance;
	}
	
	public DataGrid getExcelHelper(){
		return getExportHelper(ExportFormat.EXCEL);
	}
	
	public DataGrid getExportHelper(ExportFormat format){
		switch(format){
		case EXCEL:
			return new PoiExcel();
		default:
			return new PoiExcel();
		}
	}
	
	/**
	 * 导出格式
	 * @author jmac
	 *
	 */
	public enum ExportFormat{
		EXCEL,
		PDF,
		WORD
	}
	
	public enum ApiExcel{
		POI,
		JXL
	}
	
}
