package org.graceful.correct.core;

/**
 * 业务异常
 * 
 * @author jmac
 * 
 * @date 2013-5-7
 */
public class AbstractException extends GracefulException {

	private static final long serialVersionUID = 224301428888254037L;
	private String messageCode;
	private String[] args;
	

	public AbstractException(String message) {
		super(message);
	}

	/**
	 * 从资源文件获取信息
	 * @param messageCode   属性key
	 * @param args  在替换的数据
	 */
	public AbstractException(String messageCode,String... args) {
		super(messageCode);
		this.messageCode = messageCode;
		this.args = args;
	}
	
	/**
	 * 业务异常，取消底层异常跟踪
	 */
	public Throwable fillInStackTrace() {
		return null;
	}
	
	public String getMessageCode() {
		return messageCode;
	}
	
	public String[] getArgs() {
		return args;
	}
}
