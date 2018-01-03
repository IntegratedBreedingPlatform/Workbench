package org.generationcp.ibpworkbench.security;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
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

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	private String defaultTargetUrl = "/main";

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {

		final String targetUrl = this.defaultTargetUrl;
		if (response.isCommitted()) {
			this.LOG.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		final User user = retrieveUserFromAuthentication(authentication);

		this.updateWorkbenchRuntimeData(user);

		// Initialize the ContextInfo to set the userId of the authenticated user.
		// The projectId and token will be populated later when a program is opened/loaded.
		ContextUtil.setContextInfo(request, user.getUserid(), null, null);

		this.clearAuthenticationAttributes(request);

		this.redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	protected User retrieveUserFromAuthentication(final Authentication authentication) {

		final String username = authentication.getName();
		final User user = this.workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL).get(0);
		final Person person = this.workbenchDataManager.getPersonById(user.getPersonid());
		user.setPerson(person);

		return user;

	}

	/**
	 * Actions that the old org.generationcp.ibpworkbench.actions.LoginPresenter used to perform on successful login.
	 */
	private void updateWorkbenchRuntimeData(final User user) {

		// 2. Remember Me. TODO under BMS-84.
		// See the cookie based scheme in org.generationcp.ibpworkbench.actions.LoginPresenter.doLogin(): line 97-111 for ref.
		// We want this replaced using Spring Security's "Remember Me services" options.

		// 3. Update WorkbenchRuntimeData
		WorkbenchRuntimeData data = this.workbenchDataManager.getWorkbenchRuntimeData();
		if (data == null) {
			data = new WorkbenchRuntimeData();
		}
		data.setUserId(user.getUserid());
		this.workbenchDataManager.updateWorkbenchRuntimeData(data);
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

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}
