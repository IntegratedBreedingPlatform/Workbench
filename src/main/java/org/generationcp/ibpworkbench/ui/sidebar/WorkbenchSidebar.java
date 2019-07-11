package org.generationcp.ibpworkbench.ui.sidebar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction;
import org.generationcp.ibpworkbench.actions.DeleteProjectAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.OpenProgramLocationsAction;
import org.generationcp.ibpworkbench.actions.OpenProgramMethodsAction;
import org.generationcp.ibpworkbench.actions.OpenToolVersionsAction;
import org.generationcp.ibpworkbench.actions.PageAction;
import org.generationcp.ibpworkbench.ui.programadministration.OpenManageProgramPageAction;
import org.generationcp.ibpworkbench.ui.project.create.OpenUpdateProjectPageAction;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Tree;

@Configurable
public class WorkbenchSidebar extends CssLayout implements InitializingBean {

	private static final String DELETE_PROJECT = "delete_project";
	private static final String PROJECT_LOCATION = "project_location";
	private static final String PROJECT_METHOD = "project_method";
	private static final String UPDATE_PROJECT = "update_project";
	private static final String TOOL_VERSIONS = "tool_versions";
	private static final String MANAGE_PROGRAM = "manage_program";
	private static final long serialVersionUID = 5744204745926145144L;
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebar.class);
	public static final Map<String, TreeItem> sidebarTreeMap = new HashMap<>();

	private WorkbenchSidebarPresenter presenter;

	private Tree sidebarTree;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ContextUtil contextUtil;

	public WorkbenchSidebar() {
		this.presenter = new WorkbenchSidebarPresenter();
	}

	@Override
	public void afterPropertiesSet() {
		this.initializeComponents();
		this.populateLinks();
	}

	protected void initializeComponents() {
		this.sidebarTree = new Tree();
		this.sidebarTree.setDebugId("sidebarTree");

		this.addComponent(this.sidebarTree);
	}

	void populateLinks() {
		this.removeAllComponents();

		this.sidebarTree = new Tree();
		this.sidebarTree.setDebugId("sidebarTree");
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
		this.sidebarTree.addListener(new TreeItemClickListener(this));

		this.addComponent(this.sidebarTree);
	}

	public void selectItem(TreeItem item) {
		this.sidebarTree.setValue(item);
	}

	protected boolean doCollapse(TreeItem treeItem) {
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

	protected ActionListener getLinkActions(final String toolName, Project project) {
		if (toolName == null) {
			return null;
		}

		if (ToolName.isURLAccessibleTool(toolName)) {
			return new LaunchWorkbenchToolAction(ToolName.equivalentToolEnum(toolName));
		} else if (ChangeWindowAction.WindowEnums.isCorrectTool(toolName)) {
			return new ChangeWindowAction(ChangeWindowAction.WindowEnums.equivalentWindowEnum(toolName), project);
		} else if (MANAGE_PROGRAM.equals(toolName)) {
			return new OpenManageProgramPageAction();
		} else if (TOOL_VERSIONS.equals(toolName)) {
			return new OpenToolVersionsAction();
		} else if (UPDATE_PROJECT.equals(toolName)) {
			return new OpenUpdateProjectPageAction();
		} else if (PROJECT_METHOD.equals(toolName)) {
			return new OpenProgramMethodsAction();
		} else if (PROJECT_LOCATION.equals(toolName)) {
			return new OpenProgramLocationsAction();
		} else if (DELETE_PROJECT.equals(toolName)) {
			return new DeleteProjectAction();
		}

		return null;
	}

	public void setSidebarTree(final Tree sidebarTree) {
		this.sidebarTree = sidebarTree;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setPresenter(final WorkbenchSidebarPresenter presenter) {
		this.presenter = presenter;
	}

	public void setTransactionManager(final PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
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

	class TreeItemClickListener implements ItemClickEvent.ItemClickListener {

		private WorkbenchSidebar workbenchSidebar;

		public TreeItemClickListener(final WorkbenchSidebar workbenchSidebar) {
			TreeItemClickListener.this.workbenchSidebar = workbenchSidebar;
		}

		@Override
		public void itemClick(final ItemClickEvent event) {
			final TransactionTemplate transactionTemplate = new TransactionTemplate(WorkbenchSidebar.this.transactionManager);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					if (event.getItemId() == null) {
						return;
					}

					WorkbenchSidebar.LOG.trace(event.getItemId().toString());

					TreeItem treeItem = (TreeItem) event.getItemId();

					if (!TreeItemClickListener.this.workbenchSidebar.doCollapse(treeItem)) {
						WorkbenchSidebar.this.presenter.updateProjectLastOpenedDate();

						ActionListener listener = TreeItemClickListener.this.workbenchSidebar.getLinkActions(treeItem.getId(), contextUtil.getProjectInContext());

						listener.doAction(event.getComponent().getWindow(), "/" + treeItem.getId(), true);
					}
				}
			});
		}

	}
}
