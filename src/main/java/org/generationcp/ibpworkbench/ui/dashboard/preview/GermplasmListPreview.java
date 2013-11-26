package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.*;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.themes.Reindeer;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainTreeListener;
import org.generationcp.ibpworkbench.ui.dashboard.listener.GermplasmListTreeExpandListener;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class GermplasmListPreview extends VerticalLayout {
    private GermplasmListPreviewPresenter presenter;
    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreview.class);
    private Tree treeView;
    
    
    private Project project;
    
    
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    
    
    private ThemeResource folderResource;
    private ThemeResource leafResource;
    
    private String MY_LIST = "My List";
    private String SHARED_LIST = "Shared List";
    
    private Panel panel;
    private HorizontalLayout toolbar;


    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    private Button openListManagerBtn;
    private Button addFolderBtn;
    private Button deleteFolderBtn;
    private Button renameFolderBtn;
    
    private Object lastItemId;

    public GermplasmListPreview(Project project) {
        this.project = project;
        
        presenter = new GermplasmListPreviewPresenter(this,project);

        try {
            if (project != null){
                assemble();
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
        folderResource =  new ThemeResource("images/folder.png");
        leafResource =  new ThemeResource("images/leaf_16.png");
       

    }
    
    public void setProject(Project project){
        this.removeAllComponents();

        this.setSizeFull();

        // add toolbar here
        panel = new Panel();
        panel.removeAllComponents();

        this.addComponent(buildToolbar());

        this.project = project;
        presenter = new GermplasmListPreviewPresenter(this, this.project);
        presenter.generateInitialTreeNode();

        CssLayout treeContainer = new CssLayout();
        treeContainer.setSizeUndefined();
        treeContainer.addComponent(treeView);

        panel.setContent(treeContainer);
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.setSizeFull();

        this.addComponent(panel);
        this.setExpandRatio(panel,1.0F);


    }

    private Component buildToolbar() {
        this.toolbar = new HorizontalLayout();
        this.toolbar.setSpacing(true);
        this.toolbar.setMargin(true);

        openListManagerBtn = new Button("<span class='glyphicon glyphicon-open' style='right: 6px'></span>Launch");
        openListManagerBtn.setHtmlContentAllowed(true);
        openListManagerBtn.setDescription("Open In List Manager");

        renameFolderBtn = new Button("");
        renameFolderBtn.setDescription("Rename Folder");

        addFolderBtn = new Button("");
        addFolderBtn.setDescription("Add New Folder");

        deleteFolderBtn = new Button("");
        deleteFolderBtn.setDescription("Delete Selected Folder");

        openListManagerBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        renameFolderBtn.setStyleName(Bootstrap.Buttons.INFO.styleName()+" toolbar button-pencil");
        addFolderBtn.setStyleName(Bootstrap.Buttons.INFO.styleName()+" toolbar button-plus");
        deleteFolderBtn.setStyleName(Bootstrap.Buttons.DANGER.styleName()+" toolbar button-trash");

        openListManagerBtn.setWidth("100px");
        renameFolderBtn.setWidth("40px");
        addFolderBtn.setWidth("40px");
        deleteFolderBtn.setWidth("40px");

        this.toolbar.addComponent(openListManagerBtn);

        Label spacer = new Label("");
        this.toolbar.addComponent(spacer);
        this.toolbar.setExpandRatio(spacer,1.0F);

        this.toolbar.addComponent(renameFolderBtn);
        this.toolbar.addComponent(addFolderBtn);
        this.toolbar.addComponent(deleteFolderBtn);

        //this.toolbar.setSizeFull();
        this.toolbar.setWidth("100%");

        initializeToolbarActions();

        return this.toolbar;
    }

    private void initializeToolbarActions() {
        openListManagerBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (lastItemId == null) {
                    MessageNotifier.showError(event.getComponent().getWindow(),"Please select an item in the list","");
                    return;
                }

                if (presenter.isFolder(lastItemId)) {
                    MessageNotifier.showError(event.getComponent().getWindow(),"Selected Item is a folder","");
                    return;
                }

                // change to list manager, with parameter passed
                (new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.GERMPLASM_LIST_BROWSER)).buttonClick(event);
            }
        });

        renameFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (lastItemId == null || !presenter.isFolder(lastItemId)) {
                    MessageNotifier.showError(event.getComponent().getWindow(),"Please select a folder to be renamed (not folder)","");
                    return;
                }

                final Window w = new Window("Rename a folder");
                w.setWidth("280px");
                w.setHeight("150px");
                w.setModal(true);
                w.setResizable(false);
                w.setStyleName(Reindeer.WINDOW_LIGHT);

                VerticalLayout container = new VerticalLayout();
                container.setSpacing(true);
                container.setMargin(true);

                HorizontalLayout formContainer = new HorizontalLayout();
                formContainer.setSpacing(true);

                Label l = new Label("New Folder Name");
                TextField name = new TextField();
                name.setValue(treeView.getItemCaption(lastItemId));

                formContainer.addComponent(l);
                formContainer.addComponent(name);

                HorizontalLayout btnContainer = new HorizontalLayout();
                btnContainer.setSpacing(true);
                btnContainer.setWidth("100%");

                Label spacer = new Label("");
                btnContainer.addComponent(spacer);
                btnContainer.setExpandRatio(spacer,1.0F);

                Button ok = new Button("Ok");
                ok.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                ok.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        // TODO: DO SOMETHING HERE
                    }
                });

                Button cancel = new Button("Cancel");
                cancel.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        WorkbenchMainView.getInstance().removeWindow(w);
                    }
                });

                btnContainer.addComponent(ok);
                btnContainer.addComponent(cancel);

                container.addComponent(formContainer);
                container.addComponent(btnContainer);

                w.setContent(container);

                // show window
                WorkbenchMainView.getInstance().addWindow(w);

            }
        });

        addFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        deleteFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
    
    public void expandTree(Object itemId){
        
        if(treeView.isExpanded(itemId)){
            treeView.collapseItem(itemId);            
            treeView.select(itemId);
        }
        else{
            treeView.expandItem(itemId);
            treeView.select(itemId);
            
        }
        lastItemId = itemId;
    }

    protected void initializeComponents() {        
        //treeView = new Tree("");
        //this.setHeight("400px");
        //this.setHeight("100%");
        //this.addComponent(treeView);
    }
    
    public void generateTree(List<GermplasmList> germplasmListParentLocal, List<GermplasmList> germplasmListParentCentral) {
        lastItemId = null;
        treeView = new Tree();
        treeView.setContainerDataSource(new HierarchicalContainer());
        treeView.setDropHandler(new TreeDropHandler(treeView));
        treeView.setDragMode(TreeDragMode.NODE);
        
        treeView.addItem(MY_LIST);
        treeView.setItemCaption(MY_LIST, MY_LIST);
        treeView.setItemIcon(MY_LIST, folderResource);
        
        treeView.addItem(SHARED_LIST);
        treeView.setItemCaption(SHARED_LIST, SHARED_LIST);
        treeView.setItemIcon(SHARED_LIST,folderResource);
        
        for (GermplasmList parentList : germplasmListParentLocal) {
            treeView.addItem(parentList.getId());
            treeView.setItemCaption(parentList.getId(), parentList.getName());
            treeView.setParent(parentList.getId(), MY_LIST);
            treeView.setItemIcon(parentList.getId(),folderResource);
            boolean hasChildList =  getPresenter().hasChildList(parentList.getId());
            treeView.setChildrenAllowed(parentList.getId(), hasChildList);
            treeView.setSelectable(true);
        }        
        for (GermplasmList parentList : germplasmListParentCentral) {
            treeView.addItem(parentList.getId());
            treeView.setItemCaption(parentList.getId(), parentList.getName());
            treeView.setParent(parentList.getId(), SHARED_LIST);
            treeView.setItemIcon(parentList.getId(),folderResource);
            boolean hasChildList =  getPresenter().hasChildList(parentList.getId());
            treeView.setChildrenAllowed(parentList.getId(), hasChildList);
            treeView.setSelectable(true);
        }

        treeView.addListener(new GermplasmListTreeExpandListener(this));
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);
        //return germplasmListTree;
    }
    
    public void addGermplasmListNode(int parentGermplasmListId, List<GermplasmList> germplasmListChildren, Object itemId ) throws InternationalizableException{
       
        for (GermplasmList listChild : germplasmListChildren) {
            
            boolean hasChildList =  getPresenter().hasChildList(listChild.getId());
            
            treeView.addItem(listChild.getId());
            treeView.setItemCaption(listChild.getId(), listChild.getName());
            treeView.setParent(listChild.getId(), parentGermplasmListId);
            // allow children if list has sub-lists
            treeView.setChildrenAllowed(listChild.getId(), hasChildList);
            
            ThemeResource resource = folderResource;
            if(!hasChildList){
                resource = leafResource;
                
            }            
            treeView.setItemIcon(listChild.getId(),resource);
            treeView.setSelectable(true);
            
        }
        System.out.println("add node " + itemId);
        treeView.select(itemId);
        lastItemId = itemId;
        treeView.setImmediate(true);
    }
    
    

    
    public GermplasmListPreviewPresenter getPresenter() {
        return presenter;
    }

    
    public void setPresenter(GermplasmListPreviewPresenter presenter) {
        this.presenter = presenter;
    }

    protected void initializeLayout() {
        this.setMargin(true);
    }

    protected void initializeActions() {

    }


    protected void assemble() throws Exception {
        initializeComponents();
        
        initializeLayout();
        initializeActions();
       
    }

    
    public ManagerFactoryProvider getManagerFactoryProvider() {
        return managerFactoryProvider;
    }

    
    public void setManagerFactoryProvider(
            ManagerFactoryProvider managerFactoryProvider) {
        this.managerFactoryProvider = managerFactoryProvider;
    }

    private static class TreeDropHandler implements DropHandler {
        private final Tree tree;

        public TreeDropHandler (Tree tree) {
            this.tree = tree;
        }


        @Override
        public void drop(DragAndDropEvent dropEvent) {
            // Called whenever a drop occurs on the component

            // Make sure the drag source is the same tree
            Transferable t = dropEvent.getTransferable();

            // see the comment in getAcceptCriterion()
            if (t.getSourceComponent() != tree
                    || !(t instanceof DataBoundTransferable)) {
                return;
            }

            Tree.TreeTargetDetails dropData = ((Tree.TreeTargetDetails) dropEvent
                    .getTargetDetails());

            Object sourceItemId = ((DataBoundTransferable) t).getItemId();
            // FIXME: Why "over", should be "targetItemId" or just
            // "getItemId"
            Object targetItemId = dropData.getItemIdOver();

            // Location describes on which part of the node the drop took
            // place
            VerticalDropLocation location = dropData.getDropLocation();

            moveNode(sourceItemId, targetItemId, location);

        }

        @Override
        public AcceptCriterion getAcceptCriterion() {
            return AcceptAll.get();
        }

        /**
         * Move a node within a tree onto, above or below another node depending
         * on the drop location.
         *
         * @param sourceItemId
         *            id of the item to move
         * @param targetItemId
         *            id of the item onto which the source node should be moved
         * @param location
         *            VerticalDropLocation indicating where the source node was
         *            dropped relative to the target node
         */
        private void moveNode(Object sourceItemId, Object targetItemId,
                              VerticalDropLocation location) {
            HierarchicalContainer container = (HierarchicalContainer) tree
                    .getContainerDataSource();

            // Sorting goes as
            // - If dropped ON a node, we append it as a child
            // - If dropped on the TOP part of a node, we move/add it before
            // the node
            // - If dropped on the BOTTOM part of a node, we move/add it
            // after the node

            if (location == VerticalDropLocation.MIDDLE) {
                if (container.setParent(sourceItemId, targetItemId)
                        && container.hasChildren(targetItemId)) {
                    // move first in the container
                    container.moveAfterSibling(sourceItemId, null);
                }
            } else if (location == VerticalDropLocation.TOP) {
                Object parentId = container.getParent(targetItemId);
                if (container.setParent(sourceItemId, parentId)) {
                    // reorder only the two items, moving source above target
                    container.moveAfterSibling(sourceItemId, targetItemId);
                    container.moveAfterSibling(targetItemId, sourceItemId);
                }
            } else if (location == VerticalDropLocation.BOTTOM) {
                Object parentId = container.getParent(targetItemId);
                if (container.setParent(sourceItemId, parentId)) {
                    container.moveAfterSibling(sourceItemId, targetItemId);
                }
            }
        }

    }
    

}
