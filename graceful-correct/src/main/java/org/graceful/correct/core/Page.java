package org.graceful.correct.core;

import java.io.Serializable;
import java.util.List;

/**
 * 分页类
 * @author jmac
 *
 * @param <T>
 */
public class Page<T extends Serializable>  {
	public final static int PAGESIZE = 20;
	/**
	 * 返回数据列表
	 */
	private List<T> elements;
   
	private Number recordCount=0; //总记录
	private Integer totalPage; //总页
	
	private int currentPage = 1; //当前页
	private int pageSize = PAGESIZE; //每页大小
	
	
	public Page(){
	}
	
	public Page(List<T> elements,Number recordCount,int pageSize,int currentPage) {
		this.elements = elements;
		this.recordCount = recordCount;
		this.pageSize =  pageSize;
		this.currentPage = currentPage;
		this.totalPage  = Page.getTotalPage(pageSize, this.recordCount);
	}
	
	public Page(List<T> elements,Number recordCount,int currentPage) {
		this(elements,recordCount,PAGESIZE,currentPage);
	}
	
	public Page(int currentPage,int pageSize) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
	}
	
	public List<T> getElements() {
		return elements;
	}

	public  void setElements(List<T> elements) {
		this.elements = elements;
	}

	public Number getRecordCount() {
		return recordCount;
	}
 
	public  void setRecordCount(Number recordCount) {
		this.recordCount = recordCount;
		this.totalPage = Page.getTotalPage(pageSize, this.recordCount);
	}
	
	public Integer getTotalPage() {
		return totalPage;
	} 
 
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * 计算总的页数
	 * @param pageSize  页大小 
	 * @param recordCount  总记录
	 * @return
	 */
	public static int getTotalPage(final int pageSize, final Number recordCount){
		int totalPage =(int)( recordCount.longValue() % pageSize == 0 ? recordCount.longValue()/pageSize : recordCount.longValue()/pageSize+1); 
		return totalPage;
	}
	
	/**
	 * 计算当前页开始记录 
	 * @param currentPage  当前页
	 * @param pageSize     页大小
	 * @return
	 */
	public static Long getOffset(final int currentPage,final int pageSize){
		Long offset =  (long) (pageSize*(currentPage-1)); 
		return offset;
	}
	
	public long getOffset() {
		return Page.getOffset(currentPage, pageSize);
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
}
