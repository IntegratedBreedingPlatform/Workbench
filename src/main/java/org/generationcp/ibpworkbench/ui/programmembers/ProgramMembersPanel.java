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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractSelect;
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
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.domain.workbench.RoleType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Aldrin Batac
 */
@Configurable
public class ProgramMembersPanel extends Panel implements InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(ProgramMembersPanel.class);
	private static final long serialVersionUID = 1L;
	private static final String ROLE = "role_";
	private static final String USERNAME = "userName";

	private TwinTableSelect<WorkbenchUser> select;
	private Button cancelButton;
	private Button saveButton;

	private Table tblMembers;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private ContextUtil contextUtil;

	private final Project project;

	private RoleSelectionWindow roleSelectionWindow;

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

	protected Label generateRoleCell(final Object itemId) {
		//TODO Review with new development
		final String role = (((WorkbenchUser) itemId).getRoles() != null && !((WorkbenchUser) itemId).getRoles().isEmpty()
			&& ((WorkbenchUser) itemId).getRoles().get(0) != null) ? ((WorkbenchUser) itemId).getRoles().get(0).getCapitalizedRole() : "";
		final Label label = new Label();
		label.setDebugId("label");
		label.setValue(role);

		if (((WorkbenchUser) itemId).getUserid().equals(this.contextUtil.getCurrentWorkbenchUserId())) {
			label.setStyleName("label-bold");
		}
		return label;
	}

	protected Label generateUserNameCell(final Object itemId) {
		final Person person = ((WorkbenchUser) itemId).getPerson();
		final Label label = new Label();
		label.setDebugId("label");
		label.setValue(person.getDisplayName());

		if (((WorkbenchUser) itemId).getUserid().equals(this.contextUtil.getCurrentWorkbenchUserId())) {
			label.setStyleName("label-bold");
		}
		return label;
	}

	protected void initializeComponents() {
		this.setSelect(new TwinTableSelect<>(WorkbenchUser.class));

		final Table.ColumnGenerator tableLeftUserName = new Table.ColumnGenerator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return ProgramMembersPanel.this.generateUserNameCell(itemId);
			}
		};
		final Table.ColumnGenerator tableRightUserName = new Table.ColumnGenerator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return ProgramMembersPanel.this.generateUserNameCell(itemId);
			}

		};

		final Table.ColumnGenerator tableLeftRole = new Table.ColumnGenerator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return ProgramMembersPanel.this.generateRoleCell(itemId);
			}
		};
		final Table.ColumnGenerator tableRightRole = new Table.ColumnGenerator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return ProgramMembersPanel.this.generateRoleCell(itemId);
			}

		};

		this.getSelect().getTableLeft().addGeneratedColumn(ProgramMembersPanel.USERNAME, tableLeftUserName);
		this.getSelect().getTableLeft().addGeneratedColumn(ProgramMembersPanel.ROLE, tableLeftRole);
		this.getSelect().getTableRight().addGeneratedColumn(ProgramMembersPanel.USERNAME, tableRightUserName);
		this.getSelect().getTableRight().addGeneratedColumn(ProgramMembersPanel.ROLE, tableRightRole);

		this.getSelect()
				.setVisibleColumns(new Object[] { "select", ProgramMembersPanel.USERNAME, ProgramMembersPanel.ROLE });
		this.getSelect()
				.setColumnHeaders(new String[] { "<span class='glyphicon glyphicon-ok'></span>", "User Name", "Role" });

		this.getSelect().setLeftColumnCaption("Available Users");
		this.getSelect().setRightColumnCaption("Selected Program Members");

		this.getSelect().setRightLinkCaption("Remove Selected Members");
		this.getSelect().setLeftLinkCaption("");
		this.getSelect().addRightLinkListener(new Button.ClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -7548361229675101895L;


			@Override
			public void buttonClick(final ClickEvent event) {
				ProgramMembersPanel.this.getSelect().removeCheckedSelectedItems();
			}
		});

		this.addDragAndDropBehavior();
		this.addActionHandler();
		this.initializeMembersTable();
	}

	private void addActionHandler() {
		this.getSelect().getTableLeft().removeAllActionHandlers();
		this.getSelect().getTableRight().removeAllActionHandlers();
		this.getSelect().getTableLeft().setData("left");
		this.getSelect().getTableRight().setData("right");

		this.getSelect().getTableLeft().addActionHandler(this.getActionMenu(this.getSelect().getTableLeft()));
		this.getSelect().getTableRight().addActionHandler(this.getActionMenu(this.getSelect().getTableRight()));
	}

	private void addDragAndDropBehavior() {

		this.getSelect().getTableRight().setDropHandler(new DropHandler() {

			private static final long serialVersionUID = -8853235163238131008L;

			@SuppressWarnings("unchecked")
			@Override
			public void drop(final DragAndDropEvent dragAndDropEvent) {
				final DataBoundTransferable t = (DataBoundTransferable) dragAndDropEvent.getTransferable();

				if (t.getSourceComponent() == dragAndDropEvent.getTargetDetails().getTarget()) {
					return;
				}
				ProgramMembersPanel.this.setRoleSelectionWindow(new RoleSelectionWindow(ProgramMembersPanel.this,
					new Button.ClickListener() {

						private static final long serialVersionUID = 1L;

						@Override
						public void buttonClick(ClickEvent event) {

							final Role roleSelected = ProgramMembersPanel.this.getRoleSelected();
							if (roleSelected == null) {
								MessageNotifier.showWarning(ProgramMembersPanel.this.getWindow(), "Assign Role error",
									"Select a Program role for the user(s) selected");
								return;
							}

							Table source = (Table) t.getSourceComponent();
							Table target = (Table) dragAndDropEvent.getTargetDetails().getTarget();

							Object itemIdOver = t.getItemId();
							// temporarily disable the value change listener to avoid conflict
							target.removeListener(ProgramMembersPanel.this.getSelect().getTableValueChangeListener());

							Set<Object> sourceItemIds = (Set<Object>) source.getValue();
							for (Object itemId : sourceItemIds) {
								final List<UserRole> userRoles = new ArrayList<>();
								userRoles.add(ProgramMembersPanel.this.createUserRole(roleSelected, (WorkbenchUser) itemId));
								((WorkbenchUser) itemId).setRoles(userRoles);
								if (((WorkbenchUser) itemId).isEnabled()) {
									source.removeItem(itemId);
									target.addItem(itemId);
								}
							}

							boolean selectedItemIsDisabled = false;
							if (sourceItemIds.size() == 1) {
								if (!((WorkbenchUser) sourceItemIds.iterator().next()).isEnabled()) {
									selectedItemIsDisabled = true;
								}
							}

							if (itemIdOver != null && (sourceItemIds.isEmpty() || selectedItemIsDisabled)) {
								if (((WorkbenchUser) itemIdOver).isEnabled()) {
									final List<UserRole> userRoles = new ArrayList<>();
									userRoles.add(ProgramMembersPanel.this.createUserRole(roleSelected, (WorkbenchUser) itemIdOver));
									((WorkbenchUser) itemIdOver).setRoles(userRoles);
									source.removeItem(itemIdOver);
									target.addItem(itemIdOver);
								}
							}

							target.addListener(ProgramMembersPanel.this.getSelect().getTableValueChangeListener());

							ProgramMembersPanel.this.getRoleSelectionWindow().getParent()
								.removeWindow(ProgramMembersPanel.this.getRoleSelectionWindow());
						}

					}));

				getRoleSelectionWindow().setVisible(true);
				ProgramMembersPanel.this.getWindow().addWindow(getRoleSelectionWindow());
			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AbstractSelect.AcceptItem.ALL;
			}
		});

		this.getSelect().getTableLeft().setDropHandler(new DropHandler() {

			private static final long serialVersionUID = -885323516323813999L;

			@SuppressWarnings("unchecked")
			@Override
			public void drop(final DragAndDropEvent dragAndDropEvent) {
				final DataBoundTransferable t = (DataBoundTransferable) dragAndDropEvent.getTransferable();

				if (t.getSourceComponent() == dragAndDropEvent.getTargetDetails().getTarget()) {
					return;
				}

				Table source = (Table) t.getSourceComponent();
				Table target = (Table) dragAndDropEvent.getTargetDetails().getTarget();

				Object itemIdOver = t.getItemId();
				final WorkbenchUser workbenchUser = (WorkbenchUser) itemIdOver;
				workbenchUser.getRoles().remove(0);

				Set<Object> sourceItemIds = (Set<Object>) source.getValue();
				for (Object itemId : sourceItemIds) {
					if (((WorkbenchUser) itemId).isEnabled()) {
						source.removeItem(itemId);
						target.addItem(itemId);
					}
				}

				boolean selectedItemIsDisabled = false;
				if (sourceItemIds.size() == 1) {
					if (!((WorkbenchUser) sourceItemIds.iterator().next()).isEnabled()) {
						selectedItemIsDisabled = true;
					}
				}

				if (itemIdOver != null && (sourceItemIds.isEmpty() || selectedItemIsDisabled)) {
					if (((WorkbenchUser) itemIdOver).isEnabled()) {
						source.removeItem(itemIdOver);
						target.addItem(itemIdOver);
					}
				}

				MessageNotifier.showWarning(ProgramMembersPanel.this.getWindow(), "Information",
					"Selected members will no longer have a role associated to access current program. After saving these changes will be impacted.");

			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AbstractSelect.AcceptItem.ALL;
			}
		});
	}

	private Role getRoleSelected() {
		Integer roleId = (Integer) ProgramMembersPanel.this.getRoleSelectionWindow().getRolesComboBox().getValue();
		for (final Role role : ProgramMembersPanel.this.getRoles()) {
			if (role.getId().equals(roleId)) {
				return role;
			}
		}
		return null;
	}

	private UserRole createUserRole(final Role roleSelected, final WorkbenchUser workbenchUser) {
		UserRole userRole = new UserRole();
		userRole.setRole(roleSelected);
		userRole.setUser(workbenchUser);
		userRole.setWorkbenchProject(ProgramMembersPanel.this.project);
		userRole.setCropType(ProgramMembersPanel.this.project.getCropType());
		return userRole;
	}

	final Action actionAddToProgramMembers = new Action("Add Selected Items");
	final Action actionRemoveFromProgramMembers = new Action("Remove Selected Items");
	final Action actionSelectAll = new Action("Select All");
	final Action actionDeSelectAll = new Action("De-select All");

	private Action.Handler getActionMenu(final Table table) {
		Action.Handler actionMenu = new Action.Handler() {

			@Override
			public Action[] getActions(final Object target, final Object sender) {

				if (table.getData().toString().equals("left")) {
					return new Action[] {actionAddToProgramMembers, actionSelectAll, actionDeSelectAll};
				} else {
					return new Action[] {actionRemoveFromProgramMembers, actionSelectAll, actionDeSelectAll};
				}

			}

			@Override
			public void handleAction(final Action action, final Object sender, final Object target) {
				if (actionSelectAll == action) {
					if (table.getData().toString().equals("left")) {
						ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().setValue(true);
						ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().click();
					} else {
						ProgramMembersPanel.this.getSelect().getChkSelectAllRight().setValue(true);
						ProgramMembersPanel.this.getSelect().getChkSelectAllRight().click();
					}

				} else if (actionDeSelectAll == action) {
					if (table.getData().toString().equals("left")) {
						ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().setValue(false);
						ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().click();
					} else {
						ProgramMembersPanel.this.getSelect().getChkSelectAllRight().setValue(false);
						ProgramMembersPanel.this.getSelect().getChkSelectAllRight().click();
					}
				} else if (actionAddToProgramMembers == action) {
					ProgramMembersPanel.this.addCheckedSelectedItems();
				} else if (actionRemoveFromProgramMembers == action) {
					ProgramMembersPanel.this.removeCheckedSelectedItems();
				}

			}

		};

		return actionMenu;
	}

	private void removeCheckedSelectedItems() {

		if (((Set<Object>) ProgramMembersPanel.this.getSelect().getTableRight().getValue()).size() != 0) {
			MessageNotifier.showWarning(ProgramMembersPanel.this.getWindow(), "Information",
				"Selected members will no longer have a role associated to access current program. After saving these changes will be impacted.");
		}

		for (Object itemId : (Set<Object>) ProgramMembersPanel.this.getSelect().getTableRight().getValue()) {
			if (((WorkbenchUser) itemId).isActive() && ((WorkbenchUser) itemId).isEnabled()) {
				((WorkbenchUser) itemId).setActive(false);
				ProgramMembersPanel.this.getSelect().getTableLeft().addItem(itemId);
				ProgramMembersPanel.this.getSelect().getTableRight().removeItem(itemId);
				ProgramMembersPanel.this.getSelect().getChkSelectAllRight().setValue(false);
			}
		}

	}

	private void addCheckedSelectedItems() {

		if (((Set<Object>) ProgramMembersPanel.this.getSelect().getTableLeft().getValue()).size() != 0) {

			ProgramMembersPanel.this.setRoleSelectionWindow(new RoleSelectionWindow(ProgramMembersPanel.this,
				new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {

						final Role roleSelected =  ProgramMembersPanel.this.getRoleSelected();

						if (roleSelected == null) {
							MessageNotifier.showWarning(ProgramMembersPanel.this.getWindow(), "Assign Role error",
								"Select a Program role for the user(s) selected");
							return;
						}

						for (Object itemId : (Set<Object>) ProgramMembersPanel.this.getSelect().getTableLeft().getValue()) {
							if (((WorkbenchUser) itemId).isActive() && ((WorkbenchUser) itemId).isEnabled()) {
								((WorkbenchUser) itemId).setActive(false);
								final List<UserRole> userRoles = new ArrayList<>();
								userRoles.add(ProgramMembersPanel.this.createUserRole(roleSelected, (WorkbenchUser) itemId));
								((WorkbenchUser) itemId).setRoles(userRoles);
								ProgramMembersPanel.this.getSelect().getTableRight().addItem(itemId);
								ProgramMembersPanel.this.getSelect().getTableLeft().removeItem(itemId);
								ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().setValue(false);
							}
						}

						ProgramMembersPanel.this.getRoleSelectionWindow().getParent()
							.removeWindow(ProgramMembersPanel.this.getRoleSelectionWindow());
					}

				}));

			getRoleSelectionWindow().setVisible(true);
			ProgramMembersPanel.this.getWindow().addWindow(getRoleSelectionWindow());

		}

	}

	private Table initializeMembersTable() {
		this.tblMembers = new Table();
		this.tblMembers.setDebugId("tblMembers");
		this.tblMembers.setImmediate(true);

		final List<Object> columnIds = new ArrayList<>();
		columnIds.add(ProgramMembersPanel.USERNAME);
		final List<String> columnHeaders = new ArrayList<>();
		columnHeaders.add("Member");

		// prepare the container
		final IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("userId", Integer.class, null);
		container.addContainerProperty(ProgramMembersPanel.USERNAME, String.class, null);
		container.addContainerProperty(ProgramMembersPanel.ROLE, String.class, null);
		this.tblMembers.setContainerDataSource(container);

		this.tblMembers.setVisibleColumns(columnIds.toArray(new Object[0]));
		this.tblMembers.setColumnHeaders(columnHeaders.toArray(new String[0]));

		this.tblMembers.setEditable(true);
		this.tblMembers.setTableFieldFactory(new TableFieldFactory() {

			private static final long serialVersionUID = 1L;

			@Override
			public Field createField(final Container container, final Object itemId, final Object propertyId,
					final Component uiContext) {
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
			this.getSelect().setContainerDataSource(container);
			this.getSelect().setLeftVisibleColumns(new Object[] {"select", ProgramMembersPanel.USERNAME});
			this.getSelect()
				.setLeftColumnHeaders(new String[] {"<span class='glyphicon glyphicon-ok'></span>", "User Name"});

		} catch (final MiddlewareQueryException e) {
			ProgramMembersPanel.LOG.error("Error encountered while getting workbench users", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}
	}

	protected void initializeLayout() {
		this.setStyleName(Reindeer.PANEL_LIGHT);

		final HorizontalLayout titleContainer = new HorizontalLayout();
		titleContainer.setDebugId("titleContainer");
		final Label heading = new Label(
			"<span class='bms-members' style='color: #D1B02A; font-size: 23px'></span>&nbsp;Program Members",
			Label.CONTENT_XHTML);
		final Label headingDesc = new Label(
			"Choose team members for this program by dragging available users from the list on the left into the Program Members list on the right.");

		heading.setStyleName(Bootstrap.Typography.H4.styleName());

		titleContainer.addComponent(heading);
		titleContainer.setSizeUndefined();
		titleContainer.setWidth("100%");
		// move this to css
		titleContainer.setMargin(true, false, false, false);

		final VerticalLayout root = new VerticalLayout();
		root.setDebugId("root");
		root.setMargin(new Layout.MarginInfo(false, true, true, true));
		root.setSpacing(true);
		root.setSizeFull();

		root.addComponent(titleContainer);
		root.addComponent(headingDesc);

		final ComponentContainer buttonArea = this.layoutButtonArea();

		root.addComponent(this.getSelect());

		root.addComponent(buttonArea);
		root.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);

		this.setScrollable(false);

		this.setSizeFull();
		this.setContent(root);
	}

	protected void initializeUsers() {
		final Container container = this.tblMembers.getContainerDataSource();
		final List<Integer> userIDs = this.workbenchDataManager
			.getActiveUserIDsByProjectId(this.project.getProjectId(), this.project.getCropType().getCropName());
		final Set<WorkbenchUser> selectedItems = new HashSet<>();

		for (final Integer userID : userIDs) {
			final WorkbenchUser userTemp = this.workbenchDataManager.getUserById(userID);

			if (!userTemp.isSuperAdmin()) {
				selectedItems.add(userTemp);

				container.removeItem(userTemp);

				final Item item = container.addItem(userTemp);
				item.getItemProperty("userId").setValue(1);
				item.getItemProperty(ProgramMembersPanel.USERNAME).setValue(userTemp.getPerson().getDisplayName());
				this.getSelect().select(userTemp);
			}
		}
	}

	protected void initializeActions() {
		this.saveButton.addListener(new SaveUsersInProjectAction(this.project, this.getSelect()));
		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8879824681692031501L;

			@Override
			public void buttonClick(final ClickEvent event) {
				ProgramMembersPanel.this.assemble();
			}
		});
	}

	protected ComponentContainer layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Reset");
		this.cancelButton.setDebugId("cancelButton");
		this.saveButton = new Button("Save");
		this.saveButton.setDebugId("saveButton");

		this.saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.saveButton);

		return buttonLayout;
	}

	protected Container createUsersContainer() {
		final List<WorkbenchUser> validUserList = new ArrayList<>();

		final String cropName = this.contextUtil.getProjectInContext().getCropType().getCropName();

		// TODO: This can be improved once we implement proper User-Person
		// mapping
		final List<WorkbenchUser> userList = this.workbenchDataManager.getUsersByCrop(cropName);

		for (final WorkbenchUser user : userList) {
			final Person person = this.workbenchDataManager.getPersonById(user.getPersonid());
			if (!user.isSuperAdmin()) {
				user.setPerson(person);

				if (person != null) {
					validUserList.add(user);
				}
			}
		}

		final BeanItemContainer<WorkbenchUser> beanItemContainer = new BeanItemContainer<>(WorkbenchUser.class);
		for (final WorkbenchUser user : validUserList) {
			if (user.getUserid().equals(this.contextUtil.getCurrentWorkbenchUserId())
					|| user.getUserid().equals(this.project.getUserId())) {
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
			final Set<WorkbenchUser> members = this.getSelect().getValue();

			this.project.setMembers(members);
		}
		// members not required, so even if there are no values, this returns
		// true
		return true;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public Set<WorkbenchUser> getProgramMembersDisplayed() {
		return this.getSelect().getValue();
	}

	
	public Project getProject() {
		return this.project;
	}

	public Collection<Role> getRoles() {
		return this.workbenchDataManager.getRoles(new RoleSearchDto(Boolean.TRUE, RoleType.PROGRAM.getId(), null) );
	}

	public TwinTableSelect<WorkbenchUser> getSelect() {
		return select;
	}

	public void setSelect(TwinTableSelect<WorkbenchUser> select) {
		this.select = select;
	}

	public RoleSelectionWindow getRoleSelectionWindow() {
		return roleSelectionWindow;
	}

	public void setRoleSelectionWindow(RoleSelectionWindow roleSelectionWindow) {
		this.roleSelectionWindow = roleSelectionWindow;
	}
}
