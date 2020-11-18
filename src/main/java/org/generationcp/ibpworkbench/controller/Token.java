
package org.generationcp.ibpworkbench.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

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

}
