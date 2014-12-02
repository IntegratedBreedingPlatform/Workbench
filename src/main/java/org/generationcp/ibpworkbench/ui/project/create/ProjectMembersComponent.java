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
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAddUserWindowAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;


/**
 * The third tab (Project Members) in Create Project Accordion Component.
 * 
 * @author Joyce Avestro
 */
@Configurable
public class ProjectMembersComponent extends VerticalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(ProjectMembersComponent.class);
    private static final long serialVersionUID = 1L;

    private TwinTableSelect<User> select;
    
    private Button newMemberButton;

    private Button btnCancel;
    private Button btnSave;
    private Component buttonArea;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private AddProgramPresenter presenter;


    public ProjectMembersComponent() {
    }


    public ProjectMembersComponent(AddProgramPresenter presenter) {
        this.presenter = presenter;
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
        
        select = new TwinTableSelect<User>(User.class);
        
        Table.ColumnGenerator generator1 = new Table.ColumnGenerator(){

        	private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Person person = ((User) itemId).getPerson();
				Label label = new Label();
				label.setValue(person.getDisplayName());
				if (((User) itemId).getUserid().equals(sessionData.getUserData().getUserid())) {
                    label.setStyleName("label-bold");
                }
				return label;
			}
        	
        	
        };
        Table.ColumnGenerator generator2 = new Table.ColumnGenerator(){
			
        	private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				Person person = ((User) itemId).getPerson();
				Label label = new Label();
				label.setValue(person.getDisplayName());
				if (((User) itemId).getUserid().equals(sessionData.getUserData().getUserid())) {
                    label.setStyleName("label-bold");
                }
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
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				select.removeCheckedSelectedItems();
			}
		});
        buttonArea = layoutButtonArea();
    }

    protected void initializeValues() {
        try {
            Container container = createUsersContainer();
            select.setContainerDataSource(container);
            
            Object selectItem = null;
            for (Object itemId : select.getTableLeft().getItemIds()){
            	if (((User) itemId).getUserid().equals(sessionData.getUserData().getUserid())){
            		selectItem = itemId;
            	}
            }
            
            if (selectItem != null) {
                select.select(selectItem);
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
        titleContainer.setMargin(false, false, false, false);	// move this to css

        addComponent(titleContainer);
        addComponent(headingDesc);
        addComponent(select);
        addComponent(buttonArea);
        
        setComponentAlignment(select,Alignment.TOP_CENTER);
        setComponentAlignment(buttonArea, Alignment.TOP_CENTER);

    }

    protected void initializeActions() {
        newMemberButton.addListener(new OpenNewProjectAddUserWindowAction(select));

        btnSave.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(ClickEvent clickEvent) {
                try {
                    presenter.doAddNewProgram();

                    MessageNotifier.showMessage(clickEvent.getComponent().getWindow(),
                            messageSource.getMessage(Message.SUCCESS), presenter.program.getProjectName() + " program has been successfully created.");

                    sessionData.setLastOpenedProject(presenter.program);
                    sessionData.setSelectedProject(presenter.program);

                    if (IBPWorkbenchApplication.get().getMainWindow() instanceof WorkbenchMainView) {
                        ((WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow()).getSidebar().populateLinks();
                    }

                    presenter.enableProgramMethodsAndLocationsTab();


                } catch (Exception e) {

                    if (e.getMessage().equals("basic_details_invalid")) {
                        return;
                    }

                    LOG.error("Oops there might be serious problem on creating the program, investigate it!",e);

                    MessageNotifier.showError(clickEvent.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR),
                            messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));

                }
            }
        });

        btnCancel.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(ClickEvent clickEvent) {
                presenter.resetProgramMembers();
                presenter.disableProgramMethodsAndLocationsTab();
            }
        });
    }
    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        btnCancel = new Button("Reset");
        btnSave = new Button("Save");
        btnSave.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
     
        buttonLayout.addComponent(btnCancel);
        buttonLayout.addComponent(btnSave);

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
            if (user.getUserid().equals(sessionData.getUserData().getUserid())) {
                user.setEnabled(false);
            }
            
            beanItemContainer.addBean(user);
        }
        
        return beanItemContainer;
    }

    public Set<User> getSelectedUsers(){
        return select.getValue();
    }
}
