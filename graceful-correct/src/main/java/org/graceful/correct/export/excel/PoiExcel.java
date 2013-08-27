package org.graceful.correct.export.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.graceful.correct.export.DataGrid;
import org.graceful.correct.export.ExportData;
import org.graceful.correct.export.GridHeader;

/**
 * poi 操作excel 
 * @author jmac
 * @version 1.2
 *
 */
public class PoiExcel implements DataGrid {
	private final static Log log = LogFactory.getLog(PoiExcel.class);
	
	private HSSFWorkbook wb;
	private Map<String, HSSFCellStyle> styles = new HashMap<String, HSSFCellStyle>(); //样式
	private Map<String,Integer> columnMaxLen; //为解决中文问题重新设置最大长宽
	
	
	/**
	 * 获取指定sheet excel数据
	 * @param stream
	 * @param sheet
	 * @return
	 * @throws IOException
	 */
	public  List<Map<Integer, Object>> getExcelInfo(InputStream stream,
			int sheet, short... nDateCell) throws IOException {
		List<Map<Integer, Object>> list = new ArrayList<Map<Integer, Object>>();
		POIFSFileSystem fs = new POIFSFileSystem(stream);
		wb = new HSSFWorkbook(fs);
		// 获取指定sheet;
		HSSFSheet fsSheet = wb.getSheetAt(sheet);
		// 行数
		int rowCount = fsSheet.getPhysicalNumberOfRows();
		// 列数
		int celCount = fsSheet.getRow(0).getPhysicalNumberOfCells();
		Object value = "";
		boolean isDate;
		int type;
		for (int i = 0; i < rowCount; i++) {
			boolean flag = false;
			HSSFRow row = fsSheet.getRow(i);
			// 取出每列数据
			Map<Integer, Object> data = new HashMap<Integer, Object>();
			if(log.isDebugEnabled())
				log.debug(row.getCell((short)0).toString()+" "+row.getCell((short)1).toString()+" "+row.getCell((short)2).toString()+" ");
			
			if (row != null) {
				for (short j = 0; j < celCount; j++) {
					// 数据类型
					isDate = false;
					HSSFCell cell = row.getCell(j);
					if (row.getCell(j) != null) {
						type = cell.getCellType();
						if (type == HSSFCell.CELL_TYPE_NUMERIC) {
							// 是否是日期类型
							for (short n : nDateCell) {
								if (n == j) {
									value = cell.getDateCellValue();
									isDate = true;
									break;
								}
							}
							if (!isDate)
								value = cell.getNumericCellValue();
						} else if (type == HSSFCell.CELL_TYPE_BOOLEAN)
							value = cell.getBooleanCellValue();
						else if (type == HSSFCell.CELL_TYPE_ERROR)
							value = cell.getErrorCellValue();
						else if (type == HSSFCell.CELL_TYPE_FORMULA)
							value = cell.getCellFormula();
						else if (type == HSSFCell.CELL_TYPE_STRING)
							value = cell.getRichStringCellValue().toString();
						else if (type == HSSFCell.CELL_TYPE_BLANK)
							value = "";
						else
							value = cell.toString();
						data.put(Integer.valueOf(j), value);

						flag = true;

					}
					cell = null;
				}
			}
			row = null;
			if (flag) {
				list.add(data);
			}
		}
		return list;
	}


