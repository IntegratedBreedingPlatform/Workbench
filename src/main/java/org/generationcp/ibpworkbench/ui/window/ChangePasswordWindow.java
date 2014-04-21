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

package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.ChangePasswordAction;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class ChangePasswordWindow extends Window implements InitializingBean, InternationalizableComponent {
    @Autowired
    private SessionData sessionData;

    private static final long serialVersionUID = 1L;

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
        this.setCaption("Change Password");

        password = new PasswordField();
        password.setRequired(true);
        password.setRequiredError("Password cannot be blank");
        password.setCaption("Password");
        password.focus();
        
        confirm_password = new PasswordField();
        confirm_password.setCaption("Confirm Password");
        confirm_password.setRequired(true);
        confirm_password.setRequiredError("Confirm Password cannot be blank");

        saveButton = new Button("Save");
        saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        
        cancelButton = new Button("Cancel");
    }

    protected void initializeLayout() {
        this.setWidth("480px");
        this.setHeight("260px");
        this.setModal(true);
        this.setResizable(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        //layout.setWidth("100%");
        layout.setMargin(true);
        layout.setSpacing(true);

        // add the vertical split panel

        FormLayout formLayout = new FormLayout();
        formLayout.addComponent(password);
        formLayout.addComponent(confirm_password);

        Panel formContainer = new Panel();
        formContainer.setStyleName("form-panel");
        formContainer.setSizeFull();
        formContainer.addComponent(formLayout);

        HorizontalLayout buttonlayout = new HorizontalLayout();

        buttonlayout.addComponent(cancelButton);
        buttonlayout.addComponent(saveButton);
        buttonlayout.setSpacing(true);

        layout.addComponent(formContainer);
        layout.addComponent(buttonlayout);
        layout.setComponentAlignment(buttonlayout, Alignment.MIDDLE_CENTER);
        layout.setExpandRatio(formContainer,1.0F);

        this.setContent(layout);
    }

    protected void initializeActions() {
        User user = sessionData.getUserData();
        
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
