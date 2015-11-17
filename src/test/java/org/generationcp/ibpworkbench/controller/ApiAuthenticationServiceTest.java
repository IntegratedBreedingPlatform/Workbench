package org.generationcp.ibpworkbench.controller;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;


public class ApiAuthenticationServiceTest {

	@Mock
	private HttpServletRequest currentHttpRequest;

	@Mock
	private RestOperations restClient;

	@InjectMocks
	private ApiAuthenticationService apiAuthenticationService = new ApiAuthenticationService();

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAuthenticate() {
		final Token testToken = new Token("token", 123456789L);
		Mockito.when(this.restClient.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Token.class))).thenReturn(testToken);
		Token token = this.apiAuthenticationService.authenticate("user", "password");
		Assert.assertEquals(testToken.getToken(), token.getToken());
		Assert.assertEquals(testToken.getExpires(), token.getExpires());
	}

	@Test
	public void testAuthenticateWithBMSAPIReturningNullToken() {
		Mockito.when(this.restClient.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Token.class))).thenReturn(null);
		Token token = this.apiAuthenticationService.authenticate("user", "password");
		Assert.assertNull(token);
	}

	@Test(expected = RestClientException.class)
	public void testAuthenticateWithBMSAPIThrowingUp() {
		Mockito.when(this.restClient.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Token.class))).thenThrow(
				new RestClientException("Error calling BMSAPI authentication service."));
		this.apiAuthenticationService.authenticate("user", "password");
	}
}
