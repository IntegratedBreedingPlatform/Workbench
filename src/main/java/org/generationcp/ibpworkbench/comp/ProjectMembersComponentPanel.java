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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAddUserWindowAction;
import org.generationcp.ibpworkbench.actions.SaveUsersInProjectAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;


/**
 * The third tab (Project Members) in Create Project Accordion Component.
 * 
 * @author Joyce Avestro
 */
@SuppressWarnings("unchecked")
@Configurable
public class ProjectMembersComponentPanel extends VerticalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(ProjectMembersComponentPanel.class);
    private static final long serialVersionUID = 1L;
    
   
    private TwinColSelect select;
    
    private Button newMemberButton;
    private Button saveButton;
    
    private Table tblMembers;
    
    private Button previousButton;
//    private Button nextButton;
    private Component buttonArea;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    private Project project;
    
    private  List<Role> inheritedRoles;

    public ProjectMembersComponentPanel(Project project) {
        this.project = project;
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
        
        select = new TwinColSelect();
        select.setLeftColumnCaption("Available Users");
        select.setRightColumnCaption("Selected Project Members");
        select.setRows(10);
        select.setWidth("400px");
        select.setMultiSelect(true);
        select.setNullSelectionAllowed(true);
        select.setImmediate(true);
        
        addComponent(select);
        
        initializeMembersTable();
        addComponent(tblMembers);
        
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
        
    }
    
    private List<CheckBox> createUserRolesCheckBoxList() {
        List<Role> roles = null;
        List<CheckBox> rolesCheckBoxList = new ArrayList<CheckBox>();
        try {
            roles = workbenchDataManager.getAllRolesOrderedByLabel();
        } catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting roles", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

        for (Role role : roles) {
            CheckBox cb = new CheckBox(role.getName());
            cb.setData(role.getRoleId());
            if (role.getName().equals(Role.MANAGER_ROLE_NAME)) {
                //set default checked value
                cb.setValue(true);
            }
            cb.setCaption(role.getLabel());
            rolesCheckBoxList.add(cb);

        }

        return rolesCheckBoxList;

    }

    public List<Role> getRolesForProjectMembers(){
        List<Role> roles = new ArrayList<Role>();

        for (CheckBox cb : createUserRolesCheckBoxList()) {
            if ((Boolean) cb.getValue() == true) {
                try {
                    Role role = workbenchDataManager.getRoleById((Integer) cb.getData());
                    if (!role.getName().contains(Role.MANAGER_ROLE_NAME)){
                        roles.add(role);
                    }
                } catch (MiddlewareQueryException e) {
                  LOG.error("Error encountered while getting creator user roles", e);
                  throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }
            }
        }
        return roles;
    }
    
    public List<ProjectUserRole> getProjectUserRoles() {
        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        for (CheckBox cb : createUserRolesCheckBoxList()) {
            if ((Boolean) cb.getValue() == true) {
                Role role;
                try {
                    role = workbenchDataManager.getRoleById((Integer) cb.getData());
                    ProjectUserRole projectUserRole = new ProjectUserRole();
                    projectUserRole.setRole(role);
                    projectUserRoles.add(projectUserRole);
                } catch (MiddlewareQueryException e) {
                  LOG.error("Error encountered while getting project user roles", e);
                  throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }
            }
        }
        return projectUserRoles;

    }
    private void initializeMembersTable() {
        tblMembers = new Table();
        tblMembers.setImmediate(true);
        
        inheritedRoles = getRolesForProjectMembers();
        
        List<Role> roleList = new ArrayList<Role>();
        try {
            
            // Add the roles in this order: CB, MAS, MABC, MARS
            List<Role> roles = workbenchDataManager.getAllRolesOrderedByLabel();
            for (Role role: roles){
                if (!role.getName().equals(Role.MANAGER_ROLE_NAME)) {
                    roleList.add(role);
                }
            }
            
            
        }
        catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting workbench roles", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, 
                                                   Message.CONTACT_ADMIN_ERROR_DESC);
        }
        
        final List<Object> columnIds = new ArrayList<Object>();
        columnIds.add("userName");
        List<String> columnHeaders = new ArrayList<String>();
        columnHeaders.add("Member");
        
        // prepare the container
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("userId", Integer.class, null);
        container.addContainerProperty("userName", String.class, null);
        for (Role role : roleList) {
            columnIds.add("role_" + role.getRoleId());
            columnHeaders.add(role.getName());
            if (inheritedRoles.contains(role)){
                container.addContainerProperty("role_" + role.getRoleId(), Boolean.class, Boolean.TRUE);
            } else {
                container.addContainerProperty("role_" + role.getRoleId(), Boolean.class, Boolean.FALSE);
            }
        }
        tblMembers.setContainerDataSource(container);
        
        tblMembers.setVisibleColumns(columnIds.toArray(new Object[0]));
        tblMembers.setColumnHeaders(columnHeaders.toArray(new String[0]));
        
        tblMembers.setEditable(true);
        tblMembers.setTableFieldFactory(new TableFieldFactory() {
            private static final long serialVersionUID = 1L;

            @Override
            public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                int columnIndex = columnIds.indexOf(propertyId);
                if (columnIndex >= 1) {
                    return new CheckBox();
                }
                return null;
            }
        });
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
        newMemberButton.addListener(new OpenNewProjectAddUserWindowAction(select));
        saveButton.addListener(new SaveUsersInProjectAction(this.project,  (List<ProjectUserRole>)getProjectUserRoles(),(List<ProjectUserRole>)getProjectMembers() ));
        
        
        select.addListener(new ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                Property property = event.getProperty();
                Set<User> selectedItems = (Set<User>) property.getValue();
                
                Container container = tblMembers.getContainerDataSource();

                // remove non-selected items
                Collection<?> itemIds = container.getItemIds();
                List<Object> deleteTargets = new ArrayList<Object>();
                for (Object itemId : itemIds) {
                    if (!selectedItems.contains(itemId)) {
                        deleteTargets.add(itemId);
                    }
                }
                for (Object itemId : deleteTargets) {
                    container.removeItem(itemId);
                }
                
                // add newly selected items
                itemIds = container.getItemIds();
                for (User user : selectedItems) {
                    if (!itemIds.contains(user)) {
                        Item item = container.addItem(user);
                        item.getItemProperty("userId").setValue(1);
                        item.getItemProperty("userName").setValue(user.getPerson().getDisplayName());
                        //item.getItemProperty("")
                        setInheritedRoles(item);
                      
                    }
                }
               
            }
        });
    }
    
   


	protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        newMemberButton = new Button("Add New Member");
        saveButton = new Button("Save");
