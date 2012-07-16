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

import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.commons.vaadin.actions.UpdateComponentLabelsAction;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.actions.LoginAction;
import org.generationcp.ibpworkbench.comp.window.LoginWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class IBPWorkbenchApplication extends SpringContextApplication{

    private static final long serialVersionUID = 1L;

    private final static Logger LOG = LoggerFactory.getLogger(IBPWorkbenchApplication.class);

    private LoginWindow loginWindow;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private UpdateComponentLabelsAction messageSourceListener;
    
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
}
