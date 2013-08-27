package org.graceful.correct.web;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * controller  
 * 计算 view 名称
 * @author qin 2013-8-27
 * @version
 */
public abstract class AbstractController implements InitializingBean{

	private String prefixView = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//解析view 名称
		RequestMapping mapping = AnnotationUtils.findAnnotation(getClass(), RequestMapping.class);
		if(mapping!=null) {
			String[] value = mapping.value();
			if(value.length>0) {
				prefixView = value[0];
			}
		}
	}
	
	/**
	 * 返回 view
	 * @param suffix  
	 * @return
	 */
	protected String view(String suffix){
		String view;
		//资源名
		if(prefixView == null) {
			prefixView = "";
		}
		if(suffix.startsWith("/") || prefixView.endsWith("/")) {
			view = prefixView + suffix;
		}else {
			view = prefixView + "/" + suffix;
		}
		return view;
	}
}
