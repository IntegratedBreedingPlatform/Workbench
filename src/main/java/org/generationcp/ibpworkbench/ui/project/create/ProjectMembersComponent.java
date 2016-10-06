/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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
public class ProjectMembersComponent extends VerticalLayout implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectMembersComponent.class);
	private static final long serialVersionUID = 1L;
	private static final String ROLE = "role";
	private static final String USERNAME = "userName";

	private TwinTableSelect<User> select;

	private Button btnCancel;
	private Button btnSave;
	private Component buttonArea;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SessionData sessionData;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private PlatformTransactionManager transactionManager;

	private AddProgramPresenter presenter;

	public ProjectMembersComponent() {
	}

	public ProjectMembersComponent(final AddProgramPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeValues();
		this.initializeLayout();
		this.initializeActions();
	}

	protected void initializeComponents() {

		this.setSpacing(true);
		this.setMargin(true);

		this.select = new TwinTableSelect<User>(User.class);

		final Table.ColumnGenerator tableLeftUserName = new Table.ColumnGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return ProjectMembersComponent.this.generateUserNameCell(itemId);
			}

		};
		final Table.ColumnGenerator tableRightUserName = new Table.ColumnGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return ProjectMembersComponent.this.generateUserNameCell(itemId);
			}

		};

		final Table.ColumnGenerator tableLeftRole = new Table.ColumnGenerator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return ProjectMembersComponent.this.generateRoleCell(itemId);
			}
		};
		final Table.ColumnGenerator tableRightRole = new Table.ColumnGenerator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return ProjectMembersComponent.this.generateRoleCell(itemId);
			}

		};

		this.select.getTableLeft().addGeneratedColumn(ProjectMembersComponent.USERNAME, tableLeftUserName);
		this.select.getTableRight().addGeneratedColumn(ProjectMembersComponent.USERNAME, tableRightUserName);
		this.select.getTableLeft().addGeneratedColumn(ProjectMembersComponent.ROLE, tableLeftRole);
		this.select.getTableRight().addGeneratedColumn(ProjectMembersComponent.ROLE, tableRightRole);

		this.select.setVisibleColumns(new Object[] {"select", ProjectMembersComponent.USERNAME, ProjectMembersComponent.ROLE});
		this.select.setColumnHeaders(new String[] {"<span class='glyphicon glyphicon-ok'></span>", "User Name", "Role"});

		this.select.setLeftColumnCaption("Available Users");
		this.select.setRightColumnCaption("Selected Program Members");

		this.select.setLeftLinkCaption("");
		this.select.setRightLinkCaption("Remove Selected Members");
		this.select.addRightLinkListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final ClickEvent event) {
				ProjectMembersComponent.this.select.removeCheckedSelectedItems();
			}
		});
		this.buttonArea = this.layoutButtonArea();
	}

	protected void initializeValues() {
		try {
			final Container container = this.createUsersContainer();
			this.select.setContainerDataSource(container);

			Object selectItem = null;
			for (final Object itemId : this.select.getTableLeft().getItemIds()) {
				if (((User) itemId).getUserid().equals(this.sessionData.getUserData().getUserid())) {
					selectItem = itemId;
				}
			}

			if (selectItem != null) {
				this.select.select(selectItem);
			}

		} catch (final MiddlewareQueryException e) {
			ProjectMembersComponent.LOG.error("Error encountered while getting workbench users", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}

	}

	protected void initializeLayout() {
		this.setSpacing(true);
		this.setMargin(true);

		final HorizontalLayout titleContainer = new HorizontalLayout();
		titleContainer.setDebugId("titleContainer");
		final Label heading = new Label("<span class='bms-members' style='color: #D1B02A; font-size: 23px'></span>&nbsp;Program Members",
				Label.CONTENT_XHTML);
		final Label headingDesc = new Label(
				"Choose team members for this program by dragging available users from the list on the left into the Program Members list on the right.");

		heading.setStyleName(Bootstrap.Typography.H4.styleName());

		titleContainer.addComponent(heading);
		titleContainer.setSizeUndefined();
		titleContainer.setWidth("100%");
		// TODO: move this to css
		titleContainer.setMargin(false, false, false, false);

		this.addComponent(titleContainer);
		this.addComponent(headingDesc);
		this.addComponent(this.select);
		this.addComponent(this.buttonArea);

		this.setComponentAlignment(this.select, Alignment.TOP_CENTER);
		this.setComponentAlignment(this.buttonArea, Alignment.TOP_CENTER);

	}

	protected void initializeActions() {
		this.btnSave.addListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final ClickEvent clickEvent) {
				final TransactionTemplate transactionTemplate = new TransactionTemplate(ProjectMembersComponent.this.transactionManager);
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(final TransactionStatus status) {
						try {
							final Project newlyCreatedProgram = ProjectMembersComponent.this.presenter.doAddNewProgram();
							MessageNotifier.showMessage(clickEvent.getComponent().getWindow(),
									ProjectMembersComponent.this.messageSource.getMessage(Message.SUCCESS),
									newlyCreatedProgram.getProjectName() + " program has been successfully created.");

							ProjectMembersComponent.this.sessionData.setLastOpenedProject(newlyCreatedProgram);
							ProjectMembersComponent.this.sessionData.setSelectedProject(newlyCreatedProgram);

							if (IBPWorkbenchApplication.get().getMainWindow() instanceof WorkbenchMainView) {
								((WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow()).getSidebar().populateLinks();
							}

							ProjectMembersComponent.this.presenter.enableProgramMethodsAndLocationsTab();

						} catch (final Exception e) {

							if ("basic_details_invalid".equals(e.getMessage())) {
								return;
							}

							ProjectMembersComponent.LOG
									.error("Oops there might be serious problem on creating the program, investigate it!", e);

							MessageNotifier.showError(clickEvent.getComponent().getWindow(),
									ProjectMembersComponent.this.messageSource.getMessage(Message.DATABASE_ERROR),
									ProjectMembersComponent.this.messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));

						}
					}
				});
			}
		});

		this.btnCancel.addListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final ClickEvent clickEvent) {
				ProjectMembersComponent.this.presenter.resetProgramMembers();
				ProjectMembersComponent.this.presenter.disableProgramMethodsAndLocationsTab();
			}
		});
	}

	protected Component layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.btnCancel = new Button("Reset");
		this.btnCancel.setDebugId("btnCancel");
		this.btnSave = new Button("Save");
		this.btnSave.setDebugId("btnSave");
		this.btnSave.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		buttonLayout.addComponent(this.btnCancel);
		buttonLayout.addComponent(this.btnSave);

		return buttonLayout;
	}

	private Label generateRoleCell(final Object itemId) {
		final String role = ((User) itemId).getRoles().get(0).getCapitalizedRole();
		final Label label = new Label();
		label.setDebugId("label");
		label.setValue(role);

		if (((User) itemId).getUserid().equals(ProjectMembersComponent.this.sessionData.getUserData().getUserid())) {
			label.setStyleName("label-bold");
		}
		return label;
	}

	private Label generateUserNameCell(final Object itemId) {
		final Person person = ((User) itemId).getPerson();
		final Label label = new Label();
		label.setDebugId("label");
		label.setValue(person.getDisplayName());

		if (((User) itemId).getUserid().equals(ProjectMembersComponent.this.sessionData.getUserData().getUserid())) {
			label.setStyleName("label-bold");
		}
		return label;
	}

	private Container createUsersContainer() {
		final List<User> validUserList = new ArrayList<User>();

		// TODO: This can be improved once we implement proper User-Person mapping
		final List<User> userList = this.workbenchDataManager.getAllUsersSorted();
		for (final User user : userList) {
			final Person person = this.workbenchDataManager.getPersonById(user.getPersonid());
			user.setPerson(person);

			if (person != null) {
				validUserList.add(user);
			}
		}

		final BeanItemContainer<User> beanItemContainer = new BeanItemContainer<User>(User.class);
		for (final User user : validUserList) {
			if (user.getUserid().equals(this.sessionData.getUserData().getUserid())) {
				user.setEnabled(false);
			}

			beanItemContainer.addBean(user);
		}

		return beanItemContainer;
	}

	public Set<User> getSelectedUsers() {
		return this.select.getValue();
	}
}
