package org.graceful.correct.export;

import java.util.List;

public interface ExportData {
	
	/**
	 * 分页大小
	 */
	final static int pageSize = 500;
	
	/**
	 * 分页获取数据
	 * @param pageIndex
	 * @return
	 */
	public List<?> getDataList(int pageIndex);
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<?> getDataList();
	
	/**
	 * 获取总共数据总数
	 * @return
	 */
	public long getTotal();
	
	/**
	 * 获取excel表头
	 * @return
	 */
	public List<GridHeader> getGridHeader();
	
}
