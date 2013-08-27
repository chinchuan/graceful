package org.graceful.correct.service;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.RowBounds;
import org.graceful.correct.baits.StandardMapper;
import org.graceful.correct.core.Filter;
import org.graceful.correct.core.Page;

public abstract class AbstractService {

	/** 默认一页显示的数据大小 */
	public static final int PAGE_SIZE = 20;
	
	protected Log logger = LogFactory.getLog(getClass());
	/**
	 * 初始化分页类
	 * @param <T>     返回实体
	 * @param currentPage  当前页
	 * @param pageSize    页大小
	 * @return
	 */
	protected <T extends Serializable> Page<T> initPage(Integer currentPage,Integer pageSize){
		
		currentPage = (currentPage==null || currentPage<0) ? currentPage=1 : currentPage ;
		pageSize = pageSize==null || pageSize<=0 ? PAGE_SIZE : pageSize;
		Page<T> page = new Page<T>();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
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
	@SuppressWarnings("unchecked")
	protected <T extends Serializable> Page<T> query(StandardMapper mapper,Filter filter,Integer currentPage,Integer pageSize){
		
		Page<T> page = initPage(currentPage,pageSize);
		int offset = Page.getOffset(page.getCurrentPage(), page.getPageSize()).intValue();
		page.setElements((List<T>)mapper.query(filter,new RowBounds(offset,page.getPageSize())));
		page.setRecordCount(mapper.getQueryCount(filter));
		if(logger.isDebugEnabled()) {
			logger.debug("query recore count "+page.getRecordCount());
		}
		return page;
	}
}
