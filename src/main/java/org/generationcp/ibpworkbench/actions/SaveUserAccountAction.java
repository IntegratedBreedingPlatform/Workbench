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
package org.generationcp.ibpworkbench.actions;

import java.util.Date;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;


/**
 * <b>Description</b>: Listerner responsible for saving new Users and Persons record.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 17, 2012
 */
@Configurable
public class SaveUserAccountAction implements ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(SaveUserAccountAction.class);
    
    private static final long serialVersionUID = 5386242653138617919L;

    private Form userAccountForm;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public SaveUserAccountAction(Form userAccountForm) {
        this.userAccountForm = userAccountForm;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        
        @SuppressWarnings("unchecked")
        BeanItem<UserAccountModel> bean =  (BeanItem<UserAccountModel>) userAccountForm.getItemDataSource();
        UserAccountModel userAccount = bean.getBean();
        
        try {
        	userAccountForm.commit();
        } catch (Exception e) {
            //TODO: investigate. Exception still not properly handled.
            //vaadin shows an "Internal Error" message
            if (e instanceof InternationalizableException) {                
                InternationalizableException i = (InternationalizableException) e;

                MessageNotifier.showError(event.getComponent().getWindow(),
                        i.getCaption(), 
                        i.getDescription());
            } else if(e instanceof InvalidValueException){
            	String errorMessage=e.getMessage();
            	
            	if(errorMessage==null){
            		errorMessage="Please enter valid Email Address";
            	}

                MessageNotifier.showWarning(this.userAccountForm.getWindow(),
                        errorMessage,
                        "");

            } else {
                MessageNotifier.showError(event.getComponent().getWindow(), 
                        e.getMessage(), 
                        "");
            }
            return;
        }
        
        try {
            saveUserAccount(userAccount);
        } catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while trying to save user account details.", e);
            MessageNotifier.showError(event.getComponent().getWindow(), 
                    messageSource.getMessage(Message.DATABASE_ERROR), 
                    messageSource.getMessage(Message.SAVE_USER_ACCOUNT_ERROR_DESC));
            return;
        }
        
        // Just attempt to log... user will be null if session has just started,
        // and currentProject will be null when theres no last opened project
        try {
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            User user = app.getSessionData().getUserData();
            Project currentProject = app.getSessionData().getLastOpenedProject();

            if (currentProject != null) {
                ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Program Member", "Added a new user (" + userAccount.getUsername()
                        + ") to " + currentProject.getProjectName(), user, new Date());
                workbenchDataManager.addProjectActivity(projAct);
            }
        } catch (MiddlewareQueryException e) {
            LOG.error("Cannot register program ectivity", e);
        }
        
        //OpenLoginWindowFromRegistrationAction action = new OpenLoginWindowFromRegistrationAction();
        //GCP:5025
        LoginPresenter loginPresenter = LoginPresenter.getLoginActionInstance();
        loginPresenter.doLogin(userAccount.getUsername(),userAccount.getPassword(),event);	// Attempt to auto login
        
        //action.buttonClick(event);
        
    }

    private void saveUserAccount(UserAccountModel userAccount) throws MiddlewareQueryException {
        userAccount.trimAll();
        
        Person person = new Person();
        person.setFirstName(userAccount.getFirstName());
        person.setMiddleName(userAccount.getMiddleName());
        person.setLastName(userAccount.getLastName());
        person.setEmail(userAccount.getEmail());
        person.setTitle("-");
        person.setContact("-");
        person.setExtension("-");
        person.setFax("-");
        person.setInstituteId(0);
        person.setLanguage(0);
        person.setNotes("-");
        person.setPositionName("-");
        person.setPhone("-");
        workbenchDataManager.addPerson(person);
        
        User user = new User();
        user.setPersonid(person.getId());
        user.setPerson(person);
        user.setName(userAccount.getUsername());
        user.setPassword(userAccount.getPassword());
        user.setAccess(0);
        user.setAdate(0);
        user.setCdate(0);
        user.setInstalid(0);
        user.setStatus(0);
        user.setType(0);
        workbenchDataManager.addUser(user);
        
        SecurityQuestion question = new SecurityQuestion();
        question.setUserId(user.getUserid());
        question.setSecurityQuestion(userAccount.getSecurityQuestion());
        question.setSecurityAnswer(userAccount.getSecurityAnswer());
        workbenchDataManager.addSecurityQuestion(question);
    }

}
