package org.graceful.correct.export;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ValueFormat {
	
	/**
	 * 获取 格式化值
	 * @return
	 */
	public abstract  Object getFormatValue(Object target,Object value);
	
	/**
	 * 格式化日期 
	 * @param format
	 * @param value
	 * @return
	 */
	public String getFormatData(String format,Object value){
		if(value instanceof Date){
			DateFormat df = new SimpleDateFormat(format);
			return df.format(value);
		}else
			return "";
	}
}
