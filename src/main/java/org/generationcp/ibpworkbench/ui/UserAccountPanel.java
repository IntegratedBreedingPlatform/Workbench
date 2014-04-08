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
package org.generationcp.ibpworkbench.ui;

import java.util.Collection;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenLoginWindowAction;
import org.generationcp.ibpworkbench.actions.SaveUserAccountAction;
import org.generationcp.ibpworkbench.ui.form.UserAccountForm;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;


/**
 * <b>Description</b>: Panel for displaying UserAccountForm.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 17, 2012
 */
@Configurable
public class UserAccountPanel extends Panel {

    private static final long serialVersionUID = 1013885188470873112L;

    private VerticalLayout vl;
    private VerticalLayout rootLayout;
    private UserAccountForm userForm;
    
    private Button saveButton;
    private Button cancelButton;
    
    private HorizontalLayout buttonLayout;
    private HorizontalLayout spaceLayout;
    private HorizontalLayout smallSpaceLayout;
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    private Label lblTitle;
    private Label lblBanner;
    public UserAccountPanel() {
        assemble();
    }


    protected void assemble() {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }


    protected void initializeComponents() {
    	
    	VerticalLayout loginPanelLayout = new VerticalLayout();
    	loginPanelLayout.setHeight("87px");
        loginPanelLayout.setImmediate(false);
        loginPanelLayout.setStyleName("gcp-login");

        lblBanner = new Label();
        lblBanner.setWidth("100%");
        lblBanner.setStyleName("gcp-login-title gcp-user-account-title");
        
        loginPanelLayout.addComponent(lblBanner);
        
        rootLayout = new VerticalLayout();
        vl = new VerticalLayout();
        
        final Panel p = new Panel();
        p.setStyleName("form-panel");
        p.setSizeFull();
        
        userForm = new UserAccountForm(new UserAccountModel());
      
        vl.setSizeFull();
        vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
        vl.addComponent(userForm);
        vl.setSpacing(true);
        vl.setMargin(false);
        
        p.addComponent(vl);
        p.setWidth("95%");
       
      
        saveButton = new Button();
        saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        cancelButton = new Button();
        buttonLayout = new HorizontalLayout();
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveButton);
        buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        buttonLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonLayout.setWidth("140px");
        buttonLayout.setMargin(true, false, false, false);
      
        //rootLayout.setMargin(new Layout.MarginInfo(false,true,true,true));
        rootLayout.setSpacing(true);
        lblTitle = new Label("Register a New User Account");
        lblTitle.setStyleName(Bootstrap.Typography.H4.styleName());
        lblTitle.setWidth("95%");
    
        rootLayout.addComponent(loginPanelLayout);
        rootLayout.addComponent(lblTitle);
        rootLayout.addComponent(p);
        rootLayout.addComponent(buttonLayout);
        rootLayout.setComponentAlignment(lblTitle, Alignment.MIDDLE_CENTER);
        rootLayout.setComponentAlignment(p, Alignment.MIDDLE_CENTER);
        rootLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
        rootLayout.setStyleName("v-panel-content-gcp-createuser");
        
        setContent(rootLayout);
        
    }
    
    protected void initializeValues() {
    	//set default value for Security Question
        ComboBox questionField = (ComboBox) userForm.getField("securityQuestion");
        Collection<?> itemIds = questionField.getItemIds();
        questionField.setValue(itemIds.iterator().next());
    }

    protected void initializeLayout() {
        setImmediate(false);
        setWidth("820px");
        setHeight("620px");
    }

    protected void initializeActions() {
        saveButton.addListener(new SaveUserAccountAction(userForm));
        cancelButton.addListener(new OpenLoginWindowAction());
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    public void updateLabels() {
    	messageSource.setValue(lblBanner,Message.LOGIN_TITLE);
        messageSource.setCaption(saveButton, Message.SAVE);
        messageSource.setCaption(cancelButton, Message.CANCEL);
        //messageSource.setCaption(userForm, Message.REGISTER_USER_ACCOUNT_FORM);
    }
}
