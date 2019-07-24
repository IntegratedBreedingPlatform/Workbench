package org.generationcp.ibpworkbench.security;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkbenchAuthenticationSuccessHandlerTest {

	private static final String TEST_USER = "testUser";

	@Test
	public void testOnAuthenticationSuccessWorkbenchSpecificDataIsPopulated()
			throws IOException, ServletException, MiddlewareQueryException {
		final WorkbenchAuthenticationSuccessHandler handler = new WorkbenchAuthenticationSuccessHandler();

		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		final HttpSession httpSession = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(httpSession);

		final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

		final Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getName()).thenReturn(WorkbenchAuthenticationSuccessHandlerTest.TEST_USER);

		final UserService userService = Mockito.mock(UserService.class);
		final List<WorkbenchUser> matchingUsers = new ArrayList<WorkbenchUser>();
		final WorkbenchUser testUserWorkbench = new WorkbenchUser();
		testUserWorkbench.setName(WorkbenchAuthenticationSuccessHandlerTest.TEST_USER);
		final Person person = new Person();
		testUserWorkbench.setPerson(person);
		matchingUsers.add(testUserWorkbench);
		Mockito.when(userService.getUserByName(WorkbenchAuthenticationSuccessHandlerTest.TEST_USER, 0, 1, Operation.EQUAL))
				.thenReturn(matchingUsers);

		handler.setUserService(userService);
		handler.onAuthenticationSuccess(request, response, authentication);

		// Just make sure following methods are invoked to populate session data for now.
		Mockito.verify(userService).getUserByName(WorkbenchAuthenticationSuccessHandlerTest.TEST_USER, 0, 1, Operation.EQUAL);
		Mockito.verify(httpSession).setAttribute(Matchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO), Matchers.isA(ContextInfo.class));
	}

}
