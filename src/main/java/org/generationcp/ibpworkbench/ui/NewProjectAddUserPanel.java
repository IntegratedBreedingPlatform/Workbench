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

import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CloseWindowAction;
import org.generationcp.ibpworkbench.actions.SaveNewProjectAddUserAction;
import org.generationcp.ibpworkbench.ui.form.UserAccountForm;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;


/**
 * <b>Description</b>: Panel for displaying UserAccountForm in the AddUser pop-up window.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado
 * <br>
 * <b>File Created</b>: October 15, 2012
 */
@Configurable
public class NewProjectAddUserPanel extends Panel {

    private static final long serialVersionUID = 2187912990347713234L;

    private VerticalLayout vl;
    
    private UserAccountForm userForm;
    
    private Button saveButton;
    private Button cancelButton;
    
    private HorizontalLayout buttonLayout;
    
    private TwinColSelect membersSelect;
    
    private final static String[] VISIBLE_ITEM_PROPERTIES = new String[] { 
        "firstName", "middleName", "lastName", "email", 
        "username", "securityQuestion",  "securityAnswer"};
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public NewProjectAddUserPanel(TwinColSelect membersSelect) {
        this.membersSelect = membersSelect;
        
        assemble();
    }

    protected void assemble() {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }

    protected void initializeComponents() {
        vl = new VerticalLayout();
        setContent(vl);
        
        userForm = new UserAccountForm(new UserAccountModel());
        userForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
        vl.addComponent(userForm);
        
        saveButton = new Button();
        cancelButton = new Button();
        buttonLayout = new HorizontalLayout();
        buttonLayout.addComponent(saveButton);
        buttonLayout.addComponent(cancelButton);
        vl.addComponent(buttonLayout);
    }
    
    protected void initializeValues() {
    	//set default value for Security Question
        ComboBox questionField = (ComboBox) userForm.getField("securityQuestion");
        Collection<?> itemIds = questionField.getItemIds();
        questionField.setValue(itemIds.iterator().next());
    }

    protected void initializeLayout() {
        setImmediate(false);
        setStyleName(Reindeer.PANEL_LIGHT);
        setWidth("675px");
        
        userForm.setHeight("320px");
        
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setComponentAlignment(userForm, Alignment.MIDDLE_CENTER);
        vl.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
        
        buttonLayout.setWidth("140px");
    }

    protected void initializeActions() {
        saveButton.addListener(new SaveNewProjectAddUserAction(userForm, membersSelect));
        saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

        cancelButton.addListener(new CloseWindowAction());
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    public void updateLabels() {
        messageSource.setCaption(saveButton, Message.SAVE);
        messageSource.setCaption(cancelButton, Message.CANCEL);
        messageSource.setCaption(userForm, Message.REGISTER_USER_ACCOUNT_FORM);
    }
    
    public UserAccountForm getForm() {
        return userForm;
    }
    
    public void refreshVisibleItems(){
        userForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
    }
}
