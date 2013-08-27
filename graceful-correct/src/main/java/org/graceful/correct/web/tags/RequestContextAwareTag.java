package org.graceful.correct.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.record.formula.functions.T;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

public abstract class RequestContextAwareTag extends TagSupport {

	private static final long serialVersionUID = -8523651183380306803L;

	private ApplicationContext context;
	
	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());
	
	public final int doStartTag() throws JspException {
		context = (ApplicationContext)this.pageContext.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		try {
			return doStartTagInternal();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new JspTagException(e);
		}
	}
	/**
	 * 获取 spring 管理的 bean
	 * @param cls
	 * @return
	 */
	protected final T getBean(Class<T> cls) {
		if(context==null) {
			return null;
		}
		return this.context.getBean(cls);
	}
	/**
	 * 获取 spring 管理的 bean
	 * @param name
	 * @return
	 */
	protected final Object getBean(String name) {
		if(context==null) {
			return null;
		}
		return this.context.getBean(name);
	}
	
	protected abstract int doStartTagInternal() throws Exception;
}
