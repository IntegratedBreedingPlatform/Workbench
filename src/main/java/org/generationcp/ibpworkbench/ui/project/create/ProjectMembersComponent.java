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
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAddUserWindowAction;
import org.generationcp.ibpworkbench.ui.programmembers.TwinTableSelect;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
public class ProjectMembersComponent extends VerticalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(ProjectMembersComponent.class);
    private static final long serialVersionUID = 1L;
    
    private CreateProjectPanel createProjectPanel;
   
    private TwinTableSelect<User> select;
    
    private Button newMemberButton;
    
    private Table tblMembers;
    
    private Button previousButton;
//    private Button nextButton;
    private Component buttonArea;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SessionData sessionData;
    
    private  List<Role> inheritedRoles;

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
        
        /**
         * select = new TwinColSelect();
        select.setLeftColumnCaption("Available Users");
        select.setRightColumnCaption("Selected Program Members");
        select.setRows(10);
        select.setWidth("500px");
        select.setMultiSelect(true);
        select.setNullSelectionAllowed(true);
        select.setImmediate(true);**/
        
        select = new TwinTableSelect<User>(User.class);
        
        Table.ColumnGenerator generator1 = new Table.ColumnGenerator(){

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Person person = ((User) itemId).getPerson();
				return person.getDisplayName();
			}
        	
        	
        };
        Table.ColumnGenerator generator2 = new Table.ColumnGenerator(){

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Person person = ((User) itemId).getPerson();
				return person.getDisplayName();
			}
        	
        	
        };
        
        select.getTableLeft().addGeneratedColumn("userName", generator1);
        select.getTableRight().addGeneratedColumn("userName", generator2);
        
        select.setVisibleColumns(new Object[] {"select","userName"});
        select.setColumnHeaders(new String[] {"<span class='glyphicon glyphicon-ok'></span>","USER NAME"});
        
        select.setLeftColumnCaption("Available Users");
        select.setRightColumnCaption("Selected Program Members");
        
        select.setLeftLinkCaption("");
        select.setRightLinkCaption("Remove Selected Members");
        select.addRightLinkListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				select.removeAllSelectedItems();
			}
		});
        
        addComponent(select);
        
        initializeMembersTable();
        
        
        /*        
        int personUID = sessionData.getUserData().getPersonid();

		
			String loggedinUserStr = ""; 
			Person loggedinUser;
			    
			loggedinUser = workbenchDataManager.getPersonById(personUID);
		

            if (loggedinUser.getFirstName() != null) {
            	loggedinUserStr = loggedinUser.getFirstName();
            }
            if (loggedinUser.getMiddleName() != null) {
            	loggedinUserStr += " " + loggedinUser.getMiddleName(); 
            }
            if (loggedinUser.getLastName() != null) {
            	loggedinUserStr += " " + loggedinUser.getLastName();
            }
       */
        
        String currentUserMsg = "* Note: By default, you are a member of this program.";	//TODO FIXME: add correct internationalization message for this.
        
        Label currentUserLbl = new Label(currentUserMsg,Label.CONTENT_XHTML);
        currentUserLbl.addStyleName("create_project_member_current_user_msg");
        
        
        addComponent(currentUserLbl);
        

        //addComponent(tblMembers);
        
        
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
        
    }

    private void initializeMembersTable() {
        tblMembers = new Table();
        tblMembers.setImmediate(true);
        
        inheritedRoles = createProjectPanel.getCreateProjectAccordion().getRolesForProjectMembers();
        
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
                    CheckBox cb = new CheckBox();
                    cb.setValue(true);
                    return cb;
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
                //select.setItemCaption(itemId, user.getPerson().getDisplayName());
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
        
        select.setWidth("100%");
        select.setHeight("300px");
        
        setComponentAlignment(select,Alignment.TOP_CENTER);
        //setComponentAlignment(tblMembers,Alignment.TOP_CENTER);
        setComponentAlignment(buttonArea, Alignment.TOP_CENTER);
    }

    protected void initializeActions() {
        newMemberButton.addListener(new OpenNewProjectAddUserWindowAction(select));
        
        previousButton.addListener(new PreviousButtonClickListener());
//        nextButton.addListener(new NextButtonClickListener());
        
        /**
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
        });**/
    }
    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        newMemberButton = new Button("Add New Member");
        newMemberButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());

        previousButton = new Button("Previous");
        //previousButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());

//        nextButton = new Button("Next");
        buttonLayout.addComponent(previousButton);
        buttonLayout.addComponent(newMemberButton);
//        buttonLayout.addComponent(nextButton);
        return buttonLayout;
    }
    

    private Container createUsersContainer() throws MiddlewareQueryException {
        List<User> validUserList = new ArrayList<User>();

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
            Project project = createProjectPanel.getProject();
            project.setMembers(members);
            createProjectPanel.setProject(project);
        }
        return true;    // members not required, so even if there are no values, this returns true
    }
    
    private class PreviousButtonClickListener implements ClickListener{
        private static final long serialVersionUID = 1L;

        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            createProjectPanel.getCreateProjectAccordion().setFocusToTab(CreateProjectAccordion.FIRST_TAB_BASIC_DETAILS);
        }
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
        inheritedRoles = createProjectPanel.getCreateProjectAccordion().getRolesForProjectMembers();

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
            inheritedRoles = createProjectPanel.getCreateProjectAccordion().getRolesForProjectMembers();

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
                            property.setValue(Boolean.TRUE);
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
