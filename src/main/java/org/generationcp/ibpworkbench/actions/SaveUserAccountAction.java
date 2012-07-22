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

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.WorkbenchManagerFactory;
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
    private WorkbenchManagerFactory workbenchManagerFactory;
    
    public SaveUserAccountAction(Form userAccountForm) {
        this.userAccountForm = userAccountForm;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        
        @SuppressWarnings("unchecked")
        BeanItem<UserAccountModel> bean =  (BeanItem<UserAccountModel>) userAccountForm.getItemDataSource();
        UserAccountModel userAccount = bean.getBean();
        
        userAccountForm.commit();
        try {
            saveUserAccount(userAccount);
        } catch (QueryException e) {
            if(LOG.isErrorEnabled()) {
                LOG.error(e.getMessage());
            }
            return;
        }
        
        OpenLoginWindowAction action = new OpenLoginWindowAction();
        action.buttonClick(event);
        
    }

    private void saveUserAccount(UserAccountModel userAccount) throws QueryException {
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
        workbenchManagerFactory.getWorkBenchDataManager().addPerson(person);
        
        User user = new User();
        user.setPersonid(person.getId());
        user.setName(userAccount.getUsername());
        user.setPassword(userAccount.getPassword());
        user.setAccess(0);
        user.setAdate(0);
        user.setCdate(0);
        user.setInstalid(0);
        user.setStatus(0);
        user.setType(0);
        workbenchManagerFactory.getWorkBenchDataManager().addUser(user);
    }

}
