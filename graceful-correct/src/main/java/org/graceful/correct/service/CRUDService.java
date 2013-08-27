package org.graceful.correct.service;

import java.io.Serializable;
import java.util.List;

import org.graceful.correct.core.Filter;
import org.graceful.correct.core.Page;

public interface CRUDService{

	/**
	 * 新增数据
	 * @param entity  数据实体
	 * @return
	 */
	<Entity extends Serializable> int insert(Entity entity);
	
	/**
	 * 更新数据
	 * @param entity 数据实体
	 * @return
	 */
	<Entity extends Serializable> int update(Entity entity);
	
	/**
	 * 删除数据 
	 * @param key  主键
	 * @return
	 */
	<Key extends Serializable> int delete(Key key);
	
	/**
	 * 按主键获取实体数据
	 * @param key  主键
	 * @return
	 */
	<Entity extends Serializable,Key extends Serializable> Entity get(Key key);
	
	/**
	 * 分页查询数据
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	<Entity extends Serializable> Page<Entity> query(Filter filter,Integer page,Integer size);
	
	/**
	 * 查询所有数据
	 * @param filter
	 * @return
	 */
	<Entity extends Serializable> List<Entity> query(Filter filter);
}
