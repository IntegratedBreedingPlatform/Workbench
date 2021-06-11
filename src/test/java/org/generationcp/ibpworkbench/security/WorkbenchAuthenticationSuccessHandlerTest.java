package org.generationcp.ibpworkbench.security;

import org.apache.commons.lang.math.RandomUtils;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.releasenote.ReleaseNoteService;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WorkbenchAuthenticationSuccessHandlerTest {

	private static final String TEST_USER = "testUser";
	private static final Integer USER_ID = new Random().nextInt();

	@InjectMocks
	private WorkbenchAuthenticationSuccessHandler handler;

	@Mock
	private ReleaseNoteService releaseNoteService;

	@Mock
	private UserService userService;

	@Captor
	private ArgumentCaptor<ContextInfo> contextInfoArgumentCaptor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOnAuthenticationSuccessWorkbenchSpecificDataIsPopulated()
			throws IOException, ServletException, MiddlewareQueryException {

		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpSession httpSession = mock(HttpSession.class);
		when(request.getSession()).thenReturn(httpSession);

		final HttpServletResponse response = mock(HttpServletResponse.class);

		final Authentication authentication = mock(Authentication.class);
		when(authentication.getName()).thenReturn(WorkbenchAuthenticationSuccessHandlerTest.TEST_USER);

		when(this.releaseNoteService.shouldShowReleaseNote(USER_ID)).thenReturn(true);

		final List<WorkbenchUser> matchingUsers = new ArrayList();
		final WorkbenchUser testUserWorkbench = mock(WorkbenchUser.class);
		when(testUserWorkbench.getName()).thenReturn(TEST_USER);
		when(testUserWorkbench.getUserid()).thenReturn(USER_ID);

		final Person person = mock(Person.class);
		when(person.getId()).thenReturn(USER_ID);
		when(testUserWorkbench.getPerson()).thenReturn(person);

		matchingUsers.add(testUserWorkbench);
		when(this.userService.getUserByName(WorkbenchAuthenticationSuccessHandlerTest.TEST_USER, 0, 1, Operation.EQUAL))
				.thenReturn(matchingUsers);

		final UserInfo userInfo = mock(UserInfo.class);
		when(this.userService.getUserInfo(USER_ID)).thenReturn(userInfo);
		final Integer loginCount = nextInt();
		when(userInfo.getLoginCount()).thenReturn(loginCount);

		this.handler.setUserService(userService);
		this.handler.onAuthenticationSuccess(request, response, authentication);

		// Just make sure following methods are invoked to populate session data for now.
		verify(this.userService).getUserByName(WorkbenchAuthenticationSuccessHandlerTest.TEST_USER, 0, 1, Operation.EQUAL);
		verify(httpSession).setAttribute(ArgumentMatchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO), this.contextInfoArgumentCaptor.capture());

		final ContextInfo actualContextInfo = this.contextInfoArgumentCaptor.getValue();
		assertTrue(actualContextInfo.shouldShowReleaseNotes());

		verify(this.releaseNoteService).shouldShowReleaseNote(USER_ID);
		verify(userInfo).setLoginCount(loginCount + 1);
	}

}
