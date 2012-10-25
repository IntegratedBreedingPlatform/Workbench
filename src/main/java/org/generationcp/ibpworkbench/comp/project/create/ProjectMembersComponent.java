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
package org.generationcp.ibpworkbench.comp.project.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;


/**
 * The third tab (Project Members) in Create Project Accordion Component.
 * 
 * @author Joyce Avestro
 */
@SuppressWarnings("unchecked")
@Configurable
public class ProjectMembersComponent extends VerticalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(ProjectMembersComponent.class);
    private static final long serialVersionUID = 1L;
    

    private CreateProjectPanel createProjectPanel;
    private TwinColSelect select;
    
    private Button previousButton;
    private Button nextButton;
    private Component buttonArea;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    

    public ProjectMembersComponent(CreateProjectPanel createProjectPanel) {
        this.createProjectPanel = createProjectPanel;
    }


    @Override
    public void afterPropertiesSet() throws Exception {       
        assemble();
    }

    protected void assemble() {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }

    protected void initializeComponents(){

        setSpacing(true);
        setMargin(true);
        
        select = new TwinColSelect("Project Members");
        select.setLeftColumnCaption("Available Users");
        select.setRightColumnCaption("Selected Project Members");
        select.setRows(10);
        select.setWidth("400px");
        select.setMultiSelect(true);
        select.setNullSelectionAllowed(true);
        
        addComponent(select);
        
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
        
    }


    protected void initializeValues() {
        try {
            Container container = createUsersContainer();
            select.setContainerDataSource(container);

            for (Object itemId : container.getItemIds()) {
                User user = (User) itemId;
                select.setItemCaption(itemId, user.getPerson().getDisplayName());
            }
        }
        catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting workbench users", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, 
                                                   Message.CONTACT_ADMIN_ERROR_DESC);
        }

    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
    }

    protected void initializeActions() {
        previousButton.addListener(new PreviousButtonClickListener());
        nextButton.addListener(new NextButtonClickListener());
    }
    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        previousButton = new Button("Previous");
        nextButton = new Button("Next");
        buttonLayout.addComponent(previousButton);
        buttonLayout.addComponent(nextButton);
        return buttonLayout;
    }
    

    private Container createUsersContainer() throws MiddlewareQueryException {
        List<User> validUserList = new ArrayList<User>();
        
        // TODO: This can be improved once we implement proper User-Person mapping
        List<User> userList = workbenchDataManager.getAllUsers();
        for (User user : userList) {
            Person person = workbenchDataManager.getPersonById(user.getPersonid());
            user.setPerson(person);
            
            if (person != null) {
                validUserList.add(user);
            }
        }
        
        BeanItemContainer<User> beanItemContainer = new BeanItemContainer<User>(User.class);
        for (User user : validUserList) {
            beanItemContainer.addBean(user);
        }
        
        return beanItemContainer;
    }


    private boolean validate(){
        boolean success = true;
        if (select != null) {
            Set<User> members = (Set<User>) select.getValue();
            if (members.size() == 0){
                MessageNotifier.showWarning(getWindow(), "Warning", "No members selected.");  
            }
        } else {
            MessageNotifier.showWarning(getWindow(), "Warning", "No members selected.");
        }     
        return success;

    }

    public boolean validateAndSave(){
        if (validate()) {
            Set<User> members = (Set<User>) select.getValue();
            Project project = createProjectPanel.getProject();
            project.setMembers(members);
            createProjectPanel.setProject(project);
        }
        return true;
    }
    
    private class PreviousButtonClickListener implements ClickListener{
        private static final long serialVersionUID = 1L;

        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            createProjectPanel.getCreateProjectAccordion().setFocusToTab(CreateProjectAccordion.SECOND_TAB_USER_ROLES);
        }
    }
    
    private class NextButtonClickListener implements ClickListener{
        private static final long serialVersionUID = 1L;

        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            createProjectPanel.getCreateProjectAccordion().setFocusToTab(CreateProjectAccordion.FOURTH_TAB_BREEDING_METHODS);
        }
    }

}
