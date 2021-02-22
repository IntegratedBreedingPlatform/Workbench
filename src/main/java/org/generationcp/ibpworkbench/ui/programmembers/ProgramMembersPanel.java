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
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
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
import org.generationcp.middleware.service.api.user.UserService;
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
import java.util.Optional;
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
	private UserService userService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private ContextUtil contextUtil;

	private final Project project;

	private RoleSelectionWindow roleSelectionWindow;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

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
		final String role = this.getUserRole(itemId);
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

		this.getSelect().setLeftColumnCaption(this.messageSource.getMessage(Message.MEMBERS_TAB_AVAILABLE_USERS));
		this.getSelect().setRightColumnCaption(this.messageSource.getMessage(Message.MEMBERS_TAB_SELECTED_PROGRAM_MEMBERS));

		this.getSelect().setRightLinkCaption(this.messageSource.getMessage(Message.MEMBERS_TAB_REMOVE_SELECTED_MEMBERS));
		this.getSelect().setLeftLinkCaption("");
		this.getSelect().addRightLinkListener(new Button.ClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -7548361229675101895L;


			@Override
			public void buttonClick(final ClickEvent event) {
				final Integer removedItemsCount = ProgramMembersPanel.this.getSelect().removeCheckedSelectedItems();
				if (removedItemsCount > 0) {
					MessageNotifier.showWarning(ProgramMembersPanel.this.getWindow(), "Information",
						ProgramMembersPanel.this.messageSource.getMessage(Message.MEMBERS_TAB_UNSELECT_MEMBERS_CONFIRMATION_MESSAGE));
				}
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
						public void buttonClick(final ClickEvent event) {

							final Role roleSelected = ProgramMembersPanel.this.getRoleSelected();
							if (roleSelected == null) {
								MessageNotifier.showWarning(ProgramMembersPanel.this.getWindow(), ProgramMembersPanel.this.messageSource.getMessage(Message.MEMBERS_TAB_ASSIGN_ROLE_ERROR),
									ProgramMembersPanel.this.messageSource.getMessage(Message.MEMBERS_TAB_SELECT_PROGRAM_ROLE));
								return;
							}

							final Table source = (Table) t.getSourceComponent();
							final Table target = (Table) dragAndDropEvent.getTargetDetails().getTarget();

							final Object itemIdOver = t.getItemId();
							// temporarily disable the value change listener to avoid conflict
							target.removeListener(ProgramMembersPanel.this.getSelect().getTableValueChangeListener());

							final Set<Object> sourceItemIds = (Set<Object>) source.getValue();
							for (final Object itemId : sourceItemIds) {
								final UserRole userRole = ProgramMembersPanel.this.createUserRole(roleSelected, (WorkbenchUser) itemId);
								((WorkbenchUser) itemId).getRoles().add(userRole);
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
									final UserRole newUserRole =
										ProgramMembersPanel.this.createUserRole(roleSelected, (WorkbenchUser) itemIdOver);
									((WorkbenchUser) itemIdOver).getRoles().add(newUserRole);
									source.removeItem(itemIdOver);
									target.addItem(itemIdOver);
								}
							}
							target.addListener(ProgramMembersPanel.this.getSelect().getTableValueChangeListener());
							ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().setValue(false);
							ProgramMembersPanel.this.getRoleSelectionWindow().getParent()
								.removeWindow(ProgramMembersPanel.this.getRoleSelectionWindow());
						}

					}));

				ProgramMembersPanel.this.getRoleSelectionWindow().setVisible(true);
				ProgramMembersPanel.this.getWindow().addWindow(ProgramMembersPanel.this.getRoleSelectionWindow());
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

				final Table source = (Table) t.getSourceComponent();
				final Table target = (Table) dragAndDropEvent.getTargetDetails().getTarget();

				final Object itemIdOver = t.getItemId();
				final Set<Object> sourceItemIds = new HashSet<>((Set<Object>) source.getValue());
				sourceItemIds.add(itemIdOver);

				int droppedItemsCount = 0;
				for (final Object itemId : sourceItemIds) {
					if (((WorkbenchUser) itemId).isEnabled()) {
						final WorkbenchUser workbenchUser = (WorkbenchUser) itemId;

						final Optional<UserRole>
							programRole = workbenchUser.getRoles().stream()
							.filter(
								ur -> ur.getWorkbenchProject() != null && ur.getWorkbenchProject().equals(ProgramMembersPanel.this.project))
							.findFirst();

						workbenchUser.getRoles().remove(programRole.get());

						source.removeItem(itemId);
						target.addItem(itemId);
						droppedItemsCount++;
					}
				}

				ProgramMembersPanel.this.getSelect().getChkSelectAllRight().setValue(false);
				if (droppedItemsCount > 0) {
					MessageNotifier.showWarning(ProgramMembersPanel.this.getWindow(), "Information",
						ProgramMembersPanel.this.messageSource.getMessage(Message.MEMBERS_TAB_UNSELECT_MEMBERS_CONFIRMATION_MESSAGE));
				}


			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AbstractSelect.AcceptItem.ALL;
			}
		});
	}

	private Role getRoleSelected() {
		final Integer roleId = (Integer) this.getRoleSelectionWindow().getRolesComboBox().getValue();
		for (final Role role : this.getRoles()) {
			if (role.getId().equals(roleId)) {
				return role;
			}
		}
		return null;
	}

	private UserRole createUserRole(final Role roleSelected, final WorkbenchUser workbenchUser) {
		final UserRole userRole = new UserRole();
		userRole.setRole(roleSelected);
		userRole.setUser(workbenchUser);
		userRole.setWorkbenchProject(this.project);
		userRole.setCropType(this.project.getCropType());
		return userRole;
	}

	final Action actionRemoveFromProgramMembers = new Action("Remove Selected Items");
	final Action actionSelectAll = new Action("Select All");
	final Action actionDeSelectAll = new Action("De-select All");

	private Action.Handler getActionMenu(final Table table) {
		final Action.Handler actionMenu = new Action.Handler() {

			@Override
			public Action[] getActions(final Object target, final Object sender) {

				if (table.getData().toString().equals("left")) {
					return new Action[] {
						ProgramMembersPanel.this.actionSelectAll,
						ProgramMembersPanel.this.actionDeSelectAll};
				} else {
					return new Action[] {
						ProgramMembersPanel.this.actionRemoveFromProgramMembers, ProgramMembersPanel.this.actionSelectAll,
						ProgramMembersPanel.this.actionDeSelectAll};
				}

			}

			@Override
			public void handleAction(final Action action, final Object sender, final Object target) {
				if (ProgramMembersPanel.this.actionSelectAll == action) {
					if (table.getData().toString().equals("left")) {
						ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().setValue(true);
						ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().click();
					} else {
						ProgramMembersPanel.this.getSelect().getChkSelectAllRight().setValue(true);
						ProgramMembersPanel.this.getSelect().getChkSelectAllRight().click();
					}

				} else if (ProgramMembersPanel.this.actionDeSelectAll == action) {
					if (table.getData().toString().equals("left")) {
						ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().setValue(false);
						ProgramMembersPanel.this.getSelect().getChkSelectAllLeft().click();
					} else {
						ProgramMembersPanel.this.getSelect().getChkSelectAllRight().setValue(false);
						ProgramMembersPanel.this.getSelect().getChkSelectAllRight().click();
					}
				} else if (ProgramMembersPanel.this.actionRemoveFromProgramMembers == action) {
					ProgramMembersPanel.this.removeCheckedSelectedItems();
				}

			}

		};

		return actionMenu;
	}

	private void removeCheckedSelectedItems() {

		if (((Set<Object>) this.getSelect().getTableRight().getValue()).size() != 0) {
			MessageNotifier.showWarning(this.getWindow(), "Information",
				this.messageSource.getMessage(Message.MEMBERS_TAB_UNSELECT_MEMBERS_CONFIRMATION_MESSAGE));
		}

		for (final Object itemId : (Set<Object>) this.getSelect().getTableRight().getValue()) {
			if (((WorkbenchUser) itemId).isActive() && ((WorkbenchUser) itemId).isEnabled()) {
				((WorkbenchUser) itemId).setActive(false);
				this.getSelect().getTableLeft().addItem(itemId);
				this.getSelect().getTableRight().removeItem(itemId);
				this.getSelect().getChkSelectAllRight().setValue(false);
			}
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
			this.messageSource.getMessage(Message.MEMBERS_TAB_CHOOSE_TEAM_MEMBERS));

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
		final List<Integer> userIDs =
			this.userService.getActiveUserIDsWithAccessToTheProgram(this.project.getProjectId());
		final Set<WorkbenchUser> selectedItems = new HashSet<>();

		for (final Integer userID : userIDs) {
			final WorkbenchUser userTemp = this.userService.getUserById(userID);
			final boolean enabled = this.isUserEnabled(userTemp);
			userTemp.setEnabled(enabled);

			selectedItems.add(userTemp);
			container.removeItem(userTemp);

			final Item item = container.addItem(userTemp);
			item.getItemProperty("userId").setValue(1);
			item.getItemProperty(ProgramMembersPanel.USERNAME).setValue(userTemp.getPerson().getDisplayName());
			this.getSelect().select(userTemp);

		}
	}

	protected void initializeActions() {
		this.saveButton.addListener(new SaveUsersInProjectAction(this));
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
		final String cropName = this.contextUtil.getProjectInContext().getCropType().getCropName();
		final List<WorkbenchUser> userList = this.userService.getUsersByCrop(cropName);
		final BeanItemContainer<WorkbenchUser> beanItemContainer = new BeanItemContainer<>(WorkbenchUser.class);
		for (final WorkbenchUser user : userList) {
			final boolean enabled = this.isUserEnabled(user);
			user.setEnabled(enabled);
			beanItemContainer.addBean(user);
		}
		return beanItemContainer;
	}

	public boolean validate() {
		return true;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
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
		return this.select;
	}

	public void setSelect(final TwinTableSelect<WorkbenchUser> select) {
		this.select = select;
	}

	public RoleSelectionWindow getRoleSelectionWindow() {
		return this.roleSelectionWindow;
	}

	public void setRoleSelectionWindow(final RoleSelectionWindow roleSelectionWindow) {
		this.roleSelectionWindow = roleSelectionWindow;
	}

	private String getUserRole(final Object itemId) {
		final String cropName = this.contextUtil.getProjectInContext().getCropType().getCropName();
		final WorkbenchUser workbenchUser = (WorkbenchUser) itemId;
		if (workbenchUser != null && workbenchUser.getRoles() != null && !workbenchUser.getRoles().isEmpty()) {
			if (workbenchUser.hasInstanceRole()) {
				return workbenchUser.getInstanceRole().getCapitalizedRole();
			}
			if (workbenchUser.hasCropRole(cropName) && !workbenchUser.hasProgramRoles(cropName)) {
				return workbenchUser.getCropRole(cropName).getCapitalizedRole();
			}

			final Optional<UserRole>
				programRole = workbenchUser.getRoles().stream()
				.filter(ur -> ur.getWorkbenchProject() != null && ur.getWorkbenchProject().equals(this.project)).findFirst();
			if (programRole.isPresent()) {
				return programRole.get().getCapitalizedRole();
			} else {
				return "";
			}
		}
		return "";
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private boolean isUserEnabled(final WorkbenchUser user) {
		final String cropName = this.contextUtil.getProjectInContext().getCropType().getCropName();
		final int currentUserId = this.contextUtil.getCurrentWorkbenchUserId();
		if (user.getUserid().equals(currentUserId)
			|| user.getUserid().equals(this.project.getUserId()) || user.hasInstanceRole() || (user.hasCropRole(cropName)
			&& !user.hasProgramRoles(cropName))) {
			return false;
		}
		return true;
	}

}
