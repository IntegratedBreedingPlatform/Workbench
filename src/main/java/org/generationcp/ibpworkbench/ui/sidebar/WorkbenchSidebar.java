
package org.generationcp.ibpworkbench.ui.sidebar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction;
import org.generationcp.ibpworkbench.actions.DeleteProjectAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.OpenProgramLocationsAction;
import org.generationcp.ibpworkbench.actions.OpenProgramMethodsAction;
import org.generationcp.ibpworkbench.actions.OpenToolVersionsAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction;
import org.generationcp.ibpworkbench.actions.OpenWorkflowForRoleAction;
import org.generationcp.ibpworkbench.ui.programadministration.OpenManageProgramPageAction;
import org.generationcp.ibpworkbench.ui.project.create.OpenUpdateProjectPageAction;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/19/13 Time: 7:22 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class WorkbenchSidebar extends CssLayout implements InitializingBean {

	/**
	 *
	 */
	private static final long serialVersionUID = 5744204745926145144L;
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebar.class);
	public static Map<String, TreeItem> sidebarTreeMap = new HashMap<>();

	@Autowired
	private SessionData sessionData;

	private final WorkbenchSidebarPresenter presenter;
	private Tree sidebarTree;

	private final ItemClickEvent.ItemClickListener treeClickListener = new ItemClickEvent.ItemClickListener() {

		@Override
		public void itemClick(ItemClickEvent event) {
			if (event.getItemId() == null) {
				return;
			}

			WorkbenchSidebar.LOG.trace(event.getItemId().toString());

			TreeItem treeItem = (TreeItem) event.getItemId();

			if (!WorkbenchSidebar.this.doCollapse(treeItem)) {
				WorkbenchSidebar.this.presenter.updateProjectLastOpenedDate();

				ActionListener listener =
						WorkbenchSidebar.this.getLinkActions(treeItem.getId(), WorkbenchSidebar.this.sessionData.getSelectedProject());

				listener.doAction(event.getComponent().getWindow(), "/" + treeItem.getId(), true);
			}
		}
	};

	public WorkbenchSidebar() {
		this.presenter = new WorkbenchSidebarPresenter(this);
	}

	@Override
	public void afterPropertiesSet() {
		this.assemble();
	}

	protected void initializeComponents() {
		this.sidebarTree = new Tree();

		this.addComponent(this.sidebarTree);
	}

	protected void initializeLayout() {
		// do nothing
	}

	protected void initializeActions() {
		// do nothing
	}

	public void populateLinks() {
		this.removeAllComponents();

		this.sidebarTree = new Tree();
		this.sidebarTree.setImmediate(true);

		Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> links = this.presenter.getCategoryLinkItems();
		this.sidebarTree.setContainerDataSource(new HierarchicalContainer());
		this.sidebarTree.addContainerProperty("id", String.class, "");
		this.sidebarTree.addContainerProperty("caption", String.class, "");
		this.sidebarTree.addContainerProperty("value", Object.class, null);

		boolean expandedFirst = false;
		for (WorkbenchSidebarCategory category : links.keySet()) {
			TreeItem parentItem = new TreeItem(category.getSidebarCategoryName(), category.getSidebarCategorylabel(), null);

			WorkbenchSidebar.sidebarTreeMap.put(category.getSidebarCategoryName(), parentItem);

			Item parent = this.sidebarTree.addItem(parentItem);

			this.sidebarTree.setChildrenAllowed(parent, true);
			this.sidebarTree.setItemCaption(parentItem, parentItem.getCaption());
			for (WorkbenchSidebarCategoryLink link : links.get(category)) {
				TreeItem item = new TreeItem(link.getTool().getToolName(), link.getSidebarLinkTitle(), link);

				WorkbenchSidebar.sidebarTreeMap.put(link.getSidebarLinkName(), item);

				this.sidebarTree.addItem(item);
				this.sidebarTree.setParent(item, parentItem);
				this.sidebarTree.setChildrenAllowed(item, false);
				this.sidebarTree.setItemCaption(item, item.getCaption());
			}

			if (!expandedFirst) {
				this.sidebarTree.expandItem(parentItem);
				expandedFirst = true;
			}

		}

		this.sidebarTree.setSelectable(true);
		this.sidebarTree.addListener(this.treeClickListener);

		this.addComponent(this.sidebarTree);
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	public void clearLinks() {
		if (this.sidebarTree != null) {
			this.sidebarTree.setContainerDataSource(new HierarchicalContainer());
		}
	}

	public void selectItem(TreeItem item) {
		this.sidebarTree.setValue(item);
	}

	private boolean doCollapse(TreeItem treeItem) {
		if (treeItem.getValue() != null) {
			return false;
		}

		if (this.sidebarTree.isExpanded(treeItem)) {
			this.sidebarTree.collapseItem(treeItem);
		} else {
			this.sidebarTree.expandItem(treeItem);
		}

		return true;
	}

	private ActionListener getLinkActions(final String toolName, Project project) {
		if (toolName == null) {
			return null;
		}

		if (ToolEnum.isCorrectTool(toolName)) {
			return new LaunchWorkbenchToolAction(ToolEnum.equivalentToolEnum(toolName));
		} else if (ChangeWindowAction.WindowEnums.isCorrectTool(toolName)) {
			return new ChangeWindowAction(ChangeWindowAction.WindowEnums.equivalentWindowEnum(toolName), project);
		} else if (OpenWindowAction.WindowEnum.isCorrectTool(toolName)) {
			return new OpenWindowAction(OpenWindowAction.WindowEnum.equivalentWindowEnum(toolName), project);
		} else if (toolName.equals("manage_program")) {
			return new OpenManageProgramPageAction();
		} else if (toolName.equals("tool_versions")) {
			return new OpenToolVersionsAction();
		} else if (toolName.equals("update_project")) {
			return new OpenUpdateProjectPageAction();
		} else if (toolName.equals("project_method")) {
			return new OpenProgramMethodsAction(project);
		} else if (toolName.equals("project_location")) {
			return new OpenProgramLocationsAction(project, this.sessionData.getUserData());
		} else if (toolName.equals("delete_project")) {
			return new DeleteProjectAction();
		} else {
			try {
				List<Role> roles = this.presenter.getRoleByTemplateName(toolName);
				if (roles.size() > 0) {
					final Role role1 = roles.get(0);

					return new OpenWorkflowForRoleAction(project) {

						@Override
						public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {

							if (role1.getWorkflowTemplate() == null) {
								WorkbenchSidebar.LOG.warn("No workflow template assigned to role: {}", role1);
								return;
							}
							super.showWorkflowDashboard(super.project, role1, (IContentWindow) window);

						}
					};
				}
			} catch (IndexOutOfBoundsException e) {
				// IGNORE
			}
		}

		return null;
	}

	public class TreeItem {

		private String id;
		private Object value;
		private String caption;

		public TreeItem(String id, String caption, Object action) {
			this.id = id;
			this.value = action;
			this.caption = caption;
		}

		public String getId() {
			return this.id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Object getValue() {
			return this.value;
		}

		public void setValue(String caption) {
			this.caption = caption;
		}

		public void setValue(Object action) {
			this.value = action;
		}

		public String getCaption() {
			return this.caption;
		}
	}
}
