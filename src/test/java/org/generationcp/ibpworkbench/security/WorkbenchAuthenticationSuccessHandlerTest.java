package org.generationcp.ibpworkbench.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;


public class WorkbenchAuthenticationSuccessHandlerTest {
	
	private static final String TEST_USER = "testUser";
	
	@Test
	public void testOnAuthenticationSuccessWorkbenchSpecificDataIsPopulated() throws IOException, ServletException, MiddlewareQueryException {
		WorkbenchAuthenticationSuccessHandler handler = new WorkbenchAuthenticationSuccessHandler();
		
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		
		SessionData sessionData = Mockito.mock(SessionData.class);
		
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getName()).thenReturn(TEST_USER);
		
		WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		List<User> matchingUsers = new ArrayList<User>();
		User testUserWorkbench = new User();
		testUserWorkbench.setName(TEST_USER);
		testUserWorkbench.setPersonid(1);
		matchingUsers.add(testUserWorkbench);
		Mockito.when(workbenchDataManager.getUserByName(TEST_USER, 0, 1, Operation.EQUAL)).thenReturn(matchingUsers);
		
		handler.setWorkbenchDataManager(workbenchDataManager);
		handler.setSessionData(sessionData);
		handler.onAuthenticationSuccess(request, response, authentication);
		
		//Just make sure following methods are invoked to populate session data and workbench runtime data for now.
		Mockito.verify(workbenchDataManager).getUserByName(TEST_USER, 0, 1, Operation.EQUAL);
		Mockito.verify(workbenchDataManager).getPersonById(testUserWorkbench.getPersonid());
		Mockito.verify(sessionData).setUserData(testUserWorkbench);
		Mockito.verify(workbenchDataManager).getWorkbenchRuntimeData();
		Mockito.verify(workbenchDataManager).updateWorkbenchRuntimeData(Mockito.any(WorkbenchRuntimeData.class));
	}

}
