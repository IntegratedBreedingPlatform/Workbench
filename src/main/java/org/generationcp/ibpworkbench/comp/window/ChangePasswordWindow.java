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

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.ibpworkbench.actions.ChangePasswordAction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable
public class ChangePasswordWindow extends Window implements IContentWindow, InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    private static final String VERSION = "1.1.3.10";
    
    private Label workbenchTitle;
    private Label passwordLabel;
    private Label confirmLabel;
    
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
        // workbench header components
        workbenchTitle = new Label("Change Password");
        workbenchTitle.setStyleName("gcp-window-title");

        passwordLabel = new Label("Password");
        confirmLabel = new Label("Change Password");
        
        workbenchTitle = new Label("Change Password");
        workbenchTitle.setStyleName("gcp-window-title");
        
        password = new PasswordField();
        confirm_password = new PasswordField();
        
        saveButton = new Button("Save");
      //  saveButton.setStyleName(BaseTheme.BUTTON_LINK);
        saveButton.setSizeUndefined();
        
        cancelButton = new Button("Cancel");
       // cancelButton.setStyleName(BaseTheme.BUTTON_LINK);
        cancelButton.setSizeUndefined();

    }

    protected void initializeLayout() {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        // add the vertical split panel
        layout.addComponent(workbenchTitle);
        
        layout.addComponent(passwordLabel);
        layout.addComponent(password);
        layout.addComponent(confirmLabel);
        layout.addComponent(confirm_password);
        
        layout.addComponent(saveButton);
        layout.addComponent(cancelButton);
       // layout.setComponentAlignment(saveButton, Alignment.CENTER);

       
        
     
        // layout the right area of the content area split panel
        // contentAreaSplitPanel.addComponent(workbenchDashboard);
       
        setContent(layout);
    }

    protected void initializeActions() {
    	String username = "aabro";
    	saveButton.addListener(new ChangePasswordAction(username,password, confirm_password));
       
    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showContent(Component content) {
		// TODO Auto-generated method stub
		
	}
 
   
}
