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
    
    
    private Project project;
    
    
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    
    
    private ThemeResource folderResource;
    private ThemeResource leafResource;
    
    private String MY_LIST = "My List";
    private String SHARED_LIST = "Shared List";
    
    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    
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
        this.project = project;
        presenter = new GermplasmListPreviewPresenter(this, this.project);
        //generateTree();     
        presenter.generateInitialTreeNode();
        this.addComponent(treeView);
        
        
    }
    
    

    protected void initializeComponents() {        
        //treeView = new Tree("Test");
        this.setHeight("400px");
        //this.addComponent(treeView);
    }
    
    public void generateTree(List<GermplasmList> germplasmListParentLocal, List<GermplasmList> germplasmListParentCentral) {
        
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
            boolean hasChildList =  getPresenter().hasChildList(parentList.getId());
            treeView.setChildrenAllowed(parentList.getId(), hasChildList);
        }
        
        for (GermplasmList parentList : germplasmListParentCentral) {
            treeView.addItem(parentList.getId());
            treeView.setItemCaption(parentList.getId(), parentList.getName());
            treeView.setParent(parentList.getId(), SHARED_LIST);
            treeView.setItemIcon(parentList.getId(),folderResource);
            boolean hasChildList =  getPresenter().hasChildList(parentList.getId());
            treeView.setChildrenAllowed(parentList.getId(), hasChildList);
        }

        treeView.addListener(new GermplasmListTreeExpandListener(this));
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);
        //return germplasmListTree;
    }
    
    public void addGermplasmListNode(int parentGermplasmListId, List<GermplasmList> germplasmListChildren ) throws InternationalizableException{
       
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
            
        }
        treeView.setImmediate(true);
    }
    
    

    
    public GermplasmListPreviewPresenter getPresenter() {
        return presenter;
    }

    
    public void setPresenter(GermplasmListPreviewPresenter presenter) {
        this.presenter = presenter;
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

    
    public ManagerFactoryProvider getManagerFactoryProvider() {
        return managerFactoryProvider;
    }

    
    public void setManagerFactoryProvider(
            ManagerFactoryProvider managerFactoryProvider) {
        this.managerFactoryProvider = managerFactoryProvider;
    }

    
    

}
