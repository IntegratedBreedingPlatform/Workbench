
package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.api.brapi.v1.common.Metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

	private Metadata metadata;

	private String userDisplayName;

	@JsonProperty("access_token")
	private String token;

	@JsonProperty("expires_in")
	private long expires;

	public Token() {

	}

	public Token(final String token, final long expires) {
		this.token = token;
		this.expires = expires;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public long getExpires() {
		return this.expires;
	}

	public void setExpires(final long expires) {
		this.expires = expires;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

}
