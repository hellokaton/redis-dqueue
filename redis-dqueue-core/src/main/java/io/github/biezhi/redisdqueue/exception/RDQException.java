package io.github.biezhi.redisdqueue.exception;

/**
 * @author biezhi
 * @date 2019/11/21
 */
public class RDQException extends Exception {

	public RDQException() {
	}

	public RDQException(String message) {
		super(message);
	}

	public RDQException(String message, Throwable cause) {
		super(message, cause);
	}

	public RDQException(Throwable cause) {
		super(cause);
	}

	public RDQException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
