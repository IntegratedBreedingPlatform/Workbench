package org.generationcp.ibpworkbench.ui.dashboard.preview;

import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    public NurseryListPreview(Project project) {
        presenter = new NurseryListPreviewPresenter(this,project);

        try {
            assemble();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void setProject(Project project){
        presenter = new NurseryListPreviewPresenter(this,project);
        generateTree();
    }
    
    protected void initializeComponents() {
        generateTree();
        this.setHeight("400px");
        this.addComponent(treeView);
    }
    
    private void generateTree(){
        treeView = new Tree("NurseryListPreview");
        
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
