
package org.generationcp.ibpworkbench.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

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
	private RestOperations restClient;

	@Value("${bmsapi.url}")
	private String apiUrl;

	public Token authenticate(final String userName, final String password) {
		LOG.debug("Trying to authenticate user {} with BMSAPI to obtain a token.", userName);
		try {
			final TokenRequest tokenRequest = new TokenRequest(userName, password);
			final String bmsApiAuthURLFormat = this.apiUrl + "/brapi/v1/token";
			/**
			 * We want to make sure we construct the URL based on the server/port the request was received on. We want to hit the same
			 * server's authentication end point to obtain token. For servers in networks behind proxies and different cross network access
			 * rules etc, use of local loop back address and getLocalPort() ensures we always hit the correct server.
			 */
			final String bmsApiAuthURL = String.format(bmsApiAuthURLFormat);
			final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(bmsApiAuthURL);
			// Indicate that the components are already escaped
			final URI uri = builder.build(true).toUri();

			final Token apiAuthToken = this.restClient.postForObject(uri, tokenRequest, Token.class);
			if (apiAuthToken != null) {
				LOG.debug("Successfully authenticated and obtained a token from BMSAPI for user {}.", userName);
			}
			return apiAuthToken;
		} catch (RestClientException e) {
			LOG.debug("Error encountered while trying authenticate user {} with BMSAPI to obtain a token: {}", userName, e.getMessage());
			throw e;
		}
	}

	String encode(final String textToEncode) {
		try {
			// After the text is encoded, make sure that the space (escaped as "+" by URLEncoder) is replaced with "%20" so that
			// it will work with UriComponentsBuilder.build later.
			return URLEncoder.encode(textToEncode, "UTF-8").replaceAll("\\+", "%20");
		} catch (final UnsupportedEncodingException e) {
			LOG.debug("Error encountered while trying encode password: {}", e.getMessage());
		}
		return textToEncode;
	}

	void setRestClient(RestOperations restClient) {
		this.restClient = restClient;
	}

	void setApiUrl(final String apiUrl) {
		this.apiUrl = apiUrl;
	}
}
