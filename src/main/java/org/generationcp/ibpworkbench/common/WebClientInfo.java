package org.generationcp.ibpworkbench.common;

import com.vaadin.terminal.gwt.server.WebBrowser;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class WebClientInfo {

	private WebBrowser webBrowser;

	public WebBrowser getWebBrowser() {
		return webBrowser;
	}

	public void setWebBrowser(WebBrowser webBrowser) {
		this.webBrowser = webBrowser;
	}
}
