package org.generationcp.ibpworkbench.ui.dashboard.preview;

import com.vaadin.ui.Panel;
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

    public NurseryListPreview(Project project) {
        presenter = new NurseryListPreviewPresenter(this,project);

        try {
            assemble();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected void initializeComponents() {

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
