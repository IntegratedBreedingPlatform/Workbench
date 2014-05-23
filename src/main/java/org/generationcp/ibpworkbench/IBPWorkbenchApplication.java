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
import org.generationcp.ibpworkbench.ui.window.LoginWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IBPWorkbenchApplication extends SpringContextApplication implements IWorkbenchSession {

    private static final long serialVersionUID = 1L;
    private final static Logger LOG = LoggerFactory.getLogger(IBPWorkbenchApplication.class);

    private LoginWindow loginWindow;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    private UpdateComponentLabelsAction messageSourceListener;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private boolean jiraSetupDone = false;

    @Override
    public void close() {
        super.close();

        // implement this when we need to do something on session timeout

        messageSource.removeListener(messageSourceListener);

        LOG.debug("Application closed");
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

    public static IBPWorkbenchApplication get() {
        return get(IBPWorkbenchApplication.class);
    }

    @Override
    protected void doOnRequestStart(HttpServletRequest request, HttpServletResponse response) {
        super.doOnRequestStart(request, response);

        this.response = response;
        this.request = request;
        //LOG.trace("Request started " + request.getRequestURI() + "?" + request.getQueryString());

        //synchronized (this) {
        //    HttpRequestAwareUtil.onRequestStart(applicationContext, request, response);
        //}

        //IBPWorkbenchApplication.response = response;	// get a reference of the response
        //IBPWorkbenchApplication.request = request;
    }

    @Override
    protected void doOnRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        super.doOnRequestEnd(request, response);

        //LOG.trace("Request ended " + request.getRequestURI() + "?" + request.getQueryString());

        //synchronized (this) {
        //    HttpRequestAwareUtil.onRequestEnd(applicationContext, request, response);
        //}
    }

    @Override
    protected void initSpringApplication(ConfigurableWebApplicationContext context) {
        assemble();
    }

    protected void initialize() {
        setTheme("gcp-default");
    }

    protected void initializeComponents() {
        loginWindow = new LoginWindow();
    }

    protected void initializeLayout() {

    }

    protected void initializeActions() {
        //new LoginPresenter(loginWindow);
        
        messageSourceListener = new UpdateComponentLabelsAction(this);
        messageSource.addListener(messageSourceListener);
    }

    protected void assemble() {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();

        setMainWindow(loginWindow);
    }

    public void toggleJira() {
        jiraSetupDone = !jiraSetupDone;
    }

    @Override
    public Window getWindow(String name) {
        Window w = super.getWindow(name);

        if (w instanceof WorkbenchMainView && !jiraSetupDone) {
            // do script injection
            // attempt to add feedback js
            //final String jiraRatingsJSSrc ="http://jira.efficio.us.com/s/d41d8cd98f00b204e9800998ecf8427e/en_US-4nkfpc-1988229788/6144/3/1.4.0-m6/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?collectorId=3bcb8466";
            final String jiraSupportJSSrc = "https://pods.iplantcollaborative.org/jira/s/en_US-ihxzyo-418945332/852/5/1.2.9/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?collectorId=5f718b22";

            String script = "try{var fileref=document.createElement('script'); fileref.setAttribute(\"type\",\"text/javascript\"); fileref.setAttribute(\"src\", \" %s \"); document.getElementsByTagName(\"head\")[0].appendChild(fileref);}catch(e){alert(e);}";

            //w.executeJavaScript(String.format(script, jiraRatingsJSSrc) + String.format(script, jiraSupportJSSrc));
            w.executeJavaScript(String.format(script, jiraSupportJSSrc));

            this.jiraSetupDone = true;
        }


        return w;
    }
}
