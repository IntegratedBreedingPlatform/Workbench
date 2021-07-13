package org.generationcp.ibpworkbench.security;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.releasenote.ReleaseNoteService;
import org.generationcp.middleware.service.api.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handler for setting up Workbench specific stuff e.g. {@link org.generationcp.commons.spring.util.ContextUtil} before redirecting to the page requested on successful
 * authentication. Could also be used to redirect to different destinations based on role if needed.
 *
 * @author Naymesh Mistry
 */
public class WorkbenchAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchAuthenticationSuccessHandler.class);

	private static final String DEFAULT_TARGET_URL = "/main/";

	@Autowired
	private UserService userService;

	@Autowired
	private ReleaseNoteService releaseNoteService;

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {

		final String targetUrl = DEFAULT_TARGET_URL;
		if (response.isCommitted()) {
			LOG.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		final WorkbenchUser user = retrieveUserFromAuthentication(authentication);
		final boolean shouldShowReleaseNote = this.releaseNoteService.shouldShowReleaseNote(user.getUserid());

		// Initialize the ContextInfo to set the userId of the authenticated user.
		// The projectId and token will be populated later when a program is opened/loaded.
		ContextUtil.setContextInfo(request, user.getUserid(), null, null, shouldShowReleaseNote);

		this.userService.incrementUserLogInCount(user.getUserid());

		this.clearAuthenticationAttributes(request);

		this.redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	protected WorkbenchUser retrieveUserFromAuthentication(final Authentication authentication) {

		final String username = authentication.getName();
		final WorkbenchUser user = this.userService.getUserByName(username, 0, 1, Operation.EQUAL).get(0);

		return user;

	}

	/**
	 * Removes temporary authentication-related data which may have been stored in the session during the authentication process.
	 */
	protected final void clearAuthenticationAttributes(final HttpServletRequest request) {
		final HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}
}
