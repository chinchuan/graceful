package org.graceful.correct.service;

import java.io.Serializable;
import java.util.List;

import org.graceful.correct.core.Filter;
import org.graceful.correct.core.Page;

public interface CRUDService<Entity extends Serializable ,Key extends Serializable>{

	/**
	 * 新增数据
	 * @param entity  数据实体
	 * @return
	 */
	int insert(Entity entity);
	
	/**
	 * 更新数据
	 * @param entity 数据实体
	 * @return
	 */
	int update(Entity entity);
	
	/**
	 * 删除数据 
	 * @param key  主键
	 * @return
	 */
	int delete(Key key);
	
	/**
	 * 按主键获取实体数据
	 * @param key  主键
	 * @return
	 */
	Entity get(Key key);
	
	/**
	 * 分页查询数据
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	Page<Entity> query(Filter filter,Integer page,Integer size);
	
	/**
	 * 查询所有数据
	 * @param filter
	 * @return
	 */
	List<Entity> query(Filter filter);
}
