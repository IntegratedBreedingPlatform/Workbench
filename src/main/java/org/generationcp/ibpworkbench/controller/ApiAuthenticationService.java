
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
// **Important note for developers** : This class is central to the authentication framework of BMSAPI. Please do not alter it without a
// good understanding of Spring Security in general and BMS X-Auth-Token based authentication workflow in particular, otherwise there will
// be MAJOR breakages in the functioning of BMS components. Consult your friendly senior developer first if you are unsure.
@Service
public class ApiAuthenticationService {

	static final String LOCAL_LOOPBACK_ADDRESS = "127.0.0.1";

	private static final Logger LOG = LoggerFactory.getLogger(ApiAuthenticationService.class);

	@Autowired
	private HttpServletRequest currentHttpRequest;

	@Autowired
	private RestOperations restClient;

	public Token authenticate(String userName, String password) {
		LOG.debug("Trying to authenticate user {} with BMSAPI to obtain a token.", userName);
		try {
			String bmsApiAuthURLFormat = "%s://%s:%s/bmsapi/brapi/v1/token";
			/**
			 * We want to make sure we construct the URL based on the server/port the request was received on. We want to hit the same
			 * server's authentication end point to obtain token. For servers in networks behind proxies and different cross network access
			 * rules etc, use of local loop back address and getLocalPort() ensures we always hit the correct server.
			 */
			String bmsApiAuthURL =
					String.format(bmsApiAuthURLFormat, this.currentHttpRequest.getScheme(), LOCAL_LOOPBACK_ADDRESS,
							this.currentHttpRequest.getLocalPort());

			HashMap<String, String> body = new HashMap<>();
			body.put("grant_type", "password");
			body.put("username", userName);
			body.put("password", password);
			body.put("client_id", "");

			final Token apiAuthToken = this.restClient.postForObject(bmsApiAuthURL, body, Token.class);

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
