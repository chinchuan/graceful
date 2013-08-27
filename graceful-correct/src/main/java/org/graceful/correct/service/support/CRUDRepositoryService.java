package org.graceful.correct.service.support;

import java.io.Serializable;
import java.util.List;

import org.graceful.correct.baits.StandardMapper;
import org.graceful.correct.core.Filter;
import org.graceful.correct.core.Page;
import org.graceful.correct.service.AbstractService;
import org.graceful.correct.service.CRUDService;
import org.springframework.beans.factory.InitializingBean;

/**
 * CRUD 数据操作
 * @author qin 2013-8-26
 * @version
 * @param <Entity>
 * @param <Key>
 */
public  class CRUDRepositoryService<Entity extends Serializable ,Key extends Serializable> 
													 extends AbstractService implements CRUDService<Entity,Key>,InitializingBean {

	private StandardMapper<Entity> mapper;
	
	@Override
	public int insert(Entity entity) {
		return mapper.insert(entity);
	}

	@Override
	public int update(Entity entity) {
		return mapper.updateByPrimaryKey(entity);
	}

	@Override
	public int delete(Key key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	public Entity get(Key key) {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public Page<Entity> query(Filter filter, Integer page, Integer size) {
		return this.query(mapper, filter, page, size);
	}

	@Override
	public List<Entity> query(Filter filter) {
		return mapper.query(filter);
	}

	public void setMapper(StandardMapper<Entity> mapper){
		this.mapper = mapper;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
