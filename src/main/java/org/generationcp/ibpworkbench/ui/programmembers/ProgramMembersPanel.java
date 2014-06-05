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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAddUserWindowAction;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
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

import java.util.*;


/**
 *
 * @author Aldrin Batac
 */
@SuppressWarnings("unchecked")
@Configurable
public class ProgramMembersPanel extends Panel implements InitializingBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProgramMembersPanel.class);
    private static final long serialVersionUID = 1L;
    
    private TwinTableSelect<User> select;
    
    private Button newMemberButton;
    private Button cancelButton;
    private Button saveButton;
    
    private Table tblMembers;
    
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SessionData sessionData;
    
    private Project project;
    
    private  List<Role> inheritedRoles;

    public ProgramMembersPanel(Project project) {
    	//LOG.debug("Project is " + project.getProjectName());
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
        try{
        	initializeUsers();
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
    }

    protected void initializeComponents(){

        
        
        select = new TwinTableSelect<User>(User.class);
        
        Table.ColumnGenerator generator1 = new Table.ColumnGenerator(){

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Person person = ((User) itemId).getPerson();
				Label label = new Label();
				label.setValue(person.getDisplayName());
				//if (((User) itemId).getUserid()  == project.getUserId()) label.setStyleName("label-bold");
				if (((User) itemId).getUserid().equals(sessionData.getUserData().getUserid())) label.setStyleName("label-bold");
				return label;
			}
        	
        	
        };
        Table.ColumnGenerator generator2 = new Table.ColumnGenerator(){

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Person person = ((User) itemId).getPerson();
				Label label = new Label();
				label.setValue(person.getDisplayName());
				//if (((User) itemId).getUserid() == project.getUserId()) label.setStyleName("label-bold");
				if (((User) itemId).getUserid().equals(sessionData.getUserData().getUserid())) label.setStyleName("label-bold");
				return label;
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
				//select.removeAllSelectedItems();
				select.removeCheckedSelectedItems();
			}
		});
        
       
     

    }
    
    private List<CheckBox> createUserRolesCheckBoxList() {
        List<Role> roles = null;
        List<CheckBox> rolesCheckBoxList = new ArrayList<CheckBox>();
        
        LOG.debug("createUserRolesCheckBoxList");
        
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
        LOG.debug("getRolesForProjectMembers");
        for (CheckBox cb : createUserRolesCheckBoxList()) {
            
        	
        	
        	if ((Boolean) cb.getValue() == true) {
                try {
                    Role role = workbenchDataManager.getRoleById((Integer) cb.getData());
                    //if (!role.getName().contains(Role.MANAGER_ROLE_NAME)){
                    LOG.debug("getRolesForProjectMembers id : "+cb.getData());
                    LOG.debug("getRolesForProjectMembers name : "+role.getName());
                        roles.add(role);
                    //}
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
        LOG.debug("getProjectUserRoles");
        for (CheckBox cb : createUserRolesCheckBoxList()) {
            if ((Boolean) cb.getValue() == true) {
                Role role;
                try {
                    role = workbenchDataManager.getRoleById((Integer) cb.getData());
                    ProjectUserRole projectUserRole = new ProjectUserRole();
                    projectUserRole.setRole(role);
                    
                    projectUserRoles.add(projectUserRole);
                } catch (MiddlewareQueryException e) {
                  LOG.error("Error encountered while getting program user roles", e);
                  throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }
            }
        }
        return projectUserRoles;

    }
    
    private Table initializeMembersTable() {
        tblMembers = new Table();
        tblMembers.setImmediate(true);
        
        inheritedRoles = getRolesForProjectMembers();
        
        List<Role> roleList = new ArrayList<Role>();
        try {
            
            // Add the roles in this order: CB, MAS, MABC, MARS
            List<Role> roles = workbenchDataManager.getAllRolesOrderedByLabel();
            for (Role role: roles){
              //  if (!role.getName().equals(Role.MANAGER_ROLE_NAME)) {
                    roleList.add(role);
               // }
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

        return tblMembers;
    }

    protected void initializeValues() {
        try {
            
            Container container = createUsersContainer();
            select.setContainerDataSource(container);
            
        }
        catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting workbench users", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, 
                                                   Message.CONTACT_ADMIN_ERROR_DESC);
        }

    }

    protected void initializeLayout() {
    	 this.setStyleName(Reindeer.PANEL_LIGHT);

    	 final HorizontalLayout titleContainer = new HorizontalLayout();
         final Label heading = new Label("<span class='bms-members' style='color: #D1B02A; font-size: 23px'></span>&nbsp;Program Members",Label.CONTENT_XHTML);
         final Label headingDesc = new Label("Choose team members for this program by dragging available users from the list on the left into the Program Members list on the right.");

         heading.setStyleName(Bootstrap.Typography.H4.styleName());

         newMemberButton = new Button("Add New User");
         newMemberButton.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");

         titleContainer.addComponent(heading);
         titleContainer.addComponent(newMemberButton);

         titleContainer.setComponentAlignment(newMemberButton, Alignment.MIDDLE_RIGHT);
         titleContainer.setSizeUndefined();
         titleContainer.setWidth("100%");
         titleContainer.setMargin(true, false, false, false);	// move this to css
    	
    	

        final VerticalLayout root = new VerticalLayout();
        root.setMargin(new Layout.MarginInfo(false,true,true,true));
        root.setSpacing(true);
        root.setSizeFull();
        
        root.addComponent(titleContainer);
        root.addComponent(headingDesc);

        final ComponentContainer buttonArea = layoutButtonArea();

        root.addComponent(select);
      
        initializeMembersTable();
        
        root.addComponent(buttonArea);
        root.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);

        this.setScrollable(false);

        this.setSizeFull();
        this.setContent(root);
    }
    	
    protected void initializeUsers() throws MiddlewareQueryException 
    {
    	 Container container = tblMembers.getContainerDataSource();
    	 
    	 List<ProjectUserRole> projectUserRoles = workbenchDataManager.getProjectUserRolesByProject(this.project);
     
         Set<User> selectedItems = new HashSet();
         
         for (ProjectUserRole projrole : projectUserRoles) {
        	 User userTemp = workbenchDataManager.getUserById(projrole.getUserId());
        	 selectedItems.add(userTemp);
        	 
        	 container.removeItem(userTemp);
        	
        	 Item item = container.addItem(userTemp);
             item.getItemProperty("userId").setValue(1);
             item.getItemProperty("userName").setValue(userTemp.getPerson().getDisplayName());
             item.getItemProperty("role_" + projrole.getRole().getRoleId()).setValue("true");
             //item.getItemProperty("")
             List<Role> projroles = workbenchDataManager.getRolesByProjectAndUser(project, userTemp);
             setInheritedRoles(item,projroles);
             
             this.select.select(userTemp);
            
           
         }
        
    }
    protected void initializeActions() {
        newMemberButton.addListener(new OpenNewProjectAddUserWindowAction(select));
        saveButton.addListener(new SaveUsersInProjectAction(this.project, select ));
        cancelButton.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				initializeValues();
				try {
					initializeUsers();
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
        
    }
    
   


	protected ComponentContainer layoutButtonArea() {
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Reset");
        saveButton = new Button("Save");

        saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveButton);

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
            //if (user.getUserid() == project.getUserId()) {
        	if (user.getUserid().equals(sessionData.getUserData().getUserid())) {
              user.setEnabled(false);
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
        
        LOG.debug("getProjectMembers");
        
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
                    LOG.debug("getProjectMembers name "+ user.getName());
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
        
        public void setInheritedRoles(Item currentItem, List<Role> myinheritedRoles){
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
                  
                    for (Role inheritedRole : myinheritedRoles) {
                        String propertyId = "role_" + inheritedRole.getRoleId();
                        LOG.debug("inheritedRole " + inheritedRole);
                        LOG.debug("currentItem " + currentItem);
                        Property property = currentItem.getItemProperty(propertyId);
                        if (property.getType() == Boolean.class)
                            property.setValue(Boolean.TRUE);

                    }
                
                
                requestRepaintAll();
                    
            }

            

        
        
    }
        
       
        
        
}
