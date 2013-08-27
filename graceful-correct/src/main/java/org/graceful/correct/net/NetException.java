package org.graceful.correct.net;

import org.graceful.correct.core.GracefulException;

public class NetException extends GracefulException {
 
	private static final long serialVersionUID = -3372025512178169881L;

	public NetException(String message, Throwable cause) {
		super(message, cause);
	}

}