	public void export(ExportData data, OutputStream out, String sheetName) {
		if(data.getGridHeader()==null || data.getGridHeader().size()==0)
			throw new NullPointerException("excel Header");
		
		wb = new HSSFWorkbook();
	    try {   
	    	export(data,wb,sheetName);
			wb.write(out);
			out.flush();	
 
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e);
		}
	}
	
	
	public void export(ExportData data, HSSFWorkbook workbook, String sheetName) {
		
		if(data.getGridHeader()==null || data.getGridHeader().size()==0)
			throw new NullPointerException("excel Header");
		
		HSSFSheet sheet =sheetName!=null && !"".equals(sheetName) ?  workbook.createSheet(sheetName) :workbook.createSheet();
		
		//创建样式
		initStyles(data.getGridHeader());
		columnMaxLen = new HashMap<String,Integer>(data.getGridHeader().size());
		
		sheet.setPrintGridlines(false);
		sheet.setDisplayGridlines(false);
		
		// 生成excel表头
		createHeader(sheet,data.getGridHeader());
		
		//获取数据
		long total = data.getTotal();
		if(total>0){
			if(total<ExportData.pageSize)
				
					setRowValue(sheet,data.getDataList(),data.getGridHeader(),0);
				
			else{ //分页情况
				int totalPage =(int)( total % ExportData.pageSize == 0 ? total/ExportData.pageSize : total/ExportData.pageSize+1); 
				//取出每页数据
				for(int page=1;page<=totalPage;page++){
					setRowValue(sheet,data.getDataList(page),data.getGridHeader(),ExportData.pageSize*(page - 1));
				}
			}
		}
		
		//调整列宽度
		short column = 0;
		int headLen = 0;
		Integer maxLen=0;
		
		for(GridHeader hd :data.getGridHeader()){
			maxLen  = columnMaxLen.get(hd.getField());
			headLen = hd.getName().getBytes().length;
			maxLen = maxLen==null  || maxLen ==0 ? 0  : headLen>maxLen ? headLen : maxLen;
			
			if(log.isDebugEnabled())
				log.debug(" column name:"+ hd.getName()+" len:"+ maxLen);
			
			if(maxLen==null  || maxLen ==0){ //自动设置宽
				sheet.autoSizeColumn(column);
				//如果 宽度小于表头长度则设置表头长度宽
				/*
				temp = sheet.getColumnWidth(column);
				if(temp<headLen)
					sheet.setColumnWidth(column,(short)(headLen * 256));
				 */
			}else {
				maxLen +=2;
				sheet.setColumnWidth(column,(short)(maxLen * 256));
			}
			column ++ ;
		}
	}

	/**
	 * 创建表头
	 * @param sheet
	 */
	private  void createHeader(HSSFSheet sheet,List<GridHeader> header){
		HSSFRow headerRow = sheet.createRow(0);
		HSSFCellStyle headerStyle = this.styles.get("header");
		
		//生成表头
		short i = 0;
		for (GridHeader field:header) {
			HSSFCell cell = headerRow.createCell(i++);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellStyle(headerStyle);
			HSSFRichTextString txt = new HSSFRichTextString(field.getName());
			cell.setCellValue(txt);
			//初始化行宽
			columnMaxLen.put(field.getField(), field.getMinWidth());
		}
		headerRow.setHeightInPoints(20f);
	}
	
	/**
	 * 设置行值
	 * @param data
	 * @param header
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	private  void setRowValue(HSSFSheet sheet,List<?> data,List<GridHeader> header,int offset){
		
		int rowIndex = StartDataIndex + offset;
		short i=0;
		for (Object item : data) {
			Class<?> cls = item.getClass();
			if(log.isDebugEnabled())
				log.debug("excel set row  index:"+ rowIndex);
			
			HSSFRow row = sheet.createRow(rowIndex++);
			i = 0;
			Object value=null;
			for (GridHeader hd:header) {
				if(hd.getField()!=null && !hd.getField().equals("")){
					if (item instanceof Map)
						value = ((Map<?,?>) item).get(hd.getField());
					else{
						try {
							value = getValue(cls,item,hd.getField()); //获取属性值
							
							if(hd.getFormat()!=null) 
								value = hd.getFormat().getFormatValue(item, value);
							
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}else if(hd.getFormat()!=null)
					value = hd.getFormat().getFormatValue(item, null);
				else
					value ="";
					 
				setCellValue(row, i++,item, value,hd);
			}
		}
	}
	
	/**
	 * 反射获取java bean 值 
	 * @param bean
	 * @param name
	 * @return
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private  Object getValue(Class<?> cls,Object bean,String name) 
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		
		int split = name.indexOf(".");
		if(split>0){
			String parent= name.substring(0,split);
			//获取bean
			Object parentBean = getValue(cls,bean,parent);
			//递归获取值 
			String children = name.substring(split+1);
			if(parentBean!=null){
				return getValue(parentBean.getClass(),parentBean,children);
			}else
				return null;
			 
		}else{
			//获取值
			Field field = cls.getDeclaredField(name);
			if(field!=null){
				field.setAccessible(true);
				return field.get(bean);
			}else
				return null;
		}
	}

	/**
	 * 设置单元格内容
	 * @param row
	 * @param celIndex
	 * @param value
	 */
	private  void setCellValue(HSSFRow row, short celIndex, Object target,Object value,GridHeader header) {
		HSSFCell cell = row.createCell(celIndex);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellStyle(styles.get(header.getField()));//设置样式
		
		//显示数据
		if(value instanceof Date){
			cell.setCellValue((Date)value);
		}else if(value instanceof Boolean)
			cell.setCellValue((Boolean)value);
		else if(value instanceof Number){
			cell.setCellValue(Double.parseDouble(value.toString()));
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		}else{
			String cellValue = value!=null ? value.toString() :"" ;
			cell.setCellValue(new HSSFRichTextString(cellValue));
			//解决中文问题重设置列宽
			int dataLen = cellValue.getBytes().length;
			dataLen = dataLen == 0 ? 1 : dataLen;
			if(columnMaxLen.get(header.getField())<dataLen)
				columnMaxLen.put(header.getField(), dataLen);
		}
	}
	
	/**
	 * 创建所有样式
	 * @param wb
	 * @return
	 */
	private  Map<String, HSSFCellStyle> initStyles(List<GridHeader> headers){
		 
		 HSSFDataFormat df = wb.createDataFormat();
		 
		 HSSFCellStyle style;
		 //设置字体
	     HSSFFont headerFont = wb.createFont();
	     headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		 
	     style = createBorderedStyle();
	     style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	     style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
	     style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	     style.setFont(headerFont);
	     
	     styles.put("header", style);
	     //数据列表样式
	     for(GridHeader header:headers){
	    	 style = createBorderedStyle();
	    	 style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	    	 style.setWrapText(true);
	    	 if(header.getStringFormat()!=null  && !"".equals(header.getStringFormat())){
	    		 style.setDataFormat(df.getFormat(header.getStringFormat()));
	    	 }
	    	 styles.put(header.getField(), style);
	     }
		 return styles;
	}
	
	/**
	 * 边框样式
	 * @param wb
	 * @return
	 */
	private  HSSFCellStyle createBorderedStyle(){
		 HSSFCellStyle style = wb.createCellStyle();
		 style.setBorderRight(HSSFCellStyle.BORDER_THIN);
	     style.setRightBorderColor(HSSFColor.BLACK.index);
	     style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	     style.setBottomBorderColor(HSSFColor.BLACK.index);
	     style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	     style.setLeftBorderColor(HSSFColor.BLACK.index);
	     style.setBorderTop(HSSFCellStyle.BORDER_THIN);
	     style.setTopBorderColor(HSSFColor.BLACK.index);
	     return style;
	 }



}
