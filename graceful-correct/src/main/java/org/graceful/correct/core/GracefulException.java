package org.graceful.correct.core;

 
public abstract class GracefulException extends RuntimeException {
	
	private static final long serialVersionUID = 8361390426905415042L;

	public GracefulException(String message){
		super(message);
	}
	
	public GracefulException(String message,Throwable cause){
		super(message,cause);
	}

	public Throwable getRootCause() {
		Throwable rootCause = null;
		Throwable cause = getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		return rootCause;
	}
}
