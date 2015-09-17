
package org.generationcp.ibpworkbench.exception;

/**
 * Created by cyrus on 3/5/15.
 */
public class AppLaunchException extends Throwable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7775524159716055130L;
	private String[] params;

	public AppLaunchException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppLaunchException(String message) {
		super(message);
		this.params = new String[] {""};
	}

	public AppLaunchException(String message, String[] params) {
		super(message);
		this.params = params;
	}

	public AppLaunchException(String message, String[] params, Throwable cause) {
		super(message, cause);
		this.params = params;
	}

	public String[] getParams() {
		if (this.params != null) {
			return this.params;
		} else {
			return new String[] {""};
		}
	}

}
