package org.generationcp.ibpworkbench.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by cyrus on 11/25/14.
 */
public class WorkbenchAuthenticationDetails extends WebAuthenticationDetails {

	private boolean rememberMe;

	public WorkbenchAuthenticationDetails(HttpServletRequest request) {
		super(request);

	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
}
