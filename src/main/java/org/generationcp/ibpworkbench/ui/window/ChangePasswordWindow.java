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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.ChangePasswordAction;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ChangePasswordWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {
    @Autowired
    private SessionData sessionData;

    private static final long serialVersionUID = 1L;

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
        this.setOverrideFocus(true);
        this.addStyleName(Reindeer.WINDOW_LIGHT);
        this.setCaption("Change Password");

        passwordLabel = new Label("&nbsp;&nbsp;&nbsp;Password: &nbsp;&nbsp;", Label.CONTENT_XHTML);
        confirmLabel = new Label("&nbsp;&nbsp;&nbsp;Confirm Password :&nbsp;&nbsp;", Label.CONTENT_XHTML);
        passwordLabel.setStyleName("v-label");
        confirmLabel.setStyleName("v-label");
        
        password = new PasswordField();
        password.focus();
        
        confirm_password = new PasswordField();
        
        saveButton = new Button("Save");
        saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        
        cancelButton = new Button("Cancel");
    }

    protected void initializeLayout() {
        this.setWidth("335px");
        this.setHeight("200px");
        this.setModal(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);

        passwordLabel.setWidth("140px");
        password.setWidth("140px");
        confirmLabel.setWidth("140px");
        confirm_password.setWidth("140px");
        
        GridLayout passwordGridLayout = new GridLayout(2, 2);
        passwordGridLayout.setMargin(false, false, false, false);
        passwordGridLayout.addComponent(passwordLabel);
        passwordGridLayout.addComponent(password);
        passwordGridLayout.addComponent(confirmLabel);
        passwordGridLayout.addComponent(confirm_password);
        passwordGridLayout.setSizeFull();
        
        
        HorizontalLayout buttonlayout = new HorizontalLayout();

        buttonlayout.addComponent(cancelButton);
        buttonlayout.addComponent(saveButton);
        buttonlayout.setSpacing(true);

        layout.addComponent(passwordGridLayout);
        layout.addComponent(buttonlayout);
        layout.setComponentAlignment(buttonlayout, Alignment.MIDDLE_CENTER);
        layout.setExpandRatio(passwordGridLayout,1.0F);

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
        // currently does nothing
    }

}
