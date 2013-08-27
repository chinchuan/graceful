package org.graceful.correct.web.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

public class GracefulContentNegotiatingViewResolver extends ContentNegotiatingViewResolver {

	private static final Log logger = LogFactory.getLog(GracefulContentNegotiatingViewResolver.class);

	private List<View> defaultViews;
	
	@Override
	public void setDefaultViews(List<View> defaultViews) {
		 super.setDefaultViews(defaultViews);
		 this.defaultViews = defaultViews;
	}
	
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		ApplicationContext context =  getApplicationContext();
		Map<String,View> register =  context.getBeansOfType(View.class);
		if(register!=null && register.size()>0) {
			if (logger.isDebugEnabled()) {
				logger.debug("register views  size:" + register.size());
			}		
			if (CollectionUtils.isEmpty(this.defaultViews)) {
				this.defaultViews = new ArrayList<View>(register.size());
				setDefaultViews(this.defaultViews);
			}
			this.defaultViews.addAll(register.values());
		}
	}
}