//        nextButton = new Button("Next");
        buttonLayout.addComponent(newMemberButton);
        buttonLayout.addComponent(saveButton);
//        buttonLayout.addComponent(nextButton);
        return buttonLayout;
    }
    

    private Container createUsersContainer() throws MiddlewareQueryException {
        List<User> validUserList = new ArrayList<User>();
        
        SessionData sessionData = IBPWorkbenchApplication.get().getSessionData();
        
        // TODO: This can be improved once we implement proper User-Person mapping
        List<User> userList = workbenchDataManager.getAllUsersSorted();
        for (User user : userList) {
            Person person = workbenchDataManager.getPersonById(user.getPersonid());
            user.setPerson(person);
            
            if (person != null) {
                validUserList.add(user);
            }
        }
        
        BeanItemContainer<User> beanItemContainer = new BeanItemContainer<User>(User.class);
        for (User user : validUserList) {
            if (user.equals(sessionData.getUserData())) {
                continue;
            }
            
            beanItemContainer.addBean(user);
        }
        
        return beanItemContainer;
    }


    public boolean validate(){
        return true;
    }

    public boolean validateAndSave(){
        if (validate()) {
            Set<User> members = (Set<User>) select.getValue();
           
            project.setMembers(members);
           // createProjectPanel.setProject(project);
        }
        return true;    // members not required, so even if there are no values, this returns true
    }
    
   
    
