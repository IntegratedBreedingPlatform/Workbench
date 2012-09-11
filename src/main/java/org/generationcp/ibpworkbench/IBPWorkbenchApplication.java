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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.commons.hibernate.util.HttpRequestAwareUtil;
import org.generationcp.commons.vaadin.actions.UpdateComponentLabelsAction;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.actions.LoginAction;
import org.generationcp.ibpworkbench.comp.window.LoginWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class IBPWorkbenchApplication extends SpringContextApplication implements ApplicationContextAware {

    private static final long serialVersionUID = 1L;

    private final static Logger LOG = LoggerFactory.getLogger(IBPWorkbenchApplication.class);

    private LoginWindow loginWindow;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private UpdateComponentLabelsAction messageSourceListener;
    
    private SessionData sessionData = new SessionData();

    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    public SessionData getSessionData() {
        return sessionData;
    }
    
    public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
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
        new LoginAction(loginWindow);
        
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
    
    public static IBPWorkbenchApplication get() {
        return get(IBPWorkbenchApplication.class);
    }
    
    @Override
    protected void doOnRequestStart(HttpServletRequest request, HttpServletResponse response) {
        super.doOnRequestStart(request, response);
        
        LOG.trace("Request started " + request.getRequestURI() + "?" + request.getQueryString());
        
        synchronized (this) {
            HttpRequestAwareUtil.onRequestStart(applicationContext, request, response);
        }
    }
    
    @Override
    protected void doOnRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        super.doOnRequestEnd(request, response);
        
        LOG.trace("Request ended " + request.getRequestURI() + "?" + request.getQueryString());
        
        synchronized (this) {
            HttpRequestAwareUtil.onRequestEnd(applicationContext, request, response);
        }
    }
}
