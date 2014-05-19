package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 10/28/13
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class OpenUpdateProjectPageAction  implements Button.ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(OpenUpdateProjectPageAction.class);

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;


    @Override
    public void buttonClick(Button.ClickEvent event) {
        doAction(event.getComponent().getWindow(), null, true);
    }

    @Override
    public void doAction(Component.Event event) {
        //NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        final IContentWindow w = (IContentWindow) window;

        try {
            UpdateProjectPanel projectPanel = new UpdateProjectPanel();
            w.showContent(projectPanel);

            //NavManager.navigateApp(window, "/UpdateProgram", isLinkAccessed);
            ProjectActivity projAct = new ProjectActivity(new Integer(sessionData.getSelectedProject().getProjectId().intValue()), sessionData.getSelectedProject(),"Update Program", "Launched Update Program", sessionData.getUserData(), new Date());
            workbenchDataManager.addProjectActivity(projAct);

        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(window, i.getCaption(), i.getDescription());
            }
            return;
        }

    }

}