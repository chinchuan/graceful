package org.graceful.wr.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

 

public class PermissioninInterceptor extends HandlerInterceptorAdapter {
	
	//不需要验证的页
	private final static Log log = LogFactory.getLog(PermissioninInterceptor.class);
	 
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
	     
		if(!(handler instanceof ResourceHttpRequestHandler)) {
			log.info("====="+handler);
			
			System.out.println(handler);
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			log.info(handlerMethod.getMethod());
		}
		
		//HandlerMethod handlerMethod = (HandlerMethod)handler;
		//log.info(handlerMethod.getMethod());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	 
	}
}
