/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench;

import com.vaadin.ui.Window;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.commons.vaadin.actions.UpdateComponentLabelsAction;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IBPWorkbenchApplication extends SpringContextApplication implements IWorkbenchSession {

	private static final long serialVersionUID = 1L;
	private final static Logger LOG = LoggerFactory.getLogger(IBPWorkbenchApplication.class);

	@Resource
	private SimpleResourceBundleMessageSource messageSource;

	@Resource
	private SessionData sessionData;

	@Resource
	private LogoutHandler rememberMeServices;

	private UpdateComponentLabelsAction messageSourceListener;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private boolean jiraSetupDone = false;

	public static IBPWorkbenchApplication get() {
		return get(IBPWorkbenchApplication.class);
	}

	@Override
	public void close() {
		super.close();
		// implement this when we need to do something on session timeout
		messageSource.removeListener(messageSourceListener);

		this.logout();

		LOG.debug("IBPWorkbenchApplication closed");
	}

	protected void logout() {
		Authentication auth = this.getCurrentSecurityContext().getAuthentication();
		if (auth != null) {
			rememberMeServices.logout(this.getRequest(), this.getResponse(), auth);
		}
		this.getCurrentSecurityContext().setAuthentication(null);
	}

	protected SecurityContext getCurrentSecurityContext() {
		return SecurityContextHolder.getContext();
	}


	@Override
	public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
		LOG.error("Encountered error", event.getThrowable());
	}

	@Override
	public SessionData getSessionData() {
		return sessionData;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	protected void doOnRequestStart(HttpServletRequest request, HttpServletResponse response) {
		super.doOnRequestStart(request, response);

		this.response = response;
		this.request = request;
	}

	@Override
	protected void doOnRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		super.doOnRequestEnd(request, response);
	}

	@Override
	protected void initSpringApplication(ConfigurableWebApplicationContext context) {
		assemble();
	}

	protected void initialize() {
		setTheme("gcp-default");
	}

	protected void initializeComponents() {

	}

	protected void initializeLayout() {

	}

	protected void initializeActions() {
		messageSourceListener = new UpdateComponentLabelsAction(this);
		messageSource.addListener(messageSourceListener);
	}

	protected void assemble() {
		initialize();
		initializeComponents();
		initializeLayout();
		initializeActions();

		setMainWindow(new WorkbenchMainView());
	}

	public void toggleJira() {
		jiraSetupDone = !jiraSetupDone;
	}

	@Override
	public Window getWindow(String name) {
		Window w = super.getWindow(name);

		final String prefetch_script = "/ibpworkbench/VAADIN/js/prefetch-resources.js";

		final String script = "try{var fileref=document.createElement('script'); fileref.setAttribute(\"type\",\"text/javascript\"); fileref.setAttribute(\"src\", \" %s \"); document.getElementsByTagName(\"head\")[0].appendChild(fileref);}catch(e){alert(e);}";

		if (!jiraSetupDone) {
			w.executeJavaScript(String.format(script, prefetch_script));
		}

		if (w instanceof WorkbenchMainView && !jiraSetupDone) {
			final String jiraSupportJSSrc = "https://pods.iplantcollaborative.org/jira/s/en_US-ihxzyo-418945332/852/5/1.2.9/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?collectorId=5f718b22";

			w.executeJavaScript(String.format(script, jiraSupportJSSrc));

			this.jiraSetupDone = true;
		}

		return w;
	}
}
