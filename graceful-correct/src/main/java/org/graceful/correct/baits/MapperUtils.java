package org.graceful.correct.baits;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.graceful.correct.core.Filter;
import org.graceful.correct.core.Page;



public class MapperUtils {
	
	/**
	 * 初始化分页类
	 * @param <T>     返回实体
	 * @param currentPage  当前页
	 * @param pageSize    页大小
	 * @return
	 */
	public static <T extends Serializable> Page<T> initPage(Integer currentPage,Integer pageSize){
		currentPage = (currentPage==null || currentPage<0) ? currentPage=1 : currentPage ;
		pageSize = pageSize==null ? Page.PAGESIZE : pageSize;
		Page<T> page = new Page<T>(currentPage,pageSize);
		return page;
	}
	
	/**
	 * 分页查询
	 * @param <T>      实体类
	 * @param mapper   数据操作类
	 * @param currentPage 当前页
	 * @param pageSize  页大小
	 * @return
	 */
	public static <T extends Serializable> Page<T> query(StandardMapper<T> mapper,Filter filter,Integer currentPage,Integer pageSize){
		
		Page<T> page = initPage(currentPage,pageSize);
		int offset = Page.getOffset(page.getCurrentPage(), page.getPageSize()).intValue();
		List<T> result = mapper.query(filter, new RowBounds(offset,page.getPageSize()));
		page.setElements(result); 
		page.setRecordCount(mapper.getQueryCount(filter));
		
		return page;
	}
}
