package org.generationcp.ibpworkbench.ui.sidebar;

import com.vaadin.ui.CssLayout;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public WorkbenchSidebar(Project project,Role role) {
        presenter = new WorkbenchSidebarPresenter(this,project,role);

        try {
            assemble();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected void initializeComponents() {

    }

    protected void initializeLayout() {

    }

    protected void initializeActions() {

    }


    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}