package org.generationcp.ibpworkbench.security;

/**
 * Created by cyrus on 4/8/15.
 */
public class InvalidResetTokenException extends Exception {
	public InvalidResetTokenException() {
	}

	public InvalidResetTokenException(String message) {
		super(message);
	}

	public InvalidResetTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidResetTokenException(Throwable cause) {
		super(cause);
	}

	public InvalidResetTokenException(String message, Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
