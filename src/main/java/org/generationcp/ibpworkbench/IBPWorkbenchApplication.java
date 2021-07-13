/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench;

import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Window;
import org.dellroad.stuff.vaadin.ContextApplication;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.ibpworkbench.common.WebClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IBPWorkbenchApplication extends SpringContextApplication {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(IBPWorkbenchApplication.class);

	@Resource
	private LogoutHandler rememberMeServices;

	@Resource
	private WebClientInfo webClientInfo;

	private HttpServletRequest request;
	private HttpServletResponse response;

	private boolean scriptsRun = false;
	public static final String PREFETCH_SCRIPT = "/ibpworkbench/VAADIN/js/prefetch-resources.js";
	public static final String SCRIPT =
			"try{var fileref=document.createElement('script'); fileref.setAttribute(\"type\",\"text/javascript\"); fileref.setAttribute(\"src\", \" %s \"); document.getElementsByTagName(\"head\")[0].appendChild(fileref);}catch(e){alert(e);}";

	public static IBPWorkbenchApplication get() {
		return ContextApplication.get(IBPWorkbenchApplication.class);
	}

	@Override
	public void close() {
		super.close();

		IBPWorkbenchApplication.LOG.debug("IBPWorkbenchApplication closed");
	}

	protected void logout() {
		final Authentication auth = this.getCurrentSecurityContext().getAuthentication();
		if (auth != null) {
			this.rememberMeServices.logout(this.getRequest(), this.getResponse(), auth);
		}
		this.getCurrentSecurityContext().setAuthentication(null);
	}

	protected SecurityContext getCurrentSecurityContext() {
		return SecurityContextHolder.getContext();
	}

	@Override
	public void terminalError(final com.vaadin.terminal.Terminal.ErrorEvent event) {
		IBPWorkbenchApplication.LOG.error("Encountered error", event.getThrowable());
	}

	public HttpServletResponse getResponse() {
		return this.response;
	}

	public HttpServletRequest getRequest() {
		return this.request;
	}

	@Override
	protected void doOnRequestStart(final HttpServletRequest request, final HttpServletResponse response) {
		super.doOnRequestStart(request, response);

		this.response = response;
		this.request = request;
	}

	@Override
	protected void initSpringApplication(final ConfigurableWebApplicationContext context) {
		this.assemble();
	}

	protected void initialize() {
		this.setTheme("gcp-default");
	}

	protected void assemble() {
		this.initialize();
		this.setMainWindow(new Window());
	}

	public void toggleScripts() {
		this.scriptsRun = !this.scriptsRun;
	}

	@Override
	public Window getWindow(final String name) {

		webClientInfo.setWebBrowser((WebBrowser) this.getMainWindow().getTerminal());

		final Window w = super.getWindow(name);

		if (!this.scriptsRun) {
			w.executeJavaScript(String.format(SCRIPT, PREFETCH_SCRIPT));
			this.scriptsRun = true;
		}

		return w;
	}

	boolean isScriptsRun() {
		return scriptsRun;
	}
}
