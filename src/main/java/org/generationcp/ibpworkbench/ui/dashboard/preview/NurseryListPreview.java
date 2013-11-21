package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.Reindeer;

import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainTreeListener;

import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class NurseryListPreview extends Panel {
    private NurseryListPreviewPresenter presenter;
    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreview.class);
    private Tree treeView;
    
    @Autowired
    private GermplasmListManager germplasmListManager;
    @Autowired
    private ToolUtil toolUtil;
    private Project project;
    
    public NurseryListPreview(Project project) {
        presenter = new NurseryListPreviewPresenter(this,project);

        try {
            assemble();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    
    public void setProject(Project project){
        this.project = project;
        presenter = new NurseryListPreviewPresenter(this,project);
        generateTree();
        /*
        try {
            //toolUtil.set
            List<GermplasmList> germplasmList = germplasmListManager.getAllGermplasmLists(0, (int) germplasmListManager.countAllGermplasmLists(), Database.LOCAL);
        } catch (MiddlewareQueryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
    }
    
    protected void initializeComponents() {
        generateTree();
        this.setHeight("400px");
        this.addComponent(treeView);
    }
    
    public class TreeNode{
        long id;
        String name;
        List<TreeNode> treeNodeList;
        boolean isLeaf;
        public TreeNode(long id, String name, List<TreeNode> treeNodes, boolean isLeaf){
            this.id = id;
            this.name = name;
            this.treeNodeList = treeNodes;
            this.isLeaf = isLeaf;
        }
        
        
        
        public boolean isLeaf() {
            return isLeaf;
        }


        
        public void setLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }


        public long getId() {
            return id;
        }
        
        public void setId(long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public List<TreeNode> getTreeNodeList() {
            return treeNodeList;
        }
        
        public void setTreeNodeList(List<TreeNode> treeNodeList) {
            this.treeNodeList = treeNodeList;
        }
                
    }
    
    public List<TreeNode> generateDummyData(){
        List<TreeNode> treeNodes1 = new ArrayList();
        
        List<TreeNode> treeNodes2 = new ArrayList();
        List<TreeNode> treeNodes3 = new ArrayList();
        List<TreeNode> treeNodes4 = new ArrayList();
        
        treeNodes2.add(new TreeNode(42, "42",  new ArrayList(), true));
        treeNodes2.add(new TreeNode(52, "52",  new ArrayList(), true));
        treeNodes2.add(new TreeNode(62, "62",  new ArrayList(), true));
        
        treeNodes3.add(new TreeNode(43, "43",  new ArrayList(), true));
        treeNodes3.add(new TreeNode(53, "53",  new ArrayList(), true));
        treeNodes3.add(new TreeNode(63, "63",  new ArrayList(), true));
        
        treeNodes4.add(new TreeNode(44, "44",  new ArrayList(), true));
        treeNodes4.add(new TreeNode(54, "54",  new ArrayList(), true));
        treeNodes4.add(new TreeNode(64, "64",  new ArrayList(), true));
        
        
        treeNodes1.add(new TreeNode(2, "2", treeNodes2, false));
        treeNodes1.add(new TreeNode(3, "3", treeNodes3, false));
        treeNodes1.add(new TreeNode(4, "4", treeNodes4, false));
        
        
        List<TreeNode> treeNodes = new ArrayList();
        treeNodes.add(new TreeNode(1, "Root", treeNodes1, false));
        return treeNodes;
    }
    
    private void generateTree(){
        treeView = new Tree("NurseryListPreview");
        
        List<TreeNode> treeNodes = generateDummyData();
        
        ThemeResource folderResource =  new ThemeResource("images/folder.png");
        ThemeResource leafResource =  new ThemeResource("images/leaf_16.png");
        doCreateTree(treeNodes, treeView, null, folderResource, leafResource);
        treeView.addListener(new DashboardMainTreeListener(this, project));
        /*
        // Create the tree nodes
        treeView.addItem("Root");
        treeView.addItem("Branch 1");
        treeView.addItem("Branch 2");
        treeView.addItem("Leaf 1");
        treeView.addItem("Leaf 2");
        treeView.addItem("Leaf 3");
        treeView.addItem("Leaf 4");
        
        // Set the hierarchy
        treeView.setParent("Branch 1", "Root");
        treeView.setParent("Branch 2", "Root");
        treeView.setParent("Leaf 1", "Branch 1");
        treeView.setParent("Leaf 2", "Branch 1");
        treeView.setParent("Leaf 3", "Branch 2");
        treeView.setParent("Leaf 4", "Branch 2");
        */
    }
    
    private void doCreateTree(List<TreeNode> treeNodes, Tree treeView, Object parent, ThemeResource folder, ThemeResource leaf){
        for(TreeNode treeNode : treeNodes){
            treeView.addItem(treeNode.getId());
            treeView.setItemCaption(treeNode.getId(), treeNode.getName());
            
            ThemeResource resource = folder;
            if(treeNode.isLeaf()){
                resource = leaf;
                treeView.setChildrenAllowed(treeNode.getId(), false);
                //we add listener if its the leaf
                Item item = treeView.getItem(treeNode.getId());
                
            }
            
            treeView.setItemIcon(treeNode.getId(),resource);
            if(parent != null)
                treeView.setParent(treeNode.getId(), parent);
            
            doCreateTree(treeNode.getTreeNodeList(), treeView, treeNode.getId(), folder, leaf);
        }
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
}
