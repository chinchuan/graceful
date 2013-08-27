package org.graceful.correct.web;

import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.graceful.correct.core.AbstractException;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 统一异常处理
 * 
 * @author jmac
 * 
 * @date 2013-5-7
 */
public class HandlerException implements HandlerExceptionResolver {

	@Resource
	private AbstractMessageSource messageSource;
	private final static String ERROR_PAGE = "globalResult";

	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object obj, Exception e) {
		String message = null;
		// 业务异常
		if (e instanceof AbstractException) {
			AbstractException ex = (AbstractException) e;
			message = this.messageSource.getMessage(ex.getMessageCode(),
					ex.getArgs(), Locale.getDefault());
		} else {
			message = e.getMessage();
		}
		
		ModelAndView mav = new ModelAndView(ERROR_PAGE);
		mav.addObject("error", 1);
		mav.addObject("message", message);
		mav.addObject("url", request.getRequestURI());

		return mav;
	}

}
