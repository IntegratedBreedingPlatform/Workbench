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
package org.generationcp.ibpworkbench.ui.form;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.model.formfieldfactory.UserAccountFormFieldFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


/**
 * <b>Description</b>: Custom form for registering a user account.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 11, 2012
 */
@Configurable
public class UserAccountForm extends Form {
	
    
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private static final long serialVersionUID = -7726164779128415602L;
    
    private BeanItem<UserAccountModel> userBean;
    
    private UserAccountModel userAccount;
    
    private Object[] visibleProperties;
    
    private Label lblName;
    private Label lblFirstName;
    private Label lblMiddleName;
    private Label lblLastName;
    
    private Label lblEmail;
    private Label lblUserName;
    private Label lblPassword;
    private Label lblPasswordConfirmation;
    private Label lblSecurityQuestion;
    private Label lblSecurityAnswer;
    
    
    private GridLayout grid;
    
    public UserAccountForm(UserAccountModel userAccount) {
        this.userAccount = userAccount;
        
        this.visibleProperties = new Object[] { "firstName", "middleName", "lastName", "email", 
                "username", "password", "passwordConfirmation",
                "securityQuestion", "securityAnswer"};
        super.setVisibleItemProperties(visibleProperties);
        
        assemble();
    }
    
    
    public UserAccountForm(UserAccountModel userAccount, Object[] visibleProperties){
    	this.userAccount = userAccount;
    	this.visibleProperties = visibleProperties;
    	super.setVisibleItemProperties(visibleProperties);
    	
    	assemble();
    }

    protected void assemble() {
        
        initializeComponents();
        initializeLayout();
    }

    protected void initializeComponents() {
        
    	setImmediate(false);
        grid = new GridLayout(5, 8);
        grid.setSpacing(true);
        grid.setMargin(new Layout.MarginInfo(true,true,true,true));
        grid.setWidth("100%");
        
        setLayout(grid);
        
        userBean = new BeanItem<UserAccountModel>(userAccount);
        
        setItemDataSource(userBean);
        setComponentError(null);
        setFormFieldFactory(new UserAccountFormFieldFactory());
        setVisibleItemProperties(visibleProperties);
        
        setWriteThrough(false);
        setInvalidCommitted(false);
        setValidationVisibleOnCommit(false);
        
    }
    
    protected void initializeLayout() {
        
    }
    
    @Override
    protected void attachField(Object propertyId, Field field) {
        
        if ("firstName".equals(propertyId)) {
            grid.addComponent(field, 1, 0);
        } else if ("middleName".equals(propertyId)) {
            grid.addComponent(field, 2, 0);
        } else if ("lastName".equals(propertyId)) {
            grid.addComponent(field, 3, 0);
        } else if ("email".equals(propertyId)) {
            grid.addComponent(field, 1, 2, 2, 2);
        } else if ("username".equals(propertyId)) {
            grid.addComponent(field, 1, 3, 2, 3);
        } else if ("password".equals(propertyId)) {
            grid.addComponent(field, 1, 4);
        } else if ("passwordConfirmation".equals(propertyId)) {
            grid.addComponent(field, 1, 5);
        } else if ("securityQuestion".equals(propertyId)) {
            grid.addComponent(field, 1, 6, 3, 6);
        } else if ("securityAnswer".equals(propertyId)) {
            grid.addComponent(field, 1, 7, 3, 7);
        } 
    }
    
    @Override
	public void attach() {
		// TODO Auto-generated method stub
    	
    	
    	lblName = createLabel();
    	lblName.setValue(messageSource.getMessage(Message.USER_ACC_NAME));
    	
    	lblFirstName = createLabel();
    	lblFirstName.setValue(messageSource.getMessage(Message.USER_ACC_FNAME));
    	lblMiddleName = createLabel();
    	lblMiddleName.setValue(messageSource.getMessage(Message.USER_ACC_MIDNAME));
    	lblLastName = createLabel();
    	lblLastName.setValue(messageSource.getMessage(Message.USER_ACC_LNAME));
    	
        lblEmail = createLabel();
        lblEmail.setValue(messageSource.getMessage(Message.USER_ACC_EMAIL));
        lblUserName = createLabel();
        lblUserName.setValue(messageSource.getMessage(Message.USER_ACC_USERNAME));
        lblPassword = createLabel();
        lblPassword.setValue(messageSource.getMessage(Message.USER_ACC_PASSWORD));
        lblPasswordConfirmation = createLabel();        
        lblPasswordConfirmation.setValue(messageSource.getMessage(Message.USER_ACC_PASSWORD_CONFIRM));
        lblSecurityQuestion = createLabel();
        lblSecurityQuestion.setValue(messageSource.getMessage(Message.SECURITY_QUESTION));
        lblSecurityAnswer = createLabel();
        lblSecurityAnswer.setValue(messageSource.getMessage(Message.SECURITY_ANSWER));
        
        if (grid.getComponent(0, 0) == null) grid.addComponent(lblName, 0, 0);
        if (grid.getComponent(1, 1) == null) grid.addComponent(lblFirstName, 1, 1);
        grid.setComponentAlignment(lblFirstName, Alignment.TOP_LEFT);
        if (grid.getComponent(2, 1) == null) grid.addComponent(lblMiddleName, 2, 1);
        grid.setComponentAlignment(lblMiddleName, Alignment.TOP_LEFT);
        if (grid.getComponent(3, 1) == null) grid.addComponent(lblLastName, 3, 1);
        grid.setComponentAlignment(lblLastName, Alignment.TOP_LEFT);
        
        
        if (grid.getComponent(0, 2) == null) grid.addComponent(lblEmail, 0, 2);
        if (grid.getComponent(0, 3) == null) grid.addComponent(lblUserName, 0, 3);
        if (this.getVisibleItemProperties().contains("password")){
    		if (grid.getComponent(0, 4) == null) grid.addComponent(lblPassword, 0, 4);
    	}
        if (this.getVisibleItemProperties().contains("passwordConfirmation")){
    		if (grid.getComponent(0, 5) == null) grid.addComponent(lblPasswordConfirmation, 0, 5);
    	}
        if (grid.getComponent(0, 6) == null) grid.addComponent(lblSecurityQuestion, 0, 6);
        if (grid.getComponent(0, 7) == null) grid.addComponent(lblSecurityAnswer, 0, 7);
    	
    
        //grid.setColumnExpandRatio(0, 2f);
        
		super.attach();
		
	}
    
    private Label createLabel(){
    	
    	Label label = new Label();
    	label.setContentMode(Label.CONTENT_XHTML);
    	label.setWidth("150px");
    	return label;
    }
    
}
