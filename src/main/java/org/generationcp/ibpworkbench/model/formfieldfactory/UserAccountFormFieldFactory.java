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
package org.generationcp.ibpworkbench.model.formfieldfactory;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.validator.PersonNameValidator;
import org.generationcp.ibpworkbench.validator.UserPasswordValidator;
import org.generationcp.ibpworkbench.validator.UsernameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;


/**
 * <b>Description</b>: Field factory for generating Users and Persons fields for UserAccountForm class.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 16, 2012
 */
@Configurable
public class UserAccountFormFieldFactory extends DefaultFieldFactory{

    private static final long serialVersionUID = 3560059243526106791L;
    
    private Field firstName;
    private Field lastName;
    private Field middleName;
    private Field email;
    private Field username;
    
    private PasswordField password;
    private PasswordField passwordConfirmation;
    
    private ComboBox securityQuestion;
    private Field securityAnswer;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public UserAccountFormFieldFactory() {
        initFields();
    }
    
    private void initFields() {
        firstName = new TextField();  
        lastName = new TextField();
        firstName.addValidator(new PersonNameValidator(firstName, lastName));
        middleName = new TextField();
        email = new TextField();
        username = new TextField();
        password = new PasswordField();
        passwordConfirmation = new PasswordField();
        password.addValidator(new UserPasswordValidator(password, passwordConfirmation));
        securityQuestion = new ComboBox();
        securityAnswer = new TextField();
        
        int tabIndex = 100;
        firstName.setTabIndex(tabIndex++);
        middleName.setTabIndex(tabIndex++);
        lastName.setTabIndex(tabIndex++);       
        email.setTabIndex(tabIndex++);
        username.setTabIndex(tabIndex++);
        password.setTabIndex(tabIndex++);
        passwordConfirmation.setTabIndex(tabIndex++);
        securityQuestion.setTabIndex(tabIndex++);
        securityAnswer.setTabIndex(tabIndex++);
        
        
        firstName.setStyleName("hide-caption");
        lastName.setStyleName("hide-caption");
        middleName.setStyleName("hide-caption");
        email.setStyleName("hide-caption");
        username.setStyleName("hide-caption");
        password.setStyleName("hide-caption");
        passwordConfirmation.setStyleName("hide-caption");
        securityQuestion.setStyleName("hide-caption");
        securityAnswer.setStyleName("hide-caption");
    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
         
        Field field = super.createField(item, propertyId, uiContext);
        
        if ("firstName".equals(propertyId)) {
        	firstName.setRequired(true);
            firstName.setRequiredError("Please enter a First Name.");
            firstName.addValidator(new StringLengthValidator("First Name must be 1-20 characters.", 1, 20, false));
            return firstName;
        } else if ("lastName".equals(propertyId)) {
        	lastName.setRequired(true);
            lastName.setRequiredError("Please enter a Last Name.");
            lastName.addValidator(new StringLengthValidator("Last Name must be 1-50 characters.", 1, 50, false));
            return lastName;
        } else if ("middleName".equals(propertyId)) {
            middleName.setRequired(false);
            middleName.addValidator(new StringLengthValidator("Middle Name must be 1-15 characters.", 1, 15, false));
           return middleName;
        } else if ("email".equals(propertyId)) {
        	email.setRequired(true);
        	email.setRequiredError("Please enter an Email Address.");
        	email.setWidth("80%");
        	email.addValidator(new StringLengthValidator("Email Address must be 5-40 characters.", 5, 40, false));
        	email.addValidator(new EmailValidator("Please enter a valid Email Address."));
        	return email;
        } else if ("username".equals(propertyId)) {
        	username.setRequired(true);
        	username.setRequiredError("Please enter a Username.");
        	username.setWidth("80%");
        	username.addValidator(new StringLengthValidator("Username must be 1-30 characters.", 1, 30, false));
        	username.addValidator(new UsernameValidator());
        	return username;
        } else if ("password".equals(propertyId)) {
            password.setRequired(true);
            password.setRequiredError("Please enter a Password.");
            password.addValidator(new StringLengthValidator("Password must be 1-30 characters.", 1, 30, false));
            return password;
        } else if ("passwordConfirmation".equals(propertyId)) {
        	passwordConfirmation.setRequired(true);
            passwordConfirmation.setRequiredError("Please re-type your password for confirmation.");
            passwordConfirmation.addValidator(new StringLengthValidator("Password confirmation must be 1-30 characters.", 1, 30, false));
            return passwordConfirmation;
        } else if ("securityQuestion".equals(propertyId)) {
            securityQuestion.setWidth("100%");
            securityQuestion.setNullSelectionAllowed(false);
            securityQuestion.setRequired(true);
            securityQuestion.setRequiredError("Please specify a security question that only you can answer.");
            
            securityQuestion.setTextInputAllowed(false);
            securityQuestion.addItem("What is your first pet's name?");
            securityQuestion.addItem("What is your mother's maiden name?");
            securityQuestion.addItem("What is your town of birth?");
            
            return securityQuestion;
        } else if ("securityAnswer".equals(propertyId)) {
        	securityAnswer.setWidth("100%");
        	securityAnswer.setRequired(true);
        	securityAnswer.setRequiredError("Please enter the answer to your security question.");
            securityAnswer.addValidator(new StringLengthValidator("Security Answer must be 1-255 characters.", 1, 255, false));
            return securityAnswer;
        } 
        
        return field;
    }
    
    
}
