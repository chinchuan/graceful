package org.graceful.correct.service.support;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.graceful.correct.baits.StandardMapper;
import org.graceful.correct.core.CRUD;
import org.graceful.correct.core.Filter;
import org.graceful.correct.core.GracefulException;
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
public  class CRUDRepositoryService extends AbstractService implements CRUDService,InitializingBean {

	private StandardMapper mapper;

	@Override
	public void afterPropertiesSet() throws Exception {
		Field[] fields = this.getClass().getFields();
		CRUD crud = null ;
		Object value = null;
		for(Field field : fields) {
			crud = field.getAnnotation(CRUD.class);
			if(crud!=null) {
				value = field.get(getClass());
				//设置crud StandardMapper 实例
				if(value == null || !(value instanceof StandardMapper)) {
					throw new GracefulException(getClass().getName()+":"+field.getName() +" not CRUD StandardMapper" );
				}
				setMapper((StandardMapper)value);
			}
		}
		if(crud==null) {
			throw new GracefulException(getClass().getName() +" no set CRUD StandardMapper" );
		}
	}

	@Override
	public <Entity extends Serializable> int insert(Entity entity) {
		return mapper.insert(entity);
	}

	@Override
	public <Entity extends Serializable> int update(Entity entity) {
		return mapper.updateByPrimaryKey(entity);
	}
	
	@Override
	public <Key extends Serializable> int delete(Key key) {
		return mapper.deleteByPrimaryKey(key);
	}
	
	@Override
	public <Entity extends Serializable, Key extends Serializable> Entity get(
			Key key) {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	public <Entity extends Serializable> Page<Entity> query(Filter filter,
			Integer page, Integer size) {
		return this.query(mapper, filter, page, size);
	}

	@Override
	public <Entity extends Serializable> List<Entity> query(Filter filter) {
		return this.query(filter);
	}

	public void setMapper(StandardMapper mapper) {
		this.mapper = mapper;
	}
	
}
