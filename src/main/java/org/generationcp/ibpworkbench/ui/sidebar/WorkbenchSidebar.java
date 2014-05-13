package org.generationcp.ibpworkbench.ui.sidebar;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.*;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.ui.programadministration.OpenManageProgramPageAction;
import org.generationcp.ibpworkbench.ui.project.create.OpenUpdateProjectPageAction;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.pojos.workbench.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class WorkbenchSidebar extends CssLayout {
    @Autowired
    private SessionData sessionData;

    private WorkbenchSidebarPresenter presenter;
    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebar.class);
    private Tree sidebarTree;

    public static Map<String,TreeItem> sidebarTreeMap = new HashMap<String, TreeItem>();

    private ItemClickEvent.ItemClickListener treeClickListener = new ItemClickEvent.ItemClickListener() {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (event.getItemId() == null)
                return;
            else {
                LOG.trace(event.getItemId().toString());

                TreeItem treeItem = (TreeItem) event.getItemId();
                if (treeItem.getValue() == null) {
                    return;
                }

                presenter.updateProjectLastOpenedDate();

                ActionListener listener = WorkbenchSidebar.this.getLinkActions(treeItem.getId(),sessionData.getSelectedProject());
                if (listener instanceof LaunchWorkbenchToolAction) {

                    ((LaunchWorkbenchToolAction)listener).launchTool(treeItem.getId(),event.getComponent().getWindow(),true);
                }
                else if (listener instanceof OpenWindowAction) {
                    ((OpenWindowAction)listener).launchWindow(event.getComponent().getWindow(),treeItem.getId());
                }

                else {
                    listener.doAction(event.getComponent().getWindow(),"/" + treeItem.getId(),true);
                }
            }
        }
    };


    public WorkbenchSidebar() {
        presenter = new WorkbenchSidebarPresenter(this);

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
        this.removeAllComponents();

        sidebarTree = new Tree();
        sidebarTree.setImmediate(true);

        Map<WorkbenchSidebarCategory,List<WorkbenchSidebarCategoryLink>> links = presenter.getCategoryLinkItems();
        sidebarTree.setContainerDataSource(new HierarchicalContainer());
        sidebarTree.addContainerProperty("id",String.class,"");
        sidebarTree.addContainerProperty("caption",String.class,"");
        sidebarTree.addContainerProperty("value",Object.class,null);

        boolean expandedFirst = false;
        for (WorkbenchSidebarCategory category : links.keySet()) {
            TreeItem parentItem = new TreeItem(category.getSidebarCategoryName(),category.getSidebarCategorylabel(),null);

            sidebarTreeMap.put(category.getSidebarCategoryName(),parentItem);

            Item parent = sidebarTree.addItem(parentItem);

            sidebarTree.setChildrenAllowed(parent, true);
            sidebarTree.setItemCaption(parentItem,parentItem.getCaption());
            for (WorkbenchSidebarCategoryLink link : links.get(category)) {
                TreeItem item = new TreeItem(link.getTool().getToolName(),link.getSidebarLinkTitle(),link);

                sidebarTreeMap.put(link.getSidebarLinkName(),item);

                sidebarTree.addItem(item);
                sidebarTree.setParent(item, parentItem);
                sidebarTree.setChildrenAllowed(item,false);
                sidebarTree.setItemCaption(item,item.getCaption());
            }

            if (!expandedFirst) {
                sidebarTree.expandItem(parentItem);
                expandedFirst = true;
            }

        }

        sidebarTree.setSelectable(true);
        sidebarTree.addListener(treeClickListener);
        sidebarTree.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LOG.trace("valueChange");

                // expand category if not yet expanded

                Object parentItem = sidebarTree.getParent(event.getProperty().getValue());

                if (parentItem != null && !sidebarTree.isExpanded(parentItem)) {
                    sidebarTree.expandItem(sidebarTree.getParent(event.getProperty().getValue()));

                }

                // item is category, expand or collapse this
                if (event.getProperty() != null || event.getProperty().getValue() != null) {
                    if (sidebarTree.isExpanded(event.getProperty().getValue()))
                        sidebarTree.collapseItem(event.getProperty().getValue());
                    else
                        sidebarTree.expandItem(event.getProperty().getValue());
                }


            }
        });
        this.addComponent(sidebarTree);
    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    public void clearLinks() {
        if (sidebarTree != null)
            sidebarTree.setContainerDataSource(new HierarchicalContainer());
    }

    public void selectItem(TreeItem item) {
        sidebarTree.setValue(item);
    }

    public class TreeItem {
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
            return new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.equivalentToolEnum(toolName),project,null);
        } else if (ChangeWindowAction.WindowEnums.isCorrectTool(toolName) ) {
            return new ChangeWindowAction(ChangeWindowAction.WindowEnums.equivalentWindowEnum(toolName),project,null);
        } else if (OpenWindowAction.WindowEnum.isCorrectTool(toolName)) {
            return new OpenWindowAction(OpenWindowAction.WindowEnum.equivalentWindowEnum(toolName),project);
        } else if (toolName.equals("manage_program")) {
            return new OpenManageProgramPageAction();
        } else if (toolName.equals("update_project")) {
            return new OpenUpdateProjectPageAction();
        } else if (toolName.equals("project_method")) {
            return new OpenProgramMethodsAction(project);
        } else if (toolName.equals("project_location")) {
            return new OpenProgramLocationsAction(project,sessionData.getUserData());
        } else if (toolName.equals("delete_project")) {
            return new DeleteProjectAction();
        } else {
            try {
                List<Role> roles = presenter.getRoleByTemplateName(toolName);
                if (roles.size() > 0) {
                    final Role role1 = roles.get(0);

                    return new OpenWorkflowForRoleAction(project) {
                        @Override
                        public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {

                            if (role1.getWorkflowTemplate() == null) {
                                LOG.warn("No workflow template assigned to role: {}", role1);
                                return;
                            }
                            super.showWorkflowDashboard(super.project,role1,(IContentWindow)window);

                            //NavManager.navigateApp(window,String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d", super.project.getProjectId(), role1.getRoleId()), isLinkAccessed, role1.getLabel());
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
