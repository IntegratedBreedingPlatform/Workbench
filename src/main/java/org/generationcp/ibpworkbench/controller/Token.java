
package org.generationcp.ibpworkbench.controller;

public class Token {

	private String token;
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
