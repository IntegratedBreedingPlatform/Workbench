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

import java.util.Collection;
import java.util.HashSet;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.TwinColSelect;


/**
 * <b>Description</b>: Listener responsible for saving new Users and Persons records
 * created from the Create New Project screen.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado
 * <br>
 * <b>File Created</b>: October 15, 2012
 */
@Configurable
public class SaveNewProjectAddUserAction implements ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(SaveNewProjectAddUserAction.class);
    
    private static final long serialVersionUID = 5386242653138617919L;

    private Form userAccountForm;
    private TwinColSelect membersSelect;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public SaveNewProjectAddUserAction(Form userAccountForm, TwinColSelect membersSelect) {
        this.userAccountForm = userAccountForm;
        this.membersSelect = membersSelect;
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
                
            } else {
                
                MessageNotifier.showError(event.getComponent().getWindow(), 
                        e.getMessage(), 
                        "");
                
            }
            
            
            return;
        }
        
        try {
            saveUserAccount(userAccount, membersSelect);
        } catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while trying to save user account details.", e);
            MessageNotifier.showError(event.getComponent().getWindow(), 
                    messageSource.getMessage(Message.DATABASE_ERROR), 
                    messageSource.getMessage(Message.SAVE_USER_ACCOUT_ERROR_DESC));
            return;
        }
        
        CloseWindowAction action = new CloseWindowAction();
        action.buttonClick(event);
        
    }

    private void saveUserAccount(UserAccountModel userAccount, TwinColSelect membersSelect) throws MiddlewareQueryException {
        userAccount.trimAll();
        
        Person person = new Person();
        person.setFirstName(userAccount.getFirstName());
        person.setMiddleName(userAccount.getMiddleName());
        person.setLastName(userAccount.getLastName());
        person.setEmail(userAccount.getEmail());
        person.setTitle(userAccount.getPositionTitle());
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
        //set default password for the new user 
        user.setPassword(userAccount.getUsername());
        user.setAccess(0);
        user.setAdate(0);
        user.setCdate(0);
        user.setInstalid(0);
        user.setStatus(0);
        user.setType(0);
        workbenchDataManager.addUser(user);
        
        // add new user to the TwinColumnSelect
        membersSelect.addItem(user);
        membersSelect.setItemCaption(user, person.getDisplayName());
        // get currently selected users and add the new user
        @SuppressWarnings("unchecked")
        HashSet<User> selectedMembers = new HashSet<User>((Collection<User>) membersSelect.getValue());
        selectedMembers.add(user);
        membersSelect.setValue(selectedMembers);
    }

}
