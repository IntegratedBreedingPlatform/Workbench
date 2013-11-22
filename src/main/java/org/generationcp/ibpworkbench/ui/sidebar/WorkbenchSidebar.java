package org.generationcp.ibpworkbench.ui.sidebar;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.treetable.HierarchicalContainerOrderedWrapper;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.actions.*;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.project.create.OpenUpdateProjectPageAction;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.pojos.workbench.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkbenchSidebar extends CssLayout {
    private WorkbenchSidebarPresenter presenter;
    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebar.class);
    private Tree sidebarTree;
    private Project project;
    private Role role;

    public static WorkbenchSidebar thisInstance;

    public WorkbenchSidebar(Project project,Role role) {
        presenter = new WorkbenchSidebarPresenter(this,project);
        thisInstance = this;

        this.project = project;
        try {
            assemble();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected void initializeComponents() {
        sidebarTree = new Tree();

        this.addComponent(sidebarTree);
    }

    protected void initializeLayout() {

    }

    protected void initializeActions() {

    }

    public void populateLinks() {
        Map<WorkbenchSidebarCategory,List<Tool>> links = presenter.getCategoryLinkItems();
        sidebarTree.setContainerDataSource(new HierarchicalContainer());
        sidebarTree.addContainerProperty("id",String.class,"");
        sidebarTree.addContainerProperty("caption",String.class,"");
        sidebarTree.addContainerProperty("value",Object.class,null);

        for (WorkbenchSidebarCategory category : links.keySet()) {
            TreeItem parentItem;
            parentItem = new TreeItem(category.getSidebarCategoryName(),category.getSidebarCategorylabel(),null);

            Item parent = sidebarTree.addItem(parentItem);

            sidebarTree.setChildrenAllowed(parent, true);
            sidebarTree.setItemCaption(parentItem,parentItem.getCaption());
            for (Tool link : links.get(category)) {
                TreeItem item = new TreeItem(link.getToolName(),link.getTitle(),link);
                sidebarTree.addItem(item);
                sidebarTree.setParent(item, parentItem);
                sidebarTree.setChildrenAllowed(item,false);
                sidebarTree.setItemCaption(item,item.getCaption());
            }
        }

        sidebarTree.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                WorkbenchSidebar.this.project = IBPWorkbenchApplication.get().getSessionData().getSelectedProject();
                if (event.getItemId() == null || WorkbenchSidebar.this.project == null)
                    return;
                else {
                    TreeItem treeItem = (TreeItem) event.getItemId();
                    if (treeItem.getId() == null)
                        return;

                    ActionListener listener = WorkbenchSidebar.this.getLinkActions(treeItem.getId(),WorkbenchSidebar.this.project);
                    if (listener instanceof LaunchWorkbenchToolAction) {
                        ((LaunchWorkbenchToolAction)listener).launchTool(treeItem.getId(),WorkbenchMainView.getInstance(),true);
                    }
                    if (listener instanceof OpenWindowAction) {
                        ((OpenWindowAction)listener).launchWindow(WorkbenchMainView.getInstance(),treeItem.getId());
                    }

                    else {
                        listener.doAction(WorkbenchMainView.getInstance(),treeItem.getId(),true);
                    }
                }

                LOG.trace(event.getItemId().toString());
            }
        });

    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    private class TreeItem {
        private String id;
        private Object value;
        private String caption;


        public TreeItem(String id,String caption, Object action) {
            this.id = id;
            this.value = action;
            this.caption = caption;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object action) {
            this.value = action;
        }

        public String getCaption() {
            return caption;
        }

        public void setValue(String caption) {
            this.caption = caption;
        }
    }

    private ActionListener getLinkActions(final String toolName,Project project) {
        if (toolName == null) return null;

        if (LaunchWorkbenchToolAction.ToolEnum.isCorrectTool(toolName)) {
            return new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.equivalentToolEnum(toolName));
        } else if (ChangeWindowAction.WindowEnums.isCorrectTool(toolName) ) {
            return new ChangeWindowAction(ChangeWindowAction.WindowEnums.equivalentWindowEnum(toolName),project,this.role,null);
        } else if (OpenWindowAction.WindowEnum.isCorrectTool(toolName)) {
            return new OpenWindowAction(OpenWindowAction.WindowEnum.equivalentWindowEnum(toolName));
        } else if (toolName.equals("update_project")) {
            return new OpenUpdateProjectPageAction();
        } else if (toolName.equals("project_method")) {
            return new OpenProjectMethodsAction(project,this.role);
        } else if (toolName.equals("project_location")) {
            return new OpenProjectLocationAction(project,this.role);
        } else if (toolName.equals("delete_project")) {
            return new DeleteProjectAction(presenter.getManager());
        } else {
            try {
                List<Role> roles = presenter.getRoleByTemplateName(toolName);
                if (roles.size() > 0) {
                    final Role role1 = roles.get(0);

                    return new OpenWorkflowForRoleAction(project) {
                        @Override
                        public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {

                            if (role1.getWorkflowTemplate() == null) {
                                WorkbenchSidebar.this.role = role1;
                                LOG.warn("No workflow template assigned to role: {}", role1);
                                return;
                            }
                            super.showWorkflowDashboard(super.project,role1,(IContentWindow)window);

                            NavManager.navigateApp(window, uriFragment, isLinkAccessed, role1.getWorkflowTemplate().getName());
                        }
                    };
                }
            } catch (IndexOutOfBoundsException e) {
                // IGNORE
            }
        }

        return null;
    }

}