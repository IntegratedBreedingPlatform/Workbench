package org.generationcp.ibpworkbench.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;

public class ApiAuthenticationServiceTest {

	private static final String API_URL = RandomStringUtils.randomAlphabetic(20) + "/";

	@Mock
	private RestOperations restClient;

	@InjectMocks
	private final ApiAuthenticationService apiAuthenticationService = new ApiAuthenticationService();

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);
		this.apiAuthenticationService.setApiUrl(API_URL);
	}

	@Test
	public void testAuthenticate() {
		final Token testToken = new Token("token", 123456789L);

		final ArgumentCaptor<URI> urlCaptor = ArgumentCaptor.forClass(URI.class);
		Mockito.when(this.restClient.postForObject(urlCaptor.capture(), Mockito.any(), Mockito.eq(Token.class))).thenReturn(testToken);

		final String user = RandomStringUtils.randomAlphabetic(5);
		final String password = RandomStringUtils.randomAlphabetic(8);
		final Token token = this.apiAuthenticationService.authenticate(user, password);
		Assert.assertEquals(testToken.getToken(), token.getToken());
		Assert.assertEquals(testToken.getExpires(), token.getExpires());
		final URI urlUsedForTokenResource = urlCaptor.getValue();
		Assert.assertEquals(
			"Token authentication request URL must use BMSAPI URL from property files",
			API_URL + "token", urlUsedForTokenResource.toString());
	}

	@Test
	public void testAuthenticateEncodedPassword() {
		final Token testToken = new Token("token", 123456789L);

		final ArgumentCaptor<URI> urlCaptor = ArgumentCaptor.forClass(URI.class);
		Mockito.when(this.restClient.postForObject(urlCaptor.capture(), Mockito.any(), Mockito.eq(Token.class))).thenReturn(testToken);

		final String specialCharacters = "~!@#$%^&*()_+{}|:\"<>?`1234567890-=[]\\;',./ a";
		final Token token = this.apiAuthenticationService.authenticate(specialCharacters, specialCharacters);
		Assert.assertEquals(testToken.getToken(), token.getToken());
		Assert.assertEquals(testToken.getExpires(), token.getExpires());
		final URI urlUsedForTokenResource = urlCaptor.getValue();
		Assert.assertEquals(
			"Token authentication request URL must use BMSAPI URL from property files", API_URL
				+ "token",
			urlUsedForTokenResource.toString());
	}

	@Test
	public void testAuthenticateWithBMSAPIReturningNullToken() {
		Mockito.when(this.restClient.postForObject(Mockito.any(URI.class), Mockito.any(), Mockito.eq(Token.class))).thenReturn(null);
		final Token token = this.apiAuthenticationService.authenticate("user", "password");
		Assert.assertNull(token);
	}

	@Test(expected = RestClientException.class)
	public void testAuthenticateWithBMSAPIThrowingUp() {
		Mockito.when(this.restClient.postForObject(Mockito.any(URI.class), Mockito.any(), Mockito.eq(Token.class))).thenThrow(
			new RestClientException("Error calling BMSAPI authentication service."));
		this.apiAuthenticationService.authenticate("user", "password");
	}
}
