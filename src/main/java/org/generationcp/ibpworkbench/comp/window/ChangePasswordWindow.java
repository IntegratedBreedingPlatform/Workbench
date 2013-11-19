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

package org.generationcp.ibpworkbench.comp.window;

import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.actions.ChangePasswordAction;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable
public class ChangePasswordWindow extends Window implements InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    private Label workbenchTitle;
    private Label passwordLabel;
    private Label confirmLabel;
    private Label gap;
    private Label space;
    
    private Button cancelButton;
    private Button saveButton;
    
    private PasswordField password;
    private PasswordField confirm_password;

    public ChangePasswordWindow() {
    }

    /**
     * Assemble the UI after all dependencies has been set.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }

    protected void initializeComponents() throws Exception {
        this.addStyleName(Reindeer.WINDOW_LIGHT);

        // workbench header components
        workbenchTitle = new Label("&nbsp;Change Password", Label.CONTENT_XHTML);
        workbenchTitle.setStyleName("gcp-window-title");

        gap = new Label();
        gap.setHeight("1em");
        
        space = new Label();
        space.setWidth("1em");
        

        passwordLabel = new Label("&nbsp;&nbsp;&nbsp;Password: &nbsp;&nbsp;", Label.CONTENT_XHTML);
        confirmLabel = new Label("&nbsp;&nbsp;&nbsp;Confirm Password :&nbsp;&nbsp;", Label.CONTENT_XHTML);
        passwordLabel.setStyleName("v-label");
        confirmLabel.setStyleName("v-label");
        
        password = new PasswordField();
        password.focus();
        
        confirm_password = new PasswordField();
        
        saveButton = new Button("Save");
        saveButton.setSizeUndefined();
        
        cancelButton = new Button("Cancel");
        cancelButton.setSizeUndefined();

    }

    protected void initializeLayout() {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        // add the vertical split panel
       
        layout.addComponent(gap);
        layout.addComponent(workbenchTitle);
        
        gap = new Label();
        gap.setHeight("1em");
        layout.addComponent(gap);
        
        HorizontalLayout fieldslayout = new HorizontalLayout();
        fieldslayout.addComponent(passwordLabel);
        fieldslayout.addComponent(password);
        fieldslayout.addComponent(confirmLabel);
        fieldslayout.addComponent(confirm_password);
        
        
        HorizontalLayout buttonlayout = new HorizontalLayout();
        space = new Label();
        space.setWidth("1em");
        buttonlayout.addComponent(space);
        buttonlayout.addComponent(saveButton);
        space = new Label();
        space.setWidth("1em");
        buttonlayout.addComponent(space);
        buttonlayout.addComponent(cancelButton);
        
        layout.addComponent(fieldslayout);
        layout.addComponent(buttonlayout);
       // layout.setComponentAlignment(saveButton, Alignment.CENTER);
        
        setContent(layout);
    }

    protected void initializeActions() {
        User user = IBPWorkbenchApplication.get().getSessionData().getUserData();
        
        saveButton.addListener(new ChangePasswordAction(user.getName(),password, confirm_password));
        cancelButton.addListener(new RemoveWindowListener());
    }
    
    public class RemoveWindowListener implements ClickListener {
        private static final long serialVersionUID = 1L;

        @Override
        public void buttonClick(ClickEvent event) {
            event.getComponent().getWindow().getParent().removeWindow(getWindow());
        }

    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    @Override
    public void updateLabels() {
    }

}
