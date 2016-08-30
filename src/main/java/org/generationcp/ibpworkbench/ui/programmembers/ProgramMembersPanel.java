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

package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author Aldrin Batac
 */
@SuppressWarnings("unchecked")
@Configurable
public class ProgramMembersPanel extends Panel implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramMembersPanel.class);
	private static final long serialVersionUID = 1L;
	private static final String ROLE_PREFIX = "role_";
	private static final String USERNAME = "userName";

	private TwinTableSelect<User> select;

	private Button newMemberButton;
	private Button cancelButton;
	private Button saveButton;

	private Table tblMembers;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SessionData sessionData;

	private final Project project;

	public ProgramMembersPanel(final Project project) {
		this.project = project;
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
		this.initializeUsers();

	}

	protected void initializeComponents() {
		this.select = new TwinTableSelect<User>(User.class);

		final Table.ColumnGenerator generator1 = new Table.ColumnGenerator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 6976921612035925373L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final Person person = ((User) itemId).getPerson();
				final Label label = new Label();
				label.setValue(person.getDisplayName());

				if (((User) itemId).getUserid().equals(ProgramMembersPanel.this.sessionData.getUserData().getUserid())) {
					label.setStyleName("label-bold");
				}
				return label;
			}
		};
		final Table.ColumnGenerator generator2 = new Table.ColumnGenerator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2789260422341831368L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final Person person = ((User) itemId).getPerson();
				final Label label = new Label();
				label.setValue(person.getDisplayName());

				if (((User) itemId).getUserid().equals(ProgramMembersPanel.this.sessionData.getUserData().getUserid())) {
					label.setStyleName("label-bold");
				}
				return label;
			}
		};

		this.select.getTableLeft().addGeneratedColumn(USERNAME, generator1);
		this.select.getTableRight().addGeneratedColumn(USERNAME, generator2);

		this.select.setVisibleColumns(new Object[] {"select", USERNAME});
		this.select.setColumnHeaders(new String[] {"<span class='glyphicon glyphicon-ok'></span>", "USER NAME"});

		this.select.setLeftColumnCaption("Available Users");
		this.select.setRightColumnCaption("Selected Program Members");

		this.select.setLeftLinkCaption("");
		this.select.setRightLinkCaption("Remove Selected Members");
		this.select.addRightLinkListener(new Button.ClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -7548361229675101895L;

			@Override
			public void buttonClick(final ClickEvent event) {
				ProgramMembersPanel.this.select.removeCheckedSelectedItems();
			}
		});
	}

	private List<CheckBox> createUserRolesCheckBoxList() {
		List<Role> roles = null;
		final List<CheckBox> rolesCheckBoxList = new ArrayList<CheckBox>();

		ProgramMembersPanel.LOG.debug("createUserRolesCheckBoxList");

		try {
			roles = this.workbenchDataManager.getAllRolesOrderedByLabel();
		} catch (final MiddlewareQueryException e) {
			ProgramMembersPanel.LOG.error("Error encountered while getting roles", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}

		for (final Role role : roles) {
			final CheckBox cb = new CheckBox(role.getName());
			cb.setData(role.getRoleId());
			if (role.getName().equals(Role.MANAGER_ROLE_NAME)) {
				// set default checked value
				cb.setValue(true);
			}
			cb.setCaption(role.getLabel());
			rolesCheckBoxList.add(cb);

		}

		return rolesCheckBoxList;
	}

	public List<Role> getRolesForProjectMembers() {
		final List<Role> roles = new ArrayList<Role>();
		ProgramMembersPanel.LOG.debug("getRolesForProjectMembers");
		for (final CheckBox cb : this.createUserRolesCheckBoxList()) {
			if ((Boolean) cb.getValue()) {
				try {
					final Role role = this.workbenchDataManager.getRoleById((Integer) cb.getData());
					ProgramMembersPanel.LOG.debug("getRolesForProjectMembers id : " + cb.getData());
					ProgramMembersPanel.LOG.debug("getRolesForProjectMembers name : " + role.getName());
					roles.add(role);

				} catch (final MiddlewareQueryException e) {
					ProgramMembersPanel.LOG.error("Error encountered while getting creator user roles", e);
					throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
				}
			}
		}
		return roles;
	}

	private Table initializeMembersTable() {
		this.tblMembers = new Table();
		this.tblMembers.setImmediate(true);

		final List<Role> inheritedRoles = this.getRolesForProjectMembers();

		final List<Role> roleList = new ArrayList<Role>();
		try {

			// Add the roles in this order: CB, MAS, MABC, MARS
			final List<Role> roles = this.workbenchDataManager.getAllRolesOrderedByLabel();
			for (final Role role : roles) {
				roleList.add(role);

			}

		} catch (final MiddlewareQueryException e) {
			ProgramMembersPanel.LOG.error("Error encountered while getting workbench roles", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}

		final List<Object> columnIds = new ArrayList<Object>();
		columnIds.add(USERNAME);
		final List<String> columnHeaders = new ArrayList<String>();
		columnHeaders.add("Member");

		// prepare the container
		final IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("userId", Integer.class, null);
		container.addContainerProperty(USERNAME, String.class, null);
		for (final Role role : roleList) {
			columnIds.add(ROLE_PREFIX + role.getRoleId());
			columnHeaders.add(role.getName());
			if (inheritedRoles.contains(role)) {
				container.addContainerProperty(ROLE_PREFIX + role.getRoleId(), Boolean.class, Boolean.TRUE);
			} else {
				container.addContainerProperty(ROLE_PREFIX + role.getRoleId(), Boolean.class, Boolean.FALSE);
			}
		}
		this.tblMembers.setContainerDataSource(container);

		this.tblMembers.setVisibleColumns(columnIds.toArray(new Object[0]));
		this.tblMembers.setColumnHeaders(columnHeaders.toArray(new String[0]));

		this.tblMembers.setEditable(true);
		this.tblMembers.setTableFieldFactory(new TableFieldFactory() {

			private static final long serialVersionUID = 1L;

			@Override
			public Field createField(final Container container, final Object itemId, final Object propertyId, final Component uiContext) {
				final int columnIndex = columnIds.indexOf(propertyId);
				if (columnIndex >= 1) {
					return new CheckBox();
				}
				return null;
			}
		});

		return this.tblMembers;
	}

	protected void initializeValues() {
		try {
			final Container container = this.createUsersContainer();
			this.select.setContainerDataSource(container);
		} catch (final MiddlewareQueryException e) {
			ProgramMembersPanel.LOG.error("Error encountered while getting workbench users", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}
	}

	protected void initializeLayout() {
		this.setStyleName(Reindeer.PANEL_LIGHT);

		final HorizontalLayout titleContainer = new HorizontalLayout();
		final Label heading =
				new Label("<span class='bms-members' style='color: #D1B02A; font-size: 23px'></span>&nbsp;Program Members",
						Label.CONTENT_XHTML);
		final Label headingDesc =
				new Label(
						"Choose team members for this program by dragging available users from the list on the left into the Program Members list on the right.");

		heading.setStyleName(Bootstrap.Typography.H4.styleName());

		this.newMemberButton = new Button("Add New User");
		this.newMemberButton.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");

		titleContainer.addComponent(heading);
		titleContainer.addComponent(this.newMemberButton);

		titleContainer.setComponentAlignment(this.newMemberButton, Alignment.MIDDLE_RIGHT);
		titleContainer.setSizeUndefined();
		titleContainer.setWidth("100%");
		// move this to css
		titleContainer.setMargin(true, false, false, false);

		final VerticalLayout root = new VerticalLayout();
		root.setMargin(new Layout.MarginInfo(false, true, true, true));
		root.setSpacing(true);
		root.setSizeFull();

		root.addComponent(titleContainer);
		root.addComponent(headingDesc);

		final ComponentContainer buttonArea = this.layoutButtonArea();

		root.addComponent(this.select);

		this.initializeMembersTable();

		root.addComponent(buttonArea);
		root.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);

		this.setScrollable(false);

		this.setSizeFull();
		this.setContent(root);
	}

	protected void initializeUsers() {
		final Container container = this.tblMembers.getContainerDataSource();

		final List<ProjectUserRole> projectUserRoles = this.workbenchDataManager.getProjectUserRolesByProject(this.project);

		final Set<User> selectedItems = new HashSet<User>();

		for (final ProjectUserRole projrole : projectUserRoles) {
			final User userTemp = this.workbenchDataManager.getUserById(projrole.getUserId());
			selectedItems.add(userTemp);

			container.removeItem(userTemp);

			final Item item = container.addItem(userTemp);
			item.getItemProperty("userId").setValue(1);
			item.getItemProperty(USERNAME).setValue(userTemp.getPerson().getDisplayName());
			item.getItemProperty(ROLE_PREFIX + projrole.getRole().getRoleId()).setValue("true");
			// item.getItemProperty("")
			final List<Role> projroles = this.workbenchDataManager.getRolesByProjectAndUser(this.project, userTemp);
			this.setInheritedRoles(item, projroles);

			this.select.select(userTemp);
		}

	}

	protected void initializeActions() {
		this.newMemberButton.addListener(new OpenNewProjectAddUserWindowAction(this.select));
		this.saveButton.addListener(new SaveUsersInProjectAction(this.project, this.select));
		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8879824681692031501L;

			@Override
			public void buttonClick(final ClickEvent event) {
				ProgramMembersPanel.this.initializeValues();
				ProgramMembersPanel.this.initializeUsers();
			}
		});
	}

	protected ComponentContainer layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Reset");
		this.saveButton = new Button("Save");

		this.saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.saveButton);

		return buttonLayout;
	}

	protected Container createUsersContainer() {
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
			if (user.getUserid().equals(this.sessionData.getUserData().getUserid()) || user.getUserid().equals(this.project.getUserId()) ||
					user.hasRole(org.generationcp.commons.security.Role.ADMIN.toString())) {
				user.setEnabled(false);
			}

			beanItemContainer.addBean(user);

		}

		return beanItemContainer;
	}

	public boolean validate() {
		return true;
	}

	public boolean validateAndSave() {
		if (this.validate()) {
			final Set<User> members = this.select.getValue();

			this.project.setMembers(members);
		}
		// members not required, so even if there are no values, this returns true
		return true;
	}

	public void setInheritedRoles(final Item currentItem, final List<Role> myinheritedRoles) {

		if (this.tblMembers != null) {
			List<Role> roleList = null;
			try {
				roleList = this.workbenchDataManager.getAllRoles();
			} catch (final MiddlewareQueryException e) {
				ProgramMembersPanel.LOG.error("Error encountered while getting workbench roles", e);
				throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
			}

			// Reset old values
			for (final Role role : roleList) {
				final String propertyId = ROLE_PREFIX + role.getRoleId();
				final Property property = currentItem.getItemProperty(propertyId);
				if (property.getType() == Boolean.class) {
					property.setValue(Boolean.FALSE);
				}
			}

			// Set checked boxes based on inherited roles
			for (final Role inheritedRole : myinheritedRoles) {
				final String propertyId = ROLE_PREFIX + inheritedRole.getRoleId();
				ProgramMembersPanel.LOG.debug("inheritedRole " + inheritedRole);
				ProgramMembersPanel.LOG.debug("currentItem " + currentItem);
				final Property property = currentItem.getItemProperty(propertyId);
				if (property.getType() == Boolean.class) {
					property.setValue(Boolean.TRUE);
				}
			}
			this.requestRepaintAll();
		}
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setSessionData(final SessionData sessionData) {
		this.sessionData = sessionData;
	}

}
