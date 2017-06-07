package org.generationcp.ibpworkbench.controller;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import junit.framework.Assert;


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

		Mockito.when(currentHttpRequest.getLocalPort()).thenReturn(78080);
		Mockito.when(currentHttpRequest.getScheme()).thenReturn("http");

		ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.when(this.restClient.postForObject(urlCaptor.capture(), Mockito.any(), Mockito.eq(Token.class))).thenReturn(testToken);

		Token token = this.apiAuthenticationService.authenticate("user", "password");
		Assert.assertEquals(testToken.getToken(), token.getToken());
		Assert.assertEquals(testToken.getExpires(), token.getExpires());
		final String urlUsedForTokenResource = urlCaptor.getValue();
		Assert.assertEquals(
				"Token authentication request URL must use the local loop back address and local port where request was received.",
				"http://" + ApiAuthenticationService.LOCAL_LOOPBACK_ADDRESS + ":78080/bmsapi/brapi/v1/token",
				urlUsedForTokenResource);
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
