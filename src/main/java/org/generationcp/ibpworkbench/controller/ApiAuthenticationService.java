
package org.generationcp.ibpworkbench.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

/**
 * Service for Java clients e.g. {@link AuthenticationController} to authenticate and obtain an access token in exchange of valid user name
 * and password. This token is required to make calls to BMSAPI REST services.
 */
@Service
public class ApiAuthenticationService {

	private static final Logger LOG = LoggerFactory.getLogger(ApiAuthenticationService.class);

	@Autowired
	private HttpServletRequest currentHttpRequest;

	@Autowired
	private RestOperations restClient;

	public Token authenticate(String userName, String password) {
		LOG.debug("Trying to authenticate user {} with BMSAPI to obtain a token.", userName);
		try {
			String bmsApiAuthURLFormat = "%s://%s:%s/bmsapi/authenticate?username=%s&password=%s";
			String bmsApiAuthURL =
					String.format(bmsApiAuthURLFormat, this.currentHttpRequest.getScheme(), this.currentHttpRequest.getServerName(),
							this.currentHttpRequest.getServerPort(), userName, password);
			final Token apiAuthToken = this.restClient.postForObject(bmsApiAuthURL, new HashMap<String, String>(), Token.class);
			if (apiAuthToken != null) {
				LOG.debug("Successfully authenticated and obtained a token from BMSAPI for user {}.", userName);
			}
			return apiAuthToken;
		} catch (RestClientException e) {
			LOG.debug("Error encountered while trying authenticate user {} with BMSAPI to obtain a token: {}", userName, e.getMessage());
			throw e;
		}
	}

	void setCurrentHttpRequest(HttpServletRequest currentHttpRequest) {
		this.currentHttpRequest = currentHttpRequest;
	}

	void setRestClient(RestOperations restClient) {
		this.restClient = restClient;
	}
}
