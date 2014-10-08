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
package org.generationcp.ibpworkbench.ui.programmembers;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CloseWindowAction;
import org.generationcp.ibpworkbench.actions.SaveNewProjectAddUserAction;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.ibpworkbench.ui.form.UserAccountForm;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collection;


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
    private VerticalLayout rootLayout;
    
    private UserAccountForm userForm;
    
    private Button saveButton;
    private Button cancelButton;
    
    private HorizontalLayout buttonLayout;
    
    private TwinTableSelect<User> membersSelect;
    
    private final static Object[] VISIBLE_ITEM_PROPERTIES = new Object[] { 
        "firstName", "middleName", "lastName", "email", 
        "username", "securityQuestion",  "securityAnswer"};
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public NewProjectAddUserPanel(TwinTableSelect<User> membersSelect) {
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
    	rootLayout = new VerticalLayout();
        vl = new VerticalLayout();
        
        final Panel p = new Panel();
        p.setStyleName("form-panel");
        p.setSizeFull();
        
        userForm = new UserAccountForm(new UserAccountModel(), VISIBLE_ITEM_PROPERTIES);
      
        vl.setSizeFull();
        vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
        vl.addComponent(userForm);
        vl.setSpacing(true);
        
        p.addComponent(vl);
      
        saveButton = new Button();
        cancelButton = new Button();
        buttonLayout = new HorizontalLayout();
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveButton);
        buttonLayout.setWidth("140px");
        buttonLayout.setMargin(true, false, false, false);
      
        rootLayout.setMargin(new Layout.MarginInfo(false,true,true,true));
        rootLayout.setSpacing(true);
        Label lblTitle = new Label("Register a New User Account");
        lblTitle.setStyleName(Bootstrap.Typography.H4.styleName());
        rootLayout.addComponent(lblTitle);
        rootLayout.addComponent(p);
        rootLayout.addComponent(buttonLayout);
        rootLayout.setComponentAlignment(p, Alignment.MIDDLE_CENTER);
        rootLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
       
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
        setStyleName(Reindeer.PANEL_LIGHT);
        setWidth("925px");
        
        
      
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
    }
    
    public UserAccountForm getForm() {
        return userForm;
    }
    
    public void refreshVisibleItems(){
        userForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
    }
}
