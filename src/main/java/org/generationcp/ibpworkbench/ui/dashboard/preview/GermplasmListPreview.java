package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.vaadin.data.Item;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.Reindeer;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
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
public class GermplasmListPreview extends Panel {
    private GermplasmListPreviewPresenter presenter;
    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreview.class);
    private Tree treeView;
    
    @Autowired
    private ToolUtil toolUtil;
    
    private Project project;
    
    private final static int BATCH_SIZE = 50;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    private ManagerFactory managerFactory;
    
    private ThemeResource folderResource;
    private ThemeResource leafResource;
    
    private String MY_LIST = "My List";
    private String SHARED_LIST = "Shared List";
    
    
    public GermplasmListPreview(Project project) {
        this.project = project;
        presenter = new GermplasmListPreviewPresenter(this,project);

        try {
            assemble();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
        folderResource =  new ThemeResource("images/folder.png");
        leafResource =  new ThemeResource("images/leaf_16.png");
    }
    
    public void setProject(Project project){
        this.project = project;
        presenter = new GermplasmListPreviewPresenter(this, this.project);
        
        
        try {
            Tool tool = new Tool();
            tool.setToolName(ToolName.germplasm_list_browser.name());
            toolUtil.updateToolConfigurationForProject(tool,  this.project);  
            setManagerFactory(managerFactoryProvider.getManagerFactoryForProject(this.project));
            this.removeAllComponents();
            generateTree();                                   
            this.addComponent(treeView);
        } catch (MiddlewareQueryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    

    protected void initializeComponents() {        
        //treeView = new Tree("Test");
        this.setHeight("400px");
        //this.addComponent(treeView);
    }
    
    private void generateTree() {
        List<GermplasmList> germplasmListParentLocal = new ArrayList<GermplasmList>();
        List<GermplasmList> germplasmListParentCentral = new ArrayList<GermplasmList>();

        try {
            germplasmListParentLocal = this.getManagerFactory().getGermplasmListManager().getAllTopLevelListsBatched(BATCH_SIZE, Database.LOCAL);
            germplasmListParentCentral = this.getManagerFactory().getGermplasmListManager().getAllTopLevelListsBatched(BATCH_SIZE, Database.CENTRAL);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();
          
            germplasmListParentLocal = new ArrayList<GermplasmList>();
            germplasmListParentCentral = new ArrayList<GermplasmList>();
        }

        treeView = new Tree();

        
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
            boolean hasChildList =  hasChildList(parentList.getId());
            treeView.setChildrenAllowed(parentList.getId(), hasChildList);
        }
        
        for (GermplasmList parentList : germplasmListParentCentral) {
            treeView.addItem(parentList.getId());
            treeView.setItemCaption(parentList.getId(), parentList.getName());
            treeView.setParent(parentList.getId(), SHARED_LIST);
            treeView.setItemIcon(parentList.getId(),folderResource);
            boolean hasChildList =  hasChildList(parentList.getId());
            treeView.setChildrenAllowed(parentList.getId(), hasChildList);
        }

        treeView.addListener(new GermplasmListTreeExpandListener(this));
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);
        //return germplasmListTree;
    }
    
    public void addGermplasmListNode(int parentGermplasmListId) throws InternationalizableException{
        List<GermplasmList> germplasmListChildren = new ArrayList<GermplasmList>();

        try {
            germplasmListChildren = getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderIdBatched(parentGermplasmListId, BATCH_SIZE);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();/*
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID));
                    */
            germplasmListChildren = new ArrayList<GermplasmList>();
        }

        for (GermplasmList listChild : germplasmListChildren) {
            
            boolean hasChildList =  hasChildList(listChild.getId());
            
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
            
        }
        treeView.setImmediate(true);
    }
    
    private boolean hasChildList(int listId) {

        List<GermplasmList> listChildren = new ArrayList<GermplasmList>();

        try {
            listChildren = getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderId(listId, 0, 1);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            /*
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID));
                    */
            listChildren = new ArrayList<GermplasmList>();
        }
        
        return !listChildren.isEmpty();
    }
    
    

    protected void initializeLayout() {
        this.setStyleName(Reindeer.PANEL_LIGHT);
        this.setSizeFull();

    }

    protected void initializeActions() {

    }


    protected void assemble() throws Exception {
        initializeComponents();
        
        initializeLayout();
        initializeActions();
    }

    
    public ManagerFactory getManagerFactory() {
        return managerFactory;
    }

    
    public void setManagerFactory(ManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }
    
    

}
