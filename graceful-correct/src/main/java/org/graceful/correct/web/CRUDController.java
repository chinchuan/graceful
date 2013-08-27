package org.graceful.correct.web;

import java.io.Serializable;

import org.graceful.correct.core.Filter;
import org.graceful.correct.core.Page;
import org.graceful.correct.service.CRUDService;
import org.springframework.web.bind.annotation.RequestMapping;

public abstract class CRUDController<E extends Serializable,K extends Serializable> {
	
	private CRUDService<E,K> service;
	
	public void setService(CRUDService<E,K> service) {
		this.service = service;
	}
 	
	/**
	 * 查询数据并返回分页后的内容
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/table")
	public Page<E> table(Filter filter,Integer page,Integer size){
		return service.query(filter, page, size);
	}
	
	public String edit(K key){
		return null;
	}
}
