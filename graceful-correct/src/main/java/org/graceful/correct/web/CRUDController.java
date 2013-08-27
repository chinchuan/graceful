package org.graceful.correct.web;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.graceful.correct.core.CRUD;
import org.graceful.correct.core.GracefulException;
import org.graceful.correct.service.CRUDService;

public class CRUDController<Entity extends Serializable,Key extends Serializable> extends AbstractController{
	
	private CRUDService service;
	
	private Class<Entity> cls;
 
	public void setService(CRUDService service) {
		this.service = service;
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Field[] fields = this.getClass().getFields();
		CRUD crud = null ;
		Object value = null;
		for(Field field : fields) {
			crud = field.getAnnotation(CRUD.class);
			if(crud!=null) {
				value = field.get(getClass());
				//设置CRUDService 实例
				if(value == null || !(value instanceof CRUDService)) {
					throw new GracefulException(getClass().getName()+":"+field.getName() +" not CRUDService" );
				}
				setService((CRUDService)value);
			}
		}
		if(crud==null) {
			throw new GracefulException(getClass().getName() +" no set CRUDService" );
		}
	}
}