//    private class NextButtonClickListener implements ClickListener{
//        private static final long serialVersionUID = 1L;
//
//        @Override
//        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
//            createProjectPanel.getCreateProjectAccordion().setFocusToTab(CreateProjectAccordion.FOURTH_TAB_BREEDING_METHODS);
//        }
//    }
   
    public List<ProjectUserRole> getProjectMembers() {
        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        
        Container container = tblMembers.getContainerDataSource();
        Collection<User> userList = (Collection<User>) container.getItemIds();
        
        List<Role> roleList = null;
        try {
            roleList = workbenchDataManager.getAllRoles();
        }
        catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting workbench roles", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, 
                                                   Message.CONTACT_ADMIN_ERROR_DESC);
        }
        
        for (User user : userList) {
            Item item = container.getItem(user);
            
            for (Role role : roleList) {
                String propertyId = "role_" + role.getRoleId();
                Property property = item.getItemProperty(propertyId);
                Boolean value = (Boolean) property.getValue();
                
                if (value != null && value.booleanValue()) {
                    ProjectUserRole projectUserRole = new ProjectUserRole();
                    projectUserRole.setUserId(user.getUserid());
                    projectUserRole.setRole(role);
                    
                    projectUserRoles.add(projectUserRole);
                }
            }
        }
        return projectUserRoles;
    }
    
    /**
     * Used to set the inherited roles from the Breeding Workflows tab when edits are made after values are set in this tab.
     * 
     */
    public void setInheritedRoles(){
       // inheritedRoles = createProjectPanel.getCreateProjectAccordion().getRolesForProjectMembers();
    	inheritedRoles = getRolesForProjectMembers();
    	 
        if (tblMembers != null){

            Container container = tblMembers.getContainerDataSource();
            Collection<User> userList = (Collection<User>) container.getItemIds();

            for (User user : userList) {
                Item item = container.getItem(user);

                List<Role> roleList = null;
                try {
                    roleList = workbenchDataManager.getAllRoles();
                } catch (MiddlewareQueryException e) {
                    LOG.error("Error encountered while getting workbench roles", e);
                    throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }

                // Reset old values
                for (Role role : roleList) {
                    String propertyId = "role_" + role.getRoleId();
                    Property property = item.getItemProperty(propertyId);
                    if (property.getType() == Boolean.class){
                        property.setValue(Boolean.FALSE);
                    }
                }

                // Set checked boxes based on inherited roles
                for (Role inheritedRole : inheritedRoles) {
                    String propertyId = "role_" + inheritedRole.getRoleId();
                    Property property = item.getItemProperty(propertyId);
                    if (property.getType() == Boolean.class)
                        property.setValue(Boolean.TRUE);

                }
            }
            
        }
            requestRepaintAll();
                
        }
        
        public void setInheritedRoles(Item currentItem){
         //   inheritedRoles = createProjectPanel.getCreateProjectAccordion().getRolesForProjectMembers();

            if (tblMembers != null){

                Container container = tblMembers.getContainerDataSource();
                Collection<User> userList = (Collection<User>) container.getItemIds();

                

                    List<Role> roleList = null;
                    try {
                        roleList = workbenchDataManager.getAllRoles();
                    } catch (MiddlewareQueryException e) {
                        LOG.error("Error encountered while getting workbench roles", e);
                        throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                    }

                    // Reset old values
                    for (Role role : roleList) {
                        String propertyId = "role_" + role.getRoleId();
                        Property property = currentItem.getItemProperty(propertyId);
                        if (property.getType() == Boolean.class){
                            property.setValue(Boolean.FALSE);
                        }
                    }

                    // Set checked boxes based on inherited roles
                    for (Role inheritedRole : inheritedRoles) {
                        String propertyId = "role_" + inheritedRole.getRoleId();
                        Property property = currentItem.getItemProperty(propertyId);
                        if (property.getType() == Boolean.class)
                            property.setValue(Boolean.TRUE);

                    }
                
                
                requestRepaintAll();
                    
            }

            

        
        
    }
}
