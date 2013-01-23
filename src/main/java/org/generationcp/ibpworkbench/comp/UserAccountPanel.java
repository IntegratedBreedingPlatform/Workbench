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
package org.generationcp.ibpworkbench.comp;

import java.util.Collection;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenLoginWindowAction;
import org.generationcp.ibpworkbench.actions.SaveUserAccountAction;
import org.generationcp.ibpworkbench.comp.form.UserAccountForm;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
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
    
    private UserAccountForm form;
    
    private Button saveButton;
    private Button cancelButton;
    
    private HorizontalLayout buttonLayout;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
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
        vl = new VerticalLayout();
        setContent(vl);
        
        form = new UserAccountForm(new UserAccountModel());
        vl.addComponent(form);
        
        saveButton = new Button();
        cancelButton = new Button();
        buttonLayout = new HorizontalLayout();
        buttonLayout.addComponent(saveButton);
        buttonLayout.addComponent(cancelButton);
        vl.addComponent(buttonLayout);
    }
    
    protected void initializeValues() {
    	//set default value for Security Question
        ComboBox questionField = (ComboBox) form.getField("securityQuestion");
        Collection<?> itemIds = questionField.getItemIds();
        questionField.setValue(itemIds.iterator().next());
    }

    protected void initializeLayout() {
        setImmediate(false);
        setWidth("675px");
        
        form.setHeight("400px");
        
        vl.setMargin(true);
        vl.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        vl.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
        
        buttonLayout.setWidth("140px");
    }

    protected void initializeActions() {
        saveButton.addListener(new SaveUserAccountAction(form));
        cancelButton.addListener(new OpenLoginWindowAction());
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    public void updateLabels() {
        messageSource.setCaption(saveButton, Message.SAVE);
        messageSource.setCaption(cancelButton, Message.CANCEL);
        messageSource.setCaption(form, Message.REGISTER_USER_ACCOUNT_FORM);
    }
}
