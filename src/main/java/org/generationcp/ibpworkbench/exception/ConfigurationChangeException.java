
package org.generationcp.ibpworkbench.exception;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/12/2015 Time: 1:11 PM
 */
public class ConfigurationChangeException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 6468175326515904069L;

	public ConfigurationChangeException() {
	}

	public ConfigurationChangeException(String message) {
		super(message);
	}

	public ConfigurationChangeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationChangeException(Throwable cause) {
		super(cause);
	}

	public ConfigurationChangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
