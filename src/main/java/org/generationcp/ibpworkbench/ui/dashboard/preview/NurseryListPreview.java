package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.themes.Reindeer;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainTreeListener;

import org.generationcp.middleware.pojos.workbench.Project;
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
public class NurseryListPreview extends AbsoluteLayout {

    private static final long serialVersionUID = 1L;
    
    private NurseryListPreviewPresenter presenter;
    
    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreview.class);
    
    private Tree treeView;

    private Project project;
    
    private Panel panel;

    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    
    public NurseryListPreview(Project project) {
        
        this.project = project;
        presenter = new NurseryListPreviewPresenter(this, project);

        try {
            if (project != null){
                assemble();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    
    public void setProject(Project project){
        panel = new Panel();
        panel.removeAllComponents();
        this.removeAllComponents();
        this.project = project;
        presenter = new NurseryListPreviewPresenter(this, project);
        presenter.generateTreeNodes();        
        panel.addComponent(treeView);
        panel.setSizeFull();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        this.addComponent(panel, "left: 0px; top: 0px;");
    }
    
    protected void initializeComponents() {
        //this.setHeight("400px");
    }
    
    
    public void generateTree(List<TreeNode> treeNodes){
        
        treeView = new Tree();
        treeView.setDragMode(TreeDragMode.NODE);
        
        ThemeResource folderResource =  new ThemeResource("images/folder.png");
        ThemeResource leafResource =  new ThemeResource("images/leaf_16.png");
        
        doCreateTree(treeNodes, treeView, null, folderResource, leafResource);
        
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);
        
    }
    
    private void doCreateTree(List<TreeNode> treeNodes, Tree treeView, Object parent, ThemeResource folder, ThemeResource leaf){
        for(TreeNode treeNode : treeNodes){
        	
            treeView.addItem(treeNode.getId());
            treeView.setItemCaption(treeNode.getId(), treeNode.getName());

            // Set resource icon
            ThemeResource resource = folder;
            if(treeNode.isLeaf()){
                resource = leaf;
                treeView.setChildrenAllowed(treeNode.getId(), false);
                //we add listener if its the leaf
                Item item = treeView.getItem(treeNode.getId());
                
                if (treeNode.getName().equals(messageSource.getMessage(Message.MY_STUDIES)) 
                		|| treeNode.getName().equals(messageSource.getMessage(Message.SHARED_STUDIES))){
                	resource = folder;
                }
            }
            treeView.setItemIcon(treeNode.getId(), resource);

            // Disable arrow of folders with no children
            if (treeNode.getTreeNodeList().size() == 0){
                treeView.setChildrenAllowed(treeNode.getId(), false);
            }
            
            // Set parent
            if(parent != null){
                treeView.setParent(treeNode.getId(), parent);
            }
            
            // Create children nodes
            doCreateTree(treeNode.getTreeNodeList(), treeView, treeNode.getId(), folder, leaf);
        }
    }

    public void expandTree(Object itemId){
        
        if(treeView.isExpanded(itemId))
            treeView.collapseItem(itemId);
        else
            treeView.expandItem(itemId);
    }
    
    protected void initializeLayout() {        
        this.setSizeFull();
    }

    protected void initializeActions() {

    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    

    public NurseryListPreviewPresenter getPresenter() {
        return presenter;
    }

    
    public void setPresenter(NurseryListPreviewPresenter presenter) {
        this.presenter = presenter;
    }


    public ManagerFactoryProvider getManagerFactoryProvider() {
        return managerFactoryProvider;
    }

    
    public void setManagerFactoryProvider(
            ManagerFactoryProvider managerFactoryProvider) {
        this.managerFactoryProvider = managerFactoryProvider;
    }

    
}
