package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.vaadin.data.Item;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.themes.Reindeer;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
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
        // add toolbar here
        panel = new Panel();
        panel.removeAllComponents();
        this.removeAllComponents();

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
        panel.setHeight("282px");

        this.addComponent(panel);
        this.setExpandRatio(panel,1.0F);
    }

    private Component buildToolbar() {
        this.toolbar = new HorizontalLayout();
        this.toolbar.setSpacing(true);
        this.toolbar.setMargin(true);

        openListManagerBtn = new Button("Open");
        openListManagerBtn.setDescription("Open in List Manager");

        renameFolderBtn = new Button("Rename");
        renameFolderBtn.setDescription("Rename Folder");

        addFolderBtn = new Button("+");
        addFolderBtn.setDescription("Add New Folder");

        deleteFolderBtn = new Button("-");
        deleteFolderBtn.setDescription("Delete Selected Folder");

        openListManagerBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());
        renameFolderBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());
        addFolderBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());
        deleteFolderBtn.setStyleName(Bootstrap.Buttons.DANGER.styleName());

        this.toolbar.addComponent(openListManagerBtn);
        this.toolbar.addComponent(renameFolderBtn);
        this.toolbar.addComponent(addFolderBtn);
        this.toolbar.addComponent(deleteFolderBtn);

        return this.toolbar;
    }

    private void initializeToolbarActions() {
        openListManagerBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                //To change body of implemented methods use File | Settings | File Templates.
                
               //System.out.println(treeView.getValue());
            }
        });

        renameFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                //To change body of implemented methods use File | Settings | File Templates.
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
    }

    protected void initializeComponents() {        
        //treeView = new Tree("");
        //this.setHeight("400px");
        //this.setHeight("100%");
        //this.addComponent(treeView);
    }
    
    public void generateTree(List<GermplasmList> germplasmListParentLocal, List<GermplasmList> germplasmListParentCentral) {
        
        treeView = new Tree();
        treeView.setDragMode(TreeDragMode.NODE);
        
        treeView.addItem(MY_LIST);
        treeView.setItemCaption(MY_LIST, MY_LIST);
        treeView.setItemIcon(MY_LIST,folderResource);
        
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
        treeView.select(itemId);
        treeView.setImmediate(true);
    }
    
    

    
    public GermplasmListPreviewPresenter getPresenter() {
        return presenter;
    }

    
    public void setPresenter(GermplasmListPreviewPresenter presenter) {
        this.presenter = presenter;
    }

    protected void initializeLayout() {
        //this.setStyleName(Reindeer.PANEL_LIGHT);
        this.setSizeFull();
        this.setSpacing(false);
        this.setMargin(false);

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

    
    

}
