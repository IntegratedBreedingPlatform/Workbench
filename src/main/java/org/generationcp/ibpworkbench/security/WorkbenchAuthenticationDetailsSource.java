package org.generationcp.ibpworkbench.security;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by cyrus on 11/25/14.
 */
public class WorkbenchAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

	@Override
	public WebAuthenticationDetails buildDetails(HttpServletRequest httpServletRequest) {
		return new WorkbenchAuthenticationDetails(httpServletRequest);
	}
}
